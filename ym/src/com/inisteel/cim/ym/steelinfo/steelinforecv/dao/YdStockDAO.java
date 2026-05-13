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
package com.inisteel.cim.ym.steelinfo.steelinforecv.dao;

import java.sql.Types;
import java.util.List;

import jspeed.base.log.LogLevel;
import jspeed.base.record.JDTOException;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.record.JDTORecordSet;
import jspeed.base.util.StringHelper;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.dao.DBAssistantDAO;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.bcommon.dao.YmCommDAO;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;

public class YdStockDAO extends CommonDAO {
    
	private YmCommDAO commDao = new YmCommDAO();
	
    /**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Insert한다.
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */		
	public int createData(List createData) {
		/*
		INSERT INTO TB_YM_STOCK(
		STOCK_ID,
		WBOOK_ID,
		STOCK_ITEM,
		STOCK_STAT,
		STOCK_COOL_STAT,
		STOCK_COOL_START_DDTT,
		STOCK_COOL_START_TEMP,
		STACK_LOT_NO,
		STOCK_MOVE_TERM,
		FRTOMOVE_EQUIP_GP,
		FRTOMOVE_EQUIP_BED_GP,
		FRTOMOVE_EQUIP_LAYER_GP,
		CHARGE_LOT_NO,
		FRTOMOVE_WORD_NO,
		TRANS_WORD_NO,
		SCARFING_SUPPLY_YN,
		CAR_CARD_NO,
		SHEAR_SUPPLY_SEQ,
		CTS_RELAY_YN,
		CTS_RELAY_BAY,
		CTS_RELAY_SADDLE,
		CARUNLOAD_YD,
		CARUNLOAD_BAY,
		REGISTER,
		REG_DDTT,
		MODIFIER,
		MOD_DDTT,
		DEL_YN )
		VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,SYSDATE,?,?,'N')
		*/
		String  queryCode = "ym.steelinfo.steelinforecv.YdStockDAO.insertData";
		return super.insertData(queryCode, createData.toArray());
	}
	
	public int AslabcreateData(List createData) {
		/*
		MERGE INTO TB_YM_STOCK A                                -- 저장품
		    USING (SELECT ? 	   AS STOCK_ID                  --저장품 ID  
		                , ? 		 AS STOCK_ITEM              --저장품 품목  
		                , ? 		 AS STACK_LOT_NO            -- 산적 LOT 번호  
		                , ? 		 AS STOCK_MOVE_TERM         -- 저장품 이동 조건 
		                , ? 		 AS SCARFING_SUPPLY_YN      -- SCARFING 보급 유무 
		                , ? 		 AS REGISTER                -- 등록자       
		                ,Sysdate AS REG_DDTT					-- 등록일시
		             FROM DUAL) B
		      ON (A.STOCK_ID     = B.STOCK_ID)
		WHEN MATCHED THEN
		 UPDATE
			SET STOCK_ITEM          =    B.STOCK_ITEM         
				,STACK_LOT_NO       =    B.STACK_LOT_NO       
				,STOCK_MOVE_TERM    =    B.STOCK_MOVE_TERM    
				,SCARFING_SUPPLY_YN =    B.SCARFING_SUPPLY_YN 
				,REGISTER           =    B.REGISTER
				,MOD_DDTT      	    =    B.REG_DDTT
		where a.stock_id = b.stock_id        
		WHEN NOT MATCHED THEN        
		 INSERT 
		        (                                                                                 
				STOCK_ID                       -- 저장품 ID          																															
				,STOCK_ITEM                    -- 저장품 품목        																															
				,STACK_LOT_NO                  -- 산적 LOT 번호      																															   					
				,STOCK_MOVE_TERM               -- 저장품 이동 조건  																															
				,SCARFING_SUPPLY_YN            -- SCARFING 보급 유무 																															 
				,REGISTER                      -- 등록자             																															
				,REG_DDTT
		        )                                                                                 
		 VALUES (                                                                                 
				B.STOCK_ID                     -- 저장품 ID          
				, B.STOCK_ITEM                 -- 저장품 품목        
				, B.STACK_LOT_NO               -- 산적 LOT 번호         
				, B.STOCK_MOVE_TERM            -- 저장품 이동 조건    
				, B.SCARFING_SUPPLY_YN         -- SCARFING 보급 유무  
				, B.REGISTER                   -- 등록자            
				, B.REG_DDTT
		        )
		*/
		String  queryCode = "ym.steelinfo.steelinforecv.YdStockDAO.AslabcreateData";
		return super.insertData(queryCode, createData.toArray());
	}
	
	/**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Insert한다.
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */		
	public int updateData(List editData) {
		String  queryCode = "ym.steelinfo.steelinforecv.YdStockDAO.updateData";
		return super.updateData(queryCode, editData.toArray());
	}
	
    public boolean isExistPrimaryKey(Object listPK) throws DAOException {
	    String queryCode = "ym.steelinfo.steelinforecv.YdStockDAO.getByPrimaryKey";
	    JDTORecord record = null;
	    if(listPK instanceof List) {
	        record = super.findByPrimaryKey(queryCode, ((List)listPK).toArray());   
	    }else if(listPK instanceof String) {
	        record = super.findByPrimaryKey(queryCode, (String)listPK);
	    }		 
		return record != null && record.size() > 0 ? true : false;
	}
    
	/**
     * List에 입력된 순서대로 Query값에 Matching하여 데이타를 Update한다. 
     * @param listData 입력할값
     * @return int 
     * @throws DAOException
     */
	public JDTORecord requestFind(String queryCode) throws DAOException{
		return super.find(queryCode);          
    }
	
	public JDTORecord requestgetData(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);          
    }

	public int requestinsertData(String queryCode, Object[] objs) throws DAOException{	
		return super.insertData(queryCode,objs);
	}

   	public int requestupdateData(String queryCode, Object[] objs) throws DAOException{	   	 	
   		return super.updateData(queryCode,objs);
   	}
   	
	public void selectCoilInfo(String coilNo, String isMat) {
	}
	

	/**
     * 해당 재료번호에 해당하는 상세내역을 가져와 JDTORecord로 데이터를 리턴한다.
     * 
     * @param 재료번호
	 * @param 쿼리코드
     * @return
     * @throws 
     */
	public JDTORecord getData(String queryCode,Object[] objs) throws DAOException{
		return super.findByPrimaryKey(queryCode, objs);          
    }
	

	/**
     * 해당 재료번호에 해당하는 재료의 변경이력정보를 가져와 LIST로 반환한다.
     * 
     * @param 야드구분, 동구분, 스판구분, 소재구분
     * @return List
     * @throws 
     */
	public List getListData(String queryCode,Object[] objs) throws DAOException{
		return super.findList(queryCode, objs);	
    }
    
    /**
	 * 콘베이어 정보를 생성한다.
	 * {call YM_SP_CREATCONVEYORINFO_01(?,?,?,?)}
	 *
     * @param 열구분, 저장품ID, User_Id
     * @return List
     * @throws 
     */
	public JDTORecord insertConveyorInfoSp(String sStackColGp,
										   String sGoodsNo,
										   String sUserId)throws DAOException{
		
		Object[][] obj = new Object[4][2];
		obj[0][0] = "IN";
		obj[0][1] = sStackColGp;
		
		obj[1][0] = "IN";
		obj[1][1] = sGoodsNo;
		
		obj[2][0] = "IN";
		obj[2][1] = sUserId;
		
		obj[3][0] = "OUT";
		obj[3][1] = new Integer(Types.VARCHAR);
		
		String sQueryId = "ym.steelinfo.steelinforecv.dao.YdStockDAO.insertConveyorInfoSp";
		return super.execute(sQueryId, obj);
    }
    
    /**
     * YJK
     *
     * 출하 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 운송 작업지시 일자
     * @param String	: 운송 작업지시 순번
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getDmStockInfo(String sTransWordDate,
							   String sTransWordSeq) throws DAOException
	{	
		/*
		VER2.0--
		SELECT 
		    a.trans_word_date  as trans,    -- 운송지시일자
		    a.trans_word_seqno as seq,   -- 운송지시순번
		    a.card_no,            -- 차량카드번호
		    b.goods_no            -- 제품번호  
		FROM usrdma.tb_dm_transwordcomm a,
		     usrdma.tb_dm_transwordgoods b
		WHERE a.trans_word_date   = :trans_word_date
		AND   a.trans_word_seqno  = :trans_word_seqno
		AND   a.trans_word_date   = b.trans_word_date
		AND   a.trans_word_seqno  = b.trans_word_seqno
		*/

		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getDmStockInfo_PIDEV";	
		Object[] params = {sTransWordDate,sTransWordSeq};	
		return super.findList(queryCode, params);
	}
	
	/**
     * YJK
     *
     * 조업 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 공장구분
     * @param String	: 공정구분
     * @param String	: 작업지시 단위명
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getPoPmStockInfo(String sPlantGp,
							     String sProcGp,
							     String sWordUnitName) throws DAOException
	{	
		/*
		SELECT 
		    a.coil_no as goods_no,    -- 코일번호
		    a.step_no,    -- 차수
		    a.work_stat,  -- 작업상태(<> 0)
		    a.word_unit_name, -- 작업지시 단위 명
		    a.plant_gp, -- 공장구분(A-A열연,B-B열연)
		    a.proc_gp -- 공정구분(K-SPM,H-HFL)
		FROM USRPOA.TB_PO_SHEARORDPRIOR a,
		    (
		    SELECT coil_no, MAX(step_no) as step_no
		    FROM USRPOA.TB_PO_SHEARORDPRIOR
		    WHERE plant_gp       = :plant_gp
		    AND   proc_gp        = :proc_gp
		    AND   word_unit_name = :word_unit_name
		    AND   work_stat NOT IN ('0','1')
		    GROUP BY coil_no
		    )b
		WHERE a.coil_no = b.coil_no
		AND   a.step_no = b.step_no       
		AND   a.plant_gp       = :plant_gp
		AND   a.proc_gp        = :proc_gp
		AND   a.word_unit_name = :word_unit_name
		AND   a.work_stat NOT IN ('0','1')
		*/

		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getPoPmStockInfo";	
		Object[] params = {sPlantGp,sProcGp,sWordUnitName,sPlantGp,sProcGp,sWordUnitName};	
		return super.findList(queryCode, params);
	}
	
	/**
     * YJK
     * 상차지시 출하 TABLE , 공정 TABLE에서
     * 저장품 정보를 가져온다.
     *
     * @param String	: 이송상차일자
     * @param String	: 이송상차순번
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getPmStockInfo_01(String sFrtomoveWordDate,
							   	  String sFrtomoveWordSeqno) throws DAOException
	{	
		/*
		VER2.0--
		SELECT a.coil_no as goods_no, 
		       c.card_no, 
		       a.frtomove_word_date  as trans,
		       a.frtomove_word_seqno as seq
		FROM USRPOA.TB_PO_COILFRTOMOVE a,
		    (
		    SELECT coil_no, MAX(step_no) as step_no
		    FROM USRPOA.TB_PO_COILFRTOMOVE
		    WHERE frtomove_word_date  = :frtomove_word_date
		    AND   frtomove_word_seqno = :frtomove_word_seqno 
		    GROUP BY coil_no
		    )b,
		    USRDMA.TB_DM_COILFRTOMOVEWORDCOMM c
		WHERE a.coil_no = b.coil_no
		AND   a.step_no = b.step_no    
		AND   a.frtomove_word_date  = c.frtomove_word_date
		AND   a.frtomove_word_seqno = c.frtomove_word_seqno
		AND   a.frtomove_word_date  = :frtomove_word_date
		AND   a.frtomove_word_seqno = :frtomove_word_seqno
		*/

		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getPmStockInfo_01";	
		Object[] params = {sFrtomoveWordDate,sFrtomoveWordSeqno,sFrtomoveWordDate,sFrtomoveWordSeqno};	
		return super.findList(queryCode, params);
	}
	
	/**
     * YJK
     * 
     * 이송지시 공정 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 이송작업지시일자
     * @param String	: 이송작업지시순번
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getPmStockInfo_02(String sFrtomoveWordDate,
							      String sFrtomoveWordSeqno) throws DAOException
	{	
		/*
		VER2.0--
		SELECT a.coil_no as goods_no, 
		   a.frtomove_wreq_date  as trans,
		   a.frtomove_wreq_seqno as seq
		FROM USRPOA.TB_PO_COILFRTOMOVE a,
			(
			SELECT coil_no, MAX(step_no) as step_no
			FROM USRPOA.TB_PO_COILFRTOMOVE
			WHERE frtomove_wreq_date  = :frtomove_wreq_date
			AND   frtomove_wreq_seqno = :frtomove_wreq_seqno 
			GROUP BY coil_no
			)b
		WHERE a.coil_no = b.coil_no
		AND   a.step_no = b.step_no    
		AND   a.frtomove_wreq_date  = :frtomove_wreq_date
		AND   a.frtomove_wreq_seqno = :frtomove_wreq_seqno
		*/

		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getPmStockInfo_02";	
		Object[] params = {sFrtomoveWordDate,sFrtomoveWordSeqno,sFrtomoveWordDate,sFrtomoveWordSeqno};	
		return super.findList(queryCode, params);
	}
	
	/**
     * YJK 2009.07 일관제철수정사항 반
     * 이송지시 공정 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 이송작업지시일자
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getPmStockInfo_03(String sFrtomoveWordDate) throws DAOException
	{	
		/*
		VER2.0--
		일관제철 슬라브 이송지시 쿼리 공유.
		*/
		String  queryCode = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE";	
		Object[] params = {sFrtomoveWordDate};	
		return super.findList(queryCode, params);
	}
	/**
     * YJK 2009.07 일관제철수정사항 반
     * 이송지시 공정 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 이송작업지시일자
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getPmStockInfo_04(String sFrtomoveWordDate) throws DAOException
	{	
		/*
		VER2.0--
		일관제철 코일  이송지시 쿼리 공유.
		*/
		String  queryCode = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_COIL";	
		Object[] params = {sFrtomoveWordDate};	
		return super.findList(queryCode, params);
	}
	/**
     * YJK 2009.07 일관제철수정사항 반
     * 이송지시 공정 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 이송작업지시일자
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getPmStockInfo_05(String sFrtomoveWordDate) throws DAOException
	{	
		/*
		VER2.0--
		일관제철 코일임가공  이송지시 쿼리 공유.
		*/
		String  queryCode = "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockFRTOMOVE_WORD_DATE_RENTCOIL";	
		Object[] params = {sFrtomoveWordDate};	
		return super.findList(queryCode, params);
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
	public int insertStockTransInfo(String sStockId,
									String sStockItem,
									String sStockMoveTerm) throws DAOException
	{	
		/*
		Ver4.1--
		INSERT INTO TB_YM_STOCK
		(
		 stock_id, stock_item, stock_move_term,
		 register, reg_ddtt, del_yn
		)
		VALUES
		(
		 :stock_id, :stock_item, :stock_move_term,
		 'SYSTEM', sysdate, 'N'
		)      
		*/
 		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.insertStockTransInfo";	
		Object[] params = {sStockId,sStockItem,sStockMoveTerm};	
		return super.insertData(queryCode,params);
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
     * 2007-03-28 A열연 SLAB야드 추가 (MCH) 강제 출발시 저장품 TABLE UPDATE 같이 사용
     */			
	public int updateStockTransInfo(String sStockId,
									String sStockMoveTerm) throws DAOException
	{	
		/*
		Ver4.1--
		UPDATE tb_ym_stock
		SET	
			stock_move_term = :stock_move_term,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE stock_id = :stock_id
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockTransInfo";	
		Object[] params = {sStockMoveTerm,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 슬라브공통Table에  SCRF_MCNO_GP Scarfer호기구분  정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * 
     * @return int
     * @throws DAOException
     */			
	public int updatePtMComScrfMcnoGpInfo(String sStockId) throws DAOException
	{	
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updatePtMComScrfMcnoGpInfo";	
		Object[] params = {sStockId};	
		return super.updateData(queryCode,params);
	}
	public int updatePtSComScrfMcnoGpInfo(String sStockId) throws DAOException
	{	
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updatePtSComScrfMcnoGpInfo";	
		Object[] params = {sStockId};	
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
     * 2007-03-28 A열연 SLAB야드 추가 (MCH) 강제 출발시 저장품 TABLE UPDATE 같이 사용
     */			
	public int updateCoilCommTransInfo(String sStockId) throws DAOException
	{	
		/*
		Ver4.1--
		UPDATE TB_PM_COILCOMM 
        SET HYSCO_TRANS_GP = 'T'
        WHERE coil_no = :coil_no
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateCoilCommTransInfo";	
		Object[] params = {sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo_06(String sStockId,
									   String sStockItem,
									   String sStockMoveTerm) throws DAOException
	{	
		/*
		Ver4.1--
		UPDATE tb_ym_stock
		SET	
			stock_item		= :stock_item,
			stock_move_term = :stock_move_term,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE stock_id = :stock_id
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockTransInfo_06";	
		Object[] params = {sStockItem,sStockMoveTerm,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 Del_Yn 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: Del_Yn
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockDelYnInfo(String sStockId,
									String sDelYn) throws DAOException
	{	
		/*
		Ver4.1--
		UPDATE tb_ym_stock
		SET	
			del_yn     = :del_yn,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE stock_id = :stock_id
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockDelYnInfo";	
		Object[] params = {sDelYn,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 Del_Yn 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: Del_Yn
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockKeepStockStlYnInfo(String sStockId,
											 String sDelYn) throws DAOException
	{	
		/*
		Ver4.1--
		UPDATE tb_ym_stock
		SET	
			keepstock_stl_yn= :del_yn,
			modifier   		= 'SYSTEM',
		 	mod_ddtt   		= sysdate           
		WHERE stock_id = :stock_id
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockKeepStockStlYnInfo";	
		Object[] params = {sDelYn,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 차량카드번호
     * @param String	: 운송지시일자
     * @param String	: 운송지시일련번호
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo_01(String sStockId,
									   String sCarCardNo,
		            				   String sTransWordDate,
		            				   String sTransWordSeqno,
									   String sStockMoveTerm) throws DAOException
	{	
		 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockTransInfo_01";	
		Object[] params = {sCarCardNo,sTransWordDate+sTransWordSeqno,sStockMoveTerm,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 차량카드번호
     * @param String	: 운송지시일자
     * @param String	: 운송지시일련번호
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo_10(String sStockId,
									   String sItem,
									   String sCarCardNo,
		            				   String sTransWordDate,
		            				   String sTransWordSeqno,
									   String sStockMoveTerm) throws DAOException
	{	
		 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockTransInfo_10";	
		Object[] params = {sCarCardNo,sItem,sTransWordDate+sTransWordSeqno,sStockMoveTerm,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 운송지시일자
     * @param String	: 운송지시일련번호
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo_05(String sStockId,
									   String sTransWordDate,
		            				   String sTransWordSeqno,
									   String sStockMoveTerm) throws DAOException
	{	
		/*
		VER 060202--
		UPDATE tb_ym_stock
		SET	frtomove_word_no 	= :frtomove_word_no,
			stock_move_term 	= :stock_move_term,
			modifier   = 'SYSTEM',
		 	mod_ddtt   = sysdate           
		WHERE stock_id = :stock_id
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockTransInfo_05";	
		Object[] params = {sTransWordDate+sTransWordSeqno,sStockMoveTerm,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 이동관련 정보를 UPDATE한다.
     *
     * @param String	: 저장품ID
     * @param String	: 작업예약ID
     * @param String	: 차량카드번호
     * @param String	: 운송지시일자
     * @param String	: 운송지시일련번호
     * @param String	: 저장품이동조건
     *
     * @return int
     * @throws DAOException
     */			
	public int updateStockTransInfo_02(String sStockId,
									   String sWbookId,	
		            				   String sCarCardNo,
		            				   String sFrtomoveWordNo,
		            				   String sTransWordNo,
									   String sStockMoveTerm) throws DAOException
	{	
		 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateStockTransInfo_02";	
		Object[] params = {sWbookId,sCarCardNo,sFrtomoveWordNo,sTransWordNo,sStockMoveTerm,sStockId};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 이송작업지시일자
     * @param String	: 이송작업지시순번
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getStockList_01(String sFrtomoveWordDate,
							    String sFrtomoveWordSeqno) throws DAOException
	{	
		/*
		VER 060202--
		SELECT 
		    a.stock_id, 
		    a.stock_move_term,
		    a.wbook_id,
		    substr(b.stack_col_gp,0,1) as yd_gp,
		    substr(b.stack_col_gp,2,1) as bay_gp,
		    b.stack_col_gp,
		    b.stack_bed_gp,
		    b.stack_layer_gp
		FROM tb_ym_stock a,
		     tb_ym_stacklayer b
		WHERE a.frtomove_word_no  = :frtomove_word_no
		AND   a.stock_id          = b.stock_id
		AND   b.stack_layer_stat in ('L','S','U')
		ORDER BY b.stack_col_gp,
				 b.stack_layer_gp DESC
		*/

		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getStockList_01";	
		Object[] params = {sFrtomoveWordDate+sFrtomoveWordSeqno};	
		return super.findList(queryCode, params);
	}				
	
	/**
     * YJK
     *
     * 저장품 TABLE 에서 저장품 정보를 가져온다.
     *
     * @param String	: 운송작업지시일자
     * @param String	: 운송작업지시순번
     *
     * @return List 저장품정보
     * @throws DAOException
     */			
	public List getStockList_02(String sFrtomoveWordDate,
							    String sFrtomoveWordSeqno) throws DAOException
	{	
		 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getStockList_02";	
		Object[] params = {sFrtomoveWordDate+sFrtomoveWordSeqno};	
		return super.findList(queryCode, params);
	}												   
	
	/**
     * YJK
     *	조업 정정지시 실적 Table 참조.
	 *  입고보류 SCRAP원인구분(receipt_hold_scrap_cause_gp) 항목을 참조
	 *
     * @param String	: 저장품ID
     *
     * @return JDTORecord 저장품정보
     * @throws DAOException
     */			
	public JDTORecord getPoWrsltInfo(String sCoilNo) throws DAOException
	{	
		/*
		VER4.1--
		SELECT  a.receipt_hold_scrap_cause_gp as gbn
		FROM tb_po_coilshearord_wrslt a,
		     (
		      SELECT coil_no, MAX(step_no) as step_no
		      FROM tb_po_coilshearord_wrslt
		      WHERE coil_no = :coil_no
		      GROUP BY coil_no
		     )b
		WHERE a.coil_no = b.coil_no
		AND   a.step_no = b.step_no     
		AND   a.coil_no = :coil_no
		*/

		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.getPoWrsltInfo";	
		Object[] params = {sCoilNo,sCoilNo};	
		return super.findByPrimaryKey(queryCode, params);
	}		
	
	/**
     * YJK
     *
     * Scarfing 실적을 수신하면 조업의 Scarfing 실적 Table에 Update 한다.
     *
     * @param String	: 스카핑방법
     * @param String	: 저장품ID
     * @param String	: 두께
     * @param String	: 폭
     * @param String	: 길이
     * @param String	: 중량
     * @param String	: 온도
     * @param String	: 패턴1
     * @param String	: 패턴2
     * @param String	: 패턴3
     * @param String	: 패턴4
     * @param String	: 패턴5
     * @param String	: 패턴6
     * @param String	: 깊이
     * @param String	: 시작일자
     * @param String	: 시작일시
     * @param String	: 종료일자
     * @param String	: 종료일시
     *
     * @return int
     * @throws DAOException
     */			
	public int insertScarfingWrsltInfo(String sGbn,
									   String sSlabNo,
									   String sThick,
									   String sWidth,
									   String sLength,
									   String sWeight,
									   String SlabTemp,			
									   String TopPattern,		
									   String BottomPattern,	
									   String LeftPattern,		
									   String RightPattern,		
									   String TCornerPattern,	
									   String BCornerPattern,	
									   String sDepth, 			
									   String sSdate1,
									   String sSdate2,
									   String sEdate1,
									   String sEdate2) throws DAOException
	{	
		/*
		Ver051228--
	INSERT INTO TB_PO_SCARFINGWRSLT     
		(
		    SLAB_NO,
		    STEP_NO,
		    SCARFING_METHOD,
		    SLAB_T,
		    SLAB_W,
		    SLAB_LEN,
		    SLAB_WT,
		    SLAB_TEMP,
		    SCARFING_TOP,
		    SCARFING_BOTTOM,
		    SCARFING_LEFT,
		    SCARFING_RIGHT,
		    SCARFING_TOP_CORNER,
		    SCARFING_BOTTOM_CORNER,
		    SCARFING_DEPTH,
		    SCARFING_START_DATE,
		    SCARFING_START_TIME,
		    SCARFING_END_DATE,
		    SCARFING_END_TIME,
		    REGISTER,
		    REG_DDTT,
		    SCARFING_PATTERN
		)
		SELECT 
		    :slab_no,
		    A.STEP_NO, 
		    :scarfing_method,
		    :slab_t,
		    :slab_w,
		    :slab_len,
		    :slab_wt,
		    :slab_temp,
		    :scarfing_top,
		    :scarfing_bottom,
		    :scarfing_left,
		    :scarfing_right,
		    :scarfing_top_corner,
		    :scarfing_bottom_corner,
		    :scarfing_depth,
		    :scarfing_start_date,
		    :scarfing_start_time,
		    :scarfing_end_date,
		    :scarfing_end_time,
		    'SYSTEM',
		    sysdate,
		    B.SCARFING_PATTERN
	    FROM (SELECT NVL(MAX(STEP_NO)+1,1) as STEP_NO FROM TB_PO_SCARFINGWRSLT WHERE SLAB_NO = :slab_no) A,
	         TB_PM_SLABCOMM B
		WHERE B.SLAB_NO = :slab_no 
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.insertScarfingWrsltInfo";	
		Object[] params = {sSlabNo,sGbn,sThick,sWidth,sLength,sWeight,SlabTemp,
						   TopPattern,BottomPattern,LeftPattern,RightPattern,TCornerPattern,BCornerPattern,
						   sDepth,sSdate1,sSdate2,sEdate1,sEdate2,sSlabNo,sSlabNo};	
		return super.updateData(queryCode,params);
	}
	
	/**
     * YJK
     *
     * Scarfing 실적을 수신하면 조업의 Scarfing 실적 Table에 Update 한다.
     *
     * @param String	: 스카핑방법
     * @param String	: 저장품ID
     * @param String	: 두께
     * @param String	: 폭
     * @param String	: 길이
     * @param String	: 중량
     * @param String	: 온도
     * @param String	: 패턴1
     * @param String	: 패턴2
     * @param String	: 패턴3
     * @param String	: 패턴4
     * @param String	: 패턴5
     * @param String	: 패턴6
     * @param String	: 깊이
     * @param String	: 시작일자
     * @param String	: 시작일시
     * @param String	: 종료일자
     * @param String	: 종료일시
     *
     * @return int
     * @throws DAOException
     */			
	public int insertScarfingWrsltInfoNEW(String sGbn,
									   String sSlabNo,
									   String sThick,
									   String sWidth,
									   String sLength,
									   String sWeight,
									   String SlabTemp,			
									   String TopPattern,		
									   String BottomPattern,	
									   String LeftPattern,		
									   String RightPattern,		
									   String TCornerPattern,	
									   String BCornerPattern,	
									   String sDepth, 
									   String sSpeed, 
									   String sSdate1,
									   String sSdate2,
									   String sEdate1,
									   String sEdate2) throws DAOException
	{	
		/*
		Ver051228--
	INSERT INTO TB_PO_SCARFINGWRSLT     
		(
		    SLAB_NO,
		    STEP_NO,
		    SCARFING_METHOD,
		    SLAB_T,
		    SLAB_W,
		    SLAB_LEN,
		    SLAB_WT,
		    SLAB_TEMP,
		    SCARFING_TOP,
		    SCARFING_BOTTOM,
		    SCARFING_LEFT,
		    SCARFING_RIGHT,
		    SCARFING_TOP_CORNER,
		    SCARFING_BOTTOM_CORNER,
		    SCARFING_DEPTH,
            CC_FULL_SCRF_PCH_ROLL_SPD,
		    SCARFING_START_DATE,
		    SCARFING_START_TIME,
		    SCARFING_END_DATE,
		    SCARFING_END_TIME,
		    REGISTER,
		    REG_DDTT
		  --  WO_MSLAB_RPR_MTD
		)
		SELECT 
		    :slab_no,
		    A.STEP_NO, 
		    :scarfing_method,
		    :slab_t,
		    :slab_w,
		    :slab_len,
		    :slab_wt,
		    :slab_temp,
		    :scarfing_top,
		    :scarfing_bottom,
		    :scarfing_left,
		    :scarfing_right,
		    :scarfing_top_corner,
		    :scarfing_bottom_corner,
		    :scarfing_depth,
            :scarfing_speed,
		    :scarfing_start_date,
		    :scarfing_start_time,
		    :scarfing_end_date,
		    :scarfing_end_time,
		    'SYSTEM',
		    sysdate
		   -- B.WO_MSLAB_RPR_MTD
	       FROM (SELECT NVL(MAX(STEP_NO)+1,1) as STEP_NO FROM TB_PO_SCARFINGWRSLT WHERE SLAB_NO = :slab_no) A,
	            TB_PT_SLABCOMM B
	       WHERE B.SLAB_NO = :slab_no 

		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.insertScarfingWrsltInfoNEW";	
		Object[] params = {sSlabNo,sGbn,sThick,sWidth,sLength,sWeight,SlabTemp,
						   TopPattern,BottomPattern,LeftPattern,RightPattern,TCornerPattern,BCornerPattern,
						   sDepth,sSpeed,sSdate1,sSdate2,sEdate1,sEdate2,sSlabNo,sSlabNo};	
		return super.updateData(queryCode,params);
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
	public int updateSlabCommonScarfingInfo(String sStockId) throws DAOException
	{	
		/*
		Ver4.0--
		UPDATE USRPMA.TB_PM_SLABCOMM
		SET(  
		    curr_prog_cd_reg_pgm,   -- 현재진도코드 PGM
		    curr_prog_reg_ddtt,     -- 현재진도코드등록일시
		    curr_prog_cd,           -- 현재진도코드
		    befo_prog_cd_reg_pgm,   -- 전 진도코드 PGM
		    befo_prog_reg_ddtt,     -- 전 진도코드등록일시
		    befo_prog_cd,           -- 전 진도코드
		    modifier,               -- 수정자
		    mod_ddtt                -- 수정일시
		   )=
		   (
		    select 
		        'CS1PB04',
		        sysdate,
		        decode(ord_yeojae_gp,'1','E','2','Z',curr_prog_cd),
		        curr_prog_cd_reg_pgm,
		        curr_prog_reg_ddtt,
		        curr_prog_cd,
		        'SYSTEM',
		        sysdate
		    from USRPMA.TB_PM_SLABCOMM
		    where slab_no = :slab_no
		    )
		WHERE slab_no = :slab_no
		*/
 
		String  queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateSlabCommonScarfingInfo";	
		Object[] params = {sStockId,sStockId};	
		return super.updateData(queryCode,params);
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
    
	public int insertMoveProduct(String qeuryId, List editData) throws DAOException {
		return super.insertData(qeuryId, editData.toArray());
	}
	
	public int updateMoveProduct(String qeuryId, List updateData) {
        return super.updateData(qeuryId, updateData.toArray());
    }
	
	public int updateStockScarfing(String qeuryId, List updateData) {
        return super.updateData(qeuryId, updateData.toArray());
    }
	
	
	public List getListData(String query, List whereData) throws DAOException{
		// PIDEV
//		query = commDao.getYmRulePI("", "getListData", "YM0001", query, "APPPI0", "*", "*" );
		
		return super.findList(query, whereData.toArray());
	}
	public List getListData2(String query) throws DAOException{	
		return super.findList(query);
	}
	
	public int updateData(String qeuryId, List updateData) {
		return super.updateData(qeuryId, updateData.toArray());
	}
	
	
	public JDTORecord getData(String query, List whereData) throws DAOException{
		return super.findByPrimaryKey(query, whereData.toArray());          
	}
	
	//COIL 야드 관리 -> 산적 LOT관리 -> 저장품지정이적 조회
    public int updateStockIdPutLoc(String qeuryId, String updateData, String modifier) {
    	Object[] params = {modifier, updateData};
    	return super.updateData(qeuryId, params);
    }
    
    
    public List getListFrtomoveEndSearchPUP(List list) throws DAOException {

        for(int ii=0 ; ii<list.size() ; ii++) {
            System.out.println("list value = "+list.get(ii));
        }
        if(list == null) {
            throw new DAOException("Input Data is NULL");
        }

        String queryCode = "ym.tsinfo.dao.ydStockDAO.getListFrtomoveEndSearchPUP";  
        				   

        return super.findList(queryCode, list.toArray());
    }
    
    
    
    public List getListFrtomoveEndSearchPUPIfr(List list) throws DAOException {

        for(int ii=0 ; ii<list.size() ; ii++) {
            System.out.println("list value = "+list.get(ii));
        }
        if(list == null) {
            throw new DAOException("Input Data is NULL");
        }

        String queryCode = "ym.tsinfo.dao.ydStockDAO.getListFrtomoveEndSearchPUPIfr";  
        				   

        return super.findList(queryCode, list.toArray());
    }
    
    
    
    /**
     * 차량정보 초기화 프로시져
     * @return findList
     * @throws DAOException
     * .
     */   
    public JDTORecord spCall_YM_SP_TRANSMOVEBACKUPPRO(List list) throws DAOException {
        /*

         */
        int count = 0;
        Object[][] objParam = new Object[list.size()+1][2];

        for(int ii = 0; ii < list.size(); ii++) {
            
            objParam[ii][0] = "IN";
            objParam[ii][1] = list.get(ii);
            
           
        }
        objParam[list.size()][0] = "OUT";
        objParam[list.size()][1] = new Integer(Types.INTEGER);

        String queryCode = "ym.tsinfo.spCall_YM_SP_TRANSMOVEBACKUPPRO";

        return super.execute(queryCode, objParam);
        
    }
    
    /**
     * 차량정보 초기화 프로시져
     * @return findList
     * @throws DAOException
     * .
     */   
    public JDTORecord spCall_YM_SP_TRANSMOVEBACKUPSLAB(List list) throws DAOException {
        /*

         */
        int count = 0;
        Object[][] objParam = new Object[list.size()+1][2];

        for(int ii = 0; ii < list.size(); ii++) {
            
            objParam[ii][0] = "IN";
            objParam[ii][1] = list.get(ii);
            
           
        }
        objParam[list.size()][0] = "OUT";
        objParam[list.size()][1] = new Integer(Types.INTEGER);

        String queryCode = "ym.tsinfo.spCall_YM_SP_TRANSMOVEBACKUPSLAB";

        return super.execute(queryCode, objParam);
        
    }
    
    
    public int getYdStock(JDTORecord inRec, JDTORecordSet outRecSet, int intGp) throws DAOException, JDTOException {
		
		JDTORecordSet rsTemp = null;
		int intRtnVal = -100;
		boolean blnChk_Field = true;
		JDTORecord recPara = null;

		try {
			
			DBAssistantDAO dbAssDao = new DBAssistantDAO();
						
			if (intGp == 1)
				inRec.setField("JSPEED_QUERY_ID",  "ym.steelinfo.steelinforecv.dao.YdStockDAO.getStockCheckList_001");
			else if (intGp == 2)
				inRec.setField("JSPEED_QUERY_ID", "ym.steelinfo.steelinforecv.dao.YdStockDAO.getStockCheckList_002");
			else if (intGp == 3)
				inRec.setField("JSPEED_QUERY_ID", "com.inisteel.cim.yd.dao.ydstockdao.YdStockDao.getYdStockTransOrdDateAB");
			
			//query execute
			rsTemp = dbAssDao.getRecordSet(inRec);
			
			//result recordSet check
			if (rsTemp.size() > 0) {
				//result recordSet -> return recordSet copy
				outRecSet.addAll(rsTemp);
				intRtnVal = rsTemp.size();
			}else {
				//data not found
				return intRtnVal = 0;
			}
		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException( e.getMessage(), e);
		}
		return intRtnVal;
	}
	
    /**
	 * [A] 오퍼레이션명 : 검수테이블 등록 UPDATE
	 * 
	 * @param JDTORecord
	 *            inRec parameter record int intGp 구분(0:STL_NO)
	 * @return int execution count(성공), 0:data not found, -1:duplicate data,
	 *         -2:parameter error, -3:execution failed
	 * @throws DAOException
	 * @throws JDTOException
	 */
	public int updYdStock10(JDTORecord inRec, int intGp) throws DAOException,
			JDTOException {
		String sQueryId = "";

		int count = 0;
		String STL_NO = StringHelper.evl(inRec.getFieldString("STOCK_ID"), "");

		ymCommonDAO dao = ymCommonDAO.getInstance();

		try {
			
	    	// PIDEV
//	    	String sApplyYnPI = commDao.ApplyYnPI("", "updYdStock10=> 검수테이블 등록 UPDATE", "APPPI0", "3", "*");			

//	    	if("Y".equals(sApplyYnPI)) {
				sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW_PIDEV";	
//	    	} else {
//				sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.CreatCarExaminationjlNEW";	    		
//	    	}
	    	
			count = dao.updateData(sQueryId, new Object[] { STL_NO });

		} catch (Exception e) {
			// Exception발생시 EJBServiceException의 상속클래스로 throw합니다.
			throw new DAOException( e.getMessage(), e);
		}
		return count;
	} // end of updYdStock10
    
    
}

