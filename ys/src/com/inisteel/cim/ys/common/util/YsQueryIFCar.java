package com.inisteel.cim.ys.common.util;

public interface YsQueryIFCar {

	

	/**
	 * <pre>
	 * 
com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike
-- 적치열 조회
SELECT YS_STK_COL_GP
      ,REG_DDTT
      ,REGISTER
      ,MOD_DDTT
      ,MODIFIER
      ,DEL_YN
      ,YD_GP
      ,YD_BAY_GP
      ,YD_EQP_GP
      ,YD_STK_COL_NO
      ,YD_STK_COL_ACT_STAT
      ,YD_STK_COL_RULE_YAXIS
      ,YD_STK_COL_W
      ,YD_STK_COL_L
      ,YD_CAR_USE_GP
      ,TRN_EQP_CD
      ,CAR_NO
      ,CARD_NO
      ,WLOC_CD
      ,YD_PNT_CD
  FROM TB_YS_STKCOL A
  WHERE WLOC_CD LIKE NVL(:V_WLOC_CD,'*') || '%'
   AND YS_STK_COL_GP LIKE NVL(SUBSTR(:V_YS_STK_COL_GP,0,2),'') || '%' 
   AND DEL_YN='N'
   AND YD_STK_COL_ACT_STAT<>'N'
--   AND NOT EXISTS ( SELECT YD_CARLD_STOP_LOC FROM TB_YS_CARSCH  B
--                     WHERE DEL_YN='N'
--                       AND YD_CAR_PROG_STAT IN ('1','2','3','4','5')
--                       AND B.YD_CARLD_STOP_LOC=A.YS_STK_COL_GP)
 ORDER BY YS_STK_COL_GP
 
	 * </pre>
	 */
	
	public final static String getYdStkcolColGpLike = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike";
	
	

	/**
	 * <pre>
com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat
-- TB_YS_STKCOL 등록
UPDATE TB_YS_STKCOL
   SET MOD_DDTT     = SYSDATE             
	 , MODIFIER     = :V_MODIFIER             
	 , YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
	 , TRN_EQP_CD   = :V_TRN_EQP_CD     
	 , CAR_NO       = :V_CAR_NO       
	 , CARD_NO      = :V_CARD_NO           
     , YD_STKBED_USG_CD = NVL(:V_STKBED_USG_CD,YD_STKBED_USG_CD)
     , YD_CAR_USE_GP = NVL(:V_YD_CAR_USE_GP,YD_CAR_USE_GP)
WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	 * </pre>
	 */
	
	public final static String updYdStkcolByColActStat = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStat";
	

	

	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSchPntWord
-- 준비스케줄 포인트지시 여부
UPDATE TB_YS_PREPSCH
   SET MODIFIER       = :V_MODIFIER
     , MOD_DDTT       = SYSDATE
     , CAR_GP         = '1'       -- 포인트 지시 송신
     , YD_TO_LOC_GUIDE = SUBSTR(:V_TRN_EQP_CD,1,8)
 WHERE YD_PREP_SCH_ID = :V_YD_PREP_SCH_ID
   AND DEL_YN         = 'N'

	 * </pre>
	 */
	
	public final static String updPrepSchPntWord = "com.inisteel.cim.ys.common.dao.YsCommDAO.updPrepSchPntWord";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCarschByWloc
-- TB_YS_CARSCH 등록 
UPDATE TB_YS_CARSCH
   SET MODIFIER     		= :V_MODIFIER
     , MOD_DDTT     		= SYSDATE
     , SPOS_WLOC_CD         = DECODE( :V_TRN_WRK_FULLVOID_GP, 'E', :V_SPOS_WLOC_CD,										SPOS_WLOC_CD)
     , YD_CARLD_STOP_LOC    = DECODE( :V_TRN_WRK_FULLVOID_GP, 'E', :V_YD_CARLD_STOP_LOC,								YD_CARLD_STOP_LOC)
     , YD_CARLD_PNT_WO_DT   = DECODE( :V_TRN_WRK_FULLVOID_GP, 'E', TO_DATE(:V_YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS'),	YD_CARLD_PNT_WO_DT)
     , YD_PNT_CD1           = DECODE( :V_TRN_WRK_FULLVOID_GP, 'E', :V_YD_PNT_CD1,										YD_PNT_CD1)
     , ARR_WLOC_CD          = DECODE( :V_TRN_WRK_FULLVOID_GP, 'F', :V_ARR_WLOC_CD,										ARR_WLOC_CD)
     , YD_CARUD_STOP_LOC    = DECODE( :V_TRN_WRK_FULLVOID_GP, 'F', :V_YD_CARUD_STOP_LOC,								YD_CARUD_STOP_LOC)
     , YD_CARUD_PNT_WO_DT   = DECODE( :V_TRN_WRK_FULLVOID_GP, 'F', TO_DATE(:V_YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS'),	YD_CARUD_PNT_WO_DT)
     , YD_PNT_CD3           = DECODE( :V_TRN_WRK_FULLVOID_GP, 'F', :V_YD_PNT_CD3,										YD_PNT_CD3)
     , YD_CARLD_LEV_LOC     = DECODE( :V_TRN_WRK_FULLVOID_GP, 'E', :V_YD_CARLD_STOP_LOC,								YD_CARLD_LEV_LOC) -- 2025.09.15 항목 추가 - 상차정지위치와 같음
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 
	 * </pre>
	 */
	
	public final static String updYdCarschByWloc = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCarschByWloc";
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd
-- 차량스케줄을 조회
SELECT *
  FROM (
        SELECT YD_CAR_SCH_ID        AS YD_CAR_SCH_ID                          
             , REGISTER             AS REGISTER
             , TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
             , MODIFIER             AS MODIFIER
             , TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
             , DEL_YN               AS DEL_YN
             , YD_EQP_ID            AS YD_EQP_ID
             , YD_CAR_USE_GP        AS YD_CAR_USE_GP
             , CAR_NO               AS CAR_NO
             , TRN_EQP_CD           AS TRN_EQP_CD
             , CAR_KIND             AS CAR_KIND
             , TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE
             , YD_EQP_WRK_STAT      AS YD_EQP_WRK_STAT
             , YD_WRK_PROG_STAT     AS YD_WRK_PROG_STAT
             , YD_EQP_WRK_SH        AS YD_EQP_WRK_SH
             , YD_EQP_WRK_WT        AS YD_EQP_WRK_WT
             , YS_STK_BED_TP        AS YS_STK_BED_TP
             , SPOS_WLOC_CD         AS SPOS_WLOC_CD
             , ARR_WLOC_CD          AS ARR_WLOC_CD
             , YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
             , TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_LEV_DT
             , TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_PNT_WO_DT
             , NVL(YD_PNT_CD1,'0000') AS YD_PNT_CD1
             , YD_PNT_CD2           AS YD_PNT_CD2
             , YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
             , YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
             , YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
             , TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_ARR_DT
             , TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_ST_DT
             , TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_CMPL_DT
             , YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
             , TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS') AS YD_CARLD_CHK_DT
             , TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_LEV_DT
             , TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_PNT_WO_DT
             , NVL(YD_PNT_CD3,'0000') AS YD_PNT_CD3
             , YD_PNT_CD4           AS YD_PNT_CD4
             , YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
             , YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
             , YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
             , TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_ARR_DT
             , TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_CHK_DT
             , TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_ST_DT
             , TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS') AS YD_CARUD_CMPL_DT
             , YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
             , YD_TRN_WRK_DELY_CD   AS YD_TRN_WRK_DELY_CD
             , CARD_NO              AS CARD_NO
             , YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
             , FRTOMOVE_PLANT_GP    AS FRTOMOVE_PLANT_GP      
             , PROC_TO              AS PROC_TO                
             , RENTPROC_CD          AS RENTPROC_CD            
             , YD_FRTOMOVE_YD_GP    AS YD_FRTOMOVE_YD_GP      
             , YD_FRTOMOVE_BAY_GP   AS YD_FRTOMOVE_BAY_GP     
             , URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
             , DEST_TEL_NO          AS DEST_TEL_NO            
             , YD_DLVRDD_RULE_DD    AS YD_DLVRDD_RULE_DD      
             , SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE      
             , SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO   
             , SHIP_CD              AS SHIP_CD                
             , SHIP_NAME            AS SHIP_NAME              
             , RSHP_HOLD_NO         AS RSHP_HOLD_NO           
             , BERTH_NO             AS BERTH_NO               
             , SAILNO               AS SAILNO                 
             , YD_CAR_WRK_GP        AS YD_CAR_WRK_GP          
             , TRANS_ORD_DATE       AS TRANS_ORD_DATE         
             , TRANS_ORD_SEQNO      AS TRANS_ORD_SEQNO   
             , (SELECT YD_STKBED_USG_CD 
                  FROM TB_YS_STKCOL B 
                 WHERE B.YS_STK_COL_GP=A.YD_CARLD_STOP_LOC 
                   AND B.YD_STKBED_USG_CD IN ('A','D','E')
               ) AS NEW_DEST_BAY
             , (SELECT YD_CARUD_STOP_LOC
                  FROM TB_YS_CARSCH C
                 WHERE C.TRN_EQP_CD = A.TRN_EQP_CD 
                   AND C.YD_CAR_SCH_ID = (SELECT MAX(YD_CAR_SCH_ID)
                                            FROM TB_YS_CARSCH 
                                           WHERE YD_CAR_PROG_STAT = 'E' 
                                             AND DEL_YN = 'Y'
                                             AND TRN_EQP_CD = C.TRN_EQP_CD 
                                         )    
               ) AS LAST_TRN_EQP_CD_LOC            
          FROM TB_YS_CARSCH A                                  
         WHERE TRN_EQP_CD = :V_TRN_EQP_CD            
           AND DEL_YN='N'
         ORDER BY YD_CAR_SCH_ID DESC , YD_CARUD_CMPL_DT DESC
         ) A
 WHERE ROWNUM<=1

	 * </pre>
	 */
	
	public final static String getYdCarschDaoTrnEqpCd = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschDaoTrnEqpCd";
	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyCarUsrGpTrnEqpCd
-- 작업예약 조회
SELECT B.YD_WBOOK_ID    AS YD_WBOOK_ID
      ,B.YD_SCH_CD
      ,A.SSTL_NO         AS SSTL_NO
--      ,A.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ
      ,A.YS_STK_COL_GP  AS YS_STK_COL_GP
      ,A.YS_STK_BED_NO  AS YS_STK_BED_NO
      ,A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
      ,A.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
  FROM TB_YS_WRKBOOKMTL A
      ,(SELECT *
          FROM (SELECT YD_WBOOK_ID
                     , YD_SCH_CD
                  FROM TB_YS_WRKBOOK
                 WHERE TRN_EQP_CD = :V_TRN_EQP_CD
                     AND DEL_YN='N'
                 ORDER BY YD_WBOOK_ID DESC
                ) C
          WHERE ROWNUM<=1
        ) B
 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
 ORDER BY YS_STK_COL_GP,YS_STK_BED_NO,YS_STK_LYR_NO

	 * </pre>
	 */
	
	public final static String getWorkBookMtlbyCarUsrGpTrnEqpCd = "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyCarUsrGpTrnEqpCd";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd
-- 적치열 조회
SELECT A.YS_STK_COL_GP                         AS YS_STK_COL_GP
      ,TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
      ,A.REGISTER                              AS REGISTER
      ,TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
      ,A.MODIFIER                              AS MODIFIER
      ,A.DEL_YN                                AS DEL_YN
      ,A.YD_GP                                 AS YD_GP
      ,A.YD_BAY_GP                             AS YD_BAY_GP
      ,A.YD_EQP_GP                             AS YD_EQP_GP
      ,A.YD_STK_COL_NO                         AS YD_STK_COL_NO
      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.YD_STK_COL_ACT_STAT   ELSE A.YD_STK_COL_ACT_STAT  END ) AS YD_STK_COL_ACT_STAT     
      ,A.YD_STK_COL_RULE_XAXIS                 AS YD_STK_COL_RULE_XAXIS 
      ,A.YD_STK_COL_RULE_YAXIS                 AS YD_STK_COL_RULE_YAXIS 
      ,A.YD_STK_COL_W                          AS YD_STK_COL_W                   
      ,A.YD_STK_COL_L                          AS YD_STK_COL_L                   
      ,A.YD_CAR_USE_GP                         AS YD_CAR_USE_GP                 
      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.TRN_EQP_CD ELSE A.TRN_EQP_CD END)    AS TRN_EQP_CD                       
      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CAR_NO  ELSE  A.CAR_NO END)                 AS CAR_NO                               
      ,(CASE WHEN A.YD_GP IN ('H','J') THEN  B.CARD_NO   ELSE A.CARD_NO END)               AS CARD_NO                             
      ,A.WLOC_CD                               AS WLOC_CD                             
      ,A.YD_PNT_CD                             AS YD_PNT_CD   
      ,B.YD_CARPNT_CD AS YD_CARPNT_CD
      ,A.YD_GP || A.YD_BAY_GP || A.YD_EQP_GP || '02' || B.YD_STK_COL_ACT_STAT || 'M' AS YD_SCH_CD
  FROM TB_YS_STKCOL A   
     , TB_YD_CARPOINT B
 WHERE B.YD_STK_COL_GP=A.YS_STK_COL_GP
   AND A.WLOC_CD LIKE   SUBSTR(:V_WLOC_CD,1,3)||'%'
   AND A.YD_PNT_CD = :V_YD_PNT_CD

	 * </pre>
	 */
	
	public final static String getYdStkcolWLocCdandPntCd = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolWLocCdandPntCd";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl
-- TB_YS_CARFTMVMTL 종료  
UPDATE TB_YS_CARFTMVMTL
SET MODIFIER = :V_MODIFIER
 ,MOD_DDTT    = SYSDATE
, DEL_YN = :V_DEL_YN
WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    AND DEL_YN = 'N'

	 * </pre>
	 */
	
	public final static String updDelYnCarFtmvMtl = "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarFtmvMtl";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch
-- TB_YS_CARSCH 종료  
UPDATE TB_YS_CARSCH
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , DEL_YN = 'Y'
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
     AND DEL_YN = 'N'

	 * </pre>
	 */
	
	public final static String updDelYnCarSch = "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSch";
	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolTrnEqpCdToNull
-- TB_YS_STKCOL 등록
UPDATE TB_YS_STKCOL
   SET TRN_EQP_CD = NULL
     , YD_CAR_USE_GP = NULL
     , MODIFIER ='CdToNull'  
     , MOD_DDTT =SYSDATE
 WHERE TRN_EQP_CD = :V_TRN_EQP_CD

	 * </pre>
	 */
	
	public final static String updYdStkcolTrnEqpCdToNull = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolTrnEqpCdToNull";
	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl
-- 작업예약재료 삭제 
UPDATE TB_YS_WRKBOOKMTL
   SET MODIFIER    = :V_MODIFIER
      ,MOD_DDTT    = SYSDATE
      ,DEL_YN      = 'Y'
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
   AND DEL_YN      = 'N'

	 * </pre>
	 */
	
	public final static String updDelYnWrkBookMtl = "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBookMtl";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook
-- 작업예약 삭제
UPDATE TB_YS_WRKBOOK
   SET MODIFIER    = :V_MODIFIER
      ,MOD_DDTT    = SYSDATE
      ,DEL_YN      = 'Y'
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
   AND DEL_YN      = 'N'

	 * </pre>
	 */
	
	public final static String updDelYnWrkBook = "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnWrkBook";
	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr
-- 준비재료 복원 - 
UPDATE TB_YS_PREPMTL
   SET MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,DEL_YN   = 'N'
 WHERE YD_PREP_SCH_ID IN
      (SELECT YD_PREP_SCH_ID
         FROM TB_YS_PREPSCH
        WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID)

	 * </pre>
	 */
	
	public final static String updCommPrepMtlRcvr = "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepMtlRcvr";

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr
-- 준비스케줄 복원 - 
UPDATE TB_YS_PREPSCH
   SET MODIFIER    = :V_MODIFIER
      ,MOD_DDTT    = SYSDATE
      ,DEL_YN      = 'N'
      ,YD_WBOOK_ID = NULL
      ,CAR_GP = NULL
      ,YD_TO_LOC_GUIDE  = NULL
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID

	 * </pre>
	 */
	
	public final static String updCommPrepSchRcvr = "com.inisteel.cim.ys.common.dao.YsCommDAO.updCommPrepSchRcvr";
	

	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear
-- TB_YS_STKCOL 등록
UPDATE TB_YS_STKCOL
   SET MOD_DDTT     = SYSDATE             
	 , MODIFIER     = :V_MODIFIER             
	 , YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT,'N',YD_STK_COL_ACT_STAT, :V_YD_STK_COL_ACT_STAT) --// 사용불가는 생략
	 , TRN_EQP_CD   = null
	 , CAR_NO       = null
	 , CARD_NO      = null
     , YD_CAR_USE_GP = null
WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP

	 * </pre>
	 */
	
	public final static String updYdStkcolByColActStatClear = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkcolByColActStatClear";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp
-- TB_YS_STKBED 등록
UPDATE TB_YS_STKBED
   SET MOD_DDTT     = SYSDATE             
	 , MODIFIER     = :V_MODIFIER             
	 , YD_STK_BED_ACT_STAT = NVL(:V_YD_STK_BED_ACT_STAT,YD_STK_BED_ACT_STAT)
     , YD_STK_BED_WT_MAX = NVL(:V_YD_STK_BED_WT_MAX,YD_STK_BED_WT_MAX )
  WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP

	 * </pre>
	 */
	
	public final static String updYdStkbedYdStkColGp = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedYdStkColGp";
	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear
-- TB_YS_STKLYR 등록
UPDATE TB_YS_STKLYR            
   SET MOD_DDTT     = SYSDATE             
	 , MODIFIER     = :V_MODIFIER             
	 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
     , SSTL_NO = null
     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP

	 * </pre>
	 */
	
	public final static String updYdStkLyrYdStkColGpClear = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGpClear";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID
-- 차량재료 조회
SELECT A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
     , A.SSTL_NO  AS SSTL_NO
     , A.REGISTER  AS REGISTER
     , TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
     , A.MODIFIER  AS MODIFIER
     , TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
     , A.DEL_YN  AS DEL_YN
     , A.YS_STK_BED_NO  AS YS_STK_BED_NO
     , A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
     , A.YS_STK_SEQ_NO  AS YS_STK_SEQ_NO
     , A.HCR_GP  AS HCR_GP
     , A.STL_PROG_CD  AS STL_PROG_CD
     , A.YS_MTL_ITEM  AS YS_MTL_ITEM
     , B.YD_RCPT_PLN_STR_LOC
     , (SELECT ARR_YD_PNT_CD FROM USRTSA.TB_TS_MATL_FTMV_WO C
         WHERE C.TRANSWORD_SEQNO=(SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO D
                                   WHERE D.STL_NO=C.STL_NO
                                   )
           AND C.STL_NO=A.SSTL_NO ) AS ARR_YD_PNT_CD
     , B.CUST_CD     
     , B.DETAIL_ARR_CD     
     , B.HEAT_NO     
     , B.YD_MTL_L_GP             
     , B.CUST_CD || B.DETAIL_ARR_CD || B.HEAT_NO || B.YD_MTL_L_GP AS GROUP_CHK_ID            
  FROM TB_YS_CARFTMVMTL A
     , TB_YS_STOCK B
 WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
   AND A.SSTL_NO = B.SSTL_NO(+)
   AND A.DEL_YN='N'
 ORDER BY YS_STK_BED_NO, YS_STK_LYR_NO DESC

	 * </pre>
	 */
	
	public final static String getYdCarftmvmtlID = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlID";
	
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlIDLm
	-- 차량 스케줄 재료 조회 (특수강 대형야드 신예화)
	
	SELECT
	  A.YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
	, A.SSTL_NO  AS SSTL_NO
	, A.REGISTER  AS REGISTER
	, TO_CHAR(A.REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
	, A.MODIFIER  AS MODIFIER
	, TO_CHAR(A.MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
	, A.DEL_YN  AS DEL_YN
	, A.YS_STK_BED_NO  AS YS_STK_BED_NO
	, A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
	, A.YS_STK_SEQ_NO  AS YS_STK_SEQ_NO
	, A.HCR_GP  AS HCR_GP
	, A.STL_PROG_CD  AS STL_PROG_CD
	, A.YS_MTL_ITEM  AS YS_MTL_ITEM
	, B.YD_RCPT_PLN_STR_LOC
	, (
	    SELECT
	      ARR_YD_PNT_CD
	    FROM
	      USRTSA.TB_TS_MATL_FTMV_WO C
	    WHERE C.TRANSWORD_SEQNO = (
	      SELECT
	        MAX(TRANSWORD_SEQNO)
	      FROM
	        TB_TS_MATL_FTMV_WO D
	      WHERE D.STL_NO = C.STL_NO
	      )
	    AND C.STL_NO = A.SSTL_NO
	  ) AS ARR_YD_PNT_CD
	, B.CUST_CD
	, B.DETAIL_ARR_CD
	, B.HEAT_NO
	, B.YD_MTL_L_GP
	, B.CUST_CD || B.DETAIL_ARR_CD || B.HEAT_NO || B.YD_MTL_L_GP AS GROUP_CHK_ID
	FROM
	  TB_YS_CARFTMVMTL A
	, TB_YS_STOCK B
	WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	AND A.SSTL_NO = B.SSTL_NO(+)
	AND A.DEL_YN = 'N'
	ORDER BY
	  YS_STK_BED_NO
	, YS_STK_LYR_NO
	, YS_STK_SEQ_NO

	 * </pre>
	 */
	
	public final static String getYdCarftmvmtlIDLm = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlIDLm";
	

	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbookmtlId
-- 작업예약재료 조회
SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
      ,SSTL_NO  AS SSTL_NO
      ,REGISTER  AS REGISTER
      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
      ,MODIFIER  AS MODIFIER
      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
      ,DEL_YN  AS DEL_YN
      ,YS_STK_COL_GP  AS YS_STK_COL_GP
      ,YS_STK_BED_NO  AS YS_STK_BED_NO
      ,YS_STK_LYR_NO  AS YS_STK_LYR_NO
      ,YS_STK_SEQ_NO  AS YS_STK_SEQ_NO
--      ,YD_UP_COLL_SEQ  AS YD_UP_COLL_SEQ
   FROM TB_YS_WRKBOOKMTL A
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
   AND DEL_YN='N'

	 * </pre>
	 */
	
	public final static String getYdWrkbookmtlId = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbookmtlId";
	

	
	
	
	/**
	 * <pre>

 com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStockPrepSchByYdCrn
 -- 가장빠른 상차 LOT
WITH TEMP_TABLE1 AS (
    SELECT *
      FROM USRYSA.TB_YS_PREPSCH A
     WHERE A.YD_GP = :V_YD_GP
       AND A.YD_SCH_CD LIKE SUBSTR(:V_YD_SCH_CD,1,6) || '%'
       AND A.YD_PREP_WK_ST LIKE :V_YD_PREP_WK_ST || '%'
       AND NVL(A.YD_WRK_PLAN_CRN, '*') LIKE :V_YD_WRK_PLAN_CRN || '%'
       AND SUBSTR(A.YD_SCH_CD,2,1) =SUBSTR(:V_YD_PNT_CD,2,1)
       AND A.DEL_YN = 'N'
     ORDER BY A.YD_CARASGN_SEQ, A.YD_PREP_SCH_ID
), 
TEMP_TABLE2 AS (
SELECT A.SSTL_NO       
     , A.YS_MTL_ITEM   
     , A.YD_MTL_L      
     , A.YD_MTL_W      
     , A.YD_MTL_WT     
     , B.YD_PREP_SCH_ID
     , B.YD_SCH_CD
     , B.YD_GP 
     , B.YD_PREP_WK_ST
     , B.YD_TO_LOC_DCSN_MTD
     , B.YD_TO_LOC_GUIDE
     , B.ARR_WLOC_CD
     , B.YD_AIM_YD_GP
     , B.YD_AIM_BAY_GP
     , B.YD_CARASGN_SEQ
     , B.YD_EQP_WRK_SH
     , B.YD_WRK_PLAN_CRN
     , B.YS_STK_COL_GP 
     , B.YS_STK_BED_NO 
     , B.YS_STK_LYR_NO 
     , B.YS_STK_SEQ_NO 
  FROM USRYSA.TB_YS_STOCK  A
      , (
          SELECT A.YD_PREP_SCH_ID
               , A.YD_SCH_CD
               , A.YD_GP 
               , A.YD_PREP_WK_ST
               , A.YD_TO_LOC_DCSN_MTD
               , A.YD_TO_LOC_GUIDE
               , A.ARR_WLOC_CD
               , A.YD_AIM_YD_GP
               , A.YD_AIM_BAY_GP
               , A.YD_CARASGN_SEQ
               , A.YD_EQP_WRK_SH
               , A.YD_WRK_PLAN_CRN
               , B.SSTL_NO
               , B.YS_STK_COL_GP 
               , B.YS_STK_BED_NO 
               , B.YS_STK_LYR_NO 
               , B.YS_STK_SEQ_NO 
            FROM TEMP_TABLE1 A
               , TB_YS_PREPMTL B
               , TB_YS_STKLYR C
           WHERE A.YD_PREP_SCH_ID = B.YD_PREP_SCH_ID
             AND B.SSTL_NO = C.SSTL_NO
             AND C.YD_STK_LYR_MTL_STAT = 'C'
             AND B.DEL_YN = 'N'
           ORDER BY A.YD_CARASGN_SEQ ASC, A.YD_PREP_SCH_ID ASC
      ) B
 WHERE A.SSTL_NO = B.SSTL_NO
   AND A.DEL_YN = 'N'
 )
 SELECT *
   FROM TEMP_TABLE2 B
  WHERE YD_PREP_SCH_ID = (SELECT YD_PREP_SCH_ID
                            FROM 
                                 (SELECT YD_PREP_SCH_ID 
                                    FROM TEMP_TABLE2 
                                   ORDER BY YD_CARASGN_SEQ ASC, YD_PREP_SCH_ID ASC)
                           WHERE ROWNUM <= 1)
  ORDER BY B.YS_STK_COL_GP ASC,B.YS_STK_BED_NO DESC,B.YS_STK_LYR_NO DESC

	 * </pre>
	 */
	
	public final static String getYdStockPrepSchByYdCrn = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStockPrepSchByYdCrn";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchruleCrn
-- 스케줄기준 조회 
SELECT A.YD_GP
      ,A.YD_BAY_GP
      ,YD_SCH_CD
      ,CASE WHEN A.YD_CRN_PRIOR1 <= YD_CRN_PRIOR2 THEN YD_CRN1
            WHEN A.YD_CRN_PRIOR2 < YD_CRN_PRIOR1 THEN YD_CRN2
       END AS YD_WRK_CRN
      ,CASE WHEN A.YD_CRN_PRIOR1 <= YD_CRN_PRIOR2 THEN YD_CRN_PRIOR1
            WHEN A.YD_CRN_PRIOR2 < YD_CRN_PRIOR1 THEN YD_CRN_PRIOR2
       END AS YD_WRK_CRN_PRIOR 
      ,YD_SCH_CD_NM
      ,YD_SCH_CONTENTS
    FROM (
             SELECT YD_DATA_GP
                   ,YD_SCH_GP
                   ,YD_GP
                   ,YD_BAY_GP
                   ,YD_SCH_CD
                   ,YD_SCH_CD_NM
                   ,YD_SCH_CONTENTS
                   ,YD_CRN1
                   ,YD_CRN_STAT1
                   ,CASE WHEN YD_CRN_PRIOR1 <=0 THEN 99 ELSE YD_CRN_PRIOR1 END AS YD_CRN_PRIOR1
                   ,YD_CRN2
                   ,YD_CRN_STAT2
                   ,CASE WHEN YD_CRN_PRIOR2 <=0 THEN 99 ELSE YD_CRN_PRIOR2 END AS YD_CRN_PRIOR2
                   ,YD_SCH_PROH_EXN
             FROM   TB_YS_SCHRULE
         ) A
        ,(
			SELECT YD_GP
			      ,YD_BAY_GP
			      ,YD_SCH_GP
			      ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CLS, 1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1 
			      ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(CLS, 2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2 
			FROM   (
						SELECT YD_EQP_ID
						      ,YD_GP
						      ,YD_BAY_GP
						      ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
						      ,YD_EQP_GP AS YD_SCH_GP
						      ,'1' CLS
						FROM   TB_YS_EQP
						WHERE  YD_EQP_ID IN (SELECT YD_CRN1 FROM TB_YS_SCHRULE WHERE YD_DATA_GP = 'M' AND YD_SCH_CD LIKE :V_YD_SCH_CD || '%')
						UNION ALL
						SELECT YD_EQP_ID
						      ,YD_GP
						      ,YD_BAY_GP
						      ,DECODE(YD_EQP_STAT,'B','C','O') AS YD_EQP_STAT
						      ,YD_EQP_GP AS YD_SCH_GP
						      ,'2' CLS
						FROM   TB_YS_EQP
						WHERE  YD_EQP_ID IN (SELECT YD_CRN2 FROM TB_YS_SCHRULE WHERE YD_DATA_GP = 'M' AND YD_SCH_CD LIKE :V_YD_SCH_CD || '%')
			       )
			GROUP BY YD_GP, YD_BAY_GP , YD_SCH_GP             
         ) B
    WHERE A.YD_SCH_CD LIKE :V_YD_SCH_CD || '%'
    AND   A.YD_DATA_GP = 'M'
    AND   A.YD_SCH_GP = B.YD_SCH_GP
    AND   A.YD_GP = B.YD_GP
    AND   A.YD_BAY_GP = B.YD_BAY_GP
    AND   A.YD_CRN_STAT1 = B.STAT1
    AND   A.YD_CRN_STAT2 = B.STAT2
    ORDER BY A.YD_GP, A.YD_BAY_GP, A.YD_SCH_CD 

	 * </pre>
	 */
	
	public final static String getYdSchruleCrn = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdSchruleCrn";
	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook
--작업예약 등록 
INSERT INTO TB_YS_WRKBOOK (
       YD_WBOOK_ID        --야드작업예약ID
     , YD_GP              --야드구분
     , YD_BAY_GP          --야드동구분
     , YD_SCH_CD          --야드스케쥴코드
     , YD_SCH_PRIOR       --야드스케쥴우선순위
     , YD_SCH_PROG_STAT   --야드스케쥴진행상태
     , YD_SCH_ST_GP       --야드스케쥴기동구분
     , YD_SCH_REQ_GP      --야드스케쥴요청구분
     , YD_AIM_YD_GP       --야드목표야드구분
     , YD_AIM_BAY_GP      --야드목표동구분
     , YD_TO_LOC_DCSN_MTD --야드To위치결정방법
     , YD_TO_LOC_GUIDE    --야드To위치Guide
     , YD_WRK_PLAN_TCAR   --야드작업계획대차
     , YD_CAR_USE_GP      --야드차량사용구분
     , TRN_EQP_CD         --운송장비코드
     , CAR_NO             --차량번호
     , CARD_NO            --카드번호
     , PTOP_PLNT_GP       --조업공장구분
     , DEST_TEL_NO        --목적지전화번호
     , DIST_SHIPASSIGN_GP --출하배선지시구분 
     , YD_WRK_PLAN_CRN    --야드작업계획크레인
     , REGISTER           --등록자
     , REG_DDTT           --등록일시
     , MODIFIER           --수정자
     , MOD_DDTT           --수정일시
     , DEL_YN             --삭제유무
     , CAR_YD_WBOOK_ID
) VALUES (
      :V_YD_WBOOK_ID
     ,:V_YD_GP
     ,:V_YD_BAY_GP
     ,:V_YD_SCH_CD
     ,TO_NUMBER(:V_YD_SCH_PRIOR)
     ,:V_YD_SCH_PROG_STAT
     ,:V_YD_SCH_ST_GP
     ,:V_YD_SCH_REQ_GP
     ,:V_YD_AIM_YD_GP
     ,:V_YD_AIM_BAY_GP
     ,:V_YD_TO_LOC_DCSN_MTD
     ,:V_YD_TO_LOC_GUIDE
     ,:V_YD_WRK_PLAN_TCAR
     ,:V_YD_CAR_USE_GP
     ,:V_TRN_EQP_CD
     ,:V_CAR_NO
     ,:V_CARD_NO
     ,:V_PTOP_PLNT_GP
     ,:V_DEST_TEL_NO
     ,:V_DIST_SHIPASSIGN_GP
     ,:V_YD_WRK_PLAN_CRN      
     ,:V_MODIFIER
     ,SYSDATE
     ,:V_MODIFIER
     ,SYSDATE
     ,'N'
     ,:V_CAR_YD_WBOOK_ID
)

	 * </pre>
	 */
	
	public final static String insWrkBook = "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBook";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl
-- 작업예약재료 등록 
INSERT INTO TB_YS_WRKBOOKMTL (
  YD_WBOOK_ID    --야드작업예약ID
 ,SSTL_NO         --재료번호
 ,YS_STK_COL_GP  --야드적치열구분
 ,YS_STK_BED_NO  --야드적치BED번호
 ,YS_STK_LYR_NO  --야드적치단번호
 ,YS_STK_SEQ_NO  --야드적치SEQ번호
, YD_UP_COLL_SEQ
 ,REGISTER       --등록자
 ,REG_DDTT       --등록일시
 ,MODIFIER       --수정자
 ,MOD_DDTT       --수정일시
 ,DEL_YN         --삭제유무
) VALUES (
  :V_YD_WBOOK_ID
 ,:V_SSTL_NO
 ,:V_YS_STK_COL_GP
 ,:V_YS_STK_BED_NO
 ,:V_YS_STK_LYR_NO
 ,:V_YS_STK_SEQ_NO
 ,:V_YD_UP_COLL_SEQ
 ,:V_MODIFIER
 ,SYSDATE
 ,:V_MODIFIER
 ,SYSDATE
 ,'N'
)

	 * </pre>
	 */
	
	public final static String insWrkBookMtl = "com.inisteel.cim.ys.common.dao.YsCommDAO.insWrkBookMtl";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepmtlByPrepSchIdYN
-- 준비작업 재료 삭제
UPDATE USRYSA.TB_YS_PREPMTL
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , DEL_YN = :V_DEL_YN
WHERE YD_PREP_SCH_ID = REPLACE(:V_YD_PREP_SCH_ID,' ','')

	 * </pre>
	 */
	
	public final static String delYdPrepmtlByPrepSchIdYN = "com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepmtlByPrepSchIdYN";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepSch
-- 준비스케줄 삭제
UPDATE USRYSA.TB_YS_PREPSCH
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , DEL_YN = :V_DEL_YN
     , YD_WBOOK_ID = :V_YD_WBOOK_ID
WHERE YD_PREP_SCH_ID = REPLACE(:V_YD_PREP_SCH_ID,' ','')

	 * </pre>
	 */
	
	public final static String delYdPrepSch = "com.inisteel.cim.ys.common.dao.YsCommDAO.delYdPrepSch";
	

	
	
	
	/**
	 * <pre>

TB_YS_CARSCH 갱신 - com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkBook2
-- TB_YS_CARSCH 갱신
UPDATE TB_YS_CARSCH
   SET MODIFIER 			= :V_MODIFIER
     , MOD_DDTT 			= SYSDATE
     , YD_PNT_CD1 			= :V_YD_PNT_CD1
     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
     , YD_CARLD_STOP_LOC 	= :V_YD_CARLD_STOP_LOC
     , YD_CARLD_ARR_DT 		= TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')
     , YD_CAR_PROG_STAT 	= :V_YD_CAR_PROG_STAT
     , TRN_EQP_CD 			= :V_TRN_EQP_CD
  --   , ARR_WLOC_CD = :V_ARR_WLOC_CD  -- WC추가
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID

	 * </pre>
	 */
	
	public final static String updYdCarSchCarWrkBook2 = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdCarSchCarWrkBook2";
	

	
	
	/**
	 * <pre>
com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWrkbook
-- TB_YS_WBOOK 수정
UPDATE TB_YS_WRKBOOK
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , YD_BAY_GP = :V_YD_BAY_GP
     , YD_SCH_CD = :V_YD_SCH_CD
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID

	 * </pre>
	 */
	
	public final static String updYdWrkbook = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWrkbook";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch
-- 차량스케줄조회
SELECT 
    YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
    ,REGISTER AS REGISTER
    ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
    ,MODIFIER AS MODIFIER
    ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
    ,DEL_YN AS DEL_YN
    ,YD_EQP_ID AS YD_EQP_ID
    ,YD_CAR_USE_GP AS YD_CAR_USE_GP
    ,CAR_NO AS CAR_NO
    ,TRN_EQP_CD AS TRN_EQP_CD
    ,CAR_KIND AS CAR_KIND
    ,TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE 
    ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
    ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
    ,NVL(YD_EQP_WRK_SH,'0')  AS YD_EQP_WRK_SH
    ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
    ,YS_STK_BED_TP  AS YD_STK_BED_TP
    ,SPOS_WLOC_CD  AS SPOS_WLOC_CD
    ,(CASE WHEN ARR_WLOC_CD IS NULL THEN (SELECT 
                                                    (SELECT /*+ INDEX(D PK_PT_STLFRTOMOVE) 
                                                            D.ARR_WLOC_CD
                                                        FROM TB_PB_STLFRTOMOVE D
                                                       WHERE D.TRANSWORD_SEQNO=(SELECT/*+ INDEX_DESC(D PK_PT_STLFRTOMOVE)
                                                                            MAX(TRANSWORD_SEQNO) 
                                                                         FROM TB_PB_STLFRTOMOVE K
                                                                         WHERE D.SSTL_NO=K.SSTL_NO
                                                                          AND ROWNUM<=1)
                                                         AND B.SSTL_NO =D.SSTL_NO
                                                         ) AS ARR_WLOC_CD
                                             FROM TB_YS_CARSCH A
                                                 , TB_YS_STKLYR B                                              
                                              WHERE A.YD_CARLD_STOP_LOC=B.YS_STK_COL_GP                                          
                                                AND A.DEL_YN='N'
                                                AND A.TRN_EQP_CD=NVL(C.TRN_EQP_CD,C.CAR_NO)                                              
                                                AND ROWNUM<=1 ) 
          ELSE ARR_WLOC_CD END) AS ARR_WLOC_CD
    ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
    ,TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_LEV_DT
    ,TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_PNT_WO_DT
    ,YD_PNT_CD1 AS YD_PNT_CD1
    ,YD_PNT_CD2 AS YD_PNT_CD2
    ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
    ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
    ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
    ,TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ARR_DT
    ,TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ST_DT
    ,TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CMPL_DT
    ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
    ,TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CHK_DT
    ,TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_LEV_DT
    ,TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_PNT_WO_DT
    ,YD_PNT_CD3 AS YD_PNT_CD3
    ,YD_PNT_CD4 AS YD_PNT_CD4
    ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
    ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
    ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
    ,TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ARR_DT
    ,TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CHK_DT
    ,TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
    ,TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
    ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
    ,YD_TRN_WRK_DELY_CD  AS YD_TRN_WRK_DELY_CD
    ,CARD_NO  AS CARD_NO
    ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
    ,FRTOMOVE_PLANT_GP AS FRTOMOVE_PLANT_GP
    ,PROC_TO AS PROC_TO
    ,RENTPROC_CD AS RENTPROC_CD
    ,YD_FRTOMOVE_YD_GP AS YD_FRTOMOVE_YD_GP
    ,YD_FRTOMOVE_BAY_GP AS YD_FRTOMOVE_BAY_GP
    ,URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
    ,DEST_TEL_NO AS DEST_TEL_NO
    ,YD_DLVRDD_RULE_DD AS YD_DLVRDD_RULE_DD
    ,SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE
    ,SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO
    ,SHIP_CD AS SHIP_CD
    ,SHIP_NAME AS SHIP_NAME
    ,RSHP_HOLD_NO AS RSHP_HOLD_NO
    ,BERTH_NO AS BERTH_NO
    ,SAILNO AS SAILNO
    ,YD_CAR_WRK_GP AS YD_CAR_WRK_GP
    ,TRANS_ORD_DATE AS TRANS_ORD_DATE
    ,TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
    ,YD_BAYIN_WO_SEQ
    ,YD_CAR_RCPT_CHK_YN
    ,YD_CAR_ISSUE_CHK_YN
    ,YD_CAR_RCPT_CHECKER
    ,YD_CAR_ISSUE_CHECKER    
    ,SUBSTR(YD_CARLD_STOP_LOC,1,1) AS YD_GP
    ,(SELECT COUNT(*)
     FROM TB_YS_CRNSCH
     WHERE YD_WBOOK_ID = YD_CARLD_WRK_BOOK_ID
     AND DEL_YN = 'N') YD_CRN_SCH_ID
     , CASE WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC
            ELSE YD_CARUD_STOP_LOC END AS CAR_LOC
FROM TB_YS_CARSCH C
WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID

	 * </pre>
	 */
	
	public final static String getYdCarsch = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarsch";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCarSchCarWrkUD
-- TB_YS_CARSCH 등록
UPDATE TB_YS_CARSCH
   SET MODIFIER 				= :V_MODIFIER
     , MOD_DDTT 				= SYSDATE
     , YD_CARUD_WRK_BOOK_ID 	= :V_YD_CARUD_WRK_BOOK_ID
     , YD_CAR_PROG_STAT 		= :V_YD_CAR_PROG_STAT
     , ARR_WLOC_CD      		= NVL(:V_ARR_WLOC_CD,		ARR_WLOC_CD)
     , YD_CARUD_STOP_LOC   		= NVL(:V_YD_CARUD_STOP_LOC,	YD_CARUD_STOP_LOC)
     , YD_CARUD_LEV_DT		 	= NVL(YD_CARUD_LEV_DT,		SYSDATE)
     , YD_CARUD_ARR_DT		 	= NVL(YD_CARUD_ARR_DT,		SYSDATE)
 WHERE YD_CAR_SCH_ID 			= :V_YD_CAR_SCH_ID

	 * </pre>
	 */
	
	public final static String updYsCarSchCarWrkUD = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYsCarSchCarWrkUD";
	

	
	
	
	/**
	 * <pre>

COM.INISTEEL.CIM.YS.COMMON.DAO.YSCOMMDAO.UPDYDWRKBOOKMTL
-- TB_YS_WRKBOOKMTL 등록
UPDATE TB_YS_WRKBOOKMTL
  SET MODIFIER = :V_MODIFIER
    , MOD_DDTT = SYSDATE
    , YS_STK_COL_GP = :V_YS_STK_COL_GP
    , YS_STK_BED_NO = NVL(:V_YS_STK_BED_NO,YS_STK_BED_NO)
    , YS_STK_LYR_NO = NVL(:V_YS_STK_LYR_NO,YS_STK_LYR_NO)
    , YS_STK_SEQ_NO = NVL(:V_YS_STK_SEQ_NO,YS_STK_SEQ_NO)
  WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
    AND SSTL_NO = :V_SSTL_NO

	 * </pre>
	 */
	
	public final static String updYdWrkbookmtl = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdWrkbookmtl";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp2
-- TB_YS_STKLYR 등록
UPDATE TB_YS_STKLYR            
   SET MOD_DDTT     = SYSDATE             
	 , MODIFIER     = :V_MODIFIER             
	 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
     , SSTL_NO = :V_SSTL_NO
     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP
 AND YS_STK_BED_NO = :V_YS_STK_BED_NO
 AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
 AND YS_STK_SEQ_NO = :V_YS_STK_SEQ_NO

	 * </pre>
	 */
	
	public final static String updYdStkLyrYdStkColGp2 = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp2";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbook
-- 작업예약 조회
SELECT YD_WBOOK_ID  AS YD_WBOOK_ID
      ,REGISTER  AS REGISTER
      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
      ,MODIFIER  AS MODIFIER
      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
      ,DEL_YN  AS DEL_YN
      ,YD_GP  AS YD_GP
      ,YD_BAY_GP  AS YD_BAY_GP
      ,YD_SCH_CD  AS YD_SCH_CD
      ,YD_SCH_PRIOR  AS YD_SCH_PRIOR
      ,YD_SCH_PROG_STAT  AS YD_SCH_PROG_STAT
      ,YD_SCH_ST_GP  AS YD_SCH_ST_GP
      ,YD_SCH_REQ_GP  AS YD_SCH_REQ_GP
      ,YD_AIM_YD_GP  AS YD_AIM_YD_GP
      ,YD_AIM_BAY_GP  AS YD_AIM_BAY_GP
      ,YD_CTS_RELAY_YN  AS YD_CTS_RELAY_YN
      ,YD_CTS_RELAY_BAY_GP  AS YD_CTS_RELAY_BAY_GP
      ,YD_TO_LOC_DCSN_MTD AS YD_TO_LOC_DCSN_MTD
      ,YD_TO_LOC_GUIDE  AS YD_TO_LOC_GUIDE
      ,YD_WRK_PLAN_TCAR AS YD_WRK_PLAN_TCAR
      ,YD_CAR_USE_GP
      ,TRN_EQP_CD AS TRN_EQP_CD
      ,CAR_NO AS CAR_NO
      ,CARD_NO AS CARD_NO
   FROM TB_YS_WRKBOOK
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID

	 * </pre>
	 */
	
	public final static String getYdWrkbook = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdWrkbook";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike2
-- 적치열 조회
SELECT YS_STK_COL_GP
      ,REG_DDTT
      ,REGISTER
      ,MOD_DDTT
      ,MODIFIER
      ,DEL_YN
      ,YD_GP
      ,YD_BAY_GP
      ,YD_EQP_GP
      ,YD_STK_COL_NO
      ,YD_STK_COL_ACT_STAT
      ,YD_STK_COL_RULE_YAXIS
      ,YD_STK_COL_W
      ,YD_STK_COL_L
      ,YD_CAR_USE_GP
      ,TRN_EQP_CD
      ,CAR_NO
      ,CARD_NO
      ,WLOC_CD
      ,YD_PNT_CD
      ,YS_STK_COL_W_GP
--      ,YD_STK_COL_H_MAX
--      ,YS_STK_COL_BED_L_TP
  FROM TB_YS_STKCOL
 WHERE YS_STK_COL_GP LIKE :V_YS_STK_COL_GP || '%'
   AND WLOC_CD = :V_ARR_WLOC_CD
   AND DEL_YN='N'
 ORDER BY YS_STK_COL_GP

	 * </pre>
	 */
	
	public final static String getYdStkcolColGpLike2 = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdStkcolColGpLike2";
	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.insYsCarsch
-- 차량스케줄 등록 
INSERT INTO TB_YS_CARSCH
(      YD_CAR_SCH_ID        -- 야드차량스케쥴ID     : 신규 야드차량스케쥴ID
     , REGISTER             -- 등록자               : 기존 등록자 로직
     , REG_DDTT             -- 등록일시             : SYSDATE
     , YD_EQP_ID            -- 야드설비ID           : XXPT01 - 구내운송차량에 대한 기본 설비ID
     , YD_CAR_USE_GP        -- 야드차량사용구분     : L - L:구내,G:출하
     , TRN_EQP_CD           -- 운송장비코드         : 소재차량출발(TSYSJ004) 전문에 있는 운송장비코드
     , SPOS_WLOC_CD         -- 발지개소코드         : 소재차량출발(TSYSJ004) 전문에 있는 발지개소코드
     , ARR_WLOC_CD          -- 착지개소코드         : 소재차량출발(TSYSJ004) 전문에 있는 착지개소코드
     , YD_CARLD_LEV_LOC     -- 야드상차출발위치     : TB_YD_CARPOINT 테이블 발지개소코드에 해당하는 적치열
     , YD_PNT_CD1           -- 야드포인트코드1      : 소재차량출발(TSYSJ004) 전문에 있는 발지개소코드에 해당하는 야드 포인트 코드
     , YD_CARLD_STOP_LOC    -- 야드상차정지위치     : TB_YD_CARPOINT 테이블 발지개소코드에 해당하는 적치열
     , YD_PNT_CD3           -- 야드포인트코드3      : 소재차량출발(TSYSJ004) 전문에 있는 착지개소코드에 해당하는 야드 포인트 코드
     , YD_CARUD_STOP_LOC    -- 야드하차정지위치     : TB_YD_CARPOINT 테이블 착지개소코드에 해당하는 적치열
     , YD_CAR_PROG_STAT     -- 야드차량진행상태     : 1 - 상차출발
     , YD_BAYIN_WO_SEQ      -- 야드입동지시순번     : 9 - 기본값으로 설정(9)
     , YD_CARLD_LEV_DT      -- 야드상차출발일시     : SYSDATE
       )
VALUES (
      :V_YD_CAR_SCH_ID
     , :V_REGISTER
     , SYSDATE
     , :V_YD_EQP_ID
     , :V_YD_CAR_USE_GP
     , :V_TRN_EQP_CD
     , :V_SPOS_WLOC_CD
     , :V_ARR_WLOC_CD
     , NVL((SELECT YD_STK_COL_GP FROM TB_YD_CARPOINT WHERE WLOC_CD = :V_SPOS_WLOC_CD AND ROWNUM = 1),'')
     , NVL((SELECT YD_PNT_CD FROM TB_YD_CARPOINT WHERE WLOC_CD = :V_SPOS_WLOC_CD AND ROWNUM = 1),'')
     , NVL((SELECT YD_STK_COL_GP FROM TB_YD_CARPOINT WHERE WLOC_CD = :V_SPOS_WLOC_CD AND ROWNUM = 1),'')
     , NVL((SELECT YD_PNT_CD FROM TB_YD_CARPOINT WHERE WLOC_CD = :V_ARR_WLOC_CD AND ROWNUM = 1),'')
     , NVL((SELECT YD_STK_COL_GP FROM TB_YD_CARPOINT WHERE WLOC_CD = :V_ARR_WLOC_CD AND ROWNUM = 1),'')
     , :V_YD_CAR_PROG_STAT
     , :V_YD_BAYIN_WO_SEQ
     , SYSDATE
       )

	 * </pre>
	 */
	
	public final static String insYsCarsch = "com.inisteel.cim.ys.common.dao.YsCommDAO.insYsCarsch";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC2
-- 차량포인트 비활성 처리
UPDATE TB_YD_CARPOINT
   SET YD_STK_COL_ACT_STAT=:V_STAT
     , CAR_NO  =:V_CAR_NO
     , CARD_NO =:V_TRN_EQP_CD
     , MOD_DDTT=sysdate
     , MODIFIER='CarPointC'
 WHERE YD_STK_COL_GP=:V_YS_STK_COL_GP

	 * </pre>
	 */
	
	public final static String carpointtrneqpcdupdateC2 = "com.inisteel.cim.ys.common.dao.YsCommDAO.carpointtrneqpcdupdateC2";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp
-- TB_YS_STKLYR 등록
UPDATE TB_YS_STKLYR            
   SET MOD_DDTT     = SYSDATE             
	 , MODIFIER     = :V_MODIFIER             
	 , YD_STK_LYR_ACT_STAT = :V_YD_STK_LYR_ACT_STAT
     , SSTL_NO = :V_SSTL_NO
     , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
 WHERE YS_STK_COL_GP  = :V_YS_STK_COL_GP

	 * </pre>
	 */
	
	public final static String updYdStkLyrYdStkColGp = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkLyrYdStkColGp";
	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschTransDTSeq2
-- 차량스케줄  조회
SELECT *
 FROM (
SELECT 
    YD_CAR_SCH_ID  AS YD_CAR_SCH_ID
    ,REGISTER AS REGISTER
    ,TO_CHAR(REG_DDTT,'YYYYMMDDHH24MISS') AS REG_DDTT
    ,MODIFIER AS MODIFIER
    ,TO_CHAR(MOD_DDTT,'YYYYMMDDHH24MISS') AS MOD_DDTT
    ,DEL_YN AS DEL_YN
    ,YD_EQP_ID AS YD_EQP_ID
    ,YD_CAR_USE_GP AS YD_CAR_USE_GP
    ,CAR_NO AS CAR_NO
    ,TRN_EQP_CD AS TRN_EQP_CD
    ,CAR_KIND AS CAR_KIND
    ,TRANS_EQUIPMENT_TYPE AS TRANS_EQUIPMENT_TYPE 
    ,YD_EQP_WRK_STAT  AS YD_EQP_WRK_STAT
    ,YD_WRK_PROG_STAT  AS YD_WRK_PROG_STAT
    ,YD_EQP_WRK_SH  AS YD_EQP_WRK_SH
    ,YD_EQP_WRK_WT  AS YD_EQP_WRK_WT
    ,YS_STK_BED_TP  AS YS_STK_BED_TP
    ,SPOS_WLOC_CD  AS SPOS_WLOC_CD
    ,ARR_WLOC_CD  AS ARR_WLOC_CD
    ,YD_CARLD_LEV_LOC  AS YD_CARLD_LEV_LOC
    ,TO_CHAR(YD_CARLD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_LEV_DT
    ,TO_CHAR(YD_CARLD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_PNT_WO_DT
    ,(CASE WHEN YD_CAR_PROG_STAT IN('A','B','C','D','E') THEN YD_PNT_CD3 ELSE YD_PNT_CD1 END) AS YD_PNT_CD1
    ,YD_PNT_CD2 AS YD_PNT_CD2
    ,YD_CARLD_WRK_BOOK_ID  AS YD_CARLD_WRK_BOOK_ID
    ,YD_CARLD_SCH_REQ_GP  AS YD_CARLD_SCH_REQ_GP
    ,YD_CARLD_STOP_LOC  AS YD_CARLD_STOP_LOC
    ,TO_CHAR(YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ARR_DT
    ,TO_CHAR(YD_CARLD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_ST_DT
    ,TO_CHAR(YD_CARLD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CMPL_DT
    ,YD_CARLD_WRK_ACT_GP  AS YD_CARLD_WRK_ACT_GP
    ,TO_CHAR(YD_CARLD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARLD_CHK_DT
    ,TO_CHAR(YD_CARUD_LEV_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_LEV_DT
    ,TO_CHAR(YD_CARUD_PNT_WO_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_PNT_WO_DT
    ,YD_PNT_CD3 AS YD_PNT_CD3
    ,YD_PNT_CD4 AS YD_PNT_CD4
    ,YD_CARUD_WRK_BOOK_ID  AS YD_CARUD_WRK_BOOK_ID
    ,YD_CARUD_STOP_LOC  AS YD_CARUD_STOP_LOC
    ,YD_CARUD_SCH_REQ_GP  AS YD_CARUD_SCH_REQ_GP
    ,TO_CHAR(YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ARR_DT
    ,TO_CHAR(YD_CARUD_CHK_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CHK_DT
    ,TO_CHAR(YD_CARUD_ST_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_ST_DT
    ,TO_CHAR(YD_CARUD_CMPL_DT,'YYYYMMDDHH24MISS')  AS YD_CARUD_CMPL_DT
    ,YD_CARUD_WRK_ACT_GP  AS YD_CARUD_WRK_ACT_GP
    ,YD_TRN_WRK_DELY_CD  AS YD_TRN_WRK_DELY_CD
    ,CARD_NO  AS CARD_NO
    ,YD_CAR_PROG_STAT AS YD_CAR_PROG_STAT
    ,FRTOMOVE_PLANT_GP AS FRTOMOVE_PLANT_GP
    ,PROC_TO AS PROC_TO
    ,RENTPROC_CD AS RENTPROC_CD
    ,YD_FRTOMOVE_YD_GP AS YD_FRTOMOVE_YD_GP
    ,YD_FRTOMOVE_BAY_GP AS YD_FRTOMOVE_BAY_GP
    ,URGENT_FRTOMOVE_WORD_GP AS URGENT_FRTOMOVE_WORD_GP
    ,DEST_TEL_NO AS DEST_TEL_NO
    ,YD_DLVRDD_RULE_DD AS YD_DLVRDD_RULE_DD
    ,SHIPASSIGN_WORD_DATE AS SHIPASSIGN_WORD_DATE
    ,SHIPASSIGN_WORD_SEQNO AS SHIPASSIGN_WORD_SEQNO
    ,SHIP_CD AS SHIP_CD
    ,SHIP_NAME AS SHIP_NAME
    ,RSHP_HOLD_NO AS RSHP_HOLD_NO
    ,BERTH_NO AS BERTH_NO
    ,SAILNO AS SAILNO
    ,YD_CAR_WRK_GP AS YD_CAR_WRK_GP
    ,TRANS_ORD_DATE AS TRANS_ORD_DATE
    ,TRANS_ORD_SEQNO AS TRANS_ORD_SEQNO
    ,YD_BAYIN_WO_SEQ
    ,YD_CAR_RCPT_CHK_YN
    ,YD_CAR_ISSUE_CHK_YN
    ,YD_CAR_RCPT_CHECKER
    ,YD_CAR_ISSUE_CHECKER 
    ,CMBN_CARLD_YN
FROM TB_YS_CARSCH
WHERE CAR_NO LIKE :V_CAR_NO||'%'
  AND TRANS_ORD_DATE = :V_TRANS_ORD_DATE
  AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
--AND DEL_YN='N'
ORDER BY YD_CAR_SCH_ID DESC
) A
WHERE ROWNUM<=1

	 * </pre>
	 */
	
	public final static String getYdCarschTransDTSeq2 = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarschTransDTSeq2";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlBySchId
-- 차량이송 조회
SELECT 
       YD_CAR_SCH_ID 
     , SSTL_NO 
     , YS_STK_BED_NO 
     , YS_STK_LYR_NO
     , STL_PROG_CD 
     , YS_MTL_ITEM
     , YS_ROUTE_GP
     , SUBSTR(YS_MTL_ITEM, 1, 1) AS YD_MTL_GP 
  FROM TB_YS_CARFTMVMTL
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 ORDER BY YS_STK_BED_NO, YS_STK_LYR_NO

	 * </pre>
	 */
	
	public final static String getYdCarftmvmtlBySchId = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdCarftmvmtlBySchId";
	

	
	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl
-- 차량스케줄재료 삭제  
UPDATE TB_YS_CARFTMVMTL
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , DEL_YN = 'Y'
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID

	 * </pre>
	 */
	
	public final static String updDelYnCarSchMtl = "com.inisteel.cim.ys.common.dao.YsCommDAO.updDelYnCarSchMtl";
	

	
	
	/**
	 * <pre>

com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyWlocCd
-- 개소코드로 작업예약을 조회	
SELECT B.YD_WBOOK_ID    AS YD_WBOOK_ID
      ,B.YD_SCH_CD
      ,A.SSTL_NO         AS SSTL_NO
--      ,A.YD_UP_COLL_SEQ AS YD_UP_COLL_SEQ
      ,A.YS_STK_COL_GP  AS YS_STK_COL_GP
      ,A.YS_STK_BED_NO  AS YS_STK_BED_NO
      ,A.YS_STK_LYR_NO  AS YS_STK_LYR_NO
      ,A.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
  FROM TB_YS_WRKBOOKMTL A
      ,(SELECT *
          FROM (SELECT YD_WBOOK_ID
                     , YD_SCH_CD
                  FROM TB_YS_WRKBOOK
                 WHERE YD_SCH_CD LIKE (SELECT SUBSTR(YS_STK_COL_GP,0,4) || '%'  FROM TB_YS_STKCOL WHERE WLOC_CD = :V_WLOC_CD)
                     AND DEL_YN='N'
                 ORDER BY YD_WBOOK_ID DESC
                ) C
          WHERE ROWNUM<=1
        ) B
 WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
 ORDER BY YS_STK_COL_GP,YS_STK_BED_NO,YS_STK_LYR_NO

	 * </pre>
	 */
	
	public final static String getWorkBookMtlbyWlocCd = "com.inisteel.cim.ys.common.dao.YsCommDAO.getWorkBookMtlbyWlocCd";
	
	
	
	
	
	
// ***************************************************************************************
// SAMPLE
	
	/**
	 * <pre>



	 * </pre>
	 */
	
	public final static String sample = "com.inisteel.cim.ys.cbt.dao.sample";
	
// ***************************************************************************************
	
}
