package com.inisteel.cim.ys.message.dao;

import java.sql.Types;

import java.util.ArrayList;
import java.util.HashMap;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.property.PropertyService;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.AppRuntimeException;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.util.Crypt;

/**
 * 
 * @author LJH
 *
 */

public class MessageSendTalkDAO {
	
	private Logger logger = new Logger("common");

	
	/**
	 * jSpeed PropertyService
	 */
	private PropertyService propertyService;
	
	private DBAssistantDAO assistantDAO = new DBAssistantDAO(); 
	
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void sendTalk(JDTORecord inDto) throws DAOException 
	{
		/*
		 * 발신프로필키
		 */
		String SenderKey;
		/*
		 * 수신자 전화번호
		 */
		String PhoneNum;
		/*
		 * 템플릿 코드
		 */		
		String TmplCd;
		/*
		 * 메시지 내용
		 */
		String SndMsg;
		/*
		 * 우회발송 문자 제목
		 */
		String Subject;
		/*
		 * 우회발송 발송자 전화번호
		 */
		String SmsSndNum;
		/*
		 * Recv_id
		 */
		String recvId;
		/*
		 * Group_id
		 */
		String groupId;
		/*
		 * Program_id
		 */
		String programId;

		Object[] param = null;
		
		try 
		{	
	System.out.println(11);
			PropertyService jprop = PropertyService.getInstance();
	System.out.println(12);
			SenderKey = jprop.getProperty("message.properties", "message.auth.key");
	System.out.println(13 + "SenderKey : "+ SenderKey);
			PhoneNum = (String) inDto.getField("PHONE_NUM");
	System.out.println(14 + "PHONE_NUM : "+ PhoneNum);
			TmplCd = (String) inDto.getField("TMPL_CD");
	System.out.println(15);
			SndMsg = (String) inDto.getField("SND_MSG");
	System.out.println(16);
			
			Subject = (String) inDto.getField("SUBJECT");
	System.out.println(17);
			SmsSndNum = (String) inDto.getField("SMS_SND_NUM");
	System.out.println(18);
			recvId = (String) inDto.getField("RECV_ID");
	System.out.println(19);
			groupId = (String) inDto.getField("GRO UP_ID");
	System.out.println(20);
			programId = (String) inDto.getField("PROGRAM_ID");

			logger.println(LogLevel.DEBUG, " [ SENDER_KEY ] " + SenderKey);
			logger.println(LogLevel.DEBUG, " [ PHONE_NUM ] " + PhoneNum);
			logger.println(LogLevel.DEBUG, " [ TMPL_CD ] " + TmplCd);
			logger.println(LogLevel.DEBUG, " [ SND_MSG ] " + SndMsg);
			
			logger.println(LogLevel.DEBUG, " [ SUBJECT ] " + Subject);
			logger.println(LogLevel.DEBUG, " [ SMS_SND_NUM ] " + SmsSndNum);
			
			logger.println(LogLevel.DEBUG, " [ RECV_ID ] " + recvId);
			logger.println(LogLevel.DEBUG, " [ GROUP_ID ] " + groupId);
			logger.println(LogLevel.DEBUG, " [ PROGRAM_ID ] " + programId);
			
			param = new Object[9];
			param[0] = SenderKey;
			param[1] = PhoneNum;
			param[2] = TmplCd;
			param[3] = SndMsg;
			param[4] = Subject;
			param[5] = SmsSndNum;
			param[6] = recvId;
			param[7] = groupId;
			param[8] = programId;
			
			assistantDAO.trtProcess("com.inisteel.cim.common.message.insertkakaoTalk", param);
		} 
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, "###(COMMON) Message(Kakao talk Error)", e);
			e.printStackTrace();
			logger.println(LogLevel.ERROR, e.getMessage(), e);
			throw new DAOException(e.toString(), e);
		} 
		finally 
		{
		
		}
	}
	
	/**
	 * 
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void sendMTalk(JDTORecord[] inDtoArr) throws DAOException 
	{
		/*
		 * 발신프로필키
		 */
		String SenderKey;
		/*
		 * 수신자 전화번호
		 */
		String PhoneNum;
		/*
		 * 템플릿 코드
		 */		
		String TmplCd;
		/*
		 * 메시지 내용
		 */
		String SndMsg;
		/*
		 * 우회발송 문자 제목
		 */
		String Subject;
		/*
		 * 우회발송 발송자 전화번호
		 */
		String SmsSndNum;
		/*
		 * Recv_id
		 */
		String recvId;
		/*
		 * Group_id
		 */
		String groupId;
		/*
		 * Program_id
		 */
		String programId;

		Object[] param = null;
		
		JDTORecord inDto = null;
		
		try 
		{
			logger.println(LogLevel.DEBUG, " < Multi Message(Kakao Multi talk) Send Start > SIZE : " + inDtoArr.length);
			for(int i = 0; i < inDtoArr.length; i++)
			{			
				logger.println(LogLevel.DEBUG, " < Message(Kakao Multi talk) Send > index : " + i);
				inDto = inDtoArr[i];
				
				PropertyService jprop = PropertyService.getInstance();
				SenderKey = jprop.getProperty("message.properties", "message.auth.key");
				PhoneNum = (String) inDto.getField("PHONE_NUM");
				TmplCd = (String) inDto.getField("TMPL_CD");
				SndMsg = (String) inDto.getField("SND_MSG");
				
				Subject = (String) inDto.getField("SUBJECT");
				SmsSndNum = (String) inDto.getField("SMS_SND_NUM");
				
				recvId = (String) inDto.getField("RECV_ID");
				groupId = (String) inDto.getField("GROUP_ID");
				programId = (String) inDto.getField("PROGRAM_ID");

				logger.println(LogLevel.DEBUG, " [ SENDER_KEY ] " + SenderKey);
				logger.println(LogLevel.DEBUG, " [ PHONE_NUM ] " + PhoneNum);
				logger.println(LogLevel.DEBUG, " [ TMPL_CD ] " + TmplCd);
				logger.println(LogLevel.DEBUG, " [ SND_MSG ] " + SndMsg);
				
				logger.println(LogLevel.DEBUG, " [ SUBJECT ] " + Subject);
				logger.println(LogLevel.DEBUG, " [ SMS_SND_NUM ] " + SmsSndNum);
				
				logger.println(LogLevel.DEBUG, " [ RECV_ID ] " + recvId);
				logger.println(LogLevel.DEBUG, " [ GROUP_ID ] " + groupId);
				logger.println(LogLevel.DEBUG, " [ PROGRAM_ID ] " + programId);
				
				param = new Object[9];
				param[0] = SenderKey;
				param[1] = PhoneNum;
				param[2] = TmplCd;
				param[3] = SndMsg;
				param[4] = Subject;
				param[5] = SmsSndNum;
				param[6] = recvId;
				param[7] = groupId;
				param[8] = programId;
				
				assistantDAO.trtProcess("com.inisteel.cim.common.message.insertkakaoTalk", param);
			}
		}
		catch (Exception e) 
		{
			logger.println(LogLevel.ERROR, "###(COMMON) Message(Kakao talk Error)", e);
			e.printStackTrace();
			logger.println(LogLevel.ERROR, e.getMessage(), e);
			throw new DAOException(e.toString(), e);
		} 
		finally 
		{
		
		}
	}
	 
}
