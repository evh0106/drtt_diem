/**
 * @(#)CondPredRcvL2SeEJBSBean
 *
 * @version		V1.00
 * @author		현대제철
 * @date		2025/02/20
 *
 * @description	열연 결로 예측(Condensation Prediction) 시스템 L2 수신 session EJB
 * 
 * -------------------------------------------------------------------------------
 * Ver.		수정일자	요청자	수정자	내용
 * =======	==========	======	======	==========================================
 * V1.00	2025/02/20	정종균	양태호	최초 등록
 * 
 */
package com.inisteel.cim.yf.condpred.session;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yf.common.YfCommUtils;
import com.inisteel.cim.yf.common.dao.YfCommDAO;
import com.inisteel.cim.yf.condpred.CondPredQueryIF;
import com.inisteel.cim.yf.condpred.CondPredUtil;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;

/**
 * [A] 클래스명 : 열연 결로 예측(Condensation Prediction) 시스템 L2 수신 session EJB
 *
 * @ejb.bean name="CondPredRcvL2SeEJB" jndi-name="CondPredRcvL2SeEJB" type="Stateless" view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300"
 * @ejb.transaction type="Required"
 */

public class CondPredRcvL2SeEJBSBean extends BaseSessionBean implements CondPredQueryIF {

	private static final long serialVersionUID = 1L;
	private YfCommUtils commUtils = new YfCommUtils();
	private YfCommDAO commDao = new YfCommDAO();
	private CondPredUtil condpredUtil = new CondPredUtil();

	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/**
	 * [A] 오퍼레이션명 : 열연야드_THM_공장내외온습도정보 응답(YDX1L001) 전문 생성
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 *            rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 */
	public JDTORecord getYDX1L001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "열연야드_THM_공장내외온습도정보 응답 전문 생성[CondPredRcvL2SeEJB.getYDX1L001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			// 수신 항목 값
			String msgId = commUtils.trim(rcvMsg.getFieldString("MSG_ID"));
			String msgGp = commUtils.trim(rcvMsg.getFieldString("MSG_GP"));
			String ydGp = commUtils.trim(rcvMsg.getFieldString("YD_GP"));
			String meaDh = commUtils.trim(rcvMsg.getFieldString("MEA_DH"));
			String Ydl3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD"));

			/**********************************************************
			 * 1. 수신 항목 값 Check
			 **********************************************************/
			if ("".equals(msgId)) {
				return null;
			}
			if (meaDh.length() != 14 || !meaDh.matches("^[0-9]*$")) {
				return null;
			}

			/**********************************************************
			 * 2. 전문 생성
			 **********************************************************/
			String ydL3Msg = "";
			if ("0000".equals(Ydl3HdRsCd)) {
				ydL3Msg = "정상 처리";
			} else if ("9999".equals(Ydl3HdRsCd)) {
				ydL3Msg = "오류발생";
			} else {
				ydL3Msg = "";
			}

			StringBuffer sbMsg = new StringBuffer();

			sbMsg = sbMsg.append(msgId); // 전문ID
			sbMsg = sbMsg.append(commUtils.getDate10()); // 생성일자
			sbMsg = sbMsg.append(commUtils.getTime8()); // 생성시간
			sbMsg = sbMsg.append(commUtils.getRPad(msgGp, 1, " ")); // 전문구분
			sbMsg = sbMsg.append("0059"); // 전문길이
			sbMsg = sbMsg.append(commUtils.getRPad(" ", 29, " ")); // 임시
			sbMsg = sbMsg.append(commUtils.getRPad(ydGp, 1, " ")); // 야드구분
			sbMsg = sbMsg.append(commUtils.getRPad(meaDh, 14, " ")); // 측정일시
			sbMsg = sbMsg.append(commUtils.getRPad(Ydl3HdRsCd, 4, " "));// 야드L3처리결과코드
			sbMsg = sbMsg.append(commUtils.getRPad(ydL3Msg, 40, " ")); // 야드L3MESSAGE

			JDTORecord sndL2Msg = JDTORecordFactory.getInstance().create();

			sndL2Msg.setResultCode(logId); // Log ID
			sndL2Msg.setResultMsg(methodNm); // Log Method Name
			sndL2Msg.addField("JMS_TC_CD", msgId); // JMSTC코드
			sndL2Msg.addField("JMS_TC_CREATE_DDTT", commUtils.getDateTime14()); // JMSTC생성일시(yyyyMMddHHmmss)
			sndL2Msg.addField("JMS_TC_MESSAGE", sbMsg.toString()); // JMSTCMessage

			// 전송 Data Return
			return commUtils.addSndData(sndL2Msg);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}

	/**
	 * [A] 오퍼레이션명 : 열연야드_THM_공장내외온습도정보(X1YDL001, X2YDL001, X3YDL001)
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param JDTORecord
	 *            rcvMsg
	 * @return JDTORecord
	 * @throws DAOException
	 * @ejb.transaction type="RequiresNew"
	 */
	public JDTORecord rcvX1YDL001(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "열연야드_THM_공장내외온습도정보[CondPredRcvL2SeEJB.rcvX1YDL001] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); // 전문 Return
		JDTORecord jrRtnMsg = JDTORecordFactory.getInstance().create();

		try {
			commUtils.printLog(logId, methodNm, "S+");
			commUtils.printParam(logId + " 열연야드_THM_공장내외온습도정보 수신 ", rcvMsg);

			// 수신 항목 header 값
			String msgId = commUtils.nvl(commUtils.getMsgId(rcvMsg), ""); // EAI, JMS, HTTP(출하관리 등) 수신 전문 I/F ID
			String msgGp = commUtils.trim(rcvMsg.getFieldString("MSG_GP"));
			
			// 수신 항목 body 값
			String sYD_GP = commUtils.trim(rcvMsg.getFieldString("YD_GP")); // 야드구분
			String sMEA_DH = commUtils.trim(rcvMsg.getFieldString("MEA_DH")); // 측정일시 
			String sTEM_IN_LOC1 = commUtils.trim(rcvMsg.getFieldString("TEM_IN_LOC1")); // 온도_실내(M1)
			String sTEM_IN_LOC2 = commUtils.trim(rcvMsg.getFieldString("TEM_IN_LOC2")); // 온도_실내(M2)
			String sTEM_IN_LOC3 = commUtils.trim(rcvMsg.getFieldString("TEM_IN_LOC3")); // 온도_실내(M3)
			String sTEM_IN_LOC4 = commUtils.trim(rcvMsg.getFieldString("TEM_IN_LOC4")); // 온도_실내(M4)
			String sTEM_IN_LOC5 = commUtils.trim(rcvMsg.getFieldString("TEM_IN_LOC5")); // 온도_실내(M5)
			String sTEM_OUT_LOC1 = commUtils.trim(rcvMsg.getFieldString("TEM_OUT_LOC1")); // 온도_실외(M7)
			String sHUM_IN_LOC1 = commUtils.trim(rcvMsg.getFieldString("HUM_IN_LOC1")); // 습도_실내(M1)
			String sHUM_IN_LOC2 = commUtils.trim(rcvMsg.getFieldString("HUM_IN_LOC2")); // 습도_실내(M2)
			String sHUM_IN_LOC3 = commUtils.trim(rcvMsg.getFieldString("HUM_IN_LOC3")); // 습도_실내(M3)
			String sHUM_IN_LOC4 = commUtils.trim(rcvMsg.getFieldString("HUM_IN_LOC4")); // 습도_실내(M4)
			String sHUM_IN_LOC5 = commUtils.trim(rcvMsg.getFieldString("HUM_IN_LOC5")); // 습도_실내(M5)
			String sHUM_OUT_LOC1 = commUtils.trim(rcvMsg.getFieldString("HUM_OUT_LOC1")); // 습도_실외(M7)
			String sTEM_COIL_LOC1 = commUtils.trim(rcvMsg.getFieldString("TEM_COIL_LOC1")); // 온도_코일(M1)
			String sTEM_COIL_LOC2 = commUtils.trim(rcvMsg.getFieldString("TEM_COIL_LOC2")); // 온도_코일(M2)
			String sTEM_COIL_LOC3 = commUtils.trim(rcvMsg.getFieldString("TEM_COIL_LOC3")); // 온도_코일(M3)
			String sTEM_COIL_LOC4 = commUtils.trim(rcvMsg.getFieldString("TEM_COIL_LOC4")); // 온도_코일(M4)
			String sTEM_COIL_LOC5 = commUtils.trim(rcvMsg.getFieldString("TEM_COIL_LOC5")); // 온도_코일(M5)
			String sTEM_IN_LOC6 = commUtils.trim(rcvMsg.getFieldString("TEM_IN_LOC6")); // 온도_실내(M6)
			String sHUM_IN_LOC6 = commUtils.trim(rcvMsg.getFieldString("HUM_IN_LOC6")); // 습도_실내(M6)
			String sTEM_COIL_LOC6 = commUtils.trim(rcvMsg.getFieldString("TEM_COIL_LOC6")); // 온도_코일(M6)
			
			// 수신 항목 기타 값
			String modifier = commUtils.trim(rcvMsg.getFieldString("MODIFIER")); // 수정자(Backup Only)
			if ("".equals(modifier)) {
				modifier = msgId;
			}

			// 수신 항목 값 확인
			if (msgId.equals("")) {
				throw new Exception("MSG_ID 값이 정확하지 않습니다!! [" + msgId + "]");
			}
			if (msgId.startsWith("X1") && !sYD_GP.equals("1")) {
				throw new Exception("야드 구분값이 정확하지 않습니다!! [" + msgId + "-" + sYD_GP + "]");
			}
			if (msgId.startsWith("X2") && !sYD_GP.equals("3")) {
				throw new Exception("야드 구분값이 정확하지 않습니다!! [" + msgId + "-" + sYD_GP + "]");
			}
			if (msgId.startsWith("X3") && !sYD_GP.equals("J")) {
				throw new Exception("야드 구분값이 정확하지 않습니다!! [" + msgId + "-" + sYD_GP + "]");
			}
			if (sMEA_DH.length() != 14 || !sMEA_DH.matches("^[0-9]*$")) {
				throw new Exception("측정일시 값이 정확하지 않습니다!! [" + sMEA_DH + "]");
			}

			// DB Query 파라메터 설정
			JDTORecord jrParam = JDTORecordFactory.getInstance().create(); // Query 실행시 파라메터 전달용 JDTORecord
			jrParam.setResultCode(logId); // Log ID
			jrParam.setResultMsg(methodNm); // Log Method Name

			jrParam.setField("YD_GP", sYD_GP);
			jrParam.setField("MEA_LOC", "DCS");
			jrParam.setField("MEA_DH", sMEA_DH);
			jrParam.setField("TEM_IN_LOC1", sTEM_IN_LOC1);
			jrParam.setField("TEM_IN_LOC2", sTEM_IN_LOC2);
			jrParam.setField("TEM_IN_LOC3", sTEM_IN_LOC3);
			jrParam.setField("TEM_IN_LOC4", sTEM_IN_LOC4);
			jrParam.setField("TEM_IN_LOC5", sTEM_IN_LOC5);
			jrParam.setField("TEM_OUT_LOC1", sTEM_OUT_LOC1);
			jrParam.setField("HUM_IN_LOC1", sHUM_IN_LOC1);
			jrParam.setField("HUM_IN_LOC2", sHUM_IN_LOC2);
			jrParam.setField("HUM_IN_LOC3", sHUM_IN_LOC3);
			jrParam.setField("HUM_IN_LOC4", sHUM_IN_LOC4);
			jrParam.setField("HUM_IN_LOC5", sHUM_IN_LOC5);
			jrParam.setField("HUM_OUT_LOC1", sHUM_OUT_LOC1);
			jrParam.setField("TEM_COIL_LOC1", sTEM_COIL_LOC1);
			jrParam.setField("TEM_COIL_LOC2", sTEM_COIL_LOC2);
			jrParam.setField("TEM_COIL_LOC3", sTEM_COIL_LOC3);
			jrParam.setField("TEM_COIL_LOC4", sTEM_COIL_LOC4);
			jrParam.setField("TEM_COIL_LOC5", sTEM_COIL_LOC5);
			jrParam.setField("TEM_IN_LOC6", sTEM_IN_LOC6);
			jrParam.setField("HUM_IN_LOC6", sHUM_IN_LOC6);
			jrParam.setField("TEM_COIL_LOC6", sTEM_COIL_LOC6);
			jrParam.setField("REGISTER", modifier);
			jrParam.setField("MODIFIER", modifier);

			commDao.insert(jrParam, insTHMeas, logId, methodNm, "열연야드_THM_공장내외온습도정보 등록");

//			//PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", methodNm, "APPPI0", "1", "*");
//			commUtils.printLog(logId, methodNm, "sApplyYnPI:" + sApplyYnPI);
//			
//			if("N".equals(sApplyYnPI)) {
				if (msgGp.equals("U")) {
					commUtils.printLog(logId, methodNm, "MSG_GP:" + msgGp + " 공장내외온습도정보AB 응답 전송");
					
					jrRtnMsg.setField("MSG_ID", condpredUtil.getRtnMsgId(rcvMsg));
					jrRtnMsg.setField("MSG_GP", "I");
					jrRtnMsg.setField("YD_GP", sYD_GP);
					jrRtnMsg.setField("MEA_DH", sMEA_DH);
					jrRtnMsg.setField("YD_L3_HD_RS_CD", "0000");

					jrRtn = commUtils.addSndData(jrRtn, getYDX1L001(jrRtnMsg));
				}
//			}
			
			// 결로 발생 알람 발생 처리
			Object[] inParam = { sYD_GP, sMEA_DH };
			int[] inParamIndex = { 1, 2 };
			JDTORecord record = commDao.callProcedure(inParam, inParamIndex, callSpYfCfAlmProc);
			
			if(record == null || record.size() <= 0){
				commUtils.printParam(logId + " 열연야드_THM_공장내외온습도정보 수신 후 알람 발생 처리 호출 실패", record);
			} else {
				commUtils.printParam(logId + " 열연야드_THM_공장내외온습도정보 수신 후 알람 발생 처리 호출", record);
			}
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
