/*
 * @(#) 야드크레인작업재료 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/20
 *
 * @description		야드크레인작업재료 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/20   김현우      김현우       최초작성 
 */

package com.inisteel.cim.yd.jplateyd.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordSet;


import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드크레인작업재료 DAO
 *
*/

public class JPlateYdCrnWrkMtlDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdCrnWrkMtlDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();
	private YdPICommDAO	   		ydPICommDAO   = new YdPICommDAO();
	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
	
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT , PK로 조회 , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnWrkMtl(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnWrkMtl";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtl");

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
	} //end of getYdCrnWrkMtl

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT , intGp == 1
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByYdCrnSchId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdCrnSchId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlID
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getByYdCrnSchId_PIDEV

			//PIDEV
			String queryId = "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getByYdCrnSchId_PIDEV";
//			queryId = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", queryId, "APPPI0", "*", "*" );
			recPara.setField("JSPEED_QUERY_ID", queryId);
			
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
	} //end of getByYdCrnSchId

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT [삭제정보 포함]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByYdCrnSchIdWithDel(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdCrnSchIdWithDel";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getByYdCrnSchIdWithDel
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getByYdCrnSchIdWithDel");

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
	} //end of getByYdCrnSchIdWithDel

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT [실제권하한 재료만 (파라메터로 넘어온 값)]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByYdCrnSchIdWithDel2(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdCrnSchIdWithDel2";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getByYdCrnSchIdWithDel
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.getByYdCrnSchIdWithDel2");

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
	} //end of getByYdCrnSchIdWithDel
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT , intGp == 6
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnWrkMtlBySchId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnWrkMtlBySchId";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlBySchId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtlBySchId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getYdCrnWrkMtlBySchId");

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
	} //end of getYdCrnWrkMtlBySchId

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT [크레인작업재료의 길이가 긴 순서로 정렬] , intGp == 16
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getSortStlLengthDesc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getSortStlLengthDesc";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlStlLengthDesc
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSortStlLengthDesc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSortStlLengthDesc");

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
	} //end of getSortStlLengthDesc

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT [크레인작업재료의 폭이 긴 순서로 정렬] , intGp == 20
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getSortStlWidthDesc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getSortStlWidthDesc";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlStlWidthDesc
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSortStlWidthDesc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSortStlWidthDesc");

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
	} //end of getSortStlWidthDesc

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT : 다른작업예약의 크레인스케줄작업재료로 등록되어 있는 지를 조회 , intGp == 18
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return String
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public String getExistByWbookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getExistByWbookId";
		String 	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMsg 			= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                    	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlNWrkbookmtlByWbookId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getExistByWbookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getExistByWbookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[" + szMethodName + "] 존재합니다. - 대상재[" + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szMsg = "[" + szMethodName + "] 존재하지 않습니다.";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				szRtnMsg = JPlateYdConst.RETN_CD_NOTEXIST;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szRtnMsg;
	} //end of getExistByWbookId

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT : 크레인스케줄의 작업재료로 등록되어 있는 지 조회 , intGp == 19
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return String
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public String getSaveByWbookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara	= null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getSaveByWbookId";
		String 	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMsg 			= null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlAtUpperLayerByWbookId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSaveByWbookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSaveByWbookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "["+szMethodName+"] 존재합니다. - 대상재["+Integer.toString(intRtnVal)+"]";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			} else {
				szMsg = "["+szMethodName+"] 존재하지 않습니다.";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				szRtnMsg = JPlateYdConst.RETN_CD_NOTEXIST;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szRtnMsg;
	} //end of getSaveByWbookId

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT , intGp == 17
	 *			같은 작업예약의 해당크레인스케줄보다 다음 크레인스케줄중에서 해당 재료가 크레인작업재료로 존재하는 지 조회
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return String
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public String getGreaterThanCrnSch(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara	= null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getGreaterThanCrnSch";
		String 	szRtnMsg		= JPlateYdConst.RETN_CD_SUCCESS;
		String 	szMsg 			= null;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
  		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

  		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		      						
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlGreaterThanCrnSch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getGreaterThanCrnSch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getGreaterThanCrnSch");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[" + szMethodName + "] 존재합니다. - 대상재[" + Integer.toString(intRtnVal) + "]";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szMsg = "[" + szMethodName + "] 존재하지 않습니다.";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				szRtnMsg = JPlateYdConst.RETN_CD_NOTEXIST;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szRtnMsg;
	} // end of getGreaterThanCrnSch

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT [저장위치 포함]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByYdCrnSchIdWithLoc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdCrnSchIdWithLoc";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getByYdCrnSchIdWithLoc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getByYdCrnSchIdWithLoc");

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
	} //end of getByYdCrnSchIdWithLoc

	/**
	 *      [A] 오퍼레이션명 : 크레인 작업재료의 높이합 조회 , intGp == 8
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getSumMtlByYdCrnSchId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdCrnSchIdWithLoc";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlByCrnSchIDOrdStkLyrNo
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSumMtlByYdCrnSchId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getSumMtlByYdCrnSchId");

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
	} //end of getSumMtlByYdCrnSchId

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT , (조회조건 : 크레인스케줄ID , 재료번호)
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getBySchIdStlNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getBySchIdStlNo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getBySchIdStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.getBySchIdStlNo");

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
	} //end of getBySchIdStlNo

/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdCrnWrkMtl(JDTORecord inRec) throws DAOException, JDTOException {
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
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.insYdCrnwrkmtl
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.insYdCrnWrkMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.insYdCrnWrkMtl");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdCrnWrkMtl

	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료  MERGE [강제권상요구 크레인작업재료 등록]
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int mergeYdCrnWrkMtl(JDTORecord inRec) throws DAOException, JDTOException {
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
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.mergeYdCrnWrkMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.mergeYdCrnWrkMtl");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of mergeYdCrnWrkMtl

/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 UPDATE_YD_SCH_CD[K]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updByYdCrnSchId(JDTORecord inRec) throws DAOException, JDTOException {
		int 	intRtnVal 	= 0;
		boolean blnChkField = true;
		JDTORecord recPara 	= null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = this.chkParameterUpd(recPara);

			//parameter error return
			if (!blnChkField){
				return intRtnVal = -2;
			}

			// 쿼리 아이디 세팅
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.updYdCrnwrkmtlYdCrnSchId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.updByYdCrnSchId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.updByYdCrnSchId");

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

	} // end of updByYdCrnSchId

/*------------------------------------- DELETE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 삭제처리
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int delYdCrnWrkMtl(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.delYdCrnWrkMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnWrkMtlDAO.delYdCrnWrkMtl");

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

	} // end of delYdCrnWrkMtl

/*------------------------------------- ETC    -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws DAOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws DAOException  {

		boolean blnErr = true;

		try {

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_SCH_ID", 18, 1, 'S', 0, 0);
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

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_AID_WRK_YN", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LYR_NO", 3, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LOT_TP", 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LOT_CD", 18, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_HCR_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_STL_PROG_CD", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_MTL_ITEM", 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_ROUTE_GP", 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_TO_LOC_DCSN_MTD", 1, 2, 'S', 0, 0);


		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter

	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 parameter Check
	 *      V_MODIFIER, V_DEL_YN, V_YD_WBOOK_ID
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws DAOException
	 */
	public boolean chkParameterUpd(JDTORecord inRec) throws DAOException  {

		boolean blnErr = true;

		try {
			blnErr = ydDaoUtils.chkField(inRec, "V_MODIFIER", 10, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_DEL_YN", 1, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_SCH_ID", 18, 1, 'S', 0, 0);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter


	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws DAOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws DAOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_SCH_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_STL_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_AID_WRK_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LYR_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LOT_TP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LOT_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_HCR_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_STL_PROG_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MTL_ITEM");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_ROUTE_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TO_LOC_DCSN_MTD");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

	} // end of dataMapping

} // end of class
