package com.inisteel.cim.yfPI.common;

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
// * @ejb.bean name="M10YdExLm11SenderFaEJB" jndi-name="M10YdExLm11SenderFaEJB" type="Stateless"
// *           view-type="remote" display-name="" description="RabbitMQ Yd"
// * @weblogic.enable-call-by-reference True
// * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
// * @ejb.transaction type="Required" 
//*/
public class M10YfExLm11SenderFaEJBBean { //extends BaseSessionBean{

	//private Logger logger = new Logger("qd");
	
	private RabbitmqSender rabbitSender;
	private Channel channel;
	
	private PropertyService propertyService;
	
	private String EXCHANGENAME;
	private String ROUTINGKEY;
	private PSlabYdUtils slabUtils = new PSlabYdUtils();

	
//	/**
//	 *      [A] 오퍼레이션명 : SendMessage:삭제 사용 안함
//	 * 
//	 * @ejb.interface-method view-type="remote" EJBDoclet을 생성하는 태그입니다.
//	 * @param HashMap
//	 * @return HashMap
//	 * @throws IOException, TimeoutException, Exception 
//	*/
	public HashMap SendMessageD(final HashMap paramMap) throws IOException, TimeoutException, Exception{

		//logger.println(LogLevel.DEBUG_TEXT, this, "PiRabbitSenderFaEJBBean SendMessage called ");
//		slabUtils.printLog("MES_PI_RABBIT_MQ", "===== M10YdExLm11SenderFaEJB(박판야드송신) SendMessage called", "S+");
		
		HashMap mapResult = new HashMap();
		String keyName       = (String) paramMap.get("ROUTING_KEY_NAME");
		String exchangeName  = (String) paramMap.get("EXCHANGENAME");
		

		// just return용 성공시 200리턴.
		mapResult.put("STATUSCODE", "200");
		return mapResult;
	}
	
}

