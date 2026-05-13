package com.inisteel.cim.yd.common.delegate;

import java.net.InetAddress;
import java.util.List;

import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.jms.JmsQueueSender;
import com.inisteel.cim.yd.common.dao.YdCommDAO;
import com.inisteel.cim.yd.common.delegate.YdDeleComm;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdTcConst;
import com.inisteel.cim.yd.common.util.tcconst.TcConstMgr;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO;
import com.inisteel.cim.ydPI.common.M10YdExLm21SenderFaEJBBean; //후판제품 
import com.inisteel.cim.ydPI.common.M10YdExLm31SenderFaEJBBean; //SLAB 
import com.inisteel.cim.ydPI.common.util.PIYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;


/**
 * JMS Q, Remote EAI, L2 EAI, Facade Call
 * @param inRecord
 * @throws 
 */
public class YdDelegate{
	
	private String szSessionName = this.getClass().getName();

	private YdUtils ydUtils = new YdUtils();
	private YdTcConst ydTcConst = new YdTcConst();
	private YdDeleComm deleComm = new YdDeleComm();
	private TcConstMgr tcConstMgr =new TcConstMgr();
	private YdCommDAO commDao = new YdCommDAO();
	private SlabYdCommDAO slabYdCommDao = new SlabYdCommDAO();
	private YmCommUtils commUtils = new YmCommUtils();

	private String szIPDevSys1="10.216.133.61";	// 개발계
	private String szIPDevSys2="10.216.133.59";	// 개발계TM
	private String szIPDevSys3="10.216.133.204"; // 개발계TMNEW
	private String szIPDevSys4="10.216.133.207"; // 개발계TMNEW
	private String szIPDevSys5="10.216.132.204"; // 개발계TMNEW
	private String szIPDevSys6="10.216.132.207"; // 개발계TMNEW
	private String szIPTstSys1="10.216.133.15";	// 운영계1
	private String szIPTstSys2="10.216.133.25";	// 운영계2
	private String szIPTstSys3="10.216.133.163";	// 운영계1TM
	private String szIPTstSys4="10.216.133.164";	// 운영계2TM
	
	private String szIPDevSys7="10.216.132.104"; // PI검증기
	private String szIPDevSys8="10.216.132.107"; // PI검증기
	private String szIPDevSys9="10.216.132.108"; // PI검증기
	
	//조업서버에서 차량작업관리 연결해서 사용하는경우를 위한 예외처리 추가 2022.08.09
	private String szIPTstSys5="10.216.133.116";	// 운영계2TM
	private String szIPTstSys6="10.216.133.117";	// 운영계2TM

	private PIYdUtils     commPiUtils = new PIYdUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private M10YdExLm21SenderFaEJBBean      M10YdExLm21Sender   = new M10YdExLm21SenderFaEJBBean();  //후판제품 
	private M10YdExLm31SenderFaEJBBean      M10YdExLm31Sender   = new M10YdExLm31SenderFaEJBBean();  //slab 
	
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
	
		String szMsg		= "";
		String szMethodName = "sendMsg";
		String szTcCode 	= "";
		String szQueueName 	= "";
		String szWkGp		= "";
		String szYdJMSQName = "";
		String szYdEAIQName = "";
		String szYdWMEAIQName = "";
		String szYdaEAIQName = "";
		String szYdName     = "";
		String szBUFFER_TC  = "";
		int nTmp			= 0;
		int nRtc			= 0;
		int nTcKind			= 0;
		
		// 송신 용 TC (Maked TC)
		JDTORecord tcRecord 	= null;	
		JDTORecordSet tcRecSet 	= null;
		
		PropertyService propertyService = null;	
		
		JmsQueueSender jmsQSnder = null;
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		try{
			
			szTcCode = ydUtils.getTcCode(msgRecord);

			szBUFFER_TC =  StringHelper.evl(msgRecord.getFieldString(YdConstant.BUFFER_TC_CD),"");
			
			szMsg = "Delegate 송신 요청 수신 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if( szTcCode.equals("YDYDJ500")||
			    szTcCode.equals("YDYDJ503")||
			    szTcCode.equals("YDYDJ506")||
			    szTcCode.equals("YDYDJ509")) {
				
				if(szBUFFER_TC.equals("")){
					msgRecord.setField("JMS_TC_CD"	 , YdConstant.YDYDJ701);
					msgRecord.setField(YdConstant.BUFFER_TC_CD	, szTcCode);				
				}else{
					msgRecord.setField(YdConstant.BUFFER_TC_CD	, "");
				}
			}
						
			// 1:JMS, 2:Remote EAI, 3:L2 EAI, 9:Facade
			nTcKind = ydTcConst.chkTcType(szTcCode);	
		
			// TC코드가 맞지 않을때
			if(nTcKind <=0) {
				szMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			// 전송용 JDTORecord 생성
			tcRecSet =JDTORecordFactory.getInstance().createRecordSet("YDDelegate");
						
			// nRtc>0 : tcRecSet의 Record Count
			nRtc = tcConstMgr.makeTc(msgRecord, tcRecSet);
			
			if( nRtc<=0){
				szMsg=" TC("+szTcCode+") Data Make Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			
			//PIDEV
			// TC코드가 맞지 않을때
			//임가공
			if(szTcCode.startsWith("M10")) {
			
				if (szTcCode.endsWith("3")) {
					for(int i = 0; i < nRtc; i++){
						tcRecord =tcRecSet.getRecord(i);
						M10YdExLm31Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(tcRecord));
						szMsg = "rabbit mq 송신 완료 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}	
					return ;
					
				} else {
					for(int i = 0; i < nRtc; i++){
						tcRecord =tcRecSet.getRecord(i);
						M10YdExLm21Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(tcRecord));
						szMsg = "rabbit mq 송신 완료 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					return ;
				}
			}
				
			propertyService = PropertyService.getInstance();
			
			// YD_MDB_QUEUE
//			szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			// YD_EAI_QUEUE
			szYdEAIQName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE"); 
			
			// YD_EAI(WEB METHOD)_QUEUE
			szYdWMEAIQName =propertyService.getProperty("common.properties", "jms.queue.YD_WM_EAI_QUEUE");
			
			// YD_EAI(YD QUEUE 분리)_QUEUE
			szYdaEAIQName = propertyService.getProperty("common.properties", "jms.queue.YDA_EAI_QUEUE");
			
			InetAddress ipAddr=InetAddress.getLocalHost();
			// 
			// DEBUG MSG
//			szMsg="[DEBUG] 대상Queue  : ["+szYdJMSQName+"] "+ipAddr.getHostAddress();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg=szMethodName+" SERVER IP PRINT21 : "+ipAddr.getHostAddress();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
	 
			
			//개발자 로컬인 경우에만 내부 인터페이스를 로컬처리 한다.
			if(szTcCode.substring(0, 4).equals("YDYD") && 
				((!ipAddr.getHostAddress().equals(szIPDevSys1)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys2)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys3)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys4)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys5)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys6)) &&
//PIDEV				 
				 (!ipAddr.getHostAddress().equals(szIPDevSys7)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys8)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys9)) &&
//PIDEV				 
				 (!ipAddr.getHostAddress().equals(szIPTstSys1)) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys2)) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys3)) &&
				 (!"10.216.143.57".equals(ipAddr.getHostAddress())) &&
				 (!"10.216.143.49".equals(ipAddr.getHostAddress())) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys4)) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys5)) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys6))
				)){
			
				lclSndMsg(msgRecord);
			
			}else{
			
				switch(nTcKind){
			
				// EnQueue
				case 1:		// JMS Queue 송신
					
					
					if(szTcCode.substring(0, 4).equals("YDYD")){
			    		
						//YDYD전문으로 야드구분 추출
						szYdName=(String)ydTcConst.rcvTcYdMap.get(szTcCode);
						ydUtils.putLog(szSessionName, szMethodName, "["+szTcCode+"]TC해당야드:"+szYdName, YdConstant.DEBUG);
						
						szBUFFER_TC =  StringHelper.evl(msgRecord.getFieldString(YdConstant.BUFFER_TC_CD),"");
						
						
						if(!szBUFFER_TC.equals("")){//BUFFER
							szWkGp 		= "YDG";
						}else if(szYdName.equals("A")){//C연주슬라브
							szWkGp 		= "YDB";
						}else if(szYdName.equals("D")){//A후판슬라브
							szWkGp 		= "YDD";
						}else if(szYdName.equals("K")){//후판제품
							szWkGp 		= "YDE";
							//szWkGp 		= "YD";
						}else if(szYdName.equals("H")){//C열연코일
							if(szTcCode.equals("YDYDJ633")
							 ||szTcCode.equals("YDYDJ653")
							 ||szTcCode.equals("YDYDJ282")
							 ||szTcCode.equals("YDYDJ292")){
								szWkGp 		= "YDF";  //출하 입동지시요구
							}else{
								szWkGp 		= "YDC";
							}
						}else if(szYdName.equals("S")){//통합슬라브
							szWkGp 		= "YDF";
						}else if(szYdName.equals("T")){//후판저장계획 전용
							szWkGp 		= "YDH";	
						}else{
							szWkGp 		= szTcCode.substring(2, 4);
						}
						
					}else{
						szWkGp 		= szTcCode.substring(2, 4);
					}
					
					/**
					 * YDDMR001, YDDMR004 queue 변경 20200407
					 */
					if ("YDDMR001".equals(szTcCode) || "YDDMR004".equals(szTcCode)) {
						szWkGp = "DMA";
					}
					
					szQueueName = propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");
//					 DEBUG MSG
					szMsg="[DEBUG] 대상Queue  : ["+szQueueName+"] "+ipAddr.getHostAddress();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					 * 2010.07.15 YJK
					 * Send Class For문 밖으로 빼냄.
					 */
					jmsQSnder = new JmsQueueSender();
					jmsQSnder.initQueueService(szQueueName);
					
					JDTORecord[] sndMsgs1 = new JDTORecord[nRtc];
					
					// Internal Queue Send
					for(int i = 0; i < nRtc; i++){
						tcRecord =tcRecSet.getRecord(i);
						// JMS : JMS_TC_CD, L2 EAI : MSG_ID
						szTcCode =ydUtils.getTcCode(tcRecord);
						//출하http ->jms 
						if(szTcCode.substring(2,4).equals("DM")){
							tcRecord.setField("JMS_TC_CD", szTcCode);
						}
						
						sndMsgs1[i] =  tcRecord;
					} // end of for()
					jmsQSnder.send(sndMsgs1);
					break;	// end of case 1
					
				case 2:		// 리모트 EAI 송신
			
					// Remote EAI Send
					for(int i=0; i<nRtc;i++){
						
						tcRecord =tcRecSet.getRecord(i);
						//deleComm.httpSnder(tcRecord);
						deleComm.remoteEaiSnder(tcRecord);
					
					} // end of for()
					
					break; // end of case 2
					
				case 3:		// L2 EAI 송신
					
					JDTORecord targetId = null;
					JDTORecordSet targetIdSet = null;
					String chkYN = "";
					
					//YDA로 전송해야하는 Queue ID 조회 (19.04.29)
					targetId = JDTORecordFactory.getInstance().create();
					targetId.setField("JMS_TC_CD", szTcCode);
					targetId.setField("CD_GP", "YDA");
					targetIdSet = slabYdCommDao.select(targetId , "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDao.getTargetTCcodeId" , "SYSTEM" , "sndToEAI" , "EAI인터페이스 송신 처리");
					
					if(targetIdSet.size() > 0) {
						chkYN = commUtils.trim(targetIdSet.getRecord(0).getFieldString("ITEM1"));
					}
					
					// EAI Queue Send
					if("Y".equals(chkYN)) {
						szQueueName = szYdaEAIQName;
					} else {
						szQueueName = szYdEAIQName;
					}
					//szQueueName = szYdEAIQName;
					/*
					 * 2010.07.15 YJK
					 * Send Class For문 밖으로 빼냄.
					 */
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
					
					for(int i = 0; i < nRtc; i++){
						eaiL2Record = JDTORecordFactory.getInstance().create();
						tcRecord 	= tcRecSet.getRecord(i);
						nTmp 		= deleComm.makeL2EaiRecord(tcRecord, eaiL2Record);
						
						sndMsgs3[i] =  eaiL2Record;
					} // end of for()
					jmsQSnder.send(sndMsgs3);
					
					
					/*
					 * 2019.02.28 YJK
					 * WEB METHOD EAI TEST
					 */
					
//					if(szTcCode.substring(2,4).equals("Y5")){
//						jmsQSnder = new JmsQueueSender();
//						jmsQSnder.initQueueService(szYdWMEAIQName);
//						
//						JDTORecord eaiL2Record2 = null;
//						JDTORecord tcRecord2	= null;	
//						JDTORecord[] sndMsgs4 = new JDTORecord[nRtc];
//						
//						for(int i = 0; i < nRtc; i++){
//							eaiL2Record2 = JDTORecordFactory.getInstance().create();
//							tcRecord2 	= tcRecSet.getRecord(i);
//							nTmp 		= deleComm.makeL2EaiRecord(tcRecord2, eaiL2Record2);
//							
//							sndMsgs4[i] =  eaiL2Record2;
//						} // end of for()
//						jmsQSnder.send(sndMsgs4);
//					}
					
					
					
					break; // end of case 3
			
				// Facade Call
				case 9:
					//
					// Facade Call의 경우에는 수신 한 Record에서 FacadeName, MethodName을 발췌하여 송신
					nRtc =deleComm.facadeSender(msgRecord);
					if(nRtc<0) {
	
						String szErrMsg = "Remote Facade Call Fail";
						ydUtils.putLog(szSessionName, szMethodName, szErrMsg, YdConstant.ERROR);
						
						return;
					}
	
					break; // end of case 3:
	
				default: 
					
					szMsg="Unknown TC Case : "+nTcKind;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				} // end of switch()
			}
		} catch (Exception e) {
			e.printStackTrace();
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		} // end of try catch
	} // end of  sendMsg()
	
	/**
	 * JMS Q, Remote EAI, L2 EAI, Facade Call
	 * @param inRecord
	 * @throws 
	 */
	public void sendMsg_NoMakeTc(JDTORecord msgRecord){
		
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
		String szYdName     = "";
		String szBUFFER_TC  = "";

		int nRtc=0;
		int nTcKind=0;
		
		//
		// 송신 용 TC (Maked TC)
		JDTORecord tcRecord =null;	
		JDTORecordSet tcRecSet =null;
		
		PropertyService propertyService = null;	
		
		try{
			
			szTcCode = ydUtils.getTcCode(msgRecord);
			
			szMsg = "Delegate 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	
			// 1:JMS, 2:Remote EAI, 3:L2 EAI, 9:Facade
			nTcKind = ydTcConst.chkTcType(szTcCode);	
		
			// TC코드가 맞지 않을때
			if(nTcKind <=0) {

				szMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			// TC코드가 맞지 않을때
			if(szTcCode.startsWith("M10")) {
//				M10YdExLm21Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(msgRecord));
//				szMsg = "rabbit mq 송신 완료 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
//				return ;
				
				if (szTcCode.endsWith("3")) {
					M10YdExLm31Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(msgRecord));
					szMsg = "rabbit mq 송신 완료 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return ;
					
				} else {
					
					M10YdExLm21Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(msgRecord));
					szMsg = "rabbit mq 송신 완료 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					return ;
				}
			}			
			//까지						
			
			propertyService = PropertyService.getInstance();
			
			// YD_MDB_QUEUE
			szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			// YD_EAI_QUEUE
			szYdEAIQName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");
			 
			// DEBUG MSG
			szMsg="[DEBUG] 대상Queue  : ["+szYdJMSQName+"] ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			propertyService = PropertyService.getInstance();
			
			// YD_MDB_QUEUE
			szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			// YD_EAI_QUEUE
			szYdEAIQName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");
			 
			// DEBUG MSG
			szMsg="[DEBUG] 대상Queue  : ["+szYdJMSQName+"] ";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			switch(nTcKind){
		
			//
			// EnQueue
			case 1:		// JMS Queue 송신
				
				if(szTcCode.substring(0, 4).equals("YDYD")){
		    		
					//YDYD전문으로 야드구분 추출
					szYdName=(String)ydTcConst.rcvTcYdMap.get(szTcCode);
					ydUtils.putLog(szSessionName, szMethodName, "["+szTcCode+"]TC해당야드:"+szYdName, YdConstant.DEBUG);
					
					szBUFFER_TC =  StringHelper.evl(msgRecord.getFieldString(YdConstant.BUFFER_TC_CD),"");
					
					if(!szBUFFER_TC.equals("")){//BUFFER
						szWkGp 		= "YDG";
					}else if(szYdName.equals("A")){//C연주슬라브
						szWkGp 		= "YDB";
					}else if(szYdName.equals("D")){//A후판슬라브
						szWkGp 		= "YDD";
					}else if(szYdName.equals("K")){//후판제품
						szWkGp 		= "YDE";
						//szWkGp 		= "YD";
					}else if(szYdName.equals("H")){//C열연코일
						if(szTcCode.equals("YDYDJ633")
						 ||szTcCode.equals("YDYDJ653")
						 ||szTcCode.equals("YDYDJ282")
						 ||szTcCode.equals("YDYDJ292")){
							szWkGp 		= "YDF";  //출하 입동지시요구
						}else{
							szWkGp 		= "YDC";
						}
					}else if(szYdName.equals("S")){//통합슬라브
						szWkGp 		= "YDF";
					}else if(szYdName.equals("T")){//후판저장계획 전용
						szWkGp 		= "YDH";	
					}else{
						szWkGp 		= szTcCode.substring(2, 4);
					}
					
				}else{
					szWkGp 		= szTcCode.substring(2, 4);
				}
				
				szQueueName = propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");

				deleComm.jmsQSnder(szQueueName, msgRecord);
				
				break;	// end of case 1
				
				
			case 2:		// 리모트 EAI 송신
			
				
				deleComm.remoteEaiSnder(msgRecord);
								
				break; // end of case 2
				
				
			case 3:		// L2 EAI 송신
			
				// 
				// EAI Queue Send
				szQueueName=szYdEAIQName;
				
				deleComm.jmsQSnder(szQueueName, msgRecord);
				
				break; // end of case 3
		
				
			case 9:	// Facade Call
				//
				// Facade Call의 경우에는 수신 한 Record에서 FacadeName, MethodName을 발췌하여 송신
				nRtc =deleComm.facadeSender(msgRecord);
				if(nRtc<0) {

					String szErrMsg = "Remote Facade Call Fail";
					ydUtils.putLog(szSessionName, szMethodName, szErrMsg, YdConstant.ERROR);
					
					return;
				}

				break; // end of case 3:

			default: 
				
				szMsg="Unknown TC Case : "+nTcKind;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return;

			} // end of switch()

		} catch (Exception e) {
			
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
			return;
		
		} // end of try catch

		
	} // end of  sendMsg_NoMakeTc()
	
	
	
	/**
	 * Application 환경이 서버(개발계/테스트계)인지를 판단하여
	 * 서버인 경우에는 서버로, 로컬인 경우에는 로컬로 메시지 전송 
	 */
	public void msgSend(JDTORecord msgRecord){
		
		String szMethodName="msgSend";
		String szMsg="";
		
		try{
			
			InetAddress ipAddr=InetAddress.getLocalHost();
			szMsg=szMethodName+" SERVER IP PRINT : "+ipAddr.getHostAddress();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
			if( (ipAddr.getHostAddress().equals(szIPDevSys1)) ||
				(ipAddr.getHostAddress().equals(szIPDevSys2)) ||
				(ipAddr.getHostAddress().equals(szIPDevSys3)) ||
				(ipAddr.getHostAddress().equals(szIPDevSys4)) ||
				(ipAddr.getHostAddress().equals(szIPDevSys5)) ||
				(ipAddr.getHostAddress().equals(szIPDevSys6)) ||
				(ipAddr.getHostAddress().equals(szIPTstSys1)) ||
				(ipAddr.getHostAddress().equals(szIPTstSys2)) ||
				(ipAddr.getHostAddress().equals(szIPTstSys3)) ||
				(ipAddr.getHostAddress().equals(szIPTstSys4))  ){
				
				// 개발계/테스트1,2 서버
				this.sendMsg(msgRecord);
			}
			else{
				
				// 개발계가 아닌 경우 (로컬)
				this.lclSndMsg(msgRecord);
			}
			
		} catch(Exception e){
			
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			
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
			
			szTcCode = ydUtils.getTcCode(msgRecord);
			
			szMsg = "Delegate(Local) 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			
			
			
			int nTcKind = 0;

			// 1:JMS, 2:Remote, 3:L2, 9:Facade
			nTcKind = ydTcConst.chkTcType(szTcCode);	
		
		
			// TC코드가 맞지 않을때
			if(nTcKind < 0) {

				szMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}
		
		
		
			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 내부 JMs Queue에 송신한다.
			//			
			deleComm.jmsTargetQSnder(msgRecord, 1);
			
			
		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
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
			
			szTcCode = ydUtils.getTcCode(msgRecord);
			
			szMsg = "Delegate(Remote:개발계) 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			
			
			
			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 서버 JMs Queue에 송신한다.
			//	
			deleComm.jmsTargetQSnder(msgRecord, 2);
			
			
		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
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
			
			szTcCode = ydUtils.getTcCode(msgRecord);
			
			szMsg = "Delegate(Remote:테스트계) 송신 요청 수신 (TC Code="+szTcCode+")";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		
			
			
			
			//
			// Simulator Msg 송신
			// 모든 TC Type에 대해서 서버 JMs Queue에 송신한다.
			//	
			
			deleComm.jmsTargetQSnder(msgRecord, 3);
			
		} catch (Exception e) {
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		} // end of try catch

		
	} // end of tstSndMsg()

	
	/** sndSms  EAI (SMS) SEND 공통 
	 * @param   1. Message 내용, 2. TC코드 
	 * @return  String  정상일때 "Y"
	 * @throws DAOException
	 */	
	public String sndSms(String  message, String tccode) throws com.inisteel.cim.common.exception.DAOException { 
		
		String szMsg="";
		String szMethodName = "sndSms";
		com.inisteel.cim.common.jms.JmsQueueSender sender = null;
		String queueName = null; 
		JDTORecord inRecord = null;
		PropertyService propertyService=null;
		String   flag = "N";
		try {
	    // 프로퍼티 서비스 인스턴스를 취득합니다.
		propertyService = PropertyService.getInstance();

		// 큐 명칭을 프로퍼티로부터 취득합니다. [[ EAI = jms.queue.SMS_EAI_QUEUE ]]
		queueName = propertyService.getProperty("common.properties","jms.queue.SMS_EAI_QUEUE");
 
		sender = new com.inisteel.cim.common.jms.JmsQueueSender();
		
		String msgID = tccode;
		/*
		 	PRP2L008/TL3CRL/1후판압연L2[29008]/후판라우팅작업지시
			PRP2L008/TL3CRL/1후판전단L2[29108]/후판라우팅작업지시
			PRP2L010/TL3CP2/1후판전단L2[29010]/후판압연분할판제품작업지시[오토파일러정보]
		 */
		//---------------------------------------------------------------------------------------------
		//WebMethod EAI 방식 변경에 따른 BRE에서 큐명 호출 2019.12.17 
		//---------------------------------------------------------------------------------------------
		{
		    ydUtils.putLog(szSessionName, szMethodName, "▒▒▒▒ sndQueue 변경전 큐네임 : " +	queueName, JPlateYdConst.DEBUG);
		    
		    String szQueueName 	= StringHelper.evl(sender.getQueueName("YD", msgID), "");
		    
		    if(!"".equals(szQueueName)){
		        
		    	queueName = propertyService.getProperty("common.properties", "jms.queue."+szQueueName);
		    }
		    
		    ydUtils.putLog(szSessionName,szMethodName, "▒▒▒▒ sndQueue JMS_TC_CD : " +	msgID, JPlateYdConst.DEBUG);
		    ydUtils.putLog(szSessionName,szMethodName, "▒▒▒▒ sndQueue 변경후 큐네임        : " +	queueName, JPlateYdConst.DEBUG);
		}
		//---------------------------------------------------------------------------------------------
		
		// 큐에 연결할 리소스를 생성합니다.
		sender.initQueueService(queueName); 
		/*
		 * 큐에 넣을 데이터를 생성합니다.
		 * 1. LABEL2 에서 EAI 수신 정보를 생산 통데로 ByPass 한다.
		 */  
		inRecord = JDTORecordFactory.getInstance().create();
		//inRecord.setRecord(indo);		
		inRecord.setField("JMS_TC_CD", new String (tccode) );	
		inRecord.setField("JMS_TC_CREATE_DDTT", jspeed.base.util.DateHelper.format(
				new java.util.Date(System.currentTimeMillis()),
				"yyyyMMddHHmmss")); 		
		inRecord.setField("JMS_TC_MESSAGE", new String (message) );
 		
		// 큐에 데이터를 전송합니다.
		sender.send(inRecord);  

		szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + tccode  + ">   SEND FINISH ";
		ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
		flag =  "Y";
		} catch(Exception e){  
			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + tccode  + ">   SEND Exception " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
              throw new com.inisteel.cim.common.exception.DAOException(getClass().getName() + e.getMessage(), e);
		}finally{
			   try{
			       sender.closeAll(); 
			   }catch(Exception e){
			        ydUtils.putLog(szSessionName, szMethodName, e.getMessage(), YdConstant.ERROR);
			   }
		}
	    return flag;
	} 

	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
	//                                                
	//                      일관제철소정보관리시스템-야드관리
	//              			YD Delegate Class
	//                          2008.09.30 YHWHman
	//                                                      
	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
	
	public static void main(String[] args){
		YdDelegate im =new YdDelegate();

		JDTORecord testRecord =null;
		try{

			testRecord =JDTORecordFactory.getInstance().create();
			
			testRecord.setField("MSG_ID", "CTYDJ002");		// JMS Q 
			//testRecord.setField("MSG_ID", "YDC3L002"); 	// L2 EAI
			//testRecord.setField("MSG_ID", "YDDMR002"); 	// Remote L2 EAI
			testRecord.setField("DATE", im.ydUtils.getCurDate("yyyy-MM-dd") );
			testRecord.setField("TIME", im.ydUtils.getCurDate("HH-mm-ss") );
			testRecord.setField("MSG_GP","I");
			testRecord.setField("MSG_LEN", im.ydUtils.fillSpZr("123", 4, 0));
			testRecord.setField("TEMP", im.ydUtils.fillSpZr("", 29, 1) );
			testRecord.setField("DATA1", im.ydUtils.fillSpZr("YHWHman", 20, 1) );
			testRecord.setField("DATA2", im.ydUtils.fillSpZr("yhwhman@gmail.com", 30, 1) );
			testRecord.setField("DATA3", im.ydUtils.fillSpZr("010-6257-3209", 13, 1) );
			
			im.sendMsg(testRecord);
			
		} catch(Exception e){
			System.out.println("Exception Error : "+e.getLocalizedMessage());
			return;
		}
		
		
	} // end of testMain()
	
	
  //---------------------------------------------------------------------------
	/**
	 * JMS Q, Remote EAI, L2 EAI, Facade Call
	 * @param inRecord
	 * @throws 
	 */
	public void sendMsgPI(JDTORecord msgRecord){
		
		//
		// YD Delegate 송신 Main
		// 수신 한 msgRecord의 TCCode를 분석하여
		// 내부(J), RemoteEAI(R), L2EAI(L)를 판단 한 후 
		// 대상 메소드를 통해서 송신한다.
		//
	
		String szMsg		= "";
		String szMethodName = "sendMsg";
		String szTcCode 	= "";
		String szQueueName 	= "";
		String szWkGp		= "";
		String szYdJMSQName = "";
		String szYdEAIQName = "";
		String szYdWMEAIQName = "";
		String szYdaEAIQName = "";
		String szYdName     = "";
		String szBUFFER_TC  = "";
		int nTmp			= 0;
		int nRtc			= 0;
		int nTcKind			= 0;
		
		// 송신 용 TC (Maked TC)
		JDTORecord tcRecord 	= null;	
		JDTORecordSet tcRecSet 	= null;
		
		PropertyService propertyService = null;	
		
		JmsQueueSender jmsQSnder = null;
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		try{
			
			szTcCode = ydUtils.getTcCode(msgRecord);

			szBUFFER_TC =  StringHelper.evl(msgRecord.getFieldString(YdConstant.BUFFER_TC_CD),"");
			
			
			szMsg = "Delegate 송신 요청 수신 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if( szTcCode.equals("YDYDJ500")||
			    szTcCode.equals("YDYDJ503")||
			    szTcCode.equals("YDYDJ506")||
			    szTcCode.equals("YDYDJ509")) {
				
				if(szBUFFER_TC.equals("")){
					msgRecord.setField("JMS_TC_CD"	 , YdConstant.YDYDJ701);
					msgRecord.setField(YdConstant.BUFFER_TC_CD	, szTcCode);				
				}else{
					msgRecord.setField(YdConstant.BUFFER_TC_CD	, "");
				}
			}
						
			// 1:JMS, 2:Remote EAI, 3:L2 EAI, 9:Facade
			nTcKind = ydTcConst.chkTcType(szTcCode);	
		
			// TC코드가 맞지 않을때
			if(nTcKind <=0) {
				szMsg = "Unknown TC Code("+szTcCode+") Error (ErrCode="+nTcKind+")";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

			// 전송용 JDTORecord 생성
			tcRecSet =JDTORecordFactory.getInstance().createRecordSet("YDDelegate");
						
			// nRtc>0 : tcRecSet의 Record Count
			nRtc = tcConstMgr.makeTc(msgRecord, tcRecSet);
			
			if( nRtc<=0){
				szMsg=" TC("+szTcCode+") Data Make Error";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return ;
			}

//PIDEV
			// TC코드가 맞지 않을때
			//임가공
			if(szTcCode.startsWith("M10")) {
			
				if (szTcCode.endsWith("3")) {
					for(int i = 0; i < nRtc; i++){
						tcRecord =tcRecSet.getRecord(i);
						M10YdExLm31Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(tcRecord));
						szMsg = "rabbit mq 송신 완료 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}	
					return ;
					
				} else {
					for(int i = 0; i < nRtc; i++){
						tcRecord =tcRecSet.getRecord(i);
						M10YdExLm21Sender.SendMessage(commPiUtils.jdtoRecordToLinkedHashMap(tcRecord));
						szMsg = "rabbit mq 송신 완료 (TC Code="+szTcCode+")"+"BUFFER_TC="+ szBUFFER_TC;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					}
					return ;
				}
			}	
//까지			
			propertyService = PropertyService.getInstance();
			
			// YD_MDB_QUEUE
//			szYdJMSQName =propertyService.getProperty("common.properties", "jms.queue.YD_MDB_QUEUE");
			// YD_EAI_QUEUE
			szYdEAIQName =propertyService.getProperty("common.properties", "jms.queue.YD_EAI_QUEUE");
			
			// YD_EAI(WEB METHOD)_QUEUE
			szYdWMEAIQName =propertyService.getProperty("common.properties", "jms.queue.YD_WM_EAI_QUEUE");
			
			// YD_EAI(YD QUEUE 분리)_QUEUE
			szYdaEAIQName = propertyService.getProperty("common.properties", "jms.queue.YDA_EAI_QUEUE");
			
			InetAddress ipAddr=InetAddress.getLocalHost();
			// 
			// DEBUG MSG
//			szMsg="[DEBUG] 대상Queue  : ["+szYdJMSQName+"] "+ipAddr.getHostAddress();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg=szMethodName+" SERVER IP PRINT2 : "+ipAddr.getHostAddress();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.INFO);
			
	 
			
			//개발자 로컬인 경우에만 내부 인터페이스를 로컬처리 한다.
			if(szTcCode.substring(0, 4).equals("YDYD") && 
				((!ipAddr.getHostAddress().equals(szIPDevSys1)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys2)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys3)) &&
				 (!ipAddr.getHostAddress().equals(szIPDevSys4)) &&
				 
				 (!ipAddr.getHostAddress().equals(szIPTstSys1)) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys2)) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys3)) &&
				 (!"10.216.143.57".equals(ipAddr.getHostAddress())) &&
				 (!"10.216.143.49".equals(ipAddr.getHostAddress())) &&
//PIDEV          
//PI검증기
				 (!"10.216.132.104".equals(ipAddr.getHostAddress())) &&
//				 
				 (!ipAddr.getHostAddress().equals(szIPTstSys4)) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys5)) &&
				 (!ipAddr.getHostAddress().equals(szIPTstSys6))
				)){
			
				lclSndMsg(msgRecord);
			
			}else{
			
				switch(nTcKind){
			
				// EnQueue
				case 1:		// JMS Queue 송신
					
					
					if(szTcCode.substring(0, 4).equals("YDYD")){
			    		
						//YDYD전문으로 야드구분 추출
						szYdName=(String)ydTcConst.rcvTcYdMap.get(szTcCode);
						ydUtils.putLog(szSessionName, szMethodName, "["+szTcCode+"]TC해당야드:"+szYdName, YdConstant.DEBUG);
						
						szBUFFER_TC =  StringHelper.evl(msgRecord.getFieldString(YdConstant.BUFFER_TC_CD),"");
						
						
						if(!szBUFFER_TC.equals("")){//BUFFER
							szWkGp 		= "YDG";
						}else if(szYdName.equals("A")){//C연주슬라브
							szWkGp 		= "YDB";
						}else if(szYdName.equals("D")){//A후판슬라브
							szWkGp 		= "YDD";
						}else if(szYdName.equals("K")){//후판제품
							szWkGp 		= "YDE";
							//szWkGp 		= "YD";
						}else if(szYdName.equals("H")){//C열연코일
							if(szTcCode.equals("YDYDJ633")
							 ||szTcCode.equals("YDYDJ653")
							 ||szTcCode.equals("YDYDJ282")
							 ||szTcCode.equals("YDYDJ292")){
								szWkGp 		= "YDF";  //출하 입동지시요구
							}else{
								szWkGp 		= "YDC";
							}
						}else if(szYdName.equals("S")){//통합슬라브
							szWkGp 		= "YDF";
						}else if(szYdName.equals("T")){//후판저장계획 전용
							szWkGp 		= "YDH";	
						}else{
							szWkGp 		= szTcCode.substring(2, 4);
						}
						
					}else{
						szWkGp 		= szTcCode.substring(2, 4);
					}
					
					/**
					 * YDDMR001, YDDMR004 queue 변경 20200407
					 */
					if ("YDDMR001".equals(szTcCode) || "YDDMR004".equals(szTcCode)) {
						szWkGp = "DMA";
					}
					
					szQueueName = propertyService.getProperty("common.properties", "jms.queue."+szWkGp+"_MDB_QUEUE");
//					 DEBUG MSG
					szMsg="[DEBUG] 대상Queue  : ["+szQueueName+"] "+ipAddr.getHostAddress();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					/*
					 * 2010.07.15 YJK
					 * Send Class For문 밖으로 빼냄.
					 */
					jmsQSnder = new JmsQueueSender();
					jmsQSnder.initQueueService(szQueueName);
					
					JDTORecord[] sndMsgs1 = new JDTORecord[nRtc];
					
					// Internal Queue Send
					for(int i = 0; i < nRtc; i++){
						tcRecord =tcRecSet.getRecord(i);
						// JMS : JMS_TC_CD, L2 EAI : MSG_ID
						szTcCode =ydUtils.getTcCode(tcRecord);
						//출하http ->jms 
						if(szTcCode.substring(2,4).equals("DM")){
							tcRecord.setField("JMS_TC_CD", szTcCode);
						}
						
						sndMsgs1[i] =  tcRecord;
					} // end of for()
					jmsQSnder.send(sndMsgs1);
					break;	// end of case 1
					
				case 2:		// 리모트 EAI 송신
			
					// Remote EAI Send
					for(int i=0; i<nRtc;i++){
						
						tcRecord =tcRecSet.getRecord(i);
						//deleComm.httpSnder(tcRecord);
						deleComm.remoteEaiSnder(tcRecord);
					
					} // end of for()
					
					break; // end of case 2
					
				case 3:		// L2 EAI 송신
					
					JDTORecord targetId = null;
					JDTORecordSet targetIdSet = null;
					String chkYN = "";
					
					//YDA로 전송해야하는 Queue ID 조회 (19.04.29)
					targetId = JDTORecordFactory.getInstance().create();
					targetId.setField("JMS_TC_CD", szTcCode);
					targetId.setField("CD_GP", "YDA");
					targetIdSet = slabYdCommDao.select(targetId , "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDao.getTargetTCcodeId" , "SYSTEM" , "sndToEAI" , "EAI인터페이스 송신 처리");
					
					if(targetIdSet.size() > 0) {
						chkYN = commUtils.trim(targetIdSet.getRecord(0).getFieldString("ITEM1"));
					}
					
					// EAI Queue Send
					if("Y".equals(chkYN)) {
						szQueueName = szYdaEAIQName;
					} else {
						szQueueName = szYdEAIQName;
					}
					//szQueueName = szYdEAIQName;
					/*
					 * 2010.07.15 YJK
					 * Send Class For문 밖으로 빼냄.
					 */
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
					
					for(int i = 0; i < nRtc; i++){
						eaiL2Record = JDTORecordFactory.getInstance().create();
						tcRecord 	= tcRecSet.getRecord(i);
						nTmp 		= deleComm.makeL2EaiRecord(tcRecord, eaiL2Record);
						
						sndMsgs3[i] =  eaiL2Record;
					} // end of for()
					jmsQSnder.send(sndMsgs3);
					
					
					/*
					 * 2019.02.28 YJK
					 * WEB METHOD EAI TEST
					 */
					
//					if(szTcCode.substring(2,4).equals("Y5")){
//						jmsQSnder = new JmsQueueSender();
//						jmsQSnder.initQueueService(szYdWMEAIQName);
//						
//						JDTORecord eaiL2Record2 = null;
//						JDTORecord tcRecord2	= null;	
//						JDTORecord[] sndMsgs4 = new JDTORecord[nRtc];
//						
//						for(int i = 0; i < nRtc; i++){
//							eaiL2Record2 = JDTORecordFactory.getInstance().create();
//							tcRecord2 	= tcRecSet.getRecord(i);
//							nTmp 		= deleComm.makeL2EaiRecord(tcRecord2, eaiL2Record2);
//							
//							sndMsgs4[i] =  eaiL2Record2;
//						} // end of for()
//						jmsQSnder.send(sndMsgs4);
//					}
					
					
					
					break; // end of case 3
			
				// Facade Call
				case 9:
					//
					// Facade Call의 경우에는 수신 한 Record에서 FacadeName, MethodName을 발췌하여 송신
					nRtc =deleComm.facadeSender(msgRecord);
					if(nRtc<0) {
	
						String szErrMsg = "Remote Facade Call Fail";
						ydUtils.putLog(szSessionName, szMethodName, szErrMsg, YdConstant.ERROR);
						
						return;
					}
	
					break; // end of case 3:
	
				default: 
					
					szMsg="Unknown TC Case : "+nTcKind;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					return;
				} // end of switch()
			}
		} catch (Exception e) {
			e.printStackTrace();
			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		} // end of try catch
	} // end of  sendMsg()	
	
} // end of class YdDelegate
