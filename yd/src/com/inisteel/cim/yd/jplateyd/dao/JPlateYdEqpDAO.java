/*
 * @(#) 야드설비 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/15
 *
 * @description		야드설비 DAO
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/15   김현우      김현우       최초작성
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

/**
 *      [A] 클래스명 : 야드설비 DAO
 *
*/
public class JPlateYdEqpDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdEqpDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

/*------------------------------------- SELECT -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드설비 SELECT [YD_EQP_ID] , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdEqp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdEqp";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqp
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqp");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getYdEqp

	/**
	 *      [A] 오퍼레이션명 : 야드설비 SELECT [해당 설비ID의 현재 설비상태와 휴지테이블의 MAX차수에 대한 데이터 추출]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getEqpStatOfMax(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getEqpStatOfMax";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getEqpStatofMAX
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getEqpStatOfMax
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getEqpStatOfMax");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getEqpStatOfMax


	/**
	 *      [A] 오퍼레이션명 : 야드설비 SELECT [Magnet 흡착기준 BRE 조회]
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdEqpMagnetBre(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdEqpMagnetBre";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB653
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqpMagnetBre
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdEqpMagnetBre");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getYdEqpMagnetBre

	/**
	 *      [A] 오퍼레이션명 : 야드설비목록 SELECT [YD_GP,YD_BAY_GP,YD_EQP_GP] , intGp == 2
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getJspYdEqpList(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getJspYdEqpList";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getJspYdEqpList
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getJspYdEqpList
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getJspYdEqpList");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getJspYdEqpList


	/**
	 *      [A] 오퍼레이션명 : 크레인별 배차기준 정보 조회 , intGp == 9
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getCarAsgnStdByCrn(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getCarAsgnStdByCrn";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getCarAsgnStdByCrn
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getCarAsgnStdByCrn
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getCarAsgnStdByCrn");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getCarAsgnStdByCrn

	/**
	 *      [A] 오퍼레이션명 : 크레인상태설정 정보 조회 , intGp == 6
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdCrnStsSetById(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet 	rsTemp 	= null;
		JDTORecord 		recPara = null;
		int 	intRtnVal 		= -100;
		String 	szMethodName 	= "getYdCrnStsSetById";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getSlabYdCrnStsSetByEqpId
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdCrnStsSetById
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.getYdCrnStsSetById");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getYdCrnStsSetById

/*------------------------------------- INSERT -------------------------------------------*/



/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdEqp(JDTORecord inRec) throws DAOException, JDTOException {

		int 	intRtnVal = 0;

		String 	szMethodName 	= "updYdEqp";
		String 	szMsg 			= null;
		boolean blnChkField 	= true;
		JDTORecord outRec 		= null;

		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			//필드명 변환 (필드명 -> V_필드명)
			JDTORecord recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//update data select
			intRtnVal = this.getYdEqp(inRec, outRecSet);

			//parameter error return
			if(intRtnVal < 0) {
				szMsg = "parameter error!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return intRtnVal;
			}

			//data not found return
			if(intRtnVal == 0) {
				szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return intRtnVal;
			}

			//duplicate data return
			if(outRecSet.size() != 1) {
				szMsg = "duplicate data!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return intRtnVal = -1;
			}

			outRecSet.first();
			outRec = outRecSet.getRecord();

			//필드명 변환 (필드명 -> V_필드명)
			JDTORecord recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);

			//data mapping
			this.dataMapping(recInPara, recOutPara);

			//parameter check
			blnChkField = this.chkParameter(recOutPara);

			//parameter error return
			if(!blnChkField) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.updYdEqp
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqp
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqp");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if(intRtnVal <= 0) {
				intRtnVal = -3;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdEqp

	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE [야드설비상태 : YD_EQP_STAT]
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdEqpStat(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {

			//필드명 변환 (필드명 -> V_필드명)
			JDTORecord recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpStat
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpStat");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if(intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdEqpStat

	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE [야드설비작업Mode : YD_EQP_WRK_MODE]
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdEqpWrkMode(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {

			//필드명 변환 (필드명 -> V_필드명)
			JDTORecord recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpWrkMode
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdEqpWrkMode");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if(intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdEqpWrkMode

	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE [야드현재동구분 : YD_CURR_BAY_GP]
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdCurrBayGp(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;

		try {

			//필드명 변환 (필드명 -> V_필드명)
			JDTORecord recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// query id setting
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdCurrBayGp
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO.updYdCurrBayGp");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if(intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCurrBayGp

/*------------------------------------- DELETE -------------------------------------------*/

/*------------------------------------- ETC -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드설비 INSERT parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {

		boolean blnErr = true;

		try {
			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_ID", 6, 1, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_REGISTER", 10, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_REG_DDTT", 0, 3, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_GP", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_BAY_GP", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_GP", 2, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_NO", 2, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ALW_XAXIS_TO", 7, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_NAME", 50, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_STAT", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_EQP_WRK_MODE", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ALW_XAXIS_FR", 7, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ALW_YAXIS_FR", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ALW_YAXIS_TO", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ALW_ZAXIS_FR", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ALW_ZAXIS_TO", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_TRAVL_OFFSET", 7, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_GRAB_TP", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_TRAVS_OFFSET", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_L2_HMI_STAT", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CTS_RELAY_YN", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CTS_RELAY_BAY_GP", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_GRAB1_ACT_STAT", 1, 2, 'S', 4, 1);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_GRAB2_ACT_STAT", 1, 2, 'S', 4, 1);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ABLE_XAXIS_FR", 7, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ABLE_XAXIS_TO", 7, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ABLE_YAXIS_FR", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ABLE_YAXIS_TO", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ABLE_ZAXIS_FR", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_ABLE_ZAXIS_TO", 5, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CURR_BAY_GP", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_HOME_BAY_GP", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_TCAR_WRK_ABLE_BAY1", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_TCAR_WRK_ABLE_BAY2", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_TCAR_WRK_ABLE_BAY3", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_TCAR_WRK_ABLE_BAY4", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_TCAR_WRK_ABLE_BAY5", 1, 2, 'S', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_USE_SEQ", 1, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_CONT_CARASGN_CNT", 2, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_CRN_CONT_CARASGN_WR", 2, 2, 'L', 0, 0);
			if(!blnErr) { return blnErr; }

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter

	/**
	 *      [A] 오퍼레이션명 : 야드설비 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ALW_XAXIS_TO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_NAME");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_MODE");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ALW_XAXIS_FR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ALW_YAXIS_FR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ALW_YAXIS_TO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ALW_ZAXIS_FR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ALW_ZAXIS_FR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ALW_ZAXIS_TO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_TRAVL_OFFSET");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_GRAB_TP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_TRAVS_OFFSET");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_L2_HMI_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CTS_RELAY_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CTS_RELAY_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_GRAB1_ACT_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_GRAB2_ACT_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ABLE_XAXIS_FR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ABLE_XAXIS_TO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ABLE_YAXIS_FR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ABLE_YAXIS_TO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ABLE_ZAXIS_FR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_ABLE_ZAXIS_TO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CURR_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_HOME_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TCAR_WRK_ABLE_BAY1");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TCAR_WRK_ABLE_BAY2");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TCAR_WRK_ABLE_BAY3");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TCAR_WRK_ABLE_BAY4");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TCAR_WRK_ABLE_BAY5");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_USE_SEQ");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_CONT_CARASGN_CNT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CRN_CONT_CARASGN_WR");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}

	} // end of dataMapping

} // end of class
