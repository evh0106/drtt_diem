/*
 * @(#) 2후판정정야드 Delegate 공통 클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2013/04/03
 *
 * @description		2후판정정야드 Delegate 공통 클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2013/04/03   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.delegate;

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

import com.inisteel.cim.yd.common.dao.YdCommDAO;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdCommonUtils;


public class JPlateYdDeleComm {

	private static String 	szSessionName = JPlateYdDeleComm.class.getName();
	private YdCommDAO commDao = new YdCommDAO();
	private JPlateYdUtils 	ydUtils 					= new JPlateYdUtils();

	public static final String 	EAI_SVR_IP  			= "10.216.133.28";
//  public static final int  	EAI_SVR_PORT  			= 6806;
	public static final String 	EAI_REMOTE_URL			= "http://10.216.130.42:8080/dmsnd/";

	//로컬에서만 화면시물레이터에서 테스트시 사용하는 개발계, 테스트계 ip 정보
	public static final String 	JMS_SVR_IP1				= "10.216.130.45";	// 개발계
	public static final String 	JMS_SVR_IP2				= "10.216.133.163";	// 테스트계
	public static final int	 	JMS_SVR_PORT 			= 7100;
	public static final String 	JMS_CONNECTION_FACTORY	= "LJMSConnectionFactory";

	/**
	 * 큐네임과 Record값을 받아서 CM의 JmsQueueSender를 이용하여 해당 큐에 송신한다.
	 * @param strQueueName
	 *        내부JMS : jms/XX_MDB_QUEUE
	 *        EAI JMS : jms/YD_EAI_QUEUE
	 * @param inRecord
	 * @return int
	 */
	public int jmsQSnder(String szQueueName, JDTORecord tcRecord){

		String szMsg			= "";
		String szMethodName 	= "jmsQSnder";
		String szTargetQName	= szQueueName;
		String szTcCode			= "";

		JDTORecord eaiL2Record	= null;
		int nRtc				= 0;

		JmsQueueSender jmsQSnder =new JmsQueueSender();
		PropertyService propertyService = null;

		try{

			// JMS : JMS_TC_CD, L2 EAI : MSG_ID
			szTcCode = ydUtils.getTcCode(tcRecord);
			if( szTcCode==null || "".equals(szTcCode)){
				szMsg="TC COde() Error ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -2;
			}
			// EAI Send Check and Make EAI Record
			propertyService = PropertyService.getInstance();

			// L2송신을 위한 YD EAI QUEUE (jms/YD_EAI_QUEUE)인 경우
			// L2 EAI 송신 용 레코드로 변환 후 송신
			if( szQueueName.equals( propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE"))){

				// 송신용 EAI MSg Record 생성
				eaiL2Record = JDTORecordFactory.getInstance().create();
				nRtc 		= makeL2EaiRecord(tcRecord, eaiL2Record);
				if (nRtc<=0){
					szMsg="EAI MSG Record Make Error (ErrCode:"+nRtc+")";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
					return -2;
				}
				tcRecord =eaiL2Record;
				
				if("Y".equals(commDao.getWebMothodYn())) {
					//---------------------------------------------------------------------------------------------
					//WebMethod EAI 방식 변경에 따른 BRE에서 큐명 호출 2019.08.20
					//---------------------------------------------------------------------------------------------
					if ("YD".equals(szTcCode.substring(2, 4))) {
						if ("YDYDJ031".equals(szTcCode)) {
							szQueueName = propertyService.getProperty("common.properties", "jms.queue.YDH_MDB_QUEUE"); //후판압연지시 제품창고 송신
						}else{
							szQueueName = propertyService.getProperty("common.properties", "jms.queue.YDB_MDB_QUEUE"); //일단 연주야드 내부Queue	
						}
					} else {
						if ("L".equals(szTcCode.substring(4, 5))) {
							
							szQueueName = jmsQSnder.getQueueName("YD", szTcCode);
							
							if("".equals(szQueueName)){
								szQueueName = propertyService.getProperty("common.properties","jms.queue.YD_EAI_QUEUE"); 
							}else{
								szQueueName = propertyService.getProperty("common.properties","jms.queue."+szQueueName);
							}
									
						} else {
							//내부 JMS Queue명
							szQueueName = propertyService.getProperty("common.properties", "jms.queue." + szTcCode.substring(2, 4) + "_MDB_QUEUE"); //내부 JMS Queue명
						}
					}
					//---------------------------------------------------------------------------------------------									
				}
			} // end of if()

			//출하http ->jms
			if ("DM".equals(szTcCode.substring(2,4))){
				tcRecord.setField("JMS_TC_CD", szTcCode);
			}

			jmsQSnder.initQueueService(szQueueName);
			jmsQSnder.send(tcRecord);

			szMsg="JMS Queue Send success (QueueName="+szTargetQName+", TC Code="+szTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e){

			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
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
		String szWkGp = "";
		PropertyService propertyService = null;

		try {
			szTcCode =ydUtils.getTcCode(tcRecord);
			if (szTcCode==null || "".equals(szTcCode)){
				szMsg="TC COde() Error ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -2;
			}

			szWkGp = szTcCode.substring(2, 4);

			switch(nTarget){
				case 1:
					szYdJMSQName="jms/YD_MDB_QUEUE";
					this.queueSendMsg(szYdJMSQName, tcRecord, "Localhost", 9100);
					break;

				case 2:
					propertyService = PropertyService.getInstance();
					szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue." + szWkGp + "_MDB_QUEUE");	// YD_MDB_QUEUE
					//
					// Debug MSG
					szMsg="YdDeleComm::jmsTargetQSender() Qname=["+szYdJMSQName+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

					//
					// 개발계 서버로 메시지 송신
					// Server의 Load Balancing기능에 의존함
					this.queueSendMsg(szYdJMSQName, tcRecord, JMS_SVR_IP1, JMS_SVR_PORT);

					break;

				case 3:
					propertyService = PropertyService.getInstance();
					szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue." + szWkGp + "_MDB_QUEUE");	// YD_MDB_QUEUE
					//
					// Debug MSG
					szMsg="YdDeleComm::jmsTargetQSender() Qname=["+szYdJMSQName+"]";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

					//
					// 테스트계 서버로 메시지 송신
					// Server의 Load Balancing기능에 의존함
					this.queueSendMsg(szYdJMSQName, tcRecord, JMS_SVR_IP2, JMS_SVR_PORT);

					break;

				default:
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
					ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
					return -3;

			} // end of swtich()

			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);


		} catch(Exception e){

			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

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
			if( !"Localhost".equals(szIp)){
				h.put(Context.SECURITY_PRINCIPAL, "weblogic");
				h.put(Context.SECURITY_CREDENTIALS, "dlfrhkswpcjf");
			}
			ctx = new InitialContext(h);
			factory = (QueueConnectionFactory)ctx.lookup(JMS_CONNECTION_FACTORY);
			queue = (Queue)ctx.lookup(szQueueName);
			con = factory.createQueueConnection();
			session = con.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			qsender = session.createSender(queue);

			message = session.createObjectMessage();
			message.setObject(tcRecord);

			qsender.send(message);

			con.close();

			//
			// 메시지 로깅
			// saveSndMsg(msgRecord);	// 메시지정보관리 Table에 저장


		} catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
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
		String szOperationName = "Remote EAI Http-Send 1";
		String szTargetUrl="";
		String szTcCode="";
		int nRtc=0;

		HttpClient httpClt =null;
		PostMethod postMethod=null;

		httpClt =new HttpClient();

		try {

			//
			// TC Code Check
			szTcCode =ydUtils.getTcCode(msgRecord);
			if( szTcCode==null || "".equals(szTcCode)){
				szMsg = "TC Code("+szTcCode+") Error";
				return -2;
			}

			//
			// Make Target EAI URL
			szTargetUrl =makeREaiUrl(szTcCode);

			//
			// Make PostMethod
			postMethod = new PostMethod(szTargetUrl);
			makePostMsg(msgRecord, postMethod);

			//
			// Message Send
			//
			//httpClt =new HttpClient();
			nRtc = httpClt.executeMethod(postMethod);
			if( nRtc<=0){
				szMsg="HttpClient.executeMethod Error (ErrCode:"+nRtc+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -3;
			}

			szMsg="Remote EAI Http 송신 완료 [MSG_ID:"+szTcCode+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//
			// Debug Msg
			ydUtils.displayRecord(szOperationName, msgRecord);


		} catch(Exception e) {
			szMsg = "Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

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

		String szMsg			= "";
		String szMethodName 	= "remoteEaiSnder";
//		String szOperationName 	= "Remote EAI Http-Send 2";
		String szTcCode			= "";
		String szEaiLogID		= "";

		EAIHttpSender eaiHttpSender = null;

		try {
			// TC Code Check
			szTcCode =ydUtils.getTcCode(msgRecord);
			if( szTcCode==null || "".equals(szTcCode)){
				szMsg="TC Code("+szTcCode+") Error";
				return -2;
			}
			// Message Send
			eaiHttpSender = new EAIHttpSender();
			eaiHttpSender.initService(EAIHttpSender.issnd);
			szEaiLogID = eaiHttpSender.send(msgRecord);

			szMsg="Remote EAI Http 송신 완료 [TC_CODE : "+szTcCode+"]\n"
			     +"                         [ 송신ID : "+szEaiLogID+"]";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch(Exception e) {
			szMsg = "Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
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
		String szMsg = "";
		String szMethodName = "socketSender";
		Socket socket = null;
		OutputStream out = null;
		int nPort = 0;

		szMsg = "socketSender() In : " + szRcvMsg;
		ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

		try {
			String szTcCode = szRcvMsg.substring(0, 9);
			nPort = JPlateYdCommonUtils.getPortByUsingCode(szTcCode);
			szMsg = "Connect IP : " + EAI_SVR_IP + " PORT : " + nPort;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

			socket = new Socket(EAI_SVR_IP, nPort);
			if(socket != null){
				out = socket.getOutputStream ();
				out.write(szRcvMsg.getBytes());
				out.flush();
				socket.close();
				szMsg = "EAI 송신 완료  Send MSG : [" + szRcvMsg +"]";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
//			} else {
//				szMsg="소켓 연결이 되지 않았습니다. " + EAI_SVR_IP + "(" + nPort + ")";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

		} catch(Exception e) {
			szMsg = "Exception Error : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
			if( socket!=null) {
				try {
					socket.close();
				} catch (IOException e1) {
					szMsg = "IOException Error : " + e.getMessage();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				}
			}
			return -1;
		}

		szMsg = "socketSender() Out";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.DEBUG);

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

			if(szFaName==null || "".equals(szFaName) ||
			   szOpName==null || "".equals(szOpName) ||
			   szTcCode==null || "".equals(szTcCode)){

				szMsg="FacadeName=["+szFaName+"], "
				     +"MethodName=["+szOpName+"], "
				     +"TcCode=["+szTcCode+"] Error";
				ydUtils.putLog(szSessionName, szFaName, szMsg, JPlateYdConst.ERROR);

				return -2;
			}

			inRecord =ydUtils.delRecKey(inRecord,"TGT_FACADE");
			inRecord =ydUtils.delRecKey(inRecord,"TGT_METHOD");

			// 공통재료 변경 정보 등록 요청
			ejbCon = new EJBConnector("default", this);
			ejbCon.trx(szFaName, szOpName, inRecord);

			//
			// 메시지 로깅
			// saveSndMsg(inRecord);	// 메시지정보관리 Table에 저장

			szMsg="FacadeName=["+szFaName+"], "
			     +"MethodName=["+szOpName+"], "
			     +"TcCode=["+szTcCode+"] 호출 완료";
			ydUtils.putLog(szSessionName, szFaName, szMsg, JPlateYdConst.ERROR);



		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
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
	// 2012.11.14 김현우 JPlateYdDelegate.java 위치변경으로 public 으로 변경
	public int makeL2EaiRecord(JDTORecord msgRecord, JDTORecord eaiRecord){

		String szMsg="";
		String szMethodName="makeL2EaiRecord";

		String szTcCode=null;
		String [] szRecKey=null;
		int nKeyCnt=0;

		String szEaiStrmMsg=null;


		try{

			szTcCode=ydUtils.getTcCode(msgRecord);
			if( szTcCode==null || "".equals(szTcCode)){
				szMsg="TC Code("+szTcCode+") Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -2;
			}

			szRecKey =ydUtils.getRecKey(msgRecord);
			nKeyCnt=szRecKey.length;

			if( nKeyCnt<=0){
				szMsg="msgRecord is empty Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

				return -3;
			}

			for( int i=0;i<nKeyCnt;i++){
				if( szEaiStrmMsg==null) {
					szEaiStrmMsg =msgRecord.getFieldString(szRecKey[i]);
				} else {
					szEaiStrmMsg+=msgRecord.getFieldString(szRecKey[i]);
				}
			} // end of for()

			// EAI TC CODE Set
			eaiRecord.setField("JMS_TC_CD", szTcCode);

			// EAI MSG Set
			eaiRecord.setField("JMS_TC_MESSAGE", szEaiStrmMsg);

		} catch(Exception e){
			szMsg=szMethodName+ " Exception Error : "+ e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);

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
			if(szREaiUrl==null || "".equals(szREaiUrl)) {
				szREaiUrl = EAI_REMOTE_URL + szTcCode;
			}

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
	public int makePostMsg(JDTORecord msgRecord, PostMethod postMethod){

		String szMsg="";
		String szMethodName="makePostMsg";

		String []szaKeys =null;
		int nKeyCnt=0;


		try{

			szaKeys=ydUtils.getRecKey(msgRecord);
			nKeyCnt=szaKeys.length;
			if (nKeyCnt<=0) {
				szMsg="Record Key Cnt is Empty ";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
				return -2;
			}

			for(int i=0;i<nKeyCnt;i++) {

				postMethod.addParameter(szaKeys[i], msgRecord.getFieldString(szaKeys[i]));

			} // end of for()

		}catch(Exception e){
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, JPlateYdConst.ERROR);
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
		JPlateYdDeleComm im = new JPlateYdDeleComm();
		JDTORecord testRec = JDTORecordFactory.getInstance().create();

		try {
			testRec.setField("JMS_TC_CD","CTYDJ021 ");

			im.jmsQSnder("jms/YD_MDB_QUEUE", testRec);

		} catch (Exception e) {
		//	System.out.println("Exception Error : "+e.getMessage());
		//	e.printStackTrace();
		}


	} // end of testMain()

  //---------------------------------------------------------------------------
} // end of class YdDeleComm
