package com.inisteel.cim.yd.common.dao.ydStkBedDao;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;

/**
 *      [A] 클래스명 : 야드적치BED DAO
 * 
*/

public class YdStkBedDao {
	
	// Dao NameszQueryIdGet1
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
	private CCommUtils	   commUtils   = new CCommUtils();		 
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedCol";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYdStklyrBed";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYdStklyrCol";
	//2009.02.17 권오창추가
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYdStkLyrGpBedWhioStat";
	//2009.02.17 권오창추가
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYdStkColGpBed";
	
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYdStkColGpBed2";
	// 20080302 권오창 
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkLyrYdStkBedGpBedNStl";
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkBedYdStkLyrYdStockGpBedStl";
	// 20080309 이현성
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkBedFlexYdGp";
	// 20080315 이현성
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandTrnEqpCd";
	// 20080323 권오창 
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedColStkColGpBedNoCnt";
	
	// 20080325 이현성 
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedByYdStrGtrCd";
	
	// 20090406 김진욱
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedColGpBedNoBedNoR";
	
	// 20090427 김창일
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdStrPosLackCntStats";
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdStrPosLackWgtStats";

	// 	20090506  이현성
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getReadySchGp";
	// 	20090514  김창일
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilYdColStkUsageRtoExpStk";	
	// 	20090520  김창일
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilYdInPlan";
	// 	20090609  심명순
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilYdInPlan1";
	
	// 20090706 이현성
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getMaxLyrmtlInfo";
	// 20090820 김진욱	
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedByCarUseGpandCarNoCardNo_PIDEV";
	
	// 20090903 이현성	[]
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getSlabTotYdStkPosList";
	
	// 20091015 석창화	[BED NO만 읽어오기]
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getSlabTotBedNoByColGp";
	//베드번호 Asc
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedByColBedLike";
	//베드분석정보 쿼리 - 임춘수 : 2009.12.03
	private String szQueryIdGet25 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkBedAnalysis1";
	//길이구분/폭구분이 동일한 적치가능한 공베드 조회 : 적치열 ASC - 임춘수 : 2009.12.03
	private String szQueryIdGet26 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAsc";
	//길이구분/폭구분이 동일한 적치가능한 공베드 조회 : 적치열 ASC - 임춘수 : 2009.12.03
	private String szQueryIdGet27 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDesc";
	//길이구분/폭구분이 동일한 적치가능한 공베드 조회 : 해당열보다 큰/작은 열, 적치열 ASC - 임춘수 : 2009.12.07
	private String szQueryIdGet28 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscForAidWrk";
	//길이구분/폭구분이 동일한 적치가능한 공베드 조회 : 해당열보다 큰/작은 열, 적치열 ASC - 임춘수 : 2009.12.07
	private String szQueryIdGet29 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescForAidWrk";
	//이적및 설별작업의 FROM BED와 TO_LOC_GUIDE BED의  길이구분/폭구분을 조회 - 석창화 : 2009.12.10
	private String szQueryIdGet30 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getFrBedToBedWLGp";
	
	// 20091215 이현성	[저장위치별 재고 LIST]
	private String szQueryIdGet31 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdStkPosList_PIDEV";
	//준비작업스케줄편성 화면의 페이징쿼리 - 임춘수 2009.12.29
	private String szQueryIdGet32 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getReadySchPage";
	
	// 2010.01.28 이현성	[저장위치별 재고 코일제품용]
	private String szQueryIdGet33 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilYdStkPosList_PIDEV";
	//준비작업스케줄편성 화면의 페이징쿼리 : 이송대기상태인 대상재 조회(이송지시테이블과 조인) - 임춘수 2010.02.05
	private String szQueryIdGet34 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getReadySchPageForFrtoMove";
	//준비작업스케줄편성 화면의 페이징쿼리 : 이송대기상태인 대상재 조회(이송지시테이블과 조인) - 임춘수 2010.02.05
	private String szQueryIdGet35 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getReadySchPageForFrtoMoveAll";
	
	//이적및 선별작업의 재료와 TO_LOC_GUIDE BED의  길이구분/폭구분을 조회 - 석창화 : 2010.02.24
	private String szQueryIdGet36 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getFrStockToBedWLGp";
	//해당적치열의 MAX배드를 조회 - 석창화 : 2010.03.08
	private String szQueryIdGet37 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getMaxBedNoByColGp";
	
	
	private String szQueryIdGet38 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkBedFlexYdGpTot";
	
	private String szQueryIdGet39 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getcSlabYdStkBedFlexYdGpTot";
	
	private String szQueryIdGet40 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getaPlateYdStkBedFlexYdGpTot";
	
	private String szQueryIdGet41 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdDmFrList";
	private String szQueryIdGet42 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdDmFrStlList";
	private String szQueryIdGet43 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdDmIfStlList";
	private String szQueryIdGet44 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdCnIfStlList";
	private String szQueryIdGet45 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdDmFrList_01";
	
	private String szQueryIdGet46 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscWithAlFrTo";
	private String szQueryIdGet47 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescWithAlFrTo";
	
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedDan";
	// 위치검색SPAN관리 bed조회 - 이종헌: 2010.04.02
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkBedByYdStrGtrCdCoil";

	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilMaxBedNoByColGp";
	private String szQueryIdGet304 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedLyr";
	
	private String szQueryIdGet305 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdCoilStkBedAnalysis";

	private String szQueryIdGet306 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNew";
	private String szQueryIdGet307 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescNew";
	
	private String szQueryIdGet308 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getSlabTotYdStkPosListS_PIDEV";
	
	private String szQueryIdGet309 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdStrPosLackCntStatsNew";
	private String szQueryIdGet310 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getPlateYdStrPosLackWgtStatsNew";
	private String szQueryIdGet311 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedAll";
	private String szQueryIdGet312 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedTempStk";
	private String szQueryIdGet313 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBedAll";
	private String szQueryIdGet314 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedYardWrap";
	private String szQueryIdGet315 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getCoilYdTcarWbookChk";
	
	private String szQueryIdGet316 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getWaitLocArrBackupList_PIDEV";
	
	private String szQueryIdGet317 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.slabTotYdToMoveList";
	
	private String szQueryIdGet318 = "ym.steelinfo.steelinforecv.dao.YdStockDAO.slabTotYdToMoveMgt";
	
	private String szQueryIdGet319 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrOverRunChk";
	
	private String szQueryIdGet320 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColNew";
	
	private String szQueryIdGet321 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getSlabShipTargetList_PIDEV";
	
	private String szQueryIdGet330 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkBedDao.getSmsUserShipDate";
	
	private String szQueryIdGet331 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColAscNewWithAlFr";
	
	private String szQueryIdGet332 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getEmptyBedWithSameLWGpColDescNewWithAlFr";
	
	private String szQueryIdGet333 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSameSizeGroup";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.insYdStkbed";
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbed";
	//update query id
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdStkColGp";
	// 김종건(2009. 08. 05) BED X 좌표 일괄 수정 
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdStkXaxis";
	// 김종건(2009. 08. 10) BED Y 좌표 01 BED 수정 
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdStkYaxisFst";
	// 김종건(2009. 08. 25) 야드적치열 구분에 따른 폭 및 길이 구분 SET 
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdWLSet";
	// 김종건(2009. 08. 25) 야드적치열 구분에 따른 야드저장집합코드 SET 
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdStrGtrCdSet";
	
	// 이현성(2010. 02. 04) 야드적치열 구분에 따른 외경군정보 UPDATE 
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdCoilOutdiaGrpGp";
	
	// 이현성(2010. 02. 04) 야드적치열 구분에 따른 폭정보 UPDATE 
	private String szQueryIdUpd8 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdStkBedWGp";

	// 이현성(2010. 03. 08) 야드적치열 구분에 따른 저장집합코드 
	private String szQueryIdUpd9 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdStrGtrCd";
	
	//야드 베드입출고 상태 및 선별상태를 UPDATE
	private String szQueryIdUpd10 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedStsCd";
	private String szQueryIdUpd11 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedStsCd_02";
	
	//송정현  야드적치열 구분에 따른 활성화
	private String szQueryIdUpd300 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdStrActStat";
	//송정현  x좌표값
	private String szQueryIdUpd301 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedYdStrX";
//선별
	private String szQueryIdUpd400 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updPlateYdSelList";
	private String szQueryIdUpd401 = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.updYdStkbedAll";

	

/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_STK_COL_GP,YD_STK_BED_NO
	 *                                      1:YD_STK_COL_GP
	 *                                      2:YD_STK_COL_GP,YD_STK_BED_NO[JOIN STKLYR]
	 *                                      3:YD_STK_COL_GP[JOIN STKLYR]
	 *                                      4:0:YD_STK_COL_GP,YD_STK_BED_NO[JOIN STKLYR]
	 *                                      5:0:YD_STK_COL_GP,YD_STK_BED_NO[JOIN STKCOL]
	 *                                      6:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                      7:YD_STK_COL_GP, YD_STK_BED_NO, STL_NO
	 *                                      8:YD_GP
	 *                                      9:YD_CAR_USE_GP , TRN_EQP_CD
	 *                                      10:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                      11:YD_STR_GTR_CD ,YD_ROUTE_GP ,YD_LOC_SRCH_RNG_REG_SNO 
	 *                                      12:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_BED_NO_R
	 *                                      13:
	 *                                      14:
	 *                                      15: YD_GP,YD_BAY_GP,YD_EQP_GP,YD_AIM_RT_GP,....
	 *                                      
	 *                                      
	 *                                      
	 *                                      21: V_YD_BAY_GP,V_YD_EQP_GP,V_PAGE_CNT,V_ROW_CNT
	 *                                      
	 *                                      23:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                      24:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                      25:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP
	 *                                      26:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP
	 *                                      27:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO
	 *                                      28:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO
	 *                                      
	 *                                      30:V_YD_GP, V_YD_BAY_GP,V_YD_EQP_GP,V_PAGE_CNT,V_ROW_CNT
	 *                                      31:V_YD_AIM_RT_GP, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
	 *                                      
	 *                                      33:V_YD_AIM_RT_GP, V_SPOS_WLOC_CD, V_ARR_WLOC_CD, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
	 *                                      34:V_YD_AIM_RT_GP, V_SPOS_WLOC_CD, V_ARR_WLOC_CD, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
	 *                                      )                     
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdStkbed(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStkbed";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStkbed(recPara, intGp);
			
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
			else if (intGp == 309)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet309);
			else if (intGp == 310)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet310);
			else if (intGp == 311)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet311);
			else if (intGp == 312)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet312);
			else if (intGp == 313)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet313);
			else if (intGp == 314)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet314);
			else if (intGp == 315)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet315);
			else if (intGp == 316)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet316);
			else if (intGp == 317)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet317);
			else if (intGp == 318)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet318);
			else if (intGp == 319)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet319);
			else if (intGp == 320)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet320);
			else if (intGp == 321)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet321);
			else if (intGp == 330)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet330);
			else if (intGp == 331)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet331);
			else if (intGp == 332)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet332);			
			else if (intGp == 333)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet333);			
			
			String mthdNm = "YdStkBedDao.getYdStkbed";
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");				
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", mthdNm, "YD0001", recPara.getField("JSPEED_QUERY_ID").toString(), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", toQuery_ID);				
			
			
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
	} //end of getYdStkbed
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치bed SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO
	 *                              1:YD_STK_COL_GP
	 *                              2:YD_STK_COL_GP,YD_STK_BED_NO[JOIN STKLYR]
	 *                              3:YD_STK_COL_GP[JOIN STKLYR]
	 *                              4:0:YD_STK_COL_GP,YD_STK_BED_NO[JOIN STKLYR]
	 *                              5:0:YD_STK_COL_GP,YD_STK_BED_NO[JOIN STKCOL]
 	 *                              6:YD_STK_COL_GP, YD_STK_BED_NO
	 *                              7:YD_STK_COL_GP, YD_STK_BED_NO, STL_NO
	 *                              8:YD_GP
	 *                              9:YD_CAR_USE_GP , TRN_EQP_CD     
	 *                             10:YD_STK_COL_GP, YD_STK_BED_NO
	 *                             11:YD_STR_GTR_CD ,YD_ROUTE_GP ,YD_LOC_SRCH_RNG_REG_SNO                     
	 *                             12:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_BED_NO_R
	 *                             13:
	 *                             14:
	 *                             15: YD_GP,YD_BAY_GP,YD_EQP_GP,YD_AIM_RT_GP,.....
	 *                             
	 *                             
	 *                             
	 *                             
	 *                             21: V_YD_BAY_GP,V_YD_EQP_GP,V_PAGE_CNT,V_ROW_CNT
	 *                             
	 *                             23:YD_STK_COL_GP, YD_STK_BED_NO
	 *                             24:YD_STK_COL_GP, YD_STK_BED_NO
	 *                             25:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP
	 *                             26:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP
	 *                             27:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO
	 *                             28:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO
	 *                             
	 *                             30:V_YD_GP, V_YD_BAY_GP,V_YD_EQP_GP,V_PAGE_CNT,V_ROW_CNT
	 *                             31:V_YD_AIM_RT_GP, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
	 *                             32:
	 *                             33:V_YD_AIM_RT_GP, V_SPOS_WLOC_CD, V_ARR_WLOC_CD, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
	 *                             34:V_YD_AIM_RT_GP, V_SPOS_WLOC_CD, V_ARR_WLOC_CD, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
	 *                             )                         
	 * @return boolean          true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdStkbed(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			
			//PIDEV
			//PIDEV_S :병행가동용:PI_YD
			String sPI_YD     = commUtils.nvl(inRec.getFieldString("V_PI_YD"), "*");				
//			String sApplyYnPI = ydPICommDAO.ApplyYnPI("", "YdStkBedDao.chkPara_getYdStkbed", "APPPI0", sPI_YD, "*");			
			
			if (intGp == 0 || intGp == 2 || intGp == 4 || intGp == 5 || intGp == 10 || intGp == 22) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			} else if (intGp == 1 || intGp == 3) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			} else if (intGp == 6) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if (intGp == 7 || intGp == 319) {
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}  else if (intGp == 8) {				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			}	 else if (intGp == 9) {				
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
			} else if (intGp == 11) {			
				szFieldName = "V_YD_STR_GTR_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
							
				szFieldName = "V_YD_ROUTE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_LOC_SRCH_RNG_REG_SNO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'L', 0, 0);
				
			} else if (intGp == 12) {			
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO_R";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if (intGp == 13 || intGp == 14) {
				szFieldName = "V_YD_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP4";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP5";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			} else if (intGp == 15) {
				
				szFieldName = "V_YD_AIM_RT_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			
				szFieldName = "V_YD_STK_COL_GP3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;				
				
				szFieldName = "V_YD_STK_COL_GP4";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP4";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP5";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP6";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP5";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
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
				
			} else if (intGp == 16) {
				szFieldName = "V_YD_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_BAY_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_YD_GP2";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
//				if (!blnErr) return blnErr;
//				szFieldName = "V_YD_BAY_GP2";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
//				if (!blnErr) return blnErr;
//				
//				szFieldName = "V_YD_GP3";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
//				if (!blnErr) return blnErr;
//				szFieldName = "V_YD_BAY_GP3";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			} else if (intGp == 17) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD4";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				
				szFieldName = "V_BRANCH_CD5";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
			}else if (intGp == 18) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_BRANCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;				
			} else if (intGp == 19) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;				
			} else if (intGp == 20) {
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_CAR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				//PIDEV	
//				if("N".equals(sApplyYnPI)) {
//					szFieldName = "V_CARD_NO";
//					blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
//					if (!blnErr) return blnErr;	
//				}
				
			}  else if (intGp == 21) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_BED_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 23) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			} else if (intGp == 24) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if ( intGp == 25 || intGp == 26 ) {
				szFieldName = "V_SPAN_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_BED_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_L_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_W_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
			} else if ( intGp == 27 || intGp == 28 ) {
				//28:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_STK_BED_L_GP, YD_STK_BED_W_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO
				szFieldName = "V_SPAN_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_BED_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_L_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_W_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_COL_GT_LT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
			} else if (intGp == 29) {
				szFieldName = "V_YD_STK_COL_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_STK_COL_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			}  else if (intGp == 30) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_BED_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			}  else if (intGp == 31) {
				//31:V_YD_AIM_RT_GP, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_MAIN_WRK_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
			}   else if (intGp == 32) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			}  else if (intGp == 33) {
				//33:V_YD_AIM_RT_GP, V_SPOS_WLOC_CD, V_ARR_WLOC_CD, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_SPOS_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_ARR_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_MAIN_WRK_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 34) {
				//33:V_YD_AIM_RT_GP, V_SPOS_WLOC_CD, V_ARR_WLOC_CD, V_YD_STK_COL_GP, V_MAIN_WRK_SEARCH_GP, V_PAGE_NO, V_ROW_CNT
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_AIM_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SPOS_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_ARR_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;	
				
				szFieldName = "V_MAIN_WRK_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 35) {
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
			} else if (intGp == 36) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 300) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);

			}
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStkbed
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치bed INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdStkbed(JDTORecord inRec) throws DAOException, JDTOException {
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
	} // end of insYdStkbed
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치bed parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_STK_COL_GP";   			//야드적치열구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_NO";			// 야드적치Bed번호
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STR_GTR_CD";			// 야드저장집합코드
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REGISTER";					// 등록자
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";					// 등록일시
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;	
			
//			szFieldName = "V_MODIFIER";					// 수정자
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_MOD_DDTT";					// 수정일
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if (!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";					// 삭제유무
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_TP";			// 야드적치BedType
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_L_GP";			// 야드적치Bed길이구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_W_GP";			// 야드적치Bed폭구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_DIR_GP";		// 야드적치Bed방향구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_BED_ACT_STAT";		// 야드적치Bed활성상태
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_BED_WHIO_STAT";		// 야드적치Bed입출고상태
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_USG_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_BED_XAXIS";			// 야드적치BedX축
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_BED_YAXIS";			// 야드적치BedY축
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_BED_ZAXIS";			// 야드적치BedZ축
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_LYR_MAX";		// 야드적치Bed단Max
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_WT_MAX";		// 야드적치Bed중량Max
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_H_MAX";			// 야드적치Bed높이Max
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_L_MAX";	// 야드적치Bed길이Max
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_W_MAX";	// 야드적치Bed폭Max
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'D', 4, 1);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_XAXIS_TOL";    // 야드적치BedX축허용오차
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_BED_YAXIS_TOL";    // 야드적치BedY축허용오차
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;

			szFieldName = "V_YD_L_S_GRP_GP";    // 야드길이소그룹구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;

			szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";    // 야드코일외경군구분
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

		return blnErr;
	} //end of chkPara_YdStkbed
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치bed UPDATE
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStkbed(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStkbed";
		String szMsg = null;
		String szYD_STK_COL_GP = "";
		String szYD_STK_BED_W_GP = "";
		String szYD_STK_BED_L_GP = "";
		String szYD_STR_GTR_CD = "";
		
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
			intRtnVal = this.getYdStkbed(inRec, outRecSet, 0);
			
			
			
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
			
			
			
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recOutPara,"V_YD_STK_COL_GP");
			szYD_STK_BED_W_GP = ydDaoUtils.paraRecChkNull(recOutPara,"V_YD_STK_BED_W_GP");
			szYD_STK_BED_L_GP = ydDaoUtils.paraRecChkNull(recOutPara,"V_YD_STK_BED_L_GP");
			szYD_STR_GTR_CD = ydDaoUtils.paraRecChkNull(recOutPara,"V_YD_STR_GTR_CD");
			
			//--2013.02.14 수정 (3기)
			if( (YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_STK_COL_GP.substring(0, 1)) || 
				 YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_STK_COL_GP.substring(0, 1)) ) && "".equals(szYD_STR_GTR_CD)) {
				if (!"".equals(szYD_STK_BED_W_GP) && !"".equals(szYD_STK_BED_L_GP)) {
					if(YdConstant.SPAN_ORDER_NEW_04.equals(szYD_STK_COL_GP.substring(2,4)) || 
					   YdConstant.SPAN_ORDER_NEW_05.equals(szYD_STK_COL_GP.substring(2,4)) || 
					   YdConstant.SPAN_ORDER_NEW_06.equals(szYD_STK_COL_GP.substring(2,4)) ){
				         szYD_STR_GTR_CD = szYD_STK_COL_GP.substring(0,2) + szYD_STK_BED_W_GP + szYD_STK_BED_L_GP + "12";
					} else {
						 szYD_STR_GTR_CD = szYD_STK_COL_GP.substring(0,2) + szYD_STK_BED_W_GP + szYD_STK_BED_L_GP + "34";
					}
				} else {
					szYD_STR_GTR_CD = "      ";
				}
				
				recOutPara.setField("V_YD_STR_GTR_CD", szYD_STR_GTR_CD);
			}
					
			
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
	} // end of updYdStkbed
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치bed UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_STK_COL_GP";   	//야드적치열구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_NO";	// 야드적치Bed번호
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STR_GTR_CD";	// 야드저장집합코드
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REGISTER";			// 등록자
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_REG_DDTT";			// 등록일시
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MODIFIER";			// 수정자
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_MOD_DDTT";			// 수정일
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_DEL_YN";			// 삭제유무
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_TP";	// 야드적치BedType
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_L_GP";	// 야드적치Bed길이구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_W_GP";	// 야드적치Bed폭구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_DIR_GP"; // 야드적치Bed방향구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_ACT_STAT"; // 야드적치Bed활성상태
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_WHIO_STAT"; // 야드적치Bed입출고상태
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_USG_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_XAXIS";		// 야드적치BedX축
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_YAXIS";		// 야드적치BedY축
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_ZAXIS";		// 야드적치BedZ축
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_LYR_MAX";	// 야드적치Bed단Max
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_WT_MAX";	// 야드적치Bed중량Max
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_H_MAX";		// 야드적치Bed높이Max
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_L_MAX";		// 야드적치Bed길이Max
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_W_MAX";		// 야드적치Bed폭Max
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_XAXIS_TOL";    // 야드적치BedX축허용오차
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_BED_YAXIS_TOL";    // 야드적치BedY축허용오차
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_L_S_GRP_GP";    // 야드길이소그룹구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";    // 야드코일외경군구분
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	} // end of YdStkbed_DataMapping
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치bed UPDATE
	 * 
	 * @param JDTORecord inRec parameter record
	 *         int       intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO)
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStkbedYdStkColGp(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStkbedYdStkColGp";
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
			
//			recOutPara = JDTORecordFactory.getInstance().create();
			
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//query id setting
			if (intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			if (intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			if (intGp == 2)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			if (intGp == 3)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd5);
			if (intGp == 4)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6);
			if (intGp == 5) // YD_STK_COL_GP, YD_COIL_OUTDIA_GRP_GP,MODIFIER
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7);
			if (intGp == 6) // YD_STK_COL_GP, YD_STK_BED_W_GP,MODIFIER
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd8);
			if (intGp == 7) // YD_STK_COL_GP, YD_STK_BED_W_GP,MODIFIER
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd9);
			if (intGp == 300) // YD_STK_COL_GP, YD_STK_BED_W_GP,MODIFIER
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd300);
			if (intGp == 301) // YD_STK_COL_GP, YD_STK_BED_W_GP,MODIFIER
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd301);
			if (intGp == 400) 
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd400);
			if (intGp == 401) 
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd401);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);
			
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}

		return intRtnVal;
	} // end of updYdStkbed
	
	

	/**
	 *      [A] 오퍼레이션명 :베드에 입출고상태 및 선별상태를 업데이트한다.
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStkBedStat_01(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "updYdStkBedStat_01";
		String szMsg = null;
		int intRtnVal = 0;
		try {
			
			JDTORecord recOutPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
				 		
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd10);
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return 
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkBedStat_01
	
	/**
	 *      [A] 오퍼레이션명 :베드에 입출고상태 및 선별상태를 업데이트한다.
	 * 
	 * @param  JDTORecord      inRec parameter record
	 *         int             intGp 구분(0:YD_STK_COL_GP))
	 * @return int             execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStkBedStat_02(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "updYdStkBedStat_02";
		String szMsg = null;
		int intRtnVal = 0;
		try {
			
			JDTORecord recOutPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recOutPara = ydDaoUtils.conversionFieldname(inRec, 0);
				 		
			//query id setting
			recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd11);
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return 
			if (intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStkBedStat_02
/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






