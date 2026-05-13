package com.inisteel.cim.yd.common.dao.ydBedInvSumDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
    
/**
 *      [A] 클래스명 : 야드적치BED재고집계 DAO
 * 
*/

public class YdBedInvSumDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();

	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydbedinvsumdao.YdBedinvsumDao.getYdBedinvsum";
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydbedinvsumdao.YdBedinvsumDao.insYdBedinvsum";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydbedinvsumdao.YdBedinvsumDao.updYdBedinvsum";
	
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치BED재고집계 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_BED_INV_SUM_ID)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdBedinvsum(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdBedinvsum";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdBedinvsum(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
	
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0)
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
			else {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 3);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal = rsTemp.size();
	} //end of getYdBedinvsum
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치BED재고집계 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_BED_INV_SUM_ID)
	 * @return boolean          true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdBedinvsum(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		try {
			if (intGp == 0) {
				
				szFieldName = "V_YD_BED_INV_SUM_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'L', 0, 0);
	
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdBedinvsum
	
	
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치BED재고집계 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdBedinvsum(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
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
	} // end of insYdBedinvsum
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치BED재고집계 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_BED_INV_SUM_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REGISTER";			
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			
			szFieldName = "V_YD_INV_SUM_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_STK_LOT_CNT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_SH1";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_SH2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH3";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT3";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH4";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT4";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH5";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT5";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH6";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT6";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH7";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT7";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH8";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT8";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH9";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT9";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH10";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT10";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH11";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT11";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			
			szFieldName = "V_YD_INV_SUM_SH12";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_INV_SUM_WT12";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;	
			

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkPara_YdBedinvsum
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치BED재고집계 UPDATE
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_BED_INV_SUM_ID)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, 
	 *                         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdBedinvsum(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdBedinvsum";
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
			intRtnVal = this.getYdBedinvsum(inRec, outRecSet, 0);
			
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
			
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdBedinvsum
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치BED재고집계 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_BED_INV_SUM_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_NO";
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
			
			szFieldName = "V_YD_INV_SUM_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_STK_LOT_CNT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH1";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH3";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT3";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH4";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT4";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH5";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT5";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH6";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT6";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH7";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT7";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH8";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT8";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH9";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT9";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH10";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT10";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH11";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT11";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_SH12";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_INV_SUM_WT12";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
	} // end of YdBedinvsum_DataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






