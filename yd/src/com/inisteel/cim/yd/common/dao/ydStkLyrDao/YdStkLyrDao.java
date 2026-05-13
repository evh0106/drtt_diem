package com.inisteel.cim.yd.common.dao.ydStkLyrDao;

import java.util.List;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
/**
 *      [A] 클래스명 : 야드적치단 DAO
 * 
*/

public class YdStkLyrDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	
	
	//------------------------------select query id--------------------------------------------//
	//열과 BED,적치단으로 조회
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyr";
	//열과 BED만으로 조회
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrBED";
	//열과 BED만으로 조회(PAGE 처리)
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrBED_PAGE";
	
	//재료번호로 조회( 이현성_20081213)
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO";
	
	// 이현성_20081218 - 적치단,저장품,작업예약재료 JOIN
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWBookStock";
	//열로 조회(BED번호 DESCENDING)
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCOL";
	
	// 이현성 20080108 - 저장품 JOIN
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrYdStock";
	//C열연소재정리Lot편성,C열연제품정리Lot편성
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCNT";	
	// 이현성 20080305  
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getSlabYdStkPos";
	// 이현성 20080306  
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdRTDetMatMonitor_PAGE";
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdDdArtclStkRef_PAGE_PIDEV";
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdOrdInfoStkRef_PAGE";
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdRetCrnReg_PAGE";
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdDongSpanLineRef_PAGE_PIDEV";
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdDongOrdStkRef_PAGE";
	                                  
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydStkLyrDao.getStkLyrbyWBookIdEtc";
	
	// 이현성 20080310
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydStkLyrDao.getStkLyrbyStkColGpEtc";
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydStkLyrDao.getStkLyrbyYdGpYdTCar";
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydStkLyrDao.getStkLyrbyYdTCar";
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydStkLyrDao.getYdCSlabStlnoStatFlex";
	
	// 김창일 20090305 - 코일야드 저장위치별 정보 조회
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkLocInfoList_PAGE"; //SPAN별 저장위치관리 조회 
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdColStkPosList_PAGE";  //열별 저장위치 관리 
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdBayInvList_PAGE";
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdConveyorMgt_PAGE";
	private String szQueryIdGet25 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkPosInfo_PIDEV";
	private String szQueryIdGet26 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getStlNoRegYn";
	// 김창일 20090317 - 후판제품야드 동별 BED 사용현황 조회
	private String szQueryIdGet27 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdBayBedUseStat";
	// 권오창 20090323 
	private String szQueryIdGet28 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrStkColGpBedNoMtlStat";
	// 김창일 20090324 - 베드 join 단 - 열구분과 베드번호로 조회 (descending) 
	private String szQueryIdGet29 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStkBedLyrDesc";
	// 이현성  20090330   
	private String szQueryIdGet30 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByColGpBedNo";
	
	// 김진욱 20090406
	private String szQueryIdGet31 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWrkBookIDColGpBedNoLyrNoIn";
	private String szQueryIdGet32 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrYdStockColGpBedNoLyrNoIN";
	
	private String szQueryIdGet33 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByStkColGpDesc";

	// 김창일 20090409 - 코일제품창고  일품별재고조회
	private String szQueryIdGet34 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdDdArtclStkRef_PIDEV";
	
	// 김종건  20090408 - 크레인 번호 Select
	private String szQueryIdGet36 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdCrNo";
	
	// 김창일  20090414 - 코일소재 컨베어정보 조회
	private String szQueryIdGet38 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdConveyorMgt_PAGE2";
	
	// 이현성 20090415 - 적치재료 조회 (FLEX 화면 - 슬라브형태)
	private String szQueryIdGet39 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStlnoStatFlex";
	
	//이현성 20090420 - 
	private String szQueryIdGet40 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStkBedLyrByColGpBedNoLyrNo";
	
	//김창일 20090421 - 
	private String szQueryIdGet41 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdGdsStockList";
	
	//이현성 20090427
	private String szQueryIdGet42 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdRetCrnRegSelect_PAGE_PIDEV";
	//김창일  20090511
	private String szQueryIdGet43 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdSpanStkPosList_PAGE";
	
	//이현성  20090513
	private String szQueryIdGet44 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilStateWorkGbn_Flex";

	//김창일  20090513
	private String szQueryIdGet45 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdBayOrdInv1";
	//김창일  20090513
	private String szQueryIdGet46 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdBayOrdInv2";
	
	//김창일  20090521
	private String szQueryIdGet47 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdInPlanDtl_PIDEV";
	
	//이현성  20090527 - 코일제품야드 Flex 스판별 적치열 조회
	private String szQueryIdGet48 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilGdsStateWorkGbn_Flex";
	//이현성  20090605 - 스카핑 관리화면 조회 
	private String szQueryIdGet49 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getScarfMgt_PAGE";
	//이현성  20090605 - PICK UP BED 조회 
	private String szQueryIdGet50 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPickUpBed";
	private String szQueryIdGet51 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPickUpBedDet";
	//김창일  200906211 - 열별저장위치 조회
	private String szQueryIdGet52 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdStkLocLyrGpInfoList";
	//김창일  200906211 - 코일저장위치 조회
	private String szQueryIdGet53 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdCoilStkPos";
	
	
	//김창일  200906213 - 정정보급 화면 조회 쿼리 ( 55 : 생산기한일순  , 56 : HEAT 번호 순)
	
	private String szQueryIdGet54 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getShearDueDate";
	private String szQueryIdGet55 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getShearHeatNo";
	
	//김종건 20090616 - 후판제품 저장위치 수정
	private String szQueryIdGet56 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdStkPosFix";
	//김창일  20090616 - 단정보에서 열별 베드 수 조회
	private String szQueryIdGet57 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdColBedCnt";
	
	//이현성  20090616 - PICK UP BED 조회 (A후판슬라브야드)
	private String szQueryIdGet58 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPickUpBedDet2";
	
	//김종건  20090619 - 저장위치별 정보조회 (후판제품)
	private String szQueryIdGet59 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdStkLocInfoList_PIDEV";
	
	//심명순  20090630 - 반납 크레인 등록화면 조회  (코일제품)
	private String szQueryIdGet60 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdGdsRetCrnReg_PAGE";
	//심명순  20090630 - 반납 크레인 등록화면 조회  (코일제품)
	private String szQueryIdGet61 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdGdsRetCrnRegSelect_PAGE";
	// 심명순  20090630 - 열별저장위치 조회  (코일제품)
	private String szQueryIdGet62 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdColStkPosList_PAGE";
	// 심명순  20090714 - 주문별재고 조회  (코일제품)
	private String szQueryIdGet63 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilGdsYdOrdInfoStkRef_PAGE";
	// 심명순  20090714 - 저장위치별 재고  조회  (코일제품)
	private String szQueryIdGet64 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilGdsYdStkLocInfoList_PAGE_PIDEV";
	// 심명순  20090715 - 열별저장위치 조회(코일제품)
	private String szQueryIdGet65 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdColStkPosLyrGpList_PIDEV";
	// 김창일  20090729 - 여재보유현황 
	private String szQueryIdGet66 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdRmnPossList";
	private String szQueryIdGet67 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdBayInlnDtlList";
	private String szQueryIdGet68 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdBayInlnList_PIDEV";
	// 김종건  20090804 - 열별 저장품 조회
	private String szQueryIdGet69 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdStkColStockList";
	
	// 김종건  20090820 - 열별 저장품 조회
	private String szQueryIdGet70 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrBEDAplate_PAGE";
	
	// 심명순  20090824 - 사유별이적조회 상단데이터조회
	private String szQueryIdGet71 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getBecauseMvUpLyr";
	
	//김진욱 20090831 - 적치단 적치중인 재료의 두께조회
	private String szQueryIdGet72 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getStkLyrMtlSumW";

	// 권오창 20090910 - 적치열구분, BED번호, 상태(적치중 : C . . .)
	private String szQueryIdGet73 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrColGpBedNoMtlStat";
	
	// 김종건 20090911 - 입고 예정 위치 등록 할 저장품 조회
	private String szQueryIdGet74 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrRcptStrLocStlNo";
	
	//CrnSchSort 이현성
	private String szQueryIdGet75 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.CrnSchSort01";
	
	private String szQueryIdGet76 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrByColGpBedNoLike";
	
	

	// 후판제품 반납크레인등록화면에서 반송조건 (77:현물, 78:정보, 79:전체)
	private String szQueryIdGet77 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdRetCrnRegSelect_PAGE_2";
	private String szQueryIdGet78 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdRetCrnRegSelect_PAGE_3";
	private String szQueryIdGet79 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdRetCrnRegSelect_PAGE_4";
	
	// 후판제품 위치별 저장품 조회(PDA)
	private String szQueryIdGet80 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdDongSpanLineRef_PDA";
	
	//적치단의 최상단 재료의 Piling코드로 조회 : 적치열 ASC - 임춘수 2009.12.02						-- 사용안함 : 윤재광 적치비율쿼리로 통합
	private String szQueryIdGet81 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCd";
	//적치단의 최상단 재료의 Piling코드로 조회 : 적치열 DESC - 임춘수 2009.12.02					-- 사용안함 : 윤재광 적치비율쿼리로 통합
	private String szQueryIdGet82 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdColDesc";
	//적치단의 최상단 재료의 Piling코드로 조회 : 해당열보다 큰/작은 열, 적치열 ASC - 임춘수 2009.12.07	-- 사용안함 : 윤재광 적치비율쿼리로 통합
	private String szQueryIdGet83 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdColAscForAidWrk";
	//적치단의 최상단 재료의 Piling코드로 조회 : 해당열보다 큰/작은 열, 적치열 DESC - 임춘수 2009.12.07	-- 사용안함 : 윤재광 적치비율쿼리로 통합
	private String szQueryIdGet84 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdColDescForAidWrk";
	//적치단의 재료의 Piling코드로 조회 : 적치비율 - 윤재광 2010.08.09
	private String szQueryIdGet106 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatio";
	//적치단의 재료의 혼적베드 조회 : 적재높이 ASC - 윤재광 2010.08.09
	private String szQueryIdGet107 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBed";
	//적치단의 재료의 혼적베드 조회 : 권역별 출하 - 윤재광 2010.08.09
	private String szQueryIdGet108 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBed_PIDEV";
	
	//Depiler 조회  - 이현성  2009.12.23
	private String szQueryIdGet85 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getDepilerBed";
	
	
	// 총매수 조회 - 이현성 2010.01.12 
	private String szQueryIdGet86 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getAPlateYdTotCount";
	
	// 총중량 조회 - 이현성 2010.01.12
	private String szQueryIdGet87 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getAPlateYdTotSumMgt";
	
	// 주문별 재고조회 - 재료정보 2010.02.01
	private String szQueryIdGet88 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoidGdsYdOrdInfoMtl_PIDEV";
	
	// 수주별,고객사별 재고조회 - 재료정보 2010.02.02
	private String szQueryIdGet89 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoidGdsYdOrdGpMtl_PIDEV";
	
	// C열연 정정지시실적 + 작업예약 + 작업예약 재료 + 적치단 : 정정대상재 추출 2010.03.03 이영근
	private String szQueryIdGet90 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrHRShearWK";
	private String szQueryIdGet91 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrHRShearWK01";
	private String szQueryIdGet92 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrHRShearWK02";
	
	//현재 적치단보다 상위단이 몇개가 존재하는 지를 조회 - 현재 적치단 포함 : 2010.03.04 - 임춘수
	private String szQueryIdGet93 ="com.inisteel.cim.yd.dao.ydStkLyrDao.getStkLyrCntOverCurLyr";
	//해당하는 적치단부터 아래단에 적치된 재료들의 정보 조회 - 현재 적치단 포함 2010.03.05 : 임춘수
	private String szQueryIdGet94 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrYdStockBelowLyr";
	
	//적치재료 (재료상태가 : 'C','U') 적치단 정보조회
	private String szQueryIdGet95 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStkBedLyrAsc";
	
	//코일제품 저장위치별 재고조회 (2010.03.09)
	private String szQueryIdGet96 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilGdsPos_PIDEV";
	
	//선별출하송신이고 운송지시번호가 존재하는 재료가 해당 베드에 존재하는 조회 - 임춘수 2010.03.10
	private String szQueryIdGet97 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSelCompTransOrd";
	//가장 최근에 적치단에 등록된 정보를 조회	- 임춘수 2010.03.11
	private String szQueryIdGet98 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrStlOderByModDate";
	// 실제 적치단의 최고 TOP 적치위치를 조회 - 석창화 2010.03.11
	private String szQueryIdGet99 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrRealTopLyr";
	// 적치단의 더미재 조회 - 석창화 2010.03.17
	private String szQueryIdGet100 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrDummy";
	// 적치단의 더미재 조회 - 석창화 2010.03.18
	private String szQueryIdGet101 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrRealMoveLyr";
	// 적치BED의 재료중 운송대기, 운송지시대기인 재료의 수 - 석창화 2010.03.23
	private String szQueryIdGet102 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getStlMoveCnt";
	
	private String szQueryIdGet103 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSchSTLNO";
	// 적치BED의 재료중 장입재인 재료의 수 
	private String szQueryIdGet104 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrLotSTLNO";
	// 적치BED의 재료중 해당재료의 선순위 장입재인 재료의 수 
	private String szQueryIdGet105 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrPriorLotSTLNO";
	
	// 정정야드 총중량/총매수  - 권휘원 2013.03.08
	private String szQueryIdGet109 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateShareYdTotCntMgt";
	
	
	//해당 단,열,베드에 재료가 존재하는지 조회 - 신지은 2017.03.27
	private String szQueryIdGet110 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCurStlNo";
	
	private String szQueryIdGet111 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioWithAlFrTo";
	private String szQueryIdGet112 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBed_PIDEVWithAlFrTo";
	private String szQueryIdGet113 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedWithAlFrTo";
	
	//2025-12-29 : 저장위치 수정시 예외 조건 체크
	private String szQueryIdGet114 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyryAllowCheckYdStkColGp";
	
//	!AT	
	private String szQueryIdGet300 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilGdsPos_n_PIDEV";
	private String szQueryIdGet301 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilData";
	private String szQueryIdGet302 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdHrTrackingDtl_PIDEV";
	private String szQueryIdGet303 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdHrTrackingBackUpDtl_PIDEV";
	private String szQueryIdGet304 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdToSearchList";

	private String szQueryIdGet393 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdHrTrackingBackUpNew";
	
//	다음공정별 재공현황_팝업	
	private String szQueryIdGet305 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdBayInlnList_Pp";
	
//	재료진도별 재공현황 - 2010.04.21
	private String szQueryIdGet306 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilMtlProgIdInlnStat";	
//	재료진도별 재공현황 - 2010.04.21
	private String szQueryIdGet307 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilGdsMtlProgIdInlnStat";	
	
	private String szQueryIdGet308 ="com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getCoilTotYdNextproctListDetail_PIDEV";	
	
	private String szQueryIdGet309 ="ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.getSpanStockList";
	
	
//	재료진도별 재공현황 - 2010.04.21
	private String szQueryIdGet400 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSchSCHID";	
	private String szQueryIdGet401 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWrkBookIDColGpBedNoLyrNoLineOff";	
	private String szQueryIdGet402 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM";
	private String szQueryIdGet403 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrXYZ";
	private String szQueryIdGet404 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMM_AUTO";
	
	private String szQueryIdGet500 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCOMMMaxWidth";	
	private String szQueryIdGet600 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilCline";	
	private String szQueryIdGet601 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilClineCheck1";	
	private String szQueryIdGet602 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilClineCheck2";	
	private String szQueryIdGet603 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilSndbk";	
	private String szQueryIdGet604 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdRetCrnReg_PIDEV";	
	private String szQueryIdGet605 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrCoilClineY";	
	private String szQueryIdGet606 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdConveyorMgtImg";
	private String szQueryIdGet607 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdConvStlNo";
	private String szQueryIdGet608 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getSlabYdStkcar";
	private String szQueryIdGet609 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getSlabYdStkcar2";

	private String szQueryIdGet610 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdPilingList_PIDEV";
	private String szQueryIdGet611 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdSizeStkStat";
	private String szQueryIdGet612 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getPlateYdStkOrdList";

	private String szQueryIdGet613 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioNew";
	private String szQueryIdGet624 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNew";
	private String szQueryIdGet625 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNew";
	private String szQueryIdGet626 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBedReNew_PIDEV";
	private String szQueryIdGet627 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdSendGF";
	private String szQueryIdGet628 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrHotCoilChk";
	private String szQueryIdGet629 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrHotCoilInChk";
	private String szQueryIdGet630 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrPoCoilInChk";
	private String szQueryIdGet631 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithTempBedRatioReNew";
	private String szQueryIdGet632 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNONEW";
	private String szQueryIdGet633 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithTempBedRatioReNewWithAlFrTo";
	private String szQueryIdGet634 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithSamePilingCdRatioReNewWithAlFrTo";
	private String szQueryIdGet635 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBedReNew_PIDEVWithAlFrTo";
	private String szQueryIdGet636 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedReNewWithAlFrTo";
	
	private String szQueryIdGet614 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithShipSimilarBedNew_PIDEV";
	private String szQueryIdGet615 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrWithMinSimilarBedNew";

	private String szQueryIdGet616 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStlnoStatFlexK";
	
	private String szQueryIdGet617 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrHRShearWKNEW";

	private String szQueryIdGet618 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getStlMoveSlCnt";
	private String szQueryIdGet619 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getCoilYdConveyorMgtImgNew";
	private String szQueryIdGet620 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO2";
	
	private String szQueryIdGet621 ="com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getHotcoilStrLocList_PIDEV";

	//저장위치수정 재료별 현 저장위치조회
	private String szQueryIdGet622 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrStlNo3";
	
	private String szQueryIdGet623 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrStlchk";
	
	private String szQueryIdGet999 ="com.inisteel.cim.yd.common.util.YdUtils.chklist";
	
	//항만슬라브야드 그라인딩머신 인출/보급 트래킹용 작업이력 get
	private String szQueryIdGet700 = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.getLatestYdWrkHist";
	//------------------------------insert query id--------------------------------------------//
	//기본 insert
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.insYdStklyr";
	
	//항만슬라브야드 그라인딩머신 인출/보급 트래킹
	private String szQueryIdGet9999 = "com.inisteel.cim.yd.slabyd.dao.SlabYdCommDAO.insSlabYdWrkHistByGrinder";
	//------------------------------update query id--------------------------------------------//
	//기본 update
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyr";
	//완료일시 -> 수정일시
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyr2";
	//재료번호를 기준으로 업데이트
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrSTLNO";

	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrColGpBedNo";

	//김진욱 적치열번호로 적치단업데이트(차량용 맵정리용)
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGp";
	//김진욱 적치열, 베드번호로 적치단 업데이트(대차 맵정리용)
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo";
	//김진욱 적치열, 베드번호로 적치단 업데이트(대차 맵정리용)
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrYdStkColGpYdStkBedNo2";

	// 권오창 (2009.09.30 ROT재료정보 수신시 업데이트)
	private String szQueryIdUpd8 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrStlNoOnRcvROT";

	// 권오창 (2010.01.18 해당 적치열+적치BED위치에 있는 모든단의 재료번호와 재료적치상태를 클리어)
	private String szQueryIdUpd9 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updClearYdStkColGpStkBedNo";
	
	private String szQueryIdUpd10 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updClearYmStklyr";

	private String szQueryIdUpd300 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrColActStat";
	private String szQueryIdUpd301 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrDan";
	private String szQueryIdUpd302 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrX";
	private String szQueryIdUpd303 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrInStlNo";
	private String szQueryIdUpd304 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrACT_STAT";
	private String szQueryIdUpd305 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrWithColStock";
	private String szQueryIdUpd306 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrTol";
	private String szQueryIdUpd307 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrWithStock";
	private String szQueryIdUpd308 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrPlate";
	private String szQueryIdUpd309 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrWithColStockStat";
	private String szQueryIdUpd310 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrWithJplateStlNo";
	private String szQueryIdUpd311 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.updYdStklyrWithGM";
	/*------------------------------------- SELECT -------------------------------------------*/
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO
	 *                                      1:YD_STK_COL_GP,YD_STK_BED_NO
	 *                                      2:YD_STK_COL_GP,YD_STK_BED_NO[PAGE]
	 *                                      3:STL_NO
	 *                                      4:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, YD_STK_LYR_MTL_STAT
	 *                                      5:YD_STK_COL_GP
	 *                                      6:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                      7:YD_STK_COL_GP, CNT
	 *                                      8:YD_STK_COL_GP,YD_STK_BED_NO
	 *                                      9:YD_EQP_ID, YD_STK_COL_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2 
	 *                                     10:YD_STK_COL_GP, YD_STK_BED_NO, STL_NO, YD_RCPT_DATE, ORD_NO, ORD_DTL, ORD_GP, YD_MTL_ITEM, STL_PROG_CD, PAGE_CNT1, ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                                     11:YD_GP, ORD_NO, ORD_DTL, DEST_CD, CUST_CD, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     12:YD_GP, YD_BAY_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     13:YD_GP, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *									   14:YD_GP, YD_BAY_GP, ORD_YEOJAE_GP,YD_STR_GTR_CD, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     15:YD_STK_LYR_NO1,YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO2, YD_STK_LYR_MTL_STAT
	 *                                     16:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, STL_NO
	 *                                     17:YD_GP, YD_TCAR
	 *                                     18:YD_CAR
	 *                                     19:NONE
	 *                                     20:YD_STK_COL_GP
	 *                                     27:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_MTL_STAT
	 *                                     28:
	 *                                     29: YD_SRK_COL_GP, YD_STK_BED_NO
	 *                                     30:YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, YD_STK_BED_NO, YD_STK_BED_NO, YD_STK_LYR_NO
	 *                                     31:YD_STK_COL_GP, YD_STK_BED_NO1, YD_STK_LYR_NO1, YD_STK_BED_NO2, YD_STK_BED_NO_R, YD_STK_LYR_NO2
	 *                                     32:YD_STK_COL_GP
	 *                                     .....
	 *                                     34: 
	 *                                     .....
	 *                                     36: 
	 *                                     38: YD_GP1, YD_GP2
	 *                                     39: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO
	 *                                     40: 
	 *                                     41: YD_AIM_RT_GP,YD_GP, YD_BAY_GP, CRN_RET_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     42:
	 *                                     43: YD_STK_COL_GP (스판정보까지 4자리 들어옴)
	 *                                     44:
	 *                                     45:
	 *                                     46:
	 *                                     47: YD_STK_COL_GP  (스판정보까지 4자리 들어옴)
	 *                                     48: YD_BAY_GP , YD_MTL_ITEM , WO_MSLAB_RPR_MTD
	 *                                     49: V_YD_STK_COL_GP, V_YD_STK_BED_NO 
	 *                                     50: V_YD_STK_COL_GP, V_YD_STK_BED_NO
	 *                                     51:
	 *                                     52:
	 *                                     53: YD_BAY_GP , HCR_GP
	 *                                     54: YD_BAY_GP , HCR_GP
	 *                                     55: YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     57: YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     58: YD_STK_COL_GP, YD_STK_BED_NO, PAGE_CNT1, PAGE_CNT2, ROW_CNT1, ROW_CNT2
	 *                                     68: YD_STK_COL_GP
	 *                                     69: YD_STK_COL_GP, YD_STK_BED_NO, ....
	 *                                     71: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO
	 *                                     72: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_MTL_STAT
	 *                                     73: YD_PILING_CD, YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     
	 *                                     75: YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     76: YD_AIM_RT_GP,YD_GP, YD_BAY_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     77: YD_AIM_RT_GP,YD_GP, YD_BAY_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     79: YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     80: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
	 *                                     81: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
	 *                                     82: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO, YD_PILING_CD
	 *                                     83: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO, YD_PILING_CD
	 *                                     84: V_YD_STK_COL_GP,V_YD_STK_BED_NO
	 *                                     85
	 *                                     86
	 *                                     87: YD_GP, ORD_NO, ORD_DTL , DEST_CD , CUST_CD
	 *                                     89: V_YD_SCH_CD
	 *                                     90: V_YD_SCH_CD
	 *                                     91: V_YD_SCH_CD
	 *                                     92: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO
	 *                                     93: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO
	 *                                     94: YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     95: YD_STK_COL_GP
	 *                                     96: YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     97: STL_NO, YD_STK_LYR_MTL_STAT
	 *                                     )
	 *
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdStklyr(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStklyr"; 
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStklyr(recPara, intGp);
			
			//parameter error return
			if(!blnChk_Field)
				return intRtnVal = -2;
			
			//query id setting
			if(intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if(intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
			else if(intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
			else if(intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet4);
			else if(intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet5);
			else if(intGp == 5)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet6);
			else if(intGp == 6)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet7);
			else if(intGp == 7)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet8);
			else if(intGp == 8)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9);
			else if(intGp == 9)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet10);
			else if(intGp == 10)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet11);
			else if(intGp == 11)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet12);
			else if(intGp == 12)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet13);
			else if(intGp == 13)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet14);
			else if(intGp == 14)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet15);
			else if(intGp == 15)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet16);
			else if(intGp == 16)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet17);
			else if(intGp == 17)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet18);
			else if(intGp == 18)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet19);
			else if(intGp == 19)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet20);
			else if(intGp == 20)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet21);
			else if(intGp == 21)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet22);
			else if(intGp == 22)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet23);
			else if(intGp == 23)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet24);
			else if(intGp == 24)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet25);
			else if(intGp == 25)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet26);
			else if(intGp == 26)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet27);
			else if(intGp == 27)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet28);
			else if(intGp == 28)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet29);
			else if(intGp == 29)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet30);
			else if(intGp == 30)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet31);
			else if(intGp == 31)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet32);
			else if(intGp == 32)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet33);
			else if(intGp == 33)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet34);
			else if(intGp == 35)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet36);
			else if(intGp == 37)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet38);
			else if(intGp == 38)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet39);
			else if(intGp == 39)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet40);
			else if(intGp == 40)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet41);		
			else if(intGp == 41)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet42);
			else if(intGp == 42)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet43);
			else if(intGp == 43)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet44);
			else if(intGp == 44)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet45);
			else if(intGp == 45)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet46);
			else if(intGp == 46)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet47);
			else if(intGp == 47)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet48);
			else if(intGp == 48)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet49);
			else if(intGp == 49)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet50);
			else if(intGp == 50)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet51);
			else if(intGp == 51)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet52);
			else if(intGp == 52)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet53);
			else if(intGp == 53)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet54);
			else if(intGp == 54)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet55);
			else if(intGp == 55)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet56);
			else if(intGp == 56)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet57);
			else if(intGp == 57)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet58);
			else if(intGp == 58)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet59);
			else if(intGp == 59)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet60);
			else if(intGp == 60)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet61);
			else if(intGp == 61)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet62);
			else if(intGp == 62)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet63);
			else if(intGp == 63)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet64);
			else if(intGp == 64)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet65);
			else if(intGp == 65)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet66);
			else if(intGp == 66)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet67);
			else if(intGp == 67)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet68);
			else if(intGp == 68)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet69);
			else if(intGp == 69)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet70);
			else if(intGp == 70)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet71);
			else if(intGp == 71)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet72);
			else if(intGp == 72)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet73);
			else if(intGp == 73)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet74);
			else if(intGp == 74)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet75);
			else if(intGp == 75)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet76);
			else if(intGp == 76)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet77);
			else if(intGp == 77)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet78);
			else if(intGp == 78)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet79);
			else if(intGp == 79)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet80);
			else if(intGp == 80)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet81);
			else if(intGp == 81)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet82);
			else if(intGp == 82)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet83);
			else if(intGp == 83)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet84);
			else if(intGp == 84)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet85);
			else if(intGp == 85)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet86);
			else if(intGp == 86)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet87);
			else if(intGp == 87)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet88);
			else if(intGp == 88)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet89);
			else if(intGp == 89)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet90);			
			else if(intGp == 90)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet91);			
			else if(intGp == 91)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet92);	
			else if(intGp == 92)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet93);
			else if(intGp == 93)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet94);
			else if(intGp == 94)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet95);
			else if(intGp == 95)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet96);
			else if(intGp == 96)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet97);
			else if(intGp == 97)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet98);
			else if(intGp == 98)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet99);
			else if(intGp == 99)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet100);
			else if(intGp == 100)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet101);
			else if(intGp == 101)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet102);
			else if(intGp == 102)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet103);
			else if(intGp == 103)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet104);
			else if(intGp == 104)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet105);
			else if(intGp == 105)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet106);
			else if(intGp == 106)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet107);
			else if(intGp == 107)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet108);
			else if(intGp == 108)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet109);
			else if(intGp == 109)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet110);
			else if(intGp == 110)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet111);
			else if(intGp == 111)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet112);	
			else if(intGp == 112)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet113);			
			
			else if(intGp == 113)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet114);
			
//			!AT		
			else if(intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if(intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			else if(intGp == 302)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet302);
			else if(intGp == 303)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet303);
			else if(intGp == 304)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet304);
			else if(intGp == 305)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet305);
			else if(intGp == 306)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet306);
			else if(intGp == 307)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet307);
			else if(intGp == 308)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet308);
			else if(intGp == 309)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet309);
			else if(intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
			else if(intGp == 401)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet401);
			else if(intGp == 402)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet402);
			else if(intGp == 403)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet403);
			else if(intGp == 404)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet404);
			else if(intGp == 500)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet500);
			else if(intGp == 600)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet600);
			else if(intGp == 601)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet601);
			else if(intGp == 602)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet602);
			else if(intGp == 603)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet603);
			else if(intGp == 604)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet604);
			else if(intGp == 605)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet605);
			else if(intGp == 606)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet606);
			else if(intGp == 607)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet607);
			else if(intGp == 608)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet608);
			else if(intGp == 609)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet609);
			else if(intGp == 610)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet610);
			else if(intGp == 611)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet611);
			else if(intGp == 612)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet612);
			else if(intGp == 613)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet613);
			else if(intGp == 614)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet614);
			else if(intGp == 615)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet615);
			else if(intGp == 616)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet616);
			else if(intGp == 617)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet617);
			else if(intGp == 393)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet393);
			else if(intGp == 618)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet618);
			else if(intGp == 619)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet619);
			else if(intGp == 620)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet620);
			else if(intGp == 621)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet621);
			else if(intGp == 622)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet622);
			else if(intGp == 623)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet623);
			else if(intGp == 624)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet624);
			else if(intGp == 625)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet625);
			else if(intGp == 626)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet626);
			else if(intGp == 627)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet627);
			else if(intGp == 628)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet628);
			else if(intGp == 629)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet629);
			else if(intGp == 630)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet630);
			else if(intGp == 631)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet631);
			else if(intGp == 632)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet632);
			else if(intGp == 633)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet633);		
			else if(intGp == 634)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet634);	
			else if(intGp == 635)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet635);	
			else if(intGp == 636)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet636);			
			else if(intGp == 999)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet999);
			
			
//PIDEV
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");				
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", recPara.getField("JSPEED_QUERY_ID").toString(), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", toQuery_ID);				
						
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if(rsTemp.size() > 0)
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
	} //end of getYdStklyr
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int        intGp  구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO
	 *                               1:YD_STK_COL_GP,YD_STK_BED_NO
	 *                               2:YD_STK_COL_GP,YD_STK_BED_NO[PAGE]
	 *                               3:STL_NO 
	 *                               4:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, YD_STK_LYR_MTL_STAT
	 *                               5:YD_STK_COL_GP)
	 *                               6:YD_STK_COL_GP, YD_STK_BED_NO
	 *                               7:YD_STK_COL_GP, CNT
	 *                               8:YD_STK_COL_GP,YD_STK_BED_NO 
	 *                               9.YD_EQP_ID, YD_STK_COL_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              10.YD_STK_COL_GP, YD_STK_BED_NO, STL_NO, YD_RCPT_DATE, ORD_NO, ORD_DTL, ORD_GP, YD_MTL_ITEM, STL_PROG_CD, PAGE_CNT1, ROW_CNT1,PAGE_CNT2,ROW_CNT2
	 *                              11.YD_GP , ORD_NO, ORD_DTL, DEST_CD, CUST_CD, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              12:YD_GP, YD_BAY_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              13:YD_GP, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              14:YD_GP, YD_BAY_GP, ORD_YEOJAE_GP,YD_STR_GTR_CD, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              15:YD_STK_LYR_NO1,YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO2, YD_STK_LYR_MTL_STAT
	 *                              16:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, STL_NO
	 *                              17:YD_GP, YD_TCAR
	 *                              18:YD_CAR
	 *                              19:NONE
	 *                              20:YD_STK_COL_GP
	 *                              26:YD_GP, YD_BAY_GP
 	 *                              27:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_MTL_STAT
 	 *                              28:
 	 *                              29:YD_STK_COL_GP , YD_STK_BED_NO
	 *                              30:YD_WBOOK_ID, YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, YD_STK_BED_NO, YD_STK_BED_NO, YD_STK_LYR_NO
 	 *                              31:YD_STK_COL_GP, YD_STK_BED_NO1, YD_STK_LYR_NO1, YD_STK_BED_NO2, YD_STK_BED_NO_R, YD_STK_LYR_NO2
 	 *                              32:YD_STK_COL_GP
 	 *                              33:V_YD_GP, V_YD_STK_COL_GP, V_STL_NO, V_ORDERLINE, V_RECEIPT_DATE, V_ORD_GP, V_ITEMNAME_CD, V_STL_PROG_CD, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, V_ROW_CNT2
 	 *                              34: 
 	 *                              36: 
 	 *                              38: YD_GP1, YD_GP2
 	 *                              39: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO
 	 *                              40:
 	 *                              41: YD_AIM_RT_GP,YD_GP, YD_BAY_GP, CRN_RET_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
 	 *                              42:
 	 *                              43: YD_STK_COL_GP (스판정보까지 4자리 들어옴)
	 *                              44:
	 *                              45:
	 *                              46:
	 *                              47: YD_STK_COL_GP  (스판정보까지 4자리 들어옴)\
	 *                              48: YD_BAY_GP , YD_MTL_ITEM , WO_MSLAB_RPR_MTD 
	 *                              49: V_YD_STK_COL_GP, V_YD_STK_BED_NO
	 *                              50: V_YD_STK_COL_GP, V_YD_STK_BED_NO 
	 *                              51:
	 *                              52:
	 *                              53: YD_BAY_GP , HCR_GP
	 *                              54: YD_BAY_GP , HCR_GP
	 *                              55: YD_STK_COL_GP, YD_STK_BED_NO
	 *                              57: YD_STK_COL_GP, YD_STK_BED_NO
	 *                              58: YD_STK_COL_GP, YD_STK_BED_NO, PAGE_CNT1, PAGE_CNT2, ROW_CNT1, ROW_CNT2
	 *                              68: YD_STK_COL_GP
	 *                              69: YD_STK_COL_GP, YD_STK_BED_NO, ....
	 *                              72: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_MTL_STAT
	 *                              73: YD_PILING_CD, YD_STK_COL_GP, YD_STK_BED_NO
	 *                              
	 *                              75: YD_STK_COL_GP, YD_STK_BED_NO
	 *                              76: YD_AIM_RT_GP,YD_GP, YD_BAY_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              77: YD_AIM_RT_GP,YD_GP, YD_BAY_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              78: YD_AIM_RT_GP,YD_GP, YD_BAY_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                              79: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO, STL_NO
	 *                              80: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
	 *                              81: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
	 *                              82: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO, YD_PILING_CD
	 *                              83: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO, YD_PILING_CD
	 *                              84: YD_STK_COL_GP , YD_STK_BED_NO
	 *                              85: YD_GP
	 *                              86: YD_GP
	 *                              87: YD_GP, ORD_NO, ORD_DTL , DEST_CD , CUST_CD
	 *                              88: YD_GP, YD_BAY_GP
	 *                              89: V_YD_SCH_CD
	 *                              90: V_YD_SCH_CD
	 *                              91: V_YD_SCH_CD
	 *                              92: YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO
	 *                              93:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO
	 *                              94:YD_STK_COL_GP, YD_STK_BED_NO
	 *                              95:YD_STK_COL_GP
	 *                              96: YD_STK_COL_GP, YD_STK_BED_NO
	 *                              97: STL_NO, YD_STK_LYR_MTL_STAT
 	 *                              )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException
	 */
	public boolean chkPara_getYdStklyr(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if(intGp == 0 || intGp == 39 || intGp == 71 || intGp == 92 || intGp == 93 || intGp == 99 || intGp == 100) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
			} else if(intGp == 1 ||intGp == 94 || intGp == 96 || intGp == 98 || intGp == 101) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			
			} else if(intGp == 2 || intGp ==69) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			} else if(intGp == 3) {
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			} else if(intGp == 4 || intGp ==74) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_MTL_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				
			} else if(intGp == 5 || intGp ==32 || intGp == 52 || intGp == 56) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if(intGp == 6 || intGp == 28) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if(intGp == 7){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'L', 0, 0);
				
			}else if(intGp == 8) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if(intGp == 9) {
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 10) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_RCPT_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_STL_PROG_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if(intGp == 11 || intGp == 62) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_DEST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if(intGp == 12 || intGp == 59) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if(intGp == 13) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
					
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if(intGp == 14) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_YEOJAE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STR_GTR_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
			
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if(intGp == 15) {
				szFieldName = "V_YD_STK_LYR_NO1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'L', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_YD_STK_LYR_NO2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_MTL_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);				
			} else if(intGp == 16) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				
			} else if(intGp == 17) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_TCAR";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);			
											
			} else if(intGp == 18) {
				szFieldName = "V_YD_CAR";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
			} else if(intGp == 20 ) {
				szFieldName = "YD_STK_COL_GP";
				//blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;	
				
//				szFieldName = "V_PAGE_CNT1";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
//				if(!blnErr) return blnErr;
//				
//				szFieldName = "V_ROW_CNT1";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
//				if(!blnErr) return blnErr;
//				
//				szFieldName = "V_PAGE_CNT2";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
//				if(!blnErr) return blnErr;
//				
//				szFieldName = "V_ROW_CNT2";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			} else if(intGp == 21 || intGp == 61) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
			} else if(intGp == 22 || intGp == 65 || intGp == 66) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
			} else if(intGp == 23) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if(intGp == 24) {
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			} else if(intGp == 25) {
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				
			} else if(intGp == 26) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			} else if(intGp == 27){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_YD_STK_LYR_MTL_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);				
			} else if(intGp == 29){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
								
			} else if(intGp == 30){
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'L', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_LYR_NO1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO_L";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_LYR_NO2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			} else if(intGp == 31){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_NO1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
								
				szFieldName = "V_YD_STK_BED_NO2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO_R";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_LYR_NO2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			} else if(intGp == 33){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_COIL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORDERLINE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 13, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_RECEIPT_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PROG_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
			} else if(intGp == 37){
//				szFieldName = "V_YD_STK_COL_GP1";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
//				if(!blnErr) return blnErr;
			
//				szFieldName = "V_YD_STK_COL_GP2";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
//				if(!blnErr) return blnErr;
				/*
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				*/
				
			} else if(intGp == 38){
				szFieldName = "V_YD_GP1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				
			} else if(intGp == 40){
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}
			else if(intGp == 46) {
				
				szFieldName = "V_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6,2, 'S', 0, 0);
				
			}
			else if(intGp == 60) {				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if(intGp == 41) {				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_CRN_RET_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			} else if(intGp == 42) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
					
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if(intGp == 43 || intGp ==47) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
			
			} else if(intGp == 44) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "ORD_GP"; //!A 수주구분 (박지열 - 2010/03/24)
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 3, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "CUST_CD"; //!A 고객사코드 (박지열 - 2010/03/24)
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 3, 'S', 0, 0);
				if(!blnErr) return blnErr;

			} else if(intGp == 45) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ITEMNAME_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			}  else if(intGp == 48) {
						
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
					
				szFieldName = "V_WO_MSLAB_RPR_MTD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 49 ||intGp == 50 ||intGp == 57 || intGp == 84) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			} else if(intGp == 51) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
								
				szFieldName = "V_YD_STK_LYR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
			}  else if(intGp == 53 || intGp == 54) {
						
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_HCR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if(intGp == 55) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if(intGp == 58) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			} else if(intGp == 63) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);

				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			}  else if(intGp == 64) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				
				// !A 목적지코드 추가
				szFieldName = "V_DEST_CD1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 3, 'S', 0, 0);
				szFieldName = "V_DEST_CD2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 3, 'S', 0, 0);
				
			} else if(intGp == 68) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			}  else if(intGp == 70) {				
								
				szFieldName = "V_FROMLOC";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 12, 1, 'S', 0, 0);
			}  else if(intGp == 72) {				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_LYR_MTL_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			}  else if(intGp == 72) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			}else if(intGp == 75){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;

				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;				
			} else if(intGp == 76 || intGp == 77 || intGp == 78) {				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
				szFieldName = "V_PAGE_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			} else if(intGp == 79) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 3, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 3, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 3, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 3, 'S', 0, 0);
			
			} else if( intGp == 80 || intGp == 81 ) {
				//80: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, YD_PILING_CD
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
				
				szFieldName = "V_YD_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
			} else if( intGp == 82 || intGp == 83 ) {
				//83: SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, BED_SEARCH_GP, COL_GT_LT_GP, YD_STK_COL_GP, YD_STK_BED_NO, YD_PILING_CD
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
				
				szFieldName = "V_COL_GT_LT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
			
			}  else if( intGp == 85 || intGp == 86 ) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if(!blnErr) return blnErr;
			}  else if(intGp == 87) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_DEST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
			
			}  else if(intGp == 88) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
			}  else if(intGp == 89) {
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;

			}  else if(intGp == 90) {
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
			}  else if(intGp == 91) {
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;				
			}  else if(intGp == 95) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);

				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
			} else if(intGp == 97) {
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_MTL_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
//				!AT
			}  else if(intGp == 300) {
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);

				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if(!blnErr) return blnErr;
				
			} else if(intGp == 301) {
					
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if(!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LYR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
			} else if(intGp == 302) {
				
				szFieldName = "V_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6,2, 'S', 0, 0);
					
			} else if(intGp == 303) {
				
				szFieldName = "V_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6,2, 'S', 0, 0);
					
			} else if(intGp == 400) {
				
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18,1, 'S', 0, 0);
					
			}
			 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStklyr
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdStklyr(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		
		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkParameter(recPara);
			
			//parameter error return
			if(!blnChk_Field)
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
	} // end of insYdStklyr
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 INSERT parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_YD_STK_COL_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if(!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_BED_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_REGISTER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if(!blnErr) return blnErr;
	
//			szFieldName = "V_MODIFIER";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
//			if(!blnErr) return blnErr;
//	
//			szFieldName = "V_MOD_DDTT";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
//			if(!blnErr) return blnErr;
//			
//			szFieldName = "V_DEL_YN";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//			if(!blnErr) return blnErr;
			
			szFieldName = "V_STL_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_ACT_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_MTL_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_XAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_YAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if(!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LYR_ZAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkPara_YdStklyr

/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp  
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYdStklyrNEW(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updYdStklyrNEW";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		List sposYNChklist = null;
		try {
			//장애 발생시 이전 소스로 원복 하기 위한 조치
			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStklyrDao.updYdStklyrchklist";
		    sposYNChklist = dao.getCommonList(QueryId, new Object[]{});

		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
			
				String StringGp=Integer.toString(intGp) ;
			
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdStklyrReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec,StringGp });
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
		    }else{
	    		//기존 방식 적용 
	    		intRtnVal = this.updYdStklyrNEWTX(inRec,intGp);
	    		if(intRtnVal ==0){
	    			return intRtnVal = -1;
	    		}
	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrNEW
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO,
	 *                              1:YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO(수정일->완료일시)
	 *                              2:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrNEWTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStklyrNEWTX";
		String szMsg = null;
		String szOperationName = "야드적치단 UPDATE TX";
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
			intRtnVal = this.getYdStklyr(inRec, outRecSet, 0);
			//parameter error return
			if(intRtnVal < 0) {
	//			szMsg = "parameter error!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//data not found return
			if(intRtnVal == 0) {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//duplicate data return
			if(outRecSet.size() != 1) {
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
			
			if(intGp != 303){
				//parameter check
				blnChk_Field = this.chkParameter(recOutPara);
				
				//parameter error return
				if(!blnChk_Field) return intRtnVal = -2;
			}	
			//query id setting
			if(intGp == 0)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			else if(intGp == 1)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			else if(intGp == 2)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			else if(intGp == 3)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			else if(intGp == 303)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd303);
			else if(intGp == 308)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd308);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrNEWTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO,
	 *                              1:YD_STK_COL_GP,YD_STK_BED_NO,YD_STK_LYR_NO(수정일->완료일시)
	 *                              2:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyr(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStklyr";
		String szMsg = null;
		String szOperationName = "야드적치단 UPDATE";
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
			intRtnVal = this.getYdStklyr(inRec, outRecSet, 0);
			//parameter error return
			if(intRtnVal < 0) {
	//			szMsg = "parameter error!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//data not found return
			if(intRtnVal == 0) {
	//			szMsg = "data not found!";
	//			ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			}
			
			//duplicate data return
			if(outRecSet.size() != 1) {
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
			
			if(intGp != 303){
				//parameter check
				blnChk_Field = this.chkParameter(recOutPara);
				
				//parameter error return
				if(!blnChk_Field) return intRtnVal = -2;
			}	
			//query id setting
			if(intGp == 0)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
			else if(intGp == 1)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			else if(intGp == 2)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			else if(intGp == 3)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			else if(intGp == 303)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd303);
			else if(intGp == 308)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd308);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyr
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE_STL_NO[K]
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyr_STL_NO(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//parameter check
			blnChk_Field = this.chkParameter_STL_NO(recPara);
			
			//parameter error return
			if(!blnChk_Field)
				return intRtnVal = -2;
			
			
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd1);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyr_STL_NO
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드작업예약 parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkParameter_STL_NO(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		

		szFieldName = "V_MODIFIER";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_MOD_DDTT";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_DEL_YN";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_STL_NO1";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_YD_STK_LYR_ACT_STAT";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_YD_STK_LYR_MTL_STAT";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_YD_STK_LYR_XAXIS";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_YD_STK_LYR_YAXIS";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_YD_STK_LYR_ZAXIS";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
		if(!blnErr) return blnErr;
		
		szFieldName = "V_STL_NO2";
		blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
		

		return blnErr;
	} //end of chkParameter_STL_NO
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_YD_STK_COL_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_NO";
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
			
			szFieldName = "V_STL_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_ACT_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_MTL_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_XAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_YAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_LYR_ZAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}

	} // end of dataMapping
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE_YD_STK_COL_GP
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrYdStkColGp(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
//			//parameter check
//			blnChk_Field = this.chkParameter_STL_NO(recPara);
//			
//			//parameter error return
//			if(!blnChk_Field)
//				return intRtnVal = -2;
			
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd5);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyr_STL_NO
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE_YD_STK_COL_GP
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYmStklyr(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
 
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd10);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYmStklyr
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE_YD_STK_COL_GP, YD_STK_BED_NO
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrYdStkColGpBedNo(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
//			//parameter check
//			blnChk_Field = this.chkParameter_STL_NO(recPara);
//			
//			//parameter error return
//			if(!blnChk_Field)
//				return intRtnVal = -2;
			
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrYdStkColGpBedNo
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE_YD_STK_COL_GP, YD_STK_BED_NO
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrYdStkColGpBedNo2(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
//			//parameter check
//			blnChk_Field = this.chkParameter_STL_NO(recPara);
//			
//			//parameter error return
//			if(!blnChk_Field)
//				return intRtnVal = -2;
			
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrYdStkColGpBedNo2

	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 업데이트  
	 *      
	 *      C3ROT재료도착통과정보 올 시 
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrStlNoOnRcvROT(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recPara = null;

		// 변수 선언
		int nRet = 0;

		try {
			// 필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
				
			// 쿼리 아이디 설정
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd8);
	
			// 쿼리 실행
			nRet = dbAssDao.trtProcess(recPara);
			if(nRet <= 0){
				nRet = -3;
			}
			
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		
		return nRet;
	} 
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치위치(YD_STK_COL_GP + YD_STK_BED_NO)의 모든단 재료번호와 재료적치상태 클리어  
	 *      
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrClearStkColGpStkBedNo(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recPara = null;

		// 변수 선언
		int nRet = 0;

		try {
			// 필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
				
			// 쿼리 아이디 설정
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd9);
	
			// 쿼리 실행
			nRet = dbAssDao.trtProcess(recPara);
			if(nRet <= 0){
				nRet = -3;
			}
			
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		
		return nRet;
	} 

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrActStat(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd300);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // e
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 UPDATE
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrDan(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd301);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // 
	

	/**
	 *      [A] 오퍼레이션명 : 야드적치단 좌표만  UPDATE
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrX(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd302);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // 
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 updYdStklyrACT_STAT
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrACT_STAT(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd304);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyr_STL_NO
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 updYdStklyrWithColStock
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrWithColStock(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd305);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyr_STL_NO
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 updYdStklyrWithColStock
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrWithColStockStat(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd309);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyr_STL_NO
	
	/**
	 *      [A] 오퍼레이션명 : 항만 그라인딩머신 트래킹정보 SHIFT updYdStklyrWithGM
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrWithGM(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd311);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrWithGM
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치BED UPDATE
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrTol(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd306);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // 	
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 updYdStklyrWithColStock
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrWithStock(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd307);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyr_STL_NO
	
	/**
	 *      [A] 오퍼레이션명 : 야드적치단 updYdStklyrJplateStlNo
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStklyrJplateStlNo(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd310);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStklyrJplateStlNo
	
	/**
	 *      [A] 오퍼레이션명 : 항만슬라브야드 그라인딩머신 인출/보급 작업이력 insert
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */
	public int insYdWrkHistByGrinder(JDTORecord inRec) throws DAOException, JDTOException {
		String szMethodName = "insYdWrkHistByGrinder";
		String szOperationName = "항만슬라브야드 그라인딩머신 인출/보급 작업이력 insert";
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;
		//ydUtils.putLog(szDaoName, szMethodName, "수정자"+inRec.getField("MODIFIER").toString(), 1);
		//ydUtils.putLog(szDaoName, szMethodName, "재료번호"+inRec.getField("STL_NO").toString(), 1);
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
						
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet9999);
			//ydUtils.putLog(szDaoName, szMethodName, "modi: "+recPara.getField("V_MODIFIER").toString(), 1);
			//ydUtils.putLog(szDaoName, szMethodName, "stl_no: "+recPara.getField("V_STL_NO").toString(), 1);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdWrkHistByGrinderIn
	/**
	 *      [A] 오퍼레이션명 : 최근 작업이력의 권하위치 get (항만스카핑야드 그라인더 인출 확인목적)
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return 
	 * @throws DAOException
	 * @throws JDTOException 
	 */
	public boolean isGrinderTakeOut(JDTORecord inRec) throws DAOException, JDTOException {
		boolean RtnVal = false;
		JDTORecord recPara = null;
		JDTORecord temp =null;
		JDTORecordSet rsTemp = null;
		String YdDnWrLoc = null;
		//ydUtils.putLog(szDaoName, szMethodName, "수정자"+inRec.getField("MODIFIER").toString(), 1);
		//ydUtils.putLog(szDaoName, szMethodName, "재료번호"+inRec.getField("STL_NO").toString(), 1);
		
		try {
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
						
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet700);
			
			//query execute
			
			//rsTemp = dbAssDao.getRecord(recPara);
			rsTemp = dbAssDao.getRecordSet(recPara);
			if(rsTemp != null && rsTemp.next()) {
				temp = rsTemp.getRecord();
				YdDnWrLoc = temp.getFieldString("YD_DN_WR_LOC");
				YdDnWrLoc  = temp.getField("YD_DN_WR_LOC").toString();
			}
			//YdDnWrLoc = rsTemp.getField("YD_DN_WR_LOC").toString();
			if("MBGM0101".equals(YdDnWrLoc)){
				RtnVal =true;
			}
			//result recordSet check
			
			//ydUtils.putLog(szDaoName, szMethodName, "modi: "+recPara.getField("V_MODIFIER").toString(), 1);
			//ydUtils.putLog(szDaoName, szMethodName, "stl_no: "+recPara.getField("V_STL_NO").toString(), 1);
			//query execute
			//intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return RtnVal;
	} // end of insYdWrkHistByGrinderIn
	

/*------------------------------------- DELETE -------------------------------------------*/
} // end of class






