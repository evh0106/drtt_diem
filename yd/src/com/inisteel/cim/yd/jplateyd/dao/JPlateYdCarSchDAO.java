/*
 * @(#) 야드차량스케줄 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		야드차량스케줄 DAO
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
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdDaoUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdUtils;
import com.inisteel.cim.yd.jplateyd.util.JPlateYdConst;

/**
 *      [A] 클래스명 : 야드차량스케줄 DAO
 *
*/

public class JPlateYdCarSchDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdCarSchDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

/*------------------------------------- SELECT -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 SELECT , intGp == 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdCarSch(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp = null;
		boolean blnErr = true;
		int intRtnVal = -100;
		JDTORecord recPara = null;
		String szMethodName = "getYdCarSch";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_CAR_SCH_ID", 18, 1, 'S', 0, 0);

			//parameter error return
			if (!blnErr) {
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.getYdCarSch
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.getYdCarSch");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
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
	} //end of getYdCarSch

	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 SELECT [운송장비코드] , intGp == 7
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdCarschDaoTrnEqpCd(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp = null;
		boolean blnErr = true;
		int intRtnVal = -100;
		JDTORecord recPara = null;
		String szMethodName = "getYdCarschDaoTrnEqpCd";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_EQP_ID", 6, 1, 'S', 0, 0);

			//parameter error return
			if (!blnErr) {
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschDaoTrnEqpCd
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.getYdCarschDaoTrnEqpCd
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.getYdCarschDaoTrnEqpCd");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
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
	} //end of getYdCarschDaoTrnEqpCd

	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 SELECT [차량번호,카드번호] , intGp == 11
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdCarschCarNoCardNo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnErr = true;
		JDTORecord recPara = null;
		String szMethodName = "getYdCarschCarNoCardNo";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_EQP_ID", 6, 1, 'S', 0, 0);

			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_GP1", 1, 1, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_GP2", 1, 1, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_EQP_GP", 2, 3, 'S', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_PAGE_CNT1", 9, 1, 'P', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_ROW_CNT1", 9, 1, 'R', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_PAGE_CNT2", 9, 1, 'P', 0, 0);
			}
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_ROW_CNT2", 9, 1, 'R', 0, 0);
			}

			//parameter error return
			if (!blnErr) {
				return -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschCarNoCardNo_PIDEV
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.getYdCarschCarNoCardNo
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.getYdCarschCarNoCardNo");

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
	} //end of getYdCarschCarNoCardNo

	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 SELECT , intGp == 3
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdCarschByWrkBookId(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		JDTORecordSet rsTemp = null;
		int intRtnVal 		= -100;
		JDTORecord recPara 	= null;
		String szMethodName = "getYdCarschByWrkBookId";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschUDLDWRKBOOKID_PIDEV
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.getYdCarschByWrkBookId
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.getYdCarschByWrkBookId");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0){
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
	} //end of getYdCarschByWrkBookId

/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 UPDATE
	 *
	 * @param  JDTORecord inRec parameter record
	 *
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdCarSch(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName = "updYdCarsch";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnErr = true;

		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");

			//변환용 레코드
			JDTORecord recInPara = null;
			JDTORecord recOutPara = null;

			//수정
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//update data select
			intRtnVal = this.getYdCarSch(inRec, outRecSet);

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

			//parameter check
			blnErr = this.chkParameter(recOutPara);

			//parameter error return
			if (!blnErr) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.updYdCarsch
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.updYdCarSch
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdCarSchDAO.updYdCarSch");

			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);

			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCarsch

/*------------------------------------- ETC -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 *        JDTORecord updRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_SCH_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_USE_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_CAR_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_TRN_EQP_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_CAR_KIND");
			ydDaoUtils.mappingData(inRec, outRec, "V_TRANS_EQUIPMENT_TYPE");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_PROG_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_SH");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_EQP_WRK_WT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_STK_BED_TP");
			ydDaoUtils.mappingData(inRec, outRec, "V_SPOS_WLOC_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_ARR_WLOC_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_LEV_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_LEV_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_PNT_WO_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_PNT_CD1");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_PNT_CD2");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_WRK_BOOK_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_SCH_REQ_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_STOP_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_ARR_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_ST_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_CMPL_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_WRK_ACT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARLD_CHK_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_LEV_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_PNT_WO_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_PNT_CD3");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_PNT_CD4");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_WRK_BOOK_ID");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_STOP_LOC");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_SCH_REQ_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_ARR_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_CHK_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_ST_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_CMPL_DT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CARUD_WRK_ACT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_TRN_WRK_DELY_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_CARD_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_PROG_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_FRTOMOVE_PLANT_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_PROC_TO");
			ydDaoUtils.mappingData(inRec, outRec, "V_RENTPROC_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_FRTOMOVE_YD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_FRTOMOVE_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_URGENT_FRTOMOVE_WORD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEST_TEL_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_DLVRDD_RULE_DD");
			ydDaoUtils.mappingData(inRec, outRec, "V_SHIPASSIGN_WORD_DATE");
			ydDaoUtils.mappingData(inRec, outRec, "V_SHIPASSIGN_WORD_SEQNO");
			ydDaoUtils.mappingData(inRec, outRec, "V_SHIP_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_SHIP_NAME");
			ydDaoUtils.mappingData(inRec, outRec, "V_RSHP_HOLD_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_BERTH_NO");
			ydDaoUtils.mappingData(inRec, outRec, "V_SAILNO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_WRK_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_TRANS_ORD_DATE");
			ydDaoUtils.mappingData(inRec, outRec, "V_TRANS_ORD_SEQNO");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_BAYIN_WO_SEQ");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_RCPT_CHK_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_ISSUE_CHK_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_RCPT_CHECKER");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_CAR_ISSUE_CHECKER");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
	} // end of dataMapping

	/**
	 *      [A] 오퍼레이션명 : 야드차량스케줄 INSERT parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

	    String szMsg              		 = "";
	    String szMethodName       		 = "chkParameter";
	    szMsg = "chkParameter() In";
		ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);

		try {
			szFieldName = "V_YD_CAR_SCH_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
//			if (!blnErr) { return blnErr; }
//
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_EQP_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_USE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_CAR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_TRN_EQP_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_CAR_KIND";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_TRANS_EQUIPMENT_TYPE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_EQP_WRK_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_WRK_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_EQP_WRK_SH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_EQP_WRK_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_STK_BED_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_SPOS_WLOC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_ARR_WLOC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_LEV_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_LEV_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_PNT_WO_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_PNT_CD1";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_PNT_CD2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_STOP_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_ARR_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_ST_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARLD_CHK_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_LEV_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_PNT_WO_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_PNT_CD3";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_PNT_CD4";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_WRK_BOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_STOP_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_ARR_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_CHK_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_ST_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'T', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CARUD_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_TRN_WRK_DELY_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_CARD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_FRTOMOVE_PLANT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_PROC_TO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_RENTPROC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_FRTOMOVE_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_FRTOMOVE_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_URGENT_FRTOMOVE_WORD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_DEST_TEL_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 20, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_DLVRDD_RULE_DD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_SHIPASSIGN_WORD_DATE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_SHIPASSIGN_WORD_SEQNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_SHIP_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_SHIP_NAME";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 50, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_RSHP_HOLD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_BERTH_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_SAILNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_WRK_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_TRANS_ORD_DATE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_TRANS_ORD_SEQNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_BAYIN_WO_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_RCPT_CHK_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_ISSUE_CHK_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_RCPT_CHECKER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			szFieldName = "V_YD_CAR_ISSUE_CHECKER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}

	    szMsg = "chkParameter() Out (" + blnErr + ")";
		ydUtils.putLog(YdCarSchDao.class.getName(), szMethodName, szMsg, 4);

		return blnErr;
	} //end of chkParameter

} // end of class
