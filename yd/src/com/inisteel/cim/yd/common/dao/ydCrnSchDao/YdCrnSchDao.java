package com.inisteel.cim.yd.common.dao.ydCrnSchDao;

import java.util.Iterator;

import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.ccommon.util.CCommUtils;
import com.inisteel.cim.yd.common.util.YdConstant;
import com.inisteel.cim.yd.common.util.YdDaoUtils;
import com.inisteel.cim.yd.common.util.YdUtils;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

/**
 *      [A] 클래스명 : 크레인스케줄 DAO
 * 
 */

public class YdCrnSchDao {
	
	// Dao Name
	private String szDaoName = getClass().getName();
	
	private YdUtils ydUtils = new YdUtils();	

	private DBAssistantDAO dbAssDao = new DBAssistantDAO();
	
	private YdDaoUtils ydDaoUtils = new YdDaoUtils();
	private YdPICommDAO	   ydPICommDAO   = new YdPICommDAO();	
	private CCommUtils	   commUtils   = new CCommUtils();
	
	//select query id
	private String szQueryIdGet1 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch";
	private String szQueryIdGet2 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschEqp_PAGE";
	//미사용 쿼리 삭제  
	//private String szQueryIdGet3 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschSch";
	private String szQueryIdGet4 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtl";
	private String szQueryIdGet5 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschCNT";
	private String szQueryIdGet6 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschCrnIdOVERID";
	private String szQueryIdGet7 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschSCHCD";
	private String szQueryIdGet56= "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschSCHCDEQPID";
	private String szQueryIdGet8 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlEQPID";

	//페이징 쿼리 추가 (이현성_20081203)
	private String szQueryIdGet9 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtl_PAGE";
	private String szQueryIdGet10 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYD_CRN_SCH_ID";
	//20090217 권오창
	private String szQueryIdGet11 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtl";
	//20090217 권오창
	private String szQueryIdGet12 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlAidY";
	//20090302 권오창 - 상태코드에 따른 적치단 JOIN
	private String szQueryIdGet13 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchYdStklyrYdStkBedStatCode";
	//20090302 권오창 - 다음번 크레인스케쥴과 재료번호를 포함하여 조회
	private String szQueryIdGet14 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlWrkBookId";
	//20090304 이현성 
	private String szQueryIdGet15 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getJspCrnSchList";
	//20090304 김진욱
	private String szQueryIdGet16 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdPrior";
	//20090304 김진욱
	private String szQueryIdGet57 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdPriorL";
	
	private String szQueryIdGet58 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByEqpIdandSchId";
	//20090304 김진욱
	private String szQueryIdGet17 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat";
	//20090305 이현성
	private String szQueryIdGet18 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getSlabYdEventWorkMatRef_PAGE";
	//20090309 권오창
	private String szQueryIdGet19 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlEQPID";
	//20090310 이현성 
	private String szQueryIdGet20 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschEqp1_PAGE";
	//20090311 이현성 
	private String szQueryIdGet21 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschEqp2_PAGE";
	//20090315  이현성 
	private String szQueryIdGet22 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByEqpIdandWBookId";
	//20090325  연은정
	private String szQueryIdGet23 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlPlateNoBookLoc";
	
	//20090330 이현성
	private String szQueryIdGet24 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnSchByWBookId";
	
	//20090330 이현성
	private String szQueryIdGet25 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnSchBySchCd";

	//20090512 김창일
	private String szQueryIdGet26 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilYdWrkRsltQty";
	//	20090514 심명순
	private String szQueryIdGet27 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getPlateYdCrnCnt";
	//	20090522 김진욱
	private String szQueryIdGet28 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByWrkbookIdDnWrLoc";
	//	20090527 권오창
	private String szQueryIdGet29 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByWrkId";
	//	20090601 김진욱
	private String szQueryIdGet30 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByYdStkLyrNo";
	//	20090615 김종건
	private String szQueryIdGet31 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getPlateYdWrkRsltQty";
	//	20090616 김진욱
	private String szQueryIdGet32 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschLDWRKBOOKID";
	private String szQueryIdGet33 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschUDWRKBOOKID";
	//	20090616 김창일
	private String szQueryIdGet34 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilYdCrnWrkMtl_PIDEV";
	//	20090701 김창일
	private String szQueryIdGet35 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchList";

	//	20090714 심명순
	private String szQueryIdGet36 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilGdsYdCrnschEqp2_PAGE";
	
	//	20090804 이현성 (스케줄 ID에 포함된 작업예약ID에 삭제되지않은 모든 스케줄을 재조회)
	private String szQueryIdGet37 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchId";
	
	private String szQueryIdGet38 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschTrans";

	// 20090907 권오창
	private String szQueryIdGet39 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnWrkMtlByWBookIdSTLNo";

	// 20090907 권오창 (크레인스케쥴ID로 크레인스케쥴을 조회하여 나온 작업예약 ID로 다시 크레인스케쥴을 조회 : N건)
	private String szQueryIdGet40 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschDescCrnSchByWbookIdToCrnSchId";

	// 20090908 석창화 (스케줄점검 From위치 Check)
	private String szQueryIdGet41 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnsch_CheckLoc";

	// 20090916 권오창 (크레인스케쥴ID와 작업진행상태로 크레인관련 데이터와 NEXT크레인스케쥴을 조회 : N건)
	private String szQueryIdGet42 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT";

	// 20090918 권오창 (크레인스케쥴ID로 작업예약ID추출 => 다시 크레인 스케쥴 조회하여 크레인스케쥴ID로 역순조회)
	private String szQueryIdGet43 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByWBookIdCrnschIdDesc";
	
	//20091015 크레인스케줄 ID에 포함된 작업예약 ID에 같은 스케줄중 작업상태가 'W','1' 상태가 아닌 다른상태값을 가지는 레코드 구하기 
	private String szQueryIdGet44 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCheckYdCrnSchIdIntoWBook"; 
	
	//차량상차크레인스케줄의 크레인설비정보 조회 - 야드구분, 동구분 : 임춘수 추가 2009.11.04
	private String szQueryIdGet45 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdEqpForCarLd";
	
	// 크레인 스케줄 정보 조회 [스케줄 ID 가 큰정보에서->작업정보순](크레인 설비상태가 'W','1' 이며 작업예약 ID로 조회함)  - 이현성 2009.11.10
	private String szQueryIdGet46 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchbyWBookinW1";
	
	//크레인리스케줄작업취소용 - 김진욱 추가 2009.11.17
	private String szQueryIdGet47 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdEqpIdYdWrkProgStat";
	
	// 후판제품 PDA 크레인현황
	private String szQueryIdGet48 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschEqp_PDA";
	//운송지시일자,운송지시순번의 재료에 대한 크레인스케줄을 조회 - 임춘수 2009.11.30 
	private String szQueryIdGet49 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschByTransOrd";
	
	// 상.하차 작업실적등록조회 1  - 이현성 2009.12.03 
	private String szQueryIdGet50 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchLdUdWrkMgt1";
	
	// 상.하차 작업실적등록조회 2  - 이현성 2009.12.03
	private String szQueryIdGet51 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchLdUdWrkMgt2";
	
	private String szQueryIdGet52 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlPlate";
	
	//재료번호로 스케줄 정보조회
	private String szQueryIdGet53 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchByStlNo";
	
	//해당 크레인스케줄보다 빠르고 해당 작업재료가 존재하는 크레인스케줄 정보 조회 - 임춘수 2010.03.11
	private String szQueryIdGet54 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCrnSchEarlierThanCrnSch";
	
	//후판제품 야드, 동별 입고스케쥴 조회
	private String szQueryIdGet55 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getPlateLSchList";
	
	//코일야드 크레인 작업조회
	private String szQueryIdGet300 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschEqp3_PAGE";
	
//	크레인작업관리 조회  2010.04.08
	private String szQueryIdGet301 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilYdCrnWorkMgt_New";
	
//	크레인작업실적LIST조회 2010.04.20
	private String szQueryIdGet302 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilCrnWrkWrList_PIDEV";	
	
	// 20090916 권오창 (크레인스케쥴ID와 작업진행상태로 크레인관련 데이터와 NEXT크레인스케쥴을 조회 : N건)
	private String szQueryIdGet303 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT2";
	// 20150721 hun 크레인무인화 분리
	private String szQueryIdGet510 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnWrkMtlNextWrkBookId_NEXT3";
	// 정정track bun read
	private String szQueryIdGet305 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdTrackingBun";
	
	private String szQueryIdGet306 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschTransNo";
	private String szQueryIdGet307 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdTrackingStlNo";

	//	크레인작업관리 조회  2010.09.08
	private String szQueryIdGet309 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilYdCrnWoWorkMgt";
	//	크레인작업관리 조회  2010.09.08
	private String szQueryIdGet311 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilYdCrnWoWorkMgtDtl";
	

	private String szQueryIdGet400 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCrnwrkmtlCoilComm";
	private String szQueryIdGet500 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCvTrackingStlNo";
	private String szQueryIdGet501 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdCvTrackingStlNo1";
	
	private String szQueryIdGet502 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.pPlateYdGetBedempty";

	private String szQueryIdGet503 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getCoilGdsYdWrkRsltQty_PIDEV";
	
	//임가공 이송지시 대상
	private String szQueryIdGet504 = "yd.facilitystatus.facilityinquiry.CraneSchDAO.getYmPoFrtoInfo_PIDEV";

	//후판상차완료 여부 판단
	private String szQueryIdGet505 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getWbookIdEndCheck";
	
	private String szQueryIdGet506 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSangchendChk";
	//크레인 스케줄 중복 체크 
	private String szQueryIdGet507 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschDuplicateChk";
	
	private String szQueryIdGet508 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarSangchendChk2";
	
	private String szQueryIdGet509 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdPlateHyundai3Info";
	
	private String szQueryIdGet601 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdPlateReSchAbleInfo";
	private String szQueryIdGet602 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdPlateReSchCrRtList";
	
	private String szQueryIdGet603 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStatPlusS";

	private String szQueryIdGet604 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnschYdStockYdCrnSchByWBookIdPlusS";
	
	private String szQueryIdGet605 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdWrkProgStat1S";
	
	private String szQueryIdGet606 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnSchEqpIdPriorByTR";
	
	private String szQueryIdGet607 = "com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarHachendChk";
	
	//현재 크레인 작업 인터락 여부 
	private String szQueryIdGet701 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnWrkInterlockSectYN";
	//현재 크레인 작업 인터락 여부 by schid
	private String szQueryIdGet702 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.getYdCrnWrkInterlockSectYNbySchID";
	
	 
	//insert query id
	private String szQueryIdIns1 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.insYdCrnsch";
	
	//insert query id
	private String szQueryIdIns2 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.insYdCrnschHist";
	
	//insert query id
	private String szQueryIdIns3 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.insYdCrnWrmtlHist";
	
	//update query id
	private String szQueryIdUpd1 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnsch";
	private String szQueryIdUpd2 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnschSchcd";
	// 보류처리 
	private String szQueryIdUpd3 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnDelay";
	// 보류해제 처리 
	private String szQueryIdUpd4 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnCancleDelay";
	// 크레인리스케줄(주작업크레인) 2009.10.26 김진욱
	private String szQueryIdUpd5 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnReSchWrkCrn";
	// 크레인리스케줄(보조작업크레인) 2009.10.26 김진욱
	private String szQueryIdUpd6 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnReSchAltCrn";
	// 권하위치 변경시 하위 권하분리(X)스케쥴 From위치 변경  2010.09.09  윤재광
	private String szQueryIdUpd7 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnXSchFromLoc";
	// 동별 크레인 전체사용 여부에 따른 스케쥴 코드 변경(스케쥴) 2010.11.09  윤재광
	private String szQueryIdUpd8 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnSchCd";
	// 동별 크레인 전체사용 여부에 따른 스케쥴 코드 변경(작업예약) 2010.11.09  윤재광
	private String szQueryIdUpd9 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkCd";
	// 동별 크레인 전체사용 여부에 따른  설비 코드 변경(스케쥴) 2010.11.09  윤재광
	private String szQueryIdUpd10 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnEqpCd";
	// 동별 크레인 전체사용 여부에 따른  설비 코드 변경(스케쥴기준) 2010.11.09  윤재광
	private String szQueryIdUpd11 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnRuleCd";
	// 동별 크레인 전체사용 여부에 따른  설비 코드 변경(스케쥴기준) 2010.11.09  윤재광
	private String szQueryIdUpd12 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnRuleCd_01";
	// 크레인스케쥴의 설비/스케쥴코드 변경 2010.11.09  윤재광
	private String szQueryIdUpd13 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnSchCd_01";
	// 크레인스케쥴의 설비코드 변경 2014.12.22  윤재광
	private String szQueryIdUpd14 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnEqpId_01";
	
	private String szQueryIdUpd301 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnprior";
	
	private String szQueryIdUpd302 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkProgStat";
	
	private String szQueryIdUpd303 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkSidedelyn";
	
	private String szQueryIdUpd304 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.updYdCrnWrkReject";
	
	private String szQueryIdDel1 = "com.inisteel.cim.yd.dao.ydcrnschdao.YdCrnschDao.delYdCrnSchInfo";
	
/*------------------------------------- SELECT -------------------------------------------*/

	/**
	 *      [A] 오퍼레이션명 : 크레인스케줄 SELECT
	 *      
	 * @param  JDTORecord inRec         parameter record
	 *         JDTORecordSet outRecSet  result recordSet
	 *         int intGp                구분(0:YD_CRN_SCH_ID, 
	 *         						        1:YD_GP,YD_BAY_GP,YD_EQP_ID,YD_SCH_CD, 
	 *                                      2:Empty , 
	 *                                      3:JOIN TB_YD_CRNWRKMTL[K],
	 *                                      4:END COUNT[K], 
	 *                                      5:YD_CRN_SCH_ID,YD_SCH_CD[OVER],
	 *                                      6:YD_SCH_CD, 
	 *                                      7:JOIN TB_YD_CRNWRKMTL[YD_EQP_ID],
	 *                                      8:YD_WRK_HDS_DD,YD_WRK_DUTY,YD_SCH_CD ,ORD_YEOJAE_GP,
	 *                                      9:YD_CRN_SCH_ID[GET SEQUENCE],
	 *                                      10:YD_AIM_RT_GP,YD_CRN_SCH_ID,
	 *                                      11:YD_CRN_SCH_ID
	 *                                      12:szQueryIdGet30
	 *                                      13:YD_CRN_SCH_ID1, YD_WRK_PROG_STAT, YD_CRN_SCH_ID2, V_YD_CRN_SCH_ID3
	 *                                      14:YD_GP ,YD_BAY_GP
	 *                                      15:YD_EQP_ID
	 *                                      16:YD_EQP_ID
	 *                                      17:YD_WRK_HDS_DD,YD_WRK_DUTY,YD_SCH_CD ,ORD_YEOJAE_GP ,PAGE_CNT1 ,ROW_CNT1, _PAGE_CNT2 ,ROW_CNT2
	 *                                      18:YD_EQP_ID
	 *                                      19:YD_GP,YD_BAY_GP,YD_EQP_ID,YD_SCH_CD, PAGE_CNT1 ,ROW_CNT1, _PAGE_CNT2 ,ROW_CNT2
	 *                                      20:YD_GP,YD_BAY_GP,YD_EQP_ID,YD_SCH_CD, PAGE_CNT1 ,ROW_CNT1, _PAGE_CNT2 ,ROW_CNT2
	 *                                      21:YD_EQP_ID , YD_YD_WBOOK_ID
	 *                                      22:YD_CRN_SCH_ID
	 *                                      23:YD_WBOOK_ID ,YD_WRK_PROG_STAT
	 *                                      24:YD_SCH_CD, YD_WRK_PROG_STAT
	 *                                      27:YD_WBOOK_ID, YD_EQP_GP
	 *                                      28:YD_WBOOK_ID
	 *										29:YD_CRN_SCH_ID
	 * 	 									30:YD_GP, YD_WRK_HDS_DD, YD_WRK_DUTY
	 *										31:
	 *										32:
	 *										33:
	 *										34:
	 *										35
	 *										36: YD_CRN_SCH_ID
	 *										38:YD_WBOOK_ID, STL_NO
	 *										39:YD_CRN_SCH_ID
	 *										41:YD_CRN_SCH_ID, YD_WRK_PROG_STAT
	 *										42:YD_CRN_SCH_ID
	 *										43:YD_CRN_SCH_ID
	 *										44:YD_GP, YD_BAY_GP
	 *										45:YD_WBOOK_ID
	 *										46:YD_EQP_ID, YD_WRK_PROG_STAT
	 *                                      47:YD_EQP_ID
	 *                                      48:YD_GP, TRANS_ORD_DATE, TRANS_ORD_SEQNO
	 *                                      49:V_YD_GP , V_YD_EQP_ID, YD_SCH_CD[ 입출고구분 한자리만]
	 *                                      50:V_YD_WBOOK_ID
	 *                                      51:YD_CRN_SCH_ID
	 *                                      52:STL_NO
	 *                                      53:YD_CRN_SCH_ID, YD_WBOOK_ID, STL_NO
	 *                                      )    
	 * @return int                    record count:성공, 0:data not found, -2:parameter error
	 * @throws DAOExceptione
	 * @throws JDTOException 
	 */	
	public int getYdCrnsch(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdCrnsch";
		JDTORecordSet rsTemp = null;
		int intRtnVal = 0;
		boolean blnChk_Field = true;
		String szMsg = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			//parameter check
			blnChk_Field = this.chkPara_getYdCrnsch(recPara, intGp);
			
			//parameter error return
			if (!blnChk_Field)
				return intRtnVal = -2;
			
			if(intGp == 0)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet1);
			else if(intGp == 1)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet2);
//			else if(intGp == 2)
//				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet3);
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
			else if(intGp == 34)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet35);
			else if(intGp == 35)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet36);
			else if(intGp == 36)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet37);
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
			else if(intGp == 300)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet300);
			else if(intGp == 301)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet301);
			else if(intGp == 302)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet302);
			else if(intGp == 303)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet303);
			else if(intGp == 305)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet305);
			else if(intGp == 306)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet306);
			else if(intGp == 307)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet307);
			else if(intGp == 309)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet309);
			else if(intGp == 311)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet311);
			else if(intGp == 400)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet400);
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
			else if(intGp == 507)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet507);
			else if(intGp == 508)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet508);
			else if(intGp == 509)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet509);
			else if(intGp == 510)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet510);
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
			else if(intGp == 701)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet701);
			else if(intGp == 702)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet702);

			String mthdNm = "YdCrnSchDao.getYdCarsch";
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
	} //end of getYdCrnsch
	
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
	public int getYdCrnResch(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		String szMethodName = "getYdCrnResch";
		JDTORecordSet rsTemp = null;
		JDTORecord recPara = null;

		try {
			//필드명 변환 (필드명 -> V_필드명)
			recPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			if(intGp == 1) 
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet601);
			else if(intGp == 2)
				recPara.setField("JSPEED_QUERY_ID", szQueryIdGet602);			
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
				return 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return rsTemp.size();
	} //end of getYdCrnsch
	
	/**
	 *      [A] 오퍼레이션명 : SELECT parameter Check
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_CRN_SCH_ID, 
	 *                              1:YD_GP,YD_BAY_GP,YD_EQP_ID,YD_SCH_CD  
     *                              2:Empty, 
     *                              3:JOIN TB_YD_CRNWRKMTL[K],
     *                              4:END COUNT[K], 
     *                              5:YD_CRN_SCH_ID,YD_SCH_CD[OVER],
     *                              6:YD_SCH_CD, 
     *                              7:JOIN TB_YD_CRNWRKMTL[YD_EQP_ID],
     *                              8:YD_WRK_HDS_DD,YD_WRK_DUTY,YD_SCH_CD ,ORD_YEOJAE_GP,
     *                              9:YD_CRN_SCH_ID[GET SEQUENCE]
     *                              10:YD_AIM_RT_GP,YD_CRN_SCH_ID,
	 *                              11:YD_CRN_SCH_ID
	 *                              12:YD_CRN_SCH_ID
	 *                              13:YD_CRN_SCH_ID1, YD_WRK_PROG_STAT, YD_CRN_SCH_ID2, V_YD_CRN_SCH_ID3
	 *                              14:YD_GP,YD_BAY_GP
	 *                              15:YD_EQP_ID
	 *                              16:YD_EQP_ID
	 *                              17:YD_WRK_HDS_DD,YD_WRK_DUTY,YD_SCH_CD ,ORD_YEOJAE_GP ,PAGE_CNT1 ,ROW_CNT1, _PAGE_CNT2 ,ROW_CNT2
	 *                              18:YD_EQP_ID
	 *                              19:YD_GP,YD_BAY_GP,YD_EQP_ID,YD_SCH_CD, PAGE_CNT1 ,ROW_CNT1, _PAGE_CNT2 ,ROW_CNT2
	 *                              20:YD_GP,YD_BAY_GP,YD_EQP_ID,YD_SCH_CD, PAGE_CNT1 ,ROW_CNT1, _PAGE_CNT2 ,ROW_CNT2
	 *                              21:YD_EQP_ID , YD_WBOOK_ID)
	 *                              22:YD_CRN_SCH_ID
	 *                              23:YD_WBOOK_ID,YD_WRK_PROG_STAT
	 *                              24:YD_SCH_CD, YD_WRK_PROG_STAT
	 *                              27:YD_WBOOK_ID, YD_EQP_GP
	 *                              28:YD_WBOOK_ID
	 *                              29:YD_CRN_SCH_ID
	 *                              30:YD_GP, YD_WRK_HDS_DD, YD_WRK_DUTY
	 *                              31:YD_WBOOK_ID
	 *                              32:YD_WBOOK_ID
	 *                              33:
	 *                              34:
	 *                              35:
	 *                              36 :YD_CRN_SCH_ID
 	 *								38:YD_WBOOK_ID, STL_NO       
 	 *								39:YD_CRN_SCH_ID
 	 *								41:YD_CRN_SCH_ID, YD_WRK_PROG_STAT
	 *				 				42:YD_CRN_SCH_ID
	 *								43:YD_CRN_SCH_ID
	 *								44:YD_GP, YD_BAY_GP
	 *								45:YD_WBOOK_ID
	 *                              47:YD_GP,YD_BAY_GP,YD_EQP_ID,YD_SCH_CD, PAGE_CNT1 ,ROW_CNT1, _PAGE_CNT2 ,ROW_CNT2
	 *                              
	 *                              48:YD_GP, TRANS_ORD_DATE, TRANS_ORD_SEQNO
	 *                              49:YD_GP , YD_EQP_ID, YD_SCH_CD[ 입출고구분 한자리만]
	 *                              50:YD_WBOOK_ID
	 *                              51:YD_CRN_SCH_ID
	 *                              52:STL_NO
	 *                              53:YD_CRN_SCH_ID, YD_WBOOK_ID, STL_NO
 	 *                              )                           
	 *                              
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException 
	 */	
	public boolean chkPara_getYdCrnsch(JDTORecord inRec, int intGp) throws JDTOException  {
		String szFieldName = null;
		boolean blnErr = true;

		try {
			if (intGp == 0 || intGp == 3 || intGp == 11 || intGp == 12 || intGp == 22 || intGp == 29 || intGp == 39 || intGp == 42  || intGp == 43  ) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
			} else if (intGp == 1) {
			
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);				
			}else if (intGp == 2) {
				// Empty
                	
            }else if (intGp == 4 || intGp == 28 || intGp == 31 || intGp == 32 || intGp == 34 || intGp == 45) {
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			
			} else if (intGp == 5) {
				
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				
			} else if (intGp == 6) {
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				
			} else if (intGp == 7 || intGp == 15 || intGp == 16 || intGp == 18 || intGp == 606) {
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				
			} else if (intGp == 8) {
				
				szFieldName = "V_YD_WRK_HDS_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_WRK_DUTY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_ORD_YEOJAE_GP";
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
				
			} else if (intGp == 9) {
				
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
			} else if (intGp == 10) {
				szFieldName = "V_YD_AIM_RT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 3, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if (intGp == 13) {
				szFieldName = "V_YD_CRN_SCH_ID1";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PROG_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
	
				szFieldName = "V_YD_CRN_SCH_ID2";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
	
				szFieldName = "V_YD_CRN_SCH_ID3";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			} else if (intGp == 14) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			} else if (intGp == 17) {
				
				szFieldName = "V_YD_WRK_HDS_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_WRK_DUTY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_ORD_YEOJAE_GP";
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
				
			}  else if (intGp == 19 || intGp ==20 || intGp == 35 || intGp == 47) {
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
							
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
				
			} else if (intGp == 21) {
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			}  else if (intGp == 23) {
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				
				szFieldName = "V_YD_WRK_PROG_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			} else if (intGp == 24) {
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PROG_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			} else if(intGp == 25) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_HDS_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_DUTY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			} else if(intGp == 27){
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_EQP_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 1, 'S', 0, 0);
			} else if(intGp == 30){
				
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_GNT_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_YD_WRK_HDS_DD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
											
				szFieldName = "V_YD_WRK_DUTY";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				
				szFieldName = "V_STL_PROG_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
			} else if (intGp == 33) {
				szFieldName = "V_YD_STK_COL_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);				
			} else if (intGp == 36){
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
			
			} else if (intGp == 38){
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);

			
			} else if (intGp == 40) {
				szFieldName = "V_YD_UP_WO_LOC";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_DN_WO_LOC";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
			} else if (intGp == 41) {
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PROG_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
			} else if (intGp == 44) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
			} else if (intGp == 46) {
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WRK_PROG_STAT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

			}	 else if (intGp == 48) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_TRANS_ORD_DATE";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
//				szFieldName = "V_TRANS_ORD_SEQNO";
//				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 4, 1, 'S', 0, 0);
//				if (!blnErr) return blnErr;

			}	else if (intGp == 49) {
				szFieldName = "V_YD_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_PAGE_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'P', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_ROW_CNT";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 9, 1, 'R', 0, 0);
				if (!blnErr) return blnErr;

			} else if (intGp == 50) {
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;

			} else if (intGp == 51) {
				
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
			} else if (intGp == 52) {
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
			} else if (intGp == 53) {
				//53:YD_CRN_SCH_ID, YD_WBOOK_ID, STL_NO
				szFieldName = "V_YD_CRN_SCH_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;
				
				szFieldName = "V_YD_WBOOK_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 1, 'S', 0, 0);
				if (!blnErr) return blnErr;
				
				szFieldName = "V_STL_NO";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 11, 1, 'S', 0, 0);	
				if (!blnErr) return blnErr;

			}  else if (intGp == 300) {
				
				szFieldName = "V_YD_BAY_GP";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'S', 0, 0);
		
				szFieldName = "V_YD_EQP_ID";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			
				szFieldName = "V_YD_SCH_CD";
				blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
						
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
			}
//			사용
			else if (intGp == 301) {}
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} // end of chkPara_getYdCrnsch
	

	
/*------------------------------------- INSERT -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int insYdCrnsch(JDTORecord inRec) throws DAOException, JDTOException {
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
	} // end of insYdCrnsch
	
	
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄HISTORY INSERT
	 * 
	 * @param JDTORecord inRec parameter record
	 * @return int             execution count, -2:parameter error
	 * @throws DAOException
	 * @throws JDTOException 
	 */	
	public int insYdCrnschHist(JDTORecord inRec, int chk) throws DAOException, JDTOException {
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
			if(chk == 0 ){
				recPara.setField("JSPEED_QUERY_ID", szQueryIdIns2);
			}else {
				recPara.setField("JSPEED_QUERY_ID", szQueryIdIns3);
			}
	
			//query execute
			intRtnVal = dbAssDao.trtProcess(recPara);
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of insYdCrnschHist
	
	/**
	 *      [A] 오퍼레이션명 : parameter Check
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
			
			szFieldName = "V_YD_EQP_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 1, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_BAY_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_SCH_CD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_SCH_ST_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_SCH_REQ_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_SCH_PRIOR";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_EQP_WRK_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr)
				return blnErr;
	
			szFieldName = "V_YD_WRK_PROG_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
	
			szFieldName = "V_YD_WBOOK_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "YD_SCH_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WORD_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_CMPL_DT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 0, 3, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_HDS_DD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_DUTY";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_WRK_PARTY";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_MAIN_WRK_MTL_SH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_AID_WRK_MTL_SH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_AID_WRK_UPDN_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TO_LOC_DCSN_MTD";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_TO_LOC_GUIDE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_WRK_SH";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 2, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_WRK_WT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_WRK_T";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'D', 4, 3);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_WRK_MAX_W";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 6, 2, 'D', 4, 1);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_EQP_WRK_MAX_L";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CRN_SB_CTL_H";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_CRN_GRAB_USE_RULE_ID";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 18, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_LAYER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_LOC_XAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_XAXIS_GAP_MAX";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_XAXIS_GAP_MIN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_LOC_YAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_LOC_YAXIS1";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_LOC_YAXIS2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_YAXIS_GAP_MAX";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_YAXIS_GAP_MIN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_LOC_ZAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_ZAXIS_GAP_MAX";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WO_ZAXIS_GAP_MIN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_LAYER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_LOC_XAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_XAXIS_GAP_MAX";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_XAXIS_GAP_MIN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_LOC_YAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_LOC_YAXIS1";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_LOC_YAXIS2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_YAXIS_GAP_MAX";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_YAXIS_GAP_MIN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_LOC_ZAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_ZAXIS_GAP_MAX";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WO_ZAXIS_GAP_MIN";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WR_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WR_LAYER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WR_XAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WR_YAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WR_YAXIS1";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WR_YAXIS2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_UP_WR_ZAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WR_LOC";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 8, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WR_LAYER";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 3, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WRK_ACT_GP";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WR_XAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 7, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WR_YAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WR_YAXIS1";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WR_YAXIS2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WR_ZAXIS";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 5, 2, 'L', 0, 0);
			
			szFieldName = "V_YD_UP_WRK_MODE2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_DN_WRK_MODE2";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
			szFieldName = "V_YD_L2_REQUEST_STAT";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;

			szFieldName = "V_UP_ROTATION_ANGLE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;

			szFieldName = "V_DOWN_ROTATION_ANGLE";
			blnErr = ydDaoUtils.chkField(inRec, szFieldName, 1, 2, 'S', 0, 0);
			if (!blnErr) return blnErr;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
		return blnErr;
	} //end of chkParameter
		
	
/*------------------------------------- UPDATE -------------------------------------------*/
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0:YD_CRN_SCH_ID, 1:YD_CRN_SCH_ID, YD_SCH_CD)
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCrnsch(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		String szMethodName = "updYdCrnsch";
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
			intRtnVal = this.getYdCrnsch(inRec, outRecSet, 0);
			
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
			else if (intGp == 1)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd2);
			else if (intGp == 2)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			else if (intGp == 3)
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			else if (intGp == 303)
				//DEL_YN만 제외
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd303);
			else if (intGp == 304)
				//DEL_YN만 제외
				recOutPara.setField("JSPEED_QUERY_ID", szQueryIdUpd304);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recOutPara);
			
			//execution error return
			if (intRtnVal <= 0) intRtnVal = -3;
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCrnsch
	
	
	
	/**
	 *      [A] 오퍼레이션명 : UPDATE parameter mapping
	 * 
	 * @param JDTORecord inRec
	 *        JDTORecord outRec
	 *        JDTORecord updRec
	 * @return void
	 * @throws JDTOException 
	 */		
	public void dataMapping(JDTORecord inRec, JDTORecord outRec) throws JDTOException {
		String szFieldName = null;
		
		try {
			szFieldName = "V_YD_CRN_SCH_ID";
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
			
			szFieldName = "V_YD_EQP_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_BAY_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_CD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_ST_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_SCH_REQ_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_SCH_PRIOR";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_EQP_WRK_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_WRK_PROG_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
	
			szFieldName = "V_YD_WBOOK_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_SCH_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WORD_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_CMPL_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_CMPL_DT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_HDS_DD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_DUTY";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_WRK_PARTY";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_MAIN_WRK_MTL_SH";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_AID_WRK_MTL_SH";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_AID_WRK_UPDN_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TO_LOC_DCSN_MTD";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_TO_LOC_GUIDE";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_SH";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_WT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_T";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_MAX_W";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_EQP_WRK_MAX_L";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_SB_CTL_H";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_CRN_GRAB_USE_RULE_ID";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LOC";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LAYER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LOC_XAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_XAXIS_GAP_MAX";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_XAXIS_GAP_MIN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LOC_YAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LOC_YAXIS1";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LOC_YAXIS2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_YAXIS_GAP_MAX";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_YAXIS_GAP_MIN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_LOC_ZAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_ZAXIS_GAP_MAX";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WO_ZAXIS_GAP_MIN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LOC";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LAYER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LOC_XAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_XAXIS_GAP_MAX";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_XAXIS_GAP_MIN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LOC_YAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LOC_YAXIS1";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LOC_YAXIS2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_YAXIS_GAP_MAX";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_YAXIS_GAP_MIN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_LOC_ZAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_ZAXIS_GAP_MAX";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WO_ZAXIS_GAP_MIN";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_LOC";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_LAYER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WRK_ACT_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_XAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_YAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_YAXIS1";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_YAXIS2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WR_ZAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_LOC";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_LAYER";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WRK_ACT_GP";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_XAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_YAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_YAXIS1";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_YAXIS2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WR_ZAXIS";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_UP_WRK_MODE2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_DN_WRK_MODE2";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
			szFieldName = "V_YD_L2_REQUEST_STAT";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_UP_ROTATION_ANGLE";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);

			szFieldName = "V_DOWN_ROTATION_ANGLE";
			ydDaoUtils.mappingData(inRec, outRec, szFieldName);
			
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new JDTOException(szDaoName + e.getMessage(), e);
		}
	} // end of dataMapping
	
	
	
	
	

	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE (보류/해제)
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0 : 보류 1: 해제 )
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCrnschDelay(JDTORecord inRec, int intGp) throws DAOException, JDTOException {

		String szMethodName = "updYdCrnschDelay";
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
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd3);
			else if (intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd4);
			else if (intGp == 301)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd301);
			else if (intGp == 302)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd302);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCrnschDelay
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE (보류/해제)
	 * 
	 * @param  JDTORecord inRec parameter record
	 *         int        intGp 구분(0 : 보류 1: 해제 )
	 * @return int              execution count(성공), 0:data not found, -1:duplicate data, -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCrnschReSch(JDTORecord inRec, int intGp) throws DAOException, JDTOException {
		
		String szMethodName = "updYdCrnschReSch";
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
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd5);
			else if (intGp == 1)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd6);
			else if (intGp == 2)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd8);
			else if (intGp == 3)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd9);
			else if (intGp == 4)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd10);
			else if (intGp == 5)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd11);
			else if (intGp == 6)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd12);
			else if (intGp == 7)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd13);
			else if (intGp == 8)
				recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd14);
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCrnschDelay
	
	/**
	 *      [A] 오퍼레이션명 : 야드크레인스케줄 UPDATE
	 * 
	 * @throws DAOException
	 * @throws JDTOException 
	 */		
	public int updYdCrnXSchFromLoc(JDTORecord inRec) throws DAOException, JDTOException {
		
		String szMethodName = "updYdCrnXSchFromLoc";
		String szMsg = null;
		
		int intRtnVal = 0;
			
		try {
			//변환용 레코드
			JDTORecord recInPara = null;
			
			recInPara = JDTORecordFactory.getInstance().create();
			
			//필드명 변환 (필드명 -> V_필드명)
			recInPara = ydDaoUtils.conversionFieldname(inRec, 0);
			
			recInPara.setField("JSPEED_QUERY_ID", szQueryIdUpd7);
			
			//query execute
			intRtnVal = dbAssDao.trtProcess(recInPara);			
		
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException(szDaoName + e.getMessage(), e);
		}
		return intRtnVal;
	} // end of updYdCrnschDelay
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






