/*
 * @(#) 2후판정정야드 Delegate 클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		2후판정정야드 Delegate 클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.delegate;

import java.net.InetAddress;

import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.jms.JmsQueueSender;

import com.inisteel.cim.yd.common.dao.YdCommDAO;
import com.inisteel.cim.yd.common.delegate.YdDeleComm;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdTcConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdTcMgr;

/**
 * JMS Q, Remote EAI, L2 EAI, Facade Call
 * @param inRecord
 * @throws
 */
public class JPlateYdDelegate{

	private JPlateYdUtils 	ydUtils 	= new JPlateYdUtils();
	private JPlateYdTcConst ydTcConst 	= new JPlateYdTcConst();
	private YdCommDAO commDao = new YdCommDAO();
	private YdDeleComm deleComm = new YdDeleComm();
	private JPlateYdTcMgr tcConstMgr = new JPlateYdTcMgr();

	private final String SZ_SESSION_NAME = this.getClass().getName();
	private final String SZ_IP_DEV_SYS1  = "10.216.133.204";// 개발계
	private final String SZ_IP_DEV_SYS2  = "10.216.133.207";// 개발계TM
	private final String SZ_IP_DEV_SYS3  = "10.216.132.204";// 개발계 NEW
	private final String SZ_IP_DEV_SYS4  = "10.216.132.207";// 개발계TM NEW	
	private final String SZ_IP_TST_SYS1  = "10.216.133.116";//"10.216.133.15";	// 운영계1(조업1)
	private final String SZ_IP_TST_SYS2  = "10.216.133.117";//"10.216.133.25";	// 운영계2(조업2)
	private final String SZ_IP_TST_SYS3  = "10.216.133.163";	// 운영계1TM(물류1)
	private final String SZ_IP_TST_SYS4  = "10.216.133.164";	// 운영계2TM(물류2)
	private final String SZ_IP_TST_SYS5  = "10.216.133.167";	// 운영계1(공정1)
	private final String SZ_IP_TST_SYS6  = "10.216.133.168";	// 운영계2(공정2)

	/**
	 * JMS Q, Remote EAI, L2 EAI, Facade Call
	 * @param inRecord
	 * @throws
	 */
	public String sendMsg(JDTORecord msgRecord) throws DAOException {

		//
		// YD Delegate 송신 Main
		// 수신 한 msgRecord의 TCCode를 분석하여
		// 내부(J), RemoteEAI(R), L2EAI(L)를 판단 한 후
		// 대상 메소드를 통해서 송신한다.
		//

		String 	szMsg			= "";
		String 	szMethodName 	= "sendMsg";
		String 	szTcCode 		= "";
		String 	szQueueName 	= "";
		String 	szWkGp			= "";
		String 	szYdEaiQName 	= "";
		String 	szYdName     	= "";
		String 	szBufferTc   	= "";
		int 	nRtc			= 0;
		int 	nTcKind			= 0;
		boolean	isLocal			= false;

		// 송신 용 TC (Maked TC)
		JDTORecord    tcRecord 	= null;
		JDTORecordSet tcRecSet 	= null;

		PropertyService propertyService = null;

		JmsQueueSender jmsQSnder = null;

		try {

			szTcCode = ydUtils.getTcCode(msgRecord);

			szBufferTc = StringHelper.evl(msgRecord.getFieldString(JPlateYdConst.BUFFER_TC_CD),"");

			szMsg = "Delegate 송신 요청 수신 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBufferTc;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if( "YDY7L001".equals(szTcCode) ||			// 저장위치제원
				"YDY7L002".equals(szTcCode) ||			// 저장품제원
				"YDY7L004".equals(szTcCode) ||			// 크레인작업지시
				"YDY7L005".equals(szTcCode) ||			// 크레인작업실적응답
				"YDPPJ011".equals(szTcCode) ||			// 저장위치변경정보
				"YDS1L005".equals(szTcCode)) {			// Book-In/Book-Out실적

				if("".equals(szBufferTc)){
				//	msgRecord.setField("JMS_TC_CD", JPlateYdConst.YDYDJ701);
					msgRecord.setField("JMS_TC_CD", szTcCode);
					msgRecord.setField(JPlateYdConst.BUFFER_TC_CD, szTcCode);
				}else{
					msgRecord.setField(JPlateYdConst.BUFFER_TC_CD, "");
				}
			}

			// 1:JMS, 2:Remote EAI, 3:L2 EAI, 9:Facade
			nTcKind = ydTcConst.chkTcType(szTcCode);

			// TC코드가 맞지 않을때
			if (nTcKind <= 0) {
				szMsg = "Unknown TC Code("+szTcCode+") Error (chkTcType="+nTcKind+")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
			}

			// 전송용 JDTORecord 생성
			tcRecSet = JDTORecordFactory.getInstance().createRecordSet("JPlateYdDelegate");

			// nRtc>0 : tcRecSet의 Record Count
			nRtc = tcConstMgr.makeTc(msgRecord, tcRecSet);

			if (nRtc<=0){
				szMsg=" TC("+szTcCode+") Data Make Error :: " + nRtc;
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
			}

			propertyService = PropertyService.getInstance();

			// YD_MDB_QUEUE
//			szYdJmsQName = propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			// YD_EAI_QUEUE
			szYdEaiQName = propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");

			InetAddress ipAddr = InetAddress.getLocalHost();
			String szIpAddr = ipAddr.getHostAddress();
			if ((!SZ_IP_DEV_SYS1.equals(szIpAddr)) &&
				(!SZ_IP_DEV_SYS2.equals(szIpAddr)) &&
				(!SZ_IP_DEV_SYS3.equals(szIpAddr)) &&
				(!SZ_IP_DEV_SYS4.equals(szIpAddr)) &&				
				(!SZ_IP_TST_SYS1.equals(szIpAddr)) &&
				(!SZ_IP_TST_SYS2.equals(szIpAddr)) &&
				(!SZ_IP_TST_SYS3.equals(szIpAddr)) &&
				(!SZ_IP_TST_SYS4.equals(szIpAddr)) &&
				(!SZ_IP_TST_SYS5.equals(szIpAddr)) &&
				(!SZ_IP_TST_SYS6.equals(szIpAddr)) ) {
				isLocal = true;
			}

			//
			// DEBUG MSG
			szMsg="[DEBUG] 대상Queue  : szIpAddr >>>> " + szIpAddr + " >>>> " + isLocal;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//개발자 로컬인 경우에만 내부 인터페이스를 로컬처리 한다.
			if ("YDYD".equals(szTcCode.substring(0, 4)) &&
				((!SZ_IP_DEV_SYS1.equals(szIpAddr)) &&
				 (!SZ_IP_DEV_SYS2.equals(szIpAddr)) &&
				 (!SZ_IP_TST_SYS1.equals(szIpAddr)) &&
				 (!SZ_IP_TST_SYS2.equals(szIpAddr)) &&
				 (!SZ_IP_TST_SYS3.equals(szIpAddr)) &&
				 (!SZ_IP_TST_SYS4.equals(szIpAddr)) &&
				 (!SZ_IP_TST_SYS5.equals(szIpAddr)) &&
				 (!SZ_IP_TST_SYS6.equals(szIpAddr)) 
				)){

				lclSndMsg(msgRecord);

			}else{

				// 로컬일경우 ... JMS , EAI 호출 안되도록 처리
				if (isLocal) {
					szMsg = ">>>> LOCAL 에서  송신 SKIP >>>> nTcKind :: " + nTcKind;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					return JPlateYdConst.RETN_CD_SUCCESS;
				}

				switch(nTcKind){

					// EnQueue
					case 1:		// JMS Queue 송신

						if("YDYD".equals(szTcCode.substring(0, 4))){

							//YDYD전문으로 야드구분 추출
							szYdName=(String)ydTcConst.rcvTcYdMap.get(szTcCode);
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, "["+szTcCode+"]TC해당야드:"+szYdName, JPlateYdConst.DEBUG);

							szBufferTc =  StringHelper.evl(msgRecord.getFieldString(JPlateYdConst.BUFFER_TC_CD),"");

							if(!"".equals(szBufferTc)){	//BUFFER
								szWkGp = "YDG";
							} else {
								szWkGp = szTcCode.substring(2, 4);
							}

						} else {
							szWkGp = szTcCode.substring(2, 4);
						}

						szQueueName = propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");
						// DEBUG MSG
						szMsg="[DEBUG] 대상Queue  : ["+szQueueName+"] "+szIpAddr;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

						jmsQSnder = new JmsQueueSender();
						jmsQSnder.initQueueService(szQueueName);

						JDTORecord[] sndMsgs1 = new JDTORecord[nRtc];

						// Internal Queue Send
						for(int ii = 0; ii < nRtc; ii++){
							tcRecord =tcRecSet.getRecord(ii);
							// JMS : JMS_TC_CD, L2 EAI : MSG_ID
							szTcCode =ydUtils.getTcCode(tcRecord);
							//출하http ->jms
							if("DM".equals(szTcCode.substring(2,4))){
								tcRecord.setField("JMS_TC_CD", szTcCode);
							}
							sndMsgs1[ii] =  tcRecord;
						} // end of for()

						jmsQSnder.send(sndMsgs1);
						break;	// end of case 1

					case 2:		// 리모트 EAI 송신

						// Remote EAI Send
						for(int ii=0; ii<nRtc; ii++){

							tcRecord =tcRecSet.getRecord(ii);
							//deleComm.httpSnder(tcRecord);
							deleComm.remoteEaiSnder(tcRecord);

						} // end of for()
						break; // end of case 2

					case 3:		// L2 EAI 송신

						// EAI Queue Send
						szQueueName = szYdEaiQName;

						jmsQSnder = new JmsQueueSender();
						
						if("Y".equals(commDao.getWebMothodYn())) {
							//---------------------------------------------------------------------------------------------
							//WebMethod EAI 방식 변경에 따른 BRE에서 큐명 호출 2019.08.20
							//---------------------------------------------------------------------------------------------
							szQueueName = jmsQSnder.getQueueName("YD", szTcCode);
							
							if("".equals(szQueueName)){
								szQueueName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");
							}else{
								szQueueName =propertyService.getProperty("common.properties", "jms.queue."+szQueueName);
							}
							//---------------------------------------------------------------------------------------------
						}
						jmsQSnder.initQueueService(szQueueName);

						JDTORecord eaiL2Record = null;

						JDTORecord[] sndMsgs3 = new JDTORecord[nRtc];

						for(int ii = 0; ii < nRtc; ii++){
							eaiL2Record = JDTORecordFactory.getInstance().create();
							tcRecord 	= tcRecSet.getRecord(ii);
							deleComm.makeL2EaiRecord(tcRecord, eaiL2Record);
							sndMsgs3[ii] = eaiL2Record;
						} // end of for()

						jmsQSnder.send(sndMsgs3);

						break; // end of case 3

					case 9:		// Facade Call
						//
						// Facade Call의 경우에는 수신 한 Record에서 FacadeName, MethodName을 발췌하여 송신
						nRtc = deleComm.facadeSender(msgRecord);
						if (nRtc<0) {

							String szErrMsg = "Remote Facade Call Fail";
							ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szErrMsg, JPlateYdConst.ERROR);
							return szErrMsg;
						}
						break; // end of case 3:

					default:

						szMsg = "Unknown TC Case : "+nTcKind;
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szMsg;

				} // end of switch()
			}
		} catch (Exception e) {
			szMsg = szMethodName + " Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_CD_FAILURE;
		} // end of try catch

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of  sendMsg()

	/**
	 * JMS Q, Remote EAI, L2 EAI, Facade Call
	 * @param inRecord
	 * @throws
	 */
	public String sendMsgNoMakeTc(JDTORecord msgRecord){

		//
		// YD Delegate 송신 Main
		// 수신 한 msgRecord의 TCCode를 분석하여
		// 내부(J), RemoteEAI(R), L2EAI(L)를 판단 한 후
		// 대상 메소드를 통해서 송신한다.
		//

		String szMsg		= "";
		String szMethodName = "sendMsgNoMakeTc";
		String szTcCode 	= "";
		String szQueueName 	= "";
		String szWkGp 		= "";
		String szYdJmsQName = "";
		String szYdEaiQName = "";

		int nRtc = 0;
		int nTcKind = 0;

		//
		// 송신 용 TC (Maked TC)
		//JDTORecord tcRecord =null;
		//JDTORecordSet tcRecSet =null;

		PropertyService propertyService = null;

		try{

			szTcCode = ydUtils.getTcCode(msgRecord);

			szMsg = "Delegate 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 1:JMS, 2:Remote EAI, 3:L2 EAI, 9:Facade
			nTcKind = ydTcConst.chkTcType(szTcCode);


			// TC코드가 맞지 않을때
			if(nTcKind <=0) {

				szMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
			}

			propertyService = PropertyService.getInstance();

			// YD_MDB_QUEUE
			szYdJmsQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			// YD_EAI_QUEUE
			szYdEaiQName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");

			//
			// DEBUG MSG
			szMsg="[DEBUG] 대상Queue  : ["+szYdJmsQName+"] ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			switch(nTcKind){

				//
				// EnQueue
				case 1:		// JMS Queue 송신

					szWkGp =szTcCode.substring(2, 4);
					szQueueName =propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");

					deleComm.jmsQSnder(szQueueName, msgRecord);

					break;	// end of case 1


				case 2:		// 리모트 EAI 송신

					deleComm.remoteEaiSnder(msgRecord);

					break; 	// end of case 2


				case 3:		// L2 EAI 송신

					//
					// EAI Queue Send
					szQueueName=szYdEaiQName;

					deleComm.jmsQSnder(szQueueName, msgRecord);

					break; 	// end of case 3


				case 9:		// Facade Call
					//
					// Facade Call의 경우에는 수신 한 Record에서 FacadeName, MethodName을 발췌하여 송신
					nRtc =deleComm.facadeSender(msgRecord);
					if(nRtc<0) {

						String szErrMsg = "Remote Facade Call Fail";
						ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szErrMsg, JPlateYdConst.ERROR);

						return szErrMsg;
					}

					break; // end of case 3:

				default:

					szMsg="Unknown TC Case : "+nTcKind;
					ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
					return szMsg;

			} // end of switch()

		} catch (Exception e) {

			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;

		} // end of try catch

		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of  sendMsgNoMakeTc()

	/**
	 * Application 환경이 서버(개발계/테스트계)인지를 판단하여
	 * 서버인 경우에는 서버로, 로컬인 경우에는 로컬로 메시지 전송
	 */
	public String msgSend(JDTORecord msgRecord){

		String szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;

		szRtnMsg = this.sendMsg(msgRecord);

		/* 2013.04.26 김현우 주석처리
		 * sendMsg를 호출하도록 보완
		 *
		String szMethodName = "msgSend";
		String szMsg		= "";

		try {

			InetAddress ipAddr = InetAddress.getLocalHost();
			String szIpAddr = ipAddr.getHostAddress();

			szMsg = szMethodName+" szIpAddr : "+szIpAddr;
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			if( (SZ_IP_DEV_SYS1.equals(szIpAddr)) ||
				(SZ_IP_DEV_SYS2.equals(szIpAddr)) ||
				(SZ_IP_TST_SYS1.equals(szIpAddr)) ||
				(SZ_IP_TST_SYS2.equals(szIpAddr)) ||
				(SZ_IP_TST_SYS3.equals(szIpAddr)) ||
				(SZ_IP_TST_SYS4.equals(szIpAddr)) ){

				// 개발계/테스트1,2 서버
				szRtnMsg = this.sendMsg(msgRecord);

			} else {

				// 개발계가 아닌 경우 (로컬)
				szRtnMsg = this.lclSndMsg(msgRecord);
			}

		} catch(Exception e) {

			szMsg = szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;

		} // end of try-catch
		*/

		return szRtnMsg;

	} // end of msgSend()


	/**
	 * local JMS Q송신을 위한 내부(Local) Queue 송신
	 *
	 */
	public String lclSndMsg(JDTORecord msgRecord){

		String szMsg		= "";
		String szMethodName = "lclSndMsg";
		String szTcCode 	= "";

		try{

			szTcCode = ydUtils.getTcCode(msgRecord);

			szMsg = "Delegate(Local) 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			int nTcKind = 0;

			// 1:JMS, 2:Remote, 3:L2, 9:Facade
			nTcKind = ydTcConst.chkTcType(szTcCode);

			// TC코드가 맞지 않을때
			if(nTcKind < 0) {

				szMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;
			}

			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 내부 JMs Queue에 송신한다.
			//
			deleComm.jmsTargetQSnder(msgRecord, 1);


		} catch (Exception e) {
			szMsg = szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		} // end of try catch

		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of lclSndMsg()

	/**
	 *  remote JMS Q를 위한 원격(개발계) Queue 송신
	 *
	 */
	public String rmtSndMsg(JDTORecord msgRecord){

		String szMsg 		= "";
		String szMethodName = "rmtSndMsg";
		String szTcCode 	= "";

		try{

			szTcCode = ydUtils.getTcCode(msgRecord);

			szMsg = "Delegate(Remote:개발계) 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 서버 JMs Queue에 송신한다.
			//
			deleComm.jmsTargetQSnder(msgRecord, 2);

		} catch (Exception e) {
			szMsg = szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		} // end of try catch

		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of rmtSndMsg()

	/**
	 *  remote JMS Q를 위한 원격(테스트계) Queue 송신
	 *
	 */
	public String tstSndMsg(JDTORecord msgRecord){

		String szMsg 		= "";
		String szMethodName = "tstSndMsg";
		String szTcCode 	= "";

		try{

			szTcCode = ydUtils.getTcCode(msgRecord);

			szMsg = "Delegate(Remote:테스트계) 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 서버 JMs Queue에 송신한다.
			//

			deleComm.jmsTargetQSnder(msgRecord, 3);

		} catch (Exception e) {
			szMsg = szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		} // end of try catch

		return JPlateYdConst.RETN_CD_SUCCESS;

	} // end of tstSndMsg()


	/** sndSms  EAI (SMS) SEND 공통
	 * @param   1. Message 내용, 2. TC코드
	 * @return  String  정상일때 "Y"
	 * @throws DAOException
	 */
	public String sndSms(String  message, String tccode) throws com.inisteel.cim.common.exception.DAOException {

		String szMsg			= "";
		String szMethodName 	= "sndSms";
		JmsQueueSender sender 	= null;
		String queueName 		= null;
		JDTORecord inRecord 	= null;
		PropertyService propertyService = null;
		String   flag = "N";
		try {
		    // 프로퍼티 서비스 인스턴스를 취득합니다.
			propertyService = PropertyService.getInstance();

			// 큐 명칭을 프로퍼티로부터 취득합니다. [[ EAI = jms.queue.SMS_EAI_QUEUE ]]
			queueName = propertyService.getProperty("common.properties", "jms.queue.SMS_EAI_QUEUE");

//			sender = new com.inisteel.cim.common.jms.JmsQueueSender();
			sender = new JmsQueueSender();
			// 큐에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName);
			/*
			 * 큐에 넣을 데이터를 생성합니다.
			 * 1. LABEL2 에서 EAI 수신 정보를 생산 통데로 ByPass 한다.
			 */
			inRecord = JDTORecordFactory.getInstance().create();
			//inRecord.setRecord(indo);
			inRecord.setField("JMS_TC_CD", tccode);
			inRecord.setField("JMS_TC_CREATE_DDTT",
					jspeed.base.util.DateHelper.format(new java.util.Date(System.currentTimeMillis()), "yyyyMMddHHmmss"));
			inRecord.setField("JMS_TC_MESSAGE", message);

			// 큐에 데이터를 전송합니다.
			sender.send(inRecord);

			szMsg = "[ " + queueName + " ]  JMS_TC_CD:<" + tccode  + ">   SEND FINISH ";
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			flag = "Y";
		} catch(Exception e){
			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + tccode  + ">   SEND Exception " + e.getMessage();
			ydUtils.putLog(SZ_SESSION_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		//	throw new com.inisteel.cim.common.exception.DAOException(getClass().getName() + e.getMessage(), e);
			return flag;
		}finally{
			try{
				sender.closeAll();
			}catch(Exception e){
				ydUtils.putLog(SZ_SESSION_NAME, szMethodName, e.getMessage(), JPlateYdConst.ERROR);
			}
		}
	    return flag;
	}

  //---------------------------------------------------------------------------
}
