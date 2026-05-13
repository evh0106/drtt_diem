package com.inisteel.cim.ym.ilkwan.dao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.common.YmCommonUtil;

/**
 *      [A] 클래스명 : 야드적치열 DAO
 * 
*/

public class YdStkColDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YmCommonUtil ymCommonUtil = new YmCommonUtil();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
		
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolEqp";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolCol";
	
	//이현성[090305]_후판제품창고 MAP
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolBedInfo_PAGE";	
	//이현성[090308]
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolWLocCdandPntCd";
	//김창일[090312]
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdBaySetList_PAGE";
	//김창일[090312]
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getCoilYdColStsSetInfo";
	//김창일[090312]
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkPosSet_PAGE";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.insYdStkcol";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.ym.dao.ydstkcoldao.YdStkcolDao.updYdStkcol";

	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT
	 *      
	 * @param  JDTORecord    inRec      parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int           intGp      구분(0:YD_STK_COL_GP,
	 *                                      1:YD_GP,YD_BAY_GP,YD_EQP_GP,
	 *                                      2:YD_GP,YD_BAY_GP,YD_EQP_GP,YD_STK_COL_NO
	 *                                      3:YD_STK_COL_NO1 ,YD_STK_COL_NO2,YD_STK_COL_NO3,PAGE_CNT1,ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                                      4:V_WLOC_CD ,  V_YD_PNT_CD
	 *                                      7:V_YD_GP, V_YD_BAY_GP, V_YD_EQP_GP, V_YD_STK_COL_NO, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                                      )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdStkcol(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStkcol";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ymCommonUtil.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStkcol(recPara, intGp);
			
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
			else if (intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			else if (intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			else if (intGp == 7)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet8);
			
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
	} //end of getYdStkcol
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_STK_COL_GP,
	 *                               1:YD_GP,YD_BAY_GP,YD_EQP_GP,
	 *                               2:YD_GP,YD_BAY_GP,YD_EQP_GP,YD_STK_COL_NO
	 *                               3:YD_STK_COL_NO1 ,YD_STK_COL_NO2,YD_STK_COL_NO3,PAGE_CNT1,ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                               4:V_WLOC_CD ,  V_YD_PNT_CD
	 *                               5:V_YD_GP, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                               6:V_YD_STK_COL_GP
	 *                               )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdStkcol(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			if (intGp == 0 || intGp == 6) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if (intGp == 1) {
			
				szFieldName = "V_YD_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
			} else if (intGp == 2) {
				
				szFieldName = "V_YD_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			}else if (intGp == 3) {
				
				szFieldName = "V_YD_STK_COL_NO1";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO2";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO3";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if (intGp == 4) {
				
				szFieldName = "V_WLOC_CD";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PNT_CD";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
			} else if (intGp == 5) {
				szFieldName = "V_YD_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_PAGE_CNT1";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);

			} else if (intGp == 7) {
				szFieldName = "V_YD_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_BAY_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ymCommonUtil.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);

			} 
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStkcol
	
	
	
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
	} // end of insYdStkcol
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_STK_COL_GP";   	//야드적치열구분
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_REG_DDTT";			// 등록일
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_REGISTER";			// 등록자
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_MOD_DDTT";			// 수정일
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_MODIFIER";			// 수정자
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEL_YN";			// 삭제유무
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_GP";			// 야드구분
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BAY_GP";		// 야드동구분
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_GP";		// 야드설비구분
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_COL_NO";	// 야드적치열번호
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_ACT_STAT";		// 야드적치열활성상태
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_RULE_XAXIS";	// 야드적치열기준X축
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_RULE_YAXIS";	// 야드적치열기준Y축
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_W";				// 야드적치열폭
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_COL_L";				// 야드적치열길이
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CAR_USE_GP";	// 차량사용구분
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRN_EQP_CD";	// 운송장비코드
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CAR_NO";	// 차량번호
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CARD_NO";	// 카번호
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_WLOC_CD";	// 개소코
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PNT_CD";	// 야드포인트코드
			blnErr = ymCommonUtil.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
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
	public int updYdStkcol(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStkcol";
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
			intRtnVal = this.getYdStkcol(inRec, outRecSet, 0);
			
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
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			
			//query execute
			if (intGp == 0)
				intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkcol
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치열 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_STK_COL_GP";	// 야드적치열구분
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REGISTER";			// 등록자
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";			// 등록일
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MODIFIER";			// 수정자
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MOD_DDTT";			// 수정일
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";			// 삭제유무
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_GP";			// 야드구분
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_BAY_GP";		// 야드동구분
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_GP";		// 야드설비구분
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_NO";	// 야드적치열번호
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_ACT_STAT";	 // 야드적치열활성상태
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_RULE_XAXIS"; // 야드적치열기준X축
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_RULE_YAXIS"; // 야드산적Lot코드
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_W";			 // 야드적치열폭
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_COL_L";			 // 야드적치열길이
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CAR_USE_GP";	// 차량사용구분
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_TRN_EQP_CD";	// 운송장비코드
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CAR_NO";	// 차량번호
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CARD_NO";	// 카번호
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_WLOC_CD";	// 개소코
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PNT_CD";	// 야드포인트코드
			ymCommonUtil.mappingData(inRec, outRec, szFieldName);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of ydStkcol_DataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






