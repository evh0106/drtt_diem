package com.inisteel.cim.ymPI.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.inisteel.cim.common.rabbitmq.RabbitmqSender;
import com.rabbitmq.client.Channel;
import jspeed.base.property.PropertyService;
import com.inisteel.cim.ym.bcommon.util.YmCommUtils;

///**
// *      [A] 클래스명 : RabbitMQ Yd ( ym야드  )
// * 
// * @ejb.bean name="M10YfExLm52SenderFaEJB" jndi-name="M10YfExLm52SenderFaEJB" type="Stateless"
// *           view-type="remote" display-name="" description="RabbitMQ Yd"
// * @weblogic.enable-call-by-reference True
// * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
// * @ejb.transaction type="Required" 
//*/
public class M10YmExLm52SenderFaEJBSBean { //extends BaseSessionBean{

	//private Logger logger = new Logger("qd");
	
	private RabbitmqSender rabbitSender;
	private Channel channel;
	
	private PropertyService propertyService;
	
	private String EXCHANGENAME;
	private String ROUTINGKEY;
	private YmCommUtils piYdUtils = new YmCommUtils(); 

	
//	/**
//	 *      [A] 오퍼레이션명 : SendMessage
//	 * 
//	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
//	 * @param HashMap
//	 * @return HashMap
//	 * @throws IOException, TimeoutException, Exception 
//	*/
	public HashMap SendMessage(final HashMap paramMap) throws IOException, TimeoutException, Exception{

		HashMap mapResult = new HashMap();
		String keyName       = (String) paramMap.get("ROUTING_KEY_NAME");
		String exchangeName  = (String) paramMap.get("EXCHANGENAME");
		String ydSndYn       = (String) paramMap.get("YD_SND_YN");
		
		if ("".equals(keyName) || keyName == null) {
			keyName = "m10yd.lm.51";
		}
		if ("".equals(exchangeName) || exchangeName == null) {
			exchangeName = "M10YD-EX-LM-51";
		}	
		
		try {
			
			RabbitConnect(keyName, exchangeName, ydSndYn);
			
			rabbitSender.SendMessage(paramMap);
			
			//RabbitDisConnect();
			
			piYdUtils.printLog("MES_PI_RABBIT_MQ", "===== M10YfExLm52SenderFaEJB(1열연임가공야드 송신)송신Message 전문: >>>>>>>>>> :" + paramMap, "S+");
			

		} 
		catch (IOException e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YfExLm52SenderFaEJB(1열연임가공야드 송신) IOException Error " + e.getMessage());
		}
		catch (Exception e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YfExLm52SenderFaEJB(1열연임가공야드 송신) Exception Error " + e.getMessage());
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
	public HashMap RabbitConnect(String keyName, String exchangeName, String ydSndYn) throws IOException, TimeoutException, Exception{
		
		HashMap mapResult = new HashMap();
		// just return용
		mapResult.put("STATUSCODE", "200");

		//if((this.channel != null) && this.channel.isOpen()) return mapResult;
		
		try {
			this.propertyService = PropertyService.getInstance();

			if ("Y".equals(ydSndYn)) { // 야드 재송신
				this.EXCHANGENAME = propertyService.getProperty("common.properties","rabbitmq.YD.M10LM-EX-YD-51");
				this.ROUTINGKEY   = propertyService.getProperty("common.properties","rabbitmq.YD.m10lm.yd.51");
			} else {
				this.EXCHANGENAME = propertyService.getProperty("common.properties","rabbitmq.YD.M10YD-EX-LM-51");
				this.ROUTINGKEY   = propertyService.getProperty("common.properties","rabbitmq.YD.m10yd.lm.51");
			}
//LOCAL			
//			this.EXCHANGENAME = "M10YD-EX-LM-51S";  
//			this.ROUTINGKEY   = "m10yd.lm.51S";  
			
			this.rabbitSender = new RabbitmqSender(this.EXCHANGENAME, this.ROUTINGKEY);
			
			this.channel  = this.rabbitSender.RabbitOpen();
			
		}  
		catch (IOException e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YfExLm52SenderFaEJB(1열연임가공야드 송신) RabbitConnect IOException ======" + e.getMessage());
		}
		catch (TimeoutException e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YfExLm52SenderFaEJB(1열연임가공야드 송신) RabbitConnect TimeoutException ======" + e.getMessage());
		}
		catch (Exception e) {
			piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YfExLm52SenderFaEJB(1열연임가공야드 송신) Exception ======" + e.getMessage());
		}

		piYdUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YfExLm52SenderFaEJB(1열연임가공야드 송신) RabbitConnected ======OK");
		
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
		
		piYdUtils.printLog("MES_PI_RABBIT_MQ","", "=====M10YfExLm52SenderFaEJB(1열연임가공야드 송신) RabbitDisConnect called");
	}
	
	
}

