/*
 * @(#) 크레인스케줄 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		크레인스케줄 DAO
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
//2024.12.06 1후판 정정 로그 관련 야드공통 UTIL  
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 크레인스케줄 DAO
 *
 */

public class JPlateYdCrnSchDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdCrnSchDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
		
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄ID 조회 [SEQ 생성] , intGp == 9
	 *
	 * @param  none
	 * @return String                   [스케쥴ID]
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public String getSeqId() throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		String 	szSeqId 		= null;
		String 	szMethodName 	= "getSeqId";

		try {

			recPara = JDTORecordFactory.getInstance().create();

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYD_CRN_SCH_ID
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getSeqId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getSeqId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				szSeqId = ydDaoUtils.paraRecChkNull(rsTemp.getRecord(0), "YD_CRN_SCH_ID");
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
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [YD_CRN_SCH_ID] , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnSch(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnSch";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch");

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
	} // end of getYdCrnSch

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [YD_CRN_SCH_ID] , intGp == 20
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnWorkMgt(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnWorkMgt";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 크레인작업관리 데이타 조회
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschEqp2_PAGE
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWorkMgt
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWorkMgt");

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
	} //end of getYdCrnWorkMgt

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT , intGp == 41
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnWrkMtlNext(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStkBedNotStl";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 크레인스케쥴ID와 작업진행상태로 크레인관련 데이터와 NEXT크레인스케쥴을 조회 : N건
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtlNext
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtlNext");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	} //end of getYdCrnWrkMtlNext

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [크레인리스케줄작업취소용] , intGp == 46
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdWrkProgStat(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStkBedNotStl";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdEqpIdYdWrkProgStat
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdWrkProgStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdWrkProgStat");

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
	} //end of getYdWrkProgStat

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [V_YD_EQP_ID] , intGp == 16
	 *						현재 작업중인 크레인 작업지시 조회  YD_WRK_PROG_STAT IN ('1','2','3')
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getWrkProgStat(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnsch";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getWrkProgStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getWrkProgStat");

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
	} // end of getWrkProgStat


	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [V_YD_EQP_ID] , intGp == 55
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getSchCdEqpId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnsch";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschSCHCDEQPID
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getSchCdEqpId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getSchCdEqpId");

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
	} //end of getSchCdEqpId

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT , intGp == 15
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnSchEqpIdPrior(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnsch";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdPrior
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSchEqpIdPrior
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSchEqpIdPrior");

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
	} //end of getYdCrnSchEqpIdPrior

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT , intGp == 3
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
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtl
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtl");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [권하위치 단 역순으로 조회]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnWrkMtlDesc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnWrkMtlDesc";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtl
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtlDesc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnWrkMtlDesc");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	} //end of getYdCrnWrkMtlDesc

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT , intGp == 28
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByWrkId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByWrkId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByWrkId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByWrkId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByWrkId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	} //end of getByWrkId

	/**
	 *      [A] 오퍼레이션명 : 권상위치, 좌표값, 이적 재료 등을 Check
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getOffCrnUpBed(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getOffCrnUpBed";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL012BL
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getOffCrnUpBed
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getOffCrnUpBed");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	} //end of getOffCrnUpBed

	/**
	 *      [A] 오퍼레이션명 : 강제권상요구 설비상태 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getOffCrnUpStat(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getOffCrnUpStat";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.slabyd.dao.SlabYdL2RcvDAO.getY1YDL012ES
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getOffCrnUpStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getOffCrnUpStat");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	} //end of getOffCrnUpStat

	/**
	 *      [A] 오퍼레이션명 : 스케줄 ID에 포함된 작업예약ID에 삭제되지않은 모든 스케줄을 재조회 , intGp == 36
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getCheckYdCrnSchId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getCheckYdCrnSchId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getCheckYdCrnSchId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getCheckYdCrnSchId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	} //end of getCheckYdCrnSchId

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [스케줄에 포함된 동일 작업예약] , intGp == 5
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByYdCrnSchIdOver(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdCrnSchIdOver";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschCrnIdOVERID
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdCrnSchIdOver
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdCrnSchIdOver");

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
	} //end of getByYdCrnSchIdOver

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [조회조건:YD_SCH_CD] , intGp == 6
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByYdSchCd(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdSchCd";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschSCHCD
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdSchCd
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdSchCd");

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
	} //end of getByYdSchCd

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [조회조건:YD_WBOOK_ID,YD_WRK_PROG_STAT] , intGp == 23
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByYdWBookIdStat(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdWBookIdStat";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnSchByWBookId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdWBookIdStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdWBookIdStat");

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
	} //end of getByYdWBookIdStat

	/**
	 *      [A] 오퍼레이션명 : 강제권하 - 권하위치, 좌표값을 Check
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getOffCrnDnBed(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getOffCrnDnBed";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getOffCrnDnBed
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getOffCrnDnBed");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	} //end of getOffCrnDnBed

	/**
	 *      [A] 오퍼레이션명 : 해당 작업지시 상단에 크레인 작업 예약 Check [명령선택시 하단부터 선택 안되도록 체크]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getUpLyrCrnSchExist(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getUpLyrCrnSchExist";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getUpLyrCrnSchExist
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getUpLyrCrnSchExist");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			}else {
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
	} // end of getUpLyrCrnSchExist

	/**
	 *      [A] 오퍼레이션명 : 재료번호로 크레인작업지시 존재여부 체크
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getExistByStlNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getExistByStlNo";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
										
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getExistByStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getExistByStlNo");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			}else {
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
	} //end of getExistByStlNo

	/**
	 *      [A] 오퍼레이션명 : 작업예약ID로 크레인스케줄 SELECT [조회조건:YD_WBOOK_ID]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getByYdWBookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdWBookId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdWBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getByYdWBookId");

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
	} //end of getByYdWBookId

/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdCrnsch(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChkField = true;
		JDTORecord recPara = null;

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
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.insYdCrnsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.insYdCrnsch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.insYdCrnsch");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdCrnsch

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 INSERT / UPDATE [강제권상요구 크레인스케줄 등록]
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int mergeYdCrnsch(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChkField = true;
		JDTORecord recPara = null;

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
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.mergeYdCrnsch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.mergeYdCrnsch");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of mergeYdCrnsch

/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE [야드작업진행상태, 야드작업지시일시] , intGp = 302
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdCrnWrkProgStat(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = null;

			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updYdCrnWrkProgStat
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updYdCrnWrkProgStat");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCrnWrkProgStat

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 Clear
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updProgStatByEqpId(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = null;

			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updProgStatByEqpId
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updProgStatByEqpId");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updProgStatByEqpId

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE [크레인스케줄 To 위치정보]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updDnWoInfo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = null;

			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updDnWoInfo
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updDnWoInfo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updDnWoInfo

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE [크레인스케줄 From,To 위치정보] , intGp == 303
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updEqpUpDnWoInfo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = null;

			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updEqpUpDnWoInfo
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updEqpUpDnWoInfo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updEqpUpDnWoInfo

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE [권하위치 변경시 하위 권하분리(X)스케쥴 From위치 변경]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdCrnXSchFromLoc(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnXSchFromLoc
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updYdCrnXSchFromLoc
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updYdCrnXSchFromLoc");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCrnXSchFromLoc

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE [권상실적처리]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updCrnUpWr(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = null;

			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnUpWr
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnUpWr");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updCrnUpWr

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE [권하실적처리]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updCrnDnWr(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = null;

			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnDnWr
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updCrnDnWr");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updCrnDnWr

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE [우선순위변경]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updSchPrior(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = null;

			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updSchPrior
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.updSchPrior");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updSchPrior

	/**
	 *      [A] 오퍼레이션명 : 크레인작업재료 정보 UPDATE (매수,중량,높이,폭,길이)
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int upYdEqpWrkInfo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {
			//변환용 레코드
			JDTORecord recInPara = null;

			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.upYdEqpWrkInfo
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.upYdEqpWrkInfo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of upYdEqpWrkInfo

/*------------------------------------- DELETE -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 삭제처리 (DEL_YN='Y'로 UPdate)
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int delYdCrnSch(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recInPara = null;

		try {
			recInPara = JDTORecordFactory.getInstance().create();

			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.delYdCrnSch
			recInPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.delYdCrnSch");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of delYdCrnSch

/*------------------------------------- ETC -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		boolean blnErr = true;

		try {
			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_SCH_ID", 18, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_REGISTER", 10, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_REG_DDTT", 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

//			szFieldName = "";
//			blnErr = ydDaoUtils.chkField(inRec, "V_MODIFIER", 10, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			szFieldName = "";
//			blnErr = ydDaoUtils.chkField(inRec, "V_MOD_DDTT", 0, 3, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			szFieldName = "";
//			blnErr = ydDaoUtils.chkField(inRec, "V_DEL_YN", 1, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WBOOK_ID", 18, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_ID", 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_BAY_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_CD", 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_ST_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_REQ_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_PRIOR", 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_WRK_STAT", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_PROG_STAT", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WBOOK_DT", 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "YD_SCH_DT", 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WORD_DT", 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_CMPL_DT", 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_CMPL_DT", 0, 3, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_HDS_DD", 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_DUTY", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_PARTY", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_MAIN_WRK_MTL_SH", 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_AID_WRK_MTL_SH", 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_AID_WRK_UPDN_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_TO_LOC_DCSN_MTD", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_TO_LOC_GUIDE", 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_WRK_SH", 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_WRK_WT", 7, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_WRK_T", 8, 2, 'D', 4, 3);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_WRK_MAX_W", 6, 2, 'D', 4, 1);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_WRK_MAX_L", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_SB_CTL_H", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_GRAB_USE_RULE_ID", 18, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_LOC", 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_LAYER", 3, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_LOC_XAXIS", 7, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_XAXIS_GAP_MAX", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_XAXIS_GAP_MIN", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_LOC_YAXIS", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_LOC_YAXIS1", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_LOC_YAXIS2", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_YAXIS_GAP_MAX", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_YAXIS_GAP_MIN", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_LOC_ZAXIS", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_ZAXIS_GAP_MAX", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WO_ZAXIS_GAP_MIN", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_LOC", 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_LAYER", 3, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_LOC_XAXIS", 7, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_XAXIS_GAP_MAX", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_XAXIS_GAP_MIN", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_LOC_YAXIS", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_LOC_YAXIS1", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_LOC_YAXIS2", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_YAXIS_GAP_MAX", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_YAXIS_GAP_MIN", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_LOC_ZAXIS", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_ZAXIS_GAP_MAX", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WO_ZAXIS_GAP_MIN", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WR_LOC", 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WR_LAYER", 3, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WRK_ACT_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WR_XAXIS", 7, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WR_YAXIS", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WR_YAXIS1", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WR_YAXIS2", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_UP_WR_ZAXIS", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WR_LOC", 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WR_LAYER", 3, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WRK_ACT_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WR_XAXIS", 7, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WR_YAXIS", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WR_YAXIS1", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WR_YAXIS2", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_DN_WR_ZAXIS", 5, 2, 'L', 0, 0);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter

	/**
	 *      [A] 오퍼레이션명 : UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 *        JDTORecord updRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_SCH_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WBOOK_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_ST_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_REQ_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_PRIOR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_PROG_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WBOOK_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WORD_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_CMPL_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_CMPL_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_HDS_DD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_DUTY");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_PARTY");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_MAIN_WRK_MTL_SH");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_AID_WRK_MTL_SH");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_AID_WRK_UPDN_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TO_LOC_DCSN_MTD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TO_LOC_GUIDE");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_SH");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_WT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_T");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_MAX_W");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_MAX_L");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_SB_CTL_H");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_GRAB_USE_RULE_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_LAYER");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_LOC_XAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_XAXIS_GAP_MAX");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_XAXIS_GAP_MIN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_LOC_YAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_LOC_YAXIS1");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_LOC_YAXIS2");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_YAXIS_GAP_MAX");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_YAXIS_GAP_MIN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_LOC_ZAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_ZAXIS_GAP_MAX");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WO_ZAXIS_GAP_MIN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_LAYER");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_LOC_XAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_XAXIS_GAP_MAX");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_XAXIS_GAP_MIN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_LOC_YAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_LOC_YAXIS1");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_LOC_YAXIS2");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_YAXIS_GAP_MAX");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_YAXIS_GAP_MIN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_LOC_ZAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_ZAXIS_GAP_MAX");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WO_ZAXIS_GAP_MIN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WR_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WR_LAYER");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WRK_ACT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WR_XAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WR_YAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WR_YAXIS1");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WR_YAXIS2");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_UP_WR_ZAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WR_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WR_LAYER");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WRK_ACT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WR_XAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WR_YAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WR_YAXIS1");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WR_YAXIS2");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DN_WR_ZAXIS");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
	} // end of dataMapping

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [YD_CRN_SCH_ID] , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnSchYdP(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnSchYdP";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
						
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch");

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
	} // end of getYdCrnSchYdP


	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [YD_CRN_SCH_ID] , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getYdCrnSchYdF(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnSchYdF";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
						
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getYdCrnSch");

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
	} // end of getYdCrnSchYdF


	/**
	 *      [A] 오퍼레이션명 : 해당 작업지시 상단에 크레인 작업 예약 Check [명령선택시 하단부터 선택 안되도록 체크]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getUpLyrCrnSchExistYdP(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getUpLyrCrnSchExistYdP";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getUpLyrCrnSchExist
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getUpLyrCrnSchExist");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			}else {
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
	} // end of getUpLyrCrnSchExistYdP


	/**
	 *      [A] 오퍼레이션명 : 해당 작업지시 상단에 크레인 작업 예약 Check [명령선택시 하단부터 선택 안되도록 체크]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getUpLyrCrnSchExistYdF(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getUpLyrCrnSchExistYdF";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getUpLyrCrnSchExist
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getUpLyrCrnSchExist");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();

				String szMsg = "조회완료 >>>> 건수 :: " + Integer.toString(intRtnVal);
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			}else {
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
	} // end of getUpLyrCrnSchExistYdF


	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [V_YD_EQP_ID] , intGp == 16
	 *						현재 작업중인 크레인 작업지시 조회  YD_WRK_PROG_STAT IN ('1','2','3')
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getWrkProgStatYdP(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getWrkProgStatYdP";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
								
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getWrkProgStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getWrkProgStat");

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
	} // end of getWrkProgStatYdP



	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT [V_YD_EQP_ID] , intGp == 16
	 *						현재 작업중인 크레인 작업지시 조회  YD_WRK_PROG_STAT IN ('1','2','3')
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet

	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException
	 */
	public int getWrkProgStatYdF(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getWrkProgStatYdF";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
								
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getWrkProgStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSchDAO.getWrkProgStat");

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
	} // end of getWrkProgStatYdF


	
	
} // end of class
