package com.inisteel.cim.ymPI.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date; 
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.TimeoutException;
import com.inisteel.cim.common.rabbitmq.HttpRabbitMqLog;
import com.inisteel.cim.common.rabbitmq.RabbitmqReceiver;
import com.inisteel.cim.common.rabbitmq.Util;
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

import com.inisteel.cim.ym.bcommon.util.YmCommUtils;
import com.inisteel.cim.common.rabbitmq.Alimtalk.Util.AlimtalkSendUtil;

/**
 *      [A] 클래스명 : RabbitMQ Yd
 * 

 * @ejb.bean name="M10LmExYm12RecieveFaEJB" jndi-name="M10LmExYm12RecieveFaEJB" type="Stateless"
 *           view-type="remote" display-name="" description="RabbitMQ Yd"
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
 * @ejb.transaction type="Required" 
*/
public class M10LmExYm12RecieveFaEJBSBean extends BaseSessionBean{

	private RabbitmqReceiver rabbitReceiver;
	private Channel channel;
            
	private PropertyService propertyService;
	        
	private String EXCHANGENAME;
	private String ROUTINGKEY;
	private String QNAME;
	private YmCommUtils piYmUtils = new YmCommUtils();       
	
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
//		}
		try {//HS(조업서버), TM(물류서버), PM(진행서버)중 선택
			if(Util.isRabbitmqServiceDomain("TM")) {
				//연결하고 수신시작
				RabbitConnect();
				StartReceive();			
			}
		} 		
		catch (IOException e) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbCreate IOException error :" + e.getMessage());
		}
		catch (TimeoutException e) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbCreate TimeoutException error :" + e.getMessage());
		}		
		catch (Exception e) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbCreate Exception error :" + e.getMessage());
		}
	}
	  	  
	/**
	 *  EJB 종료시점에 WEBLOGIC 컨테이너가 호출하는 메소드
	 * 
	 * @throws javax.ejb.CreateException
	 */
	public void ejbRemove() {
		
    	piYmUtils.printLog("MES_PI_RABBIT_MQ", "", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbRemove called");
    	
    	try {
    		//WAS(Weblogic) 종료시 리소스 해제
			RabbitDisConnect();
		} 
		catch (IOException e1) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbRemove error :" + e1.getMessage());
		}
		catch (TimeoutException e2) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbRemove error :" + e2.getMessage());
		}		
		catch (Exception e3) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbRemove error :" + e3.getMessage());
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
			this.EXCHANGENAME = propertyService.getProperty("common.properties","rabbitmq.YD.M10LM-EX-YD-12"); 
			this.QNAME        = propertyService.getProperty("common.properties","rabbitmq.YD.M10LM-Q-YD-12");
			this.ROUTINGKEY   = propertyService.getProperty("common.properties","rabbitmq.YD.m10lm.yd.12");
			
//LOCAL			
//			this.QNAME        = "M10LM-Q-YD-12S";  //propertyService.getProperty("common.properties","rabbitmq.example.qname");
//			this.ROUTINGKEY   = "m10lm.yd.12S";    //propertyService.getProperty("common.properties","rabbitmq.example.routingkey");
			
			this.rabbitReceiver = new RabbitmqReceiver(this.EXCHANGENAME,this.ROUTINGKEY,this.QNAME);

			this.channel  = this.rabbitReceiver.RabbitOpen();
			
		} 
		catch (IOException e) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbRemove error :" + e.getMessage());
		}
		catch (TimeoutException e) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbRemove error :" + e.getMessage());
		}		
		catch (Exception e) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) ejbRemove error :" + e.getMessage());
		}
		piYmUtils.printLog("MES_PI_RABBIT_MQ", "", "===== M10LmExYd12RecieveFaEJB(1열연야드수신)      RabbitConnected OK");
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
		
		piYmUtils.printLog("MES_PI_RABBIT_MQ", "", "=====M10LmExYd12RecieveFaEJB(1열연야드수신) RabbitDisConnect called");
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
    				recievedMessage = new String(message,"UTF-8"); 
    				//수신시간
					recvTime = Util.Date_YYYYMMddHHmmss(new Date());
					
					piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드)수신Message 전문: <<<<<<<<<< : " + recievedMessage);

					//받은 메세지를 처리하는 함수 호출
					MessageProcessYd12(recievedMessage);

		            //수신하고, queue에서 메세지 삭제.
		            channel.basicAck(deliveryTag, false);
		            
				} 
    			catch (Exception e) {
					try {
						rabbitReceiver.dlxErrorHandler.handleErrorMessage(basicproperties,channel, deliveryTag, EXCHANGENAME, ROUTINGKEY, message);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					piYmUtils.printLog("MES_PI_RABBIT_MQ","", "========== M10LmExYd12RecieveFaEJB(1열연야드수신) StartReceive handleErrorMessage error " + e.getMessage());
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
	public HashMap MessageProcessYd12( String paramMap ) throws JSONException,Exception{
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
			
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "YmCommEJB.rcvInterface");

			ejbConn = new EJBConnector("default", "YmCommEJB", this);
			ejbConn.trx("rcvInterface",  new Class[]{JDTORecord.class}, new Object[]{paramRecord});	
			//성공시 log기록
			endTime = Util.Date_YYYYMMddHHmmss(new Date());
			httpRaggitMqlog.postReceiveLog(trId, recvTime, endTime, "S", "Success");			

		} catch (JSONException e) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) MessageProcessYd12 JSONException " + e.getMessage());
			AlimtalkSendUtil.SendAlimTalk(this.QNAME, "M10LmExYm12RecieveFaEJB", e.getMessage());//알림톡전송
			httpRaggitMqlog.postReceiveLog(trId, recvTime, Util.Now_YYYYMMddHHmmss(), "E", "MessageProcessExam Exception:" +e.getMessage());

		} catch (Exception e) {
			piYmUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10LmExYd12RecieveFaEJB(1열연야드수신) MessageProcessYd12 Exception " + e.getMessage());
			AlimtalkSendUtil.SendAlimTalk(this.QNAME, "M10LmExYm12RecieveFaEJB", e.getMessage());//알림톡전송
			httpRaggitMqlog.postReceiveLog(trId, recvTime, Util.Now_YYYYMMddHHmmss(), "E", "MessageProcessExam Exception:" +e.getMessage());
		}
		
		// just return용 성공시 200리턴.
		mapResult.put("STATUSCODE", "200");
		return mapResult;
	}
}
