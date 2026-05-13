package com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao;

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
 *      [A] 클래스명 : 야드작업예약재료 DAO
 * 
*/

public class YdWrkbookMtlDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtl";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlId";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSTLNO";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlSchCdRow";
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlRow";
	//20090306_이현성
	private String szQueryIdGet6  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookNONE";
	private String szQueryIdGet7  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookSCHCD";
	private String szQueryIdGet8  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyWBookId";
	private String szQueryIdGet9  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyMtlSCHCD";
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyWBookIdEqpId";
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbySchCd";
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdDesc";
	//20090308_이현성
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookId2";
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyCarUsrGpTrnEqpCd";
	//20090308_이현성
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdCnt";
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyCarLdWBookId";
	//20090407_이현성 차량상차 작업예약ID로 PAGE QUERY 
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdPage";
	// 20090408 김진욱
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWrkBookMtlWrkBookCrnSchYdSchCD";
	// 20090506 김진욱
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlIdMin";
	// 20090525 김진욱
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlByYdWrkIDOrdCollSeq";
	
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdPage_BCoil";
	
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdPage_BSlab";
	
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdPage_ASlab";
	
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyWBookIdPage_ACoil";
	// 20090701 김창일
	private String szQueryIdGet25 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getYdCrnSchMtlFrmBed";
	private String szQueryIdGet26 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getYdCrnSchMtlToBed";
	//차량작업관리 작업재료 조회
	private String szQueryIdGet27 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlStockByTrnEqpCd";
	private String szQueryIdGet271 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlStockByTrnEqpCdManual";
	private String szQueryIdGet272 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookManualYN";
	private String szQueryIdGet28 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlStockSumByWBookID";
	
	// 20090914 이현성
	private String szQueryIdGet29 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getYdWrkbookmtldWithDel";
	
	// 20090915 이현성
	private String szQueryIdGet30 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getYdWrkbookmtldWithDelAPlate";
	
	private String szQueryIdGet31 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.chkCrnSchEffectCondition02";
	
	private String szQueryIdGet32 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.CrnSchSort01";
	
	private String szQueryIdGet33 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.CrnSchIns01";
	
	private String szQueryIdGet34 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.chkAidStkBed01";
	
	private String szQueryIdGet35 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getMainWrkColl01";
	
	//2009.09.30 이현성 
	private String szQueryIdGet36 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getTCarSchWrkMtl";
	//2009.10.08 김진욱  작업예약재료 권상모음순서 재정렬을 위한쿼리 (작업예약ID로 작업예약재료 조회 적치열 asc, 베드번호 desc, 단번호 desc 순서)
	private String szQueryIdGet37 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlOrderByStkColStkBedStkLyr";
	//2009.10.08 김진욱  작업예약재료 권상모음순서 재정렬을 위한쿼리 (작업예약ID로 작업예약재료 조회 적치열 asc, 베드번호 asc, 단번호 desc순서)
	private String szQueryIdGet38 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlOrderByStkColStkBedStkLyrDscA";
	//통합야드 차량작업관리 작업재료 조회
	private String szQueryIdGet39 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlStockByTrnEqpCdForSlabTot";
	
	//대차 스케줄 작업예약 재료 조회 2009.11.16 (이현성)
	private String szQueryIdGet40  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlByTcarSchWBookID";
	
	//작업예약재료와 저장품 조인 : 작업예약ID로 조회 - 임춘수 : 2009.12.09
	private String szQueryIdGet41  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlStockByWBookID";
	
	//20090407_이현성 차량상차 작업예약ID로 PAGE QUERY (후판제품용)
	private String szQueryIdGet42 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getPlateGdsWorkBookMtlbyWBookIdPage_PIDEV";
	private String szQueryIdGet46 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getPlateGdsWorkBookMtlbyWBookIdPage_ForPlate_PIDEV";
	
	private String szQueryIdGet47 = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getYdWrkbookmtldForPlate_PIDEV";
	
	//작업재료중에서 해당 권상모음순서보다 작은 재료들의 정보 조회 - 임춘수 2010.03.05 
	private String szQueryIdGet43  = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlLessThanUpCollSeq";
	
	//작업재료중에서 해당 권상모음순서보다 작거나 같은 재료들의 정보 조회 - 임춘수 2010.03.05
	private String szQueryIdGet44  = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlLessEqThanUpCollSeq";
	
	//작업재료중 배드별 재료수 조회 - 석창화 2010.03.20
	private String szQueryIdGet45  = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.getYdWrkbookmtlBedCnt";

//	예정정보에서 가이트 위치 READ 처리 함 (송)
	private String szQueryIdGet300  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookbyMtlSCHCD_GUIDE";
//	열별이적 작업일 경우 대상재 1개만 check
	private String szQueryIdGet301  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookNONE1Row";
//	대차작업시 동일 공장구분 동으로 이동할 작업예약 카운트 
	private String szQueryIdGet306  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlbyCarLdWBookIdCoil";

	//대차작업중인 재료내역
	private String szQueryIdGet307  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlByTcarSchWBookID2";
	
	private String szQueryIdGet400  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlStockByTrnEqpCdCoil";
	private String szQueryIdGet401  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookNONECoil";
	private String szQueryIdGet402  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookSCHCDCoil";
	private String szQueryIdGet403  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookMtlStockByTrnEqpCdForCoil";
	private String szQueryIdGet404  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookSCHCDCoil1Row";
	private String szQueryIdGet405  = "com.inisteel.cim.yd.dao.ydWrkbookMtlDao.getWorkBookSCHCDCoil1Row";
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.insYdWrkbookmtl";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.updYdWrkbookmtl";
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.updYdWrkbookmtlId";
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.updYdWrkbookmtlDelete";
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydwrkbookmtldao.YdWrkbookmtlDao.updYdWrkbookmtlDelete2";
	
	
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_WBOOK_ID,STL_NO
	 *                                      1:YD_WBOOK_ID
	 *                                      2:STL_NO
	 *                                      3:YD_SCH_CD,ROWNUM
	 *                                      4:ROWNUM
	 *                                      5:NONE
	 *                                      6:YD_SCH_CD
	 *                                      7:YD_WBOOK_ID
	 *                                      8:YD_SCH_CD
	 *                                      9:YD_WBOOK_ID, YD_EQP_ID
	 *                                     10:YD_SCH_CD
	 *                                     11:YD_WBOOK_ID
	 *                                     12:YD_WBOOK_ID,YD_CARLD_WRK_BOOK_ID
	 *                                     13:YD_CAR_USE_GP	,TRN_EQP_CD
	 *                                     14:YD_WBOOK_ID
	 *                                     15:YD_CARLD_WRK_BOOK_ID	, YD_WBOOK_ID
	 *                                     16:YD_WBOOK_ID
	 *                                     17:YD_SCH_CD
	 *                                     18:YD_WBOOK_ID
	 *                                     19:YD_WBOOK_ID
	 *                                     26:YD_CAR_USE_GP, TRN_EQP_CD, CAR_NO, CARD_NO
	 *                                     
	 *                                     
	 *                                     28:YD_WBOOK_ID
	 *                                     29:YD_WBOOK_ID
	 *                                     
	 *                                     35:YD_WBOOK_ID
	 *                                     38:YD_CAR_USE_GP, TRN_EQP_CD, CAR_NO, CARD_NO
	 *                                     39:YD_WBOOK_ID
	 *                                     40:YD_WBOOK_ID
	 *                                     41:YD_WBOOK_ID
	 *                                     42:YD_WBOOK_ID, YD_UP_COLL_SEQ
	 *                                     43:YD_WBOOK_ID, YD_UP_COLL_SEQ
	 *                                     )
	 *                                     
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdWrkbookmtl(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdWrkbookmtl";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null; 
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdWrkbookmtl(recPara, intGp);
			
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
			else if (intGp == 24)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet25);
			else if (intGp == 25)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet26);
			else if (intGp == 26)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet27);
			else if (intGp == 271)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet271);
			else if (intGp == 272)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet272);
			else if (intGp == 27)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet28);
			else if (intGp == 28)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet29);
			else if (intGp == 29)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet30);			
			else if (intGp == 30)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet31);
			else if (intGp == 31)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet32);
			else if (intGp == 32)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet33);
			else if (intGp == 33)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet34);
			else if (intGp == 34)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet35);
			else if (intGp == 35)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet36);
			else if (intGp == 36)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet37);
			else if (intGp == 37)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet38);
			else if (intGp == 38)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet39);
			else if (intGp == 39)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet40);
			else if (intGp == 40)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet41);
			else if (intGp == 41)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet42);
			else if (intGp == 42)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet43);
			else if (intGp == 43)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet44);
			else if (intGp == 44)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet45);
			else if (intGp == 45)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet46);
			else if (intGp == 46)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet47);
			else if (intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if (intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			else if (intGp == 306)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet306);
			else if (intGp == 307)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet307);
			else if (intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
			else if (intGp == 401)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);
			else if (intGp == 402)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet402);
			else if (intGp == 403)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet403);
			else if (intGp == 404)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet404);
			else if (intGp == 405)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet405);
			
//PIDEV
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");	
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", commUtils.trim(recPara.getFieldString("JSPEED_QUERY_ID")), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", commUtils.trim(toQuery_ID));				
			
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
	} //end of getYdWrkbookmtl
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int intGp                구분(0:YD_WBOOK_ID,STL_NO
	 *                                      1:YD_WBOOK_ID
	 *                                      2:STL_NO
	 *                                      3:YD_SCH_CD,ROWNUM
	 *                                      4:ROWNUM
	 *                                      5:NONE
	 *                                      6:YD_SCH_CD
	 *                                      7:YD_WBOOK_ID
	 *                                      8:YD_SCH_CD
	 *                                      9:YD_WBOOK_ID, YD_EQP_ID
	 *                                     10:YD_SCH_CD
	 *                                     11:YD_WBOOK_ID
	 *                                     12:YD_WBOOK_ID,YD_CARLD_WRK_BOOK_ID
	 *                                     13:YD_CAR_USE_GP,	TRN_EQP_CD
	 *                                     14:YD_WBOOK_ID
	 *                                     15:YD_CARLD_WRK_BOOK_ID	, YD_WBOOK_ID
	 *                                     16:YD_WBOOK_ID
	 *                                     17:YD_SCH_CD
 	 *                                     18:YD_WBOOK_ID
	 *                                     19:YD_WBOOK_ID
	 *                                     
	 *                                     28:YD_WBOOK_ID
	 *                                     29:YD_WBOOK_ID
	 *                                     
	 *                                     
	 *                                     35:YD_WBOOK_ID
	 *                                     38:YD_CAR_USE_GP, TRN_EQP_CD, CAR_NO, CARD_NO
	 *                                     39:YD_WBOOK_ID
	 *                                     40:YD_WBOOK_ID
	 *                                     41:YD_WBOOK_ID
	 *                                     42:YD_WBOOK_ID, YD_UP_COLL_SEQ
	 *                                     43:YD_WBOOK_ID, YD_UP_COLL_SEQ
 	 *                                     )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdWrkbookmtl(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0) {
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 1 || intGp == 14 || intGp == 27 ||  intGp == 28  ||  intGp == 29 || intGp == 31 ||intGp ==34
					|| intGp == 35 || intGp == 36 || intGp == 37 ||   intGp == 40 || intGp == 44 ) {
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
			} else if (intGp == 2) {
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 3) {
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROWNUM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'L', 0, 0);				
			}  else if (intGp == 4) {
				
				szFieldName = "V_ROWNUM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'L', 0, 0);		
			//20090330 김진욱 수정
			}  else if (intGp == 5) {
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);		
				
			}  else if (intGp ==6 || intGp == 10 || intGp == 17 || intGp ==33 ) {	
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);	
				
			}  else if (intGp ==7 || intGp == 11 || intGp == 18 || intGp == 19) {	
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);		
				
			} else if (intGp ==8) {			
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);	
				
			} else if (intGp ==9 ) {				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);		
			} else if (intGp == 12) {				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if ( intGp == 13 ) {				
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
			} else if (intGp == 15) {				
				szFieldName = "V_YD_CARLD_WRK_BOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18,1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if (intGp == 16 ||intGp == 41  ) {
				
				szFieldName = "V_YD_WBOOK_ID";
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
			}  else if ( intGp == 26 || intGp == 38 ) {				
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CARD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 32) {
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				
			}else if (intGp == 42 || intGp == 43) {
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				
				szFieldName = "V_YD_UP_COLL_SEQ";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'L', 0, 0);
				if (!blnErr) return blnErr;
			}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdWrkbookmtl
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdWrkbookmtl(JDTORecord inRec) throws DAOException, JDTOException {
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
	} // end of insYdWrkbookmtl
	
	
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_WBOOK_ID,STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkbookmtl(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdWrkbookmtl";
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
			intRtnVal = this.getYdWrkbookmtl(inRec, outRecSet, intGp);
		
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
	} // end of updYdWrkbookmtl
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 UPDATE_YD_SCH_CD[K]
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkbookmtl_YD_SCH_CD(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter_YD_SCH_CD(recPara);
			
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

	} // end of updYdWrkbookmtl_YD_SCH_CD
	
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 UPDATE_YD_SCH_CD[K]
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkbookmtl1(JDTORecord inRec) throws DAOException, JDTOException {
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
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdWrkbookmtl1
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료삭제
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkbookmtlDelete(JDTORecord inRec) throws DAOException, JDTOException {
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
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdWrkbookmtlDelete
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료삭제
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdWrkbookmtlDelete2(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			//blnChk_Field = this.chkParameter1(recPara);
			
			//parameter error return
			//if (!blnChk_Field)
			//	return intRtnVal = -2;
			
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;

	} // end of updYdWrkbookmtlDelete2
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분자
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_WBOOK_ID";
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
			
			szFieldName = "V_YD_STK_COL_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_COLL_SEQ";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 parameter Check
	 *      V_MODIFIER, V_DEL_YN, V_YD_SCH_CD[K]
	 *      
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter_YD_SCH_CD(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_MODIFIER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEL_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
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
			
			szFieldName = "V_YD_WBOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약재료 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_WBOOK_ID";
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
			
			szFieldName = "V_YD_STK_COL_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_COLL_SEQ";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	} // end of dataMapping
	
	
	
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






