/**
 * 
 * @(#)YmCommonConst
 * 
 * @version    :
 * @author     : 
 * @date       : 
 *
 * @description :
 * 
 */
package com.inisteel.cim.ym.common;

public class YmCommonConst{
    /* 관제 상태 코드 */
    public final static String PC_WORK_STAT_C = "C";	//Reschedule 확정
    public final static String PC_WORK_STAT_D = "D";	//동간보급 요구(야드 이적 작업)
    public final static String PC_WORK_STAT_E = "E";	//동간보급 이동 중(야드 대차 이동)
    public final static String PC_WORK_STAT_F = "F";	//W/B Loading 대기(대차 도착, 야드 적치)
    public final static String PC_WORK_STAT_G = "G";	//W/B Loading 요구(W/B Loading Schedule)
    
    /* 적치 가능 관련 */
    public final static String YM_DEFAULT_WLOC_CD = "XXPTXX";
    public final static String YM_DEFAULT_PNT_CD = "0000";
    
    /* 적치 가능 관련 */
    public final static String STACK_BED_ABLE_QNTY_0 = "0";
    public final static String STACK_BED_QNTY_CURR_1 = "1";
    
    /* SKID NO */
    public final static String SKID_NO_1 = "1";//1STD
    public final static String SKID_NO_2 = "2";//2STD
    public final static String SKID_NO_3 = "3";//3STD
    public final static String SKID_NO_4 = "4";//4STD
    public final static String SKID_NO_5 = "5";
    
    /* 야드맵 정보 송신/수신 구분 */
    public final static String SEND_REQ_R = "R";//요구
    public final static String SEND_REQ_A = "A";//응답
    
    /* 사용유무 */
    public final static String USE_YN_0 = "0";//사용안함
    public final static String USE_YN_Y = "Y";//사용
    public final static String USE_YN_N = "N";//사용안함
    
    /* 저장품 DELETE MARK */
    public final static String DELETE_STOCK = "Y";
    //==START========================================================================================
    /*
	 * [ Static 상수 추가 : (2009.01.20 KBK)]
	 * 
     *  B열연 Coil의 SPM 보급 구분(#2 SPM 추가)
     */
    public final static String NEW_WORK_SPM_O = "S";// B열연 #1 SPM 작업
    public final static String NEW_WORK_SPM_N = "N";// B열연 #2 SPM 작업
    public final static String NEW_WORK_HFL_F = "F";// B열연 #2 HFL 작업
    //==END==========================================================================================
    //==START========================================================================================
	//야드 모니터링 채널
	//public static final String YD_MONITORING_CHANNEL_01					= "yd_monitor01";
	
	public static final String YD_MONITORING_CHANNEL_3					= "yd_monitor3";
	
	// 야드 Event 구분 (C:크레인, Q:설비, E:에러, W:경고, I:정보, Z:기타)
	// putLogMsg Method Parameter 설정
	public static final String YD_EVT_CRANE                  = "C";
	public static final String YD_EVT_EQP                    = "Q";
	public static final String YD_EVT_ERROR                  = "E";
	public static final String YD_EVT_WARNING                = "W";
	public static final String YD_EVT_INFO                   = "I";
	public static final String YD_EVT_ETC                    = "Z";
	
	//==END==========================================================================================
    /* A열연 보급 구분 작업 */
	public final static String WORK_SPM_E = "E";//EQL보급
    public final static String WORK_SPM_S = "S";//SPM보급
    public final static String WORK_HFL_H = "H";//HFL보급
    public final static String WORK_HFL_S = "D";//HFL 결속대 보급
    public final static String SUPPLY_1   = "1";//보급

    public final static String WORK_SPM_1 = "1";//SPM입측 1번 
    public final static String WORK_SPM_2 = "2";//SPM입측 2번
    public final static String WORK_SPM_5 = "5";//SPM입측 5번
    public final static String WORK_SPM_6 = "6";//SPM입측 6번
    public final static String WORK_SPM_7 = "7";//SPM입측 7번
    
    public final static String WORK_HFL_OUT_FD  = "FD"; //HFL 출측
    public final static String WORK_HFL_IN_FE   = "FE"; //HFL 입측    
    public final static String WORK_SPM_OUT_KD  = "KD"; //SPM 출측
    public final static String WORK_SPM_IN_KE   = "KE"; //SPM 입측
    public final static String WORK_EQL_OUT_QD  = "QD"; //SPM 출측
    public final static String WORK_EQL_IN_QE   = "QE"; //SPM 입측
        
    /* 열연계획작업코드(공통에서 사용 중인 공정) */
    //AB열연 
//    public final static String SHEAR_SUPPLY_GP_2K  = "2K"; //B열연 SPM
//    public final static String SHEAR_SUPPLY_GP_4K  = "4K"; //A열연 SPM
//    public final static String SHEAR_SUPPLY_GP_2H  = "2H"; //A열연 HFL
//    public final static String SHEAR_SUPPLY_GP_4H  = "4H"; //B열연 HFL    
//    public final static String SHEAR_SUPPLY_GP_2T  = "2T"; //B열연 수냉재    
//    public final static String SHEAR_SUPPLY_GP_2A  = "2A"; //B열연 공냉재    

    //일관제철
    public final static String SHEAR_SUPPLY_GP_5K  = "5K"; //B열연 SPM
    public final static String SHEAR_SUPPLY_GP_1K  = "1K"; //A열연 SPM 
    public final static String SHEAR_SUPPLY_GP_1Q  = "1Q"; //A열연 EQL
    public final static String SHEAR_SUPPLY_GP_1H  = "1H"; //A열연 HFL 
    public final static String SHEAR_SUPPLY_GP_5H  = "5H"; //B열연 HFL
    public final static String SHEAR_SUPPLY_GP_5T  = "5T"; //B열연 수냉재    
    public final static String SHEAR_SUPPLY_GP_5A  = "5A"; //B열연 공냉재 
    // SPM2에 대한 코드 추가
    // 최규성 2009-10-05
    // 6K, 6H 추가. 최규성 2009-12-11
    public final static String SHEAR_SUPPLY_GP_5N  = "5N"; //B열연 SPM2
    public final static String SHEAR_SUPPLY_GP_6K  = "6K"; //B열연 SPM2
    public final static String SHEAR_SUPPLY_GP_6H  = "6H"; //B열연 HFL결속장
    
    
    /* A열연 Cran No */
    public final static String A_CraneNo_FCR1 = "FCR1"; //FCR1 Crane No
    public final static String A_CraneNo_FCR2 = "FCR2"; //FCR2 Crane No
	
    /* 지시구분 */
    public final static String COIL_ORDER_GP_1 = "1";//1:소재이송지시
    public final static String COIL_ORDER_GP_2 = "2";//2:제품이송지시
    public final static String COIL_ORDER_GP_3 = "3";//3:B열연 출하지시
    public final static String SLAB_ORDER_GP_1 = "1";//1:장입지시
    public final static String SLAB_ORDER_GP_4 = "4";//4:장입지시
    public final static String SLAB_ORDER_GP_2 = "2";//2:이송지시
    
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

    
    /* 이송상차, 하차 FLAG */
    public final static String LOAD_1 	= "1"; //이송상차
    public final static String UNLOAD_2 = "2"; //이송하차
    public final static String UNLOAD_3 = "3"; //이적하차
    
    /* 출하, 이송, 이적 구분 */
    public final static String MOVE_GP_1 = "1"; //출하
    public final static String MOVE_GP_2 = "2"; //이송
    public final static String MOVE_GP_3 = "3"; //이적
    
    /* 지시/취소 구분 */
    public final static String ORDER_GP_1 = "1"; //지시
    public final static String ORDER_GP_2 = "2"; //취소
    public final static String ORDER_GP_3 = "3"; //완료
    
    /* 차량구분 */
    public final static String CAR_GP_1	= "1";//반입
    public final static String CAR_GP_2	= "2";//출하    
    public final static String CAR_GP_U	= "U";//상차
    public final static String CAR_GP_D	= "D";//도착,하차
    public final static String CAR_GP_S	= "S";//출발
    //==START========================================================================================
    /*
	 * [ Static 상수 추가 : (2009.01.20 KBK)]
	 * 
     * 동간 이적 차량 Card No (9999 ~ 9990)
    */
    public final static String CAR_BAY_TRANS_CARD_NO_1="9999";
    public final static String CAR_BAY_TRANS_CARD_NO_2="9998";
    public final static String CAR_BAY_TRANS_CARD_NO_3="9997";
    public final static String CAR_BAY_TRANS_CARD_NO_4="9996";
    public final static String CAR_BAY_TRANS_CARD_NO_5="9995";
   
    //==END==========================================================================================
    /* 주/보조작업 구분 */
    public final static String SUB 	= "SUB";
    public final static String MAIN = "MAIN";
    
    /* 로그 유형 */
    public final static String LOG_L = "L";//LOG
    public final static String LOG_W = "W";//Warning
    public final static String LOG_E = "E";//Error
    
    /* 야드구분 */
    public final static String YD_GP_A = "A"; //A열연 COIL 야드
    public final static String YD_GP_B = "B"; //B열연 COIL 야드
    public final static String YD_GP_0 = "0"; //A열연 SLAB 야드
    public final static String YD_GP_1 = "1"; //A열연 COIL 야드
    public final static String YD_GP_2 = "2"; //B열연 SLAB 야드
    public final static String YD_GP_3 = "3"; //B열연 COIL 야드
    public final static String YD_GP_4 = "4"; //부드야드
    
    /* A열연 동구분 */
    public final static String BAY_GP_A   = "A";
    public final static String BAY_GP_B   = "B";
    public final static String BAY_GP_C   = "C";
    public final static String BAY_GP_D   = "D";
    public final static String BAY_GP_E   = "E";
    public final static String BAY_GP_F   = "F";
    public final static String BAY_GP_G   = "G";
    public final static String BAY_GP_H   = "H";
    public final static String BAY_GP_I   = "I";
    
    /* B열연 동구분 */
    public final static String B_BAY_GP_1 = "1";
    public final static String B_BAY_GP_2 = "2";
    public final static String B_BAY_GP_3 = "3";
    public final static String B_BAY_GP_4 = "4";
    public final static String B_BAY_GP_5 = "5";
    public final static String B_BAY_GP_6 = "6";
    public final static String B_BAY_GP_7 = "7";
        
    /* 고장/복구분 */
    public final static String TRO_REC_0 = "0";	//복구
	public final static String TRO_REC_1 = "1";	//고장
    public final static String TRO_REC_O = "O";	//복구
    public final static String TRO_REC_C = "C";	//고장
    public final static String TRO_REC_E = "E";	//자리비움
	public final static String TRO_REC_9 = "9";	//경미한 작업 불가
	
	/* 운전모드/시스템모드 */
	public final static String MODE_0 = "0";	//OFF
	public final static String MODE_1 = "1";	//ON
	public final static String MODE_2 = "2";	//자리비움
	public final static String MODE_C = "C";	//OFF
	public final static String MODE_O = "O";	//ON
	public final static String MODE_E = "E";	//자리비움
	
    /* 코일유무 */
    public final static String COIL_YN_N = "0";
    public final static String COIL_YN_Y = "1";

    /* 작업진행상태 */
	public final static String WPROG_STAT_W	= "W";	//IDEL, Schedule  대기
	public final static String WPROG_STAT_T	= "T";	//출발지시
	public final static String WPROG_STAT_M	= "M";	//이동 중
	public final static String WPROG_STAT_S	= "S";	//Schedule 수행
	public final static String WPROG_STAT_R	= "R";	//작업 중
		
	public final static String SLAB_STOCK_STAT_A	= "A"; //구입검사대기
	
	/* 설비상태 */
	public final static String EQUIP_STAT_O = "O";	//정상
	public final static String EQUIP_STAT_C = "C";	//고장
	public final static String EQUIP_STAT_X = "X";	//사용금지
	
	/* 적재상태 */
	public final static String STACK_STAT_L = "L";	//영차
	public final static String STACK_STAT_U = "U";	//공차
	public final static String STACK_STAT_I = "I";	//IDLE
	
	/* 휴지코드 */
	public final static String DOWN_CD_0000 = "0000"; //정상
	
	/* 2매작업 가능 구분 */
	public final static String GRIP_LOT_YN_T = "T"; //최상단만작업
	public final static String GRIP_LOT_YN_G = "G"; //2매작업
	
	/* 상차스케쥴 지정 구분 */
	public final static String CARLOAD_ASSIGN_GP_Y = "Y";//상차
	
	/* 상차지정유무 */
	public final static String CARLOAD_ASSIGN_Y   = "Y"; //지정
	public final static String CARLOAD_ASSIGN_N   = "N"; //미지정
	
	/* 스케쥴기준 활성 상태 */
	public final static String SCH_RULE_ACTIVE_STAT_A = "A"; //기준
	public final static String SCH_RULE_ACTIVE_STAT_B = "B"; //BackUp
	public final static String SCH_RULE_STAT_X        = "X"; //Schedule금지
	
	/* 중계구역사용여부 */
	public final static String CTS_RELAY_GP_Y = "Y"; //사용
	public final static String CTS_RELAY_GP_N = "N"; //미사용

	/* A열연 CNOVEYOR LINE */
	public final static String CONVEYOR_LINE_0 	 	= "0";
	public final static String CONVEYOR_LINE_1 	 	= "1";
	public final static String CONVEYOR_LINE_2 	 	= "2";
	public final static String CONVEYOR_LINE_3 	 	= "3";
	public final static String CONVEYOR_LINE_4 	 	= "4";
	public final static String CONVEYOR_LINE_5 	 	= "5";
	public final static String CONVEYOR_LINE_ASTA 	= "*";
	public final static String CONVEYOR_LINE_ALL 	 = "ALL";
	public final static String CONVEYOR_LINE_0_LHCVO = "LHCVO";
	public final static String CONVEYOR_LINE_1_LHFPI = "LHFPI";
	public final static String CONVEYOR_LINE_2_LSPMI = "LSPMI";
	public final static String CONVEYOR_LINE_3_LSH1I = "LSH1I";
	public final static String CONVEYOR_LINE_4_LSH2I = "LSH2I";
	
	/* A열연 CNOVEYOR STACK COL */
	//stack_col_gp like '__FE%' or stack_col_gp like '__FD%' or stack_col_gp like '__KE%' or stack_col_gp like '__KD%'
	
	public final static String HFL_COL_1BFE = "1BFE";//HFL
	public final static String HFL_COL_1CFD = "1CFD";//HFL
	public final static String SPM_COL_1DKE = "1DKE";//SPM
	public final static String SPM_COL_1EKE = "1EKE";//SPM
	public final static String SPM_COL_1EKD = "1EKD";//SPM
	public final static String SPM_COL_1FKD = "1FKD";//SPM
	public final static String EQL_COL_1FQE = "1EQE";//EQL
	public final static String EQL_COL_1FQD = "1FQD";//EQL
	public final static String EQL_COL_1GQD = "1GQD";//EQL
	public final static String Roll_COL_1BDC = "1BDC";//압연
	public final static String Roll_COL_1CDC = "1CDC";//압연		
	public final static String SPM_COL_3CKE = "3CKE";
	public final static String SPM_COL_3BKE = "3BKE";
	public final static String SPM_COL_3BKD = "3BKD";
	public final static String SPM_COL_3AKD = "3AKD";
	public final static String HFL_COL_3AFE = "3AFE";
	public final static String HFL_COL_3BFE = "3BFE";
	public final static String HFL_COL_3CFD = "3CFD";
	//==START========================================================================================
	/*
	 * [ Static 상수 추가 최규성]
	 * 
 	 * 신규 SPM(#2 SPM) 관련 설비정보
     *   - 입측 : 3DKE, 출측 : 3EKD
     */
	public final static String SPM_COL_3DKE = "3DKE";
	public final static String SPM_COL_3EKE = "3EKE";
	public final static String SPM_COL_3EKD = "3EKD";
	/*
	 * 신규SPM 내의 HFL 설비 정보
	 * 최규성
	 */
	public final static String HFL_COL_3EFE = "3EFE";
	public final static String HFL_COL_3EFD = "3EFD";
	

	//==END==========================================================================================	
	
	/* B열연 SCARFING COL */
	public final static String SCARFING_Y = "Y";
	public final static String SCARFING_N = "N";
	public final static String SCARFING_COL_2ESE = "2ESE";
	public final static String SCARFING_COL_2ESD = "2ESD";
			
	/* 전문 상수 */
	public final static String FORM_I = "I";
	public final static String FORM_R = "R";
	
	/* 압연실적정보 송신시 KEY 항목*/
	public final static String KEY_LHCVO = "LHCVO";
	
	/* CTS 설비번호/MPA */
	public final static String CTS_GP_1XTC01 = "1XTC01";
	public final static String CTS_GP_1XTC02 = "1XTC02";
	
	/* 확장대차 설비번호  */
	public final static String CTS_GP_1XTC03 = "1XTC03";
	
	/* HYSCO 설비번호/MPA */
	public final static String HYSCO_3HTC03 = "3HTC03";
	public final static String HYSCO_3HTC02 = "3HTC02";
	public final static String HYSCO_3XTC02 = "3XTC02";
	//==START========================================================================================
	// CGS 추가. 
	public final static String HFL_3XTC01 = "3XTC01";		// HFL 대차
	public final static String NEW_TC_3XTC03 = "3XTC03";	// 신규대차 #1
	public final static String NEW_TC_3XTC04 = "3XTC04";	// 신규대차 #2
	public final static String NEW_TC_3XTC05 = "3XTC05";	// 신규대차 #3
	//==END==========================================================================================

	/* 압연실적정보 송신시 GROUP 항목*/
	public final static String GROUP_2 = "2";
	
	public final static String PLANT_GP_A					= "A";
	public final static String PLANT_GP_H					= "H";
	
	/* 작업 구분 수동,자동 구분 */
	public final static String WORK_GP_A					= "A"; // 자동
	public final static String WORK_GP_H					= "H"; // 수동
	
	public final static String GBN_MIN 						= "MIN";
	public final static String GBN_MAX 					= "MAX";
	public final static String GBN_J05 					= "J05";
	
	public final static String STACK_BED_GP_01 	 			= "01";
	public final static String STACK_BED_GP_02 	 			= "02";
	public final static String STACK_BED_GP_03 	 			= "03";
	public final static String STACK_BED_GP_04 	 			= "04";
	public final static String STACK_BED_GP_05 	 			= "05";
	//================================================================================================
	// SPM2 추출 Position 고정으로 인한 상수값 추가 
	// 최규성 2010-01-26
	public final static String STACK_BED_GP_21 	 			= "21";
	public final static String STACK_BED_GP_22 	 			= "22";
	public final static String STACK_BED_GP_23 	 			= "23";
	public final static String STACK_BED_GP_24 	 			= "24";
	public final static String STACK_BED_GP_25 	 			= "25";
	public final static String STACK_BED_GP_26 	 			= "26";

	public final static String NEW_BAK_STACK_BED_START 		= "25";
	public final static String TMP_STACK_COL_RULE_X_AXIS	= "9999";
	public final static String TMP_STACK_COL_RULE_Y_AXIS 	= "9999";

	//================================================================================================
	public final static String STACK_LAYER_GP_01 			= "01";
	public final static String STACK_LAYER_GP_02 			= "02";
	public final static String STACK_LAYER_GP_03 			= "03";
	public final static String STACK_LAYER_GP_04 			= "04";
	public final static String STACK_COL_GP_1ATC03 		= "1ATC03";
	public final static String STACK_COL_GP_1BTC03 		= "1BTC03";
	public final static String STACK_COL_GP_1BDC01 		= "1BDC01";
	public final static String STACK_COL_GP_2ESE01 		= "2ESE01"; //scarfing 입측
	public final static String STACK_COL_GP_2ESD01 		= "2ESD01"; //scarfing 출측
	public final static String STACK_COL_GP_1CDC01 		= "1CDC01";
	public final static String STACK_COL_GP_3XDC01 		= "3XDC01";
	public final static String STACK_COL_GP_1CDC02          	= "1CDC02";

	public final static String STACK_COL_GP_3AST01          = "3AST01";
	public final static String STACK_COL_GP_3BST02          = "3BST02";
	public final static String STACK_COL_GP_3CST03          = "3CST03";
	public final static String STACK_COL_GP_3CEX01          = "3CEX01";
	public final static String STACK_COL_GP_3BTT01          = "3BTT01";
	public final static String STACK_COL_GP_3BTT02          = "3BTT02";
	public final static String STACK_COL_GP_3BSC01          = "3BSC01";
	public final static String STACK_COL_GP_1FKD01          = "1FKD01";
	public final static String STACK_COL_GP_3AKD01          = "3AKD01";
	public final static String STACK_COL_GP_3DHS01          = "3DHS01";
	public final static String STACK_COL_GP_1CFD01          = "1CFD01";
	public final static String STACK_COL_GP_3CFD01          = "3CFD01";
	public final static String STACK_COL_GP_1EKE01          = "1EKE01";
	public final static String STACK_COL_GP_1EKE02          = "1EKE02";
	public final static String STACK_COL_GP_1EKD01          = "1EKD01";
	
	public final static String STACK_COL_GP_1EQE01          = "1EQE01";
	public final static String STACK_COL_GP_1FQE01          = "1FQE01";
	public final static String STACK_COL_GP_1FQD01          = "1FQD01";
	public final static String STACK_COL_GP_1GQD01          = "1GQD01";
	
	public final static String STACK_COL_GP_1DKE01          = "1DKE01";
	public final static String STACK_COL_GP_3CKE01          = "3CKE01";
	public final static String STACK_COL_GP_3BKE01          = "3BKE01";
	public final static String STACK_COL_GP_3BKD01          = "3BKD01";
	public final static String STACK_COL_GP_1BFE01          = "1BFE01";
	public final static String STACK_COL_GP_3AFE01          = "3AFE01";
	public final static String STACK_COL_GP_3BFE01          = "3BFE01";
	public final static String STACK_COL_GP_3BFD01          = "3BFD01";
	public final static String STACK_COL_GP_2AHB01          = "2AHB01";
	public final static String STACK_COL_GP_2BHB02          = "2BHB02";
	public final static String STACK_COL_GP_2CHB03          = "2CHB03";
	public final static String STACK_COL_GP_2CWB01          = "2CWB01";
	
	public final static String STACK_COL_GP_2E0113          = "2E0113";
	
	public final static String STACK_COL_GP_2ACT01          = "2ACT01";
	public final static String STACK_COL_GP_2ACT02          = "2ACT02";
	public final static String STACK_COL_GP_2BCT03          = "2BCT03";
	public final static String STACK_COL_GP_2CCT04          = "2CCT04";
	//==START========================================================================================
	/*
	 * [ Static 상수 추가 : (2009.01.20 KBK)]
	 * 
	 * #2 SPM 적치열(입측) : 3DKE01
	 * #2 SPM 적치열(출측) : 3EKD01
	 */
	public final static String STACK_COL_GP_3DKE01          = "3DKE01";
	public final static String STACK_COL_GP_3EKE01          = "3EKE01";
	public final static String STACK_COL_GP_3EKD01          = "3EKD01";
	public final static String STACK_COL_GP_3EKD02          = "3EKD02";
	/*
	 * SPM2 내의 HFL 적치열
	 */
	public final static String STACK_COL_GP_3EFE01          = "3EFE01";		// 입측
	public final static String STACK_COL_GP_3EFD01          = "3EFD01";		// 출측
	/*
	 * 추가: SCRAP장에 대한 적치열 구분 추가.
	 * 3ASP01,
	 * SPM2의 E동의 Scarp Box 추가 : 3ESP01
	 */
	public final static String STACK_COL_GP_3ASP01			= "3ASP01";		// COIL SCRAP처리장 적치 단
	public final static String STACK_COL_GP_3ESP01			= "3ESP01";		// COIL SCRAP처리장 적치 단
	//==END==========================================================================================
	
	//상태 : [X] = 사용금지 ,[C] = 비활성화, [O] = 활성화
	public final static String STACK_LAYER_ACTIVE_STAT_X 	= "X";
	public final static String STACK_LAYER_ACTIVE_STAT_C 	= "C";
	public final static String STACK_LAYER_ACTIVE_STAT_O 	= "O";
	
	/* 중계구역 사용 유무 */
	public final static String CTS_RELAY_YN_Y 	= "Y"; //유
	public final static String CTS_RELAY_YN_N 	= "N"; //무
	
	public final static String CTS_1XTC01 	= "1XTC01"; //1번 CTS
	public final static String CTS_1XTC02 	= "1XTC02"; //2번 CTS
	
	public final static String TC_1ATC03 	= "1ATC03"; //A동 대차
	public final static String TC_1BTC03 	= "1BTC03"; //B동 대차
	
	/* 설비구분 */
	public final static String TC_1X      	= "1X";
	public final static String TC_2X      	= "2X";
	public final static String TC_3X      	= "3X";		// B열연 Coil대차
	
	/* 작업MODE */
	public final static String WORK_MODE_O = "O";	//OnLine
	public final static String WORK_MODE_C = "C";	//OffLine
	public final static String WORK_MODE_E = "E";	//자리비움
			
	/* SCHEDULE 작업요구 형태 */
	public final static String SCH_WDEMAND_TYPE_S = "S";//SCHEDULE
	public final static String SCH_WDEMAND_TYPE_V = "V";//화면
	public final static String SCH_WDEMAND_TYPE_P = "P";//PDA
	public final static String SCH_WDEMAND_TYPE_C = "C";//CRANE
	public final static String SCH_WDEMAND_TYPE_M = "M";//MILL L2
	public final static String SCH_WDEMAND_TYPE_J = "J";//정정 L2
	public final static String SCH_WDEMAND_TYPE_Y = "Y";//야드 L2
	public final static String SCH_WDEMAND_TYPE_H = "H";//HMI
	
	/* TC */ 
	public final static String TC_CF1BP15 = "CF1BP15";
	public final static String TC_CF1BP05 = "CF1BP05";
	public final static String TC_CF1BP06 = "CF1BP06";
	public final static String TC_THHT400 = "THHT400";
	public final static String TC_THHT410 = "THHT410";
	public final static String TC_CN1PB01 = "CN1PB01";
	public final static String TC_CM1PB01 = "CM1PB01";
	public final static String TC_CN1PB03 = "CN1PB03";
	public final static String TC_THCH610 = "THCH610";
	public final static String TC_THCH530 = "THCH530";
	public final static String TC_THCH510 = "THCH510";
	public final static String TC_THCH511 = "THCH511";
	public final static String TC_THCH600 = "THCH600";	
	public final static String TC_THTH430 = "THTH430";
	public final static String TC_THCH520 = "THCH520";
	public final static String TC_THCH550 = "THCH550";
	public final static String TC_THCH560 = "THCH560";
	public final static String TC_THCH570 = "THCH570";
	public final static String TC_THCH580 = "THCH580";
	
	public final static String TC_THHC110 = "THHC110";
	public final static String TC_THHC120 = "THHC120";
	public final static String TC_THHC130 = "THHC130";
	public final static String TC_THHC140 = "THHC140";
	public final static String TC_THHC180 = "THHC180";
	public final static String TC_THHC200 = "THHC200";
	public final static String TC_CN1BP01 = "CN1BP01";
	public final static String TC_CN1PB02 = "CN1PB02";
	public final static String TC_CN1PB05 = "CN1PB05";
	public final static String TC_CN1PB06 = "CN1PB06";
	public final static String TC_CN1PB07 = "CN1PB07";
	public final static String TC_CM1BP01 = "CM1BP01";
	public final static String TC_CN1BP02 = "CN1BP02";
	public final static String TC_CM1BP02 = "CM1BP02";
	public final static String TC_CM1BP04 = "CM1BP04";
	public final static String TC_CM1BP05 = "CM1BP05";	
	public final static String TC_CM1PB02 = "CM1PB02";
	public final static String TC_CM1PB05 = "CM1PB05";
	public final static String TC_CM1PB06 = "CM1PB06";
	public final static String TC_CM1PB07 = "CM1PB07";
	public final static String TC_CM1PB12 = "CM1PB12";
	public final static String TC_CN1PB11 = "CN1PB11";
	public final static String TC_CN1PB14 = "CN1PB14";
	
	public final static String TC_THCH590 = "THCH590";
	public final static String TC_THCH660 = "THCH660";
	public final static String TC_THHC150 = "THHC150";
	public final static String TC_CN1PB12 = "CN1PB12";
	public final static String TC_THHC151 = "THHC151";
	public final static String TC_THHC172 = "THHC172";	
	public final static String TC_THHC250 = "THHC250";
	public final static String TC_THHC160 = "THHC160";
	public final static String TC_THHC170 = "THHC170";
	public final static String TC_THHC171 = "THHC171";
	public final static String TC_THHC131 = "THHC131";
	public final static String TC_THHC132 = "THHC132";
	public final static String TC_THHC260 = "THHC260";
	public final static String TC_THHC300 = "THHC300";
	public final static String TC_THHT510 = "THHT510";
	public final static String TC_CM1PB03 = "CM1PB03";
	public final static String TC_CM1BP03 = "CM1BP03";
	public final static String TC_CN1BP03 = "CN1BP03";
	public final static String TC_CN1BP04 = "CN1BP04";
	public final static String TC_CN1BP05 = "CN1BP05";
	public final static String TC_QMYM001 = "QMYM001";
	public final static String TC_QMYM002 = "QMYM002";
	public final static String TC_QMYM003 = "QMYM003";
	public final static String TC_THHC190 = "THHC190";
	public final static String TC_CM1BP06 = "CM1BP06";
	public final static String TC_CN1BP06 = "CN1BP06";
	public final static String TC_CS1BP01 = "CS1BP01";
	public final static String TC_CF1BP12 = "CF1BP12";
	public final static String TC_CF1BP03 = "CF1BP03";
	public final static String TC_CM1PB10 = "CM1PB10";
	public final static String TC_CN1BP09 = "CN1BP09";
	
	public final static String TC_YMPC010 = "10";
	public final static String TC_YMPC020 = "20";
	public final static String TC_YMPC030 = "30";
	public final static String TC_YMPC031 = "31";
	
	public final static String TC_MIMH110 = "MIMH110";
	
	/*A열연 SLAB야드 추가(MCH)*/
	public final static String TC_HM1PB10="HM1PB10";	//Yard (ASY 차상국A)	A열연 SLAB 자동이적요구 
	public final static String TC_HM1PB60="HM1PB60";	//Yard (ASY 차상국B)	A열연 SLAB 자동이적요구    
	
	public final static String TC_HM1PB02="HM1PB02";	//Yard (ASY 차상국A)	A열연 SLAB 작업지시요구    
	public final static String TC_HM1PB52="HM1PB52";	//Yard (ASY 차상국B)	A열연 SLAB 작업지시요구	            
	public final static String TC_HM1PB05="HM1PB05";	//Yard (ASY 차상국A)	A열연 SLAB 권상실적	                
	public final static String TC_HM1PB55="HM1PB55";	//Yard (ASY 차상국B)	A열연 SLAB 권상실적	                
	public final static String TC_HM1PB06="HM1PB06";	//Yard (ASY 차상국A)	A열연 SLAB 권하실적	                
	public final static String TC_HM1PB56="HM1PB56";	//Yard (ASY 차상국B)	A열연 SLAB 권하실적	                
	public final static String TC_HM1PB07="HM1PB07";	//Yard (ASY 차상국A)	A열연 SLAB 권하이상실적	            
	public final static String TC_HM1PB57="HM1PB57";	//Yard (ASY 차상국B)	A열연 SLAB 권하이상실적	            
	public final static String TC_HM1PB03="HM1PB03";	//Yard (ASY 차상국A)	A열연 SLAB 운전모드 변경 정보	      
	public final static String TC_HM1PB53="HM1PB53";	//Yard (ASY 차상국B)	A열연 SLAB 운전모드 변경 정보	      
	public final static String TC_HM1PB04="HM1PB04";	//Yard (ASY 차상국A)	A열연 SLAB 고장상태 변경 정보	      
	public final static String TC_HM1PB54="HM1PB54";	//Yard (ASY 차상국B)	A열연 SLAB 고장상태 변경 정보	      
	public final static String TC_HM1PB01="HM1PB01";	//Yard (ASY 차상국A)	A열연 SLAB 시스템 ON/OFF 정보	      
	public final static String TC_HM1PB51="HM1PB51";	//Yard (ASY 차상국B)	A열연 SLAB 시스템 ON/OFF 정보	      
	public final static String TC_HM1PB08="HM1PB08";	//Yard (ASY 차상국A)	A열연 SLAB 시각정보요구	            
	public final static String TC_HM1PB58="HM1PB58";	//Yard (ASY 차상국B)	A열연 SLAB 시각정보요구	            
	public final static String TC_HM1PB09="HM1PB09";	//Yard (ASY 차상국A)	A열연 SLAB YARD MAP 정보 요구	      
	public final static String TC_HM1PB59="HM1PB59";	//Yard (ASY 차상국B)	A열연 SLAB YARD MAP 정보 요구	      
	public final static String TC_HM1BP01="HM1BP01";	//Yard (ASY 차상국A)	CRANE 작업지시	                     
	public final static String TC_HM1BP51="HM1BP51";	//Yard (ASY 차상국B)	CRANE 작업지시	                     
	public final static String TC_HM1BP03="HM1BP03";	//Yard (ASY 차상국A)	시각설정정보	                       
	public final static String TC_HM1BP53="HM1BP53";	//Yard (ASY 차상국B)	시각설정정보	                       
	public final static String TC_HM1BP04="HM1BP04";	//Yard (ASY 차상국A)	야드MAP 정보	                       
	public final static String TC_HM1BP54="HM1BP54";	//Yard (ASY 차상국B)	야드MAP 정보
	
	/*A열연 SLAB야드 추가(MCH) 연주 7호기(MCH) */
	public final static String TC_HC3PB51="HC3PB51";	//A열연 연주 7호기	Slab Line Off 요구	                   
	public final static String TC_HC3PB52="HC3PB52";	//A열연 연주 7호기	YARD MAP 정보 요구	                   
	public final static String TC_HC3PB53="HC3PB53";	//A열연 연주 7호기	ROT 고장,복구	                    
	public final static String TC_HC3BP51="HC3BP51";	//A열연 연주 7호기	야드MAP 정보                         
	public final static String TC_HC3BP52="HC3BP52";	//A열연 연주 7호기	CRANE 고장정보

	/* MODEL */
	
	public final static String MODEL_PMYDJ001 	= "PMYDJ001";		
	public final static String MODEL_PMYDJ002 	= "PMYDJ002";	
	public final static String MODEL_PTYDJ001 	= "PTYDJ001";	
	public final static String MODEL_PTYDJ002 	= "PTYDJ002";	
	public final static String MODEL_PTYDJ003 	= "PTYDJ003";	
	public final static String MODEL_CTYDJ011 	= "CTYDJ011";	
	public final static String MODEL_CTYDJ012 	= "CTYDJ012";	
	public final static String MODEL_CTYDJ013 	= "CTYDJ013";	
	public final static String MODEL_CTYDJ032 	= "CTYDJ032";	
	public final static String MODEL_CSYDJ001 	= "CSYDJ001";	
	public final static String MODEL_TSYDJ002 	= "TSYDJ002";	
	public final static String MODEL_TSYDJ003 	= "TSYDJ003";	
	public final static String MODEL_TSYDJ004 	= "TSYDJ004";	
	public final static String MODEL_TSYDJ014 	= "TSYDJ014";
	public final static String MODEL_DMYDR002 	= "DMYDR002";	
	public final static String MODEL_DMYDR004 	= "DMYDR004";	
	public final static String MODEL_DMYDR005 	= "DMYDR005";	
	public final static String MODEL_DMYDR008 	= "DMYDR008";	
	public final static String MODEL_DMYDR011 	= "DMYDR011";	
	public final static String MODEL_DMYDR013 	= "DMYDR013";	
	public final static String MODEL_DMYDR014 	= "DMYDR014";	
	public final static String MODEL_DMYDR016 	= "DMYDR016";	
	public final static String MODEL_DMYDR019 	= "DMYDR019";	
	public final static String MODEL_DMYDR020 	= "DMYDR020";	
	public final static String MODEL_DMYDR022 	= "DMYDR022";	
	public final static String MODEL_DMYDR023 	= "DMYDR023";	
	public final static String MODEL_DMYDR025 	= "DMYDR025";	
	public final static String MODEL_DMYDR026 	= "DMYDR026";	
	public final static String MODEL_DMYDR027 	= "DMYDR027";	
	public final static String MODEL_DMYDR029 	= "DMYDR029";	
	public final static String MODEL_DMYDR030 	= "DMYDR030";	
	public final static String MODEL_DMYDR032 	= "DMYDR032";	
	public final static String MODEL_DMYDR033 	= "DMYDR033";	
	public final static String MODEL_DMYDR035 	= "DMYDR035";	
	public final static String MODEL_DMYDR036 	= "DMYDR036";	
	public final static String MODEL_DMYDR037 	= "DMYDR037";	
	public final static String MODEL_DMYDR039 	= "DMYDR039";	
	public final static String MODEL_DMYDR040 	= "DMYDR040";	
	public final static String MODEL_DMYDR041 	= "DMYDR041";	
	
	public final static String MODEL_DMYDR070 	= "DMYDR070";
	public final static String MODEL_DMYDR071 	= "DMYDR071";
	public final static String MODEL_DMYDR072 	= "DMYDR072";
	public final static String MODEL_DMYDR073 	= "DMYDR073";
	public final static String MODEL_DMYDR074 	= "DMYDR074";
	public final static String MODEL_DMYDR075 	= "DMYDR075";
			
	public final static String MODEL_YM = "YM";
	public final static String MODEL_YMDM001 = "YMDM001";
	public final static String MODEL_YMDM002 = "YMDM002";
	public final static String MODEL_YMDM003 = "YMDM003";
	public final static String MODEL_YMDM004 = "YMDM004";
	public final static String MODEL_YMDM005 = "YMDM005";
	public final static String MODEL_YMDM006 = "YMDM006";
	public final static String MODEL_YMDM007 = "YMDM007";
	public final static String MODEL_YMDM008 = "YMDM008";
	public final static String MODEL_YMDM009 = "YMDM009";
	public final static String MODEL_YMDM010 = "YMDM010";
	public final static String MODEL_YMDM011 = "YMDM011";
	public final static String MODEL_YMDM012 = "YMDM012";
	public final static String MODEL_YMDM013 = "YMDM013";
	public final static String MODEL_YMDM014 = "YMDM014";
	public final static String MODEL_YMDM015 = "YMDM015";
	public final static String MODEL_YMPO159 = "YMPO159";
	public final static String MODEL_YMPO163 = "YMPO163";
	public final static String MODEL_YMPO164 = "YMPO164";
	public final static String MODEL_YMPO161 = "YMPO161";
	public final static String MODEL_YMPO155 = "YMPO155";
	
	//2007-04-11 A열연 SLAB야드 상차완료 TC 추가 (MCH)
	public final static String MODEL_YMPM002 = "YMPM002";
	
	public final static String MODEL_YMPM001 = "YMPM001";
	public final static String MODEL_ZZPC001 = "ZZPC001";
	public final static String MODEL_YMPC100 = "YMPC100";
	public final static String MODEL_YMPC110 = "YMPC110";
	public final static String MODEL_YMPC120 = "YMPC120";
	public final static String MODEL_YMPC130 = "YMPC130";
	public final static String MODEL_YMPC140 = "YMPC140";
	public final static String MODEL_YMPC150 = "YMPC150";
	public final static String MODEL_DMYM001 = "DMYM001";
	public final static String MODEL_DMYM002 = "DMYM002";
	public final static String MODEL_DMYM003 = "DMYM003";
	public final static String MODEL_DMYM004 = "DMYM004";
	public final static String MODEL_DMYM005 = "DMYM005";
	public final static String MODEL_DMYM006 = "DMYM006";
	public final static String MODEL_DMYM007 = "DMYM007";
	public final static String MODEL_DMYM008 = "DMYM008";
	public final static String MODEL_PCYM001 = "PCYM001";
	public final static String MODEL_PCYM002 = "PCYM002";
	public final static String MODEL_PCYM003 = "PCYM003";
	public final static String MODEL_PMYM001 = "PMYM001";
	public final static String MODEL_PMYM002 = "PMYM002";
	public final static String MODEL_PMYM003 = "PMYM003";
	public final static String MODEL_PMYM004 = "PMYM004";
	public final static String MODEL_PMYM005 = "PMYM005";
	public final static String MODEL_PMYM006 = "PMYM006";
	public final static String MODEL_PMYM007 = "PMYM007";
	public final static String MODEL_PMYM008 = "PMYM008";
	public final static String MODEL_POYM001 = "POYM001";
	public final static String MODEL_POYM002 = "POYM002";
	public final static String MODEL_POYM003 = "POYM003";
	public final static String MODEL_POYM004 = "POYM004";
	public final static String MODEL_POYM005 = "POYM005";
	public final static String MODEL_POYM006 = "POYM006";
	public final static String MODEL_POYM007 = "POYM007";
	public final static String MODEL_POYM008 = "POYM008";
	public final static String MODEL_POYM009 = "POYM009";
	//==START========================================================================================
	/*신규 SPM 관련 Interface */
	public final static String MODEL_POYM010 = "POYM010";
	//==END==========================================================================================	
	public final static String MODEL_DMYM009 = "DMYM009";
	public final static String MODEL_DMYM010 = "DMYM010";
	public final static String MODEL_DMYM011 = "DMYM011";
	public final static String MODEL_QMYM001 = TC_QMYM001;
	public final static String MODEL_QMYM002 = TC_QMYM002;
	public final static String MODEL_QMYM003 = TC_QMYM003;

	public final static String MODEL_CF1BP14 = "CF1BP14";
	public final static String MODEL_CM1PB09 = "CM1PB09";
	public final static String MODEL_THCH660 = "THCH660";
	
	public final static String MODEL_PSYM001 = "PSYM001";
	public final static String MODEL_PSYM002 = "PSYM002";
	public final static String MODEL_YMPS001 = "YMPS001";
	public final static String MODEL_YMPS002 = "YMPS002";
		
	public final static String STACK_LAYER_STAT_E	= "E";	// 적치가능한위치
	public final static String STACK_LAYER_STAT_X	= "X";	// 적치불가
	public final static String STACK_LAYER_STAT_V	= "V";	// 적치불가
	public final static String STACK_LAYER_STAT_L	= "L";	// 적치중
	public final static String STACK_LAYER_STAT_S	= "S";	// 스케쥴 예정
	public final static String STACK_LAYER_STAT_U	= "U";	// UP 스케쥴 수행
	public final static String STACK_LAYER_STAT_P	= "P";	// PUT 스케쥴 수행
	
	public final static String EQUIP_KIND_ALL     	= "*"; 	//SADDLE, CTS
	public final static String EQUIP_KIND_SA     	= "S"; 	//SADDLE
	public final static String EQUIP_KIND_CT     	= "C"; 	//CTS
	public final static String EQUIP_KIND_TC     	= "TC";	//대차
	public final static String EQUIP_KIND_CR     	= "CR"; //CRANE
	public final static String EQUIP_KIND_TR     	= "TR"; //차량
	public final static String EQUIP_KIND_PT     	= "PT"; //팔레트
	
	//A열연 동에 따른 ROT Name(MCH)
	public final static String EQUIP_KIND_RT     	= "RT"; 	//팔레트
	public final static String EQUIP_KIND_0_A_RT    = "RT03"; 	//ROT 존
	public final static String EQUIP_KIND_0_B_RT    = "RT02"; 	//ROT 존
	
	public final static String EQUIP_GP_1XTC03     	= "1XTC03"; 	//A열연 확장대차
	public final static String EQUIP_GP_2XTC01     	= "2XTC01"; 	//B열연 확장대차
	public final static String EQUIP_GP_2XTC02     	= "2XTC02"; 	//B열연 확장대차
	public final static String EQUIP_GP_2XTC03     	= "2XTC03"; 	//B열연 확장대차
	public final static String EQUIP_GP_3XTC02     	= "3XTC02"; 	//B열연 확장대차
	
	public final static String MAIN_WORK_M			= "M";// 주작업
	public final static String SUB_WORK_S			= "S";// 보조작업 
	public final static String MAIN_WORK_01			= "01";// 주작업
	public final static String SUB_WORK_02			= "02";// 보조작업
	
	public final static String ITEM_SM 				= "SM";	// SLAB 소재
	public final static String ITEM_CM 				= "CM";	// COIL 소재
	public final static String ITEM_CG 				= "CG";	// COIL 제품
	public final static String ITEM_HP 				= "HP";	// Plate

	/* COIL NEW 이동조건 */
	/*
	public final static String NEW_STOCK_MOVE_TERM_1C = "1C";	//생산예정
	public final static String NEW_STOCK_MOVE_TERM_3C = "3C";	//생산종료
	public final static String NEW_STOCK_MOVE_TERM_AC = "AC";	//재질판정대기
	public final static String NEW_STOCK_MOVE_TERM_A1 = "A1";	//HFL 추출
	public final static String NEW_STOCK_MOVE_TERM_A2 = "A2";	//SPM 추출
	public final static String NEW_STOCK_MOVE_TERM_A3 = "A3";   //수냉재추출
	public final static String NEW_STOCK_MOVE_TERM_A4 = "A4";	//공냉재추출
	public final static String NEW_STOCK_MOVE_TERM_A5 = "A5";	//수냉재보급완료
	public final static String NEW_STOCK_MOVE_TERM_BC = "BC";	//정정작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_CC = "CC";	//정정작업대기
	public final static String NEW_STOCK_MOVE_TERM_C1 = "C1";  	//보급완료 
	public final static String NEW_STOCK_MOVE_TERM_DC = "DC";	//이송작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_E1 = "E1";	//이송완료
	public final static String NEW_STOCK_MOVE_TERM_EC = "EC";	//이송작업대기
	public final static String NEW_STOCK_MOVE_TERM_FC = "FC";	//판정보류
	public final static String NEW_STOCK_MOVE_TERM_GC = "GC";	//종합판정대기
	public final static String NEW_STOCK_MOVE_TERM_HG = "HG";	//입고대기
	public final static String NEW_STOCK_MOVE_TERM_H1 = "H1";	//입고완료
	public final static String NEW_STOCK_MOVE_TERM_JG = "JG";	//반납 대기(정보)
	public final static String NEW_STOCK_MOVE_TERM_JR = "JR";	//반납 대기(현물)
	public final static String NEW_STOCK_MOVE_TERM_J1 = "J1";	//반납 완료
	public final static String NEW_STOCK_MOVE_TERM_KG = "KG";	//출하작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_K1 = "K1";	//반입대기
	public final static String NEW_STOCK_MOVE_TERM_LG = "LG";	//출하작업대기
	public final static String NEW_STOCK_MOVE_TERM_L1 = "L1";	//대차출하완료
	public final static String NEW_STOCK_MOVE_TERM_L2 = "L2";	//대차출하대기
	public final static String NEW_STOCK_MOVE_TERM_MG = "MG";	//출하완료
	public final static String NEW_STOCK_MOVE_TERM_M1 = "M1";	//보관Coil
	public final static String NEW_STOCK_MOVE_TERM_M2 = "M2";	//보관제품
	public final static String NEW_STOCK_MOVE_TERM_XG = "XG";	//경매대상선정	
	public final static String NEW_STOCK_MOVE_TERM_YG = "YG";	//재공충당대기
	public final static String NEW_STOCK_MOVE_TERM_ZG = "ZG";	//제품충당대기
	public final static String NEW_STOCK_MOVE_TERM_TL = "TL";	//대차상차완료
	public final static String NEW_STOCK_MOVE_TERM_TM = "TM";	//대차이동
	public final static String NEW_STOCK_MOVE_TERM_CL = "CL";	//CTS상차완료
	public final static String NEW_STOCK_MOVE_TERM_CM = "CM";	//CTS이동
	*/
	
	public final static String NEW_STOCK_MOVE_TERM_1C 	= "1C";	//생산예정
	public final static String NEW_STOCK_MOVE_TERM_A1 	= "A1";	//HFL 추출
	public final static String NEW_STOCK_MOVE_TERM_A2 	= "A2";	//SPM 추출
	public final static String NEW_STOCK_MOVE_TERM_A3 	= "A3";   //수냉재추출
	public final static String NEW_STOCK_MOVE_TERM_A4 	= "A4";	//공냉재추출
	public final static String NEW_STOCK_MOVE_TERM_A5 	= "A5";	//수냉재보급완료
	public final static String NEW_STOCK_MOVE_TERM_AC   = "AC";	//재질판정대기
	public final static String NEW_STOCK_MOVE_TERM_BC	= "BC";	//정정작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_CC	= "CC";	//정정작업대기
	public final static String NEW_STOCK_MOVE_TERM_C1 	= "C1";  	//보급완료 
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
	// 최규성 ==========================
	// 2009-08-10
	// 2009-10-05  "B열연SPM2추출" 코드 추가
    public final static String NEW_STOCK_MOVE_TERM_CR 	= "CR";	// Coil 차량이적
	public final static String NEW_STOCK_MOVE_TERM_RL 	= "RL";	// Coil 차량상차완료
	public final static String NEW_STOCK_MOVE_TERM_A6 	= "A6";	// B열연SPM2추출
	public final static String NEW_STOCK_MOVE_TERM_A7 	= "A7";	// HFL결속장  추출
	public final static String NEW_STOCK_MOVE_TERM_A8 	= "A8";	// 지포장  추출
	//===============================================================================
	/* SLAB NEW 이동조건 */
	/*
	public final static String NEW_STOCK_MOVE_TERM_1S = "1S";	//생산예정
	public final static String NEW_STOCK_MOVE_TERM_11 = "11";	//부두야드 입고예정
	public final static String NEW_STOCK_MOVE_TERM_12 = "12";	//부두야드 입고완료
	public final static String NEW_STOCK_MOVE_TERM_3S = "3S";	//생산종료
	public final static String NEW_STOCK_MOVE_TERM_AS = "AS";	//수입검사대기
	public final static String NEW_STOCK_MOVE_TERM_BS = "BS";	//이송지시대기
	public final static String NEW_STOCK_MOVE_TERM_B1 = "B1";	//WCR재 추출
	public final static String NEW_STOCK_MOVE_TERM_B2 = "B2";	//CCR재 추출
	public final static String NEW_STOCK_MOVE_TERM_CS = "CS";	//이송대기
//	public final static String NEW_STOCK_MOVE_TERM_C1 = "C1";	//이송완료
	public final static String NEW_STOCK_MOVE_TERM_DS = "DS";	//정정작업대기
	public final static String NEW_STOCK_MOVE_TERM_D1 = "D1";	//Scarfing 보급완료
	public final static String NEW_STOCK_MOVE_TERM_D2 = "D2";	//시편작업대기
	public final static String NEW_STOCK_MOVE_TERM_D3 = "D3";	//핸드스카핑작업대기
	public final static String NEW_STOCK_MOVE_TERM_D4 = "D4";	//보류재
	public final static String NEW_STOCK_MOVE_TERM_ES = "ES";	//압연지시대기
	public final static String NEW_STOCK_MOVE_TERM_FS = "FS";	//압연작업대기
	public final static String NEW_STOCK_MOVE_TERM_F1 = "F1";	//W/B 보급완료
	public final static String NEW_STOCK_MOVE_TERM_F2 = "F2";	//CTC Loading완료
	public final static String NEW_STOCK_MOVE_TERM_F3 = "F3";	//R/T Loading완료
	public final static String NEW_STOCK_MOVE_TERM_KS = "KS";	//SLAB 출하작업지시대기
	public final static String NEW_STOCK_MOVE_TERM_LS = "LS";	//SLAB 출하작업대기
	public final static String NEW_STOCK_MOVE_TERM_MS = "MS";	//SLAB 출하완료
	public final static String NEW_STOCK_MOVE_TERM_YS = "YS";	//판정보류
	public final static String NEW_STOCK_MOVE_TERM_ZS = "ZS";	//충당대기
//	public final static String NEW_STOCK_MOVE_TERM_TL = "TL";	//대차상차완료
//	public final static String NEW_STOCK_MOVE_TERM_TM = "TM";	//대차이동
	public final static String NEW_STOCK_MOVE_TERM_VL = "VL";	//상차완료
	public final static String NEW_STOCK_MOVE_TERM_VM = "VM";	//차량이동
	public final static String NEW_STOCK_MOVE_TERM_VW = "VW";	//WCR이동
	*/
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
//	public final static String NEW_STOCK_MOVE_TERM_C1 	= "C1";	//이송완료
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
//	public final static String NEW_STOCK_MOVE_TERM_TL 	= "TL";	//대차상차완료
//	public final static String NEW_STOCK_MOVE_TERM_TM 	= "TM";	//대차이동
	public final static String NEW_STOCK_MOVE_TERM_VL 	= "VL";	//상차완료
	public final static String NEW_STOCK_MOVE_TERM_VM 	= "VM";	//차량이동
	public final static String NEW_STOCK_MOVE_TERM_VW 	= "VW";	//WCR이동
	
	/* COIL NEW 스케쥴종류 */
	public final static String NEW_SCH_WORK_KIND_CDTO = "CDTO";	//Coil DC Take out
	public final static String NEW_SCH_WORK_KIND_CDTI = "CDTI";		//Coil DC Take In
	public final static String NEW_SCH_WORK_KIND_CDLO = "CDLO";	//Coil DC Line Off
	public final static String NEW_SCH_WORK_KIND_CDLI = "CDLI";		//Coil DC Line In
	public final static String NEW_SCH_WORK_KIND_CELO = "CELO";	//Coil EC Line Off
	
	public final static String NEW_SCH_WORK_KIND_CCMU = "CCMU";	//Coil CTS 하차       --좌측 적치 시
	public final static String NEW_SCH_WORK_KIND_CCMR = "CCMR";	//Coil CTS 하차(2) --우측 적치 시
	public final static String NEW_SCH_WORK_KIND_CFLI = "CFLI";		//Coil HFL 보급
	public final static String NEW_SCH_WORK_KIND_CFSI = "CFSI";		//Coil HFL 결속대 보급
	public final static String NEW_SCH_WORK_KIND_CKLI = "CKLI";		//Coil SPM 보급
	public final static String NEW_SCH_WORK_KIND_EQLI = "EQLI";		//Coil EQL 보급
	public final static String NEW_SCH_WORK_KIND_CWLI = "CWLI";		//Coil 수냉재보급
	public final static String NEW_SCH_WORK_KIND_CFTI = "CFTI";		//Coil HFL Take In
	public final static String NEW_SCH_WORK_KIND_CKTI = "CKTI";		//Coil SPM Take In
	public final static String NEW_SCH_WORK_KIND_EQTI = "EQTI";		//Coil EQL Take In
	public final static String NEW_SCH_WORK_KIND_CFTO = "CFTO";		//Coil HFL Take Out
	public final static String NEW_SCH_WORK_KIND_CKTO = "CKTO";		//Coil SPM Take Out
	public final static String NEW_SCH_WORK_KIND_EQTO = "EQTO";		//Coil EQL Take Out
	public final static String NEW_SCH_WORK_KIND_CFLO = "CFLO";		//Coil HFL 추출
	public final static String NEW_SCH_WORK_KIND_CFSO = "CFSO";		//Coil HFL 결속대 추출
	public final static String NEW_SCH_WORK_KIND_CKLO = "CKLO";		//Coil SPM 추출
	public final static String NEW_SCH_WORK_KIND_EQLO = "EQLO";		//Coil EQL 추출
	public final static String NEW_SCH_WORK_KIND_CKLR = "CKLR";		//Coil SPM 재작업 추출
	public final static String NEW_SCH_WORK_KIND_EQLR = "EQLR";		//Coil EQL 재작업 추출
	public final static String NEW_SCH_WORK_KIND_CWLO = "CWLO";	//Coil 수냉탱크추출
	public final static String NEW_SCH_WORK_KIND_CYST = "CYST";		//Coil 보류장입고
	
	public final static String NEW_SCH_WORK_KIND_CVML = "CVML";	//Coil 소재이송상차
	public final static String NEW_SCH_WORK_KIND_CVM2 = "CVM2";	//Coil 소재이송상차
	public final static String NEW_SCH_WORK_KIND_CVM3 = "CVM3";	//Coil 소재이송상차
	public final static String NEW_SCH_WORK_KIND_CVM6 = "CVM6";	//Coil 소재차량이송상차(L)
	public final static String NEW_SCH_WORK_KIND_CVM8 = "CVM8";	//Coil 소재차량이송상차(R)
	
	public final static String NEW_SCH_WORK_KIND_CVMU = "CVMU";	//Coil 소재이송하차
	public final static String NEW_SCH_WORK_KIND_CVM4 = "CVM4";	//Coil 소재이송하차
	public final static String NEW_SCH_WORK_KIND_CVM5 = "CVM5";	//Coil 소재이송하차
	public final static String NEW_SCH_WORK_KIND_CVM7 = "CVM7";	//Coil 소재차량이송하차(L)
	public final static String NEW_SCH_WORK_KIND_CVM9 = "CVM9";	//Coil 소재차량이송하차(R)
	
	public final static String NEW_SCH_WORK_KIND_CYMM = "CYMM";	//Coil 동내이적
	public final static String NEW_SCH_WORK_KIND_CYM1 = "CYM1";	//Coil 동내이적
	public final static String NEW_SCH_WORK_KIND_CYM2 = "CYM2";	//Coil 동내이적
	public final static String NEW_SCH_WORK_KIND_CYM3 = "CYM3";	//Coil 동내이적
	
	// 자동 동내 이적
	public final static String NEW_SCH_WORK_KIND_CYA1 = "CYA1";	//Coil 동내이적
	public final static String NEW_SCH_WORK_KIND_CYA2 = "CYA2";	//Coil 동내이적
	public final static String NEW_SCH_WORK_KIND_CYA3 = "CYA3";	//Coil 동내이적
	public final static String NEW_SCH_WORK_KIND_CYA4 = "CYA4";	//Coil 동내이적
	
	public final static String NEW_SCH_WORK_KIND_CTSL = "CTSL";		//Coil 동간보급상차
	public final static String NEW_SCH_WORK_KIND_CTS2 = "CTS2";		//Coil 동간보급상차
	public final static String NEW_SCH_WORK_KIND_CTS3 = "CTS3";		//Coil 동간보급상차
	
	public final static String NEW_SCH_WORK_KIND_CTML = "CTML";	//Coil 동간이적상차(L)
	public final static String NEW_SCH_WORK_KIND_CTM2 = "CTM2";	//Coil 동간이적상차(R)
	public final static String NEW_SCH_WORK_KIND_CTM3 = "CTM3";	//Coil 동간이적상차
	
	public final static String NEW_SCH_WORK_KIND_CTMU = "CTMU";	//Coil 대차하차(L)
	public final static String NEW_SCH_WORK_KIND_CTM4 = "CTM4";	//Coil 대차하차(R)
	//==START========================================================================================
	// CGS
	// B열연 Coil 신규 대차에 대한 sch Code : 동간이적상차
	// 3개의 추가설비에 대한 코드 
	// 2009-04-13
	public final static String NEW_SCH_WORK_KIND_CTM5 = "CTM5"; // Coil 신규1대차 동간이적상차 (A <--> B)
	public final static String NEW_SCH_WORK_KIND_CTM6 = "CTM6"; // Coil 신규2대차 동간이적상차 (C <--> D)
	public final static String NEW_SCH_WORK_KIND_CTM7 = "CTM7"; // Coil 신규3대차 동간이적상차 (D <--> E)
	// B열연 Coil 신규 대차에 대한 sch Code : 대차 하차
	// 3개의 추가설비에 대한 코드 
	// 2009-04-13	
	public final static String NEW_SCH_WORK_KIND_CTM8 = "CTM8"; // Coil 신규1대차(A <--> B)하차 
	public final static String NEW_SCH_WORK_KIND_CTM9 = "CTM9"; // Coil 신규2대차(C <--> D)하차
	public final static String NEW_SCH_WORK_KIND_CTMX = "CTMX"; // Coil 신규3대차(D <--> E)하차
	/*
	 * 추가 최규성 
	 */
	public final static String NEW_SCH_WORK_KIND_CNLI = "CNLI"; // Coil #2 SPM 보급
	public final static String NEW_SCH_WORK_KIND_CNTI = "CNTI";	// Coil #2 SPM Take In
	public final static String NEW_SCH_WORK_KIND_CNLO = "CNLO";	// Coil #2 SPM 추출
	public final static String NEW_SCH_WORK_KIND_CNTO = "CNTO";	// Coil #2 SPM Take Out
	public final static String NEW_SCH_WORK_KIND_CHLI = "CHLI"; // Coil #2 HFL 보급
	public final static String NEW_SCH_WORK_KIND_CHLO = "CHLO"; // Coil #2 HFL 보급
	//==END==========================================================================================	
	public final static String NEW_SCH_WORK_KIND_GVML = "GVML";	//Coil 제품이송상차
	public final static String NEW_SCH_WORK_KIND_GVM2 = "GVM2";	//Coil 제품이송상차
	public final static String NEW_SCH_WORK_KIND_GVM3 = "GVM3";	//Coil 제품이송상차
	public final static String NEW_SCH_WORK_KIND_GVM6 = "GVM6";	//Coil 제품차량이송상차(L)
	public final static String NEW_SCH_WORK_KIND_GVM8 = "GVM8";	//Coil 제품차량이송상차(R)
	
	public final static String NEW_SCH_WORK_KIND_GVMU = "GVMU";	//Coil 제품이송하차(L)
	public final static String NEW_SCH_WORK_KIND_GVM4 = "GVM4";	//Coil 제품이송하차(2)
	public final static String NEW_SCH_WORK_KIND_GVM5 = "GVM5";	//Coil 제품이송하차
	public final static String NEW_SCH_WORK_KIND_GVM7 = "GVM7";	//Coil 제품차량이송하차(L)
	public final static String NEW_SCH_WORK_KIND_GVM9 = "GVM9";	//Coil 제품차량이송하차(R)
	
	public final static String NEW_SCH_WORK_KIND_GVFL = "GVFL";		//Coil TR제품출하상차
	public final static String NEW_SCH_WORK_KIND_GVF1 = "GVF1";		//Coil TR제품출하상차
	public final static String NEW_SCH_WORK_KIND_GVF2 = "GVF2";		//Coil TR제품출하상차
	
	public final static String NEW_SCH_WORK_KIND_GTFL = "GTFL";		//Coil TT제품출하상차
	public final static String NEW_SCH_WORK_KIND_GTF1 = "GTF1";		//Coil TT제품출하상차
	public final static String NEW_SCH_WORK_KIND_GTF2 = "GTF2";		//Coil TT제품출하상차
	
	public final static String NEW_SCH_WORK_KIND_GPFL = "GPFL";		//Coil PT제품출하상차
	public final static String NEW_SCH_WORK_KIND_GPF1 = "GPF1";		//Coil PT제품출하상차
	public final static String NEW_SCH_WORK_KIND_GPF2 = "GPF2";		//Coil PT제품출하상차
	
	public final static String NEW_SCH_WORK_KIND_CTFL = "CTFL";		//Coil 대차출하상차
	public final static String NEW_SCH_WORK_KIND_CTFU = "CTFU";		//Coil 대차출하하차
	public final static String NEW_SCH_WORK_KIND_CVRU = "CVRU";	//Coil 반입
	
	/* 저장 영역 */
	public final static String STACK_COL_USAGE_CD_C1 	= "C1";			// COIL 냉각장
	public final static String STACK_COL_USAGE_CD_C2 	= "C2";			// COIL TAKE OUT 적치장
	public final static String STACK_COL_USAGE_CD_C3 	= "C3";			// COIL 이상분기적치장
	public final static String STACK_COL_USAGE_CD_C4 	= "C4";			// COIL 비상적치장
	public final static String STACK_COL_USAGE_CD_C5 	= "C5";			// COIL 보관소재적치장
	public final static String STACK_COL_USAGE_CD_C6 	= "C6";			// COIL 정정보급대기장
	public final static String STACK_COL_USAGE_CD_C7 	= "C7";			// COIL 소재이송대기장
	public final static String STACK_COL_USAGE_CD_C8 	= "C8";			// COIL 보류장
	public final static String STACK_COL_USAGE_CD_G1 	= "G1";			// COIL 제품출하대기장
	public final static String STACK_COL_USAGE_CD_G2 	= "G2";			// COIL 제품이송상차장
	public final static String STACK_COL_USAGE_CD_G3 	= "G3";			// COIL 제품이송하차장
	public final static String STACK_COL_USAGE_CD_G4 	= "G4";			// COIL 제품이적중계장
	public final static String STACK_COL_USAGE_CD_G5 	= "G5";			// COIL 제품보관적치장
	public final static String STACK_COL_USAGE_CD_FS 	= "FS";			// CTS FROM SADDLE
	public final static String STACK_COL_USAGE_CD_TS 	= "TS";			// CTS TO SADDLE
	public final static String STACK_COL_USAGE_CD_CC 	= "CC";			// COIL 분기콘베이어
	public final static String STACK_COL_USAGE_CD_CE 	= "CE";			// COIL 확장콘베이어
	public final static String STACK_COL_USAGE_CD_CW 	= "CW";			// COIL 수냉탱크
	public final static String STACK_COL_USAGE_CD_FE 	= "FE";			// COIL HFL보급위치
	public final static String STACK_COL_USAGE_CD_FI 	= "FI";			// COIL HFLTAKEIN위치
	public final static String STACK_COL_USAGE_CD_FD 	= "FD";			// COIL HFL추출위치
	public final static String STACK_COL_USAGE_CD_KE 	= "KE";			// COIL SPM보급위치
	public final static String STACK_COL_USAGE_CD_QE 	= "QE";			// COIL EQL보급위치
	public final static String STACK_COL_USAGE_CD_KI 	= "KI";			// COIL SPMTAKEIN위치
	public final static String STACK_COL_USAGE_CD_KD 	= "KD";			// COIL SPM추출위치
	public final static String STACK_COL_USAGE_CD_QD 	= "QD";			// COIL EQL추출위치
	public final static String STACK_COL_USAGE_CD_XX 	= "XX";			// COIL 비상적치위치
	public final static String STACK_COL_USAGE_CD_CX 	= "CX";			// 대차정지위치
	public final static String STACK_COL_USAGE_CD_TX 	= "TX";			// 차량정지위치
	public final static String STACK_COL_USAGE_CD_PX 	= "PX";			// 팔레트정지위치
	public final static String STACK_COL_USAGE_CD_BK 	= "BK";			// SLAB 보온카바위치
	public final static String STACK_COL_USAGE_CD_CT 	= "CT";			// SLAB CTC
	public final static String STACK_COL_USAGE_CD_HD 	= "HD";			// SLAB Holding Bed
	public final static String STACK_COL_USAGE_CD_RT 	= "RT";			// SLAB Roller Table
	public final static String STACK_COL_USAGE_CD_WB 	= "WB";			// SLAB Walking Beam
	public final static String STACK_COL_USAGE_CD_SE 	= "SE";			// SLAB Scafing 입측
	public final static String STACK_COL_USAGE_CD_SD 	= "SD";			// SLAB Scafing 출측
	public final static String STACK_COL_USAGE_CD_31 	= "31";			// SLAB 옥내이송적치장
	public final static String STACK_COL_USAGE_CD_32 	= "32";			// SLAB 정정작업대기장
	public final static String STACK_COL_USAGE_CD_33 	= "33";			// SLAB 압연지시대기장
	public final static String STACK_COL_USAGE_CD_34 	= "34";			// SLAB 동간보급준비장
	public final static String STACK_COL_USAGE_CD_35 	= "35";			// SLAB 압연보급대기장
	public final static String STACK_COL_USAGE_CD_36 	= "36";			// SLAB Take Out적치장
	public final static String STACK_COL_USAGE_CD_37 	= "37";			// SLAB WCR재 적치장
	public final static String STACK_COL_USAGE_CD_41 	= "41";			// SLAB 부두입고적치장
	public final static String STACK_COL_USAGE_CD_42 	= "42";			// SLAB 부두이송대기장
	public final static String STACK_COL_USAGE_CD_43 	= "43";			// SLAB Hand Scarfing 장
	public final static String STACK_COL_USAGE_CD_44 	= "44";			// SLAB Slab 절단장
	//==START========================================================================================
	// CGS
	/*
	 * 추가: 
	 * SPM Coil Scrap처리장 저장영역
	 * SPM Coil 보급 위치 ECC2
	 * */
	public final static String STACK_COL_USAGE_CD_SP    = "SP";			// COIL SCRAP처리장 코드
	public final static String STACK_COL_USAGE_CD_K2 	= "K2";			// COIL SPM보급위치 ECC2
	
	//==END==========================================================================================
	/* SLAB NEW 스케쥴종류 */
	public final static String NEW_SCH_WORK_KIND_SYST = "SYST";		//Slab 부두야드 입고
	public final static String NEW_SCH_WORK_KIND_SVML = "SVML";	//Slab 이송상차
	public final static String NEW_SCH_WORK_KIND_SVMU = "SVMU";	//Slab 이송하차
	public final static String NEW_SCH_WORK_KIND_SYMM = "SYMM";	//Slab 동내이적
	public final static String NEW_SCH_WORK_KIND_SYM2 = "SYM2";	//Slab 동내이적
	public final static String NEW_SCH_WORK_KIND_SYM3 = "SYM3";	//Slab 동내이적
	public final static String NEW_SCH_WORK_KIND_STSL = "STSL";		//Slab 동간보급상차
	public final static String NEW_SCH_WORK_KIND_STML = "STML";	//Slab 동간이적상차
	public final static String NEW_SCH_WORK_KIND_STM2 = "STM2";		//Slab 동간이적상차
	public final static String NEW_SCH_WORK_KIND_STMU = "STMU";	//Slab 대차하차(1)
	public final static String NEW_SCH_WORK_KIND_STM4 = "STM4";		//Slab 대차하차(2)
	public final static String NEW_SCH_WORK_KIND_SSLI = "SSLI";		//Slab Scarfing 보급
	public final static String NEW_SCH_WORK_KIND_SSLO = "SSLO";		//Slab Scarfing 추출
	public final static String NEW_SCH_WORK_KIND_SSTO = "SSTO";		//Slab Scarfing Take Out
	public final static String NEW_SCH_WORK_KIND_SWLI = "SWLI";		//Slab W/B 보급
	public final static String NEW_SCH_WORK_KIND_SCLI = "SCLI";		//Slab CTC 보급
	public final static String NEW_SCH_WORK_KIND_SCL2 = "SCL2";		//Slab STE 비상보급
	public final static String NEW_SCH_WORK_KIND_SWTO = "SWTO";	//Slab W/B Take Out 
	public final static String NEW_SCH_WORK_KIND_SHLO = "SHLO";		//Slab H/B Line Off
	public final static String NEW_SCH_WORK_KIND_SRLO = "SRLO";		//Slab ROT Line Off
	public final static String NEW_SCH_WORK_KIND_SRLI = "SRLI";		//Slab ROT Line In
	public final static String NEW_SCH_WORK_KIND_SHSI = "SHSI";		//Slab Hand Scarfing 보급
	public final static String NEW_SCH_WORK_KIND_SHSO = "SHSO";	//Slab Hand Scarfing 추출  
	public final static String NEW_SCH_WORK_KIND_SRPI = "SRPI";		//Slab 시편재 보급
	public final static String NEW_SCH_WORK_KIND_SRPO = "SRPO";	//Slab 시편재 추출  
	public final static String NEW_SCH_WORK_KIND_SPML = "SPML";	//Slab 팔레트이적상차
	public final static String NEW_SCH_WORK_KIND_SPMU = "SPMU";	//Slab 팔레트이적하차
	public final static String NEW_SCH_WORK_KIND_SVFL = "SVFL";		//Slab 제품출하상차
	
	/* 공통 현재 진도코드 */
	public final static String STOCK_STAT_D = "D";			//정정작업지시
	public final static String STOCK_STAT_E = "E";			//압연작업지시
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
	public final static String CURR_PROG_CD_SLAB_0 	= "0";	//Slab구입등록(품질)				11  
	public final static String CURR_PROG_CD_SLAB_1 	= "1";	//Slab구입확정(품질)				12  
	public final static String CURR_PROG_CD_SLAB_3 	= "3";	//생산종료						3S  
	public final static String CURR_PROG_CD_SLAB_A 	= "A";	//Slab정정작업대기/수입검사대기	AS  
	public final static String CURR_PROG_CD_SLAB_B 	= "B";	//지시대기/이송지시대기			BS  
	public final static String CURR_PROG_CD_SLAB_C 	= "C";	//작업대기/이송대기				CS  
	public final static String CURR_PROG_CD_SLAB_D 	= "D";	//이송지시대기/정정작업대기		DS  
	public final static String CURR_PROG_CD_SLAB_E 	= "E";	//이송작업대기/압연지시대기		ES  
	public final static String CURR_PROG_CD_SLAB_F 	= "F";	//판정보류/압연작업대기			FS  
	public final static String CURR_PROG_CD_SLAB_K 	= "K";	//출하지시대기/출하작업지시  		KS  
	public final static String CURR_PROG_CD_SLAB_L 	= "L";	//운송대기/출하작업대기			LS  
	public final static String CURR_PROG_CD_SLAB_M	= "M";	//출하완료/출하완료				MS  
	public final static String CURR_PROG_CD_SLAB_Y 	= "Y";	//재공충당대기/판정보류			YS  
	public final static String CURR_PROG_CD_SLAB_Z 	= "Z";	//제품충당대기/충당대기			ZS  
	public final static String CURR_PROG_CD_SLAB_N 	= "N";	//운송지시대기[신규추가]			NS  
	public final static String CURR_PROG_CD_SLAB_G 	= "G";	//종합판정대기[신규추가]			GS  
	public final static String CURR_PROG_CD_SLAB_H 	= "H";	//입고대기[신규추가]				HS  
	public final static String CURR_PROG_CD_SLAB_J 	= "J";	//반납대기[신규추가]				JS  
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
	public final static String CURR_PROG_CD_COIL_1 	= "1";	//생산예정		1C  
	public final static String CURR_PROG_CD_COIL_3 	= "3";	//생산종료		3C  
	public final static String CURR_PROG_CD_COIL_A 	= "A";	//재질판정대기	AC  
	public final static String CURR_PROG_CD_COIL_R 	= "R";	//재질판정대기	AC 
	public final static String CURR_PROG_CD_COIL_B 	= "B";	//정정작업지시  	BC  
	public final static String CURR_PROG_CD_COIL_C 	= "C";	//정정작업대기	CC  
	public final static String CURR_PROG_CD_COIL_D 	= "D";	//이송작업지시  	DC  
	public final static String CURR_PROG_CD_COIL_E 	= "E";	//이송작업대기	EC  
	public final static String CURR_PROG_CD_COIL_F 	= "F";	//판정보류		FC  
	public final static String CURR_PROG_CD_COIL_G 	= "G";	//종합판정대기	GC  
	public final static String CURR_PROG_CD_COIL_H 	= "H";	//입고대기		HG  
	public final static String CURR_PROG_CD_COIL_J 	= "J";	//반납 대기		JG  
	public final static String CURR_PROG_CD_COIL_K 	= "K";	//출하작업지시  	KG  
	public final static String CURR_PROG_CD_COIL_L 	= "L";	//출하작업대기	LG  
	public final static String CURR_PROG_CD_COIL_M 	= "M";	//출하완료		MG  
	public final static String CURR_PROG_CD_COIL_P 	= "P";	//인도완료		PG  
	public final static String CURR_PROG_CD_COIL_N 	= "N";	//운송지시대기	NG  
	public final static String CURR_PROG_CD_COIL_X 	= "X";	//경매대상선정	XG 
	public final static String CURR_PROG_CD_COIL_Y 	= "Y";	//재공충당대기	YG  
	public final static String CURR_PROG_CD_COIL_Z 	= "Z";	//제품충당대기	ZG  
	
	public final static String SCRAP_CAUSE_GP_I 	= "I";			// I:입고
	public final static String SCRAP_CAUSE_GP_B 	= "B";		// B:보류
	public final static String SCRAP_CAUSE_GP_S 	= "S";		// S:Scrap
	public final static String SCRAP_CAUSE_GP_C 	= "C";		// C:차공정
	public final static String SCRAP_CAUSE_GP_J 	= "J";		// J:재작업
		     					
	public final static String SCH_WORK_LOC_DECISION_METHOD_S 	= "S"; 	// SCHEDULE 에서 검색
	public final static String SCH_WORK_LOC_DECISION_METHOD_O 	= "O"; 	// OPERATOR 지정위치
	
	public final static String WORK_PROG_STAT_W		= "W"; //대기
	public final static String WORK_PROG_STAT_1 	= "1"; //UP지시
	public final static String WORK_PROG_STAT_2 	= "2"; //UP실적
	public final static String WORK_PROG_STAT_3 	= "3"; //PUT지시
	
	public final static String SCH_WORK_STAT_S  	= "S"; //스케쥴등록
	public final static String SCH_WORK_STAT_1  	= "1"; //UP지시
	public final static String SCH_WORK_STAT_2 		= "2"; //UP실적
	public final static String SCH_WORK_STAT_3  	= "3"; //PUT지시
	public final static String SCH_WORK_STAT_4  	= "4"; //PUT실적
	
	public final static String STACK_BED_ABLE_QNTY_1   = "1";
	public final static String STACK_BED_QNTY_CURR_0   = "0"; //적치Bed수량현재
	
	public final static String WT_E_PROCESS	= "E"; // 종료구분
	
	public final static String CTS_RELAY_SECT_BAY_F = "F";
	public final static String CTS_RELAY_SECT_BAY_A = "A";
	public final static String CTS_RELAY_SECT_BAY_C = "C";
	
	/* SPM, HFL 구분 */
	public final static String PROCESS_ID_1         = "1"; // 보급
	public final static String PROCESS_ID_2         = "2"; // 취소
	public final static String PROCESS_ID_3         = "3"; // 추출
	public final static String PROCESS_ID_4         = "4"; // Take-Out
	public final static String PROCESS_ID_5         = "5"; // Take-In
	
	public final static String LOCATION_1           = "1"; 
	public final static String LOCATION_2           = "2";
	public final static String LOCATION_3           = "3";
	public final static String LOCATION_4           = "4";
	public final static String LOCATION_5           = "5";
	
	/* 조업 위치 POSITION */
	public final static String PO_POSITION_D1 		= "D1";
	public final static String PO_POSITION_D5 		= "D5";
	
	/*	Slab C동 또는 3동에 잇는 Slab 검색 */ 
	public final static String SEARCH_A_BAY_GP      = "2A0%";
	public final static String SEARCH_B_BAY_GP      = "2B0%";
	public final static String SEARCH_C_BAY_GP      = "2C0%";
	public final static String SEARCH_TC_A_BAY_GP   = "2ATC%";
	public final static String SEARCH_TC_B_BAY_GP   = "2BTC%";
	public final static String SEARCH_TC_C_BAY_GP   = "2CTC%";
	public final static String SEARCH_TC_BAY_GP_11  = "2CTC11";
	public final static String SEARCH_TC_BAY_GP_12  = "2CTC12";
	public final static String SEARCH_TC_BAY_GP_21  = "2CTC21";
	public final static String SEARCH_TC_BAY_GP_22  = "2CTC22";
	public final static String SEARCH_TC_BAY_GP_31  = "2CTC31";
	public final static String SEARCH_TC_BAY_GP_32  = "2CTC32";
	
	/*	코일 공통 분기위치코드 */ 
	public final static String COIL_PROC_CODE_1F	= "1F"; //상 HFL
	public final static String COIL_PROC_CODE_2F 	= "2F"; //하 HFL
	public final static String COIL_PROC_CODE_3S 	= "3S"; //SPM
	public final static String COIL_PROC_CODE_4E 	= "4E"; //확장콘베이어
	
	/*	냉각방법코드 */ 
	public final static String COIL_COOL_METHOD_W	= "W"; //수냉
	public final static String COIL_COOL_METHOD_A 	= "A"; //공냉
	
	/*	이송수단 */ 
	public final static String HYSCO_TRANS_GP_C	= "C"; //대차
	public final static String HYSCO_TRANS_GP_T 	= "T"; //차량
	
	/*	적치기준코드 */ 
	public final static String STACK_RULE_CD_XCD	= "X-CD"; 
	public final static String STACK_RULE_CD_YCD 	= "Y-CD"; 
	
	/*	실적송신 코드, TAKE IN-OUT 실적 송신 코드 */ 
	public final static String RESULT_MODE_0		= "0"; //정상, TAKE IN
	public final static String RESULT_MODE_1 		= "1"; //이상, TAKE OUT 
	
	/*	반납 코드 */ 
	public final static String RETURN_GP_1			= "1"; //현물반납
	public final static String RETURN_GP_2 			= "2"; //정보반납
	
	/*	권상,권하 실적처리 방법 */ 
	public final static String CRANE_FUNC_N		= "N";  //차상국 Auto 작업
	public final static String CRANE_FUNC_M		= "M"; //차상국 Manual 작업
	public final static String CRANE_FUNC_L		= "L"; //지상국 작업
	public final static String CRANE_FUNC_V		= "V"; //화면 BACK_UP
	public final static String CRANE_FUNC_S		= "S"; //산적위치 수정
	public final static String CRANE_FUNC_U		= "U"; //비상조업 L2 시스템 처리
	public final static String CRANE_FUNC_B		= "B"; //비상조업 L2 산적위치 수정
	
	public final static String TC_WORK_R		= "R"; //크레인 작업요구
	public final static String TC_WORK_I		= "I";  //시스템 작업요구
	
	/*	코일 군관리  */ 
	public final static String COIL_GROUP_1			= "1"; //1군
	public final static String COIL_GROUP_2			= "2"; //2군
	public final static String COIL_GROUP_3			= "3"; //3군
	public final static String COIL_GROUP_4			= "4"; //4군
	public final static String COIL_GROUP_5			= "5"; //5군
	
	/*	HCR 구분 */ 
	public final static String ORD_HCR_GP_V	= "V"; //HCR필수
	public final static String ORD_HCR_GP_H	= "H"; //HCR가능
	public final static String ORD_HCR_GP_W	= "W"; //WCR가능 
	public final static String ORD_HCR_GP_C = "C"; //CCR필수
	
	/*	SLAB지시행선 */ 
	public final static String SLAB_WO_RT_CD_HB = "HB"; //B열연재
	public final static String SLAB_WO_RT_CD_HC = "HC"; //C열연재
	public final static String SLAB_WO_RT_CD_PA = "PA"; //후판재 
	
	/* A열연 SLAB야드 PALLET 정지위치 = 정상모드, 우천 모드 */
	public final static String  A_SLAB_PALLET_R 	= "R";  //우천모드
	public final static String  A_SLAB_PALLET_N 	= "N";	//정상모드
	
	/*
     *	G : 하단에 같은 장입순번이 있는 위치를 검색
     *	S : 하단에 같은 산적번호가 있는 위치를 검색
     *	E : 적치가능한 01단 위치를 검색
     *	P : 하단에 후순위의 장입순번이 있는 위치를 검색
     *	U : 적치가능한 02단 이상정보를 검색
     *	N : 하단에 장입순번이 없는 위치를 검색 
     *	A : A열연 Slab 야드 위치검색[두께,폭,길이 허용오차 체크]
     *  B : A열연 Slab 야드 위치검색[저장품이동조건 체크]
     *	G : 하단에 같은강종코드가 있는 위치를 검색
    */
	public final static String SLAB_TO_LOC_G = "G"; 
	public final static String SLAB_TO_LOC_S = "S"; 
	public final static String SLAB_TO_LOC_E = "E"; 
	public final static String SLAB_TO_LOC_U = "U"; 
	public final static String SLAB_TO_LOC_P = "P";
	public final static String SLAB_TO_LOC_N = "N";
	public final static String SLAB_TO_LOC_A = "A";
	public final static String SLAB_TO_LOC_B = "B";
	public final static String SLAB_TO_LOC_K = "K";
	
	
	/**
	 * Error Log 정의 (MSG0000:내부인터페이스항목점검, MSG0001:강제메세지지정, MSG0002~MSG9999:Jspeed에 등록한 Message) 
	 * @author 이영근
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Style - Code Templates
	 */
	public final static String MSG001 = "MSG0002";
	
	
	public final static String MSG_TO_01 = "저장영역에 등록된 정보가 존재하지 않습니다.";
	public final static String MSG_TO_02 = "적치가능 TO위치정보가 존재하지 않습니다.";
	public final static String MSG_TO_03 = "대차설정정보로 인해 TO위치에 적치할 수 없습니다.";
	public final static String MSG_TO_04 = "하단저장품 작업예약으로 인해 TO위치에 적치할 수 없습니다.";
	public final static String MSG_TO_05 = "작업을 할당할 크레인이 설정되어 있지 않습니다.";
	public final static String MSG_TO_06 = "할당된 크레인의 설정모드가 작업불가 상태입니다.";
	
	
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
	public final static String DMYDR022	="DMYDR022";   //외판슬라브운송상차지시 
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
	public final static String DMYDR060	="DMYDR060";   //코일제품운송상차지시 
	public final static String DMYDR070	="DMYDR070";   //코일이송상차대기장도착PDA 
	public final static String DMYDR073	="DMYDR073";   //코일이송하차대기장도착PDA 

	
	
}
