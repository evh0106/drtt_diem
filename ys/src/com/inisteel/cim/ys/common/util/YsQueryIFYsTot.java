package com.inisteel.cim.ys.common.util;

public interface YsQueryIFYsTot {
	/**
	 * <pre>
	com.inisteel.cim.ys.ystot.dao.getMvStkWrkBookMtlPp
	-- 작업예약등록 이적재료조회
	
	WITH PARAM AS 
	(
	    SELECT :V_SSTL_NOS      AS V_SSTL_NOS
	          ,:V_YS_STK_COL_GP AS V_YS_STK_COL_GP
	      FROM DUAL
	)
	SELECT DISTINCT SL.SSTL_NO
	      ,SL.YS_STK_COL_GP
	      ,SL.YS_STK_BED_NO
	      ,SL.YS_STK_LYR_NO
	      ,SL.YS_STK_SEQ_NO
		  ,ST.ITEMNAME_CD
          ,ST.SPEC_ABBSYM
	      ,ST.YD_MTL_WT
	      ,ST.YD_MTL_T
	      ,ST.YD_MTL_W
	      ,TO_CHAR(YD_MTL_T)||' X '||TO_CHAR(YD_MTL_W,'FM9,999')||' X '||TO_CHAR(YD_MTL_L,'FM99,999') AS MTL_SIZE
	      ,SL.YS_STK_COL_GP||SL.YS_STK_BED_NO||'-'||SL.YS_STK_LYR_NO AS YS_STR_LOC
	      
	  FROM TB_YS_STKLYR SL
	      ,TB_YS_STOCK  ST
	      ,PARAM        PA
	      ,(SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS SSTL_NO
	          FROM (SELECT V_SSTL_NOS AS SSTL_NOS FROM PARAM)
	       CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL) SN  -- 입력 배열
	       
	 WHERE SL.SSTL_NO = SN.SSTL_NO
	   AND SL.SSTL_NO = ST.SSTL_NO
	   AND SL.YS_STK_COL_GP LIKE SUBSTR(PA.V_YS_STK_COL_GP,1,2)||'%'
	   AND SL.YD_STK_LYR_MTL_STAT = 'C'
	 ORDER BY SL.YS_STK_COL_GP, SL.YS_STK_LYR_NO DESC,SL.YS_STK_BED_NO ,SL.YS_STK_SEQ_NO
	 * </pre>
	 */
	public final static String getMvStkWrkBookMtlPp = "com.inisteel.cim.ys.ystot.dao.getMvStkWrkBookMtlPp";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.ystot.dao.getCarldInfoInqjlByYdWrkBook
	-- 차량번호로 작업예약 조회
	
	WITH P AS (
	  SELECT
	    :V_CAR_NO AS V_CAR_NO
	  FROM DUAL
	),
	PAGE AS (
	  SELECT
	    :V_PAGE_NO AS V_PAGE_NO
	  , :V_PAGE_SIZE AS V_PAGE_SIZE
	  FROM DUAL
	)
	SELECT
	  *
	FROM
	  (
	    SELECT
	      ROWNUM AS RNUM
	    , X.*
	    FROM
	      (
	        SELECT
	          COUNT(*) OVER() AS TOTALCOUNT
	        , SUM(E.REAL_MEASURE_BUNDLE_WT) OVER() AS TOTAL_WGT
	        , F.YD_CARPNT_CD
	        , F.YD_STK_COL_GP AS YS_STK_COL_GP
	        , C.YS_STK_LYR_NO
	        , '' AS YD_PREP_SCH_ID_SUB
	        , B.SSTL_NO
	        , E.REAL_MEASURE_BUNDLE_T AS YD_MTL_T
	        , E.REAL_MEASURE_BUNDLE_W AS YD_MTL_W
	        , E.REAL_MEASURE_BUNDLE_LEN AS YD_MTL_L
	        , E.REAL_MEASURE_BUNDLE_WT AS YD_MTL_WT
	        , C.YS_STK_BED_NO AS YS_STK_BED_NO -- 차상위치
	        , NVL(B.SPEC_ABBSYM,E.SPEC_ABBSYM) AS SPEC_ABBSYM
	        , B.HCR_GP
	        , A.YD_CAR_SCH_ID
	        , B.CUST_CD AS CUST_CD
	        , DECODE(E.CURR_PROG_CD,'N',B.DETAIL_ARR_CD,'L',B.DETAIL_ARR_CD,'M',B.DETAIL_ARR_CD,DECODE(G.DETAIL_ARR_CD,NULL,B.DETAIL_ARR_CD,G.DETAIL_ARR_CD)) AS DETAIL_ARR_CD
	        , E.HEAT_NO AS HEAT_NO
	        , V_PAGE_NO
	        , V_PAGE_SIZE
	        FROM
	          TB_YS_CARSCH A
	        , TB_YS_STOCK  B
	        , TB_YS_STKLYR C
	        , TB_YD_CARPOINT F
	        , TB_PB_BUNDLECOMM E
	        , USRPBA.TB_PB_OSCOMM G
	        , P
	        , PAGE
	        WHERE A.CAR_NO = P.V_CAR_NO
	        AND A.TRANS_ORD_DATE = B.TRANS_ORD_DATE
	        AND A.TRANS_ORD_SEQNO = B.TRANS_ORD_SEQNO
	        AND B.SSTL_NO = C.SSTL_NO
	        AND C.YD_STK_LYR_MTL_STAT IN ('C', 'U')
	        AND A.DEL_YN = 'N'
	        AND B.SSTL_NO = E.BNDL_NO(+)
	        AND A.YD_CARLD_STOP_LOC = F.YD_STK_COL_GP(+)
	        AND E.ORD_NO = G.ORD_NO(+)
	        AND E.ORD_DTL = G.ORD_DTL(+)
	      ) X
	    WHERE ROWNUM <= V_PAGE_NO * V_PAGE_SIZE
	  ) XX
	WHERE RNUM >= ((:V_PAGE_NO - 1) * :V_PAGE_SIZE) + 1
	 * </pre>
	 */
	public final static String getCarldInfoInqjlByYdWrkBook = "com.inisteel.cim.ys.ystot.dao.getCarldInfoInqjlByYdWrkBook";
	
	/**
	 * <pre>
	 com.inisteel.cim.ys.ystot.dao.getCarldInfoInqjlByCarFtmvMtl 
	-- 차량 스케줄번호로 차량이송재료 조회
	
	WITH P AS (
	  SELECT
	    :V_YD_CAR_SCH_ID AS V_YD_CAR_SCH_ID
	  FROM DUAL
	),
	PAGE AS (
	  SELECT
	    :V_PAGE_NO AS V_PAGE_NO
	  , :V_PAGE_SIZE AS V_PAGE_SIZE
	  FROM DUAL
	)
	SELECT
	  *
	FROM
	  (
	    SELECT
	      ROWNUM AS RNUM
	    , (
	        SELECT
	          YD_CARPNT_CD
	        FROM
	          USRYDA.TB_YD_CARPOINT
	        WHERE YD_STK_COL_GP = X.YS_STK_COL_GP
	        AND ROWNUM = 1
	      )  AS YD_CARPNT_CD
	    , X.*
	    FROM
	      (
	        SELECT
	          COUNT(*) OVER() AS TOTALCOUNT
	        , SUM(NVL(B.YD_MTL_WT,E.BUNDLE_WT)) OVER() AS TOTAL_WGT
	        , CASE
	          WHEN YD_CAR_PROG_STAT IN ('A','B','C','D','E') THEN
	              YD_CARUD_STOP_LOC
	          ELSE
	            YD_CARLD_STOP_LOC
	          END AS YS_STK_COL_GP
	        , A.YS_STK_LYR_NO
	        , '' AS YD_PREP_SCH_ID_SUB
	        , NVL(B.SSTL_NO,E.BNDL_NO)  AS SSTL_NO
	        , NVL(B.YD_MTL_T,E.BUNDLE_T) AS YD_MTL_T
	        , NVL(B.YD_MTL_W,E.BUNDLE_W) AS YD_MTL_W
	        , NVL(B.YD_MTL_L,E.BUNDLE_LEN) AS YD_MTL_L
	        , NVL(B.YD_MTL_WT,E.BUNDLE_WT) AS YD_MTL_WT
	        , A.YS_STK_BED_NO
	        , NVL(B.SPEC_ABBSYM,E.SPEC_ABBSYM) AS SPEC_ABBSYM
	        , (
	            SELECT
	              YD_WBOOK_ID
	            FROM
	              TB_YS_WRKBOOK
	            WHERE YD_WBOOK_ID = C.YD_CARLD_WRK_BOOK_ID
	            AND DEL_YN = 'N'
	          ) YD_WBOOK_ID
	        , B.HCR_GP
	        , A.YD_CAR_SCH_ID
	        , B.CUST_CD AS CUST_CD
	        , DECODE(E.CURR_PROG_CD, 'N', B.DETAIL_ARR_CD, 'L', B.DETAIL_ARR_CD, 'M', B.DETAIL_ARR_CD, 
	            DECODE(F.DETAIL_ARR_CD, NULL, B.DETAIL_ARR_CD, F.DETAIL_ARR_CD)
	          ) AS DETAIL_ARR_CD
	        , E.HEAT_NO AS HEAT_NO
	        , V_PAGE_NO
	        , V_PAGE_SIZE
	        FROM
	          TB_YS_CARFTMVMTL A
	        , TB_YS_STOCK B
	        , TB_YS_CARSCH C
	        , TB_PB_BUNDLECOMM E
	        , USRPBA.TB_PB_OSCOMM F
	        , P
	        , PAGE
	        WHERE A.SSTL_NO = B.SSTL_NO(+)
	        AND A.SSTL_NO = E.BNDL_NO(+)
	        AND E.ORD_NO = F.ORD_NO(+)
	        AND E.ORD_DTL = F.ORD_DTL(+)
	        AND A.DEL_YN = 'N'
	        AND C.DEL_YN = 'N'
	        AND A.YD_CAR_SCH_ID = C.YD_CAR_SCH_ID
	        AND A.YD_CAR_SCH_ID = P.V_YD_CAR_SCH_ID
	        ORDER BY
	          A.YS_STK_BED_NO
	        , A.YS_STK_LYR_NO DESC
	      ) X
	    WHERE  ROWNUM <= V_PAGE_NO * V_PAGE_SIZE
	  ) XX
	WHERE RNUM >= ((V_PAGE_NO - 1) * V_PAGE_SIZE ) + 1
	 * </pre>
	 */
	public final static String getCarldInfoInqjlByCarFtmvMtl = "com.inisteel.cim.ys.ystot.dao.getCarldInfoInqjlByCarFtmvMtl";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.ystot.dao.updYdCarSchCarWrkDn
	-- 사외통합야드 PDA 차량 스케줄 수정
	
	UPDATE TB_YS_CARSCH SET
	  MODIFIER = :V_MODIFIER
	, MOD_DDTT = SYSDATE
	, YD_EQP_WRK_STAT = NVL(:V_YD_EQP_WRK_STAT,YD_EQP_WRK_STAT)
	, YD_CARUD_ST_DT= NVL(TO_DATE(:V_YD_CARUD_ST_DT, 'YYYYMMDDHH24MISS'), YD_CARUD_ST_DT)
	, YD_CARUD_CMPL_DT= NVL(TO_DATE(:V_YD_CARUD_CMPL_DT, 'YYYYMMDDHH24MISS'),YD_CARUD_CMPL_DT)
	, YD_CAR_PROG_STAT= NVL(:V_YD_CAR_PROG_STAT, YD_CAR_PROG_STAT)
	WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	 * </pre>
	 */
	public final static String updYdCarSchCarWrkDn_ystot = "com.inisteel.cim.ys.ystot.dao.updYdCarSchCarWrkDn";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.ystot.dao.updN7YSL206StlfrToMove
	-- 사외통합야드 이송지시 수정
	
	MERGE INTO
	  TB_PB_STLFRTOMOVE ST
	USING
	  (
	    SELECT
	      A.SSTL_NO AS SSTL_NO
	    , A.TRANSWORD_SEQNO AS TRANSWORD_SEQNO
	    FROM
	      USRPBA.TB_PB_STLFRTOMOVE A
	    , (
	        SELECT
	          CM.SSTL_NO
	        FROM
	          TB_YS_CARSCH      CS
	        , TB_YS_CARFTMVMTL  CM
	        WHERE CS.YD_CAR_SCH_ID = CM.YD_CAR_SCH_ID
	        AND CS.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	      ) B
	    WHERE A.SSTL_NO = B.SSTL_NO
	    AND A.FRTOMOVE_STAT_CD IN ('1','3')
	    AND A.TRANSWORD_SEQNO = (
	        SELECT
	          --+ INDEX_DESC(B PK_PT_STLFRTOMOVE)--
	          MAX(TRANSWORD_SEQNO)
	        FROM
	          TB_PB_STLFRTOMOVE B
	        WHERE A.SSTL_NO = B.SSTL_NO
	        AND FRTOMOVE_STAT_CD NOT IN ('Z','C')
	      )
	  ) DD
	ON (ST.SSTL_NO = DD.SSTL_NO
	    AND ST.TRANSWORD_SEQNO = DD.TRANSWORD_SEQNO
	  )
	WHEN MATCHED THEN UPDATE SET
	    FRTOMOVE_DONE_DATE = SYSDATE-0.00003
	  , FTMV_HDS_DD = TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD')
	  , FRTOMOVE_STAT_CD = '*'
	  , MODIFIER = :V_MODIFIER
	  , MOD_DDTT = SYSDATE

	 * </pre>
	 */
	public final static String updN7YSL206StlfrToMove_ystot = "com.inisteel.cim.ys.ystot.dao.updN7YSL206StlfrToMove";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.ystot.dao.getEmptyCol
	-- 소재가 없는 열 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YS_STK_COL_GP AS V_YS_STK_COL_GP
	      FROM DUAL
	)
	SELECT YS_STK_COL_GP
	     , SUM_CNT
	  FROM (
				SELECT S.YS_STK_COL_GP
				     , COUNT(S.SSTL_NO) AS SUM_CNT
				  FROM TB_YS_STKLYR S
				     , PARAM        P
				 WHERE S.YS_STK_COL_GP LIKE P.V_YS_STK_COL_GP || '%'
				   AND S.DEL_YN = 'N'
				 GROUP BY S.YS_STK_COL_GP
				HAVING COUNT(S.SSTL_NO) < 1
		   )
	 WHERE ROWNUM = 1
	 * </pre>
	 */
	public final static String getEmptyCol = "com.inisteel.cim.ys.ystot.dao.getEmptyCol";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.ystot.dao.insWrkBook
	-- 작업예약 등록
	
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
	)
	 * </pre>
	 */
	public final static String insWrkBook_ystot = "com.inisteel.cim.ys.ystot.dao.insWrkBook";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.ystot.dao.insWrkBookMtl
	-- 작업예약재료 등록
	
	INSERT INTO TB_YS_WRKBOOKMTL (
	    YD_WBOOK_ID      --야드작업예약ID
	    ,SSTL_NO         --재료번호
	    ,YS_STK_COL_GP   --야드적치열구분
	    ,YS_STK_BED_NO   --야드적치BED번호
	    ,YS_STK_LYR_NO   --야드적치단번호
	    ,YS_STK_SEQ_NO   --야드적치SEQ번호
	    ,YD_UP_COLL_SEQ  
	    ,REGISTER        --등록자
	    ,REG_DDTT        --등록일시
	    ,MODIFIER        --수정자
	    ,MOD_DDTT        --수정일시
	    ,DEL_YN          --삭제유무
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
	public final static String insWrkBookMtl_ystot = "com.inisteel.cim.ys.ystot.dao.insWrkBookMtl";
	
	/**
	 * <pre>
	com.inisteel.cim.ys.ystot.dao.getStatEqp
	-- 설비상태조회 
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_EQP_ID AS V_YD_EQP_ID
	      FROM DUAL
	)
	SELECT EQ.YD_EQP_STAT
	      ,DECODE(EQ.YD_EQP_WRK_MODE,'1','1','0')  AS YD_EQP_WRK_MODE
	      ,(SELECT YD_CRN_SCH_ID FROM TB_YS_CRNSCH WHERE DEL_YN = 'N' AND YD_EQP_ID = EQ.YD_EQP_ID AND YD_WRK_PROG_STAT != 'W') AS YD_CRN_SCH_ID
	  FROM TB_YS_EQP EQ
	      ,PARAM     PA
	 WHERE YD_EQP_ID = PA.V_YD_EQP_ID
	   AND DEL_YN    = 'N'
	 * </pre>
	 */
	public final static String getStatEqp = "com.inisteel.cim.ys.ystot.dao.getStatEqp";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.ystot.dao.updYdStkcol
	-- 열정보 수정
	
	UPDATE TB_YS_STKCOL SET
	  YD_GP = :V_YD_GP
	, YD_BAY_GP = :V_YD_BAY_GP
	, YD_EQP_GP = :V_YD_EQP_GP
	, YD_STK_COL_NO = :V_YD_STK_COL_NO
	, YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
	, YD_STK_COL_RULE_XAXIS = :V_YD_STK_COL_RULE_XAXIS
	, YD_STK_COL_RULE_YAXIS = :V_YD_STK_COL_RULE_YAXIS
	, YD_STK_COL_W = :V_YD_STK_COL_W
	, YD_STK_COL_L = :V_YD_STK_COL_L
	, YS_STK_COL_L_GP = :V_YS_STK_COL_L_GP
	, YD_STK_COL_DIR_GP = :V_YD_STK_COL_DIR_GP
	, YD_STKBED_USG_CD = :V_YD_STKBED_USG_CD
	, MOD_DDTT = SYSDATE
	, MODIFIER = :V_MODIFIER
	WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	 * </pre>
	 */
	public final static String updYdStkcol = "com.inisteel.cim.ys.ystot.dao.updYdStkcol";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.ystot.dao.selCarMvInfoByStlCarNo --
	-- 재료와 차량번호로 차량이송 정보 조회 PDA
	
	WITH P AS (
	  SELECT
	    :V_SSTL_NO AS V_SSTL_NO
	  , :V_CAR_NO  AS V_CAR_NO
	  FROM DUAL
	),
	MTL AS (
	  SELECT *
	  FROM TB_YS_PREPMTL
	    , P
	  WHERE 1=1
	  AND SSTL_NO = P.V_SSTL_NO
	  AND DEL_YN = 'N'
	),
	STL AS (
	  SELECT A.* FROM TB_PB_STLFRTOMOVE A
	    INNER JOIN MTL
	    ON 1=1
	    AND A.SSTL_NO = MTL.SSTL_NO 
	  WHERE 1=1
	  
	  AND TRANSWORD_SEQNO = (
	    --해당 재료의 마지막 이송지시차수
	    SELECT  /-+ INDEX_DESC(B PK_PB_STLFRTOMOVE) -/  
	    MAX(TRANSWORD_SEQNO)  
	    FROM TB_PB_STLFRTOMOVE B  
	    WHERE B.SSTL_NO = A.SSTL_NO  
	    AND ROWNUM<=1 
	  )
	),
	CAR AS (
	  SELECT *
	  FROM TB_YS_CARSCH
	    , P
	  WHERE 1=1
	  AND DEL_YN = 'N'
	  AND
	    (
	      CAR_NO = P.V_CAR_NO OR TRN_EQP_CD = P.V_CAR_NO
	    )
	  AND ROWNUM = 1
	)
	SELECT
	  STL.SSTL_NO
	, STL.TRANSWORD_SEQNO
	, STL.STL_APPEAR_GP
	, STL.ORD_DTL
	, STL.ORD_NO
	, STL.HCR_GP
	, STL.WO_CAR_PLNT_PROC_CD
	, STL.ORD_BEFO_PROG_CD
	, STL.STL_WT
	, STL.FRTOMOVE_WREQ_DATE
	, STL.FRTOMOVE_WREQ_CAUSE_CD
	, STL.FRTOMOVE_WORD_DATE
	, STL.FRTOMOVE_WORD_SEQNO
	, STL.FRTOMOVE_CARLOAD_DATE
	, STL.FRTOMOVE_WREQ_SEQNO
	, STL.FRTOMOVE_DONE_DATE
	, STL.FRTOMOVE_ORD_CANCEL_DATE
	, STL.YD_MTL_PLN_STR_FR_LOC_CD
	, STL.YD_MTL_PLN_STR_TO_LOC_CD
	, STL.PROC_FROM
	, STL.PROC_TO
	, STL.SPST_WO_RT_CD
	, STL.URGENT_FRTOMOVE_WORD_GP
	, STL.MS_ISSUE_REQUEST_GP
	, STL.RENTPROC_COMCD
	, STL.YD_GP
	, STL.SPOS_WLOC_CD
	, STL.ARR_WLOC_CD
	, STL.FRTOMOVE_STAT_CD
	, STL.TRS_INDI_DT
	, STL.PRD_ITM_CD
	, STL.FTMV_HDS_DD
	, STL.FRMV_WORD_GP
	, STL.TO_CURR_PROG_CD
	, STL.FRTOMOVE_WORD_DATE1
	, STL.TRANSWORD_SEQNO1
	, STL.FRTOMOVE_STAT_CD1
	, STL.ORD_YEOJAE_GP
	, STL.REWO_LMT_YN
	, STL.REWO_LMT_RSN
	, STL.REWO_LMT_DT
	, STL.REWO_LMT_REGER
	, STL.REWO_LMT_REL_YN
	, STL.REWO_LMT_REL_RSN
	, STL.REWO_LMT_REL_DT
	, STL.REWO_LMT_REL_REGER
	, STL.MCSCARFING_DONE_YN
	, STL.WO_SPOS_WLOC_CD
	, STL.VIA_GP
	, STL.DESCRIPTION
	, STL.YD_CAR_PROG_STAT
	, STL.FTMV_WH_CD1
	, STL.FTMV_WH_CD2
	, STL.NEW_PRD_ITM_CD
	, STL.OLD_PRD_ITM_CD
	, STL.REWO_LMT_RSN_CD
	, STL.REGISTER
	, STL.REG_DDTT
	, STL.MODIFIER
	, STL.MOD_DDTT
	, STL.FTMV_REQ_NO
	, STL.TRANSMIT_YN
	, STL.REG_PGM
	, CAR.YD_CAR_SCH_ID
	, CAR.YD_EQP_ID
	, CAR.YD_CAR_USE_GP
	, CAR.CAR_NO
	, CAR.TRN_EQP_CD
	, CAR.CAR_KIND
	, CAR.TRANS_EQUIPMENT_TYPE
	, CAR.YD_EQP_WRK_STAT
	, CAR.YD_WRK_PROG_STAT
	, CAR.YD_EQP_WRK_SH
	, CAR.YD_EQP_WRK_WT
	, CAR.YS_STK_BED_TP
	, CAR.YD_CARLD_LEV_LOC
	, CAR.YD_CARLD_LEV_DT
	, CAR.YD_CARLD_PNT_WO_DT
	, CAR.YD_PNT_CD1
	, CAR.YD_PNT_CD2
	, CAR.YD_CARLD_WRK_BOOK_ID
	, CAR.YD_CARLD_SCH_REQ_GP
	, CAR.YD_CARLD_STOP_LOC
	, CAR.YD_CARLD_ARR_DT
	, CAR.YD_CARLD_ST_DT
	, CAR.YD_CARLD_CMPL_DT
	, CAR.YD_CARLD_WRK_ACT_GP
	, CAR.YD_CARLD_CHK_DT
	, CAR.YD_CARUD_LEV_DT
	, CAR.YD_CARUD_PNT_WO_DT
	, CAR.YD_PNT_CD3
	, CAR.YD_PNT_CD4
	, CAR.YD_CARUD_WRK_BOOK_ID
	, CAR.YD_CARUD_STOP_LOC
	, CAR.YD_CARUD_SCH_REQ_GP
	, CAR.YD_CARUD_ARR_DT
	, CAR.YD_CARUD_CHK_DT
	, CAR.YD_CARUD_ST_DT
	, CAR.YD_CARUD_CMPL_DT
	, CAR.YD_CARUD_WRK_ACT_GP
	, CAR.YD_TRN_WRK_DELY_CD
	, CAR.CARD_NO
	, CAR.FRTOMOVE_PLANT_GP
	, CAR.RENTPROC_CD
	, CAR.YD_FRTOMOVE_YD_GP
	, CAR.YD_FRTOMOVE_BAY_GP
	, CAR.DEST_TEL_NO
	, CAR.YD_DLVRDD_RULE_DD
	, CAR.SHIPASSIGN_WORD_DATE
	, CAR.SHIPASSIGN_WORD_SEQNO
	, CAR.SHIP_CD
	, CAR.SHIP_NAME
	, CAR.RSHP_HOLD_NO
	, CAR.BERTH_NO
	, CAR.SAILNO
	, CAR.YD_CAR_WRK_GP
	, CAR.TRANS_ORD_DATE
	, CAR.TRANS_ORD_SEQNO
	, CAR.YD_BAYIN_WO_SEQ
	, CAR.YD_CAR_RCPT_CHK_YN
	, CAR.YD_CAR_ISSUE_CHK_YN
	, CAR.YD_CAR_RCPT_CHECKER
	, CAR.YD_CAR_ISSUE_CHECKER
	, CAR.IF_SEQ_NO
	, CAR.CMBN_CARLD_YN
	, CAR.WAIT_ARR_DDTT
	, CAR.WAIT_ARR_GP
	FROM STL
	  INNER JOIN CAR
	  ON 1=1
	  AND CAR.SPOS_WLOC_CD = STL.SPOS_WLOC_CD
	 * </pre>
	 */
	public final static String selCarMvInfoByStlCarNo = "com.inisteel.cim.ys.ystot.dao.selCarMvInfoByStlCarNo";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.ystot.dao.getTopEmptyBedGrp
	-- 최 상단 빈 배드 그룹 위치 찾기 (사외통합야드용)
	
	WITH P AS (
	  SELECT
	    :V_YS_STK_COL_GP AS V_YS_STK_COL_GP
	  FROM DUAL
	)
	
	SELECT
	  BED_GRP
	, MIN(YS_STK_LYR_NO) AS MIN_LYR
	FROM
	  (
	    SELECT
	      CASE
	      WHEN YS_STK_BED_NO IN ('01','02') THEN
	        '01' 
	      WHEN YS_STK_BED_NO IN ('03','04') THEN
	        '03'
	      ELSE
	        '01'
	      END AS BED_GRP
	    , YS_STK_LYR_NO
	    , SUM(SEQ_CNT) AS SUM_BED_SEQ_CNT
	    FROM (
	      SELECT
	        YS_STK_BED_NO
	      , YS_STK_LYR_NO
	      , COUNT(YS_STK_SEQ_NO) AS SEQ_CNT
	      FROM TB_YS_STKLYR
	        , P
	      WHERE 1=1
	      AND YS_STK_COL_GP = P.V_YS_STK_COL_GP
	      AND SSTL_NO IS NULL
	      AND YD_STK_LYR_MTL_STAT NOT IN ('C')
	      GROUP BY
	        YS_STK_BED_NO
	      , YS_STK_LYR_NO
	      ORDER BY 
	        YS_STK_BED_NO
	      , YS_STK_LYR_NO
	    )
	    GROUP BY
	      CASE
	      WHEN YS_STK_BED_NO IN ('01','02') THEN
	        '01' 
	      WHEN YS_STK_BED_NO IN ('03','04') THEN
	        '03'
	      ELSE
	        '01'
	      END
	      , YS_STK_LYR_NO
	    ORDER BY 
	      YS_STK_LYR_NO, BED_GRP
	  ) SEQ
	  INNER JOIN (
	    SELECT
	      COUNT(YS_STK_SEQ_NO) AS YS_STK_SEQ_CNT
	    FROM TB_YS_STKLYR
	      , P
	    WHERE 1=1
	    AND YS_STK_COL_GP = P.V_YS_STK_COL_GP
	    AND YS_STK_BED_NO IN ('01', '02')
	    AND YS_STK_LYR_NO = '01'
	  ) MAX_SEQ
	  ON 1=1
	  AND MAX_SEQ.YS_STK_SEQ_CNT = SEQ.SUM_BED_SEQ_CNT
	WHERE ROWNUM = 1
	GROUP BY BED_GRP
	ORDER BY MIN_LYR ASC, BED_GRP ASC
	 * </pre>
	 */
	public final static String getTopEmptyBedGrp = "com.inisteel.cim.ys.ystot.dao.getTopEmptyBedGrp";
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.ystot.dao.getWrkPerCount
	-- 재료 번호로 재료정보,작업매수와 상차완료 기준을 가져온다. (사외통합야드 PDA용)
	
	WITH P AS (
	  SELECT
	    :V_SSTL_NO AS V_SSTL_NO
	  FROM
	    DUAL
	)
	, COMM AS (
	  SELECT * FROM (
	    SELECT
	      BLT_NO AS SSTL_NO
	    , BILLET_T AS S_T
	    , BILLET_LEN AS S_L
	    , 'QT' AS STLKND
	    FROM
	      TB_PB_BILLETCOMM
	      LEFT JOIN P ON 1=1
	    WHERE 1 = 1
	    AND BLT_NO = P.V_SSTL_NO
	       
	    UNION ALL
	       
	    SELECT
	      BNDL_NO AS SSTL_NO
	    , BUNDLE_T  AS S_T
	    , BUNDLE_LEN  AS S_L
	    , 'RR' AS STLKND
	    FROM
	      TB_PB_BUNDLECOMM
	      LEFT JOIN P ON 1=1
	    WHERE 1 = 1
	    AND BNDL_NO = P.V_SSTL_NO
	    
	--    SELECT
	--      'TEST' AS SSTL_NO
	--    , :V_S_T AS S_T
	--    , :V_S_L AS S_L
	--    , 'RR' AS STLKND
	--    FROM DUAL
	  )
	  WHERE ROWNUM = 1
	)
	, LD_C AS (
	  SELECT
	    0 AS SEQ, 'QT' AS STLKND, 0 AS T, 0 AS L, 6 AS PCNT, 12 AS MCNT
	  FROM DUAL
	  
	  UNION ALL
	
	  SELECT
	    1 AS SEQ, 'QT' AS STLKND, 131 AS T, 10 * 1000 AS L, 8 AS PCNT, 16 AS MCNT   --각강 131, 10 미만
	  FROM DUAL
	  
	  UNION ALL
	  
	  SELECT
	    2 AS SEQ, 'QT' AS STLKND, 130 AS T, 11 * 1000 AS L, 6 AS PCNT, 12 AS MCNT   --각강 130, 11
	  FROM DUAL  
	  
	  UNION ALL
	  
	  SELECT
	    3 AS SEQ, 'QT' AS STLKND, 130 AS T, 10 * 1000 AS L, 8 AS PCNT, 16 AS MCNT   --각강 130 이하, 10 이하(11M 미만)
	  FROM DUAL
	  
	  UNION ALL
	  
	  SELECT
	    4 AS SEQ, 'QT' AS STLKND, 129 AS T, 0 AS L, 8 AS PCNT, 16 AS MCNT   --각강 129 이하
	  FROM DUAL
	  
	  UNION ALL
	  
	  SELECT
	    10 AS SEQ, 'RR' AS STLKND, 0 AS T, 0 AS L, 3 AS PCNT, 8 AS MCNT    -- 봉강 기본
	  FROM DUAL
	  
	  UNION ALL
	
	  SELECT
	    11 AS SEQ, 'RR' AS STLKND, 87 AS T, 260 AS L, 3 AS PCNT, 8 AS MCNT   -- 봉강 R87이상, R260이하
	  FROM DUAL
	  
	  UNION ALL
	  
	  SELECT
	    12 AS SEQ, 'RR' AS STLKND, 270 AS T, 0 AS L, 4 AS PCNT, 16 AS MCNT   -- 봉강 R270
	  FROM DUAL  
	  
	  UNION ALL
	  
	  SELECT
	    13 AS SEQ, 'RR' AS STLKND, 271 AS T, 330 AS L, 3 AS PCNT, 12 AS MCNT   -- 봉강 R271이하, R330이하
	  FROM DUAL
	  
	  UNION ALL
	  
	  SELECT
	    14 AS SEQ, 'RR' AS STLKND, 331 AS T, 0 AS L, 3 AS PCNT, 8 AS MCNT   -- 봉강 R331 이상
	  FROM DUAL
	  
	)
	SELECT
	  SSTL_NO
	, S_T     -- 사이즈 T
	, S_L     -- 사이즈 L
	, STLKND  -- 재료 구분 (각강빌렛, 봉강)
	, CASE
	  WHEN STLKND = 'QT' THEN   --각강
	    CASE
	    WHEN S_T = (SELECT T FROM LD_C WHERE STLKND = 'QT' AND SEQ = 1) AND S_L < (SELECT L FROM LD_C WHERE STLKND = 'QT' AND SEQ = 1) THEN   -- 131각, 10M 미만
	      (SELECT PCNT FROM LD_C WHERE SEQ = 1) -- 8매
	    WHEN S_T = (SELECT T FROM LD_C WHERE STLKND = 'QT' AND SEQ = 2) AND S_L = (SELECT L FROM LD_C WHERE STLKND = 'QT' AND SEQ = 2) THEN   -- 130각, 11M
	      (SELECT PCNT FROM LD_C WHERE SEQ = 2) -- 6매
	    WHEN S_T <= (SELECT T FROM LD_C WHERE STLKND = 'QT' AND SEQ = 3) AND S_L <= (SELECT L FROM LD_C WHERE STLKND = 'QT' AND SEQ = 3) THEN   -- 130각 이하, 11M 미만
	      (SELECT PCNT FROM LD_C WHERE SEQ = 3) -- 8매
	    WHEN S_T <= (SELECT T FROM LD_C WHERE STLKND = 'QT' AND SEQ = 4) THEN -- 각강 129 이하
	      (SELECT PCNT FROM LD_C WHERE STLKND = 'QT' AND SEQ = 4) -- 8매
	    ELSE
	      (SELECT PCNT FROM LD_C WHERE STLKND = 'QT' AND SEQ = 0) -- 기본값 6매
	    END
	    
	  ELSE -- 봉강
	    CASE
	    WHEN S_T >= (SELECT T FROM LD_C WHERE STLKND = 'RR' AND SEQ = 11) AND S_T <= (SELECT L FROM LD_C WHERE STLKND = 'RR' AND SEQ = 11) THEN   -- 봉강 R87이상, R260이하
	      (SELECT PCNT FROM LD_C WHERE SEQ = 11) -- 3매
	    WHEN S_T = (SELECT T FROM LD_C WHERE STLKND = 'RR' AND SEQ = 12) THEN   -- 봉강 R270
	      (SELECT PCNT FROM LD_C WHERE SEQ = 12) -- 4매
	    WHEN S_T >= (SELECT T FROM LD_C WHERE STLKND = 'RR' AND SEQ = 13) AND S_T <= (SELECT L FROM LD_C WHERE STLKND = 'RR' AND SEQ = 13) THEN   -- 봉강 R271이상, R330이하
	      (SELECT PCNT FROM LD_C WHERE SEQ = 13) -- 3매
	    WHEN S_T >= (SELECT T FROM LD_C WHERE STLKND = 'RR' AND SEQ = 14) THEN   -- 봉강 R331 이상
	      (SELECT PCNT FROM LD_C WHERE SEQ = 14) -- 3매
	    ELSE
	      (SELECT PCNT FROM LD_C WHERE STLKND = 'RR' AND SEQ = 10) -- 기본값 3매
	    END  
	  END AS R_PCNT -- 작업매수
	  
	, CASE
	  WHEN STLKND = 'QT' THEN
	    CASE
	    WHEN S_T = (SELECT T FROM LD_C WHERE SEQ = 1) AND S_L < (SELECT L FROM LD_C WHERE SEQ = 1) THEN   -- 131각, 10M 미만
	      (SELECT MCNT FROM LD_C WHERE SEQ = 1) 
	    WHEN S_T = (SELECT T FROM LD_C WHERE SEQ = 2) AND S_L = (SELECT L FROM LD_C WHERE SEQ = 2) THEN   -- 130각, 11M
	      (SELECT MCNT FROM LD_C WHERE SEQ = 2) 
	    WHEN S_T <= (SELECT T FROM LD_C WHERE SEQ = 3) AND S_L <= (SELECT L FROM LD_C WHERE SEQ = 3) THEN   -- 130각 이하, 11M 미만
	      (SELECT MCNT FROM LD_C WHERE SEQ = 3) 
	    WHEN S_T <= (SELECT T FROM LD_C WHERE SEQ = 4) THEN -- 각강 129 이하
	      (SELECT MCNT FROM LD_C WHERE SEQ = 4)
	    ELSE
	      (SELECT MCNT FROM LD_C WHERE SEQ = 0)
	    END 
	  ELSE
	    CASE
	    WHEN S_T >= (SELECT T FROM LD_C WHERE STLKND = 'RR' AND SEQ = 11) AND S_T <= (SELECT L FROM LD_C WHERE STLKND = 'RR' AND SEQ = 11) THEN   -- 봉강 R87이상, R260이하
	      (SELECT MCNT FROM LD_C WHERE SEQ = 11)
	    WHEN S_T = (SELECT T FROM LD_C WHERE STLKND = 'RR' AND SEQ = 12) THEN   -- 봉강 R270
	      (SELECT MCNT FROM LD_C WHERE SEQ = 12)
	    WHEN S_T >= (SELECT T FROM LD_C WHERE STLKND = 'RR' AND SEQ = 13) AND S_T <= (SELECT L FROM LD_C WHERE STLKND = 'RR' AND SEQ = 13) THEN   -- 봉강 R271이상, R330이하
	      (SELECT MCNT FROM LD_C WHERE SEQ = 13)
	    WHEN S_T >= (SELECT T FROM LD_C WHERE STLKND = 'RR' AND SEQ = 14) THEN   -- 봉강 R331 이상
	      (SELECT MCNT FROM LD_C WHERE SEQ = 14)
	    ELSE
	      (SELECT MCNT FROM LD_C WHERE STLKND = 'RR' AND SEQ = 10) -- 기본값 3매
	    END  
	  END AS R_MCNT -- 상차완료 기준 매수
	FROM COMM
	 * </pre>
	 */
	public final static String getWrkPerCount = "com.inisteel.cim.ys.ystot.dao.getWrkPerCount";
	
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.ystot.dao.udtCarschArrWloc
	-- 차량스케줄 도착 개소 코드 수정 (사외통합야드 PDA)
	
	UPDATE TB_YS_CARSCH
	   SET MODIFIER = :V_MODIFIER
	      ,MOD_DDTT = SYSDATE
	      ,ARR_WLOC_CD = :V_ARR_WLOC_CD
	 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
	   AND DEL_YN = 'N'
	 * </pre>
	 */
	public final static String udtCarschArrWloc = "com.inisteel.cim.ys.ystot.dao.udtCarschArrWloc";
}
