/**
 * @(#)GdsYsComm
 *
 * @version          V1.00
 * @author           조병기
 * @date             2014/12/22
 *
 * @description      제품(봉강,선재) 야드 공통 처리 EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2014/12/22   윤재광      조병기      최초 등록
 */
package com.inisteel.cim.ys.gds.session;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ys.common.dao.YsCommDAO;
import com.inisteel.cim.ys.common.util.YsCommUtils;


/**
 *      [A] 클래스명 : 제품(봉강,선재) 야드 공통 처리
 *
*/

public class GdsYsComm {

	private YsCommUtils commUtils = new YsCommUtils();
	private YsCommDAO commDao = new YsCommDAO();	
	
	/***************************************************************************
	 * 공통 Check
	 **************************************************************************/
	/**
	 *      [A] 오퍼레이션명 : 스케줄코드 및 크레인 Check
	 *
	 *      @param JDTORecord jrParam
	 *      @return JDTORecord jrRtn
	 *      @throws DAOException
	*/
	public JDTORecord chkSchCdEqp(JDTORecord jrParam) {
		String methodNm = "스케줄코드 및 크레인 Check[GdsYsComm.chkSchCdEqp] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//수신 항목 값
			String ydSchCd = commUtils.trim(jrParam.getFieldString("YD_SCH_CD")); //야드스케쥴코드

			if ("".equals(ydSchCd)) {
				throw new Exception("스케쥴코드 없음");
			} else if (ydSchCd.length() < 8) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 이상");
			}
			
			//야드스케쥴금지유무 조회
			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatSchCd", logId, methodNm, "야드스케쥴금지유무 조회"); 
			

//			String ydSchProhExn = "";  //야드스케쥴금지유무
			String ydEqpId      = "";  //야드설비ID(작업크레인)

			if (jsChk.size() > 0) {
//				ydSchProhExn = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_PROH_EXN"));
				ydEqpId      = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"      ));
			}

//			if ("".equals(ydSchProhExn)) {
//				throw new Exception("스케쥴코드[" + ydSchCd + "] 정보 없음");
//			} else if ("Y".equals(ydSchProhExn)) {
//				throw new Exception("스케쥴코드[" + ydSchCd + "] 기동금지");
//			} else if ("".equals(ydEqpId)) {
//				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
//			}
			if ("".equals(ydEqpId)) {
				throw new Exception("스케쥴코드[" + ydSchCd + "] 작업가능 크레인 없음");
			}
			commUtils.printLog(logId, methodNm, "S-");

			return jsChk.getRecord(0);
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}


	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 전문 조회
	 *
	 *      @param String JDTORecord rcvMsg
	 *      @return String JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getCrnSchMsg(JDTORecord jrParam) {
 		/***************************************************************************
		 * 스케줄 기동시 사용: procCrnWrkBookMgtStart
		 **************************************************************************/
		
		String methodNm = "크레인스케줄전문조회[GdsYsComm.getCrnSchMsg] < " + jrParam.getResultMsg();
		String logId = jrParam.getResultCode();

		try {
			commUtils.printLog(logId, methodNm, "S+");

			//Return Value
			JDTORecord jrRtn  = null;
			String currDate   = commUtils.getDateTime14();									//현재시각
			String ydGp       = commUtils.trim(jrParam.getFieldString("YD_GP"        ));	//야드구분
			String ydBayGp    = commUtils.trim(jrParam.getFieldString("YD_BAY_GP"    ));	//동구분
			String ydWbookId  = commUtils.trim(jrParam.getFieldString("YD_WBOOK_ID"  ));	//야드작업예약ID
			String ydSchCd    = commUtils.trim(jrParam.getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
			String ydEqpId    = commUtils.trim(jrParam.getFieldString("YD_EQP_ID"    ));	//야드설비ID
			String ydSchStGp  = commUtils.trim(jrParam.getFieldString("YD_SCH_ST_GP" ));	//야드스케쥴기동구분
			String ydSchReqGp = commUtils.trim(jrParam.getFieldString("YD_SCH_REQ_GP"));	//야드스케쥴요청구분
			String modifier   = commUtils.trim(jrParam.getFieldString("MODIFIER"     ));	//수정자
			String ejbCallYn  = commUtils.trim(jrParam.getFieldString("EJB_CALL_YN"  ));	//EJBCall여부(신 크레인스케줄)

			if ("".equals(ydWbookId) && "".equals(ydSchCd) && "".equals(ydEqpId)) {
				if ("Y".equals(ejbCallYn)) {
					throw new Exception("크레인스케줄 기동을 위한 정보가 없습니다.");
				} else {
					commUtils.printLog(logId, "크레인스케줄 기동을 위한 정보가 없습니다.", "SL");
					return null;
				}
			}

			//크레인스케줄기동구분 조회
			if (!"".equals(ydWbookId) && ("".equals(ydSchCd) || "".equals(ydEqpId))) {
				jrParam.setField("YD_WBOOK_ID", ydWbookId); //야드작업예약ID
				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchStartGp", logId, methodNm, "크레인스케줄기동구분 조회");

				if (jsChk.size() > 0) {
					ydGp       = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_GP"        ));	//야드구분
					ydSchCd    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_SCH_CD"    ));	//야드스케쥴코드
					ydEqpId    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_ID"    ));	//야드설비ID
					ydBayGp    = ydSchCd.substring(1, 2);
				} else {
					if ("Y".equals(ejbCallYn)) {
						throw new Exception("작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.");
					} else {
						commUtils.printLog(logId, "작업예약ID[" + ydWbookId + "]의 정보가 없어 크레인스케줄을 기동할 수 없습니다.", "SL");
						return null;
					}
				}
			} else {
				if ("".equals(ydGp)) {
					if (!"".equals(ydSchCd)) {
						ydGp       = ydSchCd.substring(0, 1);
						ydBayGp    = ydSchCd.substring(1, 2);
					} else if (!"".equals(ydEqpId)) {
						ydGp = ydEqpId.substring(0, 1);
						ydBayGp    = ydEqpId.substring(1, 2);
					}
				}

				jrParam.setField("YD_GP", ydGp); //야드구분
//				JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getCrnSchStartGp", logId, methodNm, "크레인스케줄기동구분 조회");
				
			}


			commUtils.printLog(logId, "[작업예약ID:" + ydWbookId + ", 스케쥴코드:" + ydSchCd + ", 설비ID:" + ydEqpId + "]" + "ydBayGp" + ydBayGp, "SL");

			if ("".equals(ydBayGp)) {
				ydBayGp    = ydSchCd.substring(1, 2);
			}	
			//크레인스케줄 전문 - Log ID, Method, 수정자 Set
			JDTORecord jrYdMsg = commUtils.getParam(logId, jrParam.getResultMsg(), modifier);
			jrYdMsg.setResultCode(logId);	//Log ID
			jrYdMsg.setResultMsg(methodNm);	//Log Method Name
			// 크레인스케줄 기동 
			if ("D".equals(ydBayGp)||"E".equals(ydBayGp)) {
				jrYdMsg.setField("JMS_TC_CD", "YSYSJ402"); //
			} else {
				jrYdMsg.setField("JMS_TC_CD", "YSYSJ302"); //
			}	
			
			//화면에서 작업예약을 선택해서 기동 시
			if (!"".equals(ydWbookId) && "M".equals(ydSchStGp)) {
				ydSchCd = "";	//야드스케쥴코드
				ydEqpId = "";	//야드설비ID
			}

			jrYdMsg.setField("JMS_TC_CREATE_DDTT", currDate  ); //JMSTC생성일시
			jrYdMsg.setField("YD_WBOOK_ID"       , ydWbookId ); //야드작업예약ID
			jrYdMsg.setField("YD_SCH_CD"         , ydSchCd   ); //야드스케쥴코드
			jrYdMsg.setField("YD_EQP_ID"         , ydEqpId   ); //야드설비ID
			jrYdMsg.setField("YD_SCH_ST_GP"      , ydSchStGp ); //야드스케쥴기동구분
			jrYdMsg.setField("YD_SCH_REQ_GP"     , ydSchReqGp); //야드스케쥴요청구분

			//신 크레인스케줄이고 EJBCall여부가 'Y'이면
				 
			//신 크레인스케줄 EJB Call
//			EJBConnector sndConn = new EJBConnector("default", "GdsYsSchSeEJB", this);
//			JDTORecord jrRst = (JDTORecord)sndConn.trx("rcvYSYSJ302", new Class[] { JDTORecord.class }, new Object[] { jrYdMsg });
			//작업지시 등 전송할 전문이 있으면 받아서 전송
			jrRtn = commUtils.addSndData(jrRtn, jrYdMsg);
				
			
			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 선재수동창고 크레인작업실적응답(YSN3L004) 전문 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYSN3L004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재수동창고 크레인작업실적응답 생성[GdsYsComm.getYSN3L004] < " + rcvMsg.getResultMsg();
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

			if (ydEqpId.startsWith("K")) {
				msgId = "YSN3L004";
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

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();
			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 봉강수동창고 크레인작업실적응답(YSN3L004) 전문 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYSN4L004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강수동창고 크레인작업실적응답 생성[GdsYsComm.getYSN4L004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE
			String modifier	  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //MODIFIER
			
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("K")) {
				msgId = "YSN4L004";
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
			
			if (ydEqpId.startsWith("KB") && (!"N4YSL005".equals(modifier)&& !"N4YSL006".equals(modifier))) {
				ydL3Msg = ydL3Msg + " (L3백업)";
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

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();
			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name
			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 선재자동창고 크레인작업실적응답(YSN5L004) 전문 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYSN5L004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "선재자동창고 크레인작업실적응답 생성[GdsYsComm.getYSN5L004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE
			String modifier	  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //MODIFIER

			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("K")) {
				msgId = "YSN5L004";
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
			
			if (ydEqpId.startsWith("KD") && (!"N5YSL005".equals(modifier)&& !"N5YSL006".equals(modifier))) {
				ydL3Msg = ydL3Msg + " (L3백업)";
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

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();
			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name

			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}

	/**
	 *      [A] 오퍼레이션명 : 봉강자동창고 크레인작업실적응답(YSN6L004) 전문 생성
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord getYSN6L004(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "봉강자동창고 크레인작업실적응답 생성[GdsYsComm.getYSN6L004] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();

		try {
			//수신 항목 값
			String msgId      = ""; //전문ID
			String ydEqpId    = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID"     )); //야드설비ID
			String ydL2WrGp   = commUtils.trim(rcvMsg.getFieldString("YD_L2_WR_GP"   )); //야드L2실적구분
			String ydL3HdRsCd = commUtils.trim(rcvMsg.getFieldString("YD_L3_HD_RS_CD")); //야드L3처리결과코드
			String ydL3Msg    = commUtils.trim(rcvMsg.getFieldString("YD_L3_MSG"     )); //야드L3MESSAGE
			String modifier	  = commUtils.trim(rcvMsg.getFieldString("MODIFIER"      )); //MODIFIER
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				return null;
			}

			if (ydEqpId.startsWith("K")) {
				msgId = "YSN6L004";
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
			
			if (ydEqpId.startsWith("KA") && (!"N6YSL005".equals(modifier)&& !"N6YSL006".equals(modifier))) {
				ydL3Msg = ydL3Msg + " (L3백업)";
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

			JDTORecord sndMsg = JDTORecordFactory.getInstance().create();
			sndMsg.setResultCode(logId);	//Log ID
			sndMsg.setResultMsg(methodNm);	//Log Method Name

			sndMsg.addField("JMS_TC_CD"          , msgId                    ); //JMSTC코드
			sndMsg.addField("JMS_TC_CREATE_DDTT" , commUtils.getDateTime14()); //JMSTC생성일시(yyyyMMddHHmmss)
			sndMsg.addField("JMS_TC_MESSAGE"     , sbMsg.toString()         ); //JMSTCMessage

			//전송 Data Return
			return commUtils.addSndData(sndMsg);
		} catch (Exception e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return null;
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 설비상태 Check
	 *
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord chkEqpStat(JDTORecord rcvMsg) throws DAOException {
		String methodNm = "설비상태Check[GdsYsComm.chkEqpStat] < " + rcvMsg.getResultMsg();
		String logId = rcvMsg.getResultCode();
		JDTORecord jrRtn = JDTORecordFactory.getInstance().create(); //결과

		try {
			commUtils.printLog(logId, methodNm, "S+");

			jrRtn.setField("YD_L3_HD_RS_CD", "EQ99"); //야드L3처리결과코드(Error)
			jrRtn.setField("YD_L3_MSG"     , "오류:설비상태Check 예상치 못한 오류"); //야드L3MESSAGE(40Byte)

			//수신 항목 값
			String ydEqpId = commUtils.trim(rcvMsg.getFieldString("YD_EQP_ID")); //야드설비ID
			String ydL3HdRsCd = ""; //야드L3처리결과코드
			String ydL3Msg    = ""; //야드L3MESSAGE
			
			/**********************************************************
			* 1. 수신 항목 값 Check
			**********************************************************/
			if ("".equals(ydEqpId)) {
				ydL3HdRsCd = "EQ01";
				ydL3Msg = "오류:설비ID 없음";
			} else if (ydEqpId.length() < 6) {
				ydL3HdRsCd = "EQ02";
				ydL3Msg = "오류:설비ID[" + ydEqpId + "] 이상";
			}

			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			/**********************************************************
			* 2. 설비상태 Check
			**********************************************************/
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = commUtils.getParam(logId, methodNm, "");

			jrParam.setField("YD_EQP_ID", ydEqpId); //야드설비ID

			JDTORecordSet jsChk = commDao.select(jrParam, "com.inisteel.cim.ys.common.dao.YsCommDAO.getStatEqp", logId, methodNm, "설비상태 Check"); 

			String ydEqpStat     = ""; //야드설비상태
			String ydEqpWrkMode  = ""; //야드설비작업Mode

			if (jsChk.size() > 0) {
				ydEqpStat    = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_STAT"    ));
				ydEqpWrkMode = commUtils.trim(jsChk.getRecord(0).getFieldString("YD_EQP_WRK_MODE"));
			}

			if ("".equals(ydEqpStat)) {
				//설비 Table 정보 Check
				ydL3HdRsCd = "EQ03";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] 정보 없음";
			} else if ("B".equals(ydEqpStat)) {
				//설비 Table 설비상태 Check
				ydL3HdRsCd = "EQ04";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] 고장";
			} else if (!"1".equals(ydEqpWrkMode)) {
				//설비 Table 설비작업Mode Check
				ydL3HdRsCd = "EQ05";
				ydL3Msg = "오류:크레인[" + ydEqpId + "] Off-Line";
			}
			
			if (!"".equals(ydL3Msg)) {
				jrRtn.setField("YD_L3_HD_RS_CD", ydL3HdRsCd); //야드L3처리결과코드
				jrRtn.setField("YD_L3_MSG"     , ydL3Msg   ); //야드L3MESSAGE
				throw new Exception(ydL3Msg);
			}
			
			jrRtn.setField("YD_L3_HD_RS_CD", "0000"); //야드L3처리결과코드
			jrRtn.setField("YD_L3_MSG"     , ""    ); //야드L3MESSAGE

			commUtils.printLog(logId, methodNm, "S-");

			return jrRtn;
		} catch (DAOException e) {
			commUtils.printErrorLog(commUtils.makeErrorLog(logId, methodNm, e), this, e);
			return jrRtn;
		} catch (Exception e) {
			return jrRtn;
		}
	}

	
}
