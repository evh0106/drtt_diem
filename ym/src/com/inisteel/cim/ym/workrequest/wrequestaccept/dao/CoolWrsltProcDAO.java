/*****************************************************************************
** 프로그램 ID	: com.inisteel.cim.po.jungjung.result.dao.CoolWrsltProcDAO.java
** 작 성 일 자	: 2006/04/13
** 작  성  자	: 박종민
** 설       명	: 냉각실적처리
** E J B   명 	: 
**   JNDI       : 
** 주요 Method	:
** --------------------------------------------------------------------------
** 수정이력     :
** 수정일자		: 
** 수 정 자		: 
** 설    명	 	:
** 주요Table	: 
** 조회 조건 항목 
** 
*****************************************************************************/
/*
 * Created on 2005. 8. 16.
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.inisteel.cim.ym.workrequest.wrequestaccept.dao;

import java.util.List;
import java.util.ArrayList;

import jspeed.base.log.LogService;
import jspeed.base.log.Logger;

import com.inisteel.cim.common.dao.CommonDAO;
import com.inisteel.cim.common.exception.DAOException;

public class CoolWrsltProcDAO extends CommonDAO {
	private Logger log = null;
	public CoolWrsltProcDAO()
	{
		log = LogService.getInstance().getLogServiceContext().getLogger( "template" );
	}
	
	/*****************************************************************************
	 * 냉각처리 대상 코일공통 정보 조회
	 * @param ListParam
	 * @return
	 * @throws DAOException
	 ****************************************************************************/
	public List getListCoolWrsltProcCoilCommRead(List ListParam) throws DAOException {
		/**
		SELECT
		     A.*                       -- 코일공통
		     ,E.STEP_NO                 -- 코일정정지시실적.차수
		     ,B.ORD_CONV_T             as SM_ORD_T             -- 주문진행.주문두께
		     ,B.T_TOL_MAX              as SM_T_TOL_MAX         -- 주문진행.두께허용오차최대
		     ,B.T_TOL_MIN              as SM_T_TOL_MIN         -- 주문진행.두게허용오차최소
		     ,B.ORD_CONV_W             as SM_ORD_W             -- 주문진행.주문 폭
		     ,B.W_TOL_MAX              as SM_W_TOL_MAX         -- 주문진행.폭허용오차 최대
		     ,B.W_TOL_MIN              as SM_W_TOL_MIN         -- 주문진행.폭허용오차 최소
		     ,B.ORD_CONV_LEN           as SM_ORD_LEN           -- 주무진행.주문길이
		     ,B.WRAPUNIT_WT_EA_UNIT    as SM_WRAP_UNIT         -- 주문진행.포장단위
		     ,B.WRAPUNIT_WT_EA_MIN     as SM_WRAPUNIT_WT_EA_MIN  -- 주문진행.포장단위중량최소
		     ,B.WRAPUNIT_WT_EA_MAX     as SM_WRAPUNIT_WT_EA_MAX  -- 주문진행.포장단위중량최대
		     ,B.CUST_CD                as SM_CUST_CD           -- 고객공통.고객코드
		     ,( SELECT  Y.CUST_KO_NAME FROM  MV_SM_CUSTINFO  Y WHERE Y.CUST_CD = B.CUST_CD )      as SM_CUST_NAME       -- 고객정보.수요가명
		     ,B.DEMANDER_CD            as SM_REAL_CUST_CD      -- 고객공통.실고객코드
		     ,( SELECT  Y.CUST_KO_NAME FROM  MV_SM_CUSTINFO  Y WHERE Y.CUST_CD = B.DEMANDER_CD ) as SM_REAL_CUST_NAME  -- 고객정보.실고객명
		     ,F.WRSLT_COIL_T           as PO_WRSLT_COIL_T      -- 압연실적.실적 COIL 두께
		     ,F.WRSLT_COIL_W           as PO_WRSLT_COIL_W      -- 압연실적.실적 COIL 폭
		     ,F.WT_DECISION_CD         as PO_WT_DECISION_CD    -- 압연실적.중량 결정 CODE
		     ,F.COIL_CAL_WT            as PO_COIL_CAL_WT       -- 압연실적.계산 중량
		     ,F.COIL_REAL_WT           as PO_COIL_REAL_WT      -- 압연실적.COIL 실 중량
		     ,TO_CHAR(F.MILL_WRK_DT, 'YYYYMMDDHH24MISS')  as PO_MILL_WORK_DATE    -- 압연실적.압연 작업 일자
		     ,G.SHEAR_ITM_CD           as SHEAR_ITM_CD         -- 정정 ITEM코드
		     
		       FROM TB_PT_COILCOMM         A  -- 코일공통
		      ,TB_PT_OSCOMM           B  -- 주문진행
		      ,TB_HR_C_SHEARWOWR      E  -- 코일정정지시실적
		      ,TB_HR_B_MILLWR         F  -- 코일압연실적
		      ,TB_CT_L_HRMILLWO       G  -- 압연작업지시
		 WHERE A.HR_PLNT_GP IN ('B','C') --  공장구분
		   AND A.COIL_NO = ?             -- 1코일번호
		   AND A.ORD_NO  = B.ORD_NO(+)
		   AND A.ORD_DTL = B.ORD_DTL(+)
		   AND A.COIL_NO = E.COIL_NO(+)
		   AND A.SLAB_NO = G.SLAB_NO(+)
		   AND NVL(E.STEP_NO,0) = ( SELECT NVL(MAX(X.STEP_NO),0)
		                     FROM TB_HR_C_SHEARWOWR X
		                     WHERE X.COIL_NO = E.COIL_NO )
		   AND A.COIL_NO = F.COIL_NO(+)
		   AND A.HR_PLNT_GP = F.HR_PLNT_GP(+)   
		   AND (A.COOL_DONE_GP != 'Y' OR A.COOL_DONE_GP IS NULL)
		   AND A.NEXT_PROC IN ('5A','DA','EA','FA','GA','HA')   
		 * 
		 */
		//String queryCode="po.jungjung.result.coolWrsltProc.getListCoolWrsltProcDAO.CoilComm.select";
		String queryCode="ym.workrequest.wrequestaccept.CoolWrsltProcDAO.getListCoolWrsltProcCoilCommRead_PIDEV";
		return super.findList(queryCode, ListParam.toArray());
	}	
	
	/*****************************************************************************
	 * 코일공통(여재경우) UPDATE 
	 * @param ListParam
	 * @return
	 * @throws DAOException
	 ****************************************************************************/		
	public int upDateCoolWrsltProcYeojae(List ListParam)throws DAOException{
		/**
		update TB_PT_COILCOMM
		    set  CURR_PROG_CD_REG_PGM     = ?                        -- 0 현재진도CODE등록 PROGRAM
		        ,CURR_PROG_REG_DDTT       = SYSDATE                  --   현재진도등록일시
		        ,CURR_PROG_CD             = ?                        -- 1 현재진도CODE(보류재:F/입고대기:G)
		        ,BEFO_PROG_CD_REG_PGM     = CURR_PROG_CD_REG_PGM     --   전진도CODE등록 PROGRAM
		        ,BEFO_PROG_REG_DDTT       = CURR_PROG_REG_DDTT       --   전진도등록일시
		        ,BEFO_PROG_CD             = CURR_PROG_CD             --   전진도CODE
		        ,BEFOBEFO_PROG_CD_REG_PGM = BEFO_PROG_CD_REG_PGM     --   전전진도CODE등록 PROGRAM
		        ,BEFOBEFO_PROG_REG_DDTT   = BEFO_PROG_REG_DDTT       --   전전진도등록일시
		        ,BEFOBEFO_PROG_CD         = BEFO_PROG_CD             --   전전진도CODE
		
		        ,ORD_YEOJAE_GP            = ?                        -- 2 주문 여재 구분
		        ,YEOJAE_CAUSE_CD          = ?                        -- 3 여재 원인 CODE
		        ,YEOJAE_OCCUR_DATE        = ?                        -- 4 여재 발생일자
		        ,YEOJAE_OCCUR_TIME        = ?                        -- 5 여재 발생시각
		        ,YEOJAE_CAUSE_CD1         = ?                        -- 6 여재 원인 CODE1
		        ,YEOJAE_OCCUR_DATE1       = ?                        -- 7 여재 발생일자1
		        ,YEOJAE_CAUSE_CD2         = ?                        -- 8 여재 원인 CODE2
		        ,YEOJAE_OCCUR_DATE2       = ?                        -- 9 여재 발생일자2
		        ,YEOJAE_CAUSE_CD3         = ?                        -- 10 여재 원인 CODE3
		        ,YEOJAE_OCCUR_DATE3       = ?                        -- 11 여재 발생일자3
		        ,YEOJAE_CAUSE_CD4         = ?                        -- 12 여재 원인 CODE4
		        ,YEOJAE_OCCUR_DATE4       = ?                        -- 13 여재 발생일자4
		
		        ,COOL_DONE_GP             = ?                        -- 14 냉각완료('Y')
		        ,PASS_PROC1               = ?                        -- 15 통과공정1('2A')
		        ,REMAIN_PROC1             = ?                        -- 16 잔여공정1('')
		        ,NEXT_PROC                = ?                        -- 17 다음공정('')
		
		        ,HOLD_CAUSE_CD            = ?                        -- 18 보류원인 CODE
		        ,HOLD_CAUSE_CD_PM1        = ?                        -- 19 보류원인1 CODE
		        ,HOLD_CAUSE_CD_PM2        = ?                        -- 20 보류원인2 CODE
		        ,HOLD_CAUSE_CD_PM3        = ?                        -- 21 보류원인3 CODE
		        ,HOLD_CAUSE_CD_PM4        = ?                        -- 22 보류원인4 CODE
		        ,HOLD_PROG_STAT           = ?                        -- 23 보류 진행 상태
		        ,HOLD_STAMP_DATE          = ?                        -- 24 보류 판정 일자
		        ,HOLD_STAMP_TIME          = ?                        -- 25 보류 판정 시각
		
		        ,COIL_T                   = ?                        -- 26 코일 두께
		        ,COIL_W                   = ?                        -- 27 코일 폭
		        ,SHEAR_WORD_DATE          = ?                        -- 28 정정 작업 지시 일자
		        ,SHEAR_WRSLT_DATE         = ?                        -- 29 정정 실적 일자
		        ,MODIFIER                 = ?                        -- 30 수정자
		        ,MOD_DDTT                 = SYSDATE                  --    수정일자
		        ,WO_ITM                   = ?                        --    정정작업지시item
		        ,PRD_ITM_CD               = ?                        --    작업실적item
		        ,PLNT_PROC_CD             = ?                        --    공장공정코드(HBF/HCF) 
		        
		where   COIL_NO                   =  ?                       -- 31 코일번호 
		 and    PLANT_GP                  =  ?                       -- 32 공장구분 

		 * 
		 */
		
		//String queryCode="po.jungjung.result.coolWrsltProc.upDateCoolWrsltProcYeojaeDAO.CoilComm.update";
		String queryCode="ym.workrequest.wrequestaccept.CoolWrsltProcDAO.upDateCoolWrsltProcYeojae";
		return super.updateData(queryCode, ListParam.toArray());			
	}
	
	/*****************************************************************************
	 * 코일공통(주문재경우) UPDATE 
	 * @param ListParam
	 * @return
	 * @throws DAOException
	 ****************************************************************************/		
	public int upDateCoolWrsltProc(List ListParam)throws DAOException{
		/**
		update TB_PT_COILCOMM
		  set  CURR_PROG_CD_REG_PGM     = ?                        -- 0 현재진도CODE등록 PROGRAM
		      ,CURR_PROG_REG_DDTT       = SYSDATE                  --   현재진도등록일시
		      ,CURR_PROG_CD             = ?                        -- 1 현재진도CODE(보류재:F,입고대기:G)
		      ,BEFO_PROG_CD_REG_PGM     = CURR_PROG_CD_REG_PGM     --   전진도CODE등록 PROGRAM
		      ,BEFO_PROG_REG_DDTT       = CURR_PROG_REG_DDTT       --   전진도등록일시
		      ,BEFO_PROG_CD             = CURR_PROG_CD             --   전진도CODE
		      ,BEFOBEFO_PROG_CD_REG_PGM = BEFO_PROG_CD_REG_PGM     --   전전진도CODE등록 PROGRAM
		      ,BEFOBEFO_PROG_REG_DDTT   = BEFO_PROG_REG_DDTT       --   전전진도등록일시
		      ,BEFOBEFO_PROG_CD         = BEFO_PROG_CD             --   전전진도CODE
		
		      ,COOL_DONE_GP             = ?                        -- 2 냉각완료('Y')
		      ,PASS_PROC1               = ?                        -- 3 통과공정1('2A')
		      ,REMAIN_PROC1             = ?                        -- 4 잔여공정1('')
		      ,NEXT_PROC                = ?                        -- 5 다음공정('')
		
		      ,HOLD_CAUSE_CD            = ?                        -- 6 보류원인 CODE
		      ,HOLD_CAUSE_CD_PM1        = ?                        -- 7 보류원인1 CODE
		      ,HOLD_PROG_STAT           = ?                        -- 11 보류 진행 상태
		      ,HOLD_STAMP_DATE          = ?                        -- 12 보류 판정 일자
		      ,HOLD_STAMP_TIME          = ?                        -- 13 보류 판정 시각
		      ,SHEAR_WORD_DATE          = ?                        -- 14 정정 작업 지시 일자
		      ,SHEAR_WRSLT_DATE         = ?                        -- 15 정정 실적 일자
		      ,MODIFIER                 = ?                        -- 16 수정자
		      ,MOD_DDTT                 = SYSDATE                  --    수정일자
		      ,WO_ITM                   = ?                        -- 16 정정작업지시ITEM
		      ,PRD_ITM_CD               = ?                        -- 16 작업실적ITEM
		      ,PLNT_PROC_CD             = ?                        -- 16 공장공정코드
		where  COIL_NO                  =  ?                       -- 17 코일번호 
		 and   HR_PLNT_GP               =  ?                       -- 18 공장구분 
		 * 
		 */
		
		//String queryCode="po.jungjung.result.coolWrsltProc.upDateCoolWrsltProcDAO.CoilComm.update";
		String queryCode="ym.workrequest.wrequestaccept.CoolWrsltProcDAO.upDateCoolWrsltProc";
		return super.updateData(queryCode, ListParam.toArray());			
	}
	
    
	
	/*******************************************************************************
	 * 정정 지시실적 등록 
	 * @param ListParam
	 * @return
	 * @throws DAOException
	 ******************************************************************************/
	public int insertCoolWrsltProc(List ListParam) throws DAOException{
		/**
		INSERT INTO TB_HR_C_SHEARWOWR
		    (  COIL_NO                     --0 COIL 번호                                         
		      ,STEP_NO                     --1 차수(STEP_NO+1)                                   
		      ,HR_PLNT_GP                  --2 공장 구분(B열연)                                  
		      ,PROC_GP                     --3 공정 구분(A:공냉/T:수냉)                          
		      ,WORK_STAT                   --4 작업 상태(*)                                      
		      ,WORD_OUTDIA                 --5 작업지시 외경                                     
		      ,COIL_T                      --6 COIL 두께                                         
		      ,COIL_W                      --7 COIL 폭                                           
		      ,WT_DECISION_CD              --8 중량 결정 CODE                                    
		      ,REAL_WT                     --9 실 중량                                           
		      ,CAL_WT                      --10 계산 중량                                        
		      ,AIM_T                       --11 목표 두께                                        
		      ,T_TOL_MAX                   --12 두께 허용오차 최대                               
		      ,T_TOL_MIN                   --13 두께 허용오차 최소                               
		      ,AIM_W                       --14 목표 폭                                          
		      ,W_TOL_MAX                   --15 폭 허용오차 최대                                 
		      ,W_TOL_MIN                   --16 폭 허용오차 최소                                 
		      ,ORD_T                       --17 주문 두께                                        
		      ,ORD_W                       --18 주문 폭                                          
		      ,ORD_LEN                     --19 주문 길이                                        
		      ,FNL_SHEAR_DEFECT_CD1        --20 최종정정 흠 CODE1                                
		      ,FNL_SHEAR_DEFECT_CD2        --21 최종정정 흠 CODE2                                
		      ,FNL_SHEAR_DEFECT_CD3        --22 최종정정 흠 CODE3                                
		      ,FNL_SHEAR_DEFECT_CD4        --23 최종정정 흠 CODE4                                
		      ,FNL_SHEAR_DEFECT_CD5        --24 최종정정 흠 CODE5                                
		      ,DEMANDER_CD                 --25 수요가 CODE                                      
		      ,DEMANDER_NAME               --26 수요가 명                                        
		      ,ORD_NO                      --27 주문 번호                                        
		      ,ORD_DTL                     --28 주문 행번                                        
		      ,SPEC_ABBSYM                 --29 규격 약호                                        
		      ,USAGE_CD                    --30 용도 CODE                                        
		      ,MILL_WRK_DT                --31 압연 작업 일자                                          
		      ,SHEAR_WORD_DT              --33 정정 지시 일자                                   
		      ,AIM_LEN                     --34 목표 길이                                        
		      ,WRSLT_OUTDIA                --35 실적 외경                                        
		      ,WRSLT_INDIA                 --36 실적 내경                                        
		      ,WRSLT_T                     --37 실적 두께                                        
		      ,WRSLT_W                     --38 실적 폭                                          
		      ,WRSLT_LEN                   --39 실적 길이                                        
		      ,WRSLT_WT_DECISION_CD        --40 실적 중량 결정 CODE                              
		      ,WRSLT_REAL_WT               --41 실적 실 중량                                     
		      ,WRSLT_CAL_WT                --42 실적 계산 중량                                   
		      ,RECEIPT_HOLD_SCRAP_CAUSE_GP --43 입고 보류 SCRAP 원인 구분 (입고(I)/보류(I))      
		      ,DUTY_GP                        --44 근                                               
		      ,PARTY                       --45 조                                               
		      ,SHEAR_INI_DATE              --46 정정 계상 일자                                   
		      ,SHEAR_WORK_INSPECT_DT       --47 정정 작업 검사 일자                                     
		      ,SHEAR_WORK_ST_DT            --49 정정 작업 FROM 일자                                     
		      ,SHEAR_WORK_CMPL_DT          --51 정정 작업 TO 일자                                       
		      ,SHEAR_WORK_DT               --53 정정 작업 종료 일자                                     
		      ,HOLD_CAUSE_CD               --55 보류 원인 CODE                                   
		      ,HOLD_CAUSE_CD_PM1           --56 보류 원인1 CODE                                  
		      ,HOLD_CAUSE_CD_PM2           --57 보류 원인2 CODE                                  
		      ,HOLD_CAUSE_CD_PM3           --58 보류 원인3 CODE                                  
		      ,HOLD_CAUSE_CD_PM4           --59 보류 원인4 CODE                                  
		      ,HOLD_PROG_STAT              --60 보류 진행 상태                                   
		      ,HOLD_STAMP_DT               --61 보류 판정 일자                                          
		      ,CUST_CD                  --63 주문자CODE(실고객코드)                           
		      ,CUST_NAME                --64 주문자명(실고객명)                               
		      ,WRAPUNIT_WT_EA_UNIT           --65 포장 단위 중량 단위                              
		      ,WRAPUNIT_WT_EA_MAX            --66 포장 단위 중량 최대                              
		      ,WRAPUNIT_WT_EA_MIN            --67 포장 단위 중량 최소                              
		--      ,FNL_TC_BACKUP_GP            --68 최종 TC BACKUP 구분(B)                           
		--      ,FNL_REG_TC_NAME             --69 최종 등록 TC 명(coolWrsltBatch))                 
		      ,ITEMNAME_CD                 --70 품명 CODE                                        
		      ,REGISTER                    --71 등록자                                           
		      ,REG_DDTT                    --72 등록 일시                                               
		      ,MODIFIER                    --73 수정자                                           
		      ,MOD_DDTT                    --74 수정 일시
		      ,WO_ITM                      --작업지시ITEM
		      ,PRD_ITM_CD                  --생산ITEM코드
		      ,PTOP_PLNT_GP                -- 조업공장구분
		      ,SLAB_NO)                    --75 SLAB번호                                         
		      VALUES (
		      ?,                         
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      TO_DATE(?, 'YYYYMMDDHH24MISS'),    
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      TO_DATE(?, 'YYYYMMDDHH24MISS'),    
		      TO_DATE(?, 'YYYYMMDDHH24MISS'),    
		      TO_DATE(?, 'YYYYMMDDHH24MISS'),    
		      TO_DATE(?, 'YYYYMMDDHH24MISS'),    
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      TO_DATE(?, 'YYYYMMDDHH24MISS'),    
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      ?,                                                                 
		      ?,                                 
		      ?,                                 
		      ?,                                 
		      TO_DATE(?, 'YYYYMMDDHH24MISS'),    
		      ?,                                 
		      TO_DATE(?, 'YYYYMMDDHH24MISS'),
		      ?,
		      ?,
		      ?,
		      ?)                                 
		 * 
		 */
		//String queryCode="po.jungjung.result.coolWrsltProc.insertCoolWrsltProcDAO.coilSharordWrlst.insert";
		String queryCode="ym.workrequest.wrequestaccept.CoolWrsltProcDAO.insertCoolWrsltProc";
		return super.insertData(queryCode, ListParam.toArray());
	}
	
	/* COIL공통 조회(TB_PM_COILCOMM)*/     		
	public List getCoilCommDtl(String coilNo) throws DAOException {
		/*  ----------------------------------------------------------
			SELECT * FROM TB_PM_COILCOMM  WHERE COIL_NO = ?
		  -----------------------------------------------------------*/
		List ParamList = new ArrayList();
		ParamList.add(coilNo);                
	    //String queryCode="po.jungjung.result.dao.ABReceiveRollingInfoDAO.getCoilCommDtl";
	    String queryCode="ym.workrequest.wrequestaccept.ABReceiveRollingInfoDAO.getCoilCommDtl";
	    return super.findList(queryCode, ParamList.toArray());
    }
	
	/* 주문진행자료조회(TB_PM_ORDPROG)*/     		
	public List getOrdProgDtl(List ParamList) throws DAOException {
		/*  ----------------------------------------------------------
			SELECT * FROM TB_PM_ORDPROG
			WHERE ORD_NO =? 
			     and   ORD_DTL =?	
		  -----------------------------------------------------------*/
	    //String queryCode="po.jungjung.result.dao.ABReceiveRollingInfoDAO.getOrdProgDtl";
	    String queryCode="ym.workrequest.wrequestaccept.ABReceiveRollingInfoDAO.getOrdProgDtl";
	    return super.findList(queryCode, ParamList.toArray());
    }
	
	/* 주문공통(TB_SM_ORDCOMM)*/     		
	public List getOrdComm(List ParamList) throws DAOException {
		/*  ----------------------------------------------------------
		 	SELECT * FROM TB_SM_ORDCOMM
 			WHERE   ORD_NO  = ? 
		  -----------------------------------------------------------*/
	    //String queryCode="po.jungjung.result.dao.ABReceiveRollingInfoDAO.getOrdComm";
	    String queryCode="ym.workrequest.wrequestaccept.ABReceiveRollingInfoDAO.getOrdComm";
	    return super.findList(queryCode, ParamList.toArray());
    }
	
}
	
