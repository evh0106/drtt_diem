package com.inisteel.cim.ym.facilitystatus.facilityinquiry.session;

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
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="RolletTableStatusRegEJB" jndi-name="JNDIRolletTableStatusReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class RolletTableStatusRegSBean extends BaseSessionBean {
	private Logger logger=null;
	private CraneSchDAO dao=null;
	private YmComm ymComm = new YmComm();	
	
	public void ejbCreate() {
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
        logger = new Logger(config);
        dao 	= new CraneSchDAO();
	}

	/**
	 * 오퍼레이션명 : 
	 *
	 * W/B 장입 요구 (One Click 정보) 
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */               
	public boolean receiveRollerTableLineIn(String sMessage) throws java.rmi.RemoteException{
		boolean isSuccess = false;
		
		/*
	     *  야드 LEVEL2로 부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당 업무 로직을 처리한다.
	     *  전문내용을 JDTORecord로 파싱한다.
	     *  업무 로직
	     *	1.TC_CD - CM1PB09
	     *	2.야드 LEVEL2로부터  W/B 장입 요구 (One Click 정보) 정보를 수신
	     *  3.W/B Tracking Shift 한다.  
		 */

		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveRollerTableLineIn()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			int iResult = 0;
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			/*
			 *	1 Pitch 이동 정보에 W/B 2열에 대한 정보를 수신하여
			 *	Level-3와 Level-2 간의 Tracking 정보 정합성을 맞춘다.
			 */
			String tcSlabNo21   = StringHelper.evl(jDTORecord.getFieldString("SlabNo21"), "");
			
			String sQuery1	   	= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectWBSTACKLAYER02BED";
			JDTORecord dataJr 	= ydStackLayerDAO.requestgetData(sQuery1, 
																 new Object[]{ 
							                                                 YmCommonConst.STACK_COL_GP_2CWB01, 
																		     YmCommonConst.STACK_BED_GP_02, 
																		     YmCommonConst.STACK_LAYER_GP_01 });

			String sSlabNo21    = StringHelper.evl(dataJr.getFieldString("STOCK_ID"), "");
			/*
			 *	05/06/09 14:12 SMJ 수정
			 *	수신한 2번지 01단 정보가 공백이면 SKIP
			 */
			logger.println(LogLevel.DEBUG,this,"tcSlabNo21="+tcSlabNo21+"=");
			logger.println(LogLevel.DEBUG,this,"sSlabNo21 ="+sSlabNo21+"=");
			 
			if ( !"".equals(tcSlabNo21.trim()) &&
				 !"".equals(sSlabNo21.trim()) &&
			     !tcSlabNo21.trim().equals(sSlabNo21.trim())
			   ){
			   	String sEMsg = "야드 L-2에서 수신한 W/B 2열 1단 Slab와 "+
			   				   "야드 Level-3 Slab 정보가 일치하지 않아 1 Pitch 하지 않습니다.";
				throw new EJBServiceException(sEMsg);
			}
			
			/* 
			 *	01번지에 있는 Slab들 중에 Put 지시 잡혀 있는 slab가 
			 *	한건이라도 있다면 Shift 해선 않됨 
			 */
			{ 
				String sQuery2		= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectWBSTACKLAYER01BED";
				List wbLayerL1	= ydStockDAO.getListData(sQuery2, 
														 new Object[]{YmCommonConst.STACK_COL_GP_2CWB01, 
														 			  YmCommonConst.STACK_BED_GP_01 });
	
				JDTORecord TmpStackStat     = null;
				int MaxRec                     	= wbLayerL1.size();	
				String[] tmpStockID            	= new String[MaxRec];
				String[] tmpStackLayerStat    	= new String[MaxRec];
				
				String   TmpStackCheck         = "";
				
				String sStackColGp 	= "";
				String sStackBedGp 	= "";
				String sStackLayerGp 	= "";
				int 	  iReq			= 0;
				
				for (int ii=0; ii<MaxRec; ii++){
					TmpStackStat               	= (JDTORecord) wbLayerL1.get(ii);
					tmpStockID[ii]             	= StringHelper.evl(TmpStackStat.getFieldString("STOCK_ID"), "");
					tmpStackLayerStat[ii]     = StringHelper.evl(TmpStackStat.getFieldString("STACK_LAYER_STAT"), "");
					
					sStackColGp 		= StringHelper.evl(TmpStackStat.getFieldString("STACK_COL_GP"), "");
					sStackBedGp 		= StringHelper.evl(TmpStackStat.getFieldString("STACK_BED_GP"), "");
					sStackLayerGp 	= StringHelper.evl(TmpStackStat.getFieldString("STACK_LAYER_GP"), "");
				
					if (tmpStackLayerStat[ii].trim().equals(YmCommonConst.STACK_LAYER_STAT_P)){
					    	
						//throw new EJBServiceException("W/B Shift Error -- Slab 적치 스케줄 상태 이므로 Shift 불가");
						
						logger.println(LogLevel.DEBUG,this, "W/B보급 PUT위치 삭제 => "+sStackColGp+sStackBedGp+sStackLayerGp);
						
						iReq = dao.updateCraneStackLayerStat(sStackColGp,
			    										 	sStackBedGp,
			    										 	sStackLayerGp,
			    											"",
			    										 	YmCommonConst.STACK_LAYER_STAT_E);
					}
				}	
			}
			
			/* 
			 *	W/B 05열에 Slab가 존재하면 Shift 불가 
			 */
			{ 
				String sQuery3	= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectWBSTACKLAYER01BED";
				List wbLayerL5  = ydStockDAO.getListData(sQuery3, 
														 new Object[]{YmCommonConst.STACK_COL_GP_2CWB01, 
														 			  YmCommonConst.STACK_BED_GP_05 });
				
				JDTORecord TmpStackStat01      	= null;
				int MaxRec01                   		= wbLayerL5.size();	
				String[] tmpStockID01          		= new String[MaxRec01];
				String[] tmpStackLayerStat01   	= new String[MaxRec01];
				StringBuffer sMsg              		= new StringBuffer();
				
				EJBConnector ejbConn = new EJBConnector("default","JNDISRTSpplyWrkOrdReg",this);
				
				for (int jj=0; jj<MaxRec01; jj++){
					TmpStackStat01             	= (JDTORecord) wbLayerL5.get(jj);
					tmpStockID01[jj]           	= StringHelper.evl(TmpStackStat01.getFieldString("STOCK_ID"), "");
					tmpStackLayerStat01[jj]  = StringHelper.evl(TmpStackStat01.getFieldString("STACK_LAYER_STAT"), "");
									
					if (! tmpStockID01[jj].equals("")){
	
						/* 2006.6.1 조계성 차장
						 * W/B 1 Pitch 이동은 Mill Level-2로 부터 #4 CTC Loading Result를 받아
						 * 마지막 Slab를 뺐다는 정보를 받고 난 뒤 W/B 1 Pitch 이동을 하게 되어 있었지만
						 * #4 CTC Loading Result와 상관없이   W/B 1 Pitch 이동 정보는 일어날  수 있다고 하여
						 * W/B 1 Pitch 이동 정보가 주 우선으로 5열에 Slab가 있다면 각 Slab를  #4 CTC Loading 한것으로 처리함
						 */
							
						String TC 			= "CF1PB16"; 
						String sDate 			= YmCommonUtil.getCurDate("yyyy-MM-dd");
						String sTime			= YmCommonUtil.getCurDate("hh-mm-ss");
						String Form			= "R";
						String Message_Length	= "0045";
						String SlabNo			= tmpStockID01[jj].trim();
						String sSpare           	= "";
						
						sMsg.append(YmCommonUtil.FillToString(TC		   	,  7));
						sMsg.append(YmCommonUtil.FillToString(sDate			, 10));
						sMsg.append(YmCommonUtil.FillToString(sTime	    		,  8));
						sMsg.append(YmCommonUtil.FillToString(Form		    	,  1));
						sMsg.append(YmCommonUtil.FillToNumber(Message_Length,  4));
						sMsg.append(YmCommonUtil.FillToString(SlabNo			, 11));
						sMsg.append(YmCommonUtil.FillToString(sSpare			,  1));
										
						ejbConn.trx("receive4CTCLoadingResult",new  Class[]{String.class},
															   new Object[]{sMsg.toString()});	
						sMsg.setLength(0);
					}			
				}
			}
			
			/* 
			 *	W/B Data를 한 Col씩 Shift한다.
			 */
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_04, YmCommonConst.STACK_LAYER_GP_01);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_04, YmCommonConst.STACK_LAYER_GP_02);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_04, YmCommonConst.STACK_LAYER_GP_03);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_04, YmCommonConst.STACK_LAYER_GP_04);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_03, YmCommonConst.STACK_LAYER_GP_01);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_03, YmCommonConst.STACK_LAYER_GP_02);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_03, YmCommonConst.STACK_LAYER_GP_03);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_03, YmCommonConst.STACK_LAYER_GP_04);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_02, YmCommonConst.STACK_LAYER_GP_01);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_02, YmCommonConst.STACK_LAYER_GP_02);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_02, YmCommonConst.STACK_LAYER_GP_03);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_02, YmCommonConst.STACK_LAYER_GP_04);			
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_01, YmCommonConst.STACK_LAYER_GP_01);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_01, YmCommonConst.STACK_LAYER_GP_02);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_01, YmCommonConst.STACK_LAYER_GP_03);
			selectWBShiftStackLayerDB(YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_01, YmCommonConst.STACK_LAYER_GP_04);
			
			/**
			 *	Shift후 1열 Clear 해주는 로직 
			 */
			{
				/* 
				 *	1열 Clear TB_YM_STACKLAYER
				 */
				String updateWBClearSTACKLAYER = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateWBClearSTACKLAYER";
				int updatewbClear              = ydStackLayerDAO.requestupdateData(updateWBClearSTACKLAYER, 
																				   new Object[]{YmCommonConst.STACK_LAYER_STAT_E, 
																				   				YmCommonConst.STACK_COL_GP_2CWB01, 
												 												YmCommonConst.STACK_BED_GP_01 });
	
				/*
				 *	1열 Clear TB_YM_STACKER
				 */
				String selectWBClearSTACKER    = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBClearSTACKER";
				JDTORecord selectwbclearstaker = ydStackLayerDAO.requestgetData(selectWBClearSTACKER, new Object[]{ 
						                         YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_01 });
				
				String TmpQntyMax   = StringHelper.evl(selectwbclearstaker.getFieldString("STACK_BED_QNTY_MAX"), "0");
				String TmpWtMax     = StringHelper.evl(selectwbclearstaker.getFieldString("STACK_BED_WT_MAX"), "0");
				String TmpHighMax   = StringHelper.evl(selectwbclearstaker.getFieldString("STACK_BED_HIGH_MAX"), "0");
				String TmpWMax      = StringHelper.evl(selectwbclearstaker.getFieldString("STACK_BED_W_MAX"), "0");
				String TmpLenMax    = StringHelper.evl(selectwbclearstaker.getFieldString("STACK_BED_LEN_MAX"), "0");
				
			   /*
			    *	1열 TB_YM_STACKER 설정정보 셋팅
			    */	
				String updateWBClearSTACKER  = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateWBClearSTACKER";
				int updatewbClearSTACKER     = ydStackLayerDAO.requestupdateData(updateWBClearSTACKER, 
																				 new Object[]{"0", "0", "0", "0", "0", 
																							  TmpQntyMax.trim(), 
																							  TmpWtMax.trim(), 
																							  TmpHighMax.trim(), 
																							  TmpWMax.trim(), 
																							  TmpLenMax.trim(),
																							  YmCommonConst.STACK_COL_GP_2CWB01, 
																							  YmCommonConst.STACK_BED_GP_01 });										
			}	
			
			/**
			 *	W/B 보급요구 시점에 체크사항
			 *	.	현재 W/B보급 크레인에 W/B 스케쥴코드에 해당하는 작업지시가 있는지 체크(UP지시,PUT지시 상태)
			 *	.	해당 정보 존재시 작업지시를 재송신한다.
			 *	.	W/B 설비 01번지에 해당 정보 To위치를 셋팅한다.
			 */
			///////////////////////////////////////////////////////////////////
			/////////////////  C동 W/B보급 예정작업지시 처리  ////////////////
			///////////////////////////////////////////////////////////////////
			boolean isCheck = setPreviousWorkCheck();
			///////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////
			if(!isCheck)
			{
				boolean isTrue1 = setPreviousWBWork();
				/*
				 * 저장품 Map(C동)에서 설비 Table의 보급대상 Lot No인 것을 검색해서 작업예약등록
				 *  -. C동 야드에 있는 Slab 중에 보급대상 Lot에 해당하면서 작업예약이 않되어 있는 Slab를 작업 예약
				 *  -. 3대의 대차가 C동에 도착한것이  있다면 작업예약이  않되어 있다면 작업예약 등록
				 */
				boolean isTrue2 = selectWBSearchLineInSlab();
			}
			
			{
				EJBConnector ejbConn = new EJBConnector("default","JNDISRTSpplyWrkOrdReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("callCraneCtc4Info",new  Class[]{String.class},
																new Object[]{"STE3 고장에 따른 크레인 작업지시 호출"});
			}	
			logger.println(LogLevel.DEBUG,this,"End-receiveRollerTableLineIn()");
			 
			return isSuccess;
			    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	}
	
	/**
	 * 오퍼레이션명 : 
	 *
	 *	선작업지시실행
	 *	W/B 01번지 정보를 임시삭제후 다시 복원한다.
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean callPreviousWBWork(String Msg){
		
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return false;
		}
		
		boolean isTrue = false;
		
		JDTORecord infoV 	   	= null;
		 int iSeq 				= 0;	
			
		/*
		 *	선작업1 : W/B 01번 BED정보를 검색한다.
		 *	선작업2 : W/B 01번 BED정보를 초기화한다.
		 */
		List infoL = dao.getStackLayerInfoWithBed(YmCommonConst.STACK_COL_GP_2CWB01,
										    YmCommonConst.STACK_BED_GP_01);
		
		if(infoL != null)
		{	
			for(int inx = 0; inx < infoL.size() ; inx++)
			{
			 	infoV = (JDTORecord)infoL.get(inx);
			 	
			 	iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2CWB01,
												YmCommonConst.STACK_BED_GP_01, 
												"0"+(inx+1), 
												"",
												YmCommonConst.STACK_LAYER_STAT_E);
			 }
		}	
		
		logger.println(LogLevel.DEBUG,this,"Start-callPreviousWBWork()");
		logger.println(LogLevel.DEBUG,this,"Msg ="+Msg);
		
		isTrue = setPreviousWBWork();
		isTrue = selectWBSearchLineInSlab();
		
		logger.println(LogLevel.DEBUG,this,"End-callPreviousWBWork()");
		
		/*
		 *	후작업1 : W/B 01번 BED정보를 복원한다.
		 */
		if(infoL != null)
		{	
			for(int inx = 0; inx < infoL.size() ; inx++)
			{
			 	infoV = (JDTORecord)infoL.get(inx);
			 	
			 	iSeq = dao.updateCraneStackLayerStat(YmCommonConst.STACK_COL_GP_2CWB01,
												YmCommonConst.STACK_BED_GP_01, 
												"0"+(inx+1), 
												StringHelper.evl(infoV.getFieldString("STOCK_ID"), ""),
												StringHelper.evl(infoV.getFieldString("STACK_LAYER_STAT"), ""));
			 }
		}	 
		
		return isTrue;
	}
	
	/*
	 *	YJK.2007.05.03 처리로직 추가
	 *
	 *	W/B 보급요구 시점에 체크사항
	 *	.	현재 W/B보급 크레인에 W/B 스케쥴코드에 해당하는 작업지시가 있는지 체크(UP지시,PUT지시 상태)
	 *	.	해당 정보 존재시 작업지시를 재송신한다.
	 *	.	W/B 설비 01번지에 해당 정보 To위치를 셋팅한다.
	 *
	 *	@return
	 */
	private boolean setPreviousWorkCheck(){
		
		boolean isSuccess = false;
		
		try{
			logger.println(LogLevel.DEBUG,this, "Start-setPreviousWorkCheck()");
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			{
				{
					//	1.1	W/B보급 주크레인 정보가져오기
					String sMainCrane = "";
					JDTORecord craneV = dao.getCoilCraneInfo(YmCommonConst.YD_GP_2,
				 										 YmCommonConst.BAY_GP_C,
				 										 YmCommonConst.NEW_SCH_WORK_KIND_SWLI);
					if(craneV != null){
						sMainCrane	= StringHelper.evl(craneV.getFieldString("SELECT_CRANE_NO"),"");
					}
					
					logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>크레인="+sMainCrane);
					
					//	1.2	주크레인이 W/B보급 스케쥴코드에 해당하는 스케쥴 정보 가져오기
					String sSchWorkStat = "";
					String sSchWorkKind = "";
					String sScheduleId 	= "";
					JDTORecord schJr = dao.getWBCraneSchInfo(YmCommonConst.YD_GP_2, 
														   YmCommonConst.BAY_GP_C,
														   YmCommonConst.EQUIP_KIND_CR,
														   sMainCrane,
														   YmCommonConst.NEW_SCH_WORK_KIND_SWLI);	  
					if(schJr != null){
						sSchWorkStat	= StringHelper.evl(schJr.getFieldString("SCH_WORK_STAT"),"");
						sSchWorkKind	= StringHelper.evl(schJr.getFieldString("SCH_WORK_KIND"),"");
						sScheduleId 	= StringHelper.evl(schJr.getFieldString("SCH_ID"), "");
					}
					
					logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sSchWorkStat	="+sSchWorkStat);
					logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sSchWorkKind	="+sSchWorkKind);
					logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sScheduleId 		="+sScheduleId);
					
					//	1.3	스케쥴정보 상태 체크(UP지시,PUT지시)
					if( YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)||
					    YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)|| 	
					    YmCommonConst.SCH_WORK_STAT_2.equals(sSchWorkStat)|| 	
					    YmCommonConst.SCH_WORK_STAT_3.equals(sSchWorkStat)){
						
						JDTORecord schInfo  = dao.getCraneSchInfo(sScheduleId);
					    	logger.println(LogLevel.DEBUG,this, "sScheduleId=" + sScheduleId);
					    	if(schInfo == null){
					    		logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>작업지시 전문발생 스케쥴 정보가 존재안함");
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
						String sNPutLoc1	= YmCommonConst.STACK_COL_GP_2CWB01+
										  YmCommonConst.STACK_BED_GP_01+
										  YmCommonConst.STACK_LAYER_GP_01;
						String sSchId2 	= "";
					    	String sSlabNo2 	= "";
					    	String sOPutLoc2	= "";
						String sNPutLoc2	= "";
												
						if(sOPutLoc1.indexOf(YmCommonConst.STACK_COL_GP_2CWB01) == -1 ){
						
					    		logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>TO위치가 W/B설비가 아님");
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
					    	
					    	logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sSchId1		="+sSchId1);	
						logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sSlabNo1	="+sSlabNo1);	
						logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sOPutLoc1	="+sOPutLoc1);	
						logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sNPutLoc1	="+sNPutLoc1);	
						logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sSchId2		="+sSchId2);	
						logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sSlabNo2	="+sSlabNo2);	
						logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sOPutLoc2	="+sOPutLoc2);	
						logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>sNPutLoc2	="+sNPutLoc2);	
						
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
		    				/*
		    				EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
		    				Boolean isTemp  = (Boolean)ejbConn.trx("callBSlabCraneMsgInfo",
		    													new  Class[]{String.class},
															new Object[]{sScheduleId});
						
						logger.println(LogLevel.DEBUG,this, "W/B보급 선작업지시 체크=>작업지시 재송신 ");
						*/									
						
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
	 *	YJK.2006.10.20 처리로직 추가
	 *
	 *	1.	W/B 보급 크레인이 대차하차 UP지시를 가지고 있는지 체크
	 *		=> UP지시상태면 크레인 대기모드 처리
	 *	2.  현 장입순위의 슬라브가 대차하차 스케쥴로 등록이 되어 있으면 
	 *		=> 스케쥴 취소처리
	 *
	 *	@return
	 */
	private boolean setPreviousWBWork(){
		
		boolean isSuccess = false;
		
		try{
			logger.println(LogLevel.DEBUG,this, "Start-setPreviousWBWork()");
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			{
				{
					//	1.1	W/B보급 주크레인 정보가져오기
					String sMainCrane = "";
					JDTORecord craneV = dao.getCoilCraneInfo(YmCommonConst.YD_GP_2,
					 										 YmCommonConst.BAY_GP_C,
					 										 YmCommonConst.NEW_SCH_WORK_KIND_SWLI);
					if(craneV != null){
						sMainCrane	= StringHelper.evl(craneV.getFieldString("SELECT_CRANE_NO"),"");
					}
					
					logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>크레인="+sMainCrane);
					
					//	1.2	주크레인이 W/B보급이 아닌 다른스케쥴코드로 UP지시 상태이면 대기모드 전송
					String sSchWorkStat = "";
					String sSchWorkKind = "";
					String sScheduleId 	= "";
					JDTORecord schJr = dao.getCraneSchInfo(YmCommonConst.YD_GP_2, 
														   YmCommonConst.BAY_GP_C,
														   YmCommonConst.EQUIP_KIND_CR,
														   sMainCrane,
														   "",
														   "");	  
					if(schJr != null){
						sSchWorkStat	= StringHelper.evl(schJr.getFieldString("SCH_WORK_STAT"),"");
						sSchWorkKind	= StringHelper.evl(schJr.getFieldString("SCH_WORK_KIND"),"");
						sScheduleId 	= StringHelper.evl(schJr.getFieldString("SCH_ID"), "");
					}
					
					logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>sSchWorkStat="+sSchWorkStat);
					logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>sSchWorkKind="+sSchWorkKind);
					logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>sScheduleId ="+sScheduleId);
					//	1.3	스케쥴정보 초기화
					if( YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)&&
					   !YmCommonConst.NEW_SCH_WORK_KIND_SWLI.equals(sSchWorkStat)){
						
						int iReq1 = dao.updateCraneEquipStatFromOrd(sScheduleId,
				    									            YmCommonConst.WPROG_STAT_W);
				    	
				    	int iReq2 = dao.updateCraneSchStat(sScheduleId,
				    								       YmCommonConst.SCH_WORK_STAT_S);
				    	
						EJBConnector ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
	    				Boolean isTemp  = (Boolean)ejbConn.trx("callBSlabCraneMsgInfo",
	    													new  Class[]{String.class},
															new Object[]{
																YmCommonConst.YD_GP_2+
			    										   		YmCommonConst.BAY_GP_C+
			    										   		YmCommonConst.EQUIP_KIND_CR+
			    										   		sMainCrane});
						logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>스케쥴정보 초기화 ="+iReq1+"/"+iReq2);
					}
				}
				{
					//	2.1	현장입순위의 슬라브가 대차하차스케쥴로 등록 및 UP지시상태인것 검색
					//	2.2	스케쥴등록이면 스케쥴 취소처리
					//	2.3	UP지시상태이면 스케쥴 취소처리 및 대기모드 전송
					/*
					SELECT  
					        STOCK.STOCK_ID,
					        STOCK.CHARGE_LOT_NO,
					        LAYER.STACK_COL_GP,
					        LAYER.STACK_BED_GP,
					        LAYER.STACK_LAYER_GP,
					        SCH.SCH_ID,
					        SCH.SCH_WORK_KIND,
					        SCH.SCH_WORK_STAT
					FROM    TB_YM_STOCK         STOCK,
					        TB_YM_STACKLAYER    LAYER,
					        TB_YM_SCH           SCH
					WHERE   STOCK.CHARGE_LOT_NO = 
					        (
					        SELECT  MIN(CHARGE_LOT_NO)
					        FROM    TB_YM_STOCK
					        WHERE   CHARGE_LOT_NO IS NOT NULL
					        )
					AND     STOCK.STOCK_ID 			= LAYER.STOCK_ID
					AND     LAYER.STACK_COL_GP 		LIKE :TC||'%'
					AND     LAYER.STACK_LAYER_STAT 	IN ('L','S','U')
					AND     STOCK.STOCK_ID          = SCH.STOCK_ID
					ORDER BY SCH.SCH_ID,
					         STACK_COL_GP, 
							 STACK_BED_GP, 
							 STACK_LAYER_GP DESC
					*/		 
					String sQuery8 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_08";
					ymCommonDAO dao1 = ymCommonDAO.getInstance();
					List loadSchs	= dao1.getCommonList(sQuery8, new Object[]{ "2CTC" });
					
					logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>loadSchs="+loadSchs.size());
																		   
					String sSchId 		= "";
					String sSchWorkStat = "";
					String sSchWorkKind = "";
					
					JDTORecord schJr 	= null;
					
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					
					for(int index = 0; index < loadSchs.size(); index++)
					{
						schJr  			= (JDTORecord)loadSchs.get(index);
						sSchId 			= StringHelper.evl(schJr.getFieldString("SCH_ID"),"");
						sSchWorkStat	= StringHelper.evl(schJr.getFieldString("SCH_WORK_STAT"),"");
						sSchWorkKind	= StringHelper.evl(schJr.getFieldString("SCH_WORK_KIND"),"");
						
						logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>sSchId		 ="+sSchId);
						logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>sSchWorkStat="+sSchWorkStat);
						logger.println(LogLevel.DEBUG,this, "W/B보급 선처리=>sSchWorkKind="+sSchWorkKind);
						
						if((
						    YmCommonConst.SCH_WORK_STAT_S.equals(sSchWorkStat)||
						    YmCommonConst.SCH_WORK_STAT_1.equals(sSchWorkStat)
						    )&&
						    (
					   	    YmCommonConst.NEW_SCH_WORK_KIND_STMU.equals(sSchWorkKind)||
					   	    YmCommonConst.NEW_SCH_WORK_KIND_STM4.equals(sSchWorkKind)
					   	    )
					   	){
							Boolean isTemp  = (Boolean)ejbConn.trx("cancelSlabSchInfo",
														new  Class[]{String.class},
														new Object[]{sSchId});
						}								
					}		
				}	
			}
			logger.println(LogLevel.DEBUG,this, "End-setPreviousWBWork()");    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	    
		return isSuccess;
	}
	/**
	 *	3동 야드에 있는 Slab 중에 보급대상 Lot에 해당하면서 
	 *	작업예약이 않되어 있는 Slab를 작업 예약
	 *
	 *	@return
	 */
	private boolean selectWBSearchLineInSlab(){
		
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
	    try{
	    	logger.println(LogLevel.DEBUG,this, "Start-selectWBSearchLineInSlab()");

			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/* 
			 *	장입 대상 슬라브를 WB에 않올리고 싶을때 화면에서 처리.  
			 *	TB_YM_EQUIP(2CWB01)  HMI_STAT MODE 체크 
			 */
			String sQuery1	= "ym.facilitystatus.facilityinquiry.dao.YdEquipDAO.SelectWBHMISTAT";
			JDTORecord wbJr = ydStackLayerDAO.requestgetData(sQuery1, 
															 new Object[]{ YmCommonConst.STACK_COL_GP_2CWB01 });

			String sHmiStat  = "";
			
			if (wbJr != null){ 
				sHmiStat  = StringHelper.evl(wbJr.getFieldString("HMI_STAT"), "");
			}
			
			if ("".equals(sHmiStat)||
			    "C".equals(sHmiStat)){
			    	
			    logger.println(LogLevel.DEBUG,this, "SLAB 장입 모드 아님.");	
				return false;    	
			}
			 
			String Glo_Sch_Call   = "";
			
			/* 
			 *	조건 => 현재 장입대상순번이고,
			 			C동 대차에 적치중인 SLAB 나
			 			C동 야드에 적치중인 SLAB 
				   SELECT   + FIRST_ROWS   
					        WBOOK.WBOOK_ID,
					        STOCK.STOCK_ID,
					        STOCK.CHARGE_LOT_NO,
					        LAYER.STACK_COL_GP,
					        LAYER.STACK_BED_GP,
					        LAYER.STACK_LAYER_GP
					FROM    TB_YM_STOCK         STOCK,
					        TB_YM_STACKLAYER    LAYER,
					        (
					         SELECT A.WBOOK_ID
					         FROM  TB_YM_WBOOK A    
					         WHERE A.SCH_WORK_KIND IN ('SWLI','SCLI')  
					         AND NOT EXISTS(SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)   
					        )WBOOK                
					WHERE   STOCK.CHARGE_LOT_NO IN 
					        (
					            SELECT
					                A.CHARGE_LOT_NO
					            FROM 
					            (
					                    SELECT  + FIRST_ROWS
					                            A.CHARGE_LOT_NO
					                    FROM    TB_YM_STOCK A
					                    WHERE   A.CHARGE_LOT_NO >= ' '
					                    AND EXISTS( SELECT 'X' FROM TB_YM_STACKLAYER B 
					                                           WHERE   A.STOCK_ID = B.STOCK_ID
					                                           --AND     B.STACK_COL_GP NOT IN ('2ABK01','2ABK02')
					                                           AND     B.STACK_COL_GP NOT LIKE '2A%'
					                                           AND     B.STACK_COL_GP NOT LIKE '2B%'
					                                           AND     B.STACK_LAYER_STAT  IN ('L','S','U') 
					                                           )
					                    ORDER BY A.CHARGE_LOT_NO                               
					            ) A
					            WHERE ROWNUM <= 2  
					        )
					AND     STOCK.WBOOK_ID          = WBOOK.WBOOK_ID
					AND     STOCK.STOCK_ID                  = LAYER.STOCK_ID
					AND     LAYER.STACK_COL_GP              LIKE :COL1||'%'
					AND     LAYER.STACK_LAYER_STAT  IN ('L','S','U')
					UNION
					SELECT  '',
					        STOCK.STOCK_ID,
					        STOCK.CHARGE_LOT_NO,
					        LAYER.STACK_COL_GP,
					        LAYER.STACK_BED_GP,
					        LAYER.STACK_LAYER_GP
					FROM    TB_YM_STOCK         STOCK,
					        TB_YM_STACKLAYER    LAYER
					WHERE   STOCK.CHARGE_LOT_NO IN
					        (
					            SELECT
					                A.CHARGE_LOT_NO
					            FROM 
					            (
					                    SELECT  + FIRST_ROWS 
					                            A.CHARGE_LOT_NO
					                    FROM    TB_YM_STOCK A
					                    WHERE   A.CHARGE_LOT_NO >= ' '
					                    AND EXISTS( SELECT 'X' FROM TB_YM_STACKLAYER B 
					                                           WHERE   A.STOCK_ID = B.STOCK_ID
					                                           --AND     B.STACK_COL_GP NOT IN ('2ABK01','2ABK02')
					                                           AND     B.STACK_COL_GP NOT LIKE '2A%'
					                                           AND     B.STACK_COL_GP NOT LIKE '2B%'
					                                           AND     B.STACK_LAYER_STAT  IN ('L','S','U') 
					                                           )
					                    ORDER BY A.CHARGE_LOT_NO                               
					            ) A
					            WHERE ROWNUM <= 2  
					        )
					AND     STOCK.WBOOK_ID              IS NULL
					AND     STOCK.STOCK_ID                  = LAYER.STOCK_ID
					AND     LAYER.STACK_COL_GP              LIKE :COL1||'%'
					AND     LAYER.STACK_LAYER_STAT  IN ('L','S','U')
					UNION
					SELECT  WBOOK.WBOOK_ID,
					        STOCK.STOCK_ID,
					        STOCK.CHARGE_LOT_NO,
					        LAYER.STACK_COL_GP,
					        LAYER.STACK_BED_GP,
					        LAYER.STACK_LAYER_GP
					FROM    TB_YM_STOCK         STOCK,
					        TB_YM_STACKLAYER    LAYER,
					        (
					         SELECT A.WBOOK_ID
					         FROM  TB_YM_WBOOK A    
					         WHERE A.SCH_WORK_KIND IN ('SWLI','SCLI')  
					         AND NOT EXISTS(SELECT WBOOK_ID FROM TB_YM_SCH WHERE WBOOK_ID = A.WBOOK_ID)  
					        )WBOOK               
					WHERE   STOCK.CHARGE_LOT_NO IN
					        (
					            SELECT
					                A.CHARGE_LOT_NO
					            FROM 
					            (
					                    SELECT  + FIRST_ROWS
					                            A.CHARGE_LOT_NO
					                    FROM    TB_YM_STOCK A
					                    WHERE   A.CHARGE_LOT_NO >= ' '
					                    AND EXISTS( SELECT 'X' FROM TB_YM_STACKLAYER B 
					                                           WHERE   A.STOCK_ID = B.STOCK_ID
					                                           --AND     B.STACK_COL_GP NOT IN ('2ABK01','2ABK02')
					                                           AND     B.STACK_COL_GP NOT LIKE '2A%'
					                                           AND     B.STACK_COL_GP NOT LIKE '2B%'
					                                           AND     B.STACK_LAYER_STAT  IN ('L','S','U') 
					                                           )
					                    ORDER BY A.CHARGE_LOT_NO                               
					            ) A
					            WHERE ROWNUM <= 2  
					        )
					AND     STOCK.WBOOK_ID          = WBOOK.WBOOK_ID
					AND     STOCK.STOCK_ID                  = LAYER.STOCK_ID
					AND     LAYER.STACK_COL_GP              LIKE :COL2||'%'
					AND     LAYER.STACK_LAYER_STAT  IN ('L','S','U')
					UNION
					SELECT  '',
					        STOCK.STOCK_ID,
					        STOCK.CHARGE_LOT_NO,
					        LAYER.STACK_COL_GP,
					        LAYER.STACK_BED_GP,
					        LAYER.STACK_LAYER_GP
					FROM    TB_YM_STOCK         STOCK,
					        TB_YM_STACKLAYER    LAYER
					WHERE   STOCK.CHARGE_LOT_NO IN 
					        (
					            SELECT
					                A.CHARGE_LOT_NO
					            FROM 
					            (
					                    SELECT  + FIRST_ROWS
					                            A.CHARGE_LOT_NO
					                    FROM    TB_YM_STOCK A
					                    WHERE   A.CHARGE_LOT_NO >= ' '
					                    AND EXISTS( SELECT 'X' FROM TB_YM_STACKLAYER B 
					                                           WHERE   A.STOCK_ID = B.STOCK_ID
					                                           --AND     B.STACK_COL_GP NOT IN ('2ABK01','2ABK02')
					                                           AND     B.STACK_COL_GP NOT LIKE '2A%'
					                                           AND     B.STACK_COL_GP NOT LIKE '2B%'
					                                           AND     B.STACK_LAYER_STAT  IN ('L','S','U') 
					                                           )
					                    ORDER BY A.CHARGE_LOT_NO                               
					            ) A
					            WHERE ROWNUM <= 2  
					        )
					AND     STOCK.WBOOK_ID          IS NULL
					AND     STOCK.STOCK_ID                  = LAYER.STOCK_ID
					AND     LAYER.STACK_COL_GP              LIKE :COL2||'%'
					AND     LAYER.STACK_LAYER_STAT  IN ('L','S','U')
					ORDER BY CHARGE_LOT_NO, 
					                 STACK_COL_GP DESC, 
					                 STACK_BED_GP DESC, 
					                 STACK_LAYER_GP DESC
			*/
			String sQuery2 	= "ym.steelinfo.steelinforecv.YdStockDAO.selectWBSlabSearch_03";
			List wbL   		= ydStockDAO.getListData(sQuery2, new Object[]{YmCommonConst.SEARCH_C_BAY_GP,
																		   YmCommonConst.SEARCH_C_BAY_GP,
																		   YmCommonConst.SEARCH_TC_C_BAY_GP,
																		   YmCommonConst.SEARCH_TC_C_BAY_GP });
	    	
	    	if (wbL 		== null	||
	    		wbL.size()	==0){
				logger.println(LogLevel.DEBUG,this, "SLAB 장입 대상재 존재 안함.");	
				return false;    	
			}	
			
			JDTORecord TmpSelBedAddr  = null;
			int MaxRec                = wbL.size();	
			String[] tmpStockID       = new String[MaxRec];
			String[] tmpWbookID       = new String[MaxRec];
			
			/*
			 *	W/B 보급요구 W/B 최대적치수량 매씩 처리한다.
			 *	W/B 최대적치수량매 이상일 경우 나머지 보급요구 대상재는 TO위치 FAIL(W/B MAX 4매)
			 *  예를 들면 02단 보급대상재가 W/B 보급 FAIL이고
			 *			  01단 보급대상재도 W/B 보급 FAIL이나, 02단이 01단의 보조작업으로
			 *			  스케쥴이 편성되는 경우가 존재한다.
			 */
			
			/**
			 *	W/B 최대적치수량을 가져온다.
			 */
			int	iMaxCnt = 0;
			{
				
				List infoL = dao.getStackLayerInfoWithBed(YmCommonConst.STACK_COL_GP_2CWB01,
														  YmCommonConst.STACK_BED_GP_01);
				
				if(infoL != null)
				{	
					JDTORecord infoV 	   = null;
				 	String sStackActiveStat= "";
				 	
					for(int inx = 0; inx < infoL.size() ; inx++)
					{
					 	infoV = (JDTORecord)infoL.get(inx);
					 	
					 	sStackActiveStat = StringHelper.evl(infoV.getFieldString("STACK_LAYER_ACTIVE_STAT"), "");	 														 
					 	
					 	if(YmCommonConst.STACK_LAYER_ACTIVE_STAT_O.equals(sStackActiveStat)){
					 		
					 		iMaxCnt++;
					 	}
					 }
				}	
				logger.println(LogLevel.DEBUG,this, "W/B보급요구=>W/B설비 최대적치수량=>"+iMaxCnt);
			}
			 
			if(MaxRec > iMaxCnt){
				MaxRec = iMaxCnt;
				logger.println(LogLevel.DEBUG,this, "W/B보급요구=>최대 "+iMaxCnt+"매씩 처리한다.");
			}
			
			/*
			 * 2010.09.02 윤재광 - 다른 장입LOT대상재 작업가능 처리
			 */
			if(MaxRec == 2){
				
				String sPreStockId		= "";
				String sPreChargeLotNo	= "";
				String sPreStackColGp	= "";
				String sPreStackBedGp	= "";
				String sPreStackLayerGp	= "";
				
				String sCurStockId		= "";
				String sCurChargeLotNo	= "";
				String sCurStackColGp	= "";
				String sCurStackBedGp	= "";
				String sCurStackLayerGp	= "";
				
				for (int ink = 0; ink < MaxRec; ink++){
					TmpSelBedAddr     = (JDTORecord) wbL.get(ink);
					
					sCurStockId 	= StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), "");
					sCurChargeLotNo = StringHelper.evl(TmpSelBedAddr.getFieldString("CHARGE_LOT_NO"), "");
					sCurStackColGp 	= StringHelper.evl(TmpSelBedAddr.getFieldString("STACK_COL_GP"), "");
					sCurStackBedGp 	= StringHelper.evl(TmpSelBedAddr.getFieldString("STACK_BED_GP"), "");
					sCurStackLayerGp= StringHelper.evl(TmpSelBedAddr.getFieldString("STACK_LAYER_GP"), "");
					
					
					
					if("".equals(sPreChargeLotNo)){
						sPreStockId		= sCurStockId;
						sPreChargeLotNo = sCurChargeLotNo;
						sPreStackColGp 	= sCurStackColGp;
						sPreStackBedGp 	= sCurStackBedGp;
						sPreStackLayerGp= sCurStackLayerGp;
					}else{
						
						JDTORecord stockJr1 = dao.getSlabCommonInfo(sPreStockId);
				    	JDTORecord stockJr2 = dao.getSlabCommonInfo(sCurStockId);
				    	
						String sTSlabWt = StringHelper.evl(stockJr1.getFieldString("SLAB_WT"),"0");
						String sGSlabWt = StringHelper.evl(stockJr2.getFieldString("SLAB_WT"),"0");
						
						String sTSlabW = StringHelper.evl(stockJr1.getFieldString("SLAB_W"),"0");
						String sGSlabW = StringHelper.evl(stockJr2.getFieldString("SLAB_W"),"0");
						
						String sTSlabSpe = StringHelper.evl(stockJr1.getFieldString("SPEC_ABBSYM"),"");
						String sGSlabSpe = StringHelper.evl(stockJr2.getFieldString("SPEC_ABBSYM"),"");
						
						String sTLCwrsltYn = StringHelper.evl(stockJr1.getFieldString("C_WRSLT_CHK"),"");
						String sGLCwrsltYn = StringHelper.evl(stockJr2.getFieldString("C_WRSLT_CHK"),"");
						
						String sTScarfingYn = StringHelper.evl(stockJr1.getFieldString("SCARFING_DONE_YN"),"N");
						String sGScarfingYn = StringHelper.evl(stockJr2.getFieldString("SCARFING_DONE_YN"),"N");
						
						double dTSlabWt = Double.parseDouble(sTSlabWt);
						double dGSlabWt = Double.parseDouble(sGSlabWt);
						double dTSlabW = Double.parseDouble(sTSlabW);
						double dGSlabW = Double.parseDouble(sGSlabW);
			 
						if(sPreChargeLotNo.equals(sCurChargeLotNo)){
							//장입lot번호가 동일한 경우
							
							if(ink >0 ){
								
								/*
								 * Slab 중량 53000 이상 제외
								 */
								if((dTSlabWt + dGSlabWt) > 53000){
									
									logger.println(LogLevel.DEBUG,this, "=[작업예약]동일 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 크레인설정 중량을 초과함 1매작업.");
									MaxRec = ink;
									break;
								}
								
								/*
								 * Slab 폭 20mm 이상 제외
								 */
								if( Math.abs(dTSlabW - dGSlabW) > 20.0){
									
									logger.println(LogLevel.DEBUG,this, "=[작업예약]동일 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 크레인설정 폭을 초과함 1매작업.");
									MaxRec = ink;
									break;
								}  
								
								/*
								 * 카본함량0.20이상  제외
								 */
//이슈ID:13241					if(YmCommonConst.USE_YN_Y.equals(sTLCwrsltYn)
//					    				&&(sTScarfingYn.equals("Y"))){
//									logger.println(LogLevel.DEBUG,this, "=[작업예약]동일 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 카본함량0.20이상 스카핑완료재 1매작업.");
//									MaxRec = ink;
//									break;
//								}
// 
//								if(YmCommonConst.USE_YN_Y.equals(sGLCwrsltYn)
//					    				&&(sGScarfingYn.equals("Y"))){
//									logger.println(LogLevel.DEBUG,this, "=[작업예약]동일 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 카본함량0.20이상 스카핑완료재 1매작업.");
//									MaxRec = ink;
//									break;
//								}
								
								/*
								 * Slab 강종체크(고강도 슬라브)
								 */
//이슈ID:13241					if((sTSlabSpe.equals("API-J55")||sTSlabSpe.equals("JS-S45C")||sTSlabSpe.equals("JS-SS490")||sTSlabSpe.equals("HSC1470HPF"))
//										   &&(sTScarfingYn.equals("Y"))){
//									logger.println(LogLevel.DEBUG,this, "=[작업예약]동일 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 고강도규격 대상 1매작업.");
//									MaxRec = ink;
//									break;
//								}
//						 
//								if((sGSlabSpe.equals("API-J55")||sGSlabSpe.equals("JS-S45C")||sGSlabSpe.equals("JS-SS490")||sGSlabSpe.equals("HSC1470HPF"))
//										   &&(sGScarfingYn.equals("Y"))){
//									logger.println(LogLevel.DEBUG,this, "=[작업예약]동일 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 고강도규격 대상 1매작업.");
//									MaxRec = ink;
//									break;
//								}
								 
							}
							
							
							
						}else{
							//장입lot번호가 틀린 경우
							if(sPreStackColGp.equals(sCurStackColGp) && 
							   sPreStackBedGp.equals(sCurStackBedGp) &&
							   sPreStackLayerGp.equals(YmCommonUtil.changeLayerFormat(sCurStackLayerGp,"P"))){
													
								/*
								 * Slab 중량 53000 이상 제외
								 */
								if((dTSlabWt + dGSlabWt) > 53000){
									
									logger.println(LogLevel.DEBUG,this, "=[작업예약]다른 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 크레인설정 중량을 초과함 1매작업.");
									MaxRec = ink;
									break;
								}
								/*
								 * Slab 폭 20mm 이상 제외
								 */
								if( Math.abs(dTSlabW - dGSlabW) > 20.0){
									
									logger.println(LogLevel.DEBUG,this, "=[작업예약]다른 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 크레인설정 폭을 초과함 1매작업.");
									MaxRec = ink;
									break;
								}
								
								/*
								 * Slab 강종체크(고강도 슬라브)
								 */
//이슈ID:13241					if((sTSlabSpe.equals("API-J55")||sTSlabSpe.equals("JS-S45C")||sTSlabSpe.equals("JS-SS490")||sTSlabSpe.equals("HSC1470HPF"))
//										   &&(sTScarfingYn.equals("Y"))){
//									logger.println(LogLevel.DEBUG,this, "=[작업예약]다른 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 고강도규격 대상 1매작업.");
//									MaxRec = ink;
//									break;
//								}
//						 
//								if((sGSlabSpe.equals("API-J55")||sGSlabSpe.equals("JS-S45C")||sGSlabSpe.equals("JS-SS490")||sGSlabSpe.equals("HSC1470HPF"))
//										   &&(sGScarfingYn.equals("Y"))){
//									logger.println(LogLevel.DEBUG,this, "=[작업예약]동일 장입LOT 두매작업["+sPreStockId+":"+sCurStockId+"] => 고강도규격 대상 1매작업.");
//									MaxRec = ink;
//									break;
//								}
								
								int Seq = dao.updateStockSaddleInfo(StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), ""),"LOT");
							}else{
								MaxRec = ink;
								break;
							}
						}
					}
				}
			}
			
			for (int inx = 0; inx < MaxRec; inx++){
				TmpSelBedAddr     = (JDTORecord) wbL.get(inx);
				
				tmpStockID[inx]   = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), "");
				tmpWbookID[inx]   = StringHelper.evl(TmpSelBedAddr.getFieldString("WBOOK_ID"), "");
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
						logger.println(LogLevel.DEBUG,this, "Coil의 적치 위치 검색 Query Error");
						return false; 												
					}
					
					String sYdGp 	 = StringHelper.evl(colJr.getFieldString("STACK_COL_GP1"), "");
					String sBayGp 	 = StringHelper.evl(colJr.getFieldString("STACK_COL_GP2"), "");
					String sLayerStat= StringHelper.evl(colJr.getFieldString("STACK_LAYER_STAT"), "");
																					   
	                /*
	                 *	적치단  Table Update(작업요구상태='S'로 변경)
	                 */
	            	String sQuery4 = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
					int iSeq1      = ydStackLayerDAO.requestupdateData(sQuery4, 
																	   new Object[]{YmCommonConst.STACK_LAYER_STAT_S, 
																	   				tmpStockID[iny].trim() });
					
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
																  			   YmCommonConst.NEW_SCH_WORK_KIND_SWLI, // Slab W/B 보급
							               									   YmCommonUtil.getWorkDuty(), 
							               									   YmCommonUtil.getWorkParty() });	
					
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
							//관제 압연 사양  Table 물류진행상태 Update 
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
     * W/B TB_YM_STACKLAYER Shift
     * 
     * @param 적치열, 적치BED, 적치단
     * @return
     * @throws 
     */
	private boolean selectWBShiftStackLayerDB(String STACKCOLGP, String STACKBEDGP, String STACKLAYERGP){
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
	    try{
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/*
			 * 적치단(TB_YM_STACKLAYER) Select
			 * 적치열(STACK_COL_GP) = '2CWB01', 적치BED구분(STACK_BED_GP) = '05', 적치단상태(STACK_LAYER_STAT) = 'L'
			 * Select 
			 *        STOCK_ID,		              STACK_LAYER_ACTIVE_STAT,    STACK_LAYER_STAT,
			 * 		  STACK_LAYER_X_AXIS,         STACK_LAYER_Y_AXIS,         STACK_LAYER_Z_AXIS, 
			 *        STACK_LAYER_2ND_OUTDIA_MIN, STACK_LAYER_2ND_OUTDIA_MAX, STACK_LAYER_2ND_WT_MIN,
			 *        STACK_LAYER_2ND_WT_MAX,     STACK_LAYER_2ND_W_MIN,      STACK_LAYER_2ND_W_MAX,
			 *        STACK_LAYER_COOL_DDTT_MIN,  STACK_LAYER_COOL_DDTT_MAX,  STACK_LAYER_TEMP_MIN, 
			 *        STACK_LAYER_TEMP_MAX,       STACK_LAYER_STACK_LOT_NO1,  STACK_LAYER_STACK_LOT_NO2,
			 *        REGISTER,                   REG_DDTT,                   MODIFIER,
			 *        MOD_DDTT,                   DEL_YN
			 *   From TB_YM_STACKLAYER
			 * 	Where STACK_COL_GP   = '2CWB01'
			 * 	  And STACK_BED_GP   = '04'                  ~'01'
			 *    And STACK_LAYER_GP = '01'                  ~'04'
			 * 
			 *  4열을 Read 하여  5열에 Update,
			 *  3열을 Read 하여  4열에 Update,
			 *  2열을 Read 하여  3열에 Update,
			 *  1열을 Read 하여  2열에 Update,
			 * 	1열    Clear		  	 
			 */
			String selectWBShiftSTACKLAYER  = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectWBShiftSTACKLAYER"; 
			JDTORecord selectwbShift        = ydStackLayerDAO.requestgetData(selectWBShiftSTACKLAYER, new Object[]{ 
					                          STACKCOLGP, STACKBEDGP, STACKLAYERGP });

			String TmpStockId    = StringHelper.evl(selectwbShift.getFieldString("STOCK_ID"), "");
			String TmpActiveStat = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_ACTIVE_STAT"), "");
			String TmpStat       = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_STAT"), "");
			String TmpXAxis      = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_X_AXIS"), "");
			String TmpYAxis      = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_Y_AXIS"), "");
			String TmpZAxis      = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_Z_AXIS"), "");
			String Tmp2ndOMin    = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_2ND_OUTDIA_MIN"), "");
			String Tmp2ndOMax    = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_2ND_OUTDIA_MAX"), "");
			String Tmp2ndWtMin   = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_2ND_WT_MIN"), "");
			String Tmp2ndWtMax   = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_2ND_WT_MAX"), "");
			String Tmp2ndWMin    = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_2ND_W_MIN"), "");
			String Tmp2ndWMax    = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_2ND_W_MAX"), "");
			String TmpCoolMin    = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_COOL_DDTT_MIN"), "");
			String TmpCoolMax    = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_COOL_DDTT_MAX"), "");
			String TmpTempMin    = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_TEMP_MIN"), "");
			String TmpTempMax    = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_TEMP_MAX"), "");
			String TmpLotNo1     = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_STACK_LOT_NO1"), "");
			String TmpLotNo2     = StringHelper.evl(selectwbShift.getFieldString("STACK_LAYER_STACK_LOT_NO2"), "");
			String TmpRegister   = StringHelper.evl(selectwbShift.getFieldString("REGISTER"), "");
			String TmpRedddtt    = StringHelper.evl(selectwbShift.getFieldString("REG_DDTT"), "");
			String TmpModifier   = StringHelper.evl(selectwbShift.getFieldString("MODIFIER"), "");
			String TmpModddtt    = StringHelper.evl(selectwbShift.getFieldString("MOD_DDTT"), "");
			String TmpDelYN      = StringHelper.evl(selectwbShift.getFieldString("DEL_YN"), "");
			
			
		   /*
		    *  Update TB_YM_STACKLAYER 
		    *     Set STOCK_ID                   = TmpStockId,
		    *         STACK_LAYER_ACTIVE_STAT    = TmpActiveStat,      STACK_LAYER_STAT           = TmpStat
		    *   Where STACK_COL_GP   = '2CWB01'
		    *     And STACK_BED_GP   = '05'                  ~'02' 
		    *     And STACK_LAYER_GP = '01'                  ~'04'   
		    */
			String TmpSTACKBEDGP = "0" + (Integer.parseInt(STACKBEDGP) + 1);
			
			String updateWBShiftSTACKLAYER = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateWBShiftSTACKLAYER";
			int updatewbShift              = ydStackLayerDAO.requestupdateData(updateWBShiftSTACKLAYER, new Object[]{ 
											 TmpStockId.trim(),	  TmpActiveStat.trim(), TmpStat.trim(),
											 STACKCOLGP,          TmpSTACKBEDGP,        STACKLAYERGP });	    	
	    	
			
			/*
			 * SLAB 공통 Table 저장위치 Update 
			 */
			if (!TmpStockId.trim().equals("")){
				int iReq = dao.updateSlabCommonLocInfo(TmpStockId.trim(),STACKCOLGP + TmpSTACKBEDGP + STACKLAYERGP);	
			}
			  	  
			
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	
	/**
     * W/B TB_YM_STACKER Shift
     * 
     * @param 적치열, 적치BED
     * @return
     * @throws 
     */
	private boolean selectWBShiftStackerDB(String STACKCOLGP, String STACKBEDGP){
		boolean isSuccess = false;
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
	    try{
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			}
			
			/* TB_YM_STACKER Shift
			 *  ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBClearSTACKER
			 *  Select STACK_COL_GP,       STACK_BED_GP,        STACK_BED_X_AXIS,    STACK_BED_Y_AXIS,	  STACK_BED_Z_AXIS,
			 * 		   STACK_BED_QNTY_MAX, STACK_BED_WT_MAX,    STACK_BED_HIGH_MAX,  STACK_BED_W_MAX,
			 * 		   STACK_BED_LEN_MAX,  STACK_BED_QNTY_CURR, STACK_BED_WT_CURR,	 STACK_BED_HIGH_CURR,
			 * 		   STACK_BED_W_CURR,   STACK_BED_LEN_CURR,	STACK_BED_ABLE_QNTY, STACK_BED_ABLE_WT,
			 * 		   STACK_BED_ABLE_HIGH,STACK_BED_ABLE_W,    STACK_BED_ABLE_LEN,  STACK_BED_ACTIVE_STAT,
			 * 		   REGISTER,	       REG_DDTT,	        MODIFIER,	         MOD_DDTT,	          DEL_YN 
			 *    From TB_YM_STACKER
			 *   Where STACK_COL_GP   = '2CWB01' 
			 *     And STACK_BED_GP   = '04' 
			 * 
			 */
			String selectWBShiftSTACKER    = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.selectWBShiftSTACKER";
			JDTORecord selectwbshiftstaker = ydStackLayerDAO.requestgetData(selectWBShiftSTACKER, new Object[]{ STACKCOLGP, STACKBEDGP });
			
			String TmpStackerColGp     = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_COL_GP"), "");
			String TmpStackerBedGp     = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_GP"), "");
			String TmpStackerXAxis     = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_X_AXIS"), "");
			String TmpStackerYAxis     = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_Y_AXIS"), "");
			String TmpStackerZAxis     = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_Z_AXIS"), "");
			String TmpStackerQntyMax   = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_QNTY_MAX"), "");
			String TmpStackerWtMax     = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_WT_MAX"), "");
			String TmpStackerHighMax   = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_HIGH_MAX"), "");
			String TmpStackerWMax      = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_W_MAX"), "");
			String TmpStackerLenMax    = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_LEN_MAX"), "");
			String TmpStackerQntyCurr  = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_QNTY_CURR"), "");
			String TmpStackerWtCurr    = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_WT_CURR"), "");
			String TmpStackerHighCurr  = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_HIGH_CURR"), "");
			String TmpStackerWCurr     = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_W_CURR"), "");
			String TmpStackerLenCurr   = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_LEN_CURR"), "");
			String TmpStackerAbleQnty  = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_ABLE_QNTY"), "");
			String TmpStackerAbleWt    = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_ABLE_WT"), "");
			String TmpStackerAbleHigh  = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_ABLE_HIGH"), "");
			String TmpStackerAbleW     = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_ABLE_W"), "");
			String TmpStackerAbleLen   = StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_ABLE_LEN"), "");
			String TmpStackerActiveStat= StringHelper.evl(selectwbshiftstaker.getFieldString("STACK_BED_ACTIVE_STAT"), "");
			String TmpStackerRegister  = StringHelper.evl(selectwbshiftstaker.getFieldString("REGISTER"), "");
			String TmpStackerRegddtt   = StringHelper.evl(selectwbshiftstaker.getFieldString("REG_DDTT"), "");
			String TmpStackerModifier  = StringHelper.evl(selectwbshiftstaker.getFieldString("MODIFIER"), "");
			String TmpStackerModddtt   = StringHelper.evl(selectwbshiftstaker.getFieldString("MOD_DDTT"), "");
			String TmpStackerDelYn     = StringHelper.evl(selectwbshiftstaker.getFieldString("DEL_YN"), "");
			
		   /*
		    * ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateWBShiftSTACKER
		    * Update TB_YM_STACKER
		    *    Set STACK_BED_X_AXIS      = TmpStackerXAxis,	   STACK_BED_Y_AXIS      = TmpStackerYAxis,
			*		 STACK_BED_Z_AXIS      = TmpStackerZAxis,      STACK_BED_QNTY_MAX    = TmpStackerQntyMax,
			*		 STACK_BED_WT_MAX      = TmpStackerWtMax,	   STACK_BED_HIGH_MAX    = TmpStackerHighMax,
			*		 STACK_BED_W_MAX       = TmpStackerWMax,	   STACK_BED_LEN_MAX     = TmpStackerLenMax,
			*		 STACK_BED_QNTY_CURR   = TmpStackerQntyCurr,   STACK_BED_WT_CURR     = TmpStackerWtCurr,
			*		 STACK_BED_HIGH_CURR   = TmpStackerHighCurr,   STACK_BED_W_CURR      = TmpStackerWCurr,
			*		 STACK_BED_LEN_CURR    = TmpStackerLenCurr,	   STACK_BED_ABLE_QNTY   = TmpStackerAbleQnty,
			*		 STACK_BED_ABLE_WT     = TmpStackerAbleWt,	   STACK_BED_ABLE_HIGH   = TmpStackerAbleHigh,
			*		 STACK_BED_ABLE_W      = TmpStackerAbleW,      STACK_BED_ABLE_LEN    = TmpStackerAbleLen,
			*		 STACK_BED_ACTIVE_STAT = TmpStackerActiveStat, REGISTER              = TmpStackerRegister,
			* 	     REG_DDTT              = TmpStackerRegddtt,    MODIFIER              = TmpStackerModifier,
			*		 MOD_DDTT              = TmpStackerModddtt,    DEL_YN                = TmpStackerDelYn
			*  Where stack_col_gp = '2CWB01'
			*	 And stack_bed_gp = '05'  
		    */	
			String TmpSTACKBEDGP = "0" + (Integer.parseInt(STACKBEDGP) + 1);
			
			String updateWBShiftSTACKER  = "ym.facilitystatus.facilityinquiry.dao.YdStackerDAO.updateWBShiftSTACKER";
			int updatewbShiftSTACKER     = ydStackLayerDAO.requestupdateData(updateWBShiftSTACKER, new Object[]{ 
						       TmpStackerXAxis.trim(),      TmpStackerYAxis.trim(), 
						       TmpStackerZAxis.trim(),      TmpStackerQntyMax.trim(),  TmpStackerWtMax.trim(),    TmpStackerHighMax.trim(),
						       TmpStackerWMax.trim(),       TmpStackerLenMax.trim(),   TmpStackerQntyCurr.trim(), TmpStackerWtCurr.trim(),
						       TmpStackerHighCurr.trim(),   TmpStackerWCurr.trim(),    TmpStackerLenCurr.trim(),  TmpStackerAbleQnty.trim(),
						       TmpStackerAbleWt.trim(),     TmpStackerAbleHigh.trim(), TmpStackerAbleW.trim(),    TmpStackerAbleLen.trim(),
						       TmpStackerActiveStat.trim(), TmpStackerRegister.trim(), TmpStackerRegddtt.trim(),  TmpStackerModifier.trim(),
						       TmpStackerModddtt.trim(),    TmpStackerDelYn.trim(),
							   STACKCOLGP,                  TmpSTACKBEDGP });
	    	
	    	return isSuccess;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	}
	

	/**
	 * 오퍼레이션명 : 
	 *
	 * Mill LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당업무로직을 처리한다.
        * 전문내용을 JDTORecord로 파싱한다.
        * 업무 로직
     	 * 1.TC_CD - CF1PB27 (I/F ID : YM-BIF-039)
     	 * 2.Mill LEVEL2로 부터 BSY_W/B Information Request 정보를 수신
	 * 3.STACK_COL_GP:적치열(2CWB01), STACK_BED_GP:적치BED(01~05), STACK_LAYER_GP:적치단(01~04)
	 *  3-1.적치BED(05번지): 적치단 01에서 05 Slab가 존재하는지 Select한다.
 	 *	                     존재 한다면 Slab No로 공통 Table에 두께, 폭, 길이, 중량을 Select한다. 
	 *	  3-2.적치BED(04번지): 적치단 01에서 04로 Slab가 존재하는지 Select한다.
 	 *	                     존재 한다면 Slab No로 공통 Table에 두께, 폭, 길이, 중량을 Select한다. 
        * 4.송신전문 편집을 한다.
        * 5.송신한다. 
        *
	 *      1         2         3         4         5  
	 *       0....v....0....v....0....v....0....v....0
	 *      CF1PB272005-09-0510:15:31I0030
        * 수신 
	 *		CF1PB27	1	전문코드	C	7
	 *		CF1PB27	2	발생일자	C	10
	 *		CF1PB27	3	발생시간	C	8
	 *		CF1PB27	4	전문구분	C	1
	 *		CF1PB27	5	전문길이	C	4
	 * 
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */ 
	public boolean receiveRollerTableStatus(String sMessage) throws java.rmi.RemoteException{ 
		/**
		logger.println("전문코드 07=="   +jDTORecord.getFieldString("전문코드"));
		logger.println("발생일자 10=="   +jDTORecord.getFieldString("발생일자"));
		logger.println("발생시간 08=="   +jDTORecord.getFieldString("발생시간"));
		logger.println("전문구분 01=="   +jDTORecord.getFieldString("전문구분")); // I:Initialize, U:Update, D:Delete, R:Re-request
		logger.println("전문길이 04=="   +jDTORecord.getFieldString("전문길이"));
		*
		*/
		boolean isSuccess = false;

		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger           = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveRollerTableStatus()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		
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
				jDTORecord.setField("MSG_ID"	, "CF1PB27");  
				EJBConnector ydEjbCon = new EJBConnector("default", this);
			    ydEjbCon.trx("YmCommEJB", "rcvInterface", jDTORecord);
				return true;
			}				
			//--------------------------------------------------------------------
			
			String SendCF1BP14  		= "";
			String SendMsgInfo 	 	    = "";	
			
			SendMsgInfo = setWorkBeamStatusMsgInfo(SendCF1BP14);
			
			logger.println(LogLevel.DEBUG,this,"SendCF1BP14");

			// BSY_W/B Information 요구를 수신하면 W/B 4,5 정보를 송신한다.
			// CF1BP14send Method Call
			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CF1BP14send",new Class[]{String.class},new Object[]{ SendMsgInfo.trim() });					
			
			logger.println(LogLevel.DEBUG,this,"End-receiveRollerTableStatus()");
			
			return isSuccess;
			    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	} 		
	
	private String setWorkBeamStatusMsgInfo(String Send_CF1BP14){
		
		StringBuffer sMsg = new StringBuffer();
		
		String TC_CD                    = ""; //CF1BP14
		String TC_Date                  = "";
		String TC_Time					= "";
		String TC_ID 					= ""; //I
		String TC_Length 				= ""; //0254
		String WB5ROWSlab1SlabNo		= "";
		String WB5ROWSlab1SPARE			= "";
		String WB5ROWSlab1Thinckness	= "";
		String WB5ROWSlab1Width			= "";
		String WB5ROWSlab1Length		= "";
		String WB5ROWSlab1Weight		= "";
		String WB5ROWSlab2SlabNo		= "";
		String WB5ROWSlab2SPARE			= "";
		String WB5ROWSlab2Thinckness	= "";
		String WB5ROWSlab2Width			= "";
		String WB5ROWSlab2Length		= "";
		String WB5ROWSlab2Weight		= "";
		String WB5ROWSlab3SlabNo		= "";
		String WB5ROWSlab3SPARE			= "";
		String WB5ROWSlab3Thinckness	= "";
		String WB5ROWSlab3Width			= "";
		String WB5ROWSlab3Length		= "";
		String WB5ROWSlab3Weight		= "";
		String WB5ROWSlab4SlabNo		= "";
		String WB5ROWSlab4SPARE 		= "";
		String WB5ROWSlab4Thinckness	= "";
		String WB5ROWSlab4Width			= "";
		String WB5ROWSlab4Length		= "";
		String WB5ROWSlab4Weight		= "";
		String WB4ROWSlab1SlabNo		= "";
		String WB4ROWSlab1SPARE			= "";
		String WB4ROWSlab1Thinckness	= "";
		String WB4ROWSlab1Width			= "";
		String WB4ROWSlab1Length		= "";
		String WB4ROWSlab1Weight		= "";
		String WB4ROWSlab2SlabNo		= "";
		String WB4ROWSlab2SPARE			= "";
		String WB4ROWSlab2Thinckness	= "";
		String WB4ROWSlab2Width			= "";
		String WB4ROWSlab2Length		= "";
		String WB4ROWSlab2Weight		= "";
		String WB4ROWSlab3SlabNo		= "";
		String WB4ROWSlab3SPARE			= "";
		String WB4ROWSlab3Thinckness	= "";
		String WB4ROWSlab3Width			= "";
		String WB4ROWSlab3Length		= "";
		String WB4ROWSlab3Weight		= "";
		String WB4ROWSlab4SlabNo		= "";
		String WB4ROWSlab4SPARE			= "";
		String WB4ROWSlab4Thinckness	= "";
		String WB4ROWSlab4Width			= "";
		String WB4ROWSlab4Length		= "";
		String WB4ROWSlab4Weight		= "";
		
		int	iTC_CD						= 7;
		int	iTC_Date					= 10;
		int	iTC_Time					= 8;
		int	iTC_ID						= 1;
		int	iTC_Length					= 4;
		int	iWB5ROWSlab1SlabNo			= 11;
		int	iWB5ROWSlab1SPARE			= 1;
		int	iWB5ROWSlab1Thinckness		= 3;
		int	iWB5ROWSlab1Width			= 4;
		int	iWB5ROWSlab1Length			= 5;
		int	iWB5ROWSlab1Weight			= 5;
		int	iWB5ROWSlab2SlabNo			= 11;
		int	iWB5ROWSlab2SPARE			= 1;
		int	iWB5ROWSlab2Thinckness		= 3;
		int	iWB5ROWSlab2Width			= 4;
		int	iWB5ROWSlab2Length			= 5;
		int	iWB5ROWSlab2Weight			= 5;
		int	iWB5ROWSlab3SlabNo			= 11;
		int	iWB5ROWSlab3SPARE			= 1;
		int	iWB5ROWSlab3Thinckness		= 3;
		int	iWB5ROWSlab3Width			= 4;
		int	iWB5ROWSlab3Length			= 5;
		int	iWB5ROWSlab3Weight			= 5;
		int	iWB5ROWSlab4SlabNo			= 11;
		int	iWB5ROWSlab4SPARE 			= 1;
		int	iWB5ROWSlab4Thinckness		= 3;
		int	iWB5ROWSlab4Width			= 4;
		int	iWB5ROWSlab4Length			= 5;
		int	iWB5ROWSlab4Weight			= 5;
		int	iWB4ROWSlab1SlabNo			= 11;
		int	iWB4ROWSlab1SPARE			= 1;
		int	iWB4ROWSlab1Thinckness		= 3;
		int	iWB4ROWSlab1Width			= 4;
		int	iWB4ROWSlab1Length			= 5;
		int	iWB4ROWSlab1Weight			= 5;
		int	iWB4ROWSlab2SlabNo			= 11;
		int	iWB4ROWSlab2SPARE			= 1;
		int	iWB4ROWSlab2Thinckness		= 3;
		int	iWB4ROWSlab2Width			= 4;
		int	iWB4ROWSlab2Length			= 5;
		int	iWB4ROWSlab2Weight			= 5;
		int	iWB4ROWSlab3SlabNo			= 11;
		int	iWB4ROWSlab3SPARE			= 1;
		int	iWB4ROWSlab3Thinckness		= 3;
		int	iWB4ROWSlab3Width			= 4;
		int	iWB4ROWSlab3Length			= 5;
		int	iWB4ROWSlab3Weight			= 5;
		int	iWB4ROWSlab4SlabNo			= 11;
		int	iWB4ROWSlab4SPARE			= 1;
		int	iWB4ROWSlab4Thinckness		= 3;
		int	iWB4ROWSlab4Width			= 4;
		int	iWB4ROWSlab4Length			= 5;
		int	iWB4ROWSlab4Weight			= 5;
		
		try{
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC1");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			}
			
			YdStockDAO ydStockDAO 	        = new YdStockDAO();
			YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
			YdWBookDAO ydWBookDAO           = new YdWBookDAO();
						
			TC_CD 						= "CF1BP14"; 
			TC_Date 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			TC_Time						= YmCommonUtil.getCurDate("hh-mm-ss");
			TC_ID						= "I";
			TC_Length					= "0254";
			
			// 적치단(TB_YM_STACKLAYER) Select
			// 적치열(STACK_COL_GP) = '2CWB01', 적치BED구분(STACK_BED_GP) = '05', 적치단상태(STACK_LAYER_STAT) = 'L'
			// select STOCK_ID
			//   From USRYMA.TB_YM_STACKLAYER 
			//  where STACK_COL_GP = '2CWB01' And (STACK_BED_GP = '05' Or STACK_BED_GP = '04') 
			//  Order by STACK_BED_GP desc
			String stackLayCount = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStockCount";
			List stackLayerStock = ydStackLayerDAO.getExistStockID(stackLayCount, new Object[]{ 
					               YmCommonConst.STACK_COL_GP_2CWB01, YmCommonConst.STACK_BED_GP_05, YmCommonConst.STACK_BED_GP_04 }); 
  
			logger.println(LogLevel.DEBUG,this, "1================================");
			logger.println(LogLevel.DEBUG,this, "stackLayerStock="+ stackLayerStock);
			logger.println(LogLevel.DEBUG,this, "1================================");
			
			JDTORecord TmpSelBedAddr = null;
			int MaxRec               = stackLayerStock.size();
			String[] TmpStockId      = new String[8];
			
			for (int ii=0; ii<8; ii++){
				TmpSelBedAddr     = (JDTORecord) stackLayerStock.get(ii);
				TmpStockId[ii]    = StringHelper.evl(TmpSelBedAddr.getFieldString("STOCK_ID"), "");
				
				logger.println(LogLevel.DEBUG,this, "2==============================");
				logger.println(LogLevel.DEBUG,this, "TmpStockId[ii]= "+ TmpStockId[ii]);
				logger.println(LogLevel.DEBUG,this, "2==============================");
			}						
			
			// SELECT   SLAB_T,	    --SLAB 두께
			//	        SLAB_W,	    --SLAB 폭
			//		    SLAB_LEN,	--SLAB 길이
			//		    SLAB_WT 	--SLAB 중량
			//	 FROM   TB_PM_SLABCOMM
			//  WHERE   SLAB_NO = TmpStockId[ii]
			JDTORecord slabInfo = null;
			for (int ii=0; ii<8; ii++){
	            String sQueryId     = "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectSlabInfo";
	            
	            if (TmpStockId[ii] == null || TmpStockId[ii].trim().equals("")){
	            	
	            }else {
	            	slabInfo = ydStockDAO.getData(sQueryId, new Object[]{ TmpStockId[ii] });	
	            }
	            
				logger.println(LogLevel.DEBUG,this, "3==================================");
				logger.println(LogLevel.DEBUG,this, "slabInfo= "+ slabInfo  + " ii= "+ ii);
				logger.println(LogLevel.DEBUG,this, "3==================================");
				
	            switch (ii) {
					case 0:
	             		if(TmpStockId[ii] != null && !TmpStockId[ii].trim().equals("")) {
				 			WB5ROWSlab4SlabNo			= TmpStockId[ii];
							WB5ROWSlab4SPARE			= "";
							WB5ROWSlab4Thinckness		= StringHelper.evl(slabInfo.getFieldString("두께"), "");
							WB5ROWSlab4Width			= StringHelper.evl(slabInfo.getFieldString("폭"), "");
							WB5ROWSlab4Length			= StringHelper.evl(slabInfo.getFieldString("길이"), "");
							WB5ROWSlab4Weight			= StringHelper.evl(slabInfo.getFieldString("중량"), "");
							WB5ROWSlab4SPARE			= "";	
	             		}else{
	            			WB5ROWSlab4SlabNo			= "";
	            			WB5ROWSlab4SPARE			= "";
	            			WB5ROWSlab4Thinckness		= "";
	            			WB5ROWSlab4Width			= "";
	            			WB5ROWSlab4Length			= "";
	            			WB5ROWSlab4Weight			= "";
	            			WB5ROWSlab4SPARE			= "";
	             		}
						break;
						
					case 1:	
						if(TmpStockId[ii] != null && !TmpStockId[ii].trim().equals("")) {
				 			WB5ROWSlab3SlabNo			= TmpStockId[ii];
							WB5ROWSlab3SPARE			= "";
							WB5ROWSlab3Thinckness		= StringHelper.evl(slabInfo.getFieldString("두께"), "");
							WB5ROWSlab3Width			= StringHelper.evl(slabInfo.getFieldString("폭"), "");
							WB5ROWSlab3Length			= StringHelper.evl(slabInfo.getFieldString("길이"), "");
							WB5ROWSlab3Weight			= StringHelper.evl(slabInfo.getFieldString("중량"), "");
	             		}else{
	            			WB5ROWSlab3SlabNo			= "";
	            			WB5ROWSlab3SPARE			= "";
	            			WB5ROWSlab3Thinckness		= "";
	            			WB5ROWSlab3Width			= "";
	            			WB5ROWSlab3Length			= "";
	            			WB5ROWSlab3Weight			= "";
	             		}	
						break;

					case 2:	
						if(TmpStockId[ii] != null && !TmpStockId[ii].trim().equals("")) {
				 			WB5ROWSlab2SlabNo			= TmpStockId[ii];
							WB5ROWSlab2SPARE			= "";
							WB5ROWSlab2Thinckness		= StringHelper.evl(slabInfo.getFieldString("두께"), "");
							WB5ROWSlab2Width			= StringHelper.evl(slabInfo.getFieldString("폭"), "");
							WB5ROWSlab2Length			= StringHelper.evl(slabInfo.getFieldString("길이"), "");
							WB5ROWSlab2Weight			= StringHelper.evl(slabInfo.getFieldString("중량"), "");
	             		}else{
	            			WB5ROWSlab2SlabNo			= "";
	            			WB5ROWSlab2SPARE			= "";
	            			WB5ROWSlab2Thinckness		= "";
	            			WB5ROWSlab2Width			= "";
	            			WB5ROWSlab2Length			= "";
	            			WB5ROWSlab2Weight			= "";
	             		}	
						break;						

					case 3:
						if(TmpStockId[ii] != null && !TmpStockId[ii].trim().equals("")) {
				 			WB5ROWSlab1SlabNo			= TmpStockId[ii];
							WB5ROWSlab1SPARE			= "";
							WB5ROWSlab1Thinckness		= StringHelper.evl(slabInfo.getFieldString("두께"), "");
							WB5ROWSlab1Width			= StringHelper.evl(slabInfo.getFieldString("폭"), "");
							WB5ROWSlab1Length			= StringHelper.evl(slabInfo.getFieldString("길이"), "");
							WB5ROWSlab1Weight			= StringHelper.evl(slabInfo.getFieldString("중량"), "");
	             		}else{
	            			WB5ROWSlab1SlabNo			= "";
	            			WB5ROWSlab1SPARE			= "";
	            			WB5ROWSlab1Thinckness		= "";
	            			WB5ROWSlab1Width			= "";
	            			WB5ROWSlab1Length			= "";
	            			WB5ROWSlab1Weight			= "";
	             		}	
						break;			
						
					case 4:	
						if(TmpStockId[ii] != null && !TmpStockId[ii].trim().equals("")) {
				 			WB4ROWSlab4SlabNo			= TmpStockId[ii];
							WB4ROWSlab4SPARE			= "";
							WB4ROWSlab4Thinckness		= StringHelper.evl(slabInfo.getFieldString("두께"), "");
							WB4ROWSlab4Width			= StringHelper.evl(slabInfo.getFieldString("폭"), "");
							WB4ROWSlab4Length			= StringHelper.evl(slabInfo.getFieldString("길이"), "");
							WB4ROWSlab4Weight			= StringHelper.evl(slabInfo.getFieldString("중량"), "");
	             		}else{
	            			WB4ROWSlab4SlabNo			= "";
	            			WB4ROWSlab4SPARE			= "";
	            			WB4ROWSlab4Thinckness		= "";
	            			WB4ROWSlab4Width			= "";
	            			WB4ROWSlab4Length			= "";
	            			WB4ROWSlab4Weight			= "";
	             		}	
						break;

					case 5:	
						if(TmpStockId[ii] != null && !TmpStockId[ii].trim().equals("")) {
				 			WB4ROWSlab3SlabNo			= TmpStockId[ii];
							WB4ROWSlab3SPARE			= "";
							WB4ROWSlab3Thinckness		= StringHelper.evl(slabInfo.getFieldString("두께"), "");
							WB4ROWSlab3Width			= StringHelper.evl(slabInfo.getFieldString("폭"), "");
							WB4ROWSlab3Length			= StringHelper.evl(slabInfo.getFieldString("길이"), "");
							WB4ROWSlab3Weight			= StringHelper.evl(slabInfo.getFieldString("중량"), "");
	             		}else{
	            			WB4ROWSlab3SlabNo			= "";
	            			WB4ROWSlab3SPARE			= "";
	            			WB4ROWSlab3Thinckness		= "";
	            			WB4ROWSlab3Width			= "";
	            			WB4ROWSlab3Length			= "";
	            			WB4ROWSlab3Weight			= "";
	             		}	
						break;			
						
					case 6:	
						if(TmpStockId[ii] != null && !TmpStockId[ii].trim().equals("")) {
				 			WB4ROWSlab2SlabNo			= TmpStockId[ii];
							WB4ROWSlab2SPARE			= "";
							WB4ROWSlab2Thinckness		= StringHelper.evl(slabInfo.getFieldString("두께"), "");
							WB4ROWSlab2Width			= StringHelper.evl(slabInfo.getFieldString("폭"), "");
							WB4ROWSlab2Length			= StringHelper.evl(slabInfo.getFieldString("길이"), "");
							WB4ROWSlab2Weight			= StringHelper.evl(slabInfo.getFieldString("중량"), "");
	             		}else{
	            			WB4ROWSlab2SlabNo			= "";
	            			WB4ROWSlab2SPARE			= "";
	            			WB4ROWSlab2Thinckness		= "";
	            			WB4ROWSlab2Width			= "";
	            			WB4ROWSlab2Length			= "";
	            			WB4ROWSlab2Weight			= "";
	             		}	
						break;			
						
					case 7:
						if(TmpStockId[ii] != null && !TmpStockId[ii].trim().equals("")) {
				 			WB4ROWSlab1SlabNo			= TmpStockId[ii];
							WB4ROWSlab1SPARE			= "";
							WB4ROWSlab1Thinckness		= StringHelper.evl(slabInfo.getFieldString("두께"), "");
							WB4ROWSlab1Width			= StringHelper.evl(slabInfo.getFieldString("폭"), "");
							WB4ROWSlab1Length			= StringHelper.evl(slabInfo.getFieldString("길이"), "");
							WB4ROWSlab1Weight			= StringHelper.evl(slabInfo.getFieldString("중량"), "");
	             		}else{
	            			WB4ROWSlab1SlabNo			= "";
	            			WB4ROWSlab1SPARE			= "";
	            			WB4ROWSlab1Thinckness		= "";
	            			WB4ROWSlab1Width			= "";
	            			WB4ROWSlab1Length			= "";
	            			WB4ROWSlab1Weight			= "";
	             		}	
						break;														
				}
			}
			
			sMsg.append(YmCommonUtil.FillToString(TC_CD					,iTC_CD));
			sMsg.append(YmCommonUtil.FillToString(TC_Date				,iTC_Date));
			sMsg.append(YmCommonUtil.FillToString(TC_Time				,iTC_Time));
			sMsg.append(YmCommonUtil.FillToString(TC_ID					,iTC_ID));
			sMsg.append(YmCommonUtil.FillToString(TC_Length				,iTC_Length));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab1SlabNo		,iWB5ROWSlab1SlabNo));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab1SPARE		,iWB5ROWSlab1SPARE));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab1Thinckness	,iWB5ROWSlab1Thinckness));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab1Width		,iWB5ROWSlab1Width));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab1Length		,iWB5ROWSlab1Length));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab1Weight		,iWB5ROWSlab1Weight));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab2SlabNo		,iWB5ROWSlab2SlabNo));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab2SPARE		,iWB5ROWSlab2SPARE));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab2Thinckness	,iWB5ROWSlab2Thinckness));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab2Width		,iWB5ROWSlab2Width));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab2Length		,iWB5ROWSlab2Length));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab2Weight		,iWB5ROWSlab2Weight));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab3SlabNo		,iWB5ROWSlab3SlabNo));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab3SPARE		,iWB5ROWSlab3SPARE));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab3Thinckness	,iWB5ROWSlab3Thinckness));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab3Width		,iWB5ROWSlab3Width));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab3Length		,iWB5ROWSlab3Length));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab3Weight		,iWB5ROWSlab3Weight));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab4SlabNo		,iWB5ROWSlab4SlabNo));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab4SPARE		,iWB5ROWSlab4SPARE));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab4Thinckness	,iWB5ROWSlab4Thinckness));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab4Width		,iWB5ROWSlab4Width));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab4Length		,iWB5ROWSlab4Length));
			sMsg.append(YmCommonUtil.FillToString(WB5ROWSlab4Weight		,iWB5ROWSlab4Weight));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab1SlabNo		,iWB4ROWSlab1SlabNo));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab1SPARE		,iWB4ROWSlab1SPARE));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab1Thinckness	,iWB4ROWSlab1Thinckness));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab1Width		,iWB4ROWSlab1Width));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab1Length		,iWB4ROWSlab1Length));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab1Weight		,iWB4ROWSlab1Weight));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab2SlabNo		,iWB4ROWSlab2SlabNo));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab2SPARE		,iWB4ROWSlab2SPARE));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab2Thinckness	,iWB4ROWSlab2Thinckness));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab2Width		,iWB4ROWSlab2Width));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab2Length		,iWB4ROWSlab2Length));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab2Weight		,iWB4ROWSlab2Weight));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab3SlabNo		,iWB4ROWSlab3SlabNo));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab3SPARE		,iWB4ROWSlab3SPARE));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab3Thinckness	,iWB4ROWSlab3Thinckness));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab3Width		,iWB4ROWSlab3Width));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab3Length		,iWB4ROWSlab3Length));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab3Weight		,iWB4ROWSlab3Weight));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab4SlabNo		,iWB4ROWSlab4SlabNo));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab4SPARE		,iWB4ROWSlab4SPARE));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab4Thinckness	,iWB4ROWSlab4Thinckness));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab4Width		,iWB4ROWSlab4Width));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab4Length		,iWB4ROWSlab4Length));
			sMsg.append(YmCommonUtil.FillToString(WB4ROWSlab4Weight		,iWB4ROWSlab4Weight));
			
		}catch(Exception e){
	        throw new EJBServiceException(e);
	    }
	    return sMsg.toString();
	}			
}