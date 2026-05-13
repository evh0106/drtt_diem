package com.inisteel.cim.ys.common.util;

public interface YsQueryIFSbr {

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getSwitchOffYn
	-- 추출대(GDPC11/GDPC21) 모든 스위치 OFF 여부 체크
	
	SELECT CASE WHEN EXISTS ( SELECT * FROM TB_YS_RULE WHERE REPR_CD_GP = 'GD0001' AND ITEM = 'Y' ) THEN 'N'  --[N:스위치 ON 존재  ]
			    ELSE 'Y'                                                                                      --[Y:스위치 ON 미존재]
		   END AS OFF_YN
	  FROM DUAL
	 * </pre>
	 */
	public final static String getSwitchOffYn = "com.inisteel.cim.ys.sbr.dao.getSwitchOffYn";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getPCFixedEqpId
	-- 추출대(GDPC11/GDPC21) 입고용 지정 크레인 조회 
	
	WITH PARAM AS 
	(
	    SELECT :V_YS_STK_COL_GP AS V_YS_STK_COL_GP
	      FROM DUAL
	)
	SELECT YD_EQP_ID
	     , YD_EQP_STAT
	     , YD_EQP_WRK_MODE
	  FROM TB_YS_EQP
	 WHERE YD_GP     = 'G'
	   AND YD_BAY_GP = 'D'
	   AND DEL_YN    = 'N'
	   AND YD_EQP_ID = (
						 SELECT R.ITEM 
						  FROM TB_YS_RULE R
						     , PARAM      P
						 WHERE R.REPR_CD_GP = 'GD0003'
						   AND SUBSTR(P.V_YS_STK_COL_GP,3,2) = 'PC'
					   )
	 * </pre>
	 */
	public final static String getPCFixedEqpId = "com.inisteel.cim.ys.sbr.dao.getPCFixedEqpId";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getMvStkWrkBookMtlPp
	-- 작업예약등록 이적재료조회
	
	WITH PARAM AS 
	(
	    SELECT :V_SSTL_NOS      AS V_SSTL_NOS
	          ,:V_YS_STK_COL_GP AS V_YS_STK_COL_GP
		      ,:V_ASC_DESC      AS V_ASC_DESC
	      FROM DUAL
	)
	SELECT DISTINCT SL.SSTL_NO
	      ,SL.YS_STK_COL_GP
	      ,SL.YS_STK_BED_NO
	      ,SL.YS_STK_LYR_NO
	      ,SL.YS_STK_SEQ_NO
		  ,ST.STL_APPEAR_GP
		  ,ST.ITEMNAME_CD
          ,ST.SPEC_ABBSYM
	      ,ST.YD_MTL_WT
	      ,ST.YD_MTL_T
	      ,ST.YD_MTL_W
	      ,ST.YD_MTL_L
	      ,TO_CHAR(YD_MTL_T)||' X '||TO_CHAR(YD_MTL_W,'FM9,999')||' X '||TO_CHAR(YD_MTL_L,'FM99,999') AS MTL_SIZE
	      ,SL.YS_STK_COL_GP||SL.YS_STK_BED_NO||'-'||SL.YS_STK_LYR_NO AS YS_STR_LOC
	      ,ST.HEAT_NO
	      
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
	 ORDER BY SL.YS_STK_COL_GP
	        , SL.YS_STK_LYR_NO DESC
		    , SL.YS_STK_BED_NO * CASE WHEN (SELECT V_ASC_DESC FROM PARAM) = 'ASC'  THEN  1
									  WHEN (SELECT V_ASC_DESC FROM PARAM) = 'DESC' THEN -1 END
	        , SL.YS_STK_SEQ_NO
	 * </pre>
	 */
	public final static String getMvStkWrkBookMtlPp = "com.inisteel.cim.ys.sbr.dao.getMvStkWrkBookMtlPp";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getStatSchCd
	-- 스케줄코드의 설비상태 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_SCH_CD AS V_YD_SCH_CD
	      FROM DUAL
	)
	SELECT SR.YD_SCH_CD
	     , SR.YD_WRK_CRN       AS YD_EQP_ID
	     , SR.YD_WRK_CRN_PRIOR AS YD_SCH_PRIOR
	     , SR.YD_SCH_PROH_EXN
	  FROM (
	            -- "주/부크레인 설비고장여부", "우선순위"를 고려한 스케줄코드의 크레인 및 스케줄금지여부 조회
	            SELECT A.YD_GP
	                  ,A.YD_BAY_GP
	                  ,A.YD_SCH_CD
	                  ,CASE WHEN B.STAT1  = 'O' AND B.STAT2  = 'O' AND A.YD_CRN_PRIOR1 <= A.YD_CRN_PRIOR2 THEN A.YD_CRN1  --['O':작업가능],['C':작업불가(작업중)],['X':NULL]
	                        WHEN B.STAT1  = 'O' AND B.STAT2  = 'O' AND A.YD_CRN_PRIOR1 >  A.YD_CRN_PRIOR2 THEN A.YD_CRN2  --우선순위: 낮은 숫자가 빠름
	                        WHEN B.STAT1  = 'O' AND B.STAT2  = 'C' THEN YD_CRN1
	                        WHEN B.STAT1  = 'C' AND B.STAT2  = 'O' THEN YD_CRN2
	                        WHEN B.STAT1  = 'C' AND B.STAT2  = 'C' THEN YD_CRN1
	                        WHEN B.STAT1 <> 'B' AND B.STAT2  = 'B' THEN YD_CRN1
	                        WHEN B.STAT1  = 'B' AND B.STAT2 <> 'B' THEN YD_CRN2
	                        ELSE ''
	                   END AS YD_WRK_CRN
	                  ,CASE WHEN B.STAT1  = 'O' AND B.STAT2  = 'O' AND A.YD_CRN_PRIOR1 <= A.YD_CRN_PRIOR2 THEN A.YD_CRN_PRIOR1
	                        WHEN B.STAT1  = 'O' AND B.STAT2  = 'O' AND A.YD_CRN_PRIOR1 >  A.YD_CRN_PRIOR2 THEN A.YD_CRN_PRIOR2
	                        WHEN B.STAT1  = 'O' AND B.STAT2  = 'C' THEN YD_CRN_PRIOR1
	                        WHEN B.STAT1  = 'C' AND B.STAT2  = 'O' THEN YD_CRN_PRIOR2
	                        WHEN B.STAT1  = 'C' AND B.STAT2  = 'C' THEN YD_CRN_PRIOR1
	                        WHEN B.STAT1 <> 'B' AND B.STAT2  = 'B' THEN YD_CRN_PRIOR1
	                        WHEN B.STAT1  = 'B' AND B.STAT2 <> 'B' THEN YD_CRN_PRIOR2
	                        ELSE 0
	                   END AS YD_WRK_CRN_PRIOR
	                  ,YD_SCH_CD_NM
	                  ,YD_SCH_CONTENTS
	                  ,YD_SCH_PROH_EXN
	              FROM (
	                        --특수강 스케줄코드, 주/부크레인, 우선순위 조회
	                        SELECT YD_DATA_GP
	                              ,YD_SCH_GP 
	                              ,YD_GP
	                              ,YD_BAY_GP
	                              ,YD_SCH_CD
	                              ,YD_SCH_CD_NM
	                              ,YD_SCH_CONTENTS
	                              ,YD_CRN1
	                              ,YD_CRN_STAT1
	                              ,CASE WHEN YD_CRN_PRIOR1 <= 0 THEN 99 ELSE YD_CRN_PRIOR1 END AS YD_CRN_PRIOR1
	                              ,YD_CRN2
	                              ,YD_CRN_STAT2
	                              ,CASE WHEN YD_CRN_PRIOR2 <= 0 THEN 99 ELSE YD_CRN_PRIOR2 END AS YD_CRN_PRIOR2
	                              ,YD_SCH_PROH_EXN
	                          FROM TB_YS_SCHRULE                        -- YS_특수강스케줄기준
	                         WHERE YD_GP      = 'G'                     -- 특수강-야드: 'G'
	                           AND YD_BAY_GP  = 'D'                     -- 특수강-동  : 'D'
	                           AND YD_DATA_GP = 'M'                     -- DATA구분: R(Register), M(Modify)
	                   ) A
	                  ,(
	                        -- 주/부크레인의 설비상태값 조회
	                        SELECT YD_GP
	                              ,YD_BAY_GP
	                              ,YD_SCH_GP
	                              ,MAX(YD_CRN1) AS CRN1  -- 주크레인
	                              ,MAX(YD_CRN2) AS CRN2  -- 부크레인
	                              ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(YD_EQP_ID, YD_CRN1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1  -- 주크레인 상태
	                              ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(YD_EQP_ID, YD_CRN2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2  -- 부크레인 상태
	                          FROM (
	                                    -- 크레인 시설정보에 있는 "스케줄코드-주/부크레인"의 설비상태 조회
	                                    SELECT E.YD_GP
	                                          ,E.YD_BAY_GP
	                                          ,E.YD_EQP_ID
		                                      ,DECODE(E.YD_EQP_STAT,'W','O','B','B','C')                 AS YD_EQP_STAT  -- 설비: 크레인상태 ['W':대기],['O':작업가능],['B':고장],['C':작업불가(작업중)]
	                                          ,DECODE(E.YD_EQP_GP,'CR','CR','S'||SUBSTR(E.YD_EQP_ID,-1)) AS YD_SCH_GP    -- 'CR', 'S1', 'S2'...
	                                          ,YD_CRN1  -- 주크레인
	                                          ,YD_CRN2  -- 부크레인
	                                      FROM TB_YS_EQP E  -- YS_설비
	                                          ,(SELECT YD_CRN1, YD_CRN2 FROM TB_YS_SCHRULE, PARAM P WHERE YD_SCH_CD = P.V_YD_SCH_CD AND YD_DATA_GP = 'M') S
	                                     WHERE (E.YD_EQP_ID = S.YD_CRN1 OR E.YD_EQP_ID = S.YD_CRN2)
	                                       AND E.YD_GP     = 'G'
	                                       AND E.YD_BAY_GP = 'D'
	                                       AND E.YD_EQP_GP IN ('CR','SC')
	                               )
	                         GROUP BY YD_GP, YD_BAY_GP, YD_SCH_GP
	                   ) B
	                  ,PARAM P
	             WHERE 1=1
	               AND A.YD_GP     = B.YD_GP
	               AND A.YD_BAY_GP = B.YD_BAY_GP
	               AND A.YD_SCH_GP = B.YD_SCH_GP  -- 스케쥴구분: CR(OHC), S1~S7(SC)
	               AND (A.YD_CRN1  = B.CRN1 OR A.YD_CRN2 = B.CRN2)
	               AND A.YD_SCH_CD = P.V_YD_SCH_CD
	       ) SR
	 * </pre>
	 */
	public final static String getStatSchCd = "com.inisteel.cim.ys.sbr.dao.getStatSchCd";


	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.insWrkBook
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
	 * </pre>
	 */
	public final static String insWrkBook_sbr = "com.inisteel.cim.ys.sbr.dao.insWrkBook";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.insWrkBookMtl
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
	public final static String insWrkBookMtl_sbr = "com.inisteel.cim.ys.sbr.dao.insWrkBookMtl";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getStatEqp
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
	public final static String getStatEqp = "com.inisteel.cim.ys.sbr.dao.getStatEqp";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getCrnSchWbook
	-- (작업예약ID) 작업예약ID 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID
	      FROM DUAL
	)
	SELECT WB.YD_WBOOK_ID
	  FROM TB_YS_WRKBOOK WB
	      ,PARAM         PA
	 WHERE WB.YD_WBOOK_ID = PA.V_YD_WBOOK_ID
	   AND WB.DEL_YN      = 'N'
	   AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
	                                FROM TB_YS_CRNSCH
	                               WHERE DEL_YN = 'N')
	 * </pre>
	 */
	public final static String getCrnSchWbook = "com.inisteel.cim.ys.sbr.dao.getCrnSchWbook";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getCrnSchWbookSchcd
	-- (스케줄코드) 작업예약ID 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_SCH_CD AS V_YD_SCH_CD
	      FROM DUAL
	)
	SELECT YD_WBOOK_ID
	  FROM (SELECT WB.YD_WBOOK_ID
	          FROM TB_YS_WRKBOOK WB
	              ,PARAM         PA
	         WHERE WB.YD_SCH_CD = PA.V_YD_SCH_CD
	           AND WB.DEL_YN    = 'N'
	           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
	                                        FROM TB_YS_CRNSCH
	                                       WHERE DEL_YN = 'N')
	         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID)
	 WHERE ROWNUM = 1
	 * </pre>
	 */
	public final static String getCrnSchWbookSchcd = "com.inisteel.cim.ys.sbr.dao.getCrnSchWbookSchcd";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getCrnSchWbookEqp
	-- (설비코드) 작업예약ID 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_EQP_ID AS V_YD_EQP_ID
	      FROM DUAL
	)
	SELECT YD_WBOOK_ID
	  FROM (SELECT WB.YD_WBOOK_ID
	          FROM 
	               (
	                    -- "스케줄코드"별 작업 가능한 "크레인, 우선순위" 조회
	                    SELECT YD_SCH_CD
	                          ,CASE WHEN YD_EQP_CRN_STAT1 = 'O' AND YD_EQP_CRN_STAT2 = 'O' AND YD_CRN_PRIOR1 <= YD_CRN_PRIOR2 THEN YD_CRN1  -- 'O':정상, 'C':고장, 'X':NULL
	                                WHEN YD_EQP_CRN_STAT1 = 'O' AND YD_EQP_CRN_STAT2 = 'O' AND YD_CRN_PRIOR1 >  YD_CRN_PRIOR2 THEN YD_CRN2  -- 우선순위: 낮은 숫자가 빠름
	                                WHEN YD_EQP_CRN_STAT1 = 'O' AND YD_EQP_CRN_STAT2 = 'C'                                    THEN YD_CRN1
	                                WHEN YD_EQP_CRN_STAT1 = 'C' AND YD_EQP_CRN_STAT2 = 'O'                                    THEN YD_CRN2
	                                ELSE ''
	                           END AS YD_WRK_CRN
	                      FROM (
	                                -- "스케줄코드"별 "주/부크레인"의 "우선순위, 설비상태" 조회
	                                SELECT YD_SCH_CD
	                                      ,YD_CRN1
	                                      ,CASE WHEN YD_CRN_PRIOR1 <= 0 THEN 99 ELSE YD_CRN_PRIOR1 END AS YD_CRN_PRIOR1
	                                      ,(SELECT DECODE(YD_EQP_STAT,'B','C','O') FROM TB_YS_EQP WHERE YD_EQP_ID  = YD_CRN1) AS YD_EQP_CRN_STAT1  -- 설비   > 주크레인상태 ('B':고장)
	                                      ,YD_CRN2
	                                      ,CASE WHEN YD_CRN_PRIOR2 <= 0 THEN 99 ELSE YD_CRN_PRIOR2 END AS YD_CRN_PRIOR2
	                                      ,(SELECT DECODE(YD_EQP_STAT,'B','C','O') FROM TB_YS_EQP WHERE YD_EQP_ID  = YD_CRN2) AS YD_EQP_CRN_STAT2  -- 설비   > 부크레인상태 ('B':고장)
	                                  FROM TB_YS_SCHRULE                        -- YS_특수강스케줄기준
	                                 WHERE YD_GP      = 'G'                     -- 특수강-야드: 'G'
	                                   AND YD_BAY_GP  = 'D'                     -- 특수강-동  : 'D'
	                                   AND YD_DATA_GP = 'M'                     -- DATA구분: R(Register), M(Modify)
	                           )
	               ) SR
	              ,TB_YS_WRKBOOK WB
	              ,PARAM         PA
	         WHERE SR.YD_SCH_CD  = WB.YD_SCH_CD
	           AND SR.YD_WRK_CRN = PA.V_YD_EQP_ID
	           AND WB.DEL_YN     = 'N'
	           AND WB.YD_WBOOK_ID NOT IN (SELECT YD_WBOOK_ID
	                                        FROM TB_YS_CRNSCH
	                                       WHERE DEL_YN = 'N')
	         ORDER BY WB.YD_SCH_PRIOR, WB.YD_WBOOK_ID)
	 WHERE ROWNUM = 1
	 * </pre>
	 */
	public final static String getCrnSchWbookEqp = "com.inisteel.cim.ys.sbr.dao.getCrnSchWbookEqp";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.updWmStrLoc
	-- 크레인스케줄 작업예약재료 저장위치 수정
	
	MERGE INTO TB_YS_WRKBOOKMTL WM USING (
	    
	    SELECT YD_WBOOK_ID
	          ,SSTL_NO
	          ,:V_MODIFIER AS MODIFIER
	          ,SYSDATE     AS MOD_DDTT
	          ,YS_STK_COL_GP
	          ,YS_STK_BED_NO
	          ,YS_STK_LYR_NO
	          ,YS_STK_SEQ_NO
	          ,YD_UP_COLL_SEQ 
	      FROM (
	                SELECT WM.*
	                  FROM (
	                            SELECT WB.YD_WBOOK_ID
	                                  ,WB.YD_SCH_CD
	                                  ,WM.SSTL_NO
	                                  ,SL.YS_STK_COL_GP
	                                  ,SL.YS_STK_BED_NO
	                                  ,SL.YS_STK_LYR_NO
	                                  ,SL.YS_STK_SEQ_NO
	                                  ,SL.YS_STK_COL_GP||SL.YS_STK_BED_NO||SL.YS_STK_LYR_NO AS YS_STR_LOC
	                                  ,SL.YS_STK_COL_GP||SL.YS_STK_BED_NO AS YD_STK_COL_BED
	                                  ,RANK() OVER(PARTITION BY SL.YS_STK_COL_GP,SL.YS_STK_BED_NO
	                                                   ORDER BY SL.YS_STK_COL_GP,SL.YS_STK_BED_NO,SL.YS_STK_LYR_NO) AS YD_UP_COLL_SEQ
	                              FROM TB_YS_WRKBOOK    WB  -- PK: YD_WBOOK_ID
	                                  ,TB_YS_WRKBOOKMTL WM  -- PK: YD_WBOOK_ID, SSTL_NO 
	                                  ,TB_YS_STKLYR     SL  -- PK: YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO,YS_STK_SEQ_NO
	                                  ,TB_YS_STOCK      ST  -- PK: SSTL_NO
	                                  
	                             WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
	                               AND WM.SSTL_NO     = SL.SSTL_NO
	                               AND WM.SSTL_NO     = ST.SSTL_NO
	                               AND WB.YD_WBOOK_ID = :V_YD_WBOOK_ID
	                               AND WB.DEL_YN      = 'N'
	                               AND WM.DEL_YN      = 'N'
	                               AND WB.YD_GP       = SUBSTR(SL.YS_STK_COL_GP,1,1)
	                               AND SL.YD_STK_LYR_MTL_STAT = 'C'  -- 야드적치단재료상태 [C:적치중]
	                             ORDER BY YS_STR_LOC DESC
	                       ) WM
	                 ORDER BY YS_STR_LOC DESC
	           )
	) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.SSTL_NO = DD.SSTL_NO)
	WHEN MATCHED THEN UPDATE SET
	     WM.MODIFIER       = DD.MODIFIER
	    ,WM.MOD_DDTT       = DD.MOD_DDTT
	    ,WM.YS_STK_COL_GP  = DD.YS_STK_COL_GP
	    ,WM.YS_STK_BED_NO  = DD.YS_STK_BED_NO
	    ,WM.YS_STK_LYR_NO  = DD.YS_STK_LYR_NO
	    ,WM.YS_STK_SEQ_NO  = DD.YS_STK_SEQ_NO
	    ,WM.YD_UP_COLL_SEQ = DD.YD_UP_COLL_SEQ 
	 * </pre>
	 */
	public final static String updWmStrLoc = "com.inisteel.cim.ys.sbr.dao.updWmStrLoc";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getCrnSchStat
	-- 스케줄 수행가능여부 판단을 위한 상태정보 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID
	      FROM DUAL
	)
	SELECT WB.YD_GP                                                --야드구분
	      ,WB.YD_BAY_GP                                            --야드동구분
	      ,WB.YD_SCH_CD                                            --야드스케쥴코드 
	      ,WB.YD_SCH_PRIOR                                         --야드스케쥴우선순위
	      ,WB.YD_TO_LOC_DCSN_MTD                                   --야드TO위치결정방법
	      ,WB.YD_TO_LOC_GUIDE                                      --야드TO위치GUIDE
	      ,CASE WHEN WB.YD_SCH_CD LIKE '__TR__U_'                                                            THEN 'C'  -- "차량상차"
	            WHEN LENGTH(WB.YD_TO_LOC_GUIDE) >= 4 AND WB.YD_TO_LOC_GUIDE LIKE WB.YD_GP||WB.YD_BAY_GP||'%' THEN 'G'  -- "TO위치GUIDE": 작업예약에 "스판" 이상 등록가능함
	      	    ELSE 'Z'                                                                                               -- "기타"       : TF/PC/TC/TR
	      	END AS TO_LOC_CHK_GP                                   --TO위치점검을 위한 구분
	      ,TO_CHAR(WB.REG_DDTT,'YYYYMMDDHH24MISS') AS YD_WBOOK_DT  --야드작업예약일시
	      ,WB.YD_WRK_PLAN_CRN                                      --작업계획크레인
	      ,(SELECT YD_EQP_STAT     
	          FROM TB_YS_EQP 
	         WHERE YD_GP     = 'G'
	           AND YD_BAY_GP = 'D'
	           AND YD_EQP_ID = WB.YD_WRK_PLAN_CRN
	           AND DEL_YN    = 'N') AS YD_EQP_STAT_PLN             --작업계획크레인 야드설비상태
	      ,(SELECT YD_EQP_WRK_MODE 
	          FROM TB_YS_EQP 
	         WHERE YD_GP     = 'G'
	           AND YD_BAY_GP = 'D'
	           AND YD_EQP_ID = WB.YD_WRK_PLAN_CRN
	           AND DEL_YN    = 'N') AS YD_EQP_WRK_MODE_PLN         --작업계획크레인 설비작업MODE     
	      ,SR.YD_WRK_CRN                                           --작업크레인
	      ,SR.YD_WRK_CRN_PRIOR                                     --작업크레인 우선순위
	      ,SR.YD_WRK_CRN_STAT   AS YD_EQP_STAT_WRK                 --작업크레인 야드설비상태
	      ,(SELECT YD_EQP_WRK_MODE 
	          FROM TB_YS_EQP 
	         WHERE YD_GP     = 'G'
	           AND YD_BAY_GP = 'D'
	           AND YD_EQP_ID = SR.YD_WRK_CRN
	           AND DEL_YN    = 'N') AS YD_EQP_WRK_MODE_WRK         --작업크레인 설비작업MODE
	      ,(SELECT NVL(YD_STK_COL_ACT_STAT, '')
	          FROM TB_YS_STKCOL
	         WHERE YS_STK_COL_GP = SUBSTR(WB.YD_TO_LOC_GUIDE,1,6)
	           AND DEL_YN    = 'N') AS TO_LOC_COL_STAT             --TO위치-야드적치열활성상태: [L:적치가능][C:비활성화]
	      ,(SELECT YD_EQP_STAT
	          FROM TB_YS_EQP
	         WHERE YD_GP     = 'G'
	           AND YD_BAY_GP = 'D'
	           AND DEL_YN    = 'N'
	           AND WB.YD_SCH_CD LIKE 'GDTC__UM'
	           AND YD_EQP_ID = SUBSTR(WB.YD_SCH_CD,1,6)) AS YD_EQP_STAT_TCAR      --대차 야드설비상태
	      ,(SELECT DECODE(YD_EQP_WRK_MODE,'1','1','0')
	          FROM TB_YS_EQP
	         WHERE YD_GP     = 'G'
	           AND YD_BAY_GP = 'D'
	           AND DEL_YN    = 'N'
	           AND WB.YD_SCH_CD LIKE 'GDTC__UM'
	           AND YD_EQP_ID = SUBSTR(WB.YD_SCH_CD,1,6)) AS YD_EQP_WRK_MODE_TCAR  --대차 설비작업MODE [0:Offline Mode],[1:Online Mode]
		  ,(SELECT LISTAGG(YD_EQP_ID,',') WITHIN GROUP (ORDER BY YD_EQP_ID) 
		      FROM TB_YS_EQP 
		     WHERE YD_EQP_ID LIKE 'GDTC%' 
	           AND WB.YD_SCH_CD LIKE 'GDTC__UM'
		       AND YD_EQP_ID <> SUBSTR(WB.YD_SCH_CD,1,6)
		       AND YD_EQP_STAT <> 'B' 
		       AND YD_EQP_WRK_MODE <> '0'
	           AND DEL_YN = 'N') AS ENABLED_TCAR                   --사용가능한 다른 대차
		  ,(SELECT COUNT(*) 
		      FROM TB_YS_EQP 
		     WHERE YD_EQP_ID LIKE 'GDTC%' 
		       AND YD_EQP_ID <> SUBSTR(WB.YD_SCH_CD,1,6) 
		       AND YD_EQP_STAT <> 'B' 
		       AND YD_EQP_WRK_MODE <> '0'
	           AND DEL_YN = 'N') AS ENABLED_TCAR_CNT               --사용가능한 다른 대차 개수
	      ,(SELECT NVL(YD_CURR_BAY_GP, '')
	          FROM TB_YS_EQP
	         WHERE YD_EQP_ID = SUBSTR(WB.YD_TO_LOC_GUIDE,1,6)
	           AND DEL_YN    = 'N') AS YD_CURR_BAY_GP              --대차 현재동
	      ,(SELECT COUNT(*) 
			  FROM TB_YS_STKLYR
			 WHERE YS_STK_COL_GP LIKE SUBSTR(WB.YD_TO_LOC_GUIDE,1,6)||'%'
			   AND SSTL_NO IS NOT NULL
			   AND WB.YD_SCH_CD LIKE 'GDTC__UM'
			   AND DEL_YN = 'N') AS YD_CURR_BAY_SH                 --대차     재료매수 (대차 상하차시)
	      ,NVL(WM.TT_MTL_SH,0)  AS TT_MTL_SH                       --전체     재료매수
	      ,NVL(WM.WM_MTL_SH,0)  AS WM_MTL_SH                       --작업예약 재료매수
	      ,NVL(WM.ST_MTL_SH,0)  AS ST_MTL_SH                       --저장품   재료매수
	      ,NVL(WM.SL_MTL_SH,0)  AS SL_MTL_SH                       --적치단   재료매수
	      ,NVL(WM.STAT_C_SH,0)  AS STAT_C_SH                       --적치중인 재료매수
	      ,NVL(WM.STAT_SC_SH,0) AS STAT_SC_SH                      --서냉     재료매수
	      ,(SELECT COUNT(*)
	          FROM TB_YS_WRKBOOKMTL WM
	              ,TB_YS_STKLYR     SL
	         WHERE WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
	           AND WM.SSTL_NO     = SL.SSTL_NO
	           AND SL.YD_GP       = 'G'
	           AND SL.YS_STK_COL_GP NOT LIKE SUBSTR(WB.YD_SCH_CD,1,2)||'%'
	           AND SL.YD_STK_LYR_MTL_STAT = 'C'
	           AND WM.DEL_YN      = 'N'
	           AND SL.DEL_YN      = 'N') AS AB_LOC_SH              --저장위치이상 재료매수
	      ,(SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END
	          FROM TB_YS_WRKBOOKMTL WM
	              ,TB_YS_CRNSCH     CS
	              ,TB_YS_CRNWRKMTL  CM
	         WHERE WM.SSTL_NO       = CM.SSTL_NO
	           AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
	           AND WM.YD_WBOOK_ID   = WB.YD_WBOOK_ID
	           AND WM.YD_WBOOK_ID   = CS.YD_WBOOK_ID
	           AND CS.YD_GP         = 'G'
	           AND CS.YD_BAY_GP     = 'D'
	           AND WM.DEL_YN        = 'N'
	           AND CM.DEL_YN        = 'N'
	           AND CS.DEL_YN        = 'N') AS CM_DUP_YN            --크레인스케줄 재료중복여부
	      ,(SELECT MIN(CASE WHEN SL.YS_STK_COL_GP||SL.YS_STK_BED_NO||SL.YS_STK_LYR_NO = CS.YS_DN_WO_LOC||YS_DN_WO_LAYER                                THEN '1'
	                        WHEN SL.YS_STK_COL_GP||SL.YS_STK_BED_NO||SL.YS_STK_LYR_NO = CS.YS_UP_WO_LOC||YS_UP_WO_LAYER AND CS.YD_WRK_PROG_STAT != '2' THEN '2' END)  -- 야드작업진행상태: [2:권상완료]
	          FROM TB_YS_WRKBOOKMTL WM
	              ,TB_YS_STKLYR     SL
	              ,TB_YS_CRNSCH     CS
	         WHERE WM.SSTL_NO     = SL.SSTL_NO
	           AND SL.YD_GP       = 'G'
	           AND CS.YD_GP       = 'G'
	           AND CS.YD_BAY_GP   = 'D'
	           AND SL.YS_STK_COL_GP||SL.YS_STK_BED_NO IN (CS.YS_UP_WO_LOC, CS.YS_DN_WO_LOC)
	           AND (SUBSTR(SL.YS_STK_COL_GP,3,2) NOT IN ('TF') OR (SUBSTR(SL.YS_STK_COL_GP,3,2) = 'TR' AND WB.YD_SCH_CD NOT LIKE '__TR__U_'))  -- n개 크레인스케줄(권상or권하)이 생성 가능한 '보급대'or'차량상차' 시 저장위치중복 CHECK 안함 => 보급[TF], 추출[PC], 보급+추출[TC/TR]
	           AND WM.YD_WBOOK_ID = WB.YD_WBOOK_ID
	           AND WM.DEL_YN      = 'N'
	           AND SL.DEL_YN      = 'N'
	           AND CS.DEL_YN      = 'N') AS CL_DUP_GP              --크레인스케줄 저장위치중복여부: [1:권하위치와 중복],[2:권상위치와 중복]
	      ,WB.YD_CAR_USE_GP                                        --야드차량사용구분
	      ,WB.TRN_EQP_CD                                           --운송장비코드
	      ,WB.CAR_NO                                               --차량번호
	      ,WB.CARD_NO                                              --카드번호
	      ,(SELECT ITEM FROM USRYSA.TB_YS_RULE WHERE REPR_CD_GP = 'K99999') AS EFF_YN 
	      ,WM.YS_STK_COL_GP
	      
	      --선택된 작업예약이 "대차상차" 작업이면, 
	      --선택된 작업예약 보다 먼저 생성된 "대차상차" 작업예약들 중 동일 '스판/열'에 높은 적치단에 작업재료들이 존재하는지 확인
	      --(# 존재 시 먼저 생성된 "대차상차" 작업예약을 우선 실행하기 위해 선택된 작업예약의 크레인스케줄 생성을 취소함)
	      ,(SELECT 1
	          FROM DUAL
	         WHERE EXISTS(
					         SELECT * 
					          FROM TB_YS_WRKBOOK B1
					             , (SELECT YD_WBOOK_ID, MAX(YS_STK_COL_GP) AS YS_STK_COL_GP, MAX(YS_STK_LYR_NO) AS YS_STK_LYR_NO FROM TB_YS_WRKBOOKMTL WHERE DEL_YN = 'N' AND YS_STK_COL_GP LIKE 'GD%' GROUP BY YD_WBOOK_ID) M1 
					         WHERE 1 = 1
					           AND WB.YD_SCH_CD     LIKE 'GDTC__UM'
					           AND B1.YD_SCH_CD     LIKE 'GDTC__UM'
					           AND B1.DEL_YN        = 'N'
					           AND B1.YD_SCH_PRIOR  <= WB.YD_SCH_PRIOR 
					           AND B1.YD_WBOOK_ID   <  WB.YD_WBOOK_ID
					           AND B1.YD_WBOOK_ID   =  M1.YD_WBOOK_ID
					           AND M1.YS_STK_COL_GP =  WM.YS_STK_COL_GP
					           AND M1.YS_STK_LYR_NO >  WM.YS_STK_LYR_NO
					     )
	       ) AS TCUM_WRKBOOK_CHK
	      
	  FROM TB_YS_WRKBOOK WB
	      ,(
	            -- "스케줄코드"별 작업 가능한 "크레인, 우선순위" 조회
	            SELECT YD_SCH_CD
	                  ,CASE WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'O' AND YD_CRN_PRIOR1 <= YD_CRN_PRIOR2 THEN YD_CRN1  -- ['O':작업가능],['B':고장],['C':작업불가(작업중)],['X':NULL]
	                        WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'O' AND YD_CRN_PRIOR1 >  YD_CRN_PRIOR2 THEN YD_CRN2  -- 우선순위: 낮은 숫자가 빠름
	                        WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'C'                                    THEN YD_CRN1
	                        WHEN YD_EQP_CRN_STAT1  = 'C' AND YD_EQP_CRN_STAT2  = 'O'                                    THEN YD_CRN2
	                        WHEN YD_EQP_CRN_STAT1  = 'C' AND YD_EQP_CRN_STAT2  = 'C' THEN YD_CRN1
	                        WHEN YD_EQP_CRN_STAT1 <> 'B' AND YD_EQP_CRN_STAT2  = 'B' THEN YD_CRN1
	                        WHEN YD_EQP_CRN_STAT1  = 'B' AND YD_EQP_CRN_STAT2 <> 'B' THEN YD_CRN2
	                        ELSE ''
	                   END AS YD_WRK_CRN
	                  ,CASE WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'O' AND YD_CRN_PRIOR1 <= YD_CRN_PRIOR2 THEN YD_CRN_PRIOR1
	                        WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'O' AND YD_CRN_PRIOR1 >  YD_CRN_PRIOR2 THEN YD_CRN_PRIOR2
	                        WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'C'                                    THEN YD_CRN_PRIOR1
	                        WHEN YD_EQP_CRN_STAT1  = 'C' AND YD_EQP_CRN_STAT2  = 'O'                                    THEN YD_CRN_PRIOR2
	                        WHEN YD_EQP_CRN_STAT1  = 'C' AND YD_EQP_CRN_STAT2  = 'C' THEN YD_CRN_PRIOR1
	                        WHEN YD_EQP_CRN_STAT1 <> 'B' AND YD_EQP_CRN_STAT2  = 'B' THEN YD_CRN_PRIOR1
	                        WHEN YD_EQP_CRN_STAT1  = 'B' AND YD_EQP_CRN_STAT2 <> 'B' THEN YD_CRN_PRIOR2
	                        ELSE 0
	                   END AS YD_WRK_CRN_PRIOR
	                  ,CASE WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'O' AND YD_CRN_PRIOR1 <= YD_CRN_PRIOR2 THEN YD_EQP_CRN_STAT1
	                        WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'O' AND YD_CRN_PRIOR1 >  YD_CRN_PRIOR2 THEN YD_EQP_CRN_STAT2
	                        WHEN YD_EQP_CRN_STAT1  = 'O' AND YD_EQP_CRN_STAT2  = 'C'                                    THEN YD_EQP_CRN_STAT1
	                        WHEN YD_EQP_CRN_STAT1  = 'C' AND YD_EQP_CRN_STAT2  = 'O'                                    THEN YD_EQP_CRN_STAT2
	                        WHEN YD_EQP_CRN_STAT1  = 'C' AND YD_EQP_CRN_STAT2  = 'C' THEN YD_EQP_CRN_STAT1
	                        WHEN YD_EQP_CRN_STAT1 <> 'B' AND YD_EQP_CRN_STAT2  = 'B' THEN YD_EQP_CRN_STAT1
	                        WHEN YD_EQP_CRN_STAT1  = 'B' AND YD_EQP_CRN_STAT2 <> 'B' THEN YD_EQP_CRN_STAT2
	                        ELSE ''
	                   END AS YD_WRK_CRN_STAT
	              FROM (
	                        -- "스케줄코드"별 "주/부크레인"의 "우선순위, 설비상태" 조회
	                        SELECT YD_SCH_CD
	                              ,YD_CRN1
	                              ,CASE WHEN YD_CRN_PRIOR1 <= 0 THEN 99 ELSE YD_CRN_PRIOR1 END AS YD_CRN_PRIOR1
	--                            ,(SELECT DECODE(YD_EQP_STAT,'B','C','O') FROM TB_YS_EQP WHERE YD_EQP_ID  = YD_CRN1) AS YD_EQP_CRN_STAT1          -- 설비: 주크레인상태 ('B':고장)
	                              ,(SELECT DECODE(YD_EQP_STAT,'W','O','B','B','C') FROM TB_YS_EQP WHERE YD_EQP_ID  = YD_CRN1) AS YD_EQP_CRN_STAT1  -- 설비: 주크레인상태 ['W':대기],['O':작업가능],['B':고장],['C':작업불가(작업중)]
	                              ,YD_CRN2
	                              ,CASE WHEN YD_CRN_PRIOR2 <= 0 THEN 99 ELSE YD_CRN_PRIOR2 END AS YD_CRN_PRIOR2
	--                            ,(SELECT DECODE(YD_EQP_STAT,'B','C','O') FROM TB_YS_EQP WHERE YD_EQP_ID  = YD_CRN2) AS YD_EQP_CRN_STAT2          -- 설비: 부크레인상태 ('B':고장)
	                              ,(SELECT DECODE(YD_EQP_STAT,'W','O','B','B','C') FROM TB_YS_EQP WHERE YD_EQP_ID  = YD_CRN2) AS YD_EQP_CRN_STAT2  -- 설비: 주크레인상태 ['W':대기],['O':작업가능],['B':고장],['C':작업불가(작업중)]
	                          FROM TB_YS_SCHRULE     -- YS_특수강스케줄기준
	                         WHERE YD_GP      = 'G'  -- 특수강-야드: 'G'
	                           AND YD_BAY_GP  = 'D'  -- 특수강-동  : 'D'
	                           AND YD_DATA_GP = 'M'  -- DATA구분: R(Register), M(Modify)
	                   )
	        ) SR
	      ,(
	            SELECT WM.YD_WBOOK_ID
	                  ,COUNT(*)                                  AS TT_MTL_SH
	                  ,COUNT(DISTINCT WM.SSTL_NO)                AS WM_MTL_SH
	                  ,COUNT(DISTINCT ST.SSTL_NO)                AS ST_MTL_SH
	                  ,COUNT(DISTINCT SL.SSTL_NO)                AS SL_MTL_SH
	                  ,SUM(DECODE(SL.YD_STK_LYR_MTL_STAT,'C',1)) AS STAT_C_SH   -- 적치중인 재료매수
	                  ,SUM(DECODE(ST.BLOOM_CL_MTD,'A',1))        AS STAT_SC_SH  -- 서냉 재료매수
	                  ,MAX(WM.YS_STK_COL_GP)                     AS YS_STK_COL_GP
					  ,MAX(WM.YS_STK_LYR_NO)                     AS YS_STK_LYR_NO
	              FROM TB_YS_WRKBOOKMTL WM
	                  ,TB_YS_STOCK      ST
	                  ,TB_YS_STKLYR     SL
	                  ,PARAM            PA
	             WHERE WM.SSTL_NO     = ST.SSTL_NO(+)
	               AND WM.SSTL_NO     = SL.SSTL_NO(+)
	               AND WM.YD_WBOOK_ID = PA.V_YD_WBOOK_ID
	               AND SL.YD_GP       = 'G'
	               AND SL.YD_STK_LYR_MTL_STAT IN('C','U')  -- 야드적치단재료상태: [C:적치중],[U:권상대기]
	               AND WM.DEL_YN      = 'N'
	               AND ST.DEL_YN(+)   = 'N'
	               AND SL.DEL_YN(+)   = 'N'
	             GROUP BY WM.YD_WBOOK_ID
	       ) WM
	      ,PARAM PA
	 WHERE WB.YD_SCH_CD       = SR.YD_SCH_CD(+)
	   AND WB.YD_WBOOK_ID     = WM.YD_WBOOK_ID(+)
	   AND WB.YD_WBOOK_ID     = PA.V_YD_WBOOK_ID
	   AND WB.DEL_YN          = 'N'
	 * </pre>
	 */
	public final static String getCrnSchStat = "com.inisteel.cim.ys.sbr.dao.getCrnSchStat";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getWorkBookColGpBedGroupTr
	-- 작업예약재료들의 적치단('열/BED/단') 위치정보 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID
	      FROM DUAL
	)
	SELECT WM.YD_WBOOK_ID   AS YD_WBOOK_ID
	      ,WM.YS_STK_COL_GP AS YS_STK_COL_GP
	      ,WM.YS_STK_BED_NO AS YS_STK_BED_NO
	      ,WM.YS_STK_LYR_NO AS YS_STK_LYR_NO
	  FROM TB_YS_WRKBOOKMTL WM
	      ,PARAM            PA
	 WHERE WM.YD_WBOOK_ID = PA.V_YD_WBOOK_ID
	   AND WM.DEL_YN      = 'N'
	 GROUP BY WM.YD_WBOOK_ID, WM.YS_STK_COL_GP, WM.YS_STK_BED_NO, WM.YS_STK_LYR_NO
	 ORDER BY WM.YS_STK_COL_GP, WM.YS_STK_LYR_NO DESC, WM.YS_STK_BED_NO
	 * </pre>
	 */
	public final static String getWorkBookColGpBedGroupTr = "com.inisteel.cim.ys.sbr.dao.getWorkBookColGpBedGroupTr";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getStkLyrbyWBookIdGroupTr
	-- 조회된 적치단('열/BED/단') 상세정보 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID   AS V_YD_WBOOK_ID
	          ,:V_YS_STK_COL_GP AS V_YS_STK_COL_GP
	          ,:V_YS_STK_BED_NO AS V_YS_STK_BED_NO
	          ,:V_YS_STK_LYR_NO AS V_YS_STK_LYR_NO
	      FROM DUAL
	)
	SELECT X.SSTL_NO        
	      ,X.YS_STK_COL_GP  
	      ,X.YS_STK_BED_NO  
	      ,X.YS_STK_LYR_NO  
	      ,X.YD_UP_COLL_SEQ 
	      ,Y.YD_MTL_T       
	      ,Y.YD_MTL_W       
	      ,Y.YD_MTL_L       
	      ,Y.YD_MTL_WT      
	      ,Y.YD_STK_LOT_TP  
	      ,Y.YS_MTL_ITEM    
	      ,Y.HCR_GP         
	      ,Y.STL_PROG_CD    
	      ,X.YD_STK_LYR_MTL_STAT 
	      ,(
	            SELECT SUM((SELECT 1  
	                          FROM TB_YS_WRKBOOKMTL
	                         WHERE YD_WBOOK_ID = P.V_YD_WBOOK_ID
	                           AND SSTL_NO     = A.SSTL_NO))
	              FROM TB_YS_STKLYR A
	             WHERE A.YS_STK_COL_GP = X.YS_STK_COL_GP
	               AND A.YS_STK_BED_NO = X.YS_STK_BED_NO
	               AND A.YS_STK_LYR_NO = X.YS_STK_LYR_NO
	       ) AS WRKBOOKMTL_CNT -- 해당단에 작업예약매수
	      ,CASE WHEN X.YS_STK_LYR_NO = P.V_YS_STK_LYR_NO THEN 'Y' ELSE 'N' END AS LOWEST_LYR_NO
	     
	  FROM (
	            SELECT MAX(A.SSTL_NO)             AS SSTL_NO                  
	                  ,A.YS_STK_COL_GP            AS YS_STK_COL_GP           
	                  ,A.YS_STK_BED_NO            AS YS_STK_BED_NO           
	                  ,A.YS_STK_LYR_NO            AS YS_STK_LYR_NO           
	                  ,MAX(B.YD_UP_COLL_SEQ)      AS YD_UP_COLL_SEQ          
	                  ,MAX(A.YD_STK_LYR_MTL_STAT) AS YD_STK_LYR_MTL_STAT
	              FROM TB_YS_STKLYR A                                   
	                  ,(
	                        SELECT SSTL_NO
	                             , YD_UP_COLL_SEQ
	                             , DEL_YN
	                          FROM TB_YS_WRKBOOKMTL
	                         WHERE YD_WBOOK_ID = (SELECT V_YD_WBOOK_ID FROM PARAM)
	                   ) B
	                  ,PARAM PA
	             WHERE A.SSTL_NO             = B.SSTL_NO(+)              
	               AND A.YS_STK_COL_GP       = PA.V_YS_STK_COL_GP      
	               AND A.YS_STK_BED_NO       = PA.V_YS_STK_BED_NO     
	               AND A.YS_STK_LYR_NO       = PA.V_YS_STK_LYR_NO   
	               AND A.YD_STK_LYR_MTL_STAT = 'C'
	               AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)
	               AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
	             GROUP BY YS_STK_COL_GP, YS_STK_BED_NO, A.YS_STK_LYR_NO
	       ) X        
	      ,TB_YS_STOCK Y
	      ,PARAM       P
	      
	 WHERE X.SSTL_NO = Y.SSTL_NO                                      
	   AND (Y.DEL_YN <> 'Y' OR Y.DEL_YN IS NULL)
	 ORDER BY X.YS_STK_COL_GP, X.YS_STK_BED_NO, X.YS_STK_LYR_NO DESC
	 * </pre>
	 */
	public final static String getStkLyrbyWBookIdGroupTr = "com.inisteel.cim.ys.sbr.dao.getStkLyrbyWBookIdGroupTr";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getWorkBookColGpGroup
	-- 작업예약재료들의 적치열 내 최하단 위치정보 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID   AS V_YD_WBOOK_ID
	      FROM DUAL
	)
	SELECT WB.YD_WBOOK_ID        AS YD_WBOOK_ID
	      ,WB.YS_STK_COL_GP      AS YS_STK_COL_GP
	      ,MIN(WB.YS_STK_LYR_NO) AS YS_STK_LYR_NO
	  FROM TB_YS_WRKBOOKMTL WB
	      ,PARAM            PA
	 WHERE WB.YD_WBOOK_ID = PA.V_YD_WBOOK_ID
	   AND WB.DEL_YN      = 'N'
	 GROUP BY WB.YD_WBOOK_ID, WB.YS_STK_COL_GP
	 ORDER BY WB.YS_STK_COL_GP DESC
	 * </pre>
	 */
	public final static String getWorkBookColGpGroup = "com.inisteel.cim.ys.sbr.dao.getWorkBookColGpGroup";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getStkLyrbyWBookIdGroupV1
	-- 크레인작업단위('열/BED/단')별 위치 및 정보 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID   AS V_YD_WBOOK_ID
	          ,:V_YS_STK_COL_GP AS V_YS_STK_COL_GP
	          ,:V_YS_STK_LYR_NO AS V_YS_STK_LYR_NO
	      FROM DUAL
	)
	SELECT *
	  FROM (
	            SELECT X.SSTL_NO       
	                 , X.YS_STK_COL_GP  
	                 , X.YS_STK_BED_NO  
	                 , X.YS_STK_LYR_NO  
	                 , X.YD_UP_COLL_SEQ 
	                 , Y.YD_MTL_T       
	                 , Y.YD_MTL_W       
	                 , Y.YD_MTL_L       
	                 , Y.YD_MTL_WT      
	                 , Y.YD_STK_LOT_TP  
	                 , Y.YD_STK_LOT_CD  
	                 , Y.YS_MTL_ITEM    
	                 , Y.HCR_GP         
	                 , Y.STL_PROG_CD    
	                 , X.YD_STK_LYR_MTL_STAT 
	                 , X.WRKBOOKMTL_CNT -- 해당단에 작업예약매수
	                 , (SELECT COUNT(*)  
	                      FROM TB_YS_STKLYR A                                   
	                     WHERE A.YS_STK_COL_GP     = X.YS_STK_COL_GP
	                       AND A.YS_STK_BED_NO     = X.YS_STK_BED_NO
	                       AND A.YS_STK_LYR_NO     = X.YS_STK_LYR_NO
	                       AND  A.SSTL_NO IS NOT NULL
	                   )  AS LYR_CNT   -- 해당단에 작업재료매수 
	                 , (SELECT DECODE(SUM((
	                             SELECT COUNT(*)
	                               FROM TB_YS_STKLYR A
	                                  , TB_YS_STOCK B
	                              WHERE A.SSTL_NO = B.SSTL_NO
	                                AND A.YS_STK_COL_GP = A1.YS_STK_COL_GP
	                                AND A.YS_STK_BED_NO = A1.YS_STK_BED_NO
	                                AND A.YS_STK_LYR_NO = A1.YS_STK_LYR_NO
	                                AND A.YS_STK_SEQ_NO > A1.YS_STK_SEQ_NO
	                                AND B.YD_CHG_NO < B1.YD_CHG_NO
	                          )),0,'Y','N') AS CHECK_SUM
	                     FROM TB_YS_STKLYR A1
	                        , TB_YS_STOCK B1
	                    WHERE A1.SSTL_NO = B1.SSTL_NO
	                      AND A1.YS_STK_COL_GP = X.YS_STK_COL_GP
	                      AND A1.YS_STK_BED_NO = X.YS_STK_BED_NO 
	                      AND A1.YS_STK_LYR_NO = X.YS_STK_LYR_NO) AS YD_CHG_NO_SEQ_YN -- 장입순번 CEHCK    
	                 , CASE WHEN X.YS_STK_LYR_NO = P.V_YS_STK_LYR_NO THEN 'Y' ELSE 'N' END AS LOWEST_LYR_NO
	                 , (SELECT MAX(YD_SCH_CD) AS YD_SCH_CD FROM TB_YS_WRKBOOK WHERE YD_WBOOK_ID = P.V_YD_WBOOK_ID AND DEL_YN ='N') AS YD_SCH_CD  --추가
	                 ,Y.ITEMNAME_CD --추가
	                 
	              FROM (
	                        SELECT MAX(A.SSTL_NO)             AS SSTL_NO                  
	                             , A.YS_STK_COL_GP            AS YS_STK_COL_GP           
	                             , A.YS_STK_BED_NO            AS YS_STK_BED_NO           
	                             , A.YS_STK_LYR_NO            AS YS_STK_LYR_NO           
	                             , MAX(B.YD_UP_COLL_SEQ )     AS YD_UP_COLL_SEQ          
	                             , MAX(A.YD_STK_LYR_MTL_STAT) AS YD_STK_LYR_MTL_STAT     
	                             , COUNT(A.SSTL_NO)           AS WRKBOOKMTL_CNT            
	                          FROM TB_YS_STKLYR A                                   
	                             , (
	                                    SELECT SSTL_NO
	                                         , YD_UP_COLL_SEQ
	                                         , DEL_YN
	                                      FROM TB_YS_WRKBOOKMTL
	                                     WHERE YD_WBOOK_ID = (SELECT V_YD_WBOOK_ID FROM PARAM)
	                               ) B
	                             , PARAM P
	                         WHERE A.SSTL_NO       = B.SSTL_NO(+)              
	                           AND A.YS_STK_COL_GP = P.V_YS_STK_COL_GP
	                           AND A.YS_STK_LYR_NO > P.V_YS_STK_LYR_NO
	                           AND A.YD_STK_LYR_MTL_STAT = 'C'
	                           AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)            
	                           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
	                         GROUP BY YS_STK_COL_GP, YS_STK_BED_NO,A.YS_STK_LYR_NO 
	                         
	                        UNION ALL
	                          
	                        SELECT MAX(A.SSTL_NO)             AS SSTL_NO                  
	                             , A.YS_STK_COL_GP            AS YS_STK_COL_GP           
	                             , A.YS_STK_BED_NO            AS YS_STK_BED_NO           
	                             , A.YS_STK_LYR_NO            AS YS_STK_LYR_NO           
	                             , MAX(B.YD_UP_COLL_SEQ )     AS YD_UP_COLL_SEQ          
	                             , MAX(A.YD_STK_LYR_MTL_STAT) AS YD_STK_LYR_MTL_STAT    
	                             , COUNT(A.SSTL_NO)           AS WRKBOOKMTL_CNT                  
	                          FROM TB_YS_STKLYR A                                   
	                             , (
	                                    SELECT SSTL_NO
	                                         , YD_UP_COLL_SEQ
	                                         , YS_STK_COL_GP
	                                         , YS_STK_BED_NO
	                                         , DEL_YN
	                                      FROM TB_YS_WRKBOOKMTL
	                                     WHERE YD_WBOOK_ID = (SELECT V_YD_WBOOK_ID FROM PARAM)
	                               ) B
	                             , PARAM P
	                         WHERE A.SSTL_NO       = B.SSTL_NO(+)              
	                           AND A.YS_STK_COL_GP = B.YS_STK_COL_GP
	                           AND A.YS_STK_BED_NO = B.YS_STK_BED_NO
	                           AND A.YS_STK_COL_GP = P.V_YS_STK_COL_GP
	                           AND A.YS_STK_LYR_NO = P.V_YS_STK_LYR_NO
	                           AND A.YD_STK_LYR_MTL_STAT = 'C'
	                           AND (A.DEL_YN <> 'Y' OR A.DEL_YN IS NULL)            
	                           AND (B.DEL_YN <> 'Y' OR B.DEL_YN IS NULL)
	                         GROUP BY A.YS_STK_COL_GP, A.YS_STK_BED_NO, A.YS_STK_LYR_NO
	                   ) X        
	                  ,TB_YS_STOCK Y
	                  ,PARAM       P
	             WHERE X.SSTL_NO = Y.SSTL_NO
	               AND (Y.DEL_YN <> 'Y' OR Y.DEL_YN IS NULL)
	)AA
	ORDER BY AA.YS_STK_COL_GP, AA.YS_STK_LYR_NO DESC, AA.YS_STK_BED_NO
	 * </pre>
	 */
	public final static String getStkLyrbyWBookIdGroupV1 = "com.inisteel.cim.ys.sbr.dao.getStkLyrbyWBookIdGroupV1";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getYdStklyrSstl
	-- 크레인스케줄(권상위치) 재료정보 조회
	 
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID   AS V_YD_WBOOK_ID
	          ,:V_YS_STK_COL_GP AS V_YS_STK_COL_GP
	          ,:V_YS_STK_BED_NO AS V_YS_STK_BED_NO
	          ,:V_YS_STK_LYR_NO AS V_YS_STK_LYR_NO
	          ,:V_WRK_SPR       AS V_WRK_SPR
	      FROM DUAL
	)
	SELECT A.YS_STK_COL_GP
	     , A.YS_STK_BED_NO
	     , A.YS_STK_LYR_NO
	     , A.YS_STK_SEQ_NO
	     , A.SSTL_NO         
	     , B.YD_STK_LOT_TP     
	     , B.HCR_GP
	     , B.STL_PROG_CD
	     , B.YS_MTL_ITEM
	  FROM TB_YS_STKLYR A
	     , TB_YS_STOCK  B
	     , PARAM        P
	 WHERE A.SSTL_NO = B.SSTL_NO 
	   AND A.YS_STK_COL_GP = P.V_YS_STK_COL_GP
	   AND A.YS_STK_BED_NO = P.V_YS_STK_BED_NO
	   AND A.YS_STK_LYR_NO = P.V_YS_STK_LYR_NO
	   AND A.SSTL_NO IS NOT NULL 
	   AND A.DEL_YN = 'N'
	   AND NVL(P.V_WRK_SPR,'ALL') = 'ALL'
	
	UNION ALL   
	
	SELECT A.YS_STK_COL_GP   
	     , A.YS_STK_BED_NO   
	     , A.YS_STK_LYR_NO   
	     , A.YS_STK_SEQ_NO   
	     , A.SSTL_NO         
	     , B.YD_STK_LOT_TP     
	     , B.HCR_GP
	     , B.STL_PROG_CD
	     , B.YS_MTL_ITEM
	  FROM TB_YS_STKLYR A
	     , TB_YS_STOCK  B  
	     , PARAM        P
	 WHERE A.SSTL_NO = B.SSTL_NO 
	   AND A.YS_STK_COL_GP = P.V_YS_STK_COL_GP
	   AND A.YS_STK_BED_NO = P.V_YS_STK_BED_NO
	   AND A.YS_STK_LYR_NO = P.V_YS_STK_LYR_NO
	   AND A.SSTL_NO IS NOT NULL 
	   AND A.DEL_YN = 'N' 
	   AND P.V_WRK_SPR = 'TARGET'
	   AND A.SSTL_NO IN (SELECT SSTL_NO 
	                       FROM TB_YS_WRKBOOKMTL                          
			              WHERE YD_WBOOK_ID = P.V_YD_WBOOK_ID  
		  	                AND DEL_YN = 'N')
		  	                
	 ORDER BY YS_STK_COL_GP, YS_STK_BED_NO, YS_STK_LYR_NO, YS_STK_SEQ_NO
	 * </pre>
	 */
	public final static String getYdStklyrSstl = "com.inisteel.cim.ys.sbr.dao.getYdStklyrSstl";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.insYdCrnsch
	-- 크레인스케줄 등록
	
	INSERT INTO TB_YS_CRNSCH
	(
		 YD_CRN_SCH_ID
		,REGISTER
		,REG_DDTT
		,DEL_YN
		,YD_WBOOK_ID
		,YD_EQP_ID
		,YD_GP
		,YD_BAY_GP
		,YD_SCH_CD
		,YD_SCH_ST_GP
		,YD_SCH_REQ_GP
		,YD_SCH_PRIOR
		,YD_EQP_WRK_STAT
		,YD_WRK_PROG_STAT
		,YD_WBOOK_DT
		,YD_SCH_DT
		,YD_WORD_DT
		,YD_UP_CMPL_DT
		,YD_DN_CMPL_DT
		,YD_WRK_HDS_DD
		,YD_WRK_DUTY
		,YD_WRK_PARTY
		,YD_MAIN_WRK_MTL_SH
		,YD_AID_WRK_MTL_SH
		,YD_AID_WRK_UPDN_GP
		,YD_TO_LOC_DCSN_MTD
		,YD_TO_LOC_GUIDE
		,YD_EQP_WRK_SH
		,YD_EQP_WRK_WT
		,YD_EQP_WRK_T
		,YD_EQP_WRK_MAX_W
		,YD_EQP_WRK_MAX_L
		,YD_CRN_SB_CTL_H
		,YD_CRN_GRAB_USE_RULE_ID
		,YS_UP_WO_LOC
		,YS_UP_WO_LAYER
		,YS_UP_WO_SEQ_NO
		,YD_UP_WO_LOC_XAXIS
		,YD_UP_WO_XAXIS_GAP_MAX
		,YD_UP_WO_XAXIS_GAP_MIN
		,YD_UP_WO_LOC_YAXIS
		,YD_UP_WO_LOC_YAXIS1
		,YD_UP_WO_LOC_YAXIS2
		,YD_UP_WO_YAXIS_GAP_MAX
		,YD_UP_WO_YAXIS_GAP_MIN
		,YD_UP_WO_LOC_ZAXIS
		,YD_UP_WO_ZAXIS_GAP_MAX
		,YD_UP_WO_ZAXIS_GAP_MIN
		,YS_DN_WO_LOC
		,YS_DN_WO_LAYER
		,YS_DN_WO_SEQ_NO
		,YD_DN_WO_LOC_XAXIS
		,YD_DN_WO_XAXIS_GAP_MAX
		,YD_DN_WO_XAXIS_GAP_MIN
		,YD_DN_WO_LOC_YAXIS
		,YD_DN_WO_LOC_YAXIS1
		,YD_DN_WO_LOC_YAXIS2
		,YD_DN_WO_YAXIS_GAP_MAX
		,YD_DN_WO_YAXIS_GAP_MIN
		,YD_DN_WO_LOC_ZAXIS
		,YD_DN_WO_ZAXIS_GAP_MAX
		,YD_DN_WO_ZAXIS_GAP_MIN
		,YS_UP_WR_LOC
		,YS_UP_WR_LAYER
	    ,YS_UP_WR_SEQ_NO
		,YD_UP_WRK_ACT_GP
		,YD_UP_WR_XAXIS
		,YD_UP_WR_YAXIS
		,YD_UP_WR_YAXIS1
		,YD_UP_WR_YAXIS2
		,YD_UP_WR_ZAXIS
		,YS_DN_WR_LOC
		,YS_DN_WR_LAYER
	    ,YS_DN_WR_SEQ_NO
		,YD_DN_WRK_ACT_GP
		,YD_DN_WR_XAXIS
		,YD_DN_WR_YAXIS
		,YD_DN_WR_YAXIS1
		,YD_DN_WR_YAXIS2
		,YD_DN_WR_ZAXIS 
	)
	VALUES
	(
	     :V_YD_CRN_SCH_ID
		,:V_REGISTER
		,SYSDATE
		,'N'
		,:V_YD_WBOOK_ID
		,:V_YD_EQP_ID
		,:V_YD_GP
		,:V_YD_BAY_GP
		,:V_YD_SCH_CD
		,(SELECT YD_EQP_STAT FROM TB_YD_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID)
		,:V_YD_SCH_REQ_GP
		,:V_YD_SCH_PRIOR
		,:V_YD_EQP_WRK_STAT
		,:V_YD_WRK_PROG_STAT
		,TO_DATE(:V_YD_WBOOK_DT,'YYYYMMDDHH24MISS') 
		,SYSDATE
		,:V_YD_WORD_DT
		,:V_YD_UP_CMPL_DT
		,:V_YD_DN_CMPL_DT
		,TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD')
		,CASE WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '000000' AND '065959' THEN '3' 
		  	  WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '070000' AND '145959' THEN '1'
			  WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '150000' AND '225959' THEN '2' 
			  WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '230000' AND '235959' THEN '3' 
		 END
		,:V_YD_WRK_PARTY
		,:V_YD_MAIN_WRK_MTL_SH
		,:V_YD_AID_WRK_MTL_SH
		,:V_YD_AID_WRK_UPDN_GP
		,:V_YD_TO_LOC_DCSN_MTD
		,:V_YD_TO_LOC_GUIDE
		,:V_YD_EQP_WRK_SH
		,:V_YD_EQP_WRK_WT
		,:V_YD_EQP_WRK_T
		,:V_YD_EQP_WRK_MAX_W
		,:V_YD_EQP_WRK_MAX_L
		,:V_YD_CRN_SB_CTL_H
		,:V_YD_CRN_GRAB_USE_RULE_ID
		,:V_YS_UP_WO_LOC
		,:V_YS_UP_WO_LAYER
		,:V_YS_UP_WO_SEQ_NO
		,:V_YD_UP_WO_LOC_XAXIS
		,:V_YD_UP_WO_XAXIS_GAP_MAX
		,:V_YD_UP_WO_XAXIS_GAP_MIN
		,:V_YD_UP_WO_LOC_YAXIS
		,:V_YD_UP_WO_LOC_YAXIS1
		,:V_YD_UP_WO_LOC_YAXIS2
		,:V_YD_UP_WO_YAXIS_GAP_MAX
		,:V_YD_UP_WO_YAXIS_GAP_MIN
		,:V_YD_UP_WO_LOC_ZAXIS
		,:V_YD_UP_WO_ZAXIS_GAP_MAX
		,:V_YD_UP_WO_ZAXIS_GAP_MIN
		,:V_YS_DN_WO_LOC
		,:V_YS_DN_WO_LAYER
		,:V_YS_DN_WO_SEQ_NO
		,:V_YD_DN_WO_LOC_XAXIS
		,:V_YD_DN_WO_XAXIS_GAP_MAX
		,:V_YD_DN_WO_XAXIS_GAP_MIN
		,:V_YD_DN_WO_LOC_YAXIS
		,:V_YD_DN_WO_LOC_YAXIS1
		,:V_YD_DN_WO_LOC_YAXIS2
		,:V_YD_DN_WO_YAXIS_GAP_MAX
		,:V_YD_DN_WO_YAXIS_GAP_MIN
		,:V_YD_DN_WO_LOC_ZAXIS
		,:V_YD_DN_WO_ZAXIS_GAP_MAX
		,:V_YD_DN_WO_ZAXIS_GAP_MIN
		,:V_YS_UP_WR_LOC
		,:V_YS_UP_WR_LAYER
	    ,:V_YS_UP_WR_SEQ_NO
		,:V_YD_UP_WRK_ACT_GP
		,:V_YD_UP_WR_XAXIS
		,:V_YD_UP_WR_YAXIS
		,:V_YD_UP_WR_YAXIS1
		,:V_YD_UP_WR_YAXIS2
		,:V_YD_UP_WR_ZAXIS
		,:V_YS_DN_WR_LOC
		,:V_YS_DN_WR_LAYER
	    ,:V_YS_DN_WR_SEQ_NO
		,:V_YD_DN_WRK_ACT_GP
		,:V_YD_DN_WR_XAXIS
		,:V_YD_DN_WR_YAXIS
		,:V_YD_DN_WR_YAXIS1
		,:V_YD_DN_WR_YAXIS2
		,:V_YD_DN_WR_ZAXIS 
	)
	 * </pre>
	 */
	public final static String insYdCrnsch = "com.inisteel.cim.ys.sbr.dao.insYdCrnsch";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.updYdStkLyrMtlStat
	-- 권상위치 작업재료들의 재료상태를 "권상대기"로 변경: 적치단(TB_YS_STKLYR)
	
	UPDATE TB_YS_STKLYR            
	   SET MOD_DDTT            = SYSDATE
	     , MODIFIER            = :V_MODIFIER
	     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT, YD_STK_LYR_MTL_STAT)
	 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
	   AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
	   AND SSTL_NO       = :V_SSTL_NO
	 * </pre>
	 */
	public final static String updYdStkLyrMtlStat = "com.inisteel.cim.ys.sbr.dao.updYdStkLyrMtlStat";

	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.insYdCrnwrkmtl
	-- 크레인스케줄 작업재료 등록
	
	MERGE INTO TB_YS_CRNWRKMTL CS USING (
	
	    SELECT :V_YD_CRN_SCH_ID AS V_YD_CRN_SCH_ID
	          ,:V_SSTL_NO AS V_SSTL_NO
	          ,:V_REGISTER AS V_REGISTER
	     
	          ,:V_YD_AID_WRK_YN AS V_YD_AID_WRK_YN
	          ,:V_YS_STK_LYR_NO AS V_YS_STK_LYR_NO
	          ,:V_YS_STK_SEQ_NO AS V_YS_STK_SEQ_NO
	          ,:V_YD_STK_LOT_TP AS V_YD_STK_LOT_TP
	          ,:V_YD_STK_LOT_CD AS V_YD_STK_LOT_CD
	          ,:V_HCR_GP AS V_HCR_GP
	          ,:V_STL_PROG_CD AS V_STL_PROG_CD
	          ,:V_YS_MTL_ITEM AS V_YS_MTL_ITEM
	          ,:V_YS_ROUTE_GP AS V_YS_ROUTE_GP
	          ,:V_YD_TO_LOC_DCSN_MTD AS V_YD_TO_LOC_DCSN_MTD
	      FROM DUAL
	) DD ON (CS.YD_CRN_SCH_ID = DD.V_YD_CRN_SCH_ID AND CS.SSTL_NO = DD.V_SSTL_NO )
	WHEN MATCHED THEN 
	     UPDATE SET 
	         CS.MODIFIER           = DD.V_REGISTER
	        ,CS.MOD_DDTT           = SYSDATE
	        ,CS.DEL_YN             = 'N'
	        ,CS.YD_AID_WRK_YN      = DD.V_YD_AID_WRK_YN
	        ,CS.YS_STK_LYR_NO      = DD.V_YS_STK_LYR_NO
	        ,CS.YS_STK_SEQ_NO      = DD.V_YS_STK_SEQ_NO
	        ,CS.YD_STK_LOT_TP      = DD.V_YD_STK_LOT_TP
	        ,CS.YD_STK_LOT_CD      = DD.V_YD_STK_LOT_CD
	        ,CS.HCR_GP             = DD.V_HCR_GP
	        ,CS.STL_PROG_CD        = DD.V_STL_PROG_CD
	        ,CS.YS_MTL_ITEM        = DD.V_YS_MTL_ITEM
	        ,CS.YS_ROUTE_GP        = DD.V_YS_ROUTE_GP
	        ,CS.YD_TO_LOC_DCSN_MTD = DD.V_YD_TO_LOC_DCSN_MTD
	WHEN NOT MATCHED THEN 
	     INSERT (
	         CS.YD_CRN_SCH_ID
	        ,CS.SSTL_NO
	        ,CS.REGISTER
	        ,CS.REG_DDTT
	        ,CS.DEL_YN
	        ,CS.YD_AID_WRK_YN
	        ,CS.YS_STK_LYR_NO
	        ,CS.YS_STK_SEQ_NO
	        ,CS.YD_STK_LOT_TP
	        ,CS.YD_STK_LOT_CD
	        ,CS.HCR_GP
	        ,CS.STL_PROG_CD
	        ,CS.YS_MTL_ITEM
	        ,CS.YS_ROUTE_GP
	        ,CS.YD_TO_LOC_DCSN_MTD )
	     VALUES (
	         DD.V_YD_CRN_SCH_ID
	        ,DD.V_SSTL_NO
	        ,DD.V_REGISTER
	        ,SYSDATE
	        ,'N'
	        ,DD.V_YD_AID_WRK_YN
	        ,DD.V_YS_STK_LYR_NO
	        ,DD.V_YS_STK_SEQ_NO
	        ,DD.V_YD_STK_LOT_TP
	        ,DD.V_YD_STK_LOT_CD
	        ,DD.V_HCR_GP
	        ,DD.V_STL_PROG_CD
	        ,DD.V_YS_MTL_ITEM
	        ,DD.V_YS_ROUTE_GP
	        ,DD.V_YD_TO_LOC_DCSN_MTD )
	 * </pre>
	 */
	public final static String insYdCrnwrkmtl = "com.inisteel.cim.ys.sbr.dao.insYdCrnwrkmtl";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getYdWrkbook
	-- 작업예약 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID   AS V_YD_WBOOK_ID
	      FROM DUAL
	)
	SELECT YD_WBOOK_ID         AS YD_WBOOK_ID
	      ,DEL_YN              AS DEL_YN
	      ,YD_GP               AS YD_GP
	      ,YD_BAY_GP           AS YD_BAY_GP
	      ,YD_SCH_CD           AS YD_SCH_CD
	      ,YD_SCH_PRIOR        AS YD_SCH_PRIOR
	      ,YD_SCH_PROG_STAT    AS YD_SCH_PROG_STAT
	      ,YD_SCH_ST_GP        AS YD_SCH_ST_GP
	      ,YD_SCH_REQ_GP       AS YD_SCH_REQ_GP
	      ,YD_AIM_YD_GP        AS YD_AIM_YD_GP
	      ,YD_AIM_BAY_GP       AS YD_AIM_BAY_GP
	      ,YD_CTS_RELAY_YN     AS YD_CTS_RELAY_YN
	      ,YD_CTS_RELAY_BAY_GP AS YD_CTS_RELAY_BAY_GP
	      ,YD_TO_LOC_DCSN_MTD  AS YD_TO_LOC_DCSN_MTD
	      ,YD_TO_LOC_GUIDE     AS YD_TO_LOC_GUIDE
	      ,YD_WRK_PLAN_TCAR    AS YD_WRK_PLAN_TCAR
	      ,YD_CAR_USE_GP
	      ,TRN_EQP_CD          AS TRN_EQP_CD
	      ,CAR_NO              AS CAR_NO
	      ,CARD_NO             AS CARD_NO
	      ,REGISTER            AS REGISTER
	      ,MODIFIER            AS MODIFIER
	      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	  FROM TB_YS_WRKBOOK WB
	      ,PARAM         PA
	 WHERE YD_WBOOK_ID = PA.V_YD_WBOOK_ID
	 * </pre>
	 */
	public final static String getYdWrkbook = "com.inisteel.cim.ys.sbr.dao.getYdWrkbook";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getYdCrnschByEqpIdandWBookId
	-- 크레인스케줄 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID
	          ,:V_YD_EQP_ID   AS V_YD_EQP_ID
	      FROM DUAL
	)
	SELECT A.YD_EQP_ID               AS YD_EQP_ID
	      ,A.YD_EQP_NAME             AS YD_EQP_NAME
	      ,B.YD_CRN_SCH_ID           AS YD_CRN_SCH_ID
	      ,B.REGISTER                AS REGISTER
	      ,TO_CHAR(B.REG_DDTT, 'YYYYMMDDHH24MISS') AS REG_DDTT
	      ,B.MODIFIER                AS MODIFIER
	      ,TO_CHAR(B.MOD_DDTT, 'YYYYMMDDHH24MISS') AS MOD_DDTT
	      ,B.DEL_YN                  AS DEL_YN
	      ,B.YD_WBOOK_ID             AS YD_WBOOK_ID
	      ,B.YD_GP                   AS YD_GP
	      ,B.YD_BAY_GP               AS YD_BAY_GP
	      ,B.YD_SCH_CD               AS YD_SCH_CD
	      ,B.YD_SCH_ST_GP            AS YD_SCH_ST_GP
	      ,B.YD_SCH_REQ_GP           AS YD_SCH_REQ_GP
	      ,B.YD_SCH_PRIOR            AS YD_SCH_PRIOR
	      ,B.YD_EQP_WRK_STAT         AS YD_EQP_WRK_STAT
	      ,B.YD_WRK_PROG_STAT        AS YD_WRK_PROG_STAT
	      ,TO_CHAR(B.YD_WBOOK_DT  , 'YYYYMMDDHH24MISS') AS YD_WBOOK_DT
	      ,TO_CHAR(B.YD_SCH_DT    , 'YYYYMMDDHH24MISS') AS YD_SCH_DT
	      ,TO_CHAR(B.YD_WORD_DT   , 'YYYYMMDDHH24MISS') AS YD_WORD_DT
	      ,TO_CHAR(B.YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_UP_CMPL_DT
	      ,TO_CHAR(B.YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS') AS YD_DN_CMPL_DT
	      ,B.YD_WRK_HDS_DD           AS YD_WRK_HDS_DD
	      ,B.YD_WRK_DUTY             AS YD_WRK_DUTY
	      ,B.YD_WRK_PARTY            AS YD_WRK_PARTY
	      ,B.YD_MAIN_WRK_MTL_SH      AS YD_MAIN_WRK_MTL_SH
	      ,B.YD_AID_WRK_MTL_SH       AS YD_AID_WRK_MTL_SH
	      ,B.YD_AID_WRK_UPDN_GP      AS YD_AID_WRK_UPDN_GP
	      ,B.YD_TO_LOC_DCSN_MTD      AS YD_TO_LOC_DCSN_MTD
	      ,B.YD_TO_LOC_GUIDE         AS YD_TO_LOC_GUIDE
	      ,B.YD_EQP_WRK_SH           AS YD_EQP_WRK_SH   
	      ,B.YD_EQP_WRK_WT           AS YD_EQP_WRK_WT   
	      ,B.YD_EQP_WRK_T            AS YD_EQP_WRK_T    
	      ,B.YD_EQP_WRK_MAX_W        AS YD_EQP_WRK_MAX_W
	      ,B.YD_EQP_WRK_MAX_L        AS YD_EQP_WRK_MAX_L
	      ,B.YD_CRN_SB_CTL_H         AS YD_CRN_SB_CTL_H 
	      ,B.YD_CRN_GRAB_USE_RULE_ID AS YD_CRN_GRAB_USE_RULE_ID         
	      ,B.YS_UP_WO_LOC            AS YS_UP_WO_LOC    
	      ,B.YS_UP_WO_LAYER          AS YS_UP_WO_LAYER  
	      ,B.YS_UP_WO_SEQ_NO         AS YS_UP_WO_SEQ_NO  
	      ,B.YD_UP_WO_LOC_XAXIS      AS YD_UP_WO_LOC_XAXIS              
	      ,B.YD_UP_WO_XAXIS_GAP_MAX  AS YD_UP_WO_XAXIS_GAP_MAX          
	      ,B.YD_UP_WO_XAXIS_GAP_MIN  AS YD_UP_WO_XAXIS_GAP_MIN          
	      ,B.YD_UP_WO_LOC_YAXIS      AS YD_UP_WO_LOC_YAXIS              
	      ,B.YD_UP_WO_LOC_YAXIS1     AS YD_UP_WO_LOC_YAXIS1             
	      ,B.YD_UP_WO_LOC_YAXIS2     AS YD_UP_WO_LOC_YAXIS2             
	      ,B.YD_UP_WO_YAXIS_GAP_MAX  AS YD_UP_WO_YAXIS_GAP_MAX          
	      ,B.YD_UP_WO_YAXIS_GAP_MIN  AS YD_UP_WO_YAXIS_GAP_MIN          
	      ,B.YD_UP_WO_LOC_ZAXIS      AS YD_UP_WO_LOC_ZAXIS              
	      ,B.YD_UP_WO_ZAXIS_GAP_MAX  AS YD_UP_WO_ZAXIS_GAP_MAX          
	      ,B.YD_UP_WO_ZAXIS_GAP_MIN  AS YD_UP_WO_ZAXIS_GAP_MIN          
	      ,B.YS_DN_WO_LOC            AS YS_DN_WO_LOC    
	      ,B.YS_DN_WO_LAYER          AS YS_DN_WO_LAYER  
	      ,B.YS_DN_WO_SEQ_NO         AS YS_DN_WO_SEQ_NO
	      ,B.YD_DN_WO_LOC_XAXIS      AS YD_DN_WO_LOC_XAXIS              
	      ,B.YD_DN_WO_XAXIS_GAP_MAX  AS YD_DN_WO_XAXIS_GAP_MAX          
	      ,B.YD_DN_WO_XAXIS_GAP_MIN  AS YD_DN_WO_XAXIS_GAP_MIN          
	      ,B.YD_DN_WO_LOC_YAXIS      AS YD_DN_WO_LOC_YAXIS              
	      ,B.YD_DN_WO_LOC_YAXIS1     AS YD_DN_WO_LOC_YAXIS1             
	      ,B.YD_DN_WO_LOC_YAXIS2     AS YD_DN_WO_LOC_YAXIS2             
	      ,B.YD_DN_WO_YAXIS_GAP_MAX  AS YD_DN_WO_YAXIS_GAP_MAX          
	      ,B.YD_DN_WO_YAXIS_GAP_MIN  AS YD_DN_WO_YAXIS_GAP_MIN          
	      ,B.YD_DN_WO_LOC_ZAXIS      AS YD_DN_WO_LOC_ZAXIS              
	      ,B.YD_DN_WO_ZAXIS_GAP_MAX  AS YD_DN_WO_ZAXIS_GAP_MAX          
	      ,B.YD_DN_WO_ZAXIS_GAP_MIN  AS YD_DN_WO_ZAXIS_GAP_MIN          
	      ,B.YS_UP_WR_LOC            AS YS_UP_WR_LOC    
	      ,B.YS_UP_WR_LAYER          AS YS_UP_WR_LAYER  
	      ,B.YS_UP_WR_SEQ_NO         AS YS_UP_WR_SEQ_NO
	      ,B.YD_UP_WRK_ACT_GP        AS YD_UP_WRK_ACT_GP
	      ,B.YD_UP_WR_XAXIS          AS YD_UP_WR_XAXIS  
	      ,B.YD_UP_WR_YAXIS          AS YD_UP_WR_YAXIS  
	      ,B.YD_UP_WR_YAXIS1         AS YD_UP_WR_YAXIS1 
	      ,B.YD_UP_WR_YAXIS2         AS YD_UP_WR_YAXIS2 
	      ,B.YD_UP_WR_ZAXIS          AS YD_UP_WR_ZAXIS  
	      ,B.YS_DN_WR_LOC            AS YS_DN_WR_LOC    
	      ,B.YS_DN_WR_LAYER          AS YS_DN_WR_LAYER  
	      ,B.YS_DN_WR_SEQ_NO         AS YS_DN_WR_SEQ_NO
	      ,B.YD_DN_WRK_ACT_GP        AS YD_DN_WRK_ACT_GP
	      ,B.YD_DN_WR_XAXIS          AS YD_DN_WR_XAXIS  
	      ,B.YD_DN_WR_YAXIS          AS YD_DN_WR_YAXIS  
	      ,B.YD_DN_WR_YAXIS1         AS YD_DN_WR_YAXIS1 
	      ,B.YD_DN_WR_YAXIS2         AS YD_DN_WR_YAXIS2 
	      ,B.YD_DN_WR_ZAXIS          AS YD_DN_WR_ZAXIS   
	      ,(SELECT YD_STKBED_USG_CD FROM TB_YS_STKCOL WHERE YS_STK_COL_GP = SUBSTR(B.YS_UP_WO_LOC,1,6)) AS BED_USG_CD
		  
	  FROM TB_YS_EQP    A     
	      ,TB_YS_CRNSCH B
	      ,PARAM        P
	      
	 WHERE B.YD_EQP_ID   = A.YD_EQP_ID
	   AND B.YD_WBOOK_ID = P.V_YD_WBOOK_ID
	   AND B.YD_EQP_ID   = P.V_YD_EQP_ID
	   AND B.YD_GP       = 'G'
	   AND B.YD_BAY_GP   = 'D'
	   AND A.YD_GP       = 'G'
	   AND A.YD_BAY_GP   = 'D'
	   AND (B.DEL_YN IS NULL OR B.DEL_YN <> 'Y')
	   
	 ORDER BY B.YD_CRN_SCH_ID
	 * </pre>
	 */
	public final static String getYdCrnschByEqpIdandWBookId = "com.inisteel.cim.ys.sbr.dao.getYdCrnschByEqpIdandWBookId";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getYdCrnwrkmtlBySchId
	-- 크레인스케줄 작업재료 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_CRN_SCH_ID AS V_YD_CRN_SCH_ID
	      FROM DUAL
	)
	SELECT A.SSTL_NO                                                                       AS SSTL_NO
	      ,A.YS_STK_LYR_NO                                                                 AS YS_STK_LYR_NO
	      ,A.YD_CRN_SCH_ID                                                                 AS YD_CRN_SCH_ID
	      ,A.REGISTER                                                                      AS REGISTER
	      ,A.REG_DDTT                                                                      AS REG_DDTT
	      ,A.MOD_DDTT                                                                      AS MOD_DDTT
	      ,A.MODIFIER                                                                      AS MODIFIER
	      ,A.DEL_YN                                                                        AS DEL_YN
	      ,A.YD_AID_WRK_YN                                                                 AS YD_AID_WRK_YN
	      ,A.HCR_GP                                                                        AS HCR_GP
	      ,A.STL_PROG_CD                                                                   AS STL_PROG_CD
	      ,A.YS_ROUTE_GP                                                                   AS YS_ROUTE_GP
	      ,B.ITEMNAME_CD                                                                   AS ITEMNAME_CD          --품명코드
	      ,B.YD_MTL_W                                                                      AS YD_MTL_W             --야드재료폭
	      ,B.YD_MTL_WT                                                                     AS YD_MTL_WT            --야드재료중량
	      ,B.YD_MTL_T                                                                      AS YD_MTL_T             --야드재료두께
	      ,B.YD_MTL_L                                                                      AS YD_MTL_L             --야드재료길이
	      ,B.YS_MTL_ITEM                                                                   AS YS_MTL_ITEM          --야드재료품목
	      ,B.YD_STK_LOT_TP                                                                 AS YD_STK_LOT_TP        --야드산적LotType 
	      ,B.YD_STK_LOT_CD                                                                 AS YD_STK_LOT_CD        --야드산적Lot코드 
	      ,B.REFUR_CHG_PLN_SERNO                                                           AS REFUR_CHG_PLN_SERNO  --가열로장입예정일련번호 
	      ,B.BLOOM_CL_MTD                                                                  AS BLOOM_CL_MTD         --BLOOM냉각방법(A:서냉)
	      ,B.HEAT_NO                                                                       AS HEAT_NO              --Heat번호
	      ,NVL(B.BUNDLE_T, B.YD_MTL_T )                                                    AS BUNDLE_T             --번들두께
	      ,SUM(B.YD_MTL_WT)       OVER (ORDER BY A.YS_STK_LYR_NO DESC)                     AS SUM_MTL_WT           --야드재료중량 합계
	      ,SUM(B.YD_MTL_T)        OVER (ORDER BY A.YS_STK_LYR_NO DESC)                     AS SUM_MTL_T            --야드재료두께 합계
	      ,MAX(B.YD_MTL_W)        OVER (ORDER BY A.YS_STK_LYR_NO DESC)                     AS MAX_MTL_W            --야드재료폭   최대값
	      ,MAX(B.YD_MTL_L)        OVER (ORDER BY A.YS_STK_LYR_NO DESC)                     AS MAX_MTL_L            --야드재료길이 최대값
	      ,ROUND(AVG(B.YD_MTL_L)  OVER (ORDER BY A.YS_STK_LYR_NO DESC))                    AS AVG_MTL_L            --야드재료길이 평균값
	      ,COUNT(A.SSTL_NO)       OVER (ORDER BY A.YS_STK_LYR_NO DESC)                     AS SH_CNT               --야드재료매수
	      ,NVL(MIN(B.YD_CHG_NO)   OVER (ORDER BY A.YS_STK_LYR_NO DESC),0)                  AS YD_CHG_NO            --야드장입순위
	      ,(SELECT YS_UP_WO_LOC   FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YS_UP_WO_LOC
	      ,(SELECT YS_UP_WO_LAYER FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YS_UP_WO_LAYER
	      ,(SELECT YD_WBOOK_ID    FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_WBOOK_ID
	      ,(SELECT YD_EQP_ID      FROM TB_YS_CRNSCH WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_EQP_ID
	      ,A.YD_TO_LOC_DCSN_MTD                                                            AS YD_TO_LOC_DCSN_MTD
	      ,B.CUST_CD                                                                       AS CUST_CD
	      ,B.DETAIL_ARR_CD                                                                 AS DETAIL_ARR_CD
	      ,MAX(CASE WHEN B.YD_MTL_WT <= 1500 THEN 'N' ELSE 'N' END) OVER()                 AS TY_BED_YN
	  FROM TB_YS_CRNWRKMTL A
	      ,TB_YS_STOCK     B
	      ,PARAM           P
	 WHERE A.SSTL_NO       = B.SSTL_NO                                                      
	   AND A.YD_CRN_SCH_ID = P.V_YD_CRN_SCH_ID
	   AND A.DEL_YN = 'N'                                    
	   AND B.DEL_YN = 'N'                                   
	 ORDER BY A.YS_STK_LYR_NO
	 * </pre>
	 */
	public final static String getYdCrnwrkmtlBySchId = "com.inisteel.cim.ys.sbr.dao.getYdCrnwrkmtlBySchId";

	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getCrnWrkMgtSCSch_Car
	--크레인작업관리 > 취소할 차량상차 크레인스케줄 조회 
	
	WITH PARAM AS 
	(
	    SELECT :V_YS_STK_COL_GP AS V_YS_STK_COL_GP  -- 차량상차위치
	      FROM DUAL
	)
	SELECT CS.YD_WBOOK_ID
	 	 , CS.YD_CRN_SCH_ID
	     , CS.YD_WRK_PROG_STAT  --야드작업진행상태: [C:스케쥴명령취소][S:스케쥴작성중][W:명령선택대기][1:권상지시][2:권상완료][3:권하지시][4:권하완료]
	     , CASE WHEN EQ.YD_EQP_STAT IN ('B', 'W') OR EQ.YD_EQP_WRK_MODE != '1' THEN 'N'  -- [B:고장],[W:대기(Wait)],[1:Online Mode] 
	            ELSE 'Y' 
	        END AS EQP_UPD_YN   --설비상태수정여부
	     , CS.YD_EQP_ID
	     , 'W' AS YD_EQP_STAT
	  FROM TB_YS_CRNSCH CS
	     , TB_YS_EQP    EQ
	     , PARAM        PA
	 WHERE CS.YD_GP     = SUBSTR(PA.V_YS_STK_COL_GP,1,1)
	   AND CS.YD_BAY_GP = SUBSTR(PA.V_YS_STK_COL_GP,2,1)
	   AND (CS.YD_SCH_CD LIKE PA.V_YS_STK_COL_GP||'U%' OR CS.YD_SCH_CD LIKE SUBSTR(PA.V_YS_STK_COL_GP,1,4)||'01U%')  --차량출고
	   AND CS.DEL_YN    = 'N'
	   AND CS.YD_EQP_ID = EQ.YD_EQP_ID(+)
	 ORDER BY CS.YD_CRN_SCH_ID
	 * </pre>
	 */
	public final static String getCrnWrkMgtSCSch_Car = "com.inisteel.cim.ys.sbr.dao.getCrnWrkMgtSCSch_Car";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getYdStock
	-- 저장품정보 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_SSTL_NO   AS V_SSTL_NO
	      FROM DUAL
	)
	SELECT SSTL_NO                 --재료번호
	     , PTOP_PLNT_GP             --조업공장구분
	     , YS_MTL_ITEM              --특수강야드재료품목
	     , ITEMNAME_CD              --품명코드
	     , YD_MTL_STAT              --야드재료상태
	     , STL_PROG_CD              --재료진도코드
	     , ORD_YEOJAE_GP            --주문여재구분
	     , ORD_NO                   --주문번호
	     , ORD_DTL                  --주문행번
	     , YD_STK_LOT_TP            --야드산적LOTTYPE
	     , FRTOMOVE_ORD_DATE        --이송지시일자
	     , FRTOMOVE_PLANT_GP        --이송공장구분
	     , URGENT_FRTOMOVE_WORD_GP  --긴급이송작업지시구분
	     , STL_APPEAR_GP            --재료외형구분
	     , PLNT_PROC_CD             --공장공정코드
	     , YD_MTL_T                 --야드재료두께
	     , YD_MTL_W                 --야드재료폭
	     , YD_MTL_L                 --야드재료길이
	     , YD_MTL_WT                --야드재료중량
	     , YD_MTL_OUTDIA            --야드재료외경
	     , YD_MTL_W_GP              --야드재료폭구분
	     , YD_MTL_T_GP              --야드재료두께구분
	     , YD_MTL_L_GP              --야드재료길이구분
	     , YS_OUTDIA_GRP_GP         --특수강야드외경군구분
	     , HCR_GP                   --HCR구분
	     , ROLL_UNIT_GP             --ROLL단위구분
	     , ROLL_UNIT_NAME           --ROLL단위명
	     , REFUR_CHG_LOT_NO         --가열로장입LOT번호
	     , REFUR_CHG_PLN_SERNO      --가열로장입예정일련번호
	     , ORD_GP                   --수주구분
	     , CUST_CD                  --고객코드
	     , DEST_CD                  --목적지코드
	     , DEMANDER_CD              --수요가코드
	     , DEST_TEL_NO              --목적지전화번호
	     , TRANS_ORD_DATE           --운송지시일자
	     , TRANS_ORD_SEQNO          --운송지시순번
	     , HOLD_TREAT_GP            --보류처리구분
	     , CAR_NO                   --차량번호
	     , CARD_NO                  --카드번호
	     , YS_STK_COL_GP            --특수강야드적치열구분
	     , YS_STK_BED_NO            --특수강야드적치BED번호
	     , YS_STK_LYR_NO            --특수강야드적치단번호
	     , YS_STK_SEQ_NO            --특수강야드적치SEQ번호
	     , STLKIND_CD               --강종코드
	     , SPEC_ABBSYM              --규격약호
	     , RENTPROC_CD              --임가공사코드
	     , SPOS_WLOC_CD             --발지개소코드
	     , ARR_WLOC_CD              --착지개소코드
	     , APPEAR_GRADE             --외관종합판정등급
	     , OVERALL_STAMP_GRADE      --종합판정등급
	     , GOODS_GRADE              --제품등급
	     , YD_CAR_UPP_LOC_CD        --야드차상위치코드
	     , YS_STR_LOC               --특수강야드저장위치
	     , YD_RCPT_DATE             --야드입고일자
	     , DIST_DUE_DATE            --출하기한일
	     , DIST_SHIPASSIGN_GP       --출하배선지시구분
	     , EXPORT_SHIP_SET_NO       --수출재배선번호
	     , SHIPASSIGN_WORD_DATE     --배선작업지시일자
	     , SHIPASSIGN_WORD_SEQNO    --배선작업지시순번
	     , SHIP_CD                  --선박코드
	     , SHIP_NAME                --선박명
	     , BERTH_NO                 --선석번호
	     , SAILNO                   --선박항차
	     , SNDBK_RSN_CD             --반송원인코드
	     , SNDBK_GP                 --반송요청구분
	     , SNDBK_REGISTER           --반송요청자
	     , SNDBK_REG_DDTT           --반송요청일자
	     , SNDBK_GP_ETC             --반납구분기타
	     , DELIVER_TERM_CD          --인도조건코드
	     , PRE_AR_STAT_CD           --보관매출상태코드
	     , CAR_LOTID                --차량LOTID
	     , CAR_LOTID_REG_DDTT       --차량LOTID등록일자
	     , DETAIL_ARR_CD            --상세착지코드
	     , URGENT_DIST_YN           --긴급출하유무
	     , YD_RCPT_ARR_DT           --입고존도착일시
	     , YD_RCPT_LEV_DT           --입고존출발일시
	     , YD_RCPT_PLN_STR_LOC      --야드입고예정저장위치
	     , YD_RCPT_STR_LOC_RSN      --입고동위치변경사유
	     , REGISTER                 --등록자
	     , REG_DDTT                 --등록일시
	     , MODIFIER                 --수정자
	     , MOD_DDTT                 --수정일시
	     , DEL_YN                   --삭제유무
	     , YD_CHG_NO                --야드장입순위
	     , SPST_FRTOMOVE_GP         --특수강이송구분
	     , HEAT_NO
	     , NVL(BUNDLE_T, YD_MTL_T) AS  BUNDLE_T
	     , DECODE(( --선재용
	               SELECT COUNT(1)            --0 보다 크면 스크랩     
	                 FROM TB_SB_B_PROCINSPWR  --SB_공정검사실적
	                WHERE MATL_NO = B.SSTL_NO --번들 번호 
	                  AND (
	                           SUBSTR(MID_INSPECT_DEFECT_CD1,0,3) IN ('B09', 'B16', 'B17', 'B18' )  --길이미달:'B09', 권취불량:'B18', 외경돌출:'B17', 직각권취:'B16'
	                        OR SUBSTR(MID_INSPECT_DEFECT_CD2,0,3) IN ('B09', 'B16', 'B17', 'B18' )
	                        OR SUBSTR(MID_INSPECT_DEFECT_CD3,0,3) IN ('B09', 'B16', 'B17', 'B18' )
	                        OR SUBSTR(MID_INSPECT_DEFECT_CD4,0,3) IN ('B09', 'B16', 'B17', 'B18' )
	                        OR SUBSTR(MID_INSPECT_DEFECT_CD5,0,3) IN ('B09', 'B16', 'B17', 'B18' )
	                      )
	              ),'0','N','Y')AS WR_SCRAP
	     , (SELECT CURR_PROG_CD FROM TB_PB_BUNDLECOMM WHERE BNDL_NO = B.SSTL_NO) AS BNDL_PROG_CD
	 FROM TB_YS_STOCK B
	    , PARAM       P
	WHERE SSTL_NO = P.V_SSTL_NO
	 * </pre>
	 */
	public final static String getYdStock = "com.inisteel.cim.ys.sbr.dao.getYdStock";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getYdStklyrSTLNO
	-- 적재위치정보 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_SSTL_NO             AS V_SSTL_NO
	          ,:V_YD_STK_LYR_MTL_STAT AS V_YD_STK_LYR_MTL_STAT
	      FROM DUAL
	)
	SELECT YS_STK_COL_GP            AS YS_STK_COL_GP
	      ,YS_STK_BED_NO            AS YS_STK_BED_NO
	      ,YS_STK_LYR_NO            AS YS_STK_LYR_NO
	      ,YS_STK_SEQ_NO            AS YS_STK_SEQ_NO
	      ,REGISTER                 AS REGISTER
	      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')  AS REG_DDTT
	      ,MODIFIER                 AS MODIFIER
	      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')  AS MOD_DDTT
	      ,DEL_YN                   AS DEL_YN
	      ,SSTL_NO                  AS SSTL_NO
	      ,YD_STK_LYR_ACT_STAT      AS YD_STK_LYR_ACT_STAT
	      ,YD_STK_LYR_MTL_STAT      AS YD_STK_LYR_MTL_STAT
	      ,YD_STK_LYR_XAXIS         AS YD_STK_LYR_XAXIS
	      ,YD_STK_LYR_YAXIS         AS YD_STK_LYR_YAXIS
	      ,YD_STK_LYR_ZAXIS         AS YD_STK_LYR_ZAXIS
	  FROM TB_YS_STKLYR S
	      ,PARAM        P
	 WHERE S.SSTL_NO                       = P.V_SSTL_NO
	   AND NVL(S.YD_STK_LYR_MTL_STAT, '*') = P.V_YD_STK_LYR_MTL_STAT  --야드적치단재료상태: [C:적치 중][D:권하대기][E:적치가능][U:권상대기][X:적치불가]
	   AND S.DEL_YN                        = 'N'
	 * </pre>
	 */
	public final static String getYdStklyrSTLNO = "com.inisteel.cim.ys.sbr.dao.getYdStklyrSTLNO";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getToLocCol
	-- 동일한 사양의 적치가능한 "열"(+"Max-BED/Max-단") 조회
	
		WITH PARAM AS (
		    SELECT :V_YD_TO_LOC_DCSN_MTD AS V_YD_TO_LOC_DCSN_MTD  --TO위치결정방법: [M:주작업이적][W:보조작업이적]
		         , :V_YD_SCH_CD          AS V_YD_SCH_CD           --스케줄코드
		         , :V_HEAT_NO            AS V_HEAT_NO             --Heat No
		         , :V_YD_MTL_T           AS V_YD_MTL_T            --재료두께
		         , :V_YD_MTL_L           AS V_YD_MTL_L            --재료길이
		         , :V_SPEC_ABBSYM        AS V_SPEC_ABBSYM         --규격약호 (강종)
		         , :V_YS_STK_COL_GP      AS V_YS_STK_COL_GP       --적치열
		      FROM DUAL
		),
		TEMP_M_TO_LOC_ZONE AS (
		    --"스케줄코드, 강종, YD_TO_LOC_GUIDE" 구분으로 주작업 TO위치검색 구역
			SELECT M_TO_LOC_ZONE
			     , MAX(M_TO_LOC_LEN) AS M_TO_LOC_LEN
			  FROM (
					    SELECT REGEXP_SUBSTR(M_TO_LOC_ZONE, '[^,]+', 1, LEVEL)         AS M_TO_LOC_ZONE
					         , LENGTH(REGEXP_SUBSTR(M_TO_LOC_ZONE, '[^,]+', 1, LEVEL)) AS M_TO_LOC_LEN
					      FROM (
					                SELECT B.YS_STK_COL_GP  AS M_TO_LOC_ZONE
					                  FROM TB_YS_LOCSRCHRNG A
					                     , TB_YS_LOCSRCHBED B
					                     , PARAM            P
					                 WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
					                   AND A.YD_SCH_CD     = P.V_YD_SCH_CD  --스케줄코드
					                   AND A.YD_STR_GTR_CD = 'M'            --주작업
					                   AND A.YS_ROUTE_GP   = (CASE WHEN P.V_YD_SCH_CD IN ('GDYD01MM') AND SUBSTR(P.V_YS_STK_COL_GP,3,2) <> 'TY' THEN 'Y' 
					                                               WHEN P.V_YD_SCH_CD IN ('GDYD01MM') AND SUBSTR(P.V_YS_STK_COL_GP,3,2)  = 'TY' THEN 'T'
					                                               ELSE A.YD_STR_GTR_CD END)
					           )
					   CONNECT BY REGEXP_SUBSTR(M_TO_LOC_ZONE, '[^,]+', 1, LEVEL) IS NOT NULL
				   )
			 GROUP BY M_TO_LOC_ZONE
		),
		TEMP_W_TO_LOC_ZONE AS (
		    --"스케줄코드, 강종, YD_TO_LOC_GUIDE" 구분으로 보조작업 TO위치검색 구역
		    SELECT REGEXP_SUBSTR(W_TO_LOC_ZONE, '[^,]+', 1, LEVEL) AS W_TO_LOC_ZONE
		      FROM (
		                SELECT B.YS_STK_COL_GP  AS W_TO_LOC_ZONE
		                  FROM TB_YS_LOCSRCHRNG A
		                     , TB_YS_LOCSRCHBED B
		                     , PARAM            P
		                 WHERE A.YD_LOC_SRCH_RNG_REG_SNO = B.YD_LOC_SRCH_RNG_REG_SNO
		                   AND A.YD_SCH_CD     = P.V_YD_SCH_CD                                                                      --스케줄코드
		                   AND A.YD_STR_GTR_CD = 'W'                                                                                --주작업
		                   AND A.YS_ROUTE_GP   = (CASE WHEN P.V_YD_SCH_CD IN ('GDYD01MM','GDTR11UM','GDTR21UM','GDTC04UM','GDTC05UM','GDTC06UM') AND SUBSTR(P.V_YS_STK_COL_GP,3,2) <> 'TY' THEN 'Y'
		                                               WHEN P.V_YD_SCH_CD IN ('GDYD01MM','GDTR11UM','GDTR21UM','GDTC04UM','GDTC05UM','GDTC06UM') AND SUBSTR(P.V_YS_STK_COL_GP,3,2)  = 'TY' THEN 'T' 
		                                               ELSE A.YD_STR_GTR_CD END)
		           )
		   CONNECT BY REGEXP_SUBSTR(W_TO_LOC_ZONE, '[^,]+', 1, LEVEL) IS NOT NULL
		)
		-- ("적치 가능한 열" AND "동일한 사양의 열") + "공BED 인 열"
		SELECT A.SEQ_NUM 
		     , A.YS_STK_COL_GP
		     , A.YS_STK_BED_NO
		     , A.YS_STK_LYR_NO
		     , A.YS_STK_SEQ_NO
		     , A.MAX_SSTL_NO
		     , A.MTL_STAT_UP_CNT  --"권상대기"중인 작업재료
		     , A.HEAT_NO
		     , A.STLKIND_CD
		     , A.YD_MTL_T
		     , A.TO_LOC_POINT     --TO위치를 검색한 쿼리
		       
		  FROM (
		            -- # "적치 가능한 열" && "동일한 사양의 열"
		            SELECT F.SEQ_NUM
		            
		                 , A.YS_STK_COL_GP
		                 , A.YS_STK_BED_NO
		                 , A.YS_STK_LYR_NO 
		                 , A.YS_STK_SEQ_NO 
		                 , A.MAX_SSTL_NO
		                 , A.MTL_STAT_UP_CNT
		                 
		                 , B.HEAT_NO
		                 , B.STLKIND_CD
		                 , B.YD_MTL_T
		                 , B.YD_MTL_L
		                   
		                 , F.SEQ_9_SUM
		                 , F.SEQ_8_SUM
		                 , F.SEQ_7_SUM
		                 , F.SEQ_6_SUM
		                 
		                 , NVL(A.TO_LOC_POINT, F.TO_LOC_POINT) AS TO_LOC_POINT
		                 
		              FROM (    --적치 가능한 "열"(+"Max-BED/Max-단")
		                        SELECT YS_STK_COL_GP
		                             , YS_STK_BED_NO
		                             , YS_STK_LYR_NO 
		                             , YS_STK_SEQ_NO 
		                             , MAX_SSTL_NO
		                             , MTL_STAT_UP_CNT -- 권상예약 재료매수
		                             , '적치 가능한 열' AS TO_LOC_POINT
		                          FROM 
		                               (
		                                    --최상단 "권상대기"중인 작업재료가 없는 위치정보 ("열별 > 최상단 > Max-BED번호 > 마지막SEQ")
		                                    SELECT A1.YS_STK_COL_GP
		                                         , A1.YS_STK_BED_NO
		                                         , A1.YS_STK_LYR_NO
		                                         , A1.YS_STK_SEQ_NO
		                                         , A1.SSTL_NO       AS MAX_SSTL_NO
		                                         , A1.YD_STK_LYR_MTL_STAT
		                                         , ROW_NUMBER() OVER ( PARTITION BY A1.YS_STK_COL_GP ORDER BY A1.YS_STK_COL_GP, A1.YS_STK_LYR_NO DESC, A1.YS_STK_BED_NO DESC, A1.YS_STK_SEQ_NO DESC) AS CC
		                                         , SUM(DECODE(A1.YD_STK_LYR_MTL_STAT,'U',1,0)) OVER (PARTITION BY A1.YS_STK_COL_GP ) AS MTL_STAT_UP_CNT  --권상대기 재료매수: [U:권상대기]
		                                      FROM TB_YS_STKLYR A1
		                                         , TB_YS_STKCOL B1
		                                         , PARAM        C1
		                                     WHERE A1.YS_STK_COL_GP = B1.YS_STK_COL_GP
		                                       AND A1.SSTL_NO > ' '
		                                       AND A1.DEL_YN  = 'N'
		                                       AND A1.YS_STK_COL_GP LIKE SUBSTR(C1.V_YS_STK_COL_GP,1,2)||'%'  --"[2]야드/동" 제한
		                                     ORDER BY A1.YS_STK_COL_GP, A1.YS_STK_LYR_NO DESC, A1.YS_STK_BED_NO DESC, A1.YS_STK_SEQ_NO DESC
		                                )
		                         WHERE CC = 1               --"열별 > 최상단 > Max-BED번호 > 마지막SEQ"
		                           AND MTL_STAT_UP_CNT = 0  --권상대기 재료가 없는 "열"
		                   ) A
		                 , TB_YS_STOCK  B
		                 , TB_YS_STKBED D
		                 , TB_YS_STKCOL E
		                 , (
		                     SELECT *
		                       FROM (
		                                 -- 동일한 사양의 "열" : Heat No > 길이 > 두께(각) > 강종
		                                 SELECT A1.YS_STK_COL_GP
		                                      , MAX( CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T AND B1.SPEC_ABBSYM = C1.V_SPEC_ABBSYM  THEN 9  --일치 항목: Heat No & 길이 & 두께(각) & 강종
	                                                      WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T                                        THEN 8  --일치 항목: Heat No & 길이 & 두께(각)
	                                                      WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L                                                                        THEN 7  --일치 항목: Heat No & 길이
	                                                      WHEN B1.HEAT_NO = C1.V_HEAT_NO                                                                                                        THEN 6  --일치 항목: Heat No
		                                                  ELSE 1 END)  AS SEQ_NUM
		                                      , SUM((CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T AND B1.SPEC_ABBSYM = C1.V_SPEC_ABBSYM  THEN 1  --9점 짜리 재료 개수
		                                                  ELSE 0 END)) AS SEQ_9_SUM
		                                      , SUM((CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T                                        THEN 1  --8점 짜리 재료 개수
		                                                  ELSE 0 END)) AS SEQ_8_SUM
		                                      , SUM((CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L                                                                        THEN 1  --7점 짜리 재료 개수
		                                                  ELSE 0 END)) AS SEQ_7_SUM
		                                      , SUM((CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO                                                                                                        THEN 1  --6점 짜리 재료 개수
		                                                  ELSE 0 END)) AS SEQ_6_SUM
		                                                 
		                                      , '동일한 사양의 열' AS TO_LOC_POINT
		                                      
	--	                                      , A1.SSTL_NO
	--	                                      , B1.YD_MTL_L, B1.YD_MTL_T, B1.HEAT_NO, B1.SPEC_ABBSYM, C1.V_SPEC_ABBSYM
	--	                                      , CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T AND B1.SPEC_ABBSYM = C1.V_SPEC_ABBSYM  THEN 9  --Heat No & 길이 & 두께(각) & 강종
	--                                               WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T                                        THEN 8  --Heat No & 길이 & 두께(각)
	--                                               WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L                                                                        THEN 7  --Heat No & 길이
	--                                               WHEN B1.HEAT_NO = C1.V_HEAT_NO                                                                                                        THEN 6  --Heat No
	--	                                             ELSE 1 END  AS SEQ_NUM
		                                                  
		                                  FROM TB_YS_STKLYR     A1
		                                     , TB_YS_STOCK      B1
		                                     , PARAM            C1
		                                 WHERE A1.SSTL_NO = B1.SSTL_NO
		                                   AND A1.YD_STK_LYR_ACT_STAT = 'E'                                  --야드적치단활성상태: [E:적치가능]
		                                   AND A1.YS_STK_COL_GP LIKE SUBSTR(C1.V_YS_STK_COL_GP,1,2)||'%'     --야드구분: [GE:특수강 대형옥내/빌렛정정]
		                                   AND A1.DEL_YN  = 'N'
		                                   AND A1.YS_STK_LYR_NO = (SELECT NVL(MAX(YS_STK_LYR_NO),'01')       --각 열의 최상단
		                                                             FROM TB_YS_STKLYR 
		                                                            WHERE YS_STK_COL_GP = A1.YS_STK_COL_GP
		                                                              AND SSTL_NO IS NOT NULL
		                                                              AND DEL_YN = 'N')
		                                 GROUP BY A1.YS_STK_COL_GP
		                                HAVING SUM(DECODE(A1.YD_STK_LYR_MTL_STAT,'U',1,0)) = 0               --[U:권상대기]
		                            )
		                      WHERE SEQ_NUM > 1
		                      
		                   ) F
		             WHERE 1=1
		               AND A.MAX_SSTL_NO          = B.SSTL_NO
		               AND A.YS_STK_COL_GP        = D.YS_STK_COL_GP
		               AND A.YS_STK_BED_NO        = D.YS_STK_BED_NO
		               AND NVL(D.YD_STK_BED_ACT_STAT,'*') = 'L'        --야드적치Bed활성상태: [L:적치 가능]
		               AND D.YS_STK_COL_GP        = E.YS_STK_COL_GP
		               AND NVL(E.YD_STK_COL_ACT_STAT,'*') = 'L'        --야드적치열활성상태 : [L:적치 가능]
		               AND A.YS_STK_COL_GP        = F.YS_STK_COL_GP
		            
		            UNION ALL
		            
		            -- # "공BED 인 열"
		            SELECT 1                  AS SEQ_NUM
		                 , A.YS_STK_COL_GP    AS YS_STK_COL_GP
		                 , ''                 AS YS_STK_BED_NO
		                 , ''                 AS YS_STK_LYR_NO 
		                 , ''                 AS YS_STK_SEQ_NO 
		                 , ''                 AS SSTL_NO
		                 , 0                  AS MTL_STAT_UP_CNT 
		                 
		                 , ''                 AS HEAT_NO
		                 , ''                 AS STLKIND_CD
		                 , 0                  AS YD_MTL_T
		                 , 0                  AS YD_MTL_L
		                 
		                 , 0                  AS SEQ_9_SUM      
		                 , 0                  AS SEQ_8_SUM 
		                 , 0                  AS SEQ_7_SUM 
		                 , 0                  AS SEQ_6_SUM 
		                 
		                 ,'공BED 인 열'       AS TO_LOC_POINT
		                 
		              FROM TB_YS_STKLYR A
		                 , (
		                        --적치열별 재료수 조회
		                        SELECT COUNT(A1.SSTL_NO) AS SUM_CNT
		                             , A1.YS_STK_COL_GP
		                          FROM TB_YS_STKLYR A1
		                             , TB_YS_STKCOL B1
		                             , PARAM        C1
		                         WHERE A1.YS_STK_COL_GP = B1.YS_STK_COL_GP 
		                           AND A1.YS_STK_COL_GP LIKE SUBSTR(C1.V_YS_STK_COL_GP,1,2)||'%'  --"[2]야드/동" 제한
		                           AND A1.DEL_YN = 'N'
		                           AND B1.DEL_YN = 'N'
		                         GROUP BY A1.YS_STK_COL_GP
		                   ) B
		                 , TB_YS_STKBED E
		                 , TB_YS_STKCOL F
		             WHERE A.YS_STK_COL_GP = B.YS_STK_COL_GP
		               AND B.SUM_CNT       = 0
		               AND A.YS_STK_COL_GP = E.YS_STK_COL_GP
		               AND A.YS_STK_BED_NO = E.YS_STK_BED_NO
		               AND NVL(E.YD_STK_BED_ACT_STAT,'*') = 'L'  --야드적치Bed활성상태: [L:적치 가능]
		               AND E.YS_STK_COL_GP = F.YS_STK_COL_GP
		               AND NVL(F.YD_STK_COL_ACT_STAT,'*') = 'L'  --야드적치열활성상태 : [L:적치 가능]
		             GROUP BY A.YS_STK_COL_GP
		       ) A
		     , PARAM C
		 WHERE 1 = 1
		 
		   AND (
		        CASE      --"주작업 이적"이고, "주작업 TO위치검색 구역" 내 존재여부 하면...
		             WHEN C.V_YD_TO_LOC_DCSN_MTD = 'M' AND A.YS_STK_COL_GP IN (SELECT M_TO_LOC_ZONE FROM TEMP_M_TO_LOC_ZONE) THEN 1
		                  --"보조작업 TO위치검색 구역" 내 존재여부 확인
		             WHEN C.V_YD_TO_LOC_DCSN_MTD = 'W' AND A.YS_STK_COL_GP IN (SELECT W_TO_LOC_ZONE FROM TEMP_W_TO_LOC_ZONE) THEN 1 
		             ELSE 0 END
		       ) = 1
		   
		   AND A.YS_STK_COL_GP <> C.V_YS_STK_COL_GP  -- 자신 BED(적치대) 및 열(#BED) 제외
		
		 ORDER BY SEQ_NUM DESC, SEQ_9_SUM DESC, SEQ_8_SUM DESC, SEQ_7_SUM DESC, SEQ_6_SUM DESC, YS_STK_COL_GP, YS_STK_BED_NO
	 * </pre>
	 */
	public final static String getToLocCol = "com.inisteel.cim.ys.sbr.dao.getToLocCol";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getToLocColByLocGuide
	-- 동일한 사양의 적치가능한 "열"(+"Max-BED/Max-단") 조회
	
	WITH PARAM AS (
	    SELECT :V_YD_TO_LOC_DCSN_MTD AS V_YD_TO_LOC_DCSN_MTD  --TO위치결정방법: [M:주작업이적][W:보조작업이적]
	         , :V_YD_SCH_CD          AS V_YD_SCH_CD           --스케줄코드
	         , :V_YD_TO_LOC_GUIDE    AS V_YD_TO_LOC_GUIDE     --TO위치가이드 (주작업 이적시)
	         , :V_HEAT_NO            AS V_HEAT_NO             --Heat No
	         , :V_YD_MTL_T           AS V_YD_MTL_T            --재료두께
	         , :V_YD_MTL_L           AS V_YD_MTL_L            --재료길이
	         , :V_SPEC_ABBSYM        AS V_SPEC_ABBSYM         --규격약호 (강종)
	         , :V_YS_STK_COL_GP      AS V_YS_STK_COL_GP       --적치열
	      FROM DUAL
	)
	-- ("적치 가능한 열" AND "동일한 사양의 열") + "공BED 인 열"
	SELECT A.SEQ_NUM 
	     , A.YS_STK_COL_GP
	     , A.YS_STK_BED_NO
	     , A.YS_STK_LYR_NO
	     , A.YS_STK_SEQ_NO
	     , A.MAX_SSTL_NO
	     , A.MTL_STAT_UP_CNT  --"권상대기"중인 작업재료
	     , A.HEAT_NO
	     , A.STLKIND_CD
	     , A.YD_MTL_T
	     , A.TO_LOC_POINT     --TO위치를 검색한 쿼리
	       
	  FROM (
	            -- # "적치 가능한 열" && "동일한 사양의 열"
	            SELECT F.SEQ_NUM
	            
	                 , A.YS_STK_COL_GP
	                 , A.YS_STK_BED_NO
	                 , A.YS_STK_LYR_NO 
	                 , A.YS_STK_SEQ_NO 
	                 , A.MAX_SSTL_NO
	                 , A.MTL_STAT_UP_CNT
	                 
	                 , B.HEAT_NO
	                 , B.STLKIND_CD
	                 , B.YD_MTL_T
	                 , B.YD_MTL_L
	                   
	                 , F.SEQ_9_SUM
	                 , F.SEQ_8_SUM
	                 , F.SEQ_7_SUM
	                 , F.SEQ_6_SUM
	                 
	                 , NVL(A.TO_LOC_POINT, F.TO_LOC_POINT) AS TO_LOC_POINT
	                 
	              FROM (    --적치 가능한 "열"(+"Max-BED/Max-단")
	                        SELECT YS_STK_COL_GP
	                             , YS_STK_BED_NO
	                             , YS_STK_LYR_NO 
	                             , YS_STK_SEQ_NO 
	                             , MAX_SSTL_NO
	                             , MTL_STAT_UP_CNT -- 권상예약 재료매수
	                             , '적치 가능한 열' AS TO_LOC_POINT
	                          FROM 
	                               (
	                                    --최상단 "권상대기"중인 작업재료가 없는 위치정보 ("열별 > 최상단 > Max-BED번호 > 마지막SEQ")
	                                    SELECT A1.YS_STK_COL_GP
	                                         , A1.YS_STK_BED_NO
	                                         , A1.YS_STK_LYR_NO
	                                         , A1.YS_STK_SEQ_NO
	                                         , A1.SSTL_NO       AS MAX_SSTL_NO
	                                         , A1.YD_STK_LYR_MTL_STAT
	                                         , ROW_NUMBER() OVER ( PARTITION BY A1.YS_STK_COL_GP ORDER BY A1.YS_STK_COL_GP, A1.YS_STK_LYR_NO DESC, A1.YS_STK_BED_NO DESC, A1.YS_STK_SEQ_NO DESC) AS CC
	                                         , SUM(DECODE(A1.YD_STK_LYR_MTL_STAT,'U',1,0)) OVER (PARTITION BY A1.YS_STK_COL_GP ) AS MTL_STAT_UP_CNT  --권상대기 재료매수: [U:권상대기]
	                                      FROM TB_YS_STKLYR A1
	                                         , TB_YS_STKCOL B1
	                                         , PARAM        C1
	                                     WHERE A1.YS_STK_COL_GP = B1.YS_STK_COL_GP
	                                       AND A1.SSTL_NO > ' '
	                                       AND A1.DEL_YN  = 'N'
	                                       AND (B1.YD_GP || B1.YD_BAY_GP) = SUBSTR(C1.V_YS_STK_COL_GP,1,2)                    --"[2]야드/동"         제한
	                                       AND (A1.YS_STK_COL_GP||YS_STK_BED_NO) LIKE SUBSTR(C1.V_YD_TO_LOC_GUIDE, 1,6)||'%'  --"[6]야드/동/스판/열" 제한
	                                     ORDER BY A1.YS_STK_COL_GP, A1.YS_STK_LYR_NO DESC, A1.YS_STK_BED_NO DESC, A1.YS_STK_SEQ_NO DESC
	                                )
	                         WHERE CC = 1               --"열별 > 최상단 > Max-BED번호 > 마지막SEQ"
	                           AND MTL_STAT_UP_CNT = 0  --권상대기 재료가 없는 "열"
	                   ) A
	                 , TB_YS_STOCK  B
	                 , TB_YS_STKBED D
	                 , TB_YS_STKCOL E
	                 , (
	                     SELECT *
	                       FROM (
	                                 -- 동일한 사양의 "열" : Heat No > 길이 > 두께(각) > 강종
	                                 SELECT A1.YS_STK_COL_GP
	                                      , MAX( CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T AND B1.SPEC_ABBSYM = C1.V_SPEC_ABBSYM  THEN 9  --일치 항목: Heat No & 길이 & 두께(각) & 강종
	                                                  WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T                                        THEN 8  --일치 항목: Heat No & 길이 & 두께(각)
	                                                  WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L                                                                        THEN 7  --일치 항목: Heat No & 길이
	                                                  WHEN B1.HEAT_NO = C1.V_HEAT_NO                                                                                                        THEN 6  --일치 항목: Heat No
	                                                  ELSE 1 END)  AS SEQ_NUM
	                                      , SUM((CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T AND B1.SPEC_ABBSYM = C1.V_SPEC_ABBSYM  THEN 1  --9점 짜리 재료 개수
	                                                  ELSE 0 END)) AS SEQ_9_SUM
	                                      , SUM((CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T                                        THEN 1  --8점 짜리 재료 개수
	                                                  ELSE 0 END)) AS SEQ_8_SUM
	                                      , SUM((CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L                                                                        THEN 1  --7점 짜리 재료 개수
	                                                  ELSE 0 END)) AS SEQ_7_SUM
	                                      , SUM((CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO                                                                                                        THEN 1  --6점 짜리 재료 개수
	                                                  ELSE 0 END)) AS SEQ_6_SUM
	                                                 
	                                      , '동일한 사양의 열' AS TO_LOC_POINT
	                                      
	--                                      , A1.SSTL_NO
	--                                      , B1.YD_MTL_L, B1.YD_MTL_T, B1.HEAT_NO, B1.SPEC_ABBSYM, C1.V_SPEC_ABBSYM
	--                                      , CASE WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T AND B1.SPEC_ABBSYM = C1.V_SPEC_ABBSYM  THEN 9  --Heat No & 길이 & 두께(각) & 강종
	--                                             WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L AND B1.YD_MTL_T = C1.V_YD_MTL_T                                        THEN 8  --Heat No & 길이 & 두께(각)
	--                                             WHEN B1.HEAT_NO = C1.V_HEAT_NO AND B1.YD_MTL_L = C1.V_YD_MTL_L                                                                        THEN 7  --Heat No & 길이
	--                                             WHEN B1.HEAT_NO = C1.V_HEAT_NO                                                                                                        THEN 6  --Heat No
	--                                             ELSE 1 END  AS SEQ_NUM
	                                      
	                                  FROM TB_YS_STKLYR     A1
	                                     , TB_YS_STOCK      B1
	                                     , PARAM            C1
	                                 WHERE A1.SSTL_NO = B1.SSTL_NO
	                                   AND A1.YD_STK_LYR_ACT_STAT = 'E'                                  --야드적치단활성상태: [E:적치가능]
	                                   AND A1.YS_STK_COL_GP LIKE SUBSTR(C1.V_YS_STK_COL_GP,1,2)||'%'     --야드구분: [GE:특수강 대형옥내/빌렛정정]
	                                   AND A1.DEL_YN  = 'N'
	                                   AND A1.YS_STK_LYR_NO = (SELECT NVL(MAX(YS_STK_LYR_NO),'01')       --각 열의 최상단
	                                                             FROM TB_YS_STKLYR 
	                                                            WHERE YS_STK_COL_GP = A1.YS_STK_COL_GP
	                                                              AND SSTL_NO IS NOT NULL
	                                                              AND DEL_YN = 'N')
	                                 GROUP BY A1.YS_STK_COL_GP
	                                HAVING SUM(DECODE(A1.YD_STK_LYR_MTL_STAT,'U',1,0)) = 0               --[U:권상대기]
	                            )
	                      --WHERE SEQ_NUM > 1
	                      
	                   ) F
	             WHERE 1=1
	               AND A.MAX_SSTL_NO          = B.SSTL_NO
	               AND A.YS_STK_COL_GP        = D.YS_STK_COL_GP
	               AND A.YS_STK_BED_NO        = D.YS_STK_BED_NO
	               AND NVL(D.YD_STK_BED_ACT_STAT,'*') = 'L'        --야드적치Bed활성상태: [L:적치 가능]
	               AND D.YS_STK_COL_GP        = E.YS_STK_COL_GP
	               AND NVL(E.YD_STK_COL_ACT_STAT,'*') = 'L'        --야드적치열활성상태 : [L:적치 가능]
	               AND A.YS_STK_COL_GP        = F.YS_STK_COL_GP
	            
	            UNION ALL
	            
	            -- # "공BED 인 열"
	            SELECT 1                  AS SEQ_NUM
	                 , A.YS_STK_COL_GP    AS YS_STK_COL_GP
	                 , A.YS_STK_BED_NO    AS YS_STK_BED_NO
	                 , ''                 AS YS_STK_LYR_NO 
	                 , ''                 AS YS_STK_SEQ_NO 
	                 , ''                 AS SSTL_NO
	                 , 0                  AS MTL_STAT_UP_CNT 
	                 
	                 , ''                 AS HEAT_NO
	                 , ''                 AS STLKIND_CD
	                 , 0                  AS YD_MTL_T
	                 , 0                  AS YD_MTL_L
	                 
	                 , 0                  AS SEQ_9_SUM      
	                 , 0                  AS SEQ_8_SUM 
	                 , 0                  AS SEQ_7_SUM 
	                 , 0                  AS SEQ_6_SUM 
	                 
	                 ,'공BED 인 열'       AS TO_LOC_POINT
	                 
	              FROM TB_YS_STKLYR A
	                 , (
	                        --적치열별 재료수 조회
	                        SELECT COUNT(A1.SSTL_NO) AS SUM_CNT
	                             , A1.YS_STK_COL_GP
	                             , A1.YS_STK_BED_NO 
	                          FROM TB_YS_STKLYR A1
	                             , PARAM        C1
	                         WHERE 1=1
	                           AND (A1.YS_STK_COL_GP||YS_STK_BED_NO) LIKE SUBSTR(C1.V_YD_TO_LOC_GUIDE,1,6)||'%'  --"[6]야드/동/스판/열" 제한
	                           AND A1.YS_STK_COL_GP                  LIKE SUBSTR(C1.V_YS_STK_COL_GP,  1,2)||'%'  --"[2]야드/동"         제한
	                           AND A1.DEL_YN = 'N'
	                         GROUP BY A1.YS_STK_COL_GP, A1.YS_STK_BED_NO
	                   ) B
	                 , TB_YS_STKBED C
	                 , TB_YS_STKCOL E
	                 , PARAM        P
	             WHERE A.YS_STK_COL_GP = B.YS_STK_COL_GP
	               AND A.YS_STK_BED_NO = B.YS_STK_BED_NO 
	               AND A.YS_STK_COL_GP = C.YS_STK_COL_GP 
	               AND A.YS_STK_BED_NO = C.YS_STK_BED_NO 
	               AND SUBSTR(A.YS_STK_COL_GP,1,2) = SUBSTR(P.V_YS_STK_COL_GP,1,2)
	               AND (A.YS_STK_COL_GP||A.YS_STK_BED_NO) LIKE SUBSTR(P.V_YD_TO_LOC_GUIDE, 1,6)||'%'
	               AND NVL(C.YD_STK_BED_ACT_STAT,'*') = 'L'  --야드적치Bed활성상태: [L:적치 가능]
	               AND C.YS_STK_COL_GP = E.YS_STK_COL_GP
	               AND NVL(E.YD_STK_COL_ACT_STAT,'*') = 'L'  --야드적치열활성상태 : [L:적치 가능]
	               AND B.SUM_CNT       = 0
	             GROUP BY A.YS_STK_COL_GP, A.YS_STK_BED_NO
	       ) A
	
	 ORDER BY SEQ_NUM DESC, SEQ_9_SUM DESC, SEQ_8_SUM DESC, SEQ_7_SUM DESC, SEQ_6_SUM DESC, YS_STK_COL_GP, YS_STK_BED_NO
	 * </pre>
	 */
	public final static String getToLocColByLocGuide = "com.inisteel.cim.ys.sbr.dao.getToLocColByLocGuide";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getYdStkBedInfo
	-- TO위치(열)의 모든 BED들을 대상으로 "Max단"에 존재하는 재료 매수 조회
	
	WITH PARAM AS (
	    SELECT :V_YS_STK_COL_GP  AS V_YS_STK_COL_GP  --TO위치-열
	      FROM DUAL
	)
	SELECT AA.YS_STK_COL_GP
	     , AA.YS_STK_BED_NO
	     , AA.YS_STK_LYR_NO
	     , AA.STL_CNT
	     , BB.YD_STR_GTR_CD
	     , BB.YS_STK_BED_L_GP
	     , BB.YS_STK_BED_W_GP
	     , BB.YD_STK_BED_ACT_STAT
	     , BB.YD_STK_BED_WHIO_STAT
	     , BB.YD_STK_BED_LYR_MAX
	     , BB.YD_STK_BED_WT_MAX
	     , BB.YD_STK_BED_H_MAX
	     , BB.YD_STK_BED_L_MAX
	     , BB.YD_STK_BED_W_MAX
	       
	  FROM (
	            --"TO위치(열)"에서 재료가 존재하는 "Max-단"을 찾으면 
	            --    #형으로 쌓여 있기 때문에  "Max-단"에 존재하는 모든 BED들의 소재 매수 조회
	            SELECT A.YS_STK_COL_GP
	                 , A.YS_STK_BED_NO
	                 , A.YS_STK_LYR_NO
	                 , SUM(DECODE(A.YD_STK_LYR_MTL_STAT,'C',1,'D',1,0)) AS STL_CNT  --야드적치단재료상태: [C:적치중][D:권하대기]
	              FROM TB_YS_STKLYR A
	                 , PARAM        P
	             WHERE A.YS_STK_COL_GP LIKE P.V_YS_STK_COL_GP||'%'
	               AND A.YS_STK_LYR_NO = (SELECT NVL(MAX(YS_STK_LYR_NO),'01')
	                                        FROM TB_YS_STKLYR
	                                       WHERE YS_STK_COL_GP = A.YS_STK_COL_GP
	                                         AND SSTL_NO IS NOT NULL)
	             GROUP BY A.YS_STK_COL_GP, A.YS_STK_BED_NO, A.YS_STK_LYR_NO
	       ) AA
	     , TB_YS_STKBED BB
	         
	 WHERE AA.YS_STK_COL_GP = BB.YS_STK_COL_GP(+)
	   AND AA.YS_STK_BED_NO = BB.YS_STK_BED_NO(+)
	   AND BB.YD_STK_BED_ACT_STAT = 'L'
	 ORDER BY AA.YS_STK_COL_GP, AA.YS_STK_BED_NO, AA.YS_STK_LYR_NO
	 * </pre>
	 */
	public final static String getYdStkBedInfo = "com.inisteel.cim.ys.sbr.dao.getYdStkBedInfo";
	
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getStkBedXY
	-- BED 좌표 정보 조회
	
	WITH PARAM AS (
	    SELECT :V_YS_STK_COL_GP AS V_YS_STK_COL_GP  --적치열
	         , :V_YS_STK_BED_NO AS V_YS_STK_BED_NO  --적치BED
	         , :V_YS_STK_LYR_NO AS V_YS_STK_LYR_NO  --적치단
	      FROM DUAL
	)
	SELECT YS_STK_COL_GP 
	      ,YS_STK_BED_NO 
	      ,YD_STR_GTR_CD 
	      ,YS_STK_BED_TP 
	      ,YS_STK_BED_T_GP 
	      ,YS_STK_BED_W_GP 
	      ,YS_STK_BED_L_GP 
	      ,YS_OUTDIA_GRP_GP
	      ,YD_STK_BED_DIR_GP 
	      ,YD_STK_BED_ACT_STAT 
	      ,YD_STK_BED_WHIO_STAT 
	      ,YD_STK_BED_USG_GP
	      ,YD_STK_BED_XAXIS 
	      ,YD_STK_BED_YAXIS
	      ,YD_STK_BED_ZAXIS
	      ,YD_STK_BED_LYR_MAX
	      ,YD_STK_BED_WT_MAX 
	      ,YD_STK_BED_H_MAX 
	      ,YD_STK_BED_L_MAX 
	      ,YD_STK_BED_W_MAX 
	      ,YD_STK_BED_XAXIS_TOL 
	      ,YD_STK_BED_YAXIS_TOL 
	      ,(SELECT YD_STK_COL_DIR_GP FROM TB_YS_STKCOL WHERE YS_STK_COL_GP = A.YS_STK_COL_GP AND ROWNUM = 1) AS YD_STK_COL_DIR_GP
	      ,YD_STK_BED_XAXIS1
	      ,YD_STK_BED_YAXIS1
	      ,YD_STK_BED_ZAXIS1 
	      ,(CASE WHEN MOD(P.V_YS_STK_LYR_NO,2) = 0 THEN 0 ELSE 1 END) AS DAN_GP  --[1:홀수단][0:짝수단]
	  FROM TB_YS_STKBED A
	      ,PARAM        P
	 WHERE YS_STK_COL_GP = P.V_YS_STK_COL_GP
	   AND YS_STK_BED_NO = P.V_YS_STK_BED_NO
	   AND DEL_YN        = 'N'
	 * </pre>
	 */
	public final static String getStkBedXY = "com.inisteel.cim.ys.sbr.dao.getStkBedXY";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.updCrnschXY
	-- 크레인스케줄(좌표 등) 정보 갱신
	
	UPDATE TB_YS_CRNSCH
	   SET MODIFIER                = :V_MODIFIER
	      ,MOD_DDTT                = SYSDATE
	      ,YD_EQP_WRK_SH           = NVL(:V_YD_EQP_WRK_SH,YD_EQP_WRK_SH)
	      ,YD_EQP_WRK_WT           = NVL(:V_YD_EQP_WRK_WT,YD_EQP_WRK_WT)
	      ,YD_EQP_WRK_T            = NVL(:V_YD_EQP_WRK_T,YD_EQP_WRK_T)
	      ,YD_EQP_WRK_MAX_W        = NVL(:V_YD_EQP_WRK_MAX_W,YD_EQP_WRK_MAX_W)
	      ,YD_EQP_WRK_MAX_L        = NVL(:V_YD_EQP_WRK_MAX_L,YD_EQP_WRK_MAX_L)
	      ,YD_CRN_SB_CTL_H         = NVL(:V_YD_CRN_SB_CTL_H,YD_CRN_SB_CTL_H)
	      ,YD_CRN_GRAB_USE_RULE_ID = NVL(:V_YD_CRN_GRAB_USE_RULE_ID,YD_CRN_GRAB_USE_RULE_ID)
	      ,YS_UP_WO_LOC            = NVL(:V_YS_UP_WO_LOC,YS_UP_WO_LOC)
	      ,YS_UP_WO_LAYER          = NVL(:V_YS_UP_WO_LAYER,YS_UP_WO_LAYER)
	      ,YS_UP_WO_SEQ_NO         = NVL(:V_YS_UP_WO_SEQ_NO,YS_UP_WO_SEQ_NO)
	      ,YD_UP_WO_LOC_XAXIS      = NVL(:V_YD_UP_WO_LOC_XAXIS,YD_UP_WO_LOC_XAXIS)
	      ,YD_UP_WO_XAXIS_GAP_MAX  = NVL(:V_YD_UP_WO_XAXIS_GAP_MAX,YD_UP_WO_XAXIS_GAP_MAX)
	      ,YD_UP_WO_XAXIS_GAP_MIN  = NVL(:V_YD_UP_WO_XAXIS_GAP_MIN,YD_UP_WO_XAXIS_GAP_MIN)
	      ,YD_UP_WO_LOC_YAXIS      = NVL(:V_YD_UP_WO_LOC_YAXIS,YD_UP_WO_LOC_YAXIS)
	      ,YD_UP_WO_LOC_YAXIS1     = NVL(:V_YD_UP_WO_LOC_YAXIS1,YD_UP_WO_LOC_YAXIS1)
	      ,YD_UP_WO_LOC_YAXIS2     = NVL(:V_YD_UP_WO_LOC_YAXIS2,YD_UP_WO_LOC_YAXIS2)
	      ,YD_UP_WO_YAXIS_GAP_MAX  = NVL(:V_YD_UP_WO_YAXIS_GAP_MAX,YD_UP_WO_YAXIS_GAP_MAX)
	      ,YD_UP_WO_YAXIS_GAP_MIN  = NVL(:V_YD_UP_WO_YAXIS_GAP_MIN,YD_UP_WO_YAXIS_GAP_MIN)
	      ,YD_UP_WO_LOC_ZAXIS      = NVL(:V_YD_UP_WO_LOC_ZAXIS,YD_UP_WO_LOC_ZAXIS)
	      ,YD_UP_WO_ZAXIS_GAP_MAX  = NVL(:V_YD_UP_WO_ZAXIS_GAP_MAX,YD_UP_WO_ZAXIS_GAP_MAX)
	      ,YD_UP_WO_ZAXIS_GAP_MIN  = NVL(:V_YD_UP_WO_ZAXIS_GAP_MIN,YD_UP_WO_ZAXIS_GAP_MIN)
	      ,YS_DN_WO_LOC            = NVL(:V_YS_DN_WO_LOC,YS_DN_WO_LOC)
	      ,YS_DN_WO_LAYER          = NVL(:V_YS_DN_WO_LAYER,YS_DN_WO_LAYER)
	      ,YS_DN_WO_SEQ_NO         = NVL(:V_YS_DN_WO_SEQ_NO,YS_DN_WO_SEQ_NO)
	      ,YD_DN_WO_LOC_XAXIS      = NVL(:V_YD_DN_WO_LOC_XAXIS,YD_DN_WO_LOC_XAXIS)
	      ,YD_DN_WO_XAXIS_GAP_MAX  = NVL(:V_YD_DN_WO_XAXIS_GAP_MAX,YD_DN_WO_XAXIS_GAP_MAX)
	      ,YD_DN_WO_XAXIS_GAP_MIN  = NVL(:V_YD_DN_WO_XAXIS_GAP_MIN,YD_DN_WO_XAXIS_GAP_MIN)
	      ,YD_DN_WO_LOC_YAXIS      = NVL(:V_YD_DN_WO_LOC_YAXIS,YD_DN_WO_LOC_YAXIS)
	      ,YD_DN_WO_LOC_YAXIS1     = NVL(:V_YD_DN_WO_LOC_YAXIS1,YD_DN_WO_LOC_YAXIS1)
	      ,YD_DN_WO_LOC_YAXIS2     = NVL(:V_YD_DN_WO_LOC_YAXIS2,YD_DN_WO_LOC_YAXIS2)
	      ,YD_DN_WO_YAXIS_GAP_MAX  = NVL(:V_YD_DN_WO_YAXIS_GAP_MAX,YD_DN_WO_YAXIS_GAP_MAX)
	      ,YD_DN_WO_YAXIS_GAP_MIN  = NVL(:V_YD_DN_WO_YAXIS_GAP_MIN,YD_DN_WO_YAXIS_GAP_MIN)
	      ,YD_DN_WO_LOC_ZAXIS      = NVL(:V_YD_DN_WO_LOC_ZAXIS,YD_DN_WO_LOC_ZAXIS)
	      ,YD_DN_WO_ZAXIS_GAP_MAX  = NVL(:V_YD_DN_WO_ZAXIS_GAP_MAX,YD_DN_WO_ZAXIS_GAP_MAX)
	      ,YD_DN_WO_ZAXIS_GAP_MIN  = NVL(:V_YD_DN_WO_ZAXIS_GAP_MIN,YD_DN_WO_ZAXIS_GAP_MIN)
	      ,YS_UP_WR_LOC            = NVL(:V_YS_UP_WR_LOC,YS_UP_WR_LOC)
	      ,YS_UP_WR_LAYER          = NVL(:V_YS_UP_WR_LAYER,YS_UP_WR_LAYER)
	      ,YS_UP_WR_SEQ_NO         = NVL(:V_YS_UP_WR_SEQ_NO,YS_UP_WR_SEQ_NO)
	      ,YD_UP_WRK_ACT_GP        = NVL(:V_YD_UP_WRK_ACT_GP,YD_UP_WRK_ACT_GP)
	      ,YD_UP_WR_XAXIS          = NVL(:V_YD_UP_WR_XAXIS,YD_UP_WR_XAXIS)
	      ,YD_UP_WR_YAXIS          = NVL(:V_YD_UP_WR_YAXIS,YD_UP_WR_YAXIS)
	      ,YD_UP_WR_YAXIS1         = NVL(:V_YD_UP_WR_YAXIS1,YD_UP_WR_YAXIS1)
	      ,YD_UP_WR_YAXIS2         = NVL(:V_YD_UP_WR_YAXIS2,YD_UP_WR_YAXIS2)
	      ,YD_UP_WR_ZAXIS          = NVL(:V_YD_UP_WR_ZAXIS,YD_UP_WR_ZAXIS)
	      ,YS_DN_WR_LOC            = NVL(:V_YS_DN_WR_LOC,YS_DN_WR_LOC)
	      ,YS_DN_WR_LAYER          = NVL(:V_YS_DN_WR_LAYER,YS_DN_WR_LAYER)
	      ,YS_DN_WR_SEQ_NO         = NVL(:V_YS_DN_WR_SEQ_NO,YS_DN_WR_SEQ_NO)
	      ,YD_DN_WRK_ACT_GP        = NVL(:V_YD_DN_WRK_ACT_GP,YD_DN_WRK_ACT_GP)
	      ,YD_DN_WR_XAXIS          = NVL(:V_YD_DN_WR_XAXIS,YD_DN_WR_XAXIS)
	      ,YD_DN_WR_YAXIS          = NVL(:V_YD_DN_WR_YAXIS,YD_DN_WR_YAXIS)
	      ,YD_DN_WR_YAXIS1         = NVL(:V_YD_DN_WR_YAXIS1,YD_DN_WR_YAXIS1)
	      ,YD_DN_WR_YAXIS2         = NVL(:V_YD_DN_WR_YAXIS2,YD_DN_WR_YAXIS2)
	      ,YD_DN_WR_ZAXIS          = NVL(:V_YD_DN_WR_ZAXIS,YD_DN_WR_ZAXIS)
	      ,YD_EQP_ID               = NVL(:V_YD_EQP_ID,YD_EQP_ID)
	      ,YD_SCH_CD               = NVL(:V_YD_SCH_CD,YD_SCH_CD)
	      ,YD_TO_LOC_DCSN_MTD      = NVL(:V_YD_TO_LOC_DCSN_MTD,YD_TO_LOC_DCSN_MTD)
	      ,YD_AID_WRK_UPDN_GP      = NVL(:V_YD_AID_WRK_UPDN_GP,YD_AID_WRK_UPDN_GP)
	 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
	 * </pre>
	 */
	public final static String updCrnschXY = "com.inisteel.cim.ys.sbr.dao.updCrnschXY";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getStklyrInfoByCrnsch
	-- 권상위치 작업재료 조회
	
	WITH PARAM AS (
	    SELECT :V_YD_CRN_SCH_ID AS V_YD_CRN_SCH_ID  --크레인스케줄ID
	         , :V_YS_STK_COL_GP AS V_YS_STK_COL_GP  --적치열
	         , :V_YS_STK_BED_NO AS V_YS_STK_BED_NO  --적치BED
	         , :V_YS_STK_LYR_NO AS V_YS_STK_LYR_NO  --적치단
	      FROM DUAL
	)
	SELECT S.YS_STK_COL_GP AS YS_STK_COL_GP
	     , S.YS_STK_BED_NO AS YS_STK_BED_NO
	     , S.YS_STK_LYR_NO AS YS_STK_LYR_NO
	     , S.YS_STK_SEQ_NO AS YS_STK_SEQ_NO
	     , S.SSTL_NO       AS SSTL_NO
	  FROM TB_YS_STKLYR S
	     , PARAM        P
	 WHERE S.YS_STK_COL_GP = P.V_YS_STK_COL_GP
	   AND S.YS_STK_BED_NO = P.V_YS_STK_BED_NO
	   AND S.YS_STK_LYR_NO = P.V_YS_STK_LYR_NO
	   AND S.SSTL_NO IS NOT NULL
	   AND S.SSTL_NO IN (SELECT SSTL_NO FROM TB_YS_CRNWRKMTL WHERE YD_CRN_SCH_ID = P.V_YD_CRN_SCH_ID)
	   AND S.DEL_YN        = 'N'
	 ORDER BY S.YS_STK_COL_GP, S.YS_STK_BED_NO, S.YS_STK_LYR_NO, S.YS_STK_SEQ_NO
	 * </pre>
	 */
	public final static String getStklyrInfoByCrnsch = "com.inisteel.cim.ys.sbr.dao.getStklyrInfoByCrnsch";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getStklyrMaxSeqNo
	-- TO위치(권하위치)에 쌓여있는 재료의 "최대SEQ" 조회
	
	WITH PARAM AS (
	    SELECT :V_YS_STK_COL_GP AS V_YS_STK_COL_GP  --적치열
	         , :V_YS_STK_BED_NO AS V_YS_STK_BED_NO  --적치BED
	         , :V_YS_STK_LYR_NO AS V_YS_STK_LYR_NO  --적치단
	      FROM DUAL
	)
	SELECT NVL(MAX(S.YS_STK_SEQ_NO),0) + 1 AS YS_STK_SEQ_NO_NEXT
	  FROM TB_YS_STKLYR S
	     , PARAM        P
	 WHERE S.YS_STK_COL_GP = P.V_YS_STK_COL_GP
	   AND S.YS_STK_BED_NO = P.V_YS_STK_BED_NO 
	   AND S.YS_STK_LYR_NO = P.V_YS_STK_LYR_NO
	   AND S.SSTL_NO IS NOT NULL
	   AND S.YD_STK_LYR_MTL_STAT IN ('C','D')  --[C:적치중][D:권하대기]
	 * </pre>
	 */
	public final static String getStklyrMaxSeqNo = "com.inisteel.cim.ys.sbr.dao.getStklyrMaxSeqNo";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.updStkLyrToStlNoStat
	-- 권하지시위치에 '작업재료', '재료상태' 변경
	
	UPDATE TB_YS_STKLYR
	   SET MOD_DDTT            = SYSDATE
	     , MODIFIER            = :V_MODIFIER
	     , SSTL_NO             = NVL(:V_SSTL_NO,SSTL_NO)
	     , YD_STK_LYR_ACT_STAT = NVL(:V_YD_STK_LYR_ACT_STAT,YD_STK_LYR_ACT_STAT)  --야드적치단활성상태
	     , YD_STK_LYR_MTL_STAT = NVL(:V_YD_STK_LYR_MTL_STAT,YD_STK_LYR_MTL_STAT)  --야드적치단재료상태
	 WHERE YS_STK_COL_GP = :V_YS_STK_COL_GP
	   AND YS_STK_BED_NO = :V_YS_STK_BED_NO
	   AND YS_STK_LYR_NO = :V_YS_STK_LYR_NO
	   AND YS_STK_SEQ_NO = NVL(:V_YS_STK_SEQ_NO ,1)
	 * </pre>
	 */
	public final static String updStkLyrToStlNoStat = "com.inisteel.cim.ys.sbr.dao.updStkLyrToStlNoStat";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getCrnSchStartGp
	-- 크레인스케줄 기동구분 조회 
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID
	      FROM DUAL
	)
	SELECT WB.YD_GP
	      ,WB.YD_SCH_CD
	      ,(
	        SELECT SR.YD_WRK_CRN
	          FROM (
	                SELECT YD_SCH_CD
	                      ,CASE WHEN A.YD_CRN_PRIOR1 <= YD_CRN_PRIOR2 THEN YD_CRN1
	                            WHEN A.YD_CRN_PRIOR2 < YD_CRN_PRIOR1  THEN YD_CRN2
	                       END AS YD_WRK_CRN
	                    FROM (
	                            --특수강 스케줄코드, 주/부크레인, 우선순위 조회
	                            SELECT YD_SCH_GP 
	                                  ,YD_GP
	                                  ,YD_BAY_GP
	                                  ,YD_SCH_CD
	                                  ,YD_CRN1
	                                  ,YD_CRN_STAT1
	                                  ,CASE WHEN YD_CRN_PRIOR1 <= 0 THEN 99 ELSE YD_CRN_PRIOR1 END AS YD_CRN_PRIOR1
	                                  ,YD_CRN2
	                                  ,YD_CRN_STAT2
	                                  ,CASE WHEN YD_CRN_PRIOR2 <= 0 THEN 99 ELSE YD_CRN_PRIOR2 END AS YD_CRN_PRIOR2
	                                  ,YD_SCH_PROH_EXN
	                              FROM TB_YS_SCHRULE                        -- YS_특수강스케줄기준
	                             WHERE YD_GP      = 'G'                     -- 특수강-야드: 'G'
	                               AND YD_BAY_GP  = 'D'                     -- 특수강-동  : 'D'
	                               AND YD_DATA_GP = 'M'                     -- DATA구분: R(Register), M(Modify)
	                         ) A
	                        ,(
	                            -- 주/부크레인의 설비상태값 조회
	                            SELECT YD_GP
	                                  ,YD_BAY_GP
	                                  ,YD_SCH_GP
	                                  ,MAX(YD_CRN1) AS CRN1  -- 주크레인
	                                  ,MAX(YD_CRN2) AS CRN2  -- 부크레인
	                                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(YD_EQP_ID, YD_CRN1, YD_EQP_STAT, '') ELSE 'O' END), 'X') AS STAT1  -- 주크레인 상태
	                                  ,NVL(MAX(CASE WHEN YD_SCH_GP = 'CR' THEN DECODE(YD_EQP_ID, YD_CRN2, YD_EQP_STAT, '') ELSE 'X' END), 'X') AS STAT2  -- 부크레인 상태
	                              FROM (
	                                        -- 크레인 시설정보에 있는 "스케줄코드-주/부크레인"의 설비상태 조회
	                                        SELECT E.YD_GP
	                                              ,E.YD_BAY_GP
	                                              ,E.YD_EQP_ID
	                                              ,DECODE(E.YD_EQP_STAT,'B','C','O')                         AS YD_EQP_STAT  -- 'B':고장
	                                              ,DECODE(E.YD_EQP_GP,'CR','CR','S'||SUBSTR(E.YD_EQP_ID,-1)) AS YD_SCH_GP    -- 'CR', 'S1', 'S2'...
	                                              ,YD_CRN1  -- 주크레인
	                                              ,YD_CRN2  -- 부크레인
	                                          FROM TB_YS_EQP E     -- YS_설비
	                                              ,(SELECT S.YD_CRN1, S.YD_CRN2 
	                                                  FROM TB_YS_SCHRULE S
	                                                     , (SELECT W.YD_SCH_CD FROM TB_YS_WRKBOOK W, PARAM P WHERE W.YD_WBOOK_ID = P.V_YD_WBOOK_ID) C
	                                                 WHERE S.YD_SCH_CD  = C.YD_SCH_CD 
	                                                   AND S.YD_DATA_GP = 'M') S
	                                         WHERE (E.YD_EQP_ID = S.YD_CRN1 OR E.YD_EQP_ID = S.YD_CRN2)
	                                           AND E.YD_GP     = 'G'
	                                           AND E.YD_BAY_GP = 'D'
	                                           AND E.YD_EQP_GP IN ('CR','SC')
	                                   )
	                             GROUP BY YD_GP, YD_BAY_GP, YD_SCH_GP
	                         ) B
	                    WHERE 1=1
	                    AND   A.YD_SCH_GP    = B.YD_SCH_GP
	                    AND   A.YD_GP        = B.YD_GP
	                    AND   A.YD_BAY_GP    = B.YD_BAY_GP
	                    AND   A.YD_CRN_STAT1 = B.STAT1
	                    AND   A.YD_CRN_STAT2 = B.STAT2
	               ) SR
	         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD
	       ) AS YD_EQP_ID
	  FROM TB_YS_WRKBOOK WB
	     , PARAM         PA
	 WHERE WB.YD_WBOOK_ID = PA.V_YD_WBOOK_ID
	   AND WB.DEL_YN      = 'N'
	 * </pre>
	 */
	public final static String getCrnSchStartGp = "com.inisteel.cim.ys.sbr.dao.getCrnSchStartGp";
	

	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getYsCrnSch
	-- 크레인스케줄 조회
	
	WITH PARAM AS 
	(
	    SELECT :V_YD_WBOOK_ID AS V_YD_WBOOK_ID
	      FROM DUAL
	)
	SELECT	 
	       YD_EQP_ID                                   AS YD_EQP_ID
	      ,(SELECT YD_EQP_STAT 
	          FROM USRYSA.TB_YS_EQP 
	         WHERE YD_EQP_ID = A.YD_EQP_ID)            AS YD_EQP_STAT
	      ,YD_CRN_SCH_ID                               AS YD_CRN_SCH_ID
	      ,REGISTER                                    AS REGISTER
	      ,TO_CHAR(REG_DDTT, 'YYYYMMDDHH24MISS')       AS REG_DDTT
	      ,MODIFIER                                    AS MODIFIER
	      ,TO_CHAR(MOD_DDTT, 'YYYYMMDDHH24MISS')       AS MOD_DDTT
	      ,DEL_YN                                      AS DEL_YN
	      ,YD_WBOOK_ID                                 AS YD_WBOOK_ID
	      ,YD_EQP_ID                                   AS YD_EQP_ID
	      ,YD_GP                                       AS YD_GP
	      ,YD_BAY_GP                                   AS YD_BAY_GP
	      ,YD_SCH_CD                                   AS YD_SCH_CD
	      ,YD_SCH_ST_GP                                AS YD_SCH_ST_GP
	      ,YD_SCH_REQ_GP                               AS YD_SCH_REQ_GP
	      ,YD_SCH_PRIOR                                AS YD_SCH_PRIOR
	      ,YD_EQP_WRK_STAT                             AS YD_EQP_WRK_STAT
	      ,YD_WRK_PROG_STAT                            AS YD_WRK_PROG_STAT
	      ,TO_CHAR(YD_WBOOK_DT, 'YYYYMMDDHH24MISS')    AS YD_WBOOK_DT
	      ,TO_CHAR(YD_SCH_DT, 'YYYYMMDDHH24MISS')      AS YD_SCH_DT
	      ,TO_CHAR(YD_WORD_DT, 'YYYYMMDDHH24MISS')     AS YD_WORD_DT
	      ,TO_CHAR(YD_UP_CMPL_DT, 'YYYYMMDDHH24MISS')  AS YD_UP_CMPL_DT
	      ,TO_CHAR(YD_DN_CMPL_DT, 'YYYYMMDDHH24MISS')  AS YD_DN_CMPL_DT
	      ,YD_WRK_HDS_DD                               AS YD_WRK_HDS_DD
	      ,YD_WRK_DUTY                                 AS YD_WRK_DUTY
	      ,YD_WRK_PARTY                                AS YD_WRK_PARTY
	      ,YD_MAIN_WRK_MTL_SH                          AS YD_MAIN_WRK_MTL_SH
	      ,YD_AID_WRK_MTL_SH                           AS YD_AID_WRK_MTL_SH
	      ,YD_AID_WRK_UPDN_GP                          AS YD_AID_WRK_UPDN_GP
	      ,YD_TO_LOC_DCSN_MTD                          AS YD_TO_LOC_DCSN_MTD
	      ,YD_TO_LOC_GUIDE                             AS YD_TO_LOC_GUIDE
	      ,YD_EQP_WRK_SH                               AS YD_EQP_WRK_SH
	      ,YD_EQP_WRK_WT                               AS YD_EQP_WRK_WT
	      ,YD_EQP_WRK_T                                AS YD_EQP_WRK_T
	      ,YD_EQP_WRK_MAX_W                            AS YD_EQP_WRK_MAX_W
	      ,YD_EQP_WRK_MAX_L                            AS YD_EQP_WRK_MAX_L
	      ,YD_CRN_SB_CTL_H                             AS YD_CRN_SB_CTL_H
	      ,YD_CRN_GRAB_USE_RULE_ID                     AS YD_CRN_GRAB_USE_RULE_ID
	      ,YS_UP_WO_LOC                                AS YS_UP_WO_LOC
	      ,YS_UP_WO_LAYER                              AS YS_UP_WO_LAYER
	      ,YS_UP_WO_SEQ_NO                             AS YS_UP_WO_SEQ_NO
	      ,YD_UP_WO_LOC_XAXIS                          AS YD_UP_WO_LOC_XAXIS
	      ,YD_UP_WO_XAXIS_GAP_MAX                      AS YD_UP_WO_XAXIS_GAP_MAX
	      ,YD_UP_WO_XAXIS_GAP_MIN                      AS YD_UP_WO_XAXIS_GAP_MIN
	      ,YD_UP_WO_LOC_YAXIS                          AS YD_UP_WO_LOC_YAXIS
	      ,YD_UP_WO_LOC_YAXIS1                         AS YD_UP_WO_LOC_YAXIS1
	      ,YD_UP_WO_LOC_YAXIS2                         AS YD_UP_WO_LOC_YAXIS2
	      ,YD_UP_WO_YAXIS_GAP_MAX                      AS YD_UP_WO_YAXIS_GAP_MAX
	      ,YD_UP_WO_YAXIS_GAP_MIN                      AS YD_UP_WO_YAXIS_GAP_MIN
	      ,YD_UP_WO_LOC_ZAXIS                          AS YD_UP_WO_LOC_ZAXIS
	      ,YD_UP_WO_ZAXIS_GAP_MAX                      AS YD_UP_WO_ZAXIS_GAP_MAX
	      ,YD_UP_WO_ZAXIS_GAP_MIN                      AS YD_UP_WO_ZAXIS_GAP_MIN
	      ,YS_DN_WO_LOC                                AS YS_DN_WO_LOC
	      ,YS_DN_WO_LAYER                              AS YS_DN_WO_LAYER
	      ,YS_DN_WO_SEQ_NO                             AS YS_DN_WO_SEQ_NO
	      ,YD_DN_WO_LOC_XAXIS                          AS YD_DN_WO_LOC_XAXIS
	      ,YD_DN_WO_XAXIS_GAP_MAX                      AS YD_DN_WO_XAXIS_GAP_MAX
	      ,YD_DN_WO_XAXIS_GAP_MIN                      AS YD_DN_WO_XAXIS_GAP_MIN
	      ,YD_DN_WO_LOC_YAXIS                          AS YD_DN_WO_LOC_YAXIS
	      ,YD_DN_WO_LOC_YAXIS1                         AS YD_DN_WO_LOC_YAXIS1
	      ,YD_DN_WO_LOC_YAXIS2                         AS YD_DN_WO_LOC_YAXIS2
	      ,YD_DN_WO_YAXIS_GAP_MAX                      AS YD_DN_WO_YAXIS_GAP_MAX
	      ,YD_DN_WO_YAXIS_GAP_MIN                      AS YD_DN_WO_YAXIS_GAP_MIN
	      ,YD_DN_WO_LOC_ZAXIS                          AS YD_DN_WO_LOC_ZAXIS
	      ,YD_DN_WO_ZAXIS_GAP_MAX                      AS YD_DN_WO_ZAXIS_GAP_MAX
	      ,YD_DN_WO_ZAXIS_GAP_MIN                      AS YD_DN_WO_ZAXIS_GAP_MIN
	      ,YS_UP_WR_LOC                                AS YS_UP_WR_LOC
	      ,YS_UP_WR_LAYER                              AS YS_UP_WR_LAYER
	      ,YS_UP_WR_SEQ_NO                             AS YS_UP_WR_SEQ_NO
	      ,YD_UP_WRK_ACT_GP                            AS YD_UP_WRK_ACT_GP
	      ,YD_UP_WR_XAXIS                              AS YD_UP_WR_XAXIS
	      ,YD_UP_WR_YAXIS                              AS YD_UP_WR_YAXIS
	      ,YD_UP_WR_YAXIS1                             AS YD_UP_WR_YAXIS1
	      ,YD_UP_WR_YAXIS2                             AS YD_UP_WR_YAXIS2
	      ,YD_UP_WR_ZAXIS                              AS YD_UP_WR_ZAXIS
	      ,YS_DN_WR_LOC                                AS YS_DN_WR_LOC
	      ,YS_DN_WR_LAYER                              AS YS_DN_WR_LAYER
	      ,YS_DN_WR_SEQ_NO                             AS YS_DN_WR_SEQ_NO
	      ,YD_DN_WRK_ACT_GP                            AS YD_DN_WRK_ACT_GP
	      ,YD_DN_WR_XAXIS                              AS YD_DN_WR_XAXIS
	      ,YD_DN_WR_YAXIS                              AS YD_DN_WR_YAXIS
	      ,YD_DN_WR_YAXIS1                             AS YD_DN_WR_YAXIS1
	      ,YD_DN_WR_YAXIS2                             AS YD_DN_WR_YAXIS2
	      ,YD_DN_WR_ZAXIS                              AS YD_DN_WR_ZAXIS
	  FROM TB_YS_CRNSCH A
	      ,PARAM        P
	 WHERE YD_WBOOK_ID = P.V_YD_WBOOK_ID
	   AND YD_GP       = 'G'
	   AND YD_BAY_GP   = 'D'
	   AND DEL_YN      = 'N'
	 ORDER BY YD_CRN_SCH_ID
	 * </pre>
	 */
	public final static String getYsCrnSch = "com.inisteel.cim.ys.sbr.dao.getYsCrnSch";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.insYSSchlog
	-- "작업예약", "스케줄" 생성 시 단계별 로그 생성
	
	INSERT INTO TB_YS_SCHLOG
	(
		 YS_SCHLOG_ID
		,YD_WRK_GP
		,YD_GP
		,YD_BAY_GP
		,YD_WBOOK_ID
		,YD_CRN_SCH_ID
		,SORT_SEQ
		,SCH_PROG_CNTS
		,SCH_CONTENTS
		,REGISTER
		,REG_DDTT
		,MODIFIER
		,MOD_DDTT
	)
	VALUES
	(
		 USRYSA.SQ_YS_SCHLOG_ID.NEXTVAL
		,:V_YD_WRK_GP
		,:V_YD_GP
		,:V_YD_BAY_GP
		,:V_YD_WBOOK_ID
		,:V_YD_CRN_SCH_ID
		,:V_SORT_SEQ
		,:V_SCH_PROG_CNTS
		,:V_SCH_CONTENTS
		,'SCHLOG'
		,SYSDATE
		,'SCHLOG'
		,SYSDATE
	)
	 * </pre>
	 */
	public final static String insYSSchlog = "com.inisteel.cim.ys.sbr.dao.insYSSchlog";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.updCrnWrkMgtDnLoc
	-- 크레인작업관리-권하지시위치 변경
	
	UPDATE TB_YS_CRNSCH
	   SET MODIFIER     = :V_MODIFIER
	     , MOD_DDTT     = SYSDATE
	     , YS_DN_WO_LOC = :V_YS_DN_WO_LOC
	 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
	   AND DEL_YN        = 'N'
	 * </pre>
	 */
	public final static String updCrnWrkMgtDnLoc = "com.inisteel.cim.ys.sbr.dao.updCrnWrkMgtDnLoc";
	
	
	/**
	 * <pre>
	com.inisteel.cim.ys.sbr.dao.getCarldInfoInqjlByYdWrkBook
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
	public final static String getCarldInfoInqjlByYdWrkBook = "com.inisteel.cim.ys.sbr.dao.getCarldInfoInqjlByYdWrkBook";
	
	
	/**
	 * <pre>
	 com.inisteel.cim.ys.sbr.dao.getCarldInfoInqjlByCarFtmvMtl 
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
	public final static String getCarldInfoInqjlByCarFtmvMtl = "com.inisteel.cim.ys.sbr.dao.getCarldInfoInqjlByCarFtmvMtl";
	
	
	/**
	 * <pre>
	-- com.inisteel.cim.ys.sbr.dao.updYdStkcol
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
	public final static String updYdStkcol = "com.inisteel.cim.ys.sbr.dao.updYdStkcol";
	
		
	/**
	 * <pre>

	 * </pre>
	 */
	public final static String aaaaaaa = "com.inisteel.cim.ys.sbr.dao.aaaaaaa";
	
}
