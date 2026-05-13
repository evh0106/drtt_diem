package com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao;

import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;

/**
 *      [A] 클래스명 : 야드크레인작업재료 DAO
 * 
*/

public class YdCrnWrkMtlDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtl";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlID";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlSTLNO";
	// 20090305 김진욱
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlYdStockSumCrnSchID";
	// 20090306 이현성
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydCrnWrkMtlDao.getCrnWrkMtlbyCrnSchId";
	// 20090311 김창일
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getCoilYdEventWorkMatRef_PAGE";
	// 20090315 이현성
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlBySchId";
	// 20090330 이현성
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlBySchIdAsc";
	// 
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlByCrnSchIDOrdStkLyrNo";
	// 20090701 김창일
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnSchMtlFrmBed";
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnSchMtlToBed";
	
	//20090915 이현성
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlSchIdDtl";
	
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.LocSrcRngDataSet02";
	
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.chkLocsrchbedCrnMtl";
	
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlID_Det_PIDEV";
	//재료로 크레인스케줄의 우선순위가 빠른 정보 조회 - 임춘수 2009.11.16
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlByStlNo";
	//크레인작업재료의 길이가 긴 순서로 정렬 - 임춘수 2009.11.19
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlStlLengthDesc";
	//같은 작업예약의 해당크레인스케줄보다 다음 크레인스케줄중에서 해당 재료가 크레인작업재료로 존재하는 지 조회 - 2009.12.16
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlGreaterThanCrnSch";
	//해당작업예약의 작업예약재료가 다른작업예약의 크레인스케줄작업재료로 등록되어 있는 지를 조회 - 임춘수 2010.01.04
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlNWrkbookmtlByWbookId";
	//해당작업예약의 작업예약재료의 상단에 존재하는 재료가 동일한 크레인스케줄코드가 아닌 크레인스케줄의 작업재료로 등록되어 있는 지 조회
	//임춘수 - 2010.01.04
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlAtUpperLayerByWbookId";
	//크레인작업재료의 폭이 긴 순서로 정렬 - 임춘수 2010.02.03
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlStlWidthDesc";

	//선택된 설비의 TRK 정보 조회(크레인스케줄편성여부 포함) - 이현성 2010.02.22
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getTrkSelectEqpInfo";
	
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getpPlateYdRemark";
	
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getpPlateYdBookOutRule";

	//선택된 설비의 TRK 정보 조회
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getCoilYdHrTracking";
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getCoilYdHrTrackingBackUp";
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilYdLineWrPp";
	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilYdLineWrCodePp";
	
//	크레인작업관리 - 10.04.09
	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnWrkMtlRefCoil";
	private String szQueryIdGet305 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilGdsYdLineWrPp";
	// 
	private String szQueryIdGet306 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlByCrnSchIDOrdStkLyrNoUp";
	private String szQueryIdGet307 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.LineInSchCheck";
	private String szQueryIdGet308 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilYdLineWrPpNew";
	private String szQueryIdGet400 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getYdCrnwrkmtlByCrnSchIDOrdStkLyrNoCoil";
	private String szQueryIdGet401 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilGdsYdLineWrPp2";
	
//C증설
	private String szQueryIdGet402 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getCoilYdHrTrackingNew";
	private String szQueryIdGet403 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.getcoilGdsYdLineWrPpNew";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.insYdCrnwrkmtl";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.updYdCrnwrkmtl";
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.updYdCrnwrkmtlYdCrnSchId";

	private String szQueryIdDel1 = "com.inisteel.cim.yd.dao.ydcrnwrkmtldao.YdCrnwrkmtlDao.delYdCrnSchInfo";
	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_CRN_SCH_ID,STL_NO,
	 *                                      1:YD_CRN_SCH_ID,
	 *                                      2:STL_NO,
	 *                                      3:YD_CRN_SCH_ID
	 *                                      4:YD_CRN_SCH_ID
	 *                                      5:V_YD_WRK_HDS_DD, V_YD_WRK_DUTY, V_YD_SCH_CD, V_ORD_YEOJAE_GP, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
	 *                                      6:YD_CRN_SCH_ID
	 *                                      7:YD_CRN_SCH_ID
	 *                                      8:YD_CRN_SCH_ID 
	 *                                      
	 *                                      11:YD_CRN_SCH_ID 
	 *                                      15:STL_NO
	 *                                      16:YD_CRN_SCH_ID
	 *                                      17:STL_NO, YD_CRN_SCH_ID, YD_WBOOK_ID
	 *                                      18:YD_WBOOK_ID
	 *                                      19:YD_WBOOK_ID, YD_SCH_CD
	 *                                      20:YD_CRN_SCH_ID
	 *                                      21:EQP_GP
	 *                                      )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdCrnwrkmtl(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdCrnwrkmtl";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			
			//parameter check
			blnChk_Field = this.chkPara_getYdCrnwrkmtl(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			/*
			 * 던질 모든 값을 확인한다. 
			 */
			/*
			String pLogMsg = "";
			Iterator iter = inRec.iterateName();
			while(iter.hasNext()) {
				String pName = (String)iter.next();
				pLogMsg += "pname : " +pName +" - value : "+recPara.getFieldString(pName) +"\n";
				
			}
			ydUtils.putLog(" :: DAO ::", szMethodName, pLogMsg, YdConstant.INFO);
			*/
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
			else if (intGp == 14)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet15);
			else if (intGp == 15)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet16);
			else if (intGp == 16)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet17);
			else if (intGp == 17)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet18);
			else if (intGp == 18)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet19);
			else if (intGp == 19)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet20);
			else if (intGp == 20)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet21);
			else if (intGp == 21)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet22);
			else if (intGp == 22)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet23);
			else if (intGp == 23)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet24);
			else if (intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if (intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			else if (intGp == 302)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet302);
			else if (intGp == 303)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet303);
			else if (intGp == 304)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet304);
			else if (intGp == 305)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet305);
			else if (intGp == 306)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet306);
			else if (intGp == 307)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet307);
			else if (intGp == 308)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet308);
			else if (intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
			else if (intGp == 401)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);
			else if (intGp == 402)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet402);
			else if (intGp == 403)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet403);
			
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
	} //end of getYdCrnwrkmtl
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_CRN_SCH_ID,STL_NO,
	 *                               1:YD_CRN_SCH_ID,
	 *                               2:STL_NO,
	 *                               3:YD_CRN_SCH_ID
	 *                               4:YD_CRN_SCH_ID
	 *                               5:
	 *                               6:YD_CRN_SCH_ID
	 *                               7:YD_CRN_SCH_ID
	 *                               8:YD_CRN_SCH_ID 
	 *                               
	 *                               11:YD_CRN_SCH_ID
	 *                               15:STL_NO
	 *                               16:YD_CRN_SCH_ID
	 *                               17:STL_NO, YD_CRN_SCH_ID, YD_WBOOK_ID
	 *                               18:YD_WBOOK_ID
	 *                               19:YD_WBOOK_ID, YD_SCH_CD
	 *                               20:YD_CRN_SCH_ID
	 *                               21:EQP_GP
	 *                               )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdCrnwrkmtl(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0 ) {
				
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			} else if (intGp == 1 || intGp == 3 || intGp == 4  || intGp == 6|| intGp == 7 || intGp == 8 || intGp ==11 
					   || intGp ==13  || intGp ==14 || intGp == 16 || intGp == 20 ) {
				
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
			} else if (intGp == 2) {

				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 5) {
				szFieldName = "V_YD_WRK_HDS_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName,  8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_DUTY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName,  1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName,  8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_YEOJAE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName,  1, 2, 'S', 0, 0);
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
			} else if (intGp == 9) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if (intGp == 10) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if (intGp == 12) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			
			} else if (intGp == 15) {
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 17) {
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if( intGp == 18 ) {
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if( intGp == 19 ) {
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName,  8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if( intGp == 21 ) {
				
				szFieldName = "V_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdCrnwrkmtl
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdCrnwrkmtl(JDTORecord inRec) throws DAOException, JDTOException {
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
	} // end of insYdCrnwrkmtl
	
	
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_CRN_SCH_ID,STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCrnwrkmtl(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdCrnwrkmtl";
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
			intRtnVal = this.getYdCrnwrkmtl(inRec, outRecSet, 0);
		
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
			if (intGp == 0){
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			}else if (intGp == 1){
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			}
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCrnwrkmtl
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 UPDATE_YD_SCH_CD[K]
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCrnwrkmtl_YD_CRN_SCH_ID(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter1(recPara);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdCrnwrkmtl_YD_CRN_SCH_ID
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_CRN_SCH_ID";
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
			
			szFieldName = "V_YD_AID_WRK_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LOT_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LOT_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
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
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TO_LOC_DCSN_MTD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 parameter Check
	 *      V_MODIFIER, V_DEL_YN, V_YD_WBOOK_ID
	 *      
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter1(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_MODIFIER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEL_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CRN_SCH_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
	

	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인작업재료 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_CRN_SCH_ID";
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
			
			szFieldName = "V_YD_AID_WRK_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LOT_TP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LOT_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_HCR_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_STL_PROG_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_MTL_ITEM";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_ROUTE_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TO_LOC_DCSN_MTD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of dataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 DELETE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int delYdCrnSch(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "delYdCrnSch";
		String szMsg = null;
		
		int intRtnVal = 0;
			
		try {
			//변환용 레코드
			JDTORecord recInPara = null;
			recInPara = JDTORecordFactory.getInstance().create();
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			//query id setting
			if (intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdDel1);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of delYdCrnSch
} // end of class






