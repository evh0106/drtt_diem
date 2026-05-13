package com.inisteel.cim.yfPI.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import com.inisteel.cim.common.jms.internal.BreCall;
import com.inisteel.cim.common.rabbitmq.RabbitmqMessageBean;
import com.inisteel.cim.common.rabbitmq.RabbitmqReceiver;
import com.inisteel.cim.common.rabbitmq.Util;
import com.inisteel.cim.yd.pSlabCommon.util.PSlabYdUtils;
import com.metis.rapi4j.ResultData;
import com.metis.rapi4j.RuleException;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;
import tmt.json.JSONException;

/**
 *      [A] 클래스명 : RabbitMQ Yd
 * 
 * @ejb.bean name="M10LmExYf11RecieveFaEJBD" jndi-name="M10LmExYf11RecieveFaEJBD" type="Stateless"
 *           view-type="remote" display-name="" description="RabbitMQ Yd"
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="1" max-beans-in-free-pool="1"
 * @ejb.transaction type="Required" 
*/
public class M10LmExYf11RecieveFaEJBBean extends BaseSessionBean{

	private RabbitmqReceiver rabbitReceiver;
	private Channel channel;
            
	private PropertyService propertyService;
	        
	private String EXCHANGENAME;
	private String ROUTINGKEY;
	private String QNAME;
	private PSlabYdUtils slabUtils = new PSlabYdUtils();       
	private RabbitmqMessageBean messagBean = new RabbitmqMessageBean();   
	
	/**
	 *  EJB 생성시점에 WEBLOGIC 컨테이너가 호출하는 메소드
	 * 
	 * @throws javax.ejb.CreateException
	 */
	public void ejbCreate() throws javax.ejb.CreateException {

	}

}
