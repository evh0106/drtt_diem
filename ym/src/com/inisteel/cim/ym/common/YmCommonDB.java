/**
 * 
 * @(#)YmCommonDB
 * 
 * @version    :
 * @author     : 
 * @date       : 
 *
 * @description :
 * 
 */
package com.inisteel.cim.ym.common;

import java.util.List;

import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;

import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;



public class YmCommonDB{   	
	
	private static YmCommonDB instance;
    
    public static YmCommonDB getInstance()
    {
        if(instance == null)
        {
            synchronized(com.inisteel.cim.ym.common.YmCommonDB.class)
            {
                if(instance == null)
                    instance = new YmCommonDB();
            }
        }
        return instance;
    }    
	
	/**
     * YJK
	 * 콘베이어 정보를 생성한다. 
     *
     * @param  String	: 적치열
     * @param  String	: 저장품ID
     * @param  String	: 추가시
     *                    	MIN  - 적치열 MAX +1번지를 생성하고 01번지에 
     *							   저장품ID 저장 나머지 번지 +1씩 SHIFT
     *						MAX  - 적치열 MAX +1번지를 생성하고 MAX +1번지에
     *							   저장품ID 저장
     *						번지 - 적치열 MAX +1번지를 생성하고 해당번지에 
     *							   저장품ID 저장 이후 번지 +1씩 SHIFT
     * @return String Message
     * @throws 
     */			
    public static int shiftConveyorInfo(String sStackColGp,
					 		  		    String sStackBedGp){
		return insertConveyorInfo(sStackColGp,
								  "",
								  sStackBedGp);								  		   	
	}								   		    	
	 
    public static int insertConveyorInfo(String sStackColGp,
							             String sStockId,
							             String sStackBedGp){
							        
        return YmCommonDB.getInstance().insertConveyorInfoSync(sStackColGp,
        													   sStockId,
															   sStackBedGp);        
        
    }
    
	private synchronized int insertConveyorInfoSync(String sStackColGp,
											 		String sStockId,
													String sStackBedGp){
		Logger logger = LogService.getInstance().getLogger("ym");
		
		String sMaxStackBedGp 			= "";
		String sQueryId 				= "";
		
		JDTORecord curBedV  			= null;
		JDTORecord maxBedV  			= null;
		JDTORecord colXyV  				= null;
		JDTORecord listJ  				= null;
		
		List listV 						= null;
		
		int iSeq 						= -1;
		boolean bSwitch 				= false;
		 
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		if(!"".equals(sStockId)){
			//적치열에 저장품 ID가 있는 적치번지 정보를 가져온다.
			/*
			 SELECT *
			  FROM tb_ym_stacklayer
			 WHERE stack_col_gp  = :stack_col_gp
			   AND   stock_id	 = :stock_id
			*/
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId";
			curBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,sStockId});
		}
		
		if(curBedV == null){
			
			//적치열 X,Y 좌표 정보를 가져온다.
			/*
			SELECT 
			  *
			  FROM tb_ym_stackcol
			 WHERE stack_col_gp  	= :stack_col_gp
			*/
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackColInfoWithPk";
			colXyV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp});
			//================================================================================================
			// SPM2 추출위치를 생성할 때는 X, Y 좌표값을 jSpeed의 YM002의 데이터를 읽어서 처리한다. 최규성 2010-01-29
			/*
			 * select  CLASS4_CD AS STACK_BED_GP , CLASS4_NAME1 AS STACK_COL_RULE_X_AXIS, CLASS4_NAME2 AS STACK_COL_RULE_Y_AXIS
				 from tb_cm_cdclass4
				where TYPE_CD='YM002'
				and CLASS1_CD='EQPNO'
				AND CLASS2_CD='3'
				AND CLASS3_CD='3EKD02'
			 * */
			if(sStackColGp.equals(YmCommonConst.STACK_COL_GP_3EKD02) && 
					( sStackBedGp.equals("")||sStackBedGp.equals(null) || //세들값이 없을 경우 기본값 적용
					  sStackBedGp.equals(YmCommonConst.STACK_BED_GP_21) || 
					  sStackBedGp.equals(YmCommonConst.STACK_BED_GP_23) || 
					  sStackBedGp.equals(YmCommonConst.STACK_BED_GP_24) || 
					  sStackBedGp.equals(YmCommonConst.STACK_BED_GP_25) || 
					  sStackBedGp.equals(YmCommonConst.STACK_BED_GP_26) ||
					  sStackBedGp.equals(YmCommonConst.STACK_BED_GP_22)
					)
			  )
			{
				if(sStackBedGp.equals("")||sStackBedGp.equals(null)){
					sStackBedGp ="21";
				}			
				
				String sQueryId_code = "ym.facilitystatus.facilityinquiry.getCodeClass_spm2LineOut";
				colXyV = dao.getCommonInfo(sQueryId_code,new Object[]{sStackColGp,sStackBedGp});
			}
			//================================================================================================
			//적치열 MAX번지 정보를 가져온다.
			/*
			select NVL(MAX(stack_bed_gp), '00') as MAXBED
			from tb_ym_stacker
			where STACK_COL_GP = :stack_col_gp
			*/
			sQueryId = "ym.common.YmCommonDB.getMaxStackBedInfo";
			maxBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp});
			
		    //적치열 MAX+1번지 정보를 생성한다.
		    sMaxStackBedGp = StringHelper.evl(maxBedV.getFieldString("MAXBED"),"");
		    sMaxStackBedGp = YmCommonUtil.changeLayerFormat(sMaxStackBedGp,"P");
		    
		    //SPM 재작업 보급 TO위치
		    //if(sStackColGp.substring(0,1).equals("1") && sStackBedGp.equals("J05") ){
		    //	sMaxStackBedGp ="05";
		   // }

		    if(sStackColGp.equals(YmCommonConst.STACK_COL_GP_3EKD02) && 
					(sStackBedGp.equals(YmCommonConst.STACK_BED_GP_21) || 
					 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_23) || 
					 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_24) || 
					 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_25) || 
					 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_26) ||
					 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_22)
					)
		      )
		    {
		    	
		    	sMaxStackBedGp = sStackBedGp;	//
		    }
		    
		    logger.println(LogLevel.DEBUG, "SPM2 추출위치 최대BED: "+sMaxStackBedGp);
		    
		    if(sMaxStackBedGp.length() > 2){
		    	return iSeq;
		    }
		 	//***************************************************************************************
		    //적치대정보가 존재 안 하는 경우 생성해준다.
		 	sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBSTACKER";
		 	List chkList = dao.getCommonList(sQueryId,new Object[]{sStackColGp,sMaxStackBedGp});
		 	
		 	if(chkList.size()<=0 ){
		 		//TB_YM_STACKER TABLE를 생성한다.
		 		sQueryId = "ym.common.YmCommonDB.createStakerInfo";
			/*
			 INSERT INTO tb_ym_stacker
			(
			 stack_col_gp,
			 stack_bed_gp,
			 stack_bed_active_stat,
			 register,
			 reg_ddtt,
			 del_yn
			)
			VALUES
			(
			 :stack_col_gp,
			 :stack_bed_gp,
			 'O',
			 :register,
			 sysdate,
			 'N'
			) 
			*/
		 		iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
															sMaxStackBedGp,
															"SYSTEM"});
		 	}
		 	//***************************************************************************************
		 	
		 	
		 	//***************************************************************************************
		 	sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";
		 	List chkList2 = dao.getCommonList(sQueryId,new Object[]{sStackColGp,
		 															sMaxStackBedGp,
								 									YmCommonConst.STACK_LAYER_GP_01});
		 	//dao.getCommonInfo(queryCode, objs);
		 	if(chkList2.size()<=0 ){
			//TB_YM_STACKLAYER TABLE를 생성한다.
			sQueryId = "ym.common.YmCommonDB.createStakLayerInfo";
			/*
			INSERT INTO tb_ym_stacklayer
			(
			 stack_col_gp,
			 stack_bed_gp,
			 stack_layer_gp,
			 stack_layer_active_stat,
			 stack_layer_stat,
			 STACK_LAYER_X_AXIS,
			 STACK_LAYER_Y_AXIS,
			 register,
			 reg_ddtt,
			 del_yn
			)
			VALUES
			(
			 :stack_col_gp,
			 :stack_bed_gp,
			 :stack_layer_gp,
			 'O',
			 'E',
			 :STACK_LAYER_X_AXIS,
			 :STACK_LAYER_Y_AXIS,
			 :register,
			 sysdate,
			 'N'
			)
			*/
			iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
															sMaxStackBedGp,
															YmCommonConst.STACK_LAYER_GP_01,
															StringHelper.evl(colXyV.getFieldString("STACK_COL_RULE_X_AXIS"),"9999"),
															StringHelper.evl(colXyV.getFieldString("STACK_COL_RULE_Y_AXIS"),"9999"),
															"SYSTEM"});
		 	}else{
		 		if(sStackColGp.equals(YmCommonConst.STACK_COL_GP_3EKD02) && 
						(sStackBedGp.equals(YmCommonConst.STACK_BED_GP_21) || 
						 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_23) || 
						 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_24) || 
						 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_25) || 
						 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_26) || 
						 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_22)
						)
		 		  )
		 		{
			 		// SPM2추출 일 경우에만 처리되도록 한다.
			 		//iSeq = switchConveyorInfo(sStackColGp,sMaxStackBedGp);
			 		bSwitch = true;
			 		
		 		}
		 	}

			
		 	//***************************************************************************************
			//TB_YM_STACKLAYER TABLE정보를 가져온다.
			/*
			 select * 
			from tb_ym_stacklayer
			where stack_col_gp = :stack_col_gp
			*/
			sQueryId = "ym.common.YmCommonDB.getStackLayerList";
			listV	 = dao.getCommonList(sQueryId,new Object[]{sStackColGp});
			
			//sStackBedGp 값에 따라 특정번지에 정보를 UPDATE한다.
			if(YmCommonConst.GBN_MAX.equals(sStackBedGp)){			sStackBedGp = sMaxStackBedGp;
			}else if(YmCommonConst.GBN_MIN.equals(sStackBedGp)){	sStackBedGp = YmCommonConst.STACK_BED_GP_01;
			//==============================================================================================================
			// 최규성 2010-01-26
			}else if(sStackColGp.equals(YmCommonConst.STACK_COL_GP_3EKD02) && 
														(sStackBedGp.equals(YmCommonConst.STACK_BED_GP_21) || 
														 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_23) || 
														 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_24) || 
														 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_25) || 
														 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_26) || 
														 sStackBedGp.equals(YmCommonConst.STACK_BED_GP_22)
														)
					){			// SPM2 추출위치 고정으로 분기구문 추가함. 최규성 2010-01-26
				
				sStackBedGp = sStackBedGp;
				logger.println(LogLevel.DEBUG, "SPM2 추출위치: "+sStackBedGp);
			//==============================================================================================================
			}else{													
				if(sStackBedGp.compareTo(YmCommonConst.STACK_BED_GP_01) < 1){//01보다 적을 경우
					sStackBedGp = YmCommonConst.STACK_BED_GP_01;
   				}else if(sStackBedGp.compareTo(sMaxStackBedGp) > -1){//MAX보다 클경우
   					sStackBedGp = sMaxStackBedGp;
   				}else {
   					sStackBedGp = sStackBedGp;
   				}
			}
			
			logger.println(LogLevel.DEBUG, "SPM2 listV Data: "+listV);
			
			for(int inx = Integer.parseInt(sStackBedGp); inx < listV.size(); inx++){
				
			 	listJ 	 = (JDTORecord)listV.get(inx -1);
			 	/*
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
			 	logger.println(LogLevel.DEBUG, "for Loop :changeLayerFormat " + YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""), "P") );
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
				iSeq 	 = dao.updateData(sQueryId,
						   new Object[]{StringHelper.evl(listJ.getFieldString("STOCK_ID"),""),
	                                    StringHelper.evl(listJ.getFieldString("STACK_LAYER_STAT"),""),
									    StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),""),
									    YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""), "P"),
									    StringHelper.evl(listJ.getFieldString("STACK_LAYER_GP"),"")});
				
			}
			
			if(!"".equals(sStockId)){
				logger.println(LogLevel.DEBUG, "if !NULL.equals(sStockId) ");
				//TB_YM_STACKLAYER TABLE를 수정한다.
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
				iSeq	 = dao.updateData(sQueryId,new Object[]{sStockId,
				                                                YmCommonConst.STACK_LAYER_STAT_L,
																sStackColGp,
																sStackBedGp,
																YmCommonConst.STACK_LAYER_GP_01});
				
			}else{
				logger.println(LogLevel.DEBUG, "if NULL.equals(sStockId) ");
				//TB_YM_STACKLAYER TABLE를 수정한다.
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
				iSeq	 = dao.updateData(sQueryId,new Object[]{"",
				                                                YmCommonConst.STACK_LAYER_STAT_E,
																sStackColGp,
																sStackBedGp,
																YmCommonConst.STACK_LAYER_GP_01});
			}
		}
		// 정정실적 처리 부분의 메소드나 로직을 사용한다.
		if (bSwitch){
			iSeq = 99;
			
		}
		return iSeq;
		
	}
	/** 
     * 최규성 - 사용안함.
	 * 콘베이어 정보를 변경한다. 
     * SPM2의 추출위치에서 콘베이어에 대한 정보를 변경한다.
     * SPM2의 추출요구 위치에 정보가 존재하면 다른 곳으로 정보를 옮긴다.
     * @param  String	: 적치열
     * @param  String	: 저장품ID
     * @param  String	: 삭제시
     *                    	MIN  - +1 번지정보를 현 번지정보에 UPDATE 하고 
     *							   적치열 MAX 번지정보를 삭제
     *						MAX  - 적치열 MAX 번지정보를 삭제
     *						번지 - 해당 번지정보부터 +1번지정보를 현 번지정보에 UPDATE하고
     *							   적치열 MAX 번지정보를 삭제
     * @return String Message
     * @throws 
     */		
	
	public static int switchConveyorInfo_pub(String sStackColGp, String sStackBedGp/*, String sStockId*/){
		Logger logger = LogService.getInstance().getLogger("ym");
		logger.println(LogLevel.DEBUG, ">>switchConveyorInfo() - Start ( "+sStackColGp+sStackBedGp +")");
		int iSeq = -1;
		ymCommonDAO dao = ymCommonDAO.getInstance();
		// stacklayer 존재하는지 검사
		/*
			SELECT 
				   *
			FROM tb_ym_stacklayer
			WHERE stack_col_gp  	= :stack_col_gp
			AND   stack_bed_gp		= :stack_bed_gp 
			AND   stack_layer_gp	= :stack_layer_gp 
		*/
		String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";

		JDTORecord jtrLayerInfo = dao.getCommonInfo(sQueryId, new Object[]{sStackColGp,
																sStackBedGp,
																YmCommonConst.STACK_LAYER_GP_01 } );
		
		String sStockId_onlayer = StringHelper.evl(jtrLayerInfo.getFieldString("STOCK_ID"),"");
		String sStackLayerStat = StringHelper.evl(jtrLayerInfo.getFieldString("STACK_LAYER_STAT"),"");
		
		logger.println(LogLevel.DEBUG, ">>switchConveyorInfo() - 저장품 ( "+sStockId_onlayer+")");
		logger.println(LogLevel.DEBUG, ">>switchConveyorInfo() - 적치상태 ( "+sStackLayerStat+")");

		// stack_bed_gp,stock_id 비교
		// 기존 추출 위치의 정보를 변경한다.
		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
		iSeq	 = dao.updateData(sQueryId,new Object[]{"",
		                                                YmCommonConst.STACK_LAYER_STAT_E,
														sStackColGp,
														sStackBedGp,
														YmCommonConst.STACK_LAYER_GP_01});


		// 조건 : stacklayer에 stockId가 존재하는 경우에만 실행.
		// 최대 BED 위치 검사 26보다 큰 경우
		sQueryId = "ym.common.YmCommonDB.getMaxStackBedInfo_spm2";
		JDTORecord jtrMaxBed = dao.getCommonInfo(sQueryId,new Object[]{YmCommonConst.NEW_BAK_STACK_BED_START,
																		sStackColGp,
																		YmCommonConst.NEW_BAK_STACK_BED_START});

		String sMaxStackBedGp = StringHelper.evl(jtrMaxBed.getFieldString("MAXBED"),"");
		sMaxStackBedGp = YmCommonUtil.changeLayerFormat(sMaxStackBedGp,"P");
		//적치대정보가 존재 안 하는 경우 생성해준다.
		sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBSTACKER";
		List chkList = dao.getCommonList(sQueryId,new Object[]{sStackColGp,sMaxStackBedGp});
		
		if(chkList.size()<=0 ){
			//TB_YM_STACKER TABLE를 생성한다.
			sQueryId = "ym.common.YmCommonDB.createStakerInfo";
			/*
			 INSERT INTO tb_ym_stacker
			(
			 stack_col_gp,
			 stack_bed_gp,
			 stack_bed_active_stat,
			 register,
			 reg_ddtt,
			 del_yn
			)
			VALUES
			(
			 :stack_col_gp,
			 :stack_bed_gp,
			 'O',
			 :register,
			 sysdate,
			 'N'
			) 
			*/
			iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
														sMaxStackBedGp,
														"SYSTEM"});
		}

		//***************************************************************************************
		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";
		List chkList2 = dao.getCommonList(sQueryId,new Object[]{sStackColGp,
																sMaxStackBedGp,
							 									YmCommonConst.STACK_LAYER_GP_01});
		//dao.getCommonInfo(queryCode, objs);
		if(chkList2.size()<=0 ){
			//TB_YM_STACKLAYER TABLE를 생성한다.
			sQueryId = "ym.common.YmCommonDB.createStakLayerInfo";
			/*
			INSERT INTO tb_ym_stacklayer
			(
			 stack_col_gp,
			 stack_bed_gp,
			 stack_layer_gp,
			 stack_layer_active_stat,
			 stack_layer_stat,
			 STACK_LAYER_X_AXIS,
			 STACK_LAYER_Y_AXIS,
			 register,
			 reg_ddtt,
			 del_yn
			)
			VALUES
			(
			 :stack_col_gp,
			 :stack_bed_gp,
			 :stack_layer_gp,
			 'O',
			 'E',
			 :STACK_LAYER_X_AXIS,
			 :STACK_LAYER_Y_AXIS,
			 :register,
			 sysdate,
			 'N'
			)
			*/
			iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
															sMaxStackBedGp,
															YmCommonConst.STACK_LAYER_GP_01,
															YmCommonConst.TMP_STACK_COL_RULE_X_AXIS,
															YmCommonConst.TMP_STACK_COL_RULE_Y_AXIS,
															"SYSTEM"});
		}

		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
		iSeq	 = dao.updateData(sQueryId,new Object[]{sStockId_onlayer,
		                                                YmCommonConst.STACK_LAYER_STAT_L,
														sStackColGp,
														sMaxStackBedGp,
														YmCommonConst.STACK_LAYER_GP_01});


		// stacker 생성
		// stacklayer 생성
		// stockId,stackstat update
		logger.println(LogLevel.DEBUG, ">>switchConveyorInfo() - END" );
		
		return iSeq;
	}
	// 사용안함. 최규성 .2010-02-04
	// SPM2 추출 요구시 추출위치에 저장품이 존재('L')시 추출위치의 저장품을 26BED이후로 옮긴다.
	private int switchConveyorInfo(String sStackColGp, String sStackBedGp/*, String sStockId*/){
		Logger logger = LogService.getInstance().getLogger("ym");
		logger.println(LogLevel.DEBUG, this, ">>switchConveyorInfo() - Start ( "+sStackColGp+sStackBedGp +")");
		int iSeq = -1;
		ymCommonDAO dao = ymCommonDAO.getInstance();
		// stacklayer 존재하는지 검사
		/*
			SELECT 
				   *
			FROM tb_ym_stacklayer
			WHERE stack_col_gp  	= :stack_col_gp
			AND   stack_bed_gp		= :stack_bed_gp 
			AND   stack_layer_gp	= :stack_layer_gp 
		*/
		String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";

		JDTORecord jtrLayerInfo = dao.getCommonInfo(sQueryId, new Object[]{sStackColGp,
																sStackBedGp,
																YmCommonConst.STACK_LAYER_GP_01 } );
		
		String sStockId_onlayer = StringHelper.evl(jtrLayerInfo.getFieldString("STOCK_ID"),"");
		String sStackLayerStat = StringHelper.evl(jtrLayerInfo.getFieldString("STACK_LAYER_STAT"),"");
		
		logger.println(LogLevel.DEBUG, this, ">>switchConveyorInfo() - 저장품 ( "+sStockId_onlayer+")");
		logger.println(LogLevel.DEBUG, this, ">>switchConveyorInfo() - 적치상태 ( "+sStackLayerStat+")");

		// stack_bed_gp,stock_id 비교
		// 기존 추출 위치의 정보를 변경한다.
		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
		iSeq	 = dao.updateData(sQueryId,new Object[]{"",
		                                                YmCommonConst.STACK_LAYER_STAT_E,
														sStackColGp,
														sStackBedGp,
														YmCommonConst.STACK_LAYER_GP_01});


		// 조건 : stacklayer에 stockId가 존재하는 경우에만 실행.
		// 최대 BED 위치 검사 26보다 큰 경우
		sQueryId = "ym.common.YmCommonDB.getMaxStackBedInfo_spm2";
		JDTORecord jtrMaxBed = dao.getCommonInfo(sQueryId,new Object[]{YmCommonConst.NEW_BAK_STACK_BED_START,
																		sStackColGp,
																		YmCommonConst.NEW_BAK_STACK_BED_START});

		String sMaxStackBedGp = StringHelper.evl(jtrMaxBed.getFieldString("MAXBED"),"");
		logger.println(LogLevel.DEBUG, this, ">>switchConveyorInfo() - MaxBED ( "+sMaxStackBedGp+")");
		
		sMaxStackBedGp = YmCommonUtil.changeLayerFormat(sMaxStackBedGp,"P");
		logger.println(LogLevel.DEBUG, this, ">>switchConveyorInfo() - changeLayerFormat ( "+sMaxStackBedGp +")");
		
		//적치대정보가 존재 안 하는 경우 생성해준다.
		sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBSTACKER";
		List chkList = dao.getCommonList(sQueryId,new Object[]{sStackColGp,sMaxStackBedGp});
		
		if(chkList.size()<=0 ){
			//TB_YM_STACKER TABLE를 생성한다.
			sQueryId = "ym.common.YmCommonDB.createStakerInfo";
			/*
			 INSERT INTO tb_ym_stacker
			(
			 stack_col_gp,
			 stack_bed_gp,
			 stack_bed_active_stat,
			 register,
			 reg_ddtt,
			 del_yn
			)
			VALUES
			(
			 :stack_col_gp,
			 :stack_bed_gp,
			 'O',
			 :register,
			 sysdate,
			 'N'
			) 
			*/
			iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
														sMaxStackBedGp,
														"SYSTEM"});
		}

		//***************************************************************************************
		//***************************************************************************************
		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";
		List chkList2 = dao.getCommonList(sQueryId,new Object[]{sStackColGp,
																sMaxStackBedGp,
							 									YmCommonConst.STACK_LAYER_GP_01});
		//dao.getCommonInfo(queryCode, objs);
		if(chkList2.size()<=0 ){
			//TB_YM_STACKLAYER TABLE를 생성한다.
			sQueryId = "ym.common.YmCommonDB.createStakLayerInfo";
			/*
			INSERT INTO tb_ym_stacklayer
			(
			 stack_col_gp,
			 stack_bed_gp,
			 stack_layer_gp,
			 stack_layer_active_stat,
			 stack_layer_stat,
			 STACK_LAYER_X_AXIS,
			 STACK_LAYER_Y_AXIS,
			 register,
			 reg_ddtt,
			 del_yn
			)
			VALUES
			(
			 :stack_col_gp,
			 :stack_bed_gp,
			 :stack_layer_gp,
			 'O',
			 'E',
			 :STACK_LAYER_X_AXIS,
			 :STACK_LAYER_Y_AXIS,
			 :register,
			 sysdate,
			 'N'
			)
			*/
			iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
															sMaxStackBedGp,
															YmCommonConst.STACK_LAYER_GP_01,
															YmCommonConst.TMP_STACK_COL_RULE_X_AXIS,
															YmCommonConst.TMP_STACK_COL_RULE_Y_AXIS,
															"SYSTEM"});
		}

		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
		iSeq	 = dao.updateData(sQueryId,new Object[]{sStockId_onlayer,
		                                                YmCommonConst.STACK_LAYER_STAT_L,
														sStackColGp,
														sMaxStackBedGp,
														YmCommonConst.STACK_LAYER_GP_01});


		// stacker 생성
		// stacklayer 생성
		// stockId,stackstat update
		logger.println(LogLevel.DEBUG, this, ">>switchConveyorInfo() - END" );
		
		return iSeq;
	}
	/** 
     * YJK
	 * 콘베이어 정보를 삭제한다. 
     *
     * @param  String	: 적치열
     * @param  String	: 저장품ID
     * @param  String	: 삭제시
     *                    	MIN  - +1 번지정보를 현 번지정보에 UPDATE 하고 
     *							   적치열 MAX 번지정보를 삭제
     *						MAX  - 적치열 MAX 번지정보를 삭제
     *						번지 - 해당 번지정보부터 +1번지정보를 현 번지정보에 UPDATE하고
     *							   적치열 MAX 번지정보를 삭제
     * @return String Message
     * @throws 
     */			
	public static int deleteConveyorInfo(String sStackColGp,
					 		   		     String sStockId){
		String sMaxStackBedGp 			= "";
		String sCurStackBedGp			= "";
		String sQueryId 				= "";
		
		JDTORecord maxBedV  			= null;
		JDTORecord curBedV  			= null;
		JDTORecord listJ  				= null;
		
		List listV 						= null;
		
		int iSeq 						= -1;
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		
		//2009.11.30 설비인 경우에만 삭제 가능 정종균::::
		String sPutUsageCd = YmCommonUtil.getStackColInfoWithPk(sStackColGp);
		
   		if(YmCommonConst.STACK_COL_USAGE_CD_XX.equals(sPutUsageCd)	||// COIL 비상적치위치	
   					YmCommonConst.STACK_COL_USAGE_CD_CC.equals(sPutUsageCd)	||// COIL 분기콘베이어
   					YmCommonConst.STACK_COL_USAGE_CD_CE.equals(sPutUsageCd)	||// COIL 확장콘베이어
				   YmCommonConst.STACK_COL_USAGE_CD_FE.equals(sPutUsageCd)	||// COIL HFL보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_FI.equals(sPutUsageCd)	||// COIL HFLTAKEIN위치
				   YmCommonConst.STACK_COL_USAGE_CD_FD.equals(sPutUsageCd)	||// COIL HFL추출위치
				   YmCommonConst.STACK_COL_USAGE_CD_KE.equals(sPutUsageCd)	||// COIL SPM보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_QE.equals(sPutUsageCd)	||// COIL EQL보급위치
				   YmCommonConst.STACK_COL_USAGE_CD_QD.equals(sPutUsageCd)	||// COIL EQL추출위치
				   YmCommonConst.STACK_COL_USAGE_CD_KI.equals(sPutUsageCd)	||// COIL SPMTAKEIN위치
				   YmCommonConst.STACK_COL_USAGE_CD_SE.equals(sPutUsageCd)	||// SLAB Scafing 입측
				   YmCommonConst.STACK_COL_USAGE_CD_SD.equals(sPutUsageCd)	||// SLAB Scafing 출측
				   YmCommonConst.STACK_COL_USAGE_CD_KD.equals(sPutUsageCd)	){// COIL SPM추출위치
   		//*********************************************************************************************	
 		
		//적치열 정보를 가져온다.
		//sStackBedGp 값에 따라 정보를 UPDATE한다.
		//적치열 MAX 번지정보를 삭제한다.
		
		//적치열 MAX번지 정보를 가져온다.
		if( sStackColGp.equals("3EKD02"/*YmCommonConst.STACK_COL_GP_3EKD02*/ )){
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId";
			JDTORecord curStockBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,sStockId});
			sMaxStackBedGp = StringHelper.evl(curStockBedV.getFieldString("STACK_BED_GP"),"");	
	    }else{
		sQueryId = "ym.common.YmCommonDB.getMaxStackBedInfo";
		maxBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp});
		
	    //적치열 MAX+1번지 정보를 생성한다.
	    sMaxStackBedGp = StringHelper.evl(maxBedV.getFieldString("MAXBED"),"");		
		}
	    //TB_YM_STACKLAYER TABLE정보를 가져온다.
		sQueryId = "ym.common.YmCommonDB.getStackLayerList";
		listV	 = dao.getCommonList(sQueryId,new Object[]{sStackColGp});
		
		//적치열에 저장품 ID가 있는 적치번지 정보를 가져온다.
		sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId";
		curBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,sStockId});
		
		if(curBedV != null){
			 //적치열 저장품ID 번지 정보를 생성한다.
		    sCurStackBedGp = StringHelper.evl(curBedV.getFieldString("STACK_BED_GP"),"");		
		    int sCHK =listV.size();
			if(!"".equals(sCurStackBedGp)){
				
				for(int inx = Integer.parseInt(sCurStackBedGp); inx < listV.size(); inx++){
					 
				 	listJ 	 = (JDTORecord)listV.get(inx);
				 	
				 	//맵정보가 존재 안 하는 상태에서 update를 하는 경우 코일위치정보가 없어 진다. 20091124 정종균
				 	//########################################################################################
				 	//****************************************************************************************
				 	//stack열이 존재 안 하는 경우 생성해준다.
				 	sQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBSTACKER";
				 	List chkList = dao.getCommonList(sQueryId,new Object[]{StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),""),
				 														   YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""), "M")});
				 	
				 	if(chkList.size()<=0 ){
				 	//TB_YM_STACKER TABLE를 생성한다.
					sQueryId = "ym.common.YmCommonDB.createStakerInfo";
					/*
					 INSERT INTO tb_ym_stacker
					(
					 stack_col_gp,
					 stack_bed_gp,
					 stack_bed_active_stat,
					 register,
					 reg_ddtt,
					 del_yn
					)
					VALUES
					(
					 :stack_col_gp,
					 :stack_bed_gp,
					 'O',
					 :register,
					 sysdate,
					 'N'
					) 
					*/
					iSeq	 = dao.insertData(sQueryId,new Object[]{StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),""),
																	YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""), "M"),
																	"SYSTEM"});
				 	}
				 	//****************************************************************************************
					
					
				 	//****************************************************************************************
				 	sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";
				 	List chkList2 = dao.getCommonList(sQueryId,new Object[]{StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),""),
										 									YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""), "M"),
										 									YmCommonConst.STACK_LAYER_GP_01});
				 	
				 	if(chkList2.size()<=0 ){
					
					//적치열 X,Y 좌표 정보를 가져온다.
					/*
					SELECT 
					  *
					  FROM tb_ym_stackcol
					 WHERE stack_col_gp  	= :stack_col_gp
					*/
					JDTORecord colXyV  				= null;
					sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackColInfoWithPk";
					 colXyV	 = dao.getCommonInfo(sQueryId,new Object[]{StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),"")});
					
					//TB_YM_STACKLAYER TABLE를 생성한다.
					sQueryId = "ym.common.YmCommonDB.createStakLayerInfo";
					/*
					INSERT INTO tb_ym_stacklayer
					(
					 stack_col_gp,
					 stack_bed_gp,
					 stack_layer_gp,
					 stack_layer_active_stat,
					 stack_layer_stat,
					 STACK_LAYER_X_AXIS,
					 STACK_LAYER_Y_AXIS,
					 register,
					 reg_ddtt,
					 del_yn
					)
					VALUES
					(
					 :stack_col_gp,
					 :stack_bed_gp,
					 :stack_layer_gp,
					 'O',
					 'E',
					 :STACK_LAYER_X_AXIS,
					 :STACK_LAYER_Y_AXIS,
					 :register,
					 sysdate,
					 'N'
					)
					*/
					iSeq	 = dao.insertData(sQueryId,new Object[]{StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),""),
																	YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""), "M"),
																	YmCommonConst.STACK_LAYER_GP_01,
																	StringHelper.evl(colXyV.getFieldString("STACK_COL_RULE_X_AXIS"),"9999"),
																	StringHelper.evl(colXyV.getFieldString("STACK_COL_RULE_Y_AXIS"),"9999"),
																	"SYSTEM"});
				 	}
				 	//****************************************************************************************
				 	//########################################################################################
					
					/*
					UPDATE TB_YM_STACKLAYER
					SET 
						stock_id		    = :stock_id,
						stack_layer_stat	    = :stack_layer_stat,
						modifier   = 'SYSTEM',
					 	mod_ddtt   = sysdate     
					WHERE stack_col_gp   = :stack_col_gp 
					AND   stack_bed_gp   = :stack_bed_gp 
					AND   stack_layer_gp = :stack_layer_gp
										 
					*/				 	
					sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
					iSeq 	 = dao.updateData(sQueryId,
							   new Object[]{StringHelper.evl(listJ.getFieldString("STOCK_ID"),""),
		                                    StringHelper.evl(listJ.getFieldString("STACK_LAYER_STAT"),""),
										    StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),""),
										    YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""), "M"),
										    StringHelper.evl(listJ.getFieldString("STACK_LAYER_GP"),"")});
				}
				
				//TB_YM_STACKER TABLE를 삭제한다.
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackerInfo";
				iSeq	 = dao.deleteData(sQueryId,new Object[]{sStackColGp,
																sMaxStackBedGp});
				//TB_YM_STACKLAYER TABLE를 삭제한다.
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackLayerInfo";
				iSeq	 = dao.deleteData(sQueryId,new Object[]{sStackColGp,
																sMaxStackBedGp,
																YmCommonConst.STACK_LAYER_GP_01});
				

				//설비위치 삭제 방식 변경 작업 
				
				//TB_YM_STACKER TABLE를 삭제한다.
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackerInfoNEW";
				int  iSeq2	 = dao.deleteData(sQueryId,new Object[]{sStackColGp,sStockId});
		 
				//TB_YM_STACKLAYER TABLE를 삭제한다.
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackLayerInfoNEW";
				iSeq2	 = dao.deleteData(sQueryId,new Object[]{sStackColGp,sStockId});
			}		
		}			
   		}
		return iSeq;
	}
	/** 
     * 최규성
	 * 콘베이어 정보를 삭제한다. 
     *
     * @param  String	: 적치열
     * @param  String	: 삭제시
     *                    	MIN  - +1 번지정보를 현 번지정보에 UPDATE 하고 
     *							   적치열 MAX 번지정보를 삭제
     *						MAX  - 적치열 MAX 번지정보를 삭제
     *						번지 - 해당 번지정보부터 +1번지정보를 현 번지정보에 UPDATE하고
     *							   적치열 MAX 번지정보를 삭제
     * @return String Message
     * @throws 
     */			
	public static int deleteConveyorInfo(String sStackColGp/*, String sStockId*/){
		String sMaxStackBedGp 			= "";
		String sCurStackBedGp			= "";
		String sQueryId 				= "";
		
		JDTORecord maxBedV  			= null;
		//JDTORecord curBedV  			= null;
		JDTORecord listJ  				= null;
		
		List listV 						= null;
		
		int iSeq 						= -1;
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		//적치열 정보를 가져온다.
		//sStackBedGp 값에 따라 정보를 UPDATE한다.
		//적치열 MAX 번지정보를 삭제한다.
		
		//적치열 MAX번지 정보를 가져온다.
		sQueryId = "ym.common.YmCommonDB.getMaxStackBedInfo";
		maxBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp});
		
		//적치열 MAX+1번지 정보를 생성한다.
		sMaxStackBedGp = StringHelper.evl(maxBedV.getFieldString("MAXBED"),"");		
		
		//TB_YM_STACKLAYER TABLE정보를 가져온다.
		sQueryId = "ym.common.YmCommonDB.getStackLayerList";
		listV	 = dao.getCommonList(sQueryId,new Object[]{sStackColGp});
		
		//적치열에 저장품 ID가 있는 적치번지 정보를 가져온다.
		//sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithStockId";
		//curBedV	 = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,sStockId});
		
		//if(curBedV != null){
		//적치열 저장품ID 번지 정보를 생성한다.
		//sCurStackBedGp = StringHelper.evl(curBedV.getFieldString("STACK_BED_GP"),"");		
		sCurStackBedGp="01"; 
		if(!"".equals(sCurStackBedGp)){
		
			for(int inx = Integer.parseInt(sCurStackBedGp); inx < listV.size(); inx++){
				
				listJ 	 = (JDTORecord)listV.get(inx);
				/*
				UPDATE TB_YM_STACKLAYER
				SET 
				stock_id		    = :stock_id,
				stack_layer_stat	    = :stack_layer_stat,
				modifier   = 'SYSTEM',
				mod_ddtt   = sysdate     
				WHERE stack_col_gp   = :stack_col_gp 
				AND   stack_bed_gp   = :stack_bed_gp 
				AND   stack_layer_gp = :stack_layer_gp
					 
				*/				 	
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
				iSeq 	 = dao.updateData(sQueryId, 
											new Object[]{StringHelper.evl(listJ.getFieldString("STOCK_ID"),""),
														 StringHelper.evl(listJ.getFieldString("STACK_LAYER_STAT"),""),
													     StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),""),
													     YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""), "M"),
													     StringHelper.evl(listJ.getFieldString("STACK_LAYER_GP"),"")});
			}
		
			//TB_YM_STACKER TABLE를 삭제한다.
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackerInfo";
			iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
															sMaxStackBedGp});
			//TB_YM_STACKLAYER TABLE를 삭제한다.
			sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.deleteStackLayerInfo";
			iSeq	 = dao.insertData(sQueryId,new Object[]{sStackColGp,
															sMaxStackBedGp,
															YmCommonConst.STACK_LAYER_GP_01});
			}		
//		}
		
		
		return iSeq;
	}	
	
	
	
	/*
	 * COIL
	 * 바로 위 상단 상태정보를 '적치가능' 으로 UPDATE
	 */
	public static int setCoilUpperState_E(String sStackColGp,
									 	  String sStackBedGp,
									 	  String sStackLayerGp){
		int iSeq 		= 0;
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		{
	    	/**
			 *	앞단 적치정보를 가져온다.
			 * 	적치단 상태가 'L','P' 이면
			 *	상단 적치단 정보를 적치가능상태로 변경
			 */
			String sQueryId 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";
			JDTORecord layerJr	= dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
							    										  YmCommonUtil.changeLayerFormat(sStackBedGp, "M"),
							    										  sStackLayerGp});
			if(layerJr != null){
	    		
	    		String sTmpState   = StringHelper.evl(layerJr.getFieldString("STACK_LAYER_STAT"), "");
	    		String sTmpStockId = StringHelper.evl(layerJr.getFieldString("STOCK_ID"), "");
	    		
	    		if(YmCommonConst.STACK_LAYER_STAT_L.equals(sTmpState)||
	    		   YmCommonConst.STACK_LAYER_STAT_P.equals(sTmpState)){
	    		   	
	    		   	JDTORecord lyrJr = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
									    									   YmCommonUtil.changeLayerFormat(sStackBedGp, "M"),
									    									   YmCommonUtil.changeLayerFormat(sStackLayerGp, "P")});
		    		
		    		/**
		    		 * 작업충돌로 저장품 정보가 존재하면 
		    		 * 셋팅하지 않는다.
		    		 */
		    		if(lyrJr != null && 
		    		   "".equals(StringHelper.evl(lyrJr.getFieldString("STOCK_ID"), ""))){
		    			/* 
						 * 적치단 상태를 적치가능상태로 변경
						 * tb_ym_stacklayer Table : stock_id = ''(Empty)
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
						 */	
						sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
						iSeq 	 = dao.updateData(sQueryId,new Object[]{"",
							    										YmCommonConst.STACK_LAYER_STAT_E,
							    										sStackColGp,
									                                    YmCommonUtil.changeLayerFormat(sStackBedGp, "M"),
							    										YmCommonUtil.changeLayerFormat(sStackLayerGp, "P")});
		 			}
		 		}
	    	}
	    }	
    	
    	{
	    	/**
			 *	뒷단 적치정보를 가져온다.
			 * 	적치단 상태가 'L','P' 이면
			 *	상단 적치단 정보를 적치가능상태로 변경
			 */
			String sQueryId 	= "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";
			JDTORecord layerJr	= dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
							    										  YmCommonUtil.changeLayerFormat(sStackBedGp, "P"),
							    										  sStackLayerGp});
	    	if(layerJr != null){							 
		    	
	    		String sTmpState   = StringHelper.evl(layerJr.getFieldString("STACK_LAYER_STAT"), "");
	    		String sTmpStockId = StringHelper.evl(layerJr.getFieldString("STOCK_ID"), "");
	    		
	    		if(YmCommonConst.STACK_LAYER_STAT_L.equals(sTmpState)||
	    		   YmCommonConst.STACK_LAYER_STAT_P.equals(sTmpState)){
	    		   	
	    		   	JDTORecord lyrJr = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
									    									   sStackBedGp,
									    									   YmCommonUtil.changeLayerFormat(sStackLayerGp, "P")});
		    		/**
		    		 * 작업충돌로 저장품 정보가 존재하면 
		    		 * 셋팅하지 않는다.
		    		 */
		    		if(lyrJr != null && 
		    		   "".equals(StringHelper.evl(lyrJr.getFieldString("STOCK_ID"), ""))){
		    			/* 
						 * 적치단 상태를 적치가능상태로 변경
						 * tb_ym_stacklayer Table : stock_id = ''(Empty)
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'E'(적치가능)
						 */	
				    	sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
						iSeq 	 = dao.updateData(sQueryId,new Object[]{"",
																		YmCommonConst.STACK_LAYER_STAT_E,
																		sStackColGp,
							    										sStackBedGp,
							    										YmCommonUtil.changeLayerFormat(sStackLayerGp, "P")});									 
			    	}
			    }
	    	}	
    	}
    	
    	return iSeq;
	}
	
	/*
	 * COIL
	 * 바로 위 상단 상태정보를 '적치불가' 으로 UPDATE
	 */
	public static int setCoilUpperState_V(String sStackColGp,
									 	  String sStackBedGp,
									 	  String sStackLayerGp){
		int iSeq 		= 0;
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		{
			/*
			 	SELECT 
					   *
				FROM tb_ym_stacklayer
				WHERE stack_col_gp  	= :stack_col_gp
				AND   stack_bed_gp		= :stack_bed_gp 
				AND   stack_layer_gp	= :stack_layer_gp 
			 */
	    	String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";	   	
		   	JDTORecord lyrJr = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
							    									   YmCommonUtil.changeLayerFormat(sStackBedGp, "M"),
							    									   YmCommonUtil.changeLayerFormat(sStackLayerGp, "P")});
    		if(lyrJr != null){
    			/**
	    		 * 작업충돌로 저장품 정보가 존재하면 
	    		 * 권상시점에서 셋팅하지 않는다.
	    		 */
	    		if("".equals(StringHelper.evl(lyrJr.getFieldString("STOCK_ID"), ""))){ 	
		    		/* 
					 * 적치단 UP위치의 2단 -1번지 Clear
					 * tb_ym_stacklayer Table : stock_id = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'V'(하단에 적치되지 않은 위치)
					 */	
					sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
					iSeq 	 = dao.updateData(sQueryId,new Object[]{"",
						    										YmCommonConst.STACK_LAYER_STAT_V,
						    										sStackColGp,
								                                    YmCommonUtil.changeLayerFormat(sStackBedGp, "M"),
						    										YmCommonUtil.changeLayerFormat(sStackLayerGp, "P")});
	    		}
	    	}
		}	
    	
    	{
	    	String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";	   	
		   	JDTORecord lyrJr = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
							    									   sStackBedGp,
							    									   YmCommonUtil.changeLayerFormat(sStackLayerGp, "P")});
    		if(lyrJr != null){							 
		    	/**
	    		 * 작업충돌로 저장품 정보가 존재하면 
	    		 * 권상시점에서 셋팅하지 않는다.
	    		 */
	    		if("".equals(StringHelper.evl(lyrJr.getFieldString("STOCK_ID"), ""))){ 	
			    	/* 
					 * 적치단 UP위치의 2단 동일번지 Clear
					 * tb_ym_stacklayer Table : stock_id = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'V'(하단에 적치되지 않은 위치)
					 */	
			    	sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
					iSeq 	 = dao.updateData(sQueryId,new Object[]{"",
																	YmCommonConst.STACK_LAYER_STAT_V,
																	sStackColGp,
						    										sStackBedGp,
						    										YmCommonUtil.changeLayerFormat(sStackLayerGp, "P")});			
	    		}
	    	}
    	}
    	
    	return iSeq;
	}
	
	/*
	 * SLAB
	 * 바로 위 상단 상태정보를 '적치가능' 으로 UPDATE
	 */
	public static int setSlabUpperState_E(String sStackColGp,
								 		  String sStackBedGp,
								 		  String sStackLayerGp){
		int iSeq = 0;
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";	   	
	   	JDTORecord lyrJr = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
																   sStackBedGp,
																   YmCommonUtil.changeLayerFormat(sStackLayerGp,"P")});
		if(lyrJr != null){
			
			/**
    		 * 작업충돌로 저장품 정보가 존재하면 
    		 * 권상시점에서 셋팅하지 않는다.
    		 */
    		if("".equals(StringHelper.evl(lyrJr.getFieldString("STOCK_ID"), ""))){ 	
				/*
				 * 적치단 UP위치의 바로 위 상단 번지 Clear
				 * tb_ym_stacklayer Table : stock_id = ''(Empty)
				 * tb_ym_stacklayer Table : stack_layer_stat	   = 'V'(하단에 적치되지 않은 위치)
				 */	
				sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
				iSeq 	 = dao.updateData(sQueryId,new Object[]{"",
															 	YmCommonConst.STACK_LAYER_STAT_E,
															 	sStackColGp,
															 	sStackBedGp,
															 	YmCommonUtil.changeLayerFormat(sStackLayerGp,"P")});
			}
		}
		
		return iSeq;
	}
	
	/*
	 * SLAB
	 * 바로 위 상단 상태정보를 '적치불가' 으로 UPDATE
	 */
	public static int setSlabUpperState_V(String sStackColGp,
								 		  String sStackBedGp,
								 		  String sStackLayerGp){
		int iSeq = 0;
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		{
			String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";	   	
		   	JDTORecord lyrJr = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
																	   sStackBedGp,
																	   YmCommonUtil.changeLayerFormat(sStackLayerGp,"M")});
			if(lyrJr != null){
				
				/**
	    		 * 상단정보 수정시에 하단이 적치가능이면('E')
	    		 * 현위치정보도 적치불가('V')로 셋팅한다.
	    		 */
	    		if("".equals(StringHelper.evl(lyrJr.getFieldString("STOCK_ID"), ""))){ 	
					/*
					 * 적치단 UP위치의 바로 위 상단 번지 Clear
					 * tb_ym_stacklayer Table : stock_id = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'V'(하단에 적치되지 않은 위치)
					 */	
					sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
					iSeq 	 = dao.updateData(sQueryId,new Object[]{"",
																 	YmCommonConst.STACK_LAYER_STAT_V,
																 	sStackColGp,
																 	sStackBedGp,
																 	sStackLayerGp});
				}
			}
		}
		
		{
			String sQueryId  = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getStackLayerInfoWithPk";	   	
		   	JDTORecord lyrJr = dao.getCommonInfo(sQueryId,new Object[]{sStackColGp,
																	   sStackBedGp,
																	   YmCommonUtil.changeLayerFormat(sStackLayerGp,"P")});
			if(lyrJr != null){
				
				/**
	    		 * 작업충돌로 저장품 정보가 존재하면 
	    		 * 권상시점에서 셋팅하지 않는다.
	    		 */
	    		if("".equals(StringHelper.evl(lyrJr.getFieldString("STOCK_ID"), ""))){ 	
					/*
					 * 적치단 UP위치의 바로 위 상단 번지 Clear
					 * tb_ym_stacklayer Table : stock_id = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat	   = 'V'(하단에 적치되지 않은 위치)
					 */	
					sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateCraneStackLayerStat";
					iSeq 	 = dao.updateData(sQueryId,new Object[]{"",
																 	YmCommonConst.STACK_LAYER_STAT_V,
																 	sStackColGp,
																 	sStackBedGp,
																 	YmCommonUtil.changeLayerFormat(sStackLayerGp,"P")});
				}
			}
		}
		
		return iSeq;
	}
		
	/* 
	 * 적치단 Put위치에 다른 코일이 있을 경우.
	 * 해당동의 XX번지로 저장품 MAP을 수정한다.
	 */	
	public static int updateLegacyStockId_Coil(CraneSchDAO dao,
											   String sPutStackColGp,
											   String sPutStackBedGp,
											   String sPutStackLayerGp,
											   String sCurStockId) {
		
		Logger logger = LogService.getInstance().getLogger("ym");
		
		int iSeq = 1;
		 
		JDTORecord putJr = dao.getStackLayerInfoWithPk(sPutStackColGp,
			    									   sPutStackBedGp,
			    									   sPutStackLayerGp);
		
		String sToStockId 	= "00";
		String sToStat 		= "";
		
		if(putJr != null){
			
			sToStockId 	= StringHelper.evl(putJr.getFieldString("STOCK_ID"), "");
			sToStat 	= StringHelper.evl(putJr.getFieldString("STACK_LAYER_STAT"), "");
		}
		
		
		if(!"".equals(sToStockId)&&
		   !sCurStockId.equals(sToStockId)){ 	
			
			/*
			*  2007.03.19 이정훈
	        *  Saddle로 산적 위치 수정 할 때 이상 적치로 빼는 거  Skip
			*/
			if("0".equals(sToStockId.substring(0,1)))
			{
				return 1;
			}
			
			if(!YmCommonConst.STACK_LAYER_STAT_P.equals(sToStat)){ 
				
				logger.println(LogLevel.DEBUG, "권하이상/산적 수정=> TO 위치에 저장품 정보 존재함");
				/**
				 * TO 위치에 존재하는 저장품 처리
				 */
//				{ 
//					String sTempLayer = sPutStackColGp.substring(0,2)+
//										YmCommonConst.STACK_COL_USAGE_CD_XX+
//										YmCommonConst.STACK_BED_GP_01;
//					
//					try{ 
//						iSeq = YmCommonDB.insertConveyorInfo(sTempLayer,
//															 sToStockId,
//															 YmCommonConst.STACK_BED_GP_01);
//						/*
//						 * Coil 공통 Table 저장위치 Update 
//						 */	 
//						iSeq = dao.updateCoilCommonLocInfo(sToStockId,sTempLayer+
//						 											  YmCommonConst.STACK_BED_GP_01+
//						 											  YmCommonConst.STACK_LAYER_GP_01);  	   
//							
//				    }catch(Exception e){
//				    	logger.println(LogLevel.DEBUG, "권하이상/산적 수정=> 비상적치장 ERROR"+e);
//				    }
//				    logger.println(LogLevel.DEBUG, "권하이상/산적 수정=> 비상적치장 등록완료="+iSeq);
//      					    
//				}	
				
				return -1;
				
			}else{
				return 1;
			}
		}
		return iSeq;
	}	
	
	/* 
	 * 적치단 Put위치에 다른 SLAB가 있을 경우.
	 * 해당동의 XX번지로 저장품 MAP을 수정한다.
	 */	
	public static int updateLegacyStockId_Slab(CraneSchDAO dao,
											   String sPutStackColGp,
											   String sPutStackBedGp,
											   String sPutStackLayerGp,
											   String sCurStockId) {
		
		Logger logger = LogService.getInstance().getLogger("ym");
		
		int iSeq = -1;
		
		//W/B인 경우 생략 
		if(sPutStackColGp.substring(2,4).equals("WB")){
			return 1;
		}
		 
		JDTORecord putJr = dao.getStackLayerInfoWithPk(sPutStackColGp,
			    									   sPutStackBedGp,
			    									   sPutStackLayerGp);
		
		String sToStockId 	= "";
		String sToStat 		= "";
		
		if(putJr != null){
			
			sToStockId 	= StringHelper.evl(putJr.getFieldString("STOCK_ID"), "");
			sToStat 	= StringHelper.evl(putJr.getFieldString("STACK_LAYER_STAT"), "");
		}
		
		if(!"".equals(sToStockId)&&
		   !sCurStockId.equals(sToStockId)){ 	
			
			if(!YmCommonConst.STACK_LAYER_STAT_P.equals(sToStat)){ 
				
				logger.println(LogLevel.DEBUG, "권하이상/산적 수정=> TO 위치에 저장품 정보 존재함");
				/**
				 * TO 위치에 존재하는 저장품 처리
				 */
				{ 
					String sTempLayer = sPutStackColGp.substring(0,2)+
										YmCommonConst.STACK_COL_USAGE_CD_XX+
										YmCommonConst.STACK_BED_GP_01;
					
					try{ 
						iSeq = YmCommonDB.insertConveyorInfo(sTempLayer,
															 sToStockId,
															 YmCommonConst.STACK_BED_GP_01);
						/*
						 * Coil 공통 Table 저장위치 Update 
						 */	 
						iSeq = dao.updateSlabCommonLocInfo(sToStockId,sTempLayer+
						 											  YmCommonConst.STACK_BED_GP_01+
						 											  YmCommonConst.STACK_LAYER_GP_01);  	   
							
				    }catch(Exception e){
				    	logger.println(LogLevel.DEBUG, "권하이상/산적 수정=> 비상적치장 ERROR"+e);
				    }
				    logger.println(LogLevel.DEBUG, "권하이상/산적 수정=> 비상적치장 등록완료="+iSeq);
      					    
				}		 
			}
		}
		return iSeq;
	}	
	
	/* 
	 * 적치단 Put위치정보부터 상단으로 정보를 SHIFT한다.
	 */	
	public static int updateLegacyStockId_Slab_01(CraneSchDAO dao,
												  String sStackColGp,
												  String sStackBedGp,
												  String sStackLayerGp,
												  String sStockId) {
		
		Logger logger = LogService.getInstance().getLogger("ym");
		
		JDTORecord listJ = null;
		
		int iSeq = -1;
		
		List listV = dao.getStackLayerInfoWithBed(sStackColGp,
												  sStackBedGp);
		
		for(int inx = Integer.parseInt(sStackLayerGp); inx < listV.size(); inx++){
				
		 	listJ 	 = (JDTORecord)listV.get(inx -1);
		 	
		 	iSeq 	 = dao.updateCraneStackLayerStat(StringHelper.evl(listJ.getFieldString("STACK_COL_GP"),""),
												     StringHelper.evl(listJ.getFieldString("STACK_BED_GP"),""),
												     YmCommonUtil.changeLayerFormat(StringHelper.evl(listJ.getFieldString("STACK_LAYER_GP"),""), "P"),
												     StringHelper.evl(listJ.getFieldString("STOCK_ID"),""),
				                                     StringHelper.evl(listJ.getFieldString("STACK_LAYER_STAT"),""));
		}
		
		if(!"".equals(sStockId)){
			
			iSeq 	 = dao.updateCraneStackLayerStat(sStackColGp,
													 sStackBedGp,
													 sStackLayerGp,
													 sStockId,
			                                         YmCommonConst.STACK_LAYER_STAT_L);
			
		} 
		
		return iSeq;
	}	
	
}