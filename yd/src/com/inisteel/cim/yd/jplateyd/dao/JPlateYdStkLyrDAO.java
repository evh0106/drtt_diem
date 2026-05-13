/*
 * @(#) 야드적치단 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/20
 *
 * @description		야드적치단 DAO
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

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드적치단 DAO
 *
*/

public class JPlateYdStkLyrDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdStkLyrDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
	
	/*------------------------------------- SELECT -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 SELECT , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStklyr(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStklyr";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyr
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyr");

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
	} //end of getYdStklyr

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 SELECT , 베드 JOIN
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getJoinYdStkbed(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getJoinYdStkbed";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getJoinYdStkbed
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getJoinYdStkbed");

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
	} //end of getJoinYdStkbed

	/**
	 *      [A] 오퍼레이션명 : 실제 적치단의 최고 TOP 적치위치를 조회 , intGp == 98
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public String getRealTopLyr(JDTORecord inRec) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recOut	= null;
		JDTORecord 		recPara = null;
		String	szRealTopLyr	= "000";
		String 	szMethodName 	= "getRealTopLyr";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrRealTopLyr
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getRealTopLyr
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getRealTopLyr");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				recOut = JDTORecordFactory.getInstance().create();
				rsTemp.first();
				recOut = rsTemp.getRecord();
		        szRealTopLyr = ydDaoUtils.paraRecChkNull(recOut, "REAL_TOP_LYR");

				String szMsg = "조회완료 >>>> 적치단 :: " + szRealTopLyr;
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			} else {
				//data not found
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szRealTopLyr;
	} //end of getRealTopLyr

	/**
	 *      [A] 오퍼레이션명 : 재료번호로 야드적치단TABLE SELECT
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStklyrByStlNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStklyrByStlNo";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByStlNo");

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
	} //end of getYdStklyrByStlNo

	/**
	 *      [A] 오퍼레이션명 : 재료번호로 야드적치단TABLE SELECT , intGp == 3
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStklyrByStlNoStat(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStklyrByStlNoStat";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByStlNoStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByStlNoStat");

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
	} //end of getYdStklyrByStlNoStat

	/**
	 *      [A] 오퍼레이션명 : 상단부터 해당 매수만큼 재료정보 조회 [YD_STK_COL_GP, YD_STK_BED_NO, YD_EQP_WRK_SH]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getStlNoTopCnt(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getStlNoTopCnt";

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
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getStlNoTopCnt
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getStlNoTopCnt");

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
	} // end of getStlNoTopCnt

	/**
	 *      [A] 오퍼레이션명 : 적치단 적치중인 재료의 두께조회 , intGp == 71
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getStkLyrMtlSumT(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getStkLyrMtlSumT";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getStkLyrMtlSumW
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getStkLyrMtlSumT
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getStkLyrMtlSumT");

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
	} //end of getStkLyrMtlSumT

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 SELECT , 저장품 JOIN , intGp == 6
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getJoinYdStock(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getJoinYdStock";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrYdStock
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getJoinYdStock
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getJoinYdStock");

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
	} //end of getJoinYdStock


	/**
	 *      [A] 오퍼레이션명 : Bed별로 작업예약재료를 조회 , intGp == 15
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getByWBookIdEtc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByWBookIdEtc";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydStkLyrDao.getStkLyrbyWBookIdEtc
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getByWBookIdEtc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getByWBookIdEtc");

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
	} //end of getByWBookIdEtc

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 SELECT , intGp == 29
	 *						크레인작업관리 화면에서 권하위치변경시 적치가능단 조회 [조회조건:적치열,적치베드,재료번호]
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStklyrByColGpBedNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStklyrByColGpBedNo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByColGpBedNo
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByColGpBedNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByColGpBedNo");

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
	} //end of getYdStklyrByColGpBedNo

	/**
	 *      [A] 오퍼레이션명 : 보수장 적치가능 베드 조회 (파라미터 MTL_W:폭을 베드갯수로 환산 , MTL_L:길이를 열갯수로 환산)
	 *					--> 업무기준 변경으로 실제 호출되는 곳이 없음
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getStkLyrBsEmptyBed(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara	= null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getStkLyrBsEmptyBed";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getStkLyrBsEmptyBed
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getStkLyrBsEmptyBed");

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
	} //end of getStkLyrBsEmptyBed

	/**
	 *      [A] 오퍼레이션명 : 보수장 저장위치 적치가능여부 체크 - Error일때 OCPY_BED_ERR='Y' Return
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getOcpyBedErr(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		JDTORecord 		recPara	= null;
		int intRtnVal 			= -100;
		String szMethodName 	= "getOcpyBedErr";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getOcpyBedErr
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getOcpyBedErr");

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
	} //end of getOcpyBedErr

	/**
	 *      [A] 오퍼레이션명 : TO 위치에 예약재료가 존재여부 조회 [조회조건:작업예약ID]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getMtlStatByOtherSch(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		int 			intRtnVal 	 = -100;
		JDTORecordSet 	rsTemp 		 = null;
		JDTORecord 		recPara 	 = null;
		String 			szMethodName = "getMtlStatByOtherSch";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getMtlStatByOtherSch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getMtlStatByOtherSch");

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
	} //end of getMtlStatByOtherSch

	/**
	 *      [A] 오퍼레이션명 : FROM 위치에 예약재료가 존재여부 조회 [조건:작업예약ID]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getMtlStatByFromLoc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		int 			intRtnVal 	 = -100;
		JDTORecordSet 	rsTemp 		 = null;
		JDTORecord 		recPara 	 = null;
		String 			szMethodName = "getMtlStatByFromLoc";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getMtlStatByFromLoc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getMtlStatByFromLoc");

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
	} //end of getMtlStatByFromLoc

	/**
	 *      [A] 오퍼레이션명 : TO위치정보 조회 L2 I/F 송신용
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getToLocInfo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getToLocInfo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getToLocInfo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getToLocInfo");

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
	} //end of getToLocInfo


	/**
	 *      [A] 오퍼레이션명 : From위치정보 조회 L2 I/F 송신용
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getFromLocInfo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getFromLocInfo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getFromLocInfo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getFromLocInfo");

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
	} //end of getFromLocInfo

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 SELECT [조회조건 : YD_STK_COL_GP]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getByYdStkColGp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByYdStkColGp";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getByYdStkColGp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getByYdStkColGp");

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
	} //end of getByYdStkColGp

	/**
	 *      [A] 오퍼레이션명 : 보수장 적치가능 베드 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public String getEmptyBsLoc(String pYdStkColGp, String pYdStkBedNo, String pStlNo) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recOut	= null;
		JDTORecord 		recPara = null;
		String	szRtnVal 		= "";
		String 	szMethodName 	= "getEmptyBsLoc";
		String 	szMsg			= "";

		try {

			recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("V_YD_STK_COL_GP", pYdStkColGp);
			recPara.setField("V_YD_STK_BED_NO", pYdStkBedNo);
			recPara.setField("V_STL_NO",  		pStlNo);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyBsLoc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyBsLoc");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				recOut = JDTORecordFactory.getInstance().create();
				rsTemp.first();
				recOut   = rsTemp.getRecord();
				szRtnVal = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recOut, "YD_STK_BED_NO");

				szMsg = "조회완료 >>>> DATA :: " + recOut.toString();
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			szMsg = "조회완료 >>>> 결과 :: " + szRtnVal;
			ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szRtnVal;
	} //end of getByYdStkColGp

	/**
	 *      [A] 오퍼레이션명 : 가스장 적치가능 베드 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public String getEmptyCncLoc(String pYdStkColGp, String pYdStkBedNo, String pStlNo) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recOut	= null;
		JDTORecord 		recPara = null;
		String	szRtnVal 		= "";
		String 	szMethodName 	= "getEmptyCncLoc";
		String 	szMsg			= "";

		try {

			recPara	= JDTORecordFactory.getInstance().create();
			recPara.setField("V_YD_STK_COL_GP", pYdStkColGp);
			recPara.setField("V_YD_STK_BED_NO", pYdStkBedNo);
			recPara.setField("V_STL_NO",  		pStlNo);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyCncLoc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyCncLoc");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				recOut = JDTORecordFactory.getInstance().create();
				rsTemp.first();
				recOut   = rsTemp.getRecord();
				szRtnVal = ydDaoUtils.paraRecChkNull(recOut, "YD_STK_COL_GP") + ydDaoUtils.paraRecChkNull(recOut, "YD_STK_BED_NO");

				szMsg = "조회완료 >>>> DATA :: " + recOut.toString();
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			szMsg = "조회완료 >>>> 결과 :: " + szRtnVal;
			ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return szRtnVal;
	} //end of getEmptyCncLoc

	/**
	 *      [A] 오퍼레이션명 : 해당열에 적치가능 저장위치 조회 [조회조건 : 야드적치열구분, 재료번호]
	 *						(재료번호의 재료길이가 적치 가능한 저장위치 조회)
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getEmptyToLoc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getEmptyToLoc";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyToLoc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyToLoc");

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
	} //end of getEmptyToLoc

	/**
	 *      [A] 오퍼레이션명 : 가스장 적치가능 저장위치 조회 [조회조건 : 야드적치열구분]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getEmptyToCnc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getEmptyToCnc";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.17 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "F");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F");                 	// log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyToCnc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyToCnc");

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
	} //end of getEmptyToCnc

	/**
	 *      [A] 오퍼레이션명 : 적치열,적치상태로 적치단정보 조회 [조회조건 : 야드적치열구분, 적치상태1,2,3]
	 *						[저장위치 변경 가능여부 체크] - 점유베드는 SKIP (STL_NO IS NOT NULL)
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getByLocMtlStat(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getByLocMtlStat";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
						
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getByLocMtlStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getByLocMtlStat");

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
	} //end of getToLocMtlStatChk

	/**
	 *      [A] 오퍼레이션명 : RT - BOOK-IN 대상재 조회
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getRTBookInMtl(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getRTBookInMtl";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getRTBookInMtl
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getRTBookInMtl");

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
	} //end of getRTBookInMtl

	/**
	 *      [A] 오퍼레이션명 : 작업예약 ID의 TO위치 가이드에 해당 하는 적치가능베드 조회 [조회조건:작업예약ID, 적치상태]
	 *      				- RT BOOK-IN시 TO위치 검색 (적치상태 'E')
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getEmptyLocByWBookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getEmptyLocByWBookId";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyLocByWBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyLocByWBookId");

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
	} //end of getEmptyLocByWBookId

	/**
	 *      [A] 오퍼레이션명 : 파일링/횡작업후 TO위치 검색 [조회조건:파일링구분,야드적치열,야드적치베드]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getPilingToLoc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getPilingToLoc";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getPilingToLoc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getPilingToLoc");

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
	} //end of getPilingToLoc

	/**
	 *      [A] 오퍼레이션명 : 해당 열,단정보로 재료길이 합과 열길이를 비교하여 적치가능여부 체크
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      (열길이 - 재료적치길이합)
	 * @throws DAOException
	 * @throws JDTOException
	 */

	public int getRemainMtlL(JDTORecord inRec) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recOut	= null;
		JDTORecord 		recPara = null;

		String 	szMethodName 	= "getRemainMtlL";
		int		iYdStkColL		= 0;
		int		iSumMtlL		= 0;
		int		iRemainMtlL		= 0;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getRemainMtlL
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getRemainMtlL");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				recOut = JDTORecordFactory.getInstance().create();
				rsTemp.first();
				recOut = rsTemp.getRecord();

				iYdStkColL 	= ydDaoUtils.paraRecChkNullInt(recOut, "YD_STK_COL_L");
				iSumMtlL 	= ydDaoUtils.paraRecChkNullInt(recOut, "SUM_MTL_L");
				iRemainMtlL = ydDaoUtils.paraRecChkNullInt(recOut, "REMAIN_MTL_L");
			}

			String szMsg = "조회완료 >>>> 열길이 :: " + iYdStkColL + ", 재료길이합 :: " + iSumMtlL + ", 여유 :: " + iRemainMtlL;
			ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return iRemainMtlL;
	} //end of getRealTopLyr

	/**
	 *      [A] 오퍼레이션명 : 적치단 하단에 권상재료 존재여부 체크 [조건:야드적치열구분,적치단]
	 *						[저장위치 변경 가능여부 체크]
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getUpStatByLyrNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getUpStatByLyrNo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getUpStatByLyrNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getUpStatByLyrNo");

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
	} //end of getUpStatByLyrNo

	/**
	 *      [A] 오퍼레이션명 : 권하실적 처리시 TO위치 체크 (01베드 부터 선택 하도록 체크)
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStklyrWithTopCnt(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStklyrWithTopCnt";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrWithTopCnt
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrWithTopCnt");

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
	} //end of getYdStklyrWithTopCnt


	/**
	 *      [A] 오퍼레이션명 : 강제권상 위치정보조회 (크레인상태설정팝업)
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getOffCrnUpWr(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getOffCrnUpWr";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getOffCrnUpWr
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getOffCrnUpWr");

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
	} //end of getOffCrnUpWr

/*------------------------------------- INSERT -------------------------------------------*/


/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE - 전체항목 업데이트 ... 실제로 호출되는 곳 없음
	 *                      (메서드 삭제시 빌드에러 때문에 남겨놈 updYdStklyrStat 로 대치)
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStklyr(JDTORecord inRec) throws DAOException, JDTOException {
		return 0;
		/*
		String szMethodName = "updYdStklyr";
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
			intRtnVal = this.getYdStklyr(inRec, outRecSet);

			//parameter error return
			if (intRtnVal < 0) {
				szMsg = "parameter error!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal;
			}

			//data not found return
			if (intRtnVal == 0) {
				szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal;
			}

			//duplicate data return
			if (outRecSet.size() != 1) {
				szMsg = "duplicate data!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}

			outRecSet.first();
			outRec = outRecSet.getRecord();

			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);

			//data mapping
			this.dataMapping(recInPara, recOutPara);

			blnChkField = this.chkParameter(recOutPara);

			//parameter error return
			if (!blnChkField) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyr
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyr
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyr");

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
		*/
	} // end of updYdStklyrAll

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE - [조건 :적치열구분,베드,적치단] - 항목 : 적치상태, 재료번호
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStklyrStat(JDTORecord inRec) throws DAOException, JDTOException {

		String	szYdStkLyrMtlStat = "";
		int 	intRtnVal = 0;
		JDTORecord recPara = null;
		try {

			this.chkUpdOcpyBed(inRec);		// 점유베드 UPDATE

			// 권하예약 변경시 기존 적치중인 재료가 변경되는 현상이 가끔 발생하여 메서드 분리
			szYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_LYR_MTL_STAT");
			if ("D".equals(szYdStkLyrMtlStat)) {
				intRtnVal = this.updYdStklyrDownStat(inRec);
				return intRtnVal;
			}

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat");

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
	} // end of updYdStklyrStat
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE(트랜잭션분리) - [조건 :적치열구분,베드,적치단] - 항목 : 적치상태, 재료번호
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStklyrStat2(JDTORecord inRec) throws DAOException, JDTOException {

		String	szYdStkLyrMtlStat = "";
		int 	intRtnVal = 0;
		JDTORecord recPara = null;
		try {

			this.chkUpdOcpyBed(inRec);		// 점유베드 UPDATE

			// 권하예약 변경시 기존 적치중인 재료가 변경되는 현상이 가끔 발생하여 메서드 분리
			szYdStkLyrMtlStat = ydDaoUtils.paraRecChkNull(inRec, "YD_STK_LYR_MTL_STAT");
			if ("D".equals(szYdStkLyrMtlStat)) {
				//com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrDownStat
				//intRtnVal = this.updYdStklyrDownStat(inRec); //이것도 트랜잭션 분리하자
				
				//트랜잭션 별도 분리
				inRec.setField("QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrDownStat");
				// 2021. 06. 03 트랜잭션 분리를 위한 EJb Bean설정
				EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
				ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { inRec });	
				return intRtnVal;
			}

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat
			//트랜잭션 분리 필요.
			//recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat");
			//query execute
			//intRtnVal = dbAssDao.trtProcess(recPara);
			
			//별도 트랜잭션으로 분기 처리.
			inRec.setField("QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrStat");
			EJBConnector 	ejbConn = new EJBConnector("default", "CarMvHdSeEJB", this);
			ejbConn.trx("updYdStkColTX", new Class[] { JDTORecord.class }, new Object[] { inRec });
			

			//execution error return
			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrStat2	

	/**
	 *      [A] 오퍼레이션명 : 권하예약상태로 야드적치단 UPDATE - [조건 :적치열구분,베드,적치단] - 항목 : 적치상태, 재료번호
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStklyrDownStat(JDTORecord inRec) throws DAOException, JDTOException {

		int 	intRtnVal = 0;
		JDTORecord recPara = null;
		try {

			this.chkUpdOcpyBed(inRec);		// 점유베드 UPDATE

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrDownStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrDownStat");

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
	} // end of updYdStklyrDownStat

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE - [조건 : 재료번호,야드구분] - UPDATE항목 : 적치상태
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updMtlStatByStlNo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {

			this.chkUpdOcpyBed(inRec);		// 점유베드 UPDATE

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updMtlStatByStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updMtlStatByStlNo");

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
	} // end of updMtlStatByStlNo

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 CLEAR [조건:재료번호,재료적치상태,야드구분]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStklyrClearByStlNo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord 		recPara 	= null;
		JDTORecord 		recTemp 	= JDTORecordFactory.getInstance().create();
		JDTORecordSet 	outRecSet 	= JDTORecordFactory.getInstance().createRecordSet("retTmp");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {

			// 재료번호로 대상을 조회하여 점유베드 UPDATE 호출
			intRtnVal = this.getYdStklyrByStlNoStat(inRec, outRecSet);
			if (intRtnVal > 0) {
				for(int ii=0; ii<outRecSet.size(); ii++) {
					recTemp = outRecSet.getRecord(ii);
					recTemp.setField("YD_STK_LYR_MTL_STAT", "");
					
//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recTemp에 logId 추가 
					recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
					
					this.chkUpdOcpyBed(recTemp);						// 점유베드 UPDATE
				}
			}

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리 아이디 세팅
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrWithStock
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClearByStlNo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrClearByStlNo

	/**
	 *      [A] 오퍼레이션명 : 점유베드를 체크하여 상태를 변경한다.
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int chkUpdOcpyBed(JDTORecord inRec) throws DAOException, JDTOException {

//		JPlateYdStockDAO 	ydStockDao 	= new JPlateYdStockDAO();
		JPlateYdStkColDAO	ydStkColDao	= new JPlateYdStkColDAO();
		JPlateYdStkBedDAO 	ydStkBedDao = new JPlateYdStkBedDAO();

		JDTORecordSet 	outRecSet 		= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord 		recPara 		= JDTORecordFactory.getInstance().create();
		JDTORecord 		outRec 			= null;

		String 	szYdStkColGp 			= "";
		String 	szYdStkLyrMtlStat 		= "";
	    String	szYdStkBedNo			= "";
	    String	szYdStkLyrNo			= "";
	    String	szYdStkColBedLTp		= "";
	    String	szStlNo					= "";
	    String	szModifier				= "";
		String	szStat					= "";
		String	szMsg					= "";
		String	szOperationName			= "보수장/혼적 점유베드 체크";
		String	szMethodName			= "chkUpdOcpyBed";
		String	szSpanNo				= "";
		String	szYdStkBedLMax			= "";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본

		szMsg = szOperationName + " 시작 >>>>>>>>>>>>>";
		ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
		
//-------------------------------------------------------------------------------------------------------------------------
		
		
		int 	intRtnVal 				= 0;

		try {

			szYdStkColGp 		= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_COL_GP"			);
		    szYdStkBedNo		= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_BED_NO"			);
		    szYdStkLyrNo		= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_LYR_NO"			);
			szYdStkLyrMtlStat 	= ydDaoUtils.paraRecChkNull(inRec, "YD_STK_LYR_MTL_STAT"	);
			szStlNo				= ydDaoUtils.paraRecChkNull(inRec, "STL_NO"					);
			szModifier			= ydDaoUtils.paraRecChkNull(inRec, "MODIFIER"				);

			// 2013.05.13 보수장 제외 하도록 변경 ..
			szMsg = "[JSP Session : " + szOperationName + "] >>>> 혼적베드 일때 점유베드 체크 :: " + inRec.toString();
			ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			// 조건값에 저장위치 미존재시 재료번호로 저장위치 조회 .. 권하예약위치 CLEAR시 사용
			if ("".equals(szYdStkColGp)) {
				recPara.setField("STL_NO", szStlNo);

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recPara에 logId 추가 
				inRec.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
				
				intRtnVal = this.getYdStklyrByStlNo(inRec, outRecSet);
				if (intRtnVal > 0) {
					for(int ii=0; ii<outRecSet.size(); ii++) {
						outRecSet.absolute(ii+1);
						outRec 	= outRecSet.getRecord();
						szStat 	= ydDaoUtils.paraRecChkNull(outRec, "YD_STK_LYR_MTL_STAT");
						if ("D".equals(szStat)) {
							szYdStkColGp = ydDaoUtils.paraRecChkNull(outRec, "YD_STK_COL_GP");
						    szYdStkBedNo = ydDaoUtils.paraRecChkNull(outRec, "YD_STK_BED_NO");
						    szYdStkLyrNo = ydDaoUtils.paraRecChkNull(outRec, "YD_STK_LYR_NO");
						    break;
						}
					}
				}
			}

			// 저장위치값이 파라미터에 없을 경우 SKIP
			if ("".equals(szYdStkColGp) || szYdStkColGp.length() < 4) {
				return intRtnVal;
			}

			// 혼적베드 이외의 설비일때 SKIP --> 보수장도 SKIP 하도록 변경
			szSpanNo = szYdStkColGp.substring(2, 4);
			if ("CN".equals(szSpanNo) ||			// GAS장일때
				"CR".equals(szSpanNo) ||			// 크레인일때
				"RT".equals(szSpanNo) ||			// RT일때
				"BS".equals(szSpanNo) ||			// 보수장일때
				"BC".equals(szSpanNo) ||			// 1후판 임가공 절단장
				"CB".equals(szSpanNo)) {			// 냉각대일때

				szMsg = "[JSP Session : " + szOperationName + "] >>>> 혼적베드 이외의 설비일때 SKIP :: " + szSpanNo;
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

				return intRtnVal;
			}

			// 혼적열 점유베드 Set
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", szYdStkColGp);
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			intRtnVal = ydStkColDao.getYdStkcol(recPara, outRecSet);
			if (intRtnVal > 0) {
				outRecSet.first();
				outRec = outRecSet.getRecord();
				szYdStkColBedLTp = ydDaoUtils.paraRecChkNull(outRec, "YD_STK_COL_BED_L_TP");		// 야드적치열Bed길이Type
			}

			szMsg = "[JSP Session : " + szOperationName + "] >>>> 혼적열 열인지 조회 ::" + szYdStkColBedLTp;
			ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			if ("F1".equals(szYdStkColBedLTp) || "F2".equals(szYdStkColBedLTp)) {

				if ("".equals(szYdStkLyrMtlStat)) {
					szYdStkLyrMtlStat = "E";
				}

				// 재료번호가 ""일때 해당위치의 재료번호를 조회
				if ("".equals(szStlNo)) {
					outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 inRec에 logId 추가 
					inRec.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
								
					intRtnVal = this.getYdStklyr(inRec, outRecSet);
					if (intRtnVal > 0) {
						outRecSet.first();
						outRec = outRecSet.getRecord();
						szStlNo = ydDaoUtils.paraRecChkNull(outRec, "STL_NO");
					}
				}

				// 야드적치 길이 베드 SET
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				outRec    = JDTORecordFactory.getInstance().create();
				

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 inRec에 logId 추가 
				inRec.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
									
				intRtnVal = ydStkBedDao.getYdStkbed(inRec, outRecSet);
				if (intRtnVal > 0) {
					outRecSet.first();
					outRec  = outRecSet.getRecord();
					szYdStkBedLMax = ydDaoUtils.paraRecChkNull(outRec, "YD_STK_BED_L_MAX");
				}

				recPara = JDTORecordFactory.getInstance().create();
				recPara.setField("MODIFIER",			szModifier);
				recPara.setField("YD_STK_COL_GP", 		szYdStkColGp);
				recPara.setField("YD_STK_BED_NO", 		szYdStkBedNo);
				recPara.setField("YD_STK_LYR_NO", 		szYdStkLyrNo);
				recPara.setField("YD_STK_LYR_MTL_STAT", szYdStkLyrMtlStat);
				recPara.setField("STL_NO",				szStlNo);
				recPara.setField("YD_STK_BED_L_MAX",	szYdStkBedLMax);

				intRtnVal = this.updOcpyMixedBedSet(recPara);		// 혼적열 점유베드 Set

			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of chkUpdOcpyBed

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 점유베드 Set
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updOcpyBedSet(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updOcpyBedSet
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updOcpyBedSet");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updOcpyBedSet

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 점유베드 Clear [조건:열구분(FROM,TO), 베드번호(FROM,TO), 점유베드번호, 점유베드단]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updOcpyBedClear(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updOcpyBedClear
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updOcpyBedClear");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updOcpyBedClear

	/**
	 *      [A] 오퍼레이션명 : RT 적치중일때 야드적치단 UPDATE - [조건:열구분,베드,단(001)] 항목:적치상태, 재료번호
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updRtBedClear(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;
		JDTORecord recTemp = JDTORecordFactory.getInstance().create();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.03 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {

			recTemp.setRecord(inRec);
			recTemp.setField("YD_STK_LYR_MTL_STAT", "");

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.03 recTemp에 logId 추가 
			recTemp.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------
			
			this.chkUpdOcpyBed(recTemp);					// 점유베드 UPDATE

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updRtBedClear
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updRtBedClear");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updRtBedClear

	/**
	 *      [A] 오퍼레이션명 : 혼적베드 점유 Set [조건:열구분,베드번호(>),적치단,재료번호 is null]
	 *						적치상태에 따라서 Set , Clear 한다.
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updOcpyMixedBedSet(JDTORecord inRec) throws DAOException, JDTOException {

		int 			intRtnVal 	= 0;
		JDTORecord 		recPara 	= null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//점유베드 상태 Set
			if ("E".equals(ydDaoUtils.paraRecChkNull(recPara, "V_YD_STK_LYR_MTL_STAT"))) {
				recPara.setField("V_YD_OCPY_BED_GP",		"");
				recPara.setField("V_YD_OCPY_STK_BED_NO",	"");
				recPara.setField("V_YD_OCPY_STK_LYR_NO",	"");

				// 쿼리 아이디 세팅
				// 기존쿼리 :
				// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updOcpyMixedBedClear
				recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updOcpyMixedBedClear");

			} else {
				recPara.setField("V_YD_OCPY_BED_GP",		"V");
				recPara.setField("V_YD_OCPY_STK_BED_NO",	ydDaoUtils.paraRecChkNull(recPara, "V_YD_STK_BED_NO"));
				recPara.setField("V_YD_OCPY_STK_LYR_NO",	ydDaoUtils.paraRecChkNull(recPara, "V_YD_STK_LYR_NO"));

				// 쿼리 아이디 세팅
				// 기존쿼리 :
				// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updOcpyMixedBedSet
				recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updOcpyMixedBedSet");

			}

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updOcpyMixedBedSet

	/**
	 *      [A] 오퍼레이션명 : 적치열, 베드번호로 적치단활성상태 업데이트 (대차 맵정리용)
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStkLyrActStat(JDTORecord inRec) throws DAOException, JDTOException {

		int 	intRtnVal 		= 0;
		String	szOCPY_CHK_FLAG	= "";

		JDTORecord recPara = null;

		try {

			szOCPY_CHK_FLAG = ydDaoUtils.paraRecChkNull(inRec, "OCPY_CHK_FLAG", "Y");

			if ("Y".equals(szOCPY_CHK_FLAG)) {
				this.chkUpdOcpyBed(inRec);			// 점유베드 UPDATE
			}

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo2
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStkLyrActStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStkLyrActStat");

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
	} // end of updYdStkLyrActStat

	/**
	 *      [A] 오퍼레이션명 : TO베드 활성화 및 재료정보 복사 (대차도착 정리용)
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int copyTcarFromBed(JDTORecord inRec) throws DAOException, JDTOException {

		int 	intRtnVal 		= 0;
		String	szOCPY_CHK_FLAG	= "";

		JDTORecord recPara 		= null;

		try {

			szOCPY_CHK_FLAG = ydDaoUtils.paraRecChkNull(inRec, "OCPY_CHK_FLAG", "Y");

			if ("Y".equals(szOCPY_CHK_FLAG)) {
				this.chkUpdOcpyBed(inRec);			// 점유베드 UPDATE
			}

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.copyTcarFromBed
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.copyTcarFromBed");

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
	} // end of copyTcarFromBed

	/**
	 *      [A] 오퍼레이션명 : 저장위치적치정보 CLEAR [조건:열구분,베드,적치단] - 대차 초기화 처리시 사용
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStklyrClear(JDTORecord inRec) throws DAOException, JDTOException {

		int 	intRtnVal 		= 0;
		String	szOCPY_CHK_FLAG	= "";

		JDTORecord recPara 		= null;
		JDTORecord recTemp 		= JDTORecordFactory.getInstance().create();

		try {

			szOCPY_CHK_FLAG = ydDaoUtils.paraRecChkNull(inRec, "OCPY_CHK_FLAG", "Y");

			if ("Y".equals(szOCPY_CHK_FLAG)) {
				recTemp.setRecord(inRec);
				recTemp.setField("YD_STK_LYR_MTL_STAT", "");
				this.chkUpdOcpyBed(recTemp);						// 점유베드 UPDATE
			}

			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClear
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.updYdStklyrClear");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrClear

/*------------------------------------- DELETE -------------------------------------------*/


/*------------------------------------- ETC    -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws DAOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LYR_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_STL_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LYR_ACT_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LYR_MTL_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LYR_XAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LYR_YAXIS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_LYR_ZAXIS");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}

	} // end of dataMapping

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 INSERT parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {

		boolean blnErr = true;

		try {

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_COL_GP", 6, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_BED_NO", 2, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LYR_NO", 3, 1, 'S', 0, 0);
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

			blnErr = ydDaoUtils.chkField(inRec, "V_STL_NO", 11, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LYR_ACT_STAT", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LYR_MTL_STAT", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LYR_XAXIS", 7, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LYR_YAXIS", 5, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_STK_LYR_ZAXIS", 5, 2, 'L', 0, 0);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkPara_YdStklyr

	/**********************************************************
	* 1후판정정추가 SJH16
	**********************************************************/	 
	
	
	/**
	 *      [A] 오퍼레이션명 : 1후판 임가공절단장 적치가능 저장위치 조회 [조회조건 : 야드적치열구분]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getEmptyToBc(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getEmptyToBc";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyToCnc
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getEmptyToBc");

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
	} //end of getEmptyToCnc
	
	/**
	 *      [A] 오퍼레이션명 : 재료번호로 야드적치단TABLE SELECT
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStklyrByStlNoYdP(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdStklyrByStlNoYdP";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
        String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  			// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

        if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 		// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByStlNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO.getYdStklyrByStlNoStatYdP");

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
	} // end of getYdStklyrByStlNoYdP
	
	/**
	 *      [A] 오퍼레이션명 : 1후판 열처리 BOOK IN/OUT실적 전문 편집
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0: book in, 1:book out)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYDP3L501(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp 	= null;
		JDTORecord recPara 		= null;
		
		int intRtnVal = 0;
		
		/* com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2Y2YDP3L501

		SELECT JMS_TC_CD                                     --JMSTC코드
		     ||TO_CHAR(SYSDATE,'YYYY-MM-DDHH24:MI:SS')       --생성일시
		     ||'I'                                           --전문구분
		     ||'0167'                                        --전문길이 204 --> 205
		     ||RPAD(' ',29,' ')                              --임시
		     ||RPAD(NVL(OPERATION_TYPE      ,' '),  1,' ') 
		     ||RPAD(NVL(PL_L2_TRK_NO        ,' '), 16,' ') 
		     ||RPAD(NVL(PL_MTL_NO           ,' '), 32,' ') 
		     ||RPAD(NVL(PL_MEA_GDS_L        ,' '),  5,'0') 
		     
		     ||RPAD(NVL(PL_MEA_GDS_W        ,' '),  6,'0') 
		     ||RPAD(NVL(PL_MEA_GDS_T        ,' '),  7,'0') 
		     ||RPAD(NVL(PL_TRCK_ZONE_ASGN   ,' '),  5,'0') 

		     ||RPAD(NVL(PL_BOOK_OUT_MOD     ,' '),  1,' ') 
		     ||RPAD(NVL(CRANE_NO            ,' '),  1,' ') 
		     ||RPAD(NVL(YARD_NO             ,' '), 12,' ') 
		     ||RPAD(NVL(BED_NO              ,' '), 11,' ') 
		     ||RPAD(NVL(REASON_CODE         ,' '),  5,' ') 
		     ||RPAD(NVL(NEXT_PROCESS        ,' '),  6,' ') 
		     ||RPAD(NVL(SPARE               ,' '), 80,' ') 
		       AS JMS_TC_MESSAGE --JMSTCMESSAGE
		FROM (

		SELECT  'YDP3L501' AS JMS_TC_CD
		     ,  :V_OPERATION_TYPE   AS OPERATION_TYPE
		     ,  :V_PL_L2_TRK_NO     AS PL_L2_TRK_NO
		     ,  :V_PL_MTL_NO        AS PL_MTL_NO
		     ,  ''   AS PL_MEA_GDS_L
		     ,  ''   AS PL_MEA_GDS_W
		     ,  ''   AS PL_MEA_GDS_T
		     ,  ''   AS PL_TRCK_ZONE_ASGN
		     ,  '1'   AS PL_BOOK_OUT_MOD
		     ,  ''   AS CRANE_NO
		     ,  ''   AS YARD_NO
		     ,  ''   AS BED_NO
		     ,  ''   AS REASON_CODE
		     ,  ''   AS NEXT_PROCESS
		     ,  ''   AS SPARE
			    
		FROM    DUAL
		     )
	     */  
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO.rcvL2Y2YDP3L501");
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
			throw new DAOException(getClass().getName() + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getL2TelegramInfo			
} // end of class
