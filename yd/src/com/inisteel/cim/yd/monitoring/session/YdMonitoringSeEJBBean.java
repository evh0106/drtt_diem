/**
 * @(#)MonitoringSeEJBBean
 *
 * @version          V1.00
 * @author           신지은
 * @date             2018/07/31
 *
 * @description      야드 모니터링 데이터  처리
 * ------------------------------------------------------------------------------
 * Ver.   수정일자              요청자       수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2018/07/31   신지은        신지은      최초 등록
 */
package com.inisteel.cim.yd.monitoring.session;
   
import java.util.List;
import java.util.Vector;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.CmUtil;
import com.inisteel.cim.or.common.util.CmnUtil;
import com.inisteel.cim.yd.common.util.YdSlabUtils;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.yd.message.MessageSenderTalk;
import com.inisteel.cim.yd.monitoring.dao.YdMonitoringCommDAO;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;


/**
 *      [A] 클래스명 : 야드 모니터링 데이터  처리
 *
 * @ejb.bean name="YdMonitoringSeEJB" jndi-name="YdMonitoringSeEJB" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @weblogic.transaction-descriptor trans-timeout-seconds="300" 
 * @ejb.transaction type="Required"
*/
public class YdMonitoringSeEJBBean extends BaseSessionBean {

	private static final long serialVersionUID = 1L;
	private YdSlabUtils slabUtils = new YdSlabUtils();
	private YdMonitoringCommDAO commDao = new YdMonitoringCommDAO();
	private SlabYdCommDAO  slabYdCommDao = new SlabYdCommDAO();
	
	/**
	 * ejbCrate()
	 *
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
	}
	
	
	/***************************************************************************
	 * 야드 모니터링 로그 저장
	 **************************************************************************/
	
	/**
	 * 		[A] 오퍼레이션명 : I/F 로그 저장 function 호출
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public void callFunction(JDTORecord gdReq) throws DAOException {
		
		EJBConnector ejbConn1 		= null;
		EJBConnector ejbConn2		= null;
		JDTORecord outRecord     	= JDTORecordFactory.getInstance().create();
		
		String methodNm = "I/F 로그 저장 function 호출[MonitoringSeEJB.callFunction] < " + gdReq.getResultMsg();
		String logId = gdReq.getResultCode();
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//1. trtJMSLog() 호출 
			outRecord 	= JDTORecordFactory.getInstance().create();
			ejbConn1 = new EJBConnector("default", "YdMonitoringSeEJB", this);
			outRecord = (JDTORecord)ejbConn1.trx("trtJMSLog", new Class[] { JDTORecord.class }, new Object[] { gdReq });
			
			
			//2. trtEAILog() 호출 
			outRecord 	= JDTORecordFactory.getInstance().create();
			ejbConn2 = new EJBConnector("default", "YdMonitoringSeEJB", this);
			outRecord = (JDTORecord)ejbConn2.trx("trtEAILog", new Class[] { JDTORecord.class }, new Object[] { gdReq });
			
			slabUtils.printLog(logId, methodNm, "S-");
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : JSM Log 저장
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 *      @ejb.transaction type="RequiresNew"
	*/
	public JDTORecord trtJMSLog(JDTORecord gdReq) throws DAOException {
		String methodNm = "JSM Log 저장[MonitoringSeEJB.trtJMSLog] < " + gdReq.getResultMsg();
		String logId = gdReq.getResultCode();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getFieldString("userid")));
			jrParam.setField("V_DOMAIN" , gdReq.getField("DOMAIN"));

			/**********************************************************
			* 1. EnQ 성공 후 3분 이상 DeQ가 되지 않는 전문 내역 Insert
			**********************************************************/
			commDao.insJMSLog("JMSLog1", jrParam);
			

			/**********************************************************
			* 2. DeQ 실패 전문 내역 (2분전~현재)Insert
			**********************************************************/
			commDao.insJMSLog("JMSLog2", jrParam);
			
			
			/**********************************************************
			* 3. DeQ 성공 전문 중 10분(EJB수행시간 기준) 이상 소요된 전문 내역 (2분전~현재)Insert
			**********************************************************/
			commDao.insJMSLog("JMSLog3", jrParam);

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : EAI Log 저장
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord trtEAILog(JDTORecord gdReq) throws DAOException {
		String methodNm = "EAI Log 저장[MonitoringSeEJB.trtEAILog] < " + gdReq.getResultMsg();
		String logId = gdReq.getResultCode();
		
		JDTORecordSet jsEAILogSet;
		
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getFieldString("userid")));
			jrParam.setField("DOMAIN" , gdReq.getField("DOMAIN"));
			
			/**********************************************************
			* 1. EAI 실패 전문 조회 및 등록
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jsEAILogSet = commDao.selectEAI(jrParam, "com.inisteel.cim.yd.slabyd.monitoring.dao.MonitoringCommDAO.getEAILog1", logId, methodNm, "EAI 오류 전문 조회");

			if(jsEAILogSet != null) {
				if (jsEAILogSet.size() > 0) {	
					for(int i=0; i<jsEAILogSet.size(); i++) {
						
						JDTORecord jrParam2 = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getFieldString("userid")));
						
						jrParam2.setResultCode(logId);	//Log ID
						jrParam2.setResultMsg(methodNm);	//Log Method Name
						
						jrParam2.setField("V_GROUP_NAME", jsEAILogSet.getRecord(i).getFieldString("GROUP_NAME"));
						jrParam2.setField("V_IF_ID", jsEAILogSet.getRecord(i).getFieldString("TC_ID"));
						jrParam2.setField("V_IF_NAME", jsEAILogSet.getRecord(i).getFieldString("TC_NAME"));
						jrParam2.setField("V_IF_OCCUR_TIME", jsEAILogSet.getRecord(i).getFieldString("START_TIME"));
						jrParam2.setField("V_ELAPSED", jsEAILogSet.getRecord(i).getFieldString("ELAPSED"));
						jrParam2.setField("V_ERROR_CONTENT", jsEAILogSet.getRecord(i).getFieldString("ERR_LOG"));
						
						commDao.insEAILog("EAILog1", jrParam2);
					}
				}
			}
			
			
			/**********************************************************
			* 2. EAI 성공했지만 수행시간 긴 전문 조회 및 등록
			**********************************************************/
			
			jsEAILogSet = commDao.selectEAI(jrParam, "com.inisteel.cim.yd.slabyd.monitoring.dao.MonitoringCommDAO.getEAILog2", logId, methodNm, " EAI 성공했지만 수행시간 긴 전문 조회");

			if(jsEAILogSet != null) {
				if (jsEAILogSet.size() > 0) {	
					for(int i=0; i<jsEAILogSet.size(); i++) {
						
						JDTORecord jrParam2 = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getFieldString("userid")));
						
						jrParam2.setResultCode(logId);	//Log ID
						jrParam2.setResultMsg(methodNm);	//Log Method Name
						
						jrParam2.setField("V_GROUP_NAME", jsEAILogSet.getRecord(i).getFieldString("GROUP_NAME"));
						jrParam2.setField("V_IF_ID", jsEAILogSet.getRecord(i).getFieldString("TC_ID"));
						jrParam2.setField("V_IF_NAME", jsEAILogSet.getRecord(i).getFieldString("TC_NAME"));
						jrParam2.setField("V_IF_OCCUR_TIME", jsEAILogSet.getRecord(i).getFieldString("START_TIME"));
						jrParam2.setField("V_ELAPSED", jsEAILogSet.getRecord(i).getFieldString("ELAPSED"));
						jrParam2.setField("V_ERROR_CONTENT", jsEAILogSet.getRecord(i).getFieldString("ERR_LOG"));
						
						commDao.insEAILog("EAILog2", jrParam2);
					}
				}
			}
			
			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	/**
	 * 		[A] 오퍼레이션명 : SMS 전송을 위한 Log 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public JDTORecord trtSMSLog(JDTORecord gdReq) throws DAOException {
		String methodNm = "SMS 전송을 위한 Log 처리[MonitoringSeEJB.trtSMSLog] < " + gdReq.getResultMsg();
		String logId = gdReq.getResultCode();
		
		JDTORecordSet smsListSet;
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getFieldString("userid")));
			jrParam.setField("V_DOMAIN" , gdReq.getField("DOMAIN_GP"));
			
			/**********************************************************
			* 1. TB_YD_IFLOG SMS 전송 대상 표시
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			commDao.updIFLog("updSMSList", jrParam);
			
			
			/**********************************************************
			* 2. TB_YD_SMSLOG 전송 대상 등록
			**********************************************************/
			jrParam.setField("V_PROGRAM" , "trtSendSMS");
			
			commDao.trtSMSLog("insSMSLog", jrParam);
			
			
			
			/**********************************************************
			* 3. TB_YD_IFLOG SMS LOG ID 저장
			**********************************************************/
			commDao.updIFLog("updSMSLogId" , jrParam);
			
			
			/**********************************************************
			* 4. SMS 송신 function 호출
			**********************************************************/
			EJBConnector ejbConn 		= null;
			JDTORecord outRecord     	= JDTORecordFactory.getInstance().create();
			
			ejbConn = new EJBConnector("default", "YdMonitoringSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("trtSendSMS", new Class[] { JDTORecord.class }, new Object[] { gdReq });
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	/**
	 * 		[A] 오퍼레이션명 : SMS 전송 process
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public JDTORecord trtSendSMS(JDTORecord gdReq) throws DAOException {
		String methodNm = "SMS 전송 process[MonitoringSeEJB.trtSendSMS] < " + gdReq.getResultMsg();
		String logId = gdReq.getResultCode();
		
		JDTORecordSet smsListSet;
		JDTORecordSet chkSet;//sms 알림톡 전환 여부 체크용
		JDTORecord    jrRow = null;		//현재 Row

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getFieldString("userid")));
			jrParam.setField("DOMAIN" , gdReq.getField("DOMAIN_GP"));
			
			/**********************************************************
			* 1. SMS 전송 목록 조회
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			smsListSet = commDao.selectEAI(jrParam, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.getSMSLog", logId, methodNm, "SMS 전송 목록 조회");
			
			
			/**********************************************************
			* 2. SMS 전송
			**********************************************************/
			if(smsListSet != null && smsListSet.size() > 0) {
				String rtnMsg = "";
				
				//sms 알림톡 전환 여부
				chkSet =  commDao.selectEAI(jrParam, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.getSMStoTalkYN", logId, methodNm, "SMS 전송 목록 조회");
				String newModuleYN = "N";
				
				if(chkSet != null && chkSet.size()>0){
					jrRow = chkSet.getRecord(0);
					newModuleYN = jrRow.getFieldString("NEW_MODULE_YN");
				}
				
				for(int i=0; i<smsListSet.size(); i++) {
					int sendCount = smsListSet.getRecord(i).getFieldInt("SEND_COUNT");
					int rcvMaxCount = smsListSet.getRecord(i).getFieldInt("MAX_RECV_COUNT");
					
					if(sendCount >= rcvMaxCount) {
						//전송 횟수가 수신 가능 횟수보다 큰 경우 강제로 DEL_YN = 'Y' 처리
						jrParam.setField("V_MESSAGE_LOG_ID", smsListSet.getRecord(i).getFieldString("MESSAGE_LOG_ID"));
						commDao.trtSMSLog("updDelYn",jrParam);
					} else {
						
						JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
						
						//알림톡 전환여부 Y시 알림톡 전송
						if("Y".equals(newModuleYN)){
							//if("01029626298".equals(smsListSet.getRecord(i).getFieldString("HANDPHONE_NO"))){
							//CarMvHdSeEJBBean.java 에서 복사.
							MessageSenderTalk    sender = new MessageSenderTalk();
							String sendContent = slabUtils.nvl(smsListSet.getRecord(i).getFieldString("SEND_CONTENT_TALK"), "");
							
							if("".equals(sendContent)) sendContent = slabUtils.nvl(smsListSet.getRecord(i).getFieldString("SEND_CONTENT"), "");
							
							recPara1 = JDTORecordFactory.getInstance().create();
							recPara1.setField("PHONE_NUM", smsListSet.getRecord(i).getFieldString("HANDPHONE_NO"));
							recPara1.setField("TMPL_CD", new String("CM1"));
							recPara1.setField("SND_MSG", new String("[현대제철 공지사항]\n" + sendContent));
							recPara1.setField("SUBJECT", new String("입동지시 알림"));
							recPara1.setField("SMS_SND_NUM", new String("0416801616"));
							recPara1.setField("RECV_ID","1521612");
							recPara1.setField("GROUP_ID","KaKao");
							recPara1.setField("PROGRAM_ID","udttalk");
							sender.sendTalk(recPara1);
							
							
							//SMS 전송 유뮤 표기
							JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
							recPara2.setField("V_MESSAGE_LOG_ID", smsListSet.getRecord(i).getFieldString("MESSAGE_LOG_ID"));
							recPara2.setField("V_DOMAIN_GP" , smsListSet.getRecord(i).getFieldString("DOMAIN_GP"));
							
							commDao.trtSMSLog("updSMSLog", recPara2);
						}
						//아닐시 sms전송
						else{
							recPara1.setField("FROM_PHONE_NO", "0416801616");	
							recPara1.setField("TO_PHONE_NO"  , smsListSet.getRecord(i).getFieldString("HANDPHONE_NO")); // 010-XXXX-XXXX
							recPara1.setField("TO_CONTENT"   , smsListSet.getRecord(i).getFieldString("SEND_CONTENT"));
							rtnMsg = PlateGdsYdUtil.updSmsMsgSend(recPara1); // SMS 송신 
							
							if(rtnMsg.equals("SUCCESS")) {
								//SMS 전송 유뮤 표기
								JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
								recPara2.setField("V_MESSAGE_LOG_ID", smsListSet.getRecord(i).getFieldString("MESSAGE_LOG_ID"));
								recPara2.setField("V_DOMAIN_GP" , smsListSet.getRecord(i).getFieldString("DOMAIN_GP"));
								
								commDao.trtSMSLog("updSMSLog", recPara2);
							} else {
								//전송 실패 시 SEND_COUNT + 1
								JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
								recPara2.setField("V_MESSAGE_LOG_ID", smsListSet.getRecord(i).getFieldString("MESSAGE_LOG_ID"));
								recPara2.setField("V_DOMAIN_GP" , smsListSet.getRecord(i).getFieldString("DOMAIN_GP"));
								
								commDao.trtSMSLog("updSMSLogCnt", recPara2);
							}
						}
		
					}
				}
			}
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	/**
	 * GridData - 단순 조회
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 */
	public GridData getSelectData(GridData gdReq) throws DAOException {
		String methodNm = "조회[MonitoringSeEJB.getSelectData] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		try {
			
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			
			//Grid date 를 JDTORecord data 로 변환
			JDTORecord inRecord = CmUtil.genJDTORecord(gdReq);
			
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			commDao.jspSelect(inRecord, outRecSet, inRecord.getFieldString("QUERY_ID"), logId, methodNm);	
			
			
			//UI로 반환 할 Grid data 를 생성 
			//GridData gdRet = CmUtil.genGridData(gdReq, outRecSet); -- old version
			GridData gdRtn = OperateGridData.cloneResponseGridData(gdReq);
			GridData gdRet = slabUtils.jdtoRecordToGridData(gdRtn, outRecSet.toList(), gdReq);
			
			slabUtils.printLog(logId, methodNm, "S-", gdReq);
			
			return gdRet;
			
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}
	} // end of getSelectData	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : SMS 사용자 정보 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord scrSmsUserMgt(GridData gdReq) throws DAOException {
		String methodNm = "SMS 사용자 정보 변경[MonitoringSeEJB.scrSmsUserMgt] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String handphoneNumber  = ""; //핸드폰번호
			String recvYn = ""; //수신여부
			String maxRecvCount = "";  //중복제어값
			String domainGp = "";
			String crud = "";
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			//String[] arrYdWbookId = new String[rowCnt];
			
			for (int ii = 0; ii < rowCnt; ii++) {
				//mode check
				
				crud = slabUtils.trim(gdReq.getHeader("CRUD" ).getValue(ii));
				
				//사용자 추가
				if("C".equals(crud)){
					/**********************************************************
					* 1. 입력 정보  Check
					**********************************************************/
					String userId =  slabUtils.trim(gdReq.getHeader("USER_ID" ).getValue(ii)); //유저 id
					String userName =  slabUtils.trim(gdReq.getHeader("USER_NAME" ).getValue(ii)); //유저 id
					
					handphoneNumber  = slabUtils.trim(gdReq.getHeader("HANDPHONE_NO" ).getValue(ii)); //핸드폰번호
					recvYn 			 = slabUtils.trim(gdReq.getHeader("RECV_YN").getValue(ii)); //수신여부
					maxRecvCount 	 = slabUtils.trim(gdReq.getHeader("MAX_RECV_COUNT").getValue(ii)); //중복제어값
					domainGp    	 = slabUtils.trim(gdReq.getHeader("DOMAIN_GP").getValue(ii)); //도메인 구분 EX)YD,QM... 
					slabUtils.printLog(logId, "SMS사용자 정보 추가 [USER_ID:"+userId+" USER_NAME: "+userName+" PHONE_NUM: " + handphoneNumber + "  RECV_YN: " + recvYn + " MAX_RECV_COUNT: " + maxRecvCount +" DOMAIN_GP: "+domainGp+ "]", "SL");

					/**********************************************************
					* 2. SMS사용자 Table에  Insert
					**********************************************************/
					jrParam.setField("V_USER_ID" , userId );
					jrParam.setField("V_USER_NAME" , userName );					
					jrParam.setField("V_HANDPHONE_NO" , handphoneNumber );
					jrParam.setField("V_REGISTER", slabUtils.trim(gdReq.getParam("userid")));
					jrParam.setField("V_RECV_YN", recvYn);
					jrParam.setField("V_MAX_RECV_COUNT", maxRecvCount);
					jrParam.setField("V_DOMAIN_GP", domainGp);
					
					commDao.insSMSUserInfo("smsUser", jrParam);
				}
				//사용자 수정
				else if("U".equals(crud)){
					/**********************************************************
					* 1. 입력 정보  Check
					**********************************************************/
					handphoneNumber  = slabUtils.trim(gdReq.getHeader("HANDPHONE_NO" ).getValue(ii)); //핸드폰번호
					recvYn 			 = slabUtils.trim(gdReq.getHeader("RECV_YN").getValue(ii)); //수신여부
					maxRecvCount 	 = slabUtils.trim(gdReq.getHeader("MAX_RECV_COUNT").getValue(ii)); //중복제어값
					domainGp    	 = slabUtils.trim(gdReq.getHeader("DOMAIN_GP").getValue(ii)); //도메인 구분 EX)YD,QM... 
					slabUtils.printLog(logId, "SMS사용자 정보 변경 [ PHONE_NUM: " + handphoneNumber + "  RECV_YN: " + recvYn + " MAX_RECV_COUNT: " + maxRecvCount +" DOMAIN_GP: "+domainGp+ "]", "SL");

					/**********************************************************
					* 2. SMS사용자 Table에  Update
					**********************************************************/
					jrParam.setField("V_HANDPHONE_NO" , handphoneNumber );
					jrParam.setField("V_RECV_YN", recvYn);
					jrParam.setField("V_MAX_RECV_COUNT", maxRecvCount);
					jrParam.setField("V_USER_ID", gdReq.getHeader("USER_ID").getValue(ii));
					jrParam.setField("V_DOMAIN_GP", domainGp);
					
					commDao.updSMSUserInfo("smsUser", jrParam);
				}
				else if("D".equals(crud)){
					jrParam.setField("V_USER_ID", gdReq.getHeader("USER_ID").getValue(ii));
					jrParam.setField("V_MODIFIER", slabUtils.trim(gdReq.getParam("userid")));
					commDao.updSMSUserInfo("smsUserDel", jrParam);
				}
				
			
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 에러 기준 변경
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param GridData gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	*/
	public JDTORecord scrErrorControl(GridData gdReq) throws DAOException {
		String methodNm = "에러 기준 변경[MonitoringSeEJB.scrErrorControl] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			String controlNum  = ""; //제어횟수
			String itemName = "";
			String itemCode = "";
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getParam("userid")));

			int rowCnt = gdReq.getHeader("CHECK").getRowCount();
			
			for (int ii = 0; ii < rowCnt; ii++) {
				/**********************************************************
				* 1. 입력 정보  Check
				**********************************************************/
				controlNum  = slabUtils.trim(gdReq.getHeader("ITEM1" ).getValue(ii)); //제어횟수
				itemName = slabUtils.trim(gdReq.getHeader("REPR_CD_CONTENTS").getValue(ii)); //항목명
				itemCode = slabUtils.trim(gdReq.getHeader("ITEM").getValue(ii)); //항목코드

				slabUtils.printLog(logId, "에러 기준 변경 [ 항목명: " + itemName + "  횟수: " + controlNum + "]", "SL");

				/**********************************************************
				* 2. RULE Table에  Update
				**********************************************************/
				jrParam.setField("V_ITEM" , itemCode );
				jrParam.setField("V_REPR_CD_CONTENTS", itemName);
				jrParam.setField("V_ITEM1", controlNum);
				jrParam.setField("V_USER_ID", gdReq.getParam("userid"));
				jrParam.setField("V_DOMAIN_GP", gdReq.getParam("DOMAIN_GP"));
				
				//작업예약 Table 우선순위 Update
				commDao.updErrorControl("control", jrParam);
			
			}

			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 		[A] 오퍼레이션명 : SMS 전송을 위한 Log 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public JDTORecord trtTransDelaySMSLog(JDTORecord gdReq) throws DAOException {
		String methodNm = "이송지연 알림 SMS 전송을 위한 Log 처리[MonitoringSeEJB.trtTransDelaySMSLog] < " + gdReq.getResultMsg();
		String logId = gdReq.getResultCode();
		
		JDTORecordSet smsListSet;
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);

			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getFieldString("userid")));
			/**********************************************************
			* 1. USRYDA.TB_YD_TRANS_DELAY_SMSLOG 전송 대상 조회
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			
			smsListSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.trtTransDelaySMSLog", logId, methodNm, "메세지 발송대상 조회");
			
			if(smsListSet != null && smsListSet.size() > 0) {
				for(int i=0; i<smsListSet.size(); i++) {
					String ydGp = smsListSet.getRecord(i).getFieldString("YD_GP");
					String sender = smsListSet.getRecord(i).getFieldString("SENDER");
					String sendContent = smsListSet.getRecord(i).getFieldString("SEND_CONTENT");
					String ydCarSchId = smsListSet.getRecord(i).getFieldString("YD_CAR_SCH_ID");
					String trnEqpCd = smsListSet.getRecord(i).getFieldString("TRN_EQP_CD");
					String messageType = smsListSet.getRecord(i).getFieldString("MESSAGE_TYPE");
					
					String logMsg = "담당야드 ["+ydGp+"] 전송자 ["+sender+"] ydCarSchId ["+ydCarSchId+"] 장비코드 ["+trnEqpCd+"] 메세지구분 ["+messageType+"]"; 
					slabUtils.printLog(logId, logMsg, "SL");
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("YD_GP", ydGp);
					jrParam.setField("SENDER", sender);
					jrParam.setField("SEND_CONTENT", sendContent);
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrParam.setField("TRN_EQP_CD", trnEqpCd);
					jrParam.setField("MESSAGE_TYPE", messageType);
	
					slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.insTransDelaySMSLog", logId, methodNm, "메세지 발송대상 insert");
				}
			}
		
			
			/**********************************************************
			* 2. 이송지연 알림 SMS 송신 function 호출
			**********************************************************/
			EJBConnector ejbConn 		= null;
			JDTORecord outRecord     	= JDTORecordFactory.getInstance().create();
			
			ejbConn = new EJBConnector("default", "YdMonitoringSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("trtSendTransDelaySMS", new Class[] { JDTORecord.class }, new Object[] { gdReq });
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	/**
	 * 		[A] 오퍼레이션명 : SMS 수동 전송을 위한 Log 처리
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public JDTORecord trtTransDelaySMSByManual(GridData gdReq) throws DAOException {
		String methodNm = "이송지연 알림 SMS 수동 전송을 위한 Log 처리[MonitoringSeEJB.trtTransDelaySMSByManual] < " + gdReq.getNavigateValue();
		String logId = gdReq.getIPAddress();
		
		JDTORecordSet smsListSet;
		
		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			String sModifier 	 = slabUtils.trim(gdReq.getParam("userid"));     //야드스케줄우선순위
			
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam 	= slabUtils.getParam(logId, methodNm, sModifier);
			String trnEqpCd     = slabUtils.trim(gdReq.getParam("TRN_EQP_CD" )); //차량번호
			
					
			/**********************************************************
			* 1. 차량번호로 USRYDA.TB_YD_TRANS_DELAY_SMSLOG 전송 대상 조회
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			jrParam.setField("TRN_EQP_CD", trnEqpCd);
			
			smsListSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.trtTransDelaySMSByManual", logId, methodNm, "메세지 발송대상 조회");
			
			if(smsListSet != null && smsListSet.size() > 0) {
				for(int i=0; i<smsListSet.size(); i++) {
					String ydGp = smsListSet.getRecord(i).getFieldString("YD_GP");

					String sendContent = smsListSet.getRecord(i).getFieldString("SEND_CONTENT");
					String ydCarSchId = smsListSet.getRecord(i).getFieldString("YD_CAR_SCH_ID");
					trnEqpCd = smsListSet.getRecord(i).getFieldString("TRN_EQP_CD");
					String messageType = smsListSet.getRecord(i).getFieldString("MESSAGE_TYPE");
					
					String logMsg = "담당야드 ["+ydGp+"] 전송자 ["+sModifier+"] ydCarSchId ["+ydCarSchId+"] 장비코드 ["+trnEqpCd+"] 메세지구분 ["+messageType+"]"; 
					slabUtils.printLog(logId, logMsg, "SL");
					
					jrParam = JDTORecordFactory.getInstance().create();
					jrParam.setField("YD_GP", ydGp);
					jrParam.setField("SENDER", sModifier);
					jrParam.setField("SEND_CONTENT", sendContent);
					jrParam.setField("YD_CAR_SCH_ID", ydCarSchId);
					jrParam.setField("TRN_EQP_CD", trnEqpCd);
					jrParam.setField("MESSAGE_TYPE", messageType);
	
					slabYdCommDao.insert(jrParam, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.insTransDelaySMSLog", logId, methodNm, "메세지 발송대상 insert");
				}
			}
		
			
			/**********************************************************
			* 2. 이송지연 알림 SMS 송신 function 호출
			**********************************************************/
			EJBConnector ejbConn 		= null;
			JDTORecord outRecord     	= JDTORecordFactory.getInstance().create();
			
			outRecord.setField("userid", sModifier);
			
			ejbConn = new EJBConnector("default", "YdMonitoringSeEJB", this);
			outRecord = (JDTORecord)ejbConn.trx("trtSendTransDelaySMS", new Class[] { JDTORecord.class }, new Object[] { outRecord });
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
	/**
	 * 		[A] 오퍼레이션명 : 이송지연 SMS 전송 process
	 *
	 * 		@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 *      @param JDTORecord gdReq
	 *      @return JDTORecord
	 *      @throws DAOException
	 */
	public JDTORecord trtSendTransDelaySMS(JDTORecord gdReq) throws DAOException {
		String methodNm = "이송지연 SMS 전송 process[MonitoringSeEJB.trtSendTransDelaySMS] < " + gdReq.getResultMsg();
		String logId = gdReq.getResultCode();
		
		JDTORecordSet smsListSet;
		JDTORecordSet chkSet;//sms 알림톡 전환 여부 체크용
		JDTORecord    jrRow = null;		//현재 Row

		try {
			slabUtils.printLog(logId, methodNm, "S+", gdReq);
			//DAO Parameter - Log ID, Method, 수정자 Set
			JDTORecord jrParam = slabUtils.getParam(logId, methodNm, slabUtils.trim(gdReq.getFieldString("userid")));
			/**********************************************************
			* 1. SMS 전송 목록 조회
			**********************************************************/
			jrParam.setResultCode(logId);	//Log ID
			jrParam.setResultMsg(methodNm);	//Log Method Name
			smsListSet = slabYdCommDao.select(jrParam, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.getTransDelaySMSList", logId, methodNm, "SMS 전송 목록 조회");
			
			
			/**********************************************************
			* 2. SMS 전송
			**********************************************************/
			if(smsListSet != null && smsListSet.size() > 0) {
				for(int i=0; i<smsListSet.size(); i++) {
					JDTORecord recPara1 = JDTORecordFactory.getInstance().create();
					
					MessageSenderTalk    sender = new MessageSenderTalk();
					String sendContent = slabUtils.nvl(smsListSet.getRecord(i).getFieldString("SEND_CONTENT"), "");
					
					recPara1 = JDTORecordFactory.getInstance().create();
					recPara1.setField("PHONE_NUM", smsListSet.getRecord(i).getFieldString("HANDPHONE_NO"));
					recPara1.setField("TMPL_CD", new String("CM1"));
					recPara1.setField("SND_MSG", new String("[현대제철 공지사항]\n" + sendContent));
					recPara1.setField("SUBJECT", new String("입동지시 알림"));
					recPara1.setField("SMS_SND_NUM", new String("0416801616"));
					recPara1.setField("RECV_ID","1521612");
					recPara1.setField("GROUP_ID","KaKao");
					recPara1.setField("PROGRAM_ID","udttalk");
					sender.sendTalk(recPara1);
					
					
					//SMS 전송 유뮤 표기
					JDTORecord recPara2 = JDTORecordFactory.getInstance().create();
					recPara2.setField("MESSAGE_LOG_ID", smsListSet.getRecord(i).getFieldString("MESSAGE_LOG_ID"));
					recPara2.setField("MODIFIER", smsListSet.getRecord(i).getFieldString("SENDER"));
					
					slabYdCommDao.update(recPara2, "com.inisteel.cim.yd.monitoring.dao.MonitoringCommDAO.updTransDelaySMSList", logId, methodNm, "전송한대상 업데이트");
		
				}
			}
			
			
			
			slabUtils.printLog(logId, methodNm, "S-");

			return null;
		} catch(DAOException e) {
			throw e;
		} catch(Exception e) {
			throw new DAOException(slabUtils.makeErrorLog(logId, methodNm, e));
		}	
	}
	
}
