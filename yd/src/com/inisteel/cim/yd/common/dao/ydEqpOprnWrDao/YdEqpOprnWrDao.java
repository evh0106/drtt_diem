package com.inisteel.cim.yd.common.dao.ydEqpOprnWrDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드설비가동실적 DAO
 * 
*/

public class YdEqpOprnWrDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydeqpoprnwrdao.YdEqpoprnwrDao.getYdEqpoprnwr";
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydeqpoprnwrdao.YdEqpoprnwrDao.insYdEqpoprnwr";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydeqpoprnwrdao.YdEqpoprnwrDao.updYdEqpoprnwr";

	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비가동실적 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_EQP_OPRN_WR_ID, YD_EQP_ID)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdEqpoprnwr(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdEqpoprnwr";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdEqpoprnwr(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
	
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
	} //end of getYdEqpoprnwr
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비가동실적 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_EQP_OPRN_WR_ID, YD_EQP_ID)
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdEqpoprnwr(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			if (intGp == 0) {
				szFieldName = "V_YD_EQP_OPRN_WR_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdEqpoprnwr
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비가동실적 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdEqpoprnwr(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
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
	} // end of insYdEqpoprnwr
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비가동실적 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_EQP_OPRN_WR_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//	
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_HDS_DD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_DUTY";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_PARTY";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_PREP_HR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_WRK_WAIT_HR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_OFFLN_WRK_HR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_PAUSE_HR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_PURE_WRK_TIME";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비가동실적 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_EQP_OPRN_WR_ID, YD_EQP_ID)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdEqpoprnwr(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdEqpoprnwr";
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
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			
			//update data select
			intRtnVal = this.getYdEqpoprnwr(inRec, outRecSet, 0);
			
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
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);
	
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
	} // end of updYdEqpoprnwr
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드설비가동실적 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_EQP_OPRN_WR_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_REGISTER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_MODIFIER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_MOD_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_HDS_DD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_DUTY";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_PARTY";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_PREP_HR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_WRK_WAIT_HR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_OFFLN_WRK_HR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_PAUSE_HR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_PURE_WRK_TIME";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of dataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






