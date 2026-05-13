package com.inisteel.cim.ym.common;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import com.inisteel.cim.common.eai.EAIHttpSender;

import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.ym.common.YmCommonUtil;


public class YmDeleComm { 
	
	private String szSessionName = this.getClass().getName();
	
	private YmCommonUtil ymCommonUtil = new YmCommonUtil();
	
	
	
	//private static String EAI_SVR_IP  			="10.216.133.28";
	//private static String EAI_SVR_IP  			="10.216.130.42";
	private static String 	EAI_SVR_IP  			="Localhost";
	private static int  	EAI_SVR_PORT  			=6770;
	private static String 	EAI_REMOTE_URL			="http://10.216.130.42:8080/dmsnd/";
	
	
	private static String 	JMS_SVR_IP1				="10.216.130.45";	// 개발계
	private static String 	JMS_SVR_IP2				="10.216.133.163";	// 테스트계
	private static int	 	JMS_SVR_PORT 			=7100;
	private static String 	JMS_CONNECTION_FACTORY	="LJMSConnectionFactory";	
	
	
	
	
	
	
	
	
	/**
	 * 큐네임과 Record값을 받아서 CM의 JmsQueueSender를 이용하여 해당 큐에 송신한다.
	 * @param strQueueName
	 *        내부JMS : jms/XX_MDB_QUEUE
	 *        EAI JMS : jms/YD_EAI_QUEUE
	 * @param inRecord
	 * @return int
	 */
	public int jmsQSnder(String szQueueName, JDTORecord tcRecord){
		
		String szMsg="";
		String szMethodName = "jmsQSnder";
		String szTargetQName=szQueueName;
		String szTcCode="";
		
		JDTORecord eaiL2Record=null;
		int nRtc=0;
		
		
		JmsQueueSender jmsQSnder =new JmsQueueSender();
		PropertyService propertyService = null;	
		
		try{
			
			//
			// JMS : JMS_TC_CD, L2 EAI : MSG_ID
			szTcCode =ymCommonUtil.getTcCode(tcRecord);
			if( szTcCode==null || szTcCode.equals("")){
				szMsg="TC COde() Error ";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return -2;
			} 
			
			
			
			
			//
			// EAI Send Check and Make EAI Record
			propertyService = PropertyService.getInstance();
			
			
			//
			// L2송신을 위한 YD EAI QUEUE (jms/YD_EAI_QUEUE)인 경우
			// L2 EAI 송신 용 레코드로 변환 후 송신
			if( szQueueName.equals( propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE"))){
				//
				// 송신용 EAI MSg Record 생성
				eaiL2Record =JDTORecordFactory.getInstance().create();
				nRtc =makeL2EaiRecord(tcRecord, eaiL2Record);
				if( nRtc<=0){
					szMsg="EAI MSG Record Make Error (ErrCode:"+nRtc+")";
					ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					return -2;
				}
				
				tcRecord =eaiL2Record;
				
//				//---------------------------------------------------------------------------------------------
//				//WebMethod EAI 방식 변경에 따른 BRE에서 큐명 호출 2019.08.20
//				//---------------------------------------------------------------------------------------------
//				szQueueName = jmsQSnder.getQueueName("YD", szTcCode);
//				
//				if("".equals(szQueueName)){
//					szQueueName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");
//				}else{
//					szQueueName =propertyService.getProperty("common.properties", "jms.queue."+szQueueName);
//				}
//				//---------------------------------------------------------------------------------------------
				
			} // end of if()
			 
			jmsQSnder.initQueueService(szQueueName);
			jmsQSnder.send(tcRecord);


			//
			// 메시지 로깅
			// saveSndMsg(msgRecord);	// 메시지정보관리 Table에 저장
			
			
			szMsg="JMS Queue Send success (QueueName="+szTargetQName+", TC Code="+szTcCode+")";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);
			
			
		} catch(Exception e){
			
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			return -1;
		}
		
		return 0;
		
	} // end of jmsQSnder()	
	
	
	
	
	
	
	/**
	 * 큐네임과 Record값을 받아서 YD자체 Queue(jms/YD_MDB_QUEUE)로 송신한다.
	 * @param strQueueName
	 * @param inRecord
	 * @return int
	 */
	public int jmsTargetQSnder( JDTORecord tcRecord, int nTarget){
		//
		// nTarget ==1 : Local
		// nTarget ==2 : Remote 가동계
		// nTarget ==3 : Remote 테스트계
		
		String szMsg="";
		String szMethodName = "jmsTargetQSnder";
		String szTcCode="";
		String szYdJMSQName="";
		PropertyService propertyService = null;	
		
		try{
			szTcCode =ymCommonUtil.getTcCode(tcRecord);
			if( szTcCode==null || szTcCode.equals("")){
				szMsg="TC COde() Error ";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return -2;
			}
			
			
			switch(nTarget){
			case 1: 
				szYdJMSQName="jms/YD_MDB_QUEUE";
				this.queueSendMsg(szYdJMSQName, tcRecord, "Localhost", 9100);
				break;
				
			case 2:
				propertyService = PropertyService.getInstance();
				szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");	// YD_MDB_QUEUE
				//
				// Debug MSG
				szMsg="YdDeleComm::jmsTargetQSender() Qname=["+szYdJMSQName+"]";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
				
				//
				// 개발계 서버로 메시지 송신
				// Server의 Load Balancing기능에 의존함 
				this.queueSendMsg(szYdJMSQName, tcRecord, JMS_SVR_IP1, JMS_SVR_PORT);
				
				break;
				
			case 3:
				propertyService = PropertyService.getInstance();
				szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");	// YD_MDB_QUEUE
				//
				// Debug MSG
				szMsg="YdDeleComm::jmsTargetQSender() Qname=["+szYdJMSQName+"]";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
				
				//
				// 테스트계 서버로 메시지 송신
				// Server의 Load Balancing기능에 의존함 
				this.queueSendMsg(szYdJMSQName, tcRecord, JMS_SVR_IP2, JMS_SVR_PORT);
				
				break;
			
			} // end of switch()
			
			
			switch(nTarget){
			case 1:
				szMsg="YD JMS Queue Local Send success \n"
					 +"(TargetSystem=[Local], TC Code="+szTcCode+")";
				break;
				
			case 2:
				szMsg="YD JMS Queue 개발계 Send success \n"
					 +"(TargetSystem=["+JMS_SVR_IP1+"], TC Code="+szTcCode+")";
				break;
				
			case 3:
				szMsg="YD JMS Queue 테스트계 Send success \n"
					 +"(TargetSystem=["+JMS_SVR_IP2+"], TC Code="+szTcCode+")";
				break;
			
			default:
				szMsg="YD JMS Queue Send Fail";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return -3;
			
			} // end of swtich()
			
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);
			
			
		} catch(Exception e){
			
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			return -1;
		}
		
		return 0;
		
	} // end of jmsTargetQSnder()	
	
	
	
	
	
	
	/**
	 * 큐네임과 Record값을 받아서 YD자체 Queue(jms/YD_MDB_QUEUE)로 송신한다.
	 * @param strQueueName
	 * @param inRecord
	 * @return int
	 */
	private int queueSendMsg(String szQueueName, JDTORecord tcRecord, String szIp, int nPort){
		//
		// nTarget ==1 : Local
		// nTarget ==2 : Remote
		//
		
		String szMsg="";
		String szMethodName = "queueSendMsg";
		
	
		
		Properties h =null;
		Context ctx =null;
		QueueConnectionFactory factory =null;
		Queue queue =null;
		QueueConnection con =null;
		QueueSession session =null;
		QueueSender qsender =null;
		ObjectMessage message =null;
		
		try{
			
			h = new Properties();
			h.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
			h.put(Context.PROVIDER_URL, "t3://"+szIp+":"+nPort);
			if( !szIp.equals("Localhost")){
				h.put(Context.SECURITY_PRINCIPAL, "weblogic");
				h.put(Context.SECURITY_CREDENTIALS, "dlfrhkswpcjf");    
			}    
			ctx = new InitialContext(h);
			factory = (QueueConnectionFactory)ctx.lookup(JMS_CONNECTION_FACTORY);
			queue = (Queue)ctx.lookup(szQueueName);
			con = factory.createQueueConnection();
			session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			qsender = session.createSender(queue);

			
			message =session.createObjectMessage();
			message.setObject(tcRecord);

			
			qsender.send(message);
				
			con.close();

			//
			// 메시지 로깅
			// saveSndMsg(msgRecord);	// 메시지정보관리 Table에 저장
			
			
		} catch(Exception e){
			
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return -1;
		}
		
		return 0;
		
	} // end of queueSendMsg()	
	
	
	
	
	
	
	/**
	 * Remote EAI Http-Send 1
 	 * @param msgRecord
	 * @return
	 * @throws Exception
	 */
	public int httpSnder(JDTORecord msgRecord) {

		String szMsg="";
		String szMethodName = "httpSnder";
		String szTargetUrl="";
		String szTcCode="";
		int nRtc=0;
		
		HttpClient httpClt =null;
		PostMethod postMethod=null;

		httpClt =new HttpClient();
		
		try {			
			
			//
			// TC Code Check
			szTcCode =ymCommonUtil.getTcCode(msgRecord);
			if( szTcCode==null || szTcCode.equals("")){
				szMsg="TC Code("+szTcCode+") Error";
				return -2;
			}
			
			
			//
			// Make Target EAI URL 
			szTargetUrl =makeREaiUrl(szTcCode);			
			
			// 
			// Make PostMethod 
			postMethod =new PostMethod(szTargetUrl);			
			makePostMsg(msgRecord, postMethod);

			
			// 
			// Message Send
			//
			//httpClt =new HttpClient();
			nRtc =httpClt.executeMethod(postMethod);			
			if( nRtc<=0){
				szMsg="HttpClient.executeMethod Error (ErrCode:"+nRtc+")";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return -3;
			}

			
		
			szMsg="Remote EAI Http 송신 완료 [MSG_ID:"+szTcCode+"]";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);
			
			//
			// Debug Msg
			ymCommonUtil.disyRec(msgRecord);
			

		} catch(Exception e) {
			szMsg = "Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);

			return -1;
		}

		return 0;
		
	} // end of httpSnder()
	
	
	
	
	
	/**
	 * Remote EAI Http-Send 2
	 * (cm의 HttpClient를 사용하여 HttpClient Send)
	 * @param msgRecord
	 * @return
	 * @throws Exception
	 */
	public int remoteEaiSnder(JDTORecord msgRecord) {

		String szMsg="";
		String szMethodName = "remoteEaiSnder";
		String szTcCode="";
		String szEaiLogID="";
		
		EAIHttpSender eaiHttpSender =null;
		
		try {			
			
			//
			// TC Code Check
			szTcCode =ymCommonUtil.getTcCode(msgRecord);
			if( szTcCode==null || szTcCode.equals("")){
				szMsg="TC Code("+szTcCode+") Error";
				return -2;
			}
			

			
			// 
			// Message Send
			//
			eaiHttpSender =new EAIHttpSender();
			eaiHttpSender.initService(EAIHttpSender.issnd);
			szEaiLogID =eaiHttpSender.send(msgRecord);
			
					
			szMsg="Remote EAI Http 송신 완료 [TC_CODE : "+szTcCode+"]\n"
			     +"                         [ 송신ID : "+szEaiLogID+"]";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);
			
			//
			// Debug Msg
			ymCommonUtil.disyRec(msgRecord);
			

		} catch(Exception e) {
			szMsg = "Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);

			return -1;
		}

		return 0;
		
	} // end of remoteEaiSnder()
	
	
	
	
	
	/**
	 * EAI Server 시스템으로 직접 TCP/IP Socket 전송
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public int socketSender(String szRcvMsg) {

		String szMsg="";
		String szMethodName = "socketSender";
		

		Socket socket = null;
		OutputStream out = null;

		try {
			
			socket =new Socket(EAI_SVR_IP, EAI_SVR_PORT);			
			
			out =socket.getOutputStream ();
			out.write(szRcvMsg.getBytes());
			out.flush();
			socket.close();
			

			
			//
			// 송신 메시지 로깅
			// String szTcCode = szRcvMsg.substring(0, 9);
			// saveSndMsg(szTcCode, szRcvMsg);
			
		
			szMsg="EAI 송신 완료  Send MSG : [" + szRcvMsg +"]";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 3);
			
			

		} catch(Exception e) {
			szMsg = "Exception Error : "+e.getMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			if( socket!=null)
				try {
					socket.close();
				} catch (IOException e1) {
					szMsg = "IOException Error : "+e.getMessage();
					ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
					e1.printStackTrace();
				}
			return -1;
		}

		return 0;
		
	} // end of socketSender()
	
	
	
	
	/**
	 * JNDI에 등록 된 Facade의 Method 호출
	 *
	 * @param szFaName
	 * @param inRecord
	 * @return int
	 * 
	 */
	public int facadeSender(JDTORecord inRecord) {
		
				
		EJBConnector ejbCon = null;
		String szMethodName = "facadeSender";
		String szMsg="";
		String szFaName="";
		String szOpName="";
		String szTcCode="";
		
		try {
			szFaName=inRecord.getFieldString("TGT_FACADE");
			szOpName=inRecord.getFieldString("TGT_METHOD");
			szTcCode=inRecord.getFieldString("JMS_TC_CD");
		
			if(szFaName==null || szFaName.equals("") ||
			   szOpName==null || szOpName.equals("") ||
			   szTcCode==null || szTcCode.equals("")    ){
				
				szMsg="FacadeName=["+szFaName+"], "
				     +"MethodName=["+szOpName+"], "
				     +"TcCode=["+szTcCode+"] Error";
				ymCommonUtil.putLog(szSessionName, szFaName, szMsg, 1);
			
				return -2;
			}
			
			
			
			inRecord =ymCommonUtil.delRecKey(inRecord,"TGT_FACADE");
			inRecord =ymCommonUtil.delRecKey(inRecord,"TGT_METHOD");
			
			
			// 공통재료 변경 정보 등록 요청
			ejbCon = new EJBConnector("default", this);
			ejbCon.trx(szFaName, szOpName, inRecord);

			
			//
			// 메시지 로깅
			// saveSndMsg(inRecord);	// 메시지정보관리 Table에 저장
			
			
			szMsg="FacadeName=["+szFaName+"], "
			     +"MethodName=["+szOpName+"], "
			     +"TcCode=["+szTcCode+"] 호출 완료";
			ymCommonUtil.putLog(szSessionName, szFaName, szMsg, 1);
			
			
			
		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return -1;
			
		} // end of try catch
		
		return 0;
	
	} // end of callFaDelegate()
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 *  L2 EAI MSG Make
	 *  L2 시스템으로 송신 할 Record 데이터를 L2 EAI용으로 변환
	 *  @param msgRecord, eaiRecord
	 *  @return nKeyCnt
	 *  @ehrows Exception
	 */
	int makeL2EaiRecord(JDTORecord msgRecord, JDTORecord eaiRecord){
		
		String szMsg="";
		String szMethodName="makeL2EaiRecord";
		
		String szTcCode=null;
		String [] szRecKey=null;
		int nKeyCnt=0;
		
		String szEaiStrmMsg=null;
		
		
		try{

			szTcCode=ymCommonUtil.getTcCode(msgRecord);
			if( szTcCode==null || szTcCode.equals("")){
				szMsg="TC Code("+szTcCode+") Error";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return -2;
			}

			szRecKey =ymCommonUtil.getRecKey(msgRecord);
			nKeyCnt=szRecKey.length;

			if( nKeyCnt<=0){
				szMsg="msgRecord is empty Error";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);

				return -3;
			}

			for( int i=0;i<nKeyCnt;i++){
				if( szEaiStrmMsg==null)
					szEaiStrmMsg =msgRecord.getFieldString(szRecKey[i]);
				else
					szEaiStrmMsg+=msgRecord.getFieldString(szRecKey[i]);
			} // end of for()


			// EAI TC CODE Set
			eaiRecord.setField("JMS_TC_CD", szTcCode);

			// EAI MSG Set
			eaiRecord.setField("JMS_TC_MESSAGE", szEaiStrmMsg);

		
		} catch(Exception e){
			szMsg=szMethodName+ " Exception Error : "+ e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			return -1;
			
		} // end of try-catch
		
		
		//
		// msgRecord의 키 갯수 Return
		return nKeyCnt;
		
		
	} // end of makeL2EaiRecord()
	
	
	
	
	
	/**
	 * httpSender를 위한 Remote EAI URL Maker
	 * @param szTcCode
	 * @return EAI URL
	 */
	private String makeREaiUrl(String szTcCode){

		
		String szREaiUrl = "";
		
		try {
			szREaiUrl = PropertyService.getInstance().getProperty("eai.properties", "hsEaiUrl") + szTcCode;
			if(szREaiUrl==null || szREaiUrl.equals(""))
				szREaiUrl = EAI_REMOTE_URL + szTcCode;
			
		} catch (Exception e) {
			szREaiUrl = EAI_REMOTE_URL + szTcCode;
		}
		
		return szREaiUrl;
	
	} // end of makeREaiUrl()
	
	
	
	
	
	/**
	 *  httpSender를위한 PostMsg Make
	 *  @param msgRecord, eaiRecord
	 *  @return nKeyCnt
	 *  @ehrows Exception
	 */
	int makePostMsg(JDTORecord msgRecord, PostMethod postMethod){
		
		String szMsg="";
		String szMethodName="makePostMsg";
		
		String []szaKeys =null;
		int nKeyCnt=0;
		
		
		try{
			
			szaKeys=ymCommonUtil.getRecKey(msgRecord);
			nKeyCnt=szaKeys.length;
			if( nKeyCnt<=0){
				szMsg="Record Key Cnt is Empty ";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return -2;
			}
			
			for(int i=0;i<nKeyCnt;i++){
				
				postMethod.addParameter(szaKeys[i], msgRecord.getFieldString(szaKeys[i]));
				
			} // end of for()
			
		}catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return -1;
		}
		
		
		
		return nKeyCnt;
		
		
	} // end of makePostMsg()
	

	
	


	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                     일관제철소정보관리시스템-야드관리
	//              		    Delegate Send Bean
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	public static void main(String[] args){
		YmDeleComm im =new YmDeleComm();
		JDTORecord testRec =JDTORecordFactory.getInstance().create();
		
		try {
			testRec.setField("JMS_TC_CD","CTYDJ021 ");
			
			
			im.jmsQSnder("jms/YD_MDB_QUEUE", testRec);
			
		} catch (Exception e) {
			System.out.println("Exception Error : "+e.getMessage());
			e.printStackTrace();
		}
		
		
	} // end of testMain()
	
	
	
	
  //---------------------------------------------------------------------------	
} // end of class YdDeleComm
