package com.inisteel.cim.yf.message;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yf.message.dao.MessageSendTalkDAO;

import jspeed.base.log.LogLevel;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;

public class MessageSenderTalk {

	/**
	 * @param args
	 */

	private Logger logger = new Logger("common");

	private DBAssistantDAO assistantDAO = new DBAssistantDAO();

	MessageSendTalkDAO dao = new MessageSendTalkDAO();

	public MessageSenderTalk() {
		logger.println(LogLevel.DEBUG, getClass().getName() + "[ MessageSenderTalk]");
	}

	// public HashMap sendAutoSMS(JDTORecord inDto)
	// {
	// dao.sendM2XSMS(inDto);
	// }

	public void sendTalk(JDTORecord inDto) throws DAOException {
		try {
			dao.sendTalk(inDto);
		} catch (Exception e) {
			logger.println(LogLevel.DEBUG, this, e.getMessage(), e);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}

	public void sendMTalk(JDTORecord[] inDtoArr) throws DAOException {
		try {
			dao.sendMTalk(inDtoArr);
		} catch (Exception e) {
			logger.println(LogLevel.DEBUG, this, e.getMessage(), e);
			throw new DAOException(getClass().getName() + e.getMessage());
		} finally {

		}
	}
}
