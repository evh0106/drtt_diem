/*
 * @(#) 야드작업예약재료 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/21
 *
 * @description		야드작업예약재료 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/21   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

//-------------------------------------------------------------------------------------------------------------------------
//2024.11.18 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드작업예약재료 DAO
 *
*/

public class JPlateYdWrkbookMtlDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdWrkbookMtlDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
	
/*------------------------------------- SELECT -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 SELECT , intGp == 1
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkbookMtlId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdWrkbookMtlId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getYdWrkbookMtlId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getYdWrkbookMtlId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getYdWrkbookMtlId

	/**
	 *      [A] 오퍼레이션명 : 스케쥴생성 대상 야드작업예약재료 SELECT [조회조건 : YD_WBOOK_ID] , intGp == 8
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getStockByYdWbookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getStockByYdWbookId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyMtlSCHCD
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getStockByYdWbookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getStockByYdWbookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getStockByYdWbookId

	/**
	 *      [A] 오퍼레이션명 : 스케쥴생성 대상 야드작업예약재료 SELECT [조회조건 : YD_WBOOK_ID] , intGp == 19
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getStockJoinByStlNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getStockJoinByStlNo";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyMtlSCHCD
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getStockJoinByStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getStockJoinByStlNo");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getStockJoinByStlNo

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 SELECT [조회조건 : V_YD_WBOOK_ID, GROUP BY] , intGp == 5
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getWorkBookNone(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getWorkBookNone";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookNONE
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getWorkBookNone
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getWorkBookNone");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getWorkBookNone

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 SELECT [조회조건 : V_YD_WBOOK_ID, GROUP BY] , intGp == 7
	 *                      최하단재료에서 상단재료로 조회
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getByWBookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByWBookId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyWBookId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getByWBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getByWBookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getByWBookId

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 SELECT [조회조건 : V_YD_SCH_CD, GROUP BY] , intGp == 6
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getWorkBookSchCd(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String  szMethodName 	= "getWorkBookSchCd";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookSCHCD
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getWorkBookSchCd
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getWorkBookSchCd");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getWorkBookSchCd

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 SELECT [작업예약관리화면] , intGp == 29
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdWrkBookListjmDtl(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		JDTORecord    recPara 	= null;
		int     intRtnVal 		= -100;
		String  szMethodName 	= "getYdWrkBookListjmDtl";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getYdWrkbookmtldWithDelAPlate
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getYdWrkBookListjmDtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getYdWrkBookListjmDtl");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getYdWrkBookListjmDtl

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 건수조회 [대차스케쥴] , intGp == 14
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getMtlCntByWBookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		JDTORecord    recPara 	= null;
		int     intRtnVal 		= -100;
		String  szMethodName 	= "getMtlCntByWBookId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdCnt
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getMtlCntByWBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getMtlCntByWBookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getMtlCntByWBookId

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 대차이송재료 건수조회 [대차스케쥴] , intGp == 15
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getMtlCntByCarLdWBookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		JDTORecord    recPara 	= null;
		int     intRtnVal 		= -100;
		String  szMethodName 	= "getMtlCntByCarLdWBookId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyCarLdWBookId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getMtlCntByCarLdWBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getMtlCntByCarLdWBookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getMtlCntByCarLdWBookId

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 대차이송재료 [대차스케쥴] , intGp == 11
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getTcarDnWrkMtl(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		JDTORecord    recPara 	= null;
		int     intRtnVal 		= -100;
		String  szMethodName 	= "getTcarDnWrkMtl";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdDesc
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getTcarDnWrkMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.getTcarDnWrkMtl");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getTcarDnWrkMtl

/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdWrkbookMtl(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recPara 		= null;
		int     intRtnVal 		= 0;
		boolean blnChkField 	= true;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = this.chkParameter(recPara);

			//parameter error return
			if (!blnChkField) {
				return intRtnVal = -2;
			}

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.insYdWrkbookmtl
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.insYdWrkbookMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.insYdWrkbookMtl");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdWrkbookMtl

/*------------------------------------- UPDATE -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 UPDATE_YD_SCH_CD[K]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws DAOException
	 */
	public int updYdWrkbookMtlDelYn(JDTORecord inRec) throws DAOException, DAOException {
		int intRtnVal = 0;
		boolean blnErr = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_MODIFIER", 10, 1, 'S', 0, 0);
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_DEL_YN", 1, 1, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_WBOOK_ID", 18, 1, 'S', 0, 0);
			}

			//parameter error return
			if (!blnErr) {
				return intRtnVal = -2;
			}

			//쿼리 아이디 세팅
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.updYdWrkbookmtlDelete
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdWrkbookMtlDelYn
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdWrkbookMtlDelYn");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

			//execution error return
			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdWrkbookMtlDelYn


	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 UPDATE [YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws DAOException
	 */
	public int updYdWrkbookMtl(JDTORecord inRec) throws DAOException, DAOException {
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//쿼리 아이디 세팅
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.updYdWrkbookmtl
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdWrkbookMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdWrkbookMtl");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

			//execution error return
			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdWrkbookMtl

	/**
	 *      [A] 오퍼레이션명 : 차상위치변경
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws DAOException
	 */
	public int updYdStkLyrNo(JDTORecord inRec) throws DAOException, DAOException {
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdStkLyrNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdStkLyrNo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

			//execution error return
			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdStkLyrNo
	
	
	/**
	 *      [A] 오퍼레이션명 : 차상위치변경(Bed/단)
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws DAOException
	 */
	public int updYdStkBedLyrNo(JDTORecord inRec) throws DAOException, DAOException {
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdStkLyrNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.updYdStkBedLyrNo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

			//execution error return
			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdStkLyrNo

/*------------------------------------- DELETE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 삭제
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws DAOException
	 */
	public int deldWrkbookMtl(JDTORecord inRec) throws DAOException, DAOException {
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.deldWrkbookMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO.deldWrkbookMtl");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdWrkbookMtl

/*------------------------------------- ETC    -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 *
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {

		boolean blnErr = true;

		try {

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WBOOK_ID", 18, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_STL_NO", 11, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_REGISTER", 10, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_REG_DDTT", 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

//			blnErr = ydDaoUtils.chkField(inRec, "V_MODIFIER", 10, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			blnErr = ydDaoUtils.chkField(inRec, "V_MOD_DDTT", 0, 3, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			blnErr = ydDaoUtils.chkField(inRec, "V_DEL_YN", 1, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_COL_GP", 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_BED_NO", 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LYR_NO", 3, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_COLL_SEQ", 2, 2, 'L', 0, 0);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter

} // end of class
