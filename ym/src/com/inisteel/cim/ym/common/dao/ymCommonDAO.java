/**
 * 
 * @(#)CraneSchRegSBean
 * 
 * @version    :
 * @author     : HanDong Data Systems
 * @date       : 2005. 7. 20
 *
 * @description :
 * 
 */
package com.inisteel.cim.ym.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;

import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;

public class ymCommonDAO extends CommonDAO{
    private static ymCommonDAO instance;
	private YmCommDAO ymCommDAO = new YmCommDAO();
	public static ymCommonDAO getInstance()
	{
		if(instance == null)
		{
			synchronized(com.inisteel.cim.common.dao.CommonDAO.class)
            {
                if(instance == null)
                    instance = new ymCommonDAO();
            }
		}
		return instance;
	}

	/**
	 * 야드맵에 슬라브가 존재하는지 리턴한다. 
	 * @param stockId	저장품ID
	 * @return
	 */
	public JDTORecord readStockOfLayer(String stockId) {
		String  queryCode = "ym.common.dao.selectStockOfLayer";
		/*
		--ym.common.dao.selectStockOfLayer
		--야드맵에 슬라브가 존재하는지 리턴한다.
		SELECT  STOCK_ID
		FROM     TB_YM_STACKLAYER
		WHERE   STOCK_ID = ?
		*/
		return super.findByPrimaryKey(queryCode, new Object[]{ stockId });
	}
	
	/**
	 * 크레인 작업 영역을 리턴한다.
	 * @param equipGp	설비구분
	 * @param col		적치열
	 * @param bed		번지
	 * @param layer		단
	 * @return
	 */
	public JDTORecord readCRRange(String equipGp, String col, String bed, String layer) {
		String  queryCode = "ym.common.dao.selectCRWorkRange";
		/*
		--ym.common.dao.selectCRWorkRange
		--크레인 영역을 리턴한다.
		SELECT  DECODE(GREATEST(A.FROM_LOC, A.X_AXIS), 
		            A.X_AXIS, DECODE(LEAST(A.TO_LOC, A.X_AXIS), 
		                        A.X_AXIS, 'T', 'F'), 'F') AS RESULT
		FROM    (
		        SELECT  A.FROM_LOC, 
		                A.TO_LOC,
		                B.STACK_LAYER_X_AXIS AS X_AXIS
		        FROM    TB_YM_EQUIP A, 
		                TB_YM_STACKLAYER B
		        WHERE   A.EQUIP_GP      = ? -- 설비구분
		        AND     B.STACK_COL_GP  = ? -- 적치위치
		        AND     B.STACK_BED_GP  = ? -- 적치번지
		        AND     B.STACK_LAYER_GP = ?
		        ) A
		*/
		return super.findByPrimaryKey(queryCode, new Object[]{ equipGp, col, bed, layer });	    
	}
	
	/**
	 * W/B 정보를 리턴한다.
	 * @return
	 */
	public JDTORecord readLoadWBStock() {
		String  queryCode = "ym.common.dao.selectLoadWBStock";
		/*
		--ym.common.dao.selectLoadWBStock
		--W/B 05번지 상단 저장품을 리턴한다.
		SELECT  STOCK_ID
		FROM    TB_YM_STACKLAYER
		WHERE   STACK_COL_GP = '2CWB01'
		AND     STACK_BED_GP = '05'
		AND     STACK_LAYER_STAT = 'L'
		AND     ROWNUM = 1
		ORDER BY STACK_LAYER_GP DESC
		*/
		return super.find(queryCode);	    
	}

	/**
	 * 스카핑 정보를 리턴한다.
	 * @param col
	 * @return
	 */
	public JDTORecord readScarfingInfo(String col) {
		String  queryCode = "ym.common.dao.selectScarfingInfo";
		/*
		--ym.common.dao.selectScarfingInfo
		--스카핑 출측 정보를 리턴한다. 
		SELECT STOCK_ID
		 FROM   TB_YM_STACKLAYER
		 WHERE  STACK_COL_GP = ?
		 AND    ROWNUM = 1
		 ORDER BY STACK_BED_GP DESC 
		*/
		return super.findByPrimaryKey(queryCode, new Object[]{ col });	    
	}
	/**
	 * 관제확정시 순위변경이 안되어야 할 슬라브정보 리턴한다.
	 * @param col	적치열
	 * @return
	 */
	public List readLoadWBCTC() {
		String  queryCode = "ym.common.dao.selectLoadWBCTC1";
		/*
		SELECT  STOCK_ID
		FROM    TB_YM_STACKLAYER
		WHERE   STACK_COL_GP    = '2BCT03'
		AND     STACK_BED_GP    = '01'
		AND     STACK_LAYER_GP  = '01'
		AND     STACK_LAYER_STAT= 'L'
		UNION
		SELECT  STOCK_ID
		FROM    TB_YM_STACKLAYER
		WHERE   STACK_COL_GP    = '2CCT04'
		AND     STACK_BED_GP    = '01'
		AND     STACK_LAYER_GP  = '01'
		AND     STACK_LAYER_STAT= 'L'
		UNION
		SELECT  STOCK_ID
		FROM    TB_YM_STACKLAYER
		WHERE   STACK_COL_GP LIKE '2CWB%'
		AND     STACK_LAYER_STAT= 'L'

		*/
		return super.findList(queryCode);
	}

	/**
	 * CTC,W/B에 올려진 슬라브 정보를 리턴한다.
	 * @param col	적치열
	 * @return
	 */
	public JDTORecord readLoadWBCTC(String col) {
		String  queryCode = "ym.common.dao.selectLoadWBCTC";
		/*
		SELECT  STOCK_ID
		FROM    TB_YM_STACKLAYER
		WHERE   STACK_COL_GP = ?
		AND     STACK_BED_GP = '01'
		AND     STOCK_ID IS NOT NULL
		AND     ROWNUM = 1
		ORDER BY STACK_LAYER_GP DESC
		*/
		return super.findByPrimaryKey(queryCode, new Object[]{ col });
	}

	/**
	 * 장입예정 슬라브조회 화면 쿼리.
	 * @param col	적치열
	 * @return
	 */
	public List readZoinStocks(String col) {
		String  queryCode = "ym.common.dao.selectZoinStocks";
		/*
		Select 
		    A.SEQ,
		    DECODE(A.STACK_COL_GP,NULL,
		        DECODE(C.YD_STR_LOC,NULL,'------ -- --',
		        SUBSTR(C.YD_STR_LOC,0,6) || ' ' ||
		        SUBSTR(C.YD_STR_LOC,7,2) || ' ' ||
		        SUBSTR(C.YD_STR_LOC,9,2)),
		        A.STACK_COL_GP || ' ' ||
		        A.STACK_BED_GP || ' ' ||
		        A.STACK_LAYER_GP)                                   AS FROM_LOC,
		    A.STACK_LAYER_STAT                                      AS STACK_LAYER_STAT,
		    A.STOCK_ID                                              AS STOCK_ID,
		    B.STOCK_MOVE_TERM                                       AS 이적사유,
		    NVL(A.CHARGE_LOT_NO, '-')                               AS CHARGE_LOT_NO,
		    DECODE(A.STACK_LAYER_STAT, 
		        'L','적치중',
		        'S','스케쥴등록',
		        'U','UP',
		        'P','PUT', '-')                                     AS WORK_STAT,
		    G.BUY_SLAB_NO                                           AS BUY_SLAB_NO,
		    DECODE(C.ORD_NO,NULL,E.REPRESENT_ORD_NO,C.ORD_NO)       AS ORD_NO,
		    DECODE(C.ORD_DTL,NULL,E.REPRESENT_ORD_DTL,C.ORD_DTL)    AS ORD_DTL,
		    DECODE(C.SLAB_T,NULL,E.SLAB_T,C.SLAB_T)                 AS 두께,
		    DECODE(C.SLAB_W,NULL,E.SLAB_W,C.SLAB_W)                 AS 폭,
		    DECODE(C.SLAB_WT,NULL,E.SLAB_WT,C.SLAB_WT)              AS 중량,
		    DECODE(C.SLAB_LEN,NULL,E.SLAB_LEN,C.SLAB_LEN)           AS 길이,
		    DECODE(C.SPEC_ABBSYM,NULL,E.SPEC_ABBSYM,C.SPEC_ABBSYM)  AS 강종,
		    DECODE(C.CURR_PROG_CD,NULL,'예정',C.CURR_PROG_CD)       AS 재료상태,
		    E.REFUR_CHG_LOT_NO                                      AS ROLL_UNIT_NAME,
		    E.COIL_T_AIM                                            AS COIL두께,
		    E.COIL_W_AIM                                            AS COIL폭
		From    
		    ( 
		    Select 
		        A.Stock_Id,
		        LPAD(A.Cts_Relay_Saddle,6,0) As Charge_Lot_No,
		        B.Stack_Col_Gp,
		        B.Stack_Bed_Gp,
		        B.Stack_Layer_Gp,
		        B.Stack_Layer_Stat,
		        '2' As Seq
		    From Tb_Ym_Stock A,
		         Tb_Ym_StackLayer B
		    Where A.Stock_Id = B.Stock_Id
		    And A.Charge_Lot_No Is Null
		    And A.Cts_Relay_Saddle is Not Null
		    And B.Stack_Col_Gp Like '2CCR%'
		    
		    UNION
		    
		    Select 
		        A.Stock_Id,
		        A.Charge_Lot_No,
		        B.Stack_Col_Gp,
		        B.Stack_Bed_Gp,
		        B.Stack_Layer_Gp,
		        B.Stack_Layer_Stat,
		        Decode(Substr(B.Stack_Col_Gp,0,2),'2A'
		        ,Decode(Substr(B.Stack_Col_Gp,0,6),'2ABK02','3','2ABK01','4','5')
		        ,Decode(Substr(B.Stack_Col_Gp,0,4),'2C02','3','2C01','4','5')) As Seq
		    From Tb_Ym_Stock A,
		        (SELECT * 
		         FROM Tb_Ym_StackLayer
		         WHERE  Stack_Col_Gp Like '2' || :ColGp || '%'
		         And    Stack_Layer_Stat In ('L','S','U')
		        )B
		    Where A.Stock_Id = B.Stock_Id(+)
		    And A.Charge_Lot_No Is Not Null
		    )A,
		    TB_YM_STOCK B, 
		    TB_YM_SCH D,
		    VW_YD_SLABCOMM C ,
		    TB_QM_BUYSLABINFO G,
		    TB_CT_L_HRMILLWO E
		WHERE   A.STOCK_ID = B.STOCK_ID(+)
		AND     A.STOCK_ID = C.SLAB_NO(+)
		AND     C.MSLAB_NO = G.MSLAB_NO(+)
		AND     A.STOCK_ID = D.STOCK_ID(+)
		AND     A.STOCK_ID = E.STL_NO(+)
		Order By A.Charge_Lot_No,
		         A.Seq,
		         A.Stack_Col_Gp,
		         A.Stack_Bed_Gp Desc,
		         A.Stack_Layer_Gp Desc
		*/		         
		return super.findList(queryCode, new Object[]{ col });
	}

	/**
	 * 스케쥴에 대차 상차 작업이 있는지 리턴한다.
	 * @param col	적치열
	 * @return
	 */
	public List readLoadSlabOfSch(String col) {
		String  queryCode = "ym.common.dao.selectLoadSlabOfSch";
		/*
		SELECT  SCH.SCH_ID
		FROM    (
		        SELECT  COL,
		                SUBSTR(COL, 1, 1) AS YD_GP,
		                SUBSTR(COL, 2, 1) AS BAY_GP
		        FROM    (SELECT ? AS COL FROM DUAL )A
		        )B,
		        TB_YM_SCH SCH
		WHERE   B.YD_GP = SCH.YD_GP
		AND     B.BAY_GP= SCH.BAY_GP
		AND     SCH.CRANE_WORD_PUT_LOC LIKE B.COL || '%'
		*/
		return super.findList(queryCode, new Object[] { col });
	}
	
	/**
	 * 스케쥴에 대차 상차 작업이 있는지 리턴한다.
	 * @param col	적치열
	 * @return
	 */
	public List readLoadSlabOfSch2(String col) {
		String  queryCode = "ym.common.dao.selectLoadSlabOfSch2";
		/*
		SELECT  SCH.SCH_ID
		FROM    (
		        SELECT  COL,
		                SUBSTR(COL, 1, 1) AS YD_GP,
		                SUBSTR(COL, 2, 1) AS BAY_GP
		        FROM    (SELECT ? AS COL FROM DUAL )A
		        )B,
		        TB_YM_SCH SCH
		WHERE   B.YD_GP = SCH.YD_GP
		AND     B.BAY_GP= SCH.BAY_GP
		AND     SCH.CRANE_WORD_PUT_LOC LIKE B.COL || '%'
		*/
		return super.findList(queryCode, new Object[] { col });
	}

	/**
	 * 장입대상재를 리턴한다.
	 * @param curLotNo	현재 장입 LOT NO
	 * @return
	 */
	public List readZoneInStockList(String bay) {
		String  queryCode = "ym.common.dao.selectZoneInStockList";
		/*
		SELECT  STOCK.WBOOK_ID,
		        STOCK.STOCK_ID,
		        LAYER.STACK_COL_GP
		FROM    TB_YM_STOCK         STOCK,
		        TB_YM_STACKLAYER    LAYER
		WHERE   STOCK.CHARGE_LOT_NO = 
		        (
		        SELECT  MIN(TO_NUMBER(CHARGE_LOT_NO, 999999))
		        FROM    TB_YM_STOCK
		        WHERE   CHARGE_LOT_NO IS NOT NULL
		        )
		AND     STOCK.WBOOK_ID IS NULL
		AND     STOCK.STOCK_ID = LAYER.STOCK_ID
		AND     LAYER.STACK_COL_GP LIKE '2' || ? || '%'
		AND     LAYER.STACK_LAYER_STAT = 'L'
		ORDER BY LAYER.STACK_COL_GP, LAYER.STACK_BED_GP, LAYER.STACK_LAYER_GP DESC	
		*/
		return super.findList(queryCode, new Object[]{ bay });
	}

	/**
	 * 압연취소 정보를 리턴한다.
	 * @param stockId	저장품ID
	 * @return
	 */
	public JDTORecord readCancelZoneInOfStock(String stockId) {
		String  queryCode = "ym.common.dao.selectCancelZoneInOfStock";
		return super.findByPrimaryKey(queryCode, new Object[] { stockId });
	}

	/**
	 * B열연 슬라브맵 정보를 리턴한다.
	 * @param col	적치열
	 * @param bed	번지
	 * @param layer	단
	 * @return
	 */
	public List readBYDMapInfo(String col) {
		/*
		WITH TEMP AS (
		    SELECT  YD_GP,
		            BAY_GP,
		            STACK_COL_GP,
		            STACK_COL_USAGE_CD
		    FROM    TB_YM_STACKCOL
		    WHERE   STACK_COL_GP = ?
		)
		SELECT  LAYER.STOCK_ID,
		        LAYER.STACK_COL_GP || LAYER.STACK_BED_GP || LAYER.STACK_LAYER_GP AS SKIDADDRESS,
		        LAYER.STACK_LAYER_X_AXIS,
		        LAYER.STACK_LAYER_Y_AXIS,
		        POS.XPCD,
		        POS.XMCD,
		        POS.YPCD,
		        POS.YMCD,
		        DECODE(LAYER.STACK_LAYER_ACTIVE_STAT, 'O', 'Y', 'N') AS USE_YN,
		        NVL(COMM.ORD_NO,'') ||
		        NVL(COMM.ORD_DTL,'') AS PRODUCT_NO,
		        COMM.COIL_T,
		        COMM.COIL_W,
		        COMM.CURR_COIL_LEN AS COIL_LEN,
		        COMM.COIL_WT,
		        COMM.COIL_OUTDIA,
		        COMM.BRANCH_CD,
		        COMM.COOL_METHOD,
		        COMM.EXTEND_CONVEYOR_BRANCH_CD
		FROM    TEMP,
		        (
		        SELECT  XPOS.XPCD,
		                XPOS.XMCD,
		                YPOS.YPCD,
		                YPOS.YMCD
		        FROM    (
		                SELECT  STACK_RULE_MAX AS XPCD,
		                        STACK_RULE_MIN AS XMCD
		                FROM    TEMP,
		                        TB_YM_STACKRULE RULE
		                WHERE   TEMP.YD_GP  = RULE.YD_GP
		                AND     TEMP.BAY_GP = RULE.BAY_GP
		                AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
		                AND     STACK_RULE_CD = 'X-CD'
		                )XPOS,
		                (
		                SELECT  STACK_RULE_MAX AS YPCD,
		                        STACK_RULE_MIN AS YMCD
		                FROM    TEMP,
		                        TB_YM_STACKRULE RULE
		                WHERE   TEMP.YD_GP  = RULE.YD_GP
		                AND     TEMP.BAY_GP = RULE.BAY_GP
		                AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
		                AND     STACK_RULE_CD = 'Y-CD'        
		                )YPOS                
		        ) POS,
		        (
		         SELECT 
		             DECODE(A.STACK_LAYER_STAT,'P','',A.STOCK_ID) AS STOCK_ID,
		             A.STACK_LAYER_STAT,
		             A.STACK_BED_GP,
		             A.STACK_LAYER_GP,
		             A.STACK_COL_GP,
		             A.STACK_LAYER_X_AXIS,
		             A.STACK_LAYER_Y_AXIS,
		             A.STACK_LAYER_ACTIVE_STAT
		         FROM TB_YM_STACKLAYER A    
		        )LAYER,
		        TB_PM_COILCOMM      COMM
		WHERE   TEMP.STACK_COL_GP       = LAYER.STACK_COL_GP
		AND     LAYER.STOCK_ID          = COMM.COIL_NO(+)
		ORDER BY LAYER.STACK_BED_GP, LAYER.STACK_LAYER_GP
		*/		
		String  queryCode = "ym.common.dao.selectBYDMapInfo3";
		return super.findList(queryCode, new Object[] { col });
	}

	/**
	 * B열연 슬라브맵 정보를 리턴한다.
	 * @param col	적치열
	 * @param bed	번지
	 * @param layer	단
	 * @return
	 */
	public List readBYDMapInfo(String col, String bed) {
		/*
		WITH TEMP AS (
		    SELECT  COL.YD_GP,
		            COL.BAY_GP,
		            COL.STACK_COL_USAGE_CD,
		            STACKER.STACK_COL_GP,
		            STACKER.STACK_BED_GP,
		            STACKER.STACK_BED_QNTY_MAX
		    FROM    TB_YM_STACKCOL  COL,
		            TB_YM_STACKER   STACKER
		    WHERE   COL.STACK_COL_GP = ?
		    AND     COL.STACK_COL_GP = STACKER.STACK_COL_GP
		    AND     STACKER.STACK_BED_GP = ?
		)
		SELECT  TEMP.STACK_BED_QNTY_MAX,
		        (
		        SELECT  COUNT(LAYER.STOCK_ID)
		        FROM    TEMP,
		                TB_YM_STACKLAYER LAYER
		        WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
		        AND     TEMP.STACK_BED_GP = LAYER.STACK_BED_GP
		        AND     LAYER.STACK_LAYER_STAT IN ('L','U','S')   
		        ) AS STACK_BED_QNTY_CURR,
		        LAYER.STOCK_ID,
		        LAYER.STACK_COL_GP || LAYER.STACK_BED_GP  AS BEDADDRESS,
		        LAYER.STACK_LAYER_GP,
		        LAYER.STACK_LAYER_X_AXIS,
		        LAYER.STACK_LAYER_Y_AXIS,
		        POS.XPCD,
		        POS.XMCD,
		        POS.YPCD,
		        POS.YMCD,
		        DECODE(LAYER.STACK_LAYER_ACTIVE_STAT, 'O', 'Y', 'N') AS USE_YN,
		        NVL(COMM.ORD_NO,'') ||
		        NVL(COMM.ORD_DTL,'') AS PRODUCT_NO,
		        COMM.SLAB_T,
		        COMM.SLAB_W,
		        COMM.SLAB_LEN,
		        COMM.SLAB_WT
		FROM    TEMP,
		        (
		        SELECT  XPOS.XPCD,
		                XPOS.XMCD,
		                YPOS.YPCD,
		                YPOS.YMCD
		        FROM    (
		                SELECT  STACK_RULE_MAX AS XPCD,
		                        STACK_RULE_MIN AS XMCD
		                FROM    TEMP,
		                        TB_YM_STACKRULE RULE
		                WHERE   TEMP.YD_GP  = RULE.YD_GP
		                AND     TEMP.BAY_GP = RULE.BAY_GP
		                AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
		                AND     STACK_RULE_CD = 'X-CD'
		                )XPOS,
		                (
		                SELECT  STACK_RULE_MAX AS YPCD,
		                        STACK_RULE_MIN AS YMCD
		                FROM    TEMP,
		                        TB_YM_STACKRULE RULE
		                WHERE   TEMP.YD_GP  = RULE.YD_GP
		                AND     TEMP.BAY_GP = RULE.BAY_GP
		                AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
		                AND     STACK_RULE_CD = 'Y-CD'        
		                )YPOS               
		        ) POS,
		        TB_YM_STACKLAYER    LAYER,
		        TB_PM_SLABCOMM      COMM
		WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
		AND     TEMP.STACK_BED_GP = LAYER.STACK_BED_GP
		AND     LAYER.STACK_LAYER_STAT IN ('L', 'U','S')
		AND     LAYER.STOCK_ID = COMM.SLAB_NO(+)
		*/		
		String  queryCode = "ym.common.dao.selectBYDMapInfo";
		return super.findList(queryCode, new Object[] { col, bed });
	}
	
	/**
	 * B열연 슬라브맵 정보를 리턴한다.
	 * @param col	적치열
	 * @param bed	번지
	 * @param layer	단
	 * @return
	 */
	public List readBYDMapInfoNEW(String col, String stockid) {
		 		
		String  queryCode = "ym.common.dao.selectBYDMapInfoNEW";
		return super.findList(queryCode, new Object[] { col, stockid });
	}

	/**
	 * A열연 슬라브맵 정보를 리턴한다.(MCH)
	 * @param col	적치열
	 * @return
	 */
	public List readAYDMapInfo(String col) {
		/*
		WITH TEMP AS (
		    SELECT  COL.YD_GP,
		            COL.BAY_GP,
		            COL.STACK_COL_USAGE_CD,
		            STACKER.STACK_COL_GP,
		            STACKER.STACK_BED_GP,
		            STACKER.STACK_BED_QNTY_MAX
		    FROM    TB_YM_STACKCOL      COL,
		            TB_YM_STACKER   	STACKER			
		    WHERE   COL.STACK_COL_GP = ?
		    AND     COL.STACK_COL_GP = STACKER.STACK_COL_GP			
		)
		SELECT LAYER.STACK_COL_GP || LAYER.STACK_BED_GP  AS BEDADDRESS,
		          TEMP.STACK_BED_QNTY_MAX,
		        (
		        SELECT  COUNT(C.STOCK_ID)
		        FROM    TB_YM_STACKLAYER C
		        WHERE   C.STACK_COL_GP = LAYER.STACK_COL_GP
		        AND     C.STACK_BED_GP = LAYER.STACK_BED_GP
		        AND     C.STACK_LAYER_STAT IN ('L','U','S')
		        ) AS STACK_BED_QNTY_CURR,
		        LAYER.STOCK_ID,
		        LAYER.STACK_LAYER_GP,
		        LAYER.STACK_LAYER_X_AXIS,
		        LAYER.STACK_LAYER_Y_AXIS,
		        POS.XPCD,
		        POS.XMCD,
		        POS.YPCD,
		        POS.YMCD,
		        DECODE(LAYER.STACK_LAYER_ACTIVE_STAT, 'O', 'Y', 'N') AS USE_YN,
		        NVL(COMM.ORD_NO,'') ||
		        NVL(COMM.ORD_DTL,'') AS PRODUCT_NO,
		        COMM.SLAB_T,
		        COMM.SLAB_W,
		        COMM.SLAB_LEN,
		        COMM.SLAB_WT
		FROM    TEMP,
		        (
				SELECT  MAX(DECODE(STACK_RULE_CD,'X-CD',STACK_RULE_MAX ,''))AS XPCD,
						MAX(DECODE(STACK_RULE_CD,'X-CD',STACK_RULE_MIN ,'')) AS XMCD,
						MAX(DECODE(STACK_RULE_CD,'Y-CD',STACK_RULE_MAX ,'')) AS YPCD,
						MAX(DECODE(STACK_RULE_CD,'Y-CD',STACK_RULE_MIN ,'')) AS YMCD
				FROM    TEMP,
						TB_YM_STACKRULE RULE
				WHERE   TEMP.YD_GP  = RULE.YD_GP
				AND     TEMP.BAY_GP = RULE.BAY_GP
				AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
				AND     STACK_RULE_CD IN('Y-CD','X-CD')                       
		        ) POS,
		        TB_YM_STACKLAYER    LAYER,
		        TB_PM_SLABCOMM      COMM
		WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
		AND     TEMP.STACK_BED_GP = LAYER.STACK_BED_GP
		AND     LAYER.STACK_LAYER_STAT IN ('L', 'U','S')
		AND     LAYER.STOCK_ID = COMM.SLAB_NO(+)
		ORDER BY LAYER.STACK_BED_GP ASC, LAYER.STACK_LAYER_GP ASC 
		*/		
		String  queryCode = "ym.common.dao.selectAYDMapInfo";
		return super.findList(queryCode, new Object[] { col});
	}
	
	/**
	 * B열연 코일맵 정보를 리턴한다.
	 * @param col	적치열
	 * @param bed	번지
	 * @param layer	단
	 * @return
	 */
	public List readBYDMapInfo(String col, String bed, String layer) {
	/*
	WITH TEMP AS (
	    SELECT  YD_GP,
	            BAY_GP,
	            STACK_COL_GP,
	            STACK_COL_USAGE_CD
	    FROM    TB_YM_STACKCOL
	    WHERE   STACK_COL_GP = ?
	)
	SELECT  LAYER.STOCK_ID,
	        LAYER.STACK_COL_GP || LAYER.STACK_BED_GP || LAYER.STACK_LAYER_GP AS SKIDADDRESS,
	        LAYER.STACK_LAYER_X_AXIS,
	        LAYER.STACK_LAYER_Y_AXIS,
	        POS.XPCD,
	        POS.XMCD,
	        POS.YPCD,
	        POS.YMCD,
	        DECODE(LAYER.STACK_LAYER_ACTIVE_STAT, 'O', 'Y', 'N') AS USE_YN,
	        NVL(COMM.ORD_NO,'') ||
	        NVL(COMM.ORD_DTL,'') AS PRODUCT_NO,
	        COMM.COIL_T,
	        COMM.COIL_W,
	        COMM.CURR_COIL_LEN AS COIL_LEN,
	        COMM.COIL_WT,
	        COMM.COIL_OUTDIA,
	        COMM.BRANCH_CD,
	        COMM.COOL_METHOD,
	        COMM.EXTEND_CONVEYOR_BRANCH_CD
	FROM    TEMP,
	        (
	        SELECT  XPOS.XPCD,
	                XPOS.XMCD,
	                YPOS.YPCD,
	                YPOS.YMCD
	        FROM    (
	                SELECT  STACK_RULE_MAX AS XPCD,
	                        STACK_RULE_MIN AS XMCD
	                FROM    TEMP,
	                        TB_YM_STACKRULE RULE
	                WHERE   TEMP.YD_GP  = RULE.YD_GP
	                AND     TEMP.BAY_GP = RULE.BAY_GP
	                AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
	                AND     STACK_RULE_CD = 'X-CD'
	                )XPOS,
	                (
	                SELECT  STACK_RULE_MAX AS YPCD,
	                        STACK_RULE_MIN AS YMCD
	                FROM    TEMP,
	                        TB_YM_STACKRULE RULE
	                WHERE   TEMP.YD_GP  = RULE.YD_GP
	                AND     TEMP.BAY_GP = RULE.BAY_GP
	                AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
	                AND     STACK_RULE_CD = 'Y-CD'        
	                )YPOS                
	        ) POS,
	        (
	         SELECT 
	             DECODE(A.STACK_LAYER_STAT,'P','',A.STOCK_ID) AS STOCK_ID,
	             A.STACK_LAYER_STAT,
	             A.STACK_BED_GP,
	             A.STACK_LAYER_GP,
	             A.STACK_COL_GP,
	             A.STACK_LAYER_X_AXIS,
	             A.STACK_LAYER_Y_AXIS,
	             A.STACK_LAYER_ACTIVE_STAT
	         FROM TB_YM_STACKLAYER A    
	        )LAYER,
	        TB_PM_COILCOMM      COMM
	WHERE   TEMP.STACK_COL_GP   = LAYER.STACK_COL_GP
	AND     LAYER.STACK_BED_GP  = ?
	AND     LAYER.STACK_LAYER_GP= ?
	AND     LAYER.STOCK_ID = COMM.COIL_NO(+)
	*/		
		String  queryCode = "ym.common.dao.selectBYDMapInfo1";
		return super.findList(queryCode, new Object[] { col, bed, layer });
	}
	
	/**
	 * B열연 코일맵 정보를 리턴한다.
	 * @param col	적치열
	 * @param bed	번지
	 * @param layer	단
	 * @return
	 */
	public List readCoilBYDMapInfo(String col, String bed) {
	/*
	WITH TEMP AS (
	    SELECT  YD_GP,
	            BAY_GP,
	            STACK_COL_GP,
	            STACK_COL_USAGE_CD
	    FROM    TB_YM_STACKCOL
	    WHERE   STACK_COL_GP = ?
	)
	SELECT  LAYER.STOCK_ID,
	        LAYER.STACK_COL_GP || LAYER.STACK_BED_GP || LAYER.STACK_LAYER_GP AS SKIDADDRESS,
	        LAYER.STACK_LAYER_X_AXIS,
	        LAYER.STACK_LAYER_Y_AXIS,
	        POS.XPCD,
	        POS.XMCD,
	        POS.YPCD,
	        POS.YMCD,
	        DECODE(LAYER.STACK_LAYER_ACTIVE_STAT, 'O', 'Y', 'N') AS USE_YN,
	        NVL(COMM.ORD_NO,'') ||
	        NVL(COMM.ORD_DTL,'') AS PRODUCT_NO,
	        COMM.COIL_T,
	        COMM.COIL_W,
	        COMM.CURR_COIL_LEN AS COIL_LEN,
	        COMM.COIL_WT,
	        COMM.COIL_OUTDIA,
	        COMM.BRANCH_CD,
	        COMM.COOL_METHOD,
	        COMM.EXTEND_CONVEYOR_BRANCH_CD
	FROM    TEMP,
	        (
	        SELECT  XPOS.XPCD,
	                XPOS.XMCD,
	                YPOS.YPCD,
	                YPOS.YMCD
	        FROM    (
	                SELECT  STACK_RULE_MAX AS XPCD,
	                        STACK_RULE_MIN AS XMCD
	                FROM    TEMP,
	                        TB_YM_STACKRULE RULE
	                WHERE   TEMP.YD_GP  = RULE.YD_GP
	                AND     TEMP.BAY_GP = RULE.BAY_GP
	                AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
	                AND     STACK_RULE_CD = 'X-CD'
	                )XPOS,
	                (
	                SELECT  STACK_RULE_MAX AS YPCD,
	                        STACK_RULE_MIN AS YMCD
	                FROM    TEMP,
	                        TB_YM_STACKRULE RULE
	                WHERE   TEMP.YD_GP  = RULE.YD_GP
	                AND     TEMP.BAY_GP = RULE.BAY_GP
	                AND     TEMP.STACK_COL_USAGE_CD = RULE.STACK_COL_USAGE_CD
	                AND     STACK_RULE_CD = 'Y-CD'        
	                )YPOS
	        ) POS,
	        (
	         SELECT 
	             DECODE(A.STACK_LAYER_STAT,'P','',A.STOCK_ID) AS STOCK_ID,
	             A.STACK_LAYER_STAT,
	             A.STACK_BED_GP,
	             A.STACK_LAYER_GP,
	             A.STACK_COL_GP,
	             A.STACK_LAYER_X_AXIS,
	             A.STACK_LAYER_Y_AXIS,
	             A.STACK_LAYER_ACTIVE_STAT
	         FROM TB_YM_STACKLAYER A    
	        )LAYER,
	        TB_PM_COILCOMM      COMM
	WHERE   TEMP.STACK_COL_GP       = LAYER.STACK_COL_GP
	AND     LAYER.STACK_BED_GP      = ?
	AND     LAYER.STOCK_ID          = COMM.COIL_NO(+)
	ORDER BY STACK_LAYER_GP
	*/		
		String  queryCode = "ym.common.dao.selectBYDMapInfo2";
		return super.findList(queryCode, new Object[] { col, bed });
	}

	/**
	 * 코일정보를 리턴한다.
	 * @param stockId	저장품ID
	 * @return
	 */
	public JDTORecord readCoilInfo(String stockId) {
		String  queryCode = "ym.common.dao.selectCoilInfo";
		/*
		--ym.common.dao.selectCoilInfo
		--코일정보를 리턴한다.
		SELECT  STOCK.STOCK_ID,
		        STOCK.CAR_CARD_NO,
		        STOCK.STOCK_ITEM,
		        STOCK.KEEPSTOCK_STL_YN AS KEEPSTOCK_STL_GP,
		        STOCK.STOCK_ID AS CTS_COIL_NO,
		        WBOOK.SCH_WORK_KIND,
		        COMM.CURR_PROG_CD
		FROM    TB_YM_STOCK STOCK,
		        TB_YM_WBOOK WBOOK,
		        TB_PM_COILCOMM COMM
		WHERE   STOCK.STOCK_ID = ?
		AND     STOCK.WBOOK_ID = WBOOK.WBOOK_ID(+)
		AND     STOCK.STOCK_ID = COMM.COIL_NO(+)
		*/
		return super.findByPrimaryKey(queryCode, new Object[] { stockId });	    
	}
	
	/**
	 * 스케쥴 정보를 리턴한다.
	 * @param stockId
	 * @return
	 */
	public JDTORecord readCancelSchStock(String stockId) {
		String  queryCode = "ym.common.dao.selectCancelSchStock";
		/*
		--ym.common.dao.selectCancelSchStock
		--스케쥴정보를 리턴한다.
		SELECT  SCH_ID,
		             SCH_WORK_KIND
		FROM     TB_YM_SCH
		WHERE   STOCK_ID = ?
		AND       SUBSTR(CRANE_WORD_PUT_LOC, 3, 2) = 'TC'
		*/
		return super.findByPrimaryKey(queryCode, new Object[] { stockId }); 
	}
	
	/**
	 * AS-IS 스케쥴코드를 리턴한다.
	 * @param schKind	TO-BE 스케쥴코드
	 * @return
	 */
	public String readLegacySchCode(String schKind) {
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getLegacySchCode";
		/*
		SELECT class2_name2 as sch_work_kind
		FROM tb_cm_cdclass2 
		WHERE class2_cd  	= :class3_cd
		AND   type_cd 		= 'YM104'
		AND   class1_cd 	= '1'
		*/
		JDTORecord dto = super.findByPrimaryKey(queryCode, new Object[] { schKind });
		return dto != null ? StringHelper.evl(dto.getFieldString("sch_work_kind"), "") : "";
	}

	/**
	 * 적치단의 코일정보를 리턴한다.
	 * @param col	적치열
	 * @param bed	적치번지
	 * @param layer	적치단
	 * @return
	 */
	public JDTORecord readAddressInfo(String col, String bed, String layer) {
        String queryCode = "ym.common.dao.selectAddressInfo";
        /*
        --ym.common.dao.selectAddressInfo
	--적치단의 코일정보를 리턴한다.
	SELECT  DISTINCT
	    COMM.COIL_NO,
	    COMM.COIL_T,
	    COMM.COIL_W,
	    COMM.CURR_COIL_LEN AS COIL_LEN,
	    COMM.GROSS_WEIGH_WT,
	    COMM.COIL_OUTDIA,
	    NVL(COMM.ORD_NO,'') || NVL(COMM.ORD_DTL,'') AS PRODUCT_NO
	FROM    TB_YM_STACKLAYER    LAYER,
	    		TB_PM_COILCOMM      COMM
	WHERE   LAYER.STACK_COL_GP = ?
	AND     LAYER.STACK_BED_GP   = ?
	AND     LAYER.STACK_LAYER_GP= ?
	AND     LAYER.STOCK_ID      = COMM.COIL_NO(+)
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ col, bed, layer });
	}
	
	/**
	 * 현재 장입순위 저장품 정보를 리턴한다.
	 * @return
	 */
	public List readCurZoinStock() {
	 String queryCode = "ym.common.dao.selectCurZoinStock";
	 /*
	 --ym.common.dao.selectCurZoinStock
	--현재 장입순위 저장품 정보를 리턴한다.
	SELECT  A.STOCK_ID
	FROM    (
	        SELECT  MIN(TO_NUMBER(NVL(CHARGE_LOT_NO,99999999))) AS CHARGE_LOT_NO
	        FROM    TB_YM_STOCK
	        WHERE   CHARGE_LOT_NO IS NOT NULL
	        ) ZOIN, TB_YM_STOCK A
	WHERE   A.CHARGE_LOT_NO LIKE '%' || ZOIN.CHARGE_LOT_NO
	 */
        return super.findList(queryCode);
	}
	
	/**
	 * 현재 W/B보급되어야 할 장입LOT 번호를 가져온다.
	 * @return
	 */
	public JDTORecord readCurZoinLotNo() {
        String queryCode = "ym.common.dao.selectCurZoinLotNo";
        /*
       	SELECT  ZOIN.CHARGE_LOT_NO
		FROM    (
		        SELECT  MIN(TO_NUMBER(NVL(A.CHARGE_LOT_NO,99999999))) AS CHARGE_LOT_NO
		        FROM    TB_YM_STOCK A,
		        	       TB_YM_STACKLAYER B
		        WHERE   A.CHARGE_LOT_NO IS NOT NULL
		        AND        A.STOCK_ID = B.STOCK_ID
		        AND        B.STACK_COL_GP NOT IN ('2ABK01','2ABK02')
		        AND        B.STACK_LAYER_STAT  IN ('L','S','U')
		        ) ZOIN
		WHERE   ROWNUM = 1
        */
        return super.find(queryCode);
	}
	
	/**
	 * 현재 W/B보급되어야 할 장입LOT 번호를 생산통제에서 가져온다.
	 * @return
	 */
	public JDTORecord readCurZoinLotNo_FromCT(String sGp) {
        String queryCode = "ym.common.dao.selectCurZoinLotNo_FromCT";
        /*
       	SELECT 
		    TO_NUMBER(MIN(C.YD_CHG_NO)) AS YD_CHG_NO
		FROM USRCTA.TB_CT_L_HRMILLWO C
		   ,(SELECT CHG_WO_FR_PNT AS CHG_WO_FR_PNT
		           ,CHG_WO_TO_PNT AS CHG_WO_TO_PNT
		      FROM USRCTA.TB_CT_J_MILLWOIDX
		     WHERE CT_RCV_SEQ = (SELECT MAX(CT_RCV_SEQ) AS MAX_CT_RCV_SEQ
		                           FROM USRCTA.TB_CT_J_MILLWOIDX
		                          WHERE PTOP_PLNT_GP= :GP ) 
		       AND PTOP_PLNT_GP= :GP) D                     
		WHERE C.REFUR_CHG_PLN_SERNO >=D.CHG_WO_FR_PNT
		  AND C.REFUR_CHG_PLN_SERNO <=D.CHG_WO_TO_PNT 
		  AND C.PTOP_PLNT_GP= :GP
		  AND C.CT_MILL_SPEC_WRK_STAT_GP >= '2'   -- 생산통제사양작업상태구분
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ sGp,sGp,sGp });        
	}
	
	public JDTORecord readCurZoinLotNo_Bay() {
        String queryCode = "ym.common.dao.selectCurZoinLotNoBay";
        /*
       	SELECT  STOCK.WBOOK_ID,
		        STOCK.STOCK_ID,
		        LAYER.STACK_COL_GP,
		        SUBSTR(LAYER.STACK_COL_GP,2,1) AS BAY
		FROM    TB_YM_STOCK         STOCK,
		        TB_YM_STACKLAYER    LAYER
		WHERE   STOCK.CHARGE_LOT_NO = 
		        (
		        SELECT  MIN(TO_NUMBER(CHARGE_LOT_NO, 999999))
		        FROM    TB_YM_STOCK
		        WHERE   CHARGE_LOT_NO IS NOT NULL
		        AND     CHARGE_LOT_NO NOT IN 
		                (
		                SELECT  MIN(TO_NUMBER(CHARGE_LOT_NO, 999999))
		                FROM    TB_YM_STOCK
		                WHERE   CHARGE_LOT_NO IS NOT NULL
		                )
		        )
		AND     STOCK.STOCK_ID = LAYER.STOCK_ID
		AND     LAYER.STACK_COL_GP LIKE '2' || '%'
		AND     LAYER.STACK_LAYER_STAT IN( 'L','S','U')
		AND     ROWNUM = 1
        */
        return super.find(queryCode);
	}
	
	public JDTORecord readCurZoinLotNo_CurBay(String sCurColGp) {
        String queryCode = "ym.common.dao.selectCurZoinLotNo_curbay";
        /*
       	SELECT  ZOIN.CHARGE_LOT_NO
		FROM    (
		        SELECT  MIN(TO_NUMBER(NVL(A.CHARGE_LOT_NO,99999999))) AS CHARGE_LOT_NO
		        FROM    TB_YM_STOCK A,
		        	       TB_YM_STACKLAYER B
		        WHERE   A.CHARGE_LOT_NO IS NOT NULL
		        AND        A.STOCK_ID = B.STOCK_ID
		        AND        B.STACK_COL_GP LIKE :COL||'%'
		        ) ZOIN
		WHERE   ROWNUM = 1
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ sCurColGp });        
	}
	
	/**
	 * 이미 장입된 저장품의 장입순번정보를 가져온다.
	 * @return
	 */
	public JDTORecord readCurZoinLotNo(String stockId) {
        String queryCode = "ym.common.dao.selectCurrPCLotNo";
        /*
		SELECT LPAD(CTS_RELAY_SADDLE,6,'0') AS CHARGE_LOT_NO
		FROM TB_YM_STOCK 
		WHERE STOCK_ID = :STOCK_ID
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ stockId });
	}

	/**
	 * 코일 정보를 리턴한다.
	 * @param stockId
	 * @return
	 */
	public JDTORecord readCommonCoilInfo(String stockId) {
        String queryCode = "ym.common.dao.selectCommonCoilInfo";
        /*
        --ym.common.dao.selectCommonCoilInfo
	--공통 코일 정보를 리턴한다.
	SELECT  CURR_PROG_CD, --현재 진도 CODE
	             COIL_NO,
	             COIL_T,
	             COIL_W,
	             CURR_COIL_LEN AS COIL_LEN,
	             GROSS_WEIGH_WT,
	             COIL_WT AS NET_WEIGH_WT, --COIL 중량
	             COIL_OUTDIA,
	             NVL(ORD_NO,'') || NVL(ORD_DTL,'') AS PRODUCT_NO
	FROM     TB_PM_COILCOMM
	WHERE   COIL_NO = ?
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ stockId });
	}

	/**
	 * 차량 상차 수량 정보를 리턴한다.
	 * @param yd	야드구분
	 * @param bay	동구분
	 * @param crNo	크레인번호
	 * @param kind	스케쥴작업종류
	 * @return
	 */
	public JDTORecord readCarLoadCnt(String yd, String bay, String crNo, String kind) {
        String queryCode = "ym.common.dao.selectCarLoadCnt";        
        return super.findByPrimaryKey(queryCode, new Object[]{ yd, bay, crNo, kind } );	    
	}

	/**
	 * 현재 크레인의 정보를 리턴한다.
	 * @param ydGp		야드구분
	 * @param reEquipGp	AS-IS 설비구분
	 * @return
	 */
	public JDTORecord readCurrCRInfo(String ydGp, String reCraneNo) {
        String queryCode = "ym.common.dao.selectCurrCRInfo";
        /*
        --ym.common.dao.selectCurrCRInfo
	--현재 크레인 정보를 리턴한다.
	SELECT  EQUIP.EQUIP_GP,     --설비구분
	        EQUIP.EQUIP_STAT,   --설비상태
	        EQUIP.WPROG_STAT,   --작업상태
	        DECODE(EQUIP.WORK_MODE,'O','1','C','0') AS WORK_MODE, --작업모드
	        DECODE(EQUIP.EQUIP_STAT, 'O', '0', 'C', '1') AS TRO_REC_YN, --고장 유무
	        DECODE(EQUIP.EQUIP_STAT, 'O', '1','C','4') AS STATUS_ID, --상태구분
	        SCH.SCH_ID,
	        SCH.CAR_CARD_NO,
	        SCH.SCH_WORK_KIND,
	        SCH.CRANE_WORD_UP_LOC,
	        SCH.CRANE_WORD_PUT_LOC,
	        SCH.STOCK_ID,
	        COMM.COIL_T,        --COIL 두께
	        COMM.COIL_W,        --COIL 폭
	        COMM.CURR_COIL_LEN AS COIL_LEN,      --COIL 길이
	        COMM.COIL_WT AS NET_WEIGH_WT --COIL 중량
	FROM    TB_YM_EQUIP     EQUIP,
	        TB_YM_SCH       SCH,
	        TB_PM_COILCOMM  COMM
	WHERE   EQUIP.EQUIP_GP = ?
	AND     EQUIP.WBOOK_ID = SCH.SCH_ID(+)
	AND     SCH.STOCK_ID   = COMM.COIL_NO(+)
        */
        return super.findByPrimaryKey(queryCode, readEquipGp(ydGp, reCraneNo));
	}

	/**
	 * 백업 요청한 저장품 정보를 리턴한다.
	 * @param equipNo	설비번호
	 * @param stockId	저장품ID
	 * @return
	 */
	public JDTORecord readBackUpData(String stockId, String equipNo) {
        String queryCode = "ym.common.dao.selectBackUpData";        
        /*
        --ym.common.dao.selectBackUpData
	--백업 요청한 저장품 정보를 리턴한다.
	SELECT SCH_ID,
	            WBOOK_ID,
	            SCH_RULE_ID,
	            SCH_WORK_KIND,
	            SCH_WPREFER,
	            CRANE_WORD_UP_LOC,
	            CRANE_WORD_PUT_LOC,            
	            SUBSTR(CRANE_WORD_UP_LOC, 1, 6) AS UP_STACK_COL_GP,
	            SUBSTR(CRANE_WORD_UP_LOC, 7, 2) AS UP_STACK_BED_GP,
	            SUBSTR(CRANE_WORD_UP_LOC, 9, 2) AS UP_STACK_LAYER_GP,
	            SUBSTR(CRANE_WORD_PUT_LOC, 1, 6) AS PUT_STACK_COL_GP,
	            SUBSTR(CRANE_WORD_PUT_LOC, 7, 2) AS PUT_STACK_BED_GP,
	            SUBSTR(CRANE_WORD_PUT_LOC, 9, 2) AS PUT_STACK_LAYER_GP            
	FROM    TB_YM_SCH
	WHERE  STOCK_ID = ?
	AND      SCH_WORK_EQUIP_NO = ?
	ORDER BY SCH_ID ASC
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ stockId, equipNo });	    
	}
	
	/**
	 * 적치단 테이블의 저장품 정보를 리턴한다.
	 * @param col		적치열
	 * @param bed		적치번지
	 * @param layer		적치단
	 * @param stockId	저장품ID
	 * @return
	 */
	public JDTORecord readBackUpData(String col, String bed, String layer, String stockId) {
        String queryCode = "ym.common.dao.selectBackUpData1";        
        return super.findByPrimaryKey(queryCode, new Object[]{ stockId });	    
	}
	
	/**
	 * 차량 상차작업 취소에 대한 스케쥴 편성 정보를 리턴한다.
	 * @param wbookId
	 * @return
	 */
    public boolean readCancelOrdOfSch(String wbookId) {
        String queryCode = "ym.common.dao.selectCancelOrdOfSch";   
        /*
        --ym.common.dao.selectCancelOrdOfSch
	--이송상차 취소에 대한 스케쥴 정보를 리턴한다.
	SELECT SCH_ID 
	FROM    TB_YM_SCH
	WHERE  WBOOK_ID = ?
        */     
        JDTORecord dto = super.findByPrimaryKey(queryCode, new Object[]{ wbookId });
        return dto != null && dto.size() > 0 ? true : false;
    }

	/**
	 * 적치열 테이블에 카드번호가 존재하는지 리턴한다.
	 * @param pos		차량정지위치
	 * @return
	 */
	public JDTORecord readCardNo(String pos) {
		/*
		SELECT  CAR_CARD_NO
		FROM    TB_YM_STACKCOL
		WHERE  STACK_COL_GP = ?
		*/
        String qcd = "ym.common.dao.selectCardNo_PIDEV";
        
//        qcd = ymCommDAO.getYmRulePI("", "readCardNo", "YM0001", qcd, "APPPI0", "*", "*" );        
        
        return super.findByPrimaryKey(qcd, new Object[]{ pos });
	}

	/**
	 * 적치열 테이블에 카드번호가 존재하는지 리턴한다.
	 * @param pos		차량정지위치
	 * @return
	 */
	public JDTORecord readCardNoT(String pos) {

        String qcd = "ym.common.dao.selectCardNoT_PIDEV";
        return super.findByPrimaryKey(qcd, new Object[]{ pos});
	}
	
	/**
	 * 적치열 테이블에 카드번호가 존재하는지 리턴한다. 최규성 2009-10-16
	 * pos 인자는 사용하지 않음. 최규성 2009-11-27
	 * @param query     조회쿼리코드
	 * @param pos		차량정지위치
	 * @param cardNo    차량카드번호
	 * @return
	 */
	public JDTORecord readCardNo(String query, String pos,String cardNo) {
		//return super.findByPrimaryKey(query, new Object[]{ pos, cardNo });
		return super.findByPrimaryKey(query, new Object[]{  cardNo });
	}
	
	/**
	 * 적치열 테이블에 카드번호의 적치열이 존재하는지 리턴한다.
	 * @param pos		차량정지위치
	 * @return
	 */
	public JDTORecord readStackCol(String wloccd, String ydpntcd) {
		/*
		SELECT  STACK_COL_GP
		FROM    TB_YM_STACKCOL
		WHERE  WLOC_CD = ?
		  and YD_PNT_CD = ?
		*/
        String qcd = "ym.common.dao.selectStackCol";
        return super.findByPrimaryKey(qcd, new Object[]{ wloccd ,ydpntcd });
	}
	
	
	/**
	 * 적치열 테이블에 카드번호가 존재하는지 리턴한다.
	 * @param yd		야드구분
	 * @param cardNo	차량카드번호
	 * @return
	 */
	public JDTORecord readCardNo(String yd, String cardNo) {
		/*
		SELECT  CAR_CARD_NO
		FROM    TB_YM_STACKCOL
		WHERE   YD_GP       = ? 
		AND     CAR_CARD_NO = ?
		AND     SECT_GP IN ('TR', 'PT')
		*/
        String qcd = "ym.common.dao.selectCardNo1";
        return super.findByPrimaryKey(qcd, new Object[]{ yd, cardNo });
	}
	/**
	 * 팔레트의 저장품을 리턴한다.
	 * @param col	적치열
	 * @param bed	번지
	 * @return
	 */
	public List readStockOfPallet(String col, String bed) {
        String qcd = "ym.common.dao.selectStockOfPallet";
        /*
        --ym.common.dao.selectStockOfPallet
	--'이송 설비 구분', '이송 설비 BED 구분', '이송 설비 단 구분'을 UPDATE
	SELECT  STOCK_ID,
	             STACK_COL_GP,
	             STACK_BED_GP,
	             STACK_LAYER_GP
	FROM     TB_YM_STACKLAYER
	WHERE   STACK_COL_GP = ?
	AND       STACK_BED_GP = ?
	AND       STOCK_ID IS NOT NULL
        */
        return super.findList(qcd, new Object[]{ col, bed });
	}

	/**
	 * 스케쥴정보를 리턴한다.
	 * @param wbookId	작업예약ID
	 * @return
	 */
	public List readSchInfo(String yd, String bay, String crNo) {
        String qcd = "ym.common.dao.selectSchInfo1";
        /*
		SELECT  SCH.WBOOK_ID,
		        SCH.SCH_WORK_KIND
		FROM    (
		        SELECT  WBOOK_ID,
		                SCH_WORK_KIND
		        FROM    TB_YM_SCH
		        WHERE   YD_GP   = ?
		        AND     BAY_GP  = ?
		        AND     SCH_WORK_EQUIP_NO = ?
		        ORDER BY  SCH_WPREFER, SCH_ID
		        ) SCH
		WHERE   ROWNUM = 1
		*/
        return super.findList(qcd, new Object[]{ yd, bay, crNo });
	}

	/**
	 * 스케쥴정보를 리턴한다.
	 * @param schRuleId	스케쥴기준ID
	 * @param yd		야드구분
	 * @param bay		동구분
	 * @param schKind	스케쥴종류
	 * @param equipNo	설비번호
	 * @return
	 */
	public JDTORecord readSchInfo(
	        String schRuleId, String yd, String bay, String schKind, String equipNo) {
        String qcd = "ym.common.dao.selectSchInfo";
        /*
        --ym.common.dao.selectSchInfo
	--크레인 스케쥴 정보를 리턴한다.
	SELECT  SCH_ID,
	        STOCK_ID,
	        SCH_RULE_ID,
	        YD_GP,
	        BAY_GP,
	        SCH_WORK_EQUIP_NO,
	        SCH_WORK_STAT,
	        SCH_WPREFER,
	        SCH_WORK_KIND,
	        SCH_WORK_AID_YN,
	        SCH_WORK_GRIP_LOT_YN,
	        CRANE_WORD_UP_LOC,
	        WBOOK_LOC_DECISION_METHOD,
	        CRANE_WORD_PUT_LOC,
	        SUBSTR(CRANE_WORD_PUT_LOC, 1, 6) AS COL,
	        SUBSTR(CRANE_WORD_PUT_LOC, 7, 2) AS BED,
	        SUBSTR(CRANE_WORD_PUT_LOC, 9, 2) AS LAYER,
	        SCH_WORK_CAR_NO,
	        SCH_WDEMAND_TYPE,
	        WBOOK_SCH_ACT_DDTT,
	        WBOOK_ID
	FROM    TB_YM_SCH
	WHERE   SCH_RULE_ID = ?
	AND     YD_GP       = ?
	AND     BAY_GP      = ?
	AND     SCH_WORK_KIND       = ?
	AND     SCH_WORK_EQUIP_NO   = ?
	ORDER BY SCH_WPREFER, SCH_ID
        */
        return super.findByPrimaryKey(qcd, new Object[]{ schRuleId, yd, bay, schKind, equipNo });
	}
	
	/**
     * 적치단에 대한 저장품 정보를 리턴한다.
     * @param col	적치열
     * @return
     */
    public List readStackLayer(String col) {
        String queryCode = "ym.common.dao.selectStackLayer";
        /*
        --ym.common.dao.selectStackLayer
	--팔레트의 적치단 정보를 리턴한다.
	SELECT  STOCK_ID
	FROM     TB_YM_STACKLAYER
	WHERE   STACK_COL_GP = ?
	AND       STACK_LAYER_STAT = 'L'
	AND       STOCK_ID IS NOT NULL
        */
        return super.findList(queryCode, new Object[]{ col });
    }
    
	/**
     * 적치단에 대한 저장품 정보를 리턴한다.
     * @param col	적치열
     * @return
     */
    public JDTORecord readStackLayer(String col, String bed, String layer) {
        String queryCode = "ym.common.dao.selectStackLayer1";
        /*
        --ym.common.dao.selectStackLayer1
	--적치단에 저장품 정보를 리턴한다.
	SELECT  STOCK_ID
	FROM     TB_YM_STACKLAYER
	WHERE   STACK_COL_GP = ?
	AND       STACK_BED_GP = ?
	AND       STACK_LAYER_GP = ?
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ col, bed, layer });
    }

    /**
     * 카드번호에 대한 저장품 정보를 리턴한다.
     * @param cardNo	카드번호
     * @return
     */
    public List readStockInfoOfCardNo(String cardNo) {
		/*
		SELECT  STOCK_ID,
	             STOCK_ITEM,
	             FRTOMOVE_EQUIP_GP,
	             FRTOMOVE_EQUIP_BED_GP,
	             FRTOMOVE_EQUIP_LAYER_GP,
				 CAR_CARD_NO
		FROM     TB_YM_STOCK
		WHERE   (CAR_CARD_NO = ? or SHEAR_SUPPLY_DEMAND_DDTT = ?)
		AND     (DEL_YN = 'N' OR DEL_YN IS NULL)
		*/    	
        String queryCode = "ym.common.dao.selectStockInfoOfCardNo";
        return super.findList(queryCode, new Object[]{ cardNo,cardNo });        
    }

    /**
     * 차량번호를 리턴한다.
     * @param cardNo	차량카드번호
     * @return
     */
    public JDTORecord readCarNo(String yd, String cardNo) {
	/*
	SELECT  DECODE(?, '1', 
	            DECODE(LENGTH(CAR_NO), 7, SUBSTR(CAR_NO, 4, 4), 9, SUBSTR(CAR_NO, 6, 4)),
	            CAR_NO) AS CAR_NO, --차량 번호
	        TRANS_COM_CD    --운송 회사 CODE
	FROM USRDMA.TB_DM_CARCARDINFO
	WHERE CARD_NO = ?    --카드번호(1316) 
	*/    	
        String queryCode = "ym.common.dao.selectCarNo_PIDEV";        
        return super.findByPrimaryKey(queryCode, new Object[]{ yd, cardNo });
    }
    /**
     * 구내운송 차량번호를 리턴한다.
     * @param cardNo	차량카드번호
     * @return
     */
    public JDTORecord readCarNo3(String cardNo) {
	/*
	SELECT CAR_NO , TRN_EQP_TP_GP AS TRANS_COM_CD
	  FROM USRTSA.TB_TS_CAR_SPEC_RULL
	 WHERE TRN_EQP_CD=? 
	*/    	
        String queryCode = "ym.common.dao.selectCarNo3";        
        return super.findByPrimaryKey(queryCode, new Object[]{cardNo });
    }
    
    /**
     * 차량번호를 리턴한다.
     * @param cardNo	차량카드번호
     * @return
     */
    public JDTORecord readCarNo2(String yd, String cardNo) {
	/*
	SELECT  DECODE(?, '1', 
	            DECODE(LENGTH(CAR_NO), 7, SUBSTR(CAR_NO, 4, 4), 9, SUBSTR(CAR_NO, 6, 4)),
	            CAR_NO) AS CAR_NO, --차량 번호
	        TRANS_COM_CD    --운송 회사 CODE
	FROM USRDMA.TB_DM_CARCARDINFO
	WHERE CARD_NO = ?    --카드번호(1316) 
	*/    	
        String queryCode = "ym.common.dao.selectCarNo2";        
        return super.findByPrimaryKey(queryCode, new Object[]{ yd,  cardNo });
    }
    
    /**
     * 이송 상차 정보를 리턴한다.
     * @param orderDate	이송상차지시일정
     * @param orderNo	이송상차시지번호
     * @return
     */
    public List readLoadOrderInfo(String orderDate, String orderNo) {
		/*
		--차량카드번호, 저장품 적치열을 리턴한다.
		SELECT  DM.CARD_NO,             -- 차량카드번호
		        DM.FRTOMOVE_WORD_DATE ||
		        DM.FRTOMOVE_WORD_SEQNO AS FRTOMOVE_WORD_DATE_NO,
		        STOCK.WBOOK_ID,         --작업예약ID
		        STOCK.STOCK_ID,         --저장품ID
		        STOCK.STOCK_STAT,     --저장품상태
		        LAYER.STACK_COL_GP,     --적치 열 구분
		        LAYER.STACK_BED_GP,     --적치 번지 구분
		        LAYER.STACK_LAYER_GP,    --적치단 구분
		        DM.CAR_NO                       -- 차량 번호
		FROM    TB_DM_SLABFRTOMOVEWORDCOMM  DM,
		        TB_PO_SLABFRTOMOVE          PO,
		        TB_YM_STOCK                 STOCK,
		        TB_YM_STACKLAYER            LAYER
		WHERE   DM.FRTOMOVE_WORD_DATE   = ?
		AND     DM.FRTOMOVE_WORD_SEQNO  = ?
		AND     DM.FRTOMOVE_WORD_DATE   = PO.FRTOMOVE_WORD_DATE
		AND     DM.FRTOMOVE_WORD_SEQNO  = PO.FRTOMOVE_WORD_SEQNO
		AND     PO.FRTOMOVE_STAT_CD     IN ('2', '1')--출하확인
		AND     PO.SLAB_NO              = STOCK.STOCK_ID
		--AND     STOCK.WBOOK_ID IS NULL
		AND     STOCK.STOCK_ID          = LAYER.STOCK_ID
		AND     STACK_LAYER_STAT IN ('L', 'S', 'U')
		ORDER BY LAYER.STACK_COL_GP, LAYER.STACK_BED_GP, LAYER.STACK_LAYER_GP DESC
		*/    	
        String queryCode = "ym.common.dao.selectLoadOrderInfo";        
        return super.findList(queryCode, new Object[]{ orderDate, orderNo });
    }
    
    /**
     * 차량 출발 정보를 리턴한다.
     * @param orderDate	이송상차지시일정
     * @param orderNo	이송상차시지번호
     * @return
     */
    public List readStartOrderInfo(String orderDate, String orderNo, String cardNo, String dataGp) {
        String queryCode = "ym.common.dao.selectStartOrderInfo";        
        /*
        -- ym.common.dao.selectStartOrderInfo
	SELECT 
	     PS.SLAB_NO_HD AS STOCK_ID, 
	     LPAD(PS.LAYER,2,'0') AS FRTOMOVE_EQUIP_LAYER_GP,
	     PS.CARD_NO,
	     PS.PALETTE_NO,
	     PS.FRTOMOVE_ORD_DATE||PS.FRTOMOVE_ORD_SEQNO AS FRTOMOVE_WORD_DATE_NO,
	     STOCK.WBOOK_ID
	FROM USRPSA.TB_PS_PALETTESTACKINFOIF PS,USRYMA.TB_YM_STOCK STOCK
	WHERE PS.SLAB_NO_HD = STOCK.STOCK_ID
	AND PS.FRTOMOVE_ORD_DATE = ?
	AND PS.FRTOMOVE_ORD_SEQNO = ?
	AND PS.CARD_NO = ?
	AND PS.GP = ?
	AND PS.COL = 1
        */
        return super.findList(queryCode, new Object[]{ orderDate, orderNo, cardNo, dataGp });
    }
    /**
     * 대차에 올려진 저장품을 리턴한다.
     * @param col	적치열
     * @return
     */
    public List readVicCarStock(String col) {
        String queryCode 	= "ym.common.dao.selectVicCarStock";
         /*
        --ym.common.dao.selectVicCarStock
	--대차에 저장품이 실려 있는지 리턴한다.
	SELECT  STOCK_ID,
	        STACK_COL_GP,
	        STACK_BED_GP,
	        STACK_LAYER_GP,
	        STACK_LAYER_STAT
	FROM    TB_YM_STACKLAYER
	WHERE   STACK_COL_GP = ?  
	AND     STOCK_ID IS NOT NULL
	--AND     STACK_LAYER_ACTIVE_STAT = 'O'
        */
        return super.findList(queryCode, new Object[]{ col });
    }
    
    /**
     * 'SLAB 두께', 'SLAB 폭', 'SLAB 길이', 'SLAB 중량' 정보를 리턴한다.
     * @param slabNo	야드구분
     * @return
     */
    public JDTORecord readSlabMatirialInfo(String slabNo) {
        String queryCode = "ym.common.dao.selectSlabMatirialInfo";
       /*
       		--ym.common.dao.selectSlabMatirialInfo

			SELECT  SLAB_NO,
			        PLAN_SLAB_NO,               -- 예정 SLAB 번호
			        BUY_SLAB_NO,                --구입 SLAB 번호
			        ORD_NO,                     --주문 번호
			        ORD_DTL,                    --주문 행번
			        CC_PLNT_GP  AS PLANT_GP,    --공장구분
			        SLAB_T      AS SLAB_T,      --SLAB 두께
			        SLAB_W      AS SLAB_W,	    --SLAB 폭
			        SLAB_LEN    AS SLAB_LEN,	--SLAB 길이
			        CAL_SLAB_WT AS SLAB_WT,	    --SLAB 중량
			        CURR_PROG_CD,
			        HEAT_NO,
			        SPEC_ABBSYM,		        -- 규격약호
			        INGR_STAMP_GRADE,
			        REAGENT_PICK_TARGET_YN,     -- 시편채취유무
					REAGENTPICK_DONE_YN,        -- 시편완료유무
					SCARFING_YN,                -- Scarfing유무
					SCARFING_DONE_YN,           -- Scarfing완료유무
			        WO_MSLAB_RPR_MTD,	        -- Scarfing Pattern
					SCARFING_DEPTH,		        -- Scarfing 깊이
					'' AS INGR_C,				-- 성분C
					ORD_HCR_GP,                 -- WCR/CCR 구분
					DECODE(ORD_HCR_GP,NULL,'0',
			               DECODE(LEAST(TRUNC((SYSDATE - SLAB_CREATE_DDTT)*24),DECODE(SCARFING_YN,'Y',24,12)),DECODE(SCARFING_YN,'Y',24,12),'0',NULL,'0','1')             
			        )AS TIMES
			FROM  (SELECT 
			        *
			       FROM VW_YD_SLABCOMM A, TB_QM_BUYSLABINFO B
			       WHERE A.MSLAB_NO = B.MSLAB_NO(+)
			      )SLABCOMM
			WHERE SLABCOMM.SLAB_NO = :SLAB_NO
       */
        return super.findByPrimaryKey(queryCode, new Object[]{ slabNo });
    }
    
    /**
     * TO-BE 설비번호를 AS-IS 설비번호로 리턴한다.
     * @param ydGp		야드구분
     * @param craneNo	TO-BE 설비번호	
     */
    public String readEquipGpOfToBe(String ydGp, String craneNo) {
		/*
		SELECT CLASS3_NAME2 AS EQUIPNO
		  FROM TB_CM_CDCLASS3
		WHERE TYPE_CD='YM002'
		  AND CLASS1_CD='EQPNO'
		  AND CLASS2_CD = ?      --야드구분(1)
		  AND CLASS3_CD = ?      --신설비코드(1ACR71)
		 */    	
        String queryCode = "ym.common.dao.convertEquipNo2";
        JDTORecord equip = super.findByPrimaryKey(queryCode, new Object[]{ ydGp, craneNo });
        if(equip != null) {
            return StringHelper.evl(equip.getFieldString("EQUIPNO"), "");
        }else {            
            return "";
        }
    }
    
    /**
     * CTS 초기화 정보를 리턴한다.
     * @return
     */
    public List readAllCTSIniInfo() {
        String queryCode = "ym.common.dao.selectAllCTSIniInfo";
        /*
        --ym.common.dao.selectAllCTSIniInfo
	--CTS 정보를 리턴한다.
	SELECT  EQUIP.EQUIP_GP,
	        DECODE(EQUIP.EQUIP_STAT, 'C', '9', 
	            'O', DECODE(LAYER.STACK_LAYER_STAT, 'L', '1', '0')) AS COIL_YN, --적재 상태
	        LAYER.STOCK_ID AS STACK_STOCK,  --저장품 ID
	        STOCK.STOCK_ID,                 --저장품 ID
	        STOCK.CTS_RELAY_SADDLE,         --목적동
	        NVL(COMM.ORD_NO, '') ||
	        NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	        COMM.COIL_T,                            --COIL 두께
	        COMM.COIL_W,                            --COIL 폭
	        COMM.COIL_OUTDIA,                       --COIL 외경
	        COMM.COIL_WT AS NET_WEIGH_WT, --COIL 중량
	        COMM.CURR_COIL_LEN AS COIL_LEN --COIL 길이
	FROM    TB_YM_EQUIP         EQUIP,
	        TB_YM_STACKLAYER    LAYER,
	        TB_YM_STOCK         STOCK,
	        TB_PM_COILCOMM      COMM
	WHERE   (EQUIP.EQUIP_GP = '1XTC01' OR EQUIP.EQUIP_GP = '1XTC02')
	AND     EQUIP.EQUIP_GP = LAYER.STACK_COL_GP
	AND     LAYER.STOCK_ID = STOCK.STOCK_ID(+)
	AND     LAYER.STOCK_ID = COMM.COIL_NO(+)
	
	UNION ALL
	
	SELECT  EQUIP.EQUIP_GP,
	        DECODE(EQUIP.EQUIP_STAT, 'C', '9', 
	            'O', DECODE(LAYER.STACK_LAYER_STAT, 'L', '1', '0')) AS COIL_YN, --적재 상태
	        LAYER.STOCK_ID AS STACK_STOCK,  --저장품 ID
	        STOCK.STOCK_ID,                 --저장품 ID
	        STOCK.CTS_RELAY_SADDLE,         --목적동
	        NVL(COMM.ORD_NO, '') ||
	        NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	        COMM.COIL_T,                            --COIL 두께
	        COMM.COIL_W,                            --COIL 폭
	        COMM.COIL_OUTDIA,                       --COIL 외경
	        COMM.NET_WEIGH_WT,                      --NET 계량 중량
	        COMM.CURR_COIL_LEN                      --COIL 길이
	FROM    TB_YM_EQUIP         EQUIP,
	        TB_YM_STACKLAYER    LAYER,
	        TB_YM_STOCK         STOCK,
	        TB_PM_COILCOMM      COMM
	WHERE   EQUIP.EQUIP_GP LIKE '1_S%'
	AND     EQUIP.EQUIP_GP = LAYER.STACK_COL_GP
	AND     LAYER.STOCK_ID = STOCK.STOCK_ID(+)
	AND     LAYER.STOCK_ID = COMM.COIL_NO(+)
        */
        return super.findList(queryCode);
    }

    /**
     * CTS 초기화 정보를 리턴한다.
     * @param ydGp		야드구분
     * @param reEquipNo	AS-IS 설비번호
     * @return
     */
    public JDTORecord readCTSIniInfo(String ydGp, String reEquipNo) {
        String queryCode = "ym.common.dao.selectCTSIniInfo";
        /*
        --ym.common.dao.selectCTSIniInfo
	--CTS 정보를 리턴한다.
	SELECT  DECODE(EQUIP.EQUIP_STAT, 'C', '9', 
	            'O', DECODE(LAYER.STACK_LAYER_STAT, 'L', '1', '0')) AS COIL_YN, --적재 상태
	        LAYER.STOCK_ID AS STACK_STOCK,  --저장품 ID
	        STOCK.STOCK_ID,                 --저장품 ID
	        STOCK.CTS_RELAY_SADDLE,         --목적동
	        NVL(COMM.ORD_NO, '') ||
	        NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	        COMM.COIL_T,                            --COIL 두께
	        COMM.COIL_W,                            --COIL 폭
	        COMM.COIL_OUTDIA,                       --COIL 외경
	        COMM.COIL_WT AS NET_WEIGH_WT, --COIL 중량
	        COMM.CURR.COIL_LEN AS COIL_LEN                           --COIL 길이
	FROM    TB_YM_EQUIP         EQUIP,
	        TB_YM_STACKLAYER    LAYER,
	        TB_YM_STOCK         STOCK,
	        TB_PM_COILCOMM      COMM
	WHERE   EQUIP.EQUIP_GP = ?
	AND     EQUIP.EQUIP_GP = LAYER.STACK_COL_GP
	AND     LAYER.STOCK_ID = STOCK.STOCK_ID(+)
	AND     LAYER.STOCK_ID = COMM.COIL_NO(+)
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ readEquipGp(ydGp, reEquipNo) });
    }

    /**
	 * CTS에 올려진 저장품 정보를 리턴한다.
     * @param ydGp		야드구분
     * @param ctsName	AS-IS CTS 설비명
     * @return
     */
    public JDTORecord readCTSCoilInfo(String ydGp, String ctsName) {
        String queryCode = "ym.common.dao.selectCTSCoilInfo";        
        /*
        --ym.common.dao.selectCTSCoilInfo
	--CTS 코일 정보를 리턴한다.
	SELECT  STOCK_ID AS COIL_NO
	FROM    TB_YM_EQUIP         EQUIP,
	        TB_YM_STACKLAYER    LAYER
	WHERE   EQUIP.EQUIP_GP = ?
	AND     EQUIP.EQUIP_GP = LAYER.STACK_COL_GP(+)
        */
        return super.findByPrimaryKey(queryCode, readEquipGp(ydGp, ctsName));
    }

    /**
     * LINE OFF 작업에 의한 저장품이 존재하는지 리턴한다.
     * @param col	적치열	
     * @param bed	번지
     * @param stat	적재상태
     * @return
     */
    public boolean readCoilOfLineOff(String col, String bed, String stat) {
        String queryCode = "ym.common.dao.selectCoilOfLineOff";
        /*
		--적치단의 저장품을 리턴한다.
		SELECT  STOCK_ID
		FROM    TB_YM_STACKLAYER
		WHERE   STACK_COL_GP = ?
		AND     STACK_BED_GP = ?
		AND     STACK_LAYER_STAT = ?	
        */
        JDTORecord coilInfo = null;        
        List coilInfos		= super.findList(queryCode, new Object[]{ col, bed, stat });
        int coilInfosCnt 	= coilInfos != null ? coilInfos.size() : 0;
        
        String coilNo = null;
        for(int i = 0; i < coilInfosCnt; i++) {
            coilInfo = (JDTORecord)coilInfos.get(i);
            coilNo = StringHelper.evl(coilInfo.getFieldString("STOCK_ID"), "");
            if(! "".equals(coilNo)) {
                return true;
            }
        }
        return false;
    }

    /**
	 * 차량의 멀티동 정보를 리턴한다.
	 * @param cardNo	차량CARD번호
	 * @param yd		야드구분
	 * @param bay		동구분
	 * @return
     * 내부인터페이스 CODE, VALUE 쌍을 리턴한다.
     * @param tc	전문ID
     * @return
     */
    public List readInternalTc(String tc) {
        String qId = "ym.common.dao.selectInternalTc";
        /*
        --ym.common.dao.selectInternalTc
	--내부인터페이스 레이아웃을 리턴한다.
	SELECT  ITEM_NAME,
	             ITEM_LEN
	FROM     TB_YM_IFTCLAYOUT
	WHERE   TC_CD = ?
        */
        return super.findList(qId, tc);
    }
    
    /**
     * 차량의 멀티동 정보를 리턴한다.
     * @param cardNo	차량CARD번호
     * @param yd		야드구분
     * @param bay		동구분
     * @return
     */
    public JDTORecord readMultyConfirm(String cardNo, String yd, String bay) {
        String queryCode = "ym.common.dao.selectMultyConfirm";
        /*
        --ym.common.dao.selectMultyConfirm
	--차량의 멀티동 예약 정보를 리턴한다.
	SELECT  STOCK.STOCK_ID
	FROM    TB_YM_STOCK STOCK,
	            TB_YM_WBOOK WBOOK
	WHERE   STOCK.CAR_CARD_NO = ?
	AND     STOCK.WBOOK_ID         = WBOOK.WBOOK_ID
	AND     WBOOK.YD_GP               = ?
	AND     WBOOK.BAY_GP             <> ?
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ cardNo, yd, bay });        
    }
    
    /**
     * 차량의 멀티동 정보를 리턴한다.
     * @param cardNo	차량CARD번호
     * @return
     */
    public List readMultyBay(String cardNo) {
        /*
		--복수동의 정보를 리턴한다.
		SELECT  
		        STOCK_ID,
		        STOCK_ITEM,
		        SCARFING_SUPPLY_YN,
		        FRTOMOVE_EQUIP_GP,         --이송 설비 구분
		        FRTOMOVE_EQUIP_BED_GP, --이송 설비 BED 구분
		        FRTOMOVE_EQUIP_LAYER_GP, --이송 설비 단 구분
				CAR_CARD_NO
		FROM     TB_YM_STOCK
		WHERE   (CAR_CARD_NO = ? OR SHEAR_SUPPLY_DEMAND_DDTT = ?) --pallet_no 임시 저장 컬럼
		AND     FRTOMOVE_EQUIP_GP IS NOT NULL
		AND     (SUBSTR(FRTOMOVE_EQUIP_GP,3,2) = 'TR'
		OR       SUBSTR(FRTOMOVE_EQUIP_GP,3,2) = 'PT')
		AND     (DEL_YN = 'N' OR DEL_YN IS NULL)
		ORDER BY FRTOMOVE_EQUIP_BED_GP, FRTOMOVE_EQUIP_LAYER_GP DESC
        */
        String queryCode = "ym.common.dao.selectMultyBay";    	
        return super.findList(queryCode, new Object[]{ cardNo, cardNo });
    }
    
    /**
     * 차량의 멀티동 정보를 리턴한다. 최규성 2009-10-19
     * @param cardNo	차량CARD번호
     * @return
     */
    public List readMultyBay(String cardNo,int flag_IN) {
        /*
		--복수동의 정보를 리턴한다.
		SELECT  
		        STOCK_ID,
		        STOCK_ITEM,
		        SCARFING_SUPPLY_YN,
		        FRTOMOVE_EQUIP_GP,         --이송 설비 구분
		        FRTOMOVE_EQUIP_BED_GP, --이송 설비 BED 구분
		        FRTOMOVE_EQUIP_LAYER_GP, --이송 설비 단 구분
				CAR_CARD_NO
		FROM     TB_YM_STOCK
		WHERE   (CAR_CARD_NO = ? OR SHEAR_SUPPLY_DEMAND_DDTT = ?) --pallet_no 임시 저장 컬럼
		AND     FRTOMOVE_EQUIP_GP IS NOT NULL
		AND     (SUBSTR(FRTOMOVE_EQUIP_GP,3,2) = 'TR'
		OR       SUBSTR(FRTOMOVE_EQUIP_GP,3,2) = 'PT')
		AND     (DEL_YN = 'N' OR DEL_YN IS NULL)
		ORDER BY FRTOMOVE_EQUIP_BED_GP, FRTOMOVE_EQUIP_LAYER_GP DESC
        */
        String queryCode = "ym.common.dao.selectMultyBay2";    	
        return super.findList(queryCode, new Object[]{ cardNo, cardNo });
    }
    
    /**
     * 차량 출발지시에 대한 저장품 이동조건을 리턴한다.
     * @param col		적치열
     * @return
     */
    public List readStockOfCarLoad(String col) {
        /*SELECT LAYER.STACK_COL_GP,         --적치 열 구분
	             LAYER.STACK_BED_GP,         --적치 BED 구분
	             LAYER.STACK_LAYER_GP,      --적치 단 구분
	             LAYER.STOCK_ID,                 --저장품ID 
	             STOCK.STOCK_MOVE_TERM   --저장품이동조건
		   FROM  TB_YM_STACKLAYER    LAYER,
		         TB_YM_STOCK         STOCK
		  WHERE  LAYER.STACK_COL_GP      = ?
		--AND    LAYER.STACK_LAYER_STAT  = ?
		    AND  LAYER.STOCK_ID IS NOT NULL
		    AND  LAYER.STOCK_ID = STOCK.STOCK_ID
		*/
        String queryCode = "ym.common.dao.selectStockOfCarLoad";    	
        return super.findList(queryCode, new Object[]{ col });
    }
    
    /**
     * 차량 개소코드,포인트 코드  리턴한다.
     * @param col		적치열
     * @return
     */
    public List readStockOfwloc(String pos) {
        /*
		SELECT WLOC_CD
		       ,YD_PNT_CD
		 FROM USRYMA.TB_YM_STACKCOL
		WHERE STACK_COL_GP=?
		*/
        String queryCode = "ym.common.dao.selectStockOfwloc";    	
        return super.findList(queryCode, new Object[]{ pos });
    }
    
    /**
     * 차량 개소코드,포인트 코드 차량id 리턴한다.
     * @param col		적치열
     * @return
     */
    public List readcarinfoOfwloc(String cardno,String pos) {
    	/*ym.common.dao.readcarinfoOfwloc
    	--수신MSG:  T002 3ATR04
    	SELECT *
    	 FROM (
    	        SELECT YD_CAR_SCH_ID
    	         FROM USRYDA.TB_YD_CARSCH
    	        WHERE CAR_NO LIKE 'GT002%'
    	          AND DEL_YN='N'
    	          AND CARD_NO =:CARD_NO 
    	          AND YD_CARLD_STOP_LOC=:YD_CARLD_STOP_LOC
    	        ORDER BY YD_CAR_SCH_ID
    	       ) A
    	 WHERE ROWNUM<=1
    	 */
        String queryCode = "ym.common.dao.readcarinfoOfwloc";    	
        return super.findList(queryCode, new Object[]{ cardno ,pos });
    }
    
    
    /**
     * 차량 도착 정보를 가져온다.
     * @param whrCardNo	카드번호
     * @param whrYd		야드구분
     * @param whrBay	동구분
     * @return
     */
    public List readStockOfCarLoad(String whrCardNo, String whrYd, String whrBay) {
 
    	String queryCode = "ym.common.dao.selectCarArrival";    	
        return super.findList(queryCode, new Object[]{ whrCardNo, whrYd, whrBay });
    }
    
    /**
     * 차량 도착 정보를 가져온다.
     * @param whrCardNo	카드번호
     * @param whrYd		야드구분
     * @param whrBay	동구분
     * @return
     */
    public List readymStockOfCarLoad(String TRANS_ORD_DATE2, String TRANS_ORD_SEQNO2) {
 
    	String queryCode = "ym.common.dao.readymStockOfCarLoad";    	
        return super.findList(queryCode, new Object[]{ TRANS_ORD_DATE2,TRANS_ORD_SEQNO2 });
    }
    
    /**
     * 구내운송 차량 도착 정보를 가져온다.
     * @param whrCardNo	카드번호
     * @param whrYd		야드구분
     * @param whrBay	동구분
     *
     @return
     */
    public List readStockOfCarLoad2(String whrCardNo, String whrYd, String whrBay) {
         
    	String queryCode = "ym.common.dao.selectCarArrival2";    	
        return super.findList(queryCode, new Object[]{ whrCardNo, whrYd, whrBay });
    }
    /**
     * 차량 도착 정보를 가져온다. - 최규성
     * @param whrCardNo	카드번호
     * @param whrYd		야드구분
     * @param whrBay	동구분
     * @return
     */
    public List readStockOfCarLoad3(String whrCardNo, String whrYd, String whrBay) {
        
    	String queryCode = "ym.common.dao.selectCarArrival3";    	
        return super.findList(queryCode, new Object[]{ whrCardNo, whrYd, whrBay });
    }
    /**
     * 차량 도착시 작업 예약 정보를 가져온다.(MCH)
     * @param whrCardNo	카드번호
     * @param whrYd		야드구분
     * @param whrBay	동구분
     * @return
     */
    public List readStockOfPalletLoad(String work_kind, String whrYd, String whrBay, String pos) {
        
    	String queryCode = "ym.common.dao.selectPalletArrival";    	
        return super.findList(queryCode, new Object[]{work_kind, whrYd, whrBay , pos});
    }  
    
    /**
     * 차량의 멀티동 정보를 리턴한다.
     * @param cardNo	차량CARD번호
     * @param yd		야드구분
     * @param bay		동구분
     * @return
     */
    public JDTORecord readbedmax(String pos) {
		/*
		SELECT STACK_MAX_QNTY 
		  FROM TB_YM_EQUIP
		 WHERE EQUIP_GP = ?
		*/    	
        String queryCode = "ym.common.dao.selectreadbedmax";
        return super.findByPrimaryKey(queryCode, new Object[]{ pos });        
    }

    
    /**
     * 적치대의 적치열, 번지를 리턴한다.
     * @param col	적치열
     * @return
     */
    public List readMaxOfCarMap(String col) {
        String queryCode = "ym.common.dao.selectBedMaxOfCarMap";
        /*
        SELECT  STACK_COL_GP,
		        STACK_BED_GP
		FROM    TB_YM_STACKER
		WHERE   STACK_COL_GP = ?
		*/
        return super.findList(queryCode, new Object[]{ col });
    }
    
    /**
     * 대차 정보를 리턴한다.
     * @param yd	야드구분
     * @param col	적치열
     * @param bed	번지
     * @return
     */
    public JDTORecord readVicCarInfo(String yd, String col, String bed) { 
        String queryCode = null;
        if(YmCommonConst.YD_GP_2.equals(yd)) {
            queryCode = "ym.common.dao.selectSlabVicCarInfo";
            /*
            --슬라브 대차정보를 리턴한다.
			WITH TEMP AS (
			    SELECT  STACK_COL_GP,
			            STACK_BED_GP,
			            STACK_BED_QNTY_MAX
			    FROM    TB_YM_STACKER
			    WHERE   STACK_COL_GP = ?
			    AND     STACK_BED_GP = ?
			)
			SELECT  (
			        SELECT  COUNT(STOCK_ID)
			        FROM    TEMP,
			                TB_YM_STACKLAYER LAYER
			        WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
			        AND     TEMP.STACK_BED_GP = LAYER.STACK_BED_GP
			        AND     LAYER.STACK_LAYER_STAT IN ('L', 'S', 'U')
			        ) AS CUR_QNTY,
			        --STACKER.STACK_BED_QNTY_CURR AS CUR_QNTY,   --적치 BED 수량 현재
			        TEMP.STACK_BED_QNTY_MAX,   --적치 BED 수량 MAX
			        TEMP.STACK_COL_GP,
			
			        EQUIP.STACK_MAX_QNTY  AS MAX_QNTY,   --적치 BED 수량 MAX
			        EQUIP.EQUIP_GP,                     --적재 상태
			        EQUIP.STACK_STAT,                   --적재 상태
			        EQUIP.CARLOAD_ASSIGN_YN,            --상차 지정 구분
			        EQUIP.CARUNLOAD_ASSIGN_YN,          --하차 지정 구분
			        EQUIP.CARLOAD_SCH_WORK_KIND,        --상차 SCHEDULE 작업 종류
			        EQUIP.CARUNLOAD_SCH_WORK_KIND,      --하차 SCHEDULE 작업 종류
			        NVL(EQUIP.EQUIP_KIND,'') ||
			        NVL(EQUIP.EQUIP_NO, '') AS TC_NO,   --대차번호
			        EQUIP.CURR_STOP_LOC,                --현재 정지 위치
			        EQUIP.CARLOAD_STOP_LOC,             --상차 정지 위치
			        EQUIP.CARUNLOAD_STOP_LOC            --하차 정지 위치
			        ,EQUIP.REGISTER AS AUTO_YN                  --BACKUP으로 사용됨
			FROM    TEMP,
			        TB_YM_EQUIP     EQUIP
			WHERE   EQUIP.EQUIP_GP = SUBSTR(TEMP.STACK_COL_GP, 1, 1) || 'XTC0' || SUBSTR(TEMP.STACK_COL_GP, 5, 1)
            */
        }else {
            queryCode = "ym.common.dao.selectVicCarInfo";
            /*
            --코일 대차정보를 리턴한다.
            WITH TEMP AS (
			    SELECT  STACK_COL_GP,
			            STACK_BED_GP,
			            STACK_BED_QNTY_MAX
			    FROM    TB_YM_STACKER
			    WHERE   STACK_COL_GP = ?
			    AND     STACK_BED_GP = ?
			)
			SELECT  TEMP.STACK_COL_GP,                  --적치 열 구분
			        TEMP.STACK_BED_QNTY_MAX,
			        (
			        SELECT  COUNT(STOCK_ID)
			        FROM    TEMP,
			                TB_YM_STACKLAYER LAYER
			        WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
			        AND     LAYER.STACK_LAYER_STAT IN ('L','S','U')
			        ) AS CUR_QNTY,                      --적치 BED 수량 현재        
			        EQUIP.EQUIP_GP,                     --적재 상태
			        EQUIP.STACK_STAT,                   --적재 상태
			        EQUIP.CARLOAD_ASSIGN_YN,            --상차 지정 구분
			        EQUIP.CARUNLOAD_ASSIGN_YN,          --하차 지정 구분
			        EQUIP.CARLOAD_SCH_WORK_KIND,        --상차 SCHEDULE 작업 종류
			        EQUIP.CARUNLOAD_SCH_WORK_KIND,      --하차 SCHEDULE 작업 종류
			        NVL(EQUIP.EQUIP_KIND, '') || 
			        NVL(EQUIP.EQUIP_NO, '') AS TC_NO,       --대차번호
			        EQUIP.STACK_MAX_QNTY    AS MAX_QNTY,    --설비 적재 MAX
			        EQUIP.CURR_STOP_LOC,                --현재 정지 위치
			        EQUIP.CARLOAD_STOP_LOC,             --상차 정지 위치
			        EQUIP.CARUNLOAD_STOP_LOC            --하차 정지 위치
			        ,EQUIP.REGISTER AS AUTO_YN                  --BACKUP으로 사용됨
			FROM    TEMP,
			        TB_YM_EQUIP EQUIP
			WHERE   EQUIP.EQUIP_GP = SUBSTR(TEMP.STACK_COL_GP, 1, 1) || 'X' || SUBSTR(TEMP.STACK_COL_GP, 3, 4)
			*/
        }
        return super.findByPrimaryKey(queryCode, new Object[]{ col, bed });
    }

    /**
     * SPM CONVEYOUR 초기정보를 리턴한다.
     * @param col1	YmCommonConst.SPM_COL_1DKE
     * @param col2	YmCommonConst.SPM_COL_1EKE
     * @param col3	YmCommonConst.SPM_COL_1EKD
     * @param col4	YmCommonConst.SPM_COL_1FKD
     * @return
     */
    public List readSPMConIniInfo(String col1, String col2, String col3, String col4) {
        String queryCode = "ym.common.dao.selectSPMConIniInfo";
        /*
        --ym.common.dao.selectSPMConIniInfo
	--SPM CONVEYOUR 초기정보를 리턴한다.
	WITH TEMP AS (
	    SELECT  ? AS STACK_COL1,
	            ? AS STACK_COL2,
	            ? AS STACK_COL3,
	            ? AS STACK_COL4
	    FROM    DUAL
	)
	SELECT  SPM_INFO.COIL_YN,       --코일적치유무
	        SPM_INFO.COIL_NO,       --코일번호
	        SPM_CNT.COIL_STACK_CNT  --HFL코일적치수
	FROM    (
	            SELECT  COUNT(DECODE(STACK_LAYER_STAT, 'L', 1, 0)) COIL_STACK_CNT --코일 적치 수                
	            FROM    TEMP,
	                    TB_YM_STACKLAYER
	            WHERE   STACK_COL_GP LIKE STACK_COL1 || '%'
	            OR      STACK_COL_GP LIKE STACK_COL2 || '%'
	            OR      STACK_COL_GP LIKE STACK_COL3 || '%'
	            OR      STACK_COL_GP LIKE STACK_COL4 || '%'
	        ) SPM_CNT,
	        (
	            SELECT  LAYER.STACK_LAYER_STAT,
	                    DECODE(LAYER.STACK_LAYER_STAT, 'L', '1', '0') COIL_YN,    --코일적치유무
	                    LAYER.STOCK_ID AS COIL_NO,              --코일번호
	                    
	                    NVL(COMM.ORD_NO, '') ||                 
	                    NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	                    COMM.COIL_T,                            --COIL 두께
	                    COMM.COIL_W,                            --COIL 폭
	                    COMM.COIL_OUTDIA,                       --COIL 외경
	                    COMM.COIL_WT AS NET_WEIGH_WT, --COIL 중량
	                    COMM.CURR_COIL_LEN AS COIL_LEN                           --COIL 길이
	            FROM    TEMP,
	                    TB_YM_STACKLAYER    LAYER,
	                    TB_PM_COILCOMM      COMM
	            WHERE   (LAYER.STACK_COL_GP LIKE TEMP.STACK_COL1 || '%'
	            OR      LAYER.STACK_COL_GP LIKE TEMP.STACK_COL2 || '%'
	            OR      LAYER.STACK_COL_GP LIKE TEMP.STACK_COL3 || '%'
	            OR      LAYER.STACK_COL_GP LIKE TEMP.STACK_COL4 || '%')
	            AND     LAYER.STOCK_ID = COMM.COIL_NO(+)
	        ) SPM_INFO
        */
        return super.findList(queryCode, new Object[]{ col1, col2, col3, col4 });
    }

    /**
     * HFL CONVEYOUR 초기정보를 리턴한다.
     * @param col1	YmCommonConst.Roll_COL_1BDC or HFL_COL_1BFE
     * @param col2	YmCommonConst.Roll_COL_1CDC or HFL_COL_1CFD
     */
    public List readHFLConIniInfo(String col1, String col2) {
        String queryCode = "ym.common.dao.selectHFLConIniInfo";
        /*
        --ym.common.dao.selectHFLConIniInfo
	--HFL CONVEYOUR 초기정보를 리턴한다
	WITH TEMP AS (
	    SELECT  ? AS STACK_COL1,
	            ? AS STACK_COL2
	    FROM    DUAL
	)
	SELECT  INFO.COIL_YN,       --코일적치유무
	        INFO.COIL_NO,       --코일번호
	        CNT.COIL_STACK_CNT  --HFL코일적치수
	FROM    (
	            SELECT  COUNT(DECODE(LAYER.STACK_LAYER_STAT, 'L', 1, 0)) COIL_STACK_CNT --코일 적치 수                
	            FROM    TEMP,
	                    TB_YM_STACKLAYER    LAYER
	            WHERE   (LAYER.STACK_COL_GP LIKE TEMP.STACK_COL1 || '%'
	            OR      LAYER.STACK_COL_GP LIKE TEMP.STACK_COL2 || '%')
	        ) CNT,
	        (
	            SELECT  DECODE(LAYER.STACK_LAYER_STAT, 'L', '1', '0') COIL_YN,    --코일적치유무
	                    LAYER.STOCK_ID AS COIL_NO,    --코일번호
	                    
	                    NVL(COMM.ORD_NO, '') ||                 
	                    NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	                    COMM.COIL_T,                            --COIL 두께
	                    COMM.COIL_W,                            --COIL 폭
	                    COMM.COIL_OUTDIA,                       --COIL 외경
	                    COMM.COIL_WT AS NET_WEIGH_WT, --COIL 중량
	                    COMM.CURR_COIL_LEN AS COIL_LEN  --COIL 길이
	            FROM    TEMP,
	                    TB_YM_STACKLAYER    LAYER,
	                    TB_PM_COILCOMM      COMM
	            WHERE   (LAYER.STACK_COL_GP LIKE TEMP.STACK_COL1 || '%'
	            OR      LAYER.STACK_COL_GP LIKE TEMP.STACK_COL2 || '%')
	            AND     LAYER.STOCK_ID = COMM.COIL_NO(+)
	        ) INFO
        */
        return super.findList(queryCode, new Object[]{ col1, col2 });
    }

	/**
	 * 야드맵정보를 리턴한다.
     * @param ydMapKey	적치열정보
     * @return
     */
    public List readYdMapInfo(String ydMapKey) {
        String queryCode = "ym.common.dao.selectYdMapInfo";        
        /*
        --ym.common.dao.selectYdMapInfo
	--야드맵정보를 리턴한다.
	SELECT  LAYER.STOCK_ID,  --저장품 ID
	        DECODE(LAYER.STACK_LAYER_ACTIVE_STAT, 'C', '0', 'O', '1') AS USE_YN, --사용유무
	        LAYER.STACK_LAYER_X_AXIS X_LOC, --X물리위치
	        SUBSTR(LAYER.STACK_COL_GP, 2, 1) AS BAY,
	        SUBSTR(LAYER.STACK_COL_GP, 3, 2) AS SPAN,
	        SUBSTR(LAYER.STACK_COL_GP, 6, 1) AS COL,
	        'H'                                 || 
	        SUBSTR(LAYER.STACK_COL_GP, 2, 3)    || 
	        SUBSTR(LAYER.STACK_COL_GP, 6, 1)    || 
	        SUBSTR(LAYER.STACK_LAYER_GP, 2, 1)  || LAYER.STACK_BED_GP AS BED_ADDRESS,
	        
	        NVL(COMM.ORD_NO, '') ||
	        NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	        COMM.COIL_T,                            --COIL 두께
	        COMM.COIL_W,                            --COIL 폭
	        COMM.COIL_OUTDIA,                       --COIL 외경
	        COMM.COIL_WT AS NET_WEIGH_WT,--NET 계량 중량
	        COMM.CURR_COIL_LEN AS COIL_LEN                          --COIL 길이        
	FROM    TB_YM_STACKLAYER    LAYER,
	        TB_PM_COILCOMM      COMM
	WHERE   LAYER.STACK_COL_GP LIKE ? || '%'
	AND     LAYER.STOCK_ID = COMM.COIL_NO(+)
	ORDER BY BAY, SPAN, COL
        */
        return super.findList(queryCode, new Object[]{ ydMapKey });
    }

    /**
     * 다음 작업예약ID를 리턴한다.
     * @return
     */
    public String readNextWBookId() {
		/*
		SELECT 
		    TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||LPAD(YM_WBOOK_SEQ.NEXTVAL,6,'0') AS WBOOK_ID
		FROM DUAL
		*/    	
        String queryCd = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getWBookID";        
        return ((JDTORecord)(super.find(queryCd))).getFieldString("WBOOK_ID");
    }
    
    /**
     * SADDLE의 중계 또는 최종 정보를 리턴한다.
     * @param equipGp	설비구분
     */
    public JDTORecord readRelayInfo(String equipGp) {
        String queryCode = "ym.common.dao.selectRelayInfo";        
        /*
        --ym.common.dao.selectRelayInfo
	--SADDLE의 중계 또는 최종 정보를 리턴한다.
	SELECT  NVL(COMM.ORD_NO, '') ||
	        NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	        COMM.COIL_T,                  --COIL 두께
	        COMM.COIL_W,                 --COIL 폭
	        COMM.COIL_OUTDIA,        --COIL 외경
	        COMM.COIL_WT AS NET_WEIGH_WT, --COIL 중량
	        COMM.CURR_COIL_LEN AS COIL_LEN,              --COIL 길이
	        COMM.CURR_PROG_CD,    --현재진도코드
	
	        STOCK.STOCK_ID,
	        STOCK.CTS_RELAY_YN,
	        STOCK.CTS_RELAY_SADDLE,        
	        STOCK.CARUNLOAD_PUT_LOC,
	        STOCK.KEEPSTOCK_STL_YN AS KEEPSTOCK_STL_GP,
	
	        EQUIP.WBOOK_ID AS CTS_COIL_NO,
	        WBOOK.SCH_WORK_KIND
	FROM    TB_YM_STOCK     STOCK,
	        TB_YM_EQUIP     EQUIP,
	        TB_YM_WBOOK     WBOOK,
	        TB_PM_COILCOMM  COMM
	WHERE   STOCK.CTS_RELAY_SADDLE  = ?
	AND     STOCK.STOCK_ID   = EQUIP.WBOOK_ID
	AND     STOCK.WBOOK_ID  = WBOOK.WBOOK_ID(+)
	AND     STOCK.STOCK_ID   = COMM.COIL_NO(+)
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ equipGp });
    }
    
    /**
     * SADDLE의 중계 또는 최종 정보를 리턴한다.
     * @param equipGp	설비구분
     */
    public JDTORecord readRelayInfo2(String stockId) {
        String queryCode = "ym.common.dao.selectRelayInfo2";        
        /*
        --ym.common.dao.selectRelayInfo
	--SADDLE의 중계 또는 최종 정보를 리턴한다.
	SELECT  NVL(COMM.ORD_NO, '') ||
	        NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	        COMM.COIL_T,                  --COIL 두께
	        COMM.COIL_W,                 --COIL 폭
	        COMM.COIL_OUTDIA,        --COIL 외경
	        COMM.COIL_WT AS NET_WEIGH_WT, --COIL 중량
	        COMM.CURR_COIL_LEN AS COIL_LEN,              --COIL 길이
	        COMM.CURR_PROG_CD,    --현재진도코드
	
	        STOCK.STOCK_ID,
	        STOCK.CTS_RELAY_YN,
	        STOCK.CTS_RELAY_SADDLE,        
	        STOCK.CARUNLOAD_PUT_LOC,
	        STOCK.KEEPSTOCK_STL_YN AS KEEPSTOCK_STL_GP,
	
	        EQUIP.WBOOK_ID AS CTS_COIL_NO,
	        WBOOK.SCH_WORK_KIND
	FROM    TB_YM_STOCK     STOCK,
	        TB_YM_EQUIP     EQUIP,
	        TB_YM_WBOOK     WBOOK,
	        TB_PM_COILCOMM  COMM
	WHERE   STOCK.CTS_RELAY_SADDLE  = ?
	AND     STOCK.STOCK_ID   = EQUIP.WBOOK_ID
	AND     STOCK.WBOOK_ID  = WBOOK.WBOOK_ID(+)
	AND     STOCK.STOCK_ID   = COMM.COIL_NO(+)
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ stockId });
    }

    /**
     * SADDLE 정보를 리턴한다.
     * @param ydGp			야드구분
     * @param saddleName	SADDLE 번호
     * @return
     */
    public JDTORecord readSkidStatInfo(String ydGp, String saddleName) {
        String queryCode = "ym.common.dao.selectSkidIniInfo";
        /*
        --ym.common.dao.selectSkidIniInfo
	--BASE SADDLE INFO
	SELECT  NVL(COMM.ORD_NO, '') ||                 
	        NVL(COMM.ORD_DTL, '') AS PRODUCT_NO,    --주문 번호+주문 행번 AS 제작번호/행번
	        COMM.COIL_T,                            --COIL 두께
	        COMM.COIL_W,                            --COIL 폭
	        COMM.COIL_OUTDIA,                       --COIL 외경
	        COMM.COIL_WT AS NET_WEIGH_WT,           --COIL 중량
	        COMM.CURR_COIL_LEN AS COIL_LEN,                          --COIL 길이
	        COMM.KEEPSTOCK_STL_GP,                  --보존 재료 구분
	        STOCK.STOCK_ITEM,
	        EQUIP.EQUIP_GP,
	        LAYER.STOCK_ID,
	        LAYER.STACK_COL_GP,
	        LAYER.STACK_BED_GP,
	        LAYER.STACK_LAYER_GP,
	        LAYER.STACK_LAYER_STAT
	FROM    TB_YM_EQUIP         EQUIP,
	        TB_YM_STACKLAYER    LAYER,
	        TB_YM_STOCK         STOCK,
	        TB_PM_COILCOMM      COMM
	WHERE   EQUIP.EQUIP_GP = ?
	AND     EQUIP.EQUIP_GP = LAYER.STACK_COL_GP
	AND     LAYER.STOCK_ID = STOCK.STOCK_ID(+)
	AND     LAYER.STOCK_ID = COMM.COIL_NO(+)
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ readEquipGp(ydGp, saddleName) });
    }
    
    /**
     * SADDLE 정보를 리턴한다.
     * @param ydGp			야드구분
     * @param saddleName	SADDLE 번호
     * @return
     */
    public JDTORecord readSkidStatInfoRE(String ydGp, String saddleName) {
        String queryCode = "ym.common.dao.selectSkidIniInfoRE"; 
        return super.findByPrimaryKey(queryCode, new Object[]{ readEquipGpRE(ydGp, saddleName) ,saddleName });
    }
    
    /**
     * StackLayer SADDLE 정보를 리턴한다.
     * @param ydGp			야드구분
     * @param saddleName	SADDLE 번호
     * @return
     */
    public JDTORecord readSkidStatLayerInfo(String ydGp, String CoilYN, String saddleName, String Gn) {
        String queryCode = "ym.common.dao.readSkidStatLayerInfo";
        /*
        SELECT * FROM TB_YM_STACKLAYER
	WHERE STACK_COL_GP = ?
	AND STACK_LAYER_STAT = ?	
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ readEquipGp(ydGp, saddleName),Gn });
    }
    
    /**
     * 백업할 스케쥴기준정보를 리턴한다.
     * @param ydGp		야드구분
     * @param bayGp		동구분
     * @param equipNo	설비번호
     * @param ativeStat	SCHEDULE 기준 활성 상태
     * @return 
     */
    public List readTroRecSch(String ydGp, String bayGp, String equipNo, String ativeStat) {
        String queryCode = "ym.common.dao.selectTroOfSch";
        /*SELECT  RULE.SCH_RULE_ID,
			        RULE.YD_GP,
			        RULE.BAY_GP,
			        RULE.SCH_WORK_KIND,
			        RULE.SCH_RULE_ACTIVE_STAT,
			        RULE.SCH_RULE_CRANE_NO,
			        RULE.SCH_RULE_WPREFER,
			        RULE.SCH_RULE_ALTER_CRANE_NO,
			        RULE.SCH_RULE_ALTER_WPREFER,
			        SCH.SCH_ID,
			        SCH.STOCK_ID,
			        SCH.SCH_WPREFER,
			        SCH.SCH_WORK_STAT,
			        SCH.CRANE_WORD_UP_LOC,
			        SCH.CRANE_WORD_PUT_LOC,
			        SCH.WBOOK_ID
			FROM    TB_YM_SCHRULE   RULE,
			        TB_YM_SCH       SCH
			WHERE   RULE.YD_GP  = ?
			AND     RULE.BAY_GP  = ?  
			AND     RULE.SCH_RULE_CRANE_NO      = ?
			AND     RULE.SCH_RULE_ACTIVE_STAT  = ?
			AND     RULE.SCH_RULE_ID                   = SCH.SCH_RULE_ID
			ORDER BY SCH.SCH_WPREFER, SCH.SCH_ID
			*/
        return super.findList(queryCode, new Object[]{ ydGp, bayGp, equipNo, ativeStat });
    }

    /**
     * 백업할 스케쥴기준정보를 리턴한다.
     * @param ydGp		야드구분
     * @param bayGp		동구분
     * @param equipNo	설비번호
     * @param ativeStat	SCHEDULE 기준 활성 상태
     * @return 
     */
    public List readTroRecSchRule(String ydGp, String bayGp, String equipNo, String ativeStat) {
        String queryCode = "ym.common.dao.selectTroOfSchRule";
        /*SELECT
		  SCH_RULE_ID,
		  YD_GP,
		  BAY_GP,
		  SCH_WORK_KIND,
		  SCH_RULE_ACTIVE_STAT,
		  SCH_RULE_CRANE_NO,
		  SCH_RULE_WPREFER,
		  SCH_RULE_ALTER_CRANE_NO,
		  SCH_RULE_ALTER_WPREFER,
		  REGISTER,
		  REG_DDTT,
		  MODIFIER,
		  MOD_DDTT,
		  DEL_YN
		FROM    TB_YM_SCHRULE
		WHERE  YD_GP   = ?
		AND      BAY_GP = ?  
		AND      SCH_RULE_CRANE_NO     = ?
		AND      SCH_RULE_ACTIVE_STAT = ?
		*/
        return super.findList(queryCode, new Object[]{ ydGp, bayGp, equipNo, ativeStat });
    }

    /**
     * 복구할 스케쥴기준정보를 리턴한다.
     * @param ydGp		야드구분
     * @param bayGp		동구분
     * @param alEquipNo	대체설비번호
     * @param ativeStat	SCHEDULE 기준 활성 상태
     * @return
     */
    public List readRecSchRule(String ydGp, String bayGp, String alEquipNo, String ativeStat) {
        String queryCode = "ym.common.dao.selectRecSchRule";
        /*
        --ym.common.dao.selectRecSchRule
	--복구할 크레인의 스케쥴 기준 정로를 가져온다.
	SELECT
	  SCH_RULE_ID,
	  YD_GP,
	  BAY_GP,
	  SCH_WORK_KIND,
	  SCH_RULE_ACTIVE_STAT,
	  SCH_RULE_CRANE_NO,
	  SCH_RULE_WPREFER,
	  SCH_RULE_ALTER_CRANE_NO,
	  SCH_RULE_ALTER_WPREFER,
	  REGISTER,
	  REG_DDTT,
	  MODIFIER,
	  MOD_DDTT,
	  DEL_YN
	FROM    TB_YM_SCHRULE
	WHERE  YD_GP   = ?
	AND      BAY_GP = ?  
	AND      SCH_RULE_ALTER_CRANE_NO     = ?
	AND      SCH_RULE_ACTIVE_STAT = ?
	ORDER BY SCH_RULE_ALTER_WPREFER
        */
        return super.findList(queryCode, new Object[]{ ydGp, bayGp, alEquipNo, ativeStat });
    }

    /**
     * TC LAYOUT 항목의 길이 정보를 Map으로 리턴한다.
     * @param tc
     * @return
     */
    public Map readColumnLenOfTc(String tc) {
	/*
	SELECT  ITEM_LEN,ITEM_NAME
	FROM    TB_CM_LEVEL2TCLAYOUT
	WHERE   TC_CD = ?
	*/    	
        String qCode 	= "ym.common.dao.lengthOfTcColumn";
        return readColumnLenOfTc(tc, qCode);
    }

    /**
     * TC LAYOUT 항목의 길이 정보를 Map으로 리턴한다.
     * @param qId	쿼리ID
     * @param tc	전문ID
     * @return
     */
    public Map readColumnLenOfTc(String tc, String qId) {
        List list	= super.findList(qId, new Object[]{ tc });
        int listCnt	= list != null ? list.size() : 0;        
        Map data	= new HashMap();
        for(int i = 0; i < listCnt; i++) {
            data.put(((JDTORecord)list.get(i)).getFieldString("ITEM_NAME"), 
                     ((JDTORecord)list.get(i)).getFieldString("ITEM_LEN"));
        }
        return data;
    }

    /**
     * 주작업에 대한 TO 적치열을 리턴한다.
     * @param yd		야드구분
     * @param bay		동구분
     * @param usageCd	적치열용도코드
     * @param schKind	스케쥴종류
     * @param moveTerm	저장품이동조건
     * @return
     */
    public List readMainStockCol(String yd, String bay, String usageCd, String schKind, String moveTerm) {
		String  queryCode = "ym.common.dao.selectMainStockCol";
		/*
		--주작업 적치열 검색
		SELECT  STOCKMOVEROUTE.STACK_COL_USAGE_CD,      --적치 열 용도 CODE   
		        STOCKMOVEROUTE.SCH_WORK_KIND,           --SCHEDULE 작업 종류
		        STOCKMOVEROUTE.STOCK_MOVE_TERM,         --저장품 이동 조건
		        STOCKMOVEROUTE.STOCK_MOVE_ROUTE_PRIOR,  --저장품 이동 경로 순위
		        STOCKMOVEROUTE.STOCK_MOVE_ROUTE_STAT,   --저장품 이동 경로 상태
		        STOCKMOVEROUTE.STACK_USAGE_CD_TO,       --적치 용도 CODE TO
		        LOCSEARCH.STOCK_MOVE_ROUTE_ID,          --저장품 이동 경로 ID
		        LOCSEARCH.LOC_SEARCH_ID,                --위치 검색 ID
		        LOCSEARCH.STACK_COL_GP,                 --적치 열 구분
		        LOCSEARCH.STACK_COL_SEQ,                --적치 열 순서
		        STACKCOL.STACK_COL_BED_QNTY
		FROM    TB_YM_STOCKMOVEROUTE    STOCKMOVEROUTE, 
		        TB_YM_LOCSEARCH         LOCSEARCH,
		        TB_YM_STACKCOL          STACKCOL
		WHERE   STOCKMOVEROUTE.YD_GP                = ?
		AND     STOCKMOVEROUTE.BAY_GP               = ?
		AND     STOCKMOVEROUTE.STOCK_ITEM           = 'SM'
		AND     STOCKMOVEROUTE.STACK_COL_USAGE_CD   = ?
		AND     STOCKMOVEROUTE.SCH_WORK_KIND        = ?
		AND     STOCKMOVEROUTE.STOCK_MOVE_TERM      = ?
		AND     STOCKMOVEROUTE.STOCK_MOVE_ROUTE_STAT= 'A'
		AND     STOCKMOVEROUTE.STOCK_MOVE_ROUTE_ID  = LOCSEARCH.STOCK_MOVE_ROUTE_ID
		AND     LOCSEARCH.STACK_COL_GP              = STACKCOL.STACK_COL_GP
		AND     STACKCOL.STACK_COL_ACTIVE_STAT      = 'O'
		ORDER BY STOCK_MOVE_ROUTE_PRIOR,LOC_SEARCH_ID
		*/
		return super.findList(queryCode, new Object[]{ yd, bay, usageCd, schKind, moveTerm });
    }

    /**
     * 작업예약테이블의 오퍼레이터 지정을 List 타입으로 리턴한다.
     * @param toLoc	작업예약테이블의 오퍼레이터 지정 값
     * @return
     */
    public List readDecisionToLoc(String toLoc) {
		String  queryCode = "ym.common.dao.selectDecisionToLoc";
		/*
		--ym.common.dao.selectDecisionToLoc
		--적치열을 리턴한다.
		SELECT  ? AS STACK_COL_GP
		FROM     DUAL
		*/
		return super.findList(queryCode, new Object[]{ toLoc });
    }

	/**
	 * 작업예약테이블의 오퍼레이터 지정을 List 타입으로 리턴한다.
     * @param toLoc	작업예약테이블의 오퍼레이터 지정 값
     * @return
     */
    public List readDecisionTenToLoc(String toLoc) {
		String  queryCode = "ym.common.dao.selectDecisionTenToLoc";
		/*
		--ym.common.dao.selectDecisionTenToLoc
		--적치열을 리턴한다.
		WITH TEMP AS (
		    SELECT  ? AS TO_LOC
		    FROM    DUAL
		)
		SELECT  TEMP.TO_LOC,
		        LAYER.STACK_COL_GP,
		        LAYER.STACK_BED_GP,
		        LAYER.STACK_LAYER_GP,
		        STACKER.STACK_BED_ABLE_QNTY
		FROM    TEMP,
		        TB_YM_STACKER       STACKER,
		        TB_YM_STACKLAYER    LAYER
		WHERE   SUBSTR(TEMP.TO_LOC, 1, 6)   = STACKER.STACK_COL_GP
		AND     SUBSTR(TEMP.TO_LOC, 7, 2)   = STACKER.STACK_BED_GP
		AND     STACKER.STACK_COL_GP        = LAYER.STACK_COL_GP
		AND     STACKER.STACK_BED_GP        = LAYER.STACK_BED_GP
		AND     SUBSTR(TEMP.TO_LOC, 9, 2)   = LAYER.STACK_LAYER_GP
		*/
		return super.findList(queryCode, new Object[]{ toLoc });
    }

    /**
     * 보조작업에 대한 TO 적치열을 리턴한다.
     * @param colGp	적치열구분
     * @return
     */
    public List readSubStockCol(String colGp) {
		String  queryCode = "ym.common.dao.selectSubStockCol";
		/*
		--ym.common.dao.selectSubStockCol
		--보조작업 적치열 검색
		WITH TEMP AS (
		    SELECT  STACK_COL_GP CUR_STACK_COL_GP,
		            TO_NUMBER(SUBSTR(STACK_COL_GP, 5, 2))   CUR_ROW
		    FROM    TB_YM_STACKCOL
		    WHERE   STACK_COL_GP = ?
		)
		SELECT  TEMP.CUR_STACK_COL_GP,
		        TEMP.CUR_ROW - 1 AS CUR_ROW,
		        STACKCOL.STACK_COL_GP
		FROM    TEMP,
		        TB_YM_STACKCOL  STACKCOL
		WHERE   STACKCOL.STACK_COL_GP LIKE SUBSTR(TEMP.CUR_STACK_COL_GP, 1, 4) || '%'
		AND     STACKCOL.STACK_COL_ACTIVE_STAT = 'O'
		*/
		return super.findList(queryCode, new Object[]{ colGp });
    }
    
    /**
     * 위치검색 테이블의 적치열에 의한 대차의 하차정지 위치를 리턴한다.
     * @param searchLoc 저장품이동경로 테이블의 검색조건
     *                  [야드 + 동 + 저장품품목 + 적치열용도코드 + 스케쥴작업종류 + 저장품 이동조건]
     * @return
     */
    public JDTORecord readCarUnloadLoc(String searchLoc) {
		String  queryCode = "ym.common.dao.selectUnloadLoc";	
		/*
		--ym.common.dao.selectUnloadLoc
		--대차 하차정지위치 확인
		SELECT  DECODE(SUBSTR(EQUIP.CARUNLOAD_STOP_LOC, 2,1), 
		            'B', 'B',
		            'C', 'C', '') AS BAY
		FROM    (
		            SELECT  SUBSTR(STACK.COL, 1, 1) AS YD_GP,
		                    SUBSTR(STACK.COL, 2, 1) AS BAY_GP,
		                    SUBSTR(STACK.COL, 3, 2) AS STOCK_ITEM,
		                    SUBSTR(STACK.COL, 5, 2) AS STACK_COL_USAGE_CD,
		                    SUBSTR(STACK.COL, 7, 4) AS SCH_WORK_KIND,
		                    SUBSTR(STACK.COL, 11, 2) AS STOCK_MOVE_TERM
		            FROM    (
		                    SELECT  ? AS COL
		                    FROM    DUAL
		                    ) STACK
		        ) SEARCH,
		        TB_YM_STOCKMOVEROUTE    MOVEROUTE,
		        TB_YM_LOCSEARCH         LOCSEARCH,
		        TB_YM_EQUIP             EQUIP
		WHERE   SEARCH.YD_GP                = MOVEROUTE.YD_GP
		AND     SEARCH.BAY_GP               = MOVEROUTE.BAY_GP
		AND     SEARCH.STOCK_ITEM           = MOVEROUTE.STOCK_ITEM
		AND     SEARCH.STACK_COL_USAGE_CD   = MOVEROUTE.STACK_COL_USAGE_CD
		AND     SEARCH.SCH_WORK_KIND        = MOVEROUTE.SCH_WORK_KIND
		AND     SEARCH.STOCK_MOVE_TERM      = MOVEROUTE.STOCK_MOVE_TERM
		AND     MOVEROUTE.STOCK_MOVE_ROUTE_ID   = LOCSEARCH.STOCK_MOVE_ROUTE_ID
		AND     SUBSTR(LOCSEARCH.STACK_COL_GP, 1, 1) || 'X' || SUBSTR(LOCSEARCH.STACK_COL_GP, 3, 4) = EQUIP.EQUIP_GP
		AND     ROWNUM = 1
		ORDER BY MOVEROUTE.STOCK_MOVE_ROUTE_PRIOR, LOCSEARCH.STOCK_MOVE_ROUTE_ID, LOCSEARCH.LOC_SEARCH_ID
		*/
		return super.findByPrimaryKey(queryCode, new Object[]{ searchLoc });
    }

    /**
     * @param 	slabNo	예정 실 SLAB 번호
     * @return
     */
    public List YJK_DEL_readZoneInStocks(String slabNo) {
        String queryCode = "ym.common.dao.selectZoneInStocks";
        return super.findList(queryCode, new Object[]{ slabNo });
    }
    
    public List readZoneInStocks_Del(String slabNo) {
        String queryCode = "ym.common.dao.selectZoneInStocks_Del";
        /*
        --ym.common.dao.selectZoneInStocks_Del
		SELECT  ?,--무의미한값
		        STOCK.STOCK_ID AS STL_NO,           --저장품ID
		        COMM.ORD_YEOJAE_GP,                 --주문여재구분
		        NVL(COMM.ORD_NO, '') ||
		        NVL(COMM.ORD_DTL, '') AS PRODUC_NO, --제작번호행번
		        COMM.SLAB_T,                        --두께
		        COMM.SLAB_W,                        --폭
		        COMM.SLAB_LEN,                      --길이
		        COMM.SLAB_WT,                       --중량
		        COMM.COIL_NO,                       --예정COILNO
		        COMM.STACK_LOT_NO AS STACK_LOT_CD, --산적 LOT CODE
		        COMM.BUY_SLAB_NO                	--구입슬라브번호
		FROM    TB_YM_STOCK         STOCK,
		        (SELECT * FROM VW_YD_SLABCOMM A, TB_QM_BUYSLABINFO B
		         WHERE A.MSLAB_NO = B.MSLAB_NO(+)
		        )COMM 
		WHERE   STOCK.CHARGE_LOT_NO IS NOT NULL
		AND     STOCK.STOCK_ID      = COMM.SLAB_NO
        */
        return super.findList(queryCode, new Object[]{ slabNo });
    }
    
    public JDTORecord readZoneInStocks_Lot(String sSlabNo) {
        String queryCode = "ym.common.dao.selectCurZoinLotNoComplete";
        /*
		SELECT  
		        STOCK.STOCK_ID AS STL_NO,           --저장품ID
		        COMM.ORD_YEOJAE_GP,             	--주문여재구분
		        NVL(COMM.ORD_NO, '') ||
		        NVL(COMM.ORD_DTL, '') AS PRODUC_NO, --제작번호행번
		        COMM.SLAB_T,                        --두께
		        COMM.SLAB_W,                        --폭
		        COMM.SLAB_LEN,                      --길이
		        COMM.SLAB_WT,                       --중량
		        COMM.COIL_NO,                       --예정COILNO
                COMM.STACK_LOT_NO AS STACK_LOT_CD, 	--산적 LOT CODE
		        COMM.BUY_SLAB_NO                	--구입슬라브번호
		FROM    TB_YM_STOCK         STOCK,
		        (SELECT * FROM VW_YD_SLABCOMM A, TB_QM_BUYSLABINFO B
		         WHERE A.MSLAB_NO = B.MSLAB_NO(+)
		        )COMM 
		WHERE   STOCK.STOCK_ID	= :STOCK_ID
		AND     STOCK.STOCK_ID      = COMM.SLAB_NO
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ sSlabNo });
	}
    
    /**
     * 조업시스템의 PO_SLAB 이송테이블을 조회하여 슬라브 정보를 리턴한다.
     * @param ordDate	이송지시일정
     * @param ordNo	이송지시번호
     * @return
     */
    public List readMoveOrderStocks(String ordDate, String ordNo) {
		/*
		SELECT  NVL(SLABCOMM.SCARFING_YN,'N') AS SCARFING_YN,
		        MOVE.SLAB_NO,
		        MOVE.FRTOMOVE_WREQ_DATE ||
		        MOVE.FRTOMOVE_WREQ_SEQNO AS FRTOMOVE_WREQ_DATE_NO
		FROM    (
		        SELECT  SLAB_NO, 
		                MAX(STEP_NO) AS STEP_NO
		        FROM    TB_PO_SLABFRTOMOVE
		        WHERE   FRTOMOVE_WREQ_DATE = ?
		        AND     FRTOMOVE_WREQ_SEQNO= ?
		        GROUP BY SLAB_NO
		        ) MAX_SLAB,        
		        TB_PO_SLABFRTOMOVE  MOVE,
		        TB_PM_SLABCOMM      SLABCOMM
		WHERE   MAX_SLAB.SLAB_NO = MOVE.SLAB_NO
		AND     MAX_SLAB.STEP_NO = MOVE.STEP_NO
		AND     MAX_SLAB.SLAB_NO = SLABCOMM.SLAB_NO
		AND     SLABCOMM.CURR_PROG_CD = 'C'--이송지시
		*/
        String queryCode = "ym.common.dao.selectMoveOrderStocks";
        return super.findList(queryCode, new Object[]{ ordDate, ordNo });
    }

    /**A열연 SLAB야드 이송지시 (MCH)
     * 조업시스템의 PO_SLAB 이송테이블을 조회하여 슬라브 정보를 리턴한다.
     * 작업예약시 동별로 작업예약을 생성하기 위해서 동구분 들어감
     * @param ordDate	이송지시일정
     * @param ordNo	이송지시번호
     * @return
     */
    public List readMoveOrderStocksbay(String ordDate, String ordNo, String bay) {
		/*
		SELECT  MOVE.SLAB_NO
		FROM    ( 
		        SELECT  SLAB_NO, 
		                MAX(STEP_NO) AS STEP_NO 
		        FROM    TB_PO_SLABFRTOMOVE 
		        WHERE   FRTOMOVE_WREQ_DATE = ? 
		        AND     FRTOMOVE_WREQ_SEQNO= ? 
		        GROUP BY SLAB_NO 
		        ) MAX_SLAB, 
		        TB_PO_SLABFRTOMOVE  MOVE, 
		        TB_PM_SLABCOMM      SLABCOMM, 
				TB_YM_STACKLAYER 	LAYER, 
				TB_YM_STACKCOL		COL 
		WHERE   MAX_SLAB.SLAB_NO = MOVE.SLAB_NO 
		AND     MAX_SLAB.STEP_NO = MOVE.STEP_NO 
		AND     MAX_SLAB.SLAB_NO = SLABCOMM.SLAB_NO 
		AND     MAX_SLAB.SLAB_NO = LAYER.STOCK_ID 
		AND     LAYER.STACK_COL_GP = COL.STACK_COL_GP 
		AND     LAYER.STACK_LAYER_STAT IN('S','L','U') 
		AND     COL.BAY_GP = ?
		AND     SLABCOMM.CURR_PROG_CD = 'C'--이송지시
--		ORDER BY LAYER.STACK_BED_GP ASC, STACK_LAYER_GP DESC
		ORDER BY STACK_LAYER_GP DESC,SLABCOMM.SLAB_LEN DESC, LAYER.STACK_BED_GP ASC
		*/
        String queryCode = "ym.common.dao.selectMoveOrderStocksBay";
        return super.findList(queryCode, new Object[]{ ordDate, ordNo, bay});
    }

    /**
     * 슬라브 공통테이블을 조회하여 슬라브 압연지시 정보를 리턴한다
     * @param proCode	진도코드
     * @param date		압연지시일자
     * @return
     */
    public List YJK_DEL_readRollingSlabInfo(String proCode, String date) {
        String sQueryId = "ym.common.dao.selectRollingSlabInfo";
        return super.findList(sQueryId, new Object[]{ date, date });
    }
    
    /**
     * 적치단 테이블에서 'E' 상태인 단의 상단의 상태가 'V'인 것을 리턴한다.
     * @param col	적치열
     * @param bad	번지
     * @param layer	단
     * @return
     */
    public JDTORecord readNextLayerStat(String col, String bad, String layer) {
		String  queryCode = "ym.common.dao.selectNextLayerStat";
		/*
		--ym.common.dao.selectNextLayerStat
		--상단의 상태를 리턴한다.
		SELECT  STACK_LAYER_STAT
		FROM     TB_YM_STACKLAYER
		WHERE   STACK_COL_GP = ?
		AND       STACK_BED_GP = ?
		AND       STACK_LAYER_GP = ?
		AND       STACK_LAYER_ACTIVE_STAT = 'O'
		AND       STACK_LAYER_STAT = 'V'
		*/
		return super.findByPrimaryKey(queryCode, new Object[]{ col, bad, layer });
    }
	
	/*
	
	*
	*	1.	야드
	*			
	JDTORecord readToLocDefine(String stockId, 
							   String col, 
							   String gp) 
	
	*
	*	1.	확장대차(2XTC01)
	*
	JDTORecord readToLocDefine(String colGp, 
							   String wt, 
							   String t, 
							   String w, 
							   String len) 
	
	*
	*	1.	스카핑입측(2ESE01)		
	*	2.	W/B   입측(2CWB01)
	*
	JDTORecord readToLocDefine(String colGp) 
	
	*/	
    /**
     * 적치 가능한 TO 위치를 리턴한다.
     * @param stockId	저장품ID
     * @param col		적치열
     * @param gp		쿼리구분
     *
     *		0	=>	동일한 장입LOT번호 TO위치 검색
	 *		01	=>	동일한 장입LOT번호 01단 TO위치 검색
	 *		1	=>	동일한 산적LOT번호 TO위치 검색
	 *		2	=>	동일한 산적LOT번호 01단 TO위치 검색
	 *		5	=>	적치기준,BED TYPE 가능한 TO위치 검색
     *
     * @return
     */
    public JDTORecord readToLocDefine(String stockId, String col, String gp) {
        String queryCode = "";
        if("0".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineZero";
        }else if("01".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineZeroOne";            
        }else if("1".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineOne";
        }else if("2".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineTwo";
        }else if("3".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineThree";
        }else if("4".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineFour";
        }else if("5".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineFive";
        }else if("6".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineSix";
        }else if("7".equals(gp)) {
            queryCode = "ym.common.dao.selectToLocDefineSeven";
        }
        return super.findByPrimaryKey(queryCode, new Object[]{ stockId, col });
    }
	
    public JDTORecord readSlabToLocInfo(String sSlabNo, String sColGp, String sGbn,String schkind, String stockmt) {
        /*******************************************************************************************
		SELECT  A.COL        AS STACK_COL_GP,--위치, 
		        A.BED           AS STACK_BED_GP,--번지, 
		        A.LAYER         AS STACK_LAYER_GP,--단,         
		        A.SLAB_ID, 
		        A.LOT_NO        AS CURR_LOT,        
		        A.DNLOT_NO      AS BUTTOM_LOT,        
		        A.SLAB_LN       AS SLAB길이, 
		        A.SLAB_WT       AS SLAB중량,
		        A.DNSLAB_LN     AS DNSLAB길이, 
		        A.DNSLAB_WT     AS DNSLAB중량,        		        
		        NVL(A.COL, '')    ||
		        NVL(A.BED, '')  AS GRIP_TO,        
		        NVL(A.COL, '')    ||
		        NVL(A.BED, '')    ||
		        NVL(A.LAYER, '')AS TO_LOC,
		        A.PITCH         AS 간격,
		        A.BED_WD, 
		        A.BED_LN, 
		        A.BED_MIN_WD, 
		        A.BED_MIN_LN, 
		        A.BED_MAX_WD, 
		        A.BED_MAX_LN,
		        A.SLAB_TK       AS SLAB두께, 
		        A.SLAB_WD       AS SLAB폭, 
		        A.PROG_CD       AS 진도코드, 
		        A.SPEC_NM       AS 규격약호, 
		        A.ORD_NO        AS 주문번호, 
		        A.ORD_NUM       AS 행번, 
		        A.INPUT_DT      AS 입고일자, 
		        A.DOWN_BED      AS 하단번지, 
		        A.DOWN_LAYER    AS 하단단, 
		        A.DOWN_STATE    AS 하단상태,
		        A.DNSLAB_ID, 
		        A.DNSLAB_TK     AS DNSLAB두께, 
		        A.DNSLAB_WD     AS DNSLAB폭, 
		        A.DNPROG_CD     AS 하진도코드, 
		        A.DNSPEC_NM     AS 하규격약호, 
		        A.DNORD_NO      AS 하주문번호, 
		        A.DNORD_NUM     AS 하행번, 
		        A.DNINPUT_DT    AS 하입고일자, 
		        A.UPWT, A.UPWT_YN, A.UPWT_MIN, A.UPWT_MAX, 
		        A.UPLN, A.UPLN_YN, A.UPLN_MIN, A.UPLN_MAX,
		        A.UPWD, A.UPWD_YN, A.UPWD_MIN, A.UPWD_MAX
		FROM    (
		        SELECT  A.COL, 
		                A.BED, 
		                A.LAYER, 
		                A.SLAB_ID, 
		                A.DNSLAB_ID, 
		                A.LOT_NO,
		                A.DNLOT_NO,
		                A.SLAB_WT,
		                A.DNSLAB_WT,
		                A.SLAB_LN, 
		                A.DNSLAB_LN, 
		                A.CHARGE_LOT_NO,
		                A.DNCHARGE_LOT_NO, 
		                A.PITCH,
		                A.BED_WD,  A.BED_LN,  A.BED_MIN_WD, A.BED_MIN_LN, A.BED_MAX_WD, A.BED_MAX_LN,
		                A.SLAB_TK, A.SLAB_WD, 
		                A.PROG_CD, A.SPEC_NM, A.ORD_NO, A.ORD_NUM, A.INPUT_DT, 
		                A.DOWN_BED,  A.DOWN_LAYER, A.DOWN_STATE,
		                A.DNSLAB_TK, A.DNSLAB_WD, 
		                A.DNPROG_CD, A.DNSPEC_NM, A.DNORD_NO, A.DNORD_NUM, A.DNINPUT_DT, 		                   
		                S1.STACK_RULE_CD AS UPWT, S1.STACK_RULE_USE_YN AS UPWT_YN, S1.STACK_RULE_MIN AS UPWT_MIN, S1.STACK_RULE_MAX AS UPWT_MAX, 
		                S2.STACK_RULE_CD AS UPLN, S2.STACK_RULE_USE_YN AS UPLN_YN, S2.STACK_RULE_MIN AS UPLN_MIN, S2.STACK_RULE_MAX AS UPLN_MAX,
		                S3.STACK_RULE_CD AS UPWD, S3.STACK_RULE_USE_YN AS UPWD_YN, S3.STACK_RULE_MIN AS UPWD_MIN, S3.STACK_RULE_MAX AS UPWD_MAX,
		                -- BED 폭 허용치 비교
		                DECODE(GREATEST(A.BED_MIN_WD, A.SLAB_WD), A.SLAB_WD, 
		                    DECODE(LEAST(A.BED_MAX_WD, A.SLAB_WD), A.SLAB_WD, 'T', 'F'), 'F') AS BED_WD_CP,
		                -- BED 길이 허용치 비교
		                DECODE(GREATEST(A.BED_MIN_LN, A.SLAB_LN), A.SLAB_LN, 
		                    DECODE(LEAST(A.BED_MAX_LN, A.SLAB_LN), A.SLAB_LN, 'T', 'F'), 'F') AS BED_LN_CP,
		                -- 2단 : 상하SLAB 중량기준 상/하한값 비교
		                DECODE(A.LAYER, '01', 'T', -- TRUE
		                    DECODE(S1.STACK_RULE_USE_YN, 'Y', 
		                        DECODE(GREATEST((A.DNSLAB_WT - S1.STACK_RULE_MIN), A.SLAB_WT), A.SLAB_WT,
		                            DECODE(LEAST((A.DNSLAB_WT + S1.STACK_RULE_MAX), A.SLAB_WT), A.SLAB_WT, 'T', 'F'), 'F'), 'T')) AS UPDN_WT_CP,                  
		                -- 2단 : 상하SLAB 길이기준 상/하한값 비교
		                DECODE(A.LAYER, '01', 'T', -- TRUE
		                    DECODE(S1.STACK_RULE_USE_YN, 'Y', 
		                        DECODE(GREATEST((A.DNSLAB_LN - S2.STACK_RULE_MIN), A.SLAB_LN), A.SLAB_LN,
		                            DECODE(LEAST((A.DNSLAB_LN + S2.STACK_RULE_MAX), A.SLAB_LN), A.SLAB_LN, 'T', 'F'), 'F'), 'T')) AS UPDN_LN_CP,                  
		                -- 2단 : 상하SLAB 기준 상/하한값 비교
		                DECODE(A.LAYER, '01', 'T', -- TRUE
		                    DECODE(S1.STACK_RULE_USE_YN, 'Y', 
		                        DECODE(GREATEST((A.DNSLAB_WD - S3.STACK_RULE_MIN), A.SLAB_WD), A.SLAB_WD,
		                            DECODE(LEAST((A.DNSLAB_WD + S3.STACK_RULE_MAX), A.SLAB_WD), A.SLAB_WD, 'T', 'F'), 'F'), 'T')) AS UPDN_WD_CP
		        FROM    ( 
		                SELECT  SUBSTR(A.COL, 1, 1) AS YD_GP, 
		                        SUBSTR(A.COL, 2, 1) AS BAY_GP, 
		                        A.USAGE_CD          AS USE_CD,
		                        -- 적치 SLAB INFO
		                        A.COL, 
		                        A.BED, 
		                        A.LAYER, 
		                        A.PITCH,
		                        A.BED_WD, 
		                        A.BED_LN, 
		                        A.BED_MIN_WD, 
		                        A.BED_MIN_LN, 
		                        A.BED_MAX_WD, 
		                        A.BED_MAX_LN,
		                        A.SLAB_ID, 
		                        B.CHARGE_LOT_NO, 
		                        B.CAR_CARD_NO   AS CARD_NO,
		                        E.SLAB_T        AS SLAB_TK, 
		                        E.SLAB_W        AS SLAB_WD, 
		                        E.SLAB_LEN      AS SLAB_LN, 
		                        E.CAL_SLAB_WT   AS SLAB_WT,
		                        E.CURR_PROG_CD  AS PROG_CD, 
		                        E.SPEC_ABBSYM   AS SPEC_NM, 
		                        E.ORD_NO        AS ORD_NO, 
		                        E.ORD_DTL       AS ORD_NUM, 
		                        E.RECEIPT_DATE  AS INPUT_DT, 
		                        E.INHOUSE_YD_STACK_LOT_NO AS LOT_NO, 
		                        E.PORT_YD_STACK_LOT_NO,
		                        E.ORD_HCR_GP        AS ORD_HCR_GP,
		                        -- 하단 SLAB INFO
		                        -- Slab Size 품질 실측치 반영
		                        A.DOWN_BED, 
		                        A.DOWN_LAYER, 
		                        A.DOWN_ACTIVE, 
		                        A.DOWN_STATE, 
		                        A.DNSLAB_ID, 
		                        C.CHARGE_LOT_NO AS DNCHARGE_LOT_NO, 
		                        C.CAR_CARD_NO   AS DNCARD_NO,
		                        F.SLAB_T        AS DNSLAB_TK, 
		                        F.SLAB_W        AS DNSLAB_WD, 
		                        F.SLAB_LEN      AS DNSLAB_LN, 
		                        F.CAL_SLAB_WT   AS DNSLAB_WT,
		                        F.CURR_PROG_CD  AS DNPROG_CD, 
		                        F.SPEC_ABBSYM   AS DNSPEC_NM, 
		                        F.ORD_NO        AS DNORD_NO, 
		                        F.ORD_DTL       AS DNORD_NUM, 
		                        F.RECEIPT_DATE  AS DNINPUT_DT, 
		                        F.INHOUSE_YD_STACK_LOT_NO AS DNLOT_NO, 
		                        F.PORT_YD_STACK_LOT_NO,
		                        F.ORD_HCR_GP        AS DNHCR_GP
		                FROM    ( 
		                        SELECT  -- 적치대 정보 조회
		                                A.COL, 
		                                A.BED, 
		                                A.LAYER, 
		                                A.PITCH, 
		                                A.USAGE_CD,
		                                A.BED_WD, 
		                                A.BED_LN, 
		                                A.BED_MIN_WD, 
		                                A.BED_MIN_LN, 
		                                A.BED_MAX_WD, 
		                                A.BED_MAX_LN, 
		                                A.SLAB_ID,
		                                A.DOWN_BED, 
		                                A.DOWN_LAYER, 
		                                B.STACK_LAYER_ACTIVE_STAT   AS DOWN_ACTIVE,  
		                                B.STACK_LAYER_STAT          AS DOWN_STATE, 
		                                B.STOCK_ID                  AS DNSLAB_ID
		                        FROM    ( 
		                                SELECT  +INDEX_ASC(A PK_YM_STACKLAYER)
		                                        A.STACK_COL_GP          AS COL, 
		                                        A.STACK_BED_GP          AS BED, 
		                                        A.STACK_LAYER_GP        AS LAYER, 
		                                        B.STACK_COL_BED_PITCH   AS PITCH,
		                                        B.STACK_COL_USAGE_CD    AS USAGE_CD,
		                                        C.STACK_BED_W_CURR      AS BED_WD, 
		                                        C.STACK_BED_LEN_CURR    AS BED_LN,
		                                        C.STACK_BED_ABLE_W      AS BED_MIN_WD, 
		                                        C.STACK_BED_ABLE_LEN    AS BED_MIN_LN,
		                                        C.STACK_BED_W_MAX       AS BED_MAX_WD, 
		                                        C.STACK_BED_LEN_MAX     AS BED_MAX_LN,
		                                        :stock_id               AS SLAB_ID, -- ? AS SLAB_ID,
		                                        A.STACK_BED_GP          AS DOWN_BED,
		                                        LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0') 
		                                                                AS DOWN_LAYER
		                                FROM    TB_YM_STACKLAYER    A, 
		                                        TB_YM_STACKCOL      B, 
		                                        TB_YM_STACKER       C
		                                WHERE   A.STACK_COL_GP              LIKE :COL_GP||'%'
		                                AND     A.STACK_LAYER_ACTIVE_STAT   = 'O'
		                                AND     A.STACK_LAYER_STAT          = 'E'
		                                AND     A.STACK_COL_GP              = B.STACK_COL_GP
		                                AND     A.STACK_COL_GP              = C.STACK_COL_GP 
		                                AND     A.STACK_BED_GP              = C.STACK_BED_GP
		                                ) A, 
		                                TB_YM_STACKLAYER B
		                        WHERE   A.COL       = B.STACK_COL_GP(+) 
		                        AND     A.DOWN_BED  = B.STACK_BED_GP(+) 
		                        AND     A.DOWN_LAYER= B.STACK_LAYER_GP(+)
		                        ) A, 
		                        TB_YM_STOCK B, 
		                        TB_YM_STOCK C,
		                        USRPMA.TB_PM_SLABCOMM E, 
		                        USRPMA.TB_PM_SLABCOMM F,
                                TB_YM_WBOOK G,
                                TB_YM_WBOOK H
		                WHERE   A.SLAB_ID   = B.STOCK_ID(+) 
		                AND     A.DNSLAB_ID = C.STOCK_ID(+)
		                AND     A.SLAB_ID   = E.SLAB_NO(+)  
		                AND     A.DNSLAB_ID = F.SLAB_NO(+)
		                AND     B.WBOOK_ID  = G.WBOOK_ID(+)
                        AND     C.WBOOK_ID  = H.WBOOK_ID(+)
		                AND     DECODE(A.LAYER, '01', '-', A.DOWN_ACTIVE) = DECODE(A.LAYER, '01', '-', 'O') 
		                AND     (
		                        DECODE(A.LAYER, '01', '-', A.DOWN_STATE)  = DECODE(A.LAYER, '01', '-', 'L')   
		                        OR 
		                        	(
		                        	DECODE(A.LAYER, '01', '-', A.DOWN_STATE)  = DECODE(A.LAYER, '01', '-', 'P')
		                        	AND 
                                    NVL(G.SCH_WORK_KIND,'SCH') = NVL(H.SCH_WORK_KIND,'SCH')
                                    )
		                        )
		                ) A, 
		                TB_YM_STACKRULE S1,  
		                TB_YM_STACKRULE S2, 
		                TB_YM_STACKRULE S3
		        WHERE   A.YD_GP = S1.YD_GP AND A.BAY_GP = S1.BAY_GP AND A.USE_CD = S1.STACK_COL_USAGE_CD AND S1.STACK_RULE_CD = 'UPWT' -- 상단중량기준
		        AND     A.YD_GP = S2.YD_GP AND A.BAY_GP = S2.BAY_GP AND A.USE_CD = S2.STACK_COL_USAGE_CD AND S2.STACK_RULE_CD = 'UPLN' -- 상단길이기준
		        AND     A.YD_GP = S3.YD_GP AND A.BAY_GP = S3.BAY_GP AND A.USE_CD = S3.STACK_COL_USAGE_CD AND S3.STACK_RULE_CD = 'UPWD' -- 상단폭기준
		        ) A
		        ********************************************************************************************/
            		 //AND     DECODE(A.LAYER,'01','-',DECODE(E.ORD_HCR_GP,'W','HOT','COOL')) = DECODE(A.LAYER,'01','-',DECODE(F.ORD_HCR_GP,'W','HOT','COOL'))	
        StringBuffer sDsql = new StringBuffer();
        /*
         *	G : 하단에 같은 장입순번이 있는 위치를 검색
         *	S : 하단에 같은 산적번호가 있는 위치를 검색
         *	E : 적치가능한 01단 위치를 검색
         *	P : 하단에 후순위의 장입순번이 있는 위치를 검색
         *	U : 적치가능한 02단 이상정보를 검색
         *	A : A열연 Slab야드 위치 검색[하단의 두께,폭,길이가 허용범위안에 있는 위치 검색]
         *	B : A열연 Slab야드 위치 검색[하단의 저장품이동조건이 같은 위치 검색]
         *	K : 하단에 동일강종이 있는 위치를 검색
         */
        if(YmCommonConst.SLAB_TO_LOC_G.equals(sGbn)) {
            
            sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.LAYER     > '01'");
			sDsql.append("\n AND     A.DNCHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n AND     A.CHARGE_LOT_NO = A.DNCHARGE_LOT_NO ");
				
		}else if(YmCommonConst.SLAB_TO_LOC_P.equals(sGbn)) {
	        	
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.LAYER     > '01'");
			sDsql.append("\n AND     A.DNCHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n AND     A.CHARGE_LOT_NO < A.DNCHARGE_LOT_NO ");
			
			/*
			 * 2007.02.02 이정훈 
			 * C동 이외 다른 동에 후순위 장입재 존재 할 때 Skip
			 */
			sDsql.append("\n AND NOT EXISTS (SELECT B.STOCK_ID  ");
			sDsql.append("\n 	FROM  TB_YM_STOCK B, TB_YM_STACKLAYER C ");
			sDsql.append("\n 	WHERE B.CHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n 	AND B.STOCK_ID = C.STOCK_ID(+) ");
			sDsql.append("\n 	AND A.DNCHARGE_LOT_NO = B.CHARGE_LOT_NO ");
			sDsql.append("\n 	AND SUBSTR(C.STACK_COL_GP,2,1) != 'C' ");
			sDsql.append("\n 	AND SUBSTR(C.STACK_COL_GP,1,1) = '2')  ");
				
		}else if(YmCommonConst.SLAB_TO_LOC_N.equals(sGbn)) {
        	
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.LAYER     > '01'");
			sDsql.append("\n AND     A.DNCHARGE_LOT_NO IS NULL ");
			
        }else if(YmCommonConst.SLAB_TO_LOC_S.equals(sGbn)) {
        	
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.LAYER     > '01'");
			sDsql.append("\n AND     A.DNLOT_NO IS NOT NULL ");
			sDsql.append("\n AND     A.LOT_NO = A.DNLOT_NO ");
			/*
			 * 2007.02.02 이정훈 
			 * 하단에 장입재 존재 할 때 Skip
			 */
			sDsql.append("\n AND NOT EXISTS (SELECT  B.STOCK_ID ");
			sDsql.append("\n 	FROM  TB_YM_STOCK B, TB_YM_STACKLAYER C ");
			sDsql.append("\n 	WHERE B.CHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n 	AND B.STOCK_ID = C.STOCK_ID(+) ");
			sDsql.append("\n 	AND C.STACK_COL_GP = A.COL ");
			sDsql.append("\n 	AND C.STACK_BED_GP = A.BED ");
			sDsql.append("\n 	AND C.STACK_LAYER_STAT IN ('L','S')) ");
				
        }else if(YmCommonConst.SLAB_TO_LOC_E.equals(sGbn)) {
        	
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.LAYER     = '01'");
			
        }else if(YmCommonConst.SLAB_TO_LOC_U.equals(sGbn)) {
            
            sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.LAYER     > '01'");
			
			/*
			 * 2007.02.02 이정훈 
			 * 하단에 장입재 존재 할 때 Skip
			 */
			sDsql.append("\n AND NOT EXISTS (SELECT  B.STOCK_ID ");
			sDsql.append("\n 	FROM  TB_YM_STOCK B, TB_YM_STACKLAYER C ");
			sDsql.append("\n 	WHERE B.CHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n 	AND B.STOCK_ID = C.STOCK_ID(+) ");
			sDsql.append("\n 	AND C.STACK_COL_GP = A.COL ");
			sDsql.append("\n 	AND C.STACK_BED_GP = A.BED ");
			sDsql.append("\n 	AND C.STACK_LAYER_STAT  IN ('L','S')) ");
		
		}else if(YmCommonConst.SLAB_TO_LOC_A.equals(sGbn)) {
        	
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
		
		}else if("L".equals(sGbn)) {
        	//동일강종 , 동일 생산폭(30mm이내), 동일 주문두께,주문폭 
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.ORD_YEOJAE_GP=A.DNORD_YEOJAE_GP ");
			sDsql.append("\n AND     A.SPEC_NM = A.DNSPEC_NM --동일강종 ");
			sDsql.append("\n AND     A.SLAB_WD BETWEEN A.DNSLAB_WD-30 AND A.DNSLAB_WD+30 --동일 생산폭(30mm이내) ");
			sDsql.append("\n AND     A.ORD_CONV_T=A.DNORD_CONV_T ");
			sDsql.append("\n AND     A.ORD_CONV_W =A.DNORD_CONV_W ");
			sDsql.append("\n AND NOT EXISTS (SELECT  B.STOCK_ID ");
			sDsql.append("\n 	FROM  TB_YM_STOCK B, TB_YM_STACKLAYER C ");
			sDsql.append("\n 	WHERE B.CHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n 	AND B.STOCK_ID = C.STOCK_ID(+) ");
			sDsql.append("\n 	AND C.STACK_COL_GP = A.COL ");
			sDsql.append("\n 	AND C.STACK_BED_GP = A.BED ");
			sDsql.append("\n 	AND C.STACK_LAYER_STAT  IN ('L','S')) ");
		
		}else if("L2".equals(sGbn)) {
        	//동일강종 , 동일 생산폭(30mm이내)
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.ORD_YEOJAE_GP=A.DNORD_YEOJAE_GP ");
			sDsql.append("\n AND     A.SPEC_NM = A.DNSPEC_NM --동일강종 ");
			sDsql.append("\n AND     A.SLAB_WD BETWEEN A.DNSLAB_WD-30 AND A.DNSLAB_WD+30 --동일 생산폭(30mm이내) ");
			sDsql.append("\n AND NOT EXISTS (SELECT  B.STOCK_ID ");
			sDsql.append("\n 	FROM  TB_YM_STOCK B, TB_YM_STACKLAYER C ");
			sDsql.append("\n 	WHERE B.CHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n 	AND B.STOCK_ID = C.STOCK_ID(+) ");
			sDsql.append("\n 	AND C.STACK_COL_GP = A.COL ");
			sDsql.append("\n 	AND C.STACK_BED_GP = A.BED ");
			sDsql.append("\n 	AND C.STACK_LAYER_STAT  IN ('L','S')) ");
		
		}else if("M".equals(sGbn)) {
        	//1단에 주문두께와 폭이 작은것 
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.ORD_YEOJAE_GP=A.DNORD_YEOJAE_GP ");
			sDsql.append("\n AND     A.ORD_CONV_T >= A.DNORD_CONV_T ");
			sDsql.append("\n AND     A.ORD_CONV_W >= A.DNORD_CONV_W ");
			sDsql.append("\n AND NOT EXISTS (SELECT  B.STOCK_ID ");
			sDsql.append("\n 	FROM  TB_YM_STOCK B, TB_YM_STACKLAYER C ");
			sDsql.append("\n 	WHERE B.CHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n 	AND B.STOCK_ID = C.STOCK_ID(+) ");
			sDsql.append("\n 	AND C.STACK_COL_GP = A.COL ");
			sDsql.append("\n 	AND C.STACK_BED_GP = A.BED ");
			sDsql.append("\n 	AND C.STACK_LAYER_STAT  IN ('L','S')) ");
			
		}else if(YmCommonConst.SLAB_TO_LOC_B.equals(sGbn)) {
        	
        	sDsql.append("\n WHERE   A.BED_WD_CP = 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP = 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP= 'T' ");
			sDsql.append("\n AND     A.LAYER     > '01'");
			sDsql.append("\n AND     A.DNMV_TERM IS NOT NULL ");
			sDsql.append("\n AND     A.MV_TERM = A.DNMV_TERM ");
			
		}else if(YmCommonConst.SLAB_TO_LOC_K.equals(sGbn)) {
	        	
        	sDsql.append("\n WHERE   A.BED_WD_CP 	= 'T' ");		
			sDsql.append("\n AND     A.BED_LN_CP 	= 'T' ");
			sDsql.append("\n AND     A.UPDN_WT_CP	= 'T' ");
			sDsql.append("\n AND     A.UPDN_LN_CP	= 'T' ");
			sDsql.append("\n AND     A.UPDN_WD_CP	= 'T' ");
			sDsql.append("\n AND     A.LAYER     > '01'");
			sDsql.append("\n AND     A.DNSPEC_NM IS NOT NULL ");
			sDsql.append("\n AND     A.SPEC_NM = A.DNSPEC_NM ");
			
			/*
			 * 2007.02.02 이정훈 
			 * 하단에 장입재 존재 할 때 Skip
			 */
			sDsql.append("\n AND NOT EXISTS (SELECT  B.STOCK_ID ");
			sDsql.append("\n 	FROM  TB_YM_STOCK B, TB_YM_STACKLAYER C ");
			sDsql.append("\n 	WHERE B.CHARGE_LOT_NO IS NOT NULL ");
			sDsql.append("\n 	AND B.STOCK_ID = C.STOCK_ID(+) ");
			sDsql.append("\n 	AND C.STACK_COL_GP = A.COL ");
			sDsql.append("\n 	AND C.STACK_BED_GP = A.BED ");
			sDsql.append("\n 	AND C.STACK_LAYER_STAT  IN ('L','S')) ");
					 
		}else{
			return null;
		}
	        
        if((YmCommonConst.NEW_SCH_WORK_KIND_SRLO.equals(schkind))) {
        	
    		if (YmCommonConst.NEW_STOCK_MOVE_TERM_D2.equals(stockmt) || // 시편작업대기
	    	    YmCommonConst.NEW_STOCK_MOVE_TERM_DS.equals(stockmt)) { // 스카핑재(정정작업대기)
	    		sDsql.append("\n ORDER BY A.BED DESC, A.LAYER ");
	    	}else{
	    		sDsql.append("\n ORDER BY A.BED, A.LAYER ");
	    	}    	
        } else if(YmCommonConst.NEW_SCH_WORK_KIND_SVMU.equals(schkind)) {
        	sDsql.append("\n ORDER BY A.BED DESC, A.LAYER ");
        } else {
        	/**
        	 * 2009.07.01 YJK
        	 * B열연 C동은 END BED 부터 적치가능 BED 검색처리.
        	 */
			if ("C".equals(sColGp.substring(1,2))) {
				sDsql.append("\n ORDER BY A.BED DESC, A.LAYER ");
			}else{
				sDsql.append("\n ORDER BY A.BED, A.LAYER ");
			}
        }
        String  queryCode = "ym.common.dao.readSlabToLocInfo_R";	
		Object[] params = {sSlabNo,sColGp};			
		return super.findByPrimaryKey(queryCode,sDsql.toString(),params);
    }
    
    /**
     * 스카핑 보급 TO 위치를 리턴한다.
     * @param colGp	적치열
     * @return
     */
    public JDTORecord readToLocDefine(String colGp) {
        String queryCode = "ym.common.dao.selectToLocDefine1";
        /*
        WITH TEMP AS (
		    SELECT '1' AS STACK_BED_ABLE_QNTY, 
		                STACK_COL_GP,   --적치 열 구분
		                STACK_BED_GP   --적치 BED 구분
		    FROM    TB_YM_STACKER
		    WHERE   STACK_COL_GP            LIKE :COL_GP||'%'
		    AND     STACK_BED_ACTIVE_STAT   = 'O'
		    ORDER BY STACK_BED_GP ASC
		)
		SELECT  A.STACK_COL_GP,
		        A.STACK_BED_GP,
		        A.STACK_LAYER_GP,
		        A.STACK_BED_ABLE_QNTY,
		        A.GRIP_TO,
		        A.TO_LOC        
		FROM    (
		        SELECT  CUR.STACK_COL_GP,    --적치 열 구분
		                CUR.STACK_BED_GP,    --적치 BED 구분
		                CUR.STACK_LAYER_GP,  --적치 단 구분
		                CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		                        
		                NVL(CUR.STACK_COL_GP, '')    ||
		                NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
		                
		                NVL(CUR.STACK_COL_GP, '')    ||
		                NVL(CUR.STACK_BED_GP, '')    ||
		                NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC
		        FROM    (
		                SELECT  TEMP.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		                        LAYER.STACK_COL_GP,
		                        LAYER.STACK_BED_GP,
		                        LAYER.STACK_LAYER_GP,
		                        DECODE(LAYER.STACK_LAYER_GP - 1,
		                            0, '01', 
		                            DECODE(LAYER.STACK_LAYER_GP - 1, 
		                                9, '0' || TO_CHAR(LAYER.STACK_LAYER_GP - 1),
		                                DECODE(LENGTH(LAYER.STACK_LAYER_GP - 1),
		                                    1, '0' || TO_CHAR(LAYER.STACK_LAYER_GP - 1),
		                                    TO_CHAR(LAYER.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP
		                FROM    TEMP,
		                        TB_YM_STACKLAYER LAYER
		                WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
		                AND     TEMP.STACK_BED_GP = LAYER.STACK_BED_GP
		                AND     LAYER.STACK_LAYER_STAT = 'E'
		                AND     LAYER.STACK_LAYER_ACTIVE_STAT = 'O'
		                ) CUR,
		                TB_YM_STACKLAYER STACKLAYER
		        WHERE   CUR.STACK_COL_GP = STACKLAYER.STACK_COL_GP
		        AND     CUR.STACK_BED_GP = STACKLAYER.STACK_BED_GP
		        AND     CUR.PRE_LAYER_GP = STACKLAYER.STACK_LAYER_GP
		        AND     STACKLAYER.STACK_LAYER_STAT NOT IN('S', 'U')
		        ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
		    )A
		WHERE   ROWNUM = 1
         */
        return super.findByPrimaryKey(queryCode, new Object[]{ colGp }); 
    }
    
    
    /**
     * 스카핑 보급 TO 위치를 리턴한다.
     * @param colGp	적치열
     * @return
     */
    public JDTORecord readToLocDefinebending(String colGp) {
        String queryCode = "ym.common.dao.selectToLocDefinebending";
        /*
        SELECT  A.STACK_COL_GP,
		        A.STACK_BED_GP,
		        A.STACK_LAYER_GP,
		        A.STACK_BED_ABLE_QNTY,
		        A.GRIP_TO,
		        A.TO_LOC        
		FROM    (
		        SELECT  CUR.STACK_COL_GP,    --적치 열 구분
		                CUR.STACK_BED_GP,    --적치 BED 구분
		                CUR.STACK_LAYER_GP,  --적치 단 구분
		                CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		                        
		                NVL(CUR.STACK_COL_GP, '')    ||
		                NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
		                
		                NVL(CUR.STACK_COL_GP, '')    ||
		                NVL(CUR.STACK_BED_GP, '')    ||
		                NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC
		        FROM    (
		                SELECT  TEMP.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		                        LAYER.STACK_COL_GP,
		                        LAYER.STACK_BED_GP,
		                        LAYER.STACK_LAYER_GP,
		                        DECODE(LAYER.STACK_LAYER_GP - 1,
		                            0, '01', 
		                            DECODE(LAYER.STACK_LAYER_GP - 1, 
		                                9, '0' || TO_CHAR(LAYER.STACK_LAYER_GP - 1),
		                                DECODE(LENGTH(LAYER.STACK_LAYER_GP - 1),
		                                    1, '0' || TO_CHAR(LAYER.STACK_LAYER_GP - 1),
		                                    TO_CHAR(LAYER.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP
		                                    
		                FROM    (
		                         SELECT '1' AS STACK_BED_ABLE_QNTY, 
		                         STACK_COL_GP,   --적치 열 구분
		                         STACK_BED_GP   --적치 BED 구분
		                         FROM    TB_YM_STACKER
		                         WHERE   STACK_COL_GP            LIKE :COL_GP||'%'
		                         AND     STACK_BED_ACTIVE_STAT   = 'O'
		                         ORDER BY STACK_BED_GP ASC
		                        )TEMP,
		                        TB_YM_STACKLAYER LAYER
		                WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
		                AND     TEMP.STACK_BED_GP = LAYER.STACK_BED_GP
		                AND     LAYER.STACK_LAYER_STAT = 'E'
		                AND     LAYER.STACK_LAYER_ACTIVE_STAT = 'O'
		                AND     LAYER.STOCK_ID IS NULL
		                ) CUR,
		                TB_YM_STACKLAYER STACKLAYER
		        WHERE   CUR.STACK_COL_GP = STACKLAYER.STACK_COL_GP
		        AND     CUR.STACK_BED_GP = STACKLAYER.STACK_BED_GP
		        AND     CUR.PRE_LAYER_GP = STACKLAYER.STACK_LAYER_GP
		        AND     STACKLAYER.STACK_LAYER_STAT NOT IN('S', 'U')
		        ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
		    )A
		WHERE STACK_BED_GP='03' -- BENDING 존 
		and rownum<=1
         */
        return super.findByPrimaryKey(queryCode, new Object[]{ colGp }); 
    }
	
	/**
     * 적치 가능한 TO 위치를 리턴한다.
     * @param colGp	적치열
     * @param wt	중량
     * @param t		두께=높이
     * @param w		폭
     * @return
     */
    public JDTORecord readBasicToLocInfo(String colGp, String w1, String w2, String len1, String len2) {
        String queryCode = "ym.common.dao.selectToLocDefine2";
        /*
        WITH TEMP AS (
		    SELECT  STACK_COL_GP,   		--적치 열 구분
		            STACK_BED_GP,   		--적치 BED 구분
		            STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		            STACK_BED_ABLE_WT,      --적치 BED 가능 중량
		            STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
		            STACK_BED_ABLE_W,       --적치 BED 가능 폭
		            STACK_BED_ABLE_LEN  	--적치 BED 가능 길이
		    FROM    TB_YM_STACKER
		    WHERE   STACK_COL_GP            LIKE :COL_GP||'%'
		    AND     STACK_BED_ACTIVE_STAT   = 'O'
		    AND     STACK_BED_ABLE_QNTY     >  0	 --적치 BED 가능 수량
		    AND     STACK_BED_ABLE_W        <= :W    --적치 BED 가능 폭
		    AND     STACK_BED_W_MAX         >= :W    --적치 BED 가능 폭
		    AND     STACK_BED_ABLE_LEN      <= :LEN  --적치 BED 가능 길이
		    AND     STACK_BED_LEN_MAX       >= :LEN  --적치 BED 가능 길이
		    ORDER BY STACK_BED_GP ASC
		)
		SELECT  CUR.STACK_COL_GP,    --적치 열 구분
		        CUR.STACK_BED_GP,    --적치 BED 구분
		        CUR.STACK_LAYER_GP,  --적치 단 구분
		
		        CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		        CUR.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
		        CUR.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
		        CUR.STACK_BED_ABLE_W,       --적치 BED 가능 폭
		        CUR.STACK_BED_ABLE_LEN,     --적치 BED 가능 길이
		                
		        NVL(CUR.STACK_COL_GP, '')    ||
		        NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
		        
		        NVL(CUR.STACK_COL_GP, '')    ||
		        NVL(CUR.STACK_BED_GP, '')    ||
		        NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC
		FROM    (
		        SELECT  TEMP.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		                TEMP.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
		                TEMP.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
		                TEMP.STACK_BED_ABLE_W,       --적치 BED 가능 폭
		                TEMP.STACK_BED_ABLE_LEN,     --적치 BED 가능 길이
		
		                LAYER.STACK_COL_GP,
		                LAYER.STACK_BED_GP,
		                LAYER.STACK_LAYER_GP,
		                DECODE(LAYER.STACK_LAYER_GP - 1,
		                    0, '01', 
		                    DECODE(LAYER.STACK_LAYER_GP - 1, 
		                        9, '0' || TO_CHAR(LAYER.STACK_LAYER_GP - 1),
		                        DECODE(LENGTH(LAYER.STACK_LAYER_GP - 1),
		                            1, '0' || TO_CHAR(LAYER.STACK_LAYER_GP - 1),
		                            TO_CHAR(LAYER.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP
		        FROM    TEMP,
		                TB_YM_STACKLAYER LAYER
		        WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
		        AND     TEMP.STACK_BED_GP = LAYER.STACK_BED_GP
		        AND     LAYER.STACK_LAYER_STAT        = 'E'
		        AND     LAYER.STACK_LAYER_ACTIVE_STAT = 'O'
		        ) CUR,
		        TB_YM_STACKLAYER STACKLAYER
		WHERE   CUR.STACK_COL_GP = STACKLAYER.STACK_COL_GP
		AND     CUR.STACK_BED_GP = STACKLAYER.STACK_BED_GP
		AND     CUR.PRE_LAYER_GP = STACKLAYER.STACK_LAYER_GP
		AND     STACKLAYER.STACK_LAYER_STAT NOT IN('S', 'U')
		ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ colGp, w1, w2, len1, len2 }); 
    }
    
    /**
     * 적치 가능한 TO 위치를 리턴한다.
     * @param colGp	적치열
     * @param bedGp	번지
     * @return
     */
    public JDTORecord readBasicToLocInfo(String colGp, String bedGp) {
        String queryCode = "ym.common.dao.selectToLocDefine3";
        /*
        WITH TEMP AS (
		    SELECT  STACK_COL_GP,   		--적치 열 구분
		            STACK_BED_GP,   		--적치 BED 구분
		            STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		            STACK_BED_ABLE_WT,      --적치 BED 가능 중량
		            STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
		            STACK_BED_ABLE_W,       --적치 BED 가능 폭
		            STACK_BED_ABLE_LEN  	--적치 BED 가능 길이
		    FROM    TB_YM_STACKER
		    WHERE   STACK_COL_GP            LIKE :COL_GP||'%'
		    AND     STACK_BED_GP            LIKE :BED_GP||'%'
		    AND     STACK_BED_ACTIVE_STAT   = 'O'
		    AND     STACK_BED_ABLE_QNTY     >  0	 --적치 BED 가능 수량
		    ORDER BY STACK_BED_GP ASC
		)
		SELECT  CUR.STACK_COL_GP,    --적치 열 구분
		        CUR.STACK_BED_GP,    --적치 BED 구분
		        CUR.STACK_LAYER_GP,  --적치 단 구분		
		        CUR.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		        CUR.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
		        CUR.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
		        CUR.STACK_BED_ABLE_W,       --적치 BED 가능 폭
		        CUR.STACK_BED_ABLE_LEN,     --적치 BED 가능 길이
		        NVL(CUR.STACK_COL_GP, '')    ||
		        NVL(CUR.STACK_BED_GP, '') AS GRIP_TO,
		        NVL(CUR.STACK_COL_GP, '')    ||
		        NVL(CUR.STACK_BED_GP, '')    ||
		        NVL(CUR.STACK_LAYER_GP, '') AS TO_LOC
		FROM    (
		        SELECT  TEMP.STACK_BED_ABLE_QNTY,    --적치 BED 가능 수량
		                TEMP.STACK_BED_ABLE_WT,      --적치 BED 가능 중량
		                TEMP.STACK_BED_ABLE_HIGH,    --적치 BED 가능 높이
		                TEMP.STACK_BED_ABLE_W,       --적치 BED 가능 폭
		                TEMP.STACK_BED_ABLE_LEN,     --적치 BED 가능 길이
		                LAYER.STACK_COL_GP,
		                LAYER.STACK_BED_GP,
		                LAYER.STACK_LAYER_GP,
		                DECODE(LAYER.STACK_LAYER_GP - 1,
		                    0, '01', 
		                    DECODE(LAYER.STACK_LAYER_GP - 1, 
		                        9, '0' || TO_CHAR(LAYER.STACK_LAYER_GP - 1),
		                        DECODE(LENGTH(LAYER.STACK_LAYER_GP - 1),
		                            1, '0' || TO_CHAR(LAYER.STACK_LAYER_GP - 1),
		                            TO_CHAR(LAYER.STACK_LAYER_GP - 1)))) AS PRE_LAYER_GP
		        FROM    TEMP,
		                TB_YM_STACKLAYER LAYER
		        WHERE   TEMP.STACK_COL_GP = LAYER.STACK_COL_GP
		        AND     TEMP.STACK_BED_GP = LAYER.STACK_BED_GP
		        AND     LAYER.STACK_LAYER_STAT        = 'E'
		        AND     LAYER.STACK_LAYER_ACTIVE_STAT = 'O'
		        ) CUR,
		        TB_YM_STACKLAYER STACKLAYER
		WHERE   CUR.STACK_COL_GP = STACKLAYER.STACK_COL_GP
		AND     CUR.STACK_BED_GP = STACKLAYER.STACK_BED_GP
		AND     CUR.PRE_LAYER_GP = STACKLAYER.STACK_LAYER_GP
		AND     STACKLAYER.STACK_LAYER_STAT NOT IN('S', 'U')
		ORDER BY STACK_COL_GP, STACK_BED_GP, STACK_LAYER_GP
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ colGp, bedGp }); 
    }
	/**
     * 적치 가능한 TO 위치를 리턴한다.
     * @param stockId	저장품ID
     * @param colGp		적치열
     * @return
     */
    public List testToLocDefine(String stockId, String colGp) {
        String queryCode = "ym.common.dao.selectToLocDefineTest";
        return super.findList(queryCode, new Object[]{ stockId, colGp }); 
    }
        
    /**
     * 작업예약ID에 따른 SLAB 장입순서를 고려한 주작업 대상을  리턴한다. 
     * @param wbookId	작업예약ID 
     * @param item		저장품품목
     * @return 
     * @throws
     */
	public List readMainLoadCSWorkInfo(String wbookId, String item) {
		String  queryCode = "ym.common.dao.selectMainLoadCSWorkQuntity";		 
		return super.findList(queryCode, new Object[]{ wbookId, item });
	}

    /**
     * SLAB 작업예약ID에 따른 SLAB 야드맵을 고려한 주작업 대상을 리턴한다. 
     * @param wbookId	작업예약ID 
     * @param item		저장품품목
     * @return 
     * @throws
     */
	public List readMainCSWorkInfo(String wbookId, String item) {
		String  queryCode = "ym.common.dao.selectMainCSWorkQuntity";		 
		return super.findList(queryCode, new Object[]{ wbookId, item });
	}

    /**
     * 작업예약 정보를 리턴한다.
     * @param wbookId	작업예약ID
     * @return
     */
    public JDTORecord readWBookInfo(String wbookId) {
    	/**
		SELECT  WBOOK_ID,  --작업예약 ID
		             YD_GP,         --YARD 구분
		             BAY_GP,        --동 구분
		             SCH_WORK_KIND, --SCHEDULE 작업 종류
		             SCH_WORK_LOC_DECISION_METHOD, --SCHEDULE 작업 위치 결정 방법
		             CRANE_WORD_PUT_LOC,  --CRANE 작업지시 PUT 위치
		             WBOOK_DDTT, --작업예약 일시
		             WBOOK_DUTY,
		             WBOOK_PARTY, --작업예약 조
		             WBOOK_SCH_TERM, --작업예약 SCHEDULE 조건
		             WBOOK_SCH_ACT_DDTT, --작업예약 SCHEDULE 실행 일시
		             FRTOMOVE_EQUIP_GP, --이송 설비 구분
		             CTS_RELAY_YN,    --CTS 중계 유무
		             CTS_RELAY_BAY,   --CTS 중계 동
		             CARUNLOAD_BAY, --하차 동
		             REGISTER,	         --등록자
		             REG_DDTT,	 --등록 일시
		             MODIFIER,     --수정자
		             MOD_DDTT,  --수정 일시
		             DEL_YN --삭제 유무
		FROM    TB_YM_WBOOK
		WHERE   WBOOK_ID = ?
    	 */
		String  queryCode = "ym.common.dao.selectWBookInfo";	
		return super.findByPrimaryKey(queryCode, new Object[]{ wbookId });        
    }
    
    /**
     * SLAB 주작업에 대한 보조작업 대상을 리턴한다.   
     * @param mainStockId 주작업 저장품ID
     * @return  
     */
    public List readSubCSWorkInfo(String mainStockId) {
		String  queryCode = "ym.common.dao.selectSubCSWorkQuntity";
		/*
		--보조작업검색
		WITH TEMP AS (
		    SELECT  LAYER.STOCK_ID    MAIN_STOCK_ID,   --주작업 저장품 ID
		            LAYER.STACK_COL_GP,                --적치 열 구분
		            LAYER.STACK_BED_GP,                --적치 BED 구분
		            LAYER.STACK_LAYER_GP,              --적치 단 구분
		            WBOOK.WBOOK_ID,                     --작업예약 ID
		            WBOOK.SCH_WORK_LOC_DECISION_METHOD, --SCHEDULE 작업 위치 결정 방법
		            WBOOK.WBOOK_SCH_ACT_DDTT,           --작업예약 SCHEDULE 실행 일시
		            WBOOK.WBOOK_DDTT,                   --작업예약 일시
		            WBOOK.WBOOK_DUTY,					--작업예약 근            
		            WBOOK.WBOOK_PARTY                   --작업예약 조
		    FROM    TB_YM_STACKLAYER    LAYER,
		            TB_YM_STOCK         STOCK,
		            TB_YM_WBOOK         WBOOK
		    WHERE   LAYER.STOCK_ID          = ?
		    AND     LAYER.STOCK_ID          = STOCK.STOCK_ID
		    AND     LAYER.STACK_LAYER_STAT  = 'S'
		    AND     STOCK.WBOOK_ID          = WBOOK.WBOOK_ID
		)
		SELECT  TEMP.MAIN_STOCK_ID,     --주작업 저장품 ID
		        TEMP.WBOOK_ID,          --작업예약 ID
		        TEMP.SCH_WORK_LOC_DECISION_METHOD,  --SCHEDULE 작업 위치 결정 방법
		        TEMP.WBOOK_DDTT,                    --작업예약 일시
		        TEMP.WBOOK_DUTY,                    --작업예약 근
		        TEMP.WBOOK_PARTY,                   --작업예약 조
		        TEMP.STACK_COL_GP,                  --적치 열 구분
		        TEMP.STACK_BED_GP,                  --적치 BED 구분
		        STACKLAYER.STOCK_ID,        --보조작업 저장품 ID   
		        STACKLAYER.STACK_LAYER_GP,  --적치 단 구분
		        STACKLAYER.STACK_LAYER_STAT,--적치 단 상태     
		        TO_NUMBER(STOCK.CHARGE_LOT_NO) AS CHARGE_LOT_NO,            --장입 LOT 번호
		        NVL(STACKLAYER.STACK_COL_GP, '')    ||
		        NVL(STACKLAYER.STACK_BED_GP, '') AS GRIP_FROM,
		        NVL(TEMP.STACK_COL_GP, '')    ||
		        NVL(TEMP.STACK_BED_GP, '')    ||
		        NVL(STACKLAYER.STACK_LAYER_GP, '') AS FORM_LOC,
		        STACKCOL.STACK_COL_BED_QNTY,--적치 열 BED 수량
				SLABCOMM.SLAB_T,		--SLAB 두께
		        SLABCOMM.SLAB_W,		--SLAB 폭
		        SLABCOMM.SLAB_LEN,	--SLAB 길이
		        SLABCOMM.SLAB_WT,	--SLAB 중량
		        'SUB' AS IS_MAIN   --MAIN, SUB 구분
		FROM    TEMP,        
		        TB_YM_STACKLAYER    STACKLAYER,
		        TB_YM_STOCK         STOCK,
		        TB_YM_STACKCOL      STACKCOL,
		        TB_PM_SLABCOMM		SLABCOMM        
		WHERE   TEMP.STACK_COL_GP = STACKLAYER.STACK_COL_GP
		AND     TEMP.STACK_BED_GP = STACKLAYER.STACK_BED_GP
		AND     TO_NUMBER(TEMP.STACK_LAYER_GP) < TO_NUMBER(STACKLAYER.STACK_LAYER_GP)
		AND     STACKLAYER.STACK_LAYER_STAT IN ('L', 'S', 'P')
		AND     STACKLAYER.STOCK_ID = STOCK.STOCK_ID
		AND     TEMP.STACK_COL_GP   = STACKCOL.STACK_COL_GP
		AND		STOCK.STOCK_ID      = SLABCOMM.SLAB_NO(+)
		ORDER BY STACKLAYER.STACK_LAYER_GP DESC
		*/
		return super.findList(queryCode, new Object[]{ mainStockId });
    }

    /**
     * AS-IS 설비코드를 TO-BE 설비코드로 변환하여 리턴한다.
     * @param ydGp			야드구분
     * @param reEquipGp		AS-IS 설비코드
     * @return
     */
    public String readEquipGp(String ydGp, String reEquipGp) {
        String queryCode = "ym.common.dao.convertEquipNo";
        /*
        --ym.common.dao.convertEquipNo
	-- 전문설비코드를 신설비코드로 변환
	SELECT  CLASS3_CD   AS EQUIPNO,
	        b.EQUIP_NO  as CRANE_NO
	FROM    TB_CM_CDCLASS3 a, 
	        TB_YM_EQUIP b 
	Where   a.type_cd       = 'YM002'
	AND     a.class1_cd     = 'EQPNO'
	AND     a.class2_cd     = ?
	AND     a.class3_cd     = b.equip_gp
	AND     a.CLASS3_NAME2  = ? 

        */
        JDTORecord equipGp = super.findByPrimaryKey(queryCode, new Object[]{ ydGp, reEquipGp });
        return StringHelper.evl(equipGp.getFieldString("EQUIPNO"), "");
    }
    
    
    /**
     * AS-IS 설비코드를 TO-BE 설비코드로 변환하여 리턴한다.
     * @param ydGp			야드구분
     * @param reEquipGp		AS-IS 설비코드
     * @return
     */
    public String readEquipGpRE(String ydGp, String reEquipGp) {
        String queryCode = "ym.common.dao.convertEquipNoRE";
        /*ym.common.dao.convertEquipNoRE
        -- 전문설비코드를 신설비코드로 변환
        SELECT  CLASS3_NAME2   AS EQUIPNO,
                b.EQUIP_NO  as CRANE_NO
        FROM    TB_CM_CDCLASS3 a, 
                TB_YM_EQUIP b 
        Where   a.type_cd       = 'YM002'
        AND     a.class1_cd     = 'EQPNO'
        AND     a.class3_cd     = b.equip_gp
        AND     a.class2_cd     = ?
        AND     a.CLASS3_CD   = ? 
         */
        JDTORecord equipGp = super.findByPrimaryKey(queryCode, new Object[]{ ydGp, reEquipGp });
        return StringHelper.evl(equipGp.getFieldString("EQUIPNO"), "");
    }
    

    /**
     * 설비테이블의 설비 정보를 리턴한다.
     * @param equipGp	설비구분
     * @return
     */
    public JDTORecord readEquipInfo(String equipGp) {
        String queryCode = "ym.common.dao.selectEquipInfo";
        /*
        SELECT	EQUIP_GP,		--설비 구분
		        YD_GP,			--YARD 구분
		        BAY_GP,			--동 구분
		        EQUIP_KIND,		--설비 종류
		        EQUIP_NO,		--설비 번호
		        EQUIP_NAME,		--설비 명
		        EQUIP_ABB_NAME,	--설비 약어 명
		        PALLET_NO,		--PALLET 번호
		        EQUIP_STAT,		--설비 상태
		        DECODE(EQUIP_STAT, 
		           'C','고장','정상') AS EQUIP_STAT1,
		        DOWN_CD,		--휴지 CODE
		        STACK_MAX_QNTY,		--적재 최대 수량
		        STACK_MAX_WT,		--적재 최대 중량
		        STACK_STAT,		--적재 상태
		        WPROG_STAT,		--작업진행 상태
		        WBOOK_ID,		--작업예약 ID
		        WAIT_STOP_LOC,		--대기 정지 위치
		        CURR_STOP_LOC,		--현재 정지 위치
		        CARLOAD_STOP_LOC,		--상차 정지 위치
		        CARUNLOAD_STOP_LOC,		--하차 정지 위치
		        CARLOAD_ASSIGN_YN,		--상차 지정 구분
		        CARLOAD_SCH_WORK_KIND,		--상차 SCHEDULE 작업 종류
		        CARUNLOAD_ASSIGN_YN,		--하차 지정 구분
		        CARUNLOAD_SCH_WORK_KIND,		--하차 SCHEDULE 작업 종류
		        WORK_MODE,		--작업 MODE
		        HMI_STAT,		--HMI 상태
		        DECODE(HMI_STAT,
		          'C','스케쥴금지','스케쥴사용') AS HMI_STAT1,
		        BACKUP_EQUIP_YN,		--BACKUP 설비 유무
		        BACKUP_EQUIP_KIND,		--BACKUP 설비 종류
		        BACKUP_EQUIP_NO,		--BACKUP 설비 번호
		        CTS_RELAY_YN,		--CTS 중계 구역 사용 유무
		        CTS_RELAY_BAY,		--CTS 중계 구역 동
		        REGISTER,		--등록자
		        REG_DDTT,		--등록 일시
		        MODIFIER,		--수정자
		        MOD_DDTT,		--수정 일시
		        DEL_YN		--삭제 유무
		FROM	TB_YM_EQUIP
		WHERE	EQUIP_GP = ?
        */
        return super.findByPrimaryKey(queryCode, equipGp);
    }

    /**
     * 설비테이블의 정보를 리턴한다.
     * @param ydGp			야드구분
     * @param reCraneNo		AS-IS 설비코드
     * @return
     */
    public JDTORecord readEquipInfo(String ydGp, String reCraneNo) {
        String queryCode = "ym.common.dao.selectEquipInfo";
        /*
        --ym.common.dao.selectEquipInfo
	--설비테이블의 정보를 리턴한다.
	SELECT	EQUIP_GP,		--설비 구분
	        YD_GP,			--YARD 구분
	        BAY_GP,			--동 구분
	        EQUIP_KIND,		--설비 종류
	        EQUIP_NO,		--설비 번호
	        EQUIP_NAME,		--설비 명
	        EQUIP_ABB_NAME,	--설비 약어 명
	        PALLET_NO,		--PALLET 번호
	        EQUIP_STAT,		--설비 상태
	        DECODE(EQUIP_STAT, 
	           'C','고장','정상') AS EQUIP_STAT1,
	        DOWN_CD,		--휴지 CODE
	        STACK_MAX_QNTY,		--적재 최대 수량
	        STACK_MAX_WT,		--적재 최대 중량
	        STACK_STAT,		--적재 상태
	        WPROG_STAT,		--작업진행 상태
	        WBOOK_ID,		--작업예약 ID
	        WAIT_STOP_LOC,		--대기 정지 위치
	        CURR_STOP_LOC,		--현재 정지 위치
	        CARLOAD_STOP_LOC,		--상차 정지 위치
	        CARUNLOAD_STOP_LOC,		--하차 정지 위치
	        CARLOAD_ASSIGN_YN,		--상차 지정 구분
	        CARLOAD_SCH_WORK_KIND,		--상차 SCHEDULE 작업 종류
	        CARUNLOAD_ASSIGN_YN,		--하차 지정 구분
	        CARUNLOAD_SCH_WORK_KIND,		--하차 SCHEDULE 작업 종류
	        WORK_MODE,		--작업 MODE
	        HMI_STAT,		--HMI 상태
	        DECODE(HMI_STAT,
	          'C','스케쥴금지','스케쥴사용') AS HMI_STAT1,
	        BACKUP_EQUIP_YN,		--BACKUP 설비 유무
	        BACKUP_EQUIP_KIND,		--BACKUP 설비 종류
	        BACKUP_EQUIP_NO,		--BACKUP 설비 번호
	        CTS_RELAY_YN,		--CTS 중계 구역 사용 유무
	        CTS_RELAY_BAY,		--CTS 중계 구역 동
	        REGISTER,		--등록자
	        REG_DDTT,		--등록 일시
	        MODIFIER,		--수정자
	        MOD_DDTT,		--수정 일시
	        DEL_YN		--삭제 유무
	FROM	TB_YM_EQUIP
	WHERE	EQUIP_GP = ?
        */
        return super.findByPrimaryKey(queryCode, new Object[]{ readEquipGp(ydGp, reCraneNo) });
    }


    /**
     * 슬라브정보를 리턴한다.
     * @param slabNo	저장품ID
     * @return
     */
    public JDTORecord readSlabInfo(String slabNo) {
        String queryCode = "ym.common.dao.selectSlabInfo";
        /*
         	--ym.common.dao.selectSlabInfo
			WITH TEMP AS (
			    SELECT :SLAB_NO AS SLAB_NO
			    FROM    DUAL
			)
			SELECT  COMM.SLAB_NO,                       --SLAB번호
			        COMM.SLAB_NO AS STL_NO,             --SLAB번호
			        TO_CHAR(SPEC.REFUR_CHG_ABLE_DT, 'YYYYMMDDHH24MISS') 
					AS MILL_PLAN_DDTT,                  -- 압연 예정 일시
			        SPEC.REFUR_CHG_LOT_NO AS LOT_NO,    -- 가열로장입Lot번호
			        SPEC.LOT_IN_SLAB_PRIOR,             -- Lot내Slab순위
			        SPEC.YD_CHG_NO,                     -- 야드장입순위
			        COMM.ORD_YEOJAE_GP,                 --주문여재구분
			        NVL(COMM.ORD_NO, '') ||
			        NVL(COMM.ORD_DTL, '') AS PRODUC_NO, --제작번호행번
			        COMM.SLAB_T,                        --두께
			        COMM.SLAB_W,                        --폭
			        COMM.SLAB_LEN,                      --길이
			        COMM.SLAB_WT,                       --중량
			        COMM.COIL_NO,                       --예정COILNO
			        COMM.STACK_LOT_NO AS STACK_LOT_CD,  --산적 LOT CODE
			        BUYS.BUY_SLAB_NO,                   --구입슬라브번호
			        STOCK.STOCK_ID,
			        STOCK.WBOOK_ID,
			        STOCK.STACK_LOT_NO AS STACK_LOT,
			        STOCK.STOCK_MOVE_TERM,
			        LAYER.STOCK_ID  AS LAYER_STOCK_ID
			FROM    TEMP,
			        TB_YM_STOCK         STOCK,
			        TB_YM_STACKLAYER    LAYER,
			        VW_YD_SLABCOMM      COMM,
			        TB_QM_BUYSLABINFO   BUYS,
			        TB_CT_L_HRMILLWO    SPEC
			WHERE   TEMP.SLAB_NO = COMM.SLAB_NO(+)
			AND     TEMP.SLAB_NO = BUYS.MSLAB_NO(+)
			AND     TEMP.SLAB_NO = STOCK.STOCK_ID(+)
			AND     TEMP.SLAB_NO = LAYER.STOCK_ID(+)
			AND     TEMP.SLAB_NO = SPEC.STL_NO(+)
		*/
        return super.findByPrimaryKey(queryCode, new Object[]{ slabNo });
    }
        
    /**
     * W/B의 MODE를 UPDATE
     * @param mode
     */
    public void modifyWBMode(String mode) {
        String queryCode = "ym.common.dao.updateWBMode";
        /*
        --ym.common.dao.updateWBMode
	--W/B의 HMI MODE를 UPDATE
	UPDATE  TB_YM_EQUIP
	SET        HMI_STAT = ?
	WHERE   EQUIP_GP = '2CWB01'
        */
        super.updateData(queryCode, new Object[]{ mode });        
    }
    
    /**
     * 저장품의 장입순번을 CLEAR 한다.
     */
    public void modifyZoneInNo() {
        String queryCode = "ym.common.dao.readZoneInNoOfStock";
        /*
        --ym.common.dao.readZoneInNoOfStock
	--장입순번이 존재하는 저장품을 리턴한다.
	SELECT  STOCK_ID
	FROM    TB_YM_STOCK
	WHERE   CHARGE_LOT_NO IS NOT NULL
        */
        List stocks = super.findList(queryCode);
        int stocksCnt = stocks != null ? stocks.size() : 0;
        for(int i = 0; i < stocksCnt; i++) {
            modifyZoneInNo(((JDTORecord)stocks.get(i)).getFieldString("STOCK_ID"));
        }
    }
    
    /**
     * 장입예정번호를 UPDATE 
     *
     */
    public void modifyZoneInNo(String stockId) {
        String queryCode = "ym.common.dao.updateZoneInNoOfStock";        
        /*
        --ym.common.dao.updateZoneInNoOfStock
	UPDATE  TB_YM_STOCK
	SET        CHARGE_LOT_NO = NULL
	WHERE   STOCK_ID = ?
        */
        super.updateData(queryCode, new Object[]{ stockId });
    }
    
    public void modifyZoneInNo_01() {
        String queryCode = "ym.common.dao.updateZoneInNoOfStock_01";        
        /*
        --ym.common.dao.updateZoneInNoOfStock_01
		UPDATE  TB_YM_STOCK
		SET     CHARGE_LOT_NO = :CHARGE_LOT_NO
		WHERE   CHARGE_LOT_NO IS NOT NULL
        */
        super.updateData(queryCode, new Object[]{ "" });
    }
    
    /**
     * 확장대차를 B동으로 초기화
     * @param equipGp	설비구분
     */
    public void modifyInitialOfVicCar(String equipGp) {
        String queryCode = "ym.common.dao.updateInitialOfVicCar";
        /*
        --ym.common.dao.updateInitialOfVicCar
	--확장대차를 B동으로 초기화
	UPDATE  TB_YM_EQUIP
	SET        STACK_STAT  = 'L',
	              WPROG_STAT = 'W',
	              CURR_STOP_LOC = '1BTC03',
	              CARLOAD_STOP_LOC = '1BTC03',
	              CARUNLOAD_STOP_LOC = '1ATC03'
	WHERE    EQUIP_GP = ?
        */
        super.updateData(queryCode, new Object[]{ equipGp });        
    }
    
    /**
     * HISCO 대차의 상차 스케쥴 정보를 UPDATE
     * @param mode		설비 HMI MODE
     * @param schKind	스케쥴종류
     * @param equipGp	설비구분
     */
    public void modifyLoadSchOfVicCar(String mode, String schKind, String equipGp) {
        String queryCode = "ym.common.dao.updateLoadSchOfVicCar";
        /*
        --ym.common.dao.updateLoadSchOfVicCar
	--HISCO 대차의 상차 스케쥴 정보를 UPDATE 
	UPDATE  TB_YM_EQUIP
	SET        CARLOAD_ASSIGN_YN = 'Y',
	              HMI_STAT = ?,
	              CARLOAD_SCH_WORK_KIND = ?
	WHERE   EQUIP_GP = ?
	              
        */
        super.updateData(queryCode, new Object[]{ mode, schKind, equipGp });        
    }

    /**
     * 적치대의 최대 맥스, 현재 매수를 UPDATE 한다. 
     * @param workStat		작업상태
     * @param whrStockId	슬라브번호
     */
    public void YJK_DEL_modifyPcWorkStat(String workStat, String whrStockId) {
        String queryCode = "ym.common.dao.updatePcWorkStat";
        super.updateData(queryCode, new Object[]{ workStat, whrStockId });        
    }

    /**
     * 적치대의 최대 맥스, 현재 매수를 UPDATE 한다. 
     * @param max		BED 최대수량
     * @param cur		BED 현재수량
     * @param whrCol	적치열
     * @param whrBed	적치번지
     */
    public void modifyPossibleAndCurrCntOfStacker(
            String max, String cur, String whrCol, String whrBed) {
    	/*UPDATE  TB_YM_STACKER
			 SET  STACK_BED_QNTY_MAX    = ?,
			      STACK_BED_QNTY_CURR  = ?
		  WHERE   STACK_COL_GP = ?
		  AND     STACK_BED_GP = ?
		*/
        String queryCode = "ym.common.dao.updatePossibleAndCurrCntOfStacker";
        super.updateData(queryCode, new Object[]{ max, cur, whrCol, whrBed });        
    }

    /**
     * 적치대의 최대 맥스, 현재 매수를 UPDATE 한다. 
     * @param max		BED 최대수량
     * @param cur		BED 현재수량
     * @param whrCol	적치열
     * @param whrBed	적치번지
     */
    public void modifyXYLocOfLayer(
            String xLoc, String yLoc, String whrCol, String whrBed, String whrLayer) {
        String queryCode = "ym.common.dao.updatePossibleAndCurrCntOfStacker";
        /*
        --ym.common.dao.updatePossibleAndCurrCntOfStacker
	--적치대의 최대 맥스, 현재 매수를 UPDATE 한다.
	UPDATE  TB_YM_STACKER
	SET        STACK_BED_QNTY_MAX    = ?,
	              STACK_BED_QNTY_CURR  = ?
	WHERE    STACK_COL_GP = ?
	AND        STACK_BED_GP = ?
        */
        super.updateData(queryCode, new Object[]{ xLoc, yLoc, whrCol, whrBed, whrLayer });        
    }

    /**
     * 설비상태를 UPDATE
     * @param equipStat		설비상태
     * @param whrEuqipGp	설비구분
     */
    public void modifyEquipStatOfEquip(String equipStat, String whrEuqipGp){
    	/*
		UPDATE  TB_YM_EQUIP
		SET     EQUIP_STAT = ?
		WHERE   EQUIP_GP   = ?
		*/
        String queryCode = "ym.common.dao.updateEquipStatOfEquip";
        super.updateData(queryCode, new Object[]{ equipStat, whrEuqipGp });
    }

    /**
     * 설비상태를 A열연 ROT UPDATE(MCH)
     * @param equipStat		설비상태
     * @param whrEuqipGp	설비구분
     */
    public void modifyEquipStatOfRotEquip(String equipStat, String whrEuqipGp){
    	/*
		UPDATE  TB_YM_EQUIP A
   		   SET  A.EQUIP_STAT = DECODE(?,'O',DECODE(A.EQUIP_STAT,'C','C','O'),?)
	     WHERE   A.EQUIP_GP   = ?
		*/
        String queryCode = "ym.common.dao.updateEquipStatOfRotEquip";
        super.updateData(queryCode, new Object[]{ equipStat, equipStat, whrEuqipGp });
    }
    
    /**
     * 설비 테이블의 '설비상태', '휴지 CODE', '수정자'를 UPDATE
     * @param wprogStat		작업상태
     * @param whrEuqipGp	설비 구분
     * @return
     */
    public void modifyWprogStatOfEquip(String wprogStat, String whrEuqipGp){
        String queryCode = "ym.common.dao.updateWprogStatOfEquip";
        /*
        --ym.common.dao.updateWprogStatOfEquip
	--설비의 작업상태를 UPDATE 한다.
	UPDATE  TB_YM_EQUIP
	SET        WPROG_STAT = ?
	WHERE   EQUIP_GP       = ?
        */
        super.updateData(queryCode, new Object[]{ wprogStat, whrEuqipGp });
    }

    /**
     * 저장품 테이블의  '저장품상태','저장품이동조건'을 UPDATE
     * @param term			저장품이동조건
     * @param whrStockId 	저장품ID
     */
    public void modifyStockTermOfStock(String term, String whrStockId) {
    	/*
		UPDATE  TB_YM_STOCK
		SET     STOCK_MOVE_TERM = ?
		WHERE   STOCK_ID        = ?
		*/
        String qId = "ym.common.dao.updateStockStatOfStock";
        super.updateData(qId, new Object[]{ term, whrStockId });        
    }

    /**
     * 슬라브 공통 테이블을 UPDATE
     * @param ymd		부두 YARD 반출 일자
     * @param hms		부두 YARD 반출 시각	
     * @param whrStockId 슬라브번호
     */
    public void modifyLieTakeInTimeOfSlabComm(String ymd, String hms, String whrStockId) {
        String qId = "ym.common.dao.updateLieTakeInTimeOfSlabComm";
        /*
        --ym.common.dao.updateLieTakeInTimeOfSlabComm
	--슬라브 공통 테이블 '부두 YARD 반출 일자', '부두 YARD 반출 시각' UPDATE
	UPDATE  TB_PM_SLABCOMM
	SET        PORT_YD_TAKEIN_DATE = ?,
	             PORT_YD_TAKEIN_TIME  = ?,
	             SHIPUNLOADING_DATE   = PORT_YD_TAKEIN_DATE
	WHERE   SLAB_NO = ?
        */
        super.updateData(qId, new Object[]{ ymd, hms, whrStockId });        
    }
    
    /**
     * 슬라브 공통 테이블을 UPDATE
     * @param ymd		부두 YARD 반출 일자
     * @param hms		부두 YARD 반출 시각	
     * @param whrStockId 슬라브번호
     */
    public void modifyLieTakeOutTimeOfSlabComm(String ymd, String hms, String whrStockId) {
        String qId = "ym.common.dao.updateLieTakeOutTimeOfSlabComm";
        /*
        --ym.common.dao.updateLieTakeOutTimeOfSlabComm
	--슬라브 공통 테이블 '부두 YARD 반출 일자', '부두 YARD 반출 시각' UPDATE
	UPDATE  TB_PM_SLABCOMM
	SET        PORT_YD_TAKEOUT_DATE = ?,
	             PORT_YD_TAKEOUT_TIME  = ?
	WHERE   SLAB_NO = ?
        */
        super.updateData(qId, new Object[]{ ymd, hms, whrStockId });        
    }

    /**
     * 슬라브 공통 테이블을 UPDATE
     * @param yd		야드구분
     * @param bay		동구분
     * @param span		스판구분
     * @param col		적치열구분
     * @param bed		적치 번지
     * @param layer		적치단
     * @param storeLoc	저장위치
     * @param whrStockId 슬라브번호
     */
    public void modifyStoreLocOfSlabComm(
            String yd, String bay, String span, String col, String bed, String layer, 
            String storeLoc, String whrStockId) {
        String qId = "ym.common.dao.updateStoreLocOfSlabComm";
        /*
        --ym.common.dao.updateStoreLocOfSlabComm
	--부두야드 입고시 슬라브 공통에 저장위치를 UPDATE
	UPDATE  TB_PM_SLABCOMM
	SET        YD_GP              = ?,
	              BAY                  = ?,
	              SPAN                = ?,
	              COL                  = ?,
	              CELLNO             = ?,
	              STACK_LAYER    = ?,
	              STORE_LOC_CD = ?
	WHERE    SLAB_NO = ?

        */
        super.updateData(qId, new Object[]{ yd, bay, span, col, bed, layer, storeLoc, whrStockId });
    }
    
    public void modifyYdMap(
            String actStat, String stockId, String whrCol, String whrBed, String whrLayer) {
        String qId = "ym.common.dao.updateYdMap";
        super.updateData(qId, new Object[]{ actStat, stockId, whrCol, whrBed, whrLayer });        
    }
    
    /**
     * 저장품의 이동조건 상차완료, 작업예약 ID UPDATE
     * @param wbooId		작업예약 ID
     * @param stat			저장품상태
     * @param term			저장품이동조건
     * @param whrStockId	저장품 ID
     */
    public void modifyMoveTermAndWBookOfStock(String wbooId, String term, String whrStockId) {
		/*
		UPDATE  TB_YM_STOCK
		SET     WBOOK_ID    	= ?,
		        STOCK_MOVE_TERM = ?
		WHERE   STOCK_ID = ?
		*/    	
        String qId = "ym.common.dao.updateMoveTermAndWBookOfStock";
        super.updateData(qId, new Object[]{ wbooId, term, whrStockId });        
    }

    /**
     * 설비테이블의 작업예약ID, 상차SHC을 UPDATE
     * @param ymdhhmm	현재 년월일시분
     * @param sch		스케쥴코드
     * @param loc		설비구분
     */
    public void modifyWBookAndLoadSchOfEquip(String ymdhhmm, String sch, String loc) {
        /*UPDATE  TB_YM_EQUIP
			 SET  WBOOK_ID = ?,
		          CARLOAD_SCH_WORK_KIND = ?
		   WHERE  EQUIP_GP = ?
		*/
        String qId = "ym.common.dao.updateWBookAndLoadSchOfEquip";    	
        super.updateData(qId, new Object[]{ ymdhhmm, sch, loc });
    }
    
    /**
     * 차량 하차시 관련정보를 저장품에서 CLEAR 한다.
     * @param gp			이송 설비 구분	
     * @param bed			이송 설비 BED 구분
     * @param layer			이송 설비 단 구분
     * @param cardNo		차량 CARD 번호
     * @param whrStockId	저장품 ID
     * PALLET_NO의 값을 SHEAR_SUPPLY_DEMAND_DDTT에 임시로 저장하기 때문에 삭제(MCH)
     */
    public void modifyUnloadInfoOfStock(String gp,				//이송 설비 구분 
							    		String bed,				//이송 설비 BED 구분 
							    		String layer,			//이송 설비 단 구분 
							            String ordDate, 
							            String ordNo, 
							            String cardNo,			//차량 CARD 번호 
							            String whrStockId) {	//저장품 ID
		     	
        String qId = "ym.common.dao.updateUnloadInfoOfStock";
        super.updateData(qId, new Object[]{ gp, bed, layer, ordDate, ordNo, cardNo, whrStockId });        
    }
    
    /**
     * 출하 상차지시에 따른 '작업예약ID','저장품이동조건','이송상차지시번호','차량카드 번호'를 UPDATE
     * @param wbookId		작업예약ID
     * @param stat			저장품상태
     * @param term			저장품이동조건
     * @param loadOrdNo		이송상차지시번호
     * @param cardNo		차량카드번호
     * @param whrStockId	저장품ID
     */
    public void modifyMoveLoadOrderOfStock( String wbookId, 
								    		String term, 
								    		String loadOrdNo, 
								    		String cardNo, 
								    		String whrStockId) {
    	 
        String qId = "ym.common.dao.updateMoveLoadOrderOfStock";
        super.updateData(qId, new Object[]{ wbookId, term, loadOrdNo, cardNo, whrStockId });
    }

    /**
     * 출하 상차 완료에 따른 '저장품이동조건','이송상차지시번호','차량카드 번호'를 UPDATE(MCH)
     * @param term			저장품이동조건
     * @param loadOrdNo		이송상차지시번호
     * @param cardNo		차량카드번호
     * @param whrStockId	저장품ID
     */
    public void modifyMoveCompletionOfStock(String term, 
								    		String loadOrdNo, 
								    		String cardNo, 
								    		String whrStockId) {
    	 
        String qId = "ym.common.dao.updateMoveCompletionOfStock";
        super.updateData(qId, new Object[]{term, loadOrdNo, cardNo, whrStockId });
    }
    /**
     * 차량 출발에 따른 저장품 테이블 카드번호 Clear
     * @param cardNo		차량카드번호
     */
    public void modifyStartClearOfStock(String cardNo) {
        String qId = "ym.common.dao.updateStartClearOfStock";
        /*
        UPDATE TB_YM_STOCK
	SET STOCK_MOVE_TERM = '',
	FRTOMOVE_EQUIP_GP = '',
	FRTOMOVE_EQUIP_BED_GP = '',
	FRTOMOVE_EQUIP_LAYER_GP = '',
	CAR_CARD_NO = ''
	WHERE CAR_CARD_NO = ?
        */
        super.updateData(qId, new Object[]{ cardNo });
    }
    
    /**
     * 차량 출발에 따른 StackCol 카드번호 Clear
     * @param cardNo		차량카드번호
     */
    public void modifyStartClearOfStackCol(String cardNo) {
        String qId = "ym.common.dao.updateStartClearOfStackCol";
        /*
        UPDATE TB_YM_STACKCOL
	SET CAR_CARD_NO = ''
	WHERE CAR_CARD_NO = ?
        */
        super.updateData(qId, new Object[]{ cardNo });
    }
    
    /**
     * 차량 출발에 따른 '저장품이동조건','이송설비구분','이송 설비 BED 구분','이송 설비 단'를 UPDATE
     * @param term			저장품이동조건
     * @param layer		이송상차지시번호
     * @param cardNo		차량카드번호
     * @param StockId	저장품ID
     */
    public void modifyStartOfStock(
            String term, String layer, String loadOrdNo, String cardNo,String paletteNo, String StockId) {
        String qId = "ym.common.dao.updateStartOfStock";        
        super.updateData(qId, new Object[]{ term, layer, loadOrdNo, cardNo,paletteNo, StockId });
    }
    
    /**
     * 저장품 테이블의 삭제유무를 UPDATE
     * @param deleteYN		삭제유무
     * @param whrStockId	저장품ID
     */
    public void modifyDeleteMarkOfStock(String deleteYN, String whrStockId) {
        String qId = "ym.common.dao.updateDeleteMarkOfStock";
         
        super.updateData(qId, new Object[]{ deleteYN, whrStockId });
    }
    
    /**
     * 저장품 테이블의 Card_no Clear
     * @param cardNo		카드번호 
     * 
     */
    public void modifyStockCardNo(String cardNo) {
    	 
        String qId = "ym.common.dao.updateCardOfStock";
        super.updateData(qId, new Object[]{ cardNo });
    }
    
    /**
     * 검색열의 Card_no Clear
     * @param cardNo		카드번호 
     * 
     */
    public void modifyStackcolCardNo(String cardNo) {
    	/*
		UPDATE TB_YM_STACKCOL
		SET CAR_CARD_NO = NULL
		WHERE CAR_CARD_NO = ?
    	*/
        String qId = "ym.common.dao.updateCardOfStackcol";
        super.updateData(qId, new Object[]{ cardNo });
    }
    
    /**
     * 작업예약 테이블의 '스케쥴지정방법', 'TO위치'를 UPDATE
     * @param schDec	스케쥴지정방법
     * @param loc			TO위치
     * @param whrWBookId	작업예약ID
     */
    public void modifyOperatorOfWBook(String schDec, String loc, String whrWBookId) {
        String qId = "ym.common.dao.updateOperatorOfWBook";
        /*
        --ym.common.dao.updateOperatorOfWBook
	--작업예약을 오퍼레이터 지정으로 UPDATE
	UPDATE  TB_YM_WBOOK
	SET     SCH_WORK_LOC_DECISION_METHOD = ?,
	        CRANE_WORD_PUT_LOC           = ?
	WHERE   WBOOK_ID                     = ?
        */
        super.updateData(qId, new Object[]{ schDec, loc, whrWBookId });
    }
    
    /**
     * 작업예약 테이블의 '스케쥴지정방법', 'TO위치'를 UPDATE
     * @param schDec	스케쥴지정방법
     * @param loc			TO위치
     * @param whrWBookId	작업예약ID
     */
    public void modifyOperatorOfWBook(String kind, String schDec, String loc, String whrWBookId) {
        /*
		--작업예약을 오퍼레이터 지정으로 UPDATE
		UPDATE  TB_YM_WBOOK        
		SET     SCH_WORK_KIND = ?,
		        SCH_WORK_LOC_DECISION_METHOD = ?,
		        CRANE_WORD_PUT_LOC           = ?
		WHERE   WBOOK_ID                     = ?
		*/
        String qId = "ym.common.dao.updateOperatorOfWBook1";    	
        super.updateData(qId, new Object[]{ kind, schDec, loc, whrWBookId });
    }

    /**
     * 적치열 테이블의 '차량 CARD 번호' UPDATE
     * @param cardNo	차량 CARD 번호
     */
    public void modifyCardNoOfStackCol(String cardNo) {
        String qId = "ym.common.dao.updateCardNoOfStackCol1";
        /*
        --ym.common.dao.updateCardNoOfStackCol1
	--차량의 카드번호를 UPDATE
	UPDATE  TB_YM_STACKCOL 
	SET     CAR_CARD_NO = NULL
	WHERE   CAR_CARD_NO = ?
        */
        super.updateData(qId, new Object[]{ cardNo });                
    }
    
    /**
     * 적치열 테이블의 '차량 CARD 번호' UPDATE
     * @param cardNo	차량 CARD 번호
     * @param whrCol	적치열
     */
    public void modifyCardNoOfStackCol(String cardNo, String whrCol) {
        String qId = "ym.common.dao.updateCardNoOfStackCol";
        /*UPDATE  TB_YM_STACKCOL 
		  SET     CAR_CARD_NO = ?
		  WHERE   STACK_COL_GP = ?
		*/
        super.updateData(qId, new Object[]{ cardNo, whrCol });                
    }
    
    /**
     * 적치열 테이블의 '차량 CARD 번호' UPDATE
     * @param cardNo	차량 CARD 번호
     * @param whrCol	적치열
     */
    public void modifyCardNoOfStackCol2(String cardNo ) {
        String qId = "ym.common.dao.updateCardNoOfStackCol2";
        /*UPDATE  TB_YM_STACKCOL 
		  SET     CAR_CARD_NO =NULL
		  WHERE   CAR_CARD_NO = ?
		*/
        super.updateData(qId, new Object[]{ cardNo });                
    }
    
    /**
     * 차량스케줄  테이블의 '차량 CARD 번호' UPDATE
     * @param cardNo	차량 CARD 번호
     * @param whrCol	적치열
     */
    public void modifyCardNoOfEND(String cardNo ) {
        String qId = "ym.common.dao.modifyCardNoOfEND";
        super.updateData(qId, new Object[]{ cardNo });                
    }
    
    
    /**
     * 차량스케줄  테이블의 '차량 CARD 번호' UPDATE
     * @param cardNo	차량 CARD 번호
     * @param whrCol	적치열
     */
    public void modifyCardNoOfDetailEND(String cardNo ) {
        String qId = "ym.common.dao.modifyCardNoOfDetailEND";
        super.updateData(qId, new Object[]{ cardNo });                
    }
    

    /**
     * 내부 인터페이스 전문에 대한 송수신 LOG 기록을 UPDATE
     * @param type		TC 발생 유형[L:Log, W:Warning, E:Error]
     * @param msg		TC 발생 유형 내용
     * @param whrTcCode	TC CODE
     */
    public void modifyLog(String type, String msg, String whrTcCode) {
        String qId = "ym.common.dao.updateLog";
        /*
        --ym.common.dao.updateLog
	--에러 로그를 UPDATE
	UPDATE  TB_YM_IFTCLOG
	SET     TC_OCCUR_TYPE   = ?, --TC 발생 유형
	        TC_OCCUR_MSG    = SUBSTR(?,0,89)  --TC 발생 MESSAGE
	WHERE TC_LOG_ID   = 
	  (
	  SELECT  MAX(TC_LOG_ID)
	  FROM     TB_YM_IFTCLOG
	  WHERE   TC_CD = ?
	  )
        */
        super.updateData(qId, new Object[]{ type, msg, whrTcCode });        
    }
    
    /**
     * 내부 인터페이스 전문에 대한 송수신 LOG 기록을 UPDATE
     * @param reSendCnt		TC 재기동 횟수
     * @param msg			TC 발생 유형 내용
     * @param whrTcLogId	TC LOG ID
     */
    public void modifyLogOfReSend(String reSendCnt, String msg, String whrTcLogId) {
        String qId = "ym.common.dao.updateLogOfReSend";
        /*
        --ym.common.dao.updateLogOfReSend
	--에러 로그를 UPDATE
	UPDATE  TB_YM_IFTCLOG
	SET     TC_RESTART_COUNT     = ?, --TC 재기동 횟수
	           TC_OCCUR_CONTENTS = ?  --TC 발생 MESSAGE
	WHERE   TC_LOG_ID                = ?
        */
        super.updateData(qId, new Object[]{ reSendCnt, msg, whrTcLogId });        
    }

    /**
     * 적치단 테이블의 '적치상태'를 UPDATE
     * @param layerStat	적치상태
     * @param whrCol		적치열
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyLayerStatOfLayer(String layerStat, String whrCol, String whrStockId) {
    	/*
		UPDATE  TB_YM_STACKLAYER
		SET        STACK_LAYER_STAT = ?
		WHERE   STACK_COL_GP = ?
		AND       STOCK_ID         = ?
		*/
        String qcd = "ym.common.dao.updateLayerState1";
        super.updateData(qcd, new Object[]{ layerStat, whrCol, whrStockId });
    }

    /**
     * 저장품 테이블의 '작업예약ID'를 UPDATE
     * @param wbookId		작업예약ID
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyWbookIdOfStock(String wbookId, String whrStockId) {
        String qcd = "ym.common.dao.updateWbookIdOfStock";
        /*
        --ym.common.dao.updateWbookIdOfStock
	--'작업예약ID'를 UPDATE
	UPDATE  TB_YM_STOCK
	SET        WBOOK_ID = ?
	WHERE   STOCK_ID  = ?
        */
        super.updateData(qcd, new Object[]{ wbookId, whrStockId });
    }
    
    /**
     * 저장품 테이블의 '작업예약ID'를 UPDATE
     * @param wbookId		작업예약ID
     * @param term			저장품이동조건
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyWbookIdOfStock(String wbookId, String term, String whrStockId) {
        String qcd = "ym.common.dao.updateWbookIdOfStock1";
        /*
        --ym.common.dao.updateWbookIdOfStock1
	--작업예약ID, 저장품이동조건을 UPDATE
	UPDATE  TB_YM_STOCK
	SET        WBOOK_ID = ?,
	             STOCK_MOVE_TERM = ?
	WHERE   STOCK_ID  = ?
        */
        super.updateData(qcd, new Object[]{ wbookId, term, whrStockId });
    }

    /**
     * 설비 테이블의 '작업모드', '적재상태', '중계구역구분'을 UPDATE
     * @param mode			작업모드
     * @param coilYN		적재상태
     * @param useYN			중계구역구분
     * @param whrEquipGp	설비구분
     */
    public void modifyCTSStatusOfEquip(String mode, String coilYN, String useYN, String whrEquipGp){
        String queryCode = "ym.common.dao.updateCTSStatusOfEquip";
        /*
        --ym.common.dao.updateCTSStatusOfEquip
	--CTS 정보를 UPDATE
	UPDATE  TB_YM_EQUIP
	SET     EQUIP_STAT  = DECODE(?, '9', 'C', 'O'),
	        --WORK_MODE = DECODE(?, '1', 'O', '2', 'C', 'O'),
	        STACK_STAT = DECODE(?, '1', 'L', 'U'),
	        CTS_RELAY_YN = DECODE(?, '3', 'Y', 'N')
	WHERE   EQUIP_GP     = ?
        */
        super.updateData(queryCode, new Object[]{ mode, coilYN, useYN, whrEquipGp });
    }

    /**
     * 설비 테이블의 '작업모드', '적재상태', '중계구역구분', '코일번호'을 UPDATE
     * @param mode		작업모드
     * @param coilYN	적재상태
     * @param useYN		중계구역구분
     * @param coilNo	코일번호
     * @param equipGp	설비구분
     */
    public void modifyCTSStatusOfEquip(
            String mode, String coilYN, String useYN, String coilNo, String whrEquipGp) {
        String queryCode = "ym.common.dao.updateCTSStatusOfEquip1";
        /*
        --ym.common.dao.updateCTSStatusOfEquip1
	--CTS 정보를 UPDATE
	UPDATE  TB_YM_EQUIP
	SET        EQUIP_STAT  = DECODE(?, '9', 'C', 'O'),
	              --WORK_MODE = DECODE(?, '1', 'O', '2', 'C', 'C'),
	              STACK_STAT = DECODE(?, '1', 'L', 'U'),
	              CTS_RELAY_YN = DECODE(?, '3', 'Y', 'N'),
	              WBOOK_ID = ?
	WHERE   EQUIP_GP     = ?
        */
        super.updateData(queryCode, new Object[]{ mode, coilYN, useYN, coilNo, whrEquipGp });
    }

    /**
     * 복구할 스케쥴기준 테이블 UPDATE
     * @param activeStat	스케쥴기준 활성상태
     * @param whrRuleId		스케쥴기준ID
     * @return
     */
    public void modifyActiveStatOfSchRule(String activeStat, String whrRuleId) {
        String queryCode = "ym.common.dao.updateRecSchRule";
        /*
        --ym.common.dao.updateRecSchRule
	--복구할 스케쥴기준 UPDATE
	UPDATE  TB_YM_SCHRULE
	SET        SCH_RULE_ACTIVE_STAT = ?
	WHERE   SCH_RULE_ID = ?
        */
        super.updateData(queryCode, new Object[]{ activeStat, whrRuleId });
    }

    /**
     * 백업/복구할 스케쥴테이블 UPDATE
     * @param crNo		크레인번호
     * @param stat		스케쥴작업상태
     * @param priority	우선순위
     * @param whrSchId	스케쥴ID
     */
    public void modifyTroRecOfSchedule(String crNo, String stat, String priority, String whrSchId) {
	/*
	UPDATE  TB_YM_SCH
	SET        SCH_WORK_EQUIP_NO = ?,  --SCHEDULE 작업 설비 번호
	              SCH_WORK_STAT        = ?,
	              SCH_WPREFER             = ?,  --SCHEDULE 작업우선순위
	              MOD_DDTT                  = SYSDATE
	WHERE    SCH_ID = ?
	*/    	
        String queryCode = "ym.common.dao.updateTroRecOfSchedule";
        super.updateData(queryCode, new Object[]{ crNo, stat, priority, whrSchId });
    }

    /**
     * 설비휴지테이블의 '휴지 CODE', '휴지 종료 일시'를 UPDATE
     * @param downCd		휴지 CODE
     * @param modifier		수정자
     * @param whrEquipGp	설비구분
     * @return
     */
    public void modifyDownCdOfEquipDown(
            String downCd, String duty, String party, String whrEquipGp) {
        String queryCode = "ym.common.dao.updateDownCdOfEquipDown";
        /*
        --ym.common.dao.updateDownCdOfEquipDown
	--설비휴지 고장복구 UPDATE
	UPDATE  TB_YM_EQUIPDOWN
	SET        DOWN_CD = ?,
	             DOWN_END_WORK_DUTY  = ?,
	             DOWN_END_WORK_PARTY = ?,
	             DOWN_END_DDTT = TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),
	             MODIFIER   = 'SYSTEM',             
	             MOD_DDTT = SYSDATE
	WHERE   EQUIP_GP= ?
	AND     DOWN_OCCUR_SEQ = 
	    (
	        SELECT  MAX(DOWN_OCCUR_SEQ)
	        FROM    TB_YM_EQUIPDOWN
	        WHERE   EQUIP_GP= ?
	    )
        */
        super.updateData(queryCode, new Object[]{ downCd, duty, party, whrEquipGp, whrEquipGp });
    }

    /**
     * 백업할 스케쥴기준 UPDATE  
     * @param equipNo		설비구분
     * @param stat	SCHEDULE 기준 활성 상태
     * @param ruleId	스케쥴기준ID
     * @return
     */
    public void modifyAlterNoAndActiveStatOfSchRule(String equipNo, String stat, String ruleId) {
        String queryCode = "ym.common.dao.updateAlterNoAndActiveStatOfSchRule";
        /*UPDATE  TB_YM_SCHRULE
			SET        SCH_RULE_ALTER_CRANE_NO = ?, --SCHEDULE 기준 대체 CRANE 번호
			              SCH_RULE_ACTIVE_STAT       = ?, --SCHEDULE 기준 활성 상태
			              MOD_DDTT = SYSDATE
			WHERE    SCH_RULE_ID = ?
		*/
        super.updateData(queryCode, new Object[]{ equipNo, stat, ruleId });
    }

    /**
     * 공정 이송지시에 따른 '저장품상태','저장품이동조건','이송지시번호', '스카핑유무'를 UPDATE
     * @param stat			저장품상태
     * @param term			저장품이동조건
     * @param moveOrdDateNo	이송지시번호
     * @param scarfingYN	스카핑유무
     * @param whrStockId	저장품 ID
     * @return
     */
    public void modifyMoveOrderOfStock(String term, String ordData, String whrStockId){
		/*
		UPDATE  TB_YM_STOCK
		SET     STOCK_MOVE_TERM   = ?,
		        FRTOMOVE_WORD_NO  = ?
		WHERE   STOCK_ID          = ?
		*/
        String queryCode = "ym.common.dao.updateMoveOrderOfStock1";        
   		super.updateData(queryCode, new Object[]{ term, ordData, whrStockId });        
    }

    /**
     * 저장품테이블의 '작업예약ID', '저장품 이동 조건'를 UPDATE
     * A열연 SLAB - > B열연 SLAB야드로 이송도착시 PALLET_NO로 도착처리할 경우
     * 도착처리와 동시에 임시 PALLET_NO를 저장값 삭제
     * @param moveTerm		저장품 이동 조건
     * @param moveOrdNo		이송 지시 번호
     * @param whrStockId	저장품 ID
     * @return
     */
    public void modifyTermAndWBookIdOfStock(String wbookId, String term, String whrStockId){
		/*
		UPDATE  TB_YM_STOCK
		SET     WBOOK_ID = ?,
		        STOCK_MOVE_TERM = ?,
		        SHEAR_SUPPLY_DEMAND_DDTT=''
		WHERE   STOCK_ID = ?
		*/    	
        String queryCode = "ym.common.dao.updateTermAndWBookIdOfStock";
   		super.updateData(queryCode, new Object[]{ wbookId, term, whrStockId });        
    }

    /**
     * 저장품테이블의 '작업예약ID', '저장품 이동 조건', 'SCARFING구분'을 UPDATE
     * @param moveTerm		저장품 이동 조건
     * @param moveOrdNo		이송 지시 번호
     * @param whrStockId	저장품 ID
     * @return
     */
    public void modifyTermAndWBookIdOfStock(
            String wbookId, String term, String scarfingYn, String whrStockId){
        String queryCode = "ym.common.dao.updateTermAndWBookIdOfStock1";
        /*
        --ym.common.dao.updateTermAndWBookIdOfStock1
	--저장품의 이동조건, 작업예약ID를 UPDATE.
	UPDATE  TB_YM_STOCK
	SET        WBOOK_ID = ?,
	              STOCK_MOVE_TERM = ?,
	              SCARFING_SUPPLY_YN = ?
	WHERE    STOCK_ID = ?
        */
   		super.updateData(queryCode, new Object[]{ wbookId, term, scarfingYn, whrStockId });        
    }

    /**
     * 저장품테이블의 '산적 LOT 번호', 'SCARFING 보급 유무', '저장품 상태'를 UPDATE 
     * @param stackLoc	산적 LOT 번호
     * @param carfingYN		SCARFING 보급 유무
     * @param stockStat		저장품 상태
     * @param whrStockId	저장품 ID
     * @return
     */
    public void modifyStackLocOfStock(String stackLoc, String moveTerm, String whrStockId){
        String queryCode = "ym.common.dao.updateStackLocAndScarfingOfStock";
        /*
        --ym.common.dao.updateStackLocAndScarfingOfStock
	--산적 LOT 번호, 스카핑유무, 저장품상태를 UPDATE
	UPDATE  TB_YM_STOCK
	SET        STACK_LOT_NO = ?,
	              STOCK_MOVE_TERM = ?,
	              MODIFIER   = 'SYSTEM',
	              MOD_DDTT = SYSDATE
	WHERE    STOCK_ID = ?
        */
        super.updateData(queryCode, new Object[]{ stackLoc, moveTerm, whrStockId });
    }

    /**
     * 설비 테이블의 '적재상태', '작업진행상태'를 UPDATE
     * @param stackStat		적재상태
     * @param progStat		작업진행상태
     * @param whrEquipGp	설비구분
     */
    public void modifyStackAndProgStatOfEquip(String stackStat, String progStat, String whrEquipGp){
        String queryCode = "ym.common.dao.updateStackAndProgStatOfEquip";
        /*
        --ym.common.dao.updateStackAndProgStatOfEquip
	--설비의 적재상태, 진행상태를 UPDATE
	UPDATE  TB_YM_EQUIP
	SET        STACK_STAT   = ?,
	              WPROG_STAT  = ?
	WHERE    EQUIP_GP = ?
        */
        super.updateData(queryCode, new Object[]{ stackStat, progStat, whrEquipGp });        
    }
    
    /**
     * 적치단 테이블의 '적치 단 활성 상태'를 UPDATE
     * @param stat			적치 단 활성 상태
     * @param whrStackCol	적치 열 구분
     * @return
     */
    public void modifyActiveStatOfLayer(String stat, String whrStackCol) {
        String queryCode = "ym.common.dao.updateActiveStatOfLayer";
        /*
		UPDATE  TB_YM_STACKLAYER
		SET     STACK_LAYER_ACTIVE_STAT = ?
		WHERE   STACK_COL_GP = ?
        */
        super.updateData(queryCode, new Object[]{ stat, whrStackCol });        
    }

    /**
     * 적치단 테이블의 '적치 단 활성 상태'를 UPDATE
     * @param stat			적치 단 활성 상태
     * @param whrStackCol	적치 열 구분
     * @param whrBed		적치 번지 구분
     * @param whrLayer		적치 단 구분
     * 적치 단 활성 상태 [O],비활성화 [C], 사용금지[X]
     * @return
     */
    public void modifyActiveStatOfLayer(String stat
							    		, String whrStackCol
							    		, String whrBed
							    		, String whrLayer) {
        /*
		--적치 단 활성 상태를 UPDATE
		UPDATE  TB_YM_STACKLAYER
		SET     STACK_LAYER_ACTIVE_STAT = ?
		WHERE   STACK_COL_GP = ?
		AND     STACK_BED_GP = ?
		AND     STACK_LAYER_GP = ?
		*/    	
        String queryCode = "ym.common.dao.updateActiveStatOfLayer1";
        super.updateData(queryCode, new Object[]{ stat, whrStackCol, whrBed, whrLayer });        
    }

    /**
     * 적치단 테이블의 '적치 단 활성 상태'를 UPDATE
     * @param stat			적치 단 활성 상태
     * @param whrCol		적치 열 구분
     * @param whrBed		적치 번지 구분
     * @return
     */
    public void modifyActiveStatOfLayer(String stat, String whrCol, String whrBed) {
        String queryCode = "ym.common.dao.updateActiveStatOfLayer2";
        /*
        --ym.common.dao.updateActiveStatOfLayer2
	--적치 단 활성 상태를 UPDATE
	UPDATE  TB_YM_STACKLAYER
	SET        STACK_LAYER_ACTIVE_STAT = ?
	WHERE   STACK_COL_GP = ?
	AND       STACK_BED_GP = ?
        */
        super.updateData(queryCode, new Object[]{ stat, whrCol, whrBed });        
    }
    
    public void modifyActiveStatOfLayer_02(String stat, String whrCol, String whrBed) {
        String queryCode = "ym.common.dao.updateActiveStatOfLayer_02";
        /*
        --ym.common.dao.updateActiveStatOfLayer_02
	--적치 단 활성 상태를 UPDATE
	UPDATE  TB_YM_STACKLAYER
	SET        STACK_LAYER_ACTIVE_STAT = ?,
	              STOCK_ID = '',
	              STACK_LAYER_STAT = 'E' 
	WHERE   STACK_COL_GP = ?
	AND       STACK_BED_GP = ?
        */
        super.updateData(queryCode, new Object[]{ stat, whrCol, whrBed });        
    }
    /**
     * 설비테이블의 '적재상태'를 UPDATE(MCH)
     * @param stat			적재상태
     * @param whrEquipGp	설비 구분
     * @return
     */
    public void modifycolCompletion(String card_no, String stackcolgp) { 
		/*
		UPDATE TB_YM_STACKCOL
		SET  CAR_CARD_NO   = ?
		WHERE STACK_COL_GP = ?
		*/    	
        String queryCode = "ym.common.dao.updatestackcolCompletion";
        super.updateData(queryCode, new Object[]{ card_no, stackcolgp });        
    }
    /**
     * 설비테이블의 '적재상태'를 UPDATE
     * @param stat			적재상태
     * @param whrEquipGp	설비 구분
     * @return
     */
    public void modifyStackStatOfEquip(String stat, String whrEquipGp) { 
        String queryCode = "ym.common.dao.updateStackStatOfEquip";
        /*
        --ym.common.dao.updateStackStatOfEquip
	--'적재상태'를 UPDATE
	UPDATE  TB_YM_EQUIP
	SET        STACK_STAT = ? --적재 상태
	WHERE    EQUIP_GP = ?
        */
        super.updateData(queryCode, new Object[]{ stat, whrEquipGp });        
    }

    /**
     * 설비테이블의 '작업 MODE'를 UPDATE
     * @param mode			작업 MODE
     * @param modifier		수정자
     * @param whrEquipGp	설비 구분
     * @return
     */
    public void modifyModeOfEquip(String mode, String whrEquipGp) {
        String queryCode = "ym.common.dao.updateWorkModeOfEquip";
        /*UPDATE  TB_YM_EQUIP
			SET        WORK_MODE = ?,  --설비 상태              
			              MODIFIER    = 'SYSTEM',
			              MOD_DDTT  = SYSDATE
			WHERE   EQUIP_GP = ?
		*/
        super.updateData(queryCode, new Object[]{ mode, whrEquipGp });        
    }

    /**
     * 설비테이블의 '시스템 MODE'를 UPDATE
     * @param mode			시스템 MODE
     * @param modifier		수정자
     * @param whrEquipGp	설비 구분
     * @return
     */
    public void modifyHMIOfEquip(String mode, String whrEquipGp) {
        String queryCode = "ym.common.dao.updateHMIModeOfEquip";
        /*UPDATE  TB_YM_EQUIP
			SET        HMI_STAT    = ?,  --설비 상태              
			              MODIFIER    = 'SYSTEM',
			              MOD_DDTT  = SYSDATE
			WHERE   EQUIP_GP = ?
		*/
        super.updateData(queryCode, new Object[]{ mode, whrEquipGp });        
    }

    /**
     * 설비 테이블의 '설비상태', '휴지 CODE', '수정자'를 UPDATE
     * @param equipStat		설비상태
     * @param wprogStat		작업상태
     * @param downCd		휴지 CODE
     * @param whrEuqipGp	설비 구분
     * @return
     */
    public void modifyEquipStatAndDownCdOfEquip(
            String equipStat, String mode, String wprogStat, String downCd, String whrEuqipGp){
        String queryCode = "ym.common.dao.updateEquipStatAndDownCdOfEquip";
        /*
        --ym.common.dao.updateEquipStatAndDownCdOfEquip
	--설비 고장/복구 처리
	UPDATE  TB_YM_EQUIP
	SET        EQUIP_STAT    = ?,  --설비 상태
	              WORK_MODE  = ?,
	              WPROG_STAT = ?,
	              DOWN_CD      = ?,  --휴지 CODE  
	              MODIFIER       = 'SYSTEM',
	              MOD_DDTT     = SYSDATE
	WHERE   EQUIP_GP        = ?
        */
        super.updateData(queryCode, new Object[]{ equipStat, mode, wprogStat, downCd, whrEuqipGp });
    }

    /**
     * 설비 테이블의 '설비상태', '휴지 CODE', '수정자'를 UPDATE
     * @param equipStat		설비상태
     * @param wprogStat		작업상태
     * @param downCd		휴지 CODE
     * @param whrEuqipGp	설비 구분
     * @return
     */
    public void modifyEquipStatAndDownCdOfEquip(String equipStat, String wprogStat, String downCd, String whrEuqipGp){
		/*
		UPDATE  TB_YM_EQUIP
		SET     EQUIP_STAT    = ?,  --설비 상태
		        WPROG_STAT = ?,
		        DOWN_CD      = ?,  --휴지 CODE  
		        MODIFIER       = 'SYSTEM',
		        MOD_DDTT     = SYSDATE
		WHERE   EQUIP_GP        = ?
		*/    	
        String queryCode = "ym.common.dao.updateEquipStatAndDownCdOfEquip1";
        super.updateData(queryCode, new Object[]{ equipStat, wprogStat, downCd, whrEuqipGp });
    }

    /**
     * 관제 ReSchedul 확정에 따른 '장입LOT번호'를 UPDATE
     * @param lotNo		장입예정번호
     * @param whrSlabNo	슬라브번호
     * @return
     */
    public void modifyZoneInOfStock(String lotNo, String term, String whrSlabNo) {
        String queryCode = "ym.common.dao.updateZoneInOfStock";
        /*
        --ym.common.dao.updateZoneInOfStock
	--관제 ReSchedul 확정에 따른 '장입LOT번호'를 UPDATE
	UPDATE  TB_YM_STOCK
	SET        CHARGE_LOT_NO = ?,
	              STOCK_MOVE_TERM = ?
	WHERE   STOCK_ID           = ?
        */
        super.updateData(queryCode, new Object[]{ lotNo, term, whrSlabNo });
    }

    /**
     * 저장품 테이블의 '저장품 이동 조건'을 UPDATE
     * @param moveTerm	저장품 이동 조건
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyMoveTermOfStock(String moveTerm, String whrStockId) {
        String sQueryId = "ym.common.dao.updateMoveTermOfStock";
        /*
        --ym.common.dao.updateMoveTermOfStock
	--
	UPDATE  TB_YM_STOCK
	SET        STOCK_MOVE_TERM = ?,
	              CHARGE_LOT_NO     = NULL,
	              MODIFIER                = 'SYSTEM',
	              MOD_DDTT              = SYSDATE
	WHERE   STOCK_ID = ?
        */
        super.updateData(sQueryId, new Object[]{ moveTerm, whrStockId });
    }
    
    /**
     * 적치단 테이블의 '적치 단 상태'를 UPDATE
     * @param stat		적치 단 상태
     * @param whrCol	적치열
     * @param whrBed	번지
     * @param whrLayer	단
     * @return
     */
    public void modifyStackStateOfLayer(String stat, String whrCol, String whrBed, String whrLayer) {
        /*UPDATE  TB_YM_STACKLAYER
		  SET     STACK_LAYER_STAT = ?
		  WHERE   STACK_COL_GP = ?
		  AND     STACK_BED_GP = ?
		  AND     STACK_LAYER_GP = ?
		*/    
        String  queryCode = "ym.common.dao.updateStackStateOfLayer";
        super.updateData(queryCode, new Object[]{ stat, whrCol, whrBed, whrLayer});
    }

    /**
     * 적치단 테이블의 '적치 단 상태'를 UPDATE
     * @param stockId	저장품ID
     * @param stat		적치 단 상태
     * @param whrCol	적치열
     * @param whrBed	번지
     * @param whrLayer	단
     * @return
     */
    public void modifyStackStateOfLayer(
            String stockId, String stat, String whrCol, String whrBed, String whrLayer) {
        String  queryCode = "ym.common.dao.updateStackStateOfLayer2";
        /*
        --ym.common.dao.updateStackStateOfLayer2
	--적치단의 적재상태를 UPDATE
	UPDATE  TB_YM_STACKLAYER               
	SET        STOCK_ID = ?, 
	             STACK_LAYER_STAT = ?
	WHERE   STACK_COL_GP = ?
	AND       STACK_BED_GP = ?
	AND       STACK_LAYER_GP = ?
        */
        super.updateData(queryCode, new Object[]{ stockId, stat, whrCol, whrBed, whrLayer});
    }

    /**
     * 적치단 테이블의 '적치 단 상태'를 UPDATE
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyStackStateOfLayer(String whrStockId) {
    	/*
		UPDATE  TB_YM_STACKLAYER
		SET     STOCK_ID = NULL, 
		        STACK_LAYER_ACTIVE_STAT = DECODE(SUBSTR(STACK_COL_GP,3,2), 'TR','C','O'),
		        STACK_LAYER_STAT = 'E'
		WHERE   STOCK_ID = ?
		*/
        String  queryCode = "ym.common.dao.updateStackStateOfLayer1";
        super.updateData(queryCode, new Object[]{ whrStockId });
    }

    /**
     * 저장품테이블의 '이송 설비 구분', '이송 설비 BED 구분', '이송 설비 단 구분'을 UPDATE
     * @param col			이송 설비 구분
     * @param bed			이송 설비 BED 구분
     * @param layer			이송 설비 단 구분
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyMoveEquipOfStock(String col, String bed, String layer, String whrStockId) {
        String queryCode = "ym.common.dao.updateMoveEquipOfStock";
        /*
        --ym.common.dao.updateMoveEquipOfStock
	--'이송 설비 구분', '이송 설비 BED 구분', '이송 설비 단 구분'을 UPDATE
	UPDATE  TB_YM_STOCK
	SET        FRTOMOVE_EQUIP_GP = ?,
	              FRTOMOVE_EQUIP_BED_GP = ?,
	             FRTOMOVE_EQUIP_LAYER_GP = ?
	WHERE   STOCK_ID = ?
        */
        super.updateData(queryCode, new Object[]{ col, bed, layer, whrStockId });        
    }

	/**
     * 저장품테이블의 '이송 설비 구분', '이송 설비 BED 구분', '이송 설비 단 구분'을 UPDATE
     * @param col			이송 설비 구분
     * @param bed			이송 설비 BED 구분
     * @param layer			이송 설비 단 구분
     * @param cardNo		차량카드번호
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyMoveEquipOfStock(
            String col, String bed, String layer, String cardNo, String whrStockId) {
        String queryCode = "ym.common.dao.updateMoveEquipOfStock1";
        /*
        --ym.common.dao.updateMoveEquipOfStock1
	--'이송 설비 구분', '이송 설비 BED 구분', '이송 설비 단 구분', '카드번호'를 UPDATE
	UPDATE  TB_YM_STOCK
	SET        FRTOMOVE_EQUIP_GP = ?,
	              FRTOMOVE_EQUIP_BED_GP = ?,
	             FRTOMOVE_EQUIP_LAYER_GP = ?,
	             CAR_CARD_NO = ?
	WHERE   STOCK_ID = ?
        */
        super.updateData(queryCode, new Object[]{ col, bed, layer, cardNo, whrStockId });        
    }

	/**
     * 저장품테이블의 '이송 설비 구분', '이송 설비 BED 구분', '이송 설비 단 구분'을 UPDATE
     * @param col			이송 설비 구분
     * @param bed			이송 설비 BED 구분
     * @param layer			이송 설비 단 구분
     * @param term			저장품이동조건
     * @param whrStockId	저장품ID
     * @return
     */
    public void modifyTermAndMoveEquipOfStock(String col, String bed, String layer, String term, String whrStockId) {
        String queryCode = "ym.common.dao.updateMoveEquipOfStock2";
        /*UPDATE  TB_YM_STOCK
		     SET  FRTOMOVE_EQUIP_GP = ?,
		          FRTOMOVE_EQUIP_BED_GP = ?,
		          FRTOMOVE_EQUIP_LAYER_GP = ?,
		          STOCK_MOVE_TERM = ?
		  WHERE   STOCK_ID = ?
		*/
        super.updateData(queryCode, new Object[]{ col, bed, layer, term, whrStockId });        
    }

    /**
     * 저장품테이블의 최종[중계] SADDLE 정보를 UPDATE
     * @param relaySaddle	최종[중계] SADDLE
     * @param whrStockId		저장품ID
     */
    public void modifyRelayOfStock(String relaySaddle, String whrStockId) {
        String queryCode = "ym.common.dao.updateRelayOfStock";
        /*
		--ym.common.dao.updateRelayOfStock
		--최종[중계] SADDLE 정보를 UPDATE
		UPDATE  TB_YM_STOCK A
		SET CTS_RELAY_SADDLE = ?
		  , MODIFIER='CTS'
		  , MOD_DDTT=sysdate 
		WHERE   STOCK_ID = ?
        */
        super.updateData(queryCode, new Object[]{ relaySaddle, whrStockId });        
    }

    /**
     * 적치단 테이블의 '저장품ID'를 UPDATE
     * @param stockId	저장품ID
     * @param whrCol		적치열
     * @param whrBad		번지
     * @param whrLayer		단
     * @return
     */
    public void modifyStockIdOfLayer(
            String stockId, String whrCol, String whrBad, String whrLayer) {
        String  queryCode = "ym.common.dao.updateStockIdOfLayer";
        /*
        --ym.common.dao.updateStockIdOfLayer
	--저장품ID를 UPDATE
	UPDATE  TB_YM_STACKLAYER
	SET        STOCK_ID = ?
	WHERE   STACK_COL_GP = ?
	AND       STACK_BED_GP = ?
	AND       STACK_LAYER_GP = ?
        */
        super.updateData(queryCode, new Object[]{ stockId, whrCol, whrBad, whrLayer });
    }

    /**
     * 적치단 테이블의 '저장품ID'를 UPDATE
     * @param stockId	저장품ID
     * @param whrCol		적치열
     * @return
     */
    public void modifyStockIdOfLayer(String stockId, String whrCol) {
        String  queryCode = "ym.common.dao.updateStockIdOfLayer1";
        /*
        --ym.common.dao.updateStockIdOfLayer1
	--적치단테이블의 저장품ID를 UPDATE
	UPDATE  TB_YM_STACKLAYER
	SET        STOCK_ID = ?
	WHERE   STACK_COL_GP = ?
        */
        super.updateData(queryCode, new Object[]{ stockId, whrCol });
    }

    /**
     * 적치단 테이블의 '적치상태'를 UPDATE
     * @param layerStat	적치상태
     * @param whrCol		적치열
     * @param whrBad		번지
     * @param whrLayer		단
     * @return
     */
    public void modifyLayerStateOfLayer(
            String layerStat, String whrCol, String whrBad, String whrLayer) {
        String  queryCode = "ym.common.dao.updateLayerState";
        /*
        --ym.common.dao.updateLayerState
	--적치단상태를 UPDATE
	UPDATE  TB_YM_STACKLAYER
	SET        STACK_LAYER_STAT = ?
	WHERE   STACK_COL_GP = ?
	AND       STACK_BED_GP = ?
	AND       STACK_LAYER_GP = ?
        */
        super.updateData(queryCode, new Object[]{ layerStat, whrCol, whrBad, whrLayer });
    }

    /**
     * 적치단 테이블의 '저장품ID', '적치단 상태' 항목을 UPDATE 한다.
     * @param coilNo	저장품ID
     * @param stat		적치상태['S','P','L',...]
     * @param whrCol	적치열
     * @return
     */
    public void modifyStockStatOfLayer0(String coilNo, String stat, String whrCol){
        String  queryCode = "ym.common.dao.updateStockStatOfLayer0";
        /*
        --ym.common.dao.updateStockStatOfLayer0
	--'저장품ID', '적치단 상태' 항목을 UPDATE
	UPDATE  TB_YM_STACKLAYER
	SET        STOCK_ID = ?,
	              STACK_LAYER_STAT = ?
	WHERE    STACK_COL_GP = ?
	AND        STACK_LAYER_STAT IN( 'L','P')
        */
        super.updateData(queryCode, new Object[]{ coilNo, stat, whrCol });
    }

    /**
     * 적치단 테이블의 '저장품ID', '적치단 상태' 항목을 UPDATE 한다.
     * @param coilNo	저장품ID
     * @param stat		적치상태['S','P','L',...]
     * @param whrCol	적치열
     * @return
     */
    public void modifyStockStatOfLayer(String coilNo, String stat, String whrCol){
        String  queryCode = "ym.common.dao.updateStockStatOfLayer1";
        /*
        --ym.common.dao.updateStockStatOfLayer1
	--'저장품ID', '적치단 상태' 항목을 UPDATE
	UPDATE  TB_YM_STACKLAYER
	SET        STOCK_ID = ?,
	              STACK_LAYER_STAT = ?
	WHERE    STACK_COL_GP = ?
        */
        super.updateData(queryCode, new Object[]{ coilNo, stat, whrCol });
    }

    /**
     * 적치단 테이블의 '저장품ID', '적치단 상태' 항목을 UPDATE 한다.
     * @param coilNo	저장품ID
     * @param actStat	단상태['O','C']
     * @param stat		적치상태['S','P','L',...]
     * @param whrCol	적치열
     * @return
     */
    public void modifyStockStatOfLayer(String coilNo, String actStat, String stat, String whrCol){
        String  queryCode = "ym.common.dao.updateStockStatOfLayer2";
        /*UPDATE  TB_YM_STACKLAYER
		    SET   STOCK_ID                = ?,
		          STACK_LAYER_ACTIVE_STAT = ?,
		          STACK_LAYER_STAT        = ?
		  WHERE   STACK_COL_GP            = ?
		*/
        super.updateData(queryCode, new Object[]{ coilNo, actStat, stat, whrCol });
    }

    /**
     * 적치단 테이블의 '저장품ID', '적치단 상태' 항목을 UPDATE 한다.
     * @param stockId	저장품ID
     * @param stat		적치상태['S','P','L',...]
     * @param whrCol	적치열
     * @param whrBad	번지
     * @param whrLayer	단
     * @return
     */
    public void modifyStockStatOfLayer(String stockId, String stat, String whrCol, String whrBad, String whrLayer){

        /*
		UPDATE  TB_YM_STACKLAYER
		SET     STOCK_ID         = ?,
		        STACK_LAYER_STAT = ?
		WHERE   STACK_COL_GP 	 = ?
		  AND   STACK_BED_GP 	 = ?
	 	  AND   STACK_LAYER_GP 	 = ?
        */
        String  queryCode = "ym.common.dao.updateStockStatOfLayer3";        
        super.updateData(queryCode, new Object[]{ stockId, stat, whrCol, whrBad, whrLayer });
    }

    /**
     * 적치단 테이블의 '저장품ID', '적치단 상태' 항목을 UPDATE 한다.
     * @param stockId	
     * @param active	적치단 활성 상태
     * @param stat		적치상태['S','P','L',...]
     * @param whrCol	적치열
     * @param whrBad	번지
     * @param whrLayer	단
     * @return
     */
    public void modifyStockStatOfLayer(String stockId	//저장품ID
						    		, String active		//적치단 활성 상태
						    		, String stat		//적치상태['S','P','L',...]
						    		, String whrCol		//적치열
						    		, String whrBad		//번지
						    		, String whrLayer){	//단
    	/*
		UPDATE  TB_YM_STACKLAYER
		SET        STOCK_ID                            = ?,
		             STACK_LAYER_ACTIVE_STAT = ?,
		             STACK_LAYER_STAT              = ?
		WHERE   STACK_COL_GP                    = ?
		AND       STACK_BED_GP                    = ?
		AND       STACK_LAYER_GP                 = ?
		*/
        String  queryCode = "ym.common.dao.updateStockStatOfLayer4";
        super.updateData(queryCode, new Object[]{ stockId, active, stat, whrCol, whrBad, whrLayer });
    }

    /**
     * 적치대의 가능 항목을 UPDATE 한다.
     * @param cnt	가능수량
     * @param wt	가능중량
     * @param t		가능높이
     * @param col	적치열
     * @param bed	번지
     * @return
     */
    public void modifyPossibleOfStacker(
            String cnt, String wt, String t, String whrCol, String whrBed) {
        String  queryCode = "ym.common.dao.updatePossibleOfStacker1";
        /*
        --ym.common.dao.updatePossibleOfStacker1
	--적치대의 가능 항목을 UPDATE.
	UPDATE  TB_YM_STACKER
	SET       STACK_BED_ABLE_QNTY   = ?,
	             STACK_BED_ABLE_WT      = ?,
	             STACK_BED_ABLE_HIGH   = ?
	WHERE   STACK_COL_GP = ?
	AND       STACK_BED_GP = ?
        */
        super.updateData(queryCode, new Object[]{ cnt, wt, t, whrCol, whrBed });
    }

    /**
     * 적치대의 적재능력을 초기화 한다.
     * @param col	적치열
     * @param bed	번지
     * @return
     */
    public void modifyPossibleOfStacker(String col, String bed) {
        String  queryCode = "ym.common.dao.updatePossibleOfStacker";
        /*
        UPDATE  TB_YM_STACKER
		SET		STACK_BED_QNTY_CURR 	= 0,
		        STACK_BED_WT_CURR     	= 0,
		        STACK_BED_HIGH_CURR  	= 0,
		        STACK_BED_W_CURR       	= 0,
		        STACK_BED_LEN_CURR     	= 0,
		        STACK_BED_ABLE_QNTY   	= STACK_BED_QNTY_MAX,
		        STACK_BED_ABLE_WT      	= STACK_BED_WT_MAX,
		        STACK_BED_ABLE_HIGH 	= STACK_BED_HIGH_MAX
		WHERE   STACK_COL_GP = ?
		AND     STACK_BED_GP = ?
        */
        super.updateData(queryCode, new Object[]{ col, bed });
    }

    /**
     * 백업 실적을 생성한다.
     * @param schid		스케쥴ID
     * @param sid		저장품ID
     * @param egp		설비구분
     * @param swk		스케쥴작업종류
     * @param wprefer	스케쥴작업우선순위
     * @param up		FROM위치
     * @param put		TO위치
     * @return
     */
    public int createBackUpWrslt(
            String schid, String sid, String egp, String swk, String wpre, String up, String put) {
	/*
	INSERT INTO TB_YM_WRSLT VALUES(
		TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI') || LPAD(YM_WRSLT_SEQ.NEXTVAL, 6, '0'),	--CRANE 작업실적 ID          
		?,                                                                       	--SCHEDULE ID                
		?,                                                                       	--저장품 ID                  
	    ?,                                                                       	--설비 구분                  
		?,                                                                       	--SCHEDULE 작업 종류         
		?,                                                                       	--SCHEDULE 작업우선순위      
		NULL,                                                                    	--CRANE 작업 일시            
		NULL,                                                                    	--CRANE 작업 근              
		NULL,                                                                    	--CRANE 작업 조              
		NULL,                                                                    	--CRANE 작업지시 일시        
		NULL,                                                                    	--작업예약 일시              
		NULL,                                                                    	--작업예약 SCHEDULE 조건     
		NULL,                                                                    	--작업예약 SCHEDULE 실행 일시
		NULL,                                                                    	--SCHEDULE 작업요구 일시     
		NULL,                                                                    	--SCHEDULE 작업요구 근       
		NULL,                                                                    	--SCHEDULE 작업요구 조       
		NULL,                                                                    	--SCHEDULE 작업요구 형태     
		'N',                                                                     	--CRANE 작업결과 CODE        
		?,                                                                       	--CRANE 작업지시 UP 위치     
		NULL,                                                                    	--CRANE 작업지시 UP X축      
		NULL,                                                                    	--CRANE 작업지시 UP Y축      
		NULL,                                                                    	--CRANE 작업지시 UP Z축      
		NULL,                                                                    	--CRANE 작업지시 UP X 범위   
		NULL,                                                                    	--CRANE 작업지시 UP Y 범위   
		NULL,                                                                    	--CRANE 작업지시 UP Z 범위   
		NULL,                                                                    	--CRANE 작업결과 UP X축      
		NULL,                                                                    	--CRANE 작업결과 UP Y축      
		NULL,                                                                    	--CRANE 작업결과 UP Z축      
		?,                                                                       	--CRANE 작업결과 UP 위치     
		'N',                                                                     	--CRANE 작업결과 UP 기능     
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MI'),                                       	--CRANE 작업결과 UP 일시     
		?,                                                                       	--CRANE 작업지시 PUT 위치    
		NULL,                                                                    	--CRANE 작업지시 PUT X축     
		NULL,                                                                    	--CRANE 작업지시 PUT Y축     
		NULL,                                                                    	--CRANE 작업지시 PUT Z축     
		NULL,                                                                    	--CRANE 작업지시 PUT X 범위  
		NULL,                                                                    	--CRANE 작업지시 PUT Y 범위  
		NULL,                                                                    	--CRANE 작업지시 PUT Z 범위  
		NULL,                                                                    	--CRANE 작업결과 PUT X축     
		NULL,                                                                    	--CRANE 작업결과 PUT Y축     
		NULL,                                                                    	--CRANE 작업결과 PUT Z축     
		?,                                                                       	--CRANE 작업결과 PUT 위치    
		'N',                                                                     	--CRANE 작업결과 PUT 기능    
		TO_CHAR(SYSDATE,'YYYYMMDDHH24MI'),                                       	--CRANE 작업결과 PUT 일시    
		'SYSTEM',                                                                	--등록자                     
		SYSDATE,                                                                 	--등록 일시                  
		NULL,                                                                    	--수정자                     
		NULL,																		--수정 일시
		'N', 																		--삭제 유무
		'1'  																		--YARD 구분        
	)	
*/    	
        String  queryCode = "ym.common.dao.insertBackUpWrslt";
        return super.insertData(queryCode, 
                new Object[]{ schid, sid, egp, swk, wpre, up, up, put, put });
    }

    /**
     * 스케쥴을 INSERT 한다.
     * @param editData
     */
    public int createSlabSchedule(List editData) {
        /*
		INSERT INTO TB_YM_SCH (
		  SCH_ID,
		  STOCK_ID,
		  SCH_RULE_ID,
		  YD_GP,
		  BAY_GP,
		  SCH_WORK_EQUIP_NO,
		  SCH_WORK_STAT,
		  SCH_WPREFER,
		  SCH_WORK_KIND,
		  SCH_WORK_AID_YN,
		  SCH_WORK_GRIP_LOT_YN,
		  CRANE_WORD_UP_LOC,
		  WBOOK_LOC_DECISION_METHOD,
		  CRANE_WORD_PUT_LOC,
		  SCH_WORK_CAR_NO,
		  SCH_WDEMAND_TYPE,
		  WBOOK_SCH_ACT_DDTT,
		  SCH_WDEMAND_DDTT,
		  SCH_WDEMAND_DUTY,
		  SCH_WDEMAND_PARTY,  
		  WBOOK_ID,
		  FRTOMOVE_EQUIP_GP,
		  CAR_CARD_NO,
		  STACK_STAT,
		  REGISTER,
		  REG_DDTT,
		  MODIFIER,
		  MOD_DDTT,
		  DEL_YN)
		VALUES (TO_CHAR(SYSDATE,'YYYYMMDDHH24MI')||LPAD(YM_SCH_SEQ.NEXTVAL,6,'0'),?,?,?,?,?,'S',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,NULL,NULL,'N')
        */    
        String  queryCode = "ym.common.dao.insertSlabSchedule";
        return super.insertData(queryCode, editData.toArray());
    }

    /**
     * 내부 인터페이스 전문에 대한 송수신 LOG INSERT
     * @param data	INSERT DATA
     */
    public void createLog(List data) {
        String queryCode = "ym.common.dao.insertLog";
        /*
        --ym.common.dao.insertLog
	--내부인터페이스 로그생성
	INSERT INTO TB_YM_IFTCLOG VALUES(
	  TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI') || LPAD(YM_TC_LOG_SEQ.NEXTVAL, 6, '0'),  --TC LOG ID
	  ?,
	  ?,  --TC CODE
	  SYSDATE,--TC 발생 일시
	  ?,  --TC 발생 내용
	  ?,  --TC 발생 MESSAGE
	  'L', --TC 발생 유형
	  (SELECT TC_NAME FROM TB_YM_IFTCLAYOUT WHERE TC_CD = ? AND ROWNUM = 1),
	  0,
	  'N')
        */
        super.insertData(queryCode, data.toArray());
    }
    
    /**
     * 1. 작업예약 테이블 INSERT
     * 2. 저장품 테이블에 '작업예약ID' UPDATE, 적치단 테이블에 '적치상태'를 UPDATE
     * @param colGp		적치열
     * @param sch		스케쥴작업종류
     * @param operGp	오퍼레이터 지정 구분
     * @param loc		PUT위치
     * @param workGp	근조
     */
    public String createWBook(String colGp, String sch, String operGp, String loc) {
        String nextWBookId = readNextWBookId();
        /*
		INSERT INTO TB_YM_WBOOK (
		            WBOOK_ID, 
		            YD_GP, 
		            BAY_GP,
		            SCH_WORK_KIND, 
		            SCH_WORK_LOC_DECISION_METHOD,
		            CRANE_WORD_PUT_LOC, 
		            WBOOK_DDTT, 
		            WBOOK_DUTY,
		            WBOOK_PARTY, 
		            WBOOK_SCH_TERM, 
		            WBOOK_SCH_ACT_DDTT,
		            REGISTER, 
		            REG_DDTT, 
		            MODIFIER, 
		            MOD_DDTT, 
		            DEL_YN)
		VALUES (?, ?, ?,?, ?, ?, to_char(sysdate,'YYYYMMDDHH24MI'), ?,?, 'T', to_char(sysdate,'YYYYMMDDHH24MI),'SYSTEM', sysdate, null, null, 'N')
		*/
        String queryCd = "ym.common.dao.insertWBook";
		super.insertData(queryCd, new Object[]{ nextWBookId, 
										        colGp.substring(0, 1), 
										        colGp.substring(1, 2), 
										        sch,
										        operGp,
										        loc,
										        YmCommonUtil.getWorkDuty(),
										        YmCommonUtil.getWorkParty()});
		return nextWBookId;
    }

    /**
     * 휴지설비테이블의 KEY를 창성한다.
     * @param data	초기데이터
     * @return
     */
    public void createEquipDown(List data) {
	/*
	INSERT INTO TB_YM_EQUIPDOWN (
	  EQUIP_GP,
	  DOWN_OCCUR_SEQ,
	  DOWN_PASS_HR_CARRYOVER,
	  DOWN_CD,
	  DOWN_OCCUR_DDTT,
	  DOWN_OCCUR_WORK_DUTY,
	  DOWN_OCCUR_WORK_PARTY,
	  DOWN_END_DDTT,
	  DOWN_END_WORK_PARTY,
	  DOWN_PASS_HR,
	  DOWN_RECOVER_CONTENTS,
	  REGISTER,
	  REG_DDTT,
	  MODIFIER,
	  MOD_DDTT,
	  DEL_YN )
	VALUES (?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MI') || LPAD(YM_EQUIPDOWN_SEQ.NEXTVAL, 6, '0'),
	?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?, ?, ?, ?, ?, ?, 'SYSTEM', SYSDATE, NULL, NULL, 'N')
	*/    	
        String queryCode = "ym.common.dao.insertEquipDown";
        super.insertData(queryCode, data.toArray());
    }
    
    
    /**
     *  B열연 Slab야드 도착처리시점에
     *  의미없는 카드번호를 저장품테이블에서 삭제한다.
     * @return
     */
    public int modifyNeedlessCardNo(String sColGp) {
        
        /*
	UPDATE tb_ym_stock
	SET car_card_no = null
	WHERE car_card_no in 
	(
		SELECT 
		    --a.stock_id,
		    a.car_card_no
		    --,b.stack_col_gp
		FROM tb_ym_stock a,
		     	   tb_ym_stacklayer b
		WHERE a.stock_id            = b.stock_id
		AND   a.car_card_no         is not null     
		AND   b.stack_col_gp        like :col_gp||'%'
		AND   b.stack_layer_stat    in ('L','S','U')                  
		AND   a.car_card_no not in
			(
			SELECT 
			    a.car_card_no
			FROM tb_ym_stock a,
			    	   tb_ym_stacklayer b
			WHERE a.stock_id            = b.stock_id
			AND   a.car_card_no         is not null     
			AND  (
			     b.stack_col_gp        like '2_PT%'
			     OR			     
			     b.stack_col_gp        like '2_CR%'
			     )
			AND   b.stack_layer_stat    in ('L','S','U')                
			)
	)
        */
        
        String  queryCode = "ym.common.dao.updateNeedlessCardNo";
        return super.updateData(queryCode, new Object[]{ sColGp });
    }
                  
    /**
     * 저장품을 삭제한다.
     * @param stockId	저장품ID
     */
    public void removeStock(String stockId) {
		String  queryCode = "ym.common.dao.deleteStock";
		/*
		--ym.common.dao.deleteStock
		--저장품을 삭제한다.
		DELETE
		FROM    TB_YM_STOCK
		WHERE  STOCK_ID= ?
		*/
		super.deleteData(queryCode, new Object[]{ stockId });
    }
    
    /**
     * 스케쥴을 삭제한다.
     * @param schId	스케쥴ID
     */
    public void removeSchdule(String schId) {
		String  queryCode = "ym.common.dao.deleteSchdule";
		/*
		--ym.common.dao.deleteSchdule
		--스케쥴을 삭제한다.
		DELETE
		FROM     TB_YM_SCH
		WHERE   SCH_ID = ?
		*/
		super.deleteData(queryCode, new Object[]{ schId });
    }
    
    /**
     * 스케쥴을 삭제한다.
     * @param stockId	제품id
     */
    public void removeSchdule2(String stockId) {
		String  queryCode = "ym.common.dao.deleteSchdule2";
		/*
		delete from TB_YM_SCH
		where stock_id = ?
		*/
		super.deleteData(queryCode, new Object[]{ stockId });
    }

    /**
     * 작업예약을 삭제한다.
     * @param wbookId	작업예약ID
     */
    public void removeWBook(String wbookId) {
		String  queryCode = "ym.common.dao.deleteWBook";
		/*
		--ym.common.dao.deleteWBook
		--작업예약을 삭제한다.
		DELETE
		FROM     TB_YM_WBOOK
		WHERE   WBOOK_ID = ?

		*/
		super.deleteData(queryCode, new Object[]{ wbookId });    
    }
    
    /**
     * 코드를 네임으로 반환한다.
     * 
     * @param 야드구분, 동구분, 스판구분, 소재구분
     * @return JDTORecord
     * @throws 
     */
	public JDTORecord getCodeToName(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);
    }
    
    /**
     * YJK
     * @return 
     * @throws 
     */
	public List getCommonList(String queryCode,Object[] objs) throws DAOException{
		
		// PIDEV
//		queryCode = ymCommDAO.getYmRulePI("", "getCommonList", "YM0001", queryCode, "APPPI0", "*", "*" );

		return super.findList(queryCode, objs);	
    }
	
	/**
     * YJK
     * @return 
     * @throws 
     */
	public List getCommonList(String queryCode,String sDsql, Object[] objs) throws DAOException{
		return super.findList(queryCode,sDsql,objs);	
    }
    
	/**
     * YJK
     * @return 
     * @throws 
     */
	public JDTORecord getCommonInfo(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);	
    }
	/**
     * YJK
     * @return 
     * @throws 
     */
	public List getCommonInfo2(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
    }
	
	/**
     * YJK
     * @return 
     * @throws 
     */
	public int insertData(String queryCode, Object[] objs) throws DAOException{	
		return super.insertData(queryCode,objs);
	}
	
	/**
     * YJK
     * @return 
     * @throws 
     */
   	public int updateData(String queryCode, Object[] objs) throws DAOException{	   	 	
   		return super.updateData(queryCode,objs);
   	}
   	
   	/**
     * YJK
     * @return 
     * @throws 
     */
   	public int deleteData(String queryCode, Object[] objs) throws DAOException{	   	 	
   		return super.deleteData(queryCode,objs);
   	}
   	
   	/**
     * YJK
     * @return 
     * @throws 
     */
	public List getCommonList(String queryCode) {
		return super.findList(queryCode);	
    }
    
    
   /**
     * YJK
     * @return 
     * @throws 
     */
	public List getCommonList(String queryCode,String sDsql) {
		return super.findList(queryCode,sDsql);	
    }
    
    /**
     * YJK
     * @return 
     * @throws 
     */
	public int insertData(String queryCode, String sDsql) throws DAOException{	
		return super.insertData(queryCode,sDsql);
	}
	
	/**
     * YJK
     * @return 
     * @throws 
     */
   	public int updateData(String queryCode,String sDsql) throws DAOException{	   	 	
   		return super.deleteData(queryCode,sDsql);
   	}
   	
   	/**
     * YJK
     * @return 
     * @throws 
     */
   	public int deleteData(String queryCode, String sDsql) throws DAOException{	   	 	
   		return super.deleteData(queryCode,sDsql);
   	}
   	
    /**
     * 스케줄 번호 가져오기
     * @param STL_NO	재료번호
     * @return
     */
    public JDTORecord getSchSearch(String stl_no) {
    	/**
			SELECT SCH_ID
			  FROM TB_YM_SCH
			 WHERE STOCK_ID =?
    	 */
		String  queryCode = "ym.facade.internal.session.getSchSearch";	
		return super.findByPrimaryKey(queryCode, new Object[]{ stl_no });        
    }
    
    /**
     * 작업예약존재여부 체크
     * @param STL_NO	재료번호
     * @return
     */
    public JDTORecord getWbookSearch(String stl_no) {
    	/**
			SELECT STOCK_ID
			  FROM TB_YM_STOCK
			 WHERE STOCK_ID =?
			  AND WBOOK_ID IS NULL
    	 */
		String  queryCode = "ym.facade.internal.session.getWbookSearch";	
		return super.findByPrimaryKey(queryCode, new Object[]{ stl_no });        
    }
    
    
    /**
     * 스케쥴취소이력을 INSERT 한다.
     * @param editData
     */
    public int createCancelSchedule(List editData) {
    	/*ym.common.dao.insertCancelSchedule
    	INSERT INTO TB_YM_SCHHIST
    	SELECT 
    	SCH_ID
    	,STOCK_ID
    	,SCH_RULE_ID
    	,YD_GP
    	,BAY_GP
    	,SCH_WORK_EQUIP_NO
    	,SCH_WORK_STAT
    	,SCH_WPREFER
    	,SCH_WORK_KIND
    	,SCH_WORK_AID_YN
    	,SCH_WORK_GRIP_LOT_YN
    	,CRANE_WORD_UP_LOC
    	,WBOOK_LOC_DECISION_METHOD
    	,CRANE_WORD_PUT_LOC
    	,SCH_WORK_CAR_NO
    	,SCH_WDEMAND_TYPE
    	,WBOOK_SCH_ACT_DDTT
    	,SCH_WDEMAND_DDTT
    	,SCH_WDEMAND_DUTY
    	,SCH_WDEMAND_PARTY
    	,WBOOK_ID
    	,FRTOMOVE_EQUIP_GP
    	,CAR_CARD_NO
    	,STACK_STAT
    	,REGISTER
    	,REG_DDTT
    	,V_MODIFIER
    	,SYSDATE
    	,DEL_YN
    	FROM USRYMA.TB_YM_SCH
    	WHERE SCH_ID=:V_SCH_ID */
        String  queryCode = "ym.common.dao.insertCancelSchedule";
        return super.insertData(queryCode, editData.toArray());
    }
    
    
    /**
     * 실적등록 할 마지막 실적을 리턴한다.
     * @param ydGp		야드구분
     * @param bayGp		동구분
     * @param equipNo	설비번호
     * @param ativeStat	SCHEDULE 기준 활성 상태
     * @return 
     */
    public JDTORecord readRastWrslt(String stockId) {
        String queryCode = "ym.common.dao.selectreadRastWrslt";        
        return super.findByPrimaryKey(queryCode, new Object[]{ stockId });    
    }
    
}
