/**
 * @(#)ACoilRcvL2SeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      박판열연 COIL 야드 L3 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.yf.acoil.session;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.yf.common.YFUserException;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.YfConstant;
import com.inisteel.cim.yf.common.YfQueryIF;
import com.inisteel.cim.yf.common.YfQueryIF2;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.common.session.YfComm;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.common.YmCommonUtil;


import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.DateHelper;
import jspeed.base.util.StringHelper;

/**
 *      [A] 클래스명 : 박판열연 COIL 야드 L3수신 처리
 *
 * @ejb.bean name="ACoilRcvL2SeEJB" jndi-name="ACoilRcvL2SeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class ACoilRcvL2SeEJBSBean extends BaseSessionBean implements YfQueryIF, YfQueryIF2 {
	
	private static final long serialVersionUID = 1L;
	private Logger logger = new Logger("yf");
	private YfCommUtils commUtils = new YfCommUtils();
	private YfCommDAO commDao = new YfCommDAO();
	private YfComm yfComm = new YfComm();
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 스케줄작업요구(F1YFL014)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL014(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "스케줄작업요구[ACoilRcvL2SeEJB.rcvF1YFL014] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //설비ID
//			String ydSchCd     	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));     //SCHEDULE 코드
//			String ydCrnSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); //SCHEDULEID
			String ydSchFlag   	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_FLAG"));   //요구스케쥴구분
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)

			String ydNewWbookId 	= "";
			String ydNewSchCd 		= "";
			String ydNewSchPrior	= "";
			String ydNewCrnSchId 	= "";
			
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) 
			{
				throw new Exception("설비ID 이상 [" + ydEqpId + "]");
			} 
			else if (ydSchFlag.length() == 0) 
			{
				throw new Exception("요구스케쥴구분 이상 [" + ydEqpId + "]");
			}
			
			
			
			/**********************************************************
			* 2. 작업대상 조회
			**********************************************************/
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_EQP_ID"     , ydEqpId);  					
			jrParam.setField("YD_SCH_FLAG"   , ydSchFlag);
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getCrnSchRequest, logId, methodNm, "요구정보 조회");
			
			//요구에 해당하는 스케줄이 없으면 만들어야하는것 아닌지...kbs
			if (jsCrnSch.size() == 0) {
				//throw new Exception("요구에 해당하는 정보 없음[" + ydEqpId + "]");
				commUtils.printLog(logId,  "요구에 해당하는 정보 없음[" + ydEqpId + "]", "SL");
				return jrRtn;
			} 
			else 
			{
				// 요구스케쥴구분
				ydNewCrnSchId 	= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");  // 신규 작업
				ydNewWbookId 	= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");    // 신규 작업
				ydNewSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");      // 신규 작업
				ydNewSchPrior	= "1";   // 신규 작업
			}


			jrParam.setField("YD_SCH_CD"     , ydNewSchCd);	
			jrParam.setField("YD_WBOOK_ID"   , ydNewWbookId);	
			jrParam.setField("YD_SCH_PRIOR"  , ydNewSchPrior);	
			jrParam.setField("YD_CRN_SCH_ID" , ydNewCrnSchId);
			jrParam.setField("MODIFIER"      , modifier);	
			
			//기존 크레인 작업 조회
			// 기존작업지시
			JDTORecordSet jsCrn = commDao.select(jrParam, getCrnWrkMgtPriorWrk1, logId, methodNm, "기존크레인 작업 조회");
			if (jsCrn == null || jsCrn.size() <= 0) 
			{
				/**********************************************************
				* 3.1 기존 작업이 없음  신규 작업만 하면 됨  
				**********************************************************/
			
			} 
			else 
			{
				
				// 기존 작업 
			    JDTORecord jrCrn = jsCrn.getRecord(0);
			    String ydCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"   ));
			    
				/**********************************************************
				* 3.1 기존 작업 정리 
				* 3.2 신규 작업 처리 함  
				**********************************************************/
				
			    /**** 기존 작업 지시 정리 ***********/
				//크레인스케줄 Table 크레인ID, 우선순위 Update,
			    jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchIdWrk);
				commDao.update(jrParam, updCrnWrkMgtPriorWrkNext1, logId, methodNm,  "TB_YF_CRNSCH");						    
				
			}
			
			//신규 작업예약 Table 우선순위 Update
			commDao.update(jrParam, updWrkBookPrior1, logId, methodNm, "TB_YM_WRKBOOK");				

			//신규 작업 우선순위 변경
			commDao.update(jrParam, updCrnWrkMgt1, logId, methodNm,  "TB_YF_CRNSCH");
			
			/**********************************************************
			* 3.2 신  크레인작업지시 요구 처리
			**********************************************************/

			//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			jrYdMsg.setField("JMS_TC_CD"       , "YFF1L004");	//크레인작업지시요구
			jrYdMsg.setField("MSG_GP"          , "I");	//야드설비ID
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydNewCrnSchId);	//야드크레인스케쥴ID

			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L004", jrYdMsg));			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업지시요구(F1YFL007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvF1YFL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업지시요구[ACoilRcvL2SeEJBSBean.rcvF1YFL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try 
		{
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String L2WrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //L2야드작업진행상태
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydWookId      = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"   )); //형상에서 명령선택 호출시, 작업해야할 작업지시를 명확히 하기 위해(동일 통로에 1번지, 2번지 작업에 혼선이 있음)
			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
			
			String sYD_SCH_CD    = "";
			String stlNo		 = "";
			String ydUpWoLoc	 = "";
			String ydWbookId	 = "";
			 
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			commUtils.printLog(logId, "크레인작업지시요구 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");

			JDTORecord jrRtn  = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); //야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD"  , "9999"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			
			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("YD_WBOOK_ID", ydWookId); //야드크레인스케쥴ID
			
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			
			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = yfComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) 
			{
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				
				jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));
				return jrRtn;
			}

			
			/**********************************************************
			* 2. 크레인스케줄 조회
			*    - 크레인스케줄이 존재하면 전송
			*    - 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			*    - 전문으로 스케줄id를 받으면, 지시 재전송인데, 그러한 부분이 안보임. 크레인id 만으로 스케줄 조회하고 있음..kbs
			*    - 1열연 쿼리에 차량연속작업(전문으로 받은 스케줄id와 YD_CRN_GRAB_USE_RULE_ID가 동일한 스케줄)이면, 
			*      우선순위를 높이는 부분이 있는데, 
			*      전문으로 스케줄id를 받으면, 해당 지시를 재전송하면 그만인데,, 다른 스케줄이 무슨 의미가 있는지...kbs  
			**********************************************************/
			JDTORecordSet jsSch = JDTORecordFactory.getInstance().createRecordSet("Temp");
			jsSch = commDao.select(jrParam, getCrnSchAxYML007_2, logId, methodNm, "크레인스케줄 조회");
			
			
			
			/**********************************************************
			* 3. 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
			**********************************************************/
			if (jsSch.size() > 0) 
			{
				
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
				sYD_SCH_CD    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_SCH_CD"));
				stlNo		  = commUtils.trim(jsSch.getRecord(0).getFieldString("STL_NO"));
				ydUpWoLoc	  = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_UP_WO_LOC"));
				ydWbookId	  = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WBOOK_ID"));
				
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				
				
				/**********************************************************
				* 3.1 스케쥴수행[S], 권상지시[1] 이면 지시일시 수정
				* 	1	권상지시
					2	권상완료
					3	권하지시
					4	권하완료
					5	강제권하
					C	스케쥴명령취소
					S	스케쥴수행
					W	명령선택대기
				**********************************************************/
				if ("S".equals(ydWrkProgStat)||"1".equals(ydWrkProgStat)||"2".equals(ydWrkProgStat)||"3".equals(ydWrkProgStat)||"5".equals(ydWrkProgStat))
				{
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
					commDao.update(jrParam, updYdWorkDt, logId, methodNm, "스케쥴 수정");
				}

				/**********************************************************
				* 3.2 대기[W] 이면 작업지시 전송
				**********************************************************/
				else if("W".equals(ydWrkProgStat))
				{
					
					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규

					//설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_PROG_STAT", "1"); //권상작업지시
	        		commDao.update(jrParam, updStatEqp2, logId, methodNm, "설비상태 수정");
	        		
	        		
	        		//크레인에 설정된 다른 스케줄들을 초기화 시킴
	        		//우선순위가 리셋되고, 불필요함. 20200623  
					//jrParam.setField("YD_WRK_PROG_STAT"	 , "W"); //대기
	        		//commDao.update(jrParam, updCrnSchW, logId, methodNm, "크레인스케줄 야드작업진행상태 초기화");
	        		

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
					jrParam.setField("YD_L2_REQUEST_STAT", "1"); 
					   
	        		commDao.update(jrParam, updStatCrnSchWrkProg, logId, methodNm, "크레인스케줄 야드작업진행상태 수정");
	        		
	        		
				}
				else
				{
					commUtils.printLog(logId, "크레인이 작업중 : 크레인작업진행상태 [" + ydWrkProgStat + "], L2작업상태 [" + L2WrkProgStat+"]", "SL");
				}
				
				
				
				
				/**********************************************************
				* 3.3 크레인작업지시(YFF1L004) 전문 생성 - w 일때만 보내자. 202001210
				**********************************************************/
				//if("W".equals(L2WrkProgStat) || "W".equals(ydWrkProgStat))
				//{
					jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L004", jrParam));
				//}
				

				commUtils.printLog(logId, "크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");

				
			} 
			
			
			/**********************************************************
			* 4. 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			*    - 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 응답처리
			*    - 권하완료[4] 이면 스케줄을 생성
			*    - 명령선택대기[W] 이면 응답 전문을 전송
			**********************************************************/
			else 
			{
				/**********************************************************
				* 4.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 응답처리
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) 
				{
					
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					
					jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} 
				
				/**********************************************************
				* 4.2 대기상태[W], 권하완료[4] 지시요구
				**********************************************************/
				else 
				{
					
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_PROG_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, updStatEqp2, logId, methodNm, "설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, getWbIdAxYML007, logId, methodNm, "작업예약 조회");

					//작업예약이 있으면 크레인스케줄호출
					if (jsWrkBook.size() > 0) 
					{
						
						// 대차가 아닌경우 
						ydL3Msg = "크레인스케줄 편성 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자
							
						// 크레인스케줄편성 기동
						jrRtn = yfComm.getCrnSchMsg(jrYdMsg);
					} 
					else 
					{
						ydL3Msg = "다음 크레인작업지시 없음";
					}

					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					//0615 SJH 추가
					resMsg.setField("YD_CRN_SCH_ID" , ""     ); 
					
					jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				//chito : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YfCommSeEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { yfComm.getYFF1L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	
    
    
	/**
	 *      [A] 오퍼레이션명 : 저장위치제원요구(F1YFL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvF1YFL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장위치제원요구[ACoilRcvL2SeEJB.rcvF1YFL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord jrRtn = null;
			
			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드(1:동,2:SPAN,3:열,4:BED,D:Scrap배드 삭제처리)
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("BAY_GP"      	)); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("SECT_GP"      	)); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("COL_GP"  		)); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"  )); //야드적치Bed번호
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			String strErr = "전문 정합성오류:: 야드정보동기화코드:" + ydInfoSyncCd+", 동:"+ydBayGp+", 설비:"+ydEqpGp+", 열:"+ydStkColNo+", 배드:"+ydStkBedNo;
			if ("".equals(ydInfoSyncCd)) {
				throw new Exception(strErr);
			} 
			else if ("1".equals(ydInfoSyncCd)) {
				if ("".equals(ydBayGp)) {
					throw new Exception(strErr);
				}
			}
			else if ("2".equals(ydInfoSyncCd)) {
				if ("".equals(ydBayGp) || "".equals(ydEqpGp)) {
					throw new Exception(strErr);
				}
			}
			else if ("3".equals(ydInfoSyncCd)) {
				if ("".equals(ydBayGp) || "".equals(ydEqpGp) || "".equals(ydStkColNo)) {
					throw new Exception(strErr);
				}
			}
			else if ("4".equals(ydInfoSyncCd) || "D".equals(ydInfoSyncCd)) {
				if ("".equals(ydBayGp) || "".equals(ydEqpGp) || "".equals(ydStkColNo) || "".equals(ydStkBedNo)) {
					throw new Exception(strErr);
				}
			}
			
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER", modifier);
			
			/**********************************************************
			 * 1. ydInfoSyncCd가 D로 오면 스크랩 삭제
			 **********************************************************/
			if ("D".equals(ydInfoSyncCd)) 
			{
				jrParam.setField("AREA_GP" , ydStkBedNo.substring(0, 1)); //스크랩 구역
				jrParam.setField("COL_NO"  , ydStkColNo      ); //열
				
				commDao.update(jrParam, updClearScrapStockByCol, logId, methodNm, "스크랩 TB_YF_STOCK 수정");
				
				commDao.update(jrParam, updClearScrapLyrByCol, logId, methodNm, "스크랩 TB_YF_STKLYR 수정");
				
				return jrRtn;
			}
			
			/**********************************************************
			* 2. 저장위치제원(YFF1L001) 전문 생성
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name

			sndL2Msg.setField("YD_INFO_SYNC_CD"	, ydInfoSyncCd                         ); //야드정보동기화코드
			sndL2Msg.setField("YD_STK_COL_GP"  	, ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			sndL2Msg.setField("YD_STK_BED_NO"  	, ydStkBedNo                           ); //야드적치Bed번호

			//전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L001", sndL2Msg));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(F1YFL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvF1YFL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장품제원요구[ACoilRcvL2SeEJB.rcvF1YFL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			//야드정보동기화코드 (1:동,2:SPAN,3:열,4:BED,5:지정저장품,A:생산실적,B:차량입고,C:행선변경,D:생산종료(삭제))
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("BAY_GP"      	)); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("SECT_GP"      	)); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("COL_GP"  		)); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_NO"  	)); //야드적치Bed번호
			String stlNo        = commUtils.trim(rcvMsg.getFieldString("STL_NO"		)); //재료번호
			//methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			/**********************************************************
			* 0. 수신 항목 값 Check
			**********************************************************/
			String strErr = "전문 정합성오류:: 야드정보동기화코드:" + ydInfoSyncCd+", 동:"+ydBayGp+", 설비:"+ydEqpGp+", 열:"+ydStkColNo+", 배드:"+ydStkBedNo;
			if ("".equals(ydInfoSyncCd)) {
				throw new Exception(strErr);
			} 
			else if ("1".equals(ydInfoSyncCd)) {
				if ("".equals(ydBayGp)) {
					throw new Exception(strErr);
				}
			}
			else if ("2".equals(ydInfoSyncCd)) {
				if ("".equals(ydBayGp) || "".equals(ydEqpGp)) {
					throw new Exception(strErr);
				}
			}
			else if ("3".equals(ydInfoSyncCd)) {
				if ("".equals(ydBayGp) || "".equals(ydEqpGp) || "".equals(ydStkColNo)) {
					throw new Exception(strErr);
				}
			}
			else if ("4".equals(ydInfoSyncCd)) {
				if ("".equals(ydBayGp) || "".equals(ydEqpGp) || "".equals(ydStkColNo) || "".equals(ydStkBedNo)) {
					throw new Exception(strErr);
				}
			}
			else if ("5".equals(ydInfoSyncCd) || "A".equals(ydInfoSyncCd) || "B".equals(ydInfoSyncCd) || "C".equals(ydInfoSyncCd)
					||"D".equals(ydInfoSyncCd)) {
				if ("".equals(stlNo)) {
					throw new Exception("전문 정합성오류:: 야드정보동기화코드:" + ydInfoSyncCd + " ,재료번호:"+ stlNo);
				}
			}
			
			/**********************************************************
			* 2. 저장품제원(YFF1L002) 전문 생성
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name

			sndL2Msg.setField("YD_INFO_SYNC_CD"	, ydInfoSyncCd                         ); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"                         		   ); //전문구분
			sndL2Msg.setField("YD_STK_COL_GP"  	, ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			sndL2Msg.setField("YD_STK_BED_NO"  	, ydStkBedNo                           ); //야드적치Bed번호
			sndL2Msg.setField("YD_GP"          	, ydGp                                 ); //야드구분
			sndL2Msg.setField("STL_NO"       	, stlNo                                ); //재료번호

			JDTORecord jrRtn = null;
			//전송Data 생성
			if (stlNo.startsWith("S") || "SC".equals(ydEqpGp)) {
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L002_SCRAP", sndL2Msg));
			} else {
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L002", sndL2Msg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
    
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비운전모드전환(F1YFL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm 	= "설비운전모드전환[ACoilRcvL2SeEJB.F1YFL003] < " + rcvMsg.getResultMsg();
		String logId 		= rcvMsg.getResultCode();
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= false;	//크레인작업실적응답 전문 전송여부
		//boolean resYn 		= true;	//크레인작업실적응답 전문 전송여부 - test용

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "설비운전모드전환(F1YFL003) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String ydEqpWrkMode    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE"));  // 1: On-Line, "2": Off-Line, "4": 일시정지, "5": 비상정지
			String ydEqpWrkMode2   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); // A:무인, R:리모컨, E:정비, M:유인
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }

			//크레인작업실적응답 전문 전송여부를 Check
			if (modifier.equals(msgId)) 
			{
				//Backup 이 아니고 L2에서 인터페이스 수신된 경우만 응답 전문 전송여부 Check 
				if (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4))) 
				{
					resYn = true;
				}
			}
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			String sBR_GP     = ""; 	//ON_LINE/OFF_LINE  PARAM
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "M"           ); //야드L2실적구분
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비운전모드전환 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) 
			{
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} 
			else if (ydEqpId.length() < 6) 
			{
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} 
			else if ("".equals(ydEqpWrkMode)) 
			{
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:운전모드 없음";
			} 
			

			if (!"".equals(ydL3Msg)) 
			{
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
		
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"	, ydEqpId        ); //야드설비ID
			jrParam.setField("MODIFIER"     , modifier       ); //수정자
			
			
			
			/**********************************************************
			* 2. 설비모드 Check
			**********************************************************/
	        if ("1".equals(ydEqpWrkMode)) //1: On-Line
	        { 
	        	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 	
	        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , ydEqpWrkMode); 	// 1: 정상
	        } 
	        else if ("2".equals(ydEqpWrkMode)) //2: Off-Line
	        {	
	        	jrParam.setField("YD_EQP_WRK_MODE"  	, ydEqpWrkMode); 	
	        } 
	        else // 일시정지 ,비상정지
	        { 
	        	jrParam.setField("YD_EQP_AUTO_CRN_MODE" , ydEqpWrkMode); 	// 4: 일시정지, 5:비상정지
	        }
	        
        	jrParam.setField("YD_EQP_WRK_MODE2"  		, ydEqpWrkMode2); 	// A:무인, R:리모컨, E:정비, M:유인
	        
        	
        	/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.update(jrParam, updStatEqpMode, logId, methodNm, "설비상태 MODE 수정");


			/**********************************************************
			* 4. 크레인작업실적응답 전문 전송(YFF1L005)
			**********************************************************/
			if (resYn) {
				commUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));
			}

			/**********************************************************
			* 5. 크레인 모드 변경시 
			*   - ON-LINE  : 스케줄요구호출 (작업지시전송)
			*   - OFF-LINE : 크레인 변경 처리
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) 
			{
			
				/*******************************************************
				 * 1: On-Line
				 ******************************************************/
		        if ("1".equals(ydEqpWrkMode)) 
		        { 
					jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID  
					JDTORecordSet jsChk = commDao.select(jrParam, getStatEqp, logId, methodNm, "설비상태조회");

					if ( jsChk.size() > 0) 
					{
						String sYD_EQP_WRK_MODE = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
						
						if (!sYD_EQP_WRK_MODE.equals(ydEqpWrkMode)) 
						{
			        		sBR_GP = "R"; //복구  	
			        	}
						
						if ("CR".equals(ydEqpId.substring(2, 4)) && "1".equals(sYD_EQP_WRK_MODE)) //on-line일때만 리스케줄
						{
							//크레인 리스케줄
							jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
							jrParam.setField("BR_GP" , sBR_GP);
							jrRtn = this.trtCrnResch(jrParam);
						}
						
						/*********************************************
						 * 크레인의 다음 스케줄 명령 선택 기동 
						 * trtCrnResch에서 복구된 크레인에 대한 리스케줄과 명령선택이 이미 기동됨. 확인필요 kbs
						 ********************************************/
						String sYD_EQP_PROG_STAT = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_PROG_STAT"));
						commUtils.printLog(logId, "■■■크레인모드 변경후 명령선택기동", "[INFO]");
						if (!"B".equals(sYD_EQP_PROG_STAT)) 
						{
							JDTORecord jrF1YFL007 = JDTORecordFactory.getInstance().create();
							jrF1YFL007.setField("JMS_TC_CD", "F1YFL007");
							jrF1YFL007.setField("YD_EQP_ID", ydEqpId);
							
							jrRtn = commUtils.addSndData(jrRtn, this.rcvF1YFL007(jrF1YFL007));					
						}
					}
		        } 
		        
		        /*******************************************************
				 * 2: Off-Line
				 ******************************************************/
		        else if ("2".equals(ydEqpWrkMode)) 
		        {	
					/*********************************************
					 * 크레인변경 처리 (offLineChgnCrn)
					 ********************************************/
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

					jrYdMsg.setField("JMS_TC_CD"		 , "YFYFJ305"	); //offLineChgnCrn (OFF-LINE시 작업지시 대체크레인으로 전환)
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"		 , ydEqpId		); //야드설비ID (OFF_LINE 크레인 번호)
		        	
		        	jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
		        } 
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YfCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { yfComm.getYFF1L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(F1YFL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm 	= "설비고장복구실적[ACoilRcvL2SeEJB.F1YFL004] < " + rcvMsg.getResultMsg();
		String logId 		= rcvMsg.getResultCode();
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		//boolean resYn 		= false;	//크레인작업실적응답 전문 전송여부
		boolean resYn 		= true;	//크레인작업실적응답 전문 전송여부 - test용

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "설비고장복구실적(F1YFL004) 수신 ", rcvMsg);

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			String brGp            = ""; //고장복구구분
			if ("".equals(modifier)) { modifier = msgId; }

			
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId	); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , ydEqpStat); //야드L2실적구분 R:고장복구실적
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비고장복구실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) 
			{
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} 
			else if (ydEqpId.length() < 6) 
			{
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} 
			else if ("".equals(ydEqpStat)) 
			{
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} 
			else if ("".equals(ydEqpPauseCode) && "B".equals(ydEqpStat)) 
			{
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} 
			else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat))) 
			{
				ydL3HdRsCd = "BR05";
				ydL3Msg    = "오류:고장복구일시 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			if ("B".equals(ydEqpStat)) 
			{
				brGp = "B"; //고장
				if ("".equals(ydEqpPauseCode)) 
				{
					ydEqpPauseCode = "B000";
				}
			} 
			else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) 
			{
				brGp = "R"; //복구
				
				if ("CR".equals(ydEqpId.substring(2, 4))) 
				{
					ydEqpStat = "W";
				} 
				else 
				{
					ydEqpStat = "W";
				}
				
				if ("".equals(ydEqpPauseCode)) 
				{
					ydEqpPauseCode = "0000";
				}
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("YD_EQP_PROG_STAT"   , ydEqpStat      ); //야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); //고장복구구분
			jrParam.setField("MODIFIER"           , modifier       ); //수정자
			JDTORecordSet jsChk = commDao.select(jrParam, getStatEqp, logId, methodNm, "설비상태조회");

			
			if (jsChk == null || jsChk.size() == 0) 
			{
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} 
			else if (ydEqpStat.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_PROG_STAT")))) 
			{
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
			}
			
			if (!"".equals(ydL3Msg)) 
			{
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
	        
        	
        	/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.update(jrParam, updStatEqp2, logId, methodNm, "설비상태  수정");

			
			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				commDao.update(jrParam, updEqpPause, logId, methodNm, "설비휴지 등록");
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			String sYD_EQP_WRK_MODE = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			
			//크레인 리스케줄
			jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
			if ("B".equals(brGp)) //고장은 그냥 리스케줄. 2020 11 27
			{
				jrRtn = this.offLineChgnCrn(jrParam);
			} 
			else 
			{
				if ("CR".equals(ydEqpId.substring(2, 4)) && "1".equals(sYD_EQP_WRK_MODE)) //on-line일때만 리스케줄
				{
					jrRtn = this.trtCrnResch(jrParam);
				}
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YFF1L005)
			**********************************************************/
			//크레인작업실적응답 전문 전송여부를 Check
			if (modifier.equals(msgId)) 
			{
				//Backup 이 아니고 L2에서 인터페이스 수신된 경우만 응답 전문 전송여부 Check 
				if (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4))) 
				{
					resYn = true;
				}
			}
			
			if (resYn) 
			{
				commUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));
			}

			
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YfCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { yfComm.getYFF1L005(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : OFF-LINE 크레인 변경 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord offLineChgnCrn(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "OFF-LINE 크레인 변경 처리[ACoilRcvL2SeEJB.offLineChgnCrn] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecordSet rsResult    	= null;
		
		JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecord jrRtn = null;	//전문 Return
		
		try{
			commUtils.printLog(logId, methodNm, "S+");

			String modifier     = commUtils.nvl(rcvMsg.getFieldString("MODIFIER"),"OFFLINE");
			String ydEqpId      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String ydGp					= "";
			String ydBayGp			= "";
			
			String ydCrnSchId   = ""; //야드크레인스케쥴ID
			String ydWbookId    = ""; //야드작업예약ID
			String ydWrkProgStat   = ""; //야드작업진행상태
			String chgYdEqpId      = ""; //변경 야드설비ID(크레인)
			String chgYdSchPrior   = ""; //변경 야드스케쥴우선순위
			String chgYdEqpStat    = ""; //변경 야드설비상태

		
			if ("".equals(ydEqpId)) 
			{
				return jrRtn;
			} 
			else 
			{
				if (ydEqpId.length()<2)
				{
					return jrRtn;
				} 
				else 
				{
					ydGp = ydEqpId.substring(0,1);
					ydBayGp = ydEqpId.substring(1,2);
				}
			}
			
			/**********************************************************
			* 1. 해당 Crane(off line 대상 크레인)에 걸려 있는 스케줄 정보 조회
			**********************************************************/
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Logging 을 위한 ID
			jrParam.setResultMsg(methodNm);	//상위 Method 명	
			jrParam.setField("YD_GP"		, ydGp); //야드구분
			jrParam.setField("YD_BAY_GP"	, ydBayGp); //동구분
			jrParam.setField("YD_EQP_ID"	, ydEqpId); //크레인번호
			jrParam.setField("PAGE_NO"		, "1");
			jrParam.setField("PAGE_SIZE"	, "1000");
			rsResult = commDao.select(jrParam, getbtCrnWrkMgtjm, logId, methodNm, "크레인작업관리 스케줄조회");
			
			if (rsResult.size()<=0) 
			{
				commUtils.printLog(logId, "해당 Crane 호기에 걸려 있는 스케줄 정보 없음! "+methodNm, "SL");
				return jrRtn;
			}
			
			
			/**********************************************************
			* 2. 해당 Crane(off line 대상 크레인)에 걸려 있는 스케줄만큼 loop 기동
			**********************************************************/
			String[] arrYdWbookId = new String[rsResult.size()];
			for (int ii = 0; ii < rsResult.size(); ii++) 
			{
			
				ydWbookId  = commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_WBOOK_ID"));
				ydCrnSchId = commUtils.trim(rsResult.getRecord(ii).getFieldString("YD_CRN_SCH_ID"));
			
			    //작업할  야드작업예약ID가 작업 완료한 야드작업예약ID에 있으면 이전에 모두 처리되었으므로 Skip
				if (commUtils.chkExist(arrYdWbookId, ydWbookId)) { continue; }  //해당 값이 있는지를 Check
				
				arrYdWbookId[ii] = ydWbookId;


				/**********************************************************
				* 3. 대체 크레인 정보를 get
				**********************************************************/
				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				JDTORecordSet jsCrn = commDao.select(jrParam, getCraneChange1, logId, methodNm, "크레인변경 조회");

			    if (jsCrn == null || jsCrn.size() <= 0) 
			    {
					continue;
			    }
				
			    JDTORecord jrCrn = jsCrn.getRecord(0);
			    ydWrkProgStat   = commUtils.trim(jrCrn.getFieldString("YD_WRK_PROG_STAT"   )); //야드작업진행상태
				ydEqpId         = commUtils.trim(jrCrn.getFieldString("YD_EQP_ID"          )); //야드설비ID
				chgYdEqpId      = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_ID"      )); //변경 야드설비ID
				chgYdSchPrior   = commUtils.trim(jrCrn.getFieldString("CHG_YD_SCH_PRIOR"   )); //변경 야드스케쥴우선순위
				chgYdEqpStat    = commUtils.trim(jrCrn.getFieldString("CHG_YD_EQP_STAT"    )); //변경 야드설비상태
			
				
				/**********************************************************
				* 4. 작업예약 TB 우선순위 Update
				**********************************************************/
				jrParam.setField("MODIFIER"		, modifier);
				jrParam.setField("YD_SCH_PRIOR"	, chgYdSchPrior);
				jrParam.setField("YD_EQP_ID"   	, chgYdEqpId   );
				commDao.update(jrParam, updWrkBookPrior, logId, methodNm, "작업예약 TB 우선순위 Update");				
				
			
				/**********************************************************
				* 5. 크레인스케줄 TB 크레인ID, 우선순위 Update
				**********************************************************/
				commDao.update(jrParam, updCrnWrkMgtW, logId, methodNm,  "크레인스케줄 TB 크레인ID, 우선순위 Update");				
			
				
				/**********************************************************
				* 6.  OFF-LINE 크레인의 작업지시 취소 전문 송신
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "S".equals(ydWrkProgStat)) 
				{
					commUtils.printLog(logId, "OFF-LINE 크레인의 작업지시 취소 전문 송신 "+methodNm, "SL");
					jrParam.setField("MSG_GP"       , "D"       ); //전문구분(취소)
					jrParam.setField("YD_EQP_ID"   	, commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")));
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L004", jrParam));
				}
			}
			
			commUtils.printLog(logId, "변경된 크레인의 설비상태: "+ chgYdEqpStat + " " + methodNm, "SL");
			
			
			
			/*********************************************
			* 7. 변경 크레인의 다음 스케줄 명령 선택 기동 
			********************************************/
			if (!"".equals(chgYdEqpId) && !chgYdEqpId.equals(ydEqpId)) 
			{
				//변경된 크레인 상태 w이면 명령선택기동 EQP
				if ("W".equals(chgYdEqpStat)) 
				{
					JDTORecord jrF1YFL007 = JDTORecordFactory.getInstance().create();
					jrF1YFL007.setField("JMS_TC_CD", "F1YFL007");
					jrF1YFL007.setField("YD_EQP_ID", chgYdEqpId);
					
					jrRtn = commUtils.addSndData(jrRtn, this.rcvF1YFL007(jrF1YFL007));					
				}
			}
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : Crane Reschedule 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnResch(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인리스케줄[ACoilRcvL2SeEJB.trtCrnResch] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			jrParam.setResultMsg(methodNm);	//Log Method Name

			JDTORecord jrRtn = null;	//크레인작업지시 전문 Return

			//작업예약 야드스케쥴우선순위 수정   
			commDao.update(jrParam, updCrnReschWrkBook, logId, methodNm, "작업예약 야드스케쥴우선순위 수정");

			//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정  
			commDao.update(jrParam, updCrnReschCrnSch, logId, methodNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");

			//크레인작업지시 대상 설비 조회
			JDTORecordSet jsWoEqp = commDao.select(jrParam, getCrnReschWoEqp, logId, methodNm, "크레인작업지시 대상 설비 조회");

			int schCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			String msgId = commUtils.trim(jrParam.getFieldString("MSG_ID")); //수신 전문 I/F ID
			msgId = msgId.substring(0, 2);
			
			jrYdMsg.setField("JMS_TC_CD"         , "F1YFL007"         ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("MODIFIER"        , jrParam.getFieldString("MODIFIER")); //수정자

			for (int ii = 0; ii < schCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"); //야드작업진행상태
				
				//크레인작업지시요구 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvF1YFL007(jrYdMsg));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권상실적(F1YFL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권상실적[ACoilRcvL2SeEJB.rcvF1YFL008] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= true;	//크레인작업실적응답 전문 전송여부

		try {

			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = "";
			String ydUpWrLayerTmp= commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LYR"  )); //야드권상실적단 
			
			if (ydUpWrLayerTmp.length() == 2) {
				ydUpWrLayer = ydUpWrLayerTmp;
			} else {
				//L2 수신
				ydUpWrLayer = ydUpWrLayerTmp.substring(1,3);
			}
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String ydEqpWrkMode2 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2"    )); 
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			
			// PIDEV
			commUtils.printLog(logId, "rcvMsg > ydEqpId:" + ydEqpId + ",ydEqpWrkMode:" + ydEqpWrkMode + ",ydWrkProgStat:" + ydWrkProgStat + ",ydSchCd:" + ydSchCd + ",ydCrnSchId:" + ydCrnSchId + ",ydUpWrLoc:" + ydUpWrLoc + ",ydUpWrLayer:" + ydUpWrLayer + ",ydUpWrLayerTmp:" + ydUpWrLayerTmp + ",ydCrnXaxis:" + ydCrnXaxis + ",ydCrnYaxis:" + ydCrnYaxis + ",ydCrnZaxis:" + ydCrnZaxis + ",ydEqpWrkMode2:" + ydEqpWrkMode2 , "SL");
			
			String ydWbookId     = ""; //야드작업예약ID
			String ydDnWoLoc     = ""; //야드권하지시위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			String stlAppearGp= "";     //재료외형구분 E:소재, Y:제품
			String sCoil_Gp	  = "";		//열연/냉연 코일 구분(HR:열연, CR:냉연)
			String StockId    = "";     //권상중 재료 번호
			String sAUTO_MV_C_YN = "N";    //C동 자동이적 여부
			String sAUTO_MV_E_YN = "N";    //E동 자동이적 여부
			
			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "ACoilRcvL2SeEJBSBean => 크레인권상실적", "APPPI0", "1", "*");
			
			//송신용 
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			
			//크레인작업실적응답 전문 생성용
			resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "UP01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "UP02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "UP03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydUpWrLoc)) {
				ydL3HdRsCd = "UP04";
				ydL3Msg    = "오류:권상실적위치 없음";
			} else if ("".equals(ydUpWrLayer)) {
				ydL3HdRsCd = "UP05";
				ydL3Msg    = "오류:권상실적단 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케쥴 정보 get
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			JDTORecord jrCrnSch = null;
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getStatCrnSch, logId, methodNm, "크레인스케줄상태 조회");

			if (jsCrnSch.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrCrnSch = jsCrnSch.getRecord(0);
				ydWbookId       = commUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydDnWoLoc       = commUtils.trim(jrCrnSch.getFieldString("YD_DN_WO_LOC"    )); //야드권하지시위치
				stlAppearGp     = commUtils.trim(jrCrnSch.getFieldString("STL_APPEAR_GP"    ));//재료외형 구분 소재 제품구분
				StockId         = commUtils.trim(jrCrnSch.getFieldString("STL_NO"    ));//재료번호
				String tmpStat  = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
				String tmpMode2 = commUtils.trim(jrCrnSch.getFieldString("DB_YD_EQP_WRK_MODE2" )); //DB 작업모드2
				sAUTO_MV_E_YN   = commUtils.trim(jrCrnSch.getFieldString("AUTO_MV_E_YN"    )); //E동 자동이적 여부(E3크레인상태)
				sAUTO_MV_C_YN   = commUtils.trim(jrCrnSch.getFieldString("AUTO_MV_C_YN"    )); //C동 자동이적 여부(E3크레인상태)
				sCoil_Gp		= commUtils.trim(jrCrnSch.getFieldString("COIL_GP"    	   )); //열연/냉연 코일 구분(HR:열연, CR:냉연)
				
				//크레인작업실적응답 전송
				if ("2".equals(tmpStat)) {//인터페이스오류로인해 재수신받았을경우 응답전문재송신
					resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
					resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));
					return jrRtn;
				} else if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
				
				// 실적에서 MODE2 가 안올라 오는 경우 DB에 있는 DATA SETUP
				if ("".equals(ydEqpWrkMode2)) {
					ydEqpWrkMode2 = tmpMode2;
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			commUtils.printLog(logId, " ★★★★  권상처리시작" + " 권상코일:" + StockId + ",크레인 스케쥴 ID:" + ydCrnSchId + ",ydDnWoLoc:" + ydDnWoLoc, "SL");

			String currDt = commUtils.getDateTime14(); //현재시각
			jrParam.setField("YD_WBOOK_ID" , ydWbookId  ); //수정자
			jrParam.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드크레인스케쥴ID
			
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("YD_WBOOK_ID"   , ydWbookId ); 
			jrParam.setField("WR_DT" 		 , currDt ); 
			jrParam.setField("MODIFIER" 	 , modifier     ); 
			String ydCarSchId  = "";
			
			/**********************************************************
			* 3. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 3.1 구내운송 소재차량하차개시(YSTSJ009) 전송
			* 3.2 차량이송재료 삭제
			* 3.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			if ("PT".equals(ydUpWrLoc.substring(2, 4))) 
			{
				/**********************************************************
				* 3.1 차량 하차스케쥴 정보 조회
				**********************************************************/
				JDTORecordSet jsCarChk = commDao.select(jrParam, getAxYML008CarSchUdWbId, logId, methodNm, "차량스케줄상태 조회");

				if (jsCarChk.size() < 1) 
				{
					commUtils.printLog(logId, methodNm + "오류:차량 하차스케쥴 DB정보 없음", "SL");
				} 
				else 
				{
					//크레인스케쥴 Table 야드작업진행상태 Check
					ydCarSchId  		= commUtils.trim(jsCarChk.getRecord(0).getFieldString("YD_CAR_SCH_ID"     	)); //야드작업예약ID
					String ydCarUseGp 	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("YD_CAR_USE_GP"     	)); //차량작업구분
					String equipType 	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비 TYPE P:PDA
					String ydCarProgStat= commUtils.trim(jsCarChk.getRecord(0).getFieldString("YD_CAR_PROG_STAT"    )); //차량진행상태
					String ydCarWrkGp	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("YD_CAR_WRK_GP"     	)); //차량작업구분
					String ydCarKind	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("CAR_KIND"     		)); //차량종류
					String sposWlocCd	= commUtils.trim(jsCarChk.getRecord(0).getFieldString("SPOS_WLOC_CD"		)); //발지개소코드
					
					commUtils.printLog(logId, "jsCarChk > ydSchCd:" + ydSchCd + ",ydCarProgStat:" + ydCarProgStat + ",ydCarUseGp:" + ydCarUseGp + ",sCoil_Gp:" + sCoil_Gp + ",equipType:" + equipType + ",sposWlocCd" + sposWlocCd, "SL");
					
					// 차량동간이적 skip
					if (!"PT3".equals(ydSchCd.substring(2, 5))) 
					{
						/**********************************************************
						* 3.2 하차검수 및 하차도착인 경우
						*    -A:하차출발, B:하차도착, C:하차검수, D:하차개시, E:하차완료
						**********************************************************/
						if ( ydCarProgStat.endsWith("C") || ydCarProgStat.endsWith("B")) 
						{
			    			if ( "L".equals(ydCarUseGp) ) 
			    			{
			    				/**********************************************************
								* 3.2.1 구내운송 소재차량하차개시 전송
								**********************************************************/
			    				jrYdMsg = JDTORecordFactory.getInstance().create();
			    				jrYdMsg.setResultCode(logId);	//Log ID
			    				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			    				jrYdMsg.setField("WR_DT" 		, commUtils.getDateTime14()  ); 
			    				jrYdMsg.setField("YD_STK_COL_GP"	, ydUpWrLoc.substring(0, 6)     );
			    				
			    				if("HR".equals(sCoil_Gp))
								{
			    					//열연COIL인경우 YDTSJ009
			    					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ009", jrYdMsg));
			    					
			    					/**********************************************************
									* 3.2.2 코일제품고간이송상하차개시 전송
									**********************************************************/		 
									if ("Y".equals(stlAppearGp)) 
									{	
										jrYdMsg = JDTORecordFactory.getInstance().create();
					    				jrYdMsg.setResultCode(logId);	//Log ID
					    				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					    				jrYdMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId);
					    				
					    				// PIDEV
//										if("Y".equals(sApplyYnPI)) {
											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1111A", jrYdMsg));
//										} else {
//											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR019", jrYdMsg));	
//										}
									}
								}
			    				else
			    				{
			    					//냉연COIL인경우 YFTSJ009
			    					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YFTSJ009", jrYdMsg));
			    				}
			    				
			    			} 
			    			else if ( "P".equals(equipType) ) //출하PDA
			    			{				
			    				/**********************************************************
								* 3.2.3 코일제품이송 하차개시 전송
								**********************************************************/
			    				jrYdMsg = JDTORecordFactory.getInstance().create();
			    				jrYdMsg.setResultCode(logId);	//Log ID
			    				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			    				jrYdMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId);
			    				jrYdMsg.setField("WR_DT" 		    , currDt );
			    				
			    				// PIDEV
//								if("Y".equals(sApplyYnPI)) {
									jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1111B", jrYdMsg));
//								} else {
//									jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR075", jrYdMsg));	
//								}
							}
			    			else if("G".equals(ydCarUseGp))
			    			{
			    				//출하차량인경우
			    				if("CR".equals(sCoil_Gp))
								{
			    					//냉연코일인경우
			    					/**********************************************************
									* 냉연코일제품이송 하차개시 전송
									**********************************************************/
			    					jrYdMsg = JDTORecordFactory.getInstance().create();
				    				jrYdMsg.setResultCode(logId);	//Log ID
				    				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				    				jrYdMsg.setField("YD_CAR_SCH_ID",	ydCarSchId);
				    				jrYdMsg.setField("WR_DT",			currDt );
									
				    				// PIDEV
//									if("Y".equals(sApplyYnPI)) {
										jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1111B", jrYdMsg));
//									} else {
//										jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("CRDMJ009", jrYdMsg));
//									}
								}
			    			}
						}	
					}	
					
					/**********************************************************
					* 3.3 차량하차작업예약번호 갱신
					**********************************************************/
					jrParam.setField("YD_CAR_SCH_ID" , ydCarSchId     ); 
					commDao.update(jrParam, updAxYML008CarSchUdWbId, logId, methodNm, "차량스케줄(하차) 수정");
	
					
					/**********************************************************
					* 3.4 하차완료여부 추가 SJH (6.9)
					**********************************************************/
					JDTORecordSet jsCarChk2 = commDao.select(jrParam, getA7YML008HaChaEndChk, logId, methodNm, "차량스케줄상태 조회");
					
					
					if (jsCarChk2.size() == 0) 
					{
						commUtils.printLog(logId, methodNm + "오류:차량스케쥴 DB정보 없음", "SL");
					} 
					else 
					{
						String CarHaChaEnd 	= commUtils.trim(jsCarChk2.getRecord(0).getFieldString("HACHA_CHK"     )); //하차완료 구분
						String ydEqpWrkStat	= commUtils.trim(jsCarChk2.getRecord(0).getFieldString("YD_EQP_WRK_STAT"     )); //야드설비작업상태
						
						commUtils.printLog(logId, " ★★★★★  권상차량스케쥴:" + ydCarSchId +  " 차량하차완료여부:" + CarHaChaEnd +" ★★★★", "SL");
						
						/**********************************************************
						* 3.5 하차완료처리
						**********************************************************/
						if ("Y".equals(CarHaChaEnd)) 
						{
							/**********************************************************
							* 3.5.1 하차 차량스케줄 정보 갱신
							* -야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
							**********************************************************/
							JDTORecord recPara = JDTORecordFactory.getInstance().create();
							recPara.setResultCode(logId);	//Log ID
							recPara.setResultMsg(methodNm);	//Log Method Name
							recPara.setField("MODIFIER" 		, modifier     ); 
							recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
							recPara.setField("WR_DT" 			, currDt ); 
							commDao.update(recPara, updAxYDL009CarSchUd, logId, methodNm, "하차 차량스케줄 수정  ");

							
							/**********************************************************
							* 3.5.2 하차작업완료 처리
							**********************************************************/
							if( "L".equals(ydEqpWrkStat) )
							{
				    			if(!"PT3".equals(ydSchCd.substring(2, 5))) // 차량동간이적 skip  
				    			{	
				    				jrYdMsg = JDTORecordFactory.getInstance().create();
				    				jrYdMsg.setResultCode(logId);	//Log ID
				    				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				    				jrYdMsg.setField("WR_DT"	        , currDt);
				    				jrYdMsg.setField("YD_CAR_SCH_ID"	, ydCarSchId);
				    				
				    				//출하 PDA하차 작업 인경우
				    				if ( "P".equals(equipType) )//출하PDA
				    				{
				    					/**********************************************************
										* 코일제품이송 하차완료 전송PDA(DMYDR073~075)
										**********************************************************/
				    					// PIDEV
//										if("Y".equals(sApplyYnPI)) {
											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1121B", jrYdMsg));
//										} else {
//											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR076", jrYdMsg));	
//										}	
									}	
				    				else
				        			{
				    					/**********************************************************
										* 구내운송 소재차량하차완료 + 반품 + 순천냉연입고
										**********************************************************/
				    					if("HR".equals(sCoil_Gp))
										{
					    					//열연COIL인경우 YDTSJ010
				    						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ010", jrYdMsg));
				    						
				    						/**********************************************************
											* 코일제품고간이송상하차완료 송신 YDDMR021(제품인 경우에만 )
											**********************************************************/
											if ("Y".equals(stlAppearGp)) 
											{
												// PIDEV
//												if("Y".equals(sApplyYnPI)) {
													jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1121A", jrYdMsg));
//												} else {
//													jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR021", jrYdMsg));
//												}
											}
										}
					    				else
					    				{	
					    					if("L".equals(ydCarUseGp))
					    					{
					    						//냉연COIL + 구내운송 인경우
					    						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YFTSJ010", jrYdMsg));	//구내운송 하차완료
					    						
					    						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YFCRJ003", jrYdMsg));	//냉연MES 하차완료
					    					}
					    					else
					    					{
												// PIDEV
//												if("Y".equals(sApplyYnPI)) {
													jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1121A", jrYdMsg));
//												} else {
//						    						//냉연COIL + 구내운송이 아닌경우...출하차량이용한 순천이송 하차 + 사외창고이송 하차
//						    						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("CRDMJ010", jrYdMsg));	//하차완료실적(냉연)
//												}
					    					}
					    					
											// PIDEV
//											if("Y".equals(sApplyYnPI)) {
												// PI 인 경우 삭제
						    					//발지가 사외창고가 아니고 냉연코일 제품 하차시 출하로 전문(CRDMJ018) 전문 전송해줘야함
												if ("Y".equals(stlAppearGp) && !sposWlocCd.equals("DZY30")) 
												{
													jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1121A", jrYdMsg));	
												}
												
//											} else {
//						    					//발지가 사외창고가 아니고 냉연코일 제품 하차시 출하로 전문(CRDMJ018) 전문 전송해줘야함
//												if ("Y".equals(stlAppearGp) && !sposWlocCd.equals("DZY30")) 
//												{
//													jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("CRDMJ018", jrYdMsg));	
//												}
//											}
					    				}
				        			} 
				    			}
							}
		    			}							
					}
					
					/**********************************************************
					* 3.6 차량이송재료 삭제
					**********************************************************/
					commDao.update(jrParam, updAxYML008CarMtlDel, logId, methodNm, "차량이송재료 삭제");
				}
				
				
				/**********************************************************
				* 3.7 하차 차량스케줄 정보 조회
				**********************************************************/
				JDTORecordSet jsCarSchChk = commDao.select(jrParam, getAxYML008CarSchUdSchId, logId, methodNm, "차량스케줄정보 조회");

				if (jsCarSchChk.size() == 0) 
				{
					commUtils.printLog(logId, methodNm + "오류:크레인스케쥴 DB정보 없음", "SL");
				} 
				else 
				{
					/**********************************************************
					* 3.8 하차 차량스케줄 수정
					* -야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
					**********************************************************/
					String ydEqpWrkSh  	= commUtils.trim(jsCarSchChk.getRecord(0).getFieldString("YD_EQP_WRK_SH")); 	//야드설비작업매수
					String ydEqpWrkWt  	= commUtils.trim(jsCarSchChk.getRecord(0).getFieldString("YD_EQP_WRK_WT"));     //야드설비작업중량
					String ydCarProgStat= commUtils.trim(jsCarSchChk.getRecord(0).getFieldString("YD_CAR_PROG_STAT")); 	//야드차량진행상태
					String ydCarupStDt  = commUtils.trim(jsCarSchChk.getRecord(0).getFieldString("YD_CARUD_ST_DT")); 	//야드하차개시일시
					
					jrParam.setField("YD_EQP_WRK_SH" 	, ydEqpWrkSh   ); 
					jrParam.setField("YD_EQP_WRK_WT" 	, ydEqpWrkWt   ); 
					jrParam.setField("YD_CAR_PROG_STAT" , ydCarProgStat); 
					jrParam.setField("YD_CARUD_ST_DT" 	, ydCarupStDt  ); 
					commDao.update(jrParam, updAxYML008CarSchUdSchId, logId, methodNm, "차량스케줄(하차) 수정");
				}
			}
			
			
			/**********************************************************
			* 4. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 4.1 구내운송 소재차량상차개시(YMTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 4.2 출하관리출하상차개시(YMDSJ006) 전송
			*   - 야드차량사용구분이 출하차량(G)
			* 4.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("PT".equals(ydDnWoLoc.substring(2, 4))) 
			{
				/**********************************************************
				* 4.1 차량하차스케줄 정보 조회
				**********************************************************/
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 	 , ydWbookId); 
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId); 
				recPara.setField("STL_NO" 	 	 , StockId); 
				recPara.setField("YD_EQP_ID" 	 , ydEqpId); 
				recPara.setField("YD_UP_WR_LOC"  , ydUpWrLoc);  
				JDTORecordSet jsCarSch = commDao.select(recPara, getAxYML008CarSchLd, logId, methodNm, "차량상차스케줄 정보 "); 
		    	
				if (jsCarSch.size() < 1) 
				{
					commUtils.printLog(logId, methodNm + "오류:크레인권상실적 상차 차량스케줄 DB정보 없음", "SL");
				}
				else
				{
					JDTORecord jrCarSch = jsCarSch.getRecord(0);
				
					ydCarSchId			= commUtils.trim(jrCarSch.getFieldString("YD_CAR_SCH_ID")); 	//야드차량스케쥴
					String ydCarProgStat= commUtils.trim(jrCarSch.getFieldString("YD_CAR_PROG_STAT")); 	//차량진행상태
					String cmbnCarldYn  = commUtils.trim(jrCarSch.getFieldString("CMBN_CARLD_YN"));     //복수동 조합상차유무(시작:S,종료:E,단일상차:N)
					String equipType 	= commUtils.trim(jrCarSch.getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비 TYPE P:PDA
					String arrWlocCd 	= commUtils.trim(jrCarSch.getFieldString("ARR_WLOC_CD"  	));
					String trnEqpCd 	= commUtils.trim(jrCarSch.getFieldString("TRN_EQP_CD"    	)); //운송장비코드
					String sposWlocCd 	= commUtils.trim(jrCarSch.getFieldString("SPOS_WLOC_CD"  	)); //발지개소코드
					String sposYdPntCd 	= commUtils.trim(jrCarSch.getFieldString("SPOS_YD_PNT_CD"	)); //발지야드포인트코드
					String ydCarUseGp 	= commUtils.trim(jrCarSch.getFieldString("YD_CAR_USE_GP"	)); //차량용도
					String sendYDDMR007	= commUtils.trim(jrCarSch.getFieldString("SEND_YDDMR007"	)); //상차개시 전송유무
					
					commUtils.printLog(logId, "jrCarSch > ydCarProgStat:" + ydCarProgStat + ",cmbnCarldYn:" + cmbnCarldYn + ",equipType:" + equipType + ",arrWlocCd:" + arrWlocCd + ",trnEqpCd:" + trnEqpCd + ",sposWlocCd:" + sposWlocCd + ",sposYdPntCd:" + sposYdPntCd + ",ydCarUseGp:" + ydCarUseGp + ",sendYDDMR007:" + sendYDDMR007, "SL");
					
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId); 
					
					/**********************************************************
					* 4.2 복수동 아닐 경우, 상차개시 처리
					**********************************************************/
					if ( ("3".equals(ydCarProgStat) || "2".equals(ydCarProgStat)) && !"E".equals(cmbnCarldYn) ) 
					{
						jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						// 차량동간이적 skip
						if (!"PT3".equals(ydSchCd.substring(2, 6)))  
						{
							/**********************************************************
							* 4.2.1 상차개시 처리
							* -야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
							**********************************************************/
							recPara.setField("MODIFIER" 	, modifier     ); 
							recPara.setField("ARR_WLOC_CD"  , arrWlocCd); //착지개소코드
							recPara.setField("WR_DT" 		, commUtils.getDateTime14()     );
							commDao.update(recPara, updAxYML008CarSchLd, logId, methodNm, " 상차 차량스케줄 수정 ");	
							
							
							/**********************************************************
							* 4.2.2 구내운송 소재차량상차개시
							* -권하실적에서 처리되도록 수정 필요. KBS
							**********************************************************/
							if ("L".equals(ydCarUseGp) && "Y".equals(sendYDDMR007)) 
							{
								if("HR".equals(sCoil_Gp))
								{
									//열연COIL인경우 YDTSJ007
									jrYdMsg.setField("JMS_TC_CD"         , "YDTSJ007"	); //JMSTC코드
									jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    	); //JMSTC생성일시
									jrYdMsg.setField("TRN_EQP_CD"        , trnEqpCd  	); //운송장비코드
									jrYdMsg.setField("SPOS_WLOC_CD"      , sposWlocCd	); //발지개소코드
									jrYdMsg.setField("SPOS_YD_PNT_CD"    , sposYdPntCd	); //발지야드포인트코드
									jrYdMsg.setField("ARR_WLOC_CD"       , arrWlocCd	); //착지개소코드
									jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    	); //운송작업시작일시
								}
								else
								{
									//냉연COIL인경우 YFTSJ007
									jrYdMsg.setField("JMS_TC_CD"         , "YFTSJ007"	); //JMSTC코드
									jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    	); //JMSTC생성일시
									jrYdMsg.setField("TRN_EQP_CD"        , trnEqpCd  	); //운송장비코드
									jrYdMsg.setField("SPOS_WLOC_CD"      , sposWlocCd	); //발지개소코드
									jrYdMsg.setField("SPOS_YD_PNT_CD"    , sposYdPntCd	); //발지야드포인트코드
									jrYdMsg.setField("ARR_WLOC_CD"       , arrWlocCd	); //착지개소코드
									jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    	); //운송작업시작일시
								}
								
								jrRtn = commUtils.addSndData(jrRtn	 , jrYdMsg);
							}	
							
							
							/**********************************************************
							* 4.2.3 출하 소재차량상차개시
							**********************************************************/
							if ("G".equals(ydCarUseGp))
							{
								if (!"P".equals(equipType))
								{
									//DMYDR060전문으로 출하 하는경우...
									if("HR".equals(sCoil_Gp))
									{
										//열연코일일때
										recPara.setField("STL_NO" , StockId);
										JDTORecordSet jsPofrto = commDao.select(recPara, getYmPoFrtoInfo, logId, methodNm, "임가공 여부 정보 "); 
										if (jsPofrto.size() > 0  && "Y".equals(sendYDDMR007)) 
										{
											// 임가공이송상하차개시 전문조회
//											if("Y".equals(sApplyYnPI)) {
												jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1115", recPara));
//											} else {
//												jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR020", recPara));
//											}
										} 
										else 
										{
											if (!"E".equals(cmbnCarldYn) && "Y".equals(sendYDDMR007)) //복수동 2차 상차도 도착인 경우 제외
											{  
												// PIDEV
//												if("Y".equals(sApplyYnPI)) {
													jrYdMsg = JDTORecordFactory.getInstance().create();
													//출하관리 상차개시
													jrYdMsg.setField("MQ_TC_CD"          , "M10YDLMJ1071"											   ); //JMSTC코드
													jrYdMsg.setField("MQ_TC_CREATE_DDTT" , currDt    												   ); //JMSTC생성일시
													jrYdMsg.setField("TRN_REQ_DATE"		 , commUtils.trim(jrCarSch.getFieldString("TRANS_ORD_DATE" ))  ); //운송작업지시일자
													jrYdMsg.setField("TRN_REQ_SEQ" 		 , commUtils.trim(jrCarSch.getFieldString("TRANS_ORD_SEQNO"))  ); //운송작업지시순번
													// jrYdMsg.setField("CARD_NO"           , commUtils.trim(jrCarSch.getFieldString("CARD_NO"        ))); //카드번호
													jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrCarSch.getFieldString("CAR_NO"         ))  ); //차량번호
													jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                	   ); // 야드구분
													jrYdMsg.setField("DIST_GOODS_GP"     , "H"                          							   ); // 출하제품구분
													jrYdMsg.setField("SCH_YN"            , "N"							                               ); // 스케쥴여부
													jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                               	   ); //상차개시일자
													jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                               	   ); //상차개시시각
													
//												} else {
//													//출하관리 상차개시
//													jrYdMsg.setField("TC_CODE"           , "YDDMR007"); //JMSTC코드
//													jrYdMsg.setField("TC_CREATE_DDTT"    , currDt    ); //JMSTC생성일시
//													jrYdMsg.setField("JMS_TC_CD"         , "YDDMR007"); //JMSTC코드
//													jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
//													jrYdMsg.setField("CARD_NO"           , commUtils.trim(jrCarSch.getFieldString("CARD_NO"        ))); //카드번호
//													jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrCarSch.getFieldString("CAR_NO"         ))); //차량번호
//													jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
//													jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
//													jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
//													jrYdMsg.setField("TRANS_WORD_DATE"   , commUtils.trim(jrCarSch.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
//													jrYdMsg.setField("TRANS_WORD_SEQNO"  , commUtils.trim(jrCarSch.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
//												}
												
												jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
											}
										}
									}
									else
									{
										// 냉연코일일때
										// PIDEV
										// 복수동 2차 상차도 도착인 경우 제외
										if (!"E".equals(cmbnCarldYn) && "Y".equals(sendYDDMR007)) {
//											if ("Y".equals(sApplyYnPI)) {
												// 출하관리 상차개시
												// 22-09-20 PI용 냉연 전문 추가
												jrYdMsg = JDTORecordFactory.getInstance().create();
												jrYdMsg.setField("MQ_TC_CD", 			"M10YDLMJ1071");
												jrYdMsg.setField("MQ_TC_CREATE_DDTT", 	currDt);
												jrYdMsg.setField("TRN_REQ_DATE", 		commUtils.trim(jrCarSch.getFieldString("TRANS_ORD_DATE"))); 	//운송의뢰일자
												jrYdMsg.setField("TRN_REQ_SEQ", 		commUtils.trim(jrCarSch.getFieldString("TRANS_ORD_SEQNO"))); 	//운송의뢰순번
												jrYdMsg.setField("CAR_NO", 				commUtils.trim(jrCarSch.getFieldString("CAR_NO"))); 	//차량번호
												jrYdMsg.setField("YD_GP", 				ydEqpId.substring(0, 1)); 	// 야드구분
												jrYdMsg.setField("DIST_GOODS_GP", 		"L"); 	// 출하제품구분
												jrYdMsg.setField("SCH_YN", 				"N"); 	// 스케쥴여부
												jrYdMsg.setField("CARLOAD_START_DATE", 	currDt.substring(0,  8)); //상차개시일자
												jrYdMsg.setField("CARLOAD_START_TIME", 	currDt.substring(8, 14)); //상차개시시각
												
//											} else {
//												//출하관리 상차개시(냉연)
//												jrYdMsg.setField("TC_CODE",				"CRDMJ007");													//JMSTC코드
//												jrYdMsg.setField("TC_CREATE_DDTT",		currDt);														//JMSTC생성일시
//												jrYdMsg.setField("JMS_TC_CD",			"CRDMJ007");													//JMSTC코드
//												jrYdMsg.setField("JMS_TC_CREATE_DDTT",	currDt);														//JMSTC생성일시
//												jrYdMsg.setField("CR_YD_GP",			"LD00");														//야드구분
//												jrYdMsg.setField("TRANS_FRTOMOVE_GP",	commUtils.trim(jrCarSch.getFieldString("TRANS_FRTOMOVE_GP")));	//운송이송구분
//												jrYdMsg.setField("TRANS_WORD_DATE",		commUtils.trim(jrCarSch.getFieldString("TRANS_ORD_DATE" )));	//운송작업지시일자
//												jrYdMsg.setField("TRANS_WORD_SEQNO",	commUtils.trim(jrCarSch.getFieldString("TRANS_ORD_SEQNO")));	//운송작업지시순번
//												jrYdMsg.setField("CAR_NO",				commUtils.trim(jrCarSch.getFieldString("CAR_NO")));				//차량번호
//												jrYdMsg.setField("CR_CAR_NO",			commUtils.trim(jrCarSch.getFieldString("CARD_NO")));			//냉연카드번호
//												jrYdMsg.setField("CARLOAD_STA_DATE",	currDt.substring(0, 8));										//상차개시일자
//												jrYdMsg.setField("CARLOAD_STA_TIME",	currDt.substring(8, 14));										//상차개시시각	
//												jrYdMsg.setField("REGISTER",			"F1YFL008");													//등록자
//											}
										}
										
										jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
									}
								} 
								else if("Y".equals(sendYDDMR007))
								{
									// PIDEV
//									if("Y".equals(sApplyYnPI)) {
										//코일제품이송 상차개시 전송PDA
										jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1071B", recPara));
//									} else {
//										//코일제품이송 상차개시 전송PDA
//										jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR071", recPara));										
//									}
								}
							}
						}
					} 
					
					/**********************************************************
					* 4.3 복수동 일 경우, 상차개시 처리
					**********************************************************/
					else if ( ("3".equals(ydCarProgStat) || "2".equals(ydCarProgStat))
							&& "E".equals(cmbnCarldYn) ) 
					{
						// 복수동 작업도 상차 개시 상태로 변경
						recPara.setField("MODIFIER" 	, modifier     ); 
						recPara.setField("ARR_WLOC_CD"  , arrWlocCd); //착지개소코드
						recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
						//상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						commDao.update(recPara, updAxYML008CarSchLd, logId, methodNm, " 상차 차량스케줄 수정 ");	
					}
						
					if ("G".equals(ydCarUseGp)) {	//출하차량
						JDTORecordSet jsSangChaEnd = commDao.select(recPara, getA7YML008SangChaEndChk, logId, methodNm, "상차완료 여부 정보 ");

						jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						
						if (jsSangChaEnd.size() > 0 ) {
						    // 상차 완료
							if ("Y".equals(commUtils.trim(jsSangChaEnd.getRecord(0).getFieldString("SANG_CHA_END_YN")))) 
							{
								jrYdMsg.setField("GOODS_EA","*");
							} 
							else 
							{
								jrYdMsg.setField("GOODS_EA","1");
							}
						} 
						else 
						{
							jrYdMsg.setField("GOODS_EA","1");
						}
						
						// 냉연이송
						if ("P".equals(equipType)) 
						{  
							// 냉연이송상차실적 
							jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
							jrYdMsg.setField("STL_NO"		, StockId);
							
							// PIDEV
//							if("Y".equals(sApplyYnPI)) {
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1081B", jrYdMsg));
//							} else {
//								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR072", jrYdMsg));	
//							}
						} 
						else 
						{
							jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
							jrYdMsg.setField("STL_NO"		, StockId);
							
							if("HR".equals(sCoil_Gp))
							{
								// 코일일품출하상차실적						
								// PIDEV
//								if("Y".equals(sApplyYnPI)) {
									jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1081A", jrYdMsg));
//								} else {
//									jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR011", jrYdMsg));	
//								}
							}
							else
							{
								//냉연코일출하상차실적
								//20.12.04 박주한책임과 통화 후 냉연코일은 전문 필요 없다고 함
								
								// PIDEV
								// 22-09-20 PI용 냉연 전문 추가
//								if ("Y".equals(sApplyYnPI)) {
									jrYdMsg.setField("DIST_GOODS_GP", "L");
									jrYdMsg.setField("SCH_YN", "N"); // equipType = P 인 경우 Y
									jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1081C", jrYdMsg));
//								}
							}
						}
					}
				}
			}
			
			/**********************************************************
			* 5. 실적 파라미터 세팅
			**********************************************************/
			//야드권상작업수행구분
			String ydUpWrkActGp = ydEqpWrkMode;

			if ("0".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "M"; 	//Manual
			} else if ("1".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "A"; 	//Auto
			} else if ("9".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "B"; 	//Backup
			}
			
			//설비
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("YD_WBOOK_ID"   	, ydWbookId ); 
			jrParam.setField("MODIFIER" 	 	, modifier     );
			jrParam.setField("YD_EQP_ID"       	, ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_PROG_STAT" , "2"         ); //야드설비상태(권상중)
			jrParam.setField("YD_UP_CMPL_DT"   	, currDt      ); //야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    	, ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("YD_UP_WR_LYR"  	, ydUpWrLayer ); //야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP"	, ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  	, ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  	, ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  	, ydCrnZaxis  ); //야드권상실적Z축
			jrParam.setField("YD_STK_COL_GP"    , ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_UP_WRK_MODE2"  , ydEqpWrkMode2); //자동모드
			
			
			/**********************************************************
			* 6. 설비(야드설비상태) 수정
			**********************************************************/
			commDao.update(jrParam, updStatEqp, logId, methodNm, "설비상태 수정");
			
			
			/**********************************************************
			* 7. 크레인 위치정보 정보 조회
			*    - 설비 추출일때는 적치단(권상위치)에 재료가 없는 경우가 더 많음.
			**********************************************************/
			JDTORecordSet jsStklyrChk = null;
			jsStklyrChk = commDao.select(jrParam, getAxYML008UpStkLyrCR, logId, methodNm, "권상정보 조회");
			
			
			/**********************************************************
			* 8. 스케줄 정합성 체크
			**********************************************************/
			if (jsStklyrChk.size() < 1)  
			{			
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, new Exception("오류:크레인스케쥴 DB정보 없음")));
			} 
		
			/**********************************************************
			* 9. 권상된 코일의 적재위치를 크레인으로 수정
			**********************************************************/
			JDTORecord recPara1 = jsStklyrChk.getRecord(0);	
			recPara1.setField("MODIFIER", modifier);
			commDao.update(recPara1, updAxYML008UpStkLyrToCR, logId, methodNm, "크레인 LAY 수정");
			
			
			/**********************************************************
			* 10. 적치단 권상위치 클리어
			**********************************************************/
			commDao.update(jrParam, updAxYML008UpStkLyr, logId, methodNm, "적치단(권상위치) 수정");
			

			/**********************************************************
			* 11. 크레인스케쥴 수정
			**********************************************************/
			commDao.update(jrParam, updAxYML008CrnSch, logId, methodNm, "크레인스케쥴 수정");

			
			/**********************************************************
			* 12. 크레인작업실적응답 전송
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_UP_WR_LOC"  , ydUpWrLoc); //야드권상실적위치
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));
			}

			
			/*************************************************************
			 * 13. 권상시 하단저장품 상차예정차량 입동 처리
			 *************************************************************/
			if ("02".equals(ydUpWrLayer)) 
			{
				jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
				jrParam.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호
				JDTORecordSet jsWbookId = commDao.select(jrParam, getWbookIdByLoc, logId, methodNm, "하단 코일 조회");
				
				if (jsWbookId.size() > 0) 
				{
					//크레인 스케줄 기동 YFYFJ303 호출
					JDTORecord jsMsg = JDTORecordFactory.getInstance().create();
					jsMsg.setField("JMS_TC_CD"			, "YFYFJ303"); 
	    			jsMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
	    			jsMsg.setField("YD_SCH_CD"  		, ""); //야드스케쥴코드
	    			jsMsg.setField("YD_EQP_ID"  		, ""); //야드설비ID
	    			
					for (int ii = 0; ii < jsWbookId.size(); ++ii) {
		    			jsMsg.setField("YD_WBOOK_ID"+ii	, jsWbookId.getRecord(ii).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
					} 
					
	    			jsMsg.setField("SCH_CNT", "" + jsWbookId.size()); 
		    		jrRtn = commUtils.addSndData(jrRtn, jsMsg);
				}
			} 
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YfCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { yfComm.getYFF1L005(resMsg)});
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(F1YFL009)
	 *	
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권하실적[ACoilRcvL2SeEJB.rcvF1YFL009] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= true;	//크레인작업실적응답 전문 전송여

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LYR"  )); //야드권하실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축	
			String ydEqpWrkMode2 = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE2")); 
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			
			commUtils.printLog(logId, "rcvMsg > ydEqpId:" + ydEqpId + ",ydEqpWrkMode:" + ydEqpWrkMode + ",ydWrkProgStat:" + ydWrkProgStat + ",ydSchCd:" + ydSchCd + ",ydCrnSchId:" + ydCrnSchId + ",ydDnWrLoc:" + ydDnWrLoc + ",ydDnWrLayer:" + ydDnWrLayer + ",ydCrnXaxis:" + ydCrnXaxis + ",ydCrnYaxis:" + ydCrnYaxis + ",ydCrnZaxis:" + ydCrnZaxis + ",ydEqpWrkMode2:" + ydEqpWrkMode2 , "SL");
			
			String ydWbookId     = ""; //야드작업예약ID
			String ydUpWrLoc     = ""; //야드권상실적위치
			String ydCarAutoStart= "N"; //차량 자동 출발			
			
			
			String currDt = commUtils.getDateTime14(); //현재시각
			
			if ("".equals(modifier)) { modifier = msgId; }
			
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn   = null;	//전문 Return
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			
			String ydL3HdRsCd 		= "";		//야드L3처리결과코드
			String ydL3Msg    		= ""; 		//야드L3MESSAGE
			String StockId  		= ""; 
			String stlAppearGp  	= "";   	//제품 소재 구분
			String sProgCd  		= "";   	//진도코드
			
			String coil_gp			= "";		//열연/냉연 코일 구분(HR:열연, CR:냉연)
			String CmbnCarldSndYn  	= "N";   	//복수동 완료 시 복수동처리 로직에서 권하위치 clear 여부
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "D"); //야드L2실적구분 (권하실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_XAXIS"	, ydCrnXaxis); //야드크레인스케쥴ID
			jrParam.setField("YD_CRN_YAXIS" , ydCrnYaxis); //수정자			
			jrParam.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrParam.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc    ); //권하지시위치-논리좌표(8자리)
			jrParam.setField("YD_STK_LYR_NO"  , ydDnWrLayer  ); //권하지시단  -논리좌표(3자리)
			
//			String sApplyYnPI = commDao.ApplyYnPI("", "rcvF1YFL009 => 크레인 권하시", "APPPI0", "1", "*");
			
			/***************************
			 ** 1. 강제 권하시 
			 ***************************/
			if ("5".equals(ydWrkProgStat)) 
			{
				//크레인작업실적응답 전문 전송
				JDTORecord rowDown = conpulDown(rcvMsg);
				if(!"0000".equals(rowDown.getFieldString("YD_L3_HD_RS_CD")))
				{
					return commUtils.addSndData(jrRtn, yfComm.getYFF1L005(rowDown));
				}
			}
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydDnWrLayer.length() == 3) 
			{
			   //화면기동
				ydDnWrLayer = ydDnWrLayer.substring(1,3);  // 자리수 변경
			}
			
			if ("".equals(ydEqpId)) 
			{
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} 
			else if (ydEqpId.length() < 6) 
			{
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} 
			else if ("".equals(ydCrnSchId)) 
			{
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} 
			else if ("".equals(ydDnWrLoc)) 
			{
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";	
			}
			else if ("XX".equals(ydDnWrLoc.substring(0, 2))) 
			{
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 이상";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케쥴 정보 get
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자
			jrParam.setField("YD_SCH_CD"    , ydSchCd      ); //야드스케쥴코드
			  
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getStatCrnSch, logId, methodNm, "크레인스케줄상태 조회");

			if (jsCrnSch.size() == 0) 
			{
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:크레인스케쥴ID DB정보 없음";
			} 
			else 
			{
				//크레인스케쥴 Table 야드작업진행상태 Check
				JDTORecord jrCrnSch = jsCrnSch.getRecord(0);
				ydWbookId       = commUtils.trim(jrCrnSch.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydSchCd         = commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
				ydUpWrLoc       = commUtils.trim(jrCrnSch.getFieldString("YD_UP_WR_LOC"    )); //야드권상실적위치
				StockId         = commUtils.trim(jrCrnSch.getFieldString("STL_NO"        )); //코일 
				stlAppearGp     = commUtils.trim(jrCrnSch.getFieldString("STL_APPEAR_GP"    ));//재료외형 구분 소재 제품구분
				coil_gp			= commUtils.trim(jrCrnSch.getFieldString("COIL_GP"			));//열연/냉연 코일 구분(HR:열연, CR:냉연)
				String tmpMode2 = commUtils.trim(jrCrnSch.getFieldString("DB_YD_EQP_WRK_MODE2" )); //DB 작업모드2
				String tmpStat  = commUtils.trim(jrCrnSch.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"       )); //야드설비ID
				sProgCd     	= commUtils.trim(jrCrnSch.getFieldString("CURR_PROG_CD"    ));//재료외형 구분 소재 제품구분
				
				//인터페이스오류로인해 재수신받았을경우 응답전문재송신
				if("4".equals(tmpStat))
				{
					resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
					resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));
					return jrRtn;
				}
				else if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) 
				{
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} 
				else if (!ydEqpId.equals(tmpEqpId)) 
				{
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
				
				commUtils.printLog(logId, " ★★★★  권하처리시작:" + " 권하코일:" + StockId + ",작업모드2:" + ydEqpWrkMode2 + ",coil_gp:" + coil_gp, "SL");
				
				// 실적에서 MODE2 가 안올라 오는 경우 DB에 있는 DATA SETUP
				if ("".equals(ydEqpWrkMode2)) 
				{
					ydEqpWrkMode2 = tmpMode2;
				}
			}
			
			if (!"".equals(ydL3Msg)) 
			{
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			commUtils.printLog(logId, " ★★★★  권하처리시작:" +" 스케줄코드:" + ydSchCd + ",권상위치:" + ydUpWrLoc + ",권하위치:" + ydDnWrLoc + ",권하코일:" + StockId + ",크레인 스케쥴 ID:" + ydCrnSchId, "SL");

			//조회 Parameter
			jrParam.setField("YD_WBOOK_ID"  	, ydWbookId); //야드작업예약ID
			jrParam.setField("YD_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO" 	, ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			jrParam.setField("YD_STK_LYR_NO"	, ydDnWrLayer); //야드권하작업수행구분
			jrParam.setField("YD_EQP_ID"    	, ydEqpId); //야드설비ID(크레인)
			jrParam.setField("YD_EQP_PROG_STAT" , "4"    ); //야드설비상태(권하완료)

			//야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;
			
			if ("0".equals(ydEqpWrkMode)) 
			{
				ydDnWrkActGp = "M"; //Manual
			} 
			else if ("1".equals(ydEqpWrkMode)) 
			{
				ydDnWrkActGp = "A"; //Auto
			} 
			else if ("9".equals(ydEqpWrkMode)) 
			{
				ydDnWrkActGp = "B"; //Backup
			}

			
			/**********************************************************
			* 3. 파라미터 setting 
			**********************************************************/
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      ); //야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LYR"    , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp); //야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis  ); //야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis  ); //야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); //야드권하실적Z축
			jrParam.setField("WR_DT"           , currDt      ); //실적일시
			jrParam.setField("UP_DN_GP"        , "D"         ); //권상권하구분(권하)
			
			commDao.update(jrParam, updWrXISCrnSch, logId, methodNm, "크레인스케쥴 실적좌표 수정");

							
			
			/**********************************************************
			* 4. 권하실적위치가 차량(상차)
			* 4.1 차량이송재료 등록
			* 4.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 마지막 크레인스케줄 이면
			* 4.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 4.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 4.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 4.4 야드차량사용구분이 출하차량(G)
			* 4.4.1 출하관리 일품출하상차실적(YSDSJ007) 전송
			* 4.4.2 출하관리 출하상차완료(YSDSJ008) 전송
			*     - 상차완료(마지막 크레인스케줄)이면
			**********************************************************/
			//차량상차완료여부(소재이송지시 수정 및 공통Table 소재이송일시 수정)
			String carLdCmplYn 		= "N";
			String ydCarUseGp  		= "";
			String ydCarSchId  		= "";
			String CarNo  			= "";
			String TransOrdDate  	= "";
			String TransOrdSeqNo  	= "";
			String WlocCd  			= "";
			String ydPndCd  		= "";
			String drvTelNo  		= "";   // 기사전화번호
			String ydCarProgStat  	= "";   // 차량진행상태
			String equipType 	    = "";   // 냉연이송 구분
			String cmbnCarldyn 	    = "";   // 복수상차
			String ydCarWrkGp		= "";	// 야드차량작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
			
			if ("PT".equals(ydDnWrLoc.substring(2, 4)) && !"PT3".equals(ydSchCd.substring(2, 5))) //차량동간이적은 제외
			{
				/**********************************************************
				* 4.1 상차 차량스케줄 조회
				**********************************************************/
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				recPara.setField("YD_CRN_SCH_ID"	, ydCrnSchId     ); 
				recPara.setField("YD_STK_COL_GP"	, ydDnWrLoc.substring(0, 6) ); 
				recPara.setField("YD_EQP_ID"		, ydEqpId);
				JDTORecordSet jsCarUpSch = commDao.select(recPara, getAxYML009CarSchLd, logId, methodNm, "상차 차량스케줄 조회 "); 
				
				if (jsCarUpSch.size() < 1) 
				{
					commUtils.printLog(logId, methodNm + "오류:상차 차량스케줄 DB정보 없음", "SL");
				}
				else
				{
					JDTORecord jrCarUpSch = jsCarUpSch.getRecord(0);

					ydCarSchId  	= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_SCH_ID"));
					CarNo 			= commUtils.trim(jrCarUpSch.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrCarUpSch.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrCarUpSch.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrCarUpSch.getFieldString("YD_PNT_CD")); 					
					ydCarUseGp 		= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_USE_GP")); 		//야드차량사용구분
					drvTelNo 		= commUtils.trim(jrCarUpSch.getFieldString("DEST_TEL_NO")); 		//목적지 전화번호
					carLdCmplYn 	= commUtils.trim(jrCarUpSch.getFieldString("CAR_LD_CMPL_YN")); 		//차량상차완료여부
					ydCarProgStat 	= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_PROG_STAT")); 	//차량진행상태
					equipType 		= commUtils.trim(jrCarUpSch.getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비 TYPE P:PDA					
					cmbnCarldyn 	= commUtils.trim(jrCarUpSch.getFieldString("CMBN_CARLD_YN")); 	     //복수동 조합상차유무(시작:S,종료:E,단일상차:N)
					ydCarWrkGp		= commUtils.trim(jrCarUpSch.getFieldString("YD_CAR_WRK_GP")); 	     //야드차량작업구분(1:내수/2:수출/3:연안해송/9:HYSCO스케줄)
					
					commUtils.printLog(logId," ★★★★★  권하차량스케쥴:" + ydCarSchId + " 차량상차완료(carLdCmplYn):" + carLdCmplYn+ " ★★★★", "SL");
					
					
					/**********************************************************
					* 4.2 상차 차량이송재료(TB_YD_CARFTMVMTL) 등록
					**********************************************************/
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
					recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
					recPara.setField("MODIFIER" 	 , modifier     ); 
					recPara.setField("YD_DN_WR_LOC"  , ydDnWrLoc   ); //야드권하실적위치
					commDao.update(recPara, insAxYML009CarMtlIns, logId, methodNm, " 상차 이송재료 등록 ");
					
					
					/**********************************************************
					* 4.3 크레인권하실적 상차 차량스케줄 수정
					*   -차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					**********************************************************/
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setResultCode(logId);	//Log ID
					recPara.setResultMsg(methodNm);	//Log Method Name
					
					if ("Y".equals(carLdCmplYn)) 
					{   //해당동만 완료이면 차량상태 완료처리함
						recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					} 
					else 
					{
						recPara.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}
					recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
					recPara.setField("YD_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
					recPara.setField("WR_DT" 			, currDt ); 
					recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
					recPara.setField("MODIFIER"         , modifier     ); 
					commDao.update(recPara, updAxYML009CarSchLd, logId, methodNm, " 상차 차량스케줄 수정 ");

					
					/**********************************************************
					* 4.4 검수등록 
					**********************************************************/
					if ("G".equals(ydCarUseGp)) 
					{
						// PIDEV						
//						if("Y".equals(sApplyYnPI)) {
							//출하차량중에서...
							if("HR".equals(coil_gp) && !"1".equals(ydCarWrkGp))	{
								//열연코일 + 내수가 아니면
								//열연코일 검수작업 확인
								recPara.setField("STL_NO" , StockId );
								commDao.update(recPara, insCarExaminationjlNEW_PIDEV, logId, methodNm, "검수등록 (열연)");
							} else if ("CR".equals(coil_gp)) {
								//냉연코일
								//냉연코일 검수작업 확인
								recPara.setField("STL_NO" , StockId );   	
								commDao.update(recPara, insCarExaminationjlNEWCR_PIDEV, logId, methodNm, "검수등록(냉연)");
							}
							
//						} else {
//							//출하차량중에서...
//							if("HR".equals(coil_gp) && !"1".equals(ydCarWrkGp)) {
//								//열연코일 + 내수가 아니면
//								//열연코일 검수작업 확인
//								recPara.setField("STL_NO" , StockId );   	
//								commDao.update(recPara, insCarExaminationjlNEW, logId, methodNm, "검수등록 (열연)");
//							} else if ("CR".equals(coil_gp)) {
//								//냉연코일
//								//냉연코일 검수작업 확인
//								recPara.setField("STL_NO" , StockId );   	
//								commDao.update(recPara, insCarExaminationjlNEWCR, logId, methodNm, "검수등록(냉연)");
//							}
//						}
					}
					
					
					/**********************************************************
					* 4.5 상차완료처리
					**********************************************************/
					if ("Y".equals(carLdCmplYn)) 
					{
						/**********************************************************
						* 5.5.1 구내운송 상차완료처리
						**********************************************************/
						if ("L".equals(ydCarUseGp)) 
						{
							/**********************************************************
							* - 소재차량상차완료 전문전송
							**********************************************************/
	    					jrYdMsg = JDTORecordFactory.getInstance().create();
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("YD_CAR_SCH_ID", ydCarSchId);
							
							if("HR".equals(coil_gp))
							{
								//열연COIL인경우 YDTSJ008
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDTSJ008", jrYdMsg));
								
								/**********************************************************
								* - 코일제품고간이송상하차완료 전문전송
								**********************************************************/
								if ("Y".equals(stlAppearGp)) 
								{
			    					jrYdMsg = JDTORecordFactory.getInstance().create();
									jrYdMsg.setResultCode(logId);	//Log ID
									jrYdMsg.setResultMsg(methodNm);	//Log Method Name
									jrYdMsg.setField("YD_CAR_SCH_ID"  , ydCarSchId );
									// PIDEV
//									if("Y".equals(sApplyYnPI)) {
										jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1121A", jrYdMsg));
//									} else {
//										jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR021", jrYdMsg));
//									}
								}
							}
							else
							{
								//냉연COIL인경우 YFTSJ008
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YFTSJ008", jrYdMsg));
							}
						}
						
						/**********************************************************
						* 4.5.2 PDA
						**********************************************************/
						if ("P".equals(equipType) ) 
						{
							recPara.setField("STL_NO" , StockId); 
							recPara.setField("YD_CAR_SCH_ID", ydCarSchId); 
							JDTORecordSet jsPofrto = commDao.select(recPara, getYmPoFrtoInfo, logId, methodNm, "임가공 여부 정보 ");
							
							/**********************************************************
							* - 임가공이 아닌 경우 코일제품이송상차완료 전송PDA
							**********************************************************/
							if (jsPofrto.size() == 0 ) 
							{
								jrYdMsg	= JDTORecordFactory.getInstance().create();
								jrYdMsg.setResultCode(logId);	//Log ID
								jrYdMsg.setResultMsg(methodNm);	//Log Method Name
								jrYdMsg.setField("YD_CAR_SCH_ID"  , ydCarSchId ); 		    					
								// PIDEV
//								if("Y".equals(sApplyYnPI)) {
									// 박판 복수동 이송 기능_PI0201 -->
									String YnPI0201 = commDao.ApplyYn("", "rcvF1YFL009 => 크레인 권하시", "PI0201", "*", "*");
									recPara.setField("YD_GP" , ydEqpId.substring(0, 1)); 
									if ("Y".equals(YnPI0201)) {
										if (!"S".equals(cmbnCarldyn)) {
											if("HR".equals(coil_gp)) {
												jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1091B", jrYdMsg));	 //코일이송 상차완료
											} else {
												jrYdMsg.setField("DIST_GOODS_GP", "L");
												jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1091C", jrYdMsg));	 //코일이송 상차완료
											}
										}
										
									} else {
										if("HR".equals(coil_gp)) {
											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1091B", jrYdMsg));	 //코일이송 상차완료
										} else {
											jrYdMsg.setField("DIST_GOODS_GP", "L");
											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1091C", jrYdMsg));	 //코일이송 상차완료
										}
									}
									// 박판 복수동 이송 기능_PI0201 <--
//								} else {
//									jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR073", jrYdMsg));	 //코일이송 상차완료	
//								}
							}
							 
							/**********************************************************
							* - 진행관리 냉연코일이송진행 상태실적
							**********************************************************/
							JDTORecordSet jsCarMtl = commDao.select(recPara, getYdCarYdStockYdCarFtmvMtlMES, logId, methodNm, "차량재료 정보 정보 ");
							
							
							/***********************************************************
							* - 냉연코일이송진행 상태실적 전송...2021.03.17 정종균책임 요청으로 주석처리함
							**********************************************************/
//							if (jsCarMtl.size() > 0 ) 
//							{
//								JDTORecord jrCarMtl       = null;
//								JDTORecord recInTemp      = null;
//								for(int nIdx=0; nIdx < jsCarMtl.size(); nIdx++) {
//									jrCarMtl = jsCarMtl.getRecord(nIdx);
//														        
//					    			jrYdMsg			= JDTORecordFactory.getInstance().create();
//			    					jrYdMsg = JDTORecordFactory.getInstance().create();
//									jrYdMsg.setResultCode(logId);	//Log ID
//									jrYdMsg.setResultMsg(methodNm);	//Log Method Name
//									jrYdMsg.setField("STL_NO", commUtils.trim(jrCarMtl.getFieldString("STL_NO"))); 
//									jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDPTJ006", jrYdMsg));	//냉연코일이송진행 상태실적
//							        
//								}
//							}
						}
						
						
						/**********************************************************
						* 4.5.3 출하차량(G)
						**********************************************************/
						if ("G".equals(ydCarUseGp)) 
						{
							recPara.setField("STL_NO",			StockId); 
							recPara.setField("YD_CAR_SCH_ID",	ydCarSchId); 
							recPara.setField("YD_GP",			"1");
							
							if("HR".equals(coil_gp))
							{
								//열연코일일때
								/**********************************************************
								* -임가공 여부 정보
								**********************************************************/
								JDTORecordSet jsPofrto = commDao.select(recPara, getYmPoFrtoInfo, logId, methodNm, "임가공 여부 정보 "); 
								
								
								/**********************************************************
								* -임가공 인 경우
								**********************************************************/
								if (jsPofrto.size() > 0 ) 
								{
									/**********************************************************
									* -복수상차 처리
									**********************************************************/
									if ("S".equals(cmbnCarldyn)) 
									{
										EJBConnector ejbConn1 = new EJBConnector("default","YfCommCarMvSeEJB",this); //오타 있음..kbs
										JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procCmbnCarldYn",
																	new  Class[]{JDTORecord.class },
																	new Object[]{recPara});
										jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
										CmbnCarldSndYn = "Y";
									} 
									/**********************************************************
									* -임가공이송상차완료 송신
									**********************************************************/
									else
									{
										// PIDEV
//										if("Y".equals(sApplyYnPI)) {
											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1095", recPara));
//										} else {
//											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR022", recPara));
//										}
//										ydCarAutoStart = "Y";
									}
								}
								
								
								/**********************************************************
								* -임가공이 아닌 경우(일반출하)
								**********************************************************/
								else 
								{
									if ( !"P".equals(equipType) ) 
									{					//출하PDA
										/**********************************************************
										* -복수상차 처리
										**********************************************************/
										if ("S".equals(cmbnCarldyn)) 
										{
											EJBConnector ejbConn1 = new EJBConnector("default","YfCommCarMvSeEJB",this);
											JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procCmbnCarldYn",
																		new  Class[]{JDTORecord.class },
																		new Object[]{recPara});
											jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
											CmbnCarldSndYn = "Y";
										} 
										/**********************************************************
										* -코일출하차량상차완료 송신
										**********************************************************/
										else 
										{	
											// PIDEV
//											if("Y".equals(sApplyYnPI)) {
												jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1091A", recPara));
//											} else {
//												jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR015", recPara));	
//											}
										}
									}
									// 이송 인 경우
									else {
										// 박판 복수동 이송 기능_PI0201 -->
										String YnPI0201 = commDao.ApplyYn("", "rcvF1YFL009 => 크레인 권하시", "PI0201", "*", "*");
										if ("Y".equals(YnPI0201)) {
											/**********************************************************
											* -복수상차 처리
											**********************************************************/
											if ("S".equals(cmbnCarldyn)) 
											{
												EJBConnector ejbConn1 = new EJBConnector("default","YfCommCarMvSeEJB",this);
												JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procCmbnCarldYn",
																			new  Class[]{JDTORecord.class },
																			new Object[]{recPara});
												jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
												CmbnCarldSndYn = "Y";
											}
										}
										// 박판 복수동 이송 기능_PI0201 <--
									}
								}
							}
							else
							{
								//냉연코일일때
								/**********************************************************
								* -복수상차 처리
								**********************************************************/
								if ("S".equals(cmbnCarldyn)) 
								{
									EJBConnector ejbConn1 = new EJBConnector("default","YfCommCarMvSeEJB",this);
									JDTORecord jrRtn1 = (JDTORecord)ejbConn1.trx("procCmbnCarldYn",
																new  Class[]{JDTORecord.class },
																new Object[]{recPara});
									jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
									CmbnCarldSndYn = "Y";
								} 
								/**********************************************************
								* -코일출하차량상차완료 송신
								**********************************************************/
								else 
								{	
									// PIDEV
//									if("Y".equals(sApplyYnPI)) {
										if ( !"P".equals(equipType)) { 
											recPara.setField("DIST_GOODS_GP", "L");
											jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1091C", recPara));
										}
//									} else {
//										jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("CRDMJ008", recPara));
//									}
								}
							}
						}
					}
				}
			}
			
			
			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 5.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 5.4 구내운송 소재차량하차완료 송신(YSTSJ010) 전송
			* 5.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 송신)
			String carUdCmplYn = "N";
			if ("PT".equals(ydUpWrLoc.substring(2, 4)) 
					&& !"PT".equals(ydDnWrLoc.substring(2, 4)) 
					&& !"PT3".equals(ydSchCd.substring(2, 5))) // 차량동간이적 skip	
			{
				
				/**********************************************************
				* 5.1 야드하차작업예약ID 차량하차스케줄 정보 조회
				**********************************************************/
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP"  , ydUpWrLoc.substring(0, 6) ); 
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId);
				JDTORecordSet jsCarDnSch = commDao.select(recPara, getAxYDL009CarSchUd, logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsCarDnSch.size() <1) 
				{
					commUtils.printLog(logId, methodNm + "오류:야드하차작업예약ID 차량하차스케줄 DB정보 없음", "SL");
				}
				else
				{
					JDTORecord jrCarDnSch = jsCarDnSch.getRecord(0);
					
					CarNo 			= commUtils.trim(jrCarDnSch.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrCarDnSch.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrCarDnSch.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrCarDnSch.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrCarDnSch.getFieldString("YD_PNT_CD")); 					
					ydCarSchId  	= commUtils.trim(jrCarDnSch.getFieldString("YD_CAR_SCH_ID"));
					carUdCmplYn 	= commUtils.trim(jrCarDnSch.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					equipType 		= commUtils.trim(jrCarDnSch.getFieldString("TRANS_EQUIPMENT_TYPE")); //운송장비 TYPE P:PDA
					ydCarUseGp 		= commUtils.trim(jrCarDnSch.getFieldString("YD_CAR_USE_GP")); 		//야드차량사용구분
					
					commUtils.printLog(logId, " ★★★★★  권하차량스케쥴:" + ydCarSchId + ",차량하차완료여부(carUdCmplYn):" + carUdCmplYn + " ★★★★", "SL");
					
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrCarDnSch.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					
					
					/**********************************************************
					* 5.2 코일일품출하상차실적 전송
					**********************************************************/
					if ( "P".equals(equipType) ) 
					{						
    					jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						jrYdMsg.setField("STL_NO",			StockId);
						jrYdMsg.setField("YD_CAR_SCH_ID",	ydCarSchId);
						
						// PIDEV
//						if("Y".equals(sApplyYnPI)) {
							
							String goodsEa = "";
							if ("Y".equals(carUdCmplYn)) {
								goodsEa = "*";
							} 
							else {
								goodsEa = "1";
							}
							jrYdMsg.setField("GOODS_EA",		goodsEa);
							
							if("HR".equals(coil_gp)) {
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1081B", jrYdMsg));
							} else {
								jrYdMsg.setField("DIST_GOODS_GP", 	"L"); 
								jrYdMsg.setField("SCH_YN", 			"Y");	// equipType = P 인 경우 Y
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1081C", jrYdMsg));
							}
//						} else {
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR072", jrYdMsg));	
//						}
					}
					
					/**********************************************************
					* 5.3 구내이송 소재이송이면 이송완료처리 + 열연코일인경우만
					**********************************************************/
					if ( ydCarUseGp.equals("L") && !stlAppearGp.equals("Y") && "HR".equals(coil_gp) ) 
					{
    					jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						jrYdMsg.setField("STL_NO",			StockId);
						EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
						JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procFtmvCmtl", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
					}
					
					/**********************************************************
					* 5.3 구내이송 소재이송이면 이송완료처리 + 냉연코일인경우만__ 추가
					**********************************************************/					
					String sPI0004_YN = yfComm.ACoilApplyYn("PI0004","1","1");
					if ("Y".equals(sPI0004_YN)) 
					{	
						if ( ydCarUseGp.equals("L") && !stlAppearGp.equals("Y") && "CR".equals(coil_gp) ) 
						{
	    					jrYdMsg = JDTORecordFactory.getInstance().create();
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("STL_NO",			StockId);
							EJBConnector ejbConn = new EJBConnector("default", "YfCommSeEJB", this);
							JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procFtmvCmtlCR", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
							jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
						}
					}
					
					/**********************************************************
					* 5.4 하차 차량스케줄 수정
					*    -하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정   
					**********************************************************/
					//차량하차 완료 실적은 권상실적시 처리 되는데... 중복아닌지? kbs
					if ("Y".equals(carUdCmplYn)) 
					{
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("MODIFIER" 		, modifier     ); 
						recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
						recPara.setField("WR_DT" 			, currDt ); 

						commDao.update(recPara, updAxYDL009CarSchUd, logId, methodNm, "하차 차량스케줄 수정  ");
						
						int intTransOrdSeqNo = StringHelper.parseInt(TransOrdSeqNo, 0);
						
						if (intTransOrdSeqNo > 999000 ) 
						{
							ydCarAutoStart = "Y";  // 반입 차량은 자동출발
						}
					}
				} 
			}
		
			commUtils.printLog(logId, " CmbnCarldSndYn:" + CmbnCarldSndYn, "SL");
			
			/**********************************************************
			* 6. 설비(야드설비상태, 권하완료(D)) 수정
			**********************************************************/
			commDao.update(jrParam, updStatEqp2, logId, methodNm, "설비상태 수정");
			
			
			/**********************************************************
			* 7. 적치단 Clear.
			* - clear 되는 적치단은 크레인임.
			**********************************************************/
			commDao.update(jrParam, clrUpDnWrkMtl, logId, methodNm, "적치단 작업 크레인 및권상위치 Clear ");
			
			
			/**********************************************************
			* 8. 적치단수정
			*   -복수동완료 처리는 위에서 처리 됨 , 설비보급은 적치단 수정 생략
			**********************************************************/
			if ("N".equals(CmbnCarldSndYn)) 
			{
				commDao.update(jrParam, updA7YDL009StkLyr, logId, methodNm, "적치단수정");
			}	
			
			
			/**********************************************************
			* 9. 크레인작업재료 삭제
			**********************************************************/
			commDao.update(jrParam, updA7YDL009CrnMtl, logId, methodNm, "크레인작업재료 삭제");
			
			
			/**********************************************************
			* 10. 크레인스케쥴 권하실적 수정 및 삭제
			**********************************************************/
			jrParam.setField("YD_DN_WRK_MODE2"        , ydEqpWrkMode2 ); //모드 추가
			commDao.update(jrParam, updAxYDL009CrnSch, logId, methodNm, "크레인스케쥴 권하실적 수정");
			
			
			/**********************************************************
			* 11. 작업예약, 작업예약재료 삭제
			**********************************************************/
			//작업예약재료 삭제
			commDao.update(jrParam, updAxYDL009WbMtlDel, logId, methodNm, "작업예약재료 삭제");
			//작업예약 수정 및 삭제
			commDao.update(jrParam, updAxYDL009WbDel, logId, methodNm, "작업예약 수정 및 삭제");
			
			
			/**********************************************************
			* 12. 입고작업실적 송신
			*   - 입고 스케줄이고 권하위치가 야드일때..
			*   - 진도코드 판단은 쿼리에서 한다.
			**********************************************************/
			if (("L".equals(ydSchCd.substring(6, 7)) || "M".equals(ydSchCd.substring(6, 7))) 
					   && ydDnWrLoc.matches("[1][A-H]\\d\\d\\d\\d\\d\\d")) {
				// PIDEV
//				if("Y".equals(sApplyYnPI)) {
					//진도코드가 입고대기(H), 종합판정대기(G) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1011", jrParam));					
//				} else {
//					//진도코드가 입고대기(H), 종합판정대기(G) 인 경우 (진도코드 판단은 쿼리에서 한다.)
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR001", jrParam));
//				}
			}
			
			
			
			/**********************************************************
			* 12. HFL결속장 보급실적 송신 - YMPOJ161
			**********************************************************/
			if("HS".equals(ydDnWrLoc.substring(2, 4))){
				JDTORecordSet jsYMPOJ161 = commDao.select(jrParam, TcYMPO161, logId, methodNm, "설비위 권하처리 조회 "); 
    			
    			if ( jsYMPOJ161.size() <= 0 ) 
				{
					commUtils.printLog(logId, methodNm+  "보급실적 송신 대상 스케줄이 없음:" + StockId, "SL");
				}
    			else 
				{
					JDTORecord jrYMPOJ161 = JDTORecordFactory.getInstance().create();
    				jsYMPOJ161.first();
    				jrYMPOJ161.setRecord(jsYMPOJ161.getRecord());
					JDTORecord jrSnd = JDTORecordFactory.getInstance().create();
					jrSnd.setField("JMS_TC_CD"			, commUtils.trim(jrYMPOJ161.getFieldString("JMS_TC_CD")));
					jrSnd.setField("JMS_TC_CREATE_DDTT"	, commUtils.trim(jrYMPOJ161.getFieldString("JMS_TC_CREATE_DDTT")));
					jrSnd.setField("tcCode"				, commUtils.trim(jrYMPOJ161.getFieldString("TCCODE")));
					jrSnd.setField("tcDate"				, commUtils.trim(jrYMPOJ161.getFieldString("TCDATE")));
					jrSnd.setField("tcTime"				, commUtils.trim(jrYMPOJ161.getFieldString("TCTIME")));
					jrSnd.setField("plantGbn"			, commUtils.trim(jrYMPOJ161.getFieldString("PLANTGBN")));
					jrSnd.setField("procGbn"			, commUtils.trim(jrYMPOJ161.getFieldString("PROCGBN")));
					jrSnd.setField("coilNo"				, commUtils.trim(jrYMPOJ161.getFieldString("COILNO")));
					jrSnd.setField("processId"			, commUtils.trim(jrYMPOJ161.getFieldString("PROCESSID")));
					jrSnd.setField("downDate"			, commUtils.trim(jrYMPOJ161.getFieldString("DOWNDATE")));
					jrSnd.setField("downTime"			, commUtils.trim(jrYMPOJ161.getFieldString("DOWNTIME")));
					jrSnd.setField("positionNo"			, commUtils.trim(jrYMPOJ161.getFieldString("POSITIONNO")));
					jrRtn = commUtils.addSndData(jrRtn, jrSnd);			
					
				}
			}
			
			
			/**********************************************************
			* 13. 출하관리 코일제품이적작업실적 전송  - YDDMR004
			**********************************************************/
//			if (YfConstant.CURR_PROG_CD_COIL_K.equals(sProgCd) ||
//					YfConstant.CURR_PROG_CD_COIL_P.equals(sProgCd) ||
//					YfConstant.CURR_PROG_CD_COIL_Z.equals(sProgCd) ||
//					YfConstant.CURR_PROG_CD_COIL_J.equals(sProgCd) ||
//					YfConstant.CURR_PROG_CD_COIL_L.equals(sProgCd) ||
//					YfConstant.CURR_PROG_CD_COIL_X.equals(sProgCd) ||
//					YfConstant.CURR_PROG_CD_COIL_M.equals(sProgCd)) 
//			{
//				2020.11.21 제품의 경우 이동실적을 모두 출하에게 전송해야함
				// 동내이적, 차량동간이적, 대차동간이적
//	        	if ( 
//	        			("YD".equals(ydSchCd.substring(2, 4)) && ydSchCd.substring(6).equals("MM"))				/* 일반야드 이적 */
//	        		||( "PT3".equals(ydSchCd.substring(2, 5)) && ydSchCd.substring(6).equals("LM"))
//	        	   ) 
//	        	{
					if("HR".equals(coil_gp))
					{
						jrParam.setField("STL_NO"	, StockId);
						// PIDEV						
//						if("Y".equals(sApplyYnPI)) {
							if( !"PT".equals(ydDnWrLoc.substring(2, 4))) {
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1031", jrParam));
							}
//						} else {						
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YDDMR004", jrParam));
//						}
					}
//		        }
//			}	
			
			
			/**********************************************************
			* 14. 상하차 완료 차량출발 처리
			**********************************************************/
			if ("Y".equals(ydCarAutoStart)) 
			{  
				commUtils.printLog(logId, " 차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
				JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
				recInTemp.setResultCode(logId);		//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("CAR_NO", 				CarNo);			
				recInTemp.setField("SPOS_WLOC_CD", 			WlocCd);
				recInTemp.setField("SPOS_YD_PNT_CD", 		ydPndCd);
				recInTemp.setField("TRANS_ORD_DATE", 		TransOrdDate);
				recInTemp.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
				
				EJBConnector ejbConn = new EJBConnector("default", "YfCommCarMvSeEJB", this);
				JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
				
				jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
			}
			
			/**********************************************************
			* 15. 코일공통 수정 : 별도 트렌젝션 처리
			*    -공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			*    -냉연코일은 냉연MES로 YFCRJ002(저장품위치정보)생성 및 TB_YF_CR_COILCOMM 위치 업데이트
			**********************************************************/
			jrParam	= JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);		//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"      , modifier    ); //권하 코일번호
			jrParam.setField("STL_NO"        , StockId     ); //권하 코일번호
			jrParam.setField("YD_DN_WR_LOC"  , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LYR"  , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("YD_LOC"        , ydDnWrLoc+ydDnWrLayer   ); //야드권하실적위치
			
			if("HR".equals(coil_gp))
			{
				//열연코일...TB_PT_COILCOMM 위치정보 수정
				EJBConnector ejbConn1 = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
				ejbConn1.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			}
			else
			{
				//냉연코일...YFCRJ002(저장품위치정보) 생성 및 TB_YF_CR_COILCOMM 위치 업데이트
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YFCRJ002" , jrParam));
				commDao.update(jrParam, updYfCrCoilCommLodLoc, logId, methodNm, "TB_YF_CR_COILCOMM 냉연코일 위치수정");
			}
			
			
			/**********************************************************
			* 16. 작업실적 Table에 권하실적을 Update
			*    -작업이력 등록
			**********************************************************/
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId); 
			jrParam.setField("YD_UP_CMPL_DT"   , currDt    );
			jrParam.setField("REGISTER"        , modifier  );
			if (StockId.startsWith("S")) 
			{ //스크랩은 coilcomm이 없으므로 별도 이력처리 
				// PIDEV
//				if ("Y".equals(sApplyYnPI)) {
					commDao.insert(jrParam, insWrkHistScrap_PIDEV, logId, methodNm, "작업이력 등록");
//				} else {
//					commDao.insert(jrParam, insWrkHistScrap, logId, methodNm, "작업이력 등록");
//				}
			}
			else
			{
				// PIDEV
//				if ("Y".equals(sApplyYnPI)) {
					commDao.insert(jrParam, insWrkHist_PIDEV, logId, methodNm, "작업이력 등록");
//				} else {
//					commDao.insert(jrParam, insWrkHist, logId, methodNm, "작업이력 등록");
//				}
			}
			
			
			/*************************************************************
			 * 17. 권하시 해당저장품 상차예정차량 입동 처리
			 *************************************************************/
			JDTORecordSet rst = commDao.select(jrParam, getInWoOrdReq, logId, methodNm, "입동 정보 조회");
			if (rst.size() > 0) 
			{
				commUtils.printLog(logId, "YFYFJ662 전문 세팅 START", "[INFO]");
				JDTORecord recInTemp = JDTORecordFactory.getInstance().create();	
				recInTemp.setResultCode(logId);	//Log ID
				recInTemp.setResultMsg(methodNm);	//Log Method Name
				recInTemp.setField("JMS_TC_CD"			, "YFYFJ662");          //차량입동지시 요구 기존:YDYDJ662
				recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
				recInTemp.setField("YD_CARPNT_CD"		, commUtils.trim(rst.getRecord(0).getFieldString("YD_CARPNT_CD")));
				recInTemp.setField("YD_CAR_SCH_ID"		, commUtils.trim(rst.getRecord(0).getFieldString("YD_CAR_SCH_ID")));
				recInTemp.setField("CAR_NO"             , commUtils.trim(rst.getRecord(0).getFieldString("CAR_NO")));
				recInTemp.setField("CHK_YN"				, "N");
				recInTemp.setField("CR_FRTOMOVE_GP"		, commUtils.trim(rst.getRecord(0).getFieldString("CR_FRTOMOVE_GP"))); //냉연이송구분
					
				jrRtn = commUtils.addSndData(jrRtn, recInTemp);
				commUtils.printLog(logId, "YFYFJ662 전문 세팅 END", "[INFO]");
			}
			
			
			/**********************************************************
			* 18. 강제권하시 저장품제원 전문 전송(YFF1L002)
			**********************************************************/
			if ("5".equals(ydWrkProgStat)) 
			{
				jrParam.setField("YD_INFO_SYNC_CD", "5"); 
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L002DnWr", jrParam));
			}
			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치

			//크레인작업실적응답 전송
			if (resYn) 
			{
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(resMsg));
			}
			
			
			/**********************************************************
			* 19. 차량이적 별도
			**********************************************************/
			String sAPP200_YN = yfComm.ACoilApplyYn("APP200","1","1");
			if ("Y".equals(sAPP200_YN)) 
			{
				//주작업인 경우에만 작업 한다.(to위치가 차량위치가 아니고 차량이적상차 작업 인 경우)
				if ("PT3".equals(ydSchCd.substring(2, 5))) 
				{
					String SndYn = "N";
					
					if ("U".equals(ydSchCd.substring(6, 7)) && "PT".equals(ydDnWrLoc.substring(2, 4))) 
					{
						SndYn = "Y";
					}
					if ("L".equals(ydSchCd.substring(6, 7)) && !"PT".equals(ydDnWrLoc.substring(2, 4))) 
					{
						SndYn = "Y";
					}
					
					if ("Y".equals(SndYn)) 
					{
						JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
						recInTemp1.setResultCode(logId);	//Log ID
						recInTemp1.setResultMsg(methodNm);	//Log Method Name
						recInTemp1.setField("YD_SCH_CD"		    , ydSchCd);
						recInTemp1.setField("STL_NO"       		, StockId);
						recInTemp1.setField("MODIFIER"    		, modifier);
						recInTemp1.setField("YD_UP_WR_LOC"      , ydUpWrLoc);
						recInTemp1.setField("YD_DN_WR_LOC"      , ydDnWrLoc);
						recInTemp1.setField("YD_CRN_SCH_ID"     , ydCrnSchId);
						recInTemp1.setField("YD_EQP_ID"         , ydEqpId);
		
						EJBConnector ejbCon = new EJBConnector("default", "YfCommCarMvSeEJB", this);
						JDTORecord jrRtn1 = (JDTORecord)ejbCon.trx("procTraillerMoveSch", new Class[] { JDTORecord.class }, new Object[] { recInTemp1 });
						jrRtn = commUtils.addSndData(jrRtn, jrRtn1);
					}
				}
			}
			
			
			/**********************************************************
			* 20. 크레인작업지시요구 전문 호출(F1YFL007)
			* 스케줄코드, 스케줄id를 setting해서 크레인스케줄 호출해도 되는지 확인할것..kbs
			**********************************************************/
			jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("JMS_TC_CD"		, "F1YFL007"); //JMSTC코드 
			jrYdMsg.setField("YD_EQP_ID"       	, ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT"	, "4"       ); //야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       	, ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   	, ydCrnSchId); //야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        	, modifier  ); //수정자
			
			//크레인작업지시 요구을 추가
			jrRtn = commUtils.addSndData(jrRtn, this.rcvF1YFL007(jrYdMsg));
			
			/**********************************************************
			* 21. HFL결속장 크레인스케줄 기동 / 크레인스케줄 전문 호출
			* 2025.07.10 
			**********************************************************/
			JDTORecordSet jrWBInfo_1GHS = commDao.select(jrParam, getYdWrkbookHFLChk, logId, methodNm, "HFL결속장 작업예약 조회");
			if ( jrWBInfo_1GHS.size() > 0) {
				String ydWbookId_1GHS = jrWBInfo_1GHS.getRecord(0).getFieldString("YD_WBOOK_ID");
				String ydSchCd_1GHS = jrWBInfo_1GHS.getRecord(0).getFieldString("YD_SCH_CD");
				JDTORecord jrYdMsg_1GHS = JDTORecordFactory.getInstance().create();
				jrYdMsg_1GHS.setResultCode(logId);	//Log ID
				jrYdMsg_1GHS.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg_1GHS.setField("YD_WBOOK_ID",		ydWbookId_1GHS);	//야드작업예약ID
				jrYdMsg_1GHS.setField("YD_SCH_CD",		ydSchCd_1GHS);	//야드스케쥴코드
				jrYdMsg_1GHS.setField("YD_SCH_ST_GP",	"O");		//야드스케쥴기동구분
				jrYdMsg_1GHS.setField("YD_SCH_REQ_GP",	"L");		//야드스케쥴요청구분(인출)
				jrYdMsg_1GHS.setField("MODIFIER",		modifier);	//수정자

				jrRtn = commUtils.addSndData(jrRtn, yfComm.getCrnSchMsg(jrYdMsg_1GHS));
			}			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
//					resMsg.setField("YD_L3_HD_RS_CD", "DN04"); //야드L3처리결과코드
//					resMsg.setField("YD_L3_MSG"     , e.getMessage()   ); //야드L3MESSAGE
					
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YfCommSeEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { yfComm.getYFF1L005(resMsg) });
					throw e;
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 강제권하가능유무체크
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord conpulDown(JDTORecord rcvMsg) throws DAOException 
	{

		String methodNm 	= "강제권하가능유무체크[ACoilRcvL2SeEJB.conpulDown] < " + rcvMsg.getResultMsg();
		String logId 		= rcvMsg.getResultCode();
		JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= true;	//크레인작업실적응답 전문 전송여
		JDTORecord jrRtn 	= null;
			
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YD_DN_WR_LYR"  )); //야드권하실적단
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축	
			
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "D"); //야드L2실적구분 (권하실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)
			
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_XAXIS"	, ydCrnXaxis); //야드크레인스케쥴ID
			jrParam.setField("YD_CRN_YAXIS" , ydCrnYaxis); //수정자			
			jrParam.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			jrParam.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			jrParam.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			jrParam.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc    ); //권하지시위치-논리좌표(8자리)
			jrParam.setField("YD_STK_LYR_NO"  , ydDnWrLayer  ); //권하지시단  -논리좌표(3자리)
			
			String sSTL_NO					= "";
			String sSTK_ABLE_YN             = "";
			String sYD_STK_LYR_X_AXIS		= "";
			String sYD_STK_LYR_Y_AXIS		= "";
			String sYD_STK_LYR_Z_AXIS		= "";
			String sYD_STK_LYR_STAT			= "";
			String sYD_STK_LYR_ACTIVE_STAT	= "";
			String sSTL_NO_01_CURR			= "";
			String sSTL_NO_01_NEXT			= "";
			String sLYR_STAT_01_CURR		= "";
			String sLYR_STAT_01_NEXT        = "";       
			
			String sWR_STACK_LOC_GP   = ""; //강제권하 실적을 담기위한 변수
			String sWR_YD_STK_LYR_NO = "";
			
			resMsg.setField("YD_L2_WR_GP"   , "F"); //야드L2실적구분 (강제권하)
			
			/**************************
			 * 1.1 L2 수신 논리좌표 존재
			 **************************/
			if (!"".equals(ydDnWrLoc)) 
			{ 
				JDTORecordSet jsCrnLoc = null;
				sWR_STACK_LOC_GP   = ydDnWrLoc;
				sWR_YD_STK_LYR_NO = ydDnWrLayer;
				
				if ("001".equals(ydDnWrLayer)) // 1단인 경우
				{ 
					jsCrnLoc = commDao.select(jrParam, getYdStklyrAddress, logId, methodNm, "좌표에 해당하는 논리 값 READ 조회");
					commUtils.printLog(logId, "F1YFL009 강제권하:["+ydDnWrLayer+"]"+" 논리좌표["+ydDnWrLoc+"]", "[info]");
					
				} 
				else if ("002".equals(ydDnWrLayer)) // 2단 일경우
				{ 
					jsCrnLoc = commDao.select(jrParam, getYdStklyrAddressLyr2, logId, methodNm, "좌표에 해당하는 논리 값 READ 조회");
					commUtils.printLog(logId, "F1YFL009 강제권하:["+ydDnWrLayer+"]"+" 논리좌표["+ydDnWrLoc+"]", "[info]");
					
				} 
				else 
				{
					resMsg.setField("YD_L3_HD_RS_CD", "E000");
					resMsg.setField("YD_L3_MSG"     , "단 정보 이상["+ydDnWrLayer+"]");
					commUtils.printLog(logId, "F1YFL009 강제권하:["+ydDnWrLayer+"]"+" 논리좌표["+ydDnWrLoc+"]", "[info]");
				}

				
				if (jsCrnLoc.size() == 1) 
				{
					
					sSTL_NO           			= jsCrnLoc.getRecord(0).getFieldString("STL_NO");
					sSTK_ABLE_YN        		= jsCrnLoc.getRecord(0).getFieldString("STK_ABLE_YN");
					sYD_STK_LYR_X_AXIS 			= jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_X_AXIS");
					sYD_STK_LYR_Y_AXIS 			= jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_Y_AXIS");
					sYD_STK_LYR_Z_AXIS			= jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_Z_AXIS");
					sYD_STK_LYR_STAT   			= jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_STAT");
					sYD_STK_LYR_ACTIVE_STAT 	= jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_ACTIVE_STAT");
					
					sSTL_NO_01_CURR   	= commUtils.nvl(jsCrnLoc.getRecord(0).getFieldString("STL_NO_01_CURR"), "");
					sSTL_NO_01_NEXT   	= commUtils.nvl(jsCrnLoc.getRecord(0).getFieldString("STL_NO_01_NEXT"), "");
					sLYR_STAT_01_CURR   = commUtils.nvl(jsCrnLoc.getRecord(0).getFieldString("LYR_STAT_01_CURR"), "");
					sLYR_STAT_01_NEXT   = commUtils.nvl(jsCrnLoc.getRecord(0).getFieldString("LYR_STAT_01_NEXT"), "");
					
					
					// 해당 위치에 저장품 미존재 
					if ("".equals(sSTL_NO) && "Y".equals(sSTK_ABLE_YN)) 
					{
						
						resMsg.setField("YD_L3_HD_RS_CD", "0000");
						resMsg.setField("YD_L3_MSG"     , ydDnWrLoc+ydDnWrLayer+sYD_STK_LYR_X_AXIS+sYD_STK_LYR_Y_AXIS+sYD_STK_LYR_Z_AXIS); 
						
					} 
					else 
					{
						
						resMsg.setField("YD_L3_HD_RS_CD", "E000");
					
						// 1단 적치불가 사유
						if ("001".equals(ydDnWrLayer)) 
						{
							
							if ("N".equals(sYD_STK_LYR_ACTIVE_STAT)) 
							{
								resMsg.setField("YD_L3_MSG" , "논리1단(적치불가상태)["+ydDnWrLoc+ydDnWrLayer+"]");
							} 
							else 
							{
								resMsg.setField("YD_L3_MSG" , "논리1단적치불가["+sSTL_NO +"("+ sYD_STK_LYR_STAT=="C"?"적치중":"작업예약"+ ")" +"]");
							}
						}
						
						// 2단 적치불가 사유
						if ("002".equals(ydDnWrLayer)) 
						{
							if ("N".equals(sYD_STK_LYR_ACTIVE_STAT)) 
							{
								resMsg.setField("YD_L3_MSG" , "논리2단(사용불가)["+ydDnWrLoc+ydDnWrLayer+"]");
							} 
							else if ("D".equals(sYD_STK_LYR_STAT)) 
							{
								resMsg.setField("YD_L3_MSG" , "논리2단(작업예약존재)["+sSTL_NO+" "+ydDnWrLoc+ydDnWrLayer+"]");
							} 
							else if ("".equals(sSTL_NO_01_CURR) || "".equals(sSTL_NO_01_NEXT)) 
							{
								resMsg.setField("YD_L3_MSG" , "논리2단불가 해당위치 1단저장품 미존재");
							} 
							else if (!"C".equals(sLYR_STAT_01_CURR) || !"C".equals(sLYR_STAT_01_NEXT)) 
							{
								resMsg.setField("YD_L3_MSG" , "논리2단불가 1단저장품 작업예약 존재");
							}
						}
						
					}
				} 
				else 
				{ //논리좌표 조회	
					resMsg.setField("YD_L3_HD_RS_CD", "E002");
					resMsg.setField("YD_L3_MSG"     , "논리좌표 적치불가["+ydDnWrLoc+ydDnWrLayer+"]"); //해당 물리좌표 적치불가시 수신받은 물리좌표 리턴
				}
				
			} 
			else 
			{ 
			/******************************
			 * 1.2 L2 수신 논리좌표 미존재
			 ******************************/   
				JDTORecordSet jsCrnLoc = commDao.select(jrParam, getYdStklyrXYZA7YML009, logId, methodNm, "좌표에 해당하는 논리 값 READ 조회");
				
				if (jsCrnLoc.size() == 1) 
				{
					JDTORecord rst = jsCrnLoc.getRecord(0);
					ydDnWrLoc     = commUtils.trim(rst.getFieldString("YD_DN_WR_LOC"  )); //야드권하실적위치
					ydDnWrLayer   = commUtils.trim(rst.getFieldString("YD_DN_WR_LYR")); //야드권하실적단
					
					sWR_STACK_LOC_GP   = ydDnWrLoc;
					sWR_YD_STK_LYR_NO = ydDnWrLayer;
					
					if ("001".equals(ydDnWrLayer)) 
					{
						resMsg.setField("YD_L3_HD_RS_CD", "0000");
						resMsg.setField("YD_L3_MSG"     , ydDnWrLoc+ydDnWrLayer+ydCrnXaxis+ydCrnYaxis+ydCrnZaxis); //해당 물리좌표 적치가능시 논리좌표 리턴
						
					} 
					else 
					{
						jrParam.setField("YD_DN_WR_LOC"  , ydDnWrLoc);
						jrParam.setField("YD_STK_LYR_NO", ydDnWrLayer);
						jsCrnLoc = commDao.select(jrParam, getYdStklyrAddressLyr2, logId, methodNm, "좌표에 해당하는 논리 값 READ 조회");							
						
						if (jsCrnLoc.size() == 1) 
						{
							
							sSTL_NO					= jsCrnLoc.getRecord(0).getFieldString("STL_NO");
							sSTK_ABLE_YN        	= jsCrnLoc.getRecord(0).getFieldString("STK_ABLE_YN");
							sYD_STK_LYR_X_AXIS		= jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_X_AXIS");
							sYD_STK_LYR_Y_AXIS		= jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_Y_AXIS");
							sYD_STK_LYR_STAT		= jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_STAT");
							sYD_STK_LYR_ACTIVE_STAT = jsCrnLoc.getRecord(0).getFieldString("YD_STK_LYR_ACTIVE_STAT");
							
							sSTL_NO_01_CURR   	= jsCrnLoc.getRecord(0).getFieldString("STL_NO_01_CURR");
							sSTL_NO_01_NEXT   	= jsCrnLoc.getRecord(0).getFieldString("STL_NO_01_NEXT");
							sLYR_STAT_01_CURR   = jsCrnLoc.getRecord(0).getFieldString("LYR_STAT_01_CURR");
							sLYR_STAT_01_NEXT   = jsCrnLoc.getRecord(0).getFieldString("LYR_STAT_01_NEXT");
							
							
							// 해당 위치에 저장품 미존재 
							if ("".equals(sSTL_NO) && "Y".equals(sSTK_ABLE_YN)) 
							{
								
								resMsg.setField("YD_L3_HD_RS_CD", "0000");
								resMsg.setField("YD_L3_MSG"     , ydDnWrLoc+ydDnWrLayer+sYD_STK_LYR_X_AXIS+sYD_STK_LYR_Y_AXIS+"00000"); 
								
							} 
							else 
							{
								
								resMsg.setField("YD_L3_HD_RS_CD", "E000");
							
								// 1단 적치불가 사유
								if ("001".equals(ydDnWrLayer)) 
								{
									
									if ("N".equals(sYD_STK_LYR_ACTIVE_STAT)) 
									{
										resMsg.setField("YD_L3_MSG" , "물리1단(적치불가상태)["+ydDnWrLoc+ydDnWrLayer+"]");
									} 
									else 
									{
										resMsg.setField("YD_L3_MSG" , "물리1단적치불가["+sSTL_NO +"("+ sYD_STK_LYR_STAT=="C"?"적치중":"작업예약"+ ")" +"]");
									}
								}
								
								// 2단 적치불가 사유
								if ("002".equals(ydDnWrLayer)) 
								{
									if ("N".equals(sYD_STK_LYR_ACTIVE_STAT)) 
									{
										resMsg.setField("YD_L3_MSG" , "물리2단(사용불가)["+ydDnWrLoc+ydDnWrLayer+"]");
									} 
									else if ("D".equals(sYD_STK_LYR_STAT)) 
									{
										resMsg.setField("YD_L3_MSG" , "물리2단(작업예약존재)["+sSTL_NO+" "+ydDnWrLoc+ydDnWrLayer+"]");
									} 
									else if ("".equals(sSTL_NO_01_CURR) || "".equals(sSTL_NO_01_NEXT)) 
									{
										resMsg.setField("YD_L3_MSG" , "물리2단불가 해당위치 1단저장품 미존재");
									} 
									else if (!"C".equals(sLYR_STAT_01_CURR) || !"C".equals(sLYR_STAT_01_NEXT)) 
									{
										resMsg.setField("YD_L3_MSG" , "물리2단불가 1단저장품 작업예약 존재");
									}
								}
								
							}// else
						} //if (jsCrnLoc.size() == 1)
						
						
					} //물리좌표로 좌표 조회했을때 논리좌표가 2단으로 나왔을 때 
					
				} 
				else 
				{	
					resMsg.setField("YD_L3_HD_RS_CD", "E002");
					resMsg.setField("YD_L3_MSG"     , "물리좌표 적치불가["+ydCrnXaxis+" "+ydCrnYaxis+"]"); //해당 물리좌표 적치불가시 수신받은 물리좌표 리턴
				}
			} 
			commUtils.printLog(logId, "sSTL_NO               	= " + sSTL_NO                , "[info]");
			commUtils.printLog(logId, "sSTK_ABLE_YN            	= " + sSTK_ABLE_YN             , "[info]");
			commUtils.printLog(logId, "sYD_STK_LYR_X_AXIS     	= " + sYD_STK_LYR_X_AXIS      , "[info]");
			commUtils.printLog(logId, "sYD_STK_LYR_Y_AXIS     	= " + sYD_STK_LYR_Y_AXIS      , "[info]");
			commUtils.printLog(logId, "sYD_STK_LYR_Z_AXIS     	= " + sYD_STK_LYR_Z_AXIS      , "[info]");
			commUtils.printLog(logId, "sYD_STK_LYR_STAT       	= " + sYD_STK_LYR_STAT        , "[info]");
			commUtils.printLog(logId, "sYD_STK_LYR_ACTIVE_STAT	= " + sYD_STK_LYR_ACTIVE_STAT , "[info]");
			commUtils.printLog(logId, "sSTL_NO_01_CURR       	= " + sSTL_NO_01_CURR        , "[info]");
			commUtils.printLog(logId, "sSTL_NO_01_NEXT       	= " + sSTL_NO_01_NEXT        , "[info]");
			commUtils.printLog(logId, "sLYR_STAT_01_CURR       	= " + sLYR_STAT_01_CURR        , "[info]");
			commUtils.printLog(logId, "sLYR_STAT_01_NEXT       	= " + sLYR_STAT_01_NEXT        , "[info]");
			
			//강제권하 정보 저장
//			String sAPPLY038 = yfComm.ACoilApplyYn("APP038","1","1"); //강제권하시 실적처리
//			if ("Y".equals(sAPPLY038) && "0000".equals(resMsg.getFieldString("YD_L3_HD_RS_CD"))) {
			if ("0000".equals(resMsg.getFieldString("YD_L3_HD_RS_CD"))) 
			{
				commUtils.printLog(logId, "강제권하 정보 저장", "[INFO]");
				resMsg.setField("WR_STACK_LOC_GP"  , sWR_STACK_LOC_GP  );					
				resMsg.setField("WR_YD_STK_LYR_NO", sWR_YD_STK_LYR_NO);
				
				commDao.update(resMsg, updA7YML009_5WR, logId, methodNm, "강제권하 정보 저장");
			}
			
		}
		catch(Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
		
		return resMsg;//  강제권하시 응답전문 송신 후 종료
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인 비상조업실적(F1YFL010)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL010(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인 비상조업실적[ACoilRcvL2SeEJB.rcvF1YFL010] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "크레인 비상조업실적(F1YFL010) 수신 ", rcvMsg);

			//수신 항목 값
			String sYD_EQP_ID		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String sYD_UP_LOC		= commUtils.trim(rcvMsg.getFieldString("YD_UP_LOC")); //권상위치
			String sYD_DN_LOC		= commUtils.trim(rcvMsg.getFieldString("YD_DN_LOC")); //권하위치
			String sYD_EQP_WRK_SH	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH")); //야드설비작업매수
			String sCOIL_NO1		= commUtils.trim(rcvMsg.getFieldString("COIL_NO1")); //COIL_NO1
			String sCOIL_NO2		= commUtils.trim(rcvMsg.getFieldString("COIL_NO2")); //COIL_NO1
			String sYD_UP_CMPL_DT   = commUtils.trim(rcvMsg.getFieldString("YD_UP_CMPL_DT")); //야드권상완료일시
			String sYD_DN_CMPL_DT   = commUtils.trim(rcvMsg.getFieldString("YD_DN_CMPL_DT")); //야드권하완료일시
			
			String msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"F1YFL010"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			//수신항목 Check
			if (!"1".equals(sYD_EQP_ID.substring(0,1)) || !"CR".equals(sYD_EQP_ID.substring(2,4))) {
				throw new Exception("야드설비ID(YD_EQP_ID)가 야드구분이 '1'이 아니거나 설비 구분이 'CR'이 아닙니다!! [" + sYD_EQP_ID + "]");
			}
			if (sYD_UP_LOC.length() != 10) {
				throw new Exception("권상위치(YD_UP_LOC)가 10자리가 아닙니다!! [" + sYD_UP_LOC + "]");
			}
			if (sYD_DN_LOC.length() != 10) {
				throw new Exception("권하위치(YD_DN_LOC)가 10자리가 아닙니다!! [" + sYD_DN_LOC + "]");
			}
			if (!"1".equals(sYD_UP_LOC.substring(0,1))) {
				throw new Exception("권상위치(YD_UP_LOC)가 야드구분이 '1'이  아닙니다!! [" + sYD_UP_LOC + "]");
			}
			if (!"1".equals(sYD_DN_LOC.substring(0,1))) {
				throw new Exception("권상위치(YD_DN_LOC)가 야드구분이 '1'이  아닙니다!! [" + sYD_DN_LOC + "]");
			}
			if (!"01".equals(sYD_EQP_WRK_SH)) {
				throw new Exception("야드설비작업매수 가 '01' 이 아닌 값이 들어 왔습니다!! [" + sYD_EQP_WRK_SH + "]");
			}
			if ("".equals(sCOIL_NO1)) {
				throw new Exception("sCOIL_NO1 에 빈 값이 들어왔습니다!! [" + sCOIL_NO1 + "]");
			}
			if (sYD_UP_CMPL_DT.length() != 14) {
				throw new Exception("야드권상완료일시(YD_UP_CMPL_DT)가 14자리가 아닙니다!! [" + sYD_UP_CMPL_DT + "]");
			}
			if (sYD_DN_CMPL_DT.length() != 14) {
				throw new Exception("야드권하완료일시(YD_DN_CMPL_DT)가 14자리가 아닙니다!! [" + sYD_DN_CMPL_DT + "]");
			}

			
			JDTORecord jrParam	= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//Crane스케줄ID생성 
			String sCrnSchID = commDao.getSeqId(logId, methodNm, "CrnSch"); //비상조업실적용 1개의 Crane스케줄ID 사용
			
				
			/**********************************************************
			* 1. COIL_NO 로 적치단 Clear 하기
			**********************************************************/
			jrParam.setField("MODIFIER"	, modifier		); //수정자
			jrParam.setField("STL_NO"	, commUtils.trim(rcvMsg.getFieldString("COIL_NO1")));
			jrParam.setField("YD_GP"	, "1");	
			commDao.update(jrParam, clearStackLayer, logId, methodNm, "COIL_NO 로 적치단 Clear");
			
			
			/**********************************************************
			* 2. 권하위치에  COIL_NO 를 적치중으로 설정한다.
			**********************************************************/
			jrParam.setField("MODIFIER"				, modifier		); //수정자
			jrParam.setField("STL_NO"				, commUtils.trim(rcvMsg.getFieldString("COIL_NO1")));
			jrParam.setField("YD_STK_COL_GP"		, sYD_DN_LOC.substring(0,6));	
			jrParam.setField("YD_STK_BED_NO"		, sYD_DN_LOC.substring(6,8));	
			jrParam.setField("YD_STK_LYR_NO"		, sYD_DN_LOC.substring(8,10));	
			commDao.update(jrParam, setStackLayer, logId, methodNm, "권하위치에  COIL_NO 를 적치중으로 설정 ");

			
			/**********************************************************
			* 3. 코일공통 수정 (별도 Transaction 으로 처리)
			**********************************************************/
			jrParam.setField("MODIFIER"	, modifier	 ); //수정자
			jrParam.setField("STL_NO"	, commUtils.trim(rcvMsg.getFieldString("COIL_NO1")));
			jrParam.setField("YD_LOC"   , sYD_DN_LOC ); //야드권하실적위치
			
			EJBConnector ejbConn1 = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			ejbConn1.trx("UpdCoilComLoc", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			
			/**********************************************************
			* 4. 작업이력 등록
			**********************************************************/
			jrParam.setField("YD_CRN_SCH_ID"		, sCrnSchID);	
			jrParam.setField("STL_NO"				, commUtils.trim(rcvMsg.getFieldString("COIL_NO1")));
			jrParam.setField("YD_EQP_ID"			, sYD_EQP_ID);	
			jrParam.setField("YD_UP_WR_LOC"			, sYD_UP_LOC.substring(0, 8));	
			jrParam.setField("YD_UP_WR_LYR"			, sYD_UP_LOC.substring(8, 10));	
			jrParam.setField("YD_UP_CMPL_DT"		, sYD_UP_CMPL_DT);
			jrParam.setField("YD_DN_WR_LOC"			, sYD_DN_LOC.substring(0, 8));	
			jrParam.setField("YD_DN_WR_LYR"			, sYD_DN_LOC.substring(8, 10));	
			jrParam.setField("YD_DN_CMPL_DT"		, sYD_DN_CMPL_DT);	
			jrParam.setField("MODIFIER"				, modifier); //수정자
			jrParam.setField("YD_GP"				, "1");	
			commDao.insert(jrParam, insWrkHist_AxYML010, logId, methodNm, "비상조업용 작업이력 등록 ");

			
			/*************************************************************
			 * 5. 저장품 제원 전송
			 *************************************************************/
			JDTORecord sndL2Msg 	= JDTORecordFactory.getInstance().create();
			sndL2Msg.setField("YD_INFO_SYNC_CD"	, "5"); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP"			, "I"); //전문구분
			sndL2Msg.setField("YD_STK_COL_GP"  	, sYD_DN_LOC.substring(0, 6)); //야드적치열구분
			sndL2Msg.setField("YD_STK_BED_NO"  	, sYD_DN_LOC.substring(6, 8)); //야드적치Bed번호
			sndL2Msg.setField("YD_GP"          	, sYD_DN_LOC.substring(0, 1)                                 ); //야드구분
			sndL2Msg.setField("STL_NO"       	, commUtils.trim(rcvMsg.getFieldString("COIL_NO1"))); //재료번호

			//전송Data 생성
			if (rcvMsg.getFieldString("COIL_NO1").startsWith("S")) 
			{
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L002_SCRAP", sndL2Msg));
			}
			else
			{
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L002", sndL2Msg));
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e) 
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 작업현황요구(F1YFL013)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL013(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "작업현황요구[ACoilRcvL2SeEJB.rcvF1YFL013] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     //설비ID
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID 이상 [" + ydEqpId + "]");
			}


			/**********************************************************
			* 2. 작업현황응답(YFF1L007)
			**********************************************************/
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name

			sndL2Msg.setField("YD_EQP_ID"   , ydEqpId);  					//이동구분 
			//작업현황응답정보 송신
			jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L007", sndL2Msg));
								
	
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업가능응답(F1YFL015)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL015(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업가능응답[ACoilRcvL2SeEJB.rcvF1YFL015] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     		//설비ID
			String ydWrkProgStat= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"));    //야드작업진행상태(5:권하위치변경)만 올라옴
			String ydSchCd   	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));    		//야드스케쥴코드
			String ydCrnSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); 		//야드크레인스케쥴ID
			String reqYn  		= commUtils.trim(rcvMsg.getFieldString("REQ_YN")); 				//유무응답
			String ReqMsg  		= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); //메시지
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			String headMsgGp    = commUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 		//head msg구분
			String errCd    	= commUtils.trim(rcvMsg.getFieldString("ERR_CD"     )); 		//에러코드
			String ydCrnSchIdOld    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID_OLD"     )); //이전스케줄ID
			String ydChgStlNo 		= "";
			String ydChgDnWoLoc	    = "";
			String ydChgDnWoLayer	= "";
			String ydBefDnWoLoc		= "";
			String ydBefDnWoLayer	= "";
			String ydWbookId 		= "";
			String ydWrkProgStst	= "";	 //철자유의..kbs
			String ydUpWrLoc	    = "";	
			String ydL2RequestStat	= "";
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

					
			
			/**********************************************************
			* 1. 무인 크레인이 이면 처리
			**********************************************************/
			if (yfComm.chkAutoCrn(ydEqpId) ) {
				jrRtn = this.rcvF1YFL015Auto(rcvMsg);
				return jrRtn;
			} 
			
			commUtils.printLog(logId,  "★★기존 유인 작업 함 -->  크레인 작업지시번호★★" + ydCrnSchId, "SL");
			
			
			
			/**********************************************************
			* 2. 수신 항목 값 Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);  					
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);  					
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);  					
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getYdCrnSchLocLog, logId, methodNm, "대상작업 조회");
			if (jsCrnSch.size() == 0) {
				return jrRtn;
			} else {
				ydCrnSchId 		= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");   
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");     
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");       
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				ydUpWrLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WR_LOC");    //권상위치
				
				ydChgStlNo   	= jsCrnSch.getRecord(0).getFieldString("STL_NO_TEMP");     //sTAG_STL_NO
				ydChgDnWoLoc	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_TO"); //변경후저장위치szStkPos
				ydChgDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("STK_LYR_NO_TEMP"); //변경후저장위치szStkLyrNo
				ydBefDnWoLoc 	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC");    //변경전저장위치szOldStkPos
				ydBefDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LYR");  //변경전저장위치szOldStkLyrNo
				ydL2RequestStat	= jsCrnSch.getRecord(0).getFieldString("YD_L2_REQUEST_STAT");  //야드L2요구상태
			} 
			

			/**********************************************************
			* 3. L2 응답메시지 UPDATE 
			**********************************************************/
			commDao.update(jrParam, updYdCrnSchProgStatMsgNo, logId, methodNm,  "TB_YF_CRNSCH");
			
			
			
			/**********************************************************
			* 4. 권하위치 변경 요청 일 경우
			**********************************************************/		
			if (YfConstant.YD_L2_REQUEST_STAT_5.equals(ydWrkProgStat)) 
			{
				
				/**********************************************************
				* 4-1. 응답전문 N 일때(작업 불가메세지)
				**********************************************************/
				if ("N".equals(reqYn)) 
				{
					commUtils.printLog(logId, methodNm + "권하위치 변경 불가 일경우", "SL");
					return jrRtn;
				}
				
				/**********************************************************
				* 4-2. 신규 저장위치 CHECK
				**********************************************************/
				String ydChgStkColGp = ydChgDnWoLoc.substring(0, 6); 
				String ydChgStkBedNo = ydChgDnWoLoc.substring(6, 8);
				String ydBefStkColGp = ""; 
				String ydBefStkBedNo = "";
				if ( ydBefDnWoLoc.length() == 8 && (!ydBefDnWoLoc.equals("XX010101"))) {	
					ydBefStkColGp = ydBefDnWoLoc.substring(0, 6); 
					ydBefStkBedNo = ydBefDnWoLoc.substring(6, 8);
				}
				commUtils.printLog(logId, methodNm + "전저장위치"+ ydBefDnWoLoc, "SL");
				
				JDTORecord inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
				inRecord.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
				inRecord.setField("YD_STK_LYR_NO" 	, ydChgDnWoLayer);
				//파라미터 jrParam 이거 맞는지???? kbs
				//JDTORecordSet jsChgStkLay = commDao.select(jrParam, getStackLayerInfo, logId, methodNm, "신규 적재위치 조회");				
				JDTORecordSet jsChgStkLay = commDao.select(inRecord, getStackLayerInfo, logId, methodNm, "신규 적재위치 조회");
				
				if (jsChgStkLay.size() == 0) {
					commUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
					return jrRtn;
				}
				
				
				
				/**********************************************************
				* 4-3. 기존(전) 정보 수정
				*  기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
				**********************************************************/					
				//-----------------------------------------------------------------------
				if ( ydBefDnWoLoc.length() == 8 && (!"XX010101".equals(ydBefDnWoLoc))) 
				{	
					String compSTL_NO  = "";
					inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("MODIFIER"        , modifier);	
					inRecord.setField("YD_STK_COL_GP" 	, ydBefStkColGp);	
					inRecord.setField("YD_STK_BED_NO" 	, ydBefStkBedNo);	
					inRecord.setField("YD_STK_LYR_NO" 	, ydBefDnWoLayer);	
					JDTORecordSet jsBefStkLay = commDao.select(inRecord, getStackLayerInfo, logId, methodNm, "신규 적재위치 조회");				
					if (jsBefStkLay.size() == 0) {
						commUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
						return jrRtn;
					} else {
						//jsCrnSch 이거 맞는지???? kbs
						//compSTL_NO = jsCrnSch.getRecord(0).getFieldString("STL_NO");
						compSTL_NO = jsBefStkLay.getRecord(0).getFieldString("STL_NO");
						// 기존 변경 재료 와 수정할 재료가 동일한지 비교
						if (ydChgStlNo.equals(compSTL_NO)) {
							// 기존 지시위치 에 쌓여 있는 정보 Clear
							//inRecord.setField("STL_NO"       , ydChgStlNo); //ydChgStlNo 이거 맞는지???? kbs
							inRecord.setField("STL_NO"       , ""); 
							inRecord.setField("YD_STK_LYR_ACTIVE_STAT", "E");
							inRecord.setField("YD_STK_LYR_STAT"       , "E");
							commDao.update(inRecord, updYdStkLyrYdStkColBedGp, logId, methodNm, "TB_YF_STKLYR 등록");
						}
					}
				}	
	
				/**********************************************************
				* 4-4. 신규 위치 SET
				**********************************************************/					
				// 신규위치에 정보를 Setting
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("MODIFIER"        , modifier);	
				inRecord.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
				inRecord.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
				inRecord.setField("YD_STK_LYR_NO" 	, ydChgDnWoLayer);	
				inRecord.setField("STL_NO"        , ydChgStlNo);
				inRecord.setField("YD_STK_LYR_ACTIVE_STAT"	, "E");
				inRecord.setField("YD_STK_LYR_STAT", "D");
				commDao.update(inRecord, updYdStkLyrYdStkColBedGp, logId, methodNm, "TB_YF_STKLYR 등록");				

				
				
				/**********************************************************
				* 4-5. 신규 물리 좌표 정보 READ
				**********************************************************/
				JDTORecord jrPara = JDTORecordFactory.getInstance().create();
				jrPara.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
				jrPara.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
				jrPara.setField("YD_STK_LYR_NO", ydChgDnWoLayer);
				JDTORecordSet jsStkLayAxis = commDao.select(jrPara, getYdStkbedLyr, logId, methodNm, "신규 좌표위치 조회");				
				if (jsStkLayAxis.size() == 0) {
					commUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
					return jrRtn;
				} 
				
				
				
				/**********************************************************
				* 4-6. 신규 물리 좌표 정보를 스케줄에 set
				**********************************************************/
				jsStkLayAxis.absolute(1);
				JDTORecord jrStkLayAxis = JDTORecordFactory.getInstance().create();
				jrStkLayAxis.setRecord(jsStkLayAxis.getRecord());
				
				inRecord   = JDTORecordFactory.getInstance().create();
				inRecord.setField("MODIFIER"        		, modifier);	
				inRecord.setField("YD_CRN_SCH_ID"	        , ydCrnSchId);
				inRecord.setField("YD_DN_WO_LOC"	     	, ydChgDnWoLoc);	
				inRecord.setField("YD_DN_WO_LYR"	        , ydChgDnWoLayer);
				inRecord.setField("YD_DN_WO_LOC_XAXIS"		, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_LYR_X_AXIS")));
				inRecord.setField("YD_DN_WO_XAXIS_GAP_MAX"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_XAXIS_TOL")));
				inRecord.setField("YD_DN_WO_XAXIS_GAP_MIN"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_XAXIS_TOL")));
				inRecord.setField("YD_DN_WO_LOC_YAXIS"		, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_LYR_Y_AXIS")));
				inRecord.setField("YD_DN_WO_YAXIS_GAP_MAX"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_YAXIS_TOL")));
				inRecord.setField("YD_DN_WO_YAXIS_GAP_MIN"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_YAXIS_TOL")));
				inRecord.setField("YD_DN_WO_LOC_ZAXIS"		, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_LYR_X_AXIS")));
				inRecord.setField("YD_DN_WO_ZAXIS_GAP_MAX"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_ZAXIS_TOL")));
				inRecord.setField("YD_DN_WO_ZAXIS_GAP_MIN"	, commUtils.trim(jrStkLayAxis.getFieldString("YD_STK_BED_ZAXIS_TOL")));
				inRecord.setField("YD_L2_REQUEST_STAT"	    , ""); 
				inRecord.setField("YD_DN_WO_LOC_TO"		    , ""); 
				
				if ("".equals(ydUpWrLoc)) {
					inRecord.setField("YD_WRK_PROG_STAT"	, "1");
				} else{
					inRecord.setField("YD_WRK_PROG_STAT"	, "2");
				}	
				commDao.update(inRecord, updYdCrnWrk, logId, methodNm, "TB_YF_CRNSCH 등록");		
				
				
				
				
				/**********************************************************
				* 4-7. 권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로  선택 인경우만 전송한다)
				**********************************************************/
				if ( "1".equals(ydWrkProgStst) || "2".equals(ydWrkProgStst)) //권상지시 or 권상완료
	    		{ 
	    			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	    			jrYdMsg.setResultCode(logId);	//Log ID
	    			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			jrYdMsg.setField("JMS_TC_CD"         	, "F1YFL007"); 					//JMSTC코드
	    			jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
	    			jrYdMsg.setField("MODIFIER"        		, modifier);	                //수정자
	    			jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
	    			jrYdMsg.setField("YD_WRK_PROG_STAT"     , ydWrkProgStst); 				//야드작업진행상태
	    			jrYdMsg.setField("YD_SCH_CD"        	, ydSchCd);						//야드스케쥴코드
	    			jrYdMsg.setField("YD_CRN_SCH_ID"        , ydCrnSchId); 					//야드크레인스케쥴ID
	    			
	    			//크레인작업지시요구 전문을 추가
					jrRtn = commUtils.addSndData(jrRtn, this.rcvF1YFL007(jrYdMsg));
				}
	    		
				
				/**********************************************************
				* 4-8. 본래 권하위치가 차량 또는 대차에서 일반야드로 권하위치를 변경하는경우 처리
				**********************************************************/
	    		String szChgEqpGp    = ydChgDnWoLoc.substring(2,4); 
	    		String szBefEqpGp    = "";
	    		if (ydBefDnWoLoc.length() >= 6)
	    		{
	    			szBefEqpGp = ydBefDnWoLoc.substring(2,4);
	    		}
	    		
	    		// 기존 설비구분이 차량이고 , 신규 설비 구분이 그 차량 작업이 아닌경우  
	    		// 작업예약 ID를 Clear  한다.
	    		if ("PT".equals(szBefEqpGp)) 
	    		{
	    			if (!szChgEqpGp.equals(szBefEqpGp)) 
	    			{

	    				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
	    				
	    				//차량스케줄 야드상차작업예약ID, 야드하차작업예약ID 삭제
	    				commDao.update(jrParam, updCommCarSchWbDel, logId, methodNm, "TB_YD_CARSCH");				
	    						
	    			}
	    		}
	    		
				return jrRtn;
			} 
			
			
			/**********************************************************
			* 5. 권하위치 변경이 아닌 경우
			**********************************************************/	
			else 
			{		
				/**********************************************************
				* 5-1. 작업 불가인 경우
				**********************************************************/	
				if ("N".equals(reqYn)) 
				{
					
					commUtils.printLog(logId, methodNm + "불가 일경우", "SL");
					
					
					/**********************************************************
					* 5-1-1. 권하위치 변경 N응답시 메세지 update ..위에서 이미 처리된 중복 갱신 kbs
					**********************************************************/	
					commDao.update(jrParam, updYdCrnSchProgStatMsgNo, logId, methodNm,  "TB_YF_CRNSCH");
					
					if (errCd != null && !"".equals(errCd)) 
					{
						
						/**********************************************************
						* 5-1-2. 작업지시가 내려와 이미 코일을 집었는데 다음작업지시가 내려온 경우
						* 다음작업지시를 수행할 수 없는 상황 (차상국은 이전 작업지시를 그대로 실행)
						* L3 는 'N' 응답에 'E001' 메세지 받은 크레인 스케줄 ID의 작업진행상태를  'W'로 변경한다.
						**********************************************************/
						if ("E001".equals(errCd)) 
						{
							
							commUtils.printLog(logId, methodNm + "E001 불가 일경우", "SL");
							
							jrParam.setField("YD_WRK_PROG_STAT"	, "W");  					
							jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchId);  					
							commDao.update(jrParam, updYdCrnSchYdWrkProgStat, logId, methodNm,  "크레인 스케줄 작업진행상태 변경");
							
							
							/**********************************************************
							* 5-1-3. 이전 스케줄ID의 상태를 '1'로 변경한다. 
							* 이전 스케줄 상태를 왜 수정하는지?? 
							**********************************************************/
							if (ydCrnSchIdOld != null && !"".equals(ydCrnSchIdOld)) 
							{
								jrParam.setField("YD_WRK_PROG_STAT"	, "1");  					
								jrParam.setField("YD_CRN_SCH_ID"   	, ydCrnSchIdOld);  					
								commDao.update(jrParam, updYdCrnSchYdWrkProgStat, logId, methodNm,  "이전 크레인 스케줄 작업진행상태  변경");
							}									
						}
					}
					
					return jrRtn;
				} 
				
				/**********************************************************
				* 5-2. 작업 가능인 경우
				**********************************************************/	
				else 
				{

					/**********************************************************
					* 5-2-1. 요청응답 갱신
					**********************************************************/	
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("MODIFIER"                 , modifier);	
					jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);  					
					jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);  					
					jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);  					
					commDao.update(jrParam, updYdCrnSchProgStatMsg, logId, methodNm,  "TB_YF_CRNSCH");	

					
					/**********************************************************
					* 5-2-1. 스케줄 취소인 경우
					**********************************************************/
					if ("Y".equals(reqYn) && "D".equals(headMsgGp))
					{
						commUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
						
						jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
						jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
						jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
						jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
						jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
						jrParam.setField("IS_LAST_SELECTED"	, "1");
						jrParam.setField("IS_SCH_MTL"    	, "Y"); // 스케줄 단위 취소
						
						
						
						/**********************************************************
						* 5-2-2. 크레인스케줄 취소
						**********************************************************/
						EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
						
						jrRtn = commUtils.addSndData(jrRtn, jrRst);

						commUtils.printLog(logId, "ydL2RequestStat [ " + ydL2RequestStat + " - 작업취소  ]", "SL");
						
						
						
						/**********************************************************
						* 5-2-3. 작업예약 취소
						**********************************************************/
						if ("X".equals(ydL2RequestStat)) // 화면에서 작업 취소 임 : 작업예약 삭제
						{   
							EJBConnector ejbConn1 = new EJBConnector("default", "ACoilJspSeEJB", this);
							jrRst = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							jrRtn = commUtils.addSndData(jrRtn, jrRst);
						}	
					}	
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업가능응답(rcvF1YFL015Auto)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL015Auto(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업가능응답[ACoilRcvL2SeEJB.rcvF1YFL015Auto] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId     	= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"));     		//설비ID
			String ydWrkProgStat= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT"));    //야드작업진행상태(5:권하위치변경)만 올라옴
			String ydSchCd   	= commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"));    		//야드스케쥴코드
			String ydCrnSchId  	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID")); 		//야드크레인스케쥴ID
			String reqYn  		= commUtils.trim(rcvMsg.getFieldString("REQ_YN")); 				//유무응답
			String ReqMsg  		= commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_REQ_MSG")); //메시지
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			String headMsgGp    = commUtils.trim(rcvMsg.getFieldString("MSG_GP"     )); 		//head msg구분
			String errCd    	= commUtils.trim(rcvMsg.getFieldString("ERR_CD"     )); 		//에러코드
			String ydCrnSchIdOld    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID_OLD"     )); //이전스케줄ID
			String ydChgStlNo 		= "";
			String ydChgDnWoLoc	    = "";
			String ydChgDnWoLayer	= "";
			String ydBefDnWoLoc		= "";
			String ydBefDnWoLayer	= "";
			String ydWbookId 		= "";
			String ydWrkProgStst	= ""; //철자유의..kbs	
			String ydUpWrLoc	    = "";	
			String ydL2RequestStat	= "";
			String sYD_SCH_PRIOR    = ""; //스케줄 우선순위
			String ydL3Msg			= ""; 	//야드L3MESSAGE
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			*   무인 크레인이 아니면 SKIP
			**********************************************************/
			if (!yfComm.chkAutoCrn(ydEqpId) ) {
				throw new Exception("무인 크레인이 아닙니다.! [" + ydEqpId + "]");
			} 
			commUtils.printLog(logId,  "★★자동화 작업 함---> 크레인 작업지시번호★★" + ydCrnSchId, "SL");
			commUtils.printLog(logId, "요청구분 [ " + reqYn + " - 작업취소 " + headMsgGp + " ]", "SL");
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"                 , modifier);	
			jrParam.setField("YD_EQP_ID"         		, ydEqpId);  					
			jrParam.setField("YD_CRN_SCH_ID"      		, ydCrnSchId);  					
			jrParam.setField("YD_WRK_PROG_STAT"    		, ydWrkProgStat);  					
			jrParam.setField("YD_WRK_PROG_REQ_MSG"   	, ReqMsg);    
			jrParam.setField("ERR_CD"   				, errCd);    
			jrParam.setField("YD_CRN_SCH_ID_OLD"   		, ydCrnSchIdOld);    
			
			JDTORecordSet jsCrnSch = commDao.select(jrParam, getYdCrnSchLocLog, logId, methodNm, "대상작업 조회");
			if (jsCrnSch.size() == 0) {
				throw new Exception("크레인 스케쥴 번호가 없습니다..! [" + ydCrnSchId + "]");
			} else {
				ydCrnSchId 		= jsCrnSch.getRecord(0).getFieldString("YD_CRN_SCH_ID");   
				ydWbookId 		= jsCrnSch.getRecord(0).getFieldString("YD_WBOOK_ID");     
				ydSchCd 		= jsCrnSch.getRecord(0).getFieldString("YD_SCH_CD");       
				ydWrkProgStst	= jsCrnSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT");
				ydUpWrLoc	    = jsCrnSch.getRecord(0).getFieldString("YD_UP_WR_LOC");    //권상위치
				
				ydChgStlNo   	= jsCrnSch.getRecord(0).getFieldString("STL_NO_TEMP");     //변경할 코일번호
				ydChgDnWoLoc	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC_TO"); //변경후저장위치szStkPos
				ydChgDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("STK_LYR_NO_TEMP"); //변경후저장위치szStkLyrNo
				ydBefDnWoLoc 	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LOC");    //변경전저장위치szOldStkPos
				ydBefDnWoLayer	= jsCrnSch.getRecord(0).getFieldString("YD_DN_WO_LYR");  //변경전저장위치szOldStkLyrNo
				ydL2RequestStat	= jsCrnSch.getRecord(0).getFieldString("YD_L2_REQUEST_STAT");  //야드L2요구상태
				
				sYD_SCH_PRIOR   = jsCrnSch.getRecord(0).getFieldString("YD_SCH_PRIOR");    //스케줄우선순위
			} 
			
			
			
			
			/**************************************
			 * 2. L2 응답메시지 UPDATE 
			 **************************************/
			commDao.update(jrParam, updYdCrnSchProgStatMsgNo, logId, methodNm,  "TB_YF_CRNSCH");
			
			
			
			
			/********************************
			 * 3. 스케줄 상태와 설비상태 동기화
			 ********************************/
			String sSCH_STAT = ydWrkProgStat;
			if ("5".equals(ydWrkProgStat)) //강제권하
			{ 
				sSCH_STAT = "2"; //권상완료
			}
			
			if ("Y".equals(reqYn)) 
			{
				jrParam.setField("YD_EQP_PROG_STAT"	, sSCH_STAT); 
				jrParam.setField("YD_EQP_ID"		, ydEqpId  );
				commDao.update(jrParam, updEqpStat, logId, methodNm,  "TB_YF_EQUIP");	
			}
			
			
			
			
			/**********************************************************
			* 4. 권하위치 변경 요청 결과
			**********************************************************/
			if (("1".equals(ydWrkProgStat) || "5".equals(ydWrkProgStat)) && YfConstant.YD_L2_REQUEST_STAT_5.equals(ydL2RequestStat)) 
			{
				
				/**********************************************************
				* 4-1. 응답전문 N 일때(작업 불가메세지)
				**********************************************************/
				if ("N".equals(reqYn)) 
				{
					commUtils.printLog(logId, methodNm + "권하위치 변경 불가 일경우", "SL");
					return jrRtn;
				}
				
				
				
				/**********************************************************
				* 4-2. 신규 저장위치 CHECK
				**********************************************************/
				String ydChgStkColGp = ydChgDnWoLoc.substring(0, 6); 
				String ydChgStkBedNo = ydChgDnWoLoc.substring(6, 8);
				String ydBefStkColGp = ""; 
				String ydBefStkBedNo = "";
				if ( ydBefDnWoLoc.length() == 8 && (!"XX010101".equals(ydBefDnWoLoc))) 
				{	
					ydBefStkColGp = ydBefDnWoLoc.substring(0, 6); 
					ydBefStkBedNo = ydBefDnWoLoc.substring(6, 8);
				}
				commUtils.printLog(logId, methodNm + "전저장위치"+ ydBefDnWoLoc, "SL");
				
				JDTORecord inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("STL_NO" 	    	, ydChgStlNo);	
				inRecord.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
				inRecord.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
				inRecord.setField("YD_STK_LYR_NO" 	, ydChgDnWoLayer);
				JDTORecordSet jsChgStkLay = commDao.select(inRecord, getStackLayerInfo, logId, methodNm, "신규 적재위치 조회");				
				
				if (jsChgStkLay.size() == 0) 
				{
					throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
				}
				
				inRecord.setField("YD_CRN_SCH_ID" 	, ydCrnSchId);	
				inRecord.setField("YD_SCH_CD" 		, ydSchCd);	
				
				
				
				/**********************************************************
				* 4-3. 기존(전) 정보 수정
				*  기존 산적위치 정보가 올바르지 않거나 없을 경우는 기존산적위치를 삭제할 수없으므로 건너띈다.
				**********************************************************/			
				if ( ydBefDnWoLoc.length() == 8 && (!"XX010101".equals(ydBefDnWoLoc))) {	
					String compSTL_NO  = "";
					inRecord = JDTORecordFactory.getInstance().create();
					inRecord.setField("MODIFIER"        , modifier);	
					inRecord.setField("YD_STK_COL_GP" 	, ydBefStkColGp);	
					inRecord.setField("YD_STK_BED_NO" 	, ydBefStkBedNo);	
					inRecord.setField("YD_STK_LYR_NO" 	, ydBefDnWoLayer);	
					JDTORecordSet jsBefStkLay = commDao.select(inRecord, getStackLayerInfo, logId, methodNm, "기존 적재위치 조회");				
					if (jsBefStkLay.size() == 0) {
						throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
//						commUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
//						return jrRtn;
					} else {
						compSTL_NO = jsBefStkLay.getRecord(0).getFieldString("STL_NO");
						// 기존 변경 재료 와 수정할 재료가 동일한지 비교
						if (ydChgStlNo.equals(compSTL_NO)) {
					
							// 기존 지시위치 에 쌓여 있는 정보 Clear
							inRecord.setField("STL_NO"       			, "");
							inRecord.setField("YD_STK_LYR_ACTIVE_STAT"	, "E");
							inRecord.setField("YD_STK_LYR_STAT"       	, "E");
							commDao.update(inRecord, updYdStkLyrYdStkColBedGp, logId, methodNm, "기존위치 Clear");
						}
					}
				}	
	
				
				
				/**********************************************************
				* 4-4. 신규 위치 SET
				**********************************************************/				
				// 신규위치에 정보를 Setting
				inRecord = JDTORecordFactory.getInstance().create();
				inRecord.setField("MODIFIER"        , modifier);	
				inRecord.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
				inRecord.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
				inRecord.setField("YD_STK_LYR_NO" 	, ydChgDnWoLayer);	
				inRecord.setField("STL_NO"        , ydChgStlNo);
				inRecord.setField("YD_STK_LYR_ACTIVE_STAT"	, "E");
				inRecord.setField("YD_STK_LYR_STAT", "D");
				commDao.update(inRecord, updYdStkLyrYdStkColBedGp, logId, methodNm, "신규위치 등록");				

				
				
				/**********************************************************
				* 4-5. 신규 물리 좌표 정보 READ
				**********************************************************/
				JDTORecord jrPara = JDTORecordFactory.getInstance().create();
				jrPara.setField("YD_STK_COL_GP" 	, ydChgStkColGp);	
				jrPara.setField("YD_STK_BED_NO" 	, ydChgStkBedNo);	
				jrPara.setField("YD_STK_LYR_NO", ydChgDnWoLayer);
				JDTORecordSet jsStkLayAxis = commDao.select(jrPara, getYdStkbedLyr, logId, methodNm, "신규 좌표위치 조회");				
				if (jsStkLayAxis.size() == 0) {
					throw new Exception("저장위치 이상.! [" + ydCrnSchId + "]");
//					commUtils.printLog(logId, methodNm + "저장위치 이상", "SL");
//					return jrRtn;
				} 
				
				
				
				/**********************************************************
				* 4-6. 신규 물리 좌표 정보를 스케줄에 set
				**********************************************************/
				jsStkLayAxis.absolute(1);
				JDTORecord jrStkLayAxis = JDTORecordFactory.getInstance().create();
				jrStkLayAxis.setRecord(jsStkLayAxis.getRecord());
				inRecord   = JDTORecordFactory.getInstance().create();
				inRecord.setField("MODIFIER"        		, modifier);	
				inRecord.setField("YD_CRN_SCH_ID"	        , ydCrnSchId);
				inRecord.setField("YD_DN_WO_LOC"	     	, ydChgDnWoLoc);	
				inRecord.setField("YD_DN_WO_LYR"	        , ydChgDnWoLayer);
				inRecord.setField("YD_DN_WO_LOC_XAXIS"		, commUtils.trim(rcvMsg.getFieldString("YD_STK_LYR_X_AXIS")));
				inRecord.setField("YD_DN_WO_XAXIS_GAP_MAX"	, commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_XAXIS_TOL")));
				inRecord.setField("YD_DN_WO_XAXIS_GAP_MIN"	, commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_XAXIS_TOL")));
				inRecord.setField("YD_DN_WO_LOC_YAXIS"		, commUtils.trim(rcvMsg.getFieldString("YD_STK_LYR_Y_AXIS")));
				inRecord.setField("YD_DN_WO_YAXIS_GAP_MAX"	, commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_YAXIS_TOL")));
				inRecord.setField("YD_DN_WO_YAXIS_GAP_MIN"	, commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_YAXIS_TOL")));
				inRecord.setField("YD_DN_WO_LOC_ZAXIS"		, commUtils.trim(rcvMsg.getFieldString("YD_STK_LYR_Z_AXIS")));
				inRecord.setField("YD_DN_WO_ZAXIS_GAP_MAX"	, commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_ZAXIS_TOL")));
				inRecord.setField("YD_DN_WO_ZAXIS_GAP_MIN"	, commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_ZAXIS_TOL")));
				inRecord.setField("YD_L2_REQUEST_STAT"	    , ""); 
				inRecord.setField("YD_DN_WO_LOC_TO"		    , ""); 
				inRecord.setField("DOWN_ROTATION_ANGLE"		, commUtils.trim(rcvMsg.getFieldString("ROTATION_ANGLE"))); //회전각도
				
				
				if ("".equals(ydUpWrLoc)) {
					inRecord.setField("YD_WRK_PROG_STAT"	, "1");
				} else{
					inRecord.setField("YD_WRK_PROG_STAT"	, "2");
				}			
				commDao.update(inRecord, updYdCrnWrk, logId, methodNm, "TB_YF_CRNSCH 등록");		
				
				
				
				/**********************************************************
				* 4-7. 권하위치 변경후 작업지시정보를  재전송 (Manual 작업지시보내는 방법으로 선택 인경우만 전송한다)
				**********************************************************/
	    		if ( "1".equals(ydWrkProgStst) || "2".equals(ydWrkProgStst)) //권상지시 or 권상완료
	    		{ 
	    			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	    			jrYdMsg.setResultCode(logId);	//Log ID
	    			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			jrYdMsg.setField("JMS_TC_CD"         	, "F1YFL007"); 					//JMSTC코드
	    			jrYdMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); 	//JMSTC생성일시
	    			jrYdMsg.setField("MODIFIER"        		, modifier);	                //수정자
	    			jrYdMsg.setField("YD_EQP_ID"        	, ydEqpId);  					//야드설비ID
	    			jrYdMsg.setField("YD_WRK_PROG_STAT"     , ydWrkProgStst); 				//야드작업진행상태
	    			jrYdMsg.setField("YD_SCH_CD"        	, ydSchCd);						//야드스케쥴코드
	    			jrYdMsg.setField("YD_CRN_SCH_ID"        , ydCrnSchId); 					//야드크레인스케쥴ID
	    			
	    			//크레인작업지시요구 전문을 추가
					jrRtn = commUtils.addSndData(jrRtn, this.rcvF1YFL007(jrYdMsg));
				}
	    		
	    		
	    		
	    		/**********************************************************
				* 4-8. 본래 권하위치가 차량 또는 대차에서 일반야드로 권하위치를 변경하는경우 처리
				**********************************************************/
	    		String szChgEqpGp    = ydChgDnWoLoc.substring(2,4); 
	    		String szBefEqpGp    = "";
	    		// 기존 권하위치 
	    		if (ydBefDnWoLoc.length() >= 6) {
	    			szBefEqpGp = ydBefDnWoLoc.substring(2,4);
	    		}
	    		
	    		// 기존 설비구분이 차량이고 , 신규 설비 구분이 그 차량 작업이 아닌경우  
	    		// 작업예약 ID를 Clear  한다.
	    		if ("PT".equals(szBefEqpGp)) {
	    			if (!szChgEqpGp.equals(szBefEqpGp)) {

	    				jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
	    				
	    				String szULGp  = ydSchCd.substring(6,7);  //상차구분
	    				String szCarGp = ydSchCd.substring(2,4);
	    				
	    				if ("PT".equals(szCarGp)) {
	    					// 차량인 경우 		
	    					if ("U".equals(szULGp)) {
	    						//상차인 경우 작업예약 정보 삭제
	    						commDao.update(jrParam, updCommCarSchWbDelLd, logId, methodNm, "TB_YD_CARSCH");				
	    						
	    					}else if ("L".equals(szULGp)) {
	    						//하차인 경우 작업예약 정보 삭제
	    						commDao.update(jrParam, updCommCarSchWbDelUd, logId, methodNm, "TB_YD_CARSCH");				
	    					}
	    				}
	    			}	
	    		}
	    		
				return jrRtn;
			} 
			
			
			/**********************************************************
			* 5. 권하위치 변경이 아닌 경우
			**********************************************************/
			else 
			{
				/**********************************************************
				* 5-1. 작업 가능인 경우
				**********************************************************/
				if ("Y".equals(reqYn)) 
				{
					commUtils.printLog(logId, methodNm + "가능인 경우", "SL");
					commDao.update(jrParam, updYdCrnSchProgStatMsg, logId, methodNm,  "TB_YF_CRNSCH");
					
					commUtils.printLog(logId, "요청구분 [ " + reqYn + " - 작업취소 " + headMsgGp + " ]", "SL");
					
					/********************
					 * 5-1-1. 스케줄 취소인 경우
					 ********************/
					if ("Y".equals(reqYn) && "D".equals(headMsgGp)) 
					{
						commUtils.printLog(logId, "스케쥴취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");
						
						jrParam.setField("YD_WBOOK_ID"  	, ydWbookId );
						jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
						jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
						jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
						jrParam.setField("YD_L2_RETURN_FLAG", "Y"       );
						jrParam.setField("IS_LAST_SELECTED"	, "1");
						jrParam.setField("IS_SCH_MTL"    	, "Y"); // 스케줄 단위 취소
						
						/**********************************************************
						* 5-1-2. 크레인스케줄 취소
						**********************************************************/
						EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
						JDTORecord jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
						
						jrRtn = commUtils.addSndData(jrRtn, jrRst);

						
						commUtils.printLog(logId, "ydL2RequestStat [ " + ydL2RequestStat + " - 작업취소  ]", "SL");
						
						
						/**********************************************************
						*5-1-3. 작업예약 취소(화면에서 작업 취소 임 : 작업예약 삭제)
						**********************************************************/
						if ("X".equals(ydL2RequestStat)) 
						{
							EJBConnector ejbConn1 = new EJBConnector("default", "ACoilJspSeEJB", this);
							jrRst = (JDTORecord)ejbConn1.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							jrRtn = commUtils.addSndData(jrRtn, jrRst);
						}	
					}	
					
					
					
					/***************************
					 *5-1-4. 일시정지-긴급작업(S1)
					 * 박판에서 필요한지 확인 필요. kbs
					 ***************************/
					String sAPP030 = yfComm.ACoilApplyYn("APP030","1","S1");
					commUtils.printLog(logId, "[F1YFL015] 일시정지-긴급작업(S1) 응답 시작", "[INFO]");
					
					if ("Y".equals(sAPP030) && !"1EYD99MM".equals(ydSchCd) && !"1EKE02MM".equals(ydSchCd)) {
						if ("Y".equals(reqYn) && "0".equals(sYD_SCH_PRIOR)) {
							// 해당 스케줄 1로 변경
							jrParam.setField("YD_WRK_PROG_STAT"	, "1"       ); // 긴급작업 응답이 Y로 왔으므로 선택(1)
							jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
							commDao.update(jrParam, updCrnWrkMgt1Auto, logId, methodNm,  "TB_YF_CRNSCH");
							
							// 동일크레인의 해당 스케줄외 W로 변경 
						    jrParam.setField("YD_WRK_PROG_STAT"	, "W"       );  
							jrParam.setField("YD_EQP_ID"    	, ydEqpId   );
							commDao.update(jrParam, updCrnSchW, logId, methodNm,  "TB_YF_CRNSCH");
						}
					}
					commUtils.printLog(logId, "[F1YFL015] 일시정지-긴급작업(S1) 응답 끝", "[INFO]");
				} 
				
				/**********************************************************
				* 5-2. 작업 불가능인 경우
				**********************************************************/
				else if ("N".equals(reqYn)) 
				{
					 
					commUtils.printLog(logId, methodNm + "권하위치 변경이 아닐경우  N 응답 ", "SL");
				
					
					/**********************************************************
					* 5-2-1. 응답 메시지 갱신 ..위에서 이미 처리된 중복 갱신 kbs
					**********************************************************/
					commDao.update(jrParam, updYdCrnSchProgStatMsgNo, logId, methodNm,  "TB_YF_CRNSCH");	
					
					
					/**********************************************************
					* 5-2-2. 응답 메시지 갱신
					**********************************************************/
					String sAPPLY008 = yfComm.ACoilApplyYn("APP008","1","1"); //대표코드,공장구분,ITEM
					commUtils.printLog(logId,  "자동 LINE OFF처리:" + sAPPLY008, "SL");						
					if ("Y".equals(sAPPLY008)) 
					{					
						if (errCd != null && !"".equals(errCd)) {
							if ("E001".equals(errCd) || "E002".equals(errCd)) 
							{
								
								/**********************************************************
								* 5-2-3. 작업지시가 내려와 이미 코일을 집었는데 다음작업지시가 내려온 경우
								* 다음작업지시를 수행할 수 없는 상황 (차상국은 이전 작업지시를 그대로 실행)
								* L3 는 'N' 응답에 'E001' 메세지 받은 크레인 스케줄 ID의 작업진행상태를  'W'로 변경한다.
								**********************************************************/
								commUtils.printLog(logId, methodNm + "E001 불가 일경우", "SL");
								
								jrParam.setField("YD_WRK_PROG_STAT"  , "W");
								jrParam.setField("OLD_YD_CRN_SCH_ID" , ydCrnSchId);
								commDao.update(jrParam, updCrnWrkMgtPriorWrkNext1Auto, logId, methodNm,  "크레인작업 지시 원복");
								
								
								
								/**********************************************************
								* 5-2-4. 이전 스케줄ID의 상태를 '1'로 변경한다.
								**********************************************************/
								if (ydCrnSchIdOld != null && !"".equals(ydCrnSchIdOld) ) 
								{
									
									jrParam.setField("YD_WRK_PROG_STAT" , "1");
									jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdOld);
									commDao.update(jrParam, updYdCrnSchYdWrkProgStatAuto, logId, methodNm,  "이전 크레인 스케줄 작업진행상태  변경");
								}									
							}
						}
					}
					
					
					/**********************************************************
					* 6. 보급존 권하자리가 없을 경우 L2 오류 코드 E003으로 전송, 다음스케줄 확인후 응담 전송
					**********************************************************/
					if ( "E003".equals(errCd)) 
					{

						
						commUtils.printLog(logId, methodNm + "E003 불가 일경우(다음 작업지시 전송)", "SL");
						
						jrParam.setField("YD_WRK_PROG_STAT"  , "W");
						jrParam.setField("OLD_YD_CRN_SCH_ID" , ydCrnSchId);
						commDao.update(jrParam, updCrnWrkMgtPriorWrkNext1Auto, logId, methodNm,  "크레인작업 지시 원복");
						
						
						jrParam.setField("YD_SCH_CD"    	, ydSchCd   );
						JDTORecordSet jsSch = commDao.select(jrParam, getCrnSchAxYML007Next, logId, methodNm, "다음 크레인스케줄 조회");
						
						
						/**********************************************************
						* 6.1 스케줄 조회
						**********************************************************/
						if (jsSch.size() > 0) 
						{
							String newYdCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
							jrParam.setField("YD_CRN_SCH_ID", newYdCrnSchId); //신규 야드크레인스케쥴ID
															
							//크레인스케줄 야드작업진행상태 수정
							jrParam.setField("YD_WRK_PROG_STAT"	 , "W"); //대기  
			        		commDao.update(jrParam, updCrnSchW, logId, methodNm, "크레인스케줄 야드작업진행상태 초기화");
			        		

			        		//크레인스케줄 야드작업진행상태 수정
							jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
							jrParam.setField("YD_L2_REQUEST_STAT", "1"); 
			        		commDao.update(jrParam, updStatCrnSchWrkProg, logId, methodNm, "크레인스케줄 야드작업진행상태 수정");
							
							
							jrParam.setField("MSG_GP"		, "I"); //전문구분 - 신규
							jrParam.setField("YD_EQP_STAT"	, "1"); //권상작업지시
							jrParam.setField("YD_WRK_PROG_STAT"	 , "S"); //선택지시
							jrParam.setField("YD_L2_REQUEST_STAT", "1"); 
							
							jrRtn = commUtils.addSndData(commDao.getMsgL2("YFF1L004", jrParam));
						}
						/**********************************************************
						* 6.2 작업예약 정보 검색
						*    - 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
						*    - 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
						*    - 권하완료[4] 이면 스케줄을 생성
						*    - 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
						**********************************************************/
						else
						{
							commUtils.printLog(logId, "작업예약 검색 함 ", "SL");		
							
							/**********************************************************
							* 2.2.2 대기상태[W], 권하완료[4] 지시요구
							**********************************************************/
							//크레인작업지시가 없으면 설비의 야드설비상태 수정
							jrParam.setField("YD_EQP_STAT", "W"); //작업대기
			        		commDao.update(jrParam, updStatEqp, logId, methodNm, "설비상태 수정");
			        		
			        		
			        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			        		JDTORecordSet jsWrkBook = commDao.select(jrParam, getWbIdAxYML007, logId, methodNm, "작업예약 조회");

			        		//작업예약이 있으면 크레인스케줄호출
							if (jsWrkBook.size() > 0) {
								
								// 대차가 아닌경우 
								ydL3Msg = "크레인스케줄 편성 호출";

								jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
								jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
								jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
								jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
								jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
								jrYdMsg.setField("MODIFIER"   , modifier); //수정자
									
								// 크레인스케줄편성 기동
								jrRtn = yfComm.getCrnSchMsg(jrYdMsg);
							} else {
								ydL3Msg = "다음 크레인작업지시 없음";
							}
							
							jrParam.setField("YD_L2_WR_GP"		, "J"    ); //야드L2실적구분(지시요구)
							jrParam.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
							jrParam.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
							//0615 SJH 추가
							jrParam.setField("YD_CRN_SCH_ID" , ""     ); 
							
							jrRtn = commUtils.addSndData(jrRtn, yfComm.getYFF1L005(jrParam));

							commUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");

						}
					}
					
					
					return jrRtn;
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 차량작업예정정보요구(F1YFL016)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL016(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "차량작업예정정보요구[ACoilRcvL2SeEJB.rcvF1YFL016] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try
		{	
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydLoadLoc   	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));     	//상차도 위치
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 		//수정자(Backup Only)
			
			if("".equals(modifier))
			{
				modifier = msgId;
			}
			
			methodNm = msgId.substring(0, 2) + methodNm;

			String CarNo = "";
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			
			if (ydLoadLoc.length() < 6)
			{
				commUtils.printLog(logId, methodNm + "상차도 길이 < 6 :" +ydLoadLoc , "SL");
				//throw new Exception("상차도 위치 [" + ydLoadLoc + "]");
			} 

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER",		modifier);	
			jrParam.setField("PT_LOAD_LOC",		ydLoadLoc);  					
			jrParam.setField("SEARCH_FLAG",		"1");	//1:상차도, 2:차량스케쥴 ID					
			
			//차량위치조회
			JDTORecordSet jrCarPoint = commDao.select(jrParam, getYdGetCarNoByLoc, logId, methodNm, "차량위치조회"); 
			
			if (jrCarPoint.size() == 0)
			{
				commUtils.printLog(logId, methodNm + "상차도 정보 없음 :" +ydLoadLoc , "SL");
				return jrRtn;
			}
			//2020-05-25 장치영이사와 통화 후 차량예정정보 차량 없어도 전송
//			else
//			{	
//				CarNo = jrCarPoint.getRecord(0).getFieldString("CAR_NO");
//				
//				if (CarNo.equals(""))
//				{
//					commUtils.printLog(logId, methodNm + "해당위치 차량정보 없음  :" +ydLoadLoc , "SL");
//					return jrRtn;
//				}
//			}
			
			//차량예정정보 백업 송신
			//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L008", jrParam));
			jrRtn = commUtils.addSndData(jrRtn, yfComm.procCarPlanInfo(jrParam));	//YFF1L008
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}
	
	/**
	 *      [A] 오퍼레이션명 : Coil 공통 Table 저장위치를 UPDATE한다
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean UpdCoilComLoc(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일공통update[ACoilRcvL2SeEJB.UpdCoilComLoc] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		//V_YD_LOC     -- 현 저장위치코드   
		//V_STL_NO
		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			commDao.update(rcvMsg, updateCoilCommonLocInfo2, logId, methodNm, "코일공통 수정");
        	
			commUtils.printLog(logId, methodNm, "S-");

		} catch(Exception e) {
			
		}
		return true;
	}
	
	/**
	 *      [A] 오퍼레이션명 : Coil 공통 Table 저장위치를 UPDATE한다
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public boolean UpdCoilComProg(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일공통update[ACoilRcvL2SeEJB.UpdCoilComProg] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		//V_YD_LOC     -- 현 저장위치코드   
		//V_STL_NO
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID

			commDao.update(rcvMsg, updateMatlFtmvWlrstCoil, logId, methodNm, "코일공통 수정");
        	
			commUtils.printLog(logId, methodNm, "S-");

		} catch(Exception e) {
			
		}
		return true;
	}	
	
	/**
	 *      [A] 오퍼레이션명 : 상차도 작업불가(F1YFL017)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL017(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "상차도 작업불가[ACoilRcvL2SeEJB.rcvF1YFL017] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + "상차도 작업불가(F1YFL017) 수신 ", rcvMsg);

			//수신 항목 값
			String sPT_LOAD_LOC = commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC")); //상차도위치(6)
			String sUSE_YN		= commUtils.trim(rcvMsg.getFieldString("USE_YN")); //Y:사용가능, N:사용불가
			String msgId		= commUtils.nvl(commUtils.getMsgId(rcvMsg),"F1YFL017"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier 	= commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			
			//수신항목 Check
			if (sPT_LOAD_LOC.length() != 6) {
				throw new Exception("상차도 위치 PT_LOAD_LOC 가 6자리가 아닙니다!! [" + sPT_LOAD_LOC + "]");
			}
			if (!"1".equals(sPT_LOAD_LOC.substring(0,1)) || !"PT".equals(sPT_LOAD_LOC.substring(2,4))) {
				throw new Exception("상차도 위치 PT_LOAD_LOC 가 야드구분이 '1'이 아니거나 SECT_GP가 'PT'가 아닙니다!! [" + sPT_LOAD_LOC + "]");
			}
			if (!"Y".equals(sUSE_YN) && !"N".equals(sUSE_YN)) {
				throw new Exception("상차도 사용유무 USE_YN 이 'Y','N' 이 아닌 값이 들어 왔습니다!! [" + sUSE_YN + "]");
			}
			
			JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
			
			
			// 차량포인트 적치열활성상태 UPDATE
			jrParam.setField("MODIFIER"			, modifier		); //수정자
			jrParam.setField("YD_STK_COL_GP"	, sPT_LOAD_LOC	); //상차도위치
			if ("Y".equals(sUSE_YN)) {
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "L"); //야드적치열활성상태 L:적치가능,  C아닌지...확인 필요 kbs
			} else {
				jrParam.setField("YD_STK_COL_ACT_STAT"	, "N"); //야드적치열활성상태 N:사용불가
			}
			commDao.update(jrParam, updYdCarPointActStat, logId, methodNm, "차량포인트 적치열활성상태 UPDATE ");
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
			
	/**
	 *      [A] 오퍼레이션명 : 차량동간이적 도착(F1YFL018)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL018(JDTORecord rcvMsg) throws DAOException
	{
		String methodNm = "차량동간이적 도착[ACoilRcvL2SeEJB.rcvF1YFL018] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		
		try
		{	
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값ydEqpId
			String msgId    	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydLoadLoc	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));   //상차도 위치(3APT01)
			String CarNo   		= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));     		//차량번호
			String CarUoDnGp  	= commUtils.trim(rcvMsg.getFieldString("CAR_UPDN_GP"));		//차량상하차 구분 (1:상차,2:하차)
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); 	//수정자(Backup Only)
			
			if ("".equals(modifier))
			{
				modifier = msgId;
			}
			
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydLoadLoc.length() == 0)
			{
				throw new Exception("상차도 위치 이상 [" + ydLoadLoc + "]");
			}
			else if (CarNo.length() == 0)
			{
				throw new Exception("차량번호 이상 [" + ydLoadLoc + "]");
			}
			else if (CarUoDnGp.length() == 0)
			{
				throw new Exception("차량상하차 구분 이상 [" + ydLoadLoc + "]");
			}
			
			commUtils.printLog(logId, methodNm + "/차량상하차 구분 (1:상차,2:하차  :" +CarUoDnGp , "SL");
			
			jrRtn = commUtils.addSndData(jrRtn,this.procF1YFL018(rcvMsg));	
			
			commUtils.printParam(logId, jrRtn);
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 차량동간이적 도착(F1YFL018)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procF1YFL018(JDTORecord rcvMsg) throws DAOException 
	{
		String methodNm = "차량동간이적 도착[ACoilRcvL2SeEJB.procF1YFL018] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		
		String ydSchPriorNew = "";
		String ydEqpIdNew    = "";
		
		try 
		{	
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId    	= commUtils.getMsgId(rcvMsg);							//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydLoadLoc	= commUtils.trim(rcvMsg.getFieldString("PT_LOAD_LOC"));	//상차도 위치(ex : 1APT01)
			String CarNo   		= commUtils.trim(rcvMsg.getFieldString("CAR_NO"));     	//차량번호
			String CarUoDnGp  	= commUtils.trim(rcvMsg.getFieldString("CAR_UPDN_GP"));	//차량상하차 구분 (1:상차,2:하차)
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"));	//수정자(Backup Only)
			
	    	// PIDEV
//	    	String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
	    	
			if ("".equals(modifier))
			{
//				if ("Y".equals(sApplyYnPI)) {
					modifier = msgId.substring(3,12);
//				} else {
//					modifier = msgId;
//				}
			}
			
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydLoadLoc.length() == 0)
			{
				throw new Exception("상차도 위치 이상 [" + ydLoadLoc + "]");
			}
			else if (CarNo.length() == 0)
			{
				throw new Exception("차량번호 이상 [" + ydLoadLoc + "]");
			}
			else if (CarUoDnGp.length() == 0)
			{
				throw new Exception("차량상하차 구분 이상 [" + ydLoadLoc + "]");
			}
			
			JDTORecordSet carSchCt = JDTORecordFactory.getInstance().createRecordSet("temp");
			jrParam.setField("CAR_NO",			CarNo);
			carSchCt = commDao.select(jrParam, getCarSchCt, logId, methodNm, "차량의 크레인스케줄 조회");
			
			if (carSchCt.size() > 0)
			{
				throw new Exception("차량번호(" + CarNo + ") 차량에 크레인스케줄이 진행중 입니다.");
			}
			
			commUtils.printLog(logId, methodNm + "/차량상하차 구분 (1:상차,2:하차) : " +CarUoDnGp , "SL");
			
			/**********************************************************
			* 2. 차량포인트 정보 get
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER",		modifier);	
			jrParam.setField("YD_STK_COL_GP",	ydLoadLoc);  					
			jrParam.setField("CAR_UPDN_GP",		CarUoDnGp);  					
			jrParam.setField("CAR_NO",			CarNo);  					
			JDTORecordSet jsCarPoint = commDao.select(jrParam, getYdGetCarNoTypeByLoc, logId, methodNm, "차량포인트정보조회"); 
			
			if (jsCarPoint.size() == 0)
			{
				commUtils.printLog(logId, methodNm + "해당위치 포인트정보 없음  :" +ydLoadLoc , "SL");
				return jrRtn;
			} 
			
			JDTORecord jrCarPoint = JDTORecordFactory.getInstance().create();	//전문 Return
			
			jsCarPoint.first();
			jrCarPoint.setRecord(jsCarPoint.getRecord());
			
			String sCarNo 			= commUtils.trim(jsCarPoint.getFieldString("CAR_NO"));				//szCAR_NO_GET
			String ydCarUseTypeGp 	= commUtils.trim(jsCarPoint.getFieldString("YD_CAR_USETYPE_GP"));	//szYD_CAR_USETYPE_GP
			String ydCarPntCd 		= commUtils.trim(jsCarPoint.getFieldString("YD_CARPNT_CD"));		//szYD_CARPNT_CD
			String ydStkColActStat 	= commUtils.trim(jsCarPoint.getFieldString("YD_STK_COL_ACT_STAT"));	//szYD_STK_COL_ACT_STAT
//			String chkWBook 		= commUtils.trim(jsCarPoint.getFieldString("CHK_WBOOK"));			//szCHK_WBOOK
			String WlocCd 		    = commUtils.trim(jsCarPoint.getFieldString("WLOC_CD"));				//개소코드
			String ydFrmYn			= commUtils.nvl(jsCarPoint.getFieldString("YD_FRM_YN"),"N");		//차량형상사용유무
			
			if (!"".equals(sCarNo))
			{
				commUtils.printLog(logId, methodNm + "해당위치  야드포인트가 사용불가. 현재차량 ["+sCarNo+"]" , "SL");
				return jrRtn;
			}
			
			if (!"C".equals(ydStkColActStat))
			{
				commUtils.printLog(logId, methodNm + "해당위치  야드포인트가 사용불가." , "SL");
				return jrRtn;
			}
			
			if (!"MT".equals(ydCarUseTypeGp))
			{
				commUtils.printLog(logId, methodNm + "해당위치  현재 포인트 타입 ["+ydCarUseTypeGp+"]" , "SL");
				return jrRtn;
    		}
			
			//스케줄 코드 생성
			String ydSchCd			= "";
			String transOrdDate		= "";
			String transOrdSeqNo	= "";
			String ydCarSchId		= "";

			JDTORecordSet jsWBook = JDTORecordFactory.getInstance().createRecordSet("temp");			
			JDTORecord    jrWBook = JDTORecordFactory.getInstance().create();		
			
			/**********************************************************
			* 3. 상차작업일 경우
			*    - 차량 상차 작업예약 정보 GET
			**********************************************************/
			if ("1".equals(CarUoDnGp))
			{ 
				//상차
				if ("1".equals(ydLoadLoc.substring(5 , 6)) || "2".equals(ydLoadLoc.substring(5 , 6)))
				{
					ydSchCd  = "1"+ydLoadLoc.substring(1,2) + "PT31UM"; //LEFT
				}
				else
				{
					ydSchCd  = "1"+ydLoadLoc.substring(1,2) + "PT32UM"; //RIGHT
				}
				
				jrParam.setField("YD_SCH_CD",	ydSchCd);
				jsWBook = commDao.select(jrParam, getYdWrkbookBySchCd2, logId, methodNm, "작업예약정보조회"); 
				
				if (jsWBook.size() == 0)
				{
					commUtils.printLog(logId, methodNm + "작업예약정보 없음 " +ydLoadLoc , "SL");
					return jrRtn;
				}
			} 
			
			/**********************************************************
			* 4. 하차작업일 경우
			*    - 차량 하차 작업예약 정보 GET
			**********************************************************/
			else 
			{
				//하차
				if ("1".equals(ydLoadLoc.substring(5 , 6)) || "2".equals(ydLoadLoc.substring(5 , 6)))
				{
					ydSchCd  = "1" + ydLoadLoc.substring(1,2) + "PT31LM"; //LEFT
				}
				else
				{
					ydSchCd  = "1" + ydLoadLoc.substring(1,2) + "PT32LM"; //RIGHT
				}
				
				jrParam.setField("YD_SCH_CD",	ydSchCd);
				jsWBook = commDao.select(jrParam, getYdWrkbookBySchCd, logId, methodNm, "작업예약정보조회");
				
				if (jsWBook.size() == 0)
				{
					commUtils.printLog(logId, methodNm + "작업예약정보 없음 " +ydLoadLoc , "SL");
					//return jrRtn;
					
					commUtils.printLog(logId, methodNm + "하차작업예약 생성... " + ydLoadLoc , "SL");
					
					//하차지 작업 예약 생성
					JDTORecord recInTemp1 = JDTORecordFactory.getInstance().create();
					recInTemp1.setField("YD_SCH_CD", ydSchCd);
					JDTORecordSet jsResult = commDao.select(recInTemp1, getYdSchrule, logId, methodNm, "스케줄 기준 조회");
			    	
					if(jsResult != null && jsResult.size() > 0)
					{
						ydSchPriorNew = jsResult.getRecord(0).getFieldString("YD_WRK_CRN_PRIOR"); //야드스케쥴우선순위
						ydEqpIdNew    = jsResult.getRecord(0).getFieldString("YD_WRK_CRN");
					}
					
					jrParam.setField("CAR_NO", CarNo);
					JDTORecordSet jsCarFtmvMtl = commDao.select(jrParam, getCarFtmvMtl, logId, methodNm, "TB_YD_CARFTMVMTL 대상재 조회");
			    	
					if(jsCarFtmvMtl.size() > 0)
					{
						//작업예약ID 조회
						String ydWbookIdNew = "";
						JDTORecord jrParam2		= JDTORecordFactory.getInstance().create();
						JDTORecord jrCarFtmvMtl	= JDTORecordFactory.getInstance().create();
						JDTORecord jrWbook    	= JDTORecordFactory.getInstance().create();
						JDTORecord jrWbookMtl 	= JDTORecordFactory.getInstance().create();
						
						for(int Loop_i = 1; Loop_i <= jsCarFtmvMtl.size() ; Loop_i++)
						{
							jsCarFtmvMtl.absolute(Loop_i);
							jrCarFtmvMtl = jsCarFtmvMtl.getRecord();
							
							String sStlNo		= commUtils.trim(jrCarFtmvMtl.getFieldString("STL_NO"));
							String sYdStkBedNo	= commUtils.trim(jrCarFtmvMtl.getFieldString("YD_STK_BED_NO"));
							
							//2020.04.22 허정욱책임 요청 차량동간이적 하차시 하차동이 아닌 다른동에 해당재료의 작업예약이 남아 있는경우 삭제처리
							jrParam2.setField("YD_BAY_GP",	ydLoadLoc.substring(1,2));	//하차동
							jrParam2.setField("STL_NO",		sStlNo);
							JDTORecordSet jsYdWbookIdDel = commDao.select(jrParam2, getNotDelYdWbookId, logId, methodNm, "차량동간이적 하차 작업예약 등록전 하차대상재가 다른동에 작업예약 있는지 조회");
							
							if(jsYdWbookIdDel.size() > 0)
							{
								jrParam2.setField("MODIFIER",		modifier);
								jrParam2.setField("YD_WBOOK_ID",	jsYdWbookIdDel.getRecord(0).getFieldString("YD_WBOOK_ID"));

								commDao.update(jrParam2, updDelYnWrkBookMtl, logId, methodNm, "TB_YF_WRKBOOKMTL DEL_YN = 'Y' ");	//작업예약재료 삭제

								commDao.update(jrParam2, updDelYnWrkBook, logId, methodNm, "TB_YF_WRKBOOK DEL_YN = 'Y' ");			//작업예약 삭제
							}
							
							ydWbookIdNew = commDao.getSeqId(logId, methodNm, "WrkBook");	//YD_WRKBOOK_ID 생성
							
							if(!"".equals(ydWbookIdNew))
							{
								jrWbook    = JDTORecordFactory.getInstance().create();
								//작업예약 등록
								jrWbook.setField("YD_WBOOK_ID",			ydWbookIdNew);	//야드작업예약ID
								jrWbook.setField("MODIFIER",			modifier);		//수정자
								jrWbook.setField("YD_GP",				"1");			//야드구분
								jrWbook.setField("YD_BAY_GP",			ydLoadLoc.substring(1, 2));	//야드동구분
								jrWbook.setField("YD_SCH_CD",			ydSchCd);		//야드스케쥴코드
								jrWbook.setField("YD_SCH_PRIOR",		ydSchPriorNew);	//야드스케쥴우선순위
								jrWbook.setField("YD_SCH_PROG_STAT",	"W");			//야드스케쥴진행상태(스케줄수행대기)
								jrWbook.setField("YD_SCH_ST_GP",		"O");			//야드스케쥴기동구분(Manual)
								jrWbook.setField("YD_SCH_REQ_GP",		"M");			//야드스케쥴요청구분(이적)
								jrWbook.setField("YD_WRK_PLAN_CRN",		ydEqpIdNew);	//작업예약 크레인
								jrWbook.setField("CAR_NO",				CarNo);
						    	//PIDEV				
//								if("Y".equals(sApplyYnPI)) {
									jrWbook.setField("CARD_NO",				CarNo);
//								}
								jrWbook.setField("YD_CAR_USE_GP",		"G");
						    	//PIDEV	
								int ins_cnt = 0;
//								if("Y".equals(sApplyYnPI)) {
									ins_cnt = commDao.insert(jrWbook, insWrkBook2_PIDEV, logId, methodNm, "TB_YF_WRKBOOK 등록");
//								} else {
//									ins_cnt = commDao.insert(jrWbook, insWrkBook2, logId, methodNm, "TB_YF_WRKBOOK 등록");	
//								}
								
								if(ins_cnt > 0)
								{
									jrWbookMtl = JDTORecordFactory.getInstance().create();
									//작업예약 등록
									jrWbookMtl.setField("YD_WBOOK_ID",		ydWbookIdNew);	//야드작업예약ID
									jrWbookMtl.setField("MODIFIER",			modifier);		//수정자
									jrWbookMtl.setField("STL_NO",			sStlNo);
									jrWbookMtl.setField("YD_STK_COL_GP",	ydLoadLoc.substring(0, 6));
									jrWbookMtl.setField("YD_STK_BED_NO",	sYdStkBedNo);
	
									commDao.insert(jrWbookMtl, insYfWrkBookMtl, logId, methodNm, "작업예약재료(TB_YF_WRKBOOKMTL) 생성");
								}
							}
						}
					}
					
					jrParam.setField("YD_SCH_CD",	ydSchCd);
					jsWBook = commDao.select(jrParam, getYdWrkbookBySchCd, logId, methodNm, "작업예약정보 생성 후 재조회");
				}
			}
			
			/**********************************************************
			* 5. 해당 포인트 점유
			**********************************************************/
			jrParam.setField("YD_STK_COL_ACT_STAT",	"L");
			jrParam.setField("TRN_EQP_CD",			"");
			jrParam.setField("CAR_NO",				CarNo);
	    	// PIDEV				
//			if("Y".equals(sApplyYnPI)) {
				jrParam.setField("CARD_NO",				CarNo);
//			}
			jrParam.setField("YD_CAR_USE_GP",		"G");
			jrParam.setField("YD_STK_COL_GP",		ydLoadLoc);
			jrParam.setField("YD_STK_COL_GP",		ydLoadLoc);
			jrParam.setField("YD_MAKECARPNT_CD",	ydCarPntCd);		
			
	    	//PIDEV				
//			if("Y".equals(sApplyYnPI)) {
			    //차량 POINT TABLE 점유
				commDao.update(jrParam, updYdCarpoint, logId, methodNm, "Car-Point 등록");
				
				/**********************************************************
				* 6. 적치열 테이블에 활성상태 처리 
				**********************************************************/
		    	commDao.update(jrParam, updYmStackcol_PIDEV, logId, methodNm, "TB_YF_STKCOL 수정");
				
//			} else {
//			    //차량 POINT TABLE 점유
//				commDao.update(jrParam, updYdCarpoint, logId, methodNm, "Car-Point 등록");
//				
//				/**********************************************************
//				* 6. 적치열 테이블에 활성상태 처리 
//				**********************************************************/
//		    	commDao.update(jrParam, updYmStackcol, logId, methodNm, "TB_YF_STKCOL 수정");
//			}
				
	    	/**********************************************************
			* 7. 적치베드 테이블에 활성상태 처리 
			**********************************************************/
	    	jrParam.setField("YD_STK_BED_WT_MAX", "100000");
    		jrParam.setField("YD_STK_BED_ACTIVE_STAT", "L");
			commDao.update(jrParam, updYdStkbedYdStkColGp, logId, methodNm, "TB_YF_STKBED 활성상태수정(E)");
			
			/**********************************************************
			* 8. 상차 작업
			**********************************************************/
			if ("1".equals(CarUoDnGp))
			{
				/**********************************************************
				* 8-1. 적치단 활성화
				**********************************************************/
	    		jrParam.setField("YD_STK_LYR_ACTIVE_STAT",	"E");
	    		jrParam.setField("STL_NO",					"");
	    		jrParam.setField("YD_STK_LYR_STAT",			"E");
	    		commDao.update(jrParam, updYdStkLyrYdStkColGpClear, logId, methodNm, "TB_YF_STKLYR 차량 적치단 정보 활성화(E)");
	
				/**********************************************************
				* 8-2. 운송지시번호 생성
				**********************************************************/
				JDTORecord jsTransSeq = JDTORecordFactory.getInstance().create();
				JDTORecordSet jrTransSeq = commDao.select(jrParam, getYdGetCarNoTypeByLoc2, logId, methodNm, "운송지시정보조회"); 
				
				if (jrTransSeq.size() == 0)
				{
					throw new Exception("운송지시일자,순번  생성시  오류발생");
				}
				
				jrTransSeq.first();
				jsTransSeq		= jrTransSeq.getRecord();
				
				transOrdDate	= commUtils.nvl(jsTransSeq.getFieldString("TRANS_ORD_DATE"),	"0");
				transOrdSeqNo	= commUtils.nvl(jsTransSeq.getFieldString("TRANS_ORD_SEQNO"),	"0");
				
				jrParam.setField("TRANS_ORD_DATE",		transOrdDate);
				jrParam.setField("TRANS_ORD_SEQNO",		transOrdSeqNo);
				
				/**********************************************************
				* 8-3. 저장품 갱신
				*      - 이송지시일자, 순번
				**********************************************************/
				for(int Loop_i = 1; Loop_i <= jsWBook.size() ; Loop_i++)
				{
					jsWBook.absolute(Loop_i);
					jrWBook = jsWBook.getRecord();
					
					jrParam.setField("TRANS_ORD_DATE",	transOrdDate);
					jrParam.setField("TRANS_ORD_SEQNO",	transOrdSeqNo);
					jrParam.setField("STL_NO",			commUtils.trim(jrWBook.getFieldString("STL_NO")));
			    	// PIDEV				
//					if("Y".equals(sApplyYnPI)) {
						commDao.update(jrParam, updateStockTransInfo_01_PIDEV, logId, methodNm, "TB_YF_STOCK 에 차량 카드번호 등록");
//					} else {
//						commDao.update(jrParam, updateStockTransInfo_01, logId, methodNm, "TB_YF_STOCK 에 차량 카드번호 등록");
//					}
				}
				
				ydCarSchId	= commDao.getSeqId(logId, methodNm, "CarSch");				
				
				/**********************************************************
				* 8-4. 차량스케줄 등록
				**********************************************************/
				JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
				jrInParam.setField("YD_CAR_SCH_ID",			ydCarSchId);					//야드차량스케쥴ID
				jrInParam.setField("MODIFIER",				modifier);
				jrInParam.setField("YD_EQP_ID",				YfConstant.YD_DM_CAR_EQP_ID);	//야드설비ID 
				jrInParam.setField("YD_CAR_USE_GP",			YfConstant.YD_CAR_USE_GP_DM);	//차량사용구분('G': 출하?)
				jrInParam.setField("CAR_NO",				CarNo);							//운송장비코드
				jrInParam.setField("CAR_KIND",				"TR");							//차량종류
				jrInParam.setField("YD_EQP_WRK_STAT",		"U");							//야드설비작업상태
				
				jrInParam.setField("CARD_NO",				CarNo);							//운송장비코드
				jrInParam.setField("SPOS_WLOC_CD",			WlocCd);						//발지개소코드
				jrInParam.setField("ARR_WLOC_CD",			WlocCd);						//착지개소코드
				
				jrInParam.setField("YD_CARLD_LEV_LOC",		ydLoadLoc);						//야드상차출발위치
				jrInParam.setField("YD_PNT_CD1",			ydCarPntCd);
//				jrInParam.setField("YD_CARLD_WRK_BOOK_ID",	ydWbookId);     			 	//상차 작업예약ID
				jrInParam.setField("YD_CARLD_STOP_LOC",		ydLoadLoc);						//야드상차정지위치 (직상차 제외)

				jrInParam.setField("YD_CAR_PROG_STAT",		YfConstant.YD_CAR_PROG_STAT_2);	//상차출발상태
				jrInParam.setField("TRANS_ORD_DATE",		transOrdDate);					//운송지시번호
				jrInParam.setField("TRANS_ORD_SEQNO",		transOrdSeqNo);					//운송지시번호
				jrInParam.setField("YD_BAYIN_WO_SEQ",		"9");							//입동지시순번 - 기본값으로 설정(9)
				
				commDao.insert(jrInParam, insCarSch, logId, methodNm, "TB_YD_CARSCH 차량스케쥴 상차도착 으로 INSERT");
			}
			
			/**********************************************************
			* 9. 하차 작업
			**********************************************************/
			else 
			{
				/**********************************************************
				* 9-1. 차량 스케줄 상태 변경(하차도착)
				**********************************************************/
				jsWBook.first();
				jrWBook		= jsWBook.getRecord();
				
				ydCarSchId  = commUtils.nvl(jrWBook.getFieldString("YD_CAR_SCH_ID"), "0");

				JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
				jrInParam.setResultCode(logId);		//Log ID
				jrInParam.setResultMsg(methodNm);	//Log Method Name
				jrInParam.setField("MODIFIER",				modifier);	//수정자
				jrInParam.setField("YD_CAR_SCH_ID",			ydCarSchId);
	 			jrInParam.setField("YD_CARUD_ARR_DT",		commUtils.getDateTime14());
	 			jrInParam.setField("YD_CAR_PROG_STAT",		"B");		//하차도착상태
	 			jrInParam.setField("ARR_WLOC_CD",			WlocCd);		
	 			jrInParam.setField("YD_PNT_CD3",			ydCarPntCd);
	 			jrInParam.setField("YD_CARUD_STOP_LOC",		ydLoadLoc);	
	 			jrInParam.setField("YD_EQP_WRK_STAT",		"L");		//L:영차 U:공차
				commDao.update(jrInParam, updYdCarschYD, logId, methodNm, "TB_YD_CARSCH 차량 하차도착");
				
				/**********************************************************
				* 9-2. 상차 가능 작업예약 만큼 적치단에 적치
				**********************************************************/
				for(int Loop_i = 1; Loop_i <= jsWBook.size() ; Loop_i++)
				{
					jsWBook.absolute(Loop_i);
					jrWBook = jsWBook.getRecord();
					
					// 하차인 경우: 저장위치에 재료 정보 SET
					JDTORecord recInTemp  = JDTORecordFactory.getInstance().create();
			    	recInTemp.setField("YD_STK_COL_GP",				ydLoadLoc);
			    	recInTemp.setField("YD_STK_BED_NO",				jrWBook.getFieldString("YD_STK_BED_NO"));
			    	recInTemp.setField("YD_STK_LYR_NO",				"01");
			    	recInTemp.setField("STL_NO",					jrWBook.getFieldString("STL_NO"));
			    	recInTemp.setField("YD_STK_LYR_ACTIVE_STAT",	"E");
			    	recInTemp.setField("YD_STK_LYR_STAT",			"C");
			    	recInTemp.setField("MODIFIER",					modifier);
					commDao.update(recInTemp, updYdStkLyrYdStkColBedGp, logId, methodNm, "TB_YF_STKLYR 등록");
				}
			}
			
			/**********************************************************
			* 10. 차량예정정보 백업 송신(YFF1L008)
			**********************************************************/
			//2020.02.12 정종균과장요청 형상유무 사용인 경우에만 차량예정정보 송신
			//if("Y".equals(ydFrmYn))
			//{
				if ("1".equals(CarUoDnGp))
				{
					//차량작업 예정정보 전문 data setup
				    jrParam.setField("PT_LOAD_LOC",	ydLoadLoc);	// 상차도 위치				
				    jrParam.setField("CAR_NO",		CarNo);		// 차량번호	
				    jrParam.setField("CARD_NO",		CarNo);		// 차량번호	
				    jrParam.setField("PT_CLS",		"TR");		// 차량구분				
				    jrParam.setField("WORK_CLS",	"4");		// 작업구분 4:구내출고, 3:구내입고  				
				    
				    for (int ii = 0; ii < jsWBook.size(); ii++)
				    {	
						jsWBook.absolute(ii+1);
						jrWBook = jsWBook.getRecord();
				    	
				    	jrParam.setField("STL_NO_" + ii,		commUtils.trim(jrWBook.getFieldString("STL_NO"))); 
				    	jrParam.setField("LOAD_LOC_CD_" + ii,	"0" + (ii + 1));	//차량적재위치
				    	jrParam.setField("WORK_STATE_" + ii,	"0");				//작업상태  0(작업예정),*(작업완료) -> 0(작업대상재), *(비작업대상재) 
					    jrParam.setField("WORK_COIL_MAX_CNT",	"" + (ii + 1));		//작업총 수량 
				    }
					
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L008BackUp", jrParam));
				}
				else
				{
					//jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YFF1L008", jrParam));
					jrParam.setField("YD_CAR_SCH_ID",	ydCarSchId);	//야드차량스케쥴ID
					jrParam.setField("SEARCH_FLAG",		"2");			//1:상차도, 2:차량스케쥴 ID
					jrRtn = commUtils.addSndData(jrRtn, yfComm.procCarPlanInfo(jrParam));	//YFF1L008
				}
			//}
			
			/**********************************************************
			* 11. 저장품위치제원 전문 송신(YFF1L001)
			**********************************************************/
			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();
			sndL2Msg.setResultCode(logId);		//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.setField("YD_INFO_SYNC_CD",	"3"); //야드정보동기화코드
			sndL2Msg.setField("MSG_GP",				"I"); //전문구분
			sndL2Msg.setField("YD_STK_COL_GP",		ydLoadLoc);
			sndL2Msg.setField("YD_STK_BED_NO",		"");
 
			//전송 Data 생성
			jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YFF1L001", sndL2Msg));
			
			/**********************************************************
			* 12. 크레인스케줄 전문 호출
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			for(int Loop_i = 1; Loop_i <= jsWBook.size() ; Loop_i++)
			{
				jsWBook.absolute(Loop_i);
				jrWBook = jsWBook.getRecord();
			
				jrYdMsg.setField("YD_WBOOK_ID",		jrWBook.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD",		ydSchCd);	//야드스케쥴코드
				jrYdMsg.setField("YD_SCH_ST_GP",	"O");		//야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP",	"L");		//야드스케쥴요청구분(인출)
				jrYdMsg.setField("MODIFIER",		modifier);	//수정자

				jrRtn = commUtils.addSndData(jrRtn, yfComm.getCrnSchMsg(jrYdMsg));
			}
			
			commUtils.printParam(logId, jrRtn);
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		}
		catch (DAOException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 자동이적정보 요구(F1YFL019)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL019(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "자동이적정보요구[ACoilRcvL2SeEJB.rcvF1YFL019] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용	
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydEqpWrkSh   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_SH"));  //작업매수
			int    ydCrnXaxis   = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_CRN_XAXIS"),"0")); //야드크레인X축
			int    ydCrnYaxis   = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("YD_CRN_YAXIS"),"0")); //야드크레인Y축
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			String sYD_UP_WR_LOC   = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LOC"  )); //야드권상실적위치
			String sYD_UP_WR_LYR   = commUtils.trim(rcvMsg.getFieldString("YD_UP_WR_LYR")); //야드권상실적단
			String sYD_UP_LOC      = sYD_UP_WR_LOC + sYD_UP_WR_LYR;
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "J"          ); //야드L2실적구분(지시요구)
			resMsg.setField("YD_L3_HD_RS_CD"  , "9999"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			}

			if (ydCrnXaxis == 0) {
				throw new Exception("정보이상 이상 [" + ydEqpId + "] : 위치값 없음");
			}
			if (ydCrnYaxis == 0) {
				throw new Exception("정보이상 이상 [" + ydEqpId + "] : 위치값 없음");
			}

        	
			
			/**********************************************************
			* 2. 야드설비상태 Check	
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    	, ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_XAXIS"    	, ""+ydCrnXaxis   ); 
			jrParam.setField("YD_CRN_YAXIS"    	, ""+ydCrnYaxis   ); 
			jrParam.setField("MODIFIER"         , modifier  ); //수정자
      	
			JDTORecord jrChk = yfComm.chkEqpStat(jrParam);

			String ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));//기존 C:'8888')
			String ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg + "에러:설비상태가 이적할수 없습니다"   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			

			JDTORecordSet jsCrnLoc = null;
			
			System.out.println("------------------------------> ["+sYD_UP_LOC+"] ,"+sYD_UP_LOC.length());
			/**********************************************************
			* 3. 권상실적논리 좌표 GET
			**********************************************************/
			if (sYD_UP_LOC.length() == 11)  // 논리좌표항목이 전송되었을 때 
			{
				jrParam.setField("YD_UP_WR_LOC"  , sYD_UP_WR_LOC  );
				jrParam.setField("YD_UP_WR_LYR", sYD_UP_WR_LYR);
				jsCrnLoc = commDao.select(jrParam, getYdStklyrAxisXYZByLoc, logId, methodNm, "좌표에 해당하는 저장품 조회");
			} 
			else  // 물리좌표항목이 전송되었을 때 
			{
				jsCrnLoc = commDao.select(jrParam, getYdStklyrAxisXYZ, logId, methodNm, "좌표에 해당하는 저장품 조회");
			}

			String ydWbookId = "";
			String ydSchCd   = "";
			
			
			/**********************************************************
			* 4. 합당한 권상 실적 좌표가 없거나 1개 이상이라면 에러
			**********************************************************/
    		if ( jsCrnLoc.size() <= 0 ) 
    		{
   			/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    	         * 업무기준 Desc :에서 크레인 작업지시 요구 시 크레인 작업이 더 이상 존재하지 않을 경우 크레인작업실적응답 전송
    	       +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
				resMsg.setField("YD_L3_HD_RS_CD", "E010"); //야드L3처리결과코드
				//resMsg.setField("YD_L3_MSG"     , ydL3Msg + "에러:저장품을 찾을 수 없습니다.["+ydCrnXaxis+", "+ ydCrnYaxis+"]"); //야드L3MESSAGE
				resMsg.setField("YD_L3_MSG"     , ydL3Msg + "에러:저장품 없음["+ydCrnXaxis+", "+ ydCrnYaxis+"]"); //야드L3MESSAGE
				throw new Exception("에러:저장품 없음["+ydCrnXaxis+", "+ ydCrnYaxis+"]");

    		} 
    		else if ( jsCrnLoc.size() > 1) 
    		{
				resMsg.setField("YD_L3_HD_RS_CD", "E020"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg + "에러:1곳이상 야드저장위치 논리좌표 존재"); //야드L3MESSAGE
				throw new Exception("에러:1곳이상 야드저장위치 논리좌표 존재");
    		} 
    		
    		/**********************************************************
			* 5. 합당한 권상 실적 좌표가 1개 존재한다면
			**********************************************************/
    		else 
    		{
    			jsCrnLoc.absolute(1);
    			JDTORecord jrOutTemp = JDTORecordFactory.getInstance().create();
    			jrOutTemp.setRecord(jsCrnLoc.getRecord());
    			String ydStkColGp  = commUtils.trim(jrOutTemp.getFieldString("YD_STK_COL_GP"));
    			String ydStkBedGp  = commUtils.trim(jrOutTemp.getFieldString("YD_STK_BED_NO"));
    			String ydUpWrLayer = commUtils.trim(jrOutTemp.getFieldString("YD_STK_LYR_NO"));
    			
    			
    			JDTORecord lrParam = commUtils.getParam(logId, methodNm, "");
    			lrParam.setField("REPR_CD_GP", "SCH001");
    			lrParam.setField("CD_GP"     , "LTRT_RULE");
    			lrParam.setField("ITEM"      , ydStkColGp.substring(1,2));
    			JDTORecordSet rsResult = commDao.select(lrParam, getYfRule, logId, methodNm, "스케줄코드 좌우기준 조회");
    			
    			if (rsResult.size() <= 0 ) {
    				throw new Exception("해당 동의 스케줄코드 좌우 구분이 없습니다.");
    			}
    			
    			int nLtRtRule   = Integer.parseInt(rsResult.getRecord(0).getFieldString("DTL_ITEM1"));
    			int nSECT_GP = Integer.parseInt(ydStkColGp.substring(2,4));
    			String lrGp = "";
    			if (nSECT_GP < nLtRtRule) {
    				lrGp = "1";
    			} else if (nSECT_GP >= nLtRtRule) {
    				lrGp = "2";
    			}
    			
    			ydSchCd = "1"+ ydStkColGp.substring(1,2) + "YD0" + lrGp+ "MM";  
    			
    			/**********************************************************
    			* 6. 작업예약,작업재료 등록	
    			**********************************************************/
    			jrOutTemp.setField("STL_NO"            , commUtils.trim(jrOutTemp.getFieldString("STL_NO"))); //재료번호
    			jrOutTemp.setField("YD_STK_COL_GP"     , ydStkColGp ); 
    			jrOutTemp.setField("YD_STK_BED_NO"     , ydStkBedGp ); 
    			jrOutTemp.setField("YD_STK_LYR_NO"     , ydUpWrLayer); 
    			jrOutTemp.setField("YD_SCH_CD"         , ydSchCd    );// 자동이적 
    			jrOutTemp.setField("MODIFIER"          , modifier   ); //수정자
    			jrOutTemp.setField("YD_EQP_ID"         , ydEqpId    ); //설비
    			
    			ydWbookId = yfComm.procWkBookInsert(jrOutTemp);
    			
    			if (ydWbookId.equals(YmConstant.RETN_CD_FAILURE)) {
    				resMsg.setField("YD_L3_HD_RS_CD", "E030"); //야드L3처리결과코드
    				resMsg.setField("YD_L3_MSG"     , ydL3Msg + "에러:작업예약 생성 실패"   ); //야드L3MESSAGE
    				throw new Exception("에러:작업예약 생성 실패");  				
    			}


    			
    			/**********************************************************
    			* 7. 해당 크레인에 이미 작업중인 스케줄이 있다면 	
    			**********************************************************/
    			JDTORecordSet jsCrn = commDao.select(jrParam, getCrnWrkMgtPriorWrk1, logId, methodNm, "기존크레인 작업 조회");
    			if (jsCrn != null && jsCrn.size() > 0)
    			{
    				// 기존 작업 
    			    JDTORecord jrCrn = jsCrn.getRecord(0);
    			    String ydCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"));
    			    
    				
  
    			    /**********************************************************
        			* 7-1. 기존작업 우선순위 조정
        			**********************************************************/
    			    jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchIdWrk);
    				commDao.update(jrParam, updCrnWrkMgtPriorWrkNext1, logId, methodNm,  "TB_YF_CRNSCH");
    				
    				/**********************************************************
        			* 7-2. 설비 상태 대기로 수정
        			**********************************************************/
    				jrParam.setField("YD_EQP_PROG_STAT", "W");
    				jrParam.setField("YD_EQP_ID"  , ydEqpId);
    				commDao.update(jrParam, updEqpOprnStat);
    			}
    			
    			/**********************************************************
    			* 8. 크레인스케줄 전문 호출
    			**********************************************************/
    			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
    			jrYdMsg.setResultCode(logId);	//Log ID
    			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

    			jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
    			jrYdMsg.setField("YD_SCH_CD"    , ydSchCd  ); //야드스케쥴코드
    			jrYdMsg.setField("YD_SCH_ST_GP" , "O"      ); //야드스케쥴기동구분
    			jrYdMsg.setField("YD_SCH_REQ_GP", "L"      ); //야드스케쥴요청구분(인출)
    			jrYdMsg.setField("MODIFIER"     , modifier ); //수정자
    			jrYdMsg.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID

    			jrRtn = commUtils.addSndData(jrRtn, yfComm.getCrnSchMsg(jrYdMsg));
    			
    		}	

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				//chito : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}				
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YmCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { this.getYFF1L005(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 스크랩차량 차단기 정보(F1YFL028)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL028(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "스크랩차량 차단기 정보[ACoilRcvL2SeEJB.rcvF1YFL028] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");
			// PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "스크랩차량 차단기 정보[ACoilRcvL2SeEJB.rcvF1YFL028]", "APPPI0", "1", "*");
			
			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String sYD_EQP_ID = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //1EGT01
			String sCAR_YN    = commUtils.trim(rcvMsg.getFieldString("CAR_YN"   )); //0 차량없음 1차량있음
			String modifier   = commUtils.trim(rcvMsg.getFieldString("MODIFIER" )); //수정자(Backup Only)
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sYD_EQP_ID)) {
				throw new Exception("수신한 설비코드가 없음");
			} else if (!"0".equals(sCAR_YN) && !"1".equals(sCAR_YN)) {
				throw new Exception("차량유무 코드 이상");
			}
			
			commUtils.printLog(logId, "CAR_YN = " + sCAR_YN, "[INFO]" );

			/**********************************************************
			* 2. 차량형상 완료시 스케줄 기동
			*    - 영차인지 공차인지? KBS
			**********************************************************/
			if ("PT".equals(sYD_EQP_ID.substring(2, 4))) {
				
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setResultCode(logId);	    //Log ID
	 			jrParam1.setResultMsg(methodNm);	//Log Method Name
				jrParam1.setField("YD_STK_COL_GP", sYD_EQP_ID);
				jrParam1.setField("MODIFIER"    , modifier  );	//수정자

				
				//차량형상 완료 표시
				jrParam1.setField("CAR_FRM_GP" , "Y");	//형상완료로 인한 스케줄 기동
				
				// PIDEV
//				if ("Y".equals(sApplyYnPI)) {
					commDao.update(jrParam1, updCarFrm2_PIDEV, logId, methodNm, "차량형상완료 표시");
//				} else {
//					commDao.update(jrParam1, updCarFrm2, logId, methodNm, "차량형상완료 표시");
//				}
				
				JDTORecordSet jsCarMtl = commDao.select(jrParam1, getCarWrkList, logId, methodNm, "작업예약 조회");
				
				commUtils.printLog(logId, "CAR_YN = " + sCAR_YN, "[INFO]" );
				
				
				if (jsCarMtl == null || jsCarMtl.size() == 0)
				{
					return jrRtn;
				}
				else
				{
					commUtils.printLog(logId, "SC_GP = " + jsCarMtl.getRecord(0).getFieldString("SC_GP"), "[INFO]" );
				
					if ("N".equals(jsCarMtl.getRecord(0).getFieldString("SC_GP")))//스케줄 기동 불필요(이미 스케줄 편성됨)
					{
						return jrRtn;
					}
				 
					if("F".equals(jsCarMtl.getRecord(0).getFieldString("SC_GP"))) //명령 선택 기동 필요
					{
						JDTORecord jr007 = JDTORecordFactory.getInstance().create();
						jr007.setResultCode(logId);	//Log ID
						jr007.setResultMsg(methodNm);	//Log Method Name
						jr007.setField("JMS_TC_CD",			"F1YFL007") ;	//크레인작업지시요구
						jr007.setField("YD_EQP_ID",			commUtils.trim(jsCarMtl.getRecord(0).getFieldString("YD_CRN_ID")));		//야드설비ID
						jr007.setField("YD_WBOOK_ID",		commUtils.trim(jsCarMtl.getRecord(0).getFieldString("YD_WBOOK_ID")));//동일 통로에서 1번지에 작업중인데, 2번지 형상실적으로, 1번지 작업지시를 다시 내리는 현상
	
						EJBConnector ejbConn = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
			    		jrRtn = (JDTORecord)ejbConn.trx("rcvF1YFL007", new Class[] { JDTORecord.class }, new Object[] { jr007 });
						
						return jrRtn;
					}
				}
				
				JDTORecord jrCrnSchMsg = JDTORecordFactory.getInstance().create();
				jrCrnSchMsg.setField("JMS_TC_CD"			, "YFYFJ303"); 
				jrCrnSchMsg.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrCrnSchMsg.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrCrnSchMsg.setField("YD_EQP_ID"  			, ""); //야드설비ID
				
				int pcnt = 0;
				
				//2단 적치된 대상 스케줄 호출
				for (int i = 0; i < jsCarMtl.size(); i++) {
					if ("02".equals(jsCarMtl.getRecord(i).getFieldString("YD_STK_LYR_NO"))) {
						jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
					}
				}
				
				//1단 적치된 대상 스케줄 호출
				for (int i = 0; i < jsCarMtl.size(); i++) {
					if ("01".equals(jsCarMtl.getRecord(i).getFieldString("YD_STK_LYR_NO"))) {
						jrCrnSchMsg.setField("YD_WBOOK_ID"+(++pcnt)	, jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
					}
				}
				
				// 
//				for (int i = 0; i < jsCarMtl.size(); i++) {
//					/*
//					UPDATE TB_YM_WRKBOOK
//					   SET YD_CTS_RELAY_YN = :V_YD_CTS_RELAY_YN
//					 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
//					 */
//					jrParam1.setField("YD_WBOOK_ID"     , jsCarMtl.getRecord(i).getFieldString("YD_WBOOK_ID"));
//					jrParam1.setField("YD_CTS_RELAY_YN" , "Y");	//형상완료로 인한 스케줄 기동
//					commDao.update(jrParam1, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updCarFrm", logId, methodNm, "TB_YM_WRKBOOK");
//				}
				
				jrCrnSchMsg.setField("SCH_CNT" , Integer.toString(pcnt));
				
				jrRtn = commUtils.addSndData(jrRtn, jrCrnSchMsg);
					
				return jrRtn;
				
			}
			

			/**********************************************************
			* 2. 해당 설비 테이블 수정 
			**********************************************************/
			if (!"GT".equals(sYD_EQP_ID.substring(2, 4))) {
				throw new Exception("수신한 설비코드가 이상");
			}
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_EQP_ID"    , sYD_EQP_ID);  //설비 
			jrParam.setField("CAR_YN" 	    , sCAR_YN   ); 	//차량 유무
			jrParam.setField("MODIFIER"     , modifier  );	//수정자
			commDao.update(jrParam, updEquipCarYn, logId, methodNm, "스크랩차량 상태 수정");
	
			
			/***********************************************************
			 * 3. 스크랩차량 출입시 스크랩스케줄 권하위치 수정(W대기 상태만 조회)
			 ***********************************************************/
			
			/*******************************
			 * L2작업지시 스케줄 처리
			 * - 권하위치 변경지시
			 *******************************/
			JDTORecordSet jsScrap = commDao.select(jrParam, getWrkScrpSchList, logId, methodNm, "스크랩스케줄 조회");
			if (jsScrap.size() > 0) {
				
				String sYD_DN_WO_LOC = "";
				
				jrParam.setField("STL_NO"       , jsScrap.getRecord(0).getFieldString("STL_NO"));
				jrParam.setField("YD_SCH_CD"    , jsScrap.getRecord(0).getFieldString("YD_SCH_CD"));
				jrParam.setField("YD_CRN_SCH_ID", jsScrap.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
				
				
				commUtils.printLog(logId, "STL_NO      = " + jsScrap.getRecord(0).getFieldString("STL_NO")     , "[INFO]");
				commUtils.printLog(logId, "YD_SCH_CD     = " + jsScrap.getRecord(0).getFieldString("YD_SCH_CD")    , "[INFO]");
				commUtils.printLog(logId, "YD_CRN_SCH_ID = " + jsScrap.getRecord(0).getFieldString("YD_CRN_SCH_ID"), "[INFO]");
				
				JDTORecordSet jsLOCInfo = commDao.select(jrParam, getYdPrimaryWorkSearchScrapNewRe, logId, methodNm, "권하위치 재조회");
				
				if (jsLOCInfo.size() > 0) {
					sYD_DN_WO_LOC = jsLOCInfo.getRecord(0).getFieldString("TAG_YD_STK_COL_GP")
					              + jsLOCInfo.getRecord(0).getFieldString("TAG_YD_STK_BED_NO")
					              + jsLOCInfo.getRecord(0).getFieldString("TAG_YD_STK_LYR_NO");
					commUtils.printLog(logId, "YD_DN_WO_LOC = " + sYD_DN_WO_LOC, "[INFO]");
					
					//권하위치 변경
					JDTORecord jrMsg = JDTORecordFactory.getInstance().create();
					jrMsg.setResultCode(logId);	//Log ID
					jrMsg.setResultMsg(methodNm);	//Log Method Name
					jrMsg.setField("STL_NO"        , jsScrap.getRecord(0).getFieldString("STL_NO"        )); //저장품
					jrMsg.setField("YD_EQP_ID"       , jsScrap.getRecord(0).getFieldString("YD_EQP_ID"       )); //야드설비ID(크레인)
					jrMsg.setField("YD_SCH_CD"       , jsScrap.getRecord(0).getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
					jrMsg.setField("YD_CRN_SCH_ID"   , jsScrap.getRecord(0).getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
					jrMsg.setField("YD_WBOOK_ID"     , jsScrap.getRecord(0).getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
					jrMsg.setField("YD_DN_WO_LOC"    , sYD_DN_WO_LOC                                          ); //야드권하지시위치(신규)
					jrMsg.setField("YD_WRK_PROG_STAT", jsScrap.getRecord(0).getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
					jrMsg.setField("YD_DN_WO_LOC_ORG", jsScrap.getRecord(0).getFieldString("YD_DN_WO_LOC_ORG")); //야드권하지시위치(기존)
					jrMsg.setField("MODIFIER"        , "F1YFL028"); //수정자
					EJBConnector ejbConn = new EJBConnector("default", "ACoilJspSeEJB", this);
					JDTORecord jrRst = (JDTORecord)ejbConn.trx("updCrnSchDnWoLoc", new Class[] { JDTORecord.class }, new Object[] { jrMsg });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRst);	
				} else {
					commUtils.printLog(logId, "신규 권하위치 검색 실패", "[INFO]");
				}
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	
	
	
	
	
	
	
	
    /**
	 *      [A] 오퍼레이션명 : 크레인주행금지구간(F1YFL030)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
    public JDTORecord rcvF1YFL030(JDTORecord rcvMsg) throws DAOException {
    	String methodNm = "크레인주행금지구간[ACoilRcvL2SeEJB.rcvF1YFL030] < " + rcvMsg.getResultMsg();
    	String logId = rcvMsg.getResultCode();
    	JDTORecord resMsg 	= JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn 		= true;
    	
    	try{
    		commUtils.printLog(logId, methodNm, "S+");
			
    		//Data수신 항목 값
			String msgId      		   = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			
			String StartEndGp		   = commUtils.trim(rcvMsg.getFieldString("START_END_GP")); //시작종료구분
			String ydGp 			   = commUtils.trim(rcvMsg.getFieldString("YD_GP"				   )); //야드구분
			String bayGp 			   = commUtils.trim(rcvMsg.getFieldString("BAY_GP"                 )); //야드동구분
			String TravlProhFromloc    = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_FROMLOC"     )); //야드주행금지FROM위치
			String TravlProhToloc 	   = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_TOLOC"       )); //야드주행금지TO위치
			String TravlProhFromxaxis  = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_FROMXAXIS"   )); //야드주행금지FROM위치X축
			String TravlProhToxaxis    = commUtils.trim(rcvMsg.getFieldString("TRAVL_PROH_TOXAXIS"     )); //야드주행금지TO위치X축
			
			//String ymCraneTravlProhSeq = commUtils.trim(rcvMsg.getFieldString("YM_CRANE_TRAVL_PROH_SEQ"				   )); 
			//String delyn 			   = commUtils.trim(rcvMsg.getFieldString("DEL_YN"				   )); //삭제처리유무
			//String register 		   = commUtils.trim(rcvMsg.getFieldString("REGISTER"			   )); //등록자
			String modifier 		   = commUtils.trim(rcvMsg.getFieldString("MODIFIER"               )); //수정자
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(StartEndGp)) {
				throw new Exception("시작종료구분(START_END_GP) 없음");
			}else if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			}else if ("".equals(bayGp)) {
				throw new Exception("야드동구분(BAY_GP) 없음");
			}else if ("".equals(TravlProhFromloc)) {
				throw new Exception("야드주행금지FROM위치(TRAVL_PROH_FROMLOC) 없음");
			}else if ("".equals(TravlProhToloc)) {
				throw new Exception("야드주행금지TO위치(TRAVL_PROH_TOLOC) 없음");
			}else if ("".equals(TravlProhFromxaxis)) {
				throw new Exception("야드주행금지FROM위치X축(TRAVL_PROH_FROMXAXIS) 없음");
			}else if ("".equals(TravlProhToxaxis)) {
				throw new Exception("야드주행금지TO위치X축(TRAVL_PROH_TOXAXIS) 없음");
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			/**********************************************************
			* 2. 크레인주행금지구간(F1YFL030) SEQ SELECT
			**********************************************************/
			jrParam.setField("YD_GP", ydGp                             );
			jrParam.setField("BAY_GP", bayGp                           );
			jrParam.setField("TRAVL_PROH_FROMLOC", TravlProhFromloc    );
			jrParam.setField("TRAVL_PROH_TOLOC", TravlProhToloc        );
			JDTORecordSet jrList = commDao.select(jrParam, getCRANETRAVLPROHList, logId, methodNm, "크레인주행금지구간 조회");	
			
			String sSeq = "";
			
			if (jrList.size() > 0) {
				sSeq = jrList.getRecord(0).getFieldString("YM_CRANE_TRAVL_PROH_SEQ");
			}
			
			jrParam.setField("YM_CRANE_TRAVL_PROH_SEQ", sSeq    );
			
			jrParam.setField("TRAVL_PROH_FROMXAXIS", TravlProhFromxaxis);
			jrParam.setField("TRAVL_PROH_TOXAXIS", TravlProhToxaxis    );
			
			if ("S".equals(StartEndGp)) {
				jrParam.setField("DEL_YN", "N"    ); 
			}else if ("E".equals(StartEndGp)) {
				jrParam.setField("DEL_YN", "Y"    );
			}
			jrParam.setField("REGISTER", "F1YFL030");
			jrParam.setField("MODIFIER", "F1YFL030");
			commDao.update(jrParam, updCRANETRAVLPROH, logId, methodNm, "크레인주행금지구간 수정 및 등록");
			
			
			/**********************************************************
			* 3. 크레인작업실적응답 전문 전송(YFF1L005)
			**********************************************************/
			commUtils.printLog(logId,"확인:"+resYn, "SL");
			if (resYn) {
				commUtils.printLog(logId,"확인:"+resYn, "SL");
				resMsg.setResultCode(logId);				//Log ID
				resMsg.setField("YD_L3_HD_RS_CD", "0000");	//야드L3처리결과코드(정상)
				resMsg.setField("BAY_GP", bayGp);
				
				resMsg.setField("A", "1ACRA2"); //A동 대표크레인
				resMsg.setField("C", "1CCRC1"); //C동 대표크레인
				resMsg.setField("E", "1ECRE1"); //E동 대표크레인
				jrRtn = commUtils.addSndData(jrRtn, this.getYFF1L005_recv(resMsg));
			}
			commUtils.printLog(logId,"확인:"+bayGp, "SL");
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn; 
		} catch (Exception e) {
			if (resYn) {
				try {
					resMsg.setResultCode(logId);
					//resMsg.setField("YD_L3_HD_RS_CD", "9999"); //야드L3처리결과코드(오류)
					//chito : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YfCommSeEJB", this);   //yfComm.ACoilApplyYn
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { this.getYFF1L005_recv(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//End rcvA7YML030
    
    
    
    
    
    
    /**
	 *      [A] 오퍼레이션명 : 박판열연크레인작업실적응답(YFF1L005) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYFF1L005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업실적응답 조회[ACoilRcvL2SeEJB.getYFF1L005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("1")) {
				msgId = "YFF1L005";
			} else {
				return null;
			}

			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			if ("".equals(ydL3Msg)) {
				if ("U".equals(ydL2WrGp)) {
					ydL3Msg = "권상실적";
				} else if ("D".equals(ydL2WrGp)) {
					ydL3Msg = "권하실적";
				} else if ("E".equals(ydL2WrGp)) {
					ydL3Msg = "비상조업실적";
				} else if ("R".equals(ydL2WrGp)) {
					ydL3Msg = "고장복구실적";
				} else if ("M".equals(ydL2WrGp)) {
					ydL3Msg = "운전모드전환";
				} else if ("J".equals(ydL2WrGp)) {
					ydL3Msg = "지시요구";
				} else if ("F".equals(ydL2WrGp)) {
					ydL3Msg = "강제권하";
				} else if ("G".equals(ydL2WrGp)) {
					ydL3Msg = "강제권상요구";
				} else {
					ydL3Msg = ydL2WrGp;
				}

				if ("0000".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정상 처리";
				} else if ("9999".equals(ydL3HdRsCd)) {
					ydL3Msg = ydL3Msg + " 정보 없음";
				} else {
					ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
				}
			}

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                 ); //전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()             ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                   ); //전문구분
			sbMsg = sbMsg.append("0078"                                ); //전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 29, " ")); //임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydEqpId   ,  6, " ")); //야드설비ID
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")),  1, " ")); //야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )),  8, " ")); //야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )), 18, " ")); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(ydL2WrGp  ,  1, " ")); //야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")); //야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")); //야드L3Message

			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

			sndL2Msg.setResultCode(logId);	//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndL2Msg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndL2Msg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndL2Msg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 크레인주행금지구간작업실적응답(getYFF1L005_recv) 전문 조회
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYFF1L005_recv(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인주행금지구간작업실적응답 조회[ACoilRcvL2SeEJB.getYFF1L005_recv] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		 
		try {
			//수신 항목 값 
			String msgId      = "YFF1L005"; //전문ID
			
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD"));//야드L3처리결과코드
			String ydL3Msg    = "";														//야드L3처리결과메세지
			
			String ydBayGP = commUtils.trim(rcvMsg.getFieldString("BAY_GP"));
			String ydRepA     = commUtils.trim(rcvMsg.getFieldString("A"        ));//A동 대표크레인
			String ydRepC     = commUtils.trim(rcvMsg.getFieldString("C"        ));//C동 대표크레인
			String ydRepE     = commUtils.trim(rcvMsg.getFieldString("E"        ));//E동 대표크레인
			
			
			if ("0000".equals(ydL3HdRsCd)) {
				ydL3Msg = ydL3Msg + "주행금지구역 설정 처리완료";
			} else {
				ydL3Msg = ydL3Msg + " 오류 <" + logId + ">";
			}
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			// 없음
			
			
			/**********************************************************
			* 2. 크레인작업실적응답 전문 생성
			**********************************************************/
			//야드L3Message가 없으면 생성
			

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId                                      ); //전문ID
			sbMsg = sbMsg.append(commUtils.getDateTime18()                  ); //생성일,생성시간(yyyy-MM-ddHH:mm:ss)
			sbMsg = sbMsg.append("I"                                        ); //전문구분
			sbMsg = sbMsg.append("0078"                                     ); //전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" "	     , 29, " ")     ); //임시
			if("A".equals(ydBayGP)){
				sbMsg = sbMsg.append(commUtils.getRPad(ydRepA	     ,  6, " ")     ); //A동 대표크레인
			}else if("C".equals(ydBayGP)){
				sbMsg = sbMsg.append(commUtils.getRPad(ydRepC	     ,  6, " ")     ); //C동 대표크레인
			}else if("E".equals(ydBayGP)){
				sbMsg = sbMsg.append(commUtils.getRPad(ydRepE	     ,  6, " ")     ); //E동 대표크레인
			}else
			{
				sbMsg = sbMsg.append(commUtils.getRPad(" "	     ,  6, " ")     ); //
			}
			sbMsg = sbMsg.append(commUtils.getRPad(" "       ,  1, " ")     ); //야드작업진행상태
			sbMsg = sbMsg.append(commUtils.getRPad(" "       ,  8, " ")     ); //야드스케쥴코드
			sbMsg = sbMsg.append(commUtils.getRPad(" "       , 18, " ")     ); //야드크레인스케쥴ID
			sbMsg = sbMsg.append(commUtils.getRPad(" "       ,  1, " ")     ); //야드L2실적구분
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3HdRsCd,  4, " ")     ); //야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg   , 40, " ")     ); //야드L3Message

			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

			sndL2Msg.setResultCode(logId);		//Log ID
			sndL2Msg.setResultMsg(methodNm);	//Log Method Name
			sndL2Msg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndL2Msg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndL2Msg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndL2Msg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}//End getYFF1L005_recv
    
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 코일 야드 크레인 위치정보(F1YFL031)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvF1YFL031(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일 야드 크레인 위치정보[ACoilRcvL2SeEJB.rcvF1YFL031] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		JDTORecord jrRtn = null;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");
			//수신 항목 값
			String msgId    = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			for(int Loop_i = 1; Loop_i <= 24; Loop_i++) {
				
				String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"+Loop_i ));
				if("".equals(ydEqpId)){
					break;
				}
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("MODIFIER"     		, modifier);					//수정자
				jrYdMsg.setField("YD_EQP_ID"     		, ydEqpId );  //YD_EQP_ID 
				jrYdMsg.setField("CRN_WRK_PROC_STAT"    , commUtils.trim(rcvMsg.getFieldString("CRN_WRK_PROC_STAT"+Loop_i )) );  //CRN_WRK_PROC_STAT
				jrYdMsg.setField("CURR_XAXIS"     		, commUtils.trim(rcvMsg.getFieldString("CURR_XAXIS"+Loop_i )) );  //CURR_XAXIS
				jrYdMsg.setField("FROM_XAXIS"     		, commUtils.trim(rcvMsg.getFieldString("FROM_XAXIS"+Loop_i )) );  //FROM_XAXIS 
				jrYdMsg.setField("TO_XAXIS"     		, commUtils.trim(rcvMsg.getFieldString("TO_XAXIS"+Loop_i )) );  //TO_XAXIS
				
				/*박판열연 야드 크레인 위치정보 등록*/
				commDao.update(jrYdMsg, updYmCrnLoc, logId, methodNm, "코일 야드 크레인 위치 정보 등록");
			}
			commUtils.printLog(logId, methodNm, "S-");
			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
	/**
	 * 신고도화 설비 Event
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
    public String procEqpIdEvent(JDTORecord jrEventGp) throws JDTOException {
    	String methodNm = "신고도화 설비 Event[ACoilRcvL2SeEJB.procEqpIdEvent] < " + jrEventGp.getResultMsg();
		String logId 	= jrEventGp.getResultCode();
    	String rtnPrior = "";

		commUtils.printLog(logId, methodNm, "S+");

		try {
			String ydEventGp         = commUtils.trim(jrEventGp.getFieldString("YD_EVENT_GP")); //야드설비ID
			/**********************************************************
			 * <<ydEventGp>>
			 * 3XDC01:DC_CONV,3XSPM1:SPM1,3XSPM2:SPM2,3XHFL1:HFL1
			 * 3XTC01:대차1,3XTC02:대차2,3XTC04:대차4,3XTC05:대차5
			 * 3APTL1:A동좌측
			 * 3CPTL1:C동좌측,3CPTR1:C동우측
			 * 3EPTL1:E동좌측,3EPTR1:E동우측
			 **********************************************************/
			/**********************************************************
			* 1. 동별 설비(Tracking, 대차  조건 검색 CHECK
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID" , ydEventGp); //
			
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getEquipStatTrkTc 
			WITH TRK_TBL AS 
			(
			SELECT SUM(CASE WHEN PROC_GP = 'D' THEN 1 ELSE 0 END) AS DC_TOT_CNT    -- DC총갯수
			     , SUM(CASE WHEN PROC_GP = 'H' THEN 1 ELSE 0 END) AS HFL_TOT_CNT   -- HFL총갯수
			     , SUM(CASE WHEN PROC_GP = 'K' THEN 1 ELSE 0 END) AS SPM_TOT_CNT   -- SPM총갯수
			     , SUM(CASE WHEN PROC_GP = 'P' THEN 1 ELSE 0 END) AS SPM2_TOT_CNT  -- SPM2총갯수
			     
			     , SUM(CASE WHEN PROC_GP = 'D' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) <= 18                        THEN 1 ELSE 0 END) AS DC_MIDD_CNT          -- DC입출측외 갯수   
			     , SUM(CASE WHEN PROC_GP = 'D' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) <= 18 AND STL_NO IS NOT NULL THEN 1 ELSE 0 END) AS DC_MIDD_COIL_CNT     -- DC입출측외 COIL갯수   
			     , SUM(CASE WHEN PROC_GP = 'H' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) >=  3 AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) <= 14 THEN 1 ELSE 0 END) AS HFL_MIDD_CNT   -- HFL입출측외갯수
			     , SUM(CASE WHEN PROC_GP = 'H' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) >=  3 AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) <= 14 AND STL_NO IS NOT NULL THEN 1 ELSE 0 END) AS HFL_MIDD_COIL_CNT -- HFL입출측외갯수
			     , SUM(CASE WHEN PROC_GP = 'K' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) >=  3 AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) <= 19 THEN 1 ELSE 0 END) AS SPM_MIDD_CNT   -- SPM입출측외갯수
			     , SUM(CASE WHEN PROC_GP = 'K' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) >=  3 AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2)) <= 19 AND STL_NO IS NOT NULL THEN 1 ELSE 0 END) AS SPM_MIDD_COIL_CNT -- SPM입출측외갯수
			     -- A동정보
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01')                                                 THEN 1      ELSE 0  END) AS DC_A_OUT_CNT        -- DC출측갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_A_OUT1_COIL_CNT  -- DC출측COIL1갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_A_OUT2_COIL_CNT  -- DC출측COIL2갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '03'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_A_OUT3_COIL_CNT  -- DC출측COIL3갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '04'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_A_OUT4_COIL_CNT  -- DC출측COIL4갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '05'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_A_OUT5_COIL_CNT  -- DC출측COIL5갯수

			     , MAX(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS DC_A_OUT1_COIL_NO   -- DC출측COIL1번호
			     , MAX(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS DC_A_OUT2_COIL_NO   -- DC출측COIL2번호
			     , MAX(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '03'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS DC_A_OUT3_COIL_NO   -- DC출측COIL3번호
			     , MAX(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '04'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS DC_A_OUT4_COIL_NO   -- DC출측COIL4번호
			     , MAX(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3AST01') AND YD_STK_BED_NO = '05'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS DC_A_OUT5_COIL_NO   -- DC출측COIL5번호
			     
			     , SUM(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3AFE01')                                                 THEN 1      ELSE 0  END) AS HFL_A_IN_CNT        -- HFL입측갯수
			     , SUM(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3AFE01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS HFL_A_IN1_COIL_CNT  -- HFL입측COIL1갯수
			     , SUM(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3AFE01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS HFL_A_IN2_COIL_CNT  -- HFL입측COIL2갯수
			     , MAX(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3AFE01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS HFL_A_IN1_COIL_NO  -- HFL입측COIL1번호
			     , MAX(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3AFE01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS HFL_A_IN2_COIL_NO  -- HFL입측COIL2번호
			     -- C동정보
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3BST02')                                                 THEN 1      ELSE 0  END) AS DC_B_OUT_CNT         -- B출측갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3BST02')                          AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_B_OUT_COIL_CNT    -- B출측COIL갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND SUBSTR(EQUIP_GP,3,2) IN ('30','31')                                        THEN 1      ELSE 0  END) AS DC_C_MIDD_CNT        -- C입출측외갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND SUBSTR(EQUIP_GP,3,2) IN ('30','31')                 AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_C_MIDD_COIL_CNT   -- C입출측외COIL갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3CST03') AND YD_STK_BED_NO IN ('01','02')                 THEN 1      ELSE 0  END) AS DC_C_OUT_CNT         -- C출측갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3CST03') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_C_OUT1_COIL_CNT   -- C출측COIL1갯수
			     , SUM(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3CST03') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS DC_C_OUT2_COIL_CNT   -- C출측COIL2갯수
			     , MAX(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3CST03') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS DC_C_OUT1_COIL_NO    -- C출측COIL1번호
			     , MAX(CASE WHEN PROC_GP = 'D' AND YD_STK_COL_GP IN ('3CST03') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS DC_C_OUT2_COIL_NO    -- C출측COIL2번호
			     , SUM(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3CFD01')                                                 THEN 1      ELSE 0  END) AS HFL_C_OUT_CNT        -- C출측갯수
			     , SUM(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3CFD01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS HFL_C_OUT1_COIL_CNT  -- C출측COIL1갯수
			     , SUM(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3CFD01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS HFL_C_OUT2_COIL_CNT  -- C출측COIL2갯수
			     , MAX(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3CFD01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS HFL_C_OUT1_COIL_NO   -- C출측COIL1번호
			     , MAX(CASE WHEN PROC_GP = 'H' AND YD_STK_COL_GP IN ('3CFD01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS HFL_C_OUT2_COIL_NO   -- C출측COIL2번호
			     
			     
			     , SUM(CASE WHEN PROC_GP = 'K' AND YD_STK_COL_GP IN ('3CKE01')                                                 THEN 1      ELSE 0  END) AS SPM_C_IN_CNT         -- C입측갯수
			     , SUM(CASE WHEN PROC_GP = 'K' AND YD_STK_COL_GP IN ('3CKE01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS SPM_C_IN1_COIL_CNT   -- C입측COIL1갯수
			     , SUM(CASE WHEN PROC_GP = 'K' AND YD_STK_COL_GP IN ('3CKE01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS SPM_C_IN2_COIL_CNT   -- C입측COIL2갯수
			     , MAX(CASE WHEN PROC_GP = 'K' AND YD_STK_COL_GP IN ('3CKE01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS SPM_C_IN1_COIL_NO    -- C입측COIL1NO
			     , MAX(CASE WHEN PROC_GP = 'K' AND YD_STK_COL_GP IN ('3CKE01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS SPM_C_IN2_COIL_NO    -- C입측COIL2NO
			     -- E동정보     
			     , SUM(CASE WHEN PROC_GP = 'P' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2))  =  7                                      THEN 1      ELSE 0  END) AS SPM2_E_IN_CNT        -- E입측갯수
			     , SUM(CASE WHEN PROC_GP = 'P' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2))  =  7               AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS SPM2_E_IN1_COIL_CNT  -- E입측COIL1갯수
			     , MAX(CASE WHEN PROC_GP = 'P' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2))  =  7               AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS SPM2_E_IN1_COIL_NO   -- E입측COIL1번호
			     , SUM(CASE WHEN PROC_GP = 'P' AND TO_NUMBER(SUBSTR(EQUIP_GP,3,2))  >= 22                                     THEN 1      ELSE 0  END) AS SPM2_E_OUT_CNT       -- E출측갯수
			     , SUM(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS SPM2_E_OUT1_COIL_CNT -- E출측COIL1갯수
			     , SUM(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS SPM2_E_OUT2_COIL_CNT -- E출측COIL2갯수
			     , SUM(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '03'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS SPM2_E_OUT3_COIL_CNT -- E출측COIL3갯수
			     , SUM(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '04'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS SPM2_E_OUT4_COIL_CNT -- E출측COIL4갯수
			     , SUM(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '05'  AND STL_NO IS NOT NULL THEN 1      ELSE 0  END) AS SPM2_E_OUT5_COIL_CNT -- E출측COIL5갯수

			     , MAX(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '01'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS SPM2_E_OUT1_COIL_NO  -- E출측COIL1번호
			     , MAX(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '02'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS SPM2_E_OUT2_COIL_NO  -- E출측COIL2번호
			     , MAX(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '03'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS SPM2_E_OUT3_COIL_NO  -- E출측COIL3번호
			     , MAX(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '04'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS SPM2_E_OUT4_COIL_NO  -- E출측COIL4번호
			     , MAX(CASE WHEN PROC_GP = 'P' AND YD_STK_COL_GP IN ('3EKD01') AND YD_STK_BED_NO = '05'  AND STL_NO IS NOT NULL THEN STL_NO ELSE '' END) AS SPM2_E_OUT5_COIL_NO  -- E출측COIL5번호
			     
			  FROM TB_YM_EQPTRACKING 
			 WHERE DEL_YN = 'N' 
			) ,

			TC_TBL AS (
			SELECT MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN EQUIP_GP          ELSE '' END) AS A_1_EQUIP_GP
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN EQUIP_STAT        ELSE '' END) AS A_1_EQUIP_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN CURR_STOP_LOC     ELSE '' END) AS A_1_CURR_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN WORK_MODE         ELSE '' END) AS A_1_WORK_MODE
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CAR_PROG_STAT  ELSE '' END) AS A_1_YD_CAR_PROG_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CARLD_STOP_LOC ELSE '' END) AS A_1_YD_CARLD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CARUD_STOP_LOC ELSE '' END) AS A_1_YD_CARUD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN TC_COIL_CNT       ELSE 0  END) AS A_1_TC_COIL_CNT
			     
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN EQUIP_GP          ELSE '' END) AS C_1_EQUIP_GP
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN EQUIP_STAT        ELSE '' END) AS C_1_EQUIP_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN CURR_STOP_LOC     ELSE '' END) AS C_1_CURR_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN WORK_MODE         ELSE '' END) AS C_1_WORK_MODE
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CAR_PROG_STAT  ELSE '' END) AS C_1_YD_CAR_PROG_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CARLD_STOP_LOC ELSE '' END) AS C_1_YD_CARLD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CARUD_STOP_LOC ELSE '' END) AS C_1_YD_CARUD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN TC_COIL_CNT       ELSE 0  END) AS C_1_TC_COIL_CNT
			     
			     , MAX(CASE WHEN EQUIP_GP = '3XTC02' THEN EQUIP_GP          ELSE '' END) AS C_2_EQUIP_GP
			     , MAX(CASE WHEN EQUIP_GP = '3XTC02' THEN EQUIP_STAT        ELSE '' END) AS C_2_EQUIP_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC02' THEN CURR_STOP_LOC     ELSE '' END) AS C_2_CURR_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC02' THEN WORK_MODE         ELSE '' END) AS C_2_WORK_MODE
			     , MAX(CASE WHEN EQUIP_GP = '3XTC02' THEN YD_CAR_PROG_STAT  ELSE '' END) AS C_2_YD_CAR_PROG_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC02' THEN YD_CARLD_STOP_LOC ELSE '' END) AS C_2_YD_CARLD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC02' THEN YD_CARUD_STOP_LOC ELSE '' END) AS C_2_YD_CARUD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC02' THEN TC_COIL_CNT       ELSE 0  END) AS C_2_TC_COIL_CNT

			     , MAX(CASE WHEN EQUIP_GP = '3XTC04' THEN EQUIP_GP          ELSE '' END) AS C_4_EQUIP_GP
			     , MAX(CASE WHEN EQUIP_GP = '3XTC04' THEN EQUIP_STAT        ELSE '' END) AS C_4_EQUIP_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC04' THEN CURR_STOP_LOC     ELSE '' END) AS C_4_CURR_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC04' THEN WORK_MODE         ELSE '' END) AS C_4_WORK_MODE
			     , MAX(CASE WHEN EQUIP_GP = '3XTC04' THEN YD_CAR_PROG_STAT  ELSE '' END) AS C_4_YD_CAR_PROG_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC04' THEN YD_CARLD_STOP_LOC ELSE '' END) AS C_4_YD_CARLD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC04' THEN YD_CARUD_STOP_LOC ELSE '' END) AS C_4_YD_CARUD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC04' THEN TC_COIL_CNT       ELSE 0  END) AS C_4_TC_COIL_CNT

			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN EQUIP_GP          ELSE '' END) AS E_1_EQUIP_GP
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN EQUIP_STAT        ELSE '' END) AS E_1_EQUIP_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN CURR_STOP_LOC     ELSE '' END) AS E_1_CURR_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN WORK_MODE         ELSE '' END) AS E_1_WORK_MODE
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CAR_PROG_STAT  ELSE '' END) AS E_1_YD_CAR_PROG_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CARLD_STOP_LOC ELSE '' END) AS E_1_YD_CARLD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN YD_CARUD_STOP_LOC ELSE '' END) AS E_1_YD_CARUD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC01' THEN TC_COIL_CNT       ELSE 0  END) AS E_1_TC_COIL_CNT

			     , MAX(CASE WHEN EQUIP_GP = '3XTC05' THEN EQUIP_GP          ELSE '' END) AS E_5_EQUIP_GP
			     , MAX(CASE WHEN EQUIP_GP = '3XTC05' THEN EQUIP_STAT        ELSE '' END) AS E_5_EQUIP_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC05' THEN CURR_STOP_LOC     ELSE '' END) AS E_5_CURR_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC05' THEN WORK_MODE         ELSE '' END) AS E_5_WORK_MODE
			     , MAX(CASE WHEN EQUIP_GP = '3XTC05' THEN YD_CAR_PROG_STAT  ELSE '' END) AS E_5_YD_CAR_PROG_STAT
			     , MAX(CASE WHEN EQUIP_GP = '3XTC05' THEN YD_CARLD_STOP_LOC ELSE '' END) AS E_5_YD_CARLD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC05' THEN YD_CARUD_STOP_LOC ELSE '' END) AS E_5_YD_CARUD_STOP_LOC
			     , MAX(CASE WHEN EQUIP_GP = '3XTC05' THEN TC_COIL_CNT       ELSE 0  END) AS E_5_TC_COIL_CNT
			  FROM (
			        SELECT A.EQUIP_GP
			             , A.EQUIP_NAME
			             , A.EQUIP_STAT
			             , A.CURR_STOP_LOC
			             , A.WORK_MODE 
			             --0:작업대기,1:상차출발,2:상차도착,3:상차검수,4:상차개시,5:상차완료
			             --A:하차출발,B:하차도착,C:하차검수,D:하차개시,E:하차완료
			             , B.YD_CAR_PROG_STAT
			             , B.YD_CARLD_STOP_LOC
			             , B.YD_CARUD_STOP_LOC
			             , CASE WHEN B.YD_CAR_PROG_STAT IN ('4','5')  THEN (SELECT COUNT(*) 
			                                                                  FROM TB_YM_TCARFTMVMTL TM
			                                                                     , TB_YF_STKLYR  SL
			                                                                 WHERE YD_TCAR_SCH_ID = B.YD_TCAR_SCH_ID
			                                                                   AND TM.STL_NO = SL.STL_NO
			                                                                   AND SL.STACK_LYR_STAT = 'C' )
			                    WHEN B.YD_CAR_PROG_STAT = 'A'         THEN (SELECT COUNT(*) 
			                                                                  FROM TB_YM_TCARFTMVMTL TM
			                                                                 WHERE YD_TCAR_SCH_ID = B.YD_TCAR_SCH_ID
			                                                               )
			                    WHEN B.YD_CAR_PROG_STAT IN ('B','D')  THEN (SELECT COUNT(*) 
			                                                                  FROM TB_YM_TCARFTMVMTL TM
			                                                                     , TB_YF_STKLYR  SL
			                                                                 WHERE YD_TCAR_SCH_ID = B.YD_TCAR_SCH_ID
			                                                                   AND TM.STL_NO = SL.STL_NO
			                                                                   AND SL.STACK_LYR_STAT = 'U' )
			                    ELSE 0 END                             TC_COIL_CNT
			          FROM TB_YM_EQUIP A
			             , TB_YM_TCARSCH B
			         WHERE A.EQUIP_GP = B.YD_EQP_ID
			           AND A.EQUIP_GP LIKE '3XTC%'
			           AND B.DEL_YN = 'N'
			       )
			)       
			SELECT A.DC_A_OUT_CNT              AS A_DC_OUT_CNT
			     , A.DC_A_OUT1_COIL_CNT        AS A_DC_OUT1_COIL_CNT
			     , A.DC_A_OUT2_COIL_CNT        AS A_DC_OUT2_COIL_CNT
			     , A.DC_A_OUT3_COIL_CNT        AS A_DC_OUT3_COIL_CNT
			     , A.DC_A_OUT4_COIL_CNT        AS A_DC_OUT4_COIL_CNT
			     , A.DC_A_OUT5_COIL_CNT        AS A_DC_OUT5_COIL_CNT
			     , A.DC_A_OUT1_COIL_NO         AS A_DC_OUT1_COIL_NO
			     , A.DC_A_OUT2_COIL_NO         AS A_DC_OUT2_COIL_NO
			     , A.DC_A_OUT3_COIL_NO         AS A_DC_OUT3_COIL_NO
			     , A.DC_A_OUT4_COIL_NO         AS A_DC_OUT4_COIL_NO
			     , A.DC_A_OUT5_COIL_NO         AS A_DC_OUT5_COIL_NO
			     , A.DC_MIDD_CNT               AS A_DC_MIDDLE_CNT
			     , A.DC_MIDD_COIL_CNT          AS A_DC_MIDDLE_COIL_CNT
			     , A.HFL_A_IN_CNT              AS A_HFL_IN_CNT 
			     , A.HFL_A_IN1_COIL_CNT        AS A_HFL_IN1_COIL_CNT 
			     , A.HFL_A_IN2_COIL_CNT        AS A_HFL_IN2_COIL_CNT 
			     , A.HFL_A_IN1_COIL_NO         AS A_HFL_IN1_COIL_NO 
			     , A.HFL_A_IN2_COIL_NO         AS A_HFL_IN2_COIL_NO
			     , A.HFL_MIDD_CNT              AS A_HFL_MIDDLE_CNT 
			     , A.HFL_MIDD_COIL_CNT         AS A_HFL_MIDDLE_COIL_CNT 
			     , B.A_1_EQUIP_GP              AS A_TC1_EQUIP_GP   
			     , B.A_1_EQUIP_STAT            AS A_TC1_EQUIP_STAT
			     , B.A_1_CURR_STOP_LOC         AS A_TC1_CURR_STOP_LOC
			     , B.A_1_WORK_MODE             AS A_TC1_WORK_MODE
			     , B.A_1_YD_CAR_PROG_STAT      AS A_TC1_YD_CAR_PROG_STAT 
			     , B.A_1_YD_CARLD_STOP_LOC     AS A_TC1_YD_CARLD_STOP_LOC 
			     , B.A_1_YD_CARUD_STOP_LOC     AS A_TC1_YD_CARUD_STOP_LOC 
			     , B.A_1_TC_COIL_CNT           AS A_TC1_TC_COIL_CNT      
			 
			     , A.DC_MIDD_CNT      + DC_B_OUT_CNT      + DC_C_MIDD_CNT         AS C_DC_MIDDLE_CNT
			     , A.DC_MIDD_COIL_CNT + DC_B_OUT_COIL_CNT + DC_C_MIDD_COIL_CNT    AS C_DC_MIDDLE_COIL_CNT
			     , A.DC_C_OUT_CNT              AS C_DC_OUT_CNT
			     , A.DC_C_OUT1_COIL_CNT        AS C_DC_OUT1_COIL_CNT
			     , A.DC_C_OUT2_COIL_CNT        AS C_DC_OUT2_COIL_CNT
			     , A.DC_C_OUT1_COIL_NO         AS C_DC_OUT1_COIL_NO
			     , A.DC_C_OUT2_COIL_NO         AS C_DC_OUT2_COIL_NO
			     , A.HFL_C_OUT_CNT             AS C_HFL_OUT_CNT
			     , A.HFL_C_OUT1_COIL_CNT       AS C_HFL_OUT1_COIL_CNT
			     , A.HFL_C_OUT2_COIL_CNT       AS C_HFL_OUT2_COIL_CNT
			     , A.HFL_C_OUT1_COIL_NO        AS C_HFL_OUT1_COIL_NO
			     , A.HFL_C_OUT2_COIL_NO        AS C_HFL_OUT2_COIL_NO
			     , A.SPM_C_IN_CNT              AS C_SPM_IN_CNT
			     , A.SPM_C_IN1_COIL_CNT        AS C_SPM_IN1_COIL_CNT
			     , A.SPM_C_IN2_COIL_CNT        AS C_SPM_IN2_COIL_CNT
			     , A.SPM_C_IN1_COIL_NO         AS C_SPM_IN1_COIL_NO
			     , A.SPM_C_IN2_COIL_NO         AS C_SPM_IN2_COIL_NO
			     , A.SPM_MIDD_CNT              AS C_SPM_MIDDLE_CNT
			     , A.SPM_MIDD_COIL_CNT         AS C_SPM_MIDDLE_COIL_CNT     
			     , B.C_1_EQUIP_GP              AS C_TC1_EQUIP_GP   
			     , B.C_1_EQUIP_STAT            AS C_TC1_EQUIP_STAT
			     , B.C_1_CURR_STOP_LOC         AS C_TC1_CURR_STOP_LOC
			     , B.C_1_WORK_MODE             AS C_TC1_WORK_MODE
			     , B.C_1_YD_CAR_PROG_STAT      AS C_TC1_YD_CAR_PROG_STAT 
			     , B.C_1_YD_CARLD_STOP_LOC     AS C_TC1_YD_CARLD_STOP_LOC 
			     , B.C_1_YD_CARUD_STOP_LOC     AS C_TC1_YD_CARUD_STOP_LOC 
			     , B.C_1_TC_COIL_CNT           AS C_TC1_TC_COIL_CNT      
			     , B.C_2_EQUIP_GP              AS C_TC2_EQUIP_GP   
			     , B.C_2_EQUIP_STAT            AS C_TC2_EQUIP_STAT
			     , B.C_2_CURR_STOP_LOC         AS C_TC2_CURR_STOP_LOC
			     , B.C_2_WORK_MODE             AS C_TC2_WORK_MODE
			     , B.C_2_YD_CAR_PROG_STAT      AS C_TC2_YD_CAR_PROG_STAT 
			     , B.C_2_YD_CARLD_STOP_LOC     AS C_TC2_YD_CARLD_STOP_LOC 
			     , B.C_2_YD_CARUD_STOP_LOC     AS C_TC2_YD_CARUD_STOP_LOC 
			     , B.C_2_TC_COIL_CNT           AS C_TC2_TC_COIL_CNT      
			     , B.C_4_EQUIP_GP              AS C_TC4_EQUIP_GP   
			     , B.C_4_EQUIP_STAT            AS C_TC4_EQUIP_STAT
			     , B.C_4_CURR_STOP_LOC         AS C_TC4_CURR_STOP_LOC
			     , B.C_4_WORK_MODE             AS C_TC4_WORK_MODE
			     , B.C_4_YD_CAR_PROG_STAT      AS C_TC4_YD_CAR_PROG_STAT 
			     , B.C_4_YD_CARLD_STOP_LOC     AS C_TC4_YD_CARLD_STOP_LOC 
			     , B.C_4_YD_CARUD_STOP_LOC     AS C_TC4_YD_CARUD_STOP_LOC 
			     , B.C_4_TC_COIL_CNT           AS C_TC4_TC_COIL_CNT      

			     , A.SPM2_E_IN_CNT             AS E_SPM2_IN_CNT 
			     , A.SPM2_E_IN1_COIL_CNT       AS E_SPM2_IN1_COIL_CNT
			     , A.SPM2_E_IN1_COIL_NO        AS E_SPM2_IN1_COIL_NO
			     , A.SPM2_E_OUT_CNT            AS E_SPM2_OUT_CNT
			     , A.SPM2_E_OUT1_COIL_CNT      AS E_SPM2_OUT1_COIL_CNT
			     , A.SPM2_E_OUT2_COIL_CNT      AS E_SPM2_OUT2_COIL_CNT
			     , A.SPM2_E_OUT3_COIL_CNT      AS E_SPM2_OUT3_COIL_CNT
			     , A.SPM2_E_OUT4_COIL_CNT      AS E_SPM2_OUT4_COIL_CNT
			     , A.SPM2_E_OUT5_COIL_CNT      AS E_SPM2_OUT5_COIL_CNT
			     , A.SPM2_E_OUT1_COIL_NO       AS E_SPM2_OUT1_COIL_NO
			     , A.SPM2_E_OUT2_COIL_NO       AS E_SPM2_OUT2_COIL_NO
			     , A.SPM2_E_OUT3_COIL_NO       AS E_SPM2_OUT3_COIL_NO
			     , A.SPM2_E_OUT4_COIL_NO       AS E_SPM2_OUT4_COIL_NO
			     , A.SPM2_E_OUT5_COIL_NO       AS E_SPM2_OUT5_COIL_NO
			     , B.E_1_EQUIP_GP              AS E_TC1_EQUIP_GP   
			     , B.E_1_EQUIP_STAT            AS E_TC1_EQUIP_STAT
			     , B.E_1_CURR_STOP_LOC         AS E_TC1_CURR_STOP_LOC
			     , B.E_1_WORK_MODE             AS E_TC1_WORK_MODE
			     , B.E_1_YD_CAR_PROG_STAT      AS E_TC1_YD_CAR_PROG_STAT 
			     , B.E_1_YD_CARLD_STOP_LOC     AS E_TC1_YD_CARLD_STOP_LOC 
			     , B.E_1_YD_CARUD_STOP_LOC     AS E_TC1_YD_CARUD_STOP_LOC 
			     , B.E_1_TC_COIL_CNT           AS E_TC1_TC_COIL_CNT      
			     , B.E_5_EQUIP_GP              AS E_TC5_EQUIP_GP   
			     , B.E_5_EQUIP_STAT            AS E_TC5_EQUIP_STAT
			     , B.E_5_CURR_STOP_LOC         AS E_TC5_CURR_STOP_LOC
			     , B.E_5_WORK_MODE             AS E_TC5_WORK_MODE
			     , B.E_5_YD_CAR_PROG_STAT      AS E_TC5_YD_CAR_PROG_STAT 
			     , B.E_5_YD_CARLD_STOP_LOC     AS E_TC5_YD_CARLD_STOP_LOC 
			     , B.E_5_YD_CARUD_STOP_LOC     AS E_TC5_YD_CARUD_STOP_LOC 
			     , B.E_5_TC_COIL_CNT           AS E_TC5_TC_COIL_CNT      
			  FROM TRK_TBL  A
			     , TC_TBL   B 
			*/   
			JDTORecordSet jsEquipTrkTc = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getEquipStatTrkTc", logId, methodNm, "설비정보(TRK,TC) READ");
			
			JDTORecord jrEquipTrkTc = JDTORecordFactory.getInstance().create();	
			if (jsEquipTrkTc.size() == 0) {
				commUtils.printLog(logId, "설비 정보 이상", "SL");
				commUtils.printLog(logId, methodNm, "S-");
				return YmConstant.RETN_CD_FAILURE;
			} else { 
				jrEquipTrkTc = jsEquipTrkTc.getRecord(0);
			}			

/*** A동 Check ***/
			//DC_LINE OFF코일방향 :'05'->'04'->'03'->'02'->'01'
			int iA_DC_OUT_CNT 	 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_DC_OUT_CNT"   		),"0")); //DC출측갯수
			int iA_DC_OUT1_COIL_CNT    		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_DC_OUT1_COIL_CNT"   	),"0")); //DC출측1COIL갯수
			int iA_DC_OUT2_COIL_CNT    		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_DC_OUT2_COIL_CNT"   	),"0")); //DC출측2COIL갯수
			int iA_DC_OUT3_COIL_CNT    		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_DC_OUT3_COIL_CNT"   	),"0")); //DC출측3COIL갯수
			int iA_DC_OUT4_COIL_CNT    		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_DC_OUT4_COIL_CNT"   	),"0")); //DC출측4COIL갯수
			int iA_DC_OUT5_COIL_CNT    		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_DC_OUT5_COIL_CNT"   	),"0")); //DC출측5COIL갯수
			String sA_DC_OUT1_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("A_DC_OUT1_COIL_NO"  	));//DC출측1COILNO
			String sA_DC_OUT2_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("A_DC_OUT2_COIL_NO"  	));//DC출측2COILNO
			String sA_DC_OUT3_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("A_DC_OUT3_COIL_NO"  	));//DC출측3COILNO
			String sA_DC_OUT4_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("A_DC_OUT4_COIL_NO"  	));//DC출측4COILNO
			String sA_DC_OUT5_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("A_DC_OUT5_COIL_NO"  	));//DC출측5COILNO
			
			int iA_DC_MIDDLE_CNT 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_DC_MIDDLE_CNT"		),"0")); //DC입출측외 갯수
			int iA_DC_MIDDLE_COIL_CNT 		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_DC_MIDDLE_COIL_CNT"	),"0")); //DC입출측외 COIL갯수
			
			//HFL 입측 코일방향 :'02'->'01'
			int iA_HFL_IN_CNT 	 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_HFL_IN_CNT"    		),"0")); //HFL입측갯수
			int iA_HFL_IN1_COIL_CNT 		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_HFL_IN1_COIL_CNT"  	),"0")); //HFL입측1COIL갯수
			int iA_HFL_IN2_COIL_CNT 		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_HFL_IN2_COIL_CNT"  	),"0")); //HFL입측2COIL갯수
			String sA_HFL_IN1_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("A_HFL_IN1_COIL_NO"  	));//HFL입측1COILNO
			String sA_HFL_IN2_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("A_HFL_IN2_COIL_NO"  	));//HFL입측2COILNO

			int iA_HFL_MIDDLE_CNT 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_HFL_MIDDLE_CNT"   	),"0")); //HFL입출측외갯수
			int iA_HFL_MIDDLE_COIL_CNT		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_HFL_MIDDLE_COIL_CNT"  ),"0")); //HFL입출측외COIL갯수
		
			//1대차정보
			String sA_TC1_EQUIP_GP 	 		= commUtils.trim(jrEquipTrkTc.getFieldString("A_TC1_EQUIP_GP"  		)); //HFL대차번호
			String sA_TC1_EQUIP_STAT 		= commUtils.trim(jrEquipTrkTc.getFieldString("A_TC1_EQUIP_STAT"  		)); //HFL대차상태
			String sA_TC1_CURR_STOP_LOC 	= commUtils.trim(jrEquipTrkTc.getFieldString("A_TC1_CURR_STOP_LOC"  	)); //HFL대차현위치
			String sA_TC1_WORK_MODE 	 	= commUtils.trim(jrEquipTrkTc.getFieldString("A_TC1_WORK_MODE"  		)); //HFL대차작업MODE
			String sA_TC1_YD_CAR_PROG_STAT	= commUtils.trim(jrEquipTrkTc.getFieldString("A_TC1_YD_CAR_PROG_STAT"	)); //HFL대차진행상태
			String sA_TC1_YD_CARLD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("A_TC1_YD_CARLD_STOP_LOC")); //HFL대차FROM위치
			String sA_TC1_YD_CARUD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("A_TC1_YD_CARUD_STOP_LOC")); //HFL대차TO위치
			int iA_TC1_TC_COIL_CNT	    	= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("A_TC1_TC_COIL_CNT"      ),"0")); //HFL대차매수 
			
/*** C동 Check ***/
			//DC_LINE OFF 코일방향 :'02'->'01'
			int iC_DC_OUT_CNT 	 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_DC_OUT_CNT"   		),"0")); //DC출측갯수
			int iC_DC_OUT1_COIL_CNT    		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_DC_OUT1_COIL_CNT"   	),"0")); //DC출측COIL갯수
			int iC_DC_OUT2_COIL_CNT    		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_DC_OUT2_COIL_CNT"   	),"0")); //DC출측COIL갯수
			String sC_DC_OUT1_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("C_DC_OUT1_COIL_NO"  	));//DC출측1COILNO
			String sC_DC_OUT2_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("C_DC_OUT2_COIL_NO"  	));//DC출측2COILNO

			int iC_DC_MIDDLE_CNT 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_DC_MIDDLE_CNT"		),"0")); //DC입출측외 갯수
			int iC_DC_MIDDLE_COIL_CNT 		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_DC_MIDDLE_COIL_CNT"	),"0")); //DC입출측외 COIL갯수

			//HFL 출측 코일방향 :'02'->'01'
			int iC_HFL_OUT_CNT 	 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_HFL_OUT_CNT"    		),"0")); //HFL출측갯수
			int iC_HFL_OUT1_COIL_CNT 		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_HFL_OUT1_COIL_CNT"  	),"0")); //HFL출측1COIL갯수
			int iC_HFL_OUT2_COIL_CNT 		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_HFL_OUT2_COIL_CNT"  	),"0")); //HFL출측2COIL갯수
			String sC_HFL_OUT1_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("C_HFL_OUT1_COIL_NO"  	));//HFL출측1COILNO
			String sC_HFL_OUT2_COIL_NO    	= commUtils.trim(jrEquipTrkTc.getFieldString("C_HFL_OUT2_COIL_NO"  	));//HFL출측2COILNO

			
			//SPM입측 코일방향 :'02'->'01'
			int iC_SPM_IN_CNT 				= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_SPM_IN_CNT"   		),"0")); //SPM입측갯수
			int iC_SPM_IN1_COIL_CNT			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_SPM_IN1_COIL_CNT"  	),"0")); //SPM입측COIL갯수
			int iC_SPM_IN2_COIL_CNT			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_SPM_IN2_COIL_CNT"  	),"0")); //SPM입측COIL갯수
			String sC_SPM_IN1_COIL_CNT    	= commUtils.trim(jrEquipTrkTc.getFieldString("C_SPM_IN1_COIL_NO"  	));//SPM입측1COIL갯수
			String sC_SPM_IN2_COIL_CNT	    = commUtils.trim(jrEquipTrkTc.getFieldString("C_SPM_IN2_COIL_NO"  	));//SPM입측2COIL갯수

			int iC_SPM_MIDDLE_CNT 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_SPM_MIDDLE_CNT"   	),"0")); //SPM입출측외갯수
			int iC_SPM_MIDDLE_COIL_CNT		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_SPM_MIDDLE_COIL_CNT"  ),"0")); //SPM입출측외COIL갯수
			
			//1대차정보
			String sC_TC1_EQUIP_GP 	 		= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC1_EQUIP_GP"  		)); //HFL대차번호
			String sC_TC1_EQUIP_STAT 		= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC1_EQUIP_STAT"  		)); //HFL대차상태
			String sC_TC1_CURR_STOP_LOC 	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC1_CURR_STOP_LOC"  	)); //HFL대차현위치
			String sC_TC1_WORK_MODE 	 	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC1_WORK_MODE"  		)); //HFL대차작업MODE
			String sC_TC1_YD_CAR_PROG_STAT	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC1_YD_CAR_PROG_STAT" )); //HFL대차진행상태
			String sC_TC1_YD_CARLD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC1_YD_CARLD_STOP_LOC")); //HFL대차FROM위치
			String sC_TC1_YD_CARUD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC1_YD_CARUD_STOP_LOC")); //HFL대차TO위치
			int iC_TC1_TC_COIL_CNT	    	= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_TC1_TC_COIL_CNT"      ),"0")); //HFL대차매수 
			//2대차정보
			String sC_TC2_EQUIP_GP 	 		= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC2_EQUIP_GP"  		)); //SPM대차번호
			String sC_TC2_EQUIP_STAT 		= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC2_EQUIP_STAT"  		)); //SPM대차상태
			String sC_TC2_CURR_STOP_LOC 	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC2_CURR_STOP_LOC"  	)); //SPM대차현위치
			String sC_TC2_WORK_MODE 	 	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC2_WORK_MODE"  		)); //SPM대차작업MODE
			String sC_TC2_YD_CAR_PROG_STAT	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC2_YD_CAR_PROG_STAT" )); //SPM대차진행상태
			String sC_TC2_YD_CARLD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC2_YD_CARLD_STOP_LOC")); //SPM대차FROM위치
			String sC_TC2_YD_CARUD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC2_YD_CARUD_STOP_LOC")); //SPM대차TO위치
			int iC_TC2_TC_COIL_CNT	    	= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_TC2_TC_COIL_CNT"      ),"0")); //SPM대차매수 
			//4대차정보
			String sC_TC4_EQUIP_GP 	 		= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC4_EQUIP_GP"  		)); //TC4대차번호
			String sC_TC4_EQUIP_STAT 		= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC4_EQUIP_STAT"  		)); //TC4대차상태
			String sC_TC4_CURR_STOP_LOC 	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC4_CURR_STOP_LOC"  	)); //TC4대차현위치
			String sC_TC4_WORK_MODE 	 	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC4_WORK_MODE"  		)); //TC4대차작업MODE
			String sC_TC4_YD_CAR_PROG_STAT	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC4_YD_CAR_PROG_STAT" )); //TC4대차진행상태
			String sC_TC4_YD_CARLD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC4_YD_CARLD_STOP_LOC")); //TC4대차FROM위치
			String sC_TC4_YD_CARUD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("C_TC4_YD_CARUD_STOP_LOC")); //TC4대차TO위치
			int iC_TC4_TC_COIL_CNT	    	= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("C_TC4_TC_COIL_CNT"      ),"0")); //TC4대차매수 
			
/*** E동 Check ***/
			int iE_SPM2_IN_CNT 	 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_SPM2_IN_CNT"    		),"0")); //SPM2입측갯수
			int iE_SPM2_IN1_COIL_CNT 		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_SPM2_IN1_COIL_CNT"  	),"0")); //SPM2입측COIL갯수
			//코일방향 :'05'->'04'->'03'->'02'->'01'
			int iE_SPM2_OUT_CNT 			= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_SPM2_OUT_CNT"   		),"0")); //SPM2출측외갯수
			int iE_SPM2_OUT1_COIL_CNT		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_SPM2_OUT1_COIL_CNT"  	),"0")); //SPM2출측1COIL갯수
			int iE_SPM2_OUT2_COIL_CNT		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_SPM2_OUT2_COIL_CNT"  	),"0")); //SPM2출측2COIL갯수
			int iE_SPM2_OUT3_COIL_CNT		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_SPM2_OUT3_COIL_CNT"  	),"0")); //SPM2출측3COIL갯수
			int iE_SPM2_OUT4_COIL_CNT		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_SPM2_OUT4_COIL_CNT"  	),"0")); //SPM2출측4COIL갯수
			int iE_SPM2_OUT5_COIL_CNT		= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_SPM2_OUT5_COIL_CNT"  	),"0")); //SPM2출측5COIL갯수

			String sE_SPM2_OUT1_COIL_NO		= commUtils.trim(jrEquipTrkTc.getFieldString("E_SPM2_OUT1_COIL_NO" )); //SPM2출측1COILNO
			String sE_SPM2_OUT2_COIL_NO		= commUtils.trim(jrEquipTrkTc.getFieldString("E_SPM2_OUT2_COIL_NO" )); //SPM2출측2COILNO
			String sE_SPM2_OUT3_COIL_NO		= commUtils.trim(jrEquipTrkTc.getFieldString("E_SPM2_OUT3_COIL_NO" )); //SPM2출측3COILNO
			String sE_SPM2_OUT4_COIL_NO		= commUtils.trim(jrEquipTrkTc.getFieldString("E_SPM2_OUT4_COIL_NO" )); //SPM2출측4COILNO
			String sE_SPM2_OUT5_COIL_NO		= commUtils.trim(jrEquipTrkTc.getFieldString("E_SPM2_OUT5_COIL_NO" )); //SPM2출측5COILNO
			
			//1대차정보
			String sE_TC1_EQUIP_GP 	 		= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC1_EQUIP_GP"  		)); //HFL대차번호
			String sE_TC1_EQUIP_STAT 		= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC1_EQUIP_STAT"  		)); //HFL대차상태
			String sE_TC1_CURR_STOP_LOC 	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC1_CURR_STOP_LOC"  	)); //HFL대차현위치
			String sE_TC1_WORK_MODE 	 	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC1_WORK_MODE"  		)); //HFL대차작업MODE
			String sE_TC1_YD_CAR_PROG_STAT	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC1_YD_CAR_PROG_STAT" )); //HFL대차진행상태
			String sE_TC1_YD_CARLD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC1_YD_CARLD_STOP_LOC")); //HFL대차FROM위치
			String sE_TC1_YD_CARUD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC1_YD_CARUD_STOP_LOC")); //HFL대차TO위치
			int iE_TC1_TC_COIL_CNT	    	= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_TC1_TC_COIL_CNT"      ),"0")); //HFL대차매수 
			
			//5대차정보
			String sE_TC5_EQUIP_GP 	 		= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC5_EQUIP_GP"  		)); //TC5대차번호
			String sE_TC5_EQUIP_STAT 		= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC5_EQUIP_STAT"  		)); //TC5대차상태
			String sE_TC5_CURR_STOP_LOC 	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC5_CURR_STOP_LOC"  	)); //TC5대차현위치
			String sE_TC5_WORK_MODE 	 	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC5_WORK_MODE"  		)); //TC5대차작업MODE
			String sE_TC5_YD_CAR_PROG_STAT	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC5_YD_CAR_PROG_STAT" )); //TC5대차진행상태
			String sE_TC5_YD_CARLD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC5_YD_CARLD_STOP_LOC")); //TC5대차FROM위치
			String sE_TC5_YD_CARUD_STOP_LOC	= commUtils.trim(jrEquipTrkTc.getFieldString("E_TC5_YD_CARUD_STOP_LOC")); //TC5대차TO위치
			int iE_TC5_TC_COIL_CNT	    	= Integer.parseInt(commUtils.nvl(jrEquipTrkTc.getFieldString("E_TC5_TC_COIL_CNT"      ),"0")); //TC5대차매수 

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getAdvSchedule 
			SELECT DISTINCT(YD_SCH_CD) AS YD_SCH_CD
			  FROM USRYMA.TB_YM_ADV_SCHEDULERULE
			 WHERE YD_EQP_ID = :V_YD_EQP_ID
			*/   
			JDTORecordSet jsAdvSchedule = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getAdvSchedule", logId, methodNm, "설비정보 READ");
			JDTORecord jrAdvSchedule = JDTORecordFactory.getInstance().create();	
			for(int j = 1; j <= jsAdvSchedule.size(); j++) {
				jsAdvSchedule.absolute(j);
				jrAdvSchedule  = jsAdvSchedule.getRecord();
				
				String ydSchCd = commUtils.trim(jrAdvSchedule.getFieldString("YD_SCH_CD"));
				if (ydSchCd.length() != 8) {
					commUtils.printLog(logId, methodNm + "스케쥴 코드 이상-불가", "SL");
					continue;
				}
				
				String ydGp = ydSchCd.substring(1, 2);
				String sTerm1     = "B";  //B:기본 O:출측 I:입측  A:차량도착
				String sTerm2     = "*";
				String sTerm3     = "*";
				String sTerm4     = "*";
				String sTerm5     = "*";
				//  DC LineOff
				if ("DC01L".equals(ydSchCd.substring(2, 7))) {
					sTerm1 = "O"; // 출측
					sTerm2 = "C"; // 코일이 있는경우
					
					if ("A".equals(ydGp)) {
						int iaDcSum = iA_DC_OUT1_COIL_CNT+iA_DC_OUT2_COIL_CNT+iA_DC_OUT3_COIL_CNT+iA_DC_OUT4_COIL_CNT+iA_DC_OUT5_COIL_CNT;
						if (iaDcSum == 0) {
							commUtils.printLog(logId, methodNm + "DC 대상 없음 - 불가", "SL");
							return YmConstant.RETN_CD_FAILURE;
						} 
						if ((iA_DC_OUT5_COIL_CNT == 1)        && (iaDcSum == 5)) {  
							sTerm3 = "05";
						} else if ((iA_DC_OUT4_COIL_CNT == 1) && (iaDcSum == 4)) {
							sTerm3 = "04";
						} else if ((iA_DC_OUT3_COIL_CNT == 1) && (iaDcSum == 3)) {
							sTerm3 = "03";
						} else if ((iA_DC_OUT2_COIL_CNT == 1) && (iaDcSum == 2)) {
							sTerm3 = "02";
						} else if ((iA_DC_OUT1_COIL_CNT == 1) && (iaDcSum == 1)) {
							sTerm3 = "01";
						}
					}
					if ("C".equals(ydGp)) {
						int icDcSum = iC_DC_OUT1_COIL_CNT+iC_DC_OUT2_COIL_CNT;
						if (icDcSum == 0) {
							commUtils.printLog(logId, methodNm + "DC 대상 없음 - 불가", "SL");
							continue;					
						}
						if ((iC_DC_OUT2_COIL_CNT == 0)        && (icDcSum == 2)) {
							sTerm3 = "02";
						} else if ((iC_DC_OUT1_COIL_CNT == 0) && (icDcSum == 1)) {
							sTerm3 = "01";
						}
					}
				}
				
				// HFL1 보급
				if ("FE01U".equals(ydSchCd.substring(2, 7))) {
					sTerm1 = "I"; // 입측
					sTerm2 = "E"; // 코일이 없는 경우
					
					if ("A".equals(ydGp)) {
						if (iA_HFL_IN_CNT == (iA_HFL_IN1_COIL_CNT+iA_HFL_IN2_COIL_CNT)) {
							commUtils.printLog(logId, methodNm + "HFL 입측 빈곳 없음 - 불가", "SL");
							continue;
						}
						
						if (iA_HFL_IN1_COIL_CNT + iA_HFL_IN2_COIL_CNT == 0 ) {
							sTerm3 = "12";   //'입측'01','02'번에 코일이 없는 경우
						} else if (iA_HFL_IN2_COIL_CNT == 0) {
							sTerm3 = "02";
						} else if (iA_HFL_IN1_COIL_CNT == 0) {
							sTerm3 = "01";
						}
					}
				}
				
				// SPM1 보급
				if ("KE01U".equals(ydSchCd.substring(2, 7))) {
					sTerm1 = "I"; // 입측
					sTerm2 = "E"; // 코일이 없는 경우
					if ("C".equals(ydGp)) {
						if (iC_SPM_IN_CNT == (iC_SPM_IN1_COIL_CNT+iC_SPM_IN2_COIL_CNT)) {
							commUtils.printLog(logId, methodNm + "SPM1 입측 빈곳 없음 - 불가", "SL");
							continue;
						}
						
						if (iC_SPM_IN1_COIL_CNT + iC_SPM_IN2_COIL_CNT == 0) {
							sTerm3 = "12";  //'입측'01','02'번에 코일이 없는 경우
						} else if (iC_SPM_IN2_COIL_CNT == 0) {
							sTerm3 = "02";
						} else if (iC_SPM_IN1_COIL_CNT == 0) {
							sTerm3 = "01";
						}
					}	
				}
				
				// SPM2 보급
				if ("KE02U".equals(ydSchCd.substring(2, 7))) {
					sTerm1 = "I"; // 입측
					sTerm2 = "E"; // 코일이 없는 경우
					if ("E".equals(ydGp)) {
						if (iE_SPM2_IN_CNT == iE_SPM2_IN1_COIL_CNT) {
							commUtils.printLog(logId, methodNm + "SPM2 입측 빈곳 없음 - 불가", "SL");
							continue;
						}
						if (iE_SPM2_IN1_COIL_CNT == 0) {
							sTerm3 = "01";
						}
					}				
				}
				
				// HFL1 추출
				if ("FE01L".equals(ydSchCd.substring(2, 7))) {
					sTerm1 = "O"; // 출측
					sTerm2 = "C"; // 코일이 있는경우
					
					if (ydGp.equals("C")) {
						int icHflSum = iC_HFL_OUT1_COIL_CNT+iC_HFL_OUT2_COIL_CNT;
						if (icHflSum == 0) {
							commUtils.printLog(logId, methodNm + "HFL 출측 대상 없음 - 불가", "SL");
							continue;
						}
						if ((iC_HFL_OUT1_COIL_CNT == 1)        && (icHflSum == 2)) { 
							sTerm3 = "01";
						} else if ((iC_HFL_OUT2_COIL_CNT == 1) && (icHflSum == 1)) {
							sTerm3 = "02";
						}
					}				
				}
				
				// SPM2 추출
				if (ydSchCd.substring(2, 7).equals("KD02L")) {
					sTerm1 = "O"; // 출측
					sTerm2 = "C"; // 코일이 있는경우
					
					if (ydGp.equals("E")) {
						
						int ieSpm2Sum = iE_SPM2_OUT1_COIL_CNT+iE_SPM2_OUT2_COIL_CNT+iE_SPM2_OUT3_COIL_CNT+iE_SPM2_OUT4_COIL_CNT+iE_SPM2_OUT5_COIL_CNT;
						if (ieSpm2Sum == 0) {
							commUtils.printLog(logId, methodNm + "SPM2 출측 대상 없음 - 불가", "SL");
							continue;
						}
						if ((iE_SPM2_OUT5_COIL_CNT == 1)        && (ieSpm2Sum == 5)) {
							sTerm3 = "05";
						} else if ((iE_SPM2_OUT4_COIL_CNT == 1) && (ieSpm2Sum == 4)) {
							sTerm3 = "04";
						} else if ((iE_SPM2_OUT3_COIL_CNT == 1) && (ieSpm2Sum == 3)) {
							sTerm3 = "03";
						} else if ((iE_SPM2_OUT2_COIL_CNT == 1) && (ieSpm2Sum == 2)) {
							sTerm3 = "02";
						} else if ((iE_SPM2_OUT1_COIL_CNT == 1) && (ieSpm2Sum == 1)) {
							sTerm3 = "01";
						}
					}					
					
				}
				
				// 자동이적 
				if (ydSchCd.substring(2, 5).equals("YD1")) {
					
					
				}
				// 동내이적 
				if (ydSchCd.substring(2, 5).equals("YD0")) {
					
					
				}
				// 대차상차
				if ((ydSchCd.substring(2, 4).equals("TC")) && (ydSchCd.substring(6, 7).equals("U"))) {
					if (ydGp.equals("A")) {
						// 1대차가 현재동에 있고 온라인 상태이며 상차도착/상차개시인 경우
						if ((sA_TC1_CURR_STOP_LOC.equals(ydGp))&&("O".equals(sA_TC1_EQUIP_STAT))&&("1".equals(sA_TC1_WORK_MODE))
								 &&("2".equals(sA_TC1_YD_CAR_PROG_STAT)||"4".equals(sA_TC1_YD_CAR_PROG_STAT))) {
						} else {
							commUtils.printLog(logId, methodNm + "1번 대차조건 - 불가", "SL");
							continue;
						}
					} else if (ydGp.equals("C")) {
						// 1대차가 현재동에 있고 온라인 상태이며 상차도착/상차개시인 경우
						if ((sC_TC1_CURR_STOP_LOC.equals(ydGp))&&("O".equals(sC_TC1_EQUIP_STAT))&&("1".equals(sC_TC1_WORK_MODE))
								 &&("2".equals(sC_TC1_YD_CAR_PROG_STAT)||"4".equals(sC_TC1_YD_CAR_PROG_STAT))) {
						} else {
							commUtils.printLog(logId, methodNm + "1번 대차조건 - 불가", "SL");
							continue;
						}
						// 2대차가 현재동에 있고 온라인 상태이며 상차도착/상차개시인 경우
						if ((sC_TC2_CURR_STOP_LOC.equals(ydGp))&&("O".equals(sC_TC2_EQUIP_STAT))&&("1".equals(sC_TC2_WORK_MODE))
								 &&("2".equals(sC_TC2_YD_CAR_PROG_STAT)||"4".equals(sC_TC2_YD_CAR_PROG_STAT))) {
						} else {
							commUtils.printLog(logId, methodNm + "2번 대차조건 - 불가", "SL");
							continue;
						}
						// 4대차가 현재동에 있고 온라인 상태이며 상차도착/상차개시인 경우
						if ((sC_TC4_CURR_STOP_LOC.equals(ydGp))&&("O".equals(sC_TC4_EQUIP_STAT))&&("1".equals(sC_TC4_WORK_MODE))
								 &&("2".equals(sC_TC4_YD_CAR_PROG_STAT)||"4".equals(sC_TC4_YD_CAR_PROG_STAT))) {
						} else {
							commUtils.printLog(logId, methodNm + "4번 대차조건 - 불가", "SL");
							continue;
						}

					} else if (ydGp.equals("E")) {
						// 1대차가 현재동에 있고 온라인 상태이며 상차도착/상차개시인 경우
						if ((sE_TC1_CURR_STOP_LOC.equals(ydGp))&&("O".equals(sE_TC1_EQUIP_STAT))&&("1".equals(sE_TC1_WORK_MODE))
								 &&("2".equals(sE_TC1_YD_CAR_PROG_STAT)||"4".equals(sE_TC1_YD_CAR_PROG_STAT))) {
						} else {
							commUtils.printLog(logId, methodNm + "1번 대차조건 - 불가", "SL");
							continue;
						}
						// 5대차가 현재동에 있고 온라인 상태이며 상차도착/상차개시인 경우
						if ((sE_TC5_CURR_STOP_LOC.equals(ydGp))&&("O".equals(sE_TC5_EQUIP_STAT))&&("1".equals(sE_TC5_WORK_MODE))
								 &&("2".equals(sE_TC5_YD_CAR_PROG_STAT)||"4".equals(sE_TC5_YD_CAR_PROG_STAT))) {
						} else {
							commUtils.printLog(logId, methodNm + "5번 대차조건 - 불가", "SL");
							continue;
						}
					}
				}
				
				jrParam.setField("YD_SCH_CD" , ydSchCd); //권하위치
				jrParam.setField("TERM1"     , sTerm1);
				jrParam.setField("TERM2"     , sTerm2);
				jrParam.setField("TERM3"     , sTerm3);
				jrParam.setField("TERM4"     , sTerm4);
				jrParam.setField("TERM5"     , sTerm5);
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAdvPrior 
				SELECT YD_SCH_CD, ADV_CRN_PRIOR, NEW_ADV_CRN_PRIOR  
				  FROM (
				        SELECT A.YD_SCH_CD
				             , NVL(A.ADV_CRN_PRIOR,'99') AS ADV_CRN_PRIOR
				             , (
				                SELECT YD_WRK_CRN_PRIOR 
				                  FROM TB_YM_ADV_SCHEDULERULE 
				                 WHERE YD_SCH_CD = A.YD_SCH_CD
				                   AND TERM1 = :V_TERM1
				                   AND TERM2 = :V_TERM2
				                   AND TERM3 = :V_TERM3
				                   AND TERM4 = :V_TERM4
				                   AND TERM5 = :V_TERM5 
				                   AND ROWNUM = 1
				                ) AS NEW_ADV_CRN_PRIOR    
				          FROM TB_YM_SCHEDULERULE A
				         WHERE YD_SCH_CD = :V_YD_SCH_CD
				       ) 
				 WHERE ADV_CRN_PRIOR != NEW_ADV_CRN_PRIOR   
				*/   
				JDTORecordSet jsAdvPrior = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getAdvPrior", logId, methodNm, "고도화 우선순위");
					
				if (jsAdvPrior.size() > 0) {
					rtnPrior = jsAdvPrior.getRecord(0).getFieldString("NEW_ADV_CRN_PRIOR");
					/**********************************************************
					* 스케쥴 기준: 별도 트렌젝션 처리**
					**********************************************************/
					JDTORecord jrParam1	= JDTORecordFactory.getInstance().create();
					jrParam1.setResultCode(logId);		//Log ID
					jrParam1.setResultMsg(methodNm);	//Log Method Name
					jrParam1.setField("YD_SCH_CD"       , ydSchCd     ); //스케쥴 코드
					jrParam1.setField("ADV_CRN_PRIOR"   , rtnPrior    ); //우선순위
					EJBConnector ejbConn1 = new EJBConnector("default", "ACoilRcvL2SeEJB", this);
					ejbConn1.trx("UpdScheduleRule", new Class[] { JDTORecord.class }, new Object[] { jrParam1 });
				}
				
			}		
			/**********************************************************
			* 2. 차량 조건 검색 CHECK
			*  <<ydEventGp>>
 		    * 3XDC01:DC_CONV,3XSPM1:SPM1,3XSPM2:SPM2,3XHFL1:HFL1
			* 3XTC01:대차1,3XTC02:대차2,3XTC04:대차4,3XTC05:대차5
			* 3APTL1:A동좌측
			* 3CPTL1:C동좌측,3CPTR1:C동우측
			* 3EPTL1:E동좌측,3EPTR1:E동우측
			**********************************************************/
		//	this.procEqpIdEventCar(jrEventGp);

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	} 
    
    
    /**
	 * 오퍼레이션명 : 쿼리IF생성
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @Asynchronous
	 */
	public void rcvYFYFJ998(JDTORecord rcvMsg) throws DAOException
	{
		FileWriter fw = null;
		BufferedWriter bw = null;
		DBAssistantDAO dao = new DBAssistantDAO();

		try
		{
			//String path = "src"+File.separator+"com"+File.separator+"YfQueryIFTest.java";
			String path = "YfQueryIFOld.java";
			File file = new File(path);
			file.delete();

			fw = new FileWriter(path);
			bw = new BufferedWriter(fw);

			/*
			--com.inisteel.cim.yf.common.dao.YfCommDAO.getYfQueryList
			select *
			from JSPEED.JF_QUERY
			where TARGET_DOMAIN = 'yf'
			*/
			JDTORecordSet set = dao.getRecordSet("com.inisteel.cim.yf.common.dao.YfCommDAO.getYfQueryListOld", new Object[] {});
			JDTORecord row = null;
			String queryId = null; 

			bw.write("package com.inisteel.cim.yf.common;");
			bw.newLine();
			bw.write("/**");
			bw.newLine();
			bw.write("* Ver. "+ commUtils.getDateTime19());
			bw.newLine();
			bw.write("**/");
			bw.newLine();
			bw.write("public interface YfQueryIFOld");
			bw.newLine();
			bw.write("{");
			bw.newLine();

			for(int i = 0; i < set.size(); i++)
			{
				row = set.getRecord(i);
				if("R".equals(row.getFieldString("QUERY_TYPE")))
				{
					queryId = row.getFieldString("QUERY_ID").substring(row.getFieldString("QUERY_ID").lastIndexOf(".")+1);

					bw.newLine();
					bw.write(" /** <pre> ");
					bw.newLine();

					bw.write(row.getFieldString("QUERY_CONTENT").replaceAll("\\*/","").replaceAll("/\\*","--"));

					bw.newLine();
					bw.write(" </pre> */");

					bw.newLine();
					bw.write("public final static String "+queryId+" = \""+row.getFieldString("QUERY_ID")+"\";");
					bw.newLine();
				}
			}

			bw.newLine();
			bw.write("}");
			bw.flush();
			
			/************************************************
			 * 초기화
			 * - 두번째 파일 작업 시작
			 ************************************************/
			if(bw!= null)
			{
				try
				{
					bw.close();
					bw = null;
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}

			if(fw!= null)
			{
				try
				{
					fw.close();
					fw = null;
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
			
			path = "YfQueryIFOld2.java";
			file = new File(path);
			file.delete();

			fw = new FileWriter(path);
			bw = new BufferedWriter(fw);
			
			
			bw.write("package com.inisteel.cim.yf.common;");
			bw.newLine();
			bw.write("/**");
			bw.newLine();
			bw.write("* Ver. "+ commUtils.getDateTime19());
			bw.newLine();
			bw.write("**/");
			bw.newLine();
			bw.write("public interface YfQueryIFOld2");
			bw.newLine();
			bw.write("{");
			bw.newLine();

			for(int i = 0; i < set.size(); i++)
			{
				row = set.getRecord(i);
				if(!"R".equals(row.getFieldString("QUERY_TYPE")))
				{
					queryId = row.getFieldString("QUERY_ID").substring(row.getFieldString("QUERY_ID").lastIndexOf(".")+1);

					bw.newLine();
					bw.write(" /** <pre> ");
					bw.newLine();

					bw.write(row.getFieldString("QUERY_CONTENT").replaceAll("\\*/","").replaceAll("/\\*","--"));

					bw.newLine();
					bw.write(" </pre> */");

					bw.newLine();
					bw.write("public final static String "+queryId+" = \""+row.getFieldString("QUERY_ID")+"\";");
					bw.newLine();
				}
			}

			bw.newLine();
			bw.write("}");
			bw.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(bw!= null)
			{
				try
				{
					bw.close();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}

			if(fw!= null)
			{
				try
				{
					fw.close();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
	
	
    /**
	 * 오퍼레이션명 : 쿼리IF생성
     *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param
	 * @return
	 * @throws
	 * @Asynchronous
	 */
	public void rcvYFYFJ999(JDTORecord rcvMsg) throws DAOException
	{
		FileWriter fw = null;
		BufferedWriter bw = null;
		DBAssistantDAO dao = new DBAssistantDAO();

		try
		{
			//String path = "src"+File.separator+"com"+File.separator+"YfQueryIFTest.java";
			String path = "YfQueryIF.java";
			File file = new File(path);
			file.delete();

			fw = new FileWriter(path);
			bw = new BufferedWriter(fw);

			/*
			--com.inisteel.cim.yf.common.dao.YfCommDAO.getYfQueryList
			select *
			from JSPEED.JF_QUERY
			where TARGET_DOMAIN = 'yf'
			*/
			JDTORecordSet set = dao.getRecordSet("com.inisteel.cim.yf.common.dao.YfCommDAO.getYfQueryList", new Object[] {});
			JDTORecord row = null;
			String queryId = null;
			String if1DscDate = null;
			String if1DscDate2 = null;
			
			bw.write("package com.inisteel.cim.yf.common;");
			bw.newLine();
			bw.write("/**");
			bw.newLine();
			bw.write("* Ver. "+ commUtils.getDateTime19());
			bw.newLine();
			bw.write("**/");
			bw.newLine();
			bw.write("public interface YfQueryIF");
			bw.newLine();
			bw.write("{");
			bw.newLine();
			
			for(int i = 0; i < set.size(); i++)
			{
				row = set.getRecord(i);
				if("R".equals(row.getFieldString("QUERY_TYPE")))
				{
					queryId = row.getFieldString("QUERY_ID").substring(row.getFieldString("QUERY_ID").lastIndexOf(".")+1);
					if1DscDate = row.getFieldString("DSC_DATE");
					if1DscDate2 = if1DscDate.substring(0,8);
					bw.newLine();
					bw.write(" /** <pre> ");
					bw.write(commUtils.addDateGubunStr(if1DscDate2, "-"));
					bw.newLine();

					bw.write(row.getFieldString("QUERY_CONTENT").replaceAll("\\*/","").replaceAll("/\\*","--"));

					bw.newLine();
					bw.write(" </pre> */");

					bw.newLine();
					bw.write("public final static String "+queryId+" = \""+row.getFieldString("QUERY_ID")+"\";");
					bw.newLine();
				}
			}

			bw.newLine();
			bw.write("}");
			bw.flush();
			
			/************************************************
			 * 초기화
			 * - 두번째 파일 작업 시작
			 ************************************************/
			String if2DscDate = null;
			String if2DscDate2 = null;
			if(bw!= null)
			{
				try
				{
					bw.close();
					bw = null;
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}

			if(fw!= null)
			{
				try
				{
					fw.close();
					fw = null;
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
			
			path = "YfQueryIF2.java";
			file = new File(path);
			file.delete();

			fw = new FileWriter(path);
			bw = new BufferedWriter(fw);
			
			
			bw.write("package com.inisteel.cim.yf.common;");
			bw.newLine();
			bw.write("/**");
			bw.newLine();
			bw.write("* Ver. "+ commUtils.getDateTime19());
			bw.newLine();
			bw.write("**/");
			bw.newLine();
			bw.write("public interface YfQueryIF2");
			bw.newLine();
			bw.write("{");
			bw.newLine();

			for(int i = 0; i < set.size(); i++)
			{
				row = set.getRecord(i);
				if(!"R".equals(row.getFieldString("QUERY_TYPE")))
				{
					queryId = row.getFieldString("QUERY_ID").substring(row.getFieldString("QUERY_ID").lastIndexOf(".")+1);
					if2DscDate = row.getFieldString("DSC_DATE");
					if2DscDate2 = if2DscDate.substring(0,8);
					bw.newLine();
					bw.write(" /** <pre> ");
					bw.write(commUtils.addDateGubunStr(if2DscDate2, "-"));
					bw.newLine();

					bw.write(row.getFieldString("QUERY_CONTENT").replaceAll("\\*/","").replaceAll("/\\*","--"));

					bw.newLine();
					bw.write(" </pre> */");

					bw.newLine();
					bw.write("public final static String "+queryId+" = \""+row.getFieldString("QUERY_ID")+"\";");
					bw.newLine();
				}
			}

			bw.newLine();
			bw.write("}");
			bw.flush();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(bw!= null)
			{
				try
				{
					bw.close();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}

			if(fw!= null)
			{
				try
				{
					fw.close();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
	}
}	
	
