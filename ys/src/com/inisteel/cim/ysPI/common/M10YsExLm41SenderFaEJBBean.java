package com.inisteel.cim.ysPI.common;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

import com.inisteel.cim.common.rabbitmq.RabbitmqSender;
import com.rabbitmq.client.Channel;
import jspeed.base.property.PropertyService;
import com.inisteel.cim.ys.common.util.YsCommUtils;
///**
// *      [A] 클래스명 : RabbitMQ Yd
// * 
// * @ejb.bean name="M10YdExLm41SenderFaEJB" jndi-name="M10YdExLm41SenderFaEJB" type="Stateless"
// *           view-type="remote" display-name="" description="RabbitMQ Yd"
// * @weblogic.enable-call-by-reference True
// * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
// * @ejb.transaction type="Required" 
//*/
public class M10YsExLm41SenderFaEJBBean { //extends BaseSessionBean{

	//private Logger logger = new Logger("qd");
	
	private RabbitmqSender rabbitSender;
	private Channel channel;
	
	private PropertyService propertyService;
	
	private String EXCHANGENAME;
	private String ROUTINGKEY;
	private YsCommUtils ysUtils = new YsCommUtils();

	
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
			keyName = "m10yd.lm.41";
		}
		if ("".equals(exchangeName) || exchangeName == null) {
			exchangeName = "M10YD-EX-LM-41";
		}		
		
		try {
			//if((this.channel == null) && (!this.channel.isOpen())) 
			if (ydSndYn == null) {
				ydSndYn = "N";
			} 
			
			RabbitConnect(keyName, exchangeName, ydSndYn);
			
			rabbitSender.SendMessage(paramMap);
			
			//RabbitDisConnect();
			
			ysUtils.printLog("MES_PI_RABBIT_MQ", "===== M10YdExLm41SenderFaEJB(특수강야드야드)송신Message 전문: >>>>>>>>>> :" + paramMap, "S+");

		} 
		catch (IOException e) {
			ysUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm41SenderFaEJB(특수강야드송신) IOException Error " + e.getMessage());
		}
		catch (Exception e) {
			ysUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm41SenderFaEJB(특수강야드송신) Exception Error " + e.getMessage());
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

		try {
			this.propertyService = PropertyService.getInstance();
			
			if ("Y".equals(ydSndYn)) {
				this.EXCHANGENAME = propertyService.getProperty("common.properties","rabbitmq.YD.M10LM-EX-YD-41");
				this.ROUTINGKEY   = propertyService.getProperty("common.properties","rabbitmq.YD.m10lm.yd.41");			
			} else {
				this.EXCHANGENAME = propertyService.getProperty("common.properties","rabbitmq.YD.M10YD-EX-LM-41");
				this.ROUTINGKEY   = propertyService.getProperty("common.properties","rabbitmq.YD.m10yd.lm.41");			
			}	
//LOCAL			
//			this.EXCHANGENAME = "M10YD-EX-LM-41S";  
//			this.ROUTINGKEY   = "m10yd.lm.41S";  			
			
			this.rabbitSender = new RabbitmqSender(this.EXCHANGENAME, this.ROUTINGKEY);
			
			this.channel  = this.rabbitSender.RabbitOpen();
			
		}  
		catch (IOException e) {
			ysUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm41SenderFaEJB(특수강야드송신) RabbitConnect IOException ======" + e.getMessage());
		}
		catch (TimeoutException e) {
			ysUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm41SenderFaEJB(특수강야드송신) RabbitConnect TimeoutException ======" + e.getMessage());
		}
		catch (Exception e) {
			ysUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm41SenderFaEJB(특수강야드송신) Exception ======" + e.getMessage());
		}

		ysUtils.printLog("MES_PI_RABBIT_MQ","", "===== M10YdExLm41SenderFaEJB(특수강야드송신) RabbitConnected ======OK");
		
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
		
		ysUtils.printLog("MES_PI_RABBIT_MQ","", "=====M10YdExLm41SenderFaEJB(특수강야드송신) RabbitDisConnect called");
	}
	
	
}

