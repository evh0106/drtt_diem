/*
 * @(#) 야드적치BED DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		야드적치BED DAO
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
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;

//-------------------------------------------------------------------------------------------------------------------------
//2024.11.20 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드적치BED DAO
 *
*/

public class JPlateYdStkBedDAO {

	// Dao NameszQueryIdGet1
	private static final String SZ_DAO_NAME = JPlateYdStkBedDAO.class.getName();

	private JPlateYdUtils    	ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();

/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT [PK로 조회] , intGp == 1
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkbed(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getYdStkbed";
		int 	intRtnVal 		= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbed");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		return intRtnVal;
	} // end of getYdStkbed

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT , intGp == 5
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkbedYdStkColGpBed(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getYdStkbedYdStkColGpBed";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYdStkColGpBed
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbedYdStkColGpBed
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbedYdStkColGpBed");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getYdStkbedYdStkColGpBed

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT [야드 L2 저장위치제원 송신용]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYDY7L001Info(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getYDY7L001Info";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYDY7L001Info
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYDY7L001Info");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getYDY7L001Info
	
	/**
	 *      [A] 오퍼레이션명 : 차량상차 크레인작업 SELECT [야드 L2 크레인작업메세지 송신용]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYDY2L007TrInfo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getYDY2L007TrInfo";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYDY7L001Info
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYDY2L007TrInfo");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getYDY2L007TrInfo

	/**
	 *      [A] 오퍼레이션명 : 야드적치Bed SELECT [구내운송 차량이 정지한 적치열 조회], intGp == 9
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkbedByTrnEqpCd(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getYdStkbedByTrnEqpCd";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandTrnEqpCd
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbedByTrnEqpCd
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbedByTrnEqpCd");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getYdStkbedByTrnEqpCd

	/**
	 *      [A] 오퍼레이션명 : 야드적치Bed SELECT [출하 차량이 정지한 적치열 조회], intGp == 20
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkbedByCarNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getYdStkbedByCarNo";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandCarNoCardNo_PIDEV
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbedByCarNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkbedByCarNo");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getYdStkbedByCarNo

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT , intGp == 24
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkBedAnalysis(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getYdStkBedAnalysis";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkBedAnalysis1
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkBedAnalysis
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getYdStkBedAnalysis");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getYdStkBedAnalysis

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT [길이구분/폭구분이 동일한 적치가능한 공베드 조회] , intGp == 25
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getSameLWGp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getSameLWGp";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAsc
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGp");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getSameLWGp

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT [길이구분/폭구분이 동일한 적치가능한 설비의 공베드 조회]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getSameLWGpBookIn(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getSameLWGpBookIn";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpBookIn
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpBookIn");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getSameLWGpBookIn

	/**
	 *      [A] 오퍼레이션명 : 가스장 적치가능한 공베드 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getSameLWGpBookInCnc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getSameLWGpBookInCnc";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpBookInCnc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpBookInCnc");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getSameLWGpBookInCnc

	/**
	 *      [A] 오퍼레이션명 : 길이구분/폭구분이 동일한 적치가능한 공베드 조회 , intGp == 27
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getSameLWGpColForAidWrk(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getSameLWGpColForAidWrk";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscForAidWrk
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpColForAidWrk
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpColForAidWrk");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getSameLWGpColForAidWrk

	/**
	 *      [A] 오퍼레이션명 : 저장위치 변경시 사용 - TO위치의 정보를 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getStkLocModChk(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getStkLocModChk";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getStkLocModChk
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getStkLocModChk");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getStkLocModChk


	/**
	 *      [A] 오퍼레이션명 : 저장위치 변경시 사용 - 끼워넣기 대상재를 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getStkLocModWithIns(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getStkLocModWithIns";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getStkLocModWithIns
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getStkLocModWithIns");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getStkLocModWithIns

	/**
	 *      [A] 오퍼레이션명 : 대차위의 재료정보 조회 - 대차도착처리시 사용
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getTcarMtlWithWBookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getTcarMtlWithWBookId";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getTcarMtlWithWBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getTcarMtlWithWBookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getTcarMtlWithWBookId

/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdStkbed(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChkField = false;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnChkField = this.chkParameter(recPara);

			//parameter error return
			if (!blnChkField){
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.insYdStkbed
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.insYdStkbed
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.insYdStkbed");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStkbed

/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed UPDATE
	 *
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStkbed(JDTORecord inRec) throws DAOException, JDTOException {

		int 	intRtnVal 		= 0;

	// SELECT 하여 전체항목 UPDATE 를 해당 항목만 UPDATE하도록 메서드를 세분화 ... 주석처리
	// 2013.05.28 야드Map관리 화면에서 사용함으로 다시 주석 제거

		String 	szMethodName 	= "updYdStkbed";
		String 	szMsg 			= null;

		JDTORecord outRec 		= null;
		boolean blnChkField 	= true;

		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			//변환용 레코드
			JDTORecord recInPara = null;
			JDTORecord recOutPara = null;

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//update data select
			intRtnVal = this.getYdStkbed(inRec, outRecSet);

			//parameter error return
			if (intRtnVal < 0) {
				szMsg = "parameter error!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return intRtnVal;
			}

			//data not found return
			if (intRtnVal == 0) {
				szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return intRtnVal;
			}

			if (outRecSet.size() != 1) {
				szMsg = "duplicate data!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
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
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.updYdStkbed
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.updYdStkbed");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		return intRtnVal;
	} // end of updYdStkbed

	/**
	 *      [A] 오퍼레이션명 : 적치베드 활성화 상태 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStkBedActStat(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.updYdStkBedActStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.updYdStkBedActStat");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {

			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkBedActStat

/*------------------------------------- DELETE -------------------------------------------*/

/*------------------------------------- ETC    -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord recPara) throws JDTOException  {

		boolean blnErr = true;

		try {
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_GP", 6, 1, 'S', 0, 0);			// 야드적치열구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_NO", 2, 1, 'S', 0, 0);			// 야드적치Bed번호
			if (!blnErr) { return blnErr; }

//			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STR_GTR_CD", 6, 1, 'S', 0, 0);			// 야드저장집합코드
//			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_REGISTER", 10, 2, 'S', 0, 0);				// 등록자
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_REG_DDTT", 0, 3, 'S', 0, 0);				// 등록일시
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_TP", 2, 2, 'S', 0, 0);			// 야드적치BedType
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_L_GP", 1, 2, 'S', 0, 0);		// 야드적치Bed길이구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_W_GP", 1, 2, 'S', 0, 0);		// 야드적치Bed폭구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_DIR_GP", 1, 2, 'S', 0, 0);		// 야드적치Bed방향구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_ACT_STAT", 1, 1, 'S', 0, 0);	// 야드적치Bed활성상태
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_WHIO_STAT", 1, 2, 'S', 0, 0);	// 야드적치Bed입출고상태
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_USG_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_XAXIS", 7, 2, 'L', 0, 0);		// 야드적치BedX축
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_YAXIS", 5, 2, 'L', 0, 0);		// 야드적치BedY축
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_ZAXIS", 5, 2, 'L', 0, 0);		// 야드적치BedZ축
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_LYR_MAX", 3, 2, 'L', 0, 0);		// 야드적치Bed단Max
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_WT_MAX", 7, 2, 'L', 0, 0);		// 야드적치Bed중량Max
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_H_MAX", 5, 2, 'L', 0, 0);		// 야드적치Bed높이Max
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_L_MAX", 5, 2, 'L', 0, 0);		// 야드적치Bed길이Max
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_W_MAX", 6, 2, 'D', 4, 1);		// 야드적치Bed폭Max
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_XAXIS_TOL", 5, 2, 'L', 0, 0);	// 야드적치BedX축허용오차
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_BED_YAXIS_TOL", 4, 2, 'L', 0, 0);	// 야드적치BedY축허용오차
			if (!blnErr) { return blnErr; }

//			blnErr = ydDaoUtils.chkField(recPara, "V_YD_L_S_GRP_GP", 1, 2, 'S', 0, 0);			// 야드길이소그룹구분
//			if (!blnErr) { return blnErr; }

//			blnErr = ydDaoUtils.chkField(recPara, "V_YD_COIL_OUTDIA_GRP_GP", 1, 2, 'S', 0, 0);	// 야드코일외경군구분

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkPara_YdStkbed

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_GP");			// 야드적치열구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_NO");			// 야드적치Bed번호
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STR_GTR_CD");			// 야드저장집합코드
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");				// 등록자
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");				// 등록일시
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");				// 수정자
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");				// 수정일
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");					// 삭제유무
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_TP");			// 야드적치BedType
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_L_GP");			// 야드적치Bed길이구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_W_GP");			// 야드적치Bed폭구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_DIR_GP");		// 야드적치Bed방향구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_ACT_STAT");		// 야드적치Bed활성상태
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_WHIO_STAT");	// 야드적치Bed입출고상태
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_USG_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_XAXIS");		// 야드적치BedX축
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_YAXIS");		// 야드적치BedY축
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_ZAXIS");		// 야드적치BedZ축
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_LYR_MAX");		// 야드적치Bed단Max
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_WT_MAX");		// 야드적치Bed중량Max
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_H_MAX");		// 야드적치Bed높이Max
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_L_MAX");		// 야드적치Bed길이Max
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_W_MAX");		// 야드적치Bed폭Max
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_XAXIS_TOL");	// 야드적치BedX축허용오차
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_YAXIS_TOL");	// 야드적치BedY축허용오차
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_L_S_GRP_GP");			// 야드길이소그룹구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_COIL_OUTDIA_GRP_GP");	// 야드코일외경군구분

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
	} // end of dataMapping
	
	
	
	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	 	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT [길이구분/폭구분이 동일한 적치가능한 설비의 공베드 조회]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getSameLWGpBookInYdP(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getSameLWGpBookInYdP";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpBookIn
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpBookInYdP");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getSameLWGpBookInYdP

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT [길이구분/폭구분이 동일한 적치가능한 공베드 조회] , intGp == 25
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getSameLWGpYdP(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getSameLWGpYdP";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAsc
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpYdP");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getSameLWGp
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT [길이구분/폭구분이 동일한 적치가능한 공베드 조회] , intGp == 25 용도구분 없을 때
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getSameLWGpYdP2(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String	szMethodName	= "getSameLWGpYdP";
		int 	intRtnVal 		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAsc
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkBedDAO.getSameLWGpYdP2");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

		String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
		ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		return intRtnVal;
	} // end of getSameLWGp	
	
} // end of class
