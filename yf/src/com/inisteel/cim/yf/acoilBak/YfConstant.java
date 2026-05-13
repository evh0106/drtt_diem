package com.inisteel.cim.yf.acoilBak;

public class YfConstant 
{
	
	/**
	 * 화면 요청 명령 구분자
	 */
	public final static String CMD_SELECT = "SELECT";
	public final static String CMD_MODIFY = "MODIFY";
	public final static String CMD_DELETE = "DELETE";
	public final static String CMD_INSERT = "INSERT";

	public final static String OP_CMD_TYPE1 = "1";
	public final static String OP_CMD_TYPE2 = "2";
	public final static String OP_CMD_TYPE3 = "3";
	public final static String OP_CMD_TYPE4 = "4";

	
	//공통 문자리턴값
	/** 성공메세지코드 */
	public static final String RETN_CD_SUCCESS				= "SUCCESS";		
	/** 실패메세지코드 */
	public static final String RETN_CD_FAILURE				= "FAILURE";		
	/** 값이 존재하지 않음 */
	public static final String RETN_CD_NOTEXIST				= "NOTEXIST";		
	/** 값이 존재함 */
	public static final String RETN_CD_EXIST				= "EXIST";		    	
	
	//정수리턴값
	/** 성공메세지코드 */
	public static final int RETN_INT_SUCCESS				= 1;		
	/** 실패메세지코드 */
	public static final int RETN_INT_FAILURE				= -10000;	
	/** 전문에러	 */
	public static final int RETN_INT_TC_ERROR				= -10001;	
	
	/* domain */
	public final static String DOMAIN_BF	= "BF";
	public final static String DOMAIN_CM	= "CM";
	public final static String DOMAIN_CP	= "CP";
	public final static String DOMAIN_CS	= "CS";
	public final static String DOMAIN_CT	= "CT";
	public final static String DOMAIN_DM	= "DM";
	public final static String DOMAIN_HM	= "HM";
	public final static String DOMAIN_HR	= "HR";
	public final static String DOMAIN_MA	= "MA";
	public final static String DOMAIN_OR	= "OR";
	public final static String DOMAIN_PC	= "PC";
	public final static String DOMAIN_PM	= "PM";
	public final static String DOMAIN_PO	= "PO";
	public final static String DOMAIN_PR	= "PR";
	public final static String DOMAIN_PS	= "PS";
	public final static String DOMAIN_PT	= "PT";
	public final static String DOMAIN_QM	= "QM";
	public final static String DOMAIN_SC	= "SC";
	public final static String DOMAIN_SM	= "SM";
	public final static String DOMAIN_SS	= "SS";
	public final static String DOMAIN_TS	= "TS";
	public final static String DOMAIN_YD	= "YD";
	public final static String DOMAIN_YM	= "YM";
	public final static String DOMAIN_YF	= "YF";
	
	//Log 분리자
	public final static String LOG_LINE1 = "================================================================================";
	public final static String LOG_LINE2 = "--------------------------------------------------------------------------------";
	
	//Log레벨 상수 정의
	public static final  int ERROR   = 1;
	public static final  int WARNING = 2;
	public static final  int INFO    = 3;
	public static final  int DEBUG   = 4;
	
	/**
	 * 날짜 FORMAT
	 * FORMAT : yyyy-MM-dd HH:mm:ss
	 */
	public final static String DATE_FORMAT_STR = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 날짜 FORMAT
	 * FORMAT : yyyy-MM-dd
	 */
	public final static String DATE_FORMAT_CUT_TIME = "yyyy-MM-dd";

	/**
	 * 날짜 FORMAT
	 * FORMAT : HH:mm:ss
	 */
	public final static String DATE_FORMAT_TIME = "HH:mm:ss";

	/**
	 * 날짜 FORMAT
	 * FORMAT : yyyyMMddHHmmss
	 */
	public final static String DATE_FORMAT_CRE_DT = "yyyyMMddHHmmss";

	/**
	 * 날짜 FORMAT
	 * FORMAT : yyyyMMdd
	 */
	public final static String DATE_FORMAT8 = "yyyyMMdd";
	
	
	/** 야드스케쥴진행상태 0(명령선택대기) */
	public final static String YD_SCH_PROG_STAT_0 = "0";
	/** 야드스케쥴진행상태 1(권상작업지시) */
	public final static String YD_SCH_PROG_STAT_1 = "1";
	/** 야드스케쥴진행상태 2(권상완료) */
	public final static String YD_SCH_PROG_STAT_2 = "2";
	/** 야드스케쥴진행상태 3(권하지시) */
	public final static String YD_SCH_PROG_STAT_3 = "3";
	/** 야드스케쥴진행상태 4(권하완료) */
	public final static String YD_SCH_PROG_STAT_4 = "4";
	/** 야드스케쥴진행상태 W(스케줄수행대기) */
	public final static String YD_SCH_PROG_STAT_W = "W";
	
	
	/** 야드스케쥴기동구분 A(Auto작업) */
	public final static String YD_SCH_ST_GP_A = "A";
	/** 야드스케쥴기동구분 B(작업자 Backup) */
	public final static String YD_SCH_ST_GP_B = "B";
	/** 야드스케쥴기동구분 M(Manual 작업) */
	public final static String YD_SCH_ST_GP_M = "M";
	
	
	/** 야드스케쥴요청구분 1(대차상차완료) */
	public final static String YD_SCH_REQ_GP_1 = "1";
	/** 야드스케쥴요청구분 2(영대차차출발) */
	public final static String YD_SCH_REQ_GP_2 = "2";
	/** 야드스케쥴요청구분 3(영대차도착) */
	public final static String YD_SCH_REQ_GP_3 = "3";
	/** 야드스케쥴요청구분 4(대차하차완료) */
	public final static String YD_SCH_REQ_GP_4 = "4";
	/** 야드스케쥴요청구분 5(공대차출발) */
	public final static String YD_SCH_REQ_GP_5 = "5";
	/** 야드스케쥴요청구분 6(공대차도착) */
	public final static String YD_SCH_REQ_GP_6 = "6";
	/** 야드스케쥴요청구분 A(차량상차완료) */
	public final static String YD_SCH_REQ_GP_A = "A";
	/** 야드스케쥴요청구분 B(영차량출발) */
	public final static String YD_SCH_REQ_GP_B = "B";
	/** 야드스케쥴요청구분 C(영차량도착) */
	public final static String YD_SCH_REQ_GP_C = "C";
	/** 야드스케쥴요청구분 D(차량하차완료) */
	public final static String YD_SCH_REQ_GP_D = "D";
	/** 야드스케쥴요청구분 E(공차량출발) */
	public final static String YD_SCH_REQ_GP_E = "E";
	/** 야드스케쥴요청구분 F(공차량도착) */
	public final static String YD_SCH_REQ_GP_F = "F";
	/** 야드스케쥴요청구분 L(조업설비 인출 스케줄) */
	public final static String YD_SCH_REQ_GP_L = "L";
	/** 야드스케쥴요청구분 U(조업설비 보급 스케줄) */
	public final static String YD_SCH_REQ_GP_U = "U";
	
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
	
	// 크레인작업실적응답 코드
	public static final String CRN_WRK_RE_CD_NORMAL_HD					= "0000";			//정상처리
	public static final String CRN_WRK_RE_CD_NO_WRK						= "9999";			//크레인작업지시가 없을 경우
	public static final String CRN_WRK_RE_CD_NO_WRK2					= "8888";			//강제권상요구 대상이 부적합 경우
	//.....에러코드를 계속 정의 필요.....
	
	//크레인의 설비작업상태
	public static final String YD_EQP_STAT_IDLE 						= "W";				//크레인이 IDLE인 상태 - 스케줄수행대기
	public static final String YD_EQP_STAT_OW 							= "S";				//명령선택대기
	public static final String YD_EQP_STAT_UP_WO 						= "1";				//권상지시
	public static final String YD_EQP_STAT_UP_CMPL 						= "2";				//권상완료
	public static final String YD_EQP_STAT_DN_WO 						= "3";				//권하지시
	public static final String YD_EQP_STAT_DN_CMPL 						= "4";				//권하완료
	public static final String YD_EQP_STAT_DN_CHANGE					= "5";				//권하위치변경
	

	/* +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	
	//개소코드
	/** A연주-B Cast Slab Yard (D2Y43) */
	public static final String WLOC_CD_A_CC_B_CAST_SLAB_YARD	= "D2Y43";
	/** 박판열연-#1 제품/소재 Coil Yard (D2Y44) */
	public static final String WLOC_CD_A_HR_NO1_COIL_YARD		= "D2Y44";
	/** 박판열연-#2 제품/소재 Coil Yard (D2Y45) */
	public static final String WLOC_CD_A_HR_NO2_COIL_YARD		= "D2Y45";
	
	/** B열연-#1 제품/소재 Coil Yard (D3Y41) */
	public static final String WLOC_CD_B_HR_NO1_COIL_YARD		= "D3Y41";
	/** B열연-#2 제품/소재 Coil Yard (D3Y42) */
	public static final String WLOC_CD_B_HR_NO2_COIL_YARD		= "D3Y42";
	/** B열연-Slab Yard (D3Y43) */
	public static final String WLOC_CD_B_HR_SLAB_YARD			= "D3Y43";
	/** B열연-가열로 Slab Yard (D3Y44) */
	public static final String WLOC_CD_B_HR_REFUR_SLAB_YARD		= "D3Y44";
	
	
	/** 
     * 동간 이적 차량 Card No (9990 ~ 9999)
    */
	/** 동간 이적 차량 Car No : 9999 */
    public final static String CAR_BAY_TRANS_CARD_NO_1="9999";
    /** 동간 이적 차량 Car No : 9998 */
    public final static String CAR_BAY_TRANS_CARD_NO_2="9998";
    /** 동간 이적 차량 Car No : 9997 */
    public final static String CAR_BAY_TRANS_CARD_NO_3="9997";
    /** 동간 이적 차량 Car No : 9996 */
    public final static String CAR_BAY_TRANS_CARD_NO_4="9996";
    /** 동간 이적 차량 Car No : 9995 */
    public final static String CAR_BAY_TRANS_CARD_NO_5="9995";
    /** 동간 이적 차량 Car No : 9994 */
    public final static String CAR_BAY_TRANS_CARD_NO_6="9994";
    /** 동간 이적 차량 Car No : 9993 */
    public final static String CAR_BAY_TRANS_CARD_NO_7="9993";
    /** 동간 이적 차량 Car No : 9992 */
    public final static String CAR_BAY_TRANS_CARD_NO_8="9992";
    /** 동간 이적 차량 Car No : 9991 */
    public final static String CAR_BAY_TRANS_CARD_NO_9="9991";
    /** 동간 이적 차량 Car No : 9990 */
    public final static String CAR_BAY_TRANS_CARD_NO_0="9990";
    

    /** 박판열연 SPM */
	public final static String SHEAR_SUPPLY_GP_1K  = "1K"; //
	/** 박판열연 EQL */
	public final static String SHEAR_SUPPLY_GP_1Q  = "1Q"; //
	/** 박판열연 HFL */
	public final static String SHEAR_SUPPLY_GP_1H  = "1H"; //
	
	//일관제철
    public final static String SHEAR_SUPPLY_GP_5K  = "5K"; //B열연 SPM
    public final static String SHEAR_SUPPLY_GP_5H  = "5H"; //B열연 HFL
    public final static String SHEAR_SUPPLY_GP_5T  = "5T"; //B열연 수냉재    
    public final static String SHEAR_SUPPLY_GP_5A  = "5A"; //B열연 공냉재 
    public final static String SHEAR_SUPPLY_GP_5N  = "5N"; //B열연 SPM2
    public final static String SHEAR_SUPPLY_GP_6K  = "6K"; //B열연 SPM2
    public final static String SHEAR_SUPPLY_GP_6H  = "6H"; //B열연 HFL결속장
	
	
    /* 박판열연 Cran No */
	/** FCR1 Crane No */
    public final static String A_CraneNo_FCR1 = "FCR1";
    /** FCR2 Crane No */
    public final static String A_CraneNo_FCR2 = "FCR2";
    
    
    /** 야드구분 */
    /** 박판열연SLAB야드 */
    public final static String YD_GP_0 = "0"; 
    /** 박판열연COIL야드 */
    public final static String YD_GP_1 = "1"; 
    /** C연주슬라브야드 */
    public final static String YD_GP_A = "A";
    /** 박판열연 SLAB 야드 */
    public final static String YD_GP_2 = "2";
    /** 박판열연 COIL 야드 */
    public final static String YD_GP_3 = "3";
    /** C열연 COIL소재야드 */
    public final static String YD_GP_H = "H";
    /** C열연 COIL제품야드 */
    public final static String YD_GP_J = "J";
    /** 부드야드 - 사외후판제품야드 */
    public final static String YD_GP_4 = "4";
    /** 특수강블룸소재 */
    public final static String YD_GP_B = "B";
    
    
    /** 박판열연 동구분 A동 */
    public final static String BAY_GP_A   = "A";
    /** 박판열연 동구분 B동 */
    public final static String BAY_GP_B   = "B";
    /** 박판열연 동구분 C동 */
    public final static String BAY_GP_C   = "C";
    /** 박판열연 동구분 D동 */
    public final static String BAY_GP_D   = "D";
    /** 박판열연 동구분 E동 */
    public final static String BAY_GP_E   = "E";
    /** 박판열연 동구분 F동 */
    public final static String BAY_GP_F   = "F";
    /** 박판열연 동구분 G동 */
    public final static String BAY_GP_G   = "G";
    /** 박판열연 동구분 H동 */
    public final static String BAY_GP_H   = "H";
    
    
    /** 박판열연 보급 구분 작업 EQL */
	public final static String WORK_SPM_E = "E";
	/** 박판열연 보급 구분 작업 SPM */
    public final static String WORK_SPM_S = "S";
    /** 박판열연 보급 구분 작업 HFL */
    public final static String WORK_HFL_H = "H";
    /** 박판열연 보급 구분 작업 HFL 결속대 */
    public final static String WORK_HFL_S = "D";
    /** 박판열연 보급 구분 작업 */
    public final static String SUPPLY_1   = "1";

    
    /** SPM입측 1번 */
    public final static String WORK_SPM_1 = "1"; 
    /** SPM입측 2번 */
    public final static String WORK_SPM_2 = "2";
    /** SPM입측 5번 */
    public final static String WORK_SPM_5 = "5";
    /** SPM입측 6번 */
    public final static String WORK_SPM_6 = "6";
    /** SPM입측 7번 */
    public final static String WORK_SPM_7 = "7";
    
    
    /** HFL 입측 */
    public final static String WORK_HFL_IN_FE   = "FE";
    /** HFL 출측 */
    public final static String WORK_HFL_OUT_FD  = "FD"; 
    
    /** SPM 입측 */
    public final static String WORK_SPM_IN_KE   = "KE";
    /** SPM 출측 */
    public final static String WORK_SPM_OUT_KD  = "KD"; 
    
    /** EQL 입측 */
    public final static String WORK_EQL_IN_QE   = "QE";
    /** EQL 출측 */
    public final static String WORK_EQL_OUT_QD  = "QD";
    
    
    /** 고장/복구분: 복구  */
    public final static String TRO_REC_0 = "0";	
    /** 고장/복구분: 고장  */
	public final static String TRO_REC_1 = "1";	
	/** 고장/복구분: 복구  */
    public final static String TRO_REC_O = "O";	
    /** 고장/복구분: 고장  */
    public final static String TRO_REC_C = "C";	
    /** 고장/복구분: 자리비움  */
    public final static String TRO_REC_E = "E";	
    /** 고장/복구분: 경미한 작업 불가  */
	public final static String TRO_REC_9 = "9";	
	
	
	/** 운전모드/시스템모드  OFF */
	public final static String MODE_0 = "0";	
	/** 운전모드/시스템모드  ON */
	public final static String MODE_1 = "1";	
	/** 운전모드/시스템모드  자리비움 */
	public final static String MODE_2 = "2";	
	/** 운전모드/시스템모드  OFF */
	public final static String MODE_C = "C";	
	/** 운전모드/시스템모드  ON */
	public final static String MODE_O = "O";	
	/** 운전모드/시스템모드  자리비움 */
	public final static String MODE_E = "E";	
	
	
    /** 코일유무 */
    public final static String COIL_YN_N = "0";
    /** 코일유무 */
    public final static String COIL_YN_Y = "1";

    
    /** 작업진행상태 IDEL, Schedule  대기*/ 
	public final static String WPROG_STAT_W	= "W";	
	/** 작업진행상태 출발지시 */ 
	public final static String WPROG_STAT_T	= "T";	
	/** 작업진행상태 이동 중 */ 
	public final static String WPROG_STAT_M	= "M";	
	/** 작업진행상태 Schedule 수행 */ 
	public final static String WPROG_STAT_S	= "S";	
	/** 작업진행상태 작업 중 */ 
	public final static String WPROG_STAT_R	= "R";	
		
	
	/**  구입검사대기 */ 
	public final static String SLAB_STOCK_STAT_A	= "A"; 
	
	
	/** 설비상태 정상 */
	public final static String EQUIP_STAT_O = "O";	
	/** 설비상태 고장 */
	public final static String EQUIP_STAT_C = "C";	
	/** 설비상태 사용금지 */
	public final static String EQUIP_STAT_X = "X";	
	
	
	/** 적재상태 영차 */
	public final static String STACK_STAT_L = "L";	
	/** 적재상태 공차 */
	public final static String STACK_STAT_U = "U";	
	/** 적재상태 IDLE */
	public final static String STACK_STAT_I = "I";
	
	
	/* 차량구분 */
	/** 반입 */
    public final static String CAR_GP_1	= "1";
    /** 출하 */
    public final static String CAR_GP_2	= "2";    
    /** 상차 */
    public final static String CAR_GP_U	= "U";
    /** 도착,하차 */
    public final static String CAR_GP_D	= "D";
    /** 출발 */
    public final static String CAR_GP_S	= "S";
	
	
	/** 포인트개폐구분*/
	public static final String PNT_UNIT_CL_GP_CLOSE	= "C";	//폐
	public static final String PNT_UNIT_CL_GP_OPEN	= "O";	//개
	
	
	/** 휴지코드 정상*/
	public final static String DOWN_CD_0000 = "0000"; 
	
	
	/** 2매작업 가능 구분 최상단만작업*/
	public final static String GRIP_LOT_YN_T = "T"; 
	/** 2매작업 가능 구분 2매작업*/
	public final static String GRIP_LOT_YN_G = "G"; 
	
	
	/** 상차스케쥴 지정 구분 상차*/
	public final static String CARLOAD_ASSIGN_GP_Y = "Y";
	
	
	/** 상차지정유무 지정 */
	public final static String CARLOAD_ASSIGN_Y   = "Y"; 
	/** 상차지정유무 미지정 */
	public final static String CARLOAD_ASSIGN_N   = "N"; 

	
	/** 스케쥴기준 활성 상태 기준 */
	public final static String SCH_RULE_ACTIVE_STAT_A = "A"; 
	/** 스케쥴기준 활성 상태 BackUp */
	public final static String SCH_RULE_ACTIVE_STAT_B = "B"; 
	/** 스케쥴기준 활성 상태 Schedule금지 */
	public final static String SCH_RULE_STAT_X        = "X"; 
	
	
	/** 중계구역사용여부 사용 */
	public final static String CTS_RELAY_GP_Y = "Y"; 
	/** 중계구역사용여부 미사용 */
	public final static String CTS_RELAY_GP_N = "N"; 
	
	
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_0 	 	= "0";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_1 	 	= "1";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_2 	 	= "2";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_3 	 	= "3";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_4 	 	= "4";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_5 	 	= "5";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_ASTA 	= "*";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_ALL 	 = "ALL";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_0_LHCVO = "LHCVO";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_1_LHFPI = "LHFPI";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_2_LSPMI = "LSPMI";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_3_LSH1I = "LSH1I";
	/** 박판열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_4_LSH2I = "LSH2I";
	
	
	public final static String STACK_LAYER_GP_01 			= "01";
	public final static String STACK_LAYER_GP_02 			= "02";
	public final static String STACK_LAYER_GP_03 			= "03";
	public final static String STACK_LAYER_GP_04 			= "04";
	
	
	public final static String PLANT_GP_A					= "A";
	public final static String PLANT_GP_H					= "H";
	
	
	/* 확장대차 설비번호  */
	/** 확장대차 설비번호  1XTC03 */
	public final static String CTS_GP_1XTC03				= "1XTC03";
	/** 확장대차 설비번호  1ATC03 */
	public final static String STACK_COL_GP_1ATC03 			= "1ATC03";
	/** 확장대차 설비번호  1BTC03 */
	public final static String STACK_COL_GP_1BTC03 			= "1BTC03";
	
	/**  */
	public final static String STACK_COL_GP_1BDC01 			= "1BDC01";
	/**  */
	public final static String STACK_COL_GP_1CDC01 			= "1CDC01";
	/**  */
	public final static String STACK_COL_GP_1CDC02          = "1CDC02";
	/**  */
	public final static String STACK_COL_GP_1FKD01          = "1FKD01";
	/**  */
	public final static String STACK_COL_GP_1CFD01          = "1CFD01";
	/**  */
	public final static String STACK_COL_GP_1EKE01          = "1EKE01";
	/**  */
	public final static String STACK_COL_GP_1EKE02          = "1EKE02";
	/**  */
	public final static String STACK_COL_GP_1EKD01          = "1EKD01";
	/**  */
	public final static String STACK_COL_GP_1EQE01          = "1EQE01";
	/**  */
	public final static String STACK_COL_GP_1FQE01          = "1FQE01";
	/**  */
	public final static String STACK_COL_GP_1FQD01          = "1FQD01";
	/**  */
	public final static String STACK_COL_GP_1GQD01          = "1GQD01";
	/**  */
	public final static String STACK_COL_GP_1DKE01          = "1DKE01";
	/**  */
	public final static String STACK_COL_GP_1BFE01          = "1BFE01";
	
	
	/*	설비 열  */ 
	/** 분기conv' : 압연실적 수신시 */
	public final static String STACK_COL_GP_3XDC01 			= "3XDC01";  
	/** A동 분기 LINE OFF 요구 */
	public final static String STACK_COL_GP_3AST01          = "3AST01";  
	/** B동 분기 LINE OFF 요구 */
	public final static String STACK_COL_GP_3BST02          = "3BST02";  
	/** C동 분기 LINE OFF 요구 */
	public final static String STACK_COL_GP_3CST03          = "3CST03";  
	/** 분기 LINE OFF 요구 //화면에서 EXT STD 에서 비상시 LINE-OFF 처리 */
	public final static String STACK_COL_GP_3CEX01          = "3CEX01";  
	/** B동 분기 Conveyor Take Out(Turn Table2)  */
	public final static String STACK_COL_GP_3BTT01          = "3BTT01";  
	/** B동 분기 Conveyor Take Out  */
	public final static String STACK_COL_GP_3BTT02          = "3BTT02";  
	/** B동 분기 Conveyor Take Out  */
	public final static String STACK_COL_GP_3BSC01          = "3BSC01";  
	/** B동 확장 CONV */
	public final static String STACK_COL_GP_3BWB05          = "3BWB05";  
	/** C동 확장 CONV */
	public final static String STACK_COL_GP_3CWB02          = "3CWB02";  
	/** C동 확장 CONV */
	public final static String STACK_COL_GP_3CWB03          = "3CWB03";  
	/** C동 확장 CONV */
	public final static String STACK_COL_GP_3CWB04          = "3CWB04";  
	/** C동 확장 CONV */
	public final static String STACK_COL_GP_3CWB06          = "3CWB06";  
	/** C동 확장 CONV */
	public final static String STACK_COL_GP_3CWB10          = "3CWB10";  
	/** D동 확장 CONV */
	public final static String STACK_COL_GP_3DWB07          = "3DWB07";  
	/** B동 SPM1 보급,Take-Out */
	public final static String STACK_COL_GP_3BKE01          = "3BKE01";  
	/** C동 SPM1 보급,Take-Out */
	public final static String STACK_COL_GP_3CKE01          = "3CKE01";  
	/** A동 SPM1 분기 */
	public final static String STACK_COL_GP_3AKD01          = "3AKD01";  
	/** B동 SPM1 분기,Take-Out */
	public final static String STACK_COL_GP_3BKD01          = "3BKD01";   
	/** HFL 입측 ,Take-Out  */
	public final static String STACK_COL_GP_3AFE01          = "3AFE01";  
	/** HFL 입측 ,Take-Out */
	public final static String STACK_COL_GP_3EFE01          = "3EFE01";  
	/** HFL 출측 */
	public final static String STACK_COL_GP_3CFD01          = "3CFD01";  
	/** HFL Take-Out */
	public final static String STACK_COL_GP_3BFD01          = "3BFD01";  
	/** HFL Take-Out */
	public final static String STACK_COL_GP_3BFE01          = "3BFE01";  
	/** D동 SPM2 입측Take-Out */
	public final static String STACK_COL_GP_3DKE01          = "3DKE01";  
	/** E동 SPM2 입측Take-Out */
	public final static String STACK_COL_GP_3EKE01          = "3EKE01";  
	/** E동 SPM2 출측Take-Out */
	public final static String STACK_COL_GP_3EKD01          = "3EKD01";  
	/** E동 SPM2 출측(SCRAP) */
	public final static String STACK_COL_GP_3EKD02          = "3EKD02";  
	/** A동 SPM  SCRAP처리장 적치단 */
	public final static String STACK_COL_GP_3ASP01			= "3ASP01";	 
	/** E동 SPM2 SCRAP처리장 적치단 */
	public final static String STACK_COL_GP_3ESP01			= "3ESP01";	 
	/** E동 SPM2 입측재처리위치 */
	public final static String STACK_COL_GP_3EKE05          = "3EKE05";  
	/** HFL 결속대 위치 */
	public final static String STACK_COL_GP_3DHS01          = "3DHS01";  
	
	/** HFL */
	public final static String HFL_COL_1BFE = "1BFE";
	/** HFL */
	public final static String HFL_COL_1CFD = "1CFD";
	/** SPM */
	public final static String SPM_COL_1DKE = "1DKE";
	/** SPM */
	public final static String SPM_COL_1EKE = "1EKE";
	/** SPM */
	public final static String SPM_COL_1EKD = "1EKD";
	/** SPM */
	public final static String SPM_COL_1FKD = "1FKD";
	/** EQL */
	public final static String EQL_COL_1FQE = "1EQE";
	/** EQL */
	public final static String EQL_COL_1FQD = "1FQD";
	/** EQL */
	public final static String EQL_COL_1GQD = "1GQD";
	/** 압연 */
	public final static String Roll_COL_1BDC = "1BDC";
	/** 압연 */
	public final static String Roll_COL_1CDC = "1CDC";
	
	/** 작업 구분   자동 */
	public final static String WORK_GP_A				= "A"; 
	/** 작업 구분   수동 */
	public final static String WORK_GP_H				= "H";  
	
	
	/**  */
	public final static String GBN_MIN 					= "MIN";
	public final static String GBN_MAX 					= "MAX";
	public final static String GBN_J05 					= "J05";
	
	
	/**  */
	public final static String STACK_BED_GP_01 	 		= "01";
	public final static String STACK_BED_GP_02 	 		= "02";
	public final static String STACK_BED_GP_03 	 		= "03";
	public final static String STACK_BED_GP_04 	 		= "04";
	public final static String STACK_BED_GP_05 	 		= "05";
	
	
	/* 야드L2요구상태 */
	/** 선택*/
    public final static String YD_L2_REQUEST_STAT_1     	= "1";
    /** 권상*/
    public final static String YD_L2_REQUEST_STAT_2     	= "2";
    /** 보류*/
    public final static String YD_L2_REQUEST_STAT_C     	= "C";
    /** 선택*/
    public final static String YD_L2_REQUEST_STAT_W     	= "W";
    /** 권하위치변경*/
    public final static String YD_L2_REQUEST_STAT_5     	= "5";
    /** 스케쥴취소요청:응답대기중(A7YML015) */
    public final static String YD_L2_REQUEST_STAT_D     	= "D";
    /** 작업취소요청   :응답대기중(A7YML015) */
    public final static String YD_L2_REQUEST_STAT_X     	= "X";
	
	

	
	
	/** 중계구역 사용 유무  유*/
	public final static String CTS_RELAY_YN_Y 	= "Y"; 
	/** 중계구역 사용 유무  무*/
	public final static String CTS_RELAY_YN_N 	= "N"; 
	
	
	/** 1번 CTS */
	public final static String CTS_1XTC01 	= "1XTC01"; 
	/** 2번 CTS */
	public final static String CTS_1XTC02 	= "1XTC02"; 
	/** A동 대차 */
	public final static String TC_1ATC03 	= "1ATC03"; 
	/** B동 대차 */
	public final static String TC_1BTC03 	= "1BTC03"; 
	
	
	/** STACK_MAX_QNTY_3XTC01
	 * 최대적치 가능매수 기준코드 검토
	 * CTS COIL CAR 01호
	 *  - TB_YF_EQP :: STK_MAX_QNTY
	 */
	public final static String TC_STK_MAX_QNTY_1XTC01 = "1";
	
	/** STACK_MAX_QNTY_3XTC02
	 * 최대적치 가능매수 기준코드 검토
	 * CTS COIL CAR 02호
	 * - TB_YF_EQP :: STK_MAX_QNTY
	 */
	public final static String TC_STK_MAX_QNTY_1XTC02 = "1";
	
	/** STACK_MAX_QNTY_3XTC03
	 * 최대적치 가능매수 기준코드 검토
	 * 확장대차
	 * - TB_YF_EQP :: STK_MAX_QNTY
	 */
	public final static String TC_STK_MAX_QNTY_1XTC03 = "3";
	
	
	/** 설비구분 */
	public final static String TC_1X      	= "1X";
	/** 설비구분 */
	public final static String TC_2X      	= "2X";
	/** 설비구분 B열연 Coil대차 */
	public final static String TC_3X      	= "3X";		
	
	
	/** 작업MODE OnLine */
	public final static String WORK_MODE_O = "O";	
	/** 작업MODE OffLine */
	public final static String WORK_MODE_C = "C";	
	/** 작업MODE 자리비움 */
	public final static String WORK_MODE_E = "E";	
	
	
	/** SCHEDULE 작업요구 형태 SCHEDULE */
	public final static String SCH_WDEMAND_TYPE_S = "S";
	/** SCHEDULE 작업요구 형태 화면 */
	public final static String SCH_WDEMAND_TYPE_V = "V";
	/** SCHEDULE 작업요구 형태 PDA */
	public final static String SCH_WDEMAND_TYPE_P = "P";
	/** SCHEDULE 작업요구 형태 CRANE */
	public final static String SCH_WDEMAND_TYPE_C = "C";
	/** SCHEDULE 작업요구 형태 MILL L2 */
	public final static String SCH_WDEMAND_TYPE_M = "M";
	/** SCHEDULE 작업요구 형태 정정 L2 */
	public final static String SCH_WDEMAND_TYPE_J = "J";
	/** SCHEDULE 작업요구 형태 야드 L2 */
	public final static String SCH_WDEMAND_TYPE_Y = "Y";
	/** SCHEDULE 작업요구 형태 HMI*/
	public final static String SCH_WDEMAND_TYPE_H = "H";
	
	
	/** 박판열연 연주 7호기	Slab Line Off 요구 */
	public final static String TC_HC3PB51="HC3PB51";	
	/** 박판열연 연주 7호기	YARD MAP 정보 요구 */
	public final static String TC_HC3PB52="HC3PB52";	
	/** 박판열연 연주 7호기	ROT 고장,복구	   */
	public final static String TC_HC3PB53="HC3PB53";	
	/** 박판열연 연주 7호기	야드MAP 정보 */
	public final static String TC_HC3BP51="HC3BP51";	         
	/** 박판열연 연주 7호기	CRANE 고장정보 */
	public final static String TC_HC3BP52="HC3BP52";	
	
	

	
	
	/** 적치단 재료 상태 - 적치중 */
	public static final String YD_STK_LYR_MTL_STAT_C	= "C";
	/** 적치단 재료 상태 - 권하대기 */
	public static final String YD_STK_LYR_MTL_STAT_D	= "D";
	/** 적치단 재료 상태 - 적치가능 */
	public static final String YD_STK_LYR_MTL_STAT_E	= "E";
	/** 적치단 재료 상태 - 권상대기 */
	public static final String YD_STK_LYR_MTL_STAT_U	= "U";
	/** 적치단 재료 상태 - 적치불가 */
	public static final String YD_STK_LYR_MTL_STAT_X	= "X";
	
	
	/** SADDLE, CTS */
	public final static String EQUIP_KIND_ALL     	= "*";
	/** SADDLE */
	public final static String EQUIP_KIND_SA     	= "S";
	/** CTS */
	public final static String EQUIP_KIND_CT     	= "C";
	/** 대차 */
	public final static String EQUIP_KIND_TC     	= "TC";
	/** CRANE */
	public final static String EQUIP_KIND_CR     	= "CR";
	/** 차량 */
	public final static String EQUIP_KIND_TR     	= "TR";
	/** 팔레트 */
	public final static String EQUIP_KIND_PT     	= "PT";
	
	
	/** 박판열연 동에 따른 ROT Name(MCH) 팔레트 */
	public final static String EQUIP_KIND_RT     	= "RT"; 	
	/** 박판열연 동에 따른 ROT Name(MCH) ROT 존 */
	public final static String EQUIP_KIND_0_A_RT    = "RT03"; 	
	/** 박판열연 동에 따른 ROT Name(MCH) ROT 존 */
	public final static String EQUIP_KIND_0_B_RT    = "RT02"; 	
	
	
	/** 박판열연 확장대차 */
	public final static String EQUIP_GP_1XTC03     	= "1XTC03"; 	
	/** B열연 확장대차 */
	public final static String EQUIP_GP_2XTC01     	= "2XTC01"; 	
	/** B열연 확장대차 */
	public final static String EQUIP_GP_2XTC02     	= "2XTC02"; 	
	/** B열연 확장대차 */
	public final static String EQUIP_GP_2XTC03     	= "2XTC03"; 	
	/** B열연 확장대차 */
	public final static String EQUIP_GP_3XTC02     	= "3XTC02"; 	
	
	
	/** 주작업 */
	public final static String MAIN_WORK_M			= "M";// 
	/** 보조작업 */
	public final static String SUB_WORK_S			= "S";//  
	/** 주작업 */
	public final static String MAIN_WORK_01			= "01";// 
	/** 보조작업 */
	public final static String SUB_WORK_02			= "02";// 
	
	
	/** SLAB 소재 */
	public final static String ITEM_SM 				= "SM";	 
	/** COIL 소재 */
	public final static String ITEM_CM 				= "CM";	 
	/** COIL 제품 */
	public final static String ITEM_CG 				= "CG";	 
	/** Plate */
	public final static String ITEM_HP 				= "HP";	 
	
	
	/** 생산예정 */
	public final static String NEW_STOCK_MOVE_TERM_1C 	= "1C";	
	/** HFL 추출 */
	public final static String NEW_STOCK_MOVE_TERM_A1 	= "A1";	
	/** SPM 추출 */
	public final static String NEW_STOCK_MOVE_TERM_A2 	= "A2";	
	/** 수냉재추출 */
	public final static String NEW_STOCK_MOVE_TERM_A3 	= "A3";   
	/** 공냉재추출 */
	public final static String NEW_STOCK_MOVE_TERM_A4 	= "A4";	
	/** 수냉재보급완료 */
	public final static String NEW_STOCK_MOVE_TERM_A5 	= "A5";	
	/** 재질판정대기 */
	public final static String NEW_STOCK_MOVE_TERM_AC   = "AC";	
	/** 정정작업지시대기 */
	public final static String NEW_STOCK_MOVE_TERM_BC	= "BC";	
	/** 정정작업대기 */
	public final static String NEW_STOCK_MOVE_TERM_CC	= "CC";	
	/** 보급완료  */
	public final static String NEW_STOCK_MOVE_TERM_C1 	= "C1";  	
	/** 이송작업지시대기 */
	public final static String NEW_STOCK_MOVE_TERM_DC 	= "DC";	
	/** 이송완료 */
	public final static String NEW_STOCK_MOVE_TERM_E1 	= "E1";	
	/** 이송작업대기 */
	public final static String NEW_STOCK_MOVE_TERM_EC	= "EC";	
	/** 판정보류 */
	public final static String NEW_STOCK_MOVE_TERM_FC 	= "FC";	
	/** 종합판정대기 */
	public final static String NEW_STOCK_MOVE_TERM_GC 	= "GC";	
	/** 입고대기 */
	public final static String NEW_STOCK_MOVE_TERM_HG 	= "HG";	
	/** 입고완료 */
	public final static String NEW_STOCK_MOVE_TERM_H1 	= "H1";	
	/** 반납 대기(정보) */
	public final static String NEW_STOCK_MOVE_TERM_JG 	= "JG";	
	/** 반납 대기(현물) */
	public final static String NEW_STOCK_MOVE_TERM_JR 	= "JR";	
	/** 반납 완료 */
	public final static String NEW_STOCK_MOVE_TERM_J1 	= "J1";	
	/** 출하작업지시대기 */
	public final static String NEW_STOCK_MOVE_TERM_KG 	= "KG";	
	/** 반입대기 */
	public final static String NEW_STOCK_MOVE_TERM_K1 	= "K1";	
	/** 출하작업대기 */
	public final static String NEW_STOCK_MOVE_TERM_LG 	= "LG";	
	/** 대차출하완료 */
	public final static String NEW_STOCK_MOVE_TERM_L1 	= "L1";	
	/** 대차출하대기 */
	public final static String NEW_STOCK_MOVE_TERM_L2 	= "L2";	
	/** 출하완료 */
	public final static String NEW_STOCK_MOVE_TERM_MG 	= "MG";	
	/** 보관Coil */
	public final static String NEW_STOCK_MOVE_TERM_M1 	= "M1";	
	/** 보관제품 */
	public final static String NEW_STOCK_MOVE_TERM_M2 	= "M2";	
	/** 운송지시대기	 */
	public final static String NEW_STOCK_MOVE_TERM_NG 	= "NG";	
	/** 경매대상선정 */
	public final static String NEW_STOCK_MOVE_TERM_XG 	= "XG";	
	/** 재공충당대기 */
	public final static String NEW_STOCK_MOVE_TERM_YG 	= "YG";	
	/** 제품충당대기 */
	public final static String NEW_STOCK_MOVE_TERM_ZG 	= "ZG";	
	/** 대차상차완료 */
	public final static String NEW_STOCK_MOVE_TERM_TL 	= "TL";	
	/** 대차이동 */
	public final static String NEW_STOCK_MOVE_TERM_TM 	= "TM";	
	/** CTS상차완료 */
	public final static String NEW_STOCK_MOVE_TERM_CL 	= "CL";	
	/** CTS이동 */
	public final static String NEW_STOCK_MOVE_TERM_CM 	= "CM";	
	
	
	/** Coil 차량이적 */
	public final static String NEW_STOCK_MOVE_TERM_CR 	= "CR";	
	/** Coil 차량상차완료 */
	public final static String NEW_STOCK_MOVE_TERM_RL 	= "RL";	
	/** B열연SPM2추출 */
	public final static String NEW_STOCK_MOVE_TERM_A6 	= "A6";	
	/** HFL결속장  추출 */
	public final static String NEW_STOCK_MOVE_TERM_A7 	= "A7";	 
	/** 지포장  추출 */
	public final static String NEW_STOCK_MOVE_TERM_A8 	= "A8";	 
	
	
	/** 생산예정 */
	public final static String NEW_STOCK_MOVE_TERM_1S 	= "1S";	
	/** SLAB 구입등록(품질) */
	public final static String NEW_STOCK_MOVE_TERM_11 	= "11";	
	/** SLAB 구입확정(품질) */
	public final static String NEW_STOCK_MOVE_TERM_12 	= "12";	
	/** 생산종료 */
	public final static String NEW_STOCK_MOVE_TERM_3S 	= "3S";	
	/** 수입검사대기 */
	public final static String NEW_STOCK_MOVE_TERM_AS 	= "AS";	
	/** 이송지시대기 */
	public final static String NEW_STOCK_MOVE_TERM_BS 	= "BS";	
	/** 후판WCR재추출 */
	public final static String NEW_STOCK_MOVE_TERM_B0 	= "B0";	
	/** B열연WCR재추출 */
	public final static String NEW_STOCK_MOVE_TERM_B1 	= "B1";	
	/** 후판CCR재추출 */
	public final static String NEW_STOCK_MOVE_TERM_B2 	= "B2";	
	/** B열연CCR재추출 */
	public final static String NEW_STOCK_MOVE_TERM_B3 	= "B3";	
	/** C열연WCR재추출 */
	public final static String NEW_STOCK_MOVE_TERM_B4 	= "B4";	
	/** C열연CCR재추출 */
	public final static String NEW_STOCK_MOVE_TERM_B5 	= "B5";	
	/** 이송대기 */
	public final static String NEW_STOCK_MOVE_TERM_CS	= "CS";	
	/** 정정작업대기 */
	public final static String NEW_STOCK_MOVE_TERM_DS 	= "DS";	
	/** Scarfing 보급완료 */
	public final static String NEW_STOCK_MOVE_TERM_D1 	= "D1";	
	/** 시편작업대기 */
	public final static String NEW_STOCK_MOVE_TERM_D2 	= "D2";	
	/** 핸드스카핑작업대기 */
	public final static String NEW_STOCK_MOVE_TERM_D3 	= "D3";	
	/** 보류재 */
	public final static String NEW_STOCK_MOVE_TERM_D4 	= "D4";	
	/** 압연지시대기 */
	public final static String NEW_STOCK_MOVE_TERM_ES 	= "ES";	
	/** 압연작업대기 */
	public final static String NEW_STOCK_MOVE_TERM_FS 	= "FS";	
	/** W/B 보급완료 */
	public final static String NEW_STOCK_MOVE_TERM_F1 	= "F1";	
	/** CTC Loading완료 */
	public final static String NEW_STOCK_MOVE_TERM_F2 	= "F2";	
	/** R/T Loading완료 */
	public final static String NEW_STOCK_MOVE_TERM_F3 	= "F3";	
	/** SLAB 출하작업지시대기 */
	public final static String NEW_STOCK_MOVE_TERM_KS 	= "KS";	
	/** SLAB 출하작업대기 */
	public final static String NEW_STOCK_MOVE_TERM_LS 	= "LS";	
	/** SLAB 출하완료 */
	public final static String NEW_STOCK_MOVE_TERM_MS 	= "MS";	
	/** SLAB 운송지시대기[신규추가] */
	public final static String NEW_STOCK_MOVE_TERM_NS 	= "NS";	
	/** SLAB 종합판정대기[신규추가] */
	public final static String NEW_STOCK_MOVE_TERM_GS 	= "GS";	
	/** SLAB 입고대기[신규추가] */
	public final static String NEW_STOCK_MOVE_TERM_HS 	= "HS";	
	/** SLAB 반납대기[신규추가] */
	public final static String NEW_STOCK_MOVE_TERM_JS 	= "JS";	
	/** 판정보류 */
	public final static String NEW_STOCK_MOVE_TERM_YS 	= "YS";	
	/** 충당대기 */
	public final static String NEW_STOCK_MOVE_TERM_ZS 	= "ZS";
	/** 상차완료 */
	public final static String NEW_STOCK_MOVE_TERM_VL 	= "VL";	
	/** 차량이동 */
	public final static String NEW_STOCK_MOVE_TERM_VM 	= "VM";	
	/** WCR이동 */
	public final static String NEW_STOCK_MOVE_TERM_VW 	= "VW";	
	
	
	/* COIL NEW 스케쥴종류 */
	/** Coil DC Take out */
	public final static String NEW_SCH_WORK_KIND_CDTO = "CDTO";	
	/** Coil DC Take In */
	public final static String NEW_SCH_WORK_KIND_CDTI = "CDTI";		
	/** Coil DC Line Off */
	public final static String NEW_SCH_WORK_KIND_CDLO = "CDLO";	
	/** Coil DC Line In */
	public final static String NEW_SCH_WORK_KIND_CDLI = "CDLI";		
	/** Coil EC Line Off */
	public final static String NEW_SCH_WORK_KIND_CELO = "CELO";	
	
	
	/** Coil CTS 하차       --좌측 적치 시 */
	public final static String NEW_SCH_WORK_KIND_CCMU = "CCMU";	
	/** Coil CTS 하차(2) --우측 적치 시 */
	public final static String NEW_SCH_WORK_KIND_CCMR = "CCMR";	
	/** Coil HFL 보급 */
	public final static String NEW_SCH_WORK_KIND_CFLI = "CFLI";		
	/** Coil HFL 결속대 보급 */
	public final static String NEW_SCH_WORK_KIND_CFSI = "CFSI";		
	/** Coil SPM 보급 */
	public final static String NEW_SCH_WORK_KIND_CKLI = "CKLI";		
	/** Coil EQL 보급 */
	public final static String NEW_SCH_WORK_KIND_EQLI = "EQLI";		
	/** Coil 수냉재보급 */
	public final static String NEW_SCH_WORK_KIND_CWLI = "CWLI";		
	/** Coil HFL Take In */
	public final static String NEW_SCH_WORK_KIND_CFTI = "CFTI";		
	/** Coil SPM Take In */
	public final static String NEW_SCH_WORK_KIND_CKTI = "CKTI";		
	/** Coil EQL Take In */
	public final static String NEW_SCH_WORK_KIND_EQTI = "EQTI";		
	/** Coil HFL Take Out */
	public final static String NEW_SCH_WORK_KIND_CFTO = "CFTO";		
	/** Coil SPM Take Out */
	public final static String NEW_SCH_WORK_KIND_CKTO = "CKTO";		
	/** Coil EQL Take Out */
	public final static String NEW_SCH_WORK_KIND_EQTO = "EQTO";		
	/** Coil HFL 추출 */
	public final static String NEW_SCH_WORK_KIND_CFLO = "CFLO";		
	/** Coil HFL 결속대 추출 */
	public final static String NEW_SCH_WORK_KIND_CFSO = "CFSO";		
	/** Coil SPM 추출 */
	public final static String NEW_SCH_WORK_KIND_CKLO = "CKLO";		
	/** Coil EQL 추출 */
	public final static String NEW_SCH_WORK_KIND_EQLO = "EQLO";		
	/** Coil SPM 재작업 추출 */
	public final static String NEW_SCH_WORK_KIND_CKLR = "CKLR";		
	/** Coil EQL 재작업 추출 */
	public final static String NEW_SCH_WORK_KIND_EQLR = "EQLR";		
	/** Coil 수냉탱크추출 */
	public final static String NEW_SCH_WORK_KIND_CWLO = "CWLO";	
	/** Coil 보류장입고 */
	public final static String NEW_SCH_WORK_KIND_CYST = "CYST";		
	/** Coil 소재이송상차 */
	public final static String NEW_SCH_WORK_KIND_CVML = "CVML";	
	/** Coil 소재이송상차 */
	public final static String NEW_SCH_WORK_KIND_CVM2 = "CVM2";	
	/** Coil 소재이송상차 */
	public final static String NEW_SCH_WORK_KIND_CVM3 = "CVM3";	
	/** Coil 소재차량이송상차(L) */
	public final static String NEW_SCH_WORK_KIND_CVM6 = "CVM6";	
	/** Coil 소재차량이송상차(R) */
	public final static String NEW_SCH_WORK_KIND_CVM8 = "CVM8";	
	/** Coil 소재이송하차 */
	public final static String NEW_SCH_WORK_KIND_CVMU = "CVMU";	
	/** Coil 소재이송하차 */
	public final static String NEW_SCH_WORK_KIND_CVM4 = "CVM4";	
	/** Coil 소재이송하차 */
	public final static String NEW_SCH_WORK_KIND_CVM5 = "CVM5";	
	/** Coil 소재차량이송하차(L) */
	public final static String NEW_SCH_WORK_KIND_CVM7 = "CVM7";	
	/** Coil 소재차량이송하차(R) */
	public final static String NEW_SCH_WORK_KIND_CVM9 = "CVM9";	
	/** Coil 동내이적 */
	public final static String NEW_SCH_WORK_KIND_CYMM = "CYMM";	
	/** Coil 동내이적 */
	public final static String NEW_SCH_WORK_KIND_CYM1 = "CYM1";	
	/** Coil 동내이적 */
	public final static String NEW_SCH_WORK_KIND_CYM2 = "CYM2";	
	/** Coil 동내이적 */
	public final static String NEW_SCH_WORK_KIND_CYM3 = "CYM3";	
	// 자동 동내 이적
	/** Coil 동내이적 */
	public final static String NEW_SCH_WORK_KIND_CYA1 = "CYA1";	
	/** Coil 동내이적 */
	public final static String NEW_SCH_WORK_KIND_CYA2 = "CYA2";	
	/** Coil 동내이적 */
	public final static String NEW_SCH_WORK_KIND_CYA3 = "CYA3";	
	/** Coil 동내이적 */
	public final static String NEW_SCH_WORK_KIND_CYA4 = "CYA4";	
	/** Coil 동간보급상차 */
	public final static String NEW_SCH_WORK_KIND_CTSL = "CTSL";		
	/** Coil 동간보급상차 */
	public final static String NEW_SCH_WORK_KIND_CTS2 = "CTS2";		
	/** Coil 동간보급상차 */
	public final static String NEW_SCH_WORK_KIND_CTS3 = "CTS3";		
	/** Coil 동간이적상차(L) */
	public final static String NEW_SCH_WORK_KIND_CTML = "CTML";	
	/** Coil 동간이적상차(R) */
	public final static String NEW_SCH_WORK_KIND_CTM2 = "CTM2";	
	/** Coil 동간이적상차 */
	public final static String NEW_SCH_WORK_KIND_CTM3 = "CTM3";	
	/** Coil 대차하차(L) */
	public final static String NEW_SCH_WORK_KIND_CTMU = "CTMU";	
	/** Coil 대차하차(R) */
	public final static String NEW_SCH_WORK_KIND_CTM4 = "CTM4";	
	
	
	//==START========================================================================================
	// CGS
	// B열연 Coil 신규 대차에 대한 sch Code : 동간이적상차
	// 3개의 추가설비에 대한 코드 
	// 2009-04-13
	/** Coil 신규1대차 동간이적상차 (A <--> B) */
	public final static String NEW_SCH_WORK_KIND_CTM5 = "CTM5";  
	/** Coil 신규2대차 동간이적상차 (C <--> D) */
	public final static String NEW_SCH_WORK_KIND_CTM6 = "CTM6";  
	/** Coil 신규3대차 동간이적상차 (D <--> E) */
	public final static String NEW_SCH_WORK_KIND_CTM7 = "CTM7";  
	// B열연 Coil 신규 대차에 대한 sch Code : 대차 하차
	// 3개의 추가설비에 대한 코드 	
	/** Coil 신규1대차(A <--> B)하차  */
	public final static String NEW_SCH_WORK_KIND_CTM8 = "CTM8";  
	/** Coil 신규2대차(C <--> D)하차 */
	public final static String NEW_SCH_WORK_KIND_CTM9 = "CTM9";  
	/** Coil 신규3대차(D <--> E)하차 */
	public final static String NEW_SCH_WORK_KIND_CTMX = "CTMX";  
	/** Coil #2 SPM 보급 */
	public final static String NEW_SCH_WORK_KIND_CNLI = "CNLI";  
	/** Coil #2 SPM Take In */
	public final static String NEW_SCH_WORK_KIND_CNTI = "CNTI";	 
	/** Coil #2 SPM 추출 */
	public final static String NEW_SCH_WORK_KIND_CNLO = "CNLO";	 
	/** Coil #2 SPM Take Out */
	public final static String NEW_SCH_WORK_KIND_CNTO = "CNTO";	 
	/** Coil #2 HFL 보급 */
	public final static String NEW_SCH_WORK_KIND_CHLI = "CHLI";  
	/** Coil #2 HFL 보급 */
	public final static String NEW_SCH_WORK_KIND_CHLO = "CHLO";  
	//==END==========================================================================================	
	/** Coil 제품이송상차 */
	public final static String NEW_SCH_WORK_KIND_GVML = "GVML";	
	/** Coil 제품이송상차 */
	public final static String NEW_SCH_WORK_KIND_GVM2 = "GVM2";	
	/** Coil 제품이송상차 */
	public final static String NEW_SCH_WORK_KIND_GVM3 = "GVM3";	
	/** Coil 제품차량이송상차(L) */
	public final static String NEW_SCH_WORK_KIND_GVM6 = "GVM6";	
	/** Coil 제품차량이송상차(R) */
	public final static String NEW_SCH_WORK_KIND_GVM8 = "GVM8";	
	/** Coil 제품이송하차(L) */
	public final static String NEW_SCH_WORK_KIND_GVMU = "GVMU";	
	/** Coil 제품이송하차(2) */
	public final static String NEW_SCH_WORK_KIND_GVM4 = "GVM4";	
	/** Coil 제품이송하차 */
	public final static String NEW_SCH_WORK_KIND_GVM5 = "GVM5";	
	/** Coil 제품차량이송하차(L) */
	public final static String NEW_SCH_WORK_KIND_GVM7 = "GVM7";	
	/** Coil 제품차량이송하차(R) */
	public final static String NEW_SCH_WORK_KIND_GVM9 = "GVM9";	
	/** Coil TR제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GVFL = "GVFL";		
	/** Coil TR제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GVF1 = "GVF1";		
	/** Coil TR제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GVF2 = "GVF2";		
	/** Coil TT제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GTFL = "GTFL";		
	/** Coil TT제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GTF1 = "GTF1";		
	/** Coil TT제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GTF2 = "GTF2";		
	/** Coil PT제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GPFL = "GPFL";		
	/** Coil PT제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GPF1 = "GPF1";		
	/** Coil PT제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_GPF2 = "GPF2";		
	/** Coil 대차출하상차 */
	public final static String NEW_SCH_WORK_KIND_CTFL = "CTFL";		
	/** Coil 대차출하하차 */
	public final static String NEW_SCH_WORK_KIND_CTFU = "CTFU";		
	/** Coil 반입 */
	public final static String NEW_SCH_WORK_KIND_CVRU = "CVRU";	
	
	/* 저장 영역 */
	/** COIL 냉각장 */
	public final static String STACK_COL_USAGE_CD_C1 	= "C1";			 
	/** COIL TAKE OUT 적치장 */
	public final static String STACK_COL_USAGE_CD_C2 	= "C2";			 
	/** COIL 이상분기적치장 */
	public final static String STACK_COL_USAGE_CD_C3 	= "C3";			 
	/** COIL 비상적치장 */
	public final static String STACK_COL_USAGE_CD_C4 	= "C4";			 
	/** COIL 보관소재적치장 */
	public final static String STACK_COL_USAGE_CD_C5 	= "C5";			 
	/** COIL 정정보급대기장 */
	public final static String STACK_COL_USAGE_CD_C6 	= "C6";			 
	/** COIL 소재이송대기장 */
	public final static String STACK_COL_USAGE_CD_C7 	= "C7";			 
	/** COIL 보류장 */
	public final static String STACK_COL_USAGE_CD_C8 	= "C8";			 
	/** COIL 제품출하대기장 */
	public final static String STACK_COL_USAGE_CD_G1 	= "G1";			 
	/** COIL 제품이송상차장 */
	public final static String STACK_COL_USAGE_CD_G2 	= "G2";			 
	/** COIL 제품이송하차장 */
	public final static String STACK_COL_USAGE_CD_G3 	= "G3";			 
	/** COIL 제품이적중계장 */
	public final static String STACK_COL_USAGE_CD_G4 	= "G4";			 
	/** COIL 제품보관적치장 */
	public final static String STACK_COL_USAGE_CD_G5 	= "G5";			 
	/** CTS FROM SADDLE */
	public final static String STACK_COL_USAGE_CD_FS 	= "FS";			 
	/** CTS TO SADDLE */
	public final static String STACK_COL_USAGE_CD_TS 	= "TS";			 
	/** COIL 분기콘베이어  */
	public final static String STACK_COL_USAGE_CD_CC 	= "CC";			 
	/** COIL 확장콘베이어 */
	public final static String STACK_COL_USAGE_CD_CE 	= "CE";			 
	/** COIL 수냉탱크 */
	public final static String STACK_COL_USAGE_CD_CW 	= "CW";			 
	/** COIL HFL보급위치 */
	public final static String STACK_COL_USAGE_CD_FE 	= "FE";			 
	/** COIL HFLTAKEIN위치 */
	public final static String STACK_COL_USAGE_CD_FI 	= "FI";			 
	/** COIL HFL추출위치 */
	public final static String STACK_COL_USAGE_CD_FD 	= "FD";			 
	/** COIL SPM보급위치 */
	public final static String STACK_COL_USAGE_CD_KE 	= "KE";			 
	/** COIL EQL보급위치 */
	public final static String STACK_COL_USAGE_CD_QE 	= "QE";			 
	/** COIL SPMTAKEIN위치 */
	public final static String STACK_COL_USAGE_CD_KI 	= "KI";			 
	/** COIL SPM추출위치 */
	public final static String STACK_COL_USAGE_CD_KD 	= "KD";			 
	/** COIL EQL추출위치 */
	public final static String STACK_COL_USAGE_CD_QD 	= "QD";			 
	/** COIL 비상적치위치 */
	public final static String STACK_COL_USAGE_CD_XX 	= "XX";			 
	/** 대차정지위치 */
	public final static String STACK_COL_USAGE_CD_CX 	= "CX";			 
	/** 차량정지위치 */
	public final static String STACK_COL_USAGE_CD_TX 	= "TX";			 
	/** 팔레트정지위치 */
	public final static String STACK_COL_USAGE_CD_PX 	= "PX";			 
	/** SLAB 보온카바위치 */
	public final static String STACK_COL_USAGE_CD_BK 	= "BK";			 
	/** SLAB CTC */
	public final static String STACK_COL_USAGE_CD_CT 	= "CT";			 
	/** SLAB Holding Bed */
	public final static String STACK_COL_USAGE_CD_HD 	= "HD";			 
	/** SLAB Roller Table */
	public final static String STACK_COL_USAGE_CD_RT 	= "RT";			 
	/** SLAB Walking Beam */
	public final static String STACK_COL_USAGE_CD_WB 	= "WB";			 
	/** SLAB Scafing 입측 */
	public final static String STACK_COL_USAGE_CD_SE 	= "SE";			 
	/** SLAB Scafing 출측 */
	public final static String STACK_COL_USAGE_CD_SD 	= "SD";			 
	/** SLAB 옥내이송적치장 */
	public final static String STACK_COL_USAGE_CD_31 	= "31";			 
	/** SLAB 정정작업대기장 */
	public final static String STACK_COL_USAGE_CD_32 	= "32";			 
	/** SLAB 압연지시대기장 */
	public final static String STACK_COL_USAGE_CD_33 	= "33";			 
	/** SLAB 동간보급준비장 */
	public final static String STACK_COL_USAGE_CD_34 	= "34";			 
	/** SLAB 압연보급대기장 */
	public final static String STACK_COL_USAGE_CD_35 	= "35";			 
	/** SLAB Take Out적치장 */
	public final static String STACK_COL_USAGE_CD_36 	= "36";			 
	/** SLAB WCR재 적치장 */
	public final static String STACK_COL_USAGE_CD_37 	= "37";			 
	/** SLAB 부두입고적치장 */
	public final static String STACK_COL_USAGE_CD_41 	= "41";			 
	/** SLAB 부두이송대기장 */
	public final static String STACK_COL_USAGE_CD_42 	= "42";			 
	/** SLAB Hand Scarfing 장 */
	public final static String STACK_COL_USAGE_CD_43 	= "43";			 
	/** SLAB Slab 절단장 */
	public final static String STACK_COL_USAGE_CD_44 	= "44";			 
	
	//==START========================================================================================
	/*
	 * 추가: 
	 * SPM Coil Scrap처리장 저장영역
	 * SPM Coil 보급 위치 ECC2
	 * */
	/** COIL SCRAP처리장 코드 */
	public final static String STACK_COL_USAGE_CD_SP    = "SP";			 
	/** COIL SPM보급위치 ECC2 */
	public final static String STACK_COL_USAGE_CD_K2 	= "K2";			 
	
	//==END==========================================================================================
	/* SLAB NEW 스케쥴종류 */
	/** Slab 부두야드 입고 */
	public final static String NEW_SCH_WORK_KIND_SYST = "SYST";		
	/** Slab 이송상차 */
	public final static String NEW_SCH_WORK_KIND_SVML = "SVML";	
	/** Slab 이송하차 */
	public final static String NEW_SCH_WORK_KIND_SVMU = "SVMU";	
	/** Slab 동내이적 */
	public final static String NEW_SCH_WORK_KIND_SYMM = "SYMM";	
	/** Slab 동내이적 */
	public final static String NEW_SCH_WORK_KIND_SYM2 = "SYM2";	
	/** Slab 동내이적 */
	public final static String NEW_SCH_WORK_KIND_SYM3 = "SYM3";	
	/** Slab 동간보급상차 */
	public final static String NEW_SCH_WORK_KIND_STSL = "STSL";		
	/** Slab 동간이적상차 */
	public final static String NEW_SCH_WORK_KIND_STML = "STML";	
	/** Slab 동간이적상차 */
	public final static String NEW_SCH_WORK_KIND_STM2 = "STM2";		
	/** Slab 대차하차(1) */
	public final static String NEW_SCH_WORK_KIND_STMU = "STMU";	
	/** Slab 대차하차(2) */
	public final static String NEW_SCH_WORK_KIND_STM4 = "STM4";		
	/** Slab Scarfing 보급 */
	public final static String NEW_SCH_WORK_KIND_SSLI = "SSLI";		
	/** Slab Scarfing 추출 */
	public final static String NEW_SCH_WORK_KIND_SSLO = "SSLO";		
	/** Slab Scarfing Take Out */
	public final static String NEW_SCH_WORK_KIND_SSTO = "SSTO";		
	/** Slab W/B 보급  */
	public final static String NEW_SCH_WORK_KIND_SWLI = "SWLI";		
	/** Slab CTC 보급 */
	public final static String NEW_SCH_WORK_KIND_SCLI = "SCLI";		
	/** Slab STE 비상보급 */
	public final static String NEW_SCH_WORK_KIND_SCL2 = "SCL2";		
	/** Slab W/B Take Out */
	public final static String NEW_SCH_WORK_KIND_SWTO = "SWTO";	 
	/** Slab H/B Line Off */
	public final static String NEW_SCH_WORK_KIND_SHLO = "SHLO";		
	/** Slab ROT Line Off */
	public final static String NEW_SCH_WORK_KIND_SRLO = "SRLO";		
	/** Slab ROT Line In */
	public final static String NEW_SCH_WORK_KIND_SRLI = "SRLI";		
	/** Slab Hand Scarfing 보급 */
	public final static String NEW_SCH_WORK_KIND_SHSI = "SHSI";		
	/** Slab Hand Scarfing 추출 */
	public final static String NEW_SCH_WORK_KIND_SHSO = "SHSO";	  
	/** Slab 시편재 보급 */
	public final static String NEW_SCH_WORK_KIND_SRPI = "SRPI";		
	/** Slab 시편재 추출 */
	public final static String NEW_SCH_WORK_KIND_SRPO = "SRPO";	  
	/** Slab 팔레트이적상차 */
	public final static String NEW_SCH_WORK_KIND_SPML = "SPML";	
	/** Slab 팔레트이적하차 */
	public final static String NEW_SCH_WORK_KIND_SPMU = "SPMU";	
	/** Slab 제품출하상차 */
	public final static String NEW_SCH_WORK_KIND_SVFL = "SVFL";		
	
	/*박판열연 SLAB Schedule Code*/
	/** A동 이송상차 */
	public final static String SCH_WORK_KIND_0APT01UM = "0APT01UM";	
	/** B동 이송상차 */
	public final static String SCH_WORK_KIND_0BPT01UM = "0BPT01UM";
	/** A동 이송하차 */
	public final static String SCH_WORK_KIND_0APT01LM = "0APT01LM";
	/** B동 이송상차 */
	public final static String SCH_WORK_KIND_0BPT01LM = "0BPT01LM";
	/** A동 동내이적 */
	public final static String SCH_WORK_KIND_0AYD01MM = "0AYD01MM";
	/** B동 동내이적 */
	public final static String SCH_WORK_KIND_0BYD01MM = "0BYD01MM";
	/** A동 동간이적 */
	public final static String SCH_WORK_KIND_0AYD01BM = "0AYD01BM";
	/** B동 동간이적 */
	public final static String SCH_WORK_KIND_0BYD01BM = "0BYD01BM";
	/** A동 위치삭제 */
	public final static String SCH_WORK_KIND_0AYD01DM = "0AYD01DM";
	/** B동 위치삭제 */
	public final static String SCH_WORK_KIND_0BYD01DM = "0BYD01DM";
	/** A동 강제이적 */
	public final static String SCH_WORK_KIND_0AYD01FM = "0AYD01FM";
	/** B동 강제이적 */
	public final static String SCH_WORK_KIND_0BYD01FM = "0BYD01FM";
	/** A동 Slab추가 */
	public final static String SCH_WORK_KIND_0AYD01RM = "0AYD01RM";
	/** B동 Slab추가 */
	public final static String SCH_WORK_KIND_0BYD01RM = "0BYD01RM";
	
	/* 공통 현재 진도코드 */
	/** 정정작업지시 */
	public final static String STOCK_STAT_D = "D";			
	/** 압연작업지시 */
	public final static String STOCK_STAT_E = "E";			
	/*
	public final static String CURR_PROG_CD_SLAB_1 = "1";	//생산예정		1S  
	public final static String CURR_PROG_CD_SLAB_3 = "3";	//생산종료		3S  
	public final static String CURR_PROG_CD_SLAB_A = "A";	//수입검사대기	AS  
	public final static String CURR_PROG_CD_SLAB_B = "B";	//이송지시대기	BS  
	public final static String CURR_PROG_CD_SLAB_C = "C";	//이송대기		CS  
	public final static String CURR_PROG_CD_SLAB_D = "D";	//정정작업대기	DS  
	public final static String CURR_PROG_CD_SLAB_E = "E";	//압연지시대기	ES  
	public final static String CURR_PROG_CD_SLAB_F = "F";	//압연작업대기	FS  
	public final static String CURR_PROG_CD_SLAB_K = "K";	//출하작업지시  	KS  
	public final static String CURR_PROG_CD_SLAB_L = "L";	//출하작업대기	LS  
	public final static String CURR_PROG_CD_SLAB_M = "M";	//출하완료		MS  
	public final static String CURR_PROG_CD_SLAB_Y = "Y";	//판정보류		YS  
	public final static String CURR_PROG_CD_SLAB_Z = "Z";	//충당대기		ZS  
	*/
	
	/** Slab구입등록(품질)				11 */
	public final static String CURR_PROG_CD_SLAB_0 	= "0";	  
	/** Slab구입확정(품질)				12 */
	public final static String CURR_PROG_CD_SLAB_1 	= "1";	 
	/** 생산종료						3S */
	public final static String CURR_PROG_CD_SLAB_3 	= "3";	 
	/** Slab정정작업대기/수입검사대기	AS */
	public final static String CURR_PROG_CD_SLAB_A 	= "A";	  
	/** 지시대기/이송지시대기			BS */
	public final static String CURR_PROG_CD_SLAB_B 	= "B";	 
	/** 작업대기/이송대기				CS   */
	public final static String CURR_PROG_CD_SLAB_C 	= "C";	
	/** 이송지시대기/정정작업대기		DS   */
	public final static String CURR_PROG_CD_SLAB_D 	= "D";	
	/** 이송작업대기/압연지시대기		ES */
	public final static String CURR_PROG_CD_SLAB_E 	= "E";	  
	/** 판정보류/압연작업대기			FS  */
	public final static String CURR_PROG_CD_SLAB_F 	= "F";	  
	/** 출하지시대기/출하작업지시  		KS   */
	public final static String CURR_PROG_CD_SLAB_K 	= "K";	
	/** 운송대기/출하작업대기			LS */
	public final static String CURR_PROG_CD_SLAB_L 	= "L";	  
	/** 출하완료/출하완료				MS  */
	public final static String CURR_PROG_CD_SLAB_M	= "M";	 
	/** 재공충당대기/판정보류			YS  */
	public final static String CURR_PROG_CD_SLAB_Y 	= "Y";	
	/** 제품충당대기/충당대기			ZS  */
	public final static String CURR_PROG_CD_SLAB_Z 	= "Z";	 
	/** 운송지시대기[신규추가]			NS */
	public final static String CURR_PROG_CD_SLAB_N 	= "N";	  
	/** 종합판정대기[신규추가]			GS */
	public final static String CURR_PROG_CD_SLAB_G 	= "G";	  
	/** 입고대기[신규추가]				HS*/
	public final static String CURR_PROG_CD_SLAB_H 	= "H";	 
	/** 반납대기[신규추가]				JS*/
	public final static String CURR_PROG_CD_SLAB_J 	= "J";	  
	
	/*
	public final static String CURR_PROG_CD_COIL_1 = "1";	//생산예정		1C  
	public final static String CURR_PROG_CD_COIL_3 = "3";	//생산종료		3C  
	public final static String CURR_PROG_CD_COIL_A = "A";	//재질판정대기	AC  
	public final static String CURR_PROG_CD_COIL_B = "B";	//정정작업지시  	BC  
	public final static String CURR_PROG_CD_COIL_C = "C";	//정정작업대기	CC  
	public final static String CURR_PROG_CD_COIL_D = "D";	//이송작업지시  	DC  
	public final static String CURR_PROG_CD_COIL_E = "E";	//이송작업대기	EC  
	public final static String CURR_PROG_CD_COIL_F = "F";	//판정보류		FC  
	public final static String CURR_PROG_CD_COIL_G = "G";	//종합판정대기	GC  
	public final static String CURR_PROG_CD_COIL_H = "H";	//입고대기		HG  
	public final static String CURR_PROG_CD_COIL_J = "J";	//반납 대기		JG  
	public final static String CURR_PROG_CD_COIL_K = "K";	//출하작업지시  	KG  
	public final static String CURR_PROG_CD_COIL_L = "L";	//출하작업대기	LG  
	public final static String CURR_PROG_CD_COIL_M = "M";	//출하완료		MG  
	public final static String CURR_PROG_CD_COIL_X = "X";	//경매대상선정	XG  
	public final static String CURR_PROG_CD_COIL_Y = "Y";	//재공충당대기	YG  
	public final static String CURR_PROG_CD_COIL_Z = "Z";	//제품충당대기	ZG  
	*/
	
	/** 생산예정		1C*/
	public final static String CURR_PROG_CD_COIL_1 	= "1";	  
	/** 생산종료		3C*/
	public final static String CURR_PROG_CD_COIL_3 	= "3";	  
	/** 재질판정대기	AC */
	public final static String CURR_PROG_CD_COIL_A 	= "A";	  
	/** 재질판정대기	AC */
	public final static String CURR_PROG_CD_COIL_R 	= "R";	 
	/** 정정작업지시  	BC */
	public final static String CURR_PROG_CD_COIL_B 	= "B";	  
	/** 정정작업대기	CC */
	public final static String CURR_PROG_CD_COIL_C 	= "C";	 
	/** 이송작업지시  	DC*/
	public final static String CURR_PROG_CD_COIL_D 	= "D";	  
	/** 이송작업대기	EC */
	public final static String CURR_PROG_CD_COIL_E 	= "E";	  
	/** 판정보류		FC*/
	public final static String CURR_PROG_CD_COIL_F 	= "F";	  
	/** 종합판정대기	GC */
	public final static String CURR_PROG_CD_COIL_G 	= "G";	  
	/** 입고대기		HG*/
	public final static String CURR_PROG_CD_COIL_H 	= "H";	 
	/** 반납 대기		JG*/
	public final static String CURR_PROG_CD_COIL_J 	= "J";	 
	/** 출하작업지시  	KG*/
	public final static String CURR_PROG_CD_COIL_K 	= "K";	  
	/** 출하작업대기	LG */
	public final static String CURR_PROG_CD_COIL_L 	= "L";	  
	/** 출하완료		MG*/
	public final static String CURR_PROG_CD_COIL_M 	= "M";	  
	/** 인도완료		PG  */
	public final static String CURR_PROG_CD_COIL_P 	= "P";	
	/** 운송지시대기	NG */
	public final static String CURR_PROG_CD_COIL_N 	= "N";	  
	/** 경매대상선정	XG */
	public final static String CURR_PROG_CD_COIL_X 	= "X";	
	/** 재공충당대기	YG */
	public final static String CURR_PROG_CD_COIL_Y 	= "Y";	
	/** 제품충당대기	ZG */
	public final static String CURR_PROG_CD_COIL_Z 	= "Z";	 
	
	
	/** I:입고 */
	public final static String SCRAP_CAUSE_GP_I 	= "I";			 
	/** B:보류 */
	public final static String SCRAP_CAUSE_GP_B 	= "B";		 
	/** S:Scrap */
	public final static String SCRAP_CAUSE_GP_S 	= "S";		 
	/** C:차공정 */
	public final static String SCRAP_CAUSE_GP_C 	= "C";		 
	/** J:재작업 */
	public final static String SCRAP_CAUSE_GP_J 	= "J";		 
	
	
	/** SCHEDULE 에서 검색 */
	public final static String SCH_WORK_LOC_DECISION_METHOD_S 	= "S"; 	 
	/** OPERATOR 지정위치 */
	public final static String SCH_WORK_LOC_DECISION_METHOD_O 	= "O"; 	 
	
	
	/** 대기 */
	public final static String WORK_PROG_STAT_W		= "W"; 
	/** UP지시 */
	public final static String WORK_PROG_STAT_1 	= "1"; 
	/** UP실적 */
	public final static String WORK_PROG_STAT_2 	= "2"; 
	/** PUT지시 */
	public final static String WORK_PROG_STAT_3 	= "3"; 
	
	
	/** 스케쥴등록 */
	public final static String SCH_WORK_STAT_S  	= "S"; 
	/** UP지시 */
	public final static String SCH_WORK_STAT_1  	= "1"; 
	/** UP실적 */
	public final static String SCH_WORK_STAT_2 		= "2"; 
	/** PUT지시 */
	public final static String SCH_WORK_STAT_3  	= "3"; 
	/** PUT실적 */
	public final static String SCH_WORK_STAT_4  	= "4"; 
	
	
	/**  */
	public final static String STACK_BED_ABLE_QNTY_1   = "1";
	/**적치Bed수량현재  */
	public final static String STACK_BED_QNTY_CURR_0   = "0"; 
	
	
	/** 종료구분 */
	public final static String WT_E_PROCESS	= "E";  
	
	
	/** F */
	public final static String CTS_RELAY_SECT_BAY_F = "F";
	/** A */
	public final static String CTS_RELAY_SECT_BAY_A = "A";
	/** C */
	public final static String CTS_RELAY_SECT_BAY_C = "C";
	
	
	/** SPM, HFL 구분 보급 */
	public final static String PROCESS_ID_1         = "1";  
	/** SPM, HFL 구분 취소 */
	public final static String PROCESS_ID_2         = "2";  
	/** SPM, HFL 구분 추출 */
	public final static String PROCESS_ID_3         = "3";  
	/** SPM, HFL 구분 Take-Out */
	public final static String PROCESS_ID_4         = "4";  
	/** SPM, HFL 구분 Take-In */
	public final static String PROCESS_ID_5         = "5";  
	
	
	public final static String LOCATION_1           = "1"; 
	public final static String LOCATION_2           = "2";
	public final static String LOCATION_3           = "3";
	public final static String LOCATION_4           = "4";
	public final static String LOCATION_5           = "5";
	
	
	/** 조업 위치 POSITION */
	public final static String PO_POSITION_D1 		= "D1";
	/** 조업 위치 POSITION */
	public final static String PO_POSITION_D5 		= "D5";
	
	
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_A_BAY_GP      = "2A0%";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_B_BAY_GP      = "2B0%";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_C_BAY_GP      = "2C0%";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_A_BAY_GP   = "2ATC%";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_B_BAY_GP   = "2BTC%";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_C_BAY_GP   = "2CTC%";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_BAY_GP_11  = "2CTC11";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_BAY_GP_12  = "2CTC12";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_BAY_GP_21  = "2CTC21";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_BAY_GP_22  = "2CTC22";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_BAY_GP_31  = "2CTC31";
	/**	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_TC_BAY_GP_32  = "2CTC32";
	
	
	/** 코일 공통 분기위치코드 상 HFL */
	public final static String COIL_PROC_CODE_1F	= "1F"; 
	/** 코일 공통 분기위치코드 하 HFL  */
	public final static String COIL_PROC_CODE_2F 	= "2F"; 
	/** 코일 공통 분기위치코드 SPM */
	public final static String COIL_PROC_CODE_3S 	= "3S"; 
	/** 코일 공통 분기위치코드 확장콘베이어 */
	public final static String COIL_PROC_CODE_4E 	= "4E"; 
	
	
	/** 냉각방법코드 수냉 */
	public final static String COIL_COOL_METHOD_W	= "W"; 
	/** 냉각방법코드 공냉 */
	public final static String COIL_COOL_METHOD_A 	= "A"; 
	
	
	/** 이송수단 대차 */
	public final static String HYSCO_TRANS_GP_C		= "C"; 
	/** 이송수단 차량 */
	public final static String HYSCO_TRANS_GP_T 	= "T"; 
	
	
	/** 적치기준코드 X-CD */
	public final static String STACK_RULE_CD_XCD	= "X-CD"; 
	/** 적치기준코드 Y-CD */
	public final static String STACK_RULE_CD_YCD 	= "Y-CD"; 
	
	
	/** 정상, TAKE IN */
	public final static String RESULT_MODE_0		= "0"; 
	/** 이상, TAKE OUT  */
	public final static String RESULT_MODE_1 		= "1"; 
	
	
	/** 반납 코드 현물반납 */
	public final static String RETURN_GP_1			= "1"; 
	/** 반납 코드 정보반납 */
	public final static String RETURN_GP_2 			= "2"; 
	
	
	/*	권상,권하 실적처리 방법 */ 
	/** 차상국 Auto 작업 */
	public final static String CRANE_FUNC_N		= "N";  
	/** 차상국 Manual 작업 */
	public final static String CRANE_FUNC_M		= "M"; 
	/** 지상국 작업 */
	public final static String CRANE_FUNC_L		= "L"; 
	/** 화면 BACK_UP */
	public final static String CRANE_FUNC_V		= "V"; 
	/** 산적위치 수정 */
	public final static String CRANE_FUNC_S		= "S"; 
	/** 비상조업 L2 시스템 처리 */
	public final static String CRANE_FUNC_U		= "U"; 
	/** 비상조업 L2 산적위치 수정 */
	public final static String CRANE_FUNC_B		= "B"; 
	
	/** 크레인 작업요구 */
	public final static String TC_WORK_R		= "R"; 
	/** 시스템 작업요구 */
	public final static String TC_WORK_I		= "I";  
	
	
	/*	코일 군관리  */ 
	/** 코일 군 1군*/
	public final static String COIL_GROUP_1			= "1"; 
	/** 코일 군 2군 */
	public final static String COIL_GROUP_2			= "2"; 
	/** 코일 군3군 */
	public final static String COIL_GROUP_3			= "3"; 
	/** 코일 군4군  */
	public final static String COIL_GROUP_4			= "4"; 
	/** 코일 군 5군 */
	public final static String COIL_GROUP_5			= "5"; 
	
	
	/*	HCR 구분 */ 
	/** HCR필수 */
	public final static String ORD_HCR_GP_V	= "V"; 
	/** HCR가능 */
	public final static String ORD_HCR_GP_H	= "H"; 
	/** WCR가능 */
	public final static String ORD_HCR_GP_W	= "W"; 
	/** CCR필수 */
	public final static String ORD_HCR_GP_C = "C"; 
	
	
	/*	SLAB지시행선 */ 
	/** B열연재 */
	public final static String SLAB_WO_RT_CD_HB = "HB"; 
	/** C열연재 */
	public final static String SLAB_WO_RT_CD_HC = "HC"; 
	/** 후판재 */
	public final static String SLAB_WO_RT_CD_PA = "PA";  
	
	
	/* 박판열연 SLAB야드 PALLET 정지위치 = 정상모드, 우천 모드 */
	/** 우천모드 */
	public final static String  A_SLAB_PALLET_R 	= "R";  
	/** 정상모드 */
	public final static String  A_SLAB_PALLET_N 	= "N";	
	
	/*
     *	G : 하단에 같은 장입순번이 있는 위치를 검색
     *	S : 하단에 같은 산적번호가 있는 위치를 검색
     *	E : 적치가능한 01단 위치를 검색
     *	P : 하단에 후순위의 장입순번이 있는 위치를 검색
     *	U : 적치가능한 02단 이상정보를 검색
     *	N : 하단에 장입순번이 없는 위치를 검색 
     *	A : 박판열연 Slab 야드 위치검색[두께,폭,길이 허용오차 체크]
     *  B : 박판열연 Slab 야드 위치검색[저장품이동조건 체크]
     *	G : 하단에 같은강종코드가 있는 위치를 검색
    */
	/** 하단에 같은 장입순번이 있는 위치를 검색 */
	public final static String SLAB_TO_LOC_G = "G"; 
	/** 하단에 같은 산적번호가 있는 위치를 검색 */
	public final static String SLAB_TO_LOC_S = "S"; 
	/** 적치가능한 01단 위치를 검색 */
	public final static String SLAB_TO_LOC_E = "E"; 
	/** 적치가능한 02단 이상정보를 검색 */
	public final static String SLAB_TO_LOC_U = "U"; 
	/** 하단에 후순위의 장입순번이 있는 위치를 검색 */
	public final static String SLAB_TO_LOC_P = "P";
	/** 하단에 장입순번이 없는 위치를 검색  */
	public final static String SLAB_TO_LOC_N = "N";
	/** 박판열연 Slab 야드 위치검색[두께,폭,길이 허용오차 체크] */
	public final static String SLAB_TO_LOC_A = "A";
	/** 박판열연 Slab 야드 위치검색[저장품이동조건 체크] */
	public final static String SLAB_TO_LOC_B = "B";
	/**  */
	public final static String SLAB_TO_LOC_K = "K";
	
	
	/**
	 * Error Log 정의 (MSG0000:내부인터페이스항목점검, MSG0001:강제메세지지정, MSG0002~MSG9999:Jspeed에 등록한 Message) 
	 * @author 이영근
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
	/** MSG0002 */
	public final static String MSG001 = "MSG0002";
	
	/** 저장영역에 등록된 정보가 존재하지 않습니다 */
	public final static String MSG_TO_01 = "저장영역에 등록된 정보가 존재하지 않습니다.";
	/** 적치가능 TO위치정보가 존재하지 않습니다 */
	public final static String MSG_TO_02 = "적치가능 TO위치정보가 존재하지 않습니다.";
	/** 대차설정정보로 인해 TO위치에 적치할 수 없습니다 */
	public final static String MSG_TO_03 = "대차설정정보로 인해 TO위치에 적치할 수 없습니다.";
	/** 하단저장품 작업예약으로 인해 TO위치에 적치할 수 없습니다 */
	public final static String MSG_TO_04 = "하단저장품 작업예약으로 인해 TO위치에 적치할 수 없습니다.";
	/** 작업을 할당할 크레인이 설정되어 있지 않습니다 */
	public final static String MSG_TO_05 = "작업을 할당할 크레인이 설정되어 있지 않습니다.";
	/** 할당된 크레인의 설정모드가 작업불가 상태입니다 */
	public final static String MSG_TO_06 = "할당된 크레인의 설정모드가 작업불가 상태입니다.";
	
	/* 지시구분 */
	/** 1:소재이송지시 */
    public final static String COIL_ORDER_GP_1	= "1";
    /** 2:제품이송지시 */
    public final static String COIL_ORDER_GP_2 	= "2";
    /** 3:B열연 출하지시 */
    public final static String COIL_ORDER_GP_3 	= "3";
    /** 1:장입지시 */
    public final static String SLAB_ORDER_GP_1 	= "1";
    /** 4:장입지시 */
    public final static String SLAB_ORDER_GP_4 	= "4";
    /** 2:이송지시 */
    public final static String SLAB_ORDER_GP_2 	= "2";
	
	
	//-------------------------------------------------------------------------------------------------
	// 	TC_CODE 상수 정의 시작
	//-------------------------------------------------------------------------------------------------
	
	/*------------------------   내부  TC   ---------------------*/
	/** B열연조업(PO) 송신 */
	/** 코일보급 및 보급취소 */
	public final static String   YMPOJ161  = "YMPOJ161";    //코일보급 및 보급취소
	                                                      
	//--박판열연조업(PO)                             
	/** 코일정보수신   */
	public final static String   POYMJ001  = "POYMJ001";    //코일정보수신   
	/** 코일 결번 실적  */
	public final static String   POYMJ002  = "POYMJ002";    //코일 결번 실적 
	/** 슬라브 Scarfing 출측 Line Off 요구   */
	public final static String   POYMJ003  = "POYMJ003";    //슬라브 Scarfing 출측 Line Off 요구 정보 수신
	/** 코일 SPM/HFL 작업 요구   */
	public final static String   POYMJ004  = "POYMJ004";    //코일 SPM/HFL 작업 요구 정보를 수신
	/** 슬라브 전단실적   */
	public final static String   POYMJ005  = "POYMJ005";    //슬라브 전단실적
	/** SLAB 결번실적   */
	public final static String   POYMJ006  = "POYMJ006";    //SLAB 결번실적
	/** 저장이동조건   */
	public final static String   POYMJ007  = "POYMJ007";    //저장이동조건
	/** 코일공냉재 실적   */
	public final static String   POYMJ008  = "POYMJ008";    //코일공냉재 실적
	/** 코일 SPM/HFL 작업 요구   */
	public final static String   POYMJ010  = "POYMJ010";    //코일 SPM/HFL 작업 요구 정보를 수신
	                                                      
	/** 관제(PC) 수신 */                                  
	public final static String   PCYM001   = "PCYM001";    //슬라브 장입예정번호취소   
	public final static String   PCYM002   = "PCYM002";    //슬라브 장입예정번호등록 
	public final static String   PCYM003   = "PCYM003";    //슬라브 미처리,반송 Slab 결번
	                                                    
	/** 구내운송(TS) 송신 */                              
	public final static String   YDTSJ007  = "YDTSJ007";   //상차개시실적
	public final static String   YDTSJ008  = "YDTSJ008";   //소재차량상차완료
	public final static String   YDTSJ009  = "YDTSJ009";   //하차개시실적
	public final static String   YDTSJ010  = "YDTSJ010";   //하차완료
	public final static String   YDTSJ011  = "YDTSJ011";   //소재차량Point지시
	                                                   
	/** 구내운송(TS) 수신 */                              
	public final static String   TSYDJ002  = "TSYDJ002";   //소재차량도착Point 요구
	public final static String   TSYDJ003  = "TSYDJ003";   //소재차량도착
	public final static String   TSYDJ004  = "TSYDJ004";   //소재차량출발
	public final static String   TSYDJ014  = "TSYDJ014";   //차량출발취소
	                                                   
	/** 생산통제(CT) 송신 */                              
	public final static String   YDCTJ032  = "YDCTJ032";   //B열연장입진행실적
	                                                   
	/** 생산통제(CT) 수신 */                              
	public final static String   CTYDJ032  = "CTYDJ032";   //슬라브 압연지시확정
	                                                   
	/** 연주조업(CS) 송신 */                              
	public final static String   YMCSJ001  = "YMCSJ001";   //슬라브 정정마감실적
	                                                   
	/** 연주조업(CS) 수신 */                              
	public final static String   CSYDJ001  = "CSYDJ001";   //슬라브 연주전단실적
	                                                   
	/** 진행관리(PT) 송신 */
	/** 슬라브소재이송완료실적 */
	public final static String   YDPTJ001  = "YDPTJ001";   //슬라브소재이송완료실적
	/** 코일소재이송완료실적 */
	public final static String   YDPTJ002  = "YDPTJ002";   //코일소재이송완료실적
	/** 코일소재임가공이송지시 */
	public final static String   YDPTJ003  = "YDPTJ003";   //코일소재임가공이송지시
	/** 냉연코일이송진행 상태실적 */
	public final static String   YDPTJ006  = "YDPTJ006";   //냉연코일이송진행 상태실적
	/** 재료단위 이송지시 취소 작업 */
	public final static String   YDPTJ007  = "YDPTJ007";   //재료단위 이송지시 취소 작업
	                                                   
	/** 진행관리(PT) 수신 */                              
	public final static String   PTYDJ001  = "PTYDJ001";   //코일충당실적
	public final static String   PTYDJ002  = "PTYDJ002";   //코일소재이송지시
	public final static String   PTYDJ003  = "PTYDJ003";   //코일소재임가공이송지시 
	                                                   
	/** 출하(DM) 송신 */                                 
	public final static String   YDDMR001  = "YDDMR001";   //코일입고작업실적
	public final static String   YDDMR003  = "YDDMR003";   //임가공입고작업실적
	public final static String   YDDMR004  = "YDDMR004";   //코일제품이적작업실적
	public final static String   YDDMR007  = "YDDMR007";   //코일출하상차개시
	public final static String   YDDMR009  = "YDDMR009";   //외판슬라브출하상차개시
	public final static String   YDDMR011  = "YDDMR011";   //코일일품출하상차실적
	public final static String   YDDMR013  = "YDDMR013";   //외판슬라브일품출하상차실적
	public final static String   YDDMR015  = "YDDMR015";   //코일출하상차완료
	public final static String   YDDMR017  = "YDDMR017";   //외판슬라브출하상차완료
	public final static String   YDDMR019  = "YDDMR019";   //코일제품고간이송상하차개시
	public final static String   YDDMR020  = "YDDMR020";   //임가공이송상하차개시
	public final static String   YDDMR021  = "YDDMR021";   //코일제품고간이송상하차완료
	public final static String   YDDMR022  = "YDDMR022";   //임가공이송상하차완료
	public final static String   YDDMR024  = "YDDMR024";   //HYSCO대차이송실적
	public final static String   YDDMR025  = "YDDMR025";   //HYSCO수냉실적
	public final static String   YDDMR026  = "YDDMR026";   //포인트점유사항 출하송신
	public final static String   YDDMR028  = "YDDMR028";   //차량입동지시
	public final static String   YDDMR029  = "YDDMR029";   //코일제품출하차량도착
	public final static String   YDDMR036  = "YDDMR036";   //검수완료
	public final static String   YDDMR050  = "YDDMR050";   //상차완료(야드 핸들링)
	public final static String   YDDMR070  = "YDDMR070";   //차량입동지시  
	public final static String   YDDMR071  = "YDDMR071";   //코일이송상차개시   
	public final static String   YDDMR072  = "YDDMR072";   //코일일품출하상차실적 송신
	public final static String   YDDMR074  = "YDDMR074";   //검수완료 PDA
	public final static String   YDDMR075  = "YDDMR075";   //코일이송하차개시 전송PDA
	public final static String   YDDMR076  = "YDDMR076";   //코일이송하차완료PDA
	                                                   
	/** 출하(DM) 수신 */                                 
	public final static String   DMYDR002  = "DMYDR002";   //코일제품보류확정  
	public final static String   DMYDR004  = "DMYDR004";   //외판슬라브출하지시대기 
	public final static String   DMYDR005  = "DMYDR005";   //코일제품출하지시대기
	public final static String   DMYDR008  = "DMYDR008";   //코일제품반납대기
	public final static String   DMYDR011  = "DMYDR011";   //코일제품고간이송지시 
	public final static String   DMYDR013  = "DMYDR013";   //외판슬라브목전(주문자변경)
	public final static String   DMYDR014  = "DMYDR014";   //코일제품목전 
	public final static String   DMYDR016  = "DMYDR016";   //외판슬라브운송지시대기
	public final static String   DMYDR020  = "DMYDR020";   //코일제품운송지시(삭제)
	public final static String   DMYDR022  = "DMYDR022";   //외판슬라브운송상차지시
	public final static String   DMYDR023  = "DMYDR023";   //코일제품상차지시(삭제) 
//	public final static String   DMYDR025  = "DMYDR025";   //임가공이송상차지시(삭제)
	public final static String   DMYDR026  = "DMYDR026";   //외판슬라브보관지시  
	public final static String   DMYDR027  = "DMYDR027";   //코일제품보관지시
	public final static String   DMYDR029  = "DMYDR029";   //외판슬라브출하완료 
	public final static String   DMYDR030  = "DMYDR030";   //코일제품출하완료
	public final static String   DMYDR032  = "DMYDR032";   //외판슬라브반품 
	public final static String   DMYDR033  = "DMYDR033";   //코일제품반품
//	public final static String   DMYDR035  = "DMYDR035";   //외판슬라브출하차량도착실적(삭제)
//	public final static String   DMYDR036  = "DMYDR036";   //코일제품출하차량도착실적(삭제)
//	public final static String   DMYDR037  = "DMYDR037";   //코일임가공차량도착실적(삭제)
	public final static String   DMYDR039  = "DMYDR039";   //외판슬라브출하차량출발실적
//	public final static String   DMYDR040  = "DMYDR040";   //코일제품출하차량출발실적(삭제)
//	public final static String   DMYDR041  = "DMYDR041";   //코일임가공차량출발실적(삭제)
	public final static String   DMYDR060  = "DMYDR060";   //코일제품운송상차지시
	public final static String   DMYDR070  = "DMYDR070";   //코일이송상차대기장도착PDA
	public final static String   DMYDR071  = "DMYDR071";   //코일이송상차도착PDA
	public final static String   DMYDR072  = "DMYDR072";   //코일이송상차완료PDA
	public final static String   DMYDR073  = "DMYDR073";   //코일이송하차대기장도착PDA
	public final static String   DMYDR074  = "DMYDR074";   //코일이송하차도착PDA
	public final static String   DMYDR075  = "DMYDR075";   //코일이송하차완료PDA
	                                                   
	/** 품질(QM) 송신 */                                 
	public final static String   YDQMJ002  = "YDQMJ002";   //열연정정입측보급실적
	                                                   
	/** B열연압연 송신 */                                 
	public final static String   CF1BP03   = "CF1BP03";    //슬라브 Line Off 완료실적
	public final static String   CF1BP04   = "CF1BP04";    //분기 Conveyor COIL Line Off 실적 
	public final static String   CF1BP05   = "CF1BP05";    //분기 Conveyor COIL Take Out 실적
	public final static String   CF1BP06   = "CF1BP06";    //분기 Conveyor COIL Take In 실적 
	public final static String   CF1BP12   = "CF1BP12";    //#3 CTC, #4 CTC Slab Loading  완료실적
	public final static String   CF1BP14   = "CF1BP14";    //슬라브 W/B 4,5 정보
	public final static String   CF1BP15   = "CF1BP15";    //Conveyor COIL Line In 실적 
	                                                   
	/** B열연압연 수신 */                                 
	public final static String   CF1PB11   = "CF1PB11";    //슬라브  Line Off Request 정보를 수신
	public final static String   CF1PB12   = "CF1PB12";    //코일 B열연 MILL COIL LINE OFF Request
	public final static String   CF1PB13   = "CF1PB13";    //코일 분기 Conveyor COIL Take Out 요구 정보
	public final static String   CF1PB14   = "CF1PB14";    //코일 분기 Conveyor COIL Take In 요구 정보
	public final static String   CF1PB16   = "CF1PB16";    //슬라브 #4 CTC LOADING 결과
	public final static String   CF1PB27   = "CF1PB27";    //슬라브 W/B Information Request 
	                                                   
	/** 코일야드L2 송신 */                                
	public final static String   YMA7L001  = "YMA7L001";   //저장위치제원
	public final static String   YMA7L002  = "YMA7L002";   //저장품제원
	public final static String   YMA7L004  = "YMA7L004";   //크레인작업지시
	public final static String   YMA7L005  = "YMA7L005";   //크레인작업실적응답
	public final static String   YMA7L006  = "YMA7L006";   //대차출발지시
	public final static String   YMA7L007  = "YMA7L007";   //작업현황응답
	public final static String   YMA7L008  = "YMA7L008";   //차량작업 예정정보
	public final static String   YMA7L009  = "YMA7L009";   //코일 압연실적정보
	public final static String   YMA7L010  = "YMA7L010";   //코일 분기 Conv To 확장 Conv 시점정보
	public final static String   YMA7L011  = "YMA7L011";   //코일 1냉연 대차이동요구
	public final static String   YMA7L012  = "YMA7L012";   //코일 확장 Conveyor Line Off응답
	                                                   
	/** 코일야드L2 수신 */                                
	public final static String   A7YML001  = "A7YML001";   //저장위치제원요구
	public final static String   A7YML002  = "A7YML002";   //저장품제원요구
	public final static String   A7YML003  = "A7YML003";   //설비운전모드전환
	public final static String   A7YML004  = "A7YML004";   //설비고장복구실적
	public final static String   A7YML007  = "A7YML007";   //크레인 작업지시요구
	public final static String   A7YML008  = "A7YML008";   //크레인 권상실적
	public final static String   A7YML009  = "A7YML009";   //크레인 권하실적
	public final static String   A7YML011  = "A7YML011";   //야드대차이동실적
	public final static String   A7YML012  = "A7YML012";   //강제권상요구
	public final static String   A7YML013  = "A7YML013";   //작업현황요구
	public final static String   A7YML014  = "A7YML014";   //스케쥴작업요구
	public final static String   A7YML015  = "A7YML015";   //크레인작업 가능유무응답
	public final static String   A7YML016  = "A7YML016";   //차량작업예정정보요구
	public final static String   A7YML017  = "A7YML017";   //상차도 작업불가
	public final static String   A7YML018  = "A7YML018";   //차량동간이적(도착)
	public final static String   A7YML019  = "A7YML019";   //자동이적 정보요구
	public final static String   A7YML020  = "A7YML020";   //코일 분기 Conveyor TrackIng 정보
	public final static String   A7YML021  = "A7YML021";   //코일 확장 Conveyor TrackIng 정보
	public final static String   A7YML022  = "A7YML022";   //코일 SPM1 TrackIng 정보
	public final static String   A7YML023  = "A7YML023";   //코일 SPM2 TrackIng 정보
	public final static String   A7YML024  = "A7YML024";   //코일 HFL  TrackIng 정보
	public final static String   A7YML025  = "A7YML025";   //코일 확장 Conv' Line Off 요구
	                                                   
	/** 1냉연 송신 */                                     
	public final static String   MIMH110   = "MIMH110";    //코일1냉연 대차상차실적
	public final static String   MIMH210   = "MIMH210";    //코일1냉연 대차상태정보
	public final static String   MIMH220   = "MIMH220";    //코일1냉연 대차이동정보
	public final static String   MIMH510   = "MIMH510";    //코일1냉연 코일상세정보
	                                                    
	/** 1냉연 수신 */                                     
	public final static String   MHMI110   = "MHMI110";    //코일1냉연 대차상차정보요구
	public final static String   MHMI220   = "MHMI220";    //코일1냉연 대차이동요구
	public final static String   MHMI310   = "MHMI310";    //코일1냉연 설비상태정보
	public final static String   MHMI510   = "MHMI510";    //코일1냉연 코일상세정보
	public final static String   MHMI710   = "MHMI710";    //코일1냉연 권상권하실적
	                                                   
	/** SLAB야드L2 송신 */                                
	public final static String   YMA8L001  = "YMA8L001";   //저장위치제원
	public final static String   YMA8L002  = "YMA8L002";   //저장품제원
	public final static String   YMA8L004  = "YMA8L004";   //크레인작업지시
	public final static String   YMA8L005  = "YMA8L005";   //크레인작업실적응답
	public final static String   YMA8L006  = "YMA8L006";   //대차출발지시
	public final static String   YMA8L007  = "YMA8L007";   //작업현황응답
	public final static String   YMA8L008  = "YMA8L008";   //차량작업 예정정보
	public final static String   YMA8L009  = "YMA8L009";   //슬라브 Scarfing 작업지시
	                                                   
	/** SLAB야드L2 수신 */                                
	public final static String   A8YML001  = "A8YML001";   //저장위치제원요구
	public final static String   A8YML002  = "A8YML002";   //저장품제원요구
	public final static String   A8YML003  = "A8YML003";   //설비운전모드전환
	public final static String   A8YML004  = "A8YML004";   //설비고장복구실적
	public final static String   A8YML007  = "A8YML007";   //크레인 작업지시요구
	public final static String   A8YML008  = "A8YML008";   //크레인 권상실적
	public final static String   A8YML009  = "A8YML009";   //크레인 권하실적
	public final static String   A8YML010  = "A8YML010";   //크레인 비상조업실적
	public final static String   A8YML011  = "A8YML011";   //야드대차이동실적
	public final static String   A8YML012  = "A8YML012";   //강제권상요구
	public final static String   A8YML013  = "A8YML013";   //작업현황요구
	public final static String   A8YML014  = "A8YML014";   //스케쥴작업요구
	public final static String   A8YML015  = "A8YML015";   //크레인작업 가능유무응답
	public final static String   A8YML016  = "A8YML016";   //차량작업예정정보요구
	public final static String   A8YML017  = "A8YML017";   //상차도 작업불가
	public final static String   A8YML018  = "A8YML018";   //차량동간이적(도착)
	public final static String   A8YML019  = "A8YML019";   //슬라브 Mill Salb loading Request(보급요구)
	public final static String   A8YML020  = "A8YML020";   //슬라브 W/B 장입요구
	public final static String   A8YML021  = "A8YML021";   //슬라브 CTC Tracking 정보
	public final static String   A8YML022  = "A8YML022";   //슬라브 Scaring 보급
	public final static String   A8YML023  = "A8YML023";   //슬라브 Scaring Take Out
	public final static String   A8YML024  = "A8YML024";   //슬라브 Scaring 실적
	public final static String   A8YML025  = "A8YML025";   //슬라브 Scaring 작업지시 재요구
	public final static String   A8YML026  = "A8YML026";   //슬라브 Scaring 추출 요구
	public final static String   A8YML027  = "A8YML027";   //슬라브 자동이적요구
	public final static String   A8YML028  = "A8YML028";   //슬라브 W/B Tracking 정보

	/** 내부인터페이스 정의	 */
	public final static String   YMYMJ001 = "YMYMJ001";    //공통 크레인작업지시요구YMYMJ001
	public final static String   YMYMJ201 = "YMYMJ202";    //SLAB 크레인 스케쥴 MAIN
	public final static String   YMYMJ301 = "YMYMJ302";    //COIL 크레인 스케쥴 MAIN
	
	/** 차량에 대한 설비 기본값  */
	public static final String YD_TS_CAR_EQP_ID				= "XXPT01";			//구내운송차량에 대한 기본 설비ID
	public static final String YD_DM_CAR_EQP_ID				= "XXPT02";			//출하차량에 대한 기본 설비ID
	
	/** 차량사용구분  출하,구내 운송 */
	public static final String YD_CAR_USE_GP_TS			    = "L";				// 구내운송
	public static final String YD_CAR_USE_GP_DM		        = "G";	            // 출하차량
	
	/** 야드차량생성시 사용되는 입동지시순번 기본값 */
	public static final String YD_BAYIN_WO_SEQ_DEFAULT		= "9";
	public static final String YD_STK_BED_WT_MAX_DEFAULT	= "100000";			//MAX높이 BED

	
	
	/**
	 * 
	 * 1. 크레인스케줄(최초생성)		: W
	 * 2. F1YF0007(명령선택)			: S
	 * 3. F1YF0015(크레인가능유무응답)	: 1 
	 * 4. F1YF0008(권상실적)			: 2
	 * 5. F1YF0009(권하실적)			: 4
	 * 
	 */
	/** 작업진행상태 명령선택대기 */
	public static final String YD_WRK_PROG_STAT_W		= "W";
	/** 작업진행상태 고장 */
	public static final String YD_WRK_PROG_STAT_B		= "B";
	/** 작업진행상태 명령선택지시 */
	public static final String YD_WRK_PROG_STAT_S		= "S";
	/** 작업진행상태 스케줄명령취소 */
	public static final String YD_WRK_PROG_STAT_C		= "C";
	/** 작업진행상태 권상작업지시 */
	public static final String YD_WRK_PROG_STAT_1		= "1";
	/** 작업진행상태 권상완료 - 권상실적시*/
	public static final String YD_WRK_PROG_STAT_2		= "2";
	/** 작업진행상태 권하작업지시 */
	public static final String YD_WRK_PROG_STAT_3		= "3";
	/** 작업진행상태 권하완료 - 권하실적시*/
	public static final String YD_WRK_PROG_STAT_4		= "4";
	/** 작업진행상태 강제권하 */
	public static final String YD_WRK_PROG_STAT_5		= "5";
	
	
	
	
	/** 설비진행상태 명령선택대기 */
	public static final String YD_EQP_PROG_STAT_W		= "W";
	/** 설비진행상태 고장 -복장복구실적시*/
	public static final String YD_EQP_PROG_STAT_B		= "B";
	/** 설비진행상태 명령선택지시 */
	public static final String YD_EQP_PROG_STAT_S		= "S";
	/** 설비진행상태 스케줄명령취소 */
	public static final String YD_EQP_PROG_STAT_C		= "C";
	/** 설비진행상태 권상작업지시 */
	public static final String YD_EQP_PROG_STAT_1		= "1";
	/** 설비진행상태 권상완료 -권상실적시*/
	public static final String YD_EQP_PROG_STAT_2		= "2";
	/** 설비진행상태 권하작업지시 */
	public static final String YD_EQP_PROG_STAT_3		= "3";
	/** 설비진행상태 권하완료 -권하실적시*/
	public static final String YD_EQP_PROG_STAT_4		= "4";
	/** 설비진행상태 강제권하 */
	public static final String YD_EQP_PROG_STAT_5		= "5";
	
	
	/** 적치가능한위치 -권하실적에서 UP위치 클리어시, 권상실적시*/
	public final static String STACK_LAYER_STAT_E	= "E"; 
	/** 적치불가 사용안함*/
//	public final static String STACK_LAYER_STAT_X	= "X"; 
	/** 적치불가 Slab에서만 사용함 */
	public final static String STACK_LAYER_STAT_V	= "V"; 
	/** 적치중 -슬라브용*/
//	public final static String STACK_LAYER_STAT_L	= "L"; 
	/** 스케쥴 예정 */
	public final static String STACK_LAYER_STAT_S	= "S"; 
	/** UP 스케쥴 수행 */
	public final static String STACK_LAYER_STAT_U	= "U"; 
	/** PUT 스케쥴 수행 사용안함*/
//	public final static String STACK_LAYER_STAT_P	= "P"; 
	/**적치중 -권하실적시 */
	public final static String STACK_LAYER_STAT_C	= "C"; 
	/** PUT 스케쥴 수행 -스케줄 편성시 */
	public final static String STACK_LAYER_STAT_D	= "D";
	
	
	
	
	/** 비활성화 - 영대차출발실적시 출발위치 비활성 */  
	public final static String STACK_LAYER_ACTIVE_STAT_C 	= "C";
	/** 활성화  - 슬라브용*/  
	public final static String STACK_LAYER_ACTIVE_STAT_O 	= "O";
	/** 활성화  - 코일용 */ 
	public final static String STACK_LAYER_ACTIVE_STAT_E 	= "E";
	/** 사용불가 */ 
	public static final String STACK_LAYER_ACTIVE_STAT_N    = "N"; //2단적치불가, 사용불가
	
	
	
	
	/** 비활성화 - 영대차출발실적시 출발위치 비활성 */  
	public final static String YD_STK_BED_ACTIVE_STAT_C 	= "C";
	/** 활성화 */  
	public final static String YD_STK_BED_ACTIVE_STAT_L 	= "L";
	/** 사용불가 */  
	public final static String YD_STK_BED_ACTIVE_STAT_N 	= "N";
	
	
	
	/** 상차대기 */
	public final static String YD_EQP_WRK_STAT_0	= "0";
	/** 상차출발 */
	public final static String YD_EQP_WRK_STAT_1	= "1";
	/** 상차도착 */
	public final static String YD_EQP_WRK_STAT_2	= "2";
	/** 상차검수 */
	public final static String YD_EQP_WRK_STAT_3	= "3";
	/** 상차개시 */
	public final static String YD_EQP_WRK_STAT_4	= "4";
	/** 상차완료 */
	public final static String YD_EQP_WRK_STAT_5	= "5";
	/** 하차출발 */
	public final static String YD_EQP_WRK_STAT_A	= "A";
	/** 하차도착 */
	public final static String YD_EQP_WRK_STAT_B	= "B";
	/** 하차검수 */
	public final static String YD_EQP_WRK_STAT_C	= "C";
	/** 하차개시 */
	public final static String YD_EQP_WRK_STAT_D	= "D";
	/** 하차완료 */
	public final static String YD_EQP_WRK_STAT_E	= "E";
	/** 공차 */
	public final static String YD_EQP_WRK_STAT_U	= "U";
	/** 영차 */
	public final static String YD_EQP_WRK_STAT_L	= "L";
	
	
	
	/** 상차대기 */
	public final static String YD_CAR_PROG_STAT_0	= "0";
	/** 상차출발 */
	public final static String YD_CAR_PROG_STAT_1	= "1";
	/** 상차도착 */
	public final static String YD_CAR_PROG_STAT_2	= "2";
	/** 상차검수 */
	public final static String YD_CAR_PROG_STAT_3	= "3";
	/** 상차개시 */
	public final static String YD_CAR_PROG_STAT_4	= "4";
	/** 상차완료 */
	public final static String YD_CAR_PROG_STAT_5	= "5";
	/** 하차출발 */
	public final static String YD_CAR_PROG_STAT_A	= "A";
	/** 하차도착 */
	public final static String YD_CAR_PROG_STAT_B	= "B";
	/** 하차검수 */
	public final static String YD_CAR_PROG_STAT_C	= "C";
	/** 하차개시 */
	public final static String YD_CAR_PROG_STAT_D	= "D";
	/** 하차완료 */
	public final static String YD_CAR_PROG_STAT_E	= "E";
	/** 공차 */
	public final static String YD_CAR_PROG_STAT_U	= "U";
	/** 영차 */
	public final static String YD_CAR_PROG_STAT_L	= "L";
	
	/** on line */
	public final static String YD_EQP_WRK_MODE_1_ONLINE	= "1";
	/** off line */
	public final static String YD_EQP_WRK_MODE_2_OFFLINE	= "2";
	
	
	/** 운송장비적재능력(TSYDJ003) */
	public final static String TRN_EQP_STK_CAPA = "80000";
	
	/**1EYD99MM	분동코일보급추출*/
	public final static String SCH_CODE_WFB_COIL_TAKEINOUT = "1EYD99MM";

	
	/** 야드설비작업Mode2 무인-A */
	public final static String YD_EQP_WRK_MODE2_A = "A";
	/** 야드설비작업Mode2 리모컨-R */
	public final static String YD_EQP_WRK_MODE2_R = "R";
	/** 야드설비작업Mode2 정비-E*/
	public final static String YD_EQP_WRK_MODE2_E = "E";
	/** 야드설비작업Mode2 유인-M*/
	public final static String YD_EQP_WRK_MODE2_M = "M";	

	/**
	 * 코일 SPM/HFL/EQL 작업 요구 정보(POYMJ004) 설비 HFL 구분자
	 */
	public final static String EQP_WORK_ID_HFL = "H";
	/**
	 * 코일 SPM/HFL/EQL 작업 요구 정보(POYMJ004) 설비 SPM 구분자
	 */
	public final static String EQP_WORK_ID_SPM = "S";
	/**
	 * 코일 SPM/HFL/EQL 작업 요구 정보(POYMJ004) 설비 EQL 구분자
	 */
	public final static String EQP_WORK_ID_EQL = "E";

	/**
	 * 코일 SPM/HFL/EQL 작업 요구 정보(POYMJ004) 설비 EQL 구분자
	 */
	public final static String EQP_WORK_ID_EQL_161 = "N";
	
	/**
	 * L3 HFL : H
	 */
	public final static String EQP_WORK_ID_L3_HFL = "F";
	/**
	 * L3 SPM : S
	 */
	public final static String EQP_WORK_ID_L3_SPM = "K";
	/**
	 * L3 EQL : Q
	 */
	public final static String EQP_WORK_ID_L3_EQL = "Q";
	
	/** TB_YD_CARPOINT.YD_STK_COL_ACT_STAT */
	/** R : 예약 */  
	public final static String YD_STK_COL_ACT_STAT_R 	= "R";
	/** C : 사용가능 */  
	public final static String YD_STK_COL_ACT_STAT_C 	= "C";
	/** L : 사용중 */ 
	public final static String YD_STK_COL_ACT_STAT_L 	= "L";
	/** N : 사용불가 */ 
	public static final String YD_STK_COL_ACT_STAT_N    = "N";

	/** 새들상태변경 : 2 */
	public static final String CTS_CHG_SADDLE_STATUS_2 = "2";
	
	/** 목적동 변경 : 3 */
	public static final String CTS_CHG_AIM_DONG_3 = "3";
	
	/** 작업순서변경 : 4 */
	public static final String CTS_CHG_WORK_ORDER_4 = "4";
	
	/** 대차초기화 : 8 */
	public static final String CTS_CHG_RESET_8 = "8";
	
	/** 작업실적요구 : 6 */
	public static final String CTS_REQ_WORK_RESULT_6 = "6";
	
}


