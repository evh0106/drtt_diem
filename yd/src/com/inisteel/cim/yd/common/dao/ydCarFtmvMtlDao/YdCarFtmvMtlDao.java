package com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 *      [A] 클래스명 : 야드차량이송재료 DAO
 * 
*/

public class YdCarFtmvMtlDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
	private CCommUtils	   commUtils   = new CCommUtils();
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtl";
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtl2";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlID";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlYdStock_PAGE";
	
	//이현성 2009.03.06 
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getPlateYdCarLiftInfo_PAGE";
	
	//이현성 2009.03.010 
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlBySchId";
	//김창일 2009.03.01 
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getCoilYdMvMtlList_PAGE";
	
	//이현성  2009.0406_차량 스케줄 ID 로 이송재 페이지 조회 
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getCoilYdMvMtlByCarSchId_PAGE";
	
	//이현성 쿼리분리작업 - makeDMR018
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.makeDMR018";
	
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getCoilYmMvMtlByCarSchId_PAGE";
	
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getSlabYmMvMtlByCarSchId_PAGE";

	// 2009.12.21 권오창 - 차량번호로 차량에 재료번호와 관련정보 조회
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getCarSTLInfoByCarNo";
	
	//이현성  2010.01.26_차량 스케줄 ID 로 이송재 페이지 조회 
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getplateGdsYdMvMtlByCarSchId_PAGE_PIDEV";
	
	//차량이송재료, 적치단, 저장품 조인 - 임춘수 2010.02.01
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlStockStkLyrByCarSchId";
	
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.getYdCarftmvmtlUppLocCd";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.insYdCarftmvmtl";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtl";
	//update query id
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtlDelByCarSchId";
	//update query id
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.UpdateStkLayNoCHK";
	
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.DeleteStkLayNoCHK";
	
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updateCarSch";
	
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updateCarLayer";
	
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updateCarMapLayer";
	
	private String szQueryIdUpd8 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteWorkPorthist";
	
	// 2009.10.14 (삭제된 Record와 관계없이 데이터를 다시 복구 시키기위한 쿼리) 
	private String szQueryIdUpd9 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtl_Direct";
	
	private String szQueryIdUpd10 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdStlFrtMoveCancel";
	
	private String szQueryIdUpd11 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteYdCarftmvmtl";
	
	private String szQueryIdUpd12 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.deleteYdExaminationmtl";
	
	private String szQueryIdUpd13 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdCarftmvmtlUppLocCd";
	
	private String szQueryIdUpd14 = "com.inisteel.cim.yd.dao.ydcarftmvmtldao.YdCarftmvmtlDao.updYdStkCarLotId";
	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량이송재료 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_CAR_SCH_ID,STL_NO
	 *                                      1:YD_CAR_SCH_ID
	 *                                      2:YD_GP,YD_EQP_GP,CAR_NO[PAGE]
	 *                                      3:YD_GP, YD_EQP_GP,CAR_NO,PAGE_CNT1,ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                                      4:YD_CAR_SCH_ID
	 *                                      5:V_YD_STK_COL_GP, V_STL_NO, V_FRTOMOVE_PLANT_GP, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                                      6:YD_CAR_SCH_ID,V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2 )
	 *                                      10:CAR_NO
	 *                                      12:YD_CAR_SCH_ID
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdCarftmvmtl(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdCarftmvmtl";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
					
			//parameter check
			blnChk_Field = this.chkPara_getYdCarftmvmtl(recPara, intGp);
			
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
			else if (intGp == 8)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9); 
			else if (intGp == 9)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet10); 
			else if (intGp == 10)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet11); 
			else if (intGp == 11)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet12);
			else if (intGp == 12)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet13); 
			else if (intGp == 13)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet14);
			else if (intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301); 
			
			// PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");			
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", recPara.getField("JSPEED_QUERY_ID").toString(), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", toQuery_ID);
			
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
	} //end of getYdCarftmvmtl
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량이송재료 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_CAR_SCH_ID,STL_NO
	 *                               1:YD_CAR_SCH_ID
	 *                               2:YD_GP,YD_EQP_GP,CAR_NO[PAGE]
	 *                               3:YD_GP, YD_EQP_GP,CAR_NO,PAGE_CNT1,ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                               4:YD_CAR_SCH_ID
	 *                               5:V_YD_STK_COL_GP, V_STL_NO, V_FRTOMOVE_PLANT_GP, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                               6:YD_CAR_SCH_ID
	 *                                ...
	 *                               10:CAR_NO                              
	 *                               11:YD_CAR_SCH_ID
	 *                               12:YD_CAR_SCH_ID
	 *                               )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdCarftmvmtl(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0) {
				
				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			} else if (intGp == 1 || intGp == 4  || intGp == 7) {
				
				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if (intGp == 2) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
		
			} else if (intGp == 3) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			
			} else if (intGp == 5) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName,  6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_FRTOMOVE_PLANT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2,  2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'R', 0, 0);
			} else if (intGp == 6 || intGp == 11) {
				
				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'R', 0, 0);
			} else if(intGp == 10){
				szFieldName = "V_CAR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;				
			} else if ( intGp == 12) {
				
				szFieldName = "V_YD_CAR_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdCarftmvmtl
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량이송재료 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdCarftmvmtl(JDTORecord inRec) throws DAOException, JDTOException {
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
	} // end of insYdCarftmvmtl
	
	
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량이송재료 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_CAR_SCH_ID,STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCarftmvmtl(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdCarftmvmtl";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;

		//recordSet create
		JDTORecordSet outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
		
		//변환용 레코드
		JDTORecord recInPara = null;
		JDTORecord recOutPara = null;
		
		//수정
		//필드명 변환 (필드명 -> V_필드명)
		recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
		
		//update data select
		if( intGp == 0 ) {
			intRtnVal = this.getYdCarftmvmtl(inRec, outRecSet, 0);
	
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
		}else{
			recOutPara =  JDTORecordFactory.getInstance().create();
			recOutPara.setRecord(recInPara);
		}
		
		//query id setting
		if( intGp == 0 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
		else if( intGp == 1 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
		else if( intGp == 2 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
		else if( intGp == 3 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4); //상차초기화(차량스케쥴 재료)
		else if( intGp == 4 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd5); //상차초기화(차량스케쥴)
		else if( intGp == 5 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6); //야드맵정보 삭제(차량 초기화 시)
		else if( intGp == 6 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7); //야드맵정보 수정(차상위치수정 시)
		else if( intGp == 7 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd8); //야드맵정보 수정(차상위치수정 시)
		else if( intGp == 8 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd9); //야드맵정보 수정(차상위치수정 시)
		else if( intGp == 9 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd10); //제품출하이송실적취소 
		else if( intGp == 10 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd11); //차량스케줄 재료 삭제
		else if( intGp == 11 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd12); //검수 재료 삭제
		else if( intGp == 12 )
		//query execute
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd13); //저장위치변경표시(통합야드)
		else if( intGp == 13 )
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd14); //저장품 CAR_LOTID 클리어		
		intRtnVal = dbAssDao.trtProcess(recOutPara);
		
		//execution error return
		if (intRtnVal <= 0) intRtnVal = -3;
		return intRtnVal;
	} // end of updYdCarftmvmtl
		
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량이송재료 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		szFieldName = "V_YD_CAR_SCH_ID";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_STL_NO";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
		if (!blnErr) return blnErr;
			
		szFieldName = "V_REGISTER";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_REG_DDTT";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
		if (!blnErr) return blnErr;

//		szFieldName = "V_MODIFIER";
//		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//		if (!blnErr) return blnErr;
//
//		szFieldName = "V_MOD_DDTT";
//		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//		if (!blnErr) return blnErr;
//		
//		szFieldName = "V_DEL_YN";
//		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//		if (!blnErr) return blnErr;
		
		szFieldName = "V_YD_CAR_UPP_LOC_CD";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_YD_STK_BED_NO";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_YD_STK_LYR_NO";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_HCR_GP";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_STL_PROG_CD";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_YD_MTL_ITEM";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		if (!blnErr) return blnErr;
		
		szFieldName = "V_YD_ROUTE_GP";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		
		
		return blnErr;
	} //end of chkParameter
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드차량이송재료 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		szFieldName = "V_YD_CAR_SCH_ID";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_STL_NO";
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
		
		szFieldName = "V_YD_CAR_UPP_LOC_CD";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_STK_BED_NO";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_STK_LYR_NO";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_HCR_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_STL_PROG_CD";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_MTL_ITEM";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		
		szFieldName = "V_YD_ROUTE_GP";
		ydDaoUtils.mappingData(inRec, outRec, szFieldName);

	} // end of dataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






