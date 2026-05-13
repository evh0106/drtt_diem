/**
 * @(#)BlYsL2RcvSeEJBBean
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      BLOOM 야드 L2 수신 처리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.bl.session;

import com.inisteel.cim.common.exception.DAOException;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.session.YsComm;
import com.inisteel.cim.ys.common.util.YsCommUtils;
import com.inisteel.cim.ys.common.util.YsConstant;
/**
 *      [A] 클래스명 : BLOOM 야드 L2수신 처리
 *
 * @ejb.bean name="BlYsL2RcvSeEJB" jndi-name="BlYsL2RcvSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
*/
 
public class BlYsL2RcvSeEJBBean extends BaseSessionBean {
	
	private static final long serialVersionUID = 1L;
	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();
	private BlYsComm blYsComm = new BlYsComm();
	private YsComm ysComm = new YsComm();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}	
	/***************************************************************************
	 * 
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : 저장위치제원요구(N1YSL001)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN1YSL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장위치제원요구[BlYsL2RcvSeEJB.rcvN1YSL001] < " + rcvMsg.getResultMsg();
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
			* 2. 저장위치제원(YSN1L001) 전문 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("YD_INFO_SYNC_CD", ydInfoSyncCd                         ); //야드정보동기화코드
			jrParam.setField("YS_STK_COL_GP"  , ydGp + ydBayGp + ydEqpGp + ydStkColNo); //야드적치열구분
			jrParam.setField("YS_STK_BED_NO"  , ydStkBedNo                           ); //야드적치Bed번호

			//전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN1L001", jrParam));
			

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 저장품제원요구(N1YSL002)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN1YSL002(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "저장품제원요구[BlYsL2RcvSeEJB.rcvN1YSL002] < " + rcvMsg.getResultMsg();
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
			* 2. 저장품제원(YSN1L002) 전문 생성
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
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN1L002", jrParam));

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 설비고장복구실적(N1YSL003)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN1YSL003(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비고장복구실적[BlYsL2RcvSeEJB.rcvN1YSL003] < " + rcvMsg.getResultMsg();
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
				if (msgId.startsWith("N1") || (ydEqpId.length() == 6 && "CR".equals(ydEqpId.substring(2, 4)))) {
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
			//resMsg.setField("YD_WRK_PROG_STAT", ""          ); //야드작업진행상태(야드설비상태)
			//resMsg.setField("YD_SCH_CD"       , ""          ); //야드스케쥴코드(야드설비휴지코드)
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
				jrRtn = this.trtCrnResch(jrParam);
			}
			
			/**********************************************************
			* 6. 크레인작업실적응답 전문 전송(YSN1L004)
			**********************************************************/
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, blYsComm.getYSN1L004(resMsg));
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
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { blYsComm.getYSN1L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

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
		String methodNm = "크레인리스케줄[BlYsL2RcvSeEJB.trtCrnResch] < " + jrParam.getResultMsg();
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
				jrRtn = commUtils.addSndData(jrRtn, this.rcvN1YSL004(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 크레인작업지시요구(N1YSL004)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN1YSL004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업지시요구[BlYsL2RcvSeEJB.rcvN1YSL004] < " + rcvMsg.getResultMsg();
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
			String modifier      = "N1YSL004";  //수정자

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
			JDTORecord jrChk = blYsComm.chkEqpStat(jrParam);

			ydL3HdRsCd = commUtils.trim(jrChk.getFieldString("YD_L3_HD_RS_CD"));
			ydL3Msg    = commUtils.trim(jrChk.getFieldString("YD_L3_MSG"     ));

			if (!"".equals(ydL3Msg)) {
				resMsg.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);  //테스트시 임시주석    CUYSJ002  BILLET압연지시확정 BtYsL3RcvSeEJB.rcvCUYSJ002  ->CUYSJ002  BILLET압연지시확정 BlYsL2RcvSeEJB.rcvN1YSL004 
			}

			//test 시작
			//int intYD_EQP_WRK_AVG_L =7237;
			//int YAXIS=Integer.parseInt("25600", 0);
			//YAXIS=YAXIS-((intYD_EQP_WRK_AVG_L-5390)/2);   //26500=26500-((7276-5390)/2)
			//jrParam.setField("YD_UP_WO_LOC_YAXIS",  	Long.toString(YAXIS)) ;  
			
			//test종료
			
			/**********************************************************
			* 2. 크레인스케줄 조회
			*    2.1 크레인스케줄이 존재하면 전송
			*    2.2 크레인스케줄이 존재하지 않으면 수신된 야드작업진행상태에 따라 처리
			**********************************************************/
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
//	        		commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchWoLyr", logId, methodNm, "크레인스케줄 권상지시단 수정");
				}
				
				//크레인작업지시(YSN1L003) 전문 생성
				jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN1L003", jrParam));

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
					jrRtn = commUtils.addSndData(jrRtn, blYsComm.getYSN1L004(resMsg));

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
	        		JDTORecordSet jsWrkBook = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbIdNxYSL004", logId, methodNm, "작업예약 조회");

					//작업예약이 있으면 크레인스케줄  호출
					if (jsWrkBook.size() > 0) {
						ydL3Msg = "크레인스케줄 호출";

						jrYdMsg.setField("YD_WBOOK_ID"  , jsWrkBook.getRecord(0).getFieldString("YD_WBOOK_ID")); //야드작업예약ID
						jrYdMsg.setField("YD_SCH_CD"    , jsWrkBook.getRecord(0).getFieldString("YD_SCH_CD"  )); //야드스케쥴코드
						jrYdMsg.setField("YD_EQP_ID"    , ydEqpId ); //야드설비ID
						jrYdMsg.setField("YD_SCH_ST_GP" , "A"     ); //야드스케쥴기동구분(Auto)
						jrYdMsg.setField("YD_SCH_REQ_GP", "N"     ); //야드스케쥴요청구분(권하완료후 다음)
						jrYdMsg.setField("MODIFIER"   , modifier); //수정자

						jrRtn = blYsComm.getCrnSchMsg(jrYdMsg);
					} else {
						ydL3Msg = "다음 크레인작업지시 없음";
						
						/*
						 * 2016.09.25 윤재광
						 * 블룸소재 장입크레인이 IDLE상태일때 선장입작업 호출
						 */
						jrParam = JDTORecordFactory.getInstance().create();
						
						jrParam.setField("YD_SCH_CD"  , "BBTZ01UM"); 
						JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄기준 조회");

						if(jsChk1.size() > 0) {
							
							String szPreEqpId   	= commUtils.trim(jsChk1.getRecord(0).getFieldString("YD_WRK_CRN"));
							
							if(ydEqpId.equals(szPreEqpId)){
								
								jrParam = JDTORecordFactory.getInstance().create();
								
								jrParam.setField("REPR_CD_GP"  , "BBTZ11"); 
								jrParam.setField("CD_GP"       , "*"  ); 
								JDTORecordSet jsChk2 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsRuleList", logId, methodNm, "선작업기준 조회");

								if(jsChk2.size() > 0) {
									String sPreWorkYn = commUtils.trim(jsChk2.getRecord(0).getFieldString("ITEM"));
									
									if("Y".equals(sPreWorkYn)){
										
										jrParam = JDTORecordFactory.getInstance().create();
										
										jrParam.setField("JMS_TC_CD"		 , "YSYSJ114"); 
										jrParam.setField("JMS_TC_CREATE_DDTT", commUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
										jrParam.setField("YD_EQP_ID",      "BBTZ01");
										jrParam.setField("YS_STK_BED_NO",  "01");
										jrParam.setField("L3_PRE_WORK",    "Y");
										
										jrRtn = commUtils.addSndData(jrRtn, jrParam);
									}
								} 
							}
						} 
					}

					resMsg.setField("YD_L3_HD_RS_CD", "9999" ); //야드L3처리결과코드
					resMsg.setField("YD_L3_MSG"     , ydL3Msg); //야드L3MESSAGE
					jrRtn = commUtils.addSndData(jrRtn, blYsComm.getYSN1L004(resMsg));

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
				resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { blYsComm.getYSN1L004(resMsg) });
			} catch (Exception se) {}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
			

	/**
	 *      [A] 오퍼레이션명 : 블룸 CARRY-OUT 요구(M2YSL101)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM2YSL101(JDTORecord rcvMsg) throws DAOException {
	
		String methodNm = "블름 CARRY-OUT요구[BlYsL2RcvSeEJB.rcvM2YSL101] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		String sExcptMsg 	= "";
		String sIfData 		= "";
		String msgId 		= null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;
		String szBlmNo = null;
		JDTORecord jrRtn = null;
		
		JDTORecordSet jsRule = null;
		JDTORecord jrRule = null;
		String callCarryOutYN = "Y"; //Carry-Out처리(YSYSJ113) 호출 여부 (Y:호출, N:skip);
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			msgId         		 = commUtils.getMsgId(rcvMsg); 
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			String ysStkBedNo    = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"    )); //특수강야드적치Bed번호
			String ydStkBedStlSh = commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); //야드적치위치재료매수
			String carryOutEndGp = commUtils.trim(rcvMsg.getFieldString("CARRY_OUT_END_GP" )); //Carry-Out완료구분
			String L3Hmi         = commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         //백업화면 기동 여부
			String modifier      = "M2YSL101";  //수정자
			String ysStkLyrNo    = "01"; //야드적치단번호
//			String ysBedShift    = "N"; 
			String insYsStkBedNo = "";  // 재료 적치 할 bed 
			String stlNoRcv 	 = "";	//수신된 재료번호
			String stlNo 		 = "";	//재료번호
			String stlNoRgnt     = "N";	//재료번호에 시편
			
			int mtlSh = Integer.parseInt(ydStkBedStlSh); //야드적치Bed재료매수
			
			String[][] bedMtl = new String[3][2];	//Bed재료정보
			for (int ii = 0; ii < 3; ii++) {
				for (int jj = 0; jj < 2; jj++) {
					bedMtl[ii][jj] = "";
				}
				
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(ii+1)));
				if((ii+1) < 3) {
					sIfData += ", ";
				}
			}
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			//---------------------------------------------------------------------------------
			if("D".equals(carryOutEndGp)) {
				//2015.11.24 연주M2 에서 Carry-Out완료 구분을 'D'로 보내는 경우 처리 추가
				// 1) WB 상의 재료들의 스케줄, 작업예약 ID 취소 처리
				// 2) WB 상의 재료들 적치단에서 삭제
				// 3) M2로 CARRY-OUT 완료전문을 완료구분 'D'로 설정하여 전송 
				
				/**********************************************************
				* 1. 수신 항목 값 Check
				**********************************************************/
				if (ydEqpId.length() < 6) {
					sExcptMsg = "'D'전문 이상! 설비ID(YD_EQP_ID)[" + ydEqpId + "]";
				}
				if (!ydEqpId.startsWith("BAWB")) {
					sExcptMsg = "'D'전문 이상! 설비ID(YD_EQP_ID)[" + ydEqpId + "]";
				}
				
				if(!"".equals(sExcptMsg)) {
					throw new Exception(sExcptMsg);
				}					
				
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("MODIFIER"  , modifier  ); //수정자

			    try {
			    	
					//------------------------------------------------------
					// 1) WB 상의 재료들의 스케줄, 작업예약 ID 취소 처리
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId);
					
					JDTORecordSet jsCrnSch = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbSchId", logId, methodNm, "WB상의재료 작업예약ID,스케줄ID 조회");
					
					if(jsCrnSch.size() > 0) {
						
						String ydCrnSchId = null; //야드크레인스케쥴ID
						String ydWbookId  = null; //야드작업예약ID
					    String ydCrnId    = null; //야드설비ID
					    String ydSchCd    = null; //야드스케쥴코드
					    
					    EJBConnector ejbConn = new EJBConnector("default", "BlYsJspSeEJB", this);
					    JDTORecord jrRst =  null;

					    for (int ii = 0; ii < jsCrnSch.size(); ii++) {
					   
							ydCrnSchId 	= commUtils.trim(jsCrnSch.getRecord(ii).getFieldString("YD_CRN_SCH_ID")); //크레인스케쥴ID
							ydWbookId	= commUtils.trim(jsCrnSch.getRecord(ii).getFieldString("YD_WBOOK_ID")); //작업예약ID
							ydCrnId		= commUtils.trim(jsCrnSch.getRecord(ii).getFieldString("YD_EQP_ID")); //야드설비ID
							ydSchCd		= commUtils.trim(jsCrnSch.getRecord(ii).getFieldString("YD_SCH_CD")); //야드스케쥴코드
							
							commUtils.printLog(logId, "작업취소 [ " + ydWbookId + " - " + ydCrnSchId + " ]", "SL");

							jrParam.setField("YD_WBOOK_ID"  , ydWbookId );
							jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId);
							jrParam.setField("YD_EQP_ID"    , ydEqpId   );
							jrParam.setField("YD_SCH_CD"    , ydSchCd   );
							
							/**********************************************************
							* 1. 크레인스케줄 취소
							**********************************************************/
							jrRst = (JDTORecord)ejbConn.trx("trtCrnSchCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							jrRtn = commUtils.addSndData(jrRtn, jrRst);

							/**********************************************************
							* 2. 작업예약 취소
							**********************************************************/
							jrRst = (JDTORecord)ejbConn.trx("trtWrkBookCncl", new Class[] { JDTORecord.class }, new Object[] { jrParam });
							
							jrRtn = commUtils.addSndData(jrRtn, jrRst);
							
					    }
					    
						/**********************************************************
						* 5. 크레인작업지시요구 전문 조회
						**********************************************************/
						//크레인작업지시요구 전문 - Log ID, Method, 수정자 Set
						JDTORecord jrYdMsg = commUtils.getParam(logId, methodNm, modifier);

						jrYdMsg.setResultCode(logId);	//Log ID
						jrYdMsg.setResultMsg(methodNm);	//Log Method Name
						jrYdMsg.setField("JMS_TC_CD"       , YsConstant.N1YSL004);	//크레인작업지시요구
						jrYdMsg.setField("YD_EQP_ID"       , ydCrnId   );	//야드설비ID
						jrYdMsg.setField("YD_WRK_PROG_STAT", "4"       );	//야드작업진행상태(권하완료)
						jrYdMsg.setField("YD_SCH_CD"       , ydSchCd   );	//야드스케쥴코드
						jrYdMsg.setField("YD_CRN_SCH_ID"   , ydCrnSchId);	//야드크레인스케쥴ID

						EJBConnector sndConn = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
						JDTORecord jrRtn1 = (JDTORecord)sndConn.trx("rcvN1YSL004", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
						
						jrRtn = commUtils.addSndData(jrRtn,jrRtn1);
						
					}
					
					//------------------------------------------------------
					// 2) WB 상의 재료들 적치단에서 삭제				
					jrParam.setField("YD_STK_COL_GP"      , ydEqpId); 
					jrParam.setField("YD_STK_BED_NO"      , "%"   	); 
					
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "WB 재료번호 삭제");
					
					//------------------------------------------------------
					//3) M2로 Carry-out 완료 송신 (완료구분이 'D'로 설정되어 있음)
					rcvMsg.setField("YD_EQP_WRK_SH"		, ""+mtlSh);
					rcvMsg.setField("CARRY_OUT_END_GP"	, "D");
					rcvMsg.setField("YD_STK_COL_GP"		, ydEqpId);
					rcvMsg.setField("YD_STK_BED_NO"		, ysStkBedNo);
					
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM2L101BackUp", rcvMsg));
			    	
				} catch(DAOException e) {
					sExcptMsg = "'D'전문 Error!";
					throw e;
				} catch(Exception e) {
					sExcptMsg = "'D'전문 Error!!";
					throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
				}				
				
				commUtils.printLog(logId, methodNm, "S-");
				
				sExcptMsg = "'D'전문 수신 완료 ";
				
				return jrRtn;
				
			} // end of if("D".equals(carryOutEndGp)) {
			//---------------------------------------------------------------------------------
			
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {  // BAWB01
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID)[" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {  //01
				sExcptMsg = "전문 이상! 적치Bed번호(YS_STK_BED_NO) 없음";
			} else if ("".equals(ydStkBedStlSh)) { //3
				sExcptMsg = "전문 이상! 적치Bed재료매수(YD_STK_BED_STL_SH) 없음";
			}
			
			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}			
			
			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");			
			
			
			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				/**********************************************************
				* 2. 입고 공BED여부 CHECK
				**********************************************************/
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYsStlCnt 
				SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				     , COUNT(SSTL_NO) AS STL_CNT 
				  FROM
				(
				SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				     , COUNT(SSTL_NO) AS STL_CNT
				  FROM TB_YS_STKLYR
				 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				GROUP BY YS_STK_COL_GP,YS_STK_BED_NO
				) WHERE STL_CNT = '0'
					*/
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsStlCnt", logId, methodNm, "입고 공BED 여부");
	
				if (jsChk != null && jsChk.size() > 0) {
				} else {
					//sExcptMsg = "CARRY OUT 요구 할 BED가 없습니다...";
					sExcptMsg = ydEqpId + " 에 공BED 없음";
					throw new Exception(sExcptMsg);
				}			

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNoRcv 		= commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
					if ("".equals(stlNoRcv)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					
					if(stlNoRcv.length() == 12){
						stlNo 		= stlNoRcv.substring( 0, 11).trim();	//재료번호
						stlNoRgnt 	= stlNoRcv.substring(11, 12);	//재료번호 시편여부
					} else {
						stlNo 		= stlNoRcv;	//재료번호
						stlNoRgnt 	= "N";		//재료번호 시편여부
					}
						
						
						
					commUtils.printLog(logId, methodNm + "|||" + stlNoRcv + "|||stlNo:" + stlNo+ "|||stlNoRgnt:" + stlNoRgnt, "SL");
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 
					jrParam.setField("YS_STK_BED_NO"      , "01"); //야드적치Bed번호
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo
					SELECT YS_STK_COL_GP
				         , YS_STK_BED_NO 
				      FROM TB_YS_STKLYR
			       	 WHERE SSTL_NO = :V_SSTL_NO
				       AND YS_STK_COL_GP = :V_YS_STK_COL_GP 
					   AND YS_STK_BED_NO >= :V_YS_STK_BED_NO 
					*/ 
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + stlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";
						throw new Exception(sExcptMsg);
					}	
	
				}
			}
			
			/**********************************************************
			* 2.1  저장품 등록
			**********************************************************/
			jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("MODIFIER"           , modifier  ); //수정자			
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNoRcv = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
						
				if ("".equals(stlNoRcv)) {
					sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
					throw new Exception(sExcptMsg);
				}
				
				if(stlNoRcv.length() == 12){
					stlNo 		= stlNoRcv.substring( 0, 11).trim();	//재료번호
					stlNoRgnt 	= stlNoRcv.substring(11, 12);	//재료번호 시편여부
				} else {
					stlNo 		= stlNoRcv;	//재료번호
					stlNoRgnt 	= "N";		//재료번호 시편여부
				}

				
				jrParam.setField("SSTL_NO"       	, stlNo    ); //재료번호
				//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
				jrParam.setField("CARRY_OUT"	    , "Y"       ); 
				
				//CARRY_OUT 시점에 시편정보 관리 부분
				jrParam.setField("YD_FTMV_MEANS_GP"	, stlNoRgnt); 
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.insBlYdStock", logId, methodNm, "야드 저장품 등록"); 
			}					
						
			if(L3Hmi.equals("N")) {  // I/F 수신
				
				/**********************************************************
				* 3. BED SHIFT  
				* 화면에서 기동은 3BED SET 하고 기동됨
				* 3-> 2 -> 1
	 			**********************************************************/
				JDTORecordSet jsChk1  = JDTORecordFactory.getInstance().createRecordSet("Temp");
				JDTORecord outParam1 = JDTORecordFactory.getInstance().create();
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				jrParam1.setField("YS_STK_BED_NO"      , "03"   ); //L2 수신
				
				/* com.inisteel.cim.ys.bl.dao.BlYsDAO.getStkLyrCnt
				SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				     , CASE WHEN COUNT(SSTL_NO)  > 0 THEN 1 ELSE 0 END AS SSTL_NO_CNT
				  FROM TB_YS_STKLYR
				 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				   AND YS_STK_BED_NO <= :V_YS_STK_BED_NO
				 GROUP BY YS_STK_COL_GP, YS_STK_BED_NO
				 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO DESC
				 */
				jsChk1 = commDao.select(jrParam1, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getStkLyrCnt", logId, methodNm, "적치열 구분 조회");
				
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
				for (int ii = jscnt ; ii >= 0  ; ii--) {
					
					if (bedMtl[ii][1].equals("1")) {
						continue;
					} else {
						
						if (ShiftEndYn.equals("Y")) {
							break;
						}
						
						// 빈BED 이후 BED 에서 SHIFT 대상 찾음
						for (int jj = ii ; jj >= 0  ; jj--) {
							
							if (bedMtl[jj][1].equals("1")) {
										
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
								
									jrParam1.setField("YS_STK_BED_NO"         , bedMtl[ii-1][0]     );
									jrParam1.setField("MODIFIER"              , modifier  ); //수정자			
									commDao.update(jrParam1, "com.inisteel.cim.ys.bl.dao.BlYsDAO.updYdStkLyrClrBedrow", logId, methodNm, "야드 적치단 등록");
									
									ShiftEndYn = "Y"; //SHIFT 종료
									
								}	
								
								break;
							}
						}
					}
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

			//if(L3Hmi.equals("N")) {  // I/F 수신
					
				/**********************************************************
				* 2.1 적재단, 저장품 등록
				**********************************************************/
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				jrParam.setField("MODIFIER"           , modifier  ); //수정자			

				mtlSh = Integer.parseInt(ydStkBedStlSh); //야드적치Bed재료매수
				commUtils.printLog(logId, "저장위치 등록  BED :" + insYsStkBedNo , "SL");
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNoRcv = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(stlNoRcv)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					if(stlNoRcv.length() == 12){
						stlNo 		= stlNoRcv.substring( 0, 11).trim();	//재료번호
					} else {
						stlNo 		= stlNoRcv;	//재료번호
					}

					jrParam.setField("SSTL_NO"       	, stlNo    ); //재료번호
					jrParam.setField("YS_STK_BED_NO"	, insYsStkBedNo ); 
					jrParam.setField("YS_STK_LYR_NO"	, "01"     ); //야드적치SEQ
					jrParam.setField("YS_STK_SEQ_NO"	, String.valueOf(ii)       ); //야드적치SEQ
					//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
					jrParam.setField("CARRY_OUT"	    , "Y"       ); 
					jrParam.setField("YD_STK_LYR_MTL_STAT", "C"       ); //야드적치단재료상태(적치중)
					
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

				}		
			} else {
				//if(insYsStkBedNo.equals("")){
				//	insYsStkBedNo = "03";
				//}else {
				//	insYsStkBedNo = "0"+(Integer.parseInt(insYsStkBedNo) - 1);
				//}
				insYsStkBedNo = ysStkBedNo;
			}
			
			commUtils.printLog(logId, "스케쥴 기동  BED :" + insYsStkBedNo , "SL");

			//야드기준(TB_YS_RULE 에서 전단미발생 재료 스케줄생성여부 기준을 읽어 온다.
			String szRuleYN = null;
			
			jrParam.setField("REPR_CD_GP"	, "B00010" );
			jrParam.setField("CD_GP"		, "3" );
			
			jsRule = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsRuleList", logId, methodNm, "YS_RULE 리스트 조회");
			
			if (jsRule != null && jsRule.size() > 0) {
				
				jrRule = jsRule.getRecord(0);

				szRuleYN	= commUtils.trim(jrRule.getFieldString("ITEM")); //Y:적용 , N:미적용
				
		    } else {
		    	
		    	szRuleYN 	= "N"; //기준이 없으면 미적용으로 설정
		    }
			 
			
			//전단실적 누락 재료번호 체크(STOCK 미존재)
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				stlNoRcv = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
	
				if(stlNoRcv.length() == 12){
					stlNo 		= stlNoRcv.substring( 0, 11).trim();	//재료번호
				} else {
					stlNo 		= stlNoRcv;	//재료번호
				}
				
				jrParam.setField("SSTL_NO"	, stlNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BLOOM공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp = jsTemp.getRecord(0);
	
					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); //Stock 에 존재하지 않으면 ""
					szBlmNo			= commUtils.trim(jrTemp.getFieldString("BLM_NO")); //BLOOM공통 에 존재하지 않으면 ""
			    } 			
				
				//저장품에 또는 Bloom공통에 존재하는 제품번호인지 체크
				if("".equals(szStkStlNo)||"".equals(szBlmNo)) {

					sExcptMsg = "전단실적 누락 " + stlNo;
					//throw new Exception(sExcptMsg);
					//break;
					callCarryOutYN = "N"; //전단실적 누락시 Carry-Out 처리를 안한다.
					
					//야드기준에서 전단실적 누락 제품을 크레인스케줄 생성할지 여부를 체크한다.
					//기준이 'Y'일 경우
					if("Y".equals(szRuleYN)) {
						// 1) STOCK에 재료번호를 강제 생성한다. (항목 빈 값)
						commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insEmptyStock", logId, methodNm, "Empyt 야드저장품 강제생성"); 
						
						// 2) callCarryOutYN 를 "Y" 로 설정하여 Carry-Out 처리를 수행한다.
						callCarryOutYN = "Y";
					}
				}
			}

			
			//if("".equals(sExcptMsg)) {
			if("Y".equals(callCarryOutYN)) {
				
				//예외처리 없을 경우만 호출한다.
				/**********************************************************
				* 3. Carry-Out 처리
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
	
				//설비인출요구
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ113"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YS_STK_BED_NO"     , insYsStkBedNo ); //야드적치Bed번호
				jrYdMsg.setField("YS_STK_LYR_NO"     , ysStkLyrNo               ); //야드적치단번호
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("MODIFIER"        , modifier                   ); //수정자
				
				//전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrYdMsg);
			}
			commUtils.printLog(logId, methodNm, "S-");
			
			return jrRtn;
			
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		} finally {
			try {
				
				if(!"".equals(sExcptMsg)) {

					if(sExcptMsg.indexOf("■Error■ Message :")>0) {
						sExcptMsg = sExcptMsg.substring(sExcptMsg.indexOf("■Error■ Message :")+"■Error■ Message :".length()+1,sExcptMsg.length());
					}
					
					if(sExcptMsg.getBytes().length > YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN) {
						sExcptMsg = commUtils.substr(sExcptMsg, 0, YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN); 
					}
					
					if(sIfData.getBytes().length > YsConstant.YS_EXCPTHIST_IF_DATA_LEN) {
						sIfData = commUtils.substr(sIfData, 0, YsConstant.YS_EXCPTHIST_IF_DATA_LEN); 
					}
					
					//TB_YS_EXCPTHIST(예외처리이력 테이블)에 저장
					jrParam.setField("IF_ID"		, msgId  	); 	//인터페이스Id
					jrParam.setField("IF_DATA"		, sIfData  	); 	//인터페이스Data
					jrParam.setField("EXCPT_MSG"	, sExcptMsg );  //예외메세지
					jrParam.setField("MODIFIER"		, logId     ); 	//수정자
					
					//commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insExcptHist", logId, methodNm, "예외처리이력 테이블 등록");
					EJBConnector tranConn = new EJBConnector("default", "YsCommSeEJB", this);
					tranConn.trx("insExcptHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} 
				
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 장입이상재 CARRY-OUT 요구(M3YSL102)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM3YSL102(JDTORecord rcvMsg) throws DAOException {
	/*
	YD_EQP_ID	야드설비ID
	YS_STK_BED_NO	특수강야드적치Bed번호
	YD_STK_BED_STL_SH	야드적치Bed재료매수
	YD_EQP_WRK_SH	야드설비작업매수
	SSTL_NO1	특수강재료번호1
	SSTL_NO2	특수강재료번호2
	SSTL_NO3	특수강재료번호3
	SSTL_NO4	특수강재료번호4
	SSTL_NO5	특수강재료번호5
	SSTL_NO6	특수강재료번호6
	SSTL_NO7	특수강재료번호7
	SSTL_NO8	특수강재료번호8
	SSTL_NO9	특수강재료번호9
	SSTL_NO10	특수강재료번호10
	 		
	 */
		
		String methodNm = "장입이상재 CARRY-OUT완료[BlYsL2RcvSeEJB.rcvM3YSL102] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrParam = JDTORecordFactory.getInstance().create();
		String sExcptMsg = "";
		String sIfData = "";
		String msgId = null;
		JDTORecordSet jsTemp = null;
		JDTORecord jrTemp = null;
		String szStkStlNo = null;

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			msgId         		 = commUtils.getMsgId(rcvMsg); 
			String ydEqpId       = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"        )); //야드설비ID
			String ysStkBedNo    = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO"    )); //야드적치Bed번호
			String ydStkBedStlSh = commUtils.trim(rcvMsg.getFieldString("YD_STK_BED_STL_SH")); //야드적치Bed재료매수
//			String carryOutReqYn = commUtils.trim(rcvMsg.getFieldString("CARRY_OUT_REQ_GP" )); //Carry-Out요구구분
			String modifier      = commUtils.trim(rcvMsg.getFieldString("MODIFIER"       )); //수정자(Backup Only)
			String L3Hmi         = commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");         //백업화면 기동 여부
			String ysStkLyrNo    = "01"; //야드적치단번호
			
			
			if ("".equals(modifier)) { modifier = msgId; }
			methodNm = msgId.substring(0, 2) + methodNm;
			
			for (int ii = 0; ii < 3; ii++) {
				sIfData += commUtils.trim(rcvMsg.getFieldString("SSTL_NO"+(ii+1))) + " ";
				if((ii+1) < 3) {
					sIfData += ", ";
				}
			}			

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if (ydEqpId.length() < 6) {
				sExcptMsg = "전문 이상! 설비ID(YD_EQP_ID) 이상 [" + ydEqpId + "]";
			} else if ("".equals(ysStkBedNo)) {
				sExcptMsg = "전문 이상! 적치Bed번호(YS_STK_BED_NO) 없음";
			} else if ("".equals(ydStkBedStlSh)) {
				sExcptMsg = "전문 이상! 적치Bed재료매수(YD_STK_BED_STL_SH) 없음";
			}

			if(!"".equals(sExcptMsg)) {
				throw new Exception(sExcptMsg);
			}	
			
			/**********************************************************
			* 2. 적치Bed 재료번호, 재료상태  및 저장품 Update
			**********************************************************/
			JDTORecord outParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			int mtlSh = Integer.parseInt(ydStkBedStlSh); //야드적치Bed재료매수
			String stlNo = "";	//재료번호
		
			JDTORecordSet jsChk = JDTORecordFactory.getInstance().createRecordSet("Temp");	
			
			if(L3Hmi.equals("N")) {   // 전문이 오는 경우
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
				/**********************************************************
				* 2. 입고 공BED여부 CHECK
				**********************************************************/
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYsStlCnt 
				SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				     , COUNT(SSTL_NO) AS STL_CNT 
				  FROM
				(
				SELECT YS_STK_COL_GP
				     , YS_STK_BED_NO 
				     , COUNT(SSTL_NO) AS STL_CNT
				  FROM TB_YS_STKLYR
				 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
				GROUP BY YS_STK_COL_GP,YS_STK_BED_NO
				) WHERE STL_CNT = '0'
					*/
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsStlCnt", logId, methodNm, "입고 공BED 여부");
	
				if (jsChk != null && jsChk.size() > 0) {
				} else {
					//sExcptMsg = "CARRY OUT 요구 할 BED가 없습니다...";
					sExcptMsg = ydEqpId + " 에 공BED 없음";
					throw new Exception(sExcptMsg);
				}			

				// 화면에서 백업시 야드 적치위치 등록 된 후에 온다
				/**********************************************************
				* 2.1 재료번호  CHECK : 저장위치 등록여부
				**********************************************************/
				
				for (int ii = 1; ii <= mtlSh; ii++) {
					stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
							
					if ("".equals(stlNo)) {
						sExcptMsg = "전문 이상! 재료번호(SSTL_NO" + ii + ") 없음";
						throw new Exception(sExcptMsg);
					}
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setResultCode(logId);	//Log ID
					jrParam.setResultMsg(methodNm);	//Log Method Name
					jrParam.setField("YS_STK_COL_GP"      , ydEqpId   ); //야드적치열구분
					jrParam.setField("SSTL_NO"			  , stlNo     ); 
					jrParam.setField("YS_STK_BED_NO"      , "01"); //야드적치Bed번호
					
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo
					SELECT YS_STK_COL_GP
				         , YS_STK_BED_NO 
				      FROM TB_YS_STKLYR
			       	 WHERE SSTL_NO = :V_SSTL_NO
				       AND YS_STK_COL_GP = :V_YS_STK_COL_GP 
					   AND YS_STK_BED_NO >= :V_YS_STK_BED_NO 
					*/ 
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYswBookYNstlNo", logId, methodNm, "저장위치 등록여부");
		
					if (jsChk != null && jsChk.size() > 0) {
						
						jrTemp = jsChk.getRecord(0);
						
						sExcptMsg   = "이미 야드에 적치된 재료 "
							        + stlNo	
									+ " ["
									+ commUtils.trim(jrTemp.getFieldString("YS_STK_COL_GP")) 
									+ "-" 
						            + commUtils.trim(jrTemp.getFieldString("YS_STK_BED_NO"))
						            + "]";
						throw new Exception(sExcptMsg);
					}	
	
				}
			}
			
			/**********************************************************
			* 2.1 작업예약 등록 여부
			**********************************************************/
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
						
				if (!"".equals(stlNo)) {
					jrParam.setField("SSTL_NO"       	, stlNo    ); //재료번호
					/* com.inisteel.cim.ys.bl.dao.BlYsDAO.getYswBookYN
					--설비인출요구 작업예약 조회 
					SELECT DECODE(COUNT(*),0,'N','Y') AS WB_STL_YN --작업예약재료여부
					  FROM TB_YS_STKLYR     SS
					     , TB_YS_WRKBOOKMTL WM
					     , TB_YS_WRKBOOK    WB
					WHERE SS.SSTL_NO       = WM.SSTL_NO
					  AND WM.YD_WBOOK_ID   = WB.YD_WBOOK_ID
					  AND SS.SSTL_NO       = :V_SSTL_NO
					  AND WM.DEL_YN        = 'N'
					  AND WB.DEL_YN        = 'N'
					 */ 
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getYswBookYN", logId, methodNm, "작업예약 등록여부");

					if (jsChk != null && jsChk.size() > 0) {
						outParam = jsChk.getRecord(0);
						
						if ("Y".equals(commUtils.trim(outParam.getFieldString("WB_STL_YN")))) {
							sExcptMsg = "이미 작업예약에 등록된 재료 " + stlNo;
							throw new Exception(sExcptMsg);
						}
					}	
				}
			}
						
			
			commUtils.printLog(logId, "작업예약등록 시작", "SL");
			
			/**********************************************************
			* 2.2 적재단, 저장품 등록
			**********************************************************/
			
			for (int ii = 1; ii <= mtlSh; ii++) {
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
						
				if ("".equals(stlNo)) {
					sExcptMsg = "재료번호(SSTL_NO" + ii + ") 없음";
					throw new Exception(sExcptMsg);
				}
				jrParam.setField("YS_STK_COL_GP"      	, ydEqpId   ); //야드적치열구분
				jrParam.setField("MODIFIER"           	, modifier  ); //수정자							
				jrParam.setField("SSTL_NO"       		, stlNo    ); //재료번호
				jrParam.setField("YD_STK_LYR_MTL_STAT"	, "C"       ); //야드적치단재료상태(적치중)
				jrParam.setField("YS_STK_BED_NO"		, ysStkBedNo ); 
				jrParam.setField("YS_STK_LYR_NO"		, "01"       ); //야드적치SEQ
				jrParam.setField("YS_STK_SEQ_NO"		, String.valueOf(ii)       ); //야드적치SEQ
				//CARRY_OUT 시점에 저장픔 YD_RCPT_DATE UPDATE
				jrParam.setField("CARRY_OUT"	    , "Y"       ); 

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
				
				if(!L3Hmi.equals("Y")) { 
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyr", logId, methodNm, "야드 적치단 등록"); 
				}	
				
				commDao.insert(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.insBlYdStock", logId, methodNm, "야드 저장품 등록"); 
			}
			
			//STOCK 재료번호가 존재하지 않으면 작업예약을 만들지 않는다.
			for (int ii = 1; ii <= mtlSh; ii++) {
				
				stlNo = commUtils.trim(rcvMsg.getFieldString("SSTL_NO" + ii));	//재료번호
				
				jrParam.setField("SSTL_NO"	, stlNo );
				
				//제품번호가 야드저장품에 존재하는지, 작업예약과 크레인스케줄에 잡혀있는 대상인지를 가져오고 BLOOM공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getIsEnableStlNo", logId, methodNm, "제품번호 작업대상여부 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
					
					jrTemp = jsTemp.getRecord(0);
	
					szStkStlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO")); //Stock 에 존재하지 않으면 ""
			    } 			
				
				//저장품에 존재하는 제품번호인지 체크
				if("".equals(szStkStlNo)) {

					sExcptMsg = "전단실적 누락 " + stlNo;
					//throw new Exception(sExcptMsg);
					break;
				}
			}
			
			JDTORecord jrRtn = null;
			
			if("".equals(sExcptMsg) && L3Hmi.equals("Y")) {//L3백업화면만 이용해서 Carry Out처리 2018.10.18 윤재광
				//예외처리 없을 경우만 호출한다.
				/**********************************************************
				* 3. Carry-Out 처리
				**********************************************************/
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				jrYdMsg.setResultCode(logId);	//Log ID
				jrYdMsg.setResultMsg(methodNm);	//Log Method Name
				//설비인출요구
				jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ113"               ); //JMSTC코드
				jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
				jrYdMsg.setField("YD_EQP_ID"         , ydEqpId                  ); //야드설비ID
				jrYdMsg.setField("YS_STK_BED_NO"     , ysStkBedNo               ); //야드적치Bed번호
				jrYdMsg.setField("YS_STK_LYR_NO"     , ysStkLyrNo               ); //야드적치단번호
				jrYdMsg.setField("YD_SCH_ST_GP"      , "A"                      ); //야드스케쥴기동구분(Auto)
				jrYdMsg.setField("MODIFIER"        , modifier                 ); //수정자
				
				//전송할 전문에 추가
				jrRtn = commUtils.addSndData(jrYdMsg);
			}
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw e;
		} catch (Exception e) {
			//if("".equals(sExcptMsg)) {
			//	sExcptMsg = e.getMessage();
			//}
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		} finally {
			try {
				
				if(!"".equals(sExcptMsg)) {

					if(sExcptMsg.indexOf("■Error■ Message :")>0) {
						sExcptMsg = sExcptMsg.substring(sExcptMsg.indexOf("■Error■ Message :")+"■Error■ Message :".length()+1,sExcptMsg.length());
					}
					
					if(sExcptMsg.getBytes().length > YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN) {
						sExcptMsg = commUtils.substr(sExcptMsg, 0, YsConstant.YS_EXCPTHIST_EXCPT_MSG_LEN); 
					}
					
					if(sIfData.getBytes().length > YsConstant.YS_EXCPTHIST_IF_DATA_LEN) {
						sIfData = commUtils.substr(sIfData, 0, YsConstant.YS_EXCPTHIST_IF_DATA_LEN); 
					}
					
					//TB_YS_EXCPTHIST(예외처리이력 테이블)에 저장
					jrParam.setField("IF_ID"		, msgId  	); 	//인터페이스Id
					jrParam.setField("IF_DATA"		, sIfData  	); 	//인터페이스Data
					jrParam.setField("EXCPT_MSG"	, sExcptMsg );  //예외메세지
					jrParam.setField("MODIFIER"		, logId     ); 	//수정자
					
					//commDao.insert(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.insExcptHist", logId, methodNm, "예외처리이력 테이블 등록");
					EJBConnector tranConn = new EJBConnector("default", "YsCommSeEJB", this);
					tranConn.trx("insExcptHist", new Class[] { JDTORecord.class }, new Object[] { jrParam });
					
				} 
				
			} catch (Exception e) {
				throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
			}			
		}
	}
		 	
	/**
	 *      [A] 오퍼레이션명 : 크레인작업계획요구(N1YSL008)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord rcvN1YSL008(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인작업계획요구[BlYsL2RcvSeEJB.rcvN1YSL008] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); //EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydInfoSyncCd = commUtils.trim(rcvMsg.getFieldString("YD_INFO_SYNC_CD")); //야드정보동기화코드
			methodNm = msgId.substring(0, 2) + methodNm;

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydInfoSyncCd)) {
				throw new Exception("야드정보동기화코드(YD_INFO_SYNC_CD) 없음");
			} 

			/**********************************************************
			* 2. 크레인작업계획(YSN1L007) 전문을 생성
			**********************************************************/
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name

			jrParam.setField("PTOP_PLNT_GP",  "TA" ); //조업공장구분

			//전송 Data 생성
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN1L007", jrParam));
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 대차이동실적(N1YSL007)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN1YSL007(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "대차이동실적[BlYsL2RcvSeEJB.rcvN1YSL007] < " + rcvMsg.getResultMsg();
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
//				jrParam.setField("YD_STK_COL_GP"      , ydStkColGp); //야드적치열구분
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

				//하차위치 적치단 재료번호 등록 -> 혹시 정보가 맞지 않을 수도 있으므로 무조건 Update
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStkLyrTCarStl", logId, methodNm, "대차이동실적 적치단 재료번호 등록");

				//상차지 정보 Clear 추가
				//출발위치 적치Bed 비활성화 처리
				JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
				jrParam1.setResultCode(logId);	//Log ID
				jrParam1.setResultMsg(methodNm);	//Log Method Name
				jrParam1.setField("MODIFIER"       		, modifier    ); //수정자
				jrParam1.setField("YD_STK_BED_NO"		, "01"       ); //야드적치Bed번호
				jrParam1.setField("YD_STK_COL_GP"      	, ydCarldStopLoc); //야드적치열구분
				jrParam1.setField("YD_STK_BED_ACT_STAT"	, "C"       ); //야드적치Bed활성상태(비활성화)
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSchStaErrChk 
				SELECT DECODE(COUNT(*),0, 'N','Y') AS CLEAR_YN 
				   FROM TB_YS_STKLYR
				 WHERE YS_STK_COL_GP = :V_YD_STK_COL_GP
				   AND SSTL_NO IS NOT NULL
				*/   
				JDTORecordSet jsTcar1 = commDao.select(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getTcarSchStaErrChk", logId, methodNm, "대차스케줄조회");

				JDTORecord jr1Temp = null;
				String szCLEAR_YN  = "";
				if (jsTcar1 == null || jsTcar1.size() == 0) {
				} else {				

					jr1Temp = jsTcar1.getRecord(0);
					szCLEAR_YN		= commUtils.trim(jr1Temp.getFieldString("CLEAR_YN")); 
					//상차지 정보 Clear 추가
					if(szCLEAR_YN.equals("Y")) {
						commDao.update(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStatStkBedAct", logId, methodNm, "적치Bed 활성상태 수정");
						commDao.update(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrClr", logId, methodNm, "적치단(TB_YD_STKLYR) 재료번호 삭제");
					}	
				}
				
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
			* 5. 야드저장위치제원(YSN1L001) 전문 조회
			**********************************************************/
			jrParam.setField("YD_INFO_SYNC_CD", "4"       ); //야드정보동기화코드(Bed)
			jrParam.setField("YS_STK_COL_GP"  , ydStkColGp); //야드적치열구분(현재동)
			jrParam.setField("YS_STK_BED_NO"  , "01"); //야드적치Bed

			//전송Data 조회
			JDTORecord jrRtn = commUtils.addSndData(commDao.getMsgL2("YSN1L001", jrParam));

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
				jrRtn = commUtils.addSndData(jrRtn, blYsComm.getCrnSchMsg(jrYdMsg));
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
	 *      [A] 오퍼레이션명 : 크레인권상실적(N1YSL005)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN1YSL005(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권상실적[BlYsL2RcvSeEJB.rcvN1YSL005] < " + rcvMsg.getResultMsg();
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
			
			//PIDEV
//			String sApplyYnPI = commDao.ApplyYnPI("", "특수강 권상시", "APPPI0", "K", "*");
			
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
			} else if (ydUpWrLoc.length() != 8) {
				ydL3HdRsCd = "DN05";
				ydL3Msg    = "오류:권상실적위치가 8자리가 아닙니다.";
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
			jrParam.setField("YD_L2_ID", ydL2Id); //L2 ID (N1)

			String currDt = commUtils.getDateTime14(); //현재시각
			
			/**********************************************************
			* 3. 전송 전문 조회
			* 3.1 WB Carry-out 완료  (YSM2L101)
			* 3.2 장입이상재 Carry-out 완료 (YSM3L102)
			**********************************************************/
			if("BAWB01L".equals(ydSchCd.substring(0,7))) { //A동 연주WB추출(Carry-out)
				//WB Carry-out 완료 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM2L101", jrParam));
				
			} else if(("BALB01LM".equals(ydSchCd))) { //A동 장입이상재 추출(Carry-out)
				//장입이상재 Carry-out 완료 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM3L102", jrParam));
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

						jrYdMsg.setField("YD_EQP_ID"     , ydUpWrLoc.substring(0, 1) + "X" + ydUpWrLoc.substring(2, 6)); //야드설비ID(대차)
						jrYdMsg.setField("YD_TCAR_SCH_ID", ydTcarSchId); //야드대차스케쥴ID
						jrYdMsg.setField("MODIFIER"    , modifier   ); //수정자
						jrYdMsg.setField("YD_L2_ID"    , ydL2Id   ); //수정자
						

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
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L006", jrParam));
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
				--      ,DECODE(SC.YD_CAR_USE_GP,'L',
				--       NVL(MV.ARR_WLOC_CD,SF_SLAB_YD_ARR_WLOC_CD(MV.YD_AIM_YD_GP))) AS ARR_WLOC_CD --착지개소코드
				      ,CASE WHEN SC.YD_CAR_USE_GP = 'L' AND SUBSTR(TS.YD_CARLD_STOP_LOC,1,1) = 'B' THEN
				                  (SELECT DECODE(YD_AIM_BAY_GP,'A','S3Y10','B','S3Y11','S3Y30') 
				                     FROM TB_YS_PREPSCH AA
				                    WHERE AA.YD_PREP_SCH_ID = (SELECT MAX(YD_PREP_SCH_ID) 
				                                                 FROM TB_YS_PREPSCH A
				                                                WHERE YD_WBOOK_ID = CS.YD_WBOOK_ID
				                                                  )
				                                  )
				                             ELSE MV.ARR_WLOC_CD  END AS ARR_WLOC_CD        
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
						
						// 직상차인 경우 발생  착지 개소 모름 ---> 권하처리시 송신 함
						if(!commUtils.trim(jrChk.getFieldString("ARR_WLOC_CD"   )).equals("")){
							//전송할 전문에 추가
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
						}
					} else {
						//PIDEV						
//						if("Y".equals(sApplyYnPI)) {

							//출하관리 상차개시
							jrYdMsg.setField("MQ_TC_CD"           , "M10YDLMJ1074"); //JMSTC코드
							jrYdMsg.setField("MQ_TC_CREATE_DDTT"  , currDt    ); //JMSTC생성일시
							jrYdMsg.setField("TRN_REQ_DATE"       , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
							jrYdMsg.setField("TRN_REQ_SEQ"        , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
							jrYdMsg.setField("CAR_NO"             , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
							jrYdMsg.setField("YD_GP"              , ydEqpId.substring(0, 1)                                ); //야드구분
							jrYdMsg.setField("DIST_GOODS_GP"      , "R");
							jrYdMsg.setField("YARD_GP"            , "N");
							jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
							jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
							//전송할 전문에 추가
							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);

//						} else {
//
//							//출하관리 상차개시
//							jrYdMsg.setField("JMS_TC_CD"         , "YSDSJ006"); //JMSTC코드
//							jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDt    ); //JMSTC생성일시
//							jrYdMsg.setField("CARD_NO"           , commUtils.trim(jrChk.getFieldString("CARD_NO"        ))); //카드번호
//							jrYdMsg.setField("CAR_NO"            , commUtils.trim(jrChk.getFieldString("CAR_NO"         ))); //차량번호
//							jrYdMsg.setField("YD_GP"             , ydEqpId.substring(0, 1)                                ); //야드구분
//							jrYdMsg.setField("CARLOAD_START_DATE", currDt.substring(0,  8)                                ); //상차개시일자
//							jrYdMsg.setField("CARLOAD_START_TIME", currDt.substring(8, 14)                                ); //상차개시시각
//							jrYdMsg.setField("TRANS_WORD_DATE"   , commUtils.trim(jrChk.getFieldString("TRANS_ORD_DATE" ))); //운송작업지시일자
//							jrYdMsg.setField("TRANS_WORD_SEQNO"  , commUtils.trim(jrChk.getFieldString("TRANS_ORD_SEQNO"))); //운송작업지시순번
//							jrYdMsg.setField("SPST_FRTOMOVE_GP"  , "1" ); //특수강 이송구분
//							//전송할 전문에 추가
//							jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
//							
//							
//						}							
					}
					

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
//			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyr", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkLyrblbt", logId, methodNm, "적치단(크레인 및 권상위치) 수정");
			
			
			//적치Bed(완산Bed->입출고가능) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005StkBedF", logId, methodNm, "적치Bed(완산Bed->입출고가능) 수정");
			//크레인스케쥴 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL005CrnSch", logId, methodNm, "크레인스케쥴 수정");

			
			resMsg.setField("YD_UP_WR_LOC", ydUpWrLoc); //야드권상실적위치


			//크레인작업실적응답 전송
			if (resYn) {
				resMsg.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드(정상)
				resMsg.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE
				jrRtn = commUtils.addSndData(jrRtn, blYsComm.getYSN1L004(resMsg));
			}

			//WB입고일경우(=권상위치가 BAWB01) 
			if("BAWB01".equals(ydUpWrLoc.substring(0,6))) {
				
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.bl.dao.BlYsDAO.getWB01Mtl", logId, methodNm, "BLOOM 입고대 작업대상 조회");
				
				if(jsChk.size() > 0) {

					//WB에 작업예약만 등록된 작업할 BLOOM 이 있다면
					//크레인스케줄(YSYSJ102) 전문 호출한다.
					jrChk = jsChk.getRecord(0);
					
					String sYdWbookId 	= commUtils.trim(jrChk.getFieldString("YD_WBOOK_ID"));
					String sYdSchCd	  	= commUtils.trim(jrChk.getFieldString("YD_SCH_CD"));
					String sYdScrSchId	= commUtils.trim(jrChk.getFieldString("YD_CRN_SCH_ID"));
					
					if("".equals(sYdScrSchId)&&!"".equals(sYdWbookId)){
						
						resMsg.setField("YD_WBOOK_ID"	, sYdWbookId); 	//야드작업예약ID
						resMsg.setField("YD_SCH_CD"     , sYdSchCd); 	//야드스케쥴코드
						resMsg.setField("YD_SCH_REQ_GP" , "L"      ); 	//야드스케쥴요청구분(인출)
						resMsg.setField("MODIFIER"   	, modifier );   //수정자
						
						jrRtn = commUtils.addSndData(jrRtn,blYsComm.getCrnSchMsg(resMsg));
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
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { blYsComm.getYSN1L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 크레인권하실적(N1YSL006)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvN1YSL006(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "크레인권하실적[BlYsL2RcvSeEJB.rcvN1YSL006] < " + rcvMsg.getResultMsg();
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

			String szYD_TO_LOC_DCSN_MTD = ""; //분리 작업용
			String szNEXT_YD_CRN_SCH_ID = ""; //분리 작업용			
			String szNEXT_NEXT_YD_CRN_SCH_ID = ""; //분리 작업용
			
			if("BBTZ1101".equals(ydDnWrLoc)){
				ydDnWrLoc = "BBTZ0101";
				
				commUtils.printLog(logId, "권하위치 변경 : [" + ydCrnSchId + "- BBTZ1101 => " + ydDnWrLoc , "SL");
			}
			
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
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "K", "*");
			
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
			} else if (ydDnWrLoc.length() != 8) {
				ydL3HdRsCd = "DN05";
				ydL3Msg    = "오류:권하실적위치가 8자리가 아닙니다.";
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
			/* 크레인스케줄상태 조회 - com.inisteel.cim.ys.common.dao.YsCommDAO.getStatCrnSch

			SELECT A.YD_WBOOK_ID 
			     , A.YD_EQP_ID 
			     , A.YD_SCH_CD 
			     , A.YD_WRK_PROG_STAT 
			     , A.YS_DN_WO_LOC 
			     , A.YS_UP_WR_LOC 
			     , A.YD_CRN_SCH_ID    
			     , A.YD_TO_LOC_DCSN_MTD 
			     , A.YD_AID_WRK_UPDN_GP 
			     , CASE WHEN YD_TO_LOC_DCSN_MTD = 'C' AND YD_AID_WRK_UPDN_GP = 1 THEN  
			                 (SELECT YD_CRN_SCH_ID  
			                    FROM TB_YS_CRNSCH  
			                   WHERE YD_WBOOK_ID = A.YD_WBOOK_ID  
			                     AND YD_TO_LOC_DCSN_MTD = 'C' 
			                     AND YD_AID_WRK_UPDN_GP = '2' 
			                     AND DEL_YN = 'N') 
			            WHEN YD_TO_LOC_DCSN_MTD = 'C' AND YD_AID_WRK_UPDN_GP = 2 THEN  
			                 (SELECT YD_CRN_SCH_ID 
			                    FROM TB_YS_CRNSCH  
			                   WHERE YD_WBOOK_ID = A.YD_WBOOK_ID  
			                     AND YD_TO_LOC_DCSN_MTD = 'C' 
			                     AND YD_AID_WRK_UPDN_GP = '3' 
			                     AND DEL_YN = 'N') 
			             END AS  NEXT_YD_CRN_SCH_ID                         
			  FROM TB_YS_CRNSCH A 
			 WHERE A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID 
			   AND A.DEL_YN        = 'N'
				   */		   
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
				//분리
				szYD_TO_LOC_DCSN_MTD = commUtils.trim(jrChk.getFieldString("YD_TO_LOC_DCSN_MTD"       )); 
				szNEXT_YD_CRN_SCH_ID = commUtils.trim(jrChk.getFieldString("NEXT_YD_CRN_SCH_ID"       )); 
				szNEXT_NEXT_YD_CRN_SCH_ID = commUtils.trim(jrChk.getFieldString("NEXT_NEXT_YD_CRN_SCH_ID"       )); 
				
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

			jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbCmplYn", logId, methodNm, "작업예약 완료여부 조회");
			
			if (jsChk.size() > 0) {

				jrChk = jsChk.getRecord(0);
				wbCmplYn = commUtils.trim(jrChk.getFieldString("WB_CMPL_YN"));
				commUtils.printLog(logId, "작업예약[" + ydWbookId + "] 완료여부 : " + wbCmplYn, "SL");
				
			} else {
				resMsg.setField("YD_L3_HD_RS_CD", "DN14"); //야드L3처리결과코드
				resMsg.setField("YD_L3_MSG"     , "오류:작업예약 완료여부 조회 오류"); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			//우물정자적치대와 서냉피트 권하시 단 체크 로직
			if("0".equals(ydDnWrLoc.substring(2,3))) {
				//스판 구분이 "0" 으로 시작하면 우물정자 적치대 이다 (D동은 제외이나 D동에서는 권하실적 자체가 발생 안함)
				//우물정자 적치대 단 체크
				
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CurrV1", logId, methodNm, "현재정보  단 조회");
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
					String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));
					
					//우물정자 Bed 체크
					jrParam.setField("YS_STK_LYR_NO", tbDnWrLayer); //야드적치단구분
					
					jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CurrV1Bed", logId, methodNm, "현재정보 Bed 조회");
					
					if (jsChk.size() > 0) {

						jrChk = jsChk.getRecord(0);
						String tbDnWrBedNo = commUtils.trim(jrChk.getFieldString("YD_DN_WR_BED_NO"));
						
						if (!ydDnWrLayer.equals(tbDnWrLayer) || !ydDnWrLoc.substring(6, 8).equals(tbDnWrBedNo)) {
							
							chgDnWrLayer = true;
							ydDnWrLoc	= ydDnWrLoc.substring(0, 6) + tbDnWrBedNo;
							ydDnWrLayer  = tbDnWrLayer;
							//ydCrnZaxis   = "";
							
							commUtils.printLog(logId, "권하위치 변경 : [" + ydDnWrLoc + "-" + tbDnWrLayer , "SL");
							
							//새 권하위치 단을 Clear 한다.
							jrParam.setField("YS_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
							jrParam.setField("YS_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //변경된 야드적치Bed번호
							jrParam.setField("YS_STK_LYR_NO", ydDnWrLayer); //변경된 단
							
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsStkLyrClr", logId, methodNm, "적치단 Clear");
							
							//이전 권하위치 단을 Clear 한다.
							//jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
							//jrParam.setField("YS_STK_BED_NO", "%"); 
							//commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtl", logId, methodNm, "적치단 작업Clear ");
							
							//이후 쿼리 파라메터로 YD_STK_BED_NO 를 사용하는 쿼리가 있음으로 변경된 Bed번호를 다시 설정한다.
							//jrParam.setField("YD_STK_BED_NO", ydDnWrLoc.substring(6, 8)); 
						}
						
						if ("".equals(ydCrnXaxis) || "".equals(ydCrnYaxis) || "".equals(ydCrnZaxis)) {
							ydCrnXaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_XAXIS"));
							ydCrnYaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_YAXIS"));
							ydCrnZaxis = commUtils.trim(jrChk.getFieldString("YD_DN_WR_ZAXIS"));
						}						
					}
				}
				
			} else {
				//일반 적치대 단 체크 
				
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006Curr", logId, methodNm, "현재정보  조회");
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);
					String tbDnWrLayer = commUtils.trim(jrChk.getFieldString("YD_DN_WR_LAYER"));

					if (!ydDnWrLayer.equals(tbDnWrLayer)) {
						
						//SP일 경우만 적용
						if("SP".equals(ydDnWrLoc.substring(2,4))) {
							commUtils.printLog(logId, "권하위치[" + ydDnWrLoc + "] 적치단 변경 : " + ydDnWrLayer + " -> " + tbDnWrLayer, "SL");
							chgDnWrLayer = true;
							ydDnWrLayer  = tbDnWrLayer;
							//ydCrnZaxis   = "";
							
							//새 권하위치 단을 Clear 한다.
							jrParam.setField("YS_STK_COL_GP", ydDnWrLoc.substring(0, 6)); //야드적치열구분
							jrParam.setField("YS_STK_BED_NO", ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
							jrParam.setField("YS_STK_LYR_NO", ydDnWrLayer); //변경된 단
							
							commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsStkLyrClr", logId, methodNm, "적치단 Clear");
							
							//이전 권하위치 단을 Clear 한다.
							//jrParam.setField("YD_CRN_SCH_ID", ydCrnSchId); //야드크레인스케쥴ID
							//commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtl", logId, methodNm, "적치단 작업Clear ");
						}
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
			jrParam.setField("YD_STK_BED_NO"   , ydDnWrLoc.substring(6, 8)); //야드적치Bed번호
			
			/**********************************************************
			* 3. 전문 전송
			* 3.1 L2 장입 Carry-In완료(YSM3L101)
			* 3.2 생산통제 장입진행실적(대형압연장입실적:YSCUJ031)
			**********************************************************/
//			if("BBTZ01UM".equals(ydSchCd) && "TZ".equals(ydDnWrLoc.substring(2, 4))) { //B동 장입
			if("BRT".equals(ydDnWrLoc.substring(1, 4)) || "BTZ".equals(ydDnWrLoc.substring(1, 4)) || "BZY".equals(ydDnWrLoc.substring(1, 4))) { //B동 장입
				
				//장입 Carry-in 완료 송신
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSM3L101", jrParam));
				
				//생산통제 장입진행실적 송신
				jrParam.setField("CHG_SUP_PROG_STAT" , "30"         ); //장입대적치완료 (30)
				jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSCUJ031", jrParam));
			} 
			
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
				jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006TCarSchLd", logId, methodNm, "대차상차스케쥴 조회!!!");
				
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
					}

					//대차스케줄 야드설비작업매수, 중량, 야드차량진행상태, 야드상차개시일시, 야드상차완료일시 등 수정
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006TcarSchLd", logId, methodNm, "대차스케줄 수정");
					
					//L2 대차작업실적
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L006", jrParam));
					
					//L2 영대차출발지시
					if ("Y".equals(tcarLdCmplYn)) {
						jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL2("YSN1L005", jrParam));
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
			String carLdCmplYn = "N";
			String ydCarUseGp  = "";
			String ydCarSchId  = "";
			String car_no  	= "";
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
				     , CASE WHEN BAY_LYR_CNT <= BAY_CAR_CNT + CRNMTL_CNT THEN 'Y' ELSE'N' END AS CAR_BAY_LD_CMPL_YN
				  FROM
				       (
				        SELECT TS.YD_CAR_SCH_ID
				              ,TS.YD_CAR_USE_GP
				              ,NVL(TS.CAR_NO,TS.TRN_EQP_CD) AS CAR_NO
				              ,TS.TRANS_ORD_DATE
				              ,TS.TRANS_ORD_SEQNO
				              ,TS.DEST_TEL_NO
				              ,SC.YS_STK_COL_GP
				              ,SC.WLOC_CD
				              ,SC.YD_PNT_CD
				              ,(CASE WHEN SC.YD_CAR_USE_GP = 'G'
				                THEN (
				                        SELECT COUNT(*)    -- 출하 차량상차 대상매수
				                          FROM TB_YS_STOCK
				                         WHERE TRANS_ORD_DATE = TS.TRANS_ORD_DATE
				                           AND TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				                           AND CAR_NO = TS.CAR_NO
				                           AND SC.YD_CAR_USE_GP = 'G'
				                        )
				                 ELSE       
				                ( SELECT COUNT(*)    --구내운송 대상매수
				                  FROM  USRYSA.TB_YS_WRKBOOKMTL A
				                 WHERE A.YD_WBOOK_ID =(CASE WHEN TS.YD_CAR_PROG_STAT IN('1','2','3','4','5') THEN TS.YD_CARLD_WRK_BOOK_ID ELSE TS.YD_CARUD_WRK_BOOK_ID END)
				                 )
				                 END
				               ) AS STOCK_CNT
				              ,(SELECT COUNT(DISTINCT(A.SSTL_NO))   -- 해당동 차량 상차대상 매수
				                  FROM TB_YS_STOCK A
				                     , TB_PB_BUNDLECOMM B
				                 WHERE A.SSTL_NO  = B.BNDL_NO
				                   AND A.TRANS_ORD_DATE = TS.TRANS_ORD_DATE
				                   AND A.TRANS_ORD_SEQNO = TS.TRANS_ORD_SEQNO
				                   AND A.CAR_NO = TS.CAR_NO
				                   --AND B.YD_STK_LYR_MTL_STAT IN ('C','U')
				                   AND SUBSTR(B.YS_STR_LOC,1,2) = SUBSTR(SC.YS_STK_COL_GP,1,2)
				               ) AS BAY_LYR_CNT
				             ,(SELECT COUNT(*)    -- 차량에 적재된 매수
				                  FROM TB_YS_CARFTMVMTL A
				                     , TB_PB_BUNDLECOMM B
				                 WHERE A.SSTL_NO  = B.BNDL_NO
				                   AND A.YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID
				                   AND SUBSTR(B.YS_STR_LOC,1,2) = SUBSTR(SC.YS_STK_COL_GP,1,2)
				                )  AS BAY_CAR_CNT
				                 
				             ,(SELECT COUNT(*)    -- 차량에 적재된 매수
				                  FROM TB_YS_CARFTMVMTL
				                 WHERE YD_CAR_SCH_ID = TS.YD_CAR_SCH_ID)  AS LYR_CNT
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
				           AND TS.YD_CAR_SCH_ID  IN (SELECT MAX(YD_CAR_SCH_ID)
				                                      FROM TB_YS_CARSCH   C
				                                     WHERE C.CAR_NO=TS.CAR_NO 
				                                     UNION ALL
				                                     SELECT MAX(YD_CAR_SCH_ID)
				                                      FROM TB_YS_CARSCH   C
				                                     WHERE C.TRN_EQP_CD=TS.TRN_EQP_CD 
				                                     )
				        )  
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchLd", logId, methodNm, "상차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

//					jrParam.setField("YD_CAR_SCH_ID", commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"))); //야드차량스케쥴ID(이력등록시에도 사용)
					
					ydCarSchId  = commUtils.trim(jrChk.getFieldString("YD_CAR_SCH_ID"));
					ydCarUseGp 	= commUtils.trim(jrChk.getFieldString("YD_CAR_USE_GP")); //야드차량사용구분
					carLdCmplYn = commUtils.trim(jrChk.getFieldString("CAR_LD_CMPL_YN")); //차량상차완료여부
					car_no		= commUtils.trim(jrChk.getFieldString("CAR_NO")); //출하: CAR_NO , 구내운송: TRN_EQP_CD
					
					commUtils.printLog(logId, car_no+ "-차량 상차 완료여부 : " + carLdCmplYn, "SL");
					
					
					//차량이송재료(TB_YS_CARFTMVMTL) 상차 등록
					recPara = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_CAR_SCH_ID" , ydCarSchId ); 
					recPara.setField("YD_CRN_SCH_ID", ydCrnSchId);	 // 추가
					recPara.setField("MODIFIER" , modifier     ); 
				
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
					recPara.setField("YD_CAR_USE_GP" 	, ydCarUseGp     ); 
					/* com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006CarSchLd 
					--크레인권하실적 상차 차량스케줄 수정
					MERGE INTO TB_YS_CARSCH TS USING (
					SELECT A.YD_CAR_SCH_ID
					     , A.YD_CAR_PROG_STAT
					     , A.YD_EQP_WRK_SH
					     , A.YD_EQP_WRK_WT
					     , A.YD_WBOOK_ID
					     , A.YD_CARLD_STOP_LOC
					     , A.WR_DT
					     , CASE WHEN SUBSTR(A.YD_CARLD_STOP_LOC,1,1) = 'B' AND A.YD_CAR_USE_GP = 'L' THEN
					                 NVL( (SELECT DECODE(AA.YD_AIM_BAY_GP,'A','S3Y10','C','S3Y30','S3Y11') 
					                         FROM TB_YS_PREPSCH AA
					                        WHERE AA.YD_WBOOK_ID = A.YD_WBOOK_ID 
					                      )
					                     -- DEFAULT SET 
					                     ,( DECODE(SUBSTR(A.YD_CARLD_STOP_LOC,2,1),'A','S3Y30','C','S3Y11','S3Y30')
					                      )
					                    ) 
					            ELSE 'XXXXX'  END AS ARR_WLOC_CD
					  FROM (
					        SELECT TM.YD_CAR_SCH_ID
					              ,:V_YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
					              ,COUNT(*)            AS YD_EQP_WRK_SH
					              ,SUM(ST.YD_MTL_WT)   AS YD_EQP_WRK_WT
					              ,:V_YD_WBOOK_ID      AS YD_WBOOK_ID
					              ,:V_YS_STK_COL_GP    AS YD_CARLD_STOP_LOC
					              ,:V_YD_CAR_USE_GP    AS YD_CAR_USE_GP 
					              ,NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
					          FROM TB_YS_CARFTMVMTL TM
					              ,TB_YS_STOCK      ST
					         WHERE TM.SSTL_NO       = ST.SSTL_NO
					           AND TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
					           AND TM.DEL_YN        = 'N'
					         GROUP BY TM.YD_CAR_SCH_ID
					       )  A 
					) DD ON (TS.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID)
					WHEN MATCHED THEN UPDATE SET
					     TS.MODIFIER             = :V_MODIFIER
					    ,TS.MOD_DDTT             = SYSDATE
					    ,TS.YD_EQP_WRK_STAT      = 'L' --영차
					    -- ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT    
					    ,TS.YD_CAR_PROG_STAT     = '5'
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
					recPara.setField("YD_WBOOK_ID"	, ydWbookId); 
					
					//출하차량(G)
					if ("G".equals(ydCarUseGp)) {
						//출하관리 일품출하상차실적
						
						// PIDEV						
//						if("Y".equals(sApplyYnPI)) {
							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1084", recPara));
//						} else {
//							jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ007", recPara));
//						}
						
						//출하관리 출하상차완료
						if ("Y".equals(carLdCmplYn)) {
							
							//PIDEV							
//							if("Y".equals(sApplyYnPI)) {
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("M10YDLMJ1094", recPara));
//							} else {
//								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSDSJ008", recPara));
//							}
							
						}						
					}
					if ("L".equals(ydCarUseGp)) {
						
//이송관련 추가						
					//직상차 나 권하위치 변경 처리시 이송LOT에 없는 대상을 권상했을경우 이송 LOT 생성처리 함	
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.getPrepSchCrnWrkChk  
						SELECT COUNT(*) AS LOT_CNT
						  FROM TB_YS_PREPSCH A
						     , TB_YS_PREPMTL B
						 WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
						   AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
						   AND B.SSTL_NO IN (
						                    SELECT SSTL_NO 
						                      FROM TB_YS_CRNWRKMTL
						                     WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
						                    ) 
						*/
						JDTORecord jrChk2 = null;
						JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
						JDTORecordSet jsChk2 = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getPrepSchCrnWrkChk", logId, methodNm, "이송LOT 여부"); 
						if (jsChk2.size() > 0) {
							jrChk2 = jsChk2.getRecord(0);
							if(commUtils.trim(jrChk2.getFieldString("LOT_CNT" )).equals("0")){
								
								String ydPrepSchId = commDao.getSeqId(logId, methodNm, "PrepSch");
								
								if ("".equals(ydPrepSchId)) {
									throw new Exception("준비스케쥴ID 생성 실패");
								}
								jrParam1.setResultCode(logId);	//Log ID
								jrParam1.setResultMsg(methodNm);	//Log Method Name	
								jrParam1.setField("YD_PREP_SCH_ID"	, ydPrepSchId); //야드준비스케쥴ID
								jrParam1.setField("YD_SCH_CD"		, ydEqpId.substring(0, 2)+ "TR02LM"); //스케줄코드
								jrParam1.setField("YD_CAR_SCH_ID"	, ydCarSchId); 
								jrParam1.setField("YD_CRN_SCH_ID"	, ydCrnSchId); 
								jrParam1.setField("YD_WBOOK_ID"		, ydWbookId); 
								jrParam1.setField("MODIFIER" 		, modifier     ); 
								
								commDao.insert(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepMtlUp", logId, methodNm, "준비재료 등록");
								
								commDao.insert(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.insPrepSchUp", logId, methodNm, "준비스케줄 등록");
								
								//직상차 인 경우 상차개시 전문
								JDTORecord jrYdMsg9 = JDTORecordFactory.getInstance().create();
								jrYdMsg9.setResultCode(logId);	//Log ID
								jrYdMsg9.setResultMsg(methodNm);	//Log Method Name
								jrYdMsg9.setField("YS_STK_COL_GP" 	, ydDnWrLoc.substring(0, 6) ); 
								//전송할 전문에 추가
//								jrRtn = commUtils.addSndData(jrRtn, jrYdMsg9);
								jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSTSJ007", jrYdMsg9));	
							}
						}	
///						
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
				  FROM TB_YS_CARSCH     TS
				      ,TB_YS_CARFTMVMTL TM
				 WHERE TS.YD_CAR_SCH_ID        = TM.YD_CAR_SCH_ID(+)
				   AND TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
				   AND TS.DEL_YN               = 'N'
				   AND TM.DEL_YN(+)            = 'N'
				 GROUP BY TS.YD_CAR_SCH_ID
					   */
				jsChk = commDao.select(recPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getNxYSL006CarSchUd", logId, methodNm, "하차 차량스케줄 조회 "); 
		    	
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

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
			jrParam2.setField("YS_STK_COL_GP", "B%"); 
			jrParam2.setField("YS_STK_BED_NO", "%"); 
			commDao.update(jrParam2, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpDnWrkMtl", logId, methodNm, "적치단 작업Clear ");
			
			
			//박종호 권하위치에 재료 셋팅.
			//적치단(크레인 및 권하위치) 수정
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StkLyrBLBT", logId, methodNm, "적치단(크레인 및 권하위치) 수정");
			
			
			/**********************************************************
			* 7. 권상실적위치가 샷블라스트
			*    보급위치에서 추출위치로 자동 변경(보급:BCSB01 -> 추출:BCSB02 )
			**********************************************************/
    
			if (!"SB".equals(ydUpWrLoc.substring(2, 4)) && "SB".equals(ydDnWrLoc.substring(2, 4))) {
				//저장위치 수정(추출존 으로 저장위치 변경)
				int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updStklayerEqp", logId, methodNm, "저장위치 수정");
				
				if(intRtnVal >=1){
					//이전 입측 저장위치 비우기
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.clrUpdStklayerEqp", logId, methodNm, "저장위치 재료Clear");
				}
				
			}
			
			
			// 분리 작업이고 다음 작업 이 있으면 
			// 현재  권하 위치처리 후 해당 재료의 적치 상태를 U로 변경처리 한다.
			if(szYD_TO_LOC_DCSN_MTD.equals("C") && !szNEXT_YD_CRN_SCH_ID.equals("")) {
				jrParam.setField("NEXT_YD_CRN_SCH_ID", szNEXT_YD_CRN_SCH_ID); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchSpr 
				--권상실적위치
				MERGE INTO TB_YS_STKLYR SL USING (
				SELECT SUBSTR(A.YS_UP_WO_LOC,1,6) AS YS_STK_COL_GP 
				      ,SUBSTR(A.YS_UP_WO_LOC,7,2) AS YS_STK_BED_NO 
				      ,A.YS_UP_WO_LAYER           AS YS_STK_LYR_NO 
				      ,TO_CHAR(ROWNUM,'FM00')     AS YS_STK_SEQ_NO 
				      ,B.SSTL_NO 
				      ,'U'  AS YD_STK_LYR_MTL_STAT --권상대기
				  FROM TB_YS_CRNSCH A 
				      ,TB_YS_CRNWRKMTL B 
				      ,TB_YS_STKLYR    C 
				 WHERE A.YD_CRN_SCH_ID  = B.YD_CRN_SCH_ID 
				   AND B.SSTL_NO  = C.SSTL_NO
				   AND A.YD_CRN_SCH_ID  = :V_NEXT_YD_CRN_SCH_ID
				   AND C.YD_STK_LYR_MTL_STAT = 'C' 
				   AND A.DEL_YN = 'N' 
				   AND B.DEL_YN = 'N'
				UNION ALL   
				SELECT SUBSTR(A.YS_DN_WO_LOC,1,6) AS YS_STK_COL_GP 
				      ,SUBSTR(A.YS_DN_WO_LOC,7,2) AS YS_STK_BED_NO 
				      ,A.YS_DN_WO_LAYER           AS YS_STK_LYR_NO 
				      ,TO_CHAR(ROWNUM,'FM00')     AS YS_STK_SEQ_NO 
				      ,B.SSTL_NO 
				      ,'D'  AS YD_STK_LYR_MTL_STAT --권하대기
				  FROM TB_YS_CRNSCH A 
				      ,TB_YS_CRNWRKMTL B 
				      ,TB_YS_STKLYR    C 
				 WHERE A.YD_CRN_SCH_ID  = B.YD_CRN_SCH_ID 
				   AND B.SSTL_NO  = C.SSTL_NO
				   AND A.YD_CRN_SCH_ID  = :V_NEXT_YD_CRN_SCH_ID
				   AND C.YD_STK_LYR_MTL_STAT = 'C' 
				   AND A.DEL_YN = 'N' 
				   AND B.DEL_YN = 'N'
				) DD ON (SL.YS_STK_COL_GP = DD.YS_STK_COL_GP AND SL.YS_STK_BED_NO = DD.YS_STK_BED_NO AND SL.YS_STK_LYR_NO = DD.YS_STK_LYR_NO AND SL.YS_STK_SEQ_NO = DD.YS_STK_SEQ_NO)
				WHEN MATCHED THEN UPDATE SET
					 SL.MODIFIER            = :V_MODIFIER
				    ,SL.MOD_DDTT            = SYSDATE
				    ,SL.SSTL_NO              = DD.SSTL_NO
				    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT
				   */ 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchSpr", logId, methodNm, "");
			}
			if(szYD_TO_LOC_DCSN_MTD.equals("C") && !szNEXT_NEXT_YD_CRN_SCH_ID.equals("")) {
				jrParam.setField("NEXT_NEXT_YD_CRN_SCH_ID", szNEXT_NEXT_YD_CRN_SCH_ID); 
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchSprNextCrn 
				MERGE INTO TB_YS_STKLYR SL USING (
				SELECT SUBSTR(A.YS_DN_WO_LOC,1,6) AS YS_STK_COL_GP 
				      ,SUBSTR(A.YS_DN_WO_LOC,7,2) AS YS_STK_BED_NO 
				      ,A.YS_DN_WO_LAYER           AS YS_STK_LYR_NO 
				      ,B.YS_STK_SEQ_NO            AS YS_STK_SEQ_NO 
				      ,B.SSTL_NO 
				      ,'D'  AS YD_STK_LYR_MTL_STAT --권하대기
				  FROM TB_YS_CRNSCH A 
				      ,TB_YS_CRNWRKMTL B 
				      ,TB_YS_STKLYR    C 
				 WHERE A.YD_CRN_SCH_ID  = B.YD_CRN_SCH_ID 
				   AND B.SSTL_NO  = C.SSTL_NO
				   AND A.YD_CRN_SCH_ID  = :V_NEXT_NEXT_YD_CRN_SCH_ID
				   AND C.YD_STK_LYR_MTL_STAT = 'C' 
				   AND A.DEL_YN = 'N' 
				   AND B.DEL_YN = 'N'
				) DD ON (SL.YS_STK_COL_GP = DD.YS_STK_COL_GP AND SL.YS_STK_BED_NO = DD.YS_STK_BED_NO AND SL.YS_STK_LYR_NO = DD.YS_STK_LYR_NO AND SL.YS_STK_SEQ_NO = DD.YS_STK_SEQ_NO)
				WHEN MATCHED THEN UPDATE SET
				     SL.MODIFIER            = :V_MODIFIER
				    ,SL.MOD_DDTT            = SYSDATE
				    ,SL.SSTL_NO              = DD.SSTL_NO
				    ,SL.YD_STK_LYR_MTL_STAT = DD.YD_STK_LYR_MTL_STAT
				   */ 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchSprNextCrn", logId, methodNm, "");
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
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006WbMtlDelBySchIdBLBT", logId, methodNm, "크레인권하실적 권하완료된 작업예약재료만 삭제");
			}
			
			/**********************************************************
			* 8. 공통 Table, 저장품, 작업이력 등록 (순서 변경 안됨)
			* 8.1 주편 및 Slab 공통 Table 수정
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
			* 8.4.1 진행관리 Slab이송완료실적(YDPTJ001) 전송
			*     - 하차완료 시 차량이송재료 현재진도코드 수정 후
			* 8.4.1 소재이송지시 수정 : 권하실적재료 적치단 수정 후
			*     - 상차 : 이송상차일자, 야드재료예정저장From위치코드
			*     - 하차 : 이송완료일자, 이송계상일자, 이송상태코드(*:작업완료), 야드재료예정저장To위치코드
			* 8.4.2 주편 및 Slab 공통 Table 수정
			*     - 상차 : 소재이송일시
			*     - 하차 : 소재인수일시
			**********************************************************/
			//BLOOM공통 수정** (서냉피트작업일시 셋팅)
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006BloomComm", logId, methodNm, "BLOOM공통 수정");

			//이송지시 수정 -- WC
			if ("Y".equals(carUdCmplYn)){ // 하차 - 권하실적작업시 
				
				JDTORecordSet jsTemp = null;
				JDTORecord jrTemp = null;
				String szCurrProgCd	= "";
				String szOrdYeojaeGp	= ""; 
				String szSstlNo= ""; 
				
				// BLOOM공통에서 현재진도코드를 가져온다. 
				jsTemp = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStlfrToMoveBl", logId, methodNm, "하차 대상 조회");
				
				if (jsTemp != null && jsTemp.size() > 0) {
				
					for (int Loop_i = 1; Loop_i <= jsTemp.size(); Loop_i++){
						jsTemp.absolute(Loop_i);
						jrTemp  = jsTemp.getRecord(); 
 
					szSstlNo		= commUtils.trim(jrTemp.getFieldString("SSTL_NO"));
					szCurrProgCd	= commUtils.trim(jrTemp.getFieldString("CURR_PROG_CD"));
					szOrdYeojaeGp	= commUtils.trim(jrTemp.getFieldString("ORD_YEOJAE_GP")); 
					
					
					jrParam.setField("SSTL_NO"		, szSstlNo);
					
					//이송하차완료 처리
					
					//1)BLOOM 공통의 위치와 진도코드를 변경
					//   1-1) To 위치가 D,E동이 아닌경우
					//       - 주문재 이면 진도코드를 B로 변경
					//       - 여재 이면 진도코드를 Y로 변경
					//   1-2) To 위치가 D,E동(부두) 
					//       - 주문재이면 진도코드를 D로 변경
					//       - 여재이면 진도코드를 Y로 변경
					if(!"D".equals(ydDnWrLoc.substring(1,2)) && !"E".equals(ydDnWrLoc.substring(1,2))) { //D,E동이 아닌경후 --A,B,C동
						if("1".equals(szOrdYeojaeGp)) { //주문재인경우 
							jrParam.setField("CURR_PROG_CD"		, "B" );
						} else if("2".equals(szOrdYeojaeGp)) { //여재인경우
							jrParam.setField("CURR_PROG_CD"		, "Y" );
						} else {
							jrParam.setField("CURR_PROG_CD"		, szCurrProgCd );
						}
					} else { //D,E동인경우
						if("E".equals(szCurrProgCd)) { //현재진도가 이송작업대기 이면서..
							
							if("1".equals(szOrdYeojaeGp)) { //주문재인경우 
								jrParam.setField("CURR_PROG_CD"		, "D" );
							} else if("2".equals(szOrdYeojaeGp)) { //여재인경우
								jrParam.setField("CURR_PROG_CD"		, "Y" );
							} else {
								jrParam.setField("CURR_PROG_CD"		, szCurrProgCd );
							}
						} else {
							jrParam.setField("CURR_PROG_CD"		, szCurrProgCd );
						}
					}
					commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBlCommYsProgCd", logId, methodNm, "BLOOM공통 진도코드 수정");
					
					//진도코드 변경했을 경우 진행관리로 YSPBJ001 전송
					jrRtn = commUtils.addSndData(jrRtn, commDao.getMsgL3("YSPBJ001", jrParam));
					
					}

			    } else {
					throw new Exception("제품번호로 하차작업대상여부 조회시 에러가 발생했습니다!");
			    }
				 
				
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StlfrToMoveBl", logId, methodNm, "이송지시 수정BL");	
			}
			
			//저장품 수정**  (서냉피트 BLOOM냉각방법 NULL 셋팅)
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updNxYSL006StockByBloom", logId, methodNm, "저장품 수정");
			
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
				jrRtn = commUtils.addSndData(jrRtn, blYsComm.getYSN1L004(resMsg));
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
			jrRtn = commUtils.addSndData(jrRtn, this.rcvN1YSL004(jrYdMsg));
			
//////// PIDEV 2022-07-11 YYS //////////////////////////////////////////////////////////////////////////////////////////			
			//블룸 이송실적 생산실적 발생시점에 연계
			//조업에서 빌렛실적이 발생하는 시점에  생산통계에서  이송실적을 만들어서 PP로 연계함.
				/*
				 * MES_PI 2022-07-11
				 * YYS 당진공장 내 특수강 이송실적 통계로 송신
				 * USRPDA.SP_SS_PD_MATL_FTMV_WR_MAIN('WBV041018','KD01','KDCS','20220711',:v1)
				 * SELECT A.YD_WRK_HDS_DD 
					      ,B.SSTL_NO
					  FROM TB_YS_CRNSCH A
					     , TB_YS_CRNWRKMTL B
					 WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID
					   AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
					   
				    SELECT NVL(USRYSA.SF_YS_IS_B_TRANSFER (:V_CRN_SCH_ID),0) AS TRANSFER FROM DUAL
				 
				 jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getN1YSL006Transfer_PIDEV", logId, methodNm, "적치동에 따른 상태값 조회");
				
				if (jsChk.size() > 0) {
					jrChk = jsChk.getRecord(0);

					String sTransger = commUtils.trim(jrChk.getFieldString("TRANSFER")); //대차상차완료여부					
					
					String fromLoc = "";
					String toLoc   = "";
					JDTORecord recordSp = null;
					int[] inParamIndex = {1,2,3,4};
					if("1".equals(sTransger)){//A,C동 블룸야드 → 대형압연 가열로소재장(B동)으로 이동시     S110	 S210 --입고
						fromLoc = "S110";
						toLoc   = "S210";	
					}else if("2".equals(sTransger)){//대형압연 가열로소재장(B동)으로 이동시 → A,C동 블룸야드   S210	 S110 --반입
						fromLoc = "S210";
						toLoc   = "S110";								
					}
					
					if("1".equals(sTransger) || "2".equals(sTransger) ){
						 //크레인작업지시 대상 재료 조회
						JDTORecordSet jsCrnWrkMtl = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnWrkMtl_PIDEV", logId, methodNm, "크레인작업지시 대상 재료 조회");

						for (int ii = 0; ii < jsCrnWrkMtl.size(); ii++) {
							String sstlNo     = jsCrnWrkMtl.getRecord(ii).getFieldString("SSTL_NO");
							String ydWrkHdsDd = jsCrnWrkMtl.getRecord(ii).getFieldString("YD_WRK_HDS_DD");
							Object[] inParam = {sstlNo, fromLoc, toLoc, ydWrkHdsDd};	
							recordSp = commDao.callProcedure(inParam, inParamIndex, "com.inisteel.cim.ys.common.dao.YsCommDao.callProcedure_PIDEV");
							
							commUtils.printLog(logId, "sstlNo : "+sstlNo, "SL");
							commUtils.printLog(logId, "ydWrkHdsDd : "+ydWrkHdsDd, "SL");
							//String outRtnCode = commUtils.trim(recordSp.getFieldString("OUT_RTN_CODE"));
							//commUtils.printLog(logId, "outRtnCode : "+outRtnCode, "SL");
						}
					}
					
				*/
			
//			}
			
//////////////////////////////////////////////////////////////////////////////////////////////////
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
					resConn.trx("sndToEAI", new Class[] { JDTORecord.class }, new Object[] { blYsComm.getYSN1L004(resMsg) });
				} catch (Exception se) {}
			}

			if (e instanceof DAOException) {
				throw (DAOException)e;
			}

			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	/**
	 *      [A] 오퍼레이션명 : 블룸_Carry-In요구
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public JDTORecord rcvM3YSL101(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "블룸_Carry-In요구[BlYsL2RcvSeEJB.rcvM3YSL101] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		
		//YD_EQP_ID	야드설비ID	CHAR	6
		//YS_STK_BED_NO	특수강야드적치Bed번호	CHAR	2
	
		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String msgId        = commUtils.getMsgId(rcvMsg); 								//EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String ydeqpid 		= commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); 		//야드설비ID
			String ysstkbedno   = commUtils.trim(rcvMsg.getFieldString("YS_STK_BED_NO")); 	//특수강야드적치Bed번호
			String modifier     = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); 		//수정자(Backup Only)
			String L3Hmi        = commUtils.nvl(rcvMsg.getFieldString("L3_HMI"),"N");       //백업화면 기동 여부
			String L3PreWork    = commUtils.nvl(rcvMsg.getFieldString("L3_PRE_WORK"),"N");  //선작업 수행지시(명령선택에서 장입크레인이 IDLE상태일때)
			
			//=================================================================================
			//BBTZ01 설비가 보급베드스케줄코드를 따로 생성
			String szYD_SCH_CD  = "" ;
			//=================================================================================
			int intCarryInCnt 	= 0;
			/*
			 * 장입할 설비 선택 기준
			 * BBTZ01 - N
			 * BBRT01 - Y
			 */
			JDTORecord jrEqpID = JDTORecordFactory.getInstance().create();
			
			jrEqpID.setField("REPR_CD_GP"  , "BBRT01"); 
			jrEqpID.setField("CD_GP"       , "*"  );  
			JDTORecordSet jsEqp = commDao.select(jrEqpID, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsRuleList", logId, methodNm, "장입할 설비 선택 기준 조회");

			if(jsEqp.size() <= 0) {
				throw new Exception("장입할 설비 선택 기준 조회 실패!!");
			} 
			
			String isEqpID = commUtils.trim(jsEqp.getRecord(0).getFieldString("ITEM"));
			
			if("Y".equals(isEqpID)){
				ydeqpid 	 	= "BBRT01";
				szYD_SCH_CD  	= "BBRT01UM" ;
				intCarryInCnt 	= 1;
			}else{
				ydeqpid 	 	= "BBTZ01";
				szYD_SCH_CD  	= "BBTZ01UM" ;
				intCarryInCnt 	= 3;
			}
			
			if ("".equals(modifier)) { modifier = msgId; }

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			
			if (!ydeqpid.equals(commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")))) {
				throw new Exception("L3기준설비id["+ydeqpid+"]와 L2수신설비ID["+commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"))+"]가 다릅니다.");
			}
			
			if ("".equals(ydeqpid)) {
				throw new Exception("야드설비id 가 없습니다.");
			} else if ("".equals(ysstkbedno)) {
				throw new Exception("특수강야드적치Bed번호 값이 없습니다.");
			}
	
			int iCRN_WRK_CNT = 0;
			int iPRE_WRK_CNT = 0;
			int iWRKBOOK_CNT = 0;
			String sYD_CRN_SCH_ID = "";
			
			String sBED_CLEAR_YN = "N";
			String sSCH_START_YN = "N";
			JDTORecord jrRtn = JDTORecordFactory.getInstance().create();
			
			JDTORecord jrParam1 = JDTORecordFactory.getInstance().create();
			JDTORecord RecWrk = JDTORecordFactory.getInstance().create();
			jrParam1.setField("YD_SCH_CD"  , szYD_SCH_CD); 
			
			JDTORecordSet jsWrk = commDao.select(jrParam1, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsWrkCnt", logId, methodNm, "작업지시 편성여부");
			if(jsWrk.size() > 0) {
				jsWrk.first();
				RecWrk  = jsWrk.getRecord();
				iCRN_WRK_CNT 	= commUtils.paraRecChkNullInt(RecWrk,"CRN_WRK_CNT"); 
				iPRE_WRK_CNT 	= commUtils.paraRecChkNullInt(RecWrk,"PRE_WRK_CNT");
				iWRKBOOK_CNT 	= commUtils.paraRecChkNullInt(RecWrk,"WRKBOOK_CNT");
				sYD_CRN_SCH_ID 	= commUtils.trim(RecWrk.getFieldString("YD_CRN_SCH_ID"));
				
				if (iCRN_WRK_CNT == 0 ){
					sBED_CLEAR_YN = "Y";	// 장입베드 적치정보 Clear작업만 처리
				} 
				
				// 기작업이 있으니 해당 작업예약 기동처리만 함
				if (iCRN_WRK_CNT == 0 && iWRKBOOK_CNT > 0) {
					sSCH_START_YN = "Y";	// 작업예약 스케쥴 기동만 처리		
				}
							
			}			
	
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			
			jrParam.setField("REPR_CD_GP"  , "BBTZ01"); 
			jrParam.setField("CD_GP"       , "*"  );  
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsRuleList", logId, methodNm, "장입자동기준 조회");

			if(jsChk.size() <= 0) {
				throw new Exception("장입자동기준 조회 실패!!");
			} 
			
			String sChgAutoYN = commUtils.trim(jsChk.getRecord(0).getFieldString("ITEM"));
			
			jrParam = JDTORecordFactory.getInstance().create();
			
			jrParam.setField("REPR_CD_GP"  , "BBTZ11"); 
			jrParam.setField("CD_GP"       , "*"  ); 
			JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYsRuleList", logId, methodNm, "선작업기준 조회");

			if(jsChk1.size() <= 0) {
				throw new Exception("선작업기준 조회 실패!!");
			} 
			
			String sPreWorkYn = commUtils.trim(jsChk1.getRecord(0).getFieldString("ITEM"));
			
			commUtils.printLog(logId, methodNm + " L3Hmi:["+L3Hmi+ "]  L3PreWork:["+L3PreWork+ "] CRN_WRK_CNT:["+iCRN_WRK_CNT+ "] PRE_WRK_CNT:["+iPRE_WRK_CNT+ "] WRKBOOK_CNT["+iWRKBOOK_CNT+"], sChgAutoYN["+sChgAutoYN+"], sPreWorkYn["+sPreWorkYn+"]조회 ", "SL");			
			
			/*
			 * 선작업 실행기능 - 2016.09.23 윤재광
			 * 
			 * 크레인 IDLE상태일때 선작업 해라	-> L3PreWork : Y
			 * 선작업기준에 선작업 허용 		-> sPreWorkYn : Y
			 */
			if("Y".equals(L3PreWork) &&
			   "Y".equals(sPreWorkYn)){
				
				L3Hmi 			= "Y";
				sChgAutoYN 		= "Y";
				sBED_CLEAR_YN	= "N";
				sSCH_START_YN	= "N";
			}
						
			// I/F 기동 이고 수동처리 이면 장입대 정보만 삭제처리 함
			if(L3Hmi.equals("N") && sChgAutoYN.equals("N")) {
				if( iCRN_WRK_CNT == 0 ) {
					/**********************************************************
					* 2. 기존 data clear
					**********************************************************/
					this.clearBedData(ydeqpid,ysstkbedno,logId,methodNm);
				}else{
					if(iPRE_WRK_CNT > 0){// 선 작입작업이 있을 경우에 처리
						JDTORecord jrYdMsg = this.shiftBedData(ydeqpid,szYD_SCH_CD,sYD_CRN_SCH_ID,logId,methodNm);
						jrRtn = commUtils.addSndData(jrYdMsg);
					}
				}
			} else {
			
				if (sBED_CLEAR_YN.equals("Y")){			
					/**********************************************************
					* 2. 기존 data clear
					**********************************************************/
					this.clearBedData(ydeqpid,ysstkbedno,logId,methodNm);
				}	
				
				if (sSCH_START_YN.equals("Y")){
					JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
					
					jrYdMsg.setResultCode(logId);	//Log ID
					jrYdMsg.setResultMsg(methodNm);	//Log Method Name
					jrYdMsg.setField("YD_SCH_CD"    , szYD_SCH_CD 	); 	//야드스케쥴코드
					jrYdMsg.setField("YD_SCH_ST_GP" , "U"		); 		//야드스케쥴기동구분
					jrYdMsg.setField("YD_SCH_REQ_GP", "L"     	); 		//야드스케쥴요청구분(인출)
					jrYdMsg.setField("MODIFIER"   	, modifier 	); 		//수정자
		
					jrRtn = JDTORecordFactory.getInstance().create();
					jrRtn = commUtils.addSndData(blYsComm.getCrnSchMsg(jrYdMsg));			
	
				} else { 
					
					if(iWRKBOOK_CNT > 0 ){   // 작업 예약 대기 확인 해서 작업예약이 1개라도 있으면 skip 처리 한다.
						
						if(iPRE_WRK_CNT > 0){// 선 작입작업이 있을 경우에 처리
							JDTORecord jrYdMsg = this.shiftBedData(ydeqpid,szYD_SCH_CD,sYD_CRN_SCH_ID,logId,methodNm);
							jrRtn = commUtils.addSndData(jrYdMsg);
						}
					} else {
						String sTC_SEND = "N";
						/**********************************************************
						* 2. 저장품, 소재이송지시 수정
						**********************************************************/
						jrParam = JDTORecordFactory.getInstance().create();
						JDTORecord RecLotNo = JDTORecordFactory.getInstance().create();
						
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						//적치Bed조회한다. (베드 조회시 적치베드의 상태를 고려하고 조회를 한다.)
						jrParam.setField("YS_STK_COL_GP", ydeqpid);
						jrParam.setField("YS_STK_BED_NO", ysstkbedno);
						
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
						      ..... 
						  FROM TB_YS_STKBED
						 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
						   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
						   AND DEL_YN ='N'
							 */  
						JDTORecordSet RsBed = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkBedBybed", logId, methodNm, "권상 BED 좌표 조회");
						if (RsBed.size() <= 0) {
							throw new Exception("가능 BED 검색 실패");
							
						}
						jrParam = JDTORecordFactory.getInstance().create();
						jrParam.setResultCode(logId);	//Log ID
						jrParam.setResultMsg(methodNm);	//Log Method Name
						jrParam.setField("YD_GP" 	, ydeqpid.substring(0, 1));
						jrParam.setField("BAY_GP"	, ydeqpid.substring(1, 2));
						jrParam.setField("YD_SCH_CD", szYD_SCH_CD);
						
						/* com.inisteel.cim.ys.common.dao.YsCommDAO.getLmillChgLotNo 
						SELECT YD_PREP_SCH_ID
						     , SSTL_NO 
						     , YD_LOC
						     , YS_STK_COL_GP
						     , YS_STK_BED_NO
						     , YS_STK_LYR_NO
						     , YS_STK_SEQ_NO     
						     , RN
						  FROM
						     (
						        SELECT A.YD_PREP_SCH_ID
						             , B.SSTL_NO 
						             , C.YS_STK_COL_GP || C.YS_STK_BED_NO || C.YS_STK_LYR_NO AS YD_LOC
						             , C.YS_STK_COL_GP
						             , C.YS_STK_BED_NO
						             , C.YS_STK_LYR_NO
						             , C.YS_STK_SEQ_NO     
						             , RANK() OVER(ORDER BY A.YD_PREP_SCH_ID) AS RN
						          FROM TB_YS_PREPSCH A
						             , TB_YS_PREPMTL B
						             , (SELECT A1.SSTL_NO
						                     , A1.YS_STK_COL_GP
						                     , A1.YS_STK_BED_NO
						                      ,A1.YS_STK_LYR_NO
						                      ,A1.YS_STK_SEQ_NO
						                  FROM TB_YS_STKLYR  A1
						                 WHERE A1.DEL_YN = 'N'
						                   AND A1.YS_STK_COL_GP     LIKE :V_YD_GP || :V_BAY_GP || '%' 
						                   AND A1.YD_STK_LYR_MTL_STAT IN ('C', 'U')                          -- 적치중 또는 권상대기인 재료 
						                   AND SUBSTR(A1.YS_STK_COL_GP,3,2) <> SUBSTR(:V_YD_SCH_CD,3,2)      -- 장입대 정보 삭제 'TZ','RT' 
						                   ORDER BY A1.YS_STK_COL_GP DESC, A1.YS_STK_LYR_NO DESC, A1.YS_STK_BED_NO DESC
						             ) C 
						         WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID 
						           AND B.SSTL_NO    = C.SSTL_NO
						           AND A.DEL_YN     = 'N'
						           AND B.DEL_YN     = 'N'
						           AND A.YD_GP      = :V_YD_GP
						           AND A.YD_SCH_CD  = :V_YD_SCH_CD
						           AND B.SSTL_NO NOT IN (         
						                                    -- 작업예약에 등록된 재료 제외 
						                                        SELECT SSTL_NO 
						                                          FROM TB_YS_WRKBOOK A,
						                                               TB_YS_WRKBOOKMTL B
						                                         WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
						                                           AND A.DEL_YN = 'N'
						                                           AND A.YD_SCH_CD LIKE :V_YD_GP || :V_BAY_GP || SUBSTR(:V_YD_SCH_CD,3,2) ||'__U%'
						                                      )
						        ORDER BY A.YD_PREP_SCH_ID, C.YS_STK_COL_GP DESC, C.YS_STK_LYR_NO DESC, C.YS_STK_BED_NO DESC, C.YS_STK_SEQ_NO 
						      ) 
						  WHERE RN = 1
			
						  */
						
						JDTORecordSet RsLotNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLmillChgLotNo", logId, methodNm, "보급 Lot 조회");
						
						if (RsLotNo.size() <= 0) {
				
							//보급 Lot 편성 대상 재료 Select
							//select 는 3매 기준으로 BED 정보 READ
							/* com.inisteel.cim.ys.common.dao.YsCommDAO.getLmillLotNo 
							SELECT T.HEAT_NO
							     , T.SSTL_NO 
							     , T.YS_STK_COL_GP
							     , T.YS_STK_BED_NO
							     , T.YS_STK_LYR_NO
							     , T.YS_STK_SEQ_NO
							     , T.YD_CHG_NO
							     , T.YD_STK_LOT_CD
							     , T.YD_STK_LOT_TP
							     , T.MTL_CNT
							     , T.MTL_SEQ1
							     , T.YS_STK_COL_GP || T.YS_STK_BED_NO || T.YS_STK_LYR_NO AS YD_LOC
							FROM
							    (
							    SELECT X.SPST_PLN_HEAT_NO AS HEAT_NO
							          ,X.SSTL_NO 
							          ,Y.YS_STK_COL_GP
							          ,Y.YS_STK_BED_NO
							          ,Y.YS_STK_LYR_NO
							          ,Y.YS_STK_SEQ_NO
							          ,X.YD_CHG_NO
							          ,X.MOD_DDTT
							          ,X.YD_STK_LOT_CD
							          ,X.YD_STK_LOT_TP
							          ,COUNT(*) OVER( PARTITION BY YD_CHG_NO,YS_STK_COL_GP,YS_STK_BED_NO,YS_STK_LYR_NO ORDER BY YD_CHG_NO,YS_STK_COL_GP,YS_STK_LYR_NO  DESC,YS_STK_BED_NO) MTL_CNT
							          ,RANK()   OVER( ORDER BY YD_CHG_NO,YS_STK_COL_GP,YS_STK_LYR_NO DESC, YS_STK_BED_NO DESC) MTL_SEQ1
							    FROM   (SELECT B.SPST_PLN_HEAT_NO
							                  ,B.SSTL_NO
							                  ,B.YD_CHG_NO
							                  ,B.MOD_DDTT
							                  ,B.PTOP_PLNT_GP
							                  ,A.YD_STK_LOT_CD
							                  ,A.YD_STK_LOT_TP
							              FROM (SELECT XX.SPST_PLN_HEAT_NO
							                          ,XX.SSTL_NO    --XX.BLM_NO  AS SSTL_NO
							                          ,XX.YD_CHG_NO
							                          ,XX.MOD_DDTT
							                          ,XX.PTOP_PLNT_GP
							                      FROM USRCUA.TB_CU_LMILLWO XX
							                          ,(SELECT CHG_WO_FR_PNT
							                                  ,CHG_WO_TO_PNT
							                              FROM USRCUA.TB_CU_LMILLWOIDX
							                             WHERE SEQ_NO = (SELECT MAX(SEQ_NO) AS MAX_CT_RCV_SEQ
							                                                   FROM USRCUA.TB_CU_LMILLWOIDX
							                                                  WHERE PTOP_PLNT_GP= 'TA' )
							                               AND  PTOP_PLNT_GP= 'TA') YY
							                    WHERE XX.REFUR_CHG_PLN_SERNO >=YY.CHG_WO_FR_PNT
							                      AND XX.REFUR_CHG_PLN_SERNO <=YY.CHG_WO_TO_PNT 
							                      AND XX.CT_MILL_SPEC_WRK_STAT_GP >= '2'
							                      AND XX.PTOP_PLNT_GP = 'TA'
							                   ) B
							                  ,TB_YS_STOCK A
							             WHERE B.SSTL_NO =A.SSTL_NO
							               AND A.DEL_YN = 'N') X
							         , (SELECT A.SSTL_NO
							                  ,A.YS_STK_COL_GP
							                  ,A.YS_STK_BED_NO
							                  ,A.YS_STK_LYR_NO
							                  ,A.YS_STK_SEQ_NO
							              FROM TB_YS_STKLYR  A
							             WHERE A.DEL_YN = 'N'
							               AND A.YS_STK_COL_GP     LIKE :V_YD_GP || :V_BAY_GP || '%' 
							               AND A.YD_STK_LYR_MTL_STAT IN ('C', 'U')                          -- 적치중 또는 권상대기인 재료 
							               AND SUBSTR(A.YS_STK_COL_GP,3,2) <> 'TZ'                          -- 장입대 정보 삭제
							               ORDER BY A.YS_STK_COL_GP ASC, A.YS_STK_LYR_NO DESC, A.YS_STK_BED_NO DESC) Y 
							       WHERE X.SSTL_NO = Y.SSTL_NO
							         AND Y.SSTL_NO NOT IN (         
							                            -- 작업예약에 등록된 재료 제외 
							                                SELECT SSTL_NO 
							                                  FROM TB_YS_WRKBOOK A,
							                                       TB_YS_WRKBOOKMTL B
							                                 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
							                                   AND A.DEL_YN = 'N'
							                                   AND A.YD_SCH_CD LIKE :V_YD_GP || :V_BAY_GP || 'TZ__U%'
							                              )
							    ORDER BY X.YD_CHG_NO, Y.YS_STK_COL_GP ASC,  Y.YS_STK_LYR_NO DESC, Y.YS_STK_BED_NO DESC, Y.YS_STK_SEQ_NO 
							    ) T
							    WHERE ROWNUM < 4
							*/
							RsLotNo = JDTORecordFactory.getInstance().createRecordSet("Temp");
							
							//동일 베드 기준 장입번호, SEQ순으로 가져옴(RT 1매 장입용) : 분기처리 안할시, 장입순번 높은 앞seq를 대상으로 가져오는 경우 발생.							
							if(szYD_SCH_CD.equals("BBRT01UM"))
							{
								RsLotNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLmillLotNo2", logId, methodNm, "야드 조회");
							}
							//동일 베드 기준 seq순으로 가져옴(TZ장입용. 기존 로직)
							else
							{
								RsLotNo = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getLmillLotNo", logId, methodNm, "야드 조회");
							}

							commUtils.printLog(logId, methodNm + " 윤재광 =RsLotNo.size():["+RsLotNo.size()+ "]   ", "SL");
							
							if (RsLotNo.size() > 0) {
								sTC_SEND = "Y";
							}
						} else {
							sTC_SEND = "Y";
						}
						if(sTC_SEND.equals("Y")) {
							if(intCarryInCnt > RsLotNo.size()) intCarryInCnt = RsLotNo.size();
				
							JDTORecord recOutPara = JDTORecordFactory.getInstance().create();
							JDTORecord recChkPara = JDTORecordFactory.getInstance().create();
							recOutPara.setResultCode(logId);	//Log ID
							recOutPara.setResultMsg(methodNm);	//Log Method Name
							
							String szYdLoc = "";
							String szYdLocFirst = "";
							int intCarryInCntMtl = 0;
							//Carry In 재료기준매수(현재3매)만큼 루프를 돌아 재료번호와 권상모음순서 데이터를 세팅한다.
							for (int Loop_i = 1; Loop_i <= intCarryInCnt; Loop_i++){
								RsLotNo.absolute(Loop_i);
								RecLotNo  = RsLotNo.getRecord();
								
								commUtils.printLog(logId, methodNm + " 윤재광 =SSTL_NO			:["+commUtils.trim(RecLotNo.getFieldString("SSTL_NO"))+ "]   	", "SL");
								commUtils.printLog(logId, methodNm + " 윤재광 =YD_LOC			:["+commUtils.trim(RecLotNo.getFieldString("YD_LOC"))+ "]   	", "SL");
								commUtils.printLog(logId, methodNm + " 윤재광 =YS_STK_SEQ_NO	:["+commUtils.trim(RecLotNo.getFieldString("YS_STK_SEQ_NO"))+ "]", "SL");
								commUtils.printLog(logId, methodNm + " 윤재광 =YD_CHG_NO		:["+commUtils.trim(RecLotNo.getFieldString("YD_CHG_NO"))+ "]   	", "SL");
								
								szYdLoc = commUtils.trim(RecLotNo.getFieldString("YD_LOC")); 
				
								if(Loop_i == 1 ) {
									szYdLocFirst = szYdLoc;
								}
								
								if(szYdLocFirst.equals(szYdLoc)){
									// 기 작업예약 편성 여부 check
									// 편성 되어 있으면 SKIP 처리
									recChkPara = JDTORecordFactory.getInstance().create();
									recChkPara.setField("SSTL_NO"			, commUtils.trim(RecLotNo.getFieldString("SSTL_NO")));
									JDTORecordSet RsLotNoStl = commDao.select(recChkPara, "com.inisteel.cim.ys.common.dao.YsCommDAO.getWbookYN", logId, methodNm, "WBOOK 편성여부 조회");
									
									commUtils.printLog(logId, methodNm + " 윤재광 =WBOOK_YN	:["+commUtils.trim(RsLotNoStl.getRecord(0).getFieldString("WBOOK_YN"))+ "]   ", "SL");
									
									if ((RsLotNoStl.size() > 0) && commUtils.trim(RsLotNoStl.getRecord(0).getFieldString("WBOOK_YN")).equals("N")) {
										
										intCarryInCntMtl++;
										recOutPara.setField("SSTL_NO" + intCarryInCntMtl, commUtils.trim(RecLotNo.getFieldString("SSTL_NO")));
										recOutPara.setField("YD_UP_COLL_SEQ" + intCarryInCntMtl, "" + Loop_i);
										
									} else {
										intCarryInCntMtl = 0;
										break;
									}	
								} else {
									break;
								}
							}
							
							if(intCarryInCntMtl > 0 ) {
								//전문 발생 일시
								//레코드 생성
								recOutPara.setField("JMS_TC_CD"			, "YSYSJ114"); //"YDYDJ241");
								recOutPara.setField("JMS_TC_CREATE_DDTT", commUtils.getCurDate("yyyy/MM/dd HH:mm:ss"));
								recOutPara.setField("YD_SCH_CD",          szYD_SCH_CD);
								recOutPara.setField("YD_CARRY_IN_SH",     "" + intCarryInCntMtl);
								recOutPara.setField("YS_STK_COL_GP",      ydeqpid);
								recOutPara.setField("YS_STK_BED_NO",      ysstkbedno);
								
								commUtils.printParam(logId, recOutPara);
								
								//블름보급Carry-In작업요구 전송 Data 생성
								jrRtn = commUtils.addSndData(recOutPara);
							}	
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
	
	private void clearBedData(String ydeqpid,
							  String ysstkbedno,
							  String logId,
							  String methodNm) throws DAOException {
		try {
			JDTORecord inParam = JDTORecordFactory.getInstance().create();
			JDTORecord RecBedLyr = JDTORecordFactory.getInstance().create();
			JDTORecord jrParam = JDTORecordFactory.getInstance().create();
			/**********************************************************
			* 2. 기존 data clear
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("YS_STK_COL_GP", ydeqpid);
			jrParam.setField("YS_STK_BED_NO", ysstkbedno);
			
			/* com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrStkbed 
			SELECT A.YS_STK_COL_GP AS YS_STK_COL_GP
			     , A.YS_STK_BED_NO AS YS_STK_BED_NO
			     , A.YS_STK_LYR_NO AS YS_STK_LYR_NO
			     , A.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
			     , A.SSTL_NO       AS SSTL_NO
			  FROM TB_YS_STKLYR A
			 WHERE A.YS_STK_COL_GP = :V_YS_STK_COL_GP
			   AND A.YS_STK_BED_NO = :V_YS_STK_BED_NO
			   AND A.SSTL_NO IS NOT NULL
			   AND A.DEL_YN = 'N'
				 */  
			JDTORecordSet RsBedLyr = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStklyrStkbed", logId, methodNm, "장입bed검색 조회");
			if (RsBedLyr.size() > 0) {
				for (int i = 1; i <= RsBedLyr.size(); i++){
					RsBedLyr.absolute(i);
					RecBedLyr  = RsBedLyr.getRecord();
	
					inParam = JDTORecordFactory.getInstance().create();
					inParam.setResultCode(logId);		//Log ID
					inParam.setResultMsg(methodNm);	//Log Method Name
					
					//추가, 이동
					inParam.setField("FNL_REG_PGM"			, "rcvM3YSL101" );
					inParam.setField("YD_GP"				, "*" );
					inParam.setField("YD_BAY_GP"			, ydeqpid.substring(1,2) );
					inParam.setField("YD_EQP_GP"			, ydeqpid.substring(2,4) );
					inParam.setField("YS_STK_COL_NO"		, ydeqpid.substring(4,6) );
					inParam.setField("YS_STK_BED_NO"		, commUtils.trim(RecBedLyr.getFieldString("YS_STK_BED_NO"    )) );
					inParam.setField("YS_STK_LYR_NO"		, commUtils.trim(RecBedLyr.getFieldString("YS_STK_LYR_NO"    )) );
					inParam.setField("YS_STK_SEQ_NO"		, commUtils.trim(RecBedLyr.getFieldString("YS_STK_SEQ_NO"    )) );
					inParam.setField("YS_STR_LOC"			, ydeqpid 
							                                + commUtils.trim(RecBedLyr.getFieldString("YS_STK_BED_NO"    )) 
							                                + commUtils.trim(RecBedLyr.getFieldString("YS_STK_LYR_NO"    ))  
							                                + commUtils.trim(RecBedLyr.getFieldString("YS_STK_SEQ_NO"    ))) ;
					inParam.setField("SSTL_NO"				, commUtils.trim(RecBedLyr.getFieldString("SSTL_NO"    ))  );
					
					/**********************************************************
					* 1.2공통 저장위치 Update (별도 Transaction 으로 처리)
					**********************************************************/
					EJBConnector tranConn = new EJBConnector("default", "BlYsL2RcvSeEJB", this);
					tranConn.trx("updBlCommYsStrLoc", new Class[] { JDTORecord.class }, new Object[] { inParam });
				}			
			
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrBedClr 
				--적치단 재료번호 삭제 
				UPDATE TB_YS_STKLYR
				   SET MODIFIER            = :V_MODIFIER
				      ,MOD_DDTT            = SYSDATE
				      ,SSTL_NO              = NULL
				      ,YD_STK_LYR_ACT_STAT = 'E'
				      ,YD_STK_LYR_MTL_STAT = 'E'
				 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
				   AND YS_STK_BED_NO  = :V_YS_STK_BED_NO
				   AND DEL_YN              = 'N'		    			
				*/
				int intRtnVal = commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrBedClr", logId, methodNm, "TB_YS_STKLYR 갱신");
				
				if(intRtnVal <= 0) {
					throw new Exception("적치단변경시 오류 발생.");
				}
			}
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	
	private JDTORecord shiftBedData(String sYdEqpId,
									String szYD_SCH_CD,
			                        String sYD_CRN_SCH_ID,
				 				    String logId,
								    String methodNm) throws DAOException {
		try {
				JDTORecord jrParam = JDTORecordFactory.getInstance().create();
				
				String sYS_STK_COL_GP = "";
				
				if("BBTZ01".equals(sYdEqpId)){
					sYS_STK_COL_GP = "BBTZ11";
				}else{//BBRT01
					sYS_STK_COL_GP = "BBRT11";
				}
				/**********************************************************
				* 2. 기존 data clear
				**********************************************************/
				jrParam.setResultCode(logId);	//Log ID
				jrParam.setResultMsg(methodNm);	//Log Method Name
				jrParam.setField("YD_SCH_CD"	, szYD_SCH_CD);
				jrParam.setField("YS_STK_COL_GP", sYS_STK_COL_GP);
				jrParam.setField("YS_STK_BED_NO", "01");
				/* 
				 * 1. BBTZ11 로 스케쥴이 있는지 체크, 있으면 BBTZ01로 SHIFT 처리 
				 */
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrBedShift     
			    UPDATE + BYPASS_UJVC 
			    (
			        SELECT A.YS_STK_COL_GP,A.YS_STK_BED_NO,A.YS_STK_LYR_NO,A.YS_STK_SEQ_NO,
			               A.SSTL_NO AS ORG_STL_NO,A.YD_STK_LYR_MTL_STAT AS ORG_STL_STAT,B.SSTL_NO AS TRG_STL_NO,B.YD_STK_LYR_MTL_STAT AS TRG_STL_STAT
			          FROM TB_YS_STKLYR A,
			               (
			                SELECT YS_STK_SEQ_NO,SSTL_NO,YD_STK_LYR_MTL_STAT 
			                  FROM TB_YS_STKLYR
			                 WHERE YS_STK_COL_GP = SUBSTR(:V_YS_STK_COL_GP,1,4)||'11'
			                   AND YS_STK_BED_NO = '01'
			               ) B
			         WHERE A.YS_STK_COL_GP = SUBSTR(:V_YS_STK_COL_GP,1,4)||'01'
			           AND A.YS_STK_BED_NO = '01'
			           AND A.YS_STK_SEQ_NO = B.YS_STK_SEQ_NO
			    )A
			    SET A.ORG_STL_NO    = A.TRG_STL_NO,
			        A.ORG_STL_STAT  = A.TRG_STL_STAT
			    */    
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrBedShift", logId, methodNm, "TB_YS_STKLYR SHIFT");
				
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrBedClr 
				--적치단 재료번호 삭제 
				UPDATE TB_YS_STKLYR
				   SET MODIFIER            = :V_MODIFIER
				      ,MOD_DDTT            = SYSDATE
				      ,SSTL_NO              = NULL
				      ,YD_STK_LYR_ACT_STAT = 'E'
				      ,YD_STK_LYR_MTL_STAT = 'E'
				 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
				   AND YS_STK_BED_NO  = :V_YS_STK_BED_NO
				   AND DEL_YN              = 'N'		    			
				*/
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrBedClr", logId, methodNm, "TB_YS_STKLYR 갱신");
				
				/* 
				 * 2. 스케쥴정보 체크 후 수정 
				 */
				/* com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchChg 
			    UPDATE + BYPASS_UJVC 
			    (

			        SELECT YD_SCH_CD YS_DN_WO_LOC,YS_STR_LOC_HIS2 FROM USRYSA.TB_YS_CRNSCH
			         WHERE DEL_YN       = 'N'
			           AND YD_SCH_CD    = :V_YD_SCH_CD
			           AND YS_DN_WO_LOC = SUBSTR(YD_SCH_CD,1,4)||'1101'
			       
			    )A
			    SET A.YS_DN_WO_LOC     = SUBSTR(YD_SCH_CD,1,4)||'0101',
			        A.YS_STR_LOC_HIS2  = A.YS_DN_WO_LOC
				*/
				commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updCrnSchChg", logId, methodNm, "TB_YS_CRNSCH 갱신");
			
				/* 
				 * 3. 스케쥴정보 수정 후 재작업지시 
				 */
				jrParam = JDTORecordFactory.getInstance().create();
				jrParam.setField("YD_SCH_CD"  , szYD_SCH_CD); 
				JDTORecordSet jsChk1 = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchrule", logId, methodNm, "스케줄기준 조회");
				 
				JDTORecord jrYdMsg = JDTORecordFactory.getInstance().create();
				 
				if(jsChk1.size() > 0) {
					String szPreEqpId = commUtils.trim(jsChk1.getRecord(0).getFieldString("YD_WRK_CRN"));
					    
					jrYdMsg.setField("JMS_TC_CD"         , "YSYSJ001"               ); //JMSTC코드
					jrYdMsg.setField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); //JMSTC생성일시
					jrYdMsg.setField("YD_EQP_ID"         , szPreEqpId               ); //야드설비ID
					jrYdMsg.setField("YD_WRK_PROG_STAT"  , "W"                      ); //야드작업진행상태
					jrYdMsg.setField("YD_CRN_SCH_ID" 	 , sYD_CRN_SCH_ID           ); //야드스케쥴ID
				 }
				        
				return jrYdMsg;
				
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}		
	
	/**
	 *      [A] 오퍼레이션명 : 빌렛공통 UPDATE
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updBlCommYsStrLoc(JDTORecord jrParam) throws DAOException {
		String methodNm = "BLOOM공통 UPDATE[BlYsL2RcvSeEJB.updBlCommYsStrLoc] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBlCommYsStrLoc", logId, methodNm, "BLOOM공통 UPDATE");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 빌렛공통 UPDATE2
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public void updBlCommYsStrLocProgCd(JDTORecord jrParam) throws DAOException {
		String methodNm = "BLOOM공통 UPDATE[BlYsL2RcvSeEJB.updBlCommYsStrLocProgCd] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			
			
			commDao.update(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.updBlCommYsStrLocProgCd", logId, methodNm, "BLOOM공통 UPDATE");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
