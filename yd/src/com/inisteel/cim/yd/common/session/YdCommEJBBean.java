/**
 * @(#)YdCommEJBBean
 *
 * @version          V1.00
 * @author           허철호
 * @date             2012/11/22
 * 
 * @description      야드관리 공통 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2012/11/22   허철호      허철호      최초 등록
 */
package com.inisteel.cim.yd.common.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.yd.common.dao.YdCommDAO;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;

/**
 *      [A] 클래스명 : 야드관리 공통 처리
 *
 * @ejb.bean name="YdCommEJB" jndi-name="YdCommEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class YdCommEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils slabUtils = new YdSlabUtils();
	private YdCommDAO commDao = new YdCommDAO();
	private SlabYdCommDAO slabYdCommDao = new SlabYdCommDAO();
	private YmCommUtils commUtils = new YmCommUtils();
	JmsQueueSender jmsQSnder =new JmsQueueSender();
	PropertyService propertyService = null;
	
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
		String logId = slabUtils.getLogId();
		String methodNm = "수신[YdCommEJB.rcvInterface]";
		String msgId = ""; //IF ID
		String msgNm = ""; //IF 명

		try {
			//JMS송신시 추가되는 항목값이 있으면 logId를 변경
			String uniqueId = slabUtils.trim(rcvMsg.getFieldString("UNIQUE_ID"));
			if (!"".equals(uniqueId)) {
				logId = uniqueId;
			}
			
			slabUtils.printLog(logId, "I/F" + methodNm, "I+");

			String classNm = ""; //처리 Class명
			String mthdNm  = ""; //처리 Method명
			String errMsg  = ""; //오류내용

			//EAI, JMS, HTTP(출하관리 등) 수신 전문 IF ID
			msgId = slabUtils.getMsgId(rcvMsg);
			//후판 슬랄브 수신 전문처리 Log 확인용
			//2020-11-09 염용선
			slabUtils.printLog(logId, "YdComm(" + msgId + ") >>" , "IR_PSLAB");
			slabUtils.printParam(logId, rcvMsg);
			/////////////
			if ("".equals(msgId)) {
		    	errMsg = "수신된 전문의 IF ID가 존재하지 않습니다.";
			} else {
				JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
				
				if (jrRst != null && jrRst.size() > 0) { 
					msgNm   = slabUtils.trim(jrRst.getRecord(0).getFieldString("IF_NM"       )); //IF 명
			    	classNm = slabUtils.trim(jrRst.getRecord(0).getFieldString("CLASS_NAME"  )); //Class명
			    	mthdNm  = slabUtils.trim(jrRst.getRecord(0).getFieldString("METHODE_NAME")); //Method명

			    	if ("".equals(classNm) || "".equals(mthdNm)) { 
				    	errMsg = "[ " + msgId + " ]의 처리 프로그램이 I/F(TB_YD_Z_IF) Table에 정의되지 않았습니다.\n";
				    }
			    } else {
			    	errMsg = "[ " + msgId + " ]의 정보가  I/F(TB_YD_Z_IF) Table에 존재하지 않습니다.\n";
			    }
			}

			methodNm = msgNm + "(" + msgId + ")" + methodNm;

			if (!"".equals(errMsg)) {
				slabUtils.printParam(logId + " " + methodNm, rcvMsg);
				throw new Exception(errMsg);
			}

			rcvMsg.setResultCode(logId);
			rcvMsg.setResultMsg(methodNm);
			
			//수신 전문처리 Log
			slabUtils.printLog(logId, msgNm + "(" + msgId + ") >> [ " + classNm + "." + mthdNm + " ]", "IR");

			EJBConnector rcvConn = new EJBConnector("default", classNm, this);
			if (classNm.startsWith("SlabYd")) {
				//신규 프로그램 이면 전송할 전문을 Return 받아 전송
				JDTORecord jrRst = (JDTORecord)rcvConn.trx(mthdNm, new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
				
				//전송할 Data가 있으면 전송 처리
				if (jrRst != null) {
					jrRst.setResultCode(logId);
					jrRst.setResultMsg(methodNm);
					
					sndInterface(jrRst);
				}
			} else {
				//구 프로그램 이면 수신 프로그램 Call
				rcvConn.trx(mthdNm, new Class[] { JDTORecord.class }, new Object[] { rcvMsg });
			}

			slabUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, msgNm, e));
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
		String methodNm = "I/F송신[YdCommEJB.sndInterface] < " + sndData.getResultMsg();

		try {
			slabUtils.printLog(logId, methodNm, "I+");

			String msgId   = ""; //IF ID
			String queueNm = ""; //Queue명

			int msgNo  = 0;  //IF ID 번호
			int sndCnt = 0;  //전송Data 건수
			boolean chkOK = false; //정상여부 Check

			JDTORecordSet sndMsgSet = (JDTORecordSet)sndData.getField("SEND_DATA");
			if (sndMsgSet == null || sndMsgSet.size() <= 0) {
				slabUtils.printLog("", slabUtils.makeErrorLog(logId, methodNm, "전송할 Data가 존재하지 않습니다 ."), "IS");
				return;
			}

			//인터페이스 정보
			sndCnt = sndMsgSet.size(); //전송Data 건수
			int[][] msgNos = new int[sndCnt][sndCnt+1]; //인터페이스별 건수 및 IF ID 번호
			String[][] msgInfo = new String[sndCnt][2]; //IF ID, Queue명
			
			//같은 IF ID 끼리 정리
			for (int ii = 0; ii < sndCnt; ii++) {
				//EAI, JMS, HTTP(출하관리 등) 송신 전문 IF ID
				msgId = slabUtils.getMsgId(sndMsgSet.getRecord(ii));
				
				if (!"".equals(msgId) && msgId.length() == 8) {
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
						queueNm = getQueueNm(logId, methodNm, msgId);
						
						if (!"".equals(queueNm)) {
							msgNos[msgNo][0] = 1;
							msgNos[msgNo][1] = ii;
							msgInfo[msgNo][0] = msgId;
							msgInfo[msgNo][1] = queueNm;
							msgNo++;
						} else {
							slabUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsgSet.getRecord(ii));
						}

					}
				}
			}
				
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

					if (sndMsg != null) {
						sndQueue(logId, methodNm, queueNm, sndMsg);
					}
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

					//여러건 전송
					sndQueue(logId, methodNm, queueNm, sndMsgs);
				}
			}

			//송신 결과 Log 처리부분
			slabUtils.printLog(logId, "전송 합계 : " + sndCnt + " 건", "IS");
			slabUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
		String methodNm = "JMS송신[YdCommEJB.sndToJMS] < " + sndData.getResultMsg();

		try {
			slabUtils.printLog(logId, methodNm, "I+");

			String msgId = ""; //IF ID

			JDTORecord sndMsg = (JDTORecord)sndData.getField("SEND_DATA");
			//SEND_DATA가 없을 경우
			if (sndMsg == null) {
				sndMsg = sndData;
			}

			//JMS 송신 전문 IF ID
			msgId = slabUtils.trim(sndData.getFieldString("JMS_TC_CD"));

			//불량 전문은 Logging하고 종료
			if ("".equals(msgId)) {
				slabUtils.printParam("JMS_TC_CD가 없음", sndMsg);
				throw new Exception("JMS_TC_CD가 없는 전문입니다.");
			}
			
			//Queue명 조회
			String queueNm = getQueueNm(logId, methodNm, msgId);
			
			if ("".equals(queueNm)) {
				slabUtils.printParam("[ " + msgId + " ]의 Queue명을 찾을 수 없음", sndMsg);
				throw new Exception("[ " + msgId + " ]의 Queue명을 찾을 수 없습니다.");
			}

			//JMS Queue로 전송
			sndQueue(logId, methodNm, queueNm, sndMsg);

			slabUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	}
	

	/**
	 *      [A] 오퍼레이션명 : EAI인터페이스 송신 처리 - Main 프로그램과 상관없이 무조건 전송
	 *      
	 *      [B] 처리 개요          : 3개(JMS_TC_CD, JMS_TC_CREATE_DDTT, JMS_TC_MESSAGE)의 항목이 반드시 존재하여야 함.
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	 *		@ejb.transaction type="RequiresNew"
	*/
	public void sndToEAI(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "EAI송신[YdCommEJB.sndToEAI] < " + sndData.getResultMsg();
		
		try {
			slabUtils.printLog(logId, methodNm, "I+");

			String msgId  = ""; //IF ID
			String tcMsg  = ""; //TCMessage
			JDTORecord sndMsg = null;
			JDTORecordSet sndMsgSet = null;
			JDTORecord targetId = null;
			JDTORecordSet targetIdSet = null;
			String chkYN = "";
			
			String szQueueName = "";

			Object obj = sndData.getField("SEND_DATA");

			if (obj == null) {
				sndMsg = sndData;
			} else {
				if (obj instanceof JDTORecord) {
					sndMsg = (JDTORecord)obj;
				} else if (obj instanceof JDTORecordSet) {
					sndMsgSet = (JDTORecordSet)obj;
				} else {
					slabUtils.printLog(logId, methodNm + " : [SEND_DATA] 잘못된 Data Type입니다 .", "IS");
					return;
				}
			}
			
			//EAI Queue로 전송(실제로 EAI 전송 시 해당 코드 실행)
			if (sndMsg != null) {
				//1건 전송
				msgId = slabUtils.trim(sndMsg.getFieldString("JMS_TC_CD"     )); //IF ID
				tcMsg = slabUtils.trim(sndMsg.getFieldString("JMS_TC_MESSAGE")); //TCMessage

				//불량 전문은 Logging하고 종료
				if ("".equals(msgId) || "".equals(tcMsg)) {
					slabUtils.printParam("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없음", sndMsg);
					throw new Exception("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없는 전문입니다.");
				}
				
				//YDA로 전송해야하는 Queue ID 조회 (19.04.29)
				targetId = JDTORecordFactory.getInstance().create();
				targetId.setField("JMS_TC_CD", msgId);
				targetId.setField("CD_GP", "YDA");
				targetIdSet = slabYdCommDao.select(targetId , "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDao.getTargetTCcodeId" , "SYSTEM" , "sndToEAI" , "EAI인터페이스 송신 처리");
				
				if(targetIdSet.size() > 0) {
					chkYN = commUtils.trim(targetIdSet.getRecord(0).getFieldString("ITEM1"));
				}
				
				//bre 큐명 조회
				JmsQueueSender sender = new JmsQueueSender();
				szQueueName = sender.getQueueName("YD", sndMsg.getFieldString("JMS_TC_CD").toString());
				
				slabUtils.printLog(logId, methodNm + sndMsg.getFieldString("JMS_TC_CD").toString()+"/큐명 조회 결과: "+ szQueueName, "IS");
				if("".equals(szQueueName)){
					if("Y".equals(chkYN)) {
						szQueueName = "jms.queue.YDA_EAI_QUEUE";	
					} else {
						//EAI Queue로 전송
						szQueueName = "jms.queue.YD_EAI_QUEUE";
					}
					sndQueue(logId, methodNm, szQueueName, sndMsg);
				}
				else {
					sndQueue(logId, methodNm, "jms.queue."+szQueueName, sndMsg);
				}
				
				///////////////////
				/*if("Y".equals(chkYN)) {
					sndQueue(logId, methodNm, "jms.queue.YDA_EAI_QUEUE", sndMsg);
				} else {
					//EAI Queue로 전송
					sndQueue(logId, methodNm, "jms.queue.YD_EAI_QUEUE", sndMsg);
				}*/
			} else {
				//Multi 전송
				int sndCnt = sndMsgSet.size(); //전송Data 건수

				if (sndCnt <= 0) {
					slabUtils.printLog(logId, "전송할 Data가 존재하지 않습니다 . < " + methodNm, "IS");
					return;
				}
				slabUtils.printLog(logId, "전송할 데이터는 "+ Integer.toString(sndCnt) +"건입니다. < " + methodNm, "IS");
				//JMS에 송신하기 위해 JDTORecord[]에 Set
				JDTORecord[] sndMsgs = new JDTORecord[sndCnt];

				for (int ii = 0; ii < sndCnt; ii++) {
					msgId = slabUtils.trim(sndMsgSet.getRecord(ii).getFieldString("JMS_TC_CD"     )); //IF ID
					tcMsg = slabUtils.trim(sndMsgSet.getRecord(ii).getFieldString("JMS_TC_MESSAGE")); //TCMessage

					//불량 전문은 LIng하고 종료
					if ("".equals(msgId) || "".equals(tcMsg)) {
						slabUtils.printParam("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없음", sndMsgSet.getRecord(ii));
						throw new Exception("JMS_TC_CD 또는 JMS_TC_MESSAGE가 없는 전문입니다.");
					}

					sndMsgs[ii] = sndMsgSet.getRecord(ii);
				}
				JmsQueueSender sender = new JmsQueueSender();
				szQueueName = sender.getQueueName("YD",sndMsgSet.getRecord(0).getFieldString("JMS_TC_CD").toString());
				
				slabUtils.printLog(logId, methodNm + sndMsgSet.getRecord(0).getFieldString("JMS_TC_CD").toString()+"/multisend큐명 조회 결과: "+ szQueueName, "IS");
				if("".equals(szQueueName)){
					//EAI Queue로 전송
					szQueueName = "jms.queue.YD_EAI_QUEUE";

					sndQueue(logId, methodNm, szQueueName, sndMsgs);
				}
				else {
					sndQueue(logId, methodNm, "jms.queue."+szQueueName, sndMsgs);
				}
				//EAI Queue로 전송
				//sndQueue(logId, methodNm, "jms.queue.YD_EAI_QUEUE", sndMsgs);
			}

			slabUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
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
			
			
			if("Y".equals(commDao.getWebMothodYn())) {
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
						//JMS 큐명
						
						if("Y".equals(commDao.getUsingCTNewQYn())){
							//21.02.04 야드->생산통제 큐 분리
							if("CT".equals(msgId.substring(2, 4))) queueNm = "jms.queue." + msgId.substring(2, 4) + "D_MDB_QUEUE";
							else queueNm = "jms.queue." + msgId.substring(2, 4) + "_MDB_QUEUE"; //내부 JMS Queue명	
						}
						else queueNm = "jms.queue." + msgId.substring(2, 4) + "_MDB_QUEUE"; //내부 JMS Queue명
					}
				}
				//---------------------------------------------------------------------------------------------	
				
			}else{			

				//TB_YD_Z_IF Table에 등록된 Queue명을 조회
				JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
				
				if (jrRst != null && jrRst.size() > 0) {
					queueNm = slabUtils.trim(jrRst.getRecord(0).getFieldString("QUEUE_NAME")); //Queue명
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
							//JMS 큐명
							if("Y".equals(commDao.getUsingCTNewQYn())){
								//21.02.04 야드->생산통제 큐 분리
								if("CT".equals(msgId.substring(2, 4))) queueNm = "jms.queue." + msgId.substring(2, 4) + "D_MDB_QUEUE";
								else queueNm = "jms.queue." + msgId.substring(2, 4) + "_MDB_QUEUE"; //내부 JMS Queue명	
							}
							else queueNm = "jms.queue." + msgId.substring(2, 4) + "_MDB_QUEUE"; //내부 JMS Queue명
						}
					}
				}
			}
			
			return queueNm;
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, "Queue명[YdCommEJB.getQueueNm] < " + methodNm, e));
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
			slabUtils.printParam(logId + " " + queueNm + " 송신  < " + methodNm, sndMsg);

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

			//C연주 슬라브야드 tc코드 추가 전송
			JDTORecord sndMsg2 = null;
			
			if(sndMsg.getField("JMS_TC_CD").equals("YDC3L003")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L003");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L003"+subMsg);
				
			}
			else if(sndMsg.getField("JMS_TC_CD").equals("YDC3L004")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L004");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L004"+subMsg);
			}
			else if(sndMsg.getField("JMS_TC_CD").equals("YDC3L005")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L005");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L005"+subMsg);
			}
			else if(sndMsg.getField("JMS_TC_CD").equals("YDC3L002")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L002");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L002"+subMsg);
			}
			else if(sndMsg.getField("JMS_TC_CD").equals("YDC3L008")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L008");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L008"+subMsg);
			}
			else if(sndMsg.getField("JMS_TC_CD").equals("YDC3L001")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L001");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L001"+subMsg);
			}
			else if(sndMsg.getField("JMS_TC_CD").equals("YDC3L006")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L006");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L006"+subMsg);
			}
			else if(sndMsg.getField("JMS_TC_CD").equals("YDC3L007")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L007");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L007"+subMsg);
			}
			else if(sndMsg.getField("JMS_TC_CD").equals("YDC3L009")) {
				sndMsg2 = (JDTORecord)sndMsg.clone();
				sndMsg2.setField("JMS_TC_CD" , "YDA6L009");
				
				String tcMsg = sndMsg.getFieldString("JMS_TC_MESSAGE");
				String subMsg = tcMsg.substring(8);
				sndMsg2.setField("JMS_TC_MESSAGE" , "YDA6L009"+subMsg);
			}
			
			
			
			
			//전문 송신
			if(sndMsg2 == null) {
				String id = sender.send(sndMsg);
				slabUtils.printLog(logId, queueNm + " 1 건 송신 : " + id, "SQ");
			}
			else {
				//C3, A6 큐네임 분리를 위해..
				JmsQueueSender sender2 = new JmsQueueSender();
				String szQueueName = queueName;
				
				szQueueName = sender2.getQueueName("YD", sndMsg2.getField("JMS_TC_CD").toString());
				if("".equals(szQueueName)){
					szQueueName =propertyService.getProperty("common.properties", "jms.queue.YD_WM_EAI_QUEUE");
				}
				else {
					szQueueName = propertyService.getProperty("common.properties", "jms.queue."+szQueueName);
				}
				
				sender2.initQueueService(szQueueName);
				
				String id1 = sender.send(sndMsg);
				
				slabUtils.printLog(logId, queueNm + " 1 건 송신 : " + id1, "SQ");
				
				String id2 = sender2.send(sndMsg2);

				slabUtils.printLog(logId, szQueueName + " 1 건 송신 : " + id2, "SQ");
				/*JDTORecord[] totalSndMsg = new JDTORecord[2];
				totalSndMsg[0] = sndMsg;
				totalSndMsg[1] = sndMsg2;
				
				String id[] = sender.send(totalSndMsg);
				slabUtils.printLog(logId, queueNm + " 2 건 송신 : " + slabUtils.toString(id), "SQ");*/
			}

		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, queueNm + " 송신[sndQueue] < " + methodNm, e));
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
			slabUtils.printParam(logId + " " + queueNm + " 송신  < " + methodNm, sndMsg);

			// 프로퍼티 서비스 인스턴스를 취득합니다.
			PropertyService propertyService = PropertyService.getInstance();
			// 열연 EAI Queue 명칭을 프로퍼티로부터 취득합니다.
			String queueName = propertyService.getProperty("common.properties", queueNm);

			JmsQueueSender sender = new JmsQueueSender();
			// 큐에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName);

			//JMS Log에 남으므로 초기화
			String rstCd = (logId == null || "".equals(logId)) ? null : logId;
			
			String szMsg = "";
			
			
			//새로운 전문을 저장하기 위한 배열 생성
			JDTORecord[] sndMsg2 = new JDTORecord[sndMsg.length];
			//추가될 전문 갯수 카운트
			int cnt = 0;
			
			for (int ii = 0; ii < sndMsg.length; ii++) {
				sndMsg[ii].setResultCode(rstCd);
				sndMsg[ii].setResultMsg(null);
				
				//C연주슬라브야드 전문에 대해 새로운 tc code 추가
				if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L003")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L003");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L003"+subMsg);
				}
				else if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L004")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L004");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L004"+subMsg);
				}
				else if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L005")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L005");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L005"+subMsg);
				}
				else if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L002")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L002");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L002"+subMsg);
				}
				else if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L008")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L008");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L008"+subMsg);
				}
				else if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L001")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L001");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L001"+subMsg);
				}
				else if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L006")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L006");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L006"+subMsg);
				}
				else if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L007")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L007");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L007"+subMsg);
				}
				else if(sndMsg[ii].getField("JMS_TC_CD").equals("YDC3L009")) {
					cnt++;
					sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
					sndMsg2[ii].setField("JMS_TC_CD" , "YDA6L009");
					
					String tcMsg = sndMsg[ii].getFieldString("JMS_TC_MESSAGE");
					String subMsg = tcMsg.substring(8);
					sndMsg2[ii].setField("JMS_TC_MESSAGE" , "YDA6L009"+subMsg);
				}
				//else{
			//		sndMsg2[ii] = (JDTORecord)sndMsg[ii].clone();
			//		sndMsg2[ii].setField("JMS_TC_CD" , "YDA6TEST"); 
			//	}
			}
			//A6, C3 전문 큐 네임 분리작업
	
			JDTORecord[] totalSndMsg = new JDTORecord[sndMsg.length];
			
			//기존 전문 담기
			for(int i = 0 ; i <sndMsg.length; i++) {
				totalSndMsg[i] = sndMsg[i];
			}
			
			// 큐에 데이터를 전송합니다.
			String[] id = sender.send(totalSndMsg);

			slabUtils.printLog(logId, queueNm + " " + totalSndMsg.length + " 건 송신 : " +	slabUtils.toString(id), "SQ");

			
			
			//multi 송신 전문
			if(cnt>0){
		
				//YDY1L002 YDC3L009 순으로 들어올시, cnt는 1, but sndMsg2[1] 에 YDA6L009 담긴다. 
				//큐네임 find
				JmsQueueSender sender2 = new JmsQueueSender();
				String szQueueName = "";//queueName;
				
				for(int i = 0 ; i <sndMsg2.length; i++) {
					if(sndMsg2[i] == null){
						szMsg = Integer.toString(i+1)+"번째 multi 전문 empty";
						//szMsg = Integer.toString(i+1)+"번째 전문 ["+slabUtils.trim(sndMsg2[i].getField("JMS_TC_CD").toString())+"] 송신준비 시작";
						slabUtils.printLog(logId,szMsg, "SQ");
					}
					else {
						szMsg = Integer.toString(i+1)+"번째 전문 ["+slabUtils.trim(sndMsg2[i].getField("JMS_TC_CD").toString())+"] 으로 멀티송신 큐네임 검색";
						slabUtils.printLog(logId,szMsg, "SQ");
						
						
						//sndMsg2가 비어있지 않을때만 두번째 큐네임 찾는다.
						if(!"".equals(slabUtils.trim(sndMsg2[i].getField("JMS_TC_CD").toString()))){
							szQueueName = sender2.getQueueName("YD", sndMsg2[i].getField("JMS_TC_CD").toString());	
						
						}
						szMsg = "검색 결과: ["+szQueueName +"]";
						slabUtils.printLog(logId,szMsg, "SQ");
						
						break;
					}
	
				}
			
				
				if("".equals(szQueueName)){
					szQueueName =propertyService.getProperty("common.properties", "jms.queue.YD_WM_EAI_QUEUE");
				}
				else {
					szQueueName = propertyService.getProperty("common.properties", "jms.queue."+szQueueName);
				}
				
				sender2.initQueueService(szQueueName);
				
				
				
				JDTORecord[] totalSndMsg2 = new JDTORecord[cnt];
				//totalSndMsg2 count
				int count =0;
				
				for(int i = 0 ; i <sndMsg2.length; i++) {
					if(sndMsg2[i] != null){
						if(count>= cnt){
							szMsg = "sndMsg2에 담긴 A6전문: "+Integer.toString(cnt)+"개이나 multi 송신 메시지 갯수가"+Integer.toString(count)+"이므로 index 오류 유발";
							slabUtils.printLog(logId,szMsg, "SQ");
							break;
						}
						totalSndMsg2[count] = sndMsg2[i];
						count++;
					}
				}
				
				
				// 큐에 데이터를 전송합니다.
				String[] id2 = sender2.send(totalSndMsg2);

				slabUtils.printLog(logId, szQueueName + " " + totalSndMsg2.length + " 건 송신 : " +	slabUtils.toString(id2), "SQ");
			}
	
			
			
			

		/*	
			//신규 생성 전문 담기
			while(i < totalSndMsg.length){
				if(!"YDA6TEST".equals(sndMsg2[j].getField("JMS_TC_CD"))){
					totalSndMsg[i] = sndMsg2[j];
					i++;
				}
				
				j++;
			}
			
			// 큐에 데이터를 전송합니다.
			String[] id = sender.send(totalSndMsg);

			slabUtils.printLog(logId, queueNm + " " + totalSndMsg.length + " 건 송신 : " +	slabUtils.toString(id), "SQ");*/
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, queueNm + " Multi송신[sndQueue] < " + methodNm, e));
		}
	}

}
