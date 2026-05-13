package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.util.ArrayList;
import java.util.List;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService;
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.jms.model.CommonModel;
import com.inisteel.cim.common.jms.model.pc.ZZPC001;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.dao.ymCommonDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="SRTSpplyWrkOrdRegEJB" jndi-name="JNDISRTSpplyWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class SRTSpplyWrkOrdRegSBean extends BaseSessionBean{
	private Logger logger = null;
	private CraneSchDAO dao = null;
	private YmComm ymComm = new YmComm();	
	
	public void ejbCreate() {
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		logger 	= new Logger(config);
		dao 	= new CraneSchDAO();
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSRTSpplyWrkOrd(String sMessage) throws java.rmi.RemoteException{ 
		/*
	     * Mill LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	     * 전문내용을 JDTORecord로 파싱한다.
	     * 업무 로직
	     *	1.TC_CD - CF1PB26 (I/F ID : YM-BIF-036)
	     *	2.Mill 조업시스템으로부터 SLAB Loading Request 정보를 수신
	     *	3.Slab 재료정보를 야드 저장품 Table에 존재하는지 Read한다.
	     *	4.작업예약 Table에 해당하는 Slab가 존재하면 Update하고 존재하지 않으면 Insert한다. 
	     * 
	     *  CF1PB262005-09-2211:11:11I0046HD00320     1
	     * 
	     * @param TC_CD(전문메세지)
	     * @return true(성공),false(실패)
	     * @throws
	     * 
	     * logger.println(LogLevel.DEBUG, "전문코드	  ==" +jDTORecord.getFieldString("전문코드"));
	     * logger.println(LogLevel.DEBUG, "발생일자	  ==" +jDTORecord.getFieldString("발생일자"));
	     * logger.println(LogLevel.DEBUG, "발생시간   ==" +jDTORecord.getFieldString("발생시간"));
	     * logger.println(LogLevel.DEBUG, "전문구분	  ==" +jDTORecord.getFieldString("전문구분"));  // I : Initialize, U : Update, D : Delete, R : Re-request
	     * logger.println(LogLevel.DEBUG, "전문길이	  ==" +jDTORecord.getFieldString("전문길이"));
	     * logger.println(LogLevel.DEBUG, "SLABNo     ==" +jDTORecord.getFieldString("SLABNo"));
	     * logger.println(LogLevel.DEBUG, "SPARE	  ==" +jDTORecord.getFieldString("SPARE"));
	     * 1:#1CTC B동, 2:#2CTC C동 3:#3CTC B동, 4:#4CTC C동 
	     * logger.println(LogLevel.DEBUG, "Position   ==" +jDTORecord.getFieldString("Position"));  
	     */        
        boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

			String sPosition   = StringHelper.evl(jDTORecord.getFieldString("Position"), "");
			String SlabNo      = StringHelper.evl(jDTORecord.getFieldString("SLABNo"), "").trim();
			
			/* 
			 *	장입 대상 슬라브를 CTC설비에 안올리고 싶을때 화면에서 처리.  
			 *	TB_YM_EQUIP(2CWB01)  HMI_STAT MODE 체크 
			 */
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO(); 
			String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
			JDTORecord wbJr = ydStackLayerDAO.requestgetData(sQuery1, 
														new Object[]{ YmCommonConst.STACK_COL_GP_2ACT02 });

			String sHmiStat  = "";
			
			if (wbJr != null){ 
				sHmiStat  = StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
			}
			
			logger.println(LogLevel.DEBUG,this, "CTC보급요구=>CTC 보급모드 상태=."+sHmiStat);	
			
			if ("".equals(sHmiStat)||
			    "C".equals(sHmiStat)){
			    	
			    logger.println(LogLevel.DEBUG,this, "SLAB CTC 보급모드 아님.");	
				return false;    	
			}
			
			if("1".equals(sPosition)){
				/**
				 * CTC 보급요구시 => CTC MAP 정보 CLEAR
				 */
				int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT01,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_01,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
		    										 
				isSuccess = selectWBSearchLineInSlab(	SlabNo,
													 sPosition,
													 "2A%",
													 YmCommonConst.SEARCH_TC_A_BAY_GP);
			}else if("2".equals(sPosition)){
				
				/**
				 * CTC 보급요구시 => CTC MAP 정보 CLEAR
				 */
				int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_01,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
				
				     iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_02,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);			    										 
			    	//선작업지시 존재여부 체크
			    	isSuccess = setPreviousWorkCheck();
			    	
			    	if(!isSuccess){									
			    		 
					isSuccess = selectWBSearchLineInSlab(SlabNo,
														 sPosition,
														 "2A%",
														 YmCommonConst.SEARCH_TC_A_BAY_GP);
				}													 
			}else if("3".equals(sPosition)){
				/**
				 * CTC 보급요구시 => CTC MAP 정보 CLEAR
				 */
				int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2BCT03,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_01,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
			    										 
				
				isSuccess = selectWBSearchLineInSlab(SlabNo,
				                                     sPosition,
													 YmCommonConst.SEARCH_B_BAY_GP,
													 YmCommonConst.SEARCH_TC_B_BAY_GP);
			}else if("4".equals(sPosition)){
				/**
				 * CTC 보급요구시 => CTC MAP 정보 CLEAR
				 */
				int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2CCT04,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_01,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
			    										 
				isSuccess = selectWBSearchLineInSlab(SlabNo,
				                                     sPosition,
													 YmCommonConst.SEARCH_C_BAY_GP,
													 YmCommonConst.SEARCH_TC_C_BAY_GP);
			}
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSRTSpplyWrkOrd1(String sMessage) throws java.rmi.RemoteException{ 
		/*
	     * Mill LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	     * 전문내용을 JDTORecord로 파싱한다.
	     * 업무 로직
	     *	1.TC_CD - CF1PB26 (I/F ID : YM-BIF-036)
	     *	2.Mill 조업시스템으로부터 SLAB Loading Request 정보를 수신
	     *	3.Slab 재료정보를 야드 저장품 Table에 존재하는지 Read한다.
	     *	4.작업예약 Table에 해당하는 Slab가 존재하면 Update하고 존재하지 않으면 Insert한다. 
	     * 
	     *  CF1PB262005-09-2211:11:11I0046HD00320     1
	     * 
	     * @param TC_CD(전문메세지)
	     * @return true(성공),false(실패)
	     * @throws
	     * 
	     * logger.println(LogLevel.DEBUG, "전문코드	  ==" +jDTORecord.getFieldString("전문코드"));
	     * logger.println(LogLevel.DEBUG, "발생일자	  ==" +jDTORecord.getFieldString("발생일자"));
	     * logger.println(LogLevel.DEBUG, "발생시간   ==" +jDTORecord.getFieldString("발생시간"));
	     * logger.println(LogLevel.DEBUG, "전문구분	  ==" +jDTORecord.getFieldString("전문구분"));  // I : Initialize, U : Update, D : Delete, R : Re-request
	     * logger.println(LogLevel.DEBUG, "전문길이	  ==" +jDTORecord.getFieldString("전문길이"));
	     * logger.println(LogLevel.DEBUG, "SLABNo     ==" +jDTORecord.getFieldString("SLABNo"));
	     * logger.println(LogLevel.DEBUG, "SPARE	  ==" +jDTORecord.getFieldString("SPARE"));
	     * 1:#1CTC B동, 2:#2CTC C동 3:#3CTC B동, 4:#4CTC C동 
	     * logger.println(LogLevel.DEBUG, "Position   ==" +jDTORecord.getFieldString("Position"));  
	     */        
        boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

			String sPosition   = StringHelper.evl(jDTORecord.getFieldString("Position"), "");
			String SlabNo      = StringHelper.evl(jDTORecord.getFieldString("SLABNo"), "").trim();
			
			/* 
			 *	장입 대상 슬라브를 CTC설비에 안올리고 싶을때 화면에서 처리.  
			 *	TB_YM_EQUIP(2CWB01)  HMI_STAT MODE 체크 
			 */
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO(); 
			String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
			JDTORecord wbJr = ydStackLayerDAO.requestgetData(sQuery1, 
														new Object[]{ YmCommonConst.STACK_COL_GP_2BCT03 });

			String sHmiStat  = "";
			
			if (wbJr != null){ 
				sHmiStat  = StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
			}
			
			logger.println(LogLevel.DEBUG,this, "CTC보급요구=>CTC 보급모드 상태=."+sHmiStat);	
			
			if ("".equals(sHmiStat)||
			    "C".equals(sHmiStat)){
			    	
			    logger.println(LogLevel.DEBUG,this, "SLAB CTC 보급모드 아님.");	
				return false;    	
			}
			
			if("1".equals(sPosition)){
				/**
				 * CTC 보급요구시 => CTC MAP 정보 CLEAR
				 */
				int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT01,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_01,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
		    										 
				isSuccess = selectWBSearchLineInSlab(	SlabNo,
													 sPosition,
													 "2A%",
													 YmCommonConst.SEARCH_TC_A_BAY_GP);
			}else if("2".equals(sPosition)){
				
				/**
				 * CTC 보급요구시 => CTC MAP 정보 CLEAR
				 */
				int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_01,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
				
				     iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_02,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);			    										 
			    	//선작업지시 존재여부 체크
			    	isSuccess = setPreviousWorkCheck();
			    	
			    	if(!isSuccess){									
			    		 
					isSuccess = selectWBSearchLineInSlab(SlabNo,
														 sPosition,
														 "2A%",
														 YmCommonConst.SEARCH_TC_A_BAY_GP);
				}													 
			}else if("3".equals(sPosition)){
				/**
				 * CTC 보급요구시 => CTC MAP 정보 CLEAR
				 */
				int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2BCT03,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_01,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
			    
//				선작업지시 존재여부 체크
		    	isSuccess = setPreviousWorkCheck1();
		    	
		    	if(!isSuccess){
				isSuccess = selectWBSearchLineInSlab1(SlabNo,
				                                     sPosition,
				                                     "2B%",
													 YmCommonConst.SEARCH_TC_B_BAY_GP);
		    	}
			}else if("4".equals(sPosition)){
				/**
				 * CTC 보급요구시 => CTC MAP 정보 CLEAR
				 */
				int iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2CCT04,
			    										 YmCommonConst.STACK_BED_GP_01,
			    										 YmCommonConst.STACK_LAYER_GP_01,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
			    										 
				isSuccess = selectWBSearchLineInSlab(SlabNo,
				                                     sPosition,
													 YmCommonConst.SEARCH_C_BAY_GP,
													 YmCommonConst.SEARCH_TC_C_BAY_GP);
			}
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	    return isSuccess;
	}
	
	/**
	 *	A/B/C동 야드에 있는 Slab 중에 보급대상 Lot에 해당하면서 
	 *	작업예약이 않되어 있는 Slab를 작업 예약
	 *
	 *	@return
	 */
	private boolean selectWBSearchLineInSlab(String sSlabNo,
											 String sPosition,
											 String sColGp,
											 String sTcGp){
		
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		ymCommonDAO dao1 				= ymCommonDAO.getInstance();
		
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	String Glo_Sch_Call   = "";
			
			JDTORecord TmpSelBedAddr  = null;
			int MaxRec                = 0;	
			String[] tmpStockID       = null;
			String[] tmpWbookID       = null;
			
			if("".equals(sSlabNo)){
				/* 
				 *	조건 => 현재 장입대상순번이고,
				 			A/B/C동 대차에 적치중인 SLAB 나
				 			A/B/C동 야드에 적치중인 SLAB 
				*/
				String sQuery2 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_09";
				List wbL   		= ydStockDAO.getListData(sQuery2, new Object[]{ sColGp,
			 															   sColGp,	
			 															   sColGp,
			 															   sColGp,	
																		   sTcGp,
																		   sTcGp,
																		   sTcGp,
																		   sTcGp});
		    	if (wbL == null || wbL.size() < 1){
					logger.println(LogLevel.DEBUG,this, "CTC보급요구=>SLAB 장입 대상재 존재 안함.");	
					return false;    	
				}	
				
				MaxRec           = wbL.size();	
				tmpStockID       = new String[MaxRec];
				tmpWbookID       = new String[MaxRec];
				
				/*
				 *	CTC 보급요구 일단 1매씩 처리한다.
				 *	매수에 제한이 없으면 아래의 소스를 삭제한다.
				 */
				if(MaxRec > 1){
					if (sPosition.equals("2")){          //1:#2CTC A동
						
						JDTORecord colJr = dao.getStackLayerInfoWithPk("2ACT02","01","02");
						String tLayerStat = "";
			
						if (colJr != null) {
							tLayerStat = StringHelper.evl(colJr.getFieldString("STACK_LAYER_ACTIVE_STAT"), "");
						}
						
						if("C".equals(tLayerStat)){
							MaxRec = 1;
						}else{
							MaxRec = 2;
						}
					}else{
						MaxRec = 1;
					}
					logger.println(LogLevel.DEBUG,this, "CTC보급요구=>일단 1매씩 처리한다.");
				}
				
				for (int inx = 0; inx < MaxRec; inx++){
					TmpSelBedAddr     = (JDTORecord) wbL.get(inx);
					tmpStockID[inx]   = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), "");
					tmpWbookID[inx]   = StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
				}
				
			}else{
				
				String sQeury1    = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
				JDTORecord slabJr = ydStockDAO.getData(sQeury1, 
									 				   new Object[]{ sSlabNo });
				
				if (slabJr == null){
					logger.println(LogLevel.DEBUG,this, "CTC보급요구=>SLAB 장입 대상재 존재 안함.");	
					return false;   
				}
				
				MaxRec           = 1;	
				tmpStockID       = new String[MaxRec];
				tmpWbookID       = new String[MaxRec];
				
				tmpStockID[0]	 = StringHelper.evl(slabJr.getFieldString("STOCK_ID"), "");		
				tmpWbookID[0]    = "";//StringHelper.evl(slabJr.getFieldString("WBOOK_ID"), "");
				
			}
						
			for (int iny = 0; iny < MaxRec; iny++){
	    		
	    		String sWbookId  = "";
				/**
				 *	W/B 보급 및 CTC 보급 작업예약이 존재할 경우.
				 */
				if(!"".equals(tmpWbookID[iny])){
				
					sWbookId = tmpWbookID[iny];
				
				}else{
					
					String sQuery3 		= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGpStat";
					JDTORecord colJr  	= ydStackLayerDAO.requestgetData(sQuery3, new Object[]{ tmpStockID[iny].trim() });
	
					if (colJr == null){
						logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Query Error");
						return false; 												
					}
					
					String sYdGp 	 = StringHelper.evl(colJr.getFieldString("STACK_COL_GP1"), "");
					String sBayGp 	 = StringHelper.evl(colJr.getFieldString("STACK_COL_GP2"), "");
					String sLayerStat= StringHelper.evl(colJr.getFieldString("STACK_LAYER_STAT"), "");
					
					
					// Loading 요청한 slab가 A동 B동 또는 C동에 없을시엔 Error 처리
					if (sPosition.equals("1")){          //1:#1CTC A동
						if (!sBayGp.equals("A")){ 
							logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Error = 요구동<>적치동");
							return false; 
						}
					}else if (sPosition.equals("2")){    //2:#2CTC A동
						if (!sBayGp.equals("A")){ 
							logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Error = 요구동<>적치동");
							return false; 
						}
					}else if (sPosition.equals("3")){    //3:#3CTC B동
						if (!sBayGp.equals("B")){ 
							logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Error = 요구동<>적치동");
							return false; 
						}
					}else if (sPosition.equals("4")){    //4:#4CTC C동
						if (!sBayGp.equals("C")){ 
							logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Error = 요구동<>적치동");
							return false; 
						}
					}
																					   
	                /*
	                 *	적치단  Table Update(작업요구상태='S'로 변경)
	                 */
	            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
					int iSeq1      = ydStackLayerDAO.requestupdateData(sQuery4, 
																	   new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
																	   				tmpStockID[iny].trim() });
					/*
					 *	1. A동 CTC 1번 보급요구
					 */
					if("1".equals(sPosition)){
					    			
		    			sWbookId 		 = dao1.createWBook(sColGp, 
							    						    YmCommonConst.NEW_SCH_WORK_KIND_SCLI, 
							    						    "O", 
							    						    YmCommonConst.STACK_COL_GP_2ACT01);
		    		/*
					 *	2. A동 CTC 2번 보급요구
					 */	
		    		}else if("2".equals(sPosition)){
					    			
		    			sWbookId 		 = dao1.createWBook(sColGp, 
							    						    YmCommonConst.NEW_SCH_WORK_KIND_SCLI, 
							    						    "O", 
							    						    YmCommonConst.STACK_COL_GP_2ACT02);
		    		
		    		/*
					 *	2. B동 CTC 3번 보급요구
					 */	
		    		}else if("3".equals(sPosition)){
					    			
		    			sWbookId 		 = dao1.createWBook(sColGp, 
							    						    YmCommonConst.NEW_SCH_WORK_KIND_SCLI, 
							    						    "O", 
							    						    YmCommonConst.STACK_COL_GP_2BCT03);
					
					/*
					 *	2. C동 CTC 4번 보급요구
					 */	
		    		}else if("4".equals(sPosition)){
					    			
		    			sWbookId 		 = dao1.createWBook(sColGp, 
							    						    YmCommonConst.NEW_SCH_WORK_KIND_SCLI, 
							    						    "O", 
							    						    YmCommonConst.STACK_COL_GP_2CCT04);		    						    
		    		/*
					 *	3. B/C동 CTC 3/4번 보급요구
					 */
		    		}else if(false){
		    		    		
						/*
						 *	작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
						 */
						String sQuery5 		= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
						JDTORecord wbookJr 	= ydStackLayerDAO.requestFind(sQuery5);
		
						if (wbookJr == null){
							logger.println(LogLevel.DEBUG,this, "작업예약ID 생성 Error");
							return false; 																				
						}
						
						sWbookId  = StringHelper.evl(wbookJr.getFieldString("WBOOK_SELECT"), "");
						
						/*
						 *	작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 
						 *	작업예약조, 등록자, 등록일시) 한다.
						 */
						String sQuery6 = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
						int iSeq2      = ydWBookDAO.requestinsertData(sQuery6, 
																	  new Object[]{sWbookId, 
																	  			   sYdGp, 
																	  			   sBayGp, 
																	  			   YmCommonConst.NEW_SCH_WORK_KIND_SCLI, // Slab CTC 보급
								               									   YmCommonUtil.getWorkDuty(), 
								               									   YmCommonUtil.getWorkParty() });	
					}
					/*
					 *	저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
					 */
					String sQuery7 = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
					int iSeq3      = ydStockDAO.requestupdateData(sQuery7, 
																  new Object[]{sWbookId, 
																  			   YmCommonConst.NEW_STOCK_MOVE_TERM_FS, // 압연작업대기 
																  			   tmpStockID[iny].trim() });			
				}
				Glo_Sch_Call += sWbookId +"-" ;
			}				
		
 
			if (!Glo_Sch_Call.trim().equals("")){
				/*
				 *	Slab Schedule EJB Call 
				 */
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new  Class[]{String.class},
																				  new Object[]{Glo_Sch_Call});
				/*
		    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
		    	 * 
					if (isTrue.booleanValue()){
						for (int inz = 0; inz < MaxRec; inz++){
							// 관제 압연 사양  Table 물류진행상태 Update 
							ZZPC001 model = new ZZPC001();
							model.setTcCode("YMPC100");
							model.setrealStlNo(tmpStockID[inz].trim());
							model.seteventStat("40");
							model.seteventOccurDDTT(YmCommonUtil.getStringYMDHMS());
							ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
							isTrue = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{CommonModel.class},
																			  new Object[]{model});							
						}
					}
			      *  		
	    		  */
			}				
			
			logger.println(LogLevel.DEBUG,this, "End-selectWBSearchLineInSlab()");
			
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	
	/**
	 *	A/B/C동 야드에 있는 Slab 중에 보급대상 Lot에 해당하면서 
	 *	작업예약이 않되어 있는 Slab를 작업 예약
	 *
	 *	@return
	 */
	private boolean selectWBSearchLineInSlab1(String sSlabNo,
											 String sPosition,
											 String sColGp,
											 String sTcGp){
		
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		ymCommonDAO dao1 				= ymCommonDAO.getInstance();
		
	    try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
	    	String Glo_Sch_Call   = "";
			
			JDTORecord TmpSelBedAddr  = null;
			int MaxRec                = 0;	
			String[] tmpStockID       = null;
			String[] tmpWbookID       = null;
			
			if("".equals(sSlabNo)){
				/* 
				 *	조건 => 현재 장입대상순번이고,
				 			A/B/C동 대차에 적치중인 SLAB 나
				 			A/B/C동 야드에 적치중인 SLAB 
				*/
				String sQuery2 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_09";
				List wbL   		= ydStockDAO.getListData(sQuery2, new Object[]{ sColGp,
			 															   sColGp,	
			 															   sColGp,
			 															   sColGp,	
																		   sTcGp,
																		   sTcGp,
																		   sTcGp,
																		   sTcGp});
		    	if (wbL == null || wbL.size() < 1){
					logger.println(LogLevel.DEBUG,this, "CTC보급요구=>SLAB 장입 대상재 존재 안함.");	
					return false;    	
				}	
				
				MaxRec           = wbL.size();	
				tmpStockID       = new String[MaxRec];
				tmpWbookID       = new String[MaxRec];
				
				/*
				 *	CTC 보급요구 일단 1매씩 처리한다.
				 *	매수에 제한이 없으면 아래의 소스를 삭제한다.
				 */
				if(MaxRec > 1){
					if (sPosition.equals("2")){          //1:#3CTC B동
						
						JDTORecord colJr = dao.getStackLayerInfoWithPk("2BCT03","01","01");
						String tLayerStat = "";
			
						if (colJr != null) {
							tLayerStat = StringHelper.evl(colJr.getFieldString("STACK_LAYER_ACTIVE_STAT"), "");
						}
						
						if("C".equals(tLayerStat)){
							MaxRec = 1;
						}else{
							MaxRec = 2;
						}
					}else{
						MaxRec = 1;
					}
					logger.println(LogLevel.DEBUG,this, "CTC보급요구=>일단 1매씩 처리한다.");
				}
				
				for (int inx = 0; inx < MaxRec; inx++){
					TmpSelBedAddr     = (JDTORecord) wbL.get(inx);
					tmpStockID[inx]   = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), "");
					tmpWbookID[inx]   = StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
				}
				
			}else{
				
				String sQeury1    = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
				JDTORecord slabJr = ydStockDAO.getData(sQeury1, 
									 				   new Object[]{ sSlabNo });
				
				if (slabJr == null){
					logger.println(LogLevel.DEBUG,this, "CTC보급요구=>SLAB 장입 대상재 존재 안함.");	
					return false;   
				}
				
				MaxRec           = 1;	
				tmpStockID       = new String[MaxRec];
				tmpWbookID       = new String[MaxRec];
				
				tmpStockID[0]	 = StringHelper.evl(slabJr.getFieldString("STOCK_ID"), "");		
				tmpWbookID[0]    = "";//StringHelper.evl(slabJr.getFieldString("WBOOK_ID"), "");
				
			}
						
			for (int iny = 0; iny < MaxRec; iny++){
	    		
	    		String sWbookId  = "";
				/**
				 *	W/B 보급 및 CTC 보급 작업예약이 존재할 경우.
				 */
				if(!"".equals(tmpWbookID[iny])){
				
					sWbookId = tmpWbookID[iny];
				
				}else{
					
					String sQuery3 		= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGpStat";
					JDTORecord colJr  	= ydStackLayerDAO.requestgetData(sQuery3, new Object[]{ tmpStockID[iny].trim() });
	
					if (colJr == null){
						logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Query Error");
						return false; 												
					}
					
					String sYdGp 	 = StringHelper.evl(colJr.getFieldString("STACK_COL_GP1"), "");
					String sBayGp 	 = StringHelper.evl(colJr.getFieldString("STACK_COL_GP2"), "");
					String sLayerStat= StringHelper.evl(colJr.getFieldString("STACK_LAYER_STAT"), "");
					
					
					// Loading 요청한 slab가 A동 B동 또는 C동에 없을시엔 Error 처리
					if (sPosition.equals("1")){          //1:#1CTC A동
						if (!sBayGp.equals("A")){ 
							logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Error = 요구동<>적치동");
							return false; 
						}
					}else if (sPosition.equals("2")){    //2:#2CTC A동
						if (!sBayGp.equals("A")){ 
							logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Error = 요구동<>적치동");
							return false; 
						}
					}else if (sPosition.equals("3")){    //3:#3CTC B동
						if (!sBayGp.equals("B")){ 
							logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Error = 요구동<>적치동");
							return false; 
						}
					}else if (sPosition.equals("4")){    //4:#4CTC C동
						if (!sBayGp.equals("C")){ 
							logger.println(LogLevel.DEBUG,this, "CTC보급요구=>Coil의 적치 위치 검색 Error = 요구동<>적치동");
							return false; 
						}
					}
																					   
	                /*
	                 *	적치단  Table Update(작업요구상태='S'로 변경)
	                 */
	            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
					int iSeq1      = ydStackLayerDAO.requestupdateData(sQuery4, 
																	   new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
																	   				tmpStockID[iny].trim() });
					/*
					 *	1. A동 CTC 1번 보급요구
					 */
					if("1".equals(sPosition)){
					    			
		    			sWbookId 		 = dao1.createWBook(sColGp, 
							    						    YmCommonConst.NEW_SCH_WORK_KIND_SCLI, 
							    						    "O", 
							    						    YmCommonConst.STACK_COL_GP_2ACT01);
		    		/*
					 *	2. A동 CTC 2번 보급요구
					 */	
		    		}else if("2".equals(sPosition)){
					    			
		    			sWbookId 		 = dao1.createWBook(sColGp, 
							    						    YmCommonConst.NEW_SCH_WORK_KIND_SCLI, 
							    						    "O", 
							    						    YmCommonConst.STACK_COL_GP_2ACT02);
		    		
		    		/*
					 *	2. B동 CTC 3번 보급요구
					 */	
		    		}else if("3".equals(sPosition)){
					    			
		    			sWbookId 		 = dao1.createWBook(sColGp, 
							    						    YmCommonConst.NEW_SCH_WORK_KIND_SCLI, 
							    						    "O", 
							    						    YmCommonConst.STACK_COL_GP_2BCT03);
					
					/*
					 *	2. C동 CTC 4번 보급요구
					 */	
		    		}else if("4".equals(sPosition)){
					    			
		    			sWbookId 		 = dao1.createWBook(sColGp, 
							    						    YmCommonConst.NEW_SCH_WORK_KIND_SCLI, 
							    						    "O", 
							    						    YmCommonConst.STACK_COL_GP_2CCT04);		    						    
		    		/*
					 *	3. B/C동 CTC 3/4번 보급요구
					 */
		    		}else if(false){
		    		    		
						/*
						 *	작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
						 */
						String sQuery5 		= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
						JDTORecord wbookJr 	= ydStackLayerDAO.requestFind(sQuery5);
		
						if (wbookJr == null){
							logger.println(LogLevel.DEBUG,this, "작업예약ID 생성 Error");
							return false; 																				
						}
						
						sWbookId  = StringHelper.evl(wbookJr.getFieldString("WBOOK_SELECT"), "");
						
						/*
						 *	작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 
						 *	작업예약조, 등록자, 등록일시) 한다.
						 */
						String sQuery6 = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
						int iSeq2      = ydWBookDAO.requestinsertData(sQuery6, 
																	  new Object[]{sWbookId, 
																	  			   sYdGp, 
																	  			   sBayGp, 
																	  			   YmCommonConst.NEW_SCH_WORK_KIND_SCLI, // Slab CTC 보급
								               									   YmCommonUtil.getWorkDuty(), 
								               									   YmCommonUtil.getWorkParty() });	
					}
					/*
					 *	저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
					 */
					String sQuery7 = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
					int iSeq3      = ydStockDAO.requestupdateData(sQuery7, 
																  new Object[]{sWbookId, 
																  			   YmCommonConst.NEW_STOCK_MOVE_TERM_FS, // 압연작업대기 
																  			   tmpStockID[iny].trim() });			
				}
				Glo_Sch_Call += sWbookId +"-" ;
			}				
		
 
			if (!Glo_Sch_Call.trim().equals("")){
				/*
				 *	Slab Schedule EJB Call 
				 */
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new  Class[]{String.class},
																				  new Object[]{Glo_Sch_Call});
				/*
		    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
		    	 * 
					if (isTrue.booleanValue()){
						for (int inz = 0; inz < MaxRec; inz++){
							// 관제 압연 사양  Table 물류진행상태 Update 
							ZZPC001 model = new ZZPC001();
							model.setTcCode("YMPC100");
							model.setrealStlNo(tmpStockID[inz].trim());
							model.seteventStat("40");
							model.seteventOccurDDTT(YmCommonUtil.getStringYMDHMS());
							ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
							isTrue = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{CommonModel.class},
																			  new Object[]{model});							
						}
					}
			      *  		
	    		  */
			}				
			
			logger.println(LogLevel.DEBUG,this, "End-selectWBSearchLineInSlab()");
			
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/*
	 *
	 *	#2CTC 보급요구 시점에 체크사항
	 *	.	현재 CTC보급 크레인에 CTC스케쥴코드에 해당하는 작업지시가 있는지 체크(UP지시,PUT지시 상태)
	 *	.	해당 정보 존재시 작업지시를 재송신한다.
	 *	.	#2CTC 설비 01번지에 해당 정보 To위치를 셋팅한다.
	 *
	 *	@return
	 */
	private boolean setPreviousWorkCheck(){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-setPreviousWorkCheck()");
			{
				{
					//	1.1	CTC보급 주크레인 정보가져오기
					String sMainCrane = "";
					JDTORecord craneV = dao.getCoilCraneInfo(YmCommonConst.YD_GP_2,
				 										 YmCommonConst.BAY_GP_A,
				 										 YmCommonConst.NEW_SCH_WORK_KIND_SCLI);
					if(craneV != null){
						sMainCrane	= StringHelper.evl(craneV.getFieldString("SELECT_CRANE_NO"),"");
					}
					
					logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>크레인="+sMainCrane);
					
					//	1.2	주크레인이 CTC보급 스케쥴코드에 해당하는 스케쥴 정보 가져오기
					String sSchWorkStat = "";
					String sSchWorkKind = "";
					String sScheduleId 	= "";
					JDTORecord schJr = dao.getWBCraneSchInfo(YmCommonConst.YD_GP_2, 
														   YmCommonConst.BAY_GP_A,
														   YmCommonConst.EQUIP_KIND_CR,
														   sMainCrane,
														   YmCommonConst.NEW_SCH_WORK_KIND_SCLI);	  
					if(schJr != null){
						sSchWorkStat	= StringHelper.evl(schJr.getFieldString("SCH_WORK_STAT"),"");
						sSchWorkKind	= StringHelper.evl(schJr.getFieldString("SCH_WORK_KIND"),"");
						sScheduleId 	= StringHelper.evl(schJr.getFieldString("SCH_ID"), "");
					}
					
					logger.println(LogLevel.DEBUG,this, "W/CTC보급 선작업지시 체크=>sSchWorkStat	="+sSchWorkStat);
					logger.println(LogLevel.DEBUG,this, "W/CTC보급 선작업지시 체크=>sSchWorkKind	="+sSchWorkKind);
					logger.println(LogLevel.DEBUG,this, "W/CTC보급 선작업지시 체크=>sScheduleId 	="+sScheduleId);
					
					//	1.3	스케쥴정보 상태 체크(UP지시,PUT지시)
					if( YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)||
					    YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)|| 	
					    YmCommonConst.SCH_WORK_STAT_2.equals(sSchWorkStat)|| 	
					    YmCommonConst.SCH_WORK_STAT_3.equals(sSchWorkStat)){
						
						JDTORecord schInfo  = dao.getCraneSchInfo(sScheduleId);
					    	logger.println(LogLevel.DEBUG,this, "sScheduleId=" + sScheduleId);
					    	if(schInfo == null){
					    		logger.println(LogLevel.DEBUG,this, "CTC보급선작업지시 체크=>작업지시 전문발생 스케쥴 정보가 존재안함");
					    		return isSuccess;
					    	}
					    	
					    	String sOldPutLoc 	= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
					    	String sStockId   	= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
						String sYdGp 		= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
						String sBayGp 	= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
						String sEquipKind	= YmCommonConst.EQUIP_KIND_CR;
						String sEquipNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"");
						String sSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
				
						String sSchId1 	= sScheduleId;
						String sSlabNo1 	= sStockId;
						String sOPutLoc1	= sOldPutLoc;
						String sNPutLoc1	= YmCommonConst.STACK_COL_GP_2ACT02+
										  YmCommonConst.STACK_BED_GP_01+
										  YmCommonConst.STACK_LAYER_GP_01;
						String sSchId2 	= "";
					    	String sSlabNo2 	= "";
					    	String sOPutLoc2	= "";
						String sNPutLoc2	= "";
												
						if(sOPutLoc1.indexOf(YmCommonConst.STACK_COL_GP_2ACT02) == -1 ){
						
					    		logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>TO위치가 #2CTC설비가 아님");
					    		return isSuccess;
					    	}
					    	
					    	JDTORecord stockV = dao.getSlabGripInfo_02(sYdGp,
										    					   sBayGp,
										    					   sEquipKind,
										    					   sEquipNo,
										    					   sSlabNo1,
															   sSchCode);
					    	if(stockV != null){
					    		
							sNPutLoc1 	= 	sNPutLoc1.substring(0, 6)+
									  		sNPutLoc1.substring(6, 8)+
									  		YmCommonUtil.changeLayerFormat(sNPutLoc1.substring(8,10), "P");
							
					    		sSchId2     	=	StringHelper.evl(stockV.getFieldString("SCH_ID"), "");
					    		sSlabNo2    	= 	StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");
					    		sOPutLoc2	= 	sOPutLoc1.substring(0, 6)+
										  	sOPutLoc1.substring(6, 8)+
										  	YmCommonUtil.changeLayerFormat(sOPutLoc1.substring(8,10), "M");
							sNPutLoc2	=	sNPutLoc1.substring(0, 6)+
										  	sNPutLoc1.substring(6, 8)+
										  	YmCommonUtil.changeLayerFormat(sNPutLoc1.substring(8,10), "M");
					    	}
					    	
					    	logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sSchId1		="+sSchId1);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sSlabNo1	="+sSlabNo1);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sOPutLoc1	="+sOPutLoc1);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sNPutLoc1	="+sNPutLoc1);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sSchId2		="+sSchId2);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sSlabNo2	="+sSlabNo2);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sOPutLoc2	="+sOPutLoc2);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sNPutLoc2	="+sNPutLoc2);	
						
						int iSeq = -1;
							
					 	String sPutStackColGp    	= "";
						String sPutStackBedGp   	= "";
						String sPutStackLayerGp  	= "";
						String sPutUsageCd 		= "";
						
						if(!"".equals(sSchId2)){
							
							sPutStackColGp   	= sNPutLoc2.substring(0, 6);
							sPutStackBedGp   	= sNPutLoc2.substring(6, 8);
							sPutStackLayerGp 	= sNPutLoc2.substring(8,10);
							
							/* 
							 * 적치단 Put위치를 적치상태로 변경
							 * tb_ym_stacklayer Table : stock_id = Coil No
							 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(적치중)
							 */	
					    		iSeq = dao.updateCraneStackLayerStat( sPutStackColGp,
					    										 sPutStackBedGp,
					    										 sPutStackLayerGp,
					    										 sSlabNo2,
					    										 YmCommonConst.STACK_LAYER_STAT_P);
					    		/*
						    	 * B열연 Slab 	
						    	 * 바로 위 상단 상태정보를 UPDATE
						    	 */
					    		iSeq = YmCommonDB.setSlabUpperState_E(sPutStackColGp,
									    						 	  sPutStackBedGp,
									    						 	  sPutStackLayerGp);
			    		
			    				iSeq = dao.updatePutLocInfoWithSchId(sSchId2,
			    								     				sPutStackColGp+
			    								     				sPutStackBedGp+
			    								     				sPutStackLayerGp);
				
			    			}	
			    
						sPutStackColGp   	= sNPutLoc1.substring(0, 6);
						sPutStackBedGp   	= sNPutLoc1.substring(6, 8);
						sPutStackLayerGp 	= sNPutLoc1.substring(8,10);
														  	   
				
						/* 
						 * 적치단 Put위치를 적치상태로 변경
						 * tb_ym_stacklayer Table : stock_id = Coil No
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(적치중)
						 */	
				    		iSeq = dao.updateCraneStackLayerStat( sPutStackColGp,
				    										 sPutStackBedGp,
				    										 sPutStackLayerGp,
				    										 sSlabNo1,
				    										 YmCommonConst.STACK_LAYER_STAT_P);
				    		/*
					    	 * B열연 Slab 	
					    	 * 바로 위 상단 상태정보를 UPDATE
					    	 */
				    		iSeq = YmCommonDB.setSlabUpperState_E(sPutStackColGp,
								    						 	  sPutStackBedGp,
								    						 	  sPutStackLayerGp);
		    				
		    				iSeq = dao.updatePutLocInfoWithSchId(sSchId1,
			    								     			sPutStackColGp+
			    								     			sPutStackBedGp+
			    								     			sPutStackLayerGp);
						isSuccess = true;
					}
				}
				
			}
			logger.println(LogLevel.DEBUG,this, "End-setPreviousWorkCheck()");    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	    
		return isSuccess;
	}
	
	
	/*
	 *
	 *	#2CTC 보급요구 시점에 체크사항
	 *	.	현재 CTC보급 크레인에 CTC스케쥴코드에 해당하는 작업지시가 있는지 체크(UP지시,PUT지시 상태)
	 *	.	해당 정보 존재시 작업지시를 재송신한다.
	 *	.	#2CTC 설비 01번지에 해당 정보 To위치를 셋팅한다.
	 *
	 *	@return
	 */
	private boolean setPreviousWorkCheck1(){
		
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			logger.println(LogLevel.DEBUG,this, "Start-setPreviousWorkCheck1()");
			{
				{
					//	1.1	CTC보급 주크레인 정보가져오기
					String sMainCrane = "";
					JDTORecord craneV = dao.getCoilCraneInfo(YmCommonConst.YD_GP_2,
				 										 YmCommonConst.BAY_GP_B,
				 										 YmCommonConst.NEW_SCH_WORK_KIND_SCLI);
					if(craneV != null){
						sMainCrane	= StringHelper.evl(craneV.getFieldString("SELECT_CRANE_NO"),"");
					}
					
					logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>크레인="+sMainCrane);
					
					//	1.2	주크레인이 CTC보급 스케쥴코드에 해당하는 스케쥴 정보 가져오기
					String sSchWorkStat = "";
					String sSchWorkKind = "";
					String sScheduleId 	= "";
					JDTORecord schJr = dao.getWBCraneSchInfo(YmCommonConst.YD_GP_2, 
														   YmCommonConst.BAY_GP_B,
														   YmCommonConst.EQUIP_KIND_CR,
														   sMainCrane,
														   YmCommonConst.NEW_SCH_WORK_KIND_SCLI);	  
					if(schJr != null){
						sSchWorkStat	= StringHelper.evl(schJr.getFieldString("SCH_WORK_STAT"),"");
						sSchWorkKind	= StringHelper.evl(schJr.getFieldString("SCH_WORK_KIND"),"");
						sScheduleId 	= StringHelper.evl(schJr.getFieldString("SCH_ID"), "");
					}
					
					logger.println(LogLevel.DEBUG,this, "W/CTC보급 선작업지시 체크=>sSchWorkStat	="+sSchWorkStat);
					logger.println(LogLevel.DEBUG,this, "W/CTC보급 선작업지시 체크=>sSchWorkKind	="+sSchWorkKind);
					logger.println(LogLevel.DEBUG,this, "W/CTC보급 선작업지시 체크=>sScheduleId 	="+sScheduleId);
					
					//	1.3	스케쥴정보 상태 체크(UP지시,PUT지시)
					if( YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)||
					    YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)|| 	
					    YmCommonConst.SCH_WORK_STAT_2.equals(sSchWorkStat)|| 	
					    YmCommonConst.SCH_WORK_STAT_3.equals(sSchWorkStat)){
						
						JDTORecord schInfo  = dao.getCraneSchInfo(sScheduleId);
					    	logger.println(LogLevel.DEBUG,this, "sScheduleId=" + sScheduleId);
					    	if(schInfo == null){
					    		logger.println(LogLevel.DEBUG,this, "CTC보급선작업지시 체크=>작업지시 전문발생 스케쥴 정보가 존재안함");
					    		return isSuccess;
					    	}
					    	
					    	String sOldPutLoc 	= StringHelper.evl(schInfo.getFieldString("CRANE_WORD_PUT_LOC"),"");
					    	String sStockId   	= StringHelper.evl(schInfo.getFieldString("STOCK_ID"),"");
						String sYdGp 		= StringHelper.evl(schInfo.getFieldString("YD_GP"),"");
						String sBayGp 	= StringHelper.evl(schInfo.getFieldString("BAY_GP"),"");
						String sEquipKind	= YmCommonConst.EQUIP_KIND_CR;
						String sEquipNo 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_EQUIP_NO"),"");
						String sSchCode 	= StringHelper.evl(schInfo.getFieldString("SCH_WORK_KIND"),"");
				
						String sSchId1 	= sScheduleId;
						String sSlabNo1 	= sStockId;
						String sOPutLoc1	= sOldPutLoc;
						String sNPutLoc1	= YmCommonConst.STACK_COL_GP_2BCT03+
										  YmCommonConst.STACK_BED_GP_01+
										  YmCommonConst.STACK_LAYER_GP_01;
						String sSchId2 	= "";
					    	String sSlabNo2 	= "";
					    	String sOPutLoc2	= "";
						String sNPutLoc2	= "";
												
						if(sOPutLoc1.indexOf(YmCommonConst.STACK_COL_GP_2BCT03) == -1 ){
						
					    		logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>TO위치가 #2CTC설비가 아님");
					    		return isSuccess;
					    	}
					    	
					    	JDTORecord stockV = dao.getSlabGripInfo_02(sYdGp,
										    					   sBayGp,
										    					   sEquipKind,
										    					   sEquipNo,
										    					   sSlabNo1,
															   sSchCode);
					    	if(stockV != null){
					    		
							sNPutLoc1 	= 	sNPutLoc1.substring(0, 6)+
									  		sNPutLoc1.substring(6, 8)+
									  		YmCommonUtil.changeLayerFormat(sNPutLoc1.substring(8,10), "P");
							
					    		sSchId2     	=	StringHelper.evl(stockV.getFieldString("SCH_ID"), "");
					    		sSlabNo2    	= 	StringHelper.evl(stockV.getFieldString("STOCK_ID"), "");
					    		sOPutLoc2	= 	sOPutLoc1.substring(0, 6)+
										  	sOPutLoc1.substring(6, 8)+
										  	YmCommonUtil.changeLayerFormat(sOPutLoc1.substring(8,10), "M");
							sNPutLoc2	=	sNPutLoc1.substring(0, 6)+
										  	sNPutLoc1.substring(6, 8)+
										  	YmCommonUtil.changeLayerFormat(sNPutLoc1.substring(8,10), "M");
					    	}
					    	
					    	logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sSchId1		="+sSchId1);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sSlabNo1	="+sSlabNo1);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sOPutLoc1	="+sOPutLoc1);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sNPutLoc1	="+sNPutLoc1);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sSchId2		="+sSchId2);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sSlabNo2	="+sSlabNo2);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sOPutLoc2	="+sOPutLoc2);	
						logger.println(LogLevel.DEBUG,this, "CTC보급 선작업지시 체크=>sNPutLoc2	="+sNPutLoc2);	
						
						int iSeq = -1;
							
					 	String sPutStackColGp    	= "";
						String sPutStackBedGp   	= "";
						String sPutStackLayerGp  	= "";
						String sPutUsageCd 		= "";
						
						if(!"".equals(sSchId2)){
							
							sPutStackColGp   	= sNPutLoc2.substring(0, 6);
							sPutStackBedGp   	= sNPutLoc2.substring(6, 8);
							sPutStackLayerGp 	= sNPutLoc2.substring(8,10);
							
							/* 
							 * 적치단 Put위치를 적치상태로 변경
							 * tb_ym_stacklayer Table : stock_id = Coil No
							 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(적치중)
							 */	
					    		iSeq = dao.updateCraneStackLayerStat( sPutStackColGp,
					    										 sPutStackBedGp,
					    										 sPutStackLayerGp,
					    										 sSlabNo2,
					    										 YmCommonConst.STACK_LAYER_STAT_P);
					    		/*
						    	 * B열연 Slab 	
						    	 * 바로 위 상단 상태정보를 UPDATE
						    	 */
					    		iSeq = YmCommonDB.setSlabUpperState_E(sPutStackColGp,
									    						 	  sPutStackBedGp,
									    						 	  sPutStackLayerGp);
			    		
			    				iSeq = dao.updatePutLocInfoWithSchId(sSchId2,
			    								     				sPutStackColGp+
			    								     				sPutStackBedGp+
			    								     				sPutStackLayerGp);
				
			    			}	
			    
						sPutStackColGp   	= sNPutLoc1.substring(0, 6);
						sPutStackBedGp   	= sNPutLoc1.substring(6, 8);
						sPutStackLayerGp 	= sNPutLoc1.substring(8,10);
														  	   
				
						/* 
						 * 적치단 Put위치를 적치상태로 변경
						 * tb_ym_stacklayer Table : stock_id = Coil No
						 * tb_ym_stacklayer Table : stack_layer_stat	   = 'P'(적치중)
						 */	
				    		iSeq = dao.updateCraneStackLayerStat( sPutStackColGp,
				    										 sPutStackBedGp,
				    										 sPutStackLayerGp,
				    										 sSlabNo1,
				    										 YmCommonConst.STACK_LAYER_STAT_P);
				    		/*
					    	 * B열연 Slab 	
					    	 * 바로 위 상단 상태정보를 UPDATE
					    	 */
				    		iSeq = YmCommonDB.setSlabUpperState_E(sPutStackColGp,
								    						 	  sPutStackBedGp,
								    						 	  sPutStackLayerGp);
		    				
		    				iSeq = dao.updatePutLocInfoWithSchId(sSchId1,
			    								     			sPutStackColGp+
			    								     			sPutStackBedGp+
			    								     			sPutStackLayerGp);
						isSuccess = true;
					}
				}
				
			}
			logger.println(LogLevel.DEBUG,this, "End-setPreviousWorkCheck1()");    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	    
		return isSuccess;
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 *	선작업지시실행
	 *	#2 CTC 01번지 정보를 임시삭제후 다시 복원한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callPreviousCTCWork(String Msg){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isTrue = false;
		
		JDTORecord infoV 	   	= null;
		 int iSeq 				= 0;	
			
		/*
		 *	선작업1 : #2CTC 01번 BED정보를 검색한다.
		 *	선작업2 : #2CTC 01번 BED정보를 초기화한다.
		 */
		List infoL = dao.getStackLayerInfoWithBed(YmCommonConst.STACK_COL_GP_2ACT02,
										    YmCommonConst.STACK_BED_GP_01);
		
		if(infoL != null)
		{	
			for(int inx = 0; inx < infoL.size() ; inx++)
			{
			 	infoV = (JDTORecord)infoL.get(inx);
			 	
			 	iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
												YmCommonConst.STACK_BED_GP_01, 
												"0"+(inx+1), 
												"",
												YmCommonConst.STACK_LAYER_STAT_E);
			 }
		}	
		
		logger.println(LogLevel.DEBUG,this,"Start-callPreviousCTCWork()");
		logger.println(LogLevel.DEBUG,this,"Msg ="+Msg);
		
		isTrue = selectWBSearchLineInSlab(	"", 	//SlabNo,
										"2", 	//sPosition,
										"2A%",
										YmCommonConst.SEARCH_TC_A_BAY_GP);
		
		logger.println(LogLevel.DEBUG,this,"End-callPreviousCTCWork()");
		
		/*
		 *	후작업1 :  #2CTC 01번 BED정보를 복원한다.
		 */
		if(infoL != null)
		{	
			for(int inx = 0; inx < infoL.size() ; inx++)
			{
			 	infoV = (JDTORecord)infoL.get(inx);
			 	
			 	iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
												YmCommonConst.STACK_BED_GP_01, 
												"0"+(inx+1), 
												StringHelper.evl(infoV.getFieldString("STOCK_ID"), ""),
												StringHelper.evl(infoV.getFieldString("STACK_LAYER_STAT"), ""));
			 }
		}	 
		
		return isTrue;
	}
	
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	선작업지시실행
	 *	#3 CTC 01번지 정보를 임시삭제후 다시 복원한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callPreviousCTCWork1(String Msg){
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isTrue = false;
		
		JDTORecord infoV 	   	= null;
		 int iSeq 				= 0;	
			
		/*
		 *	선작업1 : #3CTC 01번 BED정보를 검색한다.
		 *	선작업2 : #3CTC 01번 BED정보를 초기화한다.
		 */
		List infoL = dao.getStackLayerInfoWithBed(YmCommonConst.STACK_COL_GP_2BCT03,
										    YmCommonConst.STACK_BED_GP_01);
		
		if(infoL != null)
		{	
			for(int inx = 0; inx < infoL.size() ; inx++)
			{
			 	infoV = (JDTORecord)infoL.get(inx);
			 	
			 	iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2BCT03,
												YmCommonConst.STACK_BED_GP_01, 
												"0"+(inx+1), 
												"",
												YmCommonConst.STACK_LAYER_STAT_E);
			 }
		}	
		
		logger.println(LogLevel.DEBUG,this,"Start-callPreviousCTCWork1()");
		logger.println(LogLevel.DEBUG,this,"Msg ="+Msg);
		
		isTrue = selectWBSearchLineInSlab1(	"", 	//SlabNo,
										"3", 	//sPosition,
										"2B%",
										YmCommonConst.SEARCH_TC_B_BAY_GP);
		
		logger.println(LogLevel.DEBUG,this,"End-callPreviousCTCWork1()");
		
		/*
		 *	후작업1 :  #2CTC 01번 BED정보를 복원한다.
		 */
		if(infoL != null)
		{	
			for(int inx = 0; inx < infoL.size() ; inx++)
			{
			 	infoV = (JDTORecord)infoL.get(inx);
			 	
			 	iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2BCT03,
												YmCommonConst.STACK_BED_GP_01, 
												"0"+(inx+1), 
												StringHelper.evl(infoV.getFieldString("STOCK_ID"), ""),
												StringHelper.evl(infoV.getFieldString("STACK_LAYER_STAT"), ""));
			 }
		}	 
		
		return isTrue;
	}
	
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveSRTSpplyWrkOrd_backup(String sMessage) throws java.rmi.RemoteException{ 
		/*
	     * Mill LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	     * 전문내용을 JDTORecord로 파싱한다.
	     * 업무 로직
	     *	1.TC_CD - CF1PB26 (I/F ID : YM-BIF-036)
	     *	2.Mill 조업시스템으로부터 SLAB Loading Request 정보를 수신
	     *	3.Slab 재료정보를 야드 저장품 Table에 존재하는지 Read한다.
	     *	4.작업예약 Table에 해당하는 Slab가 존재하면 Update하고 존재하지 않으면 Insert한다. 
	     * 
	     *  CF1PB262005-09-2211:11:11I0046HD00320     1
	     * 
	     * @param TC_CD(전문메세지)
	     * @return true(성공),false(실패)
	     * @throws
	     * 
	     * logger.println(LogLevel.DEBUG, "전문코드	  ==" +jDTORecord.getFieldString("전문코드"));
	     * logger.println(LogLevel.DEBUG, "발생일자	  ==" +jDTORecord.getFieldString("발생일자"));
	     * logger.println(LogLevel.DEBUG, "발생시간   ==" +jDTORecord.getFieldString("발생시간"));
	     * logger.println(LogLevel.DEBUG, "전문구분	  ==" +jDTORecord.getFieldString("전문구분"));  // I : Initialize, U : Update, D : Delete, R : Re-request
	     * logger.println(LogLevel.DEBUG, "전문길이	  ==" +jDTORecord.getFieldString("전문길이"));
	     * logger.println(LogLevel.DEBUG, "SLABNo     ==" +jDTORecord.getFieldString("SLABNo"));
	     * logger.println(LogLevel.DEBUG, "SPARE	  ==" +jDTORecord.getFieldString("SPARE"));
	     * logger.println(LogLevel.DEBUG, "Position   ==" +jDTORecord.getFieldString("Position")); // 1:#3CTC B동, 2:#4CTC C동 
	     */        
        logger.println(LogLevel.DEBUG,this,"Start-receiveSRTSpplyWrkOrd()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

			String SlabNo     = StringHelper.evl(jDTORecord.getFieldString("SLABNo"), "");
			String Position   = StringHelper.evl(jDTORecord.getFieldString("Position"), "");

			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockSlabNo = ydStockDAO.getData(sStockQueryId, new Object[]{ SlabNo.trim() });
			
			if (StockSlabNo == null){
				throw new EJBServiceException("수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재하지 않습니다. Error");
			}

			String stockId = StringHelper.evl(StockSlabNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockSlabNo.getFieldString("WBOOK_ID"), "");
            
			List list = new ArrayList();
			list.add(SlabNo.trim());
			boolean exsitSlabNo = ydStockDAO.isExistPrimaryKey(list);

			if(exsitSlabNo) {

		    	// 저장품  Table에 작업예약_ID(WBookID)가 존재한다면 Error
		    	if (wbookId == null || wbookId.equals("")){
					
		    		/* 적치단(TB_YM_STACKLAYER) Table Read 
		    		 * STACK_COL_GP(적치열:첫번째자리 야드구분, 두번째 자리 동구분), STACK_LAYER_STAT(적치단 상태 L:적치중)
                     * Select STACK_COL_GP, substr(STACK_COL_GP,1,1) STACK_COL_GP1, substr(STACK_COL_GP,2,1) STACK_COL_GP2,
                     *        STACK_BED_GP, STACK_LAYER_STAT  
		    		 *   From TB_YM_STACKLAYER 
		    		 *  Where STOCK_ID = ? And (STACK_LAYER_STAT = "L" OR STACK_LAYER_STAT = "U") (적치단 상태 L:적치중, U:권상전)
		    		 */     	
					String stackLayQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGp";
					JDTORecord StackColGp  = ydStackLayerDAO.requestgetData(stackLayQueryId, new Object[]{ stockId.trim() });

					if (StackColGp == null){
						throw new EJBServiceException("적치단(TB_YM_STACKLAYER) Table Read Error");
					}
					
					String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
					String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
					String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
					String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
					
					if (stackCol != null && stackCol != ""){
						
						// Loading 요청한 slab가 B동 또는 C동에 없을시엔 Error 처리
						if (Position.equals("1")){          //1:#3CTC B동
							if (!stackCol2.trim().equals("B")){ return false; }
						}else if (Position.equals("2")){    //2:#4CTC C동
							if (!stackCol2.trim().equals("C")){ return false; }
						}
						
                        // 적치단  Table Update(작업요구상태='S'로 변경)
						// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
						if (stackStat.trim().equals(YmCommonConst.STACK_LAYER_STAT_L)){
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
									       YmCommonConst.STACK_LAYER_STAT_S, stockId.trim() });
						}
						
						// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
						String wBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
						JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);

						if (wBookSel == null){
							throw new EJBServiceException("작업예약 ID 생성  Error");
						}
						
						String wBookid  = wBookSel.getFieldString("WBOOK_SELECT");
						
						// 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
						String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
						
						// Schedule Code
						int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ 
								           wBookid.trim(), stackCol1.trim(), stackCol2.trim(), 
										   YmCommonConst.NEW_SCH_WORK_KIND_SCLI, YmCommonUtil.getWorkDuty(), 
										   YmCommonUtil.getWorkParty() });	

						// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
						// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM = ? WHERE STOCK_ID = ?
						String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
					    int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
					    		    wBookid.trim(), YmCommonConst.NEW_STOCK_MOVE_TERM_FS, stockId.trim() });	
						
						logger.println(LogLevel.DEBUG,this, "End-receiveSRTSpplyWrkOrd()");
						
						//Slab Schedule EJB Call
						EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new Class[]{String.class},new Object[]{ wBookid.trim() });							
					}
		    	}	
		    }
			
			return true;
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 * #4 CTC Slab Loading Result
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receive4CTCLoadingResult(String sMessage) throws java.rmi.RemoteException{
		/*
	     * Mill LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	     * 전문내용을 JDTORecord로 파싱한다.
	     * 업무 로직
	     *	1.TC_CD - CF1PB16
	     *	2.Mill Level-2로부터 #4 CTC SLAB Loading Reault 정보를 수신
	     *	3.W/B에서 #4로 Slab를 Loading 하고 난뒤 실적 정보가 발생한다.
	     *  4.W/B 상에 해당 Slab를 없애준다. 
	     * 
	     * @param TC_CD(전문메세지)
	     * @return true(성공),false(실패)
	     * @throws
	     * 
			SLAB No	SLAB_No	CHAR	11
			SPARE	SPARE	CHAR	1
	     */        		
        logger.println(LogLevel.DEBUG,this,"Start-receive4CTCLoadingResult()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		ymCommonDAO ycd 	 			= ymCommonDAO.getInstance();
		
		try{
			
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);

			//--------------------------------------------------------------------
			String sBSLAB_EFF_YN = "N";
			JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
			sBSLAB_EFF_YN = jrChk.getFieldString("BSLAB_EFF_YN");

			logger.println(LogLevel.DEBUG,this,"BSLAB_EFF_YN 확인 : "+sBSLAB_EFF_YN);

			if(sBSLAB_EFF_YN.equals("Y")) {
					
				//B열연 신규모듈 호출
				jDTORecord.setField("MSG_ID"	, "CF1PB16");  
				EJBConnector ydEjbCon = new EJBConnector("default", this);
			    ydEjbCon.trx("YmCommEJB", "rcvInterface", jDTORecord);
				return true;
			}				
			//--------------------------------------------------------------------
			
			String SlabNo               = StringHelper.evl(jDTORecord.getFieldString("SLABNo"), "");
			
			String sQuery1 		= "ym.common.dao.selectSlabInfo"; 
			JDTORecord slabJr  	= ydStackLayerDAO.requestgetData(sQuery1, new Object[]{ SlabNo.trim() });

			String Slab_WT  = StringHelper.evl(slabJr.getFieldString("SLAB_WT"), "0");
			String Slab_T   = StringHelper.evl(slabJr.getFieldString("SLAB_T"), "0");
			
			/*
	    	 * 	2009.07 YJK 생산통제 장입진행정보 기존모듈 삭제.
	    	 * 
				//관제 압연 사양  Table 물류진행상태 Update 
				ZZPC001 model = new ZZPC001();
				model.setTcCode("YMPC100");
				model.setrealStlNo(SlabNo.trim());
				model.seteventStat("60");
				model.seteventOccurDDTT(YmCommonUtil.getStringYMDHMS());
				EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("sendInternalModel",new  Class[]{CommonModel.class},
																		  new Object[]{model});							
	         *   
			 */
			/*
			 *	STACK_COL_GP:적치열(2CWB01), STACK_BED_GP:적치BED(01~05), STACK_LAYER_GP:적치단(01~04)
			 *	에서 해당 Slab를 검색
			 */  
			String sQuery2     = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectWBSTACKLAYER"; 
			JDTORecord layerJr = ydStackLayerDAO.requestgetData(sQuery2, 
																new Object[]{SlabNo.trim(), 
																			 YmCommonConst.STACK_COL_GP_2CWB01 });

			if (layerJr == null){
				logger.println(LogLevel.DEBUG,this, "W/B에 Slab가 존재하지 않습니다.");
				return false; 		
			}
			
			String TmpBegGp     = StringHelper.evl(layerJr.getFieldString("STACK_BED_GP"), "");
			String TmpLayerGp   = StringHelper.evl(layerJr.getFieldString("STACK_LAYER_GP"), "");
			
			/*  
			 *	TB_YM_STACKER INFO
			 */
			String sQuery3    	= "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBSTACKER";
			JDTORecord stackerJr= ydStackLayerDAO.requestgetData(sQuery3, 
															     new Object[]{YmCommonConst.STACK_COL_GP_2CWB01, 
															   				  TmpBegGp.trim()});
			
			String TmpQntyMax   = StringHelper.evl(stackerJr.getFieldString("STACK_BED_QNTY_MAX"), "0");
			String TmpWtMax     = StringHelper.evl(stackerJr.getFieldString("STACK_BED_WT_MAX"), "0");
			String TmpHighMax   = StringHelper.evl(stackerJr.getFieldString("STACK_BED_HIGH_MAX"), "0");
			String TmpQntyCurr  = StringHelper.evl(stackerJr.getFieldString("STACK_BED_QNTY_CURR"), "0");
			String TmpWtCurr    = StringHelper.evl(stackerJr.getFieldString("STACK_BED_WT_CURR"), "0");
			String TmpHighCurr  = StringHelper.evl(stackerJr.getFieldString("STACK_BED_HIGH_CURR"), "0");
			String TmpAbleQnty  = StringHelper.evl(stackerJr.getFieldString("STACK_BED_ABLE_QNTY"), "0");
			String TmpAbleWt    = StringHelper.evl(stackerJr.getFieldString("STACK_BED_ABLE_WT"), "0");
			String TmpAbleHigh  = StringHelper.evl(stackerJr.getFieldString("STACK_BED_ABLE_HIGH"), "0");
			
			/*  
			 *	STACK_BED_QNTY_CURR	=STACK_BED_QNTY_CURR-1, 
			 *	STACK_BED_WT_CURR	-(Slab 공통 중량), 
			 *	STACK_BED_HIGH_CURR	-(Slab 공통 두께)
			 */
			int intTmpQntyCurr = 0;
			int intTmpWtCurr   = 0;
			int intTmpHighCurr = 0;
			
			intTmpQntyCurr = Integer.parseInt(TmpQntyCurr);
			intTmpWtCurr   = Integer.parseInt(TmpWtCurr); 
			intTmpHighCurr = Integer.parseInt(TmpHighCurr);
			
			intTmpQntyCurr = intTmpQntyCurr - 1; 
			intTmpWtCurr   = intTmpWtCurr   - Integer.parseInt(Slab_WT);
			intTmpHighCurr = intTmpHighCurr - Integer.parseInt(Slab_T);
			
			if(intTmpQntyCurr 	< 0) intTmpQntyCurr = 0;
			if(intTmpWtCurr 	< 0) intTmpWtCurr 	= 0;
			if(intTmpHighCurr 	< 0) intTmpHighCurr = 0;
			
			/*  
			 *	해당 적치단 정보 초기화
			 */
			String sQuery4 	= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateWBSTACKLAYER";
			int iSeq1    	= ydStackLayerDAO.requestupdateData(sQuery4, 
																new Object[]{YmCommonConst.STACK_LAYER_ACTIVE_STAT_O, 
																			 YmCommonConst.STACK_LAYER_STAT_E, 
																			 YmCommonConst.STACK_COL_GP_2CWB01, 
																			 TmpBegGp, 
																			 TmpLayerGp});
			
			/*  
			 *	해당 STACKER INFO 셋팅.
			 */
			String sQuery5  = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateSlabCurrSet";
			int iSeq2     	= ydStackLayerDAO.requestupdateData(sQuery5, 
																new Object[]{intTmpQntyCurr+"", 
																			 intTmpWtCurr+"", 
																			 intTmpHighCurr+"", 
																			 YmCommonConst.STACK_COL_GP_2CWB01, 
																			 TmpBegGp.trim() });										
			
			/*  
			 *	Slab 공통에 있는 현재 위치 항목을 Clear 
			 */
			ycd.modifyStoreLocOfSlabComm(YmCommonConst.YD_GP_2, 
										 "", "", "", "", "", "", 
										 SlabNo.trim());		
			  
			logger.println(LogLevel.DEBUG,this, "End-receive4CTCLoadingResult()");
			
			return true;
			
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	STE#3 고장전문 수신시 호출
	 *	WB One Click 이벤트 정보 수신시 호출
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean callCraneCtc4Info(String sVal) throws java.rmi.RemoteException{ 
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			ymCommonDAO yDao = ymCommonDAO.getInstance();
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			/*
			 *	1. STE3가 고장인지를 체크한다.
			 */ 
				String sEquipStat = "";
				String sWorkMode = "";
				
				JDTORecord equipJr = dao.getEquipInfoWithEquipGp("2CST03");
				
				if(equipJr != null){
					sEquipStat 		= StringHelper.evl(equipJr.getFieldString("EQUIP_STAT"), "");
					sWorkMode  		= StringHelper.evl(equipJr.getFieldString("WORK_MODE"), "");
				}
				
				logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = callCraneCtc4Info EQUIP_STAT	="+sEquipStat);
				logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = callCraneCtc4Info WORK_MODE	="+sWorkMode);
				
				if("O".equals(sEquipStat)){
					logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = C동 STE3 설비 정상");
					return false;
				}
			/*
			 *	2. W/B 05번 번지정보를 가져온다.
			 */ 
			 	
			 	String sStockId 	= "";
			 	String sLayerStat 	= "";
			 	
			 	JDTORecord layerJr 	= null;
			 	List infoL 				= dao.getStackLayerInfoWithBed("2CWB01","05","L");
				if(infoL.size() > 0)
				{	
					for(int inx = 0; inx < 1 ; inx++)
					{
						layerJr 	= (JDTORecord)infoL.get(inx);
						sStockId 		= StringHelper.evl(layerJr.getFieldString("STOCK_ID"), "");	
						sLayerStat 	= StringHelper.evl(layerJr.getFieldString("STACK_LAYER_STAT"), "");	
					}
				}
				
				if("".equals(sStockId)){
					logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 존재안함");
					return false;
				}
				
				if(!"L".equals(sLayerStat)){
					logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 작업등록상태 ="+sLayerStat);
					return false;
				}
				
			/*
			 *	3. CTC4 트랙킹 관리가 안되기 때문에 크레인 작업지시 생성 전에 Clear
			 */ 
				int iSeq1 = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2CCT04,
			    									      YmCommonConst.STACK_BED_GP_01,
			    									      YmCommonConst.STACK_LAYER_GP_01,
			    									       "",
			    									      YmCommonConst.STACK_LAYER_STAT_E);
						 	
			/*
			 *	3. W/B 05번 번지 최상단 Slab 정보를 작업예약한다.
			 */   
				
				String sWbookId = yDao.createWBook(YmCommonConst.STACK_COL_GP_2CCT04, 
					    						    YmCommonConst.NEW_SCH_WORK_KIND_SCL2, 
					    						    "O", 
					    						    YmCommonConst.STACK_COL_GP_2CCT04+"01"+"01");
				
				String sQueryId  	= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
				int iSeq 	 	  	= ydStackLayerDAO.requestupdateData(sQueryId, 
															 new Object[]{ 
						       									YmCommonConst.STACK_LAYER_STAT_S, 
						       									sStockId});							
				
				String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sStockId,"");
						     
				sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				iSeq 	 = ydStockDAO.requestupdateData(sQueryId, 
													 new Object[]{ 
						    							sWbookId, 
						    							sStockInfo[1], 
						    							sStockId});		    						    
			/*
			 *	3. 작업예약정보를 스케쥴 호출한다.
			 */   
			 
			 	
			 	if (!sWbookId.trim().equals("")){
					/*
					 *	Slab Schedule EJB Call 
					 */
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new  Class[]{String.class},
																			new Object[]{sWbookId});
				}																  
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			e.printStackTrace();  
		throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	STE#1 고장전문 수신시 호출
	 *	CTC 트랙킹전문 이벤트 정보 수신시 호출
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean callCraneCtc1Info(String sVal) throws java.rmi.RemoteException{ 
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			ymCommonDAO yDao = ymCommonDAO.getInstance();	
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			/*
			 *	1. STE1가 고장인지를 체크한다.
			 */ 
				
				String sEquipStat = "";
				String sWorkMode = "";
				
				JDTORecord equipJr = dao.getEquipInfoWithEquipGp("2AST01");
				
				if(equipJr != null){
					sEquipStat 		= StringHelper.evl(equipJr.getFieldString("EQUIP_STAT"), "");
					sWorkMode  		= StringHelper.evl(equipJr.getFieldString("WORK_MODE"), "");
				}
				
				logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = callCraneCtc1Info EQUIP_STAT	="+sEquipStat);
				logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = callCraneCtc1Info WORK_MODE	="+sWorkMode);
				
				if("O".equals(sEquipStat)){
					logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = A동 STE1 설비 정상");
					return false;
				}
			/*
			 *	2. CTC#2 03번번지정보를 가져온다.
			 */ 
			 	
			 	String sStockId 	= "";
			 	String sLayerStat 	= "";
			 	String sStackLayerGp = "";
			 	
			 	JDTORecord layerJr 	= null;
			 	List infoL 				= dao.getStackLayerInfoWithBed("2ACT02","03","L");
				if(infoL.size() > 0)
				{	
					for(int inx = 0; inx < 1 ; inx++)
					{
						layerJr 	= (JDTORecord)infoL.get(inx);
						sStockId 		= StringHelper.evl(layerJr.getFieldString("STOCK_ID"), "");	
						sLayerStat 	= StringHelper.evl(layerJr.getFieldString("STACK_LAYER_STAT"), "");	
						sStackLayerGp= StringHelper.evl(layerJr.getFieldString("STACK_LAYER_GP"), "");	
					}
				}
				
				if("".equals(sStockId)){
					logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = CTC#2 3번 번지 저장품정보 존재안함");
					return false;
				}
				
				if(!"L".equals(sLayerStat)){
					logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = W/B 5번 번지 저장품정보 작업등록상태 ="+sLayerStat);
					return false;
				}
				
				if("01".equals(sStackLayerGp)){
					logger.println(LogLevel.DEBUG,this, "STE고장 비상스케쥴 = CTC#2 3번 번지 01단 정보 비상스케쥴 수행안함");
					return false;
				}
			/*
			 *	3. CTC#2 3번 번지 최상단 Slab 정보를 작업예약한다.
			 */   
				
				String sWbookId = yDao.createWBook(YmCommonConst.STACK_COL_GP_2ACT02, 
					    						    YmCommonConst.NEW_SCH_WORK_KIND_SCL2, 
					    						    "O", 
					    						    YmCommonConst.STACK_COL_GP_2ACT01+"01"+"01");
				
				String sQueryId  	= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
				int iSeq 	 	  	= ydStackLayerDAO.requestupdateData(sQueryId, 
															 new Object[]{ 
						       									YmCommonConst.STACK_LAYER_STAT_S, 
						       									sStockId});							
				
				String[] sStockInfo = YmCommonUtil.getSlabCurrProgCd(sStockId,"");
						     
				sQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				iSeq 	 = ydStockDAO.requestupdateData(sQueryId, 
													 new Object[]{ 
						    							sWbookId, 
						    							sStockInfo[1], 
						    							sStockId});		    					
			/*
			 *	4. 작업예약정보를 스케쥴 호출한다.
			 */   
			 	
			 	if (!sWbookId.trim().equals("")){
					/*
					 *	Slab Schedule EJB Call 
					 */
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("syCraneScheduleInfoInsert",new  Class[]{String.class},
																			new Object[]{sWbookId});
				}		
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			e.printStackTrace();  
		throw new EJBServiceException(e);
		}
		return isSuccess;
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 *	CTC Tracking Info(CM1PB14)
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveCTCTrackingInfo(String sMessage) throws java.rmi.RemoteException{ 
		boolean isSuccess = false;
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			isSuccess = receiveCTCTrackingInfo(jDTORecord);
			
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			e.printStackTrace();  
		throw new EJBServiceException(e);
		}
		return isSuccess;
	}
	/**
	 * 오퍼레이션명 : 
	 *
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveCTCTrackingInfo(JDTORecord jrRecord) throws java.rmi.RemoteException{ 
		boolean isSuccess = false;
		
		ymCommonDAO ycd 	 			= ymCommonDAO.getInstance();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			String SlabNo111   = StringHelper.evl(jrRecord.getFieldString("SlabNo111"), "").trim();
			String SlabNoSte   = StringHelper.evl(jrRecord.getFieldString("SlabNoSte"), "").trim();
			String SlabNo232   = StringHelper.evl(jrRecord.getFieldString("SlabNo232"), "").trim();
			String SlabNo231   = StringHelper.evl(jrRecord.getFieldString("SlabNo231"), "").trim();
			String SlabNo222   = StringHelper.evl(jrRecord.getFieldString("SlabNo222"), "").trim();
			String SlabNo221   = StringHelper.evl(jrRecord.getFieldString("SlabNo221"), "").trim();
			String SlabNo212   = StringHelper.evl(jrRecord.getFieldString("SlabNo212"), "").trim();
			String SlabNo211   = StringHelper.evl(jrRecord.getFieldString("SlabNo211"), "").trim();
			
			logger.println(LogLevel.DEBUG,this, "CTC 트랙킹정보 =>SlabNo111 "+ SlabNo111);
			logger.println(LogLevel.DEBUG,this, "CTC 트랙킹정보 =>SlabNoSte "+ SlabNoSte);
			logger.println(LogLevel.DEBUG,this, "CTC 트랙킹정보 =>SlabNo232 "+ SlabNo232);
			logger.println(LogLevel.DEBUG,this, "CTC 트랙킹정보 =>SlabNo231 "+ SlabNo231);
			logger.println(LogLevel.DEBUG,this, "CTC 트랙킹정보 =>SlabNo222 "+ SlabNo222);
			logger.println(LogLevel.DEBUG,this, "CTC 트랙킹정보 =>SlabNo221 "+ SlabNo221);
			logger.println(LogLevel.DEBUG,this, "CTC 트랙킹정보 =>SlabNo212 "+ SlabNo212);
			logger.println(LogLevel.DEBUG,this, "CTC 트랙킹정보 =>SlabNo211 "+ SlabNo211);
			
			if(true)
			{
				int iSeq = 0;
				iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT01,
		    										YmCommonConst.STACK_BED_GP_01,
		    										YmCommonConst.STACK_LAYER_GP_01,
		    										SlabNo111,
		    										"".equals(SlabNo111)?"E":"L");
				if(!"".equals(SlabNo111)){
					/*  
					 *	Slab 공통에 있는 현재 위치 항목을 Clear 
					 */
					ycd.modifyStoreLocOfSlabComm(YmCommonConst.YD_GP_2, 
												 "", "", "", "", "", "", 
												 SlabNo111.trim());		
				}
				
				iSeq = dao.updateCraneStackLayerStat("2ST01",
		    										YmCommonConst.STACK_BED_GP_01,
		    										YmCommonConst.STACK_LAYER_GP_01,
		    										SlabNoSte,
		    										"".equals(SlabNoSte)?"E":"L");
				
				iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
		    										YmCommonConst.STACK_BED_GP_03,
		    										YmCommonConst.STACK_LAYER_GP_02,
		    										SlabNo232,
		    										"".equals(SlabNo232)?"E":"L");
		    		
		    		iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
		    										YmCommonConst.STACK_BED_GP_03,
		    										YmCommonConst.STACK_LAYER_GP_01,
		    										SlabNo231,
		    										"".equals(SlabNo231)?"E":"L");
		    		
		    		iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
		    										YmCommonConst.STACK_BED_GP_02,
		    										YmCommonConst.STACK_LAYER_GP_02,
		    										SlabNo222,
		    										"".equals(SlabNo222)?"E":"L");				
				
				iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
		    										YmCommonConst.STACK_BED_GP_02,
		    										YmCommonConst.STACK_LAYER_GP_01,
		    										SlabNo221,
		    										"".equals(SlabNo221)?"E":"L");
		    		
		    		String sLayerStat 	= "";
			 	
			 	JDTORecord colJr = dao.getStackLayerInfoWithPk(YmCommonConst.STACK_COL_GP_2ACT02,
				    										YmCommonConst.STACK_BED_GP_01,
				    										YmCommonConst.STACK_LAYER_GP_02);
				if (colJr != null) {
					sLayerStat = StringHelper.evl(colJr.getFieldString("STACK_LAYER_STAT"), "");
				}
				
				if(!"P".equals(sLayerStat)){
			    		iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
			    										YmCommonConst.STACK_BED_GP_01,
			    										YmCommonConst.STACK_LAYER_GP_02,
			    										SlabNo212,
			    										"".equals(SlabNo212)?"E":"L");
		    		}
		    				    colJr = dao.getStackLayerInfoWithPk(YmCommonConst.STACK_COL_GP_2ACT02,
				    										YmCommonConst.STACK_BED_GP_01,
				    										YmCommonConst.STACK_LAYER_GP_01);
				if (colJr != null) {
					sLayerStat = StringHelper.evl(colJr.getFieldString("STACK_LAYER_STAT"), "");
				}
				
				if(!"P".equals(sLayerStat)){
			    		iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2ACT02,
			    										YmCommonConst.STACK_BED_GP_01,
			    										YmCommonConst.STACK_LAYER_GP_01,
			    										SlabNo211,
			    										"".equals(SlabNo211)?"E":"L");		
				}		    										
			}	    					
			
			callCraneCtc1Info("STE 고장에 따른 크레인 작업지시 호출");																																		    																								    										
			
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			e.printStackTrace();  
		throw new EJBServiceException(e);
		}
		return isSuccess;
	}
}
