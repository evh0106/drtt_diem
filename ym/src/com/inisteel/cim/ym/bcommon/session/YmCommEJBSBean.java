/**
 * @(#)YmCommEJBSBean
 *
 * @version          V1.00
 * @author           현대제철
 * @date             2017/02/02
 * 
 * @description      야드관리 공통 처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2017/02/02   정종균      조병기      최초 등록
 *        2017/02/22           조병기      YmCommEJBBean.java --> YmCommEJBSBean.java 
 * 
 */
package com.inisteel.cim.ym.bcommon.session;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ymPI.common.M10YmExLm12SenderFaEJBSBean;
import com.inisteel.cim.ymPI.common.M10YmExLm52SenderFaEJBSBean;

/**
 *      [A] 클래스명 : 야드관리 공통 처리
 *
 * @ejb.bean name="YmCommEJB" jndi-name="YmCommEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/

public class YmCommEJBSBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YmCommUtils commUtils = new YmCommUtils();
	private YmCommDAO commDao = new YmCommDAO();
	private JmsQueueSender jmsQSnder = new JmsQueueSender();
	private M10YmExLm12SenderFaEJBSBean      M10YmExLm12Sender   = new M10YmExLm12SenderFaEJBSBean();
	private M10YmExLm52SenderFaEJBSBean      M10YmExLm52Sender   = new M10YmExLm52SenderFaEJBSBean();
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
		String methodNm = "수신[YmCommEJB.rcvInterface]";
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
			String oprnSysGp    = ""; //운영시스템구분

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
			    	oprnSysGp = commUtils.trim(jrRst.getRecord(0).getFieldString("OPRN_SYS_GP")); //운영시스템구분
			    	
			    	//로그ID에 운영시스템구분(야드구분) 추가
			    	logId = "[" + oprnSysGp + "]" + logId;

			    	if ("".equals(classNm) || "".equals(mthdNm)) { 
				    	errMsg = "[ " + msgId + " ]의 처리 프로그램이 I/F(TB_YM_Z_IF) Table에 정의되지 않았습니다.\n";
				    }
			    } else {
			    	errMsg = "[ " + msgId + " ]의 정보가  I/F(TB_YM_Z_IF) Table에 존재하지 않습니다.\n";
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
	 *      [A] 오퍼레이션명 : 인터페이스 송신 처리 (EAI, JMS 공통)
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	*/
	public void sndInterface(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "I/F송신[YmCommEJB.sndInterface] < " + sndData.getResultMsg();

		try {
			commUtils.printLog(logId, methodNm, "I+");

			String msgId   = ""; //IF ID
			String queueNm = ""; //Queue명

			int msgNo  = 0;  //IF ID 번호
			int sndCnt = 0;  //전송Data 건수
			boolean chkOK = false; //정상여부 Check
			
			// PIDEV			
//			String sApplyYnPI = commDao.ApplyYnPI("", "", "APPPI0", "3", "*");
			if("PIDEV".equals("PIDEV")) {
				
				sndInterfacePI(sndData);
				return ;
			}		
			
			JDTORecordSet sndMsgSet = (JDTORecordSet)sndData.getField("SEND_DATA");
			if (sndMsgSet == null || sndMsgSet.size() <= 0) {
				commUtils.printLog("", commUtils.makeErrorLog(logId, methodNm, "전송할 Data가 존재하지 않습니다 ."), "IS");
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
						queueNm = getQueueNm(logId, methodNm, msgId);
						
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
			commUtils.printLog(logId, "전송 합계 : " + sndCnt + " 건", "IS");
			commUtils.printLog(logId, methodNm, "I-");
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
		String methodNm = "JMS송신[YmCommEJB.sndToJMS] < " + sndData.getResultMsg();

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
		String methodNm = "EAI송신[YmCommEJB.sndToEAI] < " + sndData.getResultMsg();
		
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
	public String getQueueNm(String logId, String methodNm, String msgId) throws DAOException {
		try {
			String queueNm = ""; //JMS Queue명

			if ("".equals(msgId) || msgId.length() < 5) {
				return queueNm;
			}
			
			if("Y".equals(commDao.getWebMothodYn())) {
				
				//WebMethod EAI 사용(Y): BRE에 등록된 Queue명을 조회
				queueNm = jmsQSnder.getQueueName("YM",msgId);
				
				if(!"".equals(queueNm)) {
					
					//BRE에 등록된 Queue 명은 "jms.queue." 구문이 없기 때문에 앞부분에 추가한다.  
					queueNm = "jms.queue." + queueNm;
					
					return queueNm;  //BRE에 등록된 큐명 리턴
				}
			}
			
			//WebMethod EAI 사용안함(N): TB_YM_Z_IF Table에 등록된 Queue명을 조회
			JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
			
			if (jrRst != null && jrRst.size() > 0) {
				queueNm = commUtils.trim(jrRst.getRecord(0).getFieldString("QUEUE_NAME")); //Queue명
		    }
			
			//TB_YS_Z_IF Table에 등록된 Queue명이 없으면
			if ("".equals(queueNm) || !queueNm.startsWith("jms.queue.")) {
				
				if ("L".equals(msgId.substring(4, 5))) {
					//야드관리 EAI Queue
					queueNm = "jms.queue.YMB_EAI_QUEUE"; 
				} else {
					//내부 JMS Queue명
					queueNm = "jms.queue." + msgId.substring(2, 4) + "_MDB_QUEUE"; //수신처 (ex: PO, TS, QM ..)
				}
			}
			
			return queueNm; 
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, "Queue명[YmCommEJB.getQueueNm] < " + methodNm, e));
		}
	}

	
	/**
	 *      [A] 오퍼레이션명 : IF ID로 Queue명을 조회 (TB_YM_Z_IF 테이블에서 Queue 명 조회 방식)
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
				
			//TB_YM_Z_IF Table에 등록된 Queue명을 조회
			JDTORecordSet jrRst = commDao.getMsgInfo(msgId);
			
			if (jrRst != null && jrRst.size() > 0) {
				queueNm = commUtils.trim(jrRst.getRecord(0).getFieldString("QUEUE_NAME")); //Queue명
		    }

			//TB_YS_Z_IF Table에 등록된 Queue명이 없으면
			if ("".equals(queueNm) || !queueNm.startsWith("jms.queue.")) {
				queueNm = msgId.substring(2, 4); //수신처

				if ("YM".equals(queueNm)) {
					queueNm = "jms.queue.YM_MDB_QUEUE"; 	
				} else {
					if ("L".equals(msgId.substring(4, 5))) {
						//야드관리 EAI Queue
						queueNm = "jms.queue.YMB_EAI_QUEUE"; //야드관리 EAI Queue
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
			throw new DAOException(commUtils.makeErrorLog(logId, "Queue명[YmCommEJB.getQueueNm] < " + methodNm, e));
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
	 *      [A] 오퍼레이션명 : 구 L2 전문 Interface 공통 수신 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param String rcvMsg
	 *      @return void
	 *      @throws DAOException
	*/
	public void rcvMessage(String rcvMsg) throws DAOException {
		String logId = commUtils.getLogId();
		String methodNm = "구전문수신[YmCommEJB.rcvMessage(String)]";
		String msgId = ""; //IF ID
		String msgNm = ""; //IF 명

		try {
			
			commUtils.printLog(logId, "I/F" + methodNm, "I+");
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(rcvMsg);

			//EAI, JMS, HTTP(출하관리 등) 수신 전문 IF ID
			msgId = commUtils.getMsgId(jDTORecord);
			
			if("CF1PB27".equals(msgId)) {
				// 압연L2 -> 1열연 SLAB야드 - W/B 4,5 Bed 정보 요구
				jDTORecord.setField("MSG_ID"	, msgId);  
				
			} else if("CF1PB11".equals(msgId)) {
				// 압연L2 -> 1열연 SLAB야드 - Line Off 요청 (HB,2CTC)
				jDTORecord.setField("MSG_ID"	, msgId);

			} else if("CF1PB16".equals(msgId)) {
				// 압연L2 -> 1열연 SLAB야드 - #4 CTC Loading 실적
				jDTORecord.setField("MSG_ID"	, msgId);
				
			} else if("CF1PB12".equals(msgId)) {
				// 압연L2 -> 1열연 COIL야드 - 분기 Conveyor COIL Line Off Request
				jDTORecord.setField("MSG_ID"	, msgId); 

			} else if("MHMI110".equals(msgId)) {
				// 냉연L2 -> 코일1냉연 대차상차정보요구
				jDTORecord.setField("MSG_ID"	, msgId);  
				jDTORecord.setField("EQUIP_CD"	, "SSX1");  
				
			} else if("MHMI220".equals(msgId)) {
				// 냉연L2 -> 코일1냉연 대차이동요구
				jDTORecord.setField("MSG_ID"	, msgId);  
				jDTORecord.setField("EQUIP_CD"	, "SSX1");   

			} else if("MHMI310".equals(msgId)) {
				// 냉연L2 -> 코일1냉연 설비상태정보
				jDTORecord.setField("MSG_ID"	, msgId);  
				jDTORecord.setField("EQUIP_STAT", commUtils.nvl(jDTORecord.getFieldString("설비상태"), ""));

			} else if("MHMI510".equals(msgId)) {
				// 냉연L2 -> 코일1냉연 코일상세정보
				jDTORecord.setField("MSG_ID"	, msgId);  
				jDTORecord.setField("COILNO"	, commUtils.nvl(jDTORecord.getFieldString("COILNO"), ""));  

			} else if("MHMI710".equals(msgId)) { 
				// 냉연L2 -> 코일1냉연 권상권하실적
				jDTORecord.setField("MSG_ID"	, msgId);  
				jDTORecord.setField("WORK_GP"	, commUtils.nvl(jDTORecord.getFieldString("작업구분"), "")); 
				jDTORecord.setField("COILNO"	, commUtils.nvl(jDTORecord.getFieldString("COILNO"), ""));
				jDTORecord.setField("EQUIP_CD"	, "JCX1");
				
			} else {
				
				throw new Exception("구전문수신 처리 TC_CODE 이상!!!");
			}
				
			EJBConnector ydEjbCon = new EJBConnector("default", this);
		    ydEjbCon.trx("YmCommEJB", "rcvInterface", jDTORecord);

			commUtils.printLog(logId, methodNm, "I-");
			
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, msgNm, e));
		}
	}
	
	// PIDEV
	/**
	 *      [A] 오퍼레이션명 : 인터페이스 송신 처리 (EAI, JMS 공통)
	 * 
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord sndData
	 *      @return void
	 *      @throws DAOException
	*/
	public void sndInterfacePI(JDTORecord sndData) throws DAOException {
		String logId = sndData.getResultCode();
		String methodNm = "I/F송신[YmCommEJB.sndInterfacePI] < " + sndData.getResultMsg();

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
			int sndCnt1 = jsSndMsg.size(); //전송Data 건수			
			for (int ii = 1; ii <= sndCnt1; ii++) {
				jsSndMsg.absolute(ii);
				jrSndMsg = jsSndMsg.getRecord();
				String msgIdMq  = commUtils.nvl(jrSndMsg.getFieldString("MQ_TC_CD"),"");
				String ydGp     = commUtils.nvl(jrSndMsg.getFieldString("YD_GP"),"");
				//if (!"".equals(msgIdMq) && (msgIdMq.length() == 12)) {
				if(msgIdMq.startsWith("M10")) { 	
					commUtils.printLog(logId, "YD_GP:" + ydGp, "SL");
//					if ("3".equals (ydGp)) {
//				   //MQ 송신처리 해야 함
//						M10YmExLm12Sender.SendMessage(commUtils.jdtoRecordToLinkedHashMap(jrSndMsg));
//					}
//					//임가공	
//					if (msgIdMq.endsWith("5")) {
//				   //MQ 송신처리 해야 함
//						M10YmExLm52Sender.SendMessage(commUtils.jdtoRecordToLinkedHashMap(jrSndMsg));
//					}
					//임가공	
					if (msgIdMq.endsWith("5")) {
				   //MQ 송신처리 해야 함
						M10YmExLm52Sender.SendMessage(commUtils.jdtoRecordToLinkedHashMap(jrSndMsg));
					} else	if ("3".equals (ydGp)) {
					   //MQ 송신처리 해야 함
						M10YmExLm12Sender.SendMessage(commUtils.jdtoRecordToLinkedHashMap(jrSndMsg));
					}
					
				} else {
					jsMsgSetTemp.addRecord(jrSndMsg);
				}	
			}

			jrMsgSetTemp.addField("SEND_DATA", jsMsgSetTemp);			
			
			JDTORecordSet sndMsgSet = (JDTORecordSet)sndData.getField("SEND_DATA");
			if (sndMsgSet == null || sndMsgSet.size() <= 0) {
				commUtils.printLog("", commUtils.makeErrorLog(logId, methodNm, "전송할 Data가 존재하지 않습니다 ."), "IS");
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
						queueNm = getQueueNm(logId, methodNm, msgId);
						
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
			commUtils.printLog(logId, "전송 합계 : " + sndCnt + " 건", "IS");
			commUtils.printLog(logId, methodNm, "I-");
		} catch (DAOException e) {
			throw e;
		} catch (Exception e) {
			throw new DAOException(commUtils.makeErrorLog(logId, methodNm, e));
		}
	}
}
