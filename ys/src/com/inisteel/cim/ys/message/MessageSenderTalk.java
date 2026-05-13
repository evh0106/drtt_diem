package com.inisteel.cim.ys.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.ArrayList;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.yd.message.dao.MessageSendTalkDAO;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.AppRuntimeException;
import com.inisteel.cim.common.exception.DAOException;



/**
 * 
 * Kakao Talk Transfer System
 * 
 * @author LJH
 *
 */
public class MessageSenderTalk {

	/**
	 * @param args
	 */
	
	private Logger logger = new Logger("common");
	
	private DBAssistantDAO assistantDAO = new DBAssistantDAO();
	
	MessageSendTalkDAO dao = new MessageSendTalkDAO();
	
	/**
	 * 
	 * @throws Exception
	 */
	public MessageSenderTalk() throws Exception
	{
		logger.println(LogLevel.DEBUG, getClass().getName() + "[ MessageSenderTalk]");
	}
	
//	public HashMap sendAutoSMS(JDTORecord inDto)
//	{
//		dao.sendM2XSMS(inDto);
//	}
 
	public void sendTalk(JDTORecord inDto) throws DAOException
	{
		try 
		{
			dao.sendTalk(inDto);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.println(LogLevel.DEBUG, this, e.getMessage(), e);
			throw new DAOException(getClass().getName() + e.getMessage());
		} 
		finally 
		{
			
		}
	}
	
	public void sendMTalk(JDTORecord[] inDtoArr) throws DAOException
	{
		try 
		{
			dao.sendMTalk(inDtoArr);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			logger.println(LogLevel.DEBUG, this, e.getMessage(), e);
			throw new DAOException(getClass().getName() + e.getMessage());
		} 
		finally 
		{
			
		}
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

}
