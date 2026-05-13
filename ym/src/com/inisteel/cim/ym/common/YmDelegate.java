package com.inisteel.cim.ym.common;

import java.net.InetAddress;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmDeleComm;


/**
 * JMS Q, Remote EAI, L2 EAI, Facade Call
 * @param inRecord
 * @throws 
 */
public class YmDelegate{
	
	private String szSessionName = this.getClass().getName();

	private YmCommonUtil ymCommonUtil = new YmCommonUtil();
	private YmDeleComm deleComm = new YmDeleComm();


	private String szIPDevSys1="10.216.130.45";	// 개발계
	private String szIPTstSys1="10.216.133.163";	// 테스트계1
	private String szIPTstSys2="10.216.133.164";	// 테스트계2
	
	/**
	 * JMS Q, Remote EAI, L2 EAI, Facade Call
	 * @param inRecord
	 * @throws 
	 */
	public void sendMsg(JDTORecord msgRecord){
		
		//
		// YD Delegate 송신 Main
		// 수신 한 msgRecord의 TCCode를 분석하여
		// 내부(J), RemoteEAI(R), L2EAI(L)를 판단 한 후 
		// 대상 메소드를 통해서 송신한다.
		//
	
		String szMsg="";
		String szMethodName = "sendMsg";
		String szTcCode = "";
		String szQueueName ="";
		String szWkGp="";
		String szYdJMSQName ="";
		String szYdEAIQName ="";
		
		int nRtc=0;
		int nTcKind=0;
		
		//
		// 송신 용 TC (Maked TC)
		JDTORecord tcRecord =null;	
		JDTORecordSet tcRecSet =null;
		
		PropertyService propertyService = null;	
		
		try{
			
			szTcCode = ymCommonUtil.getTcCode(msgRecord);
			
			szMsg = "Delegate 송신 요청 수신 (TC Code="+szTcCode+")";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
			
			
			
			// 1:JMS, 2:Remote EAI, 3:L2 EAI, 9:Facade
			nTcKind = ymCommonUtil.chkTcType(szTcCode);	
		
		
			// TC코드가 맞지 않을때
			if(nTcKind <=0) {

				szMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return ;
			}
		

			
			//
			// 전송용 JDTORecord 생성
			tcRecSet =JDTORecordFactory.getInstance().createRecordSet("YDDelegate");
			//
			// nRtc>0 : tcRecSet의 Record Count
			nRtc =ymCommonUtil.makeTc(msgRecord, tcRecSet);
			if( nRtc<=0){
				szMsg=" TC("+szTcCode+") Data Make Error";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				
				return ;
			}


			
			//
			//String szYDJMSQName="YD_MDB_QUEUE";
			//String szYDEAIQName="YD_EAI_QUEUE";
			//
			propertyService = PropertyService.getInstance();
			
			// YD_MDB_QUEUE
			szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			// YD_EAI_QUEUE
			szYdEAIQName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");
			
			
			// 
			// DEBUG MSG
			szMsg="[DEBUG] 대상Queue  : ["+szYdJMSQName+"] ";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
			
			
			
			switch(nTcKind){
		
			//
			// EnQueue
			case 1:		// JMS Queue 송신
				
				szWkGp =szTcCode.substring(2, 4);
				szQueueName =propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");

				// 
				// Internal Queue Send
				for(int i=0; i<nRtc;i++){
					
					tcRecord =tcRecSet.getRecord(i);
					deleComm.jmsQSnder(szQueueName, tcRecord);
				
				} // end of for()
				
				break;	// end of case 1
				
				
			case 2:		// 리모트 EAI 송신
			
				
				//  
				// Remote EAI Send
				for(int i=0; i<nRtc;i++){
					
					tcRecord =tcRecSet.getRecord(i);
					//deleComm.httpSnder(tcRecord);
					deleComm.remoteEaiSnder(tcRecord);
				
				} // end of for()

				
				break; // end of case 2
				
				
			case 3:		// L2 EAI 송신
			
				// 
				// EAI Queue Send
				szQueueName=szYdEAIQName;
				for(int i=0; i<nRtc;i++){
					
					tcRecord =tcRecSet.getRecord(i);
					deleComm.jmsQSnder(szQueueName, tcRecord);
				
				} // end of for()
			
				break; // end of case 3
		
		
		
			//
			// Facade Call
			case 9:
				//
				// Facade Call의 경우에는 수신 한 Record에서 FacadeName, MethodName을 발췌하여 송신
				nRtc =deleComm.facadeSender(msgRecord);
				if(nRtc<0) {

					String szErrMsg = "Remote Facade Call Fail";
					ymCommonUtil.putLog(szSessionName, szMethodName, szErrMsg, 1);
					
					return;

				}

				break; // end of case 3:


			default: 
				
				szMsg="Unknown TC Case : "+nTcKind;
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return;
				
			
			} // end of switch()
			
			
		} catch (Exception e) {
			
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			return;
		
		} // end of try catch

		
	} // end of  sendMsg()
	
	
	
	
	
	/**
	 * Application 환경이 서버(개발계/테스트계)인지를 판단하여
	 * 서버인 경우에는 서버로, 로컬인 경우에는 로컬로 메시지 전송 
	 */
	public void msgSend(JDTORecord msgRecord){
		
		String szMethodName="msgSend";
		String szMsg="";
		
		try{
			
			InetAddress ipAddr=InetAddress.getLocalHost();

			if( (ipAddr.getHostAddress().equals(szIPDevSys1)) ||
				(ipAddr.getHostAddress().equals(szIPTstSys1)) ||
				(ipAddr.getHostAddress().equals(szIPTstSys2))  ){
				
				// 개발계/테스트1,2 서버
				this.sendMsg(msgRecord);
			}
			else{
				
				// 개발계가 아닌 경우 (로컬)
				this.lclSndMsg(msgRecord);
			}
			
		} catch(Exception e){
			
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			
			return;
			
		} // end of try-catch
		
	
		
	} // end of msgSend()
	
	
	
	
	
	/**
	 * local JMS Q송신을 위한 내부(Local) Queue 송신
	 * 
	 */
	public void lclSndMsg(JDTORecord msgRecord){
		
	
		String szMsg="";
		String szMethodName = "lclSndMsg";
		String szTcCode = "";
		
		
		try{
			
			szTcCode = ymCommonUtil.getTcCode(msgRecord);
			
			szMsg = "Delegate(Local) 송신 요청 수신 (TC Code="+szTcCode+")";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
			
			
			
			int nTcKind = 0;

			// 1:JMS, 2:Remote, 3:L2, 9:Facade
			nTcKind = ymCommonUtil.chkTcType(szTcCode);	
		
		
			// TC코드가 맞지 않을때
			if(nTcKind < 0) {

				szMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
				return ;
			}
		
		
		
			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 내부 JMs Queue에 송신한다.
			//			
			deleComm.jmsTargetQSnder(msgRecord, 1);
			
			
		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return;
		} // end of try catch

		
	} // end of lclSndMsg()
	
	
	
	
	
	/**
	 *  remote JMS Q를 위한 원격(개발계) Queue 송신
	 * 
	 */
	public void rmtSndMsg(JDTORecord msgRecord){
		
	
		String szMsg="";
		String szMethodName = "rmtSndMsg";
		String szTcCode = "";
		
		
		try{
			
			szTcCode = ymCommonUtil.getTcCode(msgRecord);
			
			szMsg = "Delegate(Remote:개발계) 송신 요청 수신 (TC Code="+szTcCode+")";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
			
			
			
			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 서버 JMs Queue에 송신한다.
			//	
			deleComm.jmsTargetQSnder(msgRecord, 2);
			
			
		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return;
		} // end of try catch

		
	} // end of rmtSndMsg()
	
	
	
	
	
	/**
	 *  remote JMS Q를 위한 원격(테스트계) Queue 송신
	 * 
	 */
	public void tstSndMsg(JDTORecord msgRecord){
		
	
		String szMsg="";
		String szMethodName = "tstSndMsg";
		String szTcCode = "";
		
		
		try{
			
			szTcCode = ymCommonUtil.getTcCode(msgRecord);
			
			szMsg = "Delegate(Remote:테스트계) 송신 요청 수신 (TC Code="+szTcCode+")";
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 4);
		
			
			
			
			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 서버 JMs Queue에 송신한다.
			//	
			deleComm.jmsTargetQSnder(msgRecord, 3);
			
			
		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ymCommonUtil.putLog(szSessionName, szMethodName, szMsg, 1);
			return;
		} // end of try catch

		
	} // end of tstSndMsg()

	
	
	


	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                      일관제철소정보관리시스템-야드관리
	//              			YD Delegate Class
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	public static void main(String[] args){
		YmDelegate im =new YmDelegate();

		JDTORecord testRecord =null;
		try{

			testRecord =JDTORecordFactory.getInstance().create();
			
			testRecord.setField("MSG_ID", "CTYDJ002");		// JMS Q 
			//testRecord.setField("MSG_ID", "YDC3L002"); 	// L2 EAI
			//testRecord.setField("MSG_ID", "YDDMR002"); 	// Remote L2 EAI
			testRecord.setField("DATE", im.ymCommonUtil.getCurDate("yyyy-MM-dd") );
			testRecord.setField("TIME", im.ymCommonUtil.getCurDate("HH-mm-ss") );
			testRecord.setField("MSG_GP","I");
			testRecord.setField("MSG_LEN", im.ymCommonUtil.fillSpZr("123", 4, 0));
			testRecord.setField("TEMP", im.ymCommonUtil.fillSpZr("", 29, 1) );
			testRecord.setField("DATA1", im.ymCommonUtil.fillSpZr("YHWHman", 20, 1) );
			testRecord.setField("DATA2", im.ymCommonUtil.fillSpZr("yhwhman@gmail.com", 30, 1) );
			testRecord.setField("DATA3", im.ymCommonUtil.fillSpZr("010-6257-3209", 13, 1) );
			
			im.sendMsg(testRecord);
			
		} catch(Exception e){
			System.out.println("Exception Error : "+e.getLocalizedMessage());
			return;
		}
		
		
	} // end of testMain()
	
	
  //---------------------------------------------------------------------------	
} // end of class YdDelegate
