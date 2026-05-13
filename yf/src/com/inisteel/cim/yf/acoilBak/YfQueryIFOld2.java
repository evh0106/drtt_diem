package com.inisteel.cim.yf.acoilBak;
/**
* Ver. 2020-09-04 08:45:48
**/
public interface YfQueryIFOld2
{

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updStackLayerByStockId2   
UPDATE TB_YF_STKLYR
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , STL_NO			= ''
     , YD_STK_LYR_STAT	= 'E'
WHERE STL_NO   = :V_STL_NO
  AND YD_STK_LYR_STAT IN ('C','U')
 </pre> */
public final static String updStackLayerByStockId2 = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStackLayerByStockId2";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updNewReprCdContents
UPDATE TB_YF_RULE 
SET REPR_CD_CONTENTS = :V_REPR_CD_CONTENTS 
WHERE REPR_CD_GP || CD_GP || ITEM = :V_VER_ID
 </pre> */
public final static String updNewReprCdContents = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updNewReprCdContents";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdEqpHomeBayGp1 
    UPDATE TB_YF_EQP
       SET YD_HOME_BAY_GP = :V_DTL_ITEM2,
           MODIFIER = :V_MODIFIER,
           MOD_DDTT = SYSDATE
     WHERE YD_EQP_ID = :V_YD_EQP_ID1
 </pre> */
public final static String updYdEqpHomeBayGp1 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdEqpHomeBayGp1";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdEqpHomeBayGp2 
    UPDATE TB_YF_EQP 
       SET YD_HOME_BAY_GP = :V_DTL_ITEM3,
           MODIFIER = :V_MODIFIER,
           MOD_DDTT = SYSDATE
     WHERE YD_EQP_ID = :V_YD_EQP_ID2
 </pre> */
public final static String updYdEqpHomeBayGp2 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdEqpHomeBayGp2";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updStackLayerByStockId4 
--권하예약 위치는 초기화하고, 권상예약위치는 적치중으로 수정 
UPDATE TB_YF_STKLYR
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , STL_NO			= DECODE(YD_STK_LYR_STAT,'D','',STL_NO)
     , YD_STK_LYR_STAT	= DECODE(YD_STK_LYR_STAT,'D','E','C')
WHERE STL_NO   = :V_STL_NO
  AND YD_STK_LYR_STAT IN ('D','U')
 </pre> */
public final static String updStackLayerByStockId4 = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStackLayerByStockId4";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insStockCrInfo
INSERT INTO TB_YF_STOCK
(
    STL_NO,
    REGISTER,
    REG_DDTT,
    MODIFIER,
    MOD_DDTT,
    DEL_YN,
    STOCK_MOVE_TERM,
    CAR_CARD_NO,
    CAR_NO,
    TRN_EQP_CD,
    YD_CAR_UPP_LOC_CD,
    LINE_OFF_YN
) 
VALUES
(
    :V_STL_NO,
    :V_REGISTER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N',
    CASE 
    WHEN :V_TRN_EQP_CD IS NOT NULL THEN 'CS'
    WHEN :V_CAR_NO IS NOT NULL THEN 'LG'
    ELSE '' END,
    :V_CAR_CARD_NO,
    :V_CAR_NO,
    :V_TRN_EQP_CD,
    :V_YD_CAR_UPP_LOC_CD,
    'Y'
)
 </pre> */
public final static String insStockCrInfo = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insStockCrInfo";

 /** <pre> 
--com.inisteel.cim.yf.jsp.coiljsp.session.CarschUpdatePoint
UPDATE --+  bypass_ujvc
    (
        --------------------------------------------------------------------------
        SELECT
            YD_PNT_CD1,
            YD_CARLD_STOP_LOC,
            YD_PNT_CD3,
            YD_CARUD_STOP_LOC,
            YD_CAR_PROG_STAT ,
            (
                SELECT
                    YD_PNT_CD
                FROM
                    USRYDA.TB_YD_CARPOINT B 
                WHERE 1=1
                AND B.YD_STK_COL_GP = :V_TO_YD_STK_COL_GP
                AND DEL_YN='N'
            ) AS TO_YD_PNT_CD ,
            :V_TO_YD_STK_COL_GP AS TO_YD_STK_COL_GP
        FROM
            USRYDA.TB_YD_CARSCH A
        WHERE 1=1
        AND DEL_YN='N'
        AND YD_CAR_PROG_STAT IN('1','A')
        AND CAR_NO IS NOT NULL
        AND (CASE YD_CAR_PROG_STAT WHEN '1' THEN YD_CARLD_STOP_LOC ELSE YD_CARUD_STOP_LOC END) = :V_YD_STK_COL_GP
        --------------------------------------------------------------------------
    )
SET
    YD_PNT_CD1          = DECODE(YD_CAR_PROG_STAT,'1',TO_YD_PNT_CD, YD_PNT_CD1),
    YD_CARLD_STOP_LOC   = DECODE(YD_CAR_PROG_STAT,'1',TO_YD_STK_COL_GP, YD_CARLD_STOP_LOC),
    YD_PNT_CD3          = DECODE(YD_CAR_PROG_STAT,'A',TO_YD_PNT_CD, YD_PNT_CD3),
    YD_CARUD_STOP_LOC   = DECODE(YD_CAR_PROG_STAT,'A',TO_YD_STK_COL_GP, YD_CARUD_STOP_LOC)
 </pre> */
public final static String CarschUpdatePoint = "bak.com.inisteel.cim.yf.jsp.coiljsp.session.CarschUpdatePoint";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updStockCrInfo
UPDATE TB_YF_STOCK
SET
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE,
    DEL_YN              = 'N',
    STOCK_MOVE_TERM     = CASE 
                          WHEN :V_TRN_EQP_CD IS NOT NULL THEN 'CS'
                          WHEN :V_CAR_NO IS NOT NULL THEN 'LG'
                          ELSE STOCK_MOVE_TERM END,
    CAR_CARD_NO         = NVL(:V_CAR_CARD_NO, CAR_CARD_NO),
    CAR_NO              = NVL(:V_CAR_NO, CAR_NO),
    TRN_EQP_CD          = NVL(:V_TRN_EQP_CD, TRN_EQP_CD),
    YD_CAR_UPP_LOC_CD   = NVL(:V_YD_CAR_UPP_LOC_CD, YD_CAR_UPP_LOC_CD),
    LINE_OFF_YN         = 'Y'
WHERE 1=1
AND STL_NO              = :V_STL_NO
 </pre> */
public final static String updStockCrInfo = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStockCrInfo";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchLocSrchZoneSeq
UPDATE TB_YF_SCHLOCSRCH_ZONE A
   SET A.YD_LOC_SRCH_RNG_SEQ = (SELECT V_YD_LOC_SRCH_RNG_SEQ
                                FROM (SELECT B.YD_STK_COL_GP
                                            ,ROWNUM + TO_NUMBER(B.V_YD_LOC_SRCH_RNG_SEQ) - (SELECT DECODE(COUNT(C.YD_STK_COL_GP),0,1,0)
                                                                                             FROM TB_YF_SCHLOCSRCH_ZONE C
                                                                                            WHERE C.YD_STK_COL_GP = B.V_YD_STK_COL_GP
                                                                                              AND C.DEL_YN = 'N') AS V_YD_LOC_SRCH_RNG_SEQ
                                        FROM (SELECT B.YD_STK_COL_GP
                                                    ,B.YD_LOC_SRCH_RNG_SEQ
                                                    ,P.V_YD_LOC_SRCH_RNG_SEQ
                                                    ,P.V_YD_STK_COL_GP
                                                    ,P.V_YD_ZONE_GP
                                                FROM TB_YF_SCHLOCSRCH_ZONE B
                                                    ,(SELECT :V_YD_LOC_SRCH_RNG_SEQ AS V_YD_LOC_SRCH_RNG_SEQ
                                                            ,:V_YD_STK_COL_GP AS V_YD_STK_COL_GP
                                                            ,:V_YD_ZONE_GP AS V_YD_ZONE_GP
                                                        FROM DUAL) P
                                               WHERE B.YD_LOC_SRCH_RNG_SEQ >= P.V_YD_LOC_SRCH_RNG_SEQ
                                                 AND B.YD_STK_COL_GP != P.V_YD_STK_COL_GP
                                                 AND B.YD_ZONE_GP = P.V_YD_ZONE_GP
                                                 AND B.YD_STK_COL_GP LIKE SUBSTR(P.V_YD_STK_COL_GP,1,2) || '%'
                                                 AND B.DEL_YN = 'N'
                                               ORDER BY B.YD_LOC_SRCH_RNG_SEQ ASC,B.YD_STK_COL_GP ASC
                                              ) B
                                      ) B 
                                WHERE B.YD_STK_COL_GP = A.YD_STK_COL_GP
                             )
 WHERE A.YD_STK_COL_GP IN (SELECT B.YD_STK_COL_GP
                             FROM TB_YF_SCHLOCSRCH_ZONE B
                            WHERE B.YD_LOC_SRCH_RNG_SEQ >= :V_YD_LOC_SRCH_RNG_SEQ
                              AND B.YD_STK_COL_GP != :V_YD_STK_COL_GP
                              AND B.YD_ZONE_GP = :V_YD_ZONE_GP
                              AND B.YD_STK_COL_GP LIKE SUBSTR(:V_YD_ZONE_GP,1,2) || '%'
                              AND B.DEL_YN = 'N'
                          )
 </pre> */
public final static String updSchLocSrchZoneSeq = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchLocSrchZoneSeq";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delZoneVerId
DELETE TB_YF_SCHLOCSRCH_ZONE_V 
 WHERE VER_ID = :V_VER_ID
 </pre> */
public final static String delZoneVerId = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delZoneVerId";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.ApplyZoneVer1
MERGE 
 INTO TB_YF_SCHLOCSRCH_ZONE A
USING (
SELECT  Z.YD_STK_COL_GP
      ,Z.YD_LOC_SRCH_RNG_SEQ
      ,Z.YD_AIM_BAY_GP
      ,:V_MODIFIER AS MODIFIER
      ,Z.YD_ZONE_GP
  FROM TB_YF_SCHLOCSRCH_ZONE_V Z
 WHERE Z.VER_ID = :V_VER_ID
   AND Z.DEL_YN = 'N'
) Z ON ( A.YD_STK_COL_GP = Z.YD_STK_COL_GP)
WHEN MATCHED THEN 
UPDATE SET
    A.DEL_YN = 'N'
   ,A.YD_LOC_SRCH_RNG_SEQ = Z.YD_LOC_SRCH_RNG_SEQ
   ,A.YD_ZONE_GP = Z.YD_ZONE_GP
WHEN NOT MATCHED THEN
   INSERT  (
                     YD_STK_COL_GP
                    ,REGISTER
                    ,REG_DDTT
                    ,MODIFIER
                    ,MOD_DDTT
                    ,DEL_YN
                    ,YD_LOC_SRCH_RNG_SEQ
                    ,YD_AIM_BAY_GP
                    ,YD_ZONE_GP
		    ) VALUES (
                     Z.YD_STK_COL_GP
                    ,Z.MODIFIER
                    ,SYSDATE
                    ,Z.MODIFIER
                    ,SYSDATE
                    ,'N'
                    ,Z.YD_LOC_SRCH_RNG_SEQ
                    ,Z.YD_AIM_BAY_GP
                    ,Z.YD_ZONE_GP
		    ) 
 </pre> */
public final static String ApplyZoneVer1 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.ApplyZoneVer1";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCarStopLoc
UPDATE TB_YD_CARSCH
   SET YD_CARLD_STOP_LOC = CASE REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]') --상차차량
                           WHEN 0
                            THEN :V_YD_STK_COL_GP
                           ELSE YD_CARLD_STOP_LOC
                            END
      ,YD_CARUD_STOP_LOC = CASE REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]') --하차차량
                           WHEN 0
                            THEN YD_CARUD_STOP_LOC
                           ELSE :V_YD_STK_COL_GP
                            END
      ,YD_PNT_CD1 = CASE REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]') --상차차량
                    WHEN 0 
                        THEN (SELECT YD_PNT_CD
                                FROM TB_YD_CARPOINT
                               WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
                                 AND DEL_YN = 'N') 
                    ELSE YD_PNT_CD1
                    END
      ,YD_PNT_CD3 = CASE REGEXP_INSTR(YD_CAR_PROG_STAT,'[^0-9]') --하차차량
                    WHEN 0 
                        THEN YD_PNT_CD3 
                    ELSE (SELECT YD_PNT_CD
                                FROM TB_YD_CARPOINT
                               WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
                                 AND DEL_YN = 'N') 
                    END
 WHERE DEL_YN = 'N'
   AND NVL(TRN_EQP_CD,CAR_NO) = :V_CAR_NO
 </pre> */
public final static String updCarStopLoc = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCarStopLoc";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delCTSSch
UPDATE TB_YF_CTS_SCH
SET 
 DEL_YN         ='Y'
,MODIFIER       = :V_MODIFIER
,MOD_DDTT       = SYSDATE
WHERE DEL_YN    ='N'
  AND YD_WRK_PROG_STAT IN ('S','W')
 </pre> */
public final static String delCTSSch = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delCTSSch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.insAddVersion
INSERT 
INTO TB_YF_RULE(
            REPR_CD_GP
           ,CD_GP
           ,ITEM
           ,REGISTER
           ,REG_DDTT
           ,MODIFIER
           ,MOD_DDTT
           ,DEL_YN
           ,REPR_CD_CONTENTS
     )
VALUES(
      'YF0007'
     ,'1'
     ,:V_ITEM
     ,:V_MODIFIER
     ,SYSDATE
     ,:V_MODIFIER
     ,SYSDATE
     ,'N'
     ,:V_REPR_CD_CONTENTS
     )
 </pre> */
public final static String insAddVersion = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.insAddVersion";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchLocSrchZoneSeq1
UPDATE TB_YF_SCHLOCSRCH_ZONE_V A
   SET A.YD_LOC_SRCH_RNG_SEQ = (SELECT V_YD_LOC_SRCH_RNG_SEQ
                                FROM (SELECT B.YD_STK_COL_GP
                                            ,ROWNUM + TO_NUMBER(B.V_YD_LOC_SRCH_RNG_SEQ) - (SELECT DECODE(COUNT(C.YD_STK_COL_GP),0,1,0)
                                                                                             FROM TB_YF_SCHLOCSRCH_ZONE_V C
                                                                                            WHERE C.YD_STK_COL_GP = B.V_YD_STK_COL_GP
                                                                                              AND C.VER_ID = B.VER_ID
                                                                                              AND C.DEL_YN = 'N') AS V_YD_LOC_SRCH_RNG_SEQ
                                        FROM (SELECT B.YD_STK_COL_GP
                                                    ,B.YD_LOC_SRCH_RNG_SEQ
                                                    ,B.VER_ID
                                                    ,P.V_YD_LOC_SRCH_RNG_SEQ
                                                    ,P.V_YD_STK_COL_GP
                                                    ,P.V_YD_ZONE_GP
                                                FROM TB_YF_SCHLOCSRCH_ZONE_V B
                                                    ,(SELECT :V_YD_LOC_SRCH_RNG_SEQ AS V_YD_LOC_SRCH_RNG_SEQ
                                                            ,:V_YD_STK_COL_GP AS V_YD_STK_COL_GP
                                                            ,:V_YD_ZONE_GP AS V_YD_ZONE_GP
                                                            ,:V_VER_ID AS V_VER_ID
                                                        FROM DUAL) P
                                               WHERE B.YD_LOC_SRCH_RNG_SEQ >= P.V_YD_LOC_SRCH_RNG_SEQ
                                                 AND B.YD_STK_COL_GP != P.V_YD_STK_COL_GP
                                                 AND B.YD_ZONE_GP = P.V_YD_ZONE_GP
                                                 AND B.DEL_YN = 'N'
                                                 AND B.VER_ID = P.V_VER_ID
                                               ORDER BY B.YD_LOC_SRCH_RNG_SEQ ASC,B.YD_STK_COL_GP ASC
                                              ) B
                                      ) B 
                                WHERE B.YD_STK_COL_GP = A.YD_STK_COL_GP
                             )
 WHERE A.YD_STK_COL_GP IN (SELECT B.YD_STK_COL_GP
                             FROM TB_YF_SCHLOCSRCH_ZONE_V B
                            WHERE B.YD_LOC_SRCH_RNG_SEQ >= :V_YD_LOC_SRCH_RNG_SEQ
                              AND B.YD_STK_COL_GP != :V_YD_STK_COL_GP
                              AND B.YD_ZONE_GP = :V_YD_ZONE_GP
                              AND B.VER_ID = :V_VER_ID
                              AND B.DEL_YN = 'N'
                          )
 </pre> */
public final static String updSchLocSrchZoneSeq1 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchLocSrchZoneSeq1";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updsteTurnTableY
UPDATE TB_YF_RULE SET DTL_ITEM1 = :V_YN
                                    , DTL_ITEM2 = :V_STL_NO  
                                    , MODIFIER  = :V_MODIFIER 
                                    , MOD_DDTT = sysdate
WHERE REPR_CD_GP = :V_REPR_CD_GP
  AND CD_GP=:V_YD_GP
  AND ITEM=:V_ITEM
 </pre> */
public final static String updsteTurnTableY = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updsteTurnTableY";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateTurnTableN
UPDATE TB_YF_RULE SET DTL_ITEM1 = :V_YN 
                                    , DTL_ITEM2 = NULL
                                   , MODIFIER = :V_MODIFIER 
                                   , MOD_DDTT = sysdate
WHERE REPR_CD_GP = :V_REPR_CD_GP
  AND CD_GP=:V_YD_GP
  AND ITEM=:V_ITEM
 </pre> */
public final static String updateTurnTableN = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateTurnTableN";

 /** <pre> 
UPDATE TB_YF_STOCK
SET LINE_OFF_SEQ = '1'
  , MODIFIER = :V_MODIFIER
  , MOD_DDTT = sysdate    
WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String updBLineOffLank = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updBLineOffLank";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updLineOffTarget
UPDATE TB_YF_STOCK
SET 
 MODIFIER    = :V_MODIFIER
,MOD_DDTT    = SYSDATE
,LINE_OFF_YN = 'Y'
WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String updLineOffTarget = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updLineOffTarget";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updBLineOffLank9
UPDATE TB_YF_STOCK
SET LINE_OFF_SEQ = '9'
  , MODIFIER = :V_MODIFIER
  , MOD_DDTT = sysdate    
WHERE LINE_OFF_YN = 'N'
 </pre> */
public final static String updBLineOffLank9 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updBLineOffLank9";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkBookMtlByStkLyr2
--적재위치대상재 작업예약재료 등록 
MERGE INTO TB_YF_WRKBOOKMTL WM USING (
SELECT :V_YD_WBOOK_ID   AS YD_WBOOK_ID --야드작업예약ID
     , SL.STL_NO                     --재료번호
     , :V_MODIFIER      AS MODIFIER    --수정자
     , SYSDATE          AS MOD_DDTT    --수정일시
     , 'N'              AS DEL_YN      --삭제유무
     , SL.YD_STK_COL_GP                 --야드적치열구분
     , SL.YD_STK_BED_NO                 --야드적치Bed번호
     , SL.YD_STK_LYR_NO               --야드적치단번호
  FROM TB_YF_STKLYR SL
 WHERE SL.YD_STK_COL_GP   = :V_YD_STK_COL_GP
   AND SL.YD_STK_BED_NO   = :V_YD_STK_BED_NO
   AND SL.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
   AND SL.STL_NO IS NOT NULL
) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STL_NO = DD.STL_NO)
WHEN NOT MATCHED THEN
INSERT (WM.YD_WBOOK_ID   , WM.STL_NO        , WM.REGISTER      , WM.REG_DDTT    ,
        WM.MODIFIER      , WM.MOD_DDTT        , WM.DEL_YN        , WM.YD_STK_COL_GP,
        WM.YD_STK_BED_NO  , WM.YD_STK_LYR_NO )
VALUES (DD.YD_WBOOK_ID   , DD.STL_NO        , DD.MODIFIER      , DD.MOD_DDTT    ,
        DD.MODIFIER      , DD.MOD_DDTT        , DD.DEL_YN        , DD.YD_STK_COL_GP,
        '00'  , DD.YD_STK_LYR_NO )

 </pre> */
public final static String insWrkBookMtlByStkLyr2 = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkBookMtlByStkLyr2";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStkcol
UPDATE TB_YF_STKCOL
   SET 
       YD_STK_COL_RULE_X_AXIS = :V_YD_STK_COL_RULE_X_AXIS
     , YD_STK_COL_RULE_Y_AXIS = :V_YD_STK_COL_RULE_Y_AXIS
     , YD_STK_COL_RULE_Z_AXIS = :V_YD_STK_COL_RULE_Z_AXIS    
     , MOD_DDTT              = SYSDATE             
     , MODIFIER              = :V_MODIFIER             
     , ROTATION_ANGLE        = :V_ROTATION_ANGLE
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String updYfStkcol = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStkcol";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonLocInfo2
UPDATE TB_PT_MSLABCOMM
        SET(  
            YD_GP,              -- 야드구분
            YD_BAY_GP,                -- 동
            YD_EQP_GP,               -- SPAN
            YD_STK_COL_NO,                -- 적치열번지
            YD_STK_BED_NO,             -- 적치번지
            YD_STK_LYR_NO,        -- 적치단
            YD_STR_LOC,       -- 현 저장위치코드
            YD_STR_LOC_HIS1,      -- 전 저장위치코드
            YD_STR_LOC_HIS2,   -- 전전 저장위치코드
            CURR_PROG_CD,
            BEFO_PROG_CD_REG_PGM, 
            BEFO_PROG_REG_DDTT,
            BEFO_PROG_CD,
            BEFOBEFO_PROG_CD_REG_PGM,
            BEFOBEFO_PROG_REG_DDTT,
            BEFOBEFO_PROG_CD 
           )=
           (SELECT D.YD_GP AS YD_GP
                  ,D.BAY_GP AS BAY_GP
                  ,D.YD_EQP_GP AS YD_EQP_GP
                  ,D.YD_STK_COL_NO AS YD_STK_COL_NO
                  ,D.YD_STK_BED_NO AS YD_STK_BED_NO
                  ,'0' || D.YD_STK_LYR_NO AS YD_STK_LYR_NO
                  ,D.YD_GP || D.BAY_GP || D.YD_EQP_GP || D.YD_STK_COL_NO || D.YD_STK_BED_NO || D.YD_STK_LYR_NO AS YD_STR_LOC
                  ,C.YD_STR_LOC AS YD_STR_LOC_HIS1    -- 전 저장위치코드
                  ,C.YD_STR_LOC_HIS1 AS YD_STR_LOC_HIS2 -- 전전 저장위치코드
                  ,DECODE(C.CURR_PROG_CD,'E','A',C.CURR_PROG_CD)
                  ,C.CURR_PROG_CD_REG_PGM
                  ,C.CURR_PROG_REG_DDTT
                  ,C.CURR_PROG_CD
                  ,C.BEFO_PROG_CD_REG_PGM
                  ,C.BEFO_PROG_REG_DDTT
                  ,C.BEFO_PROG_CD
            FROM TB_PT_MSLABCOMM C
			    ,(SELECT  :V_YD_GP            AS YD_GP
                         ,:V_BAY_GP           AS BAY_GP
                         ,:V_YD_EQP_GP        AS YD_EQP_GP
                         ,:V_YD_STK_COL_NO    AS YD_STK_COL_NO
                         ,:V_YD_STK_BED_NO    AS YD_STK_BED_NO
                         ,:V_YD_STK_LYR_NO    AS YD_STK_LYR_NO
                    FROM DUAL) D
            WHERE mslab_no = :V_SLAB_NO
           )
        WHERE mslab_no = :V_SLAB_NO
 </pre> */
public final static String updateMslabCommonLocInfo2 = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonLocInfo2";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delZoneVerIdRule
UPDATE TB_YF_RULE 
   SET DEL_YN ='Y'
 WHERE REPR_CD_GP || CD_GP || ITEM = :V_VER_ID
 </pre> */
public final static String delZoneVerIdRule = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delZoneVerIdRule";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.DeleteUseZoneData
UPDATE TB_YF_SCHLOCSRCH_ZONE
   SET DEL_YN = 'Y'
        ,MODIFIER = :V_MODIFIER
        ,MOD_DDTT = SYSDATE
 WHERE DEL_YN = 'N'
 </pre> */
public final static String DeleteUseZoneData = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.DeleteUseZoneData";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updHeatCoilYn
UPDATE TB_YF_STOCK 
SET 
 MODIFIER           = :V_MODIFIER 
,MOD_DDTT           = SYSDATE
,HEATING_COIL_YN    = :V_HEATING_COIL_YN 
WHERE STL_NO        = :V_STL_NO
 </pre> */
public final static String updHeatCoilYn = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updHeatCoilYn";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarExaminationGoodsDetjl2
UPDATE TB_YD_EXAMINATIONCHKLIST
SET LABEL_YN = NVL((SELECT (CASE WHEN :V_LABEL_YN ='Y' THEN 'Y' 
                             ELSE ( CASE WHEN (Z.FNL_MATCH_ORDERTRANS_OCCURDATE||Z.FNL_MATCH_ORDERTRANS_OCCURTIME) >= to_char(Z.SHEAR_WORD_DT,'YYYYMMDDHH24MISS')
                                            THEN 'Y' 
                                         ELSE FNC_DM_GET_GOODSPROGLABEL_YN @DL_SMDB (STL_NO) 
                                     END)
                          END)
                  FROM TB_PT_COILCOMM Z
                 WHERE Z.COIL_NO = STL_NO),'N')
  , CHECKING_YN ='Y'
  , MODIFIER = NVL(:V_MODIFIER, 'YDPDA2')
  , YD_AB_CD = :V_YD_AB_CD
  , YD_AB_CD2 = :V_YD_AB_CD2
  , MOD_DDTT= SYSDATE
  , YD_CAR_UPP_LOC_CD = NVL(:V_YD_CAR_UPP_LOC_CD,YD_CAR_UPP_LOC_CD)
WHERE TRANS_ORD_DATE=:V_TRANS_ORD_DATE
  AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
  AND STL_NO = :V_STL_NO
 </pre> */
public final static String updateCarExaminationGoodsDetjl2 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarExaminationGoodsDetjl2";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStockLineOff
MERGE INTO TB_YF_STOCK ST USING
(
    SELECT
        :V_STL_NO           AS STL_NO,          --재료번호
        :V_MODIFIER         AS MODIFIER,        --수정자
        SYSDATE             AS MOD_DDTT,        --수정일시
        'N'                 AS DEL_YN,          --삭제유무
        :V_STOCK_ITEM       AS STOCK_ITEM,      --저장품 품목
        :V_STOCK_MOVE_TERM  AS STOCK_MOVE_TERM,  --저장품 이동 조건
        :V_HEATING_COIL_YN  AS HEATING_COIL_YN,
        :V_DC_TKIN_FR_STL_NO AS DC_TKIN_FR_STL_NO
    FROM
        DUAL
) DD
ON ( ST.STL_NO = DD.STL_NO)
WHEN NOT MATCHED THEN
    INSERT
    (
        STL_NO,
        STOCK_ITEM,
        STOCK_MOVE_TERM,
        REGISTER,
        REG_DDTT,
        MODIFIER,
        MOD_DDTT,
        DEL_YN,
        HEATING_COIL_YN,
        DC_TKIN_FR_STL_NO
    )
    VALUES
    (
        :V_STL_NO,
        DD.STOCK_ITEM,
        DD.STOCK_MOVE_TERM,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.DEL_YN,
        DD.HEATING_COIL_YN,
        DD.DC_TKIN_FR_STL_NO
    )
WHEN MATCHED THEN
    UPDATE SET
        --STOCK_ITEM      = (CASE WHEN KEEPSTOCK_STL_YN = 'Y' THEN STOCK_ITEM ELSE DD.STOCK_ITEM END),
        STOCK_ITEM      = DD.STOCK_ITEM,
        STOCK_MOVE_TERM = DD.STOCK_MOVE_TERM,
        MODIFIER        = DD.MODIFIER,
        MOD_DDTT        = DD.MOD_DDTT,
        HEATING_COIL_YN = DD.HEATING_COIL_YN,
        DC_TKIN_FR_STL_NO = DD.DC_TKIN_FR_STL_NO
 </pre> */
public final static String insStockLineOff = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStockLineOff";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insertBackUpZoneData
MERGE 
 INTO TB_YF_SCHLOCSRCH_ZONE_V A
USING (
SELECT Z.YD_STK_COL_GP
      ,Z.YD_LOC_SRCH_RNG_SEQ
      ,Z.YD_AIM_BAY_GP
      ,Z.YD_ZONE_GP
      ,:V_MODIFIER AS MODIFIER
      ,:V_TO_VER_ID AS TO_VER_ID
  FROM TB_YF_SCHLOCSRCH_ZONE Z
 WHERE Z.DEL_YN = 'N'
) Z ON ( A.VER_ID = Z.TO_VER_ID
   AND A.YD_STK_COL_GP = Z.YD_STK_COL_GP)
WHEN MATCHED THEN 
UPDATE SET
    A.DEL_YN = 'N'
   ,A.YD_LOC_SRCH_RNG_SEQ = Z.YD_LOC_SRCH_RNG_SEQ
   ,A.YD_AIM_BAY_GP = Z.YD_AIM_BAY_GP
   ,A.YD_ZONE_GP = Z.YD_ZONE_GP
   ,A.MODIFIER = Z.MODIFIER
   ,A.MOD_DDTT = SYSDATE
WHEN NOT MATCHED THEN
   INSERT  (
                     VER_ID
                    ,YD_STK_COL_GP
                    ,YD_ZONE_GP
                    ,REGISTER
                    ,REG_DDTT
                    ,MODIFIER
                    ,MOD_DDTT
                    ,DEL_YN
                    ,YD_LOC_SRCH_RNG_SEQ
                    ,YD_AIM_BAY_GP
		    ) VALUES (
                     Z.TO_VER_ID
                    ,Z.YD_STK_COL_GP
                    ,Z.YD_ZONE_GP
                    ,Z.MODIFIER
                    ,SYSDATE
                    ,Z.MODIFIER
                    ,SYSDATE
                    ,'N'
                    ,Z.YD_LOC_SRCH_RNG_SEQ
                    ,Z.YD_AIM_BAY_GP
		    ) 
 </pre> */
public final static String insertBackUpZoneData = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insertBackUpZoneData";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.DeleteBackUpZoneData
UPDATE TB_YF_SCHLOCSRCH_ZONE_V
   SET DEL_YN = 'Y'
 WHERE VER_ID = :V_VER_ID
      AND DEL_YN ='N'
 </pre> */
public final static String DeleteBackUpZoneData = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.DeleteBackUpZoneData";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.ApplyBackUpZoneVer
MERGE 
 INTO TB_YF_SCHLOCSRCH_ZONE_V A
USING (
SELECT 
       Z.YD_ZONE_GP
      ,Z.YD_STK_COL_GP
      ,Z.YD_LOC_SRCH_RNG_SEQ
      ,Z.YD_AIM_BAY_GP
      ,:V_MODIFIER AS MODIFIER
      ,:V_TO_VER_ID AS TO_VER_ID
  FROM TB_YF_SCHLOCSRCH_ZONE_V Z
 WHERE Z.VER_ID = :V_FR_VER_ID
   AND Z.DEL_YN = 'N'
) Z ON ( A.VER_ID = Z.TO_VER_ID
   AND A.YD_STK_COL_GP = Z.YD_STK_COL_GP)
WHEN MATCHED THEN 
UPDATE SET
    A.DEL_YN = 'N'
   ,A.YD_LOC_SRCH_RNG_SEQ = Z.YD_LOC_SRCH_RNG_SEQ
   ,A.YD_AIM_BAY_GP = Z.YD_AIM_BAY_GP
   ,A.MODIFIER = Z.MODIFIER
   ,A.MOD_DDTT = SYSDATE
WHEN NOT MATCHED THEN
   INSERT  (
                     YD_ZONE_GP
                    ,VER_ID
                    ,YD_STK_COL_GP
                    ,REGISTER
                    ,REG_DDTT
                    ,MODIFIER
                    ,MOD_DDTT
                    ,DEL_YN
                    ,YD_LOC_SRCH_RNG_SEQ
                    ,YD_AIM_BAY_GP
		    ) VALUES (
                     Z.YD_ZONE_GP
                    ,Z.TO_VER_ID
                    ,Z.YD_STK_COL_GP
                    ,Z.MODIFIER
                    ,SYSDATE
                    ,Z.MODIFIER
                    ,SYSDATE
                    ,'N'
                    ,Z.YD_LOC_SRCH_RNG_SEQ
                    ,Z.YD_AIM_BAY_GP
		    )
 </pre> */
public final static String ApplyBackUpZoneVer = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.ApplyBackUpZoneVer";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASalbDAO.insRetHtHist 
-- 회송이력 테이블 INSERT

INSERT INTO TB_YD_RETHTHIST (
     YD_RETHT_HIST_ID
    ,STL_NO
    ,YD_RETHT_EMPNO
    ,YD_RETHT_REQ_DT
    ,YD_RETHT_RSN_CD
    ,YD_RETHT_RSN_CNTS
    ,YD_RETHT_CMPL_DT
    ,YD_RETHT_STAT_CD
    ,SPOS_WLOC_CD
    ,ARR_WLOC_CD
    ,TRN_EQP_CD
    ,YD_CAR_SCH_ID
    ,REGISTER
    ,REG_DDTT
    ,MODIFIER
    ,MOD_DDTT
    ,DEL_YN
) VALUES (
     :V_YD_RETHT_HIST_ID
    ,:V_STL_NO
    ,:V_YD_RETHT_EMPNO
    ,NVL(TO_DATE(:V_YD_RETHT_REQ_DT,'YYYYMMDDHH24MISS'),SYSDATE)
    ,:V_YD_RETHT_RSN_CD
    ,:V_YD_RETHT_RSN_CNTS
    ,NULL
    ,:V_YD_RETHT_STAT_CD
    ,:V_SPOS_WLOC_CD
    ,:V_ARR_WLOC_CD
    ,:V_TRN_EQP_CD
    ,:V_YD_CAR_SCH_ID
    ,:V_MODIFIER
    ,SYSDATE
    ,:V_MODIFIER
    ,SYSDATE
    ,'N'
)

 </pre> */
public final static String insRetHtHist = "bak.com.inisteel.cim.yf.aslab.dao.ASalbDAO.insRetHtHist";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchAutoStYn 
UPDATE TB_YF_SCHRULE
   SET YD_SCH_AUTO_ST_YN = :V_YD_SCH_AUTO_ST_YN
     , MODIFIER          = :V_MODIFIER
     , MOD_DDTT          = SYSDATE
 WHERE YD_SCH_CD         = :V_YD_SCH_CD
 </pre> */
public final static String updSchAutoStYn = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchAutoStYn";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updYdZoneGp
UPDATE TB_YF_STKCOL A  
SET YD_ZONE_GP = (SELECT YD_ZONE_GP
                  FROM TB_YF_SCHLOCSRCH_ZONE B
                  WHERE A.YD_STK_COL_GP = B.YD_STK_COL_GP
                  AND B.DEL_YN = 'N'                                     
                  )
,MODIFIER = :V_MODIFIER
,MOD_DDTT = SYSDATE
WHERE A.DEL_YN = 'N'
AND A.YD_GP = :V_YD_GP 
AND A.SECT_GP BETWEEN '00' AND '99'
 </pre> */
public final static String updYdZoneGp = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updYdZoneGp";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTrkStackColGplayerTakeOut
MERGE INTO TB_YF_STKLYR SL USING
(
    SELECT
        A.YD_STK_COL_GP,
        '1'         AS YD_STK_COL_GP1,
        A.YD_BAY_GP AS YD_STK_COL_GP2,
        '00'        AS YD_STK_BED_NO,
        '01'        AS YD_STK_LYR_NO,
        'E'         AS YD_STK_LYR_ACTIVE_STAT,
        'C'         AS YD_STK_LYR_STAT,
        A.STL_NO    AS STL_NO
    FROM
        TB_YF_EQPTRACKING  A
    WHERE 1=1
    AND STL_NO = :V_STL_NO
) DD
ON
(
    1=1
    AND SL.YD_STK_COL_GP    = DD.YD_STK_COL_GP
    AND SL.YD_STK_BED_NO    = DD.YD_STK_BED_NO
    AND SL.YD_STK_LYR_NO    = DD.YD_STK_LYR_NO
)
WHEN MATCHED THEN
    UPDATE
    SET
        SL.MODIFIER                 = :V_MODIFIER,
        SL.MOD_DDTT                 = SYSDATE,
        SL.YD_STK_LYR_ACTIVE_STAT   = DD.YD_STK_LYR_ACTIVE_STAT,
        SL.YD_STK_LYR_STAT          = DD.YD_STK_LYR_STAT,
        SL.STL_NO                   = DD.STL_NO
 </pre> */
public final static String updTrkStackColGplayerTakeOut = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTrkStackColGplayerTakeOut";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateCTSSchRank
UPDATE TB_YF_CTS_SCH
   SET YD_CTS_WRK_SEQ = :V_YD_CTS_WRK_SEQ
 WHERE DEL_YN = 'N'
   AND STL_NO = :V_STL_NO

 </pre> */
public final static String updateCTSSchRank = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateCTSSchRank";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStockDC
MERGE INTO TB_YF_STOCK ST USING
(
    SELECT
        :V_STL_NO           AS STL_NO,          --재료번호
        :V_MODIFIER         AS MODIFIER,        --수정자
        SYSDATE             AS MOD_DDTT,        --수정일시
        'N'                 AS DEL_YN,          --삭제유무
        :V_STOCK_ITEM       AS STOCK_ITEM,      --저장품 품목
        :V_STOCK_MOVE_TERM  AS STOCK_MOVE_TERM, --저장품 이동 조건
        :V_LINE_OFF_YN      AS LINE_OFF_YN      --DC LINE OFF 여부
    FROM
        DUAL
) DD
ON ( ST.STL_NO = DD.STL_NO)
WHEN NOT MATCHED THEN
    INSERT
    (
        STL_NO,
        REGISTER,
        REG_DDTT,
        MODIFIER,
        MOD_DDTT,
        DEL_YN,
        STOCK_ITEM,
        STOCK_MOVE_TERM,
        LINE_OFF_YN
    )
    VALUES
    (
        DD.STL_NO,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.DEL_YN,
        DD.STOCK_ITEM,
        DD.STOCK_MOVE_TERM,
        DD.LINE_OFF_YN
    )
WHEN MATCHED THEN
    UPDATE SET
        MODIFIER        = DD.MODIFIER,
        MOD_DDTT        = DD.MOD_DDTT,
        STOCK_ITEM      = DD.STOCK_ITEM,
        STOCK_MOVE_TERM = DD.STOCK_MOVE_TERM,
        LINE_OFF_YN     = DD.LINE_OFF_YN
 </pre> */
public final static String insStockDC = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStockDC";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updWmStrLoc
--크레인스케줄 작업예약재료 저장위치 수정
UPDATE TB_YF_WRKBOOKMTL A
SET ( 
         A.MODIFIER       
        ,A.MOD_DDTT       
        ,A.YD_STK_COL_GP   
        ,A.YD_STK_BED_NO  
        ,A.YD_STK_LYR_NO 
        ,A.YD_UP_COLL_SEQ
     ) = 
     (
         SELECT 
            :V_MODIFIER
           ,SYSDATE
           ,YD_STK_COL_GP
           ,YD_STK_BED_NO
           ,YD_STK_LYR_NO
           ,RANK() OVER(PARTITION BY B.YD_STK_COL_GP,B.YD_STK_BED_NO 
                            ORDER BY B.YD_STK_COL_GP,B.YD_STK_BED_NO,B.YD_STK_LYR_NO) AS YD_UP_COLL_SEQ 
          FROM TB_YF_STKLYR B 
         WHERE A.STL_NO          = B.STL_NO
           AND B.YD_STK_LYR_STAT IN ('C','U') --U추가: TO위치재기동시 트랜잭션 분리에 따른 문제 발생으로 추가함
     )
WHERE A.YD_WBOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String updWmStrLoc = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updWmStrLoc";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delCTSLyr  
UPDATE TB_YF_STKLYR
SET MODIFIER        = :V_MODIFIER
   ,MOD_DDTT        = SYSDATE
   ,STL_NO          = CASE WHEN YD_STK_LYR_STAT = 'D' THEN '' ELSE STL_NO END
   ,YD_STK_LYR_STAT = CASE WHEN YD_STK_LYR_STAT = 'U' THEN 'C' 
                           WHEN YD_STK_LYR_STAT = 'D' THEN 'E' 
                           ELSE YD_STK_LYR_STAT END 
WHERE (YD_STK_COL_GP LIKE '%1_SR%'
       OR 
       YD_STK_COL_GP LIKE '%1_SL%'
      ) 
  AND YD_STK_LYR_STAT IN ('D','U')    
  AND STL_NO IN (SELECT STL_NO 
                   FROM TB_YF_STKLYR 
                  WHERE (YD_STK_COL_GP LIKE '%1_SR%'
                           OR 
                         YD_STK_COL_GP LIKE '%1_SL%')
                  GROUP BY STL_NO
                  HAVING COUNT(STL_NO) > 1
                 )
  AND STL_NO NOT IN (SELECT STL_NO FROM TB_YF_CTS_SCH WHERE DEL_YN ='N')  
 </pre> */
public final static String delCTSLyr = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delCTSLyr";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASalbDAO.uptRetHtHistCmplDt
-- 회송이력 테이블 완료일자 셋팅
UPDATE TB_YD_RETHTHIST
   SET MODIFIER = :V_MODIFIER
	  ,MOD_DDTT = SYSDATE
	  ,YD_RETHT_CMPL_DT = SYSDATE
	  ,YD_RETHT_STAT_CD = '3'
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
   AND STL_NO = :V_STL_NO
 </pre> */
public final static String uptRetHtHistCmplDt = "bak.com.inisteel.cim.yf.aslab.dao.ASalbDAO.uptRetHtHistCmplDt";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.delSchLocSrchZone1
DELETE TB_YF_SCHLOCSRCH_ZONE_V
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND VER_ID = :V_VER_ID
 </pre> */
public final static String delSchLocSrchZone1 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.delSchLocSrchZone1";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YdPlateCommDAO.updateQueryId_0041 
UPDATE TB_YD_CARPOINT
SET    YD_SPAN_FROM = :V_YD_SPAN_FROM 
      ,YD_SPAN_TO   = :V_YD_SPAN_TO
WHERE  YD_CARPNT_CD = :V_YD_CARPNT_CD
 </pre> */
public final static String updateQueryId_0041 = "bak.com.inisteel.cim.yf.common.dao.YdPlateCommDAO.updateQueryId_0041";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insertNewBackUp
    INSERT 
    INTO TB_YF_RULE( REPR_CD_GP
                    ,CD_GP
                    ,ITEM
                    ,REGISTER
                    ,REG_DDTT
                    ,MODIFIER
                    ,MOD_DDTT
                    ,DEL_YN
                    ,REPR_CD_CONTENTS)
        VALUES('YF0007', '1', SUBSTR(:V_VER_ID,8), :V_MODIFIER, SYSDATE, :V_MODIFIER, SYSDATE, 'N', :V_REPR_CD_CONTENTS)
 </pre> */
public final static String insertNewBackUp = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insertNewBackUp";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgt 
--크레인작업관리 크레인변경 크레인스케줄 수정
UPDATE TB_YF_CRNSCH
   SET MODIFIER     = :V_MODIFIER
      ,MOD_DDTT     = SYSDATE
      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
   AND YD_WRK_PROG_STAT IN ('1','W','S')
   AND DEL_YN = 'N'
 </pre> */
public final static String updCrnWrkMgt = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgt";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchLocSrchZone 
MERGE INTO TB_YF_SCHLOCSRCH_ZONE USING (
    SELECT  :V_YD_ZONE_GP            AS V_YD_ZONE_GP
		  , :V_YD_STK_COL_GP         AS V_YD_STK_COL_GP
		  , :V_MODIFIER              AS V_MODIFIER
		  , :V_YD_LOC_SRCH_RNG_SEQ   AS V_YD_LOC_SRCH_RNG_SEQ
      FROM  DUAL
) ON (  
            YD_STK_COL_GP = V_YD_STK_COL_GP
     )
WHEN MATCHED THEN
    UPDATE SET YD_LOC_SRCH_RNG_SEQ = V_YD_LOC_SRCH_RNG_SEQ
             , YD_ZONE_GP = V_YD_ZONE_GP
    		 , MODIFIER = V_MODIFIER
    		 , MOD_DDTT = SYSDATE
	    	 , DEL_YN   = 'N'
WHEN NOT MATCHED THEN
    INSERT  (
		          YD_STK_COL_GP
		        , REGISTER
		        , REG_DDTT
		        , MODIFIER
		        , MOD_DDTT
		        , DEL_YN
		        , YD_LOC_SRCH_RNG_SEQ
                , YD_ZONE_GP
		    ) VALUES (
		          V_YD_STK_COL_GP
		        , V_MODIFIER
		        , SYSDATE
		        , V_MODIFIER
		        , SYSDATE
		        , 'N'
		        , V_YD_LOC_SRCH_RNG_SEQ
                , V_YD_ZONE_GP
		    )
 </pre> */
public final static String updSchLocSrchZone = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchLocSrchZone";

 /** <pre> 
--yf.common.dao.updateLayerState1
UPDATE TB_YF_STKLYR
   SET YD_STK_LYR_STAT = :V_YD_STK_LYR_STAT
 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
   AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updateLayerState1 = "bak.yf.common.dao.updateLayerState1";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYfStkcol2
UPDATE TB_YF_STKCOL
SET  YD_STK_COL_ACTIVE_STAT=:V_YD_STK_COL_ACTIVE_STAT
   , YD_CAR_USE_GP = :V_YD_CAR_USE_GP
   , TRN_EQP_CD=:V_TRN_EQP_CD
   , CAR_NO=:V_CAR_NO
   , CARD_NO =:V_CARD_NO
   , MOD_DDTT= SYSDATE
   , MODIFIER ='맵활성화'
 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
 </pre> */
public final static String updYfStkcol2 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYfStkcol2";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.modifyCardNoOfEND
--TB_YD_CARSCH 차량스케줄 종료 처리
UPDATE USRYDA.TB_YD_CARSCH
SET 
    MODIFIER = :V_MODIFIER, 
    MOD_DDTT = SYSDATE, 
    DEL_YN   = 'Y'
WHERE 1=1
AND DEL_YN   = 'N'
AND CARD_NO  = :V_CARD_NO
 </pre> */
public final static String modifyCardNoOfEND = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.modifyCardNoOfEND";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateCardNoOfStackCol1
--TB_YF_STKCOL 적치열 차량예약 포인트 지우기
UPDATE TB_YF_STKCOL
SET
    CARD_NO       = NULL,
    CAR_CARD_NO   = NULL,
    CAR_NO        = NULL,
    YD_CAR_USE_GP = NULL,
    MODIFIER      = :V_MODIFIER,
    MOD_DDTT      = SYSDATE
WHERE 1=1
AND CARD_NO       = :V_CARD_NO
 </pre> */
public final static String updateCardNoOfStackCol1 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateCardNoOfStackCol1";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateArrDt4
UPDATE TB_YD_CARSCH
SET
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    YD_CARLD_WRK_BOOK_ID    = :V_YD_CARLD_WRK_BOOK_ID,
    ARR_WLOC_CD             = :V_ARR_WLOC_CD,
    YD_PNT_CD1              = :V_YD_PNT_CD1,
    YD_CARLD_STOP_LOC       = :V_YD_CARLD_STOP_LOC,
    YD_CARLD_ARR_DT         = SYSDATE,
    YD_CAR_PROG_STAT        = '2',  --상차도착
    FRTOMOVE_WORD_NO        = :V_FRTOMOVE_WORD_NO
WHERE 1=1
AND TRN_EQP_CD              = :V_TRN_EQP_CD
AND DEL_YN                  = 'N'
 </pre> */
public final static String updateArrDt4 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateArrDt4";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insWrkBook2
--작업예약 등록
INSERT INTO TB_YF_WRKBOOK
(
    YD_WBOOK_ID,        --야드작업예약ID
    YD_GP,              --야드구분
    YD_BAY_GP,          --야드동구분
    YD_SCH_CD,          --야드스케쥴코드
    YD_SCH_PRIOR,       --야드스케쥴우선순위
    YD_SCH_PROG_STAT,   --야드스케쥴진행상태
    YD_SCH_ST_GP,       --야드스케쥴기동구분
    YD_SCH_REQ_GP,      --야드스케쥴요청구분
    YD_AIM_YD_GP,       --야드목표야드구분
    YD_AIM_BAY_GP,      --야드목표동구분
    YD_TO_LOC_DCSN_MTD, --야드To위치결정방법
    YD_TO_LOC_GUIDE,    --야드To위치Guide
    YD_WRK_PLAN_TCAR,   --야드작업계획대차
    YD_CAR_USE_GP,      --야드차량사용구분
    TRN_EQP_CD,         --운송장비코드
    CAR_NO,             --차량번호
    CARD_NO,            --카드번호
    PTOP_PLNT_GP,       --조업공장구분
    DEST_TEL_NO,        --목적지전화번호
    DIST_SHIPASSIGN_GP, --출하배선지시구분
    YD_WRK_PLAN_CRN,    --야드작업계획크레인
    REGISTER,           --등록자
    REG_DDTT,           --등록일시
    MODIFIER,           --수정자
    MOD_DDTT,           --수정일시
    DEL_YN,             --삭제유무
    YD_TO_LOC_GUIDE_FNL
)
VALUES
(
    :V_YD_WBOOK_ID,
    :V_YD_GP,
    :V_YD_BAY_GP,
    :V_YD_SCH_CD,
    TO_NUMBER(:V_YD_SCH_PRIOR),
    :V_YD_SCH_PROG_STAT,
    :V_YD_SCH_ST_GP,
    :V_YD_SCH_REQ_GP,
    :V_YD_AIM_YD_GP,
    :V_YD_AIM_BAY_GP,
    :V_YD_TO_LOC_DCSN_MTD,
    :V_YD_TO_LOC_GUIDE,
    :V_YD_WRK_PLAN_TCAR,
    :V_YD_CAR_USE_GP,
    :V_TRN_EQP_CD,
    :V_CAR_NO,
    :V_CARD_NO,
    :V_PTOP_PLNT_GP,
    :V_DEST_TEL_NO,
    :V_DIST_SHIPASSIGN_GP,
    :V_YD_WRK_PLAN_CRN,
    :V_MODIFIER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N',
    :V_YD_TO_LOC_GUIDE_FNL
)
 </pre> */
public final static String insWrkBook2 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insWrkBook2";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updClrLyrByStockId
UPDATE TB_YF_STKLYR
SET
    STL_NO                  = NULL,
    YD_STK_LYR_ACTIVE_STAT  = 'E',
    YD_STK_LYR_STAT         = 'E',
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE
WHERE 1=1
AND STL_NO                  = :V_STL_NO
 </pre> */
public final static String updClrLyrByStockId = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updClrLyrByStockId";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarSchUdByTrnEqpCd
UPDATE TB_YD_CARSCH A
SET
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    YD_CAR_PROG_STAT        = :V_YD_CAR_PROG_STAT,
    YD_CAR_USE_GP           = :V_YD_CAR_USE_GP,
    YD_EQP_WRK_STAT         = :V_YD_EQP_WRK_STAT,
    ARR_WLOC_CD             = :V_ARR_WLOC_CD,
    YD_PNT_CD3              = :V_YD_PNT_CD,
    YD_CARUD_STOP_LOC       = :V_YD_CARUD_STOP_LOC,
    YD_CARUD_WRK_BOOK_ID    = :V_YD_CARUD_WRK_BOOK_ID,
    YD_CARUD_LEV_DT         = SYSDATE,
    YD_CARUD_PNT_WO_DT      = SYSDATE
WHERE 1=1
AND TRN_EQP_CD              = :V_TRN_EQP_CD
AND DEL_YN                  = 'N'
AND A.YD_CAR_SCH_ID         =
(
    SELECT
        MAX(YD_CAR_SCH_ID)
    FROM
        TB_YD_CARSCH B
    WHERE 1=1
    AND A.TRN_EQP_CD    = B.TRN_EQP_CD
    AND B.DEL_YN        = 'N'
)
 </pre> */
public final static String updCarSchUdByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarSchUdByTrnEqpCd";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgt0 
--크레인작업관리 크레인변경 크레인스케줄 수정
UPDATE TB_YF_CRNSCH
   SET MODIFIER     = :V_MODIFIER
     , MOD_DDTT     = SYSDATE
     , YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
     , YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
   AND YD_WRK_PROG_STAT IN ('1','W','S', '2')
   AND DEL_YN = 'N'
 </pre> */
public final static String updCrnWrkMgt0 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgt0";

 /** <pre> 
--yf.steelinfo.steelinforecv.dao.YdStockDAO.updateStockTransInfo_02
UPDATE TB_YF_STOCK
SET
    CAR_CARD_NO         = :V_CAR_CARD_NO,
    FRTOMOVE_WORD_NO    = :V_FRTOMOVE_WORD_NO,
    TRANS_ORD_DATE      = SUBSTR(:V_TRANS_WORD_NO, 1, 8),
    TRANS_ORD_SEQNO     = SUBSTR(:V_TRANS_WORD_NO, 9, 1),
    STOCK_MOVE_TERM     = :V_STOCK_MOVE_TERM,
    MODIFIER            = 'SYSTEM',
    MOD_DDTT            = SYSDATE
WHERE 1=1
AND STL_NO              = :V_STL_NO
 </pre> */
public final static String updateStockTransInfo_02 = "bak.yf.steelinfo.steelinforecv.dao.YdStockDAO.updateStockTransInfo_02";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.updateStackLayerStatWithStockId
UPDATE TB_YF_STKLYR
SET	
    YD_STK_LYR_STAT = :YD_STK_LYR_STAT
WHERE 1=1
AND STL_NO          = :STL_NO
AND YD_STK_LYR_STAT IN ('L', 'S', 'U')
 </pre> */
public final static String updateStackLayerStatWithStockId = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.updateStackLayerStatWithStockId";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelYnWrkBook 
--작업예약 삭제
UPDATE TB_YF_WRKBOOK
SET 
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE,
    DEL_YN      = 'Y'
WHERE 1=1
AND YD_WBOOK_ID = :V_YD_WBOOK_ID
AND DEL_YN      = 'N'
 </pre> */
public final static String updDelYnWrkBook = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelYnWrkBook";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayerStat
UPDATE TB_YF_STKLYR            
   SET MOD_DDTT             = SYSDATE             
     , MODIFIER             = :V_MODIFIER             
     , STL_NO				= :V_STL_NO
	 , YD_STK_LYR_STAT	    = :V_YD_STK_LYR_STAT
 WHERE YD_STK_COL_GP     = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO     = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO   = :V_YD_STK_LYR_NO
 </pre> */
public final static String updStackLayerStat = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayerStat";

 /** <pre> 
--yf.common.dao.updateMoveEquipOfStock2
--'이송 설비 구분', '이송 설비 BED 구분', '이송 설비 단 구분', '저장품이동조건'을 UPDATE
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM = ?
WHERE 1=1
AND STL_NO          = ?
 </pre> */
public final static String updateMoveEquipOfStock2 = "bak.yf.common.dao.updateMoveEquipOfStock2";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updydcarpoint
UPDATE USRYDA.TB_YD_CARPOINT
SET MODIFIER=:V_MODIFIER
  , MOD_DDTT=SYSDATE
  , YD_STK_COL_ACT_STAT=:V_YD_STK_COL_ACT_STAT
  , YD_CAR_USETYPE_GP=:V_YD_STKBED_USG_CD
  , TRN_EQP_CD=:V_TRN_EQP_CD
  , CAR_NO=:V_CAR_NO
  , CARD_NO=:V_CARD_NO
  ,YD_FRM_YN=:V_YD_FRM_YN
WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
 </pre> */
public final static String updydcarpoint = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updydcarpoint";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updYdCrnWrkMtl
UPDATE TB_YF_CRNWRKMTL
SET MODIFIER        = :V_MODIFIER
   ,MOD_DDTT        = SYSDATE
   ,YD_ROUTE_GP     = :V_YD_ROUTE_GP
WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
  AND STL_NO        = :V_STL_NO
 </pre> */
public final static String updYdCrnWrkMtl = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updYdCrnWrkMtl";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.insWrkBookSlab
--작업예약 등록
INSERT INTO TB_YF_WRKBOOK
(
    YD_WBOOK_ID,            --야드작업예약ID
    YD_GP,                  --야드구분
    YD_BAY_GP,              --야드동구분
    YD_SCH_CD,              --야드스케쥴코드
    YD_SCH_PRIOR,           --야드스케쥴우선순위
    YD_SCH_PROG_STAT,       --야드스케쥴진행상태
    YD_SCH_ST_GP,           --야드스케쥴기동구분
    YD_SCH_REQ_GP,          --야드스케쥴요청구분
    YD_AIM_YD_GP,           --야드목표야드구분
    YD_AIM_BAY_GP,          --야드목표동구분
    YD_TO_LOC_DCSN_MTD,     --야드To위치결정방법
    YD_TO_LOC_GUIDE,        --야드To위치Guide
    YD_WRK_PLAN_TCAR,       --야드작업계획대차
    YD_CAR_USE_GP,          --야드차량사용구분
    TRN_EQP_CD,             --운송장비코드
    CAR_NO,                 --차량번호
    CARD_NO,                --카드번호
    PTOP_PLNT_GP,           --조업공장구분
    DEST_TEL_NO,            --목적지전화번호
    DIST_SHIPASSIGN_GP,     --출하배선지시구분
    YD_WRK_PLAN_CRN,        --야드작업계획크레인
    YD_WRK_PLAN_CRN2,       --야드작업계획크레인
    REGISTER,               --등록자
    REG_DDTT,               --등록일시
    MODIFIER,               --수정자
    MOD_DDTT,               --수정일시
    DEL_YN,                 --삭제유무
    CHARGE_LOT_NO_DIV_YN    --장입순번분리여부
)
VALUES
(
    :V_YD_WBOOK_ID,
    :V_YD_GP,
    :V_YD_BAY_GP,
    :V_YD_SCH_CD,
    TO_NUMBER(:V_YD_SCH_PRIOR),
    :V_YD_SCH_PROG_STAT,
    :V_YD_SCH_ST_GP,
    :V_YD_SCH_REQ_GP,
    :V_YD_AIM_YD_GP,
    :V_YD_AIM_BAY_GP,
    :V_YD_TO_LOC_DCSN_MTD,
    :V_YD_TO_LOC_GUIDE,
    :V_YD_WRK_PLAN_TCAR,
    :V_YD_CAR_USE_GP,
    :V_TRN_EQP_CD,
    :V_CAR_NO,
    :V_CARD_NO,
    :V_PTOP_PLNT_GP,
    :V_DEST_TEL_NO,
    :V_DIST_SHIPASSIGN_GP,
    :V_YD_WRK_PLAN_CRN,
    :V_YD_WRK_PLAN_CRN2,
    :V_MODIFIER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N',
    NVL(:V_CHARGE_LOT_NO_DIV_YN,'Y')
)
 </pre> */
public final static String insWrkBookSlab = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.insWrkBookSlab";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.insYfWrkBookMtlSlab
INSERT INTO TB_YF_WRKBOOKMTL 
(
    YD_WBOOK_ID,
    STL_NO,  
    REGISTER,
    REG_DDTT,
    DEL_YN,
    YD_STK_COL_GP,  
    YD_STK_BED_NO, 
    YD_STK_LYR_NO,  
    YD_UP_COLL_SEQ,
    YD_ISPTOR,
    YD_TAKE_OUT_DT,
    YD_TAKE_OUT_CD,
    MTL_YD_TO_LOC_GUIDE
)
VALUES 
(
    :V_YD_WBOOK_ID,
    :V_STL_NO,
    :V_MODIFIER,
    SYSDATE,
    'N',
    :V_YD_STK_COL_GP,
    :V_YD_STK_BED_NO,
    :V_YD_STK_LYR_NO,
    :V_YD_UP_COLL_SEQ,
    :V_YD_ISPTOR,
    :V_YD_TAKE_OUT_DT,
    :V_YD_TAKE_OUT_CD,
    :V_MTL_YD_TO_LOC_GUIDE
)
 </pre> */
public final static String insYfWrkBookMtlSlab = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.insYfWrkBookMtlSlab";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCTSSchBackUp
INSERT INTO TB_YF_CTS_SCH
(
 YD_CTS_SCH_ID
,REGISTER
,REG_DDTT
,MODIFIER
,MOD_DDTT
,DEL_YN
,YD_EQP_ID
,YD_CTS_WRK_SEQ
,STL_NO
,YD_WRK_PROG_STAT
,YD_WBOOK_ID
,YD_AIM_BAY_GP
,YD_CTS_RELAY_YN
,YD_CTS_RELAY_BAY_GP
,YD_CARLD_WO_LOC --야드상차지시위치
--,YD_CARUD_WO_LOC --야드하차지시위치
--,YD_WORD_DT
)
(
    SELECT     
     TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') || TO_CHAR(USRYFA.YF_CTS_SCH_SEQ.NEXTVAL,'FM000000') AS SEQ_ID
    ,'BACKUP'
    ,SYSDATE
    ,:V_MODIFIER
    ,SYSDATE 
    ,'N'
    ,AA.YD_EQP_ID
    ,1 AS YD_CTS_WRK_SEQ
    ,AA.STL_NO
    ,'W' AS YD_WRK_PROG_STAT
    ,(SELECT MAX(A.YD_WBOOK_ID) AS YD_WBOOK_ID
        FROM TB_YF_WRKBOOK A
            ,TB_YF_WRKBOOKMTL B
       WHERE A.YD_WBOOK_ID = B.YD_WBOOK_ID
         AND B.STL_NO = AA.STL_NO
         AND A.YD_AIM_BAY_GP IS NOT NULL
     ) AS YD_WBOOK_ID
    ,AA.YD_AIM_BAY_GP
    ,DECODE(AA.YD_CTS_RELAY_BAY_GP,'X','N','Y') AS YD_CTS_RELAY_YN --기준과 관계없이 중계국 사용 유무 판단한 결과 
    ,AA.YD_CTS_RELAY_BAY_GP
    ,AA.YD_CARLD_WO_LOC --상차지시위치
   
    FROM
    (
        SELECT 
                MAX(B.YD_CTS_RELAY_BAY_GP)  AS YD_CTS_RELAY_BAY_GP --중계국
               ,MAX(B.YD_EQP_ID)            AS YD_EQP_ID 
               ,MAX(B.YD_CARLD_WO_LOC)      AS YD_CARLD_WO_LOC
               ,MAX(B.YD_AIM_BAY_GP)        AS YD_AIM_BAY_GP
               ,MAX(B.STL_NO)               AS STL_NO
        FROM TB_YF_RULE A
            ,(SELECT PARAM.YD_CARLD_WO_LOC
                    ,PARAM.YD_AIM_BAY_GP
                    ,DECODE(C.DTL_ITEM1,'X','N','Y') AS YD_CTS_RELAY_YN --기준에 SETTING 된 중계 사용 유무
                    
                    ,CASE WHEN C.DTL_ITEM1 = 'X' THEN 'X' --중계국 사용 안함
                          WHEN (
                                (PARAM.YD_CURR_BAY_GP < C.DTL_ITEM1 AND C.DTL_ITEM1 < PARAM.YD_AIM_BAY_GP) --현재동<중계동<목적동
                                OR
                                (PARAM.YD_CURR_BAY_GP > C.DTL_ITEM1 AND C.DTL_ITEM1 > PARAM.YD_AIM_BAY_GP) --현재동>중계동>목적동
                               )
                          THEN C.DTL_ITEM1 --중계국이 필요한 경우
                          ELSE 'X' --중계국 사용할 필요가 없는 경우
                          END AS YD_CTS_RELAY_BAY_GP --중계국     
                         
                    ,CASE WHEN C.DTL_ITEM1 = 'X'                  THEN (SELECT MIN(YD_EQP_ID) FROM TB_YF_EQP WHERE YD_EQP_ID IN ('1XTC01','1XTC02') AND YD_EQP_PROG_STAT !='B') --중계국 없으면 1번 대차만 사용
                          WHEN C.DTL_ITEM1 > PARAM.YD_CURR_BAY_GP THEN '1XTC01' --상차동이 중계국보다 작으면 1번 대차
                          WHEN C.DTL_ITEM1 < PARAM.YD_CURR_BAY_GP THEN '1XTC02' --상차동이 중계국보다 크면   2번 대차
                          WHEN C.DTL_ITEM1 = PARAM.YD_CURR_BAY_GP AND C.DTL_ITEM1 > PARAM.YD_AIM_BAY_GP THEN '1XTC01' 
                          WHEN C.DTL_ITEM1 = PARAM.YD_CURR_BAY_GP AND C.DTL_ITEM1 < PARAM.YD_AIM_BAY_GP THEN '1XTC02' 
                          END AS YD_EQP_ID
                    ,PARAM.STL_NO  
                FROM TB_YF_RULE C   --중계국 기준
                    ,(SELECT YD_STK_COL_GP                  AS YD_CARLD_WO_LOC 
                            ,SUBSTR(YD_STK_COL_GP,2,1)      AS YD_CURR_BAY_GP
                            ,:V_YD_AIM_BAY_GP AS YD_AIM_BAY_GP
                            ,STL_NO
                        FROM TB_YF_STKLYR
                       WHERE STL_NO = :V_STL_NO) PARAM
               WHERE C.REPR_CD_GP = 'PRI007'
             ) B
        WHERE A.DTL_ITEM1 = SUBSTR(B.YD_CARLD_WO_LOC,2,1)
          AND A.DTL_ITEM2 = DECODE(B.YD_CTS_RELAY_BAY_GP,'X',B.YD_AIM_BAY_GP,B.YD_CTS_RELAY_BAY_GP)
          AND A.REPR_CD_GP LIKE 'PRI%'
    ) AA
)
 </pre> */
public final static String insCTSSchBackUp = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCTSSchBackUp";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.deleteCTSSch2
--CTS스케줄 삭제 
UPDATE TB_YF_CTS_SCH 
SET MODIFIER        = :V_MODIFIER
  , MOD_DDTT        = SYSDATE
  , DEL_YN          = 'Y'
WHERE DEL_YN        = 'N'
  AND STL_NO        = :V_STL_NO
 </pre> */
public final static String deleteCTSSch2 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.deleteCTSSch2";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YfCommonDAO.updateStackLayerStat
UPDATE TB_YF_STKLYR
SET STL_NO				= :V_STL_NO,
	YD_STK_LYR_STAT	    = :V_YD_STK_LYR_STAT,
	modifier   = 'SYSTEM',
 	mod_ddtt   = sysdate     
WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP 
AND   YD_STK_BED_NO   = :V_YD_STK_BED_NO 
AND   YD_STK_LYR_NO = :V_YD_STK_LYR_NO 
 </pre> */
public final static String updateStackLayerStat = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.updateStackLayerStat";

 /** <pre> 
--yf.common.dao.updateWBookAndLoadSchOfEquip
--차량의 작업시작과 스케쥴종류를 UPDATE
UPDATE TB_YF_EQUIP
SET
    WBOOK_ID                = ?,
    CARLOAD_SCH_WORK_KIND   = ?
WHERE 1=1
AND EQUIP_GP                = ?
 </pre> */
public final static String updateWBookAndLoadSchOfEquip = "bak.yf.common.dao.updateWBookAndLoadSchOfEquip";

 /** <pre> 
--yf.common.dao.updateCardNoOfStackCol
--차량의 카드번호를 UPDATE
UPDATE TB_YF_STKCOL
SET
    CARD_NO         = ?,
    CAR_CARD_NO     = NULL,
    CAR_NO          = NULL
WHERE 1=1
AND YD_STK_COL_GP   = ?
 </pre> */
public final static String updateCardNoOfStackCol = "bak.yf.common.dao.updateCardNoOfStackCol";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updYdWorkDt
UPDATE TB_YF_CRNSCH
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , YD_WORD_DT       = SYSDATE
 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
   AND DEL_YN           = 'N'
   AND YD_WRK_PROG_STAT IN ('S', '1')
 </pre> */
public final static String updYdWorkDt = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updYdWorkDt";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnSchW
UPDATE TB_YF_CRNSCH A
   SET YD_WRK_PROG_STAT =:V_YD_WRK_PROG_STAT
     , YD_WORD_DT       = NULL
     , MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR 
                             FROM USRYFA.TB_YF_SCHRULE B
                            WHERE B.YD_SCH_CD = A.YD_SCH_CD)
 WHERE DEL_YN         = 'N'
   AND YD_EQP_ID      = :V_YD_EQP_ID
   AND YD_CRN_SCH_ID != :V_YD_CRN_SCH_ID
 </pre> */
public final static String updCrnSchW = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnSchW";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updStatCrnSchWrkProg
--크레인스케줄 작업진행상태 수정
UPDATE TB_YF_CRNSCH
   SET MODIFIER         = :V_MODIFIER
      ,MOD_DDTT         = SYSDATE
      ,YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
      ,YD_WORD_DT       = DECODE(:V_YD_WRK_PROG_STAT,'S',SYSDATE,'1',SYSDATE,'W',NULL,YD_WORD_DT)
      ,YD_L2_REQUEST_STAT = NVL(:V_YD_L2_REQUEST_STAT,YD_L2_REQUEST_STAT) 
 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
   AND DEL_YN           = 'N'
 </pre> */
public final static String updStatCrnSchWrkProg = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStatCrnSchWrkProg";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStock
MERGE INTO TB_YF_STOCK ST USING
(
    SELECT
        :V_STL_NO           AS STL_NO,          --재료번호
        :V_MODIFIER         AS MODIFIER,        --수정자
        SYSDATE             AS MOD_DDTT,        --수정일시
        'N'                 AS DEL_YN,          --삭제유무
        :V_STOCK_ITEM       AS STOCK_ITEM,      --저장품 품목
        :V_STOCK_MOVE_TERM  AS STOCK_MOVE_TERM,  --저장품 이동 조건
        :V_HEATING_COIL_YN  AS HEATING_COIL_YN
    FROM
        DUAL
) DD
ON ( ST.STL_NO = DD.STL_NO)
WHEN NOT MATCHED THEN
    INSERT
    (
        STL_NO,
        STOCK_ITEM,
        STOCK_MOVE_TERM,
        REGISTER,
        REG_DDTT,
        MODIFIER,
        MOD_DDTT,
        DEL_YN,
        HEATING_COIL_YN
    )
    VALUES
    (
        :V_STL_NO,
        DD.STOCK_ITEM,
        DD.STOCK_MOVE_TERM,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.DEL_YN,
        DD.HEATING_COIL_YN
    )
WHEN MATCHED THEN
    UPDATE SET
        --STOCK_ITEM      = (CASE WHEN KEEPSTOCK_STL_YN = 'Y' THEN STOCK_ITEM ELSE DD.STOCK_ITEM END),
        STOCK_ITEM      = DD.STOCK_ITEM,
        STOCK_MOVE_TERM = DD.STOCK_MOVE_TERM,
        MODIFIER        = DD.MODIFIER,
        MOD_DDTT        = DD.MOD_DDTT,
        HEATING_COIL_YN = DD.HEATING_COIL_YN
 </pre> */
public final static String insStock = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStock";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateEquipcolStat
UPDATE TB_YF_STKCOL
SET 
    YD_STK_STAT     = :V_YD_STK_STAT,
    CAR_CARD_NO     = :V_CAR_CARD_NO,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
 </pre> */
public final static String updateEquipcolStat = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateEquipcolStat";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarSchLdByTrnEqpCd
UPDATE TB_YD_CARSCH A
SET
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    YD_CAR_PROG_STAT        = :V_YD_CAR_PROG_STAT,
    YD_CAR_USE_GP           = :V_YD_CAR_USE_GP,
    YD_EQP_WRK_STAT         = :V_YD_EQP_WRK_STAT,
    SPOS_WLOC_CD            = :V_SPOS_WLOC_CD,
    YD_PNT_CD1              = :V_YD_PNT_CD,
    YD_CARLD_STOP_LOC       = :V_YD_CARLD_STOP_LOC,
    YD_CARLD_WRK_BOOK_ID    = :V_YD_CARLD_WRK_BOOK_ID,
    YD_CARLD_LEV_DT         = SYSDATE,
    YD_CARLD_PNT_WO_DT      = SYSDATE
WHERE 1=1
AND TRN_EQP_CD              = :V_TRN_EQP_CD
AND DEL_YN                  = 'N'
AND A.YD_CAR_SCH_ID         =
(
    SELECT
        MAX(YD_CAR_SCH_ID)
    FROM
        TB_YD_CARSCH B
    WHERE 1=1
    AND A.TRN_EQP_CD        = B.TRN_EQP_CD
    AND B.DEL_YN            = 'N'
)
 </pre> */
public final static String updCarSchLdByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarSchLdByTrnEqpCd";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateYfStockYdcarUppLocCd 
UPDATE TB_YF_STOCK
  SET MODIFIER = :V_MODIFIER
        ,MOD_DDTT = SYSDATE
        ,YD_CAR_UPP_LOC_CD = :V_YD_CAR_UPP_LOC_CD
WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String updateYfStockYdcarUppLocCd = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateYfStockYdcarUppLocCd";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updCrnWrkMgt1
UPDATE TB_YF_CRNSCH
   SET MODIFIER     = :V_MODIFIER
      ,MOD_DDTT     = SYSDATE
      ,YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
      ,YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
      ,YD_WRK_PROG_STAT = 'S' 
      ,YD_WORD_DT   = SYSDATE 
 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
   AND YD_WRK_PROG_STAT IN ('1','W','S')
   AND DEL_YN = 'N'
 </pre> */
public final static String updCrnWrkMgt1 = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updCrnWrkMgt1";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updbcoilCrnSchPrior
UPDATE TB_YF_CRNSCH
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , YD_SCH_PRIOR = :V_YD_SCH_PRIOR 
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updbcoilCrnSchPrior = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updbcoilCrnSchPrior";

 /** <pre> 
--com.inisteel.cim.yf.dao.YfCommDAO.updTB_YF_STACKCOL
--적치열 상태 비활성화
UPDATE TB_YF_STKCOL
SET
    YD_STK_COL_ACTIVE_STAT  = ?,
    YD_CAR_USE_GP           = ?,
    TRN_EQP_CD              = ?,
    CAR_NO                  = ?,
    MODIFIER                = ?,
    MOD_DDTT                = SYSDATE
WHERE 1=1
AND YD_STK_COL_GP           = ?
 </pre> */
public final static String updTB_YF_STACKCOL = "bak.com.inisteel.cim.yf.dao.YfCommDAO.updTB_YF_STACKCOL";

 /** <pre> 
--com.inisteel.cim.yf.dao.YfCommDAO.updTB_YF_STACKER
--적치베드 상태 비활성화등록
UPDATE TB_YF_STKBED
SET
    YD_STK_BED_ACTIVE_STAT  = ?,
    YD_STK_BED_WT_MAX       = ?,
    MODIFIER                = ?,
    MOD_DDTT                = SYSDATE
WHERE 1=1
AND YD_STK_COL_GP           = ?
 </pre> */
public final static String updTB_YF_STACKER = "bak.com.inisteel.cim.yf.dao.YfCommDAO.updTB_YF_STACKER";

 /** <pre> 
--com.inisteel.cim.yf.dao.YfCommDAO.updTB_YF_STACKLAYER
--적치단 상태 비활성화
UPDATE TB_YF_STKLYR
SET
    YD_STK_LYR_ACTIVE_STAT  = ?,
    YD_STK_LYR_STAT         = ?,
    STL_NO                  = ?,
    MODIFIER                = ?,
    MOD_DDTT                = SYSDATE
WHERE 1=1
AND YD_STK_COL_GP           = ?
 </pre> */
public final static String updTB_YF_STACKLAYER = "bak.com.inisteel.cim.yf.dao.YfCommDAO.updTB_YF_STACKLAYER";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updStackLayerByStockId   
UPDATE TB_YF_STKLYR
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , STL_NO			= ''
     , YD_STK_LYR_STAT	= 'E'
WHERE STL_NO   = :V_STL_NO
 </pre> */
public final static String updStackLayerByStockId = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStackLayerByStockId";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insYfWrkBookMtl
INSERT INTO TB_YF_WRKBOOKMTL 
(
    YD_WBOOK_ID,
    STL_NO,
    REGISTER,
    REG_DDTT,
    DEL_YN,
    YD_STK_COL_GP,
    YD_STK_BED_NO,
    YD_STK_LYR_NO,
    YD_UP_COLL_SEQ,
    YD_ISPTOR,
    YD_TAKE_OUT_DT,
    YD_TAKE_OUT_CD
)
VALUES
(
    :V_YD_WBOOK_ID,
    :V_STL_NO,
    :V_MODIFIER,
    SYSDATE,
    'N',
    :V_YD_STK_COL_GP,
    :V_YD_STK_BED_NO,
    :V_YD_STK_LYR_NO,
    :V_YD_UP_COLL_SEQ,
    :V_YD_ISPTOR,
    :V_YD_TAKE_OUT_DT,
    :V_YD_TAKE_OUT_CD
)
 </pre> */
public final static String insYfWrkBookMtl = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insYfWrkBookMtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.setStackLayer
UPDATE TB_YF_STKLYR
SET 
    STL_NO          = :V_STL_NO,
    YD_STK_LYR_STAT = 'C',
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
AND YD_STK_LYR_NO   = :V_YD_STK_LYR_NO
 </pre> */
public final static String setStackLayer = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.setStackLayer";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.insertCraneWrsltSanJuk
INSERT INTO TB_YF_WRKHIST
(
    YD_WRK_HIST_ID,
    REGISTER,
    REG_DDTT,
    MODIFIER,
    MOD_DDTT,
    DEL_YN,
    YD_GP,
    STL_NO,
    STL_APPEAR_GP,
    ITEMNAME_CD,
    ORD_YEOJAE_GP,
    ORD_NO,
    ORD_DTL,
    SPEC_ABBSYM,
    CUST_CD,
    DEMANDER_CD,
    YD_MTL_T,
    YD_MTL_W,
    YD_MTL_L,
    YD_MTL_WT,
    COIL_OUTDIA,
    HCR_GP,
    YD_EQP_ID,
    YD_CRN_SCH_ID,
    YD_SCH_CD,
    YD_SCH_DT,
    YD_WRK_DUTY,
    YD_WRK_PARTY,
    CRANE_WORD_UP_LOC,
    CRANE_WORD_PUT_LOC,
    CRANE_WRSLT_UP_LOC,
    CRANE_WRSLT_UP_FUNC,
    CRANE_WRSLT_UP_DDTT,
    CRANE_WRSLT_PUT_LOC,
    CRANE_WRSLT_PUT_FUNC,
    CRANE_WRSLT_PUT_DDTT
)
SELECT
    TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI') || YF_WRKHIST_SEQ.NEXTVAL AS YD_WRK_HIST_ID,
    :V_REGISTER AS REGISTER,
    SYSDATE AS REG_DDTT,
    :V_MODIFIER AS MODIFIER,
    SYSDATE AS MOD_DDTT,
    'N' AS DEL_YN,
    :V_YD_GP AS YD_GP,
    COIL_NO AS STL_NO,
    STL_APPEAR_GP,
    ITEMNAME_CD,
    ORD_YEOJAE_GP,
    ORD_NO,
    ORD_DTL,
    SPEC_ABBSYM,
    CUST_CD,
    DEMANDER_CD,
    COIL_T AS YD_MTL_T,
    COIL_W AS YD_MTL_W,
    COIL_LEN AS YD_MTL_L,
    COIL_WT AS YD_MTL_WT,
    COIL_OUTDIA,
    HCR_GP,
    :V_YD_EQP_ID,
    :V_YD_CRN_SCH_ID,
    :V_YD_SCH_CD,
    SYSDATE AS YD_SCH_DT,
    :V_YD_WRK_DUTY,
    :V_YD_WRK_PARTY,
    :V_CRANE_WORD_UP_LOC,
    :V_CRANE_WORD_PUT_LOC,
    :V_CRANE_WRSLT_UP_LOC,
    :V_CRANE_WRSLT_UP_FUNC,
    TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
    :V_CRANE_WRSLT_PUT_LOC,
    :V_CRANE_WRSLT_PUT_FUNC,
    TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')
FROM
    TB_PT_COILCOMM
WHERE 1=1
AND COIL_NO = :V_STL_NO
 </pre> */
public final static String insertCraneWrsltSanJuk = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insertCraneWrsltSanJuk";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.updateCoilCommonLocInfo
UPDATE TB_PT_COILCOMM
SET
    (  
        YD_GP,                  --야드구분
        YD_BAY_GP,              --동
        YD_EQP_GP,              --SPAN
        YD_STK_COL_NO,          --적치열번지
        YD_STK_BED_NO,          --적치번지
        YD_STK_LYR_NO,          --적치단
        YD_STR_LOC,             --현 저장위치코드
        YD_STR_LOC_HIS1,        --전 저장위치코드
        YD_STR_LOC_HIS2         --전전 저장위치코드
   ) 
   =
   (
        SELECT 
            substr(:pos, 1, 1), --야드구분
            substr(:pos, 2, 1), --동
            substr(:pos, 3, 2), --SPAN
            substr(:pos, 5, 2), --적치열번지
            substr(:pos, 7, 2), --적치번지
            substr(:pos, 9, 2), --적치단
            :pos,               --현 저장위치코드   
            YD_STR_LOC,         --전현 저장위치코드
            YD_STR_LOC_HIS1     --전전현 저장위치코드
        FROM 
            TB_PT_COILCOMM
        WHERE 1=1
        AND COIL_NO = :coil_no
   )
WHERE 1=1
AND COIL_NO = :coil_no
 </pre> */
public final static String updateCoilCommonLocInfo = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.updateCoilCommonLocInfo";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkBookMtlByStkLyr
--적재위치대상재 작업예약재료 등록 
MERGE INTO TB_YF_WRKBOOKMTL WM USING (
SELECT :V_YD_WBOOK_ID   AS YD_WBOOK_ID --야드작업예약ID
     , SL.STL_NO                     --재료번호
     , :V_MODIFIER      AS MODIFIER    --수정자
     , SYSDATE          AS MOD_DDTT    --수정일시
     , 'N'              AS DEL_YN      --삭제유무
     , SL.YD_STK_COL_GP                 --야드적치열구분
     , SL.YD_STK_BED_NO                 --야드적치Bed번호
     , SL.YD_STK_LYR_NO               --야드적치단번호
  FROM TB_YF_STKLYR SL
 WHERE SL.YD_STK_COL_GP   = SUBSTR(:V_YD_STK_COL_GP,1,6)
   AND SL.YD_STK_BED_NO   = :V_YD_STK_BED_NO
   AND SL.YD_STK_LYR_NO = :V_YD_STK_LYR_NO
   AND SL.STL_NO IS NOT NULL
) DD ON (WM.YD_WBOOK_ID = DD.YD_WBOOK_ID AND WM.STL_NO = DD.STL_NO)
WHEN NOT MATCHED THEN
INSERT (WM.YD_WBOOK_ID   , WM.STL_NO        , WM.REGISTER      , WM.REG_DDTT    ,
        WM.MODIFIER      , WM.MOD_DDTT        , WM.DEL_YN        , WM.YD_STK_COL_GP,
        WM.YD_STK_BED_NO  , WM.YD_STK_LYR_NO )
VALUES (DD.YD_WBOOK_ID   , DD.STL_NO        , DD.MODIFIER      , DD.MOD_DDTT    ,
        DD.MODIFIER      , DD.MOD_DDTT        , DD.DEL_YN        , DD.YD_STK_COL_GP,
        DD.YD_STK_BED_NO  , DD.YD_STK_LYR_NO )
 </pre> */
public final static String insWrkBookMtlByStkLyr = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkBookMtlByStkLyr";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStock
--TB_YF_STOCK 수정
UPDATE USRYFA.TB_YF_STOCK
SET
	MODIFIER            = :V_MODIFIER,
	MOD_DDTT            = SYSDATE,
	DEL_YN              = 'N',
	STOCK_MOVE_TERM     = NVL(:V_STOCK_MOVE_TERM ,  STOCK_MOVE_TERM),   --저장품이동조건
	YD_RULE_PL_RS_GP    = NVL(:V_YD_RULE_PL_RS_GP,  YD_RULE_PL_RS_GP),  --조합구분
	TRANS_ORD_DATE      = NVL(:V_TRANS_ORD_DATE,    TRANS_ORD_DATE),    --운송지시
	TRANS_ORD_SEQNO     = NVL(:V_TRANS_ORD_SEQNO,   TRANS_ORD_SEQNO),   --운송지시행번
	YD_CAR_UPP_LOC_CD   = CASE
						  WHEN :V_MODIFIER = 'DMYDR060' AND :V_YD_CAR_UPP_LOC_CD IS NULL THEN NULL
						  ELSE NVL(:V_YD_CAR_UPP_LOC_CD, YD_CAR_UPP_LOC_CD) END,  --차상위치
	CAR_NO              = NVL(:V_CAR_NO,            CAR_NO),            --차량번호
	CAR_CARD_NO         = NVL(:V_CAR_CARD_NO,       CAR_CARD_NO),       --카드번호
	CR_FRTOMOVE_GP      = NVL(:V_CR_FRTOMOVE_GP,    CR_FRTOMOVE_GP)     --냉연이송구분
WHERE 1=1
AND STL_NO              = :V_STL_NO
 </pre> */
public final static String updYfStock = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStock";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateArrDt5
UPDATE TB_YD_CARSCH
SET
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    YD_CARUD_STOP_LOC       = :V_YD_CARUD_STOP_LOC,
    YD_PNT_CD3              = :V_YD_PNT_CD3,
    YD_CARUD_ARR_DT         = SYSDATE,
    YD_CAR_PROG_STAT        = 'B', -- 하차도착
    YD_CARUD_WRK_BOOK_ID    = :V_YD_CARUD_WRK_BOOK_ID,
    FRTOMOVE_WORD_NO        = :V_FRTOMOVE_WORD_NO
WHERE 1=1
AND TRN_EQP_CD              = :V_TRN_EQP_CD
AND DEL_YN                  = 'N'
 </pre> */
public final static String updateArrDt5 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateArrDt5";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_Qty
UPDATE TB_YF_STKLYR
SET
    YD_STK_LYR_ACTIVE_STAT = :V_YD_STK_LYR_ACTIVE_STAT
WHERE 1=1
AND YD_STK_COL_GP =
(
    SELECT
        A.YD_STK_COL_GP
    FROM
        TB_YF_STKCOL A
    WHERE 1=1
    AND A.WLOC_CD     = :V_WLOC_CD
    AND A.YD_PNT_CD   = :V_YD_PNT_CD
    AND SUBSTR(A.YD_STK_COL_GP, 3, 2) = :V_TRN_EQP_GP
)
AND TO_NUMBER(YD_STK_LYR_NO) <= :V_QTY
 </pre> */
public final static String updateLayerstat_Qty = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_Qty";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updA7YML009TCarMtlIns
MERGE INTO TB_YF_TCARFTMVMTL TM USING (
SELECT :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
     , CM.STL_NO
     , :V_MODIFIER       AS MODIFIER
     , SYSDATE           AS MOD_DDTT
     , 'N'               AS DEL_YN
     , substr(:V_YD_DN_WR_LOC,7,2) AS YD_STK_BED_NO
--     , TO_CHAR((SELECT NVL(MAX(YD_STK_BED_NO),0) +1 
--                  FROM TB_YF_STACKLAYER
--                 WHERE STACK_COL_GP = substr(:V_YD_DN_WR_LOC,1,6)
--                   AND STL_NO IS NOT NULL 
--                   AND STACK_LAYER_STAT = 'C' ),'FM00') AS YD_STK_BED_NO
      , '01'              AS YD_STK_LYR_NO
      , CC.HCR_GP
      , CC.CURR_PROG_CD  AS STL_PROG_CD
      , ST.STOCK_ITEM    AS YD_MTL_ITEM
      
  FROM TB_YF_CRNWRKMTL CM
     , TB_YF_STOCK     ST
     , USRPTA.TB_PT_COILCOMM   CC  
 WHERE CM.STL_NO        = ST.STL_NO
   AND CM.STL_NO        = CC.COIL_NO
   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
   AND CM.DEL_YN        = 'N'
) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STL_NO = DD.STL_NO)
WHEN NOT MATCHED THEN
INSERT (TM.YD_TCAR_SCH_ID, TM.STL_NO     , TM.REGISTER   , TM.REG_DDTT     ,
        TM.MODIFIER      , TM.MOD_DDTT     , TM.DEL_YN     , TM.YD_STK_BED_NO,
        TM.YD_STK_LYR_NO, TM.HCR_GP       , TM.STL_PROG_CD, TM.YD_MTL_ITEM   )
VALUES (DD.YD_TCAR_SCH_ID, DD.STL_NO     , DD.MODIFIER   , DD.MOD_DDTT     ,
        DD.MODIFIER      , DD.MOD_DDTT     , DD.DEL_YN     , DD.YD_STK_BED_NO,
        DD.YD_STK_LYR_NO, DD.HCR_GP       , DD.STL_PROG_CD, DD.YD_MTL_ITEM   )
 </pre> */
public final static String updA7YML009TCarMtlIns = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updA7YML009TCarMtlIns";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updA7YSL009WbTCarIns
MERGE INTO TB_YF_WRKBOOK WB USING (
SELECT :V_YD_CARUD_WRK_BOOK_ID AS YD_WBOOK_ID      --야드작업예약ID
     , :V_MODIFIER             AS MODIFIER         --수정자
     , SYSDATE                 AS MOD_DDTT         --수정일시
     , 'N'                     AS DEL_YN           --삭제유무
     , WB.YD_GP                                    --야드구분
     , WB.YD_BAY_GP                                --야드동구분
     , WB.YD_SCH_CD                                --야드스케쥴코드
     , (SELECT SR.YD_WRK_CRN_PRIOR
          FROM TB_YF_SCHRULE SR
         WHERE SR.YD_SCH_CD = WB.YD_SCH_CD) 
                               AS YD_SCH_PRIOR     --야드스케쥴우선순위
     , 'W'                     AS YD_SCH_PROG_STAT --야드스케쥴진행상태(스케줄수행대기)
     , :V_YD_SCH_ST_GP         AS YD_SCH_ST_GP     --야드스케쥴기동구분
     , '1'                     AS YD_SCH_REQ_GP    --야드스케쥴요청구분(대차상차완료)
     , WB.YD_TO_LOC_DCSN_MTD                       --야드To위치결정방법
     , WB.YD_TO_LOC_GUIDE                          --야드To위치Guide
     , WB.YD_WRK_PLAN_TCAR                         --야드작업계획대차
  FROM (SELECT WB.YD_GP
--             , DECODE(WB.YD_AIM_BAY_GP,'', SUBSTR(TC.YD_CARLD_STOP_LOC,2,1),WB.YD_AIM_BAY_GP) AS YD_BAY_GP
             , SUBSTR(:V_YD_CARUD_STOP_LOC,2,1)         AS YD_BAY_GP
             , :V_YD_CARUD_STOP_LOC||'LM'               AS YD_SCH_CD
             
             , CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
                    THEN 'S' ELSE WB.YD_TO_LOC_DCSN_MTD END 
               AS YD_TO_LOC_DCSN_MTD
             , CASE WHEN WB.YD_TO_LOC_DCSN_MTD = 'F' AND WB.YD_AIM_BAY_GP != SUBSTR(WB.YD_TO_LOC_GUIDE,2,1)
                    THEN ''  ELSE WB.YD_TO_LOC_GUIDE    END 
               AS YD_TO_LOC_GUIDE
             , WB.YD_WRK_PLAN_TCAR
          FROM TB_YF_WRKBOOK WB
         WHERE WB.YD_WBOOK_ID = :V_YD_WBOOK_ID) WB
) DD ON (WB.YD_WBOOK_ID = DD.YD_WBOOK_ID)
WHEN NOT MATCHED THEN
INSERT (WB.YD_WBOOK_ID       , WB.REGISTER       , WB.REG_DDTT        , WB.MODIFIER    , WB.MOD_DDTT     ,
        WB.DEL_YN            , WB.YD_GP          , WB.YD_BAY_GP       , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
        WB.YD_SCH_PROG_STAT  , WB.YD_SCH_ST_GP   , WB.YD_SCH_REQ_GP   , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
        WB.YD_TO_LOC_DCSN_MTD, WB.YD_TO_LOC_GUIDE, WB.YD_WRK_PLAN_TCAR)
VALUES (DD.YD_WBOOK_ID       , DD.MODIFIER       , DD.MOD_DDTT        , DD.MODIFIER    , DD.MOD_DDTT     ,
        DD.DEL_YN            , DD.YD_GP          , DD.YD_BAY_GP       , DD.YD_SCH_CD   , DD.YD_SCH_PRIOR ,
        DD.YD_SCH_PROG_STAT  , DD.YD_SCH_ST_GP   , DD.YD_SCH_REQ_GP   , DD.YD_GP       , DD.YD_BAY_GP    ,
        DD.YD_TO_LOC_DCSN_MTD, DD.YD_TO_LOC_GUIDE, DD.YD_WRK_PLAN_TCAR)
 </pre> */
public final static String updA7YSL009WbTCarIns = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updA7YSL009WbTCarIns";

 /** <pre> 
--yf.common.dao.updateActiveStatOfLayer
--적치 단 활성 상태를 UPDATE
UPDATE TB_YF_STKLYR
SET
    YD_STK_LYR_ACTIVE_STAT  = ?
WHERE 1=1
AND YD_STK_COL_GP           = ?
 </pre> */
public final static String updateActiveStatOfLayer = "bak.yf.common.dao.updateActiveStatOfLayer";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.modifyCardNoOflayerEND
--TB_YF_STKLYR 하차인 경우 차량위치재료 종료처리
UPDATE TB_YF_STKLYR
SET
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    STL_NO          = NULL,
    YD_STK_LYR_STAT = 'E'
WHERE 1=1
AND YD_STK_COL_GP IN
(
    SELECT
        YD_CARUD_STOP_LOC
    FROM
        USRYDA.TB_YD_CARSCH A
    WHERE 1=1
    AND A.DEL_YN    = 'N'
    AND A.YD_CAR_PROG_STAT IN ('A', 'B', 'C', 'D', 'E') --//하차
    AND A.CARD_NO   = :V_CARD_NO
)
AND SUBSTR(YD_STK_COL_GP, 3, 2) = 'PT'
 </pre> */
public final static String modifyCardNoOflayerEND = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.modifyCardNoOflayerEND";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.modifyCardNoOfDetailEND
--차량이송재로 종료처리
UPDATE USRYDA.TB_YD_CARFTMVMTL
SET 
    MODIFIER    = :V_MODIFIER, 
    MOD_DDTT    = SYSDATE, 
    DEL_YN      = 'Y'
WHERE 1=1
AND YD_CAR_SCH_ID IN 
( 
    SELECT 
        YD_CAR_SCH_ID
    FROM 
        USRYDA.TB_YD_CARSCH
    WHERE 1=1
    AND DEL_YN  = 'N'
    AND CARD_NO = :V_CARD_NO 
)
 </pre> */
public final static String modifyCardNoOfDetailEND = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.modifyCardNoOfDetailEND";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointWlocpntupdatePT 
UPDATE TB_YD_CARPOINT
SET
    YD_STK_COL_ACT_STAT = :V_STAT,
    CAR_NO      = :V_CAR_NO,
    CARD_NO     = :V_TRN_EQP_CD,
    MOD_DDTT    = SYSDATE,
    MODIFIER    = 'CarPointPT'
WHERE 1=1
AND WLOC_CD     = :V_ARR_WLOC_CD
AND YD_PNT_CD   = :V_ARR_YD_PNT_CD
 </pre> */
public final static String carpointWlocpntupdatePT = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointWlocpntupdatePT";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updbcoilWrkBookPrior
UPDATE TB_YF_WRKBOOK
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , YD_SCH_PRIOR = :V_YD_SCH_PRIOR
     , YD_SCH_CD      = :V_YD_SCH_CD
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String updbcoilWrkBookPrior = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updbcoilWrkBookPrior";

 /** <pre> 
--yf.common.dao.updateStockStatOfLayer2
--저장품ID, 적치단상태, 적치상태를 UPDATE
UPDATE TB_YF_STKLYR
SET
    STL_NO                  = ?,
    YD_STK_LYR_ACTIVE_STAT  = ?,
    YD_STK_LYR_STAT         = ?
WHERE 1=1
AND YD_STK_COL_GP           = ?
 </pre> */
public final static String updateStockStatOfLayer2 = "bak.yf.common.dao.updateStockStatOfLayer2";

 /** <pre> 
--yf.common.dao.updateUnloadInfoOfStock
--차량 하차시 관련정보를 저장품에서 CLEAR 한다.
UPDATE TB_YF_STOCK
SET
    TRANS_ORD_DATE  = ?,
    TRANS_ORD_SEQNO = ?,
    CAR_CARD_NO     = ?
WHERE 1=1
AND STL_NO          = ?
 </pre> */
public final static String updateUnloadInfoOfStock = "bak.yf.common.dao.updateUnloadInfoOfStock";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updWrkCrnByChgSchCrn 
UPDATE TB_YF_WRKBOOK
   SET YD_WRK_PLAN_CRN = :V_YD_WRK_PLAN_CRN
     , MOD_DDTT        = SYSDATE
     , MODIFIER        = :V_MODIFIER
 WHERE YD_WBOOK_ID IN ( SELECT WB.YD_WBOOK_ID
                          FROM TB_YF_WRKBOOK    WB
                             , TB_YF_WRKBOOKMTL WM
                         WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
                           AND WB.DEL_YN = 'N'
                           AND WM.DEL_YN = 'N'
                           AND NOT EXISTS (SELECT 1
                                             FROM TB_YF_CRNSCH CS
                                            WHERE CS.YD_WBOOK_ID = WB.YD_WBOOK_ID
                                              AND CS.DEL_YN = 'N'
                                          )
                           AND WB.YD_GP = '1'
                           AND WB.YD_SCH_CD = :V_YD_SCH_CD
                      )
   AND YD_WRK_PLAN_CRN IS NOT NULL
 </pre> */
public final static String updWrkCrnByChgSchCrn = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updWrkCrnByChgSchCrn";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateZoneInNoOfStock
UPDATE TB_YF_STOCK
SET
    CHARGE_LOT_NO   = NULL,
    MODIFIER        = 'SYSTEM',
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updateZoneInNoOfStock = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateZoneInNoOfStock";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockTransWordNo
UPDATE TB_YF_STOCK
SET
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE,
    STOCK_MOVE_TERM     = NVL(:V_STOCK_MOVE_TERM,STOCK_MOVE_TERM),
    FRTOMOVE_WORD_NO    = :V_FRTOMOVE_WORD_NO
WHERE 1=1
AND STL_NO = :V_STL_NO
 </pre> */
public final static String updStockTransWordNo = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockTransWordNo";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkBook
--작업예약 등록 
INSERT INTO TB_YF_WRKBOOK (
	 YD_WBOOK_ID
	,REGISTER
	,REG_DDTT
	,MODIFIER
	,MOD_DDTT
	,DEL_YN
	,YD_GP
	,YD_BAY_GP
	,YD_SCH_CD
	,YD_SCH_PRIOR
	,YD_SCH_PROG_STAT
	,YD_SCH_ST_GP
	,YD_SCH_REQ_GP
	,YD_AIM_YD_GP
	,YD_AIM_BAY_GP
	,YD_CTS_RELAY_YN
	,YD_CTS_RELAY_BAY_GP
	,YD_TO_LOC_DCSN_MTD
	,YD_TO_LOC_GUIDE
	,YD_WRK_PLAN_TCAR
	,YD_CAR_USE_GP
	,TRN_EQP_CD
	,CAR_NO
	,CARD_NO
	,PTOP_PLNT_GP
	,DEST_TEL_NO
	,DIST_SHIPASSIGN_GP
	,YD_WRK_PLAN_CRN
	,SCH_CNCL_YN
	,YD_WRK_PLAN_CRN2
	,CHARGE_LOT_NO_DIV_YN
	,YD_TO_LOC_GUIDE_FNL
	,CAR_FRM_GP
) VALUES (
	 :V_YD_WBOOK_ID
	,:V_MODIFIER
    ,SYSDATE
    ,:V_MODIFIER
    ,SYSDATE
	,'N'
	,:V_YD_GP
	,:V_YD_BAY_GP
	,:V_YD_SCH_CD
	,TO_NUMBER(:V_YD_SCH_PRIOR)
	,:V_YD_SCH_PROG_STAT
	,:V_YD_SCH_ST_GP
	,:V_YD_SCH_REQ_GP
	,:V_YD_AIM_YD_GP
	,:V_YD_AIM_BAY_GP
	,:V_YD_CTS_RELAY_YN
	,:V_YD_CTS_RELAY_BAY_GP
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
	,:V_SCH_CNCL_YN
	,:V_YD_WRK_PLAN_CRN2
	,:V_CHARGE_LOT_NO_DIV_YN
	,:V_YD_TO_LOC_GUIDE_FNL
	,:V_CAR_FRM_GP
    
)
 </pre> */
public final static String insWrkBook = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkBook";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updWrkBookPrior
--작업예약 스케쥴우선순위 수정
UPDATE TB_YF_WRKBOOK
   SET MODIFIER     = :V_MODIFIER
     , MOD_DDTT     = SYSDATE
     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
   AND DEL_YN       = 'N'

 </pre> */
public final static String updWrkBookPrior = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updWrkBookPrior";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgtW
--크레인작업관리 크레인변경 크레인스케줄 수정
UPDATE TB_YF_CRNSCH
   SET MODIFIER     = :V_MODIFIER
     , MOD_DDTT     = SYSDATE
     , YD_SCH_PRIOR = TO_NUMBER(:V_YD_SCH_PRIOR)
     , YD_EQP_ID    = NVL(:V_YD_EQP_ID,YD_EQP_ID)
     , YD_WRK_PROG_STAT = 'W' 
     , YD_WORD_DT   = SYSDATE
 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
   AND YD_WRK_PROG_STAT IN ('1','W','S')
   AND DEL_YN = 'N'
 </pre> */
public final static String updCrnWrkMgtW = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgtW";

 /** <pre> 
--yf.common.dao.updateCardNoOfStackCol2
--차량의 카드번호를 UPDATE
UPDATE TB_YF_STKCOL
SET     
    CAR_CARD_NO = NULL
WHERE 1=1
AND CAR_CARD_NO = ?
 </pre> */
public final static String updateCardNoOfStackCol2 = "bak.yf.common.dao.updateCardNoOfStackCol2";

 /** <pre> 
--yf.common.dao.updateActiveStatOfLayer1
--적치 단 활성 상태를 UPDATE
UPDATE TB_YF_STKLYR
SET
    YD_STK_LYR_ACTIVE_STAT  = ?
WHERE 1=1
AND YD_STK_COL_GP           = ?
AND YD_STK_BED_NO           = ?
AND YD_STK_LYR_NO           = ?
 </pre> */
public final static String updateActiveStatOfLayer1 = "bak.yf.common.dao.updateActiveStatOfLayer1";

 /** <pre> 
--yf.common.dao.updateActiveStatOfLayer_02
--적치 단 활성 상태를 UPDATE
UPDATE TB_YF_STKLYR
SET
    YD_STK_LYR_ACTIVE_STAT  = ?,
    STL_NO                  = '',
    YD_STK_LYR_STAT         = 'E'
WHERE 1=1
AND YD_STK_COL_GP           = ?
AND YD_STK_BED_NO = LPAD(TO_NUMBER( ? ), 2, '0')
 </pre> */
public final static String updateActiveStatOfLayer_02 = "bak.yf.common.dao.updateActiveStatOfLayer_02";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYdStockStockId 
-- 저장품 Table(TB_YF_STOCK)에 WBOOK_ID를 Update 한다.
UPDATE TB_YF_STOCK 
   SET MODIFIER        = :V_MODIFIER
     , MOD_DDTT        = SYSDATE
     , STOCK_MOVE_TERM = NVL(:V_STOCK_MOVE_TERM,STOCK_MOVE_TERM) 
 WHERE STL_NO        = :V_STL_NO
 </pre> */
public final static String updateYdStockStockId = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYdStockStockId";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.StockDAO.updatePalletNO
UPDATE TB_YF_STOCK
SET 
    SHEAR_SUPPLY_DEMAND_DDTT= ''
WHERE 1=1
AND SHEAR_SUPPLY_DEMAND_DDTT = ?
 </pre> */
public final static String updatePalletNO = "bak.yf.facilitystatus.facilityinquiry.StockDAO.updatePalletNO";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updStatEqp2
--설비 상태 수정 
UPDATE TB_YF_EQP
   SET MODIFIER         = :V_MODIFIER
      ,MOD_DDTT         = SYSDATE
      ,YD_EQP_PROG_STAT = :V_YD_EQP_PROG_STAT
 WHERE YD_EQP_ID        = :V_YD_EQP_ID
   AND DEL_YN           = 'N'
 </pre> */
public final static String updStatEqp2 = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStatEqp2";

 /** <pre> 
--yf.common.dao.updateLieTakeOutTimeOfSlabComm
--슬라브 공통 테이블 '부두 YARD 반출 일자', '부두 YARD 반출 시각' UPDATE
UPDATE TB_PT_SLABCOMM
SET
    PORT_YD_TAKEOUT_DATE    = ?,
    PORT_YD_TAKEOUT_TIME    = ?
WHERE 1=1
AND SLAB_NO                 = ?
 </pre> */
public final static String updateLieTakeOutTimeOfSlabComm = "bak.yf.common.dao.updateLieTakeOutTimeOfSlabComm";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updateArrDt_1
UPDATE TB_YD_CARSCH
SET
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE,
    YD_CARLD_STOP_LOC   = :V_YD_CARLD_STOP_LOC,
    YD_CARLD_ARR_DT     = SYSDATE,
    YD_CAR_PROG_STAT    = '2'   --상차도착
WHERE 1=1
AND TRN_EQP_CD          = :V_TRN_EQP_CD
AND DEL_YN              = 'N'
 </pre> */
public final static String updateArrDt_1 = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updateArrDt_1";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.updSchPrfrPrior    
--크레인스케줄 작업예약재료 저장위치 수정
 MERGE INTO TB_YF_SCHLOCSRCHPRIOR SP USING (
 SELECT :V_YD_SCH_PRFR_PRIOR AS YD_SCH_PRFR_PRIOR
      , :V_MODIFIER          AS MODIFIER
      , SYSDATE              AS MOD_DDTT
      , :V_YD_SCH_CD         AS YD_SCH_CD
      , :V_YD_ROUTE_GP       AS YD_ROUTE_GP
   FROM DUAL
 ) DD ON (SP.YD_SCH_CD = DD.YD_SCH_CD AND SP.YD_ROUTE_GP = DD.YD_ROUTE_GP)

WHEN NOT MATCHED THEN
    INSERT (
           YD_SCH_CD                , YD_ROUTE_GP      , REGISTER
         , REG_DDTT                 , MODIFIER         , MOD_DDTT
         , YD_SCH_PRFR_PRIOR        , DEL_YN
         )
    VALUES (
           DD.YD_SCH_CD             , DD.YD_ROUTE_GP   , DD.MODIFIER
         , DD.MOD_DDTT              , DD.MODIFIER      , DD.MOD_DDTT
         , DD.YD_SCH_PRFR_PRIOR     , 'N'
         )
WHEN MATCHED THEN 
     UPDATE SET
      SP.MODIFIER       = DD.MODIFIER
    , SP.MOD_DDTT       = DD.MOD_DDTT
    , SP.YD_SCH_PRFR_PRIOR   = DD.YD_SCH_PRFR_PRIOR
 </pre> */
public final static String updSchPrfrPrior = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updSchPrfrPrior";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.delSchLocSrch  
UPDATE TB_YF_SCHLOCSRCH
   SET DEL_YN   = 'Y'
     , MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
 WHERE YD_SCH_CD    = :V_YD_SCH_CD
AND YD_ROUTE_GP  = :V_YD_ROUTE_GP
 </pre> */
public final static String delSchLocSrch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.delSchLocSrch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updStockTransInfo1
UPDATE TB_YF_STOCK
SET
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updStockTransInfo1 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updStockTransInfo1";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCarschYdBayinWoSeq
UPDATE TB_YD_CARSCH
   SET MOD_DDTT = SYSDATE
       , MODIFIER = :V_MODIFIER
       , YD_BAYIN_WO_SEQ = :V_YD_BAYIN_WO_SEQ
WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID	  

 </pre> */
public final static String updYdCarschYdBayinWoSeq = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCarschYdBayinWoSeq";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointtrneqpcdupdatePT 
UPDATE TB_YD_CARPOINT
SET
    CARD_NO     = NULL,
    CAR_NO      = NULL,
    YD_STK_COL_ACT_STAT = DECODE(YD_STK_COL_ACT_STAT, 'N', 'N',:V_STAT),
    MOD_DDTT    = SYSDATE,
    MODIFIER    = 'CarPointPT'
WHERE 1=1
AND CARD_NO     = :V_TRN_EQP_CD
 </pre> */
public final static String carpointtrneqpcdupdatePT = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointtrneqpcdupdatePT";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkLyrYdStkColBedGp
UPDATE TB_YF_STKLYR
SET
    MOD_DDTT            = SYSDATE,
    MODIFIER            = :V_MODIFIER,
    YD_STK_LYR_ACTIVE_STAT  = NVL(:V_YD_STK_LYR_ACTIVE_STAT, YD_STK_LYR_ACTIVE_STAT),
    STL_NO              = :V_STL_NO,
    YD_STK_LYR_STAT     = NVL(:V_YD_STK_LYR_STAT, YD_STK_LYR_STAT)
WHERE 1=1
AND YD_STK_COL_GP       = :V_YD_STK_COL_GP
AND YD_STK_BED_NO       = :V_YD_STK_BED_NO
AND YD_STK_LYR_NO       = :V_YD_STK_LYR_NO
 </pre> */
public final static String updYdStkLyrYdStkColBedGp = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkLyrYdStkColBedGp";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointWlocpntupdate 
UPDATE TB_YD_CARPOINT
SET
    YD_STK_COL_ACT_STAT = :V_STAT,
    TRN_EQP_CD  = :V_TRN_EQP_CD,
    MOD_DDTT    = SYSDATE,
    MODIFIER    = 'CarPointin'
WHERE 1=1
AND WLOC_CD     = :V_ARR_WLOC_CD
AND YD_PNT_CD   = :V_ARR_YD_PNT_CD
 </pre> */
public final static String carpointWlocpntupdate = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointWlocpntupdate";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updYdCrnWrkSidedelyn
UPDATE TB_YF_CRNSCH
   SET MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,YD_EQP_WRK_SH            = NVL(:V_YD_EQP_WRK_SH          ,YD_EQP_WRK_SH)
      ,YD_EQP_WRK_WT            = NVL(:V_YD_EQP_WRK_WT          ,YD_EQP_WRK_WT)
      ,YD_EQP_WRK_T             = NVL(:V_YD_EQP_WRK_T           ,YD_EQP_WRK_T)
      ,YD_EQP_WRK_MAX_W         = NVL(:V_YD_EQP_WRK_MAX_W       ,YD_EQP_WRK_MAX_W)
      ,YD_EQP_WRK_MAX_L         = NVL(:V_YD_EQP_WRK_MAX_L       ,YD_EQP_WRK_MAX_L)
      ,YD_CRN_SB_CTL_H          = NVL(:V_YD_CRN_SB_CTL_H        ,YD_CRN_SB_CTL_H)
      ,YD_CRN_GRAB_USE_RULE_ID  = NVL(:V_YD_CRN_GRAB_USE_RULE_ID,YD_CRN_GRAB_USE_RULE_ID)
      ,YD_UP_WO_LOC             = NVL(:V_YD_UP_WO_LOC           ,YD_UP_WO_LOC)
      ,YD_UP_WO_LYR             = NVL(:V_YD_UP_WO_LYR         ,YD_UP_WO_LYR)
      ,YD_UP_WO_LOC_XAXIS       = NVL(:V_YD_UP_WO_LOC_XAXIS     ,YD_UP_WO_LOC_XAXIS)
      ,YD_UP_WO_XAXIS_GAP_MAX   = NVL(:V_YD_UP_WO_XAXIS_GAP_MAX ,YD_UP_WO_XAXIS_GAP_MAX)
      ,YD_UP_WO_XAXIS_GAP_MIN   = NVL(:V_YD_UP_WO_XAXIS_GAP_MIN ,YD_UP_WO_XAXIS_GAP_MIN)
      ,YD_UP_WO_LOC_YAXIS       = NVL(:V_YD_UP_WO_LOC_YAXIS     ,YD_UP_WO_LOC_YAXIS)
      ,YD_UP_WO_LOC_YAXIS1      = NVL(:V_YD_UP_WO_LOC_YAXIS1    ,YD_UP_WO_LOC_YAXIS1)
      ,YD_UP_WO_LOC_YAXIS2      = NVL(:V_YD_UP_WO_LOC_YAXIS2    ,YD_UP_WO_LOC_YAXIS2)
      ,YD_UP_WO_YAXIS_GAP_MAX   = NVL(:V_YD_UP_WO_YAXIS_GAP_MAX ,YD_UP_WO_YAXIS_GAP_MAX)
      ,YD_UP_WO_YAXIS_GAP_MIN   = NVL(:V_YD_UP_WO_YAXIS_GAP_MIN ,YD_UP_WO_YAXIS_GAP_MIN)
      ,YD_UP_WO_LOC_ZAXIS       = NVL(:V_YD_UP_WO_LOC_ZAXIS     ,YD_UP_WO_LOC_ZAXIS)
      ,YD_UP_WO_ZAXIS_GAP_MAX   = NVL(:V_YD_UP_WO_ZAXIS_GAP_MAX ,YD_UP_WO_ZAXIS_GAP_MAX)
      ,YD_UP_WO_ZAXIS_GAP_MIN   = NVL(:V_YD_UP_WO_ZAXIS_GAP_MIN ,YD_UP_WO_ZAXIS_GAP_MIN)
      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
      ,YD_DN_WO_LYR             = NVL(:V_YD_DN_WO_LYR         ,YD_DN_WO_LYR)
      ,YD_DN_WO_LOC_XAXIS       = NVL(:V_YD_DN_WO_LOC_XAXIS     ,YD_DN_WO_LOC_XAXIS)
      ,YD_DN_WO_XAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_XAXIS_GAP_MAX ,YD_DN_WO_XAXIS_GAP_MAX)
      ,YD_DN_WO_XAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_XAXIS_GAP_MIN ,YD_DN_WO_XAXIS_GAP_MIN)
      ,YD_DN_WO_LOC_YAXIS       = NVL(:V_YD_DN_WO_LOC_YAXIS     ,YD_DN_WO_LOC_YAXIS)
      ,YD_DN_WO_LOC_YAXIS1      = NVL(:V_YD_DN_WO_LOC_YAXIS1    ,YD_DN_WO_LOC_YAXIS1)
      ,YD_DN_WO_LOC_YAXIS2      = NVL(:V_YD_DN_WO_LOC_YAXIS2    ,YD_DN_WO_LOC_YAXIS2)
      ,YD_DN_WO_YAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_YAXIS_GAP_MAX ,YD_DN_WO_YAXIS_GAP_MAX)
      ,YD_DN_WO_YAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_YAXIS_GAP_MIN ,YD_DN_WO_YAXIS_GAP_MIN)
      ,YD_DN_WO_LOC_ZAXIS       = NVL(:V_YD_DN_WO_LOC_ZAXIS     ,YD_DN_WO_LOC_ZAXIS)
      ,YD_DN_WO_ZAXIS_GAP_MAX   = NVL(:V_YD_DN_WO_ZAXIS_GAP_MAX ,YD_DN_WO_ZAXIS_GAP_MAX)
      ,YD_DN_WO_ZAXIS_GAP_MIN   = NVL(:V_YD_DN_WO_ZAXIS_GAP_MIN ,YD_DN_WO_ZAXIS_GAP_MIN)
      ,YD_UP_WR_LOC             = NVL(:V_YD_UP_WR_LOC           ,YD_UP_WR_LOC)
      ,YD_UP_WR_LYR             = NVL(:V_YD_UP_WR_LYR         ,YD_UP_WR_LYR)
      ,YD_UP_WRK_ACT_GP         = NVL(:V_YD_UP_WRK_ACT_GP       ,YD_UP_WRK_ACT_GP)
      ,YD_UP_WR_XAXIS           = NVL(:V_YD_UP_WR_XAXIS         ,YD_UP_WR_XAXIS)
      ,YD_UP_WR_YAXIS           = NVL(:V_YD_UP_WR_YAXIS         ,YD_UP_WR_YAXIS)
      ,YD_UP_WR_YAXIS1          = NVL(:V_YD_UP_WR_YAXIS1        ,YD_UP_WR_YAXIS1)
      ,YD_UP_WR_YAXIS2          = NVL(:V_YD_UP_WR_YAXIS2        ,YD_UP_WR_YAXIS2)
      ,YD_UP_WR_ZAXIS           = NVL(:V_YD_UP_WR_ZAXIS         ,YD_UP_WR_ZAXIS)
      ,YD_DN_WR_LOC             = NVL(:V_YD_DN_WR_LOC           ,YD_DN_WR_LOC)
      ,YD_DN_WR_LYR             = NVL(:V_YD_DN_WR_LYR         ,YD_DN_WR_LYR)
      ,YD_DN_WRK_ACT_GP         = NVL(:V_YD_DN_WRK_ACT_GP       ,YD_DN_WRK_ACT_GP)
      ,YD_DN_WR_XAXIS           = NVL(:V_YD_DN_WR_XAXIS         ,YD_DN_WR_XAXIS)
      ,YD_DN_WR_YAXIS           = NVL(:V_YD_DN_WR_YAXIS         ,YD_DN_WR_YAXIS)
      ,YD_DN_WR_YAXIS1          = NVL(:V_YD_DN_WR_YAXIS1        ,YD_DN_WR_YAXIS1)
      ,YD_DN_WR_YAXIS2          = NVL(:V_YD_DN_WR_YAXIS2        ,YD_DN_WR_YAXIS2)
      ,YD_DN_WR_ZAXIS           = NVL(:V_YD_DN_WR_ZAXIS         ,YD_DN_WR_ZAXIS)
      ,YD_EQP_ID                = NVL(:V_YD_EQP_ID              ,YD_EQP_ID)
      ,YD_SCH_CD                = NVL(:V_YD_SCH_CD              ,YD_SCH_CD)
      ,YD_TO_LOC_DCSN_MTD       = NVL(:V_YD_TO_LOC_DCSN_MTD     ,YD_TO_LOC_DCSN_MTD)
      ,YD_AID_WRK_UPDN_GP       = NVL(:V_YD_AID_WRK_UPDN_GP     ,YD_AID_WRK_UPDN_GP)
      ,UP_ROTATION_ANGLE        = NVL(:V_UP_ROTATION_ANGLE      ,UP_ROTATION_ANGLE)
      ,DOWN_ROTATION_ANGLE      = NVL(:V_DOWN_ROTATION_ANGLE    ,DOWN_ROTATION_ANGLE)
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updYdCrnWrkSidedelyn = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updYdCrnWrkSidedelyn";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.insertStockTransInfo
MERGE INTO TB_YF_STOCK ST USING
(
    SELECT
        :V_STL_NO   AS STL_NO,      --재료번호
        :V_MODIFIER AS MODIFIER,    --수정자
        SYSDATE     AS MOD_DDTT,    --수정일시
        'N'         AS DEL_YN       --삭제유무
    FROM
        DUAL
) DD
ON (ST.STL_NO = DD.STL_NO)
WHEN NOT MATCHED THEN
    INSERT
    (
        STL_NO,
        STOCK_ITEM,
        STOCK_MOVE_TERM,
        REGISTER,
        REG_DDTT,
        MODIFIER,
        MOD_DDTT,
        DEL_YN
    )
    VALUES
    (
        :V_STL_NO,
        'CM',
        '',
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.DEL_YN
    )
WHEN MATCHED THEN
    UPDATE
    SET
        ST.STOCK_ITEM       = 'CM',
        ST.STOCK_MOVE_TERM  = '',
        ST.MODIFIER         = DD.MODIFIER,
        ST.MOD_DDTT         = DD.MOD_DDTT,
        ST.DEL_YN           = DD.DEL_YN

 </pre> */
public final static String insertStockTransInfo = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.insertStockTransInfo";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009CrnSch
-- 크레인권하실적 크레인스케줄 수정 
UPDATE TB_YF_CRNSCH CS
   SET CS.MODIFIER         =:V_MODIFIER 
     , CS.MOD_DDTT         = SYSDATE
     , CS.DEL_YN           = 'Y'
     , CS.YD_WRK_PROG_STAT = '4' --권하완료
     , CS.YD_WRK_HDS_DD    = SF_YD_WRK_HDS_DD(TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS'))
     , CS.YD_WRK_DUTY      = SF_YD_WRK_DUTY(TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS'))
     , CS.YD_WRK_PARTY     = SF_YD_WRK_PARTY(TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS'))
     , CS.YD_DN_CMPL_DT    = TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS')
     , CS.YD_DN_WR_LOC     = :V_YD_DN_WR_LOC 
     , CS.YD_DN_WR_LYR   = :V_YD_DN_WR_LYR 
     , CS.YD_DN_WRK_ACT_GP = :V_YD_DN_WRK_ACT_GP   
     , CS.YD_DN_WR_XAXIS   = TO_NUMBER(:V_YD_DN_WR_XAXIS)
     , CS.YD_DN_WR_YAXIS   = TO_NUMBER(:V_YD_DN_WR_YAXIS)  
     , CS.YD_DN_WR_ZAXIS   = TO_NUMBER(:V_YD_DN_WR_ZAXIS)
     , CS.YD_DN_WRK_MODE2  = :V_YD_DN_WRK_MODE2
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updAxYDL009CrnSch = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009CrnSch";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009WbMtlDel
UPDATE TB_YF_WRKBOOKMTL
   SET DEL_YN = 'Y'
     , MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
   AND STL_NO IN ( 
                        SELECT STL_NO 
                        FROM   TB_YF_CRNWRKMTL
                        WHERE  YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
                    )
 </pre> */
public final static String updAxYDL009WbMtlDel = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009WbMtlDel";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchColor
MERGE INTO TB_YF_RULE A
USING (SELECT :V_MODIFIER  AS MODIFIER
             ,:V_YD_GP || '_' || SUBSTR(:V_YD_SCH_CD,3) AS ITEM
             ,:V_DTL_ITEM1 AS DTL_ITEM1
             ,'SCHRGB' AS REPR_CD_GP
         FROM DUAL) P
   ON (     A.REPR_CD_GP = P.REPR_CD_GP
        AND A.CD_GP = P.MODIFIER
        AND A.ITEM = P.ITEM )
 WHEN MATCHED THEN
 UPDATE SET   A.MODIFIER = P.MODIFIER
             ,A.MOD_DDTT = SYSDATE
             ,A.DTL_ITEM1 = P.DTL_ITEM1
 WHEN NOT MATCHED THEN
 INSERT( REPR_CD_GP
        ,CD_GP
        ,ITEM
        ,REGISTER
        ,REG_DDTT
        ,MODIFIER
        ,MOD_DDTT
        ,DEL_YN
        ,REPR_CD_CONTENTS
        ,DTL_ITEM1 )
 VALUES( P.REPR_CD_GP
        ,P.MODIFIER
        ,P.ITEM
        ,P.MODIFIER
        ,SYSDATE
        ,P.MODIFIER
        ,SYSDATE
        ,'N'
        ,'스케줄별 색상'
        ,P.DTL_ITEM1 )
 </pre> */
public final static String updSchColor = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchColor";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkHist
INSERT INTO TB_YF_WRKHIST
(
     YD_WRK_HIST_ID
    ,REGISTER
    ,REG_DDTT
    ,MODIFIER
    ,MOD_DDTT
    ,DEL_YN
    ,YD_GP
    ,STL_NO
    ,STL_APPEAR_GP
    ,ITEMNAME_CD
    ,ORD_YEOJAE_GP
    ,ORD_NO
    ,ORD_DTL
    ,STLKIND_CD
    ,SPEC_ABBSYM
    ,ORD_GP
    ,CUST_CD
    ,DEST_CD
    ,DEMANDER_CD
    ,DEST_TEL_NO
    ,STL_PROG_CD
    ,GOODS_GRADE
    ,YD_MTL_W_GP
    ,YD_MTL_T_GP
    ,YD_MTL_L_GP
    ,YD_MTL_T
    ,YD_MTL_W
    ,YD_MTL_L
    ,YD_MTL_WT
    ,YD_COIL_OUTDIA_GRP_GP
    ,COIL_INDIA
    ,COIL_OUTDIA
    ,SLAB_WO_RT_CD
    ,ORD_HCR_GP
    ,HCR_GP
    ,SCARFING_YN
    ,SCARFING_DONE_YN
    ,HYSCO_TRANS_GP
    ,RENTPROC_CD
    ,DIST_DUE_DATE
    ,FRTOMOVE_ORD_DATE
    ,URGENT_FRTOMOVE_WORD_GP
    ,SPOS_WLOC_CD
    ,ARR_WLOC_CD
    ,YD_AIM_RT_GP
    ,YD_AIM_BAY_GP
    ,TRANS_ORD_DATE
    ,SHIPASSIGN_WORD_DATE
    ,SHIPASSIGN_WORD_SEQNO
    ,SHIP_CD
    ,SHIP_NAME
    ,RSHP_HOLD_NO
    ,BERTH_NO
    ,SAILNO
    ,YD_CAR_USE_GP
    ,CAR_NO
    ,TRN_EQP_CD
    ,CAR_KIND
    ,TRANS_EQUIPMENT_TYPE
    ,CARD_NO
    ,YD_CAR_SCH_ID
    ,YD_TCAR_SCH_ID
    ,YD_WBOOK_ID
    ,YD_CRN_SCH_ID
    ,YD_SCH_CD
    ,YD_SCH_ST_GP
    ,YD_SCH_REQ_GP
    ,YD_SCH_PRIOR
    ,YD_WBOOK_DT
    ,YD_AID_WRK_YN
    ,YD_TO_LOC_DCSN_MTD
    ,YD_TO_LOC_GUIDE
    ,YD_SCH_DT
    ,YD_WRK_HDS_DD
    ,YD_WRK_DUTY
    ,YD_WRK_PARTY
    ,YD_CARLD_LEV_LOC
    ,YD_CARLD_LEV_DT
    ,YD_CARLD_PNT_WO_DT
    ,YD_PNT_CD1
    ,YD_PNT_CD2
    ,YD_CARLD_WRK_BOOK_ID
    ,YD_CARLD_SCH_REQ_GP
    ,YD_CARLD_STOP_LOC
    ,YD_CARLD_ARR_DT
    ,YD_CARLD_ST_DT
    ,YD_CARLD_CMPL_DT
    ,YD_CARLD_WRK_ACT_GP
    ,YD_CARLD_CHK_DT
    ,YD_CARUD_LEV_DT
    ,YD_CARUD_PNT_WO_DT
    ,YD_PNT_CD3
    ,YD_PNT_CD4
    ,YD_CARUD_WRK_BOOK_ID
    ,YD_CARUD_STOP_LOC
    ,YD_CARUD_SCH_REQ_GP
    ,YD_CARUD_ARR_DT
    ,YD_CARUD_CHK_DT
    ,YD_CARUD_ST_DT
    ,YD_CARUD_CMPL_DT
    ,YD_CARUD_WRK_ACT_GP
    ,YD_TRN_WRK_DELY_CD
    ,YD_EQP_ID
    ,YD_UP_WRK_MODE2
    ,YD_DN_WRK_MODE2
    ,YD_UP_WO_LOC
    ,YD_UP_WO_LYR
    ,YD_UP_WO_LOC_XAXIS
    ,YD_UP_WO_LOC_YAXIS
    ,YD_UP_WO_LOC_ZAXIS
    ,YD_UP_WR_LOC
    ,YD_UP_WR_LYR
    ,YD_UP_WR_XAXIS
    ,YD_UP_WR_YAXIS
    ,YD_UP_WR_ZAXIS
    ,YD_UP_WR_FUNC
    ,YD_UP_CMPL_DT
    ,YD_DN_WO_LOC
    ,YD_DN_WO_LYR
    ,YD_DN_WO_LOC_XAXIS
    ,YD_DN_WO_LOC_YAXIS
    ,YD_DN_WO_LOC_ZAXIS
    ,YD_DN_WR_LOC
    ,YD_DN_WR_LYR
    ,YD_DN_WR_XAXIS
    ,YD_DN_WR_YAXIS
    ,YD_DN_WR_ZAXIS
    ,YD_DN_WR_FUNC
    ,YD_DN_CMPL_DT
)
SELECT 
       TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YF_WRKHIST_SEQ.NEXTVAL AS YD_WRK_HIST_ID                 
     , :V_REGISTER             AS REGISTER                                                                            
     , SYSDATE                 AS REG_DDTT						                                                                   
     , :V_REGISTER             AS MODIFIER                      
     , SYSDATE                 AS MOD_DDTT
     , 'N'                     AS DEL_YN                              
     , A.YD_GP                 AS YD_GP
     , H.STL_NO                AS STL_NO    
     , G.STL_APPEAR_GP         AS STL_APPEAR_GP
     , G.ITEMNAME_CD           AS ITEMNAME_CD
     , G.ORD_YEOJAE_GP         AS ORD_YEOJAE_GP
     , G.ORD_NO                AS ORD_NO
     , G.ORD_DTL               AS ORD_DTL
     , ''                      AS STLKIND_CD
     , G.SPEC_ABBSYM           AS SPEC_ABBSYM
     , ''                      AS ORD_GP
     , G.CUST_CD               AS CUST_CD
     , ''                      AS DEST_CD
     , G.DEMANDER_CD           AS DEMANDER_CD
     , I.DEST_TEL_NO           AS DEST_TEL_NO
     , H.STL_PROG_CD           AS STL_PROG_CD
     , G.OVERALL_STAMP_GRADE   AS GOODS_GRADE
     
     , CASE WHEN G.COIL_W < 1601 THEN 'M' ELSE 'L' END AS YD_MTL_W_GP
     , '' AS YD_MTL_T_GP
     , '' AS YD_MTL_L_GP
     
     , G.COIL_T                                AS YD_MTL_T
     , G.COIL_W                                AS YD_MTL_W
     , G.COIL_LEN                              AS YD_MTL_L
     , G.COIL_WT                               AS YD_MTL_WT
     , CASE WHEN G.COIL_OUTDIA <=1280 THEN 'A' 
            WHEN G.COIL_OUTDIA <=1930 THEN 'B' 
                                      ELSE 'C' 
            END                                AS YD_COIL_OUTDIA_GRP_GP
     , G.COIL_INDIA                            AS COIL_INDIA
     , G.COIL_OUTDIA                           AS COIL_OUTDIA
     , ''                                     AS SLAB_WO_RT_CD
     , ''                                     AS ORD_HCR_GP
     , H.HCR_GP                               AS HCR_GP 
     , ''                                     AS SCARFING_YN
     , ''                                     AS SCARFING_DONE_YN
     , G.HYSCO_TRANS_GP                       AS HYSCO_TRANS_GP
     , I.RENTPROC_CD                          AS RENTPROC_CD
     , ''                                     AS DIST_DUE_DATE
     , G.FRTOMOVE_ORD_DATE                    AS FRTOMOVE_ORD_DATE
     , I.URGENT_FRTOMOVE_WORD_GP              AS URGENT_FRTOMOVE_WORD_GP
     , I.SPOS_WLOC_CD                         AS SPOS_WLOC_CD
     , I.ARR_WLOC_CD                          AS ARR_WLOC_CD
     , C.YD_AIM_YD_GP||C.YD_AIM_BAY_GP        AS YD_AIM_RT_GP
     , C.YD_AIM_BAY_GP                        AS YD_AIM_BAY_GP
     , B.TRANS_ORD_DATE                       AS TRANS_ORD_DATE
     , I.SHIPASSIGN_WORD_DATE                 AS SHIPASSIGN_WORD_DATE
     , I.SHIPASSIGN_WORD_SEQNO                AS SHIPASSIGN_WORD_SEQNO
     , I.SHIP_CD                              AS SHIP_CD
     , I.SHIP_NAME                            AS SHIP_NAME
     , I.RSHP_HOLD_NO                         AS RSHP_HOLD_NO
     , I.BERTH_NO                             AS BERTH_NO
     , I.SAILNO                               AS SAILNO
     , I.YD_CAR_USE_GP                        AS YD_CAR_USE_GP
     , C.CAR_NO                               AS CAR_NO
     , C.TRN_EQP_CD                           AS TRN_EQP_CD
     , I.CAR_KIND                             AS CAR_KIND
     , I.TRANS_EQUIPMENT_TYPE                 AS TRANS_EQUIPMENT_TYPE
     , C.CARD_NO                              AS CARD_NO
     , I.YD_CAR_SCH_ID                        AS YD_CAR_SCH_ID
     , ''                                     AS YD_TCAR_SCH_ID
     , C.YD_WBOOK_ID                          AS YD_WBOOK_ID
     , A.YD_CRN_SCH_ID                        AS YD_CRN_SCH_ID
     , C.YD_SCH_CD                            AS YD_SCH_CD
     , A.YD_SCH_ST_GP                         AS YD_SCH_ST_GP
     , A.YD_SCH_REQ_GP                        AS YD_SCH_REQ_GP
     , A.YD_SCH_PRIOR                         AS YD_SCH_PRIOR
     , A.YD_WBOOK_DT                          AS YD_WBOOK_DT
     , H.YD_AID_WRK_YN                        AS YD_AID_WRK_YN
     , C.YD_TO_LOC_DCSN_MTD                   AS YD_TO_LOC_DCSN_MTD
     , C.YD_TO_LOC_GUIDE                      AS YD_TO_LOC_GUIDE
     , A.YD_SCH_DT                            AS YD_SCH_DT
     , A.YD_WRK_HDS_DD                        AS YD_WRK_HDS_DD
     , A.YD_WRK_DUTY                          AS YD_WRK_DUTY
     , A.YD_WRK_PARTY                         AS YD_WRK_PARTY
     , NVL(I.YD_CARLD_LEV_LOC,J.YD_CARLD_LEV_LOC)          AS YD_CARLD_LEV_LOC
     , NVL(I.YD_CARLD_LEV_DT,J.YD_CARLD_LEV_DT)            AS YD_CARLD_LEV_DT
     , I.YD_CARLD_PNT_WO_DT                                AS YD_CARLD_PNT_WO_DT
     , I.YD_PNT_CD1                                        AS YD_PNT_CD1
     , I.YD_PNT_CD2                                        AS YD_PNT_CD2
     , NVL(I.YD_CARLD_WRK_BOOK_ID,J.YD_CARLD_WRK_BOOK_ID)  AS YD_CARLD_WRK_BOOK_ID
     , NVL(I.YD_CARLD_SCH_REQ_GP,J.YD_CARLD_SCH_REQ_GP)    AS YD_CARLD_SCH_REQ_GP
     , NVL(I.YD_CARLD_STOP_LOC,J.YD_CARLD_STOP_LOC)        AS YD_CARLD_STOP_LOC
     , NVL(I.YD_CARLD_ARR_DT,J.YD_CARLD_ARR_DT)            AS YD_CARLD_ARR_DT
     , NVL(I.YD_CARLD_ST_DT,J.YD_CARLD_ST_DT)              AS YD_CARLD_ST_DT
     , NVL(I.YD_CARLD_CMPL_DT,J.YD_CARLD_CMPL_DT)          AS YD_CARLD_CMPL_DT
     , NVL(I.YD_CARLD_WRK_ACT_GP,J.YD_CARLD_WRK_ACT_GP)    AS YD_CARLD_WRK_ACT_GP
     , I.YD_CARLD_CHK_DT                                   AS YD_CARLD_CHK_DT
     , NVL(I.YD_CARUD_LEV_DT,J.YD_CARUD_LEV_DT)            AS YD_CARUD_LEV_DT
     , I.YD_CARUD_PNT_WO_DT                                AS YD_CARUD_PNT_WO_DT
     , I.YD_PNT_CD3                                        AS YD_PNT_CD3
     , I.YD_PNT_CD4                                        AS YD_PNT_CD4
     , NVL(I.YD_CARUD_WRK_BOOK_ID,J.YD_CARUD_WRK_BOOK_ID)  AS YD_CARUD_WRK_BOOK_ID
     , NVL(I.YD_CARUD_STOP_LOC,J.YD_CARUD_STOP_LOC)        AS YD_CARUD_STOP_LOC
     , NVL(I.YD_CARUD_SCH_REQ_GP,J.YD_CARUD_SCH_REQ_GP)    AS YD_CARUD_SCH_REQ_GP
     , NVL(I.YD_CARUD_ARR_DT,J.YD_CARUD_ARR_DT)            AS YD_CARUD_ARR_DT
     , I.YD_CARUD_CHK_DT                                   AS YD_CARUD_CHK_DT
     , NVL(I.YD_CARUD_ST_DT,J.YD_CARUD_ST_DT)              AS YD_CARUD_ST_DT
     , NVL(I.YD_CARUD_CMPL_DT,J.YD_CARUD_CMPL_DT)          AS YD_CARUD_CMPL_DT
     , NVL(I.YD_CARUD_WRK_ACT_GP,J.YD_CARUD_WRK_ACT_GP)    AS YD_CARUD_WRK_ACT_GP
     , I.YD_TRN_WRK_DELY_CD        AS YD_TRN_WRK_DELY_CD
     , A.YD_EQP_ID                 AS YD_EQP_ID
     , A.YD_UP_WRK_MODE2           AS YD_UP_WRK_MODE2
     , A.YD_DN_WRK_MODE2           AS YD_DN_WRK_MODE2
     , A.YD_UP_WO_LOC              AS YD_UP_WO_LOC
     , A.YD_UP_WO_LYR              AS YD_UP_WO_LYR
     , A.YD_UP_WO_LOC_XAXIS        AS YD_UP_WO_LOC_XAXIS
     , A.YD_UP_WO_LOC_YAXIS        AS YD_UP_WO_LOC_YAXIS
     , A.YD_UP_WO_LOC_ZAXIS        AS YD_UP_WO_LOC_ZAXIS
     , A.YD_UP_WR_LOC              AS YD_UP_WR_LOC
     , A.YD_UP_WR_LYR              AS YD_UP_WR_LYR
     , A.YD_UP_WR_XAXIS            AS YD_UP_WR_XAXIS
     , A.YD_UP_WR_YAXIS            AS YD_UP_WR_YAXIS
     , A.YD_UP_WR_ZAXIS            AS YD_UP_WR_ZAXIS
     , 'M'                         AS YD_UP_WR_FUNC
     , A.YD_UP_CMPL_DT             AS YD_UP_CMPL_DT
     , A.YD_DN_WO_LOC              AS YD_DN_WO_LOC
     , A.YD_DN_WO_LYR              AS YD_DN_WO_LYR
     , A.YD_DN_WO_LOC_XAXIS        AS YD_DN_WO_LOC_XAXIS
     , A.YD_DN_WO_LOC_YAXIS        AS YD_DN_WO_LOC_YAXIS
     , A.YD_DN_WO_LOC_ZAXIS        AS YD_DN_WO_LOC_ZAXIS
     , A.YD_DN_WR_LOC              AS YD_DN_WR_LOC
     , A.YD_DN_WR_LYR              AS YD_DN_WR_LYR
     , A.YD_DN_WR_XAXIS            AS YD_DN_WR_XAXIS
     , A.YD_DN_WR_YAXIS            AS YD_DN_WR_YAXIS
     , A.YD_DN_WR_ZAXIS            AS YD_DN_WR_ZAXIS
     , 'M'                         AS YD_DN_WR_FUNC
     , A.YD_DN_CMPL_DT             AS YD_DN_CMPL_DT
  FROM TB_YF_CRNSCH A 
     , TB_YF_CRNWRKMTL H
     , TB_YF_STOCK  B 
     , TB_YF_WRKBOOK C 
     , USRPTA.TB_PT_COILCOMM G 
     , USRYDA.TB_YD_CARSCH I
     , USRYFA.TB_YF_TCARSCH J
 WHERE A.YD_CRN_SCH_ID      = H.YD_CRN_SCH_ID
   AND H.STL_NO           = B.STL_NO
   AND H.STL_NO           = G.COIL_NO(+)
   AND A.YD_WBOOK_ID        = C.YD_WBOOK_ID(+)
   AND A.YD_CRN_GRAB_USE_RULE_ID = I.YD_CAR_SCH_ID(+)
   AND A.YD_CRN_GRAB_USE_RULE_ID = J.YD_TCAR_SCH_ID(+)
   AND A.YD_CRN_SCH_ID      = :V_YD_CRN_SCH_ID
 </pre> */
public final static String insWrkHist = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkHist";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommCarSchWbDel 
--차량스케줄 작업예약ID 삭제 
UPDATE USRYDA.TB_YD_CARSCH
SET 
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    YD_CARLD_WRK_BOOK_ID    = DECODE(YD_CARLD_WRK_BOOK_ID, :V_YD_WBOOK_ID, NULL, YD_CARLD_WRK_BOOK_ID),
    YD_CARUD_WRK_BOOK_ID    = DECODE(YD_CARUD_WRK_BOOK_ID, :V_YD_WBOOK_ID, NULL, YD_CARUD_WRK_BOOK_ID)
WHERE 1=1
AND DEL_YN                  = 'N'
AND (YD_CARLD_WRK_BOOK_ID   = :V_YD_WBOOK_ID OR YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID)
 </pre> */
public final static String updCommCarSchWbDel = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommCarSchWbDel";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.insYmCrnsch
INSERT INTO TB_YF_CRNSCH
(
     YD_CRN_SCH_ID
    ,REGISTER
    ,REG_DDTT
    ,MODIFIER
    ,MOD_DDTT
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
    ,YD_UP_WO_LOC
    ,YD_UP_WO_LYR
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
    ,YD_DN_WO_LOC
    ,YD_DN_WO_LYR
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
    ,YD_UP_WR_LOC
    ,YD_UP_WR_LYR
    ,YD_UP_WRK_ACT_GP
    ,YD_UP_WR_XAXIS
    ,YD_UP_WR_YAXIS
    ,YD_UP_WR_YAXIS1
    ,YD_UP_WR_YAXIS2
    ,YD_UP_WR_ZAXIS
    ,YD_DN_WR_LOC
    ,YD_DN_WR_LYR
    ,YD_DN_WRK_ACT_GP
    ,YD_DN_WR_XAXIS
    ,YD_DN_WR_YAXIS
    ,YD_DN_WR_YAXIS1
    ,YD_DN_WR_YAXIS2
    ,YD_DN_WR_ZAXIS
    ,UP_ROTATION_ANGLE
    ,DOWN_ROTATION_ANGLE
    ,YD_DN_WO_LOC_TO
    ,YD_L2_REQUEST_STAT
    ,YD_UP_WRK_MODE2
    ,YD_DN_WRK_MODE2
    ,STL_NO_TEMP
    ,STK_LYR_NO_TEMP
    ,YD_WRK_PROG_REQ_MSG 
)
VALUES
(
     :V_YD_CRN_SCH_ID
    ,:V_REGISTER
    ,SYSDATE -- REG_DDTT
    ,:V_MODIFIER -- MODIFIER
    ,SYSDATE -- MOD_DDTT
    ,'N' -- DEL_YN
    ,:V_YD_WBOOK_ID
    ,:V_YD_EQP_ID
    ,:V_YD_GP
    ,:V_YD_BAY_GP
    ,:V_YD_SCH_CD
    ,(SELECT YD_EQP_STAT FROM TB_YF_EQP WHERE YD_EQP_ID = :V_YD_EQP_ID) --YD_SCH_ST_GP
    ,:V_YD_SCH_REQ_GP
    ,:V_YD_SCH_PRIOR
    ,:V_YD_EQP_WRK_STAT
    ,:V_YD_WRK_PROG_STAT
    ,TO_DATE(:V_YD_WBOOK_DT,'YYYYMMDDHH24MISS')  --YD_WBOOK_DT
    ,SYSDATE --YD_SCH_DT
    ,:V_YD_WORD_DT
    ,:V_YD_UP_CMPL_DT
    ,:V_YD_DN_CMPL_DT
    ,TO_CHAR(SYSDATE - (6 / 24), 'YYYYMMDD') --YD_WRK_HDS_DD
    ,CASE WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '000000' AND '065959' THEN '3' 
	  	  WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '070000' AND '145959' THEN '1'
		  WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '150000' AND '225959' THEN '2' 
		  WHEN TO_CHAR(SYSDATE,'HH24MISS') BETWEEN '230000' AND '235959' THEN '3' 
	 END --YD_WRK_DUTY
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
    ,(
        SELECT * FROM 
        (
        --//차량 ID
        SELECT B.YD_CAR_SCH_ID
          FROM USRYFA.TB_YF_WRKBOOK A
             , USRYDA.TB_YD_CARSCH B
         WHERE B.DEL_YN='N'
           AND (B.CAR_NO=A.CAR_NO OR B.TRN_EQP_CD=A.TRN_EQP_CD )
           AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
           AND ROWNUM <= 1
        UNION    
          --//대차 ID 
        SELECT B.YD_TCAR_SCH_ID 
          FROM USRYFA.TB_YF_WRKBOOK A
             , USRYFA.TB_YF_TCARSCH B
         WHERE B.DEL_YN='N'
           AND B.YD_EQP_ID=A.YD_WRK_PLAN_TCAR
           AND A.YD_WBOOK_ID = :V_YD_WBOOK_ID
           AND ROWNUM <= 1
        )
     ) --YD_CRN_GRAB_USE_RULE_ID
    ,:V_YD_UP_WO_LOC
    ,:V_YD_UP_WO_LYR
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
    ,:V_YD_DN_WO_LOC
    ,:V_YD_DN_WO_LYR
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
    ,:V_YD_UP_WR_LOC
    ,:V_YD_UP_WR_LYR
    ,:V_YD_UP_WRK_ACT_GP
    ,:V_YD_UP_WR_XAXIS
    ,:V_YD_UP_WR_YAXIS
    ,:V_YD_UP_WR_YAXIS1
    ,:V_YD_UP_WR_YAXIS2
    ,:V_YD_UP_WR_ZAXIS
    ,:V_YD_DN_WR_LOC
    ,:V_YD_DN_WR_LYR
    ,:V_YD_DN_WRK_ACT_GP
    ,:V_YD_DN_WR_XAXIS
    ,:V_YD_DN_WR_YAXIS
    ,:V_YD_DN_WR_YAXIS1
    ,:V_YD_DN_WR_YAXIS2
    ,:V_YD_DN_WR_ZAXIS
    ,:V_UP_ROTATION_ANGLE
    ,:V_DOWN_ROTATION_ANGLE
    ,:V_YD_DN_WO_LOC_TO
    ,:V_YD_L2_REQUEST_STAT
    ,:V_YD_UP_WRK_MODE2
    ,:V_YD_DN_WRK_MODE2
    ,:V_STL_NO_TEMP
    ,:V_STK_LYR_NO_TEMP
    ,:V_YD_WRK_PROG_REQ_MSG 
)
 </pre> */
public final static String insYmCrnsch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insYmCrnsch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.insYmCrnwrkmtl
INSERT INTO TB_YF_CRNWRKMTL
(
     YD_CRN_SCH_ID
    ,STL_NO
    ,REGISTER
    ,REG_DDTT
    ,MOD_DDTT
    ,MODIFIER
    ,DEL_YN
    ,YD_AID_WRK_YN
    ,YD_STK_LYR_NO
    ,YD_STK_LOT_TP
    ,YD_STK_LOT_CD
    ,HCR_GP
    ,STL_PROG_CD
    ,YD_MTL_ITEM
    ,YD_ROUTE_GP
    ,YD_TO_LOC_DCSN_MTD
)
VALUES 
(
     :V_YD_CRN_SCH_ID
    ,:V_STL_NO
    ,:V_REGISTER
    ,SYSDATE --REG_DDTT
    ,SYSDATE --MOD_DDTT
    ,:V_REGISTER --MODIFIER
    ,'N' -- DEL_YN
    ,:V_YD_AID_WRK_YN
    ,:V_YD_STK_LYR_NO
    ,:V_YD_STK_LOT_TP
    ,:V_YD_STK_LOT_CD
    ,:V_HCR_GP
    ,:V_STL_PROG_CD
    ,:V_YD_MTL_ITEM
    ,:V_YD_ROUTE_GP
    ,:V_YD_TO_LOC_DCSN_MTD
)
 </pre> */
public final static String insYmCrnwrkmtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insYmCrnwrkmtl";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStatEqp
UPDATE TB_YF_EQP
SET 
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE,
    YD_EQP_PROG_STAT    = :V_YD_EQP_PROG_STAT
WHERE 1=1
AND YD_EQP_ID           = :V_YD_EQP_ID
AND DEL_YN              = 'N'
 </pre> */
public final static String updStatEqp = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStatEqp";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.getCommWbCrnSch 
--작업예약 크레인스케줄조회 - 
SELECT 
    YD_CRN_SCH_ID
FROM 
    TB_YF_CRNSCH
WHERE 1=1
AND YD_WBOOK_ID = :V_YD_WBOOK_ID
AND DEL_YN      = 'N'
 </pre> */
public final static String getCommWbCrnSch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.getCommWbCrnSch";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updIFTestData
UPDATE TB_YF_Z_IFLAYOUT
   SET ITM_VAL9 = SUBSTRB(:V_ITM_VAL,1,200)
 WHERE IF_ID    = :V_IF_ID
   AND ITM_SEQ  = TO_NUMBER(:V_ITM_SEQ)
 </pre> */
public final static String updIFTestData = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updIFTestData";

 /** <pre> 
--yf.common.dao.updateOperatorOfWBook1
--작업예약을 오퍼레이터 지정으로 UPDATE
UPDATE TB_YF_WRKBOOK
SET
    YD_SCH_CD           = ?,
    YD_TO_LOC_DCSN_MTD  = ?,
    YD_TO_LOC_GUIDE     = ?
WHERE 1=1
AND YD_WBOOK_ID         = ?
 </pre> */
public final static String updateOperatorOfWBook1 = "bak.yf.common.dao.updateOperatorOfWBook1";

 /** <pre> 
--yf.ilkwan.session.CoilRegSBean.stackcolcarpointupdate
UPDATE USRYFA.TB_YF_STKCOL
SET
    CARD_NO     = NULL,
    MODIFIER    = 'DMYDR023',
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND CARD_NO     = ?
 </pre> */
public final static String stackcolcarpointupdate = "bak.yf.ilkwan.session.CoilRegSBean.stackcolcarpointupdate";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009WbMtlDelBySchId
UPDATE TB_YF_WRKBOOKMTL
SET    DEL_YN = 'Y'
      ,MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
WHERE  YD_WBOOK_ID = :V_YD_WBOOK_ID
AND    STL_NO IN ( 
                    SELECT STL_NO 
                    FROM   TB_YF_CRNWRKMTL
                    WHERE  YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
                 )
 </pre> */
public final static String updAxYDL009WbMtlDelBySchId = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009WbMtlDelBySchId";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009Stock
UPDATE TB_YF_STOCK
   SET MODIFIER    = :V_MODIFIER
     , MOD_DDTT    = SYSDATE
     , CARUNLOAD_PUT_LOC = :V_YD_LOC
 WHERE STL_NO    = :V_STL_NO
 </pre> */
public final static String updAxYDL009Stock = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009Stock";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkHistScrap
INSERT INTO TB_YF_WRKHIST
(
     YD_WRK_HIST_ID
    ,REGISTER
    ,REG_DDTT
    ,MODIFIER
    ,MOD_DDTT
    ,DEL_YN
    ,YD_GP
    ,STL_NO
    ,STL_APPEAR_GP
    ,ITEMNAME_CD
    ,ORD_YEOJAE_GP
    ,ORD_NO
    ,ORD_DTL
    ,STLKIND_CD
    ,SPEC_ABBSYM
    ,ORD_GP
    ,CUST_CD
    ,DEST_CD
    ,DEMANDER_CD
    ,DEST_TEL_NO
    ,STL_PROG_CD
    ,GOODS_GRADE
    ,YD_MTL_W_GP
    ,YD_MTL_T_GP
    ,YD_MTL_L_GP
    ,YD_MTL_T
    ,YD_MTL_W
    ,YD_MTL_L
    ,YD_MTL_WT
    ,YD_COIL_OUTDIA_GRP_GP
    ,COIL_INDIA
    ,COIL_OUTDIA
    ,SLAB_WO_RT_CD
    ,ORD_HCR_GP
    ,HCR_GP
    ,SCARFING_YN
    ,SCARFING_DONE_YN
    ,HYSCO_TRANS_GP
    ,RENTPROC_CD
    ,DIST_DUE_DATE
    ,FRTOMOVE_ORD_DATE
    ,URGENT_FRTOMOVE_WORD_GP
    ,SPOS_WLOC_CD
    ,ARR_WLOC_CD
    ,YD_AIM_RT_GP
    ,YD_AIM_BAY_GP
    ,TRANS_ORD_DATE
    ,SHIPASSIGN_WORD_DATE
    ,SHIPASSIGN_WORD_SEQNO
    ,SHIP_CD
    ,SHIP_NAME
    ,RSHP_HOLD_NO
    ,BERTH_NO
    ,SAILNO
    ,YD_CAR_USE_GP
    ,CAR_NO
    ,TRN_EQP_CD
    ,CAR_KIND
    ,TRANS_EQUIPMENT_TYPE
    ,CARD_NO
    ,YD_CAR_SCH_ID
    ,YD_TCAR_SCH_ID
    ,YD_WBOOK_ID
    ,YD_CRN_SCH_ID
    ,YD_SCH_CD
    ,YD_SCH_ST_GP
    ,YD_SCH_REQ_GP
    ,YD_SCH_PRIOR
    ,YD_WBOOK_DT
    ,YD_AID_WRK_YN
    ,YD_TO_LOC_DCSN_MTD
    ,YD_TO_LOC_GUIDE
    ,YD_SCH_DT
    ,YD_WRK_HDS_DD
    ,YD_WRK_DUTY
    ,YD_WRK_PARTY
    ,YD_CARLD_LEV_LOC
    ,YD_CARLD_LEV_DT
    ,YD_CARLD_PNT_WO_DT
    ,YD_PNT_CD1
    ,YD_PNT_CD2
    ,YD_CARLD_WRK_BOOK_ID
    ,YD_CARLD_SCH_REQ_GP
    ,YD_CARLD_STOP_LOC
    ,YD_CARLD_ARR_DT
    ,YD_CARLD_ST_DT
    ,YD_CARLD_CMPL_DT
    ,YD_CARLD_WRK_ACT_GP
    ,YD_CARLD_CHK_DT
    ,YD_CARUD_LEV_DT
    ,YD_CARUD_PNT_WO_DT
    ,YD_PNT_CD3
    ,YD_PNT_CD4
    ,YD_CARUD_WRK_BOOK_ID
    ,YD_CARUD_STOP_LOC
    ,YD_CARUD_SCH_REQ_GP
    ,YD_CARUD_ARR_DT
    ,YD_CARUD_CHK_DT
    ,YD_CARUD_ST_DT
    ,YD_CARUD_CMPL_DT
    ,YD_CARUD_WRK_ACT_GP
    ,YD_TRN_WRK_DELY_CD
    ,YD_EQP_ID
    ,YD_UP_WRK_MODE2
    ,YD_DN_WRK_MODE2
    ,YD_UP_WO_LOC
    ,YD_UP_WO_LYR
    ,YD_UP_WO_LOC_XAXIS
    ,YD_UP_WO_LOC_YAXIS
    ,YD_UP_WO_LOC_ZAXIS
    ,YD_UP_WR_LOC
    ,YD_UP_WR_LYR
    ,YD_UP_WR_XAXIS
    ,YD_UP_WR_YAXIS
    ,YD_UP_WR_ZAXIS
    ,YD_UP_WR_FUNC
    ,YD_UP_CMPL_DT
    ,YD_DN_WO_LOC
    ,YD_DN_WO_LYR
    ,YD_DN_WO_LOC_XAXIS
    ,YD_DN_WO_LOC_YAXIS
    ,YD_DN_WO_LOC_ZAXIS
    ,YD_DN_WR_LOC
    ,YD_DN_WR_LYR
    ,YD_DN_WR_XAXIS
    ,YD_DN_WR_YAXIS
    ,YD_DN_WR_ZAXIS
    ,YD_DN_WR_FUNC
    ,YD_DN_CMPL_DT
)
SELECT 
       TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YF_WRKHIST_SEQ.NEXTVAL AS YD_WRK_HIST_ID                 
     , :V_REGISTER             AS REGISTER                                                                            
     , SYSDATE                 AS REG_DDTT						                                                                   
     , :V_REGISTER             AS MODIFIER                      
     , SYSDATE                 AS MOD_DDTT
     , 'N'                     AS DEL_YN                              
     , A.YD_GP                 AS YD_GP
     , H.STL_NO                AS STL_NO    
     , G.STL_APPEAR_GP         AS STL_APPEAR_GP
     , G.ITEMNAME_CD           AS ITEMNAME_CD
     , G.ORD_YEOJAE_GP         AS ORD_YEOJAE_GP
     , G.ORD_NO                AS ORD_NO
     , G.ORD_DTL               AS ORD_DTL
     , ''                      AS STLKIND_CD
     , G.SPEC_ABBSYM           AS SPEC_ABBSYM
     , ''                      AS ORD_GP
     , G.CUST_CD               AS CUST_CD
     , ''                      AS DEST_CD
     , G.DEMANDER_CD           AS DEMANDER_CD
     , I.DEST_TEL_NO           AS DEST_TEL_NO
     , H.STL_PROG_CD           AS STL_PROG_CD
     , G.OVERALL_STAMP_GRADE   AS GOODS_GRADE
     
     , CASE WHEN G.COIL_W < 1601 THEN 'M' ELSE 'L' END AS YD_MTL_W_GP
     , '' AS YD_MTL_T_GP
     , '' AS YD_MTL_L_GP
     
     , G.COIL_T                                AS YD_MTL_T
     , G.COIL_W                                AS YD_MTL_W
     , G.COIL_LEN                              AS YD_MTL_L
     , G.COIL_WT                               AS YD_MTL_WT
     , CASE WHEN G.COIL_OUTDIA <=1280 THEN 'A' 
            WHEN G.COIL_OUTDIA <=1930 THEN 'B' 
                                      ELSE 'C' 
            END                                AS YD_COIL_OUTDIA_GRP_GP
     , G.COIL_INDIA                            AS COIL_INDIA
     , G.COIL_OUTDIA                           AS COIL_OUTDIA
     , ''                                     AS SLAB_WO_RT_CD
     , ''                                     AS ORD_HCR_GP
     , H.HCR_GP                               AS HCR_GP 
     , ''                                     AS SCARFING_YN
     , ''                                     AS SCARFING_DONE_YN
     , ''                                     AS HYSCO_TRANS_GP
     , I.RENTPROC_CD                          AS RENTPROC_CD
     , ''                                     AS DIST_DUE_DATE
     , G.FRTOMOVE_ORD_DATE                    AS FRTOMOVE_ORD_DATE
     , I.URGENT_FRTOMOVE_WORD_GP              AS URGENT_FRTOMOVE_WORD_GP
     , I.SPOS_WLOC_CD                         AS SPOS_WLOC_CD
     , I.ARR_WLOC_CD                          AS ARR_WLOC_CD
     , C.YD_AIM_YD_GP||C.YD_AIM_BAY_GP        AS YD_AIM_RT_GP
     , C.YD_AIM_BAY_GP                        AS YD_AIM_BAY_GP
     , B.TRANS_ORD_DATE                       AS TRANS_ORD_DATE
     , I.SHIPASSIGN_WORD_DATE                 AS SHIPASSIGN_WORD_DATE
     , I.SHIPASSIGN_WORD_SEQNO                AS SHIPASSIGN_WORD_SEQNO
     , I.SHIP_CD                              AS SHIP_CD
     , I.SHIP_NAME                            AS SHIP_NAME
     , I.RSHP_HOLD_NO                         AS RSHP_HOLD_NO
     , I.BERTH_NO                             AS BERTH_NO
     , I.SAILNO                               AS SAILNO
     , I.YD_CAR_USE_GP                        AS YD_CAR_USE_GP
     , C.CAR_NO                               AS CAR_NO
     , C.TRN_EQP_CD                           AS TRN_EQP_CD
     , I.CAR_KIND                             AS CAR_KIND
     , I.TRANS_EQUIPMENT_TYPE                 AS TRANS_EQUIPMENT_TYPE
     , C.CARD_NO                              AS CARD_NO
     , I.YD_CAR_SCH_ID                        AS YD_CAR_SCH_ID
     , ''                                     AS YD_TCAR_SCH_ID
     , C.YD_WBOOK_ID                          AS YD_WBOOK_ID
     , A.YD_CRN_SCH_ID                        AS YD_CRN_SCH_ID
     , C.YD_SCH_CD                            AS YD_SCH_CD
     , A.YD_SCH_ST_GP                         AS YD_SCH_ST_GP
     , A.YD_SCH_REQ_GP                        AS YD_SCH_REQ_GP
     , A.YD_SCH_PRIOR                         AS YD_SCH_PRIOR
     , A.YD_WBOOK_DT                          AS YD_WBOOK_DT
     , ''                                     AS YD_AID_WRK_YN
     , C.YD_TO_LOC_DCSN_MTD                   AS YD_TO_LOC_DCSN_MTD
     , C.YD_TO_LOC_GUIDE                      AS YD_TO_LOC_GUIDE
     , A.YD_SCH_DT                            AS YD_SCH_DT
     , A.YD_WRK_HDS_DD                        AS YD_WRK_HDS_DD
     , A.YD_WRK_DUTY                          AS YD_WRK_DUTY
     , A.YD_WRK_PARTY                         AS YD_WRK_PARTY
     , NVL(I.YD_CARLD_LEV_LOC,J.YD_CARLD_LEV_LOC)          AS YD_CARLD_LEV_LOC
     , NVL(I.YD_CARLD_LEV_DT,J.YD_CARLD_LEV_DT)            AS YD_CARLD_LEV_DT
     , I.YD_CARLD_PNT_WO_DT                                AS YD_CARLD_PNT_WO_DT
     , I.YD_PNT_CD1                                        AS YD_PNT_CD1
     , I.YD_PNT_CD2                                        AS YD_PNT_CD2
     , NVL(I.YD_CARLD_WRK_BOOK_ID,J.YD_CARLD_WRK_BOOK_ID)  AS YD_CARLD_WRK_BOOK_ID
     , NVL(I.YD_CARLD_SCH_REQ_GP,J.YD_CARLD_SCH_REQ_GP)    AS YD_CARLD_SCH_REQ_GP
     , NVL(I.YD_CARLD_STOP_LOC,J.YD_CARLD_STOP_LOC)        AS YD_CARLD_STOP_LOC
     , NVL(I.YD_CARLD_ARR_DT,J.YD_CARLD_ARR_DT)            AS YD_CARLD_ARR_DT
     , NVL(I.YD_CARLD_ST_DT,J.YD_CARLD_ST_DT)              AS YD_CARLD_ST_DT
     , NVL(I.YD_CARLD_CMPL_DT,J.YD_CARLD_CMPL_DT)          AS YD_CARLD_CMPL_DT
     , NVL(I.YD_CARLD_WRK_ACT_GP,J.YD_CARLD_WRK_ACT_GP)    AS YD_CARLD_WRK_ACT_GP
     , I.YD_CARLD_CHK_DT                                   AS YD_CARLD_CHK_DT
     , NVL(I.YD_CARUD_LEV_DT,J.YD_CARUD_LEV_DT)            AS YD_CARUD_LEV_DT
     , I.YD_CARUD_PNT_WO_DT                                AS YD_CARUD_PNT_WO_DT
     , I.YD_PNT_CD3                                        AS YD_PNT_CD3
     , I.YD_PNT_CD4                                        AS YD_PNT_CD4
     , NVL(I.YD_CARUD_WRK_BOOK_ID,J.YD_CARUD_WRK_BOOK_ID)  AS YD_CARUD_WRK_BOOK_ID
     , NVL(I.YD_CARUD_STOP_LOC,J.YD_CARUD_STOP_LOC)        AS YD_CARUD_STOP_LOC
     , NVL(I.YD_CARUD_SCH_REQ_GP,J.YD_CARUD_SCH_REQ_GP)    AS YD_CARUD_SCH_REQ_GP
     , NVL(I.YD_CARUD_ARR_DT,J.YD_CARUD_ARR_DT)            AS YD_CARUD_ARR_DT
     , I.YD_CARUD_CHK_DT                                   AS YD_CARUD_CHK_DT
     , NVL(I.YD_CARUD_ST_DT,J.YD_CARUD_ST_DT)              AS YD_CARUD_ST_DT
     , NVL(I.YD_CARUD_CMPL_DT,J.YD_CARUD_CMPL_DT)          AS YD_CARUD_CMPL_DT
     , NVL(I.YD_CARUD_WRK_ACT_GP,J.YD_CARUD_WRK_ACT_GP)    AS YD_CARUD_WRK_ACT_GP
     , I.YD_TRN_WRK_DELY_CD        AS YD_TRN_WRK_DELY_CD
     , A.YD_EQP_ID                 AS YD_EQP_ID
     , A.YD_UP_WRK_MODE2           AS YD_UP_WRK_MODE2
     , A.YD_DN_WRK_MODE2           AS YD_DN_WRK_MODE2
     , A.YD_UP_WO_LOC              AS YD_UP_WO_LOC
     , A.YD_UP_WO_LYR              AS YD_UP_WO_LYR
     , A.YD_UP_WO_LOC_XAXIS        AS YD_UP_WO_LOC_XAXIS
     , A.YD_UP_WO_LOC_YAXIS        AS YD_UP_WO_LOC_YAXIS
     , A.YD_UP_WO_LOC_ZAXIS        AS YD_UP_WO_LOC_ZAXIS
     , A.YD_UP_WR_LOC              AS YD_UP_WR_LOC
     , A.YD_UP_WR_LYR              AS YD_UP_WR_LYR
     , A.YD_UP_WR_XAXIS            AS YD_UP_WR_XAXIS
     , A.YD_UP_WR_YAXIS            AS YD_UP_WR_YAXIS
     , A.YD_UP_WR_ZAXIS            AS YD_UP_WR_ZAXIS
     , 'M'                         AS YD_UP_WR_FUNC
     , A.YD_UP_CMPL_DT             AS YD_UP_CMPL_DT
     , A.YD_DN_WO_LOC              AS YD_DN_WO_LOC
     , A.YD_DN_WO_LYR              AS YD_DN_WO_LYR
     , A.YD_DN_WO_LOC_XAXIS        AS YD_DN_WO_LOC_XAXIS
     , A.YD_DN_WO_LOC_YAXIS        AS YD_DN_WO_LOC_YAXIS
     , A.YD_DN_WO_LOC_ZAXIS        AS YD_DN_WO_LOC_ZAXIS
     , A.YD_DN_WR_LOC              AS YD_DN_WR_LOC
     , A.YD_DN_WR_LYR              AS YD_DN_WR_LYR
     , A.YD_DN_WR_XAXIS            AS YD_DN_WR_XAXIS
     , A.YD_DN_WR_YAXIS            AS YD_DN_WR_YAXIS
     , A.YD_DN_WR_ZAXIS            AS YD_DN_WR_ZAXIS
     , 'M'                         AS YD_DN_WR_FUNC
     , A.YD_DN_CMPL_DT             AS YD_DN_CMPL_DT
  FROM TB_YF_CRNSCH A 
     , TB_YF_CRNWRKMTL H
     , TB_YF_STOCK  B 
     , TB_YF_WRKBOOK C 
     , USRPTA.TB_PT_COILCOMM G 
     , USRYDA.TB_YD_CARSCH I
     , USRYFA.TB_YF_TCARSCH J
 WHERE A.YD_CRN_SCH_ID      = H.YD_CRN_SCH_ID
   AND H.STL_NO           = B.STL_NO
   AND H.STL_NO           = G.COIL_NO(+)
   AND A.YD_WBOOK_ID        = C.YD_WBOOK_ID(+)
   AND A.YD_CRN_GRAB_USE_RULE_ID = I.YD_CAR_SCH_ID(+)
   AND A.YD_CRN_GRAB_USE_RULE_ID = J.YD_TCAR_SCH_ID(+)
   AND A.YD_CRN_SCH_ID      = :V_YD_CRN_SCH_ID
 </pre> */
public final static String insWrkHistScrap = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkHistScrap";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009WbDel
--크레인권하실적 작업예약 삭제 
UPDATE TB_YF_WRKBOOK
   SET MODIFIER         = :V_MODIFIER
	  ,MOD_DDTT         = SYSDATE
      ,DEL_YN           = 'Y'
      ,YD_SCH_PROG_STAT = 'E' --End
 WHERE DEL_YN           = 'N'
   AND YD_WBOOK_ID = :V_YD_WBOOK_ID
   AND 1 = (SELECT DECODE(COUNT(*), 0, 1, 0) 
              FROM TB_YF_WRKBOOKMTL
             WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
               AND DEL_YN = 'N'
           )
 </pre> */
public final static String updAxYDL009WbDel = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009WbDel";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkBookMtlByStkLyr3
--적재위치대상재 작업예약재료 등록
MERGE INTO TB_YF_WRKBOOKMTL WM USING
(
    SELECT
        :V_YD_WBOOK_ID  AS YD_WBOOK_ID,     --야드작업예약ID
        SL.STL_NO       AS STL_NO,          --재료번호
        :V_MODIFIER     AS MODIFIER,        --수정자
        SYSDATE         AS MOD_DDTT,        --수정일시
        'N'             AS DEL_YN,          --삭제유무
        SL.YD_STK_COL_GP,                   --야드적치열구분
        SL.YD_STK_BED_NO,                   --야드적치Bed번호
        '01'            AS YD_STK_LYR_NO    --야드적치단번호
    FROM
        TB_YF_EQPTRACKING SL
    WHERE 1=1
    AND SL.YD_STK_COL_GP    = :V_YD_STK_COL_GP
    AND SL.YD_STK_BED_NO    = :V_YD_STK_BED_NO
    AND SL.STL_NO           IS NOT NULL
) DD
ON
(
    1=1
    AND WM.YD_WBOOK_ID  = DD.YD_WBOOK_ID
    AND WM.STL_NO       = DD.STL_NO
)
WHEN NOT MATCHED THEN
INSERT
(
    WM.YD_WBOOK_ID,
    WM.STL_NO,
    WM.REGISTER,
    WM.REG_DDTT,
    WM.MODIFIER,
    WM.MOD_DDTT,
    WM.DEL_YN,
    WM.YD_STK_COL_GP,
    WM.YD_STK_BED_NO,
    WM.YD_STK_LYR_NO
)
VALUES
(
    DD.YD_WBOOK_ID,
    DD.STL_NO,
    DD.MODIFIER,
    DD.MOD_DDTT,
    DD.MODIFIER,
    DD.MOD_DDTT,
    DD.DEL_YN,
    DD.YD_STK_COL_GP,
    DD.YD_STK_BED_NO,
    DD.YD_STK_LYR_NO
)

 </pre> */
public final static String insWrkBookMtlByStkLyr3 = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insWrkBookMtlByStkLyr3";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTrkStackColGplayer
MERGE INTO TB_YF_STKLYR SL USING
(
    SELECT
        A.YD_STK_COL_GP,
        '1'         AS YD_STK_COL_GP1,  --야드구분
        A.YD_BAY_GP AS YD_STK_COL_GP2,  --동구분
        '00'        AS YD_STK_BED_NO,
        '01'        AS YD_STK_LYR_NO,
        'E'         AS YD_STK_LYR_ACTIVE_STAT,
        'C'         AS YD_STK_LYR_STAT,
        A.STL_NO    AS STL_NO
    FROM
        TB_YF_EQPTRACKING  A
    WHERE 1=1
    AND A.STL_NO = :V_STL_NO
) DD
ON
(
    1=1
    AND SL.YD_STK_COL_GP    = DD.YD_STK_COL_GP
    AND SL.YD_STK_BED_NO    = DD.YD_STK_BED_NO
    AND SL.YD_STK_LYR_NO    = DD.YD_STK_LYR_NO
)
WHEN MATCHED THEN
    UPDATE SET
        SL.MODIFIER                 = :V_MODIFIER,
        SL.MOD_DDTT                 = SYSDATE,
        SL.YD_STK_LYR_ACTIVE_STAT   = DD.YD_STK_LYR_ACTIVE_STAT,
        SL.YD_STK_LYR_STAT          = DD.YD_STK_LYR_STAT,
        SL.STL_NO                   = DD.STL_NO
 </pre> */
public final static String updTrkStackColGplayer = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTrkStackColGplayer";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.delCrnSch
UPDATE TB_YF_CRNSCH A
   SET A.DEL_YN   = 'Y'
     , A.MODIFIER = NVL(:V_MODIFIER,'AUTO_DEL')
     , A.MOD_DDTT = SYSDATE
WHERE A.DEL_YN = 'N'
  AND A.REG_DDTT <= SYSDATE-0.002
  AND A.YD_CRN_SCH_ID NOT IN (     
                                SELECT B.YD_CRN_SCH_ID
                                  FROM TB_YF_CRNWRKMTL B
                                 WHERE A.YD_CRN_SCH_ID=B.YD_CRN_SCH_ID
                             )
 </pre> */
public final static String delCrnSch = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.delCrnSch";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointstackcolgpupdateC
--저장위치로 초기화 하는 경우(출하)
UPDATE TB_YD_CARPOINT
SET
    CARD_NO         = NULL,
    CAR_NO          = NULL,
    YD_STK_COL_ACT_STAT = DECODE(TRN_EQP_CD, NULL, :V_STAT, YD_STK_COL_ACT_STAT),
    MOD_DDTT        = SYSDATE,
    MODIFIER        = 'CarPointC'
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
 </pre> */
public final static String carpointstackcolgpupdateC = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointstackcolgpupdateC";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointtrneqpcdupdateC2
--저장위치로 차량 포인트 예약 하는 경우(출하)
UPDATE TB_YD_CARPOINT
SET
    YD_STK_COL_ACT_STAT = :V_STAT,
    CAR_NO          = :V_CAR_NO,
    CARD_NO         = :V_TRN_EQP_CD,
    MOD_DDTT        = SYSDATE,
    MODIFIER        = 'CarPointC'
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
 </pre> */
public final static String carpointtrneqpcdupdateC2 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointtrneqpcdupdateC2";

 /** <pre> 
--com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock9
UPDATE TB_YF_STKCOL
SET
    CAR_CARD_NO = ?,
    MODIFIER    = ?,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND WLOC_CD     = ?
AND YD_PNT_CD   = ?
AND SECT_GP     =
(
    --T: 해송차량 , P:빠레트
    CASE
    WHEN SUBSTR(?, 0, 1) = 'P' THEN 'PT'
    ELSE 'TR'
    END
)
 </pre> */
public final static String updYdStock9 = "bak.com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock9";

 /** <pre> 
--yf.common.YfCommonDao.MergeWrkBook 
MERGE 
 INTO TB_YF_WRKBOOK A
USING (
SELECT        :V_MODIFIER                  AS MODIFIER                 
             ,sysdate                      AS MOD_DDTT                 
             ,NVL(:V_DEL_YN,'N')                    AS DEL_YN                   
             ,:V_YD_WBOOK_ID		       AS YD_WBOOK_ID
			 ,:V_YD_GP                     AS YD_GP               
			 ,:V_YD_BAY_GP                 AS YD_BAY_GP           
			 ,:V_YD_SCH_CD                 AS YD_SCH_CD           
			 ,:V_YD_SCH_PRIOR              AS YD_SCH_PRIOR        
			 ,:V_YD_SCH_PROG_STAT          AS YD_SCH_PROG_STAT    
			 ,:V_YD_SCH_ST_GP              AS YD_SCH_ST_GP        
			 ,:V_YD_SCH_REQ_GP             AS YD_SCH_REQ_GP       
			 ,:V_YD_AIM_YD_GP              AS YD_AIM_YD_GP        
			 ,:V_YD_AIM_BAY_GP             AS YD_AIM_BAY_GP       
			 ,:V_YD_CTS_RELAY_YN           AS YD_CTS_RELAY_YN     
			 ,:V_YD_CTS_RELAY_BAY_GP       AS YD_CTS_RELAY_BAY_GP 
			 ,:V_YD_TO_LOC_DCSN_MTD        AS YD_TO_LOC_DCSN_MTD  
			 ,:V_YD_TO_LOC_GUIDE           AS YD_TO_LOC_GUIDE     
			 ,:V_YD_WRK_PLAN_TCAR          AS YD_WRK_PLAN_TCAR    
			 ,:V_YD_CAR_USE_GP             AS YD_CAR_USE_GP       
			 ,:V_TRN_EQP_CD                AS TRN_EQP_CD          
			 ,:V_CAR_NO                    AS CAR_NO              
			 ,:V_CARD_NO                   AS CARD_NO             
			 ,:V_PTOP_PLNT_GP              AS PTOP_PLNT_GP        
			 ,:V_DEST_TEL_NO               AS DEST_TEL_NO         
			 ,:V_DIST_SHIPASSIGN_GP        AS DIST_SHIPASSIGN_GP  
			 ,:V_YD_WRK_PLAN_CRN           AS YD_WRK_PLAN_CRN     
			 ,:V_SCH_CNCL_YN               AS SCH_CNCL_YN         
			 ,:V_YD_WRK_PLAN_CRN2          AS YD_WRK_PLAN_CRN2    
			 ,:V_CHARGE_LOT_NO_DIV_YN      AS CHARGE_LOT_NO_DIV_YN
			 ,:V_YD_TO_LOC_GUIDE_FNL       AS YD_TO_LOC_GUIDE_FNL 
			 ,:V_CAR_FRM_GP                AS CAR_FRM_GP   
		 FROM DUAL
      ) B
	ON (    A.YD_WBOOK_ID LIKE B.YD_WBOOK_ID  )
  WHEN MATCHED THEN
	         UPDATE 
				SET A.MODIFIER          	= DECODE(NVL(B.MODIFIER         	,A.MODIFIER         	),'-','',B.MODIFIER         	)
				   ,A.MOD_DDTT         		= DECODE(NVL(B.MOD_DDTT         	,A.MOD_DDTT         	),'-','',B.MOD_DDTT         	)
				   ,A.DEL_YN            	= DECODE(NVL(B.DEL_YN           	,A.DEL_YN           	),'-','',B.DEL_YN           	)
				   ,A.YD_GP                 = DECODE(NVL(B.YD_GP                ,A.YD_GP                ),'-','',B.YD_GP                )
				   ,A.YD_BAY_GP             = DECODE(NVL(B.YD_BAY_GP            ,A.YD_BAY_GP            ),'-','',B.YD_BAY_GP            )
                   ,A.YD_SCH_CD             = DECODE(NVL(B.YD_SCH_CD            ,A.YD_SCH_CD            ),'-','',B.YD_SCH_CD            )
                   ,A.YD_SCH_PRIOR          = DECODE(NVL(B.YD_SCH_PRIOR         ,A.YD_SCH_PRIOR         ),'-','',B.YD_SCH_PRIOR         )
                   ,A.YD_SCH_PROG_STAT      = DECODE(NVL(B.YD_SCH_PROG_STAT     ,A.YD_SCH_PROG_STAT     ),'-','',B.YD_SCH_PROG_STAT     )
                   ,A.YD_SCH_ST_GP          = DECODE(NVL(B.YD_SCH_ST_GP         ,A.YD_SCH_ST_GP         ),'-','',B.YD_SCH_ST_GP         )
                   ,A.YD_SCH_REQ_GP         = DECODE(NVL(B.YD_SCH_REQ_GP        ,A.YD_SCH_REQ_GP        ),'-','',B.YD_SCH_REQ_GP        )
                   ,A.YD_AIM_YD_GP          = DECODE(NVL(B.YD_AIM_YD_GP         ,A.YD_AIM_YD_GP         ),'-','',B.YD_AIM_YD_GP         )
                   ,A.YD_AIM_BAY_GP         = DECODE(NVL(B.YD_AIM_BAY_GP        ,A.YD_AIM_BAY_GP        ),'-','',B.YD_AIM_BAY_GP        )
                   ,A.YD_CTS_RELAY_YN       = DECODE(NVL(B.YD_CTS_RELAY_YN      ,A.YD_CTS_RELAY_YN      ),'-','',B.YD_CTS_RELAY_YN      )
                   ,A.YD_CTS_RELAY_BAY_GP   = DECODE(NVL(B.YD_CTS_RELAY_BAY_GP  ,A.YD_CTS_RELAY_BAY_GP  ),'-','',B.YD_CTS_RELAY_BAY_GP  )
                   ,A.YD_TO_LOC_DCSN_MTD    = DECODE(NVL(B.YD_TO_LOC_DCSN_MTD   ,A.YD_TO_LOC_DCSN_MTD   ),'-','',B.YD_TO_LOC_DCSN_MTD   )
                   ,A.YD_TO_LOC_GUIDE       = DECODE(NVL(B.YD_TO_LOC_GUIDE      ,A.YD_TO_LOC_GUIDE      ),'-','',B.YD_TO_LOC_GUIDE      )
                   ,A.YD_WRK_PLAN_TCAR      = DECODE(NVL(B.YD_WRK_PLAN_TCAR     ,A.YD_WRK_PLAN_TCAR     ),'-','',B.YD_WRK_PLAN_TCAR     )
                   ,A.YD_CAR_USE_GP         = DECODE(NVL(B.YD_CAR_USE_GP        ,A.YD_CAR_USE_GP        ),'-','',B.YD_CAR_USE_GP        )
                   ,A.TRN_EQP_CD            = DECODE(NVL(B.TRN_EQP_CD           ,A.TRN_EQP_CD           ),'-','',B.TRN_EQP_CD           )
                   ,A.CAR_NO                = DECODE(NVL(B.CAR_NO               ,A.CAR_NO               ),'-','',B.CAR_NO               )
                   ,A.CARD_NO               = DECODE(NVL(B.CARD_NO              ,A.CARD_NO              ),'-','',B.CARD_NO              )
                   ,A.PTOP_PLNT_GP          = DECODE(NVL(B.PTOP_PLNT_GP         ,A.PTOP_PLNT_GP         ),'-','',B.PTOP_PLNT_GP         )
                   ,A.DEST_TEL_NO           = DECODE(NVL(B.DEST_TEL_NO          ,A.DEST_TEL_NO          ),'-','',B.DEST_TEL_NO          )
                   ,A.DIST_SHIPASSIGN_GP    = DECODE(NVL(B.DIST_SHIPASSIGN_GP   ,A.DIST_SHIPASSIGN_GP   ),'-','',B.DIST_SHIPASSIGN_GP   )
                   ,A.YD_WRK_PLAN_CRN       = DECODE(NVL(B.YD_WRK_PLAN_CRN      ,A.YD_WRK_PLAN_CRN      ),'-','',B.YD_WRK_PLAN_CRN      )
                   ,A.SCH_CNCL_YN           = DECODE(NVL(B.SCH_CNCL_YN          ,A.SCH_CNCL_YN          ),'-','',B.SCH_CNCL_YN          )
                   ,A.YD_WRK_PLAN_CRN2      = DECODE(NVL(B.YD_WRK_PLAN_CRN2     ,A.YD_WRK_PLAN_CRN2     ),'-','',B.YD_WRK_PLAN_CRN2     )
                   ,A.CHARGE_LOT_NO_DIV_YN  = DECODE(NVL(B.CHARGE_LOT_NO_DIV_YN ,A.CHARGE_LOT_NO_DIV_YN ),'-','',B.CHARGE_LOT_NO_DIV_YN )
                   ,A.YD_TO_LOC_GUIDE_FNL   = DECODE(NVL(B.YD_TO_LOC_GUIDE_FNL  ,A.YD_TO_LOC_GUIDE_FNL  ),'-','',B.YD_TO_LOC_GUIDE_FNL  )
                   ,A.CAR_FRM_GP            = DECODE(NVL(B.CAR_FRM_GP           ,A.CAR_FRM_GP           ),'-','',B.CAR_FRM_GP           )
WHEN NOT MATCHED THEN
			INSERT ( YD_WBOOK_ID
					,REGISTER
					,REG_DDTT
					,MODIFIER
					,MOD_DDTT
					,DEL_YN
					,YD_GP
					,YD_BAY_GP
					,YD_SCH_CD
					,YD_SCH_PRIOR
					,YD_SCH_PROG_STAT
					,YD_SCH_ST_GP
					,YD_SCH_REQ_GP
					,YD_AIM_YD_GP
					,YD_AIM_BAY_GP
					,YD_CTS_RELAY_YN
					,YD_CTS_RELAY_BAY_GP
					,YD_TO_LOC_DCSN_MTD
					,YD_TO_LOC_GUIDE
					,YD_WRK_PLAN_TCAR
					,YD_CAR_USE_GP
					,TRN_EQP_CD
					,CAR_NO
					,CARD_NO
					,PTOP_PLNT_GP
					,DEST_TEL_NO
					,DIST_SHIPASSIGN_GP
					,YD_WRK_PLAN_CRN
					,SCH_CNCL_YN
					,YD_WRK_PLAN_CRN2
					,CHARGE_LOT_NO_DIV_YN
					,YD_TO_LOC_GUIDE_FNL
					,CAR_FRM_GP )
			VALUES ( B.YD_WBOOK_ID
					,B.MODIFIER
					,B.MOD_DDTT
					,B.MODIFIER
					,B.MOD_DDTT
					,'N'
					,B.YD_GP
					,B.YD_BAY_GP
					,B.YD_SCH_CD
					,B.YD_SCH_PRIOR
					,B.YD_SCH_PROG_STAT
					,B.YD_SCH_ST_GP
					,B.YD_SCH_REQ_GP
					,B.YD_AIM_YD_GP
					,B.YD_AIM_BAY_GP
					,B.YD_CTS_RELAY_YN
					,B.YD_CTS_RELAY_BAY_GP
					,B.YD_TO_LOC_DCSN_MTD
					,B.YD_TO_LOC_GUIDE
					,B.YD_WRK_PLAN_TCAR
					,B.YD_CAR_USE_GP
					,B.TRN_EQP_CD
					,B.CAR_NO
					,B.CARD_NO
					,B.PTOP_PLNT_GP
					,B.DEST_TEL_NO
					,B.DIST_SHIPASSIGN_GP
					,B.YD_WRK_PLAN_CRN
					,B.SCH_CNCL_YN
					,B.YD_WRK_PLAN_CRN2
					,B.CHARGE_LOT_NO_DIV_YN
					,B.YD_TO_LOC_GUIDE_FNL
					,B.CAR_FRM_GP )
 </pre> */
public final static String MergeWrkBook = "bak.yf.common.YfCommonDao.MergeWrkBook";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateZoneInOfStock
--관제 ReSchedul 확정에 따른 '장입LOT번호'를 UPDATE
UPDATE TB_YF_STOCK
SET 
    CHARGE_LOT_NO   = :V_CHARGE_LOT_NO,
    STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM,
    MODIFIER        = 'SYSTEM',
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updateZoneInOfStock = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateZoneInOfStock";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteAllWbookId2
UPDATE TB_YF_WRKBOOKMTL A
SET
    DEL_YN      = 'Y',
    MODIFIER    = ?,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND NOT EXISTS 
(
    SELECT 
        * 
    FROM 
        TB_YF_STOCK
    WHERE 1=1
    AND YD_WBOOK_ID = A.YD_WBOOK_ID
)
 </pre> */
public final static String deleteAllWbookId2 = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteAllWbookId2";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updStackLayerByStockId3   
UPDATE TB_YF_STKLYR
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , YD_STK_LYR_STAT	= 'U'
WHERE STL_NO   = :V_STL_NO
 </pre> */
public final static String updStackLayerByStockId3 = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStackLayerByStockId3";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYfStockTransOrd
UPDATE USRYFA.TB_YF_STOCK
SET
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    TRANS_ORD_DATE  = :V_NEW_TRANS_WORD_DATE,
    TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
WHERE 1=1
AND TRANS_ORD_DATE  = :V_OLD_TRANS_WORD_DATE
AND TRANS_ORD_SEQNO = :V_OLD_TRANS_WORD_SEQNO
 </pre> */
public final static String updYfStockTransOrd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYfStockTransOrd";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCarMtlReset
UPDATE TB_YD_CARFTMVMTL
   SET DEL_YN = 'Y'
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updCarMtlReset = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCarMtlReset";

 /** <pre> 
--com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock3
UPDATE TB_YF_STOCK
SET
    CAR_CARD_NO     = ' ',
    STOCK_MOVE_TERM = ?,
    MOD_DDTT        = SYSDATE,
    MODIFIER        = ?,
    DEL_YN          = 'Y'
WHERE 1=1
AND STL_NO          = ?

 </pre> */
public final static String updYdStock3 = "bak.com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock3";

 /** <pre> 
-- com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock10 
UPDATE TB_YF_STOCK
SET
    CAR_CARD_NO = NULL
WHERE 1=1
AND CAR_CARD_NO = ?
 </pre> */
public final static String updYdStock10 = "bak.com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock10";

 /** <pre> 
--com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock8
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM = ?,
    TRANS_ORD_DATE  = ?,
    TRANS_ORD_SEQNO = ?,
    CAR_CARD_NO     = ?,
    MODIFIER        = ?,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO          = ?
 </pre> */
public final static String updYdStock8 = "bak.com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock8";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStock2
UPDATE USRYFA.TB_YF_STOCK
SET
    DEL_YN          = :V_DEL_YN,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updateStock2 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStock2";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommTcarSchWbDel 
--대차스케줄 작업예약ID 삭제
UPDATE USRYFA.TB_YF_TCARSCH
SET
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    YD_CARLD_WRK_BOOK_ID    = DECODE(YD_CARLD_WRK_BOOK_ID, :V_YD_WBOOK_ID, NULL, YD_CARLD_WRK_BOOK_ID),
    YD_CARUD_WRK_BOOK_ID    = DECODE(YD_CARUD_WRK_BOOK_ID, :V_YD_WBOOK_ID, NULL, YD_CARUD_WRK_BOOK_ID)
WHERE 1=1
AND DEL_YN                  = 'N'
AND (YD_CARLD_WRK_BOOK_ID   = :V_YD_WBOOK_ID OR YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID)
 </pre> */
public final static String updCommTcarSchWbDel = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommTcarSchWbDel";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelYnWrkBookMtl 
--작업예약재료 삭제
UPDATE TB_YF_WRKBOOKMTL
SET 
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE,
    DEL_YN      = 'Y'
WHERE 1=1
AND YD_WBOOK_ID = :V_YD_WBOOK_ID
AND DEL_YN      = 'N'
 </pre> */
public final static String updDelYnWrkBookMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelYnWrkBookMtl";

 /** <pre> 
--com.inisteel.cim.yf.jsp.coiljsp.dao.CoilGdsJspDao.delCarSchMtlLayer
UPDATE USRYFA.TB_YF_STKLYR
SET 
    STL_NO          = NULL, 
    YD_STK_LYR_STAT = 'E',
    MODIFIER        = 'delCar',
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO IN
(
    --//일반 상차인 경우
    SELECT 
        B.GOODS_NO 
    FROM 
        USRYDA.TB_YD_CARSCH A,
        TB_DM_TRANSWORDGOODS B
    WHERE 1=1
    AND A.TRANS_ORD_DATE = B.TRANS_WORD_DATE
    AND A.TRANS_ORD_SEQNO = B.TRANS_WORD_SEQNO
    AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
    AND B.DEL_YN        = 'N'
    AND A.CMBN_CARLD_YN <> 'S' --조합상차 아닌경우
    
    UNION
    
    --//조합상차인 경우
    SELECT 
        C.GOODS_NO 
    FROM 
        USRYDA.TB_YD_CARSCH A,
        TB_DM_TRANSWORDCOMM B ,
        TB_DM_TRANSWORDGOODS C
    WHERE 1=1
    AND A.TRANS_ORD_DATE || A.TRANS_ORD_SEQNO = B.CMBN_CARLD_NO 
    AND C.TRANS_WORD_DATE = B.TRANS_WORD_DATE
    AND C.TRANS_WORD_SEQNO = B.TRANS_WORD_SEQNO
    AND B.DEL_YN        = 'N'
    AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
    AND A.CMBN_CARLD_YN <> 'S'  --조합상차 아닌경우
    
    UNION 
    
    SELECT 
        STL_NO 
    FROM 
        USRYDA.TB_YD_CARFTMVMTL
    WHERE 1=1
    AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
)
 </pre> */
public final static String delCarSchMtlLayer = "bak.com.inisteel.cim.yf.jsp.coiljsp.dao.CoilGdsJspDao.delCarSchMtlLayer";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointtrneqpcdupdate
--설비코드로 초기화 하는 경우(구내운송)
UPDATE TB_YD_CARPOINT
SET
    TRN_EQP_CD  = NULL,
    YD_STK_COL_ACT_STAT = DECODE(CARD_NO, NULL, (DECODE(YD_STK_COL_ACT_STAT, 'N', 'N', :V_STAT)), YD_STK_COL_ACT_STAT),
    MOD_DDTT    = SYSDATE,
    MODIFIER    = 'CarPointin'
WHERE 1=1
AND TRN_EQP_CD  = :V_TRN_EQP_CD
AND MOD_DDTT    <> SYSDATE
 </pre> */
public final static String carpointtrneqpcdupdate = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointtrneqpcdupdate";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockTransInfo_06
UPDATE TB_YF_STOCK
SET
    STOCK_ITEM      = (CASE WHEN KEEP_STL_YN ='Y' THEN STOCK_ITEM ELSE :V_STOCK_ITEM END),
    STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updateStockTransInfo_06 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockTransInfo_06";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009CarSchUd
--크레인권하실적 하차 차량스케줄 수정 
UPDATE TB_YD_CARSCH
   SET MODIFIER         = :V_MODIFIER
	  ,MOD_DDTT         = SYSDATE
      ,YD_EQP_WRK_STAT  = 'U' --공차
      ,YD_CAR_PROG_STAT = 'E' --하차완료
      ,YD_CARUD_CMPL_DT = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
   AND DEL_YN           = 'N'
 </pre> */
public final static String updAxYDL009CarSchUd = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updAxYDL009CarSchUd";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.clrUpDnWrkMtl
UPDATE TB_YF_STKLYR
   SET MODIFIER            = :V_MODIFIER
     , MOD_DDTT            = SYSDATE
     , STL_NO            = NULL
     , YD_STK_LYR_STAT = 'E'
 WHERE 1=1 --YD_STK_LYR_STAT IN ('U','D')
   AND STL_NO  IN (
            SELECT STL_NO 
              FROM TB_YF_CRNWRKMTL
             WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
       )
AND    SUBSTR(YD_STK_COL_GP,1,1) IN ('1','0')
 </pre> */
public final static String clrUpDnWrkMtl = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.clrUpDnWrkMtl";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updTcarSchLoc
UPDATE TB_YF_TCARSCH
   SET YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC --하차위치
     , YD_CARLD_STOP_LOC = :V_YD_CARLD_STOP_LOC --상차위치
     , YD_CAR_PROG_STAT  = 'A'
     , MODIFIER          = :V_MODIFIER
     , MOD_DDTT          = SYSDATE
 WHERE YD_TCAR_SCH_ID =(SELECT YD_TCAR_SCH_ID
                          FROM TB_YF_EQP    EQ
                             , TB_YF_TCARSCH  TS
                         WHERE EQ.YD_EQP_ID  = TS.YD_EQP_ID(+)
                           AND EQ.YD_EQP_ID  = :V_YD_EQP_ID      
                           AND 'N'          = TS.DEL_YN(+)
                       )
 </pre> */
public final static String updTcarSchLoc = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updTcarSchLoc";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateLoadTimeToPT
update TB_PT_STLFRTOMOVE
set FRTOMOVE_CARLOAD_DATE = TO_CHAR(sysdate,'YYYYMMDD')
where STL_NO = :V_STL_NO
and TRANSWORD_SEQNO = 
(select max(TRANSWORD_SEQNO)
from TB_PT_STLFRTOMOVE
where STL_NO = :V_STL_NO
and FRTOMOVE_STAT_CD NOT IN ('Z','C')
)
 </pre> */
public final static String updateLoadTimeToPT = "bak.yf.facilitywork.putwrecord.session.updateLoadTimeToPT";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updA7YDL009StkLyr
UPDATE TB_YF_STKLYR
   SET STL_NO = ( SELECT STL_NO 
			          FROM TB_YF_CRNWRKMTL
			          WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
			      ) -- 코일은 1개만
     , MODIFIER            = :V_MODIFIER
	 , MOD_DDTT            = SYSDATE
	 , YD_STK_LYR_STAT = 'C'
 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
 </pre> */
public final static String updA7YDL009StkLyr = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updA7YDL009StkLyr";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.upheatingCoilYN
UPDATE TB_YF_STOCK 
SET HEATING_COIL_YN = CASE :V_HEATING_COIL_YN WHEN '1' THEN 'Y' ELSE 'N' END
      , MODIFIER = :V_MODIFIER 
     , MOD_DDTT = sysdate
WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String upheatingCoilYN = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.upheatingCoilYN";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updA7YML009_5WR
MERGE INTO TB_YF_CRNSCH CS USING (
SELECT SL.YD_STK_COL_GP
     , SL.YD_STK_BED_NO
     , SL.YD_STK_LYR_NO
     , SL.YD_STK_LYR_X_AXIS
     , SL.YD_STK_LYR_Y_AXIS
     , ROUND(CASE WHEN WR_YD_STK_LYR_NO = '02' THEN SF_YM_WO_LOC_ZAXIS_AUTO(SUBSTR(WR_STACK_LOC_GP,1,6),SUBSTR(WR_STACK_LOC_GP,7,2), CC.COIL_OUTDIA)
                  ELSE NVL(YD_STK_LYR_Z_AXIS ,0) + ( CC.COIL_OUTDIA / 2 )           
              END)  AS YD_STK_LYR_Z_AXIS
     , CS.YD_CRN_SCH_ID
  FROM TB_YF_STKLYR  SL
     ,(
       SELECT :V_WR_STACK_LOC_GP                 AS WR_STACK_LOC_GP
            , SUBSTR(:V_WR_YD_STK_LYR_NO, 2, 2)  AS WR_YD_STK_LYR_NO
         FROM DUAL
      ) P
     , TB_YF_CRNSCH    CS
     , TB_YF_CRNWRKMTL CM
     , TB_PT_COILCOMM  CC
 WHERE SL.YD_STK_COL_GP = SUBSTR(P.WR_STACK_LOC_GP, 1, 6)
   AND SL.YD_STK_BED_NO = SUBSTR(P.WR_STACK_LOC_GP, 7, 2)
   AND SL.YD_STK_LYR_NO = P.WR_YD_STK_LYR_NO
   AND CS.YD_CRN_SCH_ID = CM.YD_CRN_SCH_ID
   AND CM.STL_NO        = CC.COIL_NO
   AND CS.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 ) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
 WHEN MATCHED THEN UPDATE SET
      CS.MODIFIER       = 'F1YFL009_5'
    , CS.MOD_DDTT       = SYSDATE
    , CS.YD_DN_WR_LOC   = DD.YD_STK_COL_GP||DD.YD_STK_LYR_NO
    , CS.YD_DN_WR_LYR   = DD.YD_STK_LYR_NO
    , CS.YD_DN_WR_XAXIS = DD.YD_STK_LYR_X_AXIS
    , CS.YD_DN_WR_YAXIS = DD.YD_STK_LYR_Y_AXIS
    , CS.YD_DN_WR_ZAXIS = DD.YD_STK_LYR_Z_AXIS
 </pre> */
public final static String updA7YML009_5WR = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updA7YML009_5WR";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointstackcolgpupdateCT
--저장위치로 초기화 하는 경우(구내운송)
UPDATE TB_YD_CARPOINT
SET 
    TRN_EQP_CD      = NULL,
    YD_STK_COL_ACT_STAT = DECODE(CARD_NO, NULL, (DECODE(YD_STK_COL_ACT_STAT, 'N', 'N', :V_STAT)), YD_STK_COL_ACT_STAT),
    MOD_DDTT        = SYSDATE,
    MODIFIER        = 'CarPointCT'
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
 </pre> */
public final static String carpointstackcolgpupdateCT = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointstackcolgpupdateCT";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointtrneqpcdupdateC
--저장위치로 차량 포인트 예약 하는 경우(구내운송)
UPDATE TB_YD_CARPOINT
SET
    YD_STK_COL_ACT_STAT = :V_STAT,
    TRN_EQP_CD          = :V_TRN_EQP_CD,
    MOD_DDTT            = SYSDATE,
    MODIFIER            = 'CarPointCP'
WHERE 1=1
AND YD_STK_COL_GP       = :V_YD_STK_COL_GP
 </pre> */
public final static String carpointtrneqpcdupdateC = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.carpointtrneqpcdupdateC";

 /** <pre> 
--yf.common.dao.updatePossibleOfStacker
--적치대의 적재능력을 초기화 한다.
UPDATE TB_YF_STKBED
SET
    YD_STK_BED_QNTY_CURR = 0,
    YD_STK_BED_WT_CURR   = 0,
    YD_STK_BED_HIGH_CURR = 0,
    YD_STK_BED_W_CURR    = 0,
    YD_STK_BED_LEN_CURR  = 0,
    YD_STK_BED_ABLE_QNTY = YD_STK_BED_QNTY_MAX,
    YD_STK_BED_ABLE_WT   = YD_STK_BED_WT_MAX,
    YD_STK_BED_ABLE_HIGH = YD_STK_BED_HIGH_MAX
WHERE 1=1
AND YD_STK_COL_GP = ?
AND YD_STK_BED_NO = ?
 </pre> */
public final static String updatePossibleOfStacker = "bak.yf.common.dao.updatePossibleOfStacker";

 /** <pre> 
-- com.inisteel.cim.yf.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSchMtl 
UPDATE USRYDA.TB_YD_CARFTMVMTL
SET 
    DEL_YN          = 'Y',
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
 </pre> */
public final static String delCarWrMgtCarSchMtl = "bak.com.inisteel.cim.yf.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSchMtl";

 /** <pre> 
--com.inisteel.cim.yf.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSch
UPDATE USRYDA.TB_YD_CARSCH
SET 
    DEL_YN          = 'Y',
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
 </pre> */
public final static String delCarWrMgtCarSch = "bak.com.inisteel.cim.yf.jsp.coiljsp.dao.CoilGdsJspDao.delCarWrMgtCarSch";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updWrkBookPrior1
--작업예약 스케쥴우선순위 수정
UPDATE TB_YF_WRKBOOK
   SET MODIFIER     = :V_MODIFIER
     , MOD_DDTT     = SYSDATE
     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
 WHERE YD_WBOOK_ID  = :V_YD_WBOOK_ID
   AND DEL_YN       = 'N'
 </pre> */
public final static String updWrkBookPrior1 = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updWrkBookPrior1";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockTransInfo
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM = DECODE(STOCK_MOVE_TERM,'BD',STOCK_MOVE_TERM, :V_STOCK_MOVE_TERM),
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updateStockTransInfo = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockTransInfo";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStockTransInfo
MERGE INTO TB_YF_STOCK ST USING
(
    SELECT
        :V_STL_NO           	AS STL_NO,          	--재료번호
        :V_MODIFIER         	AS MODIFIER,        	--수정자
        SYSDATE             	AS MOD_DDTT,        	--수정일시
        'N'                 	AS DEL_YN,          	--삭제유무
        :V_STOCK_ITEM       	AS STOCK_ITEM,      	--저장품 품목
        :V_STOCK_MOVE_TERM  	AS STOCK_MOVE_TERM, 	--저장품 이동 조건
        :V_YD_CAR_UPP_LOC_CD	AS YD_CAR_UPP_LOC_CD,	--차상위치코드
        :V_TRANS_ORD_DATE   	AS TRANS_ORD_DATE,  	--운송지시
        :V_TRANS_ORD_SEQNO  	AS TRANS_ORD_SEQNO, 	--운송지시순번
        :V_CAR_CARD_NO      	AS CAR_CARD_NO,     	--카드번호
        :V_CAR_NO           	AS CAR_NO,              --차량번호
        :V_CR_FRTOMOVE_GP       AS CR_FRTOMOVE_GP       --냉연이송구분
    FROM
        DUAL
) DD 
ON ( ST.STL_NO = DD.STL_NO )
WHEN NOT MATCHED THEN
    INSERT
    (
        STL_NO,
        REGISTER,
        REG_DDTT,
        MODIFIER,
        MOD_DDTT,
        DEL_YN,
        STOCK_ITEM,
        STOCK_MOVE_TERM,
        YD_CAR_UPP_LOC_CD,
        TRANS_ORD_DATE,
        TRANS_ORD_SEQNO,
        CAR_CARD_NO,
        CAR_NO,
        CR_FRTOMOVE_GP
    )
    VALUES
    (
        DD.STL_NO,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.MODIFIER,
        DD.MOD_DDTT,
        'N',
        DD.STOCK_ITEM,
        DD.STOCK_MOVE_TERM,
        DD.YD_CAR_UPP_LOC_CD,
        DD.TRANS_ORD_DATE,
        DD.TRANS_ORD_SEQNO,
        DD.CAR_CARD_NO,
        DD.CAR_NO,
        DD.CR_FRTOMOVE_GP
    )
WHEN MATCHED THEN
    UPDATE SET
        MODIFIER        	= DD.MODIFIER,
        MOD_DDTT        	= DD.MOD_DDTT,
        DEL_YN          	= DD.DEL_YN,
        STOCK_ITEM      	= DD.STOCK_ITEM,
        STOCK_MOVE_TERM		= DD.STOCK_MOVE_TERM,
        YD_CAR_UPP_LOC_CD	= DD.YD_CAR_UPP_LOC_CD,
        TRANS_ORD_DATE  	= DD.TRANS_ORD_DATE,
        TRANS_ORD_SEQNO 	= DD.TRANS_ORD_SEQNO,
        CAR_CARD_NO     	= DD.CAR_CARD_NO,
        CAR_NO          	= DD.CAR_NO,
        CR_FRTOMOVE_GP      = DD.CR_FRTOMOVE_GP
 </pre> */
public final static String insStockTransInfo = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStockTransInfo";

 /** <pre> 
--yf.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId1
--저장품 Table(TB_YF_STOCK)에  STOCK_MOVE_TERM Update 한다.
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM = ?
WHERE 1=1
AND STL_NO          = ?
 </pre> */
public final static String updateYdStockStockId1 = "bak.yf.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId1";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updStackMaxQnty 
UPDATE TB_YF_EQP
   SET 
       STK_MAX_QNTY = :V_STK_MAX_QNTY
     , STK_MAX_WT = :V_STK_MAX_WT
-- 없음    , EQP_DIR_TO_LOC = V_EQP_DIR_TO_LOC 
     , MODIFIER       = :V_MODIFIER
     , MOD_DDTT       = SYSDATE
 WHERE YD_EQP_ID = :V_YD_EQP_ID
 </pre> */
public final static String updStackMaxQnty = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStackMaxQnty";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockDelYnInfo
UPDATE TB_YF_STOCK
SET
    DEL_YN      = 'Y',
    MODIFIER    = 'SYSTEM',
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND STL_NO      = :V_STL_NO
 </pre> */
public final static String updateStockDelYnInfo = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockDelYnInfo";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayer
UPDATE TB_YF_STKLYR
SET 
    STL_NO                  = NULL,
    YD_STK_LYR_ACTIVE_STAT  = 'E',
    YD_STK_LYR_STAT         = 'E',
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE
WHERE 1=1
AND STL_NO                  = :V_STL_NO
AND SUBSTR(YD_STK_COL_GP, 1, 1) = '1'
 </pre> */
public final static String updStackLayer = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayer";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommTcarSchWbDelUd
--대차스케줄 작업예약ID 삭제 
UPDATE TB_YF_TCARSCH
   SET MODIFIER              = :V_MODIFIER
      ,MOD_DDTT              = SYSDATE
      ,YD_CARUD_WRK_BOOK_ID  =NULL
 WHERE DEL_YN                = 'N'
   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String updCommTcarSchWbDelUd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommTcarSchWbDelUd";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.AcoilDAO.updTCarYdGpMgt 
UPDATE TB_YF_EQP
   SET 
       YD_TCAR_WRK_ABLE_BAY1    =:V_YD_TCAR_WRK_ABLE_BAY1    -- 야드대차작업가능동1
      ,YD_TCAR_WRK_ABLE_BAY2    =:V_YD_TCAR_WRK_ABLE_BAY2    -- 야드대차작업가능동2
      ,YD_TCAR_WRK_ABLE_BAY3    =:V_YD_TCAR_WRK_ABLE_BAY3    -- 야드대차작업가능동3
      ,YD_TCAR_WRK_ABLE_BAY4    =:V_YD_TCAR_WRK_ABLE_BAY4    -- 야드대차작업가능동4
      ,YD_TCAR_WRK_ABLE_BAY5    =:V_YD_TCAR_WRK_ABLE_BAY5    -- 야드대차작업가능동5
      ,YD_TCAR_WRK_ABLE_BAY6    =:V_YD_TCAR_WRK_ABLE_BAY6    -- 야드대차작업가능동6
      ,MODIFIER                 =:V_MODIFIER                 -- 수정자
      ,MOD_DDTT                 = SYSDATE
 WHERE YD_EQP_ID                =:V_YD_EQP_ID                -- 설비구분
 </pre> */
public final static String updTCarYdGpMgt = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updTCarYdGpMgt";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCarschTransOrd
UPDATE USRYDA.TB_YD_CARSCH
SET
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    TRANS_ORD_DATE  = :V_NEW_TRANS_WORD_DATE,
    TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO
WHERE 1=1
AND TRANS_ORD_DATE  = :V_OLD_TRANS_WORD_DATE
AND TRANS_ORD_SEQNO = :V_OLD_TRANS_WORD_SEQNO
 </pre> */
public final static String updYdCarschTransOrd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCarschTransOrd";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnWrkMgtSCCrnMtl 
--크레인작업관리 크레인스케줄취소 재료 수정 - 
UPDATE TB_YF_CRNWRKMTL
SET 
    MODIFIER = :V_MODIFIER,
    MOD_DDTT = SYSDATE,
    DEL_YN   = 'Y'
WHERE 1=1
AND DEL_YN   = 'N'
AND YD_CRN_SCH_ID IN
(
    SELECT 
        YD_CRN_SCH_ID
    FROM 
        TB_YF_CRNSCH
    WHERE 1=1
    AND YD_WBOOK_ID = :V_YD_WBOOK_ID
    AND DEL_YN      = 'N'
)
 </pre> */
public final static String updCrnWrkMgtSCCrnMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnWrkMgtSCCrnMtl";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnWrkMgtSCCrnMtlUnitMtl 
--크레인작업관리 크레인스케줄취소 재료 수정 - 
UPDATE TB_YF_CRNWRKMTL
SET 
    MODIFIER = :V_MODIFIER, 
    MOD_DDTT = SYSDATE, 
    DEL_YN   = 'Y'
WHERE 1=1
AND DEL_YN   = 'N'
AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updCrnWrkMgtSCCrnMtlUnitMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnWrkMgtSCCrnMtlUnitMtl";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnWrkMgtSCCrnSch 
UPDATE TB_YF_CRNSCH
SET 
    MODIFIER = :V_MODIFIER,
    MOD_DDTT = SYSDATE,
    DEL_YN   = 'Y'
WHERE 1=1
AND YD_CRN_SCH_ID IN
(
    SELECT 
        YD_CRN_SCH_ID
    FROM 
        TB_YF_CRNSCH
    WHERE 1=1
    AND YD_WBOOK_ID = :V_YD_WBOOK_ID
    AND DEL_YN      = 'N'
)
 </pre> */
public final static String updCrnWrkMgtSCCrnSch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnWrkMgtSCCrnSch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayerMtlStat 
UPDATE TB_YF_STKLYR            
   SET MOD_DDTT     = SYSDATE             
     , MODIFIER     = :V_MODIFIER             
     , YD_STK_LYR_STAT = NVL(:V_YD_STK_LYR_STAT,YD_STK_LYR_STAT)
 WHERE YD_STK_COL_GP LIKE :V_YD_STK_COL_GP ||'%'
   AND YD_STK_BED_NO LIKE :V_YD_STK_BED_NO ||'%'
   AND YD_STK_LYR_NO LIKE :V_YD_STK_LYR_NO ||'%'
   AND STL_NO        = :V_STL_NO
 </pre> */
public final static String updStackLayerMtlStat = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayerMtlStat";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayerMtlStatByStockId  
UPDATE TB_YF_STKLYR            
   SET MOD_DDTT     = SYSDATE             
     , MODIFIER     = :V_MODIFIER             
     , YD_STK_LYR_STAT = NVL(:V_YD_STK_LYR_STAT,YD_STK_LYR_STAT)
 WHERE SUBSTR(YD_STK_COL_GP,1,1) = '1'
   AND STL_NO                    = :V_STL_NO
 </pre> */
public final static String updStackLayerMtlStatByStockId = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStackLayerMtlStatByStockId";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insSchLog
INSERT INTO TB_YF_SCHLOG
     ( YF_SCHLOG_ID
	 , STL_NO
	 , YD_CRN_SCH_ID
	 , YD_GP
	 , YD_SCH_CD
	 , REGISTER
	 , REG_DDTT
	 , MODIFIER
	 , MOD_DDTT
	 , DEL_YN
	 , SCH_CONTENTS
	 , SORT_SEQ
     )
VALUES
     ( YF_SCHLOG_ID_SEQ.NEXTVAL
	 , :V_STL_NO
	 , :V_YD_CRN_SCH_ID
	 , :V_YD_GP
	 , :V_YD_SCH_CD
	 , 'log'
	 , SYSDATE
	 , 'log'
	 , SYSDATE
     , 'N'	 
	 , :V_SCH_CONTENTS
	 , :V_SORT_SEQ
     )
 </pre> */
public final static String insSchLog = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insSchLog";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCrnSchProgStat 
UPDATE TB_YF_CRNSCH
   SET YD_L2_REQUEST_STAT  = :V_YD_L2_REQUEST_STAT,
       MODIFIER            = :V_MODIFIER,
       MOD_DDTT            = SYSDATE
WHERE YD_CRN_SCH_ID       = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updYdCrnSchProgStat = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCrnSchProgStat";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCTSLoc
UPDATE TB_YF_EQP 
SET YD_CURR_BAY_GP= SUBSTR(:V_YD_CURR_BAY_GP,2,1)
  , MODIFIER = :V_MODIFIER
  , MOD_DDTT = SYSDATE
WHERE YD_EQP_ID=  :V_YD_EQP_ID 
 </pre> */
public final static String updCTSLoc = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCTSLoc";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStackStatByTrnEqpCd
UPDATE TB_YF_STKCOL
SET
    CAR_CARD_NO = '',
    YD_STK_STAT = '',
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND CAR_CARD_NO = :V_CAR_CARD_NO
 </pre> */
public final static String updStackStatByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStackStatByTrnEqpCd";

 /** <pre> 
--com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock2_2
UPDATE TB_YF_STOCK
SET
    KEEP_STL_YN = ?,
    MODIFIER    = 'SYSTEM',
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND STL_NO      = ?
 </pre> */
public final static String updYdStock2_2 = "bak.com.inisteel.cim.yf.dao.ydstockdao.YdStockDao.updYdStock2_2";

 /** <pre> 
--yf.ilkwan.dao.YdStockDAO.updateListWlocSLAB_02
UPDATE TB_YF_STKCOL 
   SET CAR_CARD_NO = DECODE(:V_CAR_CARD_NO,'Y','','N','9999')
 WHERE (WLOC_CD, YD_PNT_CD)=(SELECT WLOC_CD
                                   ,YD_PNT_CD
                               FROM TB_YF_STKCOL
                              WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP)
 </pre> */
public final static String updateListWlocSLAB_02 = "bak.yf.ilkwan.dao.YdStockDAO.updateListWlocSLAB_02";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updPRI007
UPDATE TB_YF_RULE
SET MODIFIER = :V_MODIFIER
   ,MOD_DDTT = SYSDATE
   ,DTL_ITEM1 = :V_DTL_ITEM1
WHERE REPR_CD_GP = 'PRI007'
 </pre> */
public final static String updPRI007 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updPRI007";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateMatlFtmvTimeMSlab
UPDATE TB_PT_MSLABCOMM  
   set MATL_FTMV_DT = sysdate
WHERE MSLAB_NO = :V_MSLAB_NO
 </pre> */
public final static String updateMatlFtmvTimeMSlab = "bak.yf.facilitywork.putwrecord.session.updateMatlFtmvTimeMSlab";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateUpperlayerStat
update TB_YF_STKLYR
set YD_STK_LYR_ACTIVE_STAT = NVL(:V_YD_STK_LYR_ACTIVE_STAT,YD_STK_LYR_ACTIVE_STAT) ,
    YD_STK_LYR_STAT = NVL(:V_YD_STK_LYR_STAT,YD_STK_LYR_STAT)
where YD_STK_COL_GP = :V_YD_STK_COL_GP
  and YD_STK_BED_NO = :V_YD_STK_BED_NO
  and YD_STK_LYR_NO > :V_YD_STK_LYR_NO 
 </pre> */
public final static String updateUpperlayerStat = "bak.yf.facilitywork.putwrecord.session.updateUpperlayerStat";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updA7YDL009CrnMtl
--크레인권하실적 크레인작업재료 삭제 -
UPDATE TB_YF_CRNWRKMTL
   SET MODIFIER      = :V_MODIFIER
	  ,MOD_DDTT      = SYSDATE
      ,DEL_YN        = 'Y'
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
   AND DEL_YN        = 'N'

 </pre> */
public final static String updA7YDL009CrnMtl = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updA7YDL009CrnMtl";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockZoneGp
UPDATE TB_YF_STOCK
SET
    YD_ZONE_GP  = :V_YD_ZONE_GP,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE STL_NO  IN (
    SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STL_NO
    FROM (SELECT :V_ARR_STL_NO AS SSTL_NOS FROM DUAL)
    CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL
)
 </pre> */
public final static String updateStockZoneGp = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockZoneGp";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.upYfCrnSchLocStat 
-- 무인크레인 적치위치 임시저장 
UPDATE TB_YF_CRNSCH
   SET YD_DN_WO_LOC_TO    = :V_YD_DN_WO_LOC_TO
     , STL_NO_TEMP        = :V_STL_NO_TEMP
     , STK_LYR_NO_TEMP    = :V_STK_LYR_NO_TEMP
     , YD_L2_REQUEST_STAT = :V_YD_L2_REQUEST_STAT
     , MODIFIER           = :V_MODIFIER
     , MOD_DDTT           = SYSDATE
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String upYfCrnSchLocStat = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.upYfCrnSchLocStat";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnWrkMgtSCCrnSchUnitMtl 
UPDATE TB_YF_CRNSCH
SET 
    MODIFIER = :V_MODIFIER,
    MOD_DDTT = SYSDATE,
    DEL_YN   = 'Y'
WHERE 1=1
AND YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updCrnWrkMgtSCCrnSchUnitMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnWrkMgtSCCrnSchUnitMtl";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insA7YML009WbMtlTCarIns
INSERT INTO TB_YF_WRKBOOKMTL WM
       (WM.YD_WBOOK_ID          , WM.STL_NO       , WM.REGISTER       , WM.REG_DDTT    ,
        WM.MODIFIER             , WM.MOD_DDTT       , WM.DEL_YN         , WM.YD_STK_COL_GP,
        WM.YD_STK_BED_NO         , WM.YD_STK_LYR_NO , WM.YD_UP_COLL_SEQ)
VALUES (:V_YD_CARUD_WRK_BOOK_ID , :V_STL_NO       , :V_MODIFIER       , SYSDATE        ,
        :V_MODIFIER             , SYSDATE           , 'N'               , :V_YD_STK_COL_GP,
        :V_YD_STK_BED_NO         , :V_YD_STK_LYR_NO , :V_YD_UP_COLL_SEQ)
 </pre> */
public final static String insA7YML009WbMtlTCarIns = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insA7YML009WbMtlTCarIns";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updA7YML009TcarSchLd
-- 대차스케줄 수정
MERGE INTO TB_YF_TCARSCH TS USING (
SELECT TM.YD_TCAR_SCH_ID
     , :V_MODIFIER              AS MODIFIER
     , :V_YD_CAR_PROG_STAT      AS YD_CAR_PROG_STAT
     , TM.YD_EQP_WRK_SH
     , TM.YD_EQP_WRK_WT
     , :V_YD_WBOOK_ID           AS YD_CARLD_WRK_BOOK_ID
     , :V_YD_STK_COL_GP          AS YD_CARLD_STOP_LOC
     , :V_YD_CARLD_WRK_CRN      AS YD_CARLD_WRK_CRN
     , NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE) AS WR_DT
     , WB.YD_WBOOK_ID           AS YD_CARUD_WRK_BOOK_ID
     -- 1냉연으로 가는 대차 출하는 하차 스케쥴을 만들지 않는다 3ATC12UM
     , NVL(:V_YD_CARUD_STOP_LOC,YD_CARUD_STOP_LOC)     AS YD_CARUD_STOP_LOC 
  FROM TB_YF_WRKBOOK WB
      ,(SELECT TM.YD_TCAR_SCH_ID
              , YD_CARUD_STOP_LOC      AS YD_CARUD_STOP_LOC 
              ,COUNT(*)                AS YD_EQP_WRK_SH
              ,SUM(ST.COIL_WT)         AS YD_EQP_WRK_WT
              ,:V_YD_CARUD_WRK_BOOK_ID AS YD_CARUD_WRK_BOOK_ID
          FROM TB_YF_TCARSCH TS
              ,TB_YF_TCARFTMVMTL TM
              ,USRPTA.TB_PT_COILCOMM    ST
         WHERE TM.YD_TCAR_SCH_ID = TS.YD_TCAR_SCH_ID
           AND TM.STL_NO       = ST.COIL_NO
           AND TM.YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
           AND TM.DEL_YN         = 'N'
         GROUP BY TM.YD_TCAR_SCH_ID , YD_CARUD_STOP_LOC ) TM
  WHERE TM.YD_CARUD_WRK_BOOK_ID = WB.YD_WBOOK_ID(+)
) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
WHEN MATCHED THEN UPDATE SET
     TS.MODIFIER             = DD.MODIFIER
    ,TS.MOD_DDTT             = SYSDATE
    ,TS.YD_EQP_WRK_STAT      = 'L'                      --야드설비작업상태 :영차
    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT      --야드차량진행상태 
    ,TS.YD_EQP_WRK_SH        = DD.YD_EQP_WRK_SH         --야드설비작업매수
    ,TS.YD_EQP_WRK_WT        = DD.YD_EQP_WRK_WT         --야드설비작업중량
    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID  --야드상차작업예약ID
    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC     --야드상차정지위치
    ,TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT,DD.WR_DT)  --야드상차개시일시
    ,TS.YD_CARLD_CMPL_DT     = DECODE(DD.YD_CAR_PROG_STAT,'5',DD.WR_DT,NULL)  --야드상차완료일시
    ,TS.YD_CARLD_WRK_CRN     = DD.YD_CARLD_WRK_CRN      --야드상차작업크레인 
    ,TS.YD_CARUD_WRK_BOOK_ID = DD.YD_CARUD_WRK_BOOK_ID  --야드하차작업예약ID 
    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC     --야드하차정지위치 
 </pre> */
public final static String updA7YML009TcarSchLd = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updA7YML009TcarSchLd";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insAxYML009CarMtlIns
--크레인권하실적 차량이송재료 등록 

INSERT INTO USRYDA.TB_YD_CARFTMVMTL
     ( YD_CAR_SCH_ID
     , STL_NO   
     , REGISTER     
     , REG_DDTT     
     , MODIFIER     
     , MOD_DDTT 
     , DEL_YN       
     , YD_STK_BED_NO
     , YD_STK_LYR_NO   )
(
 SELECT :V_YD_CAR_SCH_ID
      , CM.STL_NO
      , :V_MODIFIER     
      , SYSDATE         
      , :V_MODIFIER     
      , SYSDATE         
      , 'N'             
      , NVL(SUBSTR(NVL(:V_YD_DN_WR_LOC, YD_DN_WO_LOC),-2),'01') AS YD_STK_BED_NO
      , '001' AS YD_STK_LYR_NO
  FROM TB_YF_CRNWRKMTL CM
      ,TB_YF_STOCK     ST
      ,TB_YF_CRNSCH CR
 WHERE CM.STL_NO      = ST.STL_NO
   AND CM.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
   AND CM.YD_CRN_SCH_ID = CR.YD_CRN_SCH_ID
   AND CM.DEL_YN        = 'N'
)
 </pre> */
public final static String insAxYML009CarMtlIns = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insAxYML009CarMtlIns";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML005TcarSchUd
--크레인권상실적 하차 대차스케줄 수정 
UPDATE TB_YF_TCARSCH
   SET MODIFIER          = :V_MODIFIER
      ,MOD_DDTT          = SYSDATE
      ,YD_CAR_PROG_STAT  = 'D' --하차개시
      ,YD_CARUD_ST_DT    = NVL(YD_CARUD_ST_DT,SYSDATE)
      ,YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
      ,YD_CARUD_WRK_CRN  = :V_YD_CARUD_WRK_CRN
 WHERE YD_TCAR_SCH_ID    = :V_YD_TCAR_SCH_ID
   AND DEL_YN            = 'N'
 </pre> */
public final static String updAxYML005TcarSchUd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML005TcarSchUd";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock
--저장품이동조건 수정
UPDATE TB_YF_STOCK
SET
	STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM,
	MOD_DDTT        = SYSDATE,
	MODIFIER        = :V_MODIFIER,
	DEL_YN          = 'N'
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updYdStock = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updCrnWrkMgtDnLoc
--크레인작업관리  - 
MERGE INTO TB_YF_CRNSCH SC USING (   
SELECT A.YD_EQP_ID                AS YD_EQP_ID                 --야드설비ID
     , A.YD_CRN_SCH_ID            AS YD_CRN_SCH_ID                 --야드설비ID
     , A.YD_UP_WO_LOC             AS YD_UP_WO_LOC              --야드권상지시위치
     , A.YD_UP_WO_LYR           AS YD_UP_WO_LYR            --야드권상지시단
     , B.YD_STK_LYR_X_AXIS       AS YD_UP_WO_LOC_XAXIS        --야드권상지시X축
     , C.YD_STK_BED_XAXIS_TOL     AS YD_UP_WO_XAXIS_GAP_MAX    --야드권상지시X축오차최대
     , C.YD_STK_BED_XAXIS_TOL     AS YD_UP_WO_XAXIS_GAP_MIN    --야드권상지시X축오차최소
     , B.YD_STK_LYR_Y_AXIS       AS YD_UP_WO_LOC_YAXIS
     , C.YD_STK_BED_YAXIS_TOL     AS YD_UP_WO_YAXIS_GAP_MAX    --야드권상지시Z축오차최대
     , C.YD_STK_BED_YAXIS_TOL     AS YD_UP_WO_YAXIS_GAP_MIN    --야드권상지시Z축오차최소
     , 0                          AS YD_UP_WO_LOC_ZAXIS        --야드권상지시Z축
     , C.YD_STK_BED_ZAXIS_TOL     AS YD_UP_WO_ZAXIS_GAP_MAX    --야드권상지시Z축오차최대
     , C.YD_STK_BED_ZAXIS_TOL     AS YD_UP_WO_ZAXIS_GAP_MIN    --야드권상지시Z축오차최소
     , (SELECT ROTATION_ANGLE FROM TB_YF_STKCOL WHERE YD_STK_COL_GP = SUBSTR(A.YD_UP_WO_LOC,1,6)) AS ROTATION_ANGLE
     , :V_MODIFIER                AS MODIFIER              
     , SYSDATE                    AS MOD_DDTT  
     , :V_YD_DN_WO_LOC            AS YD_DN_WO_LOC
     --추가
     , (SELECT COUNT(*) 
          FROM TB_YF_CRNWRKMTL 
          WHERE YD_CRN_SCH_ID = A.YD_CRN_SCH_ID) AS YD_EQP_WRK_SH 
 FROM TB_YF_CRNSCH A
    , TB_YF_STKLYR B
    , TB_YF_STKBED C
WHERE A.YD_CRN_SCH_ID     = :V_YD_CRN_SCH_ID 
  AND SUBSTR(A.YD_UP_WO_LOC,1,6)    = B.YD_STK_COL_GP
  AND SUBSTR(A.YD_UP_WO_LOC,7,2)    = B.YD_STK_BED_NO
  AND A.YD_UP_WO_LYR                = B.YD_STK_LYR_NO
  AND SUBSTR(A.YD_UP_WO_LOC,1,6)    = C.YD_STK_COL_GP
  AND SUBSTR(A.YD_UP_WO_LOC,7,2)    = C.YD_STK_BED_NO
  AND A.DEL_YN = 'N'
) DD ON (SC.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID )
 WHEN MATCHED THEN UPDATE SET
      SC.MODIFIER               = DD.MODIFIER
     ,SC.MOD_DDTT               = DD.MOD_DDTT
     ,SC.YD_DN_WO_LOC           = DD.YD_DN_WO_LOC
     ,SC.YD_UP_WO_LOC_XAXIS     = DD.YD_UP_WO_LOC_XAXIS
     ,SC.YD_UP_WO_XAXIS_GAP_MAX = DD.YD_UP_WO_XAXIS_GAP_MAX
     ,SC.YD_UP_WO_XAXIS_GAP_MIN = DD.YD_UP_WO_XAXIS_GAP_MIN
     ,SC.YD_UP_WO_LOC_YAXIS     = DD.YD_UP_WO_LOC_YAXIS
     ,SC.YD_UP_WO_YAXIS_GAP_MAX = DD.YD_UP_WO_YAXIS_GAP_MAX
     ,SC.YD_UP_WO_YAXIS_GAP_MIN = DD.YD_UP_WO_YAXIS_GAP_MIN
     ,SC.YD_UP_WO_LOC_ZAXIS     = DD.YD_UP_WO_LOC_ZAXIS
     ,SC.YD_UP_WO_ZAXIS_GAP_MAX = DD.YD_UP_WO_ZAXIS_GAP_MAX
     ,SC.YD_UP_WO_ZAXIS_GAP_MIN = DD.YD_UP_WO_ZAXIS_GAP_MIN     
     ,SC.UP_ROTATION_ANGLE      = DD.ROTATION_ANGLE
     --추가
     ,SC.YD_EQP_WRK_SH          = DD.YD_EQP_WRK_SH
 

 </pre> */
public final static String updCrnWrkMgtDnLoc = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updCrnWrkMgtDnLoc";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.setCTSAreaBaySeq
MERGE INTO TB_YF_RULE
      USING DUAL
      ON 
      (
         REPR_CD_GP = :V_REPR_CD_GP
         AND CD_GP  = :V_CD_GP
         AND ITEM   = :V_ITEM
      )
      
WHEN MATCHED THEN
UPDATE SET
    DTL_ITEM1 = :V_DTL_ITEM1,
    DTL_ITEM2 = :V_DTL_ITEM2,
    DTL_ITEM3 = :V_DTL_ITEM3,
    MODIFIER = :V_MODIFIER,
    MOD_DDTT = SYSDATE
      
WHEN NOT MATCHED THEN
INSERT
(   
    REPR_CD_GP,
    CD_GP,
    ITEM,
    DTL_ITEM1,
    DTL_ITEM2,
    DTL_ITEM3,
    REGISTER,
    REG_DDTT,
    MODIFIER,
    MOD_DDTT,
    DEL_YN
)
VALUES
(
    :V_REPR_CD_GP,
    :V_CD_GP,
    :V_ITEM,
    :V_DTL_ITEM1,
    :V_DTL_ITEM2,
    :V_DTL_ITEM3,
    :V_MODIFIER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N'
)
 </pre> */
public final static String setCTSAreaBaySeq = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.setCTSAreaBaySeq";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updEqpOprnStat2
UPDATE TB_YF_EQP 
SET YD_EQP_PROG_STAT= :V_YD_EQP_PROG_STAT
  , YD_CURR_BAY_GP = :V_YD_CURR_BAY_GP
  , MODIFIER = :V_MODIFIER
  , MOD_DDTT = SYSDATE
WHERE YD_EQP_ID=  :V_YD_EQP_ID
 </pre> */
public final static String updEqpOprnStat2 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updEqpOprnStat2";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateCarPointStat 
UPDATE USRYDA.TB_YD_CARPOINT A
SET YD_STK_COL_ACT_STAT=:V_STAT
  , TRN_EQP_CD=(CASE WHEN :V_STAT='C' AND  (SELECT MAX(A.TRN_EQP_CD) 
                                               FROM  TB_YD_CARSCH B
                                              WHERE B.DEL_YN='N'
                                                AND A.TRN_EQP_CD=B.TRN_EQP_CD
                                              ) IS NULL
                     THEN NULL ELSE A.TRN_EQP_CD END)
    , CAR_NO=(CASE WHEN :V_STAT='C' AND  (SELECT MAX(A.CARD_NO) 
                                               FROM  TB_YD_CARSCH B
                                              WHERE B.DEL_YN='N'
                                                AND A.CARD_NO=B.CARD_NO
                                              ) IS NULL
                     THEN NULL ELSE A.CAR_NO END)    
    , CARD_NO=(CASE WHEN :V_STAT='C' AND  (SELECT MAX(A.CARD_NO) 
                                           FROM  TB_YD_CARSCH B
                                          WHERE B.DEL_YN='N'
                                            AND A.CARD_NO=B.CARD_NO
                                          ) IS NULL
                 THEN NULL ELSE A.CARD_NO END)                  
  , MOD_DDTT=SYSDATE
  , MODIFIER=:V_MODIFIER
 WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD
 </pre> */
public final static String updateCarPointStat = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateCarPointStat";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insCarExaminationjlNEW
INSERT INTO USRYDA.TB_YD_EXAMINATIONCHKLIST(
       TRANS_ORD_DATE
     , TRANS_ORD_SEQNO
     , STL_NO
     , YD_GP
     , CAR_NO
     , CARD_NO
     , GATE_NM
     , BAY_GP
     , YD_CARPNT_CD
     , YD_CAR_UPP_LOC_CD
)
WITH TEMP_TABLE AS (
SELECT :V_STL_NO AS STL_NO FROM DUAL
)
select distinct *
from
(
SELECT NVL(E.TRANS_WORD_DATE , D.TRANS_ORD_DATE) 
     , NVL(E.TRANS_WORD_SEQNO, D.TRANS_ORD_SEQNO)
     , D.STL_NO 
     , F.YD_GP 
     , NVL(E.CAR_NO, B.CAR_NO) 
     , NVL(E.CARD_NO,CAR_CARD_NO) 
     , A.GATE_NAME 
     , F.YD_BAY_GP  
     , NVL( G.CARLD_PNT_CD,B.YD_CARPNT_CD) AS CARLD_PNT_CD  
     , D.YD_CAR_UPP_LOC_CD
  FROM (
        SELECT SUBSTR(ITEM,1,1) AS YD_GP
             , SUBSTR(ITEM,2,1) AS BAY_GP
             , ITEM             AS YD_STK_COL_GP
             , REPR_CD_CONTENTS AS GATE_NAME
          FROM TB_YF_RULE
         WHERE REPR_CD_GP = 'YM02'
           AND DEL_YN = 'N'
        ) A 
     , TB_YD_CARPOINT B
     , TB_YF_STOCK D
     , TB_DM_TRANSWORDCOMM@DL_SMDB E
     , TB_PT_COILCOMM F
     , TB_DM_TRANSWORDGOODS@DL_SMDB G
 WHERE D.TRANS_ORD_DATE   = E.TRANS_WORD_DATE(+)   
   AND D.TRANS_ORD_SEQNO  = E.TRANS_WORD_SEQNO(+)
   AND D.TRANS_ORD_DATE   = G.TRANS_WORD_DATE(+)
   AND D.TRANS_ORD_SEQNO  = G.TRANS_WORD_SEQNO(+)
   AND D.TRANS_ORD_SEQNO  < 700000
   AND D.STL_NO  = G.GOODS_NO(+)
   AND D.STL_NO  = F.COIL_NO
   AND A.GATE_NAME = B.YD_CARPNT_DESC(+)
   AND A.YD_GP     = B.YD_GP(+)
   AND A.BAY_GP    = B.YD_BAY_GP(+)
   --AND SUBSTR(F.YD_STR_LOC,1,6)=TRIM(A.YD_STK_COL_GP(+))  
   AND CASE WHEN SUBSTR(F.YD_STR_LOC,3,2) = 'PT' THEN SUBSTR(F.YD_STR_LOC,1,6)
            ELSE (SELECT YD_STK_COL_GP  FROM TB_YF_STKLYR WHERE  SUBSTR(YD_STK_COL_GP,3,2) = 'PT' AND STL_NO = D.STL_NO AND ROWNUM = 1 )

            END = TRIM(A.YD_STK_COL_GP)  
   AND D.STL_NO = (SELECT * FROM TEMP_TABLE)
   AND NOT EXISTS (
                     SELECT 1
                       FROM USRYDA.TB_YD_EXAMINATIONCHKLIST K
                      WHERE K.TRANS_ORD_DATE  = E.TRANS_WORD_DATE
                        AND K.TRANS_ORD_SEQNO = E.TRANS_WORD_SEQNO
                        AND K.STL_NO=D.STL_NO)
   AND ROWNUM<=1
 UNION ALL
--조합상차
SELECT D.TRANS_ORD_DATE , D.TRANS_ORD_SEQNO, D.STL_NO 
     , F.YD_GP , E.CAR_NO ,E.CARD_NO , A.GATE_NAME , F.YD_BAY_GP  
     , NVL( G.CARLD_PNT_CD,B.YD_CARPNT_CD) AS CARLD_PNT_CD  ,D.YD_CAR_UPP_LOC_CD
  FROM (
        SELECT SUBSTR(ITEM,1,1) AS YD_GP
             , SUBSTR(ITEM,2,1) AS BAY_GP
             , ITEM             AS YD_STK_COL_GP
             , REPR_CD_CONTENTS AS GATE_NAME
          FROM TB_YF_RULE
         WHERE REPR_CD_GP = 'YM02'
           AND DEL_YN = 'N'
        ) A 
     , TB_YD_CARPOINT B
     , TB_YF_STOCK D
     , TB_DM_TRANSWORDCOMM@DL_SMDB E
     , TB_PT_COILCOMM F
     , TB_DM_TRANSWORDGOODS@DL_SMDB G
 WHERE D.TRANS_ORD_DATE||LPAD(D.TRANS_ORD_SEQNO,6,'0') = E.CMBN_CARLD_NO
   AND E.TRANS_WORD_DATE  = G.TRANS_WORD_DATE 
   AND E.TRANS_WORD_SEQNO = G.TRANS_WORD_SEQNO 
   AND D.STL_NO         = G.GOODS_NO 
   AND D.STL_NO         = F.COIL_NO
   AND A.GATE_NAME        = B.YD_CARPNT_DESC(+)
   AND A.YD_GP            = B.YD_GP(+)
   AND A.BAY_GP           = B.YD_BAY_GP(+)
--   AND SUBSTR(REPLACE(F.YD_STR_LOC,'TR','PT'),1,6)=TRIM(A.YD_STK_COL_GP(+))  
   AND CASE WHEN SUBSTR(F.YD_STR_LOC,3,2) = 'PT' THEN SUBSTR(F.YD_STR_LOC,1,6)
            ELSE (SELECT YD_STK_COL_GP  FROM TB_YF_STKLYR WHERE  SUBSTR(YD_STK_COL_GP,3,2) = 'PT' AND STL_NO = D.STL_NO AND ROWNUM = 1 )

            END = TRIM(A.YD_STK_COL_GP)  
 
   AND D.STL_NO = (SELECT * FROM TEMP_TABLE)
   AND NOT EXISTS (
                     SELECT 1
                       FROM USRYDA.TB_YD_EXAMINATIONCHKLIST K
                      WHERE K.TRANS_ORD_DATE=D.TRANS_ORD_DATE
                        AND K.TRANS_ORD_SEQNO=D.TRANS_ORD_SEQNO
                        AND K.STL_NO=D.STL_NO)
   AND ROWNUM<=1
 UNION ALL
 --임가공대상
SELECT NVL(E.FRTOMOVE_WORD_DATE , D.TRANS_ORD_DATE)  
     , NVL(E.FRTOMOVE_WORD_SEQNO, D.TRANS_ORD_SEQNO)
     , D.STL_NO 
     , F.YD_GP 
     , E.CAR_NO  
     , NVL(E.CARD_NO,CAR_CARD_NO) 
     , A.GATE_NAME , F.YD_BAY_GP  
     , NVL( G.CARLD_PNT_CD,B.YD_CARPNT_CD) AS CARLD_PNT_CD  ,D.YD_CAR_UPP_LOC_CD
  FROM (
        SELECT SUBSTR(ITEM,1,1) AS YD_GP
             , SUBSTR(ITEM,2,1) AS BAY_GP
             , ITEM             AS YD_STK_COL_GP
             , REPR_CD_CONTENTS AS GATE_NAME
          FROM TB_YF_RULE
         WHERE REPR_CD_GP = 'YM02'
           AND DEL_YN = 'N'
       ) A 
     , TB_YD_CARPOINT B
     , TB_YF_STOCK D
     , TB_DM_COILFRTOMOVEWORDCOMM @DL_SMDB E
     , TB_PT_COILCOMM F
     , TB_DM_COILFRTOMOVEWORDDETAIL @DL_SMDB G
 WHERE D.TRANS_ORD_DATE   = E.FRTOMOVE_WORD_DATE(+) 
   AND D.TRANS_ORD_SEQNO  = E.FRTOMOVE_WORD_SEQNO(+) 
   AND D.TRANS_ORD_DATE   = G.FRTOMOVE_WORD_DATE(+)
   AND D.TRANS_ORD_SEQNO  = G.FRTOMOVE_WORD_SEQNO(+)
   AND D.TRANS_ORD_SEQNO  > 700000
   AND D.STL_NO        = G.COIL_NO(+)
   AND D.STL_NO        = F.COIL_NO
   AND A.GATE_NAME       = B.YD_CARPNT_DESC(+)
   AND A.YD_GP           = B.YD_GP(+)
   AND A.BAY_GP          = B.YD_BAY_GP(+)
--   AND SUBSTR(F.YD_STR_LOC,1,6)=TRIM(A.YD_STK_COL_GP(+))  
   AND CASE WHEN SUBSTR(F.YD_STR_LOC,3,2) = 'PT' THEN SUBSTR(F.YD_STR_LOC,1,6)
            ELSE (SELECT YD_STK_COL_GP  FROM TB_YF_STKLYR WHERE  SUBSTR(YD_STK_COL_GP,3,2) = 'PT' AND STL_NO = D.STL_NO AND ROWNUM = 1 )
            END = TRIM(A.YD_STK_COL_GP)  
   AND D.STL_NO =(SELECT * FROM TEMP_TABLE)
   AND NOT EXISTS (
                     SELECT 1
                       FROM USRYDA.TB_YD_EXAMINATIONCHKLIST K
                      WHERE K.TRANS_ORD_DATE=E.FRTOMOVE_WORD_DATE
                        AND K.TRANS_ORD_SEQNO=E.FRTOMOVE_WORD_SEQNO
                        AND K.STL_NO=D.STL_NO)
   AND ROWNUM<=1
   )
 </pre> */
public final static String insCarExaminationjlNEW = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insCarExaminationjlNEW";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark_01
-- 적치단  Table Update(적치단 상태 변경)
UPDATE TB_YF_STKLYR
SET
    YD_STK_LYR_STAT = ?
WHERE 1=1
AND STL_NO          = ?
AND YD_STK_LYR_STAT IN ('S','L','U')
 </pre> */
public final static String updateStackLayerStatMark_01 = "bak.yf.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark_01";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchLocSrch
MERGE INTO TB_YF_SCHLOCSRCH USING (
    SELECT  :V_YD_SCH_CD            AS V_YD_SCH_CD
		  , :V_YD_STK_COL_GP        AS V_YD_STK_COL_GP
		  , :V_MODIFIER             AS V_MODIFIER
		  , :V_YD_LOC_SRCH_RNG_SEQ  AS V_YD_LOC_SRCH_RNG_SEQ
		  , :V_YD_ROUTE_GP          AS V_YD_ROUTE_GP
          , :V_YD_AIM_BAY_GP        AS V_YD_AIM_BAY_GP
      FROM  DUAL
) ON (  
            YD_SCH_CD    = V_YD_SCH_CD
		AND YD_STK_COL_GP = V_YD_STK_COL_GP
	    AND YD_ROUTE_GP  = V_YD_ROUTE_GP
     )
WHEN MATCHED THEN
    UPDATE SET YD_LOC_SRCH_RNG_SEQ = V_YD_LOC_SRCH_RNG_SEQ
             , YD_AIM_BAY_GP = V_YD_AIM_BAY_GP
    		 , MODIFIER = V_MODIFIER
    		 , MOD_DDTT = SYSDATE
	    	 , DEL_YN   = 'N'
WHEN NOT MATCHED THEN
    INSERT  (
		          YD_SCH_CD
		        , YD_STK_COL_GP
		        , REGISTER
		        , REG_DDTT
		        , MODIFIER
		        , MOD_DDTT
		        , DEL_YN
		        , YD_LOC_SRCH_RNG_SEQ
		        , YD_ROUTE_GP
                , YD_AIM_BAY_GP
		    ) VALUES (
		          V_YD_SCH_CD
		        , V_YD_STK_COL_GP
		        , V_MODIFIER
		        , SYSDATE
		        , V_MODIFIER
		        , SYSDATE
		        , 'N'
		        , V_YD_LOC_SRCH_RNG_SEQ
		        , V_YD_ROUTE_GP
                , V_YD_AIM_BAY_GP
		    )                           
                        
 </pre> */
public final static String updSchLocSrch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchLocSrch";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.setPageHelpBtnDtlInfo
-- 박판열연 YF 화면 도움말 - 작업방법(버튼상세)등록
INSERT INTO USRYFA.TB_YF_HELP_BTNDTL B(
      B.PAGE_ID   -- 화면ID   
    , B.BTN_ID    -- 버튼ID   
    , B.BTN_SEQ   -- 순번     
    , B.BTN_CMNT  -- 버튼설명 
    , B.DEL_YN    -- 삭제여부 
    , B.REGISTER  -- 등록자   
    , B.REG_DDTT  -- 등록 일시
    , B.MODIFIER  -- 수정자   
    , B.MOD_DDTT  -- 수정 일시
) VALUES (
      :V_PAGE_ID   -- 화면ID   
    , :V_BTN_ID    -- 버튼ID   
    , :V_BTN_SEQ   -- 순번
    , :V_BTN_CMNT  -- 버튼설명
    , 'N'          -- 삭제여부 
    , :V_REGISTER  -- 등록자   
    , SYSDATE      -- 등록 일시
    , :V_MODIFIER  -- 수정자   
    , SYSDATE      -- 수정 일시 
)
 </pre> */
public final static String setPageHelpBtnDtlInfo = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.setPageHelpBtnDtlInfo";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delPageHelpBtnDtlInfo
--화면 도움말 - 작업방법(버튼상세) 삭제
UPDATE USRYFA.TB_YF_HELP_BTNDTL B
SET
    B.DEL_YN    = 'Y'
WHERE 1=1
AND B.PAGE_ID   = :V_PAGE_ID
AND B.BTN_ID    = :V_BTN_ID
 </pre> */
public final static String delPageHelpBtnDtlInfo = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delPageHelpBtnDtlInfo";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarSchStat
UPDATE TB_YD_CARSCH
   SET MOD_DDTT = SYSDATE
      ,MODIFIER = :V_MODIFIER
      ,YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
      ,YD_EQP_WRK_STAT  = NVL(:V_YD_EQP_WRK_STAT,YD_EQP_WRK_STAT)
      ,YD_CARUD_ST_DT   = DECODE(:V_YD_CARUD_ST_DT,'SYSDATE',SYSDATE,YD_CARUD_ST_DT)
      ,YD_CARUD_CMPL_DT = DECODE(:V_YD_CARUD_CMPL_DT,'SYSDATE',SYSDATE,YD_CARUD_CMPL_DT)
      ,YD_CARLD_ST_DT   = DECODE(:V_YD_CARLD_ST_DT,'SYSDATE',SYSDATE,YD_CARLD_ST_DT)
      ,YD_CARLD_CMPL_DT = DECODE(:V_YD_CARLD_CMPL_DT,'SYSDATE',SYSDATE,YD_CARLD_CMPL_DT)
 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updateCarSchStat = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarSchStat";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarSchTime
UPDATE TB_YD_CARSCH
   SET MOD_DDTT = SYSDATE
      ,MODIFIER = :V_MODIFIER
      ,YD_CARUD_ST_DT   = DECODE(:V_YD_CARUD_ST_DT,'SYSDATE',SYSDATE,YD_CARUD_ST_DT)
      ,YD_CARUD_CMPL_DT = DECODE(:V_YD_CARUD_CMPL_DT,'SYSDATE',SYSDATE,YD_CARUD_CMPL_DT)
      ,YD_CARLD_ST_DT   = DECODE(:V_YD_CARLD_ST_DT,'SYSDATE',SYSDATE,YD_CARLD_ST_DT)
      ,YD_CARLD_CMPL_DT = DECODE(:V_YD_CARLD_CMPL_DT,'SYSDATE',SYSDATE,YD_CARLD_CMPL_DT)
 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updateCarSchTime = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarSchTime";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarSchStats
UPDATE TB_YD_CARSCH
   SET MOD_DDTT = SYSDATE
      ,MODIFIER = :V_MODIFIER
      ,YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
      ,YD_EQP_WRK_STAT  = NVL(:V_YD_EQP_WRK_STAT,YD_EQP_WRK_STAT)
 WHERE YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updateCarSchStats = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarSchStats";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStackcolCarPint
--적치열 차량포인트 점유 정보 비우기
UPDATE TB_YF_STKCOL
SET
    CAR_CARD_NO = NULL,
    CARD_NO     = NULL,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND CAR_CARD_NO =
(
    SELECT
        A.CAR_CARD_NO
    FROM
        USRYFA.TB_YF_STOCK A
    WHERE 1=1
    AND A.TRANS_ORD_DATE    = :V_TRANS_ORD_DATE
    AND A.TRANS_ORD_SEQNO   = :V_TRANS_ORD_SEQNO
    AND ROWNUM <= 1
)
 </pre> */
public final static String updYdStackcolCarPint = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStackcolCarPint";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock6
--운송지시번호 삭제,저장품 이동 조건 변경
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM     = DECODE(:V_STOCK_MOVE_TERM, '', STOCK_MOVE_TERM, :V_STOCK_MOVE_TERM),
    CAR_CARD_NO         = '',
    CAR_NO              = '',
    YD_RULE_PL_RS_GP    = '',
    TRANS_ORD_DATE      = '',
    TRANS_ORD_SEQNO     = '',
    MOD_DDTT            = SYSDATE,
    MODIFIER            = :V_MODIFIER
WHERE 1=1
AND TRANS_ORD_DATE      = :V_TRANS_ORD_DATE
AND TRANS_ORD_SEQNO     = :V_TRANS_ORD_SEQNO
 </pre> */
public final static String updYdStock6 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock6";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock7
--카드번호삭제,저장품 이동 조건 변경
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM,
    CAR_CARD_NO     = (CASE WHEN (SYSDATE - MOD_DDTT) > 0.001 THEN '' ELSE CAR_CARD_NO END),
    MOD_DDTT        = SYSDATE,
    MODIFIER        = :V_MODIFIER
WHERE 1=1
AND TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
 </pre> */
public final static String updYdStock7 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock7";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock2
--TB_YF_STOCK 보관지시구분 변경
UPDATE TB_YF_STOCK
SET
    KEEP_STL_YN = :V_KEEP_STL_YN,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND STL_NO      = :V_STL_NO
 </pre> */
public final static String updYdStock2 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock2";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updA7YML009WbTCar
-- 크레인권하실적 작업예약 대차 수정
UPDATE TB_YF_WRKBOOK
   SET MODIFIER         = :V_MODIFIER
	  ,MOD_DDTT         = SYSDATE
      ,YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
 WHERE YD_WBOOK_ID      = :V_YD_WBOOK_ID
 </pre> */
public final static String updA7YML009WbTCar = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updA7YML009WbTCar";

 /** <pre> 
--yf.common.dao.updateTermAndWBookIdOfStock
--저장품의 이동조건 UPDATE.
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM = DECODE(STOCK_MOVE_TERM, 'BD', STOCK_MOVE_TERM, ? )
WHERE 1=1
AND STL_NO = ?
 </pre> */
public final static String updateTermAndWBookIdOfStock = "bak.yf.common.dao.updateTermAndWBookIdOfStock";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insStockScrapInfo
MERGE INTO TB_YF_STOCK ST USING
(
    SELECT
        :V_STL_NO   AS STL_NO,      --재료번호
        :V_MODIFIER AS MODIFIER,    --수정자
        SYSDATE     AS MOD_DDTT,    --수정일시
        'N'         AS DEL_YN       --삭제유무
    FROM
        DUAL
) DD
ON (ST.STL_NO = DD.STL_NO)
WHEN NOT MATCHED THEN
    INSERT
    (
        STL_NO,
        STOCK_ITEM,
        STOCK_MOVE_TERM,  --저장품이동조건
        REGISTER,
        REG_DDTT,
        MODIFIER,
        MOD_DDTT,
        DEL_YN
    )
    VALUES
    (
        :V_STL_NO,
        'CM',
        'A2',
        'SYSTEM',
        SYSDATE,
        'SYSTEM',
        SYSDATE,
        'N'
    )
WHEN MATCHED THEN
    UPDATE
    SET
        ST.STOCK_ITEM       = 'CM',
        ST.STOCK_MOVE_TERM  = 'A2',
        ST.MODIFIER         = 'SYSTEM',
        ST.MOD_DDTT         = SYSDATE,
        ST.DEL_YN           = 'N'
 </pre> */
public final static String insStockScrapInfo = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insStockScrapInfo";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteAllWbookId
UPDATE TB_YF_WRKBOOK A
SET
    DEL_YN      = 'Y',
    MODIFIER    = ?,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND NOT EXISTS 
(
    SELECT 
        * 
    FROM 
        TB_YF_STOCK
    WHERE 1=1
    AND YD_WBOOK_ID = A.YD_WBOOK_ID
)
 </pre> */
public final static String deleteAllWbookId = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteAllWbookId";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updLyrByBedNo
UPDATE TB_YF_STKLYR
SET
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    STL_NO          = :V_STL_NO,
    YD_STK_LYR_STAT = :V_YD_STK_LYR_STAT2
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
AND YD_STK_LYR_STAT LIKE :V_YD_STK_LYR_STAT1 || '%'
 </pre> */
public final static String updLyrByBedNo = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updLyrByBedNo";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYdStkColStatByCar 
UPDATE USRYDA.TB_YD_STKCOL
SET YD_STK_COL_ACT_STAT=:V_YD_STK_COL_ACT_STAT
  , MODIFIER=:V_MODIFIER
  , MOD_DDTT=SYSDATE
WHERE YD_STK_COL_GP=(SELECT REPLACE(YD_STK_COL_GP,'TR1','TR0')
                       FROM TB_YD_CARPOINT
                     WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD
                     )
 </pre> */
public final static String updateYdStkColStatByCar = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYdStkColStatByCar";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCTSToLocByStlNo
UPDATE TB_YF_CTS_SCH
SET YD_CARUD_WO_LOC = :V_YD_CARUD_WO_LOC
   ,MODIFIER        = :V_MODIFIER
   ,MOD_DDTT        = SYSDATE
WHERE DEL_YN = 'N'
  AND STL_NO = :V_STL_NO
 </pre> */
public final static String updCTSToLocByStlNo = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCTSToLocByStlNo";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delPageHelpBtnInfo
UPDATE TB_YF_HELP_BTN
   SET DEL_YN = 'Y'
 WHERE PAGE_ID = :V_PAGE_ID
   AND BTN_ID = :V_BTN_ID
 </pre> */
public final static String delPageHelpBtnInfo = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delPageHelpBtnInfo";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCTSRelaySch
INSERT INTO TB_YF_CTS_SCH
(
 YD_CTS_SCH_ID
,REGISTER
,REG_DDTT
,MODIFIER
,MOD_DDTT
,DEL_YN
,YD_EQP_ID
,YD_CTS_WRK_SEQ
,STL_NO
,YD_WRK_PROG_STAT
,YD_WBOOK_ID
,YD_AIM_BAY_GP
,YD_CTS_RELAY_YN
,YD_CTS_RELAY_BAY_GP
,YD_CARLD_WO_LOC --야드상차지시위치
)
(
    SELECT
         :V_YD_CTS_SCH_ID
        ,:V_MODIFIER
        ,SYSDATE
        ,:V_MODIFIER
        ,SYSDATE
        ,'N'
        ,DECODE(A.YD_EQP_ID,'1XTC01','1XTC02','1XTC01') AS YD_EQP_ID
        ,NVL(C.DTL_ITEM3, B.DTL_ITEM3) AS YD_CTS_WRK_SEQ
        ,A.STL_NO
        ,'W'
        ,A.YD_WBOOK_ID
        ,A.YD_AIM_BAY_GP
        ,'X'
        ,'X'
        ,A.YD_CARUD_WR_LOC AS YD_CARLD_WO_LOC --야드상차지시위치
    FROM TB_YF_CTS_SCH A
        ,(SELECT * FROM TB_YF_RULE WHERE REPR_CD_GP IN ('PRI002','PRI003')) B --일반
        ,(SELECT * FROM TB_YF_RULE WHERE REPR_CD_GP IN ('PRI005','PRI006')) C --긴급
    WHERE A.DEL_YN          ='N'
      AND A.STL_NO          = :V_STL_NO
      AND A.YD_WRK_PROG_STAT= '4'
      
      AND B.DTL_ITEM1(+)    = A.YD_CTS_RELAY_BAY_GP
      AND B.DTL_ITEM2(+)    = A.YD_AIM_BAY_GP
      AND B.REPR_CD_GP(+)   = DECODE(DECODE(A.YD_EQP_ID,'1XTC01','1XTC02','1XTC01'), '1XTC01', 'PRI002', 'PRI003')
      
      AND C.DTL_ITEM1(+)    = A.YD_CTS_RELAY_BAY_GP
      AND C.DTL_ITEM2(+)    = A.YD_AIM_BAY_GP
      AND C.REPR_CD_GP(+)   = DECODE(DECODE(A.YD_EQP_ID,'1XTC01','1XTC02','1XTC01'), '1XTC01', 'PRI005', 'PRI006')
)
 </pre> */
public final static String insCTSRelaySch = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCTSRelaySch";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonLocInfo
UPDATE TB_PT_MSLABCOMM
        SET(  
            YD_GP,              -- 야드구분
            YD_BAY_GP,                -- 동
            YD_EQP_GP,               -- SPAN
            YD_STK_COL_NO,                -- 적치열번지
            YD_STK_BED_NO,             -- 적치번지
            YD_STK_LYR_NO,        -- 적치단
            YD_STR_LOC,       -- 현 저장위치코드
            YD_STR_LOC_HIS1,      -- 전 저장위치코드
            YD_STR_LOC_HIS2   -- 전전 저장위치코드
           )=
           (SELECT D.YD_GP AS YD_GP
                  ,D.BAY_GP AS BAY_GP
                  ,D.YD_EQP_GP AS YD_EQP_GP
                  ,D.YD_STK_COL_NO AS YD_STK_COL_NO
                  ,D.YD_STK_BED_NO AS YD_STK_BED_NO
                  ,'0' || D.YD_STK_LYR_NO AS YD_STK_LYR_NO
                  ,D.YD_GP || D.BAY_GP || D.YD_EQP_GP || D.YD_STK_COL_NO || D.YD_STK_BED_NO || D.YD_STK_LYR_NO AS YD_STR_LOC
                  ,C.YD_STR_LOC AS YD_STR_LOC_HIS1    -- 전 저장위치코드
                  ,C.YD_STR_LOC_HIS1 AS YD_STR_LOC_HIS2 -- 전전 저장위치코드
            FROM TB_PT_MSLABCOMM C
			    ,(SELECT  :V_YD_GP            AS YD_GP
                         ,:V_BAY_GP           AS BAY_GP
                         ,:V_YD_EQP_GP        AS YD_EQP_GP
                         ,:V_YD_STK_COL_NO    AS YD_STK_COL_NO
                         ,:V_YD_STK_BED_NO    AS YD_STK_BED_NO
                         ,:V_YD_STK_LYR_NO    AS YD_STK_LYR_NO
                    FROM DUAL) D
            WHERE mslab_no = :V_SLAB_NO
           )
        WHERE mslab_no = :V_SLAB_NO
 </pre> */
public final static String updateMslabCommonLocInfo = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonLocInfo";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.updateSlabCommonLocInfo
UPDATE TB_PT_SLABCOMM
   SET(  
            YD_GP,              -- 야드구분
            YD_BAY_GP,                -- 동
            YD_EQP_GP,               -- SPAN
            YD_STK_COL_NO,                -- 적치열번지
            YD_STK_BED_NO,             -- 적치번지
            YD_STK_LYR_NO,        -- 적치단
            YD_STR_LOC,       -- 현 저장위치코드
            YD_STR_LOC_HIS1,      -- 전 저장위치코드
            YD_STR_LOC_HIS2   -- 전전 저장위치코드
           )=(SELECT D.YD_GP AS YD_GP
                    ,D.BAY_GP AS BAY_GP
                    ,D.YD_EQP_GP AS YD_EQP_GP
                    ,D.YD_STK_COL_NO AS YD_STK_COL_NO
                    ,D.YD_STK_BED_NO AS YD_STK_BED_NO
                    ,'0' || D.YD_STK_LYR_NO AS YD_STK_LYR_NO
                    ,D.YD_GP || D.BAY_GP || D.YD_EQP_GP || D.YD_STK_COL_NO || D.YD_STK_BED_NO || D.YD_STK_LYR_NO AS YD_STR_LOC
                    ,C.YD_STR_LOC AS YD_STR_LOC_HIS1    -- 전 저장위치코드
                    ,C.YD_STR_LOC_HIS1 AS YD_STR_LOC_HIS2 -- 전전 저장위치코드
            FROM TB_PT_SLABCOMM C
                ,(SELECT  :V_YD_GP            AS YD_GP
                         ,:V_BAY_GP           AS BAY_GP
                         ,:V_YD_EQP_GP        AS YD_EQP_GP
                         ,:V_YD_STK_COL_NO    AS YD_STK_COL_NO
                         ,:V_YD_STK_BED_NO    AS YD_STK_BED_NO
                         ,:V_YD_STK_LYR_NO    AS YD_STK_LYR_NO
                    FROM DUAL) D
            WHERE C.SLAB_NO = :V_SLAB_NO
           )
        WHERE SLAB_NO = :V_SLAB_NO
 </pre> */
public final static String updateSlabCommonLocInfo = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.updateSlabCommonLocInfo";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.updateSlabCommonSubInfo
UPDATE TB_PT_SLABCOMMSUB
 SET BK_STK_YN='Y' 
WHERE SLAB_NO =:V_SLAB_NO
 </pre> */
public final static String updateSlabCommonSubInfo = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.updateSlabCommonSubInfo";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockMarkingStat
UPDATE TB_YF_STOCK
   SET MKNG_GP = :V_MKNG_GP
      ,MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,MKNG_MODIFIER = :V_MODIFIER
      ,MKNG_DDTT = SYSDATE
 WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String updStockMarkingStat = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockMarkingStat";

 /** <pre> 
--yf.common.YfCommonDao.MergeWrkBookMtl 
MERGE 
 INTO TB_YF_WRKBOOKMTL A
USING (
SELECT        :V_MODIFIER                  AS MODIFIER                 
             ,sysdate                      AS MOD_DDTT                 
             ,NVL(:V_DEL_YN,'N')           AS DEL_YN                   
             ,:V_YD_WBOOK_ID	           AS YD_WBOOK_ID
			 ,:V_STL_NO                    AS STL_NO               
			 ,:V_YD_STK_COL_GP             AS YD_STK_COL_GP           
			 ,:V_YD_STK_BED_NO             AS YD_STK_BED_NO           
			 ,:V_YD_STK_LYR_NO             AS YD_STK_LYR_NO        
			 ,:V_YD_UP_COLL_SEQ            AS YD_UP_COLL_SEQ
             ,:V_YD_ISPTOR                 AS YD_ISPTOR
			 ,:V_YD_TAKE_OUT_DT            AS YD_TAKE_OUT_DT        
			 ,:V_YD_TAKE_OUT_CD            AS YD_TAKE_OUT_CD       
			 ,:V_MTL_YD_TO_LOC_GUIDE       AS MTL_YD_TO_LOC_GUIDE        
			 ,:V_MTL_YD_WRK_PLAN_CRN       AS MTL_YD_WRK_PLAN_CRN         
		 FROM DUAL
      ) B
	ON (    A.YD_WBOOK_ID   LIKE B.YD_WBOOK_ID
        AND A.STL_NO        LIKE B.STL_NO)
  WHEN MATCHED THEN
	         UPDATE 
				SET A.MODIFIER          	= DECODE(NVL(B.MODIFIER         	   ,A.MODIFIER         	    ),'-','',B.MODIFIER         	)
				   ,A.MOD_DDTT         		= DECODE(NVL(B.MOD_DDTT         	   ,A.MOD_DDTT         	    ),'-','',B.MOD_DDTT         	)
				   ,A.DEL_YN            	= DECODE(NVL(B.DEL_YN           	   ,A.DEL_YN           	    ),'-','',B.DEL_YN           	)
				   ,A.YD_STK_COL_GP         = DECODE(NVL(B.YD_STK_COL_GP           ,A.YD_STK_COL_GP         ),'-','',B.YD_STK_COL_GP        )
                   ,A.YD_STK_BED_NO         = DECODE(NVL(B.YD_STK_BED_NO           ,A.YD_STK_BED_NO         ),'-','',B.YD_STK_BED_NO        )
                   ,A.YD_STK_LYR_NO         = DECODE(NVL(B.YD_STK_LYR_NO           ,A.YD_STK_LYR_NO         ),'-','',B.YD_STK_LYR_NO        )
                   ,A.YD_UP_COLL_SEQ        = DECODE(NVL(B.YD_UP_COLL_SEQ          ,A.YD_UP_COLL_SEQ        ),'-','',B.YD_UP_COLL_SEQ       )
                   ,A.YD_ISPTOR             = DECODE(NVL(B.YD_ISPTOR               ,A.YD_ISPTOR             ),'-','',B.YD_ISPTOR            )
                   ,A.YD_TAKE_OUT_DT        = DECODE(NVL(B.YD_TAKE_OUT_DT          ,A.YD_TAKE_OUT_DT        ),'-','',B.YD_TAKE_OUT_DT       )
                   ,A.YD_TAKE_OUT_CD        = DECODE(NVL(B.YD_TAKE_OUT_CD          ,A.YD_TAKE_OUT_CD        ),'-','',B.YD_TAKE_OUT_CD       )
                   ,A.MTL_YD_TO_LOC_GUIDE   = DECODE(NVL(B.MTL_YD_TO_LOC_GUIDE     ,A.MTL_YD_TO_LOC_GUIDE   ),'-','',B.MTL_YD_TO_LOC_GUIDE  )
                   ,A.MTL_YD_WRK_PLAN_CRN   = DECODE(NVL(B.MTL_YD_WRK_PLAN_CRN     ,A.MTL_YD_WRK_PLAN_CRN   ),'-','',B.MTL_YD_WRK_PLAN_CRN  )
WHEN NOT MATCHED THEN
			INSERT ( YD_WBOOK_ID
                    ,STL_NO
                    ,REGISTER
                    ,REG_DDTT
                    ,MODIFIER
                    ,MOD_DDTT
                    ,DEL_YN
                    ,YD_STK_COL_GP
                    ,YD_STK_BED_NO
                    ,YD_STK_LYR_NO
                    ,YD_UP_COLL_SEQ
                    ,YD_ISPTOR
                    ,YD_TAKE_OUT_DT
                    ,YD_TAKE_OUT_CD
                    ,MTL_YD_TO_LOC_GUIDE
                    ,MTL_YD_WRK_PLAN_CRN )
			VALUES ( B.YD_WBOOK_ID
					,B.STL_NO
					,B.MODIFIER
					,B.MOD_DDTT
					,B.MODIFIER
					,B.MOD_DDTT
					,'N'
					,B.YD_STK_COL_GP
                    ,B.YD_STK_BED_NO
                    ,B.YD_STK_LYR_NO
                    ,B.YD_UP_COLL_SEQ
                    ,B.YD_ISPTOR
                    ,B.YD_TAKE_OUT_DT
                    ,B.YD_TAKE_OUT_CD
                    ,B.MTL_YD_TO_LOC_GUIDE
                    ,B.MTL_YD_WRK_PLAN_CRN )
 </pre> */
public final static String MergeWrkBookMtl = "bak.yf.common.YfCommonDao.MergeWrkBookMtl";

 /** <pre> 
--yf.facilitywork.putwrecord.session.CarSchDelYnToY
UPDATE TB_YD_CARSCH
   SET DEL_YN = 'Y'
 WHERE TRN_EQP_CD = 
(
    SELECT
        TRN_EQP_CD
    FROM
        TB_YD_CARPOINT
    WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
)
     AND DEL_YN = 'N'
 </pre> */
public final static String CarSchDelYnToY = "bak.yf.facilitywork.putwrecord.session.CarSchDelYnToY";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.insCrScrLyrByLoc 
UPDATE TB_YF_STKLYR
   SET STL_NO         = SUBSTR(YD_STK_COL_GP, 3, 4)||YD_STK_BED_NO||YD_STK_LYR_NO
     , YD_STK_LYR_STAT = 'C'
     , MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
 WHERE YD_STK_COL_GP LIKE '1ESC%'
   AND YD_STK_COL_GP||YD_STK_BED_NO||YD_STK_LYR_NO IN (SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STL_NO
			                                            FROM (SELECT :V_ARR_STL_NO AS SSTL_NOS FROM DUAL)
			                                          CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL)
   AND YD_STK_LYR_STAT = 'E'
 </pre> */
public final static String insCrScrLyrByLoc = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insCrScrLyrByLoc";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYmCrnLoc
MERGE INTO TB_YF_CRNLOC CRN USING (
SELECT :V_YD_EQP_ID         AS YD_EQP_ID
     , :V_CRN_WRK_PROC_STAT AS CRN_WRK_PROC_STAT
     , :V_CURR_XAXIS        AS CURR_XAXIS
     , :V_FROM_XAXIS        AS FROM_XAXIS
     , :V_TO_XAXIS          AS TO_XAXIS
     , :V_MODIFIER          AS MODIFIER
     , SYSDATE              AS MOD_DDTT
  FROM DUAL  
) DD ON (CRN.YD_EQP_ID = DD.YD_EQP_ID)
WHEN NOT MATCHED THEN
INSERT (CRN.YD_GP               , CRN.YD_BAY_GP             , CRN.YD_EQP_ID , 
        CRN.CRN_WRK_PROC_STAT   , CRN.CURR_XAXIS            , CRN.FROM_XAXIS,
        CRN.TO_XAXIS            , CRN.REGISTER              , CRN.REG_DDTT  ,  
        CRN.MODIFIER            , CRN.MOD_DDTT)
VALUES (SUBSTR(DD.YD_EQP_ID,1,1), SUBSTR(DD.YD_EQP_ID,2,1)  , DD.YD_EQP_ID  ,
        DD.CRN_WRK_PROC_STAT    , DD.CURR_XAXIS             , DD.FROM_XAXIS , 
        DD.TO_XAXIS             , DD.MODIFIER               , DD.MOD_DDTT   ,
        DD.MODIFIER             , DD.MOD_DDTT)
WHEN MATCHED THEN UPDATE SET
     CRN.MODIFIER           = DD.MODIFIER
    ,CRN.MOD_DDTT           = DD.MOD_DDTT
    ,CRN.CRN_WRK_PROC_STAT  = DD.CRN_WRK_PROC_STAT
    ,CRN.CURR_XAXIS         = DD.CURR_XAXIS
    ,CRN.FROM_XAXIS         = DD.FROM_XAXIS
    ,CRN.TO_XAXIS           = DD.TO_XAXIS
 </pre> */
public final static String updYmCrnLoc = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYmCrnLoc";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateLowerlayerStat
update TB_YF_STKLYR
set YD_STK_LYR_ACTIVE_STAT = NVL(:V_YD_STK_LYR_ACTIVE_STAT,YD_STK_LYR_ACTIVE_STAT) ,
    YD_STK_LYR_STAT = NVL(:V_YD_STK_LYR_STAT,YD_STK_LYR_STAT)
where YD_STK_COL_GP = :V_YD_STK_COL_GP
  and YD_STK_BED_NO = :V_YD_STK_BED_NO
  and YD_STK_LYR_NO < :V_YD_STK_LYR_NO 
 </pre> */
public final static String updateLowerlayerStat = "bak.yf.facilitywork.putwrecord.session.updateLowerlayerStat";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updYdCarpointByYdStkColGp
UPDATE TB_YD_CARPOINT
SET
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE,
    YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT,
    TRN_EQP_CD          = :V_TRN_EQP_CD
WHERE 1=1
AND YD_STK_COL_GP       = :V_YD_STK_COL_GP
 </pre> */
public final static String updYdCarpointByYdStkColGp = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updYdCarpointByYdStkColGp";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updClearScrapStockByArrStockId                     
UPDATE TB_YF_STOCK
   SET DEL_YN   = 'Y'
     , MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
 WHERE STL_NO IN (SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STL_NO
			          FROM (SELECT :V_ARR_STL_NO AS SSTL_NOS FROM DUAL)
			       CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL)
 </pre> */
public final static String updClearScrapStockByArrStockId = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updClearScrapStockByArrStockId";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insA7YML011WbTCarIns
INSERT INTO TB_YF_WRKBOOK WB
       (WB.YD_WBOOK_ID         , WB.REGISTER              , WB.REG_DDTT              ,
        WB.DEL_YN              , WB.YD_GP                 , WB.YD_BAY_GP             , WB.YD_SCH_CD   , WB.YD_SCH_PRIOR ,
        WB.YD_SCH_PROG_STAT    , WB.YD_SCH_ST_GP          , WB.YD_SCH_REQ_GP         , WB.YD_AIM_YD_GP, WB.YD_AIM_BAY_GP,
        WB.YD_TO_LOC_DCSN_MTD  , WB.YD_TO_LOC_GUIDE       , WB.YD_WRK_PLAN_TCAR)
VALUES (:V_YD_CARUD_WRK_BOOK_ID, :V_MODIFIER              ,  SYSDATE                 ,
        'N'                    , SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1) , :V_YD_SCH_CD   , :V_YD_WRK_CRN_PRIOR ,
        'W'                    , 'O'                      , '1'                      ,SUBSTR(:V_YD_SCH_CD,1,1) , SUBSTR(:V_YD_SCH_CD,2,1)    ,
        NULL                   , NULL                     , :V_YD_EQP_ID)
 </pre> */
public final static String insA7YML011WbTCarIns = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insA7YML011WbTCarIns";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updEqpOprnStat
UPDATE TB_YF_EQP 
SET YD_EQP_PROG_STAT= :V_YD_EQP_PROG_STAT
  , MODIFIER = :V_MODIFIER
  , MOD_DDTT = SYSDATE
WHERE YD_EQP_ID=  :V_YD_EQP_ID
 </pre> */
public final static String updEqpOprnStat = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updEqpOprnStat";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateStockYN
UPDATE TB_YF_STOCK
   SET DEL_YN = :V_DEL_YN
 WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String updateStockYN = "bak.yf.facilitywork.putwrecord.session.updateStockYN";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelCarSch 
UPDATE USRYDA.TB_YD_CARSCH
   SET MODIFIER    = :V_MODIFIER
      ,MOD_DDTT    = SYSDATE
      ,DEL_YN      = 'Y'
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
   AND DEL_YN        = 'N' 
 </pre> */
public final static String updDelCarSch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelCarSch";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteCarMtlInfo2
UPDATE TB_YD_CARFTMVMTL 
   SET DEL_YN = 'Y'
		WHERE YD_CAR_SCH_ID IN
        (
	        SELECT YD_CAR_SCH_ID
	        FROM TB_YF_STKCOL A, 
	             TB_YD_CARSCH B
	        WHERE A.YD_STK_COL_GP = :V_YD_STK_COL_GP
	          AND A.TRN_EQP_CD = B.TRN_EQP_CD
	          AND B.DEL_YN = 'N'
        )
		AND STL_NO = :V_STL_NO
 </pre> */
public final static String deleteCarMtlInfo2 = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteCarMtlInfo2";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStkbedYfStrActStat
UPDATE TB_YF_STKBED
   SET YD_STK_BED_ACTIVE_STAT = :V_YD_STK_BED_ACTIVE_STAT
       , MOD_DDTT             = SYSDATE
       , MODIFIER             = :V_MODIFIER  
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String updYfStkbedYfStrActStat = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStkbedYfStrActStat";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStklyrColActStat
UPDATE TB_YF_STKLYR            
   SET YD_STK_LYR_ACTIVE_STAT = DECODE(YD_STK_LYR_ACTIVE_STAT,'N',YD_STK_LYR_ACTIVE_STAT,REPLACE(:V_YD_STK_LYR_ACTIVE_STAT,'L','E'))
       , MOD_DDTT             = SYSDATE
       , MODIFIER             = :V_MODIFIER  
WHERE YD_STK_COL_GP  = :V_YD_STK_COL_GP
 </pre> */
public final static String updYfStklyrColActStat = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStklyrColActStat";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStkbedYfStrX
UPDATE  TB_YF_STKBED
   SET  YD_STK_BED_X_AXIS = :V_YD_STK_BED_X_AXIS
      , YD_STK_BED_Y_AXIS = :V_YD_STK_BED_Y_AXIS
      , YD_STK_BED_Z_AXIS = :V_YD_STK_BED_Z_AXIS
      , MOD_DDTT          = SYSDATE
      , MODIFIER          = :V_MODIFIER  
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String updYfStkbedYfStrX = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStkbedYfStrX";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCTSSchUpModify
--CTS스케줄 상차 실적 
UPDATE TB_YF_CTS_SCH 
SET YD_WRK_PROG_STAT= :V_YD_WRK_PROG_STAT
  , MODIFIER        = :V_MODIFIER
  , MOD_DDTT        = SYSDATE
  , YD_CARLD_WR_LOC = :V_YD_CARLD_WR_LOC
  , YD_CARLD_WR_DT  = SYSDATE
WHERE YD_EQP_ID = :V_YD_EQP_ID
  AND DEL_YN    = 'N'
  AND STL_NO    = :V_STL_NO
 </pre> */
public final static String updCTSSchUpModify = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCTSSchUpModify";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCoilYdStkPosSetBed.updYdStklyrDan2
UPDATE TB_YF_STKLYR
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , YD_STK_LYR_ACTIVE_STAT = :V_YD_STK_LYR_ACTIVE_STAT
     , YD_STK_LYR_X_AXIS = :V_YD_STK_LYR_X_AXIS
     , YD_STK_LYR_Y_AXIS = :V_YD_STK_LYR_Y_AXIS
     , YD_STK_LYR_Z_AXIS =  NVL(:V_YD_STK_LYR_Z_AXIS,YD_STK_LYR_Z_AXIS)
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
 </pre> */
public final static String updYdStklyrDan2 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCoilYdStkPosSetBed.updYdStklyrDan2";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.deleteYdCarftmvmtl
UPDATE USRYDA.TB_YD_CARFTMVMTL
SET
    DEL_YN          = 'Y'
WHERE 1=1
AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String deleteYdCarftmvmtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.deleteYdCarftmvmtl";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.deleteYdExaminationmtl
UPDATE USRYDA.TB_YD_EXAMINATIONCHKLIST
SET
    DEL_YN          = 'Y'
WHERE 1=1
AND TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String deleteYdExaminationmtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.deleteYdExaminationmtl";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCarFrm2
UPDATE TB_YF_WRKBOOK
   SET  --YD_CTS_RELAY_YN = :V_YD_CTS_RELAY_YN --항목 사용이 잘못 된 듯.. KBS
        CAR_FRM_GP = :V_CAR_FRM_GP
      , MODIFIER = :V_MODIFIER
	  , MOD_DDTT = SYSDATE
 WHERE YD_WBOOK_ID IN(SELECT WB.YD_WBOOK_ID
                        FROM TB_YF_WRKBOOK     WB
				           , TB_YF_WRKBOOKMTL  WM
                           , (SELECT TRN_EQP_CD, CAR_NO, CARD_NO
                              FROM USRYDA.TB_YD_CARPOINT
                             WHERE YD_CARPNT_CD LIKE '1%'
                               AND YD_STK_COL_GP = :V_YD_STK_COL_GP
                           ) CP
                     WHERE WB.YD_WBOOK_ID = WM.YD_WBOOK_ID
                       AND WB.DEL_YN = 'N'
                       AND WM.DEL_YN = 'N'
                       AND ((WB.YD_CAR_USE_GP = 'L' AND WB.TRN_EQP_CD = CP.TRN_EQP_CD)
                         OR (WB.YD_CAR_USE_GP = 'G' AND WB.CAR_NO = CP.CAR_NO  AND WB.CARD_NO    = CP.CARD_NO))
                     )
 </pre> */
public final static String updCarFrm2 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCarFrm2";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarProgStatCarldWrkbook
UPDATE TB_YF_TCARSCH
   SET MODIFIER             = :V_MODIFIER
     , MOD_DDTT             = SYSDATE
     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT --야드차량진행상태
     , YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
 </pre> */
public final static String updTcarProgStatCarldWrkbook = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarProgStatCarldWrkbook";

 /** <pre> 
-- com.inisteel.cim.yf.aslab.dao.ASlabDAO.updateMatlFtmvWlrstSlabNEW  
UPDATE TB_PT_SLABCOMM
   SET FNL_REG_PGM = 'yfCurrProgcdS'
      ,CURR_PROG_CD_REG_PGM = 'yfCurrProgcdS'
      ,CURR_PROG_REG_DDTT = SYSDATE
      ,CURR_PROG_CD = :V_CURR_PROG_CD
      ,BEFO_PROG_CD_REG_PGM = CURR_PROG_CD_REG_PGM
      ,BEFO_PROG_REG_DDTT = CURR_PROG_REG_DDTT
      ,BEFO_PROG_CD = CURR_PROG_CD
      ,BEFOBEFO_PROG_CD_REG_PGM = BEFO_PROG_CD_REG_PGM
      ,BEFOBEFO_PROG_REG_DDTT = BEFO_PROG_REG_DDTT
      ,BEFOBEFO_PROG_CD = BEFO_PROG_CD
      ,MODIFIER = 'SYSTEM'
      ,MOD_DDTT = SYSDATE
      ,MATL_TKOV_DT = SYSDATE
 WHERE SLAB_NO = :V_SLAB_NO
 </pre> */
public final static String updateMatlFtmvWlrstSlabNEW = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updateMatlFtmvWlrstSlabNEW";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStkLyrTCarStl
MERGE INTO TB_YF_STKLYR SL USING (
SELECT SL.YD_STK_COL_GP
      ,SL.YD_STK_BED_NO
      ,SL.YD_STK_LYR_NO
      ,'E'                            AS YD_STK_LYR_ACTIVE_STAT --적치가능
      ,DECODE(TM.STL_NO,NULL,'E','C') AS YD_STK_LYR_STAT --적치가능,적치중
      ,TM.STL_NO
  FROM TB_YF_STKLYR      SL
      ,TB_YF_TCARFTMVMTL TM
 WHERE SL.YD_STK_COL_GP    = :V_YD_STK_COL_GP
   AND SL.YD_STK_BED_NO    = TM.YD_STK_BED_NO(+) 
   AND :V_YD_TCAR_SCH_ID   = TM.YD_TCAR_SCH_ID(+)
   AND 'N'                 = TM.DEL_YN(+)
) DD ON (SL.YD_STK_COL_GP  = DD.YD_STK_COL_GP AND SL.YD_STK_BED_NO   = DD.YD_STK_BED_NO
                                              AND SL.YD_STK_LYR_NO = DD.YD_STK_LYR_NO)
WHEN MATCHED THEN UPDATE SET
     SL.MODIFIER                = :V_MODIFIER
    ,SL.MOD_DDTT                = SYSDATE
    ,SL.YD_STK_LYR_ACTIVE_STAT  = DD.YD_STK_LYR_ACTIVE_STAT
    ,SL.YD_STK_LYR_STAT         = DD.YD_STK_LYR_STAT
    ,SL.STL_NO                  = DD.STL_NO
 </pre> */
public final static String updStkLyrTCarStl = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStkLyrTCarStl";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.delSchLocSrchZone
DELETE TB_YF_SCHLOCSRCH_ZONE
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String delSchLocSrchZone = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.delSchLocSrchZone";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchAutoStYn2 
UPDATE TB_YF_SCHRULE
   SET YD_SCH_AUTO_ST_YN = :V_YD_SCH_AUTO_ST_YN
     , MODIFIER          = :V_MODIFIER
     , MOD_DDTT          = SYSDATE
 WHERE YD_SCH_CD         IN ( 
            SELECT 
              A.YD_SCH_CD
            FROM
            (
             SELECT DISTINCT SUBSTR (AA, INSTR (AA, ',', 1, LEVEL) + 1, INSTR (AA, ',', 1, LEVEL + 1) - INSTR (AA, ',', 1, LEVEL) - 1) AS YD_SCH_CD
             FROM (SELECT ',' || :V_YD_SCH_CD || ',' AA FROM DUAL)
             CONNECT BY LEVEL <= LENGTH (AA) - LENGTH (REPLACE (AA, ',')) - 1  
            ) A
            --
               마지막 1개만 스케쥴 취소로 되어 있을 경우 자동기동 y로 설정되게 함 
               확인사항 
                 
            
            WHERE (
                     SELECT COUNT(S.YD_WBOOK_ID)
                     FROM TB_YF_WRKBOOK S
                     WHERE S.YD_SCH_CD = A.YD_SCH_CD
                     AND S.SCH_CNCL_YN = 'Y'
                     AND S.DEL_YN = 'N'
                     AND NOT EXISTS (
                        SELECT 1
                        FROM TB_YF_CRNSCH SS
                        WHERE SS.YD_WBOOK_ID = S.YD_WBOOK_ID
                        AND SS.DEL_YN = 'N'
                     )
                  ) = 0
 )
 </pre> */
public final static String updSchAutoStYn2 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchAutoStYn2";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdStkLyrClr2
UPDATE TB_YF_STKLYR
   SET MODIFIER                = :V_MODIFIER
      ,MOD_DDTT                = SYSDATE
      ,STL_NO                = NULL
      ,YD_STK_LYR_ACTIVE_STAT = 'E'
      ,YD_STK_LYR_STAT        = 'E'
 WHERE YD_STK_COL_GP  LIKE  '1_TC'||SUBSTR(:V_YD_STK_COL_GP,5,2)
   AND DEL_YN                  = 'N'
 </pre> */
public final static String updYdStkLyrClr2 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdStkLyrClr2";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdEqpCurrBay 
UPDATE TB_YF_EQP
   SET MODIFIER       = :V_MODIFIER
      ,MOD_DDTT       = SYSDATE
      ,YD_CURR_BAY_GP  = SUBSTR(:V_YD_STK_COL_GP,2,1)
 WHERE YD_EQP_ID       = :V_YD_EQP_ID
 </pre> */
public final static String updYdEqpCurrBay = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdEqpCurrBay";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updYdCarSchYdPntCd3
--하차위치변경(영차-하차)
UPDATE TB_YD_CARSCH
SET
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE,
    YD_PNT_CD3          = :V_YD_PNT_CD,
    YD_CARUD_STOP_LOC   = :V_TO_LOC
WHERE 1=1
AND YD_CAR_SCH_ID =
(
    SELECT
        MAX(YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
    FROM
        USRYDA.TB_YD_CARSCH
    WHERE 1=1
    AND YD_CARUD_STOP_LOC   = :V_FROM_LOC
    AND DEL_YN              = 'N'
    AND TRN_EQP_CD          = :V_TRN_EQP_CD
)
 </pre> */
public final static String updYdCarSchYdPntCd3 = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updYdCarSchYdPntCd3";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.insertCarMtlInfo
INSERT INTO TB_YD_CARFTMVMTL
		(
		 YD_CAR_SCH_ID,STL_NO,REGISTER,REG_DDTT,MODIFIER,MOD_DDTT,DEL_YN,
		 YD_STK_BED_NO,YD_STK_LYR_NO
		)
		(
		    SELECT YD_CAR_SCH_ID,
		            :V_STL_NO,
		            'YJK',SYSDATE,'YJK',SYSDATE,'N',
		            :V_YD_STK_BED_NO,
		            '0'||:V_YD_STK_LYR_NO
		    FROM TB_YF_STKCOL A,
		         TB_YD_CARSCH B
		    WHERE A.YD_STK_COL_GP = :V_YD_STK_COL_GP
		      AND A.TRN_EQP_CD = B.TRN_EQP_CD
		      AND B.DEL_YN = 'N'
		) 
 </pre> */
public final static String insertCarMtlInfo = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.insertCarMtlInfo";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdStkLyrInActiveTC
UPDATE TB_YF_STKLYR
   SET MODIFIER                = :V_MODIFIER
      ,MOD_DDTT                = SYSDATE
      ,STL_NO                = NULL
      ,YD_STK_LYR_ACTIVE_STAT = 'C'
      ,YD_STK_LYR_STAT        = 'E'
 WHERE YD_STK_COL_GP            = :V_YD_STK_COL_GP
   AND SUBSTR(YD_STK_COL_GP,1,1)= '1'
   AND SUBSTR(YD_STK_COL_GP,3,2)= 'TC'
   AND DEL_YN                  = 'N'
 </pre> */
public final static String updYdStkLyrInActiveTC = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdStkLyrInActiveTC";

 /** <pre> 
-- com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockRule 
UPDATE  TB_YF_RULE
SET  DTL_ITEM1  = :V_DTL_ITEM1
    ,DTL_ITEM2  = :V_DTL_ITEM2
    ,DTL_ITEM3  = :V_DTL_ITEM3
    ,DEL_YN    = :V_DEL_YN
    ,MODIFIER = :V_MODIFIER
    ,MOD_DDTT = sysdate
 WHERE 1=1
  AND REPR_CD_GP ='SR0001' 
  AND CD_GP      = :V_CD_GP
  AND ITEM       = :V_ITEM
 </pre> */
public final static String updStockRule = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockRule";

 /** <pre> 
-- com.inisteel.cim.yf.aslab.dao.ASlabDAO.updSlabYdStkPosSetBed.updYdStklyrDan 
UPDATE TB_YF_STKLYR
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , YD_STK_LYR_X_AXIS = :V_YD_STK_LYR_X_AXIS
     , YD_STK_LYR_Y_AXIS = :V_YD_STK_LYR_Y_AXIS
     , YD_STK_LYR_Z_AXIS =  NVL(:V_YD_STK_LYR_Z_AXIS,YD_STK_LYR_Z_AXIS)
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
 </pre> */
public final static String updYdStklyrDan = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updSlabYdStkPosSetBed.updYdStklyrDan";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockMoveEquipInfo 
UPDATE TB_YF_STOCK
   SET CTS_RELAY_SADDLE 	   = :V_CTS_RELAY_SADDLE
     , CTS_RELAY_YN		   = :V_CTS_RELAY_YN
     , MODIFIER   = :V_MODIFIER   
     , MOD_DDTT   = SYSDATE     
 WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String updateStockMoveEquipInfo = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateStockMoveEquipInfo";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updYdStkLyrActiveStat 
-- 적치가능/적치불가 
UPDATE TB_YF_STKLYR
   SET YD_STK_LYR_ACTIVE_STAT = :V_YD_STK_LYR_ACTIVE_STAT
     , YD_STK_LYR_YD_STK_LOT_NO2 = :V_YD_STK_LYR_YD_STK_LOT_NO2
     , MODIFIER                = :V_MODIFIER
     , MOD_DDTT                = SYSDATE
 WHERE DEL_YN = 'N'
   AND YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO = :V_YD_STK_BED_N
   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
 </pre> */
public final static String updYdStkLyrActiveStat = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updYdStkLyrActiveStat";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStklyrX
UPDATE TB_YF_STKLYR            
   SET YD_STK_LYR_X_AXIS = :V_YD_STK_LYR_X_AXIS
     , YD_STK_LYR_Y_AXIS = DECODE(NVL(YD_STK_LYR_Y_AXIS,0),0,:V_YD_STK_LYR_Y_AXIS,YD_STK_LYR_Y_AXIS)
     , YD_STK_LYR_Z_AXIS = :V_YD_STK_LYR_Z_AXIS
     , MOD_DDTT          = SYSDATE
     , MODIFIER          = :V_MODIFIER  
 WHERE YD_STK_COL_GP  = :V_YD_STK_COL_GP
 </pre> */
public final static String updYfStklyrX = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilYdNEqpXyaxisSet.updYfStklyrX";

 /** <pre> 
--yf.common.YfCommonDao.MergeWrkHist  
MERGE INTO TB_YF_WRKHIST A
	 USING ( 
		SELECT	 :V_YD_WRK_HIST_ID             AS YD_WRK_HIST_ID
				,:V_MODIFIER                   AS MODIFIER
				,SYSDATE	                   AS MOD_DDTT
				,NVL(:V_DEL_YN,'N')            AS DEL_YN
				,:V_YD_GP                      AS YD_GP
				,:V_STL_NO                     AS STL_NO
				,SF_YD_WRK_HDS_DD(NULL)        AS YD_WRK_HDS_DD
				,SF_YD_WRK_DUTY(NULL)          AS YD_WRK_DUTY
				,SF_YD_WRK_PARTY(NULL)         AS YD_WRK_PARTY
                ,TO_CHAR(SYSDATE,'YYYYMMDD')   AS FRTOMOVE_ORD_DATE
                ,SYSDATE                       AS YD_WBOOK_DT
                ,:V_YD_SCH_PRIOR               AS YD_SCH_PRIOR
				,:V_YD_TO_LOC_DCSN_MTD         AS YD_TO_LOC_DCSN_MTD
				,:V_YD_UP_WRK_MODE2            AS YD_UP_WRK_MODE2
				,:V_YD_DN_WRK_MODE2            AS YD_DN_WRK_MODE2
				,:V_YD_EQP_ID                  AS YD_EQP_ID
				,:V_YD_SCH_CD                  AS YD_SCH_CD
				,:V_YD_UP_CMPL_DT              AS YD_UP_CMPL_DT
				,:V_YD_UP_WR_FUNC              AS YD_UP_WR_FUNC
				,:V_YD_UP_WO_LOC               AS YD_UP_WO_LOC
				,:V_YD_UP_WR_LOC               AS YD_UP_WR_LOC
				,:V_YD_UP_WO_LYR               AS YD_UP_WO_LYR
				,:V_YD_UP_WR_LYR               AS YD_UP_WR_LYR
				,:V_YD_DN_CMPL_DT              AS YD_DN_CMPL_DT
				,:V_YD_DN_WR_FUNC              AS YD_DN_WR_FUNC
				,:V_YD_DN_WO_LOC               AS YD_DN_WO_LOC
				,:V_YD_DN_WR_LOC               AS YD_DN_WR_LOC
				,:V_YD_DN_WO_LYR               AS YD_DN_WO_LYR
				,:V_YD_DN_WR_LYR               AS YD_DN_WR_LYR
				,:V_SCARFING_YN                AS SCARFING_YN
				,:V_SCARFING_DONE_YN           AS SCARFING_DONE_YN
				,:V_ORD_NO                     AS ORD_NO
				,:V_ORD_DTL                    AS ORD_DTL
				,:V_ORD_GP                     AS ORD_GP
				,:V_STL_PROG_CD                AS STL_PROG_CD
				,:V_CUST_CD                    AS CUST_CD
				,:V_DEST_CD                    AS DEST_CD
				,:V_STL_APPEAR_GP              AS STL_APPEAR_GP
				,:V_ITEMNAME_CD                AS ITEMNAME_CD
				,:V_ORD_YEOJAE_GP              AS ORD_YEOJAE_GP
				,:V_SPEC_ABBSYM                AS SPEC_ABBSYM
				,:V_DEMANDER_CD                AS DEMANDER_CD
				,:V_SLAB_WO_RT_CD              AS SLAB_WO_RT_CD
				,:V_ORD_HCR_GP                 AS ORD_HCR_GP
				,:V_HCR_GP                     AS HCR_GP
				,:V_YD_MTL_T                   AS YD_MTL_T
				,:V_YD_MTL_W                   AS YD_MTL_W
				,:V_YD_MTL_L                   AS YD_MTL_L
				,:V_YD_MTL_WT                  AS YD_MTL_WT
				,:V_SPOS_WLOC_CD               AS SPOS_WLOC_CD
				,:V_ARR_WLOC_CD                AS ARR_WLOC_CD
				,:V_TRN_EQP_CD				   AS TRN_EQP_CD
				,:V_CAR_NO                     AS CAR_NO
				,:V_CAR_KIND                   AS CAR_KIND
				,:V_CARD_NO                    AS CARD_NO
				,:V_YD_CAR_SCH_ID              AS YD_CAR_SCH_ID
			FROM DUAL
) B
ON ( 	A.YD_WRK_HIST_ID = B.YD_WRK_HIST_ID
    AND A.STL_NO = B.STL_NO )
WHEN MATCHED THEN
		   UPDATE
		      SET A.MODIFIER                	= DECODE(NVL(B.MODIFIER                 ,A.MODIFIER               ),'-','',B.MODIFIER                 )    
                 ,A.MOD_DDTT                	= DECODE(NVL(B.MOD_DDTT                 ,A.MOD_DDTT               ),'SYSDATE',SYSDATE,'-','',B.MOD_DDTT                 )    
                 ,A.DEL_YN                  	= DECODE(NVL(B.DEL_YN                   ,A.DEL_YN                 ),'-','',B.DEL_YN                   )    
                 ,A.YD_GP                   	= DECODE(NVL(B.YD_GP                    ,A.YD_GP                  ),'-','',B.YD_GP                    )   
                 ,A.YD_WRK_HDS_DD           	= DECODE(NVL(B.YD_WRK_HDS_DD            ,A.YD_WRK_HDS_DD          ),'-','',B.YD_WRK_HDS_DD            )    
                 ,A.YD_WRK_DUTY             	= DECODE(NVL(B.YD_WRK_DUTY              ,A.YD_WRK_DUTY            ),'-','',B.YD_WRK_DUTY              )    
				 ,A.YD_WRK_PARTY             	= DECODE(NVL(B.YD_WRK_PARTY             ,A.YD_WRK_PARTY           ),'-','',B.YD_WRK_PARTY              )    
                 ,A.FRTOMOVE_ORD_DATE       	= DECODE(NVL(B.FRTOMOVE_ORD_DATE        ,A.FRTOMOVE_ORD_DATE      ),'-','',B.FRTOMOVE_ORD_DATE        )
                 ,A.YD_WBOOK_DT             	= DECODE(NVL(B.YD_WBOOK_DT              ,A.YD_WBOOK_DT            ),'-','',B.YD_WBOOK_DT              ) 
				 ,A.YD_SCH_PRIOR			  	= DECODE(NVL(B.YD_SCH_PRIOR             ,A.YD_SCH_PRIOR           ),'-','',B.YD_SCH_PRIOR             )
				 ,A.YD_TO_LOC_DCSN_MTD	  		= DECODE(NVL(B.YD_TO_LOC_DCSN_MTD       ,A.YD_TO_LOC_DCSN_MTD     ),'-','',B.YD_TO_LOC_DCSN_MTD       )
				 ,A.YD_UP_WRK_MODE2		  		= DECODE(NVL(B.YD_UP_WRK_MODE2          ,A.YD_UP_WRK_MODE2        ),'-','',B.YD_UP_WRK_MODE2          )
				 ,A.YD_DN_WRK_MODE2		  		= DECODE(NVL(B.YD_DN_WRK_MODE2          ,A.YD_DN_WRK_MODE2        ),'-','',B.YD_DN_WRK_MODE2          )
				 ,A.YD_EQP_ID			      	= DECODE(NVL(B.YD_EQP_ID                ,A.YD_EQP_ID              ),'-','',B.YD_EQP_ID                )
				 ,A.YD_SCH_CD			      	= DECODE(NVL(B.YD_SCH_CD                ,A.YD_SCH_CD              ),'-','',B.YD_SCH_CD                )
				 ,A.YD_UP_CMPL_DT			  	= DECODE(NVL(B.YD_UP_CMPL_DT            ,A.YD_UP_CMPL_DT          ),'SYSDATE',SYSDATE,'-','',B.YD_UP_CMPL_DT            )
				 ,A.YD_UP_WR_FUNC			  	= DECODE(NVL(B.YD_UP_WR_FUNC            ,A.YD_UP_WR_FUNC          ),'-','',B.YD_UP_WR_FUNC            )
				 ,A.YD_UP_WO_LOC			  	= DECODE(NVL(B.YD_UP_WO_LOC             ,A.YD_UP_WO_LOC           ),'-','',B.YD_UP_WO_LOC             )
				 ,A.YD_UP_WR_LOC			  	= DECODE(NVL(B.YD_UP_WR_LOC             ,A.YD_UP_WR_LOC           ),'-','',B.YD_UP_WR_LOC             )
				 ,A.YD_UP_WO_LYR			  	= DECODE(NVL(B.YD_UP_WO_LYR             ,A.YD_UP_WO_LYR           ),'-','',B.YD_UP_WO_LYR             )
				 ,A.YD_UP_WR_LYR			  	= DECODE(NVL(B.YD_UP_WR_LYR             ,A.YD_UP_WR_LYR           ),'-','',B.YD_UP_WR_LYR             )
				 ,A.YD_DN_CMPL_DT			  	= DECODE(NVL(B.YD_DN_CMPL_DT            ,A.YD_DN_CMPL_DT          ),'SYSDATE',SYSDATE,'-','',B.YD_DN_CMPL_DT            )
				 ,A.YD_DN_WR_FUNC			  	= DECODE(NVL(B.YD_DN_WR_FUNC            ,A.YD_DN_WR_FUNC          ),'-','',B.YD_DN_WR_FUNC            )
				 ,A.YD_DN_WO_LOC			  	= DECODE(NVL(B.YD_DN_WO_LOC             ,A.YD_DN_WO_LOC           ),'-','',B.YD_DN_WO_LOC             )
				 ,A.YD_DN_WR_LOC			  	= DECODE(NVL(B.YD_DN_WR_LOC             ,A.YD_DN_WR_LOC           ),'-','',B.YD_DN_WR_LOC             )
				 ,A.YD_DN_WO_LYR			  	= DECODE(NVL(B.YD_DN_WO_LYR             ,A.YD_DN_WO_LYR           ),'-','',B.YD_DN_WO_LYR             )
				 ,A.YD_DN_WR_LYR			  	= DECODE(NVL(B.YD_DN_WR_LYR             ,A.YD_DN_WR_LYR           ),'-','',B.YD_DN_WR_LYR             )
				 ,A.SCARFING_YN			  		= DECODE(NVL(B.SCARFING_YN              ,A.SCARFING_YN            ),'-','',B.SCARFING_YN              )
				 ,A.SCARFING_DONE_YN		  	= DECODE(NVL(B.SCARFING_DONE_YN         ,A.SCARFING_DONE_YN       ),'-','',B.SCARFING_DONE_YN         )
				 ,A.ORD_NO			      		= DECODE(NVL(B.ORD_NO                   ,A.ORD_NO                 ),'-','',B.ORD_NO                   )
				 ,A.ORD_DTL			      		= DECODE(NVL(B.ORD_DTL                  ,A.ORD_DTL                ),'-','',B.ORD_DTL                  )
				 ,A.ORD_GP			      		= DECODE(NVL(B.ORD_GP                   ,A.ORD_GP                 ),'-','',B.ORD_GP                   )
				 ,A.STL_PROG_CD			      	= DECODE(NVL(B.STL_PROG_CD              ,A.STL_PROG_CD            ),'-','',B.STL_PROG_CD              )
				 ,A.CUST_CD			      		= DECODE(NVL(B.CUST_CD                  ,A.CUST_CD                ),'-','',B.CUST_CD                  )
				 ,A.DEST_CD			      		= DECODE(NVL(B.DEST_CD                  ,A.DEST_CD                ),'-','',B.DEST_CD                  )
				 ,A.STL_APPEAR_GP			  	= DECODE(NVL(B.STL_APPEAR_GP            ,A.STL_APPEAR_GP          ),'-','',B.STL_APPEAR_GP            )
				 ,A.ITEMNAME_CD			  		= DECODE(NVL(B.ITEMNAME_CD              ,A.ITEMNAME_CD            ),'-','',B.ITEMNAME_CD              )
				 ,A.ORD_YEOJAE_GP			  	= DECODE(NVL(B.ORD_YEOJAE_GP            ,A.ORD_YEOJAE_GP          ),'-','',B.ORD_YEOJAE_GP            )
				 ,A.SPEC_ABBSYM			  		= DECODE(NVL(B.SPEC_ABBSYM              ,A.SPEC_ABBSYM            ),'-','',B.SPEC_ABBSYM              )
				 ,A.DEMANDER_CD			  		= DECODE(NVL(B.DEMANDER_CD              ,A.DEMANDER_CD            ),'-','',B.DEMANDER_CD              )
				 ,A.SLAB_WO_RT_CD			  	= DECODE(NVL(B.SLAB_WO_RT_CD            ,A.SLAB_WO_RT_CD          ),'-','',B.SLAB_WO_RT_CD            )
				 ,A.ORD_HCR_GP			  		= DECODE(NVL(B.ORD_HCR_GP               ,A.ORD_HCR_GP             ),'-','',B.ORD_HCR_GP               )
				 ,A.HCR_GP			      		= DECODE(NVL(B.HCR_GP                   ,A.HCR_GP                 ),'-','',B.HCR_GP                   )
				 ,A.YD_MTL_T			      	= DECODE(NVL(B.YD_MTL_T                 ,A.YD_MTL_T               ),'-','',B.YD_MTL_T                 )
				 ,A.YD_MTL_W			      	= DECODE(NVL(B.YD_MTL_W                 ,A.YD_MTL_W               ),'-','',B.YD_MTL_W                 )
				 ,A.YD_MTL_L			      	= DECODE(NVL(B.YD_MTL_L                 ,A.YD_MTL_L               ),'-','',B.YD_MTL_L                 )
				 ,A.YD_MTL_WT			      	= DECODE(NVL(B.YD_MTL_WT                ,A.YD_MTL_WT              ),'-','',B.YD_MTL_WT                )
				 ,A.SPOS_WLOC_CD			  	= DECODE(NVL(B.SPOS_WLOC_CD             ,A.SPOS_WLOC_CD           ),'-','',B.SPOS_WLOC_CD             )
				 ,A.ARR_WLOC_CD			  		= DECODE(NVL(B.ARR_WLOC_CD              ,A.ARR_WLOC_CD            ),'-','',B.ARR_WLOC_CD              )
				 ,A.TRN_EQP_CD			      	= DECODE(NVL(B.TRN_EQP_CD               ,A.TRN_EQP_CD             ),'-','',B.TRN_EQP_CD               )
				 ,A.CAR_NO			      		= DECODE(NVL(B.CAR_NO                   ,A.CAR_NO                 ),'-','',B.CAR_NO                   )
				 ,A.CAR_KIND			     	= DECODE(NVL(B.CAR_KIND                 ,A.CAR_KIND               ),'-','',B.CAR_KIND                 )
				 ,A.CARD_NO			      		= DECODE(NVL(B.CARD_NO                  ,A.CARD_NO                ),'-','',B.CARD_NO                  )
				 ,A.YD_CAR_SCH_ID			  	= DECODE(NVL(B.YD_CAR_SCH_ID            ,A.YD_CAR_SCH_ID          ),'-','',B.YD_CAR_SCH_ID            )	 
WHEN NOT MATCHED THEN                                                                                                              
			   INSERT ( YD_WRK_HIST_ID
                       ,REGISTER
					   ,REG_DDTT
					   ,MODIFIER
					   ,MOD_DDTT
					   ,DEL_YN
					   ,YD_GP
					   ,STL_NO
					   ,YD_WRK_HDS_DD
					   ,YD_WRK_DUTY
					   ,YD_WRK_PARTY
					   ,FRTOMOVE_ORD_DATE
					   ,YD_WBOOK_DT
					   ,YD_SCH_PRIOR
					   ,YD_TO_LOC_DCSN_MTD
					   ,YD_UP_WRK_MODE2
					   ,YD_DN_WRK_MODE2
					   ,YD_EQP_ID
					   ,YD_SCH_CD
					   ,YD_UP_CMPL_DT
					   ,YD_UP_WR_FUNC
					   ,YD_UP_WO_LOC
					   ,YD_UP_WR_LOC
					   ,YD_UP_WO_LYR
					   ,YD_UP_WR_LYR
					   ,YD_DN_CMPL_DT
					   ,YD_DN_WR_FUNC
					   ,YD_DN_WO_LOC
					   ,YD_DN_WR_LOC
					   ,YD_DN_WO_LYR
					   ,YD_DN_WR_LYR
					   ,SCARFING_YN
					   ,SCARFING_DONE_YN
					   ,ORD_NO
					   ,ORD_DTL
					   ,ORD_GP
					   ,STL_PROG_CD
					   ,CUST_CD
					   ,DEST_CD
					   ,STL_APPEAR_GP
					   ,ITEMNAME_CD
					   ,ORD_YEOJAE_GP
					   ,SPEC_ABBSYM
					   ,DEMANDER_CD
					   ,SLAB_WO_RT_CD
					   ,ORD_HCR_GP
					   ,HCR_GP
					   ,YD_MTL_T
					   ,YD_MTL_W
					   ,YD_MTL_L
					   ,YD_MTL_WT
					   ,SPOS_WLOC_CD
					   ,ARR_WLOC_CD
					   ,TRN_EQP_CD
					   ,CAR_NO
					   ,CAR_KIND
					   ,CARD_NO
					   ,YD_CAR_SCH_ID
						)		
				VALUES( TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YF_WRKHIST_SEQ.NEXTVAL
                       ,B.MODIFIER
					   ,DECODE(B.MOD_DDTT,'SYSDATE',SYSDATE,B.MOD_DDTT)
					   ,B.MODIFIER
					   ,DECODE(B.MOD_DDTT,'SYSDATE',SYSDATE,B.MOD_DDTT)
					   ,'N'
					   ,B.YD_GP
					   ,B.STL_NO
					   ,B.YD_WRK_HDS_DD
					   ,B.YD_WRK_DUTY
					   ,B.YD_WRK_PARTY
					   ,B.FRTOMOVE_ORD_DATE
					   ,B.YD_WBOOK_DT
					   ,B.YD_SCH_PRIOR
					   ,B.YD_TO_LOC_DCSN_MTD
					   ,B.YD_UP_WRK_MODE2
					   ,B.YD_DN_WRK_MODE2
					   ,B.YD_EQP_ID
					   ,B.YD_SCH_CD
					   ,DECODE(B.YD_UP_CMPL_DT,'SYSDATE',SYSDATE,B.YD_UP_CMPL_DT)
					   ,B.YD_UP_WR_FUNC
					   ,B.YD_UP_WO_LOC
					   ,B.YD_UP_WR_LOC
					   ,B.YD_UP_WO_LYR
					   ,B.YD_UP_WR_LYR
					   ,DECODE(B.YD_DN_CMPL_DT,'SYSDATE',SYSDATE,B.YD_DN_CMPL_DT)
					   ,B.YD_DN_WR_FUNC
					   ,B.YD_DN_WO_LOC
					   ,B.YD_DN_WR_LOC
					   ,B.YD_DN_WO_LYR
					   ,B.YD_DN_WR_LYR
					   ,B.SCARFING_YN
					   ,B.SCARFING_DONE_YN
					   ,B.ORD_NO
					   ,B.ORD_DTL
					   ,B.ORD_GP
					   ,B.STL_PROG_CD
					   ,B.CUST_CD
					   ,B.DEST_CD
					   ,B.STL_APPEAR_GP
					   ,B.ITEMNAME_CD
					   ,B.ORD_YEOJAE_GP
					   ,B.SPEC_ABBSYM
					   ,B.DEMANDER_CD
					   ,B.SLAB_WO_RT_CD
					   ,B.ORD_HCR_GP
					   ,B.HCR_GP
					   ,B.YD_MTL_T
					   ,B.YD_MTL_W
					   ,B.YD_MTL_L
					   ,B.YD_MTL_WT
					   ,B.SPOS_WLOC_CD
					   ,B.ARR_WLOC_CD
					   ,B.TRN_EQP_CD
					   ,B.CAR_NO
					   ,B.CAR_KIND
					   ,B.CARD_NO
					   ,B.YD_CAR_SCH_ID)
 </pre> */
public final static String MergeWrkHist = "bak.yf.common.YfCommonDao.MergeWrkHist";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelTcarSch 
UPDATE TB_YF_TCARSCH
   SET MODIFIER    = :V_MODIFIER
      ,MOD_DDTT    = SYSDATE
      ,DEL_YN      = 'Y'
 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
   AND DEL_YN         = 'N' 
 </pre> */
public final static String updDelTcarSch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelTcarSch";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelCarFtMvMtl 
UPDATE USRYDA.TB_YD_CARFTMVMTL
   SET MODIFIER    = :V_MODIFIER
      ,MOD_DDTT    = SYSDATE
      ,DEL_YN      = 'Y'
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
   AND STL_NO        = :V_STL_NO
   AND DEL_YN        = 'N'
 </pre> */
public final static String updDelCarFtMvMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelCarFtMvMtl";

 /** <pre> 
 -- com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarMapLayer 
UPDATE TB_YF_STKLYR    
   SET STL_NO         = :V_STL_NO
     , YD_STK_LYR_STAT = 'C'
     , MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
 </pre> */
public final static String updateCarMapLayer = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarMapLayer";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStockTransInfo_06
UPDATE TB_YF_STOCK
SET 
    MODIFIER            = 'SYSTEM',
    MOD_DDTT            = SYSDATE,
    STOCK_ITEM          = (CASE WHEN KEEP_STL_YN = 'Y' THEN STOCK_ITEM ELSE :V_STOCK_ITEM END),
    STOCK_MOVE_TERM     = :V_STOCK_MOVE_TERM
WHERE 1=1
AND STL_NO              = :V_STL_NO
 </pre> */
public final static String updStockTransInfo_06 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStockTransInfo_06";

 /** <pre> 
--yf.common.YfCommonDao.MergeCarSch
MERGE 
 INTO TB_YD_CARSCH A
 USING ( SELECT 
                :V_YD_CAR_SCH_ID        AS YD_CAR_SCH_ID
               ,'N'                     AS DEL_YN
               ,:V_YD_EQP_ID    		AS YD_EQP_ID
               ,:V_YD_CAR_USE_GP    	AS YD_CAR_USE_GP
               ,:V_TRN_EQP_CD    		AS TRN_EQP_CD
               ,:V_YD_EQP_WRK_STAT    	AS YD_EQP_WRK_STAT
               ,:V_YD_EQP_WRK_SH    	AS YD_EQP_WRK_SH
               ,:V_YD_EQP_WRK_WT    	AS YD_EQP_WRK_WT
               ,:V_SPOS_WLOC_CD    		AS SPOS_WLOC_CD
               ,:V_ARR_WLOC_CD    		AS ARR_WLOC_CD
               ,:V_YD_PNT_CD1    		AS YD_PNT_CD1
               ,:V_YD_PNT_CD3    		AS YD_PNT_CD3
               ,:V_YD_CAR_PROG_STAT    	AS YD_CAR_PROG_STAT
               ,:V_YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
               ,:V_YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
               ,:V_YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
               ,:V_YD_CARLD_LEV_DT      AS YD_CARLD_LEV_DT
               ,:V_YD_CARUD_LEV_DT      AS YD_CARUD_LEV_DT
               ,:V_YD_CARLD_ARR_DT      AS YD_CARLD_ARR_DT
               ,:V_YD_CARUD_ARR_DT      AS YD_CARUD_ARR_DT
               ,:V_REGISTER             AS REGISTER
           FROM DUAL ) B
  
  ON (A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID)
  
  WHEN MATCHED THEN 
       UPDATE SET A.MODIFIER                = B.REGISTER
                 ,A.MOD_DDTT                = SYSDATE
                 ,A.DEL_YN 					= B.DEL_YN
                 ,A.YD_EQP_ID 				= B.YD_EQP_ID
                 ,A.YD_CAR_USE_GP 			= B.YD_CAR_USE_GP
                 ,A.TRN_EQP_CD 				= B.TRN_EQP_CD
                 ,A.YD_EQP_WRK_STAT 		= B.YD_EQP_WRK_STAT
                 ,A.YD_EQP_WRK_SH 			= B.YD_EQP_WRK_SH
                 ,A.YD_EQP_WRK_WT 			= B.YD_EQP_WRK_WT
                 ,A.SPOS_WLOC_CD 			= B.SPOS_WLOC_CD
                 ,A.ARR_WLOC_CD 			= B.ARR_WLOC_CD
                 ,A.YD_PNT_CD1 				= B.YD_PNT_CD1
                 ,A.YD_PNT_CD3 				= B.YD_PNT_CD3
                 ,A.YD_CAR_PROG_STAT 		= B.YD_CAR_PROG_STAT
                 ,A.YD_CARLD_WRK_BOOK_ID 	= B.YD_CARLD_WRK_BOOK_ID
                 ,A.YD_CARLD_STOP_LOC 		= B.YD_CARLD_STOP_LOC
                 ,A.YD_CARUD_STOP_LOC 		= B.YD_CARUD_STOP_LOC
                 ,A.YD_CARLD_LEV_DT 		= DECODE(B.YD_CARLD_LEV_DT,'SYSDATE',SYSDATE,NULL)
                 ,A.YD_CARUD_LEV_DT 		= DECODE(B.YD_CARUD_LEV_DT,'SYSDATE',SYSDATE,NULL)
                 ,A.YD_CARLD_ARR_DT 		= DECODE(B.YD_CARLD_ARR_DT,'SYSDATE',SYSDATE,NULL)
                 ,A.YD_CARUD_ARR_DT 		= DECODE(B.YD_CARUD_ARR_DT,'SYSDATE',SYSDATE,NULL)
  WHEN NOT MATCHED THEN
           INSERT ( YD_CAR_SCH_ID
				   ,DEL_YN 				
                   ,YD_EQP_ID 			
                   ,YD_CAR_USE_GP 		
                   ,TRN_EQP_CD 			
                   ,YD_EQP_WRK_STAT 	
                   ,YD_EQP_WRK_SH 		
                   ,YD_EQP_WRK_WT 		
                   ,SPOS_WLOC_CD 		
                   ,ARR_WLOC_CD 		
                   ,YD_PNT_CD1 			
                   ,YD_PNT_CD3 			
                   ,YD_CAR_PROG_STAT 	
                   ,YD_CARLD_WRK_BOOK_ID
                   ,YD_CARLD_STOP_LOC 	
                   ,YD_CARUD_STOP_LOC
                   ,YD_CARLD_LEV_DT
                   ,YD_CARUD_LEV_DT
                   ,YD_CARLD_ARR_DT
                   ,YD_CARUD_ARR_DT
                   ,MODIFIER
                   ,MOD_DDTT
                   )
		VALUES( B.YD_CAR_SCH_ID
			   ,B.DEL_YN
               ,B.YD_EQP_ID
               ,B.YD_CAR_USE_GP
               ,B.TRN_EQP_CD
               ,B.YD_EQP_WRK_STAT
               ,B.YD_EQP_WRK_SH
               ,B.YD_EQP_WRK_WT
               ,B.SPOS_WLOC_CD
               ,B.ARR_WLOC_CD
               ,B.YD_PNT_CD1
               ,B.YD_PNT_CD3
               ,B.YD_CAR_PROG_STAT
               ,B.YD_CARLD_WRK_BOOK_ID
               ,B.YD_CARLD_STOP_LOC
               ,B.YD_CARUD_STOP_LOC
               ,DECODE(B.YD_CARLD_LEV_DT,'SYSDATE',SYSDATE,NULL)
               ,DECODE(B.YD_CARUD_LEV_DT,'SYSDATE',SYSDATE,NULL)
               ,DECODE(B.YD_CARLD_ARR_DT,'SYSDATE',SYSDATE,NULL)
               ,DECODE(B.YD_CARUD_ARR_DT,'SYSDATE',SYSDATE,NULL)
               ,B.REGISTER
               ,SYSDATE
               )
 </pre> */
public final static String MergeCarSch = "bak.yf.common.YfCommonDao.MergeCarSch";

 /** <pre> 
--yf.facilitywork.putwrecord.session.CarPointReset
UPDATE TB_YD_CARPOINT
   SET TRN_EQP_CD = ''
      ,CAR_NO = ''
      ,CARD_NO = ''
      ,YD_STK_COL_ACT_STAT = 'C'
WHERE DEL_YN = 'N'
  AND YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String CarPointReset = "bak.yf.facilitywork.putwrecord.session.CarPointReset";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insEqpTracking
MERGE INTO TB_YF_EQPTRACKING A USING (
    SELECT
        :V_YD_GP          AS YD_GP
        ,:V_PROC_GP        AS PROC_GP
        ,:V_YD_EQP_ID      AS YD_EQP_ID
        ,:V_MODIFIER       AS REGISTER
        ,SYSDATE           AS REG_DDTT
        ,:V_MODIFIER       AS MODIFIER
        ,SYSDATE           AS MOD_DDTT
        ,'N'               AS DEL_YN
        ,:V_STL_NO         AS STL_NO
        ,:V_SORT_SEQ       AS SORT_SEQ
        ,:V_YD_EQP_NM      AS YD_EQP_NM
        ,:V_LOC_NO         AS LOC_NO
        ,:V_SKID_NO        AS SKID_NO
        ,:V_YD_STK_COL_GP  AS YD_STK_COL_GP
        ,:V_YD_STK_BED_NO  AS YD_STK_BED_NO
        ,:V_YD_BAY_GP      AS YD_BAY_GP
    FROM DUAL
) B
ON (
    A.YD_GP     = B.YD_GP
    AND A.PROC_GP   = B.PROC_GP
    AND A.YD_EQP_ID = B.YD_EQP_ID
)
WHEN MATCHED THEN
    UPDATE SET
        A.MODIFIER                = B.MODIFIER
        , A.MOD_DDTT              = B.MOD_DDTT
        , A.DEL_YN                = B.DEL_YN
        , A.STL_NO                = B.STL_NO
        , A.SORT_SEQ              = B.SORT_SEQ
        , A.YD_EQP_NM             = NVL(B.YD_EQP_NM,A.YD_EQP_NM)
        , A.LOC_NO                = NVL(B.LOC_NO, A.LOC_NO)
        , A.SKID_NO               = NVL(B.SKID_NO, A.SKID_NO)
        , A.YD_STK_COL_GP         = NVL(B.YD_STK_COL_GP, A.YD_STK_COL_GP)
        , A.YD_STK_BED_NO         = NVL(B.YD_STK_BED_NO, A.YD_STK_BED_NO)
        , A.YD_BAY_GP             = NVL(B.YD_BAY_GP, A.YD_BAY_GP)
WHEN NOT MATCHED THEN
INSERT(
    A.YD_GP, A.PROC_GP, A.YD_EQP_ID, A.REGISTER, A.REG_DDTT, A.MODIFIER, A.MOD_DDTT, A.DEL_YN
    , A.STL_NO, A.SORT_SEQ, A.YD_EQP_NM, A.LOC_NO, A.SKID_NO, A.YD_STK_COL_GP, A.YD_STK_BED_NO, A.YD_BAY_GP)
VALUES(
    B.YD_GP, B.PROC_GP, B.YD_EQP_ID, B.REGISTER, B.REG_DDTT, B.MODIFIER, B.MOD_DDTT, B.DEL_YN
    , B.STL_NO, B.SORT_SEQ, B.YD_EQP_NM, B.LOC_NO, B.SKID_NO, B.YD_STK_COL_GP, B.YD_STK_BED_NO, B.YD_BAY_GP)
 </pre> */
public final static String insEqpTracking = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insEqpTracking";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML009CarSchUd
--크레인권하실적 하차 차량스케줄 수정
UPDATE USRYDA.TB_YD_CARSCH TS
SET
    TS.MODIFIER             = :V_MODIFIER,
    TS.MOD_DDTT             = SYSDATE,
    TS.YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT,
    TS.YD_EQP_WRK_SH        = (
                                SELECT
                                    COUNT(*)
                                FROM
                                    TB_YD_CARFTMVMTL
                                WHERE 1=1
                                AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
                              ),
    TS.YD_EQP_WRK_WT        = (
                                SELECT
                                    SUM(COIL_WT)
                                FROM
                                    TB_YD_CARFTMVMTL A,
                                    USRPTA.TB_PT_COILCOMM   B
                                WHERE 1=1
                                AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
                                AND A.STL_NO        = B.COIL_NO
                              ),
    TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID,
    TS.YD_CARUD_STOP_LOC    = :V_YD_STK_COL_GP,
    TS.YD_CARUD_ST_DT       = NVL(TS.YD_CARUD_ST_DT, NVL(TO_DATE(:V_WR_DT, 'YYYYMMDDHH24MISS'), SYSDATE)),
    TS.YD_CARUD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT, 'E', NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'), SYSDATE), NULL)
WHERE 1=1
AND TS.YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updAxYML009CarSchUd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML009CarSchUd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarProgStat
UPDATE TB_YF_TCARSCH
   SET MODIFIER          = :V_MODIFIER
     , MOD_DDTT          = SYSDATE
     , YD_CAR_PROG_STAT  = :V_YD_CAR_PROG_STAT --야드차량진행상태
     , YD_CARLD_LEV_LOC  = DECODE(:V_YD_CAR_PROG_STAT,'1',:V_YD_STK_COL_GP ,YD_CARLD_LEV_LOC ) --야드상차출발위치
     , YD_CARLD_LEV_DT   = DECODE(:V_YD_CAR_PROG_STAT,'1',SYSDATE          ,YD_CARLD_LEV_DT  ) --야드상차출발일시
     , YD_CARLD_STOP_LOC = DECODE(:V_YD_CAR_PROG_STAT,'2',:V_YD_STK_COL_GP ,'0',:V_YD_STK_COL_GP,YD_CARLD_STOP_LOC) --야드상차정지위치
     , YD_CARLD_ARR_DT   = DECODE(:V_YD_CAR_PROG_STAT,'2',SYSDATE          ,'0',SYSDATE         ,YD_CARLD_ARR_DT  ) --야드상차도착일시
     , YD_CARUD_LEV_DT   = DECODE(:V_YD_CAR_PROG_STAT,'A',SYSDATE          ,YD_CARUD_LEV_DT  ) --야드하차출발일시
     , YD_CARUD_STOP_LOC = DECODE(:V_YD_CAR_PROG_STAT,'B',:V_YD_STK_COL_GP ,YD_CARUD_STOP_LOC) --야드하차정지위치
     , YD_CARUD_ARR_DT   = DECODE(:V_YD_CAR_PROG_STAT,'B',SYSDATE          ,YD_CARUD_ARR_DT  ) --야드하차도착일시
 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
 </pre> */
public final static String updTcarProgStat = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarProgStat";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.AcoilDAO.updYdCarftmvmtl 

UPDATE TB_YD_CARFTMVMTL
      SET MODIFIER = :V_MODIFIER
         ,MOD_DDTT = SYSDATE 
         ,YD_STK_BED_NO = :V_YD_CAR_UPP_LOC_CD
  WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    AND STL_NO = :V_STL_NO
 </pre> */
public final static String updYdCarftmvmtl = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updYdCarftmvmtl";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarLayer 
update TB_YF_STKLYR
set STL_NO = ''
    ,YD_STK_LYR_STAT = 'E' 
    ,MODIFIER = :V_MODIFIER
    ,MOD_DDTT = SYSDATE
where STL_NO = :V_STL_NO
 </pre> */
public final static String updateCarLayer = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarLayer";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCarFtMvMtl 
MERGE INTO TB_YD_CARFTMVMTL TM USING (
    SELECT STK.STL_NO
          ,COIL.HCR_GP
          ,COIL.RECORD_PROG_STAT AS STL_PROG_CD
          ,NVL(:V_YD_STK_BED_NO,LAY.YD_STK_BED_NO) AS YD_STK_BED_NO
          ,NVL(:V_YD_STK_LYR_NO,LAY.YD_STK_LYR_NO) AS YD_STK_LYR_NO
          ,:V_YD_CAR_SCH_ID AS YD_CAR_SCH_ID
          ,:V_MODIFIER AS MODIFIER
          ,SYSDATE AS MOD_DDTT
          ,'N' AS DEL_YN
    FROM   TB_YF_STOCK STK
          --권상대기 적치중
          ,(SELECT  STL_NO
                   ,YD_STK_BED_NO
                   ,YD_STK_LYR_NO 
              FROM  TB_YF_STKLYR
              WHERE STL_NO = :V_STL_NO
                AND YD_STK_LYR_STAT IN ('C','U') 
           ) LAY
          ,TB_PT_COILCOMM   COIL    
    WHERE  STK.STL_NO = :V_STL_NO
      AND  STK.STL_NO = COIL.COIL_NO 
      AND  STK.STL_NO = LAY.STL_NO(+)
 
) DD ON (TM.YD_CAR_SCH_ID = DD.YD_CAR_SCH_ID AND TM.STL_NO = DD.STL_NO )    
WHEN NOT MATCHED THEN
INSERT (TM.YD_CAR_SCH_ID, TM.STL_NO, TM.REGISTER, TM.REG_DDTT,
        TM.MODIFIER, TM.MOD_DDTT, TM.DEL_YN, TM.YD_STK_BED_NO,
        TM.YD_STK_LYR_NO, TM.HCR_GP, TM.STL_PROG_CD)
VALUES (DD.YD_CAR_SCH_ID, DD.STL_NO, DD.MODIFIER, DD.MOD_DDTT,
        DD.MODIFIER, DD.MOD_DDTT, DD.DEL_YN, DD.YD_STK_BED_NO,
        DD.YD_STK_LYR_NO, DD.HCR_GP, DD.STL_PROG_CD)
WHEN MATCHED THEN
UPDATE SET
    TM.MODIFIER = DD.MODIFIER
   ,TM.MOD_DDTT = DD.MOD_DDTT
   ,TM.DEL_YN = DD.DEL_YN
   ,TM.YD_STK_BED_NO = DD.YD_STK_BED_NO
   ,TM.YD_STK_LYR_NO = DD.YD_STK_LYR_NO
   ,TM.HCR_GP = DD.HCR_GP
   ,TM.STL_PROG_CD = DD.STL_PROG_CD
 </pre> */
public final static String updCarFtMvMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCarFtMvMtl";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateBayGpWithWbookId 
UPDATE TB_YF_WRKBOOK
   SET YD_BAY_GP   = :V_YD_BAY_GP
     , MODIFIER    = :V_MODIFIER
     , MOD_DDTT    = SYSDATE     
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID 
 </pre> */
public final static String updateBayGpWithWbookId = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateBayGpWithWbookId";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStockTransInfoSNBK 
MERGE INTO TB_YF_STOCK ST USING (
    SELECT :V_STL_NO            AS STL_NO            --재료번호
         , :V_MODIFIER          AS MODIFIER          --수정자
         , SYSDATE              AS MOD_DDTT          --수정일시
         , 'N'                  AS DEL_YN            --삭제유무
         , :V_STOCK_ITEM        AS STOCK_ITEM        --저장품 품목
         , :V_STOCK_MOVE_TERM   AS STOCK_MOVE_TERM   --저장품 이동 조건
         , :V_YD_CAR_UPP_LOC_CD AS YD_CAR_UPP_LOC_CD --차상위치
         , :V_TRANS_ORD_DATE    AS TRANS_ORD_DATE    --운송지시
         , :V_TRANS_ORD_SEQNO   AS TRANS_ORD_SEQNO  --운송지시순번
         , :V_CAR_CARD_NO       AS CAR_CARD_NO       --차량CARD번호
         , :V_CAR_NO            AS CAR_NO            --차량번호
         , :V_SNBK_WT           AS SNBK_WT           --반송중량
         , :V_YD_ABMTL_REM      AS YD_ABMTL_REM
      FROM DUAL
) DD ON (ST.STL_NO = DD.STL_NO)

WHEN NOT MATCHED THEN
  INSERT (
           STL_NO               , STOCK_ITEM        , STOCK_MOVE_TERM 
         , REGISTER             , REG_DDTT          , MODIFIER  
         , MOD_DDTT             , DEL_YN            , YD_CAR_UPP_LOC_CD
         , TRANS_ORD_DATE       , TRANS_ORD_SEQNO    , CAR_CARD_NO
         , CAR_NO               , SNBK_WT            , YD_ABMTL_REM
         )
  VALUES (
           DD.STL_NO            , DD.STOCK_ITEM      , DD.STOCK_MOVE_TERM 
         , DD.MODIFIER          , DD.MOD_DDTT        , DD.MODIFIER  
         , DD.MOD_DDTT          , DD.DEL_YN          , DD.YD_CAR_UPP_LOC_CD 
         , DD.TRANS_ORD_DATE     , DD.TRANS_ORD_SEQNO  , DD.CAR_CARD_NO
         , DD.CAR_NO            , DD.SNBK_WT           , DD.YD_ABMTL_REM
         )
WHEN MATCHED THEN 
    UPDATE SET
           STOCK_ITEM         = DD.STOCK_ITEM
         , STOCK_MOVE_TERM    = DD.STOCK_MOVE_TERM 
         , MODIFIER           = DD.MODIFIER 
         , MOD_DDTT           = DD.MOD_DDTT          
         , YD_CAR_UPP_LOC_CD  = DD.YD_CAR_UPP_LOC_CD     
         , TRANS_ORD_DATE      = DD.TRANS_ORD_DATE    
         , TRANS_ORD_SEQNO     = DD.TRANS_ORD_SEQNO
         , CAR_CARD_NO        = DD.CAR_CARD_NO         
         , CAR_NO             = DD.CAR_NO       
         , SNBK_WT            = DD.SNBK_WT 
         , DEL_YN             = DD.DEL_YN
		 , YD_ABMTL_REM       = DD.YD_ABMTL_REM 
 </pre> */
public final static String insStockTransInfoSNBK = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.insStockTransInfoSNBK";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStatStkBedActByCol
UPDATE TB_YF_STKBED
   SET MODIFIER              = :V_MODIFIER
      ,MOD_DDTT              = SYSDATE
      ,YD_STK_BED_ACTIVE_STAT = :V_YD_STK_BED_ACTIVE_STAT
 WHERE YD_STK_COL_GP          = :V_YD_STK_COL_GP
   AND DEL_YN                = 'N'
 </pre> */
public final static String updStatStkBedActByCol = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStatStkBedActByCol";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdStkLyrClr1
UPDATE TB_YF_STKLYR
   SET MODIFIER                = :V_MODIFIER
      ,MOD_DDTT                = SYSDATE
      ,STL_NO                = NULL
      ,YD_STK_LYR_ACTIVE_STAT = 'C'
      ,YD_STK_LYR_STAT        = 'E'
 WHERE YD_STK_COL_GP  LIKE  '1_TC'||SUBSTR(:V_YD_STK_COL_GP,5,2)
   AND DEL_YN                  = 'N'
 </pre> */
public final static String updYdStkLyrClr1 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdStkLyrClr1";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.updPageHelpDocDelYn
-- 화면 도움말 - 문서삭제처리
UPDATE USRYFA.TB_YF_HELP_DOC D
   SET D.MODIFIER = :V_MODIFIER
     , D.MOD_DDTT = SYSDATE
     , D.DEL_YN   = 'Y'
 WHERE D.PAGE_ID  = :V_PAGE_ID
   AND D.DOC_SEQ  = :V_DOC_SEQ
 </pre> */
public final static String updPageHelpDocDelYn = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updPageHelpDocDelYn";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnReschCrnSch
--크레인리스케줄 크레인스케줄 수정 
MERGE INTO TB_YF_CRNSCH CS USING (
SELECT SR.YD_SCH_CD
      ,DD.MODIFIER
      ,DECODE(DD.BR_GP,'B',SR.YD_ALT_CRN_PRIOR,SR.YD_WRK_CRN_PRIOR) AS YD_SCH_PRIOR
      ,DECODE(DD.YD_EQP_ID,SR.YD_WRK_CRN,DECODE(DD.BR_GP,'B',SR.YD_ALT_CRN,SR.YD_WRK_CRN)) AS YD_EQP_ID
  FROM TB_YF_SCHRULE SR
      ,(SELECT :V_YD_EQP_ID AS YD_EQP_ID
              ,:V_MODIFIER  AS MODIFIER
              ,:V_BR_GP     AS BR_GP --고장복구구분
          FROM DUAL) DD
 WHERE SR.YD_GP       = SUBSTR(DD.YD_EQP_ID,1,1)
   AND SR.YD_BAY_GP   = SUBSTR(DD.YD_EQP_ID,2,1)
   AND (SR.YD_WRK_CRN = DD.YD_EQP_ID OR SR.YD_ALT_CRN = DD.YD_EQP_ID)
   AND SR.DEL_YN      = 'N'
) DD ON (CS.YD_SCH_CD = DD.YD_SCH_CD 
     AND CS.YD_WRK_PROG_STAT = 'W' 
     AND CS.DEL_YN = 'N' 
     AND (SELECT YD_WRK_PLAN_CRN 
            FROM TB_YF_WRKBOOK WB
           WHERE WB.YD_WBOOK_ID = CS.YD_WBOOK_ID
             AND DEL_YN = 'N') IS NULL 
        )
WHEN MATCHED THEN UPDATE SET
	 CS.MODIFIER     = DD.MODIFIER
    ,CS.MOD_DDTT     = SYSDATE
    ,CS.YD_SCH_PRIOR = NVL(DD.YD_SCH_PRIOR,CS.YD_SCH_PRIOR)
    ,CS.YD_EQP_ID    = DECODE(DD.YD_EQP_ID,NULL,CS.YD_EQP_ID,DD.YD_EQP_ID)
 </pre> */
public final static String updCrnReschCrnSch = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnReschCrnSch";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarProgStatCarudWrkbook
UPDATE TB_YF_TCARSCH
   SET MODIFIER             = :V_MODIFIER
     , MOD_DDTT             = SYSDATE
     , YD_CARUD_WRK_BOOK_ID = :V_YD_CARUD_WRK_BOOK_ID
 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
 </pre> */
public final static String updTcarProgStatCarudWrkbook = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarProgStatCarudWrkbook";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilStkPrfrPriorjl.updStackRuleInfo
UPDATE TB_YF_STACKPRIORITY
   SET YD_SCH_CD        = :V_YD_SCH_CD
 WHERE RULE_ID          = 'YM02'
   AND YD_GP            = :V_YD_GP
   AND SCH_RULE_VAL     = :V_SCH_RULE_VAL
 </pre> */
public final static String updStackRuleInfo = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilStkPrfrPriorjl.updStackRuleInfo";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updAdvSchRuleInfo 
-- 크레인스케줄 고도화기준 수정
UPDATE TB_YF_ADV_SCHRULE
   SET MODIFIER              = :V_MODIFIER             --수정자
     , MOD_DDTT              = SYSDATE                 --수정일
     , YD_WRK_CRN_PRIOR      = :V_YD_WRK_CRN_PRIOR     --작업크레인우선순위
 WHERE YD_SCH_CD = :V_YD_SCH_CD            --스케줄코드       
   AND TERM1     = :V_TERM1
   AND TERM2     = :V_TERM2
   AND TERM3     = :V_TERM3
   AND TERM4     = :V_TERM4
   AND TERM5     = :V_TERM5
 </pre> */
public final static String updAdvSchRuleInfo = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updAdvSchRuleInfo";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateEqualUpperlayerStat
update TB_YF_STKLYR
set YD_STK_LYR_ACTIVE_STAT = NVL(:V_YD_STK_LYR_ACTIVE_STAT,YD_STK_LYR_ACTIVE_STAT) ,
    YD_STK_LYR_STAT = NVL(:V_YD_STK_LYR_STAT,YD_STK_LYR_STAT)
where YD_STK_COL_GP = :V_YD_STK_COL_GP
  and YD_STK_BED_NO = :V_YD_STK_BED_NO
  and YD_STK_LYR_NO >= :V_YD_STK_LYR_NO 
 </pre> */
public final static String updateEqualUpperlayerStat = "bak.yf.facilitywork.putwrecord.session.updateEqualUpperlayerStat";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateEqualLowerlayerStat
update TB_YF_STKLYR
set YD_STK_LYR_ACTIVE_STAT = NVL(:V_YD_STK_LYR_ACTIVE_STAT,YD_STK_LYR_ACTIVE_STAT) ,
    YD_STK_LYR_STAT = NVL(:V_YD_STK_LYR_STAT,YD_STK_LYR_STAT)
where YD_STK_COL_GP = :V_YD_STK_COL_GP
  and YD_STK_BED_NO = :V_YD_STK_BED_NO
  and YD_STK_LYR_NO <= :V_YD_STK_LYR_NO 
  and STL_NO IS NULL
 </pre> */
public final static String updateEqualLowerlayerStat = "bak.yf.facilitywork.putwrecord.session.updateEqualLowerlayerStat";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStatEqpMode
--설비 상태 Mode 수정 
UPDATE TB_YF_EQP
   SET MODIFIER             = :V_MODIFIER
      ,MOD_DDTT             = SYSDATE
      ,YD_EQP_WRK_MODE      = nvl(:V_YD_EQP_WRK_MODE,YD_EQP_WRK_MODE)
      ,YD_EQP_AUTO_CRN_MODE = nvl(:V_YD_EQP_AUTO_CRN_MODE,YD_EQP_AUTO_CRN_MODE)
      ,YD_EQP_WRK_MODE2     = nvl(:V_YD_EQP_WRK_MODE2,YD_EQP_WRK_MODE2)
 WHERE YD_EQP_ID    = :V_YD_EQP_ID
   AND DEL_YN       = 'N'
 </pre> */
public final static String updStatEqpMode = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStatEqpMode";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarunLoadPutLoc
UPDATE TB_YF_STOCK
SET CTS_RELAY_SADDLE = :V_CTS_RELAY_SADDLE
	,MODIFIER = :V_MODIFIER
	,MOD_DDTT = sysdate
WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String updateCarunLoadPutLoc = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarunLoadPutLoc";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.deleteCTSSchByStlNo
--CTS스케줄 삭제 
UPDATE TB_YF_CTS_SCH 
SET MODIFIER        = :V_MODIFIER
  , MOD_DDTT        = SYSDATE
  , DEL_YN          = 'Y'
WHERE DEL_YN        = 'N'
  AND STL_NO        = :V_STL_NO
 </pre> */
public final static String deleteCTSSchByStlNo = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.deleteCTSSchByStlNo";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYmStackcol
UPDATE TB_YF_STKCOL 
SET  
	 MOD_DDTT = SYSDATE             
	,MODIFIER = :V_MODIFIER   
	--,YD_STK_COL_GP = :V_YD_STK_COL_GP       
	,YD_STK_COL_ACTIVE_STAT = :V_YD_STK_COL_ACT_STAT 
	,YD_CAR_USE_GP = :V_YD_CAR_USE_GP      
	,TRN_EQP_CD = :V_TRN_EQP_CD           
	,CAR_NO = :V_CAR_NO              
                ,CARD_NO = :V_CARD_NO
WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String updYmStackcol = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYmStackcol";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateStacklayer
UPDATE TB_YF_STKLYR
SET
    STL_NO                  = NULL,
    YD_STK_LYR_ACTIVE_STAT  = 'E',
    YD_STK_LYR_STAT         = 'E'
WHERE 1=1
AND STL_NO                  = :V_STL_NO
 </pre> */
public final static String updateStacklayer = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateStacklayer";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.delCarFtMvMtl 

UPDATE TB_YD_CARFTMVMTL
   SET DEL_YN = 'Y'
WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
AND    STL_NO = :V_STL_NO   
 </pre> */
public final static String delCarFtMvMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.delCarFtMvMtl";

 /** <pre> 
 -- com.inisteel.cim.yf.common.dao.YfCommDAO.updHomeMvTermList 
UPDATE TB_YF_RULE
   SET DEL_YN   = :V_DEL_YN  
     , DTL_ITEM1 = :V_DTL_ITEM1
     , DTL_ITEM2 = :V_DTL_ITEM2
     , DTL_ITEM3 = :V_DTL_ITEM3
     , DTL_ITEM4 = :V_DTL_ITEM4
     , DTL_ITEM5 = :V_DTL_ITEM5
     , MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
 WHERE REPR_CD_GP = 'APP105'
   AND CD_GP      = :V_CD_GP
   AND ITEM       = :V_ITEM
 </pre> */
public final static String updHomeMvTermList = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updHomeMvTermList";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updEqpHomeBay 
--설비 홈동 수정 
UPDATE TB_YF_EQP
   SET MODIFIER       = :V_MODIFIER
      ,MOD_DDTT       = SYSDATE
      ,YD_HOME_BAY_GP  = :V_YD_HOME_BAY_GP
 WHERE YD_EQP_ID      = :V_YD_EQP_ID

 </pre> */
public final static String updEqpHomeBay = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updEqpHomeBay";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.AcoilDAO.updSchPriorModWrkBook 
--작업예약 스케쥴우선순위 수정
UPDATE TB_YF_WRKBOOK
   SET MODIFIER     = :V_MODIFIER
     , MOD_DDTT     = SYSDATE
     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
 WHERE 1 = 1
   AND DEL_YN       = 'N'
   AND YD_WBOOK_ID  IN(
                        SELECT YD_WBOOK_ID
                          FROM TB_YF_WRKBOOK 
                         WHERE DEL_YN           = 'N'
                           AND YD_SCH_CD        = :V_YD_SCH_CD
                           AND YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
                           AND YD_BAY_GP        = :V_YD_BAY_GP     --상차동
                           AND YD_AIM_BAY_GP    = :V_YD_AIM_BAY_GP --하차동      
                       )
 </pre> */
public final static String updSchPriorModWrkBook = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updSchPriorModWrkBook";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updMvCarSchCmpl 
UPDATE TB_YD_CARSCH
SET    MOD_DDTT = SYSDATE
      ,MODIFIER = :V_MODIFIER
      ,YD_CAR_PROG_STAT = NVL(:V_YD_CAR_PROG_STAT,YD_CAR_PROG_STAT)
      ,YD_CARLD_ST_DT = DECODE(NVL(:V_YD_CARLD_ST_DT,'NULL'),'NULL',YD_CARLD_ST_DT,SYSDATE)
      ,YD_CARLD_CMPL_DT = DECODE(NVL(:V_YD_CARLD_CMPL_DT,'NULL'),'NULL',YD_CARLD_CMPL_DT,SYSDATE)
      ,YD_CARUD_ST_DT = DECODE(NVL(:V_YD_CARUD_ST_DT,'NULL'),'NULL',YD_CARUD_ST_DT,SYSDATE)
      ,YD_CARUD_CMPL_DT = DECODE(NVL(:V_YD_CARUD_CMPL_DT,'NULL'),'NULL',YD_CARUD_CMPL_DT,SYSDATE)
WHERE  YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updMvCarSchCmpl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updMvCarSchCmpl";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updTcarSchInsSch 
-- 대차스케줄 등록
MERGE INTO TB_YF_TCARSCH TS USING (
SELECT :V_YD_TCAR_SCH_ID       AS YD_TCAR_SCH_ID
      ,:V_MODIFIER             AS MODIFIER
      ,SYSDATE                 AS MOD_DDTT
      ,'N'                     AS DEL_YN
      ,:V_YD_EQP_ID            AS YD_EQP_ID
      ,'U'                     AS YD_EQP_WRK_STAT     --공차
      ,:V_YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
      ,:V_YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
      ,:V_YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
      ,:V_YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
      ,:V_YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
      ,'6'                     AS YD_CARLD_SCH_REQ_GP --공대차도착
      ,'3'                     AS YD_CARUD_SCH_REQ_GP --영대차도착
  FROM DUAL
) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
WHEN MATCHED THEN UPDATE SET
	 TS.MODIFIER             = DD.MODIFIER
    ,TS.MOD_DDTT             = DD.MOD_DDTT
    ,TS.YD_EQP_WRK_STAT      = DD.YD_EQP_WRK_STAT
    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID
    ,TS.YD_CARLD_LEV_LOC     = DD.YD_CARLD_LEV_LOC
    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
    ,TS.YD_CARLD_SCH_REQ_GP  = DD.YD_CARLD_SCH_REQ_GP
    ,TS.YD_CARUD_SCH_REQ_GP  = DD.YD_CARUD_SCH_REQ_GP
WHEN NOT MATCHED THEN
INSERT (TS.YD_TCAR_SCH_ID   , TS.REGISTER            , TS.REG_DDTT           , TS.MODIFIER         ,
        TS.MOD_DDTT         , TS.DEL_YN              , TS.YD_EQP_ID          , TS.YD_EQP_WRK_STAT  ,
        TS.YD_CAR_PROG_STAT , TS.YD_CARLD_WRK_BOOK_ID, TS.YD_CARLD_LEV_LOC   , TS.YD_CARLD_STOP_LOC,
        TS.YD_CARUD_STOP_LOC, TS.YD_CARLD_SCH_REQ_GP , TS.YD_CARUD_SCH_REQ_GP)
VALUES (DD.YD_TCAR_SCH_ID   , DD.MODIFIER            , DD.MOD_DDTT           , DD.MODIFIER         ,
        DD.MOD_DDTT         , DD.DEL_YN              , DD.YD_EQP_ID          , DD.YD_EQP_WRK_STAT  ,
        DD.YD_CAR_PROG_STAT , DD.YD_CARLD_WRK_BOOK_ID, DD.YD_CARLD_LEV_LOC   , DD.YD_CARLD_STOP_LOC,
        DD.YD_CARUD_STOP_LOC, DD.YD_CARLD_SCH_REQ_GP , DD.YD_CARUD_SCH_REQ_GP)
 </pre> */
public final static String mrgTcarSchInsSch = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.mrgTcarSchInsSch";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updYdStkLyrByPk  
UPDATE USRYDA.TB_YD_STKLYR
   SET STL_NO			   = :V_STL_NO
	 , YD_STK_LYR_MTL_STAT = :V_YD_STK_LYR_MTL_STAT
	 , MODIFIER            = :V_MODIFIER
 	 , MOD_DDTT            = SYSDATE     
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
 </pre> */
public final static String updYdStkLyrByPk = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updYdStkLyrByPk";

 /** <pre> 
--yf.common.YfCommonDao.MergeCarftmvmtl
MERGE 
 INTO TB_YD_CARFTMVMTL A
 USING ( SELECT :V_MODIFIER             AS MODIFIER
               ,:V_YD_CAR_SCH_ID        AS YD_CAR_SCH_ID
               ,:V_STL_NO               AS STL_NO
               ,:V_DEL_YN               AS DEL_YN
               ,:V_YD_CAR_UPP_LOC_CD    AS YD_CAR_UPP_LOC_CD
               ,:V_YD_STK_BED_NO        AS YD_STK_BED_NO
               ,:V_YD_STK_LYR_NO        AS YD_STK_LYR_NO
               ,:V_HCR_GP               AS HCR_GP
               ,:V_STL_PROG_CD          AS STL_PROG_CD
               ,:V_YD_MTL_ITEM          AS YD_MTL_ITEM
               ,:V_YD_ROUTE_GP          AS YD_ROUTE_GP
           FROM DUAL ) B
  
  ON (A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID AND A.STL_NO = B.STL_NO)
  
  WHEN MATCHED THEN 
       UPDATE SET A.DEL_YN = DECODE(NVL(B.DEL_YN,A.DEL_YN),'-','',B.DEL_YN)
                 ,A.YD_CAR_UPP_LOC_CD = DECODE(NVL(B.YD_CAR_UPP_LOC_CD,A.YD_CAR_UPP_LOC_CD),'-','',B.YD_CAR_UPP_LOC_CD)
                 ,A.YD_STK_BED_NO = DECODE(NVL(B.YD_STK_BED_NO,A.YD_STK_BED_NO),'-','',B.YD_STK_BED_NO)
                 ,A.YD_STK_LYR_NO = DECODE(NVL(B.YD_STK_LYR_NO,A.YD_STK_LYR_NO),'-','',B.YD_STK_LYR_NO)
                 ,A.HCR_GP = DECODE(NVL(B.HCR_GP,A.HCR_GP),'-','',B.HCR_GP)
                 ,A.STL_PROG_CD = DECODE(NVL(B.STL_PROG_CD,A.STL_PROG_CD),'-','',B.STL_PROG_CD)
                 ,A.YD_MTL_ITEM = DECODE(NVL(B.YD_MTL_ITEM,A.YD_MTL_ITEM),'-','',B.YD_MTL_ITEM)
                 ,A.YD_ROUTE_GP = DECODE(NVL(B.YD_ROUTE_GP,A.YD_ROUTE_GP),'-','',B.YD_ROUTE_GP)
                 ,A.MODIFIER = B.MODIFIER
                 ,A.MOD_DDTT = SYSDATE
   
  WHEN NOT MATCHED THEN
           INSERT (YD_CAR_SCH_ID
                   ,STL_NO
                   ,REGISTER
                   ,REG_DDTT
                   ,MODIFIER
                   ,MOD_DDTT
                   ,DEL_YN
                   ,YD_CAR_UPP_LOC_CD
                   ,YD_STK_BED_NO
                   ,YD_STK_LYR_NO
                   ,HCR_GP
                   ,STL_PROG_CD
                   ,YD_MTL_ITEM
                   ,YD_ROUTE_GP)
           VALUES ( B.YD_CAR_SCH_ID
                   ,B.STL_NO
                   ,B.MODIFIER
                   ,SYSDATE
                   ,B.MODIFIER
                   ,SYSDATE
                   ,B.DEL_YN
                   ,B.YD_CAR_UPP_LOC_CD
                   ,B.YD_STK_BED_NO
                   ,B.YD_STK_LYR_NO
                   ,B.HCR_GP
                   ,B.STL_PROG_CD
                   ,B.YD_MTL_ITEM
                   ,B.YD_ROUTE_GP)
 </pre> */
public final static String MergeCarftmvmtl = "bak.yf.common.YfCommonDao.MergeCarftmvmtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insWrkHist_AxYML010
INSERT INTO TB_YF_WRKHIST (
     YD_WRK_HIST_ID
    ,YD_CRN_SCH_ID
    ,STL_NO
    ,YD_EQP_ID
    ,YD_UP_WR_LOC
    ,YD_UP_WR_LYR
    ,YD_UP_CMPL_DT
    ,YD_DN_WR_LOC
    ,YD_DN_WR_LYR
    ,YD_DN_CMPL_DT
    ,REGISTER
    ,REG_DDTT
    ,MODIFIER
    ,MOD_DDTT
    ,DEL_YN
    ,YD_GP
) VALUES (
     TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YF_WRKHIST_SEQ.NEXTVAL
    ,:V_YD_CRN_SCH_ID
    ,:V_STL_NO
    ,:V_YD_EQP_ID
    ,:V_YD_UP_WR_LOC
    ,:V_YD_UP_WR_LYR
    ,TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')
    ,:V_YD_DN_WR_LOC
    ,:V_YD_DN_WR_LYR
    ,TO_DATE(:V_YD_DN_CMPL_DT,'YYYYMMDDHH24MISS')
    ,:V_MODIFIER
    ,SYSDATE
    ,:V_MODIFIER
    ,SYSDATE
    ,'N'
    ,:V_YD_GP
)
 </pre> */
public final static String insWrkHist_AxYML010 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insWrkHist_AxYML010";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updateunLoadTimeToPT
UPDATE TB_PT_STLFRTOMOVE
   SET FRTOMOVE_DONE_DATE =  SYSDATE
     , FTMV_HDS_DD = TO_CHAR(SYSDATE - (6/24),'YYYYMMDD')
     , FRTOMOVE_STAT_CD = '*'
     , MODIFIER = 'SYSTEM'
     , MOD_DDTT =  SYSDATE
 WHERE STL_NO = :V_STL_NO
   AND FRTOMOVE_STAT_CD <> '*'  --이미 실적처리가 된 경우
   AND TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
                            FROM TB_PT_STLFRTOMOVE
                           WHERE STL_NO = :V_STL_NO
                             AND FRTOMOVE_STAT_CD NOT IN ('Z','C')
                          )
 </pre> */
public final static String updateunLoadTimeToPT = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updateunLoadTimeToPT";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateCraneStackLayerActivStat 
UPDATE TB_YF_STKLYR
   SET YD_STK_LYR_ACTIVE_STAT = :V_YD_STK_LYR_ACTIVE_STAT
     , MODIFIER   = 'SYSTEM'
     , MOD_DDTT   = SYSDATE     
 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO   
   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
 </pre> */
public final static String updateCraneStackLayerActivStat = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateCraneStackLayerActivStat";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock4
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM = (CASE WHEN STOCK_MOVE_TERM ='VL' THEN 'VL' ELSE :V_STOCK_MOVE_TERM END),
    CAR_CARD_NO     = :V_CAR_CARD_NO,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND TRANS_ORD_DATE  = :V_TRANS_ORD_DATE
AND TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
 </pre> */
public final static String updYdStock4 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStock4";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updAdvCrnPrior 
UPDATE TB_YF_ADV_SCHRULE
   SET MODIFIER              = :V_MODIFIER             --수정자
     , MOD_DDTT              = SYSDATE                 --수정일
     , ADV_CRN_PRIOR         = :V_ADV_CRN_PRIOR        --고도화우선순위
 WHERE YD_SCH_CD             = :V_YD_SCH_CD            --스케줄코드   

 </pre> */
public final static String updAdvCrnPrior = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updAdvCrnPrior";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateCarPointUsetype 
UPDATE USRYDA.TB_YD_CARPOINT
SET YD_CAR_USETYPE_GP=:V_USETYPE_GP
  , MOD_DDTT=SYSDATE
  , MODIFIER=:V_MODIFIER
 WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD
 </pre> */
public final static String updateCarPointUsetype = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateCarPointUsetype";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYdStkColUsetype 
UPDATE USRYDA.TB_YD_STKCOL
SET YD_STKBED_USG_CD=:V_YD_STKBED_USG_CD
  , MODIFIER=:V_MODIFIER
  , MOD_DDTT=SYSDATE
WHERE YD_STK_COL_GP=(SELECT REPLACE(YD_STK_COL_GP,'TR1','TR0')
                       FROM TB_YD_CARPOINT
                     WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD
                     )
 </pre> */
public final static String updateYdStkColUsetype = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYdStkColUsetype";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYmStock27
UPDATE TB_YF_STOCK
SET
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE,
    KEEP_STL_YN = :V_KEEP_STL_YN
WHERE 1=1
AND STL_NO      = :V_STL_NO
 </pre> */
public final static String updYmStock27 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYmStock27";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdStkLyrActiveTC
UPDATE TB_YF_STKLYR
   SET MODIFIER                = :V_MODIFIER
      ,MOD_DDTT                = SYSDATE
      ,STL_NO                  = NULL
      ,YD_STK_LYR_ACTIVE_STAT  = 'E'
      ,YD_STK_LYR_STAT         = 'E'
 WHERE YD_STK_COL_GP            = :V_YD_STK_COL_GP
   AND SUBSTR(YD_STK_COL_GP,1,1)= '1'
   AND SUBSTR(YD_STK_COL_GP,3,2)= 'TC'
   AND DEL_YN                   = 'N'
 </pre> */
public final static String updYdStkLyrActiveTC = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdStkLyrActiveTC";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.insertStock 
INSERT INTO TB_YF_STOCK
(
  STL_NO
, STOCK_ITEM 
--, STOCK_STAT -- 박판열연 사용안함(코일공통테이블조회) 
, STOCK_MOVE_TERM 
, REGISTER 
, REG_DDTT 
, DEL_YN
)
SELECT COIL_NO 
     , (CASE WHEN STL_APPEAR_GP='Y' THEN 'CG' ELSE 'CM' END)  --재료회형구분 Y(제품)이면 CG(coil제품) 아니면CM(coil소재)
     , '2'  --정정실적 처리
--     , 'EC' --이송작업대기
     , :V_MODIFIER
     , SYSDATE
     ,'N'
  FROM TB_PT_COILCOMM
 WHERE COIL_NO = :V_COIL_NO
 </pre> */
public final static String insertStock = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insertStock";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.AcoilDAO.updSchPriorModCrnSch 
--크레인스케줄 우선순위 수정
UPDATE TB_YF_CRNSCH
   SET MODIFIER     = :V_MODIFIER
     , MOD_DDTT     = SYSDATE
     , YD_SCH_PRIOR = NVL(TO_NUMBER(:V_YD_SCH_PRIOR),1)
 WHERE 1 = 1
   AND DEL_YN       = 'N'
   AND YD_WBOOK_ID  IN(
                        SELECT YD_WBOOK_ID
                          FROM TB_YF_WRKBOOK 
                         WHERE DEL_YN           = 'N'
                           AND YD_SCH_CD        = :V_YD_SCH_CD
                           AND YD_WRK_PLAN_TCAR = :V_YD_WRK_PLAN_TCAR
                           AND YD_BAY_GP        = :V_YD_BAY_GP     --상차동
                           AND YD_AIM_BAY_GP    = :V_YD_AIM_BAY_GP --하차동      
                       )
 </pre> */
public final static String updSchPriorModCrnSch = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updSchPriorModCrnSch";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updMvCarSchPntWo 
UPDATE USRYDA.TB_YD_CARSCH
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
 </pre> */
public final static String updMvCarSchPntWo = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updMvCarSchPntWo";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTbYfWrkBook
--작업예약 크레인 변경,Multi 작업
UPDATE TB_YF_WRKBOOK
SET
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    YD_BAY_GP               = :V_YD_BAY_GP,
    YD_SCH_CD               = :V_YD_SCH_CD,
    YD_SCH_PRIOR            = :V_YD_SCH_PRIOR,
    YD_SCH_ST_GP            = :V_YD_SCH_ST_GP,
    YD_WRK_PLAN_CRN         = :V_YD_WRK_PLAN_CRN,
    YD_WRK_PLAN_CRN2        = :V_YD_WRK_PLAN_CRN2,
    CHARGE_LOT_NO_DIV_YN    = :V_CHARGE_LOT_NO_DIV_YN,
    YD_TO_LOC_GUIDE         = :V_YD_TO_LOC_GUIDE
WHERE 1=1
AND YD_WBOOK_ID             = :V_YD_WBOOK_ID
 </pre> */
public final static String updTbYfWrkBook = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTbYfWrkBook";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updLyrByLoc
UPDATE TB_YF_STKLYR
SET
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    STL_NO          = :V_STL_NO,
    YD_STK_LYR_STAT = :V_YD_STK_LYR_STAT,
    YD_STK_LYR_ACTIVE_STAT = NVL(:V_YD_STK_LYR_ACTIVE_STAT, YD_STK_LYR_ACTIVE_STAT)
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
AND YD_STK_LYR_NO   = :V_YD_STK_LYR_NO
 </pre> */
public final static String updLyrByLoc = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updLyrByLoc";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updWrkBookMtlStackColGp
UPDATE TB_YF_WRKBOOKMTL
SET
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    YD_STK_COL_GP   = :V_YD_STK_COL_GP
WHERE 1=1
AND YD_WBOOK_ID     = :YD_WBOOK_ID
 </pre> */
public final static String updWrkBookMtlStackColGp = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updWrkBookMtlStackColGp";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarExaminationGoodsDetjlNEW2
UPDATE TB_YD_EXAMINATIONCHKLIST
SET YD_CAR_UPP_LOC_CD=NVL(:V_YD_CAR_UPP_LOC_CD,YD_CAR_UPP_LOC_CD)
  , YD_AB_CD =:V_YD_AB_CD
  , YD_AB_CD2 =:V_YD_AB_CD2
  ,  LABEL_YN = NVL((SELECT (CASE WHEN :V_LABEL_YN ='Y' THEN 'Y' 
                             ELSE ( CASE WHEN (Z.FNL_MATCH_ORDERTRANS_OCCURDATE||Z.FNL_MATCH_ORDERTRANS_OCCURTIME) >= to_char(Z.SHEAR_WORD_DT,'YYYYMMDDHH24MISS')
                                            THEN 'Y' 
                                         ELSE FNC_DM_GET_GOODSPROGLABEL_YN @DL_SMDB (STL_NO) 
                                     END)
                          END)
                  FROM TB_PT_COILCOMM Z
                 WHERE Z.COIL_NO = STL_NO),'N')
  , CHECKING_YN ='Y'
  , MODIFIER = NVL(:V_MODIFIER, 'YDPDA2')
  , MOD_DDTT= SYSDATE
WHERE TRANS_ORD_DATE=:V_TRANS_ORD_DATE
  AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
  AND STL_NO =:V_STL_NO
 </pre> */
public final static String updateCarExaminationGoodsDetjlNEW2 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarExaminationGoodsDetjlNEW2";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCarPointActStat
UPDATE  TB_YD_CARPOINT
   SET  MODIFIER = :V_MODIFIER
       ,MOD_DDTT = SYSDATE
       ,YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT
 WHERE  YD_STK_COL_GP = :V_YD_STK_COL_GP 
 </pre> */
public final static String updYdCarPointActStat = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCarPointActStat";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.insYfCrCoilComm
INSERT INTO TB_YF_CR_COILCOMM
(
    COIL_NO,
    RCD_STS_TP,
    COIL_INF_CRT_TP,
    COIL_INF_END_TP,
    WK_PRG_STS,
    PRG_CD,
    LN_TP,
    BF_PRG_CD,
    BF_LN_TP,
    COIL_ACT_THK,
    COIL_ACT_THK_MNV,
    COIL_ACT_THK_MXV,
    COIL_ACT_WTH,
    COIL_ACT_WTH_MNV,
    COIL_ACT_WTH_MXV,
    COIL_ACT_LTH,
    COIL_RMW_WGT,
    COIL_THY_WGT,
    COIL_GRS_RMW_WGT,
    COIL_GRS_THY_WGT,
    COIL_IDIA,
    COIL_ODIA,
    HC_AW,
    CC_AW,
    REM_PROC,
    NXT_PROC_CD,
    BF_PROC_CD,
    REQ_NXT_PROC_CD,
    REQ_NXT_PROC_CAU_CD,
    ACT_PAS_PROC,
    PRDN_CD,
    ORD_NO,
    ORD_LN,
    ORD_NO_2,
    ORD_LN_2,
    PL_ORD_YN,
    BF_ORD_NO,
    BF_ORD_LN,
    OBJ_CHG_APT_DH,
    WGT_DCS_MTH_TP,
    CRM_MNF_STD_CD,
    MTL_CD,
    RMTL_SYM,
    COIL_MRG_TP,
    FST_RMTL_NO,
    RPV_RMTL_NO,
    RMTL_NO_1,
    RMTL_LTH_1,
    RMTL_WGT_1,
    RMTL_NO_2,
    RMTL_LTH_2,
    RMTL_WGT_2,
    RMTL_NO_3,
    RMTL_LTH_3,
    RMTL_WGT_3,
    RMTL_SLP_CD,
    RMTL_HEAT_NO,
    SLP_RMTL_NO,
    PL_WLD_PRT,
    PL_WLD_CNT,
    PL_WLD_PNT_LOC_1,
    PL_WLD_PNT_LOC_2,
    PL_WLD_PNT_LOC_3,
    PL_WLD_PNT_LOC_4,
    PL_WLD_PNT_LOC_5,
    PRD_WLD_CNT,
    MID_WLD_PNT_LOC_1,
    MID_WLD_PNT_LOC_2,
    MID_WLD_PNT_LOC_3,
    ORD_PDN_TP,
    URG_MTL_TP,
    COIL_TOP_BOT_TP,
    DMY_PTT_PLT_TP,
    SEM_PRD_TP,
    PCM_ML_CLN_YN,
    PIN_HOLE_TP,
    DMY_COIL_USE_CNT,
    PRD_AUC_PRG_STS_TP,
    PRD_AUC_RGS_DH,
    COIL_MTL_OUT_SHP_TP,
    HLD_YN,
    HLD_RGS_DH,
    ERP_REG_YN,
    LOD_LOC,
    BF_LOD_LOC,
    STK_NO,
    BASE_NO,
    SHT_CNT,
    COIL_SLV_USE_YN,
    SPM_USE_YN,
    PL_ST_YN,
    ANN_ST_YN,
    COR_ST_YN,
    ORD_SPL_TP,
    SPL_DH,
    SPL_CAU_CD,
    COIL_SUR_GRD,
    COIL_SHP_GRD,
    COIL_SZ_GRD,
    COIL_UNT_WGT_GRD,
    COIL_APR_INS_GRD,
    COIL_MQL_GRD,
    COIL_MQL_GRD_CAU_CD,
    COIL_MQL_SYN_GRD,
    COIL_ING_GRD,
    COIL_QLT_TRK_GRD,
    OP_GRD,
    COIL_APR_INS_GRD_CAU_CD,
    COIL_ARP_INS_DH,
    PRD_SYN_GRD,
    PRD_SYN_GRD_CAU_CD,
    COIL_CLR_MPR_GRD,
    PRD_SYN_JDG_APT_DD,
    PRD_SYN_JDG_DH,
    MQL_TSTP_NO,
    MPR_TSTP_NO,
    SMP_GTH_CNT,
    SPC_AVR,
    MQL_SYM,
    COIL_MQC,
    GW_ASG_CD,
    WK_GW_FRN,
    WK_GW_BAK,
    ORD_GW_ASG_CD,
    ORD_GW_FRN,
    ORD_GW_BAK,
    COIL_GAL_PTR_CD,
    COIL_CLR_PTR_CD,
    COIL_SUR_PHM_CD,
    COIL_ROU_CD,
    ORD_USG_CD,
    COIL_OIL_PNT_CD,
    COIL_PTT_FLM_CD,
    CRT_NO,
    PAS_PROC_CD_1,
    COIL_RPROC_CNT_1,
    COIL_NO_1,
    PAS_PROC_CD_2,
    COIL_RPROC_CNT_2,
    COIL_NO_2,
    PAS_PROC_CD_3,
    COIL_RPROC_CNT_3,
    COIL_NO_3,
    PAS_PROC_CD_4,
    COIL_RPROC_CNT_4,
    COIL_NO_4,
    PAS_PROC_CD_5,
    COIL_RPROC_CNT_5,
    COIL_NO_5,
    PAS_PROC_CD_6,
    COIL_RPROC_CNT_6,
    COIL_NO_6,
    PAS_PROC_CD_7,
    COIL_RPROC_CNT_7,
    COIL_NO_7,
    PAS_PROC_CD_8,
    COIL_RPROC_CNT_8,
    COIL_NO_8,
    PAS_PROC_CD_9,
    COIL_RPROC_CNT_9,
    COIL_NO_9,
    PAS_PROC_CD_10,
    COIL_RPROC_CNT_10,
    COIL_NO_10,
    POC_TP,
    POC_CMP_CD,
    PAK_EQU_CD,
    COIL_PAK_PTN,
    COIL_PAK_APT_DD,
    COIL_PAK_CPLT_DH,
    COIL_PAK_SHF_GRP,
    PAK_WK_TP,
    LBL_PUB_CNT,
    LBL_PUB_DH,
    PRD_WHS_DH,
    PRD_WHS_APT_DD,
    PRD_DLV_REQ_KND_TP,
    CTD_SAL_TP,
    PRD_DLV_KND_TP,
    DLV_DH,
    PRD_DLV_APT_DD,
    SHPG_DH,
    SHPG_APT_DD,
    REA_INF_WHS_TP,
    REA_INF_RET_TP,
    PRD_RET_DH,
    PRD_RET_APT_DD,
    PRD_RET_PRG_STS_TP,
    PRD_RET_REQ_PROC_CD,
    PRD_RET_REQ_PRG_CD,
    PRD_RET_REQ_CAU_CD,
    SHPG_HLD_TP,
    SHPG_HLD_DD,
    SHPG_HLD_CAU_TXT,
    SHPG_HLD_CNL_DD,
    PRD_LONG_STK_CAU_CD,
    PRD_LONG_STK_RGS_DH,
    REA_INF_CRYN_TP,
    PRD_CRYN_CAU_TP,
    PRD_CRYN_DH,
    PRD_CRYN_APT_DD,
    DLV_PROG_STS_TP,
    MS_PUB_YN,
    MS_PUB_DH,
    PCOIL_NO,
    TRF_REQ_NO,
    TRF_PRG_STS_TP,
    TRF_WHS_DH,
    PLNT_TP,
    CREATED_OBJECT_TYPE,
    CREATED_OBJECT_ID,
    CREATED_PROGRAM_ID,
    CREATION_TIMESTAMP,
    LAST_UPDATED_OBJECT_TYPE,
    LAST_UPDATED_OBJECT_ID,
    LAST_UPDATE_PROGRAM_ID,
    LAST_UPDATE_TIMESTAMP,
    DATA_END_STATUS,
    DATA_END_OBJECT_TYPE,
    DATA_END_OBJECT_ID,
    DATA_END_PROGRAM_ID,
    DATA_END_TIMESTAMP,
    ARCHIVE_COMPLETED_FLAG,
    ARCHIVED_EMPLOYEE_NUM,
    ARCHIVED_TIMESTAMP,
    ARCHIVE_PROGRAM_ID,
    CCL_CR_TRT_TP,
    IN_PLT_CVT_CAU_CD,
    CCL_2PASS_YN,
    COIL_RMW_WGT_1,
    COIL_RMW_WGT_2,
    SAP_MTL_CD,
    ITEMNAME_CD,
    PDN_PLNT_TP,
    PLN_PAS_PROC_CMN,
    PAS_PLNT_ACT,
    INV_NO,
    LGS_BAS_CD,
    WIP_MTL_CD,
    MS_SPE_YN,
    UNDER_COIL_YN,
    UNDER_COIL_PLN_PAS,
    UNDER_COIL_OBJECT_ID,
    COR_CAU_TXT,
    POC_TAR_MTL,
    BLDP_TP
)
VALUES
(
    :V_COIL_NO,
    :V_RCD_STS_TP,
    :V_COIL_INF_CRT_TP,
    :V_COIL_INF_END_TP,
    :V_WK_PRG_STS,
    :V_PRG_CD,
    :V_LN_TP,
    :V_BF_PRG_CD,
    :V_BF_LN_TP,
    :V_COIL_ACT_THK,
    :V_COIL_ACT_THK_MNV,
    :V_COIL_ACT_THK_MXV,
    :V_COIL_ACT_WTH,
    :V_COIL_ACT_WTH_MNV,
    :V_COIL_ACT_WTH_MXV,
    :V_COIL_ACT_LTH,
    :V_COIL_RMW_WGT,
    :V_COIL_THY_WGT,
    :V_COIL_GRS_RMW_WGT,
    :V_COIL_GRS_THY_WGT,
    :V_COIL_IDIA,
    :V_COIL_ODIA,
    :V_HC_AW,
    :V_CC_AW,
    :V_REM_PROC,
    :V_NXT_PROC_CD,
    :V_BF_PROC_CD,
    :V_REQ_NXT_PROC_CD,
    :V_REQ_NXT_PROC_CAU_CD,
    :V_ACT_PAS_PROC,
    :V_PRDN_CD,
    :V_ORD_NO,
    :V_ORD_LN,
    :V_ORD_NO_2,
    :V_ORD_LN_2,
    :V_PL_ORD_YN,
    :V_BF_ORD_NO,
    :V_BF_ORD_LN,
    TO_DATE(:V_OBJ_CHG_APT_DH, 'YYYYMMDDHH24MISS'),
    :V_WGT_DCS_MTH_TP,
    :V_CRM_MNF_STD_CD,
    :V_MTL_CD,
    :V_RMTL_SYM,
    :V_COIL_MRG_TP,
    :V_FST_RMTL_NO,
    :V_RPV_RMTL_NO,
    :V_RMTL_NO_1,
    :V_RMTL_LTH_1,
    :V_RMTL_WGT_1,
    :V_RMTL_NO_2,
    :V_RMTL_LTH_2,
    :V_RMTL_WGT_2,
    :V_RMTL_NO_3,
    :V_RMTL_LTH_3,
    :V_RMTL_WGT_3,
    :V_RMTL_SLP_CD,
    :V_RMTL_HEAT_NO,
    :V_SLP_RMTL_NO,
    :V_PL_WLD_PRT,
    :V_PL_WLD_CNT,
    :V_PL_WLD_PNT_LOC_1,
    :V_PL_WLD_PNT_LOC_2,
    :V_PL_WLD_PNT_LOC_3,
    :V_PL_WLD_PNT_LOC_4,
    :V_PL_WLD_PNT_LOC_5,
    :V_PRD_WLD_CNT,
    :V_MID_WLD_PNT_LOC_1,
    :V_MID_WLD_PNT_LOC_2,
    :V_MID_WLD_PNT_LOC_3,
    :V_ORD_PDN_TP,
    :V_URG_MTL_TP,
    :V_COIL_TOP_BOT_TP,
    :V_DMY_PTT_PLT_TP,
    :V_SEM_PRD_TP,
    :V_PCM_ML_CLN_YN,
    :V_PIN_HOLE_TP,
    :V_DMY_COIL_USE_CNT,
    :V_PRD_AUC_PRG_STS_TP,
    TO_DATE(:V_PRD_AUC_RGS_DH, 'YYYYMMDDHH24MISS'),
    :V_COIL_MTL_OUT_SHP_TP,
    :V_HLD_YN,
    TO_DATE(:V_HLD_RGS_DH, 'YYYYMMDDHH24MISS'),
    :V_ERP_REG_YN,
    :V_LOD_LOC,
    :V_BF_LOD_LOC,
    :V_STK_NO,
    :V_BASE_NO,
    :V_SHT_CNT,
    :V_COIL_SLV_USE_YN,
    :V_SPM_USE_YN,
    :V_PL_ST_YN,
    :V_ANN_ST_YN,
    :V_COR_ST_YN,
    :V_ORD_SPL_TP,
    TO_DATE(:V_SPL_DH, 'YYYYMMDDHH24MISS'),
    :V_SPL_CAU_CD,
    :V_COIL_SUR_GRD,
    :V_COIL_SHP_GRD,
    :V_COIL_SZ_GRD,
    :V_COIL_UNT_WGT_GRD,
    :V_COIL_APR_INS_GRD,
    :V_COIL_MQL_GRD,
    :V_COIL_MQL_GRD_CAU_CD,
    :V_COIL_MQL_SYN_GRD,
    :V_COIL_ING_GRD,
    :V_COIL_QLT_TRK_GRD,
    :V_OP_GRD,
    :V_COIL_APR_INS_GRD_CAU_CD,
    TO_DATE(:V_COIL_ARP_INS_DH, 'YYYYMMDDHH24MISS'),
    :V_PRD_SYN_GRD,
    :V_PRD_SYN_GRD_CAU_CD,
    :V_COIL_CLR_MPR_GRD,
    :V_PRD_SYN_JDG_APT_DD,
    TO_DATE(:V_PRD_SYN_JDG_DH, 'YYYYMMDDHH24MISS'),
    :V_MQL_TSTP_NO,
    :V_MPR_TSTP_NO,
    :V_SMP_GTH_CNT,
    :V_SPC_AVR,
    :V_MQL_SYM,
    :V_COIL_MQC,
    :V_GW_ASG_CD,
    :V_WK_GW_FRN,
    :V_WK_GW_BAK,
    :V_ORD_GW_ASG_CD,
    :V_ORD_GW_FRN,
    :V_ORD_GW_BAK,
    :V_COIL_GAL_PTR_CD,
    :V_COIL_CLR_PTR_CD,
    :V_COIL_SUR_PHM_CD,
    :V_COIL_ROU_CD,
    :V_ORD_USG_CD,
    :V_COIL_OIL_PNT_CD,
    :V_COIL_PTT_FLM_CD,
    :V_CRT_NO,
    :V_PAS_PROC_CD_1,
    :V_COIL_RPROC_CNT_1,
    :V_COIL_NO_1,
    :V_PAS_PROC_CD_2,
    :V_COIL_RPROC_CNT_2,
    :V_COIL_NO_2,
    :V_PAS_PROC_CD_3,
    :V_COIL_RPROC_CNT_3,
    :V_COIL_NO_3,
    :V_PAS_PROC_CD_4,
    :V_COIL_RPROC_CNT_4,
    :V_COIL_NO_4,
    :V_PAS_PROC_CD_5,
    :V_COIL_RPROC_CNT_5,
    :V_COIL_NO_5,
    :V_PAS_PROC_CD_6,
    :V_COIL_RPROC_CNT_6,
    :V_COIL_NO_6,
    :V_PAS_PROC_CD_7,
    :V_COIL_RPROC_CNT_7,
    :V_COIL_NO_7,
    :V_PAS_PROC_CD_8,
    :V_COIL_RPROC_CNT_8,
    :V_COIL_NO_8,
    :V_PAS_PROC_CD_9,
    :V_COIL_RPROC_CNT_9,
    :V_COIL_NO_9,
    :V_PAS_PROC_CD_10,
    :V_COIL_RPROC_CNT_10,
    :V_COIL_NO_10,
    :V_POC_TP,
    :V_POC_CMP_CD,
    :V_PAK_EQU_CD,
    :V_COIL_PAK_PTN,
    :V_COIL_PAK_APT_DD,
    TO_DATE(:V_COIL_PAK_CPLT_DH, 'YYYYMMDDHH24MISS'),
    :V_COIL_PAK_SHF_GRP,
    :V_PAK_WK_TP,
    :V_LBL_PUB_CNT,
    TO_DATE(:V_LBL_PUB_DH, 'YYYYMMDDHH24MISS'),
    TO_DATE(:V_PRD_WHS_DH, 'YYYYMMDDHH24MISS'),
    :V_PRD_WHS_APT_DD,
    :V_PRD_DLV_REQ_KND_TP,
    :V_CTD_SAL_TP,
    :V_PRD_DLV_KND_TP,
    TO_DATE(:V_DLV_DH, 'YYYYMMDDHH24MISS'),
    :V_PRD_DLV_APT_DD,
    TO_DATE(:V_SHPG_DH, 'YYYYMMDDHH24MISS'),
    :V_SHPG_APT_DD,
    :V_REA_INF_WHS_TP,
    :V_REA_INF_RET_TP,
    TO_DATE(:V_PRD_RET_DH, 'YYYYMMDDHH24MISS'),
    :V_PRD_RET_APT_DD,
    :V_PRD_RET_PRG_STS_TP,
    :V_PRD_RET_REQ_PROC_CD,
    :V_PRD_RET_REQ_PRG_CD,
    :V_PRD_RET_REQ_CAU_CD,
    :V_SHPG_HLD_TP,
    TO_DATE(:V_SHPG_HLD_DD, 'YYYYMMDDHH24MISS'),
    :V_SHPG_HLD_CAU_TXT,
    TO_DATE(:V_SHPG_HLD_CNL_DD, 'YYYYMMDDHH24MISS'),
    :V_PRD_LONG_STK_CAU_CD,
    TO_DATE(:V_PRD_LONG_STK_RGS_DH, 'YYYYMMDDHH24MISS'),
    :V_REA_INF_CRYN_TP,
    :V_PRD_CRYN_CAU_TP,
    TO_DATE(:V_PRD_CRYN_DH, 'YYYYMMDDHH24MISS'),
    :V_PRD_CRYN_APT_DD,
    :V_DLV_PROG_STS_TP,
    :V_MS_PUB_YN,
    TO_DATE(:V_MS_PUB_DH, 'YYYYMMDDHH24MISS'),
    :V_PCOIL_NO,
    :V_TRF_REQ_NO,
    :V_TRF_PRG_STS_TP,
    TO_DATE(:V_TRF_WHS_DH, 'YYYYMMDDHH24MISS'),
    :V_PLNT_TP,
    :V_CREATED_OBJECT_TYPE,
    :V_CREATED_OBJECT_ID,
    :V_CREATED_PROGRAM_ID,
    TO_TIMESTAMP(:V_CREATION_TIMESTAMP, 'YYYYMMDDHH24MISSFF'),
    :V_LAST_UPDATED_OBJECT_TYPE,
    :V_LAST_UPDATED_OBJECT_ID,
    :V_LAST_UPDATE_PROGRAM_ID,
    TO_TIMESTAMP(:V_LAST_UPDATE_TIMESTAMP, 'YYYYMMDDHH24MISSFF'),
    :V_DATA_END_STATUS,
    :V_DATA_END_OBJECT_TYPE,
    :V_DATA_END_OBJECT_ID,
    :V_DATA_END_PROGRAM_ID,
    TO_TIMESTAMP(:V_DATA_END_TIMESTAMP, 'YYYYMMDDHH24MISSFF'),
    :V_ARCHIVE_COMPLETED_FLAG,
    :V_ARCHIVED_EMPLOYEE_NUM,
    TO_TIMESTAMP(:V_ARCHIVED_TIMESTAMP, 'YYYYMMDDHH24MISSFF'),
    :V_ARCHIVE_PROGRAM_ID,
    :V_CCL_CR_TRT_TP,
    :V_IN_PLT_CVT_CAU_CD,
    :V_CCL_2PASS_YN,
    :V_COIL_RMW_WGT_1,
    :V_COIL_RMW_WGT_2,
    :V_SAP_MTL_CD,
    :V_ITEMNAME_CD,
    :V_PDN_PLNT_TP,
    :V_PLN_PAS_PROC_CMN,
    :V_PAS_PLNT_ACT,
    :V_INV_NO,
    :V_LGS_BAS_CD,
    :V_WIP_MTL_CD,
    :V_MS_SPE_YN,
    :V_UNDER_COIL_YN,
    :V_UNDER_COIL_PLN_PAS,
    :V_UNDER_COIL_OBJECT_ID,
    :V_COR_CAU_TXT,
    :V_POC_TAR_MTL,
    :V_BLDP_TP
)

 </pre> */
public final static String insYfCrCoilComm = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insYfCrCoilComm";

 /** <pre> 
 --yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarArrWlocCd
update TB_YD_CARSCH A
set A.MODIFIER = 'yfbackup',
    A.MOD_DDTT=SYSDATE,
    A.YD_CARLD_ST_DT = SYSDATE,
    A.ARR_WLOC_CD = (SELECT ARR_WLOC_CD
                       FROM TB_PT_STLFRTOMOVE A
                      WHERE FRTOMOVE_STAT_CD = '3'
                        AND TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
                                                 FROM TB_PT_STLFRTOMOVE B
                                                WHERE A.STL_NO = B.STL_NO
                                                  AND ROWNUM=1)
                        AND STL_NO = :V_STL_NO)    
where TRN_EQP_CD = :V_TRN_EQP_CD
and YD_CAR_PROG_STAT <> '1'
and A.DEL_YN = 'N'
 </pre> */
public final static String updateCarArrWlocCd = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.updateCarArrWlocCd";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updStatStkBedActCA 
UPDATE TB_YF_STKBED
   SET MODIFIER            = :V_MODIFIER
      ,MOD_DDTT            = SYSDATE
      ,YD_STK_BED_ACTIVE_STAT = 'C'   --비활성화
 WHERE YD_STK_COL_GP  LIKE '1_TC'||SUBSTR(:V_YD_STK_COL_GP,5,2)
   AND DEL_YN              = 'N'
 </pre> */
public final static String updStatStkBedActCA = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updStatStkBedActCA";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteCarMtlInfo
UPDATE TB_YD_CARFTMVMTL
SET
    DEL_YN = 'Y'
WHERE 1=1
AND YD_CAR_SCH_ID =
(
    SELECT
        YD_CAR_SCH_ID
    FROM
        TB_YF_STKCOL A,
        TB_YD_CARSCH B
    WHERE 1=1
    AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
    AND A.TRN_EQP_CD = B.TRN_EQP_CD
    AND B.DEL_YN = 'N'
)
AND STL_NO = :V_STL_NO
 </pre> */
public final static String deleteCarMtlInfo = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteCarMtlInfo";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.setPageHelpInfo
-- 박판열연 YF 화면 도움말 등록
 MERGE INTO USRYFA.TB_YF_HELP A  -- YF_도움말
 USING (SELECT    :V_PAGE_ID     AS PAGE_ID      -- 화면ID   
                , :V_PAGE_PT     AS PAGE_PT      -- 화면개요 
                , :V_SCR_REMARK  AS SCR_REMARK   -- 특기사항 
                , :V_DEL_YN      AS DEL_YN       -- 삭제여부 
                , :V_MODIFIER    AS REGISTER     -- 등록자   
                , :V_MODIFIER    AS MODIFIER     -- 수정자   
          FROM DUAL
       ) B
    ON (    A.PAGE_ID = TO_NUMBER(B.PAGE_ID)   )
WHEN NOT MATCHED THEN 
     INSERT      
            (                                                                          
              A.PAGE_ID      -- 화면ID   
            , A.PAGE_PT      -- 화면개요 
            , A.SCR_REMARK   -- 특기사항 
            , A.DEL_YN       -- 삭제여부 
            , A.REGISTER     -- 등록자   
            , A.REG_DDTT     -- 등록 일시
            , A.MODIFIER     -- 수정자   
            , A.MOD_DDTT     -- 수정 일시
            )                                                                          
     VALUES (
              B.PAGE_ID      -- 화면ID   
            , B.PAGE_PT      -- 화면개요 
            , B.SCR_REMARK   -- 특기사항 
            , 'N'            -- 삭제여부 
            , B.REGISTER     -- 등록자   
            , SYSDATE        -- 등록 일시
            , B.MODIFIER     -- 수정자   
            , SYSDATE        -- 수정 일시
            )                                                                          
WHEN MATCHED THEN 
 UPDATE      
    SET   A.PAGE_PT      = B.PAGE_PT    -- 화면개요 
        , A.SCR_REMARK   = B.SCR_REMARK -- 특기사항 
        , A.DEL_YN       = B.DEL_YN     -- 삭제여부 
        , A.MODIFIER     = B.MODIFIER   -- 수정자   
        , A.MOD_DDTT     = SYSDATE      -- 수정 일시
  WHERE A.PAGE_ID        = TO_NUMBER(B.PAGE_ID)
 </pre> */
public final static String setPageHelpInfo = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.setPageHelpInfo";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.insHrShrMsgLog 
   열연정정작업메세지이력관리 등록 -
   com.inisteel.cim.hr.hrcomm.dao.HrCommDAO.insHrShrMsgLog 
INSERT INTO TB_HR_C_SHEARWOWR_MSG_LOG
SELECT CC.COIL_NO                                 
      ,SYSDATE                                 
      ,NVL(SR.STEP_NO, 1)
      ,NVL(SR.HR_PLNT_GP, CC.HR_PLNT_GP)
      ,'J' --:V_SHEAR_WRK_MSG_GP                     
      ,:V_MSG_CONTENTS                         
      ,:V_REGISTER                             
  FROM USRPTA.TB_PT_COILCOMM CC
     , USRHRA.TB_HR_C_SHEARWOWR SR
 WHERE CC.COIL_NO = SR.COIL_NO(+)
   AND CC.COIL_NO = :V_COIL_NO
   AND NVL(SR.STEP_NO, 0) = (SELECT NVL(MAX(X.STEP_NO),0)
                              FROM TB_HR_C_SHEARWOWR X
                             WHERE X.COIL_NO = :V_COIL_NO)
 </pre> */
public final static String insHrShrMsgLog = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insHrShrMsgLog";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateMatlFtmvTimeSlab
UPDATE TB_PT_SLABCOMM  
   set MATL_FTMV_DT = sysdate
WHERE SLAB_NO = :V_SLAB_NO
 </pre> */
public final static String updateMatlFtmvTimeSlab = "bak.yf.facilitywork.putwrecord.session.updateMatlFtmvTimeSlab";

 /** <pre> 
--yf.common.dao.updateRelayOfStock
UPDATE  TB_YF_STOCK A
      SET CTS_RELAY_SADDLE = :V_CTS_RELAY_SADDLE
           , MODIFIER='CTS'
           , MOD_DDTT=sysdate 
 WHERE   STL_NO = :V_STL_NO
 </pre> */
public final static String updateRelayOfStock = "bak.yf.common.dao.updateRelayOfStock";

 /** <pre> 
-- yf.common.YfCommonDao.updateStkLayer 
MERGE 
 INTO TB_YF_STKLYR A
USING (
       SELECT :V_MODIFIER                  AS MODIFIER                 
             ,sysdate                      AS MOD_DDTT                 
             ,:V_DEL_YN                    AS DEL_YN                   
             ,:V_STL_NO                    AS STL_NO                   
             ,:V_YD_STK_LYR_ACTIVE_STAT    AS YD_STK_LYR_ACTIVE_STAT   
             ,:V_YD_STK_LYR_STAT           AS YD_STK_LYR_STAT          
             ,:V_YD_STK_LYR_X_AXIS         AS YD_STK_LYR_X_AXIS        
             ,:V_YD_STK_LYR_Y_AXIS         AS YD_STK_LYR_Y_AXIS        
             ,:V_YD_STK_LYR_Z_AXIS         AS YD_STK_LYR_Z_AXIS        
             ,:V_YD_STK_LYR_2ND_OUTDIA_MIN AS YD_STK_LYR_2ND_OUTDIA_MIN
             ,:V_YD_STK_LYR_2ND_OUTDIA_MAX AS YD_STK_LYR_2ND_OUTDIA_MAX
             ,:V_YD_STK_LYR_2ND_WT_MIN     AS YD_STK_LYR_2ND_WT_MIN    
             ,:V_YD_STK_LYR_2ND_WT_MAX     AS YD_STK_LYR_2ND_WT_MAX    
             ,:V_YD_STK_LYR_2ND_W_MIN      AS YD_STK_LYR_2ND_W_MIN     
             ,:V_YD_STK_LYR_2ND_W_MAX      AS YD_STK_LYR_2ND_W_MAX     
             ,:V_YD_STK_LYR_COOL_DDTT_MIN  AS YD_STK_LYR_COOL_DDTT_MIN 
             ,:V_YD_STK_LYR_COOL_DDTT_MAX  AS YD_STK_LYR_COOL_DDTT_MAX 
             ,:V_YD_STK_LYR_TEMP_MIN       AS YD_STK_LYR_TEMP_MIN      
             ,:V_YD_STK_LYR_TEMP_MAX       AS YD_STK_LYR_TEMP_MAX      
             ,:V_YD_STK_LYR_YD_STK_LOT_NO1 AS YD_STK_LYR_YD_STK_LOT_NO1
             ,:V_YD_STK_LYR_YD_STK_LOT_NO2 AS YD_STK_LYR_YD_STK_LOT_NO2
             ,:V_CHARGE_SUPPLY_LEAD_HR     AS CHARGE_SUPPLY_LEAD_HR    
             ,:V_YD_STK_LYR_COMMENTS       AS YD_STK_LYR_COMMENTS  
			 ,:V_YD_STK_COL_GP 			   AS YD_STK_COL_GP
             ,:V_YD_STK_BED_NO     	       AS YD_STK_BED_NO    
             ,:V_YD_STK_LYR_NO             AS YD_STK_LYR_NO			 
		 FROM DUAL
		) B
  ON (    A.YD_STK_COL_GP LIKE B.YD_STK_COL_GP
      AND A.YD_STK_BED_NO LIKE B.YD_STK_BED_NO
	  AND A.YD_STK_LYR_NO LIKE B.YD_STK_LYR_NO )
WHEN MATCHED THEN
	UPDATE 
	   SET A.MODIFIER                  = DECODE(NVL(B.MODIFIER                 ,A.MODIFIER                 ),'-','',B.MODIFIER                 )
          ,A.MOD_DDTT                  = DECODE(NVL(B.MOD_DDTT                 ,A.MOD_DDTT                 ),'-','',B.MOD_DDTT                 )
          ,A.DEL_YN                    = DECODE(NVL(B.DEL_YN                   ,A.DEL_YN                   ),'-','',B.DEL_YN                   )
          ,A.STL_NO                    = DECODE(NVL(B.STL_NO                   ,A.STL_NO                   ),'-','',B.STL_NO                   )
          ,A.YD_STK_LYR_ACTIVE_STAT    = DECODE(NVL(B.YD_STK_LYR_ACTIVE_STAT   ,A.YD_STK_LYR_ACTIVE_STAT   ),'-','',B.YD_STK_LYR_ACTIVE_STAT   )
          ,A.YD_STK_LYR_STAT           = DECODE(NVL(B.YD_STK_LYR_STAT          ,A.YD_STK_LYR_STAT          ),'-','',B.YD_STK_LYR_STAT          )
          ,A.YD_STK_LYR_X_AXIS         = DECODE(NVL(B.YD_STK_LYR_X_AXIS        ,A.YD_STK_LYR_X_AXIS        ),'-','',B.YD_STK_LYR_X_AXIS        )
          ,A.YD_STK_LYR_Y_AXIS         = DECODE(NVL(B.YD_STK_LYR_Y_AXIS        ,A.YD_STK_LYR_Y_AXIS        ),'-','',B.YD_STK_LYR_Y_AXIS        )
          ,A.YD_STK_LYR_Z_AXIS         = DECODE(NVL(B.YD_STK_LYR_Z_AXIS        ,A.YD_STK_LYR_Z_AXIS        ),'-','',B.YD_STK_LYR_Z_AXIS        )
          ,A.YD_STK_LYR_2ND_OUTDIA_MIN = DECODE(NVL(B.YD_STK_LYR_2ND_OUTDIA_MIN,A.YD_STK_LYR_2ND_OUTDIA_MIN),'-','',B.YD_STK_LYR_2ND_OUTDIA_MIN)
          ,A.YD_STK_LYR_2ND_OUTDIA_MAX = DECODE(NVL(B.YD_STK_LYR_2ND_OUTDIA_MAX,A.YD_STK_LYR_2ND_OUTDIA_MAX),'-','',B.YD_STK_LYR_2ND_OUTDIA_MAX)
          ,A.YD_STK_LYR_2ND_WT_MIN     = DECODE(NVL(B.YD_STK_LYR_2ND_WT_MIN    ,A.YD_STK_LYR_2ND_WT_MIN    ),'-','',B.YD_STK_LYR_2ND_WT_MIN    )
          ,A.YD_STK_LYR_2ND_WT_MAX     = DECODE(NVL(B.YD_STK_LYR_2ND_WT_MAX    ,A.YD_STK_LYR_2ND_WT_MAX    ),'-','',B.YD_STK_LYR_2ND_WT_MAX    )
          ,A.YD_STK_LYR_2ND_W_MIN      = DECODE(NVL(B.YD_STK_LYR_2ND_W_MIN     ,A.YD_STK_LYR_2ND_W_MIN     ),'-','',B.YD_STK_LYR_2ND_W_MIN     )
          ,A.YD_STK_LYR_2ND_W_MAX      = DECODE(NVL(B.YD_STK_LYR_2ND_W_MAX     ,A.YD_STK_LYR_2ND_W_MAX     ),'-','',B.YD_STK_LYR_2ND_W_MAX     )
          ,A.YD_STK_LYR_COOL_DDTT_MIN  = DECODE(NVL(B.YD_STK_LYR_COOL_DDTT_MIN ,A.YD_STK_LYR_COOL_DDTT_MIN ),'-','',B.YD_STK_LYR_COOL_DDTT_MIN )
          ,A.YD_STK_LYR_COOL_DDTT_MAX  = DECODE(NVL(B.YD_STK_LYR_COOL_DDTT_MAX ,A.YD_STK_LYR_COOL_DDTT_MAX ),'-','',B.YD_STK_LYR_COOL_DDTT_MAX )
          ,A.YD_STK_LYR_TEMP_MIN       = DECODE(NVL(B.YD_STK_LYR_TEMP_MIN      ,A.YD_STK_LYR_TEMP_MIN      ),'-','',B.YD_STK_LYR_TEMP_MIN      )
          ,A.YD_STK_LYR_TEMP_MAX       = DECODE(NVL(B.YD_STK_LYR_TEMP_MAX      ,A.YD_STK_LYR_TEMP_MAX      ),'-','',B.YD_STK_LYR_TEMP_MAX      )
          ,A.YD_STK_LYR_YD_STK_LOT_NO1 = DECODE(NVL(B.YD_STK_LYR_YD_STK_LOT_NO1,A.YD_STK_LYR_YD_STK_LOT_NO1),'-','',B.YD_STK_LYR_YD_STK_LOT_NO1)
          ,A.YD_STK_LYR_YD_STK_LOT_NO2 = DECODE(NVL(B.YD_STK_LYR_YD_STK_LOT_NO2,A.YD_STK_LYR_YD_STK_LOT_NO2),'-','',B.YD_STK_LYR_YD_STK_LOT_NO2)
          ,A.CHARGE_SUPPLY_LEAD_HR     = DECODE(NVL(B.CHARGE_SUPPLY_LEAD_HR    ,A.CHARGE_SUPPLY_LEAD_HR    ),'-','',B.CHARGE_SUPPLY_LEAD_HR    )
          ,A.YD_STK_LYR_COMMENTS       = DECODE(NVL(B.YD_STK_LYR_COMMENTS      ,A.YD_STK_LYR_COMMENTS      ),'-','',B.YD_STK_LYR_COMMENTS      )
 </pre> */
public final static String updateStkLayer = "bak.yf.common.YfCommonDao.updateStkLayer";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCarpoint
UPDATE TB_YD_CARPOINT
   SET YD_STK_COL_ACT_STAT=:V_YD_STK_COL_ACT_STAT
     , TRN_EQP_CD =:V_TRN_EQP_CD
     , CAR_NO =:V_CAR_NO
     , CARD_NO =:V_CARD_NO
     , MOD_DDTT=SYSDATE
     , MODIFIER='포인트변경'
 WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
 </pre> */
public final static String updYdCarpoint = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCarpoint";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonSubInfo
UPDATE TB_PT_MSLABCOMMSUB A
 SET BK_STK_YN='Y'
   , BK_STK_START_TIME=nvl(BK_STK_START_TIME,SYSDATE)
   , CCSLAB_CL_MTD_GP =(CASE WHEN CCSLAB_CL_MTD_GP IN ('G', 'H', 'I', 'J','N') THEN 'F' 
                        ELSE CCSLAB_CL_MTD_GP
                        END)
WHERE MSLAB_NO =:V_SLAB_NO
 </pre> */
public final static String updateMslabCommonSubInfo = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonSubInfo";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYdStkBedQtyInfo 
UPDATE TB_YF_STKBED
   SET(
       YD_STK_BED_QNTY_CURR,
       YD_STK_BED_ABLE_QNTY,
       MODIFIER,
       MOD_DDTT
      )= 
        (
         SELECT 
                CASE WHEN TO_NUMBER(NVL(YD_STK_BED_QNTY_CURR,0) + :V_QTY) < 0
                     THEN 0 ELSE TO_NUMBER(NVL(YD_STK_BED_QNTY_CURR,0) + :V_QTY)
                END AS CUR_QNT,-- 적치BED수량현재
                CASE WHEN TO_NUMBER(NVL(YD_STK_BED_ABLE_QNTY,0) + (:V_QTY*-1)) < 0
                     THEN 0 ELSE TO_NUMBER(NVL(YD_STK_BED_ABLE_QNTY,0) + (:V_QTY*-1))
                END AS ABLE_QNT,-- 적치BED가능수량
                'SYSTEM',
                SYSDATE     
           FROM TB_YF_STKBED
          WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
            AND YD_STK_BED_NO = :V_YD_STK_BED_NO
        )
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
 </pre> */
public final static String updateYdStkBedQtyInfo = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYdStkBedQtyInfo";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.insTcarFtmvMtl  
INSERT INTO TB_YF_TCARFTMVMTL(
      YD_TCAR_SCH_ID
    , STL_NO
    , REGISTER
    , REG_DDTT
    , MODIFIER
    , MOD_DDTT
    , DEL_YN
    , YD_STK_BED_NO
    , YD_STK_LYR_NO
    , HCR_GP
    , STL_PROG_CD
    , YD_MTL_ITEM
    , YD_ROUTE_GP
) 
   SELECT 
          (SELECT YD_TCAR_SCH_ID 
             FROM TB_YF_TCARSCH
            WHERE DEL_YN = 'N'
              AND YD_EQP_ID = :V_YD_EQP_ID
          ) AS YD_TCAR_SCH_ID
        , ST.STL_NO
        , :V_MODIFIER
        , SYSDATE
        , :V_MODIFIER
        , SYSDATE
        , 'N'
        , :V_YD_STK_BED_NO
        , '01'
        , CC.HCR_GP
        , CC.CURR_PROG_CD  AS STL_PROG_CD 
        , ST.STOCK_ITEM    AS YD_MTL_ITEM
        , ''
     FROM TB_YF_STOCK      ST
        , TB_PT_COILCOMM   CC
    WHERE ST.STL_NO = CC.COIL_NO
      AND ST.STL_NO = :V_STL_NO
 </pre> */
public final static String insTcarFtmvMtl = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.insTcarFtmvMtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCTSSch
INSERT INTO TB_YF_CTS_SCH
(
 YD_CTS_SCH_ID
,REGISTER
,REG_DDTT
,MODIFIER
,MOD_DDTT
,DEL_YN
,YD_EQP_ID
,YD_CTS_WRK_SEQ
,STL_NO
,YD_WRK_PROG_STAT
,YD_WBOOK_ID
,YD_AIM_BAY_GP
,YD_CTS_RELAY_YN
,YD_CTS_RELAY_BAY_GP
,YD_CARLD_WO_LOC --야드상차지시위치
--,YD_CARUD_WO_LOC --야드하차지시위치
--,YD_WORD_DT
)
(
    SELECT     
     :V_YD_CTS_SCH_ID
    ,:V_MODIFIER
    ,SYSDATE
    ,:V_MODIFIER
    ,SYSDATE 
    ,'N'
    ,YD_EQP_ID
    ,NVL(EMER_SEQ,MOVE_SEQ) AS YD_CTS_WRK_SEQ
    ,:V_STL_NO
    ,'W' --YD_WRK_PROG_STAT
    ,:V_YD_WBOOK_ID
    ,YD_AIM_BAY_GP
    ,DECODE(YD_CTS_RELAY_BAY_GP,'X','N','Y') AS YD_CTS_RELAY_YN --기준과 관계없이 중계국 사용 유무 판단한 결과 
    ,YD_CTS_RELAY_BAY_GP
    ,YD_CARLD_WO_LOC --상차지시위치
   
    FROM
    (
        SELECT 
               MAX(CASE WHEN B.YD_CTS_RELAY_YN  = 'N' AND A.REPR_CD_GP = 'PRI001'                            THEN A.DTL_ITEM3 --중계국 없을때 
                        WHEN B.YD_CTS_RELAY_YN != 'N' AND B.YD_EQP_ID = '1XTC01' AND A.REPR_CD_GP = 'PRI002' THEN A.DTL_ITEM3 --중계국 있고, 1번 대차일때
                        WHEN B.YD_CTS_RELAY_YN != 'N' AND B.YD_EQP_ID = '1XTC02' AND A.REPR_CD_GP = 'PRI003' THEN A.DTL_ITEM3 --중계국 있고, 2번 대차일때
                        END) AS MOVE_SEQ
              ,MAX(CASE WHEN B.YD_CTS_RELAY_YN  = 'N' AND A.REPR_CD_GP = 'PRI004'                            THEN A.DTL_ITEM3 --중계국 없을때 
                        WHEN B.YD_CTS_RELAY_YN != 'N' AND B.YD_EQP_ID = '1XTC01' AND A.REPR_CD_GP = 'PRI005' THEN A.DTL_ITEM3 --중계국 있고, 1번 대차일때
                        WHEN B.YD_CTS_RELAY_YN != 'N' AND B.YD_EQP_ID = '1XTC02' AND A.REPR_CD_GP = 'PRI006' THEN A.DTL_ITEM3 --중계국 있고, 2번 대차일때
                        END) AS EMER_SEQ
                    
               ,MAX(B.YD_CTS_RELAY_BAY_GP)  AS YD_CTS_RELAY_BAY_GP --중계국
               ,MAX(B.YD_EQP_ID)            AS YD_EQP_ID 
               ,MAX(B.YD_CARLD_WO_LOC)      AS YD_CARLD_WO_LOC
               ,MAX(B.YD_AIM_BAY_GP)        AS YD_AIM_BAY_GP
        FROM TB_YF_RULE A
            ,(SELECT PARAM.YD_CARLD_WO_LOC
                    ,B.YD_AIM_BAY_GP
                    ,DECODE(C.DTL_ITEM1,'X','N','Y') AS YD_CTS_RELAY_YN --기준에 SETTING 된 중계 사용 유무
                    
                    ,CASE WHEN C.DTL_ITEM1 = 'X' THEN 'X' --중계국 사용 안함
                          WHEN (
                                (PARAM.YD_CURR_BAY_GP < C.DTL_ITEM1 AND C.DTL_ITEM1 < B.YD_AIM_BAY_GP) --현재동<중계동<목적동
                                OR
                                (PARAM.YD_CURR_BAY_GP > C.DTL_ITEM1 AND C.DTL_ITEM1 > B.YD_AIM_BAY_GP) --현재동>중계동>목적동
                               )
                          THEN C.DTL_ITEM1 --중계국이 필요한 경우
                          ELSE 'X' --중계국 사용할 필요가 없는 경우
                          END AS YD_CTS_RELAY_BAY_GP --중계국     
                         
                    ,CASE WHEN C.DTL_ITEM1 = 'X'                  THEN (SELECT MIN(YD_EQP_ID) FROM TB_YF_EQP WHERE YD_EQP_ID IN ('1XTC01','1XTC02') AND YD_EQP_PROG_STAT !='B') --중계국 없으면 1번 대차만 사용
                          WHEN C.DTL_ITEM1 > PARAM.YD_CURR_BAY_GP THEN '1XTC01' --상차동이 중계국보다 작으면 1번 대차
                          WHEN C.DTL_ITEM1 < PARAM.YD_CURR_BAY_GP THEN '1XTC02' --상차동이 중계국보다 크면   2번 대차
                          WHEN C.DTL_ITEM1 = PARAM.YD_CURR_BAY_GP AND C.DTL_ITEM1 > B.YD_AIM_BAY_GP THEN '1XTC01' 
                          WHEN C.DTL_ITEM1 = PARAM.YD_CURR_BAY_GP AND C.DTL_ITEM1 < B.YD_AIM_BAY_GP THEN '1XTC02' 
                          END AS YD_EQP_ID
                          
                FROM TB_YF_WRKBOOK B
                    ,TB_YF_RULE C   --중계국 기준
                    ,(SELECT SUBSTR(:V_YD_CARLD_WO_LOC,1,6) AS YD_CARLD_WO_LOC 
                            ,SUBSTR(:V_YD_CARLD_WO_LOC,2,1) AS YD_CURR_BAY_GP
                             FROM DUAL) PARAM
               WHERE B.YD_WBOOK_ID   = :V_YD_WBOOK_ID
                 AND C.REPR_CD_GP = 'PRI007'
             ) B
        WHERE A.DTL_ITEM1 = SUBSTR(B.YD_CARLD_WO_LOC,2,1)
          AND A.DTL_ITEM2 = DECODE(B.YD_CTS_RELAY_BAY_GP,'X',B.YD_AIM_BAY_GP,B.YD_CTS_RELAY_BAY_GP)
          AND A.REPR_CD_GP LIKE 'PRI%'
    )
)
 </pre> */
public final static String insCTSSch = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCTSSch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updBDCoilInfo
MERGE INTO TB_YF_RULE R USING (
SELECT :V_STL_NO   AS STL_NO
     , :V_DTL_ITEM1 AS DTL_ITEM1
     , :V_DTL_ITEM2 AS DTL_ITEM2
     , :V_DTL_ITEM3 AS DTL_ITEM3
     , :V_DTL_ITEM4 AS DTL_ITEM4
     , :V_DTL_ITEM5 AS DTL_ITEM5
     , 'BDCOIL'    AS REPR_CD_GP
     , '1'         AS CD_GP
  FROM DUAL
 ) DD ON (R.ITEM = DD.STL_NO AND R.REPR_CD_GP = DD.REPR_CD_GP AND R.CD_GP = DD.CD_GP)
 WHEN NOT MATCHED THEN
    INSERT (
             REPR_CD_GP   , CD_GP        , ITEM         , REPR_CD_CONTENTS
           , DTL_ITEM1     , DTL_ITEM2     , DTL_ITEM3     , DTL_ITEM4         , DTL_ITEM5)
    VALUES ( DD.REPR_CD_GP, DD.CD_GP     , DD.STL_NO  , '분동코일'
           , DD.DTL_ITEM1  , DD.DTL_ITEM2  , DD.DTL_ITEM3  , DD.DTL_ITEM4      , DD.DTL_ITEM5)
 WHEN MATCHED THEN UPDATE SET
      R.MODIFIER = 'F1YFL027'
    , R.MOD_DDTT = SYSDATE
    , R.DTL_ITEM1 = DD.DTL_ITEM1
    , R.DTL_ITEM2 = DD.DTL_ITEM2
    , R.DTL_ITEM3 = CASE WHEN LENGTH(DD.DTL_ITEM3) = 5 THEN SUBSTR(DD.DTL_ITEM3,1,4) ||'.'||SUBSTR(DD.DTL_ITEM3,5,1) ELSE DD.DTL_ITEM3 END
    , R.DTL_ITEM4 = DD.DTL_ITEM4
    , R.DTL_ITEM5 = DD.DTL_ITEM5
 </pre> */
public final static String updBDCoilInfo = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updBDCoilInfo";

 /** <pre> 
-- yf.common.YfCommonDao.updateStock 
MERGE 
 INTO TB_YF_STOCK A
USING (
       SELECT :V_MODIFIER                  AS MODIFIER                 
             ,sysdate                      AS MOD_DDTT                 
             ,:V_DEL_YN                    AS DEL_YN                   
             ,:V_YD_MTL_STAT		       AS YD_MTL_STAT
			 ,:V_EXPECT_YD_STK_LOC		   AS EXPECT_YD_STK_LOC
			 ,:V_STOCK_ITEM		           AS STOCK_ITEM
			 ,:V_STOCK_MOVE_TERM		   AS STOCK_MOVE_TERM
			 ,:V_CAR_CARD_NO		       AS CAR_CARD_NO
			 ,:V_CAR_NO		               AS CAR_NO
			 ,:V_TRANS_ORD_DATE		       AS TRANS_ORD_DATE
			 ,:V_TRANS_ORD_SEQNO		   AS TRANS_ORD_SEQNO
			 ,:V_TRN_EQP_CD		           AS TRN_EQP_CD
			 ,:V_LOAD_LOC_CD		       AS LOAD_LOC_CD
			 ,:V_WGT_CENTER_XAXIS		   AS WGT_CENTER_XAXIS
			 ,:V_WGT_CENTER_YAXIS		   AS WGT_CENTER_YAXIS
			 ,:V_WGT_CENTER_ZAXIS		   AS WGT_CENTER_ZAXIS
			 ,:V_YD_STK_COL_DIR_GP		   AS YD_STK_COL_DIR_GP
			 ,:V_YD_STK_COL_DEG		       AS YD_STK_COL_DEG
			 ,:V_CAU_CD		               AS CAU_CD
			 ,:V_CTS_RELAY_YN		       AS CTS_RELAY_YN
			 ,:V_CTS_RELAY_BAY		       AS CTS_RELAY_BAY
			 ,:V_CTS_RELAY_SADDLE		   AS CTS_RELAY_SADDLE
			 ,:V_YD_CAR_UPP_LOC_CD		   AS YD_CAR_UPP_LOC_CD
			 ,:V_YD_RULE_PL_RS_GP		   AS YD_RULE_PL_RS_GP
			 ,:V_SNBK_WT		           AS SNBK_WT
			 ,:V_KEEP_STL_YN		       AS KEEP_STL_YN
			 ,:V_YD_ZONE_GP		           AS YD_ZONE_GP
			 ,:V_CHARGE_LOT_NO		       AS CHARGE_LOT_NO
			 ,:V_FRTOMOVE_WORD_NO		   AS FRTOMOVE_WORD_NO
			 ,:V_STL_NO					   AS STL_NO
		 FROM DUAL
		) B
  ON (    A.STL_NO LIKE B.STL_NO  )
WHEN MATCHED THEN
	UPDATE 
	   SET A.MODIFIER          	= DECODE(NVL(B.MODIFIER         	,A.MODIFIER         ),'-','',B.MODIFIER         )
          ,A.MOD_DDTT         	= DECODE(NVL(B.MOD_DDTT         	,A.MOD_DDTT         ),'-','',B.MOD_DDTT         )
          ,A.DEL_YN            	= DECODE(NVL(B.DEL_YN           	,A.DEL_YN           ),'-','',B.DEL_YN           )
          ,A.YD_MTL_STAT 		= DECODE(NVL(B.YD_MTL_STAT 			,A.YD_MTL_STAT 		),'-','',B.YD_MTL_STAT 		)
          ,A.EXPECT_YD_STK_LOC 	= DECODE(NVL(B.EXPECT_YD_STK_LOC	,A.EXPECT_YD_STK_LOC),'-','',B.EXPECT_YD_STK_LOC)
          ,A.STOCK_ITEM 		= DECODE(NVL(B.STOCK_ITEM 			,A.STOCK_ITEM 		),'-','',B.STOCK_ITEM 		)
          ,A.STOCK_MOVE_TERM 	= DECODE(NVL(B.STOCK_MOVE_TERM 		,A.STOCK_MOVE_TERM 	),'-','',B.STOCK_MOVE_TERM 	)
          ,A.CAR_CARD_NO 		= DECODE(NVL(B.CAR_CARD_NO 			,A.CAR_CARD_NO 		),'-','',B.CAR_CARD_NO 		)
          ,A.CAR_NO 			= DECODE(NVL(B.CAR_NO 				,A.CAR_NO 			),'-','',B.CAR_NO 			)
          ,A.TRANS_ORD_DATE 	= DECODE(NVL(B.TRANS_ORD_DATE 		,A.TRANS_ORD_DATE 	),'-','',B.TRANS_ORD_DATE 	)
          ,A.TRANS_ORD_SEQNO 	= DECODE(NVL(B.TRANS_ORD_SEQNO 		,A.TRANS_ORD_SEQNO 	),'-','',B.TRANS_ORD_SEQNO 	)
          ,A.TRN_EQP_CD 		= DECODE(NVL(B.TRN_EQP_CD 			,A.TRN_EQP_CD 		),'-','',B.TRN_EQP_CD 		)
          ,A.LOAD_LOC_CD 		= DECODE(NVL(B.LOAD_LOC_CD 			,A.LOAD_LOC_CD 		),'-','',B.LOAD_LOC_CD 		)
          ,A.WGT_CENTER_XAXIS 	= DECODE(NVL(B.WGT_CENTER_XAXIS 	,A.WGT_CENTER_XAXIS ),'-','',B.WGT_CENTER_XAXIS )
          ,A.WGT_CENTER_YAXIS 	= DECODE(NVL(B.WGT_CENTER_YAXIS 	,A.WGT_CENTER_YAXIS ),'-','',B.WGT_CENTER_YAXIS )
          ,A.WGT_CENTER_ZAXIS 	= DECODE(NVL(B.WGT_CENTER_ZAXIS 	,A.WGT_CENTER_ZAXIS ),'-','',B.WGT_CENTER_ZAXIS )
          ,A.YD_STK_COL_DIR_GP 	= DECODE(NVL(B.YD_STK_COL_DIR_GP	,A.YD_STK_COL_DIR_GP),'-','',B.YD_STK_COL_DIR_GP)
          ,A.YD_STK_COL_DEG 	= DECODE(NVL(B.YD_STK_COL_DEG 		,A.YD_STK_COL_DEG 	),'-','',B.YD_STK_COL_DEG 	)
          ,A.CAU_CD 			= DECODE(NVL(B.CAU_CD 				,A.CAU_CD 			),'-','',B.CAU_CD 			)
          ,A.CTS_RELAY_YN 		= DECODE(NVL(B.CTS_RELAY_YN 		,A.CTS_RELAY_YN 	),'-','',B.CTS_RELAY_YN 	)
          ,A.CTS_RELAY_BAY 		= DECODE(NVL(B.CTS_RELAY_BAY 		,A.CTS_RELAY_BAY 	),'-','',B.CTS_RELAY_BAY 	)
          ,A.CTS_RELAY_SADDLE 	= DECODE(NVL(B.CTS_RELAY_SADDLE 	,A.CTS_RELAY_SADDLE ),'-','',B.CTS_RELAY_SADDLE )
          ,A.YD_CAR_UPP_LOC_CD 	= DECODE(NVL(B.YD_CAR_UPP_LOC_CD	,A.YD_CAR_UPP_LOC_CD),'-','',B.YD_CAR_UPP_LOC_CD)
          ,A.YD_RULE_PL_RS_GP   = DECODE(NVL(B.YD_RULE_PL_RS_GP     ,A.YD_RULE_PL_RS_GP ),'-','',B.YD_RULE_PL_RS_GP )
          ,A.SNBK_WT            = DECODE(NVL(B.SNBK_WT              ,A.SNBK_WT          ),'-','',B.SNBK_WT          )
          ,A.KEEP_STL_YN        = DECODE(NVL(B.KEEP_STL_YN          ,A.KEEP_STL_YN      ),'-','',B.KEEP_STL_YN      )
          ,A.YD_ZONE_GP         = DECODE(NVL(B.YD_ZONE_GP           ,A.YD_ZONE_GP       ),'-','',B.YD_ZONE_GP       )
          ,A.CHARGE_LOT_NO      = DECODE(NVL(B.CHARGE_LOT_NO        ,A.CHARGE_LOT_NO    ),'-','',B.CHARGE_LOT_NO    )
          ,A.FRTOMOVE_WORD_NO   = DECODE(NVL(B.FRTOMOVE_WORD_NO     ,A.FRTOMOVE_WORD_NO ),'-','',B.FRTOMOVE_WORD_NO )

 </pre> */
public final static String updateStock = "bak.yf.common.YfCommonDao.updateStock";

 /** <pre> 
--yf.tsinfo.updatecarLoadend
update TB_YD_CARSCH A
set A.MODIFIER = 'yfbackup',
    A.MOD_DDTT=SYSDATE,
    A.YD_EQP_WRK_STAT = 'L',
    A.YD_CAR_PROG_STAT = '5',
    A.ARR_WLOC_CD = NVL(A.ARR_WLOC_CD,(SELECT ARR_WLOC_CD
                                         FROM TB_PT_STLFRTOMOVE A
                                        WHERE FRTOMOVE_STAT_CD = '3'
                                          AND TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO)
                                                                   FROM TB_PT_STLFRTOMOVE B
                                                                  WHERE A.STL_NO = B.STL_NO
                                                                    AND ROWNUM=1)
                                          AND STL_NO IN (SELECT B.STL_NO
														   FROM ( SELECT MAX(A.YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
														  		  FROM TB_YD_CARSCH A
														  		 WHERE A.TRN_EQP_CD = :V_TRN_EQP_CD) A 
														  	    ,TB_YD_CARFTMVMTL B
														  WHERE A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
														    AND DEL_YN = 'N'
														    AND ROWNUM = 1)))
where TRN_EQP_CD = :V_TRN_EQP_CD
and YD_CAR_PROG_STAT <> '1'
and A.DEL_YN = 'N'
 </pre> */
public final static String updatecarLoadend = "bak.yf.tsinfo.updatecarLoadend";

 /** <pre> 
--yf.facilitywork.putwrecord.session.deleteCarMtrlEqualUpperReset
UPDATE TB_YD_CARFTMVMTL
   SET DEL_YN = 'Y'
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO >= :V_YD_STK_LYR_NO
   AND DEL_YN = 'N'
 </pre> */
public final static String deleteCarMtrlEqualUpperReset = "bak.yf.facilitywork.putwrecord.session.deleteCarMtrlEqualUpperReset";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYfStkColActiveStat  
UPDATE TB_YF_STKCOL
   SET YD_STK_COL_ACTIVE_STAT = :V_YD_STK_COL_ACTIVE_STAT
      ,MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 
 </pre> */
public final static String updYfStkColActiveStat = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYfStkColActiveStat";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStockItem
UPDATE TB_YF_STKCOL
   SET STOCK_ITEM = :V_STOCK_ITEM
      ,MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 
 </pre> */
public final static String updStockItem = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updStockItem";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStacklayer
MERGE INTO TB_YF_STKLYR SL USING
(
    SELECT
        A.YD_STK_COL_GP,
        A.YD_STK_BED_NO,
        A.YD_STK_LYR_NO,
        B.STL_NO
    FROM
        TB_YF_STKLYR A,
        TB_YD_CARFTMVMTL B
    WHERE 1=1
    AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
    AND B.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
    AND B.YD_STK_BED_NO = A.YD_STK_BED_NO
    AND A.YD_STK_LYR_NO = '01'
) DD
ON
(
    1=1
    AND SL.YD_STK_COL_GP = DD.YD_STK_COL_GP
    AND SL.YD_STK_BED_NO = DD.YD_STK_BED_NO
    AND SL.YD_STK_LYR_NO = DD.YD_STK_LYR_NO
)
WHEN MATCHED THEN
UPDATE SET
    SL.MOD_DDTT = SYSDATE,
    SL.MODIFIER = :V_MODIFIER,
    SL.STL_NO   = DD.STL_NO,
    SL.YD_STK_LYR_ACTIVE_STAT = 'E',
    SL.YD_STK_LYR_STAT = 'C'
 </pre> */
public final static String updYfStacklayer = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStacklayer";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateCarMtrlEqualUpperReset
UPDATE TB_YD_CARFTMVMTL
   SET DEL_YN = 'Y'
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO >= :V_YD_STK_LYR_NO
 </pre> */
public final static String updateCarMtrlEqualUpperReset = "bak.yf.facilitywork.putwrecord.session.updateCarMtrlEqualUpperReset";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updTSmatlftmvwoUgntCHK 
UPDATE TB_TS_MATL_FTMV_WO A
SET MTL_UGNT_GP = 'Y'
 , MODIFIER = :V_MODIFIER
 , MOD_DDTT = sysdate 
WHERE STL_NO = :V_STL_NO
  AND DEL_YN = 'N'
  AND MTL_UGNT_GP = 'N'
  AND TRANSWORD_SEQNO=(SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO B
                         WHERE A.STL_NO=B.STL_NO )
 </pre> */
public final static String updTSmatlftmvwoUgntCHK = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updTSmatlftmvwoUgntCHK";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updTSmatlftmvwoUgntgp 
UPDATE TB_TS_MATL_FTMV_WO A
SET MTL_UGNT_GP = 'P'
 , MODIFIER = :V_MODIFIER
 , MOD_DDTT = sysdate 
 where MATL_FTMV_WO_NML_HD_YN   IN('Y','X')    	
   and TS_MATL_FTMV_STAT_GP     = '1'
   and SPOS_WLOC_CD IN ('D2Y44','D2Y45')
   AND DEL_YN='N'
   AND MTL_UGNT_GP IN('P','Y')
   AND TRANSWORD_SEQNO=(SELECT MAX(TRANSWORD_SEQNO) FROM TB_TS_MATL_FTMV_WO B
                         WHERE A.STL_NO=B.STL_NO )
 </pre> */
public final static String updTSmatlftmvwoUgntgp = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updTSmatlftmvwoUgntgp";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updYdCarSchYdPntCd4
--하차위치변경(공차-상차)
UPDATE TB_YD_CARSCH
SET
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE,
    YD_PNT_CD1          = :V_YD_PNT_CD,
    YD_CARLD_STOP_LOC   = :V_TO_LOC
WHERE 1=1
AND YD_CAR_SCH_ID =
(
    SELECT
        MAX(YD_CAR_SCH_ID) AS YD_CAR_SCH_ID
    FROM
        USRYDA.TB_YD_CARSCH
    WHERE 1=1
    AND YD_CARLD_STOP_LOC   = :V_FROM_LOC
    AND DEL_YN              = 'N'
    AND TRN_EQP_CD          = :V_TRN_EQP_CD
)
 </pre> */
public final static String updYdCarSchYdPntCd4 = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updYdCarSchYdPntCd4";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updWrkBookAimBayGp

UPDATE USRYFA.TB_YF_WRKBOOK
SET REG_DDTT            = SYSDATE
   ,MODIFIER            = :V_MODIFIER
   ,YD_AIM_BAY_GP       = :V_YD_AIM_BAY_GP
   ,YD_TO_LOC_GUIDE     = '1'||:V_YD_AIM_BAY_GP
   ,YD_WRK_PLAN_TCAR    = :V_YD_WRK_PLAN_TCAR
WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String updWrkBookAimBayGp = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updWrkBookAimBayGp";

 /** <pre> 
--yf.facilitywork.putwrecord.session.CarItemDelYnToY
UPDATE TB_YD_CARFTMVMTL
   SET DEL_YN = 'Y'
 WHERE DEL_YN = 'N'
   AND YD_CAR_SCH_ID IN (SELECT YD_CAR_SCH_ID
                          FROM TB_YD_CARSCH A
                              ,TB_YD_CARPOINT B
                         WHERE B.YD_STK_COL_GP = :V_YD_STK_COL_GP
                           AND B.TRN_EQP_CD = A.TRN_EQP_CD
                           AND A.DEL_YN = 'N')
 </pre> */
public final static String CarItemDelYnToY = "bak.yf.facilitywork.putwrecord.session.CarItemDelYnToY";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCoilYdStkPosSetBed.updYdStklyrTol2
UPDATE TB_YF_STKBED
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , YD_STK_BED_XAXIS_TOL  = :V_YD_STK_BED_XAXIS_TOL
     , YD_STK_BED_YAXIS_TOL  = :V_YD_STK_BED_YAXIS_TOL
     , YD_STK_BED_ZAXIS_TOL  = NVL(:V_YD_STK_BED_ZAXIS_TOL,YD_STK_BED_ZAXIS_TOL)
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
 </pre> */
public final static String updYdStklyrTol2 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCoilYdStkPosSetBed.updYdStklyrTol2";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCRANETRAVLPROH
MERGE INTO TB_YF_CRANE_TRAVL_PROH USING DUAL ON ( 
                         YM_CRANE_TRAVL_PROH_SEQ = :V_YM_CRANE_TRAVL_PROH_SEQ
        AND YD_GP = :V_YD_GP
        AND BAY_GP  = :V_BAY_GP
	    AND TRAVL_PROH_FROMXAXIS  = :V_TRAVL_PROH_FROMXAXIS
		AND TRAVL_PROH_TOXAXIS = :V_TRAVL_PROH_TOXAXIS)
	  WHEN MATCHED THEN
    UPDATE SET DEL_YN = :V_DEL_YN,
                        MODIFIER = :V_MODIFIER,
	         MOD_DDTT = SYSDATE                 
	  WHEN NOT MATCHED THEN
	INSERT(YM_CRANE_TRAVL_PROH_SEQ,
	       YD_GP,
	       BAY_GP,
	       TRAVL_PROH_FROMLOC,
	       TRAVL_PROH_TOLOC,
	       TRAVL_PROH_FROMXAXIS,
	       TRAVL_PROH_TOXAXIS,
	       DEL_YN,
	       REGISTER,
	       REG_DDTT)
	VALUES((SELECT nvl(MAX(to_number(YM_CRANE_TRAVL_PROH_SEQ)),0)+1 FROM TB_YF_CRANE_TRAVL_PROH),
		   :V_YD_GP,
		   :V_BAY_GP,
		   :V_TRAVL_PROH_FROMLOC,
		   :V_TRAVL_PROH_TOLOC,
		   :V_TRAVL_PROH_FROMXAXIS,
		   :V_TRAVL_PROH_TOXAXIS,
		    :V_DEL_YN,
		   :V_REGISTER,
		   SYSDATE)
 </pre> */
public final static String updCRANETRAVLPROH = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCRANETRAVLPROH";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YfCommonDAO.deleteLocSearchInfo
UPDATE TB_YF_SCHLOCSRCH
SET
    DEL_YN = 'Y'
WHERE 1=1
AND
(
    YD_SCH_CD,
    YD_STK_COL_GP,
    YD_ROUTE_GP
) IN
(
    SELECT
        B.YD_SCH_CD,
        B.YD_STK_COL_GP,
        B.YD_ROUTE_GP
    FROM
        TB_YF_STKCOL A,
        TB_YF_SCHLOCSRCH B
    WHERE 1=1
    AND A.YD_STK_COL_GP         = :V_YD_STK_COL_GP
    AND B.YD_STK_COL_GP         = A.YD_STK_COL_GP
    AND A.YD_STK_COL_USAGE_CD   IN ('TS','FS')
)
 </pre> */
public final static String deleteLocSearchInfo = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.deleteLocSearchInfo";

 /** <pre> 
    --yf.common.dao.updateStockStatOfLayer1
	UPDATE TB_YF_STKLYR
	   SET STL_NO = :V_STL_NO
	      ,YD_STK_LYR_STAT = :V_YD_STK_LYR_STAT
	 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String updateStockStatOfLayer1 = "bak.yf.common.dao.updateStockStatOfLayer1";

 /** <pre> 
--com.inisteel.cim.yf.common.dao.YfCommDAO.updYfCrCoilComm
UPDATE TB_YF_CR_COILCOMM
SET
    RCD_STS_TP                  = :V_RCD_STS_TP,
    COIL_INF_CRT_TP             = :V_COIL_INF_CRT_TP,
    COIL_INF_END_TP             = :V_COIL_INF_END_TP,
    WK_PRG_STS                  = :V_WK_PRG_STS,
    PRG_CD                      = :V_PRG_CD,
    LN_TP                       = :V_LN_TP,
    BF_PRG_CD                   = :V_BF_PRG_CD,
    BF_LN_TP                    = :V_BF_LN_TP,
    COIL_ACT_THK                = :V_COIL_ACT_THK,
    COIL_ACT_THK_MNV            = :V_COIL_ACT_THK_MNV,
    COIL_ACT_THK_MXV            = :V_COIL_ACT_THK_MXV,
    COIL_ACT_WTH                = :V_COIL_ACT_WTH,
    COIL_ACT_WTH_MNV            = :V_COIL_ACT_WTH_MNV,
    COIL_ACT_WTH_MXV            = :V_COIL_ACT_WTH_MXV,
    COIL_ACT_LTH                = :V_COIL_ACT_LTH,
    COIL_RMW_WGT                = :V_COIL_RMW_WGT,
    COIL_THY_WGT                = :V_COIL_THY_WGT,
    COIL_GRS_RMW_WGT            = :V_COIL_GRS_RMW_WGT,
    COIL_GRS_THY_WGT            = :V_COIL_GRS_THY_WGT,
    COIL_IDIA                   = :V_COIL_IDIA,
    COIL_ODIA                   = :V_COIL_ODIA,
    HC_AW                       = :V_HC_AW,
    CC_AW                       = :V_CC_AW,
    REM_PROC                    = :V_REM_PROC,
    NXT_PROC_CD                 = :V_NXT_PROC_CD,
    BF_PROC_CD                  = :V_BF_PROC_CD,
    REQ_NXT_PROC_CD             = :V_REQ_NXT_PROC_CD,
    REQ_NXT_PROC_CAU_CD         = :V_REQ_NXT_PROC_CAU_CD,
    ACT_PAS_PROC                = :V_ACT_PAS_PROC,
    PRDN_CD                     = :V_PRDN_CD,
    ORD_NO                      = :V_ORD_NO,
    ORD_LN                      = :V_ORD_LN,
    ORD_NO_2                    = :V_ORD_NO_2,
    ORD_LN_2                    = :V_ORD_LN_2,
    PL_ORD_YN                   = :V_PL_ORD_YN,
    BF_ORD_NO                   = :V_BF_ORD_NO,
    BF_ORD_LN                   = :V_BF_ORD_LN,
    OBJ_CHG_APT_DH              = TO_DATE(:V_OBJ_CHG_APT_DH, 'YYYYMMDDHH24MISS'),
    WGT_DCS_MTH_TP              = :V_WGT_DCS_MTH_TP,
    CRM_MNF_STD_CD              = :V_CRM_MNF_STD_CD,
    MTL_CD                      = :V_MTL_CD,
    RMTL_SYM                    = :V_RMTL_SYM,
    COIL_MRG_TP                 = :V_COIL_MRG_TP,
    FST_RMTL_NO                 = :V_FST_RMTL_NO,
    RPV_RMTL_NO                 = :V_RPV_RMTL_NO,
    RMTL_NO_1                   = :V_RMTL_NO_1,
    RMTL_LTH_1                  = :V_RMTL_LTH_1,
    RMTL_WGT_1                  = :V_RMTL_WGT_1,
    RMTL_NO_2                   = :V_RMTL_NO_2,
    RMTL_LTH_2                  = :V_RMTL_LTH_2,
    RMTL_WGT_2                  = :V_RMTL_WGT_2,
    RMTL_NO_3                   = :V_RMTL_NO_3,
    RMTL_LTH_3                  = :V_RMTL_LTH_3,
    RMTL_WGT_3                  = :V_RMTL_WGT_3,
    RMTL_SLP_CD                 = :V_RMTL_SLP_CD,
    RMTL_HEAT_NO                = :V_RMTL_HEAT_NO,
    SLP_RMTL_NO                 = :V_SLP_RMTL_NO,
    PL_WLD_PRT                  = :V_PL_WLD_PRT,
    PL_WLD_CNT                  = :V_PL_WLD_CNT,
    PL_WLD_PNT_LOC_1            = :V_PL_WLD_PNT_LOC_1,
    PL_WLD_PNT_LOC_2            = :V_PL_WLD_PNT_LOC_2,
    PL_WLD_PNT_LOC_3            = :V_PL_WLD_PNT_LOC_3,
    PL_WLD_PNT_LOC_4            = :V_PL_WLD_PNT_LOC_4,
    PL_WLD_PNT_LOC_5            = :V_PL_WLD_PNT_LOC_5,
    PRD_WLD_CNT                 = :V_PRD_WLD_CNT,
    MID_WLD_PNT_LOC_1           = :V_MID_WLD_PNT_LOC_1,
    MID_WLD_PNT_LOC_2           = :V_MID_WLD_PNT_LOC_2,
    MID_WLD_PNT_LOC_3           = :V_MID_WLD_PNT_LOC_3,
    ORD_PDN_TP                  = :V_ORD_PDN_TP,
    URG_MTL_TP                  = :V_URG_MTL_TP,
    COIL_TOP_BOT_TP             = :V_COIL_TOP_BOT_TP,
    DMY_PTT_PLT_TP              = :V_DMY_PTT_PLT_TP,
    SEM_PRD_TP                  = :V_SEM_PRD_TP,
    PCM_ML_CLN_YN               = :V_PCM_ML_CLN_YN,
    PIN_HOLE_TP                 = :V_PIN_HOLE_TP,
    DMY_COIL_USE_CNT            = :V_DMY_COIL_USE_CNT,
    PRD_AUC_PRG_STS_TP          = :V_PRD_AUC_PRG_STS_TP,
    PRD_AUC_RGS_DH              = TO_DATE(:V_PRD_AUC_RGS_DH, 'YYYYMMDDHH24MISS'),
    COIL_MTL_OUT_SHP_TP         = :V_COIL_MTL_OUT_SHP_TP,
    HLD_YN                      = :V_HLD_YN,
    HLD_RGS_DH                  = TO_DATE(:V_HLD_RGS_DH, 'YYYYMMDDHH24MISS'),
    ERP_REG_YN                  = :V_ERP_REG_YN,
    LOD_LOC                     = :V_LOD_LOC,
    BF_LOD_LOC                  = :V_BF_LOD_LOC,
    STK_NO                      = :V_STK_NO,
    BASE_NO                     = :V_BASE_NO,
    SHT_CNT                     = :V_SHT_CNT,
    COIL_SLV_USE_YN             = :V_COIL_SLV_USE_YN,
    SPM_USE_YN                  = :V_SPM_USE_YN,
    PL_ST_YN                    = :V_PL_ST_YN,
    ANN_ST_YN                   = :V_ANN_ST_YN,
    COR_ST_YN                   = :V_COR_ST_YN,
    ORD_SPL_TP                  = :V_ORD_SPL_TP,
    SPL_DH                      = TO_DATE(:V_SPL_DH, 'YYYYMMDDHH24MISS'),
    SPL_CAU_CD                  = :V_SPL_CAU_CD,
    COIL_SUR_GRD                = :V_COIL_SUR_GRD,
    COIL_SHP_GRD                = :V_COIL_SHP_GRD,
    COIL_SZ_GRD                 = :V_COIL_SZ_GRD,
    COIL_UNT_WGT_GRD            = :V_COIL_UNT_WGT_GRD,
    COIL_APR_INS_GRD            = :V_COIL_APR_INS_GRD,
    COIL_MQL_GRD                = :V_COIL_MQL_GRD,
    COIL_MQL_GRD_CAU_CD         = :V_COIL_MQL_GRD_CAU_CD,
    COIL_MQL_SYN_GRD            = :V_COIL_MQL_SYN_GRD,
    COIL_ING_GRD                = :V_COIL_ING_GRD,
    COIL_QLT_TRK_GRD            = :V_COIL_QLT_TRK_GRD,
    OP_GRD                      = :V_OP_GRD,
    COIL_APR_INS_GRD_CAU_CD     = :V_COIL_APR_INS_GRD_CAU_CD,
    COIL_ARP_INS_DH             = TO_DATE(:V_COIL_ARP_INS_DH, 'YYYYMMDDHH24MISS'),
    PRD_SYN_GRD                 = :V_PRD_SYN_GRD,
    PRD_SYN_GRD_CAU_CD          = :V_PRD_SYN_GRD_CAU_CD,
    COIL_CLR_MPR_GRD            = :V_COIL_CLR_MPR_GRD,
    PRD_SYN_JDG_APT_DD          = :V_PRD_SYN_JDG_APT_DD,
    PRD_SYN_JDG_DH              = TO_DATE(:V_PRD_SYN_JDG_DH, 'YYYYMMDDHH24MISS'),
    MQL_TSTP_NO                 = :V_MQL_TSTP_NO,
    MPR_TSTP_NO                 = :V_MPR_TSTP_NO,
    SMP_GTH_CNT                 = :V_SMP_GTH_CNT,
    SPC_AVR                     = :V_SPC_AVR,
    MQL_SYM                     = :V_MQL_SYM,
    COIL_MQC                    = :V_COIL_MQC,
    GW_ASG_CD                   = :V_GW_ASG_CD,
    WK_GW_FRN                   = :V_WK_GW_FRN,
    WK_GW_BAK                   = :V_WK_GW_BAK,
    ORD_GW_ASG_CD               = :V_ORD_GW_ASG_CD,
    ORD_GW_FRN                  = :V_ORD_GW_FRN,
    ORD_GW_BAK                  = :V_ORD_GW_BAK,
    COIL_GAL_PTR_CD             = :V_COIL_GAL_PTR_CD,
    COIL_CLR_PTR_CD             = :V_COIL_CLR_PTR_CD,
    COIL_SUR_PHM_CD             = :V_COIL_SUR_PHM_CD,
    COIL_ROU_CD                 = :V_COIL_ROU_CD,
    ORD_USG_CD                  = :V_ORD_USG_CD,
    COIL_OIL_PNT_CD             = :V_COIL_OIL_PNT_CD,
    COIL_PTT_FLM_CD             = :V_COIL_PTT_FLM_CD,
    CRT_NO                      = :V_CRT_NO,
    PAS_PROC_CD_1               = :V_PAS_PROC_CD_1,
    COIL_RPROC_CNT_1            = :V_COIL_RPROC_CNT_1,
    COIL_NO_1                   = :V_COIL_NO_1,
    PAS_PROC_CD_2               = :V_PAS_PROC_CD_2,
    COIL_RPROC_CNT_2            = :V_COIL_RPROC_CNT_2,
    COIL_NO_2                   = :V_COIL_NO_2,
    PAS_PROC_CD_3               = :V_PAS_PROC_CD_3,
    COIL_RPROC_CNT_3            = :V_COIL_RPROC_CNT_3,
    COIL_NO_3                   = :V_COIL_NO_3,
    PAS_PROC_CD_4               = :V_PAS_PROC_CD_4,
    COIL_RPROC_CNT_4            = :V_COIL_RPROC_CNT_4,
    COIL_NO_4                   = :V_COIL_NO_4,
    PAS_PROC_CD_5               = :V_PAS_PROC_CD_5,
    COIL_RPROC_CNT_5            = :V_COIL_RPROC_CNT_5,
    COIL_NO_5                   = :V_COIL_NO_5,
    PAS_PROC_CD_6               = :V_PAS_PROC_CD_6,
    COIL_RPROC_CNT_6            = :V_COIL_RPROC_CNT_6,
    COIL_NO_6                   = :V_COIL_NO_6,
    PAS_PROC_CD_7               = :V_PAS_PROC_CD_7,
    COIL_RPROC_CNT_7            = :V_COIL_RPROC_CNT_7,
    COIL_NO_7                   = :V_COIL_NO_7,
    PAS_PROC_CD_8               = :V_PAS_PROC_CD_8,
    COIL_RPROC_CNT_8            = :V_COIL_RPROC_CNT_8,
    COIL_NO_8                   = :V_COIL_NO_8,
    PAS_PROC_CD_9               = :V_PAS_PROC_CD_9,
    COIL_RPROC_CNT_9            = :V_COIL_RPROC_CNT_9,
    COIL_NO_9                   = :V_COIL_NO_9,
    PAS_PROC_CD_10              = :V_PAS_PROC_CD_10,
    COIL_RPROC_CNT_10           = :V_COIL_RPROC_CNT_10,
    COIL_NO_10                  = :V_COIL_NO_10,
    POC_TP                      = :V_POC_TP,
    POC_CMP_CD                  = :V_POC_CMP_CD,
    PAK_EQU_CD                  = :V_PAK_EQU_CD,
    COIL_PAK_PTN                = :V_COIL_PAK_PTN,
    COIL_PAK_APT_DD             = :V_COIL_PAK_APT_DD,
    COIL_PAK_CPLT_DH            = TO_DATE(:V_COIL_PAK_CPLT_DH, 'YYYYMMDDHH24MISS'),
    COIL_PAK_SHF_GRP            = :V_COIL_PAK_SHF_GRP,
    PAK_WK_TP                   = :V_PAK_WK_TP,
    LBL_PUB_CNT                 = :V_LBL_PUB_CNT,
    LBL_PUB_DH                  = TO_DATE(:V_LBL_PUB_DH, 'YYYYMMDDHH24MISS'),
    PRD_WHS_DH                  = TO_DATE(:V_PRD_WHS_DH, 'YYYYMMDDHH24MISS'),
    PRD_WHS_APT_DD              = :V_PRD_WHS_APT_DD,
    PRD_DLV_REQ_KND_TP          = :V_PRD_DLV_REQ_KND_TP,
    CTD_SAL_TP                  = :V_CTD_SAL_TP,
    PRD_DLV_KND_TP              = :V_PRD_DLV_KND_TP,
    DLV_DH                      = TO_DATE(:V_DLV_DH, 'YYYYMMDDHH24MISS'),
    PRD_DLV_APT_DD              = :V_PRD_DLV_APT_DD,
    SHPG_DH                     = TO_DATE(:V_SHPG_DH, 'YYYYMMDDHH24MISS'),
    SHPG_APT_DD                 = :V_SHPG_APT_DD,
    REA_INF_WHS_TP              = :V_REA_INF_WHS_TP,
    REA_INF_RET_TP              = :V_REA_INF_RET_TP,
    PRD_RET_DH                  = TO_DATE(:V_PRD_RET_DH, 'YYYYMMDDHH24MISS'),
    PRD_RET_APT_DD              = :V_PRD_RET_APT_DD,
    PRD_RET_PRG_STS_TP          = :V_PRD_RET_PRG_STS_TP,
    PRD_RET_REQ_PROC_CD         = :V_PRD_RET_REQ_PROC_CD,
    PRD_RET_REQ_PRG_CD          = :V_PRD_RET_REQ_PRG_CD,
    PRD_RET_REQ_CAU_CD          = :V_PRD_RET_REQ_CAU_CD,
    SHPG_HLD_TP                 = :V_SHPG_HLD_TP,
    SHPG_HLD_DD                 = TO_DATE(:V_SHPG_HLD_DD, 'YYYYMMDDHH24MISS'),
    SHPG_HLD_CAU_TXT            = :V_SHPG_HLD_CAU_TXT,
    SHPG_HLD_CNL_DD             = TO_DATE(:V_SHPG_HLD_CNL_DD, 'YYYYMMDDHH24MISS'),
    PRD_LONG_STK_CAU_CD         = :V_PRD_LONG_STK_CAU_CD,
    PRD_LONG_STK_RGS_DH         = TO_DATE(:V_PRD_LONG_STK_RGS_DH, 'YYYYMMDDHH24MISS'),
    REA_INF_CRYN_TP             = :V_REA_INF_CRYN_TP,
    PRD_CRYN_CAU_TP             = :V_PRD_CRYN_CAU_TP,
    PRD_CRYN_DH                 = TO_DATE(:V_PRD_CRYN_DH, 'YYYYMMDDHH24MISS'),
    PRD_CRYN_APT_DD             = :V_PRD_CRYN_APT_DD,
    DLV_PROG_STS_TP             = :V_DLV_PROG_STS_TP,
    MS_PUB_YN                   = :V_MS_PUB_YN,
    MS_PUB_DH                   = TO_DATE(:V_MS_PUB_DH, 'YYYYMMDDHH24MISS'),
    PCOIL_NO                    = :V_PCOIL_NO,
    TRF_REQ_NO                  = :V_TRF_REQ_NO,
    TRF_PRG_STS_TP              = :V_TRF_PRG_STS_TP,
    TRF_WHS_DH                  = TO_DATE(:V_TRF_WHS_DH, 'YYYYMMDDHH24MISS'),
    PLNT_TP                     = :V_PLNT_TP,
    CREATED_OBJECT_TYPE         = :V_CREATED_OBJECT_TYPE,
    CREATED_OBJECT_ID           = :V_CREATED_OBJECT_ID,
    CREATED_PROGRAM_ID          = :V_CREATED_PROGRAM_ID,
    CREATION_TIMESTAMP          = TO_TIMESTAMP(:V_CREATION_TIMESTAMP, 'YYYYMMDDHH24MISSFF'),
    LAST_UPDATED_OBJECT_TYPE    = :V_LAST_UPDATED_OBJECT_TYPE,
    LAST_UPDATED_OBJECT_ID      = :V_LAST_UPDATED_OBJECT_ID,
    LAST_UPDATE_PROGRAM_ID      = :V_LAST_UPDATE_PROGRAM_ID,
    LAST_UPDATE_TIMESTAMP       = TO_TIMESTAMP(:V_LAST_UPDATE_TIMESTAMP, 'YYYYMMDDHH24MISSFF'),
    DATA_END_STATUS             = :V_DATA_END_STATUS,
    DATA_END_OBJECT_TYPE        = :V_DATA_END_OBJECT_TYPE,
    DATA_END_OBJECT_ID          = :V_DATA_END_OBJECT_ID,
    DATA_END_PROGRAM_ID         = :V_DATA_END_PROGRAM_ID,
    DATA_END_TIMESTAMP          = TO_TIMESTAMP(:V_DATA_END_TIMESTAMP, 'YYYYMMDDHH24MISSFF'),
    ARCHIVE_COMPLETED_FLAG      = :V_ARCHIVE_COMPLETED_FLAG,
    ARCHIVED_EMPLOYEE_NUM       = :V_ARCHIVED_EMPLOYEE_NUM,
    ARCHIVED_TIMESTAMP          = TO_TIMESTAMP(:V_ARCHIVED_TIMESTAMP, 'YYYYMMDDHH24MISSFF'),
    ARCHIVE_PROGRAM_ID          = :V_ARCHIVE_PROGRAM_ID,
    CCL_CR_TRT_TP               = :V_CCL_CR_TRT_TP,
    IN_PLT_CVT_CAU_CD           = :V_IN_PLT_CVT_CAU_CD,
    CCL_2PASS_YN                = :V_CCL_2PASS_YN,
    COIL_RMW_WGT_1              = :V_COIL_RMW_WGT_1,
    COIL_RMW_WGT_2              = :V_COIL_RMW_WGT_2,
    SAP_MTL_CD                  = :V_SAP_MTL_CD,
    ITEMNAME_CD                 = :V_ITEMNAME_CD,
    PDN_PLNT_TP                 = :V_PDN_PLNT_TP,
    PLN_PAS_PROC_CMN            = :V_PLN_PAS_PROC_CMN,
    PAS_PLNT_ACT                = :V_PAS_PLNT_ACT,
    INV_NO                      = :V_INV_NO,
    LGS_BAS_CD                  = :V_LGS_BAS_CD,
    WIP_MTL_CD                  = :V_WIP_MTL_CD,
    MS_SPE_YN                   = :V_MS_SPE_YN,
    UNDER_COIL_YN               = :V_UNDER_COIL_YN,
    UNDER_COIL_PLN_PAS          = :V_UNDER_COIL_PLN_PAS,
    UNDER_COIL_OBJECT_ID        = :V_UNDER_COIL_OBJECT_ID,
    COR_CAU_TXT                 = :V_COR_CAU_TXT,
    POC_TAR_MTL                 = :V_POC_TAR_MTL,
    BLDP_TP                     = :V_BLDP_TP
WHERE 1=1
AND COIL_NO                     = :V_COIL_NO

 </pre> */
public final static String updYfCrCoilComm = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updYfCrCoilComm";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YfCommonDAO.UpdateEquipStatInfo
UPDATE TB_YF_EQP
   SET YD_EQP_STAT  = :V_YD_EQP_STAT,
	   modifier   = 'SYSTEM',
	   mod_ddtt   = sysdate     
 WHERE YD_EQP_ID 	= :V_YD_EQP_ID
 </pre> */
public final static String UpdateEquipStatInfo = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.UpdateEquipStatInfo";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updEquipCarYn
UPDATE TB_YF_EQP
   SET YD_L2_HMI_STAT = :V_CAR_YN  --차량 유무
     , MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
 WHERE YD_EQP_ID = '1EGT01' --스크랩 차량
 </pre> */
public final static String updEquipCarYn = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updEquipCarYn";

 /** <pre> 
--yf.common.dao.updateWrkbookIdOfStock1
UPDATE  TB_YF_STOCK
   SET  STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM
 WHERE  STL_NO  = :V_STL_NO
 </pre> */
public final static String updateWrkbookIdOfStock1 = "bak.yf.common.dao.updateWrkbookIdOfStock1";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updTcarSchInitMtl 
UPDATE TB_YF_TCARFTMVMTL
   SET MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,DEL_YN   = 'Y'
 WHERE DEL_YN   = 'N'
   AND YD_TCAR_SCH_ID IN (SELECT YD_TCAR_SCH_ID
                            FROM TB_YF_TCARSCH
                           WHERE YD_EQP_ID = :V_YD_EQP_ID
                             AND DEL_YN    = 'N')
 </pre> */
public final static String updTcarSchInitMtl = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updTcarSchInitMtl";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updTcarSchInitSch 
UPDATE TB_YF_TCARSCH
   SET MODIFIER  = :V_MODIFIER
      ,MOD_DDTT  = SYSDATE
      ,DEL_YN    = 'Y'
 WHERE YD_EQP_ID = :V_YD_EQP_ID
   AND DEL_YN    = 'N'
 </pre> */
public final static String updTcarSchInitSch = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updTcarSchInitSch";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.insertCrnWrslt
INSERT INTO TB_YF_WRKHIST(
      YD_WRK_HIST_ID
    , REGISTER
    , REG_DDTT
    , MODIFIER
    , MOD_DDTT
    , DEL_YN
    , YD_GP
    , STL_NO
    , YD_CRN_SCH_ID
    , YD_SCH_CD
    , YD_EQP_ID
    , YD_SCH_PRIOR
    , YD_SCH_DT
    , YD_WRK_DUTY
    , YD_WRK_PARTY
    , YD_UP_WO_LOC
    , YD_DN_WO_LOC
    , YD_DN_WO_LYR
    , YD_UP_WR_LOC
    , YD_UP_WR_LYR
    , YD_UP_WR_FUNC
    , YD_UP_CMPL_DT
    , YD_DN_WR_LOC
    , YD_DN_WR_LYR
    , YD_DN_WR_FUNC
    , YD_DN_CMPL_DT
    , STL_APPEAR_GP
    , ITEMNAME_CD
    , ORD_YEOJAE_GP
    , ORD_NO
    , ORD_DTL
    , SPEC_ABBSYM
    , CUST_CD
    , DEMANDER_CD
    , GOODS_GRADE
    , YD_MTL_W_GP
    , YD_MTL_T
    , YD_MTL_W
    , YD_MTL_L
    , YD_MTL_WT
    , YD_COIL_OUTDIA_GRP_GP
    , COIL_INDIA
    , COIL_OUTDIA
    , TRANS_ORD_DATE
    , STL_PROG_CD
)
SELECT TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI')||YF_WRKHIST_SEQ.NEXTVAL  AS YD_WRK_HIST_ID
     , :V_MODIFIER                                                AS REGISTER
     , NVL(TO_DATE(:V_MOD_DDTT,'YYYYMMDDHH24MISS'),SYSDATE)       AS REG_DDTT
     , :V_MODIFIER                                                AS MODIFIER
     , NVL(TO_DATE(:V_MOD_DDTT,'YYYYMMDDHH24MISS'),SYSDATE)       AS MOD_DDTT
     , 'N'                                                        AS DEL_YN
     , :V_YD_GP                                                   AS YD_GP
     , :V_STL_NO                                                  AS STL_NO
     , :V_YD_CRN_SCH_ID                                           AS YD_CRN_SCH_ID
     , :V_YD_SCH_CD                                               AS YD_SCH_CD
     , :V_YD_EQP_ID                                               AS YD_EQP_ID
     , '1'                                                        AS YD_SCH_PRIOR
     , SYSDATE                                                    AS YD_SCH_DT
     , :V_YD_WRK_DUTY                                             AS YD_WRK_DUTY
     , :V_YD_WRK_PARTY                                            AS YD_WRK_PARTY
     , ''                                                         AS YD_UP_WO_LOC
     , CASE WHEN LENGTH(:V_YD_DN_WO_LOC) > 8 THEN SUBSTR(:V_YD_DN_WO_LOC,1,8) ELSE :V_YD_DN_WO_LOC END AS YD_DN_WO_LOC
     , CASE WHEN LENGTH(:V_YD_DN_WO_LOC) = 10 THEN SUBSTR(:V_YD_DN_WO_LOC,-2) ELSE '' END AS YD_DN_WO_LYR
     , CASE WHEN LENGTH(:V_YD_UP_WO_LOC) > 8 THEN SUBSTR(:V_YD_UP_WO_LOC,1,8) ELSE :V_YD_UP_WO_LOC END AS YD_UP_WR_LOC
     , CASE WHEN LENGTH(:V_YD_UP_WO_LOC) = 10 THEN SUBSTR(:V_YD_UP_WO_LOC,-2) ELSE '' END AS YD_UP_WR_LYR
     , :V_UP_FUNC                                                 AS YD_UP_WR_FUNC
     , SYSDATE                                                    AS YD_UP_CMPL_DT
     , CASE WHEN LENGTH(:V_YD_DN_WO_LOC) > 8 THEN SUBSTR(:V_YD_DN_WO_LOC,1,8) ELSE :V_YD_DN_WO_LOC  END AS YD_DN_WR_LOC
     , CASE WHEN LENGTH(:V_YD_DN_WO_LOC) = 10 THEN SUBSTR(:V_YD_DN_WO_LOC,-2) ELSE '' END AS YD_DN_WR_LYR
     , :V_PUT_FUNC                                                AS YD_DN_WR_FUNC
     , SYSDATE                                                    AS YD_DN_CMPL_DT
     , G.STL_APPEAR_GP         AS STL_APPEAR_GP
     , G.ITEMNAME_CD           AS ITEMNAME_CD
     , G.ORD_YEOJAE_GP         AS ORD_YEOJAE_GP
     , G.ORD_NO                AS ORD_NO
     , G.ORD_DTL               AS ORD_DTL
     , G.SPEC_ABBSYM           AS SPEC_ABBSYM
     , G.CUST_CD               AS CUST_CD
     , G.DEMANDER_CD           AS DEMANDER_CD
     , G.OVERALL_STAMP_GRADE   AS GOODS_GRADE
     , CASE WHEN G.COIL_W < 1601 THEN 'M' ELSE 'L' END AS YD_MTL_W_GP
     , G.COIL_T                                AS YD_MTL_T
     , G.COIL_W                                AS YD_MTL_W
     , G.COIL_LEN                              AS YD_MTL_L
     , G.COIL_WT                               AS YD_MTL_WT
     , CASE WHEN G.COIL_OUTDIA <=1280 THEN 'A'
            WHEN G.COIL_OUTDIA <=1930 THEN 'B'
                                      ELSE 'C'
            END                                AS YD_COIL_OUTDIA_GRP_GP
     , G.COIL_INDIA                            AS COIL_INDIA
     , G.COIL_OUTDIA                           AS COIL_OUTDIA
     , B.TRANS_ORD_DATE                       AS TRANS_ORD_DATE
     , G.CURR_PROG_CD
  FROM TB_YF_STOCK  B
     , USRPTA.TB_PT_COILCOMM G
 WHERE B.STL_NO           = B.STL_NO
   AND B.STL_NO           = G.COIL_NO(+)
   AND B.STL_NO = :V_STL_NO
 </pre> */
public final static String insertCrnWrslt = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insertCrnWrslt";

 /** <pre> 
--yf.facilitywork.putwrecord.session.updateEqualUpperReset
update TB_YF_STKLYR
set YD_STK_LYR_ACTIVE_STAT = 'C' ,
    YD_STK_LYR_STAT = 'E',
    STL_NO = ''
where YD_STK_COL_GP = :V_YD_STK_COL_GP
  and YD_STK_BED_NO = :V_YD_STK_BED_NO
  and YD_STK_LYR_NO >= :V_YD_STK_LYR_NO 
 </pre> */
public final static String updateEqualUpperReset = "bak.yf.facilitywork.putwrecord.session.updateEqualUpperReset";

 /** <pre> 
-- com.inisteel.cim.yf.common.dao.YfCommDAO.updateCraneYdStkLyrStat  
UPDATE TB_YF_STKLYR
   SET STL_NO			= :V_STL_NO
	 , YD_STK_LYR_STAT	= :V_YD_STK_LYR_STAT
	 , MODIFIER         = 'SYSTEM'
 	 , MOD_DDTT         = SYSDATE     
 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP 
   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO 
   AND YD_STK_LYR_NO   = :V_YD_STK_LYR_NO
 </pre> */
public final static String updateCraneYdStkLyrStat = "bak.com.inisteel.cim.yf.common.dao.YfCommDAO.updateCraneYdStkLyrStat";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.MergeHelpBtn
MERGE 
 INTO TB_YF_HELP_BTN A
 USING ( SELECT :V_PAGE_ID               AS PAGE_ID
               ,:V_BTN_ID                AS BTN_ID
               ,:V_BTN_NM                AS BTN_NM
               ,:V_RVS_NO                AS RVS_NO
               ,:V_BTN_IMG_PATH          AS BTN_IMG_PATH
               ,:V_BTN_DISC              AS BTN_DISC
               ,:V_BTN_SEQ               AS BTN_SEQ
               ,NVL(:V_DEL_YN,'N')       AS DEL_YN
               ,:V_MODIFIER              AS MODIFIER 
               ,SYSDATE                  AS MOD_DDTT
           FROM DUAL ) B
  ON (A.PAGE_ID = B.PAGE_ID AND A.BTN_ID = B.BTN_ID)
  WHEN MATCHED THEN 
       UPDATE SET A.DEL_YN = B.DEL_YN
                 ,A.BTN_IMG_PATH = DECODE(NVL(B.BTN_IMG_PATH,A.BTN_IMG_PATH),'-','',B.BTN_IMG_PATH)
                 ,A.BTN_DISC = DECODE(NVL(B.BTN_DISC,A.BTN_DISC),'-','',B.BTN_DISC)
                 ,A.BTN_SEQ = B.BTN_SEQ
                 ,A.MODIFIER = B.MODIFIER
                 ,A.MOD_DDTT = B.MOD_DDTT
  WHEN NOT MATCHED THEN
           INSERT ( PAGE_ID
                   ,BTN_ID
                   ,BTN_NM
                   ,RVS_NO
                   ,BTN_IMG_PATH
                   ,BTN_DISC
                   ,BTN_SEQ
                   ,DEL_YN
                   ,REGISTER
                   ,REG_DDTT
                   ,MODIFIER
                   ,MOD_DDTT)
           VALUES ( B.PAGE_ID
                   ,B.BTN_ID
                   ,B.BTN_NM
                   ,B.RVS_NO
                   ,B.BTN_IMG_PATH
                   ,B.BTN_DISC
                   ,B.BTN_SEQ
                   ,B.DEL_YN
                   ,B.MODIFIER
                   ,B.MOD_DDTT
                   ,B.MODIFIER
                   ,B.MOD_DDTT)
 </pre> */
public final static String MergeHelpBtn = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.MergeHelpBtn";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCTSSchDnModify
--CTS스케줄 하차 실적 
UPDATE TB_YF_CTS_SCH 
SET YD_WRK_PROG_STAT= :V_YD_WRK_PROG_STAT
  , MODIFIER        = :V_MODIFIER
  , MOD_DDTT        = SYSDATE
  , YD_CARUD_WR_LOC = :V_YD_CARUD_WR_LOC
  , YD_CARUD_WR_DT  = SYSDATE
WHERE YD_EQP_ID = :V_YD_EQP_ID
  AND DEL_YN    = 'N'
  AND STL_NO    = :V_STL_NO
 </pre> */
public final static String updCTSSchDnModify = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updCTSSchDnModify";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilCarPntUnitClInqjl.updCarPointStat
UPDATE TB_YD_CARPOINT
SET   MODIFIER = :V_MODIFIER
     ,MOD_DDTT = sysdate
     ,YD_STK_COL_ACT_STAT = DECODE(:V_CAR_CARD_NO,'Y','C','N','N')
WHERE YD_STK_COL_GP=:V_YD_STK_COL_GP
 </pre> */
public final static String updCarPointStat = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.acoilCarPntUnitClInqjl.updCarPointStat";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.insAxYML009CarMtlIns2
--크레인권하실적 차량이송재료 등록
INSERT INTO USRYDA.TB_YD_CARFTMVMTL
(
    YD_CAR_SCH_ID,
    STL_NO,
    REGISTER,
    REG_DDTT,
    MODIFIER,
    MOD_DDTT,
    DEL_YN,
    YD_STK_BED_NO,
    YD_STK_LYR_NO
)
VALUES
(
    :V_YD_CAR_SCH_ID,
    :V_STL_NO,
    :V_MODIFIER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N',
    NVL(SUBSTR(:V_YD_DN_WR_LOC, -2), '01'),
    '001'
)
 </pre> */
public final static String insAxYML009CarMtlIns2 = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insAxYML009CarMtlIns2";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updClearScrapLyrByArrStockId 
UPDATE TB_YF_STKLYR
   SET STL_NO         = NULL
     , YD_STK_LYR_STAT = 'E'
     , MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
 WHERE YD_STK_COL_GP LIKE '1ESC%'
   AND STL_NO IN (SELECT REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) AS STL_NO
			          FROM (SELECT :V_ARR_STL_NO AS SSTL_NOS FROM DUAL)
			       CONNECT BY REGEXP_SUBSTR(SSTL_NOS, '[^,]+', 1, LEVEL) IS NOT NULL)
   AND YD_STK_LYR_STAT IN ('C')
 </pre> */
public final static String updClearScrapLyrByArrStockId = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updClearScrapLyrByArrStockId";

 /** <pre> 
-- com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockBendingStat 
UPDATE TB_YF_STOCK
   SET MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,BENDING_MODIFIER = :V_MODIFIER
      ,BENDING_DDTT = SYSDATE
      ,STOCK_MOVE_TERM  = :V_STOCK_MOVE_TERM
      ,BENDING_GP       = :V_BENDING_GP
      ,BENDING_AXIS     = :V_BENDING_AXIS
      ,YD_RULE_PL_RS_GP = :V_YD_RULE_PL_RS_GP     
 WHERE STL_NO = :V_STL_NO
 </pre> */
public final static String updStockBendingStat = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockBendingStat";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelTcarFtMvMtl 
UPDATE TB_YF_TCARFTMVMTL
   SET MODIFIER    = :V_MODIFIER
      ,MOD_DDTT    = SYSDATE
      ,DEL_YN      = 'Y'
 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
   AND STL_NO       = :V_STL_NO
   AND DEL_YN         = 'N'
 </pre> */
public final static String updDelTcarFtMvMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updDelTcarFtMvMtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML009CarSchLd
--크레인권하실적 상차 차량스케줄 수정
UPDATE USRYDA.TB_YD_CARSCH TS
SET
    TS.MODIFIER             = :V_MODIFIER,
    TS.MOD_DDTT             = SYSDATE,
    TS.YD_EQP_WRK_STAT      = 'L',                  --영차
    TS.YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT,
    TS.YD_EQP_WRK_SH        = (
                                SELECT
                                    COUNT(*)
                                FROM
                                    TB_YD_CARFTMVMTL
                                WHERE 1=1
                                AND YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
                              ),
    TS.YD_EQP_WRK_WT        = (
                                SELECT
                                    SUM(COIL_WT)
                                FROM
                                    TB_YD_CARFTMVMTL A,
                                    USRPTA.TB_PT_COILCOMM   B
                                WHERE 1=1
                                AND A.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
                                AND A.STL_NO        = B.COIL_NO
                              ),
    --TS.ARR_WLOC_CD          = NVL(TS.ARR_WLOC_CD, DD.ARR_WLOC_CD),
    TS.YD_PNT_CD3           = '0000',
    TS.YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID,
    TS.YD_CARLD_STOP_LOC    = :V_YD_STK_COL_GP,
    TS.YD_CARLD_ST_DT       = NVL(TS.YD_CARLD_ST_DT, NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'), SYSDATE)),
    TS.YD_CARLD_CMPL_DT     = DECODE(:V_YD_CAR_PROG_STAT, '5', NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'), SYSDATE), NULL)
WHERE 1=1
AND TS.YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updAxYML009CarSchLd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML009CarSchLd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_02C
UPDATE TB_YF_STKLYR
SET
    STL_NO          = '',
    YD_STK_LYR_ACTIVE_STAT  = :V_YD_STK_LYR_ACTIVE_STAT,
    YD_STK_LYR_STAT = 'E',
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND YD_STK_COL_GP IN
(
    --개소코드와 차량번호로 적치열을 찾는다.
    SELECT
        YD_STK_COL_GP
    FROM
        (
            SELECT --+INDEX_DESC(A PK_YD_CARSCH)
                (
                    CASE
                    WHEN YD_CAR_PROG_STAT IN ('1','2','3','4','5') THEN YD_CARLD_STOP_LOC
                    ELSE YD_CARUD_STOP_LOC END
                ) AS YD_STK_COL_GP,
                TRN_EQP_CD
            FROM
                USRYDA.TB_YD_CARSCH A
            WHERE 1=1
            AND NVL(CARD_NO, TRN_EQP_CD) = :V_TRN_EQP_CD
            AND YD_CAR_SCH_ID >= TO_CHAR(SYSDATE-1, 'YYYYMMDD')
            AND ROWNUM<=1
        ) A
    WHERE 1=1
    AND EXISTS
    (
        SELECT
            1
        FROM
            TB_YF_STKCOL B
        WHERE 1=1
        AND B.TRN_EQP_CD    = A.TRN_EQP_CD
        AND B.YD_STK_COL_GP = A.YD_STK_COL_GP
    )
)
 </pre> */
public final static String updateLayerstat_02C = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_02C";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommTcarSchWbDelLd
--대차스케줄 작업예약ID 삭제 
UPDATE TB_YF_TCARSCH
   SET MODIFIER              = :V_MODIFIER
      ,MOD_DDTT              = SYSDATE
      ,YD_CARLD_WRK_BOOK_ID  = NULL
 WHERE DEL_YN                = 'N'
   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String updCommTcarSchWbDelLd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommTcarSchWbDelLd";

 /** <pre> 
-- com.inisteel.cim.yf.aslab.dao.ASlabDAO.updateMatlFtmvWlrstMSlabNEW 
UPDATE TB_PT_MSLABCOMM
SET FNL_REG_PGM = 'yfCurrProgcd',
    CURR_PROG_CD_REG_PGM ='yfCurrProgcd',   -- 현재진도코드 PGM
    CURR_PROG_REG_DDTT =sysdate,     -- 현재진도코드등록일시       
    CURR_PROG_CD = :V_CURR_PROG_CD ,
    BEFO_PROG_CD_REG_PGM=CURR_PROG_CD_REG_PGM,   -- 전 진도코드 PGM
    BEFO_PROG_REG_DDTT= CURR_PROG_REG_DDTT,     -- 전 진도코드등록일시
    BEFO_PROG_CD =CURR_PROG_CD,           -- 전 진도코드
    BEFOBEFO_PROG_CD_REG_PGM =BEFO_PROG_CD_REG_PGM ,
    BEFOBEFO_PROG_REG_DDTT =BEFO_PROG_REG_DDTT ,
    BEFOBEFO_PROG_CD = BEFO_PROG_CD,
    MODIFIER ='SYSTEM',               -- 수정자
    MOD_DDTT =sysdate,
    MATL_TKOV_DT =sysdate
WHERE MSLAB_NO =:V_MSLAB_NO
 </pre> */
public final static String updateMatlFtmvWlrstMSlabNEW = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updateMatlFtmvWlrstMSlabNEW";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.setPageHelpDoc
-- 화면 도움말 - 첨부문서 등록
INSERT INTO USRYFA.TB_YF_HELP_DOC (
      PAGE_ID   -- 화면ID   
    , DOC_SEQ   -- 문서번호 
    , DOC_NM    -- 문서명   
    , DOC_PATH  -- 문서경로 
    , REGISTER  -- 등록자   
    , REG_DDTT  -- 등록 일시
    , MODIFIER  -- 수정자   
    , MOD_DDTT  -- 수정 일시
    , DEL_YN    -- 삭제여부 
) VALUES (
      :V_PAGE_ID   -- 화면ID   
    , :V_DOC_SEQ   -- 문서번호 
    , :V_DOC_NM    -- 문서명
    , :V_DOC_PATH  -- 문서경로 
    , :V_REGISTER  -- 등록자   
    , SYSDATE      -- 등록 일시
    , :V_MODIFIER  -- 수정자   
    , SYSDATE      -- 수정 일시
    , 'N'          -- 삭제여부
)
 </pre> */
public final static String setPageHelpDoc = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.setPageHelpDoc";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.updPageHelpBtnRvsInit
-- 박판열연 YF 화면 도움말 - 이전 버전 버튼 미사용 처리(신규버전 버튼정보 입력 전 실행)
UPDATE USRYFA.TB_YF_HELP_BTN B
   SET  B.DEL_YN  = 'Y'          -- 삭제여부   
WHERE   B.PAGE_ID = :V_PAGE_ID   -- 화면ID
 </pre> */
public final static String updPageHelpBtnRvsInit = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updPageHelpBtnRvsInit";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnSchDnWoLocCarSch 
--크레인작업관리 권하위치변경 차량스케줄 수정
UPDATE TB_YD_CARSCH
   SET MODIFIER             = :V_MODIFIER
      ,MOD_DDTT             = SYSDATE
      ,YD_CARLD_WRK_BOOK_ID = NULL
 WHERE YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
   AND DEL_YN = 'N'
 </pre> */
public final static String updCrnSchDnWoLocCarSch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnSchDnWoLocCarSch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommCarSchWbDelLd
--  상차 차량 작업 예약 ID CLEAR
UPDATE USRYDA.TB_YD_CARSCH
   SET MODIFIER              = :V_MODIFIER
      ,MOD_DDTT              = SYSDATE
      ,YD_CARLD_WRK_BOOK_ID  = NULL
 WHERE DEL_YN                = 'N'
   AND YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String updCommCarSchWbDelLd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommCarSchWbDelLd";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommCarSchWbDelUd
--  하차 차량 작업 예약 ID CLEAR
UPDATE USRYDA.TB_YD_CARSCH
   SET MODIFIER              = :V_MODIFIER
      ,MOD_DDTT              = SYSDATE
      ,YD_CARUD_WRK_BOOK_ID  = NULL
 WHERE DEL_YN                = 'N'
   AND YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String updCommCarSchWbDelUd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCommCarSchWbDelUd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CarSchLd
--크레인권상실적 상차 차량스케줄 수정 
UPDATE TB_YD_CARSCH
   SET MODIFIER             = :V_MODIFIER
	  ,MOD_DDTT             = SYSDATE
      ,YD_EQP_WRK_STAT      = 'U' --공차
      ,YD_CAR_PROG_STAT     = '4' --상차개시
      ,ARR_WLOC_CD          = :V_ARR_WLOC_CD
      ,YD_CARLD_WRK_BOOK_ID = :V_YD_WBOOK_ID
      ,YD_CARLD_ST_DT       = NVL(TO_DATE(:V_WR_DT,'YYYYMMDDHH24MISS'),SYSDATE)
 WHERE YD_CAR_SCH_ID        = :V_YD_CAR_SCH_ID
   AND DEL_YN               = 'N'
 </pre> */
public final static String updAxYML008CarSchLd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CarSchLd";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCarFtMvMtlCmbnCarldYn
MERGE INTO TB_YD_CARFTMVMTL TM USING
(
    SELECT
        CC.COIL_NO          AS STL_NO,
        CC.HCR_GP           AS HCR_GP,
        CC.RECORD_PROG_STAT AS STL_PROG_CD,
        CC.YD_STK_BED_NO AS YD_STK_BED_NO,
        (
            SELECT
                MAX(A.YD_STK_BED_NO)
            FROM
                TB_YD_CARFTMVMTL A
            WHERE 1=1
            AND A.STL_NO        = CC.COIL_NO
            AND A.YD_CAR_SCH_ID =
            (
                SELECT
                    MAX(YD_CAR_SCH_ID)
                FROM
                    TB_YD_CARSCH
                WHERE 1=1
                AND TRANS_ORD_DATE  = P.V_TRANS_ORD_DT
                AND TRANS_ORD_SEQNO = P.V_TRANS_ORD_SEQNO
                AND CARD_NO         = P.V_CARD_NO
                AND CMBN_CARLD_YN   = 'S'
            )
        )                   AS YD_STK_BED_NO2,
        '001'               AS YD_STK_LYR_NO,
        P.V_YD_CAR_SCH_ID   AS YD_CAR_SCH_ID,
        P.V_MODIFIER        AS MODIFIER,
        SYSDATE             AS MOD_DDTT,
        'N'                 AS DEL_YN
    FROM
        USRPTA.TB_PT_COILCOMM CC,
        (
            SELECT
                :V_TRANS_ORD_DT     AS V_TRANS_ORD_DT,
                :V_TRANS_ORD_SEQNO  AS V_TRANS_ORD_SEQNO,
                :V_CARD_NO          AS V_CARD_NO,
                :V_YD_CAR_SCH_ID    AS V_YD_CAR_SCH_ID,
                :V_MODIFIER         AS V_MODIFIER
            FROM
                DUAL
        ) P
    WHERE 1=1
    AND COIL_NO IN
    (
        SELECT
            B.STL_NO
        FROM
            TB_YD_CARSCH A,
            TB_YD_CARFTMVMTL B
        WHERE 1=1
        AND A.YD_CAR_SCH_ID     = B.YD_CAR_SCH_ID
        AND A.TRANS_ORD_DATE    = P.V_TRANS_ORD_DT
        AND A.TRANS_ORD_SEQNO   = P.V_TRANS_ORD_SEQNO
        AND A.CARD_NO           = P.V_CARD_NO
        AND A.DEL_YN            = 'Y'
        AND B.DEL_YN            = 'Y'
        AND A.CMBN_CARLD_YN     = 'S'
    )
) DD
ON
(
    1=1
    AND TM.YD_CAR_SCH_ID    = DD.YD_CAR_SCH_ID
    AND TM.STL_NO           = DD.STL_NO
)
WHEN NOT MATCHED THEN
    INSERT
    (
        TM.YD_CAR_SCH_ID,
        TM.STL_NO,
        TM.REGISTER,
        TM.REG_DDTT,
        TM.MODIFIER,
        TM.MOD_DDTT,
        TM.DEL_YN,
        TM.YD_STK_BED_NO,
        TM.YD_STK_LYR_NO,
        TM.HCR_GP,
        TM.STL_PROG_CD
    )
    VALUES
    (
        DD.YD_CAR_SCH_ID,
        DD.STL_NO,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.DEL_YN,
        DD.YD_STK_BED_NO,
        DD.YD_STK_LYR_NO,
        DD.HCR_GP,
        DD.STL_PROG_CD
    )
 </pre> */
public final static String updCarFtMvMtlCmbnCarldYn = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCarFtMvMtlCmbnCarldYn";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.insWrkBookMtl
--작업예약재료 등록 
INSERT INTO TB_YF_WRKBOOKMTL
(
    YD_WBOOK_ID,    --야드작업예약ID
    STL_NO,         --재료번호
    YD_STK_COL_GP,  --야드적치열구분
    YD_STK_BED_NO,  --야드적치BED번호
    YD_STK_LYR_NO,  --야드적치단번호
    YD_UP_COLL_SEQ, --야드권상모음순서
    REGISTER,
    REG_DDTT,
    MODIFIER,
    MOD_DDTT,
    DEL_YN
)
VALUES
(
    :V_YD_WBOOK_ID,
    :V_STL_NO,
    :V_YD_STK_COL_GP,
    :V_YD_STK_BED_NO,
    :V_YD_STK_LYR_NO,
    :V_YD_UP_COLL_SEQ,
    :V_MODIFIER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N'
)
 </pre> */
public final static String insWrkBookMtl = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insWrkBookMtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnSchProgStatMsg
UPDATE TB_YF_CRNSCH  A 
   SET YD_WRK_PROG_STAT    =(CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
     , YD_WRK_PROG_REQ_MSG =(CASE WHEN YD_WRK_PROG_REQ_MSG IS NULL THEN :V_YD_WRK_PROG_REQ_MSG ELSE YD_WRK_PROG_REQ_MSG END) 
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updYdCrnSchProgStatMsg = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnSchProgStatMsg";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.deleteWBook
UPDATE TB_YF_WRKBOOK
SET
    DEL_YN      = 'Y',
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_WBOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String deleteWBook = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.deleteWBook";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.setPageHelpBtnInfo
-- 박판열연 YF 화면 도움말 - 버튼등록
INSERT INTO USRYFA.TB_YF_HELP_BTN B(
      B.PAGE_ID   -- 화면ID     
    , B.BTN_ID    -- 버튼ID
    , B.BTN_NM    -- 버튼명
    , B.RVS_NO    -- Revision
    , B.BTN_IMG_PATH -- 버튼 이미지 경로
    , B.BTN_DISC  -- Discription
    , B.BTN_SEQ   -- 순번       
    , B.DEL_YN    -- 삭제여부   
    , B.REGISTER  -- 등록자     
    , B.REG_DDTT  -- 등록 일시  
    , B.MODIFIER  -- 수정자     
    , B.MOD_DDTT  -- 수정 일시  
) VALUES (
      :V_PAGE_ID   -- 화면ID     
    , :V_BTN_ID    -- 버튼ID
    , :V_BTN_NM    -- 버튼명
    , :V_RVS_NO    -- 버전       
    , :V_BTN_IMG_PATH -- 버튼 이미지 경로
    , :V_BTN_DISC  -- Discription
    , (SELECT NVL(MAX(BTN_SEQ)+1, 1) FROM USRYFA.TB_YF_HELP_BTN WHERE PAGE_ID = :V_PAGE_ID)   -- 순번       
    , 'N'          -- 삭제여부   
    , :V_MODIFIER  -- 등록자     
    , SYSDATE      -- 등록 일시  
    , :V_MODIFIER  -- 수정자     
    , SYSDATE      -- 수정 일시  
)
 </pre> */
public final static String setPageHelpBtnInfo = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.setPageHelpBtnInfo";

 /** <pre> 
--yf.tsinfo.getListLoadEndStldel
UPDATE TB_YD_CARFTMVMTL
SET
    DEL_YN = 'Y'
WHERE 1=1
AND YD_CAR_SCH_ID =
(
    SELECT
        YD_CAR_SCH_ID
    FROM
        TB_YD_CARSCH
    WHERE 1=1
    AND DEL_YN='N'
    AND TRN_EQP_CD = :V_TRN_EQP_CD
    AND ROWNUM = 1
)
 </pre> */
public final static String getListLoadEndStldel = "bak.yf.tsinfo.getListLoadEndStldel";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.insEqpTrblReg 
INSERT INTO TB_YF_EQPPAUSE (
            YD_EQP_ID,
            YD_EQP_PAUSE_OCCR_SEQ,
            YD_EQP_PAUSE_PASS_HR_CARRYOV,
            YD_EQP_PAUSE_CODE,
            YD_EQP_PAUSE_OCC_DT,
            YD_EQP_PAUSE_OCC_WRK_DUTY,
            YD_EQP_PAUSE_OCC_WRK_PARTY,
            YD_EQP_PAUSE_END_DT,
            YD_EQP_PAUSE_END_WRK_DUTY,
            YD_EQP_PAUSE_PASS_HR,
            YD_EQP_PAUSE_RCVR_CNTS,
            REGISTER,
            REG_DDTT,
            MODIFIER,
            MOD_DDTT,
            DEL_YN
            ) 
            VALUES (
            :V_YD_EQP_ID,   
            TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||YM_EQUIPDOWN_SEQ.NEXTVAL,
            NULL,  --복구시점에 등록됨
            :V_YD_EQP_PAUSE_CODE,
            TO_DATE(:V_YD_EQP_PAUSE_OCC_DT,'YYYYMMDDHH24MISS'),     --날짜등록 
            :V_YD_EQP_PAUSE_OCC_WRK_DUTY,     --근조계산공통유틸사용(1,2,3)
            :V_YD_EQP_PAUSE_OCC_WRK_PARTY,     --근무조계산공통유틸사용(A,B,C,D)
            NULL,  --복구시점에 등록됨
            NULL,  --복구시점에 등록됨
            NULL,  --복구시점에 등록됨
            :V_YD_EQP_PAUSE_RCVR_CNTS,     --처리내용 
            :V_MODIFIER,
            SYSDATE,
            :V_MODIFIER,
            SYSDATE,
            'N'
            )
 </pre> */
public final static String insEqpTrblReg = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insEqpTrblReg";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnSchProgStatMsgNo
UPDATE TB_YF_CRNSCH  
   SET YD_WRK_PROG_REQ_MSG	= :V_YD_WRK_PROG_REQ_MSG
      ,ERR_CD				= :V_ERR_CD
      ,YD_CRN_SCH_ID_OLD	= :V_YD_CRN_SCH_ID_OLD
 WHERE YD_CRN_SCH_ID       	= :V_YD_CRN_SCH_ID
 </pre> */
public final static String updYdCrnSchProgStatMsgNo = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnSchProgStatMsgNo";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.acoilTcarDistCarldjl.updSchCnclYn 
UPDATE TB_YF_WRKBOOK
   SET SCH_CNCL_YN = 'Y'
     , SCH_CANCLE_MENT = :V_SCH_CANCLE_MENT
     , MODIFIER    = :V_MODIFIER
     , MOD_DDTT    = SYSDATE
 WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID

 </pre> */
public final static String updSchCnclYn = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.acoilTcarDistCarldjl.updSchCnclYn";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updEqpStat
--설비 상태 수정 
UPDATE TB_YF_EQP
   SET MODIFIER         = :V_MODIFIER
      ,MOD_DDTT         = SYSDATE
      ,YD_EQP_PROG_STAT = DECODE(YD_EQP_PROG_STAT, 'B', YD_EQP_PROG_STAT, :V_YD_EQP_PROG_STAT)
 WHERE YD_EQP_ID        = :V_YD_EQP_ID
   AND DEL_YN           = 'N'
 </pre> */
public final static String updEqpStat = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updEqpStat";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updClearScrapLyrByCol  
UPDATE TB_YF_STKLYR
   SET STL_NO           = NULL
     , YD_STK_LYR_STAT  = 'E'
     , MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
 WHERE YD_STK_COL_GP LIKE '1ESC'||:V_COL_NO    
   AND YD_STK_COL_GP||YD_STK_BED_NO IN (SELECT CD_GP||ITEM
                                        FROM TB_YF_RULE 
                                       WHERE REPR_CD_GP = 'SCRAP'
                                         AND DTL_ITEM1 LIKE :V_AREA_GP)
   AND YD_STK_LYR_STAT = 'C'
 </pre> */
public final static String updClearScrapLyrByCol = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updClearScrapLyrByCol";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnWrk
MERGE INTO TB_YF_CRNSCH CS USING (
    SELECT SL.YD_STK_LYR_X_AXIS     AS YD_DN_WO_LOC_XAXIS
         , SB.YD_STK_BED_XAXIS_TOL  AS YD_DN_WO_XAXIS_GAP_MAX
         , SB.YD_STK_BED_XAXIS_TOL  AS YD_DN_WO_XAXIS_GAP_MIN
         , SL.YD_STK_LYR_Y_AXIS     AS YD_DN_WO_LOC_YAXIS
         , SB.YD_STK_BED_YAXIS_TOL  AS YD_DN_WO_YAXIS_GAP_MAX 
         , SB.YD_STK_BED_YAXIS_TOL  AS YD_DN_WO_YAXIS_GAP_MIN 
         , SL.YD_STK_LYR_Z_AXIS     AS YD_DN_WO_LOC_ZAXIS
         , SB.YD_STK_BED_ZAXIS_TOL  AS YD_DN_WO_ZAXIS_GAP_MAX 
         , SB.YD_STK_BED_ZAXIS_TOL  AS YD_DN_WO_ZAXIS_GAP_MIN 
         , SC.ROTATION_ANGLE        AS DOWN_ROTATION_ANGLE
         , :V_YD_CRN_SCH_ID         AS YD_CRN_SCH_ID
      FROM TB_YF_STKCOL SC
         , TB_YF_STKBED SB
         , TB_YF_STKLYR SL
     WHERE SC.YD_STK_COL_GP = SB.YD_STK_COL_GP
       AND SB.YD_STK_COL_GP = SL.YD_STK_COL_GP
       AND SB.YD_STK_BED_NO = SL.YD_STK_BED_NO
       AND SL.YD_STK_COL_GP   = SUBSTR(:V_YD_DN_WO_LOC,1,6)
       AND SL.YD_STK_BED_NO   = SUBSTR(:V_YD_DN_WO_LOC,7,2)
       AND SL.YD_STK_LYR_NO = :V_YD_DN_WO_LYR
       
) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)

WHEN MATCHED THEN UPDATE SET
       MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,YD_DN_WO_LOC             = NVL(:V_YD_DN_WO_LOC           ,YD_DN_WO_LOC)
      ,YD_DN_WO_LYR           = NVL(:V_YD_DN_WO_LYR         ,YD_DN_WO_LYR)
      ,YD_DN_WO_LOC_XAXIS       = NVL(DD.YD_DN_WO_LOC_XAXIS     ,YD_DN_WO_LOC_XAXIS)
      ,YD_DN_WO_XAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_XAXIS_GAP_MAX ,YD_DN_WO_XAXIS_GAP_MAX)
      ,YD_DN_WO_XAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_XAXIS_GAP_MIN ,YD_DN_WO_XAXIS_GAP_MIN)
      ,YD_DN_WO_LOC_YAXIS       = NVL(DD.YD_DN_WO_LOC_YAXIS     ,YD_DN_WO_LOC_YAXIS)
      ,YD_DN_WO_YAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_YAXIS_GAP_MAX ,YD_DN_WO_YAXIS_GAP_MAX)
      ,YD_DN_WO_YAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_YAXIS_GAP_MIN ,YD_DN_WO_YAXIS_GAP_MIN)
      ,YD_DN_WO_LOC_ZAXIS       = NVL(DD.YD_DN_WO_LOC_ZAXIS     ,YD_DN_WO_LOC_ZAXIS)
      ,YD_DN_WO_ZAXIS_GAP_MAX   = NVL(DD.YD_DN_WO_ZAXIS_GAP_MAX ,YD_DN_WO_ZAXIS_GAP_MAX)
      ,YD_DN_WO_ZAXIS_GAP_MIN   = NVL(DD.YD_DN_WO_ZAXIS_GAP_MIN ,YD_DN_WO_ZAXIS_GAP_MIN)
      ,YD_L2_REQUEST_STAT       = :V_YD_L2_REQUEST_STAT 
      ,YD_DN_WO_LOC_TO          = :V_YD_DN_WO_LOC_TO
      ,YD_WRK_PROG_STAT         = :V_YD_WRK_PROG_STAT
      ,YD_WORD_DT               = SYSDATE
      ,DOWN_ROTATION_ANGLE      = NVL(DD.DOWN_ROTATION_ANGLE    ,DOWN_ROTATION_ANGLE)
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updYdCrnWrk = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnWrk";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilJspSeEJB.updCoilCarPointYnReg 
UPDATE TB_YF_RULE
   SET ITEM1      = :V_PRE_SUP_YN
     , MODIFIER   = :V_MODIFIER
     , MOD_DDTT   = SYSDATE
 WHERE REPR_CD_GP ='J00002'
   AND CD_GP      = '*'
   AND ITEM       = '*'
   AND DEL_YN     = 'N'
 </pre> */
public final static String updCoilCarPointYnReg = "bak.com.inisteel.cim.yf.acoil.dao.ACoilJspSeEJB.updCoilCarPointYnReg";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.clearStackLayer
UPDATE TB_YF_STKLYR
   SET STL_NO = ''
      ,YD_STK_LYR_STAT = 'E'
      ,MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
WHERE  STL_NO = :V_STL_NO
AND    YD_STK_COL_GP LIKE :V_YD_GP || '%'
 </pre> */
public final static String clearStackLayer = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.clearStackLayer";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarYdMsgNmClear
UPDATE TB_YF_CARSCHLOG
SET
    DEL_YN          = 'Y',
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updCarYdMsgNmClear = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarYdMsgNmClear";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YfCommonDAO.updateStackColUsageInfo
UPDATE TB_YF_STKCOL
   SET YD_STK_COL_USAGE_CD 	= :V_YD_STK_COL_USAGE_CD,
	   modifier             = 'SYSTEM',
	   mod_ddtt             = sysdate     
 WHERE YD_STK_COL_GP        = :V_YD_STK_COL_GP
 </pre> */
public final static String updateStackColUsageInfo = "bak.yf.facilitystatus.facilityinquiry.YfCommonDAO.updateStackColUsageInfo";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnWrkBookByTrnEqpCd
UPDATE TB_YF_WRKBOOK
SET
    DEL_YN      = :V_DEL_YN,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_WBOOK_ID IN
(
    SELECT DISTINCT
        C.YD_WBOOK_ID --하차대상
    FROM
        TB_YD_CARSCH A,
        TB_YD_CARFTMVMTL B,
        TB_YF_WRKBOOK C,
        TB_YF_WRKBOOKMTL D
    WHERE 1=1
    AND A.TRN_EQP_CD    = :V_TRN_EQP_CD
    AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
    AND A.DEL_YN        = 'N'
    AND B.STL_NO        = D.STL_NO
    AND D.DEL_YN        = 'N'
    AND D.YD_WBOOK_ID   = C.YD_WBOOK_ID
    AND C.DEL_YN        = 'N'
    AND C.YD_SCH_CD LIKE SUBSTR(C.YD_SCH_CD, 1, 2) || 'PT__LM%' --하차스케줄

    UNION

    SELECT DISTINCT
        C.YD_WBOOK_ID --상차대상
    FROM
        TB_YD_CARSCH A,
        TB_YF_STOCK B,
        TB_YF_WRKBOOK C,
        TB_YF_WRKBOOKMTL D
    WHERE 1=1
    AND A.TRN_EQP_CD    = :V_TRN_EQP_CD
    AND A.FRTOMOVE_WORD_NO  = B.FRTOMOVE_WORD_NO
    AND A.DEL_YN        = 'N'
    AND B.STL_NO        = D.STL_NO
    AND D.DEL_YN        = 'N'
    AND D.YD_WBOOK_ID   = C.YD_WBOOK_ID
    AND C.DEL_YN        = 'N'
    AND C.YD_SCH_CD LIKE SUBSTR(C.YD_SCH_CD, 1, 2) || 'PT__UM%' --상차스케줄
)
 </pre> */
public final static String updDelYnWrkBookByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnWrkBookByTrnEqpCd";

 /** <pre> 
-- com.inisteel.cim.yf.aslab.dao.ASlabDAO.updCreSchOprnStat 
UPDATE TB_YF_CRNSCH
   SET YD_WRK_PROG_STAT = :V_YD_WRK_PROG_STAT
      ,MODIFIER         = :V_MODIFIER
      ,MOD_DDTT         = SYSDATE
WHERE YD_CRN_SCH_ID     = :V_YD_CRN_SCH_ID
  AND DEL_YN            = 'N'
 </pre> */
public final static String updCreSchOprnStat = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updCreSchOprnStat";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updZoneCd  
UPDATE TB_YF_STKCOL
   SET MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,YD_ZONE_GP = :V_YD_ZONE_GP
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String updZoneCd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updZoneCd";

 /** <pre> 
--yf.tsinfo.CarpoiontReset_Col
update TB_YF_STKCOL
set YD_CAR_USE_GP = 'L'
    ,TRN_EQP_CD = ''
    ,CAR_CARD_NO = ''
    ,CARD_NO = ''
where YD_STK_COL_GP = :V_YD_STK_COL_GP
 </pre> */
public final static String CarpoiontReset_Col = "bak.yf.tsinfo.CarpoiontReset_Col";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateMatlFtmvWlrstCoil
--코일공통 업데이트(구내이송 하차완료)
UPDATE TB_PT_COILCOMM
SET( 
    CURR_PROG_CD_REG_PGM,   -- 현재진도코드 PGM
    CURR_PROG_REG_DDTT,     -- 현재진도코드등록일시
    CURR_PROG_CD,           -- 현재진도코드
    BEFO_PROG_CD_REG_PGM,   -- 전 진도코드 PGM
    BEFO_PROG_REG_DDTT,     -- 전 진도코드등록일시
    BEFO_PROG_CD,           -- 전 진도코드
    BEFOBEFO_PROG_CD_REG_PGM,
    BEFOBEFO_PROG_REG_DDTT,
    BEFOBEFO_PROG_CD
   )=
   (
    SELECT 'ydcallStartLastWo'
         , SYSDATE
         , DECODE(A.STL_APPEAR_GP, 'Y', A.CURR_PROG_CD,(
              CASE 
                WHEN B.TO_CURR_PROG_CD IS NOT NULL AND A.CURR_PROG_CD='E' THEN TO_CURR_PROG_CD
                WHEN A.ORD_YEOJAE_GP = '1'  AND A.CURR_PROG_CD='E' THEN 'B' 
                WHEN A.ORD_YEOJAE_GP <>'1'  AND A.CURR_PROG_CD='E' THEN 'Y'
                ELSE A.CURR_PROG_CD
              END)) CURR_PROG_CD,
        A.CURR_PROG_CD_REG_PGM,
        A.CURR_PROG_REG_DDTT,
        A.CURR_PROG_CD,   
        A.BEFO_PROG_CD_REG_PGM,
        A.BEFO_PROG_REG_DDTT,
        A.BEFO_PROG_CD
    FROM  USRPTA.TB_PT_COILCOMM A
         ,(SELECT *
             FROM USRPTA.TB_PT_STLFRTOMOVE AA
            WHERE AA.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO) 
                                          FROM USRPTA.TB_PT_STLFRTOMOVE C
                                         WHERE C.STL_NO=AA.STL_NO )
           )B
    WHERE A.COIL_NO = B.STL_NO(+)
      AND A.COIL_NO = :V_COIL_NO
    )
 WHERE COIL_NO = :V_COIL_NO

 </pre> */
public final static String updateMatlFtmvWlrstCoil = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateMatlFtmvWlrstCoil";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnWrkBookMtlByTrnEqpCd
UPDATE TB_YF_WRKBOOKMTL
SET
    DEL_YN      = :V_DEL_YN,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_WBOOK_ID IN
(
    SELECT DISTINCT
        C.YD_WBOOK_ID --하차대상
    FROM
        TB_YD_CARSCH A,
        TB_YD_CARFTMVMTL B,
        TB_YF_WRKBOOK C,
        TB_YF_WRKBOOKMTL D
    WHERE 1=1
    AND A.TRN_EQP_CD    = :V_TRN_EQP_CD
    AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
    AND A.DEL_YN        = 'N'
    AND B.STL_NO        = D.STL_NO
    AND D.DEL_YN        = 'N'
    AND D.YD_WBOOK_ID   = C.YD_WBOOK_ID
    AND C.DEL_YN        = 'N'
    AND C.YD_SCH_CD LIKE SUBSTR(C.YD_SCH_CD, 1, 2) || 'PT__LM%' --하차스케줄

    UNION

    SELECT DISTINCT
        C.YD_WBOOK_ID --상차대상
    FROM
        TB_YD_CARSCH A,
        TB_YF_STOCK B,
        TB_YF_WRKBOOK C,
        TB_YF_WRKBOOKMTL D
    WHERE 1=1
    AND A.TRN_EQP_CD    = :V_TRN_EQP_CD
    AND A.FRTOMOVE_WORD_NO  = B.FRTOMOVE_WORD_NO
    AND A.DEL_YN        = 'N'
    AND B.STL_NO        = D.STL_NO
    AND D.DEL_YN        = 'N'
    AND D.YD_WBOOK_ID   = C.YD_WBOOK_ID
    AND C.DEL_YN        = 'N'
    AND C.YD_SCH_CD LIKE SUBSTR(C.YD_SCH_CD, 1, 2) || 'PT__UM%' --상차스케줄
)
 </pre> */
public final static String updDelYnWrkBookMtlByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnWrkBookMtlByTrnEqpCd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delYDStkBook
UPDATE TB_YD_WRKBOOK
SET
    DEL_YN      = 'Y'
WHERE 1=1
AND YD_WBOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String delYDStkBook = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delYDStkBook";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delYMStkBookDtl
UPDATE TB_YF_WRKBOOKMTL
SET
    DEL_YN      = 'Y',
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_WBOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String delYMStkBookDtl = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delYMStkBookDtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateStackLayerStatMark_empty
UPDATE TB_YF_STKLYR
SET
    YD_STK_LYR_ACTIVE_STAT = :V_YD_STK_LYR_ACTIVE_STAT,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_STK_COL_GP = 
(
    SELECT
        YD_STK_COL_GP 
    FROM
        TB_YF_STKCOL
    WHERE 1=1
    AND TRN_EQP_CD = :V_TRN_EQP_CD
)
 </pre> */
public final static String updateStackLayerStatMark_empty = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateStackLayerStatMark_empty";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delCarschID
UPDATE TB_YD_CARSCH
SET
    DEL_YN          = 'Y',
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
 </pre> */
public final static String delCarschID = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delCarschID";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgt1Auto
UPDATE TB_YF_CRNSCH
   SET MODIFIER     = :V_MODIFIER
      ,MOD_DDTT     = SYSDATE
      ,YD_WRK_PROG_STAT =:V_YD_WRK_PROG_STAT
      ,YD_WORD_DT   = SYSDATE 
 WHERE YD_CRN_SCH_ID  = :V_YD_CRN_SCH_ID
   AND DEL_YN = 'N'
 </pre> */
public final static String updCrnWrkMgt1Auto = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgt1Auto";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnSchYdWrkProgStatAuto
UPDATE TB_YF_CRNSCH
   SET MODIFIER     = :V_MODIFIER
     , MOD_DDTT     = SYSDATE
     , YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
     , YD_WORD_DT   = CASE WHEN YD_WORD_DT IS NULL THEN SYSDATE ELSE YD_WORD_DT END 
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
   AND DEL_YN = 'N'
 </pre> */
public final static String updYdCrnSchYdWrkProgStatAuto = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnSchYdWrkProgStatAuto";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgtPriorWrkNext1Auto
UPDATE TB_YF_CRNSCH A
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN 'W' ELSE YD_WRK_PROG_STAT END)     
     , YD_WORD_DT       = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NULL ELSE YD_WORD_DT END) 
 WHERE YD_CRN_SCH_ID    = :V_OLD_YD_CRN_SCH_ID
   AND DEL_YN = 'N'
 </pre> */
public final static String updCrnWrkMgtPriorWrkNext1Auto = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgtPriorWrkNext1Auto";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdExamTransOrd
UPDATE USRYDA.TB_YD_EXAMINATIONCHKLIST
SET
    TRANS_ORD_DATE  = :V_NEW_TRANS_WORD_DATE,
    TRANS_ORD_SEQNO = :V_NEW_TRANS_WORD_SEQNO,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    DEL_YN          = 'N',
    CHECKING_YN     = 'N',
    LABEL_YN        = NULL,
    YD_AB_CD        = NULL
WHERE 1=1
AND TRANS_ORD_DATE  = :V_OLD_TRANS_WORD_DATE
AND TRANS_ORD_SEQNO = :V_OLD_TRANS_WORD_SEQNO
 </pre> */
public final static String updYdExamTransOrd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdExamTransOrd";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStockExpect
UPDATE TB_YF_STOCK A
SET
    EXPECT_YD_STK_LOC   = :V_YD_STK_COL_GP,
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE
WHERE 1=1
AND STL_NO              = :V_STL_NO
 </pre> */
public final static String updYdStockExpect = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStockExpect";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnSchDnWoLocTCarSch 
--크레인작업관리 권하위치변경 대차스케줄 수정 
UPDATE TB_YF_TCARSCH
   SET MODIFIER             = :V_MODIFIER
      ,MOD_DDTT             = SYSDATE
      ,YD_CARLD_WRK_BOOK_ID = NULL
 WHERE YD_CARLD_WRK_BOOK_ID = :V_YD_CARLD_WRK_BOOK_ID
   AND DEL_YN = 'N'
 </pre> */
public final static String updCrnSchDnWoLocTCarSch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnSchDnWoLocTCarSch";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCarYdMsgNm
INSERT INTO TB_YF_CARSCHLOG
(
    YF_CARSCHLOG_SEQ,
    YD_CAR_SCH_ID,
    YD_MSG_NM,
    REGISTER,
    REG_DDTT,
    DEL_YN
)
SELECT
    TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') || YF_CARSCHLOG_SEQ.NEXTVAL,
    :V_YD_CAR_SCH_ID,
    :V_YD_MSG_NM,
    :V_MODIFIER,
    SYSDATE,
    'N'
FROM
    DUAL
 </pre> */
public final static String insCarYdMsgNm = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCarYdMsgNm";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCTSToLoc
UPDATE TB_YF_CTS_SCH
SET YD_CARUD_WO_LOC = :V_YD_CARUD_WO_LOC
   ,YD_WORD_DT      = SYSDATE
   ,MODIFIER        = :V_MODIFIER
   ,MOD_DDTT        = SYSDATE
   ,YD_WRK_PROG_STAT = 'S'
WHERE YD_CTS_SCH_ID = :V_YD_CTS_SCH_ID
 </pre> */
public final static String updCTSToLoc = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCTSToLoc";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008UpStkLyrToCRForScr
UPDATE TB_YF_STKLYR
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , STL_NO         = (SELECT STL_NO FROM TB_YF_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)
     , YD_STK_LYR_STAT = :V_YD_STK_LYR_STAT
 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
   AND SUBSTR(YD_STK_COL_GP,3,2) = 'CR'
 </pre> */
public final static String updAxYML008UpStkLyrToCRForScr = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008UpStkLyrToCRForScr";

 /** <pre> 
--yf.tsinfo.updateLoadendLayer
insert into TB_YD_CARFTMVMTL
(
YD_CAR_SCH_ID
,STL_NO
,REGISTER
,REG_DDTT
,MODIFIER
,MOD_DDTT
,DEL_YN
,YD_STK_BED_NO
,YD_STK_LYR_NO
)
SELECT 
  B.YD_CAR_SCH_ID
, A.STL_NO
, 'yfbackup'
,sysdate
, 'yfbackup'
,sysdate
,'N'
,A.YD_STK_BED_NO
,'0'||A.YD_STK_LYR_NO
 FROM TB_YF_STKLYR A
    , TB_YD_CARSCH B
 WHERE A.STL_NO IS NOT NULL
   AND A.YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND A.YD_STK_COL_GP=B.YD_CARLD_STOP_LOC
   AND B.DEL_YN='N'
   AND B.YD_CAR_PROG_STAT IN ('1','2','3','4','5')
   AND B.YD_EQP_WRK_STAT='U'
   AND B.YD_CARLD_ARR_DT IS NOT NULL
   AND B.YD_CAR_SCH_ID=(SELECT MAX(YD_CAR_SCH_ID) FROM TB_YD_CARSCH C
                        WHERE C.TRN_EQP_CD=B.TRN_EQP_CD
                         AND C.DEL_YN='N')
 </pre> */
public final static String updateLoadendLayer = "bak.yf.tsinfo.updateLoadendLayer";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.YdEquipDAO.UpdateStackMaxQnty
        UPDATE TB_YF_EQP
		SET STK_MAX_QNTY = (SELECT COUNT(*)
                              FROM TB_YF_STKLYR
                             WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
                               AND YD_STK_LYR_ACTIVE_STAT = 'O')
		   ,MODIFIER = :V_MODIFIER
		   ,MOD_DDTT = SYSDATE
		WHERE YD_EQP_ID LIKE :V_YD_EQP_ID 
 </pre> */
public final static String UpdateStackMaxQnty = "bak.yf.facilitystatus.facilityinquiry.YdEquipDAO.UpdateStackMaxQnty";

 /** <pre> 
--yf.facilitywork.putwrecord.session.LyrStlNoReset
UPDATE TB_YF_STKLYR
   SET STL_NO = ''
 WHERE DEL_YN = 'N'
   AND STL_NO = :V_STL_NO
 </pre> */
public final static String LyrStlNoReset = "bak.yf.facilitywork.putwrecord.session.LyrStlNoReset";

 /** <pre> 
--yf.common.YfCommonDao.UpdateYdPointEqpCd
UPDATE TB_YD_CARPOINT
   SET TRN_EQP_CD = :V_TRN_EQP_CD
        ,YD_STK_COL_ACT_STAT = 'R'
 WHERE WLOC_CD = :V_WLOC_CD
   AND YD_PNT_CD = :V_YD_PNT_CD
 </pre> */
public final static String UpdateYdPointEqpCd = "bak.yf.common.YfCommonDao.UpdateYdPointEqpCd";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.insStockInfo
INSERT INTO TB_YF_STOCK
(
    STL_NO,
    STOCK_MOVE_TERM,
    CHARGE_LOT_NO,
    REGISTER,
    REG_DDTT,
    MODIFIER,
    MOD_DDTT,
    DEL_YN,
    STOCK_ITEM
)
VALUES
(
    :V_STL_NO,
    :V_STOCK_MOVE_TERM,
    '',
    :V_MODIFIER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N',
    'SM'    --SLAB소재
)
 </pre> */
public final static String insStockInfo = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.insStockInfo";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCarSchLd
INSERT INTO TB_YD_CARSCH
(
    YD_CAR_SCH_ID,
    REGISTER,
    REG_DDTT,
    MODIFIER,
    MOD_DDTT,
    DEL_YN,
    YD_EQP_ID,
    YD_CAR_USE_GP,
    TRN_EQP_CD,
    YD_EQP_WRK_STAT,
    SPOS_WLOC_CD,
    ARR_WLOC_CD,
    YD_CARLD_LEV_DT,        --야드상차출발일시
    YD_CARLD_PNT_WO_DT,     --야드상차포인트지시일시
    YD_PNT_CD1,             --야드상차포인트(4자리)
    YD_CARLD_WRK_BOOK_ID,   --야드상차작업예약ID
    YD_CARLD_STOP_LOC,      --야드상차정지위치(적치열6자리)
    YD_CAR_PROG_STAT        --야드차량진행상태
)
SELECT
    :V_YD_CAR_SCH_ID,
    :V_MODIFIER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N',
    (
        SELECT
            YD_EQP_ID
        FROM
            TB_YD_CARSPEC
        WHERE 1=1
        AND TRN_EQP_CD  = :V_TRN_EQP_CD
        AND DEL_YN      = 'N'
        AND ROWNUM      <= 1
    ) AS YD_EQP_ID,
    :V_YD_CAR_USE_GP,
    :V_TRN_EQP_CD,
    :V_YD_EQP_WRK_STAT,
    :V_SPOS_WLOC_CD,
    :V_ARR_WLOC_CD,
    SYSDATE,
    SYSDATE,
    :V_YD_PNT_CD,
    NVL(:V_YD_CARLD_WRK_BOOK_ID, ''),
    :V_YD_CARLD_STOP_LOC,
    :V_YD_CAR_PROG_STAT
FROM
    DUAL
 </pre> */
public final static String insCarSchLd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCarSchLd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008UpStkLyrToCR
UPDATE TB_YF_STKLYR
   SET MODIFIER            = :V_MODIFIER
     , MOD_DDTT            = SYSDATE
     , STL_NO            = :V_STL_NO
     , YD_STK_LYR_STAT = :V_YD_STK_LYR_STAT
 WHERE YD_STK_COL_GP   = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO   = :V_YD_STK_BED_NO
   AND YD_STK_LYR_NO = :V_YD_STK_LYR_NO
   AND SUBSTR(YD_STK_COL_GP,3,2) = 'CR'
 </pre> */
public final static String updAxYML008UpStkLyrToCR = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008UpStkLyrToCR";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.insYdCarsch
INSERT INTO USRYDA.TB_YD_CARSCH
(
    YD_CAR_SCH_ID,
    REGISTER,
    REG_DDTT,
    DEL_YN,
    YD_EQP_ID,
    YD_CAR_USE_GP,
    CAR_NO,
    TRN_EQP_CD,
    CAR_KIND,
    YD_EQP_WRK_STAT,
    SPOS_WLOC_CD,
    ARR_WLOC_CD,
    YD_CARLD_LEV_LOC,
    YD_CARLD_LEV_DT,
    YD_CARUD_LEV_DT,
    YD_PNT_CD1,
    YD_PNT_CD3,
    YD_CARLD_STOP_LOC,
    YD_CARUD_STOP_LOC,
    CARD_NO,
    YD_CAR_PROG_STAT,
    YD_CAR_WRK_GP,
    TRANS_ORD_DATE,
    TRANS_ORD_SEQNO,
    YD_BAYIN_WO_SEQ,
    TEL_NO,
    CMBN_CARLD_YN,
    WAIT_ARR_DDTT,
    WAIT_ARR_GP,
    TRANS_EQUIPMENT_TYPE,
    DRIVER_NAME
)
VALUES
(
    :V_YD_CAR_SCH_ID,
    :V_REGISTER,
    SYSDATE,
    'N',
    :V_YD_EQP_ID,
    :V_YD_CAR_USE_GP,
    :V_CAR_NO,
    :V_TRN_EQP_CD,
    :V_CAR_KIND,
    :V_YD_EQP_WRK_STAT,
    :V_SPOS_WLOC_CD,
    :V_ARR_WLOC_CD,
    :V_YD_CARLD_LEV_LOC,
    TO_DATE(:V_YD_CARLD_LEV_DT, 'YYYYMMDDHH24MISS'),
    TO_DATE(:V_YD_CARUD_LEV_DT, 'YYYYMMDDHH24MISS'),
    NVL(:V_YD_PNT_CD1, '0000'),
    NVL(:V_YD_PNT_CD3, '0000'),
    :V_YD_CARLD_STOP_LOC,
    :V_YD_CARUD_STOP_LOC,
    :V_CARD_NO,
    :V_YD_CAR_PROG_STAT,
    :V_YD_CAR_WRK_GP,
    :V_TRANS_ORD_DATE,
    :V_TRANS_ORD_SEQNO,
    :V_YD_BAYIN_WO_SEQ,
    :V_TEL_NO,
    :V_CMBN_CARLD_YN,
    :V_WAIT_ARR_DDTT,
    :V_WAIT_ARR_GP,
    :V_TRANS_EQUIPMENT_TYPE,
    :V_DRIVER_NAME
)
 </pre> */
public final static String insYdCarsch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insYdCarsch";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCarschYdCarRcptChkYn
UPDATE TB_YD_CARSCH 
SET
    YD_CAR_RCPT_CHK_YN  = :V_YD_CAR_RCPT_CHK_YN
WHERE 1=1
AND CAR_NO              = :V_CAR_NO
AND CARD_NO             = :V_CARD_NO
AND TRANS_ORD_DATE      = :V_TRANS_ORD_DATE
AND TRANS_ORD_SEQNO     = :V_TRANS_ORD_SEQNO
AND DEL_YN              = 'N'
 </pre> */
public final static String updYdCarschYdCarRcptChkYn = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCarschYdCarRcptChkYn";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkcol
UPDATE TB_YF_STKCOL
SET
    MOD_DDTT        = SYSDATE,
    MODIFIER        = :V_MODIFIER,
    YD_CAR_USE_GP   = :V_YD_CAR_USE_GP,
    TRN_EQP_CD      = :V_TRN_EQP_CD,
    CAR_NO          = :V_CAR_NO,
    CARD_NO         = :V_CARD_NO,
    YD_STK_COL_ACTIVE_STAT = :V_YD_STK_COL_ACTIVE_STAT
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
 </pre> */
public final static String updYdStkcol = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkcol";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnSchYdWrkProgStat
UPDATE TB_YF_CRNSCH
   SET YD_WRK_PROG_STAT = (CASE WHEN YD_WRK_PROG_STAT IN ('S','W','1') THEN NVL(:V_YD_WRK_PROG_STAT,'1') ELSE YD_WRK_PROG_STAT END)
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updYdCrnSchYdWrkProgStat = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCrnSchYdWrkProgStat";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008UpStkLyr
--크레인권상실적 적치단(권상위치) 
UPDATE TB_YF_STKLYR 
   SET MODIFIER            = :V_MODIFIER
     , MOD_DDTT            = SYSDATE
     , STL_NO            = NULL
     , YD_STK_LYR_STAT = 'E'
 WHERE STL_NO IN (SELECT STL_NO FROM TB_YF_CRNWRKMTL WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID)    
   AND YD_STK_LYR_STAT    IN ('C', 'U')
   AND SUBSTR(YD_STK_COL_GP,3,2) != 'CR'
 </pre> */
public final static String updAxYML008UpStkLyr = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008UpStkLyr";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnYsEqp 
--설비 상태 수정 
UPDATE TB_YF_EQP
   SET MODIFIER    = :V_MODIFIER
      ,MOD_DDTT    = SYSDATE
      ,YD_EQP_PROG_STAT  = :V_YD_EQP_PROG_STAT
 WHERE YD_EQP_ID    = :V_YD_EQP_ID
   AND DEL_YN      = 'N'
 </pre> */
public final static String updCrnYsEqp = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnYsEqp";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updClearScrapStockByCol
UPDATE TB_YF_STOCK
   SET DEL_YN   = 'Y'
     , MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
 WHERE STL_NO IN   (SELECT STL_NO
                      FROM TB_YF_STKLYR
                     WHERE YD_STK_COL_GP LIKE '1ESC'||:V_COL_NO
                       AND YD_STK_LYR_STAT IN ('C')
                       AND YD_STK_COL_GP||YD_STK_BED_NO IN (SELECT CD_GP||ITEM
                                                            FROM TB_YF_RULE 
                                                           WHERE REPR_CD_GP = 'SCRAP'
                                                             AND DTL_ITEM1 LIKE :V_AREA_GP)
                    )
 </pre> */
public final static String updClearScrapStockByCol = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updClearScrapStockByCol";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarSchInsSch
--대차스케줄 등록
MERGE INTO TB_YF_TCARSCH TS USING (
SELECT :V_YD_TCAR_SCH_ID       AS YD_TCAR_SCH_ID
      ,:V_MODIFIER             AS MODIFIER
      ,SYSDATE                 AS MOD_DDTT
      ,'N'                     AS DEL_YN
      ,:V_YD_EQP_ID            AS YD_EQP_ID
      ,'U'                     AS YD_EQP_WRK_STAT     --공차
      ,:V_YD_CAR_PROG_STAT     AS YD_CAR_PROG_STAT
      ,:V_YD_CARLD_WRK_BOOK_ID AS YD_CARLD_WRK_BOOK_ID
      ,:V_YD_CARLD_LEV_LOC     AS YD_CARLD_LEV_LOC
      ,:V_YD_CARLD_STOP_LOC    AS YD_CARLD_STOP_LOC
      ,:V_YD_CARUD_STOP_LOC    AS YD_CARUD_STOP_LOC
      ,'6'                     AS YD_CARLD_SCH_REQ_GP --공대차도착
      ,'3'                     AS YD_CARUD_SCH_REQ_GP --영대차도착
  FROM DUAL
) DD ON (TS.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID)
WHEN MATCHED THEN UPDATE SET
	 TS.MODIFIER             = DD.MODIFIER
    ,TS.MOD_DDTT             = DD.MOD_DDTT
    ,TS.YD_EQP_WRK_STAT      = DD.YD_EQP_WRK_STAT
    ,TS.YD_CAR_PROG_STAT     = DD.YD_CAR_PROG_STAT
    ,TS.YD_CARLD_WRK_BOOK_ID = DD.YD_CARLD_WRK_BOOK_ID
    ,TS.YD_CARLD_LEV_LOC     = DD.YD_CARLD_LEV_LOC
    ,TS.YD_CARLD_STOP_LOC    = DD.YD_CARLD_STOP_LOC
    ,TS.YD_CARUD_STOP_LOC    = DD.YD_CARUD_STOP_LOC
    ,TS.YD_CARLD_SCH_REQ_GP  = DD.YD_CARLD_SCH_REQ_GP
    ,TS.YD_CARUD_SCH_REQ_GP  = DD.YD_CARUD_SCH_REQ_GP
WHEN NOT MATCHED THEN
INSERT (TS.YD_TCAR_SCH_ID   , TS.REGISTER            , TS.REG_DDTT           , TS.MODIFIER         ,
        TS.MOD_DDTT         , TS.DEL_YN              , TS.YD_EQP_ID          , TS.YD_EQP_WRK_STAT  ,
        TS.YD_CAR_PROG_STAT , TS.YD_CARLD_WRK_BOOK_ID, TS.YD_CARLD_LEV_LOC   , TS.YD_CARLD_STOP_LOC,
        TS.YD_CARUD_STOP_LOC, TS.YD_CARLD_SCH_REQ_GP , TS.YD_CARUD_SCH_REQ_GP)
VALUES (DD.YD_TCAR_SCH_ID   , DD.MODIFIER            , DD.MOD_DDTT           , DD.MODIFIER         ,
        DD.MOD_DDTT         , DD.DEL_YN              , DD.YD_EQP_ID          , DD.YD_EQP_WRK_STAT  ,
        DD.YD_CAR_PROG_STAT , DD.YD_CARLD_WRK_BOOK_ID, DD.YD_CARLD_LEV_LOC   , DD.YD_CARLD_STOP_LOC,
        DD.YD_CARUD_STOP_LOC, DD.YD_CARLD_SCH_REQ_GP , DD.YD_CARUD_SCH_REQ_GP)

 </pre> */
public final static String updTcarSchInsSch = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarSchInsSch";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML005TcarMtlDel
--크레인권상실적 대차이송재료 삭제  
MERGE INTO TB_YF_TCARFTMVMTL TM USING (
SELECT :V_YD_TCAR_SCH_ID AS YD_TCAR_SCH_ID
     , STL_NO
  FROM TB_YF_CRNWRKMTL CM
 WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
   AND DEL_YN        = 'N'
) DD ON (TM.YD_TCAR_SCH_ID = DD.YD_TCAR_SCH_ID AND TM.STL_NO = DD.STL_NO)
WHEN MATCHED THEN UPDATE SET
	 TM.MODIFIER = :V_MODIFIER
    ,TM.MOD_DDTT = SYSDATE
    ,TM.DEL_YN   = 'Y'
 </pre> */
public final static String updAxYML005TcarMtlDel = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML005TcarMtlDel";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnReschWrkBook
--크레인리스케줄 작업예약 수정 
MERGE INTO TB_YF_WRKBOOK WB USING (
SELECT SR.YD_SCH_CD
      ,DD.MODIFIER
      ,DECODE(DD.BR_GP,'B',SR.YD_ALT_CRN_PRIOR,SR.YD_WRK_CRN_PRIOR) AS YD_SCH_PRIOR
  FROM TB_YF_SCHRULE SR
      ,(SELECT :V_YD_EQP_ID AS YD_EQP_ID
              ,:V_MODIFIER  AS MODIFIER
              ,:V_BR_GP     AS BR_GP --고장복구구분
          FROM DUAL) DD
 WHERE SR.YD_GP       = SUBSTR(DD.YD_EQP_ID,1,1)
   AND SR.YD_BAY_GP   = SUBSTR(DD.YD_EQP_ID,2,1)
   AND (SR.YD_WRK_CRN = DD.YD_EQP_ID OR SR.YD_ALT_CRN = DD.YD_EQP_ID)
   AND SR.DEL_YN      = 'N'
) DD ON (WB.YD_SCH_CD = DD.YD_SCH_CD AND WB.DEL_YN = 'N')
WHEN MATCHED THEN UPDATE SET
	 WB.MODIFIER     = DD.MODIFIER
    ,WB.MOD_DDTT     = SYSDATE
    ,WB.YD_SCH_PRIOR = NVL(DD.YD_SCH_PRIOR,WB.YD_SCH_PRIOR)
 </pre> */
public final static String updCrnReschWrkBook = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnReschWrkBook";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateMoveTermOfStock
UPDATE TB_YF_STOCK
SET 
    STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM,
    CHARGE_LOT_NO   = NULL,
    MODIFIER        = 'SYSTEM',
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updateMoveTermOfStock = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateMoveTermOfStock";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo
UPDATE TB_YF_WRKBOOK
SET
    DEL_YN      = 'Y',
    MODIFIER    = 'SYSTEM'
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_WBOOK_ID = :YD_WBOOK_ID
 </pre> */
public final static String deleteWbookInfo = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";

 /** <pre> 
--yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo2
UPDATE TB_YF_WRKBOOKMTL
SET
    DEL_YN      = 'Y',
    MODIFIER    = 'SYSTEM'
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_WBOOK_ID = :YD_WBOOK_ID
 </pre> */
public final static String deleteWbookInfo2 = "bak.yf.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo2";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnCarFtMvMtlByTrnEqpCd
UPDATE TB_YD_CARFTMVMTL
SET
    DEL_YN      = :V_DEL_YN,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_CAR_SCH_ID IN
(
    SELECT DISTINCT
        YD_CAR_SCH_ID
    FROM
        TB_YD_CARSCH
    WHERE 1=1
    AND TRN_EQP_CD  = :V_TRN_EQP_CD
    AND DEL_YN      = 'N'
)
 </pre> */
public final static String updDelYnCarFtMvMtlByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnCarFtMvMtlByTrnEqpCd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnCarSchByTrnEqpCd
UPDATE TB_YD_CARSCH
SET
    DEL_YN      = :V_DEL_YN,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND YD_CAR_SCH_ID IN
(
    SELECT DISTINCT
        YD_CAR_SCH_ID
    FROM
        TB_YD_CARSCH
    WHERE 1=1
    AND TRN_EQP_CD  = :V_TRN_EQP_CD
    AND DEL_YN      = 'N'
)
 </pre> */
public final static String updDelYnCarSchByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnCarSchByTrnEqpCd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CarSchUdWbId
UPDATE TB_YD_CARSCH TS 
   SET TS.MODIFIER             = :V_MODIFIER
     , TS.MOD_DDTT             = SYSDATE
     , TS.YD_CARUD_WRK_BOOK_ID = :V_YD_WBOOK_ID
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updAxYML008CarSchUdWbId = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CarSchUdWbId";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.deleteCTSSch
--CTS스케줄 삭제 
UPDATE TB_YF_CTS_SCH 
SET MODIFIER        = :V_MODIFIER
  , MOD_DDTT        = SYSDATE
  , DEL_YN          = 'Y'
WHERE YD_WRK_PROG_STAT = '4'
  AND DEL_YN        = 'N'
  AND STL_NO        = :V_STL_NO
 </pre> */
public final static String deleteCTSSch = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.deleteCTSSch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCarsch
UPDATE TB_YD_CARSCH
SET
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    DEL_YN                  = NVL(:V_DEL_YN, DEL_YN),
    --하차
    YD_CAR_PROG_STAT        = NVL(:V_YD_CAR_PROG_STAT, YD_CAR_PROG_STAT),
    YD_CARUD_WRK_BOOK_ID    = NVL(:V_YD_CARUD_WRK_BOOK_ID, YD_CARUD_WRK_BOOK_ID),
    YD_CARUD_STOP_LOC       = NVL(:V_YD_CARUD_STOP_LOC, YD_CARUD_STOP_LOC),
    YD_CARUD_ARR_DT         = NVL(TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS'), YD_CARUD_ARR_DT),
    YD_PNT_CD3              = NVL(:V_YD_PNT_CD3, YD_PNT_CD3),
    --상차
    YD_CARLD_WRK_BOOK_ID    = NVL(:V_YD_CARLD_WRK_BOOK_ID, YD_CARLD_WRK_BOOK_ID),
    YD_CARLD_STOP_LOC       = NVL(:V_YD_CARLD_STOP_LOC, YD_CARLD_STOP_LOC),
    YD_CARLD_ARR_DT         = NVL(TO_DATE(:V_YD_CARLD_ARR_DT,'YYYYMMDDHH24MISS'),YD_CARLD_ARR_DT),
    YD_PNT_CD1              = NVL(:V_YD_PNT_CD1, YD_PNT_CD1)
WHERE 1=1
AND YD_CAR_SCH_ID           = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updYdCarsch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCarsch";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStockTrnsOrd
UPDATE USRYFA.TB_YF_STOCK
SET
    DEL_YN          = :V_DEL_YN,
    STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO IN
(
    SELECT
        A.STL_NO
    FROM
        TB_YF_STOCK A,
        TB_YF_STKLYR B,
        TB_YD_CARPOINT C
    WHERE 1=1
    AND A.STL_NO            = B.STL_NO
    AND A.TRANS_ORD_DATE    = :V_TRANS_ORD_DATE
    AND A.TRANS_ORD_SEQNO   = :V_TRANS_ORD_SEQNO
    AND SUBSTR(A.EXPECT_YD_STK_LOC, 3, 2) BETWEEN YD_SPAN_FROM  AND YD_SPAN_TO
    AND SUBSTR(A.EXPECT_YD_STK_LOC, 2, 1) = SUBSTR(C.YD_STK_COL_GP, 2, 1)
    AND C.YD_STK_COL_GP LIKE :V_YD_STK_COL_GP
    AND B.YD_STK_LYR_STAT IN ('U', 'C')
)
 </pre> */
public final static String updYfStockTrnsOrd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYfStockTrnsOrd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updSchLocSrchZoneTitle
UPDATE TB_YF_RULE
   SET REPR_CD_CONTENTS = :V_REPR_CD_CONTENTS
WHERE REPR_CD_GP || CD_GP || ITEM = :V_VER_ID 
      AND DEL_YN = 'N'  
 </pre> */
public final static String updSchLocSrchZoneTitle = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updSchLocSrchZoneTitle";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updPlnInfoReSet
UPDATE TB_YD_CARPOINT
SET
    YD_STK_COL_ACT_STAT = 'C',
    TRN_EQP_CD          = '',
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE
WHERE 1=1
AND TRN_EQP_CD          = :V_TRN_EQP_CD
AND YD_STK_COL_ACT_STAT = 'R'
 </pre> */
public final static String updPlnInfoReSet = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updPlnInfoReSet";

 /** <pre> 
--com.inisteel.cim.ym.bcommon.dao.YmCommDAO.updateLayerstat_01
UPDATE TB_YF_STKCOL
SET
    YD_CAR_USE_GP   = :V_YD_CAR_USE_GP,
    TRN_EQP_CD      = :V_TRN_EQP_CD,
    CAR_NO          = :V_CAR_NO,
    CARD_NO         = :V_CARD_NO
WHERE 1=1
AND WLOC_CD         = :V_WLOC_CD
AND YD_PNT_CD       = :V_YD_PNT_CD
AND SECT_GP         = 'PT'
 </pre> */
public final static String updateLayerstat_01 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_01";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updStockTransInfo_05
UPDATE TB_YF_STOCK
SET
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE,
    STOCK_MOVE_TERM     = :V_STOCK_MOVE_TERM,
    FRTOMOVE_WORD_NO    = :V_FRTOMOVE_WORD_NO
WHERE 1=1
AND STL_NO      = :V_STL_NO
 </pre> */
public final static String updStockTransInfo_05 = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updStockTransInfo_05";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchRuleInfo 
-- 크레인스케줄기준 수정
UPDATE TB_YF_SCHRULE
   SET MODIFIER              = :V_MODIFIER             --수정자
     , MOD_DDTT              = SYSDATE                 --수정일
     , YD_SCH_RNG_CD         = :V_YD_SCH_RNG_CD        --스케줄범위코드
     , YD_SCH_WHIO_GP        = :V_YD_SCH_WHIO_GP       --스케줄입출고구분
     , YD_SCH_RULE_ACT_STAT  = :V_YD_SCH_RULE_ACT_STAT --스케줄기준활성상태
     , YD_WRK_CRN            = :V_YD_WRK_CRN           --작업크레인
     , YD_WRK_CRN_PRIOR      = :V_YD_WRK_CRN_PRIOR     --작업크레인우선순위
     , YD_ALT_CRN_YN         = :V_YD_ALT_CRN_YN        --대체크레인유무
     , YD_ALT_CRN            = :V_YD_ALT_CRN           --야드대체크레인
     , YD_ALT_CRN_PRIOR      = :V_YD_ALT_CRN_PRIOR     --대체크레인우선순위 ??
     , CD_CONTENTS           = :V_CD_CONTENTS          --코드설명
     , YD_SCH_PROH_EXN       = :V_YD_SCH_PROH_EXN      --야드스케줄금지유무
     , DAN_PRIOR             = :V_DAN_PRIOR            --단우선순위
     , YD_SCH_AUTO_ST_YN     = :V_YD_SCH_AUTO_ST_YN    --야드스케줄자동기동여부
 WHERE YD_SCH_CD             = :V_YD_SCH_CD            --스케줄코드       
 </pre> */
public final static String updSchRuleInfo = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updSchRuleInfo";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updYfRule
UPDATE TB_YF_RULE
   SET DTL_ITEM1 = NVL(:V_DTL_ITEM1, DTL_ITEM1)
      ,DTL_ITEM2 = NVL(:V_DTL_ITEM2, DTL_ITEM2)
      ,DTL_ITEM3 = NVL(:V_DTL_ITEM3, DTL_ITEM3)
      ,DTL_ITEM4 = NVL(:V_DTL_ITEM4, DTL_ITEM4)
      ,DTL_ITEM5 = NVL(:V_DTL_ITEM5, DTL_ITEM5)
      ,DTL_ITEM6 = NVL(:V_DTL_ITEM6, DTL_ITEM6)
      ,DTL_ITEM7 = NVL(:V_DTL_ITEM7, DTL_ITEM7)
      ,DTL_ITEM8 = NVL(:V_DTL_ITEM8, DTL_ITEM8)
      ,DTL_ITEM9 = NVL(:V_DTL_ITEM9, DTL_ITEM9)
      ,DTL_ITEM10 = NVL(:V_DTL_ITEM10, DTL_ITEM10)
      ,MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
 WHERE REPR_CD_GP = :V_REPR_CD_GP
   AND CD_GP = :V_CD_GP
   AND ITEM = :V_ITEM
 </pre> */
public final static String updYfRule = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updYfRule";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStockDelYn
UPDATE TB_YF_STOCK
SET
    DEL_YN      = :V_DEL_YN,
    MODIFIER    = :V_MODIFIER,
    MOD_DDTT    = SYSDATE
WHERE 1=1
AND STL_NO      = :V_STL_NO
 </pre> */
public final static String updStockDelYn = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updStockDelYn";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockMoveTerm
UPDATE TB_YF_STOCK
SET
    STOCK_MOVE_TERM = :V_STOCK_MOVE_TERM,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO          = :V_STL_NO
 </pre> */
public final static String updStockMoveTerm = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updStockMoveTerm";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarSchDelMtl
-- 대차스케줄재료 삭제 
UPDATE TB_YF_TCARFTMVMTL
   SET MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,DEL_YN   = 'Y'
 WHERE YD_TCAR_SCH_ID = :V_YD_TCAR_SCH_ID
   AND DEL_YN   = 'N'
 </pre> */
public final static String updTcarSchDelMtl = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarSchDelMtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CarMtlDel
UPDATE TB_YD_CARFTMVMTL TM
   SET TM.MODIFIER = :V_MODIFIER
     , TM.MOD_DDTT = SYSDATE
     , TM.DEL_YN   = 'Y'
 WHERE TM.YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
   AND TM.STL_NO  IN (SELECT B.STL_NO
                          FROM TB_YF_CRNSCH A
                             , TB_YF_CRNWRKMTL B
                         WHERE A.YD_CRN_SCH_ID = B.YD_CRN_SCH_ID    
                           AND A.YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
                           AND A.DEL_YN        = 'N'
                           AND A.DEL_YN        = 'N')
 </pre> */
public final static String updAxYML008CarMtlDel = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CarMtlDel";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delYDStkBookDtl
UPDATE TB_YD_WRKBOOKMTL
SET
    DEL_YN      = 'Y'
WHERE 1=1
AND YD_WBOOK_ID = :V_YD_WBOOK_ID
 </pre> */
public final static String delYDStkBookDtl = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delYDStkBookDtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.delYDStkBookLoc
UPDATE TB_YD_STKCOL
SET
    YD_STK_COL_ACT_STAT = 'C',
    YD_CAR_USE_GP       = '',
    TRN_EQP_CD          = ''
WHERE 1=1
AND TRN_EQP_CD          = :V_TRN_EQP_CD
 </pre> */
public final static String delYDStkBookLoc = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.delYDStkBookLoc";

 /** <pre> 
--yf.tsinfo.updateLoadenddelete
  update TB_YD_CARSCH
   set YD_CARLD_ST_DT = ''
      , YD_CARLD_CMPL_DT = ''
      , YD_CAR_PROG_STAT = '2'
   where DEL_YN = 'N'
   and YD_CARLD_STOP_LOC = :V_YD_STK_COL_GP
   and YD_CAR_PROG_STAT in ('4','5')
 </pre> */
public final static String updateLoadenddelete = "bak.yf.tsinfo.updateLoadenddelete";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CarSchUdSchId
UPDATE USRYDA.TB_YD_CARSCH TS
   SET TS.MODIFIER         = :V_MODIFIER
     , TS.MOD_DDTT         = SYSDATE
     , TS.YD_EQP_WRK_SH    = :V_YD_EQP_WRK_SH
     , TS.YD_EQP_WRK_WT    = :V_YD_EQP_WRK_WT
     , TS.YD_CAR_PROG_STAT = :V_YD_CAR_PROG_STAT
     , TS.YD_CARUD_ST_DT   = NVL(TS.YD_CARUD_ST_DT,SYSDATE)
 WHERE TS.YD_CAR_SCH_ID    = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updAxYML008CarSchUdSchId = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CarSchUdSchId";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkLyrYdByCrnSchId
--기존 권하지시위치
UPDATE TB_YF_STKLYR
   SET STL_NO = NULL
     , YD_STK_LYR_STAT = 'E'
 WHERE STL_NO IN (SELECT STL_NO
                    FROM TB_YF_CRNWRKMTL
                   WHERE YD_CRN_SCH_ID = :V_YD_CRN_SCH_ID
                 )
   AND YD_STK_COL_GP = :V_YD_STK_COL_GP_OLD
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO_OLD   
   AND YD_STK_LYR_STAT = 'D'
 </pre> */
public final static String updYdStkLyrYdByCrnSchId = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkLyrYdByCrnSchId";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarftmvmtlToLyr
--반품,회송,부분하차시 차량 하차 위치(STKLYR)에 제품을 적치
MERGE INTO TB_YF_STKLYR TG USING
(
    SELECT
        A.YD_CARUD_STOP_LOC AS YD_STK_COL_GP,
        B.YD_STK_BED_NO     AS YD_STK_BED_NO,
        '01'                AS YD_STK_LYR_NO,
        B.STL_NO            AS STL_NO
    FROM
        TB_YD_CARSCH A,
        TB_YD_CARFTMVMTL B
    WHERE 1=1
    AND A.CAR_NO = :V_CAR_NO
    AND A.TRANS_ORD_DATE  = :V_TRANS_ORD_DT
    AND A.TRANS_ORD_SEQNO = :V_TRANS_ORD_SEQNO
    AND A.DEL_YN = 'N'
    AND B.DEL_YN = 'N'
    AND A.YD_CAR_SCH_ID = B.YD_CAR_SCH_ID
) DD
ON
(
    1=1
    AND TG.YD_STK_COL_GP = DD.YD_STK_COL_GP
    AND TG.YD_STK_BED_NO = DD.YD_STK_BED_NO
    AND TG.YD_STK_LYR_NO = DD.YD_STK_LYR_NO
)
WHEN MATCHED THEN
UPDATE
SET
   TG.STL_NO            = DD.STL_NO,
   TG.YD_STK_LYR_STAT   = 'C',
   TG.MODIFIER          = :V_MODIFIER,
   TG.MOD_DDTT          = SYSDATE
 </pre> */
public final static String updCarftmvmtlToLyr = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarftmvmtlToLyr";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarSchUdByTrnEqpCdNew
UPDATE  TB_YD_CARSCH A
SET
    MODIFIER                = :V_MODIFIER,
    MOD_DDTT                = SYSDATE,
    YD_CAR_PROG_STAT        = :V_YD_CAR_PROG_STAT,
    YD_CAR_USE_GP           = :V_YD_CAR_USE_GP,
    YD_EQP_WRK_STAT         = :V_YD_EQP_WRK_STAT,
    ARR_WLOC_CD             = :V_ARR_WLOC_CD,
    YD_PNT_CD3              = :V_YD_PNT_CD,
    YD_CARUD_STOP_LOC       = :V_YD_CARUD_STOP_LOC,
    YD_CARUD_WRK_BOOK_ID    = :V_YD_CARUD_WRK_BOOK_ID,
    YD_CARUD_LEV_DT         = SYSDATE,
    YD_CARUD_PNT_WO_DT      = SYSDATE,
    WAIT_ARR_GP             = :V_WAIT_ARR_GP
WHERE 1=1
AND TRN_EQP_CD              = :V_TRN_EQP_CD
AND DEL_YN                  = 'N'
AND A.YD_CAR_SCH_ID =
(
    SELECT
        MAX(YD_CAR_SCH_ID)
    FROM
        TB_YD_CARSCH B
    WHERE 1=1
    AND A.TRN_EQP_CD = B.TRN_EQP_CD
    AND B.DEL_YN = 'N'
)
 </pre> */
public final static String updCarSchUdByTrnEqpCdNew = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarSchUdByTrnEqpCdNew";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCarFtMvMtl 
INSERT INTO TB_YD_CARFTMVMTL(
       YD_CAR_SCH_ID
     , STL_NO
     , REGISTER
     , REG_DDTT
     , MODIFIER
     , MOD_DDTT
     , DEL_YN
     , YD_STK_BED_NO
     , YD_STK_LYR_NO
     ) 
VALUES ( 
       :V_YD_CAR_SCH_ID
     , :V_STL_NO
     , :V_MODIFIER
     , SYSDATE
     , :V_MODIFIER
     , SYSDATE
     , :V_DEL_YN
     , :V_YD_STK_BED_NO
     , :V_YD_STK_LYR_NO
)
 </pre> */
public final static String insCarFtMvMtl = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCarFtMvMtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_02
UPDATE TB_YF_STKLYR
SET
    STL_NO          = '',
    YD_STK_LYR_ACTIVE_STAT = NVL(:V_YD_STK_LYR_ACTIVE_STAT, YD_STK_LYR_ACTIVE_STAT),
    YD_STK_LYR_STAT = 'E',
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND YD_STK_COL_GP IN
(
    --개소코드와 차량번호로 적치열을 찾는다.
    SELECT
        YD_STK_COL_GP
    FROM
        USRYFA.TB_YF_STKCOL
    WHERE 1=1
    AND WLOC_CD = :V_WLOC_CD
    AND NVL(CARD_NO, TRN_EQP_CD) = :V_TRN_EQP_CD
)
 </pre> */
public final static String updateLayerstat_02 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_02";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updLayerStatByTrnEqpCd
UPDATE TB_YF_STKLYR
SET
    YD_STK_LYR_STAT = :V_YD_STK_LYR_STAT,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND STL_NO IN 
(
    SELECT 	--하차대상
        C.STL_NO
    FROM 
        TB_YD_CARSCH A,
        TB_YD_CARFTMVMTL B,
        TB_YF_STKLYR C
    WHERE 1=1
    AND A.YD_CAR_SCH_ID     = B.YD_CAR_SCH_ID
    AND B.STL_NO            = C.STL_NO
    AND A.TRN_EQP_CD        = :V_TRN_EQP_CD
    AND A.DEL_YN            = 'N' 
    
    UNION
    
    SELECT 	--상차대상
        C.STL_NO
    FROM
        TB_YD_CARSCH A,
        TB_YF_STOCK B,
        TB_YF_STKLYR C
    WHERE 1=1
    AND A.FRTOMOVE_WORD_NO  = B.FRTOMOVE_WORD_NO
    AND B.STL_NO            = C.STL_NO
    AND A.TRN_EQP_CD        = :V_TRN_EQP_CD
    AND A.DEL_YN            = 'N'
)
 </pre> */
public final static String updLayerStatByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updLayerStatByTrnEqpCd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarUseGpByTrnEqpCd
UPDATE TB_YF_STKCOL
SET
    TRN_EQP_CD      = '',
    YD_CAR_USE_GP   = '',
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND TRN_EQP_CD      = :V_TRN_EQP_CD
 </pre> */
public final static String updCarUseGpByTrnEqpCd = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCarUseGpByTrnEqpCd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_03
UPDATE TB_YF_STKCOL
SET
    YD_CAR_USE_GP   = NULL,
    TRN_EQP_CD      = NULL,
    CAR_NO          = NULL,
    CARD_NO         = NULL,
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE
WHERE 1=1
AND WLOC_CD         = :V_WLOC_CD
AND NVL(CARD_NO, TRN_EQP_CD) = :V_TRN_EQP_CD
 </pre> */
public final static String updateLayerstat_03 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateLayerstat_03";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgtPriorWrkNext1 

UPDATE TB_YF_CRNSCH A
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , YD_WRK_PROG_STAT = 'W'
     , YD_WORD_DT       = NULL 
     , YD_SCH_PRIOR     = (SELECT YD_WRK_CRN_PRIOR FROM USRYFA.TB_YF_SCHRULE B
                            WHERE B.YD_SCH_CD=A.YD_SCH_CD)
 
 WHERE YD_WBOOK_ID IN(SELECT YD_WBOOK_ID 
                        FROM TB_YF_CRNSCH C 
                       WHERE C.YD_CRN_SCH_ID= :V_YD_CRN_SCH_ID)
   AND DEL_YN = 'N' 
 </pre> */
public final static String updCrnWrkMgtPriorWrkNext1 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgtPriorWrkNext1";

 /** <pre> 
-- com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgtS 
UPDATE TB_YF_CRNSCH
   SET MODIFIER        = :V_MODIFIER
      ,MOD_DDTT        = SYSDATE
      ,YD_SCH_PRIOR    = TO_NUMBER(:V_YD_SCH_PRIOR)
      ,YD_EQP_ID       = NVL(:V_YD_EQP_ID,YD_EQP_ID)
      ,YD_WRK_PROG_STAT= 'S' 
      ,YD_WORD_DT      = SYSDATE
 WHERE YD_CRN_SCH_ID  = (SELECT MIN(YD_CRN_SCH_ID)
                           FROM TB_YF_CRNSCH
                          WHERE YD_WBOOK_ID = :V_YD_WBOOK_ID
                            AND DEL_YN = 'N'
                        )
   AND YD_WRK_PROG_STAT IN ('1','W','S')
   AND DEL_YN = 'N'  
 </pre> */
public final static String updCrnWrkMgtS = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updCrnWrkMgtS";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateStockTransInfo_01
UPDATE TB_YF_STOCK A
SET
    MODIFIER            = :V_MODIFIER,
    MOD_DDTT            = SYSDATE,
    STOCK_MOVE_TERM     = NVL(:V_STOCK_MOVE_TERM,   A.STOCK_MOVE_TERM),
    CAR_NO              = NVL(:V_CAR_NO,            A.CAR_NO),
    CAR_CARD_NO         = NVL(:V_CAR_CARD_NO,       A.CAR_CARD_NO),
    TRANS_ORD_DATE      = NVL(:V_TRANS_ORD_DATE,    A.TRANS_ORD_DATE),
    TRANS_ORD_SEQNO     = NVL(:V_TRANS_ORD_SEQNO,   A.TRANS_ORD_SEQNO),
    CTS_RELAY_BAY       = NVL(:V_CTS_RELAY_BAY,     A.CTS_RELAY_BAY),       --작업매수,
    CTS_RELAY_SADDLE    = NVL(:V_CTS_RELAY_SADDLE,  A.CTS_RELAY_SADDLE),    --방향(L,R)
    YD_CAR_UPP_LOC_CD   = NVL(LPAD(:V_YD_CAR_UPP_LOC_CD, 2, '0'), A.YD_CAR_UPP_LOC_CD)  --차상위치
WHERE 1=1
AND STL_NO              = :V_STL_NO
 </pre> */
public final static String updateStockTransInfo_01 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateStockTransInfo_01";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.insCarSch
INSERT INTO TB_YD_CARSCH(

       YD_CAR_SCH_ID
     , REGISTER
     , REG_DDTT
     , MODIFIER
     , MOD_DDTT
     , DEL_YN
     , YD_EQP_ID
     , YD_CAR_USE_GP
     , CAR_NO
     , CAR_KIND
     , YD_EQP_WRK_STAT      --U:상차, L:하차
     
     , SPOS_WLOC_CD
     , ARR_WLOC_CD
     , YD_CARLD_LEV_LOC     --야드상차출발위치
     , YD_CARLD_LEV_DT      --야드상차출발일시
     , YD_CARLD_PNT_WO_DT   --야드상차포인트지시일시
     , YD_PNT_CD1           --야드상차포인트(4자리)
     , YD_CARLD_WRK_BOOK_ID --야드상차작업예약ID
     , YD_CARLD_STOP_LOC    --야드상차정지위치(적치열6자리)
     , YD_CARLD_ARR_DT      --야드상차도착일시

     , YD_CARUD_LEV_DT      --야드하차출발일시
     , YD_CARUD_PNT_WO_DT   --야드하차Point지시일시
     , YD_PNT_CD3           --야드하차포인트(4자리)
     , YD_CARUD_WRK_BOOK_ID --야드하차작업예약ID
     , YD_CARUD_STOP_LOC    --야드하차정지위치(적치열6자리)
     , YD_CARUD_ARR_DT      --야드하차도착일시
     , YD_CAR_PROG_STAT     --야드차량진행상태
     , TRANS_ORD_DATE
     , TRANS_ORD_SEQNO 
     , YD_BAYIN_WO_SEQ
     , CARD_NO
) SELECT :V_YD_CAR_SCH_ID
       , :V_MODIFIER
       , SYSDATE
       , :V_MODIFIER
       , SYSDATE
       , 'N'
       , :V_YD_EQP_ID
       , :V_YD_CAR_USE_GP
       , :V_CAR_NO
       , :V_CAR_KIND
       , :V_YD_EQP_WRK_STAT
       , :V_SPOS_WLOC_CD
       , :V_ARR_WLOC_CD
       -- 상차정보
       , DECODE(:V_YD_EQP_WRK_STAT,'U',:V_YD_CARLD_LEV_LOC,NULL) --야드상차출발위치
       , DECODE(:V_YD_EQP_WRK_STAT,'U',SYSDATE,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'U',SYSDATE,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'U',:V_YD_PNT_CD1,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'U',:V_YD_CARLD_WRK_BOOK_ID,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'U',:V_YD_CARLD_STOP_LOC,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'U',SYSDATE,NULL)   --야드상차도착일시
       --하차정보
       , DECODE(:V_YD_EQP_WRK_STAT,'L',SYSDATE,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'L',SYSDATE,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'L',:V_YD_PNT_CD3,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'L',:V_YD_CARUD_WRK_BOOK_ID,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'L',:V_YD_CARUD_STOP_LOC,NULL)
       , DECODE(:V_YD_EQP_WRK_STAT,'L',SYSDATE,NULL)   --야드하차도착일시
       , :V_YD_CAR_PROG_STAT
       , :V_TRANS_ORD_DATE
       , :V_TRANS_ORD_SEQNO 
       , :V_YD_BAYIN_WO_SEQ
       , :V_CARD_NO
    FROM DUAL
 </pre> */
public final static String insCarSch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.insCarSch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCarschYD
UPDATE TB_YD_CARSCH
   SET MODIFIER         = :V_MODIFIER
     , MOD_DDTT         = SYSDATE
     , YD_CAR_PROG_STAT     = :V_YD_CAR_PROG_STAT  
     , YD_CARUD_STOP_LOC    = :V_YD_CARUD_STOP_LOC   
     , YD_CARUD_ARR_DT      = TO_DATE(:V_YD_CARUD_ARR_DT,'YYYYMMDDHH24MISS') 
     , ARR_WLOC_CD          = :V_ARR_WLOC_CD
     , YD_PNT_CD3           = :V_YD_PNT_CD3
     , YD_EQP_WRK_STAT      = :V_YD_EQP_WRK_STAT  
     
 WHERE YD_CAR_SCH_ID = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updYdCarschYD = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdCarschYD";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updEqpPause
--설비고장복구실적 설비휴지 등록
MERGE INTO TB_YF_EQPPAUSE EP USING (
SELECT DECODE(DD.NEW_YN,'Y',TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||'XXXXXX' ,EP.YD_EQP_PAUSE_OCCR_SEQ) AS YD_EQP_PAUSE_OCCR_SEQ
      ,DD.YD_EQP_ID
      ,DD.MODIFIER
      ,SYSDATE AS MOD_DDTT
      ,'N'     AS DEL_YN
      ,DECODE(DD.NEW_YN,'Y',DD.YD_EQP_PAUSE_CODE) AS YD_EQP_PAUSE_CODE
      ,DECODE(DD.NEW_YN,'Y',SF_YD_WRK_DUTY(DD.YD_EQP_PAUSE_OCC_DT)) AS YD_EQP_PAUSE_OCC_WRK_DUTY
      ,DECODE(DD.NEW_YN,'Y',SF_YD_WRK_PARTY(DD.YD_EQP_PAUSE_OCC_DT)) AS YD_EQP_PAUSE_OCC_WRK_PARTY
      ,DECODE(DD.NEW_YN,'Y',DD.YD_EQP_PAUSE_OCC_DT) AS YD_EQP_PAUSE_OCC_DT
      ,DECODE(DD.NEW_YN,'N',DD.YD_EQP_PAUSE_OCC_DT) AS YD_EQP_PAUSE_END_DT
      ,DECODE(DD.NEW_YN,'N',SF_YD_WRK_DUTY(DD.YD_EQP_PAUSE_OCC_DT)) AS YD_EQP_PAUSE_END_WRK_DUTY
      ,DECODE(DD.NEW_YN,'N',SF_YD_WRK_PARTY(DD.YD_EQP_PAUSE_OCC_DT)) AS YD_EQP_PAUSE_END_WRK_PARTY
      ,DECODE(DD.NEW_YN,'N',ROUND((DD.YD_EQP_PAUSE_OCC_DT - EP.YD_EQP_PAUSE_OCC_DT) * 1440)) AS YD_EQP_PAUSE_PASS_HR
  FROM TB_YF_EQPPAUSE EP
      ,(
        SELECT A.*
              ,MAX(B.YD_EQP_PAUSE_OCCR_SEQ) AS YD_EQP_PAUSE_OCCR_SEQ
              ,CASE WHEN A.BR_GP = 'B' OR  MAX(B.YD_EQP_PAUSE_OCCR_SEQ) IS NULL THEN 'Y' ELSE 'N' END AS NEW_YN --신규여부
              ,CASE WHEN A.BR_GP = 'R' AND MAX(B.YD_EQP_PAUSE_OCCR_SEQ) IS NULL THEN 'N' ELSE 'Y' END AS REG_YN --등록여부
          FROM (
                SELECT :V_YD_EQP_ID AS YD_EQP_ID
                      ,:V_MODIFIER AS MODIFIER
                      ,:V_YD_EQP_PAUSE_CODE AS YD_EQP_PAUSE_CODE
                      ,TO_DATE(:V_YD_EQP_PAUSE_OCC_DT,'YYYYMMDDHH24MISS') AS YD_EQP_PAUSE_OCC_DT
                      ,:V_BR_GP AS BR_GP --고장복구구분
                  FROM DUAL
               ) A
              ,TB_YF_EQPPAUSE B
         WHERE A.YD_EQP_ID = B.YD_EQP_ID(+)
       ) DD
 WHERE DD.YD_EQP_ID = EP.YD_EQP_ID(+)
   AND DD.YD_EQP_PAUSE_OCCR_SEQ = EP.YD_EQP_PAUSE_OCCR_SEQ(+)
   AND DD.REG_YN = 'Y'
) DD ON (EP.YD_EQP_PAUSE_OCCR_SEQ = DD.YD_EQP_PAUSE_OCCR_SEQ AND EP.YD_EQP_ID = DD.YD_EQP_ID)
--복구 수신시 UPDATE
WHEN MATCHED THEN UPDATE SET
	    EP.MODIFIER = DD.MODIFIER
       ,EP.MOD_DDTT = DD.MOD_DDTT
       ,EP.YD_EQP_PAUSE_END_DT = DD.YD_EQP_PAUSE_END_DT
       ,EP.YD_EQP_PAUSE_END_WRK_DUTY = DD.YD_EQP_PAUSE_END_WRK_DUTY
       ,EP.YD_EQP_PAUSE_END_WRK_PARTY = DD.YD_EQP_PAUSE_END_WRK_PARTY
       ,EP.YD_EQP_PAUSE_PASS_HR = DD.YD_EQP_PAUSE_PASS_HR
WHEN NOT MATCHED THEN
--고장 수신시 INSERT
INSERT (
  EP.YD_EQP_PAUSE_OCCR_SEQ
, EP.YD_EQP_ID
, EP.REGISTER
, EP.REG_DDTT
, EP.MODIFIER
, EP.MOD_DDTT
, EP.DEL_YN
, EP.YD_EQP_PAUSE_CODE
, EP.YD_EQP_PAUSE_OCC_WRK_DUTY
, EP.YD_EQP_PAUSE_OCC_WRK_PARTY
, EP.YD_EQP_PAUSE_OCC_DT
) VALUES (
  TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') || TO_CHAR(USRYFA.YF_EQP_PAUSE_OCCR_SEQ.NEXTVAL,'FM000000') 
, DD.YD_EQP_ID
, DD.MODIFIER
, DD.MOD_DDTT
, DD.MODIFIER
, DD.MOD_DDTT
, DD.DEL_YN
, DD.YD_EQP_PAUSE_CODE
, DD.YD_EQP_PAUSE_OCC_WRK_DUTY
, DD.YD_EQP_PAUSE_OCC_WRK_PARTY
, DD.YD_EQP_PAUSE_OCC_DT
)
 </pre> */
public final static String updEqpPause = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updEqpPause";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CrnSch
-- 크레인권상실적 크레인스케줄 수정 
UPDATE TB_YF_CRNSCH
   SET MODIFIER         = :V_MODIFIER
      ,MOD_DDTT         = SYSDATE
      ,YD_WRK_PROG_STAT = '2' --권상완료
      ,YD_UP_CMPL_DT    = TO_DATE(:V_YD_UP_CMPL_DT,'YYYYMMDDHH24MISS')
      ,YD_UP_WR_LOC     = :V_YD_UP_WR_LOC
      ,YD_UP_WR_LYR   = :V_YD_UP_WR_LYR
      ,YD_UP_WRK_ACT_GP = :V_YD_UP_WRK_ACT_GP
      ,YD_UP_WR_XAXIS   = TO_NUMBER(:V_YD_UP_WR_XAXIS)
      ,YD_UP_WR_YAXIS   = TO_NUMBER(:V_YD_UP_WR_YAXIS)
      ,YD_UP_WR_ZAXIS   = TO_NUMBER(:V_YD_UP_WR_ZAXIS)
      ,YD_UP_WRK_MODE2  = :V_YD_UP_WRK_MODE2
 WHERE YD_CRN_SCH_ID    = :V_YD_CRN_SCH_ID
 </pre> */
public final static String updAxYML008CrnSch = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updAxYML008CrnSch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarExaminationGoodsEnd
UPDATE USRYDA.TB_YD_EXAMINATIONCHKLIST
   SET DEL_YN='Y'
     , MODIFIER='YDSYSTEM'
     , MOD_DDTT=SYSDATE 
 WHERE TRANS_ORD_DATE=:V_TRANS_ORD_DATE
   AND TRANS_ORD_SEQNO=:V_TRANS_ORD_SEQNO
   AND DEL_YN='N'
 </pre> */
public final static String updateCarExaminationGoodsEnd = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updateCarExaminationGoodsEnd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.deleteEqpTracking
UPDATE TB_YF_EQPTRACKING
SET
      DEL_YN = 'Y'
    , STL_NO = ''
WHERE 1=1
AND YD_GP     = :V_YD_GP
AND PROC_GP   = :V_PROC_GP
AND YD_EQP_ID LIKE :V_YD_EQP_ID || '%'
 </pre> */
public final static String deleteEqpTracking = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.deleteEqpTracking";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkLyrYdStkColGpClear
UPDATE USRYFA.TB_YF_STKLYR
SET
    MOD_DDTT        = SYSDATE,
    MODIFIER        = :V_MODIFIER,
    STL_NO          = NULL,
    YD_STK_LYR_ACTIVE_STAT = :V_YD_STK_LYR_ACTIVE_STAT,
    YD_STK_LYR_STAT = :V_YD_STK_LYR_STAT
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
 </pre> */
public final static String updYdStkLyrYdStkColGpClear = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkLyrYdStkColGpClear";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnCarSchMtl
UPDATE TB_YD_CARFTMVMTL
SET
    MODIFIER        = :V_MODIFIER,
    MOD_DDTT        = SYSDATE,
    DEL_YN          = 'Y'
WHERE 1=1
AND YD_CAR_SCH_ID   = :V_YD_CAR_SCH_ID
 </pre> */
public final static String updDelYnCarSchMtl = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updDelYnCarSchMtl";

 /** <pre> 
-- com.inisteel.cim.yf.aslab.dao.ASlabDAO.updSlabYdStkPosSetBed.updYdStklyrTol 
UPDATE TB_YF_STKBED
   SET MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
     , YD_STK_BED_X_AXIS      = :V_YD_STK_BED_X_AXIS
     , YD_STK_BED_Y_AXIS      = :V_YD_STK_BED_Y_AXIS
     , YD_STK_BED_Z_AXIS      = NVL(:V_YD_STK_BED_Z_AXIS,YD_STK_BED_Z_AXIS)
     , YD_STK_BED_XAXIS_TOL  = :V_YD_STK_BED_XAXIS_TOL
     , YD_STK_BED_YAXIS_TOL  = :V_YD_STK_BED_YAXIS_TOL
     , YD_STK_BED_ZAXIS_TOL  = NVL(:V_YD_STK_BED_ZAXIS_TOL,YD_STK_BED_ZAXIS_TOL)
 WHERE YD_STK_COL_GP = :V_YD_STK_COL_GP
   AND YD_STK_BED_NO = :V_YD_STK_BED_NO
 </pre> */
public final static String updYdStklyrTol = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.updSlabYdStkPosSetBed.updYdStklyrTol";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.AcoilDAO.updStockDCTakeIn
UPDATE TB_YF_STOCK
SET 
 MODIFIER       = :V_MODIFIER
,MOD_DDTT       = SYSDATE
,LINE_OFF_YN    = 'N'
WHERE STL_NO    = :V_STL_NO
 </pre> */
public final static String updStockDCTakeIn = "bak.com.inisteel.cim.yf.acoil.dao.AcoilDAO.updStockDCTakeIn";

 /** <pre> 
--com.inisteel.cim.yf.aslab.dao.ASlabDAO.mergeStockInfo
MERGE INTO TB_YF_STOCK ST USING
(
    SELECT
        :V_STL_NO           AS STL_NO,          --재료번호
        :V_MODIFIER         AS MODIFIER,        --수정자
        SYSDATE             AS MOD_DDTT,        --수정일시
        'N'                 AS DEL_YN,          --삭제유무
        :V_STOCK_ITEM       AS STOCK_ITEM,      --저장품 품목
        :V_STOCK_MOVE_TERM  AS STOCK_MOVE_TERM --저장품 이동 조건
    FROM
        DUAL
) DD ON ( ST.STL_NO = DD.STL_NO )
WHEN NOT MATCHED THEN
    INSERT
    (
        STL_NO,
        REGISTER,
        REG_DDTT,
        MODIFIER,
        MOD_DDTT,
        DEL_YN,
        STOCK_ITEM,
        STOCK_MOVE_TERM
    )
    VALUES
    (
        DD.STL_NO,
        DD.MODIFIER,
        DD.MOD_DDTT,
        DD.MODIFIER,
        DD.MOD_DDTT,
        'N',
        DD.STOCK_ITEM,
        DD.STOCK_MOVE_TERM
    )
WHEN MATCHED THEN
    UPDATE SET
        MODIFIER        = DD.MODIFIER,
        MOD_DDTT        = DD.MOD_DDTT,
        DEL_YN          = DD.DEL_YN,
        STOCK_ITEM      = DD.STOCK_ITEM,
        STOCK_MOVE_TERM = DD.STOCK_MOVE_TERM
 </pre> */
public final static String mergeStockInfo = "bak.com.inisteel.cim.yf.aslab.dao.ASlabDAO.mergeStockInfo";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarSchDelSch
UPDATE TB_YF_TCARSCH 
   SET MODIFIER          = :V_MODIFIER
     , MOD_DDTT          = SYSDATE
     , DEL_YN            = 'Y'
     , YD_EQP_WRK_STAT   = 'U'                         --공차
     , YD_CAR_PROG_STAT  = 'E'                         --하차완료
     , YD_CARUD_ST_DT    = NVL(YD_CARUD_ST_DT,SYSDATE) --하차개시시간
     , YD_CARUD_CMPL_DT  = SYSDATE                     --하차완료시간
     , YD_CARUD_WRK_CRN  = :V_CRANE_ID                 --작업크레인
     , YD_CARUD_STOP_LOC = :V_YD_CARUD_STOP_LOC
 WHERE YD_TCAR_SCH_ID    = :V_YD_TCAR_SCH_ID
 </pre> */
public final static String updTcarSchDelSch = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updTcarSchDelSch";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnSchDnWoLocCrnSch 
--크레인작업관리 권하위치변경 크레인스케줄 수정 - 
MERGE INTO TB_YF_CRNSCH CS USING (
WITH TEMP_TABLE AS (
SELECT :V_YD_DN_WO_LOC       AS V_YD_DN_WO_LOC 
     , :V_YD_DN_WO_LYR     AS V_YD_DN_WO_LYR
     , :V_YD_DN_WO_LOC_XAXIS AS V_YD_DN_WO_LOC_XAXIS
     , :V_YD_DN_WO_LOC_YAXIS AS V_YD_DN_WO_LOC_YAXIS
     , :V_YD_DN_WO_LOC_ZAXIS AS V_YD_DN_WO_LOC_ZAXIS
     , :V_YD_EQP_ID          AS V_YD_EQP_ID
     , :V_YD_SCH_CD          AS V_YD_SCH_CD
     , :V_YD_STK_COL_GP_OLD   AS V_YD_STK_COL_GP_OLD
     , :V_YD_STK_BED_NO_OLD   AS V_YD_STK_BED_NO_OLD
     , :V_YD_STK_LYR_NO_OLD AS V_YD_STK_LYR_NO_OLD
     , :V_YD_CRN_SCH_ID      AS V_YD_CRN_SCH_ID
  FROM DUAL
)
SELECT CS.YD_CRN_SCH_ID
      ,CS.YD_UP_WO_LOC       AS YD_UP_WO_LOC
      ,CS.YD_UP_WO_LYR     AS YD_UP_WO_LYR
      ,CS.YD_UP_WO_LOC_XAXIS AS YD_UP_WO_LOC_XAXIS
      ,CS.YD_UP_WO_LOC_YAXIS AS YD_UP_WO_LOC_YAXIS
      ,CS.YD_UP_WO_LOC_ZAXIS AS YD_UP_WO_LOC_ZAXIS
      
      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LOC  , CS.YD_DN_WO_LOC   ) AS YD_DN_WO_LOC
      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LYR, CS.YD_DN_WO_LYR ) AS YD_DN_WO_LYR
      
      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LOC_XAXIS,CS.YD_DN_WO_LOC_XAXIS) AS YD_DN_WO_LOC_XAXIS
      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LOC_YAXIS,CS.YD_DN_WO_LOC_YAXIS) AS YD_DN_WO_LOC_YAXIS
      ,DECODE(CS.YD_DN_WO_LOC,DD.OLD_YD_DN_WO_LOC,DD.NEW_YD_DN_WO_LOC_ZAXIS,CS.YD_DN_WO_LOC_ZAXIS) AS YD_DN_WO_LOC_ZAXIS

      ,CS.YD_DN_WO_XAXIS_GAP_MAX
      ,CS.YD_DN_WO_XAXIS_GAP_MIN
      ,CS.YD_DN_WO_YAXIS_GAP_MAX
      ,CS.YD_DN_WO_YAXIS_GAP_MIN
      ,DD.YD_EQP_ID
      ,DD.YD_SCH_CD
      ,DD.YD_STK_BED_XAXIS_TOL
      ,DD.YD_STK_BED_YAXIS_TOL  
      ,DD.DOWN_ROTATION_ANGLE
  FROM TB_YF_CRNSCH CS
      ,(SELECT YD_CRN_SCH_ID
              ,YD_WBOOK_ID
              ,YD_DN_WO_LOC                     AS OLD_YD_DN_WO_LOC
              ,YD_DN_WO_LYR                   AS OLD_YD_DN_WO_LYR
              ,YD_DN_WO_LOC_ZAXIS               AS OLD_YD_DN_WO_LOC_ZAXIS
              ,V_YD_DN_WO_LOC                  AS NEW_YD_DN_WO_LOC
              ,V_YD_DN_WO_LYR                AS NEW_YD_DN_WO_LYR
              ,TO_NUMBER(V_YD_DN_WO_LOC_XAXIS) AS NEW_YD_DN_WO_LOC_XAXIS
              ,TO_NUMBER(V_YD_DN_WO_LOC_YAXIS) AS NEW_YD_DN_WO_LOC_YAXIS
              ,TO_NUMBER(V_YD_DN_WO_LOC_ZAXIS) AS NEW_YD_DN_WO_LOC_ZAXIS
              ,NVL(V_YD_EQP_ID,YD_EQP_ID)      AS YD_EQP_ID
              ,NVL(V_YD_SCH_CD,YD_SCH_CD)      AS YD_SCH_CD
              ,(SELECT YD_STK_BED_XAXIS_TOL FROM TB_YF_STKBED WHERE YD_STK_COL_GP = SUBSTR(V_YD_DN_WO_LOC,1,6) AND YD_STK_BED_NO = SUBSTR(V_YD_DN_WO_LOC,7,2)) AS YD_STK_BED_XAXIS_TOL
              ,(SELECT YD_STK_BED_YAXIS_TOL FROM TB_YF_STKBED WHERE YD_STK_COL_GP = SUBSTR(V_YD_DN_WO_LOC,1,6) AND YD_STK_BED_NO = SUBSTR(V_YD_DN_WO_LOC,7,2)) AS YD_STK_BED_YAXIS_TOL
              ,(CASE WHEN SUBSTR(V_YD_DN_WO_LOC,3,2) BETWEEN '00' AND '99'THEN '0' ELSE DOWN_ROTATION_ANGLE END) AS DOWN_ROTATION_ANGLE
          FROM TB_YF_CRNSCH
             , TEMP_TABLE
         WHERE YD_CRN_SCH_ID = V_YD_CRN_SCH_ID
         ) DD
 WHERE CS.YD_WBOOK_ID   = DD.YD_WBOOK_ID
   AND CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID
   AND (
        (CS.YD_DN_WO_LOC = DD.OLD_YD_DN_WO_LOC AND NVL(CS.YD_DN_WO_LYR,'01') >= NVL(DD.OLD_YD_DN_WO_LYR,'01'))
     OR 
        (CS.YD_UP_WO_LOC = DD.OLD_YD_DN_WO_LOC AND NVL(CS.YD_UP_WO_LYR,'01') >= NVL(DD.OLD_YD_DN_WO_LYR,'01'))
    )
   AND CS.DEL_YN = 'N'
   
) DD ON (CS.YD_CRN_SCH_ID = DD.YD_CRN_SCH_ID)
WHEN MATCHED THEN UPDATE SET
     CS.MODIFIER           = :V_MODIFIER
    ,CS.MOD_DDTT           = SYSDATE
    ,CS.YD_EQP_ID          = DD.YD_EQP_ID
    ,CS.YD_SCH_CD          = DD.YD_SCH_CD
    ,CS.YD_UP_WO_LOC       = DD.YD_UP_WO_LOC
    ,CS.YD_UP_WO_LYR       = DD.YD_UP_WO_LYR
    ,CS.YD_UP_WO_LOC_XAXIS = DD.YD_UP_WO_LOC_XAXIS
    ,CS.YD_UP_WO_LOC_YAXIS = DD.YD_UP_WO_LOC_YAXIS
    ,CS.YD_UP_WO_LOC_ZAXIS = DD.YD_UP_WO_LOC_ZAXIS
    ,CS.YD_DN_WO_LOC       = DD.YD_DN_WO_LOC
    ,CS.YD_DN_WO_LYR       = DD.YD_DN_WO_LYR
    ,CS.YD_DN_WO_LOC_XAXIS = DD.YD_DN_WO_LOC_XAXIS
    ,CS.YD_DN_WO_LOC_YAXIS = DD.YD_DN_WO_LOC_YAXIS
    ,CS.YD_DN_WO_LOC_ZAXIS = DD.YD_DN_WO_LOC_ZAXIS
    ,CS.YD_DN_WO_XAXIS_GAP_MAX =DD.YD_STK_BED_XAXIS_TOL
    ,CS.YD_DN_WO_XAXIS_GAP_MIN =DD.YD_STK_BED_XAXIS_TOL
    ,CS.YD_DN_WO_YAXIS_GAP_MAX =DD.YD_STK_BED_YAXIS_TOL
    ,CS.YD_DN_WO_YAXIS_GAP_MIN =DD.YD_STK_BED_YAXIS_TOL
    ,CS.DOWN_ROTATION_ANGLE =DD.DOWN_ROTATION_ANGLE
 </pre> */
public final static String updCrnSchDnWoLocCrnSch = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updCrnSchDnWoLocCrnSch";

 /** <pre> 
--com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkbedYdStkColGp
UPDATE USRYFA.TB_YF_STKBED
SET
    MOD_DDTT        = SYSDATE,
    MODIFIER        = :V_MODIFIER,
    YD_STK_BED_ACTIVE_STAT = NVL(:V_YD_STK_BED_ACTIVE_STAT, YD_STK_BED_ACTIVE_STAT),
    YD_STK_BED_WT_MAX = NVL(:V_YD_STK_BED_WT_MAX, YD_STK_BED_WT_MAX )
WHERE 1=1
AND YD_STK_COL_GP   = :V_YD_STK_COL_GP
 </pre> */
public final static String updYdStkbedYdStkColGp = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updYdStkbedYdStkColGp";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.rstZoneCd  
UPDATE TB_YF_STKCOL
   SET MODIFIER = :V_MODIFIER
      ,MOD_DDTT = SYSDATE
      ,YD_ZONE_GP = ''
 WHERE YD_ZONE_GP = :V_YD_ZONE_GP
   AND YD_GP = :V_YD_GP
   AND BAY_GP LIKE :V_BAY_GP || '%'
   AND REGEXP_INSTR(SECT_GP,'[^0-9]') = 0
   AND DEL_YN = 'N'
 </pre> */
public final static String rstZoneCd = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.rstZoneCd";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCarSchmtl
INSERT INTO TB_YD_CARFTMVMTL
(
    YD_CAR_SCH_ID,
    STL_NO,
    REGISTER,
    REG_DDTT,
    MODIFIER,
    MOD_DDTT,
    DEL_YN,
    YD_STK_BED_NO,
    YD_STK_LYR_NO
)
VALUES
(
    :V_YD_CAR_SCH_ID,
    :V_STL_NO,
    :V_MODIFIER,
    SYSDATE,
    :V_MODIFIER,
    SYSDATE,
    'N',
    :V_YD_STK_BED_NO,
    :V_YD_STK_LYR_NO
)
 </pre> */
public final static String insCarSchmtl = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.insCarSchmtl";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCarpointByCarpnt
UPDATE USRYDA.TB_YD_CARPOINT
SET
    MOD_DDTT            = SYSDATE,
    MODIFIER            = :V_MODIFIER,
    YD_STK_COL_ACT_STAT = :V_YD_STK_COL_ACT_STAT,
    TRN_EQP_CD          = :V_TRN_EQP_CD,
    CAR_NO              = :V_CAR_NO,
    CARD_NO             = :V_CARD_NO
WHERE 1=1
AND YD_CARPNT_CD        = :V_YD_CARPNT_CD
 </pre> */
public final static String updYdCarpointByCarpnt = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updYdCarpointByCarpnt";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYfStkColStatByCar 
UPDATE TB_YF_STKCOL
SET CAR_CARD_NO=(CASE WHEN :V_YD_STK_COL_ACT_STAT IN('L','R','N') THEN '9994' ELSE '' END)
  , MODIFIER=:V_MODIFIER
  , MOD_DDTT=SYSDATE
WHERE YD_STK_COL_GP=(SELECT YD_STK_COL_GP
                       FROM TB_YD_CARPOINT
                     WHERE YD_CARPNT_CD=:V_YD_CARPNT_CD
                     )
 </pre> */
public final static String updateYfStkColStatByCar = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updateYfStkColStatByCar";

 /** <pre> 
-- com.inisteel.cim.yf.acoil.dao.ACoilDAO.updScrpCarEntYn
-- 스크랩 차량 진입여부 수정
UPDATE TB_YF_EQP
   SET YD_L2_HMI_STAT = :V_YD_L2_HMI_STAT
     , MODIFIER = :V_MODIFIER
     , MOD_DDTT = SYSDATE
 WHERE YD_EQP_ID = :V_YD_EQP_ID
   AND DEL_YN = 'N'
 </pre> */
public final static String updScrpCarEntYn = "bak.com.inisteel.cim.yf.acoil.dao.ACoilDAO.updScrpCarEntYn";

 /** <pre> 
--com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateCoilCommonLocInfo2
UPDATE TB_PT_COILCOMM
   SET (  
         YD_GP,                  -- 야드구분
         YD_BAY_GP,              -- 동
         YD_EQP_GP,              -- SPAN
         YD_STK_COL_NO,          -- 적치열번지
         YD_STK_BED_NO,          -- 적치번지
         YD_STK_LYR_NO,          -- 적치단
         YD_STR_LOC,             -- 현 저장위치코드
         YD_STR_LOC_HIS1,         -- 전 저장위치코드
         YD_STR_LOC_HIS2         -- 전전 저장위치코드
       ) =
       (
            SELECT YD_GP,                  -- 야드구분
                 YD_BAY_GP,              -- 동
                 YD_EQP_GP,              -- SPAN
                 YD_STK_COL_NO,          -- 적치열번지
                 YD_STK_BED_NO,          -- 적치번지
                 DECODE(YD_GP,'H','0','')||YD_STK_LYR_NO,          -- 적치단
                 YD_STR_LOC,             -- 현 저장위치코드
                 YD_STR_LOC_HIS1,         -- 전 저장위치코드
                 YD_STR_LOC_HIS2         -- 전전 저장위치코드
             FROM (
                    SELECT 
                        substr(:V_YD_LOC,1,1) AS YD_GP,-- 야드구분
                        substr(:V_YD_LOC,2,1) AS YD_BAY_GP,-- 동
                        substr(:V_YD_LOC,3,2) AS YD_EQP_GP,-- SPAN
                        substr(:V_YD_LOC,5,2) AS YD_STK_COL_NO,-- 적치열번지
                        substr(:V_YD_LOC,7,2) AS YD_STK_BED_NO,-- 적치번지
                        substr(:V_YD_LOC,9,2) AS YD_STK_LYR_NO,-- 적치단
                        :V_YD_LOC AS YD_STR_LOC,                       -- 현 저장위치코드   
                        YD_STR_LOC AS YD_STR_LOC_HIS1,                 -- 전현 저장위치코드
                        YD_STR_LOC_HIS1 AS YD_STR_LOC_HIS2             -- 전전현 저장위치코드
                    FROM TB_PT_COILCOMM
                    WHERE COIL_NO = :V_STL_NO
                    ) A
       )
WHERE COIL_NO = :V_STL_NO
 </pre> */
public final static String updateCoilCommonLocInfo2 = "bak.com.inisteel.cim.yf.acommon.dao.YfCommDAO.updateCoilCommonLocInfo2";

}
