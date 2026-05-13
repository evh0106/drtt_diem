package com.inisteel.cim.yd.common.dao.ydPrepMtlDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;    
/**
 *      [A] 클래스명 : 야드준비스케줄재료 DAO
 * 
*/

public class YdPrepMtlDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
// PIDEV
	private YdPICommDAO ydPICommDAO = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();		
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();

	//select query id - 20090929 이현성(테이블 컬럼변경)
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.getYdPrepmtl";
	//준비스케줄ID로 조회 쿼리 - 임춘수 2009.09.28
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.getYdPrepmtlByPrepSchId";
	//준비스케줄ID로 저장품과 조인해서 조회 쿼리 - 임춘수 2009.09.28
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.getYdPrepmtlNStockByPrepSchId";
	//준비스케줄ID로 저장품과 조인해서 조회 쿼리  - 이송지시취소재료 표시 - 임춘수 2009.10.27
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.getYdPrepmtlNWordCancelByPrepSchId";
	//준비스케줄ID로 조회 쿼리 - 적치열DESC, 적치베드 DESC, 적치단 DESC : 임춘수 2009.11.11
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.getYdPrepmtlByPrepSchIdDesc";
	// JMS_구내운송_제품운송요구취소 - 석창화 2009.11.19
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.CancelJMS_YDTSJ015";
	//준비재료와 저장품 조인 - 준비스케줄ID로 조회(적치열DESC, 적치베드DESC, 적치단DESC) : 임춘수 2010.01.13
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.getYdPrepmtlNStockByPrepSchIdDesc";

	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.getYdPrepmtlNStockByPrepSchIdCoil";
	
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.insYdPrepmtl";
	//update query id - 20090929 이현성(테이블 컬럼변경)
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.updYdPrepmtl";
	//실제적으로 레코드를 삭제하는 쿼리 - 준비스케줄ID : 임춘수 2009.09.28
	private String szQueryIdDel1 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.delYdPrepmtlByPrepSchId";
	//레코드의 DEL_YN항목에 Y/N를 설정하는 쿼리
	private String szQueryIdDel2 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.delYdPrepmtlByPrepSchId1";
	//실제적으로 레코드를 삭제하는 쿼리 - 준비스케줄ID와 준비재료 : 임춘수 2009.09.28
	private String szQueryIdDel3 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.delYdPrepmtlStlByPrepSchId";
	//CTYDJ031에 의해 레코드의 DEL_YN항목에 Y/N를 설정하는 쿼리
	private String szQueryIdDel4 = "com.inisteel.cim.yd.dao.ydprepmtldao.YdPrepmtlDao.delYdPrepmtl_CTYDJ031";
/*------------------------------------- SELECT -------------------------------------------*/
	/**
	 * 물리적으로 저장속성레코드를 삭제하는 함수
	 * @param inRec
	 * @return
	 * @throws DAOException
	 */
	public int delYdPrepmtlByPrepSchId(JDTORecord inRec, int intGp) throws DAOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
		JDTORecord recPara = null;
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdPrepmtl(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			if( intGp == 0 )
				recPara.setField("JSPEED_QUERY_ID", szQueryIdDel3);
			else if( intGp == 1 )
				recPara.setField("JSPEED_QUERY_ID", szQueryIdDel1);
			//recPara.setField("JSPEED_QUERY_ID", szQueryIdDel2);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 * 레코드의 DEL_YN항목에 Y/N를 설정
	 * @param inRec
	 * @return
	 * @throws DAOException
	 */
	public int uptDelYdPrepmtlByPrepSchId(JDTORecord inRec) throws DAOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
		JDTORecord recPara = null;
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdPrepmtl(recPara, 1);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdDel2);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 * 레코드의 DEL_YN항목에 Y/N를 설정
	 * @param inRec
	 * @return
	 * @throws DAOException
	 */
	public int uptDelYdPrepmtl_CTYDJ031(JDTORecord inRec) throws DAOException {
		int intRtnVal = 0;
		boolean blnChk_Field = false;
		JDTORecord recPara = null;
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			
			//query id setting
			recPara.setField("JSPEED_QUERY_ID", szQueryIdDel4);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:STL_NO,YD_PREP_SCH_ID
	 *         								 1:YD_PREP_SCH_ID
	 *         								 2:YD_PREP_SCH_ID
     *                                       4:YD_PREP_SCH_ID
	 *                                       5:STL_NO,YD_PREP_SCH_ID
	 *                                       6:YD_PREP_SCH_ID
	 *         								)
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdPrepmtl(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdPrepmtl";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdPrepmtl(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			if (intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			if (intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			if (intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			if (intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			if (intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			
// PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");				
//            String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", recPara.getFieldString("JSPEED_QUERY_ID"), "APPPI0", sPI_YD, "*" );
//            recPara.setField("JSPEED_QUERY_ID", recPara.getField("JSPEED_QUERY_ID").toString());
            
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
	} //end of getYdPrepmtl
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO,YD_PREP_SCH_ID)
	 *                              5:STL_NO,YD_PREP_SCH_ID 
	 *                              6:YD_PREP_SCH_ID
	 * @return boolean          true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdPrepmtl(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		try {
			if (intGp == 0 || intGp == 5) {
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
								
				szFieldName = "V_YD_PREP_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
							
				
			}else if (intGp == 1 || intGp == 2 || intGp == 4) {
								
				szFieldName = "V_YD_PREP_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			}else if (intGp == 3 ) {
				szFieldName = "V_SPOS_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			}else if (intGp == 6 ) {
				szFieldName = "V_YD_PREP_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdPrepmtl
	
	
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdPrepmtl(JDTORecord inRec) throws DAOException, JDTOException {
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
	} // end of insYdPrepmtl
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_STL_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PREP_SCH_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
//			szFieldName = "V_REG_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
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
			
			szFieldName = "V_YD_STK_COL_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_PLAN_CRN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkPara_YdPrepmtl
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 UPDATE
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:STL_NO,YD_PREP_SCH_ID)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, 
	 *                         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdPrepmtl(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdPrepmtl";
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
			intRtnVal = this.getYdPrepmtl(inRec, outRecSet, 0);
			
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
	} // end of updYdPrepmtl
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드준비스케줄 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_STL_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_PREP_SCH_ID";
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
			
			szFieldName = "V_YD_STK_COL_GP";	
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_NO";	
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_NO";	
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_PLAN_CRN";	
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
		
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		
	} // end of YdPrepmtl_DataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






