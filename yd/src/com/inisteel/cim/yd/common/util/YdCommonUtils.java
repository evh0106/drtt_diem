/**
 * @(#)YdCommonUtils.java
 * 
 * @version			1.0
 * @author 			임춘수
 * @date			2009.
 * 
 * @description		YD에서 사용되는 공통업무를 정의하는 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2009         임춘수      임춘수      최초 등록
 * V1.01  2012.12.07   조병기      조병기     htY4_RT_ZONE_BED 에 2후판 Book-out 코드 추가
 * V1.02  2012.12.17   조병기      조병기     메소드 sndStrPosSpecToL2 에 "2후판제품창고야드"관련 if문 추가
 * V1.03  2012.12.21   조병기      조병기     메소드 sndStockSpecToL2 에 "2후판제품창고야드" 관련 if문 추가
 * V1.04  2012.09.04   추관식      추관식     htY4_RT_ZONE_BED, htY4_BOOK_OUT_LOC에 G R/T 추가 및 로직 수정
 */

package com.inisteel.cim.yd.common.util;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import jspeed.base.ejb.EJBConnector;
import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.dao.YdPlateCommDAO;
import com.inisteel.cim.yd.common.dao.ptMSlabCommDao.PtMSlabCommDao;
import com.inisteel.cim.yd.common.dao.ptSlabCommDao.PtSlabCommDao;
import com.inisteel.cim.yd.common.dao.ptStlFrtoMoveDao.PtStlFrtoMoveDao;
import com.inisteel.cim.yd.common.dao.ydCarFtmvMtlDao.YdCarFtmvMtlDao;
import com.inisteel.cim.yd.common.dao.ydCarSchDao.YdCarSchDao;
import com.inisteel.cim.yd.common.dao.ydCarSpecDao.YdCarSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnSchDao.YdCrnSchDao;
import com.inisteel.cim.yd.common.dao.ydCrnSpecDao.YdCrnSpecDao;
import com.inisteel.cim.yd.common.dao.ydCrnWrkMtlDao.YdCrnWrkMtlDao;
import com.inisteel.cim.yd.common.dao.ydEqpDao.YdEqpDao;
import com.inisteel.cim.yd.common.dao.ydMsgInfoMgtDao.YdMsgInfoMgtDao;
import com.inisteel.cim.yd.common.dao.ydPrepMtlDao.YdPrepMtlDao;
import com.inisteel.cim.yd.common.dao.ydPrepSchDao.YdPrepSchDao;
import com.inisteel.cim.yd.common.dao.ydSchRuleDao.YdSchRuleDao;
import com.inisteel.cim.yd.common.dao.ydStkBedDao.YdStkBedDao;
import com.inisteel.cim.yd.common.dao.ydStkColDao.YdStkColDao;
import com.inisteel.cim.yd.common.dao.ydStkLyrDao.YdStkLyrDao;
import com.inisteel.cim.yd.common.dao.ydStockDao.YdStockDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookDao.YdWrkbookDao;
import com.inisteel.cim.yd.common.dao.ydWrkbookMtlDao.YdWrkbookMtlDao;
import com.inisteel.cim.yd.common.delegate.YdDeleComm;
import com.inisteel.cim.yd.common.delegate.YdDelegate;
import com.inisteel.cim.yd.common.rule.GetBreRule1;
import com.inisteel.cim.yd.common.rule.GetBreRule2;
import com.inisteel.cim.yd.common.rule.GetBreRule3;
import com.inisteel.cim.yd.common.rule.GetBreRule4;
import com.inisteel.cim.yd.common.rule.GetBreRule5;
import com.inisteel.cim.yd.common.rule.GetBreRule6;
import com.inisteel.cim.yd.common.util.plate.PlateGdsYdUtil;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

public class YdCommonUtils {
	//클래스명
	private static String szClassName = YdCommonUtils.class.getName();
	//private static YdCommonUtils _instance = new YdCommonUtils();
	//DAO 유틸리티 클래스
	private static YdDaoUtils ydDaoUtils = new YdDaoUtils();
	//유틸리티클래스
	private static YdUtils ydUtils = new YdUtils();
	//후판제품 RT_ZONE_NO --> 저장위치 변환 정보
	private static final Hashtable htY4_RT_ZONE_BED = new Hashtable();
	//BOOK-OUT LOC --> PILE TRANSFER 위치정보로 변환
	private static final Hashtable htY4_RT2TF_ZONE = new Hashtable();
	//BOOK-OUT LOC --> BOOK-OUT RT 정보로 변환
	private static final Hashtable htY4_BOOK_OUT_LOC = new Hashtable();
	//A BOOK-OUT LOC --> B BOOK-OUT 정보로 변환
	private static final Hashtable htY4_CHG_BBOOK_OUT_LOC = new Hashtable();
	
	//A BOOK-OUT LOC --> C BOOK-OUT 정보로 변환 (2013.08.04 신규 생성)
	private static final Hashtable htY4_CHG_CBOOK_OUT_LOC = new Hashtable();
	
	//C BOOK-OUT LOC --> A BOOK-OUT 정보로 변환 (2013.08.27 신규 생성)
	private static final Hashtable htY4_CHG_ABOOK_OUT_LOC = new Hashtable();
	
	//C BOOK-OUT LOC --> B BOOK-OUT 정보로 변환 (2013.08.27 신규 생성)
	private static final Hashtable htY4_CHG_C2BBOOK_OUT_LOC = new Hashtable();
	
	//TF BOOK-OUT LOC --> RT BOOK-OUT 정보로 변환
	private static final Hashtable ht_TF_2_RT_STKLOC = new Hashtable();	
	
	//L3야드저장위치 --> L2 ZONE NO 정보로 변환
	private static final Hashtable htY9_CHG_L2BOOK_OUT_LOC = new Hashtable();
	
	//L2 ZONE NO  --> L3야드저장위치 정보로 변환
	private static final Hashtable htY9_CHG_L3BOOK_OUT_LOC = new Hashtable();
	
	//L2 크레인 NO  --> L3 크레인 NO 정보로 변환
	private static final Hashtable htY9_CHG_L3CRANE_INFO = new Hashtable();
	
	//L2 크레인 NO  --> L3 저장위치 정보로 변환
	private static final Hashtable htY9_CHG_L3LOC_INFO = new Hashtable();
	
	//BOOK-OUT LOC --> YD_STR_LOC 정보로 변환
	private static final Hashtable htY4_BOOK_OUT_TO_YD_STR = new Hashtable();
	//Level2 Port정보 저장 맵객체
	public static final Hashtable h_MPCodeL2 = new Hashtable();

	// 야드(설비+BED.No) => 조업(설비)
	public static final Hashtable h_hstEqpGpMatch = new Hashtable();
	// 조업(설비)        => 야드(설비+BED.No)
	public static final Hashtable h_hRvsstEqpGpMatch = new Hashtable();
	//
	public static final YdMsgInfoMgtDao ydMsgInfoMgtDao = new YdMsgInfoMgtDao();
	
	public static final YdDelegate ydDelegate = new YdDelegate();
	
	static {
//////////////////////////////////////////////////////////////////
		//		후판제품 BOOK-OUT위치 --> 저장위치 변환 정보 초기화
		//////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------
		//	A LINE(ON-LINE)
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("56010", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE", "10"});
		htY4_RT_ZONE_BED.put("56020", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE", "20"});
		//------------------------------------------------
		//	가적장 정보 추가 - 임춘수 2010.01.21
		//------------------------------------------------
		htY4_RT_ZONE_BED.put(YdConstant.TEMPSTK_LOC_A_BOOK_OUT_01, new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "AGJ01", "06"});						//가적장06베드
		htY4_RT_ZONE_BED.put(YdConstant.TEMPSTK_LOC_A_BOOK_OUT_02, new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "AGJ01", "16"});						//가적장16베드
		//------------------------------------------------
		htY4_RT_ZONE_BED.put("56030", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE", "30"});
		htY4_RT_ZONE_BED.put("56040", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE", "40"});
		htY4_RT_ZONE_BED.put("56050", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRE", "50"});
		htY4_RT_ZONE_BED.put("56060", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRE", "60"});
		htY4_RT_ZONE_BED.put("56070", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRE", "70"});
		htY4_RT_ZONE_BED.put("56075", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRE", "F0"});
		htY4_RT_ZONE_BED.put("56080", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRE", "80"});
		//----------------------------------------------------------------
//2CH DONG_INSERT : OK		
//		htY4_RT_ZONE_BED.put("56086", new String[] {"KERTRA", "90"});
//		htY4_RT_ZONE_BED.put("56088", new String[] {"KERTRA", "A0"});
//		htY4_RT_ZONE_BED.put("56090", new String[] {"KFRTRA", "B0"});
//		htY4_RT_ZONE_BED.put("56092", new String[] {"KFRTRA", "C0"});

//3CH DONG_INSERT : OK		
		htY4_RT_ZONE_BED.put("56085", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE", "90"});
		htY4_RT_ZONE_BED.put("56086", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE", "A0"});
		htY4_RT_ZONE_BED.put("56087", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE", "B0"});
		htY4_RT_ZONE_BED.put("56088", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE", "C0"});

// ONLINE F동 생성시		
//		htY4_RT_ZONE_BED.put("56090", new String[] {"KFRTRA", "D0"});
//		htY4_RT_ZONE_BED.put("56092", new String[] {"KFRTRA", "E0"});

		//htY4_RT_ZONE_BED.put("56090", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRE", "80"});
		htY4_RT_ZONE_BED.put("56090", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRE", "D0"});
		//htY4_RT_ZONE_BED.put("56092", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRE", "80"});
		htY4_RT_ZONE_BED.put("56092", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRE", "E0"});
		
		//----------------------------------------------------------------
		//	B LINE(OFF-LINE) 
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("58010", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRF", "10"});
		htY4_RT_ZONE_BED.put("58020", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRF", "20"});
		htY4_RT_ZONE_BED.put("58030", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRF", "30"});
		htY4_RT_ZONE_BED.put("58040", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRF", "40"});
		htY4_RT_ZONE_BED.put("58050", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRF", "50"});
		htY4_RT_ZONE_BED.put("58060", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRF", "60"});
		htY4_RT_ZONE_BED.put("58070", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRF", "70"});
		htY4_RT_ZONE_BED.put("58075", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRF", "F0"});
		htY4_RT_ZONE_BED.put("58080", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRF", "80"});
		//----------------------------------------------------------------
//DONG_INSERT : OK		
//		htY4_RT_ZONE_BED.put("58086", new String[] {"KERTRB", "90"});
//		htY4_RT_ZONE_BED.put("58088", new String[] {"KERTRB", "A0"});
//		htY4_RT_ZONE_BED.put("58090", new String[] {"KFRTRB", "B0"});
//		htY4_RT_ZONE_BED.put("58092", new String[] {"KFRTRB", "C0"});

//3CH DONG_INSERT : OK
		htY4_RT_ZONE_BED.put("58085", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF", "90"});
		htY4_RT_ZONE_BED.put("58086", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF", "A0"});
		htY4_RT_ZONE_BED.put("58087", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF", "B0"});
		htY4_RT_ZONE_BED.put("58088", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF", "C0"});
		htY4_RT_ZONE_BED.put("58090", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRF", "D0"});
		htY4_RT_ZONE_BED.put("58092", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRF", "E0"});
		
		
		//----------------------------------------------------------------
		//	A LINE(ON-LINE) TRANSFER
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("56106", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ATF02", "06"});
		htY4_RT_ZONE_BED.put("56105", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ATF01", "05"});
		htY4_RT_ZONE_BED.put("56116", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ATF02", "16"});
		htY4_RT_ZONE_BED.put("56115", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ATF01", "15"});
		
		htY4_RT_ZONE_BED.put("56206", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF11", "06"});
		htY4_RT_ZONE_BED.put("56205", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF10", "05"});
		htY4_RT_ZONE_BED.put("56216", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF11", "16"});
		htY4_RT_ZONE_BED.put("56215", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF10", "15"});
		//----------------------------------------------------------------
		
		//----------------------------------------------------------------
		//	C LINE(#2DS-LINE) RT/TF
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("59106", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF09", "06"});			
		htY4_RT_ZONE_BED.put("59105", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF08", "05"});			
		htY4_RT_ZONE_BED.put("59116", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF09", "16"});			
		htY4_RT_ZONE_BED.put("59115", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF08", "15"});			
		
		htY4_RT_ZONE_BED.put("59010", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRD", "10"});
		htY4_RT_ZONE_BED.put("59020", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRD", "20"});
		htY4_RT_ZONE_BED.put("59030", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD", "30"});
		htY4_RT_ZONE_BED.put("59040", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD", "40"});
		htY4_RT_ZONE_BED.put("59050", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRD", "50"});
		htY4_RT_ZONE_BED.put("59060", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRD", "60"});
		htY4_RT_ZONE_BED.put("59066", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRD", "70"});
		htY4_RT_ZONE_BED.put("59067", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRD", "F0"});
		htY4_RT_ZONE_BED.put("59068", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRD", "80"});
		htY4_RT_ZONE_BED.put("59069", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD", "90"});
		htY4_RT_ZONE_BED.put("59070", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD", "A0"});
		htY4_RT_ZONE_BED.put("59071", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD", "B0"});
		htY4_RT_ZONE_BED.put("59072", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD", "C0"});
		//htY4_RT_ZONE_BED.put("59090", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRD", "D0"});
		htY4_RT_ZONE_BED.put("59074", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRD", "D0"});
		//htY4_RT_ZONE_BED.put("59092", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRD", "E0"});
		htY4_RT_ZONE_BED.put("59076", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRD", "E0"});
		//----------------------------------------------------------------

		//----------------------------------------------------------------
		//	1후판 : G R/T 신규 추가
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("60013", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRG", "13"});
		
		//----------------------------------------------------------------
		//	A LINE(#2DS ON-LINE) - 2후판
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("67010", new String[] {"TBRTRA", "10"});
		htY4_RT_ZONE_BED.put("67015", new String[] {"TBRTRA", "15"});
		htY4_RT_ZONE_BED.put("67020", new String[] {"TBRTRA", "20"});
		htY4_RT_ZONE_BED.put("67025", new String[] {"TCRTRA", "25"});
		htY4_RT_ZONE_BED.put("67030", new String[] {"TCRTRA", "30"});
		htY4_RT_ZONE_BED.put("67031", new String[] {"TCRTRA", "31"}); //2후판 UT 정정재 입고
		htY4_RT_ZONE_BED.put("67035", new String[] {"TCRTRA", "35"});
		htY4_RT_ZONE_BED.put("67040", new String[] {"TCRTRA", "40"});		
		htY4_RT_ZONE_BED.put("67050", new String[] {"TDRTRA", "50"});
		htY4_RT_ZONE_BED.put("67055", new String[] {"TDRTRA", "55"});
		htY4_RT_ZONE_BED.put("67060", new String[] {"TDRTRA", "60"});		
		htY4_RT_ZONE_BED.put("67065", new String[] {"TERTRA", "65"});
		htY4_RT_ZONE_BED.put("67070", new String[] {"TERTRA", "70"});		
		htY4_RT_ZONE_BED.put("67075", new String[] {"TERTRA", "75"});
		htY4_RT_ZONE_BED.put("67080", new String[] {"TERTRA", "80"});		
		htY4_RT_ZONE_BED.put("67090", new String[] {"TFRTRA", "90"});
		htY4_RT_ZONE_BED.put("67095", new String[] {"TFRTRA", "95"});
		htY4_RT_ZONE_BED.put("67100", new String[] {"TFRTRA", "00"});		
		htY4_RT_ZONE_BED.put("67110", new String[] {"TGRTRA", "10"});
		htY4_RT_ZONE_BED.put("67115", new String[] {"TGRTRA", "15"});
		htY4_RT_ZONE_BED.put("67120", new String[] {"TGRTRA", "20"});		
		
		//----------------------------------------------------------------
		//	B LINE(#1DS ON-LINE) - 2후판
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("66010", new String[] {"TBRTRB", "10"});
		htY4_RT_ZONE_BED.put("66020", new String[] {"TBRTRB", "20"});
		htY4_RT_ZONE_BED.put("66025", new String[] {"TCRTRB", "25"});
		htY4_RT_ZONE_BED.put("66030", new String[] {"TCRTRB", "30"});
		htY4_RT_ZONE_BED.put("66035", new String[] {"TCRTRB", "35"});	
		htY4_RT_ZONE_BED.put("66040", new String[] {"TCRTRB", "40"});	
		htY4_RT_ZONE_BED.put("66050", new String[] {"TDRTRB", "50"});
		htY4_RT_ZONE_BED.put("66055", new String[] {"TDRTRB", "55"});
		htY4_RT_ZONE_BED.put("66060", new String[] {"TDRTRB", "60"});		
		htY4_RT_ZONE_BED.put("66065", new String[] {"TERTRB", "65"});
		htY4_RT_ZONE_BED.put("66070", new String[] {"TERTRB", "70"});		
		htY4_RT_ZONE_BED.put("66075", new String[] {"TERTRB", "75"});
		htY4_RT_ZONE_BED.put("66080", new String[] {"TERTRB", "80"});		
		htY4_RT_ZONE_BED.put("66090", new String[] {"TFRTRB", "90"});
		htY4_RT_ZONE_BED.put("66095", new String[] {"TFRTRB", "95"});
		htY4_RT_ZONE_BED.put("66100", new String[] {"TFRTRB", "00"});		
		htY4_RT_ZONE_BED.put("66110", new String[] {"TGRTRB", "10"});
		htY4_RT_ZONE_BED.put("66115", new String[] {"TGRTRB", "15"});
		htY4_RT_ZONE_BED.put("66120", new String[] {"TGRTRB", "20"});		
		
		//----------------------------------------------------------------
		//	C LINE(CPL OFF-LINE) - 2후판
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("68010", new String[] {"TBRTRC", "10"});
		htY4_RT_ZONE_BED.put("68020", new String[] {"TBRTRC", "20"});
		
		//----------------------------------------------------------------
		//  TRANSFER - 2후판
		//----------------------------------------------------------------
		htY4_RT_ZONE_BED.put("67206", new String[] {"TBTF01", "06"});
		htY4_RT_ZONE_BED.put("67216", new String[] {"TBTF01", "16"});
		htY4_RT_ZONE_BED.put("67205", new String[] {"TBTF02", "05"});
		htY4_RT_ZONE_BED.put("67215", new String[] {"TBTF02", "15"});		
		
		htY4_RT_ZONE_BED.put("68209", new String[] {"TBTF03", "09"});
		htY4_RT_ZONE_BED.put("68219", new String[] {"TBTF03", "19"});
		htY4_RT_ZONE_BED.put("68208", new String[] {"TBTF04", "08"});
		htY4_RT_ZONE_BED.put("68218", new String[] {"TBTF04", "18"});
		htY4_RT_ZONE_BED.put("68207", new String[] {"TBTF05", "07"});
		htY4_RT_ZONE_BED.put("68217", new String[] {"TBTF05", "17"});
		htY4_RT_ZONE_BED.put("68206", new String[] {"TBTF06", "06"});
		htY4_RT_ZONE_BED.put("68216", new String[] {"TBTF06", "16"});
		htY4_RT_ZONE_BED.put("68205", new String[] {"TBTF07", "05"});
		htY4_RT_ZONE_BED.put("68215", new String[] {"TBTF07", "15"});

		htY4_RT_ZONE_BED.put("66206", new String[] {"TCTF01", "06"});
		htY4_RT_ZONE_BED.put("66216", new String[] {"TCTF01", "16"});
		htY4_RT_ZONE_BED.put("66226", new String[] {"TCTF01", "26"});
		htY4_RT_ZONE_BED.put("66205", new String[] {"TCTF02", "05"});
		htY4_RT_ZONE_BED.put("66215", new String[] {"TCTF02", "15"});		
		htY4_RT_ZONE_BED.put("66225", new String[] {"TCTF02", "25"});		
		//----------------------------------------------------------------		
		
		
		//////////////////////////////////////////////////////////////////
		
		//BOOK-OUT LOC --> PILE TRANSFER 위치정보로 변환 초기화
		htY4_RT2TF_ZONE.put("56010", new String[] {"56106", "56105"});
		htY4_RT2TF_ZONE.put("56020", new String[] {"56116", "56115"});
		htY4_RT2TF_ZONE.put("56030", new String[] {"56206", "56205"});
		htY4_RT2TF_ZONE.put("56040", new String[] {"56216", "56215"});
		
		/////////////////////////////////////////////////////////////////////
		//				BOOK-OUT위치를 RT의 가상베드를 판단하기 위해서 저장위치로 변환
		/////////////////////////////////////////////////////////////////////
		
		//----------------------------------------------------------------
		//	1후판 A LINE(ON-LINE)
		//----------------------------------------------------------------
		htY4_BOOK_OUT_LOC.put("56010", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE", "10"});			//RT BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("56106", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE", "10"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("56105", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE", "10"});			//TRANSFER BOOK-OUT CODE
		//------------------------------------------------
		//	가적장 정보 추가 - 임춘수 2010.01.21
		//------------------------------------------------
		htY4_BOOK_OUT_LOC.put(YdConstant.TEMPSTK_LOC_A_BOOK_OUT_01, new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRA", "10"});			//가적장06베드
		//------------------------------------------------
		
		htY4_BOOK_OUT_LOC.put("56020", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE", "20"});			//RT BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("56116", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE", "20"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("56115", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE", "20"});			//TRANSFER BOOK-OUT CODE
		//------------------------------------------------
		//	가적장 정보 추가 - 임춘수 2010.01.21
		//------------------------------------------------
		htY4_BOOK_OUT_LOC.put(YdConstant.TEMPSTK_LOC_A_BOOK_OUT_02, new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRA", "20"});			//가적장16베드
		//------------------------------------------------
		
		htY4_BOOK_OUT_LOC.put("56030", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE", "30"});			//RT BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("56206", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE", "30"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("56205", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE", "30"});			//TRANSFER BOOK-OUT CODE
		
		htY4_BOOK_OUT_LOC.put("56040", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE", "40"});			//RT BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("56216", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE", "40"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("56215", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE", "40"});			//TRANSFER BOOK-OUT CODE
		
		htY4_BOOK_OUT_LOC.put("56050", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRE", "50"});
		htY4_BOOK_OUT_LOC.put("56060", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRE", "60"});
		
		htY4_BOOK_OUT_LOC.put("56070", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRE", "70"});
		htY4_BOOK_OUT_LOC.put("56075", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRE", "F0"});
		htY4_BOOK_OUT_LOC.put("56080", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRE", "80"});
		//----------------------------------------------------------------
//DONG_INSERT : OK
//		htY4_BOOK_OUT_LOC.put("56086", new String[] {"KERTRA", "90"});
//		htY4_BOOK_OUT_LOC.put("56088", new String[] {"KERTRA", "A0"});
//		htY4_BOOK_OUT_LOC.put("56090", new String[] {"KFRTRA", "B0"});
//		htY4_BOOK_OUT_LOC.put("56092", new String[] {"KFRTRA", "C0"});
		
//3CH DONG_INSERT : OK
		htY4_BOOK_OUT_LOC.put("56085", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE", "90"});
		htY4_BOOK_OUT_LOC.put("56086", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE", "A0"});
		htY4_BOOK_OUT_LOC.put("56087", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE", "B0"});
		htY4_BOOK_OUT_LOC.put("56088", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRE", "C0"});
		
//		htY4_BOOK_OUT_LOC.put("56090", new String[] {"KFRTRA", "D0"});
//		htY4_BOOK_OUT_LOC.put("56092", new String[] {"KFRTRA", "E0"});
// ONLINE F동 생성시		
		htY4_BOOK_OUT_LOC.put("56090", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRE", "D0"});  //??
		htY4_BOOK_OUT_LOC.put("56092", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRE", "E0"});  //??

		//----------------------------------------------------------------
		//	1후판 B LINE(OFF-LINE)
		//----------------------------------------------------------------
		htY4_BOOK_OUT_LOC.put("58010", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRF", "10"});
		htY4_BOOK_OUT_LOC.put("58020", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRF", "20"});
		htY4_BOOK_OUT_LOC.put("58030", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRF", "30"});
		htY4_BOOK_OUT_LOC.put("58040", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRF", "40"});
		htY4_BOOK_OUT_LOC.put("58050", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRF", "50"});
		htY4_BOOK_OUT_LOC.put("58060", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRF", "60"});
		htY4_BOOK_OUT_LOC.put("58070", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRF", "70"});
		htY4_BOOK_OUT_LOC.put("58075", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRF", "F0"});
		htY4_BOOK_OUT_LOC.put("58080", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRF", "80"});
		
//DONG_INSERT :OK
//		htY4_BOOK_OUT_LOC.put("58086", new String[] {"KERTRB", "90"});
//		htY4_BOOK_OUT_LOC.put("58088", new String[] {"KERTRB", "A0"});
//		htY4_BOOK_OUT_LOC.put("58090", new String[] {"KFRTRB", "B0"});
//		htY4_BOOK_OUT_LOC.put("58092", new String[] {"KFRTRB", "C0"});

//3CH DONG_INSERT : OK

		htY4_BOOK_OUT_LOC.put("58085", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF", "90"});
		htY4_BOOK_OUT_LOC.put("58086", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF", "A0"});
		htY4_BOOK_OUT_LOC.put("58087", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF", "B0"});
		htY4_BOOK_OUT_LOC.put("58088", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRF", "C0"});
		htY4_BOOK_OUT_LOC.put("58090", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRF", "D0"});
		htY4_BOOK_OUT_LOC.put("58092", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRF", "E0"});
		
		//----------------------------------------------------------------
		//	1후판 C LINE(ON-LINE)
		//----------------------------------------------------------------
		htY4_BOOK_OUT_LOC.put("59010", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRD", "10"});
		htY4_BOOK_OUT_LOC.put("59020", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRD", "20"});
		htY4_BOOK_OUT_LOC.put("59030", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD", "30"});
		htY4_BOOK_OUT_LOC.put("59106", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD", "30"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("59105", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD", "30"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("59040", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD", "40"});
		htY4_BOOK_OUT_LOC.put("59116", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD", "40"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("59115", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD", "40"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("59050", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRD", "50"});
		htY4_BOOK_OUT_LOC.put("59060", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "CRTRD", "60"});
		htY4_BOOK_OUT_LOC.put("59066", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRD", "70"});
		htY4_BOOK_OUT_LOC.put("59067", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRD", "F0"});
		htY4_BOOK_OUT_LOC.put("59068", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "DRTRD", "80"});
		
		htY4_BOOK_OUT_LOC.put("59069", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD", "90"});
		htY4_BOOK_OUT_LOC.put("59070", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD", "A0"});
		htY4_BOOK_OUT_LOC.put("59071", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD", "B0"});
		htY4_BOOK_OUT_LOC.put("59072", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ERTRD", "C0"});
		//htY4_BOOK_OUT_LOC.put("59090", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRD", "D0"});
		htY4_BOOK_OUT_LOC.put("59074", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRD", "D0"});
		//htY4_BOOK_OUT_LOC.put("59092", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRD", "E0"});
		htY4_BOOK_OUT_LOC.put("59076", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "FRTRD", "E0"});
		
		//----------------------------------------------------------------
		//	1후판 : G R/T 신규 추가
		//----------------------------------------------------------------
		htY4_BOOK_OUT_LOC.put("60013", new String[] {YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRG", "13"});
		
		
		//----------------------------------------------------------------
		//	2후판 A LINE(ON-LINE)
		//----------------------------------------------------------------
		htY4_BOOK_OUT_LOC.put("67010", new String[] {"TBRTRA", "10"});			//RT BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("67206", new String[] {"TBRTRA", "10"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("67205", new String[] {"TBRTRA", "10"});			//TRANSFER BOOK-OUT CODE

		htY4_BOOK_OUT_LOC.put("67020", new String[] {"TBRTRA", "20"});
		htY4_BOOK_OUT_LOC.put("67216", new String[] {"TBRTRA", "20"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("67215", new String[] {"TBRTRA", "20"});			//TRANSFER BOOK-OUT CODE

		htY4_BOOK_OUT_LOC.put("67025", new String[] {"TCRTRA", "25"});
		htY4_BOOK_OUT_LOC.put("67030", new String[] {"TCRTRA", "30"});
		htY4_BOOK_OUT_LOC.put("67031", new String[] {"TCRTRA", "31"});			//2후판 UT 정정재 입고
		htY4_BOOK_OUT_LOC.put("67035", new String[] {"TCRTRA", "35"});
		htY4_BOOK_OUT_LOC.put("67040", new String[] {"TCRTRA", "40"});

		htY4_BOOK_OUT_LOC.put("67050", new String[] {"TDRTRA", "50"});
		htY4_BOOK_OUT_LOC.put("67055", new String[] {"TDRTRA", "55"});
		htY4_BOOK_OUT_LOC.put("67060", new String[] {"TDRTRA", "60"});

		htY4_BOOK_OUT_LOC.put("67065", new String[] {"TERTRA", "65"});
		htY4_BOOK_OUT_LOC.put("67070", new String[] {"TERTRA", "70"});
		htY4_BOOK_OUT_LOC.put("67075", new String[] {"TERTRA", "75"});
		htY4_BOOK_OUT_LOC.put("67080", new String[] {"TERTRA", "80"});

		htY4_BOOK_OUT_LOC.put("67090", new String[] {"TFRTRA", "90"});
		htY4_BOOK_OUT_LOC.put("67095", new String[] {"TFRTRA", "95"});
		htY4_BOOK_OUT_LOC.put("67100", new String[] {"TFRTRA", "00"});

		htY4_BOOK_OUT_LOC.put("67110", new String[] {"TGRTRA", "10"});
		htY4_BOOK_OUT_LOC.put("67115", new String[] {"TGRTRA", "15"});
		htY4_BOOK_OUT_LOC.put("67120", new String[] {"TGRTRA", "20"});

		//----------------------------------------------------------------
		//	2후판 B LINE(ON-LINE)
		//----------------------------------------------------------------
		htY4_BOOK_OUT_LOC.put("66010", new String[] {"TBRTRB", "10"});			//RT BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("66020", new String[] {"TBRTRB", "20"});

		htY4_BOOK_OUT_LOC.put("66025", new String[] {"TCRTRB", "25"});
		htY4_BOOK_OUT_LOC.put("66030", new String[] {"TCRTRB", "30"});
		htY4_BOOK_OUT_LOC.put("66206", new String[] {"TCRTRB", "30"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("66205", new String[] {"TCRTRB", "30"});			//TRANSFER BOOK-OUT CODE

		htY4_BOOK_OUT_LOC.put("66035", new String[] {"TCRTRB", "35"});
		htY4_BOOK_OUT_LOC.put("66216", new String[] {"TCRTRB", "35"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("66215", new String[] {"TCRTRB", "35"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("66040", new String[] {"TCRTRB", "40"});
		htY4_BOOK_OUT_LOC.put("66226", new String[] {"TCRTRB", "40"});			//TRANSFER BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("66225", new String[] {"TCRTRB", "40"});			//TRANSFER BOOK-OUT CODE		

		htY4_BOOK_OUT_LOC.put("66050", new String[] {"TDRTRB", "50"});
		htY4_BOOK_OUT_LOC.put("66055", new String[] {"TDRTRB", "55"});
		htY4_BOOK_OUT_LOC.put("66060", new String[] {"TDRTRB", "60"});

		htY4_BOOK_OUT_LOC.put("66065", new String[] {"TERTRB", "65"});
		htY4_BOOK_OUT_LOC.put("66070", new String[] {"TERTRB", "70"});
		htY4_BOOK_OUT_LOC.put("66075", new String[] {"TERTRB", "75"});
		htY4_BOOK_OUT_LOC.put("66080", new String[] {"TERTRB", "80"});

		htY4_BOOK_OUT_LOC.put("66090", new String[] {"TFRTRB", "90"});
		htY4_BOOK_OUT_LOC.put("66095", new String[] {"TFRTRB", "95"});
		htY4_BOOK_OUT_LOC.put("66100", new String[] {"TFRTRB", "00"});

		htY4_BOOK_OUT_LOC.put("66110", new String[] {"TGRTRB", "10"});
		htY4_BOOK_OUT_LOC.put("66115", new String[] {"TGRTRB", "15"});
		htY4_BOOK_OUT_LOC.put("66120", new String[] {"TGRTRB", "20"});

		//----------------------------------------------------------------
		//	2후판 C LINE(OFF-LINE)
		//----------------------------------------------------------------
		htY4_BOOK_OUT_LOC.put("68010", new String[] {"TBRTRC", "10"});			//RT BOOK-OUT CODE
		htY4_BOOK_OUT_LOC.put("68020", new String[] {"TBRTRC", "20"});
		
		
		//----------------------------------------------------------------
		
		//----------------------------------------------------------------
		//	1후판 A(E) BOOK OUT > B(F) BOOK OUT 매핑
		//----------------------------------------------------------------
		htY4_CHG_BBOOK_OUT_LOC.put("55991", "58010");
		htY4_CHG_BBOOK_OUT_LOC.put("56010", "58010");
		htY4_CHG_BBOOK_OUT_LOC.put("56105", "58010");
		htY4_CHG_BBOOK_OUT_LOC.put("56106", "58010");
		htY4_CHG_BBOOK_OUT_LOC.put("55992", "58020");
		htY4_CHG_BBOOK_OUT_LOC.put("56020", "58020");
		htY4_CHG_BBOOK_OUT_LOC.put("56115", "58020");
		htY4_CHG_BBOOK_OUT_LOC.put("56116", "58020");
		htY4_CHG_BBOOK_OUT_LOC.put("56030", "58030");
		htY4_CHG_BBOOK_OUT_LOC.put("56205", "58030");
		htY4_CHG_BBOOK_OUT_LOC.put("56206", "58030");
		htY4_CHG_BBOOK_OUT_LOC.put("56040", "58040");
		htY4_CHG_BBOOK_OUT_LOC.put("56215", "58040");
		htY4_CHG_BBOOK_OUT_LOC.put("56216", "58040");
		htY4_CHG_BBOOK_OUT_LOC.put("56050", "58050");
		htY4_CHG_BBOOK_OUT_LOC.put("56060", "58060");
		htY4_CHG_BBOOK_OUT_LOC.put("56070", "58070");
		htY4_CHG_BBOOK_OUT_LOC.put("56075", "58075");
		htY4_CHG_BBOOK_OUT_LOC.put("56080", "58080");
 		htY4_CHG_BBOOK_OUT_LOC.put("56085", "58085");
		htY4_CHG_BBOOK_OUT_LOC.put("56086", "58086");
		htY4_CHG_BBOOK_OUT_LOC.put("56087", "58087");
		htY4_CHG_BBOOK_OUT_LOC.put("56088", "58088");
		htY4_CHG_BBOOK_OUT_LOC.put("56090", "58090");
		htY4_CHG_BBOOK_OUT_LOC.put("56092", "58092");

		//----------------------------------------------------------------
		//	1후판 A(E) BOOK OUT > C(D) BOOK OUT 매핑 -- 2013.08.04 추가 (3기) 
		//----------------------------------------------------------------		
		htY4_CHG_CBOOK_OUT_LOC.put("55991", "59010");
		htY4_CHG_CBOOK_OUT_LOC.put("56010", "59010");
		htY4_CHG_CBOOK_OUT_LOC.put("56105", "59010");
		htY4_CHG_CBOOK_OUT_LOC.put("56106", "59010");
		htY4_CHG_CBOOK_OUT_LOC.put("55992", "59020");
		htY4_CHG_CBOOK_OUT_LOC.put("56020", "59020");
		htY4_CHG_CBOOK_OUT_LOC.put("56115", "59020");
		htY4_CHG_CBOOK_OUT_LOC.put("56116", "59020");
		htY4_CHG_CBOOK_OUT_LOC.put("56030", "59030");
		htY4_CHG_CBOOK_OUT_LOC.put("56205", "59105");
		htY4_CHG_CBOOK_OUT_LOC.put("56206", "59106");
		htY4_CHG_CBOOK_OUT_LOC.put("56040", "59040");
		htY4_CHG_CBOOK_OUT_LOC.put("56215", "59115");
		htY4_CHG_CBOOK_OUT_LOC.put("56216", "59116");
		htY4_CHG_CBOOK_OUT_LOC.put("56050", "59050");
		htY4_CHG_CBOOK_OUT_LOC.put("56060", "59060");
		htY4_CHG_CBOOK_OUT_LOC.put("56070", "59066");
		htY4_CHG_CBOOK_OUT_LOC.put("56075", "59067");
		htY4_CHG_CBOOK_OUT_LOC.put("56080", "59068");
		htY4_CHG_CBOOK_OUT_LOC.put("56085", "59069");
		htY4_CHG_CBOOK_OUT_LOC.put("56086", "59070");
		htY4_CHG_CBOOK_OUT_LOC.put("56087", "59071");
		htY4_CHG_CBOOK_OUT_LOC.put("56088", "59072");
		htY4_CHG_CBOOK_OUT_LOC.put("56090", "59074");
		htY4_CHG_CBOOK_OUT_LOC.put("56092", "59076");
		
		htY4_CHG_CBOOK_OUT_LOC.put("58010", "59010");
		htY4_CHG_CBOOK_OUT_LOC.put("58010", "59010");
		htY4_CHG_CBOOK_OUT_LOC.put("58010", "59010");
		htY4_CHG_CBOOK_OUT_LOC.put("58010", "59010");
		htY4_CHG_CBOOK_OUT_LOC.put("58020", "59020");
		htY4_CHG_CBOOK_OUT_LOC.put("58020", "59020");
		htY4_CHG_CBOOK_OUT_LOC.put("58020", "59020");
		htY4_CHG_CBOOK_OUT_LOC.put("58020", "59020");
		htY4_CHG_CBOOK_OUT_LOC.put("58030", "59030");
		htY4_CHG_CBOOK_OUT_LOC.put("58030", "59105");
		htY4_CHG_CBOOK_OUT_LOC.put("58030", "59106");
		htY4_CHG_CBOOK_OUT_LOC.put("56040", "59040");
		htY4_CHG_CBOOK_OUT_LOC.put("58040", "59115");
		htY4_CHG_CBOOK_OUT_LOC.put("58040", "59116");
		htY4_CHG_CBOOK_OUT_LOC.put("58050", "59050");
		htY4_CHG_CBOOK_OUT_LOC.put("58060", "59060");
		htY4_CHG_CBOOK_OUT_LOC.put("58070", "59066");
		htY4_CHG_CBOOK_OUT_LOC.put("58075", "59067");
		htY4_CHG_CBOOK_OUT_LOC.put("58080", "59068");
		htY4_CHG_CBOOK_OUT_LOC.put("58085", "59069");
		htY4_CHG_CBOOK_OUT_LOC.put("58086", "59070");
		htY4_CHG_CBOOK_OUT_LOC.put("58087", "59071");
		htY4_CHG_CBOOK_OUT_LOC.put("58088", "59072");
		htY4_CHG_CBOOK_OUT_LOC.put("58090", "59074");
		htY4_CHG_CBOOK_OUT_LOC.put("58092", "59076");
		//----------------------------------------------------------------
		//	1후판 C(D) BOOK OUT > A(E) BOOK OUT 매핑 -- 2013.08.27 추가 (3기) 
		//----------------------------------------------------------------		
		htY4_CHG_ABOOK_OUT_LOC.put("59010", "55991");
		htY4_CHG_ABOOK_OUT_LOC.put("59010", "56010");
		htY4_CHG_ABOOK_OUT_LOC.put("59010", "56105");
		htY4_CHG_ABOOK_OUT_LOC.put("59010", "56106");
		htY4_CHG_ABOOK_OUT_LOC.put("59020", "55992");
		htY4_CHG_ABOOK_OUT_LOC.put("59020", "56020");
		htY4_CHG_ABOOK_OUT_LOC.put("59020", "56115");
		htY4_CHG_ABOOK_OUT_LOC.put("59020", "56116");
		htY4_CHG_ABOOK_OUT_LOC.put("59030", "56030");
		htY4_CHG_ABOOK_OUT_LOC.put("59105", "56205");
		htY4_CHG_ABOOK_OUT_LOC.put("59106", "56206");
		htY4_CHG_ABOOK_OUT_LOC.put("59040", "56040");
		htY4_CHG_ABOOK_OUT_LOC.put("59115", "56215");
		htY4_CHG_ABOOK_OUT_LOC.put("59116", "56216");
		htY4_CHG_ABOOK_OUT_LOC.put("59050", "56050");
		htY4_CHG_ABOOK_OUT_LOC.put("59060", "56060");
		htY4_CHG_ABOOK_OUT_LOC.put("59066", "56070");
		htY4_CHG_ABOOK_OUT_LOC.put("59067", "56075");
		htY4_CHG_ABOOK_OUT_LOC.put("59068", "56080");
		htY4_CHG_ABOOK_OUT_LOC.put("59069", "56085");
		htY4_CHG_ABOOK_OUT_LOC.put("59070", "56086");
		htY4_CHG_ABOOK_OUT_LOC.put("59071", "56087");
		htY4_CHG_ABOOK_OUT_LOC.put("59072", "56088");
		htY4_CHG_ABOOK_OUT_LOC.put("59074", "56090");
		htY4_CHG_ABOOK_OUT_LOC.put("59076", "56092");		


		//----------------------------------------------------------------
		//	1후판 C(D) BOOK OUT > A(E) BOOK OUT 매핑 -- 2013.08.27 추가 (3기) 
		//----------------------------------------------------------------		
		htY4_CHG_C2BBOOK_OUT_LOC.put("59010", "58010");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59010", "58010");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59010", "58010");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59010", "58010");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59020", "58020");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59020", "58020");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59020", "58020");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59020", "58020");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59030", "58030");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59105", "58030");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59106", "58030");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59040", "56040");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59115", "58040");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59116", "58040");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59050", "58050");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59060", "58060");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59066", "58070");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59067", "58075");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59068", "58080");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59069", "58085");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59070", "58086");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59071", "58087");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59072", "58088");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59074", "58090");
		htY4_CHG_C2BBOOK_OUT_LOC.put("59076", "58092");		
		
		//----------------------------------------------------------------
		
		
		//----------------------------------------------------------------
		//	1후판  TF BOOK OUT > RT BOOK OUT 위치 매핑
		//----------------------------------------------------------------		
		ht_TF_2_RT_STKLOC.put(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ATF01", YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE");
		ht_TF_2_RT_STKLOC.put(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ATF02", YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "ARTRE");
		
		ht_TF_2_RT_STKLOC.put(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF10", YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE");
		ht_TF_2_RT_STKLOC.put(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF11", YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRE");		

		ht_TF_2_RT_STKLOC.put(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF08", YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD");
		ht_TF_2_RT_STKLOC.put(YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BTF09", YdConstant.YD_GP_INTGR_PLATE_GDS_YARD + "BRTRD");		
		
		//----------------------------------------------------------------
		//	2후판  TF BOOK OUT > RT BOOK OUT 위치 매핑
		//----------------------------------------------------------------		
		ht_TF_2_RT_STKLOC.put("TBTF01", "TBRTRA");
		ht_TF_2_RT_STKLOC.put("TBTF02", "TBRTRA");
		
		ht_TF_2_RT_STKLOC.put("TCTF01", "TCRTRB");
		ht_TF_2_RT_STKLOC.put("TCTF02", "TCRTRB");		
		
		ht_TF_2_RT_STKLOC.put("TBTF0106", "TBRTRA10"); //TF 상에서 BOOK-OUT 취소용
		ht_TF_2_RT_STKLOC.put("TBTF0116", "TBRTRA20"); //TF 상에서 BOOK-OUT 취소용
		
		ht_TF_2_RT_STKLOC.put("TCTF0106", "TCRTRB30"); //TF 상에서 BOOK-OUT 취소용
		ht_TF_2_RT_STKLOC.put("TCTF0116", "TCRTRB35"); //TF 상에서 BOOK-OUT 취소용			
		ht_TF_2_RT_STKLOC.put("TCTF0126", "TCRTRB40"); //TF 상에서 BOOK-OUT 취소용			
		
		//----------------------------------------------------------------
		//	1후판정정야드  L3 > L2 정보 매핑
		//----------------------------------------------------------------		
		htY9_CHG_L2BOOK_OUT_LOC.put("PBRT01", "42010");
		htY9_CHG_L2BOOK_OUT_LOC.put("PBRT02", "42905");
		htY9_CHG_L2BOOK_OUT_LOC.put("PBRT03", "48000");
		htY9_CHG_L2BOOK_OUT_LOC.put("PBRT04", "53530"); 
		htY9_CHG_L2BOOK_OUT_LOC.put("PART01", "47905");
		htY9_CHG_L2BOOK_OUT_LOC.put("PART02", "49005");
		htY9_CHG_L2BOOK_OUT_LOC.put("PART03", "59000"); 
		
		htY9_CHG_L3BOOK_OUT_LOC.put("42010", "PBRT01");
		htY9_CHG_L3BOOK_OUT_LOC.put("42905", "PBRT02");
		htY9_CHG_L3BOOK_OUT_LOC.put("48000", "PBRT03");
		htY9_CHG_L3BOOK_OUT_LOC.put("53530", "PBRT04");	
		htY9_CHG_L3BOOK_OUT_LOC.put("47905", "PART01");
		htY9_CHG_L3BOOK_OUT_LOC.put("49005", "PART02");
		htY9_CHG_L3BOOK_OUT_LOC.put("59000", "PART03");
		
		htY9_CHG_L3CRANE_INFO.put("33", "PBCRB1");
		htY9_CHG_L3CRANE_INFO.put("16", "PBCRB2");
		htY9_CHG_L3CRANE_INFO.put("17", "PBCRB3");
		htY9_CHG_L3CRANE_INFO.put("46", "PACRA1");
		
		htY9_CHG_L3LOC_INFO.put("16101", "PB0101");
		htY9_CHG_L3LOC_INFO.put("16102", "PB0102");
		htY9_CHG_L3LOC_INFO.put("3201" , "PB0201");
		htY9_CHG_L3LOC_INFO.put("3202" , "PB0202");
		htY9_CHG_L3LOC_INFO.put("4301" , "PB0301");
		htY9_CHG_L3LOC_INFO.put("4302" , "PB0302");
		htY9_CHG_L3LOC_INFO.put("4303" , "PB0303");
		htY9_CHG_L3LOC_INFO.put("4304" , "PB0304");
		htY9_CHG_L3LOC_INFO.put("4305" , "PB0305");
		htY9_CHG_L3LOC_INFO.put("4306" , "PB0306");
		htY9_CHG_L3LOC_INFO.put("5401" , "PB0401");
		htY9_CHG_L3LOC_INFO.put("5402" , "PB0402");
		htY9_CHG_L3LOC_INFO.put("6501" , "PB0501");
		htY9_CHG_L3LOC_INFO.put("6502" , "PB0502");
		htY9_CHG_L3LOC_INFO.put("26103", "PA0103");
		htY9_CHG_L3LOC_INFO.put("26103", "PA0104");
		htY9_CHG_L3LOC_INFO.put("20101", "PA0101");
		htY9_CHG_L3LOC_INFO.put("20102", "PA0102");
		////////////////////////////////////////////////////////////////////
		
		//==============================================================
		// 권오창
		// Level2 Port정보 저장
		//==============================================================
		h_MPCodeL2.put("Q1", "6800");  // 성분Level2
		h_MPCodeL2.put("Q2", "6802");  // 재질Level2
		h_MPCodeL2.put("C1", "6804");  // 제강Level2
		h_MPCodeL2.put("C2", "6806");  // 연주Level2
		h_MPCodeL2.put("C3", "6806");  // 연주정정Level2
		h_MPCodeL2.put("B1", "6810");  // 코크스Level2
		h_MPCodeL2.put("B2", "6812");  // 석회Level2
		h_MPCodeL2.put("B3", "6814");  // 소결Level2
		h_MPCodeL2.put("B4", "6816");  // 고로Level2
		h_MPCodeL2.put("P1", "6818");  // 가열로Level2
		h_MPCodeL2.put("P2", "6820");  // 압연전단Level2
		h_MPCodeL2.put("P3", "6822");  // 열처리Level2
		h_MPCodeL2.put("P4", "6824");  // #1GASL2
		h_MPCodeL2.put("P5", "6826");  // #1GASL2
		h_MPCodeL2.put("P6", "6828");  // #2GASL2
		h_MPCodeL2.put("P7", "6830");  // #2GASL2
		h_MPCodeL2.put("P8", "6832");  // 온도측정
		h_MPCodeL2.put("H1", "6834");  // 열연압연(가열로 포함) L2
		h_MPCodeL2.put("H2", "6836");  // 열연정정SPM1 L2
		h_MPCodeL2.put("H3", "6838");  // 열연정정HFL L2
		h_MPCodeL2.put("Y1", "6840");  // 연주슬라브야드 Level2
		h_MPCodeL2.put("Y2", "6842");  // 연주정정 야드 Level2             
		h_MPCodeL2.put("Y3", "6844");  // 후판 슬라브 야드 Level2		
		h_MPCodeL2.put("Y4", "6846");  // 후판제품 야드 Level2
		h_MPCodeL2.put("Y5", "6848");  // C열연 코일야드 Level2
		h_MPCodeL2.put("W1", "6850");  // 원료Level2
		h_MPCodeL2.put("Z1", "6852");  // 계량시스템Level2
		h_MPCodeL2.put("Z2", "6854");  // 철도Level2
		h_MPCodeL2.put("Z3", "6856");  // 환경에너지Level2
		h_MPCodeL2.put("Z4", "6858");  // 시험검정Level2
		h_MPCodeL2.put("R2", "6860");  // C열연 Mill Level2
		h_MPCodeL2.put("R3", "6862");  // C열연정정Level2
		
		
		//==============================================================
		// 2009.09.04 권오창
		// 야드와 조업의 설비구분 매칭값
		//==============================================================
		h_hstEqpGpMatch.put("HGFE0106", "ECC06");  //#1HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("HGFE0105", "ECC05");  //#1HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("HGFE0104", "ECC04");  //#1HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("HGFE0103", "ECC03");  //#1HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("HGFE0102", "ECC02");  //#1HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("HGFE0101", "ECC01");  //#1HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("HGST0101", "TATI");  //C열연 정정 #1HOT FINAL Coil Station
		h_hstEqpGpMatch.put("HGHE0101", "ECC" );  //C열연 정정 #1HOT FINAL  Enter Coil Car 
		h_hstEqpGpMatch.put("HGFD0108", "DCC08");  //#1HOT FINAL 출측8번지
		h_hstEqpGpMatch.put("HGFD0109", "DCC09");  //#1HOT FINAL 출측9번지
		h_hstEqpGpMatch.put("HGFD0110", "DCC10");  //#1HOT FINAL 출측10번지
		h_hstEqpGpMatch.put("HGFD0111", "DCC11");  //#1HOT FINAL 출측11번지
		
		h_hstEqpGpMatch.put("HFFE0201", "K2-01");  //#2HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("HFFE0202", "K2-02");  //#2HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("HFFE0203", "K2-03");  //#2HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("HFFE0204", "K2-04");  //#2HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("HFFE0205", "K2-05");  //#2HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("HFFE0206", "K2-06");  //#2HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("HFFE0207", "K2-07");  //#2HOT FINAL 입측7번지
		h_hstEqpGpMatch.put("HFFE0208", "K2-08");  //#2HOT FINAL 입측8번지
		h_hstEqpGpMatch.put("HFFE0209", "K2-09");  //#2HOT FINAL 입측9번지
		h_hstEqpGpMatch.put("HFFE0210", "K2-10");  //#2HOT FINAL 입측10번지
		h_hstEqpGpMatch.put("HFFE0211", "K2-11");  //#2HOT FINAL 입측11번지
		h_hstEqpGpMatch.put("HFFE0212", "K2-12");  //#2HOT FINAL 입측12번지
		h_hstEqpGpMatch.put("HFCR0101", "CR01");   //#2HOT FINAL 크래들롤
		
		h_hstEqpGpMatch.put("HDFE0301", "K3-01");  //#3HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("HDFE0302", "K3-02");  //#3HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("HDFE0303", "K3-03");  //#3HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("HDFE0304", "K3-04");  //#3HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("HDFE0305", "K3-05");  //#3HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("HDFE0306", "K3-06");  //#3HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("HDFE0307", "K3-07");  //#3HOT FINAL 입측7번지
		h_hstEqpGpMatch.put("HDFE0308", "K3-08");  //#3HOT FINAL 입측8번지
		h_hstEqpGpMatch.put("HDFE0309", "K3-09");  //#3HOT FINAL 입측9번지
		h_hstEqpGpMatch.put("HDFE0310", "K3-10");  //#3HOT FINAL 입측10번지
		h_hstEqpGpMatch.put("HDFE0311", "K3-11");  //#3HOT FINAL 입측11번지
		h_hstEqpGpMatch.put("HDFE0312", "K3-12");  //#3HOT FINAL 입측12번지
		h_hstEqpGpMatch.put("HDFE0313", "K3-13");  //#3HOT FINAL 입측13번지
		h_hstEqpGpMatch.put("HDFE0314", "K3-14");  //#3HOT FINAL 입측14번지
		h_hstEqpGpMatch.put("HDFE0315", "K3-15");  //#3HOT FINAL 입측15번지
		h_hstEqpGpMatch.put("HDFE0316", "K3-16");  //#3HOT FINAL 입측16번지
		h_hstEqpGpMatch.put("HDFE0317", "K3-17");  //#3HOT FINAL 입측17번지
		h_hstEqpGpMatch.put("HDFE0318", "K3-18");  //#3HOT FINAL 입측18번지
		h_hstEqpGpMatch.put("HDFE0319", "K3-19");  //#3HOT FINAL 입측19번지
		h_hstEqpGpMatch.put("HDFE0320", "K3-20");  //#3HOT FINAL 입측20번지
		h_hstEqpGpMatch.put("HDCR0101", "CR01");   //#3HOT FINAL 크래들롤

		h_hstEqpGpMatch.put("HHKE0106", "ECC06");  //C열연 정정 SPM1 입측6번지
		h_hstEqpGpMatch.put("HHKE0105", "ECC05");  //C열연 정정 SPM1 입측5번지
		h_hstEqpGpMatch.put("HHKE0104", "ECC04");  //C열연 정정 SPM1 입측4번지
		h_hstEqpGpMatch.put("HHKE0103", "ECC03");  //C열연 정정 SPM1 입측3번지
		h_hstEqpGpMatch.put("HHKE0102", "ECC02");  //C열연 정정 SPM1 입측2번지
		h_hstEqpGpMatch.put("HHKE0101", "ECC01");  //C열연 정정 SPM1 입측1번지
		h_hstEqpGpMatch.put("HHNT0101", "ENT");    //C열연 정정 SPM1 Enter Coil Car
		h_hstEqpGpMatch.put("HHKD0101", "DCC01");  //C열연 정정 SPM1 출측1번지
		h_hstEqpGpMatch.put("HHKD0102", "DCC02");  //C열연 정정 SPM1 출측2번지
		h_hstEqpGpMatch.put("HHKD0103", "DCC03");  //C열연 정정 SPM1 출측3번지
		h_hstEqpGpMatch.put("HHKD0104", "DCC04");  //C열연 정정 SPM1 출측4번지
		h_hstEqpGpMatch.put("HHKD0105", "DCC05");  //C열연 정정 SPM1 출측5번지
		h_hstEqpGpMatch.put("HHKD0106", "DCC06");  //C열연 정정 SPM1 출측6번지
		h_hstEqpGpMatch.put("HHKD0107", "DCC07");  //C열연 정정 SPM1 출측7번지
		h_hstEqpGpMatch.put("HHKD0108", "DCC08");  //C열연 정정 SPM1 출측8번지
		h_hstEqpGpMatch.put("HHKD0109", "DCC09");  //C열연 정정 SPM1 출측9번지
		h_hstEqpGpMatch.put("HHKD0110", "DCC10");  //C열연 정정 SPM1 출측10번지
		h_hstEqpGpMatch.put("HHKD0111", "DCC11");  //C열연 정정 SPM1 출측11번지
		
		h_hstEqpGpMatch.put("HEDE0106", "ECC06");  //C열연 정정 SPM2 입측6번지
		h_hstEqpGpMatch.put("HEDE0105", "ECC05");  //C열연 정정 SPM2 입측5번지
		h_hstEqpGpMatch.put("HEDE0104", "ECC04");  //C열연 정정 SPM2 입측4번지
		h_hstEqpGpMatch.put("HEDE0103", "ECC03");  //C열연 정정 SPM2 입측3번지
		h_hstEqpGpMatch.put("HEDE0102", "ECC02");  //C열연 정정 SPM2 입측2번지
		h_hstEqpGpMatch.put("HEDE0101", "ECC01");  //C열연 정정 SPM2 입측1번지
		h_hstEqpGpMatch.put("HENT0101", "ENT");    //C열연 정정 SPM2 Enter Coil Car
		h_hstEqpGpMatch.put("HEDD0101", "DCC01");  //C열연 정정 SPM2 출측1번지	
		h_hstEqpGpMatch.put("HEDD0102", "DCC02");  //C열연 정정 SPM2 출측2번지
		h_hstEqpGpMatch.put("HEDD0103", "DCC03");  //C열연 정정 SPM2 출측3번지
		h_hstEqpGpMatch.put("HEDD0104", "DCC04");  //C열연 정정 SPM2 출측4번지
		h_hstEqpGpMatch.put("HEDD0105", "DCC05");  //C열연 정정 SPM2 출측5번지
		h_hstEqpGpMatch.put("HEDD0106", "DCC06");  //C열연 정정 SPM2 출측6번지
		h_hstEqpGpMatch.put("HEDD0107", "DCC07");  //C열연 정정 SPM2 출측7번지
		h_hstEqpGpMatch.put("HEDD0108", "DCC08");  //C열연 정정 SPM2 출측8번지
		h_hstEqpGpMatch.put("HEDD0109", "DCC09");  //C열연 정정 SPM2 출측9번지
		h_hstEqpGpMatch.put("HEDD0110", "DCC10");  //C열연 정정 SPM2 출측10번지
		h_hstEqpGpMatch.put("HEDD0111", "DCC11");  //C열연 정정 SPM2 출측11번지

		
		h_hstEqpGpMatch.put("3DHS0101", "6H-01");  //#B열연 HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("3DHS0102", "6H-02");  //#B열연 HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("3DHS0103", "6H-03");  //#B열연 HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("3DHS0104", "6H-04");  //#B열연 HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("3DHS0105", "6H-05");  //#B열연 HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("3DHS0106", "6H-06");  //#B열연 HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("3DHS0107", "6H-07");  //#B열연 HOT FINAL 입측7번지
		h_hstEqpGpMatch.put("3DHS0108", "6H-08");  //#B열연 HOT FINAL 입측8번지
		h_hstEqpGpMatch.put("3DHS0109", "6H-09");  //#B열연 HOT FINAL 입측9번지
		h_hstEqpGpMatch.put("3DHS0110", "6H-10");  //#B열연 HOT FINAL 입측10번지
		h_hstEqpGpMatch.put("3DHS0111", "6H-11");  //#B열연 HOT FINAL 입측11번지
		h_hstEqpGpMatch.put("3DHS0112", "6H-12");  //#B열연 HOT FINAL 입측12번지
		h_hstEqpGpMatch.put("3DHS0113", "6H-13");  //#B열연 HOT FINAL 입측13번지
		h_hstEqpGpMatch.put("3DHS0114", "6H-14");  //#B열연 HOT FINAL 입측14번지
		h_hstEqpGpMatch.put("3DHS0115", "6H-15");  //#B열연 HOT FINAL 입측15번지
		
//C증설 
		h_hstEqpGpMatch.put("HCKE0312", "ECC12");  //C열연 정정 SPM3 입측12번지
		h_hstEqpGpMatch.put("HCKE0311", "ECC11");  //C열연 정정 SPM3 입측11번지
		h_hstEqpGpMatch.put("HCKE0310", "ECC10");  //C열연 정정 SPM3 입측10번지
		h_hstEqpGpMatch.put("HCKE0309", "ECC09");  //C열연 정정 SPM3 입측9번지
		h_hstEqpGpMatch.put("HCKE0308", "ECC08");  //C열연 정정 SPM3 입측8번지
		h_hstEqpGpMatch.put("HCKE0307", "ECC07");  //C열연 정정 SPM3 입측7번지
		h_hstEqpGpMatch.put("HCKE0306", "ECC06");  //C열연 정정 SPM3 입측6번지
		h_hstEqpGpMatch.put("HCKE0305", "ECC05");  //C열연 정정 SPM3 입측5번지
		h_hstEqpGpMatch.put("HCKE0304", "ECC04");  //C열연 정정 SPM3 입측4번지
		h_hstEqpGpMatch.put("HCKE0303", "ECC03");  //C열연 정정 SPM3 입측3번지
		h_hstEqpGpMatch.put("HCKE0302", "ECC02");  //C열연 정정 SPM3 입측2번지
		h_hstEqpGpMatch.put("HCKE0301", "ECC01");  //C열연 정정 SPM3 입측1번지
		h_hstEqpGpMatch.put("HCKD0301", "DCC01");  //C열연 정정 SPM3 출측1번지
		h_hstEqpGpMatch.put("HCKD0302", "DCC02");  //C열연 정정 SPM3 출측2번지
		h_hstEqpGpMatch.put("HCKD0303", "DCC03");  //C열연 정정 SPM3 출측3번지
		h_hstEqpGpMatch.put("HCKD0304", "DCC04");  //C열연 정정 SPM3 출측4번지
		h_hstEqpGpMatch.put("HCKD0305", "DCC05");  //C열연 정정 SPM3 출측5번지
		h_hstEqpGpMatch.put("HCKD0306", "DCC06");  //C열연 정정 SPM3 출측6번지
		h_hstEqpGpMatch.put("HCKD0307", "DCC07");  //C열연 정정 SPM3 출측7번지
		h_hstEqpGpMatch.put("HCKD0308", "DCC08");  //C열연 정정 SPM3 출측8번지
		h_hstEqpGpMatch.put("HCKD0309", "DCC09");  //C열연 정정 SPM3 출측9번지
		h_hstEqpGpMatch.put("HCKD0310", "DCC10");  //C열연 정정 SPM3 출측10번지
		h_hstEqpGpMatch.put("HCKD0311", "DCC11");  //C열연 정정 SPM3 출측11번지

		h_hstEqpGpMatch.put("HBKE0412", "ECC12");  //C열연 정정 SPM4 입측12번지
		h_hstEqpGpMatch.put("HBKE0411", "ECC11");  //C열연 정정 SPM4 입측11번지
		h_hstEqpGpMatch.put("HBKE0410", "ECC10");  //C열연 정정 SPM4 입측10번지
		h_hstEqpGpMatch.put("HBKE0409", "ECC09");  //C열연 정정 SPM4 입측9번지
		h_hstEqpGpMatch.put("HBKE0408", "ECC08");  //C열연 정정 SPM4 입측8번지
		h_hstEqpGpMatch.put("HBKE0407", "ECC07");  //C열연 정정 SPM4 입측7번지
		h_hstEqpGpMatch.put("HBKE0406", "ECC06");  //C열연 정정 SPM4 입측6번지
		h_hstEqpGpMatch.put("HBKE0405", "ECC05");  //C열연 정정 SPM4 입측5번지
		h_hstEqpGpMatch.put("HBKE0404", "ECC04");  //C열연 정정 SPM4 입측4번지
		h_hstEqpGpMatch.put("HBKE0403", "ECC03");  //C열연 정정 SPM4 입측3번지
		h_hstEqpGpMatch.put("HBKE0402", "ECC02");  //C열연 정정 SPM4 입측2번지
		h_hstEqpGpMatch.put("HBKE0401", "ECC01");  //C열연 정정 SPM4 입측1번지
		h_hstEqpGpMatch.put("HBKD0401", "DCC01");  //C열연 정정 SPM4 출측1번지
		h_hstEqpGpMatch.put("HBKD0402", "DCC02");  //C열연 정정 SPM4 출측2번지
		h_hstEqpGpMatch.put("HBKD0403", "DCC03");  //C열연 정정 SPM4 출측3번지
		h_hstEqpGpMatch.put("HBKD0404", "DCC04");  //C열연 정정 SPM4 출측4번지
		h_hstEqpGpMatch.put("HBKD0405", "DCC05");  //C열연 정정 SPM4 출측5번지
		h_hstEqpGpMatch.put("HBKD0406", "DCC06");  //C열연 정정 SPM4 출측6번지
		h_hstEqpGpMatch.put("HBKD0407", "DCC07");  //C열연 정정 SPM4 출측7번지
		h_hstEqpGpMatch.put("HBKD0408", "DCC08");  //C열연 정정 SPM4 출측8번지
		h_hstEqpGpMatch.put("HBKD0409", "DCC09");  //C열연 정정 SPM4 출측9번지
		h_hstEqpGpMatch.put("HBKD0410", "DCC10");  //C열연 정정 SPM4 출측10번지
		h_hstEqpGpMatch.put("HBKD0411", "DCC11");  //C열연 정정 SPM4 출측11번지
 
		h_hstEqpGpMatch.put("HAKE0512", "ECC12");  //C열연 정정 SPM5 입측12번지
		h_hstEqpGpMatch.put("HAKE0511", "ECC11");  //C열연 정정 SPM5 입측11번지
		h_hstEqpGpMatch.put("HAKE0510", "ECC10");  //C열연 정정 SPM5 입측10번지
		h_hstEqpGpMatch.put("HAKE0509", "ECC09");  //C열연 정정 SPM5 입측9번지
		h_hstEqpGpMatch.put("HAKE0508", "ECC08");  //C열연 정정 SPM5 입측8번지
		h_hstEqpGpMatch.put("HAKE0507", "ECC07");  //C열연 정정 SPM5 입측7번지
		h_hstEqpGpMatch.put("HAKE0506", "ECC06");  //C열연 정정 SPM5 입측6번지
		h_hstEqpGpMatch.put("HAKE0505", "ECC05");  //C열연 정정 SPM5 입측5번지
		h_hstEqpGpMatch.put("HAKE0504", "ECC04");  //C열연 정정 SPM5 입측4번지
		h_hstEqpGpMatch.put("HAKE0503", "ECC03");  //C열연 정정 SPM5 입측3번지
		h_hstEqpGpMatch.put("HAKE0502", "ECC02");  //C열연 정정 SPM5 입측2번지
		h_hstEqpGpMatch.put("HAKE0501", "ECC01");  //C열연 정정 SPM5 입측1번지
		h_hstEqpGpMatch.put("HAKD0501", "DCC01");  //C열연 정정 SPM5 출측1번지
		h_hstEqpGpMatch.put("HAKD0502", "DCC02");  //C열연 정정 SPM5 출측2번지
		h_hstEqpGpMatch.put("HAKD0503", "DCC03");  //C열연 정정 SPM5 출측3번지
		h_hstEqpGpMatch.put("HAKD0504", "DCC04");  //C열연 정정 SPM5 출측4번지
		h_hstEqpGpMatch.put("HAKD0505", "DCC05");  //C열연 정정 SPM5 출측5번지
		h_hstEqpGpMatch.put("HAKD0506", "DCC06");  //C열연 정정 SPM5 출측6번지
		h_hstEqpGpMatch.put("HAKD0507", "DCC07");  //C열연 정정 SPM5 출측7번지
		h_hstEqpGpMatch.put("HAKD0508", "DCC08");  //C열연 정정 SPM5 출측8번지
		h_hstEqpGpMatch.put("HAKD0509", "DCC09");  //C열연 정정 SPM5 출측9번지
		h_hstEqpGpMatch.put("HAKD0510", "DCC10");  //C열연 정정 SPM5 출측10번지
		h_hstEqpGpMatch.put("HAKD0511", "DCC11");  //C열연 정정 SPM5 출측11번지
		
		h_hstEqpGpMatch.put("HCFE0406", "ECC06");  //#4HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("HCFE0405", "ECC05");  //#4HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("HCFE0404", "ECC04");  //#4HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("HCFE0403", "ECC03");  //#4HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("HCFE0402", "ECC02");  //#4HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("HCFE0401", "ECC01");  //#4HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("HCFD0406", "DCC06");  //#4HOT FINAL 출측8번지
		h_hstEqpGpMatch.put("HCFD0407", "DCC07");  //#4HOT FINAL 출측9번지
		h_hstEqpGpMatch.put("HCFD0408", "DCC08");  //#4HOT FINAL 출측10번지
		h_hstEqpGpMatch.put("HCFD0409", "DCC09");  //#4HOT FINAL 출측11번지
		
		h_hstEqpGpMatch.put("HBFE0501", "K5-01");  //#5HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("HBFE0502", "K5-02");  //#5HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("HBFE0503", "K5-03");  //#5HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("HBFE0504", "K5-04");  //#5HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("HBFE0505", "K5-05");  //#5HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("HBFE0506", "K5-06");  //#5HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("HBFE0507", "K5-07");  //#5HOT FINAL 입측7번지
		h_hstEqpGpMatch.put("HBFE0508", "K5-08");  //#5HOT FINAL 입측8번지
		h_hstEqpGpMatch.put("HBFE0509", "K5-09");  //#5HOT FINAL 입측9번지
		h_hstEqpGpMatch.put("HBFE0510", "K5-10");  //#5HOT FINAL 입측10번지
		h_hstEqpGpMatch.put("HBFE0511", "K5-11");  //#5HOT FINAL 입측11번지
		h_hstEqpGpMatch.put("HBFE0512", "K5-12");  //#5HOT FINAL 입측12번지
		h_hstEqpGpMatch.put("HBFE0513", "K5-13");  //#5HOT FINAL 입측13번지
		h_hstEqpGpMatch.put("HBFE0514", "K5-14");  //#5HOT FINAL 입측14번지
		h_hstEqpGpMatch.put("HBFE0515", "K5-15");  //#5HOT FINAL 입측15번지
		h_hstEqpGpMatch.put("HBFE0516", "K5-16");  //#5HOT FINAL 입측16번지
		h_hstEqpGpMatch.put("HBFE0517", "K5-17");  //#5HOT FINAL 입측17번지
		h_hstEqpGpMatch.put("HBFE0518", "K5-18");  //#5HOT FINAL 입측18번지
		h_hstEqpGpMatch.put("HBFE0519", "K5-19");  //#5HOT FINAL 입측19번지
		h_hstEqpGpMatch.put("HBFE0520", "K5-20");  //#5HOT FINAL 입측20번지
		//이슈ID:11763
		h_hstEqpGpMatch.put("HAFE0501", "K5-01");  //#5HOT FINAL 입측1번지
		h_hstEqpGpMatch.put("HAFE0502", "K5-02");  //#5HOT FINAL 입측2번지
		h_hstEqpGpMatch.put("HAFE0503", "K5-03");  //#5HOT FINAL 입측3번지
		h_hstEqpGpMatch.put("HAFE0504", "K5-04");  //#5HOT FINAL 입측4번지
		h_hstEqpGpMatch.put("HAFE0505", "K5-05");  //#5HOT FINAL 입측5번지
		h_hstEqpGpMatch.put("HAFE0506", "K5-06");  //#5HOT FINAL 입측6번지
		h_hstEqpGpMatch.put("HAFE0507", "K5-07");  //#5HOT FINAL 입측7번지
		h_hstEqpGpMatch.put("HAFE0508", "K5-08");  //#5HOT FINAL 입측8번지
		h_hstEqpGpMatch.put("HAFE0509", "K5-09");  //#5HOT FINAL 입측9번지
		h_hstEqpGpMatch.put("HAFE0510", "K5-10");  //#5HOT FINAL 입측10번지
		h_hstEqpGpMatch.put("HAFE0511", "K5-11");  //#5HOT FINAL 입측11번지
		h_hstEqpGpMatch.put("HAFE0512", "K5-12");  //#5HOT FINAL 입측12번지
		h_hstEqpGpMatch.put("HAFE0513", "K5-13");  //#5HOT FINAL 입측13번지
		h_hstEqpGpMatch.put("HAFE0514", "K5-14");  //#5HOT FINAL 입측14번지
		h_hstEqpGpMatch.put("HAFE0515", "K5-15");  //#5HOT FINAL 입측15번지
		h_hstEqpGpMatch.put("HAFE0516", "K5-16");  //#5HOT FINAL 입측16번지
		h_hstEqpGpMatch.put("HAFE0517", "K5-17");  //#5HOT FINAL 입측17번지
		h_hstEqpGpMatch.put("HAFE0518", "K5-18");  //#5HOT FINAL 입측18번지
		h_hstEqpGpMatch.put("HAFE0519", "K5-19");  //#5HOT FINAL 입측19번지
		h_hstEqpGpMatch.put("HAFE0520", "K5-20");  //#5HOT FINAL 입측20번지
		
		//==============================================================
		// 2009.09.04 권오창
		// 조업과 야드의 설비구분 매칭값(이상함:결속대만 사용함)
		//==============================================================
		h_hRvsstEqpGpMatch.put("ECC06", "HGFE0106");  //#1HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("ECC05", "HGFE0105");  //#1HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("ECC04", "HGFE0104");  //#1HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("ECC03", "HGFE0103");  //#1HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("ECC02", "HGFE0102");  //#1HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("ECC01", "HGFE0101");  //#1HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("TATI", "HGST0101");  //C열연 정정 #1HOT FINAL Coil Station
		h_hRvsstEqpGpMatch.put("ECC" , "HGHE0101");  //C열연 정정 #1HOT FINAL  Enter Coil Car 
		h_hRvsstEqpGpMatch.put("DCC08", "HGFD0108");  //#1HOT FINAL 출측8번지
		h_hRvsstEqpGpMatch.put("DCC09", "HGFD0109");  //#1HOT FINAL 출측9번지
		h_hRvsstEqpGpMatch.put("DCC10", "HGFD0110");  //#1HOT FINAL 출측10번지
		h_hRvsstEqpGpMatch.put("DCC11", "HGFD0111");  //#1HOT FINAL 출측11번지
		h_hRvsstEqpGpMatch.put("K2-01", "HFFE0201");  //#2HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("K2-02", "HFFE0202");  //#2HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("K2-03", "HFFE0203");  //#2HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("K2-04", "HFFE0204");  //#2HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("K2-05", "HFFE0205");  //#2HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("K2-06", "HFFE0206");  //#2HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("K2-07", "HFFE0207");  //#2HOT FINAL 입측7번지
		h_hRvsstEqpGpMatch.put("K2-08", "HFFE0208");  //#2HOT FINAL 입측8번지
		h_hRvsstEqpGpMatch.put("K2-09", "HFFE0209");  //#2HOT FINAL 입측9번지
		h_hRvsstEqpGpMatch.put("K2-10", "HFFE0210");  //#2HOT FINAL 입측10번지
		h_hRvsstEqpGpMatch.put("K2-11", "HFFE0211");  //#2HOT FINAL 입측11번지
		h_hRvsstEqpGpMatch.put("K2-12", "HFFE0212");  //#2HOT FINAL 입측12번지
		h_hRvsstEqpGpMatch.put("K3-01", "HDFE0301");  //#3HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("K3-02", "HDFE0302");  //#3HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("K3-03", "HDFE0303");  //#3HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("K3-04", "HDFE0304");  //#3HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("K3-05", "HDFE0305");  //#3HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("K3-06", "HDFE0306");  //#3HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("K3-07", "HDFE0307");  //#3HOT FINAL 입측7번지
		h_hRvsstEqpGpMatch.put("K3-08", "HDFE0308");  //#3HOT FINAL 입측8번지
		h_hRvsstEqpGpMatch.put("K3-09", "HDFE0309");  //#3HOT FINAL 입측9번지
		h_hRvsstEqpGpMatch.put("K3-10", "HDFE0310");  //#3HOT FINAL 입측10번지
		h_hRvsstEqpGpMatch.put("K3-11", "HDFE0311");  //#3HOT FINAL 입측11번지
		h_hRvsstEqpGpMatch.put("K3-12", "HDFE0312");  //#3HOT FINAL 입측12번지
		h_hRvsstEqpGpMatch.put("K3-13", "HDFE0313");  //#3HOT FINAL 입측13번지
		h_hRvsstEqpGpMatch.put("K3-14", "HDFE0314");  //#3HOT FINAL 입측14번지
		h_hRvsstEqpGpMatch.put("K3-15", "HDFE0315");  //#3HOT FINAL 입측15번지
		h_hRvsstEqpGpMatch.put("K3-16", "HDFE0316");  //#3HOT FINAL 입측16번지
		h_hRvsstEqpGpMatch.put("K3-17", "HDFE0317");  //#3HOT FINAL 입측17번지
		h_hRvsstEqpGpMatch.put("K3-18", "HDFE0318");  //#3HOT FINAL 입측18번지
		h_hRvsstEqpGpMatch.put("K3-19", "HDFE0319");  //#3HOT FINAL 입측19번지
		h_hRvsstEqpGpMatch.put("K3-20", "HDFE0320");  //#3HOT FINAL 입측20번지
		h_hRvsstEqpGpMatch.put("ECC06", "HHKE0106");  //C열연 정정 SPM1 입측6번지
		h_hRvsstEqpGpMatch.put("ECC05", "HHKE0105");  //C열연 정정 SPM1 입측5번지
		h_hRvsstEqpGpMatch.put("ECC04", "HHKE0104");  //C열연 정정 SPM1 입측4번지
		h_hRvsstEqpGpMatch.put("ECC03", "HHKE0103");  //C열연 정정 SPM1 입측3번지
		h_hRvsstEqpGpMatch.put("ECC02", "HHKE0102");  //C열연 정정 SPM1 입측2번지
		h_hRvsstEqpGpMatch.put("ECC01", "HHKE0101");  //C열연 정정 SPM1 입측1번지
		h_hRvsstEqpGpMatch.put("ENT" , "HHNT0101");  //C열연 정정 SPM1 Enter Coil Car
		h_hRvsstEqpGpMatch.put("DCC01", "HHKD0101");  //C열연 정정 SPM1 출측1번지		
		h_hRvsstEqpGpMatch.put("DCC08", "HHKD0108");  //C열연 정정 SPM1 출측8번지
		h_hRvsstEqpGpMatch.put("DCC09", "HHKD0109");  //C열연 정정 SPM1 출측9번지
		h_hRvsstEqpGpMatch.put("DCC10", "HHKD0110");  //C열연 정정 SPM1 출측10번지
		h_hRvsstEqpGpMatch.put("DCC11", "HHKD0111");  //C열연 정정 SPM1 출측11번지
		h_hRvsstEqpGpMatch.put("ECC06", "HEDE0106");  //C열연 정정 SPM2 입측6번지
		h_hRvsstEqpGpMatch.put("ECC05", "HEDE0105");  //C열연 정정 SPM2 입측5번지
		h_hRvsstEqpGpMatch.put("ECC04", "HEDE0104");  //C열연 정정 SPM2 입측4번지
		h_hRvsstEqpGpMatch.put("ECC03", "HEDE0103");  //C열연 정정 SPM2 입측3번지
		h_hRvsstEqpGpMatch.put("ECC02", "HEDE0102");  //C열연 정정 SPM2 입측2번지
		h_hRvsstEqpGpMatch.put("ECC01", "HEDE0101");  //C열연 정정 SPM2 입측1번지
		h_hRvsstEqpGpMatch.put("ENT" , "HENT0101");  //C열연 정정 SPM2 Enter Coil Car 
		h_hRvsstEqpGpMatch.put("DCC01", "HEDD0101");  //C열연 정정 SPM2 출측1번지
		h_hRvsstEqpGpMatch.put("DCC08", "HEDD0108");  //C열연 정정 SPM2 출측8번지
		h_hRvsstEqpGpMatch.put("DCC09", "HEDD0109");  //C열연 정정 SPM2 출측9번지
		h_hRvsstEqpGpMatch.put("DCC10", "HEDD0110");  //C열연 정정 SPM2 출측10번지
		h_hRvsstEqpGpMatch.put("DCC11", "HEDD0111");  //C열연 정정 SPM2 출측11번지
		
		//이슈ID:11763
		h_hRvsstEqpGpMatch.put("K5-01", "HAFE0501");  //#5HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("K5-02", "HAFE0502");  //#5HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("K5-03", "HAFE0503");  //#5HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("K5-04", "HAFE0504");  //#5HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("K5-05", "HAFE0505");  //#5HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("K5-06", "HAFE0506");  //#5HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("K5-07", "HAFE0507");  //#5HOT FINAL 입측7번지
		h_hRvsstEqpGpMatch.put("K5-08", "HAFE0508");  //#5HOT FINAL 입측8번지
		h_hRvsstEqpGpMatch.put("K5-09", "HAFE0509");  //#5HOT FINAL 입측9번지
		h_hRvsstEqpGpMatch.put("K5-10", "HAFE0510");  //#5HOT FINAL 입측10번지
		h_hRvsstEqpGpMatch.put("K5-11", "HAFE0511");  //#5HOT FINAL 입측11번지
		h_hRvsstEqpGpMatch.put("K5-12", "HAFE0512");  //#5HOT FINAL 입측12번지
		h_hRvsstEqpGpMatch.put("K5-13", "HAFE0513");  //#5HOT FINAL 입측13번지
		h_hRvsstEqpGpMatch.put("K5-14", "HAFE0514");  //#5HOT FINAL 입측14번지
		h_hRvsstEqpGpMatch.put("K5-15", "HAFE0515");  //#5HOT FINAL 입측15번지
		h_hRvsstEqpGpMatch.put("K5-16", "HAFE0516");  //#5HOT FINAL 입측16번지
		h_hRvsstEqpGpMatch.put("K5-17", "HAFE0517");  //#5HOT FINAL 입측17번지
		h_hRvsstEqpGpMatch.put("K5-18", "HAFE0518");  //#5HOT FINAL 입측18번지
		h_hRvsstEqpGpMatch.put("K5-19", "HAFE0519");  //#5HOT FINAL 입측19번지
		h_hRvsstEqpGpMatch.put("K5-20", "HAFE0520");  //#5HOT FINAL 입측20번지
		
		
		h_hRvsstEqpGpMatch.put("K5-01", "HBFE0501");  //#5HOT FINAL 입측1번지
		h_hRvsstEqpGpMatch.put("K5-02", "HBFE0502");  //#5HOT FINAL 입측2번지
		h_hRvsstEqpGpMatch.put("K5-03", "HBFE0503");  //#5HOT FINAL 입측3번지
		h_hRvsstEqpGpMatch.put("K5-04", "HBFE0504");  //#5HOT FINAL 입측4번지
		h_hRvsstEqpGpMatch.put("K5-05", "HBFE0505");  //#5HOT FINAL 입측5번지
		h_hRvsstEqpGpMatch.put("K5-06", "HBFE0506");  //#5HOT FINAL 입측6번지
		h_hRvsstEqpGpMatch.put("K5-07", "HBFE0507");  //#5HOT FINAL 입측7번지
		h_hRvsstEqpGpMatch.put("K5-08", "HBFE0508");  //#5HOT FINAL 입측8번지
		h_hRvsstEqpGpMatch.put("K5-09", "HBFE0509");  //#5HOT FINAL 입측9번지
		h_hRvsstEqpGpMatch.put("K5-10", "HBFE0510");  //#5HOT FINAL 입측10번지
		h_hRvsstEqpGpMatch.put("K5-11", "HBFE0511");  //#5HOT FINAL 입측11번지
		h_hRvsstEqpGpMatch.put("K5-12", "HBFE0512");  //#5HOT FINAL 입측12번지
		h_hRvsstEqpGpMatch.put("K5-13", "HBFE0513");  //#5HOT FINAL 입측13번지
		h_hRvsstEqpGpMatch.put("K5-14", "HBFE0514");  //#5HOT FINAL 입측14번지
		h_hRvsstEqpGpMatch.put("K5-15", "HBFE0515");  //#5HOT FINAL 입측15번지
		h_hRvsstEqpGpMatch.put("K5-16", "HBFE0516");  //#5HOT FINAL 입측16번지
		h_hRvsstEqpGpMatch.put("K5-17", "HBFE0517");  //#5HOT FINAL 입측17번지
		h_hRvsstEqpGpMatch.put("K5-18", "HBFE0518");  //#5HOT FINAL 입측18번지
		h_hRvsstEqpGpMatch.put("K5-19", "HBFE0519");  //#5HOT FINAL 입측19번지
		h_hRvsstEqpGpMatch.put("K5-20", "HBFE0520");  //#5HOT FINAL 입측20번지
		
		// 151210 hun 지포장 코드 추가
		h_hRvsstEqpGpMatch.put("G1-01", "HBGF0101");  //B동 지포장 1번지
		h_hRvsstEqpGpMatch.put("G1-02", "HBGF0102");  //B동 지포장 2번지
		h_hRvsstEqpGpMatch.put("G1-03", "HBGF0103");  //B동 지포장 3번지
		h_hRvsstEqpGpMatch.put("G1-04", "HBGF0104");  //B동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G1-05", "HBGF0105");  //B동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G1-06", "HBGF0106");  //B동 지포장 6번지
		
		h_hRvsstEqpGpMatch.put("G2-01", "HCGF0101");  //C동 지포장 1번지
		h_hRvsstEqpGpMatch.put("G2-02", "HCGF0102");  //C동 지포장 2번지
		h_hRvsstEqpGpMatch.put("G2-03", "HCGF0103");  //C동 지포장 3번지
		h_hRvsstEqpGpMatch.put("G2-04", "HCGF0104");  //C동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G2-05", "HCGF0105");  //C동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G2-06", "HCGF0106");  //C동 지포장 6번지
		
		h_hRvsstEqpGpMatch.put("G3-01", "HEGF0101");  //E동 지포장 1번지
		h_hRvsstEqpGpMatch.put("G3-02", "HEGF0102");  //E동 지포장 2번지
		h_hRvsstEqpGpMatch.put("G3-03", "HEGF0103");  //E동 지포장 3번지
		h_hRvsstEqpGpMatch.put("G3-04", "HEGF0104");  //E동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G3-05", "HEGF0105");  //E동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G3-06", "HEGF0106");  //E동 지포장 6번지
		
		h_hRvsstEqpGpMatch.put("G4-01", "HHGF0101");  //H동 지포장 1번지
		h_hRvsstEqpGpMatch.put("G4-02", "HHGF0102");  //H동 지포장 2번지
		h_hRvsstEqpGpMatch.put("G4-03", "HHGF0103");  //H동 지포장 3번지
		h_hRvsstEqpGpMatch.put("G4-04", "HHGF0104");  //H동 지포장 4번지
		h_hRvsstEqpGpMatch.put("G4-05", "HHGF0105");  //H동 지포장 5번지
		h_hRvsstEqpGpMatch.put("G4-06", "HHGF0106");  //H동 지포장 6번지
		
		h_hstEqpGpMatch.put("HBGF0101", "G1-01");  //B동 지포장 1번지
		h_hstEqpGpMatch.put("HBGF0102", "G1-02");  //B동 지포장 2번지
		h_hstEqpGpMatch.put("HBGF0103", "G1-03");  //B동 지포장 3번지
		h_hstEqpGpMatch.put("HBGF0104", "G1-04");  //B동 지포장 4번지
		h_hstEqpGpMatch.put("HBGF0105", "G1-05");  //B동 지포장 5번지
		h_hstEqpGpMatch.put("HBGF0106", "G1-06");  //B동 지포장 6번지

		h_hstEqpGpMatch.put("HCGF0101", "G2-01");  //C동 지포장 1번지
		h_hstEqpGpMatch.put("HCGF0102", "G2-02");  //C동 지포장 2번지
		h_hstEqpGpMatch.put("HCGF0103", "G2-03");  //C동 지포장 3번지
		h_hstEqpGpMatch.put("HCGF0104", "G2-04");  //C동 지포장 4번지
		h_hstEqpGpMatch.put("HCGF0105", "G2-05");  //C동 지포장 5번지
		h_hstEqpGpMatch.put("HCGF0106", "G2-06");  //C동 지포장 6번지

		h_hstEqpGpMatch.put("HEGF0101", "G3-01");  //E동 지포장 1번지
		h_hstEqpGpMatch.put("HEGF0102", "G3-02");  //E동 지포장 2번지
		h_hstEqpGpMatch.put("HEGF0103", "G3-03");  //E동 지포장 3번지
		h_hstEqpGpMatch.put("HEGF0104", "G3-04");  //E동 지포장 4번지
		h_hstEqpGpMatch.put("HEGF0105", "G3-05");  //E동 지포장 5번지
		h_hstEqpGpMatch.put("HEGF0106", "G3-06");  //E동 지포장 6번지

		h_hstEqpGpMatch.put("HHGF0101", "G4-01");  //H동 지포장 1번지
		h_hstEqpGpMatch.put("HHGF0102", "G4-02");  //H동 지포장 2번지
		h_hstEqpGpMatch.put("HHGF0103", "G4-03");  //H동 지포장 3번지
		h_hstEqpGpMatch.put("HHGF0104", "G4-04");  //H동 지포장 4번지
		h_hstEqpGpMatch.put("HHGF0105", "G4-05");  //H동 지포장 5번지
		h_hstEqpGpMatch.put("HHGF0106", "G4-06");  //H동 지포장 6번지

		//이퀄라이저 
		h_hstEqpGpMatch.put("1EQE0108", "ECC08");  //#EQL 입측8번지
		h_hstEqpGpMatch.put("1EQE0107", "ECC07");  //#EQL 입측7번지
		h_hstEqpGpMatch.put("1EQE0106", "ECC06");  //#EQL 입측6번지
		h_hstEqpGpMatch.put("1EQE0105", "ECC05");  //#EQL 입측5번지
		h_hstEqpGpMatch.put("1EQE0104", "ECC04");  //#EQL 입측4번지
		h_hstEqpGpMatch.put("1EQE0103", "ECC03");  //#EQL 입측3번지
		h_hstEqpGpMatch.put("1EQE0102", "ECC02");  //#EQL 입측2번지
		h_hstEqpGpMatch.put("1EQE0101", "ECC01");  //#EQL 입측1번지
		
		h_hstEqpGpMatch.put("1GQD0108", "DCC08");  //#EQL 출측8번지
		h_hstEqpGpMatch.put("1GQD0107", "DCC07");  //#EQL 출측7번지
		h_hstEqpGpMatch.put("1GQD0106", "DCC06");  //#EQL 출측6번지
		h_hstEqpGpMatch.put("1GQD0105", "DCC05");  //#EQL 출측5번지
		h_hstEqpGpMatch.put("1GQD0104", "DCC04");  //#EQL 출측4번지
		h_hstEqpGpMatch.put("1GQD0103", "DCC03");  //#EQL 출측3번지
		h_hstEqpGpMatch.put("1GQD0102", "DCC02");  //#EQL 출측2번지
		h_hstEqpGpMatch.put("1GQD0101", "DCC01");  //#EQL 출측1번지
		
		//한시적사용(이퀄라이저)
		h_hstEqpGpMatch.put("1FQE0108", "ECC08");  //#EQL 입측8번지
		h_hstEqpGpMatch.put("1FQE0107", "ECC07");  //#EQL 입측7번지
		h_hstEqpGpMatch.put("1FQE0106", "ECC06");  //#EQL 입측6번지
		h_hstEqpGpMatch.put("1FQE0105", "ECC05");  //#EQL 입측5번지
		h_hstEqpGpMatch.put("1FQE0104", "ECC04");  //#EQL 입측4번지
		h_hstEqpGpMatch.put("1FQE0103", "ECC03");  //#EQL 입측3번지
		h_hstEqpGpMatch.put("1FQE0102", "ECC02");  //#EQL 입측2번지
		h_hstEqpGpMatch.put("1FQE0101", "ECC01");  //#EQL 입측1번지
		
		h_hstEqpGpMatch.put("1FQE0208", "ECC08");  //#EQL 입측8번지
		h_hstEqpGpMatch.put("1FQE0207", "ECC07");  //#EQL 입측7번지
		h_hstEqpGpMatch.put("1FQE0206", "ECC06");  //#EQL 입측6번지
		h_hstEqpGpMatch.put("1FQE0205", "ECC05");  //#EQL 입측5번지
		h_hstEqpGpMatch.put("1FQE0204", "ECC04");  //#EQL 입측4번지
		h_hstEqpGpMatch.put("1FQE0203", "ECC03");  //#EQL 입측3번지
		h_hstEqpGpMatch.put("1FQE0202", "ECC02");  //#EQL 입측2번지
		h_hstEqpGpMatch.put("1FQE0201", "ECC01");  //#EQL 입측1번지
		
		h_hstEqpGpMatch.put("1FQD0108", "DCC08");  //#EQL 출측8번지
		h_hstEqpGpMatch.put("1FQD0107", "DCC07");  //#EQL 출측7번지
		h_hstEqpGpMatch.put("1FQD0106", "DCC06");  //#EQL 출측6번지
		h_hstEqpGpMatch.put("1FQD0105", "DCC05");  //#EQL 출측5번지
		h_hstEqpGpMatch.put("1FQD0104", "DCC04");  //#EQL 출측4번지
		h_hstEqpGpMatch.put("1FQD0103", "DCC03");  //#EQL 출측3번지
		h_hstEqpGpMatch.put("1FQD0102", "DCC02");  //#EQL 출측2번지
		h_hstEqpGpMatch.put("1FQD0101", "DCC01");  //#EQL 출측1번지
 
 
	 }
	
	
	/**
	 * 생성자 : 외부에서 직접 호출 불가능
	 */
	private YdCommonUtils() {}
	
	/**
	 * TC CODE에 따른 LEVEL2 SOCKET PORT 반환
	 * @param szTcCode
	 * @return 
	 */
	public static int getPortByUsingCode(String szTcCode) {
		String szMethodName = "getPortByUsingCode";
		String szLogMsg = "";
        String szGpCode = "";
        String szPort   = "";
        
        try{
	        szGpCode = szTcCode.substring(0, 2);
			ydUtils.putLog(szClassName, szMethodName, "TCCODE : " + szGpCode, 4);

			szPort = (String)h_MPCodeL2.get(szGpCode);
		}catch (Exception e){
			szLogMsg = szMethodName + " Exception Error : " + e.getLocalizedMessage();
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			return -1;
		}finally{
			szLogMsg = "Mapping TC Code : " + szPort;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		}

		return Integer.parseInt(szPort);
	}
	
	
	/**
	 * 오퍼레이션명 : 야드별 재료품목과 야드목표행선구분에 따른 대차번호 선택
	 * @param szYD_GP			: 야드구분
	 * @param szYD_MTL_ITEM		: 야드재료품목
	 * @param szYD_AIM_RT_GP	: 야드목표행선구분
	 * @return
	 */
	public static String sltTCarNoByYD(String szYD_GP, String szYD_MTL_ITEM, String szYD_AIM_RT_GP ) {
		String szTCarNo = "";
		if( szYD_GP.equals("A")) {			//C연주
			if( ( szYD_MTL_ITEM.equals("BH") || szYD_MTL_ITEM.equals("SH") ) && ( szYD_AIM_RT_GP.equals("C7") ) ) {		//열연주편이고 B열연이송대기이면 #1대차 배정
				szTCarNo = "01";
			}else if( szYD_MTL_ITEM.equals("BH") && 
					( szYD_AIM_RT_GP.equals("C2") || szYD_AIM_RT_GP.equals("C3") ) ) {		//열연주편이고 C열연 HCR 지시대기 OR C열연 CCR 지시대기이면 #2대차 배정
				szTCarNo = "02";
			}else if( szYD_MTL_ITEM.equals("BP") && 
					( szYD_AIM_RT_GP.equals("C4") || szYD_AIM_RT_GP.equals("C5") || szYD_AIM_RT_GP.equals("C6")  ) ) {		//후판주편이고 H-스카핑 보급대기 OR M-스카핑 보급대기 OR 정정 보급대기이면 #3대차 배정
				szTCarNo = "03";
			}else if( szYD_MTL_ITEM.equals("SH") && szYD_AIM_RT_GP.equals("C1") ) {		//열연슬라브이고 C연주 C열연 가열로 보급 대기이면 #2대차 배정
				szTCarNo = "02";
			}else if( szYD_AIM_RT_GP.equals("C8") || /* 통합야드이송 */
					szYD_AIM_RT_GP.equals("C9") || /* 출하대기 */
					szYD_AIM_RT_GP.equals("CB") || /* 충당대기 */
					szYD_AIM_RT_GP.equals("CC") ) {	/* 장기재이송대기이면 #1대차 배정 */
				szTCarNo = "01";
			}
		}
		return szTCarNo;
	}
	
	/**
	 * 오퍼레이션명 : 야드별 재료품목과 야드목표행선구분에 따른 대차 선택
	 * @param szYD_GP			: 야드구분
	 * @param szYD_MTL_ITEM		: 야드재료품목
	 * @param szYD_AIM_RT_GP	: 야드목표행선구분
	 * @return
	 */
	public static String sltTCarByYD(String szYD_GP, String szYD_BAY_GP, String szYD_AIM_GP, String szYD_AIM_BAY_GP, String szYD_MTL_ITEM, String szYD_AIM_RT_GP ) {
		String szTCar = "";
		if(!(szYD_GP.equals(szYD_AIM_GP) && szYD_BAY_GP.equals(szYD_AIM_BAY_GP))) {
			String szTCarNo = sltTCarNoByYD(szYD_GP, szYD_MTL_ITEM, szYD_AIM_RT_GP);
			if( !szTCarNo.equals("") ) {
				szTCar = szYD_GP + "XTC" + szTCarNo;
			}
		}
		return szTCar;
	}
	
	/**
	 * 오퍼레이션명 : 이적을 위한 크레인스케쥴코드 생성
	 * @param recInParam			: Out Parameter
	 * @param szYD_STK_COL_GP		: 현재야드적치열구분
	 * @param szYD_STK_COL_GP_TO	: 목표야드적치열구분
	 * @param szYD_MTL_ITEM			: 재료품목
	 * @param szYD_AIM_RT_GP		: 야드목표행선구분
	 * @return szYD_SCH_CD			: 크레인스케쥴코드
	 * 이슈사항 : Rule 적용 필요 - 어느 저장집합은 어떤 크레인에게 할당할 것인 지 정의가 필요함
	 */
	public static void mkCrnSchCdForMv(JDTORecord recInParam, String szYD_STK_COL_GP, String szYD_STK_COL_GP_TO, 
			String szYD_MTL_ITEM, String szYD_AIM_RT_GP) throws JDTOException {
		String szMethodName = "mkCrnSchCdForMv";
		String szMsg = "";
		String szYD_SCH_CD = "";
		String szTCAR_NO = "";
		if(szYD_STK_COL_GP.substring(0, 2).equals(szYD_STK_COL_GP_TO.substring(0, 2))) {
			//동내이적 - 기준이 미정 : 어느 저장집합은 어떤 크레인에게 할당할 것인 지 정의가 필요함
			szYD_SCH_CD  = szYD_STK_COL_GP.substring(0, 2) + "YD01MM";		//야드(1)+동(1)+YD(2)+ 00 + M(이적) + M(분할없음) 
			szMsg = "동내이적 스케쥴코드 : " + szYD_SCH_CD;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}else{
			//동간이적
			szTCAR_NO = sltTCarNoByYD(szYD_STK_COL_GP.substring(0, 1), szYD_MTL_ITEM, szYD_AIM_RT_GP);
			if( szTCAR_NO.equals("") ) {
				szMsg = "동간이적인 경우 대차 할당 실패";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "TC" + szTCAR_NO + "UM";
			szMsg = "동간이적 스케쥴코드 : " + szYD_SCH_CD;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		recInParam.setField("YD_SCH_CD", szYD_SCH_CD);
	}
	
	/**
	 * 오퍼레이션명 : 동간이적을 위한 크레인스케쥴코드 생성
	 * @param recInParam			: Out Parameter
	 * @param szYD_GP				: 야드구분
	 * @param szYD_BAY_GP			: 동구분
	 * @param szYD_GP_TO			: 목표야드구분
	 * @param szYD_BAY_GP_TO		: 목표동구분
	 * @param szYD_MTL_ITEM			: 재료품목
	 * @param szYD_AIM_RT_GP		: 야드목표행선구분
	 * @return szYD_SCH_CD			: 크레인스케쥴코드
	 * 이슈사항 : Rule 적용 필요 - 어느 저장집합은 어떤 크레인에게 할당할 것인 지 정의가 필요함
	 */
	public static void mkCrnSchCdForMv(JDTORecord recInParam, String szYD_GP, String szYD_BAY_GP, String szYD_GP_TO, String szYD_BAY_GP_TO , 
			String szYD_MTL_ITEM, String szYD_AIM_RT_GP) throws JDTOException {
		String szMethodName = "mkCrnSchCdForMv";
		String szMsg = "";
		String szYD_SCH_CD = "";
		String szTCAR_NO = "";
		if( szYD_GP.equals(szYD_GP_TO) && szYD_BAY_GP.equals(szYD_BAY_GP_TO) ) {
			//동내이적 - 기준이 미정 : 어느 저장집합은 어떤 크레인에게 할당할 것인 지 정의가 필요함
			szYD_SCH_CD  = szYD_GP + szYD_BAY_GP + "YD01MM";		//야드(1)+동(1)+YD(2)+ 01 + M(이적) + M(분할없음) 
			szMsg = "동내이적 스케쥴코드 : " + szYD_SCH_CD;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}else{
			//동간이적
			szTCAR_NO = sltTCarNoByYD(szYD_GP, szYD_MTL_ITEM, szYD_AIM_RT_GP);
			if( szTCAR_NO.equals("") ) {
				szMsg = "동간이적인 경우 대차 할당 실패";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			szYD_SCH_CD = szYD_GP + szYD_BAY_GP + "TC" + szTCAR_NO + "UM";
			szMsg = "동간이적 스케쥴코드 : " + szYD_SCH_CD;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		recInParam.setField("YD_SCH_CD", szYD_SCH_CD);
	}
	
	/**
	 * 오퍼레이션명 : 후판제품의 Piling Zone No를 저장위치로 변환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String[] getY4PilingZoneNo2StrLoc(String szPilingZoneNo) {
		return (String[])htY4_RT_ZONE_BED.get(szPilingZoneNo);
	}
	
	/**
	 * 오퍼레이션명 : 후판제품의 BookOut위치를 저장위치로 변환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String[] getY4BookOutLoc2TFLoc(String szBookOutLoc) {
		return (String[])htY4_RT2TF_ZONE.get(szBookOutLoc);
	}
	
	/**
	 * 오퍼레이션명 : 후판제품의 BookOut위치를 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String[] getY4BookOutLoc(String szBookOutLoc) {
		return (String[])htY4_BOOK_OUT_LOC.get(szBookOutLoc);
	}
	
	/**
	 * 오퍼레이션명 : 후판제품의 BookOut위치를 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getY4ChgBBookOutLoc(String szBookOutLoc) {
		return (String)htY4_CHG_BBOOK_OUT_LOC.get(szBookOutLoc);
	}
	
	/**
	 * 오퍼레이션명 : 후판제품의 BookOut위치를 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getY4ChgCBookOutLoc(String szBookOutLoc) {
		return (String)htY4_CHG_CBOOK_OUT_LOC.get(szBookOutLoc);
	}
	
	/**
	 * 오퍼레이션명 : 후판제품의 BookOut위치를 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getY4ChgABookOutLoc(String szBookOutLoc) {
		return (String)htY4_CHG_ABOOK_OUT_LOC.get(szBookOutLoc);
	}	
	
	/**
	 * 오퍼레이션명 : 후판제품의 BookOut위치를 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getY4ChgC2BBookOutLoc(String szBookOutLoc) {
		return (String)htY4_CHG_C2BBOOK_OUT_LOC.get(szBookOutLoc);
	}		
	
	/**
	 * 오퍼레이션명 : 후판제품의 TF BookOut위치로 연결되어 있는 
	 *                R/T의 적치열 구분을 반환하는 메소드
	 * @param szBookOutLoc
	 * @return
	 */
	public static String getTf2RtStkLoc(String szBookOutLoc) {
		return (String)ht_TF_2_RT_STKLOC.get(szBookOutLoc);
	}
	
	/**
	 * 오퍼레이션명 : 1후판정정야드  L2 R/T 위치를 반환하는 메소드
	 * @param 야드저장위치
	 * @return
	 */
	public static String getY9ChgL2BookOutLoc(String szLoc) { 
		return (String)htY9_CHG_L2BOOK_OUT_LOC.get(szLoc);
	}	
	
	/**
	 * 오퍼레이션명 : 1후판정정야드  L3 R/T 위치를 반환하는 메소드
	 * @param 야드저장위치
	 * @return
	 */
	public static String getY9ChgL3BookOutLoc(String szLoc) { 
		return (String)htY9_CHG_L3BOOK_OUT_LOC.get(szLoc);
	}	
	
	/**
	 * 오퍼레이션명 : 1후판정정야드  L3 크레인설비를 반환하는 메소드
	 * @param 야드저장위치
	 * @return
	 */
	public static String getY9ChgL3CraneInfo(String szNo) { 
		return (String)htY9_CHG_L3CRANE_INFO.get(szNo);
	}	
	
	/**
	 * 오퍼레이션명 : 1후판정정야드  L3저장위치를 반환하는 메소드
	 * @param 야드저장위치
	 * @return
	 */
	public static String getY9ChgL3LocInfo(String szNo) { 
		return (String)htY9_CHG_L3LOC_INFO.get(szNo);
	}	
	
	/**
	 * 오퍼레이션명 : 스케쥴기준을 조회하여 크레인 정보를 반환하는 메소드
	 * @param szYD_SCH_CD
	 * @param recResult
	 * @return int
	 * 			1 : 메소드 호출 성공
	 * 			-1 : 스케쥴금지
	 * 			-2 : 작업크레인고장이고 대체크레인정보가 없는 경우
	 * 			-3  : 작업크레인고장이고 대체크레인 고장인 경우 작업 불가
	 * 			-4 : 스케쥴기준 조회에러
	 * 			-5 : 크레인설비 정보 조회시 에러 발생
	 * @throws JDTOException
	 */
	public static int getCrnInfoByCrnSchRule(String szYD_SCH_CD, JDTORecord recResult) throws JDTOException {
		int intRtnVal	= 1;
		String szMsg = "";
		String szMethodName = "getCrnInfoByCrnSchRule";
		String szCrn = "";
		String szYD_SCH_PRIOR = "9";
		// 리턴 recordSet 생성
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");

		// 스케줄 기준 체크
		boolean blnRtnVal = chkGetSchRule(szYD_SCH_CD, rsResult);
		if (!blnRtnVal) return -4;

		// 레코드 추출
		rsResult.first();
		JDTORecord recPara = rsResult.getRecord();

		// 스케줄CD 체크
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");
		
		// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
		if (szYD_SCH_PROH_EXN.equals("Y")) {

			szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return -1;
		}
		
		// 작업크레인
		String szYD_WRK_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_CRN_PRIOR");
		// 대체크레인유무
		String szYD_ALT_CRN_YN = ydDaoUtils.paraRecChkNull(recPara,"YD_ALT_CRN_YN");
		// 대체크레인
		String szYD_ALT_CRN = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR = ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");
		
		// 작업크레인 설비 상태 체크
		blnRtnVal = eqpStatCheck(szYD_WRK_CRN);

		// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
		if (!blnRtnVal) {

			szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			// 대체크레인의 유무를 체크한다.
			// 대체크레인이 없으면 에러 리턴
			if (!szYD_ALT_CRN_YN.equals("Y")) {

				szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return -2;

			}
			// 대체크레인이 있으면 대체크레인 설비 상태 체크
			blnRtnVal = eqpStatCheck(szYD_ALT_CRN);
			// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
			if (!blnRtnVal) {

				szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return -3;

			} else {
				// 대체크레인이 사용가능하면 설비사양 파라미터에 대체크레인의 설비ID를 세팅한다.
				szCrn = szYD_ALT_CRN;
				szYD_SCH_PRIOR = szYD_ALT_CRN_PRIOR;
			}
		} else {
			// 작업크레인이 사용가능하면 설비사양 파라미터에 작업크레인의 설비ID를 세팅한다.
			szCrn = szYD_WRK_CRN;
			szYD_SCH_PRIOR = szYD_WRK_CRN_PRIOR;
		}
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		blnRtnVal = chkGetCrnSpec(szCrn, rsResult);
		if( !blnRtnVal ) return -5;
		rsResult.first();
		recPara = rsResult.getRecord();
		//작업가능한 크레인
		recResult.setField("YD_WRK_CRN", szCrn);
		//스케쥴우선순위
		recResult.setField("YD_SCH_PRIOR", szYD_SCH_PRIOR);
		//크레인 작업허용중량
		recResult.setField("YD_WRK_ABLE_WT", ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_WT"));
		//크레인 집게허용 오차
		recResult.setField("YD_CRN_TONG_W_TOL", ydDaoUtils.paraRecChkNull(recPara,"YD_CRN_TONG_W_TOL"));
		//크레인 작업가능 매수
		recResult.setField("YD_WRK_ABLE_SH", ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_SH"));
		
		return 1;
	}
	
	/**
	 * 스케줄기준체크및 주/대체크레인정보반환
	 * @param szYD_SCH_CD
	 * @param recResult
	 * @return	String
	 * 			RETN_CRN_SCH_PROH 			- 스케줄금지
	 * 			RETN_CRN_NO_ALT_CRN			- 주작업크레인이 작업불가능 시 대체크레인이 없을 시
	 * 			YD_EQP_STAT_BREAK			- 주/대체크레인이 고장
	 * 			YD_EQP_WRK_MODE_OFF_LINE	- 주/대체크레인이 OFF-LINE
	 * @throws JDTOException
	 */
	public static String getWrkableCrnBySchRule(String szYD_SCH_CD, JDTORecord recResult) throws JDTOException {
		String szLogMsg				= null;
		String szMethodName			= "getWrkableCrnBySchRule";
		String szOperationName		= "스케줄기준체크및 주/대체크레인정보반환";
		String szRtnMsg				= null;
		
		JDTORecord recPara			= null;
		JDTORecord recTemp			= null;
		JDTORecordSet rsResult		= null;
		
		String szYD_WRK_ALT_GP		= "";
		String szYD_WRKABLE_CRN		= null;
		String szYD_SCH_PRIOR		= null;
		
		String szYD_SCH_PROH_EXN	= null;						//야드스케쥴금지유무
		String szYD_WRK_CRN			= null;						//야드작업크레인
		String szYD_WRK_CRN_STATUS	= null;						//야드작업크레인의 상태
		String szYD_WRK_CRN_PRIOR	= null;						//야드작업크레인우선순위
		String szYD_ALT_CRN_YN		= null;						//야드대체크레인유무
		String szYD_ALT_CRN			= null;						//야드대체크레인
		String szYD_ALT_CRN_STATUS	= null;						//야드대체크레인의 상태
		String szYD_ALT_CRN_PRIOR	= null;						//야드대체크레인우선순위
		
		szLogMsg 			= "["+szOperationName+"] ------------------- 메소드 시작 -------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//------------------------------------------------------------------------------------------
		//	스케줄코드로 스케줄기준 조회
		//------------------------------------------------------------------------------------------
		
		szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]로 스케줄기준 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		
		recPara 		= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_SCH_CD", szYD_SCH_CD);
		
		szRtnMsg		= DaoManager.getYdSchrule(recPara, rsResult, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]로 스케줄기준 조회 시 오류발생 - 반환값 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			
			return szRtnMsg;
		}else{
			if( rsResult.size() > 1 ) {
				
				szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]로 스케줄기준 조회 시 중복됩니다 - 스케줄기준 개수["+rsResult.size()+"]";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CD_DUPLICATE;
			}
		}
		
		szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]로 스케줄기준 조회 완료 - 반환값 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//------------------------------------------------------------------------------------------
		
		
		//------------------------------------------------------------------------------------------
		//	스케줄코드의 스케줄금지 판단.
		//------------------------------------------------------------------------------------------
		
		rsResult.first();
		
		recPara			= rsResult.getRecord();
		
		szYD_SCH_PROH_EXN			= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");			//야드스케쥴금지유무
		
		if( szYD_SCH_PROH_EXN.equals("Y") )	{
			
			szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]는 스케줄금지상태입니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			
			return YdConstant.RETN_CRN_SCH_PROH;
		}
		
		szYD_WRK_CRN				= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");					//야드작업크레인
		szYD_WRK_CRN_PRIOR			= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");			//야드작업크레인우선순위
		szYD_ALT_CRN_YN				= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");				//야드대체크레인유무
		szYD_ALT_CRN				= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");					//야드대체크레인
		szYD_ALT_CRN_PRIOR			= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");			//야드대체크레인우선순위
		
		szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]는 스케줄기동이 가능한 상태입니다.";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		
		//------------------------------------------------------------------------------------------
		
		szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]의 주작업크레인["+szYD_WRK_CRN+"]상태를 체크 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		recPara 		= JDTORecordFactory.getInstance().create();
		
		szRtnMsg		= checkCrnStat(szYD_WRK_CRN, recPara);
		
		szYD_WRK_CRN_STATUS				= szRtnMsg;
		
		//if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			
		szYD_WRK_ALT_GP				= YdConstant.YD_WRK_CRN;
		szYD_WRKABLE_CRN			= szYD_WRK_CRN;
		szYD_SCH_PRIOR				= szYD_WRK_CRN_PRIOR;
		
		if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
		
			szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]의 주작업크레인["+szYD_WRK_CRN+"]상태가 작업가능함 - 우선순위["+szYD_WRK_CRN_PRIOR+"]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		}else{
			
			szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]의 주작업크레인["+szYD_WRK_CRN+"]상태가 작업불가능["+szRtnMsg+"]이므로 대체크레인유무["+szYD_ALT_CRN_YN+"] 비교";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}
			
		//}else{
		//------------------------------------------------------------------------------------------
		//	야드대체크레인으로 설비테이블 조회 후 크레인상태 체크 - 고장, OFF-LINE 체크
		//------------------------------------------------------------------------------------------
		
		
		
		if( !szYD_ALT_CRN_YN.equals("Y") ) {
			
			szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]의 주작업크레인["+szYD_WRK_CRN+"]가 작업불가능한 상태에서 대체크레인이 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			
			if( !szYD_WRK_CRN_STATUS.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szRtnMsg			= YdConstant.RETN_CRN_NO_ALT_CRN;
			}
			
		}else{
		
			szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]의 대체크레인["+szYD_ALT_CRN+"]상태를 체크 시작";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			recTemp 		= JDTORecordFactory.getInstance().create();
			
			szRtnMsg		= checkCrnStat(szYD_ALT_CRN, recTemp);
			
			szYD_ALT_CRN_STATUS				= szRtnMsg;
			
			szYD_WRK_ALT_GP				= YdConstant.YD_ALT_CRN;
			szYD_WRKABLE_CRN			= szYD_ALT_CRN;
			szYD_SCH_PRIOR				= szYD_ALT_CRN_PRIOR;
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]의 대체크레인["+szYD_ALT_CRN+"]상태가 작업가능함 - 우선순위["+szYD_ALT_CRN_PRIOR+"]";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
			}else{
				
				szLogMsg 			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]의 대체크레인["+szYD_ALT_CRN+"]상태가 작업불가능["+szRtnMsg+"]함";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				
				//return szRtnMsg;
			}
			
			//------------------------------------------------------------------------------------------
			//	
			//------------------------------------------------------------------------------------------
			
			if( szYD_WRK_CRN_STATUS.equals(YdConstant.RETN_CD_SUCCESS) && szYD_ALT_CRN_STATUS.equals(YdConstant.RETN_CD_SUCCESS)  ) {
				szYD_WRK_ALT_GP				= YdConstant.YD_WRK_CRN;
				szYD_WRKABLE_CRN			= szYD_WRK_CRN;
				szYD_SCH_PRIOR				= szYD_WRK_CRN_PRIOR;
				szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
			}else if( szYD_WRK_CRN_STATUS.equals(YdConstant.RETN_CD_SUCCESS) && !szYD_ALT_CRN_STATUS.equals(YdConstant.RETN_CD_SUCCESS)  ) {
				szYD_WRK_ALT_GP				= YdConstant.YD_WRK_CRN;
				szYD_WRKABLE_CRN			= szYD_WRK_CRN;
				szYD_SCH_PRIOR				= szYD_ALT_CRN_PRIOR;
				szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
			}else if( !szYD_WRK_CRN_STATUS.equals(YdConstant.RETN_CD_SUCCESS) && szYD_ALT_CRN_STATUS.equals(YdConstant.RETN_CD_SUCCESS)  ) {
				szYD_WRK_ALT_GP				= YdConstant.YD_ALT_CRN;
				szYD_WRKABLE_CRN			= szYD_ALT_CRN;
				szYD_SCH_PRIOR				= szYD_ALT_CRN_PRIOR;
				szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
			}else{
				szRtnMsg					= szYD_ALT_CRN_STATUS;
			}
			
			//------------------------------------------------------------------------------------------
		}
			//------------------------------------------------------------------------------------------
		//}
		
		
		//------------------------------------------------------------------------------------------
		
		//주/대체작업크레인 구분자
		recResult.setField("YD_WRK_ALT_GP", 		szYD_WRK_ALT_GP);
		//작업가능한 크레인
		recResult.setField("YD_WRKABLE_CRN", 		szYD_WRKABLE_CRN);
		//주작업크레인
		recResult.setField("YD_WKR_CRN", 			szYD_WRK_CRN);
		//주작업크레인우선순위
		recResult.setField("YD_WRK_CRN_PRIOR", 		szYD_WRK_CRN_PRIOR);
		//주작업크레인상태
		recResult.setField("YD_WRK_CRN_STATUS", 	szYD_WRK_CRN_STATUS);
		//대체작업크레인
		recResult.setField("YD_ALT_CRN", 			szYD_ALT_CRN);
		//대체작업크레인우선순위
		recResult.setField("YD_ALT_CRN_PRIOR", 		szYD_ALT_CRN_PRIOR);
		//대체작업크레인상태
		recResult.setField("YD_ALT_CRN_STATUS", 	szYD_ALT_CRN_STATUS);
		
		
		//------------------------------------------------------------------------------------------------
		//	작업가능한 크레인의 우선순위와 크레인사양
		//------------------------------------------------------------------------------------------------
		
		//스케쥴우선순위
		recResult.setField("YD_SCH_PRIOR", 			szYD_SCH_PRIOR);
		//크레인 작업허용중량
		recResult.setField("YD_WRK_ABLE_WT", 		ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_WT"));
		//크레인 집게허용 오차
		recResult.setField("YD_CRN_TONG_W_TOL", 	ydDaoUtils.paraRecChkNull(recPara,"YD_CRN_TONG_W_TOL"));
		//크레인 작업가능 매수
		recResult.setField("YD_WRK_ABLE_SH", 		ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_SH"));
		
		//------------------------------------------------------------------------------------------------
		
		szLogMsg 			= "["+szOperationName+"] ------------------- Out -------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recResult);
		
		szLogMsg 			= "["+szOperationName+"] ------------------- 메소드 끝 -------------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		//return YdConstant.RETN_CD_SUCCESS;
		return szRtnMsg;
	}
	
	/**
	 * 오퍼레이션명 : 스케줄기준 체크 및 데이터 반환
	 *  
	 * @param  String     szSchCd 스케줄CD
	 * @return boolean    true(성공), false(실패)
	 * @throws JDTOException
	 */
	public static boolean chkGetSchRule(String szSchCd, JDTORecordSet rsResult)throws JDTOException  {
		
		//스케줄기준 DAO
		YdSchRuleDao ydSchRuleDao = new YdSchRuleDao();

		String szMsg              = null;
		String szMethodName       = "chkGetSchRule";
		int intRtnVal             = 0;
		boolean blnRtnVal         = false;
		JDTORecord recPara        = null;
		
		try {
			
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			//스케줄코드
			recPara.setField("YD_SCH_CD", szSchCd);

			//스케줄코드로 스케줄기준 Table 조회
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "스케줄기준 체크 중 Error : " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal = true;
		
	} //end of chkGetSchRule
	
	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *  
	 * @param   String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws JDTOException
	 */
	public static boolean eqpStatCheck(String szEqpId)throws JDTOException  {

		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = null;
		//메소드명
		String szMethodName    = "eqpStatCheck";		
		//설비상태
		String szYD_EQP_STAT   = null;
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;		
		
		try {
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 체크 및 데이터 조회
			blnRtnVal = chkGetEqp(szEqpId, rsResult);
			if (!blnRtnVal) return blnRtnVal;
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			
			//크레인의 상태가 'T'이면 false 리턴.
			// 고장상태 플레그 상수값으로 변경 [2009.12.03 - 이현성]
			if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {
			
				
				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
	
				blnRtnVal = true;
	
			}
		} catch(Exception e) {
			szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
		
	} //end of eqpStatCheck
	
	/**
	 * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szEqpId  설비ID
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public static boolean chkGetEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
		
		//설비 DAO
		YdEqpDao ydEqpDao     = new YdEqpDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetEqp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//설비ID
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetEqp
	
	/**
	 * 오퍼레이션명 : 설비 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szEqpId  설비ID
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public static int getYdEqp(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
		
		//설비 DAO
		YdEqpDao ydEqpDao     = new YdEqpDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetEqp";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//설비ID
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_INT_FAILURE.intValue();
		}
		return intRtnVal;
	} //end of chkGetEqp
	
	/**
	 * 오퍼레이션명 : 크레인사양 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szEqpId  설비ID
	 *         JDTORecordSet rsResult 결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public static boolean chkGetCrnSpec(String szEqpId, JDTORecordSet rsResult)throws JDTOException  {
		
		//크레인사양 DAO
		YdCrnSpecDao ydCrnSpecDao = new YdCrnSpecDao();
		//리턴값(boolean)
		boolean blnRtnVal     = false;
		//리턴값(int)
		int intRtnVal         = 0;
		//메소드명
		String szMethodName   = "chkGetCrnSpec";
		String szMsg          = null;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//크레인 설비ID
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//크레인사양 조회
			intRtnVal = ydCrnSpecDao.getYdCrnspec(recPara, rsResult, 0);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {

				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 parameter error 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				
				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "크레인사양 유무체크 및 조회결과 데이터 반환 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCrnSpec
	
	/**
	 * 오퍼레이션명 : 검색우선순위를 정하는 함수 - 현재 사용되지 않고 있음
	 * @param szYD_GP
	 * @param priority
	 * @return
	 */
	public static String getMvPriority(String szYD_GP, int priority) {
		String szYD_AIM_RT_GP = "";
		if( szYD_GP.equals("A") || szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD)) {  //항만슬라브야드 기능추가 - 2016.01.04 LeeJY
			if( priority == 1 ) {
				szYD_AIM_RT_GP = "C8";		//C연주 통합야드이송대기
			}else if( priority == 2 ) {
				szYD_AIM_RT_GP = "C9";		//C연주 외판출하대기
			}else if( priority == 3 ) {
				szYD_AIM_RT_GP = "CC";		//충당대기(행선정의 필요)
			}else if( priority == 4 ) {
				szYD_AIM_RT_GP = "CB";		//장기여재(행선정의 필요)
			}
		}
		return szYD_AIM_RT_GP;
	}
	/**
	 * 오퍼레이션명 : C열연 정정라인 설비별 목표행선 반환
	 * @param szYD_EQP_ID
	 * @return
	 */
	public static String getYdAimRtGpByEqpForR3(String szYD_EQP_ID) {
		String szYD_AIM_RT_GP = "";
		String szYD_EQP_GP 		= szYD_EQP_ID.substring(2, 4);
		String szYD_EQP_GP2 	= szYD_EQP_ID.substring(2, 6);
		if( szYD_EQP_GP.equals("DE") ) {			//SPM2입측컨베어
//			szYD_AIM_RT_GP = "H5";					//C열연 SPM2보급대기
			szYD_AIM_RT_GP = "CG";					//C열연 SPM2보급대기
		}else if( szYD_EQP_GP.equals("FE") ) {		//HFL입측컨베어
			if( szYD_EQP_GP2.equals("FE01") ) {
				szYD_AIM_RT_GP = "CH";	
			} else if( szYD_EQP_GP2.equals("FE02") ) {
				szYD_AIM_RT_GP = "CI";	
			} else if( szYD_EQP_GP2.equals("FE03") ) {
				szYD_AIM_RT_GP = "CE";	
			}
		}else if( szYD_EQP_GP.equals("KE") ) {		//SPM1입측컨베어
//			szYD_AIM_RT_GP = "H4";					//C열연 SPM1보급대기
			szYD_AIM_RT_GP = "CF";					//C열연 SPM1보급대기
		}
		return szYD_AIM_RT_GP;
	}
	
	/**
	 * 오퍼레이션명 : 컨베어 BED SHIFT
	 *  
	 * @param  String        szYD_EQP_ID     설비ID
	 *         String        szYD_STK_BED_NO 적치BED번호
	 *         JDTORecordSet rsResult        결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public static boolean setShiftStkBed(String szYD_EQP_ID)throws JDTOException  {
		String szMsg        = null;
		String szMethodName = "setShiftStkBed";
		String szOperationName = "컨베어 BED SHIFT";
		boolean blnRtnVal   = false;
		
		JDTORecord recPara     = null;
		JDTORecordSet rsResult = null;
		
		try {
			//결과레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//해당 적치열의 모든 BED 조회
			blnRtnVal = chkGetStkLyr(szYD_EQP_ID, rsResult);
			if (!blnRtnVal) return blnRtnVal;
			
			//rsResult.first();
			//rsResult.next();
			//적치열의 BED수 만큼 루프를 돌아 적치 데이터를 쉬프트한다.
			
			
			for (int Loop_i = rsResult.size()-1; Loop_i >= 1; Loop_i--) {
				rsResult.absolute(Loop_i);
				recPara = rsResult.getRecord();
				recPara.setField("YD_STK_BED_NO", YdUtils.fillSpZr("" + (ydDaoUtils.paraRecChkNullInt(recPara, "YD_STK_BED_NO") - 1), 2, 0));
				
				
				ydUtils.displayRecord(szOperationName, recPara);
				blnRtnVal = setStkLyr(recPara, 0);
				if (!blnRtnVal) return blnRtnVal;
				//rsResult.next();
			}
			recPara = JDTORecordFactory.getInstance().create();
			//해당 적치열의 01 BED 초기화
			//적치열
			recPara.setField("YD_STK_COL_GP",       szYD_EQP_ID);
			//적치BED
			if(rsResult.size() < 10) {
				recPara.setField("YD_STK_BED_NO",       "0" + rsResult.size());
			}else{
				recPara.setField("YD_STK_BED_NO",       "" + rsResult.size());
			}
			
			//적치단
			recPara.setField("YD_STK_LYR_NO",       "001");
			//재료번호
			recPara.setField("STL_NO",              "");
			//적치단 재료상태
			recPara.setField("YD_STK_LYR_MTL_STAT", "E");
                
			
			blnRtnVal = setStkLyr(recPara, 0);
			if (!blnRtnVal) return blnRtnVal;
			
		} catch(JDTOException e) {
			szMsg = "적치단 업데이트 중 Error : " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
			throw new DAOException(e);
		}
		return blnRtnVal;
	} //end of setShiftStkBed
	
	/**
	 * 오퍼레이션명 : 적치단 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStkColGp 적치열구분
	 *         JDTORecordSet rsResult   결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public static boolean chkGetStkLyr(String szStkColGp, JDTORecordSet rsResult) throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "chkGetStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;
		
		try {
			
			//조회 항목  record 생성
			recPara = JDTORecordFactory.getInstance().create();

			//조회 파라미터 레코드 set
			recPara.setField("YD_STK_COL_GP", 	szStkColGp);
			
			//적치단정보 조회
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 5);

			//리턴값 메세지처리
			if (intRtnVal >= 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "적치열구분("  + szStkColGp + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				szMsg = "적치열구분("  + szStkColGp + ")" +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "적치열구분("  + szStkColGp + ")" +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "적치단 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
			throw new DAOException(e);
		}
		return blnRtnVal;
	} //end of chkGetStkLyr
	
	/**
	 * 오퍼레이션명 : 적치단 업데이트
	 *  
	 * @param  JDTORecord recPara 업데이트용 레코드
	 *         int        intGp   업데이트 쿼리 구분자
	 *         String        szStkLyrNo 적치단번호
	 *         JDTORecordSet rsResult   결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public static boolean setStkLyr(JDTORecord recPara, int intGp)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "setStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		
		try {
			
			//적치단정보 업데이트
			intRtnVal = ydStkLyrDao.updYdStklyr(recPara, intGp);

			//리턴값 메세지처리
			if (intRtnVal >= 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -1) {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
				        "로 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
				        "로 적치단 업데이트중 parameter error 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "적치열구분("  + recPara.getFieldString("YD_STK_COL_GP") + ")," +
				        "적치BED번호(" + recPara.getFieldString("YD_STK_BED_NO") + ")," +
				        "적치단번호("  + recPara.getFieldString("YD_STK_LYR_NO") + ")" +
				        " 로 적치단 업데이트중 오류 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "적치단 업데이트 중 Error : " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
			throw new DAOException(e);
		}
		return blnRtnVal;
	} //end of setStkLyr
	
	/**
	 * 오퍼레이션명 : 재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환
	 *  
	 * @param  String        szStlNo   재료번호
	 *         String        szMtlStat 적치단재료상태
	 *         JDTORecordSet rsResult  결과레코드셋
	 * @return boolean       true(성공), false(실패)
	 * @throws JDTOException
	 */
	public static boolean chkGetStlStkLyr(String szStlNo, String szMtlStat, JDTORecordSet rsResult)throws JDTOException  {
		
		//적치단 DAO
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();

		String szMsg        = null;
		String szMethodName = "chkGetStlStkLyr";
		int intRtnVal       = 0;
		boolean blnRtnVal   = false;
		JDTORecord recPara  = null;
		
		try {
			
			//조회 항목  record 생성
			recPara = JDTORecordFactory.getInstance().create();

			//조회 파라미터 레코드 set
			recPara.setField("STL_NO",              szStlNo);
			recPara.setField("YD_STK_LYR_MTL_STAT", szMtlStat);
			
			//적치단정보 조회
			/*com.inisteel.cim.yd.dao.ydstklyrdao.YdStklyrDao.getYdStklyrSTLNO*/
			intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsResult, 3);
			ydUtils.putLog(szClassName, szMethodName, ""+intRtnVal, YdConstant.DEBUG);

			//리턴값 메세지처리
			if (intRtnVal > 1) {
				
				szMsg = "재료번호("      + szStlNo   + ")," +
				        "적치단재료상태(" + szMtlStat + ")," +
				        " 에 대한 적치단 데이터가 중복되었습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == 1) {
				
				blnRtnVal = true;
				
			} else if (intRtnVal == 0) {
				szMsg = "재료번호("      + szStlNo   + ")," +
		                "적치단재료상태(" + szMtlStat + ")," +
						" 에 대한 적치단 데이터가 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else if (intRtnVal == -2) {
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        "로 적치단 조회중 parameter error 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			} else {
				szMsg = "재료번호("      + szStlNo   + ")," +
                        "적치단재료상태(" + szMtlStat + ")," +
				        " 로 적치단 조회중 오류 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				blnRtnVal = false;
				
			}
		} catch(Exception e) {
			szMsg = "재료번호, 적치단재료상태에 대한 적치단 정보 유무체크 및 조회결과 데이터 반환 중 Error : " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetStlStkLyr
	
	/**
	 * 오퍼레이션명 : 야드별로 대차상차스케줄 코드를 부여하는 함수[대차는 C연주슬라브야드(A), 후판슬라브야드(D), C열연코일소재,제품(J)만 존재]
	 * @param szYD_STK_COL_GP
	 * @return
	 */
	public static String[] getSchCdNTcar(String szYD_STK_COL_GP) {
		//메세지값
		String szMsg = null;
		//반환값
		String[] retValue = new String[2];
		//야드구분
		String szYD_GP = szYD_STK_COL_GP.substring(0, 1);
		//스케줄코드
		String szYD_SCH_CD = "";
		//대차설비ID
		String szTCAR = "";
		//메소드명 
		String szMethodName = "getSchCdNTcar";
		
		if( szYD_GP.equals("A") ) {		//야드구분이 C연주슬라브야드 
			if( szYD_STK_COL_GP.substring(2, 4).equals("01") || szYD_STK_COL_GP.substring(2, 4).equals("02") ) {		// 01,02스판은 #1대차 배정
				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "TC01UM";
				szTCAR = szYD_STK_COL_GP.substring(0, 1) + "XTC01";
			}else if( szYD_STK_COL_GP.substring(2, 4).equals("03") ) {													// 03스판은 #2대차 배정
				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "TC02UM";
				szTCAR = szYD_STK_COL_GP.substring(0, 1) + "XTC02";
			}else if( szYD_STK_COL_GP.substring(2, 4).equals("04") ) {													// 04스판은 #3대차 배정
				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "TC03UM";
				szTCAR = szYD_STK_COL_GP.substring(0, 1) + "XTC03";
			}else if( szYD_STK_COL_GP.substring(2, 4).equals("05") ) {													// 04스판은 #3대차 배정
				//03열 이하는 4번 대차 이후는 5번 대차
				if( szYD_STK_COL_GP.substring(4, 6).equals("01") ||
					szYD_STK_COL_GP.substring(4, 6).equals("02") ||
					szYD_STK_COL_GP.substring(4, 6).equals("03")) {
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "TC04UM";
					szTCAR = szYD_STK_COL_GP.substring(0, 1) + "XTC04";
				} else {
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "TC05UM";
					szTCAR = szYD_STK_COL_GP.substring(0, 1) + "XTC05";
				}
			}else if( szYD_STK_COL_GP.substring(2, 4).equals("06") ) {													// 04스판은 #3대차 배정
				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "TC06UM";
				szTCAR = szYD_STK_COL_GP.substring(0, 1) + "XTC06";
			}
		} else if( szYD_GP.equals("D") ) {	//야드구분이 후판슬라브야드 
			szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "TC01UM";
			szTCAR = szYD_STK_COL_GP.substring(0, 1) + "XTC01";
		}else{
			szMsg = "현재 지원하지 않는 야드구분[" + szYD_GP + "]입니다." ;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		retValue[0] = szYD_SCH_CD;
		retValue[1] = szTCAR;
		return retValue;
	}
	
	/**
	 * 오퍼레이션명 : 설비상태 체크 - 주로 크레인의 작업진행 상태를 체크 시 사용됨
	 *  
	 * @param   String szEqpId 설비ID
	 * @return String 설비작업상태
	 * @throws JDTOException
	 */
	public static String getYdEqpStat(String szEqpId)throws JDTOException  {

		//리턴값(boolean)
		boolean blnRtnVal      = false;
		//메세지
		String szMsg           = null;
		//메소드명
		String szMethodName    = "getYdEqpStat";		
		//설비상태
		String szYD_EQP_STAT   = "";
		//레코드 선언
		JDTORecord recPara     = null;
		//레코드셋 선언
		JDTORecordSet rsResult = null;
		
		try {
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);
			
			//설비 체크 및 데이터 조회
			blnRtnVal = chkGetEqp(szEqpId, rsResult);
			if (!blnRtnVal) return "";
			
			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();
			
			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			
			//크레인의 상태가 'T'이면 false 리턴.
			//상수 수정 [2009.12.03 이현성]
			if (szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK)) {
				
				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);

			}
		} catch(Exception e) {
			szMsg = "설비상태 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		return szYD_EQP_STAT;
		
	}
	
	/**
     * JJK
	 * 야드목표행선지구분를 지정한다.
     *
     * @param  String	sItemGp :	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,JDTORecord inRecord
     *
     * @return String
     * @throws  
     */		
	public static String[] getYdAimRtGp(String sItemGp ,JDTORecord inRecord ) {
		//메세지
		String szMsg           	=null;
		String currProgCd 		=null;
		String ydAimRtGp 		=null;
		String sYD_AIM_RT_GP2   ="";
		String sHCR_GP 			="";
		String sSKINPASS_YN 	="";
		//메소드명
		String szMethodName    	= "getYdAimRtGp";
		String sNextProc		= "";	// 다음공정 
		String sPlanProc1		= "";	// 열연계획작업코드1
		int intRtnVal 			= 0;
		String[] rVal 			= new String[2];
		
		JDTORecord recEditInRecord 		= JDTORecordFactory.getInstance().create();
		JDTORecord recStockColumn 		= JDTORecordFactory.getInstance().create();
		
		JDTORecordSet rsGetPlateComm 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetCoilComm 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecordSet rsGetSlabComm 	= JDTORecordFactory.getInstance().createRecordSet("");

		YdStockDao ydStockDao 			= new YdStockDao();
		YdPICommDAO ydPICommDAO = new YdPICommDAO();
		
		try {
//PIDEV			
			//전문받아서 szRcvTcCode에 대입
			String szRcvTcCode=ydUtils.getTcCode(inRecord);
			String sSTL_NO = StringHelper.evl(inRecord.getFieldString("STL_NO"),"");
			String sINFO_GP = StringHelper.evl(inRecord.getFieldString("INFO_GP"),"");
					
			if(sItemGp.equals("P")){
			//수신한 재료번호로 plate공통 읽기***************************************************************************************************
				
				if(!sSTL_NO.equals("")){
					recEditInRecord.setField("PLATE_NO", sSTL_NO);
					intRtnVal = ydStockDao.getYdStock(recEditInRecord, rsGetPlateComm, 4);
					if(intRtnVal <=0 ){
		
						if(intRtnVal == 0){
							szMsg= "plate공통 SELECT Error :: [" + sSTL_NO + "]" +"DO NOT EXIST";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return rVal;
						}else{
							szMsg= "plate공통 SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR" ;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return rVal;
						}
					}
					szMsg=sSTL_NO +" :: plate공통 SELECT Success :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					rsGetPlateComm.first();
					recStockColumn = JDTORecordFactory.getInstance().create();
					recStockColumn = rsGetPlateComm.getRecord();
					
					//진도코드 존제여부 체크 
					if(ydDaoUtils.paraRecChkNull(recStockColumn,"CURR_PROG_CD").equals("")||ydDaoUtils.paraRecChkNull(recStockColumn,"CURR_PROG_CD").equals(null)){
						szMsg = "진도코드가  존재  안 함" ;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						return rVal;
					}
				
					//진도코드
					currProgCd = recStockColumn.getFieldString("CURR_PROG_CD");
				} else {					
					//진도코드
					currProgCd = StringHelper.evl(inRecord.getFieldString("CURR_PROG_CD"),"");
				}
				currProgCd = StringHelper.evl(inRecord.getFieldString("CURR_PROG_CD"),"");
				ydUtils.putLog(szClassName, szMethodName, "진도코드::" +currProgCd, 4);
				
				if(szRcvTcCode.equals("DMYDR006")){		
					ydAimRtGp ="K3";			//출하지시대기
					currProgCd ="K";
				}else if(szRcvTcCode.equals("DMYDR018")){
					ydAimRtGp ="N3";			//운송지시대기
					currProgCd ="N";
				}else if(szRcvTcCode.equals("DMYDR021")){
					ydAimRtGp ="L6";			//운송상차지시
					currProgCd ="L";
				}else if(szRcvTcCode.equals("DMYDR031")){
					ydAimRtGp ="M3";			//출하완료
					currProgCd ="M";
				}else if(currProgCd.equals("Y")){
					ydAimRtGp =currProgCd+"C";	//재공충당대기(A후판plate)
				}else if(currProgCd.equals("G")){
					ydAimRtGp =currProgCd+"3";	//종합판정대기
				}else if(currProgCd.equals("I")){
					ydAimRtGp =currProgCd+"3";	//반송대기
				}else if(currProgCd.equals("H")){
					ydAimRtGp =currProgCd+"3";	//입고대기
				}else if(currProgCd.equals("J")){
					ydAimRtGp =currProgCd+"3";	//반납대기
				}else if(currProgCd.equals("Z")){
					ydAimRtGp =currProgCd+"3";	//제품충당대기
				}else if(currProgCd.equals("X")){
					ydAimRtGp =currProgCd+"3";	//경매대상선정
				}else if(currProgCd.equals("K")){		
					ydAimRtGp =currProgCd+"3";	//출하지시대기
				}
//PIDEV					
				if ("M10LMYDJ1012".equals(szRcvTcCode) && "4".equals(sINFO_GP) ) {
					ydAimRtGp ="K3";			//출하지시대기
					currProgCd ="K";
				}else if ("M10LMYDJ1012".equals(szRcvTcCode) && "5".equals(sINFO_GP) ) {
					ydAimRtGp ="N3";			//운송지시대기
					currProgCd ="N";
				}else if ("M10LMYDJ1072".equals(szRcvTcCode)){
					ydAimRtGp ="M3";			//출하완료
					currProgCd ="M";
				}
			//***************************************************************************************************************************
			}else if(sItemGp.equals("C")){
			//수신한 재료번호로 코일공통 읽기***************************************************************************************************
				if(!sSTL_NO.equals("")){
					recEditInRecord.setField("COIL_NO", sSTL_NO);
					intRtnVal = ydStockDao.getYdStock(recEditInRecord, rsGetCoilComm, 8);	
					if(intRtnVal <=0 ){
		
						if(intRtnVal == 0){
							szMsg= "코일공통 SELECT Error :: [" + sSTL_NO + "]" +"DO NOT EXIST";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return rVal;
						}else{
							szMsg= "코일공통 SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR" ;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return rVal;
						}
					}
					szMsg=inRecord.getFieldString("STL_NO") +" :: 코일공통 SELECT Success :: [" + intRtnVal + "]" ;
					ydUtils.putLog(szClassName, szMethodName, szMsg,3);
					
					rsGetCoilComm.first();
					recStockColumn = JDTORecordFactory.getInstance().create();
					recStockColumn = rsGetCoilComm.getRecord();
					
					sYD_AIM_RT_GP2  	= ydDaoUtils.paraRecChkNull(recStockColumn, "YD_AIM_RT_GP2");
					sHCR_GP  			= ydDaoUtils.paraRecChkNull(recStockColumn, "HCR_GP");
					sSKINPASS_YN		= ydDaoUtils.paraRecChkNull(recStockColumn,"SKINPASS_YN");	
					
					//진도코드 존제여부 체크 
					if(ydDaoUtils.paraRecChkNull(recStockColumn,"CURR_PROG_CD").equals("")||ydDaoUtils.paraRecChkNull(recStockColumn,"CURR_PROG_CD").equals(null)){
						szMsg = "진도코드가  존재  안 함" ;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						return rVal;
					}
					
					//진도코드
					currProgCd = recStockColumn.getFieldString("CURR_PROG_CD");
				} else {					
					//진도코드
					currProgCd = StringHelper.evl(inRecord.getFieldString("CURR_PROG_CD"),"");
				}
				ydUtils.putLog(szClassName, szMethodName, "진도코드::" +currProgCd, 4);
				
				
				//***********************************************************//	
				if(szRcvTcCode.equals("DMYDR005")){		
					ydAimRtGp ="K2";			//출하지시대기
					currProgCd ="K";
				}else if(szRcvTcCode.equals("DMYDR020")){
					ydAimRtGp ="L2";			//운송지시 
					currProgCd ="L";
				}else if(szRcvTcCode.equals("DMYDR023")|| szRcvTcCode.equals("DMYDR060")){
					ydAimRtGp ="L5";			//상차지시
					currProgCd ="L";
				}else if(szRcvTcCode.equals("DMYDR030")){
					ydAimRtGp ="M2";			//출하완료
					currProgCd ="M";
				//***********************************************************//
				}else if(currProgCd.equals("G")){
					ydAimRtGp =currProgCd+"2";	//종합판정대기
				}else if(currProgCd.equals("I")){
					ydAimRtGp =currProgCd+"2";	//반송대기
				}else if(currProgCd.equals("H")){
					ydAimRtGp =currProgCd+"2";	//입고대기
				}else if(currProgCd.equals("Y")){
						ydAimRtGp =currProgCd+"C";	//재공충당대기(C열연정정)
				}else if(currProgCd.equals("B")){	//지시대기
					
					String sWorkProc	= "";	
					sNextProc		=	ydDaoUtils.paraRecChkNull(recStockColumn,"NEXT_PROC");	
					
					//C 동 수입인 경우 에만 spm재와 hfl재를 분리해서 적치 한다.
//					if(sNextProc.substring(0,1).equals("C")){
						if(sNextProc.substring(1,2).equals("H")){
							ydAimRtGp =currProgCd+"3";	//지시대기
						}else {
							ydAimRtGp =currProgCd+"4";	//지시대기
						}
//					}else{
//						//HCR재 - H, WCR재 - W, CCR재 - C
//						if(sHCR_GP.equals("H")){
//							ydAimRtGp =currProgCd+"3";	//지시대기
//						}else {
//							ydAimRtGp =currProgCd+"4";	//지시대기
//						}
//					}
				}else if(currProgCd.equals("J")){
					ydAimRtGp =currProgCd+"2";	//반납대기
//					ydAimRtGp ="B3";	//반납대기 ?????
//				}else if(currProgCd.equals("K")){		
//					ydAimRtGp =currProgCd+"2";	//출하지시대기
//				}else if(currProgCd.equals("L")){
//					if(szRcvTcCode.equals("DMYDR023")){ //코일제품상차지시
//						ydAimRtGp =currProgCd+"5";	//상차대기 
//					}else {
//						ydAimRtGp =currProgCd+"2";	//운송대기
//					}
//				}else if(currProgCd.equals("M")){
//					ydAimRtGp =currProgCd+"2";	//출하완료
				}else if(currProgCd.equals("Z")){
					ydAimRtGp =currProgCd+"2";	//제품충당대기
				}else if(currProgCd.equals("X")){
					ydAimRtGp =currProgCd+"2";	//경매대상선정
				}else if(currProgCd.equals("E") || currProgCd.equals("D")){
												 //재공이송작업대기
					String sWorkProc	= "";	
					sNextProc		=	ydDaoUtils.paraRecChkNull(recStockColumn,"NEXT_PROC");		
					sPlanProc1		=	ydDaoUtils.paraRecChkNull(recStockColumn,"PLAN_PROC1");
					
					if(!"".equals(sNextProc)){
						sWorkProc = sNextProc;
					}else{
						sWorkProc = sPlanProc1;
					}
					// 계획공정정보를 가지고 야드행선을 셋팅
					if(sWorkProc.startsWith("1")){
						ydAimRtGp	= "EA";
					}else if(sWorkProc.startsWith("5")||
							 sWorkProc.startsWith("6")){
						ydAimRtGp	= "EB";
					}else if(sWorkProc.startsWith("9S")){
						ydAimRtGp	= "ED";
					}else{
						ydAimRtGp	= "EC";
					}	
				}else if(currProgCd.equals("C")){
										 //정정작업지시대기
					String sWorkProc	= "";	
					sNextProc		=	ydDaoUtils.paraRecChkNull(recStockColumn,"NEXT_PROC");		
					sPlanProc1		=	ydDaoUtils.paraRecChkNull(recStockColumn,"PLAN_PROC1");
					
					if(!"".equals(sNextProc)){
						sWorkProc = sNextProc;
					}else{
						sWorkProc = sPlanProc1;
					}
					
					ydUtils.putLog(szClassName, szMethodName, "다음공정(계획공정)::" +sWorkProc, 4);
					
					/*
					계획공정코드
						DH C열연 D Line No3HFL C열연 D Line No3HFL(정정LINE구분 : No3HFL) 11 
						DA C열연 D Line 공냉 C열연 D Line 공냉(Hysco向) 12 
						EH C열연 E Line Hot Final C열연 E Line Hot Final(정정LINE구분:SPM2) 13 
						EK C열연 E Line Skin Pass C열연 E Line Skin Pass(정정LINE구분:SPM2) 14 
						ER C열연 E Line Recoiling C열연 E Line Recoiling(정정LINE구분:SPM2) 15 
						EA C열연 E Line 공냉 C열연 E Line 공냉(Hysco向) 16 
						FH C열연 F Line No2HFL C열연 F Line No2HFL(정정LINE구분:No2HFL) 17 
						FA C열연 F Line 공냉 C열연 F Line 공냉(Hysco向) 18 
						GA C열연 G Line 공냉 C열연 G Line 공냉(정정LINE구분:No1HFL) 19 
						GH C열연 G Line No1HFL C열연 G Line No1HFL(정정LINE구분:No1HFL) 20 
						GT C열연 G Line 수냉 C열연 G Line 수냉(정정LINE구분:No1HFL) 21 
						HH C열연 H Line Hot Final C열연 H Line Hot Final(정정LINE구분:SPM1) 22 
						HK C열연 H Line Skin Pass C열연 H Line Skin Pass(정정LINE구분:SPM1) 23 
						HR C열연 H Line Recoiling C열연 H Line Recoiling(정정LINE구분:SPM1) 24 
						HA C열연 H Line 공냉 C열연 H Line 공냉(Hysco向) 25
					야드행선구분 
						CE 작업대기(C열연 HFL)
						CF 작업대기(C열연 SPM1)
						CG 작업대기(C열연 SPM2)
						CH 작업대기(C열연#1결속대)
						CI 작업대기(C열연#2결속대) 
					*/
					
					// 계획공정정보를 가지고 야드행선을 셋팅 _ 추후 다시 셋팅 (C열연만 셋팅 )
					if(sWorkProc.equals("DH")||
					   sWorkProc.equals("FH")||
					   sWorkProc.equals("GA")||
					   sWorkProc.equals("GH")||
					   sWorkProc.equals("CA")||
					   sWorkProc.equals("CH")||
					   sWorkProc.equals("AA")||
					   sWorkProc.equals("BH")||
					   sWorkProc.equals("GT")){
						ydAimRtGp	= "CE";
					}else if(sWorkProc.equals("HH")||
							 sWorkProc.equals("HK")||
							 sWorkProc.equals("HR")){
						ydAimRtGp	= "CF";
					}else if(sWorkProc.equals("EH")||
							 sWorkProc.equals("EK")||
							 sWorkProc.equals("ER")){
						ydAimRtGp	= "CG";
					}else if(sWorkProc.equals("CK")||
							 sWorkProc.equals("CR")){
						ydAimRtGp	= "CF";
					}else if(sWorkProc.equals("BK")||
							 sWorkProc.equals("BR")){
						ydAimRtGp	= "CF";	
					}else if(sWorkProc.equals("AK")||
							 sWorkProc.equals("AR")){
						ydAimRtGp	= "CF";	
					}else {
						ydAimRtGp	= "XX";
					}	
					if(sYD_AIM_RT_GP2.equals("F4") || sYD_AIM_RT_GP2.equals("F5")) {   		//재작업인 경우 
						ydAimRtGp = sYD_AIM_RT_GP2;										   //재작업인(C열연정정)
					}

				
				}else if(currProgCd.equals("F")){

						ydAimRtGp =currProgCd+"3";	//판정보류

				}
				
				//2pass재 작업 대상
				if(sSKINPASS_YN.equals("Z") && (currProgCd.equals("C")||currProgCd.equals("D"))){
					ydAimRtGp	= "EA";
				}
//PIDEV				
				if ("M10LMYDJ1011".equals(szRcvTcCode) && "4".equals(sINFO_GP) ) {
					ydAimRtGp ="K2";			//출하지시대기
					currProgCd ="K";
				} else if ("M10LMYDJ1031".equals(szRcvTcCode)) {					
					ydAimRtGp = "L5"; // 상차지시
					currProgCd = "L";
				} else if ("M10LMYDJ1071".equals(szRcvTcCode)) {
					ydAimRtGp = "M2"; // 출하완료
					currProgCd ="M";
				}	
			//***************************************************************************************************************************
			}else if(sItemGp.equals("S")){
			//수신한 재료번호로 슬라브공통을 읽기 ***************************************************************************************************
				recEditInRecord.setField("SLAB_NO",sSTL_NO);
				intRtnVal = ydStockDao.getYdStock(recEditInRecord, rsGetSlabComm, 2);
				if(intRtnVal <=0 ){
	
					if(intRtnVal == 0){
						szMsg= "슬라브공통을 SELECT Error :: [" + sSTL_NO + "]" +"DO NOT EXIST";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						return rVal;
					}else{
						szMsg= "슬라브공통을 SELECT Error :: [" + intRtnVal + "]"+"PARAMETER ERROR" ;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						return rVal;
					}
				}
				szMsg=sSTL_NO +" :: 슬라브공통을 SELECT Success :: [" + intRtnVal + "]" ;
				ydUtils.putLog(szClassName, szMethodName, szMsg,3);
				
				rsGetSlabComm.first();
				recStockColumn = JDTORecordFactory.getInstance().create();
				recStockColumn = rsGetSlabComm.getRecord();
				
				//진도코드 존제여부 체크 
				if(ydDaoUtils.paraRecChkNull(recStockColumn,"CURR_PROG_CD").equals("")||ydDaoUtils.paraRecChkNull(recStockColumn,"CURR_PROG_CD").equals(null)){
					szMsg = "진도코드가  존재  안 함" ;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					return rVal;
				}
				
				//진도코드
				currProgCd = recStockColumn.getFieldString("CURR_PROG_CD");
				ydUtils.putLog(szClassName, szMethodName, "진도코드::" +currProgCd, 4);
				
				//***********************************************************//	
				if(szRcvTcCode.equals("DMYDR004")){		
					ydAimRtGp ="K1";			//출하지시대기
					currProgCd ="K";
				}else if(szRcvTcCode.equals("DMYDR016")){
					ydAimRtGp ="N1";			//운송지시대기
					currProgCd ="N";
				}else if(szRcvTcCode.equals("DMYDR022")){
					ydAimRtGp ="L4";			//운송상차지시
					currProgCd ="L";
				}else if(szRcvTcCode.equals("DMYDR029")){
					ydAimRtGp ="M1";			//출하완료
					currProgCd ="M";
				//***********************************************************//
				}else if(currProgCd.equals("G")){
					ydAimRtGp =currProgCd+"1";	//종합판정대기
				}else if(currProgCd.equals("H")){
					ydAimRtGp =currProgCd+"1";	//입고대기
				}else if(currProgCd.equals("J")){
					ydAimRtGp =currProgCd+"1";	//반납대기
				}else if(currProgCd.equals("K")){		
					ydAimRtGp =currProgCd+"1";	//출하지시대기
				}else if(currProgCd.equals("L")){
					ydAimRtGp =currProgCd+"1";	//운송대기
				}else if(currProgCd.equals("N")){
					ydAimRtGp =currProgCd+"1";	//운송지시대기
				}else if(currProgCd.equals("M")){
					ydAimRtGp =currProgCd+"1";	//출하완료
				}else if(currProgCd.equals("Z")){
					ydAimRtGp =currProgCd+"1";	//제품충당대기
				}else if(currProgCd.equals("X")){
					ydAimRtGp =currProgCd+"1";	//경매대상선정
				}	
//PIDEV					
				if ("M10LMYDJ1013".equals(szRcvTcCode) && "4".equals(sINFO_GP) ) {	
					ydAimRtGp ="K1";			//출하지시대기
					currProgCd ="K";
				}else if ("M10LMYDJ1013".equals(szRcvTcCode) && "5".equals(sINFO_GP) ) {	
					ydAimRtGp ="N1";			//운송지시대기
					currProgCd ="N";
				} else if ("M10LMYDJ1073".equals(szRcvTcCode)) {
					ydAimRtGp ="M1";			//출하완료
					currProgCd ="M";				
				}
			}
			//***************************************************************************************************************************
		} catch(Exception e) {
			szMsg = "야드목표행선지구분 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "야드목표행선지구분: " + ydAimRtGp;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		rVal[0] =ydAimRtGp ;
		rVal[1] =currProgCd ;
		return rVal;
	}
	
	
	/**
     * JJK
	 * 야드목표행선지구분를 지정한다.
     *
     * @param  String	:	제품구분(S:SLAB, C:COIL ,P: 후판 ) ,제품코드
     *
     * @return String
     * @throws  
     */		
	public static String[] getYdAimRtGp2(String szRcvTcCode ,String sItemGp ,String stl_no ,String currProgCd ) {
		//메세지
		String szMsg           	=null;
		String ydAimRtGp 		=null;
		
		//메소드명
		String szMethodName    	= "getYdAimRtGp";
		String[] rVal 			= new String[1];
		YdPICommDAO ydPICommDAO = new YdPICommDAO();
		
		try {

			if(sItemGp.equals("P")){
			//수신한 재료번호로 plate공통 읽기***************************************************************************************************
				
				//진도코드
				ydUtils.putLog(szClassName, szMethodName, "진도코드::" +currProgCd, 4);
				
				if(currProgCd.equals("H")){
					ydAimRtGp =currProgCd+"3";	//입고대기
				}else if(currProgCd.equals("J")){
					ydAimRtGp =currProgCd+"3";	//반납대기
				}else if(currProgCd.equals("K")){		
					ydAimRtGp =currProgCd+"3";	//출하지시대기
				}else if(currProgCd.equals("N")){
					ydAimRtGp =currProgCd+"3";	//운송지시대기
				}else if(currProgCd.equals("L")){
					if(szRcvTcCode.equals("DMYDR021")){ //후판제품운송상차지시
						ydAimRtGp =currProgCd+"6";	//상차대기 
					}else {
						ydAimRtGp =currProgCd+"3";	//운송대기
					}
				}else if(currProgCd.equals("M")){
					ydAimRtGp =currProgCd+"3";	//출하완료
				}	
			//***************************************************************************************************************************
			}else if(sItemGp.equals("C")){
			//수신한 재료번호로 코일공통 읽기***************************************************************************************************
				
				//진도코드
				ydUtils.putLog(szClassName, szMethodName, "진도코드::" +currProgCd, 4);
				
				if(currProgCd.equals("H")){
					ydAimRtGp =currProgCd+"2";	//입고대기
				}else if(currProgCd.equals("J")){
					ydAimRtGp =currProgCd+"2";	//반납대기
				}else if(currProgCd.equals("K")){		
					ydAimRtGp =currProgCd+"2";	//출하지시대기
				}else if(currProgCd.equals("N")){
					ydAimRtGp ="K2";			//운송지시대기
				}else if(currProgCd.equals("L")){
					if(szRcvTcCode.equals("DMYDR023")){ //코일제품상차지시
						ydAimRtGp =currProgCd+"5";	//상차대기 
					}else {
						ydAimRtGp =currProgCd+"2";	//운송대기
					}
				}else if(currProgCd.equals("M")){
					ydAimRtGp =currProgCd+"2";	//출하완료				
				}		
			//***************************************************************************************************************************
			}else if(sItemGp.equals("S")){
			//수신한 재료번호로 슬라브공통을 읽기 ***************************************************************************************************
				
				//진도코드
				ydUtils.putLog(szClassName, szMethodName, "진도코드::" +currProgCd, 4);
				
				if(currProgCd.equals("H")){
					ydAimRtGp =currProgCd+"1";	//입고대기
				}else if(currProgCd.equals("J")){
					ydAimRtGp =currProgCd+"1";	//반납대기
				}else if(currProgCd.equals("K")){		
					ydAimRtGp =currProgCd+"1";	//출하지시대기
				}else if(currProgCd.equals("L")){
					if(szRcvTcCode.equals("DMYDR022")){ //외판슬라브운송상차지시 
						ydAimRtGp =currProgCd+"4";	//상차대기 
					}else {
						ydAimRtGp =currProgCd+"1";	//운송대기
					}
				}else if(currProgCd.equals("N")){
					ydAimRtGp =currProgCd+"1";	//운송지시대기
				}else if(currProgCd.equals("M")){
					ydAimRtGp =currProgCd+"1";	//출하완료
				}			
			}
			//***************************************************************************************************************************
		} catch(Exception e) {
			szMsg = "야드목표행선지구분 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		
		szMsg = "야드목표행선지구분: " + ydAimRtGp;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		rVal[0] =ydAimRtGp ;
		return rVal;
	}
	
	/**
	 * 상차완료시 공통업무 처리 - 진행관리 공통테이블 업데이트
	 * @param szYD_CAR_SCH_ID
	 * @return
	 * @throws DAOException
	 */
	public static int procCarLoadCmpl(String szYD_CAR_SCH_ID) throws DAOException {
		//리턴값
		int intRtnVal = 0;
		//메세지
		String szMsg = null;
		String szRtnMsg = null;
		//메소드명
		String szMethodName    = "procCarLoadCmpl";
		String szOperationName = "상차완료공통처리";
		//레코드셋
		JDTORecordSet rsResult = null;
		JDTORecordSet rsResultTemp = null;
		JDTORecordSet getRecSet    = null;
		
		//레코드
		JDTORecord recInTemp = null;
		JDTORecord recTemp = null;
		//JDTORecord recStock = null;
		JDTORecord recOutTemp = null;
		//JDTORecord outRec    = null;
		//저장품 DAO
		//YdStockDao		ydStockDao		= new YdStockDao();
		//차량스케줄DAO
		YdCarSchDao		ydCarSchDao		= new YdCarSchDao();
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		//진행관리 - 주편공통DAO
		PtMSlabCommDao ptMSlabCommDao = new PtMSlabCommDao();
		//진행관리 - 슬라브공통DAO
		PtSlabCommDao ptSlabCommDao = new PtSlabCommDao();
		//진행관리 - 이송지시
		PtStlFrtoMoveDao ptStlFrtoMoveDao = new PtStlFrtoMoveDao();
		
		//재료번호
		String szSTL_NO = null;
		//재료품목
		String szYD_MTL_ITEM =  null;
		String szARR_WLOC_CD		= null;
		
		
		String szYD_AIM_YD_GP				= null;
		//String szCurrProgCd			= null;
		
		//이전저장위치
		String szYD_STR_LOC_HIS1 = null;
		//주편공통테이블과 슬라브공통테이블중 어느 테이블을 조회했는 지를 결정하는 변수 정의
		String szPT_TB_COMM = null;
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------
			// 1. 차량스케줄 조회
			//----------------------------------------------------------------------------------------------------------
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			
			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 상차완료 : 차량스케줄["+szYD_CAR_SCH_ID+"]이 존재하지 않거나 오류가 발생했습니다 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	착지개소코드를 목표야드로 변환
			//----------------------------------------------------------------------------------------------------------
			
			szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recTemp, "ARR_WLOC_CD");
			szYD_AIM_YD_GP = getYdFromWlocCd(szARR_WLOC_CD);
			
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 착지개소코드["+szARR_WLOC_CD+"] => 목표야드["+szYD_AIM_YD_GP+"]로 변환";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	차량이송재료 조회
			//----------------------------------------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 상차완료 : 차량스케줄에 이송재료 가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal;
			}
			//recInTemp = JDTORecordFactory.getInstance().create();
			//recStock = JDTORecordFactory.getInstance().create();
			szMsg="["+szOperationName+"] 상차완료 : 차량스케줄에 이송재료가 존재합니다 - 대상재건수["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	주편/슬라브재료공통테이블, 이송지시테이블 업데이트
			//----------------------------------------------------------------------------------------------------------
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				//----------------------------------------------------------------------------------------------------------
				// 2. 구내운송인 경우 주편/슬라브재료공통테이블에 상차완료시점을 업데이트처리
				//----------------------------------------------------------------------------------------------------------
				rsResult.absolute(Loop_i);
				JDTORecord recOutTemp1 = rsResult.getRecord();
				szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recOutTemp1, "YD_MTL_ITEM"); 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp1, "STL_NO");
				
				//주편,슬라브공통테이블 조회
				getRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				szRtnMsg = getPtCommStock(szSTL_NO, getRecSet);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 공통테이블에 재료번호[" + szSTL_NO + "] 조회 실패 ";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}
				
				getRecSet.first();
				recTemp = getRecSet.getRecord();
				//이전저장위치
				szYD_STR_LOC_HIS1 = ydDaoUtils.paraRecChkNull(recTemp, "YD_STR_LOC_HIS1");
				//주편공통테이블과 슬라브공통테이블중 어느 테이블을 조회했는 지를 결정
				szPT_TB_COMM = ydDaoUtils.paraRecChkNull(recTemp, "PT_TB_COMM");
				
				recInTemp = JDTORecordFactory.getInstance().create();
				
				intRtnVal = 0;
				if( szPT_TB_COMM.equals("B") )	{				//주편공통테이블 업데이트 - 소재이송일시
					recInTemp.setField("MSLAB_NO", szSTL_NO);
					intRtnVal = ptMSlabCommDao.updPtMSlabComm(recInTemp, 0);
				}else if( szPT_TB_COMM.equals("S") )	{		//슬라브공통테이블업데이트 - 소재이송일시
					recInTemp.setField("SLAB_NO", szSTL_NO);
					intRtnVal = ptSlabCommDao.updPtSlabComm(recInTemp, 0);
				}
				
				if(intRtnVal <= 0) {
					szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 " +(szPT_TB_COMM.equals("B") ? "주편" : (szPT_TB_COMM.equals("S") ? "슬라브" : "") ) 
					+ "공통테이블에 재료번호[" + szSTL_NO + "(" + szYD_MTL_ITEM + ")] 상차완료시점[소재이송일시] 업데이트 실패 ";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal;
					//continue;
				}
				//----------------------------------------------------------------------------------------------------------
				
				
				//----------------------------------------------------------------------------------------------------------
				// 3. PT_소재이송지시에 이송상차일자, 야드재료예정저장From위치코드를 업데이트 처리
				//----------------------------------------------------------------------------------------------------------
				recInTemp = JDTORecordFactory.getInstance().create();
				recInTemp.setField("STL_NO", szSTL_NO);
				
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(recInTemp, rsResultTemp, 0);
				if( intRtnVal <= 0 ) {
					szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + szSTL_NO + "]가 존재하지 않습니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal;
				}
				rsResultTemp.first();
				recOutTemp = rsResultTemp.getRecord();
				//야드재료예정저장From위치코드 20090616.김진욱
				recOutTemp.setField("YD_MTL_PLN_STR_FR_LOC_CD", szYD_STR_LOC_HIS1);
				//이송상차일자 업데이트
				recOutTemp.setField("FRTOMOVE_CARLOAD_DATE", YdUtils.getCurDate("yyyyMMdd"));
				
				intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 0);
				if( intRtnVal <= 0 ) {
					szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 이송상차일자 업데이트 실패";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					return intRtnVal;
				}
				szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 재료번호["
					+ ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO") + "], 이송지시차수[" 
					+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에 이송상차일자 업데이트 성공";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//----------------------------------------------------------------------------------------------------------
				
				
				
				//----------------------------------------------------------------------------------------------------------
				/*
				 * 이송상차완료 시 각 재료의 목표야드, 목표동, 목표행선을 설정한다
				 * 수정자 : 임춘수
				 * 수정일 : 2009.10.27
				 */
				recTemp.setField("STL_NO", szSTL_NO);
				recTemp.setField("YD_GP", szYD_AIM_YD_GP);
				String szRetunMsg = uptStockCodeMapping(recTemp);
				
				szMsg="["+szOperationName+"] 재료["+szSTL_NO+"]의 속성을 수정 완료 - 메세지 : " + szRetunMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				//----------------------------------------------------------------------------------------------------------
			}
			
			szMsg="["+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(JDTOException e) {
			szMsg = "["+szOperationName+"] 상차완료시 공통업무 처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 * 후판창고 BED 타입변경 처리 : 후판제품용
	 * @param sYdLocation
	 * @return
	 * @throws DAOException
	 */
	public static String procChangeBedTypeForPlateGds(String sYdLocation, 
													  String szMethodName) throws DAOException {
		int intRtnVal 			= 0;
		int intMvCnt 			= 0;
		//메세지
		String szMsg 			= "";
		String szSessionName	= "BED TYPE 변경";
		
		JDTORecord recInTemp    = null;
		JDTORecord recOutTemp   = null;
		JDTORecord recSndTemp   = null;
		
		JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
	    
	    YdStkBedDao    ydStkBedDao    = new YdStkBedDao();
    	YdStkLyrDao    ydStkLyrDao    = new YdStkLyrDao();
    	YdEqpDao   	   ydEqpDao   	  = new YdEqpDao();
    	
		try{	
			szMsg = "[" + szSessionName + "] 저장위치 : " + sYdLocation;
            ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
            
			if(sYdLocation.length() != 8){
				return YdConstant.RETN_CD_FAILURE;
			}
			String szYD_STK_COL_GP = sYdLocation.substring(0, 6);
			String szYD_STK_BED_NO = sYdLocation.substring(6, 8);
			
    		recInTemp = JDTORecordFactory.getInstance().create();
        	
			recInTemp.setField("YD_STK_COL_GP",        szYD_STK_COL_GP);
			recInTemp.setField("YD_STK_BED_NO",     	szYD_STK_BED_NO);
			
			getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
			intRtnVal = ydStkBedDao.getYdStkbed(recInTemp, getRecSet, 0);
			
			if (intRtnVal <= 0) {
				szMsg = "[" + szSessionName + "] BED정보 조회중 Error!! Code : " + intRtnVal;
                ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
	        	throw new JDTOException("<procY4CrnUdWr> getYdStkbed :" + szMsg);
			}
			
			getRecSet.first();
			recOutTemp = JDTORecordFactory.getInstance().create();
			recOutTemp.setRecord(getRecSet.getRecord());
			
			String szYD_STK_BED_WHIO_STAT = ydDaoUtils.paraRecChkNull(recOutTemp, "YD_STK_BED_WHIO_STAT");
			
			if(szYD_STK_BED_WHIO_STAT.equals(YdConstant.YD_STK_BED_WHIO_FULL)) {
				
				getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    			intRtnVal = ydStkLyrDao.getYdStklyr(recInTemp, getRecSet, 101);
    			
    			if (intRtnVal < 0) {
    				szMsg = "[" + szSessionName + "] 적치BED 운송지시대기,운송대기 재료수 조회중 Error!! Code : " + intRtnVal;
                    ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
    	        	throw new JDTOException("[" + szSessionName + "] getYdStklyr :" + szMsg);
    			}
    			
    			getRecSet.first();
				recOutTemp = JDTORecordFactory.getInstance().create();
				recOutTemp.setRecord(getRecSet.getRecord());
				
				intMvCnt = ydDaoUtils.paraRecChkNullInt(recOutTemp, "MV_CNT");
				
				if(intMvCnt == 0) {
					
					/*
					 * 출하가적베드인지 아닌지를 체크한다.
					 * 2012.04.27 윤재광
					 */
					boolean 		isGajuk			= false;
					JDTORecord		recSubPara		= null;
					JDTORecord 		inRecord 		= JDTORecordFactory.getInstance().create();
					JDTORecordSet 	outResultSet 	= JDTORecordFactory.getInstance().createRecordSet("");
					
					/*com.inisteel.cim.yd.dao.ydeqpdao.YdEqpDao.getYdEqpTcBreYDB601*/
					intRtnVal = ydEqpDao.getYdEqp(inRecord, outResultSet, 601);
					if(intRtnVal > 0) {
						for(int i = 1; i <= outResultSet.size(); i++ ) {
							recSubPara = JDTORecordFactory.getInstance().create();
					
							outResultSet.absolute(i);
							recSubPara = outResultSet.getRecord();
							
							if(szYD_STK_COL_GP.equals(recSubPara.getFieldString("YD_STK_COL_GP"))){
								
								isGajuk = true;
								break;
							}
						}
					}	
							
					/*
					 *  /yd/plateGdsYd/plateYdSelList.jsp에도 아래로직 존재
					 *  변경시에 JSP도 같이 변경요.
					 */
					if(isGajuk){
						recInTemp.setField("YD_STK_BED_WHIO_STAT", "G");
					} else {
						recInTemp.setField("YD_STK_BED_WHIO_STAT", YdConstant.YD_STK_BED_WHIO_ENABLE);
					}
					
					intRtnVal = ydStkBedDao.updYdStkbed(recInTemp, 0);
					
					if (intRtnVal < 0) {
						szMsg = "[Jsp Session : "+szSessionName+"] 적치열구분["+szYD_STK_COL_GP+"] 적치BED번호[" + szYD_STK_BED_NO + "]를 입출고가능[E]로 수정 시 오류발생 : 루프 계속처리 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						throw new JDTOException("<procY4CrnUdWr> updYdStkbed :" + szMsg);
        			}
					
					szMsg = "[Jsp Session : "+szSessionName+"] 적치열구분["+szYD_STK_COL_GP+"] 적치BED번호[" + szYD_STK_BED_NO + "]를 입출고가능[E]로 수정 완료 ";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//L2 로 적치열 수정된 정보를 내려보내준다.
					recSndTemp =  JDTORecordFactory.getInstance().create();
					//--2013.03.07 수정 (3기)
					if(szYD_STK_COL_GP.startsWith(YdConstant.YD_GP_PLATE2_GDS_YARD)) { //2후판
						recSndTemp.setField("MSG_ID", 			"YDY8L001");
						if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szYD_STK_COL_GP)){
							recSndTemp.setField("MSG_ID", 			"YDY9L001");
						}
						
						recSndTemp.setField("YD_GP", 			YdConstant.YD_GP_PLATE2_GDS_YARD);
					} else { //1후판
						recSndTemp.setField("MSG_ID", 			"YDY4L001");
						recSndTemp.setField("YD_GP", 			YdConstant.YD_GP_PLATE_GDS_YARD);
					}
					recSndTemp.setField("YD_INFO_SYNC_CD",  "4");
					recSndTemp.setField("YD_STK_COL_GP", 	szYD_STK_COL_GP);
					recSndTemp.setField("YD_STK_BED_NO", 	szYD_STK_BED_NO);
			
					szMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 시작" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					ydDelegate.sendMsg(recSndTemp);
					
					szMsg = "[Jsp-Session "+szSessionName+" ] L2 로 적치열 수정된 정보 송신 완료" ;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
		}catch(Exception e) {
			szMsg = "["+szMethodName+"] 후판창고 BED 타입변경  처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	}
	/**
	 * 상차완료시 공통업무 처리 - 진행관리 공통테이블 업데이트 : 후판제품용
	 * @param szYD_CAR_SCH_ID
	 * @return
	 * @throws DAOException
	 */
	public static String procCarLoadCmplForPlateGds(String szYD_CAR_SCH_ID, String szYD_STR_LOC_TYPE, String szFromMethod) throws DAOException {
		//리턴값
		int intRtnVal = 0;
		//메세지
		String szMsg = null;
		String szRtnMsg = null;
		//메소드명
		String szMethodName    = "procCarLoadCmplForPlateGds";
		String szOperationName = "상차완료공통처리(후판제품)";
		//레코드셋
		JDTORecordSet rsResult = null;
		//JDTORecordSet rsResultTemp = null;
		JDTORecordSet getRecSet    = null;
		
		//레코드
		JDTORecord recInTemp = null;
		JDTORecord recTemp = null;
		//저장품 DAO
		YdStockDao		ydStockDao		= new YdStockDao();
		//차량스케줄DAO
		YdCarSchDao		ydCarSchDao		= new YdCarSchDao();
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		
		//재료번호
		String szSTL_NO = null;
		String szARR_WLOC_CD		= null;
		
		String szYD_STR_LOC_FIELD			= "";
		String szYD_AIM_YD_GP				= null;
		
		//이전저장위치
		String szYD_STR_LOC_HIS1 = null;
		//주편공통테이블과 슬라브공통테이블중 어느 테이블을 조회했는 지를 결정하는 변수 정의
		//String szPT_TB_COMM = null;
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"], 공통테이블 조회 타입["+szYD_STR_LOC_TYPE+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szYD_STR_LOC_TYPE.equals(YdConstant.YD_STR_LOC_CURR) ) {
				szYD_STR_LOC_FIELD = "YD_STR_LOC"; 
			}else if( szYD_STR_LOC_TYPE.equals(YdConstant.YD_STR_LOC_HIS1) ) {
				szYD_STR_LOC_FIELD = "YD_STR_LOC_HIS1"; 
			}else{
				szYD_STR_LOC_FIELD = "YD_STR_LOC_HIS2";
			}
			
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"], 공통테이블 조회필드 이름["+szYD_STR_LOC_FIELD+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------
			// 1. 차량스케줄 조회
			//----------------------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			
			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 상차완료 : 차량스케줄["+szYD_CAR_SCH_ID+"]이 존재하지 않거나 오류가 발생했습니다 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	착지개소코드를 목표야드로 변환
			//----------------------------------------------------------------------------------------------------------
			szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recTemp, "ARR_WLOC_CD");
			szYD_AIM_YD_GP = getYdFromWlocCd(szARR_WLOC_CD);
			
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 착지개소코드["+szARR_WLOC_CD+"] => 목표야드["+szYD_AIM_YD_GP+"]로 변환";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	차량이송재료 조회
			//----------------------------------------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 상차완료 : 차량스케줄에 이송재료 가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			szMsg="["+szOperationName+"] 상차완료 : 차량스케줄에 이송재료가 존재합니다 - 대상재건수["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	후판제품공통테이블, 이송지시테이블 업데이트
			//----------------------------------------------------------------------------------------------------------
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				//----------------------------------------------------------------------------------------------------------
				// 2. 구내운송인 경우 후판제품공통테이블에 상차완료시점을 업데이트처리
				//----------------------------------------------------------------------------------------------------------
				rsResult.absolute(Loop_i);
				JDTORecord recOutTemp1 = rsResult.getRecord();
				szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp1, "STL_NO");
				//----------------------------------------------------------------------------------------------------------
				//후판제품공통테이블 조회
				//----------------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 후판제품공통테이블에 ["+Loop_i+"] 재료["+ szSTL_NO +"] 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("PLATE_NO", szSTL_NO);
				intRtnVal = ydStockDao.getYdStock(recTemp, getRecSet, 4);
				
				if( intRtnVal <= 0 ) {
					szMsg="["+szOperationName+"] 후판제품공통테이블에 ["+Loop_i+"] 재료["+ szSTL_NO +"] 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}
				
				szMsg="["+szOperationName+"] 후판제품공통테이블에 ["+Loop_i+"] 재료["+ szSTL_NO +"] 조회 시 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet.first();
				recTemp = getRecSet.getRecord();
				//이전저장위치
				szYD_STR_LOC_HIS1 = ydDaoUtils.paraRecChkNull(recTemp, szYD_STR_LOC_FIELD);
				//----------------------------------------------------------------------------------------------------------
				
				
				//----------------------------------------------------------------------------------------------------------
				//후판sizing개소코드인 경우에만 후판공통테이블의 진도코드 변경, 저장품 업데이트 처리
				//----------------------------------------------------------------------------------------------------------
				if( szARR_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
					szARR_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)	) {
					//----------------------------------------------------------------------------------------------------------
					//후판공통테이블의 진도코드 변경
					//----------------------------------------------------------------------------------------------------------
					
					szRtnMsg = setProgCodeForPlateOrCoil(szSTL_NO, "P");
					
					//----------------------------------------------------------------------------------------------------------
					

					//----------------------------------------------------------------------------------------------------------
					// 저장품 업데이트 처리 - 저장품 재료진도코드와 목표행선 변경
					//----------------------------------------------------------------------------------------------------------
					recInTemp = JDTORecordFactory.getInstance().create();
					recInTemp.setField("STL_NO", szSTL_NO);
					recInTemp.setField("STL_PROG_CD", YdConstant.PROG_CD_WRK_WAIT);
					recInTemp.setField("YD_AIM_RT_GP", YdConstant.AR_WRK_WAIT_A_MILL);
					recInTemp.setField("MODIFIER", szFromMethod.length() > 10 ? szFromMethod.substring(0, 10) : szFromMethod);
					intRtnVal = ydStockDao.updYdStock(recInTemp, 0);
					if( intRtnVal <= 0 ) {
						szMsg="["+szOperationName+"] ["+Loop_i+"] 저장품 재료[" + szSTL_NO + "] 재료진도코드, 목표행선 수정 시 존재하지 않습니다.";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
						continue;
					}
					szMsg="["+szOperationName+"] ["+Loop_i+"] 저장품 재료[" + szSTL_NO + "] 재료진도코드["+YdConstant.PROG_CD_WRK_WAIT+"], 목표행선["+YdConstant.AR_WRK_WAIT_A_MILL+"] 수정 성공";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//----------------------------------------------------------------------------------------------------------
				}
				
				szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 ["+Loop_i+"] 재료[" + szSTL_NO + "], FROM위치["+szYD_STR_LOC_HIS1+"] 업데이트 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szRtnMsg = uptPtStlFrtoMoveWhenCarLoadCmpl(szSTL_NO, szYD_STR_LOC_HIS1);
				
				szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 ["+Loop_i+"] 재료[" + szSTL_NO + "], FROM위치["+szYD_STR_LOC_HIS1+"] 업데이트 완료";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//----------------------------------------------------------------------------------------------------------
			}
			
			//----------------------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(JDTOException e) {
			szMsg = "["+szOperationName+"] 상차완료시 공통업무 처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			//throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	/**
	 * 상차완료시 공통업무 처리 - 진행관리 공통테이블 업데이트 : 코일야드용
	 * @param szYD_CAR_SCH_ID
	 * @return
	 * @throws DAOException
	 */
	public static String procCarLoadCmplForCoil(String szYD_CAR_SCH_ID, String szYD_STR_LOC_TYPE, String szFromMethod) throws DAOException {
		//리턴값
		int intRtnVal = 0;
		//메세지
		String szMsg = null;
		String szRtnMsg = null;
		//메소드명
		String szMethodName    = "procCarLoadCmplForCoil";
		String szOperationName = "상차완료공통처리(코일야드)";
		//레코드셋
		JDTORecordSet rsResult = null;
		//JDTORecordSet rsResultTemp = null;
		JDTORecordSet getRecSet    = null;
		
		//레코드
		JDTORecord recInTemp = null;
		JDTORecord recTemp = null;
		//JDTORecord recStock = null;
		//JDTORecord recOutTemp = null;
		//JDTORecord outRec    = null;
		//저장품 DAO
		YdStockDao		ydStockDao		= new YdStockDao();
		//차량스케줄DAO
		YdCarSchDao		ydCarSchDao		= new YdCarSchDao();
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		//진행관리 - 이송지시
		//PtStlFrtoMoveDao ptStlFrtoMoveDao = new PtStlFrtoMoveDao();
		
		//재료번호
		String szSTL_NO = null;
		String szARR_WLOC_CD		= null;
		
		String szYD_STR_LOC_FIELD			= "";
		String szYD_AIM_YD_GP				= null;
		
		//이전저장위치
		String szYD_STR_LOC_HIS1 = null;
		//주편공통테이블과 슬라브공통테이블중 어느 테이블을 조회했는 지를 결정하는 변수 정의
		//String szPT_TB_COMM = null;
		
		try {
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"], 공통테이블 조회 타입["+szYD_STR_LOC_TYPE+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szYD_STR_LOC_TYPE.equals(YdConstant.YD_STR_LOC_CURR) ) {
				szYD_STR_LOC_FIELD = "YD_STR_LOC"; 
			}else if( szYD_STR_LOC_TYPE.equals(YdConstant.YD_STR_LOC_HIS1) ) {
				szYD_STR_LOC_FIELD = "YD_STR_LOC_HIS1"; 
			}else{
				szYD_STR_LOC_FIELD = "YD_STR_LOC_HIS2";
			}
			
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"], 공통테이블 조회필드 이름["+szYD_STR_LOC_FIELD+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//----------------------------------------------------------------------------------------------------------
			// 1. 차량스케줄 조회
			//----------------------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			
			intRtnVal = ydCarSchDao.getYdCarsch(recInTemp, rsResult, 0);
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 상차완료 : 차량스케줄["+szYD_CAR_SCH_ID+"]이 존재하지 않거나 오류가 발생했습니다 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			
			rsResult.first();
			recTemp = rsResult.getRecord();
			
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	착지개소코드를 목표야드로 변환
			//----------------------------------------------------------------------------------------------------------
			szARR_WLOC_CD = ydDaoUtils.paraRecChkNull(recTemp, "ARR_WLOC_CD");
			szYD_AIM_YD_GP = getYdFromWlocCd(szARR_WLOC_CD);
			
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 착지개소코드["+szARR_WLOC_CD+"] => 목표야드["+szYD_AIM_YD_GP+"]로 변환";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	차량이송재료 조회
			//----------------------------------------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
				szMsg="["+szOperationName+"] 상차완료 : 차량스케줄에 이송재료 가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			szMsg="["+szOperationName+"] 상차완료 : 차량스케줄에 이송재료가 존재합니다 - 대상재건수["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	코일공통테이블, 이송지시테이블 업데이트
			//----------------------------------------------------------------------------------------------------------
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				//----------------------------------------------------------------------------------------------------------
				// 2. 구내운송인 경우 코일제품공통테이블에 상차완료시점을 업데이트처리
				//----------------------------------------------------------------------------------------------------------
				rsResult.absolute(Loop_i);
				JDTORecord recOutTemp1 = rsResult.getRecord();
				szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp1, "STL_NO");
				//----------------------------------------------------------------------------------------------------------
				//코일제품공통테이블 조회
				//----------------------------------------------------------------------------------------------------------
				szMsg="["+szOperationName+"] 코일제품공통테이블에 ["+Loop_i+"] 재료["+ szSTL_NO +"] 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("COIL_NO", szSTL_NO);
				intRtnVal = ydStockDao.getYdStock(recTemp, getRecSet, 10);
				
				if( intRtnVal <= 0 ) {
					szMsg="["+szOperationName+"] 후판제품공통테이블에 ["+Loop_i+"] 재료["+ szSTL_NO +"] 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}
				
				szMsg="["+szOperationName+"] 후판제품공통테이블에 ["+Loop_i+"] 재료["+ szSTL_NO +"] 조회 시 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet.first();
				recTemp = getRecSet.getRecord();
				//이전저장위치
				szYD_STR_LOC_HIS1 = ydDaoUtils.paraRecChkNull(recTemp, szYD_STR_LOC_FIELD);
				//----------------------------------------------------------------------------------------------------------
				//후판sizing개소코드인 경우에만 후판공통테이블의 진도코드 변경, 저장품 업데이트 처리
				//----------------------------------------------------------------------------------------------------------
				// 3. PT_소재이송지시에 이송상차일자, 야드재료예정저장From위치코드를 업데이트 처리
				//----------------------------------------------------------------------------------------------------------
				
				szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 ["+Loop_i+"] 재료[" + szSTL_NO + "], FROM위치["+szYD_STR_LOC_HIS1+"] 업데이트 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				szRtnMsg = uptPtStlFrtoMoveWhenCarLoadCmpl(szSTL_NO, szYD_STR_LOC_HIS1);
				
				szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 ["+Loop_i+"] 재료[" + szSTL_NO + "], FROM위치["+szYD_STR_LOC_HIS1+"] 업데이트 완료";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//----------------------------------------------------------------------------------------------------------
			}
			
			//----------------------------------------------------------------------------------------------------------
			
			szMsg="["+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(JDTOException e) {
			szMsg = "["+szOperationName+"] 상차완료시 공통업무 처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			//throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
			return YdConstant.RETN_CD_FAILURE;
		}
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 상차완료이송지시수정
	 * @param szSTL_NO
	 * @param YD_MTL_PLN_STR_FR_LOC_CD
	 * @return
	 * @throws JDTOException
	 */
	public static String uptPtStlFrtoMoveWhenCarLoadCmpl(String szSTL_NO, String YD_MTL_PLN_STR_FR_LOC_CD) throws JDTOException {
		String szMsg				= null;
		String szOperationName		= "상차완료이송지시수정";
		String szMethodName			= "uptPtStlFrtoMoveWhenCarLoadCmpl";
		int		intRtnVal			= -100;
		
		PtStlFrtoMoveDao	ptStlFrtoMoveDao	= new PtStlFrtoMoveDao();
		
		JDTORecord		recInTemp	= null;
		JDTORecord		recOutTemp	= null;
		JDTORecordSet	rsResultTemp	= null;
		
		
		//----------------------------------------------------------------------------------------------------------
		//	이송지시테이블 조회
		//----------------------------------------------------------------------------------------------------------
		szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + szSTL_NO + "] 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		recInTemp = JDTORecordFactory.getInstance().create();
		recInTemp.setField("STL_NO", szSTL_NO);
		rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
		intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(recInTemp, rsResultTemp, 0);
		if( intRtnVal <= 0 ) {
			szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + szSTL_NO + "]가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		
		
		rsResultTemp.first();
		recOutTemp = rsResultTemp.getRecord();
		
		szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 재료번호["
		+ szSTL_NO + "], 이송지시차수[" 
		+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에 이송상차일자, 야드재료예정저장From위치코드["+YD_MTL_PLN_STR_FR_LOC_CD+"] 업데이트 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------
		// 이송지시테이블 업데이트 - 이송상차일자, 야드재료예정저장From위치코드
		//----------------------------------------------------------------------------------------------------------
		//야드재료예정저장From위치코드 20090616.김진욱
		recOutTemp.setField("YD_MTL_PLN_STR_FR_LOC_CD", YD_MTL_PLN_STR_FR_LOC_CD);
		//이송상차일자 업데이트
		recOutTemp.setField("FRTOMOVE_CARLOAD_DATE", YdUtils.getCurDate("yyyyMMdd"));
		
		intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 0);
		if( intRtnVal <= 0 ) {
			szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 이송상차일자 업데이트 실패";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		szMsg="["+szOperationName+"] 상차완료처리 시 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 재료번호["
			+ szSTL_NO + "], 이송지시차수[" 
			+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에 이송상차일자, 야드재료예정저장From위치코드["+YD_MTL_PLN_STR_FR_LOC_CD+"] 업데이트 성공";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 코드맵핑재료수정
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static String uptStockCodeMapping(JDTORecord recPara) throws JDTOException {
		String szRtnMsg		= YdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "uptStockCodeMapping";
		String szOperationName			= "코드맵핑재료수정";
		int intRtnVal		= -100;
		YdStockDao ydStockDao = new YdStockDao();
		YdCodeMapping ydCodeMapping = new YdCodeMapping();
		JDTORecord recStock	= null;	
		JDTORecord outRec = null;
		JDTORecord recTemp = null;
		String szMsg		= null;
		// ++++++++++++++++++++++++++++ 전달되는 파라미터 추출 시작
		String szPT_TB_COMM		= ydDaoUtils.paraRecChkNull(recPara, "PT_TB_COMM");					//주편/슬라브구분
		String szSTL_NO			= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");						//재료번호
		String szSLAB_WO_RT_CD 	= ydDaoUtils.paraRecChkNull(recPara, "SLAB_WO_RT_CD");				//슬라브지시행선코드
		String szORD_YEOJAE_GP 	= ydDaoUtils.paraRecChkNull(recPara, "ORD_YEOJAE_GP");				//주여구분
		String szSCARFING_YN 	= ydDaoUtils.paraRecChkNull(recPara, "SCARFING_YN");				//스카핑여부
		String szSCARFING_DONE_YN = ydDaoUtils.paraRecChkNull(recPara, "SCARFING_DONE_YN");			//스카핑완료여부
		String szMILL_WO_EXN 	= ydDaoUtils.paraRecChkNull(recPara, "MILL_WO_EXN");				//압연지시
		String szYD_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_GP");						//야드구분
		String szSTL_APPEAR_GP	= ydDaoUtils.paraRecChkNull(recPara, "STL_APPEAR_GP");				//재료외형구분
		String szHCR_GP			= ydDaoUtils.paraRecChkNull(recPara, "HCR_GP");						//HCR구분
		// ++++++++++++++++++++++++++++ 전달되는 파라미터 추출 끝
		String szCurrProgCd	= null;																	//재료진도코드
		
		szMsg="["+szOperationName+"] 메소드 시작 - 파라미터 확인";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		if( szSCARFING_DONE_YN.equals("") ) {
			szMsg="["+szOperationName+"] 재료[" + szSTL_NO + "]의 szSCARFING_DONE_YN값이 없으므로 N으로 설정";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			szSCARFING_DONE_YN = "N";
		}
		if( szMILL_WO_EXN.equals("") ) {
			szMsg="["+szOperationName+"] 재료[" + szSTL_NO + "]의 szMILL_WO_EXN값이 없으므로 N으로 설정";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			szMILL_WO_EXN = "N";
		}
		szMsg="["+szOperationName+"] 재료[" + szSTL_NO + "]에 대한 재료진도판단 시작 - 주편/슬라브구분["+szPT_TB_COMM+"], 슬라브지시행선코드["+szSLAB_WO_RT_CD+"], 주여구분["+szORD_YEOJAE_GP+"], 스카핑여부["+szSCARFING_YN+"], 스카핑완료여부["+szSCARFING_DONE_YN+"], 압연지시구분["+szMILL_WO_EXN+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//szCurrProgCd = YdCommonUtils.getCurrProgCd(szPT_TB_COMM, szSLAB_WO_RT_CD, szORD_YEOJAE_GP, szSCARFING_YN, szSCARFING_DONE_YN, szMILL_WO_EXN);
		//---------------------------------------------------------------------------------------------------------
		//---------------------------------------------------------------------------------------------------------
		ymCommonDAO dao = ymCommonDAO.getInstance();
	    List FrtoProductList = null;
    	//공정 함수를 이용한 진도코드 가져오기
    	if(szPT_TB_COMM.equals("B")){
    		//주편 공통
		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcd";
		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szSTL_NO});
    	}else if (szPT_TB_COMM.equals("S")) {
    		//슬라브 공통
		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcdSlab";
		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szSTL_NO});
    	}       	

    	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);

    	szCurrProgCd =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
		ydUtils.putLog(szClassName, szMethodName, "IHSF_PM_주편SLAB진도찾기==>>:"+szCurrProgCd, YdConstant.DEBUG);
		//---------------------------------------------------------------------------------------------------------
		//---------------------------------------------------------------------------------------------------------
		szMsg="["+szOperationName+"] 재료[" + szSTL_NO + "]에 대한 코드맵핑 시작 - 재료진도코드["+szCurrProgCd+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg="["+szOperationName+"] 목표야드["+szYD_GP+"]를 코드맵핑시 야드구분으로 사용";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		recStock = JDTORecordFactory.getInstance().create();
		recStock.setField("YD_GP", 				szYD_GP);
		recStock.setField("SLAB_WO_RT_CD", 		szSLAB_WO_RT_CD);
		recStock.setField("STL_APPEAR_GP", 		szSTL_APPEAR_GP);
		recStock.setField("HCR_GP", 			szHCR_GP);
		recStock.setField("ORD_YEOJAE_GP", 		szORD_YEOJAE_GP);
		recStock.setField("SCARFING_YN", 		szSCARFING_YN);
		recStock.setField("SCARFING_DONE_YN", 	szSCARFING_DONE_YN);
		recStock.setField("CURR_PROG_CD", 		szCurrProgCd);
		recStock.setField("ARR_WLOC_CD", 		"");
		recStock.setField("MILL_WO_EXN", 		szMILL_WO_EXN);
		
		outRec = JDTORecordFactory.getInstance().create();
		
		intRtnVal = ydCodeMapping.CallMapping(recStock, outRec, szPT_TB_COMM);
		
		recTemp = JDTORecordFactory.getInstance().create();
		recTemp.addRecord(outRec);
		
		recTemp.setField("STL_NO", 		szSTL_NO);
		recTemp.setField("MODIFIER", 	szMethodName.length() > 10 ? szMethodName.substring(0, 10) : szMethodName);
		
		ydUtils.displayRecord(szOperationName, recTemp);
		
		intRtnVal = ydStockDao.updYdStock(recTemp, 0); 
		if( intRtnVal <= 0 ) {
			szMsg="["+szOperationName+"] 재료["+szSTL_NO+"]의 속성을 수정 시 오류발생 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}else{
			szMsg="["+szOperationName+"] 재료["+szSTL_NO+"]의 속성을 수정 성공";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return szRtnMsg;
	}
	
	/**
	 * 하차완료이송지시수정
	 * @param szSTL_NO
	 * @param szYD_MTL_PLN_STR_TO_LOC_CD
	 * @return
	 * @throws JDTOException
	 */
	public static String uptPtStlFrtoMoveWhenCarUnLoadCmpl(String szSTL_NO, String szYD_MTL_PLN_STR_TO_LOC_CD) throws JDTOException {
		String szMsg				= null;
		String szOperationName		= "하차완료이송지시수정";
		String szMethodName			= "uptPtStlFrtoMoveWhenCarUnLoadCmpl";
		int intRtnVal				= -100;
		
		PtStlFrtoMoveDao	ptStlFrtoMoveDao	= new PtStlFrtoMoveDao();
		
		JDTORecord recInTemp		= null;
		JDTORecord recOutTemp		= null;
		JDTORecordSet	rsResultTemp	= null;
		
		recInTemp = JDTORecordFactory.getInstance().create();
		recInTemp.setField("STL_NO", szSTL_NO);

		//----------------------------------------------------------------------------------------------------------
		//	이송지시테이블 조회
		//----------------------------------------------------------------------------------------------------------
		szMsg="["+szOperationName+"] 후판제품재료["+ szSTL_NO +"]로 이송지시테이블 조회 시작.";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
		intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(recInTemp, rsResultTemp, 0);
		if( intRtnVal <= 0 ) {
			szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 후판제품재료[" + szSTL_NO + "]가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NOTEXIST;
		}
		
		szMsg="["+szOperationName+"] 후판제품재료["+ szSTL_NO +"]로 이송지시테이블 조회 시 존재합니다.";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		//----------------------------------------------------------------------------------------------------------
		
		
		//----------------------------------------------------------------------------------------------------------
		// 이송지시테이블 업데이트 - 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드
		//----------------------------------------------------------------------------------------------------------
		rsResultTemp.first();
		recOutTemp = rsResultTemp.getRecord();
		//야드재료예정저장To위치코드 20090616.김진욱
		recOutTemp.setField("YD_MTL_PLN_STR_TO_LOC_CD", szYD_MTL_PLN_STR_TO_LOC_CD);
		//20090618.김진욱 이송상태코드(이송완료)
		recOutTemp.setField("FRTOMOVE_STAT_CD", "*");
		
		szMsg="[" + szOperationName + "]  진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 후판제품재료번호["
		+ szSTL_NO + "], 이송지시차수[" 
		+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 1);
		if( intRtnVal <= 0 ) {
			szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 실패";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		szMsg="[" + szOperationName + "]  진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 후판제품재료번호["
			+ szSTL_NO + "], 이송지시차수[" 
			+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 성공";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//----------------------------------------------------------------------------------------------------------
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 하차완료시 공통업무처리 - 진행관리[PT] 공통테이블 업데이트 : 후판제품용
	 * @param szYD_CAR_SCH_ID
	 * @return
	 * @throws DAOException
	 */
	public static String procCarUnLoadCmplForPlateGds(String szYD_CAR_SCH_ID, String szYD_STR_LOC_TYPE) throws DAOException {
		//리턴값
		int intRtnVal = 0;
		//메세지
		String szMsg = null;
		String szRtnMsg = null;
		//메소드명
		String szMethodName    = "procCarUnLoadCmplForPlateGds";	
		String szOperationName = "하차완료시 공통업무처리(후판제품)";
		//레코드셋
		JDTORecordSet rsResult = null;
		//JDTORecordSet rsResultTemp = null;
		JDTORecordSet getRecSet = null;
		//레코드
		JDTORecord recInTemp = null;
		JDTORecord recTemp = null;
		//JDTORecord recOutTemp = null;
		YdStockDao		ydStockDao		= new YdStockDao();
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		//진행관리 - 이송지시
		//PtStlFrtoMoveDao ptStlFrtoMoveDao = new PtStlFrtoMoveDao();
		//재료번호
		String szSTL_NO = null;
		//이전저장위치 20090616.김진욱
		String szYD_STR_LOC = null;
		
		String szYD_STR_LOC_FIELD	= "";
		
		try {
			
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"], 공통테이블 조회 타입["+szYD_STR_LOC_TYPE+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szYD_STR_LOC_TYPE.equals(YdConstant.YD_STR_LOC_CURR) ) {
				szYD_STR_LOC_FIELD = "YD_STR_LOC"; 
			}else if( szYD_STR_LOC_TYPE.equals(YdConstant.YD_STR_LOC_HIS1) ) {
				szYD_STR_LOC_FIELD = "YD_STR_LOC_HIS1"; 
			}else{
				szYD_STR_LOC_FIELD = "YD_STR_LOC_HIS2";
			}
			
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"], 공통테이블 조회필드 이름["+szYD_STR_LOC_FIELD+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			//----------------------------------------------------------------------------------------------------------
			// 1. 차량 이송재료를 조회
			//----------------------------------------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
				szMsg="[" + szOperationName + "] 차량스케줄에 이송재료 가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				//return intRtnVal;
				return YdConstant.RETN_CD_NOTEXIST;
			}
			
			szMsg="[" + szOperationName + "] 차량스케줄[" + szYD_CAR_SCH_ID + "]에 이송재료 가 존재합니다 - 대상재건수["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	후판제품공통테이블과 이송지시테이블을 업데이트 처리
			//----------------------------------------------------------------------------------------------------------
			recInTemp = JDTORecordFactory.getInstance().create();
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				rsResult.absolute(Loop_i);
				JDTORecord recOutTemp1 = rsResult.getRecord();
				szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp1, "STL_NO");
				
				//----------------------------------------------------------------------------------------------------------
				//	후판제품공통테이블 조회
				//----------------------------------------------------------------------------------------------------------
				
				szMsg="["+szOperationName+"] 후판제품공통테이블에 재료["+ szSTL_NO +"] 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("PLATE_NO", szSTL_NO);
				intRtnVal = ydStockDao.getYdStock(recTemp, getRecSet, 4);
				
				if( intRtnVal <= 0 ) {
					szMsg="["+szOperationName+"] 후판제품공통테이블에 재료["+ szSTL_NO +"] 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}
				
				szMsg="["+szOperationName+"] 후판제품공통테이블에 재료["+ szSTL_NO +"] 조회 시 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//----------------------------------------------------------------------------------------------------------
				
				getRecSet.first();
				recTemp = getRecSet.getRecord();
				
				szYD_STR_LOC = ydDaoUtils.paraRecChkNull(recTemp, szYD_STR_LOC_FIELD);
				//주편공통테이블과 슬라브공통테이블중 어느 테이블을 조회했는 지를 결정
				//----------------------------------------------------------------------------------------------------------
				// 2. PT_소재이송지시에 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드를 업데이트 처리
				//----------------------------------------------------------------------------------------------------------
				szRtnMsg = uptPtStlFrtoMoveWhenCarUnLoadCmpl(szSTL_NO, szYD_STR_LOC);
				

			}
			//----------------------------------------------------------------------------------------------------------
			
		}catch(JDTOException e) {
			szMsg = "[" + szOperationName + "] 공통업무 처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 하차완료시 공통업무처리 - 진행관리[PT] 공통테이블 업데이트 : 코일야드용
	 * @param szYD_CAR_SCH_ID
	 * @return
	 * @throws DAOException
	 */
	public static String procCarUnLoadCmplForCoil(String szYD_CAR_SCH_ID, String szYD_STR_LOC_TYPE) throws DAOException {
		//리턴값
		int intRtnVal = 0;
		//메세지
		String szMsg = null;
		String szRtnMsg = null;
		//메소드명
		String szMethodName    = "procCarUnLoadCmplForPlateGds";	
		String szOperationName = "하차완료시 공통업무처리(코일야드)";
		//레코드셋
		JDTORecordSet rsResult = null;
		//JDTORecordSet rsResultTemp = null;
		JDTORecordSet getRecSet = null;
		//레코드
		JDTORecord recInTemp = null;
		JDTORecord recTemp = null;
		//JDTORecord recOutTemp = null;
		YdStockDao		ydStockDao		= new YdStockDao();
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao = new YdCarFtmvMtlDao();
		//진행관리 - 이송지시
		//PtStlFrtoMoveDao ptStlFrtoMoveDao = new PtStlFrtoMoveDao();
		//재료번호
		String szSTL_NO = null;
		//이전저장위치 20090616.김진욱
		String szYD_STR_LOC = null;
		
		String szYD_STR_LOC_FIELD	= "";
		
		try {
			
			szMsg="["+szOperationName+"] 메소드 시작 - 차량스케줄["+szYD_CAR_SCH_ID+"], 공통테이블 조회 타입["+szYD_STR_LOC_TYPE+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szYD_STR_LOC_TYPE.equals(YdConstant.YD_STR_LOC_CURR) ) {
				szYD_STR_LOC_FIELD = "YD_STR_LOC"; 
			}else if( szYD_STR_LOC_TYPE.equals(YdConstant.YD_STR_LOC_HIS1) ) {
				szYD_STR_LOC_FIELD = "YD_STR_LOC_HIS1"; 
			}else{
				szYD_STR_LOC_FIELD = "YD_STR_LOC_HIS2";
			}
			
			szMsg="["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"], 공통테이블 조회필드 이름["+szYD_STR_LOC_FIELD+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			//----------------------------------------------------------------------------------------------------------
			// 1. 차량 이송재료를 조회
			//----------------------------------------------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
				szMsg="[" + szOperationName + "] 차량스케줄에 이송재료 가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				//return intRtnVal;
				return YdConstant.RETN_CD_NOTEXIST;
			}
			
			szMsg="[" + szOperationName + "] 차량스케줄[" + szYD_CAR_SCH_ID + "]에 이송재료 가 존재합니다 - 대상재건수["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//----------------------------------------------------------------------------------------------------------
			
			
			//----------------------------------------------------------------------------------------------------------
			//	코일공통테이블과 이송지시테이블을 업데이트 처리
			//----------------------------------------------------------------------------------------------------------
			recInTemp = JDTORecordFactory.getInstance().create();
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				rsResult.absolute(Loop_i);
				JDTORecord recOutTemp1 = rsResult.getRecord();
				szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp1, "STL_NO");
				
				//----------------------------------------------------------------------------------------------------------
				//	코일공통테이블 조회
				//----------------------------------------------------------------------------------------------------------
				
				szMsg="["+szOperationName+"] 코일공통테이블에 재료["+ szSTL_NO +"] 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				recTemp = JDTORecordFactory.getInstance().create();
				recTemp.setField("COIL_NO", szSTL_NO);
				intRtnVal = ydStockDao.getYdStock(recTemp, getRecSet, 10);
				
				if( intRtnVal <= 0 ) {
					szMsg="["+szOperationName+"] 코일공통테이블에 재료["+ szSTL_NO +"] 조회 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}
				
				szMsg="["+szOperationName+"] 코일공통테이블에 재료["+ szSTL_NO +"] 조회 시 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//----------------------------------------------------------------------------------------------------------
				
				getRecSet.first();
				recTemp = getRecSet.getRecord();
				
				szYD_STR_LOC = ydDaoUtils.paraRecChkNull(recTemp, szYD_STR_LOC_FIELD);
				
				//----------------------------------------------------------------------------------------------------------
				// 2. PT_소재이송지시에 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드를 업데이트 처리
				//----------------------------------------------------------------------------------------------------------
				szRtnMsg = uptPtStlFrtoMoveWhenCarUnLoadCmpl(szSTL_NO, szYD_STR_LOC);
				

			}
			//----------------------------------------------------------------------------------------------------------
			
		}catch(JDTOException e) {
			szMsg = "[" + szOperationName + "] 공통업무 처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
		}
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 하차완료시 공통업무처리 - 진행관리[PT] 공통테이블 업데이트
	 * @param szYD_CAR_SCH_ID
	 * @return
	 * @throws DAOException
	 */
	public static int procCarUnLoadCmpl(String szYD_CAR_SCH_ID) throws DAOException {
		//리턴값
		int intRtnVal 						= 0;
		//메세지
		String szMsg 						= null;
		String szRtnMsg 					= null;
		//메소드명
		String szMethodName    				= "procCarUnLoadCmpl";	
		String szOperationName 				= "하차완료시 공통업무처리";
		//레코드셋
		JDTORecordSet rsResult 				= null;
		JDTORecordSet rsResultTemp 			= null;
		JDTORecordSet getRecSet 			= null;
		//레코드
		JDTORecord recInTemp 				= null;
		JDTORecord recTemp 					= null;
		JDTORecord recOutTemp 				= null;
		JDTORecord recSend 					= null;
		JDTORecord recSendPR 				= null;
		//차량이송재료 DAO
		YdCarFtmvMtlDao ydCarFtmvMtlDao 	= new YdCarFtmvMtlDao();
		//진행관리 - 이송지시
		PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
		//진행관리 - 주편공통DAO
		PtMSlabCommDao ptMSlabCommDao 		= new PtMSlabCommDao();
		//진행관리 - 슬라브공통DAO
		PtSlabCommDao ptSlabCommDao 		= new PtSlabCommDao();
		//권하 20090616.김진욱
		//CraneUdHdSeEJBBean craneUdHdSeEJBBean = new CraneUdHdSeEJBBean();
		//재료번호
		String szSTL_NO 					= null;
		//재료품목 20090616.김진욱
		String szYD_MTL_ITEM 				= null;
		//이전저장위치 20090616.김진욱
		String szYD_STR_LOC 				= null;
		String szPT_TB_COMM 				= null;
		//야드구분
		String szYD_GP						= null;
		//슬라브지시행선
		String szSLAB_WO_RT_CD				= null;
		//재열재구분
		String szREHEAT_SLAB_GP				= null;
		//발지개소코드
		String szSPOS_WLOC_CD				= null;
		
		try {
			//--------------------------------------------------------------------------------------------------------------
			//	차량이송재료 조회
			//--------------------------------------------------------------------------------------------------------------
			
			szMsg="[" + szOperationName + "] 메소드 시작 - 차량스케줄[" + szYD_CAR_SCH_ID + "]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			// 1. 차량 이송재료를 조회
			
			szMsg="[" + szOperationName + "] 차량스케줄["+szYD_CAR_SCH_ID+"]로 차량이송재료 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
			intRtnVal = ydCarFtmvMtlDao.getYdCarftmvmtl(recInTemp, rsResult, 4);
			if(intRtnVal <= 0) {
				szMsg="[" + szOperationName + "] 차량스케줄에 이송재료 가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return intRtnVal;
			}
			
			szMsg="[" + szOperationName + "] 차량스케줄["+szYD_CAR_SCH_ID+"]로 차량이송재료 조회 완료 - 대상재건수["+intRtnVal+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//--------------------------------------------------------------------------------------------------------------
			
			
			//--------------------------------------------------------------------------------------------------------------
			//	공통테이블 조회 후 공통테이블 업데이트, 이송지시테이블 업데이트, 
			//	C연주/A후판슬라브야드인 경우에 생산통제로 이송하차실적 전송
			//--------------------------------------------------------------------------------------------------------------
			recSendPR		= JDTORecordFactory.getInstance().create();
			recSend 		= JDTORecordFactory.getInstance().create();
			recInTemp 		= JDTORecordFactory.getInstance().create();
			for(int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++) {
				rsResult.absolute(Loop_i);
				JDTORecord recOutTemp1 = rsResult.getRecord();
				szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(recOutTemp1, "YD_MTL_ITEM"); 
				szSTL_NO = ydDaoUtils.paraRecChkNull(recOutTemp1, "STL_NO");
				
				//--------------------------------------------------------------------------------------------------------------
				//	공통테이블 조회 - 주편/슬라브 공통테이블 조회
				//--------------------------------------------------------------------------------------------------------------
				
				szMsg="[" + szOperationName + "] 재료번호["+szSTL_NO+"]로 주편/슬라브공통테이블 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				szRtnMsg = getPtCommStock(szSTL_NO, getRecSet);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg="[" + szOperationName + "] 진행관리의 공통테이블에 재료번호[" + szSTL_NO + "] 조회 실패 ";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					continue;
				}
				
				szMsg="[" + szOperationName + "] 재료번호["+szSTL_NO+"]로 주편/슬라브공통테이블 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet.first();
				recTemp = getRecSet.getRecord();
				
				//--------------------------------------------------------------------------------------------------------------
				
				
				//--------------------------------------------------------------------------------------------------------------
				//	주편/슬라브공통테이블에 소재인수일시 업데이트
				//--------------------------------------------------------------------------------------------------------------
				
				szYD_STR_LOC = ydDaoUtils.paraRecChkNull(recTemp, "YD_STR_LOC");
				//주편공통테이블과 슬라브공통테이블중 어느 테이블을 조회했는 지를 결정
				szPT_TB_COMM = ydDaoUtils.paraRecChkNull(recTemp, "PT_TB_COMM");
				
				intRtnVal = 0;
				if( szPT_TB_COMM.equals("B") )	{				//주편공통테이블 업데이트 - 소재인수일시
					szMsg="[" + szOperationName + "] 주편공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 시작";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recInTemp.setField("MSLAB_NO", szSTL_NO);
					intRtnVal = ptMSlabCommDao.updPtMSlabComm(recInTemp, 1);
				}else if( szPT_TB_COMM.equals("S") )	{		//슬라브공통테이블업데이트 - 소재인수일시
					
					szMsg="[" + szOperationName + "] 슬라브공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 시작";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recInTemp.setField("SLAB_NO", szSTL_NO);
					intRtnVal = ptSlabCommDao.updPtSlabComm(recInTemp, 1);
				}
				
				if(intRtnVal <= 0) {
					szMsg="[" + szOperationName + "] 진행관리의 " +(szPT_TB_COMM.equals("B") ? "주편" : (szPT_TB_COMM.equals("S") ? "슬라브" : "") ) 
					+ "공통테이블에 재료번호[" + szSTL_NO + "(" + szYD_MTL_ITEM + ")] 하차완료시점[소재인수일시] 업데이트 실패 ";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
					//continue;
				}
				
				szMsg="[" + szOperationName + "] 주편/슬라브공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------------------------
				
				
				
				//================================================================
				// 야드저장품 항목을 업데이트 한다.
				//================================================================
				YdCodeMapping ydCodeMapping             = new YdCodeMapping();
				ydCodeMapping.getMappingCommonField("PMYDJ002", szSTL_NO, false);
				
				
				//--------------------------------------------------------------------------------------------------------------
				//	이송지시테이블 조회
				//--------------------------------------------------------------------------------------------------------------
				
				// 2. PT_소재이송지시에 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드를 업데이트 처리
				recInTemp.setField("STL_NO", szSTL_NO);
				
				szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료번호["+szSTL_NO+"]로 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(recInTemp, rsResultTemp, 0);
				if( intRtnVal <= 0 ) {
					szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + szSTL_NO + "]가 존재하지 않습니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
				}
				
				szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료번호["+szSTL_NO+"]로 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResultTemp.first();
				recOutTemp = rsResultTemp.getRecord();
				
				//--------------------------------------------------------------------------------------------------------------
				
				
				//--------------------------------------------------------------------------------------------------------------
				//	이송지시테이블 업데이트	
				//--------------------------------------------------------------------------------------------------------------
				
				//야드재료예정저장To위치코드 20090616.김진욱
				recOutTemp.setField("YD_MTL_PLN_STR_TO_LOC_CD", szYD_STR_LOC);
				//20090618.김진욱 이송상태코드(이송완료)
				recOutTemp.setField("FRTOMOVE_STAT_CD", "*");
				
				
				intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 1);
				if( intRtnVal <= 0 ) {
					szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 실패";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
				}
				szMsg="[" + szOperationName + "]  진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 재료번호["
					+ ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO") + "], 이송지시차수[" 
					+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 성공";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------------------------
				if(szYD_STR_LOC.length() > 1){
					szYD_GP	= szYD_STR_LOC.substring(0, 1);
				}else{
					szYD_GP	= "";
				}
				//--------------------------------------------------------------------------------------------------------------
				//	주편에 레코드상태가 3인 재료에 대해서 슬라브테이블의 지시행선(SLAB_WO_RT_CD)이 'PA','PB' 이고 
				//	재열재구분(REHEAT_SLAB_GP)이 '1','2' (GP : 176)
				//	C연주/A후판슬라브야드에 만 적용
				//--------------------------------------------------------------------------------------------------------------
				
				if( szPT_TB_COMM.equals("S") )	{					//슬라브인 경우
					
					//C연주/A후판슬라브야드인 경우에만 적용
					if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) ||
						szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD) || //항만슬라브야드 기능추가 - 2016.01.04 LeeJY
						szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {
						
						szSLAB_WO_RT_CD		=  ydDaoUtils.paraRecChkNull(recTemp, "SLAB_WO_RT_CD");
						szREHEAT_SLAB_GP	=  ydDaoUtils.paraRecChkNull(recTemp, "REHEAT_SLAB_GP");
						
						szMsg="[" + szOperationName + "] 야드구분["+szYD_GP+"]이 C연주/A후판슬라브야드 이므로 슬라브지시행선[" + szSLAB_WO_RT_CD + "], 재열재구분["+szREHEAT_SLAB_GP+"] 확인 시작";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//슬라브지시행선이 PA, PB이고 재열재구분이 1, 2인 경우에는 생산통제로 전문 발송
						if( ( szSLAB_WO_RT_CD.equals("PA") || szSLAB_WO_RT_CD.equals("PB") )
							&& ( szREHEAT_SLAB_GP.equals("1") || szREHEAT_SLAB_GP.equals("2") )
						) {
							szMsg="[" + szOperationName + "] 슬라브지시행선[" + szSLAB_WO_RT_CD + "]이 PA, PB이고, 재열재구분["+szREHEAT_SLAB_GP+"]이 1, 2이므로 생산통제로 이송하차실적[YDCTJ034] 전송 시작";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
							
							recSend.setField("MSG_ID" , 			"YDCTJ034");
							recSend.setField("SLAB_NO", 			szSTL_NO);
							ydDelegate.sendMsg(recSend);
							
							szMsg="[" + szOperationName + "] 슬라브지시행선[" + szSLAB_WO_RT_CD + "]이 PA, PB이고, 재열재구분["+szREHEAT_SLAB_GP+"]이 1, 2이므로 생산통제로 이송하차실적[YDCTJ034] 전송 시작";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						}
					}
					
				}
				
				//--------------------------------------------------------------------------------------------------------------
				
				//--------------------------------------------------------------------------------------------------------------
				//	후판조업[DKY23]에서 발생한 재열재에 대하여 조업불출후 A후판슬라브야드적치시 해당 실적[YDPRJ003]을 전송
				//--------------------------------------------------------------------------------------------------------------
				if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {			//A후판슬라브야드
					
					szSPOS_WLOC_CD			= ydDaoUtils.paraRecChkNull(recOutTemp, "SPOS_WLOC_CD");
					
					if( szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
						szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){//후판조업개소코드[이송지시테이블의 발지개소코드]
						recSendPR.setField("MSG_ID" , 			YdConstant.YDPRJ003);
						recSendPR.setField("STL_NO", 			szSTL_NO);
						ydDelegate.sendMsg(recSendPR);
					}
				}
				
				//--------------------------------------------------------------------------------------------------------------
				
			}
			
		}catch(JDTOException e) {
			szMsg = "[" + szOperationName + "] 공통업무 처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	
	/**
	 * 하차완료시 재료단위 공통업무처리 - 진행관리[PT] 공통테이블 업데이트
	 * @param szSTL_NO
	 * @return
	 * @throws DAOException
	 */
	public static int procCarUnLoadCmplStlNo(String szSTL_NO) throws DAOException {
		//리턴값
		int intRtnVal 						= 0;
		//메세지
		String szMsg 						= null;
		String szRtnMsg 					= null;
		//메소드명
		String szMethodName    				= "procCarUnLoadCmplStlNo";	
		String szOperationName 				= "하차완료시 재료단위 공통업무처리";
		JDTORecordSet rsResultTemp 			= null;
		JDTORecordSet getRecSet 			= null;
		//레코드
		JDTORecord recInTemp 				= null;
		JDTORecord recTemp 					= null;
		JDTORecord recOutTemp 				= null;
		JDTORecord recSend 					= null;
		JDTORecord recSendPR 				= null;
		//진행관리 - 이송지시
		PtStlFrtoMoveDao ptStlFrtoMoveDao 	= new PtStlFrtoMoveDao();
		//진행관리 - 주편공통DAO
		PtMSlabCommDao ptMSlabCommDao 		= new PtMSlabCommDao();
		//진행관리 - 슬라브공통DAO
		PtSlabCommDao ptSlabCommDao 		= new PtSlabCommDao();
		//권하 20090616.김진욱
		//CraneUdHdSeEJBBean craneUdHdSeEJBBean = new CraneUdHdSeEJBBean();
 
		//재료품목 20090616.김진욱
		String szYD_MTL_ITEM 				= null;
		//이전저장위치 20090616.김진욱
		String szYD_STR_LOC 				= null;
		String szPT_TB_COMM 				= null;
		//야드구분
		String szYD_GP						= null;
		//슬라브지시행선
		String szSLAB_WO_RT_CD				= null;
		//재열재구분
		String szREHEAT_SLAB_GP				= null;
		//발지개소코드
		String szSPOS_WLOC_CD				= null;
		
		try {
			//--------------------------------------------------------------------------------------------------------------
			//	공통테이블 조회 후 공통테이블 업데이트, 이송지시테이블 업데이트, 
			//	C연주/A후판슬라브야드인 경우에 생산통제로 이송하차실적 전송
			//--------------------------------------------------------------------------------------------------------------
			recSendPR		= JDTORecordFactory.getInstance().create();
			recSend 		= JDTORecordFactory.getInstance().create();
			recInTemp 		= JDTORecordFactory.getInstance().create();
 
				
				//--------------------------------------------------------------------------------------------------------------
				//	공통테이블 조회 - 주편/슬라브 공통테이블 조회
				//--------------------------------------------------------------------------------------------------------------
				
				szMsg="[" + szOperationName + "] 재료번호["+szSTL_NO+"]로 주편/슬라브공통테이블 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet = JDTORecordFactory.getInstance().createRecordSet("");
				szRtnMsg = getPtCommStock(szSTL_NO, getRecSet);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg="[" + szOperationName + "] 진행관리의 공통테이블에 재료번호[" + szSTL_NO + "] 조회 실패 ";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);			 
				}
				
				szMsg="[" + szOperationName + "] 재료번호["+szSTL_NO+"]로 주편/슬라브공통테이블 조회 완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				getRecSet.first();
				recTemp = getRecSet.getRecord();
				
				//--------------------------------------------------------------------------------------------------------------
				
				
				//--------------------------------------------------------------------------------------------------------------
				//	주편/슬라브공통테이블에 소재인수일시 업데이트
				//--------------------------------------------------------------------------------------------------------------
				
				szYD_STR_LOC = ydDaoUtils.paraRecChkNull(recTemp, "YD_STR_LOC");
				//주편공통테이블과 슬라브공통테이블중 어느 테이블을 조회했는 지를 결정
				szPT_TB_COMM = ydDaoUtils.paraRecChkNull(recTemp, "PT_TB_COMM");
				
				intRtnVal = 0;
				if( szPT_TB_COMM.equals("B") )	{				//주편공통테이블 업데이트 - 소재인수일시
					szMsg="[" + szOperationName + "] 주편공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 시작";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recInTemp.setField("MSLAB_NO", szSTL_NO);
					intRtnVal = ptMSlabCommDao.updPtMSlabComm(recInTemp, 1);
				}else if( szPT_TB_COMM.equals("S") )	{		//슬라브공통테이블업데이트 - 소재인수일시
					
					szMsg="[" + szOperationName + "] 슬라브공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 시작";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recInTemp.setField("SLAB_NO", szSTL_NO);
					intRtnVal = ptSlabCommDao.updPtSlabComm(recInTemp, 1);
				}
				
				if(intRtnVal <= 0) {
					szMsg="[" + szOperationName + "] 진행관리의 " +(szPT_TB_COMM.equals("B") ? "주편" : (szPT_TB_COMM.equals("S") ? "슬라브" : "") ) 
					+ "공통테이블에 재료번호[" + szSTL_NO + "(" + szYD_MTL_ITEM + ")] 하차완료시점[소재인수일시] 업데이트 실패 ";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
					//continue;
				}
				
				szMsg="[" + szOperationName + "] 주편/슬라브공통테이블에 재료번호["+szSTL_NO+"]로 소재인수일시 업데이트 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------------------------
				
				
				
				//================================================================
				// 야드저장품 항목을 업데이트 한다.
				//================================================================
				YdCodeMapping ydCodeMapping             = new YdCodeMapping();
				ydCodeMapping.getMappingCommonField("PMYDJ002", szSTL_NO, false);
				
				
				//--------------------------------------------------------------------------------------------------------------
				//	이송지시테이블 조회
				//--------------------------------------------------------------------------------------------------------------
				
				// 2. PT_소재이송지시에 이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드를 업데이트 처리
				recInTemp.setField("STL_NO", szSTL_NO);
				
				szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료번호["+szSTL_NO+"]로 조회 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResultTemp = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ptStlFrtoMoveDao.getPtStlFrtoMove(recInTemp, rsResultTemp, 0);
				if( intRtnVal <= 0 ) {
					szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료[" + szSTL_NO + "]가 존재하지 않습니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
				}
				
				szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에 재료번호["+szSTL_NO+"]로 조회 완료 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				rsResultTemp.first();
				recOutTemp = rsResultTemp.getRecord();
				
				//--------------------------------------------------------------------------------------------------------------
				
				
				//--------------------------------------------------------------------------------------------------------------
				//	이송지시테이블 업데이트	
				//--------------------------------------------------------------------------------------------------------------
				
				//야드재료예정저장To위치코드 20090616.김진욱
				recOutTemp.setField("YD_MTL_PLN_STR_TO_LOC_CD", szYD_STR_LOC);
				//20090618.김진욱 이송상태코드(이송완료)
				recOutTemp.setField("FRTOMOVE_STAT_CD", "*");
				
				
				intRtnVal = ptStlFrtoMoveDao.updPtStlFrtoMove(recOutTemp, 1);
				if( intRtnVal <= 0 ) {
					szMsg="[" + szOperationName + "] 진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 실패";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					//return intRtnVal;
				}
				szMsg="[" + szOperationName + "]  진행관리의 소재이송지시테이블[TB_PT_STLFRTOMOVE]- 재료번호["
					+ ydDaoUtils.paraRecChkNull(recOutTemp, "STL_NO") + "], 이송지시차수[" 
					+ ydDaoUtils.paraRecChkNull(recOutTemp, "TRANSWORD_SEQNO") + "]에  이송완료일자, 이송계상일자, 이송상태코드, 야드재료예정저장To위치코드 업데이트 성공";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------------------------------------
				if(szYD_STR_LOC.length() > 1){
					szYD_GP	= szYD_STR_LOC.substring(0, 1);
				}else{
					szYD_GP	= "";
				}
				//--------------------------------------------------------------------------------------------------------------
				//	주편에 레코드상태가 3인 재료에 대해서 슬라브테이블의 지시행선(SLAB_WO_RT_CD)이 'PA','PB' 이고 
				//	재열재구분(REHEAT_SLAB_GP)이 '1','2' (GP : 176)
				//	C연주/A후판슬라브야드에 만 적용
				//--------------------------------------------------------------------------------------------------------------
				
				if( szPT_TB_COMM.equals("S") )	{					//슬라브인 경우
					
					//C연주/A후판슬라브야드인 경우에만 적용
					if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) || 
							szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD) ||    //항만슬라브야드 기능추가 - 2016.01.04 LeeJY
						szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {
						
						szSLAB_WO_RT_CD		=  ydDaoUtils.paraRecChkNull(recTemp, "SLAB_WO_RT_CD");
						szREHEAT_SLAB_GP	=  ydDaoUtils.paraRecChkNull(recTemp, "REHEAT_SLAB_GP");
						
						szMsg="[" + szOperationName + "] 야드구분["+szYD_GP+"]이 C연주/A후판슬라브야드 이므로 슬라브지시행선[" + szSLAB_WO_RT_CD + "], 재열재구분["+szREHEAT_SLAB_GP+"] 확인 시작";
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						
						//슬라브지시행선이 PA, PB이고 재열재구분이 1, 2인 경우에는 생산통제로 전문 발송
						if( ( szSLAB_WO_RT_CD.equals("PA") || szSLAB_WO_RT_CD.equals("PB") )
							&& ( szREHEAT_SLAB_GP.equals("1") || szREHEAT_SLAB_GP.equals("2") )
						) {
							szMsg="[" + szOperationName + "] 슬라브지시행선[" + szSLAB_WO_RT_CD + "]이 PA, PB이고, 재열재구분["+szREHEAT_SLAB_GP+"]이 1, 2이므로 생산통제로 이송하차실적[YDCTJ034] 전송 시작";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
							
							recSend.setField("MSG_ID" , 			"YDCTJ034");
							recSend.setField("SLAB_NO", 			szSTL_NO);
							ydDelegate.sendMsg(recSend);
							
							szMsg="[" + szOperationName + "] 슬라브지시행선[" + szSLAB_WO_RT_CD + "]이 PA, PB이고, 재열재구분["+szREHEAT_SLAB_GP+"]이 1, 2이므로 생산통제로 이송하차실적[YDCTJ034] 전송 시작";
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						}
					}
					
				}
				
				//--------------------------------------------------------------------------------------------------------------
				
				//--------------------------------------------------------------------------------------------------------------
				//	후판조업[DKY23]에서 발생한 재열재에 대하여 조업불출후 A후판슬라브야드적치시 해당 실적[YDPRJ003]을 전송
				//--------------------------------------------------------------------------------------------------------------
				if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {			//A후판슬라브야드
					
					szSPOS_WLOC_CD			= ydDaoUtils.paraRecChkNull(recOutTemp, "SPOS_WLOC_CD");
					
					if( szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_A_PLATE_PLANT)||
						szSPOS_WLOC_CD.equals(YdConstant.WLOC_CD_B_PLATE_PLANT)){	//후판조업개소코드[이송지시테이블의 발지개소코드]
						recSendPR.setField("MSG_ID" , 			YdConstant.YDPRJ003);
						recSendPR.setField("STL_NO", 			szSTL_NO);
						ydDelegate.sendMsg(recSendPR);
					}
				}
				
				//--------------------------------------------------------------------------------------------------------------
				
		 
			
		}catch(JDTOException e) {
			szMsg = "[" + szOperationName + "] 공통업무 처리시 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szClassName + " : " + szMethodName + e.getMessage(), e);
		}
		return intRtnVal;
	}
	
	/**
	 * 공통테이블에서 재료정보를 조회하는 메소드
	 * @param szSTL_NO
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public static String getPtCommStock(String szSTL_NO, JDTORecordSet rsResult) throws JDTOException {
		//리턴메세지
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		//저장품DAO
		YdStockDao ydStockDao = new YdStockDao();
		int intRtnVal = 0;
		//로그메세지
		String szLogMsg = null;
		//메소드명
		String szMethodName = "getPtCommStock";
		//레코드셋
		JDTORecordSet rsOut = JDTORecordFactory.getInstance().createRecordSet("");
		//레코드
		JDTORecord recInParam = JDTORecordFactory.getInstance().create("");
		JDTORecord recTemp = null;
		try {
			
			recInParam.setField("MSLAB_NO", szSTL_NO);
			//주편공통테이블에서 재료를 먼저 조회한다.
			intRtnVal = ydStockDao.getYdStock(recInParam, rsOut, 6);
			szLogMsg = "주편공통테이블에서 재료["+szSTL_NO+"]를 먼저 조회한다";
            ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			if( intRtnVal == 0 ) {
				szLogMsg = "주편공통테이블에서 재료["+szSTL_NO+"]가 존재하지 않으므로 슬라브공통테이블을 조회한다";
	            ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				//슬라브공통테이블 조회
				recInParam.setField("SLAB_NO", szSTL_NO);
				rsOut = JDTORecordFactory.getInstance().createRecordSet("");
				intRtnVal = ydStockDao.getYdStock(recInParam, rsOut, 2);
				if( intRtnVal > 0 ) {
					szLogMsg = "슬라브공통테이블에 재료["+szSTL_NO+"]가 존재합니다.";
		            ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					rsOut.first();
					recTemp = rsOut.getRecord();
					recTemp.setField("PT_TB_COMM", "S");
				}
			}else if( intRtnVal > 0 ) {
				szLogMsg = "주편공통테이블에 재료["+szSTL_NO+"]가 존재합니다.";
	            ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				rsOut.first();
				recTemp = rsOut.getRecord();
				recTemp.setField("PT_TB_COMM", "B");
				//주편공통테이블에서 재료를 조회해서 Record진행상태가 3인 경우에는 슬라브공통테이블을 조회하여 대상재를 찾는다.
				if( ydDaoUtils.paraRecChkNull(recTemp, "RECORD_PROG_STAT").equals("3") ) {
					szLogMsg = "주편공통테이블에 재료["+szSTL_NO+"]가 존재하지만 레코드가 종료된 상태이므로 슬라브공통테이블을 조회한다.";
		            ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
					//슬라브공통테이블 조회
					recInParam.setField("SLAB_NO", szSTL_NO);
					rsOut = JDTORecordFactory.getInstance().createRecordSet("");
					intRtnVal = ydStockDao.getYdStock(recInParam, rsOut, 2);
					if( intRtnVal > 0 ) {
						szLogMsg = "슬라브공통테이블에 재료["+szSTL_NO+"]가 존재합니다.";
			            ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
						rsOut.first();
						recTemp = rsOut.getRecord();
						recTemp.setField("PT_TB_COMM", "S");
					}
				}
			}
			
	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	            	szLogMsg = "주편공통테이블이나 슬라브공통테이블에 재료[" + szSTL_NO + "]가 존재하지 않습니다. 에러코드 : " + intRtnVal;
	                ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
	            } else if (intRtnVal == -2) {
	            	szLogMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
	            }
	            szRtnMsg = YdConstant.RETN_CD_FAILURE;
	        }else{
	        	rsResult.addRecord(recTemp);
	        }
		}catch(JDTOException ex) {
			szLogMsg = " 에러 발생 : " + ex.getMessage();
            ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
            throw ex;
		}
		return szRtnMsg;
	}
	
	/**
     * 오퍼레이션명 : 진도코드갱신 Setting 
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public static String setProgCodeForSlab(String szStlNo){
    	
    	YdStockDao	ydStockDao	= new YdStockDao();
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szCurrProgCd					= "" ;
    	String szBefoProgCd					= "" ;
    	
    	String szMsg						= "" ;
    	String szMethodName					= "setProgCodeForSlab" ;
    	String szOperationName				= "재료공통진도코드갱신(슬라브)" ;
    	//재료종류별 번호
    	//String szStlNo						= "" ;
    	int intRtnVal 						= 0 ;
    	String szRtnMsg						= null;
    	String szSLAB_WO_RT_CD				= null;
    	String szSCARFING_YN				= null;
    	String szSCARFING_DONE_YN			= null;
    	String szMILL_WO_EXN				= "";
    	String szPT_TB_COMM					= null;
    	//전전진도코드
    	String szBEFOBEFO_PROG_CD			= null;
    	//주문여재구분
    	String szORD_YEOJAE_GP  = 			null;
    	String szCURR_PROG_REG_DDTT  = 			null;
    	String szBEFO_PROG_REG_DDTT  = 			null;
    	String szBEFOBEFO_PROG_REG_DDTT  = 			null;
    	String szCURR_PROG_CD_REG_PGM  = 			null;
    	String szBEFO_PROG_CD_REG_PGM  = 			null;
    	String szBEFOBEFO_PROG_CD_REG_PGM  = 			null;
    	
        try{
        	//szStlNo 	= msgRecord.getFieldString("STL_NO") ;

        	/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        	 * 업무기준 : 주편/슬라브공통테이블에 업데이트 처리
        	 * 수정자 : 임춘수
        	 * 일자 : 2009.07.21
        	 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
        	szRtnMsg = YdCommonUtils.getPtCommStock(szStlNo, getRecSet);
        	if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
        		szMsg = "[" + szOperationName + "] 주편/슬라브공통테이블에서 재료[" + szStlNo + "] 조회 시 오류발생 : " + szRtnMsg;
                ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
        		return szRtnMsg;
        	}
        	
        	getRecSet.first();
        	getRecord = getRecSet.getRecord() ;
        	szPT_TB_COMM  = ydDaoUtils.paraRecChkNull(getRecord, "PT_TB_COMM");
        	//주문여재구분
        	szORD_YEOJAE_GP  = ydDaoUtils.paraRecChkNull(getRecord, "ORD_YEOJAE_GP");
        	//슬라브지시행선코드
        	szSLAB_WO_RT_CD  = ydDaoUtils.paraRecChkNull(getRecord, "SLAB_WO_RT_CD");
        	//스카핑여부
        	szSCARFING_YN  = ydDaoUtils.paraRecChkNull(getRecord, "SCARFING_YN");
        	//스카핑완료여부
        	szSCARFING_DONE_YN  = ydDaoUtils.paraRecChkNull(getRecord, "SCARFING_DONE_YN");
        	//압연지시여부 - 슬라브에만 적용됨
        	if(szPT_TB_COMM.equals("S")) {
        		szMILL_WO_EXN  = ydDaoUtils.paraRecChkNull(getRecord, "MILL_WO_EXN");
        	}
        	
        	szMsg = "[" + szOperationName + "] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + "[" + szStlNo + "], ";
        	szMsg += "주문여재구분[" + szORD_YEOJAE_GP + "], 슬라브지시행선코드[" + szSLAB_WO_RT_CD + "], 스카핑여부[" + szSCARFING_YN + "], 스카핑완료여부[" + szSCARFING_DONE_YN + "], 압연지시여부[" + szMILL_WO_EXN + "]";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//현재진도코드
        	szCurrProgCd = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD");
        	//전 진도코드
        	szBefoProgCd = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD");
        	//전전진도코드 = 전 진도코드
        	szBEFOBEFO_PROG_CD = szBefoProgCd;
        	//전전진도코드 = 전진도코드
        	szBefoProgCd = szCurrProgCd;
        	
        	//현재진도코드등록Program
        	szCURR_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD_REG_PGM");
        	//전진도코드등록Program
        	szBEFO_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD_REG_PGM");
        	//전전진도코드등록Program
        	szBEFOBEFO_PROG_CD_REG_PGM = szBEFO_PROG_CD_REG_PGM;
        	szBEFO_PROG_CD_REG_PGM = szCURR_PROG_CD_REG_PGM;
        	szCURR_PROG_CD_REG_PGM = szMethodName;
        	
        	//현재진도등록일시
        	szCURR_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_REG_DDTT");
        	//전진도등록일시
        	szBEFO_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_REG_DDTT");
        	//전전진도등록일시
        	szBEFOBEFO_PROG_REG_DDTT = szBEFO_PROG_REG_DDTT;
        	szBEFO_PROG_REG_DDTT = szCURR_PROG_REG_DDTT;
        	szCURR_PROG_REG_DDTT = YdUtils.getCurDate("yyyyMMddHHmmss");
        	
        	//szCurrProgCd = YdCommonUtils.getCurrProgCd(szPT_TB_COMM, szSLAB_WO_RT_CD, szORD_YEOJAE_GP, szSCARFING_YN, szSCARFING_DONE_YN, szMILL_WO_EXN);
    		//---------------------------------------------------------------------------------------------------------
    		//---------------------------------------------------------------------------------------------------------
    		ymCommonDAO dao = ymCommonDAO.getInstance();
    	    List FrtoProductList = null;
        	//공정 함수를 이용한 진도코드 가져오기
        	if(szPT_TB_COMM.equals("B")){
        		//주편 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcd";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szStlNo});
        	}else if (szPT_TB_COMM.equals("S")) {
        		//슬라브 공통
    		    String queryID	= "ym.facilitywork.putwrecord.session.getListcurrprogcdSlab";
    		    FrtoProductList = dao.getCommonList(queryID, new Object[]{szStlNo});
        	}       	

        	JDTORecord FrtoProduct = (JDTORecord)FrtoProductList.get(0);

        	szCurrProgCd =StringHelper.evl(FrtoProduct.getFieldString("CURR_PROG_CD"), "");
    		ydUtils.putLog(szClassName, szMethodName, "IHSF_PM_주편SLAB진도찾기==>>:"+szCurrProgCd, YdConstant.DEBUG);
    		//---------------------------------------------------------------------------------------------------------
    		//---------------------------------------------------------------------------------------------------------
        	szMsg = "[" + szOperationName + "] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + " 재료[" + szStlNo + "] 수정 후 현재진도코드[" + szCurrProgCd + "], 전진도코드[" + szBefoProgCd + "], 전전진도코드[" + szBEFOBEFO_PROG_CD + "]";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
        	//전 진도코드등록일시 = 현재진도코드등록일시 , 전전진도코드등록일시 = 전진도코드등록일시
			//현재시간
        	setRecord.setField("CURR_PROG_CD", 					szCurrProgCd);
        	setRecord.setField("BEFO_PROG_CD", 					szBefoProgCd);
        	setRecord.setField("BEFOBEFO_PROG_CD", 				szBEFOBEFO_PROG_CD);
			setRecord.setField("CURR_PROG_REG_DDTT", 			szCURR_PROG_REG_DDTT);
			setRecord.setField("BEFO_PROG_REG_DDTT", 			szBEFO_PROG_REG_DDTT);
			setRecord.setField("BEFOBEFO_PROG_REG_DDTT", 		szBEFOBEFO_PROG_REG_DDTT);
			setRecord.setField("CURR_PROG_CD_REG_PGM", 			szCURR_PROG_CD_REG_PGM);
			setRecord.setField("BEFO_PROG_CD_REG_PGM", 			szBEFO_PROG_CD_REG_PGM);
			setRecord.setField("BEFOBEFO_PROG_CD_REG_PGM", 		szBEFOBEFO_PROG_CD_REG_PGM);
			setRecord.setField("FNL_REG_PGM", 					szMethodName);
			setRecord.setField("MODIFIER", 					    "YDSYSTEM");
			

        	//업데이트 항목 추가해야함 ///////////////////////////////////////////////////////
        	if(szPT_TB_COMM.equals("B")){
        		//주편 공통
        		setRecord.setField("MSLAB_NO", szStlNo);
        		intRtnVal = ydStockDao.updPtComm_PROG_CD(setRecord, 2);
        		
        	}else if (szPT_TB_COMM.equals("S")) {
        		//슬라브 공통
        		setRecord.setField("SLAB_NO", szStlNo);
        		intRtnVal = ydStockDao.updPtComm_PROG_CD(setRecord, 0);
        		
        	}
        	
        	if( intRtnVal < 0 ) {
        		szMsg = "[" + szOperationName + "] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + " 재료[" + szStlNo + "] 수정 시 오류발생 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
                return YdConstant.RETN_CD_FAILURE;
        	}else if( intRtnVal == 0 ) {
        		szMsg = "[" + szOperationName + "] " + (szPT_TB_COMM.equals("B") ? "주편" : "슬라브") + " 재료[" + szStlNo + "] 수정 시 존재하지 않습니다.";
                ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
                return YdConstant.RETN_CD_NOTEXIST;
        	}
        	
        }catch(Exception e){
        	szMsg = "[" + szOperationName + "] 오류발생 : " + e.getMessage();
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
        }//end of try~catch
        
        return YdConstant.RETN_CD_SUCCESS;
        
    }//end of setProgCodeForSlab()
    
    /**
     * 오퍼레이션명 : 진도코드갱신 Setting 
     * @param msgRecord
     * @return intRtnVal
     * @throws 
     */
    public static String setProgCodeForPlateOrCoil(String szStlNo, String szYD_MTL_ITEM){
    	
    	YdStockDao	ydStockDao	= new YdStockDao();
    	//작업계획 Sim 조회 한 값
    	JDTORecordSet getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	//getRecSet의 첫번째 레코드 값을 저장
    	JDTORecord 	  getRecord 			= JDTORecordFactory.getInstance().create();
    	//Update할 레코드 셋팅
    	JDTORecord 	  setRecord 			= JDTORecordFactory.getInstance().create();
    	
    	String szCurrProgCd					= "" ;
    	String szBefoProgCd					= "" ;
    	
    	String szMsg						= "" ;
    	String szMethodName					= "setProgCodeForPlateOrCoil" ;
    	String szOperationName				= "재료공통진도코드갱신(후판/코일)" ;
    	String szMTL_NM						= "";
    	//재료종류별 번호
    	//String szStlNo						= "" ;
    	int intRtnVal 						= 0 ;
    	String szRtnMsg						= null;
    	//전전진도코드
    	String szBEFOBEFO_PROG_CD			= null;
    	//주문여재구분
    	String szCURR_PROG_REG_DDTT  = 			null;
    	String szBEFO_PROG_REG_DDTT  = 			null;
    	String szBEFOBEFO_PROG_REG_DDTT  = 			null;
    	String szCURR_PROG_CD_REG_PGM  = 			null;
    	String szBEFO_PROG_CD_REG_PGM  = 			null;
    	String szBEFOBEFO_PROG_CD_REG_PGM  = 			null;
    	
        try{
        	
        	if( szYD_MTL_ITEM.startsWith("P") ) {
        		szMsg = "[" + szOperationName + "] 후판테이블에 재료["+szStlNo+"] 조회 시작";
        		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
        		szMTL_NM = "후판";
        		
        		getRecord.setField("PLATE_NO", szStlNo);
				intRtnVal = ydStockDao.getYdStock(getRecord, getRecSet, 4);
        	}else if( szYD_MTL_ITEM.startsWith("C") ) {
        		szMsg = "[" + szOperationName + "] 코일테이블에 재료["+szStlNo+"] 조회 시작";
        		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
        		szMTL_NM = "코일";
        		
        		getRecord.setField("COIL_NO", szStlNo);
				intRtnVal = ydStockDao.getYdStock(getRecord, getRecSet, 8);
        	}
        	
        	if( intRtnVal < 0 ) {
        		szMsg = "[" + szOperationName + "] "+szMTL_NM+"테이블에 재료["+szStlNo+"] 조회 시 오류발생 - 반환값 : " +intRtnVal;
        		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
        		return YdConstant.RETN_CD_FAILURE;
        	}else if( intRtnVal == 0 ) {
        		szMsg = "[" + szOperationName + "] "+szMTL_NM+"테이블에 재료["+szStlNo+"] 조회 시 존재하지 않습니다.";
        		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
        		return YdConstant.RETN_CD_NOTEXIST;
        	}
        	
        	getRecSet.first();
        	getRecord = getRecSet.getRecord() ;

        	
        	//현재진도코드
        	szCurrProgCd = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD");
        	//전 진도코드
        	szBefoProgCd = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD");
        	//전전진도코드 = 전 진도코드
        	szBEFOBEFO_PROG_CD = szBefoProgCd;
        	//전전진도코드 = 전진도코드
        	szBefoProgCd = szCurrProgCd;
        	
        	//현재진도코드등록Program
        	szCURR_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_CD_REG_PGM");
        	//전진도코드등록Program
        	szBEFO_PROG_CD_REG_PGM = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_CD_REG_PGM");
        	//전전진도코드등록Program
        	szBEFOBEFO_PROG_CD_REG_PGM = szBEFO_PROG_CD_REG_PGM;
        	szBEFO_PROG_CD_REG_PGM = szCURR_PROG_CD_REG_PGM;
        	szCURR_PROG_CD_REG_PGM = szMethodName;
        	
        	//현재진도등록일시
        	szCURR_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "CURR_PROG_REG_DDTT");
        	//전진도등록일시
        	szBEFO_PROG_REG_DDTT = ydDaoUtils.paraRecChkNull(getRecord, "BEFO_PROG_REG_DDTT");
        	//전전진도등록일시
        	szBEFOBEFO_PROG_REG_DDTT = szBEFO_PROG_REG_DDTT;
        	szBEFO_PROG_REG_DDTT = szCURR_PROG_REG_DDTT;
        	szCURR_PROG_REG_DDTT = YdUtils.getCurDate("yyyyMMddHHmmss");
        	
        	szCurrProgCd = YdConstant.PROG_CD_WRK_WAIT;
        	
        	szMsg = "[" + szOperationName + "] "+szMTL_NM+"재료[" + szStlNo + "] 수정 후 현재진도코드[" + szCurrProgCd + "], 전진도코드[" + szBefoProgCd + "], 전전진도코드[" + szBEFOBEFO_PROG_CD + "]";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
        	
        	//전 진도코드 = 현재 진도코드,   전전진도코드 = 전진도코드
        	//전 진도코드등록일시 = 현재진도코드등록일시 , 전전진도코드등록일시 = 전진도코드등록일시
			//현재시간
        	setRecord.setField("CURR_PROG_CD", 					szCurrProgCd);
        	setRecord.setField("BEFO_PROG_CD", 					szBefoProgCd);
        	setRecord.setField("BEFOBEFO_PROG_CD", 				szBEFOBEFO_PROG_CD);
			setRecord.setField("CURR_PROG_REG_DDTT", 			szCURR_PROG_REG_DDTT);
			setRecord.setField("BEFO_PROG_REG_DDTT", 			szBEFO_PROG_REG_DDTT);
			setRecord.setField("BEFOBEFO_PROG_REG_DDTT", 		szBEFOBEFO_PROG_REG_DDTT);
			setRecord.setField("CURR_PROG_CD_REG_PGM", 			szCURR_PROG_CD_REG_PGM);
			setRecord.setField("BEFO_PROG_CD_REG_PGM", 			szBEFO_PROG_CD_REG_PGM);
			setRecord.setField("BEFOBEFO_PROG_CD_REG_PGM", 		szBEFOBEFO_PROG_CD_REG_PGM);
			setRecord.setField("FNL_REG_PGM", 					szMethodName);
			setRecord.setField("MODIFIER", 					    "YDSYSTEM");
			

        	//업데이트 항목 추가해야함 ///////////////////////////////////////////////////////
			if( szYD_MTL_ITEM.startsWith("P") ) {
        		//후판공통
        		setRecord.setField("PLATE_NO", szStlNo);
        		intRtnVal = ydStockDao.updPtComm_PROG_CD(setRecord, 1);
        		
        	}else if( szYD_MTL_ITEM.startsWith("C") ) {
        		//코일공통
        		setRecord.setField("COIL_NO", szStlNo);
        		intRtnVal = ydStockDao.updPtComm_PROG_CD(setRecord, 3);
        		
        	}
        	
        	if( intRtnVal < 0 ) {
        		szMsg = "[" + szOperationName + "] "+szMTL_NM+"재료[" + szStlNo + "] 수정 시 오류발생 - 반환값 : " + intRtnVal;
                ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
                return YdConstant.RETN_CD_FAILURE;
        	}else if( intRtnVal == 0 ) {
        		szMsg = "[" + szOperationName + "] "+szMTL_NM+"재료[" + szStlNo + "] 수정 시 존재하지 않습니다.";
                ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
                return YdConstant.RETN_CD_NOTEXIST;
        	}
        	
        }catch(Exception e){
        	szMsg = "[" + szOperationName + "] 오류발생 : " + e.getMessage();
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
        }//end of try~catch
        
        return YdConstant.RETN_CD_SUCCESS;
        
    }//end of setProgCodeForPlateOrCoil()
	
	/**
	 * 오퍼레이션명 : 크레인사양과 비교 체크
	 *  
	 * @param  dblCurrWidth, dblMaxWidth, lngSumWt, intMtlSh, recCrnSpec
	 * @return intRtnVal [1 : 성공, -1 : 크레인사양의 집게허용 이상, -2 : 크레인 작업가능 중량 이상, -3 : 크레인 작업가능 매수 이상
	 * @throws JDTOException
	 */
	public static int chkGetCrnspec(double  dblCurrWidth, double dblMaxWidth, long lngSumWt, int intMtlSh, JDTORecord recCrnSpec)throws JDTOException  {

		String szMsg              = null;
		String szMethodName       = "chkGetCrnspec";

		int intRtnVal             = 0;
		
		//크레인 집게폭 오차
		double intCrnTongWTol        = 0;
		//크레인 허용 중량
		long lngWrkAbleWt         = 0;
		//크레인 허용 매수
		int intWrkAbleSh          = 0;
		
		try {
			
			//크레인 집게허용 오차
			intCrnTongWTol = ydDaoUtils.paraRecChkNullDouble(recCrnSpec,  "YD_CRN_TONG_W_TOL");
			//크레인 작업가능 중량
			lngWrkAbleWt   = ydDaoUtils.paraRecChkNullLong(recCrnSpec, "YD_WRK_ABLE_WT");
			//크레인 작업가능 매수
			intWrkAbleSh   = ydDaoUtils.paraRecChkNullInt(recCrnSpec,  "YD_WRK_ABLE_SH");
			
			
			//크레인사양의 집게허용 오차 Check
			if(dblMaxWidth > dblCurrWidth + intCrnTongWTol) {
				
				szMsg = "dblMaxWidth : " + dblMaxWidth + " > dblCurrWidth : " + dblCurrWidth + " intCrnTongWTol : " + intCrnTongWTol;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal = -1;
			}

			//크레인 작업가능 중량 Check
			if(lngWrkAbleWt < lngSumWt) {
				szMsg = "lngWrkAbleWt : " + lngWrkAbleWt + " < lngSumWt : " + lngSumWt;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal = -2;
			}
							
			//크레인 작업가능 매수 Check
			if (intWrkAbleSh < intMtlSh) {
				szMsg = "intWrkAbleSh : " + intWrkAbleSh + " < intMtlSh : " + intMtlSh;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return intRtnVal = -3;
			}
			
			return intRtnVal = 1;
			
		} catch(Exception e) {
			szMsg = "크레인사양과 비교 체크 중 Error :	" + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return intRtnVal = -100;
		}
		
	} //end of Y1ChkGetCrnspec
	
	/**
	 * 오퍼레이션 : 스케줄코드를 반환하는 함수
	 * @param szYD_STK_COL_GP
	 * @param szYD_STK_BED_NO
	 * @param szYD_SCH_WHIO_GP
	 * @return
	 */
	public static String getSchCd(String szYD_STK_COL_GP, String szYD_STK_BED_NO, String szYD_SCH_WHIO_GP) {
		String szMsg = null;
		String szMethodName = "getSchCd";
		String szYD_SCH_CD = "";
		String szYD_SCH_CD_NO = "";
		
		if(!"".equals(szYD_STK_COL_GP) && szYD_STK_COL_GP.length() > 5 && 
				(szYD_STK_COL_GP.startsWith(YdConstant.YD_GP_C_SLAB_YARD) ||   //C연주슬라브야드
				 szYD_STK_COL_GP.startsWith(YdConstant.YD_GP_PORT_SLAB_YARD))  //항만슬라브야드 기능추가 - 2016.01.04 LeeJY
		  ) { 
			szYD_SCH_CD_NO = szYD_STK_COL_GP.substring(5, 6);

			if("A".equals(szYD_SCH_CD_NO)) {
				szYD_SCH_CD_NO = "10";
			} else if("B".equals(szYD_SCH_CD_NO)) {
				szYD_SCH_CD_NO = "11";
			} else if("C".equals(szYD_SCH_CD_NO)) {
				szYD_SCH_CD_NO = "12";
			} else if("D".equals(szYD_SCH_CD_NO)) {
				szYD_SCH_CD_NO = "13";
			} else if("E".equals(szYD_SCH_CD_NO)) {
				szYD_SCH_CD_NO = "14";
			} else if("F".equals(szYD_SCH_CD_NO)) {
				szYD_SCH_CD_NO = "15";
			} else {
				szYD_SCH_CD_NO = "0" + szYD_SCH_CD_NO;
			}
			
			if( szYD_STK_COL_GP.equals(YdConstant.EQP_A_PU2)|| 
				szYD_STK_COL_GP.equals(YdConstant.EQP_A_PU4)||
				szYD_STK_COL_GP.equals(YdConstant.EQP_A_PU6)) {	//장입, 인출 혼용으로 사용함
				if( "01".equals(szYD_STK_BED_NO) || "02".equals(szYD_STK_BED_NO) || "03".equals(szYD_STK_BED_NO)|| "04".equals(szYD_STK_BED_NO) ) {	//LEFT
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 4) + szYD_SCH_CD_NO + szYD_SCH_WHIO_GP + "L";
				} else { //RIGHT
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 4) + szYD_SCH_CD_NO + szYD_SCH_WHIO_GP + "R";
				}
			} else {
				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 4) + szYD_SCH_CD_NO + szYD_SCH_WHIO_GP + "M";
			}
		} else {
			szMsg = "야드적치열구분[" + szYD_STK_COL_GP + "]는 지원하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}

		return szYD_SCH_CD;
	}
	
	/**
	 * 오퍼레이션명 : 해당하는 재료번호에 대하여 크레인 작업재료로 등록되어 있는 지를 확인하는 함수
	 * @param szStlNo
	 * @return
	 * @throws JDTOException
	 */
	public static String chkCrnWrkMtl(String szStlNo) throws JDTOException {
		// 크레인 작업 재료
		YdCrnWrkMtlDao ydCrnWrkMtlDao = new YdCrnWrkMtlDao();
		//리턴메세지 정의
		String szRtnMsg = "";
		// 리턴값(int)
		int intRtnVal = 0;
		// 메소드명
		String szMethodName = "chkCrnWrkMtl";
		//로그메세지
		String szLogMsg = null;
		// 레코드 선언
		JDTORecord recPara = null;
		JDTORecordSet rsResult = null;

		try {

			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			//재료번호
			recPara.setField("STL_NO", szStlNo);

			//크레인스케줄작업재료조회
			intRtnVal = ydCrnWrkMtlDao.getYdCrnwrkmtl(recPara, rsResult, 2);

			// 리턴값 메세지처리
			if (intRtnVal >= 1) {					
				szLogMsg = "재료번호(" + szStlNo + ")의 소재(제품)가 이미 크레인스케줄 작업 재료에 등록되어 있습니다.";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
				szRtnMsg = YdConstant.RETN_CD_EXIST;
			} else if (intRtnVal == 0) {
				szLogMsg = "재료번호(" + szStlNo + ")의 소재(제품)가 크레인스케줄 작업 재료에 등록되어 있지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
				szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
			} else if (intRtnVal == -2) {
				szLogMsg = "재료번호(" + szStlNo + ")에 대한 크레인스케줄 작업 재료 조회중 parameter error 발생.";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
			} else {
				szLogMsg = "재료번호(" + szStlNo + ")에 대한 크레인스케줄 작업 재료 조회중 오류 발생.";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
			}
		} catch (Exception e) {
			szLogMsg = "크레인작업재료 유무 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
		return szRtnMsg;
	} // end of chkCrnWrkMtl
	
	/**
	 * 오퍼레이션명 : 작업예약재료 등록여부 체크
	 * @param szStlNo
	 * @return
	 * @throws JDTOException
	 */
	public static String chkYdWrkBookMtl(String szStlNo)throws JDTOException  {
		
		//작업예약재료 DAO
		YdWrkbookMtlDao ydWrkbookMtlDao  = new YdWrkbookMtlDao();
		
		//메세지
		String szMsg              = null;
		//메소드명
		String szMethodName       = "chkYdWrkBookMtl";
		//리턴값
		String szRtnMsg				= "";
		//리턴값(int)
		int intRtnVal = 0;
		//레코드 선언
		JDTORecord recPara     	  = null;
		//레코드셋 선언
		JDTORecordSet rsResult 	  = null;
			
		try {	
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			
			//재료번호
			recPara.setField("STL_NO", szStlNo);
			
			//재료번호로 작업예약재료 테이블을 읽어온다.
			intRtnVal = ydWrkbookMtlDao.getYdWrkbookmtl(recPara, rsResult, 2);
			
			//리턴값 메세지처리
			if (intRtnVal > 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 이미 등록되어 있습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_EXIST;
				
			} else if (intRtnVal == 0) {
				
				szMsg = "재료번호(" + szStlNo + ")에 대한 작업예약재료 데이터가 등록되어 있지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
				
			} else if (intRtnVal == -2) {
				
				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 parameter error 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				
			} else {
				
				szMsg = "재료번호(" + szStlNo + ")로 작업예약재료 조회중 오류 발생!";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
				
			}
		} catch(Exception e) {
			szMsg = "작업예약재료 등록여부 체크 중 예외발생! 예외메세지: " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(e);
		}
		return szRtnMsg;
		
	} //end of chkYdWrkBookMtl
	
	/**
	 * 오퍼레이션명 : Message정보관리테이블에 기록하는 함수
	 * @param rsParam
	 */
	public static void writeYdMsgInfo(JDTORecordSet rsParam) {
		String szMsg = null;
		String szMethodName = "writeYdMsgInfo";
		if( rsParam == null ) {
			szMsg = "메소드 파라미터가 null입니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}else if( rsParam.size() == 0 ) {
			szMsg = "메소드 파라미터가 rsParam.size() == 0 입니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return;
		}
		try {
			for(int Loop_i = 1; Loop_i <= rsParam.size(); Loop_i++) {
				rsParam.absolute(Loop_i);
				writeYdMsgInfo(rsParam.getRecord());
			}
		}catch(JDTOException ex) {
			szMsg = "[1]Message정보관리테이블에 기록하는 중 예외발생! 예외메세지: " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
	}
	
	/**
	 * 오퍼레이션명 : Message정보관리테이블에 기록하는 함수
	 * @param recParam
	 */
	public static void writeYdMsgInfo(JDTORecord recParam) {
		String szMsg = null;
		String szMethodName = "writeYdMsgInfo";
		try {
			ydMsgInfoMgtDao.updYdMsginfomgt(recParam, 0);
		}catch(JDTOException ex) {
			szMsg = "[2]Message정보관리테이블에 기록하는 중 예외발생! 예외메세지: " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
	}
	
	/**
	 * 야드구분에 따른 개소코드를 반환하는 메소드
	 * @param szYD_GP
	 * @return
	 */
	public static String getWlocCd(String szYD_GP) {
		String szWLOC_CD = "";
		//C연주
    	if(szYD_GP.equals("A")) {
    		szWLOC_CD = "DHY21";
    		
    	//항만슬라브야드 기능추가 - 2016.01.04 LeeJY
    	}else if(szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)) { 
    		szWLOC_CD = YdConstant.WLOC_CD_PORT_SLAB_YARD;
    		
        	//A후판슬라브	
    	}else if(szYD_GP.equals("D")) {
    		szWLOC_CD = "DKY21";
    		
    	//후판제품창고	
    	}else if(szYD_GP.equals("K")) {
    		szWLOC_CD = "DKY30";
    	
        //2후판제품창고	
    	}else if(szYD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD)) {
    		szWLOC_CD = YdConstant.WLOC_CD_PLATE2_GDS_YARD; //--2013.03.16 추가 (3기)
    	
    	//통합야드
    	}else if(szYD_GP.equals("S")) {
    		szWLOC_CD = "DJY25";
    	
    	//A열연COIL야드
    	}else if(szYD_GP.equals("1")) {
    		szWLOC_CD = "D2Y45";
    	
    	//B열연SLAB야드	
    	}else if(szYD_GP.equals("2")) {
    		szWLOC_CD = "D3Y43";
    	
    	//B열연 COIL야드	
    	}else if(szYD_GP.equals("3")) {
    		szWLOC_CD = "D3Y42";
    	
    	//A열연 SLAB야드	
    	}else if(szYD_GP.equals("0")) {
    		szWLOC_CD = "D2Y43";
    		
    	}else if(szYD_GP.equals("2")) {
    		szWLOC_CD = "D3Y43";
    		
    	}
    	
    	return szWLOC_CD;
	}
	
	/**
	 * 야드구분에 따른 개소코드를 반환하는 메소드
	 * @param szYD_GP
	 * @return
	 */
	public static String getWlocCd2(String szTRN_EQP_CD) {
		String szWLOC_CD = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		//TB_TS_TRN_EQP_CUR_STAT 구내운송으로 정보를 가져 옴
		String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.tsArrwlocCd";
	    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{szTRN_EQP_CD});
	    
	    if(sposYNChklist.size()>0){
		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
		    szWLOC_CD   = StringHelper.evl(unloadPointrec.getFieldString("ARR_WLOC_CD"), "");
	    }
    	
    	return szWLOC_CD;
	}
	
	/**
	 * 재룔번호에 따른 개소코드를 반환하는 메소드
	 * @param sStlNo
	 * @return
	 */
	public static String getWlocCd3(String sStlNo) {
		String szWLOC_CD = "";
		ymCommonDAO dao = ymCommonDAO.getInstance();
		//sStlNo 구내운송으로 정보를 가져 옴
		String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.pmArrwlocCd";
	    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{sStlNo});
	    
	    if(sposYNChklist.size()>0){
		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
		    szWLOC_CD   = StringHelper.evl(unloadPointrec.getFieldString("ARR_WLOC_CD"), "");
	    }
    	
    	return szWLOC_CD;
	}
	
	
	/**
	 * 관리되는 개소코드를 야드구분으로 변환하는 메소드
	 * @param szWLOC_CD
	 * @return
	 */
	public static String getYdFromWlocCd(String szWLOC_CD) {
		String szYD_GP = "";
		if(szWLOC_CD.equals("DHY21") || szWLOC_CD.equals("DHY22") || szWLOC_CD.equals("DVY19")) {				//C연주슬라브
			szYD_GP = YdConstant.YD_GP_C_SLAB_YARD;
		}else if(szWLOC_CD.equals(YdConstant.WLOC_CD_PORT_SLAB_YARD)) {			//항만슬라브야드 기능추가 - 2016.01.04 LeeJY
			szYD_GP = YdConstant.YD_GP_PORT_SLAB_YARD;
		}else if(szWLOC_CD.equals("DJY21") || szWLOC_CD.equals("DJY22")) {			//C열연소재
			szYD_GP = YdConstant.YD_GP_C_HR_COIL_MATL_YARD;
		}else if(szWLOC_CD.equals("DJY1E")){ 
		//C열연 코일제품창고
			szYD_GP = YdConstant.YD_GP_C_HR_COIL_GDS_YARD;
    	}else if(szWLOC_CD.equals("DKY21") || szWLOC_CD.equals("DWY22")) {			//1, 2후판 소재
    		szYD_GP = YdConstant.YD_GP_A_PLATE_SLAB_YARD;
    	}else if( YdConstant.WLOC_CD_PLATE_GDS_YARD.equals(szWLOC_CD) ) {			//1후판 제품창고
    		szYD_GP = YdConstant.YD_GP_PLATE_GDS_YARD;
    	}else if( YdConstant.WLOC_CD_PLATE2_GDS_YARD.equals(szWLOC_CD) ) {			//2후판 제품창고 - 2012.12.18 추가 (3기)
    		szYD_GP = YdConstant.YD_GP_PLATE2_GDS_YARD;
    	}else if("DJY25".equals(szWLOC_CD)||"DYY15".equals(szWLOC_CD)||"BSY01".equals(szWLOC_CD)||"BSY02".equals(szWLOC_CD)||"BSY03".equals(szWLOC_CD)) { //통합야드 소재(비상야드추가)
    		szYD_GP = YdConstant.YD_GP_INTGR_YARD;
    	}else if( YdConstant.WLOC_CD_A_PLATE_PLANT.equals(szWLOC_CD)||
    			  YdConstant.WLOC_CD_B_PLATE_PLANT.equals(szWLOC_CD)){
    		szYD_GP = YdConstant.YD_GP_A_PLATE_PLANT;								//A후판조업
    	}else if( YdConstant.WLOC_CD_C_HR_PLANT.equals(szWLOC_CD) ) {
    		szYD_GP = YdConstant.YD_GP_C_HR_PLANT;									//C열연조업
		}else if( YdConstant.WLOC_CD_B_HR_PLANT.equals(szWLOC_CD) ) {
			// 2009.10.19 권오창
			szYD_GP = YdConstant.YD_GP_B_HR_SLAB_YARD;								//B열연
		}else if( YdConstant.WLOC_CD_A_HR_PLANT.equals(szWLOC_CD) ) {
			// 2009.10.19 권오창
			szYD_GP = YdConstant.YD_GP_A_HR_SLAB_YARD;								//A열연
		}else if( YdConstant.WLOC_CD_B_PLATE_PLANT.equals(szWLOC_CD) ) { 
			szYD_GP = YdConstant.YD_GP_PLATE2_JJ_YARD;								//2후판정정야드
		}
	 
		return szYD_GP;
	}
	
	/**
	 * 대상재를 같은 목표야드와 목표행선으로만 그룹핑
	 * @param rsIn
	 * @param rsOut
	 * @return
	 */
	public static String filterStockStl(JDTORecordSet rsIn, JDTORecordSet rsOut) {
		String szMethodName = "filterStockStl";
		String szMsg = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szPREV_YD_AIM_YD_GP = "";
		String szPREV_YD_AIM_RT_GP = "";
		String szYD_AIM_YD_GP = "";
		String szYD_AIM_RT_GP = "";
		JDTORecord recTemp	= null;
		int cnt = rsIn.size();
		try {
			for(int i = 1; i <= cnt; i++ ) {
				rsIn.absolute(i);
				recTemp = rsIn.getRecord();
				szYD_AIM_YD_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_AIM_YD_GP");
				szYD_AIM_RT_GP = ydDaoUtils.paraRecChkNull(recTemp, "YD_AIM_RT_GP");
				
				if( i == 1 ) {
					szPREV_YD_AIM_YD_GP = szYD_AIM_YD_GP;
					szPREV_YD_AIM_RT_GP = szYD_AIM_RT_GP;
					rsOut.addRecord(recTemp);
					continue;
				}
				
				if( szPREV_YD_AIM_YD_GP.equals(szYD_AIM_YD_GP) && szPREV_YD_AIM_RT_GP.equals(szYD_AIM_RT_GP)  ) {
					rsOut.addRecord(recTemp);
				}else{
					break;
				}
			}
		}catch(JDTOException ex) {
			szMsg = "대상재를 같은 목표야드와 목표행선으로만 그룹핑 시 예외메세지: " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	}
	
	
	/**
	 * 각 야드별 저장위치제원 전송 공통 메소드
	 * @param recInParam
	 * @return 
	 */
	public static String sndStrPosSpecToL2(JDTORecord recInParam) {
		String szOperationName			= "야드저장위치제원 전송";
		String szMethodName 			= "sndStrPosSpecToL2";
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String szLogMsg 				= null;
		//TC CODE
		String szTC_CODE 				= null;
		//야드구분
		String szYD_GP 					= null;
		//야드정보동기화코드 - 1:동,2:SPAN,3:열,4:BED
		String szYD_INFO_SYNC_CD     	= null;
		//야드적치열구분
		String szYD_STK_COL_GP       	= null;
		//야드적치베드번호
		String szYD_STK_BED_NO       	= null;
		//차량진행상태
		String szYD_CAR_PROG_STAT		= null;
		//야드설비작업상태
		String szYD_EQP_WRK_STAT		= null;
		// 차량 번호
		String szCAR_NO                = "";
		//1. 파라미터 값을 가져온다.
		try {
			szYD_GP 			= ydDaoUtils.paraRecChkNull(recInParam,"YD_GP");
			szYD_INFO_SYNC_CD 	= ydDaoUtils.paraRecChkNull(recInParam,"YD_INFO_SYNC_CD");
			szYD_STK_COL_GP 	= ydDaoUtils.paraRecChkNull(recInParam,"YD_STK_COL_GP");
			szYD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recInParam,"YD_STK_BED_NO");
			szYD_CAR_PROG_STAT 	= ydDaoUtils.paraRecChkNull(recInParam,"YD_CAR_PROG_STAT");
			szYD_EQP_WRK_STAT 	= ydDaoUtils.paraRecChkNull(recInParam,"YD_EQP_WRK_STAT");
			szCAR_NO			= ydDaoUtils.paraRecChkNull(recInParam,"CAR_NO");
			
			if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) ) {				//C연주슬라브야드
				szTC_CODE = "YDY1L001";
			}else if( szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD) ) {	//항만슬라브야드 기능추가 - 2016.01.04 LeeJY
				szTC_CODE = "YDE7L001";
			}else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {	//A후판슬라브야드
				szTC_CODE = "YDY3L001";
			}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD) ) {	//C열연코일소재야드
				szTC_CODE = "YDY5L001";
			}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD) ) {	//C열연코일제품야드
				szTC_CODE = "YDY5L001";
			}else if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD) ) {		//후판제품창고야드
				szTC_CODE = "YDY4L001";
			}else if( szYD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD) ) {		//2후판제품창고야드 -- 2012.12.17 추가 (3기)
				szTC_CODE = "YDY8L001";

			}else{
				szLogMsg = "["+szOperationName+" - sndStrPosSpecToL2]지원하지 않는 야드구분[" + szYD_GP + "], 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "], 야드적치열구분[" + szYD_STK_COL_GP + "], 야드적치베드번호[" + szYD_STK_BED_NO + "] ";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_FAILURE;
			}
			
			recInParam.setField("MSG_ID", szTC_CODE);
			
			if("YDY8L001".equals(szTC_CODE)){
				if(!"".equals(szYD_STK_COL_GP)){
					try{
						if(szYD_STK_COL_GP.length() > 4 && "PT".equals(szYD_STK_COL_GP.substring(2,4))){
							recInParam.setField("MSG_ID", "YDY9L001");
							ydDelegate.sendMsg(recInParam);
							recInParam.setField("MSG_ID", "YDY8L001");
						}
						else{
							//2021. 1. 6 추가(Y9시스템 전송여부)
							if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szYD_STK_COL_GP)){
								recInParam.setField("MSG_ID", "YDY9L001");
							}
						}
					}catch(Exception e){ydUtils.putLog(szClassName, szMethodName, "야드저장위치제원 Y9판단 중 오류 발생하였으나 SKIP:: "+e.getMessage(), YdConstant.ERROR);}
				}
			}
			
//			if(szTC_CODE.equals("YDY5L001") && szYD_STK_COL_GP.substring(0, 1).equals("J")){
//			//포인트점유사항 출하송신
//				JDTORecord Paramrecord = JDTORecordFactory.getInstance().create(); 
//				Paramrecord.setField("MSG_ID"		, 	"YDDMR026");
//				Paramrecord.setField("YD_STK_COL_GP",	szYD_STK_COL_GP);
//				ydDelegate.sendMsg(Paramrecord);
//			}
			
			szLogMsg = "["+szOperationName+" - sndStrPosSpecToL2]야드구분[" + szYD_GP + "], 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "], 야드적치열구분[" + szYD_STK_COL_GP + "], 야드적치베드번호[" + szYD_STK_BED_NO + "] ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			szLogMsg = "["+szOperationName+" - sndStrPosSpecToL2]차량진행상태[" + szYD_CAR_PROG_STAT + "], 야드설비작업상태[" + szYD_EQP_WRK_STAT + "]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ydDelegate.sendMsg(recInParam);
			
			szLogMsg = "["+szOperationName+" - sndStrPosSpecToL2]야드구분[" + szYD_GP + "], 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "], 야드적치열구분[" + szYD_STK_COL_GP + "], 야드적치베드번호[" + szYD_STK_BED_NO + "] ";
			szLogMsg += "===> 전송 완료";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}catch(JDTOException e) {
			szLogMsg= szMethodName+" Error:" +e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			//throw new DAOException(YdConstant.RETN_CD_FAILURE);
		}catch(Exception e) {
			szLogMsg= szMethodName+" Error:" +e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			//throw new DAOException(YdConstant.RETN_CD_FAILURE);
		}
		return szRtnMsg;
	}
	
	
	/**
	 * 각 야드별 저장품제원 전송 공통 메소드
	 * @param recInParam
	 * @return 
	 */
	public static String sndStockSpecToL2(JDTORecord recInParam) {
		String szOperationName			= "야드저장품제원 전송";
		String szMethodName = "sndStockSpecToL2";
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szLogMsg = null;
		//TC CODE
		String szTC_CODE = null;
		//야드구분
		String szYD_GP = null;
		//야드정보동기화코드 - 1:동,2:SPAN,3:열,4:BED
		String szYD_INFO_SYNC_CD     = null;
		//야드적치열구분
		String szYD_STK_COL_GP       = null;
		//야드적치베드번호
		String szYD_STK_BED_NO       = null;
		//저장품
		String szSTL_NO	= null;
		
		JDTORecord recTemp = null;
		
		//1. 파라미터 값을 가져온다.
		try {
			recTemp = JDTORecordFactory.getInstance().create();
			
			//szYD_GP = ydDaoUtils.paraRecChkNull(recInParam,"YD_GP");
			szYD_INFO_SYNC_CD = ydDaoUtils.paraRecChkNull(recInParam,"YD_INFO_SYNC_CD");
			szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recInParam,"YD_STK_COL_GP");
			szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recInParam,"YD_STK_BED_NO");
			szSTL_NO = ydDaoUtils.paraRecChkNull(recInParam,"STL_NO");
			
			// 2021. 10. 20
			// szYD_STK_COL_GP 값이 없을경우 오류 방지
			if(!"".equals(szYD_STK_COL_GP)){
				szYD_GP = szYD_STK_COL_GP.substring(0, 1);	
			}
			
			if(szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) ) {				    // C연주슬라브야드
				szTC_CODE = "YDY1L002";
			}else if( szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD) ) {	//항만슬라브야드 기능추가 - 2016.01.04 LeeJY
				szTC_CODE = "YDE7L002";
			}else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {	// A후판슬라브야드
				szTC_CODE = "YDY3L002";
			}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD) ) {	// C열연코일소재야드
				szTC_CODE = "YDY5L002";
			}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD) ) {	// C열연코일제품야드
				szTC_CODE = "YDY5L002";
			}else if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD) ) {		// 후판제품창고야드
				szTC_CODE = "YDY4L002";
			}else if( szYD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD) ) {		// 2후판제품창고야드 - 2012.12.21 추가 (3기)
				szTC_CODE = "YDY8L002";
				//2021. 1. 6 추가(Y9시스템 전송여부)
				if(!"".equals(szSTL_NO)){
					if(PlateGdsYdUtil.isSendToEaiY9_stlNo(szSTL_NO)){
						szTC_CODE = "YDY9L002";
					}
				}
				else{
					if(PlateGdsYdUtil.isSendToEaiY9_ydStkColGp(szYD_STK_COL_GP)){
						szTC_CODE = "YDY9L002";
					}
				}
			}else{
				szLogMsg = "["+szOperationName+"]지원하지 않는 야드구분[" + szYD_GP + "], 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "], 야드적치열구분[" + szYD_STK_COL_GP + "], 야드적치베드번호[" + szYD_STK_BED_NO + "], 재료번호["+szSTL_NO+"] ";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				return YdConstant.RETN_CD_FAILURE;
			}
			
			recTemp.setField("MSG_ID", szTC_CODE);
			recTemp.setRecord(recInParam);
			
			szLogMsg = "["+szOperationName+"]야드구분[" + szYD_GP + "], 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "], 야드적치열구분[" + szYD_STK_COL_GP + "], 야드적치베드번호[" + szYD_STK_BED_NO + "], 재료번호["+szSTL_NO+"] ";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, recTemp);
			
			ydDelegate.sendMsg(recTemp);
			
			szLogMsg = "["+szOperationName+"]야드구분[" + szYD_GP + "], 야드정보동기화코드[" + szYD_INFO_SYNC_CD + "], 야드적치열구분[" + szYD_STK_COL_GP + "], 야드적치베드번호[" + szYD_STK_BED_NO + "], 재료번호["+szSTL_NO+"] ";
			szLogMsg += "===> 전송 완료";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
		}catch(JDTOException e) {
			szLogMsg= szMethodName+" Error:" +e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			//throw new DAOException(YdConstant.RETN_CD_FAILURE);
		}catch(Exception e) {
			szLogMsg= szMethodName+" Error:" +e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			//throw new DAOException(YdConstant.RETN_CD_FAILURE);
		}
		return szRtnMsg;
	}
	
	
	/**
	 *	AB열연 야드 개소코드 체크 
	 */
	public static boolean getABLocationInfo(String sLocCd){
		
		boolean isReturn = false;
		
		if("D2Y43".equals(sLocCd)){//A연주-B Cast Slab Yard 
			isReturn = true;
		}else if("D2Y44".equals(sLocCd)){//A열연-#1 제품/소재 Coil Yard
			isReturn = true;
		}else if("D2Y45".equals(sLocCd)){//A열연-#2 제품/소재 Coil Yard
			isReturn = true;
		}else if("D3Y41".equals(sLocCd)){//B열연-#1 제품/소재 Coil Yard
			isReturn = true;
		}else if("D3Y42".equals(sLocCd)){//B열연-#2 제품/소재 Coil Yard
			isReturn = true;
		}else if("D3Y43".equals(sLocCd)){//B열연-Slab Yard
			isReturn = true;
		}else if("D3Y44".equals(sLocCd)){//B열연-가열로 Slab Yard 
			isReturn = true;	
		}
		      
		return isReturn;
	}
	
	/**
	 * 차량정지위치활성/비활성처리
	 * @param recInParam
	 * @return
	 */
	public static String procCarPosActiveOrInActive(JDTORecord recInParam) {
		String szMethodName = "procCarPosActiveOrInActive";
		String szOperationName = "차량정지위치활성/비활성처리";
		int intRtnVal = -100;
		String szMsg = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_STK_BED_ACT_STAT = null;					//야드적치베드활성상태
		String szYD_STK_LYR_ACT_STAT = null;					//야드적치단활성상태
		String szYD_STK_LYR_MTL_STAT = null;					//야드적치단재료상태
		
		JDTORecord recInTemp = null;
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		YdPICommDAO ydPICommDAO = new YdPICommDAO();
		
		/*
		 * 파라미터 확인
		 */
		String szYD_STK_COL_GP = StringHelper.evl(recInParam.getFieldString("YD_STK_COL_GP"), "");
		String szYD_CAR_USE_GP = StringHelper.evl(recInParam.getFieldString("YD_CAR_USE_GP"), "");
		String szTRN_EQP_CD = StringHelper.evl(recInParam.getFieldString("TRN_EQP_CD"), "");
		String szCAR_NO = StringHelper.evl(recInParam.getFieldString("CAR_NO"), "");
		String szCARD_NO = StringHelper.evl(recInParam.getFieldString("CARD_NO"), "");
		String szTRN_EQP_STK_CAPA = StringHelper.evl(recInParam.getFieldString("TRN_EQP_STK_CAPA"), "");
		String szYD_STK_COL_ACT_STAT  = StringHelper.evl(recInParam.getFieldString("YD_STK_COL_ACT_STAT"), "");
		/*
		 * 값 검증
		 */
		if( szYD_STK_COL_GP.equals("") ) {
			szMsg="[" + szOperationName + "] 적치열이 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NO_PARAM;
		}

//PIDEV		
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APPPI0",szYD_STK_COL_GP.substring(0,1), "*");
//		if("PIDEV".equals("PIDEV")) {
			String szRtnCd = YdCommonUtils.procCarPosActiveOrInActive_PIDEV(recInParam);
			return szRtnCd;
//		} 
		
//		
//		if( szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) ) {			//구내운송
//			if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_ACTIVE) && szTRN_EQP_CD.equals("") ) {
//				szMsg="[" + szOperationName + "] 차량사용구분["+szYD_CAR_USE_GP+"]이므로 운송장비코드가 존재해야합니다.";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_FAILURE;
//			}
//		}else if( szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) ) {	//출하차량
//			if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_ACTIVE) ) {
//				if( szCAR_NO.equals("") ) {
//					szMsg="[" + szOperationName + "] 차량사용구분["+szYD_CAR_USE_GP+"]이므로 차량번호가 존재해야합니다.";
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_FAILURE;
//				}
//				if( szCARD_NO.equals("") ) {
//					szMsg="[" + szOperationName + "] 차량사용구분["+szYD_CAR_USE_GP+"]이므로 카드번호가 존재해야합니다.";
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_FAILURE;
//				}
//			}
//		}
//
//		
//		if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_ACTIVE) ) {				//활성화
//			szYD_STK_BED_ACT_STAT 		= YdConstant.YD_STK_BED_ACTIVE;
//			szYD_STK_LYR_ACT_STAT 		= YdConstant.YD_STK_LYR_ACTIVE;
//		}else if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {		//비활성화
//			szYD_STK_BED_ACT_STAT 		= YdConstant.YD_STK_BED_INACTIVE;
//			szYD_STK_LYR_ACT_STAT 		= YdConstant.YD_STK_LYR_INACTIVE;
//			szTRN_EQP_STK_CAPA 			= YdConstant.YD_STK_BED_WT_MAX_DEFAULT;
//			szYD_CAR_USE_GP				= "";
//		}else{
//			szMsg="[" + szOperationName + "] 사용가능값[활성화"+YdConstant.YD_STK_COL_ACTIVE+":, 비활성화:"+YdConstant.YD_STK_COL_INACTIVE+"] - 사용할 수 없는 값["+szYD_STK_COL_ACT_STAT+"]입니다.";
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_FAILURE;
//		}
//		
//		try {
//			/*
//			 * 적치열 활성/비활성 처리
//			 */
//	    	recInTemp = JDTORecordFactory.getInstance().create();
//	    	recInTemp.setField("YD_STK_COL_GP",        	szYD_STK_COL_GP);
//	    	recInTemp.setField("YD_CAR_USE_GP",        	szYD_CAR_USE_GP);
//	    	recInTemp.setField("TRN_EQP_CD",           	szTRN_EQP_CD);
//	    	recInTemp.setField("CAR_NO",           		szCAR_NO);
//	    	recInTemp.setField("CARD_NO",           	szCARD_NO);
//	    	recInTemp.setField("YD_STK_COL_ACT_STAT",   szYD_STK_COL_ACT_STAT);
//	    	intRtnVal = ydStkColDao.updYdStkcol(recInTemp, 0);
//			if(intRtnVal <= 0) {
//				if(intRtnVal == 0) {
//					szMsg="[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]수정 시 존재하지 않습니다.";
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				}else if(intRtnVal == -1) {
//					szMsg="[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]수정 시 중복되었습니다.";
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
//				}else if(intRtnVal == -2) {
//					szMsg="[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]수정 시 parameter error";
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				}else if(intRtnVal == -3){
//					szMsg="[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]수정 시 execution failed";
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				}
//				return YdConstant.RETN_CD_FAILURE;
//			}
//			
//			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
//			YdStockDAO ydStockDAO = new YdStockDAO();
//			ymCommonDAO dao = ymCommonDAO.getInstance();
//			
//			//장애 발생시 이전 소스로 원복 하기 위한 조치
//			String QueryId 	= "com.inisteel.cim.yd.dao.ydstkcoldao.YdStockDao.CarPointinforegchklist";
//		    List sposYNChklist = dao.getCommonList(QueryId, new Object[]{});
//
//		    JDTORecord unloadPointrec = (JDTORecord)sposYNChklist.get(0);
//	    	String CHK   = StringHelper.evl(unloadPointrec.getFieldString("CHK"), "");
//	    	if(CHK.equals("Y")){	    		
//	    		
//	    		szMsg =  "▣▣▣▣차량포인트 통합관리(START):통합작업 시작 ▣▣▣▣▣";
//	    		ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.INFO);
//	    		//저장위치로 차량 포인트 예약 하는 경우(출하)
//				String stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdateC2";
//				ydStockDAO.requestupdateData(stkQueryId, new Object[]{ szYD_STK_COL_ACT_STAT,szCAR_NO ,szCARD_NO,szYD_STK_COL_GP});
//		 
//	    	}
//			
//			/*
//			 * 적치베드 상태 활성/비활성 처리
//			 */
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("YD_STK_BED_WT_MAX", szTRN_EQP_STK_CAPA);
//			recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recInTemp.setField("YD_STK_BED_ACT_STAT", szYD_STK_BED_ACT_STAT);
//			intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 0);
//			if(intRtnVal <= 0) {
//				szMsg = "[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]의 적치베드를 수정 시 오류발생 - 반환값 : " + intRtnVal;
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_FAILURE;
//			}
//			
//			
//			/*
//			 * 적치단 활성/비활성 처리
//			 */
//			recInTemp = JDTORecordFactory.getInstance().create();
//			recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
//			recInTemp.setField("YD_STK_LYR_ACT_STAT", szYD_STK_LYR_ACT_STAT);
//			recInTemp.setField("STL_NO", "");
//			recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
//	    	
//			intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
//			if(intRtnVal <= 0) {
//				szMsg = "[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]의 적치단을 수정 시 오류발생 - 반환값 : " + intRtnVal;
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				return YdConstant.RETN_CD_FAILURE;
//			}
//		}catch(JDTOException ex) {
//			szMsg = "[" + szOperationName + "] 오류발생 - 메세지 : " + ex.getMessage();
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			return YdConstant.RETN_CD_FAILURE;
//		}
//		return szRtnMsg;
	}
	
	/**
	 * 각 야드별 크레인스케줄MAIN 호출 TC CODE 반환하는 메소드
	 * @param szYD_GP
	 * @return
	 */
	public static String[] getCrnSchTCByYD(String szYD_GP) {
		String[] retVal = new String[2];
		if(szYD_GP.equals("A") ){
			//JMS TC CODE (C연주)
			retVal[0] = "YDYDJ500";
			retVal[1] = "procY1CrnSchMain";
		}else if(szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)){  //항만슬라브야드 기능추가 - 2016.01.04 LeeJY
			//JMS TC CODE (항만슬라브야드)
			retVal[0] = "YDYDJ500";
			retVal[1] = "procY1CrnSchMain";
		}else if(szYD_GP.equals("D")){
			//JMS TC CODE (A후판슬라브)
			retVal[0] = "YDYDJ503";
			retVal[1] = "procY3CrnSchMain";
		}else if( YdConstant.YD_GP_PLATE_GDS_YARD.equals(szYD_GP) || YdConstant.YD_GP_PLATE2_GDS_YARD.equals(szYD_GP) ){ //- 2012.12.18 수정 (3기)
			//JMS TC CODE (후판제품)
			retVal[0] = "YDYDJ506";
			retVal[1] = "procY4CrnSchMain";
		}else if(szYD_GP.equals("H") || szYD_GP.equals("J")){
			//JMS TC CODE (C열연코일)
			retVal[0] = "YDYDJ509";
			retVal[1] = "procY5CrnSchMain";
		}else if( szYD_GP.equals("S")){
			//JMS TC CODE (통합)
			retVal[0] = "YDYDJ512";
			retVal[1] = "procY0CrnSchMain";
		}else if( YdConstant.YD_GP_PLATE_JJ_YARD.equals(szYD_GP)){
			//JMS TC CODE 1후판정정야드
			retVal[0] = "YDYDJ512";
			retVal[1] = "procY0CrnSchMain";
		}else{
			retVal[0] = "";
			retVal[1] = "";
		}
		return retVal;
	}
	
	/**
	 * 사용가능한차량정지위치조회
	 * @param szYD_GP
	 * @param szYD_BAY_GP
	 * @param recOutPara
	 * @return
	 * @throws JDTOException
	 */
	public static String getUsableCarStopLoc(String szYD_GP, String szYD_BAY_GP, JDTORecord recOutPara) throws JDTOException {
		//DAO선언
		YdStkColDao ydStkColDao 		= new YdStkColDao();
		//기본변수선언
		String szMethodName 			= "getUsableCarStopLoc";
		String szOperationName 			= "사용가능한차량정지위치조회";
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		int intRtnVal					= -100;
		String szMsg					= null;
		//로컬변수선언
		String szYD_STK_COL_GP			= null;
		String szWLOC_CD				= null;
		String szYD_PNT_CD				= null;
		String szYD_STK_COL_ACT_STAT	= null;
		
		String szYD_CARLD_STOP_LOC		= "";
		String szRETN_YD_PNT_CD			= "";
		String szRETN_WLOC_CD			= "";
		
		boolean isUsable				= false;
		
		JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recPara = JDTORecordFactory.getInstance().create();
		
		recPara.setField("YD_STK_COL_GP", szYD_GP + szYD_BAY_GP + "PT");
		/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcolColGpLike*/
    	intRtnVal = ydStkColDao.getYdStkcol(recPara, rsResult, 8);
		
    	if(intRtnVal <= 0) {
			if(intRtnVal == 0) {
				szMsg="["+szOperationName+"] 차량정지위치를 조회 시 data not found";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}else if(intRtnVal == -2 ) {
				szMsg="["+szOperationName+"] 차량정지위치를 조회 시 parameter error";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}else{
				szMsg="["+szOperationName+"] 차량정지위치를 조회 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}
			return YdConstant.RETN_CD_FAILURE;
		}
		
		szMsg="["+szOperationName+"] 차량이 입동가능한 차량정지위치를 조회 성공 - 건수["+intRtnVal+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		szMsg="["+szOperationName+"] 조회된 차량정지위치에 대한 사용가능 체크 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		for( int i = 1; i <= rsResult.size(); i++ ) {
			rsResult.absolute(i);
			recPara = rsResult.getRecord();
			szYD_STK_COL_GP = StringHelper.evl(recPara.getFieldString("YD_STK_COL_GP"), "");
			szWLOC_CD = StringHelper.evl(recPara.getFieldString("WLOC_CD"), "");
			szYD_PNT_CD = StringHelper.evl(recPara.getFieldString("YD_PNT_CD"), "");
			szYD_STK_COL_ACT_STAT = StringHelper.evl(recPara.getFieldString("YD_STK_COL_ACT_STAT"), "");
			szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]에 대한 야드적치열활성상태["+szYD_STK_COL_ACT_STAT+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//C
			
			if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {  
				szMsg="["+szOperationName+"] 조회된 차량정지위치["+szYD_STK_COL_GP+"]가 사용가능";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				szYD_CARLD_STOP_LOC = szYD_STK_COL_GP;
				szRETN_YD_PNT_CD = szYD_PNT_CD;
				szRETN_WLOC_CD = szWLOC_CD;
				isUsable = true;
				break;
			}
		}
		
		if( isUsable ) {
			szMsg="["+szOperationName+"] 사용가능한 차량정지위치["+szYD_CARLD_STOP_LOC+", "+szRETN_WLOC_CD+", "+szRETN_YD_PNT_CD+"]가 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}else{
			szMsg="["+szOperationName+"] 사용가능한 차량정지위치가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		recOutPara.setField("YD_CARLD_STOP_LOC", szYD_CARLD_STOP_LOC);
		recOutPara.setField("WLOC_CD", szRETN_WLOC_CD);
		recOutPara.setField("YD_PNT_CD", szRETN_YD_PNT_CD);
		
		return szRtnMsg;
	}
	
	/**
	 * 이송대상재가 하차완료시 해당조건에 따른 진도코드를 반환하는 메소드
	 * @param szPT_TB_COMM
	 * @param szSLAB_WO_RT_CD
	 * @param szORD_YEOJAE_GP
	 * @param szSCARFING_YN
	 * @param szSCARFING_DONE_YN
	 * @param szMILL_WO_EXN
	 * @return
	 */
	public static String getCurrProgCd(String szPT_TB_COMM, 
										String szSLAB_WO_RT_CD,
										String szORD_YEOJAE_GP,
										String szSCARFING_YN,
										String szSCARFING_DONE_YN,
										String szMILL_WO_EXN) {
		
		return getCurrProgCd(	 szPT_TB_COMM, 
								 szSLAB_WO_RT_CD,
								 szORD_YEOJAE_GP,
								 szSCARFING_YN,
								 szSCARFING_DONE_YN,
								 szMILL_WO_EXN,
								 "","");
		
	}
	public static String getCurrProgCd(String szPT_TB_COMM, 
										String szSLAB_WO_RT_CD,
										String szORD_YEOJAE_GP,
										String szSCARFING_YN,
										String szSCARFING_DONE_YN,
										String szMILL_WO_EXN,
										String sYdGp) {

	return getCurrProgCd(	 szPT_TB_COMM, 
		 szSLAB_WO_RT_CD,
		 szORD_YEOJAE_GP,
		 szSCARFING_YN,
		 szSCARFING_DONE_YN,
		 szMILL_WO_EXN,
		 sYdGp,
		 "");
	
	}
	public static String getCurrProgCd(String szPT_TB_COMM, 
										String szSLAB_WO_RT_CD,
										String szORD_YEOJAE_GP,
										String szSCARFING_YN,
										String szSCARFING_DONE_YN,
										String szMILL_WO_EXN,
										String sYdGp,
										String szSTL_APPEAR_GP) {
		/*
		 * 업무기준 : 1. 주편이면 진도코드를 Slab정정작업대기[A]로 반환
		 * 			 2. 슬라브이면
		 * 				2-1. 슬라브지시행선이 판매Slab이면
		 * 					2-1-1. 주문재이면 진도코드를 출하지시대기[K]로 반환
		 * 					2-1-2. 여재이면 진도코드를 제품충당대기[Z]로 반환
		 * 				2-2. 슬라브지시행선이 판매Slab가 아니면
		 * 					2-2-1. 주문재이면
		 * 						2-2-1-1. 압연지시여부가 Y이면 진도코드를 작업대기[C]로 반환
		 * 						2-2-1-2. 압연지시여부가 Y가 아니면
		 * 							2-2-1-2-1. 스카핑여부가 Y이고 스카핑완료여부가 N이면 진도코드를 Slab정정작업대기[A]로 반환
		 * 							2-2-1-2-2. 그외는 진도코드를 지시대기[B]로 반환
		 * 					2-2-2. 여재이면
		 * 						2-2-2-1. 스카핑여부가 Y이고 스카핑완료여부가 N이면 진도코드를 Slab정정작업대기[A]로 반환
		 * 						2-2-2-2. 그외는 진도코드를 재공충당대기[Y]로 반환
		 */
		String szOperationName 		= "재료진도결정";
		String szMethodName			= "getCurrProgCd";
		String szMsg				= null;
		String szCurrProgCd = "";
		
		szMsg = "["+szOperationName+"] 메소드 시작 - 주편/슬라브구분["+szPT_TB_COMM+"], 슬라브지시행선코드["+szSLAB_WO_RT_CD+"], 주여구분["+szORD_YEOJAE_GP+"], 스카핑여부["+szSCARFING_YN+"], 스카핑완료여부["+szSCARFING_DONE_YN+"], 압연지시구분["+szMILL_WO_EXN+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		if( szPT_TB_COMM.equals("B") ) {		/* 주편이면 */
    		szCurrProgCd = "A";																//Slab정정작업대기
    	}else{									/* 슬라브이면 */
    		if( szSLAB_WO_RT_CD.equals("MS") ) {
    			
    			if(szSTL_APPEAR_GP.equals("Y"))  /* 제품이면*/
    			       {
		        			if( szORD_YEOJAE_GP.equals("1") ) {			/* 주문재이면 */
		        				szCurrProgCd = "K";						//출하지시대기
		        			}else{										/* 여재이면 */
		        				szCurrProgCd = "Z";						//제품충당대기
		        			}   				
    					}
    			else 
		    			{
		    				if( szORD_YEOJAE_GP.equals("1") ) {			/* 주문재이면 */
		    					if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		    						szCurrProgCd = "A";	
		    					}else{
		        					szCurrProgCd = "A";														//출하지시대기
		    					}
		        			}else{										/* 여재이면 */
		        				if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		        					szCurrProgCd = "A";	
		    					}else{
		    						szCurrProgCd = "Y";						//제품충당대기
		        				}		        																	
		        			}     				
		    			}    			
    		}
    		
    		if( szSLAB_WO_RT_CD.equals("HB") ) {
    			
    			if(sYdGp.equals("2"))  /* B열연 야드*/
    			       {
		    				if( szORD_YEOJAE_GP.equals("1") ) {			/* 주문재이면 */
		    					if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		    						szCurrProgCd = "A";	
		    					}else{
		        					szCurrProgCd = "B";														//출하지시대기
		    					}
		        			}else{										/* 여재이면 */
		        				if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		        					szCurrProgCd = "A";	
		    					}else{
		    						szCurrProgCd = "Y";						//제품충당대기
		        				}		        																	
		        			}   				
    					}
    			else 
		    			{
		    				if( szORD_YEOJAE_GP.equals("1") ) {			/* 주문재이면 */
		    					if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		    						szCurrProgCd = "A";	
		    					}else{
		        					szCurrProgCd = "A";														//출하지시대기
		    					}
		        			}else{										/* 여재이면 */
		        				if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		        					szCurrProgCd = "A";	
		    					}else{
		    						szCurrProgCd = "Y";						//제품충당대기
		        				}		        																	
		        			}     				
		    			}    			
    		}
    		
    		
    		if( szSLAB_WO_RT_CD.equals("HC") ) {
    			
    			if(sYdGp.equals("A")  /* C열연 야드*/
    			  || sYdGp.equals(YdConstant.YD_GP_PORT_SLAB_YARD))  //항만슬라브야드 기능추가 - 2016.01.04 LeeJY
    			       {
		    				if( szORD_YEOJAE_GP.equals("1") ) {			/* 주문재이면 */
		    					if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		    						szCurrProgCd = "A";	
		    					}else{
		        					szCurrProgCd = "B";														//출하지시대기
		    					}
		        			}else{										/* 여재이면 */
		        				if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		        					szCurrProgCd = "A";	
		    					}else{
		    						szCurrProgCd = "Y";						//제품충당대기
		        				}		        																	
		        			}   				
    					}
    			else 
		    			{
		    				if( szORD_YEOJAE_GP.equals("1") ) {			/* 주문재이면 */
		    					if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		    						szCurrProgCd = "A";	
		    					}else{
		        					szCurrProgCd = "A";														//출하지시대기
		    					}
		        			}else{										/* 여재이면 */
		        				if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		        					szCurrProgCd = "A";	
		    					}else{
		    						szCurrProgCd = "Y";						//제품충당대기
		        				}		        																	
		        			}     				
		    			}    			
    		}
    		
    		
    		if( szSLAB_WO_RT_CD.equals("PA") ) {
    			
    			if(sYdGp.equals("A") || sYdGp.equals("D"))  /* C열연,A후판 야드*/
    			       {
		    				if( szORD_YEOJAE_GP.equals("1") ) {			/* 주문재이면 */
		    					if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		    						szCurrProgCd = "A";	
		    					}else{
		        					szCurrProgCd = "B";														//출하지시대기
		    					}
		        			}else{										/* 여재이면 */
		        				if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		        					szCurrProgCd = "A";	
		    					}else{
		    						szCurrProgCd = "Y";						//제품충당대기
		        				}		        																	
		        			}   				
    					}
    			else 
		    			{
		    				if( szORD_YEOJAE_GP.equals("1") ) {			/* 주문재이면 */
		    					if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		    						szCurrProgCd = "A";	
		    					}else{
		        					szCurrProgCd = "A";														//출하지시대기
		    					}
		        			}else{										/* 여재이면 */
		        				if(szSCARFING_YN.equals("Y") && szSCARFING_DONE_YN.equals("N")){
		        					szCurrProgCd = "A";	
		    					}else{
		    						szCurrProgCd = "Y";						//제품충당대기
		        				}		        																	
		        			}     				
		    			}    			
    		}

    	}
		szMsg = "["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		return szCurrProgCd;
	}
	
	/** 
	 * 후판제품 BOOK-OUT 위치 결정
	 * @param inRecord
	 * @throws JDTOException
	 */
	
	public static String getBookOutCd(JDTORecord inRecord) {
		String szMethodName			= "getBookOutCd";
		String szOperationName 		= "후판제품 BOOK-OUT 위치 결정";
		String szMsg        		= null;
		String szYD_BOOK_OUT_LOC	= null;
		String szRTGp				= null;
		String szPlnStrLoc			= null;   
		String szYdPilingCd			= null;
		String szYdMtlLGp			= "";
		String szBayGp				= null;
		String szEqpGp				= null;
		String szBedNo				= null;
		String szYdBedLGp			= null;
		
		YdStkBedDao ydStkbedDao = new YdStkBedDao();	
		
		try {
			szRTGp 			= ydDaoUtils.paraRecChkNull(inRecord,"CONFIRM");
			szPlnStrLoc 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_RCPT_PLN_STR_LOC");
			szYdPilingCd 	= ydDaoUtils.paraRecChkNull(inRecord,"YD_PILING_CD");
			
			szBayGp = szPlnStrLoc.substring(1, 2);
			szEqpGp = szPlnStrLoc.substring(2, 4);
			szBedNo = szPlnStrLoc.substring(6, 8);
			
			if(szYdPilingCd.length()== 8){
				szYdMtlLGp  = szYdPilingCd.substring(6, 8);
			}
			szMsg = "동구분 : [ " + szBayGp + " ]\n";
			szMsg += "스판구분 : [ " + szEqpGp  + " ]\n";
			szMsg += "BED NO : [ " + szBedNo  + " ]\n";
			szMsg += "RT GP : [ " + szRTGp  + " ]\n";
			szMsg += "길이그룹  : [ " + szYdMtlLGp  + " ]\n";
			ydUtils.putLog(szOperationName, szMethodName, szMsg, 3);

			if(szBayGp.equals("E")||szBayGp.equals("F")){
				
				JDTORecordSet rsResult = JDTORecordFactory.getInstance().createRecordSet("");
				JDTORecord recPara = JDTORecordFactory.getInstance().create();
				
				recPara.setField("YD_STK_COL_GP", szPlnStrLoc.substring(0, 6));
				recPara.setField("YD_STK_BED_NO", szPlnStrLoc.substring(6, 8));
				/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedBed*/
		    	int intRtnVal = ydStkbedDao.getYdStkbed(recPara, rsResult, 0);
		    	if (intRtnVal > 0) {
			    	rsResult.first();
			    	JDTORecord recTemp = rsResult.getRecord();
			    	
			    	// BED 초단적 여부 확인
			    	szYdBedLGp = StringHelper.evl(ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_BED_L_GP"),"A"); 
		    	} else {
		    		szYdBedLGp = "A"; //기본 SETTING
		    	}
		    		
			}
			
//DONG_INSERT : OK
			if(szRTGp.equals("3")){ //B-RT 로 변경
				if(szBayGp.equals("A")){ //A 동 
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = YdConstant.OFFLINE_LOC_A_BOOK_OUT_02; //58020
					}else{
						szYD_BOOK_OUT_LOC = YdConstant.OFFLINE_LOC_A_BOOK_OUT_01; //58010
					}
				}else if(szBayGp.equals("B")){ //B 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = YdConstant.OFFLINE_LOC_B_BOOK_OUT_02; //58040
					}else{
						szYD_BOOK_OUT_LOC = YdConstant.OFFLINE_LOC_B_BOOK_OUT_01; //58030
					}
				}else if(szBayGp.equals("C")){ //C 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = "58060";
					}else{
						szYD_BOOK_OUT_LOC = "58050";
					}
				}else if(szBayGp.equals("D")){ //D 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = "58080";
					}else if(szBedNo.equals("02")){
						szYD_BOOK_OUT_LOC = "58075";
					}else{
						szYD_BOOK_OUT_LOC = "58070";
					}
//2CH DONG_INSERT : OK					
				}else if(szBayGp.equals("E")){ //E 동
				
					if(szYdBedLGp.equals("U")){  //BED가 초단척일 경우	 
						if(szBedNo.equals("04")){
							szYD_BOOK_OUT_LOC = "58085";
						}else if(szBedNo.equals("03")){
							szYD_BOOK_OUT_LOC = "58086";
						}else if(szBedNo.equals("02")){
							szYD_BOOK_OUT_LOC = "58087";
						}else {
							szYD_BOOK_OUT_LOC = "58088";
						}
						
					} else {
						if(szBedNo.equals("01")){
							szYD_BOOK_OUT_LOC = "58088";
						}else{
							szYD_BOOK_OUT_LOC = "58086";
						}
					}	
				}else if(szBayGp.equals("F")){ //F 동
					
					if(szYdBedLGp.equals("U")){  //BED가 초단척일 경우	
						if(szBedNo.equals("04")){
							szYD_BOOK_OUT_LOC = "58089";
						}else if(szBedNo.equals("03")){
							szYD_BOOK_OUT_LOC = "58090";
						}else if(szBedNo.equals("02")){
							szYD_BOOK_OUT_LOC = "58091";
						}else {
							szYD_BOOK_OUT_LOC = "58092";
						}
					} else {

						if(szBedNo.equals("01")){
							szYD_BOOK_OUT_LOC = "58092";
						}else{
							szYD_BOOK_OUT_LOC = "58090";
						}
					}	
				}else{
					return "";
				}
			} else if(szRTGp.equals("1")){ //A-RT로 변경
				if(szBayGp.equals("A")){ //A 동 
					if(szEqpGp.equals(YdConstant.SPAN_ORDER_NEW_04) || szEqpGp.equals(YdConstant.SPAN_ORDER_NEW_05)){
						if(szBedNo.equals("01")){
							szYD_BOOK_OUT_LOC = YdConstant.TEMPSTK_LOC_A_BOOK_OUT_02;				// 가적장 55992
						}else{
							// 중폭 이상일 경우에 3베드 위치라고 BOOK OUT 위치는 1베드쪽으로 한다. 
							if(szYdMtlLGp.startsWith("S")){
								szYD_BOOK_OUT_LOC = YdConstant.TEMPSTK_LOC_A_BOOK_OUT_01;			// 가적장 55991
							}else{
								szYD_BOOK_OUT_LOC = YdConstant.TEMPSTK_LOC_A_BOOK_OUT_02;			// 가적장 55992
							}
						}
					}else if(szEqpGp.equals(YdConstant.SPAN_ORDER_NEW_06) || szEqpGp.equals(YdConstant.SPAN_ORDER_NEW_07) || szEqpGp.equals(YdConstant.SPAN_ORDER_NEW_TP)){
						if(szBedNo.equals("01")){
							szYD_BOOK_OUT_LOC = YdConstant.ONLINE_TF_LOC_A_BOOK_OUT_02;				// Transfer 56116
						}else{
							// 중폭 이상일 경우에 3베드 위치라고 BOOK OUT 위치는 1베드쪽으로 한다.
							if(szYdMtlLGp.startsWith("S")){
								szYD_BOOK_OUT_LOC = YdConstant.ONLINE_TF_LOC_A_BOOK_OUT_01;			// Transfer 56106
							}else{
								szYD_BOOK_OUT_LOC = YdConstant.ONLINE_TF_LOC_A_BOOK_OUT_02;			// Transfer 56116
							}
						}
					}else{
						return "";
					}
				}else if(szBayGp.equals("B")){ //B 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = YdConstant.ONLINE_TF_LOC_B_BOOK_OUT_02;					// Transfer 56216
					}else{
						szYD_BOOK_OUT_LOC = YdConstant.ONLINE_TF_LOC_B_BOOK_OUT_01;					// Transfer 56206
					}
				}else if(szBayGp.equals("C")){ //C 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = "56060";
					}else{
						szYD_BOOK_OUT_LOC = "56050";
					}
				}else if(szBayGp.equals("D")){ //D 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = "56080";
					}else if(szBedNo.equals("02")){
						szYD_BOOK_OUT_LOC = "56075";
					}else{
						szYD_BOOK_OUT_LOC = "56070";
					}
//2CH DONG_INSERT : OK
				}else if(szBayGp.equals("E")){ //E 동
										
					if(szYdBedLGp.equals("U")){  //BED가 초단척일 경우 
						if(szBedNo.equals("04")){
							szYD_BOOK_OUT_LOC = "56085";
						}else if(szBedNo.equals("03")){
							szYD_BOOK_OUT_LOC = "56086";
						}else if(szBedNo.equals("02")){
							szYD_BOOK_OUT_LOC = "56087";
						}else {
							szYD_BOOK_OUT_LOC = "56088";
						}
						
					} else {
						if(szBedNo.equals("01")){
							szYD_BOOK_OUT_LOC = "56088";
						}else{
							szYD_BOOK_OUT_LOC = "56086";
						}
					}	
					
				}else if(szBayGp.equals("F")){
					if(szYdBedLGp.equals("U")){  //BED가 초단척일 경우  
						if(szBedNo.equals("04")){
							szYD_BOOK_OUT_LOC = "56090";
						}else if(szBedNo.equals("03")){
							szYD_BOOK_OUT_LOC = "56090";
						}else if(szBedNo.equals("02")){
							szYD_BOOK_OUT_LOC = "56090"; 
						}else {
							szYD_BOOK_OUT_LOC = "56092";
						}
					} else {

						if(szBedNo.equals("01")){
							szYD_BOOK_OUT_LOC = "56092";
						}else{
							szYD_BOOK_OUT_LOC = "56090";
						}
					}	
				}else{
					return "";
				}
			} else if(szRTGp.equals("4")){ //20250904 : 추관식 G R/T 추가
				//G R/T는 BOOK_OUT_LOC=60013임으로 설정은 하나로 함.
				szYD_BOOK_OUT_LOC = "60013";
			} else { //C-RT 로 변경
				if(szBayGp.equals("A")){ //A 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = "59020";
					}else{
						szYD_BOOK_OUT_LOC = "59010";
					}
				} else if(szBayGp.equals("B")){ //B 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = "59116";	// Transfer
					}else{
						szYD_BOOK_OUT_LOC = "59106";	// Transfer
					}
				}else if(szBayGp.equals("C")){ //C 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = "59060";
					}else{
						szYD_BOOK_OUT_LOC = "59050";
					}
				}else if(szBayGp.equals("D")){ //D 동
					if(szBedNo.equals("01")){
						szYD_BOOK_OUT_LOC = "59068";
					}else if(szBedNo.equals("02")){
						szYD_BOOK_OUT_LOC = "59067";
					}else{
						szYD_BOOK_OUT_LOC = "59066";
					}
//2CH DONG_INSERT : OK
				}else if(szBayGp.equals("E")){ //E 동
										
					if(szYdBedLGp.equals("U")){  //BED가 초단척일 경우 
						if(szBedNo.equals("04")){
							szYD_BOOK_OUT_LOC = "59069";
						}else if(szBedNo.equals("03")){
							szYD_BOOK_OUT_LOC = "59070";
						}else if(szBedNo.equals("02")){
							szYD_BOOK_OUT_LOC = "59071";
						}else {
							szYD_BOOK_OUT_LOC = "59072";
						}
						
					} else {
						if(szBedNo.equals("01")){
							szYD_BOOK_OUT_LOC = "59072";
						}else{
							szYD_BOOK_OUT_LOC = "59070";
						}
					}	
					
				}else if(szBayGp.equals("F")){ //F 동
					if(szYdBedLGp.equals("U")){  //BED가 초단척일 경우  
						if(szBedNo.equals("04")){
							//szYD_BOOK_OUT_LOC = "59089";
							szYD_BOOK_OUT_LOC = "59073";
						}else if(szBedNo.equals("03")){
							//szYD_BOOK_OUT_LOC = "59090";
							szYD_BOOK_OUT_LOC = "59074";
						}else if(szBedNo.equals("02")){
							//szYD_BOOK_OUT_LOC = "59091";
							szYD_BOOK_OUT_LOC = "59075";
						}else {
							//szYD_BOOK_OUT_LOC = "59092";
							szYD_BOOK_OUT_LOC = "59076";
						}
					} else {

						if(szBedNo.equals("01")){
							//szYD_BOOK_OUT_LOC = "59092";
							szYD_BOOK_OUT_LOC = "59076";
						}else{
							//szYD_BOOK_OUT_LOC = "59090";
							szYD_BOOK_OUT_LOC = "59074";
						}
					}	
				}else{
					return "";
				}
			}
			
			szMsg = "[YdCommonUtil] ["+szOperationName+"] 결정된 BOOK OUT CODE [ "+szYD_BOOK_OUT_LOC+" ]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			inRecord.setField("YD_BOOK_OUT_LOC"  , szYD_BOOK_OUT_LOC);	// BOOK-OUT CODE
	
		}catch(JDTOException ex) {
			szMsg = "["+szOperationName+"] BOOK-OUT 위치 결정 시 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		return YdConstant.RETN_CD_SUCCESS;
	} //end of getBookOutCd
	
	/**
	 * 준비스케줄원복
	 * @param szYD_WBOOK_ID
	 * @return
	 * @throws JDTOException
	 */
	public static String restorePrepSch(String szYD_WBOOK_ID, String szUpdateMethod) throws JDTOException {
		String szOperationName			= "준비스케줄원복";
		String szMethodName				= "restorePrepSch";
		String szMsg					= null;
		String szRtnMsg					= "";
		int intRtnVal 					= -100;
		JDTORecordSet outRecSet       	= JDTORecordFactory.getInstance().createRecordSet("retTmp");
		JDTORecord recPara         		= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
		
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		
		intRtnVal = ydPrepSchDao.getYdPrepsch(recPara, outRecSet, 8);
		
		if( intRtnVal < 0  ) {
			szMsg = "["+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 오류발생 : 반환값 - " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else if( intRtnVal == 0  ) {
			szMsg = "["+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄 조회 시 존재하지 않음 ";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else{
			
			outRecSet.first();
			recPara = outRecSet.getRecord();
			
			String szYD_PREP_SCH_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
			
			szMsg = "["+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]이 존재하므로 원복 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
			
			recPara         = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
			recPara.setField("DEL_YN",   			"N");
			recPara.setField("MODIFIER",   			szUpdateMethod.length() > 10 ? szUpdateMethod.substring(0, 10) : szUpdateMethod);
			//준비재료 원복
			intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
			
			//준비스케줄 원복
			recPara.setField("YD_WBOOK_ID",   		"");
			intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
			
			szMsg = "["+szOperationName+"] 작업예약ID[" + szYD_WBOOK_ID + "]와 연관된 준비스케줄[" + szYD_PREP_SCH_ID + "]과 준비재료 원복 성공";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		}
		return szRtnMsg;
	}
	
	/**
	 * 준비스케줄삭제
	 * @param szYD_PREP_SCH_ID
	 * @param szYD_WBOOK_ID
	 * @param szUpdateMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String deletePreSch(String szYD_PREP_SCH_ID, String szYD_WBOOK_ID, String szUpdateMethod ) throws JDTOException {
		String szOperationName			= "준비스케줄삭제";
		String szMethodName				= "deletePreSch";
		String szMsg					= "";
		int intRtnVal = -100;
		YdPrepMtlDao ydPrepMtlDao = new YdPrepMtlDao();
		
		szMsg = "["+szOperationName+"] 준비스케줄[" + szYD_PREP_SCH_ID + "]이 존재하므로 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		JDTORecord recPara         = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_PREP_SCH_ID",   	szYD_PREP_SCH_ID);
		recPara.setField("DEL_YN",   			"Y");
		recPara.setField("MODIFIER",   			szUpdateMethod.length() > 10 ? szUpdateMethod.substring(0, 10) : szUpdateMethod);
		//준비재료 삭제처리
		intRtnVal = ydPrepMtlDao.uptDelYdPrepmtlByPrepSchId(recPara);
		
		YdPrepSchDao ydPrepSchDao = new YdPrepSchDao();
		
		//준비스케줄 삭제처리
		recPara.setField("YD_WBOOK_ID",   		szYD_WBOOK_ID);
		intRtnVal =  ydPrepSchDao.updDelYdPrepsch(recPara);
		
		szMsg = "["+szOperationName+"] 준비스케줄[" + szYD_PREP_SCH_ID + "]이 존재하므로 삭제 성공";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 차량상차LOT편성기준조회
	 * @param szYD_GP
	 * @param recOut
	 * @return
	 * @throws JDTOException
	 */
	public static String getCarLdLotRuleFromBRE(String szYD_GP, JDTORecord recOut) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szOperationName			= "차량상차LOT편성기준조회";
		String szMethodName				= "getCarLdLotRuleFromBRE";
		String szMsg					= "";
		JDTORecord	jdtoRcd				= null;
		boolean bRtnVal					= false;
		String szYD_AUTO_LOT			= "Y";
		//String szYD_CARLD_LEV_ARR_LOT	= "";
		
		szMsg = "["+szOperationName+"] 메소드 시작 - 야드구분["+szYD_GP+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		jdtoRcd = JDTORecordFactory.getInstance().create();
		
		if( szYD_GP.equals(YdConstant.YD_GP_C_SLAB_YARD) ) {				//C연주슬라브야드
	    	bRtnVal = GetBreRule1.getYDB199(jdtoRcd);
	    	if( bRtnVal ) {
	    		szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_LOT_YN");
	    		szMsg="BRE RULE -- C연주슬라브야드 차량LOT편성 자동유무[" + szYD_AUTO_LOT + "]";
	    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
		}else if( szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD) ) {	//항만슬라브야드 기능추가 - 2016.01.04 LeeJY
			bRtnVal = GetBreRule2.getYDB299(jdtoRcd);
	    	if( bRtnVal ) {
	    		szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_LOT_YN");
	    		szMsg="BRE RULE -- 항만슬라브야드 차량LOT편성 자동유무[" + szYD_AUTO_LOT + "]";
	    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
		}else if( szYD_GP.equals(YdConstant.YD_GP_A_PLATE_SLAB_YARD) ) {	//A후판슬라브야드
			bRtnVal = GetBreRule2.getYDB299(jdtoRcd);
	    	if( bRtnVal ) {
	    		szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_LOT_YN");
	    		szMsg="BRE RULE -- A후판슬라브야드 차량LOT편성 자동유무[" + szYD_AUTO_LOT + "]";
	    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
		}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_MATL_YARD) ) {	//C열연코일소재야드
			bRtnVal = GetBreRule4.getYDB499(jdtoRcd);
	    	if( bRtnVal ) {
	    		szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_LOT_YN");
	    		szMsg="BRE RULE -- C열연코일소재야드 차량LOT편성 자동유무[" + szYD_AUTO_LOT + "]";
	    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
		}else if( szYD_GP.equals(YdConstant.YD_GP_C_HR_COIL_GDS_YARD) ) {	//C열연코일제품야드
			bRtnVal = GetBreRule5.getYDB599(jdtoRcd);
	    	if( bRtnVal ) {
	    		szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_LOT_YN");
	    		szMsg="BRE RULE -- C열연코일제품야드 차량LOT편성 자동유무[" + szYD_AUTO_LOT + "]";
	    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
		}else if( szYD_GP.equals(YdConstant.YD_GP_PLATE_GDS_YARD) || szYD_GP.equals(YdConstant.YD_GP_PLATE2_GDS_YARD) ) {	//후판제품창고야드, 2후판제품창고야드 - 2012.12.21 수정 (3기)
			bRtnVal = GetBreRule6.getYDB698(jdtoRcd);
	    	if( bRtnVal ) {
	    		szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_LOT_YN");
	    		szMsg="BRE RULE -- 후판제품야드 차량LOT편성 자동유무[" + szYD_AUTO_LOT + "]";
	    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}
		}else if( szYD_GP.equals(YdConstant.YD_GP_INTGR_YARD) ) {			//통합야드A(부두)
			bRtnVal = GetBreRule3.getYDB399(jdtoRcd);
	    	if( bRtnVal ) {
	    		szYD_AUTO_LOT = ydDaoUtils.paraRecChkNull(jdtoRcd, "AUTO_LOT_YN");
	    		szMsg="BRE RULE -- 통합야드 차량LOT편성 자동유무[" + szYD_AUTO_LOT + "]";
	    		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	    	}

	    	
		}
		
		recOut.setField("YD_AUTO_LOT", 				szYD_AUTO_LOT);
		//recOut.setField("YD_CARLD_LEV_ARR_LOT", 	szYD_CARLD_LEV_ARR_LOT);
		
		szMsg = "["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		return szRtnMsg;
	}
	
	/**
	 * 차량상차작업등록 후 준비스케줄 삭제처리
	 * @param szYD_GP
	 * @param szTRN_EQP_CD
	 * @param szYD_STK_COL_GP
	 * @return
	 * @throws JDTOException
	 */
	public static  String procYdWbookForCarLd(String szWLOC_CD,String szTRN_EQP_CD, String szYD_STK_COL_GP, JDTORecord recOut) throws JDTOException {
		/*
		 * 업무기준 : 1. 크레인설비정보를 먼저 조회
		 * 			 2. 해당동의 차량상차 크레인스케줄정보를 조회
		 * 			 3. 크레인스케줄에 올라가지 않은 크레인정보와 관련된 준비스케줄을 조회하여 작업예약에 등록
		 */
		int intRtnVal			= -100;
		String szMsg			= null;
		String szMethodName 	= "procYdWbookForCarLd";
		String szOperationName 	= "차량상차작업등록";
		
		String szSTL_NO			= null;
		String szYD_SCH_CD		= "";
		String szYD_WBOOK_ID	= null;
		String szYD_PREP_SCH_ID	= null;
		String szYD_AIM_YD_GP	= null;
		String szYD_AIM_BAY_GP	= null;
		String szYD_SCH_PRIOR	= null;
		String szYD_CAR_USE_GP	= YdConstant.YD_CAR_USE_GP_TS;
		String szYD_GP			= null;
		String[] szCRN_EQP_ID	= null;
		String szYD_EQP_ID		= null;
		String szYD_GP2			= null;
		boolean isEqual			= false;
		boolean isExist			= false;
		String szReturnMsg = "";
		JDTORecordSet rsResult	= null;
		JDTORecordSet outRecSet	= null;
		JDTORecord	recInTemp	= null;
		JDTORecord	recPara		= null;
		YdStockDao	ydStockDao	= new YdStockDao();
		YdWrkbookDao ydWrkbookDao	= new YdWrkbookDao();
		YdWrkbookMtlDao ydWrkbookMtlDao = new YdWrkbookMtlDao();
		YdEqpDao	ydEqpDao	= new YdEqpDao();
		YdCrnSchDao	ydCrnSchDao	= new YdCrnSchDao();
		
		szYD_GP		= szYD_STK_COL_GP.substring(0, 1);
		
		if(szYD_GP.equals("J")){
			szYD_GP2 ="H";  // C열연 제품야드인 경우 소재 크레인으로 변경
		}else {
			szYD_GP2 =szYD_GP;
		}
		
		//이미 등록된 차량이송준비스케줄을 조회
		szMsg="["+szOperationName+"] 메소드 시작 - 야드구분["+szYD_GP+"], 운송장비코드["+szTRN_EQP_CD+"], 차량정지위치["+szYD_STK_COL_GP+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		/*
		 * 1. 크레인설비정보를 먼저 조회
		 */
		szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"]의 크레인설비 정보 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		recPara = JDTORecordFactory.getInstance().create();
	    recPara.setField("YD_GP",     szYD_GP2);
		intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult, 9);
	
		if (intRtnVal < 0) {
			if (intRtnVal == -1) {
				szMsg = "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인 정보 조회 시 오류발생[1] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			} else {
				szMsg = "["+szOperationName+"] 해당야드["+szYD_GP+"]의 크레인 정보 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}
			throw new DAOException(szMsg);
		}
		
		szCRN_EQP_ID = new String[rsResult.size()];
		
		szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"]의 크레인설비 정보 조회 성공 - 대상재건수 : " + intRtnVal;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		/*
		 * 2. 해당동의 차량상차 크레인스케줄정보를 조회
		 */
		szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"], 동구분["+szYD_STK_COL_GP.substring(1, 2)+"]의 차량상차 크레인스케줄정보를 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara = JDTORecordFactory.getInstance().create();
	    recPara.setField("YD_GP",     		szYD_GP);
	    recPara.setField("YD_BAY_GP",		szYD_STK_COL_GP.substring(1, 2));
	    intRtnVal = ydCrnSchDao.getYdCrnsch(recPara, outRecSet, 44);
	    int intRowNo = 0;
	    if( intRtnVal > 0 ) {
	    	szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"], 동구분["+szYD_STK_COL_GP.substring(1, 2)+"]의 차량상차 크레인스케줄정보를 조회 성공 - 대상재건수 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	    	
	    	for(int i = 1; i <= rsResult.size(); i++ ) {
	    		rsResult.absolute(i);
	    		recInTemp = rsResult.getRecord();
	    		szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recInTemp, "YD_EQP_ID");
	    		for(int j = 1; j <= outRecSet.size(); j++ ) {
	    			outRecSet.absolute(j);
	    			recPara = outRecSet.getRecord();
	    			if( ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID").equals(szYD_EQP_ID) ) {
	    				isEqual = true;
	    				break;
	    			}
	    		}
	    		if( isEqual ) {
	    			isEqual = false;
	    		}else{
	    			szCRN_EQP_ID[intRowNo] = szYD_EQP_ID;
	    			intRowNo++;
	    		}
	    	}
	    	
	    	for(int j = 1; j <= outRecSet.size(); j++ ) {
    			outRecSet.absolute(j);
    			recPara = outRecSet.getRecord();
    			szCRN_EQP_ID[intRowNo] = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_ID");
    			intRowNo++;
    		}
	    }else if( intRtnVal == 0 ) {
	    	szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"], 동구분["+szYD_STK_COL_GP.substring(1, 2)+"]의 차량상차 크레인스케줄정보를 조회 성공 - 대상재건수 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
	    	for(int i = 1; i <= rsResult.size(); i++ ) {
	    		rsResult.absolute(i);
	    		recInTemp = rsResult.getRecord();
	    		szCRN_EQP_ID[i - 1] = ydDaoUtils.paraRecChkNull(recInTemp, "YD_EQP_ID");
	    	}
	    }else{
	    	szMsg = "["+szOperationName+"] 해당야드["+szYD_GP+"], 동구분["+szYD_STK_COL_GP.substring(1, 2)+"]의 차량상차 크레인스케줄정보를 조회 시 오류발생[2] - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
	    	throw new DAOException(szMsg);
	    }
	    
	    /*
		 * 도착동의 차량종류별(Pallet/Trailer) 빠른 준비스케줄조회
		 * 수정자 : 임춘수
		 * 수정일 : 2009.10.27
		 */
	    
	    if(szYD_GP.equals("J")){
			if((szYD_STK_COL_GP.substring(1, 2).equals("B")||szYD_STK_COL_GP.substring(1, 2).equals("C")) 
				&& (szYD_STK_COL_GP.substring(5, 6).equals("4") || szYD_STK_COL_GP.substring(5, 6).equals("5"))){
				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT52UM"; //이송2(제품2통로)
			}else{
				szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM"; //이송출고(제품1통로)
			}
		}else{
			szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT";  
		}
	    
	    recInTemp = JDTORecordFactory.getInstance().create();
	    recInTemp.setField("YD_GP", 			szYD_GP);
		recInTemp.setField("YD_SCH_CD", 		szYD_SCH_CD);
		recInTemp.setField("YD_PREP_WK_ST", 	"L");
		//항목추가
		recInTemp.setField("CAR_GP", 	szTRN_EQP_CD.substring(1, 2));
	    for(int i = 0; i < szCRN_EQP_ID.length; i++ ) {
	    	szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"], 동구분["+szYD_STK_COL_GP.substring(1, 2)+"], 운송장비코드["+szTRN_EQP_CD+"], 작업크레인["+szCRN_EQP_ID[i]+"]으로 이미 등록된 차량이송준비스케줄을 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recInTemp.setField("YD_WRK_PLAN_CRN",   szCRN_EQP_ID[i]);
			if(szYD_GP.equals("A")||szYD_GP.equals("D") ||
			   szYD_GP.equals(YdConstant.YD_GP_PORT_SLAB_YARD)  //항만슬라브야드 기능추가 - 2016.01.04 LeeJY
			  ) 
			{
				recInTemp.setField("YD_WRK_PLAN_CRN",   ""); 
			}
			else
			{
				recInTemp.setField("YD_WRK_PLAN_CRN",   szCRN_EQP_ID[i]);
			}
			 
			//이송 lot 편성대상 가져오기 
			intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 151);
			
			if( intRtnVal > 0 ) {
				szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"], 동구분["+szYD_STK_COL_GP.substring(1, 2)+"], 운송장비코드["+szTRN_EQP_CD+"], 작업크레인["+szCRN_EQP_ID[i]+"]으로 이미 등록된 차량이송준비스케줄을 조회 성공 - 대상재건수 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				isExist = true;
				break;
			}else{
				szMsg="["+szOperationName+"] 야드구분["+szYD_GP+"], 동구분["+szYD_STK_COL_GP.substring(1, 2)+"], 운송장비코드["+szTRN_EQP_CD+"], 작업크레인["+szCRN_EQP_ID[i]+"]으로 이미 등록된 차량이송준비스케줄을 조회 후 대상재가 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
	    }
	    
	    if(szYD_GP.equals("H")||szYD_GP.equals("J")) 
		{
		    if( !isExist ) {
		    	// 상차 Lot 편성 대상 재료 Select ###################################
		    	
		    	recInTemp = JDTORecordFactory.getInstance().create();
		 	    recInTemp.setField("SPOS_WLOC_CD", 			szWLOC_CD);
		 	    recInTemp.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
		 	    recInTemp.setField("YD_BAY_CD", 			szYD_STK_COL_GP.substring(0,2));
		  
				// 저장품 테이블 조회
				/* com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockYdStkLyrMatlLotC2*/
				intRtnVal = ydStockDao.getYdStock(recInTemp, rsResult, 506);
				
				if (intRtnVal > 0){					
					
		    		isExist = true;
		    	} 
				//##############################################################
		    }
		}

	    if( !isExist ) {
			szMsg="["+szOperationName+"] 이미 등록된 차량이송준비스케줄을 조회 후 대상재가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			szYD_WBOOK_ID = "";
		}else{
			rsResult.first();
			recInTemp = rsResult.getRecord();
			
			szYD_PREP_SCH_ID 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_PREP_SCH_ID");
			szYD_AIM_YD_GP 		= ydDaoUtils.paraRecChkNull(recInTemp, "YD_AIM_YD_GP");
			szYD_AIM_BAY_GP 	= ydDaoUtils.paraRecChkNull(recInTemp, "YD_AIM_BAY_GP");
			
			if(!szYD_PREP_SCH_ID.equals("")){
				szYD_SCH_CD = ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_CD");
			}else{
				// =================================================================================
				//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM";
				
				if(szYD_GP.equals("J")){
					if((szYD_STK_COL_GP.substring(1, 2).equals("B")||szYD_STK_COL_GP.substring(1, 2).equals("C")) 
						&& (szYD_STK_COL_GP.substring(5, 6).equals("4") || szYD_STK_COL_GP.substring(5, 6).equals("5"))){
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT52UM"; //이송2(제품2통로)
					}else{
						szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM"; //이송출고(제품1통로)
					}
				}else{
					//szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT02UM"; //이송출고(제품통로)
					szYD_SCH_CD = szYD_STK_COL_GP.substring(0, 2) + "PT01UM"; //B동 이송(소재통로)
				}
				
				// =================================================================================
			}
			
			recInTemp = JDTORecordFactory.getInstance().create();
			YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recInTemp);
			
			szYD_SCH_PRIOR = ydDaoUtils.paraRecChkNull(recInTemp, "YD_SCH_PRIOR");
			
			szMsg="["+szOperationName+"] 통합야드인 경우 이미 등록된 차량이송준비스케줄["+szYD_PREP_SCH_ID+"]을 조회 성공 : 목표야드["+szYD_AIM_YD_GP+"], 목표동["+szYD_AIM_BAY_GP+"] - 대상재매수["+rsResult.size()+"]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szYD_WBOOK_ID = ydWrkbookDao.getYdWrkbookId();
			
			szMsg="["+szOperationName+"] 통합야드인 경우 이미 등록된 차량이송준비스케줄["+szYD_PREP_SCH_ID+"]을 조회 후 작업예약["+szYD_WBOOK_ID+"] 등록 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//작업예약에 등록처리
			recInTemp = JDTORecordFactory.getInstance().create();
			//INSERT할 항목 SET
			recInTemp.setField("YD_WBOOK_ID",   	szYD_WBOOK_ID);
			recInTemp.setField("YD_GP", 		  	szYD_STK_COL_GP.substring(0,1));
			recInTemp.setField("YD_BAY_GP", 	  	szYD_STK_COL_GP.substring(1,2));
			recInTemp.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
			recInTemp.setField("YD_SCH_CD", 	  	szYD_SCH_CD);
			recInTemp.setField("REGISTER", 	  		szMethodName.substring(0, 10));
			recInTemp.setField("YD_CAR_USE_GP", 	szYD_CAR_USE_GP);
			recInTemp.setField("TRN_EQP_CD", 	  	szTRN_EQP_CD);
			recInTemp.setField("YD_AIM_YD_GP",  	szYD_AIM_YD_GP);
			recInTemp.setField("YD_AIM_BAY_GP", 	szYD_AIM_BAY_GP);
	
			//작업예약 INSERT
			intRtnVal = ydWrkbookDao.insYdWrkbook(recInTemp);
			if(intRtnVal < 1){
				szMsg = "["+szOperationName+"] 작업예약 데이터 등록 중 에러";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				throw new DAOException(szMsg);
				//return YdConstant.RETN_CD_FAILURE;
			}
			szMsg="["+szOperationName+"] 통합야드인 경우 이미 등록된 차량이송준비스케줄["+szYD_PREP_SCH_ID+"]을 조회 후 작업예약["+szYD_WBOOK_ID+"] 등록 성공";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg="["+szOperationName+"] 통합야드인 경우 이미 등록된 차량이송준비스케줄["+szYD_PREP_SCH_ID+"]을 조회 후 작업예약["+szYD_WBOOK_ID+"]재료 등록 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//조회항목 record 생성
			recPara = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_WBOOK_ID",   szYD_WBOOK_ID);
			recPara.setField("REGISTER", 	  szMethodName.substring(0, 10));

			for (int Loop_i = 1; Loop_i <= rsResult.size(); Loop_i++){
				rsResult.absolute(Loop_i);
				recInTemp = rsResult.getRecord();
				szSTL_NO = ydDaoUtils.paraRecChkNull(recInTemp, "STL_NO");
				
	
				//재료번호
				recPara.setField("STL_NO", 		   szSTL_NO);
				//적치열구분
				recPara.setField("YD_STK_COL_GP",  ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_COL_GP"));
				//적치BED번호
				recPara.setField("YD_STK_BED_NO",  ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_BED_NO"));
				//적치단번호
				recPara.setField("YD_STK_LYR_NO",  ydDaoUtils.paraRecChkNull(recInTemp, "YD_STK_LYR_NO"));
				//권상모음순서
				recPara.setField("YD_UP_COLL_SEQ", "" + Loop_i);
				
				// 작업예약재료 테이블에 등록한다.
				intRtnVal = ydWrkbookMtlDao.insYdWrkbookmtl(recPara);
				
				if(intRtnVal < 1){
					szMsg = "["+szOperationName+"] 작업예약재료 데이터 등록 중 에러";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					throw new DAOException(szMsg);
					//return YdConstant.RETN_CD_FAILURE;
				}
			}
			
			szMsg="["+szOperationName+"] 통합야드인 경우 이미 등록된 차량이송준비스케줄["+szYD_PREP_SCH_ID+"]을 조회 후 작업예약["+szYD_WBOOK_ID+"]재료 등록 성공";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			if(!szYD_PREP_SCH_ID.equals("")){
				 szReturnMsg = YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
			}
			szMsg="["+szOperationName+"] 통합야드인 경우 작업예약 등록 후 차량이송준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 성공 : " + szReturnMsg;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		recOut.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
		recOut.setField("YD_SCH_CD", 				szYD_SCH_CD);
		
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 후판제품창고의 BOOK-OUT LOC의 가상베드정보를 SHIFT시키는 메소드
	 * @param szYD_BOOK_OUT_LOC
	 * @param startBedNo
	 * @return
	 * @throws JDTOException
	 */
	public static String procShiftBedInfoForBookoutLoc(String szYD_BOOK_OUT_LOC) throws JDTOException {
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "procShiftBedInfoForBookoutLoc";
		String szOperationName			= "가상베드정보SHIFT";
		String szMsg					= "";
		int intRtnVal					= -100;
		int intRtnVal1					= -100;
		
		JDTORecord	recPara				= null;
		JDTORecord	recTemp				= null;
		JDTORecordSet outRecSet			= null;
		JDTORecordSet rsOut				= null;
		
		YdStkBedDao ydStkBedDao	= new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_NO			= null;
		
		String szPREV_YD_STK_COL_GP			= null;
		String szPREV_YD_STK_BED_NO			= null;
		//String szPREV_YD_STK_LYR_NO			= null;
		
		String szSTL_NO					= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		
		String szStartBedNo				= szYD_BOOK_OUT_LOC.substring(6);
		
		szMsg="["+szOperationName+"] 메소드 시작 - BOOK-OUT LOC["+szYD_BOOK_OUT_LOC+"] 시작 베드 번호["+szStartBedNo+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		szYD_STK_COL_GP = szYD_BOOK_OUT_LOC.substring(0, 6);
		szYD_STK_BED_NO = szYD_BOOK_OUT_LOC.substring(6, 7) + "_";			//LIKE 검색을 위해서
		
		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara =  JDTORecordFactory.getInstance().create();
		
		recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
		
		intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 23);
		
		//intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 75);
		
		recTemp =  JDTORecordFactory.getInstance().create();
	
	
		int startBedNo = Integer.parseInt(szStartBedNo) - Integer.parseInt(szYD_BOOK_OUT_LOC.substring(6, 7) + "0");
		
		if( intRtnVal > 0 ) {
			szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]로 베드정보 조회 성공 - 대상재건수 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			for(int i = 1 + startBedNo; i <= outRecSet.size(); i++ ) {
				/*
				 * 선행 베드의 적치열, 적치베드, 적치단 정보 추출
				 */
				outRecSet.absolute(i);
				recPara = outRecSet.getRecord();

				szPREV_YD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szPREV_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				
				if( i < outRecSet.size() ) {
				
					outRecSet.absolute(i + 1);
					recPara = outRecSet.getRecord();
					
					/*
					 * 후행 베드의 적치열, 적치베드, 적치단 정보 추출
					 */
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
					
					rsOut = JDTORecordFactory.getInstance().createRecordSet("");
					
					intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsOut, 75);
					
					if( intRtnVal <= 0 ) {
						szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 적치단 정보 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						continue;
					}else{
						szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 적치단 정보 조회 성공 - 대상재건수 : " + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						for(int j = 1; j <= rsOut.size(); j++ ) {
							rsOut.absolute(j);
							recPara = rsOut.getRecord();
							
							szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
							szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
							szYD_STK_LYR_MTL_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
							
							recTemp.setField("YD_STK_COL_GP", szPREV_YD_STK_COL_GP);
							recTemp.setField("YD_STK_BED_NO", szPREV_YD_STK_BED_NO);
							recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
							recTemp.setField("STL_NO", szSTL_NO);
							recTemp.setField("YD_STK_LYR_MTL_STAT", szYD_STK_LYR_MTL_STAT);
							
							intRtnVal1 = ydStkLyrDao.updYdStklyr(recTemp, 0);
							
							if( intRtnVal1 <= 0 ) {
								szMsg="["+szOperationName+"] 적치열["+szPREV_YD_STK_COL_GP+"], 적치베드["+szPREV_YD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]의 재료번호["+szSTL_NO+"], 재료상태["+szYD_STK_LYR_MTL_STAT+"]로 수정되지 않았습니다. - 반환값 : " + intRtnVal1;
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
								continue;
							}else{
								szMsg="["+szOperationName+"] 적치열["+szPREV_YD_STK_COL_GP+"], 적치베드["+szPREV_YD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]의 재료번호["+szSTL_NO+"], 재료상태["+szYD_STK_LYR_MTL_STAT+"]로 수정되었습니다.";
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}
					}
				}else{
					rsOut = JDTORecordFactory.getInstance().createRecordSet("");
					
					intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsOut, 75);
					
					if( intRtnVal <= 0 ) {
						
					}else{
						szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 적치단 정보 조회 성공 - 대상재건수 : " + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						for(int j = 1; j <= rsOut.size(); j++ ) {
							rsOut.absolute(j);
							recPara = rsOut.getRecord();
							
							szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
							
							recTemp.setField("YD_STK_COL_GP", szPREV_YD_STK_COL_GP);
							recTemp.setField("YD_STK_BED_NO", szPREV_YD_STK_BED_NO);
							recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
							recTemp.setField("STL_NO", 		  "");
							recTemp.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_ACTIVE);
							
							intRtnVal1 = ydStkLyrDao.updYdStklyr(recTemp, 0);
							
							if( intRtnVal1 <= 0 ) {
								szMsg="["+szOperationName+"] 적치열["+szPREV_YD_STK_COL_GP+"], 적치베드["+szPREV_YD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]의 재료번호, 재료상태를 초기화 시 수정되지 않았습니다. - 반환값 : " + intRtnVal1;
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
								continue;
							}else{
								szMsg="["+szOperationName+"] 적치열["+szPREV_YD_STK_COL_GP+"], 적치베드["+szPREV_YD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]의 재료번호, 재료상태를 초기화 시 수정되었습니다.";
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}
					}
				}
			}
			
		}else{
			szMsg="["+szOperationName+"] 해당적치열["+szYD_STK_COL_GP+"]의 베드정보가 존재하지 않으므로 SHIFT 처리를 하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		return szRtnMsg;
	}
	
	/**
	 * 후판제품창고의 BOOK-OUT LOC의 가상베드정보를 역SHIFT시키는 메소드
	 * @param szYD_BOOK_OUT_LOC
	 * @param startBedNo
	 * @return
	 * @throws JDTOException
	 */
	public static String procReverseShiftBedInfoForBookoutLoc(String szYD_BOOK_OUT_LOC) throws JDTOException {
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "procReverseShiftBedInfoForBookoutLoc";
		String szOperationName			= "가상베드정보역SHIFT";
		String szMsg					= "";
		int intRtnVal					= -100;
		int intRtnVal1					= -100;
		
		JDTORecord	recPara				= null;
		JDTORecord	recTemp				= null;
		JDTORecordSet outRecSet			= null;
		JDTORecordSet rsOut				= null;
		
		YdStkBedDao ydStkBedDao	= new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_NO			= null;
		
		String szPREV_YD_STK_COL_GP			= null;
		String szPREV_YD_STK_BED_NO			= null;
		//String szPREV_YD_STK_LYR_NO			= null;
		
		String szSTL_NO					= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		
		String szStartBedNo				= szYD_BOOK_OUT_LOC.substring(6);
		
		szMsg="["+szOperationName+"] 메소드 시작 - BOOK-OUT LOC["+szYD_BOOK_OUT_LOC+"] 시작 베드 번호["+szStartBedNo+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		szYD_STK_COL_GP = szYD_BOOK_OUT_LOC.substring(0, 6);
		szYD_STK_BED_NO = szYD_BOOK_OUT_LOC.substring(6, 7) + "_";			//LIKE 검색을 위해서
		
		outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
		recPara =  JDTORecordFactory.getInstance().create();
		
		recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
		recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
		//베드번호 Asc
		intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 23);
		
		//intRtnVal = ydStkLyrDao.getYdStklyr(recPara, outRecSet, 75);
		
		recTemp =  JDTORecordFactory.getInstance().create();
		
		int startBedNo = Integer.parseInt(szStartBedNo) - Integer.parseInt(szYD_BOOK_OUT_LOC.substring(6, 7) + "0");
		
		if( intRtnVal > 0 ) {
			szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]로 베드정보 조회 성공 - 대상재건수 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//for(int i = 1 + startBedNo; i <= outRecSet.size(); i++ ) {
			for(int i = outRecSet.size(); i >= startBedNo + 1; i-- ) {
				
				/*
				 * 후행 베드의 적치열, 적치베드, 적치단 정보 추출
				 */
				outRecSet.absolute(i);
				recPara = outRecSet.getRecord();
				
				szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				
				if( i > startBedNo + 1 ) {
					
					/*
					 * 선행 베드의 적치열, 적치베드, 적치단 정보 추출
					 */
					outRecSet.absolute(i - 1);
					recPara = outRecSet.getRecord();

					szPREV_YD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szPREV_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
					
					rsOut = JDTORecordFactory.getInstance().createRecordSet("");
					
					//YD_STK_COL_GP ASC, YD_STK_BED_NO ASC, YD_STK_LYR_NO ASC
					intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsOut, 75);
					
					if( intRtnVal <= 0 ) {
						szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 적치단 정보 조회 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						continue;
					}else{
						szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 적치단 정보 조회 성공 - 대상재건수 : " + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						for(int j = 1; j <= rsOut.size(); j++ ) {
							rsOut.absolute(j);
							recPara = rsOut.getRecord();
							
							szYD_STK_LYR_NO 		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
							szSTL_NO 				= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
							szYD_STK_LYR_MTL_STAT 	= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_MTL_STAT");
							
							recTemp.setField("YD_STK_COL_GP", 		szYD_STK_COL_GP);
							recTemp.setField("YD_STK_BED_NO", 		szYD_STK_BED_NO);
							recTemp.setField("YD_STK_LYR_NO", 		szYD_STK_LYR_NO);
							recTemp.setField("STL_NO", 				szSTL_NO);
							recTemp.setField("YD_STK_LYR_MTL_STAT", szYD_STK_LYR_MTL_STAT);
							
							intRtnVal1 = ydStkLyrDao.updYdStklyr(recTemp, 0);
							
							if( intRtnVal1 <= 0 ) {
								szMsg="["+szOperationName+"] 적치열["+szPREV_YD_STK_COL_GP+"], 적치베드["+szPREV_YD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]의 재료번호["+szSTL_NO+"], 재료상태["+szYD_STK_LYR_MTL_STAT+"]로 수정되지 않았습니다. - 반환값 : " + intRtnVal1;
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
								continue;
							}else{
								szMsg="["+szOperationName+"] 적치열["+szPREV_YD_STK_COL_GP+"], 적치베드["+szPREV_YD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]의 재료번호["+szSTL_NO+"], 재료상태["+szYD_STK_LYR_MTL_STAT+"]로 수정되었습니다.";
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}
					}
				}else{
					rsOut = JDTORecordFactory.getInstance().createRecordSet("");
					
					intRtnVal = ydStkLyrDao.getYdStklyr(recPara, rsOut, 75);
					
					if( intRtnVal <= 0 ) {
						
					}else{
						szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"], 적치베드["+szYD_STK_BED_NO+"]로 적치단 정보 조회 성공 - 대상재건수 : " + intRtnVal;
						ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
						for(int j = 1; j <= rsOut.size(); j++ ) {
							rsOut.absolute(j);
							recPara = rsOut.getRecord();
							
							szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
							
							recTemp.setField("YD_STK_COL_GP", szPREV_YD_STK_COL_GP);
							recTemp.setField("YD_STK_BED_NO", szPREV_YD_STK_BED_NO);
							recTemp.setField("YD_STK_LYR_NO", szYD_STK_LYR_NO);
							recTemp.setField("STL_NO", 		  "");
							recTemp.setField("YD_STK_LYR_MTL_STAT", YdConstant.YD_STK_LYR_ACTIVE);
							
							intRtnVal1 = ydStkLyrDao.updYdStklyr(recTemp, 0);
							
							if( intRtnVal1 <= 0 ) {
								szMsg="["+szOperationName+"] 적치열["+szPREV_YD_STK_COL_GP+"], 적치베드["+szPREV_YD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]의 재료번호, 재료상태를 초기화 시 수정되지 않았습니다. - 반환값 : " + intRtnVal1;
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
								continue;
							}else{
								szMsg="["+szOperationName+"] 적치열["+szPREV_YD_STK_COL_GP+"], 적치베드["+szPREV_YD_STK_BED_NO+"], 적치단["+szYD_STK_LYR_NO+"]의 재료번호, 재료상태를 초기화 시 수정되었습니다.";
								ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
							}
						}
					}
				}
			}
			
		}else{
			szMsg="["+szOperationName+"] 해당적치열["+szYD_STK_COL_GP+"]의 베드정보가 존재하지 않으므로 SHIFT 처리를 하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		return szRtnMsg;
	}
	
	/**
	 * 빠른준비스케줄조회
	 * @param recPara
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStockFromEarliestPrepSch(JDTORecord recPara, JDTORecordSet rsResult) throws JDTOException {
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szMsg	= null;
		int intRtnVal	= -100;
		String szMethodName		= "getYdStockFromEarliestPrepSch";
		String szOperationName	= "빠른준비스케줄조회";
		
		String szQueryType		= null;
		
		szQueryType = ydDaoUtils.paraRecChkNull(recPara, "QUERY_TYPE");
		
		YdStockDao ydStockDao	= new YdStockDao();
		
		if( szQueryType.equals("COL_DESC")) {
			intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 169);
		}else{
			szMsg="["+szOperationName+"] 빠른 준비스케줄을 조회 시 지원하지 않는 쿼리종류["+szQueryType+"]입니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		if( intRtnVal > 0 ) {
			szMsg="["+szOperationName+"] 빠른 준비스케줄을 조회 성공 - 대상재건수 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
		}else{
			szMsg="["+szOperationName+"] 빠른 준비스케줄을 조회 후 대상재가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 야드저장위치를 진행관리의 공통테이블에 야드저장위치를 업데이트하는 메소드
	 * @param msgRecord
	 * @param szFromMethod
	 * @return
	 */
    public static String setYdStrLocToPtComm (JDTORecord msgRecord, String szFromMethod) throws JDTOException {
    	/*
    	 * 업무기준 :1. 재료품목으로 재료종류를 판단하여 재료공통을 조회
    	 * 			2. 재료공통에 현재 야드저장위치를 수정
    	 * 수정자 : 임춘수
    	 * 수정일자 :1) 2009.11.13 최초등록
    	 * 
    	 * 파라미터 :
    	 * 			1)	주편인경우 MSLAB_NO
    	 * 			   	슬라브인경우 SLAB_NO
    	 * 			 	후판제품인 경우 PLATE_NO
    	 * 				코일인경우 COIL_NO
    	 * 			2)	재료품목 YD_MTL_ITEM
    	 * 			3)	YD_STK_COL_GP	: 적치열구분
    	 * 			4)	YD_STK_BED_NO 	: 적치베드번호
    	 * 			5)	YD_STK_LYR_NO	: 적치단번호
    	 */
    	String szMsg						= "" ;
    	String szMethodName					= "setYdStrLocToPtComm" ;
    	String szOperationName				= "야드저장위치수정(공통)";
    	String szRtnMsg						= YdConstant.RETN_CD_SUCCESS;
    	
    	JDTORecordSet getRecSet 			= null;
    	JDTORecord 	  getRecord 			= null;
    	JDTORecord 	  recPara	 			= null;
    	JDTORecord 	  setRecord 			= null;
    	
    	YdStockDao	ydStockDao	= new YdStockDao();
    	
    	String szSTL_NO						= null;
    	//
    	String szCurYdStrLoc				= null;
    	//현재저장위치
    	String szYdStrLoc					= "" ;
    	//이전저장위치
    	String szYdStrLocHis1				= "" ;
    	//재료품목 정의
    	String szYdMtlItem					= "" ;
    	//크레인스케줄의 정보				현재저장위치를 생성하기 위해...
    	String szYdGp						= "" ;
    	String szYdBayGp					= "" ;
    	String szYdEqpId					= "" ;
    	String szYdStkColNo					= "" ;
    	String szYdStkBedNo					= "" ;
    	String szYdStkLyrNo					= "" ;
    	//String szYdDnWrLoc                  = "";
    	//재료품목
    	String szYD_MTL_ITEM				= null;
    	String szYD_MTL_ITEM_NM				= null;
    	
    	String szYD_STK_COL_GP				= null;
    	String szYD_STK_BED_NO				= null;
    	String szYD_STK_LYR_NO				= null;
    	
    	int intRtnVal 						= -100 ;
    	int intGp							= -1;
        
    	//------------------------------------------------------------------------------------------------------
    	//	파라미터 확인
    	//------------------------------------------------------------------------------------------------------

        szMsg = "["+szOperationName+"] 메소드 시작 - 파라미터 확인";
        ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
        
        ydUtils.displayRecord(szOperationName, msgRecord);
        
        szYD_MTL_ITEM = ydDaoUtils.paraRecChkNull(msgRecord, "YD_MTL_ITEM");
        szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_COL_GP");
        szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_BED_NO");
        szYD_STK_LYR_NO = ydDaoUtils.paraRecChkNull(msgRecord, "YD_STK_LYR_NO");
        
        if( szYD_MTL_ITEM.equals("") ) {
        	szMsg = "["+szOperationName+"] 재료품목이 존재하지 않습니다.";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_NO_PARAM;
        }
        
        if( szYD_STK_COL_GP.equals("") ) {
        	szMsg = "["+szOperationName+"] 적치열정보["+szYD_STK_COL_GP+"]가 존재하지 않습니다.";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_NO_PARAM;
        }
        
        //------------------------------------------------------------------------------------------------------
        
        
        
        //------------------------------------------------------------------------------------------------------
    	//재료품목으로 비교하고 품목에따라 맞는 공통테이블을 조회한다.
        //------------------------------------------------------------------------------------------------------
        
		szYdMtlItem = szYD_MTL_ITEM.substring(0, 1) ;
		recPara = JDTORecordFactory.getInstance().create();
		
    	if(szYdMtlItem.equals("B")){
    		//주편공통
    		szYD_MTL_ITEM_NM = "주편";
            szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "MSLAB_NO");
            intGp = 6;
            recPara.setField("MSLAB_NO", szSTL_NO);
    	}else if (szYdMtlItem.equals("S")) {
    		szYD_MTL_ITEM_NM = "슬라브";
            szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "SLAB_NO");
            intGp = 2;
            recPara.setField("SLAB_NO", szSTL_NO);
    	}else if (szYdMtlItem.equals("C")) {
    		szYD_MTL_ITEM_NM = "COIL";
            szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "COIL_NO");
            intGp = 8;
            recPara.setField("COIL_NO", szSTL_NO);
    	}else if (szYdMtlItem.equals("P")) {
    		szYD_MTL_ITEM_NM = "PLATE";
            szSTL_NO = ydDaoUtils.paraRecChkNull(msgRecord, "PLATE_NO");
            intGp = 4;
            recPara.setField("PLATE_NO", szSTL_NO);
    	}else{
    		szMsg = "["+szOperationName+"] 지원하지 않는 재료품목입니다.";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_FAILURE;
    	}
    	
    	szMsg = "["+szOperationName+"] 해당재료["+szSTL_NO+"]를 "+szYD_MTL_ITEM_NM+"공통테이블에서 조회 시작";
        ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	
        getRecSet = JDTORecordFactory.getInstance().createRecordSet("temp");
    	intRtnVal = ydStockDao.getYdStock(recPara, getRecSet, intGp);
    	
    	if( intRtnVal < 0 ) {
    		szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]가 "+szYD_MTL_ITEM_NM+"공통테이블 조회 시 오류발생 - 반환값 : " + intRtnVal;
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_FAILURE;
    	}else if( intRtnVal == 0 ) {
    		szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]가 "+szYD_MTL_ITEM_NM+"공통테이블  조회 시  존재하지 않습니다.";
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
    		return YdConstant.RETN_CD_NOTEXIST;
    	}
    	
    	szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]가 "+szYD_MTL_ITEM_NM+"공통테이블에 존재합니다.";
        ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	
    	getRecSet.first();
    	getRecord 			= getRecSet.getRecord() ;
    	szYdStrLoc 			= ydDaoUtils.paraRecChkNull(getRecord, "YD_STR_LOC");
    	szYdStrLocHis1 		= ydDaoUtils.paraRecChkNull(getRecord, "YD_STR_LOC_HIS1");
    	
    	//------------------------------------------------------------------------------------------------------
    	
    	
    	//------------------------------------------------------------------------------------------------------
    	//	공통테이블 저장위치 수정
    	//------------------------------------------------------------------------------------------------------
    	szYdGp 				= szYD_STK_COL_GP.substring(0, 1); 
    	szYdBayGp 			= szYD_STK_COL_GP.substring(1, 2);
    	szYdEqpId 			= szYD_STK_COL_GP.substring(2, 4); 
    	szYdStkColNo 		= szYD_STK_COL_GP.substring(4); 
    	szYdStkBedNo 		= szYD_STK_BED_NO; 
    	szYdStkLyrNo		= szYD_STK_LYR_NO;
        //szYdDnWrLoc         = msgRecord.getFieldString("YD_DN_WR_LOC");
        
    	setRecord 			= JDTORecordFactory.getInstance().create();
    	setRecord.setField("YD_GP",         szYdGp);
    	setRecord.setField("YD_BAY_GP",     szYdBayGp);
    	setRecord.setField("YD_EQP_GP",     szYdEqpId);
    	setRecord.setField("YD_STK_COL_NO", szYdStkColNo);
    	setRecord.setField("YD_STK_BED_NO", szYdStkBedNo);
    	setRecord.setField("YD_STK_LYR_NO", szYdStkLyrNo);
    	setRecord.setField("FNL_REG_PGM",   szFromMethod);
    	setRecord.setField("MODIFIER",      "YDSYSTEM");
    	
        
    	//전저장위치 = szYdStrLoc,	 전전저장위치 = szYdStrLocHis1
    	setRecord.setField("YD_STR_LOC_HIS1", 	szYdStrLoc) ;
    	setRecord.setField("YD_STR_LOC_HIS2", 	szYdStrLocHis1) ;
    	
    	szMsg = "["+szOperationName+"] 수정 전 - 조회된 현 저장위치 : " + szYdStrLoc + " , 전 저장위치 : " + szYdStrLocHis1;
        ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
        
    	if(szYdMtlItem.equals("B")){					//주편 공통 업데이트
    		szCurYdStrLoc = szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo+szYdStkLyrNo.substring(1,3);
    		setRecord.setField("MSLAB_NO",   szSTL_NO); 
    		setRecord.setField("YD_STR_LOC",  szCurYdStrLoc);
    		intGp = 2;
    	}else if (szYdMtlItem.equals("S")) {			//슬라브 공통 업데이트
    		szCurYdStrLoc = szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo.substring(1)+szYdStkLyrNo;
    		setRecord.setField("SLAB_NO",    szSTL_NO); 
    		setRecord.setField("YD_STR_LOC",  szCurYdStrLoc);
    		intGp = 0;
     	}else if (szYdMtlItem.equals("C")) {			//코일공통 업데이트
    		szCurYdStrLoc = szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo.substring(1)+szYdStkLyrNo;
    		setRecord.setField("COIL_NO",    szSTL_NO); 
    		setRecord.setField("YD_STR_LOC",  szCurYdStrLoc);
    		intGp = 3;
     	}else if (szYdMtlItem.equals("P")) {			//PLATE공통 업데이트
     		//수정 20090907 김진욱 : PLATE공통테이블에 현저장위치 자리수가 잘못등록 (야드구분+동구분+설비구분+열번호+베드번호1자리+단번호3자리)
     		if( szYdStkBedNo.equals("") ) {
     			szCurYdStrLoc = szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo;
     		}else{
     			szCurYdStrLoc = szYdGp+szYdBayGp+szYdEqpId+szYdStkColNo+szYdStkBedNo.substring(1,2)+szYdStkLyrNo;
     		}
    		setRecord.setField("PLATE_NO",   szSTL_NO); 
    		setRecord.setField("YD_STR_LOC", szCurYdStrLoc);
    		intGp = 1;
     	}
    	
    	intRtnVal = ydStockDao.updPtComm_LOC(setRecord, intGp);
    	
    	if(intRtnVal < 0) {
            szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]를 "+szYD_MTL_ITEM_NM+"공통Table의 현저장위치["+szCurYdStrLoc+"], 전저장위치["+szYdStrLoc+"], 전전저장위치["+szYdStrLocHis1+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_FAILURE;
		}else if( intRtnVal == 0 ) {
			szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]를 "+szYD_MTL_ITEM_NM+"공통Table의 현저장위치["+szCurYdStrLoc+"], 전저장위치["+szYdStrLoc+"], 전전저장위치["+szYdStrLocHis1+"] 등록 시 재료가 존재하지 않습니다. - " + intRtnVal;
            ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
            return YdConstant.RETN_CD_NOTEXIST;
		}
    	
    	szMsg = "["+szOperationName+"] 해당하는 재료["+szSTL_NO+"]를 "+szYD_MTL_ITEM_NM+"공통Table의 현저장위치["+szCurYdStrLoc+"], 전저장위치["+szYdStrLoc+"], 전전저장위치["+szYdStrLocHis1+"] 등록 성공 ";
        ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
        
      //------------------------------------------------------------------------------------------------------

        return szRtnMsg ;
    }//end of setYdStrLocToPtComm()
    
    /**
	 * 베드적치가능비교(공통)
	 * @param wrkRec
	 * @param bedRec
	 * @return
	 * @throws JDTOException
	 */
	public static int chkBedStackable(JDTORecord wrkRec, JDTORecord bedRec) throws JDTOException {
		String szLogMsg				= "";
		String szMethodName			= "chkBedStackable";
		String szOperationName		= "베드적치가능비교(공통)";
		String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
		
		int intYD_STK_BED_LYR_MAX	= 0;					//베드정보 - 단MAX
		int intYD_STK_BED_WT_MAX	= 0;					//베드정보 - 총중량
		double dblYD_STK_BED_H_MAX	= 0;					//베드정보 - 총높이
		
		int intYD_MTL_SH			= 0;					//적치된 재료의 총매수
		int intYD_MTL_WT_SUM		= 0;					//적치된 재료의 총중량
		double dblYD_MTL_T_SUM		= 0;					//적치된 재료의 총두께
		
		int intYD_EQP_WRK_SH		= 0;					//크레인작업총매수
		int intYD_EQP_WRK_WT		= 0;					//크레인작업총중량
		double dblYD_EQP_WRK_T		= 0;					//크레인작업총두께
		
		int intYD_STKABLE_BED_LYR	= 0;					//적치가능한 단
		int intYD_STKABLE_BED_WT	= 0;					//적치가능한 중량
		double dblYD_STKABLE_BED_H	= 0;					//적치가능한 높이
		
		int	intYD_BED_ERR_CD		= 0;					//베드적치가능 오류코드
		
		String 	forceDownYn		 = "";
		
		double dblYD_STK_COL_H_MAX	= 0;					//열정보 - 최대높이
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
// 기존 putLog -> putLogNew logId 출력 되게 개선
String logId                            = ydUtils.getJDTOLogId(wrkRec, "T");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

if(ydUtils.isEmpty(logId)) logId = ydUtils.getLogIdNew("T");                    // log id 가 비어있는경우 새로 후판 제품 log id 새로 발번

szLogMsg = "베드적치가능비교(공통)(" + szMethodName + ") 시작";
ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////
		
		intYD_EQP_WRK_SH = ydDaoUtils.paraRecChkNullInt(wrkRec, "YD_EQP_WRK_SH");					//크레인작업총매수
		intYD_EQP_WRK_WT = ydDaoUtils.paraRecChkNullInt(wrkRec, "YD_EQP_WRK_WT");					//크레인작업총중량
		dblYD_EQP_WRK_T = ydDaoUtils.paraRecChkNullDouble(wrkRec, "YD_EQP_WRK_T");					//크레인작업총두께
		
		intYD_STK_BED_LYR_MAX = ydDaoUtils.paraRecChkNullInt(bedRec, "YD_STK_BED_LYR_MAX");			//베드정보 - 단MAX
		intYD_STK_BED_WT_MAX = ydDaoUtils.paraRecChkNullInt(bedRec, "YD_STK_BED_WT_MAX");			//베드정보 - 총중량
		dblYD_STK_BED_H_MAX = ydDaoUtils.paraRecChkNullDouble(bedRec, "YD_STK_BED_H_MAX");			//베드정보 - 총높이
		
		intYD_MTL_SH = ydDaoUtils.paraRecChkNullInt(bedRec, "YD_MTL_SH");							//적치된 재료의 총매수
		intYD_MTL_WT_SUM = ydDaoUtils.paraRecChkNullInt(bedRec, "YD_MTL_WT_SUM");					//적치된 재료의 총중량
		dblYD_MTL_T_SUM = ydDaoUtils.paraRecChkNullDouble(bedRec, "YD_MTL_T_SUM");					//적치된 재료의 총두께
		
		forceDownYn		 = ydDaoUtils.paraRecChkNull(wrkRec, "FORCE_DOWN_YN");
		
		dblYD_STK_COL_H_MAX = ydDaoUtils.paraRecChkNullDouble(bedRec, "YD_STK_COL_H_MAX");					//적치베드의 최대높이
		
		
		szLogMsg = "["+ szOperationName +"] 작업총매수["+intYD_EQP_WRK_SH+"], 작업총중량["+intYD_EQP_WRK_WT+"], 작업총두께["+dblYD_EQP_WRK_T+"]";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		szLogMsg = "["+ szOperationName +"] 베드정보 - 단MAX["+intYD_STK_BED_LYR_MAX+"], 총중량["+intYD_STK_BED_WT_MAX+"], 총높이["+dblYD_STK_BED_H_MAX+"]";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		szLogMsg = "["+ szOperationName +"] 적치된 재료의 총매수["+intYD_MTL_SH+"], 총중량["+intYD_MTL_WT_SUM+"], 총두께["+dblYD_MTL_T_SUM+"]";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		szLogMsg = "["+ szOperationName +"] 강제권하 여부 ["+forceDownYn+"] 적치베드 최대가능높이 ["+dblYD_STK_COL_H_MAX+"]";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		
		if( intYD_STK_BED_LYR_MAX - intYD_MTL_SH > 0 ) {
			intYD_STKABLE_BED_LYR = intYD_MTL_SH + 1;
		}
		
		if( intYD_STK_BED_LYR_MAX >= intYD_MTL_SH + intYD_EQP_WRK_SH )	{
			szLogMsg = "["+ szOperationName +"] 해당하는 적치베드에 적치가능매수 통과";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		}else{
			szLogMsg = "["+ szOperationName +"] 해당하는 적치베드에 적치가능매수 실패 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			
			intYD_BED_ERR_CD	+=		YdConstant.YD_BED_ERR_CD_SH_OVER;
			//return YdConstant.RETN_SH_OVER;
		}
		
		if( intYD_STK_BED_WT_MAX - intYD_MTL_WT_SUM > 0 ) {
			intYD_STKABLE_BED_WT = intYD_STK_BED_WT_MAX - intYD_MTL_WT_SUM;
		}
		
		
		if( intYD_STK_BED_WT_MAX >= intYD_MTL_WT_SUM + intYD_EQP_WRK_WT ) {
			szLogMsg = "["+ szOperationName +"] 해당하는적치베드에 적치가능중량 통과";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.INFO);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		}else{
			szLogMsg = "["+ szOperationName +"] 해당하는 적치베드에 적치가능중량 실패 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			
			intYD_BED_ERR_CD	+=		YdConstant.YD_BED_ERR_CD_WT_OVER;
			//return YdConstant.RETN_WT_OVER;
		}
		
		if( dblYD_STK_BED_H_MAX - dblYD_MTL_T_SUM > 0 ) {
			dblYD_STKABLE_BED_H = dblYD_STK_BED_H_MAX - dblYD_MTL_T_SUM;
		}
		
		if( dblYD_STK_BED_H_MAX >= dblYD_MTL_T_SUM + dblYD_EQP_WRK_T ) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치베드에 적치가능높이 통과";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		} 
		//25.04.28 임진후기사 요청. 강제권하위치 변경 기능 
		//plateYdStkPosMapSet3Gjm.jsp 야드관리 > 통합후판제품창고 > 저장관리 > MAP조회및수정 의 기준과 같이
		//dblYD_STK_COL_H_MAX *1.2 값으로 최대높이 지정 
		else if ("Y".equals(forceDownYn) && dblYD_STK_COL_H_MAX*1.2 >= dblYD_MTL_T_SUM + dblYD_EQP_WRK_T) {
			szLogMsg = "["+ szOperationName +"] 해당하는 적치베드에 적치가능높이 통과는 아니지만, 강제권하이고 권하가능 범위";
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
		} else{
			szLogMsg = "["+ szOperationName +"] 해당하는 적치베드에 적치가능높이 실패 ";
// 2024.09.?? 기존 putLog -> putLogNew logId 출력 되게 개선
//			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.ERROR, logId);
			
			intYD_BED_ERR_CD	+=		YdConstant.YD_BED_ERR_CD_H_OVER;
			//return YdConstant.RETN_H_OVER;
		}
		
		if( intYD_BED_ERR_CD == 0 ) {
			intYD_BED_ERR_CD = YdConstant.YD_BED_STACKABLE;
		}
		
		bedRec.setField("YD_STKABLE_BED_LYR", 	String.valueOf(intYD_STKABLE_BED_LYR));
		bedRec.setField("YD_STKABLE_BED_WT",  	String.valueOf(intYD_STKABLE_BED_WT));
		bedRec.setField("YD_STKABLE_BED_H", 	String.valueOf(dblYD_STKABLE_BED_H));
		bedRec.setField("BED_ERR_CD", 			String.valueOf(intYD_BED_ERR_CD));
		
////////////////////////////////////////////////////////////////////////////////////////
// 2024.09.?? 로그 개선  START
		szLogMsg = "베드적치가능비교(공통)(" + szMethodName + ") 완료";
		ydUtils.putLogNew(szClassName, szMethodName, szLogMsg, YdConstant.INFO, logId);
// 2024.09.?? 로그 개선  END
////////////////////////////////////////////////////////////////////////////////////////

		return intYD_BED_ERR_CD;
	}
	
	/**
	 * 차량스케줄생성
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static String mkCarSch(JDTORecord recPara) throws JDTOException {
		String szLogMsg				= null;
		String szOperationName		= "차량스케줄생성";
		String szMethodName			= "mkCarSch";
		int intRtnVal				= -100;
		String szRtnMsg				= YdConstant.RETN_CD_SUCCESS;
		YdCarSchDao		ydCarSchDao	= new YdCarSchDao();
		
		recPara.setField("YD_CAR_SCH_ID",    ydCarSchDao.getYdCarschId());
		
		//차량스케줄 등록
    	intRtnVal = ydCarSchDao.insYdCarsch(recPara);
		if( intRtnVal <= 0 ){
			szLogMsg="[" + szOperationName + "] 차량스케줄 생성 시 오류발생[반환값 : " + intRtnVal + "]";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		szLogMsg="[" + szOperationName + "] 차량스케줄 생성 완료";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return szRtnMsg;
	}
	
	/**
	 * 차량사양조회
	 * @param szTrnEqpCd
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public static String chkGetCarSpec(String szTrnEqpCd, JDTORecordSet rsResult)throws JDTOException  {
		//차량사양 DAO
		YdCarSpecDao ydCarSpecDao     	= new YdCarSpecDao();
		//리턴값(int)
		int intRtnVal         			= -100;
		//메소드명
		String szMethodName   			= "chkGetCarSpec";
		String szOperationName			= "차량사양조회";
		String szMsg          			= null;
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		
		//레코드 선언
		JDTORecord recPara        = null;	

		try {
			szMsg = "["+szOperationName+"] 메소드 시작 - 운송장비코드(" + szTrnEqpCd + ")";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();
			
			//운송장비코드
			recPara.setField("TRN_EQP_CD", szTrnEqpCd);
			//설비 테이블 조회
			intRtnVal = ydCarSpecDao.getYdCarspec(recPara, rsResult, 2);

			//리턴값 메세지처리
			if(intRtnVal > 1){
				szMsg = "["+szOperationName+"] 운송장비코드(" + szTrnEqpCd + ")에 대한 차량사양이 중복되었습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_DUPLICATE;
			} else if(intRtnVal == 1){

				szMsg = "["+szOperationName+"] 운송장비코드(" + szTrnEqpCd + ")에 대한 차량사양이 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
			} else if(intRtnVal == 0){
				szMsg = "["+szOperationName+"] 운송장비코드(" + szTrnEqpCd + ")에 대한 차량사양이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
			} else if(intRtnVal == -2){
				szMsg = "["+szOperationName+"] 운송장비코드(" + szTrnEqpCd + ")에 대한 차량사양 조회중 parameter error 발생.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
			} else {
				szMsg = "["+szOperationName+"] 운송장비코드(" + szTrnEqpCd + ")에 대한 차량사양 조회중 오류 발생.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				szRtnMsg = YdConstant.RETN_CD_FAILURE;
			}
			
			szMsg = "["+szOperationName+"] 메소드 끝 - 운송장비코드(" + szTrnEqpCd + ")";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 예외메세지 : " + e.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	} //end of chkGetCarSpec
	
	/**
	 * 소재차량도착Point요구모듈호출
	 * @param szTRN_EQP_CD
	 * @param szWLOC_CD
	 * @param szIS_EJB_CALL
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public static String callMatlCarArrPntReq(String szTRN_EQP_CD, String szWLOC_CD, String szIS_EJB_CALL, Object caller) throws Exception {

		return callMatlCarArrPntReq(szTRN_EQP_CD, szWLOC_CD, "E", szIS_EJB_CALL, caller);
	}
	
	/**
	 * 소재차량도착Point요구모듈호출
	 * @param szTRN_EQP_CD
	 * @param szWLOC_CD
	 * @param szIS_EJB_CALL
	 * @param caller
	 * @return
	 * @throws Exception
	 */
	public static String callMatlCarArrPntReq(String szTRN_EQP_CD, String szWLOC_CD, String szTRN_WRK_FULLVOID_GP, String szIS_EJB_CALL, Object caller) throws Exception {
		String szMsg				= null;
		String szMethodName			= "callMatlCarArrPntReq";
		String szOperationName		= "소재차량도착Point요구모듈호출";
		String szCurDate			= YdUtils.getCurDate("yyyyMMddHHmmss");
		
		JDTORecord recPara			= null;
		
		szMsg="["+szOperationName+"] 메소드 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		if( szWLOC_CD == null ) szWLOC_CD = "";
		
		//소재차량정지Point요구 호출
		//record 생성
		recPara = JDTORecordFactory.getInstance().create();
		//JMS TC CODE
		recPara.setField("JMS_TC_CD",               "YDYDJ630");
		//운송장비코드
		recPara.setField("TRN_EQP_CD",              szTRN_EQP_CD);
		//개소코드
		recPara.setField("WLOC_CD",                 szWLOC_CD);
		//운송작업영공구분코드
		recPara.setField("TRN_WRK_FULLVOID_GP",     szTRN_WRK_FULLVOID_GP);
		//포인트요구일시
		recPara.setField("PNT_DMD_DT",              szCurDate);
		
		//YdDelegate ydDelegate = new YdDelegate();
		if( szIS_EJB_CALL.equals("Y") ) {
			EJBConnector ejbConn = null;
			
			ejbConn = new EJBConnector("default", caller);
			ejbConn.trx("CarMvHdSeEJB", "procMatlCarArrPntReq", recPara);
		}else{
			//전문 송신
			ydDelegate.sendMsg(recPara);
		}
		
		szMsg="["+szOperationName+"] 메소드끝 - 호출 성공";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 운송장비코드-차량스케줄조회
	 * @param szTRN_EQP_CD
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public static String getCarSchByTrnEqpCd(String szTRN_EQP_CD, JDTORecordSet rsResult) throws JDTOException {
		String szMsg				= null;
		String szMethodName			= "getCarSchByTrnEqpCd";
		String szOperationName		= "운송장비코드-차량스케줄조회";
		int intRtnVal				= -100;
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		
		YdCarSchDao ydCarSchDao		= new YdCarSchDao();
		
		JDTORecord recPara			= null;
		//운송장비코드로 차량스케줄을 조회한다.
    	
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("TRN_EQP_CD", szTRN_EQP_CD);
    	intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 7);
    	if(intRtnVal > 1) {
			szMsg="["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄이 여러건["+intRtnVal+"] 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_DUPLICATE;
		}else if(intRtnVal == 0) {
			szMsg="["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 조회 시 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if(intRtnVal == -2) {
			szMsg="["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 조회 시 : parameter error";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if(intRtnVal < 0) {
			szMsg="["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄 조회 오류발생 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
	    	szMsg="["+szOperationName+"] 운송장비코드["+szTRN_EQP_CD+"]로 차량스케줄이 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		return szRtnMsg;
	}
	
	/**
	 * 차량스케줄조회
	 * @param szTRN_EQP_CD
	 * @param rsResult
	 * @return
	 * @throws JDTOException
	 */
	public static String getCarSchByCarSchId(String szYD_CAR_SCH_ID, JDTORecordSet rsResult) throws JDTOException {
		String szMsg				= null;
		String szMethodName			= "getCarSchByCarSchId";
		String szOperationName		= "차량스케줄조회";
		int intRtnVal				= -100;
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		
		YdCarSchDao ydCarSchDao		= new YdCarSchDao();
		
		JDTORecord recPara			= null;
		//운송장비코드로 차량스케줄을 조회한다.
    	
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_CAR_SCH_ID", szYD_CAR_SCH_ID);
    	intRtnVal = ydCarSchDao.getYdCarsch(recPara, rsResult, 0);
    	if(intRtnVal > 1) {
			szMsg="["+szOperationName+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄이 여러건["+intRtnVal+"] 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_DUPLICATE;
		}else if(intRtnVal == 0) {
			szMsg="["+szOperationName+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄 조회 시 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if(intRtnVal == -2) {
			szMsg="["+szOperationName+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄 조회 시 : parameter error";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if(intRtnVal < 0) {
			szMsg="["+szOperationName+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄 조회 오류발생 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}else{
	    	szMsg="["+szOperationName+"] 차량스케줄ID["+szYD_CAR_SCH_ID+"]로 차량스케줄이 존재합니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		return szRtnMsg;
	}
	
	/**
	 * 준비스케줄조회
	 * @param recPara
	 * @param rsResult
	 * @param intGp
	 * @return
	 * @throws JDTOException
	 */
	public static String getPreSch(JDTORecord recPara, JDTORecordSet rsResult, int intGp) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getPreSch";
		String szOperationName			= "준비스케줄조회";
		YdPrepSchDao ydPrepSchDao 		= new YdPrepSchDao();
		
		int intRtnVal					= -100;
		
		intRtnVal	= ydPrepSchDao.getYdPrepsch(recPara, rsResult, intGp);
		
		if( intRtnVal == 0 ) {
			szMsg="["+szOperationName+"] 준비스케줄 조회 시 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NOTEXIST;
		}else if(intRtnVal == -2) {
			szMsg="["+szOperationName+"] 준비스케줄 조회 시 : parameter error";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_NO_PARAM;
		}else if( intRtnVal < 0 ) {
			szMsg="["+szOperationName+"] 준비스케줄 조회 시 오류발생 - 반환값 : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			szRtnMsg = YdConstant.RETN_CD_FAILURE;
		}
		
		return szRtnMsg;
	}
	
	/**
	 * 구내운송 상차개시/완료전문송신
	 * @param recLdStart
	 * @param recLdCompl
	 * @return
	 * @throws JDTOException
	 */
	public static String sndLdStartNComplTc(JDTORecord recLdStart, JDTORecord recLdCompl) {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "sndLdStartNComplTc";
		String szOperationName			= "상차개시/완료전문송신";
		String szQueueName				= null;
		
		szMsg="["+szOperationName+"] 메소드 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		try {
			//-----------------------------------------------------------------------------------------------------------------------
			// Delegate 생성
			//-----------------------------------------------------------------------------------------------------------------------
			YdDeleComm deleComm = new YdDeleComm();
			PropertyService propertyService = PropertyService.getInstance();
			szQueueName =propertyService.getProperty("common.properties", "jms.queue.TS_MDB_QUEUE");
			
			//-----------------------------------------------------------------------------------------------------------------------
			// 상차개시전문 송신
			//-----------------------------------------------------------------------------------------------------------------------
			deleComm.jmsQSnder(szQueueName, recLdStart);
			
			szMsg="["+szOperationName+"] 상차개시전문 송신 완료";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			//-----------------------------------------------------------------------------------------------------------------------
			// 상차완료전문송신
			//-----------------------------------------------------------------------------------------------------------------------
			deleComm.jmsQSnder(szQueueName, recLdCompl);
			
			szMsg="["+szOperationName+"] 상차완료전문송신 완료";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			
			szMsg="["+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}catch(Exception ex) {
			szMsg="["+szOperationName+"] 오류발생 - 오류메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
		}
		return szRtnMsg;
	}
	
	
	/**
	 * 적치된 재료상태에 따른 적치단조회
	 * @param recPara
	 * @param rsResult
	 * @param szYD_STK_LYR_MTL_STAT
	 * @return
	 * @throws JDTOException
	 */
	public static String getYdStkLyrWithMtlStat(JDTORecord recPara, JDTORecordSet rsResult, String[] szYD_STK_LYR_MTL_STAT) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStklyr";
		String szOperationName			= "적치된 재료상태에 따른 적치단조회";
		String szRtnMsg2				= null;
		
		String szSTL_NO					= null;
		
		if( szYD_STK_LYR_MTL_STAT == null || szYD_STK_LYR_MTL_STAT.length == 0 ) {
			szMsg="["+szOperationName+"] 야드적치단재료상태(szYD_STK_LYR_MTL_STAT) 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NO_PARAM;
		}
		
		szSTL_NO	= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");
		
		for(int i = 0; i < szYD_STK_LYR_MTL_STAT.length; i++ ) {
			szMsg="["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[i]+"]인 재료["+szSTL_NO+"]를 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recPara.setField("YD_STK_LYR_MTL_STAT", szYD_STK_LYR_MTL_STAT[i]);
			
			szRtnMsg2 = DaoManager.getYdStklyr(recPara, rsResult, 3);
			
			if( szRtnMsg2.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg="["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[i]+"]인 재료["+szSTL_NO+"]가 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				if(rsResult.size() > 1) {
					szMsg="["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[i]+"]인 재료["+szSTL_NO+"]가 중복됩니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					return YdConstant.RETN_CD_DUPLICATE;
				}
				
				break;
			}else if(  szRtnMsg2.equals(YdConstant.RETN_CD_NOTEXIST) ) {
				szMsg="["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[i]+"]인 재료["+szSTL_NO+"]를 조회 시 존재하지 않습니다. ";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if(  szRtnMsg2.equals(YdConstant.RETN_CD_NO_PARAM) ) {
				szMsg="["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[i]+"]인 재료["+szSTL_NO+"]를 조회 시 파라미터 오류입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}else if(  szRtnMsg2.equals(YdConstant.RETN_CD_FAILURE) ) {
				szMsg="["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[i]+"]인 재료["+szSTL_NO+"]를 조회 시 오류발생입니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		}
		
		return szRtnMsg;
	}
	
	
	/**
	 * 작업예약/재료삭제
	 * @param szYD_WBOOK_ID
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String delYdWrkbookNMtl(String szYD_WBOOK_ID, String szFromMethod) throws JDTOException {
		String szRtnMsg					= YdConstant.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStklyr";
		String szOperationName			= "작업예약/재료삭제";
		
		JDTORecord recPara				= null;
		//--------------------------------------------------------------------------------------------
		//	작업예약재료 삭제
		//--------------------------------------------------------------------------------------------
		
		szMsg="["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]재료 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
		recPara.setField("DEL_YN", 				"Y");
		recPara.setField("MODIFIER", 			szFromMethod.length() > 10 ? szFromMethod.substring(0, 10) : szFromMethod);
		
		szRtnMsg = DaoManager.delYdWrkbookmtlByWBookId(recPara);
		
		szMsg="["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]재료 삭제 완료 - 반환메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		//--------------------------------------------------------------------------------------------
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS)  
			&& !szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
			return szRtnMsg;
		}
		
		//--------------------------------------------------------------------------------------------
		//	저장품에서 작업예약ID와 스케줄코드 Clear시킴
		//--------------------------------------------------------------------------------------------
		
		szMsg="["+szOperationName+"] 저장품에서 작업예약ID["+szYD_WBOOK_ID+"]와 스케줄코드 Clear 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		szRtnMsg = DaoManager.updYdStockDelYdWBookId(recPara);
		
		szMsg="["+szOperationName+"] 저장품에서 작업예약ID["+szYD_WBOOK_ID+"]와 스케줄코드 Clear 완료 - 반환메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------------------
		//	작업예약 삭제
		//--------------------------------------------------------------------------------------------
		szMsg="["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		szRtnMsg = DaoManager.updYdWrkbook(recPara, 0);
		
		szMsg="["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"] 삭제 완료 - 반환메세지 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		//--------------------------------------------------------------------------------------------
		
		return szRtnMsg;
	}
	
	/**
	 * 출하차량스케줄/차량Point삭제 기능 - 상차지시 취소 시 호출
	 * @param recPara
	 * @param szCaller
	 * @return
	 * @throws JDTOException
	 */
	public static String delCarSchNCarPointForDist(JDTORecord recPara, String szCaller) throws JDTOException {
		String	szMethodName			= "delCarSchNCarPointForDist";
		String	szOperationName			= "출하차량스케줄/차량Point삭제";
		String	szMsg					= null;
		String 	szRtnMsg				= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recTemp			= null;
		YdStockDAO ydStockDAO = new YdStockDAO();
		String	szTRANS_ORD_DATE		= null;
		String 	szTRANS_ORD_SEQNO		= null;
		String	szYD_CAR_SCH_ID			= null;
		String	szYD_CAR_PROG_STAT		= null;						//차량진행상태
		String	szYD_CARLD_STOP_LOC		= null;						//상차정지위치
		String	szCAR_NO				= null;
		String	szCARD_NO				= null;
		String	szYD_STK_COL_CAR_NO		= null;
		String	szYD_STK_COL_CARD_NO	= null;
		YdPICommDAO ydPICommDAO = new YdPICommDAO();
		
		//--------------------------------------------------------------------------------
		//	운송지시일자, 운송지시순번으로 차량스케줄 조회
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] --------------- 메소드 시작 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

//PIDEV_S
		String szPI_YD	= ydDaoUtils.paraRecChkNull(recPara, "PI_YD");
//		String sApplyYnPI = ydPICommDAO.ApplyYnPI("", szMethodName, "APPPI0", szPI_YD, "*");
		
//		if("PIDEV".equals("PIDEV")) {
			
			String szRtnCd = YdCommonUtils.delCarSchNCarPointForDist_PIDEV(recPara, szCaller);
			return szRtnCd;
////		} 				
//		
//		szTRANS_ORD_DATE			= ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DT");
//		szTRANS_ORD_SEQNO			= ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");
//		
//		szMsg = "["+szOperationName+"] 운송지시일자 :["+szTRANS_ORD_DATE+"] , 운송지시순번["+szTRANS_ORD_SEQNO+"]로 차량스케줄 조회 시작";
//		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
//		recTemp			= JDTORecordFactory.getInstance().create();
//		
//		recTemp.setField("TRANS_ORD_DATE", 			szTRANS_ORD_DATE);
//		recTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
//		
//		szRtnMsg		= DaoManager.getYdCarsch(recTemp, rsResult, 34);
//		
//		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			szMsg = "["+szOperationName+"] 운송지시일자 :["+szTRANS_ORD_DATE+"] , 운송지시순번["+szTRANS_ORD_SEQNO+"]로 차량스케줄 조회 시 오류발생 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			return szRtnMsg;
//		}
//		
//		rsResult.first();
//		recTemp		= rsResult.getRecord();
//		
//		szYD_CAR_SCH_ID			= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_SCH_ID");
//		szYD_CAR_PROG_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_PROG_STAT");
//		szYD_CARLD_STOP_LOC		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CARLD_STOP_LOC");
//		
//		szCAR_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
//		szCARD_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
//		
//		szMsg = "["+szOperationName+"] 운송지시일자 :["+szTRANS_ORD_DATE+"] , 운송지시순번["+szTRANS_ORD_SEQNO+"]로 차량스케줄 조회 완료 - 차량스케줄ID["+szYD_CAR_SCH_ID+"], 차량진행상태["+szYD_CAR_PROG_STAT+"], 상차정지위치["+szYD_CARLD_STOP_LOC+"]";
//		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		//--------------------------------------------------------------------------------
//		
//		//--------------------------------------------------------------------------------
//		//	조회된 차량스케줄로 차량이송재료삭제
//		//--------------------------------------------------------------------------------
//		
//		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 삭제 시작";
//		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		recTemp			= JDTORecordFactory.getInstance().create();
//		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
//		recTemp.setField("DEL_YN", 					"Y");
//		recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller);
//		
//		szRtnMsg		= DaoManager.updYdCarftmvmtl(recTemp, 1);
//		
//		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 삭제 완료";
//		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		//--------------------------------------------------------------------------------
//		
//		//--------------------------------------------------------------------------------
//		//	조회된 차량스케줄로 차량스케줄삭제
//		//--------------------------------------------------------------------------------
//		
//		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시작";
//		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		recTemp			= JDTORecordFactory.getInstance().create();
//		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
//		recTemp.setField("DEL_YN", 					"Y");
//		recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller);
//		
//		szRtnMsg		= DaoManager.updYdCarsch(recTemp, 0);
//		
//		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시 오류발생 - 메세지 : " + szRtnMsg;
//			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//			return szRtnMsg;
//		}
//		
//		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 완료";
//		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		//--------------------------------------------------------------------------------
//		
//		//--------------------------------------------------------------------------------
//		//	차량진행상태에 따른 차량정지위치 Clear 실행 - 상차도착 시에만 Clear
//		//--------------------------------------------------------------------------------
//		if( !szYD_CARLD_STOP_LOC.equals("") ) {
//			
//			//--------------------------------------------------------------------------------
//			//	적치열 조회
//			//--------------------------------------------------------------------------------
//			rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
//			recTemp			= JDTORecordFactory.getInstance().create();
//			recTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
//			
//			szRtnMsg		= DaoManager.getYdStkcol(recTemp, rsResult, 0);
//			
//			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
//				szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//				
//				return szRtnMsg;
//			}
//			
//			rsResult.first();
//			
//			recTemp = rsResult.getRecord();
//			
//			szYD_STK_COL_CAR_NO			= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
//			szYD_STK_COL_CARD_NO		= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
//			
//			
//			//--------------------------------------------------------------------------------
//			if( szYD_STK_COL_CAR_NO.equals(szCAR_NO) && szYD_STK_COL_CARD_NO.equals(szCARD_NO)) {
//			
//				szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 비활성화 시작";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				recTemp			= JDTORecordFactory.getInstance().create();
//				recTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
//				recTemp.setField("YD_CAR_USE_GP", 			YdConstant.YD_CAR_USE_GP_DM);
//				recTemp.setField("YD_STK_COL_ACT_STAT", 	YdConstant.YD_STK_COL_INACTIVE);
//				
//				szRtnMsg		= procCarPosActiveOrInActive(recTemp);
//				
//				szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 비활성화  완료 - 메세지 : " + szRtnMsg;
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//				
//				//--------------------------------------------------------------------------------
//				//	차량포인트통합관리 Clear 실행 - 상차도착 시에만 Clear
//				//--------------------------------------------------------------------------------
//	    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태) 
//				
//				//저장위치로 초기화 하는 경우(출하)
//				String stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointstackcolgpupdatePT";
//				ydStockDAO.requestupdateData(stkQueryId, new Object[]{ "C",szYD_CARLD_STOP_LOC});
//					
//			}else{
//				szMsg = "["+szOperationName+"] 차량스케줄의 차량번호["+szCAR_NO+"]와 카드번호["+szCARD_NO+"]와 적치열의 차량번호["+szYD_STK_COL_CAR_NO+"]와 카드번호["+szYD_STK_COL_CARD_NO+"]가 동일하지 않으므로 차량정지위치["+szYD_CARLD_STOP_LOC+"]를 비활성화하지 않음";
//				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//			}
//		}
//		//--------------------------------------------------------------------------------
//		
//		szMsg = "["+szOperationName+"] --------------- 메소드 끝 ---------------";
//		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//		
//		return szRtnMsg;
	}
	
	/**
	 * 출하차량스케줄/차량Point삭제
	 * @param recPara
	 * @param szCaller
	 * @return
	 * @throws JDTOException
	 */
	public static String delCarSchNCarPointByCarSchId(JDTORecord recPara, String szCaller) throws JDTOException {
		String	szMethodName			= "delCarSchNCarPointByCarSchId";
		String	szOperationName			= "출하차량스케줄/차량Point삭제";
		String	szMsg					= null;
		String 	szRtnMsg				= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recTemp			= null;
		
		String 	szYD_CAR_SCH_ID			= null;
		String	szYD_CAR_PROG_STAT		= null;						//차량진행상태
		String	szYD_CARLD_STOP_LOC		= null;						//상차정지위치
		String	szYD_CARLD_WRK_BOOK_ID	= null;						//야드상차작업예약ID
		String	szWBOOK_CRN_SCH_CHECK	= null;						//작업예약과 크레인스케줄 체크 유무
		String	szCAR_NO				= null;
		String	szCARD_NO				= null;
		String	szYD_STK_COL_CAR_NO		= null;
		String	szYD_STK_COL_CARD_NO	= null;
		
		szMsg = "["+szOperationName+"] --------------- 메소드 시작 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		szYD_CAR_SCH_ID			= ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID");
		szWBOOK_CRN_SCH_CHECK	= ydDaoUtils.paraRecChkNull(recPara, "WBOOK_CRN_SCH_CHECK");
		
		//--------------------------------------------------------------------------------
		//	차량스케줄ID로 차량스케줄 조회
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp			= JDTORecordFactory.getInstance().create();
		
		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
		
		szRtnMsg		= DaoManager.getYdCarsch(recTemp, rsResult, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp		= rsResult.getRecord();
		
		szYD_CAR_PROG_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_PROG_STAT");
		szYD_CARLD_STOP_LOC		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CARLD_STOP_LOC");
		szYD_CARLD_WRK_BOOK_ID	= ydDaoUtils.paraRecChkNull(recTemp, "YD_CARLD_WRK_BOOK_ID");
		
		szCAR_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
		szCARD_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 완료 - 상차작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"], 차량진행상태["+szYD_CAR_PROG_STAT+"], 상차정지위치["+szYD_CARLD_STOP_LOC+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		
		//--------------------------------------------------------------------------------
		//	작업예약과 크레인스케줄의 존재유무 판단하여 존재하면 업무 종료
		//--------------------------------------------------------------------------------
		if( szWBOOK_CRN_SCH_CHECK.equals("Y") ) {
			
			//--------------------------------------------------------------------------------
			//	크레인스케줄의 존재 판단
			//--------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"]로 크레인스케줄 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
			recTemp			= JDTORecordFactory.getInstance().create();
			
			recTemp.setField("YD_WBOOK_ID", 			szYD_CARLD_WRK_BOOK_ID);
			
			szRtnMsg		= DaoManager.getYdCrnsch(recTemp, rsResult, 28);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"]로 크레인스케줄 조회 시 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CRN_EXIST_SCH;
				
			}else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
			
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"]로 크레인스케줄 조회 시 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			}else{
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"]로 크레인스케줄 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}
			//--------------------------------------------------------------------------------
			
			
			//--------------------------------------------------------------------------------
			//	작업예약의 존재유무 판단
			//--------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"] 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
			
			szRtnMsg		= DaoManager.getYdWrkbook(recTemp, rsResult, 0);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"] 조회 시 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CRN_EXIST_SCH;
				
			}else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
			
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"] 조회 시 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			}else{
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}
			//--------------------------------------------------------------------------------
		}
		//--------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------
		//	조회된 차량스케줄로 차량이송재료삭제
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		recTemp			= JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
		recTemp.setField("DEL_YN", 					"Y");
		recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller);
		
		szRtnMsg		= DaoManager.updYdCarftmvmtl(recTemp, 1);
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 삭제 완료";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------
		//	조회된 차량스케줄로 차량스케줄삭제
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		szRtnMsg		= DaoManager.updYdCarsch(recTemp, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 완료";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		
		//--------------------------------------------------------------------------------
		//	차량진행상태에 따른 차량정지위치 Clear 실행 - 상차도착 시에만 Clear
		//--------------------------------------------------------------------------------
			
			if( !szYD_CARLD_STOP_LOC.equals("") ) {
				
				//--------------------------------------------------------------------------------
				//	적치열 조회
				//--------------------------------------------------------------------------------
				
				rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
				recTemp			= JDTORecordFactory.getInstance().create();
				recTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
				
				szRtnMsg		= DaoManager.getYdStkcol(recTemp, rsResult, 0);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					
					return szRtnMsg;
				}
				
				rsResult.first();
				
				recTemp = rsResult.getRecord();
				
				szYD_STK_COL_CAR_NO			= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
				szYD_STK_COL_CARD_NO		= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
				
				
				//--------------------------------------------------------------------------------
				
				if( szYD_STK_COL_CAR_NO.equals(szCAR_NO) && szYD_STK_COL_CARD_NO.equals(szCARD_NO)) {
					
					szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 비활성화 시작";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recTemp			= JDTORecordFactory.getInstance().create();
					recTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
					recTemp.setField("YD_CAR_USE_GP", 			YdConstant.YD_CAR_USE_GP_DM);
					recTemp.setField("YD_STK_COL_ACT_STAT", 	YdConstant.YD_STK_COL_INACTIVE);
					//recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller);
					
					szRtnMsg		= procCarPosActiveOrInActive(recTemp);
					
					szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 비활성화  완료 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
				}else{
					szMsg = "["+szOperationName+"] 차량스케줄의 차량번호["+szCAR_NO+"]와 카드번호["+szCARD_NO+"]와 적치열의 차량번호["+szYD_STK_COL_CAR_NO+"]와 카드번호["+szYD_STK_COL_CARD_NO+"]가 동일하지 않으므로 차량정지위치["+szYD_CARLD_STOP_LOC+"]를 비활성화하지 않음";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
		//}
		
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] --------------- 메소드 끝 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return szRtnMsg;
	}
	
	
	/* ------------------------------------------------------------------------------------------------
	 * ------------------------------------------------------------------------------------------------
	 * ------------------------------------------------------------------------------------------------
	 */
	
	
	/**
	 * 오퍼레이션명 : 연주/후판슬라브야드 이송하차실적 (다른데서 사용할지 몰라서 일단 주석은 안걸었음)
	 * 권오창
	 * 2009.12.04
	 * 
	 * 
	 * @param msgRecord
	 * @param szMethodName
	 * @return
	 * @throws JDTOException
	 */
	public boolean MvCarUdWr(String szSLAB_NO) throws JDTOException { 
		YdStockDao ydStockDao	= new YdStockDao();
		YdDelegate ydDelegate   = new YdDelegate();

		JDTORecordSet rsResult  = null;
		JDTORecord recPara 		= null;
		JDTORecord recGetVal    = null;
		JDTORecord recSend 		= null;

		String szMethodName     = "MvCarUdWrs";		               // 메소드명
		String szOperationName  = "연주/후판슬라브야드 이송하차실적";    // 오퍼레인션명
		String szMsg            = "";
		String szSTL_NO         = "";
		int nRet                = 0;
		
		
		try {
			if(szSLAB_NO.equals("")){
				szMsg = "SLAB_NO가 값이 없습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				return false;
			}
			
			//==============================================================================================================================
			// 주편에 레코드상태가 3인 재료에 대해서 슬라브테이블의 지시행선(SLAB_WO_RT_CD)이 'PA','PB' 이고 재열재구분(REHEAT_SLAB_GP)이 '1','2' (GP : 176)
			// com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getSLABNObyRTCDREHEAT
			//
			// 처리 파라미터 : V_SLAB_NO
			//==============================================================================================================================
	    	rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("SLAB_NO", szSLAB_NO);
			nRet = ydStockDao.getYdStock(recPara, rsResult, 176);
			if(nRet < 0){
				szMsg = "[연주/후판슬라브야드 이송하차실적] 조회시 파리미터 에러";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
			} else if(nRet == 0){
				szMsg = "[연주/후판슬라브야드 이송하차실적] 조회시 조회건수가 없음 [" + nRet + "]";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);								
			} else {
				rsResult.first();
				recGetVal = rsResult.getRecord();
				
				szSTL_NO  = ydDaoUtils.paraRecChkNull(recGetVal, "SLAB_NO");
				
				// 레코드 생성
				recSend = JDTORecordFactory.getInstance().create();
				
				recSend.setField("MSG_ID" , "YDCTJ034");
				recSend.setField("SLAB_NO", szSTL_NO);
				ydDelegate.sendMsg(recSend);
			}
		}catch(Exception ex) {
			szMsg = "[" + szOperationName + "] 예외발생 - 오류메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(ex);
		}
		
		return true;
	}	
	
	
	/**
	 * 2009.12.11
	 * LYK
	 * 
	 * 코일 공통 테이블의 진도코드를 참조해서 행선코드 값을 가져온다.
     *
     * @param  String sStockId	:	저장품ID
     *         String sEquipNo	:	설비번호 (1:C열연HFL, 2:C열연SPM1, 3:C열연SPM2, 4:C열연#1결속대, 5:C열연#2결속대)
     *         String sMoveWork	:	이송작업 (1:A열연정정, 2:B열연정정, 3:C열연정정, 4:임가공이송A사, 5:임가공이송B사, 6:임가공이송C사)
     *
     * @return String sProgCd		:	진도코드 
     * 		   String sYD_AIM_RT_GP	:	행선코드 	
     * @throws  
     */			 
	public static String[] getCoilCurrProgCdSearch(String sStockId, String sEquipNo, String sMoveWork) throws JDTOException	{	
		
		String szMethodName 	= "getCoilCurrProgCdSearch";
		String szMsg 			= "";		
		String sProgCd   		= "";
		String sYD_AIM_RT_GP	= "";
		String sYD_AIM_YD_GP	= "";
		String sYD_AIM_BAY_GP	= "";
		String sReturnGp 		= "";
		String sYD_AIM_RT_GP2   = "";
		String[] rVal 			= new String[4];
		
		int intRtnVal 			= 0;
		
		YdStockDao ydStockDao   = new YdStockDao();
		JDTORecordSet rsResult  = null;
		JDTORecord recPara 		= null;
		JDTORecord recGetVal    = null;
		
    	rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("COIL_NO", sStockId);
				
		// 파라미터로 넘겨온 재료번호로 COIL 공통 읽기
		intRtnVal = ydStockDao.getYdStock(recPara, rsResult, 0);

		if(intRtnVal < 0){
			szMsg = "COIL 공통 조회 에러";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);				
		} else if(intRtnVal == 0){
			szMsg = "COIL 공통  조회시 조회건수가 없음 [" + intRtnVal + "]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);								
		} else {
			rsResult.first();
			recGetVal 	= rsResult.getRecord();
			
			sProgCd  	= ydDaoUtils.paraRecChkNull(recGetVal, "CURR_PROG_CD");			//진도코드	
			sReturnGp  	= ydDaoUtils.paraRecChkNull(recGetVal, "RETURN_GP");			//반납구분
			sYD_AIM_RT_GP2  	= ydDaoUtils.paraRecChkNull(recGetVal, "YD_AIM_RT_GP2"); //정정지시실적의 목표행선(재작업 시)
			
	    	if(sProgCd.equals(YdConstant.PROG_CD_WO_WAIT)){								//지시대기
	    		sYD_AIM_RT_GP = YdConstant.AR_WO_WAIT_B_AIR_COOLING;					//지시대기(B열연공냉재)
	    		sYD_AIM_YD_GP = "B";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_WRK_WAIT)){						//작업대기
	    		if (sEquipNo.equals("1")){
	    			sYD_AIM_RT_GP = YdConstant.AR_WRK_WAIT_C_HFL;						//작업대기(C열연HFL)	
	    		}else if (sEquipNo.equals("2")){
	    			sYD_AIM_RT_GP = YdConstant.AR_WRK_WAIT_C_SPM1;						//작업대기(C열연SPM1)
	    		}else if (sEquipNo.equals("3")){
	    			sYD_AIM_RT_GP = YdConstant.AR_WRK_WAIT_C_SPM2;						//작업대기(C열연SPM2)
	    		}else if (sEquipNo.equals("4")){
	    			sYD_AIM_RT_GP = YdConstant.AR_WRK_WAIT_C_1BINDING;					//작업대기(C열연#1결속대)
	    		}else if (sEquipNo.equals("5")){
	    			sYD_AIM_RT_GP = YdConstant.AR_WRK_WAIT_C_2BINDING;					//작업대기(C열연#2결속대)
	    		}
	    		sYD_AIM_YD_GP = "H";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_FTMV_WRK_WAIT)){				//이송작업대기
	    		if (sMoveWork.equals("1")){
	    			sYD_AIM_RT_GP = YdConstant.AR_INLINE_FTMV_WRK_WAIT_A_CORRCETION;	//재공이송작업대기(A열연정정)
	    		}else if (sMoveWork.equals("2")){
	    			sYD_AIM_RT_GP = YdConstant.AR_INLINE_FTMV_WRK_WAIT_B_CORRCETION;	//재공이송작업대기(B열연정정)
	    		}else if (sMoveWork.equals("3")){
	    			sYD_AIM_RT_GP = YdConstant.AR_INLINE_FTMV_WRK_WAIT_C_CORRCETION;	//재공이송작업대기(C열연정정)
	    		}else if (sMoveWork.equals("4")){
	    			sYD_AIM_RT_GP = YdConstant.AR_INLINE_FTMV_WRK_WAIT_A_RENTPROC;		//재공이송작업대기(임가공이송A사)
	    		}else if (sMoveWork.equals("5")){
	    			sYD_AIM_RT_GP = YdConstant.AR_INLINE_FTMV_WRK_WAIT_B_RENTPROC;		//재공이송작업대기(임가공이송B사)
	    		}else if (sMoveWork.equals("6")){
	    			sYD_AIM_RT_GP = YdConstant.AR_INLINE_FTMV_WRK_WAIT_C_RENTPROC;		//재공이송작업대기(임가공이송C사)
	    		}
	    		sYD_AIM_YD_GP = "H";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_STMP_HOLD)){					//판정보류
	    		if(sYD_AIM_RT_GP2.equals("F4") || sYD_AIM_RT_GP2.equals("F5")) {   		//재작업인 경우 
	    			sYD_AIM_RT_GP = sYD_AIM_RT_GP2					;					//재작업인(C열연정정)
	    		}else {
	    			sYD_AIM_RT_GP = YdConstant.AR_STMP_HOLD_C_CORRCETION;				//판정보류(C열연정정)
	    		}
	    	 
	    		sYD_AIM_YD_GP = "H";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_OVALL_STMP_WAIT)){				//종합판정대기(COIL)
	    		sYD_AIM_RT_GP = YdConstant.AR_OVALL_STMP_WAIT_COIL;						//종합판정대기(COIL)
	    		sYD_AIM_YD_GP = "H";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_RCPT_WAIT)){					//입고대기
	    		sYD_AIM_RT_GP = YdConstant.AR_RCPT_WAIT_COIL;							//입고대기(COIL)
	    		sYD_AIM_YD_GP = "H";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_RETN_WAIT)){					//반납대기
	    		sYD_AIM_RT_GP = YdConstant.AR_RETN_WAIT_COIL;							//반납대기(COIL)
	    		sYD_AIM_YD_GP = "J";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_DIST_WO_WAIT)){					//출하지시대기
	    		sYD_AIM_RT_GP = YdConstant.AR_DIST_WO_WAIT_COIL;						//출하지시대기(COIL)
	    		sYD_AIM_YD_GP = "J";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_TRN_WAIT)){						//운송대기
	    		sYD_AIM_RT_GP = YdConstant.AR_TRN_WAIT_COIL;							//운송대기(COIL)
	    		sYD_AIM_YD_GP = "H";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_DIST_CMPL)){					//출하완료
	    		sYD_AIM_RT_GP = YdConstant.AR_DIST_CMPL_COIL;							//출하완료(COIL)
	    		sYD_AIM_YD_GP = "J";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_TRN_WO_WAIT)){					//운송지시대기
	    		sYD_AIM_RT_GP = YdConstant.AR_TRN_WO_WAIT_COIL;							//운송지시대기(COIL)
	    		sYD_AIM_YD_GP = "H";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_AUCT_TG_PKUP)){					//경매대상선정
	    		sYD_AIM_RT_GP = YdConstant.AR_AUCT_TG_PKUP_COIL;						//경매대상선정(COIL)
	    		sYD_AIM_YD_GP = "J";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_INLINE_MATCH_WAIT)){			//재공충당대기
	    		sYD_AIM_RT_GP = YdConstant.AR_INLINE_MATCH_WAIT_C_CORRCETION;			//재공충당대기(C열연정정)
	    		sYD_AIM_YD_GP = "H";
	    		
	    	} else if(sProgCd.equals(YdConstant.PROG_CD_GDS_MATCH_WAIT)){				//제품충당대기
	    		sYD_AIM_RT_GP = YdConstant.AR_GDS_MATCH_WAIT_COIL;						//제품충당대기(COIL)
	    		sYD_AIM_YD_GP = "J";
	 
	    	} else {
				szMsg = "[COIL야드] 조건에 맞는 행선코드를 찾지 못했습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);	
	    	}
	    	
			/*
			public static final String AR_WH_FTMV_A_WH							= "O1";				//고간이송(A열연창고)
			public static final String AR_WH_FTMV_B_WH							= "O2";				//고간이송(B열연창고)
			public static final String AR_WH_FTMV_C_WH							= "O3";				//고간이송(C열연창고)
			*/

		}
		
    	rVal[0] = sProgCd;			//진도코드
    	//rVal[1] = sYD_AIM_YD_GP;	//야드목표야드구분
    	//rVal[2] = sYD_AIM_BAY_GP;	//야드목표동구분
    	rVal[3] = sYD_AIM_RT_GP;	//야드목표행선구분
    	
		return rVal;
		
	} // End of getCoilCurrProgCdSearch
	
	
	/**
	 * 오퍼레이션명 : 크레인설비상태체크
	 * @param szEqpId
	 * @return
	 * @throws JDTOException
	 */
	public static String checkCrnStat(String szEqpId)throws JDTOException  {
		
		JDTORecord		recPara 	= JDTORecordFactory.getInstance().create();
		
		return checkCrnStat(szEqpId, recPara);
	} 
	
	
	/**
	 * 오퍼레이션명 : 크레인설비상태체크
	 * @param szEqpId
	 * @param recOut
	 * @return
	 * @throws JDTOException
	 */
	public static String checkCrnStat(String szEqpId, JDTORecord recOut)throws JDTOException  {
		String 		szLogMsg			= null;
		String		szMethodName		= "checkCrnStat";
		String		szOperationName		= "크레인설비상태체크";
		String		szRtnMsg			= null;
		
		JDTORecord	recPara				= null;
		JDTORecordSet	rsResult		= null;
		
		String		szYD_EQP_STAT		= null;							//크레인 정상/고장
		String		szYD_EQP_WRK_MODE	= null;							//온라인/오프라인
		
		rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");
		recPara 	= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_EQP_ID", szEqpId);
		
		szRtnMsg 	= DaoManager.getYdEqp(recPara, rsResult, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			return szRtnMsg;
		}else if( rsResult.size() > 1 ) {
			
			szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			szLogMsg	= "["+szOperationName+"] -------------- 해당 크레인["+szEqpId+"]정보가 중복됩니다. --------------";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			
			return YdConstant.RETN_CD_DUPLICATE;
		}else{
			rsResult.first();
			recPara = rsResult.getRecord();
			
			szYD_EQP_STAT		= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			szYD_EQP_WRK_MODE	= ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");
			
			if( szYD_EQP_STAT.equals(YdConstant.YD_EQP_STAT_BREAK) )	{
				
				szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szLogMsg	= "["+szOperationName+"] -------------- 해당 크레인["+szEqpId+"]이 고장상태입니다. --------------";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				return YdConstant.YD_EQP_STAT_BREAK;
			}
			
			else if( szYD_EQP_WRK_MODE.equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE) )	{
				
				szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szLogMsg	= "["+szOperationName+"] -------------- 해당 크레인["+szEqpId+"]이 OFF LINE입니다. --------------";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				szLogMsg	= "["+szOperationName+"] --------------------------------------------------------------";
				ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
				
				return YdConstant.YD_EQP_WRK_MODE_OFF_LINE;
			}
			
			recOut.addRecord(recPara);
		}
		
		return YdConstant.RETN_CD_SUCCESS;
	} 
	
	
	
	
	/**
	 * 로그메세지전송
	 * @param recPara
	 * @return
	 */
	public static String sndLogMsg(JDTORecord recPara) {
		String szLogMsg			= null;
		String szMethodName		= "sndLogMsg";
		String szOperationName	= "로그메세지전송";
		
		szLogMsg			= "["+szOperationName+"] ---------------- 메소드 시작 : JMS 전송시작 ----------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		ydDelegate.sendMsg(recPara);
		
		szLogMsg			= "["+szOperationName+"] ---------------- 메소드 끝 : JMS 전송완료 ----------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * BED로그메세지전송
	 * @param szBedStatus
	 * @param szYD_CRN_SCH_ID
	 * @param szYD_DN_STK_COL_GP
	 * @param szYD_DN_STK_BED_NO
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static String sndLogMsgForBedInfo(String szBedStatus,
			String szYD_CRN_SCH_ID, 
			String szYD_DN_STK_COL_GP,
			String szYD_DN_STK_BED_NO,
			JDTORecord recPara) throws JDTOException {
		String szLogMsg			= null;
		String szMethodName		= "sndLogMsgForBedInfo";
		String szOperationName	= "BED로그메세지전송";
		
		szLogMsg			= "["+szOperationName+"] ---------------- 메소드 시작 : JMS 전송시작 ----------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		String szMSG_CONTENTS			= "";
		
		if( szBedStatus.equals(YdConstant.RETN_BED_INACT) ) {
			szMSG_CONTENTS			= "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]가 비활성상태입니다.";
		}else if( szBedStatus.equals(YdConstant.RETN_BED_WHIO_NOT_IN) ) {
			szMSG_CONTENTS			= "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]가 입고불가능상태입니다.";
		}else if( szBedStatus.equals(YdConstant.RETN_BED_UN_WAIT) ) {
			szMSG_CONTENTS			= "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]가 권상대기상태입니다.";
		}else{
			szMSG_CONTENTS			= "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]적치가능한 지 비교 시 오류발생["+szBedStatus+"]";
		}
		
		recPara.setField("MSG_CONTENTS", 			szMSG_CONTENTS);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		ydDelegate.sendMsg(recPara);
		
		szLogMsg			= "["+szOperationName+"] ---------------- 메소드 끝 : JMS 전송완료 ----------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	
	/**
	 * BED상태로그메세지전송
	 * @param intYD_BED_ERR_CD
	 * @param szYD_CRN_SCH_ID
	 * @param szYD_DN_STK_COL_GP
	 * @param szYD_DN_STK_BED_NO
	 * @param recPara
	 * @return
	 * @throws JDTOException
	 */
	public static String sndLogMsgForBedStatusInfo(int intYD_BED_ERR_CD,
			String szYD_CRN_SCH_ID, 
			String szYD_DN_STK_COL_GP,
			String szYD_DN_STK_BED_NO,
			JDTORecord recPara) throws JDTOException {
		String szLogMsg			= null;
		String szMethodName		= "sndLogMsgForBedStatusInfo";
		String szOperationName	= "BED상태로그메세지전송";
		
		szLogMsg			= "["+szOperationName+"] ---------------- 메소드 시작 : JMS 전송시작 ----------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		int intERR_CD			= intYD_BED_ERR_CD;
		
		StringBuffer szSTATUS		= new StringBuffer();
	
		String szMSG_CONTENTS			= "";
		
		if( intERR_CD >= YdConstant.YD_BED_ERR_CD_H_OVER ) {
			//해당하는 적치베드에 적치가능높이 OVER
			intERR_CD	-= YdConstant.YD_BED_ERR_CD_H_OVER;
			
			szSTATUS.append("적치가능높이 OVER");
		}
		
		if( intERR_CD >= YdConstant.YD_BED_ERR_CD_WT_OVER ) {
			//해당하는 적치베드에 적치가능중량 OVER
			intERR_CD	-= YdConstant.YD_BED_ERR_CD_WT_OVER;
			
			if( szSTATUS.length() > 0 ) szSTATUS.append(", ");
			
			szSTATUS.append("적치가능중량 OVER");
		}
		
		if( intERR_CD == YdConstant.YD_BED_ERR_CD_SH_OVER ) {
			//해당하는 적치베드에 적치가능매수 OVER
			
			if( szSTATUS.length() > 0 ) szSTATUS.append(", ");
			
			szSTATUS.append("적치가능매수 OVER");
		}
		
		szMSG_CONTENTS			= "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치불가능합니다 - " + szSTATUS.toString();
		
		recPara.setField("MSG_CONTENTS", 			szMSG_CONTENTS);
		
		ydUtils.displayRecord(szOperationName, recPara);
		
		ydDelegate.sendMsg(recPara);
		
		szLogMsg			= "["+szOperationName+"] ---------------- 메소드 끝 : JMS 전송완료 ----------------";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 크레인스케줄호출[작업예약ID]
	 * @param szYD_WBOOK_ID
	 * @return
	 * @throws JDTOException
	 */
	public static String callCrnSchByWbookId(String szYD_WBOOK_ID, String szBUFFER_YN) throws JDTOException {
		String szLogMsg				= null;
		String szMethodName			= "callCrnSchByWbookId";
		String szOperationName		= "크레인스케줄호출[작업예약ID]";
		String szRtnMsg				= null;
		
		JDTORecord		recPara		= null;
		JDTORecordSet	rsResult	= null;
		
		String szYD_SCH_CD			= null;
		String szYD_EQP_ID			= null;
		int intRtnVal				= 0;
		
		szLogMsg			= "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		rsResult 			= JDTORecordFactory.getInstance().createRecordSet("");
		recPara				= JDTORecordFactory.getInstance().create();
		recPara.setField("YD_WBOOK_ID", szYD_WBOOK_ID);
		
		szRtnMsg			= DaoManager.getYdWrkbook(recPara, rsResult, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			
			szLogMsg			= "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약 조회 시 오류발생 - 반환값 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			return szRtnMsg;
		}
		
		szLogMsg			= "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"]로 작업예약 조회 완료 - 반환값 : " + szRtnMsg;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		rsResult.first();
		recPara			= rsResult.getRecord();
		
		szYD_SCH_CD		= ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_CD");
		
		szLogMsg			= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]로 스케줄기준 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		recPara 		= JDTORecordFactory.getInstance().create();
		intRtnVal 		= YdCommonUtils.getCrnInfoByCrnSchRule(szYD_SCH_CD, recPara);
		
		if( intRtnVal < 0 ) {
			szLogMsg="["+szOperationName+"] 스케줄 기준 조회 Error Code : " + intRtnVal;
			ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		szLogMsg	= "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]로 스케줄기준 조회 완료 - 반환값 : " + intRtnVal;
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
    	
    	//크레인설비ID
		szYD_EQP_ID = ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
		
		recPara 	= JDTORecordFactory.getInstance().create();
		
		if( szBUFFER_YN.equals("Y") ) {
			recPara.setField("MSG_ID",    YdConstant.YDYDJ701);
			recPara.setField(YdConstant.BUFFER_TC_CD, "YDYDJ500");
		}else{
			recPara.setField("MSG_ID",    			"YDYDJ500");
		}
		recPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
		recPara.setField("YD_EQP_ID", 			szYD_EQP_ID);
		recPara.setField("YD_WBOOK_ID", 		szYD_WBOOK_ID);
		
		szLogMsg			= "["+szOperationName+"] 크레인스케줄 JMS 호출 시작 - 스케줄코드["+szYD_SCH_CD+"], 크레인설비ID["+szYD_EQP_ID+"], 작업예약ID["+szYD_WBOOK_ID+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		ydDelegate.sendMsg(recPara);
		
		szLogMsg			= "["+szOperationName+"] 크레인스케줄 JMS 호출 완료 - 스케줄코드["+szYD_SCH_CD+"], 크레인설비ID["+szYD_EQP_ID+"], 작업예약ID["+szYD_WBOOK_ID+"]";
		ydUtils.putLog(szClassName, szMethodName, szLogMsg, YdConstant.DEBUG);
		
		return YdConstant.RETN_CD_SUCCESS;
	}
	
	/**
	 * 상차출발시스케줄 요청여부반환
	 * @param szYD_EQP_ID			대차호기
	 * @return
	 */
	public static String getLdStartSchReqYN(String szYD_EQP_ID) {
		String szOperationName		= "상차출발시스케줄 요청여부반환";
		String szMethodName			= "getLdStartSchReqYN";
		String szMsg				= null;
		boolean bBreRule			= false;
    	JDTORecord jdtoRcd			= JDTORecordFactory.getInstance().create();
    	
    	szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에 대한 상차출발시스케줄 요청여부를 BRE Rule로부터 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		bBreRule			= GetBreRule1.getYDB181(szYD_EQP_ID.substring(4), "E", jdtoRcd);
		
		szMsg="["+szOperationName+"] 대차["+szYD_EQP_ID+"]에대한 상차출발시스케줄기동구분를 BRE Rule로부터 조회 완료 - 반환값 : " + bBreRule;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	
    	String szLD_START_SCH_REQ_YN			= "N";
    	String szYD_SCH_REQ_GP					= "";
    	
    	if( bBreRule ) {
    		try {
    			
    			ydUtils.displayRecord(szOperationName, jdtoRcd);
    			
    			szYD_SCH_REQ_GP					= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_SCH_REQ_GP");
    			
    			szMsg="["+szOperationName+"] 대차["+szYD_EQP_ID+"]에대한 상차출발 시 야드스케줄요청구분값["+szYD_SCH_REQ_GP+"]";
    			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			//--------------------------------------------------------------------------------------
    			//	대차상차작업 시 맵활성화 및 크레인스케쥴기동 기준 - BRE Rule에 등록된 기준
    			//	5 - 공차출발 시
    			//	6 - 공차도착 시
    			//	수정일 : 1. 2010.02.24
    			//--------------------------------------------------------------------------------------
    			if( szYD_SCH_REQ_GP.equals("5")) {						//공차출발 시
    				szLD_START_SCH_REQ_YN			= "Y";
    			}else if( szYD_SCH_REQ_GP.equals("6")) {				//공차도착 시
    				szLD_START_SCH_REQ_YN			= "N";
    			}else{													//5와 6이외의 값은 공차도착으로 처리
    				szLD_START_SCH_REQ_YN			= "N";
    			}
    			//--------------------------------------------------------------------------------------
    		}catch(JDTOException ex) {
    			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 상차출발시스케줄 요청여부를 JDTORecord에서 추출 시 오류발생 - 기본값으로 설정[N]";
    			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
    			
    			szLD_START_SCH_REQ_YN			= "N";
    		}
    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 상차출발시스케줄 요청여부를 BRE Rule로부터 조회된 결과 - " + szLD_START_SCH_REQ_YN;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	}else{
    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 상차출발시스케줄 요청여부를 BRE Rule로부터 조회 시 오류발생 - 기본값사용[" + szLD_START_SCH_REQ_YN + "]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	}
    	
    	return szLD_START_SCH_REQ_YN;
	}
	
	/**
	 * 하차출발시스케줄 요청여부반환
	 * @param szYD_EQP_ID			대차호기
	 * @return
	 */
	public static String getUlStartSchReqYN(String szYD_EQP_ID) {
		String szOperationName		= "하차출발시스케줄 요청여부반환";
		String szMethodName			= "getUlStartSchReqYN";
		String szMsg				= null;
		boolean bBreRule			= false;
    	JDTORecord jdtoRcd			= JDTORecordFactory.getInstance().create();
    	
    	szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에 대한 하차출발시스케줄 요청여부를 BRE Rule로부터 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	
		bBreRule			= GetBreRule1.getYDB181(szYD_EQP_ID.substring(4), "F", jdtoRcd);
		
		szMsg="["+szOperationName+"] 대차["+szYD_EQP_ID+"]에대한 하차출발시스케줄기동구분를 BRE Rule로부터 조회 완료 - 반환값 : " + bBreRule;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
    	String szUL_START_SCH_REQ_YN			= "N";
    	String szYD_SCH_REQ_GP					= "";
    	
    	if( bBreRule ) {
    		try {
    			
    			ydUtils.displayRecord(szOperationName, jdtoRcd);
    			
    			szYD_SCH_REQ_GP			= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_SCH_REQ_GP");
    			
    			szMsg="["+szOperationName+"] 대차["+szYD_EQP_ID+"]에대한 하차출발 시 야드스케줄요청구분값["+szYD_SCH_REQ_GP+"]";
    			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    			
    			//--------------------------------------------------------------------------------------
    			//	대차상차작업 시 맵활성화 및 크레인스케쥴기동 기준 - BRE Rule에 등록된 기준
    			//	B - 영차출발 시
    			//	C - 영차도착 시
    			//	수정일 : 1. 2010.02.24
    			//--------------------------------------------------------------------------------------
    			if( szYD_SCH_REQ_GP.equals("B")) {						//영차출발 시
    				szUL_START_SCH_REQ_YN			= "Y";
    			}else if( szYD_SCH_REQ_GP.equals("C")) {				//영차도착 시
    				szUL_START_SCH_REQ_YN			= "N";
    			}else{													//B와 C이외의 값은 영차도착으로 처리
    				szUL_START_SCH_REQ_YN			= "N";
    			}
    			//--------------------------------------------------------------------------------------
    			
    		}catch(JDTOException ex) {
    			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 하차출발시스케줄 요청여부를 JDTORecord에서 추출 시 오류발생 - 기본값으로 설정[N]";
    			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
    			
    			szUL_START_SCH_REQ_YN			= "N";
    		}
    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 하차출발시스케줄 요청여부를 BRE Rule로부터 조회된 결과 - " + szUL_START_SCH_REQ_YN;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	}else{
    		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 하차출발시스케줄 요청여부를 BRE Rule로부터 조회 시 오류발생 - 기본값사용[" + szUL_START_SCH_REQ_YN + "]";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
    	}
    	
    	return szUL_START_SCH_REQ_YN;
	}
	
	
	/**
	 * 대차작업지정기준조회
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
	 */
	public static String[] getTCarWrkStdRule(String szYD_EQP_ID) {
		String szOperationName		= "대차작업지정기준조회";
		String szMethodName			= "getTCarWrkStdRule";
		String szMsg				= null;
		boolean bBreRule			= false;
    	JDTORecord jdtoRcd			= JDTORecordFactory.getInstance().create();
    	
    	String szUSAGE_YN			= null;					//사용구분
    	String szWORK_GP			= null;					//작업구분
    	String szYD_CARLD_BAY_GP	= null;					//상차동
    	String szYD_CARUD_BAY_GP	= null;					//하차동
    	
		String[] szRule 			= null;
		
		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에 대한 대차작업지정기준를 BRE Rule로부터 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		bBreRule				= GetBreRule1.getYDB182(szYD_EQP_ID.substring(4), jdtoRcd);
		
		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 대차작업지정기준를 BRE Rule로부터 조회 완료 - 반환값 : " + bBreRule;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		if( bBreRule ) {
			try {
    			
    			ydUtils.displayRecord(szOperationName, jdtoRcd);
    			
    			szUSAGE_YN				= ydDaoUtils.paraRecChkNull(jdtoRcd, "USAGE_YN");
    			szWORK_GP				= ydDaoUtils.paraRecChkNull(jdtoRcd, "WORK_GP");
    			szYD_CARLD_BAY_GP		= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_CARLD_BAY_GP");
    			szYD_CARUD_BAY_GP		= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_CARUD_BAY_GP");
    			
    			
    			
    		}catch(JDTOException ex) {
    			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 대차작업지정기준를 BRE Rule로부터 조회 시 오류발생 - 기본값으로 설정[N]";
    			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
    			
    			szUSAGE_YN			= "N";
    			szWORK_GP			= "";
    			szYD_CARLD_BAY_GP	= "";
    			szYD_CARUD_BAY_GP	= "";
    		}
    		
		}else{
			szUSAGE_YN			= "N";
			szWORK_GP			= "";
			szYD_CARLD_BAY_GP	= "";
			szYD_CARUD_BAY_GP	= "";
		}
		
		szRule			= new String[]{szUSAGE_YN, szWORK_GP, szYD_CARLD_BAY_GP, szYD_CARUD_BAY_GP};
		
		return szRule;
	}
	
	
	/**
	 * 대차작업지정기준조회_코일
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
	 */
	public static String[] getTCarWrkStdRule_Coil(String szYD_EQP_ID) {
		String szOperationName		= "대차작업지정기준조회(코일야드)";
		String szMethodName			= "getTCarWrkStdRule_Coil";
		String szMsg				= null;
		boolean bBreRule			= false;
    	JDTORecord jdtoRcd			= JDTORecordFactory.getInstance().create();
    	
    	String szUSAGE_YN			= null;					//사용구분
    	String szWORK_GP			= null;					//작업구분
    	String szYD_CARLD_BAY_GP	= null;					//상차동
    	String szYD_CARUD_BAY_GP	= null;					//하차동
    	
		String[] szRule 			= null;
		
		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에 대한 대차작업지정기준를 BRE Rule로부터 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		bBreRule				= GetBreRule4.getYDB430(szYD_EQP_ID.substring(4), jdtoRcd);
		
		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 대차작업지정기준를 BRE Rule로부터 조회 완료 - 반환값 : " + bBreRule;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		if( bBreRule ) {
			try {
    			
    			ydUtils.displayRecord(szOperationName, jdtoRcd);
    			
    			szUSAGE_YN				= ydDaoUtils.paraRecChkNull(jdtoRcd, "USAGE_YN");
    			szWORK_GP				= ydDaoUtils.paraRecChkNull(jdtoRcd, "WORK_GP");
    			szYD_CARLD_BAY_GP		= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_CARLD_BAY_GP");
    			szYD_CARUD_BAY_GP		= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_CARUD_BAY_GP");
    			
    			
    			
    		}catch(JDTOException ex) {
    			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 대차작업지정기준를 BRE Rule로부터 조회 시 오류발생 - 기본값으로 설정[N]";
    			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
    			
    			szUSAGE_YN			= "N";
    			szWORK_GP			= "";
    			szYD_CARLD_BAY_GP	= "";
    			szYD_CARUD_BAY_GP	= "";
    		}
    		
		}else{
			szUSAGE_YN			= "N";
			szWORK_GP			= "";
			szYD_CARLD_BAY_GP	= "";
			szYD_CARUD_BAY_GP	= "";
		}
		
		szRule			= new String[]{szUSAGE_YN, szWORK_GP, szYD_CARLD_BAY_GP, szYD_CARUD_BAY_GP};
		
		return szRule;
	}

	
//SJH	
	/**
	 * 대차작업지정기준조회1
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
	 */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr) {
		return setWiseGridCombo(obj, hTitle, comboStrArr, 1, "N");
	}
	
	/**
 	 * 대차작업지정기준조회_코일1
	 * @param szYD_EQP_ID			대차호기
	 * @return String[]
    */
	public static String setWiseGridCombo(String obj, String hTitle, String[][] comboStrArr, int cdVal, String headTextYn) {
		String comboStr = "";
		
		if(comboStrArr != null) {
			
			if("Y".equals(headTextYn)) {
				comboStr = obj + ".AddComboListValue('" + hTitle + "', '', '');";
			}
			
			if(cdVal == 0 || cdVal == 1) {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + comboStrArr[cdVal][ii] + "', '" + comboStrArr[0][ii] + "');";
				}
			}else if(cdVal == 2) { //YD에 쓸수 있게 코드/코드명 형식으로 출력				
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
									"[" +comboStrArr[0][ii] + "] " + comboStrArr[1][ii] + "', '" + comboStrArr[0][ii] + "');";
				} 
			}else {
				for(int ii=0; ii < comboStrArr[0].length; ii++) {
					comboStr += obj + ".AddComboListValue('" + hTitle + "', '" + 
										comboStrArr[0][ii] + " (" + comboStrArr[1][ii] + ")', '" + comboStrArr[0][ii] + "');";
				}
			}
		}
		
		return comboStr;
	}
	
	
	/**
	 * 후판제품창고의 BOOK-OUT LOC의 가상베드정보를 SHIFT시키는 메소드(E,F)
	 * @param szYD_BOOK_OUT_LOC
	 * @param startBedNo
	 * @return
	 * @throws JDTOException
	 */
	public static String procShiftBedInfoForBookoutLocNew(String szYD_BOOK_OUT_LOC) throws JDTOException {
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "procShiftBedInfoForBookoutLocNew";
		String szOperationName			= "가상베드정보SHIFT";
		String szMsg					= "";
		int intRtnVal					= -100;
		
		JDTORecord	recPara				= null;
		JDTORecordSet outRecSet			= null;
		
		YdStkBedDao ydStkBedDao	= new YdStkBedDao();
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		
		String szPREV_YD_STK_COL_GP		= null;
		String szPREV_YD_STK_BED_NO		= null;
		
		String szStartBedNo				= szYD_BOOK_OUT_LOC.substring(6);
		
		String szYD_STK_BED_CHK         = "";
		
		int startBedNo					= 0;

		szMsg="["+szOperationName+"] 메소드 시작 - BOOK-OUT LOC["+szYD_BOOK_OUT_LOC+"] 시작 베드 번호["+szStartBedNo+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

		try {		
			szYD_STK_COL_GP 	= szYD_BOOK_OUT_LOC.substring(0, 6);
			szYD_STK_BED_NO 	= szYD_BOOK_OUT_LOC.substring(6, 7) + "_";			//LIKE 검색을 위해서
			szYD_STK_BED_CHK 	= szYD_BOOK_OUT_LOC.substring(7, 8); 				//E,F 동 검색
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara =  JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedByColBedLike*/
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 23);
			
			if(szYD_STK_BED_CHK.equals("A")) { 
				startBedNo = 10;
			} else if (szYD_STK_BED_CHK.equals("B")){
				startBedNo = 11;
			} else if (szYD_STK_BED_CHK.equals("C")){
				startBedNo = 12;
			} else if (szYD_STK_BED_CHK.equals("D")){
				startBedNo = 13;
			} else if (szYD_STK_BED_CHK.equals("E")){
				startBedNo = 14;
			} else if (szYD_STK_BED_CHK.equals("F")){
				startBedNo = 15;
			} else if (szYD_STK_BED_CHK.equals("G")){
				startBedNo = 16;
			} else if (szYD_STK_BED_CHK.equals("H")){
				startBedNo = 17;
			} else {
				startBedNo = Integer.parseInt(szYD_STK_BED_CHK);
			}
			
			if( intRtnVal > 0 ) {
				szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]로 베드정보 조회 성공 - 대상재건수 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
				//New Version--------------------------------------------------------------------------
				for(int i = 1 + startBedNo; i <= outRecSet.size(); i++ ) {
					/*
					 * 선행 베드의 적치열, 적치베드, 적치단 정보 추출
					 */
					outRecSet.absolute(i);
					recPara = outRecSet.getRecord();
	
					szPREV_YD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szPREV_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
					
					if( i < outRecSet.size() ) {
					
						outRecSet.absolute(i + 1);
						recPara = outRecSet.getRecord();
						
						/*
						 * 후행 베드의 적치열, 적치베드, 적치단 정보 추출
						 */
						szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
						szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
						
						recPara.setField("FROM_STK_COL_GP",	szYD_STK_COL_GP);		//FROM 가상버버 번지 (NEXT)
						recPara.setField("TO_STK_COL_GP",	szPREV_YD_STK_COL_GP);	//TO 가상버버 번지 (NEXT)
						recPara.setField("FROM_BED_NO",		szYD_STK_BED_NO);  		//FROM 가상버퍼 번지(NEXT)
						recPara.setField("TO_BED_NO",		szPREV_YD_STK_BED_NO); 	//TO 가상버퍼 번지
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");
						
						if(intRtnVal < 1) {
							szMsg = "[가상베드정보SHIFT] BOOK-OUT위치 Update ERROR 위치 - 반환값 : " + intRtnVal;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}								
						
					}else{
						
						//가상버퍼의 마지막  부분 처리
						recPara.setField("FROM_STK_COL_GP",   szYD_STK_COL_GP); 
						recPara.setField("FROM_BED_NO",       szYD_STK_BED_NO); //마지막 가상버퍼 번지 (Clear 될 번지) 
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");
						
						if(intRtnVal < 1) {
							szMsg = "[가상베드정보SHIFT] 대기베드 재료정보 Clear Update ERROR 위치: " + intRtnVal;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}									
					}
				}
				//-------------------------------------------------------------------------------------
				
			}else{
				szMsg="["+szOperationName+"] 해당적치열["+szYD_STK_COL_GP+"]의 베드정보가 존재하지 않으므로 SHIFT 처리를 하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
   		}catch(JDTOException ex) {
			szMsg = "[" + szOperationName + "] 예외발생 - 오류메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(ex);  			
   		}	
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		return szRtnMsg;
	}
	
	/**
	 * 2후판제품창고의 BOOK-OUT LOC의 가상베드정보를 SHIFT시키는 메소드
	 * @param szYD_BOOK_OUT_LOC
	 * @param startBedNo
	 * @return
	 * @throws JDTOException
	 */
	public static String procShiftBedInfoForBookoutLoc3G(String szYD_BOOK_OUT_LOC) throws JDTOException {
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "procShiftBedInfoForBookoutLoc3G";
		String szOperationName			= "가상베드정보SHIFT";
		String szMsg					= "";
		int intRtnVal					= -100;
		
		JDTORecord	recPara				= null;
		JDTORecordSet outRecSet			= null;
		JDTORecordSet outRecSet1			= null;
		
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STRLOC_GRP_GP		= null;
		
		//String szPREV_YD_STK_COL_GP		= null;
		String szNEXT_YD_STK_BED_NO		= null;
		
		
		String szStartBedNo				= szYD_BOOK_OUT_LOC.substring(6);
		
		szMsg="["+szOperationName+"] 메소드 시작 - BOOK-OUT LOC["+szYD_BOOK_OUT_LOC+"] 시작 베드 번호["+szStartBedNo+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

		try {		
			//BOOK-OUT 위치를 적치열구분과 Bed번호로 분리
			szYD_STK_COL_GP 	= szYD_BOOK_OUT_LOC.substring(0, 6); 
			szYD_STK_BED_NO 	= szYD_BOOK_OUT_LOC.substring(6, 8);			
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			outRecSet1 = JDTORecordFactory.getInstance().createRecordSet("");
			recPara =  JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			
			//2후판 가상베드 번호는 R/T 의 베드번호가 10, 20 처럼 0 으로 끝나면 1A,1B...1M 까지 배정되고
			//                                15, 25 처럼 5 로 끝나면 1N,1O..1Z 까지 배정된다.
			//그리고 각 가상BED 들은 그글이 속한 R/T 베드번호와 같은 번호를 YD_STRLOC_GRP_GP 에 설정해서 그룹핑된다.
			// EX) SELECT * FROM TB_YD_STKBED
			//     WHERE YD_STK_COL_GP = 'TERTRA'
			//     AND   YD_STRLOC_GRP_GP = '65'
			//     ORDER BY YD_STK_COL_GP, YD_STRLOC_GRP_GP, YD_STK_BED_NO
			
			/*com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0046*/
			intRtnVal = commDao.select(recPara, outRecSet1, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0046");				
			
			outRecSet1.first();
			recPara = outRecSet1.getRecord();
			szYD_STRLOC_GRP_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STRLOC_GRP_GP");

			
			recPara.setField("YD_STK_COL_GP"	, szYD_STK_COL_GP);
			recPara.setField("YD_STRLOC_GRP_GP"	, szYD_STRLOC_GRP_GP);
			recPara.setField("YD_STK_BED_NO"	, szYD_STK_BED_NO);
			
			/*com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0044*/
			intRtnVal = commDao.select(recPara, outRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0044");	

			if( intRtnVal > 0 ) {
				
				
				szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]로 베드정보 조회 성공 - 대상재건수 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				for(int i = 1; i <= outRecSet.size(); i++ ) {

					outRecSet.absolute(i);
					recPara = outRecSet.getRecord();
	
					szYD_STK_COL_GP 		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO 		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");							
					szNEXT_YD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "NEXT_YD_STK_BED_NO");
					

					
					if(!"".equals(szNEXT_YD_STK_BED_NO)) {
						//가상버퍼의 내용들을 SHIFT 처리
						
						//이때 가상버퍼가 아닌 RT는 대상에서 제외해야한다. szYD_STRLOC_GRP_GP 와 szYD_STK_BED_NO 가 같다면 가상버퍼가 아니다.
						if(szYD_STK_BED_NO.equals(szYD_STRLOC_GRP_GP)) {
							continue;
						}
						
						recPara.setField("FROM_STK_COL_GP",	szYD_STK_COL_GP);		//FROM,TO 적치열구분은 동일 하다.
						recPara.setField("TO_STK_COL_GP",	szYD_STK_COL_GP);
						recPara.setField("FROM_BED_NO",		szNEXT_YD_STK_BED_NO);  //FROM 가상버퍼 번지(NEXT)
						recPara.setField("TO_BED_NO",		szYD_STK_BED_NO); 		//TO 가상버퍼 번지
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");
						
						if(intRtnVal < 1) {
							szMsg = "[가상베드정보SHIFT] BOOK-OUT위치 Update ERROR 위치 - 반환값 : " + intRtnVal;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}	
						
					} else {
						//가상버퍼의 마지막  부분 처리
						recPara.setField("FROM_STK_COL_GP",   szYD_STK_COL_GP);
						recPara.setField("FROM_BED_NO",       szYD_STK_BED_NO); //마지막 가상버퍼 번지 (Clear 될 번지) 
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");
						
						if(intRtnVal < 1) {
							szMsg = "[가상베드정보SHIFT] 대기베드 재료정보 Clear Update ERROR 위치: " + intRtnVal;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}								
					}
				}
				
			}else{
				szMsg="["+szOperationName+"] 해당적치열["+szYD_STK_COL_GP+"]의 베드정보가 존재하지 않으므로 SHIFT 처리를 하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
   		}catch(JDTOException ex) {
			szMsg = "[" + szOperationName + "] 예외발생 - 오류메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(ex);  			
   		}	
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		return szRtnMsg;
	}
	
	/**
	 * 후판제품창고의 BOOK-OUT LOC의 가상베드정보를 역SHIFT시키는 메소드(E,F)
	 * @param szYD_BOOK_OUT_LOC
	 * @param startBedNo
	 * @return
	 * @throws JDTOException
	 */
	public static String procReverseShiftBedInfoForBookoutLocNew(String szYD_BOOK_OUT_LOC) throws JDTOException {
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "procReverseShiftBedInfoForBookoutLocNew";
		String szOperationName			= "가상베드정보역SHIFT";
		String szMsg					= "";
		int intRtnVal					= -100;
		int intRtnVal1					= -100;
		
		JDTORecord	recPara				= null;
		JDTORecord	recTemp				= null;
		JDTORecordSet outRecSet			= null;
		JDTORecordSet rsOut				= null;
		JDTORecord inRecord				= null;
		
		YdStkBedDao ydStkBedDao	= new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_NO			= null;
		
		String szPREV_YD_STK_COL_GP		= null;
		String szPREV_YD_STK_BED_NO		= null;
		String szSTL_NO					= null;
		String szYD_STK_LYR_MTL_STAT	= null;
		String szYD_STK_BED_CHK         = "";
		String szStartBedNo				= szYD_BOOK_OUT_LOC.substring(6);
		int startBedNo                  = 0;
		
		
		szMsg="["+szOperationName+"] 메소드 시작 - BOOK-OUT LOC["+szYD_BOOK_OUT_LOC+"] 시작 베드 번호["+szStartBedNo+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

		try {
			szYD_STK_COL_GP 	= szYD_BOOK_OUT_LOC.substring(0, 6);
			szYD_STK_BED_NO 	= szYD_BOOK_OUT_LOC.substring(6, 7) + "_";			//LIKE 검색을 위해서
			szYD_STK_BED_CHK	= szYD_BOOK_OUT_LOC.substring(7, 8);				
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara =  JDTORecordFactory.getInstance().create();
			
			inRecord = JDTORecordFactory.getInstance().create();
			
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);
			//베드번호 Asc
			/*com.inisteel.cim.yd.dao.ydstkbeddao.YdStkbedDao.getYdStkbedByColBedLike*/
			intRtnVal = ydStkBedDao.getYdStkbed(recPara, outRecSet, 23);
			
			recTemp =  JDTORecordFactory.getInstance().create();

//DONG_INSERT : OK	문자bed 변경
			if(szYD_STK_BED_CHK.equals("A")) { 
				startBedNo = 10;
			} else if (szYD_STK_BED_CHK.equals("B")){
				startBedNo = 11;
			} else if (szYD_STK_BED_CHK.equals("C")){
				startBedNo = 12;
			} else if (szYD_STK_BED_CHK.equals("D")){
				startBedNo = 13;
			} else if (szYD_STK_BED_CHK.equals("E")){
				startBedNo = 14;
			} else if (szYD_STK_BED_CHK.equals("F")){
				startBedNo = 15;
			} else if (szYD_STK_BED_CHK.equals("G")){
				startBedNo = 16;
			} else if (szYD_STK_BED_CHK.equals("H")){
				startBedNo = 17;
			} else {
				startBedNo = Integer.parseInt(szYD_STK_BED_CHK);
			}			
			
			if( intRtnVal > 0 ) {
				szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]로 베드정보 조회 성공 - 대상재건수 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//New Version--------------------------------------------------------------------------
				for(int i = outRecSet.size(); i >= startBedNo + 1; i-- ) {
					
					/*
					 * 후행 베드의 적치열, 적치베드, 적치단 정보 추출
					 */
					outRecSet.absolute(i);
					recPara = outRecSet.getRecord();
					
					szYD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
					
					if( i > startBedNo + 1 ) {
						
						/*
						 * 선행 베드의 적치열, 적치베드, 적치단 정보 추출
						 */
						outRecSet.absolute(i - 1);
						recPara = outRecSet.getRecord();
	
						szPREV_YD_STK_COL_GP = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
						szPREV_YD_STK_BED_NO = ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
						
						recPara.setField("FROM_STK_COL_GP",	szPREV_YD_STK_COL_GP);  //FROM 가상버퍼 적치열구분(PREV)
						recPara.setField("TO_STK_COL_GP",	szYD_STK_COL_GP);		//TO 가상버퍼 적치열구분
						recPara.setField("FROM_BED_NO",		szPREV_YD_STK_BED_NO);  //FROM 가상버퍼 번지(PREV)
						recPara.setField("TO_BED_NO",		szYD_STK_BED_NO); 		//TO 가상버퍼 번지
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");
						
						if(intRtnVal < 1) {
							szMsg = "[가상베드정보SHIFT] BOOK-OUT위치 Update ERROR 위치 - 반환값 : " + intRtnVal;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}								
					}else{
						
						//BOOK-OUT 지점  Clear 처리
						recPara.setField("FROM_STK_COL_GP",	szYD_STK_COL_GP);
						recPara.setField("FROM_BED_NO",		szYD_STK_BED_NO); 
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");
						
						if(intRtnVal < 1) {
							szMsg = "[가상베드정보SHIFT] 대기베드 재료정보 Clear Update ERROR 위치: " + intRtnVal;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}								
					}
				}
				//-------------------------------------------------------------------------------------
				
			}else{
				szMsg="["+szOperationName+"] 해당적치열["+szYD_STK_COL_GP+"]의 베드정보가 존재하지 않으므로 SHIFT 처리를 하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
   		}catch(JDTOException ex) {
			szMsg = "[" + szOperationName + "] 예외발생 - 오류메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(ex);  			
   		}	
		
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		return szRtnMsg;
	}	

	/**
	 * 2후판제품창고의 BOOK-OUT LOC의 가상베드정보를 역 SHIFT시키는 메소드
	 * @param szYD_BOOK_OUT_LOC
	 * @param startBedNo
	 * @return
	 * @throws JDTOException
	 */
	public static String procReverseShiftBedInfoForBookoutLoc3G(String szYD_BOOK_OUT_LOC) throws JDTOException {
		String szRtnMsg 				= YdConstant.RETN_CD_SUCCESS;
		String szMethodName				= "procReverseShiftBedInfoForBookoutLoc3G";
		String szOperationName			= "가상베드정보역SHIFT";
		String szMsg					= "";
		int intRtnVal					= -100;
		
		JDTORecord	recPara				= null;
		JDTORecordSet outRecSet			= null;
		
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		
		String szYD_STK_COL_GP			= null;
		String szYD_STK_BED_NO			= null;
		
		String szPREV_YD_STK_BED_NO		= null;
		
		
		szMsg="["+szOperationName+"] 메소드 시작 - BOOK-OUT LOC["+szYD_BOOK_OUT_LOC+"] 시작 베드 번호["+szYD_BOOK_OUT_LOC.substring(6)+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);

		try {
			
			outRecSet = JDTORecordFactory.getInstance().createRecordSet("");
			recPara =  JDTORecordFactory.getInstance().create();			
			
			if("TF".equals(szYD_BOOK_OUT_LOC.substring(2,4))) { //TF상에서 BOOK-OUT 취소 할 경우
				
				szYD_STK_COL_GP 	= szYD_BOOK_OUT_LOC.substring(0, 6);
				szYD_STK_BED_NO 	= szYD_BOOK_OUT_LOC.substring(6, 8);
				
				szYD_BOOK_OUT_LOC = YdCommonUtils.getTf2RtStkLoc(szYD_BOOK_OUT_LOC);
				
				szMsg="["+szOperationName+"] TF 상의 BOOK-OUT 취소일 경우 - BOOK-OUT LOC["+szYD_BOOK_OUT_LOC+"] 시작 베드 번호["+szYD_BOOK_OUT_LOC.substring(6)+"] 로 변경";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);				
				
				
				recPara.setField("FROM_STK_COL_GP",	szYD_STK_COL_GP); //FROM -> TF
				recPara.setField("TO_STK_COL_GP",	szYD_BOOK_OUT_LOC.substring(0, 6)); //TO -> RT
				recPara.setField("FROM_BED_NO",		szYD_STK_BED_NO);  //FROM -> TF
				recPara.setField("TO_BED_NO",		szYD_BOOK_OUT_LOC.substring(6, 8)); //TO -> RT
				
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");
				
				
				//TF 상의 BOOK-OUT 지점  Clear 처리
				recPara.setField("FROM_STK_COL_GP",	szYD_STK_COL_GP);
				recPara.setField("FROM_BED_NO",		szYD_STK_BED_NO); 
				
				intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");
				
			}
			
			szYD_STK_COL_GP 	= szYD_BOOK_OUT_LOC.substring(0, 6);
			szYD_STK_BED_NO 	= szYD_BOOK_OUT_LOC.substring(6, 8);
			
			recPara.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recPara.setField("YD_STK_BED_NO", szYD_STK_BED_NO);

			/*com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0045*/
			intRtnVal = commDao.select(recPara, outRecSet, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.selectQueryId_0045");				
			
			if( intRtnVal > 0 ) {
				
				szMsg="["+szOperationName+"] 적치열["+szYD_STK_COL_GP+"]로 베드정보 조회 성공 - 대상재건수 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				

				for(int i = 1; i <= outRecSet.size(); i++ ) {

					outRecSet.absolute(i);
					recPara = outRecSet.getRecord();
	
					szYD_STK_COL_GP 		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
					szYD_STK_BED_NO 		= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");							
					szPREV_YD_STK_BED_NO 	= ydDaoUtils.paraRecChkNull(recPara, "PREV_YD_STK_BED_NO");
					

					
					if(!"".equals(szPREV_YD_STK_BED_NO)) {
						//가상버퍼의 내용들을 SHIFT 처리
						
						recPara.setField("FROM_STK_COL_GP",	szYD_STK_COL_GP); //FROM,TO 적치열구분은 동일함
						recPara.setField("TO_STK_COL_GP",	szYD_STK_COL_GP);
						recPara.setField("FROM_BED_NO",		szPREV_YD_STK_BED_NO);  //FROM 가상버퍼 번지(PREV)
						recPara.setField("TO_BED_NO",		szYD_STK_BED_NO); 		//TO 가상버퍼 번지
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0006");
						
						if(intRtnVal < 1) {
							szMsg = "[가상베드정보SHIFT] BOOK-OUT위치 Update ERROR 위치 - 반환값 : " + intRtnVal;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}	
						
					} else {
						//BOOK-OUT 지점  Clear 처리
						recPara.setField("FROM_STK_COL_GP",	szYD_STK_COL_GP);
						recPara.setField("FROM_BED_NO",		szYD_STK_BED_NO); 
						
						intRtnVal = commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.updateQueryId_0007");
						
						if(intRtnVal < 1) {
							szMsg = "[가상베드정보SHIFT] 대기베드 재료정보 Clear Update ERROR 위치: " + intRtnVal;
							ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
							return YdConstant.RETN_CD_FAILURE;
						}								
					}
				}
				
				
			} else {
				szMsg="["+szOperationName+"] 해당적치열["+szYD_STK_COL_GP+"]의 베드정보가 존재하지 않으므로 SHIFT 처리를 하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
			
   		}catch(JDTOException ex) {
			szMsg = "[" + szOperationName + "] 예외발생 - 오류메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(ex);  			
   		}	
		
		szMsg="["+szOperationName+"] 메소드 끝";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		return szRtnMsg;
	}	
	
	/**
	 * 1,2후판제품창고  저장위치로 야드구분을 반환 하는 메소드
	 * @param szYdStkColGp : 야드적치열 
	 * @return szYdGp : 야드구분
	 */
	public static String getPlateYdGpForL2(String szYdStkColGp) {
		String szYD_GP = "";
		
		/*
		String szSpanNo = "";
		
		szSpanNo = szYdStkColGp.substring(2,4); //Span 번호로 1,2 후판 제품창고를 구분한다.
		switch(Integer.parseInt(szSpanNo)) {
			case 1:
			case 2:
			case 3: 
					szYD_GP = YdConstant.YD_GP_PLATE2_GDS_YARD;	//2후판 제품창고
					break;
			case 4:
			case 5:
			case 6:
			case 7:
					szYD_GP = YdConstant.YD_GP_PLATE_GDS_YARD;	//1후판 제품창고
					break;
		}
		*/
		
		szYD_GP = szYdStkColGp.substring(0,1); //야드 저장위치의 맨 앞자리 가 야드구분 (1,2 후판 통합될 경우 변경 필요)
		
		return szYD_GP;
	}
	
	/**
	 * 운송지시 변경 작업(차량스케줄,검수재료,저장품)
	 * @param recPara
	 * @param szCaller
	 * @return
	 * @throws JDTOException
	 */
	public static String transOrdChange(JDTORecord recPara ) throws JDTOException {
		String	szMethodName			= "transOrdChange";
		String	szOperationName			= "운송지시 변경 작업";
		String	szMsg					= null;
		String 	szRtnMsg				= null;
		YdCarSchDao ydCarSchDao = new YdCarSchDao();
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recTemp			= null;
		
		String	szOLD_TRANS_WORD_DATE		= null;
		String 	szOLD_TRANS_WORD_SEQNO		= null;
		String	szNEW_TRANS_WORD_DATE		= null;
		String 	szNEW_TRANS_WORD_SEQNO		= null;
		String 	szCHK_GP					= null;
		
		int 	cnt	=0;
		
		//--------------------------------------------------------------------------------
		//	운송지시일자, 운송지시순번으로 차량스케줄 조회
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] --------------- 메소드 시작 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
	
		szOLD_TRANS_WORD_DATE			= ydDaoUtils.paraRecChkNull(recPara, "OLD_TRANS_WORD_DATE");
		szOLD_TRANS_WORD_SEQNO			= ydDaoUtils.paraRecChkNull(recPara, "OLD_TRANS_WORD_SEQNO");
		szNEW_TRANS_WORD_DATE			= ydDaoUtils.paraRecChkNull(recPara, "NEW_TRANS_WORD_DATE");
		szNEW_TRANS_WORD_SEQNO			= ydDaoUtils.paraRecChkNull(recPara, "NEW_TRANS_WORD_SEQNO");
		szCHK_GP						= ydDaoUtils.paraRecChkNull(recPara, "CHK_GP");

		recTemp			= JDTORecordFactory.getInstance().create();
		recTemp.setField("OLD_TRANS_WORD_DATE" , szOLD_TRANS_WORD_DATE);
		recTemp.setField("OLD_TRANS_WORD_SEQNO", szOLD_TRANS_WORD_SEQNO);
		recTemp.setField("NEW_TRANS_WORD_DATE" , szNEW_TRANS_WORD_DATE);
		recTemp.setField("NEW_TRANS_WORD_SEQNO", szNEW_TRANS_WORD_SEQNO);
		recTemp.setField("MODIFIER"				, "trOChange");
		
		//--------------------------------------------------------------------------------
		//	차량스케줄 운송지시 변경
		//--------------------------------------------------------------------------------
 
		szMsg = "["+szOperationName+"] 차량스케줄 운송지시 변경 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdCarschTransOrd*/
		cnt = ydCarSchDao.updYdTransOrdChange(recTemp, 0);
 
		szMsg = "["+szOperationName+"] 차량스케줄 운송지시 변경 완료:"+cnt;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		 
		
		//--------------------------------------------------------------------------------
		//	검수재료 운송지시 변경
		//--------------------------------------------------------------------------------
 
		szMsg = "["+szOperationName+"] 검수재료 운송지시 변경 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);		
		
		/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdExamTransOrd*/
		cnt = ydCarSchDao.updYdTransOrdChange(recTemp, 1);
 
		szMsg = "["+szOperationName+"] 검수재료 운송지시 변경 완료:"+cnt;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------
		//	재료정보 운송지시 변경
		//--------------------------------------------------------------------------------
 
		szMsg = "["+szOperationName+"] 재료정보 운송지시 변경 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		if(szCHK_GP.equals("YD")){
			/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYdStockTransOrd*/
			cnt = ydCarSchDao.updYdTransOrdChange(recTemp, 2);
		}else{
			/*com.inisteel.cim.yd.dao.ydcarschdao.ydCarSchDao.updYmStockTransOrd*/
			cnt = ydCarSchDao.updYdTransOrdChange(recTemp, 3);
		}
 
		szMsg = "["+szOperationName+"] 재료정보 운송지시 변경 완료:"+cnt;
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] --------------- 메소드 끝 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return szRtnMsg;
	}
	
	
	
	
	/**
	 * C연주 슬라브 차량상차작업등록 후 준비스케줄 삭제처리
	 * @param szYD_GP
	 * @param szTRN_EQP_CD
	 * @param szYD_STK_COL_GP
	 * @return
	 * @throws JDTOException
	 */
	public static String procYdWbookForCarLdC(JDTORecord msgRecord, JDTORecord recOut) throws JDTOException {
		/*
		 * 업무기준 : 1. 크레인설비정보를 먼저 조회
		 * 			 2. 해당동의 차량상차 크레인스케줄정보를 조회
		 * 			 3. 크레인스케줄에 올라가지 않은 크레인정보와 관련된 준비스케줄을 조회하여 작업예약에 등록
		 */
		
		int intRtnVal			= -100;
		String szMsg			= null;
		String szMethodName 	= "procYdWbookForCarLdC";
		String szSessionName 	= "C연주 슬라브 차량상차작업등록";
		String szOperationName  = " ";
		//공용 DAO METHOD
		YdDaoUtils ydDaoUtils 			= new YdDaoUtils();
		//레코드 선언
		JDTORecord recPara     			= null;
		JDTORecord recOutPara  			= null;
		//레코드셋 선언
		JDTORecordSet rsResult 			= null;
 
		String szREG_MOD_USER			= "ForCarLdC";

		//운송장비코드
		String szTRN_EQP_CD        		= null;
 
		//차량스케줄ID
		String szYD_CAR_SCH_ID			= null;
		//스케줄코드
		String szYD_SCH_CD         		= null;
		
		String szYD_PREP_SCH_ID			= null;
		String szYD_WBOOK_ID			= null;
		String szSTL_NO 				= null;
		String szYD_GP					= null;
		String szYD_BAY_GP				= null;
		String szYD_STK_COL_GP     		= null;
		String szYD_STK_BED_NO			= null;
		String szYD_STK_LYR_NO			= null;
		String szYD_AIM_YD_GP			= null;
		String szYD_AIM_BAY_GP			= null;
		String szYD_SCH_PRIOR			= null;
		String szYD_PNT_CD				= null;
		YdWrkbookDao		ydWrkbookDao	= new YdWrkbookDao();
		YdWrkbookMtlDao		ydWrkbookMtlDao	= new YdWrkbookMtlDao();
		
		//리턴값
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
 
		
		try {
			//-------------------------------------------------------------------------------------------------
			//	파라미터 확인
			//-------------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] ---------------------- 메소드 시작 : 파라미터 확인 ----------------------";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			ydUtils.displayRecord(szOperationName, msgRecord);
			
			//운송장비코드
			szTRN_EQP_CD = ydDaoUtils.paraRecChkNull(msgRecord, "TRN_EQP_CD");
			if(szTRN_EQP_CD.equals("")){
				
				szMsg = "["+szOperationName+"] 운송장비코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			
			//포인트코드
			szYD_PNT_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_PNT_CD");
			if(szYD_PNT_CD.equals("")){
				
				szMsg = "["+szOperationName+"] 도착포인트코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			
			//차량스케줄ID
			szYD_CAR_SCH_ID = ydDaoUtils.paraRecChkNull(msgRecord, "YD_CAR_SCH_ID");
			if(szYD_CAR_SCH_ID.equals("")){
				
				szMsg = "["+szOperationName+"] 차량스케줄ID가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//스케줄코드
			szYD_SCH_CD = ydDaoUtils.paraRecChkNull(msgRecord, "YD_SCH_CD");
			if(szYD_SCH_CD.equals("")){
				
				szMsg = "["+szOperationName+"] 스케줄코드가 없습니다.";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_NO_PARAM;
				
			}
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회
			//-------------------------------------------------------------------------------------------------
			szMsg = "["+szOperationName+"] ★★★도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회 시작★★★";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_GP", 			YdConstant.YD_GP_C_SLAB_YARD);
			recPara.setField("YD_SCH_CD", 		YdConstant.YD_GP_C_SLAB_YARD + "_PT");
			//항만슬라브야드 기능추가 - 2016.01.05  LeeJY
			if (szYD_SCH_CD.startsWith(YdConstant.YD_GP_PORT_SLAB_YARD)) {
				recPara.setField("YD_GP", 			YdConstant.YD_GP_PORT_SLAB_YARD);
				recPara.setField("YD_SCH_CD", 		YdConstant.YD_GP_PORT_SLAB_YARD + "_PT");
			}
			recPara.setField("YD_WRK_PLAN_CRN", "");
			recPara.setField("YD_PREP_WK_ST", 	"L");
			recPara.setField("YD_PNT_CD", 	szYD_PNT_CD);
			
			if("3".equals(szTRN_EQP_CD.substring(3,4)) && "T".equals(szTRN_EQP_CD.substring(1,2))){
				recPara.setField("CAR_GP", 	"B");  // 100ton 트레일러 추가
			} else if("4".equals(szTRN_EQP_CD.substring(3,4)) && "T".equals(szTRN_EQP_CD.substring(1,2))) {
				recPara.setField("CAR_GP", 	"S");  // 70ton 트레일러 추가
			} else if(szYD_SCH_CD.startsWith(YdConstant.YD_GP_C_SLAB_YARD) &&  
					 ( "YPT1".equals(szTRN_EQP_CD.substring(0,4)) || "GPT1".equals(szTRN_EQP_CD.substring(0,4)))) {			
				recPara.setField("CAR_GP", 	"Y");  // 110ton ET 추가
			}else {			
				recPara.setField("CAR_GP", 	szTRN_EQP_CD.substring(1, 2));
			}
		 
			/*com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockNPrepSchByYdCrnCarGpC*/
			szRtnMsg			= DaoManager.getYdStock(recPara, rsResult, 729);			
			szMsg = "["+szOperationName+"] ★★★도착포인트에 해당하는 준비스케줄이 존재 하는 경우 조회 완료★★★ - 메세지 : " + szRtnMsg + ", 대상재건수 : " + rsResult.size();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
				szMsg = "["+szOperationName+"] 도착포인트에 해당하는 준비스케줄이 존재 안하는 경우 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
 
				return szRtnMsg;
				
			}else if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 배차순서가 빠르고 준비스케줄ID가 빠른 차량 이송 대상재를 준비스케줄에서 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				return szRtnMsg;
			}
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	작업예약/작업예약재료 등록
			//-------------------------------------------------------------------------------------------------
			
			
			
			for(int i = 1; i <= rsResult.size(); i++ ) {
				
				rsResult.absolute(i);
				recPara			= rsResult.getRecord();
				
				szSTL_NO				= ydDaoUtils.paraRecChkNull(recPara, "STL_NO");				
				 
				szYD_GP					= szYD_SCH_CD.substring(0, 1);
				szYD_BAY_GP				= szYD_SCH_CD.substring(1, 2);
				szYD_AIM_YD_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_YD_GP");
				szYD_AIM_BAY_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_AIM_BAY_GP");
				
				szYD_STK_COL_GP			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_COL_GP");
				szYD_STK_BED_NO			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_BED_NO");
				szYD_STK_LYR_NO			= ydDaoUtils.paraRecChkNull(recPara, "YD_STK_LYR_NO");
				
				if( i == 1 ) {
					
					szYD_PREP_SCH_ID				= ydDaoUtils.paraRecChkNull(recPara, "YD_PREP_SCH_ID");
					
					//-------------------------------------------------------------------------------------------------
					//	스케줄코드 조회
					//-------------------------------------------------------------------------------------------------
					
					szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"] 준비스케쥴ID["+szYD_PREP_SCH_ID+"]에 대한 스케줄 기준 조회 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recOutPara = JDTORecordFactory.getInstance().create();
					
					szRtnMsg			= YdCommonUtils.getWrkableCrnBySchRule(szYD_SCH_CD, recOutPara);
					
					szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]에 대한 스케줄 기준 조회 완료 - 메세지 : " + szRtnMsg + ", 대상재건수 : " + rsResult.size();
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					if( szRtnMsg.equals(YdConstant.RETN_CRN_NO_ALT_CRN)
							|| szRtnMsg.equals(YdConstant.YD_EQP_STAT_BREAK) 
							|| szRtnMsg.equals(YdConstant.YD_EQP_WRK_MODE_OFF_LINE)
							) {
						szYD_SCH_PRIOR				= ydDaoUtils.paraRecChkNull(recOutPara, "YD_WRK_CRN_PRIOR");
					}else if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
						
						szMsg = "["+szOperationName+"] 스케줄코드["+szYD_SCH_CD+"]에 대한 스케줄 기준 조회 시 오류발생 - 메세지 : " + szRtnMsg;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						return szRtnMsg;
					}else{
						szYD_SCH_PRIOR				= ydDaoUtils.paraRecChkNull(recOutPara, "YD_SCH_PRIOR");
					}
					
					//-------------------------------------------------------------------------------------------------
					
					//-------------------------------------------------------------------------------------------------
					//	작업예약 등록
					//-------------------------------------------------------------------------------------------------
					
					szYD_WBOOK_ID			= ydWrkbookDao.getYdWrkbookId();
					
					recOutPara = JDTORecordFactory.getInstance().create();
					
					recOutPara.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
					recOutPara.setField("REGISTER", 			szREG_MOD_USER);
					recOutPara.setField("YD_GP", 				szYD_GP);
					recOutPara.setField("YD_BAY_GP", 			szYD_BAY_GP);
					recOutPara.setField("YD_SCH_CD", 			szYD_SCH_CD);
					recOutPara.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
					recOutPara.setField("YD_AIM_YD_GP", 		szYD_AIM_YD_GP);
					recOutPara.setField("YD_AIM_BAY_GP", 		szYD_AIM_BAY_GP);
					recOutPara.setField("YD_CAR_USE_GP", 		YdConstant.YD_CAR_USE_GP_TS);
					recOutPara.setField("TRN_EQP_CD", 			szTRN_EQP_CD);
					
					szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 시작";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					intRtnVal			= ydWrkbookDao.insYdWrkbook(recOutPara);
					
					if( intRtnVal <= 0 ) {
						
						szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 시 오류발생 - 반환값 : " + intRtnVal;
						ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
						
						return YdConstant.RETN_CD_FAILURE;
					}
					
					szMsg = "["+szOperationName+"] 작업예약["+szYD_WBOOK_ID+"]등록 완료";
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
					
					//-------------------------------------------------------------------------------------------------
				}
				
				//-------------------------------------------------------------------------------------------------
				//	작업예약재료 등록
				//-------------------------------------------------------------------------------------------------
				
				recOutPara = JDTORecordFactory.getInstance().create();
				
				recOutPara.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
				recOutPara.setField("REGISTER", 				szREG_MOD_USER);
				recOutPara.setField("STL_NO", 					szSTL_NO);
				recOutPara.setField("YD_STK_COL_GP", 			szYD_STK_COL_GP);
				recOutPara.setField("YD_STK_BED_NO", 			szYD_STK_BED_NO);
				recOutPara.setField("YD_STK_LYR_NO", 			szYD_STK_LYR_NO);
				recOutPara.setField("YD_UP_COLL_SEQ", 			String.valueOf(i));
				
				szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 시작";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				
				intRtnVal			= ydWrkbookMtlDao.insYdWrkbookmtl(recOutPara);
				
				if( intRtnVal <= 0 ) {
					
					szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 시 오류발생 - 반환값 : " + intRtnVal;
					ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
					
					throw new DAOException(szMsg);
				}
				
				szMsg = "["+szOperationName+"] ["+i+"] 작업예약["+szYD_WBOOK_ID+"]의 작업재료["+szSTL_NO+"] 등록 완료";
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
				//-------------------------------------------------------------------------------------------------
			}
			
			//-------------------------------------------------------------------------------------------------

			
			//-------------------------------------------------------------------------------------------------
			//	준비스케줄 삭제
			//-------------------------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg			= YdCommonUtils.deletePreSch(szYD_PREP_SCH_ID, szYD_WBOOK_ID, szMethodName);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				throw new DAOException(szMsg);
			}
			
			szMsg = "["+szOperationName+"] 준비스케줄["+szYD_PREP_SCH_ID+"] 삭제 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-------------------------------------------------------------------------------------------------
			
			
			//-------------------------------------------------------------------------------------------------
			//	차량스케줄에 상차작업예약 등록
			//-------------------------------------------------------------------------------------------------
			
			recOutPara = JDTORecordFactory.getInstance().create();
			
			recOutPara.setField("YD_CAR_SCH_ID", 				szYD_CAR_SCH_ID);
			recOutPara.setField("YD_CARLD_WRK_BOOK_ID", 		szYD_WBOOK_ID);
			recOutPara.setField("MODIFIER", 					szREG_MOD_USER);
			
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 시작";
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			szRtnMsg			= DaoManager.updYdCarsch(recOutPara, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
				
				throw new DAOException(szMsg);
			}
			
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]에 상차작업예약["+szYD_WBOOK_ID+"] 등록 완료 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.DEBUG);
			
			//-------------------------------------------------------------------------------------------------
			
			recOut.setField("YD_WBOOK_ID", 				szYD_WBOOK_ID);
			recOut.setField("YD_SCH_CD", 				szYD_SCH_CD);
			
			szMsg="["+szOperationName+"] 메소드 끝";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			return YdConstant.RETN_CD_SUCCESS;
 
	    	
		}catch(DAOException e){
			szMsg = "["+szOperationName+"] DAOException 예외발생[1] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw e;
		} catch(Exception e){
			szMsg = "["+szOperationName+"] 예외발생[2] : " + e.getMessage();
			ydUtils.putLog(szSessionName, szMethodName, szMsg, YdConstant.ERROR);
			throw new DAOException(szMsg);
			//return YdConstant.RETN_CD_FAILURE;
		}
		//return szRtnMsg;
 

	}
	
	/**
	 * 출하차량스케줄/차량Point삭제
	 *  - 전사물류개선으로 하차관련 초기화 추가(기존하차시 해당 야드상차지를 초기화)
	 *  - 차량입고 배차취소시 관련 재료 삭제
	 * @param recPara
	 * @param szCaller
	 * @return
	 * @throws JDTOException
	 */
	public static String delCarSchNCarPointByCarSchIdNew(JDTORecord recPara, String szCaller) throws JDTOException {
		String	szMethodName			= "delCarSchNCarPointByCarSchId";
		String	szOperationName			= "출하차량스케줄/차량Point삭제";
		String	szMsg					= null;
		String 	szRtnMsg				= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recTemp			= null;
		
		String 	szYD_CAR_SCH_ID			= null;
		String	szYD_CAR_PROG_STAT		= null;						//차량진행상태
		String	szYD_CARLD_STOP_LOC		= null;						//상차정지위치
		String	szYD_CARUD_STOP_LOC		= null;						//하차정지위치
		String	szYD_CARLD_WRK_BOOK_ID	= null;						//야드상차작업예약ID
		String	szWBOOK_CRN_SCH_CHECK	= null;						//작업예약과 크레인스케줄 체크 유무
		String	szCAR_NO				= null;
		String	szCARD_NO				= null;
		String	szYD_STK_COL_CAR_NO		= null;
		String	szYD_STK_COL_CARD_NO	= null;
		
		szMsg = "["+szOperationName+"] --------------- 메소드 시작 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		szYD_CAR_SCH_ID			= ydDaoUtils.paraRecChkNull(recPara, "YD_CAR_SCH_ID");
		szWBOOK_CRN_SCH_CHECK	= ydDaoUtils.paraRecChkNull(recPara, "WBOOK_CRN_SCH_CHECK");
		
		//--------------------------------------------------------------------------------
		//	차량스케줄ID로 차량스케줄 조회
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp			= JDTORecordFactory.getInstance().create();
		
		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
		
		szRtnMsg		= DaoManager.getYdCarsch(recTemp, rsResult, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp		= rsResult.getRecord();
		
		szYD_CAR_PROG_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_PROG_STAT");
		szYD_CARLD_STOP_LOC		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CARLD_STOP_LOC");
		szYD_CARLD_WRK_BOOK_ID	= ydDaoUtils.paraRecChkNull(recTemp, "YD_CARLD_WRK_BOOK_ID");
		szYD_CARUD_STOP_LOC     = ydDaoUtils.paraRecChkNull(recTemp, "YD_CARUD_STOP_LOC");
		szCAR_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
		szCARD_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
		
		// 전사물류개선 2021. 1. 6 
		// 취소 차량의 상하차 여부를 판단한다.
		String szCAR_STOP_LOC  = szYD_CARLD_STOP_LOC; // 기본상차셋팅
		String szYD_WBOOK_ID = szYD_CARLD_WRK_BOOK_ID;
		if("".equals(szYD_CARLD_STOP_LOC)){
			szCAR_STOP_LOC = szYD_CARUD_STOP_LOC;
		} 
		
		if("".equals(szYD_WBOOK_ID)){
			szYD_WBOOK_ID = ydDaoUtils.paraRecChkNull(recTemp, "YD_CARUD_WRK_BOOK_ID");
		}
		 
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 조회 완료 - 작업예약ID["+szYD_WBOOK_ID+"], 차량진행상태["+szYD_CAR_PROG_STAT+"], 차량정지위치["+szCAR_STOP_LOC+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		
		//--------------------------------------------------------------------------------
		//	작업예약과 크레인스케줄의 존재유무 판단하여 존재하면 업무 종료
		//--------------------------------------------------------------------------------
		if( szWBOOK_CRN_SCH_CHECK.equals("Y") ) {
			
			//--------------------------------------------------------------------------------
			//	크레인스케줄의 존재 판단
			//--------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 작업예약ID["+szYD_CARLD_WRK_BOOK_ID+"]로 크레인스케줄 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
			recTemp			= JDTORecordFactory.getInstance().create();
			
			recTemp.setField("YD_WBOOK_ID", 			szYD_WBOOK_ID);
			
			szRtnMsg		= DaoManager.getYdCrnsch(recTemp, rsResult, 28);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인스케줄 조회 시 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CRN_EXIST_SCH;
				
			}else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
			
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인스케줄 조회 시 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			}else{
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"]로 크레인스케줄 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}
			//--------------------------------------------------------------------------------
			
			
			//--------------------------------------------------------------------------------
			//	작업예약의 존재유무 판단
			//--------------------------------------------------------------------------------
			
			szMsg = "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"] 조회 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
			
			szRtnMsg		= DaoManager.getYdWrkbook(recTemp, rsResult, 0);
			
			if( szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"] 조회 시 존재합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return YdConstant.RETN_CRN_EXIST_SCH;
				
			}else if( szRtnMsg.equals(YdConstant.RETN_CD_NOTEXIST) ) {
			
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"] 조회 시 존재하지 않습니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				
			}else{
				szMsg = "["+szOperationName+"] 작업예약ID["+szYD_WBOOK_ID+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			}
			//--------------------------------------------------------------------------------
		}
		//--------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------
		//	조회된 차량스케줄로 차량이송재료삭제
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		recTemp			= JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
		recTemp.setField("DEL_YN", 					"Y");
		recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller);
		
		szRtnMsg		= DaoManager.updYdCarftmvmtl(recTemp, 1);
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 삭제 완료";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------
		//	조회된 차량스케줄로 차량스케줄삭제
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		
		szRtnMsg		= DaoManager.updYdCarsch(recTemp, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 완료";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		
		//--------------------------------------------------------------------------------
		//	차량진행상태에 따른 차량정지위치 Clear 실행 - 상차도착 시에만 Clear
		//--------------------------------------------------------------------------------
			
			if( !szCAR_STOP_LOC.equals("")) {
				
				//--------------------------------------------------------------------------------
				//	적치열 조회
				//--------------------------------------------------------------------------------
				
				rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
				recTemp			= JDTORecordFactory.getInstance().create();
				recTemp.setField("YD_STK_COL_GP", 			szCAR_STOP_LOC);
				
				szRtnMsg		= DaoManager.getYdStkcol(recTemp, rsResult, 0);
				
				if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
					szMsg = "["+szOperationName+"] 차량정지위치["+szCAR_STOP_LOC+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					
					return szRtnMsg;
				}
				
				rsResult.first();
				
				recTemp = rsResult.getRecord();
				
				szYD_STK_COL_CAR_NO			= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
				szYD_STK_COL_CARD_NO		= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
				
				
				//--------------------------------------------------------------------------------
				
				if( szYD_STK_COL_CAR_NO.equals(szCAR_NO) && szYD_STK_COL_CARD_NO.equals(szCARD_NO)) {
					
					szMsg = "["+szOperationName+"] 차량정지위치["+szCAR_STOP_LOC+"] 비활성화 시작";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
					recTemp			= JDTORecordFactory.getInstance().create();
					recTemp.setField("YD_STK_COL_GP", 			szCAR_STOP_LOC);
					recTemp.setField("YD_CAR_USE_GP", 			YdConstant.YD_CAR_USE_GP_DM);
					recTemp.setField("YD_STK_COL_ACT_STAT", 	YdConstant.YD_STK_COL_INACTIVE);
					//recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller);
					
					szRtnMsg		= procCarPosActiveOrInActive(recTemp);
					
					szMsg = "["+szOperationName+"] 차량정지위치["+szCAR_STOP_LOC+"] 비활성화  완료 - 메세지 : " + szRtnMsg;
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
					
				}else{
					szMsg = "["+szOperationName+"] 차량스케줄의 차량번호["+szCAR_NO+"]와 카드번호["+szCARD_NO+"]와 적치열의 차량번호["+szYD_STK_COL_CAR_NO+"]와 카드번호["+szYD_STK_COL_CARD_NO+"]가 동일하지 않으므로 차량정지위치["+szCAR_STOP_LOC+"]를 비활성화하지 않음";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				}
			}
			
		//}
		
		//--------------------------------------------------------------------------------
		//	전사물류개선 2021. 1. 6
		//   - 차량입고(반품, 부분하차, 회송)건에 대해서 초기화 처리한다.
		//--------------------------------------------------------------------------------	
		YdPlateCommDAO commDao = new YdPlateCommDAO();
		rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp			= JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
		if( commDao.select(recTemp, rsResult,"com.inisteel.cim.yd.common.dao.YdPlateCommDao.getRetnTrgtGdsPlate") > 0){
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량입고(반품, 부분하차, 회송)건 저장품 삭제 시작";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			
			recTemp			= JDTORecordFactory.getInstance().create(); 
			recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
			recTemp.setField("DEL_YN", 					"Y");
			recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller); 
			commDao.update(recPara, "com.inisteel.cim.yd.common.dao.YdPlateCommDAO.delStockByRetnCancel");
			
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량입고(반품, 부분하차, 회송)건 삭제 완료";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		}
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] --------------- 메소드 끝 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return szRtnMsg;
	}
	
//////////////////////////////////////////////////////////////////////////
// PIDEV	
//////////////////////////////////////////////////////////////////////////
	
	/**
	 * 차량정지위치활성/비활성처리
	 * @param recInParam
	 * @return
	 */
	public static String procCarPosActiveOrInActive_PIDEV(JDTORecord recInParam) {
		String szMethodName = "procCarPosActiveOrInActive_PIDEV";
		String szOperationName = "차량정지위치활성/비활성처리(카드번호삭제)";
		int intRtnVal = -100;
		String szMsg = null;
		String szRtnMsg = YdConstant.RETN_CD_SUCCESS;
		String szYD_STK_BED_ACT_STAT = null;					//야드적치베드활성상태
		String szYD_STK_LYR_ACT_STAT = null;					//야드적치단활성상태
		String szYD_STK_LYR_MTL_STAT = null;					//야드적치단재료상태
		
		JDTORecord recInTemp = null;
		YdStkColDao ydStkColDao = new YdStkColDao();
		YdStkBedDao ydStkBedDao = new YdStkBedDao();
		YdStkLyrDao ydStkLyrDao = new YdStkLyrDao();
		
		/*
		 * 파라미터 확인
		 */
		String szYD_STK_COL_GP       = StringHelper.evl(recInParam.getFieldString("YD_STK_COL_GP"), "");
		String szYD_CAR_USE_GP       = StringHelper.evl(recInParam.getFieldString("YD_CAR_USE_GP"), "");
		String szTRN_EQP_CD          = StringHelper.evl(recInParam.getFieldString("TRN_EQP_CD"), "");
		String szCAR_NO              = StringHelper.evl(recInParam.getFieldString("CAR_NO"), "");
		String szCARD_NO             = StringHelper.evl(recInParam.getFieldString("CARD_NO"), "");
		String szTRN_EQP_STK_CAPA    = StringHelper.evl(recInParam.getFieldString("TRN_EQP_STK_CAPA"), "");
		String szYD_STK_COL_ACT_STAT = StringHelper.evl(recInParam.getFieldString("YD_STK_COL_ACT_STAT"), "");
		/*
		 * 값 검증
		 */
		
		if( szYD_STK_COL_GP.equals("") ) {
			szMsg="[" + szOperationName + "] 적치열이 존재하지 않습니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_NO_PARAM;
		}
		
		if( szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_TS) ) {			//구내운송
			if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_ACTIVE) && szTRN_EQP_CD.equals("") ) {
				szMsg="[" + szOperationName + "] 차량사용구분["+szYD_CAR_USE_GP+"]이므로 운송장비코드가 존재해야합니다.";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}else if( szYD_CAR_USE_GP.equals(YdConstant.YD_CAR_USE_GP_DM) ) {	//출하차량
			if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_ACTIVE) ) {
				if( szCAR_NO.equals("") ) {
					szMsg="[" + szOperationName + "] 차량사용구분["+szYD_CAR_USE_GP+"]이므로 차량번호가 존재해야합니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
					return YdConstant.RETN_CD_FAILURE;
				}
				
//				if( szCARD_NO.equals("") ) {
//					szMsg="[" + szOperationName + "] 차량사용구분["+szYD_CAR_USE_GP+"]이므로 카드번호가 존재해야합니다.";
//					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
//					return YdConstant.RETN_CD_FAILURE;
//				}
			}
		}

		
		if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_ACTIVE) ) {				//활성화
			szYD_STK_BED_ACT_STAT 		= YdConstant.YD_STK_BED_ACTIVE;
			szYD_STK_LYR_ACT_STAT 		= YdConstant.YD_STK_LYR_ACTIVE;
		}else if( szYD_STK_COL_ACT_STAT.equals(YdConstant.YD_STK_COL_INACTIVE) ) {		//비활성화
			szYD_STK_BED_ACT_STAT 		= YdConstant.YD_STK_BED_INACTIVE;
			szYD_STK_LYR_ACT_STAT 		= YdConstant.YD_STK_LYR_INACTIVE;
			szTRN_EQP_STK_CAPA 			= YdConstant.YD_STK_BED_WT_MAX_DEFAULT;
			szYD_CAR_USE_GP				= "";
		}else{
			szMsg="[" + szOperationName + "] 사용가능값[활성화"+YdConstant.YD_STK_COL_ACTIVE+":, 비활성화:"+YdConstant.YD_STK_COL_INACTIVE+"] - 사용할 수 없는 값["+szYD_STK_COL_ACT_STAT+"]입니다.";
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		
		try {
			/*
			 * 적치열 활성/비활성 처리
			 */
	    	recInTemp = JDTORecordFactory.getInstance().create();
	    	recInTemp.setField("YD_STK_COL_GP",        	szYD_STK_COL_GP);
	    	recInTemp.setField("YD_CAR_USE_GP",        	szYD_CAR_USE_GP);
	    	recInTemp.setField("TRN_EQP_CD",           	szTRN_EQP_CD);
	    	recInTemp.setField("CAR_NO",           		szCAR_NO);
	    	recInTemp.setField("CARD_NO",           	szCARD_NO);
	    	recInTemp.setField("YD_STK_COL_ACT_STAT",   szYD_STK_COL_ACT_STAT);
	    	intRtnVal = ydStkColDao.updYdStkcol(recInTemp, 0);
			if(intRtnVal <= 0) {
				if(intRtnVal == 0) {
					szMsg="[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]수정 시 존재하지 않습니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				}else if(intRtnVal == -1) {
					szMsg="[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]수정 시 중복되었습니다.";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);	
				}else if(intRtnVal == -2) {
					szMsg="[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]수정 시 parameter error";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				}else if(intRtnVal == -3){
					szMsg="[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]수정 시 execution failed";
					ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				}
				return YdConstant.RETN_CD_FAILURE;
			}
			
			//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태)
			YdStockDAO ydStockDAO = new YdStockDAO();
			
    		szMsg =  "▣▣▣▣차량포인트 통합관리(START):통합작업 시작 ▣▣▣▣▣";
    		ydUtils.putLog(szOperationName, szMethodName, szMsg, YdConstant.INFO);
    		//저장위치로 차량 포인트 예약 하는 경우(출하)
			String stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointtrneqpcdupdateC2";
			ydStockDAO.requestupdateData(stkQueryId, new Object[]{ szYD_STK_COL_ACT_STAT,szCAR_NO ,szCARD_NO,szYD_STK_COL_GP});
		 
			/*
			 * 적치베드 상태 활성/비활성 처리
			 */
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_BED_WT_MAX", szTRN_EQP_STK_CAPA);
			recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recInTemp.setField("YD_STK_BED_ACT_STAT", szYD_STK_BED_ACT_STAT);
			intRtnVal = ydStkBedDao.updYdStkbedYdStkColGp(recInTemp, 0);
			if(intRtnVal <= 0) {
				szMsg = "[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]의 적치베드를 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
			
			
			/*
			 * 적치단 활성/비활성 처리
			 */
			recInTemp = JDTORecordFactory.getInstance().create();
			recInTemp.setField("YD_STK_COL_GP", szYD_STK_COL_GP);
			recInTemp.setField("YD_STK_LYR_ACT_STAT", szYD_STK_LYR_ACT_STAT);
			recInTemp.setField("STL_NO", "");
			recInTemp.setField("YD_STK_LYR_MTL_STAT", "E");
	    	
			intRtnVal = ydStkLyrDao.updYdStklyrYdStkColGp(recInTemp);
			if(intRtnVal <= 0) {
				szMsg = "[" + szOperationName + "] 적치열[" + szYD_STK_COL_GP + "]의 적치단을 수정 시 오류발생 - 반환값 : " + intRtnVal;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				return YdConstant.RETN_CD_FAILURE;
			}
		}catch(JDTOException ex) {
			szMsg = "[" + szOperationName + "] 오류발생 - 메세지 : " + ex.getMessage();
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return YdConstant.RETN_CD_FAILURE;
		}
		return szRtnMsg;
	}	

	
	/**
	 * 출하차량스케줄/차량Point삭제 기능 - 상차지시 취소 시 호출
	 * @param recPara
	 * @param szCaller
	 * @return
	 * @throws JDTOException
	 */
	public static String delCarSchNCarPointForDist_PIDEV(JDTORecord recPara, String szCaller) throws JDTOException {
		String	szMethodName			= "delCarSchNCarPointForDist_PIDEV";
		String	szOperationName			= "출하차량스케줄/차량Point삭제";
		String	szMsg					= null;
		String 	szRtnMsg				= null;
		
		JDTORecordSet	rsResult		= null;
		JDTORecord		recTemp			= null;
		YdStockDAO ydStockDAO = new YdStockDAO();
		String	szTRANS_ORD_DATE		= null;
		String 	szTRANS_ORD_SEQNO		= null;
		String	szYD_CAR_SCH_ID			= null;
		String	szYD_CAR_PROG_STAT		= null;						//차량진행상태
		String	szYD_CARLD_STOP_LOC		= null;						//상차정지위치
		String	szCAR_NO				= null;
		String	szCARD_NO				= null;
		String	szYD_STK_COL_CAR_NO		= null;
		String	szYD_STK_COL_CARD_NO	= null;
		
		String szCAR_KIND= null;
		
		//--------------------------------------------------------------------------------
		//	운송지시일자, 운송지시순번으로 차량스케줄 조회
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] --------------- 메소드 시작 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
//PIDEV_S :병행가동용:PI_YD		
		String szPI_YD	= ydDaoUtils.paraRecChkNull(recPara, "PI_YD");
		
		szTRANS_ORD_DATE			= ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_DT");
		szTRANS_ORD_SEQNO			= ydDaoUtils.paraRecChkNull(recPara, "TRANS_ORD_SEQNO");
		
		szMsg = "["+szOperationName+"] 운송지시일자 :["+szTRANS_ORD_DATE+"] , 운송지시순번["+szTRANS_ORD_SEQNO+"]로 차량스케줄 조회 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
		recTemp			= JDTORecordFactory.getInstance().create();
		
		recTemp.setField("TRANS_ORD_DATE", 			szTRANS_ORD_DATE);
		recTemp.setField("TRANS_ORD_SEQNO", 		szTRANS_ORD_SEQNO);
//PIDEV_S :병행가동용:PI_YD
		/*com.inisteel.cim.yd.dao.ydcarschdao.YdCarschDao.getYdCarschByTransDTSeq_PIDEV*/
		recTemp.setField("PI_YD",    	szPI_YD);
		szRtnMsg		= DaoManager.getYdCarsch(recTemp, rsResult, 34);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "["+szOperationName+"] 운송지시일자 :["+szTRANS_ORD_DATE+"] , 운송지시순번["+szTRANS_ORD_SEQNO+"]로 차량스케줄 조회 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		rsResult.first();
		recTemp		= rsResult.getRecord();
		
		szYD_CAR_SCH_ID			= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_SCH_ID");
		szYD_CAR_PROG_STAT		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CAR_PROG_STAT");
		szYD_CARLD_STOP_LOC		= ydDaoUtils.paraRecChkNull(recTemp, "YD_CARLD_STOP_LOC");
		
		szCAR_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
//		szCARD_NO				= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
		szCAR_KIND			    = ydDaoUtils.paraRecChkNull(recTemp, "CAR_KIND");  //차량종류 추가
		
		szMsg = "["+szOperationName+"] 운송지시일자 :["+szTRANS_ORD_DATE+"] , 운송지시순번["+szTRANS_ORD_SEQNO+"]로 차량스케줄 조회 완료 - 차량스케줄ID["+szYD_CAR_SCH_ID+"], 차량진행상태["+szYD_CAR_PROG_STAT+"], 상차정지위치["+szYD_CARLD_STOP_LOC+"]";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------
		//	조회된 차량스케줄로 차량이송재료삭제
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		recTemp			= JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
		recTemp.setField("DEL_YN", 					"Y");
		recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller);
		
		szRtnMsg		= DaoManager.updYdCarftmvmtl(recTemp, 1);
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"]의 차량이송재료 삭제 완료";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------
		//	조회된 차량스케줄로 차량스케줄삭제
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시작";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		recTemp			= JDTORecordFactory.getInstance().create();
		recTemp.setField("YD_CAR_SCH_ID", 			szYD_CAR_SCH_ID);
		recTemp.setField("DEL_YN", 					"Y");
		recTemp.setField("MODIFIER", 				szCaller.length() > 10 ? szCaller.substring(0, 10) : szCaller);
		
		szRtnMsg		= DaoManager.updYdCarsch(recTemp, 0);
		
		if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
			szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 시 오류발생 - 메세지 : " + szRtnMsg;
			ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
			return szRtnMsg;
		}
		
		szMsg = "["+szOperationName+"] 차량스케줄["+szYD_CAR_SCH_ID+"] 삭제 완료";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		//--------------------------------------------------------------------------------
		
		//--------------------------------------------------------------------------------
		//	차량진행상태에 따른 차량정지위치 Clear 실행 - 상차도착 시에만 Clear
		//--------------------------------------------------------------------------------
		if( !szYD_CARLD_STOP_LOC.equals("") ) {
			
			//--------------------------------------------------------------------------------
			//	적치열 조회
			//--------------------------------------------------------------------------------
			rsResult		= JDTORecordFactory.getInstance().createRecordSet("");
			recTemp			= JDTORecordFactory.getInstance().create();
			recTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
			/*com.inisteel.cim.yd.dao.ydstkcoldao.YdStkcolDao.getYdStkcol_PIDEV*/
//PIDEV_S :병행가동용:PI_YD
			recTemp.setField("PI_YD",    	szPI_YD);						
			szRtnMsg		= DaoManager.getYdStkcol(recTemp, rsResult, 0);
			
			if( !szRtnMsg.equals(YdConstant.RETN_CD_SUCCESS) ) {
				szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 조회 시 오류발생 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.ERROR);
				
				return szRtnMsg;
			}
			
			rsResult.first();
			
			recTemp = rsResult.getRecord();
			
			szYD_STK_COL_CAR_NO			= ydDaoUtils.paraRecChkNull(recTemp, "CAR_NO");
			szYD_STK_COL_CARD_NO		= ydDaoUtils.paraRecChkNull(recTemp, "CARD_NO");
			
			
			//--------------------------------------------------------------------------------
			// if( szYD_STK_COL_CAR_NO.equals(szCAR_NO) && szYD_STK_COL_CARD_NO.equals(szCARD_NO)) {
			//if( szYD_STK_COL_CAR_NO.equals(szCAR_NO)) {
			if( (szYD_STK_COL_CAR_NO.equals(szCAR_NO)) 
			    || 
				(szCAR_KIND.equals("PT") && szYD_STK_COL_CAR_NO.equals(""))  //PT일때 입동전 예약중 걸어둔 포인트 클리어 필요. 	
			  ) 
			{
				szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 비활성화 시작";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				recTemp			= JDTORecordFactory.getInstance().create();
				recTemp.setField("YD_STK_COL_GP", 			szYD_CARLD_STOP_LOC);
				recTemp.setField("YD_CAR_USE_GP", 			YdConstant.YD_CAR_USE_GP_DM);
				recTemp.setField("YD_STK_COL_ACT_STAT", 	YdConstant.YD_STK_COL_INACTIVE);
//PIDEV_S :병행가동용:PI_YD
				recTemp.setField("PI_YD",    	szPI_YD);				
				szRtnMsg		= procCarPosActiveOrInActive(recTemp);
				
				szMsg = "["+szOperationName+"] 차량정지위치["+szYD_CARLD_STOP_LOC+"] 비활성화  완료 - 메세지 : " + szRtnMsg;
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
				
				//--------------------------------------------------------------------------------
				//	차량포인트통합관리 Clear 실행 - 상차도착 시에만 Clear
				//--------------------------------------------------------------------------------
	    		//차량포인트통합관리(1구분,2 CAR_NO, 3 장비번호OR CARD_NO,4 저장위치,5 개소코드,6 포인트,7 상태) 
				
				//저장위치로 초기화 하는 경우(출하)
				String stkQueryId = "com.inisteel.cim.yd.dao.ydstkcoldao.carpointstackcolgpupdatePT";
				ydStockDAO.requestupdateData(stkQueryId, new Object[]{ "C",szYD_CARLD_STOP_LOC});
					
			}else{
				szMsg = "["+szOperationName+"] 차량스케줄의 차량번호["+szCAR_NO+"]와 적치열의 차량번호["+szYD_STK_COL_CAR_NO+"]와 카드번호["+szYD_STK_COL_CARD_NO+"]가 동일하지 않으므로 차량정지위치["+szYD_CARLD_STOP_LOC+"]를 비활성화하지 않음";
				ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
			}
		}
		//--------------------------------------------------------------------------------
		
		szMsg = "["+szOperationName+"] --------------- 메소드 끝 ---------------";
		ydUtils.putLog(szClassName, szMethodName, szMsg, YdConstant.DEBUG);
		
		return szRtnMsg;
	}
}