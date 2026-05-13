//package com.inisteel.cim.yd.common.qreceiver;
//
//
///**
// * YD_MDB_QUEUE Listener Bean
// * 야드관리 수신 Queue에 수신 되는 메시지를 처리한다.
// * 
// * @author YHWHman
// *
// */
//import javax.ejb.CreateException;
//import javax.jms.Message;
//import javax.jms.ObjectMessage;
//
//import jspeed.base.ejb.BaseMessageBean;
//import jspeed.base.ejb.EJBConnector;
//import jspeed.base.record.JDTORecord;
//import jspeed.base.record.JDTORecordFactory;
//import jspeed.base.record.JDTOException;
//
//import com.inisteel.cim.yd.common.util.YdUtils;
//import com.inisteel.cim.yd.common.util.YdTcConst;
//
///**
// * 메시지 수신 Message Driven Bean 
// *
// * @ejb.bean name="YdJmsRcvEJB" jndi-name="YdJmsRcvEJB" transaction-type="Container"
// *           description="YdQReceiverEJB" acknowledge-mode="Auto-acknowledge" 
// *  		 destination-type="javax.jms.Queue"
// *  		 
// * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
// *  
// * @weblogic.message-driven connection-factory-jndi-name="LJMSConnectionFactory"
// *           destination-jndi-name="jms/YD_MDB_QUEUE"
// *           jms-polling-interval-seconds="3"
// */ 
//public class YdJmsRcvEJBBean extends BaseMessageBean {
//
//	private String szSessionName = this.getClass().getName();	
//	
//	private EJBConnector ydEjbCon = new EJBConnector("default", this);
//	
//	private YdUtils ydUtils = new YdUtils();
//	private YdTcConst ydTcConst = new YdTcConst();
//	
//	public void ejbCreate() throws CreateException {
//		
//	} // end of ejbCreate()
//
//	public void ejbRemove() {
//	}
//	
//
//	/**
//	 *@ejb.interface-method EJBDoclet을 생성하는 태그입니다.
//	 */
//	public void onMessage(Message msg) {
//		
//		String szMethodName="onMessage";
//		
//		Object objRlt=null;
//		String szMsg="";
//		String szFaName="";
//		String szOpName="";
//		String szTcCode ="";
//
//		ObjectMessage objMsg=null;
//		JDTORecord dataRecord=null;
//		
//		objMsg = (ObjectMessage) msg;
//		
//		try {
//		
//			dataRecord = (JDTORecord) objMsg.getObject();
//			
//			//
//			// Debug MSG
//			// 수신 JDTORecord를 Key=[Value]형태로 Display
//			ydUtils.disyRec(dataRecord);
//			
//						
//			
//		
//			// 
//			// TC 발췌
//			//szTcCode =dataRecord.getFieldString("JMS_TC_CD");
//			szTcCode =ydUtils.getTcCode(dataRecord);
//
//
//			//
//			// TC Code에 해당하는 대상 Facade-Method 발췌
//			szFaName=(String)ydTcConst.rcvTcFaMap.get(szTcCode);
//			szOpName=(String)ydTcConst.rcvTcOpMap.get(szTcCode);
//		
//			if( szFaName==null || szFaName.equals("") ||
//				szOpName==null || szOpName.equals("")  ){
//			
//				szMsg ="Unknown Facade-Method Name Error!\n\t";
//				// ydUtils.putLog(szSessionName, szMethodName, szMsg, 1);
//				szMsg+="TC Code=["+szTcCode+"], "
//					 +"FacadeName=["+szFaName+"], "
//					 +"MethodName=["+szOpName+"]";
//				ydUtils.putLog(szSessionName, szMethodName, szMsg, 1);
//			
//				return;
//			}
//		
//		
//			//
//			// EJBConnector 클래스를 사용하여 ebjClass 를 호출
//			objRlt = ydEjbCon.trx(szFaName, szOpName, dataRecord);
//			
//
//		} catch (Exception e) {
//			szMsg=szMethodName+" Exception Error : "+e.getLocalizedMessage();
//			ydUtils.putLog(szSessionName, szMethodName, szMsg, 1);
//
//			return;
//		}
//		
//		
//		//
//		// Debug MSG
//		// 수신 JDTORecord를 Key=[Value]형태로 Display
//		ydUtils.disyRec(dataRecord);
//		
//		
//		//
//		// Debug MSG
//		szMsg="FacadeName=["+szFaName+"], MethodName=["+szOpName+"] 수신처리 완료";
//		ydUtils.putLog(szSessionName, szMethodName, szMsg, 4);
//		
//
//		
//
//		
//	} // end of onMessage()	
//	
//
//
//
//	//┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
//	//                                                
//	//                     일관제철소정보관리시스템-야드관리
//	//                  Queue Receiver Message Driven Bean
//	//                          2008.09.30 YHWHman
//	//                                                      
//	//┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
//	
////	public static void main(String[] args){
////		YdJmsRcvEJBBean im =new YdJmsRcvEJBBean();
////		JDTORecord testRec =JDTORecordFactory.getInstance().create();
////		
////		try {
////			testRec.setField("JMS_TC_CD","QMYDJ001 ");
////			im.onMessage( (ObjectMessage)testRec);
////		} catch (JDTOException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////		}
////		
////		
////	} // end of testMain()
//
//	
//  //---------------------------------------------------------------------------
//} // end of class YdJmsRcvEJBBean
