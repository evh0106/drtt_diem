/**
 * @(#)CCommSeEJBBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2019/05/02
 * 
 * @description      2열연 야드 공통관리 Session EJB
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 * 
 */
package com.inisteel.cim.yd.ccommon.session;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.yd.ccommon.dao.CCommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.ydPI.common.M10YdExLm13SenderFaEJBBean;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.common.dao.DBAssistantDAO;

/**
 *      [A] 클래스명 : 2열연 야드관리 공통 처리
 *
 * @ejb.bean name="CCommSeEJB" jndi-name="CCommSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class CCommSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private CCommUtils commUtils = new CCommUtils();
	private CCommDAO   commDao   = new CCommDAO();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	
	private JmsQueueSender jmsQSnder = new JmsQueueSender();
	private M10YdExLm13SenderFaEJBBean      M10YdExLm13Sender   = new M10YdExLm13SenderFaEJBBean();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}

	/***************************************************************************
	 * Interface 처리 공통
	 **************************************************************************/

	/**
	 *      [A] 오퍼레이션명 : EAI, JMS Interface 공통 수신 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	*/
	public void rcvInterface(JDTORecord rcvMsg) throws DAOException {
		String logId = commUtils.getLogId();
		String methodNm = "수신[CCommSeEJB.rcvInterface]-";
		String msgId = ""; //IF ID
		String msgNm = ""; //IF 명

		try {
			String sIF_YD = commUtils.nvl(rcvMsg.getFieldString("IF_YD"),"");
			
			if ("T".equals(sIF_YD)) {
				logId = commUtils.getLogIdT();
				//JMS송신시 추가되는 항목값이 있으면 logId를 변경
				String uniqueId = commUtils.trim(rcvMsg.getFieldString("UNIQUE_ID"));
				if (!"".equals(uniqueId)) {
					logId = "[T]<" + uniqueId + ">";
				}
			} else if ("S".equals(sIF_YD)) {
				logId = commUtils.getLogIdS();
				//JMS송신시 추가되는 항목값이 있으면 logId를 변경
				String uniqueId = commUtils.trim(rcvMsg.getFieldString("UNIQUE_ID"));
				if (!"".equals(uniqueId)) {
					logId = "[S]<" + uniqueId + ">";
				}
			} else {

//				logId = commUtils.getLogId();
				//JMS송신시 추가되는 항목값이 있으면 logId를 변경
				String uniqueId = commUtils.trim(rcvMsg.getFieldString("UNIQUE_ID"));
				if (!"".equals(uniqueId)) {
					logId = "[J]<" + uniqueId + ">";
				}
			}
			
			commUtils.printLog(logId, "I/F" + methodNm, "I+");

			String classNm = ""; //처리 Class명
			String mthdNm  = ""; //처리 Method명
			String errMsg  = ""; //오류내용

			//EAI, JMS, HTTP(출하관리 등) 수신 전문 IF ID
			msgId = commUtils.getMsgId(rcvMsg);
			
			if ("".equals(msgId)) {
		    	errMsg = "수신된 전문의 IF ID가 존재하지 않습니다.";
			} else {
				JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
				
				if (jrRst != null && jrRst.size() > 0) { 
					msgNm   = commUtils.trim(jrRst.getRecord(0).getFieldString("IF_NM"       )); //IF 명
			    	classNm = commUtils.trim(jrRst.getRecord(0).getFieldString("CLASS_NAME"  )); //Class명
			    	mthdNm  = commUtils.trim(jrRst.getRecord(0).getFieldString("METHODE_NAME")); //Method명

			    	if ("".equals(classNm) || "".equals(mthdNm)) { 
				    	errMsg = "[ " + msgId + " ]의 처리 프로그램이 I/F(TB_YD_Z_IF) Table에 정의되지 않았습니다.\n";
				    }
			    } else {
			    	errMsg = "[ " + msgId + " ]의 정보가  I/F(TB_YD_Z_IF) Table에 존재하지 않습니다.\n";
			    }
			}

			methodNm = msgNm + "(" + msgId + ")" + methodNm;

			if (!"".equals(errMsg)) {
				commUtils.printParam(logId + " " + methodNm, rcvMsg);
				throw new Exception(errMsg);
			}

			rcvMsg.setResultCode(logId);
			rcvMsg.setResultMsg(methodNm);
			
			//수신 전문처리 Log
			commUtils.printLog(logId, msgNm + "(" + msgId + ") >> [ " + classNm + "." + mthdNm + " ]", "IR");

			EJBConnector rcvConn = new EJBConnector("default", classNm, this);
			
			JDTORecord jrRst = (JDTORecord)rcvConn.trx(mthdNm, new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			//전송할 Data가 있으면 전송 처리
			if (jrRst != null) {
				jrRst.setResultCode(logId);
				jrRst.setResultMsg(methodNm);
				
				sndInterface(jrRst);
			}

			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, msgNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : EAI, JMS Interface 공통 수신 처리(기존)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return void
	 *      @throws DAOException
	*/
	public void rcvInterfaceOld(JDTORecord rcvMsg) throws DAOException {
		String logId = commUtils.getLogId();
		String methodNm = "기존방식수신[CCommSeEJB.rcvInterfaceOld]";
		String msgId = ""; //IF ID
		String msgNm = ""; //IF 명

		try {
			//JMS송신시 추가되는 항목값이 있으면 logId를 변경
			String uniqueId = commUtils.trim(rcvMsg.getFieldString("UNIQUE_ID"));
			if (!"".equals(uniqueId)) {
				logId = uniqueId;
			}
			
			commUtils.printLog(logId, "I/F" + methodNm, "I+");

			String classNm = ""; //처리 Class명
			String mthdNm  = ""; //처리 Method명
			String errMsg  = ""; //오류내용

			//EAI, JMS, HTTP(출하관리 등) 수신 전문 IF ID
			msgId = commUtils.getMsgId(rcvMsg);
			
			if ("".equals(msgId)) {
		    	errMsg = "수신된 전문의 IF ID가 존재하지 않습니다.";
			} else {
				JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
				
				if (jrRst != null && jrRst.size() > 0) { 
					msgNm   = commUtils.trim(jrRst.getRecord(0).getFieldString("IF_NM"      )); //IF 명
			    	classNm = commUtils.trim(jrRst.getRecord(0).getFieldString("BEF_PGM_NM1")); //기존Class명
			    	mthdNm  = commUtils.trim(jrRst.getRecord(0).getFieldString("BEF_PGM_NM2")); //기존Method명

			    	if ("".equals(classNm) || "".equals(mthdNm)) { 
				    	errMsg = "[ " + msgId + " ]의 처리 프로그램이 I/F(TB_YD_Z_IF) Table에 정의되지 않았습니다.\n";
				    }
			    } else {
			    	errMsg = "[ " + msgId + " ]의 정보가  I/F(TB_YD_Z_IF) Table에 존재하지 않습니다.\n";
			    }
			}

			methodNm = msgNm + "(" + msgId + ")" + methodNm;

			if (!"".equals(errMsg)) {
				commUtils.printParam(logId + " " + methodNm, rcvMsg);
				throw new Exception(errMsg);
			}

			rcvMsg.setResultCode(logId);
			rcvMsg.setResultMsg(methodNm);
			
			//수신 전문처리 Log
			commUtils.printLog(logId, msgNm + "(" + msgId + ") >> [ " + classNm + "." + mthdNm + " ]", "IR");

			EJBConnector rcvConn = new EJBConnector("default", classNm, this);
			
			rcvConn.trx(mthdNm, new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			
			commUtils.printLog(logId, methodNm, "I-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, msgNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 인터페이스 송신 처리 (EAI, JMS 공통)
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	*/
	public void sndInterface(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "I/F송신[CCommSeEJB.sndInterface] < " + sndData.getResultMsg();

		try {
			commUtils.printLog(logId, methodNm, "I+");

			String msgId   = ""; //IF ID
			String queueNm = ""; //Queue명

			int msgNo  = 0;  //IF ID 번호
			int sndCnt = 0;  //전송Data 건수
			boolean chkOK = false; //정상여부 Check

//PIDEV			
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "", "APPPI0", "J", "*");
//			if("PIDEV".equals("PIDEV")) {
				
				sndInterfacePI(sndData);
				return ;
//			}			
//			
//			JDTORecordSet sndMsgSet = (JDTORecordSet)sndData.getField("SEND_DATA");
//			if (sndMsgSet == null || sndMsgSet.size() <= 0) {
//				//commUtils.printLog("", commUtils.makeErrorLog(logId, methodNm, "전송할 Data가 존재하지 않습니다 ."), "IS");
//				commUtils.printLog("", "\n" + logId + " ■Info■ " + "Method  : " + methodNm + "\n" + logId + " ■Info■ " + "Message : " + "전송할 Data가 존재하지 않습니다 .", "IS");
//				return;
//			}
//
//			//인터페이스 정보
//			sndCnt = sndMsgSet.size(); //전송Data 건수
//			int[][] msgNos = new int[sndCnt][sndCnt+1]; //인터페이스별 건수 및 IF ID 번호
//			String[][] msgInfo = new String[sndCnt][2]; //IF ID, Queue명
//			
//			//같은 IF ID 끼리 정리
//			for (int ii = 0; ii < sndCnt; ii++) {
//				//EAI, JMS, HTTP(출하관리 등) 송신 전문 IF ID
//				msgId = commUtils.getMsgId(sndMsgSet.getRecord(ii));
//				
//				if (!"".equals(msgId) && ((msgId.length() == 8)||(msgId.length() == 7))) {
//					//기 등록된 List에서 찾기
//					chkOK = true;
//					for (int kk = 0; kk < sndCnt; kk++) {
//						if (msgId.equals(msgInfo[kk][0])) {
//							msgNos[kk][0] = msgNos[kk][0] + 1;
//							msgNos[kk][msgNos[kk][0]] = ii;
//							chkOK = false;
//							break;
//						}
//					}
//
//					//못 찾으면 신규로 등록
//					if (chkOK) {
//						//Queue명 조회
//						queueNm = getQueueNm(logId, methodNm, msgId);
//						
//						if (!"".equals(queueNm)) {
//							msgNos[msgNo][0] = 1;
//							msgNos[msgNo][1] = ii;
//							msgInfo[msgNo][0] = msgId;
//							msgInfo[msgNo][1] = queueNm;
//							msgNo++;
//						} else {
//							commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsgSet.getRecord(ii));
//						}
//
//					}
//				}
//			}
//				
//			//송신 전문 편성
//			for (int ii = 0; ii < msgNo; ii++) {
//				msgId   = msgInfo[ii][0];
//				queueNm = msgInfo[ii][1];
//				//msgId가 없으면 Skip
//				if ("".equals(msgId)) {	continue; }
//
//				int sCnt = 0;
//
//				for (int kk = ii; kk < msgNo; kk++) {
//					if (queueNm.equals(msgInfo[kk][1])) {
//						sCnt = sCnt + msgNos[kk][0];
//					}
//				}
//				
//				//전송건수 별 전송처리
//				if (sCnt == 1) {
//					//전송건수가 1개일 경우
//					JDTORecord sndMsg = sndMsgSet.getRecord(msgNos[ii][1]);
//
//					//msgId 삭제
//					msgInfo[ii][0] = "";
//
//					if (sndMsg != null) {
//						sndQueue(logId, methodNm, queueNm, sndMsg);
//					}
//				} else {
//					//여러개일 경우
//					int sNo  = 0;
//					JDTORecord[] sndMsgs = new JDTORecord[sCnt];
//
//					for (int kk = ii; kk < msgNo; kk++) {
//						if (queueNm.equals(msgInfo[kk][1])) {
//							for (int m = 1; m <= msgNos[kk][0]; m++) {
//								JDTORecord sndMsg = sndMsgSet.getRecord(msgNos[kk][m]);
//
//								if (sndMsg != null) {
//									sndMsgs[sNo] = sndMsg;
//									sNo++;
//								}
//							}
//
//							msgInfo[kk][0] = "";
//						}
//					}
//
//					//여러건 전송
//					sndQueue(logId, methodNm, queueNm, sndMsgs);
//				}
//			}
//
//			//송신 결과 Log 처리부분
//			commUtils.printLog(logId, "전송 합계 : " + sndCnt + " 건", "IS");
//			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : JMS 인터페이스 송신 처리 - Main 프로그램과 상관없이 무조건 전송
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public void sndToJMS(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "JMS송신[CCommSeEJB.sndToJMS] < " + sndData.getResultMsg();

		try {
			commUtils.printLog(logId, methodNm, "I+");

			String msgId = ""; //IF ID

			JDTORecord sndMsg = (JDTORecord)sndData.getField("SEND_DATA");
			//SEND_DATA가 없을 경우
			if (sndMsg == null) {
				sndMsg = sndData;
			}

			//JMS 송신 전문 IF ID
			msgId = commUtils.trim(sndData.getFieldString("JMS_TC_CD"));

			//불량 전문은 Logging하고 종료
			if ("".equals(msgId)) {
				commUtils.printParam("JMS_TC_CD가 없음", sndMsg);
				throw new Exception("JMS_TC_CD가 없는 전문입니다.");
			}
			
			//Queue명 조회
			String queueNm = getQueueNm(logId, methodNm, msgId);
			
			if ("".equals(queueNm)) {
				commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsg);
				throw new Exception("[ " + msgId + " ]의 Queue명을 찾을 수 없습니다.");
			}

			//JMS Queue로 전송
			sndQueue(logId, methodNm, queueNm, sndMsg);

			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 *      [A] 오퍼레이션명 : EAI인터페이스 송신 처리 - Main 프로그램과 상관없이 무조건 전송
	 *      
	 *      [B] 처리 개요    : 3개(JMS_TC_CD, JMS_TC_CREATE_DDTT, JMS_TC_MESSAGE)의 항목이 반드시 존재하여야 함.
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public void sndToEAI(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "EAI송신[CCommSeEJB.sndToEAI] < " + sndData.getResultMsg();
		
		try {
			commUtils.printLog(logId, methodNm, "I+");

			String msgId  = ""; //IF ID
			String tcMsg  = ""; //TCMessage
			String queueNm = "";
			JDTORecord sndMsg = null;
			JDTORecordSet sndMsgSet = null;

			Object obj = sndData.getField("SEND_DATA");

			if (obj == null) {
				sndMsg = sndData;
			} else {
				if (obj instanceof JDTORecord) {
					sndMsg = (JDTORecord)obj;
				} else if (obj instanceof JDTORecordSet) {
					sndMsgSet = (JDTORecordSet)obj;
				} else {
					commUtils.printLog(logId, methodNm + " : [SEND_DATA] 잘못된 Data Type입니다 .", "IS");
					return;
				}
			}
			
			//EAI Queue로 전송
			if (sndMsg != null) {
				//1건 전송
				msgId = commUtils.trim(sndMsg.getFieldString("JMS_TC_CD"     )); //IF ID
				tcMsg = commUtils.trim(sndMsg.getFieldString("JMS_TC_MESSAGE")); //TCMessage

				//불량 전문은 Logging하고 종료
				if ("".equals(msgId) || "".equals(tcMsg)) {
					commUtils.printParam("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없음", sndMsg);
					throw new Exception("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없는 전문입니다.");
				}
				
				//Queue명 조회
				queueNm = getQueueNm(logId, methodNm, msgId);
				
				if ("".equals(queueNm)) {
					commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsg);
					throw new Exception("[ " + msgId + " ]의 Queue명을 찾을 수 없습니다.");
				}

				//EAI Queue로 전송
				sndQueue(logId, methodNm, queueNm, sndMsg);
			} else {
				//Multi 전송
				int sndCnt = sndMsgSet.size(); //전송Data 건수

				if (sndCnt <= 0) {
					commUtils.printLog(logId, "전송할 Data가 존재하지 않습니다 . < " + methodNm, "IS");
					return;
				}
				
				//JMS에 송신하기 위해 JDTORecord[]에 Set
				JDTORecord[] sndMsgs = new JDTORecord[sndCnt];

				for (int ii = 0; ii < sndCnt; ii++) {
					msgId = commUtils.trim(sndMsgSet.getRecord(ii).getFieldString("JMS_TC_CD"     )); //IF ID
					tcMsg = commUtils.trim(sndMsgSet.getRecord(ii).getFieldString("JMS_TC_MESSAGE")); //TCMessage

					//불량 전문은 LIng하고 종료
					if ("".equals(msgId) || "".equals(tcMsg)) {
						commUtils.printParam("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없음", sndMsgSet.getRecord(ii));
						throw new Exception("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없는 전문입니다.");
					}

					//Queue명 조회
					queueNm = getQueueNm(logId, methodNm, msgId);
					
					if ("".equals(queueNm)) {
						commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsg);
						throw new Exception("[ " + msgId + " ]의 Queue명을 찾을 수 없습니다.");
					}
					
					sndMsgs[ii] = sndMsgSet.getRecord(ii);
				}

				//EAI Queue로 전송
				sndQueue(logId, methodNm, queueNm, sndMsgs);
			}

			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : IF ID로 Queue명을 조회
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String msgId
	 *      @return String
	 *      @throws DAOException
	*/
	public String getQueueNm_old(String logId, String methodNm, String msgId) throws DAOException {
		try {
			String queueNm = ""; //JMS Queue명

			if ("".equals(msgId) || msgId.length() < 5) {
				return queueNm;
			}
				
			//TB_YD_Z_IF Table에 등록된 Queue명을 조회
			JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
			
			if (jrRst != null && jrRst.size() > 0) {
				queueNm = commUtils.trim(jrRst.getRecord(0).getFieldString("QUEUE_NAME")); //Queue명
		    }

			//TB_YD_Z_IF Table에 등록된 Queue명이 없으면
			if ("".equals(queueNm) || !queueNm.startsWith("jms.queue.")) {
				queueNm = msgId.substring(2, 4); //수신처

				if ("YD".equals(queueNm)) {
					if ("YDYDJ031".equals(msgId)) {
						queueNm = "jms.queue.YDH_MDB_QUEUE"; //후판압연지시 제품창고 송신
					}else{
						queueNm = "jms.queue.YDB_MDB_QUEUE"; //일단 연주야드 내부Queue	
					} 	
				} else {
					if ("L".equals(msgId.substring(4, 5))) {
						//야드관리 EAI Queue
						queueNm = "jms.queue.YD_EAI_QUEUE"; //야드관리 EAI Queue
					} else {
						//내부 JMS Queue명
						queueNm = "jms.queue." + queueNm + "_MDB_QUEUE"; //내부 JMS Queue명
					}
				}
			}
			
			return queueNm;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, "Queue명[CCommSeEJB.getQueueNm] < " + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : IF ID로 Queue명을 조회
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String msgId
	 *      @return String
	 *      @throws DAOException
	*/
	public String getQueueNm(String logId, String methodNm, String msgId) throws DAOException {
		try {
			String queueNm = ""; //JMS Queue명

			if ("".equals(msgId) || msgId.length() < 5) {
				return queueNm;
			}
			
			
			if ("Y".equals(commDao.getWebMothodYn())) {
				//---------------------------------------------------------------------------------------------
				//WebMethod EAI 방식 변경에 따른 BRE에서 큐명 호출 2019.08.20
				//---------------------------------------------------------------------------------------------
				if ("YD".equals(msgId.substring(2, 4))) {
					if ("YDYDJ031".equals(msgId)) {
						queueNm = "jms.queue.YDH_MDB_QUEUE"; //후판압연지시 제품창고 송신
					}else{
						queueNm = "jms.queue.YDB_MDB_QUEUE"; //일단 연주야드 내부Queue	
					}
				} else {
					if ("L".equals(msgId.substring(4, 5))) {
						
						queueNm = jmsQSnder.getQueueName("YD", msgId);
						
						if("".equals(queueNm)){
							queueNm ="jms.queue.YD_EAI_QUEUE"; 
						}else{
							queueNm ="jms.queue."+queueNm;
						}
								
					} else {
						//내부 JMS Queue명
						queueNm = "jms.queue." + msgId.substring(2, 4) + "_MDB_QUEUE"; //내부 JMS Queue명
					}
				}
				//---------------------------------------------------------------------------------------------	
				
			} else {			

				//TB_YD_Z_IF Table에 등록된 Queue명을 조회
				JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
				
				if (jrRst != null && jrRst.size() > 0) {
					queueNm = commUtils.trim(jrRst.getRecord(0).getFieldString("QUEUE_NAME")); //Queue명
			    }
	
				//TB_YD_Z_IF Table에 등록된 Queue명이 없으면
				if ("".equals(queueNm) || !queueNm.startsWith("jms.queue.")) {
					queueNm = msgId.substring(2, 4); //수신처
	
					if ("YD".equals(queueNm)) {
						if ("YDYDJ031".equals(msgId)) {
							queueNm = "jms.queue.YDH_MDB_QUEUE"; //후판압연지시 제품창고 송신
						}else{
							queueNm = "jms.queue.YDB_MDB_QUEUE"; //일단 연주야드 내부Queue	
						}
					} else {
						if ("L".equals(msgId.substring(4, 5))) {
							//야드관리 EAI Queue
							queueNm = "jms.queue.YD_EAI_QUEUE"; //야드관리 EAI Queue
						} else {
							//내부 JMS Queue명
							queueNm = "jms.queue." + queueNm + "_MDB_QUEUE"; //내부 JMS Queue명
						}
					}
				}
			}
			
			return queueNm;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, "Queue명[CCommSeEJB.getQueueNm] < " + methodNm, e));
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : Queue로 전문 1건 송신
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String queueNm
	 *      @param JDTORecord sndMsg
	 *      @return void
	 *      @throws DAOException
	*/
	public void sndQueue(String logId, String methodNm, String queueNm, JDTORecord sndMsg) throws DAOException {
		try {
			commUtils.printParam(logId + " " + queueNm + " 송신  < " + methodNm, sndMsg);

			// Property Service 인스턴스를 취득합니다.
			PropertyService propertyService = PropertyService.getInstance();
			// Queue 명칭을 Property로부터 취득합니다.
			String queueName = propertyService.getProperty("common.properties", queueNm);

			JmsQueueSender sender = new JmsQueueSender();
			// Queue에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName);

			//JMS Log에 남으므로 초기화
			String rstCd = (logId == null || "".equals(logId)) ? null : logId;
			sndMsg.setResultCode(rstCd);
			sndMsg.setResultMsg(null);

			// Queue에 데이터를 전송합니다.
			String id = sender.send(sndMsg);

			commUtils.printLog(logId, queueNm + " 1 건 송신 : " + id, "SQ");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, queueNm + " 송신[sndQueue] < " + methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : Queue로 전문 여러건 송신
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다. 
	 *      @param String logId
	 *      @param String methodNm
	 *      @param String queueNm
	 *      @param JDTORecord[] sndMsg
	 *      @return String[]
	 *      @throws DAOException
	*/
	public void sndQueue(String logId, String methodNm, String queueNm, JDTORecord[] sndMsg) throws DAOException {
		try {
			commUtils.printParam(logId + " " + queueNm + " 송신  < " + methodNm, sndMsg);

			// 프로퍼티 서비스 인스턴스를 취득합니다.
			PropertyService propertyService = PropertyService.getInstance();
			// 열연 EAI Queue 명칭을 프로퍼티로부터 취득합니다.
			String queueName = propertyService.getProperty("common.properties", queueNm);

			JmsQueueSender sender = new JmsQueueSender();
			// 큐에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName);

			//JMS Log에 남으므로 초기화
			String rstCd = (logId == null || "".equals(logId)) ? null : logId;
			for (int ii = 0; ii < sndMsg.length; ii++) {
				sndMsg[ii].setResultCode(rstCd);
				sndMsg[ii].setResultMsg(null);
			}

			// 큐에 데이터를 전송합니다.
			String[] id = sender.send(sndMsg);

			commUtils.printLog(logId, queueNm + " " + sndMsg.length + " 건 송신 : " +	commUtils.toString(id), "SQ");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, queueNm + " Multi송신[sndQueue] < " + methodNm, e));
		}
	}

	/**
	 *      [A] 오퍼레이션명 : INSERT,UPDATE (Transaction 분리)
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord rcvMsg
	 *      @return JDTORecord
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
     */			
	public int execQueryIdTx(JDTORecord rcvMsg, String queryId) throws DAOException {
		String methodNm = "Transaction 분리 수행 [CCommSeEJB.execQueryIdTx] < " + rcvMsg.getResultMsg();
		String logId 	= rcvMsg.getResultCode();
		int intRtnVal = 0;
		
		try {
			commUtils.printLog(logId, methodNm, "S+");

			intRtnVal = commDao.update(rcvMsg, queryId, logId, methodNm, "Transaction 분리 수행");
			
			commUtils.printLog(logId, methodNm, "S-");

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
		return intRtnVal;
	} 	
	

	/**
	 * GridData - 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param rcvMsg
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getTblData(GridData gdReq) throws DAOException {
		
		String mthdNm = "조회[CCommSeEJB.getTblData] < " + gdReq.getNavigateValue();
		String logId  = commUtils.getLogId();
		
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		
		try {	
		
			commUtils.printLog(logId, mthdNm, "S+");
			
			JDTORecordSet outRecordSet = null;
			
			String sTblNm = gdReq.getParam("TABLE_NAME");
			
			int nPkCnt = Integer.parseInt(gdReq.getParam("PK_CNT")); //
			String sQuery = sTblNm + " WHERE 1 = 1 ";
			
			for (int i = 1 ; i <= nPkCnt; ++i) {
				sQuery = sQuery + " AND " + gdReq.getParam("PK_NM"+i) + " = '" +gdReq.getParam("PK_VAL"+i) + "'";
			}

			outRecordSet = assistantDAO.getRecordSet("com.inisteel.cim.yd.ccoil.dao.CCommSeEJB.getSelect", sQuery, null);
		
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = commUtils.jdtoRecordToGridData(gdRtn, outRecordSet.toList(), gdReq);
			
			commUtils.printLog(logId, mthdNm, "S+");
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}
	
	/**
	 * GridData - 수정
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param rcvMsg
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData setTblData(GridData gdReq) throws DAOException {
		
		String mthdNm = "수정[CCommSeEJB.setTblData] < " + gdReq.getNavigateValue();
		String logId  = commUtils.getLogId();
		
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		
		try {	
		
			commUtils.printLog(logId, mthdNm, "S+");
			
			String sQuery = gdReq.getParam("QUERY");

			assistantDAO.trtProcess("com.inisteel.cim.yd.ccoil.dao.CCommSeEJB.setYdTblData", sQuery, null);
		
			commUtils.printLog(logId, mthdNm, "S+");
			return gdReq;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, mthdNm, e));
		}
	}	

	/**
	 *      [A] 오퍼레이션명 : 인터페이스 송신 처리 (EAI, JMS 공통) PIDEV
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	*/
	public void sndInterfacePI(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "I/F송신[CCommSeEJB.sndInterfacePI] < " + sndData.getResultMsg();

		try {
			commUtils.printLog(logId, methodNm, "I+");

			String msgId   = ""; //IF ID
			String queueNm = ""; //Queue명

			int msgNo  = 0;  //IF ID 번호
			int sndCnt = 0;  //전송Data 건수
			boolean chkOK = false; //정상여부 Check
			
			JDTORecordSet jsMsgSetTemp = JDTORecordFactory.getInstance().createRecordSet("");
			JDTORecord    jrMsgSetTemp = JDTORecordFactory.getInstance().create();

			JDTORecord    jrSndMsg = JDTORecordFactory.getInstance().create();
			JDTORecordSet jsSndMsg = (JDTORecordSet)sndData.getField("SEND_DATA");
			if (jsSndMsg == null || jsSndMsg.size() <= 0) {
				commUtils.printLog("", "\n" + logId + " ■Info■ " + "Method  : " + methodNm + "\n" + logId + " ■Info■ " + "Message : " + "전송할 Data가 존재하지 않습니다 .", "IS");
				return;
			}
			//MQ인터페이스 정보 검색
			
	       	commUtils.printParam("I/F송신[CCommSeEJB.sndInterfacePI 송신]", jsSndMsg);
			int sndCnt1 = jsSndMsg.size(); //전송Data 건수			
			for (int ii = 1; ii <= sndCnt1; ii++) {
				jsSndMsg.absolute(ii);
				jrSndMsg = jsSndMsg.getRecord();
				String msgIdMq  = commUtils.nvl(jrSndMsg.getFieldString("MQ_TC_CD"),"");
				String ydGp     = commUtils.nvl(jrSndMsg.getFieldString("YD_GP"),"");
//				if (!"".equals(msgIdMq) && (msgIdMq.length() == 12)) {
				
				if(msgIdMq.startsWith("M10")) { 				
					commUtils.printLog(logId, "YD_GP:" + ydGp, "SL");
//					if ("J".equals (ydGp)) {
//					   //MQ 송신처리 해야 함
//						M10YdExLm13Sender.SendMessage(commUtils.jdtoRecordToLinkedHashMap(jrSndMsg));
//					}
//					if (msgIdMq.endsWith("5")) {
//						   //MQ 송신처리 해야 함
//								M10YdExLm53Sender.SendMessage(commUtils.jdtoRecordToLinkedHashMap(jrSndMsg));
//					}
					if ("J".equals (ydGp)) {
					   //MQ 송신처리 해야 함
						M10YdExLm13Sender.SendMessage(commUtils.jdtoRecordToLinkedHashMap(jrSndMsg));
					}
					
				} else {
					jsMsgSetTemp.addRecord(jrSndMsg);
				}	
			}
			
			jrMsgSetTemp.addField("SEND_DATA", jsMsgSetTemp);
///			
			JDTORecordSet sndMsgSet = (JDTORecordSet)jrMsgSetTemp.getField("SEND_DATA");
			if (sndMsgSet == null || sndMsgSet.size() <= 0) {
				commUtils.printLog("", "\n" + logId + " ■Info■ " + "Method  : " + methodNm + "\n" + logId + " ■Info■ " + "Message : " + "전송할 Data가 존재하지 않습니다 .", "IS");
				return;
			}

			//인터페이스 정보
			sndCnt = sndMsgSet.size(); //전송Data 건수
			int[][] msgNos = new int[sndCnt][sndCnt+1]; //인터페이스별 건수 및 IF ID 번호
			String[][] msgInfo = new String[sndCnt][2]; //IF ID, Queue명

			//같은 IF ID 끼리 정리
			for (int ii = 0; ii < sndCnt; ii++) {
				//EAI, JMS, HTTP(출하관리 등) 송신 전문 IF ID
				msgId = commUtils.getMsgId(sndMsgSet.getRecord(ii));
				
				if (!"".equals(msgId) && ((msgId.length() == 8)||(msgId.length() == 7))) {
					//기 등록된 List에서 찾기
					chkOK = true;
					for (int kk = 0; kk < sndCnt; kk++) {
						if (msgId.equals(msgInfo[kk][0])) {
							msgNos[kk][0] = msgNos[kk][0] + 1;
							msgNos[kk][msgNos[kk][0]] = ii;
							chkOK = false;
							break;
						}
					}

					//못 찾으면 신규로 등록
					if (chkOK) {
						//Queue명 조회
						commUtils.printLog(logId, "sndMsgSet.size111111111111111"  , "SL");
						queueNm = getQueueNm(logId, methodNm, msgId);
						commUtils.printLog(logId, "sndMsgSet.size(1)queueNm" +  queueNm , "SL");
						if (!"".equals(queueNm)) {
							msgNos[msgNo][0] = 1;
							msgNos[msgNo][1] = ii;
							msgInfo[msgNo][0] = msgId;
							msgInfo[msgNo][1] = queueNm;
							msgNo++;
						} else {
							commUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsgSet.getRecord(ii));
						}

					}
				}
			}
			commUtils.printLog(logId, "msgNo.size(2)" , "SL");	
			//송신 전문 편성
			for (int ii = 0; ii < msgNo; ii++) {
				msgId   = msgInfo[ii][0];
				queueNm = msgInfo[ii][1];
				//msgId가 없으면 Skip
				if ("".equals(msgId)) {	continue; }

				int sCnt = 0;

				for (int kk = ii; kk < msgNo; kk++) {
					if (queueNm.equals(msgInfo[kk][1])) {
						sCnt = sCnt + msgNos[kk][0];
					}
				}
				
				//전송건수 별 전송처리
				if (sCnt == 1) {
					//전송건수가 1개일 경우
					JDTORecord sndMsg = sndMsgSet.getRecord(msgNos[ii][1]);

					//msgId 삭제
					msgInfo[ii][0] = "";

					commUtils.printLog(logId, "msgNo.size(4)" , "SL");	
					if (sndMsg != null) {
						sndQueue(logId, methodNm, queueNm, sndMsg);
					}
					commUtils.printLog(logId, "msgNo.size(4111111)" , "SL");	

					
				} else {
					//여러개일 경우
					int sNo  = 0;
					JDTORecord[] sndMsgs = new JDTORecord[sCnt];

					for (int kk = ii; kk < msgNo; kk++) {
						if (queueNm.equals(msgInfo[kk][1])) {
							for (int m = 1; m <= msgNos[kk][0]; m++) {
								JDTORecord sndMsg = sndMsgSet.getRecord(msgNos[kk][m]);

								if (sndMsg != null) {
									sndMsgs[sNo] = sndMsg;
									sNo++;
								}
							}

							msgInfo[kk][0] = "";
						}
					}
					commUtils.printLog(logId, "msgNo.size(5)" , "SL");	

					//여러건 전송
					sndQueue(logId, methodNm, queueNm, sndMsgs);
					commUtils.printLog(logId, "msgNo.size(6)" , "SL");	
				}
			}

			//송신 결과 Log 처리부분
			commUtils.printLog(logId, "전송 합계 : " + sndCnt + " 건", "IS");
			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	
	
	
}
