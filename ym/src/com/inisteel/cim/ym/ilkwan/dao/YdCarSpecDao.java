package com.inisteel.cim.ym.ilkwan.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.common.YmCommonUtil;

/**
 *      [A] 클래스명 : 야드차량사양 DAO
 * 
*/

public class YdCarSpecDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YmCommonUtil ymCommonUtil = new YmCommonUtil();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydcarspecdao.YdCarspecDao.getYdCarspec";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydcarspecdao.YdCarspecDao.getYdCarspecEQPID";
	//이현성 20090308 
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydcarspecdao.YdCarspecDao.getYdCarspecTRN_EQP_CD";
	//이현성 20090310 
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydcarspecdao.YdCarspecDao.getYdCarspecTrnEqpClass";
	//권오창 20090318
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydcarspecdao.YdCarspecDao.getYdCarspecCarNoTrnEqp";	
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydcarspecdao.YdCarspecDao.insYdCarspec";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydcarspecdao.YdCarspecDao.updYdCarspec";

	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량사양 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_CAR_REG_SEQ,YD_EQP_ID
	 *                                      1:YD_EQP_ID,YD_CAR_USE_GP[LIKE],TRN_EQP_CD[LIKE],CAR_NO[LIKE]
	 *                                      2:TRN_EQP_CD
	 *                                      3:TRN_EQP_CLASS
	 *                                      4:CAR_NO)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdCarspec(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdCarspec";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ymCommonUtil.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdCarspec(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if (intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if (intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
				//data not found
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdCarspec
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량사양 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_CAR_REG_SEQ,YD_EQP_ID
	 *                               1:YD_EQP_ID,YD_CAR_USE_GP[LIKE],TRN_EQP_CD[LIKE],CAR_NO[LIKE]
	 *                               2:TRN_EQP_CD
	 *                               3:TRN_EQP_CLASS
	 *                               4:CAR_NO)
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdCarspec(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0) {
				
				szFieldName = "V_YD_CAR_REG_SEQ";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if (intGp == 1) {
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			} else if (intGp == 2) {				
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				
			} else if (intGp == 3) {	//TEST 화면 체크하지않는다. 			
				szFieldName = "V_TRN_EQP_CLASS";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);				
			} else if (intGp == 4) {				
				szFieldName = "V_CAR_NO";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdCarspec
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량사양 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdCarspec(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ymCommonUtil.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
	
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdIns1);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdCarspec
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량사양 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_CAR_REG_SEQ";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_ID";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_REGISTER";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_MODIFIER";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_MOD_DDTT";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEL_YN";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_USE_GP";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRN_EQP_CD";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CAR_NO";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_L";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_W";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_SKID_PITCH";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_SH";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_ALW_WT";
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량사양 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_CAR_REG_SEQ,YD_EQP_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCarspec(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdCarspec";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		try {
			//recordSet create
			JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			//변환용 레코드
			JDTORecord recInPara = null;
			JDTORecord recOutPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ymCommonUtil.conversionFieldname(inRec, 0);
			
			//update data select
			intRtnVal = this.getYdCarspec(inRec, outRecSet, 0);
			
			//parameter error return
			if (intRtnVal < 0) {
	//			szMsg = "parameter error!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//data not found return
			if (intRtnVal == 0) {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//duplicate data return
			if (outRecSet.size() != 1) {
	//			szMsg = "duplicate data!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
			
			outRecSet.first();
			outRec = outRecSet.getRecord();
			
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ymCommonUtil.conversionFieldname(outRec, 0);
			
	
			//data mapping
			this.dataMapping(recInPara, recOutPara);
			
			//parameter check
			blnChk_Field = this.chkParameter(recOutPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCarspec
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량사양 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_CAR_REG_SEQ";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_ID";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_REGISTER";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_MODIFIER";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_MOD_DDTT";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_USE_GP";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_TRN_EQP_CD";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CAR_NO";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_L";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_W";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_SKID_PITCH";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_SH";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_ALW_WT";
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	} // end of dataMapping
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






