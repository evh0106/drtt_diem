package com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao;

import java.util.List;
import jspeed.base.record.JDTORecord;
import jspeed.base.log.*;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.common.exception.DAOException;

/**
 * 
 * @(#)CraneSchDAO.java
 * 
 * @version    :
 * @author     : 이봉준
 * @date         : 2005. 7. 20
 *
 * @description :
 * 
 */

public class CraneSchDAO  extends CommonDAO{
	
	private Logger log = null; 
	
	public CraneSchDAO()
	{
		log = LogService.getInstance().getLogServiceContext().getLogger( "ym" );
	}
	/**
     * 해당 재료번호에 해당하는 재료의 변경이력정보를 가져와 LIST로 반환한다.
     * 
     * @param 야드구분, 동구분, 스판구분, 소재구분
     * @return List
     * @throws 
     */
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
     * 스케쥴작업을 하기위해 작업예약정보와 저장품정보를 가져온다.
     *
     * @param String	: 작업예약ID
     *
     * @return List 작업예약 및 저장품정보
     * @throws DAOException
     */
	public List getCoilBookedStockList(String sWbookId) throws DAOException
	{	
		/*
		SELECT 
		       a.wbook_id,			-- 작업예약 ID
			   a.yd_gp,	   	  		-- 야드구분 yd_gp
			   a.bay_gp,	   	  	-- 동구분   bay_gp
			   a.sch_work_kind,		-- SCH CODE
			   b.stock_id,			-- 저장품ID
			   b.stock_item,		-- 저장품품목
			   b.stock_stat,		-- 저장품상태
			   b.stock_move_term,	-- 저장품이동조건(이동조건이 발생할 경우에 등록하고 작업완료시 삭제)
			   b.shear_supply_seq,  -- 정정지시순번    
			   c.stack_col_gp,
			   c.stack_bed_gp,
			   c.stack_layer_gp
		FROM tb_ym_wbook a,
			 tb_ym_stock b,
			 tb_ym_stacklayer c,
			 tb_ym_stackcol d
		WHERE a.wbook_id 	= :wbook_id
		AND	  a.wbook_id 	= b.wbook_id
		AND   b.stock_id    = c.stock_id
		AND   c.stack_layer_stat in ('S','U','L')
		AND   c.stack_col_gp = d.stack_col_gp
		AND   a.yd_gp        = d.yd_gp
		AND   a.bay_gp       = d.bay_gp
		ORDER BY b.shear_supply_seq,
		         c.stack_col_gp,        -- 적치단 상단,적치번지(빠른) 순
		         c.stack_layer_gp desc,
		         c.stack_bed_gp 
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilBookedStockList";	
		Object[] params = {sWbookId};			
		return super.findList(queryCode,params);
	}
	 /**
     * YJK
     * 스케쥴작업을 하기위해 작업예약정보와 저장품정보를 가져온다.
     *
     * @param String	: 작업예약ID
     *
     * @return List 작업예약 및 저장품정보
     * @throws DAOException
     */
	public List getCoilBookedStockList2(String sWbookId) throws DAOException
	{	
		/*
		SELECT 
		       a.wbook_id,			-- 작업예약 ID
			   a.yd_gp,	   	  		-- 야드구분 yd_gp
			   a.bay_gp,	   	  	-- 동구분   bay_gp
			   a.sch_work_kind,		-- SCH CODE
			   b.stock_id,			-- 저장품ID
			   b.stock_item,		-- 저장품품목
			   b.stock_stat,		-- 저장품상태
			   b.stock_move_term,	-- 저장품이동조건(이동조건이 발생할 경우에 등록하고 작업완료시 삭제)
			   b.shear_supply_seq,  -- 정정지시순번    
			   c.stack_col_gp,
			   c.stack_bed_gp,
			   c.stack_layer_gp
		FROM tb_ym_wbook a,
			 tb_ym_stock b,
			 tb_ym_stacklayer c,
			 tb_ym_stackcol d
		WHERE a.wbook_id 	= :wbook_id
		AND	  a.wbook_id 	= b.wbook_id
		AND   b.stock_id    = c.stock_id
		AND   c.stack_layer_stat in ('S','U','L')
		AND   c.stack_col_gp = d.stack_col_gp
		AND   a.yd_gp        = d.yd_gp
		AND   a.bay_gp       = d.bay_gp
		ORDER BY b.shear_supply_seq,
		         c.stack_col_gp,        -- 적치단 상단,적치번지(빠른) 순
		         c.stack_layer_gp desc,
		         c.stack_bed_gp 
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilBookedStockList2";	
		Object[] params = {sWbookId};			
		return super.findList(queryCode,params);
	}
	
	/**
     * YJK
     * 위치정보를 가지고 스케쥴 등록에 필요한 정보를 가져온다.
     *
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단
     *
     * @return JDTORecord 저장품위치정보
     * @throws DAOException
     */			
	public JDTORecord getCoilMainStockFromLoc(String sStackColGp,
	    								      String sStackBedGp,
	    								      String sStackLayerGp) throws DAOException
	{	
		/*
		Ver4.3--
		SELECT
			 'M' as gbn,					 	  -- 주/보조작업	
			 c.yd_gp,	   	  		  			  -- 야드구분 yd_gp
			 c.bay_gp,	   	  		  		  	  -- 동구분   bay_gp
			 c.sect_gp,	   	  		  		  	  -- SPAN구분 sect_gp
			 c.col_gp,	   	  		  		  	  -- 열구분 col_gp
			 c.stack_col_gp,					  -- 적치열구분
			 c.stack_col_usage_cd,		 		  -- 적치열용도 Code
			 c.stack_col_active_stat,	 		  -- 적치열활성상태
			 b.stack_bed_gp,			 		  -- 적치열Bed 구분
			 b.stack_bed_active_stat,	 		  -- 적치열Bed 활성상태
			 a.stack_layer_gp,			 		  -- 적치단 구분
			 a.stack_layer_active_stat, 		  -- 적치단 활성상태
			 a.stack_layer_stat,		 		  -- 적치단 상태
			 d.stock_id,						  -- 저장품ID
			 d.stock_item,					  	  -- 저장품품목
			 d.stock_stat,					  	  -- 저장품상태
			 d.stock_move_term,				  	  -- 저장품이동조건(이동조건이 발생할 경우에 등록하고 작업완료시 삭제)
			 d.car_card_no,						  -- 차량 CARD 번호	
			 d.carunload_put_loc,				  -- 하차 PUT 위치
			 e.wbook_id,		   	  		 	  -- 작업예약 ID
			 e.sch_work_kind,		   	  		  -- SCH CODE
			 e.sch_work_loc_decision_method,	  -- SCH 작업위치결정방법
			 e.crane_word_put_loc,			  	  -- Crane 작업위치 Put 위치
			 e.wbook_ddtt,						  -- 작업예약일시
			 e.wbook_duty,						  -- 작업예약근	
			 e.wbook_party						  -- 작업예약조	
		FROM tb_ym_stacklayer a,
			 tb_ym_stacker    b,	
			 tb_ym_stackcol   c,
			 tb_ym_stock      d,
			 tb_ym_wbook      e
		WHERE a.stack_col_gp     = :stack_col_gp
		AND   a.stack_bed_gp     = :stack_bed_gp
		AND   a.stack_layer_gp   = :stack_layer_gp
		--AND   a.stack_layer_stat = 'S' -- 스케쥴수행예정
		AND   a.stack_layer_stat in ('S','U','L')
		AND   a.stack_col_gp = b.stack_col_gp
		AND   a.stack_bed_gp = b.stack_bed_gp
		AND   a.stack_col_gp = c.stack_col_gp
		AND   a.stock_id     = d.stock_id
		AND   d.wbook_id     = e.wbook_id
		*/
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilMainStockFromLoc";	
		Object[] params = {sStackColGp,sStackBedGp,sStackLayerGp};						
		return super.findByPrimaryKey(queryCode,params);
	}
	/**
     * 2008.01.09 이정훈
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmFrToInfo(String sStockId) throws DAOException{    	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmFrToInfo_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
    
    
	/**
	 * PIDEV
     * 2008.01.09 이정훈
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmFrToInfoPI(String sStockId) throws DAOException{    	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmFrToInfo_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}    
    
	/**
     * 2008.01.09 이정훈
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmFrToInfo2(String sStockId) throws DAOException{    	 		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmFrToInfo2_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
    
	/**
	 * 임가공 PIDEV
     * 2008.01.09 이정훈
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmFrToInfo2PI(String sStockId) throws DAOException{    	 		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmFrToInfo2_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}    
    
	/**
     * YJK
     * 위치정보를 가지고 스케쥴 등록에 필요한 정보를 가져온다.
     *
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단
     *
     * @return JDTORecord 저장품위치정보
     * @throws DAOException
     */			
	public JDTORecord getCoilSubStockFromLoc(String sStackColGp,
	    								     String sStackBedGp,
	    								     String sStackLayerGp) throws DAOException
	{	
		/*
		SELECT
			 'S' as gbn,					 	  -- 주/보조작업	
			 c.yd_gp,	   	  		  			  -- 야드구분 yd_gp
			 c.bay_gp,	   	  		  		  	  -- 동구분   bay_gp
			 c.sect_gp,	   	  		  		  	  -- SPAN구분 sect_gp
			 c.col_gp,	   	  		  		  	  -- 열구분 col_gp
			 c.stack_col_gp,					  -- 적치열구분
			 c.stack_col_usage_cd,		 		  -- 적치열용도 Code
			 c.stack_col_active_stat,	 		  -- 적치열활성상태
			 b.stack_bed_gp,			 		  -- 적치열Bed 구분
			 b.stack_bed_active_stat,	 		  -- 적치열Bed 활성상태
			 a.stack_layer_gp,			 		  -- 적치단 구분
			 a.stack_layer_active_stat, 		  -- 적치단 활성상태
			 a.stack_layer_stat,		 		  -- 적치단 상태
			 d.stock_id,						  -- 저장품ID
			 d.stock_item,					  	  -- 저장품품목
			 d.stock_stat,					  	  -- 저장품상태
			 d.stock_move_term,				  	  -- 저장품이동조건(이동조건이 발생할 경우에 등록하고 작업완료시 삭제)
			 e.wbook_id,		   	  		 	  -- 작업예약 ID
			 e.sch_work_kind,		   	  		  -- SCH CODE
			 e.sch_work_loc_decision_method,	  -- SCH 작업위치결정방법
			 e.crane_word_put_loc,			  	  -- Crane 작업위치 Put 위치
			 e.wbook_ddtt,						  -- 작업예약일시
			 e.wbook_duty,						  -- 작업예약근	
			 e.wbook_party						  -- 작업예약조	
		FROM tb_ym_stacklayer a,
			 tb_ym_stacker    b,	
			 tb_ym_stackcol   c,
			 tb_ym_stock      d,
			 tb_ym_wbook      e
		WHERE a.stack_col_gp     = :stack_col_gp
		AND   a.stack_bed_gp     = :stack_bed_gp
		AND   a.stack_layer_gp   = :stack_layer_gp
		AND   a.stack_layer_stat in ('L','S','U','P')
		AND   a.stack_col_gp = b.stack_col_gp
		AND   a.stack_bed_gp = b.stack_bed_gp
		AND   a.stack_col_gp = c.stack_col_gp
		AND   a.stock_id     = d.stock_id
		AND   d.wbook_id     = e.wbook_id(+)
		*/
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilSubStockFromLoc";	
		Object[] params = {sStackColGp,sStackBedGp,sStackLayerGp};						
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 작업예약 저장품정보에 대한 CRANE 을 할당한다. 
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴코드
     *
     * @return JDTORecord 저장품위치정보
     * @throws DAOException
     */			
	public JDTORecord getCoilCraneInfo(String sYdGp,
									   String sBayGp,
									   String sSchWorkKind) throws DAOException
	{	
		/*
		SELECT
			  a.sch_rule_id	as crane_sch_rule_id, -- 스케쥴 기준 ID
			  decode(a.sch_rule_active_stat,
			         'A', a.sch_rule_crane_no, 
			  		 'B', sch_rule_alter_crane_no,
			  		  a.sch_rule_crane_no) as select_crane_no,
			  decode(a.sch_rule_active_stat,
			         'A', a.sch_rule_wprefer,  
			  		 'B', a.sch_rule_alter_wprefer,  
			  		 a.sch_rule_wprefer)  as select_wprefer
		FROM tb_ym_schrule    a
		WHERE a.yd_gp			   = :yd_gp
		AND   a.bay_gp			   = :bay_gp
		AND   a.sch_work_kind	   = :sch_work_kind
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilCraneInfo";	
		Object[] params = {sYdGp,sBayGp,sSchWorkKind};	
		return super.findByPrimaryKey(queryCode, params);
	}
	
	
	/**
     * YJK
     *
     * 작업예약 저장품정보에 대한 CRANE 을 할당한다. 
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴코드
     *
     * @return JDTORecord 저장품위치정보
     * @throws DAOException
     */			
	public JDTORecord getConvertCraneInfo(String sYdGp,
									   String sBayGp,
									   String sSchWorkKind) throws DAOException
	{	
		/*
		SELECT
			  sch_rule_id	as crane_sch_rule_id, -- 스케쥴 기준 ID
			  sch_rule_active_stat,
			  sch_rule_crane_no, 
			  sch_rule_alter_crane_no
		FROM tb_ym_schrule   
		WHERE yd_gp			   = :yd_gp
		AND   bay_gp		   = :bay_gp
		AND   sch_work_kind	   = :sch_work_kind
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getConvertCraneInfo";	
		Object[] params = {sYdGp,sBayGp,sSchWorkKind};	
		return super.findByPrimaryKey(queryCode, params);
	}
	
	
	/**
     * YJK
     * 주작업이면 저장품이동경로 TABLE 과 위치검색 TABLE 에서
	 * 적치열 정보를 가져온다. 
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 적치열용도코드
     * @param String	: SCH CODE
     * @param String	: 저장품이동조건
     *
     * @return List	: 적치열
     * @throws DAOException
     */			
	public JDTORecord getCoilColLocMainList(String sYdGp,				//야드구분
								 		    String sBayGp,			//동구분
										    String sStackColUsageCd,	//적치열용도코드
										    String sSchWorkKind,		//SCH CODE
										    String sStockMoveTerm		//저장품이동조건
										   )throws DAOException
		{	
		/*
		SELECT
			 COUNT(*) AS COUNT       
		FROM tb_ym_stockmoveroute a,
			 tb_ym_locsearch	  b
		WHERE  a.yd_gp 			  	= :yd_gp				-- 야드구분(1)
		AND    a.bay_gp 		  	= :bay_gp				-- 동구분(E)
		AND    a.stack_col_usage_cd = :stack_col_usage_cd	-- 적치열용도코드(CS)   
		AND    a.sch_work_kind 		= :sch_work_kind		-- SCH CODE(CCTO)
		AND    a.stock_move_term like :stock_move_term		
		AND    a.stock_move_route_stat = 'A'
		AND    a.stock_move_route_id= b.stock_move_route_id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilColLocMainList";	
		Object[] params = {sYdGp,sBayGp,sStackColUsageCd,sSchWorkKind,sStockMoveTerm};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 보조작업이면 저장품위치정보를 가지고 적치열 TABLE에서 
	 * 적치열 정보를 가져온다. 
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: SPAN구분
     * @param String	: 열구분
     *
     * @return List	: 적치열
     * @throws DAOException
     */			  
	public List getCoilColLocSubList_01(String sYdGp,			//야드구분
									    String sBayGp,			//동구분
									    String sSectGp,			//SPAN구분
									    String sColGp,			//열구분
									    String sStackColGp
									    )throws DAOException
	{	
		/*
		SELECT 
			   A.STACK_COL_GP,
			   A.YD_GP,
			   A.BAY_GP,
			   A.SECT_GP,
			   A.COL_GP,
		   	   A.BY_SECT_GP,
			   A.BY_COL_GP
			
		FROM (
			   SELECT 
			   	STACK_COL_GP,
			   	YD_GP,
			   	BAY_GP,
			   	SECT_GP,
			   	COL_GP,
			   	ABS(TO_NUMBER(SECT_GP) - TO_NUMBER(:SECT_GP)) AS BY_SECT_GP,
			   	ABS(TO_NUMBER(COL_GP)  - TO_NUMBER(:COL_GP))  AS BY_COL_GP
			   FROM TB_YM_STACKCOL
			   WHERE YD_GP 	= :YD_GP
			   AND   BAY_GP 	= :BAY_GP
			   AND   STACK_COL_USAGE_CD = (SELECT STACK_COL_USAGE_CD FROM TB_YM_STACKCOL WHERE STACK_COL_GP = :STACK_COL_GP)
			   AND   STACK_COL_ACTIVE_STAT = 'O'
			   ORDER BY BY_SECT_GP,
			   	BY_COL_GP,	  	 
			   	COL_GP 
	      ) A		
		WHERE ROWNUM < 10
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilColLocSubList_01";	
		Object[] params = {sSectGp,sColGp,sYdGp,sBayGp,sStackColGp};	
		return super.findList(queryCode,params);
	}
	
	public List getCoilColLocSubList_02( String sYdGp,			//야드구분
								    String sBayGp,			//동구분
								    String sSectGp,			//SPAN구분
								    String sColGp,			//열구분
								    String sStackColGp
								    )throws DAOException
	{	
		/*
		SELECT 
			STACK_COL_GP,
			YD_GP,
			BAY_GP,
			SECT_GP,
			COL_GP,
			TO_CHAR(ABS(TO_NUMBER(SECT_GP) - TO_NUMBER(:SECT_GP))) AS BY_SECT_GP,
			TO_CHAR(ABS(TO_NUMBER(COL_GP)  - TO_NUMBER(:COL_GP)))  AS BY_COL_GP
		FROM TB_YM_STACKCOL
		WHERE YD_GP 	       = :YD_GP
		AND   BAY_GP 	       = :BAY_GP
		AND   STACK_COL_GP LIKE SUBSTR(:STACK_COL_GP,0,4)||'%'
		AND   ABS(TO_NUMBER(COL_GP)  - TO_NUMBER(SUBSTR(:STACK_COL_GP,5,2))) < 4
		AND   STACK_COL_ACTIVE_STAT  = 'O'
		ORDER 	BY BY_SECT_GP,
				BY_COL_GP,	  	 
				COL_GP 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilColLocSubList_02";	
		Object[] params = {sSectGp,sColGp,sYdGp,sBayGp,sStackColGp,sStackColGp};	
		return super.findList(queryCode,params);
	}
	
	/**
     * YJK
     * 해당 작업예약의 저장품의 To 적치단정보를 가져온다.
     * 	- 적치 활성상태 = 'O'
     *	- 적치단 상태	= 'E'
     *
     * @param String	: 적치열
     *
     * @return List 
     * @throws DAOException
     */			
	public List getCoilBedLocDetailList(String sStackColGp)throws DAOException
										 
	{	
		/* 사용안함.
		Ver4.5--
		  SELECT    A.COL                AS TO_STACK_COL_GP, 
    		        A.BED                AS TO_STACK_BED_GP, 
    		        A.LAYER              AS TO_STACK_LAYER_GP,
    		        A.LEFT_BED           AS LEFT_STACK_BED_GP,
    		        A.LEFT_LAYER         AS LEFT_STACK_LAYER_GP,
    		        A.LEFT_STATE         AS LEFT_STACK_LAYER_STAT,
    		        C.STOCK_MOVE_TERM	 AS LEFT_STOCK_MOVE_TERM,
    		        A.RIGHT_BED          AS RIGHT_STACK_BED_GP,
    		        A.RIGHT_LAYER        AS RIGHT_STACK_LAYER_GP,
    		        A.RIGHT_STATE        AS RIGHT_STACK_LAYER_STAT,
    		        D.STOCK_MOVE_TERM	 AS RIGHT_STOCK_MOVE_TERM
	                
	      FROM ( 
	             SELECT -- 좌/우 코일 정보 조회
	                    A.COL, 
	                    A.BED, 
	                    A.LAYER, 
	                    A.PITCH, 
	                    A.LEFT_BED, 
	                    A.LEFT_LAYER,   
	                    A.YD_GP                     AS YD_GP,
	                    A.BAY_GP                    AS BAY_GP,
	                    A.USE_CD                    AS USE_CD,
	                    B.STACK_LAYER_ACTIVE_STAT   AS LEFT_ACTIVE,  
	                    B.STACK_LAYER_STAT          AS LEFT_STATE,  
	                    B.STOCK_ID                  AS LCOIL_ID,
	                    A.RIGHT_BED, 
	                    A.RIGHT_LAYER, 
	                    C.STACK_LAYER_ACTIVE_STAT   AS RIGHT_ACTIVE, 
	                    C.STACK_LAYER_STAT          AS RIGHT_STATE, 
	                    C.STOCK_ID                  AS RCOIL_ID
	             FROM ( 
	                    SELECT   +INDEX_ASC(A PK_YM_STACKLAYER) 
	                           A.STACK_COL_GP           AS COL, 
	                           A.STACK_BED_GP           AS BED, 
	                           A.STACK_LAYER_GP         AS LAYER, 
	                           B.STACK_COL_BED_PITCH    AS PITCH,
	                           B.YD_GP                  AS YD_GP,
	                           B.BAY_GP                 AS BAY_GP,
	                           B.STACK_COL_USAGE_CD     AS USE_CD,
	                           DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   - 1, 2, '0'),
	                                                    '02', A.STACK_BED_GP) AS LEFT_BED, 
	                           DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
	                                                    '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS LEFT_LAYER,
	                           DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0'),
	                                                    '02', LPAD(TO_NUMBER(A.STACK_BED_GP)   + 1, 2, '0')) AS RIGHT_BED,
	                           DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
	                                                    '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS RIGHT_LAYER
	                    FROM TB_YM_STACKLAYER A, TB_YM_STACKCOL B
	                    WHERE A.STACK_COL_GP            =  :STACK_COL_GP
	                      AND A.STACK_LAYER_GP          IN ('01','02')
	                      AND A.STACK_LAYER_ACTIVE_STAT = 'O'
	                      AND A.STACK_LAYER_STAT        = 'E'
	                      AND A.STACK_COL_GP            = B.STACK_COL_GP
	                      -- 적치대 ( 외경, 중량, 폭 조건절 향후 추가 )
	                  ) A, 
	                  TB_YM_STACKLAYER B, 
	                  TB_YM_STACKLAYER C
	             WHERE A.COL          = B.STACK_COL_GP(+) 
	               AND A.LEFT_BED     = B.STACK_BED_GP(+) 
	               AND A.LEFT_LAYER   = B.STACK_LAYER_GP(+)
	         	   AND A.COL          = C.STACK_COL_GP(+) 
	         	   AND A.RIGHT_BED    = C.STACK_BED_GP(+) 
	         	   AND A.RIGHT_LAYER  = C.STACK_LAYER_GP(+)
	           ) A, 
	           TB_YM_STOCK C, 
	           TB_YM_STOCK D
	      WHERE A.LCOIL_ID = C.STOCK_ID(+) 
	        AND A.RCOIL_ID = D.STOCK_ID(+)
	      ORDER BY  A.COL, 
		            A.BED,
		            A.LAYER 
		*/
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilBedLocDetailList";	
		Object[] params = {sStackColGp};	
		return super.findList(queryCode, params);
	}
		
	/**
	 * YJK
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Insert한다.  
     *
     * @param listData 입력할값
     *
     * @return int 
     * @throws DAOException
     */		
	public int insertScheduleInfo(List listData) throws DAOException
	{
               		
		/* 
		INSERT INTO TB_YM_SCH
		(
		 sch_id, stock_id, sch_rule_id, yd_gp, bay_gp, sch_work_equip_no, 
		 sch_work_stat, sch_wprefer, sch_work_kind, sch_work_aid_yn, 
		 sch_work_grip_lot_yn, crane_word_up_loc, wbook_loc_decision_method,
		 crane_word_put_loc, sch_work_car_no, sch_wdemand_type, wbook_sch_act_ddtt, 
		 sch_wdemand_ddtt, sch_wdemand_duty,sch_wdemand_party, wbook_id, car_card_no,register, reg_ddtt, del_yn
		)
		VALUES
		(
		 to_char(sysdate,'YYYYMMDDHH24MI')||LPAD(YM_SCH_SEQ.nextval,6,'0'), 
		 :stock_id, :sch_rule_id, :yd_gp, :bay_gp, :sch_work_equip_no, 
		 :sch_work_stat, :sch_wprefer, :sch_work_kind, :sch_work_aid_yn, 
		 :sch_work_grip_lot_yn, :crane_word_up_loc, :wbook_loc_decision_method,
		 :crane_word_put_loc, :sch_work_car_no, :sch_wdemand_type, :wbook_sch_act_ddtt, 
		 :sch_wdemand_ddtt, :sch_wdemand_duty, :sch_wdemand_party, :wbook_id, :car_card_no, :register, sysdate, :del_yn
		)      
		*/
	 	if(listData == null) {
	 		throw new DAOException("Input Data is NULL");
	 	}
	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertScheduleInfo";			
		return super.insertData(queryCode,listData.toArray());
	}
	
	/**
     * YJK
     * 저장품ID 를 가지고 적치단정보를 가져온다.
     * 
     * @param String	: 저장품ID
     * @param String	: 적치단 활성상태
     * @param String	: 적치단 상태
     * @return 
     * @throws DAOException
     */		
    public JDTORecord getStackLayerListWithStockId(String sStockId,
    										   	   //String sStackLayerActiveStat,
    										   	   String sStackLayerStat) throws DAOException{
		/*
		SELECT 
			   b.stack_col_gp							 		   as cur_stack_col_gp,
			   decode(to_number(b.stack_bed_gp) - 1, 
			   		  0, null, 
					  lpad(to_number(b.stack_bed_gp)-1,2,'0')) 	   as pre_stack_bed_gp,  
			   decode(b.stack_layer_gp, '01','02', null) 		   as pre_stack_layer_gp,
			   
			   b.stack_bed_gp	    							   as cur_stack_bed_gp,		  
			   b.stack_layer_gp 						  		   as cur_stack_layer_gp,
			   
			   case
			   	   when	 to_number(b.stack_bed_gp) >=	
				   		 (
				   		  SELECT to_number(max(k.stack_bed_gp)) 
						  FROM tb_ym_stacklayer k
						  WHERE  k.stack_col_gp = b.stack_col_gp
						  ) 
				   then null
				   else  b.stack_bed_gp
			   end 		 			   		  				  	   as back_stack_bed_gp,
			   decode(b.stack_layer_gp, '01','02', null) 		   as back_stack_layer_gp
			    	
		FROM  tb_ym_stacklayer b
		WHERE b.stock_id 	   		    = :stock_id
		--AND   b.stack_layer_active_stat = :stack_layer_active_stat
		--AND   b.stack_layer_stat 		= :stack_layer_stat
		AND   b.stack_layer_stat 		in( :stack_layer_stat,'U','L')
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerListWithStockId";	
		Object[] params = {sStockId,sStackLayerStat};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
	
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////
    /**
     * YJK
     * 실적전문의 해당 SCHEDULE ID 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 설비종류
     * @param String	: 설비번호
     * @param String	: 저장품ID
     * @param String	: 스케쥴코드
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSlabTInfo(String sYard_Id,
			    				   String sBay_Gp,
			    				   String sEquip_Kind, //조건에서 제외
			    				   String sEquip_No,
			    				   String sSlab_No,
			    				   String sSch_Code) throws DAOException{
		/*
		Ver2.0--
		SELECT
		    a.sch_id,
		    a.wbook_id,
		    a.stock_id,
		    a.crane_word_up_loc,
		    a.crane_word_put_loc
		FROM tb_ym_sch a,
		    (    
		    SELECT 
		           wbook_id,
		           substr(crane_word_up_loc,1,8)||
		           lpad(substr(crane_word_up_loc,9)-1,2,'0')
		           as crane_word_up_loc,
		           substr(crane_word_put_loc,1,8)||
		           lpad(substr(crane_word_put_loc,9)-1,2,'0')
		           as crane_word_put_loc
		    FROM tb_ym_sch 
		    WHERE yd_gp   			= :yd_gp
		    And   bay_gp  			= :bay_gp
		    AND   sch_work_equip_no = :equip_no
		    AND   stock_id 			= :stock_id
		    AND   sch_work_kind 	= :sch_work_kind
		    )b
		WHERE a.wbook_id 			= b.wbook_id
		AND   a.sch_work_grip_lot_yn= 'G'
		AND   a.crane_word_up_loc 	= b.crane_word_up_loc
		AND   a.crane_word_put_loc 	= b.crane_word_put_loc
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSlabTInfo";	
		Object[] params = {sYard_Id,sBay_Gp,sEquip_No,sSlab_No,sSch_Code};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 실적전문의 해당 SCHEDULE ID 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 설비종류
     * @param String	: 설비번호
     * @param String	: 저장품ID
     * @param String	: 스케쥴코드
     *
     * 설명 : 장입재 스케쥴 대상재에 대해 Grip작업 검색시점에 체크
     *		   Grip대상재가 장입순번이 작으면 Grip대상으로 편성하지 않는다.
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSlabGripInfo_01(String sYard_Id,
				    				  	 String sBay_Gp,
				    				  	 String sEquip_Kind, //조건에서 제외
				    				  	 String sEquip_No,
				    				  	 String sSlab_No,
				    				  	 String sSch_Code) throws DAOException{
		/*	
		Ver3.0--	
		SELECT
		    a.sch_id,
		    a.wbook_id,
		    a.stock_id,
		    a.crane_word_up_loc,
		    a.crane_word_put_loc,
		    c.charge_lot_no
		FROM tb_ym_sch a,
		    (    
		    SELECT 
		           stock_id,
		           substr(crane_word_up_loc,1,8)||
		           lpad(substr(crane_word_up_loc,9)-1,2,'0')
		           as crane_word_up_loc,
		           decode(crane_word_put_loc,'0000000000','0000000000',
		           substr(crane_word_put_loc,1,8)||
		           lpad(substr(crane_word_put_loc,9)+1,2,'0'))
		           as crane_word_put_loc,
		           (Select Charge_Lot_No From Tb_Ym_Stock Where Stock_Id = a.Stock_id) As Charge_Lot_No
		    FROM tb_ym_sch a
		    WHERE yd_gp   			= :yd_gp
		    And   bay_gp  			= :bay_gp
		    AND   sch_work_equip_no = :equip_no
		    AND   stock_id 			= :stock_id
		    AND   sch_work_kind 	= :sch_work_kind
		    )b,
		    tb_ym_stock c 
		WHERE a.crane_word_up_loc 	= b.crane_word_up_loc
		AND   a.crane_word_put_loc 		= b.crane_word_put_loc
		AND   a.stock_id            		= c.stock_id
		AND   decode(substr(a.crane_word_put_loc,3,2),'TC','0',NVL(c.charge_lot_no,'0')) 	
		      >= decode(substr(a.crane_word_put_loc,3,2),'TC','0',NVL(b.charge_lot_no,'0')) 
		      
		SELECT
		    a.sch_id,
		    a.wbook_id,
		    a.stock_id,
		    a.crane_word_up_loc,
		    a.crane_word_put_loc,
		    c.charge_lot_no
		FROM tb_ym_sch a,
		    (    
		    SELECT 
		           stock_id,
		           substr(crane_word_up_loc,1,8)||
		           lpad(substr(crane_word_up_loc,9)-1,2,'0')
		           as crane_word_up_loc,
		           decode(crane_word_put_loc,'0000000000','0000000000',
		           substr(crane_word_put_loc,1,8)||
		           lpad(substr(crane_word_put_loc,9)+1,2,'0'))
		           as crane_word_put_loc,
		           (Select Charge_Lot_No From Tb_Ym_Stock Where Stock_Id = a.Stock_id) As Charge_Lot_No
		    FROM tb_ym_sch a
		    WHERE yd_gp   			= :yd_gp
		    And   bay_gp  			= :bay_gp
		    AND   sch_work_equip_no = :equip_no
		    AND   stock_id 			= :stock_id
		    AND   sch_work_kind 	= :sch_work_kind
		    )b,
		    tb_ym_stock c 
		WHERE a.crane_word_up_loc 	= b.crane_word_up_loc
		AND   a.crane_word_put_loc 		= b.crane_word_put_loc
		AND   a.stock_id            		= c.stock_id
		AND   NVL(c.charge_lot_no,'0') 	>= NVL(b.charge_lot_no,'0') 
		*/

		/*
		Ver2.0--
		SELECT
		    a.sch_id,
		    a.wbook_id,
		    a.stock_id,
		    a.crane_word_up_loc,
		    a.crane_word_put_loc
		FROM tb_ym_sch a,
		    (    
		    SELECT 
		           substr(crane_word_up_loc,1,8)||
		           lpad(substr(crane_word_up_loc,9)-1,2,'0')
		           as crane_word_up_loc,
		           decode(crane_word_put_loc,'0000000000','0000000000',
		           substr(crane_word_put_loc,1,8)||
		           lpad(substr(crane_word_put_loc,9)+1,2,'0'))
		           as crane_word_put_loc
		    FROM tb_ym_sch 
		    WHERE yd_gp   			= :yd_gp
		    And   bay_gp  			= :bay_gp
		    AND   sch_work_equip_no = :equip_no
		    AND   stock_id 			= :stock_id
		    AND   sch_work_kind 	= :sch_work_kind
		    )b
		WHERE a.crane_word_up_loc 	= b.crane_word_up_loc
		AND   a.crane_word_put_loc 	= b.crane_word_put_loc
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSlabGripInfo_01";	
		Object[] params = {sYard_Id,sBay_Gp,sEquip_No,sSlab_No,sSch_Code};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 실적전문의 해당 SCHEDULE ID 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 설비종류
     * @param String	: 설비번호
     * @param String	: 저장품ID
     * @param String	: 스케쥴코드
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSlabGripInfo_02(String sYard_Id,
				    				  	 String sBay_Gp,
				    				  	 String sEquip_Kind, //조건에서 제외
				    				  	 String sEquip_No,
				    				  	 String sSlab_No,
				    				  	 String sSch_Code) throws DAOException{
		/*
		Ver2.0--
		SELECT
		    a.sch_id,
		    a.wbook_id,
		    a.stock_id,
		    a.crane_word_up_loc,
		    a.crane_word_put_loc
		FROM tb_ym_sch a,
		    (    
		    SELECT 
		           substr(crane_word_up_loc,1,8)||
		           lpad(substr(crane_word_up_loc,9)-1,2,'0')
		           as crane_word_up_loc,
		           decode(crane_word_put_loc,'0000000000','0000000000',
		           substr(crane_word_put_loc,1,8)||
		           lpad(substr(crane_word_put_loc,9)-1,2,'0'))
		           as crane_word_put_loc
		    FROM tb_ym_sch 
		    WHERE yd_gp   			= :yd_gp
		    And   bay_gp  			= :bay_gp
		    AND   sch_work_equip_no = :equip_no
		    AND   stock_id 			= :stock_id
		    AND   sch_work_kind 	= :sch_work_kind
		    )b
		WHERE a.sch_work_grip_lot_yn= 'G'
		AND   a.crane_word_up_loc 	= b.crane_word_up_loc
		AND   a.crane_word_put_loc 	= b.crane_word_put_loc
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSlabGripInfo_02";	
		Object[] params = {sYard_Id,sBay_Gp,sEquip_No,sSlab_No,sSch_Code};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 실적전문의 해당 SCHEDULE ID 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 설비종류
     * @param String	: 설비번호
     * @param String	: 저장품ID
     * @param String	: 스케쥴코드
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSlabGripInfo_03(String sYard_Id,
				    				  	 String sBay_Gp,
				    				  	 String sEquip_Kind, //조건에서 제외
				    				  	 String sEquip_No,
				    				  	 String sSlab_No,
				    				  	 String sSch_Code) throws DAOException{
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSlabGripInfo_03";	
		Object[] params = {sYard_Id,sBay_Gp,sEquip_No,sSlab_No,sSch_Code};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    
    /**
     * YJK
     * 실적전문의 해당 SCHEDULE ID 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 설비종류
     * @param String	: 설비번호
     * @param String	: 저장품ID
     * @param String	: 스케쥴코드
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSchIdInfo(String sYard_Id,
			    				   String sBay_Gp,
			    				   String sEquip_Kind, //조건에서 제외
			    				   String sEquip_No,
			    				   String sCoil_No,
			    				   String sSch_Code) throws DAOException{
		/*
		Ver2.0--
		SELECT 
			  *
		FROM tb_ym_sch 
		WHERE yd_gp   			= :yd_gp
		And   bay_gp  			= :bay_gp
		AND   sch_work_equip_no = :equip_no
		AND   stock_id 			= :stock_id
		AND   sch_work_kind 	= :sch_work_kind
		ORDER BY sch_work_stat,-- 작업진행상태
				 sch_wprefer,  -- 작업우선순위
				 sch_id        -- Schedule Id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSchIdInfo";	
		Object[] params = {sYard_Id,sBay_Gp,sEquip_No,sCoil_No,sSch_Code};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * Crane 설비상태를 변경한다.
	 * tb_ym_equip Table : work_prog_stat = '1','2','3'
	 * tb_ym_equip Table : wbook_id	 	  = SCH_ID
     * 
     * @param sScheduleId   : 스케쥴ID
     * @param sStat			: 적치상태
     * @return 
     * @throws DAOException
     */		
    public int updateCraneEquipStatFromOrd(String sScheduleId,
    									   String sStat) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_equip 
		SET 
			wprog_stat = :work_prog_stat,	-- 작업진행상태
			wbook_id   = :sch_id, 			-- 스케쥴ID
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE equip_gp = 
			(	
			SELECT 
				   b.equip_gp
			FROM tb_ym_sch a, tb_ym_equip b
			WHERE a.sch_id            = :sch_id
			AND   a.yd_gp 			  = b.yd_gp
			AND   a.bay_gp  		  = b.bay_gp
			AND   a.sch_work_equip_no = b.equip_no
			AND   b.equip_kind = 'CR'
			) 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneEquipStatFromOrd";
		Object[] params = {sStat,sScheduleId,sScheduleId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     * Crane 설비상태를 변경한다.
	 * tb_ym_equip Table : work_prog_stat = '1','2','3'
	 * 
     * @param sScheduleId   : 스케쥴ID
     * @param sStat			: 적치상태
     * @return 
     * @throws DAOException
     */		
    public int updateCraneEquipStatFromUp(String sScheduleId,
    									  String sStat) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_equip 
		SET 
			wprog_stat = :work_prog_stat,	-- 작업진행상태
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE equip_gp = 
			(	
			SELECT 
				   b.equip_gp
			FROM tb_ym_sch a, tb_ym_equip b
			WHERE a.sch_id            = :sch_id
			AND   a.yd_gp 			  = b.yd_gp
			AND   a.bay_gp  		  = b.bay_gp
			AND   a.sch_work_equip_no = b.equip_no
			AND   b.equip_kind = 'CR'
			) 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneEquipStatFromUp";
		Object[] params = {sStat,sScheduleId};	
		return super.updateData(queryCode,params);
	}
	
    /**
     * YJK
     * Crane 작업상태를 변경한다.
	 * tb_ym_sch Table : sch_work_stat = '3'(PUT 수행중)
     *  
     * @param sScheduleId	: 스케쥴ID
     * @param sStat			: 적치상태
     *
     * @return 
     * @throws DAOException
     */		
    public int updateCraneSchStat(String sScheduleId,String sStat) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
		SET 
			sch_work_stat = :sch_work_stat,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE sch_id = :sch_id
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneSchStat";
		Object[] params = {sStat,sScheduleId};	
		return super.updateData(queryCode,params);
    }
    
    /**
     * YJK
     * Crane 작업상태를 변경한다.
	 * tb_ym_sch Table : sch_work_stat = '3'(PUT 수행중)
     *  
     * @param sScheduleId	: 스케쥴ID
     * @param sStat			: 적치상태
     *
     * @return 
     * @throws DAOException
     */		
    public int updateCraneSchStat2(String sydgp ,String sbaygp , String sequipno) throws DAOException{
		/* 
		update USRYMA.TB_YM_SCH b
		set SCH_WPREFER=(case SCH_WPREFER when 0 then (select SCH_RULE_WPREFER 
		                                                 from USRYMA.TB_YM_SCHRULE a
		                                               where a.SCH_RULE_ID=B.SCH_RULE_ID
		                                               ) else SCH_WPREFER end)
		
		WHERE YD_GP=:V_YD_GP
		  and  BAY_GP =:V_BAY_GP
		  AND  SCH_WORK_EQUIP_NO =:V_SCH_WORK_EQUIP_NO
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.updateresetSch";
		Object[] params = {sydgp,sbaygp,sequipno};	
		return super.updateData(queryCode,params);
    }
    
    /**
     * YJK
     * Crane 작업상태를 변경한다.
	 * tb_ym_sch Table : sch_work_stat = '1'(UP 수행중)
     *  
     * @param sStat			: 적치상태
     *
     * @return 
     * @throws DAOException
     */		
    public int updateCraneSchStat_spm2(String sStat) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
		SET 
			sch_work_stat = :sch_work_stat,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE yd_gp='3'
		and sch_work_kind = 'CNLO'
		and SCH_Work_stat = '1'
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneSchStat01";
		Object[] params = {sStat};	
		return super.updateData(queryCode,params);
    }   	
   	/**
     * YJK
     * 스케쥴의 GRIP 유무를 변경한다.
	 * tb_ym_sch Table : SCH_WORK_GRIP_LOT_YN = 'G'(2매 작업)
     *  
     * @param sScheduleId	: 스케쥴ID
     * @param sStat			: GRIP 유무
     *
     * @return 
     * @throws DAOException
     */		
    public int updateGripYnWithSchId(String sScheduleId,String sLotYn) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
		SET 
			SCH_WORK_GRIP_LOT_YN = :SCH_WORK_GRIP_LOT_YN,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE sch_id = :sch_id
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateGripYnWithSchId";
		Object[] params = {sLotYn,sScheduleId};	
		return super.updateData(queryCode,params);
    }
    
   	/**
     * YJK
     * 스케쥴 TABLE CRANE 번호를 수정한다.
	 * tb_ym_sch Table : sch_work_equip_no = 백업크레인번호
     * 
     * @param sScheduleId   : 스케쥴ID
     * @param sCraneNo		: CraneNo
     * @return 
     * @throws DAOException
     */		
    public int updateCraneNoWithSchId(String sScheduleId,
    								  String sCraneNo) throws DAOException{
		/* 
		Ver20060118--
		UPDATE tb_ym_sch 
		SET 
			sch_work_equip_no = :sch_work_equip_no,	-- 작업설비번호
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE sch_id   = :sch_id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneNoWithSchId";
		Object[] params = {sCraneNo,sScheduleId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     * 스케쥴 TABLE 권상위치를 수정한다.
	 * tb_ym_sch Table : crane_word_up_loc = 권상위치
     * 
     * @param sScheduleId   : 스케쥴ID
     * @param sUpLoc		: sUpLoc
     * @return 
     * @throws DAOException
     */		
    public int updateUpLocInfoWithSchId(String sScheduleId,
    								    String sUpLoc) throws DAOException{
		/* 
		Ver20060118--
		UPDATE tb_ym_sch 
		SET 
			crane_word_up_loc = :crane_word_up_loc,	-- 권상위치
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE sch_id   = :sch_id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateUpLocInfoWithSchId";
		Object[] params = {sUpLoc,sScheduleId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     * 스케쥴 TABLE 권하위치를 수정한다.
	 * tb_ym_sch Table : crane_word_up_loc = 권상위치
     * 
     * @param sScheduleId   : 스케쥴ID
     * @param sUpLoc		: sUpLoc
     * @return 
     * @throws DAOException
     */		
    public int updatePutLocInfoWithSchId(String sScheduleId,
    								     String sPutLoc) throws DAOException{
		/* 
		Ver20060118--
		UPDATE tb_ym_sch 
		SET 
			crane_word_put_loc = :crane_word_put_loc,	-- 권하위치
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE sch_id   = :sch_id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updatePutLocInfoWithSchId";
		Object[] params = {sPutLoc,sScheduleId};	
		return super.updateData(queryCode,params);
	}
    
	/**
     * YJK
     * 스케쥴 TABLE 권하위치를 수정한다.
	 * tb_ym_sch Table : crane_word_up_loc = 권상위치
     * 
     * @param sScheduleId   : 스케쥴ID
     * @param sUpLoc		: sUpLoc
     * @return 
     * @throws DAOException
     */		
    public int updatePutLocInfoWithGoodsNo(String sGoodsNo,
    								     String sPutLoc) throws DAOException{
		/* 
		Ver20060118--
		UPDATE tb_ym_sch 
		SET 
			crane_word_put_loc = :crane_word_put_loc,	-- 권하위치
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE stock_id   = :stock_id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updatePutLocInfoWithGoodsNo";
		Object[] params = {sPutLoc,sGoodsNo};	
		return super.updateData(queryCode,params);
	}
    
	/**
     * YJK
     * 스케쥴 TABLE 권하위치를 수정한다.
	 * tb_ym_sch Table : crane_word_up_loc = 권상위치
     * 
     * @param sScheduleId   : 스케쥴ID
     * @param sUpLoc		: sUpLoc
     * @return 
     * @throws DAOException
     */		
    public int updatePutLocInfoWithSchId_01(String sScheduleId,
    								     String sPutLoc) throws DAOException{
		/* 
		Ver20060118--
		UPDATE tb_ym_sch 
		SET 
			crane_word_put_loc = :crane_word_put_loc,	-- 권하위치
			WBOOK_LOC_DECISION_METHOD = 'O',
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate 
			WHERE sch_id   = :sch_id          
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updatePutLocInfoWithSchId_01";
		Object[] params = {sPutLoc,sScheduleId};	
		return super.updateData(queryCode,params);
	}
    
    /**
     * YJK
     * 스케쥴 TABLE 권상위치를 수정한다.
	 * tb_ym_sch Table : crane_word_up_loc = 권상위치
     * 
     * @param sScheduleId   : 스케쥴ID
     * @param sUpLoc		: sUpLoc
     * @return 
     * @throws DAOException
     */		
    public int updatePutLocInfoWbookWithSchId(String sScheduleId,
    								     String sPutLoc) throws DAOException{
		/* 
		Ver20060118--
		update tb_ym_wbook
		set CRANE_WORD_PUT_LOC = :crane_word_put_loc,
		SCH_WORK_LOC_DECISION_METHOD ='O' 
		where wbook_id = (select wbook_id
		from tb_ym_sch 
		WHERE sch_id   = :sch_id
		AND SCH_WORK_AID_YN = 'M')
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updatePutLocInfoWbookWithSchId";
		Object[] params = {sPutLoc,sScheduleId};	
		return super.updateData(queryCode,params);
	}
    /**
     * YJK
     * Crane UP실적 저장품의 적치단정보를 가져온다.
     * 
     * @param sStat			: 스케쥴ID
     * @param sStat			: 적치상태
     * @param sStat			: 적치상태
     *
     * @return 
     * @throws DAOException
     */		
    public JDTORecord getUpStackLayerListWithSchId(String sScheduleId) throws DAOException{
		/*
		Ver20060115--
		SELECT 
			   b.stack_col_gp,
			   b.stack_layer_gp,
			   b.stack_bed_gp,
			   b.stack_layer_x_axis,
			   b.stack_layer_y_axis,
			   b.stack_layer_z_axis
		FROM tb_ym_sch a , tb_ym_stacklayer b
		WHERE a.sch_id 	   		= :sch_id
		AND   a.stock_id 			= b.stock_id
		AND   b.stack_layer_stat 	in('L','U')
		AND   b.stack_col_gp not like '__CR__'
		*/
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getUpStackLayerListWithSchId";	
		Object[] params = {sScheduleId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * Crane PUT실적 저장품의 적치단정보를 가져온다.
     * 
     * @param sStat			: 스케쥴ID
     * @param sStat			: 적치상태
     * @param sStat			: 적치상태
     *
     * @return 
     * @throws DAOException
     */		
    public JDTORecord getPutStackLayerListWithSchId(String sScheduleId) throws DAOException{
		/*
		Ver20060115--
		SELECT 
			   b.stack_col_gp,
			   b.stack_layer_gp,
			   b.stack_bed_gp,
			   b.stack_layer_x_axis,
			   b.stack_layer_y_axis,
			   b.stack_layer_z_axis
		FROM tb_ym_sch a , tb_ym_stacklayer b
		WHERE a.sch_id 	   			= :sch_id
		AND   a.stock_id 			= b.stock_id
		AND   b.stack_layer_stat 	= 'P'
		AND   b.stack_col_gp      	= substr(a.crane_word_put_loc,0,6)
		*/
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getPutStackLayerListWithSchId";	
		Object[] params = {sScheduleId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    
    /**
     * YJK
     * Crane PUT실적 저장품의 적치단정보를 가져온다.
     * 
     * @param sStat			: 스케쥴ID
     * @param sStat			: 적치상태
     * @param sStat			: 적치상태
     *
     * @return 
     * @throws DAOException
     */		
    public JDTORecord getPutStackLayerListWithSchId2(String sScheduleId) throws DAOException{
		/*
		Ver20060115--
		SELECT 
			   b.stack_col_gp,
			   b.stack_layer_gp,
			   b.stack_bed_gp,
			   b.stack_layer_x_axis,
			   b.stack_layer_y_axis,
			   b.stack_layer_z_axis
		FROM tb_ym_sch a , tb_ym_stacklayer b
		WHERE a.sch_id 	   			= :sch_id
		AND   a.stock_id 			= b.stock_id
		AND   b.stack_layer_stat 	= 'P'
		AND   b.stack_col_gp      	= substr(a.crane_word_put_loc,0,6)
		*/
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getPutStackLayerListWithSchId2";	
		Object[] params = {sScheduleId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * Primary Key로 적치열 TABLE 정보를 가져온다.
     * 
	 * @param String	: 적치열
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStackColInfoWithPk(String sStackColGp) throws DAOException{
		/*
		SELECT 
				*
		FROM tb_ym_stackcol
		WHERE stack_col_gp  	= :stack_col_gp
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackColInfoWithPk";	
		Object[] params = {sStackColGp};	
		return super.findByPrimaryKey(queryCode, params);
    }
      
    /**
     * YJK
     * Primary Key로 적치단 TABLE 정보를 가져온다.
     * 
	 * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStackLayerInfoWithPk(String sStackColGp,
    										  String sStackBedGp,	
    										  String sStackLayerGp) throws DAOException{
		/*
		SELECT 
				*
		FROM tb_ym_stacklayer
		WHERE stack_col_gp  	= :stack_col_gp
		AND   stack_bed_gp		= :stack_bed_gp 
		AND   stack_layer_gp	= :stack_layer_gp 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";	
		Object[] params = {sStackColGp,sStackBedGp,sStackLayerGp};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * Primary Key로 적치단 TABLE 정보를 가져온다.
     * 
	 * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getCoilInfo(String scoilno) throws DAOException{
		/*
		SELECT 
				*
		FROM tb_ym_stacklayer
		WHERE stack_col_gp  	= :stack_col_gp
		AND   stack_bed_gp		= :stack_bed_gp 
		AND   stack_layer_gp	= :stack_layer_gp 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilInfo_PIDEV";	
		Object[] params = {scoilno};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * Primary Key로 적치단 TABLE 정보를 가져온다.
     * 
	 * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * 
     * @return 
     * @throws DAOException
     */			
    public List getStackLayerInfoWithBed(String sStackColGp,
    								 String sStackBedGp) throws DAOException{
		return getStackLayerInfoWithBed(sStackColGp,
								    sStackBedGp,
								    ""	);
    }
    
       public List getStackLayerInfoWithBed(String sStackColGp,
    								    String sStackBedGp,
    								    String sOrderBy) throws DAOException{
		/*
		SELECT 
				*
		FROM tb_ym_stacklayer
		WHERE stack_col_gp  	= :stack_col_gp
		AND   stack_bed_gp	= :stack_bed_gp 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithBed";	
		Object[] params = {sStackColGp,sStackBedGp};	
		
		if("L".equals(sOrderBy)){
			
			sOrderBy = 	"AND   Stock_Id Is Not Null "+
						"Order by Stack_Layer_Gp desc";
		}else if("E".equals(sOrderBy)){
			sOrderBy = 	"AND   Stock_Id Is Null "+
						"Order by Stack_Layer_Gp ";	
		}
		
		return super.findList(queryCode, sOrderBy, params);
    }
    
    
    /**
     * YJK
     * 적치단 위치정보 상태를 변경한다.
     *
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * @param String	: 저장품ID      
     * @param String	: 적치상태      
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateCraneStackLayerStat(String sStackColGp,
	    								 String sStackBedGp,
	    								 String sStackLayerGp,
	    								 String sStockId,
	    								 //String sStackLayerActivStat,
	    								 String sStackLayerStat) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STACKLAYER
		SET 
			stock_id				= :stock_id,
			stack_layer_stat	    = :stack_layer_stat,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE stack_col_gp   = :stack_col_gp 
		AND   stack_bed_gp   = :stack_bed_gp 
		AND   stack_layer_gp = :stack_layer_gp 
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
		Object[] params = {sStockId,sStackLayerStat,sStackColGp,sStackBedGp,sStackLayerGp};	
		return super.updateData(queryCode,params);
    }
    
    
    /**
     * hyuksang
     * 적치단 위치정보 상태를 변경한다.
     *
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * @param String	: 저장품ID      
     * @param String	: 적치상태      
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateCraneStackLayerStat1(String sStackColGp,
	    								 String sStackBedGp,
	    								 String sStackLayerGp,
	    								 String sStockId,
	    								 String sStackLayerActivStat,
	    								 String sStackLayerStat) throws DAOException{
    	/*
    	UPDATE TB_YM_STACKLAYER
    	SET 
    		stock_id				= :stock_id,
    		STACK_LAYER_ACTIVE_STAT = :STACK_LAYER_ACTIVE_STAT
    		stack_layer_stat	    = :stack_layer_stat,
    		modifier   = 'SYSTEM',
    	 	mod_ddtt   = sysdate     
    	WHERE stack_col_gp   = :stack_col_gp 
    	AND   stack_bed_gp   = :stack_bed_gp 
    	AND   stack_layer_gp = :stack_layer_gp 
    	*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat1";
		Object[] params = {sStockId,sStackLayerActivStat,sStackLayerStat,sStackColGp,sStackBedGp,sStackLayerGp};	
		return super.updateData(queryCode,params);
    }
    
    
 
    /**
     * YJK
     * 적치단 위치정보 상태를 변경한다.
     *
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * @param String	: 저장품ID      
     * @param String	: 적치상태      
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateCraneStackLayerStat2(String sStackColGp,
	    								 String sStackLayerGp,
	    								 String sStockId,
	    								 String sStackLayerStat) throws DAOException{
		/* 
		UPDATE TB_YM_STACKLAYER
		    SET stack_layer_stat	= :stack_layer_stat,
		        modifier   = 'SYSTEM',
		        mod_ddtt   = sysdate     
		    WHERE stack_col_gp   = :stack_col_gp 
		    AND   STOCK_ID   = :STOCK_ID
		    AND   stack_layer_gp = :stack_layer_gp 
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat2";
		Object[] params = {sStackLayerStat,sStackColGp,sStockId,sStackLayerGp};	
		return super.updateData(queryCode,params);
    }
    /**
     * YJK
     * 적치단 위치정보 상태를 변경한다.
     *
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * @param String	: 저장품ID      
     * @param String	: 적치상태      
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateCraneStackLayerStat_isang(String sStackColGp,
	    								 String sStackBedGp,
	    								 String sStackLayerGp,
	    								 String sStockId,
	    								 //String sStackLayerActivStat,
	    								 String sStackLayerStat) throws DAOException{
		/* 
		Ver4.0--
	UPDATE TB_YM_STACKLAYER
	SET 
		stock_id				= "",
		stack_layer_stat	    = :stack_layer_stat,
		modifier   = 'CN1PB07',
		mod_ddtt   = sysdate     
	WHERE stack_col_gp   = :stack_col_gp 
	AND   stack_bed_gp   = :stack_bed_gp 
	AND   stack_layer_gp = :stack_layer_gp 
	AND   STOCK_ID = :stock_id
	AND   stack_layer_stat = 'P'
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat_isang";
		Object[] params = {sStackLayerStat,sStackColGp,sStackBedGp,sStackLayerGp,sStockId};	
		return super.updateData(queryCode,params);
    }
    /**
     * YJK
     * 작업실적 TABLE에 INSERT
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int insertCraneWrslt(List listData) throws DAOException{

		/* 
		INSERT INTO TB_YM_WRSLT
		(
		 crane_wrslt_id,sch_id,stock_id,equip_gp,sch_work_kind,sch_wprefer,            
		 crane_work_ddtt,crane_work_duty,crane_work_party,crane_word_ddtt,wbook_ddtt,wbook_sch_term,         
		 wbook_sch_act_ddtt,sch_wdemand_ddtt,sch_wdemand_duty,sch_wdemand_party,sch_wdemand_type,
		 crane_wrslt_cd,crane_word_up_loc,crane_word_up_x_axis,crane_word_up_y_axis,   
		 crane_word_up_z_axis,  
		 crane_wrslt_up_x_axis,crane_wrslt_up_y_axis,crane_wrslt_up_z_axis,crane_wrslt_up_loc,     
		 crane_wrslt_up_func,crane_wrslt_up_ddtt,
		 crane_word_put_loc,crane_word_put_x_axis,crane_word_put_y_axis,
		 crane_word_put_z_axis,
		 register,reg_ddtt,del_yn,yd_gp                 
		)
		SELECT 
		     to_char(sysdate,'YYYYMMDDHH24MI')||YM_WRSLT_SEQ.nextval, 
		     a.sch_id,						-- sch_id,스케쥴ID
		     a.stock_id,					-- stock_id,저장품ID
		     d.equip_gp,					-- equip_gp,설비구분
		     a.sch_work_kind,				-- sch_work_kind,스케쥴코드
		     a.sch_wprefer,					-- sch_wprefer,스케쥴작업우선순위            
		     to_char(sysdate,'YYYYMMDDHH24MISS'), -- crane_work_ddtt,크레인작업일자 
		     :crane_work_duty,					-- crane_work_duty,크레인작업근
		     :crane_work_party,					-- crane_work_party,크레인작업조
		     c.wbook_sch_act_ddtt,	        -- crane_word_ddtt,크레인작업지시일시
		     c.wbook_ddtt,					-- wbook_ddtt,작업예약일시
		     c.wbook_sch_term,				-- wbook_sch_term,작업예약스케쥴조건         
		     a.wbook_sch_act_ddtt,			-- wbook_sch_act_ddtt,작업예약스케쥴실행일시
		     a.sch_wdemand_ddtt,			-- sch_wdemand_ddtt,스케쥴작업요구일시
		     a.sch_wdemand_duty,			-- sch_wdemand_duty,스케쥴작업요구근
		     a.sch_wdemand_party,			-- sch_wdemand_party,스케쥴작업요구조
		     a.sch_wdemand_type,			-- sch_wdemand_type,스케쥴작업요구형태
		     a.sch_work_aid_yn,             -- crane_wrslt_cd,크레인작업결과코드
		     a.crane_word_up_loc,			-- crane_word_up_loc,	크레인작업지시UP 위치
		     e.stack_layer_x_axis,			-- crane_word_up_x_axis,크레인작업지시UP X축
		     e.stack_layer_y_axis,			-- crane_word_up_y_axis,크레인작업지시UP Y축   
		     e.stack_layer_z_axis,			-- crane_word_up_z_axis,크레인작업지시UP Z축
		     :crane_wrslt_up_x_axis,		-- 크레인작업결과UP X축
		     :crane_wrslt_up_y_axis,		-- 크레인작업결과UP Y축
		     :crane_wrslt_up_z_axis,		-- 크레인작업결과UP Z축
		     :crane_wrslt_up_loc,			-- 크레인작업결과UP 위치     
		     :crane_wrslt_up_func,			-- 크레인작업결과UP 기능
		     :crane_wrslt_up_ddtt,			-- 크레인작업결과UP 일시
		     a.crane_word_put_loc,			-- crane_word_put_loc,크레인작업지시put 위치
		     f.stack_layer_x_axis,			-- crane_word_put_x_axis,크레인작업지시put X축
		     f.stack_layer_y_axis,			-- crane_word_put_y_axis,크레인작업지시put Y축   
		     f.stack_layer_z_axis,			-- crane_word_put_z_axis,크레인작업지시put Z축
		     :register,                     -- register,
		     sysdate,						-- reg_ddtt,
		     'N',							-- del_yn,
		     :yd_gp							-- 야드구분                       
		FROM tb_ym_sch          a, 
		     tb_ym_stock        b, 
			 tb_ym_wbook        c, 
			 (
			 SELECT yd_gp,
			        bay_gp,
			        equip_no,
			        equip_gp
			 FROM tb_ym_equip
			 WHERE equip_kind = 'CR'
			 )d,
			 (
			 SELECT stack_col_gp||stack_bed_gp||stack_layer_gp as up_loc,
			        stack_layer_x_axis,			
		            stack_layer_y_axis,			
		            stack_layer_z_axis		
			 FROM tb_ym_stacklayer
			 )e,
			 (
			 SELECT stack_col_gp||stack_bed_gp||stack_layer_gp as put_loc,
			        stack_layer_x_axis,			
		            stack_layer_y_axis,			
		            stack_layer_z_axis		
			 FROM tb_ym_stacklayer
			 )f
		WHERE a.sch_id             = :sch_id
		AND   a.stock_id           = b.stock_id
		AND   a.wbook_id           = c.wbook_id(+)
		AND   a.yd_gp 			   = d.yd_gp
		AND   a.bay_gp  		   = d.bay_gp
		AND   a.sch_work_equip_no  = d.equip_no
		AND	  a.crane_word_up_loc  = e.up_loc(+)
		AND	  a.crane_word_put_loc = f.put_loc(+)
		*/
		if(listData == null) {
	 		log.println("Input(listData) is NULL");
			throw new DAOException("Input Data is NULL");
	 	}
	 	
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertCraneWrslt_01";			
		return super.insertData(queryCode,listData.toArray());
	}
    
    /**
     * YJK
     * SCH_ID로 작업실적TABLE 정보를 가져온다.
     * 
	 * @param String	: SCH_ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getWrsltInfoWithSchId(String sSchId) throws DAOException{
		/*
		Ver20060109
		SELECT 
			   *
		FROM TB_YM_WRSLT
		WHERE sch_id = :sch_id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getWrsltInfoWithSchId";	
		Object[] params = {sSchId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 작업실적 TABLE에 UPDATE
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateCraneUpWrslt(List listData) throws DAOException{

		/* 
		Ver20060109
		UPDATE TB_YM_WRSLT
		SET 
		 sch_wdemand_duty		= :sch_wdemand_duty			,	--스케쥴작업요구근
		 sch_wdemand_party		= :sch_wdemand_party		,	--스케쥴작업요구조
		 crane_wrslt_up_x_axis	= :crane_wrslt_up_x_axis	,   --크레인작업결과up X축
		 crane_wrslt_up_y_axis	= :crane_wrslt_up_y_axis	,   --크레인작업결과up Y축
		 crane_wrslt_up_z_axis	= :crane_wrslt_up_z_axis	,   --크레인작업결과up Z축
		 crane_wrslt_up_loc		= :crane_wrslt_up_loc		,   --크레인작업결과up 위치     
		 crane_wrslt_up_func	= :crane_wrslt_up_func	    ,   --크레인작업결과up 기능
		 crane_wrslt_up_ddtt	= :crane_wrslt_up_ddtt	    ,   --크레인작업결과up 일시
		 modifier				= :modifier,
		 mod_ddtt				= sysdate,
		 yd_gp					= :yd_gp               
		WHERE sch_id = :sch_id
		*/ 
		if(listData == null) {
	 		log.println("Input(listData) is NULL");
			throw new DAOException("Input Data is NULL"); 
	 	}
	 	
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneUpWrslt";			
		return super.updateData(queryCode,listData.toArray());
	}
	
	/**
     * YJK
     * 작업실적 TABLE에 UPDATE
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateCranePutWrslt(List listData) throws DAOException{

		/* 
		Ver20060109
		UPDATE TB_YM_WRSLT
		SET 
		 (
		 crane_word_put_loc, 
		 crane_wrslt_put_x_axis,
		 crane_wrslt_put_y_axis,
		 crane_wrslt_put_z_axis,
		 crane_wrslt_put_loc,     
		 crane_wrslt_put_func,
		 crane_wrslt_put_ddtt,
		 modifier,
		 mod_ddtt       
		 )=
		 (
		 SELECT 
		     crane_word_put_loc,
		     :crane_wrslt_put_x_axis	,   --크레인작업결과put X축
		     :crane_wrslt_put_y_axis	,   --크레인작업결과put Y축
		     :crane_wrslt_put_z_axis	,   --크레인작업결과put Z축
		     :crane_wrslt_put_loc		,   --크레인작업결과put 위치     
		     :crane_wrslt_put_func	    ,   --크레인작업결과put 기능
		     :crane_wrslt_put_ddtt	    ,   --크레인작업결과put 일시
		     :modifier,
		     sysdate               
		 FROM TB_YM_SCH
		 WHERE sch_id = :sch_id
		 )
		WHERE sch_id = :sch_id
		*/ 
		if(listData == null) {
	 		log.println("Input(listData) is NULL");
			throw new DAOException("Input Data is NULL"); 
	 	}
	 	
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCranePutWrslt_01";			
		return super.updateData(queryCode,listData.toArray());
	}
	    	
    /**
     * YJK
     * 해당 CRANE의 SCHEDULE 정보를 가져온다.
     * Schedule Code 가 존재할 경우.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 설비종류
     * @param String	: 설비번호
     * @param String	: 저장품ID
     * @param String	: 스케쥴코드
     * @param String	: 작업예약ID
     *
     * @return 
     * @throws DAOException
     */	
    public JDTORecord getCraneSchInfo(String sSchId)throws DAOException{
		
		return getCraneSchInfo("", 
					  		   "",
					  		   "", 
					  		   "",
					  		   "",
					  		   "",
					  		   sSchId);										  
	} 
	 		
    public JDTORecord getCraneSchInfo(String sYardId, 
									  String sBayGp,
									  String sEquipKind, //일단 조건에서 제외
									  String sEquipNo,
									  String sSchCode,
									  String sWbookId
									  )throws DAOException{
		
		return getCraneSchInfo(sYardId, 
					  		   sBayGp,
					  		   sEquipKind, 
					  		   sEquipNo,
					  		   sSchCode,
					  		   sWbookId,
					  		   "");										  
	} 
	
	public JDTORecord getWBCraneSchInfo(String sYardId, 
									  String sBayGp,
									  String sEquipKind, //일단 조건에서 제외
									  String sEquipNo,
									  String sSchCode
									  )throws DAOException{
		
		return getCraneSchInfo(sYardId, 
					  		   sBayGp,
					  		   sEquipKind, 
					  		   sEquipNo,
					  		   sSchCode,
					  		   "",
					  		   "SWLI");										  
	} 
			    									   
	public JDTORecord getEmergencyCraneSchInfo(String sYardId, 
											   String sBayGp,
											   String sEquipKind, //일단 조건에서 제외
											   String sEquipNo
											  )throws DAOException{
		
		return getCraneSchInfo(sYardId, 
					  		   sBayGp,
					  		   sEquipKind, 
					  		   sEquipNo,
					  		   "",
					  		   "",
					  		   "EMERGENCY");										  
	} 
	
	public JDTORecord getNotEmergencyCraneSchInfo(String sYardId, 
												  String sBayGp,
												  String sEquipKind, //일단 조건에서 제외
												  String sEquipNo
												 )throws DAOException{
		
		return getCraneSchInfo(sYardId, 
					  		   sBayGp,
					  		   sEquipKind, 
					  		   sEquipNo,
					  		   "",
					  		   "",
					  		   "NOTEMERGENCY");										  
	} 
	
	public JDTORecord getCraneSchInfo(String sYardId, 
									  String sBayGp,
									  String sSchCode
									  )throws DAOException{
		
		return getCraneSchInfo(sYardId, 
					  		   sBayGp,
					  		   "", 
					  		   "",
					  		   sSchCode,
					  		   "",
					  		   "");										  
	}
	// SPM2추출(CNLO)일때 추가.최규성 2010-01-27
	public JDTORecord getCraneSchInfo_CNLO(String sYardId, 
											  String sBayGp,
											  String sEquipKind, //일단 조건에서 제외
											  String sEquipNo,
											  String sSchCode,
											  String sWbookId
											  )throws DAOException{

		return getCraneSchInfo(sYardId, 
							   sBayGp,
							   sEquipKind, 
							   sEquipNo,
							   sSchCode,
							   sWbookId,
							   "CNLO");										  
	} 
	
	// SPM2추출(CNLO)일때 추가.최규성 2010-01-27
	public JDTORecord getCraneSchInfo(String sYardId, 
									  String sBayGp,
									  String sEquipKind, //일단 조건에서 제외
									  String sEquipNo,
									  String sSchCode,
									  String sWbookId,
									  String sSchId
									  )throws DAOException
	{	  
		/*
		SELECT 
			   sch_id,					-- Schedule Id
			   wbook_id,				-- Wbook Id
			   stock_id,   			  	-- 저장품 Id
			   sch_rule_id,	  		   	-- Schedule Rule Id
			   yd_gp,		  	 		-- 야드 구분
			   bay_gp,		  	 	  	-- 동구분
			   sch_work_equip_no,	    -- Schedule 작업설비 번호
			   sch_work_stat,		    -- Schedule 상태
			   sch_wprefer,			    -- Schedule 우선순위
			   sch_work_kind,		    -- Schedule Code
			   sch_work_aid_yn,		    -- Schedule 보조유무
			   sch_work_grip_lot_yn,    -- Schedule Grip Lot 유무
			   crane_word_up_loc,	    -- Crane 작업지시 Up 위치
			   wbook_loc_decision_method, -- 작업예약위치 결정방법
			   wbook_sch_act_ddtt,		  -- 스케쥴 작업지시일자
			   crane_word_put_loc,		  -- Crane 작업지시 Put 위치
			   sch_work_car_no,			  -- Schedule 작업차량번호
			   sch_wdemand_type			  -- Schedule 작업요구형태 
		FROM tb_ym_sch
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchInfo_D2";	
		Object[] param	 	= null;	
		
		StringBuffer sDsql = new StringBuffer();
		
		if("".equals(sSchId)){
			
			if("".equals(sWbookId)){
				
				if("".equals(sSchCode)){
//					Object[] params = {sYardId,	sBayGp,	sEquipNo};	
//					param 			= params;
//					// 분기 추가. 최규성. 
//					/**
//					 * B 열연 코일일 경우 스케줄 우선순위 항목에 따라서 다음 스케줄을 호출한다.
//					 */
//					if(sYardId.equals("3")){ 		// 같은 크레인으로 묶인 작업을 검색.  최규성. 
//						sDsql.append("\n WHERE yd_gp 			= :yd_gp ");	
//						sDsql.append("\n AND   bay_gp 			= :bay_gp ");	
//						sDsql.append("\n AND   sch_work_equip_no= :sch_work_equip_no ");	
//						sDsql.append("\n ORDER BY         sch_wprefer ASC,  -- 작업우선순위");
//						sDsql.append("\n decode(sch_work_stat,'2','4',sch_work_stat),-- 작업진행상태");
//						sDsql.append("\n 		  sch_id		-- Schedule Id ");
//						
//					}else{
					// 아래 인자 변수는 위에 선언 최규성 
					Object[] params = {sYardId,	sBayGp,	sEquipNo};	
					param 			= params;
							
						sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");
						sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
						sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
						sDsql.append("\n AND   A.sch_work_equip_no= :sch_work_equip_no ");	
						/*
						 *	SLAB GRIP작업시 권상실적 처리 후 작업진행상태가 상단(3),하단(2) 으로 된다.
						 *	이 PUT지시 상태에서 작업요구를 하면 하단의 슬라브 정보가 먼저 검색되기 때문에
						 *	Grip작업으로 작업지시가 나가지 않는 문제점 발생
						 *	따라서 , 아래와 같이 정렬방법을 수행한다.
						 *	sDsql.append("\n ORDER BY sch_work_stat,-- 작업진행상태");	
						 */
						//2010.06.15 LINE OFF 작업완료 후 다음 작업지시가 다시 이전 작업 지시로 나가는 현상 (요청자: 임경빈 주임)
						sDsql.append("\n ORDER BY decode(A.sch_work_stat,'2','4', A.sch_work_stat),-- 작업진행상태");
						//sDsql.append("\n ORDER BY CASE WHEN SCH_WORK_KIND='CDLO' THEN decode(sch_work_stat ,'S','2',sch_work_stat) ELSE decode(sch_work_stat,'2','4', sch_work_stat) END,-- 작업진행상태");	
						sDsql.append("\n          A.sch_wprefer,  -- 작업우선순위");				
						sDsql.append("\n 		  A.sch_id		-- Schedule Id ");
//					}
				}else{
					if("".equals(sEquipNo)){
						Object[] params = {sYardId,	sBayGp,	sSchCode};	
						param 			= params;
						
						sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");	
						sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
						sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
						sDsql.append("\n AND   A.sch_work_kind 	= :sch_work_kind ");
						sDsql.append("\n AND   A.sch_work_stat	in( 'S','1' ) ");	
						sDsql.append("\n ORDER BY A.sch_work_stat,-- 작업진행상태");		
						sDsql.append("\n          A.sch_wprefer,  -- 작업우선순위");			
						sDsql.append("\n 		  A.sch_id		-- Schedule Id ");
					}else{
						Object[] params = {sYardId,	sBayGp,	sEquipNo, sSchCode};	
						param 			= params;
						
						sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");	
						sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
						sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
						sDsql.append("\n AND   A.sch_work_equip_no= :sch_work_equip_no ");	
						sDsql.append("\n AND   A.sch_work_kind 	= :sch_work_kind ");	
						sDsql.append("\n ORDER BY A.sch_work_stat,-- 작업진행상태");		
						sDsql.append("\n          A.sch_wprefer,  -- 작업우선순위");			
						sDsql.append("\n 		  A.sch_id		-- Schedule Id ");
					}	
				}
			}else{ 		// 같은 작업예약ID로 묶인 스케줄 검사
					Object[] params = {sYardId,	sBayGp,	sEquipNo, sSchCode,	sWbookId};	
					param 			= params;
					
					sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");	
					sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
					sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
					sDsql.append("\n AND   A.sch_work_equip_no= :sch_work_equip_no ");	
					sDsql.append("\n AND   A.sch_work_kind 	= :sch_work_kind ");	
					sDsql.append("\n AND   A.wbook_id	 		= :wbook_id ");	
					sDsql.append("\n ORDER BY A.sch_work_stat,-- 작업진행상태");		
					sDsql.append("\n          (CASE WHEN A.SCH_WORK_KIND LIKE 'G_F%' AND B.SHEAR_SUPPLY_GP='TT'");
					sDsql.append("\n                THEN A.sch_wprefer+1 ELSE A.sch_wprefer END),  -- 작업우선순위");			
					sDsql.append("\n 		  A.sch_id		-- Schedule Id ");
			}
		}else{
			
			if("LINEOFF".equals(sSchId)){
				Object[] params = {sYardId,	sBayGp,	sEquipNo};	
				param 			= params;
				sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");
				sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
				sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
				sDsql.append("\n AND   A.sch_work_equip_no= :sch_work_equip_no ");	
				sDsql.append("\n AND   A.sch_work_kind 	in('CDLO','CELO')");	
				
				sDsql.append("\n AND   EXISTS(SELECT 1 FROM TB_YM_EQUIP C WHERE C.EQUIP_GP LIKE '%CR%' AND C.YD_GP=A.YD_GP AND C.EQUIP_NO=A.SCH_WORK_EQUIP_NO AND C.WPROG_STAT='W') ");
				
				sDsql.append("\n ORDER BY A.sch_work_stat,-- 작업진행상태");		
				sDsql.append("\n          A.sch_wprefer,  -- 작업우선순위");			
				sDsql.append("\n 		  A.sch_id		-- Schedule Id ");
				
			}else if("EMERGENCY".equals(sSchId)){
				Object[] params = {sYardId,	sBayGp,	sEquipNo};	
				param 			= params;
				sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");	
				sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
				sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
				sDsql.append("\n AND   A.sch_work_equip_no= :sch_work_equip_no ");	
				sDsql.append("\n AND   A.sch_wprefer		= '0' -- 작업우선순위");			
				sDsql.append("\n ORDER BY A.sch_work_stat,-- 작업진행상태");		
				sDsql.append("\n          A.sch_wprefer,  -- 작업우선순위");			
				sDsql.append("\n 		  A.sch_id		-- Schedule Id ");
				
			}else if("NOTEMERGENCY".equals(sSchId)){
				
				Object[] params = {sYardId,	sBayGp,	sEquipNo};	
				param 			= params;
				sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");	
				sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
				sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
				sDsql.append("\n AND   A.sch_work_equip_no= :sch_work_equip_no ");	
				sDsql.append("\n AND   A.sch_wprefer	     <> '0' -- 작업우선순위 ");
				sDsql.append("\n AND   A.SCH_WORK_STAT     in ('1','2','3') ");
				sDsql.append("\n ORDER BY A.sch_work_stat,-- 작업진행상태");		
				sDsql.append("\n          (CASE WHEN A.SCH_WORK_KIND LIKE 'G_F%' AND B.SHEAR_SUPPLY_GP='TT'");
				sDsql.append("\n                THEN A.sch_wprefer+1 ELSE A.sch_wprefer END),  -- 작업우선순위");			
				sDsql.append("\n 		  A.sch_id		-- Schedule Id ");
			
			}else if("SWLI".equals(sSchId)){
				
				Object[] params = {sYardId,	sBayGp,	sEquipNo, sSchCode};	
				param 			= params;
				
				sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");	
				sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
				sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
				sDsql.append("\n AND   A.sch_work_equip_no= :sch_work_equip_no ");	
				sDsql.append("\n AND   A.sch_work_kind 	= :sch_work_kind ");	
				sDsql.append("\n ORDER BY A.sch_wprefer,  -- 작업우선순위");			
				sDsql.append("\n 		  	  A.sch_id		-- Schedule Id ");
				
			}else if("CNLO".equals(sSchId)){
				
				Object[] params = {sYardId,	sBayGp,	sSchCode};	
				param 			= params;
				
				sDsql.append("\n WHERE A.yd_gp 			= :yd_gp ");	
				sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
				sDsql.append("\n AND   A.bay_gp 			= :bay_gp ");	
				sDsql.append("\n AND   A.sch_work_kind 	= :sch_work_kind ");
				sDsql.append("\n AND   A.sch_work_stat	in( 'S','1','3' ) ");	
				sDsql.append("\n ORDER BY A.sch_wprefer , REPLACE(A.sch_work_stat,'S','0') DESC  -- 작업우선순위");			
				
				
			}else{
				Object[] params = {sSchId};	
				param 			= params;
				
				sDsql.append("\n WHERE A.sch_id = :sch_id ");
				sDsql.append("\n AND   A.STOCK_ID 		= B.STOCK_ID ");
			}
		}
		
		return super.findByPrimaryKey(queryCode,sDsql.toString(), param);
	}   
	
	/**
     * YJK
     * 해당 스케쥴 정보를 가져온다.
     * 
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
    public List getCraneSchList(String sYdGp, 
							    String sBayGp,
							    String sSchCode)throws DAOException{
		/*
		Ver3.1--
		SELECT 
		     *
		FROM tb_ym_sch
		WHERE yd_gp 			= :yd_gp 
		AND   bay_gp 			= :bay_gp 
		AND   sch_work_kind 	= :sch_work_kind
		AND   sch_work_aid_yn   = 'M'
		ORDER BY sch_work_stat,	-- 작업진행상태	
		  	     sch_id			-- Schedule Id 
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchList";	
		Object[] params = {sYdGp,sBayGp,sSchCode};	
		return super.findList(queryCode,params);
	}
	
    /**
     * 이정훈 2007.01.09 
     * 해당 스케쥴 정보를 가져온다.
     * 
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     * @param String	: 크레인 번호
     * 
     * @return 
     * @throws DAOException
     */	
    public JDTORecord getWorkCraneSchInfo(	String sYardId, 
			   								String sBayGp,
			   								String sEquipNo, 
			   								String sSchCode
			  							)throws DAOException
	{
    	/*
    	 * SELECT 
    	 		sch_id,					-- Schedule Id
    	 		wbook_id,				-- Wbook Id
    	 		stock_id,   			-- 저장품 Id
    	 		sch_rule_id,	  		-- Schedule Rule Id
    	 		yd_gp,		  	 		-- 야드 구분
    	 		bay_gp,		  	 	  	-- 동구분
    	 		sch_work_equip_no,	    -- Schedule 작업설비 번호
    	 		sch_work_stat,		    -- Schedule 상태
    	 		sch_wprefer,			-- Schedule 우선순위
    	 		sch_work_kind,		    -- Schedule Code
    	 		sch_work_aid_yn,		    -- Schedule 보조유무
    	 		sch_work_grip_lot_yn,    -- Schedule Grip Lot 유무
    	 		crane_word_up_loc,	    -- Crane 작업지시 Up 위치
    	 		wbook_loc_decision_method, -- 작업예약위치 결정방법
    	 		wbook_sch_act_ddtt,		  -- 스케쥴 작업지시일자
    	 		crane_word_put_loc,		  -- Crane 작업지시 Put 위치
    	 		sch_work_car_no,			  -- Schedule 작업차량번호
    	 		sch_wdemand_type			  -- Schedule 작업요구형태 
           FROM tb_ym_sch
           WHERE yd_gp 			= :yd_gp 	
           AND   bay_gp 		= :bay_gp 	
           AND   sch_work_equip_no= :sch_work_equip_no
           AND   sch_work_kind 	= :sch_work_kind
    	 * 
    	 */
    	String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchInfo";	
		Object[] params = {sYardId,	sBayGp,	sEquipNo, sSchCode};
		
		return super.findByPrimaryKey(queryCode, params);
										  
    } 
    
    /**
     * 이정훈 2007.07.02 
     * 해당 스케쥴 정보를 가져온다.
     * 
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 차상국 요구 SCH 코드
     * @param String	: 크레인 번호
     * 
     * @return 
     * @throws DAOException
     */	
    public List getWorkCraneSchInfo_01(	String sYardId, 
			   							String sBayGp,
			   							String sSchCode,
			   							String sEquipNo
			  							)throws DAOException
	{
	    	/*
	    	 * SELECT 
	    	 		distinct SCH_WORK_KIND AS SCH_CODE
	           FROM tb_ym_sch
	           WHERE yd_gp 			= :yd_gp 	
	           AND   bay_gp 		= :bay_gp 	
	           AND   sch_work_equip_no= :sch_work_equip_no
	    	 * 
	    	 */
	    	
	    	StringBuffer sDsql = new StringBuffer();
	    	
	    	if("0001".equals(sSchCode)){
    			sDsql.append("\n AND SCH_WORK_KIND IN ('CDLO')");	
		} else if("0002".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CFLI','CKLI','CNLI')");	
		} else if("0003".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CTML','CTMU')");		
		} else if("0004".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND LIKE 'G_FL' ");	//차량출하 (HFL측)
		} else if("0005".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CELO')");		
		} else if("0006".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CFLO','CKLO','CNLO')");		
		} else if("0007".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CTM2','CTM4')");		
		} else if("0008".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('GVF1','GVF2','GTF1','GTF2','GPF1','GPF2')");	//차량출하 (HYSCO측)
		} else if("0009".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CWLI')");	
		} else if("0010".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND LIKE ('CYM_')");	
		} else if("0013".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CTFL')");			
		} else if("0014".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CTFL')");
		} else if("0015".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CGSI')");			
		} else if("0016".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CGSO')");
		} else if("0017".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CTM5','CTM8')"); //대차작업(A-B)
		} else if("0018".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CTM6','CTM9')"); //대차작업(C-D)			
		} else if("0019".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CTM7','CTMX')"); //대차작업(D-E)
		} else if("0011".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CFSI')"); //HFL결속대 보급요구
		} else if("0012".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('CFSO')"); //HFL결속대 추출요구
		} else if("0020".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('GVML','GVMU','CVML','CVMU')");	//차량이송 (HFL측)
		} else if("1014".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('GVM2','GVM3','GVM4','CVM2','CVM4')");	//차량이송 (HYSCO측)
			
		} else if("1001".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SVMU')");				
		} else if("1002".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SVML')");				
		} else if("1003".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SYMM','SYM2','SYM3')");				
		} else if("1004".equals(sSchCode)){
			sDsql.append("\n AND ( ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STML','STM2','STSL') AND CRANE_WORD_PUT_LOC LIKE '2_TC1%' ) ");
			sDsql.append("\n 	       OR ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STMU','STM4') AND CRANE_WORD_UP_LOC LIKE '2_TC1%') ");
			sDsql.append("\n 	       ) ");
		} else if("1005".equals(sSchCode)){
			sDsql.append("\n AND ( ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STML','STM2','STSL') AND CRANE_WORD_PUT_LOC LIKE '2_TC2%' ) ");
			sDsql.append("\n 	       OR ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STMU','STM4') AND CRANE_WORD_UP_LOC LIKE '2_TC2%') ");
			sDsql.append("\n 	       ) ");
		} else if("1006".equals(sSchCode)){
			sDsql.append("\n AND ( ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STML','STM2','STSL') AND CRANE_WORD_PUT_LOC LIKE '2_TC3%' ) ");
			sDsql.append("\n 	       OR ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STMU','STM4') AND CRANE_WORD_UP_LOC LIKE '2_TC3%') ");
			sDsql.append("\n 	       ) ");	
		} else if("1007".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SWLI')");				
		} else if("1008".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SCLI')");				
		} else if("1009".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SHLO')");				
		} else if("1010".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SSLI')");				
		} else if("1011".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SSLO')");				
		} else if("1012".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SHSI')");				
		} else if("1013".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SHSO')");				
		}              
	
    		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchInfo_01";	
		Object[] params = {sYardId,	sBayGp,	sEquipNo};
		
		return super.findList(queryCode,sDsql.toString(), params);
		
    } 
    
    public List getWorkCraneSchInfo_02(	String sYardId, 
			   					String sBayGp,
			   					String sSchCode,
			   					String sEquipNo
			  					)throws DAOException
	{
	    	/*
	    	 * SELECT 
	    	 		a.sch_work_kind as sch_code,
	    	 		a.*
	           FROM tb_ym_sch
	           WHERE yd_gp 			= :yd_gp 	
	           AND   bay_gp 			= :bay_gp 	
	           AND   sch_work_equip_no	= :sch_work_equip_no
	    	 * 
	    	 */
	    	
	    	StringBuffer sDsql = new StringBuffer();
	    	
	    	if("1001".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SVMU')");				
		} else if("1014".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SVMU') AND SUBSTR(CRANE_WORD_UP_LOC,3,2) = 'TR'");		
		} else if("1015".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SVMU') AND SUBSTR(CRANE_WORD_UP_LOC,3,2) = 'PT'");		
		} else if("1002".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SVML')");			
		} else if("1003".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SYMM','SYM2','SYM3')");				
		} else if("1004".equals(sSchCode)){
			sDsql.append("\n AND ( ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STML','STM2','STSL') AND CRANE_WORD_PUT_LOC LIKE '2_TC1%' ) ");
			sDsql.append("\n 	       OR ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STMU','STM4') AND CRANE_WORD_UP_LOC LIKE '2_TC1%') ");
			sDsql.append("\n 	       ) ");
		} else if("1005".equals(sSchCode)){
			sDsql.append("\n AND ( ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STML','STM2','STSL') AND CRANE_WORD_PUT_LOC LIKE '2_TC2%' ) ");
			sDsql.append("\n 	       OR ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STMU','STM4') AND CRANE_WORD_UP_LOC LIKE '2_TC2%') ");
			sDsql.append("\n 	       ) ");
		} else if("1006".equals(sSchCode)){
			sDsql.append("\n AND ( ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STML','STM2','STSL') AND CRANE_WORD_PUT_LOC LIKE '2_TC3%' ) ");
			sDsql.append("\n 	       OR ");
			sDsql.append("\n 	       ( SCH_WORK_KIND IN ('STMU','STM4') AND CRANE_WORD_UP_LOC LIKE '2_TC3%') ");
			sDsql.append("\n 	       ) ");	
		} else if("1007".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SWLI')");				
		} else if("1008".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SCLI')");				
		} else if("1009".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SHLO')");				
		} else if("1010".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SSLI')");				
		} else if("1011".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SSLO')");				
		} else if("1012".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SHSI')");				
		} else if("1013".equals(sSchCode)){
			sDsql.append("\n AND SCH_WORK_KIND IN ('SHSO')");				
		}              
		
		sDsql.append("\n ORDER BY	sch_work_stat,	-- 작업진행상태");		
		sDsql.append("\n          	  	sch_wprefer,  		-- 작업우선순위");			
		sDsql.append("\n 		  		sch_id			-- Schedule Id ");
	
    		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchInfo_02";	
		Object[] params = {sYardId,	sBayGp,	sEquipNo};
		
		return super.findList(queryCode,sDsql.toString(), params);
		
    } 
    
	/**
     * YJK
     * 해당 CRANE의 작업예정 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfo_01(String sYardId, 
										   String sBayGp,
										   String sEquipKind,
						    			   String sEquipNo
									       )throws DAOException
	{	  
		/*
		Ver4.2--
		SELECT
            x.wbook_id
        FROM
        (    
        SELECT a.wbook_id
		FROM tb_ym_wbook a,
		    (
		    SELECT
			  yd_gp,
			  bay_gp,
			  sch_work_kind,
			  decode(sch_rule_active_stat,
			  		 'A', sch_rule_crane_no, 
			  		 'B', sch_rule_alter_crane_no, 
			  		 sch_rule_crane_no) as select_crane_no
		    FROM tb_ym_schrule
		    WHERE yd_gp	   = :yd_gp
		    AND   bay_gp   = :bay_gp
		    )b
		WHERE a.yd_gp  			    = :yd_gp
		AND   a.bay_gp 			    = :bay_gp 
		AND   b.select_crane_no 	= :sch_rule_crane_no
		AND   a.yd_gp  			    = b.yd_gp  
		AND   a.bay_gp 			    = b.bay_gp    
		AND   a.sch_work_kind       = b.sch_work_kind
		ORDER BY a.wbook_id
		)x
		WHERE rownum = 1
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfo_01";	
		Object[] params = {sYardId,sBayGp,sYardId,sBayGp,sEquipNo};	
		
		return super.findByPrimaryKey(queryCode, params);
	}  
	
	/**
     * YJK
     * 해당 스케쥴의 스케쥴 등록 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     * @param String	: 추가 조건
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneSchCount_01(String sYardId, 
									   		String sBayGp,
									   		String sSchCode
									   )throws DAOException
	{	  
		/*
		Ver4.2--
		SELECT count(*) as COUNT
		FROM tb_ym_wbook a
		WHERE a.yd_gp  			    = :yd_gp
		AND   a.bay_gp 			    = :bay_gp 
		AND   a.sch_work_kind 		= :sch_work_kind 
		*/
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchCount_01";	
		Object[] params = {sYardId,sBayGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode,params);
	}  
	
	/**
     * YJK
     * 해당 스케쥴의 스케쥴 등록 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     * @param String	: 추가 조건
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneSchCount(String sYardId, 
									   		String sBayGp,
									   		String sSchCode
									   )throws DAOException
	{	  
		/*
		Ver4.2--
		SELECT count(*) as COUNT
		FROM tb_ym_sch a
		WHERE a.yd_gp  			    = :yd_gp
		AND   a.bay_gp 			    = :bay_gp 
		AND   a.sch_work_kind 		= :sch_work_kind 
		*/
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchCount";	
		Object[] params = {sYardId,sBayGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode,params);
	}
	
	public JDTORecord getCraneSchCountM(String sYardId, 
			   							String sBayGp,
			   							String sSchCode
			   							)throws DAOException
	{	  
		/*
	Ver4.2--
	SELECT count(*) as COUNT
	FROM tb_ym_sch a
	WHERE a.yd_gp  			    = :yd_gp
	AND   a.bay_gp 			    = :bay_gp 
	AND   a.sch_work_kind 		= :sch_work_kind 
		 */
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchCountM";	
		Object[] params = {sYardId,sBayGp,sSchCode};	

		return super.findByPrimaryKey(queryCode,params);
	} 
	/**
     * YJK
     * 해당 CRANE의 Sch 정보 확인
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCranInfoSchCTFL(String sYardId, 
										   String sBayGp,
										   String sSchCode
										   )throws DAOException
	{	  
		/*
		Ver4.2--
		SELECT * 
		FROM TB_YM_SCH
		WHERE YD_GP = :yd_gp
		AND BAY_GP = :bay_gp 
		AND SCH_WORK_KIND = :sch_work_kind 
		AND SCH_WORK_AID_YN = 'M' 
		AND SCH_WORK_STAT IN ('1','S')
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCranInfoSchCTFL";	
		Object[] params = {sYardId,sBayGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	/**
     * YJK
     * 해당 CRANE의 작업예정 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfo_02(String sYardId, 
										   String sBayGp,
										   String sSchCode
										   )throws DAOException
	{	  
		/*  ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfo_02 
SELECT A.* 
   , (CASE WHEN NVL(TCHARGE_LOT_NO,1) <=NVL(GCHARGE_LOT_NO,1) THEN 'Y' ELSE 'N' END) AS CHARGE_LOT_YN 
  FROM ( 
SELECT 
            X.* 
            ,(SELECT STOCK_ID  
               FROM USRYMA.TB_YM_STACKLAYER 
              WHERE STACK_COL_GP='2CWB01' 
                AND STACK_BED_GP='01' 
                AND STACK_LAYER_GP='01' 
             ) AS GSTOCKID 
             ,(SELECT B.CTS_RELAY_SADDLE  
               FROM USRYMA.TB_YM_STACKLAYER A 
                  , tb_ym_stock B 
              WHERE A.STOCK_ID=B.STOCK_ID 
                AND A.STACK_COL_GP='2CWB01' 
                AND A.STACK_BED_GP='01' 
                AND A.STACK_LAYER_GP='01' 
             ) AS GCHARGE_LOT_NO 
        FROM 
        (     
        SELECT * 
         FROM( 
                SELECT A.wbook_id 
                     ,(SELECT COUNT(*) FROM tb_ym_sch WHERE sch_work_kind='CTML' AND YD_GP ='1' AND BAY_GP=A.BAY_GP AND YD_GP=A.YD_GP) AS CNT      
                     , A.sch_work_kind 
                     , A.YD_GP 
                     , B.STOCK_ID AS TSTOCKID 
                     , B.CHARGE_LOT_NO AS TCHARGE_LOT_NO 
                     , D.STACK_COL_GP AS STACK_COL_TYPE 
                     , NVL(D.CHK_CNT,9) AS CHK_CNT
                     , E.COIL_W
                     , E.COIL_T
                     , C.STACK_COL_GP
                     , C.STACK_BED_GP
                     , C.STACK_LAYER_GP
                FROM tb_ym_wbook a, 
                     tb_ym_stock b, 
			         tb_ym_stacklayer c ,
                     (
                     SELECT A.STACK_COL_GP, STACK_MAX_QNTY-STOCK_CNT AS CHK_CNT ,SCH_CD, SUBSTR(A.STACK_COL_GP,2,1) AS BAY_GP ,A.YD_GP
                     FROM ( 
                        SELECT STACK_COL_GP, COUNT(STOCK_ID) AS STOCK_CNT,SUBSTR(STACK_COL_GP,1,1) AS YD_GP 
                         FROM USRYMA.TB_YM_STACKLAYER 
                        WHERE STACK_COL_GP LIKE '__TC%' 
                        GROUP BY STACK_COL_GP 
                        ) A 
                        ,( 
                        SELECT STACK_MAX_QNTY,EQUIP_GP,YD_GP 
                         FROM USRYMA.TB_YM_EQUIP 
                        WHERE EQUIP_GP LIKE '__TC%' 
                        ) B 
                        ,(SELECT '3' AS YD_GP , 'CTML' AS SCH_CD , 'TC01' AS STACK_COL_GP FROM DUAL UNION ALL
                          SELECT '3' AS YD_GP , 'CTM2' AS SCH_CD , 'TC02' AS STACK_COL_GP FROM DUAL UNION ALL
                          SELECT '3' AS YD_GP , 'CTM5' AS SCH_CD , 'TC03' AS STACK_COL_GP FROM DUAL UNION ALL
                          SELECT '3' AS YD_GP , 'CTM6' AS SCH_CD , 'TC04' AS STACK_COL_GP FROM DUAL UNION ALL
                          SELECT '3' AS YD_GP , 'CTM7' AS SCH_CD , 'TC05' AS STACK_COL_GP FROM DUAL UNION ALL
                          SELECT '1' AS YD_GP , 'CTM2' AS SCH_CD , 'TC03' AS STACK_COL_GP FROM DUAL  
                         ) C
                    WHERE SUBSTR(A.STACK_COL_GP,3,4)=SUBSTR(B.EQUIP_GP,3,4) 
                      AND A.YD_GP=B.YD_GP 
                      AND A.YD_GP=C.YD_GP(+)
                      AND SUBSTR(A.STACK_COL_GP,3,4)=C.STACK_COL_GP(+)
                      AND A.YD_GP IN('1','3')
                     ) D
                     ,TB_PT_COILCOMM E
                WHERE a.wbook_id 	= b.wbook_id 
                AND   b.stock_id    = c.stock_id
                AND   A.YD_GP=D.YD_GP(+)
                AND   A.BAY_GP=D.BAY_GP(+)
                AND   A.sch_work_kind=D.SCH_CD(+)
                AND   B.STOCK_ID = E.COIL_NO
                AND   a.yd_gp  			    = :yd_gp 
                AND   a.bay_gp 			    = :bay_gp  
                AND   a.sch_work_kind 		= :sch_work_kind  
                AND   not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id and sch_work_aid_yn = 'M') 
                ORDER BY--//SPM 보급 폭큰거 , 두께 작은거 
                        (CASE WHEN a.yd_gp='1' AND a.sch_work_kind='CKLI' THEN E.COIL_W ELSE 1 END) DESC
                        ,(CASE WHEN a.yd_gp='1' AND a.sch_work_kind='CKLI' THEN E.COIL_T ELSE 1 END)
                        --//HFL 보급,동간이적상차 동일열 2단 우선
                        ,(CASE WHEN a.yd_gp='1' AND a.sch_work_kind in('CFLI','CTML') THEN C.STACK_COL_GP ELSE '' END)
                        ,(CASE WHEN a.yd_gp='1' AND a.sch_work_kind in('CFLI','CTML') THEN C.STACK_LAYER_GP ELSE '' END) DESC
                        ,a.wbook_id 
              ) A 
          WHERE (YD_GP='1' AND A.CNT<=1 AND sch_work_kind='CTML') OR (YD_GP='1' AND sch_work_kind<>'CTML') OR (YD_GP<>'1') 
		)x 
		WHERE   CHK_CNT> 0
          AND rownum = 1 
        ) A
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfo_02";	
		Object[] params = {sYardId,sBayGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	/**
     * YJK
     * 해당 CRANE의 작업예정 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfo_13(String sYardId, 
										   String sBayGp ,
										   String sEqbGp
										   )throws DAOException
	{	  
 
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfo_13";	
		Object[] params = {sYardId,sBayGp,sEqbGp};	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	/**
     * YJK
     * 해당 CRANE의 작업예정 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfo_14(String sYardId, 
										   String sBayGp ,
										   String sSch_Code
										   )throws DAOException
	{	  
 
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfo_14";	
		Object[] params = {sYardId,sBayGp,sSch_Code};	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	/**
     * YJK
     * 해당 CRANE의 작업예정 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfoCarNo(String sYardId, 
										   String sBayGp,
										   String sSchCode,
										   String sCarNo
										   )throws DAOException
	{	  
		/*
		SELECT
            x.wbook_id
        FROM
        (    
        SELECT a.wbook_id
		FROM tb_ym_wbook a
            , USRYMA.TB_YM_STOCK b
		WHERE a.wbook_id =b.wbook_id
        and   a.yd_gp  			    = :yd_gp
		AND   a.bay_gp 			    = :bay_gp 
		AND   a.sch_work_kind 		= :sch_work_kind 
        and   b.CAR_CARD_NO = :CAR_CARD_NO
		AND   not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id and sch_work_aid_yn = 'M')
		ORDER BY a.wbook_id
		)x
		WHERE rownum = 1
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCarNo";	
		Object[] params = {sYardId,sBayGp,sSchCode,sCarNo };	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	/**
     * YJK
     * 해당 CRANE의 작업예정 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfoCarNo2(String sYardId, 
										   String sBayGp,
										   String sSchCode,
										   String sCarNo
										   )throws DAOException
	{	  
		/*
		SELECT
            x.wbook_id
        FROM
        (    
        SELECT a.wbook_id
		FROM tb_ym_wbook a
            , USRYMA.TB_YM_STOCK b
		WHERE a.wbook_id =b.wbook_id
        and   a.yd_gp  			    = :yd_gp
		AND   a.bay_gp 			    = :bay_gp 
		AND   a.sch_work_kind 		= :sch_work_kind 
        and   b.CAR_CARD_NO <> :CAR_CARD_NO
		AND   not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id and sch_work_aid_yn = 'M')
		ORDER BY a.wbook_id
		)x
		WHERE rownum = 1
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCarNo2";	
		Object[] params = {sYardId,sBayGp,sSchCode,sCarNo };	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	
	/**
     * 2007.04.10 이정훈
     * B열연 Coil 야드 대차출하상차 작업 시 
	 *  1. 대차 도착 후 대차 출하하기 위해 이적한 물량 편성
	 * 
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfoCTFL_01(String sYardId, 
										   String sBayGp,
										   String sSchCode
										   )throws DAOException
	{	  
		/*
		Ver4.2--
	SELECT X.WBOOK_ID 
	FROM (  
    SELECT A.WBOOK_ID, -- 작업 ID
       		A.SCH_WORK_KIND AS 작업종류,
       		NVL(B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP, 'UNKNOW') AS FROM_ADDR,
       		A.STOCK_ID AS 재료번호,
       		B.STACK_LAYER_STAT AS 적치상태 -- 적치상태    
		FROM ( 
       		SELECT A.WBOOK_ID, -- 작업 ID
              	A.SCH_WORK_KIND, -- 작업종류
              	C.STOCK_ID -- 저장품 ID
            FROM ( SELECT WBOOK_ID, -- 작업 ID
                     YD_GP, -- 야드구분(1)
                     BAY_GP, -- 동구분('A', 'B', 'C' ...)
                     SCH_WORK_KIND -- 작업종류('CTCL', 'CSTU' ...)
              	FROM TB_YM_WBOOK 
              	WHERE YD_GP = ?-- 야드구분
                AND BAY_GP LIKE ?-- 동구분
                AND SCH_WORK_KIND LIKE ?-- 작업종류('CTCL', 'CSTU' ...)
            	) A, TB_YM_SCHRULE B, TB_YM_STOCK C
       		WHERE A.YD_GP = B.YD_GP 
         	AND A.BAY_GP = B.BAY_GP 
         	AND A.SCH_WORK_KIND = B.SCH_WORK_KIND
         	AND A.WBOOK_ID = C.WBOOK_ID
     		) A, TB_YM_STACKLAYER B, USRPMA.TB_PM_COILCOMM C
		WHERE A.STOCK_ID = B.STOCK_ID(+) 
  		AND B.STACK_LAYER_STAT = 'S'
  		AND A.STOCK_ID = C.COIL_NO(+)
  		AND B.STACK_COL_GP IN (
			SELECT STACK_COL_GP FROM USRYMA.TB_YM_LOCSEARCH
			WHERE STOCK_MOVE_ROUTE_ID  IN (
    			SELECT STOCK_MOVE_ROUTE_ID    
    			FROM TB_YM_STOCKMOVEROUTE 
    		    WHERE YD_GP = ?-- 야드구분
                AND BAY_GP LIKE ?-- 동구분
                AND SCH_WORK_KIND LIKE ?-- 작업종류('CTCL', 'CSTU' ...)
    		AND STACK_USAGE_CD_TO != 'CX'))
		ORDER BY B.STACK_LAYER_GP DESC,A.WBOOK_ID
		) X
		WHERE rownum = 1
		*/
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCTFL_01";	
		Object[] params = {sYardId,sBayGp,sSchCode,sYardId,sBayGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode, params);
	}
	
	/**
     * 2007.04.10 이정훈
     * B열연 Coil 야드 대차출하상차 작업 시 
	 *  2. 대차 도착 전 이적 편성
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfoCTFL_02(String sYardId, 
										   String sBayGp,
										   String sSchCode
										   )throws DAOException
	{	  
		/*
		Ver4.2--
SELECT X.WBOOK_ID 
FROM ( 
	SELECT A.WBOOK_ID, -- 작업 ID
       		A.SCH_WORK_KIND AS 작업종류,
       		NVL(B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP, 'UNKNOW') AS FROM_ADDR,
       		A.STOCK_ID AS 재료번호,
       		B.STACK_LAYER_STAT AS 적치상태 -- 적치상태    
		FROM ( 
       		SELECT A.WBOOK_ID, -- 작업 ID
              	A.SCH_WORK_KIND, -- 작업종류
              	C.STOCK_ID -- 저장품 ID
            FROM ( SELECT WBOOK_ID, -- 작업 ID
                     YD_GP, -- 야드구분(1)
                     BAY_GP, -- 동구분('A', 'B', 'C' ...)
                     SCH_WORK_KIND -- 작업종류('CTCL', 'CSTU' ...)
              	FROM TB_YM_WBOOK 
              	WHERE YD_GP = ?-- 야드구분
                AND BAY_GP LIKE ?-- 동구분
                AND SCH_WORK_KIND LIKE ?-- 작업종류('CTCL', 'CSTU' ...)
            	) A, TB_YM_SCHRULE B, TB_YM_STOCK C
       		WHERE A.YD_GP = B.YD_GP 
         	AND A.BAY_GP = B.BAY_GP 
         	AND A.SCH_WORK_KIND = B.SCH_WORK_KIND
         	AND A.WBOOK_ID = C.WBOOK_ID
     		) A, TB_YM_STACKLAYER B, USRPMA.TB_PM_COILCOMM C
		WHERE A.STOCK_ID = B.STOCK_ID(+) 
  		AND B.STACK_LAYER_STAT = 'S'
  		AND A.STOCK_ID = C.COIL_NO(+)
  		AND B.STACK_COL_GP NOT IN (
			SELECT STACK_COL_GP FROM USRYMA.TB_YM_LOCSEARCH
			WHERE STOCK_MOVE_ROUTE_ID  IN (
    			SELECT STOCK_MOVE_ROUTE_ID    
    			FROM TB_YM_STOCKMOVEROUTE 
    			WHERE YD_GP = ?-- 야드구분
    			AND BAY_GP LIKE ?-- 동구분
    			AND SCH_WORK_KIND LIKE ?-- 작업종류('CTCL', 'CSTU' ...)
    		AND STACK_USAGE_CD_TO != 'CX'))
		ORDER BY A.WBOOK_ID
) X
WHERE rownum = 1
		*/
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCTFL_02";	
		Object[] params = {sYardId,sBayGp,sSchCode,sYardId,sBayGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode, params);
	}
	
	/**
     * 2007.04.10 이정훈
     * B열연 Coil 야드 대차출하상차 작업 시 
	 *  2. 대차 도착 전 이적 편성
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfoCTFL_03(String sYardId, 
										   String sBayGp,
										   String sSchCode
										   )throws DAOException
	{	  
		/*
			SELECT X.WBOOK_ID 
			FROM ( 
			SELECT A.WBOOK_ID, -- 작업 ID
       			A.SCH_WORK_KIND AS 작업종류,
       			NVL(B.STACK_COL_GP||B.STACK_BED_GP||B.STACK_LAYER_GP, 'UNKNOW') AS FROM_ADDR,
       			A.STOCK_ID AS 재료번호,
       			B.STACK_LAYER_STAT AS 적치상태 -- 적치상태    
			FROM ( 
       			SELECT A.WBOOK_ID, -- 작업 ID
              		A.SCH_WORK_KIND, -- 작업종류
              		C.STOCK_ID -- 저장품 ID
            	FROM ( SELECT WBOOK_ID, -- 작업 ID
                     	YD_GP, -- 야드구분(1)
                     	BAY_GP, -- 동구분('A', 'B', 'C' ...)
                     	SCH_WORK_KIND -- 작업종류('CTCL', 'CSTU' ...)
              		FROM TB_YM_WBOOK 
              		WHERE YD_GP = ?-- 야드구분
                	AND BAY_GP LIKE ?-- 동구분
                	AND SCH_WORK_KIND LIKE ?-- 작업종류('CTCL', 'CSTU' ...)
            	) A, TB_YM_SCHRULE B, TB_YM_STOCK C
       		WHERE A.YD_GP = B.YD_GP 
         	AND A.BAY_GP = B.BAY_GP 
         	AND A.SCH_WORK_KIND = B.SCH_WORK_KIND
         	AND A.WBOOK_ID = C.WBOOK_ID
     		) A, TB_YM_STACKLAYER B, USRPMA.TB_PM_COILCOMM C
		WHERE A.STOCK_ID = B.STOCK_ID(+) 
  		AND B.STACK_LAYER_STAT = 'S'
  		AND A.STOCK_ID = C.COIL_NO(+)
  		AND SUBSTR(B.STACK_COL_GP,3,2) NOT IN ('17','18','19','20','21')
		ORDER BY A.WBOOK_ID
	) X
	WHERE rownum = 1
		*/
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfoCTFL_03";	
		Object[] params = {sYardId,sBayGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode, params);
	}
	
	/**
     * 2007.04.10 이정훈
     * B열연 Coil 야드 대차출하상차 작업 시 
	 *  1. 대차 도착 후 대차 출하하기 위해 이적한 물량 편성
	 *  2. 대차 도착 전 이적 편성
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCranTcInfoCTFL(String sYardId, 
										   String sBayGp,
										   String sSchCode
										   )throws DAOException
	{	  
		/*
		Ver4.2--
		SELECT * FROM TB_YM_STACKLAYER
		WHERE STACK_COL_GP like ?||'TC%'
	    AND STACK_LAYER_ACTIVE_STAT = 'O'
	    AND STACK_LAYER_STAT = 'E'
	    AND STOCK_ID IS NULL
		*/
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCranTcInfoCTFL";	
		Object[] params = {sYardId+sBayGp};	
		
		return super.findByPrimaryKey(queryCode, params);
	}  
	
	
	/**
     * YJK
     * 해당 CRANE의 작업예정 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfo_03(String sYardId, 
										   String sBayGp,
										   String sEquipNo,
						    			   String sSchCode
									       )throws DAOException
	{	  
		/*
		Ver4.2--
		SELECT *
		FROM tb_ym_sch a
		WHERE a.yd_gp  			     = :yd_gp
		AND   a.bay_gp 			     = :bay_gp 
		AND   a.sch_work_equip_no	 = :sch_work_equip_no 
		AND   a.sch_work_kind 		 = :sch_work_kind 
		AND   a.sch_work_stat 		 = 'S'
		AND   a.sch_work_grip_lot_yn <> 'G'
		ORDER BY a.sch_id
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfo_03";	
		Object[] params = {sYardId,sBayGp,sEquipNo,sSchCode};	
		
		return super.findByPrimaryKey(queryCode, params);
	}  
	
	/**
     * 2007.07.04 이정훈
     * 해당 CRANE의 작업예정 정보를 가져온다.
     *
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */			
	public JDTORecord getCraneWbookInfo_04(String sYardId, 
										   String sBayGp,
						    			   String sSchCode
									       )throws DAOException
	{	  
		/*
		SELECT x.wbook_id
		FROM (
    		SELECT B.STOCK_ID,a.wbook_id,C.STACK_COL_GP, C.STACK_BED_GP, STACK_LAYER_STAT
			FROM tb_ym_wbook a, TB_YM_STOCK B, TB_YM_STACKlAYER C
			WHERE a.yd_gp  			    = :yd_gp
			AND   a.bay_gp 			    = :bay_gp  
			AND   a.sch_work_kind 		= :sch_work_kind 
			AND   a.wbook_id = B.WBOOK_ID
			AND   B.STOCK_ID = C.STOCK_ID
    		AND   not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id and sch_work_aid_yn = 'M')
    		ORDER BY C.STACK_BED_GP 
		)x
		WHERE rownum = 1
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneWbookInfo_04";	
		Object[] params = {sYardId,sBayGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode, params);
	} 	
	
    
    /**
     * YJK
	 * 작업예약 동구분 항목을 수정한다.
	 *	
     * @param String	: 작업예약ID
     * @param String	: 동구분
     *
     * @return int
     * @throws DAOException
     */	
    public int updateBayGpWithWbookId(String sWbookId,
    							  	  String sBayGp) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_WBOOK
		SET
		    bay_gp    	= :bay_gp,	
		    modifier    = 'SYSTEM',
		 	mod_ddtt    = sysdate     
		WHERE wbook_id 	= :wbook_id            
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateBayGpWithWbookId";
		Object[] params = {sBayGp,sWbookId};	
		return super.updateData(queryCode,params);
    }
    
    /**
     * YJK
     * 적치열에 있는 저장품ID정보를 가지고 적치단 정보를 가져온다.
     *	
     * @param String	: 적치열
     * @param String	: 저장품ID
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStackLayerInfoWithStockId(String sStackColGp,String sStockId) throws DAOException{
		/*
		SELECT 
			   *
		FROM tb_ym_stacklayer
		WHERE stack_col_gp  = :stack_col_gp
		AND   stock_id		= :stock_id 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId";	
		Object[] params = {sStackColGp,sStockId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    /**
     * YJK
     *	저장품ID로 적치단 정보를 가져온다.
	 *
     * @param String	: 저장품ID
     *
     * @return JDTORecord 적치단정보
     * @throws DAOException
     */			
	public JDTORecord getStackLayerInfoWithStockId_02(String sCoilNo) throws DAOException
	{	
		/*
		VER4.1--
		SELECT 
		   *
		FROM  TB_YM_STACKLAYER
		WHERE STOCK_ID = :STOCK_ID --저장품번호(HB00001)
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId_02";	
		Object[] params = {sCoilNo};	
		return super.findByPrimaryKey(queryCode, params);
	}	
    /**
     * YJK
     *	저장품ID로 적치단 정보를 가져온다.
	 *
     * @param String	: 저장품ID
     *
     * @return JDTORecord 적치단정보
     * @throws DAOException
     */			
	public JDTORecord getStackLayerInfoWithStockIdChk(String sCoilNo) throws DAOException
	{	
		/*
		SELECT STOCK_ID
		 FROM TB_YM_STACKLAYER
		 WHERE SUBSTR(STACK_COL_GP,3,2) IN ('FE','FD','KE','KD','FI','KI','XX') 
		  AND STOCK_ID = :STOCK_ID --저장품번호(HB00001)
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockIdChk";	
		Object[] params = {sCoilNo};	
		return super.findByPrimaryKey(queryCode, params);
	}
    /**
     * YJK
     * Stock Table에 wbook_id 건수 정보를 가져온다.
     * 
     * @param String	: 작업예약ID
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord checkStockWbookId(String sWbookId) throws DAOException{
		/*
		SELECT 
			   count(*) as COUNT
		FROM tb_ym_stock 
		WHERE wbook_id = :sWbookId
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.checkStockWbookId";	
		Object[] params = {sWbookId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * Stock Table에 wbook_id 건수 정보를 가져온다.
     * 
     * @param String	: 작업예약ID
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStockWbookId(String sWbookId) throws DAOException{
		/*
		SELECT 
			   *
		FROM tb_ym_stock 
		WHERE wbook_id = :sWbookId
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockWbookId";	
		Object[] params = {sWbookId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * WBOOK Table 데이타를 Delete한다.  
     * 
     * @param String	: 작업예약ID
     *
     * @return int 
     * @throws DAOException
     */		
	public int deleteWbookInfo(String sWbookId) throws DAOException
	{
		/*
		DELETE tb_ym_wbook
		WHERE wbook_id = :wbook_id 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteWbookInfo";	
		Object[] params = {sWbookId};		
		return super.deleteData(queryCode, params);
	}
	
	
    /**
     * YJK
     * WBOOK Table 데이타를 Delete한다.  
     * 
     * @param String	: 작업예약ID
     *
     * @return int 
     * @throws DAOException
     */		
	public int deleteCarmtlInfo(String sWbookId) throws DAOException
	{
		/*
		DELETE FROM USRYDA.TB_YD_CARFTMVMTL
		WHERE DEL_YN='N'
		 AND STL_NO= ?
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteCarmtlInfo";	
		Object[] params = {sWbookId};		
		return super.deleteData(queryCode, params);
	}
	
	  /**
     * YJK
     * WBOOK Table 데이타를 Delete한다.  
     * 
     * @param String	: 작업예약ID
     *
     * @return int 
     * @throws DAOException
     */		
	public int deleteStockWbookInfo(String sStockId) throws DAOException
	{
		/*
		DELETE tb_ym_wbook
		WHERE wbook_id = 
   		(       
   			SELECT WBOOK_ID 
        	FROM TB_YM_STOCK
        	WHERE STOCK_ID = :sStockId
		)
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStockWbookInfo";	
		Object[] params = {sStockId};		
		return super.deleteData(queryCode, params);
	}
	
	/**
     * YJK
     * WBOOK Table 데이타를 Select한다.  
     * 
     * @param String	: 작업예약ID
     *
     * @return JDTORecord 
     * @throws DAOException
     */		
	public JDTORecord getWbookInfo(String sWbookId) throws DAOException
	{
		/*
		SELECT * FROM tb_ym_wbook
		WHERE wbook_id = :wbook_id 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getWbookInfo";	
		Object[] params = {sWbookId};		
		return super.findByPrimaryKey(queryCode, params);
	}
	
	/**
	 * YJK
     * SCHEDULE Table 데이타를 Delete한다.  
     * 
     * @param String	: 스케쥴ID
     *
     * @return int 
     * @throws DAOException
     */		
	public int deleteSchInfo(String sSchId) throws DAOException
	{
		/*
		DELETE tb_ym_sch
		WHERE sch_id = :sSchId 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteSchInfo";	
		Object[] params = {sSchId};		
		return super.deleteData(queryCode, params);
	}
	
	/**
     * YJK
     * Crane 설비상태를 변경한다.
	 * 
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 설비종류
     * @param String	: 설비번호
     * @param String	: 작업진행상태
     * @param String	: 스케쥴코드
     *
     * @return 
     * @throws DAOException
     */		
    public int updateSubCraneEquipStat(String sYard_Id,
    								   String sBay_Gp,
    								   String sEquip_Kind,
    								   String sEquip_No,
    								   String sStat,
    								   String sCarloadSchWorkKind
    								   ) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_equip 
		SET 
			wprog_stat 			  = :sStat,				    -- 작업진행상태
			carload_sch_work_kind = :carload_sch_work_kind, -- 스케쥴코드
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE yd_gp 		= :sYard_Id
		AND   bay_gp  		= :sBay_Gp
		AND   equip_kind 	= :sEquip_Kind
		AND   equip_no 		= :sEquip_No
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateSubCraneEquipStat";
		Object[] params = {sStat,sCarloadSchWorkKind,sYard_Id,sBay_Gp,sEquip_Kind,sEquip_No};	
		return super.updateData(queryCode,params);
	
    }
    /**
     * YJK
     * STACKER TABLE DELETE
	 * 
     * @param String	: 적치열
     * @param String	: 적치대
     *
     * @return 
     * @throws DAOException
     */	
    public int deleteStackerInfo( String sStackColGp,
								  String sStackBedGp) throws DAOException{
		/*
		DELETE tb_ym_stacker
		WHERE stack_col_gp   = :stack_col_gp
		AND   stack_bed_gp   = :stack_bed_gp
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackerInfo";	
		Object[] params = {sStackColGp,sStackBedGp};		
		return super.deleteData(queryCode, params);
	}
    /**
     * YJK
     * STACKER TABLE DELETE
	 * 
     * @param String	: 적치열
     * @param String	: 적치대
     *
     * @return 
     * @throws DAOException
     */	
    public int deleteStackerStockInfo( String sStock_id) throws DAOException{
		/*
		DELETE tb_ym_stacker
		WHERE stack_col_gp   = :stack_col_gp
		AND   stack_bed_gp   = :stack_bed_gp
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackerStockInfo";	
		Object[] params = {sStock_id};		
		return super.deleteData(queryCode, params);
	}
	/**
     * YJK
     * STACKLAYER TABLE DELETE
	 * 
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단
     *
     * @return 
     * @throws DAOException
     */	
    public int deleteStackLayerInfo( String sStackColGp,
									 String sStackBedGp,
									 String sStackLayerGp) throws DAOException{
		/*
		DELETE tb_ym_stacklayer
		WHERE stack_col_gp   = :stack_col_gp
		AND   stack_bed_gp   = :stack_bed_gp
		AND   stack_layer_gp = :stack_layer_gp
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackLayerInfo";	
		Object[] params = {sStackColGp,sStackBedGp,sStackLayerGp};		
		return super.deleteData(queryCode, params);
	}
    
    
	/**
     * YJK
     * STACKLAYER TABLE DELETE
	 * 
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단
     *
     * @return 
     * @throws DAOException
     */	
    public int deleteStackLayerStockInfo( String sStock_id) throws DAOException{
		/*
		DELETE tb_ym_stacklayer
		WHERE stack_col_gp   = :stack_col_gp
		AND   stack_bed_gp   = :stack_bed_gp
		AND   stack_layer_gp = :stack_layer_gp
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackLayerStockInfo";	
		Object[] params = {sStock_id};		
		return super.deleteData(queryCode, params);
	}
	
	/**
     * YJK
     * CRANE NO를 가지고 EQUIP TABLE에서 정보를 가져온다.
     * 
     * @param String	: 야드구분
     * @param String	: 설비번호
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getEquipInfoWithEquipNo(String sYdGp,
    										  String sEquipNo) throws DAOException{
		/*
		Ver3.1--
		SELECT 
			yd_gp,
			bay_gp,
			equip_kind,
			equip_no,
			equip_stat,
			work_mode,
			wprog_stat,
			wbook_id
		From tb_ym_equip	
		WHERE yd_gp      = :yd_gp
		AND   equip_no   = :crane_no
		AND   equip_kind = 'CR'
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getEquipInfoWithEquipNo";	
		Object[] params = {sYdGp,sEquipNo};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * CRANE NO를 가지고 우선순위가 가장 높은 스케쥴 정보를 가져온다.
     * 
     * @param String	: 야드구분
     * @param String	: 스케쥴진행상태
     * @param String	: 설비종류
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSchInfoWithEquipNo(String sYdGp,
    										String sSchWorkStat,	
                                            String sEquipNo) throws DAOException{
		/*
		Ver3.0--
		SELECT 
		   a.yd_gp,
		   a.bay_gp,
		   b.equip_kind,
		   b.equip_no,
		   a.stock_id,
		   a.sch_work_kind,
		   a.sch_work_aid_yn 
		FROM tb_ym_sch a, tb_ym_equip b 
		WHERE a.yd_gp			 	= :yd_gp
		AND   a.sch_work_equip_no 	= :crane_no
		AND   a.sch_work_stat       = :sch_work_stat
		AND   b.equip_kind 			= 'CR'
		AND   a.sch_work_equip_no 	= b.equip_no
		ORDER BY a.sch_work_stat,-- 작업진행상태
				 a.sch_wprefer, -- 작업우선순위
				 a.sch_id       -- Schedule Id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSchInfoWithEquipNo";	
		Object[] params = {sYdGp,sEquipNo,sSchWorkStat};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 운영중인 A열연 Equip_no를 가지고 현 시스템 Equip_No를 가져온다.
     * 
     * @param String	: 설비번호
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getCurEquipNoWithLegacyEquipNo(String sEquipNo) throws DAOException{
		/*
		SELECT substr(class3_cd,3) as equip_gp,
               equip_no  as crane_no
		FROM tb_cm_cdclass3 a, tb_ym_equip b 
		WHERE a.class3_name2 	= :equip_no
		AND   a.type_cd 		= 'YM002'
		AND   a.class1_cd 		= 'EQPNO'
		AND   a.class2_cd 		= '1'
		AND   a.class3_cd 		= b.equip_gp
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCurEquipNoWithLegacyEquipNo";	
		Object[] params = {sEquipNo};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 현 시스템 Equip_no(6자리)를 가지고 운영중인 A열연 Equip_No(5자리)를 가져온다.
     * 
     * @param String	: 설비번호
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getLegacyEquipNoWithCurEquipNo(String sEquipNo) throws DAOException{
		/*
		SELECT class3_name2	as equip_gp
		FROM  tb_cm_cdclass3 
		WHERE class3_cd  		= :class3_cd
		AND   type_cd 			= 'YM002'
		AND   class1_cd 		= 'EQPNO'
		AND   class2_cd 		= '1'
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getLegacyEquipNoWithCurEquipNo";	
		Object[] params = {sEquipNo};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 현 SCH_CODE 를 가지고 운영중인 A열연 SCH_CODE를 가져온다.
     * 
     * @param String	: 설비번호
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getLegacySchCode(String sSchcode) throws DAOException{
		/*
		VER 051223
		
		SELECT class2_name2 as sch_work_kind
		FROM tb_cm_cdclass2 
		WHERE class2_cd  	= :class3_cd
		AND   type_cd 		= 'YM104'
		AND   class1_cd 	= '1'
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getLegacySchCode";	
		Object[] params = {sSchcode};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
	/**
     * YJK
     * 적치가능한 Saddle 정보를 가져온다.
     * CST에서 작업지시 전문 발생시에 
     * Saddle To 위치 찾는 메소드
     * 
     * @param String	: 야드구분
     * @param String	: 동구분
     *
     * @return 
     * @throws DAOException
     */			
    public List getCtsSaddleToLocInfo(String sYdGp,
     								  String sBayGp) throws DAOException{
		/*
		Ver3.1--
		SELECT 
		     c.cts_relay_bay as flag,
		     c.equip_gp,
		     d.stack_layer_stat
		FROM tb_ym_stackcol a,
		     tb_ym_equip c,
		     tb_ym_stacklayer d
		WHERE a.yd_gp        = :yd_gp
		AND   a.bay_gp       = :bay_gp
		AND   a.stack_col_usage_cd = 'TS'-- To Saddle
		AND   c.equip_stat         = 'O' -- Sdeele 상태가 정상
		AND   a.stack_col_gp = c.equip_gp
		AND   a.stack_col_gp = d.stack_col_gp
		*/  
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCtsSaddleToLocInfo";	
		Object[] params = {sYdGp,sBayGp};	
		return super.findList(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 적치가능한 Saddle 정보에 해당하는 
     * 설비TABLE에 '*'을 마크한다.
     *
     * @param String	: 저장품ID
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateEquipSaddleInfo(String sEquipGp,String sFlag) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE tb_ym_equip
		SET	
			cts_relay_bay = :flag,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE equip_gp = :equip_gp
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEquipSaddleInfo";	
		Object[] params = {sFlag,sEquipGp};	
		return super.updateData(queryCode,params);
	}
	
    /**
     * YJK
     * 해당 권하위치(적치번지)의 적치BED
	 * 현재수량, 가능수량 을 셋팅한다.
	 * tb_ym_stacklayer Table stack_bed_qnty_curr :	적치BED수량현재
	 * tb_ym_stacklayer Table stack_bed_able_qnty :	적치BED가능수량
	 * 
     * @param String : 적치열구분
     * @param String : 적치BED구분
     * @param String : 현재수량
     * @param String : 가능수량
     *
     * @return 
     * @throws DAOException
     */		
    public int updateStackerQtyInfo(String sStackColGp,
		    						String sStackBedGp,
		    						String sCurrQty) throws DAOException{
		/* 
		Ver4.6--
		UPDATE tb_ym_stacker
		SET	(
			   stack_bed_qnty_curr,
			   stack_bed_able_qnty,
			   modifier,
		 	   mod_ddtt
			)= 
			(
			   SELECT 
			        case when to_number(nvl(stack_bed_qnty_curr,0) + :qty) < 0
			             then 0 else to_number(nvl(stack_bed_qnty_curr,0) + :qty)
			        end as cur_qnt,-- 적치BED수량현재
			        case when to_number(nvl(stack_bed_able_qnty,0) + (:qty*-1)) < 0
				         then 0 else to_number(nvl(stack_bed_able_qnty,0) + (:qty*-1))
				    end as able_qnt,-- 적치BED가능수량
			        'SYSTEM',
		 			sysdate     
			   FROM  tb_ym_stacker
			   WHERE stack_col_gp = :stack_col_gp
			   AND   stack_bed_gp = :stack_bed_gp
		    )
		WHERE stack_col_gp = :stack_col_gp
		AND   stack_bed_gp = :stack_bed_gp
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackerQtyInfo";
		Object[] params = {sCurrQty,sCurrQty,sCurrQty,sCurrQty,sStackColGp,sStackBedGp,sStackColGp,sStackBedGp};	
		return super.updateData(queryCode,params);
	
    }
    
    /**
     * YJK
     * 해당 권하위치(적치번지)의 적치BED
	 * 현재높이, 가능높이, 현재중량, 가능중량 을 셋팅한다.(SLAB)
	 * tb_ym_stacker Table stack_bed_high_curr : 적치BED높이현재
	 * tb_ym_stacker Table stack_bed_able_high : 적치BED가능높이
	 * tb_ym_stacker Table stack_bed_wt_curr   : 적치BED중량현재
	 * tb_ym_stacker Table stack_bed_able_wt   : 적치BED가능중량
	 * 
     * @param String : 적치열구분
     * @param String : 적치BED구분
     * @param String : 현재수량
     * @param String : 가능수량
     *
     * @return 
     * @throws DAOException
     */		
    public int updateStackerWtInfo(String sStackColGp,
		    					   String sStackBedGp,
		    					   String sSlabT,
				    			   String sSlabWt) throws DAOException{
		/* 
		Ver4.6--
		UPDATE tb_ym_stacker
		SET	(
			   stack_bed_high_curr, -- 적치BED높이현재
			   stack_bed_able_high, -- 적치BED가능높이
			   stack_bed_wt_curr,   -- 적치BED중량현재
			   stack_bed_able_wt,   -- 적치BED가능중량
			   modifier,
		 	   mod_ddtt
			)= 
			(
			   SELECT 
			        case when to_number(nvl(stack_bed_high_curr,0) + :height) < 0
				         then 0 else to_number(nvl(stack_bed_high_curr,0) + :height)
				    end as high_curr,-- 적치BED높이현재
				    case when to_number(nvl(stack_bed_able_high,0) + (:height*-1)) < 0
				         then 0 else to_number(nvl(stack_bed_able_high,0) + (:height*-1))
				    end as able_high,-- 적치BED가능높이
				    case when to_number(nvl(stack_bed_wt_curr,0) + :weight) < 0
				         then 0 else to_number(nvl(stack_bed_wt_curr,0) + :weight)
				    end as wt_curr,-- 적치BED중량현재
				    case when to_number(nvl(stack_bed_able_wt,0) + (:weight*-1)) < 0
				         then 0 else to_number(nvl(stack_bed_able_wt,0) + (:weight*-1))
				    end as able_wt,-- 적치BED가능중량
			        'SYSTEM',
		 			sysdate     
			   FROM  tb_ym_stacker
			   WHERE stack_col_gp = :stack_col_gp
			   AND   stack_bed_gp = :stack_bed_gp
		    )
		WHERE stack_col_gp = :stack_col_gp
		AND   stack_bed_gp = :stack_bed_gp
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackerWtInfo";
		Object[] params = {sSlabT,sSlabT,sSlabT,sSlabT,sSlabWt,sSlabWt,sSlabWt,sSlabWt,sStackColGp,sStackBedGp,sStackColGp,sStackBedGp};	
		return super.updateData(queryCode,params);
	
    }
    
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM001 - Coil을 제품 야드로 입고시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getYMDM001Info(String sStockId) throws DAOException{
		
		/*
		Ver4.2--
		SELECT 
			  a.stack_col_gp||a.stack_bed_gp||a.stack_layer_gp
			  as Put_Position
		FROM tb_ym_stacklayer a,
			 tb_ym_stackcol b,
			 tb_ym_stock c
		WHERE a.stock_id    		= :stock_id
		AND   a.stack_layer_stat 	in ('S','U','L')
		AND   a.stack_col_gp 		= b.stack_col_gp
		AND   a.stock_id            = c.stock_id
		--AND   b.stack_col_usage_cd 	in('G1','G2','G3','G4','G5')
		AND   c.stock_move_term     = 'H1' -- Coil 제품 입고완료
		
		UNION 
		
		SELECT 
			  a.stack_col_gp||a.stack_bed_gp||a.stack_layer_gp
			  as Put_Position
		FROM tb_ym_stacklayer a,
			 tb_ym_stackcol b,
			 tb_ym_stock c
		WHERE a.stock_id    		= :stock_id
		AND   a.stack_layer_stat 	= 'L'
		AND   a.stack_col_gp 		= b.stack_col_gp
		AND   a.stock_id            = c.stock_id
		AND   b.stack_col_usage_cd 	= 'CX' -- 대차
		AND   c.stock_move_term     = 'L1' -- 대차출하완료
		*/  
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYMDM001Info";	
		Object[] params = {sStockId,sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}
    
    
    /**
     * YJK
     * 저장품ID 항목을 가지고 차량스케줄 코드와 저장위치 가져오기
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getCarSchInfo(String sStockId) throws DAOException{
		

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCarSchInfo";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}
    
	
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * YMPO163 - 보류장에 반납 Coil 권하 시
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getYMPO163Info(String sStockId) throws DAOException{
		
		/*
		Ver4.2--
		SELECT 
			  a.stack_col_gp||a.stack_bed_gp||a.stack_layer_gp
			  as Put_Position
		FROM tb_ym_stacklayer a,
			 tb_ym_stackcol b,
			 tb_ym_stock c
		WHERE a.stock_id    		= :stock_id
		AND   a.stack_layer_stat 	in ('S','U','L')
		AND   a.stack_col_gp 		= b.stack_col_gp
		AND   a.stock_id            = c.stock_id
		AND   b.stack_col_usage_cd 	in('C1','C6','C8') -- 냉각장,정정보급대기장,보류장
		AND   c.stock_move_term     = 'J1' -- COIL 반납완료
		*/  
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYMPO163Info";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmCommonInfo(String sStockId) throws DAOException{    	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfo_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
    
	/**
	 * PIDEV
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmCommonInfoPI(String sStockId) throws DAOException{    	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfo_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}    
    
    /**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmCommonInfoNEW(String sStockId) throws DAOException{    	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfoNEW_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
    
    
    /**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmCommonInfoNEW2(String sStockId) throws DAOException{    	 		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfoNEW2_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
    
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmCommonInfo4(String sStockId, String sUpDown) throws DAOException{    	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfo4";	
		Object[] params = {sUpDown ,sStockId};	
		return super.findList(queryCode,params);
	}
    
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmCommonInfo5(String sCardNo, String sStockId, String sUpDown) throws DAOException{    	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfo5";	
		Object[] params = {sCardNo ,sUpDown ,sStockId};	
		return super.findList(queryCode,params);
	}
    
    /**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYdDmCommonInfo(String sStockId) throws DAOException{
    		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfo3_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}    
    
    /**
     * 임가공 PIDEV
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYdDmCommonInfoPI(String sStockId) throws DAOException{
    		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfo3_PIDEV";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}    
    
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmDmCommonInfo2(String sStockId) throws DAOException{
    	 	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmDmCommonInfoDM";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
    /**
     * 2008.01.11 이정훈
     * 
     * 이송하차시 임가공 구분 Setting
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public List getYmPoFrtoInfo(String sStockId) throws DAOException{
    	/*
select STL_NO,RENTPROC_COMCD 
    from TB_PT_STLFRTOMOVE
    where STL_NO = :stock_id
    and TRANSWORD_SEQNO = 
        ( select max(TRANSWORD_SEQNO) 
          from TB_PT_STLFRTOMOVE 
          where STL_NO = :stock_id
        )
     and RENTPROC_COMCD in ('A43901','A42829','A43617','A09107')
    	 */		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmPoFrtoInfo_PIDEV";	
		Object[] params = {sStockId,sStockId};	
		return super.findList(queryCode,params);
	}
    
    /**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM003 - Coil 제품창고 출고 상차 작업 시 첫 Coil 상차 권하 시.
     * YMDM004 - Coil 제품창고 출고 상차 작업 시 마지막 Coil 상차 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getYmPsCommonInfo(String SchCode, String CarCardNo) throws DAOException{
		
		/*
		SELECT COUNT(1) AS CNT
		FROM TB_YM_WBOOK A,
		     TB_YM_STOCK B
		WHERE A.WBOOK_ID = B.WBOOK_ID
		AND	  B.STOCK_ID  
			IN
			(
			    SELECT 
			        STOCK_ID
			    FROM TB_YM_STOCK 
			    WHERE CAR_CARD_NO = :CARCARDNO
			 )
		AND A.SCH_WORK_KIND = :SCHCODE
		*/  
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYmPsCommonInfo";	
		Object[] params = {CarCardNo, SchCode};	
		return super.findByPrimaryKey (queryCode,params);
	}
    
    /**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM008 - Coil을 제품 야드에서 동내이적 권하시인 경우에 해당하는지 체크한다.
     * @param String	: 저장품ID
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getYMDM008Info_01(String sStockId) throws DAOException{
		
		/*
		Ver4.2--
		SELECT z.crane_wrslt_up_loc,
		       z.crane_wrslt_put_loc
		FROM tb_ym_stackcol x,
		     tb_ym_stackcol y,
		    (
		    SELECT crane_wrslt_up_loc,
		           crane_wrslt_put_loc
		    FROM 
		        (
		        SELECT 
		            crane_wrslt_up_loc,
		            crane_wrslt_put_loc
		        FROM tb_ym_wrslt 
		        WHERE stock_id = :stock_id
		        ORDER BY crane_wrslt_id desc
		        )a
		    WHERE rownum = 1
		    )z
		WHERE x.stack_col_gp 		= substr(z.crane_wrslt_up_loc,0,6)  
		AND   x.stack_col_usage_cd 	like 'G%' -- 제품
		AND   y.stack_col_gp 		= substr(z.crane_wrslt_put_loc,0,6)  
		AND   y.stack_col_usage_cd 	like 'G%' -- 제품
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYMDM008Info_01";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM008 - Coil을 제품 야드에서 동간 이적 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getYMDM008Info_02(String sStockId) throws DAOException{
		
		/*
		Ver4.2--
		SELECT z.crane_wrslt_up_loc,
		       z.crane_wrslt_put_loc
		FROM tb_ym_stackcol x,
		     tb_ym_stackcol y,
		    (
		    SELECT max(decode(rownum,2,crane_wrslt_up_loc)) 
		           as crane_wrslt_up_loc,
		           max(decode(rownum,1,crane_wrslt_put_loc)) 
		           as crane_wrslt_put_loc
		    FROM 
		        (
		        SELECT 
		            crane_wrslt_up_loc,
		            crane_wrslt_put_loc
		        FROM tb_ym_wrslt 
		        WHERE stock_id = :stock_id
		        ORDER BY crane_wrslt_id desc
		        )a
		    WHERE rownum in(1,2)
		    )z
		WHERE x.stack_col_gp 		= substr(z.crane_wrslt_up_loc,0,6)  
		AND   x.stack_col_usage_cd 	like 'G%' -- 제품
		AND   y.stack_col_gp 		= substr(z.crane_wrslt_put_loc,0,6)  
		AND   y.stack_col_usage_cd 	like 'G%' -- 제품
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYMDM008Info_02";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * YMDM008 - Coil을 제품 야드에서 동간 이적 권하 시.
     * 인 경우에 해당하는지 체크한다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getYMDM008Info_03(String sStockId) throws DAOException{
		
		/*
		Ver4.2--
		SELECT crane_wrslt_up_loc,
	           crane_wrslt_put_loc
	    FROM 
	        (
	        SELECT 
	            crane_wrslt_up_loc,
	            crane_wrslt_put_loc
	        FROM tb_ym_wrslt 
	        WHERE stock_id = :stock_id
	        ORDER BY crane_wrslt_id desc
	        )a
	    WHERE rownum = 1
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getYMDM008Info_03";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}

	/**
     * (MCH)
     * 저장품ID 항목과 SLAB ORD_HCR_GP를 가져옴
     * @param String	: 저장품ID
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStockInfoWcrGp(String sStockId) throws DAOException{
		
		/*
		SELECT A.WBOOK_ID, B.ORD_HCR_GP ,B.ORD_YEOJAE_GP
		FROM TB_YM_STOCK A,
		   	 TB_PM_SLABCOMM B
		WHERE STOCK_ID = ?
		AND A.STOCK_ID = B.SLAB_NO(+)
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockInfoWcrGp";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}
    
	/**
     * 상차편성이 완료되었나 확인함(MCH)
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStockcolPtInfo(String sStakc_col_gp) throws DAOException{
		  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockcolPtInfo";	
		Object[] params = {sStakc_col_gp};	
		return super.findByPrimaryKey(queryCode,params);
	}
    
    
	/**
     * A열연 SLAB야드 ET_CAR 출발하기 전에 상차편성 완료됫나 체크함(MCH)
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStockcolStartOrder(String cardNo, String pos) throws DAOException{
		
		/*
		SELECT  
		        DM.FRTOMOVE_WORD_DATE ||
		        DM.FRTOMOVE_WORD_SEQNO AS FRTOMOVE_WORD_DATE_NO		
		FROM    TB_DM_SLABFRTOMOVEWORDCOMM  DM,
		        TB_PO_SLABFRTOMOVE          PO,
		        TB_YM_STOCK                 STOCK,
		        TB_YM_STACKLAYER            LAYER
		WHERE   DM.FRTOMOVE_WORD_DATE   = PO.FRTOMOVE_WORD_DATE
		AND     DM.FRTOMOVE_WORD_SEQNO  = PO.FRTOMOVE_WORD_SEQNO
		AND     PO.FRTOMOVE_STAT_CD     IN ('2', '1')--출하확인
		AND     PO.SLAB_NO              = STOCK.STOCK_ID
		AND     STOCK.STOCK_ID          = LAYER.STOCK_ID
		AND     STACK_LAYER_STAT IN ('L', 'S', 'U')
		AND     DM.CARD_NO = ?
		AND     LAYER.STACK_COL_GP = ?
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockcolStartOrder";	
		Object[] params = {cardNo, pos};	
		return super.findByPrimaryKey(queryCode,params);
	}    
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * 저장품정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStockInfo(String sStockId) throws DAOException{
		
		/*
		Ver4.2--
		SELECT *
		FROM tb_ym_stock
		WHERE stock_id = :stock_id
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockInfo_PIDEV";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * PIDEV
     * YJK
     * 저장품ID 항목을 가지고
     * 저장품정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStockInfoPI(String sStockId) throws DAOException{
		
		/*
		Ver4.2--
		SELECT *
		FROM tb_ym_stock
		WHERE stock_id = :stock_id
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockInfo_PIDEV";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}    
    
	/**
     * YJK
     * 저장품ID 항목을 가지고
     * 저장품정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getEmptyLoc(String sYdGp) throws DAOException{
		
		/*
		Ver4.2--
		SELECT STACK_COL_GP||STACK_BED_GP||STACK_LAYER_GP AS LOCATION
		FROM TB_YM_STACKLAYER
		WHERE STACK_COL_GP LIKE ?||'%'
		AND STOCK_ID IS NULL
		AND STACK_LAYER_STAT = 'E'
		AND ROWNUM = 1
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getEmptyLoc";	
		Object[] params = {sYdGp};	
		return super.findByPrimaryKey(queryCode,params);
	}
	/**
     * YJK
     *	To위치가 설비일 경우 설비상태도 체크한다.
	 *	- 현재는 B열연 대차설비상태만 체크한다.
	 *	- 하차작업완료 직후인 경우,즉 설비적재상태가 
	 *	  'U'-하차작업,'I'-Idle상태인 경우는 To위치 설정 불가
	 *     'L'인 경우에만 To 위치 가능하다.
	 *   - 상차스케쥴지정이면 상차스케쥴과 지정스케쥴이 같은지를 체크
	 *	  같으경우에만 To 위치 가능하다.
	 *	- 스케쥴미지정이면 현재 대차에 실려있는 코일이 있는지 체크
	 *	  코일이 존재하면 코일의 스케쥴코드와 지정 스케쥴이 같은지를 체크
	 *	  같은경우에만 To 위치 가능하다.
	 *	  코일이 존재하지 않으면 To 위치 가능하다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getToEquipState(String sStackColGp) throws DAOException{
		
		/*
		Ver4.5--
		SELECT 
		    equip_stat,             -- 설비상태(O,C)
		    stack_max_qnty,         -- 적재MAX수량
		    stack_stat,             -- 적재상태
		    wprog_stat,             -- 작업진행상태
		    wait_stop_loc,          -- 대기위치
		    curr_stop_loc,          -- 현재위치
		    carload_stop_loc,       -- 상차위치
		    carunload_stop_loc,     -- 하차동
		    carload_assign_yn,      -- 상차스케쥴지정
		    carload_sch_work_kind,  -- 상차스케쥴
		    carunload_assign_yn,    -- 하차스케쥴지정
		    carunload_sch_work_kind,-- 하차스케쥴
		    work_mode               -- 작업모드
		FROM tb_ym_equip a,
			(
			SELECT 
			    yd_gp||'X'||sect_gp||col_gp
			    as equip_gp
			FROM  TB_YM_STACKCOL
			WHERE stack_col_gp       = :stack_col_gp
			AND   yd_gp              = '3'
			AND   stack_col_usage_cd = 'CX'
			)b
		WHERE a.equip_gp = b.equip_gp
		
		SELECT 
		    equip_stat,             -- 설비상태(O,C)
		    stack_max_qnty,         -- 적재MAX수량
		    stack_stat,             -- 적재상태
		    wprog_stat,             -- 작업진행상태
		    wait_stop_loc,          -- 대기위치
		    curr_stop_loc,          -- 현재위치
		    carload_stop_loc,       -- 상차위치
		    carunload_stop_loc,     -- 하차동
		    carload_assign_yn,      -- 상차스케쥴지정
		    carload_sch_work_kind,  -- 상차스케쥴
		    carunload_assign_yn,    -- 하차스케쥴지정
		    carunload_sch_work_kind,-- 하차스케쥴
		    work_mode               -- 작업모드
		FROM tb_ym_equip
		WHERE equip_gp = SUBSTR(:STACK_COL_GP,0,1)||'X'||SUBSTR(:STACK_COL_GP,3)
		*/  
		//String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getToEquipState";	
//		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getToEquipState_01";	
    	// 최규성
    	// query code를 변경함. 최규성. AUTO_YN항목 추가함.
		String queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getToEquipState_02";
		Object[] params = {sStackColGp,sStackColGp};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 수조탱크의 휴지정보를 체크한다.
     * 휴지실적TABLE에 마지막 정보항목에 
     * 시작일자만 존재하면 휴지중..
     * 종료일자도 존재하면 휴지종료..
     * @param String	: 설비번호
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getToWtState(String sStackColGp) throws DAOException{
		
		/*
		Ver4.5--
		SELECT 
		    A.EQUIP_CD,
		    A.START_DDTT,
		    A.RECOVER_DDTT
		FROM TB_PO_DOWNWRSLT A,
		    (
		        SELECT 
		            PLANT_GP,PROC_GP,EQUIP_CD,
		            MAX(START_DDTT) AS START_DDTT
		        FROM TB_PO_DOWNWRSLT
		        WHERE PLANT_GP = 'B'
		        AND   PROC_GP  = 'T'
		        AND   EQUIP_CD   = :EQUIP_CD
		        AND   EQUIP_CD LIKE 'WT%'
		        GROUP BY PLANT_GP,PROC_GP,EQUIP_CD
		    )B
		WHERE A.PLANT_GP   = 'B'
		AND   A.PROC_GP    = 'T'
		AND   A.EQUIP_CD   = :EQUIP_CD
		AND   A.PLANT_GP   = B.PLANT_GP
		AND   A.PROC_GP    = B.PROC_GP
		AND   A.EQUIP_CD   = B.EQUIP_CD
		AND   A.START_DDTT = B.START_DDTT
		
		//수조탱크 조회화면 쿼리
		SELECT
		       TO_CHAR(TO_DATE(F.START_DDTT,'YYYY/MM/DD HH24:MI:SS'),'YYYY.MM.DD HH24:MI:SS')     AS 휴지시작일시,
		       TO_CHAR(TO_DATE(F.RECOVER_DDTT,'YYYY/MM/DD HH24:MI:SS'),'YYYY.MM.DD HH24:MI:SS')   AS 휴지종료일시,       
		       E.EQUIP_NAME     AS 설비명,
		       A.STACK_COL_GP   AS 위치,
		       B.STACK_BED_GP   AS 번지,
		       B.STACK_LAYER_GP AS 단,
		       B.STOCK_ID       AS 재료번호,
		       C.STOCK_ITEM     AS 저장품품목,
		       D.COIL_WT        AS 중량,
		       D.COIL_T         AS 두께,
		       D.COIL_W         AS 폭,
		       D.CURR_COIL_LEN  AS 길이,
		       B.STACK_LAYER_STAT           AS 적치상태,
		       B.STACK_LAYER_ACTIVE_STAT    AS 설비상태,
		       WATERIN_WATEROUT_GP          AS 입수배수구분,
		       START_END_GP                 AS 시작종료구분,
		       TO_CHAR(TO_DATE(WATERIN_START_DDTT,'YYYY/MM/DD HH24:MI:SS'),'YYYY.MM.DD HH24:MI:SS') AS 입수시작일시,
		       TO_CHAR(TO_DATE(WATERIN_END_DDTT,'YYYY/MM/DD HH24:MI:SS'),'YYYY.MM.DD HH24:MI:SS')   AS 입수종료일시,
		       TO_CHAR(TO_DATE(WATEROUT_START_DDTT,'YYYY/MM/DD HH24:MI:SS'),'YYYY.MM.DD HH24:MI:SS')AS 배수시작일시,
		       TO_CHAR(TO_DATE(WATEROUT_END_DDTT,'YYYY/MM/DD HH24:MI:SS'),'YYYY.MM.DD HH24:MI:SS')  AS 배수종료일시
		FROM TB_YM_STACKCOL A , TB_YM_STACKLAYER B , TB_YM_STOCK C , TB_PM_COILCOMM D, TB_YM_EQUIP E,
		     (
		        SELECT 
		            A.EQUIP_CD,
		            A.START_DDTT,
		            A.RECOVER_DDTT
		        FROM TB_PO_DOWNWRSLT A,
		            (
		                SELECT 
		                    PLANT_GP,PROC_GP,EQUIP_CD,
		                    MAX(START_DDTT) AS START_DDTT
		                FROM TB_PO_DOWNWRSLT
		                WHERE PLANT_GP = 'B'
		                AND   PROC_GP  = 'T'
		                AND   EQUIP_CD LIKE 'WT%'
		                GROUP BY PLANT_GP,PROC_GP,EQUIP_CD
		            )B
		        WHERE A.PLANT_GP   = 'B'
		        AND   A.PROC_GP    = 'T'
		        AND   A.PLANT_GP   = B.PLANT_GP
		        AND   A.PROC_GP    = B.PROC_GP
		        AND   A.EQUIP_CD   = B.EQUIP_CD
		        AND   A.START_DDTT = B.START_DDTT
		     )F 
		WHERE A.YD_GP               = ?             --야드구분(3)
		AND A.STACK_COL_USAGE_CD    = ?  --수조탱크코드(WT)
		AND A.STACK_COL_GP          = B.STACK_COL_GP
		AND B.STOCK_ID              = C.STOCK_ID(+)
		AND B.STOCK_ID              = D.COIL_NO(+)
		AND A.STACK_COL_GP          = E.EQUIP_GP(+)
		AND A.STACK_COL_GP          LIKE '3_'|| F.EQUIP_CD(+)
		ORDER BY 위치,번지

		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getToWtState";	
		Object[] params = {sStackColGp,sStackColGp};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 대차적치열 정보를 가지고 대차에 실려있는
     * 코일의 갯수를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getTCLoadCount(String sStackColGp) throws DAOException{
		
		/*
		Ver4.5--
		SELECT 
		    count(stock_id) as cnt
		FROM  TB_YM_STACKLAYER
		WHERE stack_col_gp = :stack_col_gp
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTCLoadCount";	
		Object[] params = {sStackColGp};	
		return super.findByPrimaryKey(queryCode,params);
	}
	/**
     * YJK
     * 코일공통 TABLE에서 코일 기본정보를 가져온다.
     * 
     * @param String	: 설비번호
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getCoilCommonInfo(String sStockId) throws DAOException{
		/*
		VER 051223
		SELECT
			PLANT_GP 		AS 공장구분,
			ORD_NO 			AS 제작번호,
			ORD_DTL 		AS 제작행번,
			COIL_T 			AS 코일두께,
			COIL_W 			AS 코일폭,
			CURR_COIL_LEN	AS 코일길이,
			COIL_INDIA 		AS 코일내경,
			COIL_OUTDIA 	AS 코일외경,
			COIL_WT 		AS 코일중량,
			NEXT_PROC 		AS 차공정,
			PLAN_PROC1      AS 계획공정,
		    BRANCH_CD 		AS 분기위치코드,
		    EXTEND_CONVEYOR_BRANCH_CD AS 확장분기위치코드,
		    HYSCO_TRANS_GP 	AS HYSCO이송수단,
		    COOL_METHOD 	AS 냉각방법,
			CURR_PROG_CD,
			RETURN_GP
		FROM  USRPMA.TB_PM_COILCOMM 
		WHERE COIL_NO = :COIL_NO   -- 재료번호(HE00001)
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilCommonInfo";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 코일공통 TABLE에서 코일 기본정보를 가져온다.
     * 
     * @param String	: 설비번호
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getCoilCommonInfo2(String sStockId) throws DAOException{
		/*
		VER 051223
		SELECT  
		    SUBSTR(C.CUST_PO_NO,1,10)		AS 주문번호,
			SUBSTR(C.CUST_PO_NO,11,5) 		AS 주문행번,
		    B.ORD_T           as 주문두께,   
		    B.ORD_W           as 주문폭, 
		    A.COIL_WT         as NET중량,    
		    A.COIL_INDIA      as 내경,     
		    A.COIL_OUTDIA     as 외경,
		    A.CURR_COIL_LEN   as 길이, 
		    A.COOL_METHOD     as 냉각방법,
		    A.HYSCO_TRANS_GP  as HYSCO이송수단
		    
		FROM  USRPMA.TB_PM_COILCOMM A,
		      USRPMA.TB_PM_ORDPROG B,
		      USRSMA.TB_SM_ORDDTL C
		WHERE A.COIL_NO = :COIL_NO
		AND   A.ORD_NO  = B.ORD_NO(+)
		AND   A.ORD_DTL = B.ORD_DTL(+)
		AND   A.ORD_NO  = C.ORD_NO(+) 
		AND   A.ORD_DTL = C.ORD_DTL(+)
		*/
		String  queryCode = "ym.steelinfo.steelinforecv.YdStockDAO.selectCoilComm";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
	/**
     * YJK
     * SLAB 공통 테이블에서 SLAB 정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSlabCommonInfo(String sSlabNo) throws DAOException{
		
		/*
		--ym.common.dao.selectSlabMatirialInfo
		--슬라브 재료 정보를 리턴한다.
		SELECT  SLAB_NO,
		        PLAN_SLAB_NO,               --예정 SLAB 번호
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
				SLAB_WO_RT_CD,              -- SLAB지시행선코드
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
		String  queryCode = "ym.common.dao.selectSlabMatirialInfo";	
		Object[] params = {sSlabNo};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * SLAB 이송 테이블에서 SLAB 정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSlabHeatWrsltInfo(String sHeatNo) throws DAOException{
		
		/*
		Ver051228--
		SELECT 
		   *
		FROM USRPMA.TB_PM_HEATWRSLTCOMM 
		WHERE HEAT_NO = :HEAT_NO
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSlabHeatWrsltInfo";	
		Object[] params = {sHeatNo};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 차량 CARD_NO 의 출하정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getDmCarInfo(String sStock_Id) throws DAOException{
		
		/*
		SELECT card_no, 
		       trans_com_cd, 
		       car_no, 
		       substr(car_no,length(car_no)-3) car_no_addr
		FROM TB_DM_CARCARDINFO
		WHERE card_no = (SELECT car_card_no FROM tb_ym_stock WHERE stock_id = :stock_id)
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getDmCarInfo_PIDEV";	
		Object[] params = {sStock_Id};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	
	/**
	 * 임가공 PIDEV
     * YJK
     * 차량 CARD_NO 의 출하정보를 가져온다.
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getDmCarInfoPI(String sStock_Id) throws DAOException{
		
		/*
		SELECT card_no, 
		       trans_com_cd, 
		       car_no, 
		       substr(car_no,length(car_no)-3) car_no_addr
		FROM TB_DM_CARCARDINFO
		WHERE card_no = (SELECT car_card_no FROM tb_ym_stock WHERE stock_id = :stock_id)
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getDmCarInfo_PIDEV";	
		Object[] params = {sStock_Id};	
		return super.findByPrimaryKey(queryCode,params);
	}    
    
	/**
     * YJK
     *
     * Coil 공통 Table 저장위치를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 권하위치
     *
     * @return int
     * @throws DAOException
     */			
	public int updateCoilCommonLocInfo(String sStockId,
								   	   String sPutLoc) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE tb_pm_coilcomm
		SET(  
		    yd_gp,              -- 야드구분
		    bay,                -- 동
		    span,               -- SPAN
		    col,                -- 적치열번지
		    cellno,             -- 적치번지
		    stack_layer,        -- 적치단
		    store_loc_cd,       -- 현 저장위치코드
		    befo_store_loc_cd   -- 전 저장위치코드
		   )=
		   (
		    SELECT 
		        substr(:pos,1,1),-- 야드구분
		        substr(:pos,2,1),-- 동
		        substr(:pos,3,2),-- SPAN
		        substr(:pos,5,2),-- 적치열번지
		        substr(:pos,7,2),-- 적치번지
		        substr(:pos,9,2),-- 적치단
		        :pos,            -- 현 저장위치코드   
		        store_loc_cd     -- 전 저장위치코드
		    FROM tb_pm_coilcomm
		    WHERE coil_no = :coil_no
		   )
		WHERE coil_no = :coil_no
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCoilCommonLocInfo";	
		Object[] params = {sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sStockId,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * SLAB 공통 Table 저장위치를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 권하위치
     *
     * @return int
     * @throws DAOException
     */			
	public int updateSlabCommonLocInfo(String sStockId,
								   	   String sPutLoc) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE tb_pm_slabcomm
        SET(  
            yd_gp,              -- 야드구분
            bay,                -- 동
            span,               -- SPAN
            col,                -- 적치열번지
            cellno,             -- 적치번지
            stack_layer,        -- 적치단
            store_loc_cd,       -- 현 저장위치코드
            befo_store_loc_cd,      -- 전 저장위치코드
            befobefo_store_loc_cd   -- 전전 저장위치코드
           )=
           (
            SELECT 
                substr(:pos,1,1),-- 야드구분
                substr(:pos,2,1),-- 동
                substr(:pos,3,2),-- SPAN
                substr(:pos,5,2),-- 적치열번지
                substr(:pos,7,2),-- 적치번지
                substr(:pos,9,2),-- 적치단
                :pos,            -- 현 저장위치코드   
                store_loc_cd,    -- 전 저장위치코드
                befo_store_loc_cd --전전 저장위치코드
            FROM tb_pm_slabcomm
            WHERE slab_no = :slab_no
           )
        WHERE slab_no = :slab_no
		*/
		
		//주편공통 진행 상태가 진행중(2)인 경우 주편공통을 update 하고 아닌 경우 slab공통을 update 한다.
		String  queryCode = "";
		int		chk=0;
		String  queryCode2 = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSlabCommonInfo";	
		Object[] params2 = {sStockId};	
		//YmCommonUtil.putLog("", "", "test###########################>>>>>>>>>>>>", 3);
		JDTORecord params3 = super.findByPrimaryKey(queryCode2,params2);
		if(params3!=null){//주편공통 진행 상태가 진행중(2)인 경우
			queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonLocInfo";	
			Object[] params = {sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sStockId,sStockId};	
			chk = super.updateData(queryCode,params);
		}
		
		queryCode2 = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSlabCommonInfo2";	
		JDTORecord params4 = super.findByPrimaryKey(queryCode2,params2);
		if(params4 !=null){//슬라브공통이 존재 하는 경우
			queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateSlabCommonLocInfo";	
			Object[] params1 = {sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sPutLoc,sStockId,sStockId};	
			chk = super.updateData(queryCode,params1);
		}
		
		//보온뱅크적치유무
		sPutLoc =sPutLoc.substring(2 , 4);
		
		if("BK".equals(sPutLoc)){
			//주편(보온뱅크적치유무 , 보온뱅크장입시간)
			queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateMslabCommonSubInfo";	
			Object[] params5 = {sStockId};	
			chk = super.updateData(queryCode,params5);
			
			//슬라브(보온뱅크적치유무 , 보온뱅크장입시간)
			queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateSlabCommonSubInfo";		
			chk = super.updateData(queryCode,params5);
			
		}
		
		return chk; 
	}
	
	/**
     * YJK
     *
     * Coil 공통 Table 냉각완료구분 항목을 UPDATE한다.
     *
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int updateFrToDoneInfo(String sStockId) throws DAOException
	{	
		 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateFrToDoneInfo";	
		Object[] params = {sStockId};	
		return super.updateData(queryCode,params);
	}
	
	
	/**
     * YJK
     *
     * Coil 공통 Table 냉각완료구분 항목을 UPDATE한다.
     *
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int updateFrToDoneInfo2(String sStockId) throws DAOException
	{	
		/*
		UPDATE TB_YD_STOCK
		SET YD_RCPT_DATE =TO_CHAR(SYSDATE,'YYYYMMDD')
		WHERE STL_NO = :coil_no
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateFrToDoneInfo2";	
		Object[] params = {sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * Coil 공통 Table 냉각완료구분 항목을 UPDATE한다.
     *
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int updateCommonCoilCoolDoneInfo(String sStockId) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE tb_pm_coilcomm
		SET	cool_done_gp = 'Y'
		WHERE coil_no = :coil_no
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCommonCoilCoolDoneInfo";	
		Object[] params = {sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     * 작업예약ID와 저장품ID 정보를 가지고
     * 스케쥴 정보를 가져온다.	
	 * 
     * @param String	: 작업예약ID
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSchInfoWithWbookId(String sWbookId,
    										String sStockId) throws DAOException{
		
		/*
		SELECT *
		FROM tb_ym_sch
		WHERE wbook_id = :wbook_id
		AND   stock_id = :stock_id
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSchInfoWithWbookId";	
		Object[] params = {sWbookId,sStockId};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * SCH ID 정보를 가지고
     * 스케쥴 정보를 가져온다.	
	 * 
     * @param String	: SCH ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getSchInfoWithSchId(String sSchId) throws DAOException{
		
		/*
		SELECT *
		FROM tb_ym_sch
		WHERE sch_id = :sch_id
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSchInfoWithSchId";	
		Object[] params = {sSchId};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * EQUIP_GP 정보를 가지고
     * 설비 정보를 가져온다.	
	 * 
     * @param String	: equip_gp
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getEquipInfoWithEquipGp(String sEquipGp) throws DAOException{
		
		/*
		SELECT *
		FROM tb_ym_equip
		WHERE equip_gp = :equip_gp
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getEquipInfoWithEquipGp";	
		Object[] params = {sEquipGp};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품ID에 해당하는 적치단 Table의 
     * Stack_layer_stat 값을 Update한다.
     *
     * @param String	: 적치단활성상태
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStackLayerStatWithStockId(String sStackLayerStat,
											   String sStockId) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE tb_ym_stacklayer
		SET	stack_layer_stat = :stack_layer_stat
		WHERE stock_id = :stock_id
		and stack_layer_stat in ('L','S','U')
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackLayerStatWithStockId";	
		Object[] params = {sStackLayerStat,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     * 적치단 위치정보 상태를 변경한다.
     *
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * @param String	: 적치상태      
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateCraneStackLayerActivStat(String sStackColGp,
		    								  String sStackBedGp,
		    								  String sStackLayerGp,
		    								  String sStackLayerActivStat) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STACKLAYER
		SET 
			stack_layer_active_stat = :stack_layer_active_stat,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE stack_col_gp   = :stack_col_gp 
		AND   stack_bed_gp   = :stack_bed_gp 
		AND   stack_layer_gp = :stack_layer_gp 
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerActivStat";
		Object[] params = {sStackLayerActivStat,sStackColGp,sStackBedGp,sStackLayerGp};	
		return super.updateData(queryCode,params);
    }
	
	/**
     * YJK
     * 적치열 CARD_NO 삭제
	 * tb_ym_stackcol Table : car_card_no  = ''(삭제) 
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateStackColCardNo(String sStackColGp) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STACKCOL
		SET 
			car_card_no= null,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE stack_col_gp   = :stack_col_gp 
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackColCardNo";
		Object[] params = {sStackColGp};	
		return super.updateData(queryCode,params);
    }
    
	/**
     * YJK
     * 기존에 편성된 긴급작업을 초기화 한다.
	 *  
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 크레인번호
     *
     * @return 
     * @throws DAOException
     */		
    public int updateCraneSchClaer(String sYdGp,
								   String sBayGp,
								   String sCraneNo) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
	    SET sch_wprefer = 
	        (
		    SELECT wprefer
			FROM
			    (
			    SELECT
			        decode(sch_rule_active_stat,
			        	   'A', sch_rule_wprefer, 
			        	   'B', sch_rule_alter_wprefer, 
			        	   sch_rule_wprefer) as wprefer
			    FROM tb_ym_schrule
			    WHERE yd_gp         = :yd_gp
			    AND   bay_gp        = :bay_gp                
			    AND   sch_work_kind = ( SELECT sch_work_kind
			                            FROM tb_ym_sch
			                            WHERE yd_gp             = :yd_gp
			                            AND   bay_gp            = :bay_gp
			                            AND   sch_work_equip_no = :crane_no
			                            AND   sch_wprefer       = '0'
			                            AND   rownum = 1)
			    AND   rownum = 1                
			    UNION 
			    SELECT 99 as wprefer FROM DUAL
			    )
			WHERE rownum = 1  
		    )
	    WHERE sch_id in (                
	        SELECT sch_id
	        FROM tb_ym_sch
	        WHERE yd_gp             = :yd_gp
	        AND   bay_gp            = :bay_gp
	        AND   sch_work_equip_no = :crane_no
	        AND   sch_wprefer       = '0'
	    )
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneSchClaer";
		Object[] params = {sYdGp,sBayGp,sYdGp,sBayGp,sCraneNo,sYdGp,sBayGp,sCraneNo};	
		return super.updateData(queryCode,params);
    }
    
    /**
     * YJK
     * 해당 정보를 긴급작업으로 편성한다.
	 *  
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 크레인번호
     * @param String	: 스케쥴 코드
     *
     * @return 
     * @throws DAOException
     */		
    public int updateEmergencySchInfo(String sYdGp,
									  String sBayGp,
									  String sCraneNo,
									  String sSchCode) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
		SET 
			sch_wprefer = '0',
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE yd_gp 			= :yd_gp
		AND   bay_gp 			= :bay_gp
		AND   sch_work_equip_no = :sch_work_equip_no
		AND   sch_work_kind 	= :sch_work_kind
		AND   rownum <= 1
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEmergencySchInfo";
		Object[] params = {sYdGp,sBayGp,sCraneNo,sSchCode};	
		return super.updateData(queryCode,params);
    }
    /**
     * 최규성
     * 해당 정보를 긴급작업으로 편성한다.
	 *  
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 크레인번호
     * @param String	: 스케쥴 코드
     * @param String	: 스케줄 ID
     * 
     * @return 
     * @throws DAOException
     */		
    public int updateEmergencySchInfo_spm2(String sYdGp,
									  String sBayGp,
									  String sCraneNo,
									  String sSchCode,
									  String sSchId) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
		SET 
			sch_wprefer = '0',
			
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE yd_gp 			= :yd_gp
		AND   bay_gp 			= :bay_gp
		AND   sch_work_equip_no = :sch_work_equip_no
		AND   sch_work_kind 	= :sch_work_kind
		AND   sch_id            = :sch_Id
		AND   rownum <= 1
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEmergencySchInfo_spm2";
		Object[] params = {sYdGp,sBayGp,sCraneNo,sSchCode,sSchId};	
		return super.updateData(queryCode,params);
    }
    
    public int updateEmergencySchInfo_01(String sWbookId) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
		SET 
			sch_wprefer = '0',
			modifier   	= 'SYSTEM',
		 	mod_ddtt	= sysdate     
		WHERE wbook_id = :wbook_id
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEmergencySchInfo_01";
		Object[] params = {sWbookId};	
		return super.updateData(queryCode,params);
    }
    
     public int updateEmergencySchInfo_02(String sSchId) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
		SET 
			sch_wprefer = '0',
			modifier   	= 'SYSTEM',
		 	mod_ddtt	= sysdate     
		WHERE sch_id = :sch_id
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEmergencySchInfo_02";
		Object[] params = {sSchId};	
		return super.updateData(queryCode,params);
    }
    
	/**
	 * YJK
     * 적치기준적용 기준정보를 가져온다.
     *
     * @param String	: 적치열정보
     * @param String	: 적기기준코드
     *
     * @return JDTORecord 적치기준적용 기준정보
     * @throws DAOException
     */
	public JDTORecord getStackRuleInfo_002(String sStackColGp,
										   String sStackRuleCd) throws DAOException
	{	
		/*
		Ver1.0--
		SELECT 
		    b.yd_gp,
		    b.bay_gp,
		    b.stack_col_usage_cd,
		    b.stack_rule_cd,
		    b.stack_rule_name,
		    b.stack_rule_use_yn,
		    b.stack_rule_type, 
		    b.stack_rule_unit,
		    b.stack_rule_min,
		    b.stack_rule_max,
		    b.stack_rule_contents
		FROM tb_ym_stackcol     a,
		     tb_ym_stackrule    b
		WHERE a.stack_col_gp        = :stack_col_gp
		AND   a.yd_gp               = b.yd_gp
		AND   a.bay_gp              = b.bay_gp
		AND   a.stack_col_usage_cd  = b.stack_col_usage_cd
		AND   b.stack_rule_cd       = :stack_rule_cd
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackRuleInfo_002";	
		Object[] params = {sStackColGp,sStackRuleCd};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 저장품TABLE에 이동설비항목 및 중계목적동 항목을 셋팅한다.
	 * tb_ym_stock Table frtomove_equip_gp : CTS이동설비
	 * tb_ym_stock Table cts_relay_saddle  : 목적Saddle
	 * tb_ym_stock Table cts_relay_yn  	   : 중계구분항목
	 * 
     * @param String : 저장품ID
     * @param String : 이동설비구분
     * @param String : 이동설비BED구분
     * @param String : 이동설비단구분
     * @param String : 목적동
     * @param String : 중계구분항목
     *
     * @return 
     * @throws DAOException
     */		
    public int updateStockMoveEquipInfo(String sStockId,
    							   		String sFrtomoveEquipGp,
    							   		String sFrtomoveEquipBedGp,
    							   		String sFrtomoveEquipLayerGp,
    							   		String sCtsRelaySaddle,
    							   		String sCtsRelayYn) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_stock
		SET 
			frtomove_equip_gp 		= :frtomove_equip_gp,
			frtomove_equip_bed_gp 	= :frtomove_equip_bed_gp,
			frtomove_equip_layer_gp = :frtomove_equip_layer_gp,
			cts_relay_saddle 		= :cts_relay_saddle,
			cts_relay_yn			= :cts_relay_yn,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE stock_id 		= :stock_id
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockMoveEquipInfo";
		Object[] params = {sFrtomoveEquipGp,sFrtomoveEquipBedGp,sFrtomoveEquipLayerGp,sCtsRelaySaddle,sCtsRelayYn,sStockId};	
		return super.updateData(queryCode,params);
	
    }
    
    /**
     * YJK
     * 저장품TABLE에 이동설비항목 및 중계목적동 항목을 셋팅한다.
	 * tb_ym_stock Table frtomove_equip_gp : CTS이동설비
	 * tb_ym_stock Table cts_relay_saddle  : 목적Saddle
	 * tb_ym_stock Table cts_relay_yn  	   : 중계구분항목
	 * 
     * @param String : 저장품ID
     * @param String : 이동설비구분
     * @param String : 이동설비BED구분
     * @param String : 이동설비단구분
     * @param String : 목적동
     * @param String : 중계구분항목
     *
     * @return 
     * @throws DAOException
     */		
    public int updateSlabMoveEquipInfo_01(String sStockId,
									   	  String sFrtomoveEquipGp,
									   	  String sFrtomoveEquipBedGp,
									   	  String sFrtomoveEquipLayerGp) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_stock
		SET 
			frtomove_equip_gp 		= :frtomove_equip_gp,
			frtomove_equip_bed_gp 	= :frtomove_equip_bed_gp,
			frtomove_equip_layer_gp = :frtomove_equip_layer_gp
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE stock_id 		= :stock_id
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockMoveEquipInfo_01";
		Object[] params = {sFrtomoveEquipGp,sFrtomoveEquipBedGp,sFrtomoveEquipLayerGp,sStockId};	
		return super.updateData(queryCode,params);
	
    }
    
	/**
     * YJK
	 * 동간추출시 목적동정보를 셋팅
	 *	
     * @param String	: 저장품ID
     * @param String	: 목적동
     *
     * @return int
     * @throws DAOException
     */	
    public int updateStockPutLocWithStockId(String sStockID,
    							  			String sBay) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STOCK
		SET
		    CARUNLOAD_PUT_LOC    = :Bay,	
		    modifier    = 'SYSTEM',
		 	mod_ddtt    = sysdate     
		WHERE STOCK_ID 	= :stock_id            
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockPutLocWithStockId";
		Object[] params = {sBay,sStockID};	
		return super.updateData(queryCode,params);
    }
    
    /**
     * YJK
	 * W/B 보급완료시 CHARGE_LOT_NO 항목 셋팅
	 *	
     * @param String	: 저장품ID
     * @param String	: 목적동
     *
     * @return int
     * @throws DAOException
     */	
    public int updateStockLotNoWithStockId(String sStockID,
    							  		   String sChangeLotNo) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STOCK
		SET
		    CHARGE_LOT_NO = :CHARGE_LOT_NO,	
		    modifier    = 'SYSTEM',
		 	mod_ddtt    = sysdate     
		WHERE STOCK_ID 	= :stock_id            
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockLotNoWithStockId";
		Object[] params = {sChangeLotNo,sStockID};	
		return super.updateData(queryCode,params);
    }
    
    /**
     * YJK
	 * 동간추출시 목적동정보를 셋팅
	 *	
     * @param String	: 저장품ID
     * @param String	: 목적동
     *
     * @return int
     * @throws DAOException
     */	
    public int updateStockSupplyGpWithStockId(String sStockID,
    							  			  String sSupplyGp,
    							  			  String sSupplyDt) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STOCK
		SET
		    SHEAR_SUPPLY_GP    		 = :SHEAR_SUPPLY_GP,	
		    SHEAR_SUPPLY_DEMAND_DDTT = :SHEAR_SUPPLY_DEMAND_DDTT,
		    modifier    = 'SYSTEM',
		 	mod_ddtt    = sysdate     
		WHERE STOCK_ID 	= :stock_id            
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockSupplyGpWithStockId";
		Object[] params = {sSupplyGp,sSupplyDt,sStockID};	
		return super.updateData(queryCode,params);
    }
 	
 	/**
     * YJK
	 * 보조작업 저장품ID 에 Wbook_id,stock_move_term 셋팅
	 *	
     * @param String	: 저장품ID
     * @param String	: 작업예약ID
     *
     * @return int
     * @throws DAOException
     */	
    public int updateStockWbookId(String sStockID,
    							  String sWbookId) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STOCK
		SET
		    WBOOK_ID    = :wbook_id,	
		    modifier    = 'SYSTEM',
		 	mod_ddtt    = sysdate     
		WHERE STOCK_ID 	= :stock_id            
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockWbookId";
		Object[] params = {sWbookId,sStockID};	
		return super.updateData(queryCode,params);
    }
    
	/**
     * YJK
	 * Sch 삭제 저장품 CARUNLOAD_PUT_LOC(하차 PUT 위치) 삭제
	 *	
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */	
    public int updateStockWbookId_01(String sStockID) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STOCK
		SET
		    WBOOK_ID    = '',
		    CARUNLOAD_PUT_LOC = '',	
		    modifier    = 'SYSTEM',
		 	mod_ddtt    = sysdate     
		WHERE STOCK_ID 	= :stock_id            
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockWbookId_01";
		Object[] params = {sStockID};	
		return super.updateData(queryCode,params);
    }
    
    /**
     * YJK
     *
     * 저장품Table에 CST 중계동 Saddle항목에  
     * 특정마크를 한다.
     *
     * @param String	: 저장품ID
     * @param String	: 플래그
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockSaddleInfo(String sStockId,String sFlag) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE tb_ym_stock
		SET	
			cts_relay_saddle = :flag,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE stock_id = :stock_id
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockSaddleInfo";	
		Object[] params = {sFlag,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품Table에 CST 중계동 Saddle항목에  
     * 특정마크를 한다.
     *
     * @param String	: 저장품ID
     * @param String	: 플래그
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockSaddleInfo_02(String sStockId) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE Tb_Ym_Stock
		SET	
			Charge_Lot_No = (Select LPAD(cts_relay_saddle,6,'0') From Tb_Ym_Stock Where Stock_id = :Stock_id)
		WHERE Stock_id = :Stock_id

		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockSaddleInfo_02";	
		Object[] params = {sStockId,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo(String sStockId,
									String sStockMoveTerm) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE tb_ym_stock
		SET	
			stock_move_term = :stock_move_term,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE stock_id = :stock_id
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockTransInfo";	
		Object[] params = {sStockMoveTerm,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo2(String sStockId,
									String sStockMoveTerm,
									String sStockUsedtype) throws DAOException
	{	
		/*
		UPDATE tb_ym_stock
	    SET	
	        stock_move_term = DECODE(STOCK_MOVE_TERM,'BD',STOCK_MOVE_TERM, :stock_move_term) ,
	        YD_RULE_PL_RS_GP =DECODE(:sStockUsedtype,'SE',NULL,YD_RULE_PL_RS_GP)
	        modifier   = 'SYSTEM',
	        mod_ddtt   = sysdate     
	    WHERE stock_id = :stock_id
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockTransInfo2";	
		Object[] params = {sStockMoveTerm,sStockUsedtype, sStockId};	
		return super.updateData(queryCode,params);
	}
	
	
	/**
     * JGK
     *
     * 재료공통 TABLE 에서 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int requestupdateData(String sStockId) throws DAOException
	{	
		/*
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
		    select 
		        'callStartLastWork',
		        sysdate,
		        'A',
		        CURR_PROG_CD_REG_PGM,
		        CURR_PROG_REG_DDTT,
		        DECODE(STL_APPEAR_GP, 'Y', CURR_PROG_CD,(
                          case
                            when ORD_YEOJAE_GP = '1' then 'B'
                            ELSE 'Y'
                          END)) CURR_PROG_CD,   
		        BEFO_PROG_CD_REG_PGM,
		        BEFO_PROG_REG_DDTT,
		        BEFO_PROG_CD
		    from TB_PT_COILCOMM
		    where COIL_NO = :COIL_NO
		    )
		WHERE COIL_NO = :COIL_NO
		
		*/
 
		String  queryCode = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstCoil";	
		Object[] params = {sStockId,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	
	/**
     * JGK
     *
     * 재료공통 TABLE 에서 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int requestupdateData2(String sStockId) throws DAOException
	{	
		/*
	--주편공통 업데이트(하차완료)
	UPDATE TB_PT_COILCOMM
		SET(
            HR_PLNT_GP,
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
		    select 
                'T', --임가공사 인경우에만 추가 한다.
		        'callStartLastWork',
		        sysdate,
		        DECODE(A.STL_APPEAR_GP, 'Y', A.CURR_PROG_CD,(
                          case
                            when A.ORD_YEOJAE_GP = '1' then 'B'
                            ELSE 'Y'
                          END)) CURR_PROG_CD,
		        A.CURR_PROG_CD_REG_PGM,
		        A.CURR_PROG_REG_DDTT,
		        A.CURR_PROG_CD,   
		        A.BEFO_PROG_CD_REG_PGM,
		        A.BEFO_PROG_REG_DDTT,
		        A.BEFO_PROG_CD
		    from TB_PT_COILCOMM A
                 ,USRPTA.TB_PT_STLFRTOMOVE B
		    where A.COIL_NO =B.STL_NO(+)
              AND A.COIL_NO = :COIL_NO
              AND B.TRANSWORD_SEQNO = (SELECT MAX(TRANSWORD_SEQNO) 
                                         FROM TB_PT_STLFRTOMOVE C
                                        WHERE C.STL_NO=B.STL_NO )
		    )
		WHERE COIL_NO = :COIL_NO
		
		*/
 
		String  queryCode = "ym.facilitywork.putwrecord.session.updateMatlFtmvWlrstCoil2";	
		Object[] params = {sStockId,sStockId};	
		return super.updateData(queryCode,params);
	}
	
   	/**
     * YJK
	 * CTS SADDLE 목적동 정보를 셋팅한다.
	 *	
     * @param String	: FROM SADDLE
     * @param String	: TO SADDLE
     *
     * @return int
     * @throws DAOException
     */	
    public int updateEquipPutLocWithEquipGp(String sFromSaddle,
    							  			String sToSaddle) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_EQUIP
		SET
		    carunload_stop_loc = :to_saddle,	
		    modifier    = 'SYSTEM',
		 	mod_ddtt    = sysdate     
		WHERE EQUIP_GP 	= :equip_gp            
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEquipPutLocWithEquipGp";
		Object[] params = {sToSaddle,sFromSaddle};	
		return super.updateData(queryCode,params);
    }
	
	public int updateEquipCurLocWithEquipGp(String sEquipGp,
    							  		    String sLoc) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_EQUIP
		SET
		    curr_stop_loc = :to_loc,	
		    modifier    = 'SYSTEM',
		 	mod_ddtt    = sysdate     
		WHERE EQUIP_GP 	= :equip_gp            
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEquipCurLocWithEquipGp";
		Object[] params = {sLoc,sEquipGp};	
		return super.updateData(queryCode,params);
    }
     
	/**
	 * YJK
     * 적치기준적용 기준정보를 가져온다.
     *
     * @return JDTORecord 적치기준적용 기준정보
     * @throws DAOException
     */
	public JDTORecord getStackRuleInfo_003() throws DAOException
	{	
		/*
		Ver1.0--
		SELECT 
	       NVL(MAX(decode(STACK_RULE_CD,'UPCD',STACK_RULE_MAX)),'0') AS UCARD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'UPDT',STACK_RULE_MAX)),'0') AS UINPUT_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'UPML',STACK_RULE_MAX)),'0') AS UMILL_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'UPNL',STACK_RULE_MAX)),'0') AS UNEXT_PROC,
	       NVL(MAX(decode(STACK_RULE_CD,'UPNO',STACK_RULE_MAX)),'0') AS UORD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'UPOR',STACK_RULE_MAX)),'0') AS UORD_DTL,
	       NVL(MAX(decode(STACK_RULE_CD,'UPPC',STACK_RULE_MAX)),'0') AS UPROG_CD,
	       NVL(MAX(decode(STACK_RULE_CD,'UPST',STACK_RULE_MAX)),'0') AS USPEC,
	       NVL(MAX(decode(STACK_RULE_CD,'DLCD',STACK_RULE_MAX)),'0') AS LCARD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'DLDT',STACK_RULE_MAX)),'0') AS LINPUT_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'DLML',STACK_RULE_MAX)),'0') AS LMILL_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'DLNL',STACK_RULE_MAX)),'0') AS LNEXT_PROC,
	       NVL(MAX(decode(STACK_RULE_CD,'DLNO',STACK_RULE_MAX)),'0') AS LORD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'DLOR',STACK_RULE_MAX)),'0') AS LORD_DTL,
	       NVL(MAX(decode(STACK_RULE_CD,'DLPC',STACK_RULE_MAX)),'0') AS LPROG_CD,
	       NVL(MAX(decode(STACK_RULE_CD,'DLST',STACK_RULE_MAX)),'0') AS LSPEC,
	       NVL(MAX(decode(STACK_RULE_CD,'DLLS',STACK_RULE_MAX)),'0') AS LSTAT,
	       NVL(MAX(decode(STACK_RULE_CD,'DRCD',STACK_RULE_MAX)),'0') AS RCARD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'DRDT',STACK_RULE_MAX)),'0') AS RINPUT_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'DRML',STACK_RULE_MAX)),'0') AS RMILL_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'DRNL',STACK_RULE_MAX)),'0') AS RNEXT_PROC,
	       NVL(MAX(decode(STACK_RULE_CD,'DRNO',STACK_RULE_MAX)),'0') AS RORD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'DROR',STACK_RULE_MAX)),'0') AS RORD_DTL,
	       NVL(MAX(decode(STACK_RULE_CD,'DRPC',STACK_RULE_MAX)),'0') AS RPROG_CD,
	       NVL(MAX(decode(STACK_RULE_CD,'DRST',STACK_RULE_MAX)),'0') AS RSPEC,
	       NVL(MAX(decode(STACK_RULE_CD,'DRLS',STACK_RULE_MAX)),'0') AS RSTAT,
	       NVL(MAX(decode(STACK_RULE_CD,'MX11',STACK_RULE_MAX)),'0') AS MX11,
	       NVL(MAX(decode(STACK_RULE_CD,'MX21',STACK_RULE_MAX)),'0') AS MX21,
	       NVL(MAX(decode(STACK_RULE_CD,'MXE1',STACK_RULE_MAX)),'0') AS MXE1,
	       NVL(MAX(decode(STACK_RULE_CD,'MX13',STACK_RULE_MAX)),'0') AS MX13,
	       NVL(MAX(decode(STACK_RULE_CD,'MX23',STACK_RULE_MAX)),'0') AS MX23,
	       NVL(MAX(decode(STACK_RULE_CD,'MXE3',STACK_RULE_MAX)),'0') AS MXE3,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1A',STACK_RULE_MAX)),'0') AS MX1A,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2A',STACK_RULE_MAX)),'0') AS MX2A,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEA',STACK_RULE_MAX)),'0') AS MXEA,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1B',STACK_RULE_MAX)),'0') AS MX1B,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2B',STACK_RULE_MAX)),'0') AS MX2B,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEB',STACK_RULE_MAX)),'0') AS MXEB,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1C',STACK_RULE_MAX)),'0') AS MX1C,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2C',STACK_RULE_MAX)),'0') AS MX2C,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEC',STACK_RULE_MAX)),'0') AS MXEC,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1D',STACK_RULE_MAX)),'0') AS MX1D,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2D',STACK_RULE_MAX)),'0') AS MX2D,
	       NVL(MAX(decode(STACK_RULE_CD,'MXED',STACK_RULE_MAX)),'0') AS MXED,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1E',STACK_RULE_MAX)),'0') AS MX1E,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2E',STACK_RULE_MAX)),'0') AS MX2E,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEE',STACK_RULE_MAX)),'0') AS MXEE,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1F',STACK_RULE_MAX)),'0') AS MX1F,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2F',STACK_RULE_MAX)),'0') AS MX2F,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEF',STACK_RULE_MAX)),'0') AS MXEF,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1G',STACK_RULE_MAX)),'0') AS MX1G,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2G',STACK_RULE_MAX)),'0') AS MX2G,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEG',STACK_RULE_MAX)),'0') AS MXEG,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1H',STACK_RULE_MAX)),'0') AS MX1H,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2H',STACK_RULE_MAX)),'0') AS MX2H,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEH',STACK_RULE_MAX)),'0') AS MXEH,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1J',STACK_RULE_MAX)),'0') AS MX1J,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2J',STACK_RULE_MAX)),'0') AS MX2J,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEJ',STACK_RULE_MAX)),'0') AS MXEJ,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1K',STACK_RULE_MAX)),'0') AS MX1K,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2K',STACK_RULE_MAX)),'0') AS MX2K,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEK',STACK_RULE_MAX)),'0') AS MXEK,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1L',STACK_RULE_MAX)),'0') AS MX1L,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2L',STACK_RULE_MAX)),'0') AS MX2L,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEL',STACK_RULE_MAX)),'0') AS MXEL,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1M',STACK_RULE_MAX)),'0') AS MX1M,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2M',STACK_RULE_MAX)),'0') AS MX2M,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEM',STACK_RULE_MAX)),'0') AS MXEM,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1Y',STACK_RULE_MAX)),'0') AS MX1Y,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2Y',STACK_RULE_MAX)),'0') AS MX2Y,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEY',STACK_RULE_MAX)),'0') AS MXEY,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1Z',STACK_RULE_MAX)),'0') AS MX1Z,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2Z',STACK_RULE_MAX)),'0') AS MX2Z,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEZ',STACK_RULE_MAX)),'0') AS MXEZ
		FROM  TB_YM_STACKRULE
		WHERE YD_GP  			= :YD_GP
		  AND BAY_GP 			= :BAY_GP
		  AND STACK_COL_USAGE_CD= :STACK_COL_USAGE_CD
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackRuleInfo_003";	
		Object[] params = {"1","A","-"};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * YJK
     * 적치기준적용 기준정보를 가져온다.
     *
     * @return JDTORecord 적치기준적용 기준정보
     * @throws DAOException
     */
	public JDTORecord getStackRuleInfo_004() throws DAOException
	{	
		/*
		Ver1.0--
		SELECT 
	       NVL(MAX(decode(STACK_RULE_CD,'UPCD',STACK_RULE_MAX)),'0') AS UCARD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'UPDT',STACK_RULE_MAX)),'0') AS UINPUT_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'UPML',STACK_RULE_MAX)),'0') AS UMILL_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'UPNL',STACK_RULE_MAX)),'0') AS UNEXT_PROC,
	       NVL(MAX(decode(STACK_RULE_CD,'UPNO',STACK_RULE_MAX)),'0') AS UORD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'UPOR',STACK_RULE_MAX)),'0') AS UORD_DTL,
	       NVL(MAX(decode(STACK_RULE_CD,'UPPC',STACK_RULE_MAX)),'0') AS UPROG_CD,
	       NVL(MAX(decode(STACK_RULE_CD,'UPST',STACK_RULE_MAX)),'0') AS USPEC,
	       NVL(MAX(decode(STACK_RULE_CD,'DLCD',STACK_RULE_MAX)),'0') AS LCARD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'DLDT',STACK_RULE_MAX)),'0') AS LINPUT_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'DLML',STACK_RULE_MAX)),'0') AS LMILL_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'DLNL',STACK_RULE_MAX)),'0') AS LNEXT_PROC,
	       NVL(MAX(decode(STACK_RULE_CD,'DLNO',STACK_RULE_MAX)),'0') AS LORD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'DLOR',STACK_RULE_MAX)),'0') AS LORD_DTL,
	       NVL(MAX(decode(STACK_RULE_CD,'DLPC',STACK_RULE_MAX)),'0') AS LPROG_CD,
	       NVL(MAX(decode(STACK_RULE_CD,'DLST',STACK_RULE_MAX)),'0') AS LSPEC,
	       NVL(MAX(decode(STACK_RULE_CD,'DLLS',STACK_RULE_MAX)),'0') AS LSTAT,
	       NVL(MAX(decode(STACK_RULE_CD,'DRCD',STACK_RULE_MAX)),'0') AS RCARD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'DRDT',STACK_RULE_MAX)),'0') AS RINPUT_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'DRML',STACK_RULE_MAX)),'0') AS RMILL_DT,
	       NVL(MAX(decode(STACK_RULE_CD,'DRNL',STACK_RULE_MAX)),'0') AS RNEXT_PROC,
	       NVL(MAX(decode(STACK_RULE_CD,'DRNO',STACK_RULE_MAX)),'0') AS RORD_NO,
	       NVL(MAX(decode(STACK_RULE_CD,'DROR',STACK_RULE_MAX)),'0') AS RORD_DTL,
	       NVL(MAX(decode(STACK_RULE_CD,'DRPC',STACK_RULE_MAX)),'0') AS RPROG_CD,
	       NVL(MAX(decode(STACK_RULE_CD,'DRST',STACK_RULE_MAX)),'0') AS RSPEC,
	       NVL(MAX(decode(STACK_RULE_CD,'DRLS',STACK_RULE_MAX)),'0') AS RSTAT,
	       NVL(MAX(decode(STACK_RULE_CD,'MX11',STACK_RULE_MAX)),'0') AS MX11,
	       NVL(MAX(decode(STACK_RULE_CD,'MX21',STACK_RULE_MAX)),'0') AS MX21,
	       NVL(MAX(decode(STACK_RULE_CD,'MXE1',STACK_RULE_MAX)),'0') AS MXE1,
	       NVL(MAX(decode(STACK_RULE_CD,'MX13',STACK_RULE_MAX)),'0') AS MX13,
	       NVL(MAX(decode(STACK_RULE_CD,'MX23',STACK_RULE_MAX)),'0') AS MX23,
	       NVL(MAX(decode(STACK_RULE_CD,'MXE3',STACK_RULE_MAX)),'0') AS MXE3,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1A',STACK_RULE_MAX)),'0') AS MX1A,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2A',STACK_RULE_MAX)),'0') AS MX2A,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEA',STACK_RULE_MAX)),'0') AS MXEA,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1B',STACK_RULE_MAX)),'0') AS MX1B,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2B',STACK_RULE_MAX)),'0') AS MX2B,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEB',STACK_RULE_MAX)),'0') AS MXEB,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1C',STACK_RULE_MAX)),'0') AS MX1C,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2C',STACK_RULE_MAX)),'0') AS MX2C,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEC',STACK_RULE_MAX)),'0') AS MXEC,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1D',STACK_RULE_MAX)),'0') AS MX1D,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2D',STACK_RULE_MAX)),'0') AS MX2D,
	       NVL(MAX(decode(STACK_RULE_CD,'MXED',STACK_RULE_MAX)),'0') AS MXED,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1E',STACK_RULE_MAX)),'0') AS MX1E,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2E',STACK_RULE_MAX)),'0') AS MX2E,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEE',STACK_RULE_MAX)),'0') AS MXEE,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1F',STACK_RULE_MAX)),'0') AS MX1F,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2F',STACK_RULE_MAX)),'0') AS MX2F,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEF',STACK_RULE_MAX)),'0') AS MXEF,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1G',STACK_RULE_MAX)),'0') AS MX1G,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2G',STACK_RULE_MAX)),'0') AS MX2G,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEG',STACK_RULE_MAX)),'0') AS MXEG,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1H',STACK_RULE_MAX)),'0') AS MX1H,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2H',STACK_RULE_MAX)),'0') AS MX2H,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEH',STACK_RULE_MAX)),'0') AS MXEH,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1J',STACK_RULE_MAX)),'0') AS MX1J,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2J',STACK_RULE_MAX)),'0') AS MX2J,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEJ',STACK_RULE_MAX)),'0') AS MXEJ,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1K',STACK_RULE_MAX)),'0') AS MX1K,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2K',STACK_RULE_MAX)),'0') AS MX2K,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEK',STACK_RULE_MAX)),'0') AS MXEK,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1L',STACK_RULE_MAX)),'0') AS MX1L,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2L',STACK_RULE_MAX)),'0') AS MX2L,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEL',STACK_RULE_MAX)),'0') AS MXEL,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1M',STACK_RULE_MAX)),'0') AS MX1M,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2M',STACK_RULE_MAX)),'0') AS MX2M,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEM',STACK_RULE_MAX)),'0') AS MXEM,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1Y',STACK_RULE_MAX)),'0') AS MX1Y,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2Y',STACK_RULE_MAX)),'0') AS MX2Y,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEY',STACK_RULE_MAX)),'0') AS MXEY,
	       NVL(MAX(decode(STACK_RULE_CD,'MX1Z',STACK_RULE_MAX)),'0') AS MX1Z,
	       NVL(MAX(decode(STACK_RULE_CD,'MX2Z',STACK_RULE_MAX)),'0') AS MX2Z,
	       NVL(MAX(decode(STACK_RULE_CD,'MXEZ',STACK_RULE_MAX)),'0') AS MXEZ,
           NVL(MAX(decode(STACK_RULE_CD,'SPEZ',STACK_RULE_MAX)),'0') AS SPEZ
		FROM  TB_YM_STACKRULE
		WHERE YD_GP  			= :YD_GP
		  AND BAY_GP 			= :BAY_GP
		  AND STACK_COL_USAGE_CD= :STACK_COL_USAGE_CD
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackRuleInfo_003";	
		Object[] params = {"3","A","-"};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 적치단에 존재하는 저장품정보를 가져온다.
	 *
	 * @param String : 저장품ID
     *
     * @return List	: 적치열
     * @throws DAOException
     */			
	public List getStackLayerInfoWithStockId_03(String sStockId)throws DAOException
	{	
		/*
		SELECT * 
		FROM tb_ym_stacklayer
		WHERE stock_id = :stock_id
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId_03";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
	
	/**
     * 이정훈
     * 분기, 확장 Conv Line Off 시 야드 및 수조탱크에 존재하는 코일 정보 
	 *
	 * @param String : 저장품ID
     *
     * @return List	: 저장품 정보
     * @throws DAOException
     */			
	public List getStackLayerInfoWithStockId_04(String sStockId)throws DAOException
	{	
		/*
		SELECT * 
		FROM TB_YM_STACKLAYER
		WHERE STOCK_ID = :stock_id
		AND STACK_LAYER_STAT = 'L'
		AND (SUBSTR(STACK_COL_GP,3,2) = 'WT' OR SUBSTR(STACK_COL_GP,3,2) BETWEEN '00' AND '99')
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId_04";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
	
	/**
     * 이정훈
     * 분기, 확장 Conv Line Off 시 작업예약 및 스케쥴 존재시 skip
	 *
	 * @param String : 저장품ID
     *
     * @return List	: 저장품 정보
     * @throws DAOException
     */			
	public List getStackLayerInfoWithStockId_05(String sStockId)throws DAOException
	{	
		/*
		SELECT * 
		FROM TB_YM_WBOOK
		WHERE WBOOK_ID = 
		(   SELECT WBOOK_ID 
        	FROM TB_YM_STOCK
        	WHERE STOCK_ID = :sStockId
		)
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId_05";	
		Object[] params = {sStockId};	
		return super.findList(queryCode,params);
	}
	
	/**
     * 
     * 작업예약ID에 해당하는 재료정보조회
	 *
	 * @param String : 작업예약ID
     *
     * @return List	: 저장품 정보
     * @throws DAOException
     */			
	public JDTORecord getListYwbookStlNo(String sWbookId)throws DAOException
	{	
		/*
		SELECT * 
		FROM TB_YM_WBOOK
		WHERE WBOOK_ID = 
		(   SELECT WBOOK_ID 
        	FROM TB_YM_STOCK
        	WHERE STOCK_ID = :sStockId
		)
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getListWbookStockID";	
		Object[] params = {sWbookId};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
     * YJK
     * 적치단에 존재하는 XX용도 정보를 가져온다.
	 *
	 * @param String : 야그구분
     *
     * @return List	: 적치열
     * @throws DAOException
     */			
	public List getXxStackLayerInfoWithYdGp(String sYdGp)throws DAOException
	{	
		/*
		select 
		    stock_id as 저장품,
		    stack_col_gp as 위치,
		    stack_bed_gp as 번지,
		    stack_layer_gp as 단
		from tb_ym_stacklayer
		where stack_col_gp like :yd_gp||'_XX%'
		order by stack_col_gp,
		         stack_bed_gp
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getXxStackLayerInfoWithYdGp";	
		Object[] params = {sYdGp};	
		return super.findList(queryCode,params);
	}
	
	
	/**
     * YJK
     * 대차설비에 존재하는 저장품 정보를 가져온다.
	 *
	 * @param String : 저장품ID
     *
     * @return List	: 적치열
     * @throws DAOException
     */			
	public List getStockIdWithFrtomoveEquipNo(String sTcNo)throws DAOException
	{	
		/*
		SELECT * 
		FROM tb_ym_stock
		WHERE frtomove_equip_gp = :frtomove_equip_gp
		ORDER BY frtomove_equip_bed_gp
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStockIdWithFrtomoveEquipNo";	
		Object[] params = {sTcNo};	
		return super.findList(queryCode,params);
	}
	
	/**
     * YJK
     * 적치기준적용 기준정보를 가져온다.
     *	=> SYSTEM 처리
     *
     * @param String	: 저장품ID
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: 적치열용도코드
     * @param String	: SCH CODE
     * @param String	: 저장품이동조건
     *
     * @return List 적치기준적용 기준정보
     * @throws DAOException
     */
	public List getNewStackRuleInfo_001(String sStockId,						//저장품ID
								 	    String sYdGp,							//야드구분
							 		    String sBayGp,							//동구분
									  	String sStackColUsageCd,				//적치열용도코드
									  	String sSchWorkKind,					//SCH CODE
									  	String sStockMoveTerm,					//저장품이동조건
									  	String sOrderBy,						//정렬순서
									  	String sSchGbn,							//스케쥴구분
									  	String sGroup,							//군관리
									  	String sYdZoneGp) throws DAOException 	// ZONE구분
	{	
		
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 저장품ID: "+sStockId);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 야드구분: "+sYdGp);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 동구분: "+sBayGp);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 적치열용도코드: "+sStackColUsageCd);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 SCH CODE: "+sSchWorkKind);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 저장품이동조건: "+sStockMoveTerm);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 정렬순서: "+sOrderBy);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 스케쥴구분: "+sSchGbn);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 군관리: "+sGroup);
		log.println(LogLevel.DEBUG, this,"적치기준적용 기준정보 ZONE구분: "+sYdZoneGp);
		
		log.println("SQL=====>>"+sStockId);  
		/*
		Ver1.0--
	  	 .........................
	  	 ..............................
          SELECT  
                 DECODE(B.STACK_COL_USAGE_CD, 'CX', :GROUP_BY, 'CW', :GROUP_BY,B.STACK_COL_BED_DIRECTION) AS PRIOR0,
                 C.stock_move_route_prior  AS PRIOR1,  
                 D.stack_col_seq           AS PRIOR2,                        
                 A.STACK_COL_GP         AS COL, 
                 A.STACK_BED_GP         AS BED, 
                 A.STACK_LAYER_GP       AS LAYER, 
                 B.STACK_COL_BED_PITCH  AS PITCH,
                 B.YD_GP                AS YD_GP,
                 B.BAY_GP               AS BAY_GP,
                 B.STACK_COL_USAGE_CD   AS USE_CD,
                 :STOCK_ID              AS COIL_ID, 
                 DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP) - 1, 2, '0'),
                                          '02', A.STACK_BED_GP) AS LEFT_BED, 
                 DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
                                          '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS LEFT_LAYER,
                 DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP) + 1, 2, '0'),
                                          '02', LPAD(TO_NUMBER(A.STACK_BED_GP) + 1, 2, '0')) AS RIGHT_BED,
                 DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
                                          '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS RIGHT_LAYER
            FROM TB_YM_STOCKMOVEROUTE C,
                 TB_YM_LOCSEARCH D,
                 TB_YM_STACKLAYER A, 
                 TB_YM_STACKCOL B
            WHERE  C.YD_GP 			  	    = :YD_GP				-- 야드구분(1)
            AND    C.BAY_GP 		  	    = :BAY_GP				-- 동구분(E)
            AND    C.STACK_COL_USAGE_CD     = :STACK_COL_USAGE_CD	-- 적치열용도코드(CS)   
            AND    C.SCH_WORK_KIND 		    = :SCH_WORK_KIND		-- SCH CODE(CCTO)
            AND    C.STOCK_MOVE_TERM        like :STOCK_MOVE_TERM	
            AND    C.STACK_USAGE_CD_TO      LIKE :YD_ZONE_GP||'%'   --ZONE 구분	
            AND    C.STOCK_MOVE_ROUTE_STAT  = 'A'
            AND    C.STOCK_MOVE_ROUTE_ID    = D.STOCK_MOVE_ROUTE_ID
            AND    D.STACK_COL_GP           = A.STACK_COL_GP
            AND    A.STACK_LAYER_GP         IN('01', '02')
            AND    A.STACK_LAYER_ACTIVE_STAT= 'O'
            AND    A.STACK_LAYER_STAT       = 'E'
            AND    A.STACK_COL_GP           = B.STACK_COL_GP
            AND    B.STACK_COL_BED_DIRECTION >= :GROUP_BY 
            AND    DECODE(B.STACK_COL_USAGE_CD, 'CW', (SELECT COUNT(1) FROM TB_YM_STACKLAYER
                                                      WHERE STACK_COL_GP = A.STACK_COL_GP
                                                      AND   STACK_LAYER_STAT IN ('S','U'))
                                                     , 0 ) = 0   
            ) A, TB_YM_STACKLAYER B, TB_YM_STACKLAYER C
       WHERE A.COL          = B.STACK_COL_GP(+) 
         AND A.LEFT_BED     = B.STACK_BED_GP(+) 
         AND A.LEFT_LAYER   = B.STACK_LAYER_GP(+)
         AND A.COL          = C.STACK_COL_GP(+) 
         AND A.RIGHT_BED    = C.STACK_BED_GP(+) 
         AND A.RIGHT_LAYER  = C.STACK_LAYER_GP(+)
     ..............................
     .........................
		*/
		
		StringBuffer sDsql = new StringBuffer();
		
		if("1".equals(sOrderBy)){
			/*  06.11. 18 동별 대차 상차 지시 순서 추가
			 *  A,B,C 동  3->2->1 번지 
			 *  D 동 1->2->3 번지 
			 */
			if( (YmCommonConst.YD_GP_3.equals(sYdGp)) &&
					(YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sSchWorkKind)||
					 YmCommonConst.NEW_SCH_WORK_KIND_CTML.equals(sSchWorkKind)|| 
				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM2.equals(sSchWorkKind)|| 
					 YmCommonConst.NEW_SCH_WORK_KIND_CTM3.equals(sSchWorkKind)//||
//				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM5.equals(sSchWorkKind)|| 
//					 YmCommonConst.NEW_SCH_WORK_KIND_CTM6.equals(sSchWorkKind)|| 
//				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM7.equals(sSchWorkKind)
					) && 
					 	(YmCommonConst.BAY_GP_A.equals(sBayGp)||
					     YmCommonConst.BAY_GP_B.equals(sBayGp)||
					     YmCommonConst.BAY_GP_C.equals(sBayGp)) ){	
				
				log.println("대차 순위 확인 1=====");  
				
				sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
				sDsql.append("\n 		  A.LAYER ,		-- 적치단");		
				sDsql.append("\n          A.PRIOR1,  	-- 이동경로우선순위");			
				sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
				sDsql.append("\n 		  A.COL,		-- 적치열 ");
				sDsql.append("\n 		  A.BED DESC 		-- 적치대 ");
			}else
			{
				 /*
				    * 19.11.15 구용모 주임 
				    * D동 소재이송 하차 인 경우 뒤에서부터 적치 되도록 개선 (냉각팬 가까운쪽)
				    * */
				if( YmCommonConst.YD_GP_1.equals(sYdGp) && 
					YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchWorkKind) && 
					YmCommonConst.BAY_GP_D.equals(sBayGp)){
	 					
					log.println("D동 이송하차 순위 확인 0=====>"+sOrderBy);  
					
					sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");	
					sDsql.append("\n 		  A.LAYER,		-- 적치단 ");	
					sDsql.append("\n 		  A.BED DESC,   -- 적치대 ");				
					sDsql.append("\n 		  A.PRIOR1,  	-- 이동경로우선순위");			
					sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
					sDsql.append("\n 		  A.COL		-- 적치열 ");				
					
				} else{
					sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
					sDsql.append("\n 		  A.LAYER ,		-- 적치단");		
					sDsql.append("\n          A.PRIOR1,  	-- 이동경로우선순위");			
					sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
					sDsql.append("\n 		  A.COL,		-- 적치열 ");
					sDsql.append("\n 		  A.BED  		-- 적치대 ");
				}
			}
			
		}else if("2".equals(sOrderBy)){
			/*  06.11. 18 동별 대차 상차 지시 순서 추가
			 *  A,B,C 동  3->2->1 번지 
			 *  D 동 1->2->3 번지 
			 */
			if( (YmCommonConst.YD_GP_3.equals(sYdGp)) &&
					(YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sSchWorkKind)||
					 YmCommonConst.NEW_SCH_WORK_KIND_CTML.equals(sSchWorkKind)|| 
				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM2.equals(sSchWorkKind)|| 
					 YmCommonConst.NEW_SCH_WORK_KIND_CTM3.equals(sSchWorkKind)/*||
				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM5.equals(sSchWorkKind)|| 
					 YmCommonConst.NEW_SCH_WORK_KIND_CTM6.equals(sSchWorkKind)|| 
				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM7.equals(sSchWorkKind)*/) && 
					    (YmCommonConst.BAY_GP_A.equals(sBayGp)||
					     YmCommonConst.BAY_GP_B.equals(sBayGp)||
					     YmCommonConst.BAY_GP_C.equals(sBayGp)) ){	
				
				log.println("대차 순위 확인 2=====");  
				
				sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
				sDsql.append("\n 		  A.LAYER DESC,	-- 적치단");		
				sDsql.append("\n          A.PRIOR1,  	-- 이동경로우선순위");			
				sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
				sDsql.append("\n 		  A.COL,		-- 적치열 ");
				sDsql.append("\n 		  A.BED DESC    -- 적치대 ");
			}
			else
			{
				 /*
				    * 19.11.15 구용모 주임 
				    * D동 소재이송 하차 인 경우 뒤에서부터 적치 되도록 개선 (냉각팬 가까운쪽)
				    * */
				if( YmCommonConst.YD_GP_1.equals(sYdGp) && 
					YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchWorkKind) && 
					YmCommonConst.BAY_GP_D.equals(sBayGp)){
	 					
					log.println("D동 이송하차 순위 확인 0=====>"+sOrderBy);  
					
					sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");	
					sDsql.append("\n 		  A.LAYER	,	-- 적치단 ");	
					sDsql.append("\n 		  A.BED DESC,   -- 적치대 ");				
					sDsql.append("\n 		  A.PRIOR1,  	-- 이동경로우선순위");			
					sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
					sDsql.append("\n 		  A.COL		-- 적치열 ");				
					
				} else{
					sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
					sDsql.append("\n 		  A.LAYER DESC,	-- 적치단");		
					sDsql.append("\n          A.PRIOR1,  	-- 이동경로우선순위");			
					sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
					sDsql.append("\n 		  A.COL,		-- 적치열 ");
					sDsql.append("\n 		  A.BED         -- 적치대 ");
				}
			}
		}else if("0".equals(sOrderBy)){
			/*  06.11. 18 동별 대차 상차 지시 순서 추가
			 *  A,B,C 동  3->2->1 번지 
			 *  D 동 1->2->3 번지 
			 */
//			if( (YmCommonConst.YD_GP_3.equals(sYdGp)) &&
//					(YmCommonConst.NEW_SCH_WORK_KIND_CTFL.equals(sSchWorkKind)||
//					 YmCommonConst.NEW_SCH_WORK_KIND_CTML.equals(sSchWorkKind)|| 
//				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM2.equals(sSchWorkKind)|| 
//					 YmCommonConst.NEW_SCH_WORK_KIND_CTM3.equals(sSchWorkKind)/*||
//				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM5.equals(sSchWorkKind)|| 
//					 YmCommonConst.NEW_SCH_WORK_KIND_CTM6.equals(sSchWorkKind)|| 
//				 	 YmCommonConst.NEW_SCH_WORK_KIND_CTM7.equals(sSchWorkKind) */) && 
//					    (YmCommonConst.BAY_GP_A.equals(sBayGp)||
//					     YmCommonConst.BAY_GP_B.equals(sBayGp)||
//					     YmCommonConst.BAY_GP_C.equals(sBayGp)) ){	
//				
//				log.println("대차 순위 확인 0=====");  
				
			   /*
			    * 19.11.15 구용모 주임 
			    * D동 소재이송 하차 인 경우 뒤에서부터 적치 되도록 개선 (냉각팬 가까운쪽)
			    * */
			if( YmCommonConst.YD_GP_1.equals(sYdGp) && 
				YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchWorkKind) && 
				YmCommonConst.BAY_GP_D.equals(sBayGp)){
 					
				log.println("D동 이송하차 순위 확인 0=====>"+sOrderBy);  
				
				sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");	
				sDsql.append("\n 		  A.LAYER,		-- 적치단 ");	
				sDsql.append("\n 		  A.BED DESC,   -- 적치대 ");				
				sDsql.append("\n 		  A.PRIOR1,  	-- 이동경로우선순위");			
				sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
				sDsql.append("\n 		  A.COL		-- 적치열 ");				
				
			} else
			{
				sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
				sDsql.append("\n 		  A.PRIOR1,  	-- 이동경로우선순위");			
				sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
				sDsql.append("\n 		  A.COL,		-- 적치열 ");
				sDsql.append("\n 		  A.BED,		-- 적치대 ");
				sDsql.append("\n 		  A.LAYER		-- 적치단 ");
			}
			
		}else if("9".equals(sOrderBy)){
			/*  2015.09.17  짱구코일 2단적치 우선 추가		 
			 */
			
			 /*
			    * 19.11.15 구용모 주임 
			    * D동 소재이송 하차 인 경우 뒤에서부터 적치 되도록 개선 (냉각팬 가까운쪽)
			    * */
			if( YmCommonConst.YD_GP_1.equals(sYdGp) && 
				YmCommonConst.NEW_SCH_WORK_KIND_CVMU.equals(sSchWorkKind) && 
				YmCommonConst.BAY_GP_D.equals(sBayGp)){
					
				log.println("D동 이송하차 순위 확인 0=====>"+sOrderBy);  
				
				sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");	
				sDsql.append("\n 		  A.LAYER	,	-- 적치단 ");	
				sDsql.append("\n 		  A.BED DESC,   -- 적치대 ");				
				sDsql.append("\n 		  A.PRIOR1,  	-- 이동경로우선순위");			
				sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
				sDsql.append("\n 		  A.COL		-- 적치열 ");				
				
			} else{
				sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
				sDsql.append("\n 		  A.PRIOR1,  	-- 이동경로우선순위");
				sDsql.append("\n 		  A.LAYER DESC,	-- 적치단 ");
				sDsql.append("\n 		  A.PRIOR2,		-- 적치열우선순위");
				sDsql.append("\n 		  A.COL,		-- 적치열 ");
				sDsql.append("\n 		  A.BED  		-- 적치대 ");
			}
			
		}
	
		String  queryCode =""; 
		if("".equals(sYdZoneGp)){
			queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getNewStackRuleInfo_001_R";				
		}else{
			queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getNewStackRuleInfo_001_Zone";
		}
		
		Object[] params = {sGroup,sGroup,sStockId,sYdGp,sBayGp,sStackColUsageCd,sSchWorkKind,sStockMoveTerm,sYdZoneGp, sGroup,sSchGbn,sSchGbn};
		return super.findList(queryCode,sDsql.toString(),params);		
		
	}
	
	/**
     * YJK
     * 적치기준적용 기준정보를 가져온다.
     *	=> 동내이적처리
     *
     * @param String	: 저장품ID
     * @param String	: 적치열
     *
     * @return List 적치기준적용 기준정보
     * @throws DAOException
     */
	public List getNewStackRuleInfo_002(String sStockId,
								 	 	String sStackColGp,
								 	 	String sOrderBy,						//정렬순서
									  	String sSchGbn,							//스케쥴구분
									  	String sGroup) throws DAOException 		//군관리
	{	
		/*
		Ver1.0--
	  	SELECT  A.PRIOR0,
	  			A.LAYER              AS TO_STACK_LAYER_GP,
                A.COL                AS TO_STACK_COL_GP, 
		        A.BED                AS TO_STACK_BED_GP, 
		        A.LEFT_BED           AS LEFT_STACK_BED_GP,
		        A.LEFT_LAYER         AS LEFT_STACK_LAYER_GP,
		        A.LEFT_STATE         AS LEFT_STACK_LAYER_STAT,
		        A.LCOIL_SM			 AS LEFT_STOCK_MOVE_TERM,
		        A.RIGHT_BED          AS RIGHT_STACK_BED_GP,
		        A.RIGHT_LAYER        AS RIGHT_STACK_LAYER_GP,
		        A.RIGHT_STATE        AS RIGHT_STACK_LAYER_STAT,
		        A.RCOIL_SM			 AS RIGHT_STOCK_MOVE_TERM,
		        A.LCOIL_ID			 AS LEFT_STOCK_ID, 
		        A.RCOIL_ID			 AS RIGHT_STOCK_ID, 
		        
		        A.COIL_CARD_NO,    -- 카드NO
                A.COIL_PROG_CD,    -- 진도코드
                A.COIL_NEXT_PROC,  -- 차공정
                A.COIL_SPEC_ABBSYM,-- 규격약호
                A.COIL_ORD_NO,     -- 주문번호
                A.COIL_ORD_DTL,    -- 주문행번
                A.COIL_RECEIPT_DT, -- 입고일자
                A.COIL_MILL_DT,    -- 압연일시
                
                A.LCOIL_CARD_NO,    -- 카드NO
                A.LCOIL_PROG_CD,    -- 진도코드
                A.LCOIL_NEXT_PROC,  -- 차공정
                A.LCOIL_SPEC_ABBSYM,-- 규격약호
                A.LCOIL_ORD_NO,     -- 주문번호
                A.LCOIL_ORD_DTL,    -- 주문행번
                A.LCOIL_RECEIPT_DT, -- 입고일자
                A.LCOIL_MILL_DT,    -- 압연일시
                
                A.RCOIL_CARD_NO,    -- 카드NO
                A.RCOIL_PROG_CD,    -- 진도코드
                A.RCOIL_NEXT_PROC,  -- 차공정
                A.RCOIL_SPEC_ABBSYM,-- 규격약호
                A.RCOIL_ORD_NO,     -- 주문번호
                A.RCOIL_ORD_DTL,    -- 주문행번
                A.RCOIL_RECEIPT_DT, -- 입고일자
                A.RCOIL_MILL_DT     -- 압연일시
		FROM (
		      SELECT A.PRIOR0,
		      		 A.COL, 
		             A.BED, 
		             A.LAYER, 
		             A.PITCH,
		             A.COIL_ID, 
		             A.COIL_STATE, 
		             A.COIL_COOL_STATE, 
		             A.COIL_TM, 
		             A.COIL_OD, 
		             A.COIL_WT, 
		             A.COIL_WD,
		             S1.STACK_RULE_CD AS UPWT, S1.STACK_RULE_USE_YN AS UPWT_YN, S1.STACK_RULE_MIN AS UPWT_MIN, S1.STACK_RULE_MAX AS UPWT_MAX, 
		             S2.STACK_RULE_CD AS UPOD, S2.STACK_RULE_USE_YN AS UPOD_YN, S2.STACK_RULE_MIN AS UPOD_MIN, S2.STACK_RULE_MAX AS UPOD_MAX,
		             S3.STACK_RULE_CD AS DNOD, S3.STACK_RULE_USE_YN AS DNOD_YN, S3.STACK_RULE_MIN AS DNOD_MIN, S3.STACK_RULE_MAX AS DNOD_MAX,
		             S4.STACK_RULE_CD AS UPWD, S4.STACK_RULE_USE_YN AS UPWD_YN, S4.STACK_RULE_MIN AS UPWD_MIN, S4.STACK_RULE_MAX AS UPWD_MAX,
		             -- 2단 : 중량기준 상/하한값 비교
		             DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S1.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( GREATEST(GREATEST(A.LCOIL_WT, A.RCOIL_WT), A.COIL_WT), A.COIL_WT,'P',
                    		                      DECODE( GREATEST(LEAST(A.LCOIL_WT, A.RCOIL_WT) - S1.STACK_RULE_MIN, A.COIL_WT),A.COIL_WT,'P','B')),'P')
		                   ) AS UPWT_MIN_CP,

                     DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S1.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( LEAST(LEAST(A.LCOIL_WT, A.RCOIL_WT), A.COIL_WT), A.COIL_WT,'P',
                    		                      DECODE( LEAST(LEAST(A.LCOIL_WT, A.RCOIL_WT) + S1.STACK_RULE_MAX, A.COIL_WT),A.COIL_WT,'P','B')),'P')
		                   ) AS UPWT_MAX_CP,

		             -- 2단 : 상단외경기준 상/하한값 비교
		             
		             DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S2.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( GREATEST(GREATEST(A.LCOIL_OD, A.RCOIL_OD), A.COIL_OD), A.COIL_OD,'P',
                    		                      DECODE( GREATEST(LEAST(A.LCOIL_OD, A.RCOIL_OD) - S2.STACK_RULE_MIN, A.COIL_OD),A.COIL_OD,'P','B')),'P')
		                   ) AS UPOD_MIN_CP,

                     DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S2.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( LEAST(LEAST(A.LCOIL_OD, A.RCOIL_OD), A.COIL_OD), A.COIL_OD,'P',
                    		                      DECODE( LEAST(LEAST(A.LCOIL_OD, A.RCOIL_OD) + S2.STACK_RULE_MAX, A.COIL_OD),A.COIL_OD,'P','B')),'P')
		                   ) AS UPOD_MAX_CP,

		             -- 2단 : 폭기준 상/하한값 비교
		             
		             DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S4.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( GREATEST(GREATEST(A.LCOIL_WD, A.RCOIL_WD), A.COIL_WD), A.COIL_WD,'P',
                    		                      DECODE( GREATEST(LEAST(A.LCOIL_WD, A.RCOIL_WD) - S4.STACK_RULE_MIN, A.COIL_WD),A.COIL_WD,'P','B')),'P')
		                   ) AS UPWD_MIN_CP,

                     DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S4.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( LEAST(LEAST(A.LCOIL_WD, A.RCOIL_WD), A.COIL_WD), A.COIL_WD, 'P',
                    		                      DECODE( LEAST(LEAST(A.LCOIL_WD, A.RCOIL_WD) + S4.STACK_RULE_MAX, A.COIL_WD),A.COIL_WD,'P','B')),'P')
		                   ) AS UPWD_MAX_CP,

		             -- 2단 : 하단외경차 비교
		             DECODE(A.LAYER, '01', 'P', -- PASS
		                             '02', DECODE(S3.STACK_RULE_USE_YN, 'Y',
            		                          DECODE(GREATEST(ABS(A.LCOIL_OD - A.RCOIL_OD), S3.STACK_RULE_MAX), S3.STACK_RULE_MAX, 'P','B'),'P')
            		       ) AS DNOD_DIF_CP,
		             
		             -- 진도기준 점수환산
		             '미적용' AS 적치점수,
		      
		             A.LEFT_BED, 
		             A.LEFT_LAYER, 
		             A.LEFT_STATE,
		             A.LCOIL_ID, 
		             A.LCOIL_STATE, 
		             A.LCOIL_COOL_STATE, 
		             A.LCOIL_TM, 
		             A.LCOIL_OD, 
		             A.LCOIL_WT, 
		             A.LCOIL_WD,
		             A.LCOIL_SM,
		             
		             A.RIGHT_BED, 
		             A.RIGHT_LAYER, 
		             A.RIGHT_STATE, 
		             A.RCOIL_ID, 
		             A.RCOIL_STATE, 
		             A.RCOIL_COOL_STATE, 
		             A.RCOIL_TM, 
		             A.RCOIL_OD, 
		             A.RCOIL_WT, 
		             A.RCOIL_WD,
		             A.RCOIL_SM,
		             
		             A.COIL_CARD_NO,    -- 카드NO
                     A.COIL_PROG_CD,    -- 진도코드
                     A.COIL_NEXT_PROC,  -- 차공정
                     A.COIL_SPEC_ABBSYM,-- 규격약호
                     A.COIL_ORD_NO,     -- 주문번호
                     A.COIL_ORD_DTL,    -- 주문행번
                     A.COIL_RECEIPT_DT, -- 입고일자
                     A.COIL_MILL_DT,    -- 압연일시
                     
                     A.LCOIL_CARD_NO,    -- 카드NO
                     A.LCOIL_PROG_CD,    -- 진도코드
                     A.LCOIL_NEXT_PROC,  -- 차공정
                     A.LCOIL_SPEC_ABBSYM,-- 규격약호
                     A.LCOIL_ORD_NO,     -- 주문번호
                     A.LCOIL_ORD_DTL,    -- 주문행번
                     A.LCOIL_RECEIPT_DT, -- 입고일자
                     A.LCOIL_MILL_DT,    -- 압연일시
                     
                     A.RCOIL_CARD_NO,    -- 카드NO
                     A.RCOIL_PROG_CD,    -- 진도코드
                     A.RCOIL_NEXT_PROC,  -- 차공정
                     A.RCOIL_SPEC_ABBSYM,-- 규격약호
                     A.RCOIL_ORD_NO,     -- 주문번호
                     A.RCOIL_ORD_DTL,    -- 주문행번
                     A.RCOIL_RECEIPT_DT, -- 입고일자
                     A.RCOIL_MILL_DT     -- 압연일시
		      FROM (
		            SELECT 
		                   A.YD_GP                 AS YD_GP,
			               A.BAY_GP                AS BAY_GP,
			               A.USE_CD                AS USE_CD,
		                   -- 적치 코일 정보
		                   A.PRIOR0,
		                   A.COL, 
		                   A.BED, 
		                   A.LAYER, 
		                   A.PITCH, 
		                   A.COIL_ID, 
		                   B.STOCK_STAT             AS COIL_STATE, 
		                   B.STOCK_COOL_STAT        AS COIL_COOL_STATE, 
		                   B.STOCK_COOL_START_TEMP  AS COIL_TM,
		                   B.STOCK_MOVE_TERM        AS COIL_SM,
		                   E.COIL_OUTDIA            AS COIL_OD, 
		                   E.COIL_WT           		AS COIL_WT, 
		                   E.COIL_W                 AS COIL_WD,
		                   
		                   B.CAR_CARD_NO      AS COIL_CARD_NO,    -- 카드NO
		                   E.CURR_PROG_CD     AS COIL_PROG_CD,    -- 진도코드
                           E.NEXT_PROC        AS COIL_NEXT_PROC,  -- 차공정
                           E.SPEC_ABBSYM      AS COIL_SPEC_ABBSYM,-- 규격약호
                           E.ORD_NO           AS COIL_ORD_NO,     -- 주문번호
                           E.ORD_DTL          AS COIL_ORD_DTL,    -- 주문행번
                           E.RECEIPT_DATE     AS COIL_RECEIPT_DT, -- 입고일자
                           E.MILL_INI_DATE    AS COIL_MILL_DT,    -- 압연일시
                           
		                   -- 좌하 코일 정보
		                   A.LEFT_BED, 
		                   A.LEFT_LAYER, 
		                   A.LEFT_ACTIVE, 
		                   A.LEFT_STATE, 
		                   A.LCOIL_ID               AS LCOIL_ID, 
		                   C.STOCK_STAT             AS LCOIL_STATE, 
		                   C.STOCK_COOL_STAT        AS LCOIL_COOL_STATE, 
		                   C.STOCK_COOL_START_TEMP  AS LCOIL_TM,
		                   C.STOCK_MOVE_TERM        AS LCOIL_SM,
		                   F.COIL_OUTDIA            AS LCOIL_OD, 
		                   F.COIL_WT           		AS LCOIL_WT, 
		                   F.COIL_W                 AS LCOIL_WD,
		                   
		                   C.CAR_CARD_NO      AS LCOIL_CARD_NO,    -- 카드NO
		                   F.CURR_PROG_CD     AS LCOIL_PROG_CD,    -- 진도코드
                           F.NEXT_PROC        AS LCOIL_NEXT_PROC,  -- 차공정
                           F.SPEC_ABBSYM      AS LCOIL_SPEC_ABBSYM,-- 규격약호
                           F.ORD_NO           AS LCOIL_ORD_NO,     -- 주문번호
                           F.ORD_DTL          AS LCOIL_ORD_DTL,    -- 주문행번
                           F.RECEIPT_DATE     AS LCOIL_RECEIPT_DT, -- 입고일자
                           F.MILL_INI_DATE    AS LCOIL_MILL_DT,    -- 압연일시
                           
		                   -- 우하 코일 정보
		                   A.RIGHT_BED, 
		                   A.RIGHT_LAYER, 
		                   A.RIGHT_ACTIVE, 
		                   A.RIGHT_STATE, 
		                   A.RCOIL_ID               AS RCOIL_ID,
		                   D.STOCK_STAT             AS RCOIL_STATE, 
		                   D.STOCK_COOL_STAT        AS RCOIL_COOL_STATE, 
		                   D.STOCK_COOL_START_TEMP  AS RCOIL_TM,
		                   D.STOCK_MOVE_TERM        AS RCOIL_SM,
		                   G.COIL_OUTDIA            AS RCOIL_OD, 
		                   G.COIL_WT           		AS RCOIL_WT, 
		                   G.COIL_W                 AS RCOIL_WD,
		                   
		                   D.CAR_CARD_NO      AS RCOIL_CARD_NO,    -- 카드NO
		                   G.CURR_PROG_CD     AS RCOIL_PROG_CD,    -- 진도코드
                           G.NEXT_PROC        AS RCOIL_NEXT_PROC,  -- 차공정
                           G.SPEC_ABBSYM      AS RCOIL_SPEC_ABBSYM,-- 규격약호
                           G.ORD_NO           AS RCOIL_ORD_NO,     -- 주문번호
                           G.ORD_DTL          AS RCOIL_ORD_DTL,    -- 주문행번
                           G.RECEIPT_DATE     AS RCOIL_RECEIPT_DT, -- 입고일자
                           G.MILL_INI_DATE    AS RCOIL_MILL_DT     -- 압연일시
		            FROM ( 
		                   SELECT -- 좌/우 코일 정보 조회
		                   		  A.PRIOR0,	
		                          A.COL, 
		                          A.BED, 
		                          A.LAYER, 
		                          A.PITCH, 
		                          A.COIL_ID,
		                          A.YD_GP,
		                          A.BAY_GP,
		                          A.USE_CD,
		                          A.LEFT_BED, 
		                          A.LEFT_LAYER,   
		                          B.STACK_LAYER_ACTIVE_STAT AS LEFT_ACTIVE,  
		                          B.STACK_LAYER_STAT        AS LEFT_STATE,  
		                          B.STOCK_ID                AS LCOIL_ID,
		                          A.RIGHT_BED, 
		                          A.RIGHT_LAYER, 
		                          C.STACK_LAYER_ACTIVE_STAT AS RIGHT_ACTIVE, 
		                          C.STACK_LAYER_STAT        AS RIGHT_STATE, 
		                          C.STOCK_ID                AS RCOIL_ID
		                   FROM ( 
		                         SELECT  
		                         		 B.STACK_COL_BED_DIRECTION AS PRIOR0, 
		                                 A.STACK_COL_GP         AS COL, 
		                                 A.STACK_BED_GP         AS BED, 
		                                 A.STACK_LAYER_GP       AS LAYER, 
		                                 B.STACK_COL_BED_PITCH  AS PITCH,
		                                 B.YD_GP                AS YD_GP,
			                             B.BAY_GP               AS BAY_GP,
			                             B.STACK_COL_USAGE_CD   AS USE_CD,
		                                 :STOCK_ID              AS COIL_ID, 
		                                 DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP) - 1, 2, '0'),
		                                                          '02', A.STACK_BED_GP) AS LEFT_BED, 
		                                 DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
		                                                          '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS LEFT_LAYER,
		                                 DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP) + 1, 2, '0'),
		                                                          '02', LPAD(TO_NUMBER(A.STACK_BED_GP) + 1, 2, '0')) AS RIGHT_BED,
		                                 DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
		                                                          '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS RIGHT_LAYER
		                          FROM TB_YM_STACKLAYER A, 
		                               TB_YM_STACKCOL B
		                          WHERE A.STACK_COL_GP              LIKE :STACK_COL_GP||'%' 
		                            AND A.STACK_LAYER_GP            IN('01', '02')
		                            AND A.STACK_LAYER_ACTIVE_STAT   = 'O'
		                            AND A.STACK_LAYER_STAT          = 'E'
		                            AND A.STACK_COL_GP              = B.STACK_COL_GP
		                            AND B.STACK_COL_BED_DIRECTION	>= :GROUP_BY    
		                        ) A, TB_YM_STACKLAYER B, TB_YM_STACKLAYER C
		                   WHERE A.COL          = B.STACK_COL_GP(+) 
		                     AND A.LEFT_BED     = B.STACK_BED_GP(+) 
		                     AND A.LEFT_LAYER   = B.STACK_LAYER_GP(+)
		                     AND A.COL          = C.STACK_COL_GP(+) 
		                     AND A.RIGHT_BED    = C.STACK_BED_GP(+) 
		                     AND A.RIGHT_LAYER  = C.STACK_LAYER_GP(+)
		                 ) A, 
		                 TB_YM_STOCK B, 
		                 TB_YM_STOCK C, 
		                 TB_YM_STOCK D,
		                 USRPMA.TB_PM_COILCOMM E, 
		                 USRPMA.TB_PM_COILCOMM F, 
		                 USRPMA.TB_PM_COILCOMM G
		            WHERE A.COIL_ID  = B.STOCK_ID(+) 
		              AND A.LCOIL_ID = C.STOCK_ID(+) 
		              AND A.RCOIL_ID = D.STOCK_ID(+)
		              AND A.COIL_ID  = E.COIL_NO(+)  
		              AND A.LCOIL_ID = F.COIL_NO(+)  
		              AND A.RCOIL_ID = G.COIL_NO(+)
		              AND DECODE(A.LAYER, '01', '-', '02', A.LEFT_ACTIVE)  = DECODE(A.LAYER, '01', '-', '02', 'O') 
		              AND DECODE(A.LAYER, '01', '-', '02', A.RIGHT_ACTIVE) = DECODE(A.LAYER, '01', '-', '02', 'O') 
		              AND
		                  ( 
		                  DECODE(A.LAYER, '01', '-', '02', A.LEFT_STATE)   = DECODE(A.LAYER, '01', '-', '02', 'L') 
		                  OR
		                  DECODE(A.LAYER, '01', '-', '02', A.LEFT_STATE)   = DECODE(A.LAYER, '01', '-', '02', DECODE(:GBN,'F','P','R','L')) 
		                  )
		              AND 
		                  ( 
		                  DECODE(A.LAYER, '01', '-', '02', A.RIGHT_STATE)  = DECODE(A.LAYER, '01', '-', '02', 'L') 
		                  OR
		                  DECODE(A.LAYER, '01', '-', '02', A.RIGHT_STATE)  = DECODE(A.LAYER, '01', '-', '02', DECODE(:GBN,'F','P','R','L')) 
		                  )
		           ) A, 
		           TB_YM_STACKRULE S1, 
		           TB_YM_STACKRULE S2, 
		           TB_YM_STACKRULE S3, 
		           TB_YM_STACKRULE S4
		      WHERE A.YD_GP = S1.YD_GP(+) AND A.BAY_GP = S1.BAY_GP(+) AND A.USE_CD = S1.STACK_COL_USAGE_CD(+) AND S1.STACK_RULE_CD (+)= 'UPWT' -- 상단중량기준
		        AND A.YD_GP = S2.YD_GP(+) AND A.BAY_GP = S2.BAY_GP(+) AND A.USE_CD = S2.STACK_COL_USAGE_CD(+) AND S2.STACK_RULE_CD (+)= 'UPOD' -- 상단외경기준
		        AND A.YD_GP = S3.YD_GP(+) AND A.BAY_GP = S3.BAY_GP(+) AND A.USE_CD = S3.STACK_COL_USAGE_CD(+) AND S3.STACK_RULE_CD (+)= 'DNOD' -- 하단외경기준
		        AND A.YD_GP = S4.YD_GP(+) AND A.BAY_GP = S4.BAY_GP(+) AND A.USE_CD = S4.STACK_COL_USAGE_CD(+) AND S4.STACK_RULE_CD (+)= 'UPWD' -- 상단폭기준
		     ) A
		WHERE A.UPWT_MIN_CP = 'P' AND A.UPWT_MAX_CP = 'P' 
		  AND A.UPOD_MIN_CP = 'P' AND A.UPOD_MAX_CP = 'P'
		  AND A.UPWD_MIN_CP = 'P' AND A.UPWD_MAX_CP = 'P'
		  AND A.DNOD_DIF_CP = 'P'
		*/
		StringBuffer sDsql = new StringBuffer();
		
		if("1".equals(sOrderBy)){
			
			sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
			sDsql.append("\n 		  A.LAYER ,		-- 적치단");		
			sDsql.append("\n          A.COL,		-- 적치열 ");
			sDsql.append("\n 		  A.BED			-- 적치대 ");
			
		}else if("2".equals(sOrderBy)){
			
			sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
			sDsql.append("\n 		  A.LAYER DESC,	-- 적치단");		
			sDsql.append("\n          A.COL,		-- 적치열 ");
			sDsql.append("\n 		  A.BED			-- 적치대 ");
			
		}else if("0".equals(sOrderBy)){
			
			sDsql.append("\n ORDER BY A.PRIOR0,		-- 군관리");		
			sDsql.append("\n 		  A.COL,		-- 적치열 ");
			sDsql.append("\n 		  A.BED,		-- 적치대 ");
			sDsql.append("\n 		  A.LAYER		-- 적치단 ");
		}
	
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getNewStackRuleInfo_002_R";	
		Object[] params = {sStockId,sStackColGp,sGroup,sSchGbn,sSchGbn};			
		return super.findList(queryCode,sDsql.toString(),params);
	}
		
	/**
     * YJK
     * 적치기준적용 기준정보를 가져온다.
     *	=> 보조작업처리
     *
     * @param String	: 저장품ID
     * @param String	: 야드구분
     * @param String	: 동구분
     * @param String	: SPAN구분
     * @param String	: 열구분
     *
     * @return List 적치기준적용 기준정보
     * @throws DAOException
     */
	public List getNewStackRuleInfo_003(String sStockId,
								 	 	String sYdGp, 						//야드구분
								 	 	String sBayGp,						//동구분
								 	 	String sSectGp,						//SPAN구분
								 	 	String sColGp,						//열구분
									  	String sSchGbn,						//스케쥴구분
									  	String sGroup,						//군관리
									  	String sOrderBy) throws DAOException // 정렬순서	
	{	
		/*
		Ver1.0--
	  	SELECT  A.PRIOR3,
	  			A.PRIOR0,
	  			A.PRIOR1,
		        A.PRIOR2,
                A.LAYER              AS TO_STACK_LAYER_GP,
                A.COL                AS TO_STACK_COL_GP, 
		        A.BED                AS TO_STACK_BED_GP, 
		        A.LEFT_BED           AS LEFT_STACK_BED_GP,
		        A.LEFT_LAYER         AS LEFT_STACK_LAYER_GP,
		        A.LEFT_STATE         AS LEFT_STACK_LAYER_STAT,
		        A.LCOIL_SM			 AS LEFT_STOCK_MOVE_TERM,
		        A.RIGHT_BED          AS RIGHT_STACK_BED_GP,
		        A.RIGHT_LAYER        AS RIGHT_STACK_LAYER_GP,
		        A.RIGHT_STATE        AS RIGHT_STACK_LAYER_STAT,
		        A.RCOIL_SM			 AS RIGHT_STOCK_MOVE_TERM,
		        A.LCOIL_ID			 AS LEFT_STOCK_ID, 
		        A.RCOIL_ID			 AS RIGHT_STOCK_ID, 
		        
		        A.COIL_CARD_NO,    -- 카드NO
                A.COIL_PROG_CD,    -- 진도코드
                A.COIL_NEXT_PROC,  -- 차공정
                A.COIL_SPEC_ABBSYM,-- 규격약호
                A.COIL_ORD_NO,     -- 주문번호
                A.COIL_ORD_DTL,    -- 주문행번
                A.COIL_RECEIPT_DT, -- 입고일자
                A.COIL_MILL_DT,    -- 압연일시
                
                A.LCOIL_CARD_NO,    -- 카드NO
                A.LCOIL_PROG_CD,    -- 진도코드
                A.LCOIL_NEXT_PROC,  -- 차공정
                A.LCOIL_SPEC_ABBSYM,-- 규격약호
                A.LCOIL_ORD_NO,     -- 주문번호
                A.LCOIL_ORD_DTL,    -- 주문행번
                A.LCOIL_RECEIPT_DT, -- 입고일자
                A.LCOIL_MILL_DT,    -- 압연일시
                
                A.RCOIL_CARD_NO,    -- 카드NO
                A.RCOIL_PROG_CD,    -- 진도코드
                A.RCOIL_NEXT_PROC,  -- 차공정
                A.RCOIL_SPEC_ABBSYM,-- 규격약호
                A.RCOIL_ORD_NO,     -- 주문번호
                A.RCOIL_ORD_DTL,    -- 주문행번
                A.RCOIL_RECEIPT_DT, -- 입고일자
                A.RCOIL_MILL_DT     -- 압연일시
		FROM (
		      SELECT A.PRIOR3,
		      		 A.PRIOR0,
		      		 A.PRIOR1,
		             A.PRIOR2,
		             A.COL, 
		             A.BED, 
		             A.LAYER, 
		             A.PITCH,
		             A.COIL_ID, 
		             A.COIL_STATE, 
		             A.COIL_COOL_STATE, 
		             A.COIL_TM, 
		             A.COIL_OD, 
		             A.COIL_WT, 
		             A.COIL_WD,
		             S1.STACK_RULE_CD AS UPWT, S1.STACK_RULE_USE_YN AS UPWT_YN, S1.STACK_RULE_MIN AS UPWT_MIN, S1.STACK_RULE_MAX AS UPWT_MAX, 
		             S2.STACK_RULE_CD AS UPOD, S2.STACK_RULE_USE_YN AS UPOD_YN, S2.STACK_RULE_MIN AS UPOD_MIN, S2.STACK_RULE_MAX AS UPOD_MAX,
		             S3.STACK_RULE_CD AS DNOD, S3.STACK_RULE_USE_YN AS DNOD_YN, S3.STACK_RULE_MIN AS DNOD_MIN, S3.STACK_RULE_MAX AS DNOD_MAX,
		             S4.STACK_RULE_CD AS UPWD, S4.STACK_RULE_USE_YN AS UPWD_YN, S4.STACK_RULE_MIN AS UPWD_MIN, S4.STACK_RULE_MAX AS UPWD_MAX,
		             -- 2단 : 중량기준 상/하한값 비교
		             DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S1.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( GREATEST(GREATEST(A.LCOIL_WT, A.RCOIL_WT), A.COIL_WT), A.COIL_WT,'P',
                    		                      DECODE( GREATEST(LEAST(A.LCOIL_WT, A.RCOIL_WT) - S1.STACK_RULE_MIN, A.COIL_WT),A.COIL_WT,'P','B')),'P')
		                   ) AS UPWT_MIN_CP,

                     DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S1.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( LEAST(LEAST(A.LCOIL_WT, A.RCOIL_WT), A.COIL_WT), A.COIL_WT,'P',
                    		                      DECODE( LEAST(LEAST(A.LCOIL_WT, A.RCOIL_WT) + S1.STACK_RULE_MAX, A.COIL_WT),A.COIL_WT,'P','B')),'P')
		                   ) AS UPWT_MAX_CP,

		             -- 2단 : 상단외경기준 상/하한값 비교
		             
		             DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S2.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( GREATEST(GREATEST(A.LCOIL_OD, A.RCOIL_OD), A.COIL_OD), A.COIL_OD,'P',
                    		                      DECODE( GREATEST(LEAST(A.LCOIL_OD, A.RCOIL_OD) - S2.STACK_RULE_MIN, A.COIL_OD),A.COIL_OD,'P','B')),'P')
		                   ) AS UPOD_MIN_CP,

                     DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S2.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( LEAST(LEAST(A.LCOIL_OD, A.RCOIL_OD), A.COIL_OD), A.COIL_OD,'P',
                    		                      DECODE( LEAST(LEAST(A.LCOIL_OD, A.RCOIL_OD) + S2.STACK_RULE_MAX, A.COIL_OD),A.COIL_OD,'P','B')),'P')
		                   ) AS UPOD_MAX_CP,

		             -- 2단 : 폭기준 상/하한값 비교
		             
		             DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S4.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( GREATEST(GREATEST(A.LCOIL_WD, A.RCOIL_WD), A.COIL_WD), A.COIL_WD,'P',
                    		                      DECODE( GREATEST(LEAST(A.LCOIL_WD, A.RCOIL_WD) - S4.STACK_RULE_MIN, A.COIL_WD),A.COIL_WD,'P','B')),'P')
		                   ) AS UPWD_MIN_CP,

                     DECODE(A.LAYER, '01', 'P', 
		                             '02', DECODE(S4.STACK_RULE_USE_YN, 'Y',
		                                      DECODE( LEAST(LEAST(A.LCOIL_WD, A.RCOIL_WD), A.COIL_WD), A.COIL_WD, 'P',
                    		                      DECODE( LEAST(LEAST(A.LCOIL_WD, A.RCOIL_WD) + S4.STACK_RULE_MAX, A.COIL_WD),A.COIL_WD,'P','B')),'P')
		                   ) AS UPWD_MAX_CP,

		             -- 2단 : 하단외경차 비교
		             DECODE(A.LAYER, '01', 'P', -- PASS
		                             '02', DECODE(S3.STACK_RULE_USE_YN, 'Y',
            		                          DECODE(GREATEST(ABS(A.LCOIL_OD - A.RCOIL_OD), S3.STACK_RULE_MAX), S3.STACK_RULE_MAX, 'P','B'),'P')
            		       ) AS DNOD_DIF_CP,
            		       
		             -- 진도기준 점수환산
		             '미적용' AS 적치점수,
		      
		             A.LEFT_BED, 
		             A.LEFT_LAYER, 
		             A.LEFT_STATE,
		             A.LCOIL_ID, 
		             A.LCOIL_STATE, 
		             A.LCOIL_COOL_STATE, 
		             A.LCOIL_TM, 
		             A.LCOIL_OD, 
		             A.LCOIL_WT, 
		             A.LCOIL_WD,
		             A.LCOIL_SM,
		             
		             A.RIGHT_BED, 
		             A.RIGHT_LAYER, 
		             A.RIGHT_STATE, 
		             A.RCOIL_ID, 
		             A.RCOIL_STATE, 
		             A.RCOIL_COOL_STATE, 
		             A.RCOIL_TM, 
		             A.RCOIL_OD, 
		             A.RCOIL_WT, 
		             A.RCOIL_WD,
		             A.RCOIL_SM,
		             
		             A.COIL_CARD_NO,    -- 카드NO
                     A.COIL_PROG_CD,    -- 진도코드
                     A.COIL_NEXT_PROC,  -- 차공정
                     A.COIL_SPEC_ABBSYM,-- 규격약호
                     A.COIL_ORD_NO,     -- 주문번호
                     A.COIL_ORD_DTL,    -- 주문행번
                     A.COIL_RECEIPT_DT, -- 입고일자
                     A.COIL_MILL_DT,    -- 압연일시
                     
                     A.LCOIL_CARD_NO,    -- 카드NO
                     A.LCOIL_PROG_CD,    -- 진도코드
                     A.LCOIL_NEXT_PROC,  -- 차공정
                     A.LCOIL_SPEC_ABBSYM,-- 규격약호
                     A.LCOIL_ORD_NO,     -- 주문번호
                     A.LCOIL_ORD_DTL,    -- 주문행번
                     A.LCOIL_RECEIPT_DT, -- 입고일자
                     A.LCOIL_MILL_DT,    -- 압연일시
                     
                     A.RCOIL_CARD_NO,    -- 카드NO
                     A.RCOIL_PROG_CD,    -- 진도코드
                     A.RCOIL_NEXT_PROC,  -- 차공정
                     A.RCOIL_SPEC_ABBSYM,-- 규격약호
                     A.RCOIL_ORD_NO,     -- 주문번호
                     A.RCOIL_ORD_DTL,    -- 주문행번
                     A.RCOIL_RECEIPT_DT, -- 입고일자
                     A.RCOIL_MILL_DT     -- 압연일시
		      FROM (
		            SELECT 
		                   A.PRIOR3,
		                   A.PRIOR0,
		                   A.PRIOR1,
		                   A.PRIOR2,   
		                   A.YD_GP                 AS YD_GP,
			               A.BAY_GP                AS BAY_GP,
			               A.USE_CD                AS USE_CD,
		                   -- 적치 코일 정보
		                   A.COL, 
		                   A.BED, 
		                   A.LAYER, 
		                   A.PITCH, 
		                   A.COIL_ID, 
		                   B.STOCK_STAT             AS COIL_STATE, 
		                   B.STOCK_COOL_STAT        AS COIL_COOL_STATE, 
		                   B.STOCK_COOL_START_TEMP  AS COIL_TM,
		                   B.STOCK_MOVE_TERM        AS COIL_SM,
		                   E.COIL_OUTDIA            AS COIL_OD, 
		                   E.COIL_WT           		AS COIL_WT, 
		                   E.COIL_W                 AS COIL_WD,
		                   
		                   B.CAR_CARD_NO      AS COIL_CARD_NO,    -- 카드NO
		                   E.CURR_PROG_CD     AS COIL_PROG_CD,    -- 진도코드
                           E.NEXT_PROC        AS COIL_NEXT_PROC,  -- 차공정
                           E.SPEC_ABBSYM      AS COIL_SPEC_ABBSYM,-- 규격약호
                           E.ORD_NO           AS COIL_ORD_NO,     -- 주문번호
                           E.ORD_DTL          AS COIL_ORD_DTL,    -- 주문행번
                           E.RECEIPT_DATE     AS COIL_RECEIPT_DT, -- 입고일자
                           E.MILL_INI_DATE    AS COIL_MILL_DT,    -- 압연일시
                           
		                   -- 좌하 코일 정보
		                   A.LEFT_BED, 
		                   A.LEFT_LAYER, 
		                   A.LEFT_ACTIVE, 
		                   A.LEFT_STATE, 
		                   A.LCOIL_ID               AS LCOIL_ID, 
		                   C.STOCK_STAT             AS LCOIL_STATE, 
		                   C.STOCK_COOL_STAT        AS LCOIL_COOL_STATE, 
		                   C.STOCK_COOL_START_TEMP  AS LCOIL_TM,
		                   C.STOCK_MOVE_TERM        AS LCOIL_SM,
		                   F.COIL_OUTDIA            AS LCOIL_OD, 
		                   F.COIL_WT           		AS LCOIL_WT, 
		                   F.COIL_W                 AS LCOIL_WD,
		                   
		                   C.CAR_CARD_NO      AS LCOIL_CARD_NO,    -- 카드NO
		                   F.CURR_PROG_CD     AS LCOIL_PROG_CD,    -- 진도코드
                           F.NEXT_PROC        AS LCOIL_NEXT_PROC,  -- 차공정
                           F.SPEC_ABBSYM      AS LCOIL_SPEC_ABBSYM,-- 규격약호
                           F.ORD_NO           AS LCOIL_ORD_NO,     -- 주문번호
                           F.ORD_DTL          AS LCOIL_ORD_DTL,    -- 주문행번
                           F.RECEIPT_DATE     AS LCOIL_RECEIPT_DT, -- 입고일자
                           F.MILL_INI_DATE    AS LCOIL_MILL_DT,    -- 압연일시
                           
		                   -- 우하 코일 정보
		                   A.RIGHT_BED, 
		                   A.RIGHT_LAYER, 
		                   A.RIGHT_ACTIVE, 
		                   A.RIGHT_STATE, 
		                   A.RCOIL_ID               AS RCOIL_ID,
		                   D.STOCK_STAT             AS RCOIL_STATE, 
		                   D.STOCK_COOL_STAT        AS RCOIL_COOL_STATE, 
		                   D.STOCK_COOL_START_TEMP  AS RCOIL_TM,
		                   D.STOCK_MOVE_TERM        AS RCOIL_SM,
		                   G.COIL_OUTDIA            AS RCOIL_OD, 
		                   G.COIL_WT           		AS RCOIL_WT, 
		                   G.COIL_W                 AS RCOIL_WD,
		                   
		                   D.CAR_CARD_NO      AS RCOIL_CARD_NO,    -- 카드NO
		                   G.CURR_PROG_CD     AS RCOIL_PROG_CD,    -- 진도코드
                           G.NEXT_PROC        AS RCOIL_NEXT_PROC,  -- 차공정
                           G.SPEC_ABBSYM      AS RCOIL_SPEC_ABBSYM,-- 규격약호
                           G.ORD_NO           AS RCOIL_ORD_NO,     -- 주문번호
                           G.ORD_DTL          AS RCOIL_ORD_DTL,    -- 주문행번
                           G.RECEIPT_DATE     AS RCOIL_RECEIPT_DT, -- 입고일자
                           G.MILL_INI_DATE    AS RCOIL_MILL_DT     -- 압연일시
		            FROM ( 
		                   SELECT -- 좌/우 코일 정보 조회
		                          A.PRIOR3,
		                          A.PRIOR0,
		                          A.PRIOR1,
		                          A.PRIOR2,
		                          A.COL, 
		                          A.BED, 
		                          A.LAYER, 
		                          A.PITCH, 
		                          A.COIL_ID,
		                          A.YD_GP,
		                          A.BAY_GP,
		                          A.USE_CD,
		                          A.LEFT_BED, 
		                          A.LEFT_LAYER,   
		                          B.STACK_LAYER_ACTIVE_STAT AS LEFT_ACTIVE,  
		                          B.STACK_LAYER_STAT        AS LEFT_STATE,  
		                          B.STOCK_ID                AS LCOIL_ID,
		                          A.RIGHT_BED, 
		                          A.RIGHT_LAYER, 
		                          C.STACK_LAYER_ACTIVE_STAT AS RIGHT_ACTIVE, 
		                          C.STACK_LAYER_STAT        AS RIGHT_STATE, 
		                          C.STOCK_ID                AS RCOIL_ID
		                   FROM ( 
		                         SELECT   
								     NVL(abs(to_number(A.STACK_BED_GP)  
									       - to_number(C.STACK_BED_GP)),99)  as PRIOR0,   
									       
								     abs(to_number(B.sect_gp) - to_number(:sect_gp)) as PRIOR1,
									 abs(to_number(B.col_gp)  - to_number(:col_gp))  as PRIOR2,   
									 
									 B.STACK_COL_BED_DIRECTION AS PRIOR3, 
									 A.STACK_COL_GP         AS COL, 
								     A.STACK_BED_GP         AS BED, 
								     A.STACK_LAYER_GP       AS LAYER, 
								     
								     --C.STACK_COL_GP         AS CUR_COL, 
								     --C.STACK_BED_GP         AS CUR_BED, 
								     --C.STACK_LAYER_GP       AS CUR_LAYER, 
								     
								     B.STACK_COL_BED_PITCH  AS PITCH,
								     B.YD_GP                AS YD_GP,
								     B.BAY_GP               AS BAY_GP,
								     B.STACK_COL_USAGE_CD   AS USE_CD,
								     :STOCK_ID              AS COIL_ID, 
								     DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP) - 1, 2, '0'),
								                              '02', A.STACK_BED_GP) AS LEFT_BED, 
								     DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
								                              '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS LEFT_LAYER,
								     DECODE(A.STACK_LAYER_GP, '01', LPAD(TO_NUMBER(A.STACK_BED_GP) + 1, 2, '0'),
								                              '02', LPAD(TO_NUMBER(A.STACK_BED_GP) + 1, 2, '0')) AS RIGHT_BED,
								     DECODE(A.STACK_LAYER_GP, '01', A.STACK_LAYER_GP,
								                              '02', LPAD(TO_NUMBER(A.STACK_LAYER_GP) - 1, 2, '0')) AS RIGHT_LAYER
								FROM TB_YM_STACKLAYER A, 
								     TB_YM_STACKCOL B,
								     (
								         SELECT 
								             STACK_COL_GP, 
								             STACK_BED_GP, 
								             STACK_LAYER_GP
								         FROM TB_YM_STACKLAYER
								         WHERE STOCK_ID = :STOCK_ID 
								     )C
								WHERE  B.YD_GP 			  	    = :YD_GP	-- 야드구분(1)
								AND    B.BAY_GP 		  	    = :BAY_GP	-- 동구분(E)
								AND    B.STACK_COL_USAGE_CD 	IN ('C1','C2','C3','C4','C5','C6','C7','C8','G1','G2','G3','G4','G5')
								AND    A.STACK_LAYER_GP         IN ('01','02')
								AND    A.STACK_LAYER_ACTIVE_STAT= 'O'
								AND    A.STACK_LAYER_STAT       = 'E'
								AND    A.STACK_COL_GP           = B.STACK_COL_GP
								AND    B.STACK_COL_BED_DIRECTION >= :GROUP_BY    
								AND    A.STACK_COL_GP           = C.STACK_COL_GP(+) 
		                        ) A, TB_YM_STACKLAYER B, TB_YM_STACKLAYER C
		                   WHERE A.COL          = B.STACK_COL_GP(+) 
		                     AND A.LEFT_BED     = B.STACK_BED_GP(+) 
		                     AND A.LEFT_LAYER   = B.STACK_LAYER_GP(+)
		                     AND A.COL          = C.STACK_COL_GP(+) 
		                     AND A.RIGHT_BED    = C.STACK_BED_GP(+) 
		                     AND A.RIGHT_LAYER  = C.STACK_LAYER_GP(+)
		                 ) A, 
		                 TB_YM_STOCK B, 
		                 TB_YM_STOCK C, 
		                 TB_YM_STOCK D,
		                 USRPMA.TB_PM_COILCOMM E, 
		                 USRPMA.TB_PM_COILCOMM F, 
		                 USRPMA.TB_PM_COILCOMM G
		            WHERE A.COIL_ID  = B.STOCK_ID(+) 
		              AND A.LCOIL_ID = C.STOCK_ID(+) 
		              AND A.RCOIL_ID = D.STOCK_ID(+)
		              AND A.COIL_ID  = E.COIL_NO(+)  
		              AND A.LCOIL_ID = F.COIL_NO(+)  
		              AND A.RCOIL_ID = G.COIL_NO(+)
		              AND DECODE(A.LAYER, '01', '-', '02', A.LEFT_ACTIVE)  = DECODE(A.LAYER, '01', '-', '02', 'O') 
		              AND DECODE(A.LAYER, '01', '-', '02', A.RIGHT_ACTIVE) = DECODE(A.LAYER, '01', '-', '02', 'O') 
		              AND
		                  ( 
		                  DECODE(A.LAYER, '01', '-', '02', A.LEFT_STATE)   = DECODE(A.LAYER, '01', '-', '02', 'L') 
		                  OR
		                  DECODE(A.LAYER, '01', '-', '02', A.LEFT_STATE)   = DECODE(A.LAYER, '01', '-', '02', DECODE(:GBN,'F','P','R','L')) 
		                  )
		              AND 
		                  ( 
		                  DECODE(A.LAYER, '01', '-', '02', A.RIGHT_STATE)  = DECODE(A.LAYER, '01', '-', '02', 'L') 
		                  OR
		                  DECODE(A.LAYER, '01', '-', '02', A.RIGHT_STATE)  = DECODE(A.LAYER, '01', '-', '02', DECODE(:GBN,'F','P','R','L')) 
		                  )
		           ) A, 
		           TB_YM_STACKRULE S1, 
		           TB_YM_STACKRULE S2, 
		           TB_YM_STACKRULE S3, 
		           TB_YM_STACKRULE S4
		      WHERE A.YD_GP = S1.YD_GP(+) AND A.BAY_GP = S1.BAY_GP(+) AND A.USE_CD = S1.STACK_COL_USAGE_CD(+) AND S1.STACK_RULE_CD (+)= 'UPWT' -- 상단중량기준
		        AND A.YD_GP = S2.YD_GP(+) AND A.BAY_GP = S2.BAY_GP(+) AND A.USE_CD = S2.STACK_COL_USAGE_CD(+) AND S2.STACK_RULE_CD (+)= 'UPOD' -- 상단외경기준
		        AND A.YD_GP = S3.YD_GP(+) AND A.BAY_GP = S3.BAY_GP(+) AND A.USE_CD = S3.STACK_COL_USAGE_CD(+) AND S3.STACK_RULE_CD (+)= 'DNOD' -- 하단외경기준
		        AND A.YD_GP = S4.YD_GP(+) AND A.BAY_GP = S4.BAY_GP(+) AND A.USE_CD = S4.STACK_COL_USAGE_CD(+) AND S4.STACK_RULE_CD (+)= 'UPWD' -- 상단폭기준
		     ) A
		WHERE A.UPWT_MIN_CP = 'P' AND A.UPWT_MAX_CP = 'P' 
		  AND A.UPOD_MIN_CP = 'P' AND A.UPOD_MAX_CP = 'P'
		  AND A.UPWD_MIN_CP = 'P' AND A.UPWD_MAX_CP = 'P'
		  AND A.DNOD_DIF_CP = 'P'
		
		*/
		StringBuffer sDsql = new StringBuffer();
		
		if("1".equals(sOrderBy)){
			
			sDsql.append("\n ORDER BY A.PRIOR3,		-- 군관리");	
			sDsql.append("\n 		  A.PRIOR0 ,	");
			sDsql.append("\n 		  A.PRIOR1 ,		");
			sDsql.append("\n 		  A.PRIOR2 ,		");
			sDsql.append("\n 		  A.LAYER ,		-- 적치단");		
			sDsql.append("\n          A.COL,		-- 적치열 ");
			sDsql.append("\n 		  A.BED			-- 적치대 ");
			
		}else if("2".equals(sOrderBy)){
			
			sDsql.append("\n ORDER BY A.PRIOR3,		-- 군관리");	
			sDsql.append("\n 		  A.PRIOR0 ,	");
			sDsql.append("\n 		  A.PRIOR1 ,		");
			sDsql.append("\n 		  A.PRIOR2 ,		");		
			sDsql.append("\n 		  A.LAYER DESC,	-- 적치단");		
			sDsql.append("\n          A.COL,		-- 적치열 ");
			sDsql.append("\n 		  A.BED			-- 적치대 ");
			
		}else if("0".equals(sOrderBy)){
			
			sDsql.append("\n ORDER BY A.PRIOR3,		-- 군관리");	
			sDsql.append("\n 		  A.PRIOR0 ,	");
			sDsql.append("\n 		  A.PRIOR1 ,		");
			sDsql.append("\n 		  A.PRIOR2 ,		");	
			sDsql.append("\n 		  A.COL,		-- 적치열 ");
			sDsql.append("\n 		  A.BED,		-- 적치대 ");
			sDsql.append("\n 		  A.LAYER		-- 적치단 ");
		}
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getNewStackRuleInfo_003_R";	
		Object[] params = {sSectGp,sColGp,sStockId,sStockId,sYdGp,sBayGp,sGroup,sSchGbn,sSchGbn};			
		return super.findList(queryCode,sDsql.toString(),params);
			
	}	
 	
 	/**
	 * YJK
     * 해당 스케쥴코드의 적치기준정보를 가져온다.
     *	- 0 => 번지순
     *	- 1 => 1단우선
     *	- 2 => 2단 우선n
     *
     * @param String	: 야드구분
     * @param String	: 스케쥴코드
     *
     * @return JDTORecord 적치기준적용 기준정보
     * @throws DAOException
     */
	public JDTORecord getStackRuleInfo_004(String sYdGp,
										   String sBayGp,
										   String sSchCode) throws DAOException
	{	
		/*
		Ver1.0--
		SELECT 
		    *
		FROM USRYMA.TB_YM_STACKPRIORITY
    	WHERE RULE_ID	= 'YM01'
		AND   YD_GP     = :yd_gp
		AND   BAY_GP    = :bay_gp
		AND   SCH_CD  	like :sch_code
		AND   DEL_YN    = 'N'
		ORDER BY RULE_SEQ
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackRuleInfo_004";	
		Object[] params = {sYdGp,sBayGp,sSchCode};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * YJK
     * 협폭,분할코일 기준정보를 가져온다.
     *	- sch_rule_val (1-폭, 2-길이)
     *	- sch_cd	   (기준값)
     *
     * @param String	: 야드구분
     * @param String	: 스케쥴코드
     *
     * @return JDTORecord 적치기준적용 기준정보
     * @throws DAOException
     */
	public JDTORecord getStackRuleInfo_005(String sYdGp,String sGbn) throws DAOException
	{	
		/*
		Ver1.0--
		SELECT 
		    *
		FROM USRYMA.TB_YM_STACKPRIORITY
		WHERE RULE_ID		= 'YM02'
		AND   YD_GP     	= :yd_gp
		AND   SCH_RULE_VAL 	= :rule_val
		AND   DEL_YN    = 'N'
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackRuleInfo_005";	
		Object[] params = {sYdGp,sGbn};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * YJK
     * COIL 야드의 군관련 설정 정보를 가져온다.
     *
     * @param String	: 야드구분
     *
     * @return JDTORecord 군 기준정보
     * @throws DAOException
     */
	public List getStackRuleInfo_006(String sYdGp) throws DAOException
	{	
		/*
		Ver1.0--
		SELECT  
		  SUBSTR(STACK_RULE_CD,4) AS STACK_RULE_CD,
		  STACK_RULE_MIN,
		  STACK_RULE_MAX
		FROM  TB_YM_STACKRULE
		WHERE YD_GP  			= :YD_GP
		  AND BAY_GP 			= :BAY_GP
		  AND STACK_COL_USAGE_CD= :STACK_COL_USAGE_CD
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackRuleInfo_006";	
		Object[] params = {sYdGp,"B","-"};			
		return super.findList(queryCode, params);
	}
	/**
     * YJK
     * Crane 설비상태를 변경한다.
	 * 
     * @param String	: 스케쥴기준값
     * @param String	: 야드구분
     * @param String	: 스케쥴코드
     *
     * @return 
     * @throws DAOException
     */	
    public int updateStackRuleInfo(String sSchVal,
    							   String sYdGp,
    							   String sBayGp,
    							   String sSchCd) throws DAOException{
		/* 
		Ver4.0--
		UPDATE  USRYMA.TB_YM_STACKPRIORITY
		SET SCH_RULE_VAL = :SCH_RULE_VAL
		WHERE RULE_ID	 = 'YM01'
		AND   YD_GP      = :yd_gp
		AND   BAY_GP     = :bay_gp
		AND   SCH_CD  	 = :sch_code
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackRuleInfo";
		Object[] params = {sSchVal,sYdGp,sSchCd};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     * Crane 설비상태를 변경한다.
	 * 
     * @param String	: 스케쥴기준값
     * @param String	: 야드구분
     * @param String	: 스케쥴코드
     *
     * @return 
     * @throws DAOException
     */	
    public int updateStackRuleInfo_02(String sYdGp,
    							   	  String sGbn,
    							   	  String sVal) throws DAOException{
		/* 
		Ver4.0--
		UPDATE  USRYMA.TB_YM_STACKPRIORITY
		SET SCH_CD = :SCH_CD
		WHERE RULE_ID	 	= 'YM02'
		AND   YD_GP      	= :yd_gp
		AND   SCH_RULE_VAL  = :sch_rule_val
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackRuleInfo_02";
		Object[] params = {sVal,sYdGp,sGbn};	
		return super.updateData(queryCode,params);
	
    }
    
    /**
     * YJK
     *
     * 수조탱크 보급대상 정보를 가져온다.
     *
     * @param String	: 적치열 정보
     *
     * @return JDTORecord 저장품위치정보
     * @throws DAOException
     */			
	public List getTankCoilInfo(String sColGp) throws DAOException
	{	
		/*
		SELECT 
		     A.STACK_COL_GP, 
		     A.STACK_BED_GP, 
		     A.STACK_LAYER_GP, 
		     A.STOCK_ID         AS 저장품,
		     A.STACK_COL_GP||
		     A.STACK_BED_GP||
		     A.STACK_LAYER_GP   AS 현위치,
		     B.STOCK_MOVE_TERM  AS 사유,
		     C.ORD_NO           AS 주문번호, 
		     C.ORD_DTL          AS 주문행번,
		     C.COIL_T           AS 두께,
		     C.COIL_W           AS 폭,
		     C.COIL_WT          AS 중량,
		     C.COIL_OUTDIA      AS 외경,
		     C.NEXT_PROC        AS 차공정
		FROM TB_YM_STACKLAYER A, 
		     TB_YM_STOCK B,
		     USRPMA.TB_PM_COILCOMM C,
		     TB_YM_STACKCOL D
		WHERE A.STACK_COL_GP 	LIKE :col||'%' -- 야드+동+스판+열(1B0301)
		AND A.STOCK_ID 			IS NOT NULL
		AND A.STACK_LAYER_STAT 	= 'L'
		AND A.STACK_COL_GP 		= D.STACK_COL_GP
		AND D.STACK_COL_USAGE_CD in ('C1','C2','C3','C4','C5','C6','C7','C8','G1','G2','G3','G4','G5')
		AND A.STOCK_ID 			= B.STOCK_ID
		AND A.STOCK_ID 			= C.COIL_NO
		AND DECODE(C.NEXT_PROC,'',C.PLAN_PROC1,C.NEXT_PROC) in ('2T','2A')
		ORDER BY A.STACK_LAYER_GP DESC, 
		         A.STACK_COL_GP, 
		         A.STACK_BED_GP
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTankCoilInfo";	
		Object[] params = {sColGp};	
		return super.findList(queryCode, params);
	}
	
	/**
     * YJK
     *
     * 대차출하상차 보급대상 정보를 가져온다.
     *
     * @param String	: 적치열 정보
     *
     * @return JDTORecord 저장품위치정보
     * @throws DAOException
     */			
	public List getTcarCoilInfo(String sColGp) throws DAOException
	{	
		/*
		/*ym.facilitystatus.facilityinquiry.CraneSchDAO.getTcarCoilInfo*/
//		SELECT R0.STACK_COL_GP, 
//		       R0.STACK_BED_GP, 
//		       R0.STACK_LAYER_GP, 
//		       R0.STOCK_ID         AS 저장품,
//		       R0.STORE_LOC_CD     AS 현위치,
//		       R0.STOCK_MOVE_TERM  AS 사유,
//		       R0.ORD_NO           AS 주문번호, 
//		       R0.ORD_DTL          AS 주문행번,
//		       R0.COIL_T           AS 두께,
//		       R0.COIL_W           AS 폭,
//		       R0.COIL_WT          AS 중량,
//		       R0.COIL_OUTDIA      AS 외경,
//		       R0.CURR_PROG_CD     AS 진도코드,
//		       R0.HYSCO_TRANS_GP   AS 운송수단,
//		       R0.CUST_PO_NO       AS 계약번호,
//		       R0.PASS_PROC        AS 통과공정,
//		       R0.USAGE_CD         AS 주문용도,
//		       ---- TUNNING BY MTSUNG ON 2008.08.13-------------------------------
//		       ----NVL(R1.MES_CHECK_YN,'N') AS MES확인유무  -- 새로 추가된 컬럼
//		       NVL((SELECT 'Y' FROM HYSCO_N2@DL_CMSGT R1 
//		                WHERE R1.GOODS_NO = R0.COIL_NO 
//		                    AND R1.MES_CHECK_YN = 'Y' 
//		                    AND R1.MOD_GBN = 'A' 
//		                    AND ROWNUM<=1),'N') AS MES확인유무
//		       ---- TUNNING END ------------------------
//		FROM
//		(SELECT  A.STACK_COL_GP, 
//		         A.STACK_BED_GP, 
//		         A.STACK_LAYER_GP, 
//		         A.STOCK_ID,
//		         A.STACK_COL_GP||
//		         A.STACK_BED_GP||
//		         A.STACK_LAYER_GP   AS STORE_LOC_CD,
//		         B.STOCK_MOVE_TERM,
//		         C.COIL_NO,
//		         C.ORD_NO,
//		         C.ORD_DTL,
//		         C.COIL_T,
//		         C.COIL_W,
//		         C.COIL_WT,
//		         C.COIL_OUTDIA,
//		         C.CURR_PROG_CD,
//		         C.HYSCO_TRANS_GP,
//		         D.CUST_PO_NO,
//		         C.USAGE_CD,
//		         C.PASS_PROC1||'/'||C.PASS_PROC2||'/'||C.PASS_PROC3||'/'||C.PASS_PROC4||'/'||C.PASS_PROC5 AS PASS_PROC
//		    FROM TB_YM_STACKLAYER A, 
//		         TB_YM_STOCK B,
//		         TB_PT_COILCOMM C,
//		         TB_SM_ORDDTL@DL_SMDB D,
//		         TB_YM_STACKCOL E
//		   ----WHERE A.STACK_COL_GP       LIKE :col||'%' -- 야드+동+스판+열(1B0301) --  TUNNING BY MTSUNG ON 2008.08.13  
//		   WHERE A.STACK_COL_GP || ''      LIKE :col||'%' -- 야드+동+스판+열(1B0301)
//		     AND A.STOCK_ID                     IS NOT NULL
//		     AND A.STACK_LAYER_STAT     = 'L'
//		     AND A.STOCK_ID                     = B.STOCK_ID
//		     AND A.STOCK_ID                     = C.COIL_NO
//		     AND A.STACK_COL_GP                 = E.STACK_COL_GP
//		     AND E.STACK_COL_USAGE_CD in ('C1','C2','C3','C4','C5','C6','C7','C8','G1','G2','G3','G4','G5','CW')
//		     AND C.CURR_PROG_CD      = 'K'
//		     AND C.HYSCO_TRANS_GP    = 'C'
//		     -- Hysco 당진 공장 확인 추가
//		     AND C.CUST_CD = 'A42692'
//		     AND C.ORD_NO            = D.ORD_NO(+)
//		     AND C.ORD_DTL           = D.ORD_DTL(+)
//		     ) R0
//		--------- TUNNING BY MTSUNG ON 2008.08.13  FROM 절의 R1을 SELECT절로 이동 ----------
//		----,
//		----(SELECT MAX(IF_TIME_STAMP) AS IF_TIME, 
//		----        GOODS_NO, 
//		----        MES_CHECK_YN
//		----   FROM HYSCO_N2@INI_HYSCO
//		----  WHERE MOD_GBN = 'A'
//		----    AND MES_CHECK_YN = 'Y'
//		----  GROUP BY GOODS_NO,MES_CHECK_YN
//		----) R1
//		----WHERE R0.COIL_NO = R1.GOODS_NO(+)
//		---------- TUNNING END---------------------------
//		ORDER BY R0.STACK_LAYER_GP DESC,
//		R0.STACK_COL_GP, 
//		            R0.STACK_BED_GP
 

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTcarCoilInfo";	
		Object[] params = {sColGp};	
		return super.findList(queryCode, params);
	}
    
    /**
     * YJK
     *
     * 보급가능 수조탱크 정보를 가져온다.
     *
     * @param String	: 야드 정보
     * @param String	: 동 정보
     *
     * @return JDTORecord 적치열정보
     * @throws DAOException
     */			
	public List getTankSaddleInfo(String sYdGp, String sBayGp) throws DAOException{	
		/*
		SELECT 
		     Distinct A.STACK_COL_GP
		FROM TB_YM_STACKCOL A,
		     TB_YM_STACKLAYER B 
		WHERE A.YD_GP           = :YD_GP
		AND   A.BAY_GP          = :BAY_GP
		AND   A.STACK_COL_USAGE_CD = 'CW'
		AND   B.STACK_LAYER_ACTIVE_STAT = 'O'
		AND   A.STACK_COL_GP 	= B.STACK_COL_GP
		ORDER BY A.STACK_COL_GP
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTankSaddleInfo";	
		Object[] params = {sYdGp,sBayGp};	
		return super.findList(queryCode, params);
	}     
	
	/**
     * YJK
     *
     * 확장 콘베이어에 수냉재 보급대상이 있는지를 체크
     *
     *
     * @return JDTORecord 수냉재보급대상 
     * @throws DAOException
     */			
	public JDTORecord getTankInCoilInfo(String sBayGp) throws DAOException
	{	
		/*
		SELECT COUNT(*) AS COUNT
		FROM USRPOA.TB_PO_ABHRTRACKING A,
		     USRPMA.TB_PM_COILCOMM B
		WHERE A.PLANT_GP    = 'B'
		And   A.PROC_GP     = 'X'
		And   (A.EQUIP_GP    like :GP||'%' OR A.EQUIP_GP like 'X%')
		AND   A.STL_NO      = B.COIL_NO
		AND   DECODE(B.NEXT_PROC,'',B.PLAN_PROC1,B.NEXT_PROC)='2T'
		*/
		
		if("B".equals(sBayGp)){
			sBayGp = "XTWB5";
		}else if("C".equals(sBayGp)){
			sBayGp = "XT10";
		}
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTankInCoilInfo";	
		Object[] params = {sBayGp};	
		return super.findByPrimaryKey(queryCode,params);
	}     
	
	public JDTORecord getTankInCoilInfo_01(String sBayGp) throws DAOException
	{	
		/*
		SELECT COUNT(*) AS COUNT
		FROM USRPOA.TB_PO_ABHRTRACKING A,
		     USRPMA.TB_PM_COILCOMM B
		WHERE A.PLANT_GP    = 'B'
		And   A.PROC_GP     = 'X'
		And   (A.EQUIP_GP    like :GP||'%' OR A.EQUIP_GP like 'X%')
		AND   A.STL_NO      = B.COIL_NO
		AND   DECODE(B.NEXT_PROC,'',B.PLAN_PROC1,B.NEXT_PROC)='2T'
		*/
		
		if("B".equals(sBayGp)){
			sBayGp = "XTWB5";
		}else if("C".equals(sBayGp)){
			sBayGp = "XT10";
		}
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTankInCoilInfo";	
		Object[] params = {sBayGp};	
		return super.findByPrimaryKey(queryCode,params);
	} 
	
	/**
     * YJK
     *
     * 수조탱크 실적대상 정보 가져오기
     *
     *
     * @return JDTORecord 수냉재보급대상 
     * @throws DAOException
     */			
	public List getTankWrsltInfo(String sSdate, String sEdate) throws DAOException
	{	
		/*
		SELECT  C.COIL_NO          AS 저장품, 
		        C.ORD_NO           AS 주문번호, 
		        C.ORD_DTL          AS 주문행번,
		        C.COIL_T           AS 두께,
		        C.COIL_W           AS 폭,
		        C.COIL_WT          AS 중량,
		        C.COIL_OUTDIA      AS 외경,
		        C.CURR_PROG_CD     AS 진도코드,
		        C.HYSCO_TRANS_GP   AS 운송수단
		FROM TB_PO_COILSHEARORD_WRSLT A,
		    (
		    SELECT COIL_NO, MAX(STEP_NO) AS STEP_NO
		    FROM TB_PO_COILSHEARORD_WRSLT
		    WHERE PLANT_GP = 'B'
		    AND   PROC_GP  = 'T'
		    AND   REG_DDTT BETWEEN :DATE1||'000000' AND :DATE2||'999999'
		    GROUP BY COIL_NO
		    )B,
		    TB_PM_COILCOMM C
		WHERE A.COIL_NO = B.COIL_NO
		AND   A.STEP_NO = B.STEP_NO    
		AND   A.PLANT_GP = 'B'
		AND   A.PROC_GP  = 'T'
		AND   A.REG_DDTT BETWEEN :DATE1||'000000' AND :DATE2||'999999'
		AND   A.COIL_NO  = C.COIL_NO
		ORDER BY A.REG_DDTT DESC
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getTankWrsltInfo";	
		Object[] params = {sSdate,sEdate,sSdate,sEdate};	
		return super.findList(queryCode,params);
	}     
		
	/**
     * YJK
     *
     * To위치 Fail Log 가져오기
     *
     *
     * @return List  
     * @throws DAOException
     */			
	public List getToLocFailLog(String sSdate, String sEdate) throws DAOException
	{	
		/*
		SELECT
		    A.TC_OCCUR_DDTT AS 발생일시,
		    A.TC_OCCUR_CONTENTS AS 발생내용
		FROM
		(
		    SELECT
		        ROWNUM AS SEQ,
		        A.TC_LOG_ID AS TCLOGID,
		        A.TC_CD AS TC_CD,
		        TO_CHAR(A.TC_OCCUR_DDTT,'YYYYMMDD') AS TC_OCCUR_DD,
		        TO_CHAR(A.TC_OCCUR_DDTT,'YYYY.MM.DD HH24:MI:SS') AS TC_OCCUR_DDTT,
		        A.TC_OCCUR_CONTENTS AS TC_OCCUR_CONTENTS,
		        A.TC_OCCUR_TYPE_CONTENTS AS TC_OCCUR_TYPE_CONTENTS,
		        A.TC_OCCUR_TYPE  AS TC_OCCUR_TYPE,
		        A.TC_OCCUR_MSG AS TC_OCCUR_MSG,
		        A.TC_RESTART_COUNT AS TC_RESTART_COUNT
		    FROM TB_YM_IFTCLOG A
		    WHERE A.YD_GP = 'U'          --야드구분(1)
		    AND A.TC_CD   = :tc_cd      --TC코드(POYM001)
		    AND A.TC_OCCUR_DDTT BETWEEN TO_DATE(:sdate||'0000','YYYY/MM/DD HH24MI')      --시작일자(20051128)
		                            AND TO_DATE(:edate||'2359','YYYY/MM/DD HH24MI') --종료일자(20051128)
		)A,    
		(SELECT DISTINCT TC_CD,ITEM_SND_RCV_GP FROM TB_YM_IFTCLAYOUT) B
		WHERE A.TC_CD = B.TC_CD
		AND B.ITEM_SND_RCV_GP = 'R'
		ORDER BY 발생일시 DESC
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getToLocFailLog";	
		Object[] params = {sSdate,sEdate};	
		return super.findList(queryCode,params);
	}     
	
	/**
     * YJK
     *
     * 코일 이미지 파일명 가져오기
     *
     *
     * @return String,String  
     * @throws DAOException
     */			
	public JDTORecord getCoilImageInfo(String sClass1,String sClass2) throws DAOException
	{	
		/*
		SELECT CLASS4_NAME1
		FROM TB_CM_CDCLASS4
		WHERE TYPE_CD = 'YM009'   --유형코드 (YM009)
		AND CLASS3_CD = :c3    --분류2CODE (L)
		AND CLASS4_CD = :c4    --분류2CODE (D1)
		AND DEL_YN    = 'N'
		*/
		
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilImageInfo";	
		Object[] params = {sClass1,sClass2};	
		return super.findByPrimaryKey(queryCode,params);
	}     
	
	
	/**
     * YJK
     *
     * 해당 동에 크레인기준에 등록된 
     * 동내이적/동간이적 스케쥴 코드를 가져온다.
     *
     * @return List  
     * @throws DAOException
     */			
	public List getBaySchInfo(String sYdGp,
							  String sBayGp,
							  String sWorkGbn) throws DAOException{	
		/*
		SELECT *
		FROM TB_CM_CDCLASS2 A,
		     TB_YM_SCHRULE  B
		WHERE A.TYPE_CD    ='YM104'
		AND A.CLASS1_CD    = :YD_GP            --야드구분
		AND A.CLASS2_NAME1 like :EQUIP_GP||'%' --설비구분('동간이적' or '동내이적')
		AND A.DEL_YN       = 'N'
		AND B.YD_GP  	   = :YD_GP 
		AND B.BAY_GP 	   = :BAY_GP
		AND A.CLASS2_CD    = B.SCH_WORK_KIND 
		ORDER BY A.CLASS2_CD DESC
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getBaySchInfo";	
		Object[] params = {sYdGp,sWorkGbn,sYdGp,sBayGp};	
		return super.findList(queryCode,params);
	}     
	
	/**
     * YJK
     *
     * 해당 동에 크레인기준에 등록된 
     * 동내이적/동간이적 스케쥴 코드를 가져온다.
     *
     * @return List  
     * @throws DAOException
     */			
	public JDTORecord getBayCrnSchInfo(String sYdGp,
							  	     String sBayGp,
							  	     String sCraneNo,
							  	     String sSchCd) throws DAOException{	
		/*
		SELECT *
		FROM TB_YM_SCHRULE
		WHERE YD_GP  	   			= :YD_GP 
		AND   BAY_GP 	   			= :BAY_GP
		AND   SCH_RULE_ACTIVE_STAT 	= 'A'
		AND   SCH_RULE_CRANE_NO 	= :CRANE_NO
		AND   SCH_WORK_KIND 			LIKE :SCH_CD||'%'
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getBayCrnSchInfo";	
		Object[] params = {sYdGp,sBayGp,sCraneNo,sSchCd};	
		return super.findByPrimaryKey(queryCode,params);
	}     
	
	/**
     * YJK
     *
     *
     * @return List  
     * @throws DAOException
     */			
	public List getSlabList(String sStockId) throws DAOException
	{	
		/*
		SELECT 
			SLAB_NO,
			BUY_SLAB_NO,
			DECODE(:SLAB_NO,SLAB_NO,'S',BUY_SLAB_NO,'B','X') AS GBN
		FROM TB_PM_SLABCOMM
		WHERE SLAB_NO  = :SLAB_NO
		OR BUY_SLAB_NO = :SLAB_NO
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getSlabList";	
		Object[] params = {sStockId,sStockId,sStockId};	
		return super.findList(queryCode,params);
	}     
	
	/**
     * YJK
     * 적치열 용도 수정한다.
	 * 
     * @param String	: 적치열코드
     * @param String	: 용도코드
     *
     * @return 
     * @throws DAOException
     */	
    public int updateStackColUsageInfo(String sUsageCd, String sColGp) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_STACKCOL
		SET STACK_COL_USAGE_CD 	= :USAGE_CD,
			modifier   = 'SYSTEM',
			mod_ddtt   = sysdate     
		WHERE STACK_COL_GP 	= :COL_GP
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackColUsageInfo";
		Object[] params = {sUsageCd,sColGp};	
		return super.updateData(queryCode,params);
	
    }
    
    /**
     * YJK
     * 용도수정으로 인한 저장영역을 삭제한다.
	 * 
     * @param String	: 적치열코드
     * @param String	: 용도코드
     *
     * @return 
     * @throws DAOException
     */	
    public int deleteLocSearchInfo(String sColGp) throws DAOException{
		/* 
		Ver4.0--
		DELETE TB_YM_LOCSEARCH
		WHERE (
		        STOCK_MOVE_ROUTE_ID,
		        LOC_SEARCH_ID
		      )IN
		      (
		        SELECT 
		            C.STOCK_MOVE_ROUTE_ID,
		            C.LOC_SEARCH_ID
		        FROM TB_YM_STACKCOL A,
		             TB_YM_STOCKMOVEROUTE B,
		             TB_YM_LOCSEARCH C
		        WHERE A.STACK_COL_GP        = :COL_GP      
		        AND   A.STACK_COL_USAGE_CD  IN ('TS','FS')
		        AND   A.STACK_COL_GP        = C.STACK_COL_GP
		        AND   B.STOCK_MOVE_ROUTE_ID = C.STOCK_MOVE_ROUTE_ID
		        AND   A.STACK_COL_USAGE_CD  <> B.STACK_USAGE_CD_TO
		       )
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteLocSearchInfo";
		Object[] params = {sColGp};	
		return super.updateData(queryCode,params);
	
    }
	
    /**
     * YJK
     * 설비상태 수정한다.
	 * 
     * @param String	: 적치열코드
     * @param String	: 용도코드
     *
     * @return 
     * @throws DAOException
     */	
    public int UpdateEquipStatInfo(String sEquipStat,
    							   String sEquipGp) throws DAOException{
		/* 
		Ver4.0--
		UPDATE TB_YM_EQUIP
		SET EQUIP_STAT  = :EQUIP_STAT,
			modifier   = 'SYSTEM',
			mod_ddtt   = sysdate     
		WHERE EQUIP_GP 	= :EQUIP_GP
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.UpdateEquipStatInfo";
		Object[] params = {sEquipStat,sEquipGp};	
		return super.updateData(queryCode,params);
	}	
	
	/**
     * YJK
     *
     *
     * @return List  
     * @throws DAOException
     */			
	public List getBConveyorTrackingList() throws DAOException{	
		/*
		SELECT DECODE(SUBSTR(A.EQUIP_GP,0,2),'XT','XT','ST') AS NAME,
		       A.PLANT_GP           AS PLANT_GP,
		       A.PROC_GP            AS PROC_GP,
		       A.EQUIP_GP           AS EQUIP_GP,
		       SUBSTR(A.EQUIP_GP,3) AS EQUIP_NAME,      --설비명
		       A.STL_NO             AS STL_NO,          --코일번호
		       A.OCCUR_DDTT         AS OCCUR_DDTT,
		       A.SORT_SEQ           AS SORT_SEQ,
		       B.COIL_WT            AS COIL_WT,         --중량
		       B.COIL_T             AS COIL_T,          --두께
		       B.COIL_W             AS COIL_W,          --폭
		       B.CURR_COIL_LEN      AS COIL_LEN         --길이
		FROM USRPOA.TB_PO_ABHRTRACKING A, TB_PM_COILCOMM B
		WHERE A.PLANT_GP    = 'B'
		 AND  A.PROC_GP     IN('D','X')
		 AND  (
		       A.EQUIP_GP LIKE 'XT%'  
		       OR  
		       A.EQUIP_GP LIKE 'STD%'  
		       OR  
		       A.EQUIP_GP LIKE 'EX%' 
		      ) 
		 AND  A.STL_NO = B.COIL_NO(+)
		ORDER BY SORT_SEQ
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getBConveyorTrackingList";	
		Object[] params = {};	
		return super.findList(queryCode,params);
	}     
	
	
	/**
     * YJK
     *
     * @return List  
     * @throws DAOException
     */			
	public List getWbookListData(String sYdGp,
								 String sDong,
								 String sSchCode) throws DAOException{	
		/*
		SELECT 
		    a.wbook_id,
		    b.stock_id,
		    c.stack_col_gp,
		    c.stack_bed_gp,
		    c.stack_layer_gp
		FROM tb_ym_wbook a,
		     tb_ym_stock b,
		     tb_ym_stacklayer c   
		WHERE a.yd_gp  			    = :yd_gp
		AND   a.bay_gp 			    = :bay_gp 
		AND   a.sch_work_kind 		= :sch_work_kind 
		AND   not exists (select wbook_id from tb_ym_sch where wbook_id = a.wbook_id and sch_work_aid_yn = 'M')
		AND   a.wbook_id            = b.wbook_id
		AND   b.stock_id            = c.stock_id
		ORDER BY a.wbook_id
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getWbookListData";	
		Object[] params = {sYdGp,sDong,sSchCode};	
		return super.findList(queryCode,params);
	}     
	
	/**
     *	YJK
     *	해당적치열 정보를 비활성화 한다.
     *	대차 모듈에서 사용.
	 * 
     * @param String	: 적치열
     *
     * @return 
     * @throws DAOException
     */	
    public int updateStackLayerStatInfo(String sColGp) throws DAOException{
		/* 
		Ver4.0--
		UPDATE  USRYMA.TB_YM_STACKLAYER
		SET     STACK_LAYER_ACTIVE_STAT = 'C'
		WHERE   STACK_COL_GP LIKE :STACK_COL_GP||'%'
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackLayerStatInfo";
		Object[] params = {sColGp};	
		return super.updateData(queryCode,params);
	}
	
	/**
     *	YJK
     *	해당적치열 정보를 비활성화 한다.
     *	대차 모듈에서 사용.
	 * 
     * @param String	: 적치열
     *
     * @return 
     * @throws DAOException
     */	
    public int updateStackLayerStatInfo_01(String sColGp) throws DAOException{
		/* 
		Ver4.0--
		UPDATE  USRYMA.TB_YM_STACKLAYER
		SET     STACK_LAYER_ACTIVE_STAT = 'C'
		WHERE   STACK_COL_GP LIKE :STACK_COL_GP||'%'
		AND		STOCK_ID IS NULL
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackLayerStatInfo_01";
		Object[] params = {sColGp};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * 	YJK
     *	설비테이블 대차 스케쥴 설정정보 셋팅.
     *
     * @param String	: 적치단활성상태
     * @param String	: 저장품ID
     *
     * @return int
     * @throws DAOException
     */			
	public int updateEquipSchInfo(String sEquipGp,
								  String sUDong,
								  String sUYn,
								  String sUSchCd,
								  String sPDong,
								  String sPYn,
								  String sPSchCd) throws DAOException{	
		return updateEquipSchInfo_01(sEquipGp,
									 sUDong,
									 sUDong,
									 sUYn,
									 sUSchCd,
									 sPDong,
									 sPYn,
									 sPSchCd);
	}
	
	public int updateEquipSchInfo_01(String sEquipGp,
									 String sCDong,
									 String sUDong,
									 String sUYn,
									 String sUSchCd,
									 String sPDong,
									 String sPYn,
									 String sPSchCd) throws DAOException{	
		/*
		Ver4.0--
		UPDATE  USRYMA.TB_YM_EQUIP
		SET     CURR_STOP_LOC           = :CURR_STOP_LOC,
		        CARLOAD_STOP_LOC        = :CARLOAD_STOP_LOC,
		        CARUNLOAD_STOP_LOC      = :CARUNLOAD_STOP_LOC,
		        CARLOAD_ASSIGN_YN       = :CARLOAD_ASSIGN_YN,
		        CARLOAD_SCH_WORK_KIND   = :CARLOAD_SCH_WORK_KIND,
		        CARUNLOAD_ASSIGN_YN     = :CARUNLOAD_ASSIGN_YN,
		        CARUNLOAD_SCH_WORK_KIND = :CARUNLOAD_SCH_WORK_KIND
		WHERE   EQUIP_GP                = :EQUIP_GP
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEquipSchInfo";	
		Object[] params = {sCDong,sUDong,sPDong,sUYn,sUSchCd,sPYn,sPSchCd,sEquipGp};	
		return super.updateData(queryCode,params);
	}
	
	
	public int updateEquipSchInfo_02(String sEquipGp,
									 String sCDong,
									 String sUDong,
									 String sUYn,
									 String sUSchCd,
									 String sPDong,
									 String sPYn,
									 String sPSchCd) throws DAOException{	
		/*
		Ver4.0--
		UPDATE  USRYMA.TB_YM_EQUIP
		SET     CURR_STOP_LOC           = :CURR_STOP_LOC,
		        CARLOAD_STOP_LOC        = :CARLOAD_STOP_LOC,
		        CARLOAD_ASSIGN_YN       = :CARLOAD_ASSIGN_YN,
		        CARLOAD_SCH_WORK_KIND   = :CARLOAD_SCH_WORK_KIND,
		        WAIT_STOP_LOC      		= :CARUNLOAD_STOP_LOC,
		        CARUNLOAD_ASSIGN_YN     = :CARUNLOAD_ASSIGN_YN,
		        WBOOK_ID 				= :CARUNLOAD_SCH_WORK_KIND
		WHERE   EQUIP_GP                = :EQUIP_GP
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEquipSchInfo_02";	
		Object[] params = {sCDong,sUDong,sUYn,sUSchCd,sPDong,sPYn,sPSchCd,sEquipGp};	
		return super.updateData(queryCode,params);
	}
	
	public int updateEquipSchInfo_03(String sEquipGp) throws DAOException{	
		/*
		Ver4.0--
		UPDATE  USRYMA.TB_YM_EQUIP
		SET     (
		         CARUNLOAD_STOP_LOC,
		         CARUNLOAD_SCH_WORK_KIND)=
		        (
		        SELECT  WAIT_STOP_LOC,  
		                WBOOK_ID 
		        FROM    USRYMA.TB_YM_EQUIP     
		        WHERE   EQUIP_GP = :EQUIP_GP
		        )
		WHERE   EQUIP_GP = :EQUIP_GP
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEquipSchInfo_03";	
		Object[] params = {sEquipGp,sEquipGp};	
		return super.updateData(queryCode,params);
	}
	
	/*public int updateEquipSchInfo_04(String sEquipGp) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE  USRYMA.TB_YM_EQUIP
		SET     CURR_STOP_LOC           = :CURR_STOP_LOC,
		        CARLOAD_STOP_LOC        = :CARLOAD_STOP_LOC,
		        CARLOAD_ASSIGN_YN       = :CARLOAD_ASSIGN_YN,
		        CARLOAD_SCH_WORK_KIND   = :CARLOAD_SCH_WORK_KIND,
		        WAIT_STOP_LOC      		= :CARUNLOAD_STOP_LOC,
		        CARUNLOAD_ASSIGN_YN     = :CARUNLOAD_ASSIGN_YN,
		        WBOOK_ID 				= :CARUNLOAD_SCH_WORK_KIND
		WHERE   EQUIP_GP                = :EQUIP_GP
	
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEquipSchInfo_04";	
		Object[] params = {sCDong,sUDong,sUYn,sUSchCd,sPDong,sPYn,sPSchCd,sEquipGp};	
		return super.updateData(queryCode,params);
	}*/	
	/**
     * YJK
     *
     * 대차설비의 상태정보를 UPDATE
     *
     * @param String	: 적재상태
     * @param String	: 작업진행상태
     *
     * @return int
     * @throws DAOException
     */			
	public int updateEquipTcInfo(String sStackStat,
								 String sWprogStat,
								 String sEquipGp) throws DAOException{	
		/*
		Ver4.0--
		UPDATE tb_ym_equip
		SET	
			STACK_STAT = :STACK_STAT,
			WPROG_STAT = :WPROG_STAT,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE equip_gp = :equip_gp
		*/

		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateEquipTcInfo";	
		Object[] params = {sStackStat,sWprogStat,sEquipGp};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     * 정정작업실적 TABLE에서 코일정보를 가져온다.
     * 
     * @param String	: 코일번호
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getCoilShearOrdInfo(String sStockId) throws DAOException{
		/*
		SELECT COIL_NO 
		  FROM    TB_HR_C_SHEARWOWR
		 WHERE   COIL_NO = :COIL_NO
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCoilShearOrdInfo";	
		Object[] params = {sStockId};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     * 크레인에 작업지시 시점에 작업지시 일자를 셋팅한다.
	 *  
     * @param String	: 스케쥴ID
     * @param String	: 작업지시일자
     *
     * @return 
     * @throws DAOException
     */		
    public int updateWbookSchActDdttSchInfo(String sSchId,
									  		String sDate) throws DAOException{
		/* 
		Ver4.0--
		UPDATE tb_ym_sch
		SET 
			wbook_sch_act_ddtt = :ddtt,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate     
		WHERE sch_id   = :sch_id
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateWbookSchActDdttSchInfo";
		Object[] params = {sDate,sSchId};	
		return super.updateData(queryCode,params);
    }
    
    /**
     * YJK
     * 작업예약 TABLE에 있는 Regacy 작업예약을 삭제한다.
	 * 
     * @return 
     * @throws DAOException
     */	
    public int deleteAllWbookId() throws DAOException{
		/* 
		Ver4.0--
		DELETE TB_YM_WBOOK A
		WHERE NOT EXISTS (
						SELECT * FROM TB_YM_STOCK
						WHERE WBOOK_ID = A.WBOOK_ID
						)
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteAllWbookId";
		Object[] params = {};	
		return super.updateData(queryCode,params);
	
    }
    
    /**
	 * YJK
     *	해당크레인에 할당된 스케쥴의
	 *	상위 우선순위를 찾는다(UP지시만)
	 *	예) A1 크레인이 01단 권상작업지시상태에서
	 *		02단에 A1 크레인에 할당된 또다른 작업이 존재할 때.
     *
     * @param String	: 스케쥴ID
     *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord getCraneSchCoilInfo(String sSchId) throws DAOException{	
		/*
		Ver1.0--
		SELECT 
		    b.*
		FROM tb_ym_stacklayer a,
		     tb_ym_sch b,
		    (
		    SELECT 
		           a.yd_gp,
		           a.bay_gp,
		           a.sch_work_equip_no,
		    	   b.stack_col_gp,
		    	   b.stack_bed_gp,
		    	   b.stack_layer_gp
		    FROM tb_ym_sch a , 
		         tb_ym_stacklayer b
		    WHERE a.sch_id 	   		= :sch_id
		    AND   a.stock_id 		= b.stock_id
		    AND   b.stack_layer_stat in('L','U')
		    AND   b.stack_col_gp not like '__CR__'
		    )c     
		WHERE a.stock_id            = b.stock_id    
		AND   b.yd_gp               = c.yd_gp
		AND   b.bay_gp              = c.bay_gp
		AND   b.sch_work_equip_no   = c.sch_work_equip_no
		AND   ( 
		        (
		        a.stack_col_gp        = c.stack_col_gp
		        AND   
		        a.stack_bed_gp        = LPAD(c.stack_bed_gp - 1, 2 ,'0')  
		        AND   
		        a.stack_layer_gp      = LPAD(c.stack_layer_gp + 1, 2 ,'0')  
		        )
		        OR
		        (
		        a.stack_col_gp        = c.stack_col_gp
		        AND   
		        a.stack_bed_gp        = c.stack_bed_gp  
		        AND   
		        a.stack_layer_gp      = LPAD(c.stack_layer_gp + 1, 2 ,'0')  
		        )
		      )
		ORDER BY a.stack_layer_gp DESC,
				 b.sch_id
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchCoilInfo";	
		Object[] params = {sSchId};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * YJK
     *	해당크레인에 할당된 스케쥴의
	 *	상위 우선순위를 찾는다(UP지시만)
	 *	예) A1 크레인이 01단 권상작업지시상태에서
	 *		02단에 A1 크레인에 할당된 또다른 작업이 존재할 때.
     *
     * @param String	: 스케쥴ID
     *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord getCraneSchSlabInfo(String sSchId) throws DAOException{	
		/*
		Ver1.0--
		SELECT 
		    b.*
		FROM tb_ym_stacklayer a,
		     tb_ym_sch b,
		    (
		    SELECT 
		           a.yd_gp,
		           a.bay_gp,
		           a.sch_work_equip_no,
		    	   b.stack_col_gp,
		    	   b.stack_bed_gp,
		    	   b.stack_layer_gp
		    FROM tb_ym_sch a , 
		         tb_ym_stacklayer b
		    WHERE a.sch_id 	   		= :sch_id
		    AND   a.stock_id 		= b.stock_id
		    AND   b.stack_layer_stat in('L','U')
		    AND   b.stack_col_gp not like '__CR__'
		    )c     
		WHERE a.stock_id            = b.stock_id    
		AND   b.yd_gp               = c.yd_gp
		AND   b.bay_gp              = c.bay_gp
		AND   b.sch_work_equip_no   = c.sch_work_equip_no
		AND   a.stack_col_gp        = c.stack_col_gp
		AND   a.stack_bed_gp        = c.stack_bed_gp  
		AND   a.stack_layer_gp      > c.stack_layer_gp
		ORDER BY a.stack_layer_gp DESC,
				 b.sch_id
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchSlabInfo";	
		Object[] params = {sSchId};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	
	/**
	 * YJK
     *	스카핑 패턴 정보를 가져온다.
	 * @param String	: 스케쥴ID
     *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord getScarfingPatternInfo(String sPattern) throws DAOException{	
		/*
		Ver1.0--
		SELECT 
		    SCARFING_DEPTH,
		    SCARFING_TEMP,
		    SCARFING_ORD_UP,
		    SCARFING_ORD_DOWN,
		    SCARFING_ORD_LEFT,
		    SCARFING_ORD_RIGHT,
		    SCARFING_ORD_LEFT_CORNER,
		    SCARFING_ORD_RIGHT_CORNER
		FROM USRQMA.TB_QM_SCARFINGRULE
		WHERE SCARFING_PATTERN = :SCARFING_PATTERN
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getScarfingPatternInfo";	
		Object[] params = {sPattern};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * YJK
     *	B열연 COIL 분기콘베이어 동별로 가장 빠르게
     *	LINE-OFF 해야할 대상재 정보 가져오기.
     *
	 * @param String	: 야드구분
	 * @param String	: 동구분
	 * @param String	: 스케쥴코드
	 * @param String	: 크레인번호
     *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord getFirstDConveyorInfo(String sYdGp,
											String sBayGp,
											String sSchCode,
											String sEquipNo) throws DAOException{	
		/*
		Ver1.0--
		SELECT 
		    B.STACK_COL_GP,
		    B.STACK_BED_GP,
		    A.*
		FROM TB_YM_SCH A,
		     TB_YM_STACKLAYER B
		WHERE A.STOCK_ID        = B.STOCK_ID
		AND   B.STACK_COL_GP    LIKE '3_ST%'
		AND   YD_GP             = :YD_GP
		AND   BAY_GP            = :BAY_GP
		AND   SCH_WORK_KIND     = :SCH_CODE
		AND   SCH_WORK_EQUIP_NO = :EQUIP_NO
		ORDER BY B.STACK_BED_GP DESC
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getFirstDConveyorInfo";	
		Object[] params = {sYdGp,sBayGp,sSchCode,sEquipNo};			
		return super.findByPrimaryKey(queryCode,params);
	}

	/**
	 * 	최규성 2010-01-26
     *	B열연 SPM2 COIL 추출시
     *	LINE-OFF 해야할 대상재 정보 가져오기.
     *
	 * @param String	: 야드구분
	 * @param String	: 동구분
	 * @param String	: 스케쥴코드
	 * @param String	: 크레인번호
     *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord getFirstSchInfo_Spm2( String sYdGp,
											String sBayGp,
											String sSchCode,
											String sEquipNo) throws DAOException{
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getFirstSchInfo_SPM2";
		Object[] params = {sYdGp,sBayGp,sSchCode,sEquipNo};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * 	최규성 2010-01-26
     *	B열연 SPM2 COIL 추출시
     *	LINE-OFF 해야할 대상재 정보 가져오기.
     *
	 * @param String	: 야드구분
	 * @param String	: 동구분
	 * @param String	: 스케쥴코드
	 * @param String	: 크레인번호
     *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord getEquipInfo_Spm2( String sEquipNo) throws DAOException{
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getEquipInfo_SPM2";
		Object[] params = {sEquipNo};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	
	
	/**
	 * YJK
     *	B열연 COIL 확장 콘베이어 동별로 가장 빠르게
     *	LINE-OFF 해야할 대상재 정보 가져오기.
     *
	 * @param String	: 야드구분
	 * @param String	: 동구분
	 * @param String	: 스케쥴코드
	 * @param String	: 크레인번호
     *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord getFirstEConveyorInfo(String sYdGp,
											String sBayGp,
											String sSchCode,
											String sEquipNo) throws DAOException{	
		/*
		Ver1.0--
		SELECT 
		    B.STACK_COL_GP,
		    B.STACK_BED_GP,
		    A.*
		FROM TB_YM_SCH A,
		     TB_YM_STACKLAYER B
		WHERE A.STOCK_ID        = B.STOCK_ID
		AND   B.STACK_COL_GP    = '3CWB10'
		AND   YD_GP             = :YD_GP
		AND   BAY_GP            = :BAY_GP
		AND   SCH_WORK_KIND     = :SCH_CODE
		AND   SCH_WORK_EQUIP_NO = :EQUIP_NO
		ORDER BY B.STACK_BED_GP 
		*/
 
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getFirstEConveyorInfo";	
		Object[] params = {sYdGp,sBayGp,sSchCode,sEquipNo};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * YJK
     *	B열연 SLAB 해당동에서 가장 빠른 장입대상재를 검색한다.	
     *
	 * @param String	: 동구분
	 *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord selectWBSlabSearch_06(String sBayGp) throws DAOException{	
		/*
		Ver1.0--
		SELECT  STOCK.STOCK_ID,
		        STOCK.CHARGE_LOT_NO,
		        LAYER.STACK_COL_GP,
		        LAYER.STACK_BED_GP,
		        LAYER.STACK_LAYER_GP,
		        COL.BAY_GP
		FROM    TB_YM_STOCK         STOCK,
		        TB_YM_STACKLAYER    LAYER,
		        TB_YM_STACKCOL      COL
		WHERE   STOCK.CHARGE_LOT_NO = 
		        (
		        SELECT  MIN(CHARGE_LOT_NO)
		        FROM    TB_YM_STOCK A,
		                TB_YM_STACKLAYER B,
		                TB_YM_STACKCOL C
		        WHERE   A.CHARGE_LOT_NO IS NOT NULL
		        AND     C.YD_GP         = '2'
		        AND     C.BAY_GP        IN ('A','B',:BAY)
		        AND     C.STACK_COL_GP NOT IN ('2ABK01','2ABK02')
		        AND     A.STOCK_ID      = B.STOCK_ID
		        AND     B.STACK_COL_GP  = C.STACK_COL_GP
		        AND     B.STACK_LAYER_STAT  IN ('L','S','U')
		        AND     NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
		        )
		AND     COL.YD_GP               = '2'
		AND     COL.BAY_GP              IN ('A','B',:BAY)
		AND     COL.STACK_COL_GP  NOT IN ('2ABK01','2ABK02')
		AND     LAYER.STACK_LAYER_STAT  IN ('L','S','U')
		AND     LAYER.STACK_COL_GP      = COL.STACK_COL_GP
		AND     STOCK.WBOOK_ID 		    IS NULL
		AND     STOCK.STOCK_ID 			= LAYER.STOCK_ID
		ORDER BY STACK_COL_GP DESC, 
				 STACK_BED_GP, 
				 STACK_LAYER_GP
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_06";	
		Object[] params = {sBayGp,sBayGp};			
		return super.findByPrimaryKey(queryCode,params);
	}
	
	/**
	 * YJK
     *
	 * @param String	: 동구분
	 *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord selectWBSlabSearch_07(String sYardId, 
										    String sSchCode,
										    String sTcNo)throws DAOException{	  
		/*
		Ver4.2--
		SELECT A.*,
		       B.CARUNLOAD_PUT_LOC 
		FROM TB_YM_WBOOK A,
		     TB_YM_STOCK B
		WHERE A.YD_GP  			= :YD_GP
		AND   A.SCH_WORK_KIND 	= :SCH_WORK_KIND 
		AND   A.CRANE_WORD_PUT_LOC LIKE :TC||'%'
		AND   NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
		AND   A.WBOOK_ID        = B.WBOOK_ID
		ORDER BY A.WBOOK_ID
		*/
		
		String  queryCode 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_07";	
		Object[] params = {sYardId,sSchCode,sTcNo};	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	/**
	 * YJK
     *
	 * @param String	: 동구분
	 *
     * @return JDTORecord 
     * @throws DAOException
     */
	public List selectLoadSch(String sYdGp, 
							  String sBayGp, 	
							  String sSchCd)throws DAOException{	  
		/*
		Ver4.2--
		SELECT A.WBOOK_ID  AS WBOOK_ID,
		       C.STACK_COL_GP||
		       C.STACK_BED_GP AS BED,
		       C.STACK_LAYER_GP AS LAYER
		FROM TB_YM_WBOOK A,
		     TB_YM_STOCK B,
		     TB_YM_STACKLAYER C
		WHERE A.YD_GP  			= :YD_GP
		AND   A.BAY_GP 			= :BAY_GP 
		AND   A.SCH_WORK_KIND 	= :SCH_WORK_KIND 
		AND   NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
		AND   A.WBOOK_ID        = B.WBOOK_ID
		AND   B.STOCK_ID        = C.STOCK_ID
		AND   C.STACK_LAYER_STAT  IN ('L','S','U')
		ORDER BY A.WBOOK_ID
		*/
		
		String  queryCode 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSch";	
		Object[] params = {sYdGp,sBayGp,sSchCd};	
		
		return super.findList(queryCode, params);
	} 
	
	/**
	 * YJK
     *
	 * @param String	: 동구분
	 *
     * @return JDTORecord 
     * @throws DAOException
     */
	public List selectLoadSch_01(String sYdGp, 
							     String sBayGp, 	
							     String sSchCd,
							     String sTcNo)throws DAOException{	  
		/*
		Ver4.2--
		SELECT A.*,
		       B.CARUNLOAD_PUT_LOC,
		       C.STACK_COL_GP||
		       C.STACK_BED_GP AS BED,
		       C.STACK_LAYER_GP AS LAYER
		FROM TB_YM_WBOOK A,
		     TB_YM_STOCK B,
		     TB_YM_STACKLAYER C
		WHERE A.YD_GP  			= :YD_GP
		AND   A.BAY_GP 			= :BAY_GP 
		AND   A.SCH_WORK_KIND 	= :SCH_WORK_KIND 
		AND   A.CRANE_WORD_PUT_LOC LIKE :TC||'%'
		AND   NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
		AND   A.WBOOK_ID        = B.WBOOK_ID
		AND   B.STOCK_ID        = C.STOCK_ID
		AND   C.STACK_LAYER_STAT  IN ('L','S','U')
		ORDER BY A.WBOOK_ID
		*/
		
		String  queryCode 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSch_01";	
		Object[] params = {sYdGp,sBayGp,sSchCd,sTcNo};	
		
		return super.findList(queryCode, params);
	} 
	
	/**
	 * YJK
     *
	 * @param String	: 동구분
	 *
     * @return JDTORecord 
     * @throws DAOException
     */
	public List selectLoadSch_02(String sYdGp, 
							String sBayGp, 	
							String sSchCd1,
							String sSchCd2,
							String sTcNo)throws DAOException{	  
		/*
		Ver4.2--
		SELECT A.*,
		       B.CARUNLOAD_PUT_LOC,
		       C.STACK_COL_GP||
		       C.STACK_BED_GP AS BED,
		       C.STACK_LAYER_GP AS LAYER
		FROM TB_YM_WBOOK A,
		     TB_YM_STOCK B,
		     TB_YM_STACKLAYER C
		WHERE A.YD_GP  			= :YD_GP
		AND   A.BAY_GP 			= :BAY_GP 
		AND   A.SCH_WORK_KIND 	IN( :SCH_WORK_KIND1, :SCH_WORK_KIND2)
		AND   A.CRANE_WORD_PUT_LOC LIKE :TC||'%'
		AND   NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
		AND   A.WBOOK_ID        = B.WBOOK_ID
		AND   B.STOCK_ID        = C.STOCK_ID
		AND   C.STACK_LAYER_STAT  IN ('L','S','U')
		ORDER BY A.WBOOK_ID
		*/
		
		String  queryCode 	= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectLoadSch_02";	
		Object[] params = {sYdGp,sBayGp,sSchCd1,sSchCd2,sTcNo};	
		
		return super.findList(queryCode, params);
	} 
	
	/**
	 * YJK
     *
	 * @param String	: 동구분
	 *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord selectTCCoilSearch_01(String sYdGp, 
										    String sSchCode)throws DAOException{	  
		/*
		Ver4.2--
		SELECT 
		    B.STOCK_ID,
		    B.CARUNLOAD_PUT_LOC,
		    A.WBOOK_ID,
		    A.YD_GP,
		    A.BAY_GP,
		    A.SCH_WORK_KIND,
		    A.SCH_WORK_LOC_DECISION_METHOD,
		    A.CRANE_WORD_PUT_LOC
		FROM TB_YM_WBOOK A,
		     TB_YM_STOCK B
		WHERE A.YD_GP  			= :YD_GP
		AND   A.SCH_WORK_KIND 	= :SCH_WORK_KIND 
		AND   NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
		AND   A.WBOOK_ID        = B.WBOOK_ID
		ORDER BY A.WBOOK_ID
		*/
		String  queryCode 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectTCCoilSearch_01";	
		Object[] params = {sYdGp,sSchCode};	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	/**
	 * YJK
     *
	 * @param String	: 동구분
	 *
     * @return JDTORecord 
     * @throws DAOException
     */
	public JDTORecord selectTCCoilSearch(String sYdGp, 
										    String sSchCode,
										    String sBay_Gp)throws DAOException{	  
		/*
		SELECT 
		    B.STOCK_ID,
		    B.CARUNLOAD_PUT_LOC,
		    A.WBOOK_ID,
		    A.YD_GP,
		    A.BAY_GP,
		    A.SCH_WORK_KIND,
		    A.SCH_WORK_LOC_DECISION_METHOD,
		    A.CRANE_WORD_PUT_LOC
		FROM TB_YM_WBOOK A,
		     TB_YM_STOCK B
		WHERE A.WBOOK_ID        = B.WBOOK_ID
		AND   A.YD_GP  			= :YD_GP
		AND   A.SCH_WORK_KIND 	= :SCH_WORK_KIND 
		AND   A.BAY_GP          = :BAY_GP
		AND   NOT EXISTS (SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)
		ORDER BY A.WBOOK_ID
		*/
		String  queryCode 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectTCCoilSearch";	
		Object[] params = {sYdGp,sSchCode,sBay_Gp};	
		
		return super.findByPrimaryKey(queryCode, params);
	} 
	
	/**
	 * YJK
     *
	 * @param String	: 동구분
	 *
     * @return JDTORecord 
     * @throws DAOException
     */
	public List getUpperLayerStockList(String sColGp, 
								       String sBedGp, 	
								       String sLyrCp)throws DAOException{	  
		/*
		Ver4.2--
		SELECT 
			B.SCH_ID,
			B.WBOOK_ID
		FROM TB_YM_STACKLAYER A,
		     TB_YM_SCH  B
		WHERE A.STACK_COL_GP  	= :STACK_COL_GP
		AND   A.STACK_BED_GP	= :STACK_BED_GP 
		AND   A.STACK_LAYER_GP	> :STACK_LAYER_GP 
		AND   A.STOCK_ID        = B.STOCK_ID
		ORDER BY B.SCH_ID
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getUpperLayerStockList";	
		Object[] params = {sColGp,sBedGp,sLyrCp};	
		
		return super.findList(queryCode, params);
	} 
	
   /**
     * YJK
     * 해당 BED에 적치가능한 MAX 단정보를 가져온다.
     * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getAbledMaxLayerInfo(String sStackColGp,
    									   String sStackBedGp) throws DAOException{
		
		/*
		Ver4.5--
		SELECT lpad(nvl(max(stack_layer_gp),0)+1, 2 ,'0') as max_layer_gp
		FROM tb_ym_stacklayer
		WHERE 	stack_col_gp 	= :col_gp
		AND   	stack_bed_gp 	= :bed_gp
		AND   	stock_id is not null
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getAbledMaxLayerInfo";	
		Object[] params = {sStackColGp,sStackBedGp};	
		return super.findByPrimaryKey(queryCode,params);
	}
		
	/**
     * A열연 SLAB야드 추가 (MCH)
     * 현재 위치의 적치 가능한 적치량을 가져온다.
     * @param String	: 저장품ID
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getlayerstatinfo(String Position) throws DAOException{
		
		/*
		SELECT  (A.ECOUNT - STACK_MAX_QNTY) AS ECOUNT  
		 	FROM(
		 		SELECT STACK_COL_GP, SUM(DECODE(STACK_LAYER_STAT,'L','1','0')) AS ECOUNT
		 				FROM TB_YM_STACKLAYER
		 				WHERE  STACK_COL_GP  =  ?
--		 				AND STACK_LAYER_ACTIVE_STAT ='O'
--		 				AND STOCK_ID IS NOT NULL
		 		GROUP BY STACK_COL_GP
		 		) A , 	TB_YM_EQUIP B
		 	WHERE A.STACK_COL_GP =  B.EQUIP_GP(+)
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getlayerstatinfo";	
		Object[] params = {Position};	
		return super.findByPrimaryKey(queryCode,params);
	}	
    
    /**
     * A열연 SLAB야드 추가 (MCH)
     * 공정에서 이송지시 편성되서 PT위에 실은 SLAB인지 편성않된 SLAB인지 읽어온다.
     * FRTOMOVE_WORD_NO이 없으면 상차지시편성 않된것
     * @param String	: 저장품ID
     * @return 
     * @throws DAOException
  			
    public JDTORecord getFrtomoveWordNo(String Slab_no) throws DAOException{
		
		/*
		SELECT FRTOMOVE_WORD_NO 
		FROM TB_YM_STOCK
		WHERE STOCK_ID = ?
		*/  
/*		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getFrtomoveWordNo";	
		Object[] params = {Slab_no};	
		return super.findByPrimaryKey(queryCode,params);
	}
*/    
    /**
     * A열연 SLAB야드 추가 (MCH)
     * 공정에서 이송지시 편성되서 PT위에 실은 SLAB인지 편성않된 SLAB인지 읽어온다.
     * FRTOMOVE_WORD_NO이 없으면 상차지시편성 않된것
     * @param String	: 저장품ID
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getCraneSchASlabInfo(String sEquipNo) throws DAOException{
		
		/*
		SELECT 
			   A.SCH_ID,					-- SCHEDULE ID
			   A.WBOOK_ID,				-- WBOOK ID
			   A.STOCK_ID,   			  	-- 저장품 ID
			   A.SCH_RULE_ID,	  		   	-- SCHEDULE RULE ID
			   A.YD_GP,		  	 		-- 야드 구분
			   A.BAY_GP,		  	 	  	-- 동구분
			   A.SCH_WORK_EQUIP_NO,	    -- SCHEDULE 작업설비 번호
			   A.SCH_WORK_STAT,		    -- SCHEDULE 상태
			   A.SCH_WPREFER,			    -- SCHEDULE 우선순위
			   A.SCH_WORK_KIND,		    -- SCHEDULE CODE
			   A.SCH_WORK_AID_YN,		    -- SCHEDULE 보조유무
			   A.SCH_WORK_GRIP_LOT_YN,    -- SCHEDULE GRIP LOT 유무
			   A.CRANE_WORD_UP_LOC,	    -- CRANE 작업지시 UP 위치
			   A.WBOOK_LOC_DECISION_METHOD, -- 작업예약위치 결정방법
			   A.WBOOK_SCH_ACT_DDTT,		  -- 스케쥴 작업지시일자
			   A.CRANE_WORD_PUT_LOC,		  -- CRANE 작업지시 PUT 위치
			   A.SCH_WORK_CAR_NO,			  -- SCHEDULE 작업차량번호
			   A.SCH_WDEMAND_TYPE			  -- SCHEDULE 작업요구형태 
		FROM TB_YM_SCH A, TB_YM_EQUIP B
		WHERE A.SCH_ID  = B.WBOOK_ID
		AND B.EQUIP_GP = ?
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchASlabInfo";	
		Object[] params = {sEquipNo};	
		return super.findByPrimaryKey(queryCode,params);
	}
    
    /**
     * 수조 탱크의 마지막 적치갯수 확인
     * @param String	: 저장품ID
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getCraneSchCoilCount(String PutAddress) throws DAOException{
		
		/*
		SELECT COUNT(STACK_LAYER_STAT) AS LCOUNT 
		FROM TB_YM_STACKLAYER
		WHERE STACK_COL_GP = ?
		AND STACK_LAYER_STAT='L'
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getCraneSchCoilCount";	
		Object[] params = {PutAddress};	
		return super.findByPrimaryKey(queryCode,params);
	}
	
      /**
	*	X,Y 물리적 좌표값을 기준으로 논리적위치정보를 가져온다.
	* @param String	: 적치열
	* @param String	: X 물리적좌표
	* @param String	: Y 물리적좌표
	* @return 
	* @throws DAOException
	*/
	public List getXYLogicalInfo(String sColGp, 
					             String sXaxis, 	
						      String sYaxis)throws DAOException{	  
		/*
		Ver4.2--
			SELECT 
				DISTINCT 
				STACK_COL_GP,
				STACK_BED_GP
			FROM tb_ym_stacklayer
			WHERE stack_col_gp like :colGp||'%'
			AND   STACK_LAYER_X_AXIS between :x_axis - 50 and :x_axis + 50
			AND   STACK_LAYER_Y_AXIS between :y_axis - 50 and :y_axis + 50
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getXYLogicalInfo";	
		Object[] params = {sColGp,sXaxis,sXaxis,sYaxis,sYaxis};	
		
		return super.findList(queryCode, params);
	} 
	
    /**
	*	X,Y 물리적 좌표값을 기준으로 논리적위치정보를 가져온다.
	* @param String	: 적치열
	* @param String	: X 물리적좌표
	* @param String	: Y 물리적좌표
	* @return 
	* @throws DAOException
	*/
	public List getBCoilXYLogicalInfo(String sColGp, 
					             	String sXaxis, 	
					             	String sYaxis)throws DAOException{	  
		/*
		Ver4.2--
			SELECT 
				*
			FROM tb_ym_stacklayer
			WHERE stack_col_gp like :colGp||'%'
			AND   STACK_LAYER_X_AXIS between :x_axis - 50 and :x_axis + 50
			AND   STACK_LAYER_Y_AXIS between :y_axis - 50 and :y_axis + 50
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getBCoilXYLogicalInfo";	
		Object[] params = {sColGp,sXaxis,sXaxis,sYaxis,sYaxis};	
		
		return super.findList(queryCode, params);
	} 
    
	  /**
	*	X,Y 물리적 좌표값을 기준으로 논리적위치정보를 가져온다.
	* @param String	: 적치열
	* @param String	: X 물리적좌표
	* @param String	: Y 물리적좌표
	* @return 
	* @throws DAOException
	*/
	public List getStackLayerInfo(String sColGp, 
					             	String sBedGp, 	
					             	String sLayerGp)throws DAOException{	  
		/*
		Ver4.2--
			SELECT * 
			FROM USRYMA.TB_YM_STACKLAYER
			WHERE STACK_COL_GP = :sColGp
			AND STACK_BED_GP IN (:sBedGp, LPAD(TO_NUMBER(:sBedGp)+1,2,'0'))
			AND STACK_LAYER_GP = '01'
		*/
		
		String  queryCode 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfo";	
		Object[] params = {sColGp,sBedGp,sBedGp};	
		
		return super.findList(queryCode, params);
	} 
	
    /**
     * YJK
     * 저장품 카드번호 Clear
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateStockCardNo(String sCardNo) throws DAOException{
		/* 
		Ver4.0--
			Update 	Tb_Ym_Stock 
			Set 		Car_Card_No 	= Null
			Where 	Car_Card_NO 	= :Card_No
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockCardNo";
		Object[] params = {sCardNo};	
		return super.updateData(queryCode,params);
    }

    /**
     * YJK
     * Pallet 위치정보 Clear(Tb_Ym_StackLayer)
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateStackLayerCardNo(String sCardNo) throws DAOException{
		/* 
		Ver4.0--
			Update Tb_Ym_StackLayer         
			Set 	Stock_Id 				= Null,
			    	Stack_Layer_Active_Stat 	= 'C',
			    	Stack_Layer_Stat 		= 'E'
			Where Stack_Col_Gp = (Select Stack_Col_Gp From Tb_Ym_StackCol Where Car_Card_No = :Card_No)
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStackLayerCardNo";
		Object[] params = {sCardNo};	
		return super.updateData(queryCode,params);
    }		
    
    /**
     * YJK
     * Pallet 위치정보 Clear(Tb_Ym_StackCol)
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateStackColCardNo_02(String sCardNo) throws DAOException{
		/* 
		Ver4.0--
			Update 	Tb_Ym_StackCol
			Set	 	Car_Card_No 	= Null
			Where 	Car_Card_No 	= :Card_No
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateStockLayerCardNo_02";
		Object[] params = {sCardNo};	
		return super.updateData(queryCode,params);
    }		
    
    
    /**
     * YJK
     * 현 작업지시 다음의 작업지시 정보를 작업지시 메세지 항목을 이용하여 L2에 송신한다.
     * 
     * @param String	: 설비번호
     *
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getNextCraneWorkInto(String sYdGp,
    								   	 String sBayGp,
    									 String sEquipNo) throws DAOException{
		/*
		SELECT ( SELECT CLASS2_NAME1
			        FROM TB_CM_CDCLASS2 
			        WHERE CLASS2_CD  	= A.SCH_WORK_KIND
			        AND   TYPE_CD 		= 'YM104'
			        AND   CLASS1_CD 	= '2'
			       ) AS SCH_WORK_KIND
		FROM    	TB_YM_SCH A
		WHERE   	YD_GP = :YD_GP
		AND     	BAY_GP = :BAY_GP
		AND     	SCH_WORK_EQUIP_NO = :SCH_WORK_EQUIP_NO
		AND     	SCH_WORK_STAT = 'S'
		ORDER BY SCH_WPREFER, SCH_ID
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getNextCraneWorkInto";	
		Object[] params = {sYdGp,sBayGp,sEquipNo};	
		return super.findByPrimaryKey(queryCode, params);
    }
    
    /**
     * YJK
     *
     * 차량재료  TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @return int
     * @throws DAOException
     */			
	public int insertCarMtlInfo(String sPutStackColGp,
								String sPutStackBedGp,
								String sPutStackLayerGp,
								String sStockId) throws DAOException
	{	
		/*
		Ver4.1--
		INSERT INTO TB_YD_CARFTMVMTL
		(
		 YD_CAR_SCH_ID,STL_NO,REGISTER,REG_DDTT,MODIFIER,MOD_DDTT,DEL_YN,
		 YD_STK_BED_NO,YD_STK_LYR_NO
		)
		(
		    SELECT YD_CAR_SCH_ID,
		            :STL_NO,
		            'YJK',SYSDATE,'YJK',SYSDATE,'N',
		            :YD_STK_BED_NO,
		            '0'||:YD_STK_LYR_NO
		    FROM TB_YM_STACKCOL A,
		         TB_YD_CARSCH B
		    WHERE A.STACK_COL_GP = :STACK_COL_GP
		      AND A.TRN_EQP_CD = B.TRN_EQP_CD
		      AND B.DEL_YN = 'N'
		) 
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertCarMtlInfo";	
		Object[] params = {sStockId,sPutStackBedGp,sPutStackLayerGp,sPutStackColGp};	
		return super.insertData(queryCode,params);
	}
	
    /**
     * YJK
     * 차량재료 Table 데이타를 Delete한다.  
     * 
     * @param String	: 차상위치
     * @param String	: 재료정보
     *
     * @return int 
     * @throws DAOException
     */		
	public int deleteCarMtlInfo(String sCarLoc,String sStockId) throws DAOException
	{
		/*
		DELETE TB_YD_CARFTMVMTL
		WHERE YD_CAR_SCH_ID = 
        (
	        SELECT YD_CAR_SCH_ID
	        FROM TB_YM_STACKCOL A, 
	             TB_YD_CARSCH B
	        WHERE A.STACK_COL_GP = :STACK_COL_GP
	          AND A.TRN_EQP_CD = B.TRN_EQP_CD
	          AND B.DEL_YN = 'N'
        )
		AND STL_NO = :STL_NO
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteCarMtlInfo";	
		Object[] params = {sCarLoc,sStockId};		
		return super.deleteData(queryCode, params);
	}
	
	
	
	   /**
     * YJK
     *
     * TB_CMS_FRTOMOVEWHIF  TABLE 에서 입고정보 등록
     *
     * @return int
     * @throws DAOException
     */			
	public int insertFrtoMoveWhif(String sStockId,
								String sLoc) throws DAOException
	{	
		/*
		insert into TB_CMS_FRTOMOVEWHIF @DL_CMSGT
		(
		IF_PID, 
		IF_SEND_DATE, 
		IF_STATUS, 
		IF_SEND_FLAG, 
		COIL_NO, 
		RECEIPT_DATE, 
		RECEIPT_TIME, 
		YD_GP, 
		STORE_LOC, 
		DEMANDER_CD, 
		DEMANDER_NAME, 
		REGISTER, 
		REG_DDTT
		)
		SELECT 
		'PT_FRTOMOVEWHIF_001'
		,sysdate
		,'C'
		,'N'
		,COIL_NO
		,to_char(sysdate,'YYYYMMDD')
		,to_char(sysdate,'HH24MISS')
		,YD_GP
		, ?
		,DEMANDER_CD
		,(SELECT CUST_KO_NAME FROM TB_SM_CUSTINFO WHERE CUST_CD =A.DEMANDER_CD)
		,'ydsystem'
		,sysdate
		FROM USRPTA.TB_PT_COILCOMM A
		WHERE COIL_NO= ?
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertFrtoMoveWhif_PIDEV";	
		Object[] params = {sLoc , sStockId};	
		return super.insertData(queryCode,params);
	}
	
	
	   /**
	  * YJK
	  * PIDEV
	  * TB_CMS_FRTOMOVEWHIF  TABLE 에서 입고정보 등록
	  *
	  * @return int
	  * @throws DAOException
	  */			
	public int insertFrtoMoveWhifPI(String sStockId,
								String sLoc) throws DAOException
	{	
		/*
		insert into TB_CMS_FRTOMOVEWHIF @DL_CMSGT
		(
		IF_PID, 
		IF_SEND_DATE, 
		IF_STATUS, 
		IF_SEND_FLAG, 
		COIL_NO, 
		RECEIPT_DATE, 
		RECEIPT_TIME, 
		YD_GP, 
		STORE_LOC, 
		DEMANDER_CD, 
		DEMANDER_NAME, 
		REGISTER, 
		REG_DDTT
		)
		SELECT 
		'PT_FRTOMOVEWHIF_001'
		,sysdate
		,'C'
		,'N'
		,COIL_NO
		,to_char(sysdate,'YYYYMMDD')
		,to_char(sysdate,'HH24MISS')
		,YD_GP
		, ?
		,DEMANDER_CD
		,(SELECT CUST_KO_NAME FROM TB_SM_CUSTINFO WHERE CUST_CD =A.DEMANDER_CD)
		,'ydsystem'
		,sysdate
		FROM USRPTA.TB_PT_COILCOMM A
		WHERE COIL_NO= ?
		*/
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.insertFrtoMoveWhif_PIDEV";	
		Object[] params = {sLoc , sStockId};	
		return super.insertData(queryCode,params);
	}
		
	
	   /**
     * YJK
	 * 작업예약 동구분 항목을 수정한다.
	 *	
     * @param String	: 작업예약ID
     * @param String	: 동구분
     *
     * @return int
     * @throws DAOException
     */	
    public int updateputlocWbookId(String sSchId) throws DAOException{
		/* 
		UPDATE TB_YM_WBOOK
		SET
		SCH_WORK_LOC_DECISION_METHOD='S'
		,CRANE_WORD_PUT_LOC =null   
		WHERE wbook_id =(select wbook_id from USRYMA.TB_YM_SCH	where sch_id =:sch_id)           
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateputlocWbookId";
		Object[] params = {sSchId};	
		return super.updateData(queryCode,params);
    }
	
	/**
     * YJK
     * MAX 단으로 권하위치 변경 작업
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStackLayerMaxInfo(String sStack_col_gp, String sStack_bed_gp) throws DAOException{
		
		/* 
			select LPAD(NVL(max(TO_NUMBER(STACK_LAYER_GP)),0)+1,2,'0') AS STACK_LAYER_GP
			  from USRYMA.TB_YM_STACKLAYER a
			where STACK_COL_GP = :v_STACK_COL_GP
			  and STACK_BED_GP = :v_STACK_BED_GP
			  and STACK_LAYER_STAT='L'
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerMaxInfo";	
		Object[] params = {sStack_col_gp,sStack_bed_gp};	
		return super.findByPrimaryKey(queryCode,params);
	}
    
	/**
     * YJK
     * 저장위치 적치가능 여부 체크
	 * 
     * @param String	: 저장품ID
     * 
     * @return 
     * @throws DAOException
     */			
    public JDTORecord getStackLayerChkInfo(String sStack_col_gp, String sStack_bed_gp, String sStack_layer_gp) throws DAOException{
		
		/* 
			select *
			 from USRYMA.TB_YM_STACKLAYER
			where STACK_COL_GP= ?
			 and STACK_BED_GP = ?
			 and STACK_LAYER_GP = ?
			 and STACK_LAYER_ACTIVE_STAT<>'X'
		*/  
		String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerChkInfo";	
		Object[] params = {sStack_col_gp,sStack_bed_gp,sStack_layer_gp};	
		return super.findByPrimaryKey(queryCode,params);
	}
    
    
    /**
     * YJK
     * 적치단 위치정보 상태를 변경한다.
     *
     * @param String	: 적치열
     * @param String	: 적치대
     * @param String	: 적치단      
     * @param String	: 저장품ID      
     * @param String	: 적치상태      
     * 
     * @param 
     * @return 
     * @throws DAOException
     */		
    public int updateTrakingLayerReset(String sStockId ,String sUp_Position) throws DAOException{
		/* 
		Ver1.0--
		 UPDATE  USRYMA.TB_YM_STACKLAYER
			SET STOCK_ID = NULL
			 , STACK_LAYER_STAT='E'
			 , MODIFIER = 'TRReset'
			 , MOD_DDTT = SYSDATE
			WHERE SUBSTR(STACK_COL_GP,3,2) NOT BETWEEN '00'AND '99'
			 AND STACK_COL_GP LIKE '1%'
			 AND SUBSTR(STACK_COL_GP,3,2) NOT IN ('PT','TR','XX','CR','TC')
			 AND STOCK_ID = ?
		*/
	 	String  queryCode = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateTrakingLayerReset";
		Object[] params = {sStockId,sUp_Position};	
		return super.updateData(queryCode,params);
    }
}


