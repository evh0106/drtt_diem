/*
 * @(#) 2후판정정야드에서 사용되는 공통업무를 정의하는 클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/12/14
 *
 * @description		2후판정정야드에서 사용되는 공통업무를 정의하는 클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/12/14   김현우      김현우       최초작성   
 */

package com.inisteel.cim.yd.jplateyd.util;

import java.util.Hashtable;

import jspeed.base.property.PropertyService;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.yd.common.rule.GetBreRule1;
import com.inisteel.cim.yd.common.util.YdCommonUtils;
import com.inisteel.cim.yd.common.util.YdConstant;

import com.inisteel.cim.yd.jjyd.dao.PlateReviseDao;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCommDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdCrnSpecDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdEqpDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdSchRuleDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStkLyrDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdStockDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookDAO;
import com.inisteel.cim.yd.jplateyd.dao.JPlateYdWrkbookMtlDAO;

import com.inisteel.cim.yd.jplateyd.delegate.JPlateYdDelegate;
import com.inisteel.cim.ydPI.dao.YdPICommDAO;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
import com.inisteel.cim.yd.common.util.YdUtils;

public class JPlateYdCommonUtils { 

	private static final String SZ_CLASS_NAME = JPlateYdCommonUtils.class.getName();

	private static JPlateYdDaoUtils 	ydDaoUtils 		= new JPlateYdDaoUtils();
	private static JPlateYdUtils 		ydUtils 		= new JPlateYdUtils();

	// 2후판정정 RT_ZONE_NO --> 저장위치 변환 정보
	private static final Hashtable htY7_RT_ZONE_BED 	= new Hashtable();
	// 2후판정정  저장위치 변환 정보 --> RT_ZONE_NO
	private static final Hashtable htY7_BED_RT_ZONE		= new Hashtable();

	//Level2 Port정보 저장 맵객체
	public static final Hashtable h_MPCodeL2 			= new Hashtable();

	// 1후판정정 RT_ZONE_NO --> 저장위치 변환 정보
	private static final Hashtable htY2_RT_ZONE_BED 	= new Hashtable();
	// 1후판정정  저장위치 변환 정보 --> RT_ZONE_NO
	private static final Hashtable htY2_BED_RT_ZONE		= new Hashtable();
	
	// 1후판정정 L3 크레인번호 --> L2 크레인번호 변환
	private static final Hashtable htY2_GET_L2_CRANE_NO = new Hashtable();
	
	public static final JPlateYdDelegate ydDelegate 		= new JPlateYdDelegate();

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 1후판 정정 2열처리 Book-In/Book-Out 관련 야드공통 UTIL (putLogNew, getLogIdNew, isEmpty, getJDTOLogId) 부분 
//-------------------------------------------------------------------------------------------------------------------------
    private static YdUtils 			ydLogUtils  		= new YdUtils();
 // 2024.11.15 selY2RtZoneToLoc 1후판 정정야드 RT Zone -> 저장위치 리턴 메소드에서 사용
    private static DBAssistantDAO 	dbAssDao 				= new DBAssistantDAO();
  //-------------------------------------------------------------------------------------------------------------------------
		
	
	static {
		//////////////////////////////////////////////////////////////////
		// 2후판정정 RT_ZONE_NO --> 저장위치 변환 정보
		//////////////////////////////////////////////////////////////////

		//-------------------------------------------
		//	A LINE(ON-LINE)
		//-------------------------------------------
		htY7_RT_ZONE_BED.put("2300", "FART01");			// CT1
		htY7_RT_ZONE_BED.put("2310", "FART02");
		htY7_RT_ZONE_BED.put("2320", "FART03");
		htY7_RT_ZONE_BED.put("2330", "FART04");
		htY7_RT_ZONE_BED.put("2340", "FART05");
		htY7_RT_ZONE_BED.put("2350", "FART06");			// #2DS
		htY7_RT_ZONE_BED.put("2360", "FART07");
		htY7_RT_ZONE_BED.put("2370", "FART08");			// TG2
		htY7_RT_ZONE_BED.put("2380", "FART09");			// DG2
		htY7_RT_ZONE_BED.put("2410", "FART10");

		htY7_RT_ZONE_BED.put("2440", "FART11");
		htY7_RT_ZONE_BED.put("2450", "FART12");
		htY7_RT_ZONE_BED.put("2460", "FART13");
		htY7_RT_ZONE_BED.put("2470", "FART14");
		htY7_RT_ZONE_BED.put("2480", "FART15");			// CT2
		htY7_RT_ZONE_BED.put("2490", "FART16");

		//-------------------------------------------
		//	B LINE(OFF-LINE)
		//-------------------------------------------
		htY7_RT_ZONE_BED.put("2000", "FBRT01");			// UST
		htY7_RT_ZONE_BED.put("2010", "FBRT02");
		htY7_RT_ZONE_BED.put("2020", "FBRT03");			// C/S
		htY7_RT_ZONE_BED.put("2030", "FBRT04");
		htY7_RT_ZONE_BED.put("2040", "FBRT05");
		htY7_RT_ZONE_BED.put("2050", "FBRT06");			// DSS
		htY7_RT_ZONE_BED.put("2100", "FBRT07");
		htY7_RT_ZONE_BED.put("2120", "FBRT08");
		htY7_RT_ZONE_BED.put("2130", "FBRT09");
		htY7_RT_ZONE_BED.put("2140", "FBRT10");

		htY7_RT_ZONE_BED.put("2150", "FBRT11");			// #1 DS
		htY7_RT_ZONE_BED.put("2160", "FBRT12");
		htY7_RT_ZONE_BED.put("2170", "FBRT13");			// TG1
		htY7_RT_ZONE_BED.put("2180", "FBRT14");			// DG1
		htY7_RT_ZONE_BED.put("2210", "FBRT15");
		htY7_RT_ZONE_BED.put("2240", "FBRT16");
		htY7_RT_ZONE_BED.put("2250", "FBRT17");
		htY7_RT_ZONE_BED.put("2260", "FBRT18");
		htY7_RT_ZONE_BED.put("2270", "FBRT19");
		htY7_RT_ZONE_BED.put("2280", "FBRT20");			// CT3

		htY7_RT_ZONE_BED.put("2290", "FBRT21");
		htY7_RT_ZONE_BED.put("3000", "FBRT22");
		htY7_RT_ZONE_BED.put("3010", "FBRT23");
		htY7_RT_ZONE_BED.put("3020", "FBRT24");
		htY7_RT_ZONE_BED.put("3030", "FBRT25");
		htY7_RT_ZONE_BED.put("3040", "FBRT26");
		htY7_RT_ZONE_BED.put("3050", "FBRT27");

		//-------------------------------------------
		//	C LINE(OFF-LINE)
		//-------------------------------------------
		htY7_RT_ZONE_BED.put("3080", "FCRT01");
		htY7_RT_ZONE_BED.put("3090", "FCRT02");
		htY7_RT_ZONE_BED.put("3100", "FCRT03");
		htY7_RT_ZONE_BED.put("3110", "FCRT04");

		//-------------------------------------------
		//	D LINE(OFF-LINE)
		//-------------------------------------------
		htY7_RT_ZONE_BED.put("33000", "FDRT01");		// Heavy Plate Approach Table (PBO)
		htY7_RT_ZONE_BED.put("33005", "FDRT02");

		//////////////////////////////////////////////////////////////////
		// 2후판정정 저장위치 -->  RT_ZONE_NO 변환 정보
		//////////////////////////////////////////////////////////////////

		//-------------------------------------------
		//	A LINE(ON-LINE)
		//-------------------------------------------
		htY7_BED_RT_ZONE.put("FART01", "2300");
		htY7_BED_RT_ZONE.put("FART01", "2300");
		htY7_BED_RT_ZONE.put("FART02", "2310");
		htY7_BED_RT_ZONE.put("FART03", "2320");
		htY7_BED_RT_ZONE.put("FART04", "2330");
		htY7_BED_RT_ZONE.put("FART05", "2340");
		htY7_BED_RT_ZONE.put("FART06", "2350");
		htY7_BED_RT_ZONE.put("FART07", "2360");
		htY7_BED_RT_ZONE.put("FART08", "2370");
		htY7_BED_RT_ZONE.put("FART09", "2380");
		htY7_BED_RT_ZONE.put("FART10", "2410");

		htY7_BED_RT_ZONE.put("FART11", "2440");
		htY7_BED_RT_ZONE.put("FART12", "2450");
		htY7_BED_RT_ZONE.put("FART13", "2460");
		htY7_BED_RT_ZONE.put("FART14", "2470");
		htY7_BED_RT_ZONE.put("FART15", "2480");
		htY7_BED_RT_ZONE.put("FART16", "2490");

		//-------------------------------------------
		//	B LINE(OFF-LINE)
		//-------------------------------------------
		htY7_BED_RT_ZONE.put("FBRT01", "2000");
		htY7_BED_RT_ZONE.put("FBRT02", "2010");
		htY7_BED_RT_ZONE.put("FBRT03", "2020");
		htY7_BED_RT_ZONE.put("FBRT04", "2030");
		htY7_BED_RT_ZONE.put("FBRT05", "2040");
		htY7_BED_RT_ZONE.put("FBRT06", "2050");
		htY7_BED_RT_ZONE.put("FBRT07", "2100");
		htY7_BED_RT_ZONE.put("FBRT08", "2120");
		htY7_BED_RT_ZONE.put("FBRT09", "2130");
		htY7_BED_RT_ZONE.put("FBRT10", "2140");

		htY7_BED_RT_ZONE.put("FBRT11", "2150");
		htY7_BED_RT_ZONE.put("FBRT12", "2160");
		htY7_BED_RT_ZONE.put("FBRT13", "2170");
		htY7_BED_RT_ZONE.put("FBRT14", "2180");
		htY7_BED_RT_ZONE.put("FBRT15", "2210");
		htY7_BED_RT_ZONE.put("FBRT16", "2240");
		htY7_BED_RT_ZONE.put("FBRT17", "2250");
		htY7_BED_RT_ZONE.put("FBRT18", "2260");
		htY7_BED_RT_ZONE.put("FBRT19", "2270");
		htY7_BED_RT_ZONE.put("FBRT20", "2280");

		htY7_BED_RT_ZONE.put("FBRT21", "2290");
		htY7_BED_RT_ZONE.put("FBRT22", "3000");
		htY7_BED_RT_ZONE.put("FBRT23", "3010");
		htY7_BED_RT_ZONE.put("FBRT24", "3020");
		htY7_BED_RT_ZONE.put("FBRT25", "3030");
		htY7_BED_RT_ZONE.put("FBRT26", "3040");
		htY7_BED_RT_ZONE.put("FBRT27", "3050");

		//-------------------------------------------
		//	C LINE(OFF-LINE)
		//-------------------------------------------
		htY7_BED_RT_ZONE.put("FCRT01", "3080");
		htY7_BED_RT_ZONE.put("FCRT02", "3090");
		htY7_BED_RT_ZONE.put("FCRT03", "3100");
		htY7_BED_RT_ZONE.put("FCRT04", "3110");
		
		//==============================================================
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

		h_MPCodeL2.put("Y7", "6848");  // 2후판정정야드 Level2

		h_MPCodeL2.put("W1", "6850");  // 원료Level2
		h_MPCodeL2.put("Z1", "6852");  // 계량시스템Level2
		h_MPCodeL2.put("Z2", "6854");  // 철도Level2
		h_MPCodeL2.put("Z3", "6856");  // 환경에너지Level2
		h_MPCodeL2.put("Z4", "6858");  // 시험검정Level2
		h_MPCodeL2.put("R2", "6860");  // C열연 Mill Level2
		h_MPCodeL2.put("R3", "6862");  // C열연정정Level2
		
		//////////////////////////////////////////////////////////////////
		// 1후판정정 RT_ZONE_NO --> 저장위치 변환 정보
		//////////////////////////////////////////////////////////////////

/**********************************************************
* 1후판정정추가 SJH16
**********************************************************/			
		
		//-------------------------------------------
		//	A LINE(ON-LINE)
		//-------------------------------------------
		htY2_RT_ZONE_BED.put("0009A", "PART9A");	
		htY2_RT_ZONE_BED.put("0009N", "PART9N");	
		htY2_RT_ZONE_BED.put("0009B", "PART9B");	
		htY2_RT_ZONE_BED.put("60002", "PART20");
		htY2_RT_ZONE_BED.put("60003", "PART30");
		htY2_RT_ZONE_BED.put("60008", "PART80");
		htY2_RT_ZONE_BED.put("0013N", "PART13");
		htY2_RT_ZONE_BED.put("60014", "PART14");
		//-------------------------------------------
		//	B LINE(OFF-LINE)
		//-------------------------------------------
		htY2_RT_ZONE_BED.put("0006A", "PBRT6A");
		htY2_RT_ZONE_BED.put("0006N", "PBRT6N");			
		htY2_RT_ZONE_BED.put("0006B", "PBRT6B");			
		htY2_RT_ZONE_BED.put("0005A", "PBRT5A");
		htY2_RT_ZONE_BED.put("0005N", "PBRT5N");
		htY2_RT_ZONE_BED.put("0005B", "PBRT5B");
		htY2_RT_ZONE_BED.put("0004A", "PBRT4A");
		htY2_RT_ZONE_BED.put("0004N", "PBRT4N");
		htY2_RT_ZONE_BED.put("0004B", "PBRT4B");
		htY2_RT_ZONE_BED.put("0010A", "PBRT1A");
		htY2_RT_ZONE_BED.put("0010N", "PBRT1N");
		htY2_RT_ZONE_BED.put("0010B", "PBRT1B");
		htY2_RT_ZONE_BED.put("0011N", "PBRTWB");  //추가
		//-------------------------------------------
		//	C LINE(OFF-LINE)
		//-------------------------------------------
		htY2_RT_ZONE_BED.put("33010", "PCRT10");  //추가
		htY2_RT_ZONE_BED.put("10000", "PCRT20");  //추가
		htY2_RT_ZONE_BED.put("20000", "PCRT30");  //추가
		htY2_RT_ZONE_BED.put("00012", "PCRT40");  //추가
		htY2_RT_ZONE_BED.put("57000", "PCRT50");  //추가
		htY2_RT_ZONE_BED.put("57005", "PCRT55");  //추가
		htY2_RT_ZONE_BED.put("57910", "PCRT60");  //추가
		htY2_RT_ZONE_BED.put("57915", "PCRT65");  //추가
		htY2_RT_ZONE_BED.put("54020", "PCRT70");  //추가
		htY2_RT_ZONE_BED.put("53905", "PCRT80");  //추가
		htY2_RT_ZONE_BED.put("53910", "PCRT85");  //추가
		htY2_RT_ZONE_BED.put("54915", "PCRT90");  //추가
		//-------------------------------------------
		//	D LINE(OFF-LINE)
		//-------------------------------------------
		htY2_RT_ZONE_BED.put("38915", "PDRT10");  //추가
		htY2_RT_ZONE_BED.put("42010", "PDRT20");  //추가
		htY2_RT_ZONE_BED.put("42900", "PDRT30");  //추가
		htY2_RT_ZONE_BED.put("42905", "PDRT35");  //추가
		htY2_RT_ZONE_BED.put("45000", "PDRT40");  //추가
		htY2_RT_ZONE_BED.put("45910", "PDRT49");  //추가 **
		htY2_RT_ZONE_BED.put("48000", "PDRT50");  //추가
		htY2_RT_ZONE_BED.put("48005", "PDRT55");  //추가
		htY2_RT_ZONE_BED.put("53500", "PDRT90");  //추가
		htY2_RT_ZONE_BED.put("53015", "PDRT95");  //추가
		//-------------------------------------------
		//	E LINE(OFF-LINE)
		//-------------------------------------------
		htY2_RT_ZONE_BED.put("38025", "PERT25");  //추가
		htY2_RT_ZONE_BED.put("38030", "PERT30");  //추가
		htY2_RT_ZONE_BED.put("46010", "PERT40");  //추가
		htY2_RT_ZONE_BED.put("47905", "PERT50");  //추가
		htY2_RT_ZONE_BED.put("47506", "PERT51");  //추가
		htY2_RT_ZONE_BED.put("49005", "PERT60");  //추가
		htY2_RT_ZONE_BED.put("49930", "PERT70");  //추가
		htY2_RT_ZONE_BED.put("49935", "PERT75");  //추가
		//-------------------------------------------
		//	F LINE(OFF-LINE)
		//-------------------------------------------
		htY2_RT_ZONE_BED.put("60012", "PFRT10");		
		htY2_RT_ZONE_BED.put("58116", "PFRT20");
		htY2_RT_ZONE_BED.put("58020", "PFRT30");
		htY2_RT_ZONE_BED.put("56116", "PFRT40");
		htY2_RT_ZONE_BED.put("56015", "PFRT50"); //!! 화면에 56020으로 표시되어 있다
		htY2_RT_ZONE_BED.put("56020", "PFRT55"); //!! 신시스템
		htY2_RT_ZONE_BED.put("59020", "PFRT60");

		//////////////////////////////////////////////////////////////////
		// 1후판정정 저장위치 -->  RT_ZONE_NO 변환 정보
		//////////////////////////////////////////////////////////////////

		//-------------------------------------------
		//	전체
		//-------------------------------------------
		htY2_BED_RT_ZONE.put("PART9A", "0009A");
		htY2_BED_RT_ZONE.put("PART9N", "0009N");
		htY2_BED_RT_ZONE.put("PART9B", "0009B");
		htY2_BED_RT_ZONE.put("PART20", "60002");
		htY2_BED_RT_ZONE.put("PART30", "60003");
		htY2_BED_RT_ZONE.put("PART80", "60008");
		htY2_BED_RT_ZONE.put("PART13", "0013N");
		htY2_BED_RT_ZONE.put("PART14", "60014");
		
		htY2_BED_RT_ZONE.put("PBRT6A", "0006A");
		htY2_BED_RT_ZONE.put("PBRT6N", "0006N");
		htY2_BED_RT_ZONE.put("PBRT6B", "0006B");
		htY2_BED_RT_ZONE.put("PBRT5A", "0005A");
		htY2_BED_RT_ZONE.put("PBRT5N", "0005N");
		htY2_BED_RT_ZONE.put("PBRT5B", "0005B");
		htY2_BED_RT_ZONE.put("PBRT4A", "0004A");
		htY2_BED_RT_ZONE.put("PBRT4N", "0004N");
		htY2_BED_RT_ZONE.put("PBRT4B", "0004B");
		htY2_BED_RT_ZONE.put("PBRT1A", "0010A");
		htY2_BED_RT_ZONE.put("PBRT1N", "0010N");
		htY2_BED_RT_ZONE.put("PBRT1B", "0010B");
		htY2_BED_RT_ZONE.put("PBRTWB", "0011N");  //추가
		
		htY2_BED_RT_ZONE.put("PCRT10", "33010");  //추가
		htY2_BED_RT_ZONE.put("PCRT20", "10000");  //추가
		htY2_BED_RT_ZONE.put("PCRT30", "20000");  //추가
		htY2_BED_RT_ZONE.put("PCRT40", "00012");  //추가
		htY2_BED_RT_ZONE.put("PCRT50", "57000");  //추가
		htY2_BED_RT_ZONE.put("PCRT55", "57005");  //추가
		htY2_BED_RT_ZONE.put("PCRT60", "57910");  //추가
		htY2_BED_RT_ZONE.put("PCRT65", "57915");  //추가
		htY2_BED_RT_ZONE.put("PCRT68", "54010");  //추가
		htY2_BED_RT_ZONE.put("PCRT69", "54015");  //추가
		htY2_BED_RT_ZONE.put("PCRT70", "54020");  //추가
		htY2_BED_RT_ZONE.put("PCRT80", "53905");  //추가
		htY2_BED_RT_ZONE.put("PCRT85", "53910");  //추가
		htY2_BED_RT_ZONE.put("PCRT90", "54915");  //추가

		htY2_BED_RT_ZONE.put("PDRT10", "38915");  //추가
		htY2_BED_RT_ZONE.put("PDRT20", "42010");  //추가
		htY2_BED_RT_ZONE.put("PDRT30", "42900");  //추가
		htY2_BED_RT_ZONE.put("PDRT35", "42905");  //추가
		htY2_BED_RT_ZONE.put("PDRT40", "45000");  //추가
		htY2_BED_RT_ZONE.put("PDRT49", "45910");  //추가**
		htY2_BED_RT_ZONE.put("PDRT50", "48000");  //추가
		htY2_BED_RT_ZONE.put("PDRT55", "48005");  //추가
		htY2_BED_RT_ZONE.put("PDRT90", "53500");  //추가
		htY2_BED_RT_ZONE.put("PDRT95", "53015");  //추가
		
		htY2_BED_RT_ZONE.put("PERT25", "38025");  //추가
		htY2_BED_RT_ZONE.put("PERT30", "38030");  //추가
		htY2_BED_RT_ZONE.put("PERT40", "46010");  //추가
		htY2_BED_RT_ZONE.put("PERT49", "47900");  //추가
		htY2_BED_RT_ZONE.put("PERT50", "47905");  //추가
		htY2_BED_RT_ZONE.put("PERT51", "47506");  //추가
		htY2_BED_RT_ZONE.put("PERT60", "49005");  //추가
		htY2_BED_RT_ZONE.put("PERT68", "49920");  //추가
		htY2_BED_RT_ZONE.put("PERT69", "49925");  //추가
		htY2_BED_RT_ZONE.put("PERT70", "49930");  //추가
		htY2_BED_RT_ZONE.put("PERT75", "49935");  //추가
		
		htY2_BED_RT_ZONE.put("PFRT10", "60012");
		htY2_BED_RT_ZONE.put("PFRT20", "58116");
		htY2_BED_RT_ZONE.put("PFRT28", "58010");
		htY2_BED_RT_ZONE.put("PFRT29", "58015");
		htY2_BED_RT_ZONE.put("PFRT30", "58020");
		htY2_BED_RT_ZONE.put("PFRT40", "56116");
		htY2_BED_RT_ZONE.put("PFRT48", "56005");  //!!
		htY2_BED_RT_ZONE.put("PFRT49", "56010");  //!!
		htY2_BED_RT_ZONE.put("PFRT50", "56015");  //!! 구시스템 화면에 56020으로 표시되어 있다
		htY2_BED_RT_ZONE.put("PFRT55", "56020");  //!! 신시스템
		htY2_BED_RT_ZONE.put("PFRT58", "59010");
		htY2_BED_RT_ZONE.put("PFRT59", "59015");
		htY2_BED_RT_ZONE.put("PFRT60", "59020");
		
		
		
		
		//1후판정정 L3 크레인번호(6자리) --> L2 크레이번호(2자리)
		htY2_GET_L2_CRANE_NO.put("PACRA1", "54");
		htY2_GET_L2_CRANE_NO.put("PACRA2", "55");
		htY2_GET_L2_CRANE_NO.put("PBCRB1", "44");
		htY2_GET_L2_CRANE_NO.put("PBCRB2", "20");
		htY2_GET_L2_CRANE_NO.put("PBCRB3", "21");
		htY2_GET_L2_CRANE_NO.put("PCCRC1", "43");
		htY2_GET_L2_CRANE_NO.put("PCCRC2", "18");
		htY2_GET_L2_CRANE_NO.put("PCCRC3", "19");
		htY2_GET_L2_CRANE_NO.put("PDCRD1", "33");
		htY2_GET_L2_CRANE_NO.put("PDCRD2", "16");
		htY2_GET_L2_CRANE_NO.put("PDCRD3", "17");
		htY2_GET_L2_CRANE_NO.put("PECRE1", "50");
		htY2_GET_L2_CRANE_NO.put("PECRE2", "51");
		htY2_GET_L2_CRANE_NO.put("PECRE3", "52");
		htY2_GET_L2_CRANE_NO.put("PECRE4", "45");
		htY2_GET_L2_CRANE_NO.put("PECRE5", "46");
		htY2_GET_L2_CRANE_NO.put("PFCRF1", "22");
		htY2_GET_L2_CRANE_NO.put("PFCRF2", "23");

	 }

	/**
	 * 생성자 : 외부에서 직접 호출 불가능
	 */
	private JPlateYdCommonUtils() {}

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
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "TCCODE : " + szGpCode, 4);

			szPort = (String)h_MPCodeL2.get(szGpCode);
		}catch (Exception e){
			szLogMsg = szMethodName + " Exception Error : " + e.getLocalizedMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			return -1;
		}finally{
			szLogMsg = "Mapping TC Code : " + szPort;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
		}

		return Integer.parseInt(szPort);
	}

	/**
	 * 오퍼레이션명 : 2후판정정의  RT ZONE NO를 저장위치로 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getY7RtZoneToLoc(String pBookOutLoc) {
		return (String)htY7_RT_ZONE_BED.get(pBookOutLoc);
	}

	/**
	 * 오퍼레이션명 : 2후판정정의 저장위치로 RT ZONE NO 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getY7LocToRtZone(String pBookOutLoc) {
		return (String)htY7_BED_RT_ZONE.get(pBookOutLoc);
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

		JPlateYdSchRuleDAO ydSchRuleDao = new JPlateYdSchRuleDAO();

		String szLogMsg			 = null;
		String szMethodName		 = "getWrkableCrnBySchRule";
		String szOperationName	 = "스케줄기준체크및 주/대체크레인정보반환";
		String szRtnMsg			 = null;

		JDTORecord recPara		 = null;
		JDTORecord recTemp		 = null;
		JDTORecordSet rsResult	 = null;

		String szYD_WRK_ALT_GP	 = "";
		String szYD_WRKABLE_CRN	 = null;
		String szYD_SCH_PRIOR	 = null;

		String szYD_SCH_PROH_EXN = null;						//야드스케쥴금지유무
		String szYD_WRK_CRN		 = null;						//야드작업크레인
		String szYD_WRK_CRN_STATUS = null;						//야드작업크레인의 상태
		String szYD_WRK_CRN_PRIOR = null;						//야드작업크레인우선순위
		String szYD_ALT_CRN_YN	 = null;						//야드대체크레인유무
		String szYD_ALT_CRN		 = null;						//야드대체크레인
		String szYD_ALT_CRN_STATUS = null;						//야드대체크레인의 상태
		String szYD_ALT_CRN_PRIOR = null;						//야드대체크레인우선순위

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.18 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(recResult, "P");  	// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
		
		int    intRtnVal		 = 0;

		szLogMsg = "[" + szOperationName + "] ------------------- 메소드 시작 -------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//------------------------------------------------------------------------------------------
		//	스케줄코드로 스케줄기준 조회
		//------------------------------------------------------------------------------------------
		szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]로 스케줄기준 조회 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		rsResult = JDTORecordFactory.getInstance().createRecordSet("");

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_SCH_CD", szYD_SCH_CD);

		intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult);		// intGp == 0

		if (intRtnVal <= 0) {
			szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]로 스케줄기준 조회 시 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		} else {
			if (rsResult.size() > 1) {

				szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]로 스케줄기준 조회 시 중복됩니다 - 스케줄기준 개수[" + rsResult.size() + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

				return JPlateYdConst.RETN_CD_DUPLICATE;
			}
		}

		szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]로 스케줄기준 조회 완료 ";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		//------------------------------------------------------------------------------------------
		//	스케줄코드의 스케줄금지 판단.
		//------------------------------------------------------------------------------------------
		rsResult.first();
		recPara = rsResult.getRecord();
		szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara, "YD_SCH_PROH_EXN");			//야드스케쥴금지유무

		if ("Y".equals(szYD_SCH_PROH_EXN)) {

			szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]는 스케줄금지상태입니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			return JPlateYdConst.RETN_CRN_SCH_PROH;
		}

		szYD_WRK_CRN	 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");					//야드작업크레인
		szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN_PRIOR");			//야드작업크레인우선순위
		szYD_ALT_CRN_YN	 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_YN");				//야드대체크레인유무
		szYD_ALT_CRN	 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");					//야드대체크레인
		szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");			//야드대체크레인우선순위

		szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]는 스케줄기동이 가능한 상태입니다.";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]의 주작업크레인[" + szYD_WRK_CRN + "]상태를 체크 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		recPara  = JDTORecordFactory.getInstance().create();
		szRtnMsg = checkCrnStat(szYD_WRK_CRN, recPara);

		szYD_WRK_CRN_STATUS = szRtnMsg;
		szYD_WRK_ALT_GP	 	= JPlateYdConst.YD_WRK_CRN;
		szYD_WRKABLE_CRN 	= szYD_WRK_CRN;
		szYD_SCH_PRIOR	 	= szYD_WRK_CRN_PRIOR;

		if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
			szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]의 주작업크레인[" + szYD_WRK_CRN + "]상태가 작업가능함 - 우선순위[" + szYD_WRK_CRN_PRIOR + "]";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		} else {
			szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]의 주작업크레인[" + szYD_WRK_CRN + "]상태가 작업불가능[" + szRtnMsg + "]이므로 대체크레인유무["  +szYD_ALT_CRN_YN + "] 비교";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		}

		//------------------------------------------------------------------------------------------
		//	야드대체크레인으로 설비테이블 조회 후 크레인상태 체크 - 고장, OFF-LINE 체크
		//------------------------------------------------------------------------------------------
		if (!"Y".equals(szYD_ALT_CRN_YN)) {

			szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]의 주작업크레인[" + szYD_WRK_CRN + "]가 작업불가능한 상태에서 대체크레인이 존재하지 않습니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);

			if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szYD_WRK_CRN_STATUS)) {
				szRtnMsg = JPlateYdConst.RETN_CRN_NO_ALT_CRN;
			}
		} else {
			szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]의 대체크레인[" + szYD_ALT_CRN + "]상태를 체크 시작";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

			recTemp  = JDTORecordFactory.getInstance().create();
			szRtnMsg = checkCrnStat(szYD_ALT_CRN, recTemp);

			szYD_ALT_CRN_STATUS = szRtnMsg;
			szYD_WRK_ALT_GP	 = JPlateYdConst.YD_ALT_CRN;
			szYD_WRKABLE_CRN = szYD_ALT_CRN;
			szYD_SCH_PRIOR	 = szYD_ALT_CRN_PRIOR;

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg)) {
				szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]의 대체크레인[" + szYD_ALT_CRN + "]상태가 작업가능함 - 우선순위[" + szYD_ALT_CRN_PRIOR + "]";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
			} else {
				szLogMsg = "[" + szOperationName + "] 스케줄코드[" + szYD_SCH_CD + "]의 대체크레인[" + szYD_ALT_CRN + "]상태가 작업불가능[" + szRtnMsg + "]함";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			}

			if (JPlateYdConst.RETN_CD_SUCCESS.equals(szYD_WRK_CRN_STATUS) && JPlateYdConst.RETN_CD_SUCCESS.equals(szYD_ALT_CRN_STATUS)) {
				szYD_WRK_ALT_GP	 = JPlateYdConst.YD_WRK_CRN;
				szYD_WRKABLE_CRN = szYD_WRK_CRN;
				szYD_SCH_PRIOR	 = szYD_WRK_CRN_PRIOR;
				szRtnMsg		 = JPlateYdConst.RETN_CD_SUCCESS;
			} else if (JPlateYdConst.RETN_CD_SUCCESS.equals(szYD_WRK_CRN_STATUS) && !JPlateYdConst.RETN_CD_SUCCESS.equals(szYD_ALT_CRN_STATUS)) {
				szYD_WRK_ALT_GP	 = JPlateYdConst.YD_WRK_CRN;
				szYD_WRKABLE_CRN = szYD_WRK_CRN;
				szYD_SCH_PRIOR	 = szYD_ALT_CRN_PRIOR;
				szRtnMsg		 = JPlateYdConst.RETN_CD_SUCCESS;
			} else if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szYD_WRK_CRN_STATUS) && JPlateYdConst.RETN_CD_SUCCESS.equals(szYD_ALT_CRN_STATUS)) {
				szYD_WRK_ALT_GP	 = JPlateYdConst.YD_ALT_CRN;
				szYD_WRKABLE_CRN = szYD_ALT_CRN;
				szYD_SCH_PRIOR	 = szYD_ALT_CRN_PRIOR;
				szRtnMsg		 = JPlateYdConst.RETN_CD_SUCCESS;
			} else {
				szRtnMsg		 = szYD_ALT_CRN_STATUS;
			}
		}

		//주/대체작업크레인 구분자
		recResult.setField("YD_WRK_ALT_GP",		szYD_WRK_ALT_GP);
		//작업가능한 크레인
		recResult.setField("YD_WRKABLE_CRN", 	szYD_WRKABLE_CRN);
		//주작업크레인
		recResult.setField("YD_WKR_CRN", 		szYD_WRK_CRN);
		//주작업크레인우선순위
		recResult.setField("YD_WRK_CRN_PRIOR", 	szYD_WRK_CRN_PRIOR);
		//주작업크레인상태
		recResult.setField("YD_WRK_CRN_STATUS", szYD_WRK_CRN_STATUS);
		//대체작업크레인
		recResult.setField("YD_ALT_CRN", 		szYD_ALT_CRN);
		//대체작업크레인우선순위
		recResult.setField("YD_ALT_CRN_PRIOR", 	szYD_ALT_CRN_PRIOR);
		//대체작업크레인상태
		recResult.setField("YD_ALT_CRN_STATUS", szYD_ALT_CRN_STATUS);

		//------------------------------------------------------------------------------------------------
		//	작업가능한 크레인의 우선순위와 크레인사양
		//------------------------------------------------------------------------------------------------
		//스케쥴우선순위
		recResult.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
		//크레인 작업허용중량
		recResult.setField("YD_WRK_ABLE_WT", 	ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_WT"));
		//크레인 집게허용 오차
		recResult.setField("YD_CRN_TONG_W_TOL", ydDaoUtils.paraRecChkNull(recPara,"YD_CRN_TONG_W_TOL"));
		//크레인 작업가능 매수
		recResult.setField("YD_WRK_ABLE_SH", 	ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_SH"));

		szLogMsg = "[" + szOperationName + "] ------------------- 메소드 끝 -------------------";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		return szRtnMsg;
	}

	/**
	 * 오퍼레이션명 : 크레인설비상태체크
	 * @param szEqpId
	 * @param recOut
	 * @return
	 * @throws JDTOException
	 */
	public static String checkCrnStat(String szEqpId, JDTORecord recOut)throws JDTOException {

		JPlateYdEqpDAO ydEqpDao 	= new JPlateYdEqpDAO();

		String 	szLogMsg		 	= null;
		String	szMethodName	 	= "checkCrnStat";
		String	szOperationName	 	= "크레인설비상태체크";
		String	szRtnMsg		 	= null;

		JDTORecord	  	recPara		= null;
		JDTORecordSet	rsResult	= null;

		String 	szYD_EQP_STAT	 	= null;							//크레인 정상/고장
		String	szYD_EQP_WRK_MODE 	= null;							//온라인/오프라인

		rsResult = JDTORecordFactory.getInstance().createRecordSet("");
		recPara  = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_EQP_ID", szEqpId);

		int intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);		// intGp == 0

		if (intRtnVal <= 0) {
			if (intRtnVal == 0) {
				szRtnMsg = "["+szOperationName+"] 존재하지 않습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR);
				szRtnMsg = JPlateYdConst.RETN_CD_NOTEXIST;
			} else if (intRtnVal == -2) {
				szRtnMsg = "["+szOperationName+"] 파라미터가 존재하지 않습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR);
				szRtnMsg = JPlateYdConst.RETN_CD_NO_PARAM;
			} else {
				szRtnMsg = "["+szOperationName+"] 오류발생";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szRtnMsg, JPlateYdConst.ERROR);
				szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
			}
			return szRtnMsg;
		} else if (rsResult.size() > 1) {

			szLogMsg = "["+szOperationName+"] --------------------------------------------------------------";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			szLogMsg = "["+szOperationName+"] -------------- 해당 크레인["+szEqpId+"]정보가 중복됩니다. --------------";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
			szLogMsg = "["+szOperationName+"] --------------------------------------------------------------";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

			return JPlateYdConst.RETN_CD_DUPLICATE;
		} else {
			rsResult.first();
			recPara = rsResult.getRecord();

			szYD_EQP_STAT	  = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");
			szYD_EQP_WRK_MODE = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_WRK_MODE");

			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYD_EQP_STAT)) {

				szLogMsg = "["+szOperationName+"] --------------------------------------------------------------";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				szLogMsg = "["+szOperationName+"] -------------- 해당 크레인["+szEqpId+"]이 고장상태입니다. --------------";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				szLogMsg = "["+szOperationName+"] --------------------------------------------------------------";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				return JPlateYdConst.YD_EQP_STAT_BREAK;
			} else if (JPlateYdConst.YD_EQP_WRK_MODE_OFF_LINE.equals(szYD_EQP_WRK_MODE)) {

				szLogMsg = "["+szOperationName+"] --------------------------------------------------------------";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				szLogMsg = "["+szOperationName+"] -------------- 해당 크레인["+szEqpId+"]이 OFF LINE입니다. --------------";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);
				szLogMsg = "["+szOperationName+"] --------------------------------------------------------------";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

				return JPlateYdConst.YD_EQP_WRK_MODE_OFF_LINE;
			}

			recOut.addRecord(recPara);
		}
		return JPlateYdConst.RETN_CD_SUCCESS;
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

		JPlateYdStkLyrDAO ydStklyrDao	= new JPlateYdStkLyrDAO();

		String szRtnMsg					= JPlateYdConst.RETN_CD_SUCCESS;
		String szMsg					= "";
		String szMethodName				= "getYdStklyr";
		String szOperationName			= "적치된 재료상태에 따른 적치단조회";
		String szSTL_NO					= null;
		int    intRtnVal		 		= 0;

		if (szYD_STK_LYR_MTL_STAT == null || szYD_STK_LYR_MTL_STAT.length == 0) {
			szMsg = "["+szOperationName+"] 야드적치단재료상태(szYD_STK_LYR_MTL_STAT) 파라미터가 존재하지 않습니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return JPlateYdConst.RETN_CD_NO_PARAM;
		}

		szSTL_NO = ydDaoUtils.paraRecChkNull(recPara, "STL_NO");

		for(int ii = 0; ii < szYD_STK_LYR_MTL_STAT.length; ii++) {
			szMsg = "["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[ii]+"]인 재료["+szSTL_NO+"]를 조회 시작";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			recPara.setField("YD_STK_LYR_MTL_STAT", szYD_STK_LYR_MTL_STAT[ii]);

			intRtnVal = ydStklyrDao.getYdStklyrByStlNoStat(recPara, rsResult);		// intGp == 3

			if (intRtnVal > 0) {
				szMsg = "["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[ii]+"]인 재료["+szSTL_NO+"]가 존재합니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				if (rsResult.size() > 1) {
					szMsg = "["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[ii]+"]인 재료["+szSTL_NO+"]가 중복됩니다.";
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
					return JPlateYdConst.RETN_CD_DUPLICATE;
				}
				break;
			} else if (intRtnVal == 0) {
				szMsg = "["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[ii]+"]인 재료["+szSTL_NO+"]를 조회 시 존재하지 않습니다. ";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_NOTEXIST;
			} else if (intRtnVal == -2) {
				szMsg = "["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[ii]+"]인 재료["+szSTL_NO+"]를 조회 시 파라미터 오류입니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_NO_PARAM;
			} else if (intRtnVal < 0) {
				szMsg = "["+szOperationName+"] 야드적치단재료상태["+szYD_STK_LYR_MTL_STAT[ii]+"]인 재료["+szSTL_NO+"]를 조회 시 오류발생입니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return JPlateYdConst.RETN_CD_FAILURE;
			}
		}
		return szRtnMsg;
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
	public static String sndLogMsgForBedStatusInfo(int intYD_BED_ERR_CD, String szYD_CRN_SCH_ID, String szYD_DN_STK_COL_GP,
			                                       String szYD_DN_STK_BED_NO, JDTORecord recPara) throws JDTOException {

		String szLogMsg			= null;
		String szMethodName		= "sndLogMsgForBedStatusInfo";
		String szOperationName	= "BED상태로그메세지전송";

		szLogMsg = "["+szOperationName+"] ---------------- 메소드 시작 : JMS 전송시작 ----------------";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		int intERR_CD			= intYD_BED_ERR_CD;

		StringBuffer szSTATUS	= new StringBuffer();

		String szMSG_CONTENTS	= "";

		if (intERR_CD >= JPlateYdConst.YD_BED_ERR_CD_H_OVER) {
			//해당하는 적치베드에 적치가능높이 OVER
			intERR_CD -= JPlateYdConst.YD_BED_ERR_CD_H_OVER;
			szSTATUS.append("적치가능높이 OVER");
		}

		if (intERR_CD >= JPlateYdConst.YD_BED_ERR_CD_WT_OVER) {
			//해당하는 적치베드에 적치가능중량 OVER
			intERR_CD -= JPlateYdConst.YD_BED_ERR_CD_WT_OVER;
			if (szSTATUS.length() > 0) {
				szSTATUS.append(", ");
			}

			szSTATUS.append("적치가능중량 OVER");
		}

		if (intERR_CD == JPlateYdConst.YD_BED_ERR_CD_SH_OVER) {
			//해당하는 적치베드에 적치가능매수 OVER
			if (szSTATUS.length() > 0) {
				szSTATUS.append(", ");
			}
			szSTATUS.append("적치가능매수 OVER");
		}

		szMSG_CONTENTS = "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]에 적치불가능합니다 - " + szSTATUS.toString();

		recPara.setField("MSG_CONTENTS", szMSG_CONTENTS);

		ydUtils.displayRecord(szOperationName, recPara);

		ydDelegate.sendMsg(recPara);

		szLogMsg = "["+szOperationName+"] ---------------- 메소드 끝 : JMS 전송완료 ----------------";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
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
	public static String sndLogMsgForBedInfo(String szBedStatus, String szYD_CRN_SCH_ID, String szYD_DN_STK_COL_GP,
			                                 String szYD_DN_STK_BED_NO, JDTORecord recPara) throws JDTOException {

		String szLogMsg			= null;
		String szMethodName		= "sndLogMsgForBedInfo";
		String szOperationName	= "BED로그메세지전송";

		szLogMsg = "["+szOperationName+"] ---------------- 메소드 시작 : JMS 전송시작 ----------------";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		String szMSG_CONTENTS			= "";

		if (JPlateYdConst.RETN_BED_INACT.equals(szBedStatus)) {
			szMSG_CONTENTS = "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]가 비활성상태입니다.";
		} else if (JPlateYdConst.RETN_BED_WHIO_NOT_IN.equals(szBedStatus)) {
			szMSG_CONTENTS = "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]가 입고불가능상태입니다.";
		} else if (JPlateYdConst.RETN_BED_UN_WAIT.equals(szBedStatus)) {
			szMSG_CONTENTS = "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]가 권상대기상태입니다.";
		} else {
			szMSG_CONTENTS = "해당크레인스케줄["+szYD_CRN_SCH_ID+"]의 권하지시적치열["+szYD_DN_STK_COL_GP+"], 권하지시베드["+szYD_DN_STK_BED_NO+"]적치가능한 지 비교 시 오류발생["+szBedStatus+"]";
		}

		recPara.setField("MSG_CONTENTS", szMSG_CONTENTS);

		ydUtils.displayRecord(szOperationName, recPara);

		ydDelegate.sendMsg(recPara);

		szLogMsg = "["+szOperationName+"] ---------------- 메소드 끝 : JMS 전송완료 ----------------";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

    /**
	 * 베드적치가능비교(공통)
	 * @param wrkRec
	 * @param bedRec
	 * @return
	 * @throws JDTOException
	 */
	public static String chkBedStackable(JDTORecord wrkRec, JDTORecord bedRec) throws JDTOException {

		String 	szLogMsg				= "";
		String 	szMethodName			= "chkBedStackable";
		String 	szOperationName			= "베드적치가능비교(공통)";

		String	szYdStkColGp			= "";
		String	szYdStkSpanGp			= "";
		String	szYdStkBedNo			= "";
		String	szYdStkLyrNo			= "";

		int 	intYD_STK_BED_LYR_MAX	= 0;								//베드정보 - 단MAX
		int 	intYD_STK_BED_WT_MAX	= 0;								//베드정보 - 총중량
		double 	dblYD_STK_BED_H_MAX		= 0;								//베드정보 - 총높이
//		int 	intYD_STK_BED_L_MAX		= 0;								//베드정보 - 총길이
		int		intYD_STK_COL_L   		= 0;								//베드정보 - 적치열길이

		int 	intYD_MTL_SH			= 0;								//적치된 재료의 총매수
		int 	intYD_MTL_WT_SUM		= 0;								//적치된 재료의 총중량
		double 	dblYD_MTL_T_SUM			= 0;								//적치된 재료의 총두께
		int 	intYD_MTL_L_SUM			= 0;								//적치된 재료의 총길이

		int 	intYD_EQP_WRK_SH		= 0;								//크레인작업총매수
		int 	intYD_EQP_WRK_WT		= 0;								//크레인작업총중량
		double 	dblYD_EQP_WRK_T			= 0;								//크레인작업총두께
		int		intYD_EQP_WRK_L			= 0;								//크레인작업총길이

		int 	intYD_STKABLE_BED_LYR	= 0;								//적치가능한 단
		int 	intYD_STKABLE_BED_WT	= 0;								//적치가능한 중량
		double 	dblYD_STKABLE_BED_H		= 0;								//적치가능한 높이
		int 	intYD_STKABLE_BED_L		= 0;								//적치가능한 길이

		String	szRtnMsg				= JPlateYdConst.RETN_CD_SUCCESS;	//베드적치가능 결과
		int		intYD_BED_ERR_CD		= 0;								//베드적치가능 오류코드

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.19 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(wrkRec, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------

		intYD_EQP_WRK_SH 		= ydDaoUtils.paraRecChkNullInt(wrkRec, 		"YD_EQP_WRK_SH");				//크레인작업총매수
		intYD_EQP_WRK_WT 		= ydDaoUtils.paraRecChkNullInt(wrkRec, 		"YD_EQP_WRK_WT");				//크레인작업총중량
		dblYD_EQP_WRK_T  		= ydDaoUtils.paraRecChkNullDouble(wrkRec, 	"YD_EQP_WRK_T");				//크레인작업총두께
		intYD_EQP_WRK_L  		= ydDaoUtils.paraRecChkNullInt(wrkRec, 		"YD_EQP_WRK_L");				//크레인작업최대길이

		intYD_STK_BED_LYR_MAX 	= ydDaoUtils.paraRecChkNullInt(bedRec, 		"YD_STK_BED_LYR_MAX");			//베드정보 - 단MAX
		intYD_STK_BED_WT_MAX  	= ydDaoUtils.paraRecChkNullInt(bedRec, 		"YD_STK_BED_WT_MAX");			//베드정보 - 총중량
		dblYD_STK_BED_H_MAX   	= ydDaoUtils.paraRecChkNullDouble(bedRec, 	"YD_STK_BED_H_MAX");			//베드정보 - 총높이
//		intYD_STK_BED_L_MAX   	= ydDaoUtils.paraRecChkNullInt(bedRec, 		"YD_STK_BED_L_MAX");			//베드정보 - 총길이
		intYD_STK_COL_L   		= ydDaoUtils.paraRecChkNullInt(bedRec, 		"YD_STK_COL_L");				//베드정보 - 적치열길이(적치가능 길이)로 체크

		intYD_MTL_SH     		= ydDaoUtils.paraRecChkNullInt(bedRec, 		"YD_MTL_SH");					//적치된 재료의 총매수
		intYD_MTL_WT_SUM 		= ydDaoUtils.paraRecChkNullInt(bedRec, 		"YD_MTL_WT_SUM");				//적치된 재료의 총중량
		dblYD_MTL_T_SUM  		= ydDaoUtils.paraRecChkNullDouble(bedRec, 	"YD_MTL_T_SUM");				//적치된 재료의 총두께
		intYD_MTL_L_SUM  		= ydDaoUtils.paraRecChkNullInt(bedRec, 		"YD_MTL_L_SUM");				//적치된 재료의 총길이

		szYdStkColGp			= ydDaoUtils.paraRecChkNull(bedRec,			"YD_STK_COL_GP");
		szYdStkSpanGp			= ydUtils.substr(szYdStkColGp, 2, 2);
		szYdStkBedNo			= ydDaoUtils.paraRecChkNull(bedRec,			"YD_STK_BED_NO");
		szYdStkLyrNo			= ydDaoUtils.paraRecChkNull(bedRec,			"YD_STK_LYR_NO");

		szLogMsg = "[" + szOperationName + "] 베드정보 >>>> " + szYdStkColGp + ", SPAN::" + szYdStkSpanGp + ", 베드::" + szYdStkBedNo + ", 단::" + szYdStkLyrNo;
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] 작업총매수[" + intYD_EQP_WRK_SH + "], 작업총중량[" + intYD_EQP_WRK_WT + "], 작업총두께[" + dblYD_EQP_WRK_T + "], 작업길이[" + intYD_EQP_WRK_L + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

//		szLogMsg = "[" + szOperationName + "] 베드정보 - 단MAX[" + intYD_STK_BED_LYR_MAX + "], 총중량[" + intYD_STK_BED_WT_MAX + "], 총높이[" + dblYD_STK_BED_H_MAX + "], 총길이["+intYD_STK_BED_L_MAX+"]";
		szLogMsg = "[" + szOperationName + "] 베드정보 - 단MAX[" + intYD_STK_BED_LYR_MAX + "], 총중량[" + intYD_STK_BED_WT_MAX + "], 총높이[" + dblYD_STK_BED_H_MAX + "], 총길이[" + intYD_STK_COL_L + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		szLogMsg = "[" + szOperationName + "] 적치된 재료의 총매수[" + intYD_MTL_SH + "], 총중량[" + intYD_MTL_WT_SUM + "], 총두께[" + dblYD_MTL_T_SUM + "], 총길이[" + intYD_MTL_L_SUM + "]";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		if ("BC".equals(szYdStkSpanGp) || "BS".equals(szYdStkSpanGp) || "RT".equals(szYdStkSpanGp) || "CN".equals(szYdStkSpanGp) ||
			"CB".equals(szYdStkSpanGp) || "TD".equals(szYdStkSpanGp)) {
			szLogMsg = "[" + szOperationName + "] 설비일때는 매수 체크를 SKIP 함 >>>> " + szYdStkColGp;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);
		} else {
			// 적치가능 매수 체크
			if (intYD_STK_BED_LYR_MAX - intYD_MTL_SH > 0) {
				intYD_STKABLE_BED_LYR = intYD_MTL_SH + 1;
			}

			if (intYD_STK_BED_LYR_MAX >= intYD_MTL_SH + intYD_EQP_WRK_SH)	{
				szLogMsg = "[" + szOperationName + "] 해당하는 적치베드에 적치가능매수 통과";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);
			} else {
				szRtnMsg = "해당하는 적치베드에 적치가능매수 실패 ";
				szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
				intYD_BED_ERR_CD = JPlateYdConst.YD_BED_ERR_CD_SH_OVER;
			//	return szRtnMsg;
			}
		}

		// 적치가능 중량 체크
		if (intYD_STK_BED_WT_MAX - intYD_MTL_WT_SUM > 0) {
			intYD_STKABLE_BED_WT = intYD_STK_BED_WT_MAX - intYD_MTL_WT_SUM;
		}

		if (intYD_STK_BED_WT_MAX >= intYD_MTL_WT_SUM + intYD_EQP_WRK_WT) {
			szLogMsg = "[" + szOperationName + "] 해당하는적치베드에 적치가능중량 통과";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);
		} else {
			szRtnMsg = "해당하는 적치베드에 적치가능중량 실패";
			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			intYD_BED_ERR_CD = JPlateYdConst.YD_BED_ERR_CD_WT_OVER;
		//	return szRtnMsg;
		}

		// 적치가능 높이 체크
		if (dblYD_STK_BED_H_MAX - dblYD_MTL_T_SUM > 0) {
			dblYD_STKABLE_BED_H = dblYD_STK_BED_H_MAX - dblYD_MTL_T_SUM;
		}

		if (dblYD_STK_BED_H_MAX >= dblYD_MTL_T_SUM + dblYD_EQP_WRK_T) {
			szLogMsg = "[" + szOperationName + "] 해당하는 적치베드에 적치가능높이 통과";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);
		} else {
			szRtnMsg = "해당하는 적치베드에 적치가능높이 실패";
			szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
			intYD_BED_ERR_CD = JPlateYdConst.YD_BED_ERR_CD_H_OVER;
		//	return szRtnMsg;
		}

		if ("BC".equals(szYdStkSpanGp) || "BS".equals(szYdStkSpanGp) || "RT".equals(szYdStkSpanGp) || "CN".equals(szYdStkSpanGp) ||
			"CB".equals(szYdStkSpanGp) || "TD".equals(szYdStkSpanGp)) {

			szLogMsg = "[" + szOperationName + "] 설비일때는 길이 체크를 SKIP 함 >>>> " + szYdStkColGp;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.DEBUG, logId);

		} else {
			// 적치가능 길이 체크
			if (intYD_EQP_WRK_L > intYD_STK_COL_L) {
				intYD_EQP_WRK_L = intYD_STK_COL_L;
			}
			intYD_STKABLE_BED_L = intYD_STK_COL_L - intYD_MTL_L_SUM;			// 적치가능 길이 = 베드길이 - 적치중재료의 길이합

			if (intYD_STKABLE_BED_L >= intYD_EQP_WRK_L) {
				szLogMsg = "[" + szOperationName + "] 해당하는 적치베드에 적치가능길이 통과";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.INFO, logId);
			} else {
				szRtnMsg = "해당하는 적치베드에 적치가능길이 실패 >>>> 가능길이::" + intYD_STKABLE_BED_L + ", 재료길이::" + intYD_EQP_WRK_L;
				szLogMsg = "[" + szOperationName + "] " + szRtnMsg;
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR, logId);
				intYD_BED_ERR_CD = JPlateYdConst.YD_BED_ERR_CD_L_OVER;
			//	return szRtnMsg;
			}
		}

		if( intYD_BED_ERR_CD == 0 ) {
			intYD_BED_ERR_CD = JPlateYdConst.YD_BED_STACKABLE;
		}

		bedRec.setField("YD_STKABLE_BED_LYR", 	String.valueOf(intYD_STKABLE_BED_LYR));
		bedRec.setField("YD_STKABLE_BED_WT",  	String.valueOf(intYD_STKABLE_BED_WT));
		bedRec.setField("YD_STKABLE_BED_H", 	String.valueOf(dblYD_STKABLE_BED_H));
		bedRec.setField("BED_ERR_CD", 			String.valueOf(intYD_BED_ERR_CD));

		return szRtnMsg;
	}

	/**
	 * 작업예약/재료삭제
	 * @param szYD_WBOOK_ID
	 * @param szFromMethod
	 * @return
	 * @throws JDTOException
	 */
	public static String delYdWrkbookNMtl(String pYdWbookId, String pModifier) throws JDTOException {

		String szRtnMsg			= JPlateYdConst.RETN_CD_SUCCESS;
		String szMsg			= "";
		String szMethodName		= "delYdWrkbookNMtl";
		String szOperationName	= "작업예약/재료삭제";
		int    intRtnVal		= 0;

		JDTORecord recPara		= null;

		JPlateYdWrkbookDAO    ydWrkbookDao		= new JPlateYdWrkbookDAO();
		JPlateYdWrkbookMtlDAO ydWrkbookmtlDao	= new JPlateYdWrkbookMtlDAO();
		JPlateYdStockDAO      ydStockDAO		= new JPlateYdStockDAO();

		//--------------------------------------------------------------------------------------------
		//	작업예약재료 삭제
		//--------------------------------------------------------------------------------------------
		szMsg = "["+szOperationName+"] 작업예약["+pYdWbookId+"]재료 삭제 시작";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		recPara = JDTORecordFactory.getInstance().create();
		recPara.setField("YD_WBOOK_ID",		pYdWbookId);
		recPara.setField("DEL_YN", 			"Y");
		recPara.setField("MODIFIER", 		ydUtils.substr(pModifier, 0, 10));

		intRtnVal = ydWrkbookmtlDao.deldWrkbookMtl(recPara);
		if (intRtnVal == 0) {
			szMsg = "["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			szRtnMsg = JPlateYdConst.RETN_CD_NOTEXIST;
		} else if (intRtnVal < 0) {
			szMsg = "["+szOperationName+"] 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
		}

		szMsg = "["+szOperationName+"] 작업예약["+pYdWbookId+"]재료 삭제 완료 - 반환메세지 : " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		if (!JPlateYdConst.RETN_CD_SUCCESS.equals(szRtnMsg) && !JPlateYdConst.RETN_CD_NOTEXIST.equals(szRtnMsg)) {
			return szRtnMsg;
		}

		//--------------------------------------------------------------------------------------------
		//	저장품에서 작업예약ID와 스케줄코드 Clear시킴
		//--------------------------------------------------------------------------------------------
		szMsg = "["+szOperationName+"] 저장품에서 작업예약ID["+pYdWbookId+"]와 스케줄코드 Clear 시작";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		intRtnVal = ydStockDAO.updSchCdByYdWbookId(recPara);
		if (intRtnVal == 0) {
			szMsg = "["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			szRtnMsg = JPlateYdConst.RETN_CD_NOTEXIST;
		} else if (intRtnVal < 0) {
			szMsg = "["+szOperationName+"] 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
		}

		szMsg = "["+szOperationName+"] 저장품에서 작업예약ID["+pYdWbookId+"]와 스케줄코드 Clear 완료 - 반환메세지 : " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		//--------------------------------------------------------------------------------------------
		//	작업예약 삭제
		//--------------------------------------------------------------------------------------------
		szMsg = "["+szOperationName+"] 작업예약["+pYdWbookId+"] 삭제 시작";
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		intRtnVal = ydWrkbookDao.delYdWrkbook(recPara);		// intGp == 0
		if (intRtnVal == 0) {
			szMsg = "["+szOperationName+"] 존재하지 않습니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			szRtnMsg = JPlateYdConst.RETN_CD_NOTEXIST;
		} else if (intRtnVal < 0) {
			szMsg = "["+szOperationName+"] 오류발생 - 반환값 : " + Integer.toString(intRtnVal);
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
		}

		szMsg = "["+szOperationName+"] 작업예약["+pYdWbookId+"] 삭제 완료 - 반환메세지 : " + szRtnMsg;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
		//--------------------------------------------------------------------------------------------

		return szRtnMsg;
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
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		bBreRule = GetBreRule1.getYDB182(szYD_EQP_ID.substring(4), jdtoRcd);

		szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 대차작업지정기준를 BRE Rule로부터 조회 완료 - 반환값 : " + bBreRule;
		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		if (bBreRule) {
			try {

    			ydUtils.displayRecord(szOperationName, jdtoRcd);

    			szUSAGE_YN				= ydDaoUtils.paraRecChkNull(jdtoRcd, "USAGE_YN");
    			szWORK_GP				= ydDaoUtils.paraRecChkNull(jdtoRcd, "WORK_GP");
    			szYD_CARLD_BAY_GP		= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_CARLD_BAY_GP");
    			szYD_CARUD_BAY_GP		= ydDaoUtils.paraRecChkNull(jdtoRcd, "YD_CARUD_BAY_GP");

    		} catch(JDTOException ex) {
    			szMsg="["+szOperationName+"] 대차설비["+szYD_EQP_ID+"]에대한 대차작업지정기준를 BRE Rule로부터 조회 시 오류발생 - 기본값으로 설정[N]";
    			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

    			szUSAGE_YN			= "N";
    			szWORK_GP			= "";
    			szYD_CARLD_BAY_GP	= "";
    			szYD_CARUD_BAY_GP	= "";
    		}
		} else {
			szUSAGE_YN			= "N";
			szWORK_GP			= "";
			szYD_CARLD_BAY_GP	= "";
			szYD_CARUD_BAY_GP	= "";
		}

		szRule = new String[]{szUSAGE_YN, szWORK_GP, szYD_CARLD_BAY_GP, szYD_CARUD_BAY_GP};

		return szRule;
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
		String szRtnMsg = JPlateYdConst.RETN_CD_SUCCESS;
		//저장품DAO
		JPlateYdStockDAO ydStockDao = new JPlateYdStockDAO();

		int intRtnVal 			= 0;

		String szLogMsg 		= null;
		String szMethodName 	= "getPtCommStock";

		JDTORecordSet rsOut 	= JDTORecordFactory.getInstance().createRecordSet("");
		JDTORecord recInParam 	= JDTORecordFactory.getInstance().create("");
		JDTORecord recTemp 		= null;

		try {

			recInParam.setField("STL_NO", szSTL_NO);

			//공통테이블에서 재료를 먼저 조회한다.
			intRtnVal = ydStockDao.getYdStockBookOut(recInParam, rsOut);

	        if (intRtnVal <= 0) {
	            if (intRtnVal == 0) {
	            	szLogMsg = "공통테이블에 재료[" + szSTL_NO + "]가 존재하지 않습니다. 에러코드 : " + intRtnVal;
	                ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
	            } else if (intRtnVal == -2) {
	            	szLogMsg = "parameter error!!!, ErrorCode:" + intRtnVal;
	                ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
	            }
	            szRtnMsg = JPlateYdConst.RETN_CD_FAILURE;
	        } else {
	        	rsResult.addRecord(recTemp);
	        }

		} catch(JDTOException ex) {
			szLogMsg = " 에러 발생 : " + ex.getMessage();
            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szLogMsg, JPlateYdConst.ERROR);
            throw ex;
		}
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
		JPlateYdSchRuleDAO ydSchRuleDao = new JPlateYdSchRuleDAO();

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
			intRtnVal = ydSchRuleDao.getYdSchrule(recPara, rsResult);		// intGp == 0

			//리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 중복되었습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "스케줄코드(" + szSchCd + ")에 대한 스케줄기준 데이터가 없습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 parameter error 발생!";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "스케줄코드(" + szSchCd + ")로 스케줄기준 조회중 오류 발생!";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e) {
			szMsg = "스케줄기준 체크 중 Exception 발생 ";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;

	} //end of chkGetSchRule


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

		int 	intRtnVal         = 0;
		double 	intCrnTongWTol    = 0;				//크레인 집게폭 오차
		long 	lngWrkAbleWt      = 0;				//크레인 허용 중량
		int 	intWrkAbleSh      = 0;				//크레인 허용 매수

		try {

			//크레인 집게허용 오차
			intCrnTongWTol = ydDaoUtils.paraRecChkNullDouble(recCrnSpec,  "YD_CRN_TONG_W_TOL");
			//크레인 작업가능 중량
			lngWrkAbleWt   = ydDaoUtils.paraRecChkNullLong(recCrnSpec, "YD_WRK_ABLE_WT");
			//크레인 작업가능 매수
			intWrkAbleSh   = ydDaoUtils.paraRecChkNullInt(recCrnSpec,  "YD_WRK_ABLE_SH");


			//크레인사양의 집게허용 오차 Check
			if (dblMaxWidth > dblCurrWidth + intCrnTongWTol) {

				szMsg = "dblMaxWidth : " + dblMaxWidth + " > dblCurrWidth : " + dblCurrWidth + " intCrnTongWTol : " + intCrnTongWTol;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return intRtnVal = -1;
			}

			//크레인 작업가능 중량 Check
			if (lngWrkAbleWt < lngSumWt) {
				szMsg = "lngWrkAbleWt : " + lngWrkAbleWt + " < lngSumWt : " + lngSumWt;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return intRtnVal = -2;
			}

			//크레인 작업가능 매수 Check
			if (intWrkAbleSh < intMtlSh) {
				szMsg = "intWrkAbleSh : " + intWrkAbleSh + " < intMtlSh : " + intMtlSh;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				return intRtnVal = -3;
			}

			intRtnVal = 1;

		} catch(Exception e) {
			szMsg = "크레인사양과 비교 체크 중 Exception Error 발생 ";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return intRtnVal = -100;
		}

		return intRtnVal;

	} //end of chkGetCrnspec

	/**
	 * 오퍼레이션명 : 스케쥴기준을 조회하여 크레인 정보를 반환하는 메소드
	 * @param szYD_SCH_CD
	 * @param recResult
	 * @return int
	 *			 1 : 메소드 호출 성공
	 * 			-1 : 스케쥴금지
	 * 			-2 : 작업크레인고장이고 대체크레인정보가 없는 경우
	 * 			-3 : 작업크레인고장이고 대체크레인 고장인 경우 작업 불가
	 * 			-4 : 스케쥴기준 조회에러
	 * 			-5 : 크레인설비 정보 조회시 에러 발생
	 * @throws JDTOException
	 */
	public static String getCrnInfoByCrnSchRule(String szYD_SCH_CD, JDTORecord recResult) throws JDTOException {

		String 	szMsg 			= "";
		String 	szMethodName 	= "getCrnInfoByCrnSchRule";
		String 	szCrn 			= "";
		String 	szYD_SCH_PRIOR 	= "9";
		// 리턴 recordSet 생성
		JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");

		// 스케줄 기준 체크
		boolean blnRtnVal = chkGetSchRule(szYD_SCH_CD, rsResult);
		if (!blnRtnVal) {
			szMsg = "스케쥴기준 조회에러";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		}

		// 레코드 추출
		rsResult.first();
		JDTORecord recPara = rsResult.getRecord();

		// 스케줄CD 체크
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

		// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
		if ("Y".equals(szYD_SCH_PROH_EXN)) {
			szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		}

		// 작업크레인
		String szYD_WRK_CRN 		= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN");
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_CRN_PRIOR");
		// 대체크레인유무
		String szYD_ALT_CRN_YN 		= ydDaoUtils.paraRecChkNull(recPara,"YD_ALT_CRN_YN");
		// 대체크레인
		String szYD_ALT_CRN 		= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN");
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR");

		// 작업크레인 설비 상태 체크
		blnRtnVal = eqpStatCheck(szYD_WRK_CRN);

		// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
		if (!blnRtnVal) {

			szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);

			// 대체크레인의 유무를 체크한다.
			// 대체크레인이 없으면 에러 리턴
			if (!"Y".equals(szYD_ALT_CRN_YN)) {

				szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;

			}
			// 대체크레인이 있으면 대체크레인 설비 상태 체크
			blnRtnVal = eqpStatCheck(szYD_ALT_CRN);
			// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
			if (!blnRtnVal) {

				szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szMsg;

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
		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		blnRtnVal = chkGetCrnSpec(szCrn, rsResult);
		if (!blnRtnVal) {
			szMsg = "크레인설비 정보 조회시 에러 발생";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szMsg;
		}
		rsResult.first();
		recPara = rsResult.getRecord();
		//작업가능한 크레인
		recResult.setField("YD_WRK_CRN", 		szCrn);
		//스케쥴우선순위
		recResult.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR);
		//크레인 작업허용중량
		recResult.setField("YD_WRK_ABLE_WT", 	ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_WT"));
		//크레인 집게허용 오차
		recResult.setField("YD_CRN_TONG_W_TOL", ydDaoUtils.paraRecChkNull(recPara,"YD_CRN_TONG_W_TOL"));
		//크레인 작업가능 매수
		recResult.setField("YD_WRK_ABLE_SH", 	ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_SH"));

		return JPlateYdConst.RETN_CD_SUCCESS;
	}	// end of getCrnInfoByCrnSchRule

	/**
	 * 오퍼레이션명 : 설비상태 체크
	 *
	 * @param   String szEqpId 설비ID
	 * @return boolean true(설비사용가능), false(설비사용불가)
	 * @throws JDTOException
	 */
	public static boolean eqpStatCheck(String szEqpId)throws JDTOException  {

		boolean blnRtnVal      	= false;				//리턴값(boolean)
		String 	szMsg           = null;					//메세지
		String 	szMethodName    = "eqpStatCheck";		//메소드명
		String 	szYD_EQP_STAT	= null;					//설비상태

		JDTORecord recPara     	= null;					//레코드 선언
		JDTORecordSet rsResult 	= null;					//레코드셋 선언

		try {
			//레코드 생성
			recPara = JDTORecordFactory.getInstance().create();
			//레코드셋 생성
			rsResult = JDTORecordFactory.getInstance().createRecordSet("");

			//설비ID를 작업크레인으로 설정
			recPara.setField("YD_EQP_ID", szEqpId);

			//설비 체크 및 데이터 조회
			blnRtnVal = chkGetEqp(szEqpId, rsResult);
			if (!blnRtnVal) {
				return blnRtnVal;
			}

			//레코드 추출
			rsResult.first();
			recPara = rsResult.getRecord();

			//설비상태
			szYD_EQP_STAT = ydDaoUtils.paraRecChkNull(recPara, "YD_EQP_STAT");

			//크레인의 상태가 'T'이면 false 리턴.
			if (JPlateYdConst.YD_EQP_STAT_BREAK.equals(szYD_EQP_STAT)) {

				szMsg = "설비ID(" + szEqpId + ")의 상태가 고장(" + szYD_EQP_STAT + ") 입니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else {

				blnRtnVal = true;

			}
		} catch(Exception e) {
			szMsg = "설비상태 체크 중 예외발생! Exception Error 발생";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
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
		JPlateYdEqpDAO ydEqpDao = new JPlateYdEqpDAO();

		boolean blnRtnVal     	= false;
		int 	intRtnVal       = 0;
		String 	szMethodName   	= "chkGetEqp";
		String 	szMsg          	= null;

		//레코드 선언
		JDTORecord recPara       = null;

		try {

			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//설비ID
			recPara.setField("YD_EQP_ID", szEqpId);

			//설비 테이블 조회
			intRtnVal = ydEqpDao.getYdEqp(recPara, rsResult);		// intGp == 0

			//리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 중복되었습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "설비ID(" + szEqpId + ")에 대한 설비 데이터가 없습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 parameter error 발생!";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "설비ID(" + szEqpId + ")로 설비 조회중 오류 발생!";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e) {
			szMsg = "설비 유무체크 및 조회결과 데이터 반환 중 예외발생!";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return blnRtnVal = false;
		}
		return blnRtnVal;
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
		JPlateYdCrnSpecDAO ydCrnSpecDao = new JPlateYdCrnSpecDAO();

		boolean blnRtnVal     	= false;
		int 	intRtnVal       = 0;
		String 	szMethodName	= "chkGetCrnSpec";
		String 	szMsg          	= null;

		//레코드 선언
		JDTORecord recPara      = null;

		try {
			//레코드 생성
			recPara  = JDTORecordFactory.getInstance().create();

			//크레인 설비ID
			recPara.setField("YD_EQP_ID", szEqpId);

			//크레인사양 조회
			intRtnVal = ydCrnSpecDao.getYdCrnSpec(recPara, rsResult);		// intGp == 0

			//리턴값 메세지처리
			if (intRtnVal > 1) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 중복되었습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == 1) {

				blnRtnVal = true;

			} else if (intRtnVal == 0) {

				szMsg = "크레인설비ID(" + szEqpId + ")에 대한 크레인사양 데이터가 없습니다.";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else if (intRtnVal == -2) {

				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 parameter error 발생!";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			} else {

				szMsg = "크레인설비ID(" + szEqpId + ")로 크레인사양 조회중 오류 발생!";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				blnRtnVal = false;

			}
		} catch(Exception e) {
			szMsg = "크레인사양 유무체크 및 조회결과 데이터 반환 중 예외발생!";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			blnRtnVal = false;
		}
		return blnRtnVal;
	} //end of chkGetCrnSpec

	/**
	 * 오퍼레이션명 : 야드별로 대차상차스케줄 코드를 부여하는 함수
	 * @param szYD_STK_COL_GP
	 * @return
	 */
	public static String[] getSchCdNTcar(String szYD_STK_COL_GP) {

		String 	szMsg 			= null;									//메세지값
		String 	szYD_GP 		= szYD_STK_COL_GP.substring(0, 1);		//야드구분
		String 	szYD_SCH_CD 	= "";									//스케줄코드
		String 	szTCAR 			= "";									//대차설비ID
		String	szMethodName	= "getSchCdNTcar";						//메소드명
		String	szBAY_GP		= szYD_STK_COL_GP.substring(1, 2);		//동
		String	szSPAN_GP		= szYD_STK_COL_GP.substring(2, 4);		//스판

		//반환값
		String[] retValue = new String[2];

		if ("F".equals(szYD_GP)) {		//야드구분이 2후판정정야드
			if ("01".equals(szSPAN_GP) || "02".equals(szSPAN_GP)) {		// 01,02스판은 #1대차 배정
				szYD_SCH_CD = szYD_GP + szBAY_GP + "TC01UM";
				szTCAR = szYD_GP + "XTC01";
			} else if("03".equals(szSPAN_GP)) {							// 03스판은 #2대차 배정
				szYD_SCH_CD = szYD_GP + szBAY_GP + "TC02UM";
				szTCAR = szYD_GP + "XTC02";
			}
		}else{
			szMsg = "현재 지원하지 않는 야드구분[" + szYD_GP + "]입니다." ;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		}
		retValue[0] = szYD_SCH_CD;
		retValue[1] = szTCAR;
		return retValue;
	}

	/**
	 * 오퍼레이션명 : 	저장위치 수정/북아웃 시 현재 위치 체크
	 *  			저장위치가 2후판 정정야드가 아닐경우 오류로 처리
	 * @param szYD_STK_COL_GP
	 * @return
	 */
	public static String checkUpdYdLoc(String pStlNo, String pYdGp, String pDelYn) {

		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		String	szRtnMsg			= "";
		String 	szMsg        		= "";
		String 	szMethodName		= "checkUpdYdLoc";
//		String 	szOperationName		= "현재 저장위치 체크";

		int 	intRtnVal 			= 0;

		JDTORecordSet rsResult  	= JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord recPara			= null;
		JDTORecord recTemp 			= null;

		String	szYdStkColGp		= "";
		String	szYdStkLyrMtlStat	= "";
		String	szYdGp				= "";

		try {

			//------------------------------------------------------------------
			// 현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
			//------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO",		pStlNo);			// 재료번호
			recPara.setField("YD_GP",		"%");				// 전체야드 조회

			intRtnVal = ydStkLyrDao.getYdStklyrByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				rsResult.first();
				for(int ii=0; ii<intRtnVal; ii++) {

					recTemp = JDTORecordFactory.getInstance().create();
					recTemp = rsResult.getRecord(ii);

					szYdStkColGp 	  	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
					szYdStkLyrMtlStat 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
					szYdGp 				= ydUtils.substr(szYdStkColGp, 0, 1);

					// 적치중이거나 권상예약 재료만 조회
					if ("C".equals(szYdStkLyrMtlStat) || "U".equals(szYdStkLyrMtlStat)) {
						if (!szYdGp.equals(pYdGp) && "N".equals(pDelYn)) {
							if ("T".equals(szYdGp)) {
								szRtnMsg = "해당 재료("+pStlNo+")는 2후판 제품창고 야드에 적치중입니다!";
							} else if ("P".equals(szYdGp)) {
								szRtnMsg = "해당 재료("+pStlNo+")는 1후판 정정 야드에 적치중입니다!";
							} else if ("K".equals(szYdGp)) {
								szRtnMsg = "해당 재료("+pStlNo+")는 1후판 제품창고 야드에 적치중입니다!";
							} else {
								szRtnMsg = "오류!....해당 재료("+pStlNo+")다른 야드["+szYdGp+"]에 적치중 입니다!";
							}
							szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
							ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}

					if (szYdGp.equals(pYdGp)) {
						if ("U".equals(szYdStkLyrMtlStat)) {
							szRtnMsg = "오류!....해당 재료("+pStlNo+")는 권상예약 중! [" + szYdStkColGp + "]";
							szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
							ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						} else if ("D".equals(szYdStkLyrMtlStat)) {
							szRtnMsg = "오류!....해당 재료("+pStlNo+")는 권하예약 중! [" + szYdStkColGp + "]";
							szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
							ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
				}
			}
		} catch (Exception e) {
			// Exception발생시
			szRtnMsg = "현재 저장위치 체크시 Exception 발생 >>>>" + e.getMessage();
			szMsg 	 = "["+szMethodName+"] " + szRtnMsg;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 오퍼레이션명 : 	권하실적 처리시 TO위치 체크
	 *  			-- 01베드 부터 선택 하도록 체크
	 * @param  적치열, 적치베드, 적치단
	 * @return
	 */
	public static String checkDownLoc(String pYdStkColGp, String pYdStkBedNo, String pYdStkLyrNo) {

/*  2013.11.18 주석처리
		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		String	szRtnMsg			= "";
		String 	szMsg        		= "";
		String 	szMethodName		= "checkDownLoc";
//		String 	szOperationName		= "권하실적  TO위치 체크";

		int 	intRtnVal 			= 0;

		JDTORecordSet rsResult  	= JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord recPara			= null;
		JDTORecord recTemp 			= null;

		String	szYdStkLyrMtlStat	= "";
		String	szYdSpanGp			= "";
		int		iTopMtlCnt			= 0;

		try {

			szYdSpanGp = ydUtils.substr(pYdStkColGp, 2, 2);

			// 설비 베드일때 SKIP
			if ("CN".equals(szYdSpanGp) || "BS".equals(szYdSpanGp) || "RT".equals(szYdSpanGp) || "CB".equals(szYdSpanGp)) {
				return JPlateYdConst.RETN_CD_SUCCESS;
			}

			// 01 베드일때 SKIP
			if ("01".equals(pYdStkBedNo)) {
				return JPlateYdConst.RETN_CD_SUCCESS;
			}

			//------------------------------------------------------------------
			// 01베드의 적치상태가 'C':'적치중'이 아닐경우 오류로 처리
			//------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP",		pYdStkColGp);			// 야드적치열
			recPara.setField("YD_STK_BED_NO",		"01"); 					// 야드적치열
			recPara.setField("YD_STK_LYR_NO",		pYdStkLyrNo);			// 야드적치단

			intRtnVal = ydStkLyrDao.getYdStklyrWithTopCnt(recPara, rsResult);
			if (intRtnVal > 0) {
				rsResult.first();

				recTemp = JDTORecordFactory.getInstance().create();
				recTemp = rsResult.getRecord();

				szYdStkLyrMtlStat 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
				iTopMtlCnt			= ydDaoUtils.paraRecChkNullInt(recTemp, "TOP_MTL_CNT");

				// 상단에 재료가 적치중일때는 SKIP
				if (iTopMtlCnt > 0) {
					szMsg 	 = "["+szMethodName + "] 상단에 재료가 적치중일때는 SKIP >>>> " + Integer.toString(iTopMtlCnt);
					ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
				} else {
					// 적치중이거나 권상예약 재료만 조회
					if (!"C".equals(szYdStkLyrMtlStat)) {
						szRtnMsg = "권하시 01 베드 부터 선택하세요!";
						szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
						ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
						return szRtnMsg;
					}
				}
			} else {
				szRtnMsg = "권하실적  TO위치 체크 오류 >>>> " + pYdStkColGp;
				szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
				return szRtnMsg;
			}

		} catch (Exception e) {
			// Exception발생시
			szRtnMsg = "권하실적  TO위치 체크시 Exception 발생 >>>>" + e.getMessage();
			szMsg 	 = "["+szMethodName+"] " + szRtnMsg;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}
*/
		return JPlateYdConst.RETN_CD_SUCCESS;
	}

	/**
	 * 오퍼레이션명 : 	조업L3로 저장위치 변경정보 송신처리
	 *  			-- EAI LAYOUT의 1회에 전송 데이타가 20건으로 제한되어  20건 초과시 LOOP처리
	 * @param  적치열, 적치베드, 적치단
	 * @return
	 */
	public static String sendL3YDPPJ011(JDTORecord pL3Para) {

		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		String	szRtnMsg			= "";
		String 	szMsg        		= "";
		String 	szMethodName		= "sendL3YDPPJ011";
//		String 	szOperationName		= "조업L3로 저장위치 변경정보 송신";

		JDTORecordSet rsResult  	= JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord recPara			= null;
		JDTORecord recL3Para 		= null;

		String	szYdStkColFr		= "";			// From적치열
		String	szYdStkBedFr  		= "";			// From적치BED
		String	szYdStkColTo  		= "";			// TO적치열
		String	szYdStkBedTo		= "";			// TO적치BED
//		String	szYdEqpWrkSh		= "";			// 야드설비작업매수
		String	szStlNoList			= "";			// 재료번호 List
		String	szSendList			= "";			// 전송대상 재료번호 List
		String  szBookOutCrn             = "";           // 작업 크레인  
		
		String  szIS_PF_TO_PF       = "";           // 후판 공장간 이송여부 체크 flag

		String	arrStlNo[]			= null;
		int		iSendCnt			= 0;
		int		iRtnVal				= 0;
		
		YdPICommDAO   ydPICommDAO   = new YdPICommDAO();

		try {

			szMsg = "["+szMethodName+"] <<<< 후판조업 저장위치변경정보 전송 START >>>>";
            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			szYdStkColFr	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_STK_COL_FR");
			szYdStkBedFr  	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_STK_BED_FR");
			szYdStkColTo  	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_STK_COL_TO");
			szYdStkBedTo	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_STK_BED_TO");
//			szYdEqpWrkSh	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_EQP_WRK_SH");
			szStlNoList		= ydDaoUtils.paraRecChkNull(pL3Para, "ARR_STL_NO");
			szBookOutCrn         =ydDaoUtils.paraRecChkNull(pL3Para, "BOOK_OUT_CRN");
			
			szIS_PF_TO_PF    = ydDaoUtils.paraRecChkNull(pL3Para, "IS_PF_TO_PF"			); 
			
			szMsg = "["+szMethodName+"] szBookOutCrn>>>> :: " + szBookOutCrn ;
            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			//REQ202306466285 대상 크레인 추가 강길모책임 요청사항.
            
            szMsg = "["+szMethodName+"] szIS_PF_TO_PF 공장간이송여부 >>>> :: " + szIS_PF_TO_PF ;
            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

			// 재료번호 List로 전송시
			if ("".equals(szStlNoList)) {

				rsResult = JDTORecordFactory.getInstance().createRecordSet("tempYd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 		 szYdStkColTo);
				recPara.setField("YD_STK_LYR_MTL_STAT1", "C");
				recPara.setField("YD_STK_LYR_MTL_STAT2", "U");
				iRtnVal = ydStkLyrDao.getByLocMtlStat(recPara, rsResult);

				if (iRtnVal > 0) {

					for(int ii=0; ii<rsResult.size(); ii++) {
						if (!"".equals(szStlNoList)) {
							szStlNoList = szStlNoList + ";";
						}
						szStlNoList = szStlNoList + ydDaoUtils.paraRecChkNull(rsResult.getRecord(ii), "STL_NO");
					}
				}
			}
			arrStlNo = szStlNoList.split(";");

			// 20건단위로 전송
			for(int ii=0; ii<arrStlNo.length; ii++) {

				if ("".equals(szSendList)) {
					szSendList = arrStlNo[ii];
				} else {
					szSendList = szSendList + ";" + arrStlNo[ii];
				}

				iSendCnt ++;
				if (iSendCnt >= 20) {
					recL3Para = JDTORecordFactory.getInstance().create();
					recL3Para.setField("MSG_ID", 			"YDPPJ011");
					recL3Para.setField("YD_STK_COL_FR", 	szYdStkColFr);					// From적치열
					recL3Para.setField("YD_STK_BED_FR", 	szYdStkBedFr);					// From적치BED
					recL3Para.setField("YD_STK_COL_TO", 	szYdStkColTo);					// TO적치열
					recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedTo);					// TO적치BED
					recL3Para.setField("YD_EQP_WRK_SH", 	Integer.toString(iSendCnt));	// 야드설비작업매수
					recL3Para.setField("ARR_STL_NO", 		szSendList);					// 전송대상 재료번호 List
					
					
					recL3Para.setField("BOOK_OUT_CRN", 		szBookOutCrn);					// 북아웃크레인
					//REQ202306466285 대상 크레인 추가 강길모책임 요청사항.
					
					recL3Para.setField("IS_PF_TO_PF", szIS_PF_TO_PF						);		// 후판 공장간 이송여부 flag
					
		            szRtnMsg 	= ydDelegate.sendMsg(recL3Para);

					szMsg = "["+szMethodName+"] 후판조업 저장위치변경정보 전송 결과>>>> 건수 :: " + iSendCnt + ", 결과 :: " + szRtnMsg;
		            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		            iSendCnt 	= 0;
		            szSendList 	= "";
				}
			}

			// 마지막 데이타 다시한번 전송
			if (!"".equals(szSendList)) {
				recL3Para = JDTORecordFactory.getInstance().create();
				recL3Para.setField("MSG_ID", 			"YDPPJ011");
				recL3Para.setField("YD_STK_COL_FR", 	szYdStkColFr);					// From적치열
				recL3Para.setField("YD_STK_BED_FR", 	szYdStkBedFr);					// From적치BED
				recL3Para.setField("YD_STK_COL_TO", 	szYdStkColTo);					// TO적치열
				recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedTo);					// TO적치BED
				recL3Para.setField("YD_EQP_WRK_SH", 	Integer.toString(iSendCnt));	// 야드설비작업매수
				recL3Para.setField("ARR_STL_NO", 		szSendList);					// 전송대상 재료번호 List
				
				recL3Para.setField("BOOK_OUT_CRN", 		szBookOutCrn);					// 북아웃크레인
				//REQ202306466285 대상 크레인 추가 강길모책임 요청사항.				
				
				recL3Para.setField("IS_PF_TO_PF", szIS_PF_TO_PF						);		// 후판 공장간 이송여부 flag
				
	            szRtnMsg = ydDelegate.sendMsg(recL3Para);

				szMsg = "["+szMethodName+"] 후판조업 저장위치변경정보 전송 결과>>>> 건수 :: " + iSendCnt + ", 결과 :: " + szRtnMsg;
	            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			}

			szMsg = "["+szMethodName+"] <<<< 후판조업 저장위치변경정보 전송 END >>>>";
            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);

		} catch (Exception e) {
			// Exception발생시
			szRtnMsg = "조업L3로 저장위치 변경정보 송신시 Exception 발생 >>>>" + e.getMessage();
			szMsg 	 = "["+szMethodName+"] " + szRtnMsg;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		return szRtnMsg;
	}

	/**
	 * 오퍼레이션명 : 해당 적치열의 최상단 번호 조회 (권하실적 최상단 검색)
	 *
	 * @param  	적치열, 적치베드, 재료번호, 야드권상작업수행구분, 최상단번호(""가 아닐때 해당 단이상의 재료만 검색)
	 * @return	최상단번호
	 */
	public static String getTopLyrNoByColGp(String pYdStkColGp, String pYdStkBedNo, String pStlNo, String pYdUpWrkActGp, String pTopLyrNo) {

		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		String	szRtnMsg			= "";
		String 	szMsg        		= "";
		String 	szMethodName		= "getTopLyrNoByColGp";
//		String 	szOperationName		= "해당 적치열의 최상단 번호 조회";

		int 	intRtnVal 			= 0;

		JDTORecordSet rsResult  	= JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord recPara			= null;
		JDTORecord recTemp 			= null;

		String	szRtnLyrNo 			= "000";
		String	szTopLyrNo			= "000";

		try {

			//------------------------------------------------------------------------------------------------
			// 권하위치의 최상단 정보 조회하여 파일링/횡작업/멀티 기준 단정보 SET
			//------------------------------------------------------------------------------------------------
			szTopLyrNo = pTopLyrNo;
			if (szTopLyrNo == null || "".equals(szTopLyrNo)) {
				szTopLyrNo = "000";
			}

			if ("000".equals(szTopLyrNo)) {
				// 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
				if ("H".equals(pYdUpWrkActGp) || "M".equals(pYdUpWrkActGp) || "F".equals(pYdUpWrkActGp)) {

					rsResult = JDTORecordFactory.getInstance().createRecordSet("retTmp");
					recPara  = JDTORecordFactory.getInstance().create();
					recPara.setField("YD_STK_COL_GP",       	pYdStkColGp);
					recPara.setField("YD_STK_LYR_MTL_STAT1",    "C");
					recPara.setField("YD_STK_LYR_MTL_STAT2",    "U");

					intRtnVal = ydStkLyrDao.getByLocMtlStat(recPara, rsResult);
					if (intRtnVal > 0) {
						recTemp 	= JDTORecordFactory.getInstance().create();
						rsResult.first();
						recTemp 	= rsResult.getRecord();
						szTopLyrNo 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_NO", "000");
						szTopLyrNo	= ydDaoUtils.stringPlusInt(szTopLyrNo, 1);		// 현재적치중인단 + 1 --> 적치가능단
					}
				}
			}

			//------------------------------------------------------------------------------------------------
			// 권하위치의 최상단 정보 조회
			//------------------------------------------------------------------------------------------------
			recPara = JDTORecordFactory.getInstance().create();
			recPara.setField("YD_STK_COL_GP", 	pYdStkColGp);
			recPara.setField("YD_STK_BED_NO", 	pYdStkBedNo);
			recPara.setField("STL_NO", 			pStlNo);
			recPara.setField("TOP_LYR_NO", 		szTopLyrNo);

			szRtnLyrNo = ydStkLyrDao.getRealTopLyr(recPara);

		} catch (Exception e) {
			// Exception발생시
			szRtnMsg = "해당 적치열의 최상단 번호 조회 Exception 발생 >>>>" + e.getMessage();
			szMsg 	 = "["+szMethodName+"] " + szRtnMsg;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
		}

		return szRtnLyrNo;
	}
	
/**********************************************************
* 1후판정정추가 SJH16 
**********************************************************/	
	/**
	 * 오퍼레이션명 : 1후판정정의  RT ZONE NO를 저장위치로 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getY2RtZoneToLoc(String pBookOutLoc) {
		return (String)htY2_RT_ZONE_BED.get(pBookOutLoc);
	}
	
	/**
	 * 오퍼레이션명 : 1후판정정의  저장위치를 RT ZONE NO로 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getLocToY2RtZone(String szTrkZoneNo) {
		return (String)htY2_BED_RT_ZONE.get(szTrkZoneNo);
	}	

	/**
	 * 오퍼레이션명 : 1후판정정의 저장위치로 RT ZONE NO 반환하는 메소드
	 * @param szPilingCode
	 * @return
	 */
	public static String getY2LocToRtZone(String pBookOutLoc) {
		return (String)htY2_BED_RT_ZONE.get(pBookOutLoc);
	}
	
	/**
	 * 오퍼레이션명 : 1후판정정의 1후판정정 L3 크레인번호 --> L2 크레인번호 변환
	 * @param szPilingCode
	 * @return
	 */
	public static String getY2CraneNoL2(String pL3CraneNo) {
		return (String)htY2_GET_L2_CRANE_NO.get(pL3CraneNo);
	}
	
	/**
	 * 오퍼레이션명 : 	저장위치 수정/북아웃 시 현재 위치 체크
	 *  			저장위치가 1후판 정정야드가 아닐경우 오류로 처리
	 * @param szYD_STK_COL_GP
	 * @return
	 */
	public static String checkUpdYdLocYdP(String pStlNo, String pYdGp, String pDelYn) {

		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		String	szRtnMsg			= "";
		String 	szMsg        		= "";
		String 	szMethodName		= "checkUpdYdLocYdP";
//		String 	szOperationName		= "현재 저장위치 체크";

		int 	intRtnVal 			= 0;

		JDTORecordSet rsResult  	= JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord recPara			= null;
		JDTORecord recTemp 			= null;

		String	szYdStkColGp		= "";
		String	szYdStkLyrMtlStat	= "";
		String	szYdGp				= "";

		try {

			//------------------------------------------------------------------
			// 현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
			//------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO",		pStlNo);			// 재료번호
			recPara.setField("YD_GP",		"%");				// 전체야드 조회

			intRtnVal = ydStkLyrDao.getYdStklyrByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				rsResult.first();
				for(int ii=0; ii<intRtnVal; ii++) {

					recTemp = JDTORecordFactory.getInstance().create();
					recTemp = rsResult.getRecord(ii);

					szYdStkColGp 	  	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
					szYdStkLyrMtlStat 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
					szYdGp 				= ydUtils.substr(szYdStkColGp, 0, 1);

					// 적치중이거나 권상예약 재료만 조회
					if ("C".equals(szYdStkLyrMtlStat) || "U".equals(szYdStkLyrMtlStat)) {
						if (!szYdGp.equals(pYdGp) && "N".equals(pDelYn)) {
							if ("T".equals(szYdGp)) {
								szRtnMsg = "해당 재료("+pStlNo+")는 2후판 제품창고 야드에 적치중입니다!";
							} else if ("F".equals(szYdGp)) {
								szRtnMsg = "해당 재료("+pStlNo+")는 2후판 정정 야드에 적치중입니다!";
							} else if ("K".equals(szYdGp)) {
								szRtnMsg = "해당 재료("+pStlNo+")는 1후판 제품창고 야드에 적치중입니다!";
							} else {
								szRtnMsg = "오류!....해당 재료("+pStlNo+")가 다른 야드["+szYdGp+"]에 적치중 입니다!";
							}
							szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
							ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}

					if (szYdGp.equals(pYdGp)) {
						if ("U".equals(szYdStkLyrMtlStat)) {
							szRtnMsg = "오류!....해당 재료("+pStlNo+")는 권상예약 중! [" + szYdStkColGp + "]";
							szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
							ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						} else if ("D".equals(szYdStkLyrMtlStat)) {
							szRtnMsg = "오류!....해당 재료("+pStlNo+")는 권하예약 중! [" + szYdStkColGp + "]";
							szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
							ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
							return szRtnMsg;
						}
					}
				}
			}
		} catch (Exception e) {
			// Exception발생시
			szRtnMsg = "현재 저장위치 체크시 Exception 발생 >>>>" + e.getMessage();
			szMsg 	 = "["+szMethodName+"] " + szRtnMsg;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR);
			return szRtnMsg;
		}

		return JPlateYdConst.RETN_CD_SUCCESS;
	}
	
	/**
	 * 오퍼레이션명 : 	조업L3로 저장위치 변경정보 송신처리
	 *  			-- EAI LAYOUT의 1회에 전송 데이타가 20건으로 제한되어  20건 초과시 LOOP처리
	 * @param  적치열, 적치베드, 적치단
	 * @return
	 */
	public static String sendL3YDPRJ011(JDTORecord pL3Para) {

		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		String	szRtnMsg			= "";
		String 	szMsg        		= "";
		String 	szMethodName		= "sendL3YDPRJ011";
//		String 	szOperationName		= "1후판조업L3로 저장위치 변경정보 송신";

		JDTORecordSet rsResult  	= JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord recPara			= null;
		JDTORecord recL3Para 		= null;

		String	szYdStkColFr		= "";			// From적치열
		String	szYdStkBedFr  		= "";			// From적치BED
		String	szYdStkColTo  		= "";			// TO적치열
		String	szYdStkBedTo		= "";			// TO적치BED
//		String	szYdEqpWrkSh		= "";			// 야드설비작업매수
		String	szStlNoList			= "";			// 재료번호 List
		String	szSendList			= "";			// 전송대상 재료번호 List
		String 	szPlBookOutCrane	= "";			// 북아웃 작업크레인(예:PBCRB1)  chito20230202
		String  szIS_PF_TO_PF       = "";           // 후판 공장간 이송여부 체크 flag
		
		String	arrStlNo[]			= null;
		int		iSendCnt			= 0;
		int		iRtnVal				= 0;

//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 로그 개선 
		String logId                     	= ydLogUtils.getJDTOLogId(pL3Para, "P");  		// JDTORecord 에서 logid get(1: JDTORecord.getResultCode(), Field명 - 2:UNIQUE_ID, 3:LOG_ID, 4:새로발본)

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P");                 	// log id 가 비어있는경우 새로 후판 제품 log id 새로 발본
//-------------------------------------------------------------------------------------------------------------------------
				
		try {

			szMsg = "[" + szMethodName + "] <<<< 1후판조업 저장위치변경정보 전송 START >>>>";
            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

			szYdStkColFr	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_STK_COL_FR"		);
			szYdStkBedFr  	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_STK_BED_FR"		);
			szYdStkColTo  	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_STK_COL_TO"		);
			szYdStkBedTo	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_STK_BED_TO"		);
//			szYdEqpWrkSh	= ydDaoUtils.paraRecChkNull(pL3Para, "YD_EQP_WRK_SH");
			szStlNoList		= ydDaoUtils.paraRecChkNull(pL3Para, "ARR_STL_NO"			);
			szPlBookOutCrane = ydDaoUtils.paraRecChkNull(pL3Para, "PL_BOOK_OUT_CRANE"	); //chito20230202
			szIS_PF_TO_PF    = ydDaoUtils.paraRecChkNull(pL3Para, "IS_PF_TO_PF"			);
			
			szMsg = "["+szMethodName+"] szIS_PF_TO_PF 공장간이송여부 >>>> :: " + szIS_PF_TO_PF ;
            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);


			// 재료번호 List로 전송시
			if ("".equals(szStlNoList)) {

				rsResult = JDTORecordFactory.getInstance().createRecordSet("tempYd");
				recPara  = JDTORecordFactory.getInstance().create();
				recPara.setField("YD_STK_COL_GP", 		 szYdStkColTo);
				recPara.setField("YD_STK_LYR_MTL_STAT1", "C");
				recPara.setField("YD_STK_LYR_MTL_STAT2", "U");
	        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recPara에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				recPara.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
				        	
				iRtnVal = ydStkLyrDao.getByLocMtlStat(recPara, rsResult);

				if (iRtnVal > 0) {

					for(int ii=0; ii<rsResult.size(); ii++) {
						if (!"".equals(szStlNoList)) {
							szStlNoList = szStlNoList + ";";
						}
						szStlNoList = szStlNoList + ydDaoUtils.paraRecChkNull(rsResult.getRecord(ii), "STL_NO");
					}
				}
			}
			arrStlNo = szStlNoList.split(";");

			// 20건단위로 전송
			for(int ii=0; ii<arrStlNo.length; ii++) {

				if ("".equals(szSendList)) {
					szSendList = arrStlNo[ii];
				} else {
					szSendList = szSendList + ";" + arrStlNo[ii];
				}

				iSendCnt ++;
				if (iSendCnt >= 20) {
					recL3Para = JDTORecordFactory.getInstance().create();
					recL3Para.setField("MSG_ID", 			"YDPRJ011"					);
					recL3Para.setField("YD_STK_COL_FR", 	szYdStkColFr				);		// From적치열
					recL3Para.setField("YD_STK_BED_FR", 	szYdStkBedFr				);		// From적치BED
					recL3Para.setField("YD_STK_COL_TO", 	szYdStkColTo				);		// TO적치열
					recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedTo				);		// TO적치BED
					recL3Para.setField("YD_EQP_WRK_SH", 	Integer.toString(iSendCnt)	);		// 야드설비작업매수
					recL3Para.setField("ARR_STL_NO", 		szSendList					);		// 전송대상 재료번호 List
					recL3Para.setField("PL_BOOK_OUT_CRANE", szPlBookOutCrane			);		// 북아웃 작업크레인(예:PBCRB1) chito20230202
					recL3Para.setField("IS_PF_TO_PF", szIS_PF_TO_PF						);		// 후판 공장간 이송여부 flag
		        	
//-------------------------------------------------------------------------------------------------------------------------
// 2024.12.06 recL3Para에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
					recL3Para.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
							              	
		            szRtnMsg 	= ydDelegate.sendMsg(recL3Para);

					szMsg = "[" + szMethodName + "] 1후판조업 저장위치변경정보 전송 결과>>>> 건수 :: " + iSendCnt + ", 결과 :: " + szRtnMsg;
		            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		            iSendCnt 	= 0;
		            szSendList 	= "";
				}
			}

			// 마지막 데이타 다시한번 전송
			if (!"".equals(szSendList)) {
				recL3Para = JDTORecordFactory.getInstance().create();
				recL3Para.setField("MSG_ID", 			"YDPRJ011"					);
				recL3Para.setField("YD_STK_COL_FR", 	szYdStkColFr				);		// From적치열
				recL3Para.setField("YD_STK_BED_FR", 	szYdStkBedFr				);		// From적치BED
				recL3Para.setField("YD_STK_COL_TO", 	szYdStkColTo				);		// TO적치열
				recL3Para.setField("YD_STK_BED_TO", 	szYdStkBedTo				);		// TO적치BED
				recL3Para.setField("YD_EQP_WRK_SH", 	Integer.toString(iSendCnt)	);		// 야드설비작업매수
				recL3Para.setField("ARR_STL_NO", 		szSendList					);		// 전송대상 재료번호 List
				recL3Para.setField("PL_BOOK_OUT_CRANE", szPlBookOutCrane			);		// 북아웃 작업크레인(예:PBCRB1)chito20230202
				recL3Para.setField("IS_PF_TO_PF", szIS_PF_TO_PF						);		// 후판 공장간 이송여부 flag
	        	
//-------------------------------------------------------------------------------------------------------------------------
//2024.12.06 recL3Para에 logId 추가 
//-------------------------------------------------------------------------------------------------------------------------
				recL3Para.setField("LOG_ID", logId);                                         
//-------------------------------------------------------------------------------------------------------------------------
						              	
	            szRtnMsg = ydDelegate.sendMsg(recL3Para);

				szMsg = "[" + szMethodName + "] 1후판조업 저장위치변경정보 전송 결과>>>> 건수 :: " + iSendCnt + ", 결과 :: " + szRtnMsg;
	            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);
			}

			szMsg = "[" + szMethodName + "] <<<< 1후판조업 저장위치변경정보 전송 END >>>>";
            ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		} catch (Exception e) {
			// Exception발생시
			szRtnMsg = "1후판 조업L3로 저장위치 변경정보 송신시 Exception 발생 >>>>" + e.getMessage();
			szMsg 	 = "[" + szMethodName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		return szRtnMsg;
	}

    /**
	 *      [A] 오퍼레이션명 :SMS L2 BOOK IN/OUT 실적
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException 
	 */
	public static String procJPlateSmsSend(String sOpType,String sPlateNo,String sLoc,String sEqpId) {
		
		// DAO 및 UTIL 객체 생성
		JPlateYdCommDAO	ydDao		= new JPlateYdCommDAO();
		
		// 레코드 선언
		JDTORecordSet rsOutRecSet = null;
		JDTORecord recIn          = null;
		JDTORecord recGetVal      = null;
		
		// 변수 선언
		String szMethodName       = "procJPlateSmsSend";
		String szMsg              = "";
		String szOperationName    = "SMS L2 BOOK IN/OUT 실적";
		int intRtnVal             = 0;
		
		com.inisteel.cim.common.jms.JmsQueueSender sender = null;
		String queueName = null; 
		JDTORecord inRecord = null;
		PropertyService propertyService=null;
		String   flag = "N";		
		try{
			
			szMsg= "[ " + queueName + " ]  sOpType:" + sOpType + "sPlateNo:" + sPlateNo + "sLoc:" + sLoc;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);

			// 레코드 생성
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");

			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("OPERATION_TYPE"		, sOpType);
			recIn.setField("PL_PLATE_NO"		, sPlateNo);
			recIn.setField("YD_STK_COL_GP"	    , ydUtils.substr(sLoc, 0, 6));
			recIn.setField("PL_TRCK_ZONE_ASGN"	, JPlateYdCommonUtils.getY2LocToRtZone(ydUtils.substr(sLoc, 0, 6)));
			recIn.setField("YD_EQP_ID"	, sEqpId);
			
			intRtnVal = ydDao.getJPlateL2TelegramInfo(recIn, rsOutRecSet);
			
			rsOutRecSet.first();
			recGetVal = rsOutRecSet.getRecord();
			
			String sMessage = recGetVal.getFieldString("TY3ABC");
//			String sRetVal = JPlatesndSms(sMessage,"YDP2L501");
		    // 프로퍼티 서비스 인스턴스를 취득합니다.
			propertyService = PropertyService.getInstance();

			// 큐 명칭을 프로퍼티로부터 취득합니다. [[ EAI = jms.queue.SMS_EAI_QUEUE ]]
			queueName = propertyService.getProperty("common.properties","jms.queue.SMSYD_EAI_QUEUE");
	 
			sender = new com.inisteel.cim.common.jms.JmsQueueSender();
			
			String msgID = "YDP2L501";
			//---------------------------------------------------------------------------------------------
			//WebMethod EAI 방식 변경에 따른 BRE에서 큐명 호출 2019.12.17 
			//---------------------------------------------------------------------------------------------
			{
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue 변경전 큐네임 : " +	queueName, JPlateYdConst.DEBUG);
			    
			    String szQueueName 	= StringHelper.evl(sender.getQueueName("YD", msgID), "");
			    
			    if(!"".equals(szQueueName)){
			        
			    	queueName = propertyService.getProperty("common.properties", "jms.queue."+szQueueName);
			    }
			    
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue JMS_TC_CD : " +	msgID, JPlateYdConst.DEBUG);
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue 변경후 큐네임        : " +	queueName, JPlateYdConst.DEBUG);
			}
			//---------------------------------------------------------------------------------------------
			
			// 큐에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName); 
			/*
			 * 큐에 넣을 데이터를 생성합니다.
			 * 1. LABEL2 에서 EAI 수신 정보를 생산 통데로 ByPass 한다.
			 */  
			inRecord = JDTORecordFactory.getInstance().create();
			//inRecord.setRecord(indo);		
			inRecord.setField("JMS_TC_CD", msgID);	
			inRecord.setField("JMS_TC_CREATE_DDTT", jspeed.base.util.DateHelper.format(
					new java.util.Date(System.currentTimeMillis()),
					"yyyyMMddHHmmss")); 		
			inRecord.setField("JMS_TC_MESSAGE", new String (sMessage) );
	 		
			// 큐에 데이터를 전송합니다.
			sender.send(inRecord);  

			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + "YDP2L501"  + ">   SEND FINISH ";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
			flag =  "Y";			
			szMsg = "BOOK IN/OUT실적처리(" + szMethodName + ") 완료["+sMessage+"]";

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){  
			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + "YDP2L501"  + ">   SEND Exception " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
			return "N" ;
		}
		
	    return flag;
	}// end of procSmsSend()
	   
//	/** sndSms  EAI (SMS) SEND 공통 
//	 * @param   1. Message 내용, 2. TC코드 
//	 * @return  String  정상일때 "Y"
//	 * @throws DAOException
//	 */	
//	public String JPlatesndSms(String  message, String tccode) throws com.inisteel.cim.common.exception.DAOException { 
//		
//		String szMsg="";
//		String szMethodName = "sndSms";
//		com.inisteel.cim.common.jms.JmsQueueSender sender = null;
//		String queueName = null; 
//		JDTORecord inRecord = null;
//		PropertyService propertyService=null;
//		String   flag = "N";
//		try {
//	    // 프로퍼티 서비스 인스턴스를 취득합니다.
//		propertyService = PropertyService.getInstance();
//
//		// 큐 명칭을 프로퍼티로부터 취득합니다. [[ EAI = jms.queue.SMS_EAI_QUEUE ]]
//		queueName = propertyService.getProperty("common.properties","jms.queue.SMSYD_EAI_QUEUE");
// 
//		sender = new com.inisteel.cim.common.jms.JmsQueueSender();
//		// 큐에 연결할 리소스를 생성합니다.
//		sender.initQueueService(queueName); 
//		/*
//		 * 큐에 넣을 데이터를 생성합니다.
//		 * 1. LABEL2 에서 EAI 수신 정보를 생산 통데로 ByPass 한다.
//		 */  
//		inRecord = JDTORecordFactory.getInstance().create();
//		//inRecord.setRecord(indo);		
//		inRecord.setField("JMS_TC_CD", new String (tccode) );	
//		inRecord.setField("JMS_TC_CREATE_DDTT", jspeed.base.util.DateHelper.format(
//				new java.util.Date(System.currentTimeMillis()),
//				"yyyyMMddHHmmss")); 		
//		inRecord.setField("JMS_TC_MESSAGE", new String (message) );
// 		
//		// 큐에 데이터를 전송합니다.
//		sender.send(inRecord);  
//
//		szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + tccode  + ">   SEND FINISH ";
//		ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
//		flag =  "Y";
//		} catch(Exception e){  
//			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + tccode  + ">   SEND Exception " + e.getMessage();
//			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
//              throw new com.inisteel.cim.common.exception.DAOException(getClass().getName() + e.getMessage(), e);
//		}finally{
//			   try{
//			       sender.closeAll(); 
//			   }catch(Exception e){
//			        ydUtils.putLog(SZ_CLASS_NAME, szMethodName, e.getMessage(), YdConstant.ERROR);
//			   }
//		}
//	    return flag;
//	} 
			
	/**********************************************************
	* 1후판정정야드자동화 신규메소드 추가
	**********************************************************/	
	
	public static String procJPlateSmsSendV2(String sOpType,String sPlateNo,String sLoc,String sEqpId) {
		return procJPlateSmsSendV2(sOpType,sPlateNo,sLoc,sEqpId,"");
	}
	
    /**
	 *      [A] 오퍼레이션명 :SMS L2 BOOK IN/OUT 실적 - 신규
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException 
	 */
	public static String procJPlateSmsSendV2(String sOpType,String sPlateNo,String sLoc,String sEqpId,String sCARD_NO) {
		
		// DAO 및 UTIL 객체 생성
		JPlateYdCommDAO	ydDao		= new JPlateYdCommDAO();
		
		// 레코드 선언
		JDTORecordSet rsOutRecSet = null;
		JDTORecord recIn          = null;
		JDTORecord recGetVal      = null;
		
		// 변수 선언
		String szMethodName       = "procJPlateSmsSendV2";
		String szMsg              = "";
		String szOperationName    = "SMS L2 BOOK IN/OUT 실적 - 신규";
		int intRtnVal             = 0;
		
		com.inisteel.cim.common.jms.JmsQueueSender sender = null;
		String queueName = null; 
		JDTORecord inRecord = null;
		PropertyService propertyService=null;
		String   flag = "N";		
		
		String      szPilngWrkGp	= "N";
		String[]	arrStlNo		= null;
		String 		szStlNo             = "";
		String      szStlNo2			= "";
		String      szStlNo3			= "";
		String		szCRANE_NO			= "00";
		
		try{
			
			szMsg= "[ " + queueName + " ]  sOpType:" + sOpType + " sPlateNo:" + sPlateNo + " sLoc:" + sLoc + " sEqpId:" + sEqpId;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(sLoc.startsWith("PCRT20") || sLoc.startsWith("PCRT30")) {
				//#1PRESS 는 L2 가 관리 안하는 영역으로 Book-In/Out 실적을 송신 안한다.
				flag =  "Y";			
				szMsg = "BOOK IN/OUT실적처리(" + szMethodName + ") 완료[#1PRESS(10000:PCRT20,20000:PRCR30) 는 L2 가 관리 안하는 영역으로 Book-In/Out 실적을 송신 안한다.]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
				return flag;
			}
			
			arrStlNo 		= sPlateNo.split(";");

			if(arrStlNo.length > 0) {
				szStlNo = arrStlNo[0];		//1단 재료번호
				szPilngWrkGp = "N";
				if(arrStlNo.length == 2) {
					szStlNo2 = arrStlNo[1];	//2단 재료번호
					szPilngWrkGp = "Y";
				} else if(arrStlNo.length == 3) {
					szStlNo2 = arrStlNo[1];	//2단 재료번호
					szStlNo3 = arrStlNo[2];	//3단 재료번호
					szPilngWrkGp = "Y";
				}
				
				if(sEqpId != null) {
					szCRANE_NO = JPlateYdCommonUtils.getY2CraneNoL2(sEqpId);
				}
				
			}
			
			// 레코드 생성
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");

			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("OPERATION_TYPE"		, sOpType);
			recIn.setField("PL_PLATE_NO"		, szStlNo);
			recIn.setField("YD_STK_COL_GP"	    , ydUtils.substr(sLoc, 0, 6));
			recIn.setField("PL_TRCK_ZONE_ASGN"	, JPlateYdCommonUtils.getY2LocToRtZone(ydUtils.substr(sLoc, 0, 6)));
			//recIn.setField("YD_EQP_ID"			, sEqpId);
			recIn.setField("CRANE_NO"			, szCRANE_NO);
			recIn.setField("PILNG_WRK_GP"		, szPilngWrkGp);
			recIn.setField("PL_MTL_NO2"			, szStlNo2);
			recIn.setField("PL_MTL_NO3"			, szStlNo3);
			recIn.setField("CARD_NO"			, sCARD_NO);
			
			if(sLoc.startsWith("PCRT10")) {
				//33010 존 일 경우 1후판압연 L2 
				intRtnVal = ydDao.getJPlateL2TelegramInfoV3(recIn, rsOutRecSet);
			} else {
				//그 외는 1후판전단정정 L2
				intRtnVal = ydDao.getJPlateL2TelegramInfoV2(recIn, rsOutRecSet);
			}
			
			rsOutRecSet.first();
			recGetVal = rsOutRecSet.getRecord();
			
			String sMessage = null;
			
			if(sLoc.startsWith("PCRT10")) {
				//33010 존 일 경우 1후판압연 L2 
				sMessage = recGetVal.getFieldString("TY3MBC");
			} else {
				//그 외는 1후판전단정정 L2
				sMessage = recGetVal.getFieldString("TY3ABC");
			}

		    // 프로퍼티 서비스 인스턴스를 취득합니다.
			propertyService = PropertyService.getInstance();

			// 큐 명칭을 프로퍼티로부터 취득합니다. [[ EAI = jms.queue.SMS_EAI_QUEUE ]]
			queueName = propertyService.getProperty("common.properties","jms.queue.SMSYD_EAI_QUEUE");
	 
			sender = new com.inisteel.cim.common.jms.JmsQueueSender();
			
			String msgID = "";
			/*
			 * 큐에 넣을 데이터를 생성합니다.
			 * 1. LABEL2 에서 EAI 수신 정보를 생산 통데로 ByPass 한다.
			 */  
			inRecord = JDTORecordFactory.getInstance().create();
					
			if(sLoc.startsWith("PCRT10")) {
				//33010 존 일 경우 1후판압연 L2 
				msgID = "YDP2L601";
			} else {
				//그 외는 1후판전단정정 L2
				msgID = "YDP2L501";
			}
			inRecord.setField("JMS_TC_CD", msgID);
			inRecord.setField("JMS_TC_CREATE_DDTT", jspeed.base.util.DateHelper.format(
					new java.util.Date(System.currentTimeMillis()),
					"yyyyMMddHHmmss")); 		
			inRecord.setField("JMS_TC_MESSAGE", new String (sMessage) );
			
			//---------------------------------------------------------------------------------------------
			//WebMethod EAI 방식 변경에 따른 BRE에서 큐명 호출 2019.12.17 
			//---------------------------------------------------------------------------------------------
			{
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue 변경전 큐네임 : " +	queueName, JPlateYdConst.DEBUG);
			    
			    String szQueueName 	= StringHelper.evl(sender.getQueueName("YD", msgID), "");
			    
			    if(!"".equals(szQueueName)){
			        
			    	queueName = propertyService.getProperty("common.properties", "jms.queue."+szQueueName);
			    }
			    
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue JMS_TC_CD : " +	msgID, JPlateYdConst.DEBUG);
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue 변경후 큐네임        : " +	queueName, JPlateYdConst.DEBUG);
			}
			//---------------------------------------------------------------------------------------------
			
			// 큐에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName); 
			
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDP2L501V2,makeYDP2L601 OUT ======================\n", JPlateYdConst.DEBUG);
			//ydUtils.displayRecord(szOperationName, outRec);
        	szMsg = "전송 데이터 확인 >>>> " + inRecord.toString();
            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);
			
			
			// 큐에 데이터를 전송합니다.
			sender.send(inRecord);  

			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + "YDP2L501,YDP2L601"  + ">   SEND FINISH ";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
			flag =  "Y";			
			szMsg = "BOOK IN/OUT실적처리(" + szMethodName + ") 완료["+sMessage+"]";

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){  
			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + "YDP2L501,YDP2L601"  + ">   SEND Exception " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
			return "N" ;
		}
		
	    return flag;
	}// end of procJPlateSmsSendV2()

    /**
	 *      [A] 오퍼레이션명 :SMS L2 BOOK IN/OUT 실적 - 신규--56020존 파일링 전용 코드( 오토파일러제거)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param msgRecord
	 * @return
	 * @throws JDTOException 
	 */
	public static String procJPlateSmsSendV4(String sOpType,String sPlateNo,String sLoc,String sEqpId,String sCARD_NO, String szYdSchCd) {
		
		// DAO 및 UTIL 객체 생성
		JPlateYdCommDAO	ydDao		= new JPlateYdCommDAO();
		
		// 레코드 선언
		JDTORecordSet rsOutRecSet = null;
		JDTORecord recIn          = null;
		JDTORecord recGetVal      = null;
		
		// 변수 선언
		String szMethodName       = "procJPlateSmsSendV2";
		String szMsg              = "";
		String szOperationName    = "SMS L2 BOOK IN/OUT 실적 - 신규";
		int intRtnVal             = 0;
		
		com.inisteel.cim.common.jms.JmsQueueSender sender = null;
		String queueName = null; 
		JDTORecord inRecord = null;
		PropertyService propertyService=null;
		String   flag = "N";		
		
		String      szPilngWrkGp	= "N";
		String[]	arrStlNo		= null;
		String 		szStlNo             = "";
		String      szStlNo2			= "";
		String      szStlNo3			= "";
		String		szCRANE_NO			= "00";
		
		try{
			
			szMsg= "[ " + queueName + " ]  sOpType:" + sOpType + " sPlateNo:" + sPlateNo + " sLoc:" + sLoc + " sEqpId:" + sEqpId;
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
			
			if(sLoc.startsWith("PCRT20") || sLoc.startsWith("PCRT30")) {
				//#1PRESS 는 L2 가 관리 안하는 영역으로 Book-In/Out 실적을 송신 안한다.
				flag =  "Y";			
				szMsg = "BOOK IN/OUT실적처리(" + szMethodName + ") 완료[#1PRESS(10000:PCRT20,20000:PRCR30) 는 L2 가 관리 안하는 영역으로 Book-In/Out 실적을 송신 안한다.]";
				ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
				return flag;
			}
			
			arrStlNo 		= sPlateNo.split(";");

			if(arrStlNo.length > 0) {
				szStlNo = arrStlNo[0];		//1단 재료번호
				szPilngWrkGp = "N";
				if(arrStlNo.length == 2) {
					szStlNo2 = arrStlNo[1];	//2단 재료번호
					szPilngWrkGp = "Y";
				} else if(arrStlNo.length == 3) {
					szStlNo2 = arrStlNo[1];	//2단 재료번호
					szStlNo3 = arrStlNo[2];	//3단 재료번호
					szPilngWrkGp = "Y";
				}
				
				if(sEqpId != null) {
					szCRANE_NO = JPlateYdCommonUtils.getY2CraneNoL2(sEqpId);
				}
				
			}
			
			// 레코드 생성
			rsOutRecSet = JDTORecordFactory.getInstance().createRecordSet("");

			recIn = JDTORecordFactory.getInstance().create();
			recIn.setField("OPERATION_TYPE"		, sOpType);
			recIn.setField("PL_PLATE_NO"		, szStlNo);
			recIn.setField("YD_STK_COL_GP"	    , ydUtils.substr(sLoc, 0, 6));
			recIn.setField("PL_TRCK_ZONE_ASGN"	, JPlateYdCommonUtils.getY2LocToRtZone(ydUtils.substr(sLoc, 0, 6)));
			//recIn.setField("YD_EQP_ID"			, sEqpId);
			recIn.setField("CRANE_NO"			, szCRANE_NO);
			recIn.setField("PILNG_WRK_GP"		, szPilngWrkGp);
			recIn.setField("PL_MTL_NO2"			, szStlNo2);
			recIn.setField("PL_MTL_NO3"			, szStlNo3);
			recIn.setField("CARD_NO"			, sCARD_NO);
			
			if(sLoc.startsWith("PCRT10")) {
				//33010 존 일 경우 1후판압연 L2 
				intRtnVal = ydDao.getJPlateL2TelegramInfoV3(recIn, rsOutRecSet);
			} else {
				//그 외는 1후판전단정정 L2
				//intRtnVal = ydDao.getJPlateL2TelegramInfoV2(recIn, rsOutRecSet);
				intRtnVal = ydDao.getJPlateL2TelegramInfoV4(recIn, rsOutRecSet);
			}
			
			rsOutRecSet.first();
			recGetVal = rsOutRecSet.getRecord();
			
			String sMessage = null;
			
			if(sLoc.startsWith("PCRT10")) {
				//33010 존 일 경우 1후판압연 L2 
				sMessage = recGetVal.getFieldString("TY3MBC");
			} else {
				//그 외는 1후판전단정정 L2
				sMessage = recGetVal.getFieldString("TY3ABC");
			}

		    // 프로퍼티 서비스 인스턴스를 취득합니다.
			propertyService = PropertyService.getInstance();

			// 큐 명칭을 프로퍼티로부터 취득합니다. [[ EAI = jms.queue.SMS_EAI_QUEUE ]]
			queueName = propertyService.getProperty("common.properties","jms.queue.SMSYD_EAI_QUEUE");
	 
			sender = new com.inisteel.cim.common.jms.JmsQueueSender();
			
			String msgID = "";
			/*
			 * 큐에 넣을 데이터를 생성합니다.
			 * 1. LABEL2 에서 EAI 수신 정보를 생산 통데로 ByPass 한다.
			 */  
			inRecord = JDTORecordFactory.getInstance().create();
					
			if(sLoc.startsWith("PCRT10")) {
				//33010 존 일 경우 1후판압연 L2 
				msgID = "YDP2L601";
			} else {
				//그 외는 1후판전단정정 L2
				msgID = "YDP2L501";
			}
			inRecord.setField("JMS_TC_CD", msgID);
			inRecord.setField("JMS_TC_CREATE_DDTT", jspeed.base.util.DateHelper.format(
					new java.util.Date(System.currentTimeMillis()),
					"yyyyMMddHHmmss")); 		
			inRecord.setField("JMS_TC_MESSAGE", new String (sMessage) );
			
			//---------------------------------------------------------------------------------------------
			//WebMethod EAI 방식 변경에 따른 BRE에서 큐명 호출 2019.12.17 
			//---------------------------------------------------------------------------------------------
			{
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue 변경전 큐네임 : " +	queueName, JPlateYdConst.DEBUG);
			    
			    String szQueueName 	= StringHelper.evl(sender.getQueueName("YD", msgID), "");
			    
			    if(!"".equals(szQueueName)){
			        
			    	queueName = propertyService.getProperty("common.properties", "jms.queue."+szQueueName);
			    }
			    
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue JMS_TC_CD : " +	msgID, JPlateYdConst.DEBUG);
			    ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "▒▒▒▒ sndQueue 변경후 큐네임        : " +	queueName, JPlateYdConst.DEBUG);
			}
			//---------------------------------------------------------------------------------------------
			
			// 큐에 연결할 리소스를 생성합니다.
			sender.initQueueService(queueName); 
			
			// Debug MSG
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n======================makeYDP2L501V2,makeYDP2L601 OUT ======================\n", JPlateYdConst.DEBUG);
			//ydUtils.displayRecord(szOperationName, outRec);
        	szMsg = "전송 데이터 확인 >>>> " + inRecord.toString();
            ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG);
			
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, "\n================================================================\n", JPlateYdConst.DEBUG);
			
			
			// 큐에 데이터를 전송합니다.
			sender.send(inRecord);  

			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + "YDP2L501,YDP2L601"  + ">   SEND FINISH ";
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
			flag =  "Y";			
			szMsg = "BOOK IN/OUT실적처리(" + szMethodName + ") 완료["+sMessage+"]";

			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.DEBUG);
			
		} catch(Exception e){  
			szMsg= "[ " + queueName + " ]  JMS_TC_CD:<" + "YDP2L501,YDP2L601"  + ">   SEND Exception " + e.getMessage();
			ydUtils.putLog(SZ_CLASS_NAME, szMethodName, szMsg, YdConstant.ERROR);
			return "N" ;
		}
		
	    return flag;
	}// end of procJPlateSmsSendV4()	
	
	
	/**
	 * 오퍼레이션명 : 2024.11.20 argument에 logId 추가 신규 작성
	 *                저장위치 수정/북아웃 시 현재 위치 체크
	 *  			  저장위치가 1후판 정정야드가 아닐경우 오류로 처리
	 * @param szYD_STK_COL_GP
	 * @return
	 */
	public static String checkUpdYdLocYdP(String pStlNo, String pYdGp, String pDelYn, String logId) {

		JPlateYdStkLyrDAO	ydStkLyrDao		= new JPlateYdStkLyrDAO();

		String	szRtnMsg			= "";
		String 	szMsg        		= "";
		String 	szMethodName		= "checkUpdYdLocYdP";
		String 	szOperationName		= "현재 저장위치 체크";

		int 	intRtnVal 			= 0;

		JDTORecordSet rsResult  	= JDTORecordFactory.getInstance().createRecordSet("tempYd");
		JDTORecord recPara			= null;
		JDTORecord recTemp 			= null;

		String	szYdStkColGp		= "";
		String	szYdStkLyrMtlStat	= "";
		String	szYdGp				= "";

		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("P"); // log id 가 비어있는경우 새로 후판 제품 log id 새로 발본

		szMsg = "[" + szOperationName + "] 메소드 시작";
		ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.DEBUG, logId);

		try {

			//------------------------------------------------------------------
			// 현재 저장위치가 2후판 정정야드가 아닐경우 오류로 처리
			//------------------------------------------------------------------
			rsResult = JDTORecordFactory.getInstance().createRecordSet("rsTemp");
			recPara  = JDTORecordFactory.getInstance().create();
			recPara.setField("STL_NO",		pStlNo);			// 재료번호
			recPara.setField("YD_GP",		"%");				// 전체야드 조회

//-------------------------------------------------------------------------------------------------------------------------
// 2024.11.20 recPara에 logId 추가 
			recPara.setField("LOG_ID", 			logId );      // logId
//-------------------------------------------------------------------------------------------------------------------------

			intRtnVal = ydStkLyrDao.getYdStklyrByStlNo(recPara, rsResult);
			if (intRtnVal > 0) {
				rsResult.first();
				for(int ii=0; ii<intRtnVal; ii++) {

					recTemp = JDTORecordFactory.getInstance().create();
					recTemp = rsResult.getRecord(ii);

					szYdStkColGp 	  	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_COL_GP");
					szYdStkLyrMtlStat 	= ydDaoUtils.paraRecChkNull(recTemp, "YD_STK_LYR_MTL_STAT");
					szYdGp 				= ydUtils.substr(szYdStkColGp, 0, 1);

					// 적치중이거나 권상예약 재료만 조회
					if ("C".equals(szYdStkLyrMtlStat) || "U".equals(szYdStkLyrMtlStat)) {
						if (!szYdGp.equals(pYdGp) && "N".equals(pDelYn)) {
							if ("T".equals(szYdGp)) {
								szRtnMsg = "해당 재료(" + pStlNo + ")는 2후판 제품창고 야드에 적치중입니다!";
							} else if ("F".equals(szYdGp)) {
								szRtnMsg = "해당 재료(" + pStlNo + ")는 2후판 정정 야드에 적치중입니다!";
							} else if ("K".equals(szYdGp)) {
								szRtnMsg = "해당 재료(" + pStlNo + ")는 1후판 제품창고 야드에 적치중입니다!";
							} else {
								szRtnMsg = "오류!....해당 재료(" + pStlNo + ")가 다른 야드[" + szYdGp + "]에 적치중 입니다!";
							}
							szMsg 	 = "[" + szMethodName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
						}
					}

					if (szYdGp.equals(pYdGp)) {
						if ("U".equals(szYdStkLyrMtlStat)) {
							szRtnMsg = "오류!....해당 재료(" + pStlNo + ")는 권상예약 중! [" + szYdStkColGp + "]";
							szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
						} else if ("D".equals(szYdStkLyrMtlStat)) {
							szRtnMsg = "오류!....해당 재료(" + pStlNo + ")는 권하예약 중! [" + szYdStkColGp + "]";
							szMsg 	 = "["+szMethodName + "] " + szRtnMsg;
							ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
							return szRtnMsg;
						}
					}
				}
			}
		} catch (Exception e) {
			// Exception발생시
			szRtnMsg = "현재 저장위치 체크시 Exception 발생 >>>>" + e.getMessage();
			szMsg 	 = "[" + szMethodName + "] " + szRtnMsg;
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			return szRtnMsg;
		}

		return JPlateYdConst.RETN_CD_SUCCESS;
	} // end of checkUpdYdLocYdP()
	
	
    /**
     *      [A] 오퍼레이션명 : 2024.11.21 1후판 정정야드 RT Zone -> 저장위치 리턴 메소드
     *
     * @param  String
     * @return String
     * @throws DAOException
     * @throws JDTOException
     */
    public static String selY2RtZoneToLoc(String item) throws DAOException, JDTOException {

        JDTORecord recPara      = JDTORecordFactory.getInstance().create();
        JDTORecordSet rsTemp    = null;
        String retunValue       = "N";

        try {

            //query id setting
            recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.session.JPlateYdL2RcvSeEJB.selY2RtZoneToLoc");

            //parameter setting
            recPara.setField("V_ITEM", item);

            //query execute
            rsTemp = dbAssDao.getRecordSet(recPara);

            if(rsTemp.size() <= 0) {
                retunValue = "N";
            } else {
                retunValue = StringHelper.evl(rsTemp.getRecord(0).getFieldString("Y2_RT_ZONE_TO_LOC"),"N");
            }
        } catch (Exception e) {
            retunValue = "N";
        }

        return retunValue;
    }

    /**
     *      [A] 오퍼레이션명 : 2024.11.21 저장위치 -> 1후판 정정야드 RT Zone 리턴 메소드
     *
     * @param  String
     * @return String
     * @throws DAOException
     * @throws JDTOException
     */
    public static String selLocToY2RtZone(String item) throws DAOException, JDTOException {

        JDTORecord recPara      = JDTORecordFactory.getInstance().create();
        JDTORecordSet rsTemp    = null;
        String retunValue       = "N";

        try {

            //query id setting
            recPara.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.jplateyd.session.JPlateYdL2RcvSeEJB.selLocToY2RtZone");

            //parameter setting
            recPara.setField("V_ITEM", item);

            //query execute
            rsTemp = dbAssDao.getRecordSet(recPara);

            if(rsTemp.size() <= 0) {
                retunValue = "N";
            } else {
                retunValue = StringHelper.evl(rsTemp.getRecord(0).getFieldString("LOC_TO_Y2_RT_ZONE"),"N");
            }
        } catch (Exception e) {
            retunValue = "N";
        }

        return retunValue;
    }
	

	/**
	 * 오퍼레이션명 : 스케쥴기준을 조회하여 크레인 정보를 반환하는 메소드
	 * @param szYD_SCH_CD
	 * @param recResult
	 * @return int
	 *			 1 : 메소드 호출 성공
	 * 			-1 : 스케쥴금지
	 * 			-2 : 작업크레인고장이고 대체크레인정보가 없는 경우
	 * 			-3 : 작업크레인고장이고 대체크레인 고장인 경우 작업 불가
	 * 			-4 : 스케쥴기준 조회에러
	 * 			-5 : 크레인설비 정보 조회시 에러 발생
	 * @throws JDTOException
	 */
	public static String getCrnInfoByCrnSchRule(String szYD_SCH_CD, JDTORecord recResult, String logId) throws JDTOException {

		String 	szMsg 			= "";
		String 	szMethodName 	= "getCrnInfoByCrnSchRule";
		String 	szCrn 			= "";
		String 	szYD_SCH_PRIOR 	= "9";

//---------------------------------------------------------------------------------------------
// 2024.12.19 argument에 logId 없으면 새로 발본
//---------------------------------------------------------------------------------------------
		if(ydLogUtils.isEmpty(logId)) logId = ydLogUtils.getLogIdNew("F"); // log id 가 비어있는경우 새로 2후판 정정 log id 새로 발본

		// 리턴 recordSet 생성
		JDTORecordSet rsResult 	= JDTORecordFactory.getInstance().createRecordSet("");

		// 스케줄 기준 체크
		boolean blnRtnVal = chkGetSchRule(szYD_SCH_CD, rsResult);
		if (!blnRtnVal) {
			szMsg = "스케쥴기준 조회에러";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			return szMsg;
		}

		// 레코드 추출
		rsResult.first();
		JDTORecord recPara = rsResult.getRecord();

		// 스케줄CD 체크
		// 스케줄 금지 유무
		String szYD_SCH_PROH_EXN = ydDaoUtils.paraRecChkNull(recPara,"YD_SCH_PROH_EXN");

		// 스케줄 금지 유무가 "Y"이면 처리를 중지하고 유스케이스를 종료한다.
		if ("Y".equals(szYD_SCH_PROH_EXN)) {
			szMsg = "스케줄 금지 유무가 '" + szYD_SCH_PROH_EXN + "' 입니다";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			return szMsg;
		}

		// 작업크레인
		String szYD_WRK_CRN 		= ydDaoUtils.paraRecChkNull(recPara, "YD_WRK_CRN"			);
		// 작업크레인우선순위
		String szYD_WRK_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_CRN_PRIOR"		);
		// 대체크레인유무
		String szYD_ALT_CRN_YN 		= ydDaoUtils.paraRecChkNull(recPara,"YD_ALT_CRN_YN"			);
		// 대체크레인
		String szYD_ALT_CRN 		= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN"			);
		// 대체크레인우선순위
		String szYD_ALT_CRN_PRIOR 	= ydDaoUtils.paraRecChkNull(recPara, "YD_ALT_CRN_PRIOR"		);

		// 작업크레인 설비 상태 체크
		blnRtnVal = eqpStatCheck(szYD_WRK_CRN);

		// 작업크레인이 사용불가이면 대체크레인의 상태를 체크한다.
		if (!blnRtnVal) {

			szMsg = "작업크레인(" + szYD_WRK_CRN + ")이 사용 불가 상태입니다.";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);

			// 대체크레인의 유무를 체크한다.
			// 대체크레인이 없으면 에러 리턴
			if (!"Y".equals(szYD_ALT_CRN_YN)) {

				szMsg = "대체크레인유무(" + szYD_ALT_CRN_YN + "), 대체크레인이 없습니다.";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szMsg;

			}
			// 대체크레인이 있으면 대체크레인 설비 상태 체크
			blnRtnVal = eqpStatCheck(szYD_ALT_CRN);
			// 대체크레인마저 사용불가이면 사용할 크레인이 없으므로 유스케이스를 종료한다.
			if (!blnRtnVal) {

				szMsg = "대체크레인(" + szYD_ALT_CRN + ")이 사용 불가 상태입니다.";
				ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
				
				return szMsg;

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
		rsResult  = JDTORecordFactory.getInstance().createRecordSet("");
		blnRtnVal = chkGetCrnSpec(szCrn, rsResult);
		if (!blnRtnVal) {
			szMsg = "크레인설비 정보 조회시 에러 발생";
			ydLogUtils.putLogNew(SZ_CLASS_NAME, szMethodName, szMsg, JPlateYdConst.ERROR, logId);
			
			return szMsg;
		}
		rsResult.first();
		recPara = rsResult.getRecord();
		//작업가능한 크레인
		recResult.setField("YD_WRK_CRN", 		szCrn													);
		//스케쥴우선순위
		recResult.setField("YD_SCH_PRIOR", 		szYD_SCH_PRIOR											);
		//크레인 작업허용중량
		recResult.setField("YD_WRK_ABLE_WT", 	ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_WT")		);
		//크레인 집게허용 오차
		recResult.setField("YD_CRN_TONG_W_TOL", ydDaoUtils.paraRecChkNull(recPara,"YD_CRN_TONG_W_TOL")	);
		//크레인 작업가능 매수
		recResult.setField("YD_WRK_ABLE_SH", 	ydDaoUtils.paraRecChkNull(recPara,"YD_WRK_ABLE_SH")		);

		return JPlateYdConst.RETN_CD_SUCCESS;
	}	// end of getCrnInfoByCrnSchRule

	
}