/*
 * @(#) 야드에서 사용되는 공통 상수를 정의하는 클래스
 *
 * @version			V1.00
 * @author			김현우
 * @date			2012/11/14
 *
 * @description		야드에서 사용되는 공통 상수를 정의하는 클래스
 * --------------------------------------------------------------------------------------
 * Ver.    수정일자           요청자       수정자         내용
 * =====  ===========  ======  ======  ==================================================
 * V1.00  2012/11/14   김현우      김현우       최초작성
 */

package com.inisteel.cim.yd.jplateyd.util;

public class JPlateYdConst {

	//전문버퍼의 전문항목이름
	public static final String TC_BODY									= "ZZ_TC_BODY";

	//야드 모니터링 채널
	//public static final String YD_MONITORING_CHANNEL_01				= "yd_monitor01";

	//로그레벨 상수 정의
	public static final  int ERROR   = 1;
	public static final  int WARNING = 2;
	public static final  int INFO    = 3;
	public static final  int DEBUG   = 4;

	// 크레인스케쥴 MAX 작성 건수
	public static final  int MAX_CRN_SCH_CNT							= 6;

	// 배경색 선언
	public static final String GRD_END_BG_COLOR							= "180|180|180";		//종료된 재료의 그리드 배경색  #B4B4B4 - 180|180|180
	public static final String GRD_OCPY_BG_COLOR						= "240|240|240";		//점유베드 배경색  			#F0F0F0 - 240|240|240

	// 스판별 모니터링의 베드 색상 정의
	public static final String BED_END_BG_COLOR							= "#FC7C90";			//종료된 재료의 이미지 배경색  #FC7C90 - 252|124|144
	public static final String BED_NOR_BG_COLOR							= "#BFC8BE";			//진행중 재료의 이미지 배경색  #BFC8BE - 191|200|190

	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	* 야드업무 내부적으로 사용되는 함수리턴코드 정의
	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/
	//공통 문자리턴값
	public static final String RETN_CD_SUCCESS							= "SUCCESS";			//성공메세지코드
	public static final String RETN_CD_FAILURE							= "FAILURE";			//실패메세지코드
	public static final String RETN_CD_NOTEXIST							= "NOTEXIST";			//값이 존재하지 않음
	public static final String RETN_CD_EXIST							= "EXIST";				//값이 존재함
	public static final String RETN_CD_DUPLICATE						= "DUPLICATE";			//값이 중복됨
	public static final String RETN_CD_TC_ERROR							= "TC_ERROR";			//전문에러
	public static final String RETN_CD_NO_PARAM							= "NOPARAM";			//파라미터가 존재하지 않음
	public static final String RETN_CD_EQ_STATUS						= "EQUAL_STATUS";		//상태값이 같은 경우
	public static final String RETN_CD_NOTEQ_STATUS						= "NOTEQUAL_STATUS";	//상태값이 다른 경우

	//크레인관련리턴값
	public static final String RETN_CRN_SCH_PROH						= "SCH_PROH";			//야드스케쥴금지
	public static final String RETN_CRN_NO_WRK							= "NO_WORK";			//작업예약이 존재하지 않음
	public static final String RETN_CRN_EXIST_WRK						= "EXIST_WORK";			//작업예약이 존재
	public static final String RETN_CRN_NO_SCH							= "NO_SCH";				//크레인스케줄이 존재하지 않음
	public static final String RETN_CRN_EXIST_SCH						= "EXIST_SCH";			//크레인스케줄이 존재
	public static final String RETN_CRN_STATUS_ERR						= "STATUS_ERR";			//크레인의 작업상태가 올바르지 않음
	public static final String RETN_CRN_NO_ALT_CRN						= "NO_ALT_CRN";			//대체크레인이 존재하지 않음

	//운송설비관련리턴값
	public static final String RETN_TRN_COL_ACT							= "COL_ACT";			//정지위치 활성 상태
	public static final String RETN_TRN_COL_INACT						= "COL_INACT";			//정지위치 비활성 상태

	//정수리턴값
	public static final Integer RETN_INT_SUCCESS						= new Integer(1);		//성공메세지코드
	public static final Integer RETN_INT_FAILURE						= new Integer(-10000);	//실패메세지코드
	public static final Integer RETN_INT_TC_ERROR						= new Integer(-10001);	//전문에러

	//TO위치결정 시 사용되는 반환값 정의
	public static final String RETN_SH_OVER								= "SH_OVER";			//매수초과
	public static final String RETN_WT_OVER								= "WT_OVER";			//중량초과
	public static final String RETN_H_OVER								= "H_OVER";				//높이초과
	public static final String RETN_BED_INACT							= "BED_INACT";			//적치베드가 활성상태가 아님
	public static final String RETN_BED_WHIO_NOT_IN						= "WHIO_NOT_IN";		//입고불가능상태
	public static final String RETN_BED_UN_WAIT							= "UN_WAIT";			//권상대기
	public static final String RETN_SAME_SCH_CD							= "SAME_SCH_CD";		//스케줄코드가 같음
	public static final String RETN_SCH_EARLY_PRIOR						= "EARLY_PRIOR";		//우선순위가 빠름
	public static final String RETN_SCH_LATE_PRIOR						= "LATE_PRIOR";			//우선순위가 늦음
	public static final String RETN_NOT_EXIST_SCH_CD					= "NOTEXIST_SCH_CD";	//스케줄코드가 존재하지 않음
	public static final String RETN_NOT_EXIST_BED						= "NOTEXIST_BED";		//적치가능베드가 존재하지 않음
	public static final String RETN_BIG_NOT_EXIST_BED					= "BIG_NOTEXIST_BED";	//대형고객사 적치가능베드가 존재하지 않음
	public static final String RETN_NOT_EXIST_BED_ABAYIN				= "NOT_EXIST_BED_ABAYIN";	//A동 입고시 입고GUIDE 위치 적치불가
	/*+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/

	//야드구분
	public static final String YD_GP_P_PLATE_YARD						= "P";					//1후판정정 야드
	public static final String YD_GP_F_PLATE_YARD						= "F";					//2후판정정 야드

	//야드설비상태
	public static final String YD_EQP_NOTEXIST							= "EQP_NOTEXIST";		//설비가 존재하지 않음
	public static final String YD_EQP_STAT_NORM							= "N";					//정상
	public static final String YD_EQP_STAT_BREAK						= "B";					//고장

	//야드설비작업Mode
	public static final String YD_EQP_WRK_MODE_ON_LINE					= "1";					//ON LINE
	public static final String YD_EQP_WRK_MODE_OFF_LINE					= "2";					//OFF LINE

	//크레인의 설비작업상태
	public static final String YD_EQP_STAT_IDLE 						= "W";					//크레인이 IDLE인 상태 - 스케줄수행대기
	public static final String YD_EQP_STAT_OW 							= "0";					//명령선택대기
	public static final String YD_EQP_STAT_UP_WO 						= "1";					//권상지시
	public static final String YD_EQP_STAT_UP_CMPL 						= "2";					//권상완료
	public static final String YD_EQP_STAT_DN_WO 						= "3";					//권하지시
	public static final String YD_EQP_STAT_DN_CMPL 						= "4";					//권하완료

	/*+++++++++++++++++++++
	 * 차량관련 상수 정의 시작
	 +++++++++++++++++++++*/
	//포인트개폐구분
	public static final String PNT_UNIT_CL_GP_CLOSE						= "C";					//폐
	public static final String PNT_UNIT_CL_GP_OPEN						= "O";					//개

	//차량에 대한 설비 기본값
	public static final String YD_TS_CAR_EQP_ID							= "XXPT01";				//구내운송차량에 대한 기본 설비ID
	public static final String YD_DM_CAR_EQP_ID							= "XXPT02";				//출하차량에 대한 기본 설비ID


	//차량사용구분  출하,구내 운송  ( L : 구내운송 , G : 출하차량)
	public static final String YD_CAR_USE_GP_TS						    = "L";					// 구내운송
	public static final String YD_CAR_USE_GP_DM						    = "G";					// 출하차량

	//야드차량진행상태
	public static final String YD_CARLD_LEV								= "1";					//상차출발
	public static final String YD_CARLD_ARR								= "2";					//상차도착
	public static final String YD_CARLD_CHK								= "3";					//상차검수
	public static final String YD_CARLD_ST								= "4";					//상차개시
	public static final String YD_CARLD_CMPL							= "5";					//상차완료
	public static final String YD_CARUD_LEV								= "A";					//하차출발
	public static final String YD_CARUD_ARR								= "B";					//하차도착
	public static final String YD_CARUD_CHK								= "C";					//하차검수
	public static final String YD_CARUD_ST								= "D";					//하차개시
	public static final String YD_CARUD_CMPL							= "E";					//하차완료

	//야드차량생성시 사용되는 입동지시순번 기본값
	public static final String YD_BAYIN_WO_SEQ_DEFAULT					= "9";					//입동지시순번 기본값

	//야드배차순서 기본값
	public static final String YD_CARASGN_SEQ_AUTO_DEFAULT				= "9";					//자동이송LOT편성일 경우 기본값
	public static final String YD_CARASGN_SEQ_MAN_DEFAULT				= "99";					//수동이송LOT편성일 경우 기본값

	//운송작업영공구분
	public static final String TRN_WRK_VOID								= "E";					//공차
	public static final String TRN_WRK_FULL								= "F";					//영차

	//YD_EQP_WRK_STAT - 야드설비작업상태
	public static final String YD_EQP_WRK_STAT_LD						= "L";					//상차
	public static final String YD_EQP_WRK_STAT_UD						= "U";					//하차

	//차량포인트지시 시 포인트가 없을 경우 사용되는 포인트코드
	public static final String YD_PNT_CD_NULL							= "0000";

	//대기장 포인트코드
	public static final String YD_WAIT_PNT_CD							= "1Z99";				//대기장포인트코드

	public static final String YD_REPAIR_WLOC_CD						= "DMY1P";				//중장비수리고

	public static final String WLOC_CD_P_PLATE_YARD						= "P0000";				//1후판정정야드
	public static final String WLOC_CD_F_PLATE_YARD						= "F0000";				//2후판정정야드

	/*+++++++++++++++ 차량관련 상수 정의 끝 +++++++++++++++*/

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

	//야드적치BED입출고상태
	public static final String YD_STK_BED_WHIO_ENABLE					= "E";				//입출고가능
	public static final String YD_STK_BED_WHIO_FULL						= "F";				//완산BED
	public static final String YD_STK_BED_WHIO_X						= "X";				//입출고금지
	public static final String YD_STK_BED_WHIO_VIRTUAL					= "G";				//가적BED


	//베드의 기본 야드적치Bed중량Max --> 구내운송이나 출하차량이 출발 시 차량정지위치의 BED를 비활성화 시 같이 설정하는 베드중량 MAX
	public static final String YD_STK_BED_WT_MAX_DEFAULT				= "300000";


	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * 크레인작업실적응답
	 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	//U:권상실적, D:권하실적, E: 비상조업실적, F: 강제권하,   R:고장, M:모드변경, J : 지시요구
	public static final String CRN_WRK_RE_LD_WR							= "U";				//권상실적
	public static final String CRN_WRK_RE_DN_WR							= "D";				//권하실적
	public static final String CRN_WRK_RE_EMG_PTOP						= "E";				//비상조업실적
	public static final String CRN_WRK_RE_FRCE_DN						= "F";				//강제권하
	public static final String CRN_WRK_RE_TRBL							= "R";				//고장
	public static final String CRN_WRK_RE_MD_MOD						= "M";				//모드변경
	public static final String CRN_WRK_RE_WO_DMD						= "J";				//지시요구

	// 크레인작업실적응답 코드
	public static final String CRN_WRK_RE_CD_NORMAL_HD					= "0000";			//정상처리
	public static final String CRN_WRK_RE_CD_NO_WRK						= "9999";			//크레인작업지시가 없을 경우
	public static final String CRN_WRK_RE_CD_ERROR						= "8888";			//오류
	//.....에러코드를 계속 정의 필요.....
	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */

	//야드재료품목
	//public static final String YD_MTL_ITEM_

	//재료진도코드
	public static final String PROG_CD_SLAB_CORRCETION_WRK_WAIT			= "A";				//SLAB정정작업대기
	public static final String PROG_CD_WO_WAIT							= "B";				//지시대기
	public static final String PROG_CD_WRK_WAIT							= "C";				//작업대기
	public static final String PROG_CD_FTMV_WO_WAIT						= "D";				//이송지시대기
	public static final String PROG_CD_FTMV_WRK_WAIT					= "E";				//이송작업대기
	public static final String PROG_CD_STMP_HOLD						= "F";				//판정보류
	public static final String PROG_CD_OVALL_STMP_WAIT					= "G";				//종합판정대기
	public static final String PROG_CD_RCPT_WAIT						= "H";				//입고대기
	public static final String PROG_CD_RETN_WAIT						= "J";				//반납대기
	public static final String PROG_CD_DIST_WO_WAIT						= "K";				//출하지시대기
	public static final String PROG_CD_TRN_WAIT							= "L";				//운송대기
	public static final String PROG_CD_DIST_CMPL						= "M";				//출하완료
	public static final String PROG_CD_TRN_WO_WAIT						= "N";				//운송지시대기
	public static final String PROG_CD_DELIVERY_CMPL					= "P";				//인도완료
	public static final String PROG_CD_FTMV_TKOV_WAIT					= "Q";				//이송인수대기
	public static final String PROG_CD_AUCT_TG_PKUP						= "X";				//경매대상선정
	public static final String PROG_CD_INLINE_MATCH_WAIT				= "Y";				//재공충당대기
	public static final String PROG_CD_GDS_MATCH_WAIT					= "Z";				//제품충당대기
	public static final String PROG_CD_SLAB_BUY_REG						= "0";				//SLAB구입등록(품질)
	public static final String PROG_CD_SLAB_BUY_CMMT					= "1";				//SLAB구입확정(품질)

	//대차
	public static final String YD_TCAR_MOVE_GP_LEAVE					= "S";				//출발
	public static final String YD_TCAR_MOVE_GP_ARRIVE					= "E";				//도착
	public static final String YD_TCAR_MOVE_GP_MOVE						= "M";				//이동

	//크레인 작업허용오차
	public static final int PLATE_CRANE_GAP_X							= 10000;			//주행 오차값은 10M
	public static final int PLATE_CRANE_GAP_Y							= 1500;				//횡행 오차값은 1.5M
	public static final int PLATE_CRANE_GAP_Z  							= 50;				//주행 오차값은 50mm

	// 크레인 작업허용오차
	public static final int PLATE_CRANE_PT_GAP_X						= 30000;			//차상	위치일 경우의 허용오차		30M
	public static final int PLATE_CRANE_PT_GAP_Y						= 30000;			//차상	위치일 경우의 허용오차		30M
	public static final int PLATE_CRANE_BS_GAP_X						= 30000;			//보수장	위치일 경우의 크레인허용오차 	30M
	public static final int PLATE_CRANE_BS_GAP_Y						= 10000;			//보수장	위치일 경우의 크레인허용오차 	10M
	public static final int PLATE_CRANE_CN_GAP_X						= 30000;			//가스장	위치일 경우의 크레인허용오차 	30M
	public static final int PLATE_CRANE_CN_GAP_Y						= 3000;				//가스장	위치일 경우의 크레인허용오차 	3M
	public static final int PLATE_CRANE_TC_GAP_X						= 11000;			//대차	위치일 경우의 크레인허용오차 	11M
	public static final int PLATE_CRANE_TC_GAP_Y						= 2500;				//대차	위치일 경우의 크레인허용오차 	2.5M
	public static final int PLATE_CRANE_TD_GAP_X						= 20000;			//TOD	위치일 경우의 크레인허용오차 	20M
	public static final int PLATE_CRANE_TD_GAP_Y						= 2000;				//TOD	위치일 경우의 크레인허용오차 	2M
	public static final int PLATE_CRANE_RT_GAP_X						= 10000;			//RT	위치일 경우의 허용오차          	10M
	public static final int PLATE_CRANE_RT_GAP_Y						= 1500;				//RT	위치일 경우의 허용오차		1.5M
	public static final int PLATE_CRANE_CB_GAP_X						= 30000;			//냉각대	위치일 경우의 크레인허용오차 	30M
	public static final int PLATE_CRANE_CB_GAP_Y						= 10000;			//냉각대	위치일 경우의 크레인허용오차 	10M
	public static final int PLATE_CRANE_HT_GAP_Y						= 5000;				//1후판 강력교정동 대차크레인허용오차 	5M
	

	//후판제품 길이구분
	public static final String YD_MTL_LEN_EXTRA							= "X";
	public static final String YD_MTL_LEN_LONG							= "L";
	public static final String YD_MTL_LEN_MIDDLE						= "M";
	public static final String YD_MTL_LEN_SHORT							= "S";
	public static final String YD_MTL_LEN_EXTRA_SHORT					= "U";		//초단척

	//후판제품 폭구분
	public static final String YD_MTL_WIDTH_WIDE						= "L";
	public static final String YD_MTL_WIDTH_MIDDLE						= "M";
	public static final String YD_MTL_WIDTH_SMALL						= "S";

	//후판 제품야드에서 사용되는 광폭, 중폭, 소폭 값정의
	public static final  int STKBED_W_GP_L   							= 4800;		// 광폭
	public static final  int STKBED_W_GP_M   							= 3450;		// 중폭
	public static final  int STKBED_W_GP_S   							= 2100;		// 소폭

	public static final String ORDER_BY_ASC								= "ASC";				//오름차순 정렬
	public static final String ORDER_BY_DESC							= "DESC";				//내림차순 정렬

	//--------------------------------------------------------------------------
	//	Book-In/Out 구분 코드 정의
	//--------------------------------------------------------------------------
	public static final String BOOK_IN_GP 								= "U";					//BOOK-IN  구분 FART0?UM
	public static final String BOOK_OUT_GP								= "L";					//BOOK-OUT 구분 FART0?LM
	public static final String PP_BOOK_IN_GP 							= "1";					//BOOK-IN  구분 (후판조업L3전문)
	public static final String PP_BOOK_OUT_GP							= "2";					//BOOK-OUT 구분 (후판조업L3전문)

	//-------------------------------------------------------------------------------------------------
	// 			TC_CODE 상수 정의 시작
	//-------------------------------------------------------------------------------------------------
	/*
	 * 야드내부 TC CODE
	 */
	public final static String YDYDJ701									= "YDYDJ701";		// 전문전송버퍼 BUFFER
	public final static String YDYDJ702									= "YDYDJ702";		// 로그메세지처리

	public final static String JMS_TC_CRN_SCH							= "YDYDJ750";		// 크레인 스케줄
	public final static String JMS_TC_RE_SCH							= "YDYDJ751";		// 크레인 리스케줄
	public final static String JMS_TC_TO_LOC							= "YDYDJ752";		// 저장위치결정MAIN호출
	public final static String JMS_TC_TCAR								= "YDYDJ753";		// 대차 스케줄
	public final static String JMS_TC_CRN_WRK							= "YDYDJ754";		// 크레인 작업지시
	public final static String JMS_TC_WRK_REQ							= "YDYDJ755";		// 크레인 작업지시요구

	public final static String JMS_TC_PCAR_WRK_END						= "YDYDJ770";		// 이송상차완료실적 수신

	//-------------------------------------------------------------------------------------------------

	// 재료외형구분
	public static final String STL_APPEAR_PREPARE                       = "A";              // 예정 재료
	public static final String STL_APPEAR_MSLAB                         = "B";              // 주편
	public static final String STL_APPEAR_SLAB                          = "C";              // SLAB
	public static final String STL_APPEAR_SLABSIZING                    = "D";              // SLAB(SIZING재)
	public static final String STL_APPEAR_HOTROLLCOIL                   = "E";              // 열연코일
	public static final String STL_APPEAR_PARENTPLATE                   = "F";              // 날판
	public static final String STL_APPEAR_PLATE                         = "G";              // PLATE
	public static final String STL_APPEAR_PRODUCT                       = "Y";              // 제품

	// 주문여재구분
	public static final String ORD_YEOJAE_GP_ORD                        = "1";              // 주문재
	public static final String ORD_YEOJAE_GP_YEOJAE                     = "2";              // 여재

	// 적치된 상태를 구분해서 재료를 조회 시 사용되는 구분자
	public static final String MTL_STAT_C_U_D							= "CUD";

	//--------------------------------------------------------------------------------------------------------
	//	TO위치 순위 정의 상수
	//--------------------------------------------------------------------------------------------------------
	public static final int TO_LOC_PRIOR_USER							= 1;

	//--------------------------------------------------------------------------------------------------------
	//전문버퍼전송 시 사용되는 실제 TC CODE 항목
	public static final String BUFFER_TC_CD								= "BUFFER_TC_CD";

	public static final int TO_LOC_PRIOR_PLATE_EMPTY_BED				= 3;

	//TC CODE ID 항목이름
	public static final String JMS_TC_CD								= "JMS_TC_CD";
	public static final String MSG_ID									= "MSG_ID";
	public static final String TC_CODE									= "TC_CODE";

	public static final int TO_LOC_PRIOR_PLATE_SAME_PILING_CD			= 2;
	public static final int TO_LOC_PRIOR_STEP							= 10000;

	//BED 적치가능 유무에 대한 에러코드
	public static final int YD_BED_ERR_CD_SH_OVER						= 1;				//매수초과(MAX단 초과)
	public static final int YD_BED_ERR_CD_WT_OVER						= 3;				//중량초과
	public static final int YD_BED_ERR_CD_H_OVER						= 5;				//높이초과
	public static final int YD_BED_ERR_CD_L_OVER						= 7;				//길이초과

	public static final int YD_BED_ERR_CD_SH_WT_OVER					= 4;				//매수/중량초과
	public static final int YD_BED_ERR_CD_SH_H_OVER						= 6;				//매수/높이초과
	public static final int YD_BED_ERR_CD_WT_H_OVER						= 8;				//중량/높이초과
	public static final int YD_BED_ERR_CD_SH_WT_H_OVER					= 9;				//매수/중량/높이초과

	public static final int YD_BED_STACKABLE							= 10000;			//적치가능 코드

	// 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
	// putLogMsg Method Parameter 설정
	public static final String YD_EVT_CRANE                  	= "C";
	public static final String YD_EVT_EQP                    	= "Q";
	public static final String YD_EVT_ERROR                  	= "E";
	public static final String YD_EVT_WARNING                	= "W";
	public static final String YD_EVT_INFO                   	= "I";
	public static final String YD_EVT_ETC                    	= "Z";
	public static final String YD_EVT_FUN                    	= "F";

	// 야드프로그램유형 (W:화면, S:스케줄, I:인터페이스)
	public static final String YD_PGMGP_WEB                    	= "W";
	public static final String YD_PGMGP_SCH                    	= "S";
	public static final String YD_PGMGP_INTERFACE              	= "I";


	// 야드 수불 구분
	public static final String YD_GNT_GP_RCPT                  = "L";					// 입고 : BOOK-OUT
	public static final String YD_GNT_GP_MVSTK                 = "M";					// 이적
	public static final String YD_GNT_GP_ISSUE           	   = "U";					// 출고 : BOOK-IN

	// 주작업크레인과 대체작업크레인의 구분자
	public static final String YD_WRK_CRN						= "WRK_CRN";			// 주작업크레인
	public static final String YD_ALT_CRN						= "ALT_CRN";			// 대체작업크레인

	// 야드설비구분
	public static final String YD_EQP_GP_TCAR                  	= "TC"; 				// 대차
	public static final String YD_EQP_GP_PALLET                	= "PT"; 				// Pallet
	public static final String YD_EQP_GP_TRAILER               	= "TR"; 				// 트레일러
	public static final String YD_EQP_GP_CRANE                 	= "CR"; 				// 크레인
	public static final String YD_EQP_GP_RT                 	= "RT"; 				// Roller Table
	public static final String YD_EQP_GP_CNC                 	= "CN"; 				// CNC
	public static final String YD_EQP_GP_TOD                 	= "TD"; 				// TOD

	//YD_EQP_WRK_STAT - 스케줄 상하차 구분
	public static final String YD_CRN_SCH_CD_LD					= "L";					// 하차
	public static final String YD_CRN_SCH_CD_UD					= "U";					// 상차

	//후판정정야드 운전실구분
	public static final String YD_JPLATE_LOC_GP_US				= "UST";
	public static final String YD_JPLATE_LOC_GP_CS				= "C/S";
	public static final String YD_JPLATE_LOC_GP_DSS				= "DSS";
	public static final String YD_JPLATE_LOC_GP_DS				= "D/S";
	public static final String YD_JPLATE_LOC_GP_HMD				= "열간교정운전실";
	public static final String YD_JPLATE_LOC_GP_CMD				= "냉간교정운전실";
	public static final String YD_JPLATE_LOC_GP_GT				= "정정분기";
	public static final String YD_JPLATE_LOC_GP_WT				= "열처리";
	public static final String YD_JPLATE_LOC_GP_SB				= "Shot blast";
	public static final String YD_JPLATE_LOC_GP_CR				= "검사실";

	//후판정정야드 스판별 야드구분
	public static final String YD_JPLATE_EQP_GP_01				= "극후물";
	public static final String YD_JPLATE_EQP_GP_02				= "냉각대출측";
	public static final String YD_JPLATE_EQP_GP_03				= "#1GAS";
	public static final String YD_JPLATE_EQP_GP_04				= "#1전단";
	public static final String YD_JPLATE_EQP_GP_05				= "#2전단";
	public static final String YD_JPLATE_EQP_GP_06				= "#2GAS";
	public static final String YD_JPLATE_EQP_GP_07				= "극후물GAS";
	public static final String YD_JPLATE_EQP_GP_08				= "전단GAS";
	public static final String YD_JPLATE_EQP_GP_09				= "냉간교정야드";
	public static final String YD_JPLATE_EQP_GP_10				= "보수장";
	public static final String YD_JPLATE_EQP_GP_11				= "보수장GAS";
	public static final String YD_JPLATE_EQP_GP_12				= "열처리";
	public static final String YD_JPLATE_EQP_GP_13				= "ShotBlast";
	public static final String YD_JPLATE_EQP_GP_14				= "열처리GAS";
	public static final String YD_JPLATE_EQP_GP_15				= "제품창고#1GAS";
	public static final String YD_JPLATE_EQP_GP_16				= "제품창고#2GAS";
	public static final String YD_JPLATE_EQP_GP_17				= "제품창고#3GAS";
	public static final String YD_JPLATE_EQP_GP_18				= "제품창고#4GAS";

	// 주작업이적구분 - 1:이적 , 2:RT BOOK-IN , 3:GAS장보급 , 4:보수장보급 , 5:TOD보급 , 6:RT BOOK-OUT , 7:GAS장추출 , 8:보수장추출 , 9:TOD추출, A:이송상차, B:이송하차
	public static final String YD_MAIN_WRK_GP_MV				= "1";					// 이적
	public static final String YD_MAIN_WRK_GP_RT_IN				= "2";					// RT	Book-in
	public static final String YD_MAIN_WRK_GP_GAS_IN			= "3";					// GAS장	Book-In (보급)
	public static final String YD_MAIN_WRK_GP_BS_IN				= "4";					// 보수장	Book-In (보급)
	public static final String YD_MAIN_WRK_GP_TOD_IN			= "5";					// TOD  Book-In (보급)
	public static final String YD_MAIN_WRK_GP_PT_IN				= "A";					// 이송차량 상차

	public static final String YD_MAIN_WRK_GP_RT_OUT			= "6";					// RT	Book-Out
	public static final String YD_MAIN_WRK_GP_GAS_OUT			= "7";					// GAS장	Book-Out(추출)
	public static final String YD_MAIN_WRK_GP_BS_OUT			= "8";					// 보수장	Book-Out(추출)
	public static final String YD_MAIN_WRK_GP_TOD_OUT			= "9";					// TOD  Book-Out(추출)
	public static final String YD_MAIN_WRK_GP_PT_OUT			= "B";					// 이송차량 하차

	public static final String YD_MAIN_WRK_GP_BS_MOVE			= "M";					// 보수장 이적(저장위치수정)

	// 후판정정야드 스케쥴코드
	// BOOK-IN/OUT
	public static final String SCH_CD_JPLATE_BOOK_IN_A1			= "FART01UM";			//BOOK-IN  (A동:#2DS)
	public static final String SCH_CD_JPLATE_BOOK_IN_A2			= "FART02UM";			//BOOK-IN  (A동:#2검사)
	public static final String SCH_CD_JPLATE_BOOK_IN_B1			= "FBRT01UM";			//BOOK-IN  (B동:UST)
	public static final String SCH_CD_JPLATE_BOOK_IN_B2			= "FBRT02UM";			//BOOK-IN  (B동:C/S)
	public static final String SCH_CD_JPLATE_BOOK_IN_B3			= "FBRT03UM";			//BOOK-IN  (B동:DSS/#1DS)
	public static final String SCH_CD_JPLATE_BOOK_IN_B4			= "FBRT04UM";			//BOOK-IN  (B동:#1검사)
	public static final String SCH_CD_JPLATE_BOOK_IN_B5			= "FBRT05UM";			//BOOK-IN  (B동:대차)
	public static final String SCH_CD_JPLATE_BOOK_IN_C1			= "FCRT01UM";			//BOOK-IN  (C동)
	public static final String SCH_CD_JPLATE_BOOK_IN_D1			= "FDRT01UM";			//BOOK-IN  (D동)

	public static final String SCH_CD_JPLATE_BOOK_OUT_A1		= "FART01LM";			//BOOK-OUT (A동:#2DS)
	public static final String SCH_CD_JPLATE_BOOK_OUT_A2		= "FART02LM";			//BOOK-OUT (A동:#2검사)
	public static final String SCH_CD_JPLATE_BOOK_OUT_B1 		= "FBRT01LM";			//BOOK-OUT (B동:UST)
	public static final String SCH_CD_JPLATE_BOOK_OUT_B2 		= "FBRT02LM";			//BOOK-OUT (B동:C/S)
	public static final String SCH_CD_JPLATE_BOOK_OUT_B3 		= "FBRT03LM";			//BOOK-OUT (B동:DSS/#1DS)
	public static final String SCH_CD_JPLATE_BOOK_OUT_B4 		= "FBRT04LM";			//BOOK-OUT (B동:#1검사)
	public static final String SCH_CD_JPLATE_BOOK_OUT_B5 		= "FBRT05LM";			//BOOK-OUT (B동:대차)
	public static final String SCH_CD_JPLATE_BOOK_OUT_C1 		= "FCRT01LM";			//BOOK-OUT (C동)
	public static final String SCH_CD_JPLATE_BOOK_OUT_D1 		= "FDRT01LM";			//BOOK-OUT (D동)

	// GAS장 보급/추출
	public static final String SCH_CD_JPLATE_CNC_IN_12			= "FACN10UM";			//#1가스장(CNC#1,2) 보급
	public static final String SCH_CD_JPLATE_CNC_IN_34			= "FBCN30UM";			//#2가스장(CNC#3,4) 보급
	public static final String SCH_CD_JPLATE_CNC_IN_56 			= "FCCN50UM";			//#2전단   (CNC#5,6) 보급
	public static final String SCH_CD_JPLATE_CNC_IN_78 			= "FCCN70UM";			//#1보수장(CNC#7,8) 보급
	public static final String SCH_CD_JPLATE_CNC_OUT_12			= "FACN10LM";			//#1가스장(CNC#1,2) 추출
	public static final String SCH_CD_JPLATE_CNC_OUT_34			= "FBCN30LM";			//#2가스장(CNC#3,4) 추출
	public static final String SCH_CD_JPLATE_CNC_OUT_56 		= "FCCN50LM";			//#2전단   (CNC#5,6) 추출
	public static final String SCH_CD_JPLATE_CNC_OUT_78 		= "FCCN70LM";			//#1보수장(CNC#7,8) 추출

	// 보수장 보급/추출
	public static final String SCH_CD_JPLATE_BS_IN_A			= "FABS00UM";			//A동 #1보수장 보급
	public static final String SCH_CD_JPLATE_BS_IN_C			= "FCBS00UM";			//C동 #2보수장 보급
	public static final String SCH_CD_JPLATE_BS_OUT_A			= "FABS00LM";			//A동 #1보수장 추출
	public static final String SCH_CD_JPLATE_BS_OUT_C			= "FCBS00LM";			//C동 #2보수장 추출

	// TOD 보급/추출
	public static final String SCH_CD_JPLATE_TD_IN_C			= "FCTD00UM";			//TOD 보급
	public static final String SCH_CD_JPLATE_TD_OUT_C			= "FCTD00LM";			//TOD 추출

	// 차량이송
	public static final String SCH_CD_JPLATE_PT_IN_C			= "FCPT01UM"; 			//C동 이송상차
	public static final String SCH_CD_JPLATE_PT_OUT_C			= "FCPT01LM"; 			//C동 이송하차
	public static final String SCH_CD_JPLATE_PT_IN_D			= "FDPT01UM"; 			//D동 이송상차
	public static final String SCH_CD_JPLATE_PT_OUT_D			= "FDPT01LM"; 			//D동 이송하차

	// 이적
	public static final String SCH_CD_JPLATE_LOC_A01			= "FAYD01MM"; 			//A동 01SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_A02			= "FAYD02MM"; 			//A동 02SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_A03			= "FAYD03MM"; 			//A동 03SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_B01			= "FBYD01MM"; 			//B동 01SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_B02			= "FBYD02MM"; 			//B동 02SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_B03			= "FBYD03MM"; 			//B동 03SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_B04			= "FBYD04MM"; 			//B동 04SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_C01			= "FCYD01MM"; 			//C동 01SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_C02			= "FCYD02MM"; 			//C동 02SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_C03			= "FCYD03MM"; 			//C동 03SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_C04			= "FCYD04MM"; 			//C동 04SPAN 이적
	public static final String SCH_CD_JPLATE_LOC_C05			= "FCYD05MM"; 			//C동 05SPAN 이적

	// 대차 [동간이적]
	public static final String SCH_CD_JPLATE_TCAR_IN_B01		= "FBTC01UM";			//B동 #1 대차 상차
	public static final String SCH_CD_JPLATE_TCAR_IN_B02		= "FBTC02UM";			//B동 #2 대차 상차
	public static final String SCH_CD_JPLATE_TCAR_IN_C01		= "FCTC01UM";			//C동 #1 대차 상차
	public static final String SCH_CD_JPLATE_TCAR_IN_C02		= "FCTC02UM";			//C동 #2 대차 상차
	public static final String SCH_CD_JPLATE_TCAR_IN_C03		= "FCTC03UM";			//C동 #3 대차 상차
	public static final String SCH_CD_JPLATE_TCAR_IN_D03		= "FDTC03UM";			//C동 #3 대차 상차

	public static final String SCH_CD_JPLATE_TCAR_OUT_B01		= "FBTC01LM";			//B동 #1 대차 하차
	public static final String SCH_CD_JPLATE_TCAR_OUT_B02		= "FBTC02LM";			//B동 #2 대차 하차
	public static final String SCH_CD_JPLATE_TCAR_OUT_C01		= "FCTC01LM";			//C동 #1 대차 하차
	public static final String SCH_CD_JPLATE_TCAR_OUT_C02		= "FCTC02LM";			//C동 #2 대차 하차
	public static final String SCH_CD_JPLATE_TCAR_OUT_C03		= "FCTC03LM";			//C동 #3 대차 하차
	public static final String SCH_CD_JPLATE_TCAR_OUT_D03		= "FDTC03LM";			//D동 #3 대차 하차

	// 저장위치수정
	public static final String SCH_CD_JPLATE_LOC_MOD			= "FXYD01MM";			//저장위치 수정
	public static final String SCH_CD_JPLATE_LOC_DEL			= "FXYD02MM";			//저장위치 삭제
	public static final String SCH_CD_JPLATE_LOC_LIST			= "FXYD03MM";			//저장목록 수정
	public static final String SCH_CD_JPLATE_LOC_INIT			= "FXYD04MM";			//대차 초기화
	public static final String SCH_CD_JPLATE_SCRAP				= "FXCN01MM";			//CNC 스크랩처리
	public static final String SCH_CD_JPLATE_BS_MV				= "FXBS01MM";			//보수장내이적

	// 대차 적재중량
	public static final int TCAR_WT_CAPA						= 150000;				//적치가능 중량 150TON

	// 파일링구분 - 야드권상작업수행구분(YD_UP_WRK_ACT_GP) 항목사용 (P:파일링, H:횡행작업, N:일반작업, M:멀티작업, F:강제권상)
	public static final String YD_PILING_GP_P					= "P";					//파일링
	public static final String YD_PILING_GP_H					= "H";					//횡행작업
	public static final String YD_PILING_GP_N					= "N";					//일반
	public static final String YD_PILING_GP_M					= "M";					//멀티작업 (횡작업+파일링)
	public static final String YD_PILING_GP_F					= "F";					//강제권상, 강제권하

	
	/**********************************************************
	* 1후판정정추가 SJH16 
	**********************************************************/	
	//크레인 작업허용오차
	public static final int PPLATE_CRANE_GAP_X							= 10000;			//주행 오차값은 10M
	public static final int PPLATE_CRANE_GAP_Y							= 1500;				//횡행 오차값은 1.5M
	public static final int PPLATE_CRANE_GAP_Z  						= 50;				//주행 오차값은 50mm

	// 크레인 작업허용오차 
	public static final int PPLATE_CRANE_PT_GAP_X						= 30000;			//차상	위치일 경우의 허용오차		30M
	public static final int PPLATE_CRANE_PT_GAP_Y						= 30000;			//차상	위치일 경우의 허용오차		30M
	public static final int PPLATE_CRANE_BS_GAP_X						= 30000;			//보수장	위치일 경우의 크레인허용오차 	30M
	public static final int PPLATE_CRANE_BS_GAP_Y						= 10000;			//보수장	위치일 경우의 크레인허용오차 	10M
	public static final int PPLATE_CRANE_CN_GAP_X						= 30000;			//가스장	위치일 경우의 크레인허용오차 	30M
	public static final int PPLATE_CRANE_CN_GAP_Y						= 3000;				//가스장	위치일 경우의 크레인허용오차 	3M
	public static final int PPLATE_CRANE_TC_GAP_X						= 11000;			//대차	위치일 경우의 크레인허용오차 	11M
	public static final int PPLATE_CRANE_TC_GAP_Y						= 2500;				//대차	위치일 경우의 크레인허용오차 	2.5M
	public static final int PPLATE_CRANE_TD_GAP_X						= 20000;			//TOD	위치일 경우의 크레인허용오차 	20M
	public static final int PPLATE_CRANE_TD_GAP_Y						= 2000;				//TOD	위치일 경우의 크레인허용오차 	2M
	public static final int PPLATE_CRANE_RT_GAP_X						= 10000;			//RT	위치일 경우의 허용오차          	10M
	public static final int PPLATE_CRANE_RT_GAP_Y						= 1500;				//RT	위치일 경우의 허용오차		1.5M
	public static final int PPLATE_CRANE_CB_GAP_X						= 30000;			//냉각대	위치일 경우의 크레인허용오차 	30M
	public static final int PPLATE_CRANE_CB_GAP_Y						= 10000;			//냉각대	위치일 경우의 크레인허용오차 	10M

	// 야드 길이 TYPE
	public static final String YD_STK_COL_BED_L_TP_F1					= "F1";				//혼적단척
	public static final String YD_STK_COL_BED_L_TP_F2					= "F2";				//혼적장척
	public static final String YD_STK_COL_BED_L_TP_N1					= "N1";				//일반단척
	public static final String YD_STK_COL_BED_L_TP_N2					= "N2";				//일반장척
	public static final String YD_STK_COL_BED_L_TP_V1					= "V1";				//가변	

	public static final String YD_MAIN_WRK_GP_BC_IN						= "E";				// 임가공절단장 Book-In (보급)
	public static final String YD_MAIN_WRK_GP_BC_OUT					= "F";				// 임가공절단장Book-Out(추출)
	
}
