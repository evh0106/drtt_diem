package com.inisteel.cim.yfPI.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.TimeoutException;
import com.inisteel.cim.common.rabbitmq.HttpRabbitMqLog;
import com.inisteel.cim.common.rabbitmq.RabbitmqReceiver;
import com.inisteel.cim.common.rabbitmq.Util;
import com.inisteel.cim.common.rabbitmq.Alimtalk.Util.AlimtalkSendUtil;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import tmt.json.JSONException;

import com.inisteel.cim.yf.common.YfCommUtils;

/**
 *      [A] 클래스명 : RabbitMQ Yd
 * 
 * @ejb.bean name="M10LmPsPs11RecieveFaEJB" jndi-name="M10LmPsPs11RecieveFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description="RabbitMQ Yd"
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
 * @ejb.transaction type="Required" 
*/
public class M10LmPsPs11RecieveFaEJBSBean extends BaseSessionBean{

	private RabbitmqReceiver rabbitReceiver;
	private Channel channel;
            
	private PropertyService propertyService;
	        
	private String EXCHANGENAME;
	private String ROUTINGKEY;
	private String QNAME;
	private YfCommUtils piYdUtils = new YfCommUtils();    
//	private RabbitmqMessageBean messagBean = new RabbitmqMessageBean();   
	
	/**
	 *  EJB 생성시점에 WEBLOGIC 컨테이너가 호출하는 메소드
	 * 
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {
    	
//		try {			
//			//연결하고 수신시작
//			RabbitConnect();
//			StartReceive();
//			
//		} 
		try {//HS(조업서버), TM(물류서버), PM(진행서버)중 선택
			if(Util.isRabbitmqServiceDomain("HS")) {
				//연결하고 수신시작
				RabbitConnect();
				StartReceive();			
			}
		} 
		catch (IOException e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbCreate IOException error :" + e.getMessage());
		}
		catch (TimeoutException e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbCreate TimeoutException error :" + e.getMessage());
		}		
		catch (Exception e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbCreate Exception error :" + e.getMessage());
		}
	}
	  	  
	/**
	 *  EJB 종료시점에 WEBLOGIC 컨테이너가 호출하는 메소드
	 * 
	 * @throws javax.ejb.CreateException
	 */
	public void ejbRemove() {
		
		piYdUtils.printLog("MES_PI_RABBIT_MQ", "", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbRemove called");
    	
    	try {
    		//WAS(Weblogic) 종료시 리소스 해제
			RabbitDisConnect();
		} 
		catch (IOException e1) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbRemove error :" + e1.getMessage());
		}
		catch (TimeoutException e2) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbRemove error :" + e2.getMessage());
		}		
		catch (Exception e3) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbRemove error :" + e3.getMessage());
		}
	
	}
	
	/**
	 *      [A] 오퍼레이션명 : RabbitConnect
	 * 
	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
	 * @param void
	 * @return void
	 * @throws Exception 
	*/
	public void RabbitConnect() throws IOException, TimeoutException, Exception{
		try {

			this.propertyService = PropertyService.getInstance();
			//접속정보는 jspeed 콘솔 property service에 등록
			this.EXCHANGENAME = propertyService.getProperty("common.properties","rabbitmq.PS.M10LM-EX-PS-11"); 
			this.QNAME        = propertyService.getProperty("common.properties","rabbitmq.PS.M10LM-Q-PS-11");
			this.ROUTINGKEY   = propertyService.getProperty("common.properties","rabbitmq.PS.m10lm.ps.11");
//LOCAL
//			this.QNAME        = "M10LM-Q-YD-11S";  
//			this.ROUTINGKEY   = "m10lm.yd.11S";    
			
			this.rabbitReceiver = new RabbitmqReceiver(this.EXCHANGENAME,this.ROUTINGKEY,this.QNAME);

			this.channel  = this.rabbitReceiver.RabbitOpen();
			
		} 
		catch (IOException e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbRemove error :" + e.getMessage());
		}
		catch (TimeoutException e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbRemove error :" + e.getMessage());
		}		
		catch (Exception e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) ejbRemove error :" + e.getMessage());
		}

		piYdUtils.printLog("MES_PI_RABBIT_MQ", "", "===== M10LmPsPs11RecieveFaEJB(항만조업수신)       RabbitConnected OK");
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : RabbitDisConnect
	 * 
	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
	 * @param void
	 * @return void
	 * @throws IOException,TimeoutException
	*/
	public void RabbitDisConnect() throws IOException, TimeoutException{
		if(this.rabbitReceiver != null)
			this.rabbitReceiver.RabbitClose();
		
		piYdUtils.printLog("MES_PI_RABBIT_MQ", "", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) RabbitDisConnect called");
	}
	
	//메세지 수신된 시간
	String recvTime = null;
	//로직처리된 시간
	String endTime  = null;
	
	/**
	 *      [A] 오퍼레이션명 : StartRecieve
	 * 
	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
	 * @param void
	 * @return void
	 * @throws IOException,UnsupportedEncodingException
	*/
	public void StartReceive() throws IOException,UnsupportedEncodingException {
		
		Consumer consumer = new DefaultConsumer(this.channel)
		{
			String recievedMessage;
			//메세지 수신 CALLBACK 함수(자동으로 수신된다)
	    	public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties basicproperties, byte[] message) throws IOException{
	    		long deliveryTag = envelope.getDeliveryTag();
	    		
    			try {
		    		//수신 메세지...byte[] -> String변환
					//recievedMessage = new String(message);
					recievedMessage = new String(message,"UTF-8"); 
					//수신시간
					recvTime = Util.Date_YYYYMMddHHmmss(new Date());
					
					piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) 수신Message 전문: <<<<<<<<<< : " + recievedMessage);

					//받은 메세지를 처리하는 함수 호출
					MessageProcessPs11(recievedMessage);
					
		            //수신하고, queue에서 메세지 삭제.
		            channel.basicAck(deliveryTag, false);
		            
				} 
    			catch (Exception e) {
					try {
						rabbitReceiver.dlxErrorHandler.handleErrorMessage(basicproperties,channel, deliveryTag, EXCHANGENAME, ROUTINGKEY, message);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) StartReceive handleErrorMessage error " + e.getMessage());
				}//end of catch
    			
	    	};//end of handleDelivery
		};//end of consumer

		final boolean autoAck = false;
		//수신 Callback 호출 : consumer->handleDelivery
		channel.basicConsume(this.QNAME, autoAck, consumer);		
	}
	

	
	/**
	 *      [A] 오퍼레이션명 : ProcessMessageExam
	 * 
	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
	 * @param String
	 * @return HashMap
	 * @throws Exception
	*/
	public HashMap MessageProcessPs11( String paramMap ) throws JSONException,Exception{
		Hashtable inHash = new Hashtable();
		EJBConnector ejbConn = null;
		HashMap mapResult = new HashMap();
		HttpRabbitMqLog httpRaggitMqlog = null;
		String trId = null;
		
		try {
			httpRaggitMqlog = new HttpRabbitMqLog(
					this.rabbitReceiver.RABBIT_LOG_RECV_API,
					this.rabbitReceiver.RABBIT_LOG_API_TOKEN,
					this.EXCHANGENAME,
					this.ROUTINGKEY, 
					this.QNAME);
			
			HashMap dmMessage =  (HashMap) Util.JsonStringToHashMap(paramMap);
			trId  = Util.nvl(dmMessage.get("MQ_TR_ID").toString(),"");
			
			JDTORecord paramRecord = Util.hashMapTojdtoRecord(dmMessage);
			
			ejbConn = new EJBConnector("default", "JNDIPSIntInterfacesS", this);
			ejbConn.trx("rcvYdRcptCarInfo",  new Class[]{JDTORecord.class}, new Object[]{paramRecord});	
			//성공시 log기록
//			Thread.sleep(2000);
			endTime = Util.Date_YYYYMMddHHmmss(new Date());
			httpRaggitMqlog.postReceiveLog(trId, recvTime, endTime, "S", "Success");			

		} catch (JSONException e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) MessageProcessYd11 JSONException " + e.getMessage());
//			Thread.sleep(2000);
			AlimtalkSendUtil.SendAlimTalk(this.QNAME, "M10LmPsPs11RecieveFaEJB", e.getMessage());//알림톡전송
			httpRaggitMqlog.postReceiveLog(trId, recvTime, Util.Now_YYYYMMddHHmmss(), "E", "MessageProcessExam Exception:" +e.getMessage());

		} catch (Exception e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmPsPs11RecieveFaEJB(항만조업수신) MessageProcessYd11 Exception " + e.getMessage());
//			Thread.sleep(2000);
			AlimtalkSendUtil.SendAlimTalk(this.QNAME, "M10LmPsPs11RecieveFaEJB", e.getMessage());//알림톡전송
			httpRaggitMqlog.postReceiveLog(trId, recvTime, Util.Now_YYYYMMddHHmmss(), "E", "MessageProcessExam Exception:" +e.getMessage());

		}
		
		// just return용 성공시 200리턴.
		mapResult.put("STATUSCODE", "200");
		return mapResult;
	}
}
