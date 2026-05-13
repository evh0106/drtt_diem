package com.inisteel.cim.yd.testYD.dao;

import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;


import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;

/**
 * 테스트 관련 DAO 클래스 입니다.
 *
 * @author 이현성
 */
public class JspSimTestDAO {


	private DBAssistantDAO assistantDAO = new DBAssistantDAO();

	/**
	 * 작업예약정보 조회
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getWRKBOOK(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
						
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.dao.tesjsptDao.getWRKBOOK");

			// 검색문을 실행합니다.
			outRecordSet = assistantDAO.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}	
	
	/**
	 * 크레인 스케줄 조회 
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getCRNSCH(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
						
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.dao.tesjsptDao.getCRNSCH");

			// 검색문을 실행합니다.
			outRecordSet = assistantDAO.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}
	

	/**
	 * 야드 저장품 조회 
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getSTOCK(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
						
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.dao.tesjsptDao.getSTOCK");

			// 검색문을 실행합니다.
			outRecordSet = assistantDAO.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}
	
	/**
	 * 야드 저장품 조회 
	 *
	 * @param inDto
	 * @return
	 * @throws Exception
	 */
	public JDTORecordSet getYdFlexTest(JDTORecord inDto) throws DAOException {
		JDTORecordSet outRecordSet = null;
		try {
			// 실행할 queryID를 JDTORecord에 할당하여 DAO를 호출한다.
						
			inDto.setField("JSPEED_QUERY_ID",
					"com.inisteel.cim.yd.dao.testdao.getYdFlexTest");

			// 검색문을 실행합니다.
			outRecordSet = assistantDAO.getRecordSet(inDto);

		} catch (Exception e) {
			e.getStackTrace();
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(getClass().getName() + e.getMessage(),e);
		} finally {

		}
		return outRecordSet;
	}	
	
	

}
