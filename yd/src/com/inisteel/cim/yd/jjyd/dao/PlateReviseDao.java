package com.inisteel.cim.yd.jjyd.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
/**
 * 클래스명 : 후판정정야드 DAO
 * @author : 박지열
 * @작성일 : 2010.08.18
 */
public class PlateReviseDao extends DBAssistantDAO {
	
	//ToDo... dao호출시 this를 이용한다.
	 
	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private String szDaoName = getClass().getName();
	
	/**
	 * 스판과 배드에 할당된 단정보 가져오기
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecordSet getpPlateYdCrnDownListPDADanList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getpPlateYdCrnDownListPDADanList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 후판정정야드 관리자 적치확인 처리화면 (PDA) LIST조회
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public JDTORecordSet getpPlateYdCrnDownListPDA(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getpPlateYdCrnDownListPDA");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 북아웃재료삭제
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public int deleteBookoutStlList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.deleteBookoutStlList");
			rtn = this.trtProcess(inDto);
			
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return rtn;
	}
	
	
	
	/**
	 * 후판정정야드 관리자 적치확인 처리화면 (PDA) 적치확인처리
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public int updpPlateYdCrnDownListPDA(JDTORecord inDto) throws DAOException {
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.updpPlateYdCrnDownListPDA");
			rtn = this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return rtn;
	}
	
	/**
	 * 임가공입고실적등록 (조회)
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.19
	 * 
	 */
	public JDTORecordSet getCoilFromToResultList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getCoilFromToResultList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 크레인스케쥴조회
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdSchList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdSchList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 후판정정야드 모니터링 총량조회
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateMonitoring_Tot(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getpPlateMonitoring_Tot");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 작업대상재조회
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 * @작성자 :
	 * @작성일 : 
	 */
	public JDTORecordSet getpPlateYdCrnWorkList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.JspCommonDAO.getpPlateYdCrnWorkList");
			outRdSet = this.getRecordSet(inDto);
		
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판정정야드 스케쥴정보수정 (크레인)
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updatepPlateYdSchCrn(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "updatepPlateYdSchCrn";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		
	
		try {
			//recordSet create
			
			inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.PlateReviseDao.updatepPlateYdSchCrn");
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(inRec);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSchrule
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판정정야드 스케쥴삭제
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int deletepPlateYdSchCrn(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "deletepPlateYdSchCrn";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		
	
		try {
			//recordSet create
			
			inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.PlateReviseDao.deletepPlateYdSchCrn");
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(inRec);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSchrule
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판정정야드 스케쥴재료삭제
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int deletepPlateYdSchCrn_Stl(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "deletepPlateYdSchCrn_Stl";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		
	
		try {
			//recordSet create
			
			inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.PlateReviseDao.deletepPlateYdSchCrn_Stl");
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(inRec);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSchrule
	
	
	
	/**
	 * 크레인작업실적등록(차상국) - 크레인번호로 판번호(재료번호) 조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getCrnNoAndStlNo(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getCrnNoAndStlNo");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getCrnUpDownLocList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getCrnUpDownLocList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	

	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getCrnUpDownList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getCrnUpDownList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getCrnUpDownBedList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getCrnUpDownBedList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 크레인작업실적등록(차상국) - 권상/권하위치 조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getCrnUpDownBedList2(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getCrnUpDownBedList2");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 크레인작업실적등록(차상국) - 권상위치 조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getCrnUpLocList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getCrnUpLocList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 크레인작업실적등록(차상국) - 권하위치 조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getCrnDownLocList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getCrnDownLocList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 후판정정야드 별 소재현황 - 목록조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getPlateYdlocList_1(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getPlateYdlocList_1");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 후판정정야드 별 소재현황 2 - 그래픽 표현 목록조회 
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getPlateYdlocList_2(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getPlateYdlocList_2");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 *후판정정야드 북아웃코드 (조회)
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 윤혁상
	 * @작성일 : 2010.08.25
	 * 
	 */
	public JDTORecordSet getpPlateYdBookoutCodeList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getpPlateYdBookoutCodeList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판정정야드 북아웃코드 수정
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updateYdBookoutCode(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "delYdBookoutCode";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		
	
		try {
			//recordSet create
			
			inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.PlateReviseDao.updateYdBookoutCode");
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(inRec);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSchrule
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판정정야드 북아웃코드 삭제
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int delYdBookoutCode(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "delYdBookoutCode";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		
	
		try {
			//recordSet create
			
			inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.PlateReviseDao.delYdBookoutCode");
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(inRec);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSchrule
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판정정야드 북아웃코드 수정
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int inspPlateYdBookoutCode(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "delYdBookoutCode";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		
	
		try {
			//recordSet create
			
			inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.PlateReviseDao.inspPlateYdBookoutCode");
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(inRec);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSchrule
	
	
	/**
	 * 후판 정정야드 권상실적  업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void pPlateYdCrnSchUpWrk(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.pPlateYdCrnSchUpWrk");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	/**
	 * 후판 정정야드 권하실적  업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void pPlateYdCrnSchDownWrk(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.pPlateYdCrnSchDownWrk");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	
	/**
	 * 후판 정정야드 BoonIn 권상실적  업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void pPlateYdCrnBoonInSchEnd(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.pPlateYdCrnBoonInSchEnd");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	

	/**
	 * 저장위치수정 팝업 조회 (화면)(from위치)
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecordSet getJjydPlateLocMgt(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getJjydPlateLocMgt");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 저장위치수정 - 변경될 위치(to위치) 조회  
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecordSet getJjydPlateToLoc(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getJjydPlateToLoc");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 저장위치수정 - 변경될 위치(to위치) 조회  
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecordSet getPlateydLoc(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getPlateydLoc");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 저장위치수정 - ROLLMAT조회 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecordSet getListRollMat(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListRollMat");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 저장위치수정 - PLATEMAT조회 
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public JDTORecordSet getListPlateMat(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getListPlateMat");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	
	/**
	 * 저장위치수정 수정
	 * @param inDto
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public int updJjydPlateLocMgt(JDTORecord inDto) throws DAOException {
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.updJjydPlateLocMgt");
			rtn = this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return rtn; 
	}
	
	/**
	 * 저장위치수정 수정(STKCOL)
	 * @param inDto
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.02
	 */
	public int updJjydPlateLocMgtStkCol(JDTORecord inDto) throws DAOException {
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.updJjydPlateLocMgtStkCol");
			rtn = this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return rtn; 
	}
	
	
	/**
	 * 후판 정정야드 단정보 업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void updatepPlateYdGascutresult(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.updatepPlateYdGascutresult");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 * 후판 정정야드 단정보 업데이트
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public void rcvpPlateYdSetoutStl(JDTORecord inDto) throws DAOException {
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.rcvpPlateYdSetoutStl");
			this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} finally {
		}
	}
	
	
	/**
	 *      [A] 오퍼레이션명 : 후판정정야드 작업실적등록
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int inspPlatewrkResult(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "inspPlatewrkResult";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		
	
		try {
			//recordSet create
			
			inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.PlateReviseDao.inspPlatewrkResult");
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(inRec);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSchrule
	
	/**
	 * [A] 오퍼레이션명 : 후판정정야드 BOOK OUT 정보 등록
	 * 
	 * @return int
	 * @throws JDTOException 
	 */		
	public int insBookOutInfo(JDTORecord inRec) throws DAOException {
		
		int intRtnVal = 0;

		try {
			inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jsp.common.Dao.PlateReviseDao.insBookOutInfo");
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(inRec);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSchrule
		
	
	
	
	
	/**
	 * 위치별적치현황 야드별 방침 조회   
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.09.20
	 */
	public JDTORecordSet getPlateYdlocList_REMARK(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getPlateYdlocList_REMARK");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getLocMgtCodeLayerList_L(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getLocMgtCodeLayerList_L");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getpPlateYdLocationList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getpPlateYdLocationList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	/**
	 * 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getLocMgtCodeLayerList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getpPlateYdLayerList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
		
	/**
	 * 저장위치수정 삭제/반입(PDA)-레이어삭제
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public int delPDA_pPlateLocMgtLyr(JDTORecord inDto) throws DAOException {
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.delPDA_pPlateLocMgtLyr");
			rtn = this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return rtn;
	}
	
	/**
	 * 저장위치수정 삭제/반입(PDA)-적치열
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public int delPDA_pPlateLocMgtCol(JDTORecord inDto) throws DAOException {
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.delPDA_pPlateLocMgtCol");
			rtn = this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return rtn;
	}
	
	
	/**
	 * 저장위치수정 등록(PDA) - 크레인스케쥴 아이디 조회 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getLocMgtCrnSch(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getLocMgtCrnSch");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}


	
	/**
	 * 저장위치수정 등록(PDA) - 크레인스케쥴 삭제
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public int delLocMgtCrnSch(JDTORecord inDto) throws DAOException {
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.delLocMgtCrnSch");
			rtn = this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return rtn;
	}
	
	/**
	 * 저장위치수정 등록(PDA) - 크레인스케쥴 재료정보 삭제
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 박지열
	 * @작성일 : 2010.08.17
	 */
	public int delLocMgtCrnSchMtl(JDTORecord inDto) throws DAOException {
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.delLocMgtCrnSchMtl");
			rtn = this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return rtn;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 위치별적치현황 LIST (PDA)  
	 * @param JDTORecord
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 윤재광
	 * @작성일 : 2013.09.20
	 */
	public JDTORecordSet getPDApPlateYdLocList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getPDApPlateYdLocList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 * 북인작업 대상재 정보 가져오기(PDA) 
	 * @param inDto
	 * @return
	 * @throws DAOException
	 */
	public JDTORecordSet getpPlateYdBookoutStlList(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getpPlateYdBookoutStlList");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 1후판정정야드 PM45 재료정보  조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 윤재광
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getStlInfo(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getPm45StlInfo");
			outRdSet = this.getRecordSet(inDto);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	
	/**
	 * 1후판정정야드 PM45 야드맵정보 등록/삭제
	 * @param JDTORecord
	 * @return int
	 * @throws DAOException
	 * @작성자 : 윤재광
	 * @작성일 : 2013.08.17
	 */
	public int updYdStklyr(JDTORecord inDto,int iGbn) throws DAOException {
		int rtn = 0;
		try {
			inDto.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.updatePm45LocInfo");
			rtn = this.trtProcess(inDto);
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return rtn;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 1후판 압연전단 BOOK IN/OUT실적 전문 편집
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0: book in, 1:book out)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getL2TelegramInfo(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		
		int intRtnVal = 0;
		
		String szQueryIdGet1 	= "com.inisteel.cim.yd.dao.PlateReviseDao.rcvL2TelegramT3ABIC";
		String szQueryIdGet2 	= "com.inisteel.cim.yd.dao.PlateReviseDao.rcvL2TelegramT3ABOC";
		
		/* com.inisteel.cim.yd.dao.PlateReviseDao.rcvL2TelegramT3ABIC */
		/*  T3ABIC 	BOOK IN COMPLETE */
	    /*
		SELECT 
		  '00133'
		   || '29813'                                  
		   ||'000'
		   ||'00'
		   ||'00'      
		   ||TO_CHAR(SYSDATE, 'YYYY')  
		   ||TO_CHAR(SYSDATE, 'MM')   
		   ||TO_CHAR(SYSDATE, 'DD')  
		   ||TO_CHAR(SYSDATE, 'HH24')  
		   ||TO_CHAR(SYSDATE, 'MI')  
		   ||TO_CHAR(SYSDATE, 'SS')       
		   ||'00' 	                                             
		   ||RPAD(NVL(PL_L2_TRK_NO , ' ') , 16, ' ')
		   ||RPAD(NVL(PL_MPL_NO , ' ') , 32, ' ')
		   ||RPAD(:ZONE_NO,5,'0')
		   ||RPAD('0',80,'0')
		   AS T3ABIC
		FROM
		( 
		    SELECT PL_L2_TRK_NO,SUBSTR(PL_MPL_NO,0,8) AS PL_MPL_NO FROM TB_PR_ROLL_MAT  WHERE PL_MPL_NO   = :PL_PLATE_NO
		    UNION
		    SELECT PL_L2_TRK_NO,PL_MPL_NO FROM TB_PR_PLATE_MAT WHERE PL_PLATE_NO = :PL_PLATE_NO
		)
		 */
		 /* com.inisteel.cim.yd.dao.PlateReviseDao.rcvL2TelegramT3ABOC */
		 /*  T3ABOC BOOK OUT COMPLETE */
		/* 
		SELECT  
		   '00133'
		   || '29815'                                 
		   ||'000'
		   ||'00'
		   ||'00'      
		   ||TO_CHAR(SYSDATE, 'YYYY')  
		   ||TO_CHAR(SYSDATE, 'MM')   
		   ||TO_CHAR(SYSDATE, 'DD')  
		   ||TO_CHAR(SYSDATE, 'HH24')  
		   ||TO_CHAR(SYSDATE, 'MI')  
		   ||TO_CHAR(SYSDATE, 'SS')       
		   ||'00' 	                                  
		   ||RPAD(NVL(PL_L2_TRK_NO , ' ') , 16, ' ')
		   ||RPAD(NVL(PL_MPL_NO , ' ') , 32, ' ')
		   ||RPAD(:ZONE_NO,5,'0')
		   ||RPAD('0',80,'0')
		   AS T3ABOC
		FROM
		(
		    SELECT PL_L2_TRK_NO,SUBSTR(PL_MPL_NO,0,8) AS PL_MPL_NO FROM TB_PR_ROLL_MAT  WHERE PL_MPL_NO   = :PL_PLATE_NO
		    UNION
		    SELECT PL_L2_TRK_NO,PL_MPL_NO FROM TB_PR_PLATE_MAT WHERE PL_PLATE_NO = :PL_PLATE_NO
		)
	     */  
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				//data not found
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getL2TelegramInfo
	
	/**
	 * 1후판정정야드 L3재료정보  조회
	 * @param inDto
	 * @return JDTORecordSet
	 * @throws DAOException
	 * @작성자 : 윤재광
	 * @작성일 : 2010.08.23
	 * 
	 */
	public JDTORecordSet getL3StlInfo(JDTORecord inDto) throws DAOException {
		JDTORecordSet		outRdSet 		= null;
		JDTORecord recPara 		= null;
		 /* com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getL3StlInfo */ 
		 /*
		 SELECT PL_PLATE_NO AS STL_NO FROM USRPRA.TB_PR_PLATE_MAT 
		 WHERE PL_L2_TRK_NO = :PL_L2_TRK_NO AND PL_MPL_NO = :PL_MPL_NO
		 UNION
		 SELECT PL_MPL_NO AS STL_NO FROM USRPRA.TB_PR_ROLL_MAT 
		 WHERE PL_L2_TRK_NO = :PL_L2_TRK_NO AND PL_MPL_NO LIKE :PL_MPL_NO||'%'
		 */
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inDto, 0);
			
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getL3StlInfo");
			
			outRdSet = this.getRecordSet(recPara);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		} 
		
		return outRdSet;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 기존 쿼리 CONVERT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *                                     
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getLegacyYdInfo(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= 0;
		JDTORecord recPara 		= null;
		
		//com.inisteel.cim.yd.dao.ydStkLyrDao.getStkLyrbyWBookIdEtc
		String szQueryIdGet1 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getStkLyrbyWBookIdEtc";
		//com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyMtlSCHCD
		String szQueryIdGet2 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getWorkBookbyMtlSCHCD";
		//com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlBySchId
		String szQueryIdGet3 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdCrnwrkmtlBySchId";
		//com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByYdStkLyrNo
		String szQueryIdGet4 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdCrnschByYdStkLyrNo";
		//com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrYdStock
		String szQueryIdGet5 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdStklyrYdStock";
		//com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtl
		String szQueryIdGet6 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdCrnschYdCrnwrkmtl";
		//com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT
		String szQueryIdGet7 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT";
		//com.inisteel.cim.yd.dao.ydWrkHistDao.YdWrkHistDao.getYdCrnStockByPlate
		String szQueryIdGet8 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdCrnStockByPlate";
		//com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlID
		String szQueryIdGet9 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdCrnwrkmtlID";
		//com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlByYdWrkIDOrdCollSeq
		String szQueryIdGet10 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdWrkbookmtlByYdWrkIDOrdCollSeq";
		//신규
		String szQueryIdGet11 = "com.inisteel.cim.yd.jjyd.dao.PlateReviseDao.getYdCrnSubSchToLoc"; 
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if (intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if (intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if (intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			else if (intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			else if (intGp == 7)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			else if (intGp == 8)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet8);
			else if (intGp == 9)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9);
			else if (intGp == 10)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet10);
			else if (intGp == 11)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet11);
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdWrkbookmtl
	
} // end of class






