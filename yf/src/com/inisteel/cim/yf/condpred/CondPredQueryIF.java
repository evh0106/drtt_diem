package com.inisteel.cim.yf.condpred;

public interface CondPredQueryIF {
	/**
	 * <pre>
	 * 2025-02-20
	-- insert weather forecast
	-- com.inisteel.cim.yf.condpred.dao.insWthrFcst
	MERGE INTO USRYFA.TB_YF_X_KMA_FCST T
	USING (
	  SELECT
	      :V_PLNT_TP     AS PLNT_TP     -- 공장구분
	    , :V_FCST_TP     AS FCST_TP     -- 단기,초단기_구분
	    , :V_FCST_LOC    AS FCST_LOC    -- 기상위치
	    , :V_FCST_TIME   AS FCST_TIME   -- 시간
	    , :V_FCST_MESTM  AS FCST_MESTM  -- 등록시간
	    , :V_FCST_TEMP   AS FCST_TEMP   -- 현재시간온도
	    , :V_FCST_SKY    AS FCST_SKY    -- 하늘상태코드
	    , :V_FCST_PTY    AS FCST_PTY    -- 강수상태코드
	    , :V_FCST_POP    AS FCST_POP    -- 강수확률
	    , :V_FCST_WSD    AS FCST_WSD    -- 풍속
	    , :V_FCST_VEC    AS FCST_VEC    -- 풍향
	    , :V_FCST_REH    AS FCST_REH    -- 습도
	    , :V_FCST_RN1    AS FCST_RN1    -- 강수량
	    , :V_REGISTER    AS REGISTER    -- 등록자
	    , :V_MODIFIER    AS MODIFIER    -- 수정자
	  FROM DUAL
	) S
	ON (
	  T.PLNT_TP = S.PLNT_TP
	  AND T.FCST_TP = S.FCST_TP
	  AND T.FCST_LOC = S.FCST_LOC
	  AND T.FCST_TIME = S.FCST_TIME
	  AND T.FCST_MESTM = S.FCST_MESTM
	)
	WHEN MATCHED THEN
	  UPDATE SET
	      T.FCST_TEMP  = S.FCST_TEMP 
	    , T.FCST_SKY   = S.FCST_SKY  
	    , T.FCST_PTY   = S.FCST_PTY  
	    , T.FCST_POP   = S.FCST_POP  
	    , T.FCST_WSD   = S.FCST_WSD  
	    , T.FCST_VEC   = S.FCST_VEC  
	    , T.FCST_REH   = S.FCST_REH  
	    , T.FCST_RN1   = S.FCST_RN1  
	    , T.MODIFIER   = S.MODIFIER  
	    , T.MOD_DDTT   = SYSDATE
	WHEN NOT MATCHED THEN
	  INSERT (
	    PLNT_TP     -- 공장구분
	  , FCST_TP     -- 단기,초단기_구분
	  , FCST_LOC    -- 기상위치
	  , FCST_TIME   -- 시간
	  , FCST_MESTM  -- 등록시간
	  , FCST_TEMP   -- 현재시간온도
	  , FCST_SKY    -- 하늘상태코드
	  , FCST_PTY    -- 강수상태코드
	  , FCST_POP    -- 강수확률
	  , FCST_WSD    -- 풍속
	  , FCST_VEC    -- 풍향
	  , FCST_REH    -- 습도
	  , FCST_RN1    -- 강수량
	  , REGISTER    -- 등록자
	  , REG_DDTT    -- 등록일시
	  , MODIFIER    -- 수정자
	  , MOD_DDTT    -- 수정일시
	  )
	VALUES
	  (
	    S.PLNT_TP     -- 공장구분
	  , S.FCST_TP     -- 단기,초단기_구분
	  , S.FCST_LOC    -- 기상위치
	  , S.FCST_TIME   -- 시간
	  , S.FCST_MESTM  -- 등록시간
	  , S.FCST_TEMP   -- 현재시간온도
	  , S.FCST_SKY    -- 하늘상태코드
	  , S.FCST_PTY    -- 강수상태코드
	  , S.FCST_POP    -- 강수확률
	  , S.FCST_WSD    -- 풍속
	  , S.FCST_VEC    -- 풍향
	  , S.FCST_REH    -- 습도
	  , S.FCST_RN1    -- 강수량
	  , S.REGISTER    -- 등록자
	  , SYSDATE       -- 등록일시
	  , S.MODIFIER    -- 수정자
	  , SYSDATE       -- 수정일시
	  )
	 * </pre>
	 */
	public final static String insWthrFcst = "com.inisteel.cim.yf.condpred.dao.insWthrFcst";
	
	/**
	 * <pre>
	 * 2025-02-20
	-- select weather forecast count list
	-- com.inisteel.cim.yf.condpred.dao.selWthrFcstCntList
	SELECT
	    -- 단기 예보
	    (
	    SELECT
	      COUNT(*)
	    FROM USRYFA.TB_YF_X_KMA_FCST
	    WHERE PLNT_TP = '2000'
	    AND FCST_TP = '1'
	    AND FCST_LOC = '송산면'
	    AND FCST_MESTM = :V_MESTM
	    ) AS VF1
	  , (
	    SELECT
	      COUNT(*)
	    FROM USRYFA.TB_YF_X_KMA_FCST
	    WHERE PLNT_TP = '2000'
	    AND FCST_TP = '1'
	    AND FCST_LOC = '송악읍'
	    AND FCST_MESTM = :V_MESTM
	    )  AS VF2
	    
	    -- 아래는 수량 체크 기능을 비활성하여 최신 예보를 적용할 수 있도록 함.
	    -- 초단기 실황
	  , 0 AS UN1
	  , 0 AS UN2
	    -- 초단기 예보
	  , 0 AS UF1
	  , 0 AS UF2
	FROM DUAL
	 * </pre>
	 */
	public final static String selWthrFcstCntList = "com.inisteel.cim.yf.condpred.dao.selWthrFcstCntList";

	/**
	 * <pre>
	 * 2025-02-20
	-- insert temperature humidity measurement
	-- com.inisteel.cim.yf.condpred.dao.insTHMeas
	MERGE INTO USRYFA.TB_YF_X_TH_MEAS T
	USING (
	  SELECT
	   :V_YD_GP            AS YD_GP             -- 야드 구분
	  ,:V_MEA_LOC          AS MEA_LOC           -- 결로그룹위치
	  ,:V_MEA_DH           AS MEA_DH            -- 측정일시
	  ,:V_TEM_IN_LOC1      AS TEM_IN_LOC1       -- 온도_실내(M1)
	  ,:V_TEM_IN_LOC2      AS TEM_IN_LOC2       -- 온도_실내(M2)
	  ,:V_TEM_IN_LOC3      AS TEM_IN_LOC3       -- 온도_실내(M3)
	  ,:V_TEM_IN_LOC4      AS TEM_IN_LOC4       -- 온도_실내(M4)
	  ,:V_TEM_IN_LOC5      AS TEM_IN_LOC5       -- 온도_실내(M5)
	  ,:V_TEM_OUT_LOC1     AS TEM_OUT_LOC1      -- 온도_실외(M7)
	  ,:V_HUM_IN_LOC1      AS HUM_IN_LOC1       -- 습도_실내(M1)
	  ,:V_HUM_IN_LOC2      AS HUM_IN_LOC2       -- 습도_실내(M2)
	  ,:V_HUM_IN_LOC3      AS HUM_IN_LOC3       -- 습도_실내(M3)
	  ,:V_HUM_IN_LOC4      AS HUM_IN_LOC4       -- 습도_실내(M4)
	  ,:V_HUM_IN_LOC5      AS HUM_IN_LOC5       -- 습도_실내(M5)
	  ,:V_HUM_OUT_LOC1     AS HUM_OUT_LOC1      -- 습도_실외(M7)
	  ,:V_TEM_COIL_LOC1    AS TEM_COIL_LOC1     -- 온도_코일(M1)
	  ,:V_TEM_COIL_LOC2    AS TEM_COIL_LOC2     -- 온도_코일(M2)
	  ,:V_TEM_COIL_LOC3    AS TEM_COIL_LOC3     -- 온도_코일(M3)
	  ,:V_TEM_COIL_LOC4    AS TEM_COIL_LOC4     -- 온도_코일(M4)
	  ,:V_TEM_COIL_LOC5    AS TEM_COIL_LOC5     -- 온도_코일(M5)
	  ,:V_TEM_IN_LOC6      AS TEM_IN_LOC6       -- 온도_실내(M6)
	  ,:V_HUM_IN_LOC6      AS HUM_IN_LOC6       -- 습도_실내(M6)
	  ,:V_TEM_COIL_LOC6    AS TEM_COIL_LOC6     -- 온도_코일(M6)
	  ,:V_REGISTER         AS REGISTER          -- 등록자
	  , SYSDATE            AS REG_DDTT          -- 등록일시
	  ,:V_MODIFIER         AS MODIFIER          -- 수정자
	  , SYSDATE            AS MOD_DDTT          -- 수정일시
	  FROM DUAL ) S
	ON (T.YD_GP = S.YD_GP AND T.MEA_LOC = S.MEA_LOC AND T.MEA_DH = S.MEA_DH)
	WHEN MATCHED THEN
	  UPDATE SET
	      T.TEM_IN_LOC1      = S.TEM_IN_LOC1
	    , T.TEM_IN_LOC2      = S.TEM_IN_LOC2
	    , T.TEM_IN_LOC3      = S.TEM_IN_LOC3
	    , T.TEM_IN_LOC4      = S.TEM_IN_LOC4
	    , T.TEM_IN_LOC5      = S.TEM_IN_LOC5
	    , T.TEM_OUT_LOC1     = S.TEM_OUT_LOC1
	    , T.HUM_IN_LOC1      = S.HUM_IN_LOC1
	    , T.HUM_IN_LOC2      = S.HUM_IN_LOC2
	    , T.HUM_IN_LOC3      = S.HUM_IN_LOC3
	    , T.HUM_IN_LOC4      = S.HUM_IN_LOC4
	    , T.HUM_IN_LOC5      = S.HUM_IN_LOC5
	    , T.HUM_OUT_LOC1     = S.HUM_OUT_LOC1
	    , T.TEM_COIL_LOC1    = S.TEM_COIL_LOC1
	    , T.TEM_COIL_LOC2    = S.TEM_COIL_LOC2
	    , T.TEM_COIL_LOC3    = S.TEM_COIL_LOC3
	    , T.TEM_COIL_LOC4    = S.TEM_COIL_LOC4
	    , T.TEM_COIL_LOC5    = S.TEM_COIL_LOC5
	    , T.TEM_IN_LOC6      = S.TEM_IN_LOC6
	    , T.HUM_IN_LOC6      = S.HUM_IN_LOC6
	    , T.TEM_COIL_LOC6    = S.TEM_COIL_LOC6
	    , T.DEW_CDNS_OCC_YN1 = CASE WHEN S.TEM_IN_LOC1 = 9999 OR S.HUM_IN_LOC1 = 9999 OR S.TEM_COIL_LOC1 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC1 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC1 * 0.1, S.HUM_IN_LOC1 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , T.DEW_CDNS_OCC_YN2 = CASE WHEN S.TEM_IN_LOC2 = 9999 OR S.HUM_IN_LOC2 = 9999 OR S.TEM_COIL_LOC2 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC2 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC2 * 0.1, S.HUM_IN_LOC2 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , T.DEW_CDNS_OCC_YN3 = CASE WHEN S.TEM_IN_LOC3 = 9999 OR S.HUM_IN_LOC3 = 9999 OR S.TEM_COIL_LOC3 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC3 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC3 * 0.1, S.HUM_IN_LOC3 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , T.DEW_CDNS_OCC_YN4 = CASE WHEN S.TEM_IN_LOC4 = 9999 OR S.HUM_IN_LOC4 = 9999 OR S.TEM_COIL_LOC4 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC4 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC4 * 0.1, S.HUM_IN_LOC4 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , T.DEW_CDNS_OCC_YN5 = CASE WHEN S.TEM_IN_LOC5 = 9999 OR S.HUM_IN_LOC5 = 9999 OR S.TEM_COIL_LOC5 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC5 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC5 * 0.1, S.HUM_IN_LOC5 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , T.DEW_CDNS_OCC_YN6 = CASE WHEN S.TEM_IN_LOC6 = 9999 OR S.HUM_IN_LOC6 = 9999 OR S.TEM_COIL_LOC6 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC6 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC6 * 0.1, S.HUM_IN_LOC6 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , T.MODIFIER         = S.MODIFIER
	    , T.MOD_DDTT         = S.MOD_DDTT
	WHEN NOT MATCHED THEN
	  INSERT
	    (
	      YD_GP
	    , MEA_LOC
	    , MEA_DH
	    , TEM_IN_LOC1
	    , TEM_IN_LOC2
	    , TEM_IN_LOC3
	    , TEM_IN_LOC4
	    , TEM_IN_LOC5
	    , TEM_OUT_LOC1
	    , HUM_IN_LOC1
	    , HUM_IN_LOC2
	    , HUM_IN_LOC3
	    , HUM_IN_LOC4
	    , HUM_IN_LOC5
	    , HUM_OUT_LOC1
	    , TEM_COIL_LOC1
	    , TEM_COIL_LOC2
	    , TEM_COIL_LOC3
	    , TEM_COIL_LOC4
	    , TEM_COIL_LOC5
	    , TEM_IN_LOC6
	    , HUM_IN_LOC6
	    , TEM_COIL_LOC6
	    , DEW_CDNS_OCC_YN1
	    , DEW_CDNS_OCC_YN2
	    , DEW_CDNS_OCC_YN3
	    , DEW_CDNS_OCC_YN4
	    , DEW_CDNS_OCC_YN5
	    , DEW_CDNS_OCC_YN6
	    , REGISTER
	    , REG_DDTT
	    , MODIFIER
	    , MOD_DDTT
	    )
	  VALUES
	    (
	      S.YD_GP
	    , S.MEA_LOC
	    , S.MEA_DH
	    , S.TEM_IN_LOC1
	    , S.TEM_IN_LOC2
	    , S.TEM_IN_LOC3
	    , S.TEM_IN_LOC4
	    , S.TEM_IN_LOC5
	    , S.TEM_OUT_LOC1
	    , S.HUM_IN_LOC1
	    , S.HUM_IN_LOC2
	    , S.HUM_IN_LOC3
	    , S.HUM_IN_LOC4
	    , S.HUM_IN_LOC5
	    , S.HUM_OUT_LOC1
	    , S.TEM_COIL_LOC1
	    , S.TEM_COIL_LOC2
	    , S.TEM_COIL_LOC3
	    , S.TEM_COIL_LOC4
	    , S.TEM_COIL_LOC5
	    , S.TEM_IN_LOC6
	    , S.HUM_IN_LOC6
	    , S.TEM_COIL_LOC6
	    , CASE WHEN S.TEM_IN_LOC1 = 9999 OR S.HUM_IN_LOC1 = 9999 OR S.TEM_COIL_LOC1 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC1 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC1 * 0.1, S.HUM_IN_LOC1 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , CASE WHEN S.TEM_IN_LOC2 = 9999 OR S.HUM_IN_LOC2 = 9999 OR S.TEM_COIL_LOC2 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC2 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC2 * 0.1, S.HUM_IN_LOC2 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , CASE WHEN S.TEM_IN_LOC3 = 9999 OR S.HUM_IN_LOC3 = 9999 OR S.TEM_COIL_LOC3 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC3 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC3 * 0.1, S.HUM_IN_LOC3 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , CASE WHEN S.TEM_IN_LOC4 = 9999 OR S.HUM_IN_LOC4 = 9999 OR S.TEM_COIL_LOC4 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC4 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC4 * 0.1, S.HUM_IN_LOC4 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , CASE WHEN S.TEM_IN_LOC5 = 9999 OR S.HUM_IN_LOC5 = 9999 OR S.TEM_COIL_LOC5 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC5 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC5 * 0.1, S.HUM_IN_LOC5 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , CASE WHEN S.TEM_IN_LOC6 = 9999 OR S.HUM_IN_LOC6 = 9999 OR S.TEM_COIL_LOC6 = 9999 THEN NULL WHEN ((S.TEM_COIL_LOC6 * 0.1) - USRYFA.SF_YF_CALC_DEW_POINT(S.TEM_IN_LOC6 * 0.1, S.HUM_IN_LOC6 * 0.1)) <= 1 THEN 'Y' ELSE 'N' END
	    , S.REGISTER
	    , S.REG_DDTT
	    , S.MODIFIER
	    , S.MOD_DDTT
	    )
	 * </pre>
	 */
	public final static String insTHMeas = "com.inisteel.cim.yf.condpred.dao.insTHMeas";
	
	
	/**
	 * <pre>
	-- 열연 결로 알람 메시지 목록 조회
	-- com.inisteel.cim.yf.condpred.dao.selAlarmMsgList
	SELECT
	    SEQ
	  , SND_STS
	  , YD_GP
	  , FCST_TIM
	  , SENDER
	  , SEND_CONTENT
	  , RECV_ID
	  , PHONE_NUM
	FROM USRYFA.TB_YF_X_DEW_ALARM_MSG
	WHERE SND_STS = :V_SND_STS
	ORDER BY FCST_TIM ASC, SEQ ASC
	 * </pre>
	 */
	public final static String selAlarmMsgList = "com.inisteel.cim.yf.condpred.dao.selAlarmMsgList";
	
	
	/**
	 * <pre>
	-- 열연 결로 알람 메시지 상태 변경
	-- com.inisteel.cim.yf.condpred.dao.udtAlarmMsgList
	UPDATE USRYFA.TB_YF_X_DEW_ALARM_MSG SET
	    SND_STS = :V_SND_STS
	  , MODIFIER = :V_MODIFIER
	  , MOD_DDTT = SYSDATE
	WHERE SEQ = :V_SEQ
	 * </pre>
	 */
	public final static String udtAlarmMsgList = "com.inisteel.cim.yf.condpred.dao.udtAlarmMsgList";
	
	
	/**
	 * <pre>
	-- 열연 결로 예보 알람 발송 프로시저 호출
	-- com.inisteel.cim.yf.condpred.dao.callSpYfCfAlmProc
	call USRYFA.SP_YF_CF_ALM_PROC(?,?,?)
	 * </pre>
	 */
	public final static String callSpYfCfAlmProc = "com.inisteel.cim.yf.condpred.dao.callSpYfCfAlmProc";
	
	/**
	 * <pre>
	-- 열연 결로 예측 공공데이터포털 서비스키 조회
	-- com.inisteel.cim.yf.condpred.dao.selApisServiceKey
	-- 2025.10.27 기상청 API 대체
	SELECT 
	  A.USE_YN -- 대체 유무
	, CASE WHEN A.USE_YN = 'Y' THEN A.CRNT_OTD_CFM_KEY -- 대체 API 인증키
		   ELSE A.BF_OTD_CFM_KEY -- 기존 API 인증키
		   END AS SERVICEKEY
	FROM BRE.VW_YD_YDCP04 A
	WHERE ROWNUM = 1
	 * </pre>
	 */
	public final static String selApisServiceKey = "com.inisteel.cim.yf.condpred.dao.selApisServiceKey";
}
