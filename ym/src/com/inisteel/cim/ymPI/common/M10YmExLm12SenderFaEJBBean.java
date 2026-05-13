package com.inisteel.cim.ymPI.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.inisteel.cim.common.rabbitmq.RabbitmqSender;
import com.rabbitmq.client.Channel;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.property.PropertyService;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;

///**
// *      [A] 클래스명 : RabbitMQ Yd-삭제
// * 
// * @ejb.bean name="M10YdExLm12SenderFaEJB" jndi-name="M10YdExLm12SenderFaEJB" type="Stateless"
// *           view-type="remote" display-name="" description="RabbitMQ Yd"
// * @weblogic.enable-call-by-reference True
// * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
// * @ejb.transaction type="Required" 
//*/
public class M10YmExLm12SenderFaEJBBean { //extends BaseSessionBean{

	//private Logger logger = new Logger("qd");
	
	private RabbitmqSender rabbitSender;
	private Channel channel;
	
	private PropertyService propertyService;
	
	private String EXCHANGENAME;
	private String ROUTINGKEY;
	private PSlabYdUtils slabUtils = new PSlabYdUtils();

	
//	/**
//	 *      [A] 오퍼레이션명 : SendMessage
//	 * 
//	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
//	 * @param HashMap
//	 * @return HashMap
//	 * @throws IOException, TimeoutException, Exception 
//	*/
	public HashMap SendMessageD(final HashMap paramMap) throws IOException, TimeoutException, Exception{

		//logger.println(LogLevel.DEBUG_TEXT, this, "PiRabbitSenderFaEJBBean SendMessage called ");
//		slabUtils.printLog("MES_PI_RABBIT_MQ", "===== M10YdExLm12SenderFaEJB(1열연야드송신) SendMessage called", "S+");
		
		HashMap mapResult = new HashMap();
		String keyName       = (String) paramMap.get("ROUTING_KEY_NAME");
		String exchangeName  = (String) paramMap.get("EXCHANGENAME");
		
		// TEST용으로 화면에서 기동할 경우 ROUTING_KEY_NAME: m10lm.yd.12 이걸루 들어온다
		// 아니경우 m10yd.lm.12
		if ("".equals(keyName) || keyName == null) {
			keyName = "m10yd.lm.12";
		}
		if ("".equals(exchangeName) || exchangeName == null) {
			exchangeName = "M10YD-EX-LM-12";
		}
				
		try {
			//if((this.channel == null) && (!this.channel.isOpen())) 
			
			RabbitConnectD(keyName, exchangeName );
			
//			slabUtils.printLog("MES_PI_RABBIT_MQ", "===== M10YdExLm12SenderFaEJB(1열연야드송신) SendMessage paramMap --> " + paramMap, "S+");
			//EJB CALL 예제.
			//HashMap dmMessage = (HashMap) paramMap.get("dmMessage2");
			//HashMap  dmMessage = new HashMap();
			//  dmMessage.put("JMS_TC_CD", (String) paramMap.get("JMS_TC_CD"));
			
			
			rabbitSender.SendMessage(paramMap);
			
			slabUtils.printLog("MES_PI_RABBIT_MQ", "===== M10YdExLm12SenderFaEJB(1열연야드송신) SendMessage 전문: --> " + paramMap, "S+");
			
			
//			HashMap regTcMap1 	= new HashMap();
//			regTcMap1.put("PECRE1", "11");
//			regTcMap1.put("PECRE2", "11");
//			regTcMap1.put("PECRE3", "11");
//			regTcMap1.put("PECRE4", "11");
//			regTcMap1.put("PECRE5", "11");
//			regTcMap1.put("PECRE6", "11");
//			regTcMap1.put("PECRE7", "11");
//			regTcMap1.put("PECRE8", "11");
//			regTcMap1.put("PECRE9", "11");
//			regTcMap1.put("PECRE10", "11");
//			regTcMap1.put("PECRE11", "11");
//			regTcMap1.put("PECRE12", "11");
//			regTcMap1.put("PECRE13", "11");
//			regTcMap1.put("PECRE14", "11");
//			regTcMap1.put("PECRE15", "11");
//			
//			slabUtils.printLog("MES_PI_RABBIT_MQ", "M10YdExLm13SenderFaEJB SendMessage paramMap --> " + regTcMap1, "S+");
//			
//			rabbitSender.SendMessage(regTcMap1);
//			
			
			//Bre Call예제
			//HashMap dmMessage = (HashMap) paramMap.get("dmMessage1");
			//rabbitSender.SendMessage(dmMessage);
			
			//logger.println(LogLevel.DEBUG_TEXT, this, "PiRabbitSenderFaEJBBean dmMessage --> " + dmMessage);
		} 
		catch (IOException e) {
			slabUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm12SenderFaEJB(1열연야드송신) IOException Error " + e.getMessage());
		}
		catch (Exception e) {
			slabUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm12SenderFaEJB(1열연야드송신) Exception Error " + e.getMessage());
		}

		// just return용 성공시 200리턴.
		mapResult.put("STATUSCODE", "200");
		return mapResult;
	}
	

//	/**
//	 *      [A] 오퍼레이션명 : Rabbit Connect
//	 * 
//	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
//	 * @param void
//	 * @return HashMap
//	 * @throws Exception 
//	*/
	public HashMap RabbitConnectD(String keyName, String exchangeName) throws IOException, TimeoutException, Exception{
		
		HashMap mapResult = new HashMap();
		// just return용
		mapResult.put("STATUSCODE", "200");

		//if((this.channel != null) && this.channel.isOpen()) return mapResult;
		
		try {
			this.propertyService = PropertyService.getInstance();
			
			this.EXCHANGENAME = exchangeName;//propertyService.getProperty("common.properties","rabbitmq.example.exchangename");			
			this.ROUTINGKEY   = keyName ;//"HR.TO.YD";//propertyService.getProperty("common.properties","rabbitmq.example.routingkey");
			//propertyService.getProperty("common.properties",keyName);
//			slabUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm12SenderFaEJB RabbitConnect EXCHANGENAME :"+this.EXCHANGENAME);
//			slabUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm12SenderFaEJB RabbitConnect ROUTINGKEY :"+this.ROUTINGKEY);
			
			this.rabbitSender = new RabbitmqSender(this.EXCHANGENAME, this.ROUTINGKEY);
			
			this.channel  = this.rabbitSender.RabbitOpen();
			
		}  
		catch (IOException e) {
			slabUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm12SenderFaEJB(1열연야드송신) RabbitConnect IOException ======" + e.getMessage());
		}
		catch (TimeoutException e) {
			slabUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm12SenderFaEJB(1열연야드송신) RabbitConnect TimeoutException ======" + e.getMessage());
		}
		catch (Exception e) {
			slabUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm12SenderFaEJB(1열연야드송신) Exception ======" + e.getMessage());
		}

		slabUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm12SenderFaEJB(1열연야드송신) RabbitConnected ======OK");
		
		return mapResult;
    	
	}



//	/**
//	 *      [A] 오퍼레이션명 : Rabbit DisConnect
//	 * 
//	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
//	 * @param void
//	 * @return void
//	 * @throws IOException,TimeoutException
//	*/
	public void RabbitDisConnect() throws IOException, TimeoutException{
		if(this.rabbitSender != null)
			this.rabbitSender.RabbitClose();
		
		slabUtils.printLog("MES_PI_RABBIT_MQ","", "=====M10YdExLm12SenderFaEJB(1열연야드송신) RabbitDisConnect called");
	}
	
	
}

