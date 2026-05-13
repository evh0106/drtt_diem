/*
 * @(#) 야드작업예약 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		야드작업예약 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성  
 */

package com.inisteel.cim.yd.jplateyd.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드작업예약 DAO
 *
*/

public class JPlateYdWrkbookDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdWrkbookDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
	
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약ID SELECT [야드작업예약ID : YD_WBOOK_ID]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public String getSeqId() throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String 	szMethodName 	= "getSeqId";
		String 	szSeqId 		= null;

		try {

			recPara = JDTORecordFactory.getInstance().create();

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYD_WBOOK_ID
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getSeqId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getSeqId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				szSeqId = ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "YD_WBOOK_ID"); //Sequence ID
			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 결과 :: " + szSeqId;
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return szSeqId;
	} //end of getSeqId

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 SELECT , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkbook(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdWrkbook";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbook
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbook
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbook");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			} else {
				//data not found
				String szMsg = "data not found!";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdWrkbook

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 SELECT , intGp == 2
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkbookMtl(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdWrkbookMtl";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookYdWrkbookMtl_PIDEV
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbookMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbookMtl");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdWrkbookMtl

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 SELECT , intGp == 4
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getWorkTbRefNone(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getWorkTbRefNone";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookDao.getWorkTbRefNONE
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getWorkTbRefNone
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getWorkTbRefNone");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			} else {
				//data not found
				String szMsg = "data not found!";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getWorkTbRefNone

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 SELECT [Piling 정보조회] , intGp == 32
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkPilingInfo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdWrkPilingInfo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkPilingInfo
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkPilingInfo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkPilingInfo");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdWrkPilingInfo

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 SELECT [작업예약관리화면] , intGp == 26
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkBookListjm(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String szMethodName 	= "getYdWrkBookListjm";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookSearchPage
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkBookListjm
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkBookListjm");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "[" + szMethodName + "]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdWrkBookListjm

	/**
	 *      [A] 오퍼레이션명 : 대차상차작업예약 조회 [대차스케쥴] , intGp == 27
	 *						작업예약순서가 빠른 대차상차작업예약 조회
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getForTcarLoad(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String szMethodName 	= "getForTcarLoad";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookForTcarLoad
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getForTcarLoad
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getForTcarLoad");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "[" + szMethodName + "]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getForTcarLoad

	/**
	 *      [A] 오퍼레이션명 : 대차상차작업예약 조회 [대차스케쥴] , intGp == 28
	 *						지정된 상차동, 하차동에 해당하는 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약 조회
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getTcarLoadWithAimBay(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String szMethodName 	= "getTcarLoadWithAimBay";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookForTcarLoadWithAimBay
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getTcarLoadWithAimBay
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getTcarLoadWithAimBay");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "[" + szMethodName + "]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getTcarLoadWithAimBay

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 조회 - 스케쥴 코드로 다음 작업 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getNextWrkBookIdBySchCd(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getNextWrkBookIdBySchCd";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getNextWrkBookIdBySchCd
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getNextWrkBookIdBySchCd");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "[" + szMethodName + "]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		return intRtnVal;
	} //end of getNextWrkBookIdBySchCd

	/**
	 *      [A] 오퍼레이션명 : 대차상차작업예약 조회_삭제정보 포함 [대차스케쥴] , intGp == 28
	 *						지정된 상차동, 하차동에 해당하는 우선순위가 빠르고, 작업예약순서가 빠른 대차상차작업예약 조회
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkbookWithDel(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String szMethodName 	= "getYdWrkbookWithDel";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.getYdWrkbookExceptDelYN
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbookWithDel
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getYdWrkbookWithDel");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "[" + szMethodName + "]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdWrkbookWithDel


	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 조회 - 스케쥴 코드로 등록된 작업예약 조회 [크레인작업지시 작성된것은 제외]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getBySchCdWithCrnSchNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String szMethodName 	= "getBySchCdWithCrnSchNo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getBySchCdWithCrnSchNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getBySchCdWithCrnSchNo");

			// query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "[" + szMethodName + "]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getBySchCdWithCrnSchNo


	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 조회 - 스케쥴 코드, 야드적치열구분으로 등록된 작업예약 조회 [크레인작업지시 작성된것은 제외]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getBySchCdWithYdStkColGp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String szMethodName 	= "getBySchCdWithYdStkColGp";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getBySchCdWithYdStkColGp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getBySchCdWithYdStkColGp");

			// query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "[" + szMethodName + "]data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getBySchCdWithYdStkColGp

	/**
	 *      [A] 오퍼레이션명 : 재료번호로 작업예약 존재여부 체크
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getExistByStlNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getExistByStlNo";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                    	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByStlNo");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			} else {
				//data not found
				String szMsg = "data not found!";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of getExistByStlNo

	/**
	 *      [A] 오퍼레이션명 : 저장위치로 작업예약 존재여부 체크 [조회조건 : 야드저장위치구분]
	 *						(권상예약 체크)
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getExistByYdStkColGp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getExistByYdStkColGp";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByYdStkColGp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.getExistByYdStkColGp");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp != null && rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getExistByYdStkColGp

/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdWrkbook(JDTORecord inRec) throws DAOException, JDTOException {

		int 	intRtnVal 	= 0;
		boolean blnChkField = true;
		JDTORecord recPara 	= null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = this.chkParameter(recPara);

			//parameter error return
			if (!blnChkField) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.insYdWrkbook
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.insYdWrkbook
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.insYdWrkbook");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdWrkbook

/*------------------------------------- UPDATE -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdWrkbook(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "updYdWrkbook";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChkField = true;

		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			//변환용 레코드
			JDTORecord recInPara  = null;
			JDTORecord recOutPara = null;

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//update data select
			intRtnVal = this.getYdWrkbook(inRec, outRecSet);

			//parameter error return
			if (intRtnVal < 0) {
				szMsg = "parameter error!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal;
			}

			//data not found return
			if (intRtnVal == 0) {
				szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal;
			}

			//duplicate data return
			if (outRecSet.size() != 1) {
				szMsg = "duplicate data!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal = -1;
			}

			outRecSet.first();
			outRec = outRecSet.getRecord();

			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);

			//data mapping
			this.dataMapping(recInPara, recOutPara);

			//parameter check
			blnChkField = this.chkParameter(recOutPara);

			//parameter error return
			if (!blnChkField) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookdao.YdWrkbookDao.updYdWrkbook
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.updYdWrkbook
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.updYdWrkbook");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdWrkbook

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 [야드To위치Guide,야드스케쥴우선순위] UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updToLocGuide(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara       = null;
		String szMethodName         = "updToLocGuide";
		String szMsg                = "";
		int intRtnVal               = 0;

		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.updToLocGuide
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.updToLocGuide");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) {
				szMsg = "UPDATE 처리 실패 (" + Integer.toString(intRtnVal) + ")";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal = -1;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updToLocGuide

/*------------------------------------- DELETE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 DELETE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int delYdWrkbook(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recInPara = null;

		try {
			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.delYdWrkbook
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO.delYdWrkbook");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of delYdWrkbook

/*------------------------------------- ETC -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WBOOK_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_PRIOR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_PROG_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_ST_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_REQ_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_AIM_YD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_AIM_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CTS_RELAY_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CTS_RELAY_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TO_LOC_DCSN_MTD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TO_LOC_GUIDE");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_PLAN_TCAR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_USE_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_TRN_EQP_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_CAR_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_CARD_NO");

	/*
			ydDaoUtils.mappingData(inRec, outRec, "V_PTOP_PLNT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEST_TEL_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_DIST_SHIPASSIGN_GP");
	*/

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}

	} // end of dataMapping

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			szFieldName = "V_YD_WBOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_SCH_PRIOR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_SCH_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_SCH_ST_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_AIM_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_AIM_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CTS_RELAY_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CTS_RELAY_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_TO_LOC_DCSN_MTD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_TO_LOC_GUIDE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_WRK_PLAN_TCAR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_USE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_TRN_EQP_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_CAR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_CARD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

/*
 			szFieldName = "V_PTOP_PLNT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_DEST_TEL_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 20, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_DIST_SHIPASSIGN_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
*/
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter

} // end of class
