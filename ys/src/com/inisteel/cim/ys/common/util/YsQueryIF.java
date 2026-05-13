package com.inisteel.cim.ys.common.util;

public interface YsQueryIF {
	/**
	 * <pre>
	com.inisteel.cim.ys.common.dao.YsCommDAO.updYdSchRuleLn
	-- 스케줄기준 수정 (특수강 신예화)
	
	UPDATE TB_YS_SCHRULE SET
	  YD_CRN_PRIOR1 = :V_M_CRN_PRIOR1
	, YD_CRN_PRIOR2 = :V_M_CRN_PRIOR2
	, YD_CRN_STAT1 = :V_YD_CRN_STAT1
	, YD_CRN_STAT2 = :V_YD_CRN_STAT2
	, MODIFIER = :V_USERID
	, MOD_DDTT = SYSDATE
	WHERE YD_SCH_CD = :V_YD_SCH_CD
	AND YD_DATA_GP = 'M'
	AND YD_SCH_GP = :V_YD_SCH_GP
	AND YD_CRN1 = :V_YD_CRN1
	AND YD_CRN2 = :V_YD_CRN2
	 * </pre>
	 */
	public final static String updYdSchRuleLn = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdSchRuleLn";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.common.dao.YsCommDAO.resetAllSchRuleLn
	-- 특수강제품창고스케줄기준 전체복구 (대형야드 신예화)
	
	UPDATE TB_YS_SCHRULE A SET
	(
	  A.YD_CRN_PRIOR1
	, A.YD_CRN_PRIOR2
	, MODIFIER
	, MOD_DDTT
	) = (
	  SELECT
	    B.YD_CRN_PRIOR1
	  , B.YD_CRN_PRIOR2
	  , :V_USERID
	  , SYSDATE
	  FROM
	    TB_YS_SCHRULE B
	  WHERE B.YD_DATA_GP = 'R'
	  AND B.YD_BAY_GP = A.YD_BAY_GP
	  AND B.YD_SCH_GP = A.YD_SCH_GP
	  AND B.YD_SCH_CD = A.YD_SCH_CD
	  )
	WHERE A.YD_DATA_GP = 'M'
	AND A.YD_GP = :V_YD_GP
	AND A.YD_BAY_GP = :V_YD_BAY_GP
	 * </pre>
	 */
	public final static String resetAllSchRuleLn = "com.inisteel.cim.ys.common.dao.YsCommDAO.resetAllSchRuleLn";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.common.dao.udtWhsPlnInfojlByCarFtmvMtl
	-- 차량재료정보 수정 (특수강 신예화)
	
	UPDATE TB_YS_CARFTMVMTL SET
	  YS_STK_BED_NO = :V_YS_STK_BED_NO
	, YS_STK_LYR_NO = :V_YS_STK_LYR_NO
	, REGISTER = :V_MODIFIER
	, REG_DDTT = SYSDATE
	, MODIFIER = :V_MODIFIER
	, MOD_DDTT = SYSDATE
	WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	AND SSTL_NO =  :V_SSTL_NO
	AND DEL_YN = 'N'
	 * </pre>
	 */
	public final static String udtWhsPlnInfojlByCarFtmvMtl = "com.inisteel.cim.ys.common.dao.udtWhsPlnInfojlByCarFtmvMtl";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.common.dao.udtWhsPlnInfojlByStkLyrLn
	-- 기존위치 CLEAR (특수강 신예화)
	
	UPDATE TB_YS_STKLYR SET
	  SSTL_NO = ''
	, YD_STK_LYR_MTL_STAT = 'E'
	, MODIFIER = :V_MODIFIER
	, MOD_DDTT = SYSDATE
	WHERE SSTL_NO = :V_SSTL_NO
	 * </pre>
	 */
	public final static String udtWhsPlnInfojlByStkLyrLn = "com.inisteel.cim.ys.common.dao.udtWhsPlnInfojlByStkLyrLn";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.common.dao.udtWhsPlnInfojlByCarStkLyr
	-- 차량위치 등록 (특수강 신예화)
	
	UPDATE TB_YS_STKLYR SET
	  SSTL_NO = :V_SSTL_NO
	, YD_STK_LYR_MTL_STAT = 'C'
	, MODIFIER = :V_MODIFIER
	, MOD_DDTT = SYSDATE
	WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	AND YS_STK_BED_NO = :V_YS_STK_BED_NO
	AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
	AND YS_STK_SEQ_NO = 1
	 * </pre>
	 */
	public final static String udtWhsPlnInfojlByCarStkLyr = "com.inisteel.cim.ys.common.dao.udtWhsPlnInfojlByCarStkLyr";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.common.dao.insLmillplnstrloc
	-- 대형압연재예정저장위치 등록 및 수정
	
	MERGE INTO USRYSA.TB_YS_LMILLPLNSTRLOC T
	USING (
	  SELECT
	    :V_PLN_BLM_NO AS PLN_BLM_NO
	  , :V_BLM_NO AS BLM_NO
	  , :V_HEAT_NO AS HEAT_NO
	  , :V_SPEC_ABBSYM AS SPEC_ABBSYM
	  , :V_ITEMNAME_CD AS ITEMNAME_CD
	  , :V_ORD_NO AS ORD_NO
	  , :V_ORD_DTL AS ORD_DTL
	  , :V_USAGE_CD AS USAGE_CD
	  , :V_BLM_WT AS BLM_WT
	  , :V_ORD_SZ AS ORD_SZ
	  , :V_YD_RCPT_PLN_STR_LOC AS YD_RCPT_PLN_STR_LOC
	  , 'N' AS DEL_YN
	  , :V_REGISTER AS REGISTER
	  , SYSDATE AS REG_DDTT
	  , SYSDATE AS MOD_DDTT
	  , :V_MODIFIER AS MODIFIER
	  FROM DUAL
	) S
	ON (
	  T.PLN_BLM_NO = S.PLN_BLM_NO
	)
	WHEN MATCHED THEN
	  UPDATE SET
	    T.BLM_NO = S.BLM_NO
	  , T.HEAT_NO = S.HEAT_NO
	  , T.SPEC_ABBSYM = S.SPEC_ABBSYM
	  , T.ITEMNAME_CD = S.ITEMNAME_CD
	  , T.ORD_NO = S.ORD_NO
	  , T.ORD_DTL = S.ORD_DTL
	  , T.USAGE_CD = S.USAGE_CD
	  , T.BLM_WT = S.BLM_WT
	  , T.ORD_SZ = S.ORD_SZ
	  , T.YD_RCPT_PLN_STR_LOC = S.YD_RCPT_PLN_STR_LOC
	  , T.MOD_DDTT = S.MOD_DDTT
	  , T.MODIFIER = S.MODIFIER
	WHEN NOT MATCHED THEN
	  INSERT (
	    PLN_BLM_NO
	  , BLM_NO
	  , HEAT_NO
	  , SPEC_ABBSYM
	  , ITEMNAME_CD
	  , ORD_NO
	  , ORD_DTL
	  , USAGE_CD
	  , BLM_WT
	  , ORD_SZ
	  , YD_RCPT_PLN_STR_LOC
	  , DEL_YN
	  , REGISTER
	  , REG_DDTT
	  , MOD_DDTT
	  , MODIFIER
	  )
	VALUES
	  (
	    S.PLN_BLM_NO
	  , S.BLM_NO
	  , S.HEAT_NO
	  , S.SPEC_ABBSYM
	  , S.ITEMNAME_CD
	  , S.ORD_NO
	  , S.ORD_DTL
	  , S.USAGE_CD
	  , S.BLM_WT
	  , S.ORD_SZ
	  , S.YD_RCPT_PLN_STR_LOC
	  , S.DEL_YN
	  , S.REGISTER
	  , S.REG_DDTT
	  , S.MOD_DDTT
	  , S.MODIFIER
	  )
	 * </pre>
	 */
	public final static String insLmillplnstrloc = "com.inisteel.cim.ys.common.dao.insLmillplnstrloc";
	
	/**
	 * <pre>
	-- Bed정보 수정 - com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbed
	UPDATE TB_YS_STKBED
	SET 
		 YD_STR_GTR_CD = :V_YD_STR_GTR_CD
		,YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT 
		,YD_STK_BED_WHIO_STAT = :V_YD_STK_BED_WHIO_STAT
		,YD_STK_BED_XAXIS = :V_YD_STK_BED_XAXIS
		,YD_STK_BED_YAXIS = :V_YD_STK_BED_YAXIS
	    ,YD_STK_BED_LYR_MAX = :V_YD_STK_BED_LYR_MAX  
		,YD_STK_BED_WT_MAX = :V_YD_STK_BED_WT_MAX
		,YD_STK_BED_H_MAX = :V_YD_STK_BED_H_MAX    
		,YD_STK_BED_L_MAX = :V_YD_STK_BED_L_MAX    
		,YD_STK_BED_W_MAX = :V_YD_STK_BED_W_MAX     
		,YD_STK_BED_XAXIS_TOL = :V_YD_STK_BED_XAXIS_TOL
		,YD_STK_BED_YAXIS_TOL = :V_YD_STK_BED_YAXIS_TOL
		,MODIFIER = :V_MODIFIER             
		,MOD_DDTT = SYSDATE             
		,YD_STK_BED_XAXIS1 = :V_YD_STK_BED_XAXIS1
		,YD_STK_BED_YAXIS1 = :V_YD_STK_BED_YAXIS1
																	
	WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
		AND  YS_STK_BED_NO = :V_YS_STK_BED_NO
	 * </pre>
	 */
	public final static String updYdStkbed = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbed";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedLyrMax
	-- MAX 단 일괄 UPDATE
	
	UPDATE TB_YS_STKBED SET
	  YD_STK_BED_LYR_MAX = :V_YD_STK_BED_LYR_MAX
	, MODIFIER = :V_MODIFIER
	WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	 * </pre>
	 */
	public final static String updYdStkbedLyrMax = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedLyrMax";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedLm
	-- YD_STK_BED_LYR_MAX 제외 Bed정보 수정 (대형야드 신예화)
	
	UPDATE TB_YS_STKBED SET
	  YD_STR_GTR_CD = :V_YD_STR_GTR_CD
	, YD_STK_BED_ACT_STAT = :V_YD_STK_BED_ACT_STAT
	, YD_STK_BED_WHIO_STAT = :V_YD_STK_BED_WHIO_STAT
	, YD_STK_BED_XAXIS = :V_YD_STK_BED_XAXIS
	, YD_STK_BED_YAXIS = :V_YD_STK_BED_YAXIS
	, YD_STK_BED_WT_MAX = :V_YD_STK_BED_WT_MAX
	, YD_STK_BED_H_MAX = :V_YD_STK_BED_H_MAX
	, YD_STK_BED_L_MAX = :V_YD_STK_BED_L_MAX
	, YD_STK_BED_W_MAX = :V_YD_STK_BED_W_MAX
	, YD_STK_BED_XAXIS_TOL = :V_YD_STK_BED_XAXIS_TOL
	, YD_STK_BED_YAXIS_TOL = :V_YD_STK_BED_YAXIS_TOL
	, MODIFIER = :V_MODIFIER
	, MOD_DDTT = SYSDATE
	, YD_STK_BED_XAXIS1 = :V_YD_STK_BED_XAXIS1
	, YD_STK_BED_YAXIS1 = :V_YD_STK_BED_YAXIS1
	WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	AND YS_STK_BED_NO = :V_YS_STK_BED_NO
	 * </pre>
	 */
	public final static String updYdStkbedLm = "com.inisteel.cim.ys.common.dao.YsCommDAO.updYdStkbedLm";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.dao.getYsStockchk
	-- 저장품 존재여부 확인
		
	SELECT A.SSTL_NO AS SSTL_NO
	  FROM TB_YS_STOCK  A
	WHERE A.SSTL_NO = :V_SSTL_NO
	 * </pre>
	 */
	public final static String getYsStockchk = "com.inisteel.cim.ys.dao.getYsStockchk";
	
	/**
	 * <pre>
	이송작업재료 삭제
	-- com.inisteel.cim.ys.common.dao.YsCommDAO.delCarFtMvMtl
		
	DELETE FROM TB_YS_CARFTMVMTL
	 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	   AND SSTL_NO       = :V_SSTL_NO
	 * </pre>
	 */
	public final static String delCarFtMvMtl = "com.inisteel.cim.ys.common.dao.YsCommDAO.delCarFtMvMtl";
	
	/**
	 * <pre>
	이송작업재료 등록 
	-- com.inisteel.cim.ys.common.dao.YsCommDAO.updCarFtMvMtl
		
	MERGE INTO TB_YS_CARFTMVMTL TM 
	      USING (
				    SELECT STK.SSTL_NO
				          ,STK.HCR_GP
				          ,STK.STL_PROG_CD
				          ,NVL(:V_YS_STK_BED_NO,STK.YS_STK_BED_NO) AS YS_STK_BED_NO
				          ,NVL(:V_YS_STK_LYR_NO,STK.YS_STK_LYR_NO) AS YS_STK_LYR_NO
				          ,NVL(:V_YS_STK_SEQ_NO,STK.YS_STK_SEQ_NO) AS YS_STK_SEQ_NO
				          ,:V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
				          ,:V_MODIFIER AS MODIFIER
				          ,SYSDATE AS MOD_DDTT
				          ,'N' AS DEL_YN
				    FROM   TB_YS_STOCK STK
				    WHERE  STK.SSTL_NO = :V_SSTL_NO
				) DD 
		  ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.SSTL_NO = DD.SSTL_NO )    
	WHEN NOT MATCHED THEN
			 INSERT (
			 		 TM.YD_CAR_SCH_ID, TM.SSTL_NO, TM.REGISTER, TM.REG_DDTT,
	    	         TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YS_STK_BED_NO,
	    	         TM.YS_STK_LYR_NO, TM.YS_STK_SEQ_NO, TM.HCR_GP, TM.STL_PROG_CD
	    	        )
			 VALUES (
			 		 DD.YD_CAR_SCH_ID, DD.SSTL_NO, DD.MODIFIER, DD.MOD_DDTT,
			         DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.YS_STK_BED_NO,
			         DD.YS_STK_LYR_NO, DD.YS_STK_SEQ_NO, DD.HCR_GP, DD.STL_PROG_CD
			        )
	WHEN MATCHED THEN
		 UPDATE SET  TM.MODIFIER      = DD.MODIFIER
				   , TM.MOD_DDTT      = DD.MOD_DDTT
				   , TM.DEL_YN        = DD.DEL_YN
				   , TM.YS_STK_BED_NO = DD.YS_STK_BED_NO
				   , TM.YS_STK_LYR_NO = DD.YS_STK_LYR_NO
				   , TM.YS_STK_SEQ_NO = DD.YS_STK_SEQ_NO
				   , TM.HCR_GP        = DD.HCR_GP
				   , TM.STL_PROG_CD   = DD.STL_PROG_CD
	 * </pre>
	 */
	public final static String updCarFtMvMtl = "com.inisteel.cim.ys.common.dao.YsCommDAO.updCarFtMvMtl";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.dao.updCarSchWrkSt
	-- 이송차량스케줄 차량작업상태 수정
		
	UPDATE TB_YS_CARSCH TS 
	   SET TS.MODIFIER        = :V_MODIFIER
		 , TS.MOD_DDTT        = SYSDATE
		 , TS.YD_EQP_WRK_STAT = DECODE((SELECT COUNT(*) FROM TB_YS_CARFTMVMTL WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID),0,'U','L') --'L' : 영차 ,'U' : 공차
		 , TS.YD_EQP_WRK_SH   = (SELECT COUNT(*) FROM TB_YS_CARFTMVMTL WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID)
	--	 , TS.YD_EQP_WRK_WT   = (SELECT SUM(BILLET_WT) FROM TB_YS_CARFTMVMTL A, TB_PB_BILLETCOMM B WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID AND A.SSTL_NO = B.BLT_NO)
		 , TS.YD_EQP_WRK_WT   = (SELECT SUM(YD_MTL_WT) FROM TB_YS_CARFTMVMTL A, TB_YS_STOCK B WHERE A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID AND A.SSTL_NO = B.SSTL_NO)
		 , TS.YD_PNT_CD3      = NVL(YD_PNT_CD3,'0000')
	 WHERE TS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	 * </pre>
	 */
	public final static String updCarSchWrkSt = "com.inisteel.cim.ys.dao.updCarSchWrkSt";
	
	/**
	 * <pre>
	-- 이송차량스케줄 상하차완료 수정 -- com.inisteel.cim.ys.common.dao.YsCommDAO.updMvCarSchCmpl
	UPDATE TB_YS_CARSCH
	SET    MOD_DDTT = SYSDATE
	      ,MODIFIER = :V_MODIFIER
	      ,YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
	      ,YD_CARLD_CMPL_DT = DECODE(NVL(:V_YD_CARLD_CMPL_DT,'NULL'),'NULL',YD_CARLD_CMPL_DT,SYSDATE)
	      ,YD_CARUD_CMPL_DT = DECODE(NVL(:V_YD_CARUD_CMPL_DT,'NULL'),'NULL',YD_CARUD_CMPL_DT,SYSDATE)
	WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	 * </pre>
	 */
	public final static String updMvCarSchCmpl = "com.inisteel.cim.ys.common.dao.YsCommDAO.updMvCarSchCmpl";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.common.dao.YsCommDAO.getYdPntByStkColGp
	-- 야드적치열구분으로 차량포인트 정보 조회
	
	SELECT YD_CARPNT_CD
	      ,YD_STK_COL_ACT_STAT
	      ,YD_CAR_USETYPE_GP
	      ,YD_GP
	      ,YD_BAY_GP
	      ,YD_STK_COL_GP
	      ,TRN_EQP_CD
	      ,CAR_NO
	      ,CARD_NO
	      ,WLOC_CD
	      ,YD_PNT_CD
	      ,YD_CARPNT_DESC
	      ,YD_SPAN_FROM
	      ,YD_SPAN_TO
	      ,YD_FRM_YN
	  FROM TB_YD_CARPOINT  
	 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
	 * </pre>
	 */
	public final static String getYdPntByStkColGp = "com.inisteel.cim.ys.common.dao.YsCommDAO.getYdPntByStkColGp";
	
	/**
	 * <pre>
	-- 이송차량스케줄 상하차 포인트지시 수정 -- com.inisteel.cim.ys.common.dao.YsCommDAO.updMvCarSchPntWo
	UPDATE TB_YS_CARSCH
	SET    MOD_DDTT = SYSDATE
	      ,MODIFIER = :V_MODIFIER
	      ,YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
	      ,SPOS_WLOC_CD = NVL(:V_SPOS_WLOC_CD,SPOS_WLOC_CD)
	      ,YD_CARLD_PNT_WO_DT = DECODE(NVL(:V_YD_CARLD_PNT_WO_DT,'NULL'),'NULL',YD_CARLD_PNT_WO_DT,SYSDATE)
	      ,YD_PNT_CD1 = NVL(:V_YD_PNT_CD1,YD_PNT_CD1)
	      ,YD_CARLD_STOP_LOC = NVL(:V_YD_CARLD_STOP_LOC,YD_CARLD_STOP_LOC)
	      ,ARR_WLOC_CD = NVL(:V_ARR_WLOC_CD,ARR_WLOC_CD)
	      ,YD_CARUD_PNT_WO_DT = DECODE(NVL(:V_YD_CARUD_PNT_WO_DT,'NULL'),'NULL',YD_CARUD_PNT_WO_DT,SYSDATE)
	      ,YD_PNT_CD3 = NVL(:V_YD_PNT_CD3,YD_PNT_CD3)
	      ,YD_CARUD_STOP_LOC = NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)
	WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	 * </pre>
	 */
	public final static String updMvCarSchPntWo = "com.inisteel.cim.ys.common.dao.YsCommDAO.updMvCarSchPntWo";
	
	/**
	 * <pre>
	-- 적치열 활성상태 변경 -- com.inisteel.cim.ys.common.dao.YsCommDAO.updColActStat
	
	UPDATE TB_YS_STKCOL
	SET    MODIFIER             = :V_MODIFIER
	      ,MOD_DDTT             = SYSDATE
	      ,YD_STK_COL_ACT_STAT  = :V_YD_STK_COL_ACT_STAT
	WHERE  YS_STK_COL_GP        = :V_YS_STK_COL_GP
	 * </pre>
	 */
	public final static String updColActStat = "com.inisteel.cim.ys.common.dao.YsCommDAO.updColActStat";
}
