//package com.inisteel.cim.yd.common.qreceiver;
//
//import javax.jms.Message;
//import javax.jms.ObjectMessage;
//
//import jspeed.base.ejb.BaseMessageBean;
//import jspeed.base.record.JDTORecord;
//
//import com.inisteel.cim.common.exception.EJBServiceException;
//import com.inisteel.cim.yd.common.delegate.YdDeleComm;
//import com.inisteel.cim.yd.common.util.YdUtils;
//
///**
// * 내부 JMS 메시지 수신 Message Driven Bean 
// * 
// * @ejb.bean name="YdEaiSndEJB" jndi-name="YdEaiSndEJB"
// *           transaction-type="Container" acknowledge-mode="Auto-acknowledge"
// *           destination-type="javax.jms.Queue" description="YDEaiMeEJB"
// * 
// * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
// * 
// * @weblogic.message-driven connection-factory-jndi-name="LJMSConnectionFactory"
// *                          destination-jndi-name="jms/YD_EAI_QUEUE"
// *                          jms-polling-interval-seconds="10"
// */
//public class YdEaiSndEJBBean extends BaseMessageBean {
//
//	private String szSessionName =getClass().getName();
//	
//	private YdDeleComm ydDeleComm =new YdDeleComm();
//	private YdUtils ydUtils =new YdUtils();
//	
//	public void ejbCreate() throws javax.ejb.EJBException {
//	}
//
//	/**
//	 * 클래스 설명과 동일
//	 * 
//	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 * @param message
//	 * @return
//	 * @throws EJBServiceException
//	 */
//	public void onMessage(Message message) throws EJBServiceException {
//		
//		String szMsg="";
//		String szMethodName="onMessage";
//		JDTORecord rcvRec=null;
//		
//		// JMS Message 객체
//		ObjectMessage objMsg = null;
//
//		// JMS 연동 메세지
//		String szEaiMessage = null;
//
//
//		try {
//			
//			/*
//			 * JMS로부터 메세지를 dequeue합니다.
//			 */
//			objMsg = (ObjectMessage) message;
//
//			//
//			// dequeue된 ObjectMessage로부터 JDTORecord를 취득합니다.
//			//
//			rcvRec =(JDTORecord)  objMsg.getObject();
//			
//			
//			//
//			// DEBUG Msg
//			// 수신한 메시지를 Display
//			szMsg="YdEAIRcvEJB 수신 메시지 ";
//			System.out.println(szMsg);
//			ydUtils.disyRec(rcvRec);
//			
//			
//			// 
//			// JMS_TC_CD Key 제거
//			rcvRec =ydUtils.delRecKey(rcvRec, "JMS_TC_CD");
//			
//			szEaiMessage =ydUtils.makeRec2Str(rcvRec);
//			
//
//			//
//			// EAI Socket 클라이언트를 연결하여 메세지를 전송합니다.
//			//
//			ydDeleComm.socketSender(szEaiMessage);
//			
//			
//		} catch (Exception e) {
//			szMsg=szMethodName=" Exception Error : "+e.getLocalizedMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, 1);
//			return;
//			
//		} // end of try-catch
//		
//	} // end of onMessage()
//		
//		
//  //---------------------------------------------------------------------------		
//} // end of class YdEaiSndEJBBean
