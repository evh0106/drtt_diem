/**
 * @(#)GdsYsL2RcvSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      제품(봉강,선재) 야드 L2 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.gds.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.message.MessageSenderTalk;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;
/**
 *      [A] 클래스명 : 제품(봉강,선재) 야드 L2수신 처리
 *
 * @ejb.bean name="GdsYsL2RcvSeEJB" jndi-name="GdsYsL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/

public class GdsYsL2RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private GdsYsComm gdsYsComm = new GdsYsComm();
	private YsComm ysComm = new YsComm();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	

	/**
	 *      [A] 오퍼레이션명 : 선재수동창고 설비고장복구실적(N3YSL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN3YSL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재수동창고 설비고장복구실적[GdsYsL2RcvSeEJB.rcvN3YSL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = false;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			String brGp            = ""; 
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check
			if (msgId.startsWith("N3") || (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
 
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			//resMsg.setField("YD_WRK_PROG_STAT", ydEqpStat     ); //야드작업진행상태(야드설비상태)
			//resMsg.setField("YD_SCH_CD"       , ydEqpPauseCode); //야드스케쥴코드(야드설비휴지코드)
			resMsg.setField("YD_L2_WR_GP"     , "R"           ); //야드L2실적구분(고장복구실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비고장복구실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} else if ("".equals(ydEqpPauseCode) && "B".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat))) {
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
			if ("B".equals(ydEqpStat)) {
				brGp = "B"; //고장
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) {
				brGp = "R"; //복구
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "R000";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , ydEqpStat      ); //야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); //고장복구구분
			jrParam.setField("MODIFIER"           , modifier       ); //수정자

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			

			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpStat.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updEqpPause", logId, methodNm, "설비휴지 등록");
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				//해당 크레인 스케줄 상태가 권상지시(1)일 경우 명령선택대기(W)로 변경
				if ("B".equals(ydEqpStat)) {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchW", logId, methodNm, "크래인스케줄명령선택대기설정");
				}
				//크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
				jrRtn = this.trtCrnReschN3(jrParam);
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YDY1L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN3L004(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN3L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강야드L2 설비고장복구실적(N4YSL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN4YSL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강야드L2 설비고장복구실적[GdsYsL2RcvSeEJB.rcvN4YSL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = false;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			String brGp            = ""; //고장복구구분
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check
			if(modifier.equals(msgId)) {
				//Backup 이 아니고 L2에서 인터페이스 수신된 경우만 응답 전문 전송여부 Check 
				if (msgId.startsWith("N4") || (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
					resYn = true;
				}
			}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydEqpStat     ); //야드작업진행상태(야드설비상태)
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비고장복구실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} else if ("".equals(ydEqpPauseCode) && "B".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat))) {
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
			if ("B".equals(ydEqpStat)) {
				brGp = "B"; //고장
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) {
				brGp = "R"; //복구
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "R000";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , ydEqpStat      ); //야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); //고장복구구분
			jrParam.setField("MODIFIER"           , modifier       ); //수정자

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			

			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpStat.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updEqpPause", logId, methodNm, "설비휴지 등록");
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				//해당 크레인 스케줄 상태가 권상지시(1)일 경우 명령선택대기(W)로 변경
				if ("B".equals(ydEqpStat)) {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchW", logId, methodNm, "크래인스케줄명령선택대기설정");
				}
				//크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
				jrRtn = this.trtCrnReschN4(jrParam);
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YDY1L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				resMsg.setField("MODIFIER"      , modifier  ); //수정자
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN4L004(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN4L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재자동창고 설비고장복구실적(N5YSL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN5YSL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고 설비고장복구실적[GdsYsL2RcvSeEJB.rcvN5YSL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = false;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			String brGp            = ""; //고장복구구분
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check
			if (msgId.startsWith("N5") || (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "R"           ); //야드L2실적구분(고장복구실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비고장복구실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} else if ("".equals(ydEqpPauseCode) && "B".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat))) {
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
			if ("B".equals(ydEqpStat)) {
				brGp = "B"; //고장
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) {
				brGp = "R"; //복구
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "R000";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , ydEqpStat      ); //야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); //고장복구구분
			jrParam.setField("MODIFIER"           , modifier       ); //수정자

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			

			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpStat.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updEqpPause", logId, methodNm, "설비휴지 등록");
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				//해당 크레인 스케줄 상태가 권상지시(1)일 경우 명령선택대기(W)로 변경
				if ("B".equals(ydEqpStat)) {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchW", logId, methodNm, "크래인스케줄명령선택대기설정");
				}
				//크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
				jrRtn = this.trtCrnReschN5(jrParam);
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YDY1L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN5L004(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN5L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강자동창고 설비고장복구실적(N6YSL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN6YSL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동창고 설비고장복구실적[GdsYsL2RcvSeEJB.rcvN6YSL003] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = false;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId           = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId         = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"          )); //야드설비ID
			String ydEqpStat       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_STAT"        )); //야드설비상태(B:고장, N:정상, R:복구 등)
			String ydEqpPauseCode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_PAUSE_CODE"  )); //야드설비휴지코드
			String ydEqpTrblRcvrDt = commUtils.trim(rcvMsg.getFieldString("YD_EQP_TRBL_RCVR_DT")); //야드설비고장복구일시
			String modifier        = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			String brGp            = ""; //고장복구구분
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check
			if (msgId.startsWith("N6") || (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId       ); //야드설비ID
			resMsg.setField("YD_L2_WR_GP"     , "R"           ); //야드L2실적구분(고장복구실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "BR99"        ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비고장복구실적 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "BR01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "BR02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR03";
				ydL3Msg    = "오류:야드설비상태 없음";
			} else if ("".equals(ydEqpPauseCode) && "B".equals(ydEqpStat)) {
				ydL3HdRsCd = "BR04";
				ydL3Msg    = "오류:설비휴지코드 없음";
			} else if ("".equals(ydEqpTrblRcvrDt) && ("B".equals(ydEqpStat) || "N".equals(ydEqpStat) || "R".equals(ydEqpStat))) {
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
			if ("B".equals(ydEqpStat)) {
				brGp = "B"; //고장
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "B000";
				}
			} else if ("N".equals(ydEqpStat) || "R".equals(ydEqpStat)) {
				brGp = "R"; //복구
				if ("0000".equals(ydEqpPauseCode)) {
					ydEqpPauseCode = "R000";
				}

				if ("CR".equals(ydEqpId.substring(2, 4))) {
					ydEqpStat = "W";
				} else {
					ydEqpStat = "N";
				}
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"          , ydEqpId        ); //야드설비ID
			jrParam.setField("YD_EQP_PAUSE_CODE"  , ydEqpPauseCode ); //야드설비휴지코드
			jrParam.setField("YD_EQP_PAUSE_OCC_DT", ydEqpTrblRcvrDt); //야드설비휴지발생일시
			jrParam.setField("YD_EQP_STAT"        , ydEqpStat      ); //야드설비상태
			jrParam.setField("BR_GP"              , brGp           ); //고장복구구분
			jrParam.setField("MODIFIER"           , modifier       ); //수정자

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");
			

			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "BR11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpStat.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT")))) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "BR12";
				ydL3Msg = "오류:현재 설비상태[" + ydEqpStat + "]와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 3. 설비상태 수정
			**********************************************************/
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

			/**********************************************************
			* 4. 설비휴지 등록
			**********************************************************/
			if (!"".equals(brGp)) {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updEqpPause", logId, methodNm, "설비휴지 등록");
			}
			
			/**********************************************************
			* 5. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				//해당 크레인 스케줄 상태가 권상지시(1)일 경우 명령선택대기(W)로 변경
				if ("B".equals(ydEqpStat)) {
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchW", logId, methodNm, "크래인스케줄명령선택대기설정");
				}
				//크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
				jrRtn = this.trtCrnReschN6(jrParam);
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YDY1L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				resMsg.setField("MODIFIER"      , modifier  ); //수정자
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN6L004(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN6L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 선재수동창고 Crane Reschedule 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnReschN3(JDTORecord jrParam) throws DAOException {
		String methodNm = "선재수동창고 크레인리스케줄[GdsYsL2RcvSeEJB.trtCrnReschN3] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			jrParam.setResultMsg(methodNm);	//Log Method Name

			JDTORecord jrRtn = null;	//크레인작업지시 전문 Return

			//작업예약 야드스케쥴우선순위 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschWrkBook", logId, methodNm, "작업예약 야드스케쥴우선순위 수정");

			//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschCrnSch", logId, methodNm, "레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");

			//크레인작업지시 대상 설비 조회
			JDTORecordSet jsWoEqp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnReschWoEqp", logId, methodNm, "크레인작업지시 대상 설비 조회");

			int schCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			String msgId = commUtils.trim(jrParam.getFieldString("MSG_ID")); //수신 전문 I/F ID
			msgId = msgId.substring(0, 2);

			jrYdMsg.setField("JMS_TC_CD"         , msgId + "YSL004"         ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("MODIFIER"        , jrParam.getFieldString("MODIFIER")); //수정자

			for (int ii = 0; ii < schCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"                                              ); //야드작업진행상태
				
				//크레인작업지시 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvN3YSL004(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 봉강수동창고 Crane Reschedule 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnReschN4(JDTORecord jrParam) throws DAOException {
		String methodNm = "봉강수동창고 크레인리스케줄[GdsYsL2RcvSeEJB.trtCrnReschN4] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			jrParam.setResultMsg(methodNm);	//Log Method Name

			JDTORecord jrRtn = null;	//크레인작업지시 전문 Return

			//작업예약 야드스케쥴우선순위 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschWrkBook", logId, methodNm, "작업예약 야드스케쥴우선순위 수정");

			//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschCrnSch", logId, methodNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");

			//크레인작업지시 대상 설비 조회
			JDTORecordSet jsWoEqp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnReschWoEqp", logId, methodNm, "크레인작업지시 대상 설비 조회");

			int schCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			String msgId = commUtils.trim(jrParam.getFieldString("MSG_ID")); //수신 전문 I/F ID
			msgId = msgId.substring(0, 2);

			jrYdMsg.setField("JMS_TC_CD"         , msgId + "YSL004"         ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("MODIFIER"        , jrParam.getFieldString("MODIFIER")); //수정자

			for (int ii = 0; ii < schCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"                                              ); //야드작업진행상태
				
				//크레인작업지시 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvN4YSL004(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 선재자동창고 Crane Reschedule 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnReschN5(JDTORecord jrParam) throws DAOException {
		String methodNm = "선재자동창고 크레인리스케줄[GdsYsL2RcvSeEJB.trtCrnReschN5] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			jrParam.setResultMsg(methodNm);	//Log Method Name

			JDTORecord jrRtn = null;	//크레인작업지시 전문 Return

			//작업예약 야드스케쥴우선순위 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschWrkBook", logId, methodNm, "작업예약 야드스케쥴우선순위 수정");

			//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschCrnSch", logId, methodNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");

			//크레인작업지시 대상 설비 조회
			JDTORecordSet jsWoEqp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnReschWoEqp", logId, methodNm, "크레인작업지시 대상 설비 조회");

			int schCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			String msgId = commUtils.trim(jrParam.getFieldString("MSG_ID")); //수신 전문 I/F ID
			msgId = msgId.substring(0, 2);

			jrYdMsg.setField("JMS_TC_CD"         , msgId + "YSL004"         ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("MODIFIER"        , jrParam.getFieldString("MODIFIER")); //수정자

			for (int ii = 0; ii < schCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"                                              ); //야드작업진행상태
				
				//크레인작업지시 전문을 추가
			//	jrRtn = commUtils.addSndData(jrRtn, this.rcvN5YSL004(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 봉강자동창고 Crane Reschedule 처리 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtCrnReschN6(JDTORecord jrParam) throws DAOException {
		String methodNm = "봉강자동창고 크레인리스케줄[GdsYsL2RcvSeEJB.trtCrnReschN6] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			jrParam.setResultMsg(methodNm);	//Log Method Name

			JDTORecord jrRtn = null;	//크레인작업지시 전문 Return

			//작업예약 야드스케쥴우선순위 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschWrkBook", logId, methodNm, "작업예약 야드스케쥴우선순위 수정");

			//크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnReschCrnSch", logId, methodNm, "크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정");

			//크레인작업지시 대상 설비 조회
			JDTORecordSet jsWoEqp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnReschWoEqp", logId, methodNm, "크레인작업지시 대상 설비 조회");

			int schCnt = jsWoEqp.size();

			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			String msgId = commUtils.trim(jrParam.getFieldString("MSG_ID")); //수신 전문 I/F ID
			msgId = msgId.substring(0, 2);

			jrYdMsg.setField("JMS_TC_CD"         , msgId + "YSL004"         ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("MODIFIER"        , jrParam.getFieldString("MODIFIER")); //수정자

			for (int ii = 0; ii < schCnt; ii++) {
				jrYdMsg.setField("YD_EQP_ID"       , jsWoEqp.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "W"                                              ); //야드작업진행상태
				
				//크레인작업지시 전문을 추가
			//	jrRtn = commUtils.addSndData(jrRtn, this.rcvN6YSL004(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 선재수동창고 크레인작업지시요구(N3YSL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN3YSL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재수동창고 크레인작업지시요구[GdsYsL2RcvSeEJB.rcvN3YSL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
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
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = gdsYsComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/* 크레인작업지시요구 크레인스케줄 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchNxYSL004 
			SELECT YD_CRN_SCH_ID
			     , YD_WRK_PROG_STAT
			     , YS_DN_WO_LOC
			     , YD_SCH_CD
			     , SCH_CNT
			  FROM ( SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YS_DN_WO_LOC 
			              , CS.YD_SCH_CD
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0',CS.YD_WRK_PROG_STAT) AS SEQ1
			              , DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,1)               AS SEQ2
			              , DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,CS.YD_SCH_PRIOR) AS YD_SCH_PRIOR
			              , CASE WHEN SUBSTR(CS.YD_SCH_CD,1,4) IN ('KAPC','KBPC','KECH') THEN (SELECT COUNT(*) 
			                                                                              FROM TB_YS_CRNSCH 
			                                                                             WHERE DEL_YN = 'N' 
			                                                                               AND YD_SCH_CD = CS.YD_SCH_CD
			                                                                               AND YD_EQP_ID = :V_YD_EQP_ID
			                                                                           )    
			                     ELSE 0 END  AS SCH_CNT                                                     
			          FROM TB_YS_CRNSCH CS
			              ,(SELECT MIN(YD_WBOOK_ID) AS YD_WBOOK_ID
			                  FROM TB_YS_CRNSCH
			                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) CT
			         WHERE CS.DEL_YN = 'N'
			           AND CS.YD_EQP_ID = :V_YD_EQP_ID
			         ORDER BY SEQ1 DESC, SEQ2, YD_SCH_PRIOR, SCH_CNT DESC, YD_CRN_SCH_ID)
			 WHERE ROWNUM = 1
			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchNxYSL004", logId, methodNm, "크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
				} else {
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규

					//설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT", "1"); //권상지시

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatCrnSchWrkProg", logId, methodNm, "크레인스케줄 야드작업진행상태 수정");

					//크레인스케줄 권상지시단 수정
	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchWoLyr", logId, methodNm, "크레인스케줄 권상지시단 수정");
				}
				
				//크레인작업지시(YSN1L003) 전문 생성
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN3L003", jrParam));

				commUtils.printLog(logId, "크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN3L004(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbIdNxYSL004Gds", logId, methodNm, "작업예약 조회");

					//작업예약이 있으면 크레인스케줄 호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자

						jrRtn = gdsYsComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
					}

					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN3L004(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				
				//PIDEV_F : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN3L004(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강수동창고 크레인작업지시요구(N4YSL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN4YSL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강수동창고 크레인작업지시요구[GdsYsL2RcvSeEJB.rcvN4YSL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		JDTORecord resLoc = JDTORecordFactory.getInstance().create(); //A2 크레인 입고 처리용

		try {
			commUtils.printLog(logId, methodNm, "S+");
			
			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
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
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = gdsYsComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/* 크레인작업지시요구 크레인스케줄 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchNxYSL004 
			SELECT YD_CRN_SCH_ID
			     , YD_WRK_PROG_STAT
			     , YS_UP_WO_LOC
			     , YS_DN_WO_LOC
			     , YD_SCH_CD
			     , SCH_CNT
			     , NVL(TO_YD_BAY_GP,'A') AS TO_YD_BAY_GP 
			  FROM ( SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YS_UP_WO_LOC 
			              , CS.YS_DN_WO_LOC 
			              , CS.YD_SCH_CD
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0',CS.YD_WRK_PROG_STAT) AS SEQ1
			              , DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,1)               AS SEQ2
			              , DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,CS.YD_SCH_PRIOR) AS YD_SCH_PRIOR
			              , CASE WHEN SUBSTR(CS.YD_SCH_CD,1,4) IN ('KAPC','KBPC') THEN (SELECT COUNT(*) 
			                                                                              FROM TB_YS_CRNSCH 
			                                                                             WHERE DEL_YN = 'N' 
			                                                                               AND YD_SCH_CD = CS.YD_SCH_CD
			                                                                               AND YD_EQP_ID = :V_YD_EQP_ID
			                                                                           )    
			                     ELSE 0 END  AS SCH_CNT 
			             , (SELECT SUBSTR(YD_RCPT_PLN_STR_LOC,2,1) 
			                  FROM TB_YS_STOCK 
			                 WHERE SSTL_NO IN ((SELECT MAX(SSTL_NO) AS SSTL_NO
			                                      FROM TB_YS_CRNWRKMTL AA
			                                     WHERE AA.YD_CRN_SCH_ID =CS.YD_CRN_SCH_ID))
			                       ) AS TO_YD_BAY_GP
			             
			          FROM TB_YS_CRNSCH CS
			              ,(SELECT MIN(YD_WBOOK_ID) AS YD_WBOOK_ID
			                  FROM TB_YS_CRNSCH
			                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) CT
			         WHERE CS.DEL_YN = 'N'
			           AND CS.YD_EQP_ID = :V_YD_EQP_ID
			         ORDER BY SEQ1 DESC, SEQ2, YD_SCH_PRIOR, SCH_CNT DESC, YD_CRN_SCH_ID)
			 WHERE ROWNUM = 1

			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchNxYSL004", logId, methodNm, "크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				JDTORecord recTemp = jsSch.getRecord(0);
				
				ydCrnSchId    	 = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat 	 = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));
				String ysDnWoLoc = commUtils.trim(jsSch.getRecord(0).getFieldString("YS_DN_WO_LOC"));
				String ysUpWoLoc = commUtils.trim(jsSch.getRecord(0).getFieldString("YS_UP_WO_LOC"));
				String ysToBay   = commUtils.trim(jsSch.getRecord(0).getFieldString("TO_YD_BAY_GP"));


				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
				} else {
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규

					//설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT", "1"); //권상지시

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatCrnSchWrkProg", logId, methodNm, "크레인스케줄 야드작업진행상태 수정");

					//크레인스케줄 권상지시단 수정
	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchWoLyr", logId, methodNm, "크레인스케줄 권상지시단 수정");
				}
				
				/**********************************************************
				* 2.1.2 PC에서 입고이거나  작업예정동이 B동인 경우  TO위치 재 결정
				**********************************************************/
				
				commUtils.printLog(logId, "크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL"
						+ " ysUpWoLoc =" + ysUpWoLoc
						+ " ysDnWoLoc =" + ysDnWoLoc
						+ " ysToBay =" + ysToBay
						+ " ydEqpId =" + ydEqpId);
				String sTO_BAY = "A";
				
				if (("KAXXXXXX".equals(ysDnWoLoc) && ydEqpId.equals("KACRA2")) || //A동 입고이거나, 임시베드로 저장후 B동 보내거나
						(ysUpWoLoc.startsWith("KAPC") &&ysDnWoLoc.startsWith("KATY") && ysToBay.equals("B") && ydEqpId.equals("KACRA2")
					)){
					
					// B동으로 갈 물량재 SCH
					if(ysUpWoLoc.startsWith("KAPC") &&ysDnWoLoc.startsWith("KATY") && ysToBay.equals("B") && ydEqpId.equals("KACRA2")){
						sTO_BAY = "B";
					}
					
					
					resLoc =  this.ToLocSetRbA(logId,methodNm, recTemp, sTO_BAY);   
					
					String ydDnWrLoc 	= commUtils.trim(resLoc.getFieldString("YS_DN_WO_LOC"));
					String ydTcarSndYn 	= commUtils.trim(resLoc.getFieldString("TCAR_SND_YN"));

					if(sTO_BAY.equals("A")) {  
						if( ydDnWrLoc.startsWith("KATS")){
	
							JDTORecord    recPara2 		= JDTORecordFactory.getInstance().create();
							recPara2.setResultCode(logId);	//Log ID
							recPara2.setResultMsg(methodNm);	//Log Method Name
							recPara2.setField("YD_EQP_ID"	, commUtils.trim(resLoc.getFieldString("YS_DN_WO_LOC").substring(0, 6))); //<<<<----------------------
							jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN6L005BM", recPara2));
	    					
	    					//야드설비상태가 대기이면 내부크레인작업지시요구 전송
	    					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	    					jrYdMsg.setResultCode(logId);	//Log ID
	    					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
	    					jrYdMsg.setResultCode(logId);		//Log ID
	    					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	    					jrYdMsg.setField("MSG_GP"		, "U"   	); //전문구분
	    					jrYdMsg.setField("INFO_GP"		, "I"   	); //정보구분
	    					jrYdMsg.setField("YD_CRN_SCH_ID", ydCrnSchId);
	    					
	    					commUtils.printLog(logId, "작업지시 재송신 ", "SL");
	   			    		jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L003", jrYdMsg));
	    				
	    					commUtils.printLog(logId, "크레인작업지시요구시 TO 위치 결정 성공 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
						} else {
							
							//TO 뒤치 결정  ERROR 이므로 명령선택 대기로 원위치
							jrParam.setField("YD_WRK_PROG_STAT", "W"); //권상대기
			        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatCrnSchWrkProg", logId, methodNm, "크레인스케줄 야드작업진행상태 수정");
			        		
	    					commUtils.printLog(logId, "크레인작업지시요구시 TO 위치 결정 실패 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
	    				}
					} else {
						if( ydDnWrLoc.startsWith("KATC")){
							if(ydTcarSndYn.equals("Y")){
		    					JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
		    					jrYdMsg1.setResultCode(logId);	//Log ID
		    					jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
	
		    					jrYdMsg1.setField("YD_EQP_ID"	, "KXTC01"); //야드설비ID(대차)
		    					jrYdMsg1.setField("YD_BAY_GP"	, "A"); 
		    					jrYdMsg1.setField("YD_L2_ID"	, "N4"); 
		    					jrYdMsg1.setField("MODIFIER"    , "YSSCH"   ); //수정자
	
		    					try {
		    						/**********************************************************
		    						*  대차스케줄 공대차출발지시 처리 (별도 Transaction 으로 처리)
		    						**********************************************************/
		    						EJBConnector tranConn1 = new EJBConnector("default", "GdsYsSchSeEJB", this);
		    						JDTORecord jrRtn1 = (JDTORecord)tranConn1.trx("updTcarSchLevWo", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg1 });
		    						
		    						commUtils.printLog(logId, "작업지시 재송신 ", "SL");
			   			    		jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
		    					} catch (Exception se) {}
							}	
							
//야드설비상태가 대기이면 내부크레인작업지시요구 전송
	    					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	    					jrYdMsg.setResultCode(logId);	//Log ID
	    					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
	    					jrYdMsg.setResultCode(logId);		//Log ID
	    					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	    					jrYdMsg.setField("MSG_GP"		, "U"   	); //전문구분
	    					jrYdMsg.setField("INFO_GP"		, "I"   	); //정보구분
	    					jrYdMsg.setField("YD_CRN_SCH_ID", ydCrnSchId);
	    					
	    					commUtils.printLog(logId, "작업지시 재송신 ", "SL");
	   			    		jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L003", jrYdMsg));
	    				
	    					commUtils.printLog(logId, "크레인작업지시요구시 TO 위치 결정 성공 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
	    					
	    					
						} else {	
							
							//크레인작업지시(YSN4L003) 전문 생성
							jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L003", jrParam));
	    				}						
					}
				} else {
					
					//크레인작업지시(YSN4L003) 전문 생성
					jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN4L003", jrParam));

				}
				
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					resMsg.setField("MODIFIER"      , modifier  ); //수정자
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN4L004(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbIdNxYSL004Gds", logId, methodNm, "작업예약 조회");

					//작업예약이 있으면 크레인스케줄 요구 호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   	, modifier); //수정자

						jrRtn = gdsYsComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
					}

					resMsg.setField("YD_L3_HD_RS_CD"	, "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     	, ydL3Msg); //야드L3MESSAGE
					resMsg.setField("MODIFIER"      , modifier  ); //수정자
					jrRtn = commUtils.addSndData(jrRtn	, gdsYsComm.getYSN4L004(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				
				//PIDEV_F : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN4L004(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재자동창고 크레인작업지시요구(N5YSL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN5YSL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고 크레인작업지시요구[GdsYsL2RcvSeEJB.rcvN5YSL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
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
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = gdsYsComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/* 크레인작업지시요구 크레인스케줄 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchNxYSL004 
			SELECT YD_CRN_SCH_ID
			     , YD_WRK_PROG_STAT
			     , YS_DN_WO_LOC
			     , YD_SCH_CD
			     , SCH_CNT
			  FROM ( SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YS_DN_WO_LOC 
			              , CS.YD_SCH_CD
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0',CS.YD_WRK_PROG_STAT) AS SEQ1
			              , DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,1)               AS SEQ2
			              , DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,CS.YD_SCH_PRIOR) AS YD_SCH_PRIOR
			              , CASE WHEN SUBSTR(CS.YD_SCH_CD,1,4) IN ('KAPC','KBPC','KECH') THEN (SELECT COUNT(*) 
			                                                                              FROM TB_YS_CRNSCH 
			                                                                             WHERE DEL_YN = 'N' 
			                                                                               AND YD_SCH_CD = CS.YD_SCH_CD
			                                                                               AND YD_EQP_ID = :V_YD_EQP_ID
			                                                                           )    
			                     ELSE 0 END  AS SCH_CNT                                                     
			          FROM TB_YS_CRNSCH CS
			              ,(SELECT MIN(YD_WBOOK_ID) AS YD_WBOOK_ID
			                  FROM TB_YS_CRNSCH
			                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) CT
			         WHERE CS.DEL_YN = 'N'
			           AND CS.YD_EQP_ID = :V_YD_EQP_ID
			         ORDER BY SEQ1 DESC, SEQ2, YD_SCH_PRIOR, SCH_CNT DESC, YD_CRN_SCH_ID)
			 WHERE ROWNUM = 1
			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchNxYSL004", logId, methodNm, "크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
				} else {
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규

					//설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT", "1"); //권상지시

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatCrnSchWrkProg", logId, methodNm, "크레인스케줄 야드작업진행상태 수정");

					//크레인스케줄 권상지시단 수정
	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchWoLyr", logId, methodNm, "크레인스케줄 권상지시단 수정");
				}
				
				//크레인작업지시(YSN1L003) 전문 생성
				if(!ydEqpId.substring(2, 4).equals("SC")) {
					jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN5L003", jrParam));
				}
				

				commUtils.printLog(logId, "크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN5L004(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbIdNxYSL004Gds", logId, methodNm, "작업예약 조회");

					//작업예약이 있으면 크레인스케줄 호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자

						jrRtn = gdsYsComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
					}

					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN5L004(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				
				//PIDEV_F : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN5L004(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강자동창고 크레인작업지시요구(N6YSL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN6YSL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동창고 크레인작업지시요구[GdsYsL2RcvSeEJB.rcvN6YSL004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //수정자(Backup Only)
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
			resMsg.setField("YD_L3_HD_RS_CD"  , "JR99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:크레인작업지시요구 수신처리"); //야드L3MESSAGE(Error)

			//조회 및 등록용
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"    , ydEqpId   ); //야드설비ID
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			/**********************************************************
			* 1. 설비상태 Check
			**********************************************************/
			JDTORecord jrChk = gdsYsComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
			/* 크레인작업지시요구 크레인스케줄 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchNxYSL004 
			SELECT YD_CRN_SCH_ID
			     , YD_WRK_PROG_STAT
			     , YS_DN_WO_LOC
			     , YD_SCH_CD
			     , SCH_CNT
			  FROM ( SELECT CS.YD_CRN_SCH_ID
			              , CS.YD_WRK_PROG_STAT
			              , CS.YS_DN_WO_LOC 
			              , CS.YD_SCH_CD
			              , DECODE(CS.YD_WRK_PROG_STAT,'W','0',CS.YD_WRK_PROG_STAT) AS SEQ1
			              , DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,1)               AS SEQ2
			              , DECODE(CS.YD_WBOOK_ID,CT.YD_WBOOK_ID,0,CS.YD_SCH_PRIOR) AS YD_SCH_PRIOR
			              , CASE WHEN SUBSTR(CS.YD_SCH_CD,1,4) IN ('KAPC','KBPC','KECH') THEN (SELECT COUNT(*) 
			                                                                              FROM TB_YS_CRNSCH 
			                                                                             WHERE DEL_YN = 'N' 
			                                                                               AND YD_SCH_CD = CS.YD_SCH_CD
			                                                                               AND YD_EQP_ID = :V_YD_EQP_ID
			                                                                           )    
			                     ELSE 0 END  AS SCH_CNT                                                     
			          FROM TB_YS_CRNSCH CS
			              ,(SELECT MIN(YD_WBOOK_ID) AS YD_WBOOK_ID
			                  FROM TB_YS_CRNSCH
			                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID) CT
			         WHERE CS.DEL_YN = 'N'
			           AND CS.YD_EQP_ID = :V_YD_EQP_ID
			         ORDER BY SEQ1 DESC, SEQ2, YD_SCH_PRIOR, SCH_CNT DESC, YD_CRN_SCH_ID)
			 WHERE ROWNUM = 1
			 */
			JDTORecordSet jsSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchNxYSL004", logId, methodNm, "크레인스케줄 조회");

			if (jsSch.size() > 0) {
				/**********************************************************
				* 2.1 크레인스케줄이 존재하면 수신된 야드작업진행상태에 상관없이 작업지시 전송
				**********************************************************/
				ydCrnSchId    = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_CRN_SCH_ID"   ));
				ydWrkProgStat = commUtils.trim(jsSch.getRecord(0).getFieldString("YD_WRK_PROG_STAT"));

				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID

				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.1.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 재지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "U"); //전문구분 - 재지시
				} else {
					/**********************************************************
					* 2.1.2 대기[W] 이면 다음 작업지시 전송
					**********************************************************/
					jrParam.setField("MSG_GP", "I"); //전문구분 - 신규

					//설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "1"); //권상작업지시

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");

	        		//크레인스케줄 야드작업진행상태 수정
					jrParam.setField("YD_WRK_PROG_STAT", "1"); //권상지시

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatCrnSchWrkProg", logId, methodNm, "크레인스케줄 야드작업진행상태 수정");
				}

				if(!ydEqpId.substring(2, 4).equals("SC")) {
					jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN6L003", jrParam));
				}

				commUtils.printLog(logId, "크레인작업지시요구 작업지시 전송 [ " + ydEqpId + " : " + ydWrkProgStat +  " - " + ydCrnSchId + " ]", "SL");
			} else {
				/**********************************************************
				* 2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
				*    2.1 권상지시[1], 권상완료[2], 권하지시[3] 이면 Error 처리
				*    2.2 권하완료[4] 이면 스케줄을 생성
				*    2.3 명령선택대기[W] 이면 응답 전문을 전송 -> 2.2로 통합
				**********************************************************/
				if ("1".equals(ydWrkProgStat) || "2".equals(ydWrkProgStat) || "3".equals(ydWrkProgStat)) {
					/**********************************************************
					* 2.2.1 재지시요구 시
					**********************************************************/
					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , "크레인[" + ydEqpId + "-" + ydWrkProgStat + "] 작업지시 없음"); //야드L3MESSAGE
					resMsg.setField("MODIFIER"      , modifier  ); //수정자
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN6L004(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(재지시요구) 작업지시 없음 [ " + ydEqpId + " : " + ydWrkProgStat + " - " + ydCrnSchId + " ]", "SL");
				} else {
					/**********************************************************
					* 2.2.2 대기상태[W], 권하완료[4] 지시요구
					**********************************************************/
					//크레인작업지시가 없으면 설비의 야드설비상태 수정
					jrParam.setField("YD_EQP_STAT", "W"); //대기(Wait)

	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
	        		

	        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name

	    			//작업예약 조회
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbIdNxYSL004Gds", logId, methodNm, "작업예약 조회");

					//작업예약이 있으면 크레인스케줄   호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자

						jrRtn = gdsYsComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
					}

					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					resMsg.setField("MODIFIER"      , modifier  ); //수정자
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN6L004(resMsg));

					commUtils.printLog(logId, "크레인작업지시요구(다음지시) " + ydL3Msg + " [ " + ydEqpId + " : " + ydWrkProgStat + " ]", "SL");
				}
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			try {
				
				//PIDEV_F : 정상SET후  ERROR 발생한 경우								
				if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
					resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
					resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
				}
				
				//크레인작업실적응답 전문 전송
				EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN5L004(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 선재수동창고 저장품제원요구(N3YSL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN3YSL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재수동창고 저장품제원요구[GdsYsL2RcvSeEJB.rcvN3YSL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); //야드적치Bed번호
			String stlNo        = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"         )); //재료번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			}

			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//저장위치별
				if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
					throw new Exception("야드동구분(YD_BAY_GP) 없음");
				} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
					throw new Exception("야드설비구분(YD_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(stlNo)) {
					throw new Exception("재료번호(SSTL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YSN3L002) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호
			jrParam.setField("YD_GP"          , ydGp                                 ); //야드구분
			jrParam.setField("SSTL_NO"         , stlNo                                ); //재료번호

			//전송Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN3L002", jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강수동창고 저장품제원요구(N4YSL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN4YSL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강수동창고 저장품제원요구[GdsYsL2RcvSeEJB.rcvN4YSL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); //야드적치Bed번호
			String stlNo        = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"         )); //재료번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			}

			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//저장위치별
				if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
					throw new Exception("야드동구분(YD_BAY_GP) 없음");
				} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
					throw new Exception("야드설비구분(YD_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(stlNo)) {
					throw new Exception("재료번호(SSTL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YSN4L002) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호
			jrParam.setField("YD_GP"          , ydGp                                 ); //야드구분
			jrParam.setField("SSTL_NO"         , stlNo                                ); //재료번호

			//전송Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN4L002", jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재자동창고 저장품제원요구(N5YSL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN5YSL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고 저장품제원요구[GdsYsL2RcvSeEJB.rcvN5YSL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); //야드적치Bed번호
			String stlNo        = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"         )); //재료번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			}

			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//저장위치별
				if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
					throw new Exception("야드동구분(YD_BAY_GP) 없음");
				} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
					throw new Exception("야드설비구분(YD_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(stlNo)) {
					throw new Exception("재료번호(SSTL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YSN5L002) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호
			jrParam.setField("YD_GP"          , ydGp                                 ); //야드구분
			jrParam.setField("SSTL_NO"         , stlNo                                ); //재료번호

			//전송Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN5L002", jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강자동창고 저장품제원요구(N6YSL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN6YSL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동창고 저장품제원요구[GdsYsL2RcvSeEJB.rcvN6YSL002] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); //야드적치Bed번호
			String stlNo        = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"         )); //재료번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			}

			if ("1".equals(ydInfoSyncCd) || "2".equals(ydInfoSyncCd) || "3".equals(ydInfoSyncCd) || "4".equals(ydInfoSyncCd)) {
				//저장위치별
				if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
					throw new Exception("야드동구분(YD_BAY_GP) 없음");
				} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
					throw new Exception("야드설비구분(YD_EQP_GP) 없음");
				}
			} else {
				//재료별
				if ("".equals(stlNo)) {
					throw new Exception("재료번호(SSTL_NO) 없음");
				}
			}

			/**********************************************************
			* 2. 저장품제원(YSN6L002) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호
			jrParam.setField("YD_GP"          , ydGp                                 ); //야드구분
			jrParam.setField("SSTL_NO"         , stlNo                                ); //재료번호

			//전송Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN6L002", jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 선재수동창고 저장위치제원요구(N3YSL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN3YSL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재수동창고 저장위치제원요구[GdsYsL2RcvSeEJB.rcvN3YSL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); //야드적치Bed번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원(YSN3L001) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호

			//전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN3L001", jrParam));
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강수동창고 저장위치제원요구(N4YSL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN4YSL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강수동창고 저장위치제원요구[GdsYsL2RcvSeEJB.rcvN4YSL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); //야드적치Bed번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원(YSN4L001) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호

			//전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN4L001", jrParam));
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재자동창고 저장위치제원요구(N5YSL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN5YSL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고 저장위치제원요구[GdsYsL2RcvSeEJB.rcvN5YSL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); //야드적치Bed번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원(YSN5L001) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호

			//전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN5L001", jrParam));
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강자동창고 저장위치제원요구(N6YSL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN6YSL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동창고 저장위치제원요구[GdsYsL2RcvSeEJB.rcvN6YSL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			String ydGp         = commUtils.trim(rcvMsg.getFieldString("YD_GP"          )); //야드구분
			String ydBayGp      = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP"      )); //야드동구분
			String ydEqpGp      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP"      )); //야드설비구분
			String ydStkColNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO"  )); //야드적치열번호
			String ydStkBedNo   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"  )); //야드적치Bed번호
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydGp)) {
				throw new Exception("야드구분(YD_GP) 없음");
			} else if ("".equals(ydBayGp) && (!"".equals(ydEqpGp) || "".equals(ydStkColNo))) {
				throw new Exception("야드동구분(YD_BAY_GP) 없음");
			} else if ("".equals(ydEqpGp) && !"".equals(ydStkColNo)) {
				throw new Exception("야드설비구분(YD_EQP_GP) 없음");
			}

			/**********************************************************
			* 2. 저장위치제원(YSN6L001) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호

			//전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN6L001", jrParam));
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 대차이동실적(N4YSL007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN4YSL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차이동실적[GdsYsL2RcvSeEJB.rcvN4YSL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"      )); //야드설비ID
			String ydTcarMoveGp = commUtils.trim(rcvMsg.getFieldString("YD_TCAR_MOVE_GP")); //야드대차이동구분
			String ydBayGp1     = commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP1"     )); //야드동구분1
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			} else if ("".equals(ydTcarMoveGp)) {
				throw new Exception("대차이동구분(YD_TCAR_MOVE_GP) 없음");
			} else if ("".equals(ydBayGp1)) {
				throw new Exception("현재동(YD_BAY_GP1) 없음");
			}

			if (!"S".equals(ydTcarMoveGp) && !"E".equals(ydTcarMoveGp)) {
				commUtils.printLog(logId, "대차이동구분[" + ydTcarMoveGp + "]이 'S' 또는 'E'가 아니므로 종료", "SL");
				commUtils.printLog(logId, methodNm, "S-");
				return null;
			}

			String ydStkColGp = ydEqpId.substring(0, 1) + ydBayGp1 + ydEqpId.substring(2); //야드적치열구분(현재동)
			
			/**********************************************************
			* 2. 설비 야드현재동구분, 대차스케줄 야드차량진행상태 수정
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_EQP_ID"      , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_TCAR_MOVE_GP", ydTcarMoveGp); //야드대차이동구분
			jrParam.setField("YD_STK_COL_GP"  , ydStkColGp  ); //야드적치열구분(현재동)
			jrParam.setField("MODIFIER"       , modifier    ); //수정자
			//야드현재동구분
			if ("S".equals(ydTcarMoveGp)) {
				jrParam.setField("YD_CURR_BAY_GP", "");
			} else {
				jrParam.setField("YD_CURR_BAY_GP", ydBayGp1);
			}
			
			//설비 Table 야드현재동구분 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrBay", logId, methodNm, "야드현재동구분 수정");
			
			//대차스케줄 야드차량진행상태 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updTcarProgStat", logId, methodNm, "야드차량진행상태 수정");

			/**********************************************************
			* 3. 대차스케줄 조회
			**********************************************************/
			JDTORecordSet jsTcar = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSch", logId, methodNm, "대차스케줄조회");


			if (jsTcar == null || jsTcar.size() == 0) {
				throw new Exception("대차스케줄[" + ydEqpId + "] 없음");
			}

			JDTORecord jrTcar = jsTcar.getRecord(0);
			String ydTcarSchId       = commUtils.trim(jrTcar.getFieldString("YD_TCAR_SCH_ID"        )); //야드대차스케쥴ID
			String ydCarProgStat     = commUtils.trim(jrTcar.getFieldString("YD_CAR_PROG_STAT"      )); //야드차량진행상태
			String ydCarldWrkbookId  = commUtils.trim(jrTcar.getFieldString("YD_CARLD_WRK_BOOK_ID"  )); //야드상차작업예약ID
			String ydCarudWrkbookId  = commUtils.trim(jrTcar.getFieldString("YD_CARUD_WRK_BOOK_ID"  )); //야드하차작업예약ID
			String ydCarldStopLoc    = commUtils.trim(jrTcar.getFieldString("YD_CARLD_STOP_LOC"     )); //야드상차정지위치
			String ydCarudStopLoc    = commUtils.trim(jrTcar.getFieldString("YD_CARUD_STOP_LOC"     )); //야드하차정지위치
			String ydStkBedActStatLd = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_LD")); //야드적치Bed활성상태(상차정지위치)
			String ydStkBedActStatUd = commUtils.trim(jrTcar.getFieldString("YD_STK_BED_ACT_STAT_UD")); //야드적치Bed활성상태(하차정지위치)
			//업무기준(YDB034) 야드스케쥴기동구분(Y:기동,N:기동안함) : 출발 또는 도착 중 하나 이상은 반드시 'Y'로 되어 있어야 함
			String crnSchYn          = commUtils.trim(jrTcar.getFieldString("CRN_SCH_YN"            )); //크레인스케줄기동여부
			String ydWbookId         = ""; //작업예약ID
			String ydSchReqGp        = ""; //야드스케쥴요청구분

			commUtils.printLog(logId, "대차[" + ydEqpId + "] 스케줄 >> 대차스케쥴ID:" + ydTcarSchId + ", 차량진행:" + ydCarProgStat + ", 크레인스케줄:" + crnSchYn
					                + ", 상차작업:" + ydCarldWrkbookId + "-" + ydCarldStopLoc + ", 하차작업:" + ydCarudWrkbookId + "-" + ydCarudStopLoc, "SL");
			
			/**********************************************************
			* 4. 적치Bed, 적치단 Table 상태 수정
			**********************************************************/
			jrParam.setField("YD_STK_BED_NO" , "01"       ); //야드적치Bed번호
			jrParam.setField("YD_TCAR_SCH_ID", ydTcarSchId); //야드대차스케쥴ID
			
			if ("A".equals(ydCarProgStat)) {
				ydSchReqGp = "2"; //영대차출발 : 하차출발(A)

				//영대차출발이면 출발위치 적치Bed 비활성화 처리
				jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc); //야드적치열구분
				jrParam.setField("YD_STK_BED_ACT_STAT", "C"       ); //야드적치Bed활성상태(비활성화)
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "적치Bed 활성상태 수정");
				
				//출발위치 적치단 재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 삭제");

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarudWrkbookId; //야드하차작업예약ID

					//하차위치 적치Bed 활성화 처리
					jrParam.setField("YD_STK_COL_GP"      , ydCarudStopLoc); //적치Bed 야드적치열구분(하차정지위치)
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"           ); //적치Bed 야드적치Bed활성상태(활성)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "적치Bed 활성상태 수정");

					//하차위치 적치단 재료번호 등록
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrTCarStl", logId, methodNm, "대차이동실적 적치단 재료번호 등록");

					//설비 Table 야드현재동구분 미리 수정
					jrParam.setField("YD_CURR_BAY_GP", ydCarudStopLoc.substring(1, 2));
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrBay", logId, methodNm, "야드현재동구분 수정");
					
				}
			} else if ("B".equals(ydCarProgStat)) {
				ydSchReqGp = "3"; //영대차도착 : 하차도착(B)

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarudWrkbookId; //야드하차작업예약ID
				}

				//영대차도착이고 적치Bed 비활성화 이면 활성화
				jrParam.setField("YD_STK_COL_GP", ydStkColGp); //적치Bed 야드적치열구분(현위치)

				if (!"L".equals(ydStkBedActStatUd)) {
					//현위치 적치Bed 활성화 처리
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"); //적치Bed 야드적치Bed활성상태(활성)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "적치Bed 활성상태 수정");
				}
				
				/*
				 * 영대차 하차 출발 실적이 L2에서 안오는 경우가 많아 출발지 적치단 재료번호 삭제기능 추가
				 * 2019.01.03 윤재광
				 */
				{
					JDTORecord jrTmp = JDTORecordFactory.getInstance().create();
					String tmpColGp = "";
					if(ydStkColGp.equals("KATC01")) tmpColGp = "KBTC01";
					else 							tmpColGp = "KATC01";
					
					jrTmp.setField("YD_STK_COL_GP" , tmpColGp	); //야드적치열구분
					jrTmp.setField("YD_STK_BED_NO" , "01"       ); //야드적치Bed번호
					jrTmp.setField("MODIFIER"      , "ClearCut" ); //수정자
						
					//출발위치 적치단 재료 삭제
					commDao.update(jrTmp, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 삭제");
				}

				//하차위치 적치단 재료번호 등록 -> 혹시 정보가 맞지 않을 수도 있으므로 무조건 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrTCarStl", logId, methodNm, "대차이동실적 적치단 재료번호 등록");
			} else if ("1".equals(ydCarProgStat)) {
				ydSchReqGp = "5"; //공대차출발 : 상차출발(1)

				//공대차출발이면 출발위치 적치Bed 비활성화 처리
				jrParam.setField("YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분
				jrParam.setField("YD_STK_BED_ACT_STAT", "C"       ); //야드적치Bed활성상태(비활성화)
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "적치Bed 활성상태 수정");

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarldWrkbookId; //야드상차작업예약ID

					//상차위치 적치Bed 활성화 처리
					jrParam.setField("YD_STK_COL_GP"      , ydCarldStopLoc); //야드적치열구분(상차정지위치)
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"           ); //적치Bed 야드적치Bed활성상태(활성)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "적치Bed 활성상태 수정");
				
					//설비 Table 야드현재동구분 미리 수정
					jrParam.setField("YD_CURR_BAY_GP", ydCarldStopLoc.substring(1, 2));
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrBay", logId, methodNm, "야드현재동구분 수정");
				}
			} else {
				ydSchReqGp = "6"; //공대차도착 : 상차도착(2) or 상차대기(0)

				//크레인스케줄기동여부 'Y'이면
				if ("Y".equals(crnSchYn)) {
					ydWbookId = ydCarldWrkbookId; //야드상차작업예약ID
				}

				//공대차도착이고 적치Bed 비활성화 이면 활성화
				if (!"L".equals(ydStkBedActStatLd)) {
					//현위치 적치Bed 활성화 처리
					jrParam.setField("YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분(현위치)
					jrParam.setField("YD_STK_BED_ACT_STAT", "L"       ); //야드적치Bed활성상태(활성)
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "적치Bed 활성상태 수정");
				}
			}

			/**********************************************************
			* 5. 야드저장위치제원(YSN4L001) 전문 조회
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD", "4"       ); //야드정보동기화코드(Bed)
			jrParam.setField("YS_STK_COL_GP"  , ydStkColGp); //야드적치열구분(현재동)
			jrParam.setField("YS_STK_BED_NO"  , "01"); //야드적치Bed

			//전송Data 조회
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN4L001", jrParam));

			/**********************************************************
			* 6. 크레인스케줄(YDYDJ400) 전송
			**********************************************************/
			//크레인스케줄기동여부 'Y'이고 작업예약ID가 있으면 크레인스케줄 전송
			if ("Y".equals(crnSchYn) && !"".equals(ydWbookId)) {
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name

				jrYdMsg.setField("YD_WBOOK_ID"  , ydWbookId ); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_ST_GP" , "A"       ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("YD_SCH_REQ_GP", ydSchReqGp); //야드스케쥴요청구분
				jrYdMsg.setField("MODIFIER"   , modifier  ); //수정자
				
				//크레인스케줄 전문
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getCrnSchMsg(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 설비운전모드전환
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvYSYSJ902(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비운전모드전환[GdsYsL2RcvSeEJB.rcvYSYSJ902] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		String L2_MC = "";
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = false;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId      = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"      )); //야드설비ID
			String ydEqpWrkMode = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE")); //야드설비작업Mode(1:On-Line, 0:Off-Line)
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER"     )); //수정자(Backup Only)
			String brGp         = ""; //고장복구구분
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			//크레인작업실적응답 전문 전송여부를 Check
			if ((ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
				resYn = true;
			}

			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydEqpWrkMode); //야드작업진행상태(야드설비작업Mode)
			resMsg.setField("YD_L2_WR_GP"     , "M"         ); //야드L2실적구분(운전모드변경)
			resMsg.setField("YD_L3_HD_RS_CD"  , "EM99"      ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:설비운전모드전환 수신처리"); //야드L3MESSAGE(Error)

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "EM01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "EM02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydEqpWrkMode)) {
				ydL3HdRsCd = "EM03";
				ydL3Msg    = "오류:설비작업Mode 없음";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			if ("1".equals(ydEqpWrkMode)) {
				brGp = "R";	//복구
			} else {
				brGp = "B";	//고장
				ydEqpWrkMode = "0";
			}

			/**********************************************************
			* 2. 설비작업Mode Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"      , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_WRK_MODE", ydEqpWrkMode); //야드설비작업Mode
			jrParam.setField("BR_GP"          , brGp        ); //고장복구구분
			jrParam.setField("MODIFIER"       , modifier    ); //수정자

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태조회");

			if (jsChk == null || jsChk.size() == 0) {
				//설비 Table 존재유무 Check
				ydL3HdRsCd = "EM11";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 정보 없음";
			} else if (ydEqpWrkMode.equals(commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE")))) {
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "EM12";
				ydL3Msg = "오류:현재 설비작업Mode와 동일";
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 3. 설비의 야드설비작업Mode 수정
			**********************************************************/
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqp 
			UPDATE TB_YS_EQP
			   SET MODIFIER = :V_MODIFIER
				 , MOD_DDTT = SYSDATE
				 , YD_EQP_WRK_MODE = :V_YD_EQP_WRK_MODE
			WHERE YD_EQP_ID = :V_YD_EQP_ID
			*/
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqp", logId, methodNm, "설비 수정");
			

			/**********************************************************
			* 4. 크레인 리스케줄
			*  - 고장복구구분 [R:복구 리스케줄, B:고장 리스케줄]
			*  - 작업예약 야드스케쥴우선순위 수정
			*  - 크레인스케줄 야드스케쥴우선순위, 야드설비ID 수정
			*  - 대기상태인 야드설비ID에 해당하는 크레인작업지시 전문 추가
			**********************************************************/
			L2_MC = commUtils.getYsGpBayToL2(ydEqpId);
			
			if ("CR".equals(ydEqpId.substring(2, 4))) {
				//크레인 리스케줄
				jrParam.setField("MSG_ID", msgId); //수신 전문 I/F ID
				if(L2_MC.equals("N6")) {
					jrRtn = this.trtCrnReschN6(jrParam);
				} else if(L2_MC.equals("N4")) {
					jrRtn = this.trtCrnReschN4(jrParam);
				} else if(L2_MC.equals("N5")) {
					jrRtn = this.trtCrnReschN5(jrParam);
				} else if(L2_MC.equals("N3")) {
					jrRtn = this.trtCrnReschN3(jrParam);
				}
			}
			
			/**********************************************************
			* 5. 크레인작업실적응답 전문 전송(YDY1L005)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				resMsg.setField("MODIFIER"      , modifier  ); //수정자
				if(L2_MC.equals("N6")) {
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN6L004(resMsg));
				} else if(L2_MC.equals("N4")) {
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN4L004(resMsg));
				} else if(L2_MC.equals("N5")) {
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN5L004(resMsg));
				} else if(L2_MC.equals("N3")) {
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN3L004(resMsg));
				}

			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);

					if(L2_MC.equals("N6")) {
						resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN6L004(resMsg) });
					} else if(L2_MC.equals("N4")) {
						resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN4L004(resMsg) });
					} else if(L2_MC.equals("N5")) {
						resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN5L004(resMsg) });
					} else if(L2_MC.equals("N3")) {
						resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN3L004(resMsg) });
					}

				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 선재야드 크레인권상실적(N3YSL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN3YSL005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재야드 크레인권상실적[GdsYsL2RcvSeEJB.rcvN3YSL005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LAYER"  )); //야드권상실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydDnWoLoc     = ""; //야드권하지시위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권상실적 수신처리"); //야드L3MESSAGE(Error)

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
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호
			
			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch", logId, methodNm, "크레인스케줄상태 조회");

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydDnWoLoc       = commUtils.trim(jrChk.getFieldString("YS_DN_WO_LOC"    )); //야드권하지시위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			String currDt = commUtils.getDateTime14(); //현재시각
			

			/**********************************************************
			* 4. 권상실적위치가 대차(하차)
			* 4.1 대차 하차 정보 등록
			*   - 대차이송재료 삭제
			*   - 대차스케줄 삭제 : 하차완료 시
			**********************************************************/
			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 5.1 구내운송 소재차량하차개시(YSTSJ009) 전송
			* 5.2 차량이송재료 삭제
			* 5.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			if("TR".equals(ydUpWrLoc.substring(2, 4))) {
				//차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)
				recPara.setField("YD_WBOOK_ID" , ydWbookId ); 
				recPara.setField("MODIFIER" , modifier     ); 

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId 
				--크레인권상실적 하차 차량스케줄 작업예약ID 수정 
				MERGE INTO TB_YS_CARSCH TS USING (
				SELECT YD_CAR_SCH_ID
				      ,YD_WBOOK_ID
				  FROM (SELECT TS.YD_CAR_SCH_ID
				              ,WM.YD_WBOOK_ID
				          FROM TB_YS_CARSCH     TS
				              ,TB_YS_CARFTMVMTL TM
				              ,TB_YS_WRKBOOKMTL WM
				         WHERE WM.SSTL_NO        = TM.SSTL_NO
				           AND TM.YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				           AND WM.YD_WBOOK_ID   = :V_YD_WBOOK_ID
				           AND WM.DEL_YN        = 'N'
				           AND TM.DEL_YN        = 'N'
				           AND TS.DEL_YN        = 'N'
				           AND NOT EXISTS (SELECT YD_CAR_SCH_ID
				                             FROM TB_YS_CARSCH
				                            WHERE YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				                              AND DEL_YN               = 'N')
				         ORDER BY TS.YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1
				) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
				     TS.MODIFIER             = :V_MODIFIER
				    ,TS.MOD_DDTT             = SYSDATE
				    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_WBOOK_ID
				 */   
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId", logId, methodNm, "차량스케줄(하차) 수정");
				
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				recPara.setField("WR_DT" 		 , commUtils.getDateTime14()  ); 
				recPara.setField("YS_STK_COL_GP" , ydUpWrLoc.substring(0, 6)     ); 
				//구내운송 소재차량하차개시
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ009", recPara));
				
				
				recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				recPara.setField("MODIFIER" , modifier     ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel 
				--크레인권상실적 하차 차량이송재료 삭제 
				MERGE INTO TB_YS_CARFTMVMTL TM USING (
				SELECT TS.YD_CAR_SCH_ID
				      ,CM.SSTL_NO
				  FROM TB_YS_CRNSCH    CS
				      ,TB_YS_CRNWRKMTL CM
				      ,TB_YS_CARSCH    TS
				 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = TS.YD_CARUD_WRK_BOOK_ID
				   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CM.DEL_YN        = 'N'
				   AND TS.DEL_YN        = 'N'
				) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO)
				WHEN MATCHED THEN UPDATE SET
					 TM.MODIFIER = :V_MODIFIER
				    ,TM.MOD_DDTT = SYSDATE
				    ,TM.DEL_YN   = 'Y'
				*/    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel", logId, methodNm, "차량이송재료 삭제");
				
				//하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", ydCrnSchId ); 
				recPara.setField("YD_WBOOK_ID" 	, ydWbookId); 
				recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
				recPara.setField("MODIFIER" 	, modifier     ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUd
				--크레인권상실적 하차 차량스케줄 수정 
				MERGE INTO TB_YS_CARSCH TS USING (
				SELECT TS.YD_CAR_SCH_ID
				      ,COUNT(ST.SSTL_NO)        AS YD_EQP_WRK_SH
				      ,NVL(SUM(ST.YD_MTL_WT),0) AS YD_EQP_WRK_WT
				      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN 'D'                                   --하차개시
				       ELSE MIN(TS.YD_CAR_PROG_STAT) END AS YD_CAR_PROG_STAT
				      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
				       ELSE MIN(TS.YD_CARUD_ST_DT) END AS YD_CARUD_ST_DT
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				      ,TB_YS_STOCK      ST
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TM.SSTL_NO              = ST.SSTL_NO(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID
				) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER         = :V_MODIFIER
				    ,TS.MOD_DDTT         = SYSDATE
				    ,TS.YD_EQP_WRK_SH    = DD.YD_EQP_WRK_SH
				    ,TS.YD_EQP_WRK_WT    = DD.YD_EQP_WRK_WT
				    ,TS.YD_CAR_PROG_STAT = DD.YD_CAR_PROG_STAT
				    ,TS.YD_CARUD_ST_DT   = DD.YD_CARUD_ST_DT
				*/    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUd", logId, methodNm, "하차 차량스케줄 수정 ");
				
			}
			
			/**********************************************************
			* 6. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 6.1 구내운송 소재차량상차개시(YSTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 6.2 출하관리출하상차개시(YSDSJ006) 전송
			*   - 야드차량사용구분이 출하차량(G)
			* 6.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("TR".equals(ydDnWoLoc.substring(2, 4))) {
				//차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" , ydWbookId     ); 
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd 
				--크레인권상실적 상차 차량스케줄 조회 
				SELECT TS.YD_CAR_SCH_ID               --야드차량스케쥴ID
				      ,TS.YD_CAR_USE_GP               --야드차량사용구분
				      ,TS.TRN_EQP_CD                  --운송장비코드
				      ,TS.SPOS_WLOC_CD                --발지개소코드
				      ,SC.YD_PNT_CD AS SPOS_YD_PNT_CD --발지야드포인트코드
				      ,DECODE(SC.YD_CAR_USE_GP,'L',
				       NVL(MV.ARR_WLOC_CD,SF_SLAB_YD_ARR_WLOC_CD(MV.YD_AIM_YD_GP))) AS ARR_WLOC_CD --착지개소코드
				      ,TS.CARD_NO                     --카드번호
				      ,TS.CAR_NO                      --차량번호
				      ,TS.TRANS_ORD_DATE              --운송작업지시일자
				      ,TS.TRANS_ORD_SEQNO             --운송작업지시순번
				  FROM TB_YS_CRNSCH  CS
				      ,TB_YS_STKCOL  SC
				      ,TB_YS_CARSCH  TS
				      ,(SELECT WM.YD_WBOOK_ID
				              ,WM.YD_AIM_YD_GP
				              ,MV.ARR_WLOC_CD
				          FROM TB_PB_STLFRTOMOVE MV
				              ,(SELECT WB.YD_WBOOK_ID
				                      ,WB.YD_AIM_YD_GP
				                      ,WM.SSTL_NO
				                  FROM TB_YS_WRKBOOK    WB
				                      ,TB_YS_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1) WM
				         WHERE MV.SSTL_NO = WM.SSTL_NO
				           AND MV.TRANSWORD_SEQNO = (SELECT MAX(MM.TRANSWORD_SEQNO)
				                                       FROM TB_PB_STLFRTOMOVE MM
				                                      WHERE MM.SSTL_NO = MV.SSTL_NO)) MV
				 WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = MV.YD_WBOOK_ID(+)
				   AND SC.YS_STK_COL_GP = SUBSTR(CS.YS_DN_WO_LOC,1,6)
				   AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				     OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				   AND TS.YD_CAR_PROG_STAT IN ('2','3') --상차도착,검수
				   AND TS.DEL_YN = 'N'

					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd", logId, methodNm, "차량하차스케줄 정보 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
				
					//상차개시 전문
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					
					if("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
						//구내운송 소재차량상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YSTSJ007"); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        , commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"    ))); //운송장비코드
						jrYdMsg.setField("SPOS_WLOC_CD"      , commUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"  ))); //발지개소코드
						jrYdMsg.setField("SPOS_YD_PNT_CD"    , commUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))); //발지야드포인트코드
						jrYdMsg.setField("ARR_WLOC_CD"       , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   ))); //착지개소코드
						jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    ); //운송작업시작일시
					} else {
						if(!commUtils.trim(jrChk.getFieldString("CMBN_CARLD_YN")).equals("E")) {  //복수동 2차 상차도 도착인 경우 제외
							
							//PIDEV
//							String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
							
							//출하관리 상차개시							
//							if("Y".equals(sApplyYnPI)) {

								jrYdMsg.setField("MQ_TC_CD"          , "M10YDLMJ1074"); //JMSTC코드
								jrYdMsg.setField("MQ_TC_CREATE_DDTT" , currDt    ); //JMSTC생성일시				
								jrYdMsg.setField("TRN_REQ_DATE"   	 , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
								jrYdMsg.setField("TRN_REQ_SEQ"  	 , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
								jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
								jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
								jrYdMsg.setField("DIST_GOODS_GP"     , "R"                                ); //출하제품구분
								jrYdMsg.setField("SCH_YN"     		 , "N"                                ); //출하제품구분
								jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
								jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각

//							} else {
//
//								jrYdMsg.setField("JMS_TC_CD"         , "YSDSJ006"); //JMSTC코드
//								jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
//								jrYdMsg.setField("CARD_NO"           , commUtils.trim(jrChk.getFieldString("CARD_NO"        ))); //카드번호
//								jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
//								jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
//								jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
//								jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
//								jrYdMsg.setField("TRANS_WORD_DATE"    , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
//								jrYdMsg.setField("TRANS_WORD_SEQNO"   , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
//								jrYdMsg.setField("SPST_FRTOMOVE_GP"  , "1" ); //특수강 이송구분								
//								
//							}
						}	
					}
					
					//전송할 전문에 추가
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER" 	, modifier     ); 
					recPara.setField("ARR_WLOC_CD"  , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"  ))); //착지개소코드
					recPara.setField("YD_WBOOK_ID" 	, ydWbookId     ); 
					recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
					recPara.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID
					//상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd 
					--크레인권상실적 상차 차량스케줄 수정 
					UPDATE TB_YS_CARSCH
					   SET MODIFIER             = :V_MODIFIER
						  ,MOD_DDTT             = SYSDATE
					      ,YD_EQP_WRK_STAT      = 'U' --공차
					      ,YD_CAR_PROG_STAT     = '4' --상차개시
					      ,ARR_WLOC_CD          = :V_ARR_WLOC_CD
					      ,YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
					      ,YD_CARLD_ST_DT       = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
					 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
					   AND DEL_YN               = 'N'
					*/
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
				}
			}

			/**********************************************************
			* 7. 설비, 크레인스케쥴, 적치단, 적치Bed 수정
			* 7.1 설비 야드설비상태(권상중) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 등록
			*   - 권상위치 재료정보 삭제
			* 7.3 적치Bed 야드적치Bed입출고상태(완산Bed->입출고가능) 수정
			* 7.4 크레인스케쥴 권상실적 수정
			**********************************************************/
			//야드권상작업수행구분
			String ydUpWrkActGp = ydEqpWrkMode;

			if ("0".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "B"; //Backup
			}

			//설비
			jrParam.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_STAT"     , "2"         ); //야드설비상태(권상중)
			//크레인스케쥴
			jrParam.setField("YD_UP_CMPL_DT"   , currDt      ); //야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    , ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP", ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  , ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  , ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  , ydCrnZaxis  ); //야드권상실적Z축

			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			//적치단(크레인 및 권상위치) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyr", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			//적치Bed(완산Bed->입출고가능) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkBedF", logId, methodNm, "적치Bed(완산Bed->입출고가능) 수정");
			//크레인스케쥴 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CrnSch", logId, methodNm, "크레인스케쥴 수정");

			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치


			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN3L004(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN3L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강야드 크레인권상실적(N4YSL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN4YSL005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강야드 크레인권상실적[GdsYsL2RcvSeEJB.rcvN4YSL005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LAYER"  )); //야드권상실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydDnWoLoc     = ""; //야드권하지시위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			String ydL2Id = msgId.substring(0, 2);
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권상실적 수신처리"); //야드L3MESSAGE(Error)

			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "봉강야드 크레인권상실적", "APPPI0", "K", "*");			
			
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
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호

			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch", logId, methodNm, "크레인스케줄상태 조회");

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydDnWoLoc       = commUtils.trim(jrChk.getFieldString("YS_DN_WO_LOC"    )); //야드권하지시위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
			jrParam.setField("YD_L2_ID", ydL2Id); //L2 ID (N4)
			
			String currDt = commUtils.getDateTime14(); //현재시각
			
			/**********************************************************
			* 3. 전송 전문 조회
			* 3.1 입고 Carry-out 완료  (YSM6L101)
			**********************************************************/
			if("KAPC01LM".equals(ydSchCd) ||"KAPC02LM".equals(ydSchCd) || "KBPC03LM".equals(ydSchCd) || "KBPC04LM".equals(ydSchCd)) { //입고(Carry-out)
				//입고 Carry-out 완료 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM6L101", jrParam));
				
			} 
			
			/**********************************************************
			* 4. 권상실적위치가 대차(하차)
			* 4.1 대차 하차 정보 등록
			*   - 대차이송재료 삭제
			*   - 대차작업실적 전송 : 
			*   - 대차스케줄 삭제 : 하차완료 시
			* - 공대차출발지시:  
			**********************************************************/
			if ("TC".equals(ydUpWrLoc.substring(2, 4))) {

				//대차하차스케쥴 조회
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005TcarSchUd", logId, methodNm, "대차하차스케쥴 조회");
				
				if (jsChk.size() > 0) {
					
					jrChk = jsChk.getRecord(0);
					String ydTcarSchId  = commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID" )); //야드대차스케쥴ID
					String tcarUdCmplYn = commUtils.trim(jrChk.getFieldString("TCAR_UD_CMPL_YN")); //대차하차완료여부
					
					if ("Y".equals(tcarUdCmplYn)) {
						//하차완료이면 대차스케줄 삭제 후 공대차출발지시 처리
					
						if (resYn) {
							//실패시 응답메세지 설정
							resMsg.setField("YD_L3_HD_RS_CD", "UP21"                    ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , "오류:대차 하차완료처리 실패"); //야드L3MESSAGE
						}
						
						//하차완료(공대차출발지시) 처리
						JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name

						jrYdMsg.setField("YD_EQP_ID"     	, ydUpWrLoc.substring(0, 1) + "X" + ydUpWrLoc.substring(2, 6)); //야드설비ID(대차)
						jrYdMsg.setField("YD_TCAR_SCH_ID"	, ydTcarSchId); //야드대차스케쥴ID
						jrYdMsg.setField("MODIFIER"    		, modifier   ); //수정자
						jrYdMsg.setField("YD_L2_ID"			, "N4"); //L2 ID

						jrRtn = commUtils.addSndData(jrRtn, ysComm.trtTcarSchUdCmpl(jrYdMsg));
						
					} else {
						//하차완료가 아니면 크레인 권상재료 만큼 대차이송재료 삭제 후 대차작업실적 전송
						jrParam.setField("YD_TCAR_SCH_ID"   , ydTcarSchId              ); //야드대차스케쥴ID
						jrParam.setField("YD_CARUD_STOP_LOC", ydUpWrLoc.substring(0, 6)); //야드하차정지위치
						jrParam.setField("YD_CARUD_WRK_CRN" , ydEqpId                  ); //야드하차작업크레인

						//대차스케줄(하차) 수정
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005TcarSchUd", logId, methodNm, "대차스케줄(하차) 수정");

						//대차이송재료 삭제
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005TcarMtlDel", logId, methodNm, "대차이송재료 삭제");
						
						
						//대차작업실적 송신
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L006", jrParam));
					}
				}
			}
			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 5.1 구내운송 소재차량하차개시(YSTSJ009) 전송
			* 5.2 차량이송재료 삭제
			* 5.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			if("TR".equals(ydUpWrLoc.substring(2, 4))) {
				//차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)
				recPara.setField("YD_WBOOK_ID" , ydWbookId ); 
				recPara.setField("MODIFIER" , modifier     ); 

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId 
				--크레인권상실적 하차 차량스케줄 작업예약ID 수정 
				MERGE INTO TB_YS_CARSCH TS USING (
				SELECT YD_CAR_SCH_ID
				      ,YD_WBOOK_ID
				  FROM (SELECT TS.YD_CAR_SCH_ID
				              ,WM.YD_WBOOK_ID
				          FROM TB_YS_CARSCH     TS
				              ,TB_YS_CARFTMVMTL TM
				              ,TB_YS_WRKBOOKMTL WM
				         WHERE WM.SSTL_NO        = TM.SSTL_NO
				           AND TM.YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				           AND WM.YD_WBOOK_ID   = :V_YD_WBOOK_ID
				           AND WM.DEL_YN        = 'N'
				           AND TM.DEL_YN        = 'N'
				           AND TS.DEL_YN        = 'N'
				           AND NOT EXISTS (SELECT YD_CAR_SCH_ID
				                             FROM TB_YS_CARSCH
				                            WHERE YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				                              AND DEL_YN               = 'N')
				         ORDER BY TS.YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1
				) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
				     TS.MODIFIER             = :V_MODIFIER
				    ,TS.MOD_DDTT             = SYSDATE
				    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_WBOOK_ID
				 */   
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId", logId, methodNm, "차량스케줄(하차) 수정");
				
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				recPara.setField("WR_DT" 		 , commUtils.getDateTime14()  ); 
				recPara.setField("YS_STK_COL_GP" , ydUpWrLoc.substring(0, 6)     ); 
				//구내운송 소재차량하차개시
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ009", recPara));
				
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				recPara.setField("MODIFIER" , modifier     ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel 
				--크레인권상실적 하차 차량이송재료 삭제 
				MERGE INTO TB_YS_CARFTMVMTL TM USING (
				SELECT TS.YD_CAR_SCH_ID
				      ,CM.SSTL_NO
				  FROM TB_YS_CRNSCH    CS
				      ,TB_YS_CRNWRKMTL CM
				      ,TB_YS_CARSCH    TS
				 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = TS.YD_CARUD_WRK_BOOK_ID
				   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CM.DEL_YN        = 'N'
				   AND TS.DEL_YN        = 'N'
				) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO)
				WHEN MATCHED THEN UPDATE SET
					 TM.MODIFIER = :V_MODIFIER
				    ,TM.MOD_DDTT = SYSDATE
				    ,TM.DEL_YN   = 'Y'
				*/    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel", logId, methodNm, "차량이송재료 삭제");
				
				//하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", ydCrnSchId ); 
				recPara.setField("YD_WBOOK_ID" 	, ydWbookId); 
				recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
				recPara.setField("MODIFIER" 	, modifier     ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUd
				--크레인권상실적 하차 차량스케줄 수정 
				MERGE INTO TB_YS_CARSCH TS USING (
				SELECT TS.YD_CAR_SCH_ID
				      ,COUNT(ST.SSTL_NO)        AS YD_EQP_WRK_SH
				      ,NVL(SUM(ST.YD_MTL_WT),0) AS YD_EQP_WRK_WT
				      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN 'D'                                   --하차개시
				       ELSE MIN(TS.YD_CAR_PROG_STAT) END AS YD_CAR_PROG_STAT
				      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
				       ELSE MIN(TS.YD_CARUD_ST_DT) END AS YD_CARUD_ST_DT
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				      ,TB_YS_STOCK      ST
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TM.SSTL_NO              = ST.SSTL_NO(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID
				) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER         = :V_MODIFIER
				    ,TS.MOD_DDTT         = SYSDATE
				    ,TS.YD_EQP_WRK_SH    = DD.YD_EQP_WRK_SH
				    ,TS.YD_EQP_WRK_WT    = DD.YD_EQP_WRK_WT
				    ,TS.YD_CAR_PROG_STAT = DD.YD_CAR_PROG_STAT
				    ,TS.YD_CARUD_ST_DT   = DD.YD_CARUD_ST_DT
				*/    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUd", logId, methodNm, "하차 차량스케줄 수정 ");
				
			}
			
			/**********************************************************
			* 6. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 6.1 구내운송 소재차량상차개시(YSTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 6.2 출하관리출하상차개시(YSDSJ006) 전송
			*   - 야드차량사용구분이 출하차량(G)
			* 6.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("TR".equals(ydDnWoLoc.substring(2, 4))) {
				//차량상차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" , ydWbookId     ); 
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd 
				--크레인권상실적 상차 차량스케줄 조회 
				SELECT TS.YD_CAR_SCH_ID               --야드차량스케쥴ID
				      ,TS.YD_CAR_USE_GP               --야드차량사용구분
				      ,TS.TRN_EQP_CD                  --운송장비코드
				      ,TS.SPOS_WLOC_CD                --발지개소코드
				      ,SC.YD_PNT_CD AS SPOS_YD_PNT_CD --발지야드포인트코드
				--      ,DECODE(SC.YD_CAR_USE_GP,'L',
				--       NVL(MV.ARR_WLOC_CD,SF_SLAB_YD_ARR_WLOC_CD(MV.YD_AIM_YD_GP))) AS ARR_WLOC_CD --착지개소코드
				      ,CASE WHEN SC.YD_CAR_USE_GP = 'L' AND SUBSTR(TS.YD_CARLD_STOP_LOC,1,1) = 'B' THEN
				                  (SELECT DECODE(YD_AIM_BAY_GP,'A','S3Y10','B','S3Y11','S3Y30') 
				                     FROM TB_YS_PREPSCH AA
				                    WHERE YD_WBOOK_ID = CS.YD_WBOOK_ID
				                  )  
				            ELSE MV.ARR_WLOC_CD  END AS ARR_WLOC_CD        
				      ,TS.CARD_NO                     --카드번호
				      ,TS.CAR_NO                      --차량번호
				      ,TS.TRANS_ORD_DATE              --운송작업지시일자
				      ,TS.TRANS_ORD_SEQNO             --운송작업지시순번
				      ,TS.CMBN_CARLD_YN               --복수동
				  FROM TB_YS_CRNSCH  CS
				      ,TB_YS_STKCOL  SC
				      ,TB_YS_CARSCH  TS
				      ,(SELECT WM.YD_WBOOK_ID
				              ,WM.YD_AIM_YD_GP
				              ,MV.ARR_WLOC_CD
				          FROM TB_PB_STLFRTOMOVE MV
				              ,(SELECT WB.YD_WBOOK_ID
				                      ,WB.YD_AIM_YD_GP
				                      ,WM.SSTL_NO
				                  FROM TB_YS_WRKBOOK    WB
				                      ,TB_YS_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1) WM
				         WHERE MV.SSTL_NO = WM.SSTL_NO
				           AND MV.TRANSWORD_SEQNO = (SELECT MAX(MM.TRANSWORD_SEQNO)
				                                       FROM TB_PB_STLFRTOMOVE MM
				                                      WHERE MM.SSTL_NO = MV.SSTL_NO)) MV
				 WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = MV.YD_WBOOK_ID(+)
				   AND SC.YS_STK_COL_GP = SUBSTR(CS.YS_DN_WO_LOC,1,6)
				   AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				     OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				   AND TS.YD_CAR_PROG_STAT IN ('2','3') --상차도착,검수
				   AND TS.DEL_YN = 'N'
				*/
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd", logId, methodNm, "차량하차스케줄 정보 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
				
					//상차개시 전문
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					
					if("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
						//구내운송 소재차량상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YSTSJ007"); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        , commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"    ))); //운송장비코드
						jrYdMsg.setField("SPOS_WLOC_CD"      , commUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"  ))); //발지개소코드
						jrYdMsg.setField("SPOS_YD_PNT_CD"    , commUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))); //발지야드포인트코드
						jrYdMsg.setField("ARR_WLOC_CD"       , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   ))); //착지개소코드
						jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    ); //운송작업시작일시
					} else {
						if(!commUtils.trim(jrChk.getFieldString("CMBN_CARLD_YN")).equals("E")) {    //복수동 2차 상차도 도착인 경우 제외
							
							//PIDEV		
							//출하관리 상차개시
//							if("Y".equals(sApplyYnPI)) {
								
								jrYdMsg.setField("MQ_TC_CD"          , "M10YDLMJ1074"); //JMSTC코드
								jrYdMsg.setField("MQ_TC_CREATE_DDTT" , currDt    ); //JMSTC생성일시		
								jrYdMsg.setField("TRN_REQ_DATE"   	 , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
								jrYdMsg.setField("TRN_REQ_SEQ"  	 , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
								jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
								jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
								jrYdMsg.setField("DIST_GOODS_GP"     , "R"                                ); //출하제품구분
								jrYdMsg.setField("SCH_YN"     		 , "N"                                ); //출하제품구분								
								jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
								jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
								
//							} else {
//							
//								jrYdMsg.setField("JMS_TC_CD"         , "YSDSJ006"); //JMSTC코드
//								jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
//								jrYdMsg.setField("CARD_NO"           , commUtils.trim(jrChk.getFieldString("CARD_NO"        ))); //카드번호
//								jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
//								jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
//								jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
//								jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
//								jrYdMsg.setField("TRANS_WORD_DATE"   , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
//								jrYdMsg.setField("TRANS_WORD_SEQNO"  , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
//								jrYdMsg.setField("SPST_FRTOMOVE_GP"  , "1" ); //특수강 이송구분
//							}
						}	
						/*
						 * ====================================================================================
						 * 다음 도착차량 안내메세지 송신기능 추가 시작.
						 * ====================================================================================
						 */
						{
							JDTORecordSet rsCarSch  = JDTORecordFactory.getInstance().createRecordSet("");
							JDTORecord    recCarSch = JDTORecordFactory.getInstance().create();
							JDTORecord    carPara 	= JDTORecordFactory.getInstance().create();
							carPara.setField("YD_CAR_STOP_LOC", ydDnWoLoc.substring(0, 6));
			 				
							rsCarSch = commDao.select(carPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchByBayInSeq", logId, methodNm, "차량포인트 조회"); 
							
							if(rsCarSch == null || rsCarSch.size() <= 0){
							}else{
							
								String szNEW_YD_CAR_USE_GP 		= "";
								String szNEW_CAR_NO 			= "";
								String szNEW_CARD_NO 			= "";
								String szNEW_TRANS_ORD_DATE 	= "";
								String szNEW_TRANS_ORD_SEQNO 	= "";
								String szNEW_YD_CARPNT_CD2		= "";
								String szTEL_NO					= "";
								
								// 입동 가능 차량 조회 (가장 빠른 순서, 각강/반입 검수 완료되지 않는 차량 제외)
								for (int ii = 0; ii < rsCarSch.size(); ii++) {
									recCarSch = rsCarSch.getRecord(ii);					
									
									// 반입/각강 검수 완료 체크
									JDTORecordSet resSet      = null;;
									JDTORecord    recIn       = JDTORecordFactory.getInstance().create();
									
									resSet 	= JDTORecordFactory.getInstance().createRecordSet("");
									recIn 	= JDTORecordFactory.getInstance().create();
									recIn.setField("TRN_EQP_CD", StringHelper.evl(recCarSch.getFieldString("CAR_NO"), ""));
									resSet = commDao.select(recIn, "com.inisteel.cim.ys.common.dao.YsCommDAO.getRtnStkOrSbStkCmpl"
											, logId, methodNm, "반입/각강 검수 완료 체크"); 
									if (resSet != null && resSet.size() > 0 ) {
										continue;
									}
									
									// 가장 빠른 순서 차량 선택
									szNEW_YD_CAR_USE_GP 	= StringHelper.evl(recCarSch.getFieldString("YD_CAR_USE_GP")	, "");
									szNEW_CAR_NO 			= StringHelper.evl(recCarSch.getFieldString("CAR_NO")			, "");
									szNEW_CARD_NO 			= StringHelper.evl(recCarSch.getFieldString("CARD_NO")			, "");
									szNEW_TRANS_ORD_DATE 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_DATE")	, "");
									szNEW_TRANS_ORD_SEQNO 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_SEQNO")	, "");
									szNEW_YD_CARPNT_CD2		= StringHelper.evl(recCarSch.getFieldString("YD_CARPNT_CD")		, "");
									szTEL_NO				= StringHelper.evl(recCarSch.getFieldString("DEST_TEL_NO")		, "");
									break;
								}
								
								//-----------------------------------------------------------------------------------------------------------------------
								//입동순서가 가장빠른 차량이 구내운송인 경우에는 입동지시 전문을 전송하지 않는다.
								//입동순서가 가장빠른 출하차량인 경우에만 입동지시 전문을 전송한다.
								//-----------------------------------------------------------------------------------------------------------------------
								if( szNEW_YD_CAR_USE_GP.equals("G")) {
									
									String szMsg= "크레인권상시점 입동순서가 가장빠른 차량이 출하차량[차량번호:" 		+ szNEW_CAR_NO + 
									                         				", 카드번호:" 	  	+ szNEW_CARD_NO + 
									                         				", 운송지시일자:" 	+ szNEW_TRANS_ORD_DATE + 
									                         				", 운송지시순번:" 	+ szNEW_TRANS_ORD_SEQNO + "]이므로 입동대기 전문을 전송예정.";
									commUtils.printLog(logId, szMsg, "SL");	
									
									String sSmsMsg = szNEW_YD_CARPNT_CD2.substring(2,3)+"동 "+szNEW_YD_CARPNT_CD2.substring(1,2)+"통로 입동대기 하세요.";
									
									szMsg= " 입동대기 SMS 문자내용:"+sSmsMsg;
									commUtils.printLog(logId, szMsg, "SL");								 
						
									MessageSenderTalk    sender1 = new MessageSenderTalk();
									
					 		    	JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					 		    	recPara1.setField("PHONE_NUM"	, new String(szTEL_NO));
					 		    	recPara1.setField("TMPL_CD"		, new String("CM1"));
					 		    	recPara1.setField("SND_MSG"		, new String("[현대제철 공지사항]\n" + sSmsMsg));
					 		    	recPara1.setField("SUBJECT"		, new String("입동대기 알림"));
					 		    	recPara1.setField("SMS_SND_NUM"	, new String("0416806678"));
					 		    	recPara1.setField("RECV_ID"		,"1522110");
					 		    	recPara1.setField("GROUP_ID"	,"KaKao");
					 		    	recPara1.setField("PROGRAM_ID"	,"udttalk");
									sender1.sendTalk(recPara1);
									/*
									MessageSenderTalk    sender2 = new MessageSenderTalk();
									
									JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
									recPara2.setField("PHONE_NUM"	, new String("01038433916"));
									recPara2.setField("TMPL_CD"		, new String("CM1"));
					 		    	recPara2.setField("SND_MSG"		, new String("[현대제철 공지사항]\n" + sSmsMsg+"["+szNEW_CAR_NO+"]" ));
					 		    	recPara2.setField("SUBJECT"		, new String("입동대기 알림"));
					 		    	recPara2.setField("SMS_SND_NUM"	, new String("0416806678"));
					 		    	recPara2.setField("RECV_ID"		,"1522110");
					 		    	recPara2.setField("GROUP_ID"	,"KaKao");
					 		    	recPara2.setField("PROGRAM_ID"	,"udttalk");
									sender2.sendTalk(recPara2);
									*/
									szMsg= "크레인권상시점 입동지시수신전화[" + szTEL_NO + "]에 대한 입동대기 SMS 전송";
									commUtils.printLog(logId, szMsg, "SL");	
								}
							}
						}
						/*
						 * ====================================================================================
						 * 다음 도착차량 안내메세지 송신기능 추가 종료.
						 * ====================================================================================
						 */
					}
					
					//전송할 전문에 추가
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER" 	, modifier     ); 
					recPara.setField("ARR_WLOC_CD"  , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"  ))); //착지개소코드
					recPara.setField("YD_WBOOK_ID" 	, ydWbookId     ); 
					recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
					recPara.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID
					//상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd 
					--크레인권상실적 상차 차량스케줄 수정 
					UPDATE TB_YS_CARSCH
					   SET MODIFIER             = :V_MODIFIER
						  ,MOD_DDTT             = SYSDATE
					      ,YD_EQP_WRK_STAT      = 'U' --공차
					      ,YD_CAR_PROG_STAT     = '4' --상차개시
					      ,ARR_WLOC_CD          = :V_ARR_WLOC_CD
					      ,YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
					      ,YD_CARLD_ST_DT       = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
					 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
					   AND DEL_YN               = 'N'
					*/
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
				}
			}

			/**********************************************************
			* 7. 설비, 크레인스케쥴, 적치단, 적치Bed 수정
			* 7.1 설비 야드설비상태(권상중) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 등록
			*   - 권상위치 재료정보 삭제
			* 7.3 적치Bed 야드적치Bed입출고상태(완산Bed->입출고가능) 수정
			* 7.4 크레인스케쥴 권상실적 수정
			**********************************************************/
			//야드권상작업수행구분
			String ydUpWrkActGp = ydEqpWrkMode;

			if ("0".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "B"; //Backup
			}

			//설비
			jrParam.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_STAT"     , "2"         ); //야드설비상태(권상중)
			//크레인스케쥴
			jrParam.setField("YD_UP_CMPL_DT"   , currDt      ); //야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    , ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP", ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  , ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  , ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  , ydCrnZaxis  ); //야드권상실적Z축

			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			//적치단(크레인 및 권상위치) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyr", logId, methodNm, "봉강 적치단(크레인 및 권상위치) 수정");
			//적치Bed(완산Bed->입출고가능) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkBedF", logId, methodNm, "적치Bed(완산Bed->입출고가능) 수정");
			//크레인스케쥴 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CrnSch", logId, methodNm, "크레인스케쥴 수정");

			// 제품창고(K) B동 일반야드에서 권상시 Bed 활성상태 최기화 (Query에서 해당 bed에 적치된 제품이 하나도 없을 때 update 한다.) 
			if (ydUpWrLoc.matches("[K][B]\\d\\d\\d\\d\\d\\d")) { 	
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("YS_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
				jrParam.setField("YS_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStatReSet", logId, methodNm, "적치Bed 활성상태 초기화");
			}

			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치


			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				resMsg.setField("MODIFIER"      , modifier  ); //수정자
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN4L004(resMsg));
			}

			if(ydEqpId.equals("KACRA2")){
				//SC반납 인 경우 작업예약에 있는 정보 기동처리 함
	    		JDTORecord jrYdMsg1 = JDTORecordFactory.getInstance().create();
				jrYdMsg1.setResultCode(logId);	//Log ID
				jrYdMsg1.setResultMsg(methodNm);	//Log Method Name
	
				jrParam.setField("GP", "B"); //반납
				//작업예약 조회
	    		JDTORecordSet jsWrkBook1 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbIdN6YSHGBM", logId, methodNm, "작업예약 조회");
	
				//작업예약이 있으면 크레인스케줄   호출
				if (jsWrkBook1.size() > 0) {
					ydL3Msg = "크레인스케줄 호출";
	
					jrYdMsg1.setField("YD_WBOOK_ID"  , jsWrkBook1.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
					jrYdMsg1.setField("YD_SCH_CD"    , jsWrkBook1.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
					jrYdMsg1.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
					jrYdMsg1.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
					jrYdMsg1.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
					jrYdMsg1.setField("MODIFIER"   , modifier); //수정자
					
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getCrnSchMsg(jrYdMsg1));
				} 			
		    }
			
			commUtils.printLog(logId, methodNm, "S-");
		    
			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN4L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재자동화 크레인권상실적(N5YSL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN5YSL005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동화 크레인권상실적[GdsYsL2RcvSeEJB.rcvN5YSL005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LAYER"  )); //야드권상실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydDnWoLoc     = ""; //야드권하지시위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE
			String CarYdWbookId = "";
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권상실적 수신처리"); //야드L3MESSAGE(Error)

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

			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");			
			
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호
			
			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch", logId, methodNm, "크레인스케줄상태 조회");

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID")); //야드작업예약ID
				ydDnWoLoc       = commUtils.trim(jrChk.getFieldString("YS_DN_WO_LOC")); //야드권하지시위치
				CarYdWbookId 	= commUtils.trim(jrChk.getFieldString("CAR_YD_WBOOK_ID")); //차량상차작업예약ID 

				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID")); //야드설비ID
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			String currDt = commUtils.getDateTime14(); //현재시각
			

			/**********************************************************
			* 4. 권상실적위치가 대차(하차)
			* 4.1 대차 하차 정보 등록
			*   - 대차이송재료 삭제
			*   - 대차스케줄 삭제 : 하차완료 시
			* - 공대차출발지시  
			**********************************************************/
			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 5.1 구내운송 소재차량하차개시(YSTSJ009) 전송
			* 5.2 차량이송재료 삭제
			* 5.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			if("TR".equals(ydUpWrLoc.substring(2, 4))) {
				//차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)
				recPara.setField("YD_WBOOK_ID" , ydWbookId ); 
				recPara.setField("MODIFIER" , modifier     ); 

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId 
				--크레인권상실적 하차 차량스케줄 작업예약ID 수정 
				MERGE INTO TB_YS_CARSCH TS USING (
				SELECT YD_CAR_SCH_ID
				      ,YD_WBOOK_ID
				  FROM (SELECT TS.YD_CAR_SCH_ID
				              ,WM.YD_WBOOK_ID
				          FROM TB_YS_CARSCH     TS
				              ,TB_YS_CARFTMVMTL TM
				              ,TB_YS_WRKBOOKMTL WM
				         WHERE WM.SSTL_NO        = TM.SSTL_NO
				           AND TM.YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				           AND WM.YD_WBOOK_ID   = :V_YD_WBOOK_ID
				           AND WM.DEL_YN        = 'N'
				           AND TM.DEL_YN        = 'N'
				           AND TS.DEL_YN        = 'N'
				           AND NOT EXISTS (SELECT YD_CAR_SCH_ID
				                             FROM TB_YS_CARSCH
				                            WHERE YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				                              AND DEL_YN               = 'N')
				         ORDER BY TS.YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1
				) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
				     TS.MODIFIER             = :V_MODIFIER
				    ,TS.MOD_DDTT             = SYSDATE
				    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_WBOOK_ID
				 */   
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId", logId, methodNm, "차량스케줄(하차) 수정");
				
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				recPara.setField("WR_DT" 		 , commUtils.getDateTime14()  ); 
				recPara.setField("YS_STK_COL_GP" , ydUpWrLoc.substring(0, 6)     ); 
				//구내운송 소재차량하차개시
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ009", recPara));
				
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				recPara.setField("MODIFIER" , modifier     ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel 
				--크레인권상실적 하차 차량이송재료 삭제 
				MERGE INTO TB_YS_CARFTMVMTL TM USING (
				SELECT TS.YD_CAR_SCH_ID
				      ,CM.SSTL_NO
				  FROM TB_YS_CRNSCH    CS
				      ,TB_YS_CRNWRKMTL CM
				      ,TB_YS_CARSCH    TS
				 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = TS.YD_CARUD_WRK_BOOK_ID
				   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CM.DEL_YN        = 'N'
				   AND TS.DEL_YN        = 'N'
				) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO)
				WHEN MATCHED THEN UPDATE SET
					 TM.MODIFIER = :V_MODIFIER
				    ,TM.MOD_DDTT = SYSDATE
				    ,TM.DEL_YN   = 'Y'
				*/    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel", logId, methodNm, "차량이송재료 삭제");
				
				//하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", ydCrnSchId ); 
				recPara.setField("YD_WBOOK_ID" 	, ydWbookId); 
				recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
				recPara.setField("MODIFIER" 	, modifier     ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUd
				--크레인권상실적 하차 차량스케줄 수정 
				MERGE INTO TB_YS_CARSCH TS USING (
				SELECT TS.YD_CAR_SCH_ID
				      ,COUNT(ST.SSTL_NO)        AS YD_EQP_WRK_SH
				      ,NVL(SUM(ST.YD_MTL_WT),0) AS YD_EQP_WRK_WT
				      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN 'D'                                   --하차개시
				       ELSE MIN(TS.YD_CAR_PROG_STAT) END AS YD_CAR_PROG_STAT
				      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
				       ELSE MIN(TS.YD_CARUD_ST_DT) END AS YD_CARUD_ST_DT
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				      ,TB_YS_STOCK      ST
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TM.SSTL_NO              = ST.SSTL_NO(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID
				) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER         = :V_MODIFIER
				    ,TS.MOD_DDTT         = SYSDATE
				    ,TS.YD_EQP_WRK_SH    = DD.YD_EQP_WRK_SH
				    ,TS.YD_EQP_WRK_WT    = DD.YD_EQP_WRK_WT
				    ,TS.YD_CAR_PROG_STAT = DD.YD_CAR_PROG_STAT
				    ,TS.YD_CARUD_ST_DT   = DD.YD_CARUD_ST_DT
				*/    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUd", logId, methodNm, "하차 차량스케줄 수정 ");
				
			}
		
			/**********************************************************
			* 6. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 6.1 구내운송 소재차량상차개시(YSTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 6.2 출하관리출하상차개시(YSDSJ006) 전송
			*   - 야드차량사용구분이 출하차량(G)
			* 6.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("TR".equals(ydDnWoLoc.substring(2, 4))) {
				//차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" , ydWbookId     ); 
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd 
				--크레인권상실적 상차 차량스케줄 조회 
				SELECT TS.YD_CAR_SCH_ID               --야드차량스케쥴ID
				      ,TS.YD_CAR_USE_GP               --야드차량사용구분
				      ,TS.TRN_EQP_CD                  --운송장비코드
				      ,TS.SPOS_WLOC_CD                --발지개소코드
				      ,SC.YD_PNT_CD AS SPOS_YD_PNT_CD --발지야드포인트코드
				      ,DECODE(SC.YD_CAR_USE_GP,'L',
				       NVL(MV.ARR_WLOC_CD,SF_SLAB_YD_ARR_WLOC_CD(MV.YD_AIM_YD_GP))) AS ARR_WLOC_CD --착지개소코드
				      ,TS.CARD_NO                     --카드번호
				      ,TS.CAR_NO                      --차량번호
				      ,TS.TRANS_ORD_DATE              --운송작업지시일자
				      ,TS.TRANS_ORD_SEQNO             --운송작업지시순번
				  FROM TB_YS_CRNSCH  CS
				      ,TB_YS_STKCOL  SC
				      ,TB_YS_CARSCH  TS
				      ,(SELECT WM.YD_WBOOK_ID
				              ,WM.YD_AIM_YD_GP
				              ,MV.ARR_WLOC_CD
				          FROM TB_PB_STLFRTOMOVE MV
				              ,(SELECT WB.YD_WBOOK_ID
				                      ,WB.YD_AIM_YD_GP
				                      ,WM.SSTL_NO
				                  FROM TB_YS_WRKBOOK    WB
				                      ,TB_YS_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1) WM
				         WHERE MV.SSTL_NO = WM.SSTL_NO
				           AND MV.TRANSWORD_SEQNO = (SELECT MAX(MM.TRANSWORD_SEQNO)
				                                       FROM TB_PB_STLFRTOMOVE MM
				                                      WHERE MM.SSTL_NO = MV.SSTL_NO)) MV
				 WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = MV.YD_WBOOK_ID(+)
				   AND SC.YS_STK_COL_GP = SUBSTR(CS.YS_DN_WO_LOC,1,6)
				   AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				     OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				   AND TS.YD_CAR_PROG_STAT IN ('2','3') --상차도착,검수
				   AND TS.DEL_YN = 'N'

					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd", logId, methodNm, "차량하차스케줄 정보 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
				
					//상차개시 전문
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					
					if("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
						//구내운송 소재차량상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YSTSJ007"); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        , commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"    ))); //운송장비코드
						jrYdMsg.setField("SPOS_WLOC_CD"      , commUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"  ))); //발지개소코드
						jrYdMsg.setField("SPOS_YD_PNT_CD"    , commUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))); //발지야드포인트코드
						jrYdMsg.setField("ARR_WLOC_CD"       , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   ))); //착지개소코드
						jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    ); //운송작업시작일시
					} else {
						if(!commUtils.trim(jrChk.getFieldString("CMBN_CARLD_YN")).equals("E")) {  //복수동 2차 상차도 도착인 경우 제외
							
							//PIDEV		
							//출하관리 상차개시
//							if("Y".equals(sApplyYnPI)) {								
								
								jrYdMsg.setField("MQ_TC_CD"          , "M10YDLMJ1074"); //JMSTC코드
								jrYdMsg.setField("MQ_TC_CREATE_DDTT" , currDt    ); //JMSTC생성일시												
								jrYdMsg.setField("TRN_REQ_DATE"   	 , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
								jrYdMsg.setField("TRN_REQ_SEQ"  	 , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
								jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
								jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
								jrYdMsg.setField("DIST_GOODS_GP"     , "R"                                ); //출하제품구분
								jrYdMsg.setField("SCH_YN"     		 , "N"                                ); //출하제품구분
								jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
								jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
								
//							} else {
//								
//								jrYdMsg.setField("JMS_TC_CD"         , "YSDSJ006"); //JMSTC코드
//								jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
//								jrYdMsg.setField("CARD_NO"           , commUtils.trim(jrChk.getFieldString("CARD_NO"        ))); //카드번호
//								jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
//								jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
//								jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
//								jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
//								jrYdMsg.setField("TRANS_WORD_DATE"    , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
//								jrYdMsg.setField("TRANS_WORD_SEQNO"   , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
//								jrYdMsg.setField("SPST_FRTOMOVE_GP"  , "1" ); //특수강 이송구분		
//								
//							}
						}	
					}
					
					//전송할 전문에 추가
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER" 	, modifier     ); 
					recPara.setField("ARR_WLOC_CD"  , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"  ))); //착지개소코드
					recPara.setField("YD_WBOOK_ID" 	, ydWbookId     ); 
					recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
					recPara.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID
					//상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd 
					--크레인권상실적 상차 차량스케줄 수정 
					UPDATE TB_YS_CARSCH
					   SET MODIFIER             = :V_MODIFIER
						  ,MOD_DDTT             = SYSDATE
					      ,YD_EQP_WRK_STAT      = 'U' --공차
					      ,YD_CAR_PROG_STAT     = '4' --상차개시
					      ,ARR_WLOC_CD          = :V_ARR_WLOC_CD
					      ,YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
					      ,YD_CARLD_ST_DT       = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
					 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
					   AND DEL_YN               = 'N'
					*/
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
				}
				
				
				/**
				* 상차완료 검수 처리 작업 -> 출하상차 작업예약 생성시로 이동(2020/06/11 박비오)
				*/
				// recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				// commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005EXAMINATIONCHKLIST", logId, methodNm, "상차검수등록 ");
			}

			/**********************************************************
			* 7. 설비, 크레인스케쥴, 적치단, 적치Bed 수정
			* 7.1 설비 야드설비상태(권상중) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 등록
			*   - 권상위치 재료정보 삭제
			* 7.3 적치Bed 야드적치Bed입출고상태(완산Bed->입출고가능) 수정
			* 7.4 크레인스케쥴 권상실적 수정
			**********************************************************/
			//야드권상작업수행구분
			String ydUpWrkActGp = ydEqpWrkMode;

			if ("0".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "B"; //Backup
			}

			//설비
			jrParam.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_STAT"     , "2"         ); //야드설비상태(권상중)
			//크레인스케쥴
			jrParam.setField("YD_UP_CMPL_DT"   , currDt      ); //야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    , ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP", ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  , ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  , ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  , ydCrnZaxis  ); //야드권상실적Z축

			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			//적치단(크레인 및 권상위치) 수정
			
			if("CS".equals(ydUpWrLoc.substring(2, 4))) {
				// 선재 차량권하시 별도
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyrCS", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			} else {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyrSC", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			}
		
			//적치Bed(완산Bed->입출고가능) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkBedF", logId, methodNm, "적치Bed(완산Bed->입출고가능) 수정");
			//크레인스케쥴 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CrnSch", logId, methodNm, "크레인스케쥴 수정");
			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치

			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN5L004(resMsg));
			}

			//SJH
			/**********************************************************
			* * 권상실적위치가 출고SKID
			* 1.입동되어 있는 차량중에 작업예약이 안 걸려 있는 재료 SELECT
			* 2.해당 권상 위치를 기준으로 작업 예약 생성하여 SCH 기동 처리함
			* 
			**********************************************************/
			JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
			if("CS".equals(ydUpWrLoc.substring(2, 4))) {
				
				// 레코드셋 선언
				JDTORecordSet 	rsResult 	= null;
				JDTORecord   	recResult   = null;
				JDTORecord	  	recInTemp	= null;	
				 
				if("12".equals(ydUpWrLoc.substring(4, 6))
					||	"23".equals(ydUpWrLoc.substring(4, 6))
				   ||	"34".equals(ydUpWrLoc.substring(4, 6))
				   ||	"56".equals(ydUpWrLoc.substring(4, 6))
				   ||	"67".equals(ydUpWrLoc.substring(4, 6))
				   ||	"78".equals(ydUpWrLoc.substring(4, 6))
				   ){
					//TWO 거리 작업 
					
					String ydUpWrLocF ="";
					String ydUpWrLocT ="";
					
					ydUpWrLocF =ydUpWrLoc.substring(0,4)+"0"+ydUpWrLoc.substring(4,5);
					ydUpWrLocT =ydUpWrLoc.substring(0,4)+"0"+ydUpWrLoc.substring(5,6);
					
					
					recPara1.setField("CAR_YD_WBOOK_ID"  , CarYdWbookId  );
					//앞에 대상 작업
					rsResult = commDao.select(recPara1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkTransOrdWlocCd", logId, methodNm, "작업대상 조회"); 
									
					if( rsResult.size() > 0 ) {
						for(int i = 1; i <= rsResult.size(); i++) {
							rsResult.absolute(i);
							recResult = rsResult.getRecord();
							
							jrParam.setField("YD_TO_LOC_GUIDE",	ydUpWrLocF.substring(0, 6));
							jrParam.setField("YD_WBOOK_ID",    	commUtils.trim(recResult.getFieldString("YD_WBOOK_ID")));
							
							//작업 예약 에 가이드 UPDATE 처리
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.updWBookToLocGuide 
							UPDATE TB_YS_WRKBOOK
							   SET YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
							 WHERE YD_WBOOK_ID     = :V_YD_WBOOK_ID
							   AND DEL_YN = 'N'
							*/	   
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWBookToLocGuide", logId, methodNm, "설비상태 수정");
							
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setResultCode(logId);	//Log ID
							recInTemp.setResultMsg(methodNm);	//Log Method Name
							recInTemp.setField("JMS_TC_CD",    	"YSYSJ402");
				    		recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
					    	recInTemp.setField("YD_SCH_CD", 	commUtils.trim(recResult.getFieldString("YD_SCH_CD")));
					    	recInTemp.setField("YD_EQP_ID", 	commUtils.trim(recResult.getFieldString("YD_WRK_CRN")));
					    	recInTemp.setField("YD_WBOOK_ID", 	commUtils.trim(recResult.getFieldString("YD_WBOOK_ID")));
					    	
					    	jrRtn = commUtils.addSndData(jrRtn, recInTemp);
						}			
		
					}
					
					//뒤에 대상 작업
					rsResult = commDao.select(recPara1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkTransOrdWlocCd", logId, methodNm, "작업대상 조회"); 
					
					if( rsResult.size() > 0 ) {
						for(int i = 1; i <= rsResult.size(); i++) {
							rsResult.absolute(i);
							recResult = rsResult.getRecord();
							
							jrParam.setField("YD_TO_LOC_GUIDE",	ydUpWrLocT.substring(0, 6));
							jrParam.setField("YD_WBOOK_ID",    	commUtils.trim(recResult.getFieldString("YD_WBOOK_ID")));
							
							//작업 예약 에 가이드 UPDATE 처리
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.updWBookToLocGuide 
							UPDATE TB_YS_WRKBOOK
							   SET YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
							 WHERE YD_WBOOK_ID     = :V_YD_WBOOK_ID
							   AND DEL_YN = 'N'
							*/	   
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWBookToLocGuide", logId, methodNm, "설비상태 수정");
							
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setResultCode(logId);	//Log ID
							recInTemp.setResultMsg(methodNm);	//Log Method Name
							recInTemp.setField("JMS_TC_CD",    	"YSYSJ402");
				    		recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
					    	recInTemp.setField("YD_SCH_CD", 	commUtils.trim(recResult.getFieldString("YD_SCH_CD")));
					    	recInTemp.setField("YD_EQP_ID", 	commUtils.trim(recResult.getFieldString("YD_WRK_CRN")));
					    	recInTemp.setField("YD_WBOOK_ID", 	commUtils.trim(recResult.getFieldString("YD_WBOOK_ID")));
					    	
					    	jrRtn = commUtils.addSndData(jrRtn, recInTemp);
						}			
		
					}
					
				}else{
				
					recPara1.setField("CAR_YD_WBOOK_ID"  , CarYdWbookId  );
					rsResult = commDao.select(recPara1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkTransOrdWlocCd", logId, methodNm, "작업대상 조회"); 
									
					if( rsResult.size() > 0 ) {
						for(int i = 1; i <= rsResult.size(); i++) {
							rsResult.absolute(i);
							recResult = rsResult.getRecord();
							
							jrParam.setField("YD_TO_LOC_GUIDE",	ydUpWrLoc.substring(0, 6));
							jrParam.setField("YD_WBOOK_ID",    	commUtils.trim(recResult.getFieldString("YD_WBOOK_ID")));
							
							//작업 예약 에 가이드 UPDATE 처리
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.updWBookToLocGuide 
							UPDATE TB_YS_WRKBOOK
							   SET YD_TO_LOC_GUIDE = :V_YD_TO_LOC_GUIDE
							 WHERE YD_WBOOK_ID     = :V_YD_WBOOK_ID
							   AND DEL_YN = 'N'
							*/	   
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updWBookToLocGuide", logId, methodNm, "설비상태 수정");
							
							recInTemp = JDTORecordFactory.getInstance().create();
							recInTemp.setResultCode(logId);	//Log ID
							recInTemp.setResultMsg(methodNm);	//Log Method Name
							recInTemp.setField("JMS_TC_CD",    	"YSYSJ402");
				    		recInTemp.setField("JMS_TC_CREATE_DDTT"	,  commUtils.getDateTime14());
					    	recInTemp.setField("YD_SCH_CD", 	commUtils.trim(recResult.getFieldString("YD_SCH_CD")));
					    	recInTemp.setField("YD_EQP_ID", 	commUtils.trim(recResult.getFieldString("YD_WRK_CRN")));
					    	recInTemp.setField("YD_WBOOK_ID", 	commUtils.trim(recResult.getFieldString("YD_WBOOK_ID")));
					    	
					    	jrRtn = commUtils.addSndData(jrRtn, recInTemp);
						}			
		
					}
				}
			}				
							
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN5L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강자동화 크레인권상실적(N6YSL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN6YSL005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동화 크레인권상실적[GdsYsL2RcvSeEJB.rcvN6YSL005] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydUpWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LOC"    )); //야드권상실적위치
			String ydUpWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_UP_WR_LAYER"  )); //야드권상실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydDnWoLoc     = ""; //야드권하지시위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L2_WR_GP"     , "U"          ); //야드L2실적구분(권상실적)
			resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권상실적 수신처리"); //야드L3MESSAGE(Error)

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

			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			jrParam.setField("YD_STK_COL_GP", ydUpWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydUpWrLoc.substring(6, 8)); //야드적치Bed번호
			
			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch", logId, methodNm, "크레인스케줄상태 조회");

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "UP11";
				ydL3Msg = "오류:크레인스케쥴 DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydDnWoLoc       = commUtils.trim(jrChk.getFieldString("YS_DN_WO_LOC"    )); //야드권하지시위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"1".equals(tmpStat) && !"W".equals(tmpStat)) {
					ydL3HdRsCd = "UP12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "UP13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			String currDt = commUtils.getDateTime14(); //현재시각
			

			/**********************************************************
			* 4. 권상실적위치가 대차(하차)
			* 4.1 대차 하차 정보 등록
			*   - 대차이송재료 삭제
			*   - 대차스케줄 삭제 : 하차완료 시
			**********************************************************/

			/**********************************************************
			* 5. 권상실적위치가 차량(하차)
			*    차량스케줄 야드차량진행상태가 하차도착(B) 또는 하차검수(C) 이고
			*  	   야드차량사용구분이 구내운송(L) 이면
			* 5.1 구내운송 소재차량하차개시(YSTSJ009) 전송
			* 5.2 차량이송재료 삭제
			* 5.3 차량스케줄 수정
			*   - 야드차량진행상태(D:하차개시), 야드설비작업매수 등 수정
			**********************************************************/
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			if("TR".equals(ydUpWrLoc.substring(2, 4))) {
				//차량스케줄  야드하차작업예약ID 등록 (차량스케줄에 야드하차작업예약ID 없을 경우)
				recPara.setField("YD_WBOOK_ID" , ydWbookId ); 
				recPara.setField("MODIFIER" , modifier     ); 

				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId 
				--크레인권상실적 하차 차량스케줄 작업예약ID 수정 
				MERGE INTO TB_YS_CARSCH TS USING (
				SELECT YD_CAR_SCH_ID
				      ,YD_WBOOK_ID
				  FROM (SELECT TS.YD_CAR_SCH_ID
				              ,WM.YD_WBOOK_ID
				          FROM TB_YS_CARSCH     TS
				              ,TB_YS_CARFTMVMTL TM
				              ,TB_YS_WRKBOOKMTL WM
				         WHERE WM.SSTL_NO        = TM.SSTL_NO
				           AND TM.YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				           AND WM.YD_WBOOK_ID   = :V_YD_WBOOK_ID
				           AND WM.DEL_YN        = 'N'
				           AND TM.DEL_YN        = 'N'
				           AND TS.DEL_YN        = 'N'
				           AND NOT EXISTS (SELECT YD_CAR_SCH_ID
				                             FROM TB_YS_CARSCH
				                            WHERE YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				                              AND DEL_YN               = 'N')
				         ORDER BY TS.YD_CAR_SCH_ID)
				 WHERE ROWNUM = 1
				) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
				     TS.MODIFIER             = :V_MODIFIER
				    ,TS.MOD_DDTT             = SYSDATE
				    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_WBOOK_ID
				 */   
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUdWbId", logId, methodNm, "차량스케줄(하차) 수정");
				
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setResultCode(logId);	//Log ID
				recPara.setResultMsg(methodNm);	//Log Method Name
				recPara.setField("WR_DT" 		 , commUtils.getDateTime14()  ); 
				recPara.setField("YS_STK_COL_GP" , ydUpWrLoc.substring(0, 6)     ); 
				//구내운송 소재차량하차개시
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ009", recPara));
				
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				recPara.setField("MODIFIER" , modifier     ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel 
				--크레인권상실적 하차 차량이송재료 삭제 
				MERGE INTO TB_YS_CARFTMVMTL TM USING (
				SELECT TS.YD_CAR_SCH_ID
				      ,CM.SSTL_NO
				  FROM TB_YS_CRNSCH    CS
				      ,TB_YS_CRNWRKMTL CM
				      ,TB_YS_CARSCH    TS
				 WHERE CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = TS.YD_CARUD_WRK_BOOK_ID
				   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CM.DEL_YN        = 'N'
				   AND TS.DEL_YN        = 'N'
				) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO)
				WHEN MATCHED THEN UPDATE SET
					 TM.MODIFIER = :V_MODIFIER
				    ,TM.MOD_DDTT = SYSDATE
				    ,TM.DEL_YN   = 'Y'
				*/    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarMtlDel", logId, methodNm, "차량이송재료 삭제");
				
				//하차 차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드하차개시일시 수정
				
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID", ydCrnSchId ); 
				recPara.setField("YD_WBOOK_ID" 	, ydWbookId); 
				recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
				recPara.setField("MODIFIER" 	, modifier     ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUd
				--크레인권상실적 하차 차량스케줄 수정 
				MERGE INTO TB_YS_CARSCH TS USING (
				SELECT TS.YD_CAR_SCH_ID
				      ,COUNT(ST.SSTL_NO)        AS YD_EQP_WRK_SH
				      ,NVL(SUM(ST.YD_MTL_WT),0) AS YD_EQP_WRK_WT
				      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN 'D'                                   --하차개시
				       ELSE MIN(TS.YD_CAR_PROG_STAT) END AS YD_CAR_PROG_STAT
				      ,CASE WHEN MIN(TS.YD_CAR_PROG_STAT) IN ('B','C') --하차도착,검수
				            THEN NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
				       ELSE MIN(TS.YD_CARUD_ST_DT) END AS YD_CARUD_ST_DT
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				      ,TB_YS_STOCK      ST
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TM.SSTL_NO              = ST.SSTL_NO(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID
				) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
				WHEN MATCHED THEN UPDATE SET
					 TS.MODIFIER         = :V_MODIFIER
				    ,TS.MOD_DDTT         = SYSDATE
				    ,TS.YD_EQP_WRK_SH    = DD.YD_EQP_WRK_SH
				    ,TS.YD_EQP_WRK_WT    = DD.YD_EQP_WRK_WT
				    ,TS.YD_CAR_PROG_STAT = DD.YD_CAR_PROG_STAT
				    ,TS.YD_CARUD_ST_DT   = DD.YD_CARUD_ST_DT
				*/    	
				commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchUd", logId, methodNm, "하차 차량스케줄 수정 ");
				
			}
			
			/**********************************************************
			* 6. 권하지시위치가 차량(상차)
			*    차량스케줄 야드차량진행상태가 상차도착(2) 또는 상차검수(3) 이면
			* 6.1 구내운송 소재차량상차개시(YSTSJ007) 전송
			*   - 야드차량사용구분이 구내운송(L)
			* 6.2 출하관리출하상차개시(YSDSJ006) 전송
			*   - 야드차량사용구분이 출하차량(G)
			* 6.3 차량스케줄 수정
			*   - 야드설비작업상태(U:공차), 야드차량진행상태(4:상차개시) 등 수정
			**********************************************************/
			if ("TR".equals(ydDnWoLoc.substring(2, 4))) {
				//차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" , ydWbookId     ); 
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd 
				--크레인권상실적 상차 차량스케줄 조회 
				SELECT TS.YD_CAR_SCH_ID               --야드차량스케쥴ID
				      ,TS.YD_CAR_USE_GP               --야드차량사용구분
				      ,TS.TRN_EQP_CD                  --운송장비코드
				      ,TS.SPOS_WLOC_CD                --발지개소코드
				      ,SC.YD_PNT_CD AS SPOS_YD_PNT_CD --발지야드포인트코드
				      ,DECODE(SC.YD_CAR_USE_GP,'L',
				       NVL(MV.ARR_WLOC_CD,SF_SLAB_YD_ARR_WLOC_CD(MV.YD_AIM_YD_GP))) AS ARR_WLOC_CD --착지개소코드
				      ,TS.CARD_NO                     --카드번호
				      ,TS.CAR_NO                      --차량번호
				      ,TS.TRANS_ORD_DATE              --운송작업지시일자
				      ,TS.TRANS_ORD_SEQNO             --운송작업지시순번
				  FROM TB_YS_CRNSCH  CS
				      ,TB_YS_STKCOL  SC
				      ,TB_YS_CARSCH  TS
				      ,(SELECT WM.YD_WBOOK_ID
				              ,WM.YD_AIM_YD_GP
				              ,MV.ARR_WLOC_CD
				          FROM TB_PB_STLFRTOMOVE MV
				              ,(SELECT WB.YD_WBOOK_ID
				                      ,WB.YD_AIM_YD_GP
				                      ,WM.SSTL_NO
				                  FROM TB_YS_WRKBOOK    WB
				                      ,TB_YS_WRKBOOKMTL WM
				                 WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
				                   AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
				                   AND WM.DEL_YN = 'N'
				                   AND ROWNUM = 1) WM
				         WHERE MV.SSTL_NO = WM.SSTL_NO
				           AND MV.TRANSWORD_SEQNO = (SELECT MAX(MM.TRANSWORD_SEQNO)
				                                       FROM TB_PB_STLFRTOMOVE MM
				                                      WHERE MM.SSTL_NO = MV.SSTL_NO)) MV
				 WHERE CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				   AND CS.YD_WBOOK_ID   = MV.YD_WBOOK_ID(+)
				   AND SC.YS_STK_COL_GP = SUBSTR(CS.YS_DN_WO_LOC,1,6)
				   AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				   AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				     OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				   AND TS.YD_CAR_PROG_STAT IN ('2','3') --상차도착,검수
				   AND TS.DEL_YN = 'N'

					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL005CarSchLd", logId, methodNm, "차량하차스케줄 정보 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
				
					//상차개시 전문
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					
					if("L".equals(jrChk.getFieldString("YD_CAR_USE_GP"))) {
						//구내운송 소재차량상차개시
						jrYdMsg.setField("JMS_TC_CD"         , "YSTSJ007"); //JMSTC코드
						jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
						jrYdMsg.setField("TRN_EQP_CD"        , commUtils.trim(jrChk.getFieldString("TRN_EQP_CD"    ))); //운송장비코드
						jrYdMsg.setField("SPOS_WLOC_CD"      , commUtils.trim(jrChk.getFieldString("SPOS_WLOC_CD"  ))); //발지개소코드
						jrYdMsg.setField("SPOS_YD_PNT_CD"    , commUtils.trim(jrChk.getFieldString("SPOS_YD_PNT_CD"))); //발지야드포인트코드
						jrYdMsg.setField("ARR_WLOC_CD"       , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   ))); //착지개소코드
						jrYdMsg.setField("TRN_WRK_ST_DT"     , currDt    ); //운송작업시작일시
					} else {
						if(!commUtils.trim(jrChk.getFieldString("CMBN_CARLD_YN")).equals("E")) {   //복수동 2차 상차도 도착인 경우 제외
							
							//PIDEV
							//출하관리 상차개시							
//							if("Y".equals(sApplyYnPI)) {

								jrYdMsg.setField("MQ_TC_CD"          , "M10YDLMJ1074"); //JMSTC코드
								jrYdMsg.setField("MQ_TC_CREATE_DDTT" , currDt    ); //JMSTC생성일시				
								jrYdMsg.setField("TRN_REQ_DATE"   	 , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
								jrYdMsg.setField("TRN_REQ_SEQ"  	 , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
								jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
								jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
								jrYdMsg.setField("DIST_GOODS_GP"     , "R"                                ); //출하제품구분
								jrYdMsg.setField("SCH_YN"     		 , "N"                                ); //출하제품구분
								jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
								jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
								
//							} else {				
//								
//								jrYdMsg.setField("JMS_TC_CD"         , "YSDSJ006"); //JMSTC코드
//								jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
//								jrYdMsg.setField("CARD_NO"           , commUtils.trim(jrChk.getFieldString("CARD_NO"        ))); //카드번호
//								jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
//								jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
//								jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
//								jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
//								jrYdMsg.setField("TRANS_WORD_DATE"    , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
//								jrYdMsg.setField("TRANS_WORD_SEQNO"   , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
//								jrYdMsg.setField("SPST_FRTOMOVE_GP"  , "1" ); //특수강 이송구분
//								
//							}
						}	
						/*
						 * ====================================================================================
						 * 다음 도착차량 안내메세지 송신기능 추가 시작.
						 * ====================================================================================
						 */
						{
							JDTORecordSet rsCarSch  = JDTORecordFactory.getInstance().createRecordSet("");
							JDTORecord    recCarSch = JDTORecordFactory.getInstance().create();
							JDTORecord    carPara 	= JDTORecordFactory.getInstance().create();
							carPara.setField("YD_CAR_STOP_LOC", ydDnWoLoc.substring(0, 6));
			 				
							rsCarSch = commDao.select(carPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarSchByBayInSeq", logId, methodNm, "차량포인트 조회"); 
							
							if(rsCarSch == null || rsCarSch.size() <= 0){
							}else{
							
								String szNEW_YD_CAR_USE_GP 		= "";
								String szNEW_CAR_NO 			= "";
								String szNEW_CARD_NO 			= "";
								String szNEW_TRANS_ORD_DATE 	= "";
								String szNEW_TRANS_ORD_SEQNO 	= "";
								String szNEW_YD_CARPNT_CD2		= "";
								String szTEL_NO					= "";
								
								// 입동 가능 차량 조회 (가장 빠른 순서, 각강/반입 검수 완료되지 않는 차량 제외)
								for (int ii = 0; ii < rsCarSch.size(); ii++) {
									recCarSch = rsCarSch.getRecord(ii);					
									
									// 반입/각강 검수 완료 체크
									JDTORecordSet resSet      = null;;
									JDTORecord    recIn       = JDTORecordFactory.getInstance().create();
									
									resSet 	= JDTORecordFactory.getInstance().createRecordSet("");
									recIn 	= JDTORecordFactory.getInstance().create();
									recIn.setField("TRN_EQP_CD", StringHelper.evl(recCarSch.getFieldString("CAR_NO"), ""));
									resSet = commDao.select(recIn, "com.inisteel.cim.ys.common.dao.YsCommDAO.getRtnStkOrSbStkCmpl"
											, logId, methodNm, "반입/각강 검수 완료 체크"); 
									if (resSet != null && resSet.size() > 0 ) {
										continue;
									}
									
									// 가장 빠른 순서 차량 선택
									szNEW_YD_CAR_USE_GP 	= StringHelper.evl(recCarSch.getFieldString("YD_CAR_USE_GP")	, "");
									szNEW_CAR_NO 			= StringHelper.evl(recCarSch.getFieldString("CAR_NO")			, "");
									szNEW_CARD_NO 			= StringHelper.evl(recCarSch.getFieldString("CARD_NO")			, "");
									szNEW_TRANS_ORD_DATE 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_DATE")	, "");
									szNEW_TRANS_ORD_SEQNO 	= StringHelper.evl(recCarSch.getFieldString("TRANS_ORD_SEQNO")	, "");
									szNEW_YD_CARPNT_CD2		= StringHelper.evl(recCarSch.getFieldString("YD_CARPNT_CD")		, "");
									szTEL_NO				= StringHelper.evl(recCarSch.getFieldString("DEST_TEL_NO")		, "");
									break;
								}
								
								//-----------------------------------------------------------------------------------------------------------------------
								//입동순서가 가장빠른 차량이 구내운송인 경우에는 입동지시 전문을 전송하지 않는다.
								//입동순서가 가장빠른 출하차량인 경우에만 입동지시 전문을 전송한다.
								//-----------------------------------------------------------------------------------------------------------------------
								if( szNEW_YD_CAR_USE_GP.equals("G")) {
									
									String szMsg= "크레인권상시점 입동순서가 가장빠른 차량이 출하차량[차량번호:" 		+ szNEW_CAR_NO + 
									                         				", 카드번호:" 	  	+ szNEW_CARD_NO + 
									                         				", 운송지시일자:" 	+ szNEW_TRANS_ORD_DATE + 
									                         				", 운송지시순번:" 	+ szNEW_TRANS_ORD_SEQNO + "]이므로 입동대기 전문을 전송예정.";
									commUtils.printLog(logId, szMsg, "SL");	
									
									String sSmsMsg = szNEW_YD_CARPNT_CD2.substring(2,3)+"동 "+szNEW_YD_CARPNT_CD2.substring(1,2)+"통로 입동대기 하세요.";
									
									szMsg= " 입동대기 SMS 문자내용:"+sSmsMsg;
									commUtils.printLog(logId, szMsg, "SL");								 
						
									MessageSenderTalk    sender1 = new MessageSenderTalk();
									
					 		    	JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					 		    	recPara1.setField("PHONE_NUM"	, new String(szTEL_NO));
					 		    	recPara1.setField("TMPL_CD"		, new String("CM1"));
					 		    	recPara1.setField("SND_MSG"		, new String("[현대제철 공지사항]\n" + sSmsMsg));
					 		    	recPara1.setField("SUBJECT"		, new String("입동대기 알림"));
					 		    	recPara1.setField("SMS_SND_NUM"	, new String("0416806678"));
					 		    	recPara1.setField("RECV_ID"		,"1522110");
					 		    	recPara1.setField("GROUP_ID"	,"KaKao");
					 		    	recPara1.setField("PROGRAM_ID"	,"udttalk");
									sender1.sendTalk(recPara1);
									/*
									MessageSenderTalk    sender2 = new MessageSenderTalk();
									
									JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
									recPara2.setField("PHONE_NUM"	, new String("01038433916"));
									recPara2.setField("TMPL_CD"		, new String("CM1"));
					 		    	recPara2.setField("SND_MSG"		, new String("[현대제철 공지사항]\n" + sSmsMsg+"["+szNEW_CAR_NO+"]" ));
					 		    	recPara2.setField("SUBJECT"		, new String("입동대기 알림"));
					 		    	recPara2.setField("SMS_SND_NUM"	, new String("0416806678"));
					 		    	recPara2.setField("RECV_ID"		,"1522110");
					 		    	recPara2.setField("GROUP_ID"	,"KaKao");
					 		    	recPara2.setField("PROGRAM_ID"	,"udttalk");
									sender2.sendTalk(recPara2);
									*/
									szMsg= "크레인권상시점 입동지시수신전화[" + szTEL_NO + "]에 대한 입동대기 SMS 전송";
									commUtils.printLog(logId, szMsg, "SL");	
								}
							}
						}
						/*
						 * ====================================================================================
						 * 다음 도착차량 안내메세지 송신기능 추가 종료.
						 * ====================================================================================
						 */
					}
					
					//전송할 전문에 추가
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("MODIFIER" 	, modifier     ); 
					recPara.setField("ARR_WLOC_CD"  , commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"  ))); //착지개소코드
					recPara.setField("YD_WBOOK_ID" 	, ydWbookId     ); 
					recPara.setField("WR_DT" 		, commUtils.getDateTime14()     ); 
					recPara.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID
					//상차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd 
					--크레인권상실적 상차 차량스케줄 수정 
					UPDATE TB_YS_CARSCH
					   SET MODIFIER             = :V_MODIFIER
						  ,MOD_DDTT             = SYSDATE
					      ,YD_EQP_WRK_STAT      = 'U' --공차
					      ,YD_CAR_PROG_STAT     = '4' --상차개시
					      ,ARR_WLOC_CD          = :V_ARR_WLOC_CD
					      ,YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
					      ,YD_CARLD_ST_DT       = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
					 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
					   AND DEL_YN               = 'N'
					*/
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");
				}
			}
	

			/**********************************************************
			* 7. 설비, 크레인스케쥴, 적치단, 적치Bed 수정
			* 7.1 설비 야드설비상태(권상중) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 등록
			*   - 권상위치 재료정보 삭제
			* 7.3 적치Bed 야드적치Bed입출고상태(완산Bed->입출고가능) 수정
			* 7.4 크레인스케쥴 권상실적 수정
			**********************************************************/
			//야드권상작업수행구분
			String ydUpWrkActGp = ydEqpWrkMode;

			if ("0".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydUpWrkActGp = "B"; //Backup
			}

			//설비
			jrParam.setField("YD_EQP_ID"       , ydEqpId     ); //야드설비ID
			jrParam.setField("YD_EQP_STAT"     , "2"         ); //야드설비상태(권상중)
			//크레인스케쥴
			jrParam.setField("YD_UP_CMPL_DT"   , currDt      ); //야드권상완료일시
			jrParam.setField("YD_UP_WR_LOC"    , ydUpWrLoc   ); //야드권상실적위치
			jrParam.setField("YD_UP_WR_LAYER"  , ydUpWrLayer ); //야드권상실적단
			jrParam.setField("YD_UP_WRK_ACT_GP", ydUpWrkActGp); //야드권상작업수행구분
			jrParam.setField("YD_UP_WR_XAXIS"  , ydCrnXaxis  ); //야드권상실적X축
			jrParam.setField("YD_UP_WR_YAXIS"  , ydCrnYaxis  ); //야드권상실적Y축
			jrParam.setField("YD_UP_WR_ZAXIS"  , ydCrnZaxis  ); //야드권상실적Z축

			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			//적치단(크레인 및 권상위치) 수정
			//확인
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyrSC", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			//적치Bed(완산Bed->입출고가능) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkBedF", logId, methodNm, "적치Bed(완산Bed->입출고가능) 수정");
			//크레인스케쥴 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CrnSch", logId, methodNm, "크레인스케쥴 수정");

			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치


			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				resMsg.setField("MODIFIER"      , modifier  ); //수정자
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN6L004(resMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN6L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 선재일반창고크레인권하실적(N3YSL006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN3YSL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재일반창고-크레인권하실적[GdsYsL2RcvSeEJB.rcvN3YSL006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LAYER"  )); //야드권하실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydUpWrLoc     = ""; //야드권상실적위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)
			if ("4".equals(ydWrkProgStat)) {
				resMsg.setField("YD_L2_WR_GP", "D"); //야드L2실적구분(권하실적)
			} else {
				resMsg.setField("YD_L2_WR_GP", "F"); //야드L2실적구분(강제권하)
			}

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydDnWrLoc)) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";	
			} else if ("XX".equals(ydDnWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 이상";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "선재일반창고-크레인권하실적", "APPPI0", "K", "*");
			
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자
			
			JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
			jrParam2.setResultCode(logId);	//Log ID
			jrParam2.setResultMsg(methodNm);	//Log Method Name
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("MODIFIER"     , modifier  ); //수정자
			
			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch", logId, methodNm, "크레인스케줄상태 조회");

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:크레인스케쥴ID DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydSchCd         = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
				ydUpWrLoc       = commUtils.trim(jrChk.getFieldString("YS_UP_WR_LOC"    )); //야드권상실적위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) {
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//조회 Parameter
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrParam.setField("YD_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			//설비
			jrParam.setField("YD_EQP_ID"  , ydEqpId); //야드설비ID(크레인)
			jrParam.setField("YD_EQP_STAT", "4"    ); //야드설비상태(권하완료)
			
			//실제 야드권하실적단 및 기타 정보 조회
			String wbCmplYn = ""; //작업예약완료여부
			boolean chgDnWrLayer = false; //권하위치 적치단 변경여부

			jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006Curr", logId, methodNm, "현재정보  조회");
			
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				wbCmplYn = commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));

				commUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				if (!ydDnWrLayer.equals(tbDnWrLayer)) {
					commUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
					chgDnWrLayer = true;
					ydDnWrLayer  = tbDnWrLayer;
					ydCrnZaxis   = "";
				}
				
				if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
					ydCrnXaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
					ydCrnYaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
					ydCrnZaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
				}
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:권하위치 DB정보 없음"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;
			
			if ("0".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "B"; //Backup
			}
			
			String currDt = commUtils.getDateTime14(); //현재시각
			/**********************************************************
			* 3. 전문 전송
			**********************************************************/
			
			//크레인스케쥴
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      ); //야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp); //야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis  ); //야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis  ); //야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); //야드권하실적Z축
			jrParam.setField("WR_DT"           , currDt      ); //실적일시
			jrParam.setField("UP_DN_GP"        , "D"         ); //권상권하구분(권하)

			
			/**********************************************************
			* 4. 권하실적위치가 대차(상차)
			* 4.1 대차 상차 정보 등록
			*   - 작업예약 야드작업계획대차 수정
			*   - 대차이송재료 등록
			* 4.2 대차 하차스케줄 생성
			*   - 작업예약 등록
			*   - 작업예약재료 등록
			* 4.3 대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
			* 4.4 L2 전송 전문
			**********************************************************/
			/**********************************************************
			* 5. 권하실적위치가 차량(상차)
			* 5.1 차량이송재료 등록
			* 5.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 마지막 크레인스케줄 이면
			* 5.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 5.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 5.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 5.4 야드차량사용구분이 출하차량(G)
			* 5.4.1 출하관리 일품출하상차실적(YSDSJ007) 전송
			* 5.4.2 출하관리 출하상차완료(YSDSJ008) 전송
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
			String bayCarLdCmplYn 	= "N";  // 해당동 상차완료
			String drvTelNo  		= "";   // 기사전화번호
			
			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				//상차 차량스케줄  조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId     ); 
				recPara.setField("YS_STK_COL_GP" , ydDnWrLoc.substring(0, 6) ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd
				--크레인권하실적 상차 차량스케줄 조회 
				SELECT YD_CAR_SCH_ID
				     , YD_CAR_USE_GP 
				     , CAR_NO
				     , TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO
				     , YS_STK_COL_GP
				     , WLOC_CD
				     , YD_PNT_CD
				     , STOCK_CNT
				     , LYR_CNT
				     , CRNMTL_CNT
				     , CASE WHEN STOCK_CNT <= LYR_CNT + CRNMTL_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN
				  FROM
				       (
				        SELECT TS.YD_CAR_SCH_ID
				              ,TS.YD_CAR_USE_GP
				              ,TS.CAR_NO
				              ,TS.TRANS_ORD_DATE
				              ,TS.TRANS_ORD_SEQNO
				              ,SC.YS_STK_COL_GP
				              ,SC.WLOC_CD
				              ,SC.YD_PNT_CD
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_STOCK
				                 WHERE TRANS_ORD_DATE = TS.TRANS_ORD_DATE
				                   AND TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				               ) AS STOCK_CNT
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_STKLYR
				                 WHERE YS_STK_COL_GP = SC.YS_STK_COL_GP
				                   AND SSTL_NO IS NOT NULL 
				                   AND YD_STK_LYR_MTL_STAT = 'C')  AS LYR_CNT
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_CRNWRKMTL
				                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                   AND DEL_YN        = 'N' ) AS CRNMTL_CNT
				          FROM TB_YS_STKCOL SC
				              ,TB_YS_CARSCH TS
				         WHERE SC.YS_STK_COL_GP = :V_YS_STK_COL_GP
				           AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				           AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				             OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				           AND TS.DEL_YN = 'N'
				        ) 
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd", logId, methodNm, "상차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					CarNo 			= commUtils.trim(jrChk.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrChk.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrChk.getFieldString("YD_PNT_CD")); 					
					ydCarSchId  	= commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					ydCarUseGp 		= commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); //야드차량사용구분
					carLdCmplYn 	= commUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); //차량상차완료여부
					bayCarLdCmplYn 	= commUtils.trim(jrChk.getFieldString("CAR_BAY_LD_CMPL_YN")); //해당동 차량상차완료여부
					drvTelNo 		= commUtils.trim(jrChk.getFieldString("DEST_TEL_NO")); //해당동 차량상차완료여부
					
					//차량이송재료(TB_YS_CARFTMVMTL) 상차 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
					recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
					recPara.setField("MODIFIER" 	 , modifier     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarMtlIns 
					--크레인권하실적 차량이송재료 등록 
					MERGE INTO TB_YS_CARFTMVMTL TM USING (
					SELECT :V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
					      ,CM.SSTL_NO
					      ,:V_MODIFIER      AS MODIFIER
					      ,SYSDATE          AS MOD_DDTT
					      ,'N'              AS DEL_YN
					      ,'01'             AS YS_STK_BED_NO
					      ,TO_CHAR((SELECT COUNT(*)
					                  FROM TB_YS_CARFTMVMTL
					                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					                   AND DEL_YN         = 'N') + TO_NUMBER(CM.YS_STK_LYR_NO),'FM00') AS YS_STK_LYR_NO
					      ,ST.HCR_GP
					      ,ST.STL_PROG_CD
					  FROM TB_YS_CRNWRKMTL CM
					      ,TB_YS_STOCK     ST
					 WHERE CM.SSTL_NO       = ST.SSTL_NO
					   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND CM.DEL_YN        = 'N'
					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO)
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_CAR_SCH_ID, TM.SSTL_NO  , TM.REGISTER   , TM.REG_DDTT     ,
					        TM.MODIFIER     , TM.MOD_DDTT, TM.DEL_YN     , TM.YS_STK_BED_NO,
					        TM.YS_STK_LYR_NO, TM.HCR_GP  , TM.STL_PROG_CD)
					VALUES (DD.YD_CAR_SCH_ID, DD.SSTL_NO  , DD.MODIFIER   , DD.MOD_DDTT     ,
					        DD.MODIFIER     , DD.MOD_DDTT, DD.DEL_YN     , DD.YS_STK_BED_NO,
					        DD.YS_STK_LYR_NO, DD.HCR_GP  , DD.STL_PROG_CD)
					*/    	
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarMtlIns", logId, methodNm, " 상차 이송재료 등록 ");
					
					
					
					//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					recPara = JDTORecordFactory.getInstance().create();
					
					if ("Y".equals(carLdCmplYn)) {
						recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					} else {
						recPara.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}
					recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
					recPara.setField("YS_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
					recPara.setField("WR_DT" 			, currDt ); 
					recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
					recPara.setField("MODIFIER"         , modifier     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd 
					--크레인권하실적 상차 차량스케줄 수정
					MERGE INTO TB_YS_CARSCH TS USING (
					SELECT TM.YD_CAR_SCH_ID
					      ,:V_YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
					      ,COUNT(*)            AS YD_EQP_WRK_SH
					      ,SUM(ST.YD_MTL_WT)   AS YD_EQP_WRK_WT
					      ,:V_YD_WBOOK_ID      AS YD_WBOOK_ID
					      ,:V_YS_STK_COL_GP    AS YD_CARLD_STOP_LOC
					      ,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
					 --     ,SF_SLAB_YD_ARR_WLOC_CD(MIN(ST.YD_AIM_YD_GP)) AS ARR_WLOC_CD
					     ,'XXXXX' AS ARR_WLOC_CD
					  FROM TB_YS_CARFTMVMTL TM
					      ,TB_YS_STOCK      ST
					 WHERE TM.SSTL_NO       = ST.SSTL_NO
					   AND TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND TM.DEL_YN        = 'N'
					 GROUP BY TM.YD_CAR_SCH_ID
					) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
					     TS.MODIFIER             = :V_MODIFIER
					    ,TS.MOD_DDTT             = SYSDATE
					    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
					    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
					    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH
					    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT
					    ,TS.ARR_WLOC_CD          = NVL(TS.ARR_WLOC_CD,DD.ARR_WLOC_CD)
					    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_WBOOK_ID
					    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
					    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,DD.WR_DT)
					    ,TS.YD_CARLD_CMPL_DT     = DECODE(DD.YD_CAR_PROG_STAT,'5',DD.WR_DT,NULL)
					*/    
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setResultCode(logId);	//Log ID
					recPara.setResultMsg(methodNm);	//Log Method Name
					recPara.setField("YD_CAR_SCH_ID", ydCarSchId); 
					recPara.setField("YD_CRN_SCH_ID", ydCrnSchId); 
					
					//출하차량(G)
					if ("G".equals(ydCarUseGp)) {
												
						//PIDEV
						//출하관리 일품출하상차실적
//						if("Y".equals(sApplyYnPI)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1084", recPara));
//						} else {
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ007", recPara));
//						}
						
						//검수 등록
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
						recPara.setField("MODIFIER" , modifier     ); 
						/*  com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006EXAMINATIONCHKLIST 
						MERGE INTO TB_YS_EXAMINATIONCHKLIST TM USING (
						SELECT 
						       ST.TRANS_ORD_DATE
						      , ST.TRANS_ORD_SEQNO  
						      , ST.SSTL_NO         
						      , SUBSTR(YS_STR_LOC,1,1) AS YD_GP     
						      , ST.CAR_NO        
						      , '' AS CARD_NO          
						      , '' AS CHECKING_YN     
						      , '' AS LABEL_YN  
						      , '' AS YD_AB_CD      
						      , '' AS YD_AB_CD2        
						      , '' AS GATE_NM         
						      , '' AS BAY_GP    
						      , '' AS YD_CARPNT_CD  
						      , '' AS YD_CAR_UPP_LOC_CD
						      , '' AS REGISTER        
						      , SYSDATE AS REG_DDTT  
						      , '' AS MODIFIER      
						      , SYSDATE AS MOD_DDTT 
						      , 'N' AS DEL_YN  
						  FROM TB_YS_CRNWRKMTL CM
						      ,TB_YS_STOCK     ST
						 WHERE CM.SSTL_NO       = ST.SSTL_NO
						   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND CM.DEL_YN        = 'N'
						) DD ON (TM.TRANS_ORD_DATE = DD.TRANS_ORD_DATE AND TM.TRANS_ORD_SEQNO = DD.TRANS_ORD_SEQNO AND TM.SSTL_NO = DD.SSTL_NO AND TM.YD_GP = DD.YD_GP)
						WHEN NOT MATCHED THEN
						INSERT (TM.TRANS_ORD_DATE, TM.TRANS_ORD_SEQNO  , TM.SSTL_NO         , TM.YD_GP     ,
						        TM.CAR_NO        , TM.CARD_NO          , TM.CHECKING_YN     , TM.LABEL_YN  ,
						        TM.YD_AB_CD      , TM.YD_AB_CD2        , TM.GATE_NM         , TM.BAY_GP    ,
						        TM.YD_CARPNT_CD  , TM.YD_CAR_UPP_LOC_CD, TM.REGISTER        , TM.REG_DDTT  , 
						        TM.MODIFIER      , TM.MOD_DDTT         , TM.DEL_YN                                        )
						VALUES (DD.TRANS_ORD_DATE, DD.TRANS_ORD_SEQNO  , DD.SSTL_NO         , DD.YD_GP     ,
						        DD.CAR_NO        , DD.CARD_NO          , DD.CHECKING_YN     , DD.LABEL_YN  ,
						        DD.YD_AB_CD      , DD.YD_AB_CD2        , DD.GATE_NM         , DD.BAY_GP    ,
						        DD.YD_CARPNT_CD  , DD.YD_CAR_UPP_LOC_CD, DD.REGISTER        , DD.REG_DDTT  , 
						        DD.MODIFIER      , DD.MOD_DDTT         , DD.DEL_YN                                        )
						 
						*/    	
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006EXAMINATIONCHKLIST", logId, methodNm, "검수등록 ");
									
						if ("Y".equals(carLdCmplYn)) {
							
							//PIDEV					
							//출하관리 출하상차완료
//							if("Y".equals(sApplyYnPI)) {
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1094", recPara));
//							} else {
//								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ008", recPara));
//							}
							
//SJH 추가				//압연 조업으로 송신		
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSSBJ001CAR", recPara));														
						}						
//SJH16
						else {
							
						    // 출하상차 완료가 아니고 해당동 상차 완료 인 경우 ----> 복수동 처리 임
							// 차량출발처리
							if ("Y".equals(bayCarLdCmplYn)) { 
								
		    					commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
		    					JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
		    					recInTemp.setResultCode(logId);		//Log ID
		    					recInTemp.setResultMsg(methodNm);	//Log Method Name
		    					recInTemp.setField("CAR_NO", 			CarNo);			
		    					recInTemp.setField("SPOS_WLOC_CD", 		WlocCd);
		    					recInTemp.setField("SPOS_YD_PNT_CD",	ydPndCd);
		    					recInTemp.setField("TRANS_ORD_DATE",	TransOrdDate);
		    					recInTemp.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
		    					
		    					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
		    					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
		    					 
		    					jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
		    					
	    	    				commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 복수동 나머지 출하Lot EJB 호출", "SL");		    	    				
		    					
		    					//복수동 나머지 출하Lot	    	    				
		    					recInTemp = JDTORecordFactory.getInstance().create();
		    					
		    					// PIDEV
//		    					if("Y".equals(sApplyYnPI)) {
		    					
		    						recInTemp.setField("MQ_TC_CD"			, "M10LMYDJ1044");
			    					recInTemp.setField("MQ_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			    					recInTemp.setField("YD_GP",     		ydEqpId.substring(0, 1));
			    					recInTemp.setField("CAR_NO", 			CarNo);
			    					recInTemp.setField("TRN_REQ_DATE", 		TransOrdDate);
			    					recInTemp.setField("TRN_REQ_SEQ"   , 	TransOrdSeqNo);
			    					recInTemp.setField("DOUBLEDONG_CHECK", 	"Y");				//복수동 나머지 출하여부
			    					recInTemp.setField("YD_CAR_SCH_ID",     ydCarSchId);			    					
			    					recInTemp.setField("CMBN_CARLD_YN",     "E"); 				//첫번째 도착창고 : S 두번째 도착창고 : E
			    					recInTemp.setField("WAIT_ARR_DDTT",     currDt);
			    					recInTemp.setField("WAIT_ARR_GP",     	"B");				//대기장도착구분  - B:BACKUP , S:SMARTPHONE
			    					recInTemp.setField("TEL_NO",     		drvTelNo);			//기사 전화번호 			    					
			    					recInTemp.setField("YD_SND_YN",     	"Y");			    //mq야드에서 재송신 		    						
		    						
//		    					} else {
//		    				
//			    					recInTemp.setField("JMS_TC_CD"			, "DSYSJ005");
//			    					recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
//			    					recInTemp.setField("YD_GP",     		ydEqpId.substring(0, 1));
//			    					recInTemp.setField("CAR_NO", 			CarNo);
//			    					recInTemp.setField("TRANS_ORD_DT", 		TransOrdDate);
//			    					recInTemp.setField("TRANS_ORD_SEQNO", 	TransOrdSeqNo);
//			    					recInTemp.setField("DOUBLEDONG_CHECK", 	"Y");				//복수동 나머지 출하여부
//			    					recInTemp.setField("YD_CAR_SCH_ID",     ydCarSchId);			    					
//			    					recInTemp.setField("CMBN_CARLD_YN",     "E"); 				//첫번째 도착창고 : S 두번째 도착창고 : E
//			    					recInTemp.setField("WAIT_ARR_DDTT",     currDt);
//			    					recInTemp.setField("WAIT_ARR_GP",     	"B");				//대기장도착구분  - B:BACKUP , S:SMARTPHONE
//			    					recInTemp.setField("TEL_NO",     		drvTelNo);			//기사 전화번호 
//			    					
//		    					} 
		    					
	    	    				jrRtn = commUtils.addSndData(jrRtn, recInTemp);
							}
						}
					}	
					if ("L".equals(ydCarUseGp)) {
						//구내운송 상차완료
						if ("Y".equals(carLdCmplYn)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ008", recPara));
						}						
					}
				}
			}			
			/**********************************************************
			* 6. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 6.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 6.4 구내운송 소재차량하차완료 송신(YSTSJ010) 전송
			* 6.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 송신)
			String carUdCmplYn = "N";

			if ("TR".equals(ydUpWrLoc.substring(2, 4)) && !"TR".equals(ydDnWrLoc.substring(2, 4))) {
				//야드하차작업예약ID 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd 
				--크레인권하실적 하차 차량스케줄 조회
				SELECT TS.YD_CAR_SCH_ID
				      ,DECODE(COUNT(TM.SSTL_NO),0,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
				      ,TS.CAR_NO
				      ,TS.ARR_WLOC_CD AS WLOC_CD
				      ,TS.YD_PNT_CD3  AS YD_PNT_CD
				      ,TS.TRANS_ORD_DATE
				      ,TS.TRANS_ORD_SEQNO
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID      ,TS.CAR_NO
				      ,TS.YD_CARUD_STOP_LOC
				      ,TS.YD_PNT_CD3
				      ,TS.TRANS_ORD_DATE
				      ,TS.TRANS_ORD_SEQNO
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
					
					CarNo 			= commUtils.trim(jrChk.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrChk.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrChk.getFieldString("YD_PNT_CD")); 					

					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					carUdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					
					//차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("MODIFIER" , modifier     ); 
						recPara.setField("WR_DT" 			, currDt ); 
						recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd 
						--크레인권하실적 하차 차량스케줄 수정 
						UPDATE TB_YS_CARSCH
						   SET MODIFIER         = :V_MODIFIER
							  ,MOD_DDTT         = SYSDATE
						      ,YD_EQP_WRK_STAT  = 'U' --공차
						      ,YD_CAR_PROG_STAT = 'E' --하차완료
						      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
						 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
						   AND DEL_YN           = 'N'
						*/    
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 수정  ");

						
						//구내운송 소재차량하차완료
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ010", recPara));
					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					commUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				}
			}

			/**********************************************************
			* 7. 설비, 적치단 , 크레인스케쥴, 저장품, 작업예약재료, 작업예약 수정
			* 7.1 설비 야드설비상태(권하완료) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 삭제
			*   - 권하위치 재료정보 수정
			*   - 권하위치외 같은 재료번호로 등록된 적치단 수정(권하분리 재료 제외)
			* 7.3 크레인스케쥴
			*   - 크레인작업재료 삭제
			*   - 크레인스케쥴 권하실적 수정 및 삭제
			* 7.4 작업예약 마지막 크레인스케쥴 이면
			*   - 작업예약재료 삭제
			*   - 작업예약 수정 및 삭제
			**********************************************************/
			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			
			//이전 권하위치 단을 Clear 한다.
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("YS_STK_COL_GP", "K%"); 
			jrParam2.setField("YS_STK_BED_NO", "%"); 
			commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtl", logId, methodNm, "적치단 작업Clear ");
			
			//적치단(크레인 및 권하위치) 수정
			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				// 선재 차량권하시 별도
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyrTr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			} else {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			}
			//크레인작업재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnMtl", logId, methodNm, "크레인작업재료 삭제");
			//크레인스케쥴 권하실적 수정 및 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnSch", logId, methodNm, "크레인스케쥴 권하실적 수정 및 삭제");

			//작업예약완료 이면
			if ("Y".equals(wbCmplYn)) {
				//작업예약재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDel", logId, methodNm, "작업예약재료 삭제");
				//작업예약 수정 및 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbDel", logId, methodNm, "작업예약 수정 및 삭제");
				
			} else {
				//크레인작업재료번호로 작업예약재료 삭제 (작업예약재료에 작업이 완료된 재료는 DEL_YN='Y' 함으로써 스케줄 취소 후 재 작업시 작업대상에서 제외시킨다.) 
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDelBySchId", logId, methodNm, "크레인권하실적 권하완료된 작업예약재료만 삭제");
			}
			
			
			/**********************************************************
			* 7.5 작업예약 마지막 크레인스케쥴 이면
			*   - 입고스케줄이면 YSDSJ001 (입고작업실적) 송신
			*   - 반납스케줄이면 YSDSJ011 (반납확인정보) 송신
			**********************************************************/
			if ("Y".equals(wbCmplYn)) {
				
				if(ydSchCd.substring(6, 7).equals("L") 
				   && ydDnWrLoc.matches("[K][A-E]\\d\\d\\d\\d\\d\\d")) {
					//입고 스케줄이고 권하위치가 야드이고
					//진도코드가 입고대기(H), 종합판정대기(G) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001BySchId", jrParam));
				}
				else
				if(ydDnWrLoc.substring(2, 4).equals("TY")) {
					//반납 스케줄이고 
					//진도코드가 반납대기(J) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ011BySchId", jrParam));
//SJH 추가			//압연 조업으로 송신		
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSSBJ001", jrParam));						
				}
			}
// 더미작업도 송신 되어 야 함
			if(ydSchCd.substring(6, 7).equals("M") 
			   && ydDnWrLoc.matches("[K][A-E]\\d\\d\\d\\d\\d\\d")) {
				//이적 스케줄이고 권하위치가 야드이고
				//진도코드가 입고대기(H), 종합판정대기(G), 판정보류(F) 가 아닌경우 (진도코드 판단은 쿼리에서 한다.)
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				//PIDEV				
//				if("Y".equals(sApplyYnPI)) {
					//제품이적 실적 
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1034BySchId", jrParam));				
//				} else {
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ002BySchId", jrParam));	
//				}
				
			}

			
			if ("Y".equals(carLdCmplYn)||"Y".equals(carUdCmplYn)) {  // 상하차 완료
				if(!TransOrdDate.equals("")) {      // 구내운송은 자동 출발처리 안함
					// 차량출발 처리	
					commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
					JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("CAR_NO", 				CarNo);			
					recInTemp.setField("SPOS_WLOC_CD", 			WlocCd);
					recInTemp.setField("SPOS_YD_PNT_CD", 		ydPndCd);
					recInTemp.setField("TRANS_ORD_DATE", 		TransOrdDate);
					recInTemp.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
					
					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);  //여기서 입동지시 호출(YSDSJ005)도 같이 함.
					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
				}	
			}	
			//지니지니
			/**********************************************************
			* 8. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 8.1 
			*   - 크레인스케줄 재료를 대상
			*   - 권하실적위치로 저장위치 수정
			*   - 권상실적위치가 차량이면 현재진도코드 수정
			* 8.2 저장품 수정
			*   - 작업예약 재료를 대상
			*   - 작업예약이 삭제되었으면 작업예약ID, 스케줄코드 삭제
			*   - 현재진도코드가 저장품과 다르면 관련 항목(산적LotType 등) 수정
			*   - 저장위치가 저장품과 다르면 저장위치 수정
			* 8.3 작업이력 등록
			*   - 크레인스케줄 재료를 대상
			* 8.4 차량상차 또는 하차완료 시 차량이송재료 대상으로
			* 8.4.1 
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 8.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			* 8.4.2 
			*     - 상차 : 소재이송일시
			*     - 하차 : 소재인수일시
			**********************************************************/
			//BUNDLE공통 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006BundleComm", logId, methodNm, "BUNDLE공통 수정");

			//저장품 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StockByBundle", logId, methodNm, "저장품 수정");
			
			//작업이력 등록
			jrParam.setField("YD_CAR_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CAR_SCH_ID" ))); 
			jrParam.setField("YD_TCAR_SCH_ID"  , commUtils.trim(jrParam.getFieldString("YD_TCAR_SCH_ID" ))); 
			jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CRN_SCH_ID" ))); 
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHist", logId, methodNm, "작업이력 등록");

			
			/**********************************************************
			* 9. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송(YSNxL002)
			**********************************************************/
			if (chgDnWrLayer) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L002DnWr", jrParam));
			}
			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치

			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN3L004(resMsg));
			}
			
			/**********************************************************
			* 11. 크레인작업지시요구 전문 호출(NxYDL004)
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("JMS_TC_CD", msgId.substring(0, 2) + "YSL004"); //JMSTC코드
				
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        , modifier  ); //수정자
			
			//크레인작업지시 전문을 추가
			jrRtn = commUtils.addSndData(jrRtn, this.rcvN3YSL004(jrYdMsg));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN3L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강야드 크레인권하실적(N4YSL006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN4YSL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강야드크레인권하실적[GdsYsL2RcvSeEJB.rcvN4YSL006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LAYER"  )); //야드권하실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydUpWrLoc     = ""; //야드권상실적위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)
			if ("4".equals(ydWrkProgStat)) {
				resMsg.setField("YD_L2_WR_GP", "D"); //야드L2실적구분(권하실적)
			} else {
				resMsg.setField("YD_L2_WR_GP", "F"); //야드L2실적구분(강제권하)
			}

			//PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "봉강야드크레인권하실적", "APPPI0", "K", "*");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydDnWrLoc)) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";
			} else if ("XX".equals(ydDnWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 이상";				
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자
			
			JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
			jrParam2.setResultCode(logId);	//Log ID
			jrParam2.setResultMsg(methodNm);	//Log Method Name
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("MODIFIER"     , modifier  ); //수정자
			
			JDTORecord jrParam3 = JDTORecordFactory.getInstance().create();
			jrParam3.setResultCode(logId);	//Log ID
			jrParam3.setResultMsg(methodNm);	//Log Method Name
			jrParam3.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam3.setField("MODIFIER"     , modifier  ); //수정자			
			
			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch", logId, methodNm, "크레인스케줄상태 조회");
			

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:크레인스케쥴ID DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydSchCd         = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
				ydUpWrLoc       = commUtils.trim(jrChk.getFieldString("YS_UP_WR_LOC"    )); //야드권상실적위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) {
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//조회 Parameter
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrParam.setField("YD_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			//설비
			jrParam.setField("YD_EQP_ID"  , ydEqpId); //야드설비ID(크레인)
			jrParam.setField("YD_EQP_STAT", "4"    ); //야드설비상태(권하완료)
			
			//실제 야드권하실적단 및 기타 정보 조회
			String wbCmplYn = ""; //작업예약완료여부
			boolean chgDnWrLayer = false; //권하위치 적치단 변경여부
			
			if(ydUpWrLoc.equals(ydDnWrLoc) &&( ydDnWrLoc.startsWith("KATY") || ydDnWrLoc.startsWith("KBTY"))){
				// TYMultiLyr 동일베드 권상권하
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CurrTYmultiLyrDummy", logId, methodNm, "TYMultiLyrDummy 현재정보 조회");	
			} else if(ydDnWrLoc.startsWith("KATY") || ydDnWrLoc.startsWith("KBTY")){
				// TYMultiLyr
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CurrTYmultiLyr", logId, methodNm, "TYMultiLyr 현재정보  조회");	
			} else {
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006Curr", logId, methodNm, "현재정보  조회");
			}
			
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				wbCmplYn = commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));

				commUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				if (!ydDnWrLayer.equals(tbDnWrLayer)) {
					commUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
					chgDnWrLayer = true;
					ydDnWrLayer  = tbDnWrLayer;
					ydCrnZaxis   = "";
				}
				
				if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
					ydCrnXaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
					ydCrnYaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
					ydCrnZaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
				}
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:권하위치 DB정보 없음"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;
			
			if ("0".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "B"; //Backup
			}
			
			String currDt = commUtils.getDateTime14(); //현재시각
			
			//크레인스케쥴
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      ); //야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp); //야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis  ); //야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis  ); //야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); //야드권하실적Z축
			jrParam.setField("WR_DT"           , currDt      ); //실적일시
			jrParam.setField("UP_DN_GP"        , "D"         ); //권상권하구분(권하)

			
			/**********************************************************
			* 4. 권하실적위치가 대차(상차)
			* 4.1 대차 상차 정보 등록
			*   - 작업예약 야드작업계획대차 수정
			*   - 대차이송재료 등록
			* 4.2 대차 하차스케줄 생성
			*   - 작업예약 등록
			*   - 작업예약재료 등록
			* 4.3 대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
			* 4.4 L2 전송 전문
			*   - 대차작업실적 전송 
			*   - 대차출발지시 전송 : 상차완료 시
			**********************************************************/
			if ("TC".equals(ydDnWrLoc.substring(2, 4))) {
				//야드작업계획대차
				jrParam.setField("YD_WRK_PLAN_TCAR", ydDnWrLoc.substring(0, 1) + "X" + ydDnWrLoc.substring(2, 6));
				
				//대차상차스케쥴 조회
				/* 크레인권하실적 상차 대차스케줄 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006TCarSchLd 
				SELECT YD_TCAR_SCH_ID 
				     , CASE WHEN TC_LYR_SH < TO_NUMBER(DECODE(SUBSTR(YD_EQP_ID,1,1),'K',3,1)) --제품은 3단 블름 1단
				            THEN CASE WHEN CRNWRK_SH + TC_WRKBOOK_SH + PC_LYR_SH = 0 THEN 'Y' ELSE 'N' END
				            ELSE 'Y' 
				            END AS TCAR_LD_CMPL_YN --대차상차완료여부
				  FROM  
				        (
				        SELECT TS.YD_TCAR_SCH_ID
				             , TS.YD_EQP_ID
				             , (SELECT TO_NUMBER(NVL(MAX(YS_STK_LYR_NO),0)) + 1 --현재 권하 실적 +1
				                  FROM TB_YS_STKLYR
				                 WHERE YS_STK_COL_GP = SUBSTR(:V_YD_DN_WR_LOC,1,6)
				                   AND SSTL_NO IS NOT NULL
				                   AND DEL_YN = 'N') AS TC_LYR_SH --대차 적치매수
				             , (SELECT COUNT(*)
				                  FROM TB_YS_CRNSCH
				                 WHERE SUBSTR(YS_DN_WO_LOC,1,4) = SUBSTR(:V_YD_DN_WR_LOC,1,4)
				                   AND YD_CRN_SCH_ID <> :V_YD_CRN_SCH_ID --현재거 제외
				                   AND DEL_YN = 'N') AS CRNWRK_SH  --크레인 권하위치 가 대차
				             , (SELECT COUNT(*)
				                  FROM TB_YS_WRKBOOK
				                 WHERE YD_SCH_CD LIKE SUBSTR(:V_YD_DN_WR_LOC,1,2) ||'TC' ||'%'
				                   AND YD_WBOOK_ID <> :V_YD_WBOOK_ID  --현재거 제외
				                   AND DEL_YN = 'N') AS TC_WRKBOOK_SH --작업예약에 대차
				             , (SELECT COUNT(*)
				                  FROM TB_YS_STKLYR A
				                     , TB_YS_STOCK  B
				                 WHERE A.YS_STK_COL_GP LIKE SUBSTR(:V_YD_DN_WR_LOC,1,2) ||'PC' ||'%'
				                   AND A.YS_STK_BED_NO > '06'
				                   AND A.SSTL_NO = B.SSTL_NO 
				                   AND B.YD_RCPT_PLN_STR_LOC LIKE DECODE(SUBSTR(:V_YD_DN_WR_LOC,1,2),'KA','KB','KB','KA') ||'%'
				                   AND A.DEL_YN = 'N') AS PC_LYR_SH 
				          FROM TB_YS_TCARSCH TS
				         WHERE TS.YD_EQP_ID = :V_YD_WRK_PLAN_TCAR
				           AND TS.DEL_YN    = 'N'
				        )
				*/
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006TCarSchLd", logId, methodNm, "대차상차스케쥴 조회");
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					jrParam.setField("YD_TCAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_TCAR_SCH_ID"))); //야드대차스케쥴ID(이력등록시에도 사용)
					String tcarLdCmplYn = commUtils.trim(jrChk.getFieldString("TCAR_LD_CMPL_YN")); //대차상차완료여부

					//작업예약 야드작업계획대차 수정
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbTCar", logId, methodNm, "작업예약 야드작업계획대차 수정");

					//대차이송재료 등록
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006TCarMtlIns", logId, methodNm, "대차이송재료 등록");

					commUtils.printLog(logId, "대차 상차 완료여부 : " + tcarLdCmplYn, "SL");
					
					if ("N".equals(tcarLdCmplYn)) {
						//상차완료가 아니면
						jrParam.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					} else {
						//상차완료이면
						jrParam.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)

						//대차스케줄 처리 (영대차출발지시)
						//야드하차작업예약ID 생성
						String ydCarudWrkBookId = commDao.getSeqId(logId, methodNm, "WrkBook");

						if ("".equals(ydCarudWrkBookId)) {
							ydL3Msg = "오류:대차작업예약ID 생성 실패";
							resMsg.setField("YD_L3_HD_RS_CD", "DN21" ); //야드L3처리결과코드
							resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
							throw new Exception(ydL3Msg);
						}

						//작업예약 등록
						jrParam.setField("YD_CARUD_WRK_BOOK_ID", ydCarudWrkBookId); //야드하차작업예약ID
						jrParam.setField("YD_SCH_ST_GP"        , ydDnWrkActGp    ); //야드스케쥴기동구분

						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbTCarIns", logId, methodNm, "작업예약 등록");

						//작업예약재료 등록
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlTCarIns", logId, methodNm, "작업예약재료 등록");
						
						/*
						 * 상차완료 후 추가로 상차작업을 진행해서 중복 작업예약이 발생함.
						 * - 중복제거 로직 추가
						 */
						//작업예약재료 삭제
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlTCarDel", logId, methodNm, "작업예약재료 삭제");
						//작업예약 삭제
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbTCarDel", logId, methodNm, "작업예약 삭제");
					
					}

					//대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006TcarSchLd", logId, methodNm, "대차스케줄 수정");
					
					//L2 대차작업실적
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L006", jrParam));
					
					//L2 영대차출발지시
					if ("Y".equals(tcarLdCmplYn)) {
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L005", jrParam));
					}						
				}
			}
			
			/**********************************************************
			* 5. 권하실적위치가 차량(상차)
			* 5.1 차량이송재료 등록
			* 5.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 마지막 크레인스케줄 이면
			* 5.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 5.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 5.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 5.4 야드차량사용구분이 출하차량(G)
			* 5.4.1 출하관리 일품출하상차실적(YSDSJ007) 전송
			* 5.4.2 출하관리 출하상차완료(YSDSJ008) 전송
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
			String bayCarLdCmplYn 	= "N";  // 해당동 상차완료
			String drvTelNo  		= "";   // 기사전화번호

			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				//상차 차량스케줄  조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId     ); 
				recPara.setField("YS_STK_COL_GP" , ydDnWrLoc.substring(0, 6) ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd
				--크레인권하실적 상차 차량스케줄 조회 
				SELECT YD_CAR_SCH_ID
				     , YD_CAR_USE_GP 
				     , CAR_NO
				     , TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO
				     , YS_STK_COL_GP
				     , WLOC_CD
				     , YD_PNT_CD
				     , STOCK_CNT
				     , LYR_CNT
				     , CRNMTL_CNT
				     , CASE WHEN STOCK_CNT <= LYR_CNT + CRNMTL_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN
				  FROM
				       (
				        SELECT TS.YD_CAR_SCH_ID
				              ,TS.YD_CAR_USE_GP
				              ,TS.CAR_NO
				              ,TS.TRANS_ORD_DATE
				              ,TS.TRANS_ORD_SEQNO
				              ,SC.YS_STK_COL_GP
				              ,SC.WLOC_CD
				              ,SC.YD_PNT_CD
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_STOCK
				                 WHERE TRANS_ORD_DATE = TS.TRANS_ORD_DATE
				                   AND TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				               ) AS STOCK_CNT
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_STKLYR
				                 WHERE YS_STK_COL_GP = SC.YS_STK_COL_GP
				                   AND SSTL_NO IS NOT NULL 
				                   AND YD_STK_LYR_MTL_STAT = 'C')  AS LYR_CNT
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_CRNWRKMTL
				                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                   AND DEL_YN        = 'N' ) AS CRNMTL_CNT
				          FROM TB_YS_STKCOL SC
				              ,TB_YS_CARSCH TS
				         WHERE SC.YS_STK_COL_GP = :V_YS_STK_COL_GP
				           AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				           AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				             OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				           AND TS.DEL_YN = 'N'
				        ) 
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd", logId, methodNm, "상차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					CarNo 			= commUtils.trim(jrChk.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrChk.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrChk.getFieldString("YD_PNT_CD")); 
					ydCarSchId  	= commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					ydCarUseGp 		= commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); //야드차량사용구분
					carLdCmplYn 	= commUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); //차량상차완료여부
					bayCarLdCmplYn 	= commUtils.trim(jrChk.getFieldString("CAR_BAY_LD_CMPL_YN")); //해당동 차량상차완료여부
					drvTelNo 		= commUtils.trim(jrChk.getFieldString("DEST_TEL_NO")); //해당동 차량상차완료여부
					
					//차량이송재료(TB_YS_CARFTMVMTL) 상차 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
					recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
					recPara.setField("MODIFIER" 	 , modifier     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarMtlIns 
					--크레인권하실적 차량이송재료 등록 
					MERGE INTO TB_YS_CARFTMVMTL TM USING (
					SELECT :V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
					      ,CM.SSTL_NO
					      ,:V_MODIFIER      AS MODIFIER
					      ,SYSDATE          AS MOD_DDTT
					      ,'N'              AS DEL_YN
					      ,'01'             AS YS_STK_BED_NO
					      ,TO_CHAR((SELECT COUNT(*)
					                  FROM TB_YS_CARFTMVMTL
					                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					                   AND DEL_YN         = 'N') + TO_NUMBER(CM.YS_STK_LYR_NO),'FM00') AS YS_STK_LYR_NO
					      ,ST.HCR_GP
					      ,ST.STL_PROG_CD
					  FROM TB_YS_CRNWRKMTL CM
					      ,TB_YS_STOCK     ST
					 WHERE CM.SSTL_NO       = ST.SSTL_NO
					   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND CM.DEL_YN        = 'N'
					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO)
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_CAR_SCH_ID, TM.SSTL_NO  , TM.REGISTER   , TM.REG_DDTT     ,
					        TM.MODIFIER     , TM.MOD_DDTT, TM.DEL_YN     , TM.YS_STK_BED_NO,
					        TM.YS_STK_LYR_NO, TM.HCR_GP  , TM.STL_PROG_CD)
					VALUES (DD.YD_CAR_SCH_ID, DD.SSTL_NO  , DD.MODIFIER   , DD.MOD_DDTT     ,
					        DD.MODIFIER     , DD.MOD_DDTT, DD.DEL_YN     , DD.YS_STK_BED_NO,
					        DD.YS_STK_LYR_NO, DD.HCR_GP  , DD.STL_PROG_CD)
					*/    	
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarMtlIns", logId, methodNm, " 상차 이송재료 등록 ");
					
					
					
					//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					recPara = JDTORecordFactory.getInstance().create();
					
					if ("Y".equals(carLdCmplYn)) {
						recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					} else {
						recPara.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}
					recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
					recPara.setField("YS_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
					recPara.setField("WR_DT" 			, currDt ); 
					recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
					recPara.setField("MODIFIER" 		, modifier     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd 
					--크레인권하실적 상차 차량스케줄 수정
					MERGE INTO TB_YS_CARSCH TS USING (
					SELECT TM.YD_CAR_SCH_ID
					      ,:V_YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
					      ,COUNT(*)            AS YD_EQP_WRK_SH
					      ,SUM(ST.YD_MTL_WT)   AS YD_EQP_WRK_WT
					      ,:V_YD_WBOOK_ID      AS YD_WBOOK_ID
					      ,:V_YS_STK_COL_GP    AS YD_CARLD_STOP_LOC
					      ,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
					 --     ,SF_SLAB_YD_ARR_WLOC_CD(MIN(ST.YD_AIM_YD_GP)) AS ARR_WLOC_CD
					     ,'XXXXX' AS ARR_WLOC_CD
					  FROM TB_YS_CARFTMVMTL TM
					      ,TB_YS_STOCK      ST
					 WHERE TM.SSTL_NO       = ST.SSTL_NO
					   AND TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND TM.DEL_YN        = 'N'
					 GROUP BY TM.YD_CAR_SCH_ID
					) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
					     TS.MODIFIER             = :V_MODIFIER
					    ,TS.MOD_DDTT             = SYSDATE
					    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
					    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
					    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH
					    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT
					    ,TS.ARR_WLOC_CD          = NVL(TS.ARR_WLOC_CD,DD.ARR_WLOC_CD)
					    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_WBOOK_ID
					    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
					    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,DD.WR_DT)
					    ,TS.YD_CARLD_CMPL_DT     = DECODE(DD.YD_CAR_PROG_STAT,'5',DD.WR_DT,NULL)
					*/    
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setResultCode(logId);	//Log ID
					recPara.setResultMsg(methodNm);	//Log Method Name
					recPara.setField("YD_CAR_SCH_ID", ydCarSchId); 
					recPara.setField("YD_CRN_SCH_ID", ydCrnSchId); 
					
					//출하차량(G)
					if ("G".equals(ydCarUseGp)) {

						// PIDEV
						//출하관리 일품출하상차실적
//						if("Y".equals(sApplyYnPI)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1084", recPara));
//						} else {
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ007", recPara));
//						}	
						
						//검수 등록
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
						recPara.setField("MODIFIER" , modifier     ); 
						/*  com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006EXAMINATIONCHKLIST 
						MERGE INTO TB_YS_EXAMINATIONCHKLIST TM USING (
						SELECT 
						       ST.TRANS_ORD_DATE
						      , ST.TRANS_ORD_SEQNO  
						      , ST.SSTL_NO         
						      , SUBSTR(YS_STR_LOC,1,1) AS YD_GP     
						      , ST.CAR_NO        
						      , '' AS CARD_NO          
						      , '' AS CHECKING_YN     
						      , '' AS LABEL_YN  
						      , '' AS YD_AB_CD      
						      , '' AS YD_AB_CD2        
						      , '' AS GATE_NM         
						      , '' AS BAY_GP    
						      , '' AS YD_CARPNT_CD  
						      , '' AS YD_CAR_UPP_LOC_CD
						      , '' AS REGISTER        
						      , SYSDATE AS REG_DDTT  
						      , '' AS MODIFIER      
						      , SYSDATE AS MOD_DDTT 
						      , 'N' AS DEL_YN  
						  FROM TB_YS_CRNWRKMTL CM
						      ,TB_YS_STOCK     ST
						 WHERE CM.SSTL_NO       = ST.SSTL_NO
						   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						) DD ON (TM.TRANS_ORD_DATE = DD.TRANS_ORD_DATE AND TM.TRANS_ORD_SEQNO = DD.TRANS_ORD_SEQNO AND TM.SSTL_NO = DD.SSTL_NO AND TM.YD_GP = DD.YD_GP)
						WHEN NOT MATCHED THEN
						INSERT (TM.TRANS_ORD_DATE, TM.TRANS_ORD_SEQNO  , TM.SSTL_NO         , TM.YD_GP     ,
						        TM.CAR_NO        , TM.CARD_NO          , TM.CHECKING_YN     , TM.LABEL_YN  ,
						        TM.YD_AB_CD      , TM.YD_AB_CD2        , TM.GATE_NM         , TM.BAY_GP    ,
						        TM.YD_CARPNT_CD  , TM.YD_CAR_UPP_LOC_CD, TM.REGISTER        , TM.REG_DDTT  , 
						        TM.MODIFIER      , TM.MOD_DDTT         , TM.DEL_YN                                        )
						VALUES (DD.TRANS_ORD_DATE, DD.TRANS_ORD_SEQNO  , DD.SSTL_NO         , DD.YD_GP     ,
						        DD.CAR_NO        , DD.CARD_NO          , DD.CHECKING_YN     , DD.LABEL_YN  ,
						        DD.YD_AB_CD      , DD.YD_AB_CD2        , DD.GATE_NM         , DD.BAY_GP    ,
						        DD.YD_CARPNT_CD  , DD.YD_CAR_UPP_LOC_CD, DD.REGISTER        , DD.REG_DDTT  , 
						        DD.MODIFIER      , DD.MOD_DDTT         , DD.DEL_YN                                        )
						 
						*/    	
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006EXAMINATIONCHKLIST", logId, methodNm, "검수등록 ");
			
						if ("Y".equals(carLdCmplYn)) {

							//PIDEV
							//출하관리 출하상차완료							
//							if("Y".equals(sApplyYnPI)) {
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1094", recPara));
//							} else {
//								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ008", recPara));
//							}
							
//SJH 추가				//압연 조업으로 송신		
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSSBJ001CAR", recPara));		
							
							//특수강재공야드L2로 전문 전송(반납대상)
//							if(("KA".equals(ydSchCd.substring(0 ,2)) || "KB".equals(ydSchCd.substring(0 ,2))) && "BM".equals(ydSchCd.substring(6 ,8))){
//								recPara.setField("YD_CRN_SCH_ID", ydCrnSchId); 							
//								jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L003", recPara));
//							}
						}						
//SJH16
						else {
							
						    // 출하상차 완료가 아니고 해당동 상차 완료 인 경우 ----> 복수동 처리 임
							// 차량출발처리
							if ("Y".equals(bayCarLdCmplYn)) { 
								
		    					commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
		    					JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
		    					recInTemp.setResultCode(logId);		//Log ID
		    					recInTemp.setResultMsg(methodNm);	//Log Method Name
		    					recInTemp.setField("CAR_NO", 			CarNo);			
		    					recInTemp.setField("SPOS_WLOC_CD", 		WlocCd);
		    					recInTemp.setField("SPOS_YD_PNT_CD",	ydPndCd);
		    					recInTemp.setField("TRANS_ORD_DATE",	TransOrdDate);
		    					recInTemp.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
		    					
		    					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
		    					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
		    					 
		    					jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
		    					
	    	    				commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 복수동 나머지 출하Lot EJB 호출", "SL");		    	    				
		    					
		    					//복수동 나머지 출하Lot
		    					recInTemp			= JDTORecordFactory.getInstance().create();
		    					
		    					// PIDEV
//		    					if("Y".equals(sApplyYnPI)) {
		    						
			    					recInTemp.setField("MQ_TC_CD"			, "M10LMYDJ1044");
			    					recInTemp.setField("MQ_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			    					recInTemp.setField("YD_GP",     		ydEqpId.substring(0, 1));
			    					recInTemp.setField("CAR_NO", 			CarNo);
			    					recInTemp.setField("TRN_REQ_DATE", 		TransOrdDate);
			    					recInTemp.setField("TRN_REQ_SEQ"   , 	TransOrdSeqNo);
			    					recInTemp.setField("DOUBLEDONG_CHECK", 	"Y");				//복수동 나머지 출하여부
			    					recInTemp.setField("YD_CAR_SCH_ID",     ydCarSchId);			    					
			    					recInTemp.setField("CMBN_CARLD_YN",     "E"); 				//첫번째 도착창고 : S 두번째 도착창고 : E
			    					recInTemp.setField("WAIT_ARR_DDTT",     currDt);
			    					recInTemp.setField("WAIT_ARR_GP",     	"B");				//대기장도착구분  - B:BACKUP , S:SMARTPHONE
			    					recInTemp.setField("TEL_NO",     		drvTelNo);			//기사 전화번호 			    					
			    					recInTemp.setField("YD_SND_YN",     	"Y");			    //mq야드에서 재송신 		    						
		    						
//		    					} else {
//		    					
//		    						recInTemp.setField("JMS_TC_CD"			, "DSYSJ005");
//			    					recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
//			    					recInTemp.setField("YD_GP",     		ydEqpId.substring(0, 1));
//			    					recInTemp.setField("CAR_NO", 			CarNo);
//			    					recInTemp.setField("TRANS_ORD_DT", 		TransOrdDate);
//			    					recInTemp.setField("TRANS_ORD_SEQNO", 	TransOrdSeqNo);
//			    					recInTemp.setField("DOUBLEDONG_CHECK", 	"Y");				//복수동 나머지 출하여부
//			    					recInTemp.setField("YD_CAR_SCH_ID",     ydCarSchId);			    					
//			    					recInTemp.setField("CMBN_CARLD_YN",     "E"); 				//첫번째 도착창고 : S 두번째 도착창고 : E
//			    					recInTemp.setField("WAIT_ARR_DDTT",     currDt);
//			    					recInTemp.setField("WAIT_ARR_GP",     	"B");				//대기장도착구분  - B:BACKUP , S:SMARTPHONE
//			    					recInTemp.setField("TEL_NO",     		drvTelNo);			//기사 전화번호
//			    					
//		    					}
		    					
	    	    				jrRtn = commUtils.addSndData(jrRtn, recInTemp);
							}
						}			
					}
					if ("L".equals(ydCarUseGp)) {
						//구내운송 상차완료
						if ("Y".equals(carLdCmplYn)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ008", recPara));
						}						
					}
				}
			}			
			/**********************************************************
			* 6. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 6.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 6.4 구내운송 소재차량하차완료 송신(YSTSJ010) 전송
			* 6.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 송신)
			String carUdCmplYn = "N";

			if ("TR".equals(ydUpWrLoc.substring(2, 4)) && !"TR".equals(ydDnWrLoc.substring(2, 4))) {
				//야드하차작업예약ID 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd 
				--크레인권하실적 하차 차량스케줄 조회
				SELECT TS.YD_CAR_SCH_ID
				      ,DECODE(COUNT(TM.SSTL_NO),0,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
				      ,TS.CAR_NO
				      ,TS.ARR_WLOC_CD AS WLOC_CD
				      ,TS.YD_PNT_CD3  AS YD_PNT_CD
				      ,TS.TRANS_ORD_DATE
				      ,TS.TRANS_ORD_SEQNO
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID      ,TS.CAR_NO
				      ,TS.YD_CARUD_STOP_LOC
				      ,TS.YD_PNT_CD3
				      ,TS.TRANS_ORD_DATE
				      ,TS.TRANS_ORD_SEQNO
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					CarNo 			= commUtils.trim(jrChk.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrChk.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrChk.getFieldString("YD_PNT_CD")); 		
					
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					carUdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					
					//차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("MODIFIER" , modifier     ); 
						recPara.setField("WR_DT" 			, currDt ); 
						recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd 
						--크레인권하실적 하차 차량스케줄 수정 
						UPDATE TB_YS_CARSCH
						   SET MODIFIER         = :V_MODIFIER
							  ,MOD_DDTT         = SYSDATE
						      ,YD_EQP_WRK_STAT  = 'U' --공차
						      ,YD_CAR_PROG_STAT = 'E' --하차완료
						      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
						 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
						   AND DEL_YN           = 'N'
						*/    
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 수정  ");

						
						//구내운송 소재차량하차완료
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ010", recPara));
					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					commUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				}
			}
			/**********************************************************
			* 7. 설비, 적치단 , 크레인스케쥴, 저장품, 작업예약재료, 작업예약 수정
			* 7.1 설비 야드설비상태(권하완료) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 삭제
			*   - 권하위치 재료정보 수정
			*   - 권하위치외 같은 재료번호로 등록된 적치단 수정(권하분리 재료 제외)
			* 7.3 크레인스케쥴
			*   - 크레인작업재료 삭제
			*   - 크레인스케쥴 권하실적 수정 및 삭제
			* 7.4 작업예약 마지막 크레인스케쥴 이면
			*   - 작업예약재료 삭제
			*   - 작업예약 수정 및 삭제
			**********************************************************/
			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			
			//이전 권하위치 단을 Clear 한다.
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("YS_STK_COL_GP", "K%"); 
			jrParam2.setField("YS_STK_BED_NO", "%"); 
			commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtl", logId, methodNm, "적치단 작업Clear ");
			
			//적치단(크레인 및 권하위치) 수정
			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				// 선재/봉강 차량권하시 별도
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyrTr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			} else {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			}
			//크레인작업재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnMtl", logId, methodNm, "크레인작업재료 삭제");
			//크레인스케쥴 권하실적 수정 및 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnSch", logId, methodNm, "크레인스케쥴 권하실적 수정 및 삭제");

			//작업예약완료 이면
			if ("Y".equals(wbCmplYn)) {
				//작업예약재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDel", logId, methodNm, "작업예약재료 삭제");
				//작업예약 수정 및 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbDel", logId, methodNm, "작업예약 수정 및 삭제");
			} else {
				//크레인작업재료번호로 작업예약재료 삭제 (작업예약재료에 작업이 완료된 재료는 DEL_YN='Y' 함으로써 스케줄 취소 후 재 작업시 작업대상에서 제외시킨다.) 
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDelBySchId", logId, methodNm, "크레인권하실적 권하완료된 작업예약재료만 삭제");
			}
			
			/**********************************************************
			* 7.5 작업예약 마지막 크레인스케쥴 이면
			*   - 입고스케줄이면 YSDSJ001 (입고작업실적) 송신
			*   - 이적스케줄이면 YSDSJ002 (입고작업실적) 송신
			*   - 반납스케줄이면 YSDSJ011 (반납확인정보) 송신
			**********************************************************/
			if ("Y".equals(wbCmplYn)) {
				
				if(ydSchCd.substring(6, 7).equals("L") 
				   && ydDnWrLoc.matches("[K][A-E]\\d\\d\\d\\d\\d\\d")) {
					//입고 스케줄이고 권하위치가 야드이고
					//진도코드가 입고대기(H), 종합판정대기(G) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001BySchId", jrParam));
				}
				else
				if(ydDnWrLoc.substring(2, 4).equals("TY")) {					
					//반납 스케줄이고 
					//진도코드가 반납대기(J) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ011BySchId", jrParam));
//SJH 추가			//압연 조업으로 송신		
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSSBJ001", jrParam));					
				}
				
			}			
// 더미작업도 송신 되어 야 함
			if(ydSchCd.substring(6, 7).equals("M") 
			   && ydDnWrLoc.matches("[K][A-E]\\d\\d\\d\\d\\d\\d")) {
				//이적 스케줄이고 권하위치가 야드이고
				//진도코드가 입고대기(H), 종합판정대기(G), 판정보류(F) 가 아닌경우 (진도코드 판단은 쿼리에서 한다.)
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);

				//PIDEV				
				//제품이적 실적
//				if("Y".equals(sApplyYnPI)) { 
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1034BySchId", jrParam));				
//				} else {
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ002BySchId", jrParam));
//				}
				
			}
			
			
			if(ydSchCd.substring(6, 7).equals("L") 
					&& ydDnWrLoc.matches("[K][A-E]TS\\d\\d\\d\\d")) {
				//입고 스케줄이고 권하위치가 Traverser 이면 Stacker Crane 입고스케줄을 생성한다.
				//1) Stacker Crane 작업예약등록
				//2) Stacker Crane 스케줄 Main 호출
				
				String scWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //stacker crane 용 작업예약ID
				
				String scSchCd = ydSchCd.substring(0,2) + "HS" + ydDnWrLoc.substring(4,6) + "LM"; //stacker crane 용 스케줄코드 (ex:KAHS01LM)
				String scEqpId = ydSchCd.substring(0,2) + "SC" + ydDnWrLoc.substring(4,6); //stacker crane 장비코드 (ex:KASC01)
				
				jrParam.setField("SC_YD_WBOOK_ID", scWbookId);  //신규 stacker crane 작업예약ID
				jrParam.setField("OHC_YD_WBOOK_ID", ydWbookId); //완료된 OHC 크래인 작업예약ID
				jrParam.setField("SC_SCH_CD", scSchCd); //신규 stcker crane 스케줄 코드
				jrParam.setField("SC_EQP_ID", scEqpId); //신규 stcker crane 설비ID
				jrParam.setField("YS_DN_WR_LOC", ydDnWrLoc); // 권하실적 위치 (From 위치)
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); 
				
				commUtils.printLog(logId, "권상위치XX" + ydUpWrLoc.substring(2, 4), "SL");
				//1.1) Stacker Crane 작업예약재료 생성 
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScWbMtl", logId, methodNm, "Stacker Crane 작업예약재료 생성");
				
				//1.2) Stacker Crane 작업예약  생성 
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScWbook", logId, methodNm, "Stacker Crane 작업예약  생성");
				
				//2) Stacker Crane 스케줄 Main 호출
				//크레인스케줄 전문 - Log ID, Method, 수정자 Set
				JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
				jrYdMsg.setField("YD_WBOOK_ID"       , scWbookId ); //야드작업예약ID
				jrYdMsg.setField("YD_SCH_CD"         , scSchCd   ); //야드스케쥴코드
				jrYdMsg.setField("YD_EQP_ID"         , scEqpId   ); //야드설비ID
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
				jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
				
				jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
			}				

			if ("Y".equals(carLdCmplYn)||"Y".equals(carUdCmplYn)) {  // 상하차 완료
				// 차량출발 처리	
				if(!TransOrdDate.equals("")) {      // 구내운송은 자동 출발처리 안함
					commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
					JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("CAR_NO", 				CarNo);			
					recInTemp.setField("SPOS_WLOC_CD", 			WlocCd);
					recInTemp.setField("SPOS_YD_PNT_CD", 		ydPndCd);
					recInTemp.setField("TRANS_ORD_DATE", 		TransOrdDate);
					recInTemp.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
					
					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
				}
			}	
			
			/**********************************************************
			* 8. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 8.1 
			*   - 크레인스케줄 재료를 대상
			*   - 권하실적위치로 저장위치 수정
			*   - 권상실적위치가 차량이면 현재진도코드 수정
			* 8.2 저장품 수정
			*   - 작업예약 재료를 대상
			*   - 작업예약이 삭제되었으면 작업예약ID, 스케줄코드 삭제
			*   - 현재진도코드가 저장품과 다르면 관련 항목(산적LotType 등) 수정
			*   - 저장위치가 저장품과 다르면 저장위치 수정
			* 8.2.1
			*   - 봉강자동창고 KATY 입고 스케줄 대상 (자동창고지만 백업처리기능 때문에 여기도 추가)
			*     - 입고예정위치를 봉강일반창고(KA01)로 수정
			*   - 소단중 제품(1500키로 미만)에 대상 한정 
			*     - 입고예정위치를 봉강일반창고(KB01)로 수정 
			*   - 봉강일반창고 KBTY 입고 대상 입고예정위치를 봉강일반창고(KB01)로 수정
			* 8.3 작업이력 등록
			*   - 크레인스케줄 재료를 대상
			* 8.4 차량상차 또는 하차완료 시 차량이송재료 대상으로
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 8.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			*     - 상차 : 소재이송일시
			*     - 하차 : 소재인수일시
			**********************************************************/
			//BUNDLE공통 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006BundleComm", logId, methodNm, "BUNDLE공통 수정");

			//저장품 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StockByBundle", logId, methodNm, "저장품 수정");
			
			commUtils.printLog(logId, "비오비오비오20200907" +
										", 권상:"+ ydUpWrLoc +
										", 권하:"+ ydDnWrLoc +
										", 권하단:"+ ydDnWrLayer +
										", 스케줄CD:"+ ydSchCd +
										", 스케줄ID:"+ ydCrnSchId
										, "SL");
			
			// 8.2.1 봉강자동창고 TY 입고 및 반납 소단중 제품 (중량 1500kg 미만) 입고예정위치를 봉강일반창고(KB01)로 수정
			if(( "L".equals(ydSchCd.substring(6,7)) || "B".equals(ydSchCd.substring(6,7)) ) && ydDnWrLoc.startsWith("KATY")){
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				jrParam.setField("YD_RCPT_PLN_STR_LOC",  "KA01");
				jrParam.setField("YD_RCPT_PLN_STR_LOC2", "KB01");
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNXYSL006YdRcptPlnStrLoc", logId, methodNm, "KAKY베드 권하시 입고예정위치 수정");
				commUtils.printLog(logId, "KATY -> KA01 or KB01 입고예정위치 변경 완료", "SL");
			}
			// 8.2.1 봉강일반창고 KBTY 입고 및 반납 대상 입고예정위치를 봉강일반창고(KB01)로 수정
			else if(( "L".equals(ydSchCd.substring(6,7)) || "B".equals(ydSchCd.substring(6,7)) ) && ydDnWrLoc.startsWith("KBTY")){
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				jrParam.setField("YD_RCPT_PLN_STR_LOC",  "KB01");
				jrParam.setField("YD_RCPT_PLN_STR_LOC2", "KB01");
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNXYSL006YdRcptPlnStrLoc", logId, methodNm, "KBTY베드 권하시 입고예정위치 수정");
				commUtils.printLog(logId, "KBTY -> KA01 입고예정위치 변경 완료", "SL");
			}
			else{
				commUtils.printLog(logId, "입고예정위치 변경 안함", "SL");
			}
			
			//작업이력 등록
			jrParam.setField("YD_CAR_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CAR_SCH_ID" ))); 
			jrParam.setField("YD_TCAR_SCH_ID"  , commUtils.trim(jrParam.getFieldString("YD_TCAR_SCH_ID" ))); 
			jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CRN_SCH_ID" ))); 
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHist", logId, methodNm, "작업이력 등록");

			/**********************************************************
			* 9. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송(YSNxL002)
			**********************************************************/
			if (chgDnWrLayer) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN4L002DnWr", jrParam));
			}

			// 제품창고(K) B동인 경우 최하단(01단) 권하시 Bed 활성상태 변경
			//if (ydDnWrLoc.matches("[K][B]\\d\\d\\d\\d\\d\\d") && "01".equals(ydDnWrLayer)) {
			
			// 제품창고(K) B동 일반야드에 권하시 Bed 활성상태 변경 (해당 bed에 제품이 있어도 권하시 update. 해당 bed에 같은 길이 구분의 제품이 적치되어 있다는 전제) 
			if (ydDnWrLoc.matches("[K][B]\\d\\d\\d\\d\\d\\d")) { 	
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
				jrParam.setField("YS_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
				jrParam.setField("YS_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkBedActStat", logId, methodNm, "적치Bed 활성상태 변경");
			}
			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치

			//KBC1에 적치시, 적치와 동시에 해당 야드맵 클리어
			if(ydDnWrLoc.startsWith("KBC1")){
				jrParam3.setField("YS_STK_COL_GP", "KBC1");
				commDao.update(jrParam3, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsStklyrSt2", logId, methodNm, "KBC1 적치단 전체 Clear ");
			}
			
			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				resMsg.setField("MODIFIER"      , modifier  ); //수정자
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN4L004(resMsg));
			}
			

			
			
			/**********************************************************
			* 11. 크레인작업지시요구 전문 호출(NxYDL004)
			**********************************************************/
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name

			jrYdMsg.setField("JMS_TC_CD", msgId.substring(0, 2) + "YSL004"); //JMSTC코드
				
			jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
			jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
			jrYdMsg.setField("MODIFIER"        , modifier  ); //수정자
			
			//크레인작업지시 전문을 추가
			jrRtn = commUtils.addSndData(jrRtn, this.rcvN4YSL004(jrYdMsg));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN4L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재자동창고 크레인권하실적(N5YSL006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN5YSL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고 크레인권하실적[GdsYsL2RcvSeEJB.rcvN5YSL006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LAYER"  )); //야드권하실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydUpWrLoc     = ""; //야드권상실적위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			String ydRcptLoc     = ""; 	//반입시 필요
			
			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)
			if ("4".equals(ydWrkProgStat)) {
				resMsg.setField("YD_L2_WR_GP", "D"); //야드L2실적구분(권하실적)
			} else {
				resMsg.setField("YD_L2_WR_GP", "F"); //야드L2실적구분(강제권하)
			}
			
			//PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "선재자동창고 크레인권하실적", "APPPI0", "K", "*");
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydDnWrLoc)) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";
			} else if ("XX".equals(ydDnWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 이상";
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
			jrParam2.setResultCode(logId);	//Log ID
			jrParam2.setResultMsg(methodNm);	//Log Method Name
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("MODIFIER"     , modifier  ); //수정자

			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch", logId, methodNm, "크레인스케줄상태 조회");
			

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:크레인스케쥴ID DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydSchCd         = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
				ydUpWrLoc       = commUtils.trim(jrChk.getFieldString("YS_UP_WR_LOC"    )); //야드권상실적위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				ydRcptLoc     	= commUtils.trim(jrChk.getFieldString("YD_RCPT_PLN_STR_LOC"       )); //입고예정위치
				
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) {
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//조회 Parameter
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrParam.setField("YD_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			//설비
			jrParam.setField("YD_EQP_ID"  , ydEqpId); //야드설비ID(크레인)
			jrParam.setField("YD_EQP_STAT", "4"    ); //야드설비상태(권하완료)
			
			//실제 야드권하실적단 및 기타 정보 조회
			String wbCmplYn = ""; //작업예약완료여부
			boolean chgDnWrLayer = false; //권하위치 적치단 변경여부

			jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006Curr", logId, methodNm, "현재정보  조회");
			
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				wbCmplYn = commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));

				commUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				if (!ydDnWrLayer.equals(tbDnWrLayer)) {
					commUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
					ydCrnZaxis   = "";
				}
				
				if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
					ydCrnXaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
					ydCrnYaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
					ydCrnZaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
				}
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:권하위치 DB정보 없음"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;
			
			if ("0".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "B"; //Backup
			}
			
			String currDt = commUtils.getDateTime14(); //현재시각
			/**********************************************************
			* 3. 전문 전송
			**********************************************************/
			
			//크레인스케쥴
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      ); //야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp); //야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis  ); //야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis  ); //야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); //야드권하실적Z축
			jrParam.setField("WR_DT"           , currDt      ); //실적일시
			jrParam.setField("UP_DN_GP"        , "D"         ); //권상권하구분(권하)

			
			/**********************************************************
			* 4. 권하실적위치가 대차(상차)
			* 4.1 대차 상차 정보 등록
			*   - 작업예약 야드작업계획대차 수정
			*   - 대차이송재료 등록
			* 4.2 대차 하차스케줄 생성
			*   - 작업예약 등록
			*   - 작업예약재료 등록
			* 4.3 대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
			* 4.4 L2 전송 전문
			**********************************************************/
			
			/**********************************************************
			* 5. 권하실적위치가 차량(상차)
			* 5.1 차량이송재료 등록
			* 5.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 마지막 크레인스케줄 이면
			* 5.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 5.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 5.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 5.4 야드차량사용구분이 출하차량(G)
			* 5.4.1 출하관리 일품출하상차실적(YSDSJ007) 전송
			* 5.4.2 출하관리 출하상차완료(YSDSJ008) 전송
			*     - 상차완료(마지막 크레인스케줄)이면
			**********************************************************/
			//차량상차완료여부(소재이송지시 수정 및 공통Table 소재이송일시 수정)
			String carLdCmplYn 		= "N";
			String ydCarUseGp  		= "";
			String ydCarSchId  		= "";
			String CarNo  			= "";
			String TransOrdDate 	= "";
			String TransOrdSeqNo	= "";
			String WlocCd  			= "";
			String ydPndCd  		= "";
			String bayCarLdCmplYn 	= "N";  // 해당동 상차완료
			String drvTelNo  		= "";   // 기사전화번호
			
			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				//상차 차량스케줄  조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId     ); 
				recPara.setField("YS_STK_COL_GP" , ydDnWrLoc.substring(0, 6) ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd
				--크레인권하실적 상차 차량스케줄 조회 
				SELECT YD_CAR_SCH_ID
				     , YD_CAR_USE_GP 
				     , CAR_NO
				     , TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO
				     , YS_STK_COL_GP
				     , WLOC_CD
				     , YD_PNT_CD
				     , STOCK_CNT
				     , LYR_CNT
				     , CRNMTL_CNT
				     , CASE WHEN STOCK_CNT <= LYR_CNT + CRNMTL_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN
				  FROM
				       (
				        SELECT TS.YD_CAR_SCH_ID
				              ,TS.YD_CAR_USE_GP
				              ,TS.CAR_NO
				              ,TS.TRANS_ORD_DATE
				              ,TS.TRANS_ORD_SEQNO
				              ,SC.YS_STK_COL_GP
				              ,SC.WLOC_CD
				              ,SC.YD_PNT_CD
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_STOCK
				                 WHERE TRANS_ORD_DATE = TS.TRANS_ORD_DATE
				                   AND TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				               ) AS STOCK_CNT
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_STKLYR
				                 WHERE YS_STK_COL_GP = SC.YS_STK_COL_GP
				                   AND SSTL_NO IS NOT NULL 
				                   AND YD_STK_LYR_MTL_STAT = 'C')  AS LYR_CNT
				              ,(SELECT COUNT(*)
				                  FROM TB_YS_CRNWRKMTL
				                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                   AND DEL_YN        = 'N' ) AS CRNMTL_CNT
				          FROM TB_YS_STKCOL SC
				              ,TB_YS_CARSCH TS
				         WHERE SC.YS_STK_COL_GP = :V_YS_STK_COL_GP
				           AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				           AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				             OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				           AND TS.DEL_YN = 'N'
				        )
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd", logId, methodNm, "상차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					CarNo 			= commUtils.trim(jrChk.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrChk.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrChk.getFieldString("YD_PNT_CD")); 
					ydCarSchId  	= commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					ydCarUseGp 		= commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); //야드차량사용구분
					carLdCmplYn 	= commUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); //차량상차완료여부
					bayCarLdCmplYn 	= commUtils.trim(jrChk.getFieldString("CAR_BAY_LD_CMPL_YN")); //해당동 차량상차완료여부
					drvTelNo 		= commUtils.trim(jrChk.getFieldString("DEST_TEL_NO")); //해당동 차량상차완료여부
					
					//차량이송재료(TB_YS_CARFTMVMTL) 상차 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
					recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
					recPara.setField("MODIFIER" 	 , modifier     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarMtlIns 
					--크레인권하실적 차량이송재료 등록 
					MERGE INTO TB_YS_CARFTMVMTL TM USING (
					SELECT :V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
					      ,CM.SSTL_NO
					      ,:V_MODIFIER      AS MODIFIER
					      ,SYSDATE          AS MOD_DDTT
					      ,'N'              AS DEL_YN
					      ,'01'             AS YS_STK_BED_NO
					      ,TO_CHAR((SELECT COUNT(*)
					                  FROM TB_YS_CARFTMVMTL
					                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					                   AND DEL_YN         = 'N') + TO_NUMBER(CM.YS_STK_LYR_NO),'FM00') AS YS_STK_LYR_NO
					      ,ST.HCR_GP
					      ,ST.STL_PROG_CD
					  FROM TB_YS_CRNWRKMTL CM
					      ,TB_YS_STOCK     ST
					 WHERE CM.SSTL_NO       = ST.SSTL_NO
					   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND CM.DEL_YN        = 'N'
					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO)
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_CAR_SCH_ID, TM.SSTL_NO  , TM.REGISTER   , TM.REG_DDTT     ,
					        TM.MODIFIER     , TM.MOD_DDTT, TM.DEL_YN     , TM.YS_STK_BED_NO,
					        TM.YS_STK_LYR_NO, TM.HCR_GP  , TM.STL_PROG_CD)
					VALUES (DD.YD_CAR_SCH_ID, DD.SSTL_NO  , DD.MODIFIER   , DD.MOD_DDTT     ,
					        DD.MODIFIER     , DD.MOD_DDTT, DD.DEL_YN     , DD.YS_STK_BED_NO,
					        DD.YS_STK_LYR_NO, DD.HCR_GP  , DD.STL_PROG_CD)
					*/    	
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarMtlIns", logId, methodNm, " 상차 이송재료 등록 ");
					
					
					
					//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					recPara = JDTORecordFactory.getInstance().create();
					
					if ("Y".equals(carLdCmplYn)) {
						recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					} else {
						recPara.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}
					recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
					recPara.setField("YS_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
					recPara.setField("WR_DT" 			, currDt ); 
					recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
					recPara.setField("MODIFIER" 		, modifier     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd 
					--크레인권하실적 상차 차량스케줄 수정
					MERGE INTO TB_YS_CARSCH TS USING (
					SELECT TM.YD_CAR_SCH_ID
					      ,:V_YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
					      ,COUNT(*)            AS YD_EQP_WRK_SH
					      ,SUM(ST.YD_MTL_WT)   AS YD_EQP_WRK_WT
					      ,:V_YD_WBOOK_ID      AS YD_WBOOK_ID
					      ,:V_YS_STK_COL_GP    AS YD_CARLD_STOP_LOC
					      ,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
					 --     ,SF_SLAB_YD_ARR_WLOC_CD(MIN(ST.YD_AIM_YD_GP)) AS ARR_WLOC_CD
					     ,'XXXXX' AS ARR_WLOC_CD
					  FROM TB_YS_CARFTMVMTL TM
					      ,TB_YS_STOCK      ST
					 WHERE TM.SSTL_NO       = ST.SSTL_NO
					   AND TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND TM.DEL_YN        = 'N'
					 GROUP BY TM.YD_CAR_SCH_ID
					) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
					     TS.MODIFIER             = :V_MODIFIER
					    ,TS.MOD_DDTT             = SYSDATE
					    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
					    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
					    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH
					    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT
					    ,TS.ARR_WLOC_CD          = NVL(TS.ARR_WLOC_CD,DD.ARR_WLOC_CD)
					    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_WBOOK_ID
					    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
					    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,DD.WR_DT)
					    ,TS.YD_CARLD_CMPL_DT     = DECODE(DD.YD_CAR_PROG_STAT,'5',DD.WR_DT,NULL)
					*/    
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setResultCode(logId);	//Log ID
					recPara.setResultMsg(methodNm);	//Log Method Name
					recPara.setField("YD_CAR_SCH_ID", ydCarSchId); 
					recPara.setField("YD_CRN_SCH_ID", ydCrnSchId); 
					
					//출하차량(G)
					if ("G".equals(ydCarUseGp)) {

						//PIDEV
						//출하관리 일품출하상차실적						
//						if("Y".equals(sApplyYnPI)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1084", recPara));
//						} else {
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ007", recPara));
//						}
						
						//검수 등록
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
						recPara.setField("MODIFIER" , modifier     ); 
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006EXAMINATIONCHKLIST 
						MERGE INTO TB_YS_EXAMINATIONCHKLIST TM USING (
						SELECT 
						       ST.TRANS_ORD_DATE
						      , ST.TRANS_ORD_SEQNO  
						      , ST.SSTL_NO         
						      , SUBSTR(YS_STR_LOC,1,1) AS YD_GP     
						      , ST.CAR_NO        
						      , '' AS CARD_NO          
						      , '' AS CHECKING_YN     
						      , '' AS LABEL_YN  
						      , '' AS YD_AB_CD      
						      , '' AS YD_AB_CD2        
						      , '' AS GATE_NM         
						      , '' AS BAY_GP    
						      , '' AS YD_CARPNT_CD  
						      , '' AS YD_CAR_UPP_LOC_CD
						      , '' AS REGISTER        
						      , SYSDATE AS REG_DDTT  
						      , '' AS MODIFIER      
						      , SYSDATE AS MOD_DDTT 
						      , 'N' AS DEL_YN  
						  FROM TB_YS_CRNWRKMTL CM
						      ,TB_YS_STOCK     ST
						 WHERE CM.SSTL_NO       = ST.SSTL_NO
						   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND CM.DEL_YN        = 'N'
						) DD ON (TM.TRANS_ORD_DATE = DD.TRANS_ORD_DATE AND TM.TRANS_ORD_SEQNO = DD.TRANS_ORD_SEQNO AND TM.SSTL_NO = DD.SSTL_NO AND TM.YD_GP = DD.YD_GP)
						WHEN NOT MATCHED THEN
						INSERT (TM.TRANS_ORD_DATE, TM.TRANS_ORD_SEQNO  , TM.SSTL_NO         , TM.YD_GP     ,
						        TM.CAR_NO        , TM.CARD_NO          , TM.CHECKING_YN     , TM.LABEL_YN  ,
						        TM.YD_AB_CD      , TM.YD_AB_CD2        , TM.GATE_NM         , TM.BAY_GP    ,
						        TM.YD_CARPNT_CD  , TM.YD_CAR_UPP_LOC_CD, TM.REGISTER        , TM.REG_DDTT  , 
						        TM.MODIFIER      , TM.MOD_DDTT         , TM.DEL_YN                                        )
						VALUES (DD.TRANS_ORD_DATE, DD.TRANS_ORD_SEQNO  , DD.SSTL_NO         , DD.YD_GP     ,
						        DD.CAR_NO        , DD.CARD_NO          , DD.CHECKING_YN     , DD.LABEL_YN  ,
						        DD.YD_AB_CD      , DD.YD_AB_CD2        , DD.GATE_NM         , DD.BAY_GP    ,
						        DD.YD_CARPNT_CD  , DD.YD_CAR_UPP_LOC_CD, DD.REGISTER        , DD.REG_DDTT  , 
						        DD.MODIFIER      , DD.MOD_DDTT         , DD.DEL_YN                                        )
						 
						*/    	
						//commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006EXAMINATIONCHKLIST", logId, methodNm, "검수등록 ");
			
						//출하관리 출하상차완료
						if ("Y".equals(carLdCmplYn)) {

							//PIDEV							
//							if("Y".equals(sApplyYnPI)) {
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1094", recPara));
//							} else {
//								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ008", recPara));
//							}
							
//SJH 추가				//압연 조업으로 송신		
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSSBJ001CAR", recPara));														
						}						
//SJH16
						else {
							
						    // 출하상차 완료가 아니고 해당동 상차 완료 인 경우 ----> 복수동 처리 임
							// 차량출발처리
							if ("Y".equals(bayCarLdCmplYn)) { 
								
		    					commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
		    					JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
		    					recInTemp.setResultCode(logId);		//Log ID
		    					recInTemp.setResultMsg(methodNm);	//Log Method Name
		    					recInTemp.setField("CAR_NO", 			CarNo);			
		    					recInTemp.setField("SPOS_WLOC_CD", 		WlocCd);
		    					recInTemp.setField("SPOS_YD_PNT_CD",	ydPndCd);
		    					recInTemp.setField("TRANS_ORD_DATE",	TransOrdDate);
		    					recInTemp.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
		    					
		    					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
		    					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
		    					 
		    					jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
		    					
	    	    				commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 복수동 나머지 출하Lot EJB 호출", "SL");		    	    				
		    					
		    					//복수동 나머지 출하Lot
		    					recInTemp			= JDTORecordFactory.getInstance().create();
		    					
		    					//PIDEV
//		    					if("Y".equals(sApplyYnPI)) {
		    						
			    					recInTemp.setField("MQ_TC_CD"			, "M10LMYDJ1044");
			    					recInTemp.setField("MQ_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			    					recInTemp.setField("YD_GP",     		ydEqpId.substring(0, 1));
			    					recInTemp.setField("CAR_NO", 			CarNo);
			    					recInTemp.setField("TRN_REQ_DATE", 		TransOrdDate);
			    					recInTemp.setField("TRN_REQ_SEQ"   , 	TransOrdSeqNo);
			    					recInTemp.setField("DOUBLEDONG_CHECK", 	"Y");				//복수동 나머지 출하여부
			    					recInTemp.setField("YD_CAR_SCH_ID",     ydCarSchId);
			    					recInTemp.setField("CMBN_CARLD_YN",     "E"); 				//첫번째 도착창고 : S 두번째 도착창고 : E
			    					recInTemp.setField("WAIT_ARR_DDTT",     currDt);
			    					recInTemp.setField("WAIT_ARR_GP",     	"B");				//대기장도착구분  - B:BACKUP , S:SMARTPHONE
			    					recInTemp.setField("TEL_NO",     		drvTelNo);			//기사 전화번호
			    					recInTemp.setField("YD_SND_YN",     	"Y");			    //mq야드에서 재송신 
			    					
//		    					} else {
//		    						
//			    					recInTemp.setField("JMS_TC_CD"			, "DSYSJ005");
//			    					recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
//			    					recInTemp.setField("YD_GP",     		ydEqpId.substring(0, 1));
//			    					recInTemp.setField("CAR_NO", 			CarNo);
//			    					recInTemp.setField("TRANS_ORD_DT", 		TransOrdDate);
//			    					recInTemp.setField("TRANS_ORD_SEQNO", 	TransOrdSeqNo);
//			    					recInTemp.setField("DOUBLEDONG_CHECK", 	"Y");				//복수동 나머지 출하여부
//			    					recInTemp.setField("YD_CAR_SCH_ID",     ydCarSchId);
//			    					
//			    					recInTemp.setField("CMBN_CARLD_YN",     "E"); 				//첫번째 도착창고 : S 두번째 도착창고 : E
//			    					recInTemp.setField("WAIT_ARR_DDTT",     currDt);
//			    					recInTemp.setField("WAIT_ARR_GP",     	"B");				//대기장도착구분  - B:BACKUP , S:SMARTPHONE
//			    					recInTemp.setField("TEL_NO",     		drvTelNo);			//기사 전화번호 
//			    					
//		    					}
		    					
	    	    				jrRtn = commUtils.addSndData(jrRtn, recInTemp);
							}
						}	
					}
					if ("L".equals(ydCarUseGp)) {
						//구내운송 상차완료
						if ("Y".equals(carLdCmplYn)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ008", recPara));
						}						
					}
				}
			}			
			/**********************************************************
			* 6. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 6.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 6.4 구내운송 소재차량하차완료 송신(YSTSJ010) 전송
			* 6.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 송신)
			String carUdCmplYn = "N";

			if ("TR".equals(ydUpWrLoc.substring(2, 4)) && !"TR".equals(ydDnWrLoc.substring(2, 4))) {
				//야드하차작업예약ID 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd 
				--크레인권하실적 하차 차량스케줄 조회
				SELECT TS.YD_CAR_SCH_ID
				      ,DECODE(COUNT(TM.SSTL_NO),0,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
				      ,TS.CAR_NO
				      ,TS.ARR_WLOC_CD AS WLOC_CD
				      ,TS.YD_PNT_CD3  AS YD_PNT_CD
				      ,TS.TRANS_ORD_DATE
				      ,TS.TRANS_ORD_SEQNO
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID      ,TS.CAR_NO
				      ,TS.YD_CARUD_STOP_LOC
				      ,TS.YD_PNT_CD3
				      ,TS.TRANS_ORD_DATE
				      ,TS.TRANS_ORD_SEQNO
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					CarNo 			= commUtils.trim(jrChk.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrChk.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrChk.getFieldString("YD_PNT_CD")); 				
					
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					carUdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					
					//차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("MODIFIER" , modifier     ); 
						recPara.setField("WR_DT" 			, currDt ); 
						recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd 
						--크레인권하실적 하차 차량스케줄 수정 
						UPDATE TB_YS_CARSCH
						   SET MODIFIER         = :V_MODIFIER
							  ,MOD_DDTT         = SYSDATE
						      ,YD_EQP_WRK_STAT  = 'U' --공차
						      ,YD_CAR_PROG_STAT = 'E' --하차완료
						      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
						 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
						   AND DEL_YN           = 'N'
						*/    
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 수정  ");

						
						//구내운송 소재차량하차완료
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ010", recPara));
					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					commUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				}
			}
			/**********************************************************
			* 7. 설비, 적치단 , 크레인스케쥴, 저장품, 작업예약재료, 작업예약 수정
			* 7.1 설비 야드설비상태(권하완료) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 삭제
			*   - 권하위치 재료정보 수정
			*   - 권하위치외 같은 재료번호로 등록된 적치단 수정(권하분리 재료 제외)
			* 7.3 크레인스케쥴
			*   - 크레인작업재료 삭제
			*   - 크레인스케쥴 권하실적 수정 및 삭제
			* 7.4 작업예약 마지막 크레인스케쥴 이면
			*   - 작업예약재료 삭제
			*   - 작업예약 수정 및 삭제
			**********************************************************/
			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			
			//이전 권하위치 단을 Clear 한다.
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("YS_STK_COL_GP", "K%"); 
			jrParam2.setField("YS_STK_BED_NO", "%"); 
			commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtl", logId, methodNm, "적치단 작업Clear ");
			
			//적치단(크레인 및 권하위치) 수정
			if("SC".equals(ydEqpId.substring(2,4)) && "CS".equals(ydDnWrLoc.substring(2,4))) {
				//Stacker Crane 이고 권하실적위치가 출고 SKID 이면 
				// --> 권하실적 위치를 출측 Home Position 으로 변경한다.
				// --> SC01 => HS21, SC02 => HS22, SC03 => HS23 .. , SC06 => HS26
				
				ydDnWrLoc = ydEqpId.substring(0,2) + "HS2"+ ydEqpId.substring(5,6) + "01";
				
				jrParam.setField("YD_DN_WR_LOC" , ydDnWrLoc   ); //야드권하실적위치
				jrParam.setField("YD_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
				jrParam.setField("YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			}

			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				// 선재 차량권하시 별도
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyrTr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			} else {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			}
			//크레인작업재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnMtl", logId, methodNm, "크레인작업재료 삭제");
			//크레인스케쥴 권하실적 수정 및 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnSch", logId, methodNm, "크레인스케쥴 권하실적 수정 및 삭제");

			//작업예약완료 이면
			if ("Y".equals(wbCmplYn)) {
				//작업예약재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDel", logId, methodNm, "작업예약재료 삭제");
				//작업예약 수정 및 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbDel", logId, methodNm, "작업예약 수정 및 삭제");
			} else {
				//크레인작업재료번호로 작업예약재료 삭제 (작업예약재료에 작업이 완료된 재료는 DEL_YN='Y' 함으로써 스케줄 취소 후 재 작업시 작업대상에서 제외시킨다.) 
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDelBySchId", logId, methodNm, "크레인권하실적 권하완료된 작업예약재료만 삭제");
			}
			
			/**********************************************************
			* 7.5 작업예약 마지막 크레인스케쥴 이면
			*   - 입고스케줄이면 YSDSJ001 (입고작업실적) 송신
			*   - 이적스케줄이면 YSDSJ002 (입고작업실적) 송신
			*   - 반납스케줄이면 YSDSJ011 (반납확인정보) 송신
			**********************************************************/
			if ("Y".equals(wbCmplYn)) {
				
				if(ydSchCd.substring(6, 7).equals("L") 
				   && ydDnWrLoc.matches("[K][A-E]\\d\\d\\d\\d\\d\\d")) {
					//입고 스케줄이고 권하위치가 야드이고
					//진도코드가 입고대기(H), 종합판정대기(G) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001BySchId", jrParam));
				}
				else
				if(ydSchCd.substring(6, 7).equals("C")
					&& ydDnWrLoc.matches("[K][A-E]CS\\d\\d\\d\\d")) {
					//반입 스케줄이고 권하위치가 출고SKID 이면 Stacker Crane 반입스케줄을 생성한다.
					//1) Stacker Crane 작업예약등록
					//2) Stacker Crane 스케줄 Main 호출
					
					String scWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //stacker crane 용 작업예약ID
					String scSchCd = "";
					String scEqpId = "";
					String scEqpNo = "";
					
					//입고예정 위치가 있어야 함 
					if(ydRcptLoc.length()> 6){
						if (ydRcptLoc.substring(4,6).equals("01") ||ydRcptLoc.substring(4,6).equals("02")){
							scEqpNo = "01";
						}else if (ydRcptLoc.substring(4,6).equals("03") ||ydRcptLoc.substring(4,6).equals("04")){
							scEqpNo = "02";
						}else if (ydRcptLoc.substring(4,6).equals("05") ||ydRcptLoc.substring(4,6).equals("06")){
							scEqpNo = "03";
						}else if (ydRcptLoc.substring(4,6).equals("07") ||ydRcptLoc.substring(4,6).equals("08")){
							scEqpNo = "04";
						}else if (ydRcptLoc.substring(4,6).equals("09") ||ydRcptLoc.substring(4,6).equals("09")){
							scEqpNo = "05";
						}else if (ydRcptLoc.substring(4,6).equals("10") ||ydRcptLoc.substring(4,6).equals("11")){
							scEqpNo = "06";
								
						}
						scSchCd = ydSchCd.substring(0,2) + "HS" + scEqpNo + "CM"; //반입SCH
						scEqpId = ydSchCd.substring(0,2) + "SC" + scEqpNo; //stacker crane 장비코드 (ex:KASC01)
					} else {
						
					}
					jrParam.setField("SC_YD_WBOOK_ID", scWbookId);  //신규 stacker crane 작업예약ID
					jrParam.setField("OHC_YD_WBOOK_ID", ydWbookId); //완료된 OHC 크래인 작업예약ID
					jrParam.setField("SC_SCH_CD", scSchCd); //신규 stcker crane 스케줄 코드
					jrParam.setField("SC_EQP_ID", scEqpId); //신규 stcker crane 설비ID
					jrParam.setField("YS_DN_WR_LOC", ydDnWrLoc); // 권하실적 위치 (From 위치)
					
					//1.1) Stacker Crane 작업예약재료 생성 
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScWbMtl", logId, methodNm, "Stacker Crane 작업예약재료 생성");
					
					//1.2) Stacker Crane 작업예약  생성 
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScWbook", logId, methodNm, "Stacker Crane 작업예약  생성");
					
					//2) Stacker Crane 스케줄 Main 호출
					//크레인스케줄 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
					
					jrYdMsg.setField("JMS_TC_CD", "YSYSJ402");
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
					jrYdMsg.setField("YD_WBOOK_ID"       , scWbookId ); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD"         , scSchCd   ); //야드스케쥴코드
					jrYdMsg.setField("YD_EQP_ID"         , scEqpId   ); //야드설비ID
					jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
					
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}				
				else
				if(ydDnWrLoc.substring(2, 4).equals("TY")) {					//반납 스케줄이고 
					//진도코드가 반납대기(J) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ011BySchId", jrParam));
//SJH 추가			//압연 조업으로 송신		
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSSBJ001", jrParam));					
				}
				
			}
// 더미작업도 송신 되어 야 함
			if(ydSchCd.substring(6, 7).equals("M") 
			   && ydDnWrLoc.matches("[K][A-E]\\d\\d\\d\\d\\d\\d")) {
				//이적 스케줄이고 권하위치가 야드이고
				//진도코드가 입고대기(H), 종합판정대기(G), 판정보류(F) 가 아닌경우 (진도코드 판단은 쿼리에서 한다.)
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				//PIDEV				
//				if("Y".equals(sApplyYnPI)) {
					//제품이적 실적 
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1034BySchId", jrParam));				
//				} else {
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ002BySchId", jrParam));
//				}
			}
			
			if ("Y".equals(carLdCmplYn)||"Y".equals(carUdCmplYn)) {  // 상하차 완료
				if(!TransOrdDate.equals("")) {      // 구내운송은 자동 출발처리 안함
					// 차량출발 처리	
					commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
					JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("CAR_NO", 				CarNo);			
					recInTemp.setField("SPOS_WLOC_CD", 			WlocCd);
					recInTemp.setField("SPOS_YD_PNT_CD", 		ydPndCd);
					recInTemp.setField("TRANS_ORD_DATE", 		TransOrdDate);
					recInTemp.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
					
					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
				}	
			}	
			/**********************************************************
			* 8. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 8.1 크레인스케줄 재료를 대상
			*   - 권하실적위치로 저장위치 수정
			*   - 권상실적위치가 차량이면 현재진도코드 수정
			* 8.2 저장품 수정
			*   - 작업예약 재료를 대상
			*   - 작업예약이 삭제되었으면 작업예약ID, 스케줄코드 삭제
			*   - 현재진도코드가 저장품과 다르면 관련 항목(산적LotType 등) 수정
			*   - 저장위치가 저장품과 다르면 저장위치 수정
			* 8.3 작업이력 등록
			*   - 크레인스케줄 재료를 대상
			* 8.4 차량상차 또는 하차완료 시 차량이송재료 대상으로
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 8.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			**********************************************************/
			//BUNDLE공통 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006BundleComm", logId, methodNm, "BUNDLE공통 수정");

			//저장품 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StockByBundle", logId, methodNm, "저장품 수정");
			
			//작업이력 등록
			jrParam.setField("YD_CAR_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CAR_SCH_ID" ))); 
			jrParam.setField("YD_TCAR_SCH_ID"  , commUtils.trim(jrParam.getFieldString("YD_TCAR_SCH_ID" ))); 
			jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CRN_SCH_ID" ))); 
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHist", logId, methodNm, "작업이력 등록");

			
			/**********************************************************
			* 9. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송(YSNxL002)
			**********************************************************/
			if (chgDnWrLayer) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L002DnWr", jrParam));
			}
			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치

			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN5L004(resMsg));
			}
			
			/**********************************************************
			* 11. 크레인작업지시요구 전문 호출(NxYDL004)
			**********************************************************/
			if(!ydEqpId.substring(2,4).equals("SC")) {

				/**********************************************************
				* 11. 크레인작업지시요구 전문 호출(NxYDL004)
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
				jrYdMsg.setField("JMS_TC_CD", msgId.substring(0, 2) + "YSL004"); //JMSTC코드
					
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   ); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
				jrYdMsg.setField("MODIFIER"        , modifier  ); //수정자
				
				//크레인작업지시 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvN5YSL004(jrYdMsg));
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN5L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 봉강자동화창고 - 크레인권하실적(N6YSL006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN6YSL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동화창고 - 크레인권하실적[GdsYsL2RcvSeEJB.rcvN6YSL006] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord resMsg = JDTORecordFactory.getInstance().create(); //크레인작업실적응답 전문 생성용
		boolean resYn = true;	//크레인작업실적응답 전문 전송여부

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"       )); //야드설비ID
			String ydEqpWrkMode  = commUtils.trim(rcvMsg.getFieldString("YD_EQP_WRK_MODE" )); //야드설비작업Mode(0:Manual, 1:Auto, 9:Backup)
			String ydWrkProgStat = commUtils.trim(rcvMsg.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태(4:권하완료, 5:강제권하)
			String ydSchCd       = commUtils.trim(rcvMsg.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
			String ydCrnSchId    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"   )); //야드크레인스케쥴ID
			String ydDnWrLoc     = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LOC"    )); //야드권하실적위치
			String ydDnWrLayer   = commUtils.trim(rcvMsg.getFieldString("YS_DN_WR_LAYER"  )); //야드권하실적단
			String ydCrnXaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_XAXIS"    )); //야드크레인X축
			String ydCrnYaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_YAXIS"    )); //야드크레인Y축
			String ydCrnZaxis    = commUtils.trim(rcvMsg.getFieldString("YD_CRN_ZAXIS"    )); //야드크레인Z축			
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"        )); //수정자(Backup Only)
			String ydWbookId     = ""; //야드작업예약ID
			String ydUpWrLoc     = ""; //야드권상실적위치
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			JDTORecord recPara = JDTORecordFactory.getInstance().create();
			JDTORecord jrRtn = null;	//전문 Return
			String ydL3HdRsCd = "";		//야드L3처리결과코드
			String ydL3Msg    = ""; 	//야드L3MESSAGE

			//크레인작업실적응답 전문 생성용
			resMsg.setResultCode(logId);	//Log ID
			resMsg.setResultMsg(methodNm);	//Log Method Name
			resMsg.setField("YD_EQP_ID"       , ydEqpId      ); //야드설비ID
			resMsg.setField("YD_WRK_PROG_STAT", ydWrkProgStat); //야드작업진행상태
			resMsg.setField("YD_SCH_CD"       , ydSchCd      ); //야드스케쥴코드
			resMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId   ); //야드크레인스케쥴ID
			resMsg.setField("YD_L3_HD_RS_CD"  , "DN99"       ); //야드L3처리결과코드(Error)
			resMsg.setField("YD_L3_MSG"       , "오류:권하실적 수신처리"); //야드L3MESSAGE(Error)
			if ("4".equals(ydWrkProgStat)) {
				resMsg.setField("YD_L2_WR_GP", "D"); //야드L2실적구분(권하실적)
			} else {
				resMsg.setField("YD_L2_WR_GP", "F"); //야드L2실적구분(강제권하)
			}

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "DN01";
				ydL3Msg    = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "DN02";
				ydL3Msg    = "오류:설비ID[" + ydEqpId + "] 이상";
			} else if ("".equals(ydCrnSchId)) {
				ydL3HdRsCd = "DN03";
				ydL3Msg    = "오류:크레인스케쥴ID 없음";
			} else if ("".equals(ydDnWrLoc)) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 없음";
			} else if ("XX".equals(ydDnWrLoc.substring(0, 2))) {
				ydL3HdRsCd = "DN04";
				ydL3Msg    = "오류:권하실적위치 이상";				
			}

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "봉강자동화창고 - 크레인권하실적", "APPPI0", "K", "*");			
			
			/**********************************************************
			* 2. 크레인스케쥴ID Check
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam.setField("MODIFIER"     , modifier  ); //수정자

			JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
			jrParam2.setResultCode(logId);	//Log ID
			jrParam2.setResultMsg(methodNm);	//Log Method Name
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("MODIFIER"     , modifier  ); //수정자
			
			JDTORecord jrChk = null;
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch", logId, methodNm, "크레인스케줄상태 조회");
			

			if (jsChk.size() == 0) {
				//크레인스케쥴 Table 존재유무 Check
				ydL3HdRsCd = "DN11";
				ydL3Msg = "오류:크레인스케쥴ID DB정보 없음";
			} else {
				//크레인스케쥴 Table 야드작업진행상태 Check
				jrChk = jsChk.getRecord(0);
				ydWbookId       = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"     )); //야드작업예약ID
				ydSchCd         = commUtils.trim(jrChk.getFieldString("YD_SCH_CD"       )); //야드스케쥴코드
				ydUpWrLoc       = commUtils.trim(jrChk.getFieldString("YS_UP_WR_LOC"    )); //야드권상실적위치
				String tmpStat  = commUtils.trim(jrChk.getFieldString("YD_WRK_PROG_STAT")); //야드작업진행상태
				String tmpEqpId = commUtils.trim(jrChk.getFieldString("YD_EQP_ID"       )); //야드설비ID
				if (!"2".equals(tmpStat) && !"3".equals(tmpStat)) {
					ydL3HdRsCd = "DN12";
					ydL3Msg = "오류:현재 작업진행상태[" + tmpStat + "] 이상";
				} else if (!ydEqpId.equals(tmpEqpId)) {
					ydL3HdRsCd = "DN13";
					ydL3Msg = "오류:현재 설비ID와[" + tmpEqpId + "] 다름";
				}
			}
			
			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//조회 Parameter
			jrParam.setField("YD_WBOOK_ID"  , ydWbookId); //야드작업예약ID
			jrParam.setField("YD_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
			jrParam.setField("YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			//설비
			jrParam.setField("YD_EQP_ID"  , ydEqpId); //야드설비ID(크레인)
			jrParam.setField("YD_EQP_STAT", "4"    ); //야드설비상태(권하완료)
			
			//실제 야드권하실적단 및 기타 정보 조회
			String wbCmplYn = ""; //작업예약완료여부
			boolean chgDnWrLayer = false; //권하위치 적치단 변경여부
			
			if(ydUpWrLoc.equals(ydDnWrLoc) &&( ydDnWrLoc.startsWith("KATY") || ydDnWrLoc.startsWith("KBTY"))){
				// TYMultiLyr 동일베드 권상권하
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CurrTYmultiLyrDummy", logId, methodNm, "TYMultiLyrDummy 현재정보 조회");	
			} else if(ydDnWrLoc.startsWith("KATY") || ydDnWrLoc.startsWith("KBTY")){
				// TYMultiLyr
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CurrTYmultiLyr", logId, methodNm, "TYMultiLyr 현재정보  조회");	
			} else {
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006Curr", logId, methodNm, "현재정보  조회");
			}
			
			if (jsChk.size() > 0) {
				jrChk = jsChk.getRecord(0);
				wbCmplYn = commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));

				commUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				if (!ydDnWrLayer.equals(tbDnWrLayer)) {
					commUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
					ydCrnZaxis   = "";
				}
				
				if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
					ydCrnXaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
					ydCrnYaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
					ydCrnZaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
				}
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:권하위치 DB정보 없음"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}

			//야드권하작업수행구분
			String ydDnWrkActGp = ydEqpWrkMode;
			
			if ("0".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "M"; //Manual
			} else if("1".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "A"; //Auto
			} else if("9".equals(ydEqpWrkMode)) {
				ydDnWrkActGp = "B"; //Backup
			}
			
			String currDt = commUtils.getDateTime14(); //현재시각
			/**********************************************************
			* 3. 전문 전송
			**********************************************************/
			
			//크레인스케쥴
			jrParam.setField("YD_DN_CMPL_DT"   , currDt      ); //야드권하완료일시
			jrParam.setField("YD_DN_WR_LOC"    , ydDnWrLoc   ); //야드권하실적위치
			jrParam.setField("YD_DN_WR_LAYER"  , ydDnWrLayer ); //야드권하실적단
			jrParam.setField("YD_DN_WRK_ACT_GP", ydDnWrkActGp); //야드권하작업수행구분
			jrParam.setField("YD_DN_WR_XAXIS"  , ydCrnXaxis  ); //야드권하실적X축
			jrParam.setField("YD_DN_WR_YAXIS"  , ydCrnYaxis  ); //야드권하실적Y축
			jrParam.setField("YD_DN_WR_ZAXIS"  , ydCrnZaxis  ); //야드권하실적Z축
			jrParam.setField("WR_DT"           , currDt      ); //실적일시
			jrParam.setField("UP_DN_GP"        , "D"         ); //권상권하구분(권하)

			
			/**********************************************************
			* 4. 권하실적위치가 대차(상차)
			* 4.1 대차 상차 정보 등록
			*   - 작업예약 야드작업계획대차 수정
			*   - 대차이송재료 등록
			* 4.2 대차 하차스케줄 생성
			*   - 작업예약 등록
			*   - 작업예약재료 등록
			* 4.3 대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
			* 4.4 L2 전송 전문
			**********************************************************/
			
			/**********************************************************
			* 5. 권하실적위치가 차량(상차)
			* 5.1 차량이송재료 등록
			* 5.2 차량스케줄 야드차량진행상태, 야드설비작업상태(L:영차), 야드설비작업매수 등 수정
			*   - 직상차(차량 스케줄코드가 아님) : 야드차량진행상태(4:상차개시)
			*   - 상차완료(차량 스케줄코드가 아님) : 야드차량진행상태(5:상차완료)
			*   - 야드차량사용구분이 구내운송(L)이고 상차완료이면
			*     . 마지막 크레인스케줄 이면
			* 5.3 공통 처리 : 야드차량사용구분이 구내운송(L)이고 상차완료이면
			* 5.3.2 소재이송지시 야드재료예정저장From위치코드, 이송상차일자 수정
			* 5.3.3 저장품 목표야드, 목표동, 목표행선 등을 수정
			* 5.4 야드차량사용구분이 출하차량(G)
			* 5.4.1 출하관리 일품출하상차실적(YSDSJ007) 전송
			* 5.4.2 출하관리 출하상차완료(YSDSJ008) 전송
			*     - 상차완료(마지막 크레인스케줄)이면
			**********************************************************/
			//차량상차완료여부(소재이송지시 수정 및 공통Table 소재이송일시 수정)
			String carLdCmplYn 		= "N";  // 상차완료
			String bayCarLdCmplYn 	= "N";  // 해당동 상차완료
			String ydCarUseGp  		= "";
			String ydCarSchId  		= "";
			String CarNo  			= "";
			String TransOrdDate  	= "";
			String TransOrdSeqNo  	= "";
			String WlocCd  			= "";
			String ydPndCd  		= "";
			String drvTelNo  		= "";   // 기사전화번호
			
			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				//상차 차량스케줄  조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId     ); 
				recPara.setField("YS_STK_COL_GP" , ydDnWrLoc.substring(0, 6) ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd 
				SELECT YD_CAR_SCH_ID
				     , YD_CAR_USE_GP 
				     , CAR_NO
				     , TRANS_ORD_DATE
				     , TRANS_ORD_SEQNO
				     , DEST_TEL_NO
				     , YS_STK_COL_GP
				     , WLOC_CD
				     , YD_PNT_CD
				     , STOCK_CNT
				     , LYR_CNT
				     , CRNMTL_CNT
				     , BAY_LYR_CNT
				     , CASE WHEN STOCK_CNT   <= LYR_CNT + CRNMTL_CNT THEN 'Y' ELSE'N' END AS CAR_LD_CMPL_YN
				     , CASE WHEN BAY_LYR_CNT <= LYR_CNT + CRNMTL_CNT THEN 'Y' ELSE'N' END AS CAR_BAY_LD_CMPL_YN
				  FROM
				       (
				        SELECT TS.YD_CAR_SCH_ID
				              ,TS.YD_CAR_USE_GP
				              ,TS.CAR_NO
				              ,TS.TRANS_ORD_DATE
				              ,TS.TRANS_ORD_SEQNO
				              ,TS.DEST_TEL_NO
				              ,SC.YS_STK_COL_GP
				              ,SC.WLOC_CD
				              ,SC.YD_PNT_CD
				              ,(SELECT COUNT(*)    -- 차량상차 대상매수
				                  FROM TB_YS_STOCK
				                 WHERE TRANS_ORD_DATE = TS.TRANS_ORD_DATE
				                   AND TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				                   AND CAR_NO = TS.CAR_NO
				               ) AS STOCK_CNT
				              ,(SELECT COUNT(*)    -- 해당동 차량 상차대상 매수
				                  FROM TB_YS_STOCK A
				                     , TB_YS_STKLYR B
				                 WHERE A.SSTL_NO  = B.SSTL_NO
				                   AND A.TRANS_ORD_DATE = TS.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				                   AND A.CAR_NO = TS.CAR_NO
				                   AND B.YD_STK_LYR_MTL_STAT IN ('C','D')
				                   AND SUBSTR(B.YS_STK_COL_GP,1,2) = SUBSTR(SC.YS_STK_COL_GP,1,2)
				               ) AS BAY_LYR_CNT
				              ,(SELECT COUNT(*)    -- 차량 포인트에 적재된 매수
				                  FROM TB_YS_STKLYR
				                 WHERE YS_STK_COL_GP = SC.YS_STK_COL_GP
				                   AND SSTL_NO IS NOT NULL 
				                   AND YD_STK_LYR_MTL_STAT = 'C')  AS LYR_CNT
				              ,(SELECT COUNT(*)    -- 권하처리 매수
				                  FROM TB_YS_CRNWRKMTL
				                 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
				                   AND DEL_YN        = 'N' ) AS CRNMTL_CNT
				          FROM TB_YS_STKCOL SC
				              ,TB_YS_CARSCH TS
				         WHERE SC.YS_STK_COL_GP = :V_YS_STK_COL_GP
				           AND SC.YD_CAR_USE_GP = TS.YD_CAR_USE_GP
				           AND ((SC.YD_CAR_USE_GP = 'L' AND SC.TRN_EQP_CD = TS.TRN_EQP_CD) --구내운송
				             OR (SC.YD_CAR_USE_GP = 'G' AND SC.CAR_NO = TS.CAR_NO)) --출하차량
				           AND TS.DEL_YN = 'N'
				        )   

					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd", logId, methodNm, "상차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					CarNo 			= commUtils.trim(jrChk.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrChk.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrChk.getFieldString("YD_PNT_CD")); 

					String kkSTOCK_CNT 	= commUtils.trim(jrChk.getFieldString("STOCK_CNT")); 	//차량상차 대상매수
					String kkLYR_CNT  	= commUtils.trim(jrChk.getFieldString("LYR_CNT")); 		//차량 포인트에 적재된 매수
					String kkCRNMTL_CNT = commUtils.trim(jrChk.getFieldString("CRNMTL_CNT")); 	//권하처리 매수
					String kkBAY_LYR_CNT= commUtils.trim(jrChk.getFieldString("BAY_LYR_CNT")); 	//해당동 차량 상차대상 매수
					
					ydCarSchId  	= commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					ydCarUseGp 		= commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); 	//야드차량사용구분
					carLdCmplYn 	= commUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); 	//차량상차완료여부
					bayCarLdCmplYn 	= commUtils.trim(jrChk.getFieldString("CAR_BAY_LD_CMPL_YN")); //해당동 차량상차완료여부
					drvTelNo 		= commUtils.trim(jrChk.getFieldString("DEST_TEL_NO")); //해당동 차량상차완료여부
					
					commUtils.printLog(logId, "★★★★★★복수상차::::완료 여부 :"+carLdCmplYn + " 대상수:" +kkSTOCK_CNT +" 차량에 등록수:" + kkLYR_CNT +  " CRN작업:" + kkCRNMTL_CNT+  " 해당동 차량 상차대상 매수:" + kkBAY_LYR_CNT, "SL");
					//차량이송재료(TB_YS_CARFTMVMTL) 상차 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
					recPara.setField("YD_CRN_SCH_ID" , ydCrnSchId ); 
					recPara.setField("MODIFIER" 	 , modifier     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarMtlIns 
					--크레인권하실적 차량이송재료 등록 
					MERGE INTO TB_YS_CARFTMVMTL TM USING (
					SELECT :V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
					      ,CM.SSTL_NO
					      ,:V_MODIFIER      AS MODIFIER
					      ,SYSDATE          AS MOD_DDTT
					      ,'N'              AS DEL_YN
					      ,'01'             AS YS_STK_BED_NO
					      ,TO_CHAR((SELECT COUNT(*)
					                  FROM TB_YS_CARFTMVMTL
					                 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					                   AND DEL_YN         = 'N') + TO_NUMBER(CM.YS_STK_LYR_NO),'FM00') AS YS_STK_LYR_NO
					      ,ST.HCR_GP
					      ,ST.STL_PROG_CD
					  FROM TB_YS_CRNWRKMTL CM
					      ,TB_YS_STOCK     ST
					 WHERE CM.SSTL_NO       = ST.SSTL_NO
					   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   AND CM.DEL_YN        = 'N'
					) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO)
					WHEN NOT MATCHED THEN
					INSERT (TM.YD_CAR_SCH_ID, TM.SSTL_NO  , TM.REGISTER   , TM.REG_DDTT     ,
					        TM.MODIFIER     , TM.MOD_DDTT, TM.DEL_YN     , TM.YS_STK_BED_NO,
					        TM.YS_STK_LYR_NO, TM.HCR_GP  , TM.STL_PROG_CD)
					VALUES (DD.YD_CAR_SCH_ID, DD.SSTL_NO  , DD.MODIFIER   , DD.MOD_DDTT     ,
					        DD.MODIFIER     , DD.MOD_DDTT, DD.DEL_YN     , DD.YS_STK_BED_NO,
					        DD.YS_STK_LYR_NO, DD.HCR_GP  , DD.STL_PROG_CD)
					*/    	
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarMtlIns", logId, methodNm, " 상차 이송재료 등록 ");
					
					
					
					//차량스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					recPara = JDTORecordFactory.getInstance().create();
					
					if ("Y".equals(carLdCmplYn)) {
						recPara.setField("YD_CAR_PROG_STAT", "5"); //야드차량진행상태(상차완료)
					} else {
						recPara.setField("YD_CAR_PROG_STAT", "4"); //야드차량진행상태(상차개시)
					}
					recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
					recPara.setField("YS_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
					recPara.setField("WR_DT" 			, currDt ); 
					recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
					recPara.setField("MODIFIER" 		, modifier     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd 
					--크레인권하실적 상차 차량스케줄 수정
					MERGE INTO TB_YS_CARSCH TS USING (
					SELECT TM.YD_CAR_SCH_ID
					      ,:V_YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
					      ,COUNT(*)            AS YD_EQP_WRK_SH
					      ,SUM(ST.YD_MTL_WT)   AS YD_EQP_WRK_WT
					      ,:V_YD_WBOOK_ID      AS YD_WBOOK_ID
					      ,:V_YS_STK_COL_GP    AS YD_CARLD_STOP_LOC
					      ,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
					 --     ,SF_SLAB_YD_ARR_WLOC_CD(MIN(ST.YD_AIM_YD_GP)) AS ARR_WLOC_CD
					     ,'XXXXX' AS ARR_WLOC_CD
					  FROM TB_YS_CARFTMVMTL TM
					      ,TB_YS_STOCK      ST
					 WHERE TM.SSTL_NO       = ST.SSTL_NO
					   AND TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					   AND TM.DEL_YN        = 'N'
					 GROUP BY TM.YD_CAR_SCH_ID
					) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
					     TS.MODIFIER             = :V_MODIFIER
					    ,TS.MOD_DDTT             = SYSDATE
					    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
					    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
					    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH
					    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT
					    ,TS.ARR_WLOC_CD          = NVL(TS.ARR_WLOC_CD,DD.ARR_WLOC_CD)
					    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_WBOOK_ID
					    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
					    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,DD.WR_DT)
					    ,TS.YD_CARLD_CMPL_DT     = DECODE(DD.YD_CAR_PROG_STAT,'5',DD.WR_DT,NULL)
					*/    
					commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd", logId, methodNm, " 상차 차량스케줄 수정 ");

					recPara = JDTORecordFactory.getInstance().create();
					recPara.setResultCode(logId);	//Log ID
					recPara.setResultMsg(methodNm);	//Log Method Name
					recPara.setField("YD_CAR_SCH_ID", ydCarSchId); 
					recPara.setField("YD_CRN_SCH_ID", ydCrnSchId); 
					
					//출하차량(G)
					if ("G".equals(ydCarUseGp)) {

						// PIDEV
						//출하관리 일품출하상차실적						
//						if("Y".equals(sApplyYnPI)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1084", recPara));
//						} else {
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ007", recPara));
//						}
						
						//검수 등록
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("YD_CAR_SCH_ID", ydCarSchId ); 
						recPara.setField("MODIFIER" 	, modifier     ); 
						/*  com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006EXAMINATIONCHKLIST 
						MERGE INTO TB_YS_EXAMINATIONCHKLIST TM USING (
						SELECT 
						       ST.TRANS_ORD_DATE
						      , ST.TRANS_ORD_SEQNO  
						      , ST.SSTL_NO         
						      , SUBSTR(YS_STR_LOC,1,1) AS YD_GP     
						      , ST.CAR_NO        
						      , '' AS CARD_NO          
						      , '' AS CHECKING_YN     
						      , '' AS LABEL_YN  
						      , '' AS YD_AB_CD      
						      , '' AS YD_AB_CD2        
						      , '' AS GATE_NM         
						      , '' AS BAY_GP    
						      , '' AS YD_CARPNT_CD  
						      , '' AS YD_CAR_UPP_LOC_CD
						      , '' AS REGISTER        
						      , SYSDATE AS REG_DDTT  
						      , '' AS MODIFIER      
						      , SYSDATE AS MOD_DDTT 
						      , 'N' AS DEL_YN  
						  FROM TB_YS_CRNWRKMTL CM
						      ,TB_YS_STOCK     ST
						 WHERE CM.SSTL_NO       = ST.SSTL_NO
						   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						   AND CM.DEL_YN        = 'N'
						) DD ON (TM.TRANS_ORD_DATE = DD.TRANS_ORD_DATE AND TM.TRANS_ORD_SEQNO = DD.TRANS_ORD_SEQNO AND TM.SSTL_NO = DD.SSTL_NO AND TM.YD_GP = DD.YD_GP)
						WHEN NOT MATCHED THEN
						INSERT (TM.TRANS_ORD_DATE, TM.TRANS_ORD_SEQNO  , TM.SSTL_NO         , TM.YD_GP     ,
						        TM.CAR_NO        , TM.CARD_NO          , TM.CHECKING_YN     , TM.LABEL_YN  ,
						        TM.YD_AB_CD      , TM.YD_AB_CD2        , TM.GATE_NM         , TM.BAY_GP    ,
						        TM.YD_CARPNT_CD  , TM.YD_CAR_UPP_LOC_CD, TM.REGISTER        , TM.REG_DDTT  , 
						        TM.MODIFIER      , TM.MOD_DDTT         , TM.DEL_YN                                        )
						VALUES (DD.TRANS_ORD_DATE, DD.TRANS_ORD_SEQNO  , DD.SSTL_NO         , DD.YD_GP     ,
						        DD.CAR_NO        , DD.CARD_NO          , DD.CHECKING_YN     , DD.LABEL_YN  ,
						        DD.YD_AB_CD      , DD.YD_AB_CD2        , DD.GATE_NM         , DD.BAY_GP    ,
						        DD.YD_CARPNT_CD  , DD.YD_CAR_UPP_LOC_CD, DD.REGISTER        , DD.REG_DDTT  , 
						        DD.MODIFIER      , DD.MOD_DDTT         , DD.DEL_YN                                        )
						 
						*/ 
						/**
						* 상차완료 검수 처리 작업 -> 출하상차 작업예약 생성시로 이동(2020/07/20 박비오)
						*/
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006EXAMINATIONCHKLIST", logId, methodNm, "검수등록 ");
			
						//출하관리 출하상차완료
						if ("Y".equals(carLdCmplYn)) {
							
							// PIDEV							
//							if("Y".equals(sApplyYnPI)) {
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1094", recPara));
//							} else {
//								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ008", recPara));
//							}
							
							//압연 조업으로 송신		
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSSBJ001CAR", recPara));		
							
							//특수강재공야드L2로 전문 전송(반납대상)
//							if(("KA".equals(ydSchCd.substring(0 ,2)) || "KB".equals(ydSchCd.substring(0 ,2))) && "BM".equals(ydSchCd.substring(6 ,8))){
//								recPara.setField("YD_CRN_SCH_ID", ydCrnSchId); 							
//								jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN7L003", recPara));
//							}
						} 
//SJH16
						else {
							
						    // 출하상차 완료가 아니고 해당동 상차 완료 인 경우 ----> 복수동 처리 임
							// 차량출발처리
							if ("Y".equals(bayCarLdCmplYn)) { 
								
    	    					commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
    	    					JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
    	    					recInTemp.setResultCode(logId);		//Log ID
    	    					recInTemp.setResultMsg(methodNm);	//Log Method Name
    	    					recInTemp.setField("CAR_NO", 			CarNo);			
    	    					recInTemp.setField("SPOS_WLOC_CD", 		WlocCd);
    	    					recInTemp.setField("SPOS_YD_PNT_CD",	ydPndCd);
    	    					recInTemp.setField("TRANS_ORD_DATE",	TransOrdDate);
    	    					recInTemp.setField("TRANS_ORD_SEQNO",	TransOrdSeqNo);
    	    					
    	    					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
    	    					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
    	    					 
    	    					jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
    	    					
	    	    				commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 복수동 나머지 출하Lot EJB 호출", "SL");		    	    				
    	    					
		    					//복수동 나머지 출하Lot
		    					recInTemp			= JDTORecordFactory.getInstance().create();
		    					
		    					// PIDEV
//		    					if("Y".equals(sApplyYnPI)) {
		    						
			    					recInTemp.setField("MQ_TC_CD"			, "M10LMYDJ1044");
			    					recInTemp.setField("MQ_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
			    					recInTemp.setField("YD_GP",     		ydEqpId.substring(0, 1));
			    					recInTemp.setField("CAR_NO", 			CarNo);
			    					recInTemp.setField("TRN_REQ_DATE", 		TransOrdDate);
			    					recInTemp.setField("TRN_REQ_SEQ"   , 	TransOrdSeqNo);
			    					recInTemp.setField("DOUBLEDONG_CHECK", 	"Y");				//복수동 나머지 출하여부
			    					recInTemp.setField("YD_CAR_SCH_ID",     ydCarSchId);
			    					recInTemp.setField("CMBN_CARLD_YN",     "E"); 				//첫번째 도착창고 : S 두번째 도착창고 : E
			    					recInTemp.setField("WAIT_ARR_DDTT",     currDt);
			    					recInTemp.setField("WAIT_ARR_GP",     	"B");				//대기장도착구분  - B:BACKUP , S:SMARTPHONE
			    					recInTemp.setField("TEL_NO",     		drvTelNo);			//기사 전화번호
			    					recInTemp.setField("YD_SND_YN",     	"Y");			    //mq야드에서 재송신 
			    					
//		    					} else {
//		    					
//	    	    					recInTemp.setField("JMS_TC_CD"			, "DSYSJ005");
//	    	    					recInTemp.setField("JMS_TC_CREATE_DDTT"	, commUtils.getDateTime14()); //JMSTC생성일시
//	    	    					recInTemp.setField("YD_GP",     		ydEqpId.substring(0, 1));
//	    	    					recInTemp.setField("CAR_NO", 			CarNo);
//	    	    					recInTemp.setField("TRANS_ORD_DT", 		TransOrdDate);
//	    	    					recInTemp.setField("TRANS_ORD_SEQNO", 	TransOrdSeqNo);
//	    	    					recInTemp.setField("DOUBLEDONG_CHECK", 	"Y");				//복수동 나머지 출하여부
//	    	    					recInTemp.setField("YD_CAR_SCH_ID",     ydCarSchId);
//	    	    					
//	    	    					recInTemp.setField("CMBN_CARLD_YN",     "E"); 				//첫번째 도착창고 : S 두번째 도착창고 : E
//	    	    					recInTemp.setField("WAIT_ARR_DDTT",     currDt);
//	    	    					recInTemp.setField("WAIT_ARR_GP",     	"B");				//대기장도착구분  - B:BACKUP , S:SMARTPHONE
//	    	    					recInTemp.setField("TEL_NO",     		drvTelNo);			//기사 전화번호 		    						
//		    						
//		    					}

		    					jrRtn = commUtils.addSndData(jrRtn, recInTemp);
							}
						}
					}
					if ("L".equals(ydCarUseGp)) {
						//구내운송 상차완료
						if ("Y".equals(carLdCmplYn)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ008", recPara));
						}						
					}
				}
			}			
			/**********************************************************
			* 6. 권상실적위치가 차량(하차)
			*    마지막 스케줄이면(차량이송재료 없음)
			* 6.1 차량스케줄 야드차량진행상태(E:하차완료), 야드설비작업상태(U:공차) 등 수정
			* 6.4 구내운송 소재차량하차완료 송신(YSTSJ010) 전송
			* 6.5 권하실적재료 적치단 수정 후 등록하여야 하므로 하단부로 이동
			*   - 소재이송지시 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드 수정
			**********************************************************/
			//차량하차여부(공통Table 소재인수일시 수정 송신)
			String carUdCmplYn = "N";

			if ("TR".equals(ydUpWrLoc.substring(2, 4)) && !"TR".equals(ydDnWrLoc.substring(2, 4))) {
				//야드하차작업예약ID 차량하차스케줄 정보 조회
				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_WBOOK_ID" 		, ydWbookId ); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd 
				--크레인권하실적 하차 차량스케줄 조회
				SELECT TS.YD_CAR_SCH_ID
				      ,DECODE(COUNT(TM.SSTL_NO),0,'Y','N') AS CAR_UD_CMPL_YN --차량하차완료여부(권상실적에서 이송재료 삭제)
				      ,TS.CAR_NO
				      ,TS.ARR_WLOC_CD AS WLOC_CD
				      ,TS.YD_PNT_CD3  AS YD_PNT_CD
				      ,TS.TRANS_ORD_DATE
				      ,TS.TRANS_ORD_SEQNO
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID      ,TS.CAR_NO
				      ,TS.YD_CARUD_STOP_LOC
				      ,TS.YD_PNT_CD3
				      ,TS.TRANS_ORD_DATE
				      ,TS.TRANS_ORD_SEQNO
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);


					CarNo 			= commUtils.trim(jrChk.getFieldString("CAR_NO")); 
					TransOrdDate	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE"));
					TransOrdSeqNo 	= commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO")); 
					WlocCd 			= commUtils.trim(jrChk.getFieldString("WLOC_CD")); 
					ydPndCd 		= commUtils.trim(jrChk.getFieldString("YD_PNT_CD")); 		
					
					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					carUdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_UD_CMPL_YN")); //차량하차완료여부
					
					//차량하차완료이면
					if ("Y".equals(carUdCmplYn)) {
						//하차 차량스케줄 야드설비작업상태, 야드차량진행상태, 야드상차작업예약ID, 착지개소코드 등 수정
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setResultCode(logId);	//Log ID
						recPara.setResultMsg(methodNm);	//Log Method Name
						recPara.setField("MODIFIER" , modifier     ); 
						recPara.setField("WR_DT" 			, currDt ); 
						recPara.setField("YD_CAR_SCH_ID" 	, ydCarSchId ); 
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd 
						--크레인권하실적 하차 차량스케줄 수정 
						UPDATE TB_YS_CARSCH
						   SET MODIFIER         = :V_MODIFIER
							  ,MOD_DDTT         = SYSDATE
						      ,YD_EQP_WRK_STAT  = 'U' --공차
						      ,YD_CAR_PROG_STAT = 'E' --하차완료
						      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
						 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
						   AND DEL_YN           = 'N'
						*/    
						commDao.update(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 수정  ");

						
						//구내운송 소재차량하차완료
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ010", recPara));
					}
				} else {
					//야드하차작업예약ID가 없는 경우 권상실적수신에서 작업예약 재료번호로 차량하차스케줄ID를 조회하여 작업예약ID 등록
					commUtils.printLog(logId, "권하실적 하차 차량스케줄이 없습니다.", "SL");
				}
			}
			/**********************************************************
			* 7. 설비, 적치단 , 크레인스케쥴, 저장품, 작업예약재료, 작업예약 수정
			* 7.1 설비 야드설비상태(권하완료) 수정
			* 7.2 적치단
			*   - 크레인 재료정보 삭제
			*   - 권하위치 재료정보 수정
			*   - 권하위치외 같은 재료번호로 등록된 적치단 수정(권하분리 재료 제외)
			* 7.3 크레인스케쥴
			*   - 크레인작업재료 삭제
			*   - 크레인스케쥴 권하실적 수정 및 삭제
			* 7.4 작업예약 마지막 크레인스케쥴 이면
			*   - 작업예약재료 삭제
			*   - 작업예약 수정 및 삭제
			**********************************************************/
			//설비(야드설비상태) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatEqp", logId, methodNm, "설비상태 수정");
			
			//이전 권하위치 단을 Clear 한다.
			jrParam2.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
			jrParam2.setField("YS_STK_COL_GP", "K%"); 
			jrParam2.setField("YS_STK_BED_NO", "%"); 
			commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtl", logId, methodNm, "적치단 작업Clear ");
			
			//적치단(크레인 및 권하위치) 수정
			if("TR".equals(ydDnWrLoc.substring(2, 4))) {
				// 선재/봉강 차량권하시 별도
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyrTr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			} else {
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyr", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			}
			
			//크레인작업재료 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnMtl", logId, methodNm, "크레인작업재료 삭제");
			//크레인스케쥴 권하실적 수정 및 삭제
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnSch", logId, methodNm, "크레인스케쥴 권하실적 수정 및 삭제");

			//작업예약완료 이면
			if ("Y".equals(wbCmplYn)) {
				//작업예약재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDel", logId, methodNm, "작업예약재료 삭제");
				//작업예약 수정 및 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbDel", logId, methodNm, "작업예약 수정 및 삭제");
			} else {
				//크레인작업재료번호로 작업예약재료 삭제 (작업예약재료에 작업이 완료된 재료는 DEL_YN='Y' 함으로써 스케줄 취소 후 재 작업시 작업대상에서 제외시킨다.) 
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDelBySchId", logId, methodNm, "크레인권하실적 권하완료된 작업예약재료만 삭제");
			}
			commUtils.printLog(logId, wbCmplYn, "S+");
			/**********************************************************
			* 7.5 작업예약 마지막 크레인스케쥴 이면
			*   - 입고스케줄이면 YSDSJ001 (입고작업실적) 송신
			*   - 이적스케줄이면 YSDSJ002 (입고작업실적) 송신
			*   - 반납스케줄이면 YSDSJ011 (반납확인정보) 송신
			**********************************************************/
			if ("Y".equals(wbCmplYn)) {
				commUtils.printLog(logId, ydDnWrLoc, "S+");
				commUtils.printLog(logId, ydSchCd, "S+");
				if(ydSchCd.substring(6, 7).equals("L") 
				   && ydDnWrLoc.matches("[K][A-E]\\d\\d\\d\\d\\d\\d")) {
					//입고 스케줄이고 권하위치가 야드이고
					//진도코드가 입고대기(H), 종합판정대기(G) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ001BySchId", jrParam));
				}
				else
				if(ydSchCd.substring(6, 7).equals("L")
					&& ydDnWrLoc.matches("[K][A-E]TS\\d\\d\\d\\d")) {
					//입고 스케줄이고 권하위치가 Traverser 이면 Stacker Crane 입고스케줄을 생성한다.
					//1) Stacker Crane 작업예약등록
					//2) Stacker Crane 스케줄 Main 호출
					
					String scWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //stacker crane 용 작업예약ID
					
					String scSchCd = ydSchCd.substring(0,2) + "HS" + ydDnWrLoc.substring(4,6) + "LM"; //stacker crane 용 스케줄코드 (ex:KAHS01LM)
					String scEqpId = ydSchCd.substring(0,2) + "SC" + ydDnWrLoc.substring(4,6); //stacker crane 장비코드 (ex:KASC01)
					
					jrParam.setField("SC_YD_WBOOK_ID", scWbookId);  //신규 stacker crane 작업예약ID
					jrParam.setField("OHC_YD_WBOOK_ID", ydWbookId); //완료된 OHC 크래인 작업예약ID
					jrParam.setField("SC_SCH_CD", scSchCd); //신규 stcker crane 스케줄 코드
					jrParam.setField("SC_EQP_ID", scEqpId); //신규 stcker crane 설비ID
					jrParam.setField("YS_DN_WR_LOC", ydDnWrLoc); // 권하실적 위치 (From 위치)
					
					//1.1) Stacker Crane 작업예약재료 생성 
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScWbMtl", logId, methodNm, "Stacker Crane 작업예약재료 생성");
					
					//1.2) Stacker Crane 작업예약  생성 
					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScWbook", logId, methodNm, "Stacker Crane 작업예약  생성");
					
					//2) Stacker Crane 스케줄 Main 호출
					//크레인스케줄 전문 - Log ID, Method, 수정자 Set
					JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
					jrYdMsg.setField("YD_WBOOK_ID"       , scWbookId ); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD"         , scSchCd   ); //야드스케쥴코드
					jrYdMsg.setField("YD_EQP_ID"         , scEqpId   ); //야드설비ID
					jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
					
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				}
				else
				if(ydDnWrLoc.substring(2, 4).equals("TY")) {					
					//반납 스케줄이고 
					//진도코드가 반납대기(J) 인 경우 (진도코드 판단은 쿼리에서 한다.)
					jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ011BySchId", jrParam));
//SJH 추가			//압연 조업으로 송신		
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSSBJ001", jrParam));					
				}
				
			}
// 더미작업도 송신 되어 야 함
			if(ydSchCd.substring(6, 7).equals("M") 
			   && ydDnWrLoc.matches("[K][A-E]\\d\\d\\d\\d\\d\\d")) {
				//이적 스케줄이고 권하위치가 야드이고
				//진도코드가 입고대기(H), 종합판정대기(G), 판정보류(F) 가 아닌경우 (진도코드 판단은 쿼리에서 한다.)
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				
				// PIDEV
				// 제품이적 실적				
//				if("Y".equals(sApplyYnPI)) {				 
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1034BySchId", jrParam));				
//				} else {
//					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ002BySchId", jrParam));
//				}
				
			}
			
			if ("Y".equals(carLdCmplYn)||"Y".equals(carUdCmplYn)) {  // 상하차 완료
				// 차량출발 처리	
				if(!TransOrdDate.equals("")) {      // 구내운송은 자동 출발처리 안함
					commUtils.printLog(logId, "차량스케줄["+ydCarSchId+"]의 차량출발 처리 EJB 호출", "SL");
					JDTORecord recInTemp			= JDTORecordFactory.getInstance().create();
					recInTemp.setResultCode(logId);		//Log ID
					recInTemp.setResultMsg(methodNm);	//Log Method Name
					recInTemp.setField("CAR_NO", 				CarNo);			
					recInTemp.setField("SPOS_WLOC_CD", 			WlocCd);
					recInTemp.setField("SPOS_YD_PNT_CD", 		ydPndCd);
					recInTemp.setField("TRANS_ORD_DATE", 		TransOrdDate);
					recInTemp.setField("TRANS_ORD_SEQNO", 		TransOrdSeqNo);
					
					EJBConnector ejbConn = new EJBConnector("default", "YsCommCarMvSeEJB", this);
					JDTORecord jrRtn1 = (JDTORecord)ejbConn.trx("procOutCarLevWr", new Class[] { JDTORecord.class }, new Object[] { recInTemp });
					
					jrRtn = commUtils.addSndData(jrRtn, jrRtn1); 
				}	
			}
					  			
			/**********************************************************
			* 8. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 8.1 
			*   - 크레인스케줄 재료를 대상
			*   - 권하실적위치로 저장위치 수정
			*   - 권상실적위치가 차량이면 현재진도코드 수정
			* 8.2 저장품 수정
			*   - 작업예약 재료를 대상
			*   - 작업예약이 삭제되었으면 작업예약ID, 스케줄코드 삭제
			*   - 현재진도코드가 저장품과 다르면 관련 항목(산적LotType 등) 수정
			*   - 저장위치가 저장품과 다르면 저장위치 수정
			* 8.2.1
			*   - 봉강자동창고 KATY 입고 스케줄 대상
			*     - 입고예정위치를 봉강일반창고(KA01)로 수정
			*   - 소단중 제품(1500키로 미만)에 대상 한정 
			*     - 입고예정위치를 봉강일반창고(KB01)로 수정 
			*   - 봉강일반창고 KBTY 입고 대상 입고예정위치를 봉강일반창고(KB01)로 수정
			* 8.3 작업이력 등록
			*   - 크레인스케줄 재료를 대상
			* 8.4 차량상차 또는 하차완료 시 차량이송재료 대상으로
			* 8.4.1 
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 8.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			**********************************************************/
			//BUNDLE공통 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006BundleComm", logId, methodNm, "BUNDLE공통 수정");

			//저장품 수정**
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StockByBundle", logId, methodNm, "저장품 수정");

			commUtils.printLog(logId, "비오비오비오20200907" +
					", 권상:"+ ydUpWrLoc +
					", 권하:"+ ydDnWrLoc +
					", 권하단:"+ ydDnWrLayer +
					", 스케줄CD:"+ ydSchCd +
					", 스케줄ID:"+ ydCrnSchId
					, "SL");
			
			// 8.2.1 봉강자동창고 TY 입고 및 반납 소단중 제품 (중량 1500kg 미만) 입고예정위치를 봉강일반창고(KB01)로 수정
			if(( "L".equals(ydSchCd.substring(6,7)) || "B".equals(ydSchCd.substring(6,7)) ) && ydDnWrLoc.startsWith("KATY")){
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				jrParam.setField("YD_RCPT_PLN_STR_LOC",  "KA01");
				jrParam.setField("YD_RCPT_PLN_STR_LOC2", "KB01");
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNXYSL006YdRcptPlnStrLoc", logId, methodNm, "KATY베드 권하시 입고예정위치 수정");
				commUtils.printLog(logId, "KATY -> KA01 or KB01 입고예정위치 변경 완료", "SL");
			}
			// 8.2.1 봉강일반창고 KBTY 입고 및 반납 대상 입고예정위치를 봉강일반창고(KB01)로 수정
			else if(( "L".equals(ydSchCd.substring(6,7)) || "B".equals(ydSchCd.substring(6,7)) )&& ydDnWrLoc.startsWith("KBTY")){
				jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
				jrParam.setField("YD_RCPT_PLN_STR_LOC",  "KB01");
				jrParam.setField("YD_RCPT_PLN_STR_LOC2", "KB01");
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNXYSL006YdRcptPlnStrLoc", logId, methodNm, "KBTY베드 권하시 입고예정위치 수정");
				commUtils.printLog(logId, "KBTY -> KB01 입고예정위치 변경 완료", "SL");
			}
			else{
				commUtils.printLog(logId, "입고예정위치 변경 안함", "SL");
			}
			
			
			// 8.3 작업이력 등록
			jrParam.setField("YD_CAR_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CAR_SCH_ID" ))); 
			jrParam.setField("YD_TCAR_SCH_ID"  , commUtils.trim(jrParam.getFieldString("YD_TCAR_SCH_ID" ))); 
			jrParam.setField("YD_CRN_SCH_ID"   , commUtils.trim(jrParam.getFieldString("YD_CRN_SCH_ID" ))); 
			commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkHist", logId, methodNm, "작업이력 등록");

			
			/**********************************************************
			* 9. 권하지시위치 단과 실적 단이 다르면 저장품제원 전문 전송(YSNxL002)
			**********************************************************/
			if (chgDnWrLayer) {
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L002DnWr", jrParam));
			}
			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치
			resMsg.setField("YD_DN_WR_LOC", ydDnWrLoc); //야드권하실적위치
			


			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				resMsg.setField("MODIFIER"      , modifier  ); //수정자
				jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getYSN6L004(resMsg));
			}
			
			if(!ydEqpId.substring(2,4).equals("SC")) {

				/**********************************************************
				* 11. 크레인작업지시요구 전문 호출(NxYDL004)
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
				jrYdMsg.setField("JMS_TC_CD", msgId.substring(0, 2) + "YSL004"); //JMSTC코드
					
				jrYdMsg.setField("YD_EQP_ID"       , ydEqpId   ); //야드설비ID
				jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       ); //야드작업진행상태(권하완료)
				jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   ); //야드스케쥴코드
				jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId); //야드크레인스케쥴ID
				jrYdMsg.setField("MODIFIER"        , modifier  ); //수정자
				
				//크레인작업지시 전문을 추가
				jrRtn = commUtils.addSndData(jrRtn, this.rcvN6YSL004(jrYdMsg));
			}
			else {
				//SC반납 인 경우 작업예약에 있는 정보 기동처리 함
        		JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("GP", "C"); //일반

    			//작업예약 조회
        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbIdN6YSHGBM", logId, methodNm, "작업예약 조회");

				//작업예약이 있으면 크레인스케줄   호출
				if (jsWrkBook.size() > 0) {
					ydL3Msg = "크레인스케줄 호출";

					jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
					jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
					jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
					jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
					jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
					jrYdMsg.setField("MODIFIER"   , modifier); //수정자
					
					jrRtn = commUtils.addSndData(jrRtn, gdsYsComm.getCrnSchMsg(jrYdMsg));
				} 
			}

			commUtils.printParam(logId+"99", jrRtn);
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
		} catch (Exception e) {
			if (resYn) {
				try {
					
					//PIDEV_F : 정상SET후  ERROR 발생한 경우								
					if( "0000".equals(commUtils.trim(resMsg.getFieldString("YD_L3_HD_RS_CD"))) ) {								
						resMsg.setField("YD_L3_HD_RS_CD"  , "UP99"       );    //야드L3처리결과코드(Error)							
						resMsg.setField("YD_L3_MSG"       , "오류:L3실적 수신처리"); //야드L3MESSAGE(Error)							
					}
					
					//크레인작업실적응답 전문 전송
					EJBConnector resConn = new EJBConnector("default", "YsCommEJB", this);
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { gdsYsComm.getYSN6L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재자동창고 설비이동실적(N5YSL007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN5YSL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고 설비이동실적[GdsYsL2RcvSeEJB.rcvN5YSL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId 			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"			)); //야드설비ID
			String ydTcarMoveGp		= commUtils.trim(rcvMsg.getFieldString("YD_TCAR_MOVE_GP"	)); //야드설비이동구분  S:출발, E:도착
			String trnWrkFullVoidGp	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP"	)); //운송작업영공구분  E:공차, F:영차
			String ydStrLoc			= commUtils.trim(rcvMsg.getFieldString("YS_STR_LOC"			)); //특수강저장위치
			String ydCrnSchId   	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"  	)); //야드크레인스케줄ID
			String sstlNo1		   	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO1"  			)); //특수강재료번호1
			String modifier         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"           )); //수정자(Backup Only)
			
			
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				throw new Exception("야드설비ID(YD_EQP_ID) 없음");
			} else if ("".equals(ydTcarMoveGp)) {
				throw new Exception("야드설비이동구분 값이 없음");
			} else if ("".equals(trnWrkFullVoidGp)) {
				throw new Exception("운송작업영공구분 값이 없음");
			} else if ("".equals(ydStrLoc)) {
				throw new Exception("특수강저장위치 값이 없음");
			} 

			/**********************************************************
			* 2. 설비 현재 위치 변경 변경
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_CURR_STR_LOC"	, ydStrLoc   	); //야드현재저장위치
			jrParam.setField("YD_EQP_ID"  		, ydEqpId       ); //야드설비ID
			jrParam.setField("MODIFIER"     	, modifier  	); //수정자
			jrParam.setField("YD_TCAR_MOVE_GP" 	, ydTcarMoveGp );
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrLoc", logId, methodNm, "설비현재위치 변경");

			/**********************************************************
			* 3A. 영차이면서 출발인 경우 야드맵 수정
			* 
			*   3A.1  CC01, CC02, CC03 이면
			*         CH01, CH02, CH03 의 적치된 재료번호 Clear
			*         
			*   3A.2  CC21 ~ CC26 이면
			*         Home Position HS21 ~ HS26  의 적치된 재료번호 Clear
			**********************************************************/
			if("F".equals(trnWrkFullVoidGp)&&"S".equals(ydTcarMoveGp)) {
            
			    if("KDCC01".equals(ydEqpId) || "KDCC02".equals(ydEqpId)	|| "KDCC03".equals(ydEqpId)) {
                
                    jrParam.setField("YD_STK_COL_GP" 	,  ydEqpId.substring(0,2)+"CH"+ydEqpId.substring(4,6) );
                    jrParam.setField("YD_STK_BED_NO" 	,  "01" );
                   
                    /* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrRcvSet  

                    MERGE INTO TB_YS_STKLYR SL USING (

					SELECT YS_STK_COL_GP
					     , YS_STK_BED_NO
					     , YS_STK_LYR_NO
					     , YS_STK_SEQ_NO
					     , YD_STK_LYR_MTL_STAT
					     , SSTL_NO
					  FROM (
					        SELECT SL.YS_STK_COL_GP
					             , SL.YS_STK_BED_NO
					             , SL.YS_STK_LYR_NO
					             , SL.YS_STK_SEQ_NO
					             , 'C' AS YD_STK_LYR_MTL_STAT
					             , (SELECT SSTL_NO 
					                  FROM TB_YS_STKLYR 
					                 WHERE YS_STK_COL_GP = CASE WHEN DL.P_YD_EQP_ID = 'KDCC01' THEN 'KDCH01'
					                                            WHEN DL.P_YD_EQP_ID = 'KDCC02' THEN 'KDCH02'
					                                            WHEN DL.P_YD_EQP_ID = 'KDCC03' THEN 'KDCH03'
					                                            END 
					                   AND YS_STK_BED_NO = '01'
					                   AND YS_STK_LYR_NO = '01'
					                   AND YS_STK_SEQ_NO = '1')  AS SSTL_NO
					          FROM TB_YS_STKLYR SL
					             , (SELECT :V_YD_EQP_ID AS P_YD_EQP_ID FROM DUAL) DL
					         WHERE SL.YS_STK_COL_GP = CASE WHEN DL.P_YD_EQP_ID = 'KDCC01' THEN 'KDRV01'
					                                       WHEN DL.P_YD_EQP_ID = 'KDCC02' THEN 'KDRV02'
					                                       WHEN DL.P_YD_EQP_ID = 'KDCC03' THEN 'KDRV03'
					                                        ELSE 'KKK' END 
					           AND SL.SSTL_NO IS NULL
					         ORDER BY SL.YS_STK_BED_NO  
					        )
					 WHERE ROWNUM = 1   
					   AND :V_YD_TCAR_MOVE_GP = 'S'
					UNION ALL 
					SELECT YS_STK_COL_GP
					     , YS_STK_BED_NO
					     , YS_STK_LYR_NO
					     , YS_STK_SEQ_NO
					     , 'E' AS YD_STK_LYR_MTL_STAT
					     , ''  AS SSTL_NO
					  FROM TB_YS_STKLYR
					 WHERE YS_STK_COL_GP IN ('KDRV01','KDRV02','KDRV03')
					   AND SSTL_NO  = :V_SSTL_NO
					   AND :V_YD_TCAR_MOVE_GP = 'E'
					
					) DD ON (SL.YS_STK_COL_GP = DD.YS_STK_COL_GP AND SL.YS_STK_BED_NO = DD.YS_STK_BED_NO AND SL.YS_STK_LYR_NO = DD.YS_STK_LYR_NO AND SL.YS_STK_SEQ_NO = DD.YS_STK_SEQ_NO)
					WHEN MATCHED THEN UPDATE SET
						 SL.MODIFIER            = :V_MODIFIER
					    ,SL.MOD_DDTT            = SYSDATE
					    ,SL.SSTL_NO             = DD.SSTL_NO
					    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT
                    */    
                    commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrRcvSet" , logId, methodNm, "RGV SET");
                    
                    commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr" , logId, methodNm, "CC 영차 출발 CH Clear");
                     
			    } else if("KDCC2".equals(ydEqpId.substring(0,5))) {
			    	
                    jrParam.setField("YD_STK_COL_GP" ,  ydEqpId.substring(0,2)+"HS"+ydEqpId.substring(4,6) );
                    jrParam.setField("YD_STK_BED_NO" ,  "01" );
                
                    commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr" , logId, methodNm, "CC 영차 출발 HS Clear");
                    
			    } else if("KDCC3".equals(ydEqpId.substring(0,5))) {
                    
                    jrParam.setField("YD_STK_COL_GP" ,  ydEqpId.substring(0,2)+"CS0"+ydEqpId.substring(5,6) );
                    jrParam.setField("YD_STK_BED_NO" ,  "01" );
                
                    commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr" , logId, methodNm, "CC 영차 출발 HS Clear");
                    
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
	
					jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ001"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , "KDCRD1"                  ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
					
					jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);           
                     
			    }
			}
			
			/**********************************************************
			* 3B. 영차이면서  도착인  경우 야드맵 수정
			* 
			*   3B.1  CC11 ~ CC16 이면 
			*         To위치(Home Position) 야드맵에 제품번호 , 상태 U로 설정
			*         
			*   3B.2  CC31 ~ CC38 이면
			*         To위치(출하 스키드 CS) 야드맵에 제품번호, 상태C로 설정
			**********************************************************/
			if("F".equals(trnWrkFullVoidGp)&&"E".equals(ydTcarMoveGp)) {
				
				if("KDCC1".equals(ydEqpId.substring(0,5))) {
					
					jrParam.setField("YS_STK_COL_GP" 		,  ydEqpId.substring(0,2)+"HS0"+ydEqpId.substring(5,6) );
					jrParam.setField("YS_STK_BED_NO" 		,  "01" );
					jrParam.setField("YS_STK_LYR_NO" 		,  "01" );
					jrParam.setField("YS_STK_SEQ_NO" 		,  "1"  );
					jrParam.setField("SSTL_NO"		 		, sstlNo1); 
					jrParam.setField("YD_STK_LYR_ACT_STAT"	, "E"	); 
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "U"	); 

					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrRcvSet" , logId, methodNm, "RGV CLEAR");
						
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr" , logId, methodNm, "CC 영차 도착 HP 설정");
					
				} else if("KDCC2".equals(ydEqpId.substring(0,5))) {
					
					jrParam.setField("YS_STK_COL_GP" 		,  ydEqpId.substring(0,2)+"HS2"+ydEqpId.substring(5,6) );
					jrParam.setField("YS_STK_BED_NO" 		,  "01" );
					jrParam.setField("YS_STK_LYR_NO" 		,  "01" );
					jrParam.setField("YS_STK_SEQ_NO" 		,  "1"  );
					jrParam.setField("SSTL_NO"		 		, sstlNo1); 
					jrParam.setField("YD_STK_LYR_ACT_STAT"	, "E"	); 
					jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"	); 
						
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr" , logId, methodNm, "CC 영차 도착 HP 설정");
					
					jrParam.setField("SSTL_NO"		 		, sstlNo1); 
					jrParam.setField("YD_CRN_SCH_ID" 		, ydCrnSchId);
					
					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getWrFrWbId", logId, methodNm, "선재동간이적 FROM위치 작업예약ID 조회");
					
					if(jsChk.size() < 1) {
						throw new Exception("선재동간이적,차량반입 FROM위치 작업예약ID 조회 실패!!");
					}

					String ydWbookId = jsChk.getRecord(0).getFieldString("YD_WBOOK_ID");
					jrParam.setField("YD_WBOOK_ID" 			, ydWbookId ); 
					String ydSchCd = jsChk.getRecord(0).getFieldString("YD_SCH_CD");
					if (ydSchCd.substring(6,8).equals("DM")) {
						JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNextBayToBay", logId, methodNm, "선재동간이적 TO위치 작업예약ID 조회"); 
						JDTORecord jrChk2 = JDTORecordFactory.getInstance().create();
						if (jsChk2.size() > 0) {
							jrChk2 = jsChk2.getRecord(0);
	
							JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
							String scYdSchCd = commUtils.trim(jrChk2.getFieldString("YD_SCH_CD"));
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("JMS_TC_CD", "YSYSJ402");
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
							jrYdMsg.setField("YD_WBOOK_ID"       , commUtils.trim(jrChk2.getFieldString("YD_WBOOK_ID")) ); //야드작업예약ID
							jrYdMsg.setField("YD_SCH_CD"         , scYdSchCd   ); //야드스케쥴코드
							jrYdMsg.setField("YD_EQP_ID"         , "KDSC" + scYdSchCd.substring(4, 6)); //야드설비ID
							jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
							jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
							
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
						} else {
							throw new Exception("선재동간이적,차량반입 TO위치 작업예약ID 조회 실패!!");
						}
					}

				} else if("KDCC3".equals(ydEqpId.substring(0,5))) {
					//************************** 출하 SKID OHC 크레인 작업 여부 체크 **************************
					

					jrParam.setField("YD_EQP_ID"	, ydEqpId);
					jrParam.setField("SSTL_NO1"		, sstlNo1);
					
					//L2로 부터 같은 정보(이미처리되어 출고SKID에 존재하는 재료번호) 중복 수신 될 경우 Error 처리
					JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.GdsYsDAO.getCsDupChk", logId, methodNm, "출고SKID 영차도착 중복체크");
					
					if (jsChk2.size() > 0) {
						JDTORecord jrChk3 = JDTORecordFactory.getInstance().create();
						jrChk3 = jsChk2.getRecord(0);
						String scwrkChk = commUtils.trim(jrChk3.getFieldString("WRK_CHK"));
						
						if("Y".equals(scwrkChk)){
							throw new Exception("출고SKID에 이미 작업예약이 존재 합니다. 삭제 후 backup하세요.!!");
						}else{
							commUtils.printLog(logId, "출고SKID에 작업예약 없는 대상 초기화 후 다시 작업!!", "S+");
							
							commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.GdsYsDAO.updCSkidReset" , logId, methodNm, "출고SKID 재료 초기화");
			
						}						 			 
					}				
					
					commDao.update(jrParam, "com.inisteel.cim.ys.bt.dao.GdsYsDAO.updCSkid" , logId, methodNm, "출고SKID 재료 재정렬");
									
					
//CHITO : ONE,TWO 후크 구분 하기
					/*-- ONE 후크 조건
					 * 1.옆 CS 동일 차량이 아닌 경우
					 * 2.옆 CC 가 고장 인 경우
					 * 3.옆 CS 가 공베드 인 경우
					 * 4.설비가 CC34, CC38 인 경우
					 * */
					
					JDTORecordSet jsChk4 = commDao.select(jrParam, "com.inisteel.cim.ys.bt.dao.GdsYsDAO.getCsOneTwoChk", logId, methodNm, "출고SKID ONE 후크 구분 작업 조회 ");
					String Chk ="N";
					String FORWARD_YD_EQP_ID ="";
					String NEXT_YD_EQP_ID ="";
					String FORWARD_NEXT_GP ="";
					
					if (jsChk4.size() > 0) {
						JDTORecord jrChk4 = JDTORecordFactory.getInstance().create();
						jrChk4 = jsChk4.getRecord(0);
						   Chk = commUtils.trim(jrChk4.getFieldString("CHK"));
						   FORWARD_YD_EQP_ID = commUtils.trim(jrChk4.getFieldString("FORWARD_YD_EQP_ID")); 
						   NEXT_YD_EQP_ID = commUtils.trim(jrChk4.getFieldString("NEXT_YD_EQP_ID")); 
						   FORWARD_NEXT_GP= commUtils.trim(jrChk4.getFieldString("FORWARD_NEXT_GP"));  //F:앞하고 TWO후쿠 , T:뒤하고 TWO후크
					}
					commUtils.printLog(logId, "출고SKID ONE 후크 구분 작업: "+Chk+" 앞뒤구분:"+FORWARD_NEXT_GP, "S+");
					
					if("Y".equals(Chk)){
						//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&	TWO 후크 작업 인 경우	&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& 
						JDTORecord jrParam2 = JDTORecordFactory.getInstance().create();
  
						if("F".equals(FORWARD_NEXT_GP)){
							jrParam2.setField("YD_EQP_ID"	, FORWARD_YD_EQP_ID);
						}else if("T".equals(FORWARD_NEXT_GP)){
							jrParam2.setField("YD_EQP_ID"	, NEXT_YD_EQP_ID);
						}
						jrParam2.setField("SSTL_NO1"	, sstlNo1);
						JDTORecordSet jsforward_Chk = commDao.select(jrParam2, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getCsRemainCnt", logId, methodNm, "앞OR뒤쪽 출하SKID 출하준비 완료 여부");
						
						jrParam2.setField("YD_EQP_ID"	, ydEqpId);
						jrParam2.setField("SSTL_NO1"	, sstlNo1);
						JDTORecordSet jsChk = commDao.select(jrParam2, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getCsRemainCnt", logId, methodNm, "출하SKID 출하준비 완료 여부");
						
						
						String ohcSchCd = "";
						if ("0".equals(commUtils.trim(jsforward_Chk.getRecord(0).getFieldString("REMAIN_CNT"))) 
							&& 
							"0".equals(commUtils.trim(jsChk.getRecord(0).getFieldString("REMAIN_CNT")))
						   ) {
							//OHC 작업예약 생성
							String ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
							
							String scSchCd = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"));
							if(scSchCd.equals("")||scSchCd.length() < 8) {
								ohcSchCd = "KDTR10UM"; //OHC crane 용 스케줄코드
							} else if(scSchCd.substring(6, 8).equals("UM")) {
								ohcSchCd = "KDTR10UM" ;
							} else if(scSchCd.substring(6, 8).equals("SM")) {
								ohcSchCd = "KDTR10SM" ;
							} else if(scSchCd.substring(6, 8).equals("BM")) {
								ohcSchCd = "KDTR10BM" ;
							} else {
								ohcSchCd = "KDTR10UM"; //OHC crane 용 스케줄코드
							}
							String ohcEqpId = "KDCR01"; //OHC crane 장비코드 
							
							jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  // crane 작업예약ID
							jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 	// crane 스케줄 코드
							jrParam.setField("OHC_YD_EQP_ID"	, ohcEqpId); 	// crane 설비ID
							
							if("F".equals(FORWARD_NEXT_GP)){
								jrParam.setField("FORWARD_YD_EQP_ID"	, FORWARD_YD_EQP_ID);
							}else if("T".equals(FORWARD_NEXT_GP)){
								jrParam.setField("FORWARD_YD_EQP_ID"	, NEXT_YD_EQP_ID);
							} 
							
							jrParam.setField("YD_EQP_ID"		, ydEqpId); 
							jrParam.setField("FORWARD_NEXT_GP"		, FORWARD_NEXT_GP); 
							
							//1.1) OHC Crane 앞OR뒤쪽 출하SKID 작업예약재료 생성 
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcUmWbMtlForward", logId, methodNm, "앞OR뒤쪽 OHC Crane 작업예약재료 생성");
							 
							//1.2) OHC Crane 작업예약  생성 
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcWbook", logId, methodNm, "OHC Crane 작업예약  생성");
	
							// 선재자동화창고 검수체크리스트 등록
							jrParam.setField("YD_WBOOK_ID" , ohcWbookId ); 
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN5YSL007EXAMINATIONCHKLIST", logId, methodNm, "선재자동화창고 검수체크리스트 등록");
							
                            //2) 가상위치에 저장위치 옮기기
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcLayerChange", logId, methodNm, "OHC Crane 작업예약  생성");
							
							//3) OHC Crane 스케줄 Main 호출
							//크레인스케줄 전문 - Log ID, Method, 수정자 Set
							JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("JMS_TC_CD", "YSYSJ402");
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
							jrYdMsg.setField("YD_WBOOK_ID"       , ohcWbookId ); //야드작업예약ID
							jrYdMsg.setField("YD_SCH_CD"         , ohcSchCd   ); //야드스케쥴코드
							jrYdMsg.setField("YD_EQP_ID"         , ohcEqpId   ); //야드설비ID
							jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
							jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
							
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
							
						}
						
						//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&	TWO 후크 작업 인 경우	&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
					}else if("N".equals(Chk)){
						//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&	ONE 후크 작업 인 경우	&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& 
					
						/* 출하SKID 출하준비 완료 여부 - com.inisteel.cim.ys.gds.dao.GdsYsDAO.getCsRemainCnt 
						SELECT ( SELECT COUNT(*)
						           FROM TB_YS_STKLYR 
						          WHERE YS_STK_COL_GP = 'KDCS0' || SUBSTR(:V_YD_EQP_ID, -1) --도착 SKID
						            AND YD_STK_LYR_MTL_STAT = 'D'       ) AS REMAIN_CNT -- 남은 작업수    
						     , YD_SCH_CD
						  FROM TB_YS_WRKBOOK A
						 WHERE YD_WBOOK_ID =      
						                  ( SELECT MAX(WBK.YD_WBOOK_ID) 
						                      FROM TB_YS_WRKBOOK WBK
						                         , TB_YS_WRKBOOKMTL WBM
						                     WHERE WBK.YD_WBOOK_ID = WBM.YD_WBOOK_ID
						                       AND WBK.YD_SCH_CD LIKE SUBSTR(:V_YD_EQP_ID,1,2) || '%HS%' --출고SCH
						                       AND WBK.YD_TO_LOC_GUIDE LIKE 'KDCS0'|| '%' --TO위치(출하SKID)
						                       AND WBM.SSTL_NO = :V_SSTL_NO1 )
						*/       
						
						JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getCsRemainCnt", logId, methodNm, "출하SKID 출하준비 완료 여부");
						
						String ohcSchCd = "";
						if ("0".equals(commUtils.trim(jsChk.getRecord(0).getFieldString("REMAIN_CNT")))) {
							//OHC 작업예약 생성
							String ohcWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); //OHC crane 용 작업예약ID
							
							String scSchCd = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"));
							if(scSchCd.equals("")||scSchCd.length() < 8) {
								ohcSchCd = "KDTR10UM"; //OHC crane 용 스케줄코드
							} else if(scSchCd.substring(6, 8).equals("UM")) {
								ohcSchCd = "KDTR10UM" ;
							} else if(scSchCd.substring(6, 8).equals("SM")) {
								ohcSchCd = "KDTR10SM" ;
							} else if(scSchCd.substring(6, 8).equals("BM")) {
								ohcSchCd = "KDTR10BM" ;
							} else {
								ohcSchCd = "KDTR10UM"; //OHC crane 용 스케줄코드
							}
							String ohcEqpId = "KDCR01"; //OHC crane 장비코드 
							
							jrParam.setField("OHC_YD_WBOOK_ID"	, ohcWbookId);  // crane 작업예약ID
							jrParam.setField("OHC_YD_SCH_CD"	, ohcSchCd); 	// crane 스케줄 코드
							jrParam.setField("OHC_YD_EQP_ID"	, ohcEqpId); 	// crane 설비ID
							jrParam.setField("YD_EQP_ID"		, ydEqpId);
							//jrParam.setField("CAR_NO"			, commUtils.trim(jsChk.getRecord(0).getFieldString("CAR_NO"))); // 차량번호
							
							
							//1.1) OHC Crane 작업예약재료 생성 
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcUmWbMtl", logId, methodNm, "OHC Crane 작업예약재료 생성");
							
							//1.2) OHC Crane 작업예약  생성 
							commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insOhcWbook", logId, methodNm, "OHC Crane 작업예약  생성");

							// 선재자동화창고 검수체크리스트 등록
							jrParam.setField("YD_WBOOK_ID" , ohcWbookId ); 
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN5YSL007EXAMINATIONCHKLIST", logId, methodNm, "선재자동화창고 검수체크리스트 등록");
							
							//OHC 크레인 스케줄 MAIN 호출
							
							
							//2) OHC Crane 스케줄 Main 호출
							//크레인스케줄 전문 - Log ID, Method, 수정자 Set
							JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("JMS_TC_CD", "YSYSJ402");
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
							jrYdMsg.setField("YD_WBOOK_ID"       , ohcWbookId ); //야드작업예약ID
							jrYdMsg.setField("YD_SCH_CD"         , ohcSchCd   ); //야드스케쥴코드
							jrYdMsg.setField("YD_EQP_ID"         , ohcEqpId   ); //야드설비ID
							jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
							jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
							
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
							
						}
						//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&	ONE 후크 작업 인 경우	&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& 
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
	 *      [A] 오퍼레이션명 : 봉강자동창고 TRV작업대기존 도착실적(N6YSL007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN6YSL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동창고 TRV작업대기존 도착실적[GdsYsL2RcvSeEJB.rcvN6YSL007] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId 			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"			)); //야드설비ID
			String ydTcarMoveGp		= commUtils.trim(rcvMsg.getFieldString("YD_TCAR_MOVE_GP"	)); //야드설비이동구분  S:출발, E:도착
			String trnWrkFullVoidGp	= commUtils.trim(rcvMsg.getFieldString("TRN_WRK_FULLVOID_GP"	)); //운송작업영공구분  E:공차, F:영차
			String ydStrLoc			= commUtils.trim(rcvMsg.getFieldString("YS_STR_LOC"			)); //특수강저장위치
			String ydCrnSchId   	= commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"  	)); //야드크레인스케줄ID
			String sstlNo1		   	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO1"  			)); //특수강재료번호1
			String sstlNo2		   	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO2"  			)); //특수강재료번호2
			String modifier         = commUtils.trim(rcvMsg.getFieldString("MODIFIER"         )); //수정자(Backup Only)
			
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();	//전문 Return
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				throw new Exception("야드설비ID(YD_EQP_ID) 없음");
			} else if ("".equals(ydTcarMoveGp)) {
				throw new Exception("야드설비이동구분 값이 없음");
			} else if ("".equals(trnWrkFullVoidGp)) {
				throw new Exception("운송작업영공구분 값이 없음");
			} else if ("".equals(ydStrLoc)) {
				throw new Exception("특수강저장위치 값이 없음");
			} 

			/**********************************************************
			* 2. 설비 현재 위치 변경
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_CURR_STR_LOC"	, ydStrLoc   	); //야드현재저장위치
			jrParam.setField("YD_EQP_ID"  		, ydEqpId       ); //야드설비ID
			jrParam.setField("MODIFIER"     	, modifier  	); //수정자

			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdEqpCurrLoc", logId, methodNm, "설비현재위치 변경");

			/**********************************************************
			* 2-1. 적치Bed 야드적치Bed활성상태(YD_STK_BED_ACT_STAT) 변경
			**********************************************************/
			String fromLoc = ydStrLoc.substring(0,6);
			String toLoc = ydStrLoc.substring(0,6);
			
			if("S".equals(ydTcarMoveGp)) {
				//출발일경우 출발위치 비활성화 도착위치 비활성화
				if("TS".equals(fromLoc.substring(2,4))) {
					toLoc =  fromLoc.substring(0,2) + "HS" + fromLoc.substring(4);
				} else if("HS".equals(fromLoc.substring(2,4))) {
					toLoc =  fromLoc.substring(0,2) + "TS" + fromLoc.substring(4);
				}
				jrParam.setField("FROM_LOC"				, fromLoc); //from 위치
				jrParam.setField("FROM_LOC_ACT_STAT"	, "C"); //from 위치 활성상태 Close
				jrParam.setField("TO_LOC"				, toLoc); //to 위치
				jrParam.setField("TO_LOC_ACT_STAT"		, "C"); //to 위치 활성상태 Close
 				
			} else if("E".equals(ydTcarMoveGp)) {
				//도착일경우 출발위치 비활성화 도착위치 활성화
				if("TS".equals(toLoc.substring(2,4))) {
					fromLoc =  toLoc.substring(0,2) + "HS" + toLoc.substring(4);
				} else if("HS".equals(toLoc.substring(2,4))) {
					fromLoc =  toLoc.substring(0,2) + "TS" + toLoc.substring(4);
				}
				jrParam.setField("FROM_LOC"				, fromLoc); //from 위치
				jrParam.setField("FROM_LOC_ACT_STAT"	, "C"); //from 위치 활성상태 Close
				jrParam.setField("TO_LOC"				, toLoc); //to 위치
				jrParam.setField("TO_LOC_ACT_STAT"		, "L"); //to 위치 활성상태 적치가능
			}

			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL007StkBed", logId, methodNm, "야드적치Bed활성상태 수정");
			
			
			/**********************************************************
			* 3. 영차이면서  도착인  경우 야드맵 수정
			*   3.1  이전위치 야드맵 Clear
			*   3.2 To위치 야드맵에 제품번호 설정
			*   3.3  검수체크리스트 생성
			**********************************************************/
			if("F".equals(trnWrkFullVoidGp)&&"E".equals(ydTcarMoveGp)) {
				
				jrParam.setField("SSTL_NO1"			, sstlNo1   	); //특수강재료번호1
				jrParam.setField("SSTL_NO2"  		, sstlNo2       ); //특수강재료번호2
				jrParam.setField("YD_CURR_STR_LOC"	, ydStrLoc   	); //야드현재저장위치
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL007StkLyr" , logId, methodNm, "설비이동실적 야드맵 수정");
				
				if(ydStrLoc.substring(0, 4).equals("KATS")) {
					//SC출고 스케줄이고 권하위치가 Traverser 이면 A1 Crane 출고스케줄을 생성한다.
					//1) Crane 작업예약등록
					//2) 차량스케쥴에 상차 ID 등록

					jrParam.setField("YD_CRN_SCH_ID"	, ydCrnSchId   	); //스케쥴 ID
					JDTORecord jrChk = null;
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchAll
					SELECT A.YD_CRN_SCH_ID
					     , A.YD_EQP_ID
					     , A.YD_SCH_CD
					     , B.SSTL_NO
					     , A.YS_UP_WO_LOC
					     , A.YS_UP_WO_LAYER
					     , (SELECT DECODE(YD_WRK_PLAN_TCAR,'','N','Y') FROM TB_YS_WRKBOOK WHERE YD_WBOOK_ID = A.YD_WBOOK_ID) AS TC_YN
					  FROM TB_YS_CRNSCH A
					     , TB_YS_CRNWRKMTL B
					 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					*/   
					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchAll", logId, methodNm, "크레인스케줄 조회");
					if (jsChk.size() > 0) {
						jrChk = jsChk.getRecord(0);
						String ydSchCd 	= commUtils.trim(jrChk.getFieldString("YD_SCH_CD"));
						String ydTc 	= commUtils.trim(jrChk.getFieldString("TC_YN"));
						//출하, 차량반납, 차량이송 
						if (ydSchCd.substring(4, 8).equals("HSUM")||ydSchCd.substring(4, 8).equals("HSBM")||ydSchCd.substring(4, 8).equals("HSSM")) {
						
							String scWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); // crane 용 작업예약ID
							
							String scSchCd = "";
							
							if (ydSchCd.substring(4, 8).equals("HSSM")) {
							    scSchCd = ydSchCd.substring(0,2) + "TR10SM" ; //crane 용 스케줄코드:차량이송
							} else if (ydSchCd.substring(4, 8).equals("HSBM")) {
								scSchCd = ydSchCd.substring(0,2) + "TR10BM" ; //crane 용 스케줄코드:차량반납
							} else {
								scSchCd = ydSchCd.substring(0,2) + "TR10UM" ; //crane 용 스케줄코드:출하
							}
						
							String scEqpId = ydSchCd.substring(0,2) + "CR" + ydSchCd.substring(1,2)+"1"; //장비코드 (ex:KACRA1)
							
							jrParam.setField("SC_YD_WBOOK_ID", scWbookId);  //신규 crane 작업예약ID
							jrParam.setField("YS_DN_WR_LOC", ydStrLoc); // 권하실적 위치 (From 위치)
							jrParam.setField("SC_SCH_CD", scSchCd); //신규 crane 스케줄 코드
							jrParam.setField("SC_EQP_ID", scEqpId); //신규 crane 설비ID
							
	
							commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScTrWbMtl", logId, methodNm, "Crane 작업예약재료 생성");
	
							//1.2) Crane 작업예약  생성 
							commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScTrWbook", logId, methodNm, "Crane 작업예약  생성");
							
							//1.3) 출하일 때, 봉강자동화창고 검수체크리스트 등록 _오주원 주임 요청 (2020.07.20 박비오 수정)
							//if (ydSchCd.substring(4, 8).equals("HSUM")){
							//	jrParam.setField("YD_WBOOK_ID" , scWbookId ); 
							//	commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN6YSL007EXAMINATIONCHKLIST", logId, methodNm, "선재자동화창고 검수체크리스트 등록");
							//}
							
							//2)  Crane 스케줄 Main 호출
							//크레인스케줄 전문 - Log ID, Method, 수정자 Set
							JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
							jrYdMsg.setField("YD_WBOOK_ID"       , scWbookId ); //야드작업예약ID
							jrYdMsg.setField("YD_SCH_CD"         , scSchCd   ); //야드스케쥴코드
							jrYdMsg.setField("YD_EQP_ID"         , scEqpId   ); //야드설비ID
							jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
							jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
							
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
							
						// SC 동내반납	
						} else if (ydSchCd.substring(4, 8).equals("HGBM")) {
						
							String scWbookId = commDao.getSeqId(logId, methodNm, "WrkBook"); // crane 용 작업예약ID
							String scEqpId = ydSchCd.substring(0,2) + "CR" + ydSchCd.substring(1,2)+"2"; //장비코드 (ex:KACRA1)

							String scSchCd = ""; 
							if (ydTc.equals("Y")) {
								scSchCd = ydSchCd.substring(0,2) + "TC01UM" ; //동간반납
								jrParam.setField("YD_TO_LOC_GUIDE"	, "KBTY"); //
								jrParam.setField("YD_WRK_PLAN_TCAR"	, "JXTC01"); //
								jrParam.setField("YD_AIM_BAY_GP"	, "B"); //
							} else {
								scSchCd = ydSchCd.substring(0,2) + "TS00BM" ; //동내반납
								jrParam.setField("YD_TO_LOC_GUIDE"	, "KATY"); //
								jrParam.setField("YD_WRK_PLAN_TCAR"	, ""); //
								jrParam.setField("YD_AIM_BAY_GP"	, "A"); //
							}
								
							
							jrParam.setField("SC_YD_WBOOK_ID"	, scWbookId);  //신규 crane 작업예약ID
							jrParam.setField("YS_DN_WR_LOC"  	, ydStrLoc); // 권하실적 위치 (From 위치)
							jrParam.setField("SC_SCH_CD"		, scSchCd); //신규 crane 스케줄 코드
							jrParam.setField("SC_EQP_ID"		, scEqpId); //신규 crane 설비ID

							
							commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScTrWbMtl", logId, methodNm, "Crane 작업예약재료 생성");
							

							//1.2) Crane 작업예약  생성 
							commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insScTrWbook", logId, methodNm, "Crane 작업예약  생성");
							
							//2)  Crane 스케줄 Main 호출
							//크레인스케줄 전문 - Log ID, Method, 수정자 Set
							JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
							jrYdMsg.setResultCode(logId);	//Log ID
							jrYdMsg.setResultMsg(methodNm);	//Log Method Name
							jrYdMsg.setField("JMS_TC_CD", "YSYSJ302");
							jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()  ); //JMSTC생성일시
							jrYdMsg.setField("YD_WBOOK_ID"       , scWbookId ); //야드작업예약ID
							jrYdMsg.setField("YD_SCH_CD"         , scSchCd   ); //야드스케쥴코드
							jrYdMsg.setField("YD_EQP_ID"         , scEqpId   ); //야드설비ID
							jrYdMsg.setField("YD_SCH_ST_GP"      , "A" ); //야드스케쥴기동구분
							jrYdMsg.setField("YD_SCH_REQ_GP"     , "M"); //야드스케쥴요청구분					
							
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);					
						}
						
					}
				}
				
			} else {
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL007StLyrStat", logId, methodNm, "야드적치Layer활성상태 수정");
				commUtils.printLog(logId, "trnWrkFullVoidGp:" + trnWrkFullVoidGp + "   ydTcarMoveGp:" + ydTcarMoveGp +"   toLoc:" + toLoc , "SL");
				// 공차이면서 입측 도착이면 
				// KAXXX 되어 있는 스케줄 명령선택함
				if("E".equals(trnWrkFullVoidGp) && "E".equals(ydTcarMoveGp) 
						&& (  "KATS01".equals(toLoc)||"KATS02".equals(toLoc)||"KATS03".equals(toLoc)
							||"KATS04".equals(toLoc)||"KATS05".equals(toLoc)||"KATS06".equals(toLoc)
							||"KATS07".equals(toLoc))		   ) {
					
					JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSchCmdSelStart", logId, methodNm, "입측 명령선택 기동");
					
					if (jsChk.size() > 0) {
						JDTORecord jrChk = jsChk.getRecord(0);

						JDTORecord jrInParam = JDTORecordFactory.getInstance().create();
						jrInParam.setResultCode(logId);	//Log ID
						jrInParam.setResultMsg(methodNm);	//Log Method Name
						jrInParam.setField("JMS_TC_CD"      	, "N4YSL004"); 	// 명령선택
						jrInParam.setField("YD_EQP_ID"			, commUtils.trim(jrChk.getFieldString("YD_EQP_ID")));
						jrInParam.setField("YD_WRK_PROG_STAT"	, commUtils.trim(jrChk.getFieldString("YD_EQP_WRK_STAT")));
						jrInParam.setField("YD_SCH_CD"			, commUtils.trim(jrChk.getFieldString("YD_SCH_CD")));
						jrInParam.setField("YD_CRN_SCH_ID"		, commUtils.trim(jrChk.getFieldString("YD_CRN_SCH_ID")));
						
						jrRtn = commUtils.addSndData(jrRtn, this.rcvN4YSL004(jrInParam));			
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
	 *      [A] 오퍼레이션명 : 선재자동창고 RGV BOOKOUT 정보(N5YSL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN5YSL008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고  RGV BOOKOUT 정보[GdsYsL2RcvSeEJB.rcvN5YSL008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        	= commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId 			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"			)); //야드설비ID
			String sstlNo		   	= commUtils.trim(rcvMsg.getFieldString("SSTL_NO"  			)); //특수강재료번호
			String InfoGp		   	= commUtils.trim(rcvMsg.getFieldString("INFO_GP"  			)); //작업구분 : 1:기울임, 2:스켄정보 이상
			
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return
			
			String  wbCmplYn = null;
			String 	ydCrnSchId = null;
			String  ydWbookId = ""; //야드작업예약ID  
			

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				throw new Exception("야드설비ID(YD_EQP_ID) 없음");
			} else if ("".equals(sstlNo)) {
				throw new Exception("특수강재료번호 값이 없음");
			} 

			/**********************************************************
			* 2. SSTL_NO 로 작업예약 , 크레인 스케줄 ID, 작업예약 완료여부를 조회
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("SSTL_NO"		, sstlNo   	); //특수강재료번호
			jrParam.setField("YD_EQP_ID"	, ydEqpId   ); //야드설비ID
			
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getSchWbInfoBySstlNo", logId, methodNm, "제품번호로 스케줄정보 조회");
			
			if (jsChk.size() > 0) {
				JDTORecord jrChk = jsChk.getRecord(0);
				wbCmplYn   = commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				ydCrnSchId = commUtils.trim(jrChk.getFieldString("YD_CRN_SCH_ID"));
				ydWbookId  = commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"));
			
				/**********************************************************
				* 3. 작업예약 , 크레인 스케줄 삭제처리
				**********************************************************/
				jrParam.setField("YD_CRN_SCH_ID" , ydCrnSchId  	); //크레인스케줄ID
				jrParam.setField("YD_WBOOK_ID"	 , ydWbookId   	); //작업예약ID
				
				//크레인작업재료 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CrnMtl", logId, methodNm, "크레인작업재료 삭제");
				//크레인스케쥴 권하실적 삭제
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN5YSL008CrnSchDel", logId, methodNm, "크레인스케쥴 삭제");
	
				//작업예약완료 이면
				if ("Y".equals(wbCmplYn)) {
					//작업예약재료 삭제
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDel", logId, methodNm, "작업예약재료 삭제");
					//작업예약 수정 및 삭제
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbDel", logId, methodNm, "작업예약 수정 및 삭제");
				} else {
					//크레인작업재료번호로 작업예약재료 삭제 (작업예약재료에 작업이 완료된 재료는 DEL_YN='Y' 함으로써 스케줄 취소 후 재 작업시 작업대상에서 제외시킨다.) 
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDelBySchId", logId, methodNm, "크레인 스케줄 작업예약재료만 삭제");
				}
				
				/**********************************************************
				* 4. SSTL_NO 로 야드맵 정보 Clear
				**********************************************************/
				jrParam.setField("SSTL_NO"		, sstlNo   	); //특수강재료번호
				jrParam.setField("YD_EQP_ID"	, ydEqpId   ); //야드설비ID
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN5YSL008StkLyr" , logId, methodNm, " RGV BOOKOUT 야드맵 수정");
	
				/**********************************************************
				* 5. S-Crane 작업지시 취소 전송
				**********************************************************/
				jrParam.setField("MSG_GP"	, "D"   	); //전문구분
				jrParam.setField("INFO_GP"	, "I"   	); //정보구분
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN5L006", jrParam));
	
			} else {
				throw new Exception("제품번호로 스케줄정보 조회시 실패하였습니다!!");
			}
			
			if(!InfoGp.equals("")) {
				JDTORecord jrParam1= JDTORecordFactory.getInstance().create();
				jrParam1.setResultCode(logId);	//Log ID
				jrParam1.setResultMsg(methodNm);	//Log Method Name
				jrParam1.setField("SSTL_NO"	,sstlNo);
				jrParam1.setField("OFF_RSN"	,InfoGp);
				jrParam1.setField("MODIFIER","N5YSL008");
				
				EJBConnector tranConn = new EJBConnector("default", "GdsYsSchSeEJB", this);
				tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam1 });
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
	 *      [A] 오퍼레이션명 : 선재자동창고 S-Crane작업선택(N5YSL009)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN5YSL009(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고 S-Crane작업선택[GdsYsL2RcvSeEJB.rcvN5YSL009] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"		)); //야드설비ID
			String ydCrnSchId   = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"  )); //야드크레인스케줄ID
			String infoGp	   	= commUtils.trim(rcvMsg.getFieldString("INFO_GP"  )); //작업구분
			 
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				throw new Exception("야드설비ID(YD_EQP_ID) 없음");
			} else if ("".equals(ydCrnSchId)) {
				throw new Exception("야드크레인스케줄ID 없음");
			} 

			/**********************************************************
			* 2. 크레인 스케줄 상태 변경
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			if(infoGp.equals("9")) {                 //취소
				jrParam.setField("YD_WRK_PROG_STAT"	, "W"   	    ); //명령선택 대기
			} else {
				jrParam.setField("YD_WRK_PROG_STAT"	, "1"   	    ); //권상지시 대기(선택)
			}
			jrParam.setField("YD_CRN_SCH_ID"  	, ydCrnSchId	); //야드크레인스케쥴ID
			jrParam.setField("YD_EQP_ID"  		, ydEqpId       ); //야드설비ID

			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchWrkProgStat", logId, methodNm, "크레인스케줄 야드작업진행상태 변경");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강자동창고 S-Crane작업선택(N6YSL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN6YSL008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동창고 S-Crane작업선택[GdsYsL2RcvSeEJB.rcvN6YSL008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydEqpId 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"		)); //야드설비ID
			String ydCrnSchId   = commUtils.trim(rcvMsg.getFieldString("YD_CRN_SCH_ID"  )); //야드크레인스케줄ID
			String infoGp	   	= commUtils.trim(rcvMsg.getFieldString("INFO_GP"  )); //작업구분
			
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrRtn = null;	//전문 Return

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				throw new Exception("야드설비ID(YD_EQP_ID) 없음");
			} else if ("".equals(ydCrnSchId)) {
				throw new Exception("야드크레인스케줄ID 없음");
			} 

			/**********************************************************
			* 2. 크레인 스케줄 상태 변경
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			if(infoGp.equals("9")) {                 //취소
				jrParam.setField("YD_WRK_PROG_STAT"	, "W"   	    ); //명령선택 대기
			} else {
				jrParam.setField("YD_WRK_PROG_STAT"	, "1"   	    ); //권상지시 대기(선택)
			}
			jrParam.setField("YD_CRN_SCH_ID"  	, ydCrnSchId	); //야드크레인스케쥴ID
			jrParam.setField("YD_EQP_ID"  		, ydEqpId       ); //야드설비ID

			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchWrkProgStat", logId, methodNm, "크레인스케줄 야드작업진행상태 변경");

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 봉강입고 CARRY-OUT 요구(M6YSL101)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM6YSL101(JDTORecord rcvMsg) throws DAOException {
	/*  YD_EQP_ID	야드설비ID (KAPC01 :소형정정,KAPC02 :중형정정,KBPC03 :대형정정,KBPC04 :보류재정정)
        SSTL_NO1	특수강재료번호1
           :
        SSTL_NO23	특수강재료번호23	
	*/
		String methodNm = "봉강입고 CARRY-OUT 요구[GdsYsL2RcvSeEJB.rcvM6YSL101]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); 
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			String ydSSTL_NO7    = commUtils.trim(rcvMsg.getFieldString("SSTL_NO7"        ));  //입고예정 번들 번호
			String L3Hmi         = commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         //백업화면 기동 여부
			String insYsStkBedNo= "";  // 재료 적치 할 bed 
			String modifier      = "M6YSL101";  //수정자
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YS_STK_COL_GP"      , ydEqpId); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"      , "07"   ); //기준 BED : 07
			jrParam.setField("SSTL_NO"     		  , ydSSTL_NO7); //야드적치Bed번호
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			

			if(L3Hmi.equals("N")) {
				// IF수신 기동
				/**********************************************************
				* 2. 적재위치 등록 여부
				**********************************************************/
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo
				SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				  FROM TB_YS_STKLYR
				WHERE SSTL_NO = :V_SSTL_NO
				AND YS_STK_COL_GP = :V_YS_STK_COL_GP 
				AND YS_STK_BED_NO >= :V_YS_STK_BED_NO 
			    */ 
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo", logId, methodNm, "저장위치 등록여부");
	
				if (jsChk != null && jsChk.size() > 0) {
					throw new Exception("이미 적재위치 등록된 제품입니다.");
				}							
			
			}

			String[][] bedMtl = new String[23][3];	//Bed재료정보
			for (int ii = 0; ii < 23; ii++) {
				for (int jj = 0; jj < 3; jj++) {
					bedMtl[ii][jj] = "";
				}
			}
			/**********************************************************
			* 3. BED SHIFT   7 ~ 22 까지 SHIFT	
 			**********************************************************/
			JDTORecordSet jsChk1  = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord outParam1 = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
			/* com.inisteel.cim.ys.gds.dao.GdsYsDAO.getStkLyrCnt 
			SELECT YS_STK_COL_GP
			     , YS_STK_BED_NO 
			     , CASE WHEN COUNT(SSTL_NO) > 0 THEN 1 ELSE 0 END AS SSTL_NO_CNT
			     , MIN(SSTL_NO) AS SSTL_NO
			  FROM TB_YS_STKLYR
			 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND YS_STK_BED_NO >= '07'
			GROUP BY YS_STK_COL_GP, YS_STK_BED_NO
			 */
			jsChk1 = commDao.select(jrParam1, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getStkLyrCnt", logId, methodNm, "적치열 구분 조회");

			for (int ii = 0; ii < jsChk1.size() ; ii++) {  
				jsChk1.absolute(ii+1);
				outParam1  = jsChk1.getRecord();
				bedMtl[ii][0] = commUtils.trim(outParam1.getFieldString("YS_STK_BED_NO"        ));
				bedMtl[ii][1] = commUtils.trim(outParam1.getFieldString("SSTL_NO_CNT"        ));
				bedMtl[ii][2] = commUtils.trim(outParam1.getFieldString("SSTL_NO"        ));
			} //7 ~ 22 까지 SET	
			
			String shift_flag = "N";   // 재료 적치 가능flag;
			String forContinue= "";    // 하단 BED에 SHIFT 할 대상    
			String ShiftEndYn = "N";   // SHIFT 종료
			commUtils.printParam(logId, bedMtl);
			/*
			 *  23번지 부터 7번지 까지확인해서 재료가 없으면 하단 번지에  있는 재료를 옮긴다.
			 *  
			 */
			int jscnt = jsChk1.size() - 1;
			
			for (int ii = jscnt ; ii >= 0  ; ii--) {
				
				if (bedMtl[ii][1].equals("1")) {
					continue;
				} else {
					
					shift_flag = "Y";
					
					if (ShiftEndYn.equals("Y")) {
						break;
					}
					
					// 빈BED 이후 BED 에서 SHIFT 대상 찾음
					for (int jj = ii ; jj >= 0  ; jj--) {
						
						if (bedMtl[jj][1].equals("1")) {
							/* com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkLyrShift 
							UPDATE TB_YS_STKLYR A 
							SET ( A.SSTL_NO 
							     ,A.YD_STK_LYR_ACT_STAT 
							     ,A.YD_STK_LYR_MTL_STAT 
							    ) = ( 
							        SELECT B.SSTL_NO 
							              ,B.YD_STK_LYR_ACT_STAT 
							              ,B.YD_STK_LYR_MTL_STAT 
							         FROM  TB_YS_STKLYR B 
							        WHERE B.YS_STK_COL_GP = A.YS_STK_COL_GP
							          AND B.YS_STK_BED_NO = :V_TO_YS_STK_BED_NO
							          AND B.YS_STK_LYR_NO = A.YS_STK_LYR_NO 
							          AND B.YS_STK_SEQ_NO = A.YS_STK_SEQ_NO 
							    ) 
							 WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
							   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
						 	*/							
							jrParam1.setField("TO_YS_STK_BED_NO"      , bedMtl[jj][0]   );
							jrParam1.setField("YS_STK_BED_NO"         , bedMtl[ii][0]   );
							
							commDao.update(jrParam1, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkLyrShift", logId, methodNm, "야드 적치단 등록");
		
							bedMtl[ii][1] = "1";
							bedMtl[jj][1] = "0";
							
							forContinue = "N";
							// 나머지 하단 BED에 SHIFT 할 대상이  찾음	
							for (int kk = (ii - 1) ; kk >= 0  ; kk--) {
								if (bedMtl[kk][1].equals("1")) {
									forContinue = "Y";
								} 
							}
							
							if (forContinue.equals("N")) {
							// 더이상 SHIFT 할 대상이 없는 경우 적치단 클리어 	
								/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClrBedrow 
								UPDATE TB_YS_STKLYR A 
								   SET A.MODIFIER             = :V_MODIFIER
								     , A.MOD_DDTT             = SYSDATE
								     , A.SSTL_NO              = NULL
								     , A.YD_STK_LYR_ACT_STAT  = 'E' 
								     , A.YD_STK_LYR_MTL_STAT  = 'E' 
								 WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
								   AND A.YS_STK_BED_NO <= :V_YS_STK_BED_NO
								   AND A.DEL_YN              = 'N'
									   
							 	*/							
								jrParam1.setField("YS_STK_BED_NO"         , bedMtl[ii-1][0]     );
								jrParam1.setField("MODIFIER"              , modifier  ); //수정자			
								commDao.update(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClrBedrow", logId, methodNm, "야드 적치단 등록");
								
								ShiftEndYn = "Y"; //SHIFT 종료
								
							}	
							
							break;
						}
					}
				}
			}  
			
			
			if(shift_flag.equals("N")) {
				throw new Exception("입측 "+ ydEqpId + " BED가 FULL나서 더이상  적재할 수 없습니다.");
			}
			// 적재BED 위치 검색 
			for (int ii = jscnt ; ii >= 0  ; ii--) {
				if (bedMtl[ii][1].equals("1")) {
					continue;
				} else {
					insYsStkBedNo = bedMtl[ii][0];  
					break;
				}
			}	
			
			commUtils.printParam(logId, bedMtl);

			String StmpSstlNo = "";
			String chkUpdate  = "";
			if(L3Hmi.equals("N")) {
				
				for(int i = 1; i <= 6; i++) {
					
					/**********************************************************
					* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
					**********************************************************/
					StmpSstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+i));
					commUtils.printLog(logId, StmpSstlNo, "SL");		
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("SSTL_NO"      , StmpSstlNo  	); 
					if (!StmpSstlNo.equals("")) {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "C" );
					} else {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "E" );
					}	
					jrParam.setField("YS_STK_COL_GP", ydEqpId );
					jrParam.setField("YS_STK_BED_NO", "0"+i ); 
					jrParam.setField("YS_STK_LYR_NO", "01" ); 
					jrParam.setField("YS_STK_SEQ_NO", "1" );
					jrParam.setField("MODIFIER"     , modifier  ); //수정자			
					//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
					jrParam.setField("CARRY_OUT"	, "Y"       );
					jrParam.setField("YD_FTMV_MEANS_GP"	, ydEqpId.substring(5,6));
					
					/* STKLYR 변경 - com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr
					UPDATE TB_YS_STKLYR
					SET    SSTL_NO = :V_SSTL_NO
					        ,YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
					        ,YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
					        ,MODIFIER = :V_MODIFIER
					        ,MOD_DDTT = SYSDATE
					WHERE  YS_STK_COL_GP = :V_YS_STK_COL_GP
					AND    YS_STK_BED_NO = :V_YS_STK_BED_NO
					AND    YS_STK_LYR_NO = :V_YS_STK_LYR_NO
					AND    YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
					*/
					
					chkUpdate = "Y";
					
					//야드L2기준으로 무조건 트레킹 정보 생성
//					if(!"".equals(StmpSstlNo)){
//						for (int jj = 0 ; jj < jsChk1.size()  ; jj++) {
//							if (bedMtl[jj][2].equals(StmpSstlNo)) {
//								chkUpdate = "N";
//							} 
//						}
//					}
					
					if(chkUpdate.equals("Y")){
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					}
					
					if (!StmpSstlNo.equals("")) {
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "야드 저장품 등록"); 					
					}
				}
				
				/**********************************************************
				* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
				**********************************************************/
				StmpSstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO7"));
				commUtils.printLog(logId, StmpSstlNo, "SL");		
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("SSTL_NO"      , StmpSstlNo  	); 
				if (!StmpSstlNo.equals("")) {
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C" );
				} else {
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E" );
				}	
				jrParam.setField("YS_STK_COL_GP", ydEqpId );
				jrParam.setField("YS_STK_BED_NO", insYsStkBedNo ); 
				jrParam.setField("YS_STK_LYR_NO", "01" ); 
				jrParam.setField("YS_STK_SEQ_NO", "1" );
				jrParam.setField("MODIFIER"     , modifier  ); //수정자			
				//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
				jrParam.setField("CARRY_OUT"	, "Y"       );
				jrParam.setField("YD_FTMV_MEANS_GP"	, ydEqpId.substring(5,6));
				
				/* STKLYR 변경 - com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr
				UPDATE TB_YS_STKLYR
				SET    SSTL_NO = :V_SSTL_NO
				        ,YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
				        ,YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
				        ,MODIFIER = :V_MODIFIER
				        ,MOD_DDTT = SYSDATE
				WHERE  YS_STK_COL_GP = :V_YS_STK_COL_GP
				AND    YS_STK_BED_NO = :V_YS_STK_BED_NO
				AND    YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				AND    YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
				*/
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
				
				if (!StmpSstlNo.equals("")) {
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "야드 저장품 등록"); 					
				}				
				
			} else {
				
				for(int i = 1; i <= 7; i++) {
					
					/**********************************************************
					* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
					**********************************************************/
					StmpSstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+i));
					commUtils.printLog(logId, StmpSstlNo, "SL");		
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("SSTL_NO"      , StmpSstlNo  	); 
					if (!StmpSstlNo.equals("")) {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "C" );
					} else {
						jrParam.setField("YD_STK_LYR_MTL_STAT", "E" );
					}	
					jrParam.setField("YS_STK_COL_GP", ydEqpId );
					jrParam.setField("YS_STK_BED_NO", "0"+i ); 
					jrParam.setField("YS_STK_LYR_NO", "01" ); 
					jrParam.setField("YS_STK_SEQ_NO", "1" );
					jrParam.setField("MODIFIER"     , modifier  ); //수정자			
					//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
					jrParam.setField("CARRY_OUT"	, "Y"       );
					jrParam.setField("YD_FTMV_MEANS_GP"	, ydEqpId.substring(5,6));
					
					/* STKLYR 변경 - com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr
					UPDATE TB_YS_STKLYR
					SET    SSTL_NO = :V_SSTL_NO
					        ,YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
					        ,YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
					        ,MODIFIER = :V_MODIFIER
					        ,MOD_DDTT = SYSDATE
					WHERE  YS_STK_COL_GP = :V_YS_STK_COL_GP
					AND    YS_STK_BED_NO = :V_YS_STK_BED_NO
					AND    YS_STK_LYR_NO = :V_YS_STK_LYR_NO
					AND    YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
					*/
					
					chkUpdate = "Y";
					for (int jj = 0 ; jj < jsChk1.size()  ; jj++) {
						if (bedMtl[jj][2].equals(StmpSstlNo)) {
							chkUpdate = "N";
						} 
					}
					if(chkUpdate.equals("Y")){
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
					}
					
					if (!StmpSstlNo.equals("")) {
						commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "야드 저장품 등록"); 					
					}

				}					
				
			}
			
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_EQP_ID"      , ydEqpId  	); 
			jrParam.setField("SSTL_NO"        , ydSSTL_NO7 	);
			
			commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insExcptHist_01", logId, methodNm, "야드 저장품 예외이력정보 등록"); 	
			
			/*
			 * 봉강 4PC[KBPC04] 제품 B동 전량 직입고
			 */
			/*
			if("KBPC04".equals(ydEqpId)){
				//STOCK 수정 
				jrParam.setField("YD_RCPT_STR_LOC"		, "B" ); 
				jrParam.setField("YD_RCPT_STR_LOC_RSN"	, "Y" ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojl", logId, methodNm, "TB_YS_STOCK");
			}
			*/
			
			/**********************************************************
			* 3. Carry-Out 처리
			**********************************************************/
			JDTORecord jrRtn = null;
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			//설비인출요구
			jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ313"               ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
			jrYdMsg.setField("YS_STK_BED_NO"     , "07"               		); //야드적치Bed번호 
			jrYdMsg.setField("YS_STK_LYR_NO"     , "01"               		); //야드적치단번호
			jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
			jrYdMsg.setField("MODIFIER"        , modifier                 	); //수정자
			
			//전송할 전문에 추가
			// 자동 CARRY OUT 일단 막음 없음(3.31)
			// jrRtn = commUtils.addSndData(jrYdMsg);
			methodNm += "(자동캐리아웃 중지) YSYSJ313 전송안함";
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 봉강입고 CARRY-OUT 요구(M6YSL101)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM6YSL101B(JDTORecord rcvMsg) throws DAOException {
	/*  YD_EQP_ID	야드설비ID (KAPC01 :소형정정,KAPC02 :중형정정,KBPC03 :대형정정 ,KBPC04 :보류재정정)
        SSTL_NO1	특수강재료번호1
           :
        SSTL_NO21	특수강재료번호21	
	*/
		String methodNm = "봉강입고 CARRY-OUT 요구[GdsYsL2RcvSeEJB.rcvM6YSL101]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); 
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			String ydSSTL_NO7    = commUtils.trim(rcvMsg.getFieldString("SSTL_NO7"        ));  //입고예정 번들 번호
			String L3Hmi         = commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         //백업화면 기동 여부
			
			String modifier      = "M6YSL101";  //수정자
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				throw new Exception("설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]");
			}

			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YS_STK_COL_GP"      , ydEqpId); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"      , "07"   ); //기준 BED : 07
			jrParam.setField("SSTL_NO"     		  , ydSSTL_NO7); //야드적치Bed번호
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			

			if(L3Hmi.equals("N")) {
				// 화면에서 등록 후 기동 처리
				/**********************************************************
				* 2. 적재위치 등록 여부
				**********************************************************/
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo
				SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				  FROM TB_YS_STKLYR
				WHERE SSTL_NO = :V_SSTL_NO
				AND YS_STK_COL_GP = :V_YS_STK_COL_GP 
				AND YS_STK_BED_NO >= :V_YS_STK_BED_NO 
			    */ 
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo", logId, methodNm, "저장위치 등록여부");
	
				if (jsChk != null && jsChk.size() > 0) {
					throw new Exception("이미 적재위치 등록된 제품입니다.");
				}							
			
			}

			String[][] bedMtl = new String[23][3];	//Bed재료정보
			for (int ii = 0; ii < 23; ii++) {
				for (int jj = 0; jj < 3; jj++) {
					bedMtl[ii][jj] = "";
				}
			}
			/**********************************************************
			* 3. BED SHIFT   7 ~ 22 까지 SHIFT	
 			**********************************************************/
			JDTORecordSet jsChk1  = JDTORecordFactory.getInstance().createRecordSet("Temp");
			JDTORecord outParam1 = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			jrParam1.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
			/* com.inisteel.cim.ys.gds.dao.GdsYsDAO.getStkLyrCnt 
			SELECT YS_STK_COL_GP
			     , YS_STK_BED_NO 
			     , CASE WHEN COUNT(SSTL_NO) > 0 THEN 1 ELSE 0 END AS SSTL_NO_CNT
			     , MIN(SSTL_NO) AS SSTL_NO
			  FROM TB_YS_STKLYR
			 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND YS_STK_BED_NO >= '07'
			GROUP BY YS_STK_COL_GP, YS_STK_BED_NO
			 */
			jsChk1 = commDao.select(jrParam1, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getStkLyrCnt", logId, methodNm, "적치열 구분 조회");

			for (int ii = 0; ii < jsChk1.size() ; ii++) {  
				jsChk1.absolute(ii+1);
				outParam1  = jsChk1.getRecord();
				bedMtl[ii][0] = commUtils.trim(outParam1.getFieldString("YS_STK_BED_NO"        ));
				bedMtl[ii][1] = commUtils.trim(outParam1.getFieldString("SSTL_NO_CNT"        ));
				bedMtl[ii][2] = commUtils.trim(outParam1.getFieldString("SSTL_NO"        ));
			} //7 ~ 22 까지 SET	
			
			String shift_flag = "N";   // 재료 적치 가능flag;
			String forContinue= "";    // 하단 BED에 SHIFT 할 대상    
			String ShiftEndYn = "N";   // SHIFT 종료
			commUtils.printParam(logId, bedMtl);
			/* 
			 *  23번지 부터 7번지 까지확인해서 재료가 없으면 하단 번지에  있는 재료를 옮긴다.
			 *  
			 */
			int jscnt = jsChk1.size() - 1;
			
			for (int ii = jscnt ; ii >= 0  ; ii--) {
				
				if (bedMtl[ii][1].equals("1")) {
					continue;
				} else {
					
					shift_flag = "Y";
					
					if (ShiftEndYn.equals("Y")) {
						break;
					}
					
					// 빈BED 이후 BED 에서 SHIFT 대상 찾음
					for (int jj = ii ; jj >= 0  ; jj--) {
						
						if (bedMtl[jj][1].equals("1")) {
							/* com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkLyrShift 
							UPDATE TB_YS_STKLYR A 
							SET ( A.SSTL_NO 
							     ,A.YD_STK_LYR_ACT_STAT 
							     ,A.YD_STK_LYR_MTL_STAT 
							    ) = ( 
							        SELECT B.SSTL_NO 
							              ,B.YD_STK_LYR_ACT_STAT 
							              ,B.YD_STK_LYR_MTL_STAT 
							         FROM  TB_YS_STKLYR B 
							        WHERE B.YS_STK_COL_GP = A.YS_STK_COL_GP
							          AND B.YS_STK_BED_NO = :V_TO_YS_STK_BED_NO
							          AND B.YS_STK_LYR_NO = A.YS_STK_LYR_NO 
							          AND B.YS_STK_SEQ_NO = A.YS_STK_SEQ_NO 
							    ) 
							 WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
							   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
						 	*/							
							jrParam1.setField("TO_YS_STK_BED_NO"      , bedMtl[jj][0]   );
							jrParam1.setField("YS_STK_BED_NO"         , bedMtl[ii][0]   );
							
							commDao.update(jrParam1, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkLyrShift", logId, methodNm, "야드 적치단 등록");
		
							bedMtl[ii][1] = "1";
							bedMtl[jj][1] = "0";
							
							forContinue = "N";
							// 나머지 하단 BED에 SHIFT 할 대상이  찾음	
							for (int kk = (ii - 1) ; kk >= 0  ; kk--) {
								if (bedMtl[kk][1].equals("1")) {
									forContinue = "Y";
								} 
							}
							
							if (forContinue.equals("N")) {
							// 더이상 SHIFT 할 대상이 없는 경우 적치단 클리어 	
								/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClrBedrow 
								UPDATE TB_YS_STKLYR A 
								   SET A.MODIFIER             = :V_MODIFIER
								     , A.MOD_DDTT             = SYSDATE
								     , A.SSTL_NO              = NULL
								     , A.YD_STK_LYR_ACT_STAT  = 'E' 
								     , A.YD_STK_LYR_MTL_STAT  = 'E' 
								 WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
								   AND A.YS_STK_BED_NO <= :V_YS_STK_BED_NO
								   AND A.DEL_YN              = 'N'
									   
							 	*/							
								jrParam1.setField("YS_STK_BED_NO"         , bedMtl[ii-1][0]     );
								jrParam1.setField("MODIFIER"              , modifier  ); //수정자			
								commDao.update(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClrBedrow", logId, methodNm, "야드 적치단 등록");
								
								ShiftEndYn = "Y"; //SHIFT 종료
								
							}	
							
							break;
						}
					}
				}
			}  
			
			
			if(shift_flag.equals("N")) {
				throw new Exception("입측 "+ ydEqpId + " BED가 FULL나서 더이상  적재할 수 없습니다.B");
			}
			
			commUtils.printParam(logId, bedMtl);

			String StmpSstlNo = "";
			String chkUpdate  = "";
			for(int i = 1; i <= 6; i++) {
				
				/**********************************************************
				* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
				**********************************************************/
				StmpSstlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+i));
				commUtils.printLog(logId, StmpSstlNo, "SL");		
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("SSTL_NO"      , StmpSstlNo  	); 
				if (!StmpSstlNo.equals("")) {
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C" );
				} else {
					jrParam.setField("YD_STK_LYR_MTL_STAT", "E" );
				}	
				jrParam.setField("YS_STK_COL_GP", ydEqpId );
				jrParam.setField("YS_STK_BED_NO", "0"+i ); 
				jrParam.setField("YS_STK_LYR_NO", "01" ); 
				jrParam.setField("YS_STK_SEQ_NO", "1" );
				jrParam.setField("MODIFIER"     , modifier  ); //수정자			
				//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
				jrParam.setField("CARRY_OUT"	, "Y"       );
				jrParam.setField("YD_FTMV_MEANS_GP"	, ydEqpId.substring(5,6));
				
				/* STKLYR 변경 - com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr
				UPDATE TB_YS_STKLYR
				SET    SSTL_NO = :V_SSTL_NO
				        ,YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
				        ,YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
				        ,MODIFIER = :V_MODIFIER
				        ,MOD_DDTT = SYSDATE
				WHERE  YS_STK_COL_GP = :V_YS_STK_COL_GP
				AND    YS_STK_BED_NO = :V_YS_STK_BED_NO
				AND    YS_STK_LYR_NO = :V_YS_STK_LYR_NO
				AND    YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
				*/
				
				chkUpdate = "Y";
//				for (int jj = 0 ; jj < jsChk1.size()  ; jj++) {
//					if (bedMtl[jj][2].equals(StmpSstlNo)) {
//						chkUpdate = "N";
//					} 
//				}
				if(chkUpdate.equals("Y")){
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
				}
				
				if (!StmpSstlNo.equals("")) {
					commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "야드 저장품 등록"); 					
				}

			}	
			
			/*
			 * 봉강 4PC[KBPC04] 제품 B동 전량 직입고
			 */
			/*
			if("KBPC04".equals(ydEqpId)){
				//STOCK 수정 
				jrParam.setField("YD_RCPT_STR_LOC"		, "B" ); 
				jrParam.setField("YD_RCPT_STR_LOC_RSN"	, "Y" ); 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updgdsWhsPlnInfojl", logId, methodNm, "TB_YS_STOCK");
			}
			*/
			
			/**********************************************************
			* 3. Carry-Out 처리
			**********************************************************/
			JDTORecord jrRtn = null;
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			//설비인출요구
			jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ313"               ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
			jrYdMsg.setField("YS_STK_BED_NO"     , "07"               		); //야드적치Bed번호 
			jrYdMsg.setField("YS_STK_LYR_NO"     , "01"               		); //야드적치단번호
			jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
			jrYdMsg.setField("MODIFIER"        , modifier                 	); //수정자
			
			//전송할 전문에 추가
			jrRtn = commUtils.addSndData(jrYdMsg);

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	/**
	 *      [A] 오퍼레이션명 : 선재입고 CARRY-OUT 요구(N7YSL101) 
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL101(JDTORecord rcvMsg) throws DAOException {
	/*  POSITION	1,2,3 : WareHouse Posiion, 4 : Unlading Position
        SSTL_NO  	특수강재료번호
	*/
		String methodNm = "선재입고 CARRY-OUT 요구[GdsYsL2RcvSeEJB.rcvN7YSL101]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn2 = null;
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg);
			String position      = commUtils.trim(rcvMsg.getFieldString("POSITION")); //
			String ydSSTL_NO     = commUtils.trim(rcvMsg.getFieldString("SSTL_NO"));  //번들 번호
			
			String ydEqpId       = null; //야드설비ID
			String ysStkBedNo    = "01"; 
			String ysStkLyrNo    = "01"; 
			String modifier      = "N7YSL101";  //수정자
			String insYsStkBedNo = "";  // 재료 적치 할 bed 
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (position.length() < 1) {
				throw new Exception("POSITION 값이 NULL 이 들어왔습니다!");
			}
			
			if ( (!"1".equals(position)) && (!"2".equals(position)) && (!"3".equals(position)) && (!"4".equals(position)) ) {
				throw new Exception("POSITION 값이 1,2,3,4 값이 아닙니다! 수신된 값 :[ " + position + " ]");
			}
			
			if ("".equals(ydSSTL_NO)) {
				throw new Exception("SSTL_NO 값이 NULL 이 들어왔습니다!");
			}
			
			if("Error CoilID".equals(ydSSTL_NO)){
				commUtils.printLog(logId, "특수강재료번호 :" + ydSSTL_NO , "SL");
				return jrRtn2;
			}
			
			
			
			//position 에 따라  설비ID를 설정한다.
			switch(Integer.parseInt(position)) {
			case 1:
				ydEqpId = "KDCH01";
				break;
			case 2:
				ydEqpId = "KDCH02";
				break;
			case 3:
				ydEqpId = "KDCH03";
				break;
			case 4:
				ydEqpId = "KECH01";
				break;
			}
			
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"      , "01"      ); //기준BED
			jrParam.setField("SSTL_NO"     		  , ydSSTL_NO ); //야드적치Bed번호
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			

			/**********************************************************
			* 2-1. 저장품 등록 여부
			**********************************************************/
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYsStkNo 
			SELECT *
			FROM TB_YS_STOCK STK, TB_PB_BUNDLECOMM BNDL
			WHERE 
			    STK.SSTL_NO = :V_SSTL_NO
			    AND STK.SSTL_NO = BNDL.BNDL_NO
			*/
			JDTORecordSet jsStkChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsStkNo", logId, methodNm, "저장품 등록여부");
			if (jsStkChk.size() == 0) {
				throw new Exception("등록되지 않은 제품입니다.");
			}							
			
			/**********************************************************
			* 2-2. 저장위치 등록 여부
			**********************************************************/
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo
			SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				  FROM TB_YS_STKLYR
				WHERE SSTL_NO = :V_SSTL_NO
				AND YS_STK_COL_GP = :V_YS_STK_COL_GP 
				AND YS_STK_BED_NO >= :V_YS_STK_BED_NO 
			*/ 
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo", logId, methodNm, "저장위치 등록여부");

			if (jsChk != null && jsChk.size() > 0) {
				throw new Exception("이미 적재위치 등록된 제품입니다.");
			}							
			//E 동은 SHIFT 처리 한다.
			if(ydEqpId.substring(1,2).equals("E")) {
				
				String[][] bedMtl = new String[8][2];	//Bed재료정보
				for (int ii = 0; ii < 8; ii++) {
					for (int jj = 0; jj < 2; jj++) {
						bedMtl[ii][jj] = "";
					}
				}
				/**********************************************************
				* 3. BED SHIFT  
				* 화면에서 기동은 3BED SET 하고 기동됨
				* 1-> 2 -> 3 -> 4
	 			**********************************************************/
				JDTORecordSet jsChk1  = JDTORecordFactory.getInstance().createRecordSet("Temp");
				JDTORecord outParam1 = JDTORecordFactory.getInstance().create();
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				jrParam1.setField("YS_STK_BED_NO"      , "08"   ); //L2 수신
				
				/*com.inisteel.cim.ys.gds.dao.GdsYsDAO.getStkLyrCntWrE
				SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				     , CASE WHEN COUNT(SSTL_NO)  > 0 THEN 1 ELSE 0 END AS SSTL_NO_CNT
				  FROM TB_YS_STKLYR
				 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND YS_STK_BED_NO <= :V_YS_STK_BED_NO
				 GROUP BY YS_STK_COL_GP, YS_STK_BED_NO
				 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO 
				 */
				jsChk1 = commDao.select(jrParam1, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.getStkLyrCntWrE", logId, methodNm, "적치열 구분 조회");
				
				for (int ii = 0; ii < jsChk1.size() ; ii++) {  
					jsChk1.absolute(ii+1);
					outParam1  = jsChk1.getRecord();
					bedMtl[ii][0] = commUtils.trim(outParam1.getFieldString("YS_STK_BED_NO"        ));
					bedMtl[ii][1] = commUtils.trim(outParam1.getFieldString("SSTL_NO_CNT"        ));
				} 

				int jscnt = jsChk1.size() - 1;
				
				String forContinue= "";    // 하단 BED에 SHIFT 할 대상    
				String ShiftEndYn = "N";   // SHIFT 종료
				commUtils.printParam(logId, bedMtl);
				/*
				 *  03번지 부터 확인해서 재료가 없으면 하단 번지에  있는 재료를 옮긴다.
				 *  
				 */
				for (int ii = 0 ; ii <= jscnt  ; ii++) {
					
					if (bedMtl[ii][1].equals("1")) {
						continue;
					} else {
						
						if (ShiftEndYn.equals("Y")) {
							break;
						}
						
						// 빈BED 이후 BED 에서 SHIFT 대상 찾음
						for (int jj = ii ; jj <= jscnt  ; jj++) {
							
							if (bedMtl[jj][1].equals("1")) {
										
								jrParam1.setField("TO_YS_STK_BED_NO"      , bedMtl[jj][0]   );
								jrParam1.setField("YS_STK_BED_NO"         , bedMtl[ii][0]   );
								
								commDao.update(jrParam1, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.updStkLyrShift", logId, methodNm, "야드 적치단 등록");
			
								bedMtl[ii][1] = "1";
								bedMtl[jj][1] = "0";
								
								forContinue = "N";
								// 나머지 하단 BED에 SHIFT 할 대상이  찾음	

								for (int kk = (ii + 1) ; kk <= jscnt  ; kk++) {
								
									if (bedMtl[kk][1].equals("1")) {
										forContinue = "Y";
									} 
								}
								
								if (forContinue.equals("N")) {
								
									jrParam1.setField("YS_STK_BED_NO"         , bedMtl[ii+1][0]     );
									jrParam1.setField("MODIFIER"              , modifier  ); //수정자			
									commDao.update(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClrBedrow", logId, methodNm, "야드 적치단 등록");
									
									ShiftEndYn = "Y"; //SHIFT 종료
									
								}	
								
								break;
							}
						}
					}
				}  //for
				// 적재BED 위치 검색 
				for (int ii = 0 ; ii <= jscnt  ; ii++) {
					if (bedMtl[ii][1].equals("1")) {
						continue;
					} else {
						insYsStkBedNo = bedMtl[ii][0];  
						break;
					}
				}
				commUtils.printParam(logId, bedMtl);
				
				if(insYsStkBedNo.equals("")){
					insYsStkBedNo = "08";
				}		
			} else {
				
				insYsStkBedNo = ysStkBedNo;
			}
			
			commUtils.printLog(logId, "저장위치 등록  BED :" + insYsStkBedNo , "SL");
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("SSTL_NO"      , ydSSTL_NO    	); 
			jrParam.setField("YD_STK_LYR_MTL_STAT", "C" ); 
			jrParam.setField("YS_STK_COL_GP", ydEqpId );
			jrParam.setField("YS_STK_BED_NO", insYsStkBedNo ); 
			jrParam.setField("YS_STK_LYR_NO", ysStkLyrNo ); 
			jrParam.setField("YS_STK_SEQ_NO", "1" );
			jrParam.setField("MODIFIER"     , modifier  ); //수정자	
			//CARRY_OUT 시점에 
			jrParam.setField("CARRY_OUT"	, "Y"       ); 			

			/* STKLYR 변경 - com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr
			UPDATE TB_YS_STKLYR
			SET    SSTL_NO = :V_SSTL_NO
			        ,YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
			        ,YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
			        ,MODIFIER = :V_MODIFIER
			        ,MOD_DDTT = SYSDATE
			WHERE  YS_STK_COL_GP = :V_YS_STK_COL_GP
			AND    YS_STK_BED_NO = :V_YS_STK_BED_NO
			AND    YS_STK_LYR_NO = :V_YS_STK_LYR_NO
			AND    YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO
			*/
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
			
			//번들 공통 지번 업데이트
			//지니지니
			commDao.insert(jrParam, "com.inisteel.cim.ys.gds.dao.GdsYsDAO.insBdlYdStock", logId, methodNm, "야드 저장품 등록"); 
			
			
			jrParam.setField("FNL_REG_PGM"			, "updInlnLocMod" );
			jrParam.setField("YD_GP"				, ydEqpId.substring(0,1) );
			jrParam.setField("YD_BAY_GP"			, ydEqpId.substring(1,2) );
			jrParam.setField("YD_EQP_GP"			, ydEqpId.substring(2,4) );
			jrParam.setField("YS_STK_COL_NO"		, ydEqpId.substring(4,6) );
			jrParam.setField("YS_STK_BED_NO"		, insYsStkBedNo );
			jrParam.setField("YS_STK_LYR_NO"		, ysStkLyrNo );
			jrParam.setField("YS_STK_SEQ_NO"		, "1" );
			jrParam.setField("YS_STR_LOC"			, ydEqpId + insYsStkBedNo + ysStkLyrNo+"1" );
			jrParam.setField("SSTL_NO"				, ydSSTL_NO );
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBndlCommYsStrLoc", logId, methodNm, "BUNDLE공통 야드저장위치 수정");
			
			/**********************************************************
			* 3. Carry-Out 처리
			**********************************************************/
			JDTORecord jrRtn = null;
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			//설비인출요구
			jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ413"               ); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
			jrYdMsg.setField("YS_STK_BED_NO"     , insYsStkBedNo               ); //야드적치Bed번호 
			jrYdMsg.setField("YS_STK_LYR_NO"     , ysStkLyrNo               ); //야드적치단번호
			jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
			jrYdMsg.setField("MODIFIER"        , modifier                 ); //수정자
			
			//전송할 전문에 추가
			jrRtn = commUtils.addSndData(jrYdMsg);

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}	
	/**
	 * A동 주작업TO위치결정
	 * @param msgRecord
	 * @param rsCrnwrkmtl
	 * @param recCrnSch
	 * @param recWbook
	 * @return
	 * @throws JDTOException
	 */
	public JDTORecord ToLocSetRbA(String logId, String methodNms,  JDTORecord inrecord, String sTO_BAY) throws JDTOException {
		String methodNm = "A2 명령선택시 Traverser TO위치결정 [GdsYsL2RcvSeEJB.ToLocSetRbA] < " + methodNms;
		String LocalmethodNm = "A2 명령선택시 Traverser TO위치결정 [GdsYsL2RcvSeEJB.ToLocSetRbA]";

		String szLogMsg				= null;
		JDTORecordSet outRsResult 	= null;
		JDTORecordSet RsResultBed 	= null;
		JDTORecord outRecResult 	= null;
		JDTORecord recPara			= null;
		JDTORecord recTemp			= null;
		JDTORecord recInBed			= null;
		JDTORecord recCrnwrkmtl		= null;
		JDTORecord recRtn			=  JDTORecordFactory.getInstance().create();
		String szMsg        		= "";  

		String szYS_STK_COL_GP 	= "";		
		String szYS_STK_BED_NO 	= "";		
		String szYS_STK_LYR_NO 	= "";		
		String szYS_DN_WO_LOC   = "";
		String szYS_DN_WO_LAYER = "";			
		String szTcarSndyn 		= "N";  // 대차이동지시
		//----------------------------------------------------------------------------------------------------------------------
		//	크레인 작업재료정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		commUtils.printLog(logId, methodNm, "S+");
		
		String szYD_CRN_SCH_ID 	 = commUtils.trim(inrecord.getFieldString("YD_CRN_SCH_ID"  ));	//크레인스케줄ID
		//----------------------------------------------------------------------------------------------------------------------
		//	야드 저장품 정보 READ
		//----------------------------------------------------------------------------------------------------------------------
		
		szLogMsg = LocalmethodNm + "크레인작업 :" + szYD_CRN_SCH_ID + " 조회 시작 ";
		commUtils.printLog(logId, szLogMsg, "SL");
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_CRN_SCH_ID", szYD_CRN_SCH_ID);
		
		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnwrkmtlBySchId 
		SELECT A.SSTL_NO             AS SSTL_NO          
		      ,A.YS_STK_LYR_NO       AS YS_STK_LYR_NO   
		      ,A.YD_CRN_SCH_ID       AS YD_CRN_SCH_ID   
		      ,A.REGISTER            AS REGISTER             
		      ,A.REG_DDTT            AS REG_DDTT             
		      ,A.MOD_DDTT            AS MOD_DDTT             
		      ,A.MODIFIER            AS MODIFIER
		      ,A.DEL_YN              AS DEL_YN                 
		      ,A.YD_AID_WRK_YN       AS YD_AID_WRK_YN   
		      ,A.HCR_GP              AS HCR_GP                 
		      ,A.STL_PROG_CD         AS STL_PROG_CD       
		      ,A.YS_ROUTE_GP         AS YS_ROUTE_GP
		      ,B.YD_MTL_W            AS YD_MTL_W        
		      ,B.YD_MTL_WT           AS YD_MTL_WT       
		      ,B.YD_MTL_T            AS YD_MTL_T     
		      ,B.YD_MTL_L            AS YD_MTL_L   
		      ,B.YS_MTL_ITEM         AS YS_MTL_ITEM     
		      ,B.YD_STK_LOT_TP       AS YD_STK_LOT_TP   
		      ,B.YD_STK_LOT_CD       AS YD_STK_LOT_CD   
		      ,B.REFUR_CHG_PLN_SERNO AS REFUR_CHG_PLN_SERNO
		      ,B.BLOOM_CL_MTD        AS BLOOM_CL_MTD
		      ,B.HEAT_NO             AS HEAT_NO 
		      ,NVL(B.BUNDLE_T,B.YD_MTL_T ) AS  BUNDLE_T
		      ,SUM(B.YD_MTL_WT) OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SUM_MTL_WT      
		      ,SUM(B.YD_MTL_T)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SUM_MTL_T   
		      ,MAX(B.YD_MTL_W)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS MAX_MTL_W 
		      ,MAX(B.YD_MTL_L)  OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS MAX_MTL_L 
		      ,COUNT(A.SSTL_NO) OVER (ORDER BY A.YS_STK_LYR_NO DESC) AS SH_CNT    
		      ,NVL(MIN(B.YD_CHG_NO) OVER (ORDER BY A.YS_STK_LYR_NO DESC),0) AS YD_CHG_NO   
		      ,(SELECT YS_UP_WO_LOC FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YS_UP_WO_LOC 
		      ,(SELECT YS_UP_WO_LAYER FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YS_UP_WO_LAYER 
		      ,(SELECT YD_WBOOK_ID FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_WBOOK_ID 
		      ,(SELECT YD_EQP_ID FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_EQP_ID       
		      ,A.YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD    
		      ,B.CUST_CD             AS CUST_CD
		      ,B.DETAIL_ARR_CD       AS DETAIL_ARR_CD    
		      ,MAX(CASE WHEN B.YD_MTL_WT <= 1500 THEN 'Y' ELSE 'N' END) OVER() AS TY_BED_YN
		  FROM TB_YS_CRNWRKMTL A                                                        
		      ,TB_YS_STOCK     B                                                        
		 WHERE A.SSTL_NO = B.SSTL_NO                                                      
		   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
		   AND A.DEL_YN = 'N'                                    
		   AND B.DEL_YN = 'N'                                   
		 ORDER BY A.YS_STK_LYR_NO
		*/
		
		JDTORecordSet rsCrnSch = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCrnwrkmtlBySchId", logId, methodNm, "크레인스케쥴 조회");
		if (rsCrnSch.size() <= 0) {
			szLogMsg =  LocalmethodNm + " 크레인스케쥴 조회 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return recRtn;
		}
		
		rsCrnSch.first();
		recCrnwrkmtl = rsCrnSch.getRecord();
		
		String szHEAT_NO   			= commUtils.trim(recCrnwrkmtl.getFieldString("HEAT_NO"));			
		String szCUST_CD   			= commUtils.trim(recCrnwrkmtl.getFieldString("CUST_CD"));			
		String szDETAIL_ARR_CD 		= commUtils.trim(recCrnwrkmtl.getFieldString("DETAIL_ARR_CD"));		
		String szYD_WBOOK_ID 		= commUtils.trim(recCrnwrkmtl.getFieldString("YD_WBOOK_ID"));			
		String szYS_UP_WO_LOC 		= commUtils.trim(recCrnwrkmtl.getFieldString("YS_UP_WO_LOC"));			
		String szYS_UP_WO_LAYER		= commUtils.trim(recCrnwrkmtl.getFieldString("YS_UP_WO_LAYER"));			
		String szYD_EQP_ID  		= commUtils.trim(recCrnwrkmtl.getFieldString("YD_EQP_ID"));	
		String szYD_EQP_WRK_MAX_L 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_L"));		//크레인작업재료 중 최대 길이
		String szYD_EQP_WRK_MAX_W 	= commUtils.trim(recCrnwrkmtl.getFieldString("MAX_MTL_W"));		//크레인작업재료 중 최대 폭
		int intYD_EQP_WRK_SH    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SH_CNT");				//크레인작업재료 총매수
		int intYD_EQP_WRK_WT    	= commUtils.paraRecChkNullInt(recCrnwrkmtl,"SUM_MTL_WT");			//크레인작업재료 총중량
		double dblYD_EQP_WRK_T  	= commUtils.paraRecChkNullDouble(recCrnwrkmtl,"SUM_MTL_T");			//크레인작업재료 총높이		
		String szBUNDLE_T 			= commUtils.trim(recCrnwrkmtl.getFieldString("BUNDLE_T"  ));	//BUNDLE 두께							//번들두께	
//		String szTY_BED_YN			= commUtils.trim(recCrnwrkmtl.getFieldString("TY_BED_YN"  ));	//임시베드로 갈 대상이 있는지 체크 ('Y'이면  임시베드로 to위치 결정) -> 사용자가 입고예정위치로 설정
		
		// TY 대상
		if(sTO_BAY.equals("B")) {
			szLogMsg =  LocalmethodNm + "sTO_BAY :" + sTO_BAY;
			commUtils.printLog(logId, szLogMsg, "SL");
			// 타 동으로 가야 할 경우 
			// 대차 위치 확인하여 대차가 현재동에 있으면 상차 
			//                     현재동에 없으면 대차 상태를 확인 하여  출발지시 처리 함  
			
			JDTORecord   recTc  = JDTORecordFactory.getInstance().create();
			JDTORecord   recBed = JDTORecordFactory.getInstance().create();
			
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk 
			-- 대차 목적동에 대차작업 여부 
			SELECT A.YD_EQP_ID
			     , A.YD_CURR_BAY_GP
			     , A.YD_HOME_BAY_GP
			     , A.YD_EQP_STAT
			     , (SELECT COUNT(*) 
			          FROM TB_YS_STKLYR 
			         WHERE YS_STK_COL_GP = 'K'||B.YD_AIM_BAY_GP ||'TC01' 
			           AND DEL_YN = 'N'
			           AND SSTL_NO IS NOT NULL
			       ) --상차된 경우
			       + 
			       (SELECT DECODE(COUNT(*),1,0,9) 
			          FROM TB_YS_TCARSCH 
			         WHERE DEL_YN = 'N'
			           AND YD_EQP_ID = A.YD_EQP_ID
			           AND YD_CAR_PROG_STAT = '0'
			       ) -- 대차스케쥴 상태대기
			       + 
			       (SELECT COUNT(*)
			          FROM TB_YS_CRNSCH 
			         WHERE DEL_YN = 'N'
			           AND YD_GP  = 'K'
			           AND SUBSTR(YS_DN_WO_LOC,1,4) = 'K'||B.YD_AIM_BAY_GP ||'TC'
			       ) -- 크레인작업지시 편성여부
			       
			       
			       AS WK_CNT
			  FROM TB_YS_EQP A
			     , (SELECT :V_YD_AIM_BAY_GP AS YD_AIM_BAY_GP
			          FROM DUAL) B
			 WHERE A.YD_EQP_ID = :V_YD_EQP_ID
			*/ 
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_EQP_ID"	, "KXTC01");
			recPara.setField("YD_AIM_BAY_GP", sTO_BAY);

			//대차하차스케쥴 조회
			JDTORecordSet jsTc = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcBayWrk", logId, methodNm, "대차작업 여부");
			JDTORecordSet rsBed = JDTORecordFactory.getInstance().createRecordSet("");
			
			if (jsTc.size() > 0) {
				
				recTc = jsTc.getRecord(0);
				String ydTcarCurrBayGp  = commUtils.trim(recTc.getFieldString("YD_CURR_BAY_GP" )); //야드대차스케쥴ID
				String tcarUdCmplYn 	= commUtils.trim(recTc.getFieldString("WK_CNT")); //대차하차완료여부
				String ydEqpStat 		= commUtils.trim(recTc.getFieldString("YD_EQP_STAT"));
				
				if(ydEqpStat.equals("B")) {
					//고장인 경우
					return recRtn;

				} else if ("A".equals(ydTcarCurrBayGp)) {
					//대차가위치가  같은동이면
					szMsg = LocalmethodNm + " : 대차 위치가  작업동과 같은동";
    				commUtils.printLog(logId, szMsg, "SL");
    				recPara = JDTORecordFactory.getInstance().create();
    				recPara.setField("YS_STK_COL_GP", 	"KATC01");
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc
					SELECT YS_STK_COL_GP
					     , YS_STK_BED_NO 
					     , YS_STK_LYR_NO  
					     , YD_MTL_SH 
					  FROM
					(
					SELECT SB.YS_STK_COL_GP
					     , SB.YS_STK_BED_NO 
					     , SL.YS_STK_LYR_NO 
					      ,COUNT(SL.SSTL_NO)                  AS YD_MTL_SH
					  FROM TB_YS_STKBED SB
					      ,TB_YS_STKLYR SL
					 WHERE SB.YS_STK_COL_GP= :V_YS_STK_COL_GP
					   AND SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
					   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
					   AND SB.DEL_YN        = 'N'
					   AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
					 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
					 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
					 )
					 WHERE YD_MTL_SH = 0
					   AND ROWNUM = 1
					 
    				   */
    				rsBed = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTc", logId, methodNm, "대차BED 조회"); 
            		
            		if(rsBed.size() <= 0) {
            			//A동에 대차위치가 없는 경우
            			return recRtn;
        				
            		} else {
    					rsBed.first();
    					recBed = rsBed.getRecord();
    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
    					szYS_DN_WO_LOC  = szYS_STK_COL_GP + szYS_STK_BED_NO; 
    					szYS_DN_WO_LAYER= szYS_STK_LYR_NO;
            		}	    							
				}else {	
					
					szMsg = LocalmethodNm + " : 대차위치가  작업동과 틀린 경우 + tcarUdCmplYn :"+ tcarUdCmplYn;
    				commUtils.printLog(logId, szMsg, "SL");
    				
					if (tcarUdCmplYn.equals("0")) {
						recPara = JDTORecordFactory.getInstance().create();
						recPara.setField("YS_STK_COL_GP", 	"KATC01");
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTcXbay 
						SELECT YS_STK_COL_GP
						     , YS_STK_BED_NO 
						     , YS_STK_LYR_NO  
						     , YD_MTL_SH 
						  FROM
						(
						SELECT SB.YS_STK_COL_GP
						     , SB.YS_STK_BED_NO 
						     , SL.YS_STK_LYR_NO 
						      ,COUNT(SL.SSTL_NO)                  AS YD_MTL_SH
						  FROM TB_YS_STKBED SB
						      ,TB_YS_STKLYR SL
						 WHERE SB.YS_STK_COL_GP= :V_YS_STK_COL_GP
						   AND SB.YS_STK_COL_GP = SL.YS_STK_COL_GP
						   AND SB.YS_STK_BED_NO = SL.YS_STK_BED_NO
						   AND SB.DEL_YN        = 'N'
						   -- AND SB.YD_STK_BED_ACT_STAT = 'L' --적치가능
						 GROUP BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
						 ORDER BY SB.YS_STK_COL_GP, SB.YS_STK_BED_NO, SL.YS_STK_LYR_NO 
						 )
						 WHERE YD_MTL_SH = 0
						   AND ROWNUM = 1
						*/   
	    				rsBed = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybedTcXbay", logId, methodNm, "대차BED(다른동) 조회"); 
	    				
	    				szTcarSndyn = "Y";
	    				
	            		if(rsBed.size() <= 0) {
	        				szMsg = LocalmethodNm + " : 대차 BED READ 실패!";
	        				return recRtn;
	        				
	            		} else {
	    					rsBed.first();
	    					recBed = rsBed.getRecord();
	    					szYS_STK_COL_GP	= commUtils.trim(recBed.getFieldString("YS_STK_COL_GP"));//대차정지위치 적치열
	    					szYS_STK_BED_NO	= commUtils.trim(recBed.getFieldString("YS_STK_BED_NO"));//대차정지위치 적치베드
	    					szYS_STK_LYR_NO	= commUtils.trim(recBed.getFieldString("YS_STK_LYR_NO"));//대차정지위치 적치단
	    					szYS_DN_WO_LOC  = szYS_STK_COL_GP + szYS_STK_BED_NO; 
	    					szYS_DN_WO_LAYER= szYS_STK_LYR_NO;
	    					
							szMsg =LocalmethodNm + " : 대차가위치가  틀린동 + tcarUdCmplYn :" + szYS_STK_COL_GP + szYS_STK_BED_NO + szYS_STK_LYR_NO;
	        				commUtils.printLog(logId, szMsg, "SL");
 	            		}
        				
					} else {
					   //B동작업이 있음
						szMsg = LocalmethodNm + " : B동작업이 있음 ";
						commUtils.printLog(logId, szMsg, "SL");
						return recRtn;
					}
				}
			}
		} else {  //sTO_BAY=A
			/*
			 * PC에서 나가는 중량 1500이하 대상재 임시베드(KATY)로 입고처리
			if("Y".equals(szTY_BED_YN)){
			 *	//임시베드로 갈 대상이 있는지 체크 ('Y'이면  임시베드로 to위치 결정)
		     *	recTemp = JDTORecordFactory.getInstance().create();
		     *	recTemp.setField("SH_CNT", ""+intYD_EQP_WRK_SH); //재료매수
		     *	
			 *	//임시베드 적치가능한 베드  적용
			 *	outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLocGuideKA02", logId, methodNm, "KA02가이드  베드 조회");
			 *	if (outRsResult.size() <= 0) {
			 *		szLogMsg = LocalmethodNm + "이적 BED 검색 실패 ";
			 *		commUtils.printLog(logId, szLogMsg, "SL");
			 *		return recRtn;
			 *	}
			 *	outRsResult.first();
			 *	outRecResult  = outRsResult.getRecord();
			 *	szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		//권하지시위치 TEMP
			 *	szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		//권하지시위치 TEMP
			 *	szYS_STK_LYR_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));		//권하지시위치 TEMP
			 *
			 *	
			 *	// 권하위치 최종결정정보 셋팅.
			 *	szYS_DN_WO_LOC		= szYS_STK_COL_GP + szYS_STK_BED_NO;
			 *	szYS_DN_WO_LAYER 	= szYS_STK_LYR_NO;
			 */

//			}else{
				
				//권상지시위치에 따라 알맞은 적치가능한 베드 검색 방법을 적용
		    	recTemp = JDTORecordFactory.getInstance().create();
		    	recTemp.setField("HEAT_NO", 			szHEAT_NO);											
		    	recTemp.setField("CUST_CD", 			szCUST_CD);											
		    	recTemp.setField("DETAIL_ARR_CD", 		szDETAIL_ARR_CD);									
		    	recTemp.setField("BUNDLE_T", 			szBUNDLE_T);				 //번들두께   	
		    	recTemp.setField("SH_CNT", 				""+intYD_EQP_WRK_SH);		 //재료매수

		    	szLogMsg = LocalmethodNm + " TOSQL:["+szYD_CRN_SCH_ID+ "] 동일한 HEAT_NO["+szHEAT_NO+"], 고객사["+szCUST_CD+"],  상세착지["+szDETAIL_ARR_CD+"] 의 적치가능한 베드 조회 시작";
				commUtils.printLog(logId, szLogMsg, "SL");

				outRsResult = commDao.select(recTemp, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWrkMoveRbA", logId, methodNm, "저장 계획  확인하여 공베드 수가 많은 열 선택");
				if (outRsResult.size() <= 0) {  //기존 입고 스케줄/작업예약 삭제
					szLogMsg =  LocalmethodNm + "입고 열 검색 실패";
					commUtils.printLog(logId, szLogMsg, "SL");
					
					return recRtn;
				}							
			    //  Traverser 위치부 CEHCK: 1건만 나옴	
				for(int i = 1; i <= outRsResult.size(); i++) {
		
					outRsResult.absolute(i);
					outRecResult  = outRsResult.getRecord();
				
					szYS_STK_COL_GP = commUtils.trim(outRecResult.getFieldString("YS_STK_COL_GP"  ));		
					szYS_STK_BED_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_BED_NO"  ));		
					szYS_STK_LYR_NO = commUtils.trim(outRecResult.getFieldString("YS_STK_LYR_NO"  ));		
				
					if       (szYS_STK_COL_GP.substring(4,6).equals("01")||szYS_STK_COL_GP.substring(4,6).equals("02") ) {
						szYS_DN_WO_LOC      = "KATS0101";
						szYS_DN_WO_LAYER 	= "01";
					} else if(szYS_STK_COL_GP.substring(4,6).equals("03")||szYS_STK_COL_GP.substring(4,6).equals("04") ) {
						szYS_DN_WO_LOC      = "KATS0201";
						szYS_DN_WO_LAYER 	= "01";
					} else if(szYS_STK_COL_GP.substring(4,6).equals("05")||szYS_STK_COL_GP.substring(4,6).equals("06") ) {
						szYS_DN_WO_LOC      = "KATS0301";
						szYS_DN_WO_LAYER 	= "01";
					} else if(szYS_STK_COL_GP.substring(4,6).equals("07")||szYS_STK_COL_GP.substring(4,6).equals("08") ) {
						szYS_DN_WO_LOC      = "KATS0401";
						szYS_DN_WO_LAYER 	= "01";
					} else if(szYS_STK_COL_GP.substring(4,6).equals("09")||szYS_STK_COL_GP.substring(4,6).equals("10") ) {
						szYS_DN_WO_LOC      = "KATS0501";
						szYS_DN_WO_LAYER 	= "01";
					} else if(szYS_STK_COL_GP.substring(4,6).equals("11")||szYS_STK_COL_GP.substring(4,6).equals("12") ) {
						szYS_DN_WO_LOC      = "KATS0601";
						szYS_DN_WO_LAYER 	= "01";
					} else if(szYS_STK_COL_GP.substring(4,6).equals("13")||szYS_STK_COL_GP.substring(4,6).equals("14") ) {
						szYS_DN_WO_LOC      = "KATS0701";
						szYS_DN_WO_LAYER 	= "01";
					}
					
					recInBed= JDTORecordFactory.getInstance().create();
					recInBed.setField("YS_STK_COL_GP", 	szYS_DN_WO_LOC.substring(0, 6));		
					recInBed.setField("YS_STK_BED_NO", 	szYS_DN_WO_LOC.substring(6));		
					recInBed.setField("YS_STK_LYR_NO", 	szYS_DN_WO_LAYER);		
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisRbA 
					SELECT A.YS_STK_COL_GP
					     , A.YS_STK_BED_NO
					     , A.YS_STK_LYR_NO
					  FROM TB_YS_STKLYR A
					 WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
					   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
					   AND A.YS_STK_LYR_NO = :V_YS_STK_LYR_NO
					   AND A.YD_STK_LYR_MTL_STAT = 'E'
					   AND A.YD_STK_LYR_ACT_STAT = 'E'
					   AND A.DEL_YN = 'N'
			    	 */
			    	
			    	RsResultBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedAnalysisRbA", logId, methodNm, "Traverser 가능BED 조회");
					if (RsResultBed.size() <= 0) {
						szLogMsg =  LocalmethodNm + "해당열 [" +szYS_DN_WO_LOC +"] 검색  실패 ";
						commUtils.printLog(logId, szLogMsg, "SL");
						return recRtn;
					}
//					break;
				}
//			}
				
		}	
		//----------------------------------------------------------------------------------------------------------------------
		// 권하지시위치 수정
		//----------------------------------------------------------------------------------------------------------------------
		szLogMsg =  LocalmethodNm + " 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]을 크레인스케줄에 수정 시작";
		commUtils.printLog(logId, szLogMsg, "SL");
	
		JDTORecordSet RsBedUpXy = JDTORecordFactory.getInstance().createRecordSet("");
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6)); //권상지시위치
		recInBed.setField("YS_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));	 //권상지시위치
			
		/* Bed정보 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed 
		SELECT YS_STK_COL_GP 
		      ,YS_STK_BED_NO 
		      ,YD_STR_GTR_CD 
		      ,YS_STK_BED_TP 
		      ,YS_STK_BED_T_GP 
		      ,YS_STK_BED_W_GP 
		      ,YS_STK_BED_L_GP 
		      ,YS_OUTDIA_GRP_GP
		      ,YD_STK_BED_DIR_GP 
		      ,YD_STK_BED_ACT_STAT 
		      ,YD_STK_BED_WHIO_STAT 
		      ,YD_STK_BED_USG_GP
		      ,YD_STK_BED_XAXIS 
		      ,YD_STK_BED_YAXIS
		      ,YD_STK_BED_ZAXIS
		      ,YD_STK_BED_LYR_MAX
		      ,YD_STK_BED_WT_MAX 
		      ,YD_STK_BED_H_MAX 
		      ,YD_STK_BED_L_MAX 
		      ,YD_STK_BED_W_MAX 
		      ,YD_STK_BED_XAXIS_TOL 
		      ,YD_STK_BED_YAXIS_TOL 
		      , (SELECT YD_STK_COL_DIR_GP FROM TB_YS_STKCOL WHERE YS_STK_COL_GP = A.YS_STK_COL_GP AND ROWNUM = 1) AS YD_STK_COL_DIR_GP
		  FROM TB_YS_STKBED
		 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
		   AND DEL_YN ='N'
			 */  
		RsBedUpXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권상 BED 좌표 조회");
		if (RsBedUpXy.size() <= 0) {
			szLogMsg =  LocalmethodNm+ "권상 BED 좌표 조회 검색 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			
		}
		RsBedUpXy.first();
		JDTORecord RecUpBedXy = RsBedUpXy.getRecord();
		
		JDTORecordSet RsDnBedXy = JDTORecordFactory.getInstance().createRecordSet("");
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP", 			szYS_DN_WO_LOC.substring(0, 6));										//권하지시위치
		recInBed.setField("YS_STK_BED_NO", 			szYS_DN_WO_LOC.substring(6));										//권하지시위치
			
		
		/* Bed정보 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed 
		SELECT YS_STK_COL_GP 
		      ,YS_STK_BED_NO 
		      ,YD_STR_GTR_CD 
		      ,YS_STK_BED_TP 
		      ,YS_STK_BED_T_GP 
		      ,YS_STK_BED_W_GP 
		      ,YS_STK_BED_L_GP 
		      ,YS_OUTDIA_GRP_GP
		      ,YD_STK_BED_DIR_GP 
		      ,YD_STK_BED_ACT_STAT 
		      ,YD_STK_BED_WHIO_STAT 
		      ,YD_STK_BED_USG_GP
		      ,YD_STK_BED_XAXIS 
		      ,YD_STK_BED_YAXIS
		      ,YD_STK_BED_ZAXIS
		      ,YD_STK_BED_LYR_MAX
		      ,YD_STK_BED_WT_MAX 
		      ,YD_STK_BED_H_MAX 
		      ,YD_STK_BED_L_MAX 
		      ,YD_STK_BED_W_MAX 
		      ,YD_STK_BED_XAXIS_TOL 
		      ,YD_STK_BED_YAXIS_TOL 
		  FROM TB_YS_STKBED
		 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
		   AND DEL_YN ='N'
			 */  
		RsDnBedXy = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권하 BED 좌표 조회");
		if (RsDnBedXy.size() <= 0) {
			szLogMsg =  LocalmethodNm + "권하 BED 좌표 검색 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return recRtn;
			
		}
		RsDnBedXy.first();
		JDTORecord RecDnBedXy = RsDnBedXy.getRecord();
		
		JDTORecord recUpCrnSch = JDTORecordFactory.getInstance().create();
		recUpCrnSch.setField("YD_CRN_SCH_ID", 				szYD_CRN_SCH_ID);										//크레인스케줄ID
		

		//권상정보   					
		recUpCrnSch.setField("YS_UP_WO_LOC", 				szYS_UP_WO_LOC);										//권상지시위치
		recUpCrnSch.setField("YS_UP_WO_LAYER", 				szYS_UP_WO_LAYER);										//권상지시단
		recUpCrnSch.setField("YD_UP_STK_COL_GP", 			szYS_UP_WO_LOC.substring(0, 6));						//권상지시위치 - 적치열
		recUpCrnSch.setField("YD_UP_STK_BED_NO", 			szYS_UP_WO_LOC.substring(6));							//권상지시위치 - 적치베드

		/**********************************************************
		* 1. 저장열 방향구분 = “주행방향” 이고 Crane =“ACR1” or “ACR2” or “BCR1” or “BCR2” 이면,
		*   ->  X 좌표값  = 기준값 + (제품길이 /2)::::Y 좌표값  = 기준값 
		* 2. 저장열 방향구분 = “횡행방향” 이고 Crane =“ACR1” or “ACR2” or “BCR1” or “BCR2” 이면,
		*   ->  X 기준값  = 기준값                           ::::Y 좌표값  = 기준값 + (제품길이 /2)
		**********************************************************/
		if(szYS_UP_WO_LOC.substring(2, 4).equals("TR")||szYS_UP_WO_LOC.substring(2, 4).equals("TC")||szYS_UP_WO_LOC.substring(2, 4).equals("TS")) {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
			
		} else if(commUtils.trim(RecUpBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("X") && szYD_EQP_ID.substring(2, 4).equals("CR")) {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecUpBedXy,"YD_STK_BED_XAXIS")
					                                                     +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
			
		} else if(commUtils.trim(RecUpBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("Y") && szYD_EQP_ID.substring(2, 4).equals("CR")) {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecUpBedXy,"YD_STK_BED_YAXIS")
																		 +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
		} else {
			recUpCrnSch.setField("YD_UP_WO_LOC_XAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
			recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
		}
		
		recUpCrnSch.setField("YD_UP_WO_LOC_ZAXIS",  		commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
		recUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MAX",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
		recUpCrnSch.setField("YD_UP_WO_XAXIS_GAP_MIN",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
		recUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MAX",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
		recUpCrnSch.setField("YD_UP_WO_YAXIS_GAP_MIN",  	commUtils.trim(RecUpBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
		recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS1",  		"" ) ;
		recUpCrnSch.setField("YD_UP_WO_LOC_YAXIS2",  		"" ) ;
		recUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MAX",  	"" ) ;
		recUpCrnSch.setField("YD_UP_WO_ZAXIS_GAP_MIN",  	"" ) ;
		//권하정보   					
		recUpCrnSch.setField("YS_DN_WO_LOC", 				szYS_DN_WO_LOC);										//권하지시위치
		recUpCrnSch.setField("YS_DN_WO_LAYER", 				szYS_DN_WO_LAYER);										//권하지시단
		recUpCrnSch.setField("YD_DN_STK_COL_GP", 			szYS_DN_WO_LOC.substring(0, 6));						//권하지시위치 - 적치열
		recUpCrnSch.setField("YD_DN_STK_BED_NO", 			szYS_DN_WO_LOC.substring(6));							//권하지시위치 - 적치베드

		if(szYS_DN_WO_LOC.substring(2, 4).equals("TR")||szYS_DN_WO_LOC.substring(2, 4).equals("TC")||szYS_DN_WO_LOC.substring(2, 4).equals("TS")) {
			recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
			
		} else if(commUtils.trim(RecDnBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("X") && szYD_EQP_ID.substring(2, 4).equals("CR")) {
			recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecDnBedXy,"YD_STK_BED_XAXIS")
																		 +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
			
		} else if(commUtils.trim(RecDnBedXy.getFieldString("YD_STK_COL_DIR_GP")).equals("Y") && szYD_EQP_ID.substring(2, 4).equals("CR")) {
			recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;

			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",      String.valueOf(commUtils.paraRecChkNullInt(RecDnBedXy,"YD_STK_BED_YAXIS")
																		 +(Integer.parseInt(szYD_EQP_WRK_MAX_L) /2))) ;
		} else {
			recUpCrnSch.setField("YD_DN_WO_LOC_XAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS"  ))) ;
			recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS"  ))) ;
		}

		recUpCrnSch.setField("YD_DN_WO_LOC_ZAXIS",  		commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_ZAXIS"  )) ) ;
		recUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MAX",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
		recUpCrnSch.setField("YD_DN_WO_XAXIS_GAP_MIN",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_XAXIS_TOL"  )) ) ;
		recUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MAX",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
		recUpCrnSch.setField("YD_DN_WO_YAXIS_GAP_MIN",  	commUtils.trim(RecDnBedXy.getFieldString("YD_STK_BED_YAXIS_TOL"  )) ) ;
		recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS1",  		"" ) ;
		recUpCrnSch.setField("YD_DN_WO_LOC_YAXIS2",  		"" ) ;
		recUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MAX",  	"" ) ;
		recUpCrnSch.setField("YD_DN_WO_ZAXIS_GAP_MIN",  	"" ) ;


		//기타   					
		recUpCrnSch.setField("YD_EQP_WRK_SH", 				String.valueOf(intYD_EQP_WRK_SH));						//크레인작업재료 총매수
		recUpCrnSch.setField("YD_EQP_WRK_WT", 				String.valueOf(intYD_EQP_WRK_WT));						//크레인작업재료 총중량
		recUpCrnSch.setField("YD_EQP_WRK_T", 				String.valueOf(dblYD_EQP_WRK_T));						//크레인작업재료 총높이
		recUpCrnSch.setField("YD_EQP_WRK_MAX_W", 			szYD_EQP_WRK_MAX_W);									//크레인작업재료 중 최대 폭
		recUpCrnSch.setField("YD_EQP_WRK_MAX_L", 			szYD_EQP_WRK_MAX_L);									//크레인작업재료 중 최대 길이
		recUpCrnSch.setField("MODIFIER", 					"LocSetRbA");
	
	
		int intRtnVal = commDao.update(recUpCrnSch, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCrnWrkSidedelyn", logId, methodNm, "크레인스케쥴 갱신");
		
		if(intRtnVal <= 0) {
			szLogMsg =  LocalmethodNm + " 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단[" +szYS_DN_WO_LAYER +" ]을 크레인스케줄에 수정 중 ERROR 발생";
			commUtils.printLog(logId, szLogMsg, "SL");
			return recRtn;
		}		
		
    	szLogMsg =  LocalmethodNm + " 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]을 크레인스케줄에 수정 완료";
		commUtils.printLog(logId, szLogMsg, "SL");

		//----------------------------------------------------------------------------------------------------------------------
		//	권하지시위치에 재료를 권하대기로 등록
		//         --->  권상위치 정보 READ 하여 권하 위치 SET
		//----------------------------------------------------------------------------------------------------------------------
    	szLogMsg =  LocalmethodNm + " 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]에 크레인작업재료 등록 시작";
		commUtils.printLog(logId, szLogMsg, "SL");

		JDTORecordSet rsOutBed = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsMaxSeq = JDTORecordFactory.getInstance().createRecordSet("");
		
		JDTORecord resOutBed= JDTORecordFactory.getInstance().create();
		recInBed= JDTORecordFactory.getInstance().create();
		recInBed.setField("YS_STK_COL_GP",	szYS_UP_WO_LOC.substring(0, 6));	
		recInBed.setField("YD_WBOOK_ID", 	szYD_WBOOK_ID);	
		recInBed.setField("YD_CRN_SCH_ID", 	szYD_CRN_SCH_ID);
		/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrByStlNoStockRbA 
		SELECT A.YS_STK_COL_GP            AS YS_STK_COL_GP
		     , A.YS_STK_BED_NO            AS YS_STK_BED_NO
		     , A.YS_STK_LYR_NO            AS YS_STK_LYR_NO
		     , A.YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
		     , A.SSTL_NO                   AS SSTL_NO
		     , B.YD_STK_LOT_TP
		     , B.YD_STK_LOT_CD
		     , B.HCR_GP
		     , B.STL_PROG_CD
		     , B.YS_MTL_ITEM
		  FROM TB_YS_STKLYR A
		     , TB_YS_STOCK B 
		     , (SELECT YD_WBOOK_ID    AS YD_WBOOK_ID                                                       
		              ,YS_STK_COL_GP  AS YS_STK_COL_GP                                                     
		              ,YS_STK_BED_NO  AS YS_STK_BED_NO                                                     
		              ,YS_STK_LYR_NO  AS YS_STK_LYR_NO                                                     
		              ,YS_STK_SEQ_NO  AS YS_STK_SEQ_NO                                                     
		              ,YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ                                                    
		              ,SSTL_NO         AS SSTL_NO
		              ,REG_DDTT       AS REG_DDTT
		         FROM TB_YS_WRKBOOKMTL                                                                      
		        WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID  
		        ) C    
		     , TB_YS_CRNWRKMTL D  
		 WHERE A.SSTL_NO = B.SSTL_NO
		   AND A.SSTL_NO = C.SSTL_NO
		   AND A.SSTL_NO = D.SSTL_NO
		   AND D.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
		   AND A.YS_STK_COL_GP = :V_YS_STK_COL_GP
		   AND A.SSTL_NO IS NOT NULL 
		   AND A.DEL_YN = 'N'
		ORDER BY YS_STK_COL_GP, CASE WHEN YS_STK_COL_GP IN ('KAPC02') THEN  YS_STK_BED_NO ELSE '0' END  DESC
		                      , CASE WHEN YS_STK_COL_GP IN ('KAPC02') THEN  '0'           ELSE YS_STK_BED_NO END 
		                      , YS_STK_SEQ_NO
		*/
		
		rsOutBed = commDao.select(recInBed, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToLyrByStlNoStockRbA", logId, methodNm, "권상정보 검색");
		if (rsOutBed.size() <= 0) {
			szLogMsg =  LocalmethodNm + "권상정보 BED 검색 실패 ";
			commUtils.printLog(logId, szLogMsg, "SL");
			return recRtn;
		}	
		
		JDTORecord jrParam= JDTORecordFactory.getInstance().create();
		//적치단의 재료상태를 권하대기로 변경
		String szYS_STK_SEQ_NO ="";
		for(int i = 1; i <= rsOutBed.size(); i++) {
			rsOutBed.absolute(i);
			resOutBed  = rsOutBed.getRecord();
		
			jrParam.setField("YS_STK_COL_GP", 	szYS_DN_WO_LOC.substring(0, 6));
			jrParam.setField("YS_STK_BED_NO", 	szYS_DN_WO_LOC.substring(6));
			jrParam.setField("YS_STK_LYR_NO", 	commUtils.stringPlusInt(szYS_DN_WO_LAYER,0));
			jrParam.setField("SSTL_NO",       	commUtils.trim(resOutBed.getFieldString("SSTL_NO"  )));
			jrParam.setField("YD_STK_LYR_MTL_STAT", "D");
	
			// TY 대상  --> TY 에 기존 DATA CLEAR
			if(sTO_BAY.equals("B")) {
				/* 적치단 재료번호 삭제 - com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClrMtl_D 

				UPDATE TB_YS_STKLYR
				   SET MODIFIER            = :V_MODIFIER
				      ,MOD_DDTT            = SYSDATE
				      ,SSTL_NO              = NULL
				      ,YD_STK_LYR_ACT_STAT = 'E'
				      ,YD_STK_LYR_MTL_STAT = 'E'
				 WHERE YS_STK_COL_GP    LIKE 'KATY%'
				   AND YD_STK_LYR_MTL_STAT = 'D'
				   AND SSTL_NO             = :V_SSTL_NO
				*/   
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClrMtl_D", logId, methodNm, "TB_YS_STKLYR 갱신");
			}	
			
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn 
			SELECT NVL(MAX(YS_STK_SEQ_NO),0) + 1 AS MAX_YS_STK_SEQ_NO 
			    FROM TB_YS_STKLYR
			   WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
			     AND YS_STK_BED_NO = :V_YS_STK_BED_NO 
			     AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
			     AND SSTL_NO IS NOT NULL
			 */    
			rsMaxSeq = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrToDn", logId, methodNm, "권하정보 검색");
			rsMaxSeq.first();
			recPara = rsMaxSeq.getRecord();
			
			szYS_STK_SEQ_NO = commUtils.trim(recPara.getFieldString("MAX_YS_STK_SEQ_NO"  ));
			jrParam.setField("YS_STK_SEQ_NO", 	 szYS_STK_SEQ_NO); 
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGp  
	    	UPDATE TB_YS_STKLYR            
	    	   SET MOD_DDTT     = SYSDATE             
	    		 , MODIFIER     = :V_MODIFIER             
	    		 , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)
	    	     , SSTL_NO = NVL(:V_SSTL_NO,SSTL_NO)
	    	     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)
	    	 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	    	   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
	    	   AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO   
	    	   AND YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO   
	    	 */  
			intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColBedGp", logId, methodNm, "TB_YS_STKLYR 갱신");
		
			//저장품에 등록할 위치
			if(intRtnVal <= 0) {
				commUtils.printLog(logId,  LocalmethodNm + " 적치단[" + jrParam.getFieldString("YS_STK_COL_GP") + "]활성화중 ERROR 발생", "SL");
				return recRtn;
			}
			
			/**********************************************************
			* 입고 예정 위치 SET (저장품 입고예정위치 수정)
			**********************************************************/
			
			szLogMsg =  LocalmethodNm + "저장품 입고예정위치 수정 안함 "; // 20209011 박비오 (작업선택 취소시 저장위치가 변경되지 않도록 수정)
			commUtils.printLog(logId, szLogMsg, "SL");
			
/*			JDTORecord jrParam1= JDTORecordFactory.getInstance().create();
 *			jrParam1= JDTORecordFactory.getInstance().create();
 *			jrParam1.setResultCode(logId);	//Log ID
 *			jrParam1.setResultMsg(methodNm);	//Log Method Name			
 *			jrParam1.setField("SSTL_NO"				,commUtils.trim(resOutBed.getFieldString("SSTL_NO"  )));
 *			jrParam1.setField("YD_RCPT_PLN_STR_LOC"	,szYS_STK_COL_GP + szYS_STK_BED_NO +szYS_STK_LYR_NO + szYS_STK_SEQ_NO );
 *			try {		
 *				EJBConnector tranConn = new EJBConnector("default", "GdsYsSchSeEJB", this);
 *				tranConn.trx("updStock", new Class[] { JDTORecord.class }, new Object[] { jrParam1 });
 *			//----------------------------------------------------------------------------------------------------------------------
 *			} catch (DAOException e) {
 *				throw e;
 *			} catch (Exception e) {
 *				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
 *			}
 */
		}	
		
		recRtn.setField("YD_RCPT_PLN_STR_LOC", szYS_STK_COL_GP + szYS_STK_BED_NO +szYS_STK_LYR_NO + szYS_STK_SEQ_NO);	
		recRtn.setField("YS_DN_WO_LOC", szYS_DN_WO_LOC);
		recRtn.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
		recRtn.setField("TCAR_SND_YN", szTcarSndyn);
		
		szLogMsg =  LocalmethodNm + " 권하지시위치["+szYS_DN_WO_LOC+"], 권하지시단["+szYS_DN_WO_LAYER+"]에 크레인작업재료 등록 완료 - 메세지 : ";
		commUtils.printLog(logId, szLogMsg, "SL");
		
		commUtils.printLog(logId, methodNm, "S-");
		//----------------------------------------------------------------------------------------------------------------------
    	// ERROR 발생시 ?
		//----------------------------------------------------------------------------------------------------------------------
		return recRtn;
	}
	
	/**
	 *      [A] 오퍼레이션명 : L2 저장위치이력정보수신(N7YSL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL001(JDTORecord rcvMsg) throws DAOException {

		String methodNm = "L2 저장위치이력정보수신[GdsYsL2RcvSeEJB.rcvN7YSL001]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		String sRemarks = "";
		
		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); 
			
			int sStlSh					= rcvMsg.getFieldInt("YD_STK_BED_STL_SH"); 						//재료매수
			String sYD_GP 				= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 				//야드구분
			String sYD_BAY_GP 			= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP")); 			//야드동구분
			String sYD_EQP_GP 			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP")); 			//야드설비구분
			String sYS_STK_COL_NO 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO")); 		//특수강야드적치열번호
			String sYS_STK_BED_NO 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")); 		//특수강야드적치Bed번호
			String sYS_STK_LYR_NO 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_LYR_NO")); 		//특수강야드적치단번호
			String sYS_STK_SEQ_NO1 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO1")); 		//특수강야드적치Seq번호1
			String sSSTL_NO1 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO1")); 			//특수강재료번호1
			String sYS_STK_SEQ_NO2 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO2")); 		//특수강야드적치Seq번호2
			String sSSTL_NO2 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO2")); 			//특수강재료번호2
			String sYS_STK_SEQ_NO3 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO3")); 		//특수강야드적치Seq번호3
			String sSSTL_NO3 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO3")); 			//특수강재료번호3
			String sYS_STK_SEQ_NO4 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO4")); 		//특수강야드적치Seq번호4
			String sSSTL_NO4 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO4")); 			//특수강재료번호4
			String sYS_STK_SEQ_NO5 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO5")); 		//특수강야드적치Seq번호5
			String sSSTL_NO5 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO5")); 			//특수강재료번호5
			String sYS_STK_SEQ_NO6 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO6")); 		//특수강야드적치Seq번호6
			String sSSTL_NO6 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO6")); 			//특수강재료번호6
			String sYS_STK_SEQ_NO7 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO7")); 		//특수강야드적치Seq번호7
			String sSSTL_NO7 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO7")); 			//특수강재료번호7
			
			String modifier      = "N7YSL001";  //수정자
			String sErrType		 = "";
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 0. 파라메터 설정 
			**********************************************************/
			
			sYD_GP = "G"; 
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_GP"          	, sYD_GP        	); //야드구분
			jrParam.setField("YD_BAY_GP"  		, sYD_BAY_GP 		); //야드동구분
			jrParam.setField("YD_EQP_GP"		, sYD_EQP_GP		); //야드설비구분
			jrParam.setField("YS_STK_COL_NO"    , sYS_STK_COL_NO	); //특수강야드적치열번호
			jrParam.setField("YS_STK_BED_NO"    , sYS_STK_BED_NO    ); //특수강야드적치Bed번호
			jrParam.setField("YS_STK_LYR_NO"    , sYS_STK_LYR_NO    ); //특수강야드적치단번호
			jrParam.setField("YS_STK_SEQ_NO1"   , sYS_STK_SEQ_NO1   ); //특수강야드적치Seq번호1
			jrParam.setField("SSTL_NO1"    		, sSSTL_NO1    		); //특수강재료번호1
			jrParam.setField("YS_STK_SEQ_NO2"   , sYS_STK_SEQ_NO2   ); //특수강야드적치Seq번호2
			jrParam.setField("SSTL_NO2"    		, sSSTL_NO2    		); //특수강재료번호2
			jrParam.setField("YS_STK_SEQ_NO3"   , sYS_STK_SEQ_NO3   ); //특수강야드적치Seq번호3
			jrParam.setField("SSTL_NO3"    		, sSSTL_NO3    		); //특수강재료번호3
			jrParam.setField("YS_STK_SEQ_NO4"   , sYS_STK_SEQ_NO4   ); //특수강야드적치Seq번호4
			jrParam.setField("SSTL_NO4"    		, sSSTL_NO4    		); //특수강재료번호4
			jrParam.setField("YS_STK_SEQ_NO5"   , sYS_STK_SEQ_NO5   ); //특수강야드적치Seq번호5
			jrParam.setField("SSTL_NO5"    		, sSSTL_NO5    		); //특수강재료번호5
			jrParam.setField("YS_STK_SEQ_NO6"   , sYS_STK_SEQ_NO6   ); //특수강야드적치Seq번호6
			jrParam.setField("SSTL_NO6"    		, sSSTL_NO6    		); //특수강재료번호6
			jrParam.setField("YS_STK_SEQ_NO7"   , sYS_STK_SEQ_NO7   ); //특수강야드적치Seq번호7
			jrParam.setField("SSTL_NO7"    		, sSSTL_NO7    		); //특수강재료번호7
			jrParam.setField("MODIFIER"         , modifier       ); //수정자
			jrParam.setField("YS_STR_LOC"         , sYD_GP+sYD_BAY_GP+sYD_EQP_GP+sYS_STK_COL_NO+sYS_STK_BED_NO+sYS_STK_LYR_NO+sYS_STK_SEQ_NO1); //저장위치
			

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sYD_GP)) {
				sRemarks = "ER01 야드구분(YD_GP)이 빈 값입니다!"; 
				sErrType = "1";
			} else if ("".equals(sYD_BAY_GP)) {
				sRemarks = "ER02 야드동구분(YD_BAY_GP)이 빈 값입니다!"; 
				sErrType = "1";
			} else if ("".equals(sYD_EQP_GP)) {
				sRemarks = "ER03 야드설비구분(YD_EQP_GP)이 빈 값입니다!"; 
				sErrType = "1";
			} else if ("".equals(sYS_STK_COL_NO)) {
				sRemarks = "ER04 특수강야드적치열번호(YS_STK_COL_NO)가 빈 값입니다!"; 
				sErrType = "1";
			} else if ("".equals(sYS_STK_BED_NO)) {
				sRemarks = "ER05 특수강야드적치Bed번호(YS_STK_BED_NO)가 빈 값입니다!"; 
				sErrType = "1";
			} else if ("".equals(sYS_STK_LYR_NO)) {
				sRemarks = "ER06특수강야드적치단번호(YS_STK_LYR_NO)가 빈 값입니다!"; 
				sErrType = "1";
			} else if (sYD_GP.length() != 1) {
				sRemarks = "ER07 야드구분(YD_GP) 자리수가 틀립니다!"; 
				sErrType = "1";
			} else if (sYD_BAY_GP.length() != 1) {
				sRemarks = "ER08 야드동구분(YD_BAY_GP) 자리수가 틀립니다!"; 
				sErrType = "1";
			} else if (sYD_EQP_GP.length() != 2) {
				sRemarks = "ER09 야드설비구분(YD_EQP_GP) 자리수가 틀립니다!"; 
				sErrType = "1";
			} else if (sYS_STK_COL_NO.length() != 2) {
				sRemarks = "ER10 특수강야드적치열번호(YS_STK_COL_NO) 자리수가 틀립니다!"; 
				sErrType = "1";
			} else if (sYS_STK_BED_NO.length() != 2) {
				sRemarks = "ER11 특수강야드적치Bed번호(YS_STK_BED_NO) 자리수가 틀립니다!"; 
				sErrType = "1";
			} else if (sYS_STK_LYR_NO.length() != 2) {
				sRemarks = "ER12 특수강야드적치단번호(YS_STK_LYR_NO) 자리수가 틀립니다!"; 
				sErrType = "1";
			} 
			
			if("".equals(sRemarks)) {
				/**********************************************************
				* 2. BUNDLE 공통, BILLET 공통 UPDATE
				*    - 이전 위치가 서냉PIT(설비구분이 'SP')이고 현 위치가 서냉PIT가 
				*      아니면 서냉완료일자 SET -->Query 내에서 수행
				**********************************************************/
				
				//BILLET공통 수정** (서냉피트작업일시 셋팅)
				int iCnt1 = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updM6YSL102BilletComm", logId, methodNm, "BILLET공통 수정");
				
				//BUNDLE공통 수정** 
				int iCnt2 = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updM6YSL102BundleComm", logId, methodNm, "BUNDLE공통 수정");
				
				//봉강보류재 수정** 
				int iCnt3 = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updM6YSL102ShearHodWr", logId, methodNm, "봉강보류재 수정");
				
				if(sStlSh == 0 || iCnt1 + iCnt2 + iCnt3 == 0) {
					sRemarks = "ER88 Billet공통,Bundle공통에 수신된 SSTL_NO가 존재하지 않습니다!";
					sErrType = "2";
				}
				
				commUtils.printLog(logId, "sStlSh="+sStlSh, "S-");
				commUtils.printLog(logId, "(iCnt1+iCnt2+iCnt3)="+(iCnt1+iCnt2+iCnt3), "S-");
				commUtils.printLog(logId, "sRemarks="+sRemarks, "S-");
				commUtils.printLog(logId, "sErrType="+sErrType, "S-");
				
				//재공야드 인 경우 
//				if("G".equals(sYD_GP) && "B".equals(sYD_BAY_GP)){
//				
//					//서냉피트에 장입하는 경우 (위치,장입일시)
//					if("SP".equals(sYD_EQP_GP)){
//						
//						//TB_SB_A_BARWR 서냉피트 장입** (서냉피트 위치,장입일시 적용) 
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updM6YSL102BarwrChgDt", logId, methodNm, "TB_SB_A_BARWR (서냉피트 위치,장입일시)수정");
//					}else{
//					
//						//TB_SB_A_BARWR 서냉피트 추출** (서냉피트온도 적용) 
//						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updM6YSL102BarwrTmp", logId, methodNm, "TB_SB_A_BARWR (서냉피트 온도,추칠일시)수정");
//					}
//				}
			}
			
			//저장위치이력 테이블에 저장
			jrParam.setField("REMARKS"		, sRemarks  ); //비고 
			jrParam.setField("MODIFIER"		, logId     ); //수정자
			jrParam.setField("DEL_YN"		, "N"		);
			
			/**********************************************************
			* 2. 로그 Table정보 이력등록
			**********************************************************/
			if("".equals(sRemarks)) { //정상종료 
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "저장위치이력 등록");			
			} else { //Error 로그
				commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHistErr", logId, methodNm, "저장위치이력 Error등록");
				
				/*
				 * 2016.07.01 윤재광
				 * ERROR 발생시  L2로 응답전문 송신기능 추가.
				 */
				{
					String sTmpSStlNo 		= "";
					String sTmpYsStkSeqNo 	= "";
					
					for(int inx = 1; inx <= 7; inx++)
					{
						sTmpSStlNo 		= commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+inx));
						sTmpYsStkSeqNo 	= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO"+inx));
						
						if(!"".equals(sTmpSStlNo)){
							
							jrParam.setField("SSTL_NO"		, sTmpSStlNo );
							jrParam.setField("YS_STK_SEQ_NO", sTmpYsStkSeqNo );
							
							if("1".equals(sErrType)){
								
								jrParam.setField("MESSAGE", "YS_STR_LOC ERROR" );
								//전송할 전문에 추가
								jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN7L001", jrParam));
								
							}else if("2".equals(sErrType)){
								
								JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getBtBdCommonKeyCnt", logId, methodNm, "재료키존재여부체크");
								
								if (jsChk1 == null || jsChk1.size() == 0) {
									
									jrParam.setField("MESSAGE", "COMMON KEY NOT EXISTS" );
									//전송할 전문에 추가
									jrRtn = commUtils.addSndData(jrRtn,commDao.getMsgL2("YSN7L001", jrParam));
									
								} 
							}
						}
					}
				}
			}

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(methodNm + e.getMessage(), e);
		}	
		
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : L2 포항이송재 상차실적 수신(N7YSL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL004(JDTORecord rcvMsg) throws DAOException {

		String methodNm = "L2 포항이송재 상차실적 수신[GdsYsL2RcvSeEJB.rcvN7YSL004]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		String sRemarks = "";
		
		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); 
			
			int iStlSh			 = rcvMsg.getFieldInt("STL_SH"); //재료매수
			String sChkStlNo	 = "";
			String sChkDelYn	 = "";			
			String modifier      = "N7YSL004";  //수정자
			String sErrType		 = "";
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			jrYdMsg.setField("JMS_TC_CD"         , "YSPBJ003"); //JMSTC코드
			jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
			jrYdMsg.setField("CAR_NO"		  	 , commUtils.trim(rcvMsg.getFieldString("CAR_NO"))); //차량번호
			jrYdMsg.setField("BNDL_CNT"			 , iStlSh+""); //JMSTC생성일시
			
			for(int index = 0; index < iStlSh; index++){
    			
    			sChkStlNo =  commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(index+1))); 
    			sChkDelYn =  commUtils.trim(rcvMsg.getFieldString("DEL_YN"+(index+1))); 
    			
    			if("".equals(sChkStlNo)) break;
    			
    			jrParam = JDTORecordFactory.getInstance().create();
    			jrParam.setField("PH_FRTOMOVE_YN"   , sChkDelYn); //포항이송 상차여부
    			jrParam.setField("BNDL_NO"          , sChkStlNo); //번들번호
    			
    			//BUNDLE공통 수정** 
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updN7YSL004BundleComm", logId, methodNm, "BUNDLE공통 수정");
    			
    			jrYdMsg.setField("BNDL_NO"+(index+1)       , sChkStlNo); 
    			jrYdMsg.setField("DEL_YN"+(index+1)        , sChkDelYn); 
    		}
			
			//전송할 전문에 추가
			jrRtn = commUtils.addSndData(jrYdMsg);
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(methodNm + e.getMessage(), e);
		}	
		
	}
	
	/**
	 *      [A] 오퍼레이션명 : L2 저장위치이력정보수신(N7YSL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL001_bak(JDTORecord rcvMsg) throws DAOException {

		String methodNm = "L2 저장위치이력정보수시[GdsYsL2RcvSeEJB.rcvN7YSL001]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		String sRemarks = "";

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); 
			
			String sYD_GP 				= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 				//야드구분
			String sYD_BAY_GP 			= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP")); 			//야드동구분
			String sYD_EQP_GP 			= commUtils.trim(rcvMsg.getFieldString("YD_EQP_GP")); 			//야드설비구분
			String sYS_STK_COL_NO 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_COL_NO")); 		//특수강야드적치열번호
			String sYS_STK_BED_NO 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")); 		//특수강야드적치Bed번호
			String sYS_STK_LYR_NO 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_LYR_NO")); 		//특수강야드적치단번호
			String sYS_STK_SEQ_NO1 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO1")); 		//특수강야드적치Seq번호1
			String sSSTL_NO1 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO1")); 			//특수강재료번호1
			String sYS_STK_SEQ_NO2 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO2")); 		//특수강야드적치Seq번호2
			String sSSTL_NO2 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO2")); 			//특수강재료번호2
			String sYS_STK_SEQ_NO3 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO3")); 		//특수강야드적치Seq번호3
			String sSSTL_NO3 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO3")); 			//특수강재료번호3
			String sYS_STK_SEQ_NO4 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO4")); 		//특수강야드적치Seq번호4
			String sSSTL_NO4 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO4")); 			//특수강재료번호4
			String sYS_STK_SEQ_NO5 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO5")); 		//특수강야드적치Seq번호5
			String sSSTL_NO5 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO5")); 			//특수강재료번호5
			String sYS_STK_SEQ_NO6 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO6")); 		//특수강야드적치Seq번호6
			String sSSTL_NO6 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO6")); 			//특수강재료번호6
			String sYS_STK_SEQ_NO7 		= commUtils.trim(rcvMsg.getFieldString("YS_STK_SEQ_NO7")); 		//특수강야드적치Seq번호7
			String sSSTL_NO7 			= commUtils.trim(rcvMsg.getFieldString("SSTL_NO7")); 			//특수강재료번호7
			
			String modifier      = "N7YSL001";  //수정자
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 0. 파라메터 설정 
			**********************************************************/
			
			sYD_GP = "G"; 
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YD_GP"          	, sYD_GP        	); //야드구분
			jrParam.setField("YD_BAY_GP"  		, sYD_BAY_GP 		); //야드동구분
			jrParam.setField("YD_EQP_GP"		, sYD_EQP_GP		); //야드설비구분
			jrParam.setField("YS_STK_COL_NO"    , sYS_STK_COL_NO	); //특수강야드적치열번호
			jrParam.setField("YS_STK_BED_NO"    , sYS_STK_BED_NO    ); //특수강야드적치Bed번호
			jrParam.setField("YS_STK_LYR_NO"    , sYS_STK_LYR_NO    ); //특수강야드적치단번호
			jrParam.setField("YS_STK_SEQ_NO1"   , sYS_STK_SEQ_NO1   ); //특수강야드적치Seq번호1
			jrParam.setField("SSTL_NO1"    		, sSSTL_NO1    		); //특수강재료번호1
			jrParam.setField("YS_STK_SEQ_NO2"   , sYS_STK_SEQ_NO2   ); //특수강야드적치Seq번호2
			jrParam.setField("SSTL_NO2"    		, sSSTL_NO2    		); //특수강재료번호2
			jrParam.setField("YS_STK_SEQ_NO3"   , sYS_STK_SEQ_NO3   ); //특수강야드적치Seq번호3
			jrParam.setField("SSTL_NO3"    		, sSSTL_NO3    		); //특수강재료번호3
			jrParam.setField("YS_STK_SEQ_NO4"   , sYS_STK_SEQ_NO4   ); //특수강야드적치Seq번호4
			jrParam.setField("SSTL_NO4"    		, sSSTL_NO4    		); //특수강재료번호4
			jrParam.setField("YS_STK_SEQ_NO5"   , sYS_STK_SEQ_NO5   ); //특수강야드적치Seq번호5
			jrParam.setField("SSTL_NO5"    		, sSSTL_NO5    		); //특수강재료번호5
			jrParam.setField("YS_STK_SEQ_NO6"   , sYS_STK_SEQ_NO6   ); //특수강야드적치Seq번호6
			jrParam.setField("SSTL_NO6"    		, sSSTL_NO6    		); //특수강재료번호6
			jrParam.setField("YS_STK_SEQ_NO7"   , sYS_STK_SEQ_NO7   ); //특수강야드적치Seq번호7
			jrParam.setField("SSTL_NO7"    		, sSSTL_NO7    		); //특수강재료번호7
			jrParam.setField("MODIFIER"         , modifier       ); //수정자
			

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sYD_GP)) {
				sRemarks = "ER01 야드구분(YD_GP)이 빈 값입니다!";
			} else if ("".equals(sYD_BAY_GP)) {
				sRemarks = "ER02 야드동구분(YD_BAY_GP)이 빈 값입니다!";
			} else if ("".equals(sYD_EQP_GP)) {
				sRemarks = "ER03 야드설비구분(YD_EQP_GP)이 빈 값입니다!";
			} else if ("".equals(sYS_STK_COL_NO)) {
				sRemarks = "ER04 특수강야드적치열번호(YS_STK_COL_NO)가 빈 값입니다!";
			} else if ("".equals(sYS_STK_BED_NO)) {
				sRemarks = "ER05 특수강야드적치Bed번호(YS_STK_BED_NO)가 빈 값입니다!";
			} else if ("".equals(sYS_STK_LYR_NO)) {
				sRemarks = "ER06특수강야드적치단번호(YS_STK_LYR_NO)가 빈 값입니다!";
			} else if (sYD_GP.length() != 1) {
				sRemarks = "ER07 야드구분(YD_GP) 자리수가 틀립니다!";
			} else if (sYD_BAY_GP.length() != 1) {
				sRemarks = "ER08 야드동구분(YD_BAY_GP) 자리수가 틀립니다!";
			} else if (sYD_EQP_GP.length() != 2) {
				sRemarks = "ER09 야드설비구분(YD_EQP_GP) 자리수가 틀립니다!";
			} else if (sYS_STK_COL_NO.length() != 2) {
				sRemarks = "ER10 특수강야드적치열번호(YS_STK_COL_NO) 자리수가 틀립니다!";
			} else if (sYS_STK_BED_NO.length() != 2) {
				sRemarks = "ER11 특수강야드적치Bed번호(YS_STK_BED_NO) 자리수가 틀립니다!";
			} else if (sYS_STK_LYR_NO.length() != 2) {
				sRemarks = "ER12 특수강야드적치단번호(YS_STK_LYR_NO) 자리수가 틀립니다!";
			} 
			
			if(!"".equals(sRemarks)) {
				throw new Exception(sRemarks);
			}

			/**********************************************************
			* 2. BUNDLE 공통, BILLET 공통 UPDATE
			*    - 이전 위치가 서냉PIT(설비구분이 'SP')이고 현 위치가 서냉PIT가 
			*      아니면 서냉완료일자 SET -->Query 내에서 수행
			**********************************************************/

			
			//BILLET공통 수정** (서냉피트작업일시 셋팅)
			int iCnt1 = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updM6YSL102BilletComm", logId, methodNm, "BILLET공통 수정");
			
			//BUNDLE공통 수정** 
			int iCnt2 = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updM6YSL102BundleComm", logId, methodNm, "BUNDLE공통 수정");
			
			if(iCnt1 + iCnt2 <= 0) {
				sRemarks = "ER88 Billet공통,Bundle공통에 수신된 SSTL_NO가 존재하지 않습니다!";
				throw new Exception(sRemarks);
			}
			

			/**********************************************************
			* 3. 종료
			**********************************************************/
			JDTORecord jrRtn = null;

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (DAOException e) {
			sRemarks = "ER98 DAOException!";
			throw e;
		} catch (Exception e) {
			if("".equals(sRemarks)) {
				sRemarks = "ER99 Exception!";
			}
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		} finally {
			try {
				
				//저장위치이력 테이블에 저장
				jrParam.setField("REMARKS"		, sRemarks  ); //비고 
				jrParam.setField("MODIFIER"		, logId     ); //수정자
				jrParam.setField("DEL_YN"		, "N"		);
				
//				if("".equals(sRemarks)) { //정상종료 
//					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "저장위치이력 등록");			
//				} else { //Error 로그
//					commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHistErr", logId, methodNm, "저장위치이력 Error등록");			
//				}
				
				EJBConnector ejbConn 		= null;
				ejbConn = new EJBConnector("default", "GdsYsL2RcvSeEJB", this);
				ejbConn.trx("rcvStrLocHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
 
 
		 
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}
		}
		
	}
 
	
	
	 /**
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inParam
	 * @return
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvStrLocHist(JDTORecord rcvMsg) throws DAOException {

		String methodNm = "L2 저장위치이력정보수시2[GdsYsL2RcvSeEJB.rcvStrLocHist] < " + rcvMsg.getResultMsg();
 
		String sRemarks 			= "";
		String logId	 			= ""; 
		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create(); // 
		try {
			sRemarks=commUtils.trim(rcvMsg.getFieldString("REMARKS")); 
			logId	=commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 
			
			
			if("".equals(sRemarks)) { //정상종료 
				commDao.insert(rcvMsg, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHist", logId, methodNm, "저장위치이력 등록");			
			} else { //Error 로그
				commDao.insert(rcvMsg, "com.inisteel.cim.ys.common.dao.YsCommDAO.insStrLocHistErr", logId, methodNm, "저장위치이력 Error등록");			
			}
 
			outRecord.setField("RTN_MSG"	 , "저장위치이력 등록 완료");	
			return outRecord;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(methodNm + e.getMessage(), e);
		}
	} // end of WrkBookInsertProcTX
	
	
	/**
	 *      [A] 오퍼레이션명 : L2 특수강물류일일현황수신(N7YSL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN7YSL002(JDTORecord rcvMsg) throws DAOException {

		String methodNm = "L2 특수강물류일일현황수신[GdsYsL2RcvSeEJB.rcvN7YSL002]] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		String sRemarks = "";
		
		JDTORecord jrRtn = null;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId         = commUtils.getMsgId(rcvMsg); 
			
			String sINV_DD 				= commUtils.trim(rcvMsg.getFieldString("INV_DD")); 			//재고일자
			String sSTL_APPEAR_GP 		= commUtils.trim(rcvMsg.getFieldString("STL_APPEAR_GP")); 	//재료외형구분
			String sINV_GP 				= commUtils.trim(rcvMsg.getFieldString("INV_GP")); 			//재고구분
			String sSORT_SEQ 			= commUtils.trim(rcvMsg.getFieldString("SORT_SEQ")); 		//Sort순서 
			String sSYS_GP				= commUtils.trim(rcvMsg.getFieldString("SYS_GP")); 			//시스템구분
			String sYD_GP 				= commUtils.trim(rcvMsg.getFieldString("YD_GP")); 			//야드구분
			String sYD_BAY_GP 			= commUtils.trim(rcvMsg.getFieldString("YD_BAY_GP")); 		//야드동구분
			String sPRD_WT 				= commUtils.trim(rcvMsg.getFieldString("PRD_WT")); 			//생산중량
			String sTAKEIN_WT			= commUtils.trim(rcvMsg.getFieldString("TAKEIN_WT")); 		//반입중량
			String sTODAY_TKOUT_WT 		= commUtils.trim(rcvMsg.getFieldString("TODAY_TKOUT_WT")); 	//금일반출중량
			String sHYUN_JEGO_WGT		= commUtils.trim(rcvMsg.getFieldString("HYUN_JEGO_WGT")); 	//현재고
			String sBASE_CAPA			= commUtils.trim(rcvMsg.getFieldString("BASE_CAPA")); 		//기본능력 
			
			String modifier      = "N7YSL002";  //수정자
			String sErrType		 = "";
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			/**********************************************************
			* 0. 파라메터 설정 
			**********************************************************/
			  
			
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("INV_DD"          	, sINV_DD ); 		//재고일자
			jrParam.setField("STL_APPEAR_GP"  	, sSTL_APPEAR_GP); 	//재료외형구분
			jrParam.setField("INV_GP"			, sINV_GP); 		//재고구분
			jrParam.setField("SORT_SEQ"    		, sSORT_SEQ	); 		//Sort순서
			jrParam.setField("SYS_GP"    		, sSYS_GP); 		//시스템구분
			jrParam.setField("YD_GP"    		, sYD_GP); 			//야드구분
			jrParam.setField("YD_BAY_GP"   		, sYD_BAY_GP); 		//야드동구분
			jrParam.setField("PRD_WT"    		, sPRD_WT); 		//생산중량
			jrParam.setField("TAKEIN_WT"   		, sTAKEIN_WT); 		//반입중량
			jrParam.setField("TODAY_TKOUT_WT" 	, sTODAY_TKOUT_WT); //금일반출중량
			jrParam.setField("HYUN_JEGO_WGT"  	, sHYUN_JEGO_WGT); 	//현재고
			jrParam.setField("BASE_CAPA"    	, sBASE_CAPA); 		//기본능력 
			jrParam.setField("MODIFIER"         , modifier); 		//수정자 

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(sINV_DD)) {
				sRemarks = "ER01 재고일자(INV_DD)이 빈 값입니다!"; 
				sErrType = "1";
			} else if ("".equals(sSTL_APPEAR_GP)) {
				sRemarks = "ER02 재료외형구분(STL_APPEAR_GP)이 빈 값입니다!"; 
				sErrType = "1";
			} else if ("".equals(sINV_GP)) {
				sRemarks = "ER03 재고구분(INV_GP)이 빈 값입니다!"; 
				sErrType = "1";
			} 
 
				
			//블룸야드 일일 재공현황(TB_SB_Y_SRDAYSTKSUMHIST)
			int iCnt1 = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updsrdayStkSumHist", logId, methodNm, "블룸야드 일일 재공현황 수정");
	 
				 
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(methodNm + e.getMessage(), e);
		}	
		
	}
}
