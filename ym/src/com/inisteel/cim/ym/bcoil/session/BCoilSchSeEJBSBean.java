/**
 * @(#)BCoilSchSeEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 *
 * @description      B열연 COIL 야드 Schedule 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 * 
 */
package com.inisteel.cim.ym.bcoil.session;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmConstant;
import com.inisteel.cim.ym.bcoil.session.BCoilComm;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst; 

/**
 *      [A] 클래스명 : B열연 COIL 야드 Schedule 처리
 * 
 * @ejb.bean name="BCoilSchSeEJB" jndi-name="BCoilSchSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class BCoilSchSeEJBSBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private BCoilComm bcoilComm = new BCoilComm();
	private YmComm YmComm = new YmComm();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	
	
	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄(YMYMJ302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ302(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일크레인스케줄MAIN[BCoilSchSeEJB.rcvYMYMJ302] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
		int intRtnVal = 0;
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ302(rcvMsg));
			
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄 멀티기동(YMYMJ302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ303(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일크레인스케줄MAIN멀티기동 [BCoilSchSeEJB.rcvYMYMJ303] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			int schCnt  = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("SCH_CNT"),"0")); 
			
			EJBConnector ejbConn = new EJBConnector("default", "BCoilSchSeEJB", this); //추가
			EJBConnector sndConn = new EJBConnector("default", "YmCommEJB", this);
			JDTORecord jrRtn302 = null; // 추가

			/***************************************************************************
			 * 트랜잭션 분리 
			 ***************************************************************************/
			String sAPP000_YN = YmComm.BCoilApplyYn("APP000","3","YMYMJ303");   //트랜잭션 분리 여부
			commUtils.printLog(logId,  "==========[[[ APP000 YMYMJ302 트랜잭션 분리 여부:" + sAPP000_YN + " ]]]============", "SL");
			 
			for (int i = 0 ; i<=schCnt; i++) {				
				String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"+i  )); //야드작업예약ID

				commUtils.printLog(logId,  "==========*** YMYMJ303 YD_WBOOK_ID" + i + " : " + ydWbookId + " ***============", "SL");
				
				if (ydWbookId.equals("") ||ydWbookId.length() == 0 ) {
					commUtils.printLog(logId,  "==========*** YMYMJ303 YD_WBOOK_ID" + i + " SKIP 처리 ***============", "SL");
	           	   continue;
				}
				jrParam.setField("JMS_TC_CD"			, "YMYMJ302"); 
				jrParam.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrParam.setField("YD_WBOOK_ID"			, ydWbookId); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  			, ""); //야드설비ID	
				
				if ("Y".equals(sAPP000_YN)) {
				
					jrRtn302 = (JDTORecord)ejbConn.trx("rcvYMYMJ302", new Class[] { JDTORecord.class }, new Object[] { jrParam }); //추가
					//jrRtn = commUtils.addSndData(jrRtn, jrRtn302); //추가
					
					//전송할 Data가 있으면 전송 처리
					if (jrRtn302 != null) {
						jrRtn302.setResultCode(logId);
						jrRtn302.setResultMsg(methodNm);
						
						sndConn.trx("sndInterface", new Class[] { JDTORecord.class }, new Object[] { jrRtn302 });
						
						jrRtn302 = null;
					}					
				
				} else {
				
					jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ302(jrParam));
				}
			}	
			
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄 멀티기동(YMYMJ302) OLD
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ303_OLD(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일크레인스케줄MAIN멀티기동 [BCoilSchSeEJB.멀티기동] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create();;	//전문 Return
		try {
			commUtils.printLog(logId, methodNm, "S+");

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			int schCnt  = Integer.parseInt(commUtils.nvl(rcvMsg.getFieldString("SCH_CNT"),"0")); 

			for (int i = 1 ; i<=schCnt; i++) {				
				String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"+i  )); //야드작업예약ID

				if (ydWbookId.equals("") ||ydWbookId.length() == 0 ) {
	           	   continue;
				}
				jrParam.setField("JMS_TC_CD"			, "YMYMJ302"); 
				jrParam.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시				
				jrParam.setField("YD_WBOOK_ID"			, ydWbookId); //야드작업예약ID
				jrParam.setField("YD_SCH_CD"  			, ""); //야드스케쥴코드
				jrParam.setField("YD_EQP_ID"  			, ""); //야드설비ID					
				jrRtn = commUtils.addSndData(jrRtn, this.procYMYMJ302(jrParam));
			}	
			
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	
	/**	
	 *      [A] 오퍼레이션명 : 코일크레인스케줄(YMYMJ302)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord procYMYMJ302(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "코일크레인스케줄MAIN[BCoilSchSeEJB.procYMYMJ302] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
		int intRtnVal = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			
			//수신 항목 값
			String msgId      = commUtils.getMsgId(rcvMsg);								//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydWbookId  = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID"  )); //야드작업예약ID
			String ydSchCd    = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"    )); //야드스케쥴코드
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"    )); //야드설비ID

			if ("".equals(modifier)) { modifier = msgId; }

			commUtils.printLog(logId, "스케쥴코드[" + ydSchCd + "], 설비ID[" + ydEqpId + "], 작업예약ID[" + ydWbookId + "], 수정자[" + modifier + "]", "SL");

			JDTORecord jrRtn = null;	 //전문 Return
			String trtMsg  = ""; 		 //처리메세지
			String ydL3Msg = ""; 		 //야드L3MESSAGE
			String stackLayerChkYn = ""; //적재위치 CHECK 여부
			
			
			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("YD_SCH_CD"  , ydSchCd  ); //야드스케쥴코드
			jrParam.setField("YD_EQP_ID"  , ydEqpId  ); //야드설비ID
			jrParam.setField("MODIFIER"   , modifier ); //수정자

			/*********************************************************
			 * 스케줄 재료없이 헤더만 있는 스케줄 정리
			 * -발생사유) 동시 트랜잭션 발생
			 *********************************************************/
			/*
			UPDATE TB_YM_CRNSCH
			   SET DEL_YN   = 'Y'
			     , MODIFIER = NVL(:V_MODIFIER,'AUTO_DEL')
			     , MOD_DDTT = SYSDATE
			WHERE YD_CRN_SCH_ID IN(     
			                        SELECT A.YD_CRN_SCH_ID
			                          FROM USRYMA.TB_YM_CRNSCH    A
			                             , USRYMA.TB_YM_CRNWRKMTL B
			                         WHERE A.YD_CRN_SCH_ID=B.YD_CRN_SCH_ID(+)
			                           AND A.DEL_YN = 'N'
			                           AND B.YD_CRN_SCH_ID IS NULL
			                           AND A.REG_DDTT <= SYSDATE-0.002
			                      )
			 */
			JDTORecord jrParam1	= JDTORecordFactory.getInstance().create();
			jrParam1.setField("MODIFIER"   , modifier ); //수정자
			YmComm.execQueryId(jrParam1, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.delCrnSch");
			
			/**********************************************************
			* 1. 파라메타 정보  Check
			* 1.1 스케줄코드 상태 Check
			* 1.2 설비고장 및 OFF-LINE Check
			* 1.3 파라메타 정보 Check
			**********************************************************/
			//스케줄코드 Check
			if ("".equals(ydWbookId) && !"".equals(ydSchCd)) {
				JDTORecord jrChk = YmComm.chkSchCd(jrParam);
				
				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydSchCd = "";
				}
			}

			//설비ID Check
			if ("".equals(ydWbookId) && "".equals(ydSchCd) && !"".equals(ydEqpId)) {
				JDTORecord jrChk = YmComm.chkEqpStat(jrParam);

				ydL3Msg = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"));

				if (!"".equals(ydL3Msg)) {
					ydEqpId = "";
				}
			}
			/**********************************************************
 			 * 1.3  파라메타 정보 Check
 			 * 1.3.1 파라메타 : 크레인설비ID                           -> 해당크레인설비ID로 만들어진 크레인스케줄금지가 되지 않고 크레인우선순위가 가장빠른 작업예약들 중에서
 			 * 	                                                                                                           가장빠른 작업예약을 하나 조회해서 작업 진행.
 			 * 1.3.2. 파라메타 : 크레인스케줄코드, 크레인설비ID            -> 크레인스케줄코드로 크레인스케줄이 생성되지 않은 작업예약들 중에서 가장빠른 작업예약을 하나 조회해서 작업 진행.
 			 * 1.3.3. 파라메타 : 크레인스케줄코드, 크레인설비ID, 작업예약ID	-> 해당작업예약ID로 직접 조회를 해서 작업진행 - 차량도착인 경우
			**********************************************************/			
			//작업예약ID 조회
			if (!"".equals(ydWbookId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbook
				SELECT YD_WBOOK_ID
				     -- 야드스케쥴분할구분을 저장위치 CHECK 여부로 사용
				     , NVL((SELECT YD_SCH_DIV_GP 
				              FROM TB_YM_SCHEDULERULE 
				             WHERE YD_SCH_CD = A.YD_SCH_CD),'Y') AS  STACK_LAYER_CHK_YN
				  FROM TB_YM_WRKBOOK A
				 WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID
				   AND A.DEL_YN      = 'N'
				   AND A.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                               FROM TB_YM_CRNSCH
				                              WHERE DEL_YN = 'N')
				*/
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbook", logId, methodNm, "작업예약 조회");
				
			} else if (!"".equals(ydSchCd)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookEqp
				SELECT YD_WBOOK_ID
				 -- 야드스케쥴분할구분을 저장위치 CHECK 여부로 사용
				     , NVL(A.YD_SCH_DIV_GP,'Y') AS  STACK_LAYER_CHK_YN
				  FROM (SELECT WB.YD_WBOOK_ID
				             , SR.YD_SCH_DIV_GP
				          FROM TB_YM_SCHEDULERULE SR
				              ,TB_YM_WRKBOOK WB
				         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
				           AND NVL(YD_WRK_PLAN_CRN,SR.YD_WRK_CRN)      = :V_YD_EQP_ID
				           AND SR.YD_SCH_PROH_EXN = 'N'
				           AND SR.DEL_YN          = 'N'
				           AND WB.DEL_YN          = 'N'
				           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                        FROM TB_YM_CRNSCH
				                                       WHERE DEL_YN = 'N')
				         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID) A
				 WHERE ROWNUM = 1
				*/ 
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookSchcd", logId, methodNm, "작업예약 조회");
				 
			} else if (!"".equals(ydEqpId)) {
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookEqp
				SELECT YD_WBOOK_ID
				 -- 야드스케쥴분할구분을 저장위치 CHECK 여부로 사용
				     , NVL(A.YD_SCH_DIV_GP,'Y') AS  STACK_LAYER_CHK_YN
				  FROM (SELECT WB.YD_WBOOK_ID
				             , SR.YD_SCH_DIV_GP
				          FROM TB_YM_SCHEDULERULE SR
				              ,TB_YM_WRKBOOK WB
				         WHERE SR.YD_SCH_CD       = WB.YD_SCH_CD
				           AND NVL(WB.YD_WRK_PLAN_CRN,SR.YD_WRK_CRN)      = :V_YD_EQP_ID
				           AND SR.YD_SCH_PROH_EXN = 'N'
				           AND SR.DEL_YN          = 'N'
				           AND WB.DEL_YN          = 'N'
				           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
				                                        FROM TB_YM_CRNSCH
				                                       WHERE DEL_YN = 'N')
				         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID) A
				 WHERE ROWNUM = 1
				*/ 
				jsWbook = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookEqp", logId, methodNm, "작업예약 조회");
				 
			} else {
				throw new Exception("오류:작업예약ID조회 항목 없음");
			}

			if (jsWbook != null && jsWbook.size() > 0) {
				ydWbookId 		= commUtils.trim(jsWbook.getRecord(0).getFieldString("YD_WBOOK_ID"));
				stackLayerChkYn = commUtils.trim(jsWbook.getRecord(0).getFieldString("STACK_LAYER_CHK_YN"));
			} else {
				//throw new Exception("오류:" + trtMsg + " >> 작업예약정보 없음");
				commUtils.printLog(logId, trtMsg + " >> 작업예약정보 없음", "SL");
				return jrRtn;
			}

			commUtils.printLog(logId, trtMsg + " >> 결정된 작업예약ID [" + ydWbookId + "]", "SL");

			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			// 기본적으로 적치단 check 
			// 예외  DC 라인 OFF 
			/**********************************************************
			* 1.2 크레인 작업 재료에 현재 적치단 저장위치 Update (별도 Transaction 으로 처리)
			**********************************************************/
			EJBConnector tranConn = new EJBConnector("default", "BCoilSchSeEJB", this);
			tranConn.trx("updCrnSchWB", new Class[] { JDTORecord.class }, new Object[] { jrParam });
			
			/**********************************************************
			* 2.스케줄수행판단 모듈
			* 2.1 크레인 선택
			* 2.2 TO위치 사전 점검
			**********************************************************/
	
			//조회된 작업예약ID로 상태정보 Check
			String ydToLocDcsnMtd = ""; 	//야드To위치결정방법
			String ydToLocGuide   = ""; 	//야드To위치Guide
			String toLocChkGp     = ""; 	//To위치 점검을 위한 구분(G:To위치Guide, C:차량상차, T:대차상차)
			String trnEqpCd       = ""; 	//운송장비코드
			String ydEqpStat      = "";		//야드설비상태
			String ydSchPrior     = "";
			String WbRegister     = "";     //작업예약 등록자
			
			int ttMtlSh;			//전체 재료매수
			int wmMtlSh;			//작업예약 재료매수
			int stMtlSh;			//저장품 재료매수
			int slMtlSh;			//적치단 재료매수
			int statCSh;			//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
			int abLocSh;			//저장위치이상 재료매수

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCrnSchStat 
			--크레인스케줄 상태정보 조회 
			SELECT WB.YD_GP                                         --야드구분
			      ,WB.YD_BAY_GP                                     --야드동구분
			      ,WB.YD_SCH_CD                                     --야드스케쥴코드
			      ,WB.YD_SCH_PRIOR                                  --야드스케쥴우선순위
			      ,WB.YD_TO_LOC_DCSN_MTD                            --야드To위치결정방법
			      ,WB.YD_TO_LOC_GUIDE                               --야드To위치Guide
			      ,TO_CHAR(WB.REG_DDTT,'YYYYMMDDHH24MISS') AS YD_WBOOK_DT --야드작업예약일시
			      ,CASE WHEN WB.YD_SCH_CD LIKE '__PT__U_' THEN 'C'  --차량상차
			            WHEN WB.YD_SCH_CD LIKE '__TC__U_' THEN 'T'  --대차상차
			            WHEN LENGTH(WB.YD_TO_LOC_GUIDE) >= 4        --Span구분 이상이면
			             AND WB.YD_TO_LOC_GUIDE LIKE WB.YD_GP||WB.YD_BAY_GP||'%' THEN 'G' --To위치Guide
			      	    ELSE 'Z'                                    --기타
			      	END AS TO_LOC_CHK_GP                            --To위치점검구분
			      ,SR.YD_SCH_PROH_EXN                               --야드스케쥴금지유무
			      ,(CASE WHEN (WB.YD_SCH_CD LIKE '3_TC__UM' OR WB.YD_SCH_CD LIKE '3_YD__MM')  THEN WB.YD_WRK_PLAN_CRN ELSE '' END) AS YD_WRK_PLAN_CRN                               --야드작업계획크레인
			      ,E0.WPROG_STAT      AS YD_EQP_STAT_PLN            --작업계획크레인 야드설비상태
			      ,E0.WORK_MODE       AS YD_EQP_WRK_MODE_PLN        --작업계획크레인 야드설비작업Mode
			      ,SR.YD_WRK_CRN                                    --야드작업크레인
			      ,SR.YD_WRK_CRN_PRIOR                              --야드작업크레인우선순위
			      ,E1.WPROG_STAT      AS YD_EQP_STAT_WRK            --작업크레인 야드설비상태
			      ,E1.WORK_MODE       AS YD_EQP_WRK_MODE_WRK        --작업크레인 야드설비작업Mode
			      ,SR.YD_ALT_CRN                                    --야드대체크레인
			      ,SR.YD_ALT_CRN_PRIOR                              --야드대체크레인우선순위
			      ,E2.WPROG_STAT      AS YD_EQP_STAT_ALT            --대체크레인 야드설비상태
			      ,E2.WORK_MODE  AS YD_EQP_WRK_MODE_ALT             --대체크레인 야드설비작업Mode
			      ,NVL(WM.TT_MTL_SH,0) AS TT_MTL_SH                 --전체 재료매수
			      ,NVL(WM.WM_MTL_SH,0) AS WM_MTL_SH                 --작업예약 재료매수
			      ,NVL(WM.ST_MTL_SH,0) AS ST_MTL_SH                 --저장품 재료매수
			      ,NVL(WM.SL_MTL_SH,0) AS SL_MTL_SH                 --적치단 재료매수
			      ,NVL(WM.STAT_C_SH,0) AS STAT_C_SH                 --적치중인 재료매수
			      ,(SELECT COUNT(*)
			          FROM TB_YM_WRKBOOKMTL WM
			              ,TB_YM_STACKLAYER     SL
			         WHERE WM.STOCK_ID    = SL.STOCK_ID
			           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			           AND SL.STACK_COL_GP NOT LIKE SUBSTR(WB.YD_SCH_CD,1,2)||'%'
			           AND SL.STACK_LAYER_STAT = 'C'
			           AND WM.DEL_YN      = 'N'
			           AND SL.DEL_YN      = 'N') AS AB_LOC_SH       --저장위치이상 재료매수
			      , 'N' AS CM_DUP_YN         -- 크레인스케줄 재료중복여부
			      , 'N' AS CL_DUP_GP         -- 크레인스케줄 저장위치중복여부
			      ,WB.YD_CAR_USE_GP                                     --야드차량사용구분
			      ,WB.TRN_EQP_CD                                        --운송장비코드
			      ,WB.CAR_NO                                            --차량번호
			      ,WB.CARD_NO                                           --카드번호
			      ,(SELECT SUBSTR(CURR_STOP_LOC,2,1)
			          FROM TB_YM_EQUIP EQ
			         WHERE EQ.EQUIP_GP = SUBSTR(WB.YD_SCH_CD,1,1)||'XTC'||SUBSTR(WB.YD_SCH_CD,5,2)) AS YD_CURR_BAY_GP
			      , NVL((SELECT CASE WHEN MIN(CASE WHEN S2.STACK_LAYER_STAT = 'D' THEN '1' END ) > 0 THEN 'Y' ELSE 'N' END 
			           FROM TB_YM_WRKBOOKMTL WM
			              , TB_YM_STACKLAYER SL
			              , TB_YM_STACKLAYER S2
			          WHERE WM.STOCK_ID      = SL.STOCK_ID
			            AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			            AND WM.DEL_YN      = 'N'
			            AND SL.DEL_YN      = 'N'
			            AND S2.STACK_COL_GP = SL.STACK_COL_GP 
			            AND S2.STACK_BED_GP IN (SL.STACK_BED_GP, CASE WHEN SL.STACK_LAYER_GP = '02' THEN TO_NUMBER(SL.STACK_BED_GP) 
			                                                          ELSE TO_NUMBER(SL.STACK_BED_GP) - 1 END  )
			            AND S2.STACK_LAYER_GP IN (CASE WHEN SL.STACK_LAYER_GP = '02' THEN '02'
			                                           WHEN  S2.STACK_BED_GP = TO_NUMBER(SL.STACK_BED_GP)  THEN '01'
			                                           ELSE '02' END , '02' )),'N') AS UP_DN_GP           --상단 권하 대상 여부  
			     , CASE WHEN WB.REGISTER = 'AUTO_MV_A' THEN 'A'    -- 냉각더미
			            WHEN WB.REGISTER = 'AUTO_MV_B' THEN 'B'    -- 설비더미
			            WHEN WB.REGISTER = 'AUTO_MV_C' THEN 'C' 
			            ELSE ''
			            END   AS WB_REGISTER                                      
			  FROM TB_YM_WRKBOOK WB
			      ,TB_YM_SCHEDULERULE SR
			      ,TB_YM_EQUIP     E0
			      ,TB_YM_EQUIP     E1
			      ,TB_YM_EQUIP     E2
			      ,(SELECT WM.YD_WBOOK_ID
			              ,COUNT(*)                    AS TT_MTL_SH
			              ,COUNT(DISTINCT WM.STOCK_ID) AS WM_MTL_SH
			              ,COUNT(DISTINCT ST.STOCK_ID) AS ST_MTL_SH
			              ,COUNT(DISTINCT SL.STOCK_ID) AS SL_MTL_SH
			              ,SUM(DECODE(SL.STACK_LAYER_STAT,'C',1,'U',1)) AS STAT_C_SH --적치중인 재료매수
			              ,MAX(WM.YD_UP_COLL_SEQ)      AS YD_UP_COLL_SEQ
			          FROM TB_YM_WRKBOOKMTL WM
			              ,TB_YM_STOCK      ST
			              ,(SELECT * FROM TB_YM_STACKLAYER WHERE STACK_LAYER_STAT IN ('C','U'))     SL
			         WHERE WM.STOCK_ID      = ST.STOCK_ID(+)
			           AND WM.STOCK_ID      = SL.STOCK_ID(+)
			           AND WM.YD_WBOOK_ID = :V_YD_WBOOK_ID
			           AND WM.DEL_YN      = 'N'
			           AND ST.DEL_YN(+)   = 'N'
			           AND SL.DEL_YN(+)   = 'N'
			         GROUP BY WM.YD_WBOOK_ID) WM
			 WHERE WB.YD_SCH_CD       = SR.YD_SCH_CD(+)
			   AND SR.YD_WRK_CRN      = E1.EQUIP_GP(+)
			   AND SR.YD_ALT_CRN      = E2.EQUIP_GP(+)
			   AND (CASE WHEN (WB.YD_SCH_CD LIKE '3_TC__UM' OR WB.YD_SCH_CD LIKE '3_YD__MM')  THEN WB.YD_WRK_PLAN_CRN ELSE '' END) = E0.EQUIP_GP(+)
			   AND WB.YD_WBOOK_ID     = WM.YD_WBOOK_ID(+)
			   AND WB.YD_WBOOK_ID     = :V_YD_WBOOK_ID
			   AND WB.DEL_YN          = 'N'
			   AND SR.DEL_YN(+)       = 'N'
			   AND E1.DEL_YN(+)       = 'N'
			   AND E2.DEL_YN(+)       = 'N'
			   AND E0.DEL_YN(+)       = 'N'
		*/		   
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCrnSchStat", logId, methodNm, "작업예약 조회");
			if (jsChk.size() <= 0) {
				throw new Exception("오류:" + trtMsg + " >> 상태정보 없음");
			} else {
				JDTORecord jrChk = jsChk.getRecord(0);

				ydSchCd                = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"          ));	//야드스케쥴코드
				ydToLocDcsnMtd         = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD" ));	//야드To위치결정방법
				ydToLocGuide           = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_GUIDE"    ));	//야드To위치Guide
				toLocChkGp             = commUtils.trim(jrChk.getFieldString("TO_LOC_CHK_GP"      ));	//To위치점검구분
				ydSchPrior             = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN_PRIOR"   ));	//야드스케쥴우선순위
				WbRegister             = commUtils.trim(jrChk.getFieldString("WB_REGISTER"    ));		//작업예약 등록자
				String ydWrkPlanCrn    = commUtils.trim(jrChk.getFieldString("YD_WRK_PLAN_CRN"    ));	//야드작업계획크레인
				String ydEqpStatPln    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_PLN"    ));	//야드설비상태(작업계획크레인)
				String ydEqpWrkModePln = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));	//야드설비작업Mode(작업계획크레인)
				String ydWrkCrn        = commUtils.trim(jrChk.getFieldString("YD_WRK_CRN"         ));	//야드작업크레인
				String ydEqpStatWrk    = commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_WRK"    ));	//야드설비상태(작업크레인)
				String cmDupYn         = commUtils.trim(jrChk.getFieldString("CM_DUP_YN"          ));	//크레인스케줄 재료중복여부
				String clDupGp         = commUtils.trim(jrChk.getFieldString("CL_DUP_GP"          ));	//크레인스케줄 저장위치중복여부
				String upDnGp          = commUtils.trim(jrChk.getFieldString("UP_DN_GP"           ));	//크레인스케줄 저장위치중복여부
				String sYD_ALT_CRN     = commUtils.trim(jrChk.getFieldString("YD_ALT_CRN"         ));	//대체크레인
				String sYD_EQP_STAT_ALT= commUtils.trim(jrChk.getFieldString("YD_EQP_STAT_ALT"    ));	//대체크레인 설비상태
				String sYD_SCH_PROH_EXN= commUtils.trim(jrChk.getFieldString("YD_SCH_PROH_EXN"    ));	//크레인스케줄 사용금지여부
				
				
				
				String sYD_EQP_WRK_MODE_PLN = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_PLN"));//작업계획크레인 야드설비작업Mode
				String sYD_EQP_WRK_MODE_WRK = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_WRK"));//작업크레인 야드설비작업Mode
				String sYD_EQP_WRK_MODE_ALT = commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_MODE_ALT"));//대체크레인 야드설비작업Mode
				
				ttMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("TT_MTL_SH"),"0"));	//전체 재료매수
				wmMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("WM_MTL_SH"),"0"));	//작업예약 재료매수
				stMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("ST_MTL_SH"),"0"));	//저장품 재료매수
				slMtlSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("SL_MTL_SH"),"0"));	//적치단 재료매수
				statCSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("STAT_C_SH"),"0"));	//적치중인 재료매수(야드적치단재료상태가 적치 중[C] 인 재료수)
				abLocSh = Integer.parseInt(commUtils.nvl(jrChk.getFieldString("AB_LOC_SH"),"0"));	//저장위치이상 재료매수
				String sYM_CRANE_TRAVL_PROH = commUtils.trim(jrChk.getFieldString("YM_CRANE_TRAVL_PROH"    ));	//크레인 주행금지구간
				
				
				
				if (stackLayerChkYn.equals("Y")) {
					if (wmMtlSh == 0) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료 정보 없음");
					} else if (wmMtlSh != ttMtlSh) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]");
					} else if (wmMtlSh != slMtlSh) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 정보 이상 [" + (wmMtlSh - slMtlSh) + "매]");
					} else if (wmMtlSh != statCSh) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치중[C]이 아님 [" + (wmMtlSh - statCSh) + "매]");
					} else if (wmMtlSh != stMtlSh) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]");
					} else if (abLocSh > 0) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치 이상 [" + abLocSh + "매]");
					} else if ("Y".equals(cmDupYn)) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복");
					} else if ("1".equals(clDupGp)) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복");
					} else if ("2".equals(clDupGp)) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복");
					} else if ("Y".equals(upDnGp)) {
						throw new Exception("오류:" + trtMsg + " >> 상단에 권하 작업이 있습니다. 불가합니다.");
					} else if ("Y".equals(sYM_CRANE_TRAVL_PROH)) {
						throw new Exception("오류:" + trtMsg + " >> 권상위치가 크레인 금지구간으로 설정되었습니다. 불가합니다.");
					} else if ("Y".equals(sYD_SCH_PROH_EXN)) {
						throw new Exception("오류:" + trtMsg + " >> 크레인 스케줄이 기동금지로 설정되었습니다. 불가합니다.");
					}
				} else {
					if (wmMtlSh == 0) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료 정보 없음");
					} else if (wmMtlSh != ttMtlSh) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료 적치단 중복 등록 [작업예약: " + wmMtlSh + ", 적치단: " + ttMtlSh + "]");
					} else if (wmMtlSh != stMtlSh) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료 저장품 정보 이상 [" + (wmMtlSh - stMtlSh) + "매]");
					} else if ("Y".equals(cmDupYn)) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료가 기 등록된 크레인작업재료와 중복");
					} else if ("1".equals(clDupGp)) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권하위치와 중복");
					} else if ("2".equals(clDupGp)) {
						throw new Exception("오류:" + trtMsg + " >> 작업예약재료의 현재위치가 기 등록된 크레인스케쥴 권상위치와 중복");
					} else if ("Y".equals(upDnGp)) {
						if ("3EKE02MM".equals(ydSchCd)) {
							// SPM2보급(밴딩커팅->보급대)
						} else {
							throw new Exception("오류:" + trtMsg + " >> 상단에 권하 작업이 있습니다. 불가합니다.");
						}
					} else if ("Y".equals(sYM_CRANE_TRAVL_PROH)) {
						throw new Exception("오류:" + trtMsg + " >> 권상위치가 크레인 금지구간으로 설정되었습니다. 불가합니다.");
					} else if ("Y".equals(sYD_SCH_PROH_EXN)) {
						throw new Exception("오류:" + trtMsg + " >> 크레인 스케줄이 기동금지로 설정되었습니다. 불가합니다.");
					}
				}
				
				/**********************************************************
				* 2.3 권상위치 2단일 경우 하단 좌,우 코일 체크
				**********************************************************/
	 			String sAPP060_ZZ003_YN = YmComm.BCoilApplyYn("APP060","3","ZZ003_YN");   //스케줄기동시(YMYMJ302) 권상위치 2단일경우 하단 좌,우코일 체크 여부
				if (sAPP060_ZZ003_YN.equals("Y")) {
				
					JDTORecordSet jsUpLoc2DanChk = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getUpLoc2DanChk", logId, methodNm, "권상위치 2단일경우 하단 좌,우코일 체크");
					String sUP_LOC_2DAN_CHK = "";
					if(jsUpLoc2DanChk.size() > 0) {
						sUP_LOC_2DAN_CHK = commUtils.trim(jsUpLoc2DanChk.getRecord(0).getFieldString("UP_LOC_2DAN_CHK"));
						if(!"Y".equals(sUP_LOC_2DAN_CHK)) {
							throw new Exception("오류: 권상위치 2단일경우 하단 좌,우코일 체크 >> 하단 코일 정보 없음");
						}
					} else {
						throw new Exception("오류: getUpLoc2DanChk >> 쿼리 수행 결과 정보 없음");
					}
				}
				
				
				/**********************************************************
				* 1.3 크레인 결정
				**********************************************************/

				if (!"".equals(ydWrkPlanCrn) && !"B".equals(ydEqpStatPln) && "1".equals(ydEqpWrkModePln)) {
					//작업예약 지정크레인 : 최우선 지정
					ydEqpId   = ydWrkPlanCrn;	//야드설비ID
					ydEqpStat = ydEqpStatPln;	//야드설비상태
					commUtils.printLog(logId, trtMsg + " >> 작업예약 지정크레인[" + ydWrkPlanCrn + "]으로 설정", "SL");
				} else {
					ydEqpId   = ydWrkCrn;		//야드설비ID
					ydEqpStat = ydEqpStatWrk;	//야드설비상태
				}
				
				/***********************************************************
				 * 1.3.1 크레인이 고장상태이면 대체크레인으로 편성
				 ***********************************************************/
				// 크레인이 고장이고 대체크레인이 고장이 아니면 대체크레인으로 교체
				if ("B".equals(ydEqpStat)) {
					if (!"".equals(sYD_ALT_CRN) && !"B".equals(sYD_EQP_STAT_ALT)) {
						ydEqpId   = sYD_ALT_CRN;
						ydEqpStat = sYD_EQP_STAT_ALT;
					}
					
					// 지정크레인이 고장, 해당 스케줄의 작업크레인이 고장이 아닐때
					if (!"B".equals(ydEqpStatWrk)) {
						ydEqpId   = ydWrkCrn;		//야드설비ID
						ydEqpStat = ydEqpStatWrk;	//야드설비상태
					}
				} 
				
				/**********************************************************
				* 1.4 To위치 사전 점검
				*     - 차량상차 작업('C')
				*     - 야드To위치Guide('G')
				**********************************************************/
				//To위치 사전 점검
				if ("C".equals(toLocChkGp)) {
					//차량상차작업
					String ydCarUseGp = commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP"));	//야드차량사용구분
				           trnEqpCd   = commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"   ));	//운송장비코드
					String carNo      = commUtils.trim(jrChk.getFieldString("CAR_NO"       ));	//차량번호

					if ("".equals(ydCarUseGp)) {
						throw new Exception("오류:" + trtMsg + " >> 차량상차작업 야드차량사용구분 없음");
						
					} else if ("L".equals(ydCarUseGp) && "".equals(trnEqpCd)) {
						// 구내운송
						throw new Exception("오류:" + trtMsg + " >> 구내운송 상차작업 운송장비코드 없음");
					} else if ("G".equals(ydCarUseGp)) {
						// 출하
						if ("".equals(carNo)) {
							throw new Exception("오류:" + trtMsg + " >> 출하차량 상차작업 차량번호 또는 카드번호 없음");
						}
					}
				} 
				
				//야드To위치Guide 값이 4자리 이상이고 To 야드동이 같을 경우가 아니면
				//TO 위치 가이드('G') 아니면 야드To위치결정방법,야드To위치Guide CLEAR
				if (!"G".equals(toLocChkGp)) {
					ydToLocDcsnMtd = ""; //야드To위치결정방법
					ydToLocGuide   = ""; //야드To위치Guide
				}
			}
	
			JDTORecord jrParamSet = JDTORecordFactory.getInstance().create();
			
			jrParamSet.setResultCode(logId);	//Log ID
			jrParamSet.setResultMsg(methodNm);	//Log Method Name
			jrParamSet.setField("MODIFIER"              , modifier ); //수정자
			jrParamSet.setField("YD_WBOOK_ID"			, ydWbookId); 			//야드작업예약ID
			jrParamSet.setField("YD_SCH_CD"  			, ydSchCd  ); 			//야드스케쥴코드
			jrParamSet.setField("YD_EQP_ID"  			, ydEqpId  ); 			//야드설비ID
			jrParamSet.setField("YD_SCH_PRIOR"  		, ydSchPrior  ); 		//야드스케쥴우선순위
			jrParamSet.setField("YD_TO_LOC_DCSN_MTD"  	, ydToLocDcsnMtd  ); 	//야드To위치결정방법
			jrParamSet.setField("YD_TO_LOC_GUIDE"  		, ydToLocGuide  ); 		//야드To위치Guide
			jrParamSet.setField("YD_WBOOK_MTL_CNT"   	, ""+wmMtlSh ); 		//작업예약 매수
			jrParamSet.setField("STACK_LAYER_CHK_YN"   	, stackLayerChkYn ); 	//적재위치 check여부
			jrParamSet.setField("WB_REGISTER"   		, WbRegister ); 		//작업예약 등록자
			
			commUtils.printParam(logId, jrParamSet);
			/**********************************************************
			* 2.그룹핑 파라미터 셋팅
            *  2-1.주작업 및 보조 작업 셋팅
  			**********************************************************/
			commUtils.printLog(logId, "그룹핑 파라미터 셋팅 시작", "SL");			

			JDTORecordSet jsRecset	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			intRtnVal = this.CrnSchGrp(logId, methodNm, jrParamSet, jsRecset);
			if (intRtnVal != 1) {
				m_ctx.setRollbackOnly();
				throw new Exception("오류:그룹핑 파라미터 셋팅 없음");
			}

			/**********************************************************
			* 3.크레인스케줄과 크레인작업재료 등록
			*   3-1.적치단의 재료상태를 권상대기로 변경처리
			*   3-2.크레인스케줄과 크레인작업재료 등록
  			**********************************************************/
			commUtils.printLog(logId, "크레인스케줄과 크레인작업재료 등록 시작 ", "SL");			
				
			intRtnVal = this.CrnSchIns(logId, methodNm, jrParamSet, jsRecset);
			
			if (intRtnVal != 1) {
				m_ctx.setRollbackOnly();
				throw new DAOException("크레인스케줄 및 작업재료 등록 오류");
			}

			/**********************************************************
			* 4.TO 저장위치 결정
  			**********************************************************/
			commUtils.printLog(logId, "TO 저장위치 등록 시작 ", "SL");			
			
			JDTORecord jrLocSrcRngRtn = this.LocSrcRngDataSet(logId, methodNm, jrParamSet);
			
			commUtils.printLog(logId, commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")) + "ydEqpStat:" + ydEqpStat, "SL");
			
			if (commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")).equals("9")) {
				jrLocSrcRngRtn.setResultCode(logId);	//Log ID
				jrLocSrcRngRtn.setResultMsg(methodNm);	//Log Method Name
				try {
					/**********************************************************
					*  대차스케줄 공대차출발지시 처리 (별도 Transaction 으로 처리)
					**********************************************************/
					EJBConnector tranConn1 = new EJBConnector("default", "BCoilSchSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)tranConn1.trx("updTcarSchLevWo", new Class[] { JDTORecord.class }, new Object[] { jrLocSrcRngRtn });
					jrRtn = commUtils.addSndData(jrRtn , jrRtn1);
				} catch (Exception se) {}
				
			} else if (commUtils.trim(jrLocSrcRngRtn.getFieldString("RTN")).equals("-1")) {
				m_ctx.setRollbackOnly();
				throw new DAOException("TO 저장위치 등록  오류");
				
			} 
			
			//-------------------------------------------------------------------------------------------------
			if("3AKD01LM".equals(ydSchCd) && "3ACRA3".equals(ydEqpId)) {
				//현재 스케줄MAIN이 생성하고 있는 크레인 작업지시가 SPM1 추출일 경우 + 작업크레인이 A3 호기 일 경우
			
				String sAPPLY060_SPM1NEW_YN = YmComm.BCoilApplyYn("APP060","3","SPM1NEW_YN"); //SPM1 추출시 SCRAP 차량 도착 여부에 따른 처리 적용여부
				if("Y".equals(sAPPLY060_SPM1NEW_YN)) {
					
					//현재 스케줄MAIN이 생성하고 있는 크레인스케줄 정보 조회 
					jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
					
					JDTORecordSet jr1SpmInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.get1SpmOutChkInfo", logId, methodNm, "SPM1 추출시 SCRAP 차량 도착 여부 등 관련 정보 조회 쿼리");
					
					if(jr1SpmInfo.size() > 0) {
						
						String sYD_DN_WO_LOC 	= commUtils.nvl(jr1SpmInfo.getRecord(0).getFieldString("YD_DN_WO_LOC"),"XX010101"); 
						int    iBASE_X_AXIS 	= Integer.parseInt(commUtils.nvl(jr1SpmInfo.getRecord(0).getFieldString("BASE_X_AXIS"),"0")); //A3 작업가능 X좌표
						int    iTO_X_AXIS		= Integer.parseInt(commUtils.nvl(jr1SpmInfo.getRecord(0).getFieldString("TO_X_AXIS"),"0")); //TO 위치 X좌표
						String sCAR_YN			= commUtils.nvl(jr1SpmInfo.getRecord(0).getFieldString("CAR_YN"),"N"); //Scrap 차량 진엽 여부
						String sA4_STAT			= commUtils.nvl(jr1SpmInfo.getRecord(0).getFieldString("A4_STAT"),""); //A4 크레인 상태
						String sYD_CRN_SCH_ID	= commUtils.nvl(jr1SpmInfo.getRecord(0).getFieldString("YD_CRN_SCH_ID"),""); //크레인 스케줄id
						
						String sA3_CanNotWork_YN = "N"; //A3 크레인이 버퍼 Skid 없이 Scrap Coil 을 권상 한 상태 또는
						                                //A3가 갈 수 없는 권하 위치 라서 다음 작업을 할 수 없을 경우 "Y"설정하여 A4 크레인이 작업하도록 유도한다.
						
						commUtils.printLog(logId, "■ ■ ■ SPM1 추출 관련 >> 권하위치: " + sYD_DN_WO_LOC 
								                + " ,Scrap 차량 진입여부: " + sCAR_YN 
								                + " ,A4 크레인 상태: " + sA4_STAT
								                + " ,현재 작업대상 크래인: " + ydEqpId
								                + " ,A3 작업가능 X좌표: " + iBASE_X_AXIS
								                + " ,TO위치 X좌표: " + iTO_X_AXIS
								                + " ,크레인스케줄ID: " + sYD_CRN_SCH_ID
								                + " ■ ■ ■"
								                , "SL");
						
						//Scrap 차량이 진입했을 경우 만..
						if("Y".equals(sCAR_YN)) {
							
							String sA3_YD_WRK_PROG_STAT = "0";
							String sA3_STOCK_ID 		= "";
							int    iA3_TO_X_AXIS 		= 0;
							String sA3_YD_CRN_SCH_ID 	= "";
							
							jrParam.setField("YD_GP"		, YmConstant.YD_GP_3); //야드구분
							jrParam.setField("YD_EQP_ID"	, "3ACRA3"			); //A3 크레인
							jrParam.setField("PAGE_NO"		, "1"				); 
							jrParam.setField("PAGE_SIZE"	, "10"				); 
							
							JDTORecordSet jrA3CrnInfo = commDao.select3(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getbtCrnWrkMgtjmNew_PIDEV", logId, methodNm, "A3 크레인 스케줄 정보 조회");
							
							if(jrA3CrnInfo.size() > 0) {
								sA3_YD_WRK_PROG_STAT 	= commUtils.nvl(jrA3CrnInfo.getRecord(0).getFieldString("YD_WRK_PROG_STAT"),""); //A3 크레인 진행 상태
								sA3_STOCK_ID			= commUtils.nvl(jrA3CrnInfo.getRecord(0).getFieldString("STOCK_ID"),"_"); //A3 작업하는 코일 번호
								iA3_TO_X_AXIS			= Integer.parseInt(commUtils.nvl(jrA3CrnInfo.getRecord(0).getFieldString("YD_DN_WO_LOC_XAXIS"),"0")); //A3 권하 위치 X 좌표 값
								sA3_YD_CRN_SCH_ID		= commUtils.nvl(jrA3CrnInfo.getRecord(0).getFieldString("YD_CRN_SCH_ID"),""); //A3 크레인 스케줄id
							}
							
							if(!"".equals(sA3_YD_CRN_SCH_ID)) {
								if(!sA3_YD_CRN_SCH_ID.equals(sYD_CRN_SCH_ID)) {
									//A3 크레인이 상태 체크
									if("S".equals(sA3_YD_WRK_PROG_STAT) 
											|| "1".equals(sA3_YD_WRK_PROG_STAT)
											|| "2".equals(sA3_YD_WRK_PROG_STAT)
											|| "3".equals(sA3_YD_WRK_PROG_STAT) ) {
										
										if("S".equals(sA3_STOCK_ID.substring(0,1))) { //권상한 코일이 scarp 코일 인 경우
											
											String sAPPLY060_3ASCXX_YN = YmComm.BCoilApplyYn("APP060","3","3ASCXX_YN"); //SPM1 추출시 SCRAP 차량 도착 여부에 따른 처리 적용여부
											
											if("N".equals(sAPPLY060_3ASCXX_YN)) {
												//권하 할 수 있는 Buff Skid 가 만들어 져  있지  않은 경우 --> A4 가  작업
												sA3_CanNotWork_YN = "Y";
											}	
											
										} else { //권상한 코일이 일반 코일인 경우
											
											if(iA3_TO_X_AXIS > iBASE_X_AXIS) {
												//권하위치로 A3 가  갈 수 없는 경우 --> A4 가 작업
												sA3_CanNotWork_YN = "Y";
											}
										}
									} 
								}
							}
							
							commUtils.printLog(logId, "■ ■ ■ A3 크레인  >> 진행상태: " + sA3_YD_WRK_PROG_STAT 
					                + " ,STOCK_ID: " + sA3_STOCK_ID 
					                + " ,TO위치 X좌표: " + iA3_TO_X_AXIS
					                + " ,크레인스케줄ID: " + sA3_YD_CRN_SCH_ID
					                + " ,A3 크레인 권상한 상태로 작업 불가 여부 : " + sA3_CanNotWork_YN
					                + " ■ ■ ■"
					                , "SL");
							
							//Scrap 차량이 진입했고 A3 작업인데 TO위치가 18Span 06열 보다 왼쪽이고 A4 크레인이 고장 또는 OFF 라인이 이 아니면 
							if( !"XX010101".equals(sYD_DN_WO_LOC)
									&& "3ACRA3".equals(ydEqpId)
									&& "N".equals(sA4_STAT)
									&& (iTO_X_AXIS > iBASE_X_AXIS || sA3_CanNotWork_YN == "Y") ) {
								
								//****** 설비ID를 A3 호기에서 A4 호기로 변경한다. *******
								ydEqpId = "3ACRA4";
								
								
								//작업예약 크레인 변경
								//jrParam.setField("YD_WRK_PLAN_CRN"	, ydEqpId); 	
								//jrParam.setField("YD_WBOOK_ID"		, ydWbookId); 	
								
								//commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPlanCrn", logId, methodNm, "작업예약테이블의 야드작업계획크레인 변경 쿼리");
								
								//크레인스케줄 작업 크레인 변경
								jrParam.setField("YD_EQP_ID"		, ydEqpId); 	
								jrParam.setField("YD_CRN_SCH_ID"	, sYD_CRN_SCH_ID); 	
								
								commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnSchYdEqpId", logId, methodNm, "크레인스케줄 테이블의 설비id(크레인) 변경 쿼리");
							} 
						}
					}
				}
			}
			//-------------------------------------------------------------------------------------------------
			
			
			/*********************************************************
			 * 트랜잭션 분리로 인하여 동일 작업예약Id로 20180426
			 * 크레인 스케줄이 중복 생성되었을 때 삭제 
			 *********************************************************/
			String sAPP000 = YmComm.BCoilApplyYn("APP000","3","ALL");
			if ("Y".equals(sAPP000)) {
				JDTORecord jrSchChkMsg = JDTORecordFactory.getInstance().create();
				jrSchChkMsg.setResultCode(logId);	//Log ID
				jrSchChkMsg.setResultMsg(methodNm);	//Log Method Name

				jrSchChkMsg.setField("JMS_TC_CD"         , "YMYMJ100"               ); //JMSTC코드
				jrSchChkMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrSchChkMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrSchChkMsg.setField("YD_SCH_CD"  	   	 , ydSchCd                  ); //스케줄ID
				jrSchChkMsg.setField("YD_WBOOK_ID"  	 , ydWbookId                ); //작업예약ID
				
				jrRtn = commUtils.addSndData(jrRtn, jrSchChkMsg);
			}
			
				
			
			/**********************************************************
			* 5.크레인작업지시 호출
  			**********************************************************/

			if ("W".equals(ydEqpStat)) {
				//야드설비상태가 대기이면 내부크레인작업지시요구 전송
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("JMS_TC_CD"         , "YMYMJ001"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
				
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			} 
			
			//DC Line-Off 3ADC01LM, 3BDC01LM, 3CDC01LM
			if ("3CDC01LM".equals(ydSchCd)||"3BDC01LM".equals(ydSchCd)||"3ADC01LM".equals(ydSchCd)) {
				//해당크레인 크레인스케줄에 같은 스케줄코드로 상태가 S,1 인 경우 긴급작업으로
				//지금 만들어진 스케줄을 기동시킨다. (이전스케줄 삭제 전문 발송)
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getWrkCrnSchId 
				SELECT YD_CRN_SCH_ID 
				FROM  TB_YM_CRNSCH
				WHERE YD_SCH_CD = :V_YD_SCH_CD
				AND   YD_EQP_ID = :V_YD_EQP_ID
				AND   DEL_YN = 'N'
				AND   YD_WRK_PROG_STAT IN  ('1', 'S', 'W')
				ORDER BY YD_CRN_SCH_ID DESC */
				jrParam.setField("YD_EQP_ID"	, ydEqpId); 	
				jrParam.setField("YD_SCH_CD"	, ydSchCd); 
				JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getWrkCrnSchId", logId, methodNm, "해당크레인 크레인스케줄에 같은 스케줄코드로 상태가 S,1 인 CRANE 스케줄ID 조회"); 
				
				if (rsResult.size() > 1) {

					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

					jrYdMsg.setField("JMS_TC_CD"         , "YMYMJ304"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
					jrYdMsg.setField("YD_SCH_CD"  	   	 , ydSchCd                  ); //스케줄ID
					jrYdMsg.setField("YD_WBOOK_ID"  	 , ydWbookId                ); //작업예약ID
					
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}
			}
			
			String sAPP023_YN = YmComm.BCoilApplyYn("APP023","3","2");
			commUtils.printLog(logId,  "SPM2도 DC LINE OFF 처럼 동일하게 적용여부:" + sAPP023_YN, "SL");
			if (sAPP023_YN.equals("Y")) {
				if ("3EKD02LM".equals(ydSchCd)) { //SPM2 추출
					/* 
					SELECT CS.YD_CRN_SCH_ID, CM.STOCK_ID 
					  FROM TB_YM_CRNSCH    CS
					     , TB_YM_CRNWRKMTL CM
					 WHERE CS.YD_SCH_CD = :V_YD_SCH_CD
					   AND CS.YD_EQP_ID = :V_YD_EQP_ID
					   AND CS.DEL_YN = 'N'
					   AND CS.YD_WRK_PROG_STAT IN  ('1', 'S', 'W')
					   AND CM.DEL_YN = 'N'
					   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
					 ORDER BY DECODE(SUBSTR(CM.STOCK_ID, 1, 1), 'S', 1, 2)
					        , CASE WHEN SUBSTR(CM.STOCK_ID, 1, 1) = 'S' THEN TO_NUMBER(CS.YD_CRN_SCH_ID)
					               ELSE TO_NUMBER(CS.YD_CRN_SCH_ID) * -1 END
					 */
					jrParam.setField("YD_EQP_ID"	, ydEqpId); 	
					jrParam.setField("YD_SCH_CD"	, ydSchCd); 
					JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getWrkCrnSchIdSPM2", logId, methodNm, "해당크레인 크레인스케줄에 같은 스케줄코드로 상태가 S,1 인 CRANE 스케줄ID 조회");
					
					if (rsResult.size() > 1) {
	
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
						jrYdMsg.setField("JMS_TC_CD"         , "YMYMJ306"               ); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
						jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
						jrYdMsg.setField("YD_SCH_CD"  	   	 , ydSchCd                  ); //스케줄ID
						jrYdMsg.setField("YD_WBOOK_ID"  	 , ydWbookId                ); //작업예약ID
						
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
				}
			}
			
			/*****************************
			 * EC LineOff
			 * - WB61, WB62.. 순으로 작업
			 *****************************/
			String sAPP039_YN = YmComm.BCoilApplyYn("APP039","3","1"); //EC LineOff
			commUtils.printLog(logId,  "EC LineOff 작업순서 적용여부:" + sAPP039_YN, "[APP039]");
			if ("Y".equals(sAPP039_YN)) {
				if ("3CEC02LM".equals(ydSchCd)) { //EC LineOff
					/* 
					SELECT YD_CRN_SCH_ID 
					FROM  TB_YM_CRNSCH
					WHERE YD_SCH_CD = :V_YD_SCH_CD
					AND   YD_EQP_ID = :V_YD_EQP_ID
					AND   DEL_YN = 'N'
					AND   YD_WRK_PROG_STAT IN  ('1', 'S', 'W')
					ORDER BY YD_CRN_SCH_ID DESC
					 */
					jrParam.setField("YD_EQP_ID"	, ydEqpId); 	
					jrParam.setField("YD_SCH_CD"	, ydSchCd); 
					JDTORecordSet rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getWrkCrnSchId", logId, methodNm, "해당크레인 크레인스케줄에 같은 스케줄코드로 상태가 S,1 인 CRANE 스케줄ID 조회");
					
					if (rsResult.size() > 1) {
	
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
						jrYdMsg.setField("JMS_TC_CD"         , "YMYMJ306"               ); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
						jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
						jrYdMsg.setField("YD_SCH_CD"  	   	 , ydSchCd                  ); //스케줄ID
						jrYdMsg.setField("YD_WBOOK_ID"  	 , ydWbookId                ); //작업예약ID
						
						jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
					}
				}
			}


			commUtils.printParam("AA", jrRtn);
			commUtils.printLog(logId, methodNm + "[스케쥴메인종료]", "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
		
	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 작업예약재료 수정
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updCrnSchWB(JDTORecord jrParam) throws DAOException {
		String methodNm = "크레인스케줄 작업예약재료 수정[BCoilSchSeEJB.updCrnSchWB] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWmStrLoc 
			--크레인스케줄 작업예약재료 저장위치 수정
			 MERGE INTO TB_YM_WRKBOOKMTL WM USING (
			 SELECT YD_WBOOK_ID
			       ,STOCK_ID
			       ,:V_MODIFIER AS MODIFIER
			       ,SYSDATE     AS MOD_DDTT
			       ,STACK_COL_GP
			       ,STACK_BED_GP
			       ,STACK_LAYER_GP
			       ,YD_UP_COLL_SEQ 
			   FROM (SELECT WM.*
			           FROM (SELECT WB.YD_WBOOK_ID
			                       ,WB.YD_SCH_CD
			                       ,WM.STOCK_ID
			                       ,SL.STACK_COL_GP
			                       ,SL.STACK_BED_GP
			                       ,SL.STACK_LAYER_GP
			                       ,SL.STACK_COL_GP||SL.STACK_BED_GP||SL.STACK_LAYER_GP AS YD_STR_LOC
			                       ,SL.STACK_COL_GP||SL.STACK_BED_GP AS YD_STK_COL_BED
			                       ,RANK() OVER(PARTITION BY SL.STACK_COL_GP,SL.STACK_BED_GP
			                                        ORDER BY SL.STACK_COL_GP,SL.STACK_BED_GP,SL.STACK_LAYER_GP) AS YD_UP_COLL_SEQ
			                       
			                   FROM TB_YM_WRKBOOK    WB
			                       ,TB_YM_WRKBOOKMTL WM
			                       ,TB_YM_STACKLAYER SL
			                       ,TB_YM_STOCK      ST
			                  WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
			                    AND WM.STOCK_ID    = SL.STOCK_ID
			                    AND WM.STOCK_ID    = ST.STOCK_ID
			                    AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
			                    AND WB.DEL_YN      = 'N'
			                    AND WM.DEL_YN      = 'N'
			                    AND SL.STACK_LAYER_STAT = 'C'
			                    AND WB.YD_GP=SUBSTR(SL.STACK_COL_GP,1,1)
			                  ORDER BY YD_STR_LOC DESC) WM
			          ORDER BY YD_STR_LOC DESC)
			 ) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STOCK_ID = DD.STOCK_ID)
			 WHEN MATCHED THEN UPDATE SET
			      WM.MODIFIER       = DD.MODIFIER
			     ,WM.MOD_DDTT       = DD.MOD_DDTT
			     ,WM.STACK_COL_GP   = DD.STACK_COL_GP
			     ,WM.STACK_BED_GP   = DD.STACK_BED_GP
			     ,WM.STACK_LAYER_GP = DD.STACK_LAYER_GP
			     ,WM.YD_UP_COLL_SEQ = DD.YD_UP_COLL_SEQ
			    */ 
			
			commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWmStrLoc", logId, methodNm, "작업예약재료 저장위치 수정");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 오퍼레이션명 : B열연 코일야드 크레인 스케줄 GROUPING PARAMETER DATA SETTING
	 *  
	 * @param  String     szEqpId, szSchCd, rsMinWrkBookMtl, rsReturn
	 * @return boolean    intRtnVal 1: 성공, -1:실패
	 * @throws JDTOException
	 */
	public int CrnSchGrp( String logId, String methodNms, JDTORecord jrParamSet, JDTORecordSet rsReturn )throws JDTOException  {
    	String 	methodNm = "그룹핑 파라미터 셋팅 [BCoilSchSeEJB.CrnSchGrp] < " + methodNms;
    	
    	JDTORecordSet rsCrnSchResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
    	JDTORecordSet jsWrkBookMtl   = JDTORecordFactory.getInstance().createRecordSet("Temp");
		//레코드셋 정렬 시
		String 	szLogMsg="";
		int 	intRtnVal = 0;
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			String szWbookId 			= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"));
			String szSchCd   			= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"));  
			String stackLayerChkYn   	= commUtils.trim(jrParamSet.getFieldString("STACK_LAYER_CHK_YN"));  
			if (stackLayerChkYn.equals("Y")) {
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getbcoilWrkBookMtl
				WITH DATA_TABLE AS (
				 -- 대상재 최신적재위치 READ
				SELECT DISTINCT A.STACK_COL_GP
				              , A.STACK_BED_GP
				              , A.STACK_LAYER_GP
				              , A.STOCK_ID
				              , A.STACK_LAYER_STAT
				  FROM TB_YM_STACKLAYER A
				     , (SELECT A1.STACK_COL_GP
				             , A1.STACK_BED_GP
				             , A1.STACK_LAYER_GP
				          FROM TB_YM_STACKLAYER A1 
				             , TB_YM_WRKBOOKMTL B1
				         WHERE A1.STOCK_ID = B1.STOCK_ID
				           AND B1.DEL_YN = 'N'
				           AND B1.YD_WBOOK_ID = :V_YD_WBOOK_ID ) B
				 WHERE A.STACK_COL_GP = B.STACK_COL_GP
				   AND ((  B.STACK_LAYER_GP = '01' AND (A.STACK_BED_GP|| A.STACK_LAYER_GP IN (B.STACK_BED_GP||'01', 
				                                                                              B.STACK_BED_GP||'02', 
				                                                                          TO_CHAR(TO_NUMBER(B.STACK_BED_GP) - 1,'FM00') ||'02')))
				        OR(B.STACK_LAYER_GP = '02' AND (A.STACK_BED_GP|| A.STACK_LAYER_GP IN (B.STACK_BED_GP||'02'))) 
				                                        
				       )
				   AND STACK_LAYER_STAT IN ('C','U')     
				)
				SELECT CASE WHEN STACK_LAYER_STAT = 'C' AND YD_WBOOK_ID IS NOT NULL THEN ROWNUM + 100
				            WHEN STACK_LAYER_STAT = 'C' THEN ROWNUM
				            ELSE 0 END HANDLING_CNT
				     , CASE WHEN YD_WBOOK_ID IS NOT NULL THEN 'S' --주작업
				            ELSE 'W' END YD_TO_LOC_DCSN_MTD   
				     , D.* 
				  FROM
				(
				SELECT A.STACK_COL_GP
				     , A.STACK_BED_GP
				     , A.STACK_LAYER_GP
				     , A.STOCK_ID
				     , A.STACK_LAYER_STAT
				     , B.YD_WBOOK_ID
				     , B.YD_UP_COLL_SEQ
				     , C.YD_CRN_SCH_ID
				     , C.YD_SCH_CD   AS CRNSCH_YD_SCH_CD
				     , C.YD_WBOOK_ID AS CRNSCH_WBOOK_ID
				     , C.YD_WRK_PROG_STAT   
				  FROM DATA_TABLE A
				     , (SELECT YD_WBOOK_ID 
				             , STOCK_ID
				             , YD_UP_COLL_SEQ
				          FROM TB_YM_WRKBOOKMTL
				         WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID  ) B
				     , (SELECT AA.YD_CRN_SCH_ID
				             , AA.YD_WRK_PROG_STAT
				             , BB.STOCK_ID
				             , AA.YD_SCH_CD
				             , AA.YD_WBOOK_ID
				          FROM USRYMA.TB_YM_CRNSCH AA
				             , USRYMA.TB_YM_CRNWRKMTL BB
				         WHERE AA.YD_CRN_SCH_ID = BB.YD_CRN_SCH_ID
				           AND AA.DEL_YN = 'N'
				           AND BB.DEL_YN = 'N' ) C      
				 WHERE A.STOCK_ID = B.STOCK_ID(+)
				   AND A.STOCK_ID = C.STOCK_ID(+)
				 ORDER BY A.STACK_COL_GP, A.STACK_BED_GP, A.STACK_LAYER_GP DESC 
				 ) D
				*/ 
				jsWrkBookMtl = commDao.select(jrParamSet, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getbcoilWrkBookMtl", logId, methodNm, "작업예약 재료정보 조회");
				if (jsWrkBookMtl.size() <= 0) {
					szLogMsg = methodNm+ "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, szLogMsg, "SL");    			
	    			return YmConstant.RETN_INT_FAILURE;				
				}
			} else {
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getbcoilWrkBookMtlNoLayer 
				SELECT STOCK_ID
				     , STACK_COL_GP
				     , STACK_BED_GP
				     -- RULE에 대표 BED가 있으면 사용함       
				     , NVL((SELECT DTL_ITM1
				              FROM USRYMA.TB_YM_RULE
				             WHERE REPR_CD_GP = 'YMSCH'
				               AND ITEM       =  SUBSTR(A.STACK_COL_GP,3,2)),STACK_BED_GP) AS STACK_HEAD_BED_GP      
				               
				     , STACK_LAYER_GP
				     , '1' AS HANDLING_CNT
				     , 'S' AS YD_TO_LOC_DCSN_MTD
				     , 'C' STACK_LAYER_STAT
				  FROM TB_YM_WRKBOOKMTL A
				WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
				  AND DEL_YN = 'N' 
				*/	  
				jsWrkBookMtl = commDao.select(jrParamSet, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getbcoilWrkBookMtlNoLayer", logId, methodNm, "적재위치 CHECK 안함 작업예약 재료정보 조회");
				if (jsWrkBookMtl.size() <= 0) {
					szLogMsg = methodNm+ "크레인작업재료조회 >> 조회 Data 없음";
					commUtils.printLog(logId, szLogMsg, "SL");    			
	    			return YmConstant.RETN_INT_FAILURE;				
				}	
			}
			JDTORecord jrWrkBookMtl = JDTORecordFactory.getInstance().create();
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			//작업예약재료를 조회해서 받는다. 코일은 재료별로 처리하도록...
			for (int Loop_i = 1; Loop_i <= jsWrkBookMtl.size(); Loop_i++) {
				jsWrkBookMtl.absolute(Loop_i);
				
				jrWrkBookMtl = JDTORecordFactory.getInstance().create();
				jrWrkBookMtl.setRecord( jsWrkBookMtl.getRecord() );
				
				//권상대기라면 해당 재료의 스케줄 코드를 확인한다.
				if (commUtils.trim(jrWrkBookMtl.getFieldString("STACK_LAYER_STAT")).equals("U") ) {
					//현재 스케줄 코드와 상위베드단 재료의 스케줄코드를 비교한다.
					if (!commUtils.trim(jrWrkBookMtl.getFieldString("CRNSCH_WBOOK_ID")).equals(szWbookId)) {

						commUtils.printLog(logId, "작업 상태:" + jrWrkBookMtl.getFieldString("YD_WRK_PROG_STAT") , "SL");  
			    			
    		    		if ("W".equals(jrWrkBookMtl.getFieldString("YD_WRK_PROG_STAT"))) {
    		    			
    		    			recPara = JDTORecordFactory.getInstance().create();
    		    		    recPara.setField("YD_CRN_SCH_ID", jrWrkBookMtl.getFieldString("YD_CRN_SCH_ID"));
    		    		    recPara.setField("YD_SCH_PRIOR"	, "1"); 
    		    		         
    		    		    /*com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updbcoilCrnSchPrior
    		    		    UPDATE TB_YM_CRNSCH
    		    		       SET MODIFIER = :V_MODIFIER
    		    		         , MOD_DDTT = SYSDATE
    		    		         , YD_SCH_PRIOR = :V_YD_SCH_PRIOR 
    		    		     WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
    		    		    */		 
	    		    		intRtnVal = commDao.update(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updbcoilCrnSchPrior", logId, methodNm, "크레인스케쥴 갱신");
	    		    		
	    		    		if (intRtnVal <= 0) {
	    						szLogMsg = methodNm+ "크레인스케쥴 UPDATE 할 항목이 없습니다.";
	    						commUtils.printLog(logId, szLogMsg, "SL");  
	    		    			return YmConstant.RETN_INT_FAILURE;
	    		    		}	
//					    	3. 해당 작업예약 update				    		    			
	    		    		/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updbcoilWrkBookPrior 
	    		    		UPDATE TB_YM_WRKBOOK
	    		    		   SET MODIFIER = :V_MODIFIER
	    		    		     , MOD_DDTT = SYSDATE
	    		    		     , YD_SCH_PRIOR = :V_YD_SCH_PRIOR
	    		    		     , YD_SCH_CD      = :V_YD_SCH_CD
	    		    		 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
    		    		    */ 
	    		    		recPara.setField("YD_SCH_CD"	, jrWrkBookMtl.getField("CRNSCH_YD_SCH_CD"));
    		    		    recPara.setField("YD_WBOOK_ID"	, jrWrkBookMtl.getField("CRNSCH_WBOOK_ID"));
	    		    		intRtnVal = commDao.update(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updbcoilWrkBookPrior", logId, methodNm, "작업예약 갱신");
	    		    		if (intRtnVal <= 0) {
	    						szLogMsg = methodNm+ "작업예약 우선순위 UPDATE 실패!.";
	    						commUtils.printLog(logId, szLogMsg, "SL");  
	    		    			return YmConstant.RETN_INT_FAILURE;
	    		    		}	
    		    		}
    				}	
					//같다면 앞에서 이미 등록 되었기때문에 따로 등록하지않는다.
					szLogMsg = methodNm+ "그룹작업이 끝난 재료입니다.";
					commUtils.printLog(logId, szLogMsg, "SL");  
	    		    
					
				} else {
					//STACK_LAYER_STAT = 'U' 상태 RECORD 제거
					rsCrnSchResult.addRecord(jrWrkBookMtl);
				}	
			}				

			//레코드셋 정렬
			JDTORecord recAfter = null;
			JDTORecord recCurrt = null;

			String  sCAR_KIND ="TT";
			
			JDTORecord recInTemp = JDTORecordFactory.getInstance().create();
 			recInTemp.setField("YD_WBOOK_ID", szWbookId);
 			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getbcoilStockCar 
 			SELECT A.YD_WBOOK_ID         AS YD_WBOOK_ID
 			     , A.STOCK_ID  
 			     , B.SHEAR_SUPPLY_SEQ    AS YD_CAR_UPP_LOC_CD   --차상위치
 			     , (CASE WHEN MAX(B.CAR_NO2) LIKE 'GT%' THEN 'TT' 
 			             WHEN MAX(B.CAR_NO2) IS NULL THEN 'X' ELSE 'T' END) AS CAR_KIND
 			 FROM TB_YM_WRKBOOKMTL A
 			     ,TB_YM_STOCK B
 			 WHERE A.STOCK_ID=B.STOCK_ID
 			   AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
 			   AND A.DEL_YN='N'
 			 GROUP BY A.YD_WBOOK_ID, A.STACK_COL_GP, A.STACK_BED_GP,A.STOCK_ID,B.SHEAR_SUPPLY_SEQ
 			ORDER BY DECODE(CAR_KIND,'T',YD_CAR_UPP_LOC_CD,'1'),MAX(A.YD_UP_COLL_SEQ)
 			*/
			JDTORecordSet rsStockCar = commDao.select(recInTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getbcoilStockCar", logId, methodNm, "차량정보");
			if (rsStockCar.size() <= 0) {
				szLogMsg = methodNm+ "작업재료 정보 이상.";
				commUtils.printLog(logId, szLogMsg, "SL");  
    			return YmConstant.RETN_INT_FAILURE;				
			}			
			rsStockCar.absolute(1);
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setRecord(rsStockCar.getRecord());
			
			sCAR_KIND = commUtils.trim(recInTemp.getFieldString("CAR_KIND"));
			String szSchCdGp = "";
			//(trailer인경우)
			if ("T".equals(sCAR_KIND)||"TR".equals(sCAR_KIND)) {
	 			
				szSchCdGp	=	szSchCd.substring(2, 4);
			}
			
			//차량작업이 아닌경우 정렬(trailer인경우)
 			if (!szSchCdGp.equals("TR")&& !szSchCdGp.equals("PT")&& !szSchCdGp.equals("TT")) {
				
				for (int Loop_i = 1; Loop_i < rsCrnSchResult.size(); Loop_i++) {
				
					for (int Loop_j = Loop_i + 1; Loop_j < rsCrnSchResult.size() + 1; Loop_j++) {
						
						rsCrnSchResult.absolute(Loop_i);
						recCurrt = rsCrnSchResult.getRecord();
						
						rsCrnSchResult.absolute(Loop_j);
						recAfter = rsCrnSchResult.getRecord();

						if (recCurrt.getFieldInt("HANDLING_CNT") > recAfter.getFieldInt("HANDLING_CNT")) {
							rsCrnSchResult = this.YmSortCoil(Loop_i, Loop_j, rsCrnSchResult);
							if (intRtnVal == -1) {
	    						szLogMsg = methodNm+ "<rsSort> 정렬 중 Error.";
	    						commUtils.printLog(logId, szLogMsg, "SL");  
	    		    			return YmConstant.RETN_INT_FAILURE;
							}
						}
					}//end of infor
				}//end of outfor 
			
 			}


			//재료 중복
 			String sSTOCK_ID ="";
			for (int Loop_i = 1; Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				rsCrnSchResult.absolute(Loop_i);
				/*System.out.println("HandlingCount = " 
				                     + rsReturn.getRecord().getFieldString("HANDLING_CNT") 
				                     + rsReturn.getRecord().getFieldString("STL_NO")
			                         + rsReturn.getRecord().getFieldString("YD_TO_LOC_DCSN_MTD"));
			    */                     
				recCurrt = rsCrnSchResult.getRecord();
		

				if (!rsCrnSchResult.getRecord().getFieldString("STOCK_ID").equals("") && !rsCrnSchResult.getRecord().getFieldString("STOCK_ID").equals(sSTOCK_ID)) {
					sSTOCK_ID = rsCrnSchResult.getRecord().getFieldString("STOCK_ID");
					rsReturn.addRecord(recCurrt);
				}

			}
			
			commUtils.printLog(logId, methodNm, "S-");	

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return YmConstant.RETN_INT_SUCCESS;

	} //end of Y5CrnSchSortCoil()
	
	
	/**
     * 오퍼레이션명 : 레코드 치환(H/J)
     *  
     * @param  ● recPara1, recPara2, recResult
     * @return ● intRtnVal '1': 성공   '-1': 실패
     * @throws ● JDTOException
     */
    public JDTORecordSet YmSortCoil (int intLoop_i, int intLoop_j, JDTORecordSet rsCrnSchResult) {

    	JDTORecordSet rsTemp = null; 
    	JDTORecord recTemp = null;
    	int intRtnVal = 0;
		
		try{
			rsTemp = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			for (int Loop_i = 1;  Loop_i <= rsCrnSchResult.size(); Loop_i++) {
				if (Loop_i == intLoop_i) {
					rsCrnSchResult.absolute(intLoop_j);
					recTemp = rsCrnSchResult.getRecord();
				}else if (Loop_i == intLoop_j) {
					rsCrnSchResult.absolute(intLoop_i);
					recTemp = rsCrnSchResult.getRecord();
				}else{
					rsCrnSchResult.absolute(Loop_i);
					recTemp = rsCrnSchResult.getRecord();
				}
				rsTemp.addRecord(recTemp);
			}
			
		}catch(Exception e) {
			String szMsg = "Error : " + e.getLocalizedMessage();
        }//end of try~catch
		
		return rsTemp;
    }//end of YmSortCoil()
    
	
    /**
     * 오퍼레이션명 : 창고스케줄링 Handling Data 크레인사양Check
     *  
     * @param  ● szEqpId, vecHandledData, vecResult
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int HandledDataCrnSpec (String logId, String methodNms, String szYD_SCH_CD, Vector vecHandledData, Vector vecResult) throws JDTOException {
    	String methodNm = "창고스케줄링 Handling Data 크레인사양 [GdsYsSchSeEJB.HandledDataCrnSpec] < " + methodNms;
    	String LocalmethodNm = "창고스케줄링 Handling Data 크레인사양 [GdsYsSchSeEJB.HandledDataCrnSpec]";
		JDTORecord    recPara    = null;
		JDTORecordSet rsPara 	 = null;
		JDTORecordSet rsMain 	 = null;
		
		int intMtlSh      = 0;	
		try{	

			commUtils.printLog(logId, methodNm , "S+");			

			// 대상재 그대로  vecHandledData -> Vector vecResult 
			commUtils.printLog(logId,  LocalmethodNm+ "대상재 동일하게  vecHandledData -> vecResult move 처리" , "SL");

			for (int Loop_i = 0; Loop_i < vecHandledData.size(); Loop_i++) {
    			rsPara = (JDTORecordSet)vecHandledData.get(Loop_i) ;
    			rsPara.first();
    			rsMain = JDTORecordFactory.getInstance().createRecordSet("Temp");
    			for (int Loop_j = 0; Loop_j < rsPara.size(); Loop_j++) {
    				rsPara.absolute(Loop_j+1);
    				//rsParac의 레코드를 읽어온다.
    				recPara = rsPara.getRecord();
					rsMain.addRecord(recPara);
    			}//end of infor
    			vecResult.add(rsMain);
    		}//end of outfor

			commUtils.printParam(logId, vecResult);

			commUtils.printLog(logId, methodNm, "S-");			

			return 1;
			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of HandledDataCrnSpec()	
    

	/**
     * 오퍼레이션명 : 스케줄링 크레인 스케줄 등록
     *  
     * @param  ● vResult, msgRecord
     * @return ● intRtnVal
     * @throws ● JDTOException
     */
    public int CrnSchIns(String logId, String methodNms , JDTORecord jrParamSet , JDTORecordSet jsRecset) throws JDTOException {
   		String methodNm = "스케줄링 크레인 스케줄 등록[BCoilSchSeEJB.CrnSchIns] < " + methodNms;
		JDTORecord recInCrn    = null;
		int intRtnVal = 0;
		String szName = "SYSTEM";
		String szLogMsg = "";
		
		try{
			
			commUtils.printLog(logId, methodNm, "S+");
			
			String szEqpId			= commUtils.trim(jrParamSet.getFieldString("YD_EQP_ID"  ));
			String szSchCd  		= commUtils.trim(jrParamSet.getFieldString("YD_SCH_CD"  ));
			String szydWbookId      = commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_ID"  ));

			String szYD_SCH_PRIOR  	= commUtils.trim(jrParamSet.getFieldString("YD_SCH_PRIOR"  ));
			String szYD_WBOOK_DT  	= commUtils.trim(jrParamSet.getFieldString("YD_WBOOK_DT"  ));
			String szYD_TO_LOC_GUIDE= commUtils.trim(jrParamSet.getFieldString("YD_TO_LOC_GUIDE"  ));
			String stackLayerChkYn	= commUtils.trim(jrParamSet.getFieldString("STACK_LAYER_CHK_YN")); 
			String modifier			= commUtils.trim(jrParamSet.getFieldString("MODIFIER")); //수정자(Backup Only)
			String WbRegister       = commUtils.trim(jrParamSet.getFieldString("WB_REGISTER")); //작업예약 등록자
			JDTORecordSet jsWbook = JDTORecordFactory.getInstance().createRecordSet("Temp");
			
			commUtils.printParam(logId, jsRecset);
			
 			//크레인 스케줄에 Insert한다.	
			
			JDTORecord recInCrnMtl = null;
			
			for (int i = 1; i <= jsRecset.size(); i++) {

				jsRecset.absolute(i);
				recInCrn = JDTORecordFactory.getInstance().create();
				recInCrn  = jsRecset.getRecord();
				
				
				recInCrn.setField("YD_WBOOK_ID"			, szydWbookId);
				recInCrn.setField("STOCK_ID"			, commUtils.trim(recInCrn.getFieldString("STOCK_ID"  )));
				//작업예약ID 조회-----------------------------------------------------------------------------------------
				if (!"".equals(szydWbookId)) {
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookChk
					SELECT YD_WBOOK_ID 
					  FROM TB_YM_WRKBOOK A
					 WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID
					   AND A.DEL_YN      = 'N'
					   AND NOT EXISTS (SELECT 1 FROM TB_YM_CRNSCH B 
					                                , USRYMA.TB_YM_CRNWRKMTL C
					                           WHERE B.YD_CRN_SCH_ID = C.YD_CRN_SCH_ID
					                            AND B.DEL_YN='N' 
					                            AND C.DEL_YN='N' 
					                            AND C.STOCK_ID=:V_STOCK_ID
					                            AND B.YD_WBOOK_ID=A.YD_WBOOK_ID)
					*/
					jsWbook = commDao.select(recInCrn, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnSchWbookChk", logId, methodNm, "작업예약 조회(스케쥴생선 전)");
					
				}
				
				if (jsWbook == null || jsWbook.size() <= 0) {
					 
					szLogMsg = "["+ methodNm +"]크레인 스케줄 등록중  이미 크레인 스케쥴 존재 함.Error!! ydWbookId: " + szydWbookId;
					commUtils.printLog(logId, szLogMsg, "SL");
					return YmConstant.RETN_INT_FAILURE;		 
				}
				//-------------------------------------------------------------------------------------------------------

				
				/**********************************************************
				*  크레인 스케줄 등록
				**********************************************************/			
				//크레인스케줄ID를 할당받는다
				String ydCrnSchId = commDao.getSeqId(logId, methodNm, "CrnSch");

				recInCrn.setField("MODIFIER",			modifier);
				recInCrn.setField("YD_CRN_SCH_ID",		ydCrnSchId);
				recInCrn.setField("YD_WBOOK_ID",      	szydWbookId);
				recInCrn.setField("YD_EQP_ID",        	szEqpId);
				recInCrn.setField("YD_GP",            	recInCrn.getFieldString("STACK_COL_GP").substring(0,1));
				recInCrn.setField("YD_BAY_GP",        	recInCrn.getFieldString("STACK_COL_GP").substring(1,2));
				recInCrn.setField("YD_SCH_CD",        	szSchCd);	
				recInCrn.setField("REGISTER",         	recInCrn.getFieldString("HANDLING_CNT"));	
				recInCrn.setField("YD_SCH_PRIOR",     	szYD_SCH_PRIOR);
				recInCrn.setField("YD_WBOOK_DT",      	szYD_WBOOK_DT);
				recInCrn.setField("YD_SCH_ST_GP",     	"A");
				recInCrn.setField("YD_UP_WO_LOC",     	recInCrn.getFieldString("STACK_COL_GP") + recInCrn.getFieldString("STACK_BED_GP"));
				recInCrn.setField("YD_UP_WO_LAYER",   	recInCrn.getFieldString("STACK_LAYER_GP"));
				recInCrn.setField("YD_TO_LOC_GUIDE", 	szYD_TO_LOC_GUIDE);
				recInCrn.setField("YD_WRK_PROG_STAT", 	"W");
				
				recInCrn.setField("YD_SCH_REQ_GP"   , 	WbRegister);
				
				if (commUtils.trim(recInCrn.getFieldString("YD_UP_WO_LOC")).equals("")) {
					szLogMsg = "["+ methodNm +"] 권상지시위치가 없습니다.";
					commUtils.printLog(logId, szLogMsg, "SL");    			
	    			return YmConstant.RETN_INT_FAILURE;				
				}

				/***************************************************************************
				 * 트랜잭션 분리 
				 ***************************************************************************/
				String sAPP000_YN = YmComm.BCoilApplyYn("APP000","3","1");   //트랜잭션 분리
				commUtils.printLog(logId,  "==========[[[ APP000 크레인 스케줄 등록 트랜잭션 분리 :" + sAPP000_YN + " ]]]============", "SL");
				 
				if ("Y".equals(sAPP000_YN)) {
					if (YmComm.execQueryId(recInCrn, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insYmCrnsch")) {
						intRtnVal = 1;
					} else {
						intRtnVal = 0;
					}
				} else {
					intRtnVal = commDao.insert(recInCrn, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insYmCrnsch", logId, methodNm, "TB_YM_CRNSCH 생성");
				}

				
				if (intRtnVal < 1) {
					szLogMsg = "["+ methodNm +"]크레인 스케줄 등록중  Error!! ErrorCode: " + intRtnVal;
					commUtils.printLog(logId, szLogMsg, "SL");
					return YmConstant.RETN_INT_FAILURE;
					
				}
  
				/**********************************************************
				*  크레인 스케줄 작업재료 등록
				**********************************************************/			
				recInCrnMtl = JDTORecordFactory.getInstance().create();
				recInCrnMtl.setField("YD_CRN_SCH_ID", ydCrnSchId);
				/*
				 * 기존의 MAIN_WRK_YN 은 주작업이 Y 보조작업이 N으로 들어옴 
				 * 크레인작업재료에는 보조작업여부에 값은 보조작업인경우 Y 주작업인경우 N로 셋팅!
				 */
				
				if (recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD").equals("W")) {
					recInCrnMtl.setField("YD_AID_WRK_YN", "Y"); //보조작업
				}else{
					recInCrnMtl.setField("YD_AID_WRK_YN", "N");
				}
				recInCrnMtl.setField("REGISTER"				, modifier);
				recInCrnMtl.setField("MOD_DDTT"				, "");
				recInCrnMtl.setField("STOCK_ID"				, commUtils.trim(recInCrn.getFieldString("STOCK_ID"  )));
				recInCrnMtl.setField("STACK_LAYER_GP"		, commUtils.trim(recInCrn.getFieldString("STACK_LAYER_GP"  )));
				recInCrnMtl.setField("YD_STK_LOT_TP"		, commUtils.trim(recInCrn.getFieldString("YD_STK_LOT_TP"  )));
				recInCrnMtl.setField("YD_STK_LOT_CD"		, commUtils.trim(recInCrn.getFieldString("YD_STK_LOT_CD"  )));
				recInCrnMtl.setField("HCR_GP"				, commUtils.trim(recInCrn.getFieldString("HCR_GP"  )));
				recInCrnMtl.setField("STL_PROG_CD"			, commUtils.trim(recInCrn.getFieldString("STL_PROG_CD"  )));
				recInCrnMtl.setField("YD_MTL_ITEM"			, commUtils.trim(recInCrn.getFieldString("YD_MTL_ITEM"  )));
				recInCrnMtl.setField("YD_ROUTE_GP"			, "");
				recInCrnMtl.setField("YD_TO_LOC_DCSN_MTD"	, commUtils.trim(recInCrn.getFieldString("YD_TO_LOC_DCSN_MTD")));
				//크레인작업재료 생성

				
				/***************************************************************************
				 * 트랜잭션 분리 
				 ***************************************************************************/
				if ("Y".equals(sAPP000_YN)) {
					if (YmComm.execQueryId(recInCrnMtl, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insYmCrnwrkmtl")) {
						intRtnVal = 1;
					} else {
						intRtnVal = 0;
					}
				} else {
					intRtnVal = commDao.insert(recInCrnMtl, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.insYmCrnwrkmtl", logId, methodNm, "TB_YM_CRNWRKMTL 생성");
				}
				 
				
				if (intRtnVal <= 0) {
					szLogMsg = "["+ methodNm +"] 크레인 스케줄 작업재료 등록중 실패: " + intRtnVal;
					commUtils.printLog(logId, szLogMsg, "SL");
					return YmConstant.RETN_INT_FAILURE;
				}
				
				
				if (stackLayerChkYn.equals("Y")) {
					/**********************************************************
					*  적치단의 재료상태를 권상대기로 변경
					**********************************************************/		
					recInCrn.setField("STACK_LAYER_STAT", "U");
					
					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerMtlStat  
					UPDATE TB_YM_STACKLAYER            
					   SET MOD_DDTT     = SYSDATE             
					     , MODIFIER     = :V_MODIFIER             
					     , STACK_LAYER_STAT = NVL(:V_STACK_LAYER_STAT,STACK_LAYER_STAT)
					 WHERE STACK_COL_GP = :V_STACK_COL_GP
					   AND STACK_BED_GP = :V_STACK_BED_GP
					   AND STACK_LAYER_GP = :V_STACK_LAYER_GP
					   AND STOCK_ID = :V_STOCK_ID
			    	 */  
					intRtnVal = commDao.update(recInCrn, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerMtlStat", logId, methodNm, "TB_YM_STACKLAYER 갱신");
					if (intRtnVal <= 0) {
						commUtils.printLog(logId, "[" + methodNm + "] 재료[" + recInCrn.getFieldString("STOCK_ID") + "]적재단 변경시 오류", "SL");
						return YmConstant.RETN_INT_FAILURE;
					}
					
				}  else {
					/**********************************************************
					*  적치단의 재료상태를 권상대기로 변경
					**********************************************************/		
					recInCrn.setField("STACK_LAYER_STAT", "U");
					
					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerMtlStatByStockId   
					UPDATE TB_YM_STACKLAYER            
					   SET MOD_DDTT     = SYSDATE             
					     , MODIFIER     = :V_MODIFIER             
					     , STACK_LAYER_STAT = NVL(:V_STACK_LAYER_STAT,STACK_LAYER_STAT)
					 WHERE SUBSTR(STACK_COL_GP,1,1) = '3'
					   AND STOCK_ID = :V_STOCK_ID
			    	 */  
					intRtnVal = commDao.update(recInCrn, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.updStackLayerMtlStatByStockId", logId, methodNm, "TB_YM_STACKLAYER 갱신");
					
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return  YmConstant.RETN_INT_SUCCESS;
        
    }//end of CrnSchIns()
     
    
	/**
     * 오퍼레이션명 : B열연 COIL YARD - TO 위치 결정
     * @param  inRecord, recGetCrnWrkMtl, rsResultCrnwrkmtl
     * @return int 성공:1, 실패:-1
     * @throws 
     */
    public JDTORecord LocSrcRngDataSet (String logId, String methodNms, JDTORecord inRecord)throws JDTOException{
    	String methodNm = "TO 위치 결정[BCoilSchSeEJB.LocSrcRngDataSet] < " + methodNms;
    	
    	JDTORecord jrLocSrcRngRtn = JDTORecordFactory.getInstance().create();
    	String szMsg        		= "";     	  
    	
    	int intRtnVal 				= 0 ;
    	String TcarTcSndyn   		= "N";    // 대차이동지시 

    	try{
        	commUtils.printLog(logId, methodNm, "S+");
        	//-------------------------------------------------------------------------------------------------------------
        	//	파라미터 확인
        	//-------------------------------------------------------------------------------------------------------------
			//파라미터 Null Check
        	String szWbookId	= commUtils.trim(inRecord.getFieldString("YD_WBOOK_ID" ));	
        	String szEqpId 		= commUtils.trim(inRecord.getFieldString("YD_EQP_ID"   ));	
        		

        	String sAPP048 = YmComm.BCoilApplyYn("APP048","3","1"); //A-E동간이적 적용여부
        	
			//-------------------------------------------------------------------------------------------------------------
			//작업예약을 조회한다. To위치 결정방법이  사용자 지정인지 알기위해서...
			//-------------------------------------------------------------------------------------------------------------
        	JDTORecordSet jsTemp 		= JDTORecordFactory.getInstance().createRecordSet("");
			
        	 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYmWrkbook

        	SELECT YD_WBOOK_ID      AS YD_WBOOK_ID
        	      ,REGISTER         AS REGISTER
        	      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
        	      ,MODIFIER         AS MODIFIER
        	      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
        	      ,DEL_YN           AS DEL_YN
        	      ,YD_GP            AS YD_GP
        	      ,YD_BAY_GP        AS YD_BAY_GP
        	      ,YD_SCH_CD        AS YD_SCH_CD
        	      ,YD_SCH_PRIOR     AS YD_SCH_PRIOR
        	      ,YD_SCH_PROG_STAT AS YD_SCH_PROG_STAT
        	      ,YD_SCH_ST_GP     AS YD_SCH_ST_GP
        	      ,YD_SCH_REQ_GP    AS YD_SCH_REQ_GP
        	      ,YD_AIM_YD_GP     AS YD_AIM_YD_GP
        	      ,YD_AIM_BAY_GP    AS YD_AIM_BAY_GP
        	      ,YD_CTS_RELAY_YN  AS YD_CTS_RELAY_YN
        	      ,YD_CTS_RELAY_BAY_GP  AS YD_CTS_RELAY_BAY_GP
        	      ,YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD
        	      ,YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
        	      ,YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
        	      ,YD_CAR_USE_GP
        	      ,TRN_EQP_CD       AS TRN_EQP_CD
        	      ,CAR_NO           AS CAR_NO
        	      ,CARD_NO          AS CARD_NO
        	   FROM TB_YM_WRKBOOK A
        	 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
			 */
			jsTemp = commDao.select(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYmWrkbook", logId, methodNm, "작업예약 조회"); 
	    	
	    	if (jsTemp == null || jsTemp.size() <= 0) {
				commUtils.printLog(logId, methodNm + "[작업예약종료]", "SL");
				
    			jrLocSrcRngRtn.setField("RTN", "-1");

				return jrLocSrcRngRtn;
				
			}			
			
			jsTemp.absolute(1);
			JDTORecord jrWbook = JDTORecordFactory.getInstance().create();
			jrWbook.setRecord(jsTemp.getRecord());
			
			String szSchCd 	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));
			String StockId 	= commUtils.trim(jrWbook.getFieldString("STOCK_ID"));
			
			String sYD_TO_LOC_GUIDE     = commUtils.trim(jrWbook.getFieldString("YD_TO_LOC_GUIDE"));
			String sYD_TO_LOC_GUIDE_FNL = commUtils.trim(jrWbook.getFieldString("YD_TO_LOC_GUIDE_FNL"));
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄 조회
			//-------------------------------------------------------------------------------------------------------------
			JDTORecordSet jsCrnsch = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord jrInPara = JDTORecordFactory.getInstance().create();
			jrInPara.setField("YD_WBOOK_ID"	, szWbookId);
			jrInPara.setField("YD_EQP_ID"	, szEqpId);

			if (StockId.substring(0,1).equals("S")) {
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCrnSchByWBookIdScrap
				WITH TEMP_DATA AS 
				(
				SELECT A.EQUIP_NO                AS YD_EQP_ID                       
				     , A.EQUIP_NAME              AS YD_EQP_NAME                     
				     , B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID                   
				     , B.REGISTER                AS REGISTER                        
				     , TO_CHAR(B.REG_DDTT       , 'YYYYMMDDHH24MISS') AS REG_DDTT                        
				     , B.MODIFIER                AS MODIFIER                        
				     , TO_CHAR(B.MOD_DDTT       , 'YYYYMMDDHH24MISS') AS MOD_DDTT                        
				     , B.DEL_YN                  AS DEL_YN                          
				     , B.YD_WBOOK_ID             AS YD_WBOOK_ID                     
				     , B.YD_GP                   AS YD_GP                           
				     , B.YD_BAY_GP               AS YD_BAY_GP                       
				     , B.YD_SCH_CD               AS YD_SCH_CD                       
				     , B.YD_SCH_ST_GP            AS YD_SCH_ST_GP                    
				     , B.YD_SCH_REQ_GP           AS YD_SCH_REQ_GP                   
				     , B.YD_SCH_PRIOR            AS YD_SCH_PRIOR                    
				     , B.YD_EQP_WRK_STAT         AS YD_EQP_WRK_STAT                 
				     , B.YD_WRK_PROG_STAT        AS YD_WRK_PROG_STAT                
				     , TO_CHAR(B.YD_WBOOK_DT    , 'YYYYMMDDHH24MISS') AS YD_WBOOK_DT
				     , TO_CHAR(B.YD_SCH_DT      , 'YYYYMMDDHH24MISS') AS YD_SCH_DT
				     , TO_CHAR(B.YD_WORD_DT     , 'YYYYMMDDHH24MISS') AS YD_WORD_DT
				     , TO_CHAR(B.YD_UP_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
				     , TO_CHAR(B.YD_DN_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
				     , B.YD_WRK_HDS_DD           AS YD_WRK_HDS_DD                   
				     , B.YD_WRK_DUTY             AS YD_WRK_DUTY                     
				     , B.YD_WRK_PARTY            AS YD_WRK_PARTY                    
				     , B.YD_MAIN_WRK_MTL_SH      AS YD_MAIN_WRK_MTL_SH              
				     , B.YD_AID_WRK_MTL_SH       AS YD_AID_WRK_MTL_SH               
				     , B.YD_AID_WRK_UPDN_GP      AS YD_AID_WRK_UPDN_GP              
				     , B.YD_TO_LOC_DCSN_MTD      AS YD_TO_LOC_DCSN_MTD              
				     , B.YD_TO_LOC_GUIDE         AS YD_TO_LOC_GUIDE                 
				     , B.YD_EQP_WRK_SH           AS YD_EQP_WRK_SH                   
				     , B.YD_EQP_WRK_WT           AS YD_EQP_WRK_WT                   
				     , B.YD_EQP_WRK_T            AS YD_EQP_WRK_T                    
				     , B.YD_EQP_WRK_MAX_W        AS YD_EQP_WRK_MAX_W                
				     , B.YD_EQP_WRK_MAX_L        AS YD_EQP_WRK_MAX_L                
				     , B.YD_CRN_SB_CTL_H         AS YD_CRN_SB_CTL_H                 
				     , B.YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID         
				     , B.YD_UP_WO_LOC            AS YD_UP_WO_LOC                    
				     , B.YD_UP_WO_LAYER          AS YD_UP_WO_LAYER                  
				     , B.YD_UP_WO_LOC_XAXIS      AS YD_UP_WO_LOC_XAXIS              
				     , B.YD_UP_WO_XAXIS_GAP_MAX  AS YD_UP_WO_XAXIS_GAP_MAX          
				     , B.YD_UP_WO_XAXIS_GAP_MIN  AS YD_UP_WO_XAXIS_GAP_MIN          
				     , B.YD_UP_WO_LOC_YAXIS      AS YD_UP_WO_LOC_YAXIS              
				     , B.YD_UP_WO_LOC_YAXIS1     AS YD_UP_WO_LOC_YAXIS1             
				     , B.YD_UP_WO_LOC_YAXIS2     AS YD_UP_WO_LOC_YAXIS2             
				     , B.YD_UP_WO_YAXIS_GAP_MAX  AS YD_UP_WO_YAXIS_GAP_MAX          
				     , B.YD_UP_WO_YAXIS_GAP_MIN  AS YD_UP_WO_YAXIS_GAP_MIN          
				     , B.YD_UP_WO_LOC_ZAXIS      AS YD_UP_WO_LOC_ZAXIS              
				     , B.YD_UP_WO_ZAXIS_GAP_MAX  AS YD_UP_WO_ZAXIS_GAP_MAX          
				     , B.YD_UP_WO_ZAXIS_GAP_MIN  AS YD_UP_WO_ZAXIS_GAP_MIN          
				     , B.YD_DN_WO_LOC            AS YD_DN_WO_LOC                    
				     , B.YD_DN_WO_LAYER          AS YD_DN_WO_LAYER                  
				     , B.YD_DN_WO_LOC_XAXIS      AS YD_DN_WO_LOC_XAXIS              
				     , B.YD_DN_WO_XAXIS_GAP_MAX  AS YD_DN_WO_XAXIS_GAP_MAX          
				     , B.YD_DN_WO_XAXIS_GAP_MIN  AS YD_DN_WO_XAXIS_GAP_MIN          
				     , B.YD_DN_WO_LOC_YAXIS      AS YD_DN_WO_LOC_YAXIS              
				     , B.YD_DN_WO_LOC_YAXIS1     AS YD_DN_WO_LOC_YAXIS1             
				     , B.YD_DN_WO_LOC_YAXIS2     AS YD_DN_WO_LOC_YAXIS2             
				     , B.YD_DN_WO_YAXIS_GAP_MAX  AS YD_DN_WO_YAXIS_GAP_MAX          
				     , B.YD_DN_WO_YAXIS_GAP_MIN  AS YD_DN_WO_YAXIS_GAP_MIN          
				     , B.YD_DN_WO_LOC_ZAXIS      AS YD_DN_WO_LOC_ZAXIS              
				     , B.YD_DN_WO_ZAXIS_GAP_MAX  AS YD_DN_WO_ZAXIS_GAP_MAX          
				     , B.YD_DN_WO_ZAXIS_GAP_MIN  AS YD_DN_WO_ZAXIS_GAP_MIN          
				     , B.YD_UP_WR_LOC            AS YD_UP_WR_LOC                    
				     , B.YD_UP_WR_LAYER          AS YD_UP_WR_LAYER                  
				     , B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP                
				     , B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS                  
				     , B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS                  
				     , B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1                 
				     , B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2                 
				     , B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS                  
				     , B.YD_DN_WR_LOC            AS YD_DN_WR_LOC                    
				     , B.YD_DN_WR_LAYER          AS YD_DN_WR_LAYER                  
				     , B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP                
				     , B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS                  
				     , B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS                  
				     , B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1                 
				     , B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2                 
				     , B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS   
				     , C.YD_AID_WRK_YN           AS YD_AID_WRK_YN   
				     , C.HCR_GP                  AS HCR_GP                 
				     , C.STL_PROG_CD             AS STL_PROG_CD    
				     , C.STOCK_ID   
				     , C.STACK_LAYER_GP
				     , (SELECT MAX(STEP_NO) FROM USRPOA.TB_PO_COILSHEARORD_SCRAP WHERE SCRAP_COIL_NO = C.STOCK_ID ) AS STEP_NO
				  FROM TB_YM_EQUIP  A                                               
				     , TB_YM_CRNSCH B                                               
				     , TB_YM_CRNWRKMTL C                                               
				 WHERE B.YD_EQP_ID      = A.EQUIP_GP  
				   AND B.YD_CRN_SCH_ID  = C.YD_CRN_SCH_ID  --COIL은 1:1
				   AND B.YD_WBOOK_ID    = :V_YD_WBOOK_ID
				   AND B.YD_EQP_ID      = :V_YD_EQP_ID              
				   AND B.DEL_YN = 'N'
				   AND C.DEL_YN = 'N'
				 ) 
				 SELECT A.*
				      , SUM(B.WRSLT_REAL_WT) OVER (ORDER BY A.STACK_LAYER_GP DESC) AS SUM_MTL_WT      
				      , SUM(B.WRSLT_T)       OVER (ORDER BY A.STACK_LAYER_GP DESC) AS SUM_MTL_T   
					  , MAX(B.WRSLT_W)       OVER (ORDER BY A.STACK_LAYER_GP DESC) AS MAX_MTL_W 
					  , MAX(B.WRSLT_LEN)     OVER (ORDER BY A.STACK_LAYER_GP DESC) AS MAX_MTL_L 
					  , COUNT(A.STOCK_ID)    OVER (ORDER BY A.STACK_LAYER_GP DESC) AS SH_CNT   
				   FROM TEMP_DATA A
				      , USRPOA.TB_PO_COILSHEARORD_SCRAP  B
				  WHERE A.STOCK_ID = B.SCRAP_COIL_NO 
				    AND A.STEP_NO  = B.STEP_NO 
				  ORDER BY A.YD_CRN_SCH_ID    
				*/  
				jsCrnsch = commDao.select(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCrnSchByWBookIdScrap", logId, methodNm, "크레인스케줄 조회");
			} else {
				
				
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCrnSchByWBookId 
				SELECT A.EQUIP_NO                AS YD_EQP_ID                       
				     , A.EQUIP_NAME              AS YD_EQP_NAME                     
				     , B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID                   
				     , B.REGISTER                AS REGISTER                        
				     , TO_CHAR(B.REG_DDTT       , 'YYYYMMDDHH24MISS') AS REG_DDTT                        
				     , B.MODIFIER                AS MODIFIER                        
				     , TO_CHAR(B.MOD_DDTT       , 'YYYYMMDDHH24MISS') AS MOD_DDTT                        
				     , B.DEL_YN                  AS DEL_YN                          
				     , B.YD_WBOOK_ID             AS YD_WBOOK_ID                     
				     , B.YD_GP                   AS YD_GP                           
				     , B.YD_BAY_GP               AS YD_BAY_GP                       
				     , B.YD_SCH_CD               AS YD_SCH_CD                       
				     , B.YD_SCH_ST_GP            AS YD_SCH_ST_GP                    
				     , B.YD_SCH_REQ_GP           AS YD_SCH_REQ_GP                   
				     , B.YD_SCH_PRIOR            AS YD_SCH_PRIOR                    
				     , B.YD_EQP_WRK_STAT         AS YD_EQP_WRK_STAT                 
				     , B.YD_WRK_PROG_STAT        AS YD_WRK_PROG_STAT                
				     , TO_CHAR(B.YD_WBOOK_DT    , 'YYYYMMDDHH24MISS') AS YD_WBOOK_DT
				     , TO_CHAR(B.YD_SCH_DT      , 'YYYYMMDDHH24MISS') AS YD_SCH_DT
				     , TO_CHAR(B.YD_WORD_DT     , 'YYYYMMDDHH24MISS') AS YD_WORD_DT
				     , TO_CHAR(B.YD_UP_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
				     , TO_CHAR(B.YD_DN_CMPL_DT  , 'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
				     , B.YD_WRK_HDS_DD           AS YD_WRK_HDS_DD                   
				     , B.YD_WRK_DUTY             AS YD_WRK_DUTY                     
				     , B.YD_WRK_PARTY            AS YD_WRK_PARTY                    
				     , B.YD_MAIN_WRK_MTL_SH      AS YD_MAIN_WRK_MTL_SH              
				     , B.YD_AID_WRK_MTL_SH       AS YD_AID_WRK_MTL_SH               
				     , B.YD_AID_WRK_UPDN_GP      AS YD_AID_WRK_UPDN_GP              
				     , B.YD_TO_LOC_DCSN_MTD      AS YD_TO_LOC_DCSN_MTD              
				     , B.YD_TO_LOC_GUIDE         AS YD_TO_LOC_GUIDE                 
				     , B.YD_EQP_WRK_SH           AS YD_EQP_WRK_SH                   
				     , B.YD_EQP_WRK_WT           AS YD_EQP_WRK_WT                   
				     , B.YD_EQP_WRK_T            AS YD_EQP_WRK_T                    
				     , B.YD_EQP_WRK_MAX_W        AS YD_EQP_WRK_MAX_W                
				     , B.YD_EQP_WRK_MAX_L        AS YD_EQP_WRK_MAX_L                
				     , B.YD_CRN_SB_CTL_H         AS YD_CRN_SB_CTL_H                 
				     , B.YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID         
				     , B.YD_UP_WO_LOC            AS YD_UP_WO_LOC                    
				     , B.YD_UP_WO_LAYER          AS YD_UP_WO_LAYER                  
				     , B.YD_UP_WO_LOC_XAXIS      AS YD_UP_WO_LOC_XAXIS              
				     , B.YD_UP_WO_XAXIS_GAP_MAX  AS YD_UP_WO_XAXIS_GAP_MAX          
				     , B.YD_UP_WO_XAXIS_GAP_MIN  AS YD_UP_WO_XAXIS_GAP_MIN          
				     , B.YD_UP_WO_LOC_YAXIS      AS YD_UP_WO_LOC_YAXIS              
				     , B.YD_UP_WO_LOC_YAXIS1     AS YD_UP_WO_LOC_YAXIS1             
				     , B.YD_UP_WO_LOC_YAXIS2     AS YD_UP_WO_LOC_YAXIS2             
				     , B.YD_UP_WO_YAXIS_GAP_MAX  AS YD_UP_WO_YAXIS_GAP_MAX          
				     , B.YD_UP_WO_YAXIS_GAP_MIN  AS YD_UP_WO_YAXIS_GAP_MIN          
				     , B.YD_UP_WO_LOC_ZAXIS      AS YD_UP_WO_LOC_ZAXIS              
				     , B.YD_UP_WO_ZAXIS_GAP_MAX  AS YD_UP_WO_ZAXIS_GAP_MAX          
				     , B.YD_UP_WO_ZAXIS_GAP_MIN  AS YD_UP_WO_ZAXIS_GAP_MIN          
				     , B.YD_DN_WO_LOC            AS YD_DN_WO_LOC                    
				     , B.YD_DN_WO_LAYER          AS YD_DN_WO_LAYER                  
				     , B.YD_DN_WO_LOC_XAXIS      AS YD_DN_WO_LOC_XAXIS              
				     , B.YD_DN_WO_XAXIS_GAP_MAX  AS YD_DN_WO_XAXIS_GAP_MAX          
				     , B.YD_DN_WO_XAXIS_GAP_MIN  AS YD_DN_WO_XAXIS_GAP_MIN          
				     , B.YD_DN_WO_LOC_YAXIS      AS YD_DN_WO_LOC_YAXIS              
				     , B.YD_DN_WO_LOC_YAXIS1     AS YD_DN_WO_LOC_YAXIS1             
				     , B.YD_DN_WO_LOC_YAXIS2     AS YD_DN_WO_LOC_YAXIS2             
				     , B.YD_DN_WO_YAXIS_GAP_MAX  AS YD_DN_WO_YAXIS_GAP_MAX          
				     , B.YD_DN_WO_YAXIS_GAP_MIN  AS YD_DN_WO_YAXIS_GAP_MIN          
				     , B.YD_DN_WO_LOC_ZAXIS      AS YD_DN_WO_LOC_ZAXIS              
				     , B.YD_DN_WO_ZAXIS_GAP_MAX  AS YD_DN_WO_ZAXIS_GAP_MAX          
				     , B.YD_DN_WO_ZAXIS_GAP_MIN  AS YD_DN_WO_ZAXIS_GAP_MIN          
				     , B.YD_UP_WR_LOC            AS YD_UP_WR_LOC                    
				     , B.YD_UP_WR_LAYER          AS YD_UP_WR_LAYER                  
				     , B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP                
				     , B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS                  
				     , B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS                  
				     , B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1                 
				     , B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2                 
				     , B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS                  
				     , B.YD_DN_WR_LOC            AS YD_DN_WR_LOC                    
				     , B.YD_DN_WR_LAYER          AS YD_DN_WR_LAYER                  
				     , B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP                
				     , B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS                  
				     , B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS                  
				     , B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1                 
				     , B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2                 
				     , B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS   
				     , C.YD_AID_WRK_YN           AS YD_AID_WRK_YN   
				     , C.HCR_GP                  AS HCR_GP                 
				     , C.STL_PROG_CD             AS STL_PROG_CD       
				     , D.HR_PLNT_GP 		    
				     , D.ORD_NO 			-- 제작번호,
				     , D.ORD_DTL 		    -- 제작행번,
				     , D.COIL_T 			-- 코일두께,
				     , D.COIL_W 			-- 코일폭,
				     , D.CURR_COIL_LEN	    -- 코일길이,
				     , D.COIL_INDIA 		-- 코일내경,
				     , D.COIL_OUTDIA 	    -- 코일외경,
				     , decode(D.COIL_WT, 0, D.NET_CAL_WT, D.COIL_WT) 		--AS 코일중량,
				     , D.NEXT_PROC 		    -- 차공정,
				     , D.PLAN_PROC1         -- 계획공정,
				     , D.BRANCH_CD 		    -- 분기위치코드,
				     , D.EXTEND_CONVEYOR_BRANCH_CD -- 확장분기위치코드,
				     , D.HYSCO_TRANS_GP 	-- HYSCO이송수단,
				     , D.COOL_METHOD 	    -- 냉각방법,
				     , decode(D.CURR_PROG_CD,'2','H','3','D','4','E','6','L','7','K',D.CURR_PROG_CD) AS CURR_PROG_CD
				     , D.RETURN_GP
				--     , (CASE WHEN D.YD_EQP_GP='QE'AND B.YD_BAY_GP IN('E','F') 
				--             THEN 'G' ELSE 'G' END) AS YD_BAY_GP
				     , (CASE WHEN EXISTS(SELECT 1 
				                           FROM (SELECT STACK_RULE_NAME
				    			                   FROM USRYMA.TB_YM_STACKRULE
				    			                  WHERE YD_GP = '3' 
				                                    AND STACK_COL_USAGE_CD='X' 
				                                    AND BAY_GP ='A'
				    			                    AND STACK_RULE_CD LIKE 'SPE%'
				    			                    AND STACK_RULE_USE_YN='Y') 
				                                T 
				                          WHERE T.STACK_RULE_NAME = D.HR_SPEC_ABBSYM) 
				             THEN 'Y' ELSE 'N' END ) AS JJANGGU_CHK  
				     , C.STOCK_ID       
				     , SUM(D.COIL_WT)   OVER (ORDER BY C.STACK_LAYER_GP DESC) AS SUM_MTL_WT      
					 , SUM(D.COIL_T)    OVER (ORDER BY C.STACK_LAYER_GP DESC) AS SUM_MTL_T   
					 , MAX(D.COIL_W)    OVER (ORDER BY C.STACK_LAYER_GP DESC) AS MAX_MTL_W 
					 , MAX(D.COIL_LEN)  OVER (ORDER BY C.STACK_LAYER_GP DESC) AS MAX_MTL_L 
					 , COUNT(D.COIL_NO) OVER (ORDER BY C.STACK_LAYER_GP DESC) AS SH_CNT 
				  FROM TB_YM_EQUIP  A                                               
				     , TB_YM_CRNSCH B                                               
				     , TB_YM_CRNWRKMTL C                                               
				     , USRPTA.TB_PT_COILCOMM  D  
				 WHERE B.YD_EQP_ID      = A.EQUIP_GP  
				   AND B.YD_CRN_SCH_ID  = C.YD_CRN_SCH_ID  --COIL은 1:1
				   AND C.STOCK_ID       = D.COIL_NO
				   AND B.YD_WBOOK_ID    = :V_YD_WBOOK_ID
				   AND B.YD_EQP_ID      = :V_YD_EQP_ID                         
				   AND B.DEL_YN = 'N'
				   AND C.DEL_YN = 'N'
				 ORDER BY B.YD_CRN_SCH_ID
				 */
				
				jsCrnsch = commDao.select(inRecord, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdCrnSchByWBookId", logId, methodNm, "크레인스케줄 조회"); 
			}
			
			
			//-------------------------------------------------------------------------------------------------------------
			//	크레인스케줄의 권하지시위치 결정
			//-------------------------------------------------------------------------------------------------------------
			//String StockId				= "";
   			String StackColGp 			= "";// 적치열
			String StackBedGp			= "";// 적치베드
			String StackLayerGp			= "";//차량정지위치 적치단
			String ydCrnSchId 			= "";
	    	
	    	String szToLocDcsnMtd 		= "";
	    	String szToLocGuide 		= "";
	    	String ydWrkPlanTcar        = "";
	    	int    iCoilWt              = 0;
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			JDTORecord jrCrnSch = JDTORecordFactory.getInstance().create();

			// A동 대차 하차 작업 인 경우 
			String sDIR_YN = "N";
			String sAPP007_YN = YmComm.BCoilApplyYn("APP007","3","1");   //log여부
			commUtils.printLog(logId,  "HFL 직보급 적용여부:" + sAPP007_YN, "SL");
			
		    for (int Loop_i = 1; Loop_i <= jsCrnsch.size(); Loop_i++) {

        		jsCrnsch.absolute(Loop_i);
        		jrCrnSch  = jsCrnsch.getRecord();
        		
        		//크레인스케줄Data저장
        		ydCrnSchId     = jrCrnSch.getFieldString("YD_CRN_SCH_ID");
        		szSchCd        = jrCrnSch.getFieldString("YD_SCH_CD");
        		szToLocDcsnMtd = jrCrnSch.getFieldString("YD_TO_LOC_DCSN_MTD");
        		StockId 	   = jrCrnSch.getFieldString("STOCK_ID");
        		szToLocGuide   = jrCrnSch.getFieldString("YD_TO_LOC_GUIDE");
        		
        		szMsg = "작업예약 " + szWbookId + " [" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]에 대한 권하지시위치 결정 "; //szWbookId
        		commUtils.printLog(logId, szMsg, "SL");
        		
				if (sAPP007_YN.equals("Y")) {
					/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getTcDirYn 
					SELECT NVL(MAX(CASE WHEN YD_SCH_CD = '3ATC01LM' 
					                     AND SUBSTR(STACK_COL_GP,3,2) = 'FE' 
					                     
					                     AND (SELECT DECODE(COUNT(*),0,'N','Y') 
					                            FROM TB_HR_C_SHEARWOWR SR,
					                                 TB_PT_COILCOMM CC
					                            WHERE SR.COIL_NO = CC.COIL_NO
					                              AND SR.COIL_NO = A.STOCK_ID
					                              AND SR.WORD_PROC IN ('5H','6H')
					                              AND SR.WORK_STAT IN ('2','B','E')
					                              AND CC.CURR_PROG_CD = 'C'
					                              AND SR.STEP_NO IN (SELECT MAX(STEP_NO)
					                                                   FROM TB_HR_C_SHEARWOWR
					                                                   WHERE COIL_NO = SR.COIL_NO)) = 'Y' 
					                    THEN 'Y'
					                    ELSE 'N' END ),'N')    AS DIR_YN
					  FROM
					     (  SELECT A1.YD_SCH_CD                AS YD_SCH_CD
					             , A1.STACK_COL_GP             AS STACK_COL_GP 
					             , A1.YD_LOC_SRCH_RNG_SEQ      AS YD_LOC_SRCH_RNG_SEQ
					             , B1.YD_SCH_PRFR_PRIOR        AS YD_SCH_PRFR_PRIOR
					             , CASE WHEN SUBSTR(A1.STACK_COL_GP,3,2) BETWEEN '00' AND '99' THEN SUBSTR(YD_SCH_PRFR_PRIOR,1,1)
					                    WHEN SUBSTR(A1.STACK_COL_GP,3,2) =                'PT' THEN SUBSTR(YD_SCH_PRFR_PRIOR,3,1)
					                    ELSE SUBSTR(B1.YD_SCH_PRFR_PRIOR,2,1)    END GROUP_SEQ   --검색조건 그룹순위
					             , :V_STOCK_ID AS STOCK_ID     
					          FROM TB_YM_SCHLOCSRCH      A1   
					              ,TB_YM_SCHLOCSRCHPRIOR B1   
					              , (SELECT WB.YD_SCH_CD
					                        -- 행선구분 추가
					                      , (SELECT NVL((SELECT ITEM
					                                       FROM USRYMA.TB_YM_RULE B
					                                      WHERE B.REPR_CD_GP = 'YD_RT'
					                                        AND SUBSTR(B.CD_GP,3,6) = SUBSTR(WB.YD_SCH_CD,3,6) 
					                                        AND NVL(ITEM,'GN')  = CASE WHEN SUBSTR(WB.YD_SCH_CD,3,2) IN ('DC','EC')                   
					                                                                        THEN A.NEXT_PROC
					                                                                   WHEN SUBSTR(WB.YD_SCH_CD,3,2) = 'TC' AND A.STL_APPEAR_GP = 'Y' 
					                                                                        THEN 'GD'                           --제품
					                                                                   WHEN SUBSTR(WB.YD_SCH_CD,3,2) = 'TC'                        
					                                                                        THEN A.NEXT_PROC                    --소재
					                                                                   ELSE 'GN' END 
					                                        AND ROWNUM = 1                           
					                                  ),'GN') 
					                           FROM USRPTA.TB_PT_COILCOMM A
					                          WHERE COIL_NO = :V_STOCK_ID)  
					                        AS YD_ROUTE_GP
					                   FROM (SELECT :V_YD_SCH_CD AS YD_SCH_CD FROM DUAL) WB
					                ) C1
					         WHERE A1.YD_SCH_CD               = B1.YD_SCH_CD
					           AND A1.YD_ROUTE_GP             = B1.YD_ROUTE_GP
					           AND A1.YD_SCH_CD               = C1.YD_SCH_CD
					           AND A1.YD_ROUTE_GP             = C1.YD_ROUTE_GP
					           AND A1.DEL_YN='N'
					         ORDER BY A1.YD_LOC_SRCH_RNG_SEQ
					      ) A
					WHERE ROWNUM = 1
					
					 */    		
					JDTORecord jrTcInPara 	= JDTORecordFactory.getInstance().create();
    				jrTcInPara.setField("STOCK_ID"	, StockId);
    				jrTcInPara.setField("YD_SCH_CD"	, szSchCd);
    				
    				JDTORecordSet jsTcDirYn = commDao.select(jrTcInPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getTcDirYn", logId, methodNm, "직보급 가능여부 조회");   			
				    if (jsTcDirYn.size() > 0 ) {
			    		JDTORecord jrTcDirYn = jsTcDirYn.getRecord(0);
			    		sDIR_YN = commUtils.trim(jrTcDirYn.getFieldString("DIR_YN"));//대차 직보급 여부
				    } else {
				    	sDIR_YN = "N";
				    }
    				commUtils.printLog(logId, "대차직보급 여부 :" + sDIR_YN , "SL");
    				// 직보급이 가능 하면 TO위치 가이드를 HFL 보급존으로 변경처리 함
    				if (sDIR_YN.equals("Y")) {
    					szToLocGuide = "3AFE010001";
    				}	
				} // end if (sAPP007_YN.equals("Y"))	
        		
       			if (StockId.substring(0,1).equals("S")) {       				
            		/**********************************************************
    				* SPM2 Scrap 인 경우
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은  Scrap To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
       				this.procScrapToLoc(logId, methodNm, jrWbook, jrCrnSch);

            	} else if (szToLocDcsnMtd.equals("W")) {
            		/**********************************************************
    				* 보조작업인 경우 TO위치 결정 (일반적치대로...)
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 보조작업 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			
        			this.procToLocDummy(logId, methodNm, jrWbook, jrCrnSch);

            	} else if (  szToLocDcsnMtd.equals("S")            //설비위 주작업
              			 &&(szSchCd.substring(2,4).equals("FE")   //HFL 보급,TAKE IN
              			     || szSchCd.substring(2,4).equals("FD")   //HFL추출  
   	           			     || szSchCd.substring(2,4).equals("KE")   //SPM 보급,TAKE IN
   	           			     || szSchCd.substring(2,4).equals("KD")   //SPM 추출,TAKE IN
   	           			   )  //SPM보급
   	           			 && szSchCd.substring(6,7).equals("U") ) {    
               		
               		if (szSchCd.substring(4,6).equals("05")) {
                   		// 재처리 작업 임(3EKE05UM)	  --> 재처리 요청시 to위치 가이드 있음    3EKE05UM:SPM2 재작업
                   		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 재처리사용자지정 스케줄의  To위치 결정 시작";
               			commUtils.printLog(logId, szMsg, "SL");
               			
               			this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);           			
               			
               		} else if ("3EKE07UM".equals(szSchCd) && !"".equals(szToLocGuide)) {  //3EKE07UM : SPM2 포장모드 보급
   		           		/**********************************************************
   		   				* 포장모드 SPM2 보급  일 때는 To위치 가이드 값이 존재한다.
   		   				**********************************************************/
               			
                   		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 E동 SPM2 포장모드 보급 스케줄의  To위치 결정 시작";
               			commUtils.printLog(logId, szMsg, "SL");
               			
               			this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
               			
               		} else {
               		
   		           		/**********************************************************
   		   				* HFL/SPM보급  
   		   				**********************************************************/            		
   		           		szMsg = methodNm +"["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 설비위 스케줄의  To위치 결정 시작";
   		       			commUtils.printLog(logId, szMsg, "SL");
   		       			
   		       			this.procToLocConveyor(logId, methodNm, jrWbook, jrCrnSch);
               		}
            	} else if (szToLocDcsnMtd.equals("S")        //설비위 주작업
              			 && "3EKE02MM".equals(szSchCd) ) {   //3EKE02MM : SPM2 커팅후보급 
               		
   		           		/**********************************************************
   		   				* SPM2보급 : 커팅장에서 보급존  
   		   				**********************************************************/            		
   		           		szMsg = methodNm +"["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 SPM2 밴딩스케줄의  To위치 결정 시작";
   		       			commUtils.printLog(logId, szMsg, "SL");
   		       			
   		       			this.procToLocConveyor(logId, methodNm, jrWbook, jrCrnSch);
   		       			
            	} else if ( szSchCd.substring(2,4).equals("PT") && (szSchCd.substring(6,7).equals("U"))) {  // 출하
            		/**********************************************************************
    				* 차량출고 : 차량이 정지한 적치열 조회 ==> TO위치가 됨  ==> TO 위치 가이드에 등록 처리
    				***********************************************************************/            		
        			String szRtnMsg = "";
        			
        			String ydCarUseGp   = commUtils.trim(jrWbook.getFieldString("YD_CAR_USE_GP"));	//차량사용구분
    				String TrnEqpCd		= commUtils.trim(jrWbook.getFieldString("TRN_EQP_CD"));	//운송장비코드
    				String ydCarNo		= commUtils.trim(jrWbook.getFieldString("CAR_NO"));		//차량번호
    				String ydCardNo		= commUtils.trim(jrWbook.getFieldString("CARD_NO"));	//차량번호
    				
    				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 차량상차작업예약["+szWbookId+"]의 차량정보[차량사용구분:"+ydCarUseGp+", 운송장비코드:"+TrnEqpCd+", 차량번호:"+ydCarNo+"]에 대한 적치베드 조회 시작";
    				commUtils.printLog(logId, szMsg, "SL");
				
    				JDTORecordSet jsCar	= JDTORecordFactory.getInstance().createRecordSet("");
    				
    				
    				if ( ydCarUseGp.equals("L") ) {				//구내운송
    					commUtils.printLog(logId,  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" 의 적치가능한 베드 조회 시작", "SL");
 					
    					jrParam.setField("YD_CAR_USE_GP", 	ydCarUseGp);
    					jrParam.setField("TRN_EQP_CD", 		TrnEqpCd);
    					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedByCarUseGpandTrnEqpCd 
    					SELECT STACK_COL_GP
    					     , STACK_BED_GP 
    					     , STACK_LAYER_GP 
    					     , YD_MTL_SH 
    					  FROM
    					(
    					SELECT SB.STACK_COL_GP
    					     , SB.STACK_BED_GP 
    					     , SL.STACK_LAYER_GP 
    					      ,COUNT(SL.STOCK_ID) AS YD_MTL_SH
    					  FROM TB_YM_STACKER SB
    					      ,TB_YM_STACKLAYER SL
    					      ,(SELECT STACK_COL_GP                         
    					          FROM TB_YM_STACKCOL                          
    					         WHERE YD_CAR_USE_GP =:V_YD_CAR_USE_GP
    					           AND TRN_EQP_CD = :V_TRN_EQP_CD
    					           AND STACK_COL_ACTIVE_STAT = 'L'             
    					           AND DEL_YN = 'N' ) SC
    					 WHERE SB.STACK_COL_GP = SL.STACK_COL_GP
    					   AND SB.STACK_BED_GP = SL.STACK_BED_GP
    					   AND SB.STACK_COL_GP = SC.STACK_COL_GP
    					   AND SB.DEL_YN        = 'N'
    					   AND SB.STACK_BED_ACTIVE_STAT = 'L' --적치가능
    					   AND SL.STACK_LAYER_GP='01' --차량위치는 1단만 적치
    					   AND SB.STACK_COL_GP||SB.STACK_BED_GP||SL.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
    					   AND SL.STOCK_ID IS NULL
    					 GROUP BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
    					 ORDER BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
    					 )
    					WHERE YD_MTL_SH = 0
    					   AND ROWNUM = 1
	    				*/   
    					jsCar = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedByCarUseGpandTrnEqpCd", logId, methodNm, "구내운송차량 차량BED 조회"); 
	            		
	            		if (jsCar.size() <= 0) {
	            			szMsg = methodNm + " : 구내운송차량 READ 실패!";
	        				commUtils.printLog(logId, szMsg, "SL");
	            		} else {
	            			szRtnMsg = YmConstant.RETN_CD_SUCCESS;
	            		}	
    				} else if ( ydCarUseGp.equals("G") ) {		//출하차량
    					
    					if ( szSchCd.substring(5,6).equals("2") || (szSchCd.substring(5,6).equals("6"))) {
    						// 이송상차
	    					commUtils.printLog(logId,  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" 의 적치가능한 베드 조회 시작", "SL");
	    					
	    					jrParam.setField("YD_CAR_USE_GP"	, ydCarUseGp);
	    					jrParam.setField("CAR_NO"			, ydCarNo);
	    					jrParam.setField("STOCK_ID"			, StockId);
	    					jrParam.setField("CARD_NO"			, ydCardNo);
	    					jrParam.setField("YD_SCH_CD"	    , szSchCd);
	    					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedByCarUseGpandCarNoFrto_PIDEV
							SELECT STACK_COL_GP
							     , STACK_BED_GP 
							     , STACK_LAYER_GP 
							     , YD_MTL_SH 
							  FROM
							       (
							        SELECT SB.STACK_COL_GP
							             , SB.STACK_BED_GP 
							             , SL.STACK_LAYER_GP 
							             ,COUNT(SL.STOCK_ID) AS YD_MTL_SH
							          FROM TB_YM_STACKER SB
							              ,TB_YM_STACKLAYER SL
							              ,(SELECT YD_STK_COL_GP AS STACK_COL_GP                         
							                  FROM TB_YD_CARPOINT                          
							                 WHERE CAR_NO = :V_CAR_NO ) SC
							                   
							         WHERE SB.STACK_COL_GP = SL.STACK_COL_GP
							           AND SB.STACK_COL_GP = SC.STACK_COL_GP
							           AND SB.STACK_BED_GP = SL.STACK_BED_GP
							           AND SB.STACK_BED_GP LIKE NVL((SELECT ITEM
							                                           FROM USRYMA.TB_YM_RULE 
							                                          WHERE REPR_CD_GP = 'YM1001'
							                                            AND CD_GP =  (SELECT SHEAR_SUPPLY_SEQ FROM TB_YM_STOCK WHERE STOCK_ID = :V_STOCK_ID)
							                                    ),'%')    
							           AND SB.DEL_YN       = 'N'
							           AND SB.STACK_BED_ACTIVE_STAT = 'L' --적치가능
							           AND SL.STACK_LAYER_GP='01' --차량위치는 1단만 적치
							           AND SB.STACK_COL_GP||SB.STACK_BED_GP||SL.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
							           AND SL.STOCK_ID IS NULL
							         GROUP BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
							         ORDER BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
							        )
							  WHERE YD_MTL_SH = 0
							    AND ROWNUM = 1
		    				*/
	    					jsCar = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedByCarUseGpandCarNoFrto_PIDEV", logId, methodNm, "냉연이송상차 차량BED 조회"); 
		            		
		            		if (jsCar.size() <= 0) {
		        				szMsg = methodNm + " : 냉연이송상차 READ 실패!";
		        				commUtils.printLog(logId, szMsg, "SL");
		            		} else {
		            			szRtnMsg = YmConstant.RETN_CD_SUCCESS;
		            		}    						
    						
    					} else {	
	    					
	    					commUtils.printLog(logId,  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" 의 적치가능한 베드 조회 시작", "SL");
	    					
	    					jrParam.setField("YD_CAR_USE_GP", ydCarUseGp);
	    					jrParam.setField("CAR_NO"		, ydCarNo);
	    					jrParam.setField("CARD_NO"		, ydCardNo);
	    					jrParam.setField("YD_SCH_CD"	, szSchCd);
	    					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedByCarUseGpandCarNo_PIDEV 
							SELECT STACK_COL_GP
							     , STACK_BED_GP 
							     , STACK_LAYER_GP 
							     , YD_MTL_SH 
							  FROM
							(
							SELECT SB.STACK_COL_GP
							     , SB.STACK_BED_GP 
							     , SL.STACK_LAYER_GP 
							     , COUNT(SL.STOCK_ID) AS YD_MTL_SH
							  FROM TB_YM_STACKER    SB
							     , TB_YM_STACKLAYER SL
							     , (SELECT YD_STK_COL_GP AS STACK_COL_GP                      
							          FROM TB_YD_CARPOINT                          
							         WHERE CARD_NO = :V_CARD_NO
							           AND DEL_YN = 'N' ) SC
							 WHERE SB.STACK_COL_GP = SL.STACK_COL_GP
							   AND SB.STACK_BED_GP = SL.STACK_BED_GP
							   AND SB.STACK_COL_GP = SC.STACK_COL_GP
							   AND SB.DEL_YN       = 'N'
							   AND SB.STACK_BED_ACTIVE_STAT = 'L' --적치가능
							   AND SL.STACK_LAYER_GP        = '01' --차량위치는 1단만 적치
							   AND SB.STACK_COL_GP||SB.STACK_BED_GP||SL.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
							   --복수동 적용
							   AND SB.STACK_BED_GP||SL.STACK_LAYER_GP NOT IN ( SELECT NVL(B.YD_STK_BED_NO||CASE WHEN LENGTH(B.YD_STK_LYR_NO) = '3' THEN  SUBSTR(B.YD_STK_LYR_NO,2,2)
							                                                                                    ELSE B.YD_STK_LYR_NO  END,'1') 
							                                                     FROM TB_YD_CARSCH A
							                                                        , TB_YD_CARFTMVMTL B 
							                                                    WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
							                                                      AND A.CAR_NO = :V_CAR_NO
							                                                      AND A.CARD_NO= :V_CARD_NO
							                                                      AND A.DEL_YN = 'N'
							                                                      AND B.DEL_YN = 'N'
							                                                      AND A.CAR_NO NOT IN ('9999','9998','9997','9996','9995')
							                                                 )
							   AND SL.STOCK_ID IS NULL
							   AND SUBSTR(SB.STACK_COL_GP, 1, 2) = SUBSTR(:V_YD_SCH_CD, 1, 2)
							 GROUP BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
							 ORDER BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
							)
							 WHERE YD_MTL_SH = 0
							   AND ROWNUM = 1
		    				   */
		    				                                
	    					jsCar = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedByCarUseGpandCarNo_PIDEV", logId, methodNm, "출하차량TOSQL BED 조회"); 
		            		
		            		if (jsCar.size() <= 0) {
		        				szMsg = methodNm + " : 출하차량 READ 실패!";
		        				commUtils.printLog(logId, szMsg, "SL");
		            		} else {
		            			szRtnMsg = YmConstant.RETN_CD_SUCCESS;
		            		}
    					}	
    				}
    				
    				
    				if ( szRtnMsg.equals(YmConstant.RETN_CD_SUCCESS) ) {
    					jsCar.first();
    					JDTORecord jrCar = jsCar.getRecord();
    					
            			StackColGp 	= commUtils.trim(jrCar.getFieldString("STACK_COL_GP"));//차량정지위치 적치열
            			StackBedGp	= commUtils.trim(jrCar.getFieldString("STACK_BED_GP"));//차량정지위치 적치베드
            			StackLayerGp= commUtils.trim(jrCar.getFieldString("STACK_LAYER_GP"));//차량정지위치 적치단
    					
    					jrWbook.setField("YD_TO_LOC_GUIDE", StackColGp + StackBedGp + StackLayerGp);

    					szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 차량출고 [TO위치 가이드 결정  YD_TO_LOC_GUIDE : "+StackColGp + StackBedGp + StackLayerGp+"]";
        				commUtils.printLog(logId, szMsg, "SL");
        				
        				this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
    				}	
    				
      			} else if ("Y".equals(sAPP048) && "3DTC02LM".equals(szSchCd) && !"".equals(sYD_TO_LOC_GUIDE_FNL)) { //A-E 동간이적
      				
    				/*  
    				WITH TEMP AS (
    				SELECT :V_STACK_COL_GP AS V_STACK_COL_GP FROM DUAL
    				)
    				SELECT STACK_COL_GP
    				     , STACK_BED_GP 
    				     , STACK_LAYER_GP  
    				     , YD_MTL_SH 
    				  FROM
    				(
    				SELECT SB.STACK_COL_GP
    				     , SB.STACK_BED_GP 
    				     , SL.STACK_LAYER_GP 
    				     , COUNT(SL.STOCK_ID) AS YD_MTL_SH
    				  FROM TB_YM_STACKER SB
    				     , TB_YM_STACKLAYER SL
    				     , TEMP TM
    				 WHERE SB.STACK_COL_GP = TM.V_STACK_COL_GP
    				   AND SB.STACK_COL_GP = SL.STACK_COL_GP
    				   AND SB.STACK_BED_GP = SL.STACK_BED_GP
    				   AND SB.DEL_YN        = 'N'
    				   AND SB.STACK_LAYER_ACTIVE_STAT = 'E' --적치가능
    				 GROUP BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
    				 ORDER BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
    				 )
    				 WHERE YD_MTL_SH = 0
    				   AND ROWNUM = 1
    				   */
      				jrParam.setField("STACK_COL_GP", sYD_TO_LOC_GUIDE);
    				JDTORecordSet jsTcarBed = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedBybedTc", logId, methodNm, "대차TOSQL 조회"); 
            		
            		if (jsTcarBed.size() <= 0) {
        				commUtils.printLog(logId, methodNm + " : 대차 BED READ 실패!", "SL");
            		} else {
            			JDTORecord jrTcarBed  = jsTcarBed.getRecord(0);
    						
            			StackColGp	= commUtils.trim(jrTcarBed.getFieldString("STACK_COL_GP"));//대차정지위치 적치열
            			StackBedGp	= commUtils.trim(jrTcarBed.getFieldString("STACK_BED_GP"));//대차정지위치 적치베드
            			StackLayerGp= commUtils.trim(jrTcarBed.getFieldString("STACK_LAYER_GP"));//대차정지위치 적치단
					
            			jrWbook.setField("YD_TO_LOC_GUIDE", StackColGp + StackBedGp + StackLayerGp);
            			this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
            		}
            		
		    	} else if ( szSchCd.substring(2,4).equals("TC") && szSchCd.substring(6,7).equals("U") ) {  
            		/*****************************************************************************
    				* 대차상차 작업
    				* 타 동으로 가야 할 경우 
    				*  - 대차 위치 확인하여 대차가 현재동에 있으면 상차 
    				*  - 현재동에 없으면 대차 상태를 확인 하여  출발지시 처리 함  
    				******************************************************************************/            		
        			
        			szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 대차상차 주작업 To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");

        			String ydAimBayGp	= commUtils.trim(jrWbook.getFieldString("YD_AIM_BAY_GP"));		//야드 목적동
        			ydWrkPlanTcar 		= commUtils.trim(jrWbook.getFieldString("YD_WRK_PLAN_TCAR"));	//예약등록시 지정계획대차
    				
        			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcBayWrk 
        			-- 대차 목적동에 대차작업 여부 
        			SELECT A.EQUIP_GP
        			     , SUBSTR(A.CURR_STOP_LOC,2,1) AS YD_CURR_BAY_GP
        			     , A.WAIT_STOP_LOC
        			     , A.STACK_STAT
        			     , (SELECT COUNT(*) 
        			          FROM TB_YM_STACKLAYER 
        			         WHERE SUBSTR(STACK_COL_GP,1,4) = SUBSTR(A.EQUIP_GP,1,1)||B.YD_AIM_BAY_GP ||'TC' 
        			           AND DEL_YN = 'N'
        			           AND STOCK_ID IS NOT NULL
        			       ) --상차된 경우
        			       + 
        			       (SELECT COUNT(*)
        			          FROM TB_YM_TCARSCH 
        			         WHERE DEL_YN = 'N'
        			           AND YD_EQP_ID = A.EQUIP_GP
        			           AND YD_CAR_PROG_STAT IN('0','2')
        			       ) -- 대차스케쥴 상태대기
        			       + 
        			       (SELECT COUNT(*)
        			          FROM TB_YM_CRNSCH 
        			         WHERE DEL_YN = 'N'
        			           AND YD_GP  = SUBSTR(A.EQUIP_GP,1,1)
        			           AND SUBSTR(YD_DN_WO_LOC,1,4) = SUBSTR(A.EQUIP_GP,1,1)||B.YD_AIM_BAY_GP ||'TC'
        			       ) -- 크레인작업지시 편성여부
        			       
        			       AS WK_CNT
        			  FROM TB_YM_EQUIP A
        			     , (SELECT :V_YD_AIM_BAY_GP AS YD_AIM_BAY_GP
        			          FROM DUAL) B
        			 WHERE A.EQUIP_GP = :V_EQUIP_GP
        			   AND A.WPROG_STAT <> 'B'
					*/ 
        			jrParam.setField("EQUIP_GP"		, ydWrkPlanTcar);
        			jrParam.setField("YD_AIM_BAY_GP", ydAimBayGp);
    
					//대차정보 조회
        			JDTORecordSet jsTcarInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getTcBayWrk", logId, methodNm, "대차정보조회");
					
					if (jsTcarInfo.size() > 0) {
						
						JDTORecord jrTCarInfo = jsTcarInfo.getRecord(0);
						String ydTcarCurrBayGp  = commUtils.trim(jrTCarInfo.getFieldString("YD_CURR_BAY_GP" )); // 현재동
						String tcarUdCmplYn 	= commUtils.trim(jrTCarInfo.getFieldString("WK_CNT"));  // 목표동 대차 작업 건수
//						String tcarYdEqpId 		= commUtils.trim(jrTCarInfo.getFieldString("YD_EQP_ID"));
//						String ydEqpStat 		= commUtils.trim(jrTCarInfo.getFieldString("YD_EQP_STAT"));
						                                 //작업위치 
						jrParam.setField("STACK_COL_GP", ydWrkPlanTcar.substring(0,1) + szSchCd.substring(1,2) + ydWrkPlanTcar.substring(2,6));
						
						if (szSchCd.substring(1,2).equals(ydTcarCurrBayGp)) {
							//대차 위치가  같은동이면
							commUtils.printLog(logId,  " : 대차 위치가  작업동과 같은동에 있는 경우", "SL");
	        				commUtils.printLog(logId,  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" 의 적치가능한 베드 조회 시작", "SL");	        				
	        				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedBybedTc 
	        				WITH TEMP AS (
	        				SELECT :V_STACK_COL_GP AS V_STACK_COL_GP FROM DUAL
	        				)
	        				SELECT STACK_COL_GP
	        				     , STACK_BED_GP 
	        				     , STACK_LAYER_GP  
	        				     , YD_MTL_SH 
	        				  FROM
	        				(
	        				SELECT SB.STACK_COL_GP
	        				     , SB.STACK_BED_GP 
	        				     , SL.STACK_LAYER_GP 
	        				     , COUNT(SL.STOCK_ID) AS YD_MTL_SH
	        				  FROM TB_YM_STACKER SB
	        				     , TB_YM_STACKLAYER SL
	        				     , TEMP TM
	        				 WHERE SB.STACK_COL_GP = TM.V_STACK_COL_GP
	        				   AND SB.STACK_COL_GP = SL.STACK_COL_GP
	        				   AND SB.STACK_BED_GP = SL.STACK_BED_GP
	        				   AND SB.DEL_YN        = 'N'
	        				--   AND SB.STACK_BED_ACTIVE_STAT = 'L' --적치가능
	        				   AND SB.STACK_LAYER_ACTIVE_STAT = 'E' --적치가능
	        				 GROUP BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
	        				 ORDER BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
	        				 )
	        				 WHERE YD_MTL_SH = 0
	        				   AND ROWNUM = 1
		    				   */
	        				JDTORecordSet jsTcarBed = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedBybedTc", logId, methodNm, "대차TOSQL 조회"); 
		            		
		            		if (jsTcarBed.size() <= 0) {
		        				commUtils.printLog(logId, methodNm + " : 대차 BED READ 실패!", "SL");
		            		} else {
		            			JDTORecord jrTcarBed  = jsTcarBed.getRecord(0);
		    						
		            			StackColGp	= commUtils.trim(jrTcarBed.getFieldString("STACK_COL_GP"));//대차정지위치 적치열
		            			StackBedGp	= commUtils.trim(jrTcarBed.getFieldString("STACK_BED_GP"));//대차정지위치 적치베드
		            			StackLayerGp= commUtils.trim(jrTcarBed.getFieldString("STACK_LAYER_GP"));//대차정지위치 적치단
	    					
		            			jrWbook.setField("YD_TO_LOC_GUIDE", StackColGp + StackBedGp + StackLayerGp);
		            			this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
		            		}	    							
							
						} else {	
							
	        				commUtils.printLog(logId,  " : 대차위치가  작업동과 틀린 경우 + tcarUdCmplYn :"+ tcarUdCmplYn, "SL");
	        				
							if (tcarUdCmplYn.equals("0")) {
								
								//대차 현재위치에 작업이 없음
								//상차 작업 진행 가능함
								commUtils.printLog(logId,  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" 의 적치가능한 베드 조회 시작", "SL");							
		        				
								/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedBybedTcXbay
								SELECT STACK_COL_GP
								     , STACK_BED_GP 
								     , STACK_LAYER_GP  
								     , YD_MTL_SH 
								  FROM
								(
								SELECT SB.STACK_COL_GP
								     , SB.STACK_BED_GP 
								     , SL.STACK_LAYER_GP 
								      ,COUNT(SL.STOCK_ID) AS YD_MTL_SH
								  FROM TB_YM_STACKER SB
								      ,TB_YM_STACKLAYER SL
								 WHERE SB.STACK_COL_GP = :V_STACK_COL_GP
								   AND SB.STACK_COL_GP = SL.STACK_COL_GP
								   AND SB.STACK_BED_GP = SL.STACK_BED_GP
								   AND SB.DEL_YN        = 'N'
								   AND SUBSTR(SB.STACK_COL_GP,3,6) IN ('TC03','TC04','TC05')
								 GROUP BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
								 ORDER BY SB.STACK_COL_GP, SB.STACK_BED_GP, SL.STACK_LAYER_GP 
								 )
								 WHERE YD_MTL_SH = 0
								   AND ROWNUM = 1
		        				*/   
		        				
								JDTORecordSet jsTcarBed = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkBedBybedTcXbay", logId, methodNm, "대차TOSQLBED(다른동) 조회"); 
								if (jsTcarBed.size() <= 0) {
			        				commUtils.printLog(logId,  " : 대차 BED READ 실패!", "SL");
			            		} else {
			            			JDTORecord jrTcarBed  = jsTcarBed.getRecord(0);
			    						
			            			StackColGp	= commUtils.trim(jrTcarBed.getFieldString("STACK_COL_GP"));//대차정지위치 적치열
			            			StackBedGp	= commUtils.trim(jrTcarBed.getFieldString("STACK_BED_GP"));//대차정지위치 적치베드
			            			StackLayerGp= commUtils.trim(jrTcarBed.getFieldString("STACK_LAYER_GP"));//대차정지위치 적치단
		    					
			            			jrWbook.setField("YD_TO_LOC_GUIDE", StackColGp + StackBedGp + StackLayerGp);
			            			this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
			            			//대차 이동지시 송신
	    		    				TcarTcSndyn = "Y";
			            		}	
							}
						}
					}	
      			} else if (szToLocGuide.length() >= 4) {
            		/**********************************************************
    				* 사용자 지정 :
    				**********************************************************/            		
            		szMsg = "["+ Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자지정 스케줄의  To위치 결정 시작";
        			commUtils.printLog(logId, szMsg, "SL");
        			// 대차 직보급
        			if (sDIR_YN.equals("Y")) {
    					jrWbook.setField("YD_TO_LOC_GUIDE", szToLocGuide);
    				}
        			
        			String sAPP026_YN = YmComm.BCoilApplyYn("APP026","3","1");   //log여부
        			commUtils.printLog(logId,  "사용자지정 실패시 일반야드 검색:" + sAPP026_YN, "SL");
        			
        			if ("Y".equals(sAPP026_YN)) {
            			String UserRtn = this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
            			
            			// 동내이적이고 TO위치 가이드가 8자리 미만인 경우만
            			if ((YmConstant.RETN_CD_FAILURE.equals(UserRtn)) && (szSchCd.substring(2,4).equals("YD")) && (szToLocGuide.length() < 8) ) {
            				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치결정 시작";
            				commUtils.printLog(logId, szMsg, "SL");
            				this.procToLocPrimaryWork(logId, methodNm, jrWbook, jrCrnSch);
            				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자 지정에서 주작업 스케줄의 To위치 결정 완료 ";
                			commUtils.printLog(logId, szMsg, "SL");
            			} 
            			// 동간이적시(대차하차)에도 To위치 가이드 위치검색 실패시 저장영역별 검색순서로 to위치 결정 20180214 정문식 주임 요청
            			else if ((YmConstant.RETN_CD_FAILURE.equals(UserRtn)) && (szSchCd.substring(2,4).equals("TC")) && (szToLocGuide.length() < 8) && (szSchCd.substring(6,7).equals("L"))) {
            				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-사용자 지정에서 야드로 TO위치결정 시작";
            				commUtils.printLog(logId, szMsg, "SL");
            				this.procToLocPrimaryWork(logId, methodNm, jrWbook, jrCrnSch);
            				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 사용자 지정에서 주작업 스케줄의 To위치 결정 완료 ";
                			commUtils.printLog(logId, szMsg, "SL");
            			}
        				
        			} else {
        				this.procToLocUser(logId, methodNm, jrWbook, jrCrnSch);
        			}
        			
    			} else {
    				/*****************************************************************************
    				* 일반작업
    				******************************************************************************/      

    				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "] : 주작업 TO위치결정-야드로 TO위치결정 시작";
    				commUtils.printLog(logId, szMsg, "SL");

   					this.procToLocPrimaryWork(logId, methodNm, jrWbook, jrCrnSch);
    				        				
    				szMsg =  "[" + Loop_i+"]번째 크레인 스케줄[" + ydCrnSchId + "]은 주작업 스케줄의 To위치 결정 완료 ";
        			commUtils.printLog(logId, szMsg, "SL");
    			}
        	}
        	
        	//-------------------------------------------------------------------------------------------------------------
    		// To위치 결정 실패시 default값으로 xx010101을 설정
        	//-------------------------------------------------------------------------------------------------------------
        	jsCrnsch 	= JDTORecordFactory.getInstance().createRecordSet("");
    		jrInPara 	= JDTORecordFactory.getInstance().create();
    		jrInPara.setField("YD_WBOOK_ID", szWbookId);
    		jrInPara.setField("YD_EQP_ID",   szEqpId);
    		/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnschByEqpIdandWBookId
    		SELECT A.EQUIP_NO                AS YD_EQP_ID                       
    		      ,A.EQUIP_NAME              AS YD_EQP_NAME                     
    		      ,B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID                   
    		      ,B.REGISTER                AS REGISTER                        
    		      ,TO_CHAR(B.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT                        
    		      ,B.MODIFIER                AS MODIFIER                        
    		      ,TO_CHAR(B.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT                        
    		      ,B.DEL_YN                  AS DEL_YN                          
    		      ,B.YD_WBOOK_ID             AS YD_WBOOK_ID                     
    		      ,B.YD_EQP_ID               AS YD_EQP_ID                       
    		      ,B.YD_GP                   AS YD_GP                           
    		      ,B.YD_BAY_GP               AS YD_BAY_GP                       
    		      ,B.YD_SCH_CD               AS YD_SCH_CD                       
    		      ,B.YD_SCH_ST_GP            AS YD_SCH_ST_GP                    
    		      ,B.YD_SCH_REQ_GP           AS YD_SCH_REQ_GP                   
    		      ,B.YD_SCH_PRIOR            AS YD_SCH_PRIOR                    
    		      ,B.YD_EQP_WRK_STAT         AS YD_EQP_WRK_STAT                 
    		      ,B.YD_WRK_PROG_STAT        AS YD_WRK_PROG_STAT                
    		      ,TO_CHAR(B.YD_WBOOK_DT, 'YYYYMMDDHH24MISS') AS YD_WBOOK_DT
    		      ,TO_CHAR(B.YD_SCH_DT, 'YYYYMMDDHH24MISS') AS YD_SCH_DT
    		      ,TO_CHAR(B.YD_WORD_DT, 'YYYYMMDDHH24MISS') AS YD_WORD_DT
    		      ,TO_CHAR(B.YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
    		      ,TO_CHAR(B.YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
    		      ,B.YD_WRK_HDS_DD           AS YD_WRK_HDS_DD                   
    		      ,B.YD_WRK_DUTY             AS YD_WRK_DUTY                     
    		      ,B.YD_WRK_PARTY            AS YD_WRK_PARTY                    
    		      ,B.YD_MAIN_WRK_MTL_SH      AS YD_MAIN_WRK_MTL_SH              
    		      ,B.YD_AID_WRK_MTL_SH       AS YD_AID_WRK_MTL_SH               
    		      ,B.YD_AID_WRK_UPDN_GP      AS YD_AID_WRK_UPDN_GP              
    		      ,B.YD_TO_LOC_DCSN_MTD      AS YD_TO_LOC_DCSN_MTD              
    		      ,(CASE WHEN B.YD_TO_LOC_GUIDE LIKE 'C_TY%' THEN (SELECT YD_TO_LOC_GUIDE FROM USRYMA.TB_YM_WRKBOOK C WHERE C.YD_WBOOK_ID=B.YD_WBOOK_ID) ELSE  B.YD_TO_LOC_GUIDE END)       AS YD_TO_LOC_GUIDE                 
    		      ,B.YD_EQP_WRK_SH           AS YD_EQP_WRK_SH                   
    		      ,B.YD_EQP_WRK_WT           AS YD_EQP_WRK_WT                   
    		      ,B.YD_EQP_WRK_T            AS YD_EQP_WRK_T                    
    		      ,B.YD_EQP_WRK_MAX_W        AS YD_EQP_WRK_MAX_W                
    		      ,B.YD_EQP_WRK_MAX_L        AS YD_EQP_WRK_MAX_L                
    		      ,B.YD_CRN_SB_CTL_H         AS YD_CRN_SB_CTL_H                 
    		      ,B.YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID         
    		      ,B.YD_UP_WO_LOC            AS YD_UP_WO_LOC                    
    		      ,B.YD_UP_WO_LAYER          AS YD_UP_WO_LAYER                  
    		      ,B.YD_UP_WO_LOC_XAXIS      AS YD_UP_WO_LOC_XAXIS              
    		      ,B.YD_UP_WO_XAXIS_GAP_MAX  AS YD_UP_WO_XAXIS_GAP_MAX          
    		      ,B.YD_UP_WO_XAXIS_GAP_MIN  AS YD_UP_WO_XAXIS_GAP_MIN          
    		      ,B.YD_UP_WO_LOC_YAXIS      AS YD_UP_WO_LOC_YAXIS              
    		      ,B.YD_UP_WO_LOC_YAXIS1     AS YD_UP_WO_LOC_YAXIS1             
    		      ,B.YD_UP_WO_LOC_YAXIS2     AS YD_UP_WO_LOC_YAXIS2             
    		      ,B.YD_UP_WO_YAXIS_GAP_MAX  AS YD_UP_WO_YAXIS_GAP_MAX          
    		      ,B.YD_UP_WO_YAXIS_GAP_MIN  AS YD_UP_WO_YAXIS_GAP_MIN          
    		      ,B.YD_UP_WO_LOC_ZAXIS      AS YD_UP_WO_LOC_ZAXIS              
    		      ,B.YD_UP_WO_ZAXIS_GAP_MAX  AS YD_UP_WO_ZAXIS_GAP_MAX          
    		      ,B.YD_UP_WO_ZAXIS_GAP_MIN  AS YD_UP_WO_ZAXIS_GAP_MIN          
    		      ,B.YD_DN_WO_LOC            AS YD_DN_WO_LOC                    
    		      ,B.YD_DN_WO_LAYER          AS YD_DN_WO_LAYER                  
    		      ,B.YD_DN_WO_LOC_XAXIS      AS YD_DN_WO_LOC_XAXIS              
    		      ,B.YD_DN_WO_XAXIS_GAP_MAX  AS YD_DN_WO_XAXIS_GAP_MAX          
    		      ,B.YD_DN_WO_XAXIS_GAP_MIN  AS YD_DN_WO_XAXIS_GAP_MIN          
    		      ,B.YD_DN_WO_LOC_YAXIS      AS YD_DN_WO_LOC_YAXIS              
    		      ,B.YD_DN_WO_LOC_YAXIS1     AS YD_DN_WO_LOC_YAXIS1             
    		      ,B.YD_DN_WO_LOC_YAXIS2     AS YD_DN_WO_LOC_YAXIS2             
    		      ,B.YD_DN_WO_YAXIS_GAP_MAX  AS YD_DN_WO_YAXIS_GAP_MAX          
    		      ,B.YD_DN_WO_YAXIS_GAP_MIN  AS YD_DN_WO_YAXIS_GAP_MIN          
    		      ,B.YD_DN_WO_LOC_ZAXIS      AS YD_DN_WO_LOC_ZAXIS              
    		      ,B.YD_DN_WO_ZAXIS_GAP_MAX  AS YD_DN_WO_ZAXIS_GAP_MAX          
    		      ,B.YD_DN_WO_ZAXIS_GAP_MIN  AS YD_DN_WO_ZAXIS_GAP_MIN          
    		      ,B.YD_UP_WR_LOC            AS YD_UP_WR_LOC                    
    		      ,B.YD_UP_WR_LAYER          AS YD_UP_WR_LAYER                  
    		      ,B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP                
    		      ,B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS                  
    		      ,B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS                  
    		      ,B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1                 
    		      ,B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2                 
    		      ,B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS                  
    		      ,B.YD_DN_WR_LOC            AS YD_DN_WR_LOC                    
    		      ,B.YD_DN_WR_LAYER          AS YD_DN_WR_LAYER                  
    		      ,B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP                
    		      ,B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS                  
    		      ,B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS                  
    		      ,B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1                 
    		      ,B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2                 
    		      ,B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS   
    		--      ,(SELECT YD_STKBED_USG_CD FROM TB_YM_STACKER 
    		--         WHERE STACK_COL_GP = SUBSTR(B.YD_UP_WO_LOC,1,6)) AS BED_USG_CD               
    		      ,(SELECT ITEM      
    		          FROM USRYMA.TB_YM_RULE
    		         WHERE REPR_CD_GP = 'BAWB01') AS DIRECT_GP -- 블름 직상차 구분: 1:일반, 2:차량우선, 3:대차우선
    		--      , ( SELECT MIN(C.YS_STR_LOC)
    		--         FROM BRE.VW_YS_YSB007 C 
    		--            , USRYMA.TB_YM_EQUIP D
    		--        WHERE C.DEL_YN='N'
    		--          AND C.YS_STR_LOC=D.YD_EQP_ID
    		--          AND D.YD_EQP_STAT<>'B' --//고장모드가 아닌경우
    		--          AND ((REPLACE(C.YD_EQP_GP,'TF','TY') = SUBSTR((CASE WHEN NVL(B.YS_DN_WO_LOC,'0000') LIKE 'C_TY02%' THEN '0000' ELSE NVL(B.YS_DN_WO_LOC,'0000') END),3,2) AND SUBSTR(C.YS_STR_LOC,1,2)=SUBSTR(NVL(B.YS_DN_WO_LOC,'00'),1,2))
    		--               OR 
    		--               (REPLACE(C.YD_EQP_GP,'TF','TY') = SUBSTR((CASE WHEN NVL(B.YS_UP_WO_LOC,'0000') LIKE 'C_TY02%' THEN '0000' ELSE NVL(B.YS_UP_WO_LOC,'0000') END),3,2) AND SUBSTR(C.YS_STR_LOC,1,2)=SUBSTR(NVL(B.YS_UP_WO_LOC,'00'),1,2))
    		--               )
    		--        ) AS YD_EQP_ID_NEW 
    		        , YD_DN_WO_LOC 
    		        , YD_UP_WO_LOC
    		  FROM TB_YM_EQUIP  A                                               
    		      ,TB_YM_CRNSCH B                                               
    		 WHERE B.YD_EQP_ID   = A.EQUIP_GP  
    		   AND B.YD_WBOOK_ID = :V_YD_WBOOK_ID
    		   AND B.YD_EQP_ID   = :V_YD_EQP_ID                         
    		   AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')                        
    		 ORDER BY B.YD_CRN_SCH_ID
    		 */
    		jsCrnsch = commDao.select(jrInPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdCrnschByEqpIdandWBookId", logId, methodNm, "크레인스케줄 조회");   		
    		
    		for (int Loop_i = 1; Loop_i <= jsCrnsch.size(); Loop_i++) {
				jsCrnsch.absolute(Loop_i);
				jrInPara = JDTORecordFactory.getInstance().create();
				jrInPara.setRecord(jsCrnsch.getRecord());
				if (commUtils.trim(jrInPara.getFieldString("YD_DN_WO_LOC")).equals("")) {
					jrInPara.setField("YD_DN_WO_LOC", "XX010101");
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtDnLoc
					--크레인작업관리  - 
					MERGE INTO TB_YM_CRNSCH SC USING (   
					SELECT A.YD_EQP_ID                AS YD_EQP_ID                 --야드설비ID
					     , A.YD_CRN_SCH_ID            AS YD_CRN_SCH_ID                 --야드설비ID
					     , A.YD_UP_WO_LOC             AS YD_UP_WO_LOC              --야드권상지시위치
					     , A.YD_UP_WO_LAYER           AS YD_UP_WO_LAYER            --야드권상지시단
					     , B.STACK_LAYER_X_AXIS       AS YD_UP_WO_LOC_XAXIS        --야드권상지시X축
					     , C.YD_STK_BED_XAXIS_TOL     AS YD_UP_WO_XAXIS_GAP_MAX    --야드권상지시X축오차최대
					     , C.YD_STK_BED_XAXIS_TOL     AS YD_UP_WO_XAXIS_GAP_MIN    --야드권상지시X축오차최소
					     , B.STACK_LAYER_Y_AXIS       AS YD_UP_WO_LOC_YAXIS
					     , C.YD_STK_BED_YAXIS_TOL     AS YD_UP_WO_YAXIS_GAP_MAX    --야드권상지시Z축오차최대
					     , C.YD_STK_BED_YAXIS_TOL     AS YD_UP_WO_YAXIS_GAP_MIN    --야드권상지시Z축오차최소
					     , 0                          AS YD_UP_WO_LOC_ZAXIS        --야드권상지시Z축
					     , C.YD_STK_BED_ZAXIS_TOL     AS YD_UP_WO_ZAXIS_GAP_MAX    --야드권상지시Z축오차최대
					     , C.YD_STK_BED_ZAXIS_TOL     AS YD_UP_WO_ZAXIS_GAP_MIN    --야드권상지시Z축오차최소
					     , (SELECT ROTATION_ANGLE FROM TB_YM_STACKCOL WHERE STACK_COL_GP = SUBSTR(A.YD_UP_WO_LOC,1,6)) AS ROTATION_ANGLE
					     , :V_MODIFIER                AS MODIFIER              
					     , SYSDATE                    AS MOD_DDTT  
					     , :V_YD_DN_WO_LOC            AS YD_DN_WO_LOC
					 FROM TB_YM_CRNSCH A
					    , TB_YM_STACKLAYER B
					    , TB_YM_STACKER C
					WHERE A.YD_CRN_SCH_ID     = :V_YD_CRN_SCH_ID 
					  AND SUBSTR(A.YD_UP_WO_LOC,1,6) = B.STACK_COL_GP
					  AND SUBSTR(A.YD_UP_WO_LOC,7,2) = B.STACK_BED_GP
					  AND A.YD_UP_WO_LAYER    = B.STACK_LAYER_GP
					  AND SUBSTR(A.YD_UP_WO_LOC,1,6) = C.STACK_COL_GP
					  AND SUBSTR(A.YD_UP_WO_LOC,7,2) = C.STACK_BED_GP
					  AND A.DEL_YN = 'N'
					) DD ON (SC.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID )
					 WHEN MATCHED THEN UPDATE SET
					      SC.MODIFIER               = DD.MODIFIER
					     ,SC.MOD_DDTT               = DD.MOD_DDTT
					     ,SC.YD_DN_WO_LOC           = DD.YD_DN_WO_LOC
					     ,SC.YD_UP_WO_LOC_XAXIS     = DD.YD_UP_WO_LOC_XAXIS
					     ,SC.YD_UP_WO_XAXIS_GAP_MAX = DD.YD_UP_WO_XAXIS_GAP_MAX
					     ,SC.YD_UP_WO_XAXIS_GAP_MIN = DD.YD_UP_WO_XAXIS_GAP_MIN
					     ,SC.YD_UP_WO_LOC_YAXIS     = DD.YD_UP_WO_LOC_YAXIS
					     ,SC.YD_UP_WO_YAXIS_GAP_MAX = DD.YD_UP_WO_YAXIS_GAP_MAX
					     ,SC.YD_UP_WO_YAXIS_GAP_MIN = DD.YD_UP_WO_YAXIS_GAP_MIN
					     ,SC.YD_UP_WO_LOC_ZAXIS     = DD.YD_UP_WO_LOC_ZAXIS
					     ,SC.YD_UP_WO_ZAXIS_GAP_MAX = DD.YD_UP_WO_ZAXIS_GAP_MAX
					     ,SC.YD_UP_WO_ZAXIS_GAP_MIN = DD.YD_UP_WO_ZAXIS_GAP_MIN     
					     ,SC.UP_ROTATION_ANGLE      = DD.ROTATION_ANGLE
					*/				   
					intRtnVal = commDao.update(jrInPara, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtDnLoc", logId, methodNm, "크레인스케줄 갱신");

					if (intRtnVal <= 0) {
						szMsg = methodNm + " 크레인스케줄 To위치 Default값 등록 실패!!";
	    				commUtils.printLog(logId, szMsg, "SL");
	        			jrLocSrcRngRtn.setField("RTN", "-1");
	        			return jrLocSrcRngRtn;
					}
				}
			}
			
		//-------------------------------------------------------------------------------------------------------------
    		
        	commUtils.printLog(logId, methodNm, "S-");
        	
        	if (TcarTcSndyn.equals("Y")) {   // 대차 이동지시 송신
    			jrLocSrcRngRtn.setField("RTN", "9");
    			jrLocSrcRngRtn.setField("YD_EQP_ID", ydWrkPlanTcar);           //대차설비
    			jrLocSrcRngRtn.setField("YD_BAY_GP", szSchCd.substring(1,2));  //상차동
    		} else {
    			jrLocSrcRngRtn.setField("RTN", "1");
    		}
			return jrLocSrcRngRtn;			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
    }//end of LocSrcRngDataSet()   
    
    
	/**
	 * Scrap작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procScrapToLoc(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO 위치 결정:Scrap작업[BCoilSchSeEJB.procScrapToLoc] < " + methodNms;
    	String szLogMsg					= null;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"  ));			//크레인스케줄코드
		String ydWbookId	= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"  ));			//작업예약
		
		String StockId	   	= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"  ));			//크레인설비ID
		String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"  ));		
		String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"  ));		
		String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		String ydToLocGuide = commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));
		String ydBayGp	 	= commUtils.trim(jrCrnSch.getFieldString("YD_BAY_GP"));
		int    iCoilWt   	= Integer.parseInt(commUtils.nvl(jrCrnSch.getFieldString("SUM_MTL_WT"),"0")); 
		
		JDTORecord jrToLocUserRtn = JDTORecordFactory.getInstance().create();
		try {
			
			String szDBLogMsg	 	= "";
			String sRtnBedDan 		= "";  //TO위치	
		    String szStackColGp 	= "";
 			String szStackBedGp 	= "";
 			String szStackLayerGp 	= "";	
 			
 			commUtils.printLog(logId, methodNm, "S+");
			
			if ( ydUpWoLoc.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			String sAPP005_YN = YmComm.BCoilApplyYn("APP005","3","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");

			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, StockId);											//권상 STOCK
			jrTemp.setField("YD_SCH_CD"			, ydSchCd);											//스케쥴 코드
			jrTemp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);										//크레인 작업지시 ID
			jrTemp.setField("YD_EQP_ID"			, ydEqpId);											//설비 번호
			jrTemp.setField("COIL_WGT"			, ""+iCoilWt);											//설비 번호
			
			szLogMsg =  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			if (ydToLocGuide.length() == 10) {
				szLogMsg =  " 적재위치 가이드 열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 조회 시작";
				commUtils.printLog(logId, szLogMsg, "SL");
				sRtnBedDan = ydToLocGuide;
				
			} else {
				commUtils.printLog(logId, szLogMsg + ydSchCd.substring(2, 4), "SL");
				
				/**********************************************************
				 * ★★★★검색조건은 스크랩거 사용 3EKE02LM  -> 3ESC01LM ★★★★
				 **********************************************************/
				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNew
				WITH AUTO_CR_TABLE AS
				(
				SELECT CASE WHEN EQUIP_GP IN ('3ACRA1', '3ACRA2') AND YD_EQP_WRK_MODE2 = 'A' THEN 'Y'
				            WHEN EQUIP_GP IN ('3ACRA1', '3ACRA2') AND YD_EQP_WRK_MODE2 = 'R' THEN 'Y'
				            WHEN EQUIP_GP IN ('3CCRC1', '3CCRC2', '3CCRC3') AND YD_EQP_WRK_MODE2 = 'A' THEN 'Y'
				            WHEN EQUIP_GP IN ('3CCRC1', '3CCRC2', '3CCRC3') AND YD_EQP_WRK_MODE2 = 'R' THEN 'Y'
				            --E동 무인크레인별 위치 추가해야함
				            ELSE 'N' END AS IS_AUTO
				     , EQUIP_GP
				  FROM TB_YM_EQUIP
				 WHERE DEL_YN = 'N'
				   AND EQUIP_GP = :V_YD_EQP_ID       
				),
				TO_LOC_TABLE AS
				(
				SELECT * 
				  FROM (
				        SELECT
				               '1' AS PRIOR1 
				             , K2.HMI_STAT
				             , CASE WHEN K2.HMI_STAT = '1' AND K1.TAG_STACK_COL_GP IN ('3ESC02','3ESC03') THEN 'N'
				                    WHEN SUBSTR(K1.YD_SCH_CD,3,2) = 'YD' AND K1.TAG_STACK_COL_GP IN ('3ESC01') THEN 'N'
				                    ELSE 'Y' END  AS LOC_ABLE_YN
				             , K1.*       
				          FROM 
				               (
				                SELECT 1                      AS PRIOR2 
				                     , A.STACK_COL_GP         AS TAG_STACK_COL_GP
				                     , A.STACK_BED_GP         AS TAG_STACK_BED_GP
				                     , A.STACK_LAYER_GP       AS TAG_STACK_LAYER_GP
				                     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
				                                                '02', A.STACK_BED_GP)                                AS TAG_LEFT_BED
				                     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
				                                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_LEFT_LAYER
				                     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
				                                                '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS TAG_RIGHT_BED
				                     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
				                                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_RIGHT_LAYER
				                     -- 단 우선순위
				                     , '02'                   AS RULL_DAN_PRIOR  
				                     , :V_YD_SCH_CD           AS YD_SCH_CD         
				                     , :V_STOCK_ID            AS STOCK_ID
				                  FROM TB_YM_STACKLAYER A
				                     , TB_YM_STACKCOL   B
				                 WHERE A.STACK_LAYER_GP         IN ('01','02')
				                   AND A.STACK_LAYER_ACTIVE_STAT= 'E'
				                   AND A.STACK_LAYER_STAT       = 'E'
				                   AND A.STACK_COL_GP           = B.STACK_COL_GP
				                   AND A.STACK_COL_GP           IN ('3ESC01','3ESC02','3ESC03')
				                   AND SUBSTR(A.STACK_COL_GP,1,2)  = '3E'
				                   AND A.STACK_COL_GP||A.STACK_BED_GP||A.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
				                ) K1 
				         -- HMI_STAT :0 차량없음 ,1 : 차량있음
				              , (SELECT HMI_STAT FROM TB_YM_EQUIP WHERE EQUIP_GP = '3EGT01' ) K2            
				        )      
				  WHERE LOC_ABLE_YN = 'Y'      
				--  ORDER BY PRIOR1, PRIOR2 
				)  
				, TO_LOC_DATA_TABLE AS (
				-- TO 위치코일정보 SELECT
				SELECT A.PRIOR1 
				     , A.PRIOR2 
				     , A.TAG_STACK_LAYER_GP AS PRIOR3    -- 단우선순위
				     , A.RULL_DAN_PRIOR     AS RULL_DAN_PRIOR
				     , A.STOCK_ID           AS STOCK_ID
				     , A.TAG_STACK_COL_GP   
				     , A.TAG_STACK_BED_GP
				     , A.TAG_STACK_LAYER_GP
				     , A.TAG_LEFT_BED
				     , A.TAG_LEFT_LAYER
				     , (SELECT STACK_LAYER_ACTIVE_STAT 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_ACTIVE_STAT 
				     , (SELECT STACK_LAYER_STAT 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_LAYER_STAT
				     , (SELECT STOCK_ID 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_STOCK_ID
				     , A.TAG_RIGHT_BED
				     , A.TAG_RIGHT_LAYER
				     , (SELECT STACK_LAYER_ACTIVE_STAT 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_ACTIVE_STAT 
				     , (SELECT STACK_LAYER_STAT 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_LAYER_STAT
				     , (SELECT STOCK_ID 
				          FROM TB_YM_STACKLAYER B 
				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_STOCK_ID
				  FROM TO_LOC_TABLE A
				)
				SELECT * 
				  FROM
				(
				SELECT KK.*
				     , ROW_NUMBER() OVER(PARTITION BY PRIOR5 ORDER BY PRIOR1
				                                                    , PRIOR5
				                                                    , PRIOR2 
				                                                    , TAG_STACK_BED_GP
				                                                    ) AS GROUP_ROW       
				  FROM
				        (
				        SELECT K.* 
				             , CASE WHEN TAG_LEFT_STOCK_ID  IS NOT NULL AND TAG_RIGHT_STOCK_ID IS NOT NULL THEN '1'
				                    WHEN TAG_LEFT_STOCK_ID  IS NOT NULL                            THEN '2'
				                    WHEN TAG_RIGHT_STOCK_ID IS NOT NULL                            THEN '2'
				                    ELSE '3' END AS PRIOR5 
				          FROM TO_LOC_DATA_TABLE K
				         WHERE 1  = CASE WHEN TAG_STACK_LAYER_GP = '01'    THEN 1
				                         WHEN TAG_STACK_LAYER_GP = '02'                             -- 2단일 경우 좌우 적치 상태 CHECK
				                              AND TAG_LEFT_ACTIVE_STAT = 'E' AND TAG_RIGHT_ACTIVE_STAT = 'E' 
				                              AND TAG_LEFT_LAYER_STAT  = 'C' AND TAG_RIGHT_LAYER_STAT  = 'C' THEN 1
				                    ELSE 0 END  
				        ) KK       
				)
				, AUTO_CR_TABLE  CR
				 WHERE ((PRIOR5 < 3) OR (PRIOR5 = 3 AND GROUP_ROW < 10))   -- PRIOR5=3은 공BED: 1개만 검색
				   AND ((CR.IS_AUTO = 'Y' AND TAG_STACK_COL_GP IN (SELECT R.ITEM
				                                                     FROM TB_YM_RULE      R
				                                                        , AUTO_CR_TABLE   A
				                                                    WHERE R.REPR_CD_GP = 'CR0001'
				                                                      AND R.CD_GP      = A.EQUIP_GP
				                                                      AND R.DEL_YN     = 'N'
				                                                  ))
				      OR CR.IS_AUTO = 'N'
				      )
				 ORDER BY PRIOR1                                    --위치검색그룹
				        , TAG_STACK_COL_GP DESC
				        , PRIOR2                                        --야드위치 검색범위순서   
				        , CASE WHEN RULL_DAN_PRIOR = PRIOR3 THEN '1'
				               ELSE '2' END                             --단우선순위 
				        , PRIOR5                                        --평점계산시 우선순위
				        , TAG_STACK_BED_GP       
				*/        
 				JDTORecordSet outjsResult = null;
 				if(ydToLocGuide.length() >= 6) {
 					if(ydToLocGuide.length() == 6) {
 						jrTemp.setField("STACK_COL_GP"			, ydToLocGuide.substring(0,6));		//적치열									
 						jrTemp.setField("STACK_BED_GP"			, "%");		//적치BED									
 					} else if(ydToLocGuide.length() >= 8) {
 						jrTemp.setField("STACK_COL_GP"			, ydToLocGuide.substring(0,6));		//적치열									
 						jrTemp.setField("STACK_BED_GP"			, ydToLocGuide.substring(6,8));		//적치BED									
 					}
 					
 					if(ydBayGp.equals("E")){//E동 스크랩
 						outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNewToLocGuide", logId, methodNm, "TO위치 가이드로 ScrapTOSQL 베드 조회");
 					}else if(ydBayGp.equals("A")){//A동 스크랩
 						outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNewToLocGuideA", logId, methodNm, "TO위치 가이드로 ScrapTOSQL 베드 조회");
 					}
 				} else {
 					if(ydBayGp.equals("E")){//E동 스크랩
 						outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNew", logId, methodNm, "ScrapTOSQL 베드 조회");
 					}else if(ydBayGp.equals("A")){//A동 스크랩
 						outjsResult =	commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchScrapNewA", logId, methodNm, "ScrapTOSQL 베드 조회");
 					}
 					
 				}
 				
 				if (outjsResult.size() <= 0) {
 					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
 					commUtils.printLog(logId, szLogMsg, "SL");
 					if (sAPP005_YN.equals("Y")) {
		    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
		    			jrLog.setField("STOCK_ID"		, StockId);
		    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
		    			jrLog.setField("YD_GP"			, "3");
		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
		    			jrLog.setField("SCH_CONTENTS"	, "대상코일위치검색실패:"+ StockId+" LOG :"+"\r\n" );
		    			
		    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
					}
 					
 					return YmConstant.RETN_CD_FAILURE;
 				} else {
 					
 					szStackColGp  	= commUtils.trim(outjsResult.getRecord(0).getFieldString("TAG_STACK_COL_GP"));
 					szStackBedGp  	= commUtils.trim(outjsResult.getRecord(0).getFieldString("TAG_STACK_BED_GP"));
 					szStackLayerGp  = commUtils.trim(outjsResult.getRecord(0).getFieldString("TAG_STACK_LAYER_GP"));
 					sRtnBedDan 	    = szStackColGp+szStackBedGp+ szStackLayerGp;
 					
 				}
 				
 				if (sRtnBedDan.length() < 10) {
 					szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
 					commUtils.printLog(logId, szLogMsg, "SL");
 					if (sAPP005_YN.equals("Y")) {
 		    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
 		    			jrLog.setField("STOCK_ID"		, StockId);
 		    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
 		    			jrLog.setField("YD_GP"			, "3");
 		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
 		    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택실패:"+ StockId+" LOG :"+"\r\n" + szDBLogMsg);
 		    			
 		    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
 		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
 					}
 					
 					return YmConstant.RETN_CD_FAILURE;				
 				}
 				
				if (sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, StockId);
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "3");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택:"+ StockId+"  선택위치:"+ sRtnBedDan + " LOG :"+"\r\n" + szDBLogMsg);
	    				    			
	    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
 			}				
         

			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWbookId); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch  );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
        }//end of try~catch			

		return YmConstant.RETN_CD_SUCCESS;
	} 
    

	/**
	 * 보조작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocDummy(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO위치결정:보조작업[BCoilSchSeEJB.procToLocDummy] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord	jrTemp			= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		

		try {
			
			
			commUtils.printLog(logId, methodNm, "S+");
			
			if ( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			String sAPP005_YN = YmComm.BCoilApplyYn("APP005","3","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");						
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
	    	jrTemp.setField("STOCK_ID"      , szSTOCK_ID);											//권상 STOCK
			jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);		
			
			szLogMsg =  " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	DUMMY 의 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------

			String sBAY_GP = szYD_EQP_ID.substring(1,2);
			
			String sDUMMY_NEW_YN = YmComm.BCoilApplyYn("APP060","3","DUMMY_"+ sBAY_GP +"_YN"); //동별 신규 더미 권하위치 검색 적용여부
			commUtils.printLog(logId,  " #### " + sBAY_GP + " 동 신규 DUMMY 권하위치 검색 적용여부 : " + sDUMMY_NEW_YN, "SL");						
			
			JDTORecordSet outjsResult = null;
			
			if("Y".equals(sDUMMY_NEW_YN)) {
				//동별 신규 DUMMY 권하위치 검색 적용 
				
				if("A".equals(sBAY_GP)) {
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdNewDummySearchA", logId, methodNm, "A동 Dummy 작업 적치가능한 베드 조회");
				} else if("B".equals(sBAY_GP)) {
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdNewDummySearchB", logId, methodNm, "B동 Dummy 작업 적치가능한 베드 조회");
				} else if("C".equals(sBAY_GP)) {
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdNewDummySearchC", logId, methodNm, "C동 Dummy 작업 적치가능한 베드 조회");
				} else if("D".equals(sBAY_GP)) {
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdNewDummySearchD", logId, methodNm, "D동 Dummy 작업 적치가능한 베드 조회");
				} else if("E".equals(sBAY_GP)) {
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdNewDummySearchE", logId, methodNm, "E동 Dummy 작업 적치가능한 베드 조회");
				} 
				
			} else {
			
				//기존 Dummy 권하 위치 검색
				outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdDummySearch", logId, methodNm, "기존 Dummy 권하 위치 검색");
			
			}
			
			if (outjsResult.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
			
//LOG_TABLE 
				if (sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
	    			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
	    			jrLog.setField("YD_GP"			, "3");
	    			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
	    			jrLog.setField("SCH_CONTENTS"	, "대상코일위치검색실패:"+ szSTOCK_ID+" LOG :"+"\r\n" );

	    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}	
				return YmConstant.RETN_CD_FAILURE;
			}
	
			JDTORecord	jrResult	= JDTORecordFactory.getInstance().create();
			JDTORecord	jrToLocDummyRtn	= JDTORecordFactory.getInstance().create();
			
	    	String szStackColGp 	= "";
			String szStackBedGp 	= "";
			String szStackLayerGp 	= "";	
			String szToPosGrade 	= "999";

			String sRtnBedDan 		= "";	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSchSule = this.procSchSule(logId, methodNms, szSTOCK_ID, szYD_SCH_CD);
			
			String szDBLogMsg	 = "";
			String szGRIDE = "9";
			
			if("Y".equals(sDUMMY_NEW_YN)) {
				
			    // 적치 가능 check JDTORecord;
				JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
				
				//동별 신규 DUMMY 권하위치 검색 적용 
				for (int i = 1; i <= outjsResult.size(); i++) {

					outjsResult.absolute(i);
					//jrResult  = outjsResult.getRecord();

					////평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
					//jrResult.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID); 
					//jrResult.setField("YD_SCH_CD"	 	, szYD_SCH_CD); 
					//jrSchSule.setField("SCH_LOG"	    , sAPP005_YN); 	
					
					//jrToLocDummyRtn = this.procGradeAnalysisDummy(logId, methodNm, jrResult, jrSchSule);
					
					//szToPosGrade    		= commUtils.trim(jrToLocDummyRtn.getFieldString("GRIDE"  ));
					//String szToPosGradeMsg 	= commUtils.trim(jrToLocDummyRtn.getFieldString("GRADE_CONTENTS"));
					//String szGradeRtn		= commUtils.trim(jrToLocDummyRtn.getFieldString("GRADE_RTN"  ));
					
					//if (szToPosGradeMsg.length() > 0 ) {
					//	szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
					//}

					//outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
					
					//if("1".equals(szGradeRtn)) {
						
						jrAbleResult = outjsResult.getRecord();
						
						szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
						szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
						szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
						
						//적치가능 check
						JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);
						
						String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
						String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
						if (LocAbleRtnMsg.length() > 0 ) {
							szDBLogMsg = szDBLogMsg + LocAbleRtnMsg +"\r\n";
						}
						
						
						if (LocAbleRtn.equals("1")) {
							szLogMsg = "#### 적치가능 위치 : " + szStackColGp+szStackBedGp+ szStackLayerGp + "  << " + methodNm;
							commUtils.printLog(logId, szLogMsg, "SL");				
						    //적치가능 
							sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp;
			    			break;
						}
						
					//}

				}

			} else {
			
			    // 평점 CEHCK	
				for (int i = 1; i <= outjsResult.size(); i++) {
		
					outjsResult.absolute(i);
					jrResult  = outjsResult.getRecord();
	
					szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
					szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
					szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
	
					
					//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
					jrResult.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID); 
					jrResult.setField("YD_SCH_CD"	 	, szYD_SCH_CD); 
					jrSchSule.setField("SCH_LOG"	    , sAPP005_YN); 	
					
					jrToLocDummyRtn = this.procGradeAnalysisDummy(logId, methodNm, jrResult, jrSchSule);
					
					szToPosGrade    		= commUtils.trim(jrToLocDummyRtn.getFieldString("GRIDE"  ));
					String szToPosGradeMsg 	= commUtils.trim(jrToLocDummyRtn.getFieldString("GRADE_CONTENTS"));
					if (szToPosGradeMsg.length() > 0 ) {
						szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
					}
					
					outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
				}
				
			    // 평점 CEHCK	 SORT
				String szGrideSort  = "99";
				int igride          = 10; 
				JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
				JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
				
				for (int i = 1; i <= igride; i++) {
					for (int j = 1; j <= outjsResult.size(); j++) {
						outjsResult.absolute(j);
						jrGrideResult  = outjsResult.getRecord();
						szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
						int iGrideSort = Integer.parseInt(szGrideSort);
		
						if (!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
						
							if (iGrideSort == i) {
								jsGrideResult.addRecord(outjsResult.getRecord());
							}
						}
					}
				}
					
			    // 적치 가능 check
				JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
				for (int i = 1; i <= jsGrideResult.size(); i++) {
	
					jsGrideResult.absolute(i);
					jrAbleResult  = jsGrideResult.getRecord();
					
					szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
					szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
					szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
					szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
	
					//적치가능 check
					JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);
	
					String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
					String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
					if (LocAbleRtnMsg.length() > 0 ) {
						szDBLogMsg = szDBLogMsg + LocAbleRtnMsg +"\r\n";
					}
					
					
					if (LocAbleRtn.equals("1")) {
						szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
						commUtils.printLog(logId, szLogMsg, "SL");				
					    //적치가능 
						sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp;
		    			break;
					}
				}	
			}
			
			if (sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				if (sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
	    			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
	    			jrLog.setField("YD_GP"			, "3");
	    			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
	    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택실패:"+ szSTOCK_ID+" LOG :" +"\r\n" +  szDBLogMsg);
	    			
	    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				
				return YmConstant.RETN_CD_FAILURE;				
			}
			
			if (sAPP005_YN.equals("Y")) {
    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
    			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
    			jrLog.setField("YD_GP"			, "3");
    			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택:"+ szSTOCK_ID+"  선택위치:"+ sRtnBedDan + "  평점:"+ szGRIDE +" LOG :" +"\r\n"+ szDBLogMsg );
    			    			
    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
			}

			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
			jrSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
			jrSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	szYD_UP_WO_LAYER);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch  );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}    
 
    
	/**
	 *      [A] 오퍼레이션명 : 평점분석
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procGradeAnalysis( String logId, String methodNms, JDTORecord jrResult, JDTORecord jrSchRule) throws JDTOException {
    	String methodNm = "평점분석[BCoilSchSeEJB.procGradeAnalysis] < " + methodNms;
    	String szLogMsg			= null;
    	String szGRIDE  		= "99";

    	String szSTOCK_ID 		= commUtils.trim(jrResult.getFieldString("STOCK_ID"  ));
    	String szSTACK_COL_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
		String szSTACK_BED_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
		String szSTACK_LAYER_GP	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
		
		String szLEFT_STOCK_ID 	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_STOCK_ID"  ));	//2단일 경우 좌측
		String szLEFT_BED  		= commUtils.trim(jrResult.getFieldString("TAG_LEFT_BED"  ));		//2단일 경우 좌측
		String szLEFT_LAYER    	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_LAYER"  ));		//2단일 경우 좌측
		
		String szRIGHT_STOCK_ID	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_STOCK_ID"  ));	//2단일 경우 우측		
		String szRIGHT_BED	   	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_BED"  ));		//2단일 경우 우측
		String szRIGHT_LAYER    = commUtils.trim(jrResult.getFieldString("TAG_RIGHT_LAYER"  ));		//2단일 경우 우측

		String szYD_CRN_SCH_ID  = commUtils.trim(jrResult.getFieldString("YD_CRN_SCH_ID"  ));		//크레인 스케쥴ID
		String szYD_SCH_CD      = commUtils.trim(jrResult.getFieldString("YD_SCH_CD"  ));		    //크레인 스케쥴코드
		
		String sAPP005_YN       = commUtils.trim(jrSchRule.getFieldString("SCH_LOG"  ));		    //스케줄 LOG여부
		String szGROUP_SEQ		= commUtils.trim(jrSchRule.getFieldString("GROUP_SEQ"  ));		   
		commUtils.printLog(logId, methodNm, "S+");
		
		commUtils.printLog(logId, "대상코일위치:"+ szSTACK_COL_GP+szSTACK_BED_GP+szSTACK_LAYER_GP, "SL");
		commUtils.printLog(logId, "대상코일번호:"+ szSTOCK_ID + "좌측코일번호:"+ szLEFT_STOCK_ID+ "우측코일번호:"+ szRIGHT_STOCK_ID, "SL");
		
		JDTORecord jrLocAbleRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrGradeAnalysisRtn = JDTORecordFactory.getInstance().create();
		try {
						
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, szSTOCK_ID); 
			jrTemp.setField("STACK_COL_GP"		, szSTACK_COL_GP); 
			jrTemp.setField("STACK_BED_GP"		, szSTACK_BED_GP); 
			jrTemp.setField("STACK_LAYER_GP"	, szSTACK_LAYER_GP); 
			jrTemp.setField("LEFT_STOCK_ID"		, szLEFT_STOCK_ID);	 
			jrTemp.setField("RIGHT_STOCK_ID"	, szRIGHT_STOCK_ID);	 
			jrTemp.setField("TAG_STACK_LAYER_GP", szSTACK_LAYER_GP);
			jrTemp.setField("GROUP_SEQ", szGROUP_SEQ);
			
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGride
			WITH SPEC_TABLE AS (
			    -- 특정규격CHECK 
			    SELECT DTL_ITM1 AS STACK_RULE_NAME 
			      FROM USRYMA.TB_YM_RULE
			     WHERE REPR_CD_GP = 'YM004'
			       AND DEL_YN = 'N'
			),
			TEMP_DATA AS 
			(
			SELECT C.COIL_NO          AS C_COIL_NO
			     , C.CURR_PROG_CD     AS C_PROG_CD    -- 진도코드
			     , C.NEXT_PROC        AS C_NEXT_PROC  -- 차공정
			     , C.HR_SPEC_ABBSYM   AS C_SPEC_ABBSYM-- 규격약호
			     , C.ORD_NO           AS C_ORD_NO     -- 주문번호
			     , C.ORD_DTL          AS C_ORD_DTL    -- 주문행번
			     , C.RECEIPT_DATE     AS C_RECEIPT_DT -- 입고일자
			     , C.MILL_INI_DATE    AS C_MILL_DT    -- 압연일시
			     , C.DEMANDER_CD      AS C_DEMANDER_CD-- 수요가코드 
			     , C.COIL_T           AS C_THICK      -- 두께
			     , C.COIL_W           AS C_WIDTH      -- 폭
			     , C.COIL_WT          AS C_WEIGTH     -- 중량 
			     , C.COIL_OUTDIA      AS C_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=C.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(C.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - C.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS C_JJANG_GP  --짱구여부    
			     , L.COIL_NO          AS L_COIL_NO
			     , L.CURR_PROG_CD     AS L_PROG_CD    -- 진도코드
			     , L.NEXT_PROC        AS L_NEXT_PROC  -- 차공정
			     , L.HR_SPEC_ABBSYM   AS L_SPEC_ABBSYM-- 규격약호
			     , L.ORD_NO           AS L_ORD_NO     -- 주문번호
			     , L.ORD_DTL          AS L_ORD_DTL    -- 주문행번
			     , L.RECEIPT_DATE     AS L_RECEIPT_DT -- 입고일자
			     , L.MILL_INI_DATE    AS L_MILL_DT    -- 압연일시
			     , L.DEMANDER_CD      AS L_DEMANDER_CD-- 수요가코드 
			     , L.COIL_T           AS L_THICK      -- 두께
			     , L.COIL_W           AS L_WIDTH      -- 폭
			     , L.COIL_WT          AS L_WEIGTH     -- 중량 
			     , L.COIL_OUTDIA      AS L_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=L.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(L.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - L.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS L_JJANG_GP  --짱구여부    
			     , R.COIL_NO          AS R_COIL_NO
			     , R.CURR_PROG_CD     AS R_PROG_CD    -- 진도코드
			     , R.NEXT_PROC        AS R_NEXT_PROC  -- 차공정
			     , R.HR_SPEC_ABBSYM   AS R_SPEC_ABBSYM-- 규격약호
			     , R.ORD_NO           AS R_ORD_NO     -- 주문번호
			     , R.ORD_DTL          AS R_ORD_DTL    -- 주문행번
			     , R.RECEIPT_DATE     AS R_RECEIPT_DT -- 입고일자
			     , R.MILL_INI_DATE    AS R_MILL_DT    -- 압연일시
			     , R.DEMANDER_CD      AS R_DEMANDER_CD-- 수요가코드 
			     , R.COIL_T           AS R_THICK      -- 두께
			     , R.COIL_W           AS R_WIDTH      -- 폭
			     , R.COIL_WT          AS R_WEIGTH     -- 중량 
			     , R.COIL_OUTDIA      AS R_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=R.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(R.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - R.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS R_JJANG_GP  --짱구여부
			  FROM (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_STOCK_ID            ) C        --대상코일
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_LEFT_STOCK_ID       ) L  --하단LEFT
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A 
			         WHERE COIL_NO = :V_RIGHT_STOCK_ID      ) R  --하단RIGHT
			 WHERE C.T_ROW = L.T_ROW(+)          
			   AND C.T_ROW = R.T_ROW(+)      
			)
			SELECT CASE  -- 1단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8') 
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < 180  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < 180 
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < 200 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < 200
			            THEN CASE WHEN C_PROG_CD = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌우) 진도코드
			                      WHEN C_PROG_CD = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
			                      WHEN C_PROG_CD = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
			                      WHEN L_COIL_NO IS NULL                               THEN '3' --좌 공BED
			                      WHEN R_COIL_NO IS NULL                               THEN '3' --우 공BED
			                      ELSE '9' END 
			             -- 2단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN L_JJANG_GP = 'Y'  OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌하단우하단) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
			                      ELSE '8' END 
			            -- 1단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD     IN ('F','G','H','J','K','L','M','5','6','7','8')   
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < 180  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < 180 
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < 200 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < 200
			             
			            THEN CASE WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우축 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우측 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '3' --좌측   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '3' --우측   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우측 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '5' --우측   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '5' --좌측   동일 주문번호 
			                      WHEN L_COIL_NO IS NULL                               THEN '6' --좌측 공BED
			                      WHEN R_COIL_NO IS NULL                               THEN '6' --우측 공BED
			                      ELSE '9' END 
			            -- 2단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			            THEN CASE WHEN L_JJANG_GP = 'Y' OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우하단 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우하단 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '3' --좌하단   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '3' --우하단   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우하단 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '5' --우하단   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '5' --좌하단   동일 주문번호 
			                      ELSE '8' END 
			                       
			             ELSE '9' END GRIDE           
			     , C.*        
			  FROM TEMP_DATA C
			*/
			JDTORecordSet jsCommResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGride", logId, methodNm, "코일공통 평점 조회");
			JDTORecord jrCommResult = JDTORecordFactory.getInstance().create();
			
			if (jsCommResult.size() == 1) {
				jsCommResult.absolute(1);
				jrCommResult  = jsCommResult.getRecord();
				
				szGRIDE = commUtils.nvl(jrCommResult.getFieldString("GRIDE"),"9");
					
				jrGradeAnalysisRtn.setField("GRIDE"		    , szGRIDE);
				jrGradeAnalysisRtn.setField("GRADE_RTN"		, "1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS", "LOC:"+szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치가능 위치평점:"+ szGRIDE );
    			commUtils.printLog(logId, "평점 : " + szGRIDE, "SL");
            	commUtils.printLog(logId, methodNm, "S-");
    			return jrGradeAnalysisRtn;
			} else {
				szLogMsg = methodNm+ "코일공통  평점 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
	        	commUtils.printLog(logId, methodNm, "S-");
				
    			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS"	, "코일공통  평점 검색 실패 ");
				return jrGradeAnalysisRtn;
			}
		} catch(Exception e) {
			
			szLogMsg = methodNm+ "코일창고야드위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrGradeAnalysisRtn.setField("GRIDE"		    , YmConstant.RETN_CD_FAILURE);
			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
			return jrGradeAnalysisRtn;
		}
	} //   
          
    
	/**
	 * 사용자지정작업
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocUser(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO위치결정:사용자지정작업[BCoilSchSeEJB.procToLocUser] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String ydSchCd 	   	= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));		//크레인스케줄코드
		String ydWookId		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));	//작업예약
		String ydToLocGuide	= commUtils.trim(jrWbook.getFieldString("YD_TO_LOC_GUIDE")); //작업예약

		String StockId	   	= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String ydCrnSchId 	= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String ydEqpId     	= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String ydUpWoLoc 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String ydUpWoLayer 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		
		boolean blTcStat 	= false;
		String sRtnBedDan 	= "";  //TO위치	
		JDTORecord jrToLocUserRtn = JDTORecordFactory.getInstance().create();
		try {
			
			String szDBLogMsg	 = "";
			commUtils.printLog(logId, methodNm, "S+");
			
			if ( ydUpWoLoc.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			String sAPP005_YN = YmComm.BCoilApplyYn("APP005","3","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");

			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, StockId);											//권상 STOCK
			jrTemp.setField("YD_TO_LOC_GUIDE"	, ydToLocGuide);									//가이드
			jrTemp.setField("YD_SCH_CD"			, ydSchCd);											//스케쥴 코드
			jrTemp.setField("YD_CRN_SCH_ID"		, ydCrnSchId);										//크레인 작업지시 ID
			jrTemp.setField("YD_EQP_ID"			, ydEqpId);											//설비 번호
			
			szLogMsg =  " TOSQL:["+ydCrnSchId+ "] 권상재료["+StockId +" +스케쥴 코드 : "+ ydSchCd + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	사용자 지정한 위치만 검색
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			if (ydToLocGuide.length() == 10) {
				// 차량
				szLogMsg =  " 적재위치 가이드 열+ 베드+ 단  지정된 경우 ["+ydToLocGuide+"]의 베드 조회 시작";
				commUtils.printLog(logId, szLogMsg, "SL");
				sRtnBedDan = ydToLocGuide;
				
			} else {
				commUtils.printLog(logId, szLogMsg + ydSchCd.substring(2, 4), "SL");
				  // 1단 
                 if (ydSchCd.substring(2, 4).equals("HS")||ydSchCd.substring(2, 4).equals("GF")) {
                	 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdUserSearch1Dan  
                	 SELECT STACK_COL_GP
                	      , STACK_BED_GP 
                	      , STACK_LAYER_GP 
                	      , YD_MTL_SH 
                	   FROM
                	 (
                	 SELECT SL.STACK_COL_GP
                	      , SL.STACK_BED_GP 
                	      , SL.STACK_LAYER_GP 
                	      , COUNT(SL.STOCK_ID) AS YD_MTL_SH
                	   FROM TB_YM_STACKLAYER SL
                	  WHERE SL.DEL_YN        = 'N'
                	    AND SL.STACK_COL_GP          LIKE SUBSTR(:V_YD_TO_LOC_GUIDE,1,6) || '%' --가이드 열
                	    AND SL.STACK_BED_GP          LIKE SUBSTR(:V_YD_TO_LOC_GUIDE,7,2) || '%' --가이드 BED 
                	    AND SL.STACK_LAYER_GP='01' --차량위치는 1단만 적치
                	    AND SL.STACK_COL_GP||SL.STACK_BED_GP||SL.STACK_LAYER_GP NOT IN ( SELECT NVL(YD_DN_WO_LOC||YD_DN_WO_LAYER,'1') FROM TB_YM_CRNSCH WHERE DEL_YN = 'N')
                	    AND SL.STOCK_ID IS NULL
                	  GROUP BY SL.STACK_COL_GP, SL.STACK_BED_GP, SL.STACK_LAYER_GP 
                	  ORDER BY SL.STACK_COL_GP, SL.STACK_BED_GP, SL.STACK_LAYER_GP 
                	  )
                	 WHERE YD_MTL_SH = 0
                	    AND ROWNUM = 1
                	*/ 
     				JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdUserSearch1Dan", logId, methodNm, "사용자TOSQL 베드 조회");
     				outjsResult.first();
					JDTORecord outjrResult = outjsResult.getRecord();
					
        			String StackColGp1 	= commUtils.trim(outjrResult.getFieldString("STACK_COL_GP"));//차량정지위치 적치열
        			String StackBedGp1	= commUtils.trim(outjrResult.getFieldString("STACK_BED_GP"));//차량정지위치 적치베드
        			String StackLayerGp1= commUtils.trim(outjrResult.getFieldString("STACK_LAYER_GP"));//차량정지위치 적치단
        			
        			sRtnBedDan = StackColGp1 + StackBedGp1 + StackLayerGp1;
     				if (sRtnBedDan.length() < 10) {
     					szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 TO위치 결정 실패 ";
     					commUtils.printLog(logId, szLogMsg, "SL");
    					
     					if (sAPP005_YN.equals("Y")) {
    		    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    		    			jrLog.setField("STOCK_ID"		, StockId);
    		    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
    		    			jrLog.setField("YD_GP"			, "3");
    		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
    		    			jrLog.setField("SCH_CONTENTS"	, "대상코일위치검색실패:"+ StockId+" LOG :"+"\r\n" );
    		    			
    		    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
    		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
    					}

     					return YmConstant.RETN_CD_FAILURE;				
     				}        			
                 } else {
                	 
     				/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdUserSearch 
     				WITH DATA_TABLE AS
     				(
     				-- 대상 위치 SELECT
     				SELECT B.STACK_COL_BED_DIRECTION    AS PRIOR1 --BED 방향 우선('5','X','4')
     				     , A.STACK_COL_GP               AS TAG_STACK_COL_GP
     				     , A.STACK_BED_GP               AS TAG_STACK_BED_GP
     				     , A.STACK_LAYER_GP             AS TAG_STACK_LAYER_GP
     				     , B.STACK_COL_USAGE_CD         AS TAG_COL_USE_CD
     				     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
     				                                '02', A.STACK_BED_GP)                                AS TAG_LEFT_BED
     				     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
     				                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_LEFT_LAYER
     				     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
     				                                '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS TAG_RIGHT_BED
     				     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
     				                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_RIGHT_LAYER
     				                            
     				  FROM TB_YM_STACKLAYER A
     				     , TB_YM_STACKCOL B
     				     , (
     				         SELECT K.STACK_COL_GP
     				              , K.STACK_BED_GP
     				              , K.STACK_LAYER_GP
     				              , (SELECT ITEM
     				                   FROM USRYMA.TB_YM_RULE
     				                  WHERE REPR_CD_GP = 'YMGUN'
     				                    AND (SELECT COIL_OUTDIA 
     				                           FROM USRPTA.TB_PT_COILCOMM
     				                          WHERE COIL_NO = K.STOCK_ID) 
     				                          BETWEEN TO_NUMBER(DTL_ITM1)  AND TO_NUMBER(DTL_ITM2)) 
     				                AS STACK_COL_BED_DIRECTION
     				           FROM TB_YM_STACKLAYER K
     				          WHERE STOCK_ID = :V_STOCK_ID
     				       )C
     				 WHERE SUBSTR(A.STACK_COL_GP,1,1)  = '3'
     				   AND A.STACK_COL_GP          LIKE SUBSTR(:V_YD_TO_LOC_GUIDE,1,6) || '%' --가이드 열
     				   AND A.STACK_BED_GP          LIKE SUBSTR(:V_YD_TO_LOC_GUIDE,7,2) || '%' --가이드 BED
     				   AND A.STACK_LAYER_GP       IN ('01','02')
     				   AND A.STACK_LAYER_ACTIVE_STAT= 'E'
     				   AND A.STACK_LAYER_STAT       = 'E'
     				   AND A.STACK_COL_GP           = B.STACK_COL_GP
     				--   AND B.STACK_COL_BED_DIRECTION >= C.STACK_COL_BED_DIRECTION
     				   AND 1 = CASE WHEN SUBSTR(A.STACK_COL_GP,3,2) = 'HS' THEN 1
     				                WHEN B.STACK_COL_BED_DIRECTION >= C.STACK_COL_BED_DIRECTION THEN 1
     				                ELSE 2 END
     				   AND SUBSTR(A.STACK_COL_GP,1,2)  = SUBSTR(C.STACK_COL_GP ,1,2)  

     				) 
     				SELECT K.* 
     				     , CASE WHEN TAG_LEFT_STOCK_ID  IS NULL AND TAG_RIGHT_STOCK_ID IS NULL THEN '3'
     				            WHEN TAG_LEFT_STOCK_ID  IS NOT NULL                            THEN '2'
     				            WHEN TAG_RIGHT_STOCK_ID IS NOT NULL                            THEN '2'
     				            ELSE '1' END AS PRIOR5 
     				  FROM          
     				(
     				SELECT A.PRIOR1 --BED 방향 우선
     				     , :V_STOCK_ID AS STOCK_ID
     				     , A.TAG_STACK_COL_GP
     				     , A.TAG_STACK_BED_GP
     				     , A.TAG_STACK_LAYER_GP
     				     , A.TAG_COL_USE_CD
     				     , A.TAG_LEFT_BED
     				     , A.TAG_LEFT_LAYER
     				     , (SELECT STACK_LAYER_ACTIVE_STAT 
     				          FROM TB_YM_STACKLAYER B 
     				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
     				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
     				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_ACTIVE_STAT 
     				     , (SELECT STACK_LAYER_STAT 
     				          FROM TB_YM_STACKLAYER B 
     				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
     				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
     				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_LAYER_STAT
     				     , (SELECT STOCK_ID 
     				          FROM TB_YM_STACKLAYER B 
     				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
     				           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
     				           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_STOCK_ID
     				     , A.TAG_RIGHT_BED
     				     , A.TAG_RIGHT_LAYER
     				     , (SELECT STACK_LAYER_ACTIVE_STAT 
     				          FROM TB_YM_STACKLAYER B 
     				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
     				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
     				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_ACTIVE_STAT 
     				     , (SELECT STACK_LAYER_STAT 
     				          FROM TB_YM_STACKLAYER B 
     				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
     				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
     				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_LAYER_STAT
     				     , (SELECT STOCK_ID 
     				          FROM TB_YM_STACKLAYER B 
     				         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
     				           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
     				           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_STOCK_ID
     				  FROM DATA_TABLE A
     				 ) K
     				 WHERE 1  = CASE WHEN TAG_STACK_LAYER_GP = '01'  THEN 1
     				                 WHEN TAG_STACK_LAYER_GP = '02'    --2 단일 경우 좌우 적치 상태 CHECK
     				                      AND TAG_LEFT_ACTIVE_STAT = 'E' AND TAG_RIGHT_ACTIVE_STAT = 'E' 
     				                      AND TAG_LEFT_LAYER_STAT  = 'C' AND TAG_RIGHT_LAYER_STAT  = 'C' THEN 1
     				                 ELSE 0 END                                
     				 ORDER BY PRIOR1 -- BED방향(군) 
     				        , PRIOR5 -- 평점계산시 우선순위
     				*/ 
     				
     				JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdUserSearch", logId, methodNm, "사용자TOSQL 베드 조회");
     				if (outjsResult.size() <= 0) {
     					szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
     					commUtils.printLog(logId, szLogMsg, "SL");
     					if (sAPP005_YN.equals("Y")) {
    		    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    		    			jrLog.setField("STOCK_ID"		, StockId);
    		    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
    		    			jrLog.setField("YD_GP"			, "3");
    		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
    		    			jrLog.setField("SCH_CONTENTS"	, "대상코일위치검색실패:"+ StockId+" LOG :"+"\r\n" );
    		    			
    		    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
    		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
    					}
     					
     					return YmConstant.RETN_CD_FAILURE;
     				}
     				
     				JDTORecord	jrResult		= JDTORecordFactory.getInstance().create();
     				JDTORecord	jrResultCar		= JDTORecordFactory.getInstance().create();
     				JDTORecord	jrResultCarLay	= JDTORecordFactory.getInstance().create();
     				JDTORecordSet jsResultCar 	= JDTORecordFactory.getInstance().createRecordSet("Temp");
     				JDTORecordSet jsResultCarLay = JDTORecordFactory.getInstance().createRecordSet("Temp");
     		    	String szStackColGp 	= "";
     				String szStackBedGp 	= "";
     				String szStackLayerGp 	= "";	
     				String szToPosGrade 	= "999";
     	
     				String sRtnBed  		= "";	
     				
     				String sRtnBedDanCheck 	= "";	
     				int iToPosGrade 		= 999;
     				int iToPosGradeCheck	= 999;
     				String szCOIL_CARD_NO   = "";
     				String szUpUsageCd      = "";   //용도
     				
     			    //----------------------------------------------------------------------------------------------------------------------
     				//	적재가능 위치 SCH RULL 검색
     				//----------------------------------------------------------------------------------------------------------------------
     				JDTORecord  jrSchSule = this.procSchSule(logId, methodNms, StockId, ydSchCd);
     				
     				
     			    // 평점 CEHCK	
     				for (int i = 1; i <= outjsResult.size(); i++) {
     		
     					outjsResult.absolute(i);
     					jrResult  = outjsResult.getRecord();
     	
     					szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
     					szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
     					szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
     					szUpUsageCd 	= commUtils.trim(jrResult.getFieldString("TAG_COL_USE_CD"  ));	
     	
     					//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
     					jrResult.setField("YD_CRN_SCH_ID"	, ydCrnSchId); 
     					jrResult.setField("YD_SCH_CD"	 	, ydSchCd); 
     					jrSchSule.setField("SCH_LOG"	    , sAPP005_YN); 	
     					
     					jrToLocUserRtn = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
     					
     					szToPosGrade    		= commUtils.trim(jrToLocUserRtn.getFieldString("GRIDE"  ));
     					String szToPosGradeMsg 	= commUtils.trim(jrToLocUserRtn.getFieldString("GRADE_CONTENTS"));
     					if (szToPosGradeMsg.length() > 0 ) {
     						szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
     					}
     					
     					outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
     				}
     				
     			    // 평점 CEHCK	 SORT
     				String szGrideSort  = "99";
     				int igride          = 10; 
     				JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
     				JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
     				
     				for (int i = 1; i <= igride; i++) {
     					for (int j = 1; j <= outjsResult.size(); j++) {
     						outjsResult.absolute(j);
     						jrGrideResult  = outjsResult.getRecord();
     						szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
     						int iGrideSort = Integer.parseInt(szGrideSort);
     		
     						if (!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
     						
     							if (iGrideSort == i) {
     								jsGrideResult.addRecord(outjsResult.getRecord());
     							}
     						}
     					}
     				}
     					
     			    // 적치 가능 check
     				JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
     				String szGRIDE = "9";
     				for (int i = 1; i <= jsGrideResult.size(); i++) {

     					jsGrideResult.absolute(i);
     					jrAbleResult  = jsGrideResult.getRecord();
     					
     					szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
     					szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
     					szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
     					szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

     					/*
     					 *  HFL2 3E19열 적치가능체크 패스 임시 적용 2021-03-31
     					 */
     					if(ydToLocGuide.substring(0,4).equals("3E19")){
     						sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp ;
     						commUtils.printLog(logId, "### HFL2 3E19 적치 : "+sRtnBedDan, "SL");
     						break;
     					}else{
     						//적치가능 check
         					JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);

         					String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
         					String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
         					if (LocAbleRtnMsg.length() > 0 ) {
         						szDBLogMsg = szDBLogMsg + LocAbleRtnMsg +"\r\n";
         					}
         					
         					
         					if (LocAbleRtn.equals("1")) {
         						szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
         						commUtils.printLog(logId, szLogMsg, "SL");				
         					    //적치가능 
         						sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp ;
         		    			break;
         					}
     					}
     					
     				}
     				if (sRtnBedDan.length() < 10) {
     					szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 사용자지정 TO위치 결정 실패 ";
     					commUtils.printLog(logId, szLogMsg, "SL");
     					if (sAPP005_YN.equals("Y")) {
     		    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
     		    			jrLog.setField("STOCK_ID"		, StockId);
     		    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
     		    			jrLog.setField("YD_GP"			, "3");
     		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
     		    			jrLog.setField("SCH_CONTENTS"	, "사용자 지정 대상코일선택실패:"+ StockId+" LOG :"+"\r\n" + szDBLogMsg);
     		    			
     		    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
     		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
     					}
     					
     					return YmConstant.RETN_CD_FAILURE;				
     				}
     				
					if (sAPP005_YN.equals("Y")) {
		    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
		    			jrLog.setField("STOCK_ID"		, StockId);
		    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
		    			jrLog.setField("YD_GP"			, "3");
		    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
		    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택:"+ StockId+"  선택위치:"+ sRtnBedDan + "  평점:" + szGRIDE +" LOG :"+"\r\n" + szDBLogMsg);
		    				    			
		    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
		    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
					}
     			}				
            }	 

			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
			jrSetLoc.setField("YD_EQP_ID", 		ydEqpId);	 
			jrSetLoc.setField("YD_SCH_CD", 		ydSchCd);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	ydUpWoLoc); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	ydUpWoLayer);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	ydWookId); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch  );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}      
  
    
	/**
	 * 설비 보급 작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocConveyor(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO위치결정:설비보급작업[BCoilSchSeEJB.procToLocConveyor] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));
		String szBAY_GP  			= commUtils.trim(jrCrnSch.getFieldString("YD_SCH_CD").substring(1, 2)); // 동		
		String sYD_TO_LOC_GUIDE 	= commUtils.trim(jrCrnSch.getFieldString("YD_TO_LOC_GUIDE"));   //사용자지정위치
		
		String sRtnBed           	= "";
		String sRtnBedDan           = "";
		
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			if ( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}

			String sAPP043 = YmComm.BCoilApplyYn("APP043","3","1");//출측 TAKE-IN
			
			commUtils.printLog(logId,"TOSQL", "SL");
			//  SPM 관련 내용 추가 CGS 
			if (szYD_SCH_CD.substring(2,7).equals("KE01U")) {         	
				if (szBAY_GP.equals("B")) {                               // #1 SPM 보급 (C,B)
					sRtnBed = "3BKE01";	                            
				} else if (szBAY_GP.equals("C")) {                        // #1 SPM 보급 (C,B)
					sRtnBed = "3CKE01";
				}	
			} else if (szYD_SCH_CD.substring(2,7).equals("KE02U")) {          	
				if (szBAY_GP.equals("D")) {                               // #2 SPM 보급 (D,E)
					sRtnBed = "3DKE01";
				} else if (szBAY_GP.equals("E")) {                        // #2 SPM 보급 (D,E)
					sRtnBed = "3EKE01"; //기존
//--------------------------------------------------------------------------------------------
					//SPM E동 보급
					String sAPP031   = YmComm.BCoilApplyYn("APP031","3","1");    
					String sTO_BD_YN = YmComm.BCoilApplyYn("APP031","3","2"); //밴딩커팅장 분기 적용여부
					
					if ("Y".equals(sAPP031)) {
						
						sRtnBed = "3EBD01"; // 밴딩커팅장(3EBD01)으로 이동
						
						if(szYD_UP_WO_LOC.startsWith(sRtnBed)) {
							//권상위치가 밴딩커팅장 일 경우 바로 설비로 보급한다.
							sRtnBed = "3EKE01"; // SPM2 보급위치
						}
						
						if ("Y".equals(sTO_BD_YN)) {
							/*
							SELECT WRAP_METHOD_OD
							  FROM(
							        SELECT NVL(CS.WRAP_METHOD_OD, NVL(BM.WRAP_METHOD_OD, 0)) AS WRAP_METHOD_OD 
							             , ROW_NUMBER() OVER(PARTITION BY CS.COIL_NO ORDER BY STEP_NO DESC) AS IDX
							          FROM TB_HR_C_SHEARWOWR CS
							             , TB_HR_B_MILLWR    BM
							         WHERE BM.COIL_NO = CS.COIL_NO(+)
							           AND BM.COIL_NO = :V_STOCK_ID
							      )
							 WHERE IDX = 1   
							 */
							JDTORecord jrParam = JDTORecordFactory.getInstance().create();
							jrParam.setField("STOCK_ID", szSTOCK_ID);
							JDTORecordSet jsOdWrapInfo = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getOdWrapCnt", logId, methodNm, "OD 밴딩 횟수 조회");
							
							if (jsOdWrapInfo.size() > 0) {
								int nOdWrapCnt = Integer.parseInt(commUtils.nvl(jsOdWrapInfo.getRecord(0).getFieldString("WRAP_METHOD_OD"), "0"));
								if (nOdWrapCnt <= 1) {
									commUtils.printLog(logId, "OD WRAP COUNT : "+nOdWrapCnt, "[INFO]");
									sRtnBed = "3EKE01"; // SPM2 보급위치
								}
							}
							
						}//if ("Y".equals(sTO_BD_YN))
					} // if ("Y".equals(sAPP031))
				}
			} else if (szYD_SCH_CD.substring(2,7).equals("KE02M")) { //SPM2 커팅후 보급
				sRtnBed = "3EKE01";
//--------------------------------------------------------------------------------------------				
			} else if (szYD_SCH_CD.substring(2,7).equals("KE03U")) {          	
				if (szBAY_GP.equals("B")) {                               // #1 SPM TAKE IN
					sRtnBed = "3BKE01";	                            
				} else if (szBAY_GP.equals("C")) {                        // #1 SPM TAKE IN
					sRtnBed = "3CKE01";
				}	
			} else if (szYD_SCH_CD.substring(2,7).equals("KE04U")) {    
				
				if ("Y".equals(sAPP043) && !"".equals(sYD_TO_LOC_GUIDE)) {
					sRtnBed = sYD_TO_LOC_GUIDE;  //출측작업시 사용자지정위치 세팅
				} else if (szBAY_GP.equals("D")) {                        // #2 SPM TAKE IN
					sRtnBed = "3DKE01";
				} else if (szBAY_GP.equals("E")) {                        // #2 SPM TAKE IN
					sRtnBed = "3EKE01";
				}
			} else if (szYD_SCH_CD.substring(2,7).equals("FE01U")) {     // HFL 보급 : CFLI
				if (szBAY_GP.equals("B")) {
					sRtnBed = "3BFE01";
				} else {
					sRtnBed = "3AFE01";
				}
			} else if (szYD_SCH_CD.substring(2,7).equals("FE03U")) {     // HFL TakeIn:CFTI
				sRtnBed = "3BFE01";
			}	
		
			commUtils.printLog(logId,"2"+ sRtnBed, "SL");
			
//			String sAPP029_YN = YmComm.BCoilApplyYn("APP029","3","1");   //SPM E동 보급위치 변경
			
//			if (sAPP029_YN.equals("Y")) {
				
			if (sRtnBed.length() == 6) {
				if (sRtnBed.equals("3EKE01")) { //E동 SPM2 보급, E동 SPM2 TAKE IN, SPM2 커팅후 보급 경우 3EKE01-07-01
					sRtnBedDan = sRtnBed + "07" + "01";
				} else if (sRtnBed.equals("3BKE01")) {  //B동 SPM1 보급,Take In 일경우 3BKE01-07-01 ** 
					//sRtnBedDan = sRtnBed + "01" + "01";
					sRtnBedDan = sRtnBed + "07" + "01";
				} else if (sRtnBed.equals("3BFE01")) {  //B동 HFL1 보급,Take In 일경우 3BFE01-01-01 ** 
					sRtnBedDan = sRtnBed + "01" + "01";
				} else {
					sRtnBedDan = sRtnBed + "00" + "01";
				}
			}
				
//			} else {
//				if (sRtnBed.length() == 6) {
//					sRtnBedDan = sRtnBed + "00" + "01";
//				}
//			}
			
			if ("Y".equals(sAPP043) && sRtnBed.equals("3EKD02")) { //SPM2 Take In 
				sRtnBedDan = sRtnBed + "01" + "01";
			}
			
			//밴드커팅장은 01번지
			if (sRtnBed.length() == 6 && sRtnBed.equals("3EBD01")) {
				sRtnBedDan = sRtnBed + "01" + "01";
			}
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
			jrSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
			jrSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	szYD_UP_WO_LAYER);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch  );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}      

    
	/**
	 * 주작업TO위치결정  -> 야드
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocPrimaryWork(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "주작업TO위치결정[BCoilSchSeEJB.procToLocPrimaryWork] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		

		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			if ( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			String sAPP005_YN = YmComm.BCoilApplyYn("APP005","3","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");
			
			JDTORecord jrToLocPrimary = JDTORecordFactory.getInstance().create();			
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"		, szSTOCK_ID);		//권상 STOCK
			jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);
			
			szLogMsg =  " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			JDTORecordSet outjsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	//----------------------------------------------------------------------------------------------------------------------
			//	일반적인 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearch", logId, methodNm, "동일한 적치가능한 베드 조회");
			
			if (outjsResult.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");

				if (sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
	    			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
	    			jrLog.setField("YD_GP"			, "3");
	    			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
	    			jrLog.setField("SCH_CONTENTS"	, "대상코일위치검색실패:"+ szSTOCK_ID+" LOG :"+"\r\n" );
	    			
	    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				return YmConstant.RETN_CD_FAILURE;
			}
			JDTORecord	jrResult	= JDTORecordFactory.getInstance().create();
	    	String szStackColGp 	= "";
			String szStackBedGp 	= "";
			String szStackLayerGp 	= "";	
			String szToPosGrade 	= "999";

			String sRtnBedDan 		= "";	
			String sRtnBedDanCheck 	= "";	
			int iToPosGrade 		= 999;
			int iToPosGradeCheck	= 999;

		    //----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord  jrSchSule = this.procSchSule(logId, methodNms, szSTOCK_ID, szYD_SCH_CD);
			String szDBLogMsg = "";
			String szGROUP_SEQ = "";
		    // 평점 CEHCK	
			for (int i = 1; i <= outjsResult.size(); i++) {
	
				outjsResult.absolute(i);
				jrResult  = outjsResult.getRecord();

				szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
				szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
				szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
				szGROUP_SEQ 	= commUtils.trim(jrResult.getFieldString("GROUP_SEQ"  ));
				
				
				//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
				jrResult.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID); 
				jrResult.setField("YD_SCH_CD"	 	, szYD_SCH_CD); 
				jrResult.setField("GROUP_SEQ"	 	, szGROUP_SEQ); 				
				jrSchSule.setField("SCH_LOG"	    , sAPP005_YN); 	
				
				jrToLocPrimary = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
				
				szToPosGrade    		= commUtils.trim(jrToLocPrimary.getFieldString("GRIDE"  ));
				String szToPosGradeMsg 	= commUtils.trim(jrToLocPrimary.getFieldString("GRADE_CONTENTS"));
				if (szToPosGradeMsg.length() > 0 ) {
					szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
				}
				
				outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
			}
			
		    // 평점 CEHCK	 SORT
			String szGrideSort  = "99";
			int igride          = 10; 
			JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
			
			String szSORT_WAY = ""; //Sorting 방식   구분자 
			
			String sAPPLY060_SORTNEW_YN = YmComm.BCoilApplyYn("APP060","3","SORTNEW_YN"); //컨베어 Line off 시  저장영역별검색순서로 Sorting 적용여부
			if("Y".equals(sAPPLY060_SORTNEW_YN)) {
				if("DC01LM".equals(szYD_SCH_CD.substring(2)) 
				|| "EC01LM".equals(szYD_SCH_CD.substring(2))
				|| "EC02LM".equals(szYD_SCH_CD.substring(2)) ) {
					
					//DC LineOff , EC LineOff 시 저장영역별 건색순서 에 등록된 순서 대로 Sorting 그 외는  기존 방식 Sorting
					szSORT_WAY = "NEW"; 
					
				} 
			}
			
			if("NEW".equals(szSORT_WAY)) {
				//새로운 Sort 방식 : 저장영겨별 검색순서에 등록된 순서 대로 Sorting (3D1507, 3D1506, 3D1505 ... 3D1501) 
				for (int j = 1; j <= outjsResult.size(); j++) {
					outjsResult.absolute(j);
					jrGrideResult  = outjsResult.getRecord();
					szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
	
					if (!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
					
						jsGrideResult.addRecord(outjsResult.getRecord());
					}
				}
				
			} else {
				//기존 Sort 방식 : 평접인 좋은 순으로 sorting (ex: 1,2,3..10) 
				for (int i = 1; i <= igride; i++) {
					for (int j = 1; j <= outjsResult.size(); j++) {
						outjsResult.absolute(j);
						jrGrideResult  = outjsResult.getRecord();
						szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
						int iGrideSort = Integer.parseInt(szGrideSort);
		
						if (!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
						
							if (iGrideSort == i) {
								jsGrideResult.addRecord(outjsResult.getRecord());
							}
						}
					}
				}
			}
				
		    // 적치 가능 check
			JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
			String szGRIDE = "9";
			for (int i = 1; i <= jsGrideResult.size(); i++) {

				jsGrideResult.absolute(i);
				jrAbleResult  = jsGrideResult.getRecord();
				
				szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
				szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
				szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
				szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

				//적치가능 check
				JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);

				String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				if (LocAbleRtnMsg.length() > 0 ) {
					szDBLogMsg = szDBLogMsg + LocAbleRtnMsg +"\r\n";
				}
				
				
				if (LocAbleRtn.equals("1")) {
					szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
					commUtils.printLog(logId, szLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp;
	    			break;
				}				
				
			}
			if (sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				if (sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
	    			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
	    			jrLog.setField("YD_GP"			, "3");
	    			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
	    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택실패:"+ szSTOCK_ID+" LOG :"+"\r\n" + szDBLogMsg);
	    			
	    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				return YmConstant.RETN_CD_FAILURE;				
			}
			
			if (sAPP005_YN.equals("Y")) {
    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
    			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
    			jrLog.setField("YD_GP"			, "3");
    			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택:"+ szSTOCK_ID+"  선택위치:"+ sRtnBedDan +"  평점:" + szGRIDE +" LOG :"+"\r\n" + szDBLogMsg);
    			
    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
			}
			
			//----------------------------------------------------------------------------------------------------------------------
	    	// To위치 크레인 에 update 
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
			jrSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
			jrSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	szYD_UP_WO_LAYER);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch  );
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}      

    
	/**
	 * 주작업TO위치결정  -> 야드
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocPrimaryWorkMulti(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "주작업TO위치결정[BCoilSchSeEJB.procToLocPrimaryWorkMulti] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));			//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));		

		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			if ( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			String sAPP005_YN = YmComm.BCoilApplyYn("APP005","3","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");
			
			JDTORecord jrToLocPrimary = JDTORecordFactory.getInstance().create();			
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"		, szSTOCK_ID);		//권상 STOCK
			jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);	//크레인 스케쥴 ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);
			
			szLogMsg =  " TOSQL:["+szYD_CRN_SCH_ID+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			JDTORecordSet outjsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	//----------------------------------------------------------------------------------------------------------------------
			//	일반적인 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			String sINQ_FLAG = "N"; // 검색순서 : 1.차공정(N) 2.진도코드(P) 3.일반재(G) 99. 종료(END)
			boolean bNEXT_YN = true; // 다음저장위치검색 수행구분
			
			JDTORecord	jrResult	= JDTORecordFactory.getInstance().create();
	    	String szStackColGp 	= "";
			String szStackBedGp 	= "";
			String szStackLayerGp 	= "";	
			String szToPosGrade 	= "999";

			String sRtnBedDan 		= "";	
			
			String szDBLogMsg = "";
			String szGRIDE = "9";
			
			do {
				if (bNEXT_YN && "N".equals(sINQ_FLAG)) { //차공정으로 검색
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchNextProc", logId, methodNm, "차공정행선");
					if (outjsResult.size() > 0) {
						bNEXT_YN = false;
					} else {
						sINQ_FLAG = "P";	bNEXT_YN = true;
						continue;
					}
				}
				if (bNEXT_YN && "P".equals(sINQ_FLAG)) { //진도코드로 검색
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearchProgCd", logId, methodNm, "진도코드행선");
					if (outjsResult.size() > 0) {
						bNEXT_YN = false;
					} else {
						sINQ_FLAG = "G";	bNEXT_YN = true;
						continue;
					}
				}
				if (bNEXT_YN && "G".equals(sINQ_FLAG)) { //진도코드로 검색
					outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearch", logId, methodNm, "동일한 적치가능한 베드 조회");
					sINQ_FLAG = "FAIL";  
				}

			    /*****************************************
			     * 적재가능 위치 SCH RULE 검색
			     *****************************************/
				JDTORecord jrSchSule = this.procSchSule(logId, methodNms, szSTOCK_ID, szYD_SCH_CD);
				String szGROUP_SEQ = "";
			    // 평점 CEHCK	
				for (int i = 1; i <= outjsResult.size(); i++) {
		
					outjsResult.absolute(i);
					jrResult  = outjsResult.getRecord();

					szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
					szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
					szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
					szGROUP_SEQ 	= commUtils.trim(jrResult.getFieldString("GROUP_SEQ"  ));
					
					
					//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
					jrResult.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID); 
					jrResult.setField("YD_SCH_CD"	 	, szYD_SCH_CD); 
					jrResult.setField("GROUP_SEQ"	 	, szGROUP_SEQ); 				
					jrSchSule.setField("SCH_LOG"	    , sAPP005_YN); 	
					
					jrToLocPrimary = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
					
					szToPosGrade    		= commUtils.trim(jrToLocPrimary.getFieldString("GRIDE"  ));
					String szToPosGradeMsg 	= commUtils.trim(jrToLocPrimary.getFieldString("GRADE_CONTENTS"));
					if (szToPosGradeMsg.length() > 0) {
						szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
					}
					
					outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
				}
				
			    // 평점 CEHCK	 SORT
				String szGrideSort  = "99";
				int igride          = 10; 
				JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
				JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
				
				for (int i = 1; i <= igride; i++) {
					for (int j = 1; j <= outjsResult.size(); j++) {
						outjsResult.absolute(j);
						jrGrideResult  = outjsResult.getRecord();
						szGrideSort    = commUtils.trim(jrGrideResult.getFieldString("GRIDE"));
						int iGrideSort = Integer.parseInt(szGrideSort);
		
						if (!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
						
							if (iGrideSort == i) {
								jsGrideResult.addRecord(outjsResult.getRecord());
							}
						}
					}
				}
					
			    // 적치 가능 check
				JDTORecord jrAbleResult	= JDTORecordFactory.getInstance().create();
				
				for (int i = 1; i <= jsGrideResult.size(); i++) {

					jsGrideResult.absolute(i);
					jrAbleResult  = jsGrideResult.getRecord();
					
					szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
					szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
					szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
					szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

					//적치가능 check
					JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);

					String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
					String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
					if (LocAbleRtnMsg.length() > 0) {
						szDBLogMsg = szDBLogMsg + LocAbleRtnMsg +"\r\n";
					}
					
					if (LocAbleRtn.equals("1")) {
						szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
						commUtils.printLog(logId, szLogMsg, "SL");				
					    //적치가능 
						sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp;
						sINQ_FLAG = "SUCCESS";//TO위치 검색 완료
		    			break;
					}				
				}
				
				if (sRtnBedDan.length() < 10) {
					szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 TO위치 결정 실패 ";
					commUtils.printLog(logId, szLogMsg, "SL");

	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
	    			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
	    			jrLog.setField("YD_GP"			, "3");
	    			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
	    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택실패:"+ szSTOCK_ID+" LOG :"+"\r\n" + szDBLogMsg);
	    			
	    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });

					//return YmConstant.RETN_CD_FAILURE;
	    			bNEXT_YN = true;
				}
				
			} while (!("FAIL".equals(sINQ_FLAG) || "SUCCESS".equals(sINQ_FLAG)));
			

			if ("FAIL".equals(sINQ_FLAG)) {
				commUtils.printLog(logId, methodNm+ "적치가능한 베드 검색 실패 ", "SL");

    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
    			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
    			jrLog.setField("YD_GP"			, "3");
    			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
    			jrLog.setField("SCH_CONTENTS"	, "대상코일위치검색실패:"+ szSTOCK_ID+" LOG :"+"\r\n" );
    			
    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				
				return YmConstant.RETN_CD_FAILURE;
			}
			
			// TO위치 결정 Log Insert
			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
			jrLog.setField("STOCK_ID"		, szSTOCK_ID);
			jrLog.setField("YD_CRN_SCH_ID"	, szYD_CRN_SCH_ID);
			jrLog.setField("YD_GP"			, "3");
			jrLog.setField("YD_SCH_CD"		, szYD_SCH_CD);
			jrLog.setField("SCH_CONTENTS"	, "대상코일선택:"+ szSTOCK_ID+"  선택위치:"+ sRtnBedDan +"  평점:" + szGRIDE +" LOG :"+"\r\n" + szDBLogMsg);
			
			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
						
			/*******************************************
			 * To위치 크레인 에 update
			 *******************************************/
			JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
			jrSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
			jrSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
			jrSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
			jrSetLoc.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
			jrSetLoc.setField("YD_UP_WO_LAYER",	szYD_UP_WO_LAYER);	 
			jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
			jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
			jrSetLoc.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID); 
				
			this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch);
			
			

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}
    
    
	/**
	 * TO위치 UPDATE
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */	
	public String procUpdateLoc(String logId, String methodNms, JDTORecord jrSetLoc, JDTORecord jrCrnWrk) throws JDTOException {
		String methodNm = "TO위치 UPDATE[BCoilSchSeEJB.procUpdateLoc] < " + methodNms;
		String LocalmethodNm = "TO위치 UPDATE[BCoilSchSeEJB.procUpdateLoc]" ;
		String szLogMsg					= null;
		JDTORecord		recInBed		= null;
		
		int intRtnVal					= 0;
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
		try {

			int intYD_EQP_WRK_SH    	= commUtils.paraRecChkNullInt(jrCrnWrk,"SH_CNT");				//크레인작업재료 총매수
			int intYD_EQP_WRK_WT    	= commUtils.paraRecChkNullInt(jrCrnWrk,"SUM_MTL_WT");			//크레인작업재료 총중량
			double dblYD_EQP_WRK_T     	= commUtils.paraRecChkNullDouble(jrCrnWrk,"SUM_MTL_T");			//크레인작업재료 총높이
			String szYD_EQP_WRK_MAX_W 	= commUtils.trim(jrCrnWrk.getFieldString("MAX_MTL_W"  ));		//크레인작업재료 중 최대 폭
			String szYD_EQP_WRK_MAX_L 	= commUtils.trim(jrCrnWrk.getFieldString("MAX_MTL_L"  ));		//크레인작업재료 중 최대 길이
			String szMODIFIER 			= commUtils.trim(jrCrnWrk.getFieldString("MODIFIER"  ));		//MODIFIER
			String szSTOCK_ID 	   		= commUtils.trim(jrCrnWrk.getFieldString("STOCK_ID"  ));
			
			String szYD_CRN_SCH_ID  	= commUtils.trim(jrSetLoc.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
			String szYD_SCH_CD  		= commUtils.trim(jrSetLoc.getFieldString("YD_SCH_CD"  ));	
			String szYD_UP_WO_LOC		= commUtils.trim(jrSetLoc.getFieldString("YD_UP_WO_LOC"  ));			
			String szYD_UP_WO_LAYER		= commUtils.trim(jrSetLoc.getFieldString("YD_UP_WO_LAYER"  ));			
			String szYD_DN_WO_LOC		= commUtils.trim(jrSetLoc.getFieldString("YD_DN_WO_LOC"  ));			
			String szYD_DN_WO_LAYER		= commUtils.trim(jrSetLoc.getFieldString("YD_DN_WO_LAYER"  ));			
			String szYD_RCPT_PLN_STR_LOC= commUtils.trim(jrSetLoc.getFieldString("YD_RCPT_PLN_STR_LOC"  )); // 입고예정위치			
			String szYD_WBOOK_ID  		= commUtils.trim(jrSetLoc.getFieldString("YD_WBOOK_ID"  ));		//작업예약
			if (szYD_DN_WO_LOC.equals("")) {
				return YmConstant.RETN_CD_FAILURE;
			}
			
			commUtils.printParam(logId, jrSetLoc);
			//----------------------------------------------------------------------------------------------------------------------
			// 권하지시위치 수정
			//----------------------------------------------------------------------------------------------------------------------
			//SPM2 커팅후보급시 권상위치 밴딩커팅장으로 고정
			if (szYD_SCH_CD.equals("3EKE02MM")) {
				szYD_UP_WO_LOC  = "3EBD0101";
				szYD_UP_WO_LAYER = "01";
			} 
			
			JDTORecordSet jsLayerUpXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("STACK_COL_GP", 			szYD_UP_WO_LOC.substring(0, 6)); //권상지시위치
			recInBed.setField("STACK_BED_GP", 			szYD_UP_WO_LOC.substring(6));	 //권상지시위치
			recInBed.setField("STACK_LAYER_GP", 		szYD_UP_WO_LAYER);
				
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed 
			SELECT A.STACK_COL_GP 
			     , A.STACK_BED_GP 
			     , A.STACK_LAYER_X_AXIS
			     , A.STACK_LAYER_Y_AXIS
			     , A.STACK_LAYER_Z_AXIS
			     , B.YD_STK_BED_XAXIS_TOL 
			     , B.YD_STK_BED_YAXIS_TOL 
			     , B.YD_STK_BED_ZAXIS_TOL 
			     , (SELECT ROTATION_ANGLE FROM TB_YM_STACKCOL WHERE STACK_COL_GP = A.STACK_COL_GP) AS ROTATION_ANGLE
			  FROM TB_YM_STACKLAYER A
			     , TB_YM_STACKER    B
			 WHERE A.STACK_COL_GP = B.STACK_COL_GP
			   AND A.STACK_BED_GP = B.STACK_BED_GP
			   AND A.STACK_COL_GP = :V_STACK_COL_GP
			   AND A.STACK_BED_GP = :V_STACK_BED_GP
			   AND A.STACK_LAYER_GP = :V_STACK_LAYER_GP
			   AND A.DEL_YN ='N'
			   AND B.DEL_YN ='N'
			*/  
			jsLayerUpXy = commDao.select(recInBed, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed", logId, methodNm, "권상 BED 좌표 조회");
			if (jsLayerUpXy.size() <= 0) {
				szLogMsg =  "확인:"+szSTOCK_ID+"권상 Layer 좌표 조회 검색 실패.";
				commUtils.printLog(logId, szLogMsg, "SL");
				
			}
			jsLayerUpXy.first();
			JDTORecord jrUpLayerXy = jsLayerUpXy.getRecord();
	
			JDTORecordSet jsDnLayerXy = JDTORecordFactory.getInstance().createRecordSet("");
			recInBed= JDTORecordFactory.getInstance().create();
			recInBed.setField("STACK_COL_GP", 			szYD_DN_WO_LOC.substring(0, 6));//권하지시위치
			recInBed.setField("STACK_BED_GP", 			szYD_DN_WO_LOC.substring(6));	//권하지시위치
			recInBed.setField("STACK_LAYER_GP", 		szYD_DN_WO_LAYER);				
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed 
			SELECT A.STACK_COL_GP 
			     , A.STACK_BED_GP 
			     , A.STACK_LAYER_X_AXIS
			     , A.STACK_LAYER_Y_AXIS
			     , A.STACK_LAYER_Z_AXIS
			     , B.YD_STK_BED_XAXIS_TOL 
			     , B.YD_STK_BED_YAXIS_TOL 
			     , B.YD_STK_BED_ZAXIS_TOL 
			     , (SELECT ROTATION_ANGLE FROM TB_YM_STACKCOL WHERE STACK_COL_GP = A.STACK_COL_GP) AS ROTATION_ANGLE
			     , A.STACK_LAYER_STAT
			  FROM TB_YM_STACKLAYER A
			     , TB_YM_STACKER    B
			 WHERE A.STACK_COL_GP = B.STACK_COL_GP
			   AND A.STACK_BED_GP = B.STACK_BED_GP
			   AND A.STACK_COL_GP = :V_STACK_COL_GP
			   AND A.STACK_BED_GP = :V_STACK_BED_GP
			   AND A.STACK_LAYER_GP = :V_STACK_LAYER_GP
			   AND A.DEL_YN ='N'
			   AND B.DEL_YN ='N'
			*/  
			jsDnLayerXy = commDao.select(recInBed, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYdStkLayerBybed", logId, methodNm, "권하 BED 좌표 조회");
			if (jsDnLayerXy.size() <= 0) {
				szLogMsg = LocalmethodNm +" 권하 Layer 좌표 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
			}
			
			String sAPP000_YN = YmComm.BCoilApplyYn("APP000","3","2");   //트랜잭션 분리 (저장위치)
			if ("Y".equals(sAPP000_YN)) {
				if ("D".equals(jsDnLayerXy.getRecord(0).getFieldString("STACK_LAYER_STAT"))) {
					szLogMsg = LocalmethodNm +" 권하 Layer 좌표 검색 실패(중복) ";
					commUtils.printLog(logId, szLogMsg, "SL");
					return YmConstant.RETN_CD_FAILURE;
				}
			}
			
			jsDnLayerXy.first();
			JDTORecord jrDnLayerXy = jsDnLayerXy.getRecord();
			
			JDTORecord jrUpCrnSch = JDTORecordFactory.getInstance().create();
			
			//----------------------------------------------------------------------------------------------------------------------
			// SPM보급 스케줄일경우 해당코일이 재보급재인경우 권하각도를 180도 회전해야 한다
			//----------------------------------------------------------------------------------------------------------------------
			String upRotationAngle = commUtils.trim(jrUpLayerXy.getFieldString("ROTATION_ANGLE"  ));
			String dnRotationAngle = commUtils.trim(jrDnLayerXy.getFieldString("ROTATION_ANGLE"  ));
			
			String sAPP060_YN = YmComm.BCoilApplyYn("APP060","3","SPM_ROT");   //APP060	3	SPM_ROT	SPM재보급재 180도 권하 적용
			if (sAPP060_YN.equals("Y")) {
				//C동 SPM 보급스케줄일경우 해당코일 이적이력조회한다
				if(("3CKE01UM".equals(szYD_SCH_CD) && "3CKE01".equals(szYD_DN_WO_LOC.substring(0, 6)))    
				|| ("3BKE01UM".equals(szYD_SCH_CD) && "3BKE01".equals(szYD_DN_WO_LOC.substring(0, 6)))
				){
					
					szLogMsg = " [ "+szSTOCK_ID+" ] 코일 스케줄 코드 : "+szYD_SCH_CD+" 권하위치 : "+szYD_DN_WO_LOC.substring(0, 6);
					commUtils.printLog(logId, szLogMsg, "SL");
					
					JDTORecord jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("STOCK_ID", szSTOCK_ID);
					JDTORecordSet jsWrslt = JDTORecordFactory.getInstance().createRecordSet("");
					JDTORecord jrWrslt = JDTORecordFactory.getInstance().create();
					
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockHistory
					 SELECT STOCK_ID
					     , SCH_ID
					     , YD_SCH_CD AS YD_SCH_CD
					     , CRANE_WRSLT_UP_LOC  AS FR_WR_LOC
					     , CRANE_WRSLT_PUT_LOC AS TO_WR_LOC
					     , SUBSTR(EQUIP_GP, 5, 2)  || '호기' AS YD_WRK_CRN
					     , EQUIP_GP AS EQUIP_GP
					     , YD_GP AS YD_GP
					     , CRANE_WRSLT_ID AS CRANE_WRSLT_ID
					     , YD_AID_WRK_YN AS YD_AID_WRK_YN
					     , YD_UP_WRK_MODE2 AS YD_UP_WRK_MODE2
					     , YD_DN_WRK_MODE2 AS YD_DN_WRK_MODE2
					  FROM TB_YM_WRSLT
					 WHERE STOCK_ID = :V_STOCK_ID
					   AND DEL_YN = 'N'
					   */
					jsWrslt = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getYmStockHistory", logId, methodNm, "재료 작업실적 조회");
					
					for (int Loop_i = 1; Loop_i <= jsWrslt.size(); Loop_i++) {
						
						jsWrslt.absolute(Loop_i);
						jrWrslt = JDTORecordFactory.getInstance().create();
						jrWrslt.setRecord( jsWrslt.getRecord() );
						
						String ydSchCd = commUtils.trim(jrWrslt.getFieldString("YD_SCH_CD"));
						
						//SPM 추출 이력을 가지고 있는 코일의 경우 권하시 180도 회전후 권하해야 한다 20210224
						if("3AKD01LM".equals(ydSchCd)){
							dnRotationAngle = "2";
							
							szLogMsg = " [ "+szSTOCK_ID+" ] 코일 스케줄 코드 : "+ydSchCd+" 권하각도 : "+dnRotationAngle;
							commUtils.printLog(logId, szLogMsg, "SL");
							
						}
					}//for
				}//if
			}// if sAPP060_YN
			
			
			String sAPP060_ZZ006_YN = YmComm.BCoilApplyYn("APP060","3","ZZ006_YN");   //APP060	3	ZZ006_YN	차량동간이적 통로간 이적시 180도 권상 적용			
			
			if (sAPP060_ZZ006_YN.equals("Y")) {
				//차량동간이적 하차 스케줄일 경우 통로간 동간이적인지 확인한다.
				if( "PT03LM".equals(szYD_SCH_CD.substring(2)) || "PT07LM".equals(szYD_SCH_CD.substring(2)) ) {
					
					JDTORecord jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("STOCK_ID", szSTOCK_ID);
					JDTORecordSet jsStock = JDTORecordFactory.getInstance().createRecordSet("");
					
					jsStock = commDao.select3(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getStockInfo_PIDEV", logId, methodNm, "STOCK 에서 통로간 동간이적인지 확인");
					
					if(jsStock.size() > 0) {
					
						String sCTS_RELAY_SADDLE = commUtils.trim(jsStock.getRecord(0).getFieldString("CTS_RELAY_SADDLE"));
						
						if("1".equals(sCTS_RELAY_SADDLE) || "3".equals(sCTS_RELAY_SADDLE)) {
							//1:좌(L)->우(R ),3:우(R)->좌(L) 즉 1통로에서 2통로 , 2통로에서 1통로로 차량동간이적 한 경우
							//권상할 때 180도 회전 후 권상
							upRotationAngle = "2"; 
						}
					}
				}
			} //if sAPP060_ZZ006_YN
			
			jrUpCrnSch.setField("YD_CRN_SCH_ID", 			szYD_CRN_SCH_ID);										//크레인스케줄ID
			
			//권상정보   					
			jrUpCrnSch.setField("YD_UP_WO_LOC", 			szYD_UP_WO_LOC);										//권상지시위치
			jrUpCrnSch.setField("YD_UP_WO_LAYER", 			szYD_UP_WO_LAYER);										//권상지시단
			jrUpCrnSch.setField("YD_UP_STK_COL_GP", 		szYD_UP_WO_LOC.substring(0, 6));						//권상지시위치 - 적치열
			jrUpCrnSch.setField("YD_UP_STK_BED_NO", 		szYD_UP_WO_LOC.substring(6));							//권상지시위치 - 적치베드
			jrUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("STACK_LAYER_X_AXIS"  ))) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("STACK_LAYER_Y_AXIS"  ))) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  		commUtils.trim(jrUpLayerXy.getFieldString("STACK_LAYER_Z_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS1",  	"" ) ;
			jrUpCrnSch.setField("YD_UP_WO_LOC_YAXIS2",  	"" ) ;
			jrUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	commUtils.trim(jrUpLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("UP_ROTATION_ANGLE",  		upRotationAngle ) ;
			//권하정보   					
			jrUpCrnSch.setField("YD_DN_WO_LOC", 			szYD_DN_WO_LOC);										//권하지시위치
			jrUpCrnSch.setField("YD_DN_WO_LAYER", 			szYD_DN_WO_LAYER);										//권하지시단
			jrUpCrnSch.setField("YD_DN_STK_COL_GP", 		szYD_DN_WO_LOC.substring(0, 6));						//권하지시위치 - 적치열
			jrUpCrnSch.setField("YD_DN_STK_BED_NO", 		szYD_DN_WO_LOC.substring(6));							//권하지시위치 - 적치베드
			
			
			jrUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("STACK_LAYER_X_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("STACK_LAYER_Y_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  		commUtils.trim(jrDnLayerXy.getFieldString("STACK_LAYER_Z_AXIS"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS1",  	"" ) ;
			jrUpCrnSch.setField("YD_DN_WO_LOC_YAXIS2",  	"" ) ;
			jrUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	commUtils.trim(jrDnLayerXy.getFieldString("YD_STK_BED_ZAXIS_TOL"  )) ) ;
			jrUpCrnSch.setField("DOWN_ROTATION_ANGLE",  	dnRotationAngle ) ;
	
	
			//기타   					
			jrUpCrnSch.setField("YD_EQP_WRK_SH", 			String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
			jrUpCrnSch.setField("YD_EQP_WRK_WT", 			String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
			jrUpCrnSch.setField("YD_EQP_WRK_T", 			String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_W", 		szYD_EQP_WRK_MAX_W);									//크레인작업재료 중 최대 폭
			jrUpCrnSch.setField("YD_EQP_WRK_MAX_L", 		szYD_EQP_WRK_MAX_L);									//크레인작업재료 중 최대 길이
			jrUpCrnSch.setField("MODIFIER", 				szMODIFIER);
			
			intRtnVal = commDao.update(jrUpCrnSch, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdCrnWrkSidedelyn", logId, methodNm, "크레인스케쥴 갱신");
			
			if (intRtnVal <= 0) {
				szLogMsg =  "확인:"+szSTOCK_ID+"권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단[" +szYD_DN_WO_LAYER +" ]을 크레인스케줄에 수정 중 ERROR 발생";

				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}		
			


			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setField("STACK_COL_GP", 	szYD_DN_WO_LOC.substring(0, 6));
			jrParam.setField("STACK_BED_GP", 	szYD_DN_WO_LOC.substring(6));
			jrParam.setField("STACK_LAYER_GP", 	commUtils.stringPlusInt(szYD_DN_WO_LAYER,0));
			jrParam.setField("STOCK_ID",       	szSTOCK_ID);
			jrParam.setField("STACK_LAYER_STAT", "D");
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrYdStkColBedGp 
			UPDATE TB_YM_STACKLAYER            
			   SET MOD_DDTT     = SYSDATE             
			     , MODIFIER     = :V_MODIFIER             
			     , STACK_LAYER_ACTIVE_STAT  = NVL(:V_STACK_LAYER_ACTIVE_STAT,STACK_LAYER_ACTIVE_STAT)
			     , STACK_LAYER_STAT         = NVL(:V_STACK_LAYER_STAT,STACK_LAYER_STAT)
			     , STOCK_ID                 = NVL(:V_STOCK_ID , STOCK_ID)
			 WHERE STACK_COL_GP     = :V_STACK_COL_GP
			   AND STACK_BED_GP     = :V_STACK_BED_GP
			   AND STACK_LAYER_GP   = :V_STACK_LAYER_GP
  
		    */  
			if ("Y".equals(sAPP000_YN)) {
				commUtils.printLog(logId, "TB_YM_STACKLAYER 권하위치 수정", "[INFO]");
				if (YmComm.execQueryId(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrYdStkColBedGp")) {
					intRtnVal = 1;
				} else {
					intRtnVal = 0;
				}
			} else {
				intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YM_STKLYR 갱신");
			}
		
			//저장품에 등록할 위치
			if (intRtnVal <= 0) {
				commUtils.printLog(logId,  "확인:"+szSTOCK_ID+"권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"] 활성화중 ERROR 발생", "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
		
			szLogMsg =LocalmethodNm +" 크레인스케쥴 ID["+szYD_CRN_SCH_ID+"] TO위치결정>>>>>>>> 권하지시위치["+szYD_DN_WO_LOC+"], 권하지시단["+szYD_DN_WO_LAYER+"]" ;
			commUtils.printLog(logId, szLogMsg, "SL");

			commUtils.printLog(logId, methodNm, "S-");

		//----------------------------------------------------------------------------------------------------------------------
    	
		//----------------------------------------------------------------------------------------------------------------------
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}		
		return YmConstant.RETN_CD_SUCCESS;
	}    
       
    
	/**
	 *      [A] 오퍼레이션명 : 평점 및 적치 가능 분석
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procGradeLocAble( String logId, String methodNms, JDTORecord jrResult,JDTORecord jrSchRule) throws JDTOException {
    	String methodNm = "평점분석[BCoilSchSeEJB.procGradeLocAble] < " + methodNms;
    	String szLogMsg			= null;
    	String szGRIDE  		= "99";

    	String szSTOCK_ID 		= commUtils.trim(jrResult.getFieldString("STOCK_ID"  ));
    	String szSTACK_COL_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
		String szSTACK_BED_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
		String szSTACK_LAYER_GP	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
		
		String szLEFT_STOCK_ID 	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_STOCK_ID"  ));	//2단일 경우 좌측
		String szLEFT_BED  		= commUtils.trim(jrResult.getFieldString("TAG_LEFT_BED"  ));		//2단일 경우 좌측
		String szLEFT_LAYER    	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_LAYER"  ));		//2단일 경우 좌측
		
		String szRIGHT_STOCK_ID	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_STOCK_ID"  ));	//2단일 경우 우측		
		String szRIGHT_BED	   	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_BED"  ));		//2단일 경우 우측
		String szRIGHT_LAYER    = commUtils.trim(jrResult.getFieldString("TAG_RIGHT_LAYER"  ));		//2단일 경우 우측

		String szYD_CRN_SCH_ID  = commUtils.trim(jrResult.getFieldString("YD_CRN_SCH_ID"  ));		//크레인 스케쥴ID
		String szYD_SCH_CD      = commUtils.trim(jrResult.getFieldString("YD_SCH_CD"  ));		    //크레인 스케쥴코드
		
		String sAPP005_YN       = commUtils.trim(jrSchRule.getFieldString("SCH_LOG"  ));		    //스케줄 LOG여부
		
		commUtils.printLog(logId, methodNm, "S+");
		
		commUtils.printLog(logId, "대상코일위치:"+ szSTACK_COL_GP+szSTACK_BED_GP+szSTACK_LAYER_GP, "SL");
		commUtils.printLog(logId, "대상코일번호:"+ szSTOCK_ID + "좌측코일번호:"+ szLEFT_STOCK_ID+ "우측코일번호:"+ szRIGHT_STOCK_ID, "SL");
		
		JDTORecord jrLocAbleRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrGradeAnalysisRtn = JDTORecordFactory.getInstance().create();
		try {
						
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, szSTOCK_ID); 
			jrTemp.setField("STACK_COL_GP"		, szSTACK_COL_GP); 
			jrTemp.setField("STACK_BED_GP"		, szSTACK_BED_GP); 
			jrTemp.setField("STACK_LAYER_GP"	, szSTACK_LAYER_GP); 
			jrTemp.setField("LEFT_STOCK_ID"		, szLEFT_STOCK_ID);	 
			jrTemp.setField("RIGHT_STOCK_ID"	, szRIGHT_STOCK_ID);	 
			jrTemp.setField("TAG_STACK_LAYER_GP", szSTACK_LAYER_GP);	 
				
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGride
			WITH SPEC_TABLE AS (
			    -- 특정규격CHECK 
			    SELECT DTL_ITM1 AS STACK_RULE_NAME 
			      FROM USRYMA.TB_YM_RULE
			     WHERE REPR_CD_GP = 'YM004'
			       AND DEL_YN = 'N'
			),
			TEMP_DATA AS 
			(
			SELECT C.COIL_NO          AS C_COIL_NO
			     , C.CURR_PROG_CD     AS C_PROG_CD    -- 진도코드
			     , C.NEXT_PROC        AS C_NEXT_PROC  -- 차공정
			     , C.HR_SPEC_ABBSYM   AS C_SPEC_ABBSYM-- 규격약호
			     , C.ORD_NO           AS C_ORD_NO     -- 주문번호
			     , C.ORD_DTL          AS C_ORD_DTL    -- 주문행번
			     , C.RECEIPT_DATE     AS C_RECEIPT_DT -- 입고일자
			     , C.MILL_INI_DATE    AS C_MILL_DT    -- 압연일시
			     , C.DEMANDER_CD      AS C_DEMANDER_CD-- 수요가코드 
			     , C.COIL_T           AS C_THICK      -- 두께
			     , C.COIL_W           AS C_WIDTH      -- 폭
			     , C.COIL_WT          AS C_WEIGTH     -- 중량 
			     , C.COIL_OUTDIA      AS C_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=C.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(C.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - C.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS C_JJANG_GP  --짱구여부    
			     , L.COIL_NO          AS L_COIL_NO
			     , L.CURR_PROG_CD     AS L_PROG_CD    -- 진도코드
			     , L.NEXT_PROC        AS L_NEXT_PROC  -- 차공정
			     , L.HR_SPEC_ABBSYM   AS L_SPEC_ABBSYM-- 규격약호
			     , L.ORD_NO           AS L_ORD_NO     -- 주문번호
			     , L.ORD_DTL          AS L_ORD_DTL    -- 주문행번
			     , L.RECEIPT_DATE     AS L_RECEIPT_DT -- 입고일자
			     , L.MILL_INI_DATE    AS L_MILL_DT    -- 압연일시
			     , L.DEMANDER_CD      AS L_DEMANDER_CD-- 수요가코드 
			     , L.COIL_T           AS L_THICK      -- 두께
			     , L.COIL_W           AS L_WIDTH      -- 폭
			     , L.COIL_WT          AS L_WEIGTH     -- 중량 
			     , L.COIL_OUTDIA      AS L_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=L.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(L.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - L.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS L_JJANG_GP  --짱구여부    
			     , R.COIL_NO          AS R_COIL_NO
			     , R.CURR_PROG_CD     AS R_PROG_CD    -- 진도코드
			     , R.NEXT_PROC        AS R_NEXT_PROC  -- 차공정
			     , R.HR_SPEC_ABBSYM   AS R_SPEC_ABBSYM-- 규격약호
			     , R.ORD_NO           AS R_ORD_NO     -- 주문번호
			     , R.ORD_DTL          AS R_ORD_DTL    -- 주문행번
			     , R.RECEIPT_DATE     AS R_RECEIPT_DT -- 입고일자
			     , R.MILL_INI_DATE    AS R_MILL_DT    -- 압연일시
			     , R.DEMANDER_CD      AS R_DEMANDER_CD-- 수요가코드 
			     , R.COIL_T           AS R_THICK      -- 두께
			     , R.COIL_W           AS R_WIDTH      -- 폭
			     , R.COIL_WT          AS R_WEIGTH     -- 중량 
			     , R.COIL_OUTDIA      AS R_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=R.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(R.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - R.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS R_JJANG_GP  --짱구여부
			  FROM (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_STOCK_ID            ) C        --대상코일
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_LEFT_STOCK_ID       ) L  --하단LEFT
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A 
			         WHERE COIL_NO = :V_RIGHT_STOCK_ID      ) R  --하단RIGHT
			 WHERE C.T_ROW = L.T_ROW(+)          
			   AND C.T_ROW = R.T_ROW(+)      
			)
			SELECT CASE  -- 1단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8') 
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < 180  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < 180 
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < 200 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < 200
			            THEN CASE WHEN C_PROG_CD = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌우) 진도코드
			                      WHEN C_PROG_CD = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
			                      WHEN C_PROG_CD = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
			                      WHEN L_COIL_NO IS NULL                               THEN '3' --좌 공BED
			                      WHEN R_COIL_NO IS NULL                               THEN '3' --우 공BED
			                      ELSE '9' END 
			             -- 2단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN L_JJANG_GP = 'Y'  OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌하단우하단) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
			                      ELSE '8' END 
			            -- 1단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD     IN ('F','G','H','J','K','L','M','5','6','7','8')   
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < 180  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < 180 
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < 200 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < 200
			             
			            THEN CASE WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우축 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우측 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '3' --좌측   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '3' --우측   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우측 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '5' --우측   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '5' --좌측   동일 주문번호 
			                      WHEN L_COIL_NO IS NULL                               THEN '6' --좌측 공BED
			                      WHEN R_COIL_NO IS NULL                               THEN '6' --우측 공BED
			                      ELSE '9' END 
			            -- 2단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			            THEN CASE WHEN L_JJANG_GP = 'Y' OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우하단 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우하단 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '3' --좌하단   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '3' --우하단   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우하단 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '5' --우하단   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '5' --좌하단   동일 주문번호 
			                      ELSE '8' END 
			                       
			             ELSE '9' END GRIDE           
			     , C.*        
			  FROM TEMP_DATA C
			*/
			JDTORecordSet jsCommResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGride", logId, methodNm, "코일공통 평점 조회");
			if (jsCommResult.size() <= 0) {
				szLogMsg = methodNm+ "코일공통  평점 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
    			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS"	, "코일공통  평점 검색 실패 ");
				return jrGradeAnalysisRtn;
			}
			
			JDTORecord jrCommResult = JDTORecordFactory.getInstance().create();
			
			for (int i = 1; i <= jsCommResult.size(); i++) {

				jsCommResult.absolute(i);
				jrCommResult  = jsCommResult.getRecord();
				
				szGRIDE = commUtils.nvl(jrCommResult.getFieldString("GRIDE"),"9");
	//LOG_TABLE 
					
				szLogMsg = "대상코일:" + szSTOCK_ID  + "대상위치:" + szSTACK_COL_GP + szSTACK_BED_GP + szSTACK_LAYER_GP 
				           + "좌코일:" + szLEFT_STOCK_ID  + "우코일:" + szRIGHT_STOCK_ID + "평점:" + szGRIDE;
				
				
				if (!szGRIDE.equals("9")) {
					//적치가능 check
					jrLocAbleRtn = this.procLocAbleCheck(logId, methodNm, jrResult, jrCommResult,jrSchRule);

					String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
					String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
					
					
					if (LocAbleRtn.equals("1")) {
						szLogMsg = methodNm+ szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치가능 위치평점:"+ szGRIDE;
						commUtils.printLog(logId, szLogMsg, "SL");				
					    //적치가능 
						jrGradeAnalysisRtn.setField("GRIDE"		    , szGRIDE);
						jrGradeAnalysisRtn.setField("GRADE_RTN"		, "1");
		    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS", "LOC:"+szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치가능 위치평점:"+ szGRIDE );
					} else {
						//적치 불가
						szLogMsg = methodNm+ szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치불가 위치평점:"+ szGRIDE;
						commUtils.printLog(logId, szLogMsg, "SL");				
						jrGradeAnalysisRtn.setField("GRIDE"		    , YmConstant.RETN_CD_FAILURE);
						jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
		    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS", "LOC:"+ szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치불가 위치평점:"+ szGRIDE +"사유:"+ LocAbleRtnMsg );
						return jrGradeAnalysisRtn;
					}
				} else {
					jrGradeAnalysisRtn.setField("GRADE_CONTENTS", "LOC:"+szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치가능 위치평점:"+ szGRIDE );
				}
			}	
        	commUtils.printLog(logId, methodNm, "S-");
			 // 적치 가능 여부 CEHCK	
        	jrGradeAnalysisRtn.setField("GRIDE"		    , szGRIDE);
			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "1");
			return jrGradeAnalysisRtn;

		} catch(Exception e) {
			
			szLogMsg = methodNm+ "코일창고야드위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrGradeAnalysisRtn.setField("GRIDE"		    , YmConstant.RETN_CD_FAILURE);
			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
			return jrGradeAnalysisRtn;
		}
	} //   
    

	/**
	 * [A] 오퍼레이션명 : 적치가능 Check(스케쥴에서 사용에서 사용)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procLocAbleChk( String logId, String methodNms, JDTORecord jrCoil, JDTORecord jrSchRule) throws JDTOException  {
	  	String methodNm = "적치가능 Check[BCoilSchSeEJB.procLocAbleChk] < " + methodNms;
	  	String szLogMsg			= null;
	  	//String szLogMsg1		= null;
	  	//String szLogMsg2		= null;
	    
		//String szYD_CRN_SCH_ID  = commUtils.trim(jrCoil.getFieldString("YD_CRN_SCH_ID"  ));		//크레인 스케쥴ID
		String szYD_SCH_CD      = commUtils.trim(jrCoil.getFieldString("YD_SCH_CD"  ));		    //크레인 스케쥴코드
		
		String szStockId 		= commUtils.trim(jrCoil.getFieldString("STOCK_ID"));
    	String szStackGp 		= commUtils.trim(jrCoil.getFieldString("TAG_STACK_COL_GP"));
		String szStackBedGp 	= commUtils.trim(jrCoil.getFieldString("TAG_STACK_BED_GP"));
		String szStackLayer		= commUtils.trim(jrCoil.getFieldString("TAG_STACK_LAYER_GP"));
		
		String szLeftStockId 	= commUtils.trim(jrCoil.getFieldString("TAG_LEFT_STOCK_ID"));	//2단일 경우 좌측
		//String szLeftBed  		= commUtils.trim(jrCoil.getFieldString("TAG_LEFT_BED"));		//2단일 경우 좌측
		//String szLeftLayer    	= commUtils.trim(jrCoil.getFieldString("TAG_LEFT_LAYER"));		//2단일 경우 좌측
		
		String szRightStockId	= commUtils.trim(jrCoil.getFieldString("TAG_RIGHT_STOCK_ID"));	//2단일 경우 우측		
		//String szRightBed	   	= commUtils.trim(jrCoil.getFieldString("TAG_RIGHT_BED"));		//2단일 경우 우측
		//String szRightLayer    	= commUtils.trim(jrCoil.getFieldString("TAG_RIGHT_LAYER"));		//2단일 경우 우측

	   	//String szProgCd			= "";   	// 진도코드     
    	//String szNextProc   	= "";   	// 차공정       
    	//String szSpecAbbsym 	= "";// 규격약호     
    	//String szOrdNo      	= "";// 주문번호     
    	//String szOrdDtl     	= "";// 주문행번     
    	//String szReceiptDt  	= "";// 입고일자     
    	//String szMillDt     	= "";// 압연일시     
    	//String szDemanderCd 	= "";// 수요가코드   
    	String szThick      	= "";// 두께          
    	String szWidth      	= "";// 폭           
    	String szWeigth      	= "";// 중량          
    	String szOutDia      	= "";// 외경    
    	
    	//String szLeftProgCd     = "";// Left 진도코드     
    	//String szLeftNextProc   = "";// Left 차공정       
    	//String szLeftSpecAbbsym = "";// Left 규격약호     
    	//String szLeftOrdNo      = "";// Left 주문번호     
    	//String szLeftOrdDtl     = "";// Left 주문행번     
    	//String szLeftReceiptDt  = "";// Left 입고일자     
    	//String szLeftMillDt     = "";// Left 압연일시     
    	//String szLeftDemanderCd = "";// Left 수요가코드   
    	String szLeftThick      = "";// Left 두께           
    	String szLeftWidth      = "";// Left 폭           
    	String szLeftWeigth     = "";// Left 중량          
    	String szLeftOutDia     = "";// Left 외경
    	
    	//String szRightProgCd    = "";// Right 진도코드     
    	//String szRightNextProc  = "";// Right 차공정       
    	//String szRightSpecAbbsym= "";// Right 규격약호     
    	//String szRightOrdNo     = "";// Right 주문번호     
    	//String szRightOrdDtl    = "";// Right 주문행번     
    	//String szRightReceiptDt = "";// Right 입고일자     
    	//String szRightMillDt    = "";// Right 압연일시     
    	//String szRightDemanderCd= "";// Right 수요가코드   
    	String szRightThick     = "";// Right 두께           
    	String szRightWidth     = "";// Right 폭           
    	String szRightWeigth    = "";// Right 중량          
    	String szRightOutDia    = "";// Right 외경

		
 
    	String szRtnVal 		= "";
    	
    	JDTORecord jrGradeChk = JDTORecordFactory.getInstance().create();
    	commUtils.printLog(logId, methodNm, "S+");
    	
    	JDTORecord jrRtnLog = JDTORecordFactory.getInstance().create(); 

		try {
			
			commUtils.printLog(logId, "적치가능대상코일위치:"+ szStackGp+szStackBedGp+szStackLayer, "SL");
			commUtils.printLog(logId, "코일번호:"+ szStockId+"  좌측코일번호:"+ szLeftStockId+"  우측코일번호:"+ szRightStockId, "SL");
			
//			String sAPP027_YN = YmComm.BCoilApplyYn("APP027","3","1");   //log여부
//			commUtils.printLog(logId,  "로직변경:" + sAPP027_YN, "SL");
						
//			if (sAPP027_YN.equals("Y")) {
				if (szStockId != null) {
					
					JDTORecord jrCoilCom  = jrCoil;
				   	//szProgCd		 = commUtils.trim(jrCoilCom.getFieldString("C_PROG_CD"));     	// 진도코드     
			    	//szNextProc   	 = commUtils.trim(jrCoilCom.getFieldString("C_NEXT_PROC"));   	// 차공정       
			    	//szSpecAbbsym 	 = commUtils.trim(jrCoilCom.getFieldString("C_SPEC_ABBSYM")); 	// 규격약호     
			    	//szOrdNo      	 = commUtils.trim(jrCoilCom.getFieldString("C_ORD_NO"));   		// 주문번호     
			    	//szOrdDtl     	 = commUtils.trim(jrCoilCom.getFieldString("C_ORD_DTL"));   		// 주문행번     
			    	//szReceiptDt  	 = commUtils.trim(jrCoilCom.getFieldString("C_RECEIPT_DT"));   	// 입고일자     
			    	//szMillDt     	 = commUtils.trim(jrCoilCom.getFieldString("C_MILL_DT"));   		// 압연일시     
			    	//szDemanderCd 	 = commUtils.trim(jrCoilCom.getFieldString("C_DEMANDER_CD"));  	// 수요가코드   
			    	szThick      	 = commUtils.trim(jrCoilCom.getFieldString("C_THICK"));   		// 두께          
			    	szWidth      	 = commUtils.trim(jrCoilCom.getFieldString("C_WIDTH"));   		// 폭           
			    	szWeigth      	 = commUtils.trim(jrCoilCom.getFieldString("C_WEIGTH"));   		// 중량          
			    	szOutDia      	 = commUtils.trim(jrCoilCom.getFieldString("C_OUTDIA"));   		// 외경    
			    	
			    	//szLeftProgCd     = commUtils.trim(jrCoilCom.getFieldString("L_PROG_CD"));  		// Left 진도코드     
			    	//szLeftNextProc   = commUtils.trim(jrCoilCom.getFieldString("L_NEXT_PROC"));   	// Left 차공정       
			    	//szLeftSpecAbbsym = commUtils.trim(jrCoilCom.getFieldString("L_SPEC_ABBSYM"));  	// Left 규격약호     
			    	//szLeftOrdNo      = commUtils.trim(jrCoilCom.getFieldString("L_ORD_NO"));   		// Left 주문번호     
			    	//szLeftOrdDtl     = commUtils.trim(jrCoilCom.getFieldString("L_ORD_DTL"));   		// Left 주문행번     
			    	//szLeftReceiptDt  = commUtils.trim(jrCoilCom.getFieldString("L_RECEIPT_DT"));   	// Left 입고일자     
			    	//szLeftMillDt     = commUtils.trim(jrCoilCom.getFieldString("L_MILL_DT"));   		// Left 압연일시     
			    	//szLeftDemanderCd = commUtils.trim(jrCoilCom.getFieldString("L_DEMANDER_CD"));  	// Left 수요가코드   
			    	szLeftThick      = commUtils.trim(jrCoilCom.getFieldString("L_THICK"));   		// Left 두께           
			    	szLeftWidth      = commUtils.trim(jrCoilCom.getFieldString("L_WIDTH"));   		// Left 폭           
			    	szLeftWeigth     = commUtils.trim(jrCoilCom.getFieldString("L_WEIGTH"));   		// Left 중량          
			    	szLeftOutDia     = commUtils.trim(jrCoilCom.getFieldString("L_OUTDIA"));   		// Left 외경
			    	
			    	//szRightProgCd    = commUtils.trim(jrCoilCom.getFieldString("R_PROG_CD"));   		// Right 진도코드     
			    	//szRightNextProc  = commUtils.trim(jrCoilCom.getFieldString("R_NEXT_PROC"));   	// Right 차공정       
			    	//szRightSpecAbbsym= commUtils.trim(jrCoilCom.getFieldString("R_SPEC_ABBSYM"));   	// Right 규격약호     
			    	//szRightOrdNo     = commUtils.trim(jrCoilCom.getFieldString("R_ORD_NO"));   		// Right 주문번호     
			    	//szRightOrdDtl    = commUtils.trim(jrCoilCom.getFieldString("R_ORD_DTL"));   		// Right 주문행번     
			    	//szRightReceiptDt = commUtils.trim(jrCoilCom.getFieldString("R_RECEIPT_DT"));   	// Right 입고일자     
			    	//szRightMillDt    = commUtils.trim(jrCoilCom.getFieldString("R_MILL_DT"));   		// Right 압연일시     
			    	//szRightDemanderCd= commUtils.trim(jrCoilCom.getFieldString("R_DEMANDER_CD"));   	// Right 수요가코드   
			    	szRightThick     = commUtils.trim(jrCoilCom.getFieldString("R_THICK"));   		// Right 두께           
			    	szRightWidth     = commUtils.trim(jrCoilCom.getFieldString("R_WIDTH"));   		// Right 폭           
			    	szRightWeigth    = commUtils.trim(jrCoilCom.getFieldString("R_WEIGTH"));   		// Right 중량          
			    	szRightOutDia    = commUtils.trim(jrCoilCom.getFieldString("R_OUTDIA"));   		// Right 외경
				} else {
					jrRtnLog.setField("LOC_ABLE_RTN"  , "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "<적치불가:대상재 READ 실패>");
					commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;				
				}
								
//			} else {}

			String ruleODiaDeff1 = commUtils.trim(jrSchRule.getFieldString("ODIA_DIFF1")); //외경기준
			String ruleWidDeff1  = commUtils.trim(jrSchRule.getFieldString("WID_DIFF1"));  //폭기준
			String ruleODiaDeff2 = commUtils.trim(jrSchRule.getFieldString("ODIA_DIFF2"));
			String ruleBedDeff2  = commUtils.trim(jrSchRule.getFieldString("BED_DIFF2"));  //BED 간격
			String ruleWidDeff2  = commUtils.trim(jrSchRule.getFieldString("WID_DIFF2"));  //폭 편차
			String ruleWgtDeff2  = commUtils.trim(jrSchRule.getFieldString("WGT_DIFF2"));  //중량 편차
			
			String ruleWidLift1Deff1  = commUtils.trim(jrSchRule.getFieldString("WID_LIFT1"));   //LIFT 간격
			String ruleWidChk 	 = "";
			String ruleWidChkA 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_A"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String ruleWidChkB 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_B"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String ruleWidChkC 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_C"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String ruleWidChkD 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_D"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String ruleWidChkE 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_E"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경

			String ruleSTK_RT    = commUtils.trim(jrSchRule.getFieldString("STK_RT"));      //1단 외경편차 계산 수행 여부  적치율
			String ruleRULE_APP054    = commUtils.trim(jrSchRule.getFieldString("RULE_APP054")); //1단 외경편차 계산 수행 여부 판단기준 적치율 
			String ruleODiaDevCalActYn = commUtils.nvl(jrSchRule.getFieldString("OUTDIA_DEV_CAL_ACT_YN"),"Y");   //1단 외경편차 계산 수행 여부 기준  Y:수행 ,그외 값: skip
			
			commUtils.printLog(logId, "ruleOdiaDeff1="  + ruleODiaDeff1 + " WID_DIFF1="  + ruleWidDeff1+ " ODIA_DIFF2="  + ruleODiaDeff2+ " BED_DIFF2="  + ruleBedDeff2, "SL");
			
		    if ("A".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkA;
		    } else if ("B".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkB;
		    } else if ("C".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkC;
		    } else if ("D".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkD;
		    } else if ("E".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkE;
		    }	

        	commUtils.printLog(logId, "1단 외경편차 계산 수행 여부 적치율="  + ruleSTK_RT  + " , APP054 기준=" + ruleRULE_APP054, "SL");
        	commUtils.printLog(logId, "1단 외경편차 계산 수행 여부 ruleODiaDevCalActYn="  + ruleODiaDevCalActYn , "SL");
		    
        	if("Y".equals(ruleODiaDevCalActYn)) {
	        	if ("01".equals(szStackLayer) && szStackGp.matches("[3][A-E]\\d\\d\\d\\d")) {
					// 1단일 경우 외경편차를 계산한다. --> 일반야드 1단일 경우 외경편자를 계산한다 (대차상에 권하위치는 외경편자 체크 안함) 
					commUtils.printLog(logId, "====================1단일 경우 외경편차를 계산한다.====================", "SL");
					
	        		jrGradeChk = JDTORecordFactory.getInstance().create();
	        		jrGradeChk = procCoilYdOutDiaDiffCheck_1( logId, methodNm, szStockId, szStackLayer, szOutDia, szLeftStockId, szLeftOutDia,  szRightStockId, szRightOutDia, ruleODiaDeff1);
	        		
	        		szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
	        		
	        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	    				
	        			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일과 외경차이("+ruleODiaDeff1+"mm)로 적치가 불가능한 1 단 입니다.";
	    				commUtils.printLog(logId, szLogMsg, "SL");
	
	    				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
		    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:1단 외경편차>\r\n" + commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
		    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
	    				
	    				commUtils.printLog(logId, methodNm, "S-");
						return jrRtnLog;
	        		}
	        	}
        	}
        	
        	commUtils.printLog(logId, "2단폭 check 여부 ruleWidChk="  + ruleWidChk , "SL");
 			
        	if ("Y".equals(ruleWidChk)) {
				if ("02".equals(szStackLayer) && !"".equals(szLeftStockId) && !"".equals(szRightStockId)) {
		         	// 2단일 경우 폭기준를 계산한다. 
					commUtils.printLog(logId, "====================2단일 경우 폭기준를 계산한다 .====================", "SL");
	        		
	        		jrGradeChk = JDTORecordFactory.getInstance().create();
	        		jrGradeChk = procCoilYdWidthCheck_2( logId, methodNm, szStockId, szStackLayer, szWidth, szLeftStockId, szLeftWidth,  szRightStockId, szRightWidth, ruleWidDeff1);
	  
	        		szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
	       		        		
	        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일과 폭기준("+ ruleWidDeff1 +"mm)로 적치가 불가능한 2 단 입니다.";
	    				commUtils.printLog(logId, szLogMsg, "SL");
	    				
	       				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
		    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단 폭기준>\r\n" + commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
		    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
	 
	    	        	commUtils.printLog(logId, methodNm, "S-");
						return jrRtnLog;
	        		}
	        	}
        		
        	} else {
	        	if ("01".equals(szStackLayer) && szStackGp.matches("[3][A-E]\\d\\d\\d\\d")) {
		         	// 1단일 경우 폭기준를 계산한다. --> 일반야드 1단일 경우 폭기준을 계산한다 (대차상에 권하위치는 폭기준 체크 안함) 
					commUtils.printLog(logId, "====================1단일 경우 폭기준를 계산한다 .====================", "SL");
	        		
	        		jrGradeChk = JDTORecordFactory.getInstance().create();
	        		jrGradeChk = procCoilYdWidthCheck_1( logId, methodNm, szStockId, szStackLayer, szWidth, szLeftStockId, szLeftWidth,  szRightStockId, szRightWidth, ruleWidDeff1);
	  
	        		szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
	       		        		
	        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일과 폭기준("+ ruleWidDeff1 +"mm)로 적치가 불가능한 1 단 입니다.";
	    				commUtils.printLog(logId, szLogMsg, "SL");
	    				
	       				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
		    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:1단 폭기준>\r\n" + commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
		    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
	 
	    	        	commUtils.printLog(logId, methodNm, "S-");
						return jrRtnLog;
	        		}
	        	}
        	}	
        	//commUtils.printLog(logId, "====================1단일 경우 폭간섭을 계산한다 .====================", "SL");
         	//  1단일 경우 폭간섭를 계산한다. (저장위치수정인 경우 삭제 한다.) 확인
//        	String sAPP020 = YmComm.BCoilApplyYn("APP020","3","1");
//			commUtils.printLog(logId,  " 1단 폭 간섭  처리:" + sAPP020, "SL");
//			if (sAPP020.equals("Y")) {
				if ("01".equals(szStackLayer) && szStackGp.matches("[3][A-E]\\d\\d\\d\\d")) { 
					commUtils.printLog(logId, "====================1단일 경우 폭간섭을 계산한다 .====================", "SL");
	        		jrGradeChk = JDTORecordFactory.getInstance().create();
	        		jrGradeChk = procCoilYdWidthInterCheck_1( logId, methodNm, szStockId,szStackGp, szStackBedGp, szStackLayer, szWidth, ruleWidLift1Deff1);
	  
	        		szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
	       		        		
	        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일과 폭간섭으로 적치가 불가능한 1 단 입니다.";
	    				commUtils.printLog(logId, szLogMsg, "SL");
	    				
	       				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
		    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:1단 폭간섭>\r\n" + commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
		    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
	 
	    	        	commUtils.printLog(logId, methodNm, "S-");
						return jrRtnLog;
	        		}
	        	}
//			}
				
				
			/**********************************************************
			* 좌,우열 1,2 단 폭 간섭 체크 
			**********************************************************/
 			String sAPP060_ZZ004_YN = YmComm.BCoilApplyYn("APP060","3","ZZ004_YN");   //TO위치 검색 적치가능체크에서 2단 폭 간섭 체크 여부
			if (sAPP060_ZZ004_YN.equals("Y")) {
				
				if(szStackGp.matches("[3][A-E]\\d\\d\\d\\d")) {
					commUtils.printLog(logId, "====================좌,우열 폭간섭을 체크 한다 .====================", "SL");
	        		jrGradeChk = JDTORecordFactory.getInstance().create();
	        		jrGradeChk = this.procCoilYdWidthInterCheck_2( logId, methodNm, szStockId,szStackGp, szStackBedGp, szStackLayer, szWidth, ruleWidLift1Deff1);
					
	        		szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
	        		
					if(szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 좌우열 폭간섭으로 적치가 불가능한 위치 입니다.";
	    				commUtils.printLog(logId, szLogMsg, "SL");
	    				
	       				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
		    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:폭간섭>\r\n" + commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
		    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
	 
	    	        	commUtils.printLog(logId, methodNm, "S-");
						return jrRtnLog;
					}
				}

			}
				

       	// 2단일 경우 중량/폭편차를 계산한다.
        	if ("02".equals(szStackLayer) && !"".equals(szLeftStockId) && !"".equals(szRightStockId)) {
    			commUtils.printLog(logId, "====================2단일 경우 중량/폭편차를 계산한다 .====================", "SL");
    			jrGradeChk = JDTORecordFactory.getInstance().create();
    			jrGradeChk = procCoilYdWtWidCheck_2(logId, methodNm, szStockId, szThick,szWidth, szWeigth, szLeftStockId, szLeftThick,szLeftWidth, szLeftWeigth
	       				                                        ,szRightStockId,szRightThick,szRightWidth, szRightWeigth,ruleWidDeff2,ruleWgtDeff2);
    			
    			szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
    			
        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일과 중량/폭편차로 적치가 불가능한 2단 입니다.";

    				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
          			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단중량폭편차>\r\n"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
	    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
    	    		
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;	
					
        		} else if (szRtnVal.equals("WID_FAILURE")) {
    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일과폭편차로 적치가 불가능한 2단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");
       				
	    			jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단중량폭편차>\r\n"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
	    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
    				
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;
					
           		} else if (szRtnVal.equals("WGT_FAILURE")) {
    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일과중량편차로 적치가 불가능한 2단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");
	    			jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단중량폭편차>\r\n"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
	    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
    				
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;					
        		}
        	}
        	
        	// 횡행좌표 거리 계산 값에 의한 외경 CHECK
        	if ("02".equals(szStackLayer)) {
    			commUtils.printLog(logId, "====================2단일 경우 횡행좌표 거리 계산 값에 의한 외경체크 .====================", "SL");
        		
    			szRtnVal = procCoilYdOutDiaOverRunCheck_2( logId, methodNm, szStockId, szStackGp, szStackBedGp, szStackLayer, szOutDia, szLeftStockId, szLeftOutDia,  szRightStockId, szRightOutDia);
        		        		
        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
        			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일이 횡행좌표 거리 계산 값에 의한 적치가 불가능한 2단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");
   					jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단 횡행좌표외경체크>\r\n"+  szLogMsg);
	    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
    				
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;	
        		}
        	}
        	
        	
        	
        	// 2단일 경우 외경간격을 계산한다.(OK)
        	if ("02".equals(szStackLayer) && !"".equals(szLeftStockId) && !"".equals(szRightStockId)) {
    			commUtils.printLog(logId, "====================2단일 경우 외경간격을 계산한다 .====================", "SL");
    			
    			jrGradeChk = JDTORecordFactory.getInstance().create();
    			jrGradeChk = procCoilYdOutDiaIntervalCheck_2(logId, methodNm, szStockId,  szStackGp, szStackBedGp, szStackLayer
        				                                  , szOutDia, szLeftStockId, szLeftOutDia,  szRightStockId, szRightOutDia, ruleODiaDeff2, ruleBedDeff2);
    			
    			szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));          		        		
        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
        			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일이 외경간격편차로 적치가 불가능한 2단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");
   					jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단 외경간격>\r\n"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
	    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
	    		
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;
        		}
        	}
           	
           	
        	// 2단일 경우 기울기 공식 적용.
        	//2단 우측에 코일이 있을 경우
         	//  1단일 경우 폭간섭를 계산한다. (저장위치수정인 경우 삭제 한다.) 확인
//        	String sAPP024 = YmComm.BCoilApplyYn("APP024","3","1");
//			commUtils.printLog(logId,  " 기울기공식적용:" + sAPP024, "SL");
//			if (sAPP024.equals("Y")) {
	        	if ("02".equals(szStackLayer)) {
	    			commUtils.printLog(logId, "====================2단일 경우 기울기를 계산한다 .====================", "SL");
	 
	        		String sGRP_GP_LOC      = "";
	        		String sCoilNoA 		= "";
	        		String sOutDiaA 		= "";
	        		String sCoilNoB 		= "";
	        		String sOutDiaB 		= "";
	        		String sCoilNoC 		= "";
	        		String sOutDiaC 		= "";
	        		String sCoilNoD 		= "";
	        		String sOutDiaD 		= "";
	        		String sCoilNoE 		= "";
	        		String sOutDiaE 		= "";
	        		String sCoilNoF 		= "";
	        		String sOutDiaF 		= "";
	        		String sCoilNoG 		= "";
	        		String sOutDiaG 		= "";
	        		//String sGRP_GP_CD       = "";
	
	        		//long   lngsOutDiaA  	= 0;           //코일외경
	        		long   lngsOutDiaB   	= 0;           
	        		long   lngsOutDiaC   	= 0;           
	        		//long   lngsOutDiaD   	= 0;           
	        		long   lngsOutDiaE   	= 0;           
	        		//long   lngsOutDiaF   	= 0;           
	        		long   lngsOutDiaG   	= 0;                  		
	        		
	        		JDTORecord recPara = JDTORecordFactory.getInstance().create();
	    			recPara.setField("STACK_COL_GP"		, szStackGp);
	    			recPara.setField("STACK_BED_GP"		, szStackBedGp);
	    			recPara.setField("STACK_LAYER_GP"	, szStackLayer);
	       	    
	    			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilCline
	    			WITH TEMP_TABLE AS (
	    			    SELECT 'A' AS COIL_GP FROM DUAL UNION ALL 
	    			    SELECT 'B' AS COIL_GP FROM DUAL UNION ALL 
	    			    SELECT 'C' AS COIL_GP FROM DUAL UNION ALL 
	    			    SELECT 'D' AS COIL_GP FROM DUAL UNION ALL 
	    			    SELECT 'E' AS COIL_GP FROM DUAL UNION ALL 
	    			    SELECT 'F' AS COIL_GP FROM DUAL UNION ALL 
	    			    SELECT 'G' AS COIL_GP FROM DUAL 
	    			),
	    			TEMP_TABLE1 AS (
	    			    SELECT :V_STACK_COL_GP AS STACK_COL_GP
	    			         , :V_STACK_BED_GP AS STACK_BED_GP
	    			     FROM DUAL   
	    			)
	    			SELECT AA.COIL_GP
	    			     , AA.STACK_COL_GP
	    			     , AA.STACK_BED_GP
	    			     , AA.STACK_LAYER_GP
	    			     , AA.STOCK_ID
	    			     , AA.COIL_OUTDIA
	    			     , AA.COIL_CNT
	    			     , 650 GRP_GP_LOC
	    			     , AA.COIL_GP1
	    			  FROM (
	    			        SELECT Z1.COIL_GP
	    			             , NVL(Z2.STACK_COL_GP,(SELECT STACK_COL_GP FROM TEMP_TABLE1 )) AS STACK_COL_GP
	    			             , Z2.STACK_BED_GP
	    			             , STACK_LAYER_GP
	    			             , STOCK_ID
	    			             , COIL_OUTDIA
	    			             , COIL_CNT
	    			             , GRP_GP_LOC
	    			             , COIL_GP1
	    			          FROM 
	    			               (SELECT * FROM TEMP_TABLE
	    			                 ORDER BY COIL_GP) Z1,
	    			               (SELECT A.STACK_COL_GP
	    			                     , A.STACK_BED_GP
	    			                     , A.STACK_LAYER_GP
	    			                     , A.STOCK_ID
	    			                     , C.COIL_OUTDIA
	    			                     , COUNT(STOCK_ID) OVER() COIL_CNT
	    			                     , 650  AS  GRP_GP_LOC
	    			                     , (CASE WHEN A.STACK_COL_GP || A.STACK_BED_GP || A.STACK_LAYER_GP 
	    			                                = D.STACK_COL_GP || TRIM(TO_CHAR(TO_NUMBER(D.STACK_BED_GP) - 1,'00'))||'01' THEN 'B' 
	    			                             WHEN A.STACK_COL_GP || A.STACK_BED_GP || A.STACK_LAYER_GP 
	    			                                = D.STACK_COL_GP || TRIM(TO_CHAR(TO_NUMBER(D.STACK_BED_GP) - 1,'00'))||'02' THEN 'A' 
	    			                             WHEN A.STACK_COL_GP || A.STACK_BED_GP ||A.STACK_LAYER_GP 
	    			                                = D.STACK_COL_GP || D.STACK_BED_GP || '01' THEN 'C' 
	    			                             WHEN A.STACK_COL_GP || A.STACK_BED_GP ||A.STACK_LAYER_GP 
	    			                                = D.STACK_COL_GP || D.STACK_BED_GP || '02' THEN 'D' 
	    			                             WHEN A.STACK_COL_GP || A.STACK_BED_GP || A.STACK_LAYER_GP 
	    			                                = D.STACK_COL_GP || TRIM(TO_CHAR(TO_NUMBER(D.STACK_BED_GP) + 1,'00'))||'01' THEN 'E' 
	    			                             WHEN A.STACK_COL_GP || A.STACK_BED_GP || A.STACK_LAYER_GP 
	    			                                = D.STACK_COL_GP || TRIM(TO_CHAR(TO_NUMBER(D.STACK_BED_GP) + 1,'00'))||'02' THEN 'F' 
	    			                             WHEN A.STACK_COL_GP || A.STACK_BED_GP || A.STACK_LAYER_GP 
	    			                                = D.STACK_COL_GP || TRIM(TO_CHAR(TO_NUMBER(D.STACK_BED_GP) + 2,'00'))||'01' THEN 'G' 
	    			                             ELSE'' END) COIL_GP1
	    			                  FROM TB_YM_STACKLAYER A
	    			                     , TB_YM_STACKCOL B
	    			                     , USRPTA.TB_PT_COILCOMM C 
	    			                     , TEMP_TABLE1 D
	    			                 WHERE A.STACK_COL_GP = B.STACK_COL_GP 
	    			                   AND A.STACK_COL_GP = D.STACK_COL_GP 
	    			                   AND A.STACK_BED_GP IN (D.STACK_BED_GP
	    			                                         , TO_NUMBER(D.STACK_BED_GP) - 1
	    			                                         , TO_NUMBER(D.STACK_BED_GP) + 1
	    			                                         , TO_NUMBER(D.STACK_BED_GP) + 2
	    			                                         ) 
	    			                   AND A.STOCK_ID = C.COIL_NO(+)
	    			               )Z2
	    			        WHERE  Z1.COIL_GP = Z2.COIL_GP1(+)  
	    			        ) AA
	    			        , TB_YM_STACKCOL BB
	    			    WHERE AA.STACK_COL_GP=BB.STACK_COL_GP
	    			    ORDER BY COIL_GP
	    			    */
	    			JDTORecordSet jsStkLyr = commDao.select(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilCline", logId, methodNm, "코일기울기 코일정보");
	    			if (jsStkLyr.size() <= 0) {
	    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일기울기 코일정보검색 실패.";
	    				commUtils.printLog(logId, szLogMsg, "SL");
	   					jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
		    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단 코일기울기>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
		    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
		    		
	    	        	commUtils.printLog(logId, methodNm, "S-");
						return jrRtnLog;
	    			}
	    			
	     	    	
	    			jsStkLyr.first();
	    			JDTORecord jrStkLyr = JDTORecordFactory.getInstance().create();
	     	    	jrStkLyr = jsStkLyr.getRecord();
	
	     	    	for (int Loop_i = 1; Loop_i <= jsStkLyr.size(); Loop_i++) {
	     	    		jsStkLyr.absolute(Loop_i);
	     	    		jrStkLyr = jsStkLyr.getRecord();
						
						String sCOIL_GP	= commUtils.trim(jrStkLyr.getFieldString("COIL_GP"));
										  
						//좌측2단
						if (sCOIL_GP.equals("A")) {
							sCoilNoA    = commUtils.trim(jrStkLyr.getFieldString("STOCK_ID"));
							sOutDiaA    = commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA"));
							//lngsOutDiaA = commUtils.paraRecChkNullLong(jrStkLyr,"COIL_OUTDIA");
							sGRP_GP_LOC = commUtils.nvl(jrStkLyr.getFieldString("GRP_GP_LOC"),"0"); 
							
							
						//좌측1단
						} else if (sCOIL_GP.equals("B")) {
							sCoilNoB 	= commUtils.trim(jrStkLyr.getFieldString("STOCK_ID"));
							sOutDiaB    = commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA"));
							lngsOutDiaB	= commUtils.paraRecChkNullLong(jrStkLyr,"COIL_OUTDIA");
							
						//대상1단
						} else if (sCOIL_GP.equals("C")) {
							sCoilNoC 	= commUtils.trim(jrStkLyr.getFieldString("STOCK_ID"));
							sOutDiaC    = commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA"));
							lngsOutDiaC	= commUtils.paraRecChkNullLong(jrStkLyr,"COIL_OUTDIA");
							
						//대상2단 : 목적
						} else if (sCOIL_GP.equals("D")) {
							sCoilNoD 	= szStockId;
							sOutDiaD    = szOutDia;
							//lngsOutDiaD	= Long.parseLong(commUtils.nvl(szOutDia,"0"));
						//우측1단
						} else if (sCOIL_GP.equals("E")) {
							sCoilNoE 	= commUtils.trim(jrStkLyr.getFieldString("STOCK_ID"));
							sOutDiaE    = commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA"));
							lngsOutDiaE	= commUtils.paraRecChkNullLong(jrStkLyr,"COIL_OUTDIA");
						//우측2단
						} else if (sCOIL_GP.equals("F")) {
							sCoilNoF 	= commUtils.trim(jrStkLyr.getFieldString("STOCK_ID"));
							sOutDiaF    = commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA"));
							//lngsOutDiaF	= commUtils.paraRecChkNullLong(jrStkLyr,"COIL_OUTDIA");
	
							//우측+1 1단
						} else if (sCOIL_GP.equals("G")) {
							sCoilNoG 	= commUtils.trim(jrStkLyr.getFieldString("STOCK_ID"));
							sOutDiaG    = commUtils.trim(jrStkLyr.getFieldString("COIL_OUTDIA"));
							lngsOutDiaG	= commUtils.paraRecChkNullLong(jrStkLyr,"COIL_OUTDIA");
						}
					}
	     	    		
	     	    	if ((!sCoilNoA.equals("")) 
	     	    			&& ( lngsOutDiaB >= lngsOutDiaC) 
	     	    			&& ( lngsOutDiaE >= lngsOutDiaC)) {
	       				szRtnVal = searchCoilYdGdsCline( logId, methodNm, szStackGp, szStackBedGp, szStackLayer, sGRP_GP_LOC
	       						                        ,sCoilNoA, sOutDiaA
	       						                        ,sCoilNoB, sOutDiaB
	       						                        ,sCoilNoC, sOutDiaC
	               				                        ,sCoilNoD, sOutDiaD
	               				                        ,sCoilNoE, sOutDiaE);
	
	               		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	        				
	        				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일기울기 코일정보검색 실패.";
	        				commUtils.printLog(logId, szLogMsg, "SL");
	       					jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단 코일기울기>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
			    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
	    	    		
	        	        	commUtils.printLog(logId, methodNm, "S-");
	    					return jrRtnLog;
	        				
	            		}
	     	    	} else if ((!sCoilNoF.equals("")) && (!sCoilNoG.equals("")) 
	     	    			&& ( lngsOutDiaC >= lngsOutDiaE ) 
	     	    			&& ( lngsOutDiaG >= lngsOutDiaE)) {	
	
	       				szRtnVal = searchCoilYdGdsCline( logId, methodNm, szStackGp, szStackBedGp, szStackLayer, sGRP_GP_LOC
	       												,sCoilNoD, sOutDiaD
	       												,sCoilNoC, sOutDiaC
	       												,sCoilNoE, sOutDiaE
	       						                        ,sCoilNoF, sOutDiaF
	       						                        ,sCoilNoG, sOutDiaG);
	
	               		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	               			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일기울기 코일정보검색 실패.";
	        				commUtils.printLog(logId, szLogMsg, "SL");
	       					jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:2단 코일기울기>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
			    			jrRtnLog.setField("LOG_MSG"				, szLogMsg);
	    	    		
	        	        	commUtils.printLog(logId, methodNm, "S-");
	    					return jrRtnLog;            		
	    				}
	            	} else {
	            		commUtils.printLog(logId, "기울기 공식적용 적용안함", "SL");
	               	}	
	         	}                	
//			}	
        	commUtils.printLog(logId, methodNm, "S-");
		} catch(Exception e) {
			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
        	commUtils.printLog(logId, methodNm, "S-");
        	jrRtnLog.setField("LOC_ABLE_RTN"  , "-1");
        	return jrRtnLog;
		}
		
		jrRtnLog.setField("LOC_ABLE_RTN"		, "1");
		jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치가능 CHECK : OK>");
		return jrRtnLog;

	} 	
	
	
	/**
	 * [A] 오퍼레이션명 : 평점분석(크레인 응답에서 사용)
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procLocAbleCheck( String logId, String methodNms, JDTORecord jrCoil, JDTORecord jrCoilCom, JDTORecord jrSchRule) throws JDTOException  {
	  	String methodNm = "평점분석[BCoilSchSeEJB.procLocAbleCheck] < " + methodNms;
	  	String szLogMsg			= null;
	  	String szLogMsg1		= null;
	  	String szLogMsg2		= null;
	    
		String szYD_CRN_SCH_ID  = commUtils.trim(jrCoil.getFieldString("YD_CRN_SCH_ID"  ));		//크레인 스케쥴ID
		String szYD_SCH_CD      = commUtils.trim(jrCoil.getFieldString("YD_SCH_CD"  ));		    //크레인 스케쥴코드
		
		String szStockId 		= commUtils.trim(jrCoil.getFieldString("STOCK_ID"));
    	String szStackGp 		= commUtils.trim(jrCoil.getFieldString("TAG_STACK_COL_GP"));
		String szStackBedGp 	= commUtils.trim(jrCoil.getFieldString("TAG_STACK_BED_GP"));
		String szStackLayer		= commUtils.trim(jrCoil.getFieldString("TAG_STACK_LAYER_GP"));
		
		String szLeftStockId 	= commUtils.trim(jrCoil.getFieldString("TAG_LEFT_STOCK_ID"));	//2단일 경우 좌측
		String szLeftBed  		= commUtils.trim(jrCoil.getFieldString("TAG_LEFT_BED"));		//2단일 경우 좌측
		String szLeftLayer    	= commUtils.trim(jrCoil.getFieldString("TAG_LEFT_LAYER"));		//2단일 경우 좌측
		
		String szRightStockId	= commUtils.trim(jrCoil.getFieldString("TAG_RIGHT_STOCK_ID"));	//2단일 경우 우측		
		String szRightBed	   	= commUtils.trim(jrCoil.getFieldString("TAG_RIGHT_BED"));		//2단일 경우 우측
		String szRightLayer    	= commUtils.trim(jrCoil.getFieldString("TAG_RIGHT_LAYER"));		//2단일 경우 우측


		
    	String szProgCd			= commUtils.trim(jrCoilCom.getFieldString("C_PROG_CD"));     	// 진도코드     
    	String szNextProc   	= commUtils.trim(jrCoilCom.getFieldString("C_NEXT_PROC"));   	// 차공정       
    	String szSpecAbbsym 	= commUtils.trim(jrCoilCom.getFieldString("C_SPEC_ABBSYM")); 	// 규격약호     
    	String szOrdNo      	= commUtils.trim(jrCoilCom.getFieldString("C_ORD_NO"));   		// 주문번호     
    	String szOrdDtl     	= commUtils.trim(jrCoilCom.getFieldString("C_ORD_DTL"));   		// 주문행번     
    	String szReceiptDt  	= commUtils.trim(jrCoilCom.getFieldString("C_RECEIPT_DT"));   	// 입고일자     
    	String szMillDt     	= commUtils.trim(jrCoilCom.getFieldString("C_MILL_DT"));   		// 압연일시     
    	String szDemanderCd 	= commUtils.trim(jrCoilCom.getFieldString("C_DEMANDER_CD"));  	// 수요가코드   
    	String szThick      	= commUtils.trim(jrCoilCom.getFieldString("C_THICK"));   		// 두께          
    	String szWidth      	= commUtils.trim(jrCoilCom.getFieldString("C_WIDTH"));   		// 폭           
    	String szWeigth      	= commUtils.trim(jrCoilCom.getFieldString("C_WEIGTH"));   		// 중량          
    	String szOutDia      	= commUtils.trim(jrCoilCom.getFieldString("C_OUTDIA"));   		// 외경    
    	
    	String szLeftProgCd     = commUtils.trim(jrCoilCom.getFieldString("L_PROG_CD"));  		// Left 진도코드     
    	String szLeftNextProc   = commUtils.trim(jrCoilCom.getFieldString("L_NEXT_PROC"));   	// Left 차공정       
    	String szLeftSpecAbbsym = commUtils.trim(jrCoilCom.getFieldString("L_SPEC_ABBSYM"));  	// Left 규격약호     
    	String szLeftOrdNo      = commUtils.trim(jrCoilCom.getFieldString("L_ORD_NO"));   		// Left 주문번호     
    	String szLeftOrdDtl     = commUtils.trim(jrCoilCom.getFieldString("L_ORD_DTL"));   		// Left 주문행번     
    	String szLeftReceiptDt  = commUtils.trim(jrCoilCom.getFieldString("L_RECEIPT_DT"));   	// Left 입고일자     
    	String szLeftMillDt     = commUtils.trim(jrCoilCom.getFieldString("L_MILL_DT"));   		// Left 압연일시     
    	String szLeftDemanderCd = commUtils.trim(jrCoilCom.getFieldString("L_DEMANDER_CD"));  	// Left 수요가코드   
    	String szLeftThick      = commUtils.trim(jrCoilCom.getFieldString("L_THICK"));   		// Left 두께           
    	String szLeftWidth      = commUtils.trim(jrCoilCom.getFieldString("L_WIDTH"));   		// Left 폭           
    	String szLeftWeigth     = commUtils.trim(jrCoilCom.getFieldString("L_WEIGTH"));   		// Left 중량          
    	String szLeftOutDia     = commUtils.trim(jrCoilCom.getFieldString("L_OUTDIA"));   		// Left 외경
    	
    	String szRightProgCd    = commUtils.trim(jrCoilCom.getFieldString("R_PROG_CD"));   		// Right 진도코드     
    	String szRightNextProc  = commUtils.trim(jrCoilCom.getFieldString("R_NEXT_PROC"));   	// Right 차공정       
    	String szRightSpecAbbsym= commUtils.trim(jrCoilCom.getFieldString("R_SPEC_ABBSYM"));   	// Right 규격약호     
    	String szRightOrdNo     = commUtils.trim(jrCoilCom.getFieldString("R_ORD_NO"));   		// Right 주문번호     
    	String szRightOrdDtl    = commUtils.trim(jrCoilCom.getFieldString("R_ORD_DTL"));   		// Right 주문행번     
    	String szRightReceiptDt = commUtils.trim(jrCoilCom.getFieldString("R_RECEIPT_DT"));   	// Right 입고일자     
    	String szRightMillDt    = commUtils.trim(jrCoilCom.getFieldString("R_MILL_DT"));   		// Right 압연일시     
    	String szRightDemanderCd= commUtils.trim(jrCoilCom.getFieldString("R_DEMANDER_CD"));   	// Right 수요가코드   
    	String szRightThick     = commUtils.trim(jrCoilCom.getFieldString("R_THICK"));   		// Right 두께           
    	String szRightWidth     = commUtils.trim(jrCoilCom.getFieldString("R_WIDTH"));   		// Right 폭           
    	String szRightWeigth    = commUtils.trim(jrCoilCom.getFieldString("R_WEIGTH"));   		// Right 중량          
    	String szRightOutDia    = commUtils.trim(jrCoilCom.getFieldString("R_OUTDIA"));   		// Right 외경
    	
    	String szRtnVal 		= "";
    	
    	JDTORecord jrGradeChk = JDTORecordFactory.getInstance().create();
    	commUtils.printLog(logId, methodNm, "S+");
    	
    	JDTORecord jrRtnLog = JDTORecordFactory.getInstance().create(); 

		try {
			
			String ruleODiaDeff1 = commUtils.trim(jrSchRule.getFieldString("ODIA_DIFF1")); //외경기준
			String ruleWidDeff1  = commUtils.trim(jrSchRule.getFieldString("WID_DIFF1"));  //폭기준
			String ruleODiaDeff2 = commUtils.trim(jrSchRule.getFieldString("ODIA_DIFF2"));
			String ruleBedDeff2  = commUtils.trim(jrSchRule.getFieldString("BED_DIFF2"));  //BED 간격
			String ruleWidDeff2  = commUtils.trim(jrSchRule.getFieldString("WID_DIFF2"));  //폭 편차
			String ruleWgtDeff2  = commUtils.trim(jrSchRule.getFieldString("WGT_DIFF2"));  //중량 편차
			String ruleWidChk 	 = "";
			String ruleWidChkA 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_A"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String ruleWidChkB 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_B"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String ruleWidChkC 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_C"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String ruleWidChkD 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_D"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String ruleWidChkE 	 = commUtils.trim(jrSchRule.getFieldString("WID_CHK_E"));   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			commUtils.printLog(logId, "ruleOdiaDeff1="  + ruleODiaDeff1 + " WID_DIFF1="  + ruleWidDeff1+ " ODIA_DIFF2="  + ruleODiaDeff2+ " BED_DIFF2="  + ruleBedDeff2, "SL");
			
			commUtils.printLog(logId, "szStackLayer="  + szStackLayer , "SL");
			
		    if ("A".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkA;
		    } else if ("B".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkB;
		    } else if ("C".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkC;
		    } else if ("D".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkD;
		    } else if ("E".equals(szYD_SCH_CD.substring(1, 2))) {
		    	ruleWidChk = ruleWidChkE;
		    }
		   // 1단일 경우 외경편차를 계산한다.
			commUtils.printLog(logId, "====================1단일 경우 외경편차를 계산한다.====================", "SL");
        	if ("01".equals(szStackLayer)) {
        		
        		jrGradeChk = JDTORecordFactory.getInstance().create();
        		jrGradeChk = procCoilYdOutDiaDiffCheck_1( logId, methodNm, szStockId, szStackLayer, szOutDia, szLeftStockId, szLeftOutDia,  szRightStockId, szRightOutDia, ruleODiaDeff1);
        		
        		szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
        		
        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
    				
        			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일과 외경차이("+ruleODiaDeff1+"mm)로 적치가 불가능한 1 단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");

    				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "<적치불가:1단 외경편차>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
    				
    				commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;
        		}
        	}
       	 	commUtils.printLog(logId, "2단폭 check 여부 ruleWidChk="  + ruleWidChk , "SL");
 			
           	if ("Y".equals(ruleWidChk)) {
				if ("02".equals(szStackLayer) && !"".equals(szLeftStockId) && !"".equals(szRightStockId)) {
		         	// 2단일 경우 폭기준를 계산한다. 
					commUtils.printLog(logId, "====================2단일 경우 폭기준를 계산한다 .====================", "SL");
	        		
	        		jrGradeChk = JDTORecordFactory.getInstance().create();
	        		jrGradeChk = procCoilYdWidthCheck_2( logId, methodNm, szStockId, szStackLayer, szWidth, szLeftStockId, szLeftWidth,  szRightStockId, szRightWidth, ruleWidDeff1);
	  
	        		szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
	       		        		
	        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일과 폭기준("+ ruleWidDeff1 +"mm)로 적치가 불가능한 2 단 입니다.";
	    				commUtils.printLog(logId, szLogMsg, "SL");
	    				
	       				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
		    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "<적치불가:2단 폭기준>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
	 
	    	        	commUtils.printLog(logId, methodNm, "S-");
						return jrRtnLog;
	        		}
	        	}
         	} else {
	        	if ("01".equals(szStackLayer)) {
		         	// 1단일 경우 폭기준를 계산한다. 
					commUtils.printLog(logId, "====================1단일 경우 폭기준를 계산한다 .====================", "SL");
	        		
	        		jrGradeChk = JDTORecordFactory.getInstance().create();
	        		jrGradeChk = procCoilYdWidthCheck_1( logId, methodNm, szStockId, szStackLayer, szWidth, szLeftStockId, szLeftWidth,  szRightStockId, szRightWidth, ruleWidDeff1);
	  
	        		szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
	       		        		
	        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
	    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일과 폭기준("+ ruleWidDeff1 +"mm)로 적치가 불가능한 1 단 입니다.";
	    				commUtils.printLog(logId, szLogMsg, "SL");
	    				
	       				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
		    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "적치단(" + szStackGp + szStackBedGp + szStackLayer + ")<적치불가:1단 폭기준>\r\n" + commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
	 
	    	        	commUtils.printLog(logId, methodNm, "S-");
						return jrRtnLog;
	        		}
	        	}
        	}	        	

        	
        	// 2단일 경우 중량/폭편차를 계산한다.
        	if ("02".equals(szStackLayer) && !"".equals(szLeftStockId) && !"".equals(szRightStockId)) {
    			commUtils.printLog(logId, "====================2단일 경우 중량/폭편차를 계산한다 .====================", "SL");
    			jrGradeChk = JDTORecordFactory.getInstance().create();
    			jrGradeChk = procCoilYdWtWidCheck_2(logId, methodNm, szStockId, szThick,szWidth, szWeigth, szLeftStockId, szLeftThick,szLeftWidth, szLeftWeigth
	       				                                        ,szRightStockId,szRightThick,szRightWidth, szRightWeigth,ruleWidDeff2,ruleWgtDeff2);
    			
    			szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));
    			
        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일과 중량/폭편차로 적치가 불가능한 2단 입니다.";

    				jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
          			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "<적치불가:2단중량폭편차>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
    	    		
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;	
					
        		} else if (szRtnVal.equals("WID_FAILURE")) {
    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일과폭편차로 적치가 불가능한 2단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");
       				
	    			jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "<적치불가:2단중량폭편차-폭>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
    				
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;
					
           		} else if (szRtnVal.equals("WGT_FAILURE")) {
    				szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일과중량편차로 적치가 불가능한 2단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");
	    			jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "<적치불가:2단중량폭편차-중량>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
    				
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;					
        		}
        	}
        	
        	// 횡행좌표 거리 계산 값에 의한 외경 CHECK
        	if ("02".equals(szStackLayer)) {
    			commUtils.printLog(logId, "====================2단일 경우 횡행좌표 거리 계산 값에 의한 외경체크 .====================", "SL");
        		
    			szRtnVal = procCoilYdOutDiaOverRunCheck_2( logId, methodNm, szStockId, szStackGp, szStackBedGp, szStackLayer, szOutDia, szLeftStockId, szLeftOutDia,  szRightStockId, szRightOutDia);
        		        		
        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
        			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일이 횡행좌표 거리 계산 값에 의한 적치가 불가능한 2단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");
   					jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "<적치불가:2단 횡행좌표외경체크>"+  szLogMsg);
    				
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;	
        		}
        	}
        	
        	// 2단일 경우 외경간격을 계산한다.(OK)
        	if ("02".equals(szStackLayer) && !"".equals(szLeftStockId) && !"".equals(szRightStockId)) {
    			commUtils.printLog(logId, "====================2단일 경우 외경간격을 계산한다 .====================", "SL");
    			
    			jrGradeChk = JDTORecordFactory.getInstance().create();
    			jrGradeChk = procCoilYdOutDiaIntervalCheck_2(logId, methodNm, szStockId,  szStackGp, szStackBedGp, szStackLayer
        				                                  , szOutDia, szLeftStockId, szLeftOutDia,  szRightStockId, szRightOutDia, ruleODiaDeff2, ruleBedDeff2);
    			
    			szRtnVal = commUtils.trim(jrGradeChk.getFieldString("RTN_CD"));          		        		
        		if (szRtnVal.equals(YmConstant.RETN_CD_FAILURE)) {
        			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 1단 코일이 외경간격편차로 적치가 불가능한 2단 입니다.";
    				commUtils.printLog(logId, szLogMsg, "SL");
   					jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
	    			jrRtnLog.setField("LOC_ABLE_CONTENTS"	, "<적치불가:2단 외경간격>"+ commUtils.trim(jrGradeChk.getFieldString("RTN_MSG")));
	    		
    	        	commUtils.printLog(logId, methodNm, "S-");
					return jrRtnLog;
        		}
        	}
           	
        	commUtils.printLog(logId, methodNm, "S-");
		} catch(Exception e) {
			szLogMsg =  "확인:"+szStockId+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일야드 To위치검색 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
        	commUtils.printLog(logId, methodNm, "S-");
        	jrRtnLog.setField("LOC_ABLE_RTN"		, "-1");
        	return jrRtnLog;
		}
		
		jrRtnLog.setField("LOC_ABLE_RTN"		, "1");
		return jrRtnLog;

	} 	
		
	
	/**
	 *      [A] 오퍼레이션명 : 1단 코일창고야드외경편차Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdOutDiaDiffCheck_1(String logId, String methodNms, String szStockId, String szStackLayer, String szOutDia
			                                                                , String szLeftStockId,String szLeftOutDia
			                                                                , String szRightStockId,String szRightOutDia
			                                                                , String ruleODiaDeff1) throws JDTOException  {
	  	String methodNm = "1단 코일창고야드외경편차Check[BCoilSchSeEJB.procCoilYdOutDiaDiffCheck_1] < " + methodNms;
    	String szLogMsg			= "";		
    	String szLogMsg1		= "";		
    	JDTORecord  jrRnt   = JDTORecordFactory.getInstance().create();	
		try {

			commUtils.printLog(logId, methodNm, "S+");
			
			long lngOutDia 		= Long.parseLong(commUtils.nvl(szOutDia,"0"));
			long lngLeftOutDia 	= Long.parseLong(commUtils.nvl(szLeftOutDia,"0"));
			long lngRightOutDia	= Long.parseLong(commUtils.nvl(szRightOutDia,"0"));
			
			long lngODiaRule	= Long.parseLong(commUtils.nvl(ruleODiaDeff1,"180"));
			
			//좌우재료번호가 이적대상과 동일 한 경우 
			if (szRightStockId.equals(szStockId)) {
				lngRightOutDia	= 0;
				szRightStockId	= "";
			}else if (szLeftStockId.equals(szStockId)) {
				lngLeftOutDia	= 0;
				szLeftStockId	= "";
			}

			szLogMsg =  "  --> 정보:대상코일외경:"+lngOutDia  +" 좌측 외경:"+lngLeftOutDia + " 우측 외경:"+lngRightOutDia + " 기준외경:"+lngODiaRule+"\r\n";
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
			
			if ("01".equals(szStackLayer)) {
				// 좌우가 비워있는경우
				if ("".equals(szLeftStockId) && "".equals(szRightStockId)) {  // 좌우가 비워있는경우
	        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
	        		
				}else {
					//좌우코일이 존재 하는 경우 
					if (!"".equals(szLeftStockId) && (!"".equals(szRightStockId))) {
						if (Math.abs(lngOutDia - lngRightOutDia) <= lngODiaRule && Math.abs(lngOutDia - lngLeftOutDia) <= lngODiaRule) {
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
			        	} else {
			        		
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			        		szLogMsg1 = szLogMsg1 + "  --> 좌우코일 존재 대상외경-우측외경=" +  Math.abs(lngOutDia - lngRightOutDia)   
			        			                  +            " 대상외경-좌측외경=" +  Math.abs(lngOutDia - lngLeftOutDia)  + " 기준외경 보다 큼" ;
			        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 );	
			        	}
					} else if (!"".equals(szRightStockId)) {
						if (Math.abs(lngOutDia - lngRightOutDia) <= lngODiaRule) {
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
			        	} else {
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			        		szLogMsg1 = szLogMsg1 + "  --> 우측만 코일 존재 대상외경-우측외경=" +  Math.abs(lngOutDia - lngRightOutDia) + " 기준외경 보다 큼" ;
			        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 );	
			        	}
					} else if (!"".equals(szLeftStockId)) {
						if (Math.abs(lngOutDia - lngLeftOutDia) <= lngODiaRule) {
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
			        	} else {
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			        		szLogMsg1 = szLogMsg1 + "  --> 좌측만 코일 존재  대상외경-좌측외경=" +  Math.abs(lngOutDia - lngLeftOutDia)  + " 기준외경 보다 큼" ;
			        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 );	
			        	}
					}
				}
			} 
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRnt;
		} catch(Exception e) {
			szLogMsg =  "코일제품창고야드외경편차Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");

			jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			return jrRnt ;
		}
		
	} // end of procCoilYdOutDiaDiffCheck_1

	
	/**
	 *      [A] 오퍼레이션명 : 1단코일폭 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdWidthCheck_1(String logId, String methodNms, String szStockId,  String szStackLayer,String szWidth
			                     , String szLeftStockId,String szLeftWidth, String szRightStockId,String szRightWidth ,String ruleWidDeff1) throws JDTOException  {
		
		String methodNm = "1단코일폭 Check[BCoilSchSeEJB.procCoilYdWidthCheck_1] < " + methodNms;
    	String szLogMsg			= "";		
    	String szLogMsg1		= "";		
    	JDTORecord  jrRnt   = JDTORecordFactory.getInstance().create();
    	
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			double lngWidth 		= Double.parseDouble(commUtils.nvl(szWidth,"0"));
			double lngLeftWidth 	= Double.parseDouble(commUtils.nvl(szLeftWidth,"0"));
			double lngRighdWidth 	= Double.parseDouble(commUtils.nvl(szRightWidth,"0"));
			double lngruleWidDeff 	= Double.parseDouble(commUtils.nvl(ruleWidDeff1,"200"));
			
//			좌우재료번호가 이적대상과 동일 한 경우 
			if (szRightStockId.equals(szStockId)) {
				lngRighdWidth = 0;
				szRightStockId = "";
			}else if (szLeftStockId.equals(szStockId)) {
				lngLeftWidth = 0;
				szLeftStockId = "";
			}

			szLogMsg =  "  --> 정보:대상코일폭:"+lngWidth + " 좌측 폭:"+lngLeftWidth + " 우측 폭:"+lngRighdWidth+ " 기준폭:"+lngruleWidDeff+"\r\n";
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
			
			if ("01".equals(szStackLayer)) {
				// 좌우가 비워있는경우
				if ("".equals(szLeftStockId) && "".equals(szRightStockId)) {  // 좌우가 비워있는경우
					jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	        		
				}else{
					
//					좌우코일이 존재 하는 경우
					if (!"".equals(szLeftStockId) && (!"".equals(szRightStockId))) {
						if (Math.abs(lngWidth - lngRighdWidth) <= lngruleWidDeff && Math.abs(lngWidth - lngLeftWidth) <= lngruleWidDeff) {
							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	        		
			        	} else {
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			        		szLogMsg1 = szLogMsg1 + "  --> 좌우코일 존재 대상폭-우측폭=" +  Math.abs(lngWidth - lngRighdWidth) 
                            					  + " 대상폭-좌측폭=" +  Math.abs(lngWidth - lngLeftWidth)  + " 기준폭 보다 큼" ;
			        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 );
			        	}
					} else if (!"".equals(szLeftStockId)) {
						if ( Math.abs(lngWidth - lngLeftWidth) <= lngruleWidDeff) {
							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	        		
			        	} else {
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			        		szLogMsg1 = szLogMsg1 + "  --> 좌측만 코일 존재 대상폭-좌측폭=" +  Math.abs(lngWidth - lngLeftWidth) + " 기준폭 보다 큼" ;
			        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 );	
			        	}
						
					} else if (!"".equals(szRightStockId)) {
						if (Math.abs(lngWidth - lngRighdWidth) <= lngruleWidDeff ) {
							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	        		
			        	} else {
			        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			        		szLogMsg1 = szLogMsg1 + "  --> 우측만 코일 존재 대상폭-우측폭=" +  Math.abs(lngWidth - lngRighdWidth) + " 기준폭 보다 큼" ;
			        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 );	
			        	}
					}
				}
			} 
			commUtils.printLog(logId, methodNm, "S-");
			return jrRnt;
		} catch(Exception e) {
			szLogMsg =  "폭CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			return jrRnt ;
		}
	} 	
	
	
	/**
	 *      [A] 오퍼레이션명 : 2단코일폭 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdWidthCheck_2(String logId, String methodNms, String szStockId,  String szStackLayer,String szWidth
			                     , String szLeftStockId,String szLeftWidth, String szRightStockId,String szRightWidth ,String ruleWidDeff1) throws JDTOException  {
		
		String methodNm = "2단코일폭 Check[BCoilSchSeEJB.procCoilYdWidthCheck_2] < " + methodNms;
    	String szLogMsg			= "";		
    	String szLogMsg1		= "";		
    	JDTORecord  jrRnt   = JDTORecordFactory.getInstance().create();
    	
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			
			double lngWidth 		= Double.parseDouble(commUtils.nvl(szWidth,"0"));
			double lngLeftWidth 	= Double.parseDouble(commUtils.nvl(szLeftWidth,"0"));
			double lngRighdWidth 	= Double.parseDouble(commUtils.nvl(szRightWidth,"0"));
			double lngruleWidDeff 	= Double.parseDouble(commUtils.nvl(ruleWidDeff1,"200"));
			
			szLogMsg =  "  --> 정보:대상코일폭:"+lngWidth + " 좌측 폭:"+lngLeftWidth + " 우측 폭:"+lngRighdWidth+ " 기준폭:"+lngruleWidDeff+"\r\n";
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
			
			if ("".equals(szLeftStockId) || "".equals(szRightStockId)) {
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
				return jrRnt;
			}

			if ((lngWidth == 0) || (lngLeftWidth == 0) || (lngRighdWidth == 0)) {
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
				return jrRnt;
			}
			
			//좌측 check
		    if ((lngLeftWidth >= lngWidth)||((lngWidth - lngLeftWidth) <= lngruleWidDeff)) {
		    	//우측check
		    	if ((lngRighdWidth >= lngWidth)||((lngWidth - lngRighdWidth) <= lngruleWidDeff)) {
		    		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);
	        		szLogMsg1 = szLogMsg1 + "  --> 적치가능" ;
	        		commUtils.printLog(logId, szLogMsg1, "SL");
		    	} else {
		    		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
	        		szLogMsg1 = szLogMsg1 + "  --> 대상폭-우측폭=" +  (lngWidth - lngRighdWidth)  + " 기준폭 보다 큼" ;
	        		commUtils.printLog(logId, szLogMsg1, "SL");
	        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 );
		    	}	
		    } else {
		    	jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
        		szLogMsg1 = szLogMsg1 + "  --> 대상폭-좌측폭=" +  (lngWidth - lngLeftWidth)  + " 기준폭 보다 큼" ;
        		commUtils.printLog(logId, szLogMsg1, "SL");
        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 );
        	}

			commUtils.printLog(logId, methodNm, "S-");
			return jrRnt;
		} catch(Exception e) {
			szLogMsg =  "2단 폭CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			return jrRnt ;
		}
		
	} 	
		
	
	/**
	 * [A] 오퍼레이션명 : 2단 횡행좌표 거리 계산 값에 의한 외경 CHECK
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String procCoilYdOutDiaOverRunCheck_2(String logId, String methodNms,String szStockId, String szStackGp, String szStackBedGp , String szStackLayer, String lOutDia
			                                     , String szLeftStockId,String szLeftOutDia
			                                     , String szRightStockId,String szRightOutDia) throws JDTOException  {
		
		String methodNm = "2단횡행좌표 거리 계산 값에 의한 외경 CHECK[BCoilSchSeEJB.procCoilYdOutDiaOverRunCheck_2] < " + methodNms;
		String szLogMsg	= "";		
		String szRtnVal = YmConstant.RETN_CD_FAILURE;
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			if ("".equals(szLeftStockId) || "".equals(szRightStockId)) {
				return szRtnVal;
			}
			
			// BED정보
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STACK_COL_GP"	, szStackGp);
			recPara.setField("STACK_BED_GP"	, szStackBedGp);
			recPara.setField("STOCK_ID"		, szStockId);
    	    
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdStklyrOverRunChk 
			SELECT SF_YM_TO_COIL_OUTDIA_CHK(:V_STACK_COL_GP, :V_STACK_BED_GP, :V_STOCK_ID) AS CHK
	  		  FROM DUAL
			*/
			JDTORecord jrBed = JDTORecordFactory.getInstance().create();
			JDTORecordSet jsBed = commDao.select(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdStklyrOverRunChk", logId, methodNm, "횡행좌표 거리 계산 값에 의한 외경 CHECK 정보");
			if (jsBed.size() <= 0) {
    			szLogMsg =  "확인:"+szStockId+"적치베드(" + szStackGp + szStackBedGp + ")로 조회중 error 발생!";
				commUtils.printLog(logId, szLogMsg, "SL");
	        	commUtils.printLog(logId, methodNm, "S-");
				return szRtnVal;
			}
        	
        	jsBed.first();
        	jrBed = jsBed.getRecord();
        	
        	String szCHK = commUtils.trim(jrBed.getFieldString("CHK"));
        	        	
        	if ("Y".equals(szCHK)) {
        		szRtnVal = YmConstant.RETN_CD_SUCCESS;
        	}else{
        		szRtnVal = YmConstant.RETN_CD_FAILURE;
        	}
        	
        	commUtils.printLog(logId, methodNm, "S-");
			return szRtnVal;
		} catch(Exception e) {
			szLogMsg =  "확인:"+szStockId+"횡행좌표 거리 계산 값에 의한 외경 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			
			return szRtnVal = YmConstant.RETN_CD_FAILURE;
		}
		
	} // end of procCoilYdOutDiaOverRunCheck_2	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 기울기 공식 적용
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public String searchCoilYdGdsCline( String logId, String methodNms, String szStackGp,String  szStackBedGp,String  szStackLayer, String sGRP_GP_LOC,
			                            String sCoilNoA,String sOutDiaA,
			                            String sCoilNoB,String sOutDiaB, 
			                            String sCoilNoC,String sOutDiaC,
			                            String sCoilNoD,String sOutDiaD,
			                            String sCoilNoE,String sOutDiaE) throws JDTOException  {
		
		String methodNm = "기울기 공식[BCoilSchSeEJB.searchCoilYdGdsCline] < " + methodNms;
		String szLogMsg = "";
		int    iY1 = 0; 
		int    iY2 = 0; 
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			long lngsOutDiaA	= Long.parseLong(commUtils.nvl(sOutDiaA,"0"));
			long lngsOutDiaB	= Long.parseLong(commUtils.nvl(sOutDiaB,"0"));
			long lngsOutDiaC	= Long.parseLong(commUtils.nvl(sOutDiaC,"0"));
			long lngsOutDiaD	= Long.parseLong(commUtils.nvl(sOutDiaD,"0"));
			long lngsOutDiaE	= Long.parseLong(commUtils.nvl(sOutDiaE,"0"));

  			szLogMsg =  "lngsOutDiaA:"+lngsOutDiaA + "lngsOutDiaB:"+lngsOutDiaB+ "lngsOutDiaC:"+lngsOutDiaC+ "lngsOutDiaD:"+lngsOutDiaD+ "lngsOutDiaE:"+lngsOutDiaE;
			commUtils.printLog(logId, szLogMsg, "SL");
			
			if (lngsOutDiaC <= lngsOutDiaE) {

				JDTORecord recPara = JDTORecordFactory.getInstance().create();
    			recPara.setField("GRP_GP_LOC"	, sGRP_GP_LOC);
    			recPara.setField("COIL_OUTDIA_A", sOutDiaA);
    			recPara.setField("COIL_OUTDIA_B", sOutDiaB);
    			recPara.setField("COIL_OUTDIA_C", sOutDiaC);
    			recPara.setField("COIL_OUTDIA_D", sOutDiaD);
    			recPara.setField("COIL_OUTDIA_E", sOutDiaE);
    			
    			 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilClineCheck1 
    			-- RC <= RE 최종 QUERY
    			WITH T1 AS ( 
    			    SELECT TO_NUMBER(:V_GRP_GP_LOC) LEN   --1950
    				     , TO_NUMBER(:V_COIL_OUTDIA_A) / 2 RA --1827
    					 , TO_NUMBER(:V_COIL_OUTDIA_B) / 2 RB --1782
    					 , TO_NUMBER(:V_COIL_OUTDIA_C) / 2 RC --1728
    					 , TO_NUMBER(:V_COIL_OUTDIA_D) / 2 RD --1745
    					 , TO_NUMBER(:V_COIL_OUYDIA_E) / 2 RE --1902
    				  FROM DUAL 
    			) 
    			SELECT ROUND((RD + RC) *
    			       ((((POWER(B.L,2) + POWER((RD + RC),2) - POWER((RD + RE),2)) ) / (2 * B.L * (RD + RC)) * (LEN / B.L)
    			           ) - ( SQRT(1 - POWER(((POWER(B.L,2) + POWER((RD + RC),2) - POWER((RD + RE),2)) ) / (2 * B.L * (RD + RC)),2)) *
    			               (RE-RC) / B.L
    			        )),0) Y1
    			  FROM   (SELECT SQRT(POWER(LEN,2) + POWER((RE) - (RC),2)) L
    			            FROM   DUAL , T1) B			
    			     , T1 C
    			*/     
    			
    			JDTORecordSet jsCline1 = commDao.select(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilClineCheck1", logId, methodNm, "코일기울기 코일정보");
    			if (jsCline1.size() <= 0) {
        			szLogMsg =  "확인:"+sCoilNoD+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일기울기 코일정보검색 실패.";
    				commUtils.printLog(logId, szLogMsg, "SL");
    				commUtils.printLog(logId, methodNm, "S-");	
    				return YmConstant.RETN_CD_FAILURE;
    			}
    	
    			jsCline1.first();
    			JDTORecord jrCline1 = JDTORecordFactory.getInstance().create();
    			jrCline1 = jsCline1.getRecord();
     	    	iY1 = commUtils.paraRecChkNullInt(jrCline1, "Y1");
     			
    			szLogMsg =  "확인:"+sCoilNoD+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ")=>>Y1:"+iY1 ;
				commUtils.printLog(logId, szLogMsg, "SL");
				
     	    	if (lngsOutDiaB >= lngsOutDiaC) {
     	    		 /* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilClineCheck2 
     	    		-- RB >= RC 최종 QUERY
     	    		WITH T1 AS ( 
     	    		    SELECT TO_NUMBER(:V_GRP_GP_LOC) LEN   --1950
     	    			     , TO_NUMBER(:V_COIL_OUTDIA_A) / 2 RA --1827
     	    				 , TO_NUMBER(:V_COIL_OUTDIA_B) / 2 RB --1782
     	    				 , TO_NUMBER(:V_COIL_OUTDIA_C) / 2 RC --1728
     	    				 , TO_NUMBER(:V_COIL_OUTDIA_D) / 2 RD --1745
     	    				 , TO_NUMBER(:V_COIL_OUTDIA_E) / 2 RE --1902
     	    			  FROM DUAL 
     	    		) 
     	    		SELECT ROUND((RA + RC) * ((((POWER(A.L,2) + POWER((RA + RC),2) - POWER((RA + RB),2)) ) / (2 * A.L * (RA + RC)) * (LEN / A.L)) 
     	    		                     - (SQRT(1 - POWER(((POWER(A.L,2) + POWER((RA + RC),2) - POWER((RA + RB),2)) ) / (2 * A.L * (RA + RC)),2)) * (RB - RC) / A.L 
     	    		               )),0) Y2 
     	    		  FROM  (SELECT SQRT(POWER(LEN,2) + POWER((RB) - (RC),2)) L 
     	    		             FROM DUAL , T1) A 
     	    					, T1 B   
     	    		*/			
     	   			JDTORecordSet jsCline2 = commDao.select(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilClineCheck2", logId, methodNm, "코일기울기 코일정보");
        			if (jsCline2.size() <= 0) {
            			szLogMsg =  "확인:"+sCoilNoD+"적치단(" + szStackGp + szStackBedGp + szStackLayer + ") 코일기울기 코일정보검색 실패.";
        				commUtils.printLog(logId, szLogMsg, "SL");
        				commUtils.printLog(logId, methodNm, "S-");	
        				return YmConstant.RETN_CD_FAILURE;
        			}
        			jsCline2.first();
        			JDTORecord jrCline2 = JDTORecordFactory.getInstance().create();
        			jrCline2 = jsCline2.getRecord();
         	    	iY2 = commUtils.paraRecChkNullInt(jrCline2, "Y2");
         	    	
         	    	szLogMsg =  "확인:"+sCoilNoD+"적치단(" + szStackGp + szStackBedGp + szStackLayer + "))=>>Y2:"+iY2 ;
    				commUtils.printLog(logId, szLogMsg, "SL");
    				
         	    	szLogMsg =  "확인:"+sCoilNoD+"적치단(" + szStackGp + szStackBedGp + szStackLayer + "))=>>Y1+Y2:" + (iY1 + iY2 ) ;
    				commUtils.printLog(logId, szLogMsg, "SL");

    				
         	    	szLogMsg =  "확인:"+sCoilNoD+"적치단(" + szStackGp + szStackBedGp + szStackLayer +")=>>lngsOUTDIA_A+lngsOUTDIA_D/2:" + (((lngsOutDiaA + lngsOutDiaD)/2) + 50) ;
    				commUtils.printLog(logId, szLogMsg, "SL");
         	    	
         	    	if ((iY1 + iY2) > (((lngsOutDiaA + lngsOutDiaD)/2) + 50)) {
         	    		commUtils.printLog(logId, methodNm, "S-");	
         	    		return YmConstant.RETN_CD_SUCCESS;
         	    	} else {
         	    		commUtils.printLog(logId, methodNm, "S-");	
         	    		return YmConstant.RETN_CD_FAILURE;
        			}

    			} else {
    				commUtils.printLog(logId, methodNm, "S-");	
    				return  YmConstant.RETN_CD_FAILURE;
    			}
     
			} else {
				commUtils.printLog(logId, methodNm, "S-");	
				return YmConstant.RETN_CD_FAILURE;
			}
			
 		} catch(Exception e) {

 			szLogMsg =  "확인:"+sCoilNoD+"적치단(" + szStackGp + szStackBedGp + szStackLayer + "기울기 편차 CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");

			commUtils.printLog(logId, methodNm, "S-");	
			return YmConstant.RETN_CD_FAILURE;
			
		}
		
	} 			
	

	/**
	 *      [A] 오퍼레이션명 : 2단 코일야드중량/폭 편차Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdWtWidCheck_2(String logId, String methodNms,String szStockId,     String szThick,     String szWidth,     String szWeigth
			                                                        ,String szLeftStockId, String szLeftThick, String szLeftWidth, String szLeftWeigth
			                                                        ,String szRightStockId,String szRightThick,String szRightWidth,String szRightWeigth
			                                                        ,String ruleWidDeff2  ,String ruleWgtDeff2) throws JDTOException  {

		String methodNm = "2단 코일야드중량/폭 편차Check[BCoilSchSeEJB.procCoilYdWtWidCheck_2] < " + methodNms;
		String szLogMsg = "";
		String szLogMsg1 = "";
		JDTORecord  jrRnt   = JDTORecordFactory.getInstance().create();	
		double dblEndWeigth  	= 0;  // 최소코일중량
		long   lngMinWeigth  	= 0;  // 최소코일중량
		double dblMinThick  	= 0;
		double dblMinWidth  	= 0;  //
		double dblMaxWidth  	= 0;  //
		double dblChkVal  		= 0;  // 
		
		commUtils.printLog(logId, methodNm, "S+");	
		try {
			
			if ("".equals(szLeftStockId) || "".equals(szRightStockId)) {
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
				return jrRnt;
			}
			double dblThick 		= Double.parseDouble(commUtils.nvl(szThick,"0"));
			double dblWidth 		= Double.parseDouble(commUtils.nvl(szWidth,"0"));
			long lngWeigth 			= Long.parseLong(commUtils.nvl(szWeigth,"0"));
			double lngruleWidDeff2 	= Double.parseDouble(commUtils.nvl(ruleWidDeff2,"0")); // 폭 가중치
			long lngruleWgtDeff2    = Long.parseLong(commUtils.nvl(ruleWgtDeff2,"0")); // 중량 가중치
			
			double dblLeftThick 	= Double.parseDouble(commUtils.nvl(szLeftThick,"0"));
			double dblLeftWidth 	= Double.parseDouble(commUtils.nvl(szLeftWidth,"0"));
			long lngLeftWeigth 		= Long.parseLong(commUtils.nvl(szLeftWeigth,"0"));
			
			double dblRightThick	= Double.parseDouble(commUtils.nvl(szRightThick,"0"));
			double dblRightWidth	= Double.parseDouble(commUtils.nvl(szRightWidth,"0"));
			long lngRightWeigth		= Long.parseLong(commUtils.nvl(szRightWeigth,"0")); 
			

			if (lngLeftWeigth >= lngRightWeigth) {
				lngMinWeigth = lngRightWeigth;  //1단 중량
				dblMinThick  = dblRightThick;   //1단 두께
			} else {
				lngMinWeigth = lngLeftWeigth;
				dblMinThick  = dblLeftThick;
			}
 			
			if (dblLeftWidth >= dblRightWidth) {
				dblMinWidth = dblRightWidth;
				dblMaxWidth = dblLeftWidth;
			} else {
				dblMinWidth = dblLeftWidth;
				dblMaxWidth = dblRightWidth;
			}
   
			szLogMsg =  "확인:"+szStockId+"=>>대상코일중량,두께,폭:"+lngWeigth+","+dblThick +","+dblWidth ;
			commUtils.printLog(logId, szLogMsg, "SL");
			
			szLogMsg =  "확인:"+szStockId+":1단좌측재료번호(" + szLeftStockId + ")=>>좌측중량,두께,폭:"+lngLeftWeigth+","+dblLeftThick+","+dblLeftWidth  ;
			commUtils.printLog(logId, szLogMsg, "SL");
			
			szLogMsg =  "확인:"+szStockId+ ":1단우측재료번호(" + szRightStockId + ")=>>우측중량,두께,폭:"+lngRightWeigth+","+dblRightThick+","+dblRightWidth  ;
			commUtils.printLog(logId, szLogMsg, "SL");	
			
			szLogMsg =  "확인:"+szStockId+ ":소 중량,소두께,소폭,큰폭:"+lngMinWeigth+","+dblMinThick+","+dblMinWidth+","+dblMaxWidth;
			commUtils.printLog(logId, szLogMsg, "SL");	

			szLogMsg =  "확인:"+szStockId+ ":폭가중치:"+lngruleWidDeff2+":중량가중치:"+lngruleWgtDeff2;
			commUtils.printLog(logId, szLogMsg, "SL");	
			
			if  (dblMinThick < 7  ) {
				if  (dblMinWidth < 1301  ) {
					dblChkVal = lngMinWeigth * 0.13;
				} else {
					dblChkVal = lngMinWeigth * 0.14;
				}	
			} else {
				if  (dblMinWidth < 1301  ) {
					dblChkVal = lngMinWeigth * 0.14;
				} else {
					dblChkVal = lngMinWeigth * 0.15;
				}	
			}
			
			dblEndWeigth = lngMinWeigth + dblChkVal;

			
			if (lngWeigth <= (dblEndWeigth+lngruleWgtDeff2) ) {  //중량 가중치를 더함
				
				if (dblWidth <= (dblMaxWidth+lngruleWidDeff2)) { // 폭 가중치를 더함  
					jrRnt.setField("RTN_CD" 	, "SUCCESS");	

				} else {
					jrRnt.setField("RTN_CD" 	, "WID_FAILURE");	
					szLogMsg1 = szLogMsg1 + "  --> 대상폭(" + dblWidth + ")이  좌우 MAX폭("+dblMaxWidth + ") + 폭가중치("+lngruleWidDeff2+") 보다 큼" ;
					jrRnt.setField("RTN_MSG" 	, szLogMsg1);
				}

			} else {
				jrRnt.setField("RTN_CD" 	, "WGT_FAILURE");	
				szLogMsg1 = szLogMsg1 + "  --> 대상중량(" + lngWeigth  + ")이   기준중량("+dblEndWeigth + ") + 중량가중치("+lngruleWgtDeff2+") 보다 큼";
				jrRnt.setField("RTN_MSG" 	, szLogMsg1);

			}
				
			commUtils.printLog(logId, methodNm, "S-");	
			return jrRnt;
			
		} catch(Exception e) {
 			szLogMsg =  "확인:"+szStockId+ "코일창고야드중량Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			
			jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			return jrRnt ;
			
		}
		
	} // end of procCoilYdWtWidCheck_2	


	/**
	 *      [A] 오퍼레이션명 : 2단 코일야드외경간격Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdOutDiaIntervalCheck_2(String logId, String methodNms,String szStockId,String szStackGp, String szStackBedGp , String szStackLayer, String szOutDia
			                                              , String szLeftStockId,String szLeftOutDia
			                                              , String szRightStockId,String szRightOutDia
			                                              , String ruleODiaDeff2, String ruleBedDeff2) throws JDTOException  {
		String methodNm = "2단 코일야드외경간격Check[BCoilSchSeEJB.procCoilYdOutDiaIntervalCheck_2] < " + methodNms;
		String szLogMsg = "";
		String szLogMsg1 = "";
		
		JDTORecord  jrRnt   = JDTORecordFactory.getInstance().create();	
		
		commUtils.printLog(logId, methodNm, "S+");	
		try {
			
			if ("".equals(szLeftStockId) || "".equals(szRightStockId)) {
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
				return jrRnt;
			}
			
			
        	//권하대상코일 외경 ;
			long lngOutDia 			= Long.parseLong(commUtils.nvl(szOutDia,"0"));
			long lngLeftOutDia 		= Long.parseLong(commUtils.nvl(szLeftOutDia,"0"));
			long lngRightOutDia 	= Long.parseLong(commUtils.nvl(szRightOutDia,"0"));
        	
			long lngDiaDiffConst = Long.parseLong(commUtils.nvl(ruleODiaDeff2,"550"));//550;     
			long lngBedLength    = Long.parseLong(commUtils.nvl(ruleBedDeff2,"1300"));//1300; 

			szLogMsg =  "확인:"+szStockId+")=>>좌측코일외경:"+lngLeftOutDia ;
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
			szLogMsg =  "확인:"+szStockId+")=>>우측코일외경:"+lngRightOutDia ;
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;

			szLogMsg =  "확인:"+szStockId+ "대상코일외경군(" + "외경간격상수:"+lngDiaDiffConst;
			commUtils.printLog(logId, szLogMsg, "SL");	
			szLogMsg1 = szLogMsg1 + szLogMsg;

			
        	long lngLength = lngOutDia -(lngBedLength - ((lngLeftOutDia + lngRightOutDia) / 2));
        	
			if (lngLength > lngDiaDiffConst) {
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
        	} else {
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);
				szLogMsg1 = szLogMsg1 + "  --> 외경간격 상수 값보다 큼" + "베드길이 - (좌외경 + 우외경 )/2 값 "+ lngLength   ;
				jrRnt.setField("RTN_MSG" 	, szLogMsg1);
				
        	}
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRnt;			
		} catch(Exception e) {
			szLogMsg =  "코일창고야드외경간격Check 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			return jrRnt;		
		}
		
	} // end of procCoilYdOutDiaIntervalCheck_2    
    
	
	/**
	 *      [A] 오퍼레이션명 : 2단 외경편차 Check
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdOutDiaDiffCheck_2(String logId, String methodNms,String szStockId,  String szStackLayer, String szOutDia
			                                              , String szLeftStockId,String szLeftOutDia
			                                              , String szRightStockId,String szRightOutDia,String ruleODiaDeff1) throws JDTOException  {
		
		String methodNm = "2단 코일야드외경간격Check[BCoilSchSeEJB.procCoilYdOutDiaDiffCheck_2] < " + methodNms;
		String szLogMsg = "";		
		String szLogMsg1 = "";		
		JDTORecord  jrRnt   = JDTORecordFactory.getInstance().create();	
		
		String szRtnVal = YmConstant.RETN_CD_FAILURE;
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			if ("".equals(szLeftStockId) || "".equals(szRightStockId)) {
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
				return jrRnt;				
			}
//외경편차			
			long lngOutDia 		 = Long.parseLong(commUtils.nvl(szOutDia,"0"));
			long lngLeftOutDia 	 = Long.parseLong(commUtils.nvl(szLeftOutDia,"0"));
			long lngRightOutDia  = Long.parseLong(commUtils.nvl(szRightOutDia,"0"));
			long lngruleODiaDeff = Long.parseLong(commUtils.nvl(ruleODiaDeff1,"0"));

			szLogMsg =  "확인:"+szStockId+")=>>대상코일외경:"+lngOutDia ;
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
			szLogMsg =  "확인:"+szStockId+")=>>좌측코일외경:"+lngLeftOutDia ;
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
			szLogMsg =  "확인:"+szStockId+ ")=>>우측코일외경:"+lngRightOutDia ;
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
			
			
			if (Math.abs(lngLeftOutDia - lngRightOutDia) < lngruleODiaDeff) {  
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
			} else {
				jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);
				szLogMsg1 = szLogMsg1 + "  --> 좌우 외경 차이가 " + Math.abs(lngLeftOutDia - lngRightOutDia) +  " 기준:"+  lngruleODiaDeff +" 보다 큼 "  ;
				jrRnt.setField("RTN_MSG" 	, szLogMsg1);
			}
			
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRnt;		
		} catch(Exception e) {
			szLogMsg =  "외경CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");

			jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);
			return jrRnt;
		}
		
	} 
	
	
	/**
	 *      [A] 오퍼레이션명 : 대차스케줄 공대차출발지시 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord updTcarSchLevWo(JDTORecord jrParam) throws DAOException {
		String methodNm = "대차스케줄 공대차출발지시 처리[GdsYsSchSeEJB.updTcarSchLevWo] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {

			return commUtils.addSndData(YmComm.trtTcarSchLevWo(jrParam));
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	} 
	
	
	/**	
	 *      [A] 오퍼레이션명 : LINE-OFF 긴급작업(YMYMJ304)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ304(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "LINE-OFF긴급작업 [BCoilSchSeEJB.rcvYMYMJ304] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn 			= JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecordSet rsResult    	= null;
		String ydCrnSchIdWrk = "";
		
		try {
			
			//LINE-OFF 긴급작업 사용여부 확인 시작 --------------------------------------
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getLineOffUgntWrkEffYn", logId, methodNm, "B열연 LINE-OFF 긴급작업 사용여부");
			if (rsResult.size() <= 0) {
				return jrRtn;
			} else {
				if ("N".equals(commUtils.nvl(rsResult.getRecord(0).getFieldString("LINE_OFF_UGNT_EFF_YN"   ),"N"))) {
					return jrRtn;
				}
			}
			//LINE-OFF 긴급작업 사용여부 확인 종료 --------------------------------------
			
			commUtils.printLog(logId, methodNm, "S+");

	    	//수신항목 변수 저장
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //설비번호(크레인번호)
			String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //스케줄코드
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); //야드작업예약ID

			//SJH 추가 
			
			String sAPPLY008 = YmComm.BCoilApplyYn("APP008","3","1");
			commUtils.printLog(logId,  "자동 LINE OFF  처리:" + sAPPLY008, "SL");
			if (sAPPLY008.equals("Y")) {
				if (YmComm.chkAutoCrn(ydEqpId) ) {
					jrRtn = this.rcvYMYMJ304Auto(rcvMsg);
					return jrRtn;
				} 
			}	
			
			
			
			String msgId	= commUtils.nvl(commUtils.getMsgId(rcvMsg),"YMYMJ304"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//해당크레인 크레인스케줄에 같은 스케줄코드로 상태가 S,1 인 경우 긴급작업으로
			//마지막으로 만들어진(최신) 스케줄을 기동시킨다. (이전스케줄 삭제 전문 발송)
			
			//가장 최근에 만들어진 크레인 스케줄ID를 가져온다.
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLastCrnSchId 
			SELECT YD_CRN_SCH_ID 
			FROM  TB_YM_CRNSCH
			WHERE YD_SCH_CD = :V_YD_SCH_CD
			AND   YD_EQP_ID = :V_EQP_ID
			AND   DEL_YN = 'N'
			AND   YD_WRK_PROG_STAT IN ('W')
			ORDER BY YD_CRN_SCH_ID DESC */
			jrParam.setField("YD_EQP_ID"	, ydEqpId); 	
			jrParam.setField("YD_SCH_CD"	, ydSchCd); 
			jrParam.setField("YD_WBOOK_ID"	, ydWbookId);
			jrParam.setField("YD_SCH_PRIOR"		, "0");           //신규
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLastCrnSchId", logId, methodNm, ""); 
			
			if (rsResult.size() > 0) {
				
			    String ydCrnSchId 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
	
				commUtils.printLog(logId, "Line-Off 긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + " >> " + ydCrnSchId +" >> " + ydSchCd + " ]", "SL");
	
			
			    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchId );
	
			    /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnWrkMgtPriorWrkLineOff  
			    SELECT YD_CRN_SCH_ID
			      FROM (
			            SELECT YD_CRN_SCH_ID
			                 , COUNT(*)  AS CRN_WRK_CNT
			              FROM TB_YM_CRNSCH
			             WHERE YD_SCH_CD = :V_YD_SCH_CD
			               AND YD_EQP_ID = :V_YD_EQP_ID
			               AND YD_WRK_PROG_STAT IN ('1', 'S')
			               AND DEL_YN = 'N'   
			             GROUP BY YD_CRN_SCH_ID  
			            )
			     WHERE CRN_WRK_CNT = 1   */   
			    jrParam.setField("YD_SCH_CD"	, ydSchCd);
			    jrParam.setField("YD_EQP_ID"	, ydEqpId );
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnWrkMgtPriorWrkLineOff", logId, methodNm, "크레인변경 조회");
	
				// 기존 작업 우선순위 변경
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt 
				--크레인작업관리 크레인변경 크레인스케줄 수정
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				      ,MOD_DDTT     = SYSDATE
				      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
				      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
				 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
				   AND YD_WRK_PROG_STAT IN ('1','W','S')
				   AND DEL_YN = 'N' */
				jrParam.setField("YD_SCH_PRIOR"	, "0");
				jrParam.setField("YD_EQP_ID"	, ydEqpId); 	
				jrParam.setField("YD_WBOOK_ID" 	, ydWbookId );
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt", logId, methodNm,  "TB_YM_CRNSCH");
				
				if (jsCrn.size() == 0) {
					
			    } else {
			    	
			    	JDTORecord jrCrn = jsCrn.getRecord(0);
				    ydCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"));
				    
			    	/*
					SELECT YD_EQP_WRK_MODE2
					  FROM TB_YM_EQUIP
					 WHERE DEL_YN = 'N'
					   AND EQUIP_GP = :V_YD_EQP_ID
			    	 */
			    	rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.ChkCrnMode2");
			    	if (rsResult.size() > 0) {
			    		
			    		String sYD_EQP_WRK_MODE2 = rsResult.getRecord(0).getFieldString("YD_EQP_WRK_MODE2");
			    		
			    		if ("A".equals(sYD_EQP_WRK_MODE2)||"R".equals(sYD_EQP_WRK_MODE2)) {
	
			    			/******************************************************
			    			 * 긴급작업시 Auto크레인 우선순위 변경후 아무것도 안함
			    			 ******************************************************/
			    			return jrRtn;
			    			
			    		} 
			    		if ("M".equals(sYD_EQP_WRK_MODE2)) {			    			
			    			/******************************************************
			    			 * 유인 긴급작업일 경우 명령선택 기동(기존작업)
			    			 ******************************************************/
						    jrParam.setField("YD_CRN_SCH_ID" 	, ydCrnSchIdWrk );
						    
						    
						    /**** 기존 작업 지시 정리 ***********/
							//크레인스케줄 Table 크레인ID, 우선순위 Update, 
						    /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1 
						    UPDATE TB_YM_CRNSCH A
						       SET MODIFIER         = :V_MODIFIER
						         , MOD_DDTT         = SYSDATE
						         , YD_WRK_PROG_STAT = 'W'
						    	 , YD_WORD_DT       = NULL 
						    	 , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR FROM USRYMA.TB_YM_SCHEDULERULE B
							                            WHERE B.YD_SCH_CD=A.YD_SCH_CD)
						     WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
						       AND DEL_YN = 'N'       
						     */   
						    jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchIdWrk);
							commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1", logId, methodNm,  "TB_YM_CRNSCH");						    
			    		}// 'M'
			    	}
			    	
					//신규 작업예약 Table 우선순위 Update
			    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPrior1 
			    	--작업예약 스케쥴우선순위 수정
			    	UPDATE TB_YM_WRKBOOK
			    	   SET MODIFIER     = :V_MODIFIER
			    	     , MOD_DDTT     = SYSDATE
			    	     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
			    	 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
			    	   AND DEL_YN       = 'N'

					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updWrkBookPrior1", logId, methodNm, "TB_YM_WRKBOOK");				

					//신규 작업 우선순위 변경
					/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMg1Chk 
					UPDATE TB_YM_CRNSCH
					   SET MODIFIER     = :V_MODIFIER
					      ,MOD_DDTT     = SYSDATE
					--      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
					      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
					      ,YD_WRK_PROG_STAT = 'S' 
					 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
					   AND YD_WRK_PROG_STAT IN ('1','W','S')
					   AND DEL_YN = 'N'
					 */
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMg1Chk", logId, methodNm,  "TB_YM_CRNSCH");
						
					/**********************************************************
					* 3.2 신  크레인작업지시 요구 처리
					**********************************************************/

					//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YMA7L004);	//크레인작업지시요구
					jrYdMsg.setField("MSG_GP"          , "I");	//야드설비ID
					jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L004", jrYdMsg));	
			    	
			    }
			}
			
			commUtils.printLog(logId, methodNm , "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	

      
	/**
	 * 기본 BASE CHECK
	 * 적치위치 변경 에서 사용
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public String procStockIdBaseCheck(String logId, String methodNms,JDTORecord jrLayer) throws JDTOException {
    	String methodNm = "기본 BASE CHECK[BCoilSchSeEJB.procStockIdBaseCheck] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String StockId	   	= commUtils.trim(jrLayer.getFieldString("STOCK_ID"));			//크레인작업재료
		String ydSchCd      = commUtils.trim(jrLayer.getFieldString("YD_SCH_CD"));		    //스케쥴 코드
		String ydCrnSchId   = commUtils.trim(jrLayer.getFieldString("YD_CRN_SCH_ID"));		//스케쥴 ID
		String StackColGp 	= commUtils.trim(jrLayer.getFieldString("STACK_COL_GP"));		//열
		String StackBedGp   = commUtils.trim(jrLayer.getFieldString("STACK_BED_GP"));		//BED
		String StackLayerGp = commUtils.trim(jrLayer.getFieldString("STACK_LAYER_GP"));		//단 
		String sRtnBedDan 	= "";  //TO위치	
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			if ( StackColGp.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			String sAPP005_YN = YmComm.BCoilApplyYn("APP005","3","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");
			
			JDTORecord jrToLocBaseCheckRtn = JDTORecordFactory.getInstance().create();
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, StockId);			
			jrTemp.setField("YD_SCH_CD"			, ydSchCd);			
			jrTemp.setField("STACK_COL_GP"		, StackColGp);		
			jrTemp.setField("STACK_BED_GP"		, StackBedGp);		
			jrTemp.setField("STACK_LAYER_GP"	, StackLayerGp);	
			
			szLogMsg =  "재료["+StockId +" +스케쥴 코드 : "+ ydSchCd + "열"+ StackColGp + "베드"+StackBedGp+ "단" + StackLayerGp + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStockIdBaseCheck
			WITH SPEC_TABLE AS (
			    -- 특정규격CHECK 
			    SELECT DTL_ITM1 AS STACK_RULE_NAME 
			      FROM USRYMA.TB_YM_RULE
			     WHERE REPR_CD_GP = 'YM004'
			       AND DEL_YN = 'N'
			)
			, TO_LOC_TABLE AS
			(
			-- 대상 위치 SELECT
			SELECT A.STACK_COL_GP               AS TAG_STACK_COL_GP
			     , A.STACK_BED_GP               AS TAG_STACK_BED_GP
			     , A.STACK_LAYER_GP             AS TAG_STACK_LAYER_GP
			     , B.STACK_COL_USAGE_CD         AS TAG_COL_USE_CD
			     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
			                                '02', A.STACK_BED_GP)                                AS TAG_LEFT_BED
			     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
			                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_LEFT_LAYER
			     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
			                                '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS TAG_RIGHT_BED
			     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
			                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_RIGHT_LAYER
			      -- 단 우선순위
			     , C.JJANG_GP  
			     , C.STOCK_ID
			  FROM TB_YM_STACKLAYER A
			     , TB_YM_STACKCOL B
			     , (SELECT CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=C.HR_SPEC_ABBSYM) --특정규격존재                                 
			                     AND NVL(C.NEXT_PROC,1) NOT IN ('5K','6K') --정정대상                                  
			                     AND TRUNC((SYSDATE - C.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						        THEN 'Y' 
						        ELSE 'N' END  AS JJANG_GP
			              , C.COIL_NO  AS STOCK_ID
			         FROM USRPTA.TB_PT_COILCOMM C
			        WHERE C.COIL_NO = :V_STOCK_ID  
			       )C
			 WHERE SUBSTR(A.STACK_COL_GP,1,1)  = '3'
			   AND A.STACK_COL_GP         = :V_STACK_COL_GP     --가이드 열
			   AND A.STACK_BED_GP         = :V_STACK_BED_GP
			   AND A.STACK_LAYER_GP       = :V_STACK_LAYER_GP
			   AND A.STACK_LAYER_ACTIVE_STAT= 'E'
			   AND A.STACK_LAYER_STAT       = 'E'
			   AND A.STACK_COL_GP           = B.STACK_COL_GP
			
			) 
			, TO_LOC_DATA_TABLE AS (
			-- TO 위치코일정보 SELECT
			SELECT A.STOCK_ID           AS STOCK_ID
			     , A.TAG_STACK_COL_GP
			     , A.TAG_STACK_BED_GP
			     , A.TAG_STACK_LAYER_GP
			     , A.TAG_LEFT_BED
			     , A.TAG_LEFT_LAYER
			     , A.JJANG_GP
			     , (SELECT STACK_LAYER_ACTIVE_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_ACTIVE_STAT 
			     , (SELECT STACK_LAYER_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_LAYER_STAT
			     , (SELECT STOCK_ID 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_STOCK_ID
			     , A.TAG_RIGHT_BED
			     , A.TAG_RIGHT_LAYER
			     , (SELECT STACK_LAYER_ACTIVE_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_ACTIVE_STAT 
			     , (SELECT STACK_LAYER_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_LAYER_STAT
			     , (SELECT STOCK_ID 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_STOCK_ID
			  FROM TO_LOC_TABLE A
			)
			SELECT KK.*
			  FROM
			        (
			        SELECT K.* 
			          FROM TO_LOC_DATA_TABLE K
			         WHERE 1  = CASE WHEN TAG_STACK_LAYER_GP = '01'                     THEN 1
			                         WHEN TAG_STACK_LAYER_GP = '02' AND JJANG_GP = 'Y'  THEN 0  -- 짱구코일 2단제외
			                         WHEN TAG_STACK_LAYER_GP = '02'                             -- 2단일 경우 좌우 적치 상태 CHECK
			                              AND TAG_LEFT_ACTIVE_STAT = 'E' AND TAG_RIGHT_ACTIVE_STAT = 'E' 
			                              AND TAG_LEFT_LAYER_STAT  = 'C' AND TAG_RIGHT_LAYER_STAT  = 'C' THEN 1
			                    ELSE 0 END  
			        ) KK   
			
			*/ 
			
			JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStockIdBaseCheck", logId, methodNm, "사용자TOSQL 베드 조회");
			if (outjsResult.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
	
			String szToPosGrade	= "";
			String szDBLogMsg	= "";
			
			JDTORecord	jrResult		= JDTORecordFactory.getInstance().create();
		    //----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSchSule = this.procSchSule(logId, methodNms);	
			
			for (int i = 1; i <= outjsResult.size(); i++) {
	
				outjsResult.absolute(i);
				jrResult  = outjsResult.getRecord();


				jrResult.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
				jrResult.setField("YD_SCH_CD"	 , 	ydSchCd); 	
				jrSchSule.setField("SCH_LOG"	 , 	sAPP005_YN); 	
				
				jrToLocBaseCheckRtn = this.procGradeLocAble(logId, methodNm, jrResult, jrSchSule);
				
				szToPosGrade = commUtils.trim(jrToLocBaseCheckRtn.getFieldString("GRIDE"  ));
				szDBLogMsg = szDBLogMsg + commUtils.trim(jrToLocBaseCheckRtn.getFieldString("GRADE_CONTENTS"  ) );
				
        		if (!szToPosGrade.equals(YmConstant.RETN_CD_FAILURE)) {
        			sRtnBedDan = StackColGp + StackBedGp + StackLayerGp;
        		}	
			}
			if (sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 적치기준 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				if (sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, StockId);
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "3");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택실패:"+ StockId+" LOG :"+"\r\n" + szDBLogMsg);
	    			    			
	    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				return YmConstant.RETN_CD_FAILURE;				
			}

			if (sAPP005_YN.equals("Y")) {
    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    			jrLog.setField("STOCK_ID"		, StockId);
    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
    			jrLog.setField("YD_GP"			, "3");
    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택:"+ StockId+" 선택위치:"+ StackColGp + StackBedGp + StackLayerGp+ " LOG :"+"\r\n" + szDBLogMsg);
    			    			
    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
			}

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}      
  
    
	/**
	 *      [A] 오퍼레이션명 : 스케줄 To위치 로그 Log
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void insSchLog(JDTORecord jrParam) throws DAOException {
		String methodNm = "스케줄 To위치 로그 Log[BCoilSchSeEJB.insSchLog] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insSchLog 
			INSERT INTO TB_YM_SCHLOG
			     ( YM_SCHLOG_ID_SEQ
				 , STOCK_ID
				 , YD_CRN_SCH_ID
				 , YD_GP
				 , YD_SCH_CD
				 , REGISTER
				 , REG_DDTT
				 , MODIFIER
				 , MOD_DDTT
				 , DEL_YN
				 , SCH_CONTENTS
			     )
			VALUES
			     ( YM_SCHLOG_ID_SEQ.NEXTVAL
				 , :V_STOCK_ID
				 , :V_YD_CRN_SCH_ID
				 , :V_YD_GP
				 , :V_YD_SCH_CD
				 , 'log'
				 , SYSDATE
				 , 'log'
				 , SYSDATE
			     , 'N'	 
				 , :V_SCH_CONTENTS
			     )
			*/     
			
			String sAPP310_YN = YmComm.BCoilApplyYn("APP310","3","*");   
			commUtils.printLog(logId,  "실좌표적용여부:" + sAPP310_YN, "SL");	
			if(sAPP310_YN.equals("Y") ) {
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.insSchLog", logId, methodNm, "스케줄 To위치 로그");
			}
			
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * TO위치 RULE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procSchSule(String logId, String methodNms) throws JDTOException {
    	String methodNm = "TO위치 RULE [BCoilSchSeEJB.procSchSule] < " + methodNms;
		JDTORecord jrResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrOutResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrParam   = commUtils.getParam(logId, methodNm, "");
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			jrParam.setField("REPR_CD_GP", "SCH_TO"  ); //작업구분
			jrParam.setField("CD_GP"     , "3"      ); 	//공장구분
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchToLocRule
			SELECT REPR_CD_GP,CD_GP,ITEM,REPR_CD_CONTENTS,DTL_ITM1
			  FROM USRYMA.TB_YM_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- SCH_TO
			   AND CD_GP = :V_CD_GP            -- 3
			   AND DEL_YN = 'N'
			*/  
			JDTORecordSet jsSchToLocRule = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchToLocRule", logId, methodNm, "스케줄 기준 Read"); 
		    // 평점 CEHCK	
			for (int i = 1; i <= jsSchToLocRule.size(); i++) {
	 
				jsSchToLocRule.absolute(i);
				jrResult  = jsSchToLocRule.getRecord();
				if ("ODIA_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 1단외경편차
					jrOutResult.setField("ODIA_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "180")); 
	        	}
				if ("WID_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 1단폭편차
					jrOutResult.setField("WID_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "200")); 
	        	}
				if ("ODIA_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 2단외경간격
					jrOutResult.setField("ODIA_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "550")); 
	        	}
				if ("BED_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 2단BED길이
					jrOutResult.setField("BED_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1300")); 
	        	}
				if ("WID_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 2단폭편차
					jrOutResult.setField("WID_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "0")); 
	        	}
				if ("WGT_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 2단중량편차
					jrOutResult.setField("WGT_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "0")); 
	        	}
				if ("WID_LIFT1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // LIFT 간격
					jrOutResult.setField("WID_LIFT1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "650")); 
	        	}
				if ("WID_SKID1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // SKID 폭
					jrOutResult.setField("WID_SKID1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1000")); 
	        	}
				if ("SKID_SKID".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // SKID 간격
					jrOutResult.setField("SKID_SKID", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1150")); 
	        	}
			}
			
			String sAPP044_A_YN = YmComm.BCoilApplyYn("APP044","3","A");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_B_YN = YmComm.BCoilApplyYn("APP044","3","B");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_C_YN = YmComm.BCoilApplyYn("APP044","3","C");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_D_YN = YmComm.BCoilApplyYn("APP044","3","D");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_E_YN = YmComm.BCoilApplyYn("APP044","3","E");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			jrOutResult.setField("WID_CHK_A", sAPP044_A_YN); 
			jrOutResult.setField("WID_CHK_B", sAPP044_B_YN); 
			jrOutResult.setField("WID_CHK_C", sAPP044_C_YN); 
			jrOutResult.setField("WID_CHK_D", sAPP044_D_YN); 
			jrOutResult.setField("WID_CHK_E", sAPP044_E_YN); 
        	

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jrOutResult;
	}      
    
    
	/**
	 * TO위치 RULE
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public JDTORecord procSchSule(String logId, String methodNms, String sSTOCK_ID, String sYD_SCH_CD) throws JDTOException {
    	String methodNm = "TO위치 RULE [BCoilSchSeEJB.procSchSule] < " + methodNms;
		JDTORecord jrResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrOutResult  = commUtils.getParam(logId, methodNm, "");
		JDTORecord jrParam   = commUtils.getParam(logId, methodNm, "");
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			jrParam.setField("REPR_CD_GP", "SCH_TO"  ); //작업구분
			jrParam.setField("CD_GP"     , "3"      ); 	//공장구분
			
			/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchToLocRule
			SELECT REPR_CD_GP,CD_GP,ITEM,REPR_CD_CONTENTS,DTL_ITM1
			  FROM USRYMA.TB_YM_RULE
			 WHERE REPR_CD_GP = :V_REPR_CD_GP  -- SCH_TO
			   AND CD_GP = :V_CD_GP            -- 3
			   AND DEL_YN = 'N'
			*/  
			JDTORecordSet jsSchToLocRule = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getSchToLocRule", logId, methodNm, "스케줄 기준 Read"); 
		    // 평점 CEHCK	
			for (int i = 1; i <= jsSchToLocRule.size(); i++) {
	 
				jsSchToLocRule.absolute(i);
				jrResult  = jsSchToLocRule.getRecord();
				if ("ODIA_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 1단외경편차
					jrOutResult.setField("ODIA_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "180")); 
	        	}
				if ("WID_DIFF1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 1단폭편차
					jrOutResult.setField("WID_DIFF1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "200")); 
	        	}
				if ("ODIA_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 2단외경간격
					jrOutResult.setField("ODIA_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "550")); 
	        	}
				if ("BED_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {     // 2단BED길이
					jrOutResult.setField("BED_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1300")); 
	        	}
				if ("WID_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 2단폭편차
					jrOutResult.setField("WID_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "0")); 
	        	}
				if ("WGT_DIFF2".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // 2단중량편차
					jrOutResult.setField("WGT_DIFF2", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "0")); 
	        	}
				if ("WID_LIFT1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // LIFT 간격
					jrOutResult.setField("WID_LIFT1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "650")); 
	        	}
				if ("WID_SKID1".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // SKID 폭
					jrOutResult.setField("WID_SKID1", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1000")); 
	        	}
				if ("SKID_SKID".equals(commUtils.trim(jrResult.getFieldString("ITEM")))) {      // SKID 간격
					jrOutResult.setField("SKID_SKID", 	commUtils.nvl(jrResult.getFieldString("DTL_ITM1"), "1150")); 
	        	}
			}
			
			String sAPP044_A_YN = YmComm.BCoilApplyYn("APP044","3","A");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_B_YN = YmComm.BCoilApplyYn("APP044","3","B");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_C_YN = YmComm.BCoilApplyYn("APP044","3","C");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_D_YN = YmComm.BCoilApplyYn("APP044","3","D");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			String sAPP044_E_YN = YmComm.BCoilApplyYn("APP044","3","E");   //1단 폭 CHECK -> 2단폭 CHECK로 변경
			jrOutResult.setField("WID_CHK_A", sAPP044_A_YN); 
			jrOutResult.setField("WID_CHK_B", sAPP044_B_YN); 
			jrOutResult.setField("WID_CHK_C", sAPP044_C_YN); 
			jrOutResult.setField("WID_CHK_D", sAPP044_D_YN); 
			jrOutResult.setField("WID_CHK_E", sAPP044_E_YN); 
        	
			//----------------------------------------------------------------------------------------------------------------------------------------
			jrParam.setField("YD_SCH_CD", sYD_SCH_CD  );
			jrParam.setField("STOCK_ID"	, sSTOCK_ID  );
			
			//1단 외경편차 계산 수행 여부 기준
			JDTORecordSet jsOutdiaDevCalActYn =  commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BCoilDAO.getOutdiaDevCalActYn", logId, methodNm, "1단 외경편차 계산 수행 여부 기준");

			if(jsOutdiaDevCalActYn.size() > 0) {

				jrOutResult.setField("STK_RT"	    			, commUtils.trim(jsOutdiaDevCalActYn.getRecord(0).getFieldString("STK_RT"))); 	
				jrOutResult.setField("RULE_APP054"	    		, commUtils.trim(jsOutdiaDevCalActYn.getRecord(0).getFieldString("RULE_APP054"))); 	
				jrOutResult.setField("OUTDIA_DEV_CAL_ACT_YN"	, commUtils.trim(jsOutdiaDevCalActYn.getRecord(0).getFieldString("OUTDIA_DEV_CAL_ACT_YN"))); 	
			} else {
				jrOutResult.setField("STK_RT"	    			, ""); 	
				jrOutResult.setField("RULE_APP054"	    		, ""); 	
				jrOutResult.setField("OUTDIA_DEV_CAL_ACT_YN"	, "Y"); 
			}
			//----------------------------------------------------------------------------------------------------------------------------------------

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return jrOutResult;
	}
    
    
	/**
	 * 대차에서 설비 직보급(사용안함)
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
    public String procToLocDirConveyor(String logId, String methodNms, JDTORecord jrWbook, JDTORecord jrCrnSch) throws JDTOException {
    	String methodNm = "TO위치결정:대차에서 설비 직보급[BCoilSchSeEJB.procToLocDirConveyor] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------

		String szYD_SCH_CD 	   		= commUtils.trim(jrWbook.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrWbook.getFieldString("YD_WBOOK_ID"));		//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrCrnSch.getFieldString("STOCK_ID"));			//크레인작업재료
		String szYD_CRN_SCH_ID 		= commUtils.trim(jrCrnSch.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrCrnSch.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrCrnSch.getFieldString("YD_UP_WO_LAYER"));
		String sRtnBedDan           = "";
		
		commUtils.printLog(logId, methodNm, "S+");
		try {
			
			if ( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			//HFL 대차 직보급만 
			if (szYD_SCH_CD.equals("3ATC01LM")) {
				
				sRtnBedDan = "3AFE010001";
			
				//----------------------------------------------------------------------------------------------------------------------
		    	// To위치 크레인 에 update 
				//----------------------------------------------------------------------------------------------------------------------
				JDTORecord jrSetLoc = JDTORecordFactory.getInstance().create();
				jrSetLoc.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID); 
				jrSetLoc.setField("YD_EQP_ID", 		szYD_EQP_ID);	 
				jrSetLoc.setField("YD_SCH_CD", 		szYD_SCH_CD);	 
				jrSetLoc.setField("YD_UP_WO_LOC", 	szYD_UP_WO_LOC); 
				jrSetLoc.setField("YD_UP_WO_LAYER",	szYD_UP_WO_LAYER);	 
				jrSetLoc.setField("YD_DN_WO_LOC", 	sRtnBedDan.substring(0,8));
				jrSetLoc.setField("YD_DN_WO_LAYER", sRtnBedDan.substring(8,10));
				jrSetLoc.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID); 
					
				this.procUpdateLoc(logId,methodNm, jrSetLoc, jrCrnSch  );
			}	
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}
    
    
	/**
	 *      [A] 오퍼레이션명 : DUMMY 평점분석
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procGradeAnalysisDummy( String logId, String methodNms, JDTORecord jrResult, JDTORecord jrSchRule) throws JDTOException {
    	String methodNm = "평점분석[BCoilSchSeEJB.procGradeAnalysisDummy] < " + methodNms;
    	String szLogMsg			= null;
    	String szGRIDE  		= "99";

    	String szSTOCK_ID 		= commUtils.trim(jrResult.getFieldString("STOCK_ID"  ));
    	String szSTACK_COL_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
		String szSTACK_BED_GP 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
		String szSTACK_LAYER_GP	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
		
		String szLEFT_STOCK_ID 	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_STOCK_ID"  ));	//2단일 경우 좌측
		String szLEFT_BED  		= commUtils.trim(jrResult.getFieldString("TAG_LEFT_BED"  ));		//2단일 경우 좌측
		String szLEFT_LAYER    	= commUtils.trim(jrResult.getFieldString("TAG_LEFT_LAYER"  ));		//2단일 경우 좌측
		
		String szRIGHT_STOCK_ID	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_STOCK_ID"  ));	//2단일 경우 우측		
		String szRIGHT_BED	   	= commUtils.trim(jrResult.getFieldString("TAG_RIGHT_BED"  ));		//2단일 경우 우측
		String szRIGHT_LAYER    = commUtils.trim(jrResult.getFieldString("TAG_RIGHT_LAYER"  ));		//2단일 경우 우측

		String szYD_CRN_SCH_ID  = commUtils.trim(jrResult.getFieldString("YD_CRN_SCH_ID"  ));		//크레인 스케쥴ID
		String szYD_SCH_CD      = commUtils.trim(jrResult.getFieldString("YD_SCH_CD"  ));		    //크레인 스케쥴코드
		
		String sAPP005_YN       = commUtils.trim(jrSchRule.getFieldString("SCH_LOG"  ));		    //스케줄 LOG여부
		
		commUtils.printLog(logId, methodNm, "S+");
		
		commUtils.printLog(logId, "대상코일위치:"+ szSTACK_COL_GP+szSTACK_BED_GP+szSTACK_LAYER_GP, "SL");
		commUtils.printLog(logId, "대상코일번호:"+ szSTOCK_ID + "좌측코일번호:"+ szLEFT_STOCK_ID+ "우측코일번호:"+ szRIGHT_STOCK_ID, "SL");
		
		JDTORecord jrLocAbleRtn = JDTORecordFactory.getInstance().create();
		JDTORecord jrGradeAnalysisRtn = JDTORecordFactory.getInstance().create();
		try {
						
			JDTORecord jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, szSTOCK_ID); 
			jrTemp.setField("STACK_COL_GP"		, szSTACK_COL_GP); 
			jrTemp.setField("STACK_BED_GP"		, szSTACK_BED_GP); 
			jrTemp.setField("STACK_LAYER_GP"	, szSTACK_LAYER_GP); 
			jrTemp.setField("LEFT_STOCK_ID"		, szLEFT_STOCK_ID);	 
			jrTemp.setField("RIGHT_STOCK_ID"	, szRIGHT_STOCK_ID);	 
			jrTemp.setField("TAG_STACK_LAYER_GP", szSTACK_LAYER_GP);	 
				
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGrideDummy
			WITH SPEC_TABLE AS (
			    -- 특정규격CHECK 
			    SELECT DTL_ITM1 AS STACK_RULE_NAME 
			      FROM USRYMA.TB_YM_RULE
			     WHERE REPR_CD_GP = 'YM004'
			       AND DEL_YN = 'N'
			),
			TEMP_DATA AS 
			(
			SELECT C.COIL_NO          AS C_COIL_NO
			     , C.CURR_PROG_CD     AS C_PROG_CD    -- 진도코드
			     , C.NEXT_PROC        AS C_NEXT_PROC  -- 차공정
			     , C.HR_SPEC_ABBSYM   AS C_SPEC_ABBSYM-- 규격약호
			     , C.ORD_NO           AS C_ORD_NO     -- 주문번호
			     , C.ORD_DTL          AS C_ORD_DTL    -- 주문행번
			     , C.RECEIPT_DATE     AS C_RECEIPT_DT -- 입고일자
			     , C.MILL_INI_DATE    AS C_MILL_DT    -- 압연일시
			     , C.DEMANDER_CD      AS C_DEMANDER_CD-- 수요가코드 
			     , C.COIL_T           AS C_THICK      -- 두께
			     , C.COIL_W           AS C_WIDTH      -- 폭
			     , C.COIL_WT          AS C_WEIGTH     -- 중량 
			     , C.COIL_OUTDIA      AS C_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=C.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(C.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - C.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS C_JJANG_GP  --짱구여부    
			     , L.COIL_NO          AS L_COIL_NO
			     , L.CURR_PROG_CD     AS L_PROG_CD    -- 진도코드
			     , L.NEXT_PROC        AS L_NEXT_PROC  -- 차공정
			     , L.HR_SPEC_ABBSYM   AS L_SPEC_ABBSYM-- 규격약호
			     , L.ORD_NO           AS L_ORD_NO     -- 주문번호
			     , L.ORD_DTL          AS L_ORD_DTL    -- 주문행번
			     , L.RECEIPT_DATE     AS L_RECEIPT_DT -- 입고일자
			     , L.MILL_INI_DATE    AS L_MILL_DT    -- 압연일시
			     , L.DEMANDER_CD      AS L_DEMANDER_CD-- 수요가코드 
			     , L.COIL_T           AS L_THICK      -- 두께
			     , L.COIL_W           AS L_WIDTH      -- 폭
			     , L.COIL_WT          AS L_WEIGTH     -- 중량 
			     , L.COIL_OUTDIA      AS L_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=L.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(L.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - L.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS L_JJANG_GP  --짱구여부    
			     , R.COIL_NO          AS R_COIL_NO
			     , R.CURR_PROG_CD     AS R_PROG_CD    -- 진도코드
			     , R.NEXT_PROC        AS R_NEXT_PROC  -- 차공정
			     , R.HR_SPEC_ABBSYM   AS R_SPEC_ABBSYM-- 규격약호
			     , R.ORD_NO           AS R_ORD_NO     -- 주문번호
			     , R.ORD_DTL          AS R_ORD_DTL    -- 주문행번
			     , R.RECEIPT_DATE     AS R_RECEIPT_DT -- 입고일자
			     , R.MILL_INI_DATE    AS R_MILL_DT    -- 압연일시
			     , R.DEMANDER_CD      AS R_DEMANDER_CD-- 수요가코드 
			     , R.COIL_T           AS R_THICK      -- 두께
			     , R.COIL_W           AS R_WIDTH      -- 폭
			     , R.COIL_WT          AS R_WEIGTH     -- 중량 
			     , R.COIL_OUTDIA      AS R_OUTDIA     -- 외경 
			     , CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=R.HR_SPEC_ABBSYM) --특정규격존재                                 
			             AND NVL(R.NEXT_PROC,1) NOT IN('5K','6K') --정정대상                                  
			             AND TRUNC((SYSDATE - R.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						THEN 'Y'  --짱구(적치불가)
						ELSE 'N' END  AS R_JJANG_GP  --짱구여부
			  FROM (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_STOCK_ID            ) C        --대상코일
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A
			         WHERE COIL_NO = :V_LEFT_STOCK_ID       ) L  --하단LEFT
			     , (SELECT 1 T_ROW, A.* 
			          FROM USRPTA.TB_PT_COILCOMM  A 
			         WHERE COIL_NO = :V_RIGHT_STOCK_ID      ) R  --하단RIGHT
			 WHERE C.T_ROW = L.T_ROW(+)          
			   AND C.T_ROW = R.T_ROW(+)      
			)
			SELECT CASE  -- 1단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8') 
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < 180  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < 180 
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < 200 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < 200
			            THEN CASE WHEN C_PROG_CD = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌우) 진도코드
			                      WHEN C_PROG_CD = L_PROG_CD                           THEN '2' --동일(좌단) 진도코드
			                      WHEN C_PROG_CD = R_PROG_CD                           THEN '2' --동일(우단) 진도코드
			                      WHEN L_COIL_NO IS NULL                               THEN '3' --좌 공BED
			                      WHEN R_COIL_NO IS NULL                               THEN '3' --우 공BED
			                      ELSE '9' END 
			             -- 2단이면서 소재인 경우     
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD NOT IN ('F','G','H','J','K','L','M','5','6','7','8')
			            THEN CASE WHEN L_JJANG_GP = 'Y'  OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN C_PROG_CD  = L_PROG_CD AND C_PROG_CD = R_PROG_CD THEN '1' --동일(좌하단우하단) 진도코드
			                      WHEN C_PROG_CD  = L_PROG_CD                           THEN '2' --동일(좌하단) 진도코드
			                      WHEN C_PROG_CD  = R_PROG_CD                           THEN '2' --동일(우하단) 진도코드
			                      ELSE '8' END 
			            -- 1단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '01' AND C_PROG_CD     IN ('F','G','H','J','K','L','M','5','6','7','8')   
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(L_OUTDIA) > 0 THEN  L_OUTDIA ELSE C_OUTDIA END)) < 180  --외경차이
			             AND ABS(TO_NUMBER(C_OUTDIA)-TO_NUMBER(CASE WHEN TO_NUMBER(R_OUTDIA) > 0 THEN  R_OUTDIA ELSE C_OUTDIA END)) < 180 
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(L_WIDTH)  > 0 THEN  L_WIDTH  ELSE C_WIDTH  END)) < 200 --폭차이
			             AND ABS(TO_NUMBER(C_WIDTH) -TO_NUMBER(CASE WHEN TO_NUMBER(R_WIDTH)  > 0 THEN  R_WIDTH  ELSE C_WIDTH  END)) < 200
			             
			            THEN CASE WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우축 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우측 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '3' --좌측   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '3' --우측   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우측 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '5' --우측   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '5' --좌측   동일 주문번호 
			                      WHEN L_COIL_NO IS NULL                               THEN '6' --좌측 공BED
			                      WHEN R_COIL_NO IS NULL                               THEN '6' --우측 공BED
			                      ELSE '9' END 
			            -- 2단이면서 제품인 경우           
			            WHEN :V_TAG_STACK_LAYER_GP = '02' AND C_PROG_CD IN ('F','G','H','J','K','L','M','5','6','7','8') --제품
			            THEN CASE WHEN L_JJANG_GP = 'Y' OR  R_JJANG_GP = 'Y'           THEN '9' -- 짱구 위에서 못 올린다.
			                      WHEN C_DEMANDER_CD = L_DEMANDER_CD             
			                       AND C_DEMANDER_CD = R_DEMANDER_CD 
			                       AND C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '1' --좌우하단 동일 고객사+주문번호행번
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL 
			                       AND C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '2' --좌우하단 동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = L_ORD_NO||L_ORD_DTL       THEN '3' --좌하단   동일 주문번호행번 
			                      WHEN C_ORD_NO||C_ORD_DTL = R_ORD_NO||R_ORD_DTL       THEN '3' --우하단   동일 주문번호행번 
			                      WHEN C_ORD_NO = L_ORD_NO AND C_ORD_NO = R_ORD_NO     THEN '4' --좌우하단 동일 주문번호 
			                      WHEN C_ORD_NO = L_ORD_NO                             THEN '5' --우하단   동일 주문번호 
			                      WHEN C_ORD_NO = R_ORD_NO                             THEN '5' --좌하단   동일 주문번호 
			                      ELSE '8' END 
			                       
			             ELSE '9' END GRIDE           
			     , C.*        
			  FROM TEMP_DATA C
			*/
			JDTORecordSet jsCommResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getGrideDummy", logId, methodNm, "코일공통 평점 조회");
			JDTORecord jrCommResult = JDTORecordFactory.getInstance().create();
			
			if (jsCommResult.size() == 1) {
				jsCommResult.absolute(1);
				jrCommResult  = jsCommResult.getRecord();
				
				szGRIDE = commUtils.nvl(jrCommResult.getFieldString("GRIDE"),"9");
					
				jrGradeAnalysisRtn.setField("GRIDE"		    , szGRIDE);
				jrGradeAnalysisRtn.setField("GRADE_RTN"		, "1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS", "LOC:"+szSTACK_COL_GP+szSTACK_BED_GP+ szSTACK_LAYER_GP+"적치가능 위치평점:"+ szGRIDE );
            	commUtils.printLog(logId, methodNm, "S-");
    			return jrGradeAnalysisRtn;
			} else {
				szLogMsg = methodNm+ "코일공통  평점 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
	        	commUtils.printLog(logId, methodNm, "S-");
				
    			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
    			jrGradeAnalysisRtn.setField("GRADE_CONTENTS"	, "코일공통  평점 검색 실패 ");
				return jrGradeAnalysisRtn;
			}
		} catch(Exception e) {
			
			szLogMsg = methodNm+ "코일창고야드위치평점항목Set 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrGradeAnalysisRtn.setField("GRIDE"		    , YmConstant.RETN_CD_FAILURE);
			jrGradeAnalysisRtn.setField("GRADE_RTN"		, "-1");
			return jrGradeAnalysisRtn;
		}
	} //   
	
	
	/**	
	 *      [A] 오퍼레이션명 : LINE-OFF 긴급작업(YMYMJ304)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvYMYMJ304Auto(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "자동 LINE-OFF긴급작업 [BCoilSchSeEJB.rcvYMYMJ304Auto] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn 			= JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecordSet rsResult    	= null;
		String ydCrnSchIdWrk = "";
		String ydBefCrnSchIdWrk = "";
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");

	    	//수신항목 변수 저장
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //설비번호(크레인번호)
			String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //스케줄코드
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); //야드작업예약ID

			String msgId	 = commUtils.nvl(commUtils.getMsgId(rcvMsg),"YMYMJ304"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier  = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			//가장 최근에 만들어진 크레인 스케줄ID를 가져온다.
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLastCrnSchIdAuto 
			SELECT YD_CRN_SCH_ID 
			  FROM TB_YM_CRNSCH
			 WHERE YD_SCH_CD = :V_YD_SCH_CD
			   AND YD_EQP_ID = :V_YD_EQP_ID
			   AND YD_WBOOK_ID = :V_YD_WBOOK_ID
			   AND DEL_YN = 'N'
			   AND YD_WRK_PROG_STAT IN ('W','S','1')
			 ORDER BY YD_CRN_SCH_ID DESC
			 */
			jrParam.setField("YD_EQP_ID"		, ydEqpId); 	
			jrParam.setField("YD_SCH_CD"		, ydSchCd); 
			jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
			jrParam.setField("YD_SCH_PRIOR"		, "0");           //신규
			rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLastCrnSchIdAuto", logId, methodNm, ""); 
			
			if (rsResult.size() > 0) {
				
			    String ydNewCrnSchId 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
	
				commUtils.printLog(logId, "Line-Off 긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + " >> " + ydNewCrnSchId +" >> " + ydSchCd + " ]", "SL");
	
			    //기존 크레인작업 지시 검색
			    /* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnWrkMgtPriorWrkLineOffAuto  
			    SELECT *
			      FROM (
			            SELECT YD_CRN_SCH_ID
			              FROM TB_YM_CRNSCH
			             WHERE YD_SCH_CD = :V_YD_SCH_CD
			               AND YD_EQP_ID = :V_YD_EQP_ID
			               AND YD_WRK_PROG_STAT IN ('1', 'S')
			               AND DEL_YN = 'N'   
			               AND YD_CRN_SCH_ID <>  :V_YD_CRN_SCH_ID 
			            ) 
			     WHERE ROWNUM = 1

			    */   
				jrParam.setField("YD_CRN_SCH_ID", ydNewCrnSchId );
			    jrParam.setField("YD_SCH_CD"	, ydSchCd);
			    jrParam.setField("YD_EQP_ID"	, ydEqpId );
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.getCrnWrkMgtPriorWrkLineOffAuto", logId, methodNm, "기존 크레인작업 지시 검색조회");
				if (jsCrn.size() > 0) {
					
			    	JDTORecord jrCrn = jsCrn.getRecord(0);
			    	ydBefCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"));
					// 기존 작업 우선순위 변경
			    	/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1Auto 
			    	UPDATE TB_YM_CRNSCH A
			    	   SET MODIFIER         = :V_MODIFIER
			    	     , MOD_DDTT         = SYSDATE
			    	     , YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN 'W' ELSE YD_WRK_PROG_STAT END)     
			    	     , YD_WORD_DT       = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NULL ELSE YD_WORD_DT END) 
			    	 WHERE YD_CRN_SCH_ID    = :V_OLD_YD_CRN_SCH_ID
			    	   AND DEL_YN = 'N'
				     */   
			    	jrParam.setField("YD_WRK_PROG_STAT" , "W");
				    jrParam.setField("OLD_YD_CRN_SCH_ID" , ydBefCrnSchIdWrk);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1Auto", logId, methodNm,  "기존 크레인작업 지시 원복");	
				}
				

				//신규 작업 우선순위 변경
				/* com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt1Auto 
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				      ,MOD_DDTT     = SYSDATE
				      ,YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
				      ,YD_WORD_DT   = SYSDATE 
				 WHERE YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
				   AND DEL_YN = 'N'
					 */
				jrParam.setField("YD_WRK_PROG_STAT" , "S");
				jrParam.setField("YD_CRN_SCH_ID" 	, ydNewCrnSchId);
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt1Auto", logId, methodNm,  "신규 크레인작업 지시 대기 ");
						
				/**********************************************************
				* 3.2 신  크레인작업지시 요구 처리
				**********************************************************/

				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YMA7L004);	//크레인작업지시요구
				jrYdMsg.setField("MSG_GP"          , "I");					//야드설비ID
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydNewCrnSchId);		//야드크레인스케쥴ID

				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L004", jrYdMsg));	
		    	
			}
			
			commUtils.printLog(logId, methodNm , "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	

	/**
	 *      [A] 오퍼레이션명 : 좌우 폭간섭
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdWidthInterCheck_1(String logId, String methodNms, String szStockId,String szStackGp,String szStackBedGp, String szStackLayer
            								,String szWidth, String ruleWidLift1Deff1 ) throws JDTOException  {

		String methodNm = "좌우 폭간섭Check[BCoilSchSeEJB.procCoilYdWidthInterCheck_1] < " + methodNms;
    	JDTORecord  jrRnt   = JDTORecordFactory.getInstance().create();	
		
		String szMethodName		=	"procCoilYdWidthInterCheck_1";
		String szOperationName	=	"좌우 폭간섭 고정skid 1단 폭간섭 Check";
		
		String szRtnVal = YmConstant.RETN_CD_FAILURE;
		
		long   lngOutDia   	= 0;           //코일외경
		
		String sWidth_GP 	= "";
		String sMaxBedNo 	= "";
		double dblMAX_W 	= 0;
		double dblWidthGap 	= 0;
		String szLogMsg 	= "";
		String szLogMsg1 	= "";
		
		
		try {
			// BED정보
			commUtils.printLog(logId, methodNm, "S+");
			
			double lngWidth 	= Double.parseDouble(commUtils.nvl(szWidth,"0"));
			double lngWidLift 	= Double.parseDouble(commUtils.nvl(ruleWidLift1Deff1,"680"));  //LIFT폭
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STACK_COL_GP"	, szStackGp);
			recPara.setField("STACK_BED_GP"	, szStackBedGp);
			recPara.setField("STOCK_ID"		, szStockId);
    	    
			
			
			JDTORecord jrBed = JDTORecordFactory.getInstance().create();
			
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilYdWidthCheck_2
			SELECT TK.STACK_COL_GP
			     ,8(SELECT STACK_LAYER_X_AXIS FROM TB_YM_STACKLAYER WHERE STACK_COL_GP =  TK.STACK_COL_GP AND ROWNUM = 1) 
			        AS STACK_LAYER_X_AXIS
			     , TK.BEFO_STACK_COL_GP
			     , TK.NEXT_STACK_COL_GP
			     , SL1.STOCK_ID             AS BEFO_STOCK_ID
			     , NVL(CC1.COIL_W,0)        AS BEFO_COIL_W
			     , SL1.STACK_LAYER_X_AXIS   AS BEFO_STACK_LAYER_X_AXIS
			     , SL2.STOCK_ID             AS NEXT_STOCK_ID
			     , NVL(CC2.COIL_W,0)        AS NEXT_COIL_W
			     , SL2.STACK_LAYER_X_AXIS   AS NEXT_STACK_LAYER_X_AXIS
			  FROM (
			        SELECT STACK_COL_GP,BEFO_STACK_COL_GP,NEXT_STACK_COL_GP
			          FROM
			             (  SELECT A.STACK_COL_GP
			                     , LEAD(A.STACK_COL_GP) OVER (ORDER BY STACK_COL_GP DESC) BEFO_STACK_COL_GP
			                     , LAG (A.STACK_COL_GP) OVER (ORDER BY STACK_COL_GP DESC) NEXT_STACK_COL_GP
			                  FROM TB_YM_STACKCOL A
			                 WHERE A.STACK_COL_GP LIKE '3C%'
			                   AND SUBSTR(A.STACK_COL_GP,3,2) BETWEEN '01' AND '99'
			                 ORDER BY 1
			             )
			          WHERE STACK_COL_GP = :V_STACK_COL_GP --'3C0305'  
			        ) TK
			     , TB_YM_STACKLAYER SL1    
			     , TB_YM_STACKLAYER SL2
			     , TB_PT_COILCOMM CC1
			     , TB_PT_COILCOMM CC2
			 WHERE TK.BEFO_STACK_COL_GP = SL1.STACK_COL_GP
			   AND SL1.STACK_BED_GP   = :V_STACK_BED_GP --'03'
			   AND SL1.STACK_LAYER_GP = '01'
			   AND SL1.STOCK_ID  = CC1.COIL_NO(+)
			   AND TK.NEXT_STACK_COL_GP = SL2.STACK_COL_GP
			   AND SL2.STACK_BED_GP   = :V_STACK_BED_GP --'03'
			   AND SL2.STACK_LAYER_GP = '01'  
			   AND SL2.STOCK_ID  = CC2.COIL_NO(+)
			*/
			
			JDTORecordSet jsBed = commDao.select(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilYdWidthCheck_2", logId, methodNm, "1단 폭간섭 CHECK 정보");
			if (jsBed.size() <= 0) {
    			szLogMsg =  "확인:"+szStockId+"적치베드(" + szStackGp + szStackBedGp + ")로 조회중 error 발생!";
				commUtils.printLog(logId, szLogMsg, "SL");
	        	commUtils.printLog(logId, methodNm, "S-");
	        	jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);
				return jrRnt;
			}
        	
        	jsBed.first();
        	jrBed = jsBed.getRecord();
        	String currStackColGp = commUtils.trim(jrBed.getFieldString("STACK_COL_GP"));
        	String befoStackColGp = commUtils.trim(jrBed.getFieldString("BEFO_STACK_COL_GP"));
        	String nextStackColGp = commUtils.trim(jrBed.getFieldString("NEXT_STACK_COL_GP"));
        	String befoStockId    = commUtils.trim(jrBed.getFieldString("BEFO_STOCK_ID"));
        	String nextStockId    = commUtils.trim(jrBed.getFieldString("NEXT_STOCK_ID"));
        	
        	double befoCoilW 	  = Double.parseDouble(commUtils.nvl(jrBed.getFieldString("BEFO_COIL_W"),"0"));
        	double nextCoilW      = Double.parseDouble(commUtils.nvl(jrBed.getFieldString("NEXT_COIL_W"),"0"));
        	
        	double befoLayerXAxis = Double.parseDouble(commUtils.nvl(jrBed.getFieldString("BEFO_STACK_LAYER_X_AXIS"),"0"));
        	double currLayerXAxis = Double.parseDouble(commUtils.nvl(jrBed.getFieldString("STACK_LAYER_X_AXIS"),"0"));
        	double nextLayerXAxis = Double.parseDouble(commUtils.nvl(jrBed.getFieldString("NEXT_STACK_LAYER_X_AXIS"),"0"));
        	
			szLogMsg =  "  --> 정보:대상코일:"+szStockId + " 대상코일폭:"+lngWidth  + " 대상SKID중심:"+currLayerXAxis + " LIFT간격:"+ruleWidLift1Deff1  +"\r\n";
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;

        	
        	szLogMsg1 =  "  --> 정보:좌측위치:"+befoStackColGp + " 좌측코일:"+befoStockId + " 좌측코일폭:"+befoCoilW+ " 좌측SKID중심:"+befoLayerXAxis  
	                  +  " 우측위치:"+nextStackColGp + " 우측코일:"+nextStockId + " 우측코일폭:"+nextCoilW+ " 우측SKID중심:"+nextLayerXAxis  +"\r\n" ;
			commUtils.printLog(logId, szLogMsg1, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
        	
        	
			if (jsBed.size() <= 0) {
    			szLogMsg =  "확인:"+szStockId+"적치베드(" + szStackGp + szStackBedGp + ")로 조회중 error 발생!";
				commUtils.printLog(logId, szLogMsg, "SL");
	        	commUtils.printLog(logId, methodNm, "S-");
	        	jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);
				return jrRnt;	
			}
			
			if ("01".equals(szStackLayer)) {
				// 좌우가 비워있는경우
				if ("".equals(befoStockId) && "".equals(nextStockId)) {  // 좌우가 비워있는경우
	        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
	        		
				}else {
					//좌우코일이 존재 하는 경우 
					if (!"".equals(befoStockId) && (!"".equals(nextStockId))) {
						
						//	좌측계산:	스키드중심좌표 간격(B-A) - (좌코일폭/2+대상폭(X)/2)>=650					
						//	우측계산:	스키드중심좌표 간격(C-B) - (우코일폭/2+대상폭(X)/2)>=650					
						
						double befoA = (currLayerXAxis - befoLayerXAxis) - (befoCoilW/2 + lngWidth/2 );
						double nextA = (nextLayerXAxis - currLayerXAxis) - (nextCoilW/2 + lngWidth/2 );
						
						szLogMsg = "  --> 정보:좌측계산값:"+befoA + " 우측계산값:"+nextA + " 권하대상폭:" + lngWidth; 
						commUtils.printLog(logId, szLogMsg, "SL");
						
						
						if ((befoA >=  lngWidLift) && ( nextA >=  lngWidLift)) {
							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
						} else {

							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);
							szLogMsg1 = szLogMsg1 + "실패-->" + lngWidth;
							commUtils.printLog(logId, szLogMsg1, "SL");
							jrRnt.setField("RTN_MSG" 	, szLogMsg1 );

						}
						
					} else if (!"".equals(befoStockId)) {
						double befoA = (currLayerXAxis - befoLayerXAxis) - (befoCoilW/2 + lngWidth/2 );
						
						szLogMsg = "  --> 정보:좌측계산값:" + befoA + " 권하대상폭:" + lngWidth; 
						commUtils.printLog(logId, szLogMsg, "SL");

						if (befoA >=  lngWidLift) {
							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
						} else {
							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
							szLogMsg1 = szLogMsg1 + "실패--> 좌측코일:" + befoStockId + " 좌측계산값" + befoA + " 권하대상폭 :" + lngWidth;
							commUtils.printLog(logId, szLogMsg1, "SL");
							jrRnt.setField("RTN_MSG" 	, szLogMsg1 );
						}
						
					} else if (!"".equals(nextStockId)) {
						double nextA = (nextLayerXAxis - currLayerXAxis) - (nextCoilW/2 + lngWidth/2 );
						
						szLogMsg = "  --> 정보:우측계산값:"+nextA + " 권하대상폭:" + lngWidth; 
						commUtils.printLog(logId, szLogMsg, "SL");

						if (nextA >=  lngWidLift) {
							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);	
						} else {
							jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
							szLogMsg1 = szLogMsg1 + "실패--> 우측코일:" + nextStockId + " 우측계산 값" + nextA + " 권하대상폭 :" + lngWidth;
							commUtils.printLog(logId, szLogMsg1, "SL");
							jrRnt.setField("RTN_MSG" 	, szLogMsg1 );
						}
					}
				}
			} 
			
			commUtils.printLog(logId, methodNm, "S-");
			return jrRnt;
		} catch(Exception e) {
			szLogMsg =  "폭CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			return jrRnt ;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 좌우 폭간섭
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord procCoilYdWidthInterCheck_2(String logId, String methodNms, String szStockId,String szStackGp,String szStackBedGp, String szStackLayer
            								,String szWidth, String ruleWidLift1Deff1 ) throws JDTOException  {

		String methodNm = "좌,우열 폭간섭Check[BCoilSchSeEJB.procCoilYdWidthInterCheck_2] < " + methodNms;
    	JDTORecord  jrRnt   = JDTORecordFactory.getInstance().create();	
		
		String szMethodName		=	"procCoilYdWidthInterCheck_1";
		String szOperationName	=	"좌,우열 폭간섭 고정skid 폭간섭 Check";
		
		String szRtnVal = YmConstant.RETN_CD_FAILURE;
		
		long   lngOutDia   	= 0;           //코일외경
		
		String sWidth_GP 	= "";
		String sMaxBedNo 	= "";
		double dblMAX_W 	= 0;
		double dblWidthGap 	= 0;
		String szLogMsg 	= "";
		String szLogMsg1 	= "";
		
		
		try {
			// BED정보
			commUtils.printLog(logId, methodNm, "S+");
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STOCK_ID"			, szStockId);
			recPara.setField("STACK_COL_GP"		, szStackGp);
			recPara.setField("STACK_BED_GP"		, szStackBedGp);
			recPara.setField("STACK_LAYER_GP"	, szStackLayer);
			recPara.setField("CRANE_GAP"		, commUtils.nvl(ruleWidLift1Deff1,"680"));
			
			szLogMsg =  "  --> 정보:대상코일:"+szStockId + " 대상코일폭:"+commUtils.nvl(szWidth,"0")  + " LIFT간격:"+commUtils.nvl(ruleWidLift1Deff1,"680")  +"\r\n";
			commUtils.printLog(logId, szLogMsg, "SL");
			szLogMsg1 = szLogMsg1 + szLogMsg;
			
			JDTORecord jrBed = JDTORecordFactory.getInstance().create();
			
			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilYdWidthCheck_3 
			--좌,우열 폭 간섭 체크 NEW
			WITH TEMP_INFO AS (

			    SELECT COIL_GP
			          ,P_COIL_W
			          ,P_COIL_ODIA
			          ,P_LYR_XAXIS
			          ,P_LYR_YAXIS
			          ,COIL_Y_MIN
			          ,COIL_Y_MAX
			          ,STACK_COL_GP
			          ,STACK_BED_GP
			          ,STACK_LAYER_GP
			          ,STOCK_ID
			          ,COL_H_COIL_W
			          ,COIL_H_COIL_ODIA
			          ,STACK_LAYER_X_AXIS
			          ,STACK_LAYER_Y_AXIS
			          ,STACK_LAYER_X_AXIS - COIL_H_COIL_ODIA AS COL_Y_MIN
			          ,STACK_LAYER_Y_AXIS + COIL_H_COIL_ODIA AS COL_Y_MAX
			          ,NVL(MAX(COL_H_COIL_W) OVER (PARTITION BY COIL_GP ORDER BY COIL_GP),0) AS COL_MAX_W
			      FROM (
			           
			            --해당 BED에 코일이 넣고 난뒤에 충돌가능 BED 검색
			            SELECT B.COIL_GP
			                  ,B.P_COIL_W
			                  ,B.P_COIL_ODIA
			                  ,B.P_LYR_XAXIS
			                  ,B.P_LYR_YAXIS
			                  ,B.COIL_Y_MIN
			                  ,B.COIL_Y_MAX
			                  ,A.STACK_COL_GP
			                  ,A.STACK_BED_GP
			                  ,A.STACK_LAYER_GP
			                  ,CASE WHEN B.COIL_GP = 'D' THEN B.P_STL_NO ELSE A.STOCK_ID END AS STOCK_ID
			                  ,CASE WHEN B.COIL_GP = 'D' THEN B.P_COIL_W ELSE ROUND((SELECT COIL_W FROM TB_PT_COILCOMM WHERE COIL_NO = A.STOCK_ID)/2,0) END AS COL_H_COIL_W
			                  ,CASE WHEN B.COIL_GP = 'D' THEN B.P_COIL_ODIA ELSE ROUND((SELECT COIL_OUTDIA FROM TB_PT_COILCOMM WHERE COIL_NO = A.STOCK_ID)/2,0) END AS COIL_H_COIL_ODIA
			                  ,A.STACK_LAYER_X_AXIS
			                  ,A.STACK_LAYER_Y_AXIS
			              FROM TB_YM_STACKLAYER A
			                  ,(
			                    SELECT CASE WHEN B.P_LYR_XAXIS > A.STACK_LAYER_X_AXIS THEN 'B'
			                                WHEN B.P_LYR_XAXIS < A.STACK_LAYER_X_AXIS THEN 'N'
			                                ELSE 'D' END AS COIL_GP
			                         , B.P_STL_NO
			                         , B.P_COIL_W
			                         , B.P_COIL_ODIA
			                         , B.P_LYR_XAXIS
			                         , B.P_LYR_YAXIS
			                         , A.STACK_LAYER_Y_AXIS - P_COIL_ODIA AS COIL_Y_MIN --최소BED Y계산(Y값-코일외경/2)
			                         , A.STACK_LAYER_Y_AXIS + P_COIL_ODIA AS COIL_Y_MAX --최대BED Y계산(Y값+코일외경/2)
			                         , A.STACK_COL_GP
			                         , A.STACK_BED_GP
			                         , A.STACK_LAYER_GP
			                         , A.STACK_LAYER_X_AXIS
			                         , A.STACK_LAYER_Y_AXIS
			                      FROM TB_YM_STACKLAYER A
			                         , (
			                            SELECT :V_STOCK_ID AS P_STL_NO
			                                 , STACK_COL_GP
			                                 , STACK_BED_GP
			                                 , STACK_LAYER_GP
			                                 , STACK_LAYER_X_AXIS AS P_LYR_XAXIS
			                                 , STACK_LAYER_Y_AXIS AS P_LYR_YAXIS
			                                 , ROUND((SELECT COIL_W      FROM TB_PT_COILCOMM WHERE COIL_NO = :V_STOCK_ID)/2.0) AS P_COIL_W
			                                 , ROUND((SELECT COIL_OUTDIA FROM TB_PT_COILCOMM WHERE COIL_NO = :V_STOCK_ID)/2.0) AS P_COIL_ODIA
			                              FROM TB_YM_STACKLAYER
			                             WHERE STACK_COL_GP   = :V_STACK_COL_GP
			                               AND STACK_BED_GP   = :V_STACK_BED_GP
			                               AND STACK_LAYER_GP = :V_STACK_LAYER_GP
			                           ) B 
			                     WHERE A.STACK_COL_GP LIKE SUBSTR(B.STACK_COL_GP,1,2) || '%'
			                       AND A.STACK_BED_GP    = B.STACK_BED_GP
			                       AND A.STACK_LAYER_GP  = B.STACK_LAYER_GP
			                       AND SUBSTR(A.STACK_COL_GP,3,2) BETWEEN '01' AND '99'
			                       AND A.STACK_LAYER_X_AXIS BETWEEN B.P_LYR_XAXIS - 3000 AND B.P_LYR_XAXIS + 3000  
			                   ) B 
			             WHERE A.STACK_COL_GP = B.STACK_COL_GP
			               AND A.STACK_BED_GP IN ( LPAD(TO_NUMBER(B.STACK_BED_GP)-1,2,'0')
			                                      ,B.STACK_BED_GP
			                                      ,LPAD(TO_NUMBER(B.STACK_BED_GP)+1,2,'0'))
			               AND 'Y' = CASE WHEN B.COIL_GP = 'D' THEN 
			                                   CASE WHEN A.STACK_BED_GP = B.STACK_BED_GP AND A.STACK_LAYER_GP = B.STACK_LAYER_GP THEN 'Y'
			                                        ELSE 'N' END
			                              WHEN A.STACK_BED_GP = LPAD(TO_NUMBER(B.STACK_BED_GP)+1,2,'0') AND A.STACK_LAYER_GP = '02' THEN 'N'
			                              ELSE 'Y'
			                         END
			               --대상위치가 2단일 경우 2단만 검색
			               AND 'Y' = CASE WHEN :V_STACK_LAYER_GP = '01' THEN 'Y'
			                              ELSE CASE WHEN A.STACK_LAYER_GP = '02' THEN 'Y' ELSE 'N' END
			                         END
			           
			           )
			     WHERE STACK_LAYER_Y_AXIS - COIL_H_COIL_ODIA BETWEEN COIL_Y_MIN AND COIL_Y_MAX
			        OR STACK_LAYER_Y_AXIS + COIL_H_COIL_ODIA BETWEEN COIL_Y_MIN AND COIL_Y_MAX
			                 
			)
			SELECT CASE WHEN MIN(GAP_CHK) = 'N' THEN 'N'
			            ELSE 'Y' END AS ABLE_YN
			  FROM (
			  
			        SELECT CASE WHEN (ABS(P_LYR_XAXIS - STACK_LAYER_X_AXIS) - COL_MAX_W - P_COIL_W) >= :V_CRANE_GAP THEN 'Y'
			                    ELSE 'N' END AS GAP_CHK
			              ,ABS(P_LYR_XAXIS - STACK_LAYER_X_AXIS) - COL_MAX_W - P_COIL_W AS GAP
			              ,A.*
			          FROM TEMP_INFO A
			         WHERE COIL_GP = 'B'

			        UNION ALL

			        SELECT CASE WHEN (ABS(P_LYR_XAXIS - STACK_LAYER_X_AXIS) - COL_MAX_W - P_COIL_W) >= :V_CRANE_GAP THEN 'Y'
			                    ELSE 'N' END AS GAP_CHK
			              ,ABS(P_LYR_XAXIS - STACK_LAYER_X_AXIS) - COL_MAX_W - P_COIL_W AS GAP
			              ,A.*
			          FROM TEMP_INFO A
			         WHERE A.COIL_GP = 'N'

			        UNION ALL

			        SELECT 'Y'
			             , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL
			             , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL , NULL
			          FROM DUAL 
			  
			       )	
			*/		
			JDTORecordSet jsBed = commDao.select(recPara, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCoilYdWidthCheck_3", logId, methodNm, "좌,우열 폭간섭 CHECK 정보");
			if (jsBed.size() <= 0) {
    			szLogMsg =  "확인:"+szStockId+"적치베드(" + szStackGp + szStackBedGp + ")로 조회중 error 발생!";
				commUtils.printLog(logId, szLogMsg, "SL");
	        	commUtils.printLog(logId, methodNm, "S-");
        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 + szLogMsg  );
	        	jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);
				return jrRnt;
			}
        	
        	jsBed.first();
        	jrBed = jsBed.getRecord();
			
        	String sAbleYn  = commUtils.trim(jrBed.getFieldString("ABLE_YN"));
        	
        	if( "N".equals(sAbleYn) ) {
        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 + "폭 간섭으로 권하 불가 "  );
	        	jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);
	        	
        	} else {
        		jrRnt.setField("RTN_MSG" 	, szLogMsg1 + "폭 간섭 없음 권하 가능 ");
        		jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_SUCCESS);
        	}
			
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRnt;
			
		} catch(Exception e) {
			szLogMsg =  "폭CHECK 중 예외발생! 예외메세지: " + e.getMessage();
			commUtils.printLog(logId, szLogMsg, "SL");
			jrRnt.setField("RTN_CD" 	, YmConstant.RETN_CD_FAILURE);	
			return jrRnt ;
		}
	}

	
	/**	
	 *      [A] 오퍼레이션명 : SPM2 LINE-OFF 긴급작업(YMYMJ306)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYMYMJ306(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "SPM2 LINE-OFF긴급작업 [BCoilSchSeEJB.rcvYMYMJ306] < " + rcvMsg.getResultMsg();
		
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn 			= JDTORecordFactory.getInstance().create();;	//전문 Return
		JDTORecord jrParam			= JDTORecordFactory.getInstance().create();	//Query 실행시 파라메터 전달용 JDTORecord 
		JDTORecordSet rsResult    	= null;
		String ydCrnSchIdWrk = "";
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

	    	//수신항목 변수 저장
			String ydEqpId   = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //설비번호(크레인번호)
			String ydSchCd   = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD")); //스케줄코드
			String ydWbookId = commUtils.trim(rcvMsg.getFieldString("YD_WBOOK_ID")); //야드작업예약ID

			//SJH 추가 
			String msgId	= commUtils.nvl(commUtils.getMsgId(rcvMsg),"YMYMJ306"); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			//변경자 설정 (insert,update 문에서 사용)
			jrParam.setField("MODIFIER", modifier); //수정자
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			String ydBefCrnSchIdWrk = "";
			//가장 최근에 만들어진 크레인 스케줄ID를 가져온다.

			jrParam.setField("YD_EQP_ID"		, ydEqpId); 	
			jrParam.setField("YD_SCH_CD"		, ydSchCd); 
			jrParam.setField("YD_WBOOK_ID"		, ydWbookId);
			jrParam.setField("YD_SCH_PRIOR"		, "0");           //신규
			
			if ("3CEC02LM".equals(ydSchCd)) { //WB06
				/*
				SELECT * 
				  FROM (
				        SELECT YD_CRN_SCH_ID 
				             , (SELECT WM.STACK_BED_GP 
				                  FROM TB_YM_WRKBOOK     WB
				                     , TB_YM_WRKBOOKMTL  WM
				                 WHERE WB.YD_SCH_CD = '3CEC02LM' --EC LineOff
				                   AND WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.DEL_YN = 'N'
				                   AND WM.DEL_YN = 'N'
				                   AND WB.YD_WBOOK_ID = A.YD_WBOOK_ID) AS STACK_BED_GP
				          FROM TB_YM_CRNSCH  A
				         WHERE YD_SCH_CD = '3CEC02LM'
				           AND YD_EQP_ID = :V_YD_EQP_ID
				           AND DEL_YN = 'N'
				           AND YD_WRK_PROG_STAT IN ('W','S','1')
				           AND 1 = CASE WHEN (SELECT COUNT(*) 
				                                FROM TB_YM_CRNSCH
				                               WHERE YD_WRK_PROG_STAT IN ('S','1') 
				                                 AND YD_SCH_CD <> A.YD_SCH_CD 
				                                 AND YD_EQP_ID =  A.YD_EQP_ID
				                                 AND DEL_YN = 'N' ) > 0 
				                        THEN 0 
				                        ELSE 1 END
				         ORDER BY DECODE(STACK_BED_GP,'01',1,'02',2, 3) --02, 01, 00 순으로 작업(61, 62..)
				                , YD_CRN_SCH_ID      --00번지는 스케줄 생성순
				) WHERE ROWNUM = 1 
				 */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getECLineOffSchId", logId, methodNm, "EC LinfOff 최근 스케줄 조회");
				
				if (rsResult.size() > 0) { //스케줄이 수행중이면 작업지시 보내지 않음
					if (!"W".equals(rsResult.getRecord(0).getFieldString("YD_WRK_PROG_STAT"))) {
						return jrRtn;
					}
				}
				
			} 
			
			else if ("3EKD02LM".equals(ydSchCd)) {//SPM2 추출
				/* 
				SELECT * 
				  FROM (
				        SELECT YD_CRN_SCH_ID 
				          FROM TB_YM_CRNSCH  A
				         WHERE YD_SCH_CD = :V_YD_SCH_CD
				           AND YD_EQP_ID = :V_YD_EQP_ID
				           AND DEL_YN = 'N'
				           AND YD_WRK_PROG_STAT IN ('W','S','1')
				           AND 1 = CASE WHEN (SELECT COUNT(*) 
				                                FROM TB_YM_CRNSCH
				                               WHERE YD_WRK_PROG_STAT IN ('S','1') 
				                                 AND YD_SCH_CD <> A.YD_SCH_CD 
				                                 AND YD_EQP_ID =  A.YD_EQP_ID
				                                 AND DEL_YN = 'N' ) > 0 
				                        THEN 0 
				                        ELSE 1 END
				         ORDER BY YD_CRN_SCH_ID DESC
				) WHERE ROWNUM = 1 
				 */
				rsResult = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getLastCrnSchIdAuto", logId, methodNm, "SPM2 추출 최근 스케줄 조회");
			}
			 
			if (rsResult.size() > 0) {
			    String ydNewCrnSchId 	= commUtils.trim(rsResult.getRecord(0).getFieldString("YD_CRN_SCH_ID"));
				commUtils.printLog(logId, "Line-Off 긴급작업 [ " + ydEqpId + " >> " + ydWbookId + " >> " + " >> " + ydNewCrnSchId +" >> " + ydSchCd + " ]", "SL");
	
			    //기존 크레인작업 지시 검색
				/*
				SELECT *
				  FROM (
				        SELECT YD_CRN_SCH_ID
				          FROM TB_YM_CRNSCH
				         WHERE YD_SCH_CD = :V_YD_SCH_CD
				           AND YD_EQP_ID = :V_YD_EQP_ID
				           AND YD_WRK_PROG_STAT IN ('1', 'S')
				           AND DEL_YN = 'N'   
				           AND YD_CRN_SCH_ID <>  :V_YD_CRN_SCH_ID 
				        ) 
				 WHERE ROWNUM = 1
				 */   
				jrParam.setField("YD_CRN_SCH_ID", ydNewCrnSchId );
			    jrParam.setField("YD_SCH_CD"	, ydSchCd);
			    jrParam.setField("YD_EQP_ID"	, ydEqpId );
				JDTORecordSet jsCrn = commDao.select(jrParam, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getCrnWrkMgtPriorWrkLineOffAuto", logId, methodNm, "기존 크레인작업 지시 검색조회");
				
				if (jsCrn.size() > 0) {
			    	JDTORecord jrCrn = jsCrn.getRecord(0);
			    	ydBefCrnSchIdWrk   = commUtils.trim(jrCrn.getFieldString("YD_CRN_SCH_ID"));
					// 기존 작업 우선순위 변경
			    	/*
			    	UPDATE TB_YM_CRNSCH A
			    	   SET MODIFIER         = :V_MODIFIER
			    	     , MOD_DDTT         = SYSDATE
			    	     , YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN 'W' ELSE YD_WRK_PROG_STAT END)     
			    	     , YD_WORD_DT       = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NULL ELSE YD_WORD_DT END) 
			    	 WHERE YD_CRN_SCH_ID    = :V_OLD_YD_CRN_SCH_ID
			    	   AND DEL_YN = 'N'
				     */   
			    	jrParam.setField("YD_WRK_PROG_STAT" , "W");
				    jrParam.setField("OLD_YD_CRN_SCH_ID" , ydBefCrnSchIdWrk);
					commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgtPriorWrkNext1Auto", logId, methodNm,  "기존 크레인작업 지시 원복");	
				}

				/* 
				UPDATE TB_YM_CRNSCH
				   SET MODIFIER     = :V_MODIFIER
				      ,MOD_DDTT     = SYSDATE
				      ,YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
				      ,YD_WORD_DT   = SYSDATE 
				 WHERE YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
				   AND DEL_YN = 'N'
					 */
				jrParam.setField("YD_WRK_PROG_STAT" , "S");
				jrParam.setField("YD_CRN_SCH_ID" 	, ydNewCrnSchId);
				commDao.update(jrParam, "com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updCrnWrkMgt1Auto", logId, methodNm,  "신규 크레인작업 지시 대기 ");
						
				/**********************************************************
				* 3.2 신  크레인작업지시 요구 처리
				**********************************************************/
				//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();

				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("JMS_TC_CD"       , YmConstant.YMA7L004);	//크레인작업지시요구
				jrYdMsg.setField("MSG_GP"          , "I");					//야드설비ID
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydNewCrnSchId);		//야드크레인스케쥴ID

				jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YMA7L004", jrYdMsg));	
		    	
			}	
			
			commUtils.printLog(logId, methodNm , "S-");
			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 * 신기본 BASE CHECK
	 * 화면권하위치변경 변경 에서 사용
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException
	 */
    public String procStockIdBaseCheckNew(String logId, String methodNms,JDTORecord jrLayer) throws JDTOException {
    	String methodNm = "화면권하위치변경 CHECK[BCoilSchSeEJB.procStockIdBaseCheckNew] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String StockId	   	= commUtils.trim(jrLayer.getFieldString("STOCK_ID"));			//크레인작업재료
		String ydSchCd      = commUtils.trim(jrLayer.getFieldString("YD_SCH_CD"));		    //스케쥴 코드
		String ydCrnSchId   = commUtils.trim(jrLayer.getFieldString("YD_CRN_SCH_ID"));		//스케쥴 ID
		String StackColGp 	= commUtils.trim(jrLayer.getFieldString("STACK_COL_GP"));		//열
		String StackBedGp   = commUtils.trim(jrLayer.getFieldString("STACK_BED_GP"));		//BED
		String StackLayerGp = commUtils.trim(jrLayer.getFieldString("STACK_LAYER_GP"));		//단 
		String sRtnBedDan 	= "";  //TO위치	
		
		try {
			
			commUtils.printLog(logId, methodNm, "S+");
			
			if ("".equals(StackColGp)) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			String sAPP005_YN = YmComm.BCoilApplyYn("APP005","3","1");   //log여부
			commUtils.printLog(logId,  "TO위치 LOG 적용:" + sAPP005_YN, "SL");
			
			JDTORecord jrToLocBaseCheckRtn = JDTORecordFactory.getInstance().create();
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"			, StockId);			
			jrTemp.setField("YD_SCH_CD"			, ydSchCd);			
			jrTemp.setField("STACK_COL_GP"		, StackColGp);		
			jrTemp.setField("STACK_BED_GP"		, StackBedGp);		
			jrTemp.setField("STACK_LAYER_GP"	, StackLayerGp);	
			
			szLogMsg =  "재료["+StockId +" +스케쥴 코드 : "+ ydSchCd + "열"+ StackColGp + "베드"+StackBedGp+ "단" + StackLayerGp + "의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
	      	
	    	//----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------

			/* com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStockIdBaseCheck
			WITH SPEC_TABLE AS (
			    -- 특정규격CHECK 
			    SELECT DTL_ITM1 AS STACK_RULE_NAME 
			      FROM USRYMA.TB_YM_RULE
			     WHERE REPR_CD_GP = 'YM004'
			       AND DEL_YN = 'N'
			)
			, TO_LOC_TABLE AS
			(
			-- 대상 위치 SELECT
			SELECT A.STACK_COL_GP               AS TAG_STACK_COL_GP
			     , A.STACK_BED_GP               AS TAG_STACK_BED_GP
			     , A.STACK_LAYER_GP             AS TAG_STACK_LAYER_GP
			     , B.STACK_COL_USAGE_CD         AS TAG_COL_USE_CD
			     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
			                                '02', A.STACK_BED_GP)                                AS TAG_LEFT_BED
			     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
			                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_LEFT_LAYER
			     , DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
			                                '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS TAG_RIGHT_BED
			     , DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
			                                '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS TAG_RIGHT_LAYER
			      -- 단 우선순위
			     , C.JJANG_GP  
			     , C.STOCK_ID
			  FROM TB_YM_STACKLAYER A
			     , TB_YM_STACKCOL B
			     , (SELECT CASE WHEN EXISTS(SELECT 1 FROM SPEC_TABLE T WHERE T.STACK_RULE_NAME=C.HR_SPEC_ABBSYM) --특정규격존재                                 
			                     AND NVL(C.NEXT_PROC,1) NOT IN ('5K','6K') --정정대상                                  
			                     AND TRUNC((SYSDATE - C.HRMILL_CMPL_DT)*24,0)<=48 --압연시간 48시간이 안된 경우                                       
						        THEN 'Y' 
						        ELSE 'N' END  AS JJANG_GP
			              , C.COIL_NO  AS STOCK_ID
			         FROM USRPTA.TB_PT_COILCOMM C
			        WHERE C.COIL_NO = :V_STOCK_ID  
			       )C
			 WHERE SUBSTR(A.STACK_COL_GP,1,1)  = '3'
			   AND A.STACK_COL_GP         = :V_STACK_COL_GP     --가이드 열
			   AND A.STACK_BED_GP         = :V_STACK_BED_GP
			   AND A.STACK_LAYER_GP       = :V_STACK_LAYER_GP
			   AND A.STACK_LAYER_ACTIVE_STAT= 'E'
			   AND A.STACK_LAYER_STAT       = 'E'
			   AND A.STACK_COL_GP           = B.STACK_COL_GP
			
			) 
			, TO_LOC_DATA_TABLE AS (
			-- TO 위치코일정보 SELECT
			SELECT A.STOCK_ID           AS STOCK_ID
			     , A.TAG_STACK_COL_GP
			     , A.TAG_STACK_BED_GP
			     , A.TAG_STACK_LAYER_GP
			     , A.TAG_LEFT_BED
			     , A.TAG_LEFT_LAYER
			     , A.JJANG_GP
			     , (SELECT STACK_LAYER_ACTIVE_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_ACTIVE_STAT 
			     , (SELECT STACK_LAYER_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_LAYER_STAT
			     , (SELECT STOCK_ID 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_LEFT_BED
			           AND B.STACK_LAYER_GP = A.TAG_LEFT_LAYER)  AS  TAG_LEFT_STOCK_ID
			     , A.TAG_RIGHT_BED
			     , A.TAG_RIGHT_LAYER
			     , (SELECT STACK_LAYER_ACTIVE_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_ACTIVE_STAT 
			     , (SELECT STACK_LAYER_STAT 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_LAYER_STAT
			     , (SELECT STOCK_ID 
			          FROM TB_YM_STACKLAYER B 
			         WHERE B.STACK_COL_GP   = A.TAG_STACK_COL_GP
			           AND B.STACK_BED_GP   = A.TAG_RIGHT_BED
			           AND B.STACK_LAYER_GP = A.TAG_RIGHT_LAYER) AS  TAG_RIGHT_STOCK_ID
			  FROM TO_LOC_TABLE A
			)
			SELECT KK.*
			  FROM
			        (
			        SELECT K.* 
			          FROM TO_LOC_DATA_TABLE K
			         WHERE 1  = CASE WHEN TAG_STACK_LAYER_GP = '01'                     THEN 1
			                         WHEN TAG_STACK_LAYER_GP = '02' AND JJANG_GP = 'Y'  THEN 0  -- 짱구코일 2단제외
			                         WHEN TAG_STACK_LAYER_GP = '02'                             -- 2단일 경우 좌우 적치 상태 CHECK
			                              AND TAG_LEFT_ACTIVE_STAT = 'E' AND TAG_RIGHT_ACTIVE_STAT = 'E' 
			                              AND TAG_LEFT_LAYER_STAT  = 'C' AND TAG_RIGHT_LAYER_STAT  = 'C' THEN 1
			                    ELSE 0 END  
			        ) KK   
			
			*/ 
			
			JDTORecordSet outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getStockIdBaseCheck", logId, methodNm, "화면TO위치 변경 베드 조회");
			if (outjsResult.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
	
			String szToPosGrade	= "";
			String szDBLogMsg	= "";
			
			JDTORecord	jrResult		= JDTORecordFactory.getInstance().create();
		    //----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord jrSchSule = this.procSchSule(logId, methodNms, StockId, ydSchCd);	
			
			for (int i = 1; i <= outjsResult.size(); i++) {
	
				outjsResult.absolute(i);
				jrResult  = outjsResult.getRecord();


				jrResult.setField("YD_CRN_SCH_ID", 	ydCrnSchId); 
				jrResult.setField("YD_SCH_CD"	 , 	ydSchCd); 	
				jrSchSule.setField("SCH_LOG"	 , 	sAPP005_YN); 	
				
				jrToLocBaseCheckRtn = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
				
				szToPosGrade = commUtils.trim(jrToLocBaseCheckRtn.getFieldString("GRIDE"  ));
				szDBLogMsg = szDBLogMsg + commUtils.trim(jrToLocBaseCheckRtn.getFieldString("GRADE_CONTENTS"  ) );
				
			}
		 // 적치 가능 check
			JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
			String szGRIDE = "9";
			String LogMsg = "";
			for (int i = 1; i <= outjsResult.size(); i++) {

				outjsResult.absolute(i);
				jrAbleResult  = outjsResult.getRecord();
				
				szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
				String szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
				String szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
				String szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

				//적치가능 check
				JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);

				String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				LogMsg		 		 = commUtils.trim(jrLocAbleRtn.getFieldString("LOG_MSG")) ;
				
				if (LocAbleRtnMsg.length() > 0 ) {
					szDBLogMsg = szDBLogMsg + LocAbleRtnMsg +"\r\n";
				}
			
				if (LocAbleRtn.equals("1")) {
					szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
					commUtils.printLog(logId, szLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp ;
	    			break;
				}
			}
			commUtils.printLog(logId, "sRtnBedDan" + sRtnBedDan, "SL");
			
			if (sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+StockId+"]에 대한 적치기준 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				if (sAPP005_YN.equals("Y")) {
	    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
	    			jrLog.setField("STOCK_ID"		, StockId);
	    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
	    			jrLog.setField("YD_GP"			, "3");
	    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
	    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택실패:"+ StockId+" LOG :"+"\r\n" + szDBLogMsg);
	    			    			
	    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
	    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
				}
				jrLayer.setField("LOG_MSG", LogMsg);
				return YmConstant.RETN_CD_FAILURE;				
			}

			if (sAPP005_YN.equals("Y")) {
    			JDTORecord jrLog  				= JDTORecordFactory.getInstance().create(); 
    			jrLog.setField("STOCK_ID"		, StockId);
    			jrLog.setField("YD_CRN_SCH_ID"	, ydCrnSchId);
    			jrLog.setField("YD_GP"			, "3");
    			jrLog.setField("YD_SCH_CD"		, ydSchCd);
    			jrLog.setField("SCH_CONTENTS"	, "대상코일선택:"+ StockId+" 선택위치:"+ StackColGp + StackBedGp + StackLayerGp+ " LOG :"+"\r\n" + szDBLogMsg);
    			    			
    			EJBConnector SchLog = new EJBConnector("default", "BCoilSchSeEJB", this);
    			SchLog.trx( "insSchLog" , new Class[] { JDTORecord.class }, new Object[] { jrLog });
			}

			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}      

	/**
	 *      TO위치결정테스트  (YMYMJ310 용 TO위치 가능여부 확인용)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return String
	 *      @throws DAOException
	*/
    public String procToLocTest(String logId, String methodNms, JDTORecord jrParam) throws JDTOException {
    	String methodNm = "TO위치결정테스트[BCoilSchSeEJB.procToLocTest] < " + methodNms;
    	String szLogMsg					= null;
		JDTORecord		jrTemp			= null;
		
		
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");

		String szYD_SCH_CD 	   		= commUtils.trim(jrParam.getFieldString("YD_SCH_CD"));			//크레인스케줄코드
		String szYD_WBOOK_ID		= commUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"));			//작업예약

		String szSTOCK_ID	   		= commUtils.trim(jrParam.getFieldString("STOCK_ID"));			//크레인작업재료
		//String szYD_CRN_SCH_ID 		= commUtils.trim(jrParam.getFieldString("YD_CRN_SCH_ID"));		//크레인스케줄ID
		String szYD_EQP_ID     		= commUtils.trim(jrParam.getFieldString("YD_EQP_ID"));			//크레인설비ID
		String szYD_UP_WO_LOC 		= commUtils.trim(jrParam.getFieldString("YD_UP_WO_LOC"));		
		String szYD_UP_WO_LAYER 	= commUtils.trim(jrParam.getFieldString("YD_UP_WO_LAYER"));		

		try {
			
			if ( szYD_UP_WO_LOC.equals("") ) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 권하 또는 권상위치 이상 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				return YmConstant.RETN_CD_FAILURE;
			}
			
			JDTORecord jrToLocPrimary = JDTORecordFactory.getInstance().create();			
			
			//권상재료에 따라 알맞은 적치가능한 베드 검색 방법을 적용
			jrTemp = JDTORecordFactory.getInstance().create();
			jrTemp.setField("STOCK_ID"		, szSTOCK_ID);		//권상 STOCK
			jrTemp.setField("YD_SCH_CD"		, szYD_SCH_CD);		//스케줄 코드
			jrTemp.setField("YD_EQP_ID"		, szYD_EQP_ID);		//설비ID
			jrTemp.setField("YD_UP_WO_LOC"  , szYD_UP_WO_LOC);
			
			szLogMsg =  " TOSQL:["+szYD_SCH_CD+ "] 권상재료["+szSTOCK_ID +" 의 적치가능한 베드 조회 시작";
			commUtils.printLog(logId, szLogMsg, "SL");
			
			JDTORecordSet outjsResult = JDTORecordFactory.getInstance().createRecordSet("Temp");
	    	//----------------------------------------------------------------------------------------------------------------------
			//	일반적인 적치가능한 베드 조회
			//  적재가능 위치 만 검색
			//----------------------------------------------------------------------------------------------------------------------
			outjsResult = commDao.select(jrTemp, "com.inisteel.cim.ym.bcoil.dao.BcoilDAO.getYdPrimaryWorkSearch", logId, methodNm, "동일한 적치가능한 베드 조회");
			
			if (outjsResult.size() <= 0) {
				szLogMsg = methodNm+ "적치가능한 베드 검색 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");

				return YmConstant.RETN_CD_FAILURE;
			}
			JDTORecord	jrResult	= JDTORecordFactory.getInstance().create();
	    	String szStackColGp 	= "";
			String szStackBedGp 	= "";
			String szStackLayerGp 	= "";	
			String szToPosGrade 	= "999";

			String sRtnBedDan 		= "";	
			String sRtnBedDanCheck 	= "";	
			int iToPosGrade 		= 999;
			int iToPosGradeCheck	= 999;

		    //----------------------------------------------------------------------------------------------------------------------
			//	적재가능 위치 SCH RULL 검색
			//----------------------------------------------------------------------------------------------------------------------
			JDTORecord  jrSchSule = this.procSchSule(logId, methodNms, szSTOCK_ID, szYD_SCH_CD);
			String szDBLogMsg = "";
			String szGROUP_SEQ = "";
		    // 평점 CEHCK	
			for (int i = 1; i <= outjsResult.size(); i++) {
	
				outjsResult.absolute(i);
				jrResult  = outjsResult.getRecord();

				szStackColGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_COL_GP"  ));
				szStackBedGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_BED_GP"  ));
				szStackLayerGp 	= commUtils.trim(jrResult.getFieldString("TAG_STACK_LAYER_GP"  ));	
				szGROUP_SEQ 	= commUtils.trim(jrResult.getFieldString("GROUP_SEQ"  ));
				
				
				//평점 분석  /////////////////////////////////////////////////////////////////////////////////////////////////////		
				jrResult.setField("YD_SCH_CD"	 	, szYD_SCH_CD); 
				jrResult.setField("GROUP_SEQ"	 	, szGROUP_SEQ); 				
				
				jrToLocPrimary = this.procGradeAnalysis(logId, methodNm, jrResult, jrSchSule);
				
				szToPosGrade    		= commUtils.trim(jrToLocPrimary.getFieldString("GRIDE"  ));
				String szToPosGradeMsg 	= commUtils.trim(jrToLocPrimary.getFieldString("GRADE_CONTENTS"));
				if (szToPosGradeMsg.length() > 0 ) {
					szDBLogMsg = szDBLogMsg + szToPosGradeMsg +"\r\n";
				}
				
				outjsResult.getRecord(i-1).setField("GRIDE" , szToPosGrade ); 
			}
			
		    // 평점 CEHCK	 SORT
			String szGrideSort  = "99";
			int igride          = 10; 
			JDTORecordSet	jsGrideResult	= JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord	    jrGrideResult	= JDTORecordFactory.getInstance().create();
			
			for (int i = 1; i <= igride; i++) {
				for (int j = 1; j <= outjsResult.size(); j++) {
					outjsResult.absolute(j);
					jrGrideResult  = outjsResult.getRecord();
					szGrideSort 	= commUtils.trim(jrGrideResult.getFieldString("GRIDE"  ));
					int iGrideSort = Integer.parseInt(szGrideSort);
	
					if (!szGrideSort.equals(YmConstant.RETN_CD_FAILURE)) {
					
						if (iGrideSort == i) {
							jsGrideResult.addRecord(outjsResult.getRecord());
						}
					}
				}
			}
				
		    // 적치 가능 check
			JDTORecord	    jrAbleResult	= JDTORecordFactory.getInstance().create();
			String szGRIDE = "9";
			for (int i = 1; i <= jsGrideResult.size(); i++) {

				jsGrideResult.absolute(i);
				jrAbleResult  = jsGrideResult.getRecord();
				
				szGRIDE 		= commUtils.nvl (jrAbleResult.getFieldString("GRIDE"),"9");
				szStackColGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_COL_GP"  ));
				szStackBedGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_BED_GP"  ));
				szStackLayerGp 	= commUtils.trim(jrAbleResult.getFieldString("TAG_STACK_LAYER_GP"  ));	

				//적치가능 check
				JDTORecord jrLocAbleRtn = this.procLocAbleChk(logId, methodNm, jrAbleResult, jrSchSule);

				String LocAbleRtn 	 = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_RTN")) ;
				String LocAbleRtnMsg = commUtils.trim(jrLocAbleRtn.getFieldString("LOC_ABLE_CONTENTS")) ;
				if (LocAbleRtnMsg.length() > 0 ) {
					szDBLogMsg = szDBLogMsg + LocAbleRtnMsg +"\r\n";
				}
				
				
				if (LocAbleRtn.equals("1")) {
					szLogMsg = methodNm+ szStackColGp+szStackBedGp+ szStackLayerGp+"  적치가능 위치평점:"+ szGRIDE;
					commUtils.printLog(logId, szLogMsg, "SL");				
				    //적치가능 
					sRtnBedDan = szStackColGp+szStackBedGp+ szStackLayerGp;
	    			break;
				}				
				
			}
			if (sRtnBedDan.length() < 10) {
				szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 TO위치 결정 실패 ";
				commUtils.printLog(logId, szLogMsg, "SL");
				
				return YmConstant.RETN_CD_FAILURE;				
			}

			szLogMsg = methodNm+ "크레인작업재료의  재료정보["+szSTOCK_ID+"]에 대한 TO위치 결정 성공 : " + sRtnBedDan;
			commUtils.printLog(logId, szLogMsg, "SL");
			
			//----------------------------------------------------------------------------------------------------------------------
			// ERROR 발생시 ?
			//----------------------------------------------------------------------------------------------------------------------
			commUtils.printLog(logId, methodNm, "S-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
	    }//end of try~catch				
		return YmConstant.RETN_CD_SUCCESS;
	}      
	
}
