package com.inisteel.cim.ym.ilkwan.dao;

import java.util.List;
import jspeed.base.record.JDTORecord;
import jspeed.base.log.*;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.common.exception.DAOException;

/**
 * 
 * @(#)ilkwanDAO.java
 * 
 * @version    :
 * @author     : 윤재광
 * @date         : 2009. 3. 18
 *
 * @description :
 * 
 */

public class ilkwanDAO  extends CommonDAO{
	
	private Logger log = null; 
	
	public ilkwanDAO()
	{
		log = LogService.getInstance().getLogServiceContext().getLogger( "ym" );
	}
	
	public List getListCraneWBook(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
	}
	public int updateStockMoveTerm(String qeuryId, List updateData) {
	    return super.updateData(qeuryId, updateData.toArray());
	}
	public JDTORecord getData(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);          
	}
	public List getListData(String query, List whereData) throws DAOException{	
		return super.findList(query, whereData.toArray());
	}
	public List getListData(String queryCode,Object[] objs) throws DAOException{
	return super.findList(queryCode, objs);	
	}
	public int requestupdateData(String queryCode, Object[] objs) throws DAOException{	   	 	
		return super.updateData(queryCode,objs);
	}
	
	/**
     * YJK
     * 생산통제 B열연 압연지시 전문을 수신하여 해당정보를 가져온다. 
     *
     * @param String	: 조업공장구분 
     * @param String	: 장입지시From
     * @param String	: 장입지시To
     *
     * @return List 압연지시 대상재 
     * @throws DAOException
     */
	public List getBSlabWorkList(	String sPtopPlntGp,
									String sChgWoFrPnt,
									String sChgWoToPnt) throws DAOException
	{	
		/*
		
SELECT 
		    C.STL_NO,                   -- 재료번호
		    C.PTOP_PLNT_GP,             -- 조업공장구분
		    C.CT_WO_WRK_STAT_GP,        -- 생산통제지시작업상태구분
		    C.CT_MILL_SPEC_WRK_STAT_GP, -- 생산통제사양작업상태구분
		    C.SLAB_NO,                  -- Slab번호
		    C.PLAN_SLAB_NO,             -- 예정Slab번호
		    C.YD_CHG_NO,                -- 야드장입순위
		    C.ROLL_UNIT_NAME,           -- ROLL단위명    
		    C.REFUR_CHG_LOT_NO,         -- 가열로장입Lot번호
		    C.L_CHG_LOT_NO,             -- 대장입Lot번호
		    C.LOT_IN_SLAB_PRIOR,        -- Lot내Slab순위
		    C.REFUR_CHG_PLN_SERNO,      -- 가열로장입예정일련번호
		    C.HCR_GP,                   -- HCR구분
		    C.SLAB_T,                   -- Slab두께
		    C.SLAB_W,                   -- Slab폭
		    C.SLAB_LEN,                 -- Slab길이
		    C.SLAB_WT,                  -- Slab중량
		    C.CUST_CD,                  -- 고객코드
		    C.ORD_GP,                   -- 수주구분
		    C.ITEMNAME_CD,              -- 품명코드
		    C.ARRIVAL_DUE_DATE,         -- 인도기한일
		    C.PROD_DUE_DATE,            -- 생산기한일
		    C.ORD_YEOJAE_GP,            -- 주문여재구분
		    NVL(C.REPRESENT_ORD_NO, '') ||
	        NVL(C.REPRESENT_ORD_DTL, '') 
	        AS PRODUC_NO,               -- 제작번호행번
		    C.COIL_NO,                  -- 코일번호
		    TO_CHAR(C.REFUR_CHG_ABLE_DT, 'YYYYMMDDHH24MISS') 
		    AS MILL_PLAN_DDTT,          -- 압연 예정 일시
		    C.BUY_SLAB_NO               -- 구입Slab번호
            -- 확인용 --
		    --E.CT_MILL_SPEC_WRK_STAT_GP,   -- 생산통제사양작업상태구분
            --E.CT_MILL_SCH_WRK_STAT_GP,    -- 생산통제스케줄작업상태구분 
            -- C.REFUR_CHG_PLN_SERNO,
            -- E.REFUR_CHG_PLN_SERNO
		FROM USRCTA.TB_CT_L_HRMILLWO C  	 -- CT_열연압연지시
		   ,(SELECT CHG_WO_FR_PNT AS CHG_WO_FR_PNT
		           ,CHG_WO_TO_PNT AS CHG_WO_TO_PNT
		      FROM USRCTA.TB_CT_J_MILLWOIDX
		     WHERE CT_RCV_SEQ = (SELECT MAX(CT_RCV_SEQ) AS MAX_CT_RCV_SEQ
		                           FROM USRCTA.TB_CT_J_MILLWOIDX
		                          WHERE PTOP_PLNT_GP= :GP ) 
		       AND PTOP_PLNT_GP= :GP) D,
               TB_CT_K_HRMILLSPEC     E	 -- CT_열연압연사양
		WHERE C.REFUR_CHG_PLN_SERNO >=D.CHG_WO_FR_PNT
		  AND C.REFUR_CHG_PLN_SERNO <=D.CHG_WO_TO_PNT 
		  AND C.PTOP_PLNT_GP= :GP
		  AND C.CT_MILL_SPEC_WRK_STAT_GP IN ('3','4')   -- 생산통제사양작업상태구분
		  AND E.CT_MILL_SPEC_WRK_STAT_GP IN ('3','4')   -- 생산통제사양작업상태구분
          AND E.CT_MILL_SCH_WRK_STAT_GP  IN ('3','4')   -- 생산통제스케줄작업상태구분 
          AND E.STL_NO = C.STL_NO 
		ORDER BY E.REFUR_CHG_PLN_SERNO 
		*/
 
		String  queryCode = "ym.ilkwan.ilkwanDAO.getBSlabWorkList";	
		Object[] params = {sPtopPlntGp,sPtopPlntGp,sPtopPlntGp};			
		return super.findList(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * * @param String	: 예정Slab번
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo(String sPlanSlabNo,
									String sStockId,
									String sStockMoveTerm) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE tb_ym_stock
		SET	
			stock_id 		= :stock_id1,
			stock_move_term = :stock_move_term,
			modifier   = 'CSCUT',
		 	mod_ddtt   = sysdate     
		WHERE stock_id = :stock_id2
		*/
 
		String  queryCode = "ym.ilkwan.ilkwanDAO.updateStockTransInfo";	
		Object[] params = {sStockId,sStockMoveTerm,sPlanSlabNo};	
		return super.updateData(queryCode,params);
	}
   
}


