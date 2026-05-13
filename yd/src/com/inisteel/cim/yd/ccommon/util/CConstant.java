/**
 * @(#)CConstant.java
 * 
 * @version			1.0
 * @author 			현대제철
 * @date			2019.05.02
 * 
 * @description		2열연 야드에서 사용되는 공통 상수를 정의하는 클래스입니다.
 * ------------------------------------------------------------------------------
 * Ver.   수정일자     요청자  수정자      내용
 * =====  ===========  ======  ======  ==========================================
 * V1.00  2019/05/02   정종균  이현진      최초 등록
 */
package com.inisteel.cim.yd.ccommon.util;

import java.util.Hashtable;

public class CConstant {
	 
	
	//----------------------------------------------------
	public static final String YD_GP			= "J";		//2열연야드
	
	//전문버퍼의 전문항목이름  
	public static final String TC_BODY									= "ZZ_TC_BODY";
	
//	//야드 모니터링 채널
//	public static final String YD_MONITORING_CHANNEL_01					= "yd_monitor01";
//	
//	public static final String YD_MONITORING_CHANNEL_A					= "yd_monitorA";
//	public static final String YD_MONITORING_CHANNEL_D					= "yd_monitorD";
//	public static final String YD_MONITORING_CHANNEL_K					= "yd_monitorK";
//	public static final String YD_MONITORING_CHANNEL_T					= "yd_monitorT";
//	public static final String YD_MONITORING_CHANNEL_H					= "yd_monitorH";
//	public static final String YD_MONITORING_CHANNEL_J					= "yd_monitorJ";
//	public static final String YD_MONITORING_CHANNEL_S					= "yd_monitorS";
//	
//	
//	
//	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	* 야드업무 내부적으로 사용되는 함수리턴코드 정의
//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	//공통 문자리턴값
	public static final String RETN_CD_SUCCESS							= "SUCCESS";		//성공메세지코드
	public static final String RETN_CD_FAILURE							= "FAILURE";		//실패메세지코드
	public static final String RETN_CD_NOTEXIST							= "NOTEXIST";		//값이 존재하지 않음
	public static final String RETN_CD_EXIST							= "EXIST";			//값이 존재함
	public static final String RETN_CD_DUPLICATE						= "DUPLICATE";		//값이 중복됨
	public static final String RETN_CD_TC_ERROR							= "TC_ERROR";		//전문에러
	public static final String RETN_CD_NO_PARAM							= "NOPARAM";		//파라미터가 존재하지 않음
	public static final String RETN_CD_EQ_STATUS						= "EQUAL_STATUS";	//상태값이 같은 경우
	public static final String RETN_CD_NOTEQ_STATUS						= "NOTEQUAL_STATUS";//상태값이 다른 경우
//	
	//크레인관련리턴값
	public static final String RETN_CRN_SCH_PROH						= "SCH_PROH";		//야드스케쥴금지
	public static final String RETN_CRN_NO_WRK							= "NO_WORK";		//작업예약이 존재하지 않음
	public static final String RETN_CRN_EXIST_WRK						= "EXIST_WORK";		//작업예약이 존재
	public static final String RETN_CRN_NO_SCH							= "NO_SCH";			//크레인스케줄이 존재하지 않음
	public static final String RETN_CRN_EXIST_SCH						= "EXIST_SCH";		//크레인스케줄이 존재
	public static final String RETN_CRN_STATUS_ERR						= "STATUS_ERR";		//크레인의 작업상태가 올바르지 않음
	public static final String RETN_CRN_NO_ALT_CRN						= "NO_ALT_CRN";		//대체크레인이 존재하지 않음
//	
//	//운송설비관련리턴값
//	public static final String RETN_TRN_COL_ACT							= "COL_ACT";		//정지위치 활성 상태
//	public static final String RETN_TRN_COL_INACT						= "COL_INACT";		//정지위치 비활성 상태
//	
	//정수리턴값
	public static final int RETN_INT_SUCCESS				= 1;		//성공메세지코드
	public static final int RETN_INT_FAILURE				= -10000;	//실패메세지코드
	public static final int RETN_INT_TC_ERROR				= -10001;	//전문에러	
//	
//	//TO위치결정 시 사용되는 반환값 정의
//	public static final String RETN_SH_OVER								= "SH_OVER";		//매수초과
//	public static final String RETN_WT_OVER								= "WT_OVER";		//중량초과
//	public static final String RETN_H_OVER								= "H_OVER";			//높이초과
//	public static final String RETN_BED_INACT							= "BED_INACT";		//적치베드가 활성상태가 아님
//	public static final String RETN_BED_WHIO_NOT_IN						= "WHIO_NOT_IN";	//입고불가능상태
//	public static final String RETN_BED_UN_WAIT							= "UN_WAIT";		//권상대기
//	public static final String RETN_SAME_SCH_CD							= "SAME_SCH_CD";	//스케줄코드가 같음
//	public static final String RETN_SCH_EARLY_PRIOR						= "EARLY_PRIOR";	//우선순위가 빠름
//	public static final String RETN_SCH_LATE_PRIOR						= "LATE_PRIOR";		//우선순위가 늦음
//	public static final String RETN_NOT_EXIST_SCH_CD					= "NOTEXIST_SCH_CD";//스케줄코드가 존재하지 않음
//	public static final String RETN_NOT_EXIST_BED						= "NOTEXIST_BED";	//적치가능베드가 존재하지 않음
//	public static final String RETN_BIG_NOT_EXIST_BED					= "BIG_NOTEXIST_BED";	//대형고객사 적치가능베드가 존재하지 않음
//	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	
//	//C연주슬라브야드의 B열연이송대기 기본 이송 적치열 - D동 팔레트 저장위치
//	public static final String A_YD_BASE_FTMV_COL 						= "ADPT01";
//	
//	//야드구분
//	public static final String YD_GP_C_SLAB_YARD						= "A";				//C연주슬라브야드
//	public static final String YD_GP_A_PLATE_SLAB_YARD					= "D";				//A후판슬라브야드
//	public static final String YD_GP_C_HR_COIL_MATL_YARD				= "H";				//C열연코일소재야드
//	public static final String YD_GP_C_HR_COIL_GDS_YARD					= "J";				//C열연코일제품야드
	public static final String YD_GP_PLATE_GDS_YARD						= "K";				//후판제품창고야드
//	public static final String YD_GP_PLATE2_JJ_YARD						= "F";				//2후판정정야드
	public static final String YD_GP_PLATE2_GDS_YARD					= "T";				//2후판제품창고야드 - 2012.12.17 추가 (3기)
//	public static final String YD_GP_INTGR_PLATE_GDS_YARD				= "T";				//1,2 후판 통합 제품창고야드 (Data 이행 후 소스 상에서 'K'-->'T'로 일괄 변경하기 위한 상수로 데이터 이전에 K , 데이터 이후에 T 로 변경하여 컴파일 할 것) 
//	public static final String YD_GP_INTGR_YARD							= "S";				//통합야드A(부두)
//	public static final String YD_GP_A_HR_SLAB_YARD						= "0";				//A열연슬라브야드
//	public static final String YD_GP_A_HR_COIL_YARD						= "1";				//A열연COIL야드
//	public static final String YD_GP_B_HR_SLAB_YARD						= "2";				//B열연슬라브야드
//	public static final String YD_GP_B_HR_COIL_YARD						= "3";				//B열연COIL야드
//	public static final String YD_GP_A_PLATE_PLANT						= "@";				//A후판조업 - 가상야드구분 사용
//	public static final String YD_GP_C_HR_PLANT							= "%";				//C열연조업 - 가상야드구분 사용
//	public static final String YD_GP_PORT_SLAB_YARD						= "M";				//항만야드 추가 : 2015.12.23 Leejy
//	
//	
//   
	//야드설비상태
	public static final String YD_EQP_NOTEXIST							= "EQP_NOTEXIST";	//설비가 존재하지 않음
	public static final String YD_EQP_STAT_NORM							= "N";				//정상
	
	//야드설비작업Mode(TB_YD_EQP.YD_EQP_WRK_MODE)
	public static final String YD_EQP_WRK_MODE_ON_LINE					= "1";				//ON LINE
	public static final String YD_EQP_WRK_MODE_OFF_LINE					= "2";				//OFF LINE
	
	//야드작업Mode(TB_YD_EQP.YD_EQP_WRK_MODE2)
	public static final String YD_EQP_WRK_MODE2_A						= "A";				// 무인
	public static final String YD_EQP_WRK_MODE2_R						= "R";				// 리모컨
	public static final String YD_EQP_WRK_MODE2_E						= "E";				// 정비
	public static final String YD_EQP_WRK_MODE2_M						= "M";				// 유인
//	
	
	//크레인의 설비작업상태(TB_YD_EQP.YD_EQP_STAT)
	public static final String YD_EQP_STAT_IDLE 						= "W";				//크레인이 IDLE인 상태 - 스케줄수행대기
	public static final String YD_EQP_STAT_OW 							= "0";				//명령선택대기
	public static final String YD_EQP_STAT_UP_WO 						= "1";				//권상지시
	public static final String YD_EQP_STAT_UP_CMPL 						= "2";				//권상완료
	public static final String YD_EQP_STAT_DN_WO 						= "3";				//권하지시
	public static final String YD_EQP_STAT_DN_CMPL 						= "4";				//권하완료
	public static final String YD_EQP_STAT_DN_CHANGE					= "5";				//권하위치변경
	public static final String YD_EQP_STAT_BREAK						= "B";				//고장
	
//	//야드적치Bed용도구분
//	public static final String YD_STK_BED_USG_GP_RCPT					= "S";				//수입구 - CARRYOUT
//	public static final String YD_STK_BED_USG_GP_ISSUE					= "B";				//불출구 - CARRYIN
//	public static final String YD_STK_BED_USG_GP_YARD					= "Y";				//야드베드
//	
//	/*+++++++++++++++++++++
//	 * 차량관련 상수 정의 시작
//	 +++++++++++++++++++++*/
//	//포인트개폐구분
//	public static final String PNT_UNIT_CL_GP_CLOSE						= "C";				//폐
//	public static final String PNT_UNIT_CL_GP_OPEN						= "O";				//개
//	
	//차량에 대한 설비 기본값
	public static final String YD_TS_CAR_EQP_ID							= "XXPT01";			//구내운송차량에 대한 기본 설비ID
	public static final String YD_DM_CAR_EQP_ID							= "XXPT02";			//출하차량에 대한 기본 설비ID
//	
//	
	//차량사용구분  출하,구내 운송  ( L : 구내운송 , G : 출하차량)
	public static final String YD_CAR_USE_GP_TS						    = "L";				// 구내운송
	public static final String YD_CAR_USE_GP_DM						    = "G";				// 출하차량
// 
//	
//	//야드차량진행상태
//	public static final String YD_CARLD_LEV								= "1";				//상차출발
//	public static final String YD_CARLD_ARR								= "2";				//상차도착
//	public static final String YD_CARLD_CHK								= "3";				//상차검수
//	public static final String YD_CARLD_ST								= "4";				//상차개시
//	public static final String YD_CARLD_CMPL							= "5";				//상차완료
//	public static final String YD_CARUD_LEV								= "A";				//하차출발
//	public static final String YD_CARUD_ARR								= "B";				//하차도착
//	public static final String YD_CARUD_CHK								= "C";				//하차검수
//	public static final String YD_CARUD_ST								= "D";				//하차개시
//	public static final String YD_CARUD_CMPL							= "E";				//하차완료
//
//	
	//야드차량생성시 사용되는 입동지시순번 기본값
	public static final String YD_BAYIN_WO_SEQ_DEFAULT					= "9";				//입동지시순번 기본값
//	
//	//야드배차순서 기본값
//	public static final String YD_CARASGN_SEQ_AUTO_DEFAULT				= "9";				//자동이송LOT편성일 경우 기본값
//	public static final String YD_CARASGN_SEQ_MAN_DEFAULT				= "99";				//수동이송LOT편성일 경우 기본값
//	
//	//운송작업영공구분
//	public static final String TRN_WRK_VOID								= "E";				//공차
//	public static final String TRN_WRK_FULL								= "F";				//영차
//	
//	//YD_EQP_WRK_STAT - 야드설비작업상태
//	public static final String YD_EQP_WRK_STAT_LD						= "L";				//상차
//	public static final String YD_EQP_WRK_STAT_UD						= "U";				//하차
//	
//	//차량포인트지시 시 포인트가 없을 경우 사용되는 포인트코드
//	public static final String YD_PNT_CD_NULL							= "0000";
//	
//	//대기장 포인트코드 
	public static final String YD_WAIT_PNT_CD							= "1Z99";			//대기장포인트코드
//	
	public static final String YD_REPAIR_WLOC_CD						= "DMY1P";			//중장비수리고
//	
//	public static final String WLOC_CD_C_SLAB_YARD						= "DHY21";			//C연주슬라브야드(연주-옥내 Yard)
//	public static final String WLOC_CD_C_SLAB_YARD2						= "DVY19";			//C연주슬라브야드(2연주-옥내 Yard)
//	public static final String WLOC_CD_A_PLATE_SLAB_YARD				= "DKY21";			//1후판슬라브야드(1후판-옥내 Yard)
//	public static final String WLOC_CD_2_PLATE_SLAB_YARD				= "DWY22";			//2후판슬라브야드(2후판-옥내 Yard)
//	public static final String WLOC_CD_C_HR_COIL_MATL_YARD				= "DJY21";			//C열연 소재야드(D,E)
	public static final String WLOC_CD_C_HR_COIL_MATL_YARD2				= "DJY22";			//C열연 소재야드(G,H)
	public static final String WLOC_CD_C_HR_COIL_MATL_YARD3				= "DJY1E";			//C열연 제품야드(D,E,F,G,H)
//	
//	public static final String WLOC_CD_A_PLATE_PLANT 					= "DKY23";			//후판SIZING 개소코드
//	public static final String WLOC_CD_B_PLATE_PLANT 					= "DWY23";			//2후판SIZING 개소코드
//	public static final String WLOC_CD_A_PLATE_PLANT_PNT_CD				= "1A01";			//후판SIZING Point코드
//	public static final String WLOC_CD_C_HR_PLANT 						= "DJY24";			//열연재열재 개소코드 
//	public static final String WLOC_CD_C_HR_PLANT_PNT_CD 				= "1A01";			//열연재열재 Point코드
//	public static final String WLOC_CD_B_HR_PLANT 						= "D3Y43";			//B열연 개소코드	
//	public static final String WLOC_CD_A_HR_PLANT 						= "D2Y43";			//A열연 개소코드	
//	
//	public static final String WLOC_CD_PLATE_GDS_YARD 					= "DWY26";			//1후판 제품창고 개소코드 - 2012.12.18 추가 (3기) , 통합후 DKY30 -> DWY26
//	
//	public static final String WLOC_CD_PLATE2_GDS_YARD 					= "DWY26";			//2후판 제품창고 개소코드 - 2012.12.18 추가 (3기) *구내운송에서 개소코드 2013.03.12 일 DWY26으로 확정*
//	
//	public static final String WLOC_CD_PORT_SLAB_YARD					= "C3S01";			//항만슬라브야드(옥내 Yard) 추가 - 2015.12.24 LeeJY
//	
//	/*+++++++++++++++ 차량관련 상수 정의 끝 +++++++++++++++*/
//	
	//야드적치열활성상태
	public static final String YD_STK_COL_ACTIVE						= "L";				//적치가능
	public static final String YD_STK_COL_INACTIVE						= "C";				//비활성화
	public static final String YD_STK_COL_NOUSE							= "N";				//사용불가
	
	//야드적치Bed활성상태
	public static final String YD_STK_BED_ACTIVE						= "L";				//적치가능
	public static final String YD_STK_BED_INACTIVE						= "C";				//비활성화
	public static final String YD_STK_BED_NOUSE							= "N";				//사용불가
	
	//적치단활성상태
	public static final String YD_STK_LYR_ACTIVE						= "E";				//적치가능
	public static final String YD_STK_LYR_INACTIVE						= "C";				//비활성화
	public static final String YD_STK_LYR_NOUSE							= "N";				//사용불가
	public static final String YD_STK_LYR_FULL							= "F";				//적치완료
	
	//적치단 재료 상태 	
	public static final String YD_STK_LYR_MTL_STAT_STK                  = "C";              //적치중
	public static final String YD_STK_LYR_MTL_STAT_DN_WAIT              = "D";              //권하대기
	public static final String YD_STK_LYR_MTL_STAT_STK_ABLE             = "E";              //적치가능
	public static final String YD_STK_LYR_MTL_STAT_UN_WAIT              = "U";              //권상대기
	public static final String YD_STK_LYR_MTL_STAT_STK_UNABLE           = "X";              //적치불가
//	
//	//야드적치BED입출고상태
//	public static final String YD_STK_BED_WHIO_ENABLE					= "E";				//입출고가능
//	public static final String YD_STK_BED_WHIO_FULL						= "F";				//완산BED
//	public static final String YD_STK_BED_WHIO_X						= "X";				//입출고금지
//	public static final String YD_STK_BED_WHIO_VIRTUAL					= "G";				//가적BED
//
//	
//	
//	//베드의 기본 야드적치Bed중량Max --> 구내운송이나 출하차량이 출발 시 차량정지위치의 BED를 비활성화 시 같이 설정하는 베드중량 MAX
	public static final String YD_STK_BED_WT_MAX_DEFAULT				= "300000";
//	
//	
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 크레인작업실적응답 
	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//U:권상실적, D:권하실적, E: 비상조업실적, F: 강제권하,   R:고장, M:모드변경, J : 지시요구
	public static final String CRN_WRK_RE_LD_WR							= "U";				//권상실적
	public static final String CRN_WRK_RE_DN_WR							= "D";				//권하실적
	public static final String CRN_WRK_RE_EMG_PTOP						= "E";				//비상조업실적
	public static final String CRN_WRK_RE_FRCE_DN						= "F";				//강제권하
	public static final String CRN_WRK_RE_BREAK							= "B";				//고장
	public static final String CRN_WRK_RE_TRBL							= "R";				//고장(해제)
	public static final String CRN_WRK_RE_MD_MOD						= "M";				//모드변경
	public static final String CRN_WRK_RE_WO_DMD						= "J";				//지시요구
//	
	// 크레인작업실적응답 코드
	public static final String CRN_WRK_RE_CD_NORMAL_HD					= "0000";			//정상처리
	public static final String CRN_WRK_RE_CD_NO_WRK						= "9999";			//크레인작업지시가 없을 경우
	public static final String CRN_WRK_RE_CD_NO_WRK2					= "8888";			//강제권상요구 대상이 부적합 경우
//	//.....에러코드를 계속 정의 필요.....
//	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
//	
//	//야드재료품목 
//	//public static final String YD_MTL_ITEM_
//	
//	//재료진도코드
//	public static final String PROG_CD_SLAB_CORRCETION_WRK_WAIT			= "A";				//SLAB정정작업대기
//	public static final String PROG_CD_WO_WAIT							= "B";				//지시대기
//	public static final String PROG_CD_WRK_WAIT							= "C";				//작업대기
//	public static final String PROG_CD_FTMV_WO_WAIT						= "D";				//이송지시대기
//	public static final String PROG_CD_FTMV_WRK_WAIT					= "E";				//이송작업대기
//	public static final String PROG_CD_STMP_HOLD						= "F";				//판정보류
//	public static final String PROG_CD_OVALL_STMP_WAIT					= "G";				//종합판정대기
//	public static final String PROG_CD_RCPT_WAIT						= "H";				//입고대기
//	public static final String PROG_CD_RETN_WAIT						= "J";				//반납대기
//	public static final String PROG_CD_DIST_WO_WAIT						= "K";				//출하지시대기
//	public static final String PROG_CD_TRN_WAIT							= "L";				//운송대기
//	public static final String PROG_CD_DIST_CMPL						= "M";				//출하완료
//	public static final String PROG_CD_TRN_WO_WAIT						= "N";				//운송지시대기
//	public static final String PROG_CD_DELIVERY_CMPL					= "P";				//인도완료
//	public static final String PROG_CD_FTMV_TKOV_WAIT					= "Q";				//이송인수대기
//	public static final String PROG_CD_AUCT_TG_PKUP						= "X";				//경매대상선정
//	public static final String PROG_CD_INLINE_MATCH_WAIT				= "Y";				//재공충당대기
//	public static final String PROG_CD_GDS_MATCH_WAIT					= "Z";				//제품충당대기
//	public static final String PROG_CD_SLAB_BUY_REG						= "0";				//SLAB구입등록(품질)
//	public static final String PROG_CD_SLAB_BUY_CMMT					= "1";				//SLAB구입확정(품질)
//	
//	//야드목표행선구분
//	public static final String AR_SLAB_BUY_REG							= "01";				//슬라브구입등록
//	public static final String AR_SLAB_BUY_CMMT							= "11";				//슬라브구입확정
//	public static final String AR_CORRCETION_WRK_WAIT_B_CCR_SF 			= "A1";				//정정작업대기(B열연CCR스카핑)
//	public static final String AR_CORRCETION_WRK_WAIT_C_CCR_SF 			= "A2";				//정정작업대기(C열연CCR스카핑)
//	public static final String AR_CORRCETION_WRK_WAIT_A_BP_SF 			= "A3";				//정정작업대기(A후판주편스카핑)
//	public static final String AR_CORRCETION_WRK_WAIT_A_BP 				= "A4";				//정정작업대기(A후판주편정정)
//	public static final String AR_WO_WAIT_B_HCR 						= "B1";				//지시대기(B열연HCR)
//	public static final String AR_WO_WAIT_B_CCR 						= "B2";				//지시대기(B열연CCR)
//	public static final String AR_WO_WAIT_C_HCR 						= "B3";				//지시대기(C열연HCR)
//	public static final String AR_WO_WAIT_C_CCR 						= "B4";				//지시대기(C열연CCR)
//	public static final String AR_WO_WAIT_A_HCR 						= "B5";				//지시대기(A후판HCR)
//	public static final String AR_WO_WAIT_A_CCR 						= "B6";				//지시대기(A후판CCR)
//	public static final String AR_WO_WAIT_2_HCR 						= "B7";				//지시대기(2후판HCR) --추가
//	public static final String AR_WO_WAIT_2_CCR 						= "B8";				//지시대기(2후판CCR) --추가
//	public static final String AR_WO_WAIT_A_AIR_COOLING					= "BA";				//지시대기(A열연공냉재)
//	public static final String AR_WO_WAIT_B_AIR_COOLING					= "BB";				//지시대기(B열연공냉재)
//	public static final String AR_WO_WAIT_C_AIR_COOLING					= "BC";				//지시대기(C열연공냉재)
//	public static final String AR_WRK_WAIT_B_MILL 						= "C1";				//작업대기(B열연압연)
//	public static final String AR_WRK_WAIT_C_MILL 						= "C2";				//작업대기(C열연압연)
//	public static final String AR_WRK_WAIT_A_MILL 						= "C3";				//작업대기(A후판압연)
//	public static final String AR_WRK_WAIT_2_MILL 						= "C3";				//작업대기(2후판압연) --추가
//	public static final String AR_WRK_WAIT_B_HFL 						= "CA";				//작업대기(B열연HFL)
//	public static final String AR_WRK_WAIT_B_1SPM 						= "CB";				//작업대기(B열연#1SPM)
//	public static final String AR_WRK_WAIT_B_2SPM 						= "CC";				//작업대기(B열연#2SPM)
//	public static final String AR_WRK_WAIT_B_WATER_COOLING				= "CD";				//작업대기(B열연수냉재)
//	public static final String AR_WRK_WAIT_C_HFL						= "CE";				//작업대기(C열연HFL)
//	public static final String AR_WRK_WAIT_C_SPM1 						= "CF";				//작업대기(C열연SPM1)
//	public static final String AR_WRK_WAIT_C_SPM2 						= "CG";				//작업대기(C열연SPM2)
//	public static final String AR_WRK_WAIT_C_1BINDING					= "CH";				//작업대기(C열연#1결속대)
//	public static final String AR_WRK_WAIT_C_2BINDING					= "CI";				//작업대기(C열연#2결속대)
//	public static final String AR_INLINE_MATCH_WAIT_B_MILL				= "Y1";				//재공충당대기(B열연압연)
//	public static final String AR_INLINE_MATCH_WAIT_C_MILL				= "Y2";				//재공충당대기(C열연압연)
//	public static final String AR_INLINE_MATCH_WAIT_A_BP_CORRCETION		= "Y3";				//재공충당대기(A후판주편정정)
//	public static final String AR_INLINE_MATCH_WAIT_A_MILL				= "Y4";				//재공충당대기(A후판압연)
//	public static final String AR_INLINE_MATCH_WAIT_2_BP_CORRCETION		= "Y5";				//재공충당대기(2후판주편정정) --추가
//	public static final String AR_INLINE_MATCH_WAIT_2_MILL				= "Y6";				//재공충당대기(2후판압연)     --추가
//	public static final String AR_INLINE_MATCH_WAIT_A_CORRCETION		= "YA";				//재공충당대기(A열연정정)
//	public static final String AR_INLINE_MATCH_WAIT_B_CORRCETION		= "YB";				//재공충당대기(B열연정정)
//	public static final String AR_INLINE_MATCH_WAIT_C_CORRCETION		= "YC";				//재공충당대기(C열연정정)
//	public static final String AR_INLINE_MATCH_WAIT_A_PLATE				= "YD";				//재공충당대기(A후판Plate)
//	public static final String AR_INLINE_MATCH_WAIT_2_PLATE				= "YE";				//재공충당대기(2후판Plate) --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_HCR			= "E1";				//재공이송작업대기(B열연HCR)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_SCARF			= "E2";				//재공이송작업대기(B열연스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_NONSCARF		= "E3";				//재공이송작업대기(B열연NON스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_C_SCARF			= "E4";				//재공이송작업대기(C열연스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_C_NONSCARF		= "E5";				//재공이송작업대기(C열연NON스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_BP_SCARF		= "E6";				//재공이송작업대기(A후판주편스카핑)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_BP_CORRCETION	= "E7";				//재공이송작업대기(A후판주편정정)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_SP				= "E8";				//재공이송작업대기(A후판슬라브)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_SP_SIZING		= "E9";				//재공이송작업대기(A후판슬라브SIZING)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_2_BP_SCARF		= "EK";				//재공이송작업대기(2후판주편스카핑)   --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_2_BP_CORRCETION	= "EL";				//재공이송작업대기(2후판주편정정)     --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_2_SP				= "EM";				//재공이송작업대기(2후판슬라브)       --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_2_SP_SIZING		= "EN";				//재공이송작업대기(2후판슬라브SIZING) --추가
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_CORRCETION		= "EA";				//재공이송작업대기(A열연정정)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_CORRCETION		= "EB";				//재공이송작업대기(B열연정정)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_C_CORRCETION		= "EC";				//재공이송작업대기(C열연정정)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_A_RENTPROC		= "ED";				//재공이송작업대기(임가공이송A사)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_B_RENTPROC		= "EE";				//재공이송작업대기(임가공이송B사)
//	public static final String AR_INLINE_FTMV_WRK_WAIT_C_RENTPROC		= "EF";				//재공이송작업대기(임가공이송C사)
//	public static final String AR_OVALL_STMP_WAIT_OUTPL_SLAB			= "G1";				//종합판정대기(외판슬라브)
//	public static final String AR_OVALL_STMP_WAIT_COIL					= "G2";				//종합판정대기(COIL)
//	public static final String AR_OVALL_STMP_WAIT_PLATE					= "G3";				//종합판정대기(PLATE)
//	public static final String AR_SNDBK_WAIT_COIL						= "I2";				//반송대기(COIL)
//	public static final String AR_SNDBK_WAIT_PLATE						= "I3";				//반송대기(PLATE)
//	public static final String AR_RCPT_WAIT_OUTPL_SLAB					= "H1";				//입고대기(외판슬라브)
//	public static final String AR_RCPT_WAIT_COIL						= "H2";				//입고대기(COIL)
//	public static final String AR_RCPT_WAIT_PLATE						= "H3";				//입고대기(PLATE)
//	public static final String AR_RETN_WAIT_OUTPL_SLAB					= "J1";				//반납대기(외판슬라브)
//	public static final String AR_RETN_WAIT_COIL						= "J2";				//반납대기(COIL)
//	public static final String AR_RETN_WAIT_PLATE						= "J3";				//반납대기(PLATE)
//	public static final String AR_DIST_WO_WAIT_OUTPL_SLAB				= "K1";				//출하지시대기(외판슬라브)
//	public static final String AR_DIST_WO_WAIT_COIL						= "K2";				//출하지시대기(COIL)
//	public static final String AR_DIST_WO_WAIT_PLATE					= "K3";				//출하지시대기(PLATE)
//	public static final String AR_TRN_WO_WAIT_OUTPL_SLAB				= "N1";				//운송지시대기(외판슬라브)
//	public static final String AR_TRN_WO_WAIT_COIL						= "N2";				//운송지시대기(COIL)
//	public static final String AR_TRN_WO_WAIT_PLATE_SEL_WRK				= "N3";				//운송지시대기(PLATE선별작업대상)
//	public static final String AR_TRN_WO_WAIT_PLATE_SEL_WRK_CMPL		= "NA";				//운송지시대기(PLATE선별작업완료)
//	public static final String AR_TRN_WO_WAIT_PLATE_SEL_CMPL_SND		= "NB";				//운송지시대기(PLATE선별완료송신)
//	public static final String AR_TRN_WAIT_OUTPL_SLAB					= "L1";				//운송대기(외판슬라브)
//	public static final String AR_TRN_WAIT_COIL							= "L2";				//운송대기(COIL)
//	public static final String AR_TRN_WAIT_PLATE						= "L3";				//운송대기(PLATE)
//	public static final String AR_CARLD_WAIT_OUTPL_SLAB					= "L4";				//상차대기(외판슬라브)
//	public static final String AR_CARLD_WAIT_COIL						= "L5";				//상차대기(COIL)
//	public static final String AR_CARLD_WAIT_PLATE						= "L6";				//상차대기(PLATE)
//	public static final String AR_DIST_CMPL_OUTPL_SLAB					= "M1";				//출하완료(외판슬라브)
//	public static final String AR_DIST_CMPL_COIL						= "M2";				//출하완료(COIL)
//	public static final String AR_DIST_CMPL_PLATE						= "M3";				//출하완료(PLATE)
//	public static final String AR_GDS_MATCH_WAIT_OUTPL_SLAB				= "Z1"; 			//제품충당대기(외판슬라브)
//	public static final String AR_GDS_MATCH_WAIT_COIL					= "Z2"; 			//제품충당대기(COIL)
//	public static final String AR_GDS_MATCH_WAIT_PLATE					= "Z3"; 			//제품충당대기(PLATE)
//	public static final String AR_AUCT_TG_PKUP_OUTPL_SLAB				= "X1";				//경매대상선정(외판슬라브)
//	public static final String AR_AUCT_TG_PKUP_COIL						= "X2";				//경매대상선정(COIL)
//	public static final String AR_AUCT_TG_PKUP_PLATE					= "X3";				//경매대상선정(PLATE)
//	public static final String AR_STMP_HOLD_A_CORRCETION				= "F1";				//판정보류(A열연정정)
//	public static final String AR_STMP_HOLD_B_CORRCETION				= "F2";				//판정보류(B열연정정)
//	public static final String AR_STMP_HOLD_C_CORRCETION				= "F3";				//판정보류(C열연정정)
//	public static final String AR_STMP_HOLD_C_CORRCETION4				= "F4";				//재작업(코일-정정입측재투입)
//	public static final String AR_STMP_HOLD_C_CORRCETION5				= "F5";				//재작업(코일-보류장인출)
//	public static final String AR_WH_FTMV_A_WH							= "O1";				//고간이송(A열연창고)
//	public static final String AR_WH_FTMV_B_WH							= "O2";				//고간이송(B열연창고)
//	public static final String AR_WH_FTMV_C_WH							= "O3";				//고간이송(C열연창고)
//	public static final String AR_WH_FTMV_S_WH							= "O4";				//고간이송(통합야드)
//	public static final String AR_WH_FTMV_Z_WH							= "O5";				//고간이송(가상야드)
//	

//	//대차
//	public static final String YD_TCAR_MOVE_GP_LEAVE					= "S";				//출발
//	public static final String YD_TCAR_MOVE_GP_ARRIVE					= "E";				//도착
//	public static final String YD_TCAR_MOVE_GP_MOVE						= "M";				//이동
//	
//	/*++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/* 			A후판 슬라브 야드 상수 정의 시작
//	++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	//야드스케줄코드 정의 - 스케줄코드 추가 예정
//	public static final String SCH_CD_D_REFUR_SUP1						= "DAPU01UM";		//A동 DAPU01 장입(A후판가열로보급스케줄)
//	public static final String SCH_CD_D_PU_CARRY_OUT_02					= "DAPU02LM";		//A동 DAPU02 입고(A후판입고스케줄)
//	public static final String SCH_CD_D_DAYD99MR						= "DAYD99MR";		//A동 DAPU01 장입준비(A후판가열로보급준비스케줄)
//	
//	//설비ID
//	public static final String EQP_D_PU1								= "DAPU01";			//A동 가열로보급[수입]PICKUP베드
//	public static final String EQP_D_PU2								= "DAPU02";			//A동 입고PICKUP베드
//	
//	//가열로장입예정일련번호 비교 시 반환값 정의
//	public static final String REFUR_CHG_PLN_SERNO_SMALL				= "SERNO_SMALL";	//일련번호가 작다.
//	public static final String REFUR_CHG_PLN_SERNO_EQUAL				= "SERNO_EQUAL";	//일련번호가 같다.
//	public static final String REFUR_CHG_PLN_SERNO_BIG					= "SERNO_BIG";		//일련번호가 크다.
//	
//	//크레인 작업허용오차
//	public static final int A_PLATE_SLAB_CRANE_GAP						= 50;					//주행/횡행 오차값은 50mm
//	/*++++++++++++++++++++++++++++++++++++++++++++++++++++
//	/* 			A후판 슬라브 야드 상수 정의 끝
//	++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	

//	
//	//-------------------------------------------------------------------------------------------------
//	// 			TC_CODE 상수 정의 시작
//	//-------------------------------------------------------------------------------------------------
//	
//	/*
//	 * 야드내부 TC CODE
//	 */
//	
//	public final static String YDYDJ701									= "YDYDJ701";		//전문전송버퍼 BUFFER
//	public final static String YDYDJ702									= "YDYDJ702";		//로그메세지처리
//	public final static String YDYDJ901									= "YDYDJ901";		//플렉스 버퍼
//	
//	public final static String YDPRJ003									= "YDPRJ003";		//후판조업L3
//	
	/**
	 * 출하인터페이스 TC_CODE
	 */
	public final static String DMYDR002	="DMYDR002";   //코일제품보류확정              
	public final static String DMYDR004	="DMYDR004";   //외판슬라브출하지시대기        
	public final static String DMYDR005	="DMYDR005";   //코일제품출하지시대기          
	public final static String DMYDR008	="DMYDR008";   //코일제품반납대기              
	public final static String DMYDR011	="DMYDR011";   //코일제품고간이송지시          
	public final static String DMYDR013	="DMYDR013";   //외판슬라브목전(주문자변경)    
	public final static String DMYDR014	="DMYDR014";   //코일제품목전                  
	public final static String DMYDR016	="DMYDR016";   //외판슬라브운송대기            
	public final static String DMYDR020	="DMYDR020";   //코일제품운송지시              
	public final static String DMYDR023	="DMYDR023";   //코일제품상차지시              
	public final static String DMYDR026	="DMYDR026";   //외판슬라브보관지시            
	public final static String DMYDR027	="DMYDR027";   //코일제품보관지시              
	public final static String DMYDR029	="DMYDR029";   //외판슬라브출하완료            
	public final static String DMYDR030	="DMYDR030";   //코일제품출하완료              
	public final static String DMYDR032	="DMYDR032";   //외판슬라브반품                
	public final static String DMYDR033	="DMYDR033";   //코일제품반품                  
	public final static String DMYDR035	="DMYDR035";   //외판슬라브출하차량도착실적  
	public final static String DMYDR036	="DMYDR036";   //코일제품출하차량도착실적 
	public final static String DMYDR037	="DMYDR037";   //코일임가공차량도착실적 
	public final static String DMYDR039	="DMYDR039";   //외판슬라브출하차량출발실적 
	public final static String DMYDR040	="DMYDR040";   //코일제품출하차량출발실적 
	public final static String DMYDR041	="DMYDR041";   //코일임가공차량출발실적 	
	public final static String DMYDR012	="DMYDR012";	//후판제품고간이송지시
	public final static String DMYDR015	="DMYDR015";	//후판제품목전
	public final static String DMYDR009	="DMYDR009";	//후판제품반납대기
	public final static String DMYDR028	="DMYDR028";	//후판제품보관지시
	public final static String DMYDR003	="DMYDR003";	//후판제품보류확정
	public final static String DMYDR024	="DMYDR024";	//후판제품상차지시(삭제)
	public final static String DMYDR021	="DMYDR021";	//후판제품운송상차지시
	public final static String DMYDR046	="DMYDR046";	//후판제품선별LOT편성정보
	public final static String DMYDR022	="DMYDR022";	//외판슬라브운송상차지시
	public final static String DMYDR025	="DMYDR025";	//임가공이송상차지시
	public final static String DMYDR018	="DMYDR018";	//후판제품운송지시대기
	public final static String DMYDR031	="DMYDR031";	//후판제품출하완료
	public final static String DMYDR044	="DMYDR044";	//후판제품목적지변경
	public final static String DMYDR045	="DMYDR045";	//코일제품사외이송
	public final static String DMYDR060	="DMYDR060";	//코일제품운송상차지시
	public final static String DMYDR070	="DMYDR070";	//코일이송상차대기장도착PDA
	public final static String DMYDR073	="DMYDR073";	//코일이송하차대기장도착PDA
//
//	//-------------------------------------------------------------------------------------------------
//	
//	// HCR구분
//	public static final String HCR_GP_CCR                               = "CCR";            // CCR
//	public static final String HCR_GP_HDR                               = "HDR";            // HDR
//	public static final String HCR_GP_HCR                               = "HCR";            // HCR
//	public static final String HCR_GP_WCR                               = "WCR";            // WCR
//	
//	// 재료외형구분
//	public static final String STL_APPEAR_PREPARE                       = "A";              // 예정 재료
//	public static final String STL_APPEAR_MSLAB                         = "B";              // 주편
//	public static final String STL_APPEAR_SLAB                          = "C";              // SLAB
//	public static final String STL_APPEAR_SLABSIZING                    = "D";              // SLAB(SIZING재)
//	public static final String STL_APPEAR_HOTROLLCOIL                   = "E";              // 열연코일
//	public static final String STL_APPEAR_PARENTPLATE                   = "F";              // 날판
//	public static final String STL_APPEAR_PLATE                         = "G";              // PLATE
//	public static final String STL_APPEAR_PRODUCT                       = "Y";              // 제품
//	
//	// SLAB지시행선코드
//	public static final String SLAB_ORD_RT_CD_HOTROLL_A                 = "HA";             // A열연
//	public static final String SLAB_ORD_RT_CD_HOTROLL_B                 = "HB";             // B열연
//	public static final String SLAB_ORD_RT_CD_HOTROLL_C                 = "HC";             // C열연
//	public static final String SLAB_ORD_RT_CD_SELL_SLAB                 = "MS";             // 판매SLAB
//	public static final String SLAB_ORD_RT_CD_PLATE_A                   = "PA";             // A후판
//	public static final String SLAB_ORD_RT_CD_PLATE_B                   = "PB";             // B후판
//	
//	// 주문여재구분
//	public static final String ORD_YEOJAE_GP_ORD                        = "1";              // 주문재
//	public static final String ORD_YEOJAE_GP_YEOJAE                     = "2";              // 여재
//	

//	// 야드설비구분
	public static final String YD_EQP_GP_TCAR                  = "TC"; //대차
	public static final String YD_EQP_GP_PALLET                = "PT"; //Pallet
	public static final String YD_EQP_GP_TRAILER               = "TR"; //트레일러
	public static final String YD_EQP_GP_CRANE                 = "CR"; //크레인
//	
//	
//	//YD_EQP_WRK_STAT - 스케줄 상하차 구분
	public static final String YD_CRN_SCH_CD_LD					= "L";				//하차
	public static final String YD_CRN_SCH_CD_UD					= "U";				//상차
//
	//C열연 대차 CAPA
	public static final int YD_COIL_TC_WEIGH_MAX				= 75000;			//대차 무게
	
//	
//	//----------------------------------------------------
//	//-------------------------------------------------------------------------------------------------
//	// 	TC_CODE 상수 정의 시작
//	//-------------------------------------------------------------------------------------------------
//	
//	public final static String   PMYDJ001 	= "PMYDJ001"; //수신-공정계획-슬라브충당실적
//	
//	//야드재료품목
//	public static final String YD_ITEM_HR_MSLAB			= "BH";		//열연주편
//	public static final String YD_ITEM_PL_MSLAB			= "BP";		//후판주편
//	public static final String YD_ITEM_PL_STR_MSLAB		= "BK";		//후판비축주편
//	public static final String YD_ITEM_HR_SLAB			= "SH";		//열연슬라브
//	public static final String YD_ITEM_PL_SLAB			= "SP";		//후판슬라브
//	public static final String YD_ITEM_PL_SIZING_SLAB	= "SZ";		//후판SizingSlab
//	public static final String YD_ITEM_SLAB_GDS			= "SG";		//슬라브제품(외판)
//	public static final String YD_ITEM_COIL_MATL		= "CM";		//COIL소재
//	public static final String YD_ITEM_COIL_GDS			= "CG";		//COIL제품
//	public static final String YD_ITEM_PL_NOT_RCPT_GDS	= "PT";		//후판미입고제품
//	public static final String YD_ITEM_PL_GDS			= "PG";		//후판제품
//	
//	/*------------------------   내부  TC   ---------------------*/
//	/** 구내운송(TS) 송신 */                              
//	public final static String   YDTSJ007  = "YDTSJ007";   //상차개시실적
//	public final static String   YDTSJ008  = "YDTSJ008";   //소재차량상차완료
//	public final static String   YDTSJ009  = "YDTSJ009";   //하차개시실적
//	public final static String   YDTSJ010  = "YDTSJ010";   //하차완료
//	public final static String   YDTSJ011  = "YDTSJ011";   //소재차량Point지시
//	                                                   
//	/** 구내운송(TS) 수신 */                              
//	public final static String   TSYDJ002  = "TSYDJ002";   //소재차량도착Point 요구
//	public final static String   TSYDJ003  = "TSYDJ003";   //소재차량도착
//	public final static String   TSYDJ004  = "TSYDJ004";   //소재차량출발
//	public final static String   TSYDJ014  = "TSYDJ014";   //차량출발취소
//	                                                   
//	                                                   
//	/** 진행관리(PT) 송신 */                              
//	public final static String   YDPTJ002  = "YDPTJ002";   //코일소재이송완료실적
//	public final static String   YDPTJ003  = "YDPTJ003";   //코일소재임가공이송지시
//	public final static String   YDPTJ006  = "YDPTJ006";   //냉연코일이송진행 상태실적
//	public final static String   YDPTJ007  = "YDPTJ007";   //재료단위 이송지시 취소 작업
//	                                                   
//	/** 진행관리(PT) 수신 */                              
//	public final static String   PTYDJ001  = "PTYDJ001";   //코일충당실적
//	public final static String   PTYDJ002  = "PTYDJ002";   //코일소재이송지시
//	public final static String   PTYDJ003  = "PTYDJ003";   //코일소재임가공이송지시 
//	                                                   
//	/** 출하(DM) 송신 */                                 
//	public final static String   YDDMR001  = "YDDMR001";   //코일입고작업실적
//	public final static String   YDDMR003  = "YDDMR003";   //임가공입고작업실적
//	public final static String   YDDMR004  = "YDDMR004";   //코일제품이적작업실적
//	public final static String   YDDMR007  = "YDDMR007";   //코일출하상차개시
//	public final static String   YDDMR009  = "YDDMR009";   //외판슬라브출하상차개시
//	public final static String   YDDMR011  = "YDDMR011";   //코일일품출하상차실적
//	public final static String   YDDMR013  = "YDDMR013";   //외판슬라브일품출하상차실적
//	public final static String   YDDMR015  = "YDDMR015";   //코일출하상차완료
//	public final static String   YDDMR017  = "YDDMR017";   //외판슬라브출하상차완료
//	public final static String   YDDMR019  = "YDDMR019";   //코일제품고간이송상하차개시
//	public final static String   YDDMR020  = "YDDMR020";   //임가공이송상하차개시
//	public final static String   YDDMR021  = "YDDMR021";   //코일제품고간이송상하차완료
//	public final static String   YDDMR022  = "YDDMR022";   //임가공이송상하차완료
//	public final static String   YDDMR024  = "YDDMR024";   //HYSCO대차이송실적
//	public final static String   YDDMR025  = "YDDMR025";   //HYSCO수냉실적
//	public final static String   YDDMR026  = "YDDMR026";   //포인트점유사항 출하송신
//	public final static String   YDDMR028  = "YDDMR028";   //차량입동지시
//	public final static String   YDDMR029  = "YDDMR029";   //코일제품출하차량도착
//	public final static String   YDDMR036  = "YDDMR036";   //검수완료
//	public final static String   YDDMR050  = "YDDMR050";   //상차완료(야드 핸들링)
//	public final static String   YDDMR070  = "YDDMR070";   //차량입동지시  
//	public final static String   YDDMR071  = "YDDMR071";   //코일이송상차개시   
//	public final static String   YDDMR072  = "YDDMR072";   //코일일품출하상차실적 송신
//	public final static String   YDDMR074  = "YDDMR074";   //검수완료 PDA
//	public final static String   YDDMR075  = "YDDMR075";   //코일이송하차개시 전송PDA
//	public final static String   YDDMR076  = "YDDMR076";   //코일이송하차완료PDA
//	                                                   
//	
//	public final static String   DMYDR071  = "DMYDR071";   //코일이송상차도착PDA
//	public final static String   DMYDR072  = "DMYDR072";   //코일이송상차완료PDA
//	public final static String   DMYDR074  = "DMYDR074";   //코일이송하차도착PDA
//	public final static String   DMYDR075  = "DMYDR075";   //코일이송하차완료PDA
//	                                                   
	/** 품질(QM) 송신 */                                 
	public final static String   YDQMJ002  = "YDQMJ002";   //열연정정입측보급실적
//PIDEV_QM
	public final static String   YDQMJ601  = "YDQMJ601";   //후판제품입고(야드에서전송)
	
	/** 2열연 코일야드L2 송신 */                                
	public final static String   YDY5L001  = "YDY5L001";   //저장위치제원
	public final static String   YDY5L002  = "YDY5L002";   //저장품제원
	public final static String   YDY5L004  = "YDY5L004";   //크레인작업지시
	public final static String   YDY5L005  = "YDY5L005";   //크레인작업실적응답
	public final static String   YDY5L006  = "YDY5L006";   //대차출발지시
	public final static String   YDY5L007  = "YDY5L007";   //작업현황응답
	public final static String   YDY5L008  = "YDY5L008";   //차량작업 예정정보

	/** 2열연 코일야드L2 수신 */                                
	public final static String   Y5YDL001  = "Y5YDL001";   //저장위치제원요구
	public final static String   Y5YDL002  = "Y5YDL002";   //저장품제원요구
	public final static String   Y5YDL003  = "Y5YDL003";   //설비운전모드전환
	public final static String   Y5YDL004  = "Y5YDL004";   //설비고장복구실적
	public final static String   Y5YDL007  = "Y5YDL007";   //크레인 작업지시요구
	public final static String   Y5YDL008  = "Y5YDL008";   //크레인 권상실적
	public final static String   Y5YDL009  = "Y5YDL009";   //크레인 권하실적
	public final static String   Y5YDL011  = "Y5YDL011";   //야드대차이동실적
	public final static String   Y5YDL012  = "Y5YDL012";   //강제권상요구
	public final static String   Y5YDL013  = "Y5YDL013";   //작업현황요구
	public final static String   Y5YDL014  = "Y5YDL014";   //스케쥴작업요구
	public final static String   Y5YDL015  = "Y5YDL015";   //크레인작업 가능유무응답
	public final static String   Y5YDL016  = "Y5YDL016";   //차량작업예정정보요구
	public final static String   Y5YDL017  = "Y5YDL017";   //상차도 작업불가
	public final static String   Y5YDL018  = "Y5YDL018";   //차량동간이적(도착)
	public final static String   Y5YDL019  = "Y5YDL019";   //크레인 비상조업실적
	//추가 20190501 2열연 자동화
	public final static String   Y5YDL020  = "Y5YDL020";   //코일 SPM1 TrackIng 정보
	public final static String   Y5YDL021  = "Y5YDL021";   //코일 SPM2 TrackIng 정보
	public final static String   Y5YDL022  = "Y5YDL022";   //코일 SPM3 TrackIng 정보
	public final static String   Y5YDL023  = "Y5YDL023";   //코일 SPM4 TrackIng 정보
	public final static String   Y5YDL024  = "Y5YDL024";   //코일 SPM5 TrackIng 정보
	public final static String   Y5YDL025  = "Y5YDL025";   //코일 HFL1 TrackIng 정보
	public final static String   Y5YDL026  = "Y5YDL026";   //코일 HFL4 TrackIng 정보
	public final static String   Y5YDL027  = "Y5YDL027";   //코일 수입 Conveyor TrackIng 정보
	public final static String   Y5YDL028  = "Y5YDL028";   //분동코일작업지시요구
	public final static String   Y5YDL029  = "Y5YDL029";   //스크랩차량 차단기 정보
	public final static String   Y5YDL030  = "Y5YDL030";   //크레인주행금지구간
	public final static String   Y5YDL031  = "Y5YDL031";   //크레인위치정보
//
//
//	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//	* 야드업무 내부적으로 사용되는 함수리턴코드 정의
//	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
//	
	//야드차량진행상태
	public static final String YD_CAR_PROG_STAT_1			= "1";				//상차출발 YD_CARLD_LEV
	public static final String YD_CAR_PROG_STAT_2			= "2";				//상차도착 YD_CARLD_ARR
	public static final String YD_CAR_PROG_STAT_3			= "3";				//상차검수 YD_CARLD_CHK
	public static final String YD_CAR_PROG_STAT_4			= "4";				//상차개시 YD_CARLD_ST
	public static final String YD_CAR_PROG_STAT_5			= "5";				//상차완료 YD_CARLD_CMPL
	public static final String YD_CAR_PROG_STAT_A			= "A";				//하차출발 YD_CARUD_LEV
	public static final String YD_CAR_PROG_STAT_B			= "B";				//하차도착 YD_CARUD_ARR
	public static final String YD_CAR_PROG_STAT_C			= "C";				//하차검수 YD_CARUD_CHK
	public static final String YD_CAR_PROG_STAT_D			= "D";				//하차개시 YD_CARUD_ST
	public static final String YD_CAR_PROG_STAT_E			= "E";				//하차완료 하차완료	 
//	
//	
//	//개소코드
	public static final String WLOC_CD_A_CC_B_CAST_SLAB_YARD= "D2Y43";			//A연주-B Cast Slab Yard (D2Y43)
	public static final String WLOC_CD_A_HR_NO1_COIL_YARD	= "D2Y44";			//A열연-#1 제품/소재 Coil Yard (D2Y44)
	public static final String WLOC_CD_A_HR_NO2_COIL_YARD	= "D2Y45";			//A열연-#2 제품/소재 Coil Yard (D2Y45)
	
	public static final String WLOC_CD_B_HR_NO1_COIL_YARD	= "D3Y41";			//B열연-#1 제품/소재 Coil Yard (D3Y41)
	public static final String WLOC_CD_B_HR_NO2_COIL_YARD	= "D3Y42";			//B열연-#2 제품/소재 Coil Yard (D3Y42)
	public static final String WLOC_CD_B_HR_SLAB_YARD		= "D3Y43";			//B열연-Slab Yard (D3Y43)
	public static final String WLOC_CD_B_HR_REFUR_SLAB_YARD	= "D3Y44";			//B열연-가열로 Slab Yard (D3Y44)
//	
//	//대차 직상차 구분
//	public static final String EQP_DIR_UP_GP0               = "0";				//직상차없음
//	public static final String EQP_DIR_UP_GP1               = "1";				//SPM1 추출
//	public static final String EQP_DIR_UP_GP2               = "2";				//SPM2 추출
//	public static final String EQP_DIR_UP_GP3               = "3";				//HFL추출
//	public static final String EQP_DIR_UP_GP4               = "4";				//HFL결속대
//	
	//야드설비작업Mode
	public static final String YD_EQP_WRK_MODE_1			= "1";				//ON LINE
	public static final String YD_EQP_WRK_MODE_2			= "2";				//OFF LINE
	public static final String YD_EQP_WRK_MODE_3			= "3";				//
	public static final String YD_EQP_WRK_MODE_4			= "4";				//일시정지
	public static final String YD_EQP_WRK_MODE_5			= "5";				//비상정지
//
//	/*	반납 코드 */ 
	public final static String RETURN_GP_1					= "1"; //현물반납
	public final static String RETURN_GP_2 					= "2"; //정보반납	
//	
	public final static String CURR_PROG_CD_COIL_1 			= "1";	//생산예정		1C  
	public final static String CURR_PROG_CD_COIL_3 			= "3";	//생산종료		3C  
	public final static String CURR_PROG_CD_COIL_A 			= "A";	//재질판정대기	AC  
	public final static String CURR_PROG_CD_COIL_R 			= "R";	//재질판정대기	AC 
	public final static String CURR_PROG_CD_COIL_B 			= "B";	//정정작업지시  	BC  
	public final static String CURR_PROG_CD_COIL_C 			= "C";	//정정작업대기	CC  
	public final static String CURR_PROG_CD_COIL_D 			= "D";	//이송작업지시  	DC  
	public final static String CURR_PROG_CD_COIL_E 			= "E";	//이송작업대기	EC  
	public final static String CURR_PROG_CD_COIL_F 			= "F";	//판정보류		FC  
	public final static String CURR_PROG_CD_COIL_G 			= "G";	//종합판정대기	GC  
	public final static String CURR_PROG_CD_COIL_H 			= "H";	//입고대기		HG  
	public final static String CURR_PROG_CD_COIL_J 			= "J";	//반납 대기		JG  
	public final static String CURR_PROG_CD_COIL_K 			= "K";	//출하작업지시  	KG  
	public final static String CURR_PROG_CD_COIL_L 			= "L";	//출하작업대기	LG  
	public final static String CURR_PROG_CD_COIL_M 			= "M";	//출하완료		MG  
	public final static String CURR_PROG_CD_COIL_P 			= "P";	//인도완료		PG  
	public final static String CURR_PROG_CD_COIL_N 			= "N";	//운송지시대기	NG  
	public final static String CURR_PROG_CD_COIL_X 			= "X";	//경매대상선정	XG 
	public final static String CURR_PROG_CD_COIL_Y 			= "Y";	//재공충당대기	YG  
	public final static String CURR_PROG_CD_COIL_Z 			= "Z";	//제품충당대기	ZG  	
//	
//	
	/* B열연 COIL 크레인 자동화 - 스케줄종류 */
	public final static String YD_SCH_CD_3ADC01LM   	    = "3ADC01LM";   //A동 DC LineOff     
	public final static String YD_SCH_CD_3ADC03UM      		= "3ADC03UM";   //A동 DC TakeIn      
	public final static String YD_SCH_CD_3AKD01LM       	= "3AKD01LM";   //A동 SPM 추출       
	public final static String YD_SCH_CD_3AFE01UM       	= "3AFE01UM";   //A동 HFL 보급       
	public final static String YD_SCH_CD_3AFE03LM       	= "3AFE03LM";   //A동 HFL TakeOut    
	public final static String YD_SCH_CD_3AFE03UM       	= "3AFE03UM";   //A동 HFL TakeIn     
	public final static String YD_SCH_CD_3ATC01UM       	= "3ATC01UM";   //A동 동간이적상차   
	public final static String YD_SCH_CD_3ATC01LM       	= "3ATC01LM";   //A동 #HFL 대차하차  
	public final static String YD_SCH_CD_3ATC02UM       	= "3ATC02UM";   //A동 대차출하상차   
	public final static String YD_SCH_CD_3ATC02LM       	= "3ATC02LM";   //A동 #냉연 대차하차 
	public final static String YD_SCH_CD_3ATC03LM       	= "3ATC03LM";   //A동 #N1 대차하차   
	public final static String YD_SCH_CD_3ATC03UM       	= "3ATC03UM";   //A동 #N1 이적상차   
	public final static String YD_SCH_CD_3APT01UM       	= "3APT01UM";   //A동 차량출고(L)    
	public final static String YD_SCH_CD_3APT01LM       	= "3APT01LM";   //A동 차량반입(L)    
	public final static String YD_SCH_CD_3APT05UM       	= "3APT05UM";   //A동 차량출고(R)    
	public final static String YD_SCH_CD_3APT05LM       	= "3APT05LM";   //A동 차량반입(L)    
	public final static String YD_SCH_CD_3APT02UM       	= "3APT02UM";   //A동 이송상차(L)    
	public final static String YD_SCH_CD_3APT02LM       	= "3APT02LM";   //A동 이송하차(L)    
	public final static String YD_SCH_CD_3APT06UM       	= "3APT06UM";   //A동 이송상차(R)    
	public final static String YD_SCH_CD_3APT06LM       	= "3APT06LM";   //A동 이송하차(R)    
	public final static String YD_SCH_CD_3APT03UM       	= "3APT03UM";   //A동 차량이송상차(L)
	public final static String YD_SCH_CD_3APT03LM       	= "3APT03LM";   //A동 차량이송하차(L)
	public final static String YD_SCH_CD_3APT07UM       	= "3APT07UM";   //A동 차량이송상차(R)
	public final static String YD_SCH_CD_3APT07LM       	= "3APT07LM";   //A동 차량이송하차(R)
	public final static String YD_SCH_CD_3AYD01MM       	= "3AYD01MM";   //A동 동내이적(L)    
	public final static String YD_SCH_CD_3AYD05MM       	= "3AYD05MM";   //A동 동내이적(R)    
	public final static String YD_SCH_CD_3AYD12MM       	= "3AYD12MM";   //A동 자동이적1      	
	public final static String YD_SCH_CD_3BDC01LM   	    = "3BDC01LM";   //B동 DC LineOff
	public final static String YD_SCH_CD_3BDC03LM      		= "3BDC03LM";   //B동 DC TakeOut
	public final static String YD_SCH_CD_3BEC01LM       	= "3BEC01LM";   //B동 EC LineOff
	public final static String YD_SCH_CD_3BKE01UM       	= "3BKE01UM";   //B동 SPM 보급
	public final static String YD_SCH_CD_3BKE03UM       	= "3BKE03UM";   //B동 SPM TakeIn
	public final static String YD_SCH_CD_3BKE03LM       	= "3BKE03LM";   //B동 SPM TakeOut
	public final static String YD_SCH_CD_3BFE01UM       	= "3BFE01UM";   //B동 HFL 보급
	public final static String YD_SCH_CD_3BFE03LM       	= "3BFE03LM";   //B동 HFL TakeOut
	public final static String YD_SCH_CD_3BFE03UM       	= "3BFE03UM";   //B동 HFL TakeIn
	public final static String YD_SCH_CD_3BHS01LM       	= "3BHS01LM";   //B동 HFL결속대 추출
	public final static String YD_SCH_CD_3BHS01UM       	= "3BHS01UM";   //B동 HFL결속대 보급
	public final static String YD_SCH_CD_3BTC01UM       	= "3BTC01UM";   //B동 동간이적상차
	public final static String YD_SCH_CD_3BTC01LM       	= "3BTC01LM";   //B동 #HFL 대차하차
	public final static String YD_SCH_CD_3BTC02UM       	= "3BTC02UM";   //B동 대차출하상차
	public final static String YD_SCH_CD_3BTC02LM       	= "3BTC02LM";   //B동 #냉연 대차하차
	public final static String YD_SCH_CD_3BTC03LM       	= "3BTC03LM";   //B동 #N1 대차하차
	public final static String YD_SCH_CD_3BTC03UM       	= "3BTC03UM";   //B동 #N1 이적상차
	public final static String YD_SCH_CD_3BPT01UM       	= "3BPT01UM";   //B동 차량출고(L)
	public final static String YD_SCH_CD_3BPT01LM       	= "3BPT01LM";   //B동 차량반입(L)
	public final static String YD_SCH_CD_3BPT05UM       	= "3BPT05UM";   //B동 차량출고(R)
	public final static String YD_SCH_CD_3BPT05LM       	= "3BPT05LM";   //B동 차량반입(R)
	public final static String YD_SCH_CD_3BPT02UM       	= "3BPT02UM";   //B동 이송상차(L)
	public final static String YD_SCH_CD_3BPT02LM       	= "3BPT02LM";   //B동 이송하차(L)
	public final static String YD_SCH_CD_3BPT06UM       	= "3BPT06UM";   //B동 이송상차(R)
	public final static String YD_SCH_CD_3BPT06LM       	= "3BPT06LM";   //B동 이송하차(R)
	public final static String YD_SCH_CD_3BPT03UM       	= "3BPT03UM";   //B동 차량이송상차(L)
	public final static String YD_SCH_CD_3BPT03LM       	= "3BPT03LM";   //B동 차량이송하차(L)
	public final static String YD_SCH_CD_3BPT07UM       	= "3BPT07UM";   //B동 차량이송상차(R)
	public final static String YD_SCH_CD_3BPT07LM       	= "3BPT07LM";   //B동 차량이송하차(R)
	public final static String YD_SCH_CD_3BYD01MM       	= "3BYD01MM";   //B동 동내이적(L)
	public final static String YD_SCH_CD_3BYD05MM       	= "3BYD05MM";   //B동 동내이적(R) 
	public final static String YD_SCH_CD_3BYD12MM       	= "3BYD12MM";   //B동 자동이적1
	public final static String YD_SCH_CD_3CDC01LM       	= "3CDC01LM";   //C동 DC LineOff
	public final static String YD_SCH_CD_3CEC01LM       	= "3CEC01LM";   //C동 EC LineOff
	public final static String YD_SCH_CD_3CKE01UM       	= "3CKE01UM";   //C동 SPM 보급
	public final static String YD_SCH_CD_3CKE03UM       	= "3CKE03UM";   //C동 SPM TakeIn
	public final static String YD_SCH_CD_3CKE03LM       	= "3CKE03LM";   //C동 SPM TakeOut
	public final static String YD_SCH_CD_3CFE01LM       	= "3CFE01LM";   //C동 HFL 추출
	public final static String YD_SCH_CD_3CHS01LM       	= "3CHS01LM";   //C동 HFL결속대 추출
	public final static String YD_SCH_CD_3CHS01UM       	= "3CHS01UM";   //C동 HFL결속대 보급
	public final static String YD_SCH_CD_3CTC01UM       	= "3CTC01UM";   //C동 동간이적상차
	public final static String YD_SCH_CD_3CTC01LM       	= "3CTC01LM";   //C동 #HFL 대차하차
	public final static String YD_SCH_CD_3CTC02UM       	= "3CTC02UM";   //C동 대차출하상차
	public final static String YD_SCH_CD_3CTC02LM       	= "3CTC02LM";   //C동 #냉연 대차하차
	public final static String YD_SCH_CD_3CTC04LM       	= "3CTC04LM";   //C동 #N2 대차하차
	public final static String YD_SCH_CD_3CTC04UM       	= "3CTC04UM";   //C동 #N2 동간이적상차
	public final static String YD_SCH_CD_3CPT01UM       	= "3CPT01UM";   //C동 차량출고(L)
	public final static String YD_SCH_CD_3CPT01LM       	= "3CPT01LM";   //C동 차량반입(L)
	public final static String YD_SCH_CD_3CPT05UM       	= "3CPT05UM";   //C동 차량출고(R)
	public final static String YD_SCH_CD_3CPT05LM       	= "3CPT05LM";   //C동 차량반입(R)
	public final static String YD_SCH_CD_3CPT02UM       	= "3CPT02UM";   //C동 이송상차(L)
	public final static String YD_SCH_CD_3CPT02LM       	= "3CPT02LM";   //C동 이송하차(L)
	public final static String YD_SCH_CD_3CPT06UM       	= "3CPT06UM";   //C동 이송상차(R)
	public final static String YD_SCH_CD_3CPT06LM       	= "3CPT06LM";   //C동 이송하차(R)
	public final static String YD_SCH_CD_3CPT03UM       	= "3CPT03UM";   //C동 차량이송상차(L)
	public final static String YD_SCH_CD_3CPT03LM       	= "3CPT03LM";   //C동 차량이송하차(L)
	public final static String YD_SCH_CD_3CPT07UM       	= "3CPT07UM";   //C동 차량이송상차(R)
	public final static String YD_SCH_CD_3CPT07LM       	= "3CPT07LM";   //C동 차량이송하차(R)
	public final static String YD_SCH_CD_3CYD01MM       	= "3CYD01MM";   //C동 동내이적(L)
	public final static String YD_SCH_CD_3CYD05MM       	= "3CYD05MM";   //C동 동내이적(R) 
	public final static String YD_SCH_CD_3CYD12MM       	= "3CYD12MM";   //C동 자동이적1
	public final static String YD_SCH_CD_3DEC01LM       	= "3DEC01LM";   //D동 EC LineOff
	public final static String YD_SCH_CD_3DKE02UM       	= "3DKE02UM";   //D동 SPM2 보급
	public final static String YD_SCH_CD_3DKE03UM       	= "3DKE03UM";   //D동 SPM2 TakeIn
	public final static String YD_SCH_CD_3DKE03LM       	= "3DKE03LM";   //D동 SPM2 TakeOut
	public final static String YD_SCH_CD_3DHS01LM       	= "3DHS01LM";   //D동 HFL결속대 추출
	public final static String YD_SCH_CD_3DHS01UM       	= "3DHS01UM";   //D동 HFL결속대 보급
	public final static String YD_SCH_CD_3DTC01UM       	= "3DTC01UM";   //D동 동간이적상차
	public final static String YD_SCH_CD_3DTC01LM       	= "3DTC01LM";   //D동 #HFL 대차하차
	public final static String YD_SCH_CD_3DTC02UM       	= "3DTC02UM";   //D동 대차출하상차
	public final static String YD_SCH_CD_3DTC02LM       	= "3DTC02LM";   //D동 #냉연 대차하차
	public final static String YD_SCH_CD_3DTC04LM       	= "3DTC04LM";   //D동 #N2 대차하차
	public final static String YD_SCH_CD_3DTC04UM       	= "3DTC04UM";   //D동 #N2 동간이적상차
	public final static String YD_SCH_CD_3DTC05UM       	= "3DTC05UM";   //D동 #N3 동간이적상차
	public final static String YD_SCH_CD_3DTC05LM       	= "3DTC05LM";   //D동 #N3 대차하차
	public final static String YD_SCH_CD_3DPT01UM       	= "3DPT01UM";   //D동 차량출고(L)
	public final static String YD_SCH_CD_3DPT01LM       	= "3DPT01LM";   //D동 차량반입(L)
	public final static String YD_SCH_CD_3DPT05UM       	= "3DPT05UM";   //D동 차량출고(R)
	public final static String YD_SCH_CD_3DPT05LM       	= "3DPT05LM";   //D동 차량반입(R)
	public final static String YD_SCH_CD_3DPT02UM       	= "3DPT02UM";   //D동 이송상차(L)
	public final static String YD_SCH_CD_3DPT02LM       	= "3DPT02LM";   //D동 이송하차(L)
	public final static String YD_SCH_CD_3DPT06UM       	= "3DPT06UM";   //D동 이송상차(R)
	public final static String YD_SCH_CD_3DPT06LM       	= "3DPT06LM";   //D동 이송하차(R)
	public final static String YD_SCH_CD_3DPT03UM       	= "3DPT03UM";   //D동 차량이송상차(L)
	public final static String YD_SCH_CD_3DPT03LM       	= "3DPT03LM";   //D동 차량이송하차(L)
	public final static String YD_SCH_CD_3DPT07UM       	= "3DPT07UM";   //D동 차량이송상차(R)
	public final static String YD_SCH_CD_3DPT07LM       	= "3DPT07LM";   //D동 차량이송하차(R)
	public final static String YD_SCH_CD_3DYD01MM       	= "3DYD01MM";   //D동 동내이적(L)
	public final static String YD_SCH_CD_3DYD05MM       	= "3DYD05MM";   //D동 동내이적(R) 
	public final static String YD_SCH_CD_3DYD12MM       	= "3DYD12MM";   //D동 자동이적1
	public final static String YD_SCH_CD_3EKE02UM       	= "3EKE02UM";   //E동 SPM2 보급
	public final static String YD_SCH_CD_3EKD02LM       	= "3EKD02LM";   //E동 SPM2 추출
	public final static String YD_SCH_CD_3EKE03LM       	= "3EKE03LM";   //E동 SPM2 TakeOut
	public final static String YD_SCH_CD_3EGF01LM       	= "3EGF01LM";   //E동 지포장 추출
	public final static String YD_SCH_CD_3EGF01UM       	= "3EGF01UM";   //E동 지포장 보급
	public final static String YD_SCH_CD_3ETC01UM       	= "3ETC01UM";   //E동 동간이적상차
	public final static String YD_SCH_CD_3ETC01LM       	= "3ETC01LM";   //E동 #HFL 대차하차
	public final static String YD_SCH_CD_3ETC05UM       	= "3ETC05UM";   //E동 #N3 동간이적상차
	public final static String YD_SCH_CD_3ETC05LM       	= "3ETC05LM";   //E동 #N3 대차하차
	public final static String YD_SCH_CD_3EPT01UM       	= "3EPT01UM";   //E동 차량출고(L)
	public final static String YD_SCH_CD_3EPT01LM       	= "3EPT01LM";   //E동 차량반입(L)
	public final static String YD_SCH_CD_3EPT05UM       	= "3EPT05UM";   //E동 차량출고(R)
	public final static String YD_SCH_CD_3EPT05LM       	= "3EPT05LM";   //E동 차량반입(R)
	public final static String YD_SCH_CD_3EPT02UM       	= "3EPT02UM";   //E동 이송상차(L)
	public final static String YD_SCH_CD_3EPT02LM       	= "3EPT02LM";   //E동 이송하차(L)
	public final static String YD_SCH_CD_3EPT06UM       	= "3EPT06UM";   //E동 이송상차(R)
	public final static String YD_SCH_CD_3EPT06LM       	= "3EPT06LM";   //E동 이송하차(R)
	public final static String YD_SCH_CD_3EPT03UM       	= "3EPT03UM";   //E동 차량이송상차(L)
	public final static String YD_SCH_CD_3EPT03LM       	= "3EPT03LM";   //E동 차량이송하차(L)
	public final static String YD_SCH_CD_3EPT07UM       	= "3EPT07UM";   //E동 차량이송상차(R)
	public final static String YD_SCH_CD_3EPT07LM       	= "3EPT07LM";   //E동 차량이송하차(R)
	public final static String YD_SCH_CD_3EYD01MM       	= "3EYD01MM";   //E동 동내이적(L)
	public final static String YD_SCH_CD_3EYD05MM       	= "3EYD05MM";   //E동 동내이적(R) 
	public final static String YD_SCH_CD_3EYD12MM       	= "3EYD12MM";   //E동 자동이적1
	
//	

//	
//    /* 야드L2요구상태 */
    public final static String YD_L2_REQUEST_STAT_1     	= "1";//'선택'
    public final static String YD_L2_REQUEST_STAT_2     	= "2";//'권상'
    public final static String YD_L2_REQUEST_STAT_C     	= "C";//'보류'
    public final static String YD_L2_REQUEST_STAT_W     	= "W";//'선택'
    public final static String YD_L2_REQUEST_STAT_5     	= "5";//'권하위치변경'
    public final static String YD_L2_REQUEST_STAT_D     	= "D";//'스케쥴취소요청:응답대기중(A7YML015)'
    public final static String YD_L2_REQUEST_STAT_X     	= "X";//'작업취소요청   :응답대기중(A7YML015)'
//    
//    

    public final static String CAR_BAY_TRANS_CARD_NO_1="9999";
    public final static String CAR_BAY_TRANS_CARD_NO_2="9998";
    public final static String CAR_BAY_TRANS_CARD_NO_3="9997";
    public final static String CAR_BAY_TRANS_CARD_NO_4="9996";
    public final static String CAR_BAY_TRANS_CARD_NO_5="9995";


//	
//	//A열연 동에 따른 ROT Name(MCH)
//	public final static String EQUIP_KIND_RT     	= "RT"; 	//팔레트
//	public final static String EQUIP_KIND_0_A_RT    = "RT03"; 	//ROT 존
//	public final static String EQUIP_KIND_0_B_RT    = "RT02"; 	//ROT 존
//	
//	public final static String EQUIP_GP_2XTC01     	= "2XTC01"; 	//B열연 확장대차
//	public final static String EQUIP_GP_2XTC02     	= "2XTC02"; 	//B열연 확장대차
//	public final static String EQUIP_GP_2XTC03     	= "2XTC03"; 	//B열연 확장대차
//	public final static String EQUIP_GP_3XTC02     	= "3XTC02"; 	//B열연 확장대차
//	
//	public final static String MAIN_WORK_M			= "M";// 주작업
//	public final static String SUB_WORK_S			= "S";// 보조작업 
//	public final static String MAIN_WORK_01			= "01";// 주작업
//	public final static String SUB_WORK_02			= "02";// 보조작업
//	
	public final static String ITEM_SM 				= "SM";	// SLAB 소재
	public final static String ITEM_CM 				= "CM";	// COIL 소재
	public final static String ITEM_CG 				= "CG";	// COIL 제품
	public final static String ITEM_HP 				= "HP";	// Plate
//
//	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */	
//	
//	/* 이동조건 */
	public final static String NEW_STOCK_MOVE_TERM_1C 	= "1C";	//생산예정
	public final static String NEW_STOCK_MOVE_TERM_A1 	= "A1";	//HFL 추출
	public final static String NEW_STOCK_MOVE_TERM_A2 	= "A2";	//SPM 추출
	public final static String NEW_STOCK_MOVE_TERM_A3 	= "A3";   //수냉재추출
	public final static String NEW_STOCK_MOVE_TERM_A4 	= "A4";	//공냉재추출
	public final static String NEW_STOCK_MOVE_TERM_A5 	= "A5";	//수냉재보급완료
	public final static String NEW_STOCK_MOVE_TERM_AC   = "AC";	//재질판정대기
	public final static String NEW_STOCK_MOVE_TERM_BC	= "BC";	//정정작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_CC	= "CC";	//정정작업대기
	public final static String NEW_STOCK_MOVE_TERM_C1 	= "C1";	//보급완료 
	public final static String NEW_STOCK_MOVE_TERM_DC 	= "DC";	//이송작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_E1 	= "E1";	//이송완료
	public final static String NEW_STOCK_MOVE_TERM_EC	= "EC";	//이송작업대기
	public final static String NEW_STOCK_MOVE_TERM_FC 	= "FC";	//판정보류
	public final static String NEW_STOCK_MOVE_TERM_GC 	= "GC";	//종합판정대기
	public final static String NEW_STOCK_MOVE_TERM_HG 	= "HG";	//입고대기
	public final static String NEW_STOCK_MOVE_TERM_H1 	= "H1";	//입고완료
	public final static String NEW_STOCK_MOVE_TERM_JG 	= "JG";	//반납 대기(정보)
	public final static String NEW_STOCK_MOVE_TERM_JR 	= "JR";	//반납 대기(현물)
	public final static String NEW_STOCK_MOVE_TERM_J1 	= "J1";	//반납 완료
	public final static String NEW_STOCK_MOVE_TERM_KG 	= "KG";	//출하작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_K1 	= "K1";	//반입대기
	public final static String NEW_STOCK_MOVE_TERM_LG 	= "LG";	//출하작업대기
	public final static String NEW_STOCK_MOVE_TERM_L1 	= "L1";	//대차출하완료
	public final static String NEW_STOCK_MOVE_TERM_L2 	= "L2";	//대차출하대기
	public final static String NEW_STOCK_MOVE_TERM_MG 	= "MG";	//출하완료
	public final static String NEW_STOCK_MOVE_TERM_M1 	= "M1";	//보관Coil
	public final static String NEW_STOCK_MOVE_TERM_M2 	= "M2";	//보관제품
	public final static String NEW_STOCK_MOVE_TERM_NG 	= "NG";	//운송지시대기	
	public final static String NEW_STOCK_MOVE_TERM_XG 	= "XG";	//경매대상선정
	public final static String NEW_STOCK_MOVE_TERM_YG 	= "YG";	//재공충당대기
	public final static String NEW_STOCK_MOVE_TERM_ZG 	= "ZG";	//제품충당대기
	public final static String NEW_STOCK_MOVE_TERM_TL 	= "TL";	//대차상차완료
	public final static String NEW_STOCK_MOVE_TERM_TM 	= "TM";	//대차이동
	public final static String NEW_STOCK_MOVE_TERM_CL 	= "CL";	//CTS상차완료
	public final static String NEW_STOCK_MOVE_TERM_CM 	= "CM";	//CTS이동
    public final static String NEW_STOCK_MOVE_TERM_CR 	= "CR";	// Coil 차량이적
	public final static String NEW_STOCK_MOVE_TERM_RL 	= "RL";	// Coil 차량상차완료
	public final static String NEW_STOCK_MOVE_TERM_A6 	= "A6";	// B열연SPM2추출
	public final static String NEW_STOCK_MOVE_TERM_A7 	= "A7";	// HFL결속장  추출
	public final static String NEW_STOCK_MOVE_TERM_A8 	= "A8";	// 지포장  추출
	//===============================================================================

	public final static String NEW_STOCK_MOVE_TERM_1S 	= "1S";	//생산예정
	public final static String NEW_STOCK_MOVE_TERM_11 	= "11";	//SLAB 구입등록(품질)
	public final static String NEW_STOCK_MOVE_TERM_12 	= "12";	//SLAB 구입확정(품질)
	public final static String NEW_STOCK_MOVE_TERM_3S 	= "3S";	//생산종료
	public final static String NEW_STOCK_MOVE_TERM_AS 	= "AS";	//수입검사대기
	public final static String NEW_STOCK_MOVE_TERM_BS 	= "BS";	//이송지시대기
	public final static String NEW_STOCK_MOVE_TERM_B0 	= "B0";	//후판WCR재추출
	public final static String NEW_STOCK_MOVE_TERM_B1 	= "B1";	//B열연WCR재추출
	public final static String NEW_STOCK_MOVE_TERM_B2 	= "B2";	//후판CCR재추출
	public final static String NEW_STOCK_MOVE_TERM_B3 	= "B3";	//B열연CCR재추출
	public final static String NEW_STOCK_MOVE_TERM_B4 	= "B4";	//C열연WCR재추출
	public final static String NEW_STOCK_MOVE_TERM_B5 	= "B5";	//C열연CCR재추출
	public final static String NEW_STOCK_MOVE_TERM_CS	= "CS";	//이송대기
	public final static String NEW_STOCK_MOVE_TERM_DS 	= "DS";	//정정작업대기
	public final static String NEW_STOCK_MOVE_TERM_D1 	= "D1";	//Scarfing 보급완료
	public final static String NEW_STOCK_MOVE_TERM_D2 	= "D2";	//시편작업대기
	public final static String NEW_STOCK_MOVE_TERM_D3 	= "D3";	//핸드스카핑작업대기
	public final static String NEW_STOCK_MOVE_TERM_D4 	= "D4";	//보류재
	public final static String NEW_STOCK_MOVE_TERM_ES 	= "ES";	//압연지시대기
	public final static String NEW_STOCK_MOVE_TERM_FS 	= "FS";	//압연작업대기
	public final static String NEW_STOCK_MOVE_TERM_F1 	= "F1";	//W/B 보급완료
	public final static String NEW_STOCK_MOVE_TERM_F2 	= "F2";	//CTC Loading완료
	public final static String NEW_STOCK_MOVE_TERM_F3 	= "F3";	//R/T Loading완료
	public final static String NEW_STOCK_MOVE_TERM_KS 	= "KS";	//SLAB 출하작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_LS 	= "LS";	//SLAB 출하작업대기
	public final static String NEW_STOCK_MOVE_TERM_MS 	= "MS";	//SLAB 출하완료
	public final static String NEW_STOCK_MOVE_TERM_NS 	= "NS";	//SLAB 운송지시대기[신규추가]
	public final static String NEW_STOCK_MOVE_TERM_GS 	= "GS";	//SLAB 종합판정대기[신규추가]
	public final static String NEW_STOCK_MOVE_TERM_HS 	= "HS";	//SLAB 입고대기[신규추가]
	public final static String NEW_STOCK_MOVE_TERM_JS 	= "JS";	//SLAB 반납대기[신규추가]
	public final static String NEW_STOCK_MOVE_TERM_YS 	= "YS";	//판정보류
	public final static String NEW_STOCK_MOVE_TERM_ZS 	= "ZS";	//충당대기
	public final static String NEW_STOCK_MOVE_TERM_VL 	= "VL";	//상차완료
	public final static String NEW_STOCK_MOVE_TERM_VM 	= "VM";	//차량이동
	public final static String NEW_STOCK_MOVE_TERM_VW 	= "VW";	//WCR이동
//	
//	/* COIL NEW 스케쥴종류 */
// 
//	public final static String NEW_SCH_WORK_KIND_CDLO = "CDLO";		//Coil DC Line Off  
//
//	
//	/* 저장 영역 */
//	public final static String STACK_COL_USAGE_CD_C1 	= "C1";			// COIL 냉각장
//	public final static String STACK_COL_USAGE_CD_C2 	= "C2";			// COIL TAKE OUT 적치장
//	public final static String STACK_COL_USAGE_CD_C4 	= "C4";			// COIL 비상적치장
//	public final static String STACK_COL_USAGE_CD_CC 	= "CC";			// COIL 분기콘베이어
//	public final static String STACK_COL_USAGE_CD_CE 	= "CE";			// COIL 확장콘베이어
//	public final static String STACK_COL_USAGE_CD_CW 	= "CW";			// COIL 수냉탱크
//	public final static String STACK_COL_USAGE_CD_CX 	= "CX";			// 대차정지위치
//
//	public final static String STACK_COL_USAGE_CD_FD 	= "FD";			// COIL HFL추출위치
//	public final static String STACK_COL_USAGE_CD_FE 	= "FE";			// COIL HFL보급위치
//
//	public final static String STACK_COL_USAGE_CD_G1 	= "G1";			// COIL 제품출하대기장
//	public final static String STACK_COL_USAGE_CD_G5 	= "G5";			// COIL 제품보관적치장
//	public final static String STACK_COL_USAGE_CD_GF 	= "GF";			// COIL 지포장장
//	
//	public final static String STACK_COL_USAGE_CD_HS 	= "HS";			// COIL HFL 결속대 
//	public final static String STACK_COL_USAGE_CD_KD 	= "KD";			// COIL SPM추출위치
//	public final static String STACK_COL_USAGE_CD_KE 	= "KE";			// COIL SPM보급위치
//	
//	public final static String STACK_COL_USAGE_CD_SC 	= "SC";			//
//	
//	public final static String STACK_COL_USAGE_CD_TX 	= "TX";			// 차량정지위치
//	public final static String STACK_COL_USAGE_CD_XX 	= "XX";			// COIL 비상적치위치
//
////	public final static String STACK_COL_USAGE_CD_C3 	= "C3";			// COIL 이상분기적치장
////	public final static String STACK_COL_USAGE_CD_C5 	= "C5";			// COIL 보관소재적치장
////	public final static String STACK_COL_USAGE_CD_C6 	= "C6";			// COIL 정정보급대기장
////	public final static String STACK_COL_USAGE_CD_C7 	= "C7";			// COIL 소재이송대기장
////	public final static String STACK_COL_USAGE_CD_C8 	= "C8";			// COIL 보류장
////	public final static String STACK_COL_USAGE_CD_G2 	= "G2";			// COIL 제품이송상차장
////	public final static String STACK_COL_USAGE_CD_G3 	= "G3";			// COIL 제품이송하차장
////	public final static String STACK_COL_USAGE_CD_G4 	= "G4";			// COIL 제품이적중계장
//	
//	public final static String STACK_COL_USAGE_CD_FS 	= "FS";			// CTS FROM SADDLE
//	public final static String STACK_COL_USAGE_CD_TS 	= "TS";			// CTS TO SADDLE
//	public final static String STACK_COL_USAGE_CD_FI 	= "FI";			// COIL HFLTAKEIN위치
//	public final static String STACK_COL_USAGE_CD_QE 	= "QE";			// COIL EQL보급위치
//	public final static String STACK_COL_USAGE_CD_KI 	= "KI";			// COIL SPMTAKEIN위치
//	public final static String STACK_COL_USAGE_CD_QD 	= "QD";			// COIL EQL추출위치
//	public final static String STACK_COL_USAGE_CD_PX 	= "PX";			// 팔레트정지위치
//	
//	
//	public final static String STACK_COL_USAGE_CD_BK 	= "BK";			// SLAB 보온카바위치
//	public final static String STACK_COL_USAGE_CD_CT 	= "CT";			// SLAB CTC
//	public final static String STACK_COL_USAGE_CD_HD 	= "HD";			// SLAB Holding Bed
//	public final static String STACK_COL_USAGE_CD_RT 	= "RT";			// SLAB Roller Table
//	public final static String STACK_COL_USAGE_CD_WB 	= "WB";			// SLAB Walking Beam
//	public final static String STACK_COL_USAGE_CD_SE 	= "SE";			// SLAB Scafing 입측
//	public final static String STACK_COL_USAGE_CD_SD 	= "SD";			// SLAB Scafing 출측
//	public final static String STACK_COL_USAGE_CD_31 	= "31";			// SLAB 옥내이송적치장
//	public final static String STACK_COL_USAGE_CD_32 	= "32";			// SLAB 정정작업대기장
//	public final static String STACK_COL_USAGE_CD_33 	= "33";			// SLAB 압연지시대기장
//	public final static String STACK_COL_USAGE_CD_34 	= "34";			// SLAB 동간보급준비장
//	public final static String STACK_COL_USAGE_CD_35 	= "35";			// SLAB 압연보급대기장
//	public final static String STACK_COL_USAGE_CD_36 	= "36";			// SLAB Take Out적치장
//	public final static String STACK_COL_USAGE_CD_37 	= "37";			// SLAB WCR재 적치장
//	public final static String STACK_COL_USAGE_CD_41 	= "41";			// SLAB 부두입고적치장
//	public final static String STACK_COL_USAGE_CD_42 	= "42";			// SLAB 부두이송대기장
//	public final static String STACK_COL_USAGE_CD_43 	= "43";			// SLAB Hand Scarfing 장
//	public final static String STACK_COL_USAGE_CD_44 	= "44";			// SLAB Slab 절단장
//
//	public final static String STACK_COL_USAGE_CD_SP    = "SP";			// COIL SCRAP처리장 코드
//	public final static String STACK_COL_USAGE_CD_K2 	= "K2";			// COIL SPM보급위치 ECC2
//
//	/* SLAB NEW 스케쥴종류 */
//	public final static String NEW_SCH_WORK_KIND_SYST = "SYST";		//Slab 부두야드 입고
//	public final static String NEW_SCH_WORK_KIND_SVML = "SVML";		//Slab 이송상차
//	public final static String NEW_SCH_WORK_KIND_SVMU = "SVMU";		//Slab 이송하차
//	public final static String NEW_SCH_WORK_KIND_SYMM = "SYMM";		//Slab 동내이적
//	public final static String NEW_SCH_WORK_KIND_SYM2 = "SYM2";		//Slab 동내이적
//	public final static String NEW_SCH_WORK_KIND_SYM3 = "SYM3";		//Slab 동내이적
//	public final static String NEW_SCH_WORK_KIND_STSL = "STSL";		//Slab 동간보급상차
//	public final static String NEW_SCH_WORK_KIND_STML = "STML";		//Slab 동간이적상차
//	public final static String NEW_SCH_WORK_KIND_STM2 = "STM2";		//Slab 동간이적상차
//	public final static String NEW_SCH_WORK_KIND_STMU = "STMU";		//Slab 대차하차(1)
//	public final static String NEW_SCH_WORK_KIND_STM4 = "STM4";		//Slab 대차하차(2)
//	public final static String NEW_SCH_WORK_KIND_SSLI = "SSLI";		//Slab Scarfing 보급
//	public final static String NEW_SCH_WORK_KIND_SSLO = "SSLO";		//Slab Scarfing 추출
//	public final static String NEW_SCH_WORK_KIND_SSTO = "SSTO";		//Slab Scarfing Take Out
//	public final static String NEW_SCH_WORK_KIND_SWLI = "SWLI";		//Slab W/B 보급
//	public final static String NEW_SCH_WORK_KIND_SCLI = "SCLI";		//Slab CTC 보급
//	public final static String NEW_SCH_WORK_KIND_SCL2 = "SCL2";		//Slab STE 비상보급
//	public final static String NEW_SCH_WORK_KIND_SWTO = "SWTO";		//Slab W/B Take Out 
//	public final static String NEW_SCH_WORK_KIND_SHLO = "SHLO";		//Slab H/B Line Off
//	public final static String NEW_SCH_WORK_KIND_SRLO = "SRLO";		//Slab ROT Line Off
//	public final static String NEW_SCH_WORK_KIND_SRLI = "SRLI";		//Slab ROT Line In
//	public final static String NEW_SCH_WORK_KIND_SHSI = "SHSI";		//Slab Hand Scarfing 보급
//	public final static String NEW_SCH_WORK_KIND_SHSO = "SHSO";		//Slab Hand Scarfing 추출  
//	public final static String NEW_SCH_WORK_KIND_SRPI = "SRPI";		//Slab 시편재 보급
//	public final static String NEW_SCH_WORK_KIND_SRPO = "SRPO";		//Slab 시편재 추출  
//	public final static String NEW_SCH_WORK_KIND_SPML = "SPML";		//Slab 팔레트이적상차
//	public final static String NEW_SCH_WORK_KIND_SPMU = "SPMU";		//Slab 팔레트이적하차
//	public final static String NEW_SCH_WORK_KIND_SVFL = "SVFL";		//Slab 제품출하상차
//	
//
//	
//	/* 공통 현재 진도코드 */
//	public final static String STOCK_STAT_D = "D";			//정정작업지시
//	public final static String STOCK_STAT_E = "E";			//압연작업지시
//	
//	public final static String CURR_PROG_CD_SLAB_0 	= "0";	//Slab구입등록(품질)				11  
//	public final static String CURR_PROG_CD_SLAB_1 	= "1";	//Slab구입확정(품질)				12  
//	public final static String CURR_PROG_CD_SLAB_3 	= "3";	//생산종료						3S  
//	public final static String CURR_PROG_CD_SLAB_A 	= "A";	//Slab정정작업대기/수입검사대기	    AS  
//	public final static String CURR_PROG_CD_SLAB_B 	= "B";	//지시대기/이송지시대기		  	    BS  
//	public final static String CURR_PROG_CD_SLAB_C 	= "C";	//작업대기/이송대기				CS  
//	public final static String CURR_PROG_CD_SLAB_D 	= "D";	//이송지시대기/정정작업대기		    DS  
//	public final static String CURR_PROG_CD_SLAB_E 	= "E";	//이송작업대기/압연지시대기		    ES  
//	public final static String CURR_PROG_CD_SLAB_F 	= "F";	//판정보류/압연작업대기		     	FS  
//	public final static String CURR_PROG_CD_SLAB_K 	= "K";	//출하지시대기/출하작업지시  		    KS  
//	public final static String CURR_PROG_CD_SLAB_L 	= "L";	//운송대기/출하작업대기			    LS  
//	public final static String CURR_PROG_CD_SLAB_M	= "M";	//출하완료/출하완료				MS  
//	public final static String CURR_PROG_CD_SLAB_Y 	= "Y";	//재공충당대기/판정보류		      	YS  
//	public final static String CURR_PROG_CD_SLAB_Z 	= "Z";	//제품충당대기/충당대기		  	    ZS  
//	public final static String CURR_PROG_CD_SLAB_N 	= "N";	//운송지시대기[신규추가]			NS  
//	public final static String CURR_PROG_CD_SLAB_G 	= "G";	//종합판정대기[신규추가]			GS  
//	public final static String CURR_PROG_CD_SLAB_H 	= "H";	//입고대기[신규추가]				HS  
//	public final static String CURR_PROG_CD_SLAB_J 	= "J";	//반납대기[신규추가]				JS  
//	
//	
//	
//	public final static String SCRAP_CAUSE_GP_I 	= "I";			// I:입고
//	public final static String SCRAP_CAUSE_GP_B 	= "B";		// B:보류
//	public final static String SCRAP_CAUSE_GP_S 	= "S";		// S:Scrap
//	public final static String SCRAP_CAUSE_GP_C 	= "C";		// C:차공정
//	public final static String SCRAP_CAUSE_GP_J 	= "J";		// J:재작업
//		     					
//	/** SCHEDULE 작업 위치 결정 방법 **/
	public final static String SCH_WORK_LOC_DECISION_METHOD_S 	= "S"; 	// SCHEDULE 에서 검색
	public final static String SCH_WORK_LOC_DECISION_METHOD_O 	= "O"; 	// OPERATOR 지정위치

    public final static String YD_GP_A = "A"; //A열연 COIL 야드
    public final static String YD_GP_0 = "0"; //A열연 SLAB 야드
    public final static String YD_GP_1 = "1"; //A열연 COIL 야드
    public final static String YD_GP_4 = "4"; //부드야드

	//로그레벨 상수 정의
	public static final  int ERROR   = 1;
	public static final  int WARNING = 2;
	public static final  int INFO    = 3;
	public static final  int DEBUG   = 4;
}
