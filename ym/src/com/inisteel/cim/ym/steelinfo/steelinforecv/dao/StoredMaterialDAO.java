package com.inisteel.cim.ym.steelinfo.steelinforecv.dao;


import com.inisteel.cim.common.dao.CommonDAO;
import jspeed.base.record.JDTORecord;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jspeed.base.query.DBAssistant;
import jspeed.base.record.JDTORecordFactory;
import com.inisteel.cim.common.exception.DAOException;

/**
 * 
 * @(#)StoredMaterialDAO.java
 * 
 * @version    :
 * @author     : 이봉준
 * @date         : 2005. 7. 20
 *
 * @description :
 * 
 */
public class StoredMaterialDAO extends CommonDAO{


	public void selectCoilInfo(String coilNo, String isMat) {
	}

	/**
	 * @Param 
	 * stockid : 슬라브 재료번호
	 * 
	 *
	 * @description : 해당 재료 번호에 해당하는 슬라브의 상세 정보를 가져온다. 
	 * 
	 */
	public JDTORecord selectSlabInfo(String stockid) {
		DBAssistant dba = null;
	    ResultSet rset = null;
	    JDTORecord jRecord = null;
		try{
		    dba = new DBAssistant(this);
			jRecord = JDTORecordFactory.getInstance().create();		    
		    String queryCode = "ym.steelinfo.steelinforecv.dao.StoredMaterialDAO.selectSlabInfopm";
		    rset = dba.executeQueryUsingId(queryCode,new Object[]{stockid});	    		    
		    while(rset.next()){	   
				jRecord.setField("SLAB_NO",								rset.getString("SLAB_NO"));
		    	jRecord.setField("PLAN_SLAB_NO",						rset.getString("PLAN_SLAB_NO"));
				jRecord.setField("FNL_REG_PGM",							rset.getString("FNL_REG_PGM"));
				jRecord.setField("PLANT_GP",							rset.getString("PLANT_GP"));
				jRecord.setField("MFG_PROG_STAT",						rset.getString("MFG_PROG_STAT"));
				jRecord.setField("SLAB_CREATE_DDTT",					rset.getString("SLAB_CREATE_DDTT"));
				jRecord.setField("SLAB_CREATE_GP",						rset.getString("SLAB_CREATE_GP"));
				jRecord.setField("MFG_END_GP",							rset.getString("MFG_END_GP"));
				jRecord.setField("MFG_END_DDTT",						rset.getString("MFG_END_DDTT"));
				jRecord.setField("CURR_PROG_CD_REG_PGM",				rset.getString("CURR_PROG_CD_REG_PGM"));
				jRecord.setField("CURR_PROG_REG_DDTT",					rset.getString("CURR_PROG_REG_DDTT"));
				jRecord.setField("CURR_PROG_CD",						rset.getString("CURR_PROG_CD"));
				jRecord.setField("BEFO_PROG_CD_REG_PGM",				rset.getString("BEFO_PROG_CD_REG_PGM"));
				jRecord.setField("BEFO_PROG_REG_DDTT",					rset.getString("BEFO_PROG_REG_DDTT"));
				jRecord.setField("BEFO_PROG_CD",						rset.getString("BEFO_PROG_CD"));
				jRecord.setField("BEFOBEFO_PROG_CD_REG_PGM",			rset.getString("BEFOBEFO_PROG_CD_REG_PGM"));
				jRecord.setField("BEFOBEFO_PROG_REG_DDTT",				rset.getString("BEFOBEFO_PROG_REG_DDTT"));
				jRecord.setField("BEFOBEFO_PROG_CD",					rset.getString("BEFOBEFO_PROG_CD"));
				jRecord.setField("ORD_YEOJAE_GP",						rset.getString("ORD_YEOJAE_GP"));
				jRecord.setField("MFG_NO",								rset.getString("MFG_NO"));
				jRecord.setField("MFG_DTL",								rset.getString("MFG_DTL"));
				jRecord.setField("HEATOUT_AIM",							rset.getString("HEATOUT_AIM"));
				jRecord.setField("SPEC_ABBSYM",							rset.getString("SPEC_ABBSYM"));
				jRecord.setField("SLAB_T",								rset.getString("SLAB_T"));
				jRecord.setField("SLAB_W",								rset.getString("SLAB_W"));
				jRecord.setField("SLAB_LEN",							rset.getString("SLAB_LEN"));
				jRecord.setField("SLAB_WT",								rset.getString("SLAB_WT"));
				jRecord.setField("SLAB_TRIM_GP",						rset.getString("SLAB_TRIM_GP"));
				jRecord.setField("SLAB_TRIM_QNTY",						rset.getString("SLAB_TRIM_QNTY"));
				jRecord.setField("SLAB_TRIM_DATE",						rset.getString("SLAB_TRIM_DATE"));
				jRecord.setField("CC_FST_FNL_SLAB_GP",					rset.getString("CC_FST_FNL_SLAB_GP"));
				jRecord.setField("TAPER_SLAB_GP",						rset.getString("TAPER_SLAB_GP"));
				jRecord.setField("ITEMNAME_CD",							rset.getString("ITEMNAME_CD"));
				jRecord.setField("PROC_ITEMNAME_CD",					rset.getString("PROC_ITEMNAME_CD"));
				jRecord.setField("YEOJAE_CAUSE_CD",						rset.getString("YEOJAE_CAUSE_CD"));
				jRecord.setField("YEOJAE_OCCUR_DDTT",					rset.getString("YEOJAE_OCCUR_DDTT"));
				jRecord.setField("ORD_HCR_GP",							rset.getString("ORD_HCR_GP"));
				jRecord.setField("MAKER_CD",							rset.getString("MAKER_CD"));
				jRecord.setField("BUY_HEAT_NO",							rset.getString("BUY_HEAT_NO"));
				jRecord.setField("BUY_SLAB_NO",							rset.getString("BUY_SLAB_NO"));
				jRecord.setField("SHIPUNLOADING_DATE",					rset.getString("SHIPUNLOADING_DATE"));
				jRecord.setField("SHIPUNLOADING_PLAN_DATE",				rset.getString("SHIPUNLOADING_PLAN_DATE"));
				jRecord.setField("YD_GP",								rset.getString("YD_GP"));
				jRecord.setField("BAY",									rset.getString("BAY"));
				jRecord.setField("SPAN",								rset.getString("SPAN"));
				jRecord.setField("COL",									rset.getString("COL"));
				jRecord.setField("CELLNO",								rset.getString("CELLNO"));
				jRecord.setField("STACK_LAYER",							rset.getString("STACK_LAYER"));
				jRecord.setField("BEFO_LOC",							rset.getString("BEFO_LOC"));
				jRecord.setField("BEFOBEFO_LOC",						rset.getString("BEFOBEFO_LOC"));
				jRecord.setField("PORT_YD_TAKEOUT_DDTT",				rset.getString("PORT_YD_TAKEOUT_DDTT"));
				jRecord.setField("FRTOMOVE_WRSLT_OCCUR_DDTT",			rset.getString("FRTOMOVE_WRSLT_OCCUR_DDTT"));
				jRecord.setField("MISSNO_YN",							rset.getString("MISSNO_YN"));
				jRecord.setField("SCRAP_CAUSE",							rset.getString("SCRAP_CAUSE"));
				jRecord.setField("MISSNO_OCCUR_DDTT",					rset.getString("MISSNO_OCCUR_DDTT"));
				jRecord.setField("A1_TAB_PASS_DDTT",					rset.getString("A1_TAB_PASS_DDTT"));
				jRecord.setField("STORE_LOC_CD",						rset.getString("STORE_LOC_CD"));
		    }		    
		    return jRecord;
		}catch(Exception e){
		    throw new DAOException(e);
		}finally{
		    if(rset !=null)try{rset.close();}catch(Exception e){}
		    if(dba !=null)try{dba.close();}catch(Exception e){}
		}
	}
	
	/**
	 * @Param 
	 * stockid : 코일 재료번호
	 * 
	 *
	 * @description : 해당 재료 번호에 해당하는 코일의 상세정보를 소제 또는 제품별로 가져온다. 
	 * 
	 */
	public JDTORecord selectCoilInfo(String stockid) {
		DBAssistant dba = null;
	    ResultSet rset = null;
	    JDTORecord jRecord = null;
		try{
		    dba = new DBAssistant(this);
			jRecord = JDTORecordFactory.getInstance().create();		    
		    String queryCode = "ym.steelinfo.steelinforecv.dao.StoredMaterialDAO.selectCoilInfo";
		    rset = dba.executeQueryUsingId(queryCode,new Object[]{stockid});	    		    
		    while(rset.next()){	   
		    	jRecord.setField("COIL_NO",							rset.getString("COIL_NO"));//COIL 번호
		    	jRecord.setField("PLANT_GP",						rset.getString("PLANT_GP"));//공장 구분
		    	jRecord.setField("MFG_PROG_STAT",					rset.getString("MFG_PROG_STAT"));//제작 진행 상태
		    	jRecord.setField("REAGENT_NO",						rset.getString("REAGENT_NO"));//시편 번호
		    	jRecord.setField("COIL_CREATE_DDTT",				rset.getString("COIL_CREATE_DDTT"));//COIL 생성 일시
		    	jRecord.setField("COIL_CREATE_GP",					rset.getString("COIL_CREATE_GP"));//COIL 생성 구분
		    	jRecord.setField("MFG_END_GP",						rset.getString("MFG_END_GP"));//제작 종료 구분
		    	jRecord.setField("MFG_END_DDTT",					rset.getString("MFG_END_DDTT"));//제작 종료 일시
		    	jRecord.setField("CURR_PROG_CD_REG_PGM",			rset.getString("CURR_PROG_CD_REG_PGM"));//현재 진도 CODE 등록 PROGRAM
		    	jRecord.setField("CURR_PROG_REG_DDTT",				rset.getString("CURR_PROG_REG_DDTT"));//현재 진도 등록 일시
		    	jRecord.setField("CURR_PROG_CD",					rset.getString("CURR_PROG_CD"));//현재 진도 CODE
		    	jRecord.setField("BEFO_PROG_CD_REG_PGM",			rset.getString("BEFO_PROG_CD_REG_PGM"));//전 진도 CODE 등록 PROGRAM
		    	jRecord.setField("BEFO_PROG_REG_DDTT",				rset.getString("BEFO_PROG_REG_DDTT"));//전 진도 등록 일시
		    	jRecord.setField("BEFO_PROG_CD",					rset.getString("BEFO_PROG_CD"));//전 진도 CODE
		    	jRecord.setField("BEFOBEFO_PROG_CD_REG_PGM",		rset.getString("BEFOBEFO_PROG_CD_REG_PGM"));//전전 진도 CODE 등록 PROGRAM
		    	jRecord.setField("BEFOBEFO_PROG_REG_DDTT",			rset.getString("BEFOBEFO_PROG_REG_DDTT"));//전전 진도 등록 일시
		    	jRecord.setField("BEFOBEFO_PROG_CD",				rset.getString("BEFOBEFO_PROG_CD"));//전전 진도 CODE
		    	jRecord.setField("ORD_YEOJAE_GP",					rset.getString("ORD_YEOJAE_GP"));//주문 여재 구분
		    	jRecord.setField("STL_APPEAR_GP",					rset.getString("STL_APPEAR_GP"));//재료 외형 구분
		    	jRecord.setField("SPEC_ABBSYM",						rset.getString("SPEC_ABBSYM"));//규격 약호
		    	jRecord.setField("HEATOUT_AIM",						rset.getString("HEATOUT_AIM"));//출강 목표
		    	jRecord.setField("COIL_T",							rset.getString("COIL_T"));//COIL 두께
		    	jRecord.setField("COIL_W",							rset.getString("COIL_W"));//COIL 폭
		    	jRecord.setField("COIL_LEN",						rset.getString("COIL_LEN"));//COIL 길이
		    	jRecord.setField("WT_GP",							rset.getString("WT_GP"));//중량 구분
		    	jRecord.setField("NET_WEIGH_WT",					rset.getString("NET_WEIGH_WT"));//NET 계량 중량
		    	jRecord.setField("NET_CAL_WT",						rset.getString("NET_CAL_WT"));//NET 계산 중량
		    	jRecord.setField("GROSS_WEIGH_WT",					rset.getString("GROSS_WEIGH_WT"));//GROSS 계량 중량
		    	jRecord.setField("GROSS_CAL_WT",					rset.getString("GROSS_CAL_WT"));//GROSS 계산 중량
		    	jRecord.setField("MFG_NO_DTL",						rset.getString("MFG_NO_DTL"));//제작 번호 행번
		    	jRecord.setField("ITEMNAME_CD",						rset.getString("ITEMNAME_CD"));//품명 CODE
		    	jRecord.setField("PROC_ITEMNAME_CD",				rset.getString("PROC_ITEMNAME_CD"));//공정 품명 CODE
		    	jRecord.setField("YEOJAE_CAUSE_CD",					rset.getString("YEOJAE_CAUSE_CD"));//여재 원인 CODE
		    	jRecord.setField("YEOJAE_OCCUR_DATE",				rset.getString("YEOJAE_OCCUR_DATE"));//여재 발생 일자
		    	jRecord.setField("TEST_STL_GP",						rset.getString("TEST_STL_GP"));//TEST 재료 구분
		    	jRecord.setField("PLAN_PROC1",						rset.getString("PLAN_PROC1"));//계획 공정1
		    	jRecord.setField("PLAN_PROC2",						rset.getString("PLAN_PROC2"));//계획 공정2
		    	jRecord.setField("PLAN_PROC3",						rset.getString("PLAN_PROC3"));//계획 공정3
		    	jRecord.setField("PASS_PROC1",						rset.getString("PASS_PROC1"));//통과 공정1
		    	jRecord.setField("PASS_PROC2",						rset.getString("PASS_PROC2"));//통과 공정2
		    	jRecord.setField("PASS_PROC3",						rset.getString("PASS_PROC3"));//통과 공정3
		    	jRecord.setField("REMAIN_PROC1",					rset.getString("REMAIN_PROC1"));//잔여 공정1
		    	jRecord.setField("REMAIN_PROC2",					rset.getString("REMAIN_PROC2"));//잔여 공정2
		    	jRecord.setField("NEXT_DEMAND_PROC",				rset.getString("NEXT_DEMAND_PROC"));//다음 요구 공정
		    	jRecord.setField("NEXT_DEMAND_PROC_CAUSE",			rset.getString("NEXT_DEMAND_PROC_CAUSE"));//다음 요구 공정 원인
		    	jRecord.setField("YD_GP",							rset.getString("YD_GP"));//YARD 구분
		    	jRecord.setField("BAY",								rset.getString("BAY"));//동
		    	jRecord.setField("SPAN",							rset.getString("SPAN"));//SPAN
		    	jRecord.setField("COL",								rset.getString("COL"));//열
		    	jRecord.setField("CELLNO",							rset.getString("CELLNO"));//번지
		    	jRecord.setField("STACK_LAYER",						rset.getString("STACK_LAYER"));//적치 단
		    	jRecord.setField("COIL_INDIA",						rset.getString("COIL_INDIA"));//COIL 내경
		    	jRecord.setField("COIL_ALLOC_WT",					rset.getString("COIL_ALLOC_WT"));//COIL 배분 중량
		    	jRecord.setField("SHEAR_WORD_DDTT",					rset.getString("SHEAR_WORD_DDTT"));//정정 작업지시 일시
		    	jRecord.setField("SHEAR_WRSLT_DDTT",				rset.getString("SHEAR_WRSLT_DDTT"));//정정 실적 일시
		    	jRecord.setField("SCRAP_CAUSE",						rset.getString("SCRAP_CAUSE"));//SCRAP 원인
		    	jRecord.setField("BOOK_MARK_YN",					rset.getString("BOOK_MARK_YN"));//예약 표시 유무
		    	jRecord.setField("BOOK_DATE",						rset.getString("BOOK_DATE"));//예약 일자
		    	jRecord.setField("FRTOMOVE_ORD_DATE",				rset.getString("FRTOMOVE_ORD_DATE"));//이송 지시 일자
		    	jRecord.setField("FRTOMOVE_PLANT_GP",				rset.getString("FRTOMOVE_PLANT_GP"));//이송 공장 구분
		    	jRecord.setField("DUTY_PARTY",						rset.getString("DUTY_PARTY"));//근 조
		    	jRecord.setField("INGR_STAMP_GRADE",				rset.getString("INGR_STAMP_GRADE"));//성분 판정 등급
		    	jRecord.setField("INGR_STAMP_DATE",					rset.getString("INGR_STAMP_DATE"));//성분 판정 일자
		    	jRecord.setField("STLQLTY_STAMP_GRADE",				rset.getString("STLQLTY_STAMP_GRADE"));//재질 판정 등급
		    	jRecord.setField("STLQLTY_STAMP_DATE",				rset.getString("STLQLTY_STAMP_DATE"));//재질 판정 일자
		    	jRecord.setField("SURFACE_GRADE_CR_USAGE",			rset.getString("SURFACE_GRADE_CR_USAGE"));//표면 등급 냉연 용도
		    	jRecord.setField("SURFACE_GRADE_STLPIPE_USAGE",		rset.getString("SURFACE_GRADE_STLPIPE_USAGE"));//표면 등급 강관 용도
		    	jRecord.setField("SURFACE_GRADE_STRUCTURE_USAGE",	rset.getString("SURFACE_GRADE_STRUCTURE_USAGE"));//표면 등급 구조 용도
		    	jRecord.setField("SURFACE_OVERALL_GRADE",			rset.getString("SURFACE_OVERALL_GRADE"));//표면 종합 등급
		    	jRecord.setField("FORM_GRADE",						rset.getString("FORM_GRADE"));//형상 등급
		    	jRecord.setField("WDH_GRADE",						rset.getString("WDH_GRADE"));//칫수 등급
		    	jRecord.setField("APPEAR_OVERALL_GRADE",			rset.getString("APPEAR_OVERALL_GRADE"));//외관 종합 등급
		    	jRecord.setField("APPEAR_GRADE_STAMP_DATE",			rset.getString("APPEAR_GRADE_STAMP_DATE"));//외관 등급 판정 일자
		    	jRecord.setField("OVERALL_STAMP_GRADE",				rset.getString("OVERALL_STAMP_GRADE"));//종합 판정 등급
		    	jRecord.setField("OVERALL_STAMP_DATE",				rset.getString("OVERALL_STAMP_DATE"));//종합 판정 일자
		    	jRecord.setField("MID_INSPECT_DATE",				rset.getString("MID_INSPECT_DATE"));//중간 검사 일자
		    	jRecord.setField("MID_INSPECT_DEFECT_CD1",			rset.getString("MID_INSPECT_DEFECT_CD1"));//중간 검사 흠 CODE1
		    	jRecord.setField("MID_INSPECT_EXAMMARK_CD1",		rset.getString("MID_INSPECT_EXAMMARK_CD1"));//중간 검사 평점 CODE1
		    	jRecord.setField("MID_INSPECT_DEFECT_CD2",			rset.getString("MID_INSPECT_DEFECT_CD2"));//중간 검사 흠 CODE2
		    	jRecord.setField("MID_INSPECT_EXAMMARK_CD2",		rset.getString("MID_INSPECT_EXAMMARK_CD2"));//중간 검사 평점 CODE2
		    	jRecord.setField("MID_INSPECT_DEFECT_CD3",			rset.getString("MID_INSPECT_DEFECT_CD3"));//중간 검사 흠 CODE3
		    	jRecord.setField("MID_INSPECT_EXAMMARK_CD3",		rset.getString("MID_INSPECT_EXAMMARK_CD3"));//중간 검사 평점 CODE3
		    	jRecord.setField("MID_INSPECT_DEFECT_CD4",			rset.getString("MID_INSPECT_DEFECT_CD4"));//중간 검사 흠 CODE4
		    	jRecord.setField("MID_INSPECT_EXAMMARK_CD4",		rset.getString("MID_INSPECT_EXAMMARK_CD4"));//중간 검사 평점 CODE4
		    	jRecord.setField("MID_INSPECT_DEFECT_CD5",			rset.getString("MID_INSPECT_DEFECT_CD5"));//중간 검사 흠 CODE5
		    	jRecord.setField("MID_INSPECT_EXAMMARK_CD5",		rset.getString("MID_INSPECT_EXAMMARK_CD5"));//중간 검사 평점 CODE5
		    	jRecord.setField("FNL_SHEAR_DEFECT_CD1",			rset.getString("FNL_SHEAR_DEFECT_CD1"));//최종 정정 흠 CODE1
		    	jRecord.setField("FNL_SHEAR_EXAMMARK_CD1",			rset.getString("FNL_SHEAR_EXAMMARK_CD1"));//최종 정정 평점 CODE1
		    	jRecord.setField("FNL_SHEAR_DEFECT_CD2",			rset.getString("FNL_SHEAR_DEFECT_CD2"));//최종 정정 흠 CODE2
		    	jRecord.setField("FNL_SHEAR_EXAMMARK_CD2",			rset.getString("FNL_SHEAR_EXAMMARK_CD2"));//최종 정정 평점 CODE2
		    	jRecord.setField("FNL_SHEAR_DEFECT_CD3",			rset.getString("FNL_SHEAR_DEFECT_CD3"));//최종 정정 흠 CODE3
		    	jRecord.setField("FNL_SHEAR_EXAMMARK_CD3",			rset.getString("FNL_SHEAR_EXAMMARK_CD3"));//최종 정정 평점 CODE3
		    	jRecord.setField("FNL_SHEAR_DEFECT_CD4",			rset.getString("FNL_SHEAR_DEFECT_CD4"));//최종 정정 흠 CODE4
		    	jRecord.setField("FNL_SHEAR_EXAMMARK_CD4",			rset.getString("FNL_SHEAR_EXAMMARK_CD4"));//최종 정정 평점 CODE4
		    	jRecord.setField("FNL_SHEAR_DEFECT_CD5",			rset.getString("FNL_SHEAR_DEFECT_CD5"));//최종 정정 흠 CODE5
		    	jRecord.setField("FNL_SHEAR_EXAMMARK_CD5",			rset.getString("FNL_SHEAR_EXAMMARK_CD5"));//최종 정정 평점 CODE5
		    	jRecord.setField("SLAB_NO",							rset.getString("SLAB_NO"));//SLAB 번호
		    	jRecord.setField("STORE_LOC_CD",					rset.getString("STORE_LOC_CD"));//저장 위치 CODE
		    }		    
		    return jRecord;
		}catch(Exception e){
		    throw new DAOException(e);
		}finally{
		    if(rset !=null)try{rset.close();}catch(Exception e){}
		    if(dba !=null)try{dba.close();}catch(Exception e){}
		}
	}



	/**
	 * @Param 
	 * stockid : 재료번호
	 * 
	 *
	 * @description : 해당 재료 번호에 해당하는 제품(소재)의 위치 변경이력을 가져온다. 
	 * 
	 */
	public List historyMatLoc(String stockid){
        DBAssistant dba = null;
	    ResultSet rset = null;
	    JDTORecord jRecord = null;
	    List locHisList = null;
	
		try{
		    dba = new DBAssistant(this);
		    locHisList = new ArrayList();		    
		    String queryCode = "ym.wrecord.wrecordif.dao.CraneWrkResDAO.selectStockLocHistory";		    
		    rset = dba.executeQueryUsingId(queryCode,new Object[]{stockid});		    		    
		    while(rset.next()){
		        jRecord = JDTORecordFactory.getInstance().create();
				jRecord.setField("CRANE_WORK_DATE",rset.getString("CRANE_WORK_DATE"));//변경일시
				jRecord.setField("CRANE_WRSLT_PUT_LOC",rset.getString("CRANE_WRSLT_PUT_LOC"));//위치
				jRecord.setField("CRANE_WORK_PARTY",rset.getString("CRANE_WORK_PARTY"));//작업크레인
				jRecord.setField("SCH_WKIND",rset.getString("SCH_WKIND"));//스케줄종류
				locHisList.add(jRecord);
		    }
		    
		    return locHisList;
		}catch(Exception e){
		    throw new DAOException(e);
		}finally{
		    if(rset !=null)try{rset.close();}catch(Exception e){}
		    if(dba !=null)try{dba.close();}catch(Exception e){}
		}
    }
	
    public int updateScarfingPatternWrt(List list) throws DAOException {

        
        String queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateScarfingPatternWrt";
        
        return super.updateData(queryCode, list.toArray());
    }
    
    public int updateHdScarfingWrt(List list) throws DAOException {

        
        String queryCode = "ym.steelinfo.steelinforecv.dao.YdStockDAO.updateHdScarfingWrt";
        
        return super.updateData(queryCode, list.toArray());
    }
    
    public int updateScarfingDelayPUP(List list) throws DAOException {
    	

        String queryCode = "ym.steelinfo.steelinforecv.dao.StoredMaterialDAO.updateScarfingDelayPUP";

        return updateData(queryCode, list.toArray());
    }
	
	
	
	public List findByProductList(String stack_col_gp){
        DBAssistant dba = null;
	    ResultSet rset = null;
	    JDTORecord jRecord = null;
	    List producList = null;
	
		try{
		    dba = new DBAssistant(this);
		    producList = new ArrayList();		    
		    String queryCode = "ym.steelinfo.steelinforecv.dao.StoredMaterialDAO.selectProducList";		    
		    rset = dba.executeQueryUsingId(queryCode,new Object[]{stack_col_gp});		    		    
		    while(rset.next()){

/*
    A.STOCK_ID AS STOCK_ID,
    A.STACK_COL_GP||A.STACK_BED_GP||A.STACK_LAYER_GP AS STOCK_LOCATION, 
    B.COIL_WT AS COIL_WT,
    B.COIL_W AS COIL_W,
    B.COIL_T AS COIL_T,
    '강종' AS STLKIND,
    NVL2(C.WBOOK_ID,'Y','N') AS WBOOK_ID
*/
		        jRecord = JDTORecordFactory.getInstance().create();
				jRecord.setField("STOCK_ID",rset.getString("STOCK_ID"));//재료 NO
				jRecord.setField("STOCK_LOCATION",rset.getString("STOCK_LOCATION"));//저장위치
				jRecord.setField("COIL_WT",rset.getString("COIL_WT"));//중량
				jRecord.setField("COIL_W",rset.getString("COIL_W"));//폭
				jRecord.setField("COIL_T",rset.getString("COIL_T"));//두께
				jRecord.setField("STLKIND",rset.getString("STLKIND"));//강종
				jRecord.setField("WBOOK_ID",rset.getString("WBOOK_ID"));//작업지시 수신여부
				producList.add(jRecord);
		    }
		    
		    return producList;
		}catch(Exception e){
		    throw new DAOException(e);
		}finally{
		    if(rset !=null)try{rset.close();}catch(Exception e){}
		    if(dba !=null)try{dba.close();}catch(Exception e){}
		}
    }


	
}

