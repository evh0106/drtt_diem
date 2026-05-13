/*
 * @(#) 야드적치열 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		야드적치열 DAO
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
// 2024.11.19 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드적치열 DAO
 *
*/

public class JPlateYdStkColDAO {

	// Dao Name
	private final String SZ_DAO_NAME = getClass().getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 1후판 정정 로그 관련 야드공통 UTIL 
//-------------------------------------------------------------------------------------------------------------------------
    private YdUtils 			ydLogUtils  = new YdUtils();
	
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT [조회조건:야드적치열구분]
	 *
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkcol(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnErr = true;
		JDTORecord recPara = null;
		String szMethodName = "getYdStkcol";

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(inRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 1후판 정정 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_GP", 6, 1, 'S', 0, 0);

			//parameter error return
			if (!blnErr) {
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcol
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcol");

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
				String szMsg = "data not found!";
				ydLogUtils.putLogNew(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of getYdStkcol

	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT
	 *
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkcolEqp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnErr = true;
		JDTORecord recPara = null;
		String szMethodName = "getYdStkcolEqp";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_GP", 1, 1, 'S', 0, 0);
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_BAY_GP", 1, 1, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_EQP_GP", 2, 1, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_ACT_STAT", 1, 2, 'S', 0, 0);
			}

			//parameter error return
			if (!blnErr) {
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolEqp
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcolEqp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcolEqp");

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
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of getYdStkcolEqp

	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT [조회조건::야드구분,야드동구분,야드설비구분,야드적치열번호]
	 *
	 * WHERE YD_GP             = :V_YD_GP
   AND YD_BAY_GP         = :V_YD_BAY_GP
   AND YD_EQP_GP         = :V_YD_EQP_GP
   AND YD_STK_COL_NO     = :V_YD_STK_COL_NO

	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkcolCol(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnErr = true;
		JDTORecord recPara = null;
		String szMethodName = "getYdStkcolCol";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_GP", 1, 1, 'S', 0, 0);
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_BAY_GP", 1, 1, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_EQP_GP", 2, 1, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_NO", 2, 1, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_V_YD_STK_COL_ACT_STAT", 1, 2, 'S', 0, 0);
			}

			//parameter error return
			if (!blnErr) {
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolCol
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcolCol
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcolCol");

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
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of getYdStkcolCol

	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT [for UPDATE]
	 *
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdStkcolForUpdate(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnErr = true;
		JDTORecord recPara = null;
		String szMethodName = "getYdStkcolForUpdate";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_GP", 6, 1, 'S', 0, 0);

			//parameter error return
			if (!blnErr) {
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolNotDelYn
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcolForUpdate
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.getYdStkcolForUpdate");

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
				String szMsg = "data not found!";
				ydUtils.putLog(SZ_DAO_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of getYdStkcolForUpdate

	/*------------------------------------- INSERT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치열 INSERT
	 *
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int insYdStkcol(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = -100;
		boolean blnErr = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = this.chkParameter(recPara);

			//parameter error return
			if (!blnErr){
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.insYdStkcol
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.insYdStkcol
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.insYdStkcol");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStkcol

	/**
	 *      [A] 오퍼레이션명 : 야드적치열 INSERT parameter Check
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

			blnErr = ydDaoUtils.chkField(recPara, "V_REG_DDTT", 0, 3, 'S', 0, 0);					// 등록일
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_REGISTER", 10, 2, 'S', 0, 0);				// 등록자
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_GP", 1, 2, 'S', 0, 0);					// 야드구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_BAY_GP", 1, 2, 'S', 0, 0);				// 야드동구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_EQP_GP", 2, 2, 'S', 0, 0);				// 야드설비구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_NO", 2, 2, 'S', 0, 0);			// 야드적치열번호
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_ACT_STAT", 1, 2, 'S', 0, 0);		// 야드적치열활성상태
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_RULE_XAXIS", 7, 2, 'L', 0, 0);	// 야드적치열기준X축
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_RULE_YAXIS", 5, 2, 'L', 0, 0);	// 야드적치열기준Y축
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_W", 5, 2, 'L', 0, 0);				// 야드적치열폭
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_L", 5, 2, 'L', 0, 0);				// 야드적치열길이
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_CAR_USE_GP", 1, 2, 'S', 0, 0);			// 차량사용구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_TRN_EQP_CD", 8, 2, 'S', 0, 0);				// 운송장비코드
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_CAR_NO", 15, 2, 'S', 0, 0);					// 차량번호
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_CARD_NO", 4, 2, 'S', 0, 0);					// 카드번호
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_WLOC_CD", 5, 2, 'S', 0, 0);					// 개소코드
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_PNT_CD", 4, 2, 'S', 0, 0);				// 야드포인트코드
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_W_GP", 1, 2, 'S', 0, 0);			// 야드적치열폭구분
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_H_MAX", 5, 2, 'S', 0, 0);			// 야드적치열높이Max
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_STK_COL_BED_L_TP", 2, 2, 'S', 0, 0);		// 야드적치열Bed길이Type
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(recPara, "V_YD_COIL_OUTDIA_GRP_GP", 2, 2, 'S', 0, 0);	// 야드코일외경군구분

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkPara_YdStkcol

/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치열 UPDATE
	 *
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStkcol(JDTORecord inRec) throws DAOException, JDTOException {

		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChkField = true;
		String szMethodName = "updYdStkcol";
		String szMsg = "";

		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			//필드명 변환 (필드명 -> V_필드명)
			JDTORecord recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//update data select
			intRtnVal = this.getYdStkcolForUpdate(inRec, outRecSet);

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

			//duplicate data return
			if (outRecSet.size() != 1) {
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
			if (!blnChkField){
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.updYdStkcol
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.updYdStkcol
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.updYdStkcol");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) {
				intRtnVal = -3;
			}

		} catch
		(Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkcol

	/**
	 *      [A] 오퍼레이션명 : 적치열 활성화 상태 UPDATE [YD_STK_COL_ACT_STAT]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStkColActStat(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.updYdStkColActStat
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.updYdStkColActStat");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {

			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkColActStat

	/**
	 *      [A] 오퍼레이션명 : 적치열검수 UPDATE [CARD_NO]
	 *
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updCardNo(JDTORecord inRec) throws DAOException, JDTOException {

		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리 아이디 세팅
			// 기존쿼리 :
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.updCardNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkColDAO.updCardNo");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {

			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updCardNo

	/**
	 *      [A] 오퍼레이션명 : 야드적치열 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_GP");			// 야드적치열구분
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");				// 등록자
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");				// 등록일
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");				// 수정자
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");				// 수정일
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");					// 삭제유무
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_GP");					// 야드구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_BAY_GP");				// 야드동구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_GP");				// 야드설비구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_NO");			// 야드적치열번호
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_ACT_STAT");		// 야드적치열활성상태
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_RULE_XAXIS");	// 야드적치열기준X축
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_RULE_YAXIS");	// 야드산적Lot코드
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_W");			// 야드적치열폭
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_L");			// 야드적치열길이
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_USE_GP");			// 차량사용구분
			ydDaoUtils.mappingData(inRec, outRec, "V_TRN_EQP_CD");				// 운송장비코드
			ydDaoUtils.mappingData(inRec, outRec, "V_CAR_NO");					// 차량번호
			ydDaoUtils.mappingData(inRec, outRec, "V_CARD_NO");					// 카드번호
			ydDaoUtils.mappingData(inRec, outRec, "V_WLOC_CD");					// 개소코드
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_PNT_CD");				// 야드포인트코드
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_W_GP");			// 야드적치열폭구분
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_H_MAX");		// 야드적치열높이Max
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_COL_BED_L_TP");		// 야드적치열Bed길이Type
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_COIL_OUTDIA_GRP_GP");	// V_YD_COIL_OUTDIA_GRP_GP
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STKBED_USG_CD");		// YD_STKBED_USG_CD

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}

	} // end of dataMapping

/*------------------------------------- DELETE -------------------------------------------*/
} // end of class
