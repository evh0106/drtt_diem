package com.inisteel.cim.yd.common.dao.ydStockDao;

import java.util.ArrayList;
import java.util.List;

import xlib.cmc.GridData;
import xlib.cmc.OperateGridData;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.or.common.util.CmnUtil;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
//import com.inisteel.cim.or.common.util.RullCallUtil;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

/**
 *      [A] 클래스명 : 야드저장품 DAO
*/

public class YdStockDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();
	private CCommUtils	   commUtils   = new CCommUtils();	
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStock_PIDEV";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockSLABCOMM";
	private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockPLATECOMM";
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMM_PIDEV";
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockMSLABCOMM";
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMSLABCOMM";
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCOILCOMM";
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCOILCOMM";
	// 페이징 쿼리 추가(이현성_20081204_이현성)
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCTHRMILLWO_PAGE";
	// FROM POINT , TO 포인트를 구하는 쿼리 (이현성 20081209)
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCTFTPMSWOIDX_FROMTO";
	// FROM POINT 부터 TO POINT 까지의 HEAT번호를 가져오는 쿼리 (이현성 20081209)
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCTFTPMSWOHEAT_HEATNO";
	// FROM POINT 부터 TO POINT 까지의 HEAT번호로 부터 주편지시 정보를 가져오는 쿼리 (추가 : 이현성 20081209)
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCTFMSLABWO_HEATNO";
	// C열연압연작업지시 로트편성
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCHRSUPPLYLOT";
	// Scarfing 보급 로트편성 --> C연주 정정보급로트편성으로 변경
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockSCARFINGSUPPLYLOT";
	// 연주정정 로트편성
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockSHEARSUPPLYLOT";
	// C연주소재이송상차Lot편성,대차이송상차Lot편성,C열연수냉탱크Lot편성
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockMTLFTMVCARLOADLOT";
	//C연주 외판출하 Lot 편성
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockPLDISTCARLOADLOT";
	//C열연 정정입측 보급Lot 편성
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCHrShearInSupplyLot";
	//C연주장입LotNo적용보급Lot편성
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCCcChgLotNoSupplyLot";
	//C연주 정리 Lot 편성
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCCcReadjLot";
	//A후판장입LotNo적용보급Lot편성
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockAPlChgLotNoSupplyLot";
	//후판창고 선별작업 Lot 편성
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockPlWhSelWrkLot";
	//C열연정정보급준비Lot편성(동간이적)
	private String szQueryIdGet25 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCHrShearSupPrepLotOut";
	//C열연정정보급준비Lot편성(동내이적)
	private String szQueryIdGet26 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCHrShearSupPrepLotIn";
	//2009.02.17 권오창추가
	private String szQueryIdGet27 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStl_PIDEV";
	//C연주소재이송상차LOT편성
	private String szQueryIdGet28 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLot1";
	//C연주소재이송상차LOT편성
	private String szQueryIdGet29 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLot2";
	//C연주소재이송상차LOT편성
	private String szQueryIdGet30 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLot3";
	//C연주소재이송상차LOT편성
	private String szQueryIdGet31 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLot4";
	//2009.03.02연은정-상차지시
	private String szQueryIdGet32 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTRANS_ORD_DAT";
	//2009.03.02연은정-이송지시
	private String szQueryIdGet33 	= "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE";
	// 2009.03.06 이현성 
	private String szQueryIdGet34 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdEventWorkMatRef_PAGE";
	// 2009.03.09 연은정
	private String szQueryIdGet35 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockHrCShear";
	// 2009.03.09 연은정
	private String szQueryIdGet36 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockOSCOMM";
	// 2009.03.10 연은정
	private String szQueryIdGet37 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockSLABCOMMOSCOMM";
	// 2009.03.12 권오창
	private String szQueryIdGet38 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrTransOrdDateSeqNo";
	// 2009.03.19 김진욱
	private String szQueryIdGet39 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNo";
	// Scarfing 보급 로트편성 - 2009.03.23 임춘수
	private String szQueryIdGet40 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockSCARFINGSUPPLYLOT1";
	// 공베드확보 로트편성 - 2009.03.23 임춘수
	private String szQueryIdGet41 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockEmptyBedSecurLotComp";
	//C연주장입LotNo적용보급Lot편성(동간이적)
	private String szQueryIdGet42 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCCcChgLotNoSupplyLot1"; 
	//이송,출하지시 준비LOT편성(동간이적, 동내이적 Desc)- 2009.03.25 임춘수
	private String szQueryIdGet43 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMvLotGp";

	//공통 LIKE 조회 (이현성 :20090325)
	private String szQueryIdGet44 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTB_PT_SLABCOMMwithLike";  
	private String szQueryIdGet45 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTB_PT_MSLABCOMMwithLike";
	private String szQueryIdGet46 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTB_PT_PLATECOMMwithLike";
	private String szQueryIdGet47 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTB_PT_COILCOMMwithLike";
	//이송지시 준비LOT편성(동간이적, 동내이적 Asc) - A후판슬라브 - 2009.03.26 임춘수
	private String szQueryIdGet48 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMvLotGpASC";
	//야드,동,스판,목표행선 대상재 검색 - 2009.03.27 임춘수
	private String szQueryIdGet49 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTgMtlLot";
	//해당적치단 위의 대상재검색 - 2009.03.27 임춘수
	private String szQueryIdGet50 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTgMtlAboveStkLyrLot";
	//야드의 목표행선별 대상재 검색 - 2009.03.31  임춘수
	private String szQueryIdGet51 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTgMtlAimRtLot";
	//야드, 동의 목표행선별 대상재 검색 - 2009.03.31  임춘수
	private String szQueryIdGet52 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockRetnLot";
	//야드, 동의 목표행선별 대상재 검색(열 : DESC, BED : ASC, 단 : DESC) - 2009.04.07  임춘수
	private String szQueryIdGet53 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdBayRtDesc";
	//	코일 제품상세정보 조회/검색 - 2009.04.09  심명순
	private String szQueryIdGet54 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPtCoilCommInfoji_PIDEV";
	//C연주슬라브야드 구내이송 직상차용 대상재 조회(D동 PICKUP BED 2개, C동 PICKUP BED 1개, C동 DEPILER 2개) - 임춘수 2009.04.23
	private String szQueryIdGet55 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCarDirectLoadLot";
	// 김종건  20090407 - 후판제품야드 입고예정 모니터링
	private String szQueryIdGet56 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdRcptPlnMonitor_PIDEV";
	// 김종건  20090409 - 코일 작업실적 일품조회
	private String szQueryIdGet57 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStklyrByWrkRsltDd_PIDEV";
	// 대차 작업관리 - 2009.04.24 심명순
	private String szQueryIdGet58 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTcarSchMtlList";
	// 대차 작업관리 제품상세조회  - 2009.04.27 심명순
	private String szQueryIdGet59 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTcarWorkMtlList";
	// 사유별 이적등록 조회  - 2009.04.28 심명순
	private String szQueryIdGet60 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getBecauseMv_PIDEV";
	// 코일소재 재료상세정보 조회  - 2009.04.30 김창일
	private String szQueryIdGet61 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilYdMtlDtl";
	// 입고 Backup처리 조회  - 2009.04.30 심명순
	private String szQueryIdGet62 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdBackupWork";
	// 김종건 20090506 - 준비이적대상재조회 팝업  
	private String szQueryIdGet63 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdRedyTranReSrcPop";
	// 권오창 20090507
	private String szQueryIdGet64 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMSLABCOMMCrnSchID";
	// 권오창 20090507
	private String szQueryIdGet65 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCTFHEATWOCrnSchID";
	// 권오창 20090507
	private String szQueryIdGet66 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCSMSLABFSWRCrnSchID";
	// 권오창 20090507
	private String szQueryIdGet67 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCTFSLABWOCrnSchID";
	//야드,동,스판,목표행선 대상재 오름차순 검색 - 임춘수 2009.05.07
	private String szQueryIdGet68 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdBaySpanRtLot";
	//	 심명순 20090506 - 저장그룹 상세조정 코드조회  
	private String szQueryIdGet69 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdCodeSearch";
	//	 심명순 20090506 - 저장그룹 상세조정 조회  (combobox조건)
	private String szQueryIdGet70 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdSvGpInfoCombo_PIDEV";
	// 권오창 20090511 
	private String szQueryIdGet71 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockSTLNOCrnSchCrnWrkMtl";
	
	// 이현성  20090511 
	private String szQueryIdGet72 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCtNPlrefurwo";
	// 이현성  20090511 
	private String szQueryIdGet722 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCtNPlrefurwo2";

	// 심명순 20090506 - 저장그룹 상세조정 조회  (주문일 조건)
	private String szQueryIdGet73 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdSvGpInfoDate_PIDEV";
	// 심명순 20090506 - 저장그룹 상세조정 조회  (ORDER LINE 조건)
	private String szQueryIdGet74 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdSvGpInfoOrder_PIDEV";
	//야드,동,스판,목표행선 대상재 오름차순 검색[작업예약재료와 크레인작업재료로 등록된 재료는 대상재에서 제외] - 임춘수 2009.05.13
	private String szQueryIdGet75 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdBaySpanRtLotExceptWrkMtl";
	// 김종건  20090513 - 후판제품야드 선별 대상제품 처리
	private String szQueryIdGet76 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdSortWrkPdtProc_PIDEV";
	// 연은정 20090518 - 후판Plate사양 조회
	private String szQueryIdGet77 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCtPlatspec";
	// 김종건  20090520 - 후판제품야드 Piling 정보변경 및 입고처리
	private String szQueryIdGet78 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdPilingDataChng";
	// 김종건  20090520 - 후판제품야드 Piling 정보변경 및 입고처리 상세조
	private String szQueryIdGet79 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdPilingDataChngDtl_PIDEV";
	// 연은정 20090525 - 충당실적[슬라브]
	private String szQueryIdGet80 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMatchWrSlab";
	// 연은정 20090525 - 충당실적[코일]
	private String szQueryIdGet81 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMatchWrCoil";
	// 김종건  20090528 - 후판제품야드 저장 Group 편성 스케줄
	private String szQueryIdGet82 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdSvGpSchFm_PIDEV";
	//	심명순 20090518 - 코일제품 제품단위 이적등록 조회
	private String szQueryIdGet83 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilYdGdsGdsUnitMvReg_PIDEV";
	//	김창일 20090529 - 재료상세백업 재료정보조회
	private String szQueryIdGet84 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilYdGdsStlDtlBackup";
	//  윤재광 20090603 - 코일이송지시 검색쿼리 추가 
	private String szQueryIdGet85   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_COIL";
	//  윤재광 20090603 - 코일임가공 이송지시 검색쿼리 추가 
	private String szQueryIdGet86   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_RENTCOIL";
	//  윤재광 20090603 - 주편번호로 슬라브정보 가져오기 
	private String szQueryIdGet87   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMMWITHMSLAB";

	// 연은정20090610 - OS공통 조회
	private String szQueryIdGet89   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOSCOMM";
	
	// 이현성 20090616 - OS공통 PILING CODE LIKE 조회
	private String szQueryIdGet90   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOSCOMMPilingLike";
	
	// 이현성 20090616 - OS공통 PILING CODE NONE  조회
	private String szQueryIdGet91   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOSCOMMPilingNone";
	
	private String szQueryIdGet616 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getBecauseMv2_PIDEV";
	
	private String szQueryIdGet617 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getBecauseMv3";
	
	//허정욱 2020.03.18 후판재 스카핑여부 
	private String szQueryIdGet800 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateIsScarfing";
	///////////////////////////////////생산통제 정보 조회 쿼리////////////////////////////////////////////////
	
	// 생산통제 압연지시[C열연, B열연가열로보급 압연지시] - 확정지시,작업계획
	/* 2010.01.20 권오창  조회쿼리에 항목이 없어서 편집하는 소스에서 에러남 ㅡ.ㅡ(쿼리에 STL_NO, SLAB_T, SLAB_W, SLAB_LEN, SLAB_WT 항목추가) */
	private String szQueryIdGet88   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM_MillPlntGp1";
	/* 일괄배치정보  한쿼리로 만듬*/
	private String szQueryIdGet217   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM_MillPlntGp1_C_PIDEV";
	// 생산통제 압연지시 [A후판압연지시]- 확정지시,작업계획
	/* 2010.01.20 권오창  조회쿼리에 항목이 없어서 편집하는 소스에서 에러남 ㅡ.ㅡ(쿼리에 CURR_PROG_CD, ITEMNAME_CD, DEMANDER_CD, ORD_HCR_GP, STL_APPEAR_GP, PTOP_PLNT_GP, REHEAT_SLAB_GP 추가) */
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM_MillPlntGp";
	/* 일괄배치정보  한쿼리로 만듬*/
	private String szQueryIdGet218= "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM_MillPlntGp_C_PIDEV";
	
	//YJK 압연작업지시 대상재 조회 
	//C열연압연작업지시 로트편성 - 임춘수 추가 2009.06.18
	//C열연가열로보급LOT편성 - 적치중 또는 권상대기 인 재료 조회
	//설비(보급PICKUP베드 등)베드상의 대상재 제외, 기존 작업예약에 등록된 대상재 제외
	private String szQueryIdGet92   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCHRSUPPLYLOT2";
	
	//YJK 압연작업지시 대상재 조회 
	//A후판압연작업지시 로트편성 - 임춘수 추가 2009.06.18
	//A후판가열로보급LOT편성 - 적치중 또는 권상대기 인 재료 조회
	//설비(보급PICKUP베드 등)베드상의 대상재 제외, 기존 작업예약에 등록된 대상재 제외
	private String szQueryIdGet93   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCHRSUPPLYLOT3";
	
	// 후판슬라브야드에서  가열로장입LOT번호, 생산통제Lot스케줄일련번호 값
	private String szQueryIdGet162 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMM_MillPlntGpBySTLNo";
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////
	
	//적치열,베드에 적치된 해당야드목표행선을 가진 대상재를 조회하는 쿼리
	private String szQueryIdGet94   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockByStkColBedAimRt";
	
	// 김종건 20090626 - 제품상세 정보조회
	private String szQueryIdGet95   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdProdDtlInfo_PIDEV";
	
	// 심명순 20090701 - 설계입고예정위치 heat_no코드 조회 (C연주슬라브)
	private String szQueryIdGet96   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdHeatCodeSearch";
	
	// 심명순 20090701 - 설계입고예정위치 machine코드 조회 (C연주슬라브)
	private String szQueryIdGet97   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdMacCodeSearch";
	
	// 심명순 20090701 - 설계입고예정위치 sch_cd코드 조회 (C연주슬라브)
	private String szQueryIdGet98   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdSchCodeSearch";
	
	// 심명순 20090701 - 설계입고예정위치  조회 (C연주슬라브)
	private String szQueryIdGet99   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabYdEqpInEstiLoc";
	
	// 심명순 20090701 - 설계입고예정위치 CRN_NAME 조회 (C연주슬라브)
	private String szQueryIdGet100   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdCrnSearch";
	// 심명순 20090701 - 설계입고예정위치 스케줄위치검색 조회 (C연주슬라브)
	private String szQueryIdGet101   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdSchLocSrc";

	// 심명순 20090701 - 설계입고예정위치 적치위치 검색 조회 (C연주슬라브)
	private String szQueryIdGet102   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStkLocSearch";

	// 권오창 20090707
	private String szQueryIdGet103   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPmBSlabRprGpSlabMatchCcslabRprGp";
	// 권오창 20090707
	private String szQueryIdGet104   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getQmMslabQltyInfoRprMtds";
	
	// 사용안함.
	private String szQueryIdGet105   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCTHRMILLWOAndStkLyr_PAGE";
	
	//김종건  20090715 - 후판제품창고 차량별 작업상세 관리
	private String szQueryIdGet106   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdCarDtlWorkMgt_PIDEV";
	
	//C연주소재이송상차LOT편성 - 임춘수 추가 2009.07.16
	private String szQueryIdGet107 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLot5";
	
	
	// 후판제품야드 입고 대상제 - 이현성  추가 2009.07.23
	private String szQueryIdGet108 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdRcptPlnMtl_PIDEV";
	
	// 차량작업관리화면 작업재료 - 심명순  추가 2009.07.23
	private String szQueryIdGet109 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilYdGdsCarWork";
	
	// 작업예약 존재 여부 체크
	private String szQueryIdGet110 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockWrkbook";
	
	// 적치단정보와 JOIN
	private String szQueryIdGet111 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockJoinStkLyr";
	
	// 외판슬라브LOT 대상재 검색
	private String szQueryIdGet112 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockOutplDistCarLdlot";
	
	//김종건 20090813 - Piling 정보변경 및 입고처리 행 추가시 제품번호 정보 검색
	private String szQueryIdGet113 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdPilingStockData_PIDEV";
	
	// 운송지시번호로 제품번호 가져 오기 
	private String szQueryIdGet114 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateSeqNo";
		
	//직상차용 설비에서 이송대상재LOT편성 쿼리
	private String szQueryIdGet115 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCarDirectLoadLot2";
	//야드에서 이송대상재LOT편성 쿼리
	private String szQueryIdGet116 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLot6";
																							
	// 차량출발대상정보 가져오기 
	private String szQueryIdGet117 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateSeqNo2_PIDEV";
	
	// A후판 슬라브야드 - 재료상세정보조회  
	private String szQueryIdGet118 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockAPlateStlNo";
	
	// 권오창 (20090820)
	private String szQueryIdGet119 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransDTNOCarCardNo";
	
	// 이현성  (20090824) - 코일소재 CONV 대상 재료 검색
	private String szQueryIdGet120 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getDivConvTargetMtl_Popup";

	// 권오창
	private String szQueryIdGet121 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedNotStl_PIDEV";
	
	//이송대상재 조회 쿼리 - 임춘수 2009.08.27 : 이송테이블, 적치단, 저장품 조인 - 페이징쿼리
	private String szQueryIdGet122 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockStlFrtoMovePage";
	//이송대상재 조회 쿼리 - 임춘수 2009.08.27 : 이송테이블, 적치단, 저장품 조인
	private String szQueryIdGet123 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockStlFrtoMove";
	
	//이송재료 LIST 
	private String szQueryIdGet124 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabTotYdTransMtlList";
	
	
	//이송대상재 조회 쿼리 - 임춘수 :이송테이블, 저장품 조인 - 야드에서 관리되지 않는 조업에서 대상재 조회 시 사용되는 쿼리
	private String szQueryIdGet125 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockStlFrtoMovePage2";
	private String szQueryIdGet126 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockStlFrtoMove2";

	
	
	

	//출하차량상차LOT 조회 쿼리 - 임춘수 : 파라미터 - 카드번호, 운송지시일자, 운송지시순번, 적치열구분[옵션] ->열 Desc
	private String szQueryIdGet127 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV";
	//출하차량상차LOT 조회 쿼리 - 임춘수 : 차량번호, 카드번호, 운송지시일자, 운송지시순번 그룹핑, 파라미터-야드구분
	private String szQueryIdGet128 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrTransDTSeqCardNoGroup";
	//출하차량상차LOT 조회 쿼리 - 임춘수 : 파라미터 - 카드번호, 운송지시일자, 운송지시순번, 적치열구분[옵션] ->열 Asc
	private String szQueryIdGet129 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNoAsc_PIDEV";
	//김종건 - 입고예정 BED 현황 조회
	private String szQueryIdGet130 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockRcptPlnBedStaus";
	
	private String szQueryIdGet131 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStock_route_gp";

	// 권오창 20090915 - TB_PT_SLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회 
	private String szQueryIdGet132 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabcomYdStockYD_STKLYRToCodeMapping";

	// 이현성 20090916 - 재료상세정보 조회(슬라브) 
	private String szQueryIdGet133 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabYdStrlocIdInfojl";
	
	// 김종건  20090918 - 이적작업 진행관리 제품 조회  
	private String szQueryIdGet134 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockForPlateRmvProcMgt_PIDEV";

	// 권오창 20090922 - TB_PT_MSLABCOMM, TB_YD_STOCK, TB_YD_STKLYR 공통 조회 
	private String szQueryIdGet135 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMSLABCOMMYdStockYD_STKLYRToCodeMapping";
	// 김종건 20090924 - OSCOMM KARTPA DATA 조회
	private String szQueryIdGet136 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockForPlatePilingStlNo";
	// 김종건  20090928 - 후판제품야드 입고예정 모니터링 BY 정렬 
	private String szQueryIdGet137 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdRcptPlnMonitorBySort_PIDEV";
	//이송지시된 이송재료 LIST 페이징 - 임춘수 2009.09.28
	private String szQueryIdGet138 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdTransMtlListPage";
	//이송재료 LOT편성 쿼리 - 임춘수 2009.09.28
	private String szQueryIdGet139 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdTransMtlList";
	// 김종건  20090929 - 후판제품야드 선별 대상제품 처리 BY SORT
	private String szQueryIdGet140 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdSortWrkPdtProcBySort_PIDEV";
	// 차량이송 준비스케줄 조회 쿼리 - 준비스케줄과저장품 조인 : 임춘수 2009.09.29 - 야드구분만 조건으로 사용
	private String szQueryIdGet141 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockNPrepSch";
	// 가장 빠른 차량이송 준비스케줄 조회 쿼리 - 준비스케줄과저장품 조인 : 임춘수 2009.10.02 - 야드구분[YD_GP], 스케쥴코드[YD_SCH_CD], 준비작업상태[YD_PREP_WK_ST]
	private String szQueryIdGet142 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockNPrepSchByYd";
	//이송완료된 이송재료 LIST 페이징 - 임춘수 2009.10.05
	private String szQueryIdGet143 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdTransMtlListPageForMoveCmpl_PIDEV";
	
	//크레인별 가장 빠른 준비스케줄 조회
	private String szQueryIdGet144 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockNPrepSchByYdCrn";	
	//이송재료에 있는 저장품 재료 정보 조회 쿼리 - 이현성 2009.10.13
	private String szQueryIdGet145 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getStlInfobyMoveStl";
	//이송LOT 개수에 해당하는 가장 빠른 준비스케줄을 조회하는 쿼리 - 임춘수 2009.10.16
	private String szQueryIdGet146 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockNPrepSchByYdLotCnt";
	// 권오창 2009.10.20 - 이송지시(PMYDJ002) 처리 시 주편공통(레코드 상태 1, 2)읽는 쿼리 (N건)
	private String szQueryIdGet147 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_MSLAB";
	// 권오창 2009.10.20 - 이송지시 (PMYDJ002) 처리 시 재료번호로 슬라브공통 읽는 쿼리 (1건) 
	private String szQueryIdGet148 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATEBySTLNo";
	// 윤재광 2011.03.25 - 이송지시(PMYDJ002) 이송지시일자/순번/상태로 읽는 쿼리 (N건)
	private String szQueryIdGet220 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVELIST";
	// 윤재광 2011.03.25 - 충당지시(PMYDJ001) 충당지시일자/순번/상태로 읽는 쿼리 (N건)
	private String szQueryIdGet221 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockORDERTRANSLIST";
	
	private String szQueryIdGet222 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdStrPosAreaStatsLength_PIDEV";
	private String szQueryIdGet223 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdStrPosAreaStatsLengthDtl_PIDEV";
	private String szQueryIdGet224 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdStrPosAreaStatsWidth_PIDEV";
	private String szQueryIdGet225 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdStrPosAreaStatsWidthDtl_PIDEV";
	private String szQueryIdGet226 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdStrPosAreaStatsDtl_PIDEV";
		
	
	// 김종건 20091020 - 후판제품야드 Marking 대상 List 정보 조회
	private String szQueryIdGet149 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockMarkingPdList_PIDEV";
	// 권오창 2009.10.21 - 슬라브충당실적 (PMYDJ001) 처리 시 주편테이블 조회
	private String szQueryIdGet150 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMatchWrMSlab";
	// 권오창 2009.10.21 - 슬라브충당실적 (PMYDJ001) 처리 시 슬라브테이블 조회
	private String szQueryIdGet151 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMatchWrSlabStlNo";
	//크레인별 가장 빠른 준비스케줄 조회 - 차량구분 조건에 포함
	private String szQueryIdGet152 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockNPrepSchByYdCrnCarGp";
	//이송지시 또는 이송LOT편성된 이송재료 LIST 페이징 - 임춘수 2009.10.21 : 조건 - 제조사 추가
	private String szQueryIdGet153 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdTransOrdOrPrepMtlListPage";
	//이송지시된 이송재료 LIST 페이징 - 임춘수 2009.10.21 : 조건 - 제조사 추가
	private String szQueryIdGet154 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMoveOrdMtlListPage_PIDEV";
	//이송지시된 이송재료 LIST (긴급재) 페이징 - 신지은 2017.03.06
	private String szQueryIdGet227 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdUrgFrtoMoveOrdMtlListPage_PIDEV";
	//후판제품창고 입고시 동별 분산로직 수행 - 윤재광 2020.12.06
	private String szQueryIdGet228 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getBayGpByStlNo";
	//저장위치 수정 : 허용된 사용자(업무기준) 확인 - 추관식 2025.11.20
	private String szQueryIdGet229 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPtPlateCommLocUser";
	//이송지시되고 준비스케줄 편성된 이송재료 LIST 페이징 - 임춘수 2009.10.21 : 조건 - 제조사 추가
	private String szQueryIdGet155 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMovePrepMtlListPage_PIDEV";
	// 김종건 20091021 - 후판제품야드 이송재료 List 정보 조회(지시)
	private String szQueryIdGet156 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMoveMtlListByDire";
	// 김종건 20091021 - 후판제품야드 이송재료 List 정보 조회(완료)
	private String szQueryIdGet157 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMoveMtlListByComp";
	//재료 저장품및 공통에 있는 정보가 있는지 확인하기 위함 - 이현성 2009.10.23 
	private String szQueryIdGet158 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCheckStlNo";
	//이송지시된 이송재료 LIST - 임춘수 2009.10.21 : 조건 - 제조사 추가
	private String szQueryIdGet159 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdFrtoMoveOrdMtlList";
	// 권오창 2009.10.30 - 이송지시 (PMYDJ002) 취소 처리 시 주편공통 읽는 쿼리 (N건)
	private String szQueryIdGet160 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_MSLAB_CANCEL";
	// 권오창 2009.10.30 - 이송지시 (PMYDJ002) 취소 처리 시 재료번호로 슬라브공통 읽는 쿼리 (1건) 
	private String szQueryIdGet161 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATEBySTLNo_CANCEL";
	
	// 이현성 2009.11.10 - 슬라브 상세정보조회(슬라브공통의 MSLAB_NO로 조회)
	private String szQueryIdGet163 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABCOMMbyMSLABNO";
	
	//스카핑대상재조회  페이징 쿼리 - 보급LOT편성제외, 작업예약제외 - 임춘수 2009.11.10
	private String szQueryIdGet164 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getScarfingMtlListPage";
	//정정대상재조회 페이징 쿼리 - 보급LOT편성제외, 작업예약제외 - 임춘수 2009.11.10
	private String szQueryIdGet165 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getShearMtlListPage";
	//보급LOT편성된 스카핑대상재조회  쿼리 - 임춘수 2009.11.10
	private String szQueryIdGet166 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getScarfingSupLotMtlListPage";
	//보급LOT편성된 정정대상재조회 쿼리  - 임춘수 2009.11.10
	private String szQueryIdGet167 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getShearSupLotMtlListPage";
	//스카핑대상재조회 - 보급LOT편성  쿼리 - 보급LOT편성제외, 작업예약제외 - 임춘수 2009.11.10
	private String szQueryIdGet168 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getScarfingMtlList";
	//정정대상재조회 - 보급LOT편성  쿼리 - 보급LOT편성제외, 작업예약제외 - 임춘수 2009.11.10
	private String szQueryIdGet169 = "com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getShearMtlList";
	
	//준비스케줄에서 대상재 추출 쿼리 : 적치열 DESC, 적치베드 DESC, 적치단 DESC - 임춘수 2009.11.11
	private String szQueryIdGet170 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFromEarliestPrepSchDesc";
	
	//입고예정 예정위치 등록 쿼리 : 입고계획도 추가한 버전 석창화 2009.11.13
	private String szQueryIdGet171 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockRcptPlnBedStaus_OSCOMM";

	// 권오창 2009.11.16 - 날판번호 조회 
	private String szQueryIdGet172 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSizeSLABCOMMPlPmpNo";

	// 권오창 2009.11.17 - A후판 제품생산실적 (PRYDJ004) 조회 (ORD_GP와 DEST_CD가 없어서 OSCOMM과 조인)
	private String szQueryIdGet173 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMMOSCOMM";
	
	// 후판제품 차량 상세 내역 조회 PDA 화면용
	private String szQueryIdGet174 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECarNoSTLNO";

	// PLATECOMM 조회 - SLAB_NO
	private String szQueryIdGet175 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECOMMBySlabNo";

	// 권오창 2009.11.27 - 이송지시(PMYDJ002) 처리 시 슬라브(레코드 상태 3, '')읽는 쿼리 (N건)	
	private String szQueryIdGet176 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_SLAB";

	// 권오창 2009.12.04 - 주편에 레코드상태가 3인 재료에 대해서 슬라브테이블의 지시행선(SLAB_WO_RT_CD)이 'PA','PB' 이고 재열재구분(REHEAT_SLAB_GP)이 '1','2'
	private String szQueryIdGet177 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABNObyRTCDREHEAT";
	
	//이현성 2009.12.18 - LINE OFF 대상 재료 검색
	private String szQueryIdGet178 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getDivConvTargetMtl_NextProc";

	// 권오창 2009.12.23 - COIL_NO로 HR_열연정정지시실적테이블의 작업상태를 조회
	private String szQueryIdGet179 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getHRWorkStatByCoilNo";

	// 권오창 2009.12.31 - 저장품제원 동별 DEL_YN 비체크
	private String szQueryIdGet180 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedNotStlDelYNNoCheck";
	
	// 권오창 2009.12.31 - 저장품제원 지정저장품 DEL_YN 비체크
	private String szQueryIdGet181 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStlDelYNNoCheck_PIDEV";
	
	// 석창화 2010.01.11 - Depiler보급현황 조회 신규로 만듬(70번대체)
	private String szQueryIdGet182 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCtNPlrefurwo_aPlate";
	
	// 후판제품 차량 상세 내역 조회 PDA 화면용
	private String szQueryIdGet183 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLATECarNoInspectSTLNO";

	//베드에 권하대기이거나 적치중인 재료 조회 - 적치열 ASC, 적치베드 ASC, 적치단 DESC : 임춘수 2010.01.15
	private String szQueryIdGet184 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockInDnWaitOrStkAtBed_PIDEV";

	// 2010.01.25 권오창 - 주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 주편공통)   - V_MSLAB_NO 
	private String szQueryIdGet185 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockMslabCommBySTLNo";

	// 2010.01.25 권오창 - 주편공통, 저장품, 슬라브공통 테이블에서 공통된 항목만 조회 (저장품 + 슬라브공통) - V_SLAB_NO
	private String szQueryIdGet186 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCommonFieldYdStockSlabCommBySTLNo";
	
	// 야드에 적치된 압연작업대상정보 검색 - V_SLAB_WO_RT_CD, V_YD_AIM_RT_GP
	private String szQueryIdGet213 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdRefurStockListByCTYDJ03";
	
	// 슬라브 자동준비작업 LOT편성
	private String szQueryIdGet215 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockListByAutoLotAsc";
	private String szQueryIdGet216 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockListByAutoLotDesc";
	
	//------------------------------------------------------------------------------------
	//	스카핑/정정보급 재료 LIST 화면용 쿼리 - 2010.01.26
	//------------------------------------------------------------------------------------
	
	//스카핑대상재조회  페이징 쿼리 ver2 - 보급LOT편성제외, 작업예약제외
	private String szQueryIdGet187 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getScarfingMtlListPage2";
	//정정대상재조회 페이징 쿼리 ver2 - 보급LOT편성제외, 작업예약제외
	private String szQueryIdGet188 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getShearMtlListPage2";
	//보급LOT편성된 스카핑대상재조회  쿼리
	private String szQueryIdGet189 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getScarfingSupLotMtlListPage2";
	//보급LOT편성된 정정대상재조회 쿼리
	private String szQueryIdGet190 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getShearSupLotMtlListPage2";
	//스카핑대상재조회 - 보급LOT편성  쿼리 - 보급LOT편성제외, 작업예약제외
	private String szQueryIdGet191 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getScarfingMtlList2";
	//정정대상재조회 - 보급LOT편성  쿼리 - 보급LOT편성제외, 작업예약제외
	private String szQueryIdGet192 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getShearMtlList2";
	
	//------------------------------------------------------------------------------------
	
	// 코일제품 차량 상세 내역 조회 PDA 화면용
	private String szQueryIdGet193 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilCarNoSTLNO";
	
	//적치열,베드에 적치된 해당야드목표행선을 가진 대상재를 조회하는 쿼리 - 이송대상재, 착지개소코드 : 임춘수 2010.02.05
	private String szQueryIdGet194   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockByStkColBedAimRtForFrtoMove";
	
	// CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 조회 : 이영근 2010.02.23
	private String szQueryIdGet195   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTBCTNPLRTNGLAYOUTWO";
	
	
	// 석창화 2010.02.25 - 이송지시(PMYDJ002) 처리 시 이송지시읽는 쿼리 (N건)
	private String szQueryIdGet196  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTB_PT_STLFRTOMOVE";
	//C열연가열로보급 대상재(C열연작업대기) 조회 - 가열로장입LOT번호 ASC순 : 임춘수 2010.03.03
	private String szQueryIdGet197  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getHrMillWoByChgLotAsc";
	//재료번호로 생산통제 CT_열연압연작업지시 조회 - 임춘수 2010.03.03
	private String szQueryIdGet198  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getHrMillWo";
	//1M/C Pickup Bed, 1M/C Pile Bed, 2M/C Pickup Bed설비에 B열연 HCR작업대기인 대상재가 존재하는 지 조회 - 임춘수 2010.03.03
	private String szQueryIdGet199  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockForHB";
	
	//후판날판번호(PL_MTL_NO) + 후판L2제품번호(szPL_L2_TRK_NO : 2010030401060001 - 13번째부터 4 Byte)로 후판번호를 추출 - 이영근 2010.03.05
	private String szQueryIdGet200  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getTBPRPLATEMAT";
	
	
	
	
	//코일 소재 이송재료 LIST (지시)
	private String szQueryIdGet201 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilYdFrtoMoveOrdMtlListPage";		        

	//코일 소재 이송재료 LIST (완료)
	private String szQueryIdGet202 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilYdTransMtlListPageForMoveCmpl";
	
	//코일 소재 이송재료 LIST (LOT 편성)
	private String szQueryIdGet203 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilYdFrtoMovePrepMtlListPage"; 

	
	
	
	// 재료번호로 가열로장입LOT번호와 가열로장립LOT순번 조회(슬라브)
	private String szQueryIdGet204  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getHrMillWoBySTLNo";

	// 재료번호로 가열로장입LOT번호와 가열로장립LOT순번 조회
	private String szQueryIdGet205  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYMStockbyStockID";
	
	// 재료번호로 가열로장입LOT번호와 가열로장립LOT순번 조회(후판)
	private String szQueryIdGet206  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPLREFURWOBySTLNo";
	
	private String szQueryIdGet207  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getslabFrtomoveListBySTLNo";
	
	private String szQueryIdGet208  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_MSLAB_STL";
	
	private String szQueryIdGet209  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_SLAB_STL";
	
	private String szQueryIdGet210  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_MSLAB_CANCEL_STL";
	
	private String szQueryIdGet211  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMatchWrMSlab_STL";

	private String szQueryIdGet212  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMatchWrSlab_STL";
	
	private String szQueryIdGet214  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getOsPilingCdByStlNo_PIDEV";
	
	private String szQueryIdGet219  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.get56ZoneLevStlList_PIDEV";
	
	private String szQueryIdGet301  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.get58ZoneLevStlList_PIDEV";
	
	private String szQueryIdGet302  = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getStockTbCtMPlmplspec";
	
	
	//이송지시 대상 저장품존재 유무 체크
	private String szQueryIdGet300   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_COILCHK";

	//
	private String szQueryIdGet400   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedNotStlCoil_PIDEV";
	//
	private String szQueryIdGet401   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStlDelYNNoCheckCoil_PIDEV";
	//
	private String szQueryIdGet402   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrYdStkBedStlCoil_PIDEV";
	
	private String szQueryIdGet403   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkHisttoPort";
	private String szQueryIdGet404   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCoilStlFrtoMovePage";
	private String szQueryIdGet405   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockCoilStlFrtoMove";
	private String szQueryIdGet500   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSpanUnitMvstkReg_PIDEV";
	
	
	private String szQueryIdGet501   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.rcvpPlateYdGascutresult";
	
	private String szQueryIdGet502   = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.rcvpPlateYdGascutresult1";

	private String szQueryIdGet503 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockJoinStkLyr2";
	
	private String szQueryIdGet504 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNo2";
	
	
	private String szQueryIdGet505 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLotC";
	
	private String szQueryIdGet506 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLotC2";

	//코일제품 이송재료 LIST (지시)
	private String szQueryIdGet600 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilGdsYdFrtoMoveOrdMtlListPage";		        

	//코일제품 이송재료 LIST (완료)
	private String szQueryIdGet601 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilGdsYdTransMtlListPageForMoveCmpl";
	
	//코일제품 이송재료 LIST (LOT 편성)
	private String szQueryIdGet602 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCoilGdsYdFrtoMovePrepMtlListPage"; 

	private String szQueryIdGet603 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrTransDTSeqCardNoGroupS_PIDEV"; 
	
	private String szQueryIdGet604 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMtlCTransDTSeqCardNoDesc_PIDEV";
	
	private String szQueryIdGet605 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrDmCarPointNoGroupS"; 
	
	private String szQueryIdGet606 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdOrdWgtCheck"; 
	
//
	private String szQueryIdGet607 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCtPlatspecOrdno";
	private String szQueryIdGet608 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getStklyrWithSamePilingCd";
	private String szQueryIdGet609 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getStklyrWithOrdLocCnt";
	private String szQueryIdGet610 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdPilingNotCnt";
		
	private String szQueryIdGet611 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdPilingDataChngNew";
	//선별용	
	private String szQueryIdGet612 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdSelWrk";
	//선별용	
	private String szQueryIdGet613 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdSelWrkDtl";
	//선별용	
	private String szQueryIdGet614 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdSelWrkList";
	//가적BED
	private String szQueryIdGet615 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getPlateYdTempLocReg";
	
	private String szQueryIdGet618 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getDemenderNoGroup_PIDEV"; 
	
	private String szQueryIdGet619 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.YdStkLyrMtlCTransDTSeqCardNoDesc2_PIDEV"; 
	
	// 후판제품 출하Lot대상재 상차동 검색(지시번호기준)
	private String szQueryIdGet701 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockPlateLotDong"; 
	// 후판제품 출하Lot대상재 검색(포인트기준)
	private String szQueryIdGet702 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockPlateLotPoint"; 
	// 후판제품 출하Lot대상재  갯수 검색(야드적치기준)
	private String szQueryIdGet703 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockPlateLotCnt_PIDEV"; 
	
	private String szQueryIdGet723 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getDemenderNoGroup2_PIDEV";
	
	private String szQueryIdGet724 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYmStockTransOrdDateSeqNo";
	
	private String szQueryIdGet725 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYmStockWbookcheck";
	
	private String szQueryIdGet726 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockWbookcheck";
 
	private String szQueryIdGet727 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDTWbook";
	
	private String szQueryIdGet728 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCarInfo_PIDEV";
	
	//크레인별 가장 빠른 준비스케줄 조회 - 차량구분 조건에 포함
	private String szQueryIdGet729 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockNPrepSchByYdCrnCarGpC";
	
	private String szQueryIdGet730 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDate";
	
	private String szQueryIdGet731 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB";
	
	private String szQueryIdGet732 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getMarkingDemenderNo_PIDEV";
	
	private String szQueryIdGet733 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getCondenMvstkReg_PIDEV";
	
	//기타
	private String szQueryIdGet801 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getQslabFrtoMoveMtl";
	private String szQueryIdGet802 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabShipListInfo1";
	private String szQueryIdGet803 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSlabShipListInfo2";
	
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.insYdStock";
	private String szQueryIdIns2 = "com.inisteel.cim.yd.dao.ydstockdao.YmStockDao.insYmStock";
	private String szQueryIdIns3 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.insTBCTNPLRTNGLAYOUTWO";

	
	
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStock";
	
	//update query id(운송지시로)
	private String szQueryIdUpd13 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStock2";

	//공통 테이블 진도코드 업데이트
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtSlabcommPROGCD";
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommPROGCD";
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtMslabcommPROGCD";
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtCoilcommPROGCD";
	private String szQueryIdUpd302 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtCoilcommPROGCD2";
	
	//공통 테이블 저장위치 업데이트
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtSlabcommLOC";
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommLOC";
	private String szQueryIdUpd8 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtMslabcommLOC";
	private String szQueryIdUpd9 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtCoilcommLOC";
	private String szQueryIdUpd500 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtCoilcommSUBLOC";
	private String szQueryIdUpd300 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtCoilcommOutLOC";
	private String szQueryIdUpd301 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtPlatecommOutLOC";
	
	//공통테이블 항목 수정 업데이트 
	private String szQueryIdUpd10 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtSlabcommFix";
	private String szQueryIdUpd11 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtMSlabcommFix";
	
	// 김종건  20090528 - 후판제품야드 저장 Group 편성 스케줄 통합 스케줄 업데이트
	private String szQueryIdUpd12 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdSvGpSchFm";

	// 석창화  20090929 - TB_PT_STLFRTOMOVE 테이블에 해당하는 재료번호와 이송지시차수의 이송상태코드를 업데이트 
	private String szQueryIdUpd14 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPtStlFrtMoveByStlNoTransSeq";
	
	// 석창화  20091125 - STOCK에  Piling_CD, Bookout_cd, 입고예정위치를 업데이트 
	private String szQueryIdUpd15 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockPilingCd";

	// 권오창 20091210 - PLATE공통에 야드BookOut위치, 입고예정위치를 업데이트
	private String szQueryIdUpd16 	= "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPLATECOMMYdBookOutLoc";
	private String szQueryIdUpd16_1 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updStockYdBookOutLoc";
	
	// 석창화 20091216 - STOCK에에 야드BookOut위치, 입고예정위치를 업데이트 (주문번호, 행번, Piling_cd)
	private String szQueryIdUpd17 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockYdBookOutLoc";

	// 권오창 20091216 - 주편공통에 야드구분 업데이트
	private String szQueryIdUpd18 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updMslabCommYdGp";
	
	// 권오창 20091216 - 슬라브공통에 야드구분 업데이트
	private String szQueryIdUpd19 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updSlabCommYdGp";
	
	// 석창화 20091222 - STOCK에에 야드BookOut위치, 입고예정위치를 업데이트 (Piling_cd)-여재
	private String szQueryIdUpd20 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockYdBookOutLoc_Yeojae";
	
	// 임춘수2009.12.28 - 저장품의 작업예약ID와 스케줄코드를 삭제하는 쿼리
	private String szQueryIdUpd21 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDelYdWBookId";
	// 석창화 20091230 - A후판압연지사확정(CTYDJ031) 수신시  산적LOT_TYPE과 LOT_CD등을 일단 초기화 
	private String szQueryIdUpd22 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockByCTYDJ031";
	
	// CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Update : 이영근 2010.02.23
	private String szQueryIdUpd23 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updTBCTNPLRTNGLAYOUTWO";
	// CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO)  Update : 이영근 2010.02.23
	private String szQueryIdUpd24 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updTBCTNPLMILLDIVPLNGDSWO";
	// A후판압연지사확정(CTYDJ031) 수신시  저장품 테이블 장입LOT번호 초기화
	private String szQueryIdUpd26 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockLotNoClearByCTYDJ031_삭제";
	private String szQueryIdUpd29 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockLotNoClearByCTYDJ03";
	private String szQueryIdUpd37 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockLotNoClearByCTYDJ033";
	private String szQueryIdUpd38 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockLotNoClearByCTYDJ033_DEL";
	private String szQueryIdUpd39 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockLotNoClearByCTYDJ031";
	private String szQueryIdUpd40 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockLotNoClearByCTYDJ031_DEL";
	
	// 후판주문외제품 이송지시 등록 및 취소
	private String szQueryIdUpd27 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDmFrRegister";
	private String szQueryIdUpd28 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDmFrCancel";
	
	// 후판제품 입고시간 업데이트
	private String szQueryIdUpd30 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDmTime01";
	private String szQueryIdUpd31 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDmTime02";
	private String szQueryIdUpd32 = "com.inisteel.cim.yd.dao.ymEtcDao.YmEtcDao.updPlateDelayResn";
	private String szQueryIdUpd33 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDmTime04";
	private String szQueryIdUpd41 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDmTime06";
	// 후판공통 이송구분/시간 업데이트
	private String szQueryIdUpd36 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDmTime05";
	
	// 후판제품 목적지코드 변경
	private String szQueryIdUpd34 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockDmDestCd";
	// 후판Plate공통 주여구분 변경
	private String szQueryIdUpd35 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPlateYeajaeGp";
	
	// YM업데이트
	private String szQueryIdUpd25 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYmStock1";
	
	// 후판Plate공통 운송지시 대기
	private String szQueryIdUpd42 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updPlateYdStockDMYDR028";	

	// 후판제품 CAR-LOT-ID 변경
	private String szQueryIdUpd400 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockCarLotId";
	// 후판제품 CAR-LOT-ID 변경
	private String szQueryIdUpd401 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockCoilCarLotId";
	
	private String szQueryIdUpd402 = "com.inisteel.cim.yd.jsp.slabjsp.dao.slabJspDao.updYd_SlabScarfDelyReg";
	
	private String szQueryIdUpd403 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockReg";
	
	private String szQueryIdUpd404 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockEndReg";
	
	private String szQueryIdUpd405 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockTelInfo";
	
	private String szQueryIdUpd406 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updCoilCarPointYnReg";
	
	private String szQueryIdUpd407 = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.updYdStockMessage";
/*------------------------------------- SELECT -------------------------------------------*/
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:STL_NO
	 *                                      1:STL_NO  (JOIN SLABCOMM)
	 *                                      2:SLAB_NO (GET SLABCOMM)
	 *                                      3:STL_NO  (JOIN PLATECOMM)
	 *                                      4:PLATE_NO(GET PLATECOMM)
	 *                                      5:STL_NO  (JOIN MSLABCOMM)
	 *                                      6:MSLAB_NO(GET MSLABCOMM)
	 *                                      7:STL_NO  (JOIN COILCOMM)
	 *                                      8:COIL_NO (GET COILCOMM)
	 *                                      9:PTOP_PLNT_GP (JOIN TB_CT_L_HRMILLWO
	 *                                     10:CT_PLN_WO_MC_NO
	 *                                     11:CHG_WO_FR_PNT,CHG_WO_TO_PNT
	 *                                     12:CHG_WO_FR_PNT,CHG_WO_TO_PNT,CT_PLN_WO_MC_NO,PLNT_PROC_CD
	 *                                     13:PTOP_PLNT_GP
	 *                                     14:YD_MTL_ITEM,YD_AIM_RT_GP,YD_GP,YD_BAY_GP
	 *                                     15:YD_GP,YD_BAY_GP
	 *                                     16:YD_MTL_ITEM,YD_GP,YD_BAY_GP
	 *                                     17:YD_MTL_ITEM,YD_AIM_RT_GP,YD_AIM_YD_GP,YD_AIM_BAY_GP,FRTOMOVE_PLANT_GP,YD_STK_COL_GP(야드,동구분)
	 *                                     18:YD_MTL_ITEM,YD_STK_COL_GP(야드,동구분)
	 *                                     19:PROC_GP,YD_AIM_RT_GP,YD_STK_COL_GP(야드,동구분)
	 *                                     20:PTOP_PLNT_GP,REFUR_CHG_LOT_NO,YD_STK_COL_GP,YD_STK_BED_NO
	 *                                     21:YD_STK_COL_GP,YD_STK_BED_NO
	 *                                     22:PTOP_PLNT_GP,REFUR_CHG_LOT_NO,YD_STK_COL_GP,YD_STK_BED_NO
	 *                                     23:YD_STR_GTR_CD,YD_ROUTE_GP,YD_AIM_RT_GP,YD_STK_LOT_CD
	 *                                     24:PROC_GP,YD_AIM_RT_GP,YD_STK_COL_GP
	 *                                     25:PROC_GP,YD_AIM_RT_GP,YD_STK_COL_GP
	 *                                     26:YD_STK_COL_GP,YD_STK_BED_NO,STL_NO
	 *                                     27:YD_MTL_ITEM,YD_AIM_RT_GP,HCR_GP,YD_STK_COL_GP
	 *                                     28:YD_MTL_ITEM,YD_AIM_RT_GP,HCR_GP,YD_STK_COL_GP
	 *                                     29:YD_MTL_ITEM,YD_AIM_RT_GP,HCR_GP,YD_STK_COL_GP
	 *                                     30:YD_MTL_ITEM,YD_AIM_RT_GP,YD_STK_COL_GP
	 *                                     31:TRANS_ORD_DATE,TRANS_ORD_SEQNO
	 *                                     32:FRTOMOVE_WORD_DATE
	 *                                     33:YD_GP, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     34:PTOP_PLNT_GP, WORD_PROC, SHEAR_WORD_DT
	 *                                     35:MSLAB_NO
	 *                                     36:SLAB_NO
	 *                                     37:TRANS_ORD_DATE, TRANS_ORD_SEQNO, CARD_NO
	 *                                     38:CARD_NO,TRANS_ORD_DATE,TRANS_ORD_SEQNO,YD_STK_COL_GP
	 *                                     39:YD_GP, YD_BAY_GP, YD_STK_COL_GP, YD_MTL_ITEM, YD_AIM_RT_GP,YD_AIM_SCH_CD
	 *                                     40:YD_STK_COL_GP
	 *                                     41:YD_GP,YD_BAY_GP,YD_STK_COL_GP,REFUR_CHG_LOT_NO,ROW_CNT
	 *                                     42:YD_GP,YD_BAY_GP,YD_AIM_RT_GP,ROW_CNT
	 *                                     43:YD_GP,YD_BAY_GP,YD_AIM_RT_GP,ROW_CNT)
	 *                                     42:YD_GP,YD_BAY_GP,YD_AIM_RT_GP,ROW_CNT 
	 *                                     43:SLAB_NO
	 *                                     44:MSLAB_NO
	 *                                     45:PLATE_NO
	 *                                     46:COIL_NO 
	 *                                     48:YD_STK_COL_GP(야드,동,스판), YD_AIM_RT_GP
	 *                                     49:YD_STK_COL_GP, YD_STK_BED_NO, YD_STK_LYR_NO
	 *                                     50:YD_GP, YD_AIM_RT_GP
	 *                                     51:YD_GP, YD_BAY_GP, YD_AIM_RT_GP
	 *                                     52:YD_GP, YD_BAY_GP, YD_AIM_RT_GP
	 *                                     53:STL_NO  (JOIN SLABCOMM)
	 *                                     54:직상차용 대상재 검색
	 *                                     55:STL_NO, YD_AIM_BAY_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     56:
	 *                                     62:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     63:STL_NO
	 *                                     64:STL_NO
	 *                                     65:STL_NO
	 *                                     66:STL_NO
	 *                                     67:YD_STK_COL_GP(야드,동,스판), YD_AIM_RT_GP
	 *                                     70:YD_CRN_SCH_ID
	 *                                     71:YD_GP,....
	 *                                     74:YD_STK_COL_GP(야드,동,스판), YD_AIM_RT_GP 
	 *                                     75:YD_STRCHAR_GRP_CD, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, YD_SHIP_ORD_NO, YD_SHIP_ORD_SEQ_MIN, YD_SHIP_ORD_SEQ_MAX, YD_AIM_RT, YD_WRK_HDS_FM, YD_WRK_HDS_TO, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     76:PTOP_PLNT_GP, CT_MILL_SPEC_WRK_STAT_GP, PRPL_MILL_WO_DT
	 *                                     77:
	 *                                     78:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     79,80:OCCUR_DDTT
	 *                                     81:SEARCH_GBN, PRINT_COND, YD_FROM_DD, YD_TO_DD, PRINT_WEAL, PRINT_UNIT, CUST_CD, DEST_CD
	 *                                     87:PTOP_PLNT_GP 
	 *                                     88:ORD_NO ,ORD_DTL
	 *                                     89:YD_PILING_CD
	 *                                     90:NONE 
	 *                                     91,92:PTOP_PLNT_GP(조업공장구분), YD_GP(야드구분), YD_BAY_GP(동구분)
	 *                                     93:YD_STK_COL_GP(적치열구분), YD_STK_BED_NO(베드), YD_AIM_RT_GP(야드목표행선구분)
	 *                                     94:STL_NO
	 *                                     102:SCARFING_SIGN 
	 *                                     103:MSLAB_NO 
	 *                                     105:YD_GP, CAR_NO, YD_CAR_PROG_STAT, CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, PAGE_CNT1, PAGE_CNT2, ROW_CNT1, ROW_CNT2
	 *                                     106:YD_GP, YD_BAY_GP
	 *                                     107:HEAT_NO
	 *                                     108:
	 *                                     109:
	 *                                     110:STL_NO(JOIN_STKLYR)
	 *                                     111:DEST_TEL_NO
 	 *                                     112:STL_NO
 	 *                                     117:STL_NO
	 *                                     118:TRANS_ORD_DATE, TRANS_ORD_SEQNO, CAR_NO, CARD_NO
	 *                                     119:NEXT_PROC
	 *                                     120:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     121: SPOS_WLOC_CD, YD_AIM_RT_GP, YD_STK_COL_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     122: SPOS_WLOC_CD, YD_AIM_RT_GP, YD_STK_COL_GP
	 *                                     123: WO_STATE,SPOS_WLOC_CD,SPOS_WLOC_CD,DATE_FROM,DATE_TO
	 *                                     124: SPOS_WLOC_CD, YD_AIM_RT_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     125: SPOS_WLOC_CD, YD_AIM_RT_GP
	 *                                     127: YD_ROUTE_GP
	 *                                     126: CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP
	 *                                     127: YD_GP
	 *                                     128: CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP
	 *                                     129: YD_PILING_CD, YD_BAY_GP, YD_EQP_GP, YD_MTL_W_GP, YD_MTL_L_GP
	 *                                     131: STL_NO
	 *                                     132: SLAB_NO
	 *                                     133: YD_SCH_CD, YD_EQP_ID, YD_BAY_GP, YD_EQP_GP, CUST_CD, DEST_CD, TRANS_ORD_DATE, TRANS_ORD_SEQNO_MIN, TRANS_ORD_SEQNO_MAX, PAGE_CNT1, PAGE_CNT2, ROW_CNT1, ROW_CNT2
	 *                                     134: STL_NO
	 *                                     135: PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     136:STL_NO, YD_AIM_BAY_GP, SORT_1, SORT_2, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     137:WO_STATE, DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_GP, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, PAGE_NO, ROW_CNT
	 *                                     138:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME
	 *                                     139:YD_STRCHAR_GRP_CD, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, YD_SHIP_ORD_NO, YD_SHIP_ORD_SEQ_MIN, YD_SHIP_ORD_SEQ_MAX, YD_AIM_RT, YD_WRK_HDS_FM, YD_WRK_HDS_TO, SORT_1, SORT_2, SORT_3, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     140: YD_GP
	 *                                     141:YD_GP, YD_SCH_CD, YD_PREP_WK_ST
	 *                                     142:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_AIM_RT_GP, YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, PAGE_NO, ROW_CNT
	 *                                     143:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, YD_WRK_PLAN_CRN
	 *                                     144:STL_NO
	 *                                     145:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, LOT_MTL_CNT
	 *                                     146:FRTOMOVE_WORD_DATE
	 *                                     147:FRTOMOVE_WORD_DATE, STL_NO
	 *                                     148:YD_BAY_GP, YD_EQP_GP, YD_COL_GP, MK_MOD_EXN, MATCH_ORDERTRANS_GP, MK_MOD_DT, PAGE_CNT, ROW_CNT
	 *                                     149:OCCUR_DDTT
	 *                                     150:OCCUR_DDTT, STL_NO
	 *                                     151:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, YD_WRK_PLAN_CRN, CAR_GP
	 *                                     152:WO_STATE, DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_GP, SEARCH_GP, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, PAGE_NO, ROW_CNT
	 *                                     153:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, YD_GP, YD_PREP_WK_ST, PAGE_NO, ROW_CNT
	 *                                     154:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, YD_GP, YD_PREP_WK_ST, PAGE_NO, ROW_CNT
	 *                                     155:WO_STATE, DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_GP, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, PAGE_NO, ROW_CNT
	 *                                     156:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_AIM_RT_GP, YD_AIM_YD_GP, YD_AIM_BAY_GP, PAGE_NO, ROW_CNT
	 *                                     157:STL_NO
	 *                                     158:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, YD_GP, YD_PREP_WK_ST
	 *                                     159:FRTOMOVE_ORD_CANCEL_DATE
	 *                                     160:FRTOMOVE_ORD_CANCEL_DATE, STL_NO
	 *                                     161:PTOP_PLNT_GP, STL_NO 
	 *                                     162:SLAB_NO
	 *                                     163:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     164:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
	 *                                     165:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     166:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     167:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD
	 *                                     168:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO
	 *                                     169:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, YD_WRK_PLAN_CRN, CAR_GP
	 *                                     170:YD_PILING_CD, YD_BAY_GP, YD_EQP_GP, YD_MTL_W_GP, YD_MTL_L_GP    
	 *                                     171:SLAB_NO                                 
	 *                                     172:PLATE_NO            
	 *                                     174:SLAB_NO               
	 *                                     175:FRTOMOVE_WORD_DATE      
 	 *                                     176:SLAB_NO
 	 *                                     177:NEXT_PROC,COIL_NO 
 	 *                                     178:COIL_NO
	 *                                     179:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     180:STL_NO
	 *                                     183:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     184:V_MSLAB_NO
	 *                                     185:V_SLAB_NO
	 *                                     186:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     187:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
	 *                                     188:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     189:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SCH_SEARCH_GP, YD_SCH_CD, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
	 *                                     190:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD
	 *                                     191:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO
	 *                                     193:SPOS_WLOC_CD, ARR_WLOC_CD, YD_STK_COL_GP(적치열구분), YD_STK_BED_NO(베드), YD_AIM_RT_GP(야드목표행선구분)
	 *                                     194:PL_PLATE_NO
	 *                                     
	 *                                     196:PTOP_PLNT_GP, CT_MILL_SPEC_WRK_STAT_GP, YD_GP, YD_BAY_GP, YD_STK_TC_LOC, 
	 *                                     197:STL_NO
	 *                                     198: Parameter 없음
	 *                                     203:V_STL_NO
	 *                                     204:V_STOCK_ID
	 *                                     )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdStock(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdStock";
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPara_getYdStock(recPara, intGp);
			
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
			else if(intGp == 114)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet115);
			else if(intGp == 115)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet116);
			else if(intGp == 116)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet117);
			else if(intGp == 117)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet118);
			else if(intGp == 118)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet119);
			else if(intGp == 119)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet120);
			else if(intGp == 120)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet121);
			else if(intGp == 121)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet122);
			else if(intGp == 122)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet123);
			else if(intGp == 123)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet124);
			else if(intGp == 123)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet124);
			else if(intGp == 124)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet125);
			else if(intGp == 125)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet126);
			else if(intGp == 126)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet127);
			else if(intGp == 127)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet128);
			else if(intGp == 128)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet129);
			else if(intGp == 129)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet130);
			else if(intGp == 130)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet131);
			else if(intGp == 131)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet132);
			else if(intGp == 132)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet133);
			else if(intGp == 133)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet134);
			else if(intGp == 134)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet135);
			else if(intGp == 135)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet136);
			else if(intGp == 136)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet137);			
			else if(intGp == 137)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet138);
			else if(intGp == 138)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet139);
			else if(intGp == 139)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet140);
			else if(intGp == 140)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet141);
			else if(intGp == 141)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet142);
			else if(intGp == 142)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet143);
			else if(intGp == 143)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet144);
			else if(intGp == 144)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet145);
			else if(intGp == 145)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet146);
			else if(intGp == 146)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet147);
			else if(intGp == 147)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet148);
			else if(intGp == 148)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet149);
			else if(intGp == 149)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet150);
			else if(intGp == 150)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet151);
			else if(intGp == 151)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet152);
			else if(intGp == 152)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet153);
			else if(intGp == 153)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet154);
			else if(intGp == 154)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet155);
			else if(intGp == 155)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet156);
			else if(intGp == 156)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet157);
			else if(intGp == 157)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet158);
			else if(intGp == 158)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet159);
			else if(intGp == 159)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet160);
			else if(intGp == 160)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet161);
			else if(intGp == 161)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet162);
			else if(intGp == 162)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet163);
			else if(intGp == 163)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet164);
			else if(intGp == 164)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet165);
			else if(intGp == 165)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet166);
			else if(intGp == 166)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet167);
			else if(intGp == 167)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet168);
			else if(intGp == 168)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet169);
			else if(intGp == 169)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet170);
			else if(intGp == 170)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet171);
			else if(intGp == 171)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet172);
			else if(intGp == 172)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet173);
			else if(intGp == 173)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet174);			
			else if(intGp == 174)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet175);			
			else if(intGp == 175)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet176);			
			else if(intGp == 176)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet177);			
			else if(intGp == 177)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet178);
			else if(intGp == 178)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet179);
			else if(intGp == 179)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet180);
			else if(intGp == 180)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet181);
			else if(intGp == 181)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet182);
			else if(intGp == 182)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet183);
			else if(intGp == 183)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet184);
			else if(intGp == 184)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet185);
			else if(intGp == 185)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet186);
			else if(intGp == 186)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet187);
			else if(intGp == 187)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet188);
			else if(intGp == 188)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet189);
			else if(intGp == 189)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet190);
			else if(intGp == 190)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet191);
			else if(intGp == 191)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet192);
			else if(intGp == 192)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet193);
			else if(intGp == 193)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet194);
			else if(intGp == 194)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet195);			
			else if(intGp == 195)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet196);
			else if(intGp == 196)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet197);			
			else if(intGp == 197)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet198);
			else if(intGp == 198)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet199);
			else if(intGp == 199)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet200);
			else if(intGp == 200)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet201);
			else if(intGp == 201)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet202);
			else if(intGp == 202)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet203);
			else if(intGp == 203)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet204);
			else if(intGp == 204)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet205);
			else if(intGp == 205)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet206);
			else if(intGp == 206)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet207);
			else if(intGp == 207)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet208);
			else if(intGp == 208)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet209);
			else if(intGp == 209)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet210);
			else if(intGp == 210)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet211);
			else if(intGp == 211)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet212);
			else if(intGp == 213)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet214);
			else if(intGp == 217)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet217);
			else if(intGp == 218)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet218);
			else if(intGp == 220)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet220);
			else if(intGp == 221)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet221);
			else if(intGp == 222)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet222);
			else if(intGp == 223)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet223);
			else if(intGp == 224)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet224);
			else if(intGp == 225)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet225);
			else if(intGp == 226)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet226);
			else if(intGp == 227)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet227);
			else if(intGp == 228)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet228);
			else if(intGp == 229) //신규 추가 : 저장위치 수정 : 허용된 사용자(업무기준) 확인 - 20251120 : 추관식
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet229);
			else if(intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
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
			else if(intGp == 405)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet405);
			else if(intGp == 500)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet500);
			else if(intGp == 501)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet501);
			else if(intGp == 502)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet502);
			else if(intGp == 503)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet503);
			else if(intGp == 504)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet504);			
			else if(intGp == 505)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet505);
			else if(intGp == 506)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet506);
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
			else if(intGp == 618)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet618);
			else if(intGp == 619)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet619);
			else if(intGp == 722)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet722);
			else if(intGp == 723)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet723);
			else if(intGp == 724)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet724);
			else if(intGp == 725)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet725);
			else if(intGp == 726)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet726);
			else if(intGp == 727)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet727);
			else if(intGp == 728)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet728);
			else if(intGp == 729)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet729);
			else if(intGp == 730)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet730);
			else if(intGp == 731)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet731);
			else if(intGp == 732)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet732);
			else if(intGp == 733)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet733);
			else if(intGp == 800)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet800);
			else if(intGp == 801)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet801);
			else if(intGp == 802)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet802);
			else if(intGp == 803)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet803);
			
			
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");
//			
//			// PIDEV
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", commUtils.trim(recPara.getFieldString("JSPEED_QUERY_ID")), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", commUtils.trim(toQuery_ID));

			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getYdStock] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getYdStock]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getYdStock] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getYdStock
		
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec  parameter record
	 *         int intGp                구분(0:STL_NO
	 *                                      1:STL_NO  (JOIN SLABCOMM)
	 *                                      2:SLAB_NO (GET SLABCOMM)
	 *                                      3:STL_NO  (JOIN PLATECOMM)
	 *                                      4:PLATE_NO(GET PLATECOMM)
	 *                                      5:STL_NO  (JOIN MSLABCOMM)
	 *                                      6:MSLAB_NO(GET MSLABCOMM)
	 *                                      7:STL_NO  (JOIN COILCOMM)
	 *                                      8:COIL_NO (GET COILCOMM)  
	 *                                      9:PTOP_PLNT_GP (JOIN TB_CT_L_HRMILLWO)
	 *                                     10:CT_PLN_WO_MC_NO
	 *                                     11:CHG_WO_FR_PNT,CHG_WO_TO_PNT
	 *                                     12:CHG_WO_FR_PNT,CHG_WO_TO_PNT,CT_PLN_WO_MC_NO,PLNT_PROC_CD
	 *                                     13:PTOP_PLNT_GP
	 *                                     14:YD_MTL_ITEM,YD_AIM_RT_GP,YD_GP,YD_BAY_GP
	 *                                     15:YD_GP,YD_BAY_GP
	 *                                     16:YD_MTL_ITEM,YD_GP,YD_BAY_GP
	 *                                     17:YD_MTL_ITEM,YD_AIM_RT_GP,YD_AIM_YD_GP,YD_AIM_BAY_GP,FRTOMOVE_PLANT_GP,YD_STK_COL_GP(야드,동구분)
	 *                                     18:YD_MTL_ITEM,YD_STK_COL_GP(야드,동구분)
	 *                                     19:PROC_GP,YD_AIM_RT_GP,YD_STK_COL_GP(야드,동구분)
	 *                                     20:PTOP_PLNT_GP,REFUR_CHG_LOT_NO,YD_STK_COL_GP,YD_STK_BED_NO
	 *                                     21:YD_STK_COL_GP,YD_STK_BED_NO
	 *                                     22:PTOP_PLNT_GP,REFUR_CHG_LOT_NO,YD_STK_COL_GP,YD_STK_BED_NO
	 *                                     23:YD_STR_GTR_CD,YD_ROUTE_GP,YD_AIM_RT_GP,YD_STK_LOT_CD
	 *                                     24:PROC_GP,YD_AIM_RT_GP,YD_STK_COL_GP
	 *                                     25:PROC_GP,YD_AIM_RT_GP,YD_STK_COL_GP
	 *                                     26:STL_NO
	 *                                     27:YD_MTL_ITEM,YD_AIM_RT_GP,HCR_GP,YD_STK_COL_GP
	 *                                     28:YD_MTL_ITEM,YD_AIM_RT_GP,HCR_GP,YD_STK_COL_GP
	 *                                     29:YD_MTL_ITEM,YD_AIM_RT_GP,HCR_GP,YD_STK_COL_GP
	 *                                     30:YD_MTL_ITEM,YD_AIM_RT_GP,YD_STK_COL_GP
	 *                                     31:TRANS_ORD_DATE,TRANS_ORD_SEQNO
	 *                                     32:FRTOMOVE_WORD_DATE
	 *                                     33:YD_GP, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2   
	 *                                     34:PTOP_PLNT_GP, WORD_PROC, SHEAR_WORD_DT
	 *                                     35:MSLAB_NO
	 *                                     36:SLAB_NO
	 *                                     37:TRANS_ORD_DATE, TRANS_ORD_SEQNO, CARD_NO
	 *                                     38:CARD_NO,TRANS_ORD_DATE,TRANS_ORD_SEQNO,YD_STK_COL_GP
	 *                                     39:YD_GP, YD_BAY_GP, YD_STK_COL_GP, YD_MTL_ITEM, YD_AIM_RT_GP,YD_AIM_SCH_CD
	 *                                     40:YD_STK_COL_GP
	 *                                     41:YD_GP,YD_BAY_GP,YD_STK_COL_GP,REFUR_CHG_LOT_NO,ROW_CNT
	 *                                     42:YD_GP,YD_BAY_GP,YD_AIM_RT_GP,ROW_CNT
	 *                                     43:YD_GP,YD_BAY_GP,YD_AIM_RT_GP,ROW_CNT)                                    
	 *                                     42:YD_GP,YD_BAY_GP,YD_AIM_RT_GP,ROW_CNT
	 *                                     43:SLAB_NO
	 *                                     44:MSLAB_NO
	 *                                     45:PLATE_NO
	 *                                     46:COIL_NO 
	 *                                     48:YD_STK_COL_GP(야드,동,스판), YD_AIM_RT_GP
	 *                                     49:
	 *                                     50:YD_GP, YD_AIM_RT_GP
	 *                                     51:YD_GP, YD_BAY_GP, YD_AIM_RT_GP
	 *                                     52:YD_GP, YD_BAY_GP, YD_AIM_RT_GP
	 *                                     55:V_STL_NO, V_YD_AIM_BAY_GP, V_PAGE_CNT1, V_ROW_CNT1, V_PAGE_CNT2, ROW_CNT2
	 *                                     56:
	 *                                     62:V_YD_STK_COL_GP, V_YD_STK_BED_NO
	 *                                     63:STL_NO
	 *                                     64:STL_NO
	 *                                     65:STL_NO
	 *                                     66:STL_NO
	 *                                     70:YD_CRN_SCH_ID
	 *                                     71:YD_GP
	 *                                     .....
	 *                                     75:YD_STRCHAR_GRP_CD, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, YD_SHIP_ORD_NO, YD_SHIP_ORD_SEQ_MIN, YD_SHIP_ORD_SEQ_MAX, YD_AIM_RT, YD_WRK_HDS_FM, YD_WRK_HDS_TO, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     76:PTOP_PLNT_GP, CT_MILL_SPEC_WRK_STAT_GP, PRPL_MILL_WO_DT
	 *                                     77:STL_NO, YD_STK_COL_NO, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     78:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     79,80:OCCUR_DDTT
	 *                                     81:SEARCH_GBN, PRINT_COND, YD_FROM_DD, YD_TO_DD, PRINT_WEAL, PRINT_UNIT, CUST_CD, DEST_CD
	 *                                     87:PTOP_PLNT_GP
	 *                                     88:ORD_NO ,ORD_DTL
	 *                                     89:YD_PILING_CD
	 *                                     90:NONE
	 *                                     91,92:PTOP_PLNT_GP(조업공장구분), YD_GP(야드구분), YD_BAY_GP(동구분)
	 *                                     93:YD_STK_COL_GP(적치열구분), YD_STK_BED_NO(베드), YD_AIM_RT_GP(야드목표행선구분)
	 *                                     94:STL_NO
 	 *                                     102:SCARFING_SIGN 
	 *                                     103:MSLAB_NO
	 *                                     105:YD_GP, CAR_NO, YD_CAR_PROG_STAT, CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, PAGE_CNT1, PAGE_CNT2, ROW_CNT1, ROW_CNT2
	 *                                     112:STL_NO 
	 *                                     113:TRANS_ORD_DT,TRANS_ORD_SEQNO 
	 *                                     117:STL_NO
	 *                                     118:TRANS_ORD_DATE, TRANS_ORD_SEQNO, CAR_NO, CARD_NO
	 *                                     119:NEXT_PROC
	 *                                     120:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     
	 *                                     
	 *                                     123: WO_STATE,SPOS_WLOC_CD,SPOS_WLOC_CD,DATE_FROM,DATE_TO
	 *                                     124: SPOS_WLOC_CD, YD_AIM_RT_GP, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     125: SPOS_WLOC_CD, YD_AIM_RT_GP
	 *                                     126: CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP
	 *                                     127: YD_GP
	 *                                     128: CARD_NO, TRANS_ORD_DATE, TRANS_ORD_SEQNO, YD_STK_COL_GP
	 *                                     129: YD_PILING_CD, YD_BAY_GP, YD_EQP_GP, YD_MTL_W_GP, YD_MTL_L_GP
	 *                                     131: STL_NO
	 *                                     132: SLAB_NO
	 *                                     133: YD_SCH_CD, YD_EQP_ID, YD_BAY_GP, YD_EQP_GP, CUST_CD, DEST_CD, TRANS_ORD_DATE, TRANS_ORD_SEQNO_MIN, TRANS_ORD_SEQNO_MAX, PAGE_CNT1, PAGE_CNT2, ROW_CNT1, ROW_CNT2
	 *                                     134: STL_NO
	 *                                     135: PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     136: STL_NO, YD_AIM_BAY_GP, SORT_1, SORT_2, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     137:WO_STATE, DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_GP, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, PAGE_NO, ROW_CNT
	 *                                     138:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_AIM_YD_GP, YD_AIM_BAY_GP
	 *                                     139:YD_STRCHAR_GRP_CD, YD_BAY_GP, YD_EQP_GP, YD_STK_COL_NO, YD_SHIP_ORD_NO, YD_SHIP_ORD_SEQ_MIN, YD_SHIP_ORD_SEQ_MAX, YD_AIM_RT, YD_WRK_HDS_FM, YD_WRK_HDS_TO, SORT_1, SORT_2, SORT_3, PAGE_CNT1, ROW_CNT1, PAGE_CNT2, ROW_CNT2
	 *                                     140: YD_GP
	 *                                     141:YD_GP, YD_SCH_CD, YD_PREP_WK_ST
	 *                                     142:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_AIM_RT_GP, YD_AIM_YD_GP, YD_AIM_BAY_GP, PAGE_NO, ROW_CNT
	 *                                     143:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, YD_WRK_PLAN_CRN
	 *                                     144:STL_NO
	 *                                     145:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, LOT_MTL_CNT
	 *                                     146:FRTOMOVE_WORD_DATE
	 *                                     147:FRTOMOVE_WORD_DATE, STL_NO
	 *                                     148:YD_BAY_GP, YD_EQP_GP, YD_COL_GP, MK_MOD_EXN, MATCH_ORDERTRANS_GP, MK_MOD_DT, PAGE_CNT, ROW_CNT
	 *                                     149:OCCUR_DDTT
	 *                                     150:OCCUR_DDTT, STL_NO
	 *                                     151:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, YD_WRK_PLAN_CRN, CAR_GP
	 *                                     152:WO_STATE, DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_GP, SEARCH_GP, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, PAGE_NO, ROW_CNT
	 *                                     153:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, YD_GP, YD_PREP_WK_ST, PAGE_NO, ROW_CNT
	 *                                     154:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, YD_GP, YD_PREP_WK_ST, PAGE_NO, ROW_CNT
	 *                                     155:WO_STATE, DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_GP, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, PAGE_NO, ROW_CNT
	 *                                     156:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_AIM_RT_GP, YD_AIM_YD_GP, YD_AIM_BAY_GP, PAGE_NO, ROW_CNT
	 *                                     157:STL_NO
	 *                                     158:DATE_FROM, DATE_TO, SPOS_WLOC_CD, ARR_WLOC_CD, YD_STK_COL_GP, YD_AIM_RT_GP,YD_AIM_YD_GP, YD_AIM_BAY_GP, MAKER_NAME, YD_GP, YD_PREP_WK_ST
	 *                                     159:FRTOMOVE_ORD_CANCEL_DATE
	 *                                     160:FRTOMOVE_ORD_CANCEL_DATE, STL_NO
	 *                                     161:PTOP_PLNT_GP, STL_NO 
	 *                                     162:SLAB_NO
	 *                                     163:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     164:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
	 *                                     165:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     166:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, YD_SCH_CD, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
	 *                                     167:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD
	 *                                     168:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO
	 *                                     169:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, YD_WRK_PLAN_CRN, CAR_GP
	 *                                     170:YD_PILING_CD, YD_BAY_GP, YD_EQP_GP, YD_MTL_W_GP, YD_MTL_L_GP
	 *                                     171:SLAB_NO
	 *                                     172:PLATE_NO  
	 *                                     173:YD_CAR_USE_GP, SPOS_WLOC_CD, CAR_NO, CAR_NO        
	 *                                     174:SLAB_NO        
	 *                                     175:FRTOMOVE_WORD_DATE   
	 *                                     176:SLAB_NO   
	 *                                     177:NEXT_PROC,COIL_NO
	 *                                     178:COIL_NO
	 *                                     179:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     180:STL_NO
	 *                                     182:YD_CAR_USE_GP,ARR_WLOC_CD
	 *                                     183:YD_STK_COL_GP, YD_STK_BED_NO
	 *                                     184:V_MSLAB_NO
	 *                                     185:V_SLAB_NO
	 *                                     186:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     187:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
	 *                                     188:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
	 *                                     189:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SCH_SEARCH_GP, YD_SCH_CD, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
	 *                                     190:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD
	 *                                     191:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO
	 *                                     192:YD_CAR_USE_GP, CAR_NO, CAR_NO
	 *                                     193:SPOS_WLOC_CD, ARR_WLOC_CD, YD_STK_COL_GP(적치열구분), YD_STK_BED_NO(베드), YD_AIM_RT_GP(야드목표행선구분)
	 *                                     194:PL_PLATE_NO
	 *                                     
	 *                                     196:PTOP_PLNT_GP, CT_MILL_SPEC_WRK_STAT_GP, YD_GP, YD_BAY_GP, YD_STK_TC_LOC, 
	 *                                     197:STL_NO
	 *                                     198:
	 *                                     199:V_PL_MPL_NO,V_PL_DIV_TRIM_GP_CD
	 *                                     203:V_STL_NO
	 *                                     204:V_STOCK_ID
	 *                                     )
	 * @return boolean           true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdStock(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			
			if (intGp == 0 || intGp == 1 || intGp == 3 || intGp == 5 || intGp == 7 || intGp == 60 || intGp == 63 || intGp == 64 || intGp == 65 || intGp == 66 || intGp == 83 || intGp == 110 || intGp == 112
					|| intGp ==117 || intGp == 131 || intGp == 134 || intGp == 144 || intGp == 157 || intGp == 203 || intGp == 205) {
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 2 || intGp == 36 || intGp == 176 || intGp == 185) {
				
				szFieldName = "V_SLAB_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if (intGp == 4 || intGp == 172) {
				
				szFieldName = "V_PLATE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				
			} else if (intGp == 6 || intGp == 103 || intGp == 184) {
				
				szFieldName = "V_MSLAB_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'S', 0, 0);
				
			} else if (intGp == 8 || intGp == 178) {
				
				szFieldName = "V_COIL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				
			} else if (intGp == 9) {
				
				szFieldName = "V_PTOP_PLNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
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
			} else if (intGp == 10) {		
				szFieldName = "V_CT_PLN_WO_MC_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
			} else if (intGp == 11) {			
				szFieldName = "V_CHG_WO_FR_PNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 22, 1, 'L', 0, 0);	
				if (!blnErr) return blnErr;				
				szFieldName = "V_CHG_WO_TO_PNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 22, 1, 'L', 0, 0);					
			} else if (intGp == 12) {		
				szFieldName = "V_CHG_WO_FR_PNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 22, 1, 'L', 0, 0);	
				if (!blnErr) return blnErr;

				szFieldName = "V_CHG_WO_TO_PNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 22, 1, 'L', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CT_PLN_WO_MC_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PLNT_PROC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);	
			} else if (intGp == 13 || intGp == 87) {		
				szFieldName = "V_PTOP_PLNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);	
			} else if (intGp == 14) {		
				
				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 15) {		
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 16) {		
				
				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 17) {		
				
				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_FRTOMOVE_PLANT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if (intGp == 18) {		
				
				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if (intGp == 19) {		
	
				szFieldName = "V_PROC_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);				
				
			} else if (intGp == 20) {		

				szFieldName = "V_PTOP_PLNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_REFUR_CHG_LOT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);				
				
			} else if (intGp == 21) {		

				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);				
				
			} else if (intGp == 22) {		

				szFieldName = "V_PTOP_PLNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_REFUR_CHG_LOT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);				
				
			} else if (intGp == 23) {		

				szFieldName = "V_YD_STR_GTR_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_ROUTE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_LOT_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);				
				
			} else if (intGp == 24) {		

				szFieldName = "V_PROC_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);				
				
			} else if (intGp == 25) {		

				szFieldName = "V_PROC_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);				
				
			} else if (intGp == 26 || intGp == 180) {		

//				szFieldName = "V_YD_STK_COL_GP";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
//				if (!blnErr) return blnErr;
//				
//				szFieldName = "V_YD_STK_BED_NO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				
			} else if (intGp == 27 || intGp == 28 || intGp == 29) {		
				
				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HCR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);				
				
			} else if (intGp == 30) {		

				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);					
			} 
//			else if (intGp == 31) {		
//				szFieldName = "V_TRANS_ORD_DATE";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
//				if (!blnErr) return blnErr;
//				
//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'L', 0, 0);				
//			} 
			else if (intGp == 32 || intGp == 146 || intGp == 175) {		
				szFieldName = "V_FRTOMOVE_WORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);				
			} else if (intGp == 33) {		
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
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
			} else if (intGp == 34) {		
				szFieldName = "V_WORD_UNIT_NAME";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 1, 'S', 0, 0);				
				if (!blnErr) return blnErr;
			} else if (intGp == 35) {		
				szFieldName = "V_MSLAB_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'S', 0, 0);				
			} else if (intGp == 37) {		
				szFieldName = "V_TRANS_ORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'L', 0, 0);				
//				if (!blnErr) return blnErr;
			
				szFieldName = "V_CARD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_BAY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			} else if (intGp == 38) {		
				szFieldName = "V_CARD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRANS_ORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'L', 0, 0);		
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);		
			}else if (intGp == 39) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_ITEM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 40) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 41) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_REFUR_CHG_LOT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 42) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 43) {
				szFieldName = "V_SLAB_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				
			} else if (intGp == 44) {
				szFieldName = "V_MSLAB_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 2, 'S', 0, 0);
				
			} else if (intGp == 45) {
				szFieldName = "V_PLATE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				
			} else if (intGp == 46) {
				szFieldName = "V_COIL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				
			}else if (intGp == 47) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 48) {
				szFieldName = "V_YD_STK_COL_GP";			//야드,동,스판
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 49) {
				szFieldName = "V_YD_STK_COL_GP";			//적치열
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_STK_BED_NO";			//적치BED
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_STK_LYR_NO";			//적치단
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 50) {
				szFieldName = "V_YD_GP";					//야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_AIM_RT_GP";				//목표행선구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 51) {
				szFieldName = "V_YD_GP";					//야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_BAY_GP";				//동구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_AIM_RT_GP";				//목표행선구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 52) {
				szFieldName = "V_YD_GP";					//야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_BAY_GP";				//동구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_YD_AIM_RT_GP";				//목표행선구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 55) {
				szFieldName = "V_SEARCH_1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ORD_YEOJAE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
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
			} else if(intGp == 56){

				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);				
			
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_WRK_DUTY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_AID_WRK_YN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_AID_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				
				szFieldName = "V_STL_PROG_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
								
				
				szFieldName = "V_YD_GNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9,  1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				
			}else if (intGp == 58) {
				szFieldName = "V_YD_EQP_NAME";					//야드설비명
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 50, 1, 'S', 0, 0);				

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
				
			}else if (intGp == 59){
				//!AD szFieldName = "V_YD_COIL_GP";					//조회별 구분
				//!AD blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				//!AD if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
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
				
				szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				//!AD szFieldName = "V_ORD_GP";					
				//!AD blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				//!AD if (!blnErr) return blnErr;
				
				//!AD szFieldName = "V_DESC_CD";					
				//!AD blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				//!AD if (!blnErr) return blnErr;
				
				//!AD szFieldName = "V_DEMANDER_CD";					
				//!AD blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				//!AD if (!blnErr) return blnErr;
				
				//!AD szFieldName = "V_ORD";					
				//!AD blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				//!AD if (!blnErr) return blnErr;
				
				//!AD szFieldName = "V_PROG_CD";					
				//!AD blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				//!AD if (!blnErr) return blnErr;

				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			}else if (intGp == 61){
				szFieldName = "V_YD_GP";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				
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
				
			}else if (intGp == 62) {		

				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);				
				
			}else if(intGp == 70){
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);				
			}else if (intGp == 67 || intGp == 74 ) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
			}else if (intGp == 68) {		

				szFieldName = "V_SEARCH_1";					// 조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			
			}else if (intGp == 71 || intGp == 722) {
			
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
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
		 	
			}else if (intGp == 72){
				szFieldName = "V_ORD_DAY";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);

				szFieldName = "V_SORT1";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 1, 'S', 0, 0);
								
				szFieldName = "V_SORT2";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 1, 'S', 0, 0);
								
				szFieldName = "V_SORT3";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 1, 'S', 0, 0);
									
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
				
			}else if (intGp == 73){
				szFieldName = "V_ORD_NO";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				
				szFieldName = "V_ORD_DTL";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
				
				szFieldName = "V_SORT1";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 1, 'S', 0, 0);
								
				szFieldName = "V_SORT2";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 1, 'S', 0, 0);
								
				szFieldName = "V_SORT3";						//조회별 구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 1, 'S', 0, 0);
								
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
				
			} else if (intGp == 75){
				szFieldName = "V_YD_STRCHAR_GRP_CD";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
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
				
				szFieldName = "V_YD_SHIP_ORD_NO";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 12, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SHIP_ORD_SEQ_MIN";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SHIP_ORD_SEQ_MAX";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_HDS_FM";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_HDS_TO";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
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
				
			} else if (intGp == 76){
				szFieldName = "V_PTOP_PLNT_GP";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CT_MILL_SPEC_WRK_STAT_GP";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			
				szFieldName = "V_PRPL_MILL_WO_DT";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 14, 1, 'S', 0, 0);
					
			}else if (intGp == 78) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			}
			else if (intGp == 79 || intGp == 80 || intGp == 149) {
				szFieldName = "V_OCCUR_DDTT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 14, 1, 'S', 0, 0);
			}else if (intGp == 81) {
				szFieldName = "V_SEARCH_GBN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_PRINT_COND";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_PRINT_WEAL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_PRINT_UNIT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_FROM_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_TO_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			}else if (intGp == 82){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_ORD_LINE";	
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);

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
			
			}else if (intGp == 84){
				szFieldName = "V_FRTOMOVE_WORD_DATE";
				// 추후 반영
			}else if (intGp == 85){
				szFieldName = "V_FRTOMOVE_WORD_DATE";
				// 추후 반영
			}else if (intGp == 86){
				szFieldName = "V_STL_NO";
				// 추후 반영
			}else if (intGp == 88){
				szFieldName = "V_ORD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
			
				szFieldName = "V_ORD_DTL";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 1, 'S', 0, 0);
			} else if (intGp == 89){
				szFieldName = "V_YD_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 3, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
				
					
			} else if (intGp == 90){
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			}else if (intGp == 91 || intGp == 92){
				szFieldName = "V_PTOP_PLNT_GP";		//조업공장구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";			//야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";		//동구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			} else if (intGp == 93){
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if (intGp == 94){
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
			} else if (intGp == 95){
		
				szFieldName = "V_CT_PLN_WO_MC_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
			} else if (intGp == 96 || intGp == 97){
				szFieldName = "V_MACHINE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);				
			} else if (intGp == 98){
			
				szFieldName = "V_CT_PLN_WO_MC_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PLAN_HEAT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
				
			} else if (intGp == 99||intGp == 100){
				szFieldName = "V_SCH_CODE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);		

			} else if (intGp == 101){
				szFieldName = "V_YD_STR_GTR_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);		
			} else if (intGp == 102){
				szFieldName = "V_SCARFING_SIGN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);		
			} else if (intGp == 105){
				szFieldName = "V_YD_GP";			//야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CAR_PROG_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CARD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRANS_ORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
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
			} else if (intGp == 54 || intGp == 106){
				szFieldName = "V_YD_GP";			//야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";		//동구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";		//목표야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			} else if ( intGp == 107){
				szFieldName = "V_HEAT_NO";			//HEAT NO
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				
			} else if ( intGp == 108){
				szFieldName = "V_YD_CAR_SCH_ID"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRN_EQP_CD"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				
				szFieldName = "V_NO1"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
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
			} else if ( intGp == 111){
				szFieldName = "V_DEST_TEL_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 20, 1, 'S', 0, 0);
			} else if (intGp == 113){
				szFieldName = "V_TRANS_ORD_DT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'L', 0, 0);				
			} else if (intGp == 114 || intGp == 115){
				szFieldName = "V_YD_GP";			//야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";		//동구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";		//목표야드구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP1";		//목표행선구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP2";		//목표행선구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP3";		//목표행선구분
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 118) {
				szFieldName = "V_TRANS_ORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'I', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
	
				szFieldName = "V_CARD_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
			} else if (intGp == 119) {
				szFieldName = "V_NEXT_PROC";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName,  2,2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 120 || intGp == 179) {		
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			}else if (intGp == 121){
				szFieldName = "V_SPOS_WLOC_CD";					//개소코드
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					//야드목표행선
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					//적치열
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
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
				if (!blnErr) return blnErr;
			}else if (intGp == 122){
				szFieldName = "V_SPOS_WLOC_CD";					//개소코드
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					//야드목표행선
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					//적치열
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}  else if (intGp == 123){
				szFieldName = "V_WO_STATE";					      //상태 지시/완료
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 124){
				szFieldName = "V_SPOS_WLOC_CD";					//개소코드
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					//야드목표행선
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
				if (!blnErr) return blnErr;
			}else if (intGp == 125){
				szFieldName = "V_SPOS_WLOC_CD";					//개소코드
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					//야드목표행선
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 126 || intGp == 128) {
				szFieldName = "V_TRANS_ORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, YdDaoUtils.STRING_TYPE, 0, 0);	//운송지시일자
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, YdDaoUtils.INTEGER_TYPE, 0, 0);	//운송지시순번
//				if (!blnErr) return blnErr;
				
//				szFieldName = "V_CAR_NO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 1, 'S', 0, 0);
//				if (!blnErr) return blnErr;

//PIDEV	
//				szFieldName = "V_CARD_NO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, YdDaoUtils.STRING_TYPE, 0, 0);	//카드번호
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					//적치열
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, YdDaoUtils.STRING_TYPE, 0, 0);	//적치열
				if (!blnErr) return blnErr;
			}else if (intGp == 127) {		
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 129) {
				szFieldName = "V_YD_PILING_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_W_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_L_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
			}else if (intGp == 130){
				szFieldName = "V_YD_WBOOK_ID";					//Book ID
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID2";					//Book ID
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;	

			} else if ( intGp == 132 || intGp == 162 || intGp == 171 || intGp == 174) {
				
				szFieldName = "V_SLAB_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				
			} else if ( intGp == 133) {
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CUST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DEST_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRANS_ORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO_MIN";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO_MAX";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
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
			} else if ( intGp == 135) {
				szFieldName = "V_BOOK_OUT";
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
			} else if (intGp == 136) {
				szFieldName = "V_SEARCH_1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SORT_1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 30, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SORT_2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 30, 2, 'S', 0, 0);
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
			} else if (intGp == 137 || intGp == 152) {
				szFieldName = "V_WO_STATE";					      //상태 지시/완료
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_MAKER_NAME";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 138) {
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_MAKER_NAME";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 139){
				szFieldName = "V_YD_STRCHAR_GRP_CD";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
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
				
				szFieldName = "V_YD_SHIP_ORD_NO";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 12, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SHIP_ORD_SEQ_MIN";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SHIP_ORD_SEQ_MAX";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_HDS_FM";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_HDS_TO";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SORT_1";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SORT_2";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SORT_3";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 99, 2, 'S', 0, 0);
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
			} else if (intGp == 140){
				szFieldName = "V_YD_GP";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 141){
				szFieldName = "V_YD_GP";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";						
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 142 || intGp == 156){
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_MAKER_NAME";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BED_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 143){
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PLAN_CRN";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 145){
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_TYPE";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 147){
				szFieldName = "V_FRTOMOVE_WORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);				
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 148){
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);				
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);				
				if (!blnErr) return blnErr;
				
				szFieldName = "V_MK_MOD_EXN";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_MATCH_ORDERTRANS_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);				
				if (!blnErr) return blnErr;
				
				szFieldName = "V_MK_MOD_DT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;				
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
			} else if (intGp == 150){
				szFieldName = "V_OCCUR_DDTT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 14, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
			}else if (intGp == 151){
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PLAN_CRN";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 152) {
//				szFieldName = "V_WO_STATE";					      //상태 지시/완료
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
//				if (!blnErr) return blnErr;
				
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_MAKER_NAME";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if ( intGp == 153 ) {
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_MAKER_NAME";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BED_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 154) {
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_MAKER_NAME";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BED_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if ( intGp == 158 ) {
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_MAKER_NAME";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 40, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if(intGp == 159){
				szFieldName = "V_FRTOMOVE_ORD_CANCEL_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);				
				
			} else if(intGp == 160){
				szFieldName = "V_FRTOMOVE_ORD_CANCEL_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);				
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;				
			} else if(intGp == 161){
				szFieldName = "V_PTOP_PLNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
			}else if(intGp == 163 ){
				//163:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
				szFieldName = "V_SPAN_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_WO_MSLAB_RPR_MTD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 164 ){
				//164:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
				szFieldName = "V_SPAN_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HCR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HEAT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if( intGp == 165){
				//163:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
				szFieldName = "V_SPAN_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_WO_MSLAB_RPR_MTD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 166 ){
				//164:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
				szFieldName = "V_SPAN_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HCR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HEAT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 167 ){
				//167:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD
				szFieldName = "V_SPAN_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_WO_MSLAB_RPR_MTD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				
			}else if(intGp == 168 ){
				//168:SPAN_SEARCH_GP, YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO
				szFieldName = "V_SPAN_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HCR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HEAT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				
			}else if(intGp == 169 ){
				//169:YD_GP, YD_SCH_CD, YD_PREP_WK_ST, YD_WRK_PLAN_CRN, CAR_GP
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "YD_PREP_WK_ST";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PLAN_CRN";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 170) {
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_W_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_MTL_L_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);

			}else if (intGp == 173) { //YD_CAR_USE_GP, SPOS_WLOC_CD, CAR_NO, CAR_NO
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_SPOS_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 177) {
				szFieldName = "V_NEXT_PROC"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_COIL_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 181) {
				
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
			}else if(intGp == 182){
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			}else if(intGp == 183){
				//183:YD_STK_COL_GP, YD_STK_BED_NO
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			}else if(intGp == 186 ){
				//186:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_WO_MSLAB_RPR_MTD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 187 ){
				//187:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HCR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HEAT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if( intGp == 188){
				//188:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD, PAGE_NO, ROW_CNT
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_WO_MSLAB_RPR_MTD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 189 ){
				//189:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SCH_SEARCH_GP, YD_SCH_CD, 
				//SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO, PAGE_NO, ROW_CNT
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SCH_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HCR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HEAT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.PAGE_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, YdDaoUtils.ROW_COUNT_TYPE, 0, 0);
				if (!blnErr) return blnErr;
			}else if(intGp == 190 ){
				//190:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, WO_MSLAB_RPR_MTD
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_WO_MSLAB_RPR_MTD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				
			}else if(intGp == 191 ){
				//191:YD_GP, YD_BAY_GP, YD_SPAN_GP, RT_SEARCH_GP, YD_AIM_RT_GP, SEARCH_GP, HCR_GP, DATE_FROM, DATE_TO, HEAT_NO
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SPAN_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_RT_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_SEARCH_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HCR_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_HEAT_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, YdDaoUtils.STRING_TYPE, 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 192) { //YD_CAR_USE_GP, CAR_NO, CAR_NO
				szFieldName = "V_YD_CAR_USE_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CAR_NO"; 
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 193){
				
				szFieldName = "V_SPOS_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_BED_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				
			}else if (intGp == 194){
				
				szFieldName = "V_PL_PLATE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;				
			}
			
			else if (intGp == 196){
				
				szFieldName = "V_PTOP_PLNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_CT_MILL_SPEC_WRK_STAT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_TC_LOC";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			}else if (intGp == 197){
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;		
				
			}else if (intGp == 199){
				
				szFieldName = "V_PL_MPL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;					

				szFieldName = "V_PL_DIV_TRIM_GP_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;	
				
			} else if ( intGp == 200 ) {
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 201){
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 202) {
				szFieldName = "V_SPOS_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ARR_WLOC_CD";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_STK_COL_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_RT_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_YD_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_AIM_BAY_GP";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_FROM";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_DATE_TO";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_PREP_WK_ST";					
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				szFieldName = "V_PAGE_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;
			}else if (intGp == 204) {
				szFieldName = "V_STOCK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;				
			}
			
			
		}catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdStock
	
	
	
/*------------------------------------- INSERT -------------------------------------------*/
	
	
	public int insYdStock(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "insYdStock";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("insYdStockReTX", new Class[] { JDTORecord.class }, new Object[] { inRec });
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.insYdStockTX(inRec);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStock
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYdStockTX(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insYdStock";
		String szMsg                = "";
		
		try {			

			oParam = new Object[] {
					ydDaoUtils.paraRecChkNull(jRecordParam, "STL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REGISTER")
				  //,ydDaoUtils.paraRecChkNull(jRecordParam, "REG_DDTT")
				  //,ydDaoUtils.paraRecChkNull(jRecordParam, "MODIFIER")
				  //,ydDaoUtils.paraRecChkNull(jRecordParam, "MOD_DDTT")
				  //,ydDaoUtils.paraRecChkNull(jRecordParam, "DEL_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PTOP_PLNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_ITEM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_STAT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "FRTOMOVE_PLANT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PLNT_PROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "APPEAR_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "OVERALL_STAMP_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_PILING_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_BOOK_OUT_LOC2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "HANDSCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WO_MSLAB_RPR_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_DLVRDD_RULE_DD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "TRANS_ORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CAR_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CARD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_BED_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STK_COL_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "WO_CAR_PLNT_PROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ORD_BEFO_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_FRTOMOVE_YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_FRTOMOVE_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "URGENT_FRTOMOVE_WORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_FTMV_MEANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "CC_CCM_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "MMATL_FEE_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PLAN_CRN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PLAN_TCAR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_WRK_PLAN_CAR_USE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CAR_UPP_LOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_CURR_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_RCPT_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_RCPT_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "SAILNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_RCPT_PLN_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_RCPT_PLN_STR_LOC1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_RCPT_PLN_STR_LOC2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "YD_STRCHAR_GRP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "PL_RCPT_TRK_NO")
			};

			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns1, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdStockTX
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 INSERT parameter Check
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
			
			szFieldName = "V_YD_WBOOK_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_ITEM";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_RT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_LOT_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_STK_LOT_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_AIM_RT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_AIM_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_AIM_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_AIM_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_T";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'D', 3, 3);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_W";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'D', 4, 1);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_L";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_COIL_INDIA";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'D', 4, 1);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_COIL_OUTDIA";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 4, 1);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_STL_APPEAR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_PL_MPL_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_PLNT_PROC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_STL_PROG_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_YEOJAE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_DTL";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_BUY_SLAB_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 30, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SLAB_WO_RT_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_HCR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_HCR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CCM_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SCARFING_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SCARFING_DONE_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_WO_MSLAB_RPR_MTD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SCARFING_DEPTH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REHEAT_SLAB_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ROLL_UNIT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ROLL_UNIT_NAME";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ROLL_UNIT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REFUR_CHG_LOT_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_REFUR_CHG_PLN_SERNO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 22, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ITEMNAME_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_FRTOMOVE_ORD_DATE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_FRTOMOVE_PLANT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_URGENT_FRTOMOVE_WORD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_HYSCO_TRANS_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_APPEAR_GRADE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_COOL_METHOD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_COOL_DONE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CONVEYOR_BRANCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_PILING_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BOOK_OUT_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CUST_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEST_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DLVRDD_RULE_DD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_TRANS_ORD_DATE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
//			szFieldName = "V_TRANS_ORD_SEQNO";
//			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'L', 0, 0);
//			if (!blnErr) return blnErr;
			
			szFieldName = "V_OVERALL_STAMP_GRADE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CAR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 15, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CARD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_W_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_T_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MTL_L_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
/*
			szFieldName = "V_PTOP_PLNT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_DEST_TEL_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 20, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;

			szFieldName = "V_DIST_SHIPASSIGN_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
*/			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updYdStock";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
				
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdStockReTX", new Class[] { JDTORecord.class }, new Object[] { inRec});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					szMsg = "UPDATE 처리 실패 (" + iRtn + ")";
					ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdStockOLD(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE - 선적예정일
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:SHPM_SCH_DD)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockShipDD(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		
		String szMethodName         = "updYdStockShipDD";
		String szMsg                = "";
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		
		String queryId 				= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updSlabShipingSchDate";
		String queryId2			    = "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updSlabShipingSchDate2";
		
		try {
			
			//변환용 레코드
			JDTORecord recInPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			String stlNo  		=  ydDaoUtils.paraRecChkNull(recInPara, "V_STL_NO");
	    	String shipDate  	=  ydDaoUtils.paraRecChkNull(recInPara, "V_SHPM_SCH_DD");
	    	String userId       =  ydDaoUtils.paraRecChkNull(recInPara, "V_USER_ID");
	    	String flagYN		=  ydDaoUtils.paraRecChkNull(recInPara, "V_STEP_YN");
	    	
	    	szMsg=">>>>>>>>> YdStockDao.updYdStockShipDD -- 재료번호: [ "+stlNo+"], 선적예정일: ["+shipDate+"]";
	    	
	    	ydUtils.putLog(YdStockDao.class.getName(), szMethodName, szMsg, YdConstant.WARNING);
			blnChk_Field = this.chkParameter(recInPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0) {
				recInPara.setField("JSPEED_QUERY_ID", queryId);
			}
			else if (intGp == 1) {
				recInPara.setField("JSPEED_QUERY_ID", queryId2);
			}
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
			
			//query execute
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockShipDD
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품YM UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYmStock(JDTORecord inRec) throws DAOException,
	JDTOException {
		String sQueryId = ""; 
		int count = 0;
		String sTock_ID 			= StringHelper.evl(inRec.getFieldString("STL_NO"), "");
		String sSHEAR_SUPPLY_SEQ 	= StringHelper.evl(inRec.getFieldString("YD_CAR_UPP_LOC_CD"), "");
		String sTRANS_ORD_DT 		= StringHelper.evl(inRec.getFieldString("TRANS_ORD_DATE"), "");
		String sTRANS_ORD_SEQNO 	= StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO"), "");
		String sTock_Move_Term 		= StringHelper.evl(inRec.getFieldString("STOCK_MOVE_TERM"), "");
		String sMODIFIER 			= StringHelper.evl(inRec.getFieldString("MODIFIER"),"");		
		String sCAR_NO 				= StringHelper.evl(inRec.getFieldString("CAR_NO"), "");
		String sCARD_NO 			= StringHelper.evl(inRec.getFieldString("CARD_NO"), "");
		String sYD_RULE_PL_RS_GP	= StringHelper.evl(inRec.getFieldString("YD_RULE_PL_RS_GP"), "");
		String sSHEAR_SUPPLY_GP		= StringHelper.evl(inRec.getFieldString("SHEAR_SUPPLY_GP"), "");
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		try {
		
			/*com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYmStock*/ 
			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.YdStockDao.updYmStock";
			count = dao.updateData(sQueryId, new Object[] {sTock_Move_Term, sYD_RULE_PL_RS_GP,sTRANS_ORD_DT+sTRANS_ORD_SEQNO,
					sTRANS_ORD_DT,sTRANS_ORD_SEQNO,sSHEAR_SUPPLY_SEQ,
					sMODIFIER,sCAR_NO,sCARD_NO , sSHEAR_SUPPLY_GP , sTock_ID });
			 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
		} // end of updYmStock
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품YD UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYdStock(JDTORecord inRec) throws DAOException,
	JDTOException {
		String sQueryId = ""; 
		int count = 0;
		String sTock_ID 			= StringHelper.evl(inRec.getFieldString("STL_NO"), "");
		String sSHEAR_SUPPLY_SEQ 	= StringHelper.evl(inRec.getFieldString("YD_CAR_UPP_LOC_CD"), "");
		String sTRANS_ORD_DT 		= StringHelper.evl(inRec.getFieldString("TRANS_ORD_DATE"), "");
		String sTRANS_ORD_SEQNO 	= StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO"), "");
		String sTock_Move_Term 		= StringHelper.evl(inRec.getFieldString("YD_AIM_RT_GP"), "");
		String sMODIFIER 			= StringHelper.evl(inRec.getFieldString("MODIFIER"),"");		
		String sCAR_NO 				= StringHelper.evl(inRec.getFieldString("CAR_NO"), "");
		String sCARD_NO 			= StringHelper.evl(inRec.getFieldString("CARD_NO"), "");
		String sYD_RULE_PL_RS_GP	= StringHelper.evl(inRec.getFieldString("YD_RULE_PL_RS_GP"), "");
		String sCAR_LOTID			= StringHelper.evl(inRec.getFieldString("CAR_LOTID"), "");
		String sYD_STK_BED_NO		= StringHelper.evl(inRec.getFieldString("YD_STK_BED_NO"), "");
 
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		try {
		
			/*com.inisteel.cim.ym.dao.ydstockdao.updYdStock*/ 
			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.updYdStock";
			count = dao.updateData(sQueryId, new Object[] {sTock_Move_Term, sYD_RULE_PL_RS_GP,sTRANS_ORD_DT,sTRANS_ORD_SEQNO,sSHEAR_SUPPLY_SEQ,
					sMODIFIER,sCAR_NO,sCARD_NO ,sCAR_LOTID,sCAR_LOTID,sYD_STK_BED_NO, sTock_ID });
			 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
		} // end of updYdStock
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품YD UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYdStockTrnsOrd(JDTORecord inRec) throws DAOException,
	JDTOException {
		String sQueryId = ""; 
		int count = 0;
 
		String sTRANS_ORD_DT 		= StringHelper.evl(inRec.getFieldString("TRANS_ORD_DT"), "");
		String sTRANS_ORD_SEQNO 	= StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO"), "");
		String sDEL_YN		 		= StringHelper.evl(inRec.getFieldString("DEL_YN"), "");
		String sSTL_PROG_CD			= StringHelper.evl(inRec.getFieldString("STL_PROG_CD"),"");		
		String sYD_AIM_RT_GP		= StringHelper.evl(inRec.getFieldString("YD_AIM_RT_GP"), "");
		String sMODIFIER 			= StringHelper.evl(inRec.getFieldString("MODIFIER"), "");
		String sYD_STK_COL_GP 		= StringHelper.evl(inRec.getFieldString("YD_STK_COL_GP"), "");
		ymCommonDAO dao = ymCommonDAO.getInstance();
 
		
		try {
		
			/*com.inisteel.cim.ym.dao.ydstockdao.updYdStockTrnsOrd*/ 
			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.updYdStockTrnsOrd";
			count = dao.updateData(sQueryId, new Object[] {sDEL_YN,sSTL_PROG_CD,sYD_AIM_RT_GP,sMODIFIER,sTRANS_ORD_DT,sTRANS_ORD_SEQNO,sYD_STK_COL_GP });
			 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
		} // end of updYdStockTrnsOrd
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품YD UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYmStockTrnsOrd(JDTORecord inRec) throws DAOException,
	JDTOException {
		String sQueryId = ""; 
		int count = 0;
 
		String sTRANS_ORD_DT 		= StringHelper.evl(inRec.getFieldString("TRANS_ORD_DT"), "");
		String sTRANS_ORD_SEQNO 	= StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO"), "");
		String sDEL_YN		 		= StringHelper.evl(inRec.getFieldString("DEL_YN"), "");
 		String sSTOCK_MOVE_TERM		= StringHelper.evl(inRec.getFieldString("STOCK_MOVE_TERM"), "");
		String sMODIFIER 			= StringHelper.evl(inRec.getFieldString("MODIFIER"), "");
		String sYD_STK_COL_GP 		= StringHelper.evl(inRec.getFieldString("YD_STK_COL_GP"), "");
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
 
		
		try {
		
			/*com.inisteel.cim.ym.dao.ydstockdao.updYmStockTrnsOrd*/ 
			sQueryId = "com.inisteel.cim.ym.dao.ydstockdao.updYmStockTrnsOrd";
			count = dao.updateData(sQueryId, new Object[] {sDEL_YN,sSTOCK_MOVE_TERM,sMODIFIER,sTRANS_ORD_DT,sTRANS_ORD_SEQNO,sYD_STK_COL_GP });
			 
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
		} // end of updYmStockTrnsOrd
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdUtils ydUtils             = new YdUtils();
		JDTORecordSet outRecSet     = null;
		JDTORecord recInPara        = null;
		JDTORecord recOutPara       = null;
		JDTORecord outRec           = null;
		JDTORecord recPara          = null;
		JDTORecord jRecordParam     = null;
		Object oParam[]             = null;
		String szMethodName         = "updYdStockTX";
		String szMsg                = "";
		String szOperationName      = "=== 저장품 기본 업데이트 쿼리(" + inRec.getFieldString("STL_NO") + ") ===";
		int intRtnVal               = 0;

		try {
			// RecordSet Create
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// Update Data Select
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", inRec.getFieldString("STL_NO"));
			
//PIDEV_S :병행가동용:PI_YD
			recPara.setField("PI_YD", commUtils.nvl(inRec.getFieldString("V_PI_YD"), "*") );
			intRtnVal = this.getYdStock(recPara, outRecSet, 0);
			
			if(intRtnVal < 0) {
				// Parameter Error Return
				szMsg = "Parameter Error!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			} else if(intRtnVal == 0) {
				// Data Not Found Return
				szMsg = "Data Not Found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			} else if(intRtnVal != 1) {
				// Duplicate Data Return
				szMsg = "Duplicate Data!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
			
			outRecSet.absolute(1);
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setRecord(outRecSet.getRecord());

			// 필드명 변환 (필드명 -> V_필드명)		
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);
			
			// Data Mapping
			this.dataMapping(recInPara, recOutPara);
			
			//parameter check
			//boolean blnChk_Field = true;
			//blnChk_Field = this.chkParameter(recOutPara);
			
			//parameter error return
			//if (!blnChk_Field) 
			//	return intRtnVal = -2;
			
			jRecordParam = recOutPara;
			
			oParam = new Object[] {
					 ydDaoUtils.paraRecChkNull(jRecordParam, "V_REGISTER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_REG_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_MODIFIER")
			      //,ydDaoUtils.paraRecChkNull(jRecordParam, "V_MOD_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DEL_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PTOP_PLNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_ITEM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_STAT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_AIM_YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_FRTOMOVE_PLANT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PLNT_PROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_APPEAR_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_OVERALL_STAMP_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_PILING_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_BOOK_OUT_LOC1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_PILING_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_BOOK_OUT_LOC2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_HANDSCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_WO_MSLAB_RPR_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_DLVRDD_RULE_DD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_TRANS_ORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CAR_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CARD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STK_BED_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STK_COL_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_WO_CAR_PLNT_PROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_BEFO_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_FRTOMOVE_YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_FRTOMOVE_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_URGENT_FRTOMOVE_WORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_FTMV_MEANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CC_CCM_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_MMATL_FEE_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_WRK_PLAN_CRN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_WRK_PLAN_TCAR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_WRK_PLAN_CAR_USE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_CAR_UPP_LOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_CURR_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SAILNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_PLN_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_PLN_STR_LOC1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_PLN_STR_LOC2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STRCHAR_GRP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PL_RCPT_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PL_RCPT_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CAR_LOTID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PRE_AR_STAT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SNDBK_RSN_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_STL_NO")
			};
			
			int iRtn = assistantDAO.trtProcess(szQueryIdUpd1, oParam);
			if(iRtn <= 0){
				szMsg = "UPDATE 처리 실패 (" + iRtn + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockTX
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:STL_NO)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockOLD(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdUtils ydUtils             = new YdUtils();
		JDTORecordSet outRecSet     = null;
		JDTORecord recInPara        = null;
		JDTORecord recOutPara       = null;
		JDTORecord outRec           = null;
		JDTORecord recPara          = null;
		JDTORecord jRecordParam     = null;
		Object oParam[]             = null;
		String szMethodName         = "updYdStockOLD";
		String szMsg                = "";
		String szOperationName      = "=== 저장품 기본 업데이트 쿼리(" + inRec.getFieldString("STL_NO") + ") ===";
		int intRtnVal               = 0;

		try {
			// RecordSet Create
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// Update Data Select
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO", inRec.getFieldString("STL_NO"));
			intRtnVal = this.getYdStock(recPara, outRecSet, 0);
			
			if(intRtnVal < 0) {
				// Parameter Error Return
				szMsg = "Parameter Error!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			} else if(intRtnVal == 0) {
				// Data Not Found Return
				szMsg = "Data Not Found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal;
			} else if(intRtnVal != 1) {
				// Duplicate Data Return
				szMsg = "Duplicate Data!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
			
			outRecSet.absolute(1);
			outRec = JDTORecordFactory.getInstance().create();
			outRec.setRecord(outRecSet.getRecord());

			// 필드명 변환 (필드명 -> V_필드명)		
			recOutPara = ydDaoUtils.conversionFieldname(outRec, 0);
			
			// Data Mapping
			this.dataMapping(recInPara, recOutPara);
			
			//parameter check
			//boolean blnChk_Field = true;
			//blnChk_Field = this.chkParameter(recOutPara);
			
			//parameter error return
			//if (!blnChk_Field) 
			//	return intRtnVal = -2;
			
			jRecordParam = recOutPara;

			
			
			ydUtils.displayRecord(szOperationName, recInPara);
			
			
			oParam = new Object[] {
					 ydDaoUtils.paraRecChkNull(jRecordParam, "V_REGISTER")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_REG_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_MODIFIER")
			      //,ydDaoUtils.paraRecChkNull(jRecordParam, "V_MOD_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DEL_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_WBOOK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_SCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PTOP_PLNT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_ITEM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ITEMNAME_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_STAT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_STL_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_YEOJAE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_DTL")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_BUY_SLAB_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_AIM_RT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_AIM_YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_AIM_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STK_LOT_TP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STK_LOT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_FRTOMOVE_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_FRTOMOVE_PLANT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_STL_APPEAR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PLNT_PROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_APPEAR_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_OVERALL_STAMP_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_T")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_W")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_L")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_WT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_COIL_INDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_COIL_OUTDIA")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_W_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_T_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_MTL_L_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_COIL_OUTDIA_GRP_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SLAB_WO_RT_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_HCR_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_HYSCO_TRANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_COOL_METHOD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_COOL_DONE_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_CONVEYOR_BRANCH_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_PILING_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_BOOK_OUT_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_PILING_CD1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_BOOK_OUT_LOC1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_PILING_CD2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_BOOK_OUT_LOC2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SCARFING_DONE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_HANDSCARFING_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_WO_MSLAB_RPR_MTD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_REHEAT_SLAB_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ROLL_UNIT_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ROLL_UNIT_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_REFUR_CHG_LOT_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_REFUR_CHG_PLN_SERNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CUST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DEST_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DEMANDER_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DEST_TEL_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_DLVRDD_RULE_DD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_TRANS_ORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_TRANS_ORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CAR_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CARD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STK_BED_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STK_COL_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_STLKIND_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SPEC_ABBSYM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_WO_CAR_PLNT_PROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ORD_BEFO_PROG_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_RENTPROC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_ARR_WLOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_FRTOMOVE_YD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_FRTOMOVE_BAY_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_URGENT_FRTOMOVE_WORD_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_FTMV_MEANS_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_GOODS_GRADE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CC_CCM_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_MMATL_FEE_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_WRK_PLAN_CRN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_WRK_PLAN_TCAR")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_WRK_PLAN_CAR_USE_YN")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_CAR_UPP_LOC_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_CURR_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DIST_DUE_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PL_L2_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PL_RCPT_LN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_DIST_SHIPASSIGN_GP")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_EXPORT_SHIP_SET_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SHIPASSIGN_WORD_DATE")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SHIPASSIGN_WORD_SEQNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SHIP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SHIP_NAME")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_RSHP_HOLD_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_BERTH_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_SAILNO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_PLN_STR_LOC")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_PLN_STR_LOC1")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_RCPT_PLN_STR_LOC2")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_YD_STRCHAR_GRP_CD")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PL_RCPT_DDTT")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_PL_RCPT_TRK_NO")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_CAR_LOTID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "V_STL_NO")
			};
			
			int iRtn = assistantDAO.trtProcess(szQueryIdUpd1, oParam);
			if(iRtn <= 0){
				szMsg = "UPDATE 처리 실패 (" + iRtn + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockOLD
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 진도코드 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0: SLAB_COMM 
	 *                              1: PLATE_COMM
	 *                              2: M_SLABCOMM
	 *                              3: COILCOMM)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updPtComm_PROG_CD(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updPtComm_FIX";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updPtComm_PROG_CDReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updPtComm_PROG_CDTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_PROG_CD
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 진도코드 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0: SLAB_COMM 
	 *                              1: PLATE_COMM
	 *                              2: M_SLABCOMM
	 *                              3: COILCOMM)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updPtComm_PROG_CDTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updPtComm_PROG_CDTX";
		String szMsg = null;
		//JDTORecord outRec = null;
		int intRtnVal = -1;
		boolean blnChk_Field = true;
		String szQueryId		= null;
		try {
			
			//변환용 레코드
			JDTORecord recPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPtCommParameter_PROG_CD(recPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				szQueryId = szQueryIdUpd2;
			else if (intGp == 1)
				szQueryId = szQueryIdUpd3;
			else if (intGp == 2)
				szQueryId = szQueryIdUpd4;
			else if (intGp == 3)
				szQueryId = szQueryIdUpd5;
			else if (intGp == 4)
				szQueryId = szQueryIdUpd302;
			else{
				szMsg = "[updPtComm_PROG_CD] 지원하지 않는 쿼리입니다.";
	            ydUtils.putLog(szDaoName, szMethodName, szMsg, YdConstant.DEBUG);
				return -4;
			}
			recPara.setField("JSPEED_QUERY_ID", szQueryId);
			
			szMsg = "[updPtComm_PROG_CD] 실행 전 JSPEED_QUERY_ID = " + szQueryId;
            ydUtils.putLog(szDaoName, szMethodName, szMsg, YdConstant.DEBUG);	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			szMsg = "[updPtComm_PROG_CD] 실행 후 JSPEED_QUERY_ID = " + szQueryId;
            ydUtils.putLog(szDaoName, szMethodName, szMsg, YdConstant.DEBUG);
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_PROG_CDTX
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0: SLAB_COMM 
	 *                              1: PLATE_COMM
	 *                              2: M_SLABCOMM
	 *                              3: COILCOMM)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updPtComm_LOC(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updPtComm_LOC";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updPtComm_LOCReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updPtComm_LOCTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_LOC
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0: SLAB_COMM 
	 *                              1: PLATE_COMM
	 *                              2: M_SLABCOMM
	 *                              3: COILCOMM)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updPtComm_LOCTX (JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updPtComm_LOCTX";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;

		try {
			
			//변환용 레코드
			JDTORecord recPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			blnChk_Field = this.chkPtCommParameter_LOC(recPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7);
			else if (intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd8);
			else if (intGp == 3)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd9);
			else if (intGp == 4)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd500);
			else if (intGp == 300)     // 사외이송
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd300);
			else if (intGp == 301)     // 사외이송
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd301);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_LOCTX
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 수정 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0: SLAB_COMM) 
	 *                              
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updPtComm_FIX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updPtComm_FIX";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updPtComm_FIXReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updPtComm_FIXTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_FIX
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 수정 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0: SLAB_COMM) 
	 *                              
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updPtComm_FIXTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updPtComm_FIXTX";
		String szMsg = null;
		JDTORecord outRec = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;

		try {
			
			//변환용 레코드
			JDTORecord recPara = null;
			
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			//parameter check
			if(intGp==0)
				blnChk_Field = this.chkPtSlabCommParameter_FIX(recPara);
			else if(intGp==1)
				blnChk_Field = this.chkPtMSlabCommParameter_FIX(recPara);
			
			//parameter error return
			if (!blnChk_Field) return intRtnVal = -2;
			
			//query id setting
			if (intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd10);
			else if (intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd11);			
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if (intRtnVal < 0) intRtnVal = -3;
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updPtComm_FIXTX
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 진도코드 UPDATE parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPtCommParameter_PROG_CD(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {
			szFieldName = "V_CURR_PROG_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CURR_PROG_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_BEFO_PROG_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_BEFO_PROG_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_BEFOBEFO_PROG_REG_DDTT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_BEFOBEFO_PROG_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPtCommParameter_PROG_CD
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통테이블 저장위치 UPDATE parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPtCommParameter_LOC(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {			
			szFieldName = "V_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
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
			
			szFieldName = "V_YD_STK_LYR_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STR_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_MODIFIER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_FNL_REG_PGM";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName,20, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPtCommParameter_LOC
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통 슬라브 저장위치 UPDATE parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPtSlabCommParameter_FIX(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {			
			szFieldName = "V_PLNT_PROC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CC_PLNT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_RECORD_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CURR_PROG_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_YEOJAE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_DTL";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SLAB_T";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 2, 'D', 6, 3);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SLAB_W";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'D', 5, 1);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SLAB_LEN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 5, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SLAB_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 5, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LOT_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_STK_LOT_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SCARFING_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_HCR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CCM_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'L', 1, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_STL_APPEAR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SLAB_WO_RT_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_STACK_LOT_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 14, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPtSlabCommParameter_FIX
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 주편 공통 저장위치 UPDATE parameter Check
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return boolean         true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPtMSlabCommParameter_FIX(JDTORecord inRec) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;
		
		try {			
			szFieldName = "V_PLNT_PROC_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CC_PLNT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_STL_APPEAR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			
			szFieldName = "V_RECORD_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CURR_PROG_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_YEOJAE_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 10, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_ORD_DTL";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_MSLAB_T";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 2, 'D', 6, 3);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_MSLAB_W";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'D', 5, 1);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_MSLAB_L";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 5, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_MSLAB_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 5, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CC_CCM_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_CC_CCM_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SLAB_WO_RT_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_HCR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_HCR_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			
			szFieldName = "V_STACK_LOT_TP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			
			szFieldName = "V_STACK_LOT_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_SCARFING_YN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
		
	
			szFieldName = "V_STACK_LOT_NO";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 14, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
				
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPtSlabCommParameter_FIX
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE parameter mapping
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

			szFieldName = "V_YD_WBOOK_ID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_SCH_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_PTOP_PLNT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_ITEM";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ITEMNAME_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_STAT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_STL_PROG_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ORD_YEOJAE_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ORD_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ORD_DTL";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_BUY_SLAB_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_AIM_RT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_AIM_YD_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_AIM_BAY_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_STK_LOT_TP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_STK_LOT_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_FRTOMOVE_ORD_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_FRTOMOVE_PLANT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_STL_APPEAR_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_PLNT_PROC_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_APPEAR_GRADE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_OVERALL_STAMP_GRADE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_T";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_W";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_L";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_WT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_COIL_INDIA";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_COIL_OUTDIA";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_W_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_T_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_MTL_L_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_COIL_OUTDIA_GRP_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SLAB_WO_RT_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ORD_HCR_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_HCR_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_HYSCO_TRANS_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_COOL_METHOD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_COOL_DONE_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_CONVEYOR_BRANCH_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_PILING_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_BOOK_OUT_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_PILING_CD1";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_BOOK_OUT_LOC1";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_PILING_CD2";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_BOOK_OUT_LOC2";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SCARFING_YN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SCARFING_DONE_YN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_HANDSCARFING_YN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_WO_MSLAB_RPR_MTD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_REHEAT_SLAB_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ROLL_UNIT_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ROLL_UNIT_NAME";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_REFUR_CHG_LOT_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_REFUR_CHG_PLN_SERNO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ORD_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_CUST_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_DEST_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_DEMANDER_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_DEST_TEL_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_DLVRDD_RULE_DD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_TRANS_ORD_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_TRANS_ORD_SEQNO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_CAR_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_CARD_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_STK_BED_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_STK_COL_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_STLKIND_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SPEC_ABBSYM";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_WO_CAR_PLNT_PROC_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ORD_BEFO_PROG_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_RENTPROC_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_ARR_WLOC_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_FRTOMOVE_YD_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_FRTOMOVE_BAY_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_URGENT_FRTOMOVE_WORD_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_FTMV_MEANS_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_GOODS_GRADE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_CC_CCM_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_MMATL_FEE_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_WRK_PLAN_CRN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_WRK_PLAN_TCAR";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_WRK_PLAN_CAR_USE_YN";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_CAR_UPP_LOC_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_CURR_STR_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_RCPT_STR_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_RCPT_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_DIST_DUE_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_PL_L2_TRK_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_PL_RCPT_LN_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_DIST_SHIPASSIGN_GP";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_EXPORT_SHIP_SET_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SHIPASSIGN_WORD_DATE";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SHIPASSIGN_WORD_SEQNO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SHIP_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SHIP_NAME";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_RSHP_HOLD_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_BERTH_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_SAILNO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_RCPT_PLN_STR_LOC";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_RCPT_PLN_STR_LOC1";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_RCPT_PLN_STR_LOC2";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_YD_STRCHAR_GRP_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_PL_RCPT_DDTT";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_PL_RCPT_TRK_NO";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CAR_LOTID";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_PRE_AR_STAT_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SNDBK_RSN_CD";        
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);			
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	} // end of dataMapping
	
	
	/**
	 *      [A] 오퍼레이션명 : 공통 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void ptCommdataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_CURR_PROG_REG_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_CURR_PROG_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_BEFO_PROG_REG_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_BEFO_PROG_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_BEFOBEFO_PROG_REG_DDTT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_BEFOBEFO_PROG_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_BAY_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STK_COL_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_BED_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_LYR_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STR_LOC";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STR_LOC_HIS1";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_STR_LOC_HIS2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	
	} // end of dataCommMapping
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 수정 UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataSlabCommFixMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;

		try {
			szFieldName = "V_PLNT_PROC_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_CC_PLNT_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_RECORD_PROG_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_CURR_PROG_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_ORD_YEOJAE_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "OV_RD_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_ORD_DTL";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SLAB_T";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SLAB_W";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SLAB_LEN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_SLAB_WT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_LOT_TP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_STK_LOT_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_SCARFING_YN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_HCR_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_CCM_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_STL_APPEAR_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_SLAB_WO_RT_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_STACK_LOT_NO";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	
	} // end of dataSlabCommFixMapping
	
	
	
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장품 BATCH UPDATE
	 * 
	 * @param  JDTORecord[] recArrPara parameter record array
	 *         int          intMaxCnt  실행횟수 
	 *         int          intGp      쿼리 구분
	 * @return int[]        복수개의 쿼리문에 대한 각각의 수행처리결과건수
	 * @throws JDTOException 
	 * @throws DAOException
	 */		
	public int[] updBatchStock(JDTORecord[] recArrPara, int intMaxCnt, int intGp) throws DAOException, JDTOException {
		JDTORecord[] recArrCvs = null;
		int[] intRtnVal = null;
		
		try {
			for (int Loop_i = 0; Loop_i < intMaxCnt; Loop_i++) {
				
				//필드명 변환 (필드명 -> V_필드명)
				recArrCvs[Loop_i] = ydDaoUtils.conversionFieldname(recArrPara[Loop_i], 0);
				recArrCvs[Loop_i].setField("JSPEED_QUERY_ID", szQueryIdUpd1);
	
			}
			intRtnVal = dbAssDao.trtProcess(recArrPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드 저장품 BATCH UPDATE 파라미터 체크 로직 추가
	 * 
	 * @param  JDTORecord[] recArrPara parameter record array
	 *         int          intMaxCnt  실행횟수 
	 *         int          intGp      쿼리 구분
	 * @return int[]        복수개의 쿼리문에 대한 각각의 수행처리결과건수
	 * @throws JDTOException 
	 * @throws DAOException
	 */		
	public int[] updBatchStock_Chk(JDTORecord[] recArrPara, int intMaxCnt, int intGp) throws DAOException, JDTOException {
		JDTORecord[] recArrCvsIn = null;
		JDTORecord[] recArrCvsOut = null;
		JDTORecord   recPara = null;
		boolean blnChk_Field = false;
		int[] intRtnVal = null;
		int intRtnValLoc = 0;
		JDTORecordSet outRecSet = null;
		
		try {
			for (int Loop_i = 0; Loop_i < intMaxCnt; Loop_i++) {
				
				//recordSet create
				outRecSet = JDTORecordFactory.getInstance().createRecordSet("retTmp");
				
				//필드명 변환 (필드명 -> V_필드명)
				recArrCvsIn[Loop_i] = ydDaoUtils.conversionFieldname(recArrPara[Loop_i], 0);
				
				//update data select
				intRtnValLoc = this.getYdStock(recArrCvsIn[Loop_i], outRecSet, 0);
				
				//parameter error return
				if (intRtnValLoc < 0) {
//					szMsg = "parameter error!";
//					ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
					intRtnVal[Loop_i] = Loop_i;
					return intRtnVal;
				}
				
				//data not found return
				if (intRtnValLoc == 0) {
//					szMsg = "data not found!";
//					ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
					intRtnVal[Loop_i] = Loop_i;
					return intRtnVal;
				}
				
				//duplicate data return
				if (outRecSet.size() != 1) {
//					szMsg = "duplicate data!";
//					ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
					intRtnVal[Loop_i] = Loop_i;
					return intRtnVal;
				}
				
				outRecSet.first();
				recPara = outRecSet.getRecord();
		
				//필드명 변환 (필드명 -> V_필드명)
				recArrCvsOut[Loop_i] = ydDaoUtils.conversionFieldname(recPara, 0);
				
				//data mapping
				this.dataMapping(recArrCvsIn[Loop_i], recArrCvsOut[Loop_i]);
				
				//parameter check
				blnChk_Field = this.chkParameter(recArrCvsOut[Loop_i]);
				
				//parameter error return
				if (!blnChk_Field) {
					intRtnVal[Loop_i] = Loop_i;
					return intRtnVal;
				}
				recArrCvsOut[Loop_i].setField("JSPEED_QUERY_ID", szQueryIdUpd1);
	
			}
			intRtnVal = dbAssDao.trtProcess(recArrCvsOut);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;	
	}
	
	/**
	 *      [A] 오퍼레이션명 : 후판제품야드 저장 Group 편성 스케줄 UPDATE 
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0 : 고객사 및 목적지)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdSvGpSchFm(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdSvGpSchFm";
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
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd12);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSvGpSchFm
	
	/**
	 *      [A] 오퍼레이션명 : 후판제품야드 저장 PilingCd, BookOut, 저장예정위치 UPDATE 
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0 : 고객사 및 목적지)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockPilingCd(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStockPilingCd";
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
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd15);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockPilingCd
	
	/**
	 *      [A] 오퍼레이션명 : 상차지시에 따른 제품UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0 : 고객사 및 목적지)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */
	public int updYdStock2(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updYdStock2";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdStock2ReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdStock2TX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock2
	
	/**
	 *      [A] 오퍼레이션명 : 상차지시에 따른 제품UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0 : 고객사 및 목적지)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock2TX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdStock2TX";
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
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd13);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock2TX
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE(운송지시번호 삭제)
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock6(JDTORecord inRec ) throws DAOException, JDTOException {
		String sQueryId = "";
		
		int count = 0;
		String sTrans_Ord_Date = StringHelper.evl(inRec.getFieldString("TRANS_ORD_DATE"),"");
		String sTrans_Ord_Seqno = StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO"),"");
		String sTC_CODE =StringHelper.evl(inRec.getFieldString("TC_CODE"),"");
		
		String sYd_Aim_Rt_Gp ="";
		
		if(YdConstant.DMYDR011.equals(sTC_CODE)||  //코일제품고간이송지시
		   YdConstant.DMYDR012.equals(sTC_CODE) ){  //후판제품고간이송지시	
		} else{	
		sYd_Aim_Rt_Gp =StringHelper.evl(inRec.getFieldString("YD_AIM_RT_GP"),"");
		}
		String sModifier =StringHelper.evl(inRec.getFieldString("MODIFIER"),"");
	
		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {
			/*
			  com.inisteel.cim.yd.common.dao.YdStockDao.updYdStock6
				UPDATE TB_YD_STOCK
			SET YD_AIM_RT_GP = decode(?,'',YD_AIM_RT_GP ,?)
			      ,TRANS_ORD_DATE =''
			      ,TRANS_ORD_SEQNO =''
			      ,MOD_DDTT = SYSDATE
			      ,MODIFIER =?
			WHERE TRANS_ORD_DATE = ?
			  AND TRANS_ORD_SEQNO =?
			 */
		   sQueryId = "com.inisteel.cim.yd.common.dao.YdStockDao.updYdStock6";
		   count = dao.updateData(sQueryId,new Object[]{ sYd_Aim_Rt_Gp,sYd_Aim_Rt_Gp,sModifier ,sTrans_Ord_Date,sTrans_Ord_Seqno });
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock6
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 UPDATE(카드번호 삭제)
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock7(JDTORecord inRec ) throws DAOException, JDTOException {
		String sQueryId = "";
		
		int count = 0;
		String sTRANS_ORD_DT = StringHelper.evl(inRec.getFieldString("TRANS_ORD_DT"),"");
		String sTRANS_ORD_SEQNO = StringHelper.evl(inRec.getFieldString("TRANS_ORD_SEQNO"),"");
		String sYd_Aim_Rt_Gp =StringHelper.evl(inRec.getFieldString("YD_AIM_RT_GP"),"");
		String sModifier =StringHelper.evl(inRec.getFieldString("MODIFIER"),"");
	
		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {
			/*
			 com.inisteel.cim.yd.common.dao.YdStockDao.updYdStock7
			UPDATE TB_YD_STOCK
			SET YD_AIM_RT_GP = ?
			      ,CARD_NO =''
			      ,CAR_NO =''
			      ,MOD_DDTT = SYSDATE
			      ,MODIFIER =?
			WHERE TRANS_ORD_DT= ?
			    AND TRANS_ORD_SEQNO =?
			 */
		   sQueryId = "com.inisteel.cim.yd.common.dao.YdStockDao.updYdStock7";
		   count = dao.updateData(sQueryId,new Object[]{ sYd_Aim_Rt_Gp,sModifier ,sTRANS_ORD_DT,sTRANS_ORD_SEQNO });
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStock7
	


	
	
	/**
	 *      [A] 오퍼레이션명 : TB_PT_STLFRTOMOVE 테이블에 해당하는 재료번호와 이송지시차수의 이송상태코드를 업데이트 
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock8(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock8";
		String szMsg        = "";
		int nRet            = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리ID 설정d
			if(intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd14);
			
			// 해당 쿼리 실행
			nRet = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return nRet;
	} 
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장품의 YD_WBOOK_ID, YD_SCH_CD 삭제 
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockDelYdWBookId(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updYdStockDelYdWBookId";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdStockDelYdWBookIdReTX", new Class[] { JDTORecord.class}, new Object[] { inRec});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdStockDelYdWBookIdTX(inRec);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockDelYdWBookId
	
	/**
	 *      [A] 오퍼레이션명 : 저장품의 YD_WBOOK_ID, YD_SCH_CD 삭제 
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockDelYdWBookIdTX(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStockDelYdWBookIdTX";
		String szMsg        = "";
		int nRet            = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리ID 설정d
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd21);
			
			// 해당 쿼리 실행
			nRet = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return nRet;
	} 
	
	/**
	 * [A] 오퍼레이션명 : PLATE공통 BookOut저장위치 업데이트 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYdPlateCommBookOutLoc(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updYdPlateCommBookOutLoc";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdPlateCommBookOutLocReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdPlateCommBookOutLocTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLoc
	
	/**
	 * [A] 오퍼레이션명 : PLATE공통 BookOut저장위치 업데이트 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdPlateCommBookOutLocTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdPlateCommBookOutLocTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			if(intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd16);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdPlateCommBookOutLocTX
	
	/**
	 * [A] 오퍼레이션명 : 저장품 BookOutCD 업데이트 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockBookOutLoc(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updYdStockBookOutLoc";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdStockBookOutLocReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdStockBookOutLocTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLoc
	
	/**
	 * [A] 오퍼레이션명 : 저장품 BookOutCD 업데이트 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockBookOutLocTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStockBookOutLocTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			if(intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd16_1);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLocTX
	
	/**
	 * [A] 오퍼레이션명 : Stock의 BookOut저장위치 업데이트 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYdStockBookOutLoc(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updYdStockBookOutLoc";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdStockBookOutLocReTX", new Class[] { JDTORecord.class }, new Object[] { inRec});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdStockBookOutLocTX(inRec);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLoc
	
	/**
	 * [A] 오퍼레이션명 : Stock의 BookOut저장위치 업데이트 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockBookOutLocTX(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStockBookOutLocTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			//if(intGp == 0)
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd17);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLocTX
	
	/**
	 * [A] 오퍼레이션명 : Stock의 BookOut저장위치 업데이트 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockBookOutLoc_Yeojae(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updYdStockBookOutLoc_Yeojae";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdStockBookOutLoc_YeojaeReTX", new Class[] { JDTORecord.class }, new Object[] { inRec});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdStockBookOutLoc_YeojaeTX(inRec);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLoc_Yeojae
	
	/**
	 * [A] 오퍼레이션명 : Stock의 BookOut저장위치 업데이트 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockBookOutLoc_YeojaeTX(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStockBookOutLoc_YeojaeTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			//if(intGp == 0)
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd20);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockBookOutLocTX
	
	/**
	 * [A] 오퍼레이션명 : 주편공통에 야드구분 업데이트 - 2009.12.16  권오창
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */
	public int updYdMSlabCommYdGp(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updPtComm_FIX";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdMSlabCommYdGpReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdMSlabCommYdGpTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdMSlabCommYdGp
		
	
	/**
	 * [A] 오퍼레이션명 : 주편공통에 야드구분 업데이트 - 2009.12.16  권오창
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdMSlabCommYdGpTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdMSlabCommYdGpTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			if(intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd18);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdMSlabCommYdGpTX
	
	/**
	 * [A] 오퍼레이션명 : 슬라브공통에 야드구분 업데이트 - 2009.12.16  권오창
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdSlabCommYdGp(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updPtComm_FIX";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updYdSlabCommYdGpReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updYdSlabCommYdGpTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSlabCommYdGp
	
	
	/**
	 * [A] 오퍼레이션명 : 슬라브공통에 야드구분 업데이트 - 2009.12.16  권오창
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdSlabCommYdGpTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdSlabCommYdGpTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			if(intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd19);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdSlabCommYdGpTX		
	
	
	/**
	 *      [A] 오퍼레이션명 : 저장품의 LOT TYPE, LOT CD 초기화 
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock_CTYDJ031(JDTORecord inRec, int iGbn) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_CTYDJ031";
		String szMsg        = "";
		int nRet            = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			if(iGbn == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd22);
			else if(iGbn == 2) 			
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd26);
			// 해당 쿼리 실행
			nRet = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return nRet;
	} 
	
	/**
	 *      [A] 오퍼레이션명 : 저장품의 LOT TYPE, LOT CD 초기화 
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock_CTYDJ03(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_CTYDJ031";
		String szMsg        = "";
		int nRet            = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd29);
			
			// 해당 쿼리 실행
			nRet = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return nRet;
	} 
	
	/**
	 *      [A] 오퍼레이션명 : C열연 압연지시정보 셋팅 
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock_CTYDJ033(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_CTYDJ033";
		String szMsg        = "";
		int nRet            = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd37);
			
			// 해당 쿼리 실행
			nRet = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return nRet;
	} 
	
	public int updYdStock_CTYDJ033_DEL(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_CTYDJ033_DEL";
		String szMsg        = "";
		int nRet            = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd38);
			
			// 해당 쿼리 실행
			nRet = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return nRet;
	} 
	
	/**
	 *      [A] 오퍼레이션명 : C열연 압연지시정보 셋팅 
	 * 
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock_CTYDJ031(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_CTYDJ031";
		String szMsg        = "";
		int nRet            = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd39);
			
			// 해당 쿼리 실행
			nRet = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return nRet;
	} 
	
	public int updYdStock_CTYDJ031_DEL(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_CTYDJ031_DEL";
		String szMsg        = "";
		int nRet            = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd40);
			
			// 해당 쿼리 실행
			nRet = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return nRet;
	} 
	
	/**
	 * [A] 오퍼레이션명 : CT_후판RoutingLayout작업지시(USRCTA.TB_CT_N_PLRTNGLAYOUTWO) 업데이트 
	 * 이영근
	 * 2010.02.23
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updateTBCTCOMMON(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updateTBCTCOMMON";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			if(intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd23);
			else if(intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd24);	
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updateTBCTCOMMON
	
	
	
	/**
	 * [A] 오퍼레이션명 : 후판주문외제품 이송지시 등록/취소 업데이트 
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updateDmFr(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "updateDmFr";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updateDmFrReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updateDmFrTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updateDmFr
	
	
	/**
	 * [A] 오퍼레이션명 : 후판주문외제품 이송지시 등록/취소 업데이트 
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updateDmFrTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updateDmFrTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			if(intGp == 0)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd27);
			else if(intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd28);	
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updateDmFrTX
	
	
	/**
	 * [A] 오퍼레이션명 : 후판제품 입고시간 업데이트 
	 * 
	 * @param  JDTORecord, int
	 * @return int                
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update_Dm_Time(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName         = "update_Dm_Time";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
	    		String StringGp=Integer.toString(intGp) ;
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("update_Dm_TimeReTX", new Class[] { JDTORecord.class, String.class }, new Object[] { inRec ,StringGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.update_Dm_TimeTX(inRec, intGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of update_Dm_Time
	
	
	/**
	 * [A] 오퍼레이션명 : 후판제품 입고시간 업데이트 
	 * 
	 * @param  JDTORecord, int
	 * @return int                
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update_Dm_TimeTX(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "update_Dm_TimeTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			if(intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd30);
			else if(intGp == 2)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd31);	
			else if(intGp == 4)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd33);
			else if(intGp == 5)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd36);
			else if(intGp == 6)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd41);
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of update_Dm_TimeTX
	
	/**
	 * [A] 오퍼레이션명 : 후판제품 목적지코드 변경
	 * 
	 * @param  JDTORecord, int
	 * @return int                
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update_Dm_DestCd(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "update_Dm_DestCd";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			// 쿼리설정
			if(intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd34);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of update_Dm_DestCd
	
	/**
	 * [A] 오퍼레이션명 : 후판제품 목적지코드 변경
	 * 
	 * @param  JDTORecord, int
	 * @return int                
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update_PlateYeajaeGp(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "update_PlateYeajaeGp";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
	    		
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("update_PlateYeajaeGpReTX", new Class[] { JDTORecord.class }, new Object[] { inRec });
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.update_PlateYeajaeGpTX(inRec);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of update_PlateYeajaeGp
	/**
	 * [A] 오퍼레이션명 : 후판제품 목적지코드 변경
	 * 
	 * @param  JDTORecord, int
	 * @return int                
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int update_PlateYeajaeGpTX(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "update_PlateYeajaeGpTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd35);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of update_PlateYeajaeGpTX
	
	/**
	 *      [A] 오퍼레이션명 : CT INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insertTBCTCOMMON(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insertTBCTCOMMON";
		String szMsg                = "";
		
		try {			
			//com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.insTBCTNPLRTNGLAYOUTWO
			oParam = new Object[] {
					 ydDaoUtils.paraRecChkNull(inRec, "PL_TOT_ROUTE_CNT")
					,ydDaoUtils.paraRecChkNull(inRec, "PL_ROUTE_NODE_NO_GROUP")
					,ydDaoUtils.paraRecChkNull(inRec, "PL_ROUTE_NODE_TYPE_GROUP")
					,ydDaoUtils.paraRecChkNull(inRec, "PL_PLATE_NO")
			};
			
			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns3, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYmStock
	
	
	
	/**
	 *      [A] 오퍼레이션명 : YM업데이트
	 *      
	 * @param  JDTORecord inRec parameter record
	 * @return int              execution count(성공), -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYmStock1(JDTORecord inRec) throws DAOException, JDTOException {
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
	
			//쿼리 아이디 세팅
			recPara.setField("JSPEED_QUERY_ID", szQueryIdUpd25);
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
			
			//execution error return
			if(intRtnVal <= 0) 
				intRtnVal = -3;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYmStock1
	
	

	
	
	/**
	 *      [A] 오퍼레이션명 : TB_YM_STOCK INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int insYmStock(JDTORecord inRec) throws DAOException, JDTOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		YdDaoUtils ydDaoUtils       = new YdDaoUtils();
		JDTORecord jRecordParam     = inRec;
		Object oParam[]             = null;
		int intRtnVal               = 0;
		String szMethodName         = "insYmStock";
		String szMsg                = "";
		
		try {			

			oParam = new Object[] {
					ydDaoUtils.paraRecChkNull(jRecordParam, "STOCK_ID")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STOCK_ITEM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "STOCK_MOVE_TERM")
					,ydDaoUtils.paraRecChkNull(jRecordParam, "REGISTER")
			};

			// INSERT 쿼리 실행
			intRtnVal  = assistantDAO.trtProcess(szQueryIdIns2, oParam);
			if(intRtnVal <= 0){
				szMsg = "INSERT 처리 실패 (" + intRtnVal + ")";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
				return intRtnVal = -1;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYmStock

	
	/**
	 * [A] 오퍼레이션명 : 작업보류/해제
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public GridData updateStlHoldstat(GridData inParam) throws DAOException {
		List param = null;     
		List invParam = null;
		int rowCount = 0;
		int result = 0;
		String s_DNGR_GP = "";
		GridData returnGrid = null;
		GridData outGrid = null;
		JDTORecord outRecord = JDTORecordFactory.getInstance().create();
		//RullCallUtil util = new RullCallUtil();
		try {
			rowCount = inParam.getHeader("CHECK").getRowCount();
			String queryId = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStockDao.updatecSlabYdStkHoldGp";
			returnGrid = OperateGridData.cloneResponseGridData(inParam);
			for(int i = 0; i < rowCount; i++){
				
				   param = new ArrayList(5);

				  				   
				   param.add(CmnUtil.getComboList(inParam, "WORK_HOLD_GP",i));	
				   param.add(inParam.getParam("USER_ID"));
				   param.add(inParam.getHeader("STL_NO").getValue(i));

	
				   result = dbAssDao.trtProcess(queryId, param.toArray());
												
			}
			if(result > 0){
				inParam.addParam("RESULT", "SUCCESS");
				outGrid = CmnUtil.jdtoRecordToGridData(returnGrid, outRecord, inParam);
			}else{
				inParam.addParam("RESULT", "FAILED");
				outGrid = CmnUtil.jdtoRecordToGridData(returnGrid, outRecord, inParam);				
			}
			return outGrid;
		} catch (Exception e) {
			//util.errorLoger(LogLevel.ERROR, this + " :: " + e.getMessage(), e);
			//logger.println(LogLevel.ERROR, this + " :: " + e.getMessage(), e);
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 작업보류/해제
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public int updateStlHoldstat(String sStlNo,String sUserId,String sWorkHoldGp) throws DAOException {

		String szMethodName         = "updateStlHoldstat";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
//			장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.updYdStockchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){
				//트렌젝션 분리 적용	
				
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updateStlHoldstatReTX", new Class[] { String.class,String.class,String.class }, new Object[] { sStlNo,sUserId,sWorkHoldGp});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					szMsg = "UPDATE 처리 실패 (" + iRtn + ")";
					ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
					return intRtnVal = -1;
				}
//	    	}else{
//	    		//기존 방식 적용 
//	    		intRtnVal = this.updateStlHoldstatTX(sStlNo,sUserId,sWorkHoldGp);
//	    		if(intRtnVal ==0){
//	    			return intRtnVal = -1;
//	    		}
//	    	}
			
			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updateStlHoldstat
	
	
	/**
	 * [A] 오퍼레이션명 : 이상재 등록/해제
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public int updateStlAbMtlRsnCd(String sStlNo,
								   String sUserId,
								   String sYdAbmtlRsnCd,
								   String sYdAbmtlHdMtdCd,
								   String sYdAbmtlGrd,
								   String sYdAbmtlRem,
								   String sYDAbmtAsgnDd) throws DAOException {
		
		String szMethodName         = "updateStlAbMtlRsnCd";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
				ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
				iRtn =(Integer)ejbConn.trx("updateStlAbMtlRsnCdReTX", new  Class[] { String.class,String.class,String.class,String.class,String.class,String.class,String.class }, 
						                                              new Object[] { sStlNo,sUserId,sYdAbmtlRsnCd,sYdAbmtlHdMtdCd,sYdAbmtlGrd,sYdAbmtlRem,sYDAbmtAsgnDd});
				if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
					szMsg = "UPDATE 처리 실패 (" + iRtn + ")";
					ydUtils.putLog(szDaoName, szMethodName, szMsg, 1);
					return intRtnVal = -1;
				}

				intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updateStlAbMtlRsnCd
	
	/**
	 * [A] 오퍼레이션명 : 이상재 등록/해제
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public int updateStlAbMtlRsnCdTX(  String sStlNo,
									   String sUserId,
									   String sYdAbmtlRsnCd,
									   String sYdAbmtlHdMtdCd,
									   String sYdAbmtlGrd,
									   String sYdAbmtlRem,
									   String sYDAbmtAsgnDd) throws DAOException {

		int result = 0;
		
		try {
			String queryId = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStockDao.updatecSlabYdStkHoldGp_02";

			List param = new ArrayList(1);
			param.add(sYdAbmtlRsnCd);	
			param.add(sYdAbmtlHdMtdCd);
			param.add(sYdAbmtlGrd);
			param.add(sYdAbmtlRem);
			param.add(sYDAbmtAsgnDd);
			param.add(sUserId);
			param.add(sStlNo);
			result = dbAssDao.trtProcess(queryId, param.toArray());
												
			return result;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	}
	
	/**
	 * [A] 오퍼레이션명 : 작업보류/해제
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public int updateStlHoldstatTX(String sStlNo,String sUserId,String sWorkHoldGp) throws DAOException {
		int result = 0;
		
		try {
			String queryId = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStockDao.updatecSlabYdStkHoldGp";
			/*
			JDTORecord recInPara = JDTORecordFactory.getInstance().create();
			recInPara.setField("WORK_HOLD_GP"	,sWorkHoldGp);	
		    recInPara.setField("USER_ID"		,sUserId);
		    recInPara.setField("STL_NO"			,sStlNo);
		    recInPara.setField("JSPEED_QUERY_ID",queryId);
		    
			result = dbAssDao.trtProcess(recInPara);
			*/
			List param = new ArrayList(1);
			param.add(sWorkHoldGp);	
			param.add(sUserId);
			param.add(sStlNo);
			result = dbAssDao.trtProcess(queryId, param.toArray());
												
			return result;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	}
	
	
	/**
	 * [A] 오퍼레이션명 : 후판출하 보관매출 대상 일괄변경
	 * @param inParam
	 * @return
	 * @throws DAOException
	 */
	public int updateStlHoldstat_01(String sTransmitDate, String sSendSeq) throws DAOException {
		int result = 0;
		
		try {
			String queryId = "com.inisteel.cim.yd.dao.ydstkbeddao.YdStockDao.updatecSlabYdStkHoldGp_01";
			
			List param = new ArrayList(1);
			param.add(sTransmitDate);	
			param.add(sSendSeq);
			result = dbAssDao.trtProcess(queryId, param.toArray());
												
			return result;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	}
	
	/**
	 *      [A] 오퍼레이션명 : 야드저장품 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  return recordSet
	 *         int intGp                구분(0:
	 *                                    
	 *                                     )
	 * @return int                      record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int getYdStock_CTYDJ03(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		String szMethodName = "getYdStock_CTYDJ03";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet213);
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getYdWrkHist] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getYdWrkHist]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getYdStock_CTYDJ03] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getYdStock_CTYDJ03
	
	/**
	 *      [A] 오퍼레이션명 : 슬라브 자동LOT편성 대상재 SELECT
	 */	
	public int getYdStock_AutoLot(JDTORecord inRec, JDTORecordSet outRecSet, String sGp) throws DAOException, JDTOException {
		
		String szMethodName = "getYdStock_AutoLot";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리설정
			if("A".equals(sGp))
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet215);
			else if("D".equals(sGp))
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet216);	
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getYdStock_AutoLot] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getYdStock_AutoLot]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getYdStock_AutoLot] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getYdStock_AutoLot
	
	/**
	 *      [A] 오퍼레이션명 : 후판제품 출하Lot 대상재 SELECT
	 */	
	public int getYdStock_DoubleDong(JDTORecord inRec, JDTORecordSet outRecSet, String sGp) throws DAOException, JDTOException {
		
		String szMethodName = "getYdStock_DoubleDong";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			// 쿼리설정
			if("1".equals(sGp))
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet701);
			else if("2".equals(sGp))
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet702);	
			else if("3".equals(sGp))
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet703);	
			
//PIDEV		
			//PIDEV_S :병행가동용:PI_YD
//			String sPI_YD     = commUtils.nvl(recPara.getFieldString("V_PI_YD"), "*");
//			String toQuery_ID = ydPICommDAO.getYdRulePI("", szMethodName, "YD0001", commUtils.trim(recPara.getFieldString("JSPEED_QUERY_ID")), "APPPI0", sPI_YD, "*" );
//			recPara.setField("JSPEED_QUERY_ID", commUtils.trim(toQuery_ID));

			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
				szMsg = "[getYdStock_AutoLot] data found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
			}else {
				//data not found
				szMsg = "[getYdStock_AutoLot]data not found!";
				ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		szMsg = "[getYdStock_AutoLot] return value : " + intRtnVal;
		ydUtils.putLog(szDaoName, szMethodName, szMsg, 4);
		return intRtnVal;
	} //end of getYdStock_DoubleDong
	
	/**
	 * 
	 */	
	public int getYdStock56ZoneLevInfo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet219);
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdStock56ZoneLevInfo	/**
	/**	
	 * 
	 */	
	public int getYdStock58ZoneLevInfo(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdStock56ZoneLevInfo
    
	public int getYdStockTbCtMPlmplspec(JDTORecord inRec, JDTORecordSet outRecSet) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);

			recPara.setField("JSPEED_QUERY_ID", szQueryIdGet302);
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(recPara);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} //end of getYdStock56ZoneLevInfo
	
	/**
	 * [A] 오퍼레이션명 : Stock의  후판제품운송지시 대기 update 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYdStock_DMYDR028(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updYdStock_DMYDR028";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
			//트렌젝션 분리 적용	
	    		
			ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
			iRtn =(Integer)ejbConn.trx("updYdStock_DMYDR028TX", new Class[] { JDTORecord.class }, new Object[] { inRec});
			if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
				return intRtnVal = -1;
			}

			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_DMYDR028
	
	/**
	 * [A] 오퍼레이션명 : 후판제품운송지시 대기 update
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock_DMYDR028TX(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_DMYDR028TX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd42);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_DMYDR028TX
		
	
	/**
	 * [A] 오퍼레이션명 : Stock의  후판제품차량LOTID update 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYdStock_LOTID(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updYdStock_LOTID";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
			//트렌젝션 분리 적용	
	    		
			ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
			iRtn =(Integer)ejbConn.trx("updYdStock_LOTID", new Class[] { JDTORecord.class }, new Object[] { inRec});
			if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
				return intRtnVal = -1;
			}

			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_LOTID
	
	/**
	 * [A] 오퍼레이션명 : 후판제품차량LOTID update
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock_LOTIDTX(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_LOTIDTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd400);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_LOTIDTX
		
	/**
	 * [A] 오퍼레이션명 : Stock의  C열연차량LOTID update 
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int updYdStock_COIL_LOTID(JDTORecord inRec) throws DAOException, JDTOException {

		String szMethodName         = "updYdStock_COIL_LOTID";
		int intRtnVal               = 0;
		Integer iRtn               	= null;
		EJBConnector ejbConn 		= null;
		String szMsg                = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		try {
			
			//트렌젝션 분리 적용	
	    		
			ejbConn = new EJBConnector("default", "StockSpecRegSeEJB", this);
			iRtn =(Integer)ejbConn.trx("updYdStock_COIL_LOTID", new Class[] { JDTORecord.class }, new Object[] { inRec});
			if( iRtn.intValue() != YdConstant.RETN_INT_SUCCESS.intValue() ) {			//성공
				return intRtnVal = -1;
			}

			intRtnVal = 1;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_LOTID
	
	/**
	 * [A] 오퍼레이션명 : C열연차량LOTID update
	 * 권오창
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStock_COIL_LOTIDTX(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStock_COIL_LOTIDTX";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd401);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStock_LOTIDTX
		
	
	/**
	 * [A] 오퍼레이션명 : 슬라브 지연내용 등록
	 * 정종균
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYd_SlabScarfDelyReg(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYd_SlabScarfDelyReg";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd402);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYd_SlabScarfDelyReg
	
	
	/**
	 * [A] 오퍼레이션명 : 검수테이블 등록 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStockExa(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String STL_NO = StringHelper.evl(inRec.getFieldString("STL_NO"), "");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW2";
			count = dao.updateData(sQueryId, new Object[] { STL_NO });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStockExa
	
	
	
	/**
	 * [A] 오퍼레이션명 : 출하PDA저장품 등록
	 * 정종균
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockReg(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStockReg";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd403);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockReg
	
	
	/**
	 * [A] 오퍼레이션명 : 출하PDA저장품 종료
	 * 정종균
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockEndReg(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStockEndReg";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd404);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockEndReg
	
	/**
	 * [A] 오퍼레이션명 : 후판출하 전화번호 변경정보 수정
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockTelInfo(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStockTelInfo";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd405);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockTelInfo
	
	//2016.04.27 기능추가 : 벤딩재 처리 (표시/해제)
	public GridData updStockBendReg(GridData jrParam) throws DAOException {
    
		String methodNm = "벤딩재 처리 [updStockBendReg] : ";
		String logId = "updStockBendReg";  //gdReq.getIPAddress();
		String trtNm = ",updStockBendReg";
		int result = 0;

		try {
		
		int processBendingCount = 0;
		String vStock_No = jrParam.getParam("V_STL_NOS");
		String bendingYN = jrParam.getParam("V_BENDING_YN");
		String userId = jrParam.getParam("V_MODIFIER");
		String vStockList[] = vStock_No.split(",");
		Object oParam[]   = null;
		
		System.out.println("   -. 재료정보(Parms) : " + vStockList);
		
		String queryId = "com.inisteel.cim.yd.common.dao.ydStockDao.updStockBendReg";
		
		for (int i = 0; i < vStockList.length; i++) {
			oParam = new Object[] {
					 bendingYN
					,userId
					,vStockList[i]
			};

			// INSERT 쿼리 실행
			result = dbAssDao.trtProcess(queryId, oParam);
			processBendingCount++;
		} // for

		System.out.println("   -. 재료정보 벤딩처리 건수 : " + processBendingCount);

		return jrParam;

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} 	
	
	
	
	//2016.04.27 기능추가 : 마킹대상재 처리 (표시/해제)
	public GridData updStockMarkReg(GridData jrParam) throws DAOException {
    
		String methodNm = "마킹대상재 처리 [updStockMarkReg] : ";
		String logId = "updStockMarkReg";
		String trtNm = ",updStockMarkReg";
		int result = 0;

		try {
		
		int processMarkingCount = 0;
		String vStock_No = jrParam.getParam("V_STL_NOS");
		String markingYN = jrParam.getParam("V_MARKING_YN");
		String userId = jrParam.getParam("V_MODIFIER");
		String vStockList[] = vStock_No.split(",");
		Object oParam[]   = null;
		
		System.out.println("   -. 재료정보(Parms) : " + vStockList);
		
		String queryId = "com.inisteel.cim.yd.common.dao.ydStockDao.updStockMarkReg";
		
		for (int i = 0; i < vStockList.length; i++) {
			oParam = new Object[] {
					 markingYN
					,userId
					,vStockList[i]
			};

			// INSERT 쿼리 실행
			result = dbAssDao.trtProcess(queryId, oParam);
			processMarkingCount++;
		} // for

		System.out.println("   -. 재료정보 마킹처리 건수 : " + processMarkingCount);

		return jrParam;

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	}
	
	
	
	//2017.09.22 기능추가 : Q재 등록
	public GridData updStockQslabReg(GridData jrParam) throws DAOException {
    
		String methodNm = "Q재 등록 [updStockQslabReg] : ";
		String logId = "updStockQslabReg";  //gdReq.getIPAddress();
		String trtNm = ",updStockQslabReg";
		int result = 0;

		try {
		
		String vStock_No = jrParam.getParam("V_STL_NO");
		String qslabYN = jrParam.getParam("V_QSLAB_YN");
		String userId = jrParam.getParam("V_MODIFIER");
		Object oParam[]   = null;
		
		String queryId = "com.inisteel.cim.yd.common.dao.ydStockDao.updStockQslabReg";
		
		oParam = new Object[] {
				qslabYN
				,userId
				,vStock_No
		};

		result = dbAssDao.trtProcess(queryId, oParam);
		
		return jrParam;

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} 	//end of updStockQslabReg
	
	
	
	public GridData inStockBendReg(GridData jrParam) throws DAOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		String methodNm = "벤딩재 처리 [inStockBendReg] : ";
		String logId = "inStockBendReg";  //gdReq.getIPAddress();
		String trtNm = ",inStockBendReg";
		int result = 0;

		try {
		
		int processBendingCount = 0;
		String vStock_No = jrParam.getParam("V_STL_NOS");
		String bendingYN = jrParam.getParam("V_BENDING_YN");
		String userId = jrParam.getParam("V_MODIFIER");
		String vStockList[] = vStock_No.split(",");
		Object oParam[]   = null;
		
		System.out.println("   -. 재료정보(Parms) : " + vStockList);
		
		String queryId = "com.inisteel.cim.yd.common.dao.ydStockDao.inStockBendReg";
		
		for (int i = 0; i < vStockList.length; i++) {
			oParam = new Object[] {
					userId
					,userId
					,bendingYN
					,vStockList[i] 
			};

			// INSERT 쿼리 실행
			result = assistantDAO.trtProcess(queryId, oParam);
			processBendingCount++;
		} // for

		System.out.println("   -. 재료정보 벤딩처리 건수 : " + processBendingCount);

		return jrParam;

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	} 	
	
	
	/**
	 * [A] 오퍼레이션명 : 전체입동제한 변경 
	 * 정종균
	 * 2009.12.11
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updCoilCarPointYnReg(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updCoilCarPointYnReg";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd406);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockReg
	
	
	public GridData inStockMarkingReg(GridData jrParam) throws DAOException {
		DBAssistantDAO assistantDAO = new DBAssistantDAO();
		String methodNm = "마킹관리 처리 [inStockMarkingReg] : ";
		String logId = "inStockBendReg";  //gdReq.getIPAddress();
		String trtNm = ",inStockBendReg";
		int result = 0;

		try {
		
		int processBendingCount = 0;
		String vStock_No = jrParam.getParam("V_STL_NOS");
		String bendingYN = jrParam.getParam("V_BENDING_YN");//미사용
		String userId = jrParam.getParam("V_MODIFIER");
		String inspectYN = jrParam.getParam("V_INSPECT_YN");
		String inspectURL = jrParam.getParam("V_INSPECT_IMAGE_URL");
		String vStockList[] = vStock_No.split(",");
		
		Object oParam[]   = null;
		Object oParam2[]   = null;
		
		System.out.println("   -. 재료정보(Parms) : " + vStockList);
		
		String queryId = "com.inisteel.cim.yd.common.dao.ydStockDao.inStockMarkingReg";
		String queryId2 = "com.inisteel.cim.yd.common.dao.ydStockDao.upStockMarkingReg";
		
		for (int i = 0; i < vStockList.length; i++) {
			oParam = new Object[] {
					 userId
					,userId
					,inspectYN
					,inspectURL
					,vStockList[i]
			};

			// INSERT 쿼리 실행
			result = assistantDAO.trtProcess(queryId, oParam);
			
			oParam2 = new Object[] {
					vStockList[i] 
			};

			// INSERT 쿼리 실행
			result = assistantDAO.trtProcess(queryId2, oParam2);
			
			processBendingCount++;
		} // for

		System.out.println("   -. 재료정보 벤딩처리 건수 : " + processBendingCount);

		return jrParam;

		} catch (DAOException daoe) {
			throw daoe;
		} catch (Exception e) {
			throw new DAOException(getClass().getName() + ":" + e.getMessage(), e);
		}
	}
	/**
	 * [A] 오퍼레이션명 : 후판슬라브야드 테스트 슬라브 제작
	 * 허정욱
	 * 2019.11.26
	 * 
	 * @param  JDTORecord, int
	 * @return int                execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdStockMessage(JDTORecord inRec) throws DAOException, JDTOException {
		// 레코드 선언
		JDTORecord recInPara = null;

		// 변수 선언
		String szMethodName = "updYdStockMessage";
		String szMsg        = "";		
		int intRtnVal       = 0;
		
		try {
			// 레코드 생성
			recInPara = JDTORecordFactory.getInstance().create();
			
			// 필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd407);
			
			// 쿼리실행
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdStockReg

	

	/**
	 * [A] 오퍼레이션명 : 검수테이블 등록 UPDATE_PIDEV
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStockExa_PIDEV(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String STL_NO = StringHelper.evl(inRec.getFieldString("STL_NO"), "");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {

			sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW2_PIDEV";
			count = dao.updateData(sQueryId, new Object[] { STL_NO });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return count;
	} // end of updYdStockExa
		
	
	
} // end of class
