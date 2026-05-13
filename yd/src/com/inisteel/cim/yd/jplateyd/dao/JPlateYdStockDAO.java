/*
 * @(JPlateYdStockDao) 야드저장품 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		야드저장품 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/20   김현우      김현우       최초작성  
 */

package com.inisteel.cim.yd.jplateyd.dao;


import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

import com.inisteel.cim.yd.common.dao.YdCommDAO;
import com.inisteel.cim.yd.common.rule.GetBreRule8;

//-------------------------------------------------------------------------------------------------------------------------
//2024.11.20 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드저장품 DAO
*/

public class JPlateYdStockDAO { 

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdStockDAO.class.getName();
	
	private JPlateYdUtils 		ydUtils     = new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
	
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT [조회조건:STL_NO] , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStock(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStock";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStock
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStock
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStock");

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
	} // end of getYdStock

	/**
	 *      [A] 오퍼레이션명 : 야드저장품조회(야드적치단조인)[조회조건:STL_NO] , intGp == 110
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStockWithLoc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStockWithLoc";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockJoinStkLyr
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithLoc");

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
	} // end of getYdStockWithLoc

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT , intGp == 120
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkBedNotStl(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStkBedNotStl";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedNotStl
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStkBedNotStl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStkBedNotStl");

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
	} // end of getYdStkBedNotStl
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT , intGp == 120
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkBedNotStlForPillingBookIn (JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStkBedNotStlForPillingBookIn ";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedNotStl
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStkBedNotStl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStkBedNotStlForPillingBookIn");

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
	} // end of getYdStkBedNotStlForPillingBookIn 	

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT , intGp == 180
	 *						저장품제원 지정저장품 DEL_YN 비체크
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkBedStlDelCheck(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStkBedStlDelCheck";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStlDelYNNoCheck
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStkBedStlDelCheck
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStkBedStlDelCheck");

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
	} // end of getYdStkBedStlDelCheck

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT , intGp == 26
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkBedStl(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStkBedStl";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStl
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStkBedStl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStkBedStl");

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
	} // end of getYdStkBedStl

	/**
	 *      [A] 오퍼레이션명 : GAS장절단실적 SELECT , intGp == 502
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getGasCutResult(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getGasCutResult";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.rcvpPlateYdGascutresult1
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getGasCutResult
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getGasCutResult");

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
	} //end of getGasCutResult

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT [BOOK-OUT 대상재료 조회]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStockBookOut(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStockBookOut";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockBookOut
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockBookOut");

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
	} //end of getYdStockBookOut

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT [조업공통 정보 포함 조회] , intGp == 132 , 162
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStockWithPRInfo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStockWithPRInfo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabYdStrlocIdInfojl
			//			com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMMbyMSLABNO
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithPRInfo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getYdStockWithPRInfo");

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
	} //end of getYdStockWithPRInfo


	/**
	 *      [A] 오퍼레이션명 : 야드저장품 보수장 적치 가능 베드수 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getOcpyBedCnt(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getOcpyBedCnt";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getOcpyBedCnt
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getOcpyBedCnt");

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
	} //end of getOcpyBedCnt

	/**
	 *      [A] 오퍼레이션명 : 보수대상재 체크 - 품질점검 때문에 추가
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public String getBsEndCheck(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 		= null;
		JDTORecord 		recPara 	= null;

		String 	szMethodName 		= "getBsEndCheck";

		String	szBsEnd				= "Y";
		String	szUS_MAINTMATL		= "";	// 상면보수재
		String	szLS_MAINTMATL		= "";	// 하면보수재
		String	szCPL_WRK_MTL		= "";	// 냉간교정재
		String	szHTTRT_HPL_MTL		= "";	// 열처리교정재
		String	szGAS_WRK_MTL		= "";	// GAS작업재
		String	szSHOT_BLST_WRK_MTL	= "";	// ShortBlast작업재
		String	szPRESS_WRK_MTL		= "";	// 프레스교정재
		String	szGDS_MAIN_GRD		= "";	// 제품주등급

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getBsEndCheck
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getBsEndCheck");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {

				szUS_MAINTMATL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "US_MAINTMATL", 		"N");		// 상면보수재
				szLS_MAINTMATL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "LS_MAINTMATL", 		"N");		// 하면보수재
				szCPL_WRK_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "CPL_WRK_MTL", 		"N");		// 냉간교정재
				szHTTRT_HPL_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "HTTRT_HPL_MTL", 		"N");		// 열처리교정재
				szGAS_WRK_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "GAS_WRK_MTL", 		"N");		// GAS작업재
				szSHOT_BLST_WRK_MTL	= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "SHOT_BLST_WRK_MTL", 	"N");		// ShortBlast작업재
				szPRESS_WRK_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "PRESS_WRK_MTL", 		"N");		// 프레스교정재
				szGDS_MAIN_GRD		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "GDS_MAIN_GRD",		"1");		// 제품주등급

				if ("Y".equals(szUS_MAINTMATL) 	|| 	"Y".equals(szLS_MAINTMATL) 		||
					"Y".equals(szCPL_WRK_MTL) 	||	"Y".equals(szHTTRT_HPL_MTL) 	||
					"Y".equals(szGAS_WRK_MTL) 	||	"Y".equals(szSHOT_BLST_WRK_MTL) ||
					"Y".equals(szPRESS_WRK_MTL) || 	"7".equals(szGDS_MAIN_GRD)
				   ) {

					szBsEnd = "N";

					if ("7".equals(szGDS_MAIN_GRD) || "6".equals(szGDS_MAIN_GRD)) {		// 충당대기재 , 스크랩재
						szBsEnd = szGDS_MAIN_GRD;
					} else if ("Y".equals(szGAS_WRK_MTL)) {								// GAS작업재
						szBsEnd = "G";
					} else if ("Y".equals(szUS_MAINTMATL)) {							// 상면보수
						szBsEnd = "B";
					} else if ("Y".equals(szLS_MAINTMATL)) {							// 하면보수
						szBsEnd = "T";
					} else if ("Y".equals(szCPL_WRK_MTL)) {								// 냉간교정재
						szBsEnd = "C";
					}
				}
			}

			String szMsg = "조회완료 >>>> szBsEnd :: " + szBsEnd;
			ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szBsEnd;
	} //end of getBsEndCheck

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT [저장품제원정보송신용] - 상단부터 5단까지
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getY7YDL002TopLyr(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getY7YDL002TopLyr";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getY7YDL002TopLyr
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getY7YDL002TopLyr");

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
	} //end of getY7YDL002TopLyr

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT [저장품제원정보송신용] - 상단부터 30매
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getY7YDL002TopCnt(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getY7YDL002TopCnt";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getY7YDL002TopCnt
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getY7YDL002TopCnt");

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
	} // end of getY7YDL002TopCnt

/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdStock(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "insYdStock";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.insYdStock
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.insYdStock");

			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.insYdStockBookOut
			//recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.insYdStockBookOut");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) {
				szMsg = "INSERT 처리 실패 (" + Integer.toString(intRtnVal) + ")";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal = -1;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStock

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT [후판정정야드 GAS절단실적 등록]
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdStockCutResult(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "insYdStockCutResult";
		String szMsg            = "";
		int intRtnVal           = 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
						
		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.insYdStockCutResult
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.insYdStockCutResult");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) {
				szMsg = "INSERT 처리 실패 (" + Integer.toString(intRtnVal) + ")";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return intRtnVal = -1;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStockCutResult

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT [BOOK-OUT]
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdStockBookOut(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecordSet outRecSet = null;
		JDTORecord outRec       = null;
		String 	szMethodName    = "insYdStockBookOut";
		String 	szMsg           = "";
		int 	intRtnVal       = 0;
	    String 	sYdMtlWGp       = "";       	// 야드재료폭구분		YD_MTL_W_GP < 4500:S1-소폭 , >=4500:L1-광폭
	    String 	sYdMtlLGp       = "";       	// 야드재료길이구분	YD_MTL_L_GP < 15000:S1-단척, >=1500:L1-장척
		float  	fYdMtlW			= 0.0f;			// 야드재료폭
		int    	iYdMtlL			= 0;			// 야드재료길이

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			// RecordSet Create
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			// 대상정보 조회
			intRtnVal = this.getYdStockBookOut(inRec, outRecSet);
			if (intRtnVal <= 0) {
				szMsg = "INSERT 대상재조회 실패 (" + Integer.toString(intRtnVal) + ")";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return intRtnVal = -1;
			}

			outRecSet.absolute(1);
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setRecord(outRecSet.getRecord());
			outRec.setField("REGISTER", 			ydDaoUtils.paraRecChkNull(inRec, "REGISTER"));
			outRec.setField("MODIFIER", 			ydDaoUtils.paraRecChkNull(inRec, "MODIFIER"));
			outRec.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRec, "STL_NO"));
			outRec.setField("DEL_YN",				"N");
			outRec.setField("BOOK_OUT_RESN",    	ydDaoUtils.paraRecChkNull(inRec, "BOOK_OUT_RESN"));	        // Book-Out원인
			outRec.setField("BOOK_OUT_DATE",    	ydDaoUtils.paraRecChkNull(inRec, "BOOK_OUT_DATE"));         // Book-Out일자
			outRec.setField("BOOK_OUT_PROG",    	ydDaoUtils.paraRecChkNull(inRec, "BOOK_OUT_PROG"));			// Book-Out공정
			outRec.setField("FRTOMOVE_PLANT_GP",	ydDaoUtils.paraRecChkNull(inRec, "FRTOMOVE_PLANT_GP"));		// 북인대상재 구분 (이송공장구분 항목사용)
			outRec.setField("ARR_WLOC_CD",			ydDaoUtils.paraRecChkNull(inRec, "ARR_WLOC_CD"));			// 후판트래킹존지정(PL_TRCK_ZONE_ASG) chito20230202
			outRec.setField("YD_FRTOMOVE_YD_GP",	ydDaoUtils.paraRecChkNull(inRec, "YD_FRTOMOVE_YD_GP"));		// 북아웃모드(PL_BOOK_OUT_MOD) chito20230202
			
			// BRE RULE 적용 - 야드재료폭, 길이구분 Set
			fYdMtlW = (float)ydDaoUtils.paraRecChkNullDouble(outRec, "YD_MTL_W");								// 야드재료폭
			iYdMtlL = ydDaoUtils.paraRecChkNullInt(outRec, "YD_MTL_L");											// 야드재료길이

			JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
	    	boolean bRtnVal = GetBreRule8.getYDB801(JPlateYdConst.YD_GP_F_PLATE_YARD, fYdMtlW, jdtoRcd);
	    	if (bRtnVal) {
	    		sYdMtlWGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_W_GP");
	    	} else {
	    		sYdMtlWGp = "";
	    	}

	    	szMsg = ">>>> iYdMtlL :" + Integer.toString(iYdMtlL);
	    	ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			jdtoRcd = JDTORecordFactory.getInstance().create();
	    	bRtnVal = GetBreRule8.getYDB802(JPlateYdConst.YD_GP_F_PLATE_YARD, iYdMtlL, jdtoRcd);
	    	if (bRtnVal) {
	    		sYdMtlLGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_L_GP");
	    	} else {
	    		sYdMtlLGp = "";
	    	}
			outRec.setField("YD_MTL_W_GP", 	sYdMtlWGp);		// 야드재료폭구분
			outRec.setField("YD_MTL_L_GP", 	sYdMtlLGp);		// 야드재료길이구분

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 outRec에 logId 추가 
			outRec.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

			// 기존자료 존재시 UPDATE 처리
			if ("0".equals(outRec.getFieldString("STOCK_CNT"))) {
				// INSERT 처리
				intRtnVal = this.insYdStock(outRec);
			} else {
				// UPDATE 처리
				intRtnVal = this.updYdStockTX(outRec);
			}

			//execution error return
			if (intRtnVal <= 0) {
				szMsg = "INSERT 처리 실패 (" + Integer.toString(intRtnVal) + ")";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				return intRtnVal = -1;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStockBookOut

/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updYdStock";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";

		try {
			//트렌젝션 분리 적용
			ejbConn = new EJBConnector("default", "JPlateYdStockSeEJB", this);
			iRtn = (Integer)ejbConn.trx("updYdStockReTX", new Class[] { JDTORecord.class }, new Object[] { inRec});
			if (iRtn.intValue() != JPlateYdConst.RETN_INT_SUCCESS.intValue()) {			//성공
				szMsg = "UPDATE 처리 실패 (" + Integer.toString(iRtn.intValue()) + ")";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}

			intRtnVal = 1;

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStockTX(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecordSet outRecSet     = null;
		JDTORecord recInPara        = null;
		JDTORecord recOutPara       = null;
		JDTORecord outRec           = null;
		String szMethodName         = "updYdStockTX";
		String szMsg                = "";
		int intRtnVal               = 0;

		try {
			// RecordSet Create
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			intRtnVal = this.getYdStock(inRec, outRecSet);

			if (intRtnVal < 0) {
				// Parameter Error Return
				szMsg = "Parameter Error!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal;
			} else if(intRtnVal == 0) {
				// Data Not Found Return
				szMsg = "Data Not Found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal;
			} else if(intRtnVal != 1) {
				// Duplicate Data Return
				szMsg = "Duplicate Data!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}

			outRecSet.absolute(1);
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setRecord(outRecSet.getRecord());

			// 필드명 변환 (필드명 -> V_필드명)
			recInPara  = ydDaoUtils.conversionFieldname(inRec, 0);
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);

			// Data Mapping
			this.dataMapping(recInPara, recOutPara);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStock
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStock
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStock");

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
	} // end of updYdStockTX

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE [GAS전단실적]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStockCutResult(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara       = null;
		String szMethodName         = "updYdStockCutResult";
		String szMsg                = "";
		int intRtnVal               = 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
						
		try {
			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStockCutResult
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStockCutResult");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) {
				szMsg = "UPDATE 처리 실패 (" + Integer.toString(intRtnVal) + ")";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				;
				
				return intRtnVal = -1;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockTX

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE [저장품 작업예약정보 수정]
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStockWbook(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "updYdStockWbook";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.updY1YDL012ST
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStockWbook
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStockWbook");

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
	} // end of updYdStockWbook

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE [차행선결정정보 수정]
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updNextDeciInfo(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "updNextDeciInfo";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updNextDeciInfo
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updNextDeciInfo");

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
	} // end of updNextDeciInfo

	/**
	 *      [A] 오퍼레이션명 : 저장품에서 작업예약ID와 스케줄코드 Clear시킴
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updSchCdByYdWbookId(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "updSchCdByYdWbookId";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDelYdWBookId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updSchCdByYdWbookId
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updSchCdByYdWbookId");

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
	} // end of updSchCdByYdWbookId


	/**
	 *      [A] 오퍼레이션명 : 저장품에서 저장위치정보 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStkColInfo(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "updYdStkColInfo";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStkColInfo
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdStkColInfo");

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
	} // end of updYdStkColInfo

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE [보수대상재 :: 품질점검 때문에 추가]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updBsEnd(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara       = null;
		String szMethodName         = "updBsEnd";
		String szMsg                = "";
		int intRtnVal               = 0;

		try {
			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updBsEnd
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updBsEnd");

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
	} // end of updBsEnd

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 긴급재구분 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdUgntGp(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "updYdUgntGp";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {
			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdUgntGp
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdUgntGp");

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
	} // end of updYdUgntGp

	/**
	 *      [A] 오퍼레이션명 : 야드작업계획대차 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdWrkPlanTcar(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "updYdWrkPlanTcar";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {
			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdWrkPlanTcar
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updYdWrkPlanTcar");

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
	} // end of updYdWrkPlanTcar

	/**
	 *      [A] 오퍼레이션명 : 시편채취구분 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updRgntPkGp(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "updRgntPkGp";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {
			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updRgntPkGp
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updRgntPkGp");

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
	} // end of updRgntPkGp

/*------------------------------------- DELETE ----------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 저장품 정보 삭제 처리
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int delYdStock(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "delYdStock";
		String szMsg            = "";
		int intRtnVal           = 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.delYdStock
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.delYdStock");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) {
				szMsg = "DELETE 처리 실패 (" + Integer.toString(intRtnVal) + ")";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return intRtnVal = -1;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		
		return intRtnVal;
	} // end of delYdStock

/*------------------------------------- ETC -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_STL_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WBOOK_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_PTOP_PLNT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_ITEM");
			ydDaoUtils.mappingData(inRec, outRec, "V_ITEMNAME_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_STL_PROG_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_ORD_YEOJAE_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_FRTOMOVE_ORD_DATE");
			ydDaoUtils.mappingData(inRec, outRec, "V_FRTOMOVE_PLANT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_STL_APPEAR_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_PLNT_PROC_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_T");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_W");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_L");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_WT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_W_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_T_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_L_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_COOL_DONE_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_REHEAT_SLAB_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_TRANS_ORD_DATE");
			ydDaoUtils.mappingData(inRec, outRec, "V_TRANS_ORD_SEQNO");
			ydDaoUtils.mappingData(inRec, outRec, "V_CAR_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_CARD_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_ARR_WLOC_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_FRTOMOVE_YD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_FRTOMOVE_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_URGENT_FRTOMOVE_WORD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_FTMV_MEANS_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_MMATL_FEE_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_PLAN_CRN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_PLAN_TCAR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_UPP_LOC_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CURR_STR_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_RCPT_DATE");
			ydDaoUtils.mappingData(inRec, outRec, "V_SNDBK_RSN_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_SNDBK_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_SNDBK_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_SNDBK_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_CAR_LOTID");
			ydDaoUtils.mappingData(inRec, outRec, "V_CAR_LOTID_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DETAIL_ARR_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_FTMV_WRK_CMPL_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_FTMV_WRK_CMPL_DD");
			ydDaoUtils.mappingData(inRec, outRec, "V_BOOK_OUT_RESN");
			ydDaoUtils.mappingData(inRec, outRec, "V_BOOK_OUT_DATE");
			ydDaoUtils.mappingData(inRec, outRec, "V_BOOK_OUT_PROG");
			ydDaoUtils.mappingData(inRec, outRec, "V_US_MAINTMATL");
			ydDaoUtils.mappingData(inRec, outRec, "V_US_MAINT_SCH_MAKE_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_US_MAINT_WRK_CMPL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_LS_MAINTMATL");
			ydDaoUtils.mappingData(inRec, outRec, "V_LS_MAINT_SCH_MAKE_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_LS_MAINT_WRK_CMPL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_CPL_WRK_MTL");
			ydDaoUtils.mappingData(inRec, outRec, "V_CR_CORR_SCH_MAKE_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_CR_CORR_WRK_CMPL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_HTTRT_HPL_MTL");
			ydDaoUtils.mappingData(inRec, outRec, "V_HTTRT_CORR_SCH_MAKE_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_HTTRT_CORR_WRK_CMPL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_GAS_WRK_MTL");
			ydDaoUtils.mappingData(inRec, outRec, "V_GAS_WRK_SCH_MAKE_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_GAS_WRK_WRK_CMPL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_SHOT_BLST_WRK_MTL");
			ydDaoUtils.mappingData(inRec, outRec, "V_S_BLST_WRK_SCH_MAKE_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_S_BLST_WRK_WRK_CMPL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_PRESS_WRK_MTL");
			ydDaoUtils.mappingData(inRec, outRec, "V_PRS_CORR_SCH_MAKE_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_PRS_CORR_WRK_CMPL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_PL_WR_PRSNT_PROC_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UGNT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UGNT_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UGNT_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_RGNT_PK_GP");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
	} // end of dataMapping

	/**********************************************************
	* 1후판정정추가 SJH16 
	**********************************************************/
	
	/**
	 *      [A] 오퍼레이션명 : 1후판 정정 야드저장품 INSERT [BOOK-OUT]
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdStockBookOutYdP(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecordSet outRecSet = null;
		JDTORecord outRec       = null;
		String 	szMethodName    = "insYdStockBookOutYdP";
		String 	szMsg           = "";
		int 	intRtnVal       = 0;
	    String 	sYdMtlWGp       = "";       	// 야드재료폭구분		YD_MTL_W_GP < 4500:S1-소폭 , >=4500:L1-광폭
	    String 	sYdMtlLGp       = "";       	// 야드재료길이구분	YD_MTL_L_GP < 15000:S1-단척, >=1500:L1-장척
		float  	fYdMtlW			= 0.0f;			// 야드재료폭
		int    	iYdMtlL			= 0;			// 야드재료길이

		try {
			// RecordSet Create
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			// 대상정보 조회
			intRtnVal = this.getYdStockBookOut(inRec, outRecSet);
			if (intRtnVal <= 0) {
				szMsg = "INSERT 대상재조회 실패 (" + Integer.toString(intRtnVal) + ")";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal = -1;
			}

			outRecSet.absolute(1);
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setRecord(outRecSet.getRecord());
			outRec.setField("REGISTER", 			ydDaoUtils.paraRecChkNull(inRec, "REGISTER"));
			outRec.setField("MODIFIER", 			ydDaoUtils.paraRecChkNull(inRec, "MODIFIER"));
			outRec.setField("STL_NO", 				ydDaoUtils.paraRecChkNull(inRec, "STL_NO"));
			outRec.setField("DEL_YN",				"N");
			outRec.setField("BOOK_OUT_RESN",    	ydDaoUtils.paraRecChkNull(inRec, "BOOK_OUT_RESN"));	        // Book-Out원인
			outRec.setField("BOOK_OUT_DATE",    	ydDaoUtils.paraRecChkNull(inRec, "BOOK_OUT_DATE"));         // Book-Out일자
			outRec.setField("BOOK_OUT_PROG",    	ydDaoUtils.paraRecChkNull(inRec, "BOOK_OUT_PROG"));			// Book-Out공정
			outRec.setField("FRTOMOVE_PLANT_GP",	ydDaoUtils.paraRecChkNull(inRec, "FRTOMOVE_PLANT_GP"));		// 북인대상재 구분 (이송공장구분 항목사용)

			// BRE RULE 적용 - 야드재료폭, 길이구분 Set
			fYdMtlW = (float)ydDaoUtils.paraRecChkNullDouble(outRec, "YD_MTL_W");								// 야드재료폭
			iYdMtlL = ydDaoUtils.paraRecChkNullInt(outRec, "YD_MTL_L");											// 야드재료길이

			JDTORecord jdtoRcd = JDTORecordFactory.getInstance().create();
	    	boolean bRtnVal = GetBreRule8.getYDB801(JPlateYdConst.YD_GP_P_PLATE_YARD, fYdMtlW, jdtoRcd);
	    	if (bRtnVal) {
	    		sYdMtlWGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_W_GP");
	    	} else {
	    		sYdMtlWGp = "";
	    	}

	    	szMsg = ">>>> iYdMtlL :" + Integer.toString(iYdMtlL);
	    	ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			jdtoRcd = JDTORecordFactory.getInstance().create();
	    	bRtnVal = GetBreRule8.getYDB802(JPlateYdConst.YD_GP_P_PLATE_YARD, iYdMtlL, jdtoRcd);
	    	if (bRtnVal) {
	    		sYdMtlLGp = ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_MTL_L_GP");
	    	} else {
	    		sYdMtlLGp = "";
	    	}
			outRec.setField("YD_MTL_W_GP", 	sYdMtlWGp);		// 야드재료폭구분
			outRec.setField("YD_MTL_L_GP", 	sYdMtlLGp);		// 야드재료길이구분

			// 기존자료 존재시 UPDATE 처리
			if ("0".equals(outRec.getFieldString("STOCK_CNT"))) {
				// INSERT 처리
				intRtnVal = this.insYdStock(outRec);
			} else {
				// UPDATE 처리
				intRtnVal = this.updYdStockTX(outRec);
			}

			//execution error return
			if (intRtnVal <= 0) {
				szMsg = "INSERT 처리 실패 (" + Integer.toString(intRtnVal) + ")";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return intRtnVal = -1;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStockBookOut
	
	/**
	 *      [A] 오퍼레이션명 : 1후판정정야드 보수대상재 체크 - 강력교정재포함
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public String getBsEndCheckYdP(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 		= null;
		JDTORecord 		recPara 	= null;
		JDTORecord 		recYN 		= null; // YN_flag
		JDTORecordSet 	rsYN 		= null; // YN_flag
		String 			szYNFlag    = null; // YN_flag
		
		String 	szMethodName 		= "getBsEndCheckYdP";

		String	szBsEnd				= "Y";
		String	szUS_MAINTMATL		= "";	// 상면보수재
		String	szLS_MAINTMATL		= "";	// 하면보수재
		String	szCPL_WRK_MTL		= "";	// 냉간교정재
		String	szHTTRT_HPL_MTL		= "";	// 열처리교정재
		String	szGAS_WRK_MTL		= "";	// GAS작업재
		String	szSHOT_BLST_WRK_MTL	= "";	// ShortBlast작업재
		String	szPRESS_WRK_MTL		= "";	// 프레스교정재
		String	szGDS_MAIN_GRD		= "";	// 제품주등급
		String  szSCPL_WRK_MTL		= "";	// 강력교정재**
		String  szQA_ASGN_MTL		= "";	// QA지정재***
		String  szUT_WAIT_MTL		= "";	// UT대기재***

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.getBsEndCheck");
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getBsEndCheck2");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {

				szUS_MAINTMATL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "US_MAINTMATL", 		"N");		// 상면보수재
				szLS_MAINTMATL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "LS_MAINTMATL", 		"N");		// 하면보수재
				szCPL_WRK_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "CPL_WRK_MTL", 		"N");		// 냉간교정재
				szHTTRT_HPL_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "HTTRT_HPL_MTL", 		"N");		// 열처리교정재
				szGAS_WRK_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "GAS_WRK_MTL", 		"N");		// GAS작업재
				szSHOT_BLST_WRK_MTL	= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "SHOT_BLST_WRK_MTL", 	"N");		// ShortBlast작업재
				szPRESS_WRK_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "PRESS_WRK_MTL", 		"N");		// 프레스교정재
				szGDS_MAIN_GRD		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "GDS_MAIN_GRD",		"1");		// 제품주등급
				szSCPL_WRK_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "SCPL_WRK_MTL", 		"N");		// 강력교정재**
				szQA_ASGN_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "QA_ASGN_MTL", 		"N");		// QA지정재***
				szUT_WAIT_MTL		= ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "UT_WAIT_MTL", 		"N");		// UT대기재***

				if ("Y".equals(szUS_MAINTMATL) 	|| 	"Y".equals(szLS_MAINTMATL) 		||
					"Y".equals(szCPL_WRK_MTL) 	||	"Y".equals(szHTTRT_HPL_MTL) 	||
					"Y".equals(szGAS_WRK_MTL) 	||	"Y".equals(szSHOT_BLST_WRK_MTL) ||
					"Y".equals(szPRESS_WRK_MTL) || 	"7".equals(szGDS_MAIN_GRD)		||
					"Y".equals(szSCPL_WRK_MTL)  ||
					"Y".equals(szQA_ASGN_MTL)  ||
					"Y".equals(szUT_WAIT_MTL)  
				   ) {

					szBsEnd = "N";

					// flag 임시처리 BO 20200915
					recYN = ydDaoUtils.conversionFieldname(inRec, 0);
					recYN.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getBsEndCheckYdP.YNflag");
					rsYN = dbAssDao.getRecordSet(recYN);
					szYNFlag = ydDaoUtils.paraRecChkNull(rsYN.getRecord(0), "YN_FLAG", 		"N");		// YN_flag

					if("Y".equals(szYNFlag)){
						if ("7".equals(szGDS_MAIN_GRD) || "6".equals(szGDS_MAIN_GRD)) {		// 충당대기재(7) , 스크랩재(6)
							szBsEnd = szGDS_MAIN_GRD;
						} else if ("Y".equals(szGAS_WRK_MTL)) {								// GAS작업재
							szBsEnd = "G";
						} else if ("Y".equals(szPRESS_WRK_MTL)) {							// 프레스교정재
							szBsEnd = "P";  //N 에서 분리
						} else if ("Y".equals(szUT_WAIT_MTL)) {								// UT대기재***
							szBsEnd = "U";
						} else if ("Y".equals(szUS_MAINTMATL)) {							// 상면보수
							szBsEnd = "B";
						} else if ("Y".equals(szLS_MAINTMATL)) {							// 하면보수
							szBsEnd = "T";
						} else if ("Y".equals(szCPL_WRK_MTL)) {								// 냉간교정재
							szBsEnd = "C";
						} else if ("Y".equals(szSHOT_BLST_WRK_MTL)) {						// ShortBlast작업재
							szBsEnd = "A";  //N 에서 분리
						} else if ("Y".equals(szHTTRT_HPL_MTL)) {							// 열처리교정재
							szBsEnd = "H";  //N 에서 분리
						} else if ("Y".equals(szSCPL_WRK_MTL)) {							// 강력교정재**
							szBsEnd = "S";
						} else if ("Y".equals(szQA_ASGN_MTL)) {								// QA지정재***
							szBsEnd = "Q";
						}
					} else{
						if ("7".equals(szGDS_MAIN_GRD) || "6".equals(szGDS_MAIN_GRD)) {		// 충당대기재(7) , 스크랩재(6)
					 		szBsEnd = szGDS_MAIN_GRD;
					 	} else if ("Y".equals(szGAS_WRK_MTL)) {								// GAS작업재
					 		szBsEnd = "G";
					 	} else if ("Y".equals(szUS_MAINTMATL)) {							// 상면보수
					 		szBsEnd = "B";
					 	} else if ("Y".equals(szLS_MAINTMATL)) {							// 하면보수
					  		szBsEnd = "T";
					  	} else if ("Y".equals(szCPL_WRK_MTL)) {								// 냉간교정재
					  		szBsEnd = "C";
					  	} else if ("Y".equals(szSCPL_WRK_MTL)) {							// 강력교정재**
					  		szBsEnd = "S";
					  	} else if ("Y".equals(szQA_ASGN_MTL)) {								// QA지정재***
					  		szBsEnd = "Q";
					  	} else if ("Y".equals(szUT_WAIT_MTL)) {								// UT대기재***
					  		szBsEnd = "U";
					  	} else if ("Y".equals(szHTTRT_HPL_MTL)) {							// 열처리교정재
					  		szBsEnd = "H";  //N 에서 분리
					  	} else if ("Y".equals(szSHOT_BLST_WRK_MTL)) {						// ShortBlast작업재
					  		szBsEnd = "A";  //N 에서 분리
					  	} else if ("Y".equals(szPRESS_WRK_MTL)) {							// 프레스교정재
					  		szBsEnd = "P";  //N 에서 분리
					  	}
					}
				}
			}

			String szMsg = "조회완료 >>>> szBsEnd :: " + szBsEnd;
			ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szBsEnd;
	} //end of getBsEndCheckYdP
	
	/**
	 *      [A] 오퍼레이션명 : 1후판정정야드 야드저장품 UPDATE [차행선결정정보 수정]
	 *
	 * @param  JDTORecord inRec parameter record

	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updNextDeciInfoYdP(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord recOutPara	= null;
		String szMethodName     = "updNextDeciInfoYdP";
		String szMsg            = "";
		int intRtnVal           = 0;

		try {

			// 필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO.updNextDeciInfo
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.updNextDeciInfo2");

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
	} // end of updNextDeciInfoYdP	
	
	/**
	 *      [A] 오퍼레이션명 : 1후판정정야드 야드저장품조회(야드적치단조인)[조회조건:STL_NO] , intGp == 110
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStockWithLocYdP(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStockWithLocYdP";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdStockWithLoc2");

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
	} //end of getYdStockWithLocYdP
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 1후판정정야드 RT SPEED 조회[조회조건:STL_NO] 
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getRtSpeedByStlNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getRtSpeedByStlNo";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getYdRtSpeedByStlNo");

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
	} //end of getRtSpeedByStlNo	
	
} // end of class
