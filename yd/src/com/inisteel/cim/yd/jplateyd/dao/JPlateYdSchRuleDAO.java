/*
 * @(#) 야드스케줄기준 DAO
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		야드스케줄기준 DAO
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

/**
 *      [A] 클래스명 : 야드스케줄기준 DAO
 *
*/

public class JPlateYdSchRuleDAO {

	// Dao Name
	private static final String SZ_DAO_NAME = JPlateYdSchRuleDAO.class.getName();

	private JPlateYdUtils 		ydUtils 	= new JPlateYdUtils();
	private DBAssistantDAO 		dbAssDao 	= new DBAssistantDAO();
	private JPlateYdDaoUtils 	ydDaoUtils 	= new JPlateYdDaoUtils();

/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드스케줄기준 SELECT , intGp = 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdSchrule(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= -100;
		boolean blnErr 			= true;
		JDTORecord recPara 		= null;
		String szMethodName 	= "getYdSchrule";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_SCH_CD", 8, 1, 'S', 0, 0);

			//parameter error return
			if (!blnErr){
				return -2;
			}

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchrule
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchrule");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getYdSchrule


	/**
	 *      [A] 오퍼레이션명 : 야드스케줄기준, 대상 설비 SELECT , intGp = 0
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdSchruleWithEqp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= -100;
		boolean blnErr 			= true;
		JDTORecord recPara 		= null;
		String szMethodName 	= "getYdSchruleWithEqp";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_SCH_CD", 8, 1, 'S', 0, 0);

			//parameter error return
			if (!blnErr){
				return -2;
			}

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchrule
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchrule
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchruleWithEqp");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getYdSchrule	
	
	/**
	 *      [A] 오퍼레이션명 : 야드스케줄기준 SELECT [리스케쥴] , intGp = 7
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getYdSchruleYdBayGp(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= -100;
		boolean blnErr 			= true;
		JDTORecord recPara 		= null;
		String szMethodName 	= "getYdSchruleYdBayGp";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_GP", 1, 1, 'S', 0, 0);
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_BAY_GP", 1, 1, 'S', 0, 0);
			}

			//parameter error return
			if (!blnErr){
				return -2;
			}

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchruleYdGpYdBayGp
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchruleYdBayGp
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getYdSchruleYdBayGp");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getYdSchruleByBayGp

	/**
	 *      [A] 오퍼레이션명 : 야드스케줄기준 리스트 SELECT , intGp = 4
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getSchRuleList(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= -100;
		boolean blnErr 			= true;
		JDTORecord recPara 		= null;
		String szMethodName 	= "getSchRuleList";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_GP", 1, 1, 'S', 0, 0);
			if (blnErr) {
				blnErr = ydDaoUtils.chkField(recPara, "V_YD_BAY_GP", 1, 2, 'S', 0, 0);
			}

			//parameter error return
			if (!blnErr){
				return -2;
			}

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getSchRuleList
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getSchRuleList
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getSchRuleList");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
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
	} //end of getSchRuleList

	/**
	 *      [A] 오퍼레이션명 : 야드스케줄기준 SELECT [스케쥴기동화면] , intGp = 1
	 *
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet

	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int getJPlateYdSchStartMgt(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {

		JDTORecordSet rsTemp 	= null;
		int intRtnVal 			= -100;
		boolean blnErr 			= true;
		JDTORecord recPara 		= null;
		String szMethodName 	= "getJPlateYdSchStartMgt";

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			//parameter check
			blnErr = ydDaoUtils.chkField(recPara, "V_YD_GP", 1, 1, 'S', 0, 0);

			//parameter error return
			if (!blnErr){
				return -2;
			}

			// 기존쿼리 : com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.getYdSchruleYdGp_PAGE
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getJPlateYdSchStartMgt
			recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.getJPlateYdSchStartMgt");

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);

			//result recordSet check
			if (rsTemp.size() > 0) {
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
	} //end of getJPlateYdSchStartMgt

/*------------------------------------- UPDATE -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드스케줄기준 UPDATE , intGp == 0
	 *
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:YD_SCH_CD)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdSchrule(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName = "updYdSchrule";
		String szMsg 		= null;
		JDTORecord outRec 	= null;
		int intRtnVal 		= 0;
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
			intRtnVal = this.getYdSchrule(inRec, outRecSet);			// intGp == 0

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
			blnChkField = this.chkParameter(recOutPara);

			//parameter error return
			if (!blnChkField) {
				return intRtnVal = -2;
			}

			//query id setting
			// 기존쿼리 : com.inisteel.cim.yd.dao.ydschruledao.YdSchruleDao.updYdSchrule
			// 변경쿼리 : com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.updYdSchrule
			recOutPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO.updYdSchrule");

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
	} // end of updYdSchrule

/*------------------------------------- DELETE -------------------------------------------*/


/*------------------------------------- ETC    -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드스케줄기준 INSERT parameter Check
	 *
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {

		boolean blnErr = true;

		try {
			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_CD", 8, 1, 'S', 0, 0);
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
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_BAY_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_RNG_CD", 4, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_WHIO_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_DIV_GP", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_RULE_ACT_STAT", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_CRN", 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_WRK_CRN_PRIOR", 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_ALT_CRN_YN", 1, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_ALT_CRN", 6, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_ALT_CRN_PRIOR", 2, 2, 'L', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_CD_CONTENTS", 100, 2, 'S', 0, 0);
			if (!blnErr) { return blnErr; }

			blnErr = ydDaoUtils.chkField(inRec, "V_YD_SCH_PROH_EXN", 1, 2, 'S', 0, 0);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter

	/**
	 *      [A] 오퍼레이션명 : 야드스케줄기준 UPDATE parameter mapping
	 *
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException
	 */
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {

		try {

			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_REGISTER");
			ydDaoUtils.mappingData(inRec, outRec, "V_REG_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_MODIFIER");
			ydDaoUtils.mappingData(inRec, outRec, "V_MOD_DDTT");
			ydDaoUtils.mappingData(inRec, outRec, "V_DEL_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_BAY_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_RNG_CD");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_WHIO_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_DIV_GP");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_RULE_ACT_STAT");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_CRN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_WRK_CRN_PRIOR");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_ALT_CRN_YN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_ALT_CRN");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_ALT_CRN_PRIOR");
			ydDaoUtils.mappingData(inRec, outRec, "V_CD_CONTENTS");
			ydDaoUtils.mappingData(inRec, outRec, "V_YD_SCH_PROH_EXN");

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(SZ_DAO_NAME + e.getMessage(), e);
		}
	} // end of dataMapping

} // end of class
