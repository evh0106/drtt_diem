package com.inisteel.cim.ym.workrequest.wrequestaccept.session;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import jspeed.base.ejb.BaseSessionBean;
import jspeed.base.ejb.EJBConnector;
import jspeed.base.log.LogLevel;
import jspeed.base.log.LogService; 
import jspeed.base.log.LogServiceConfig;
import jspeed.base.log.Logger;
import jspeed.base.record.JDTORecord;
import jspeed.base.record.JDTORecordFactory;
import jspeed.base.util.StringHelper;
import com.inisteel.cim.common.exception.DAOException;
import com.inisteel.cim.common.exception.EJBServiceException;
import com.inisteel.cim.common.parser.Level2Parser;
import com.inisteel.cim.ym.bcommon.session.YmComm;
import com.inisteel.cim.ym.common.dao.*;
import com.inisteel.cim.ym.common.YmCommonConst;
import com.inisteel.cim.ym.common.YmCommonDB;
import com.inisteel.cim.ym.common.YmCommonUtil;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO;
import com.inisteel.cim.ym.steelinfo.steelinforecv.dao.YdStockDAO;
import com.inisteel.cim.ym.facilitystatus.facilityinquiry.dao.CraneSchDAO;
import com.inisteel.cim.ym.workrequest.wrequestaccept.dao.YdWBookDAO;
/**
 * 이클래스는 업무 화면의 메뉴를 관리하기 위한 Session EJB클래스입니다.
 *
 * @ejb.bean name="CCExtWrkOrdRegEJB" jndi-name="JNDICCExtWrkOrdReg" type="Stateless"
 *           view-type="remote" display-name="" description=""
 * @weblogic.enable-call-by-reference True
 * @weblogic.pool initial-beans-in-free-pool="10" max-beans-in-free-pool="100"
 * @ejb.transaction type="Required"
 */
public class CCExtWrkOrdRegSBean extends BaseSessionBean {
	public void ejbCreate() {
	}
	 // SJH    
	private YmComm ymComm = new YmComm();	
	
     /**
	 * 오퍼레이션명 : 확장 Conveyor Line Off 요구(CN1PB08)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveCCExtWrkOrd(String sMessage) throws java.rmi.RemoteException{ 
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

			return receiveCCExtWrkOrd(jDTORecord);
		    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	}
	
	/**
	 * 오퍼레이션명 : 확장 Conveyor Line Off 요구
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return
	 * @throws 
	 */
	public boolean receiveCCExtWrkOrd(JDTORecord jDTORecord) throws java.rmi.RemoteException{ 
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveCCExtWrkOrd()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO			dao 		= new CraneSchDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			int iResult = 0;
			
			String CoilNo    = StringHelper.evl(jDTORecord.getFieldString("CoilNo"), "").trim();
			String yardId    = StringHelper.evl(jDTORecord.getFieldString("야드구분"), "");
			String dongId    = StringHelper.evl(jDTORecord.getFieldString("동구분"), "");
			String equipType = StringHelper.evl(jDTORecord.getFieldString("설비종류"), "");
			String equipNo   = StringHelper.evl(jDTORecord.getFieldString("설비번호"), "");
			String addrNo    = StringHelper.evl(jDTORecord.getFieldString("번지"), "");
			// 사용안함. 최규성 ==========
			String stackNo   = StringHelper.evl(jDTORecord.getFieldString("단"), "");
			String weight    = StringHelper.evl(jDTORecord.getFieldString("중량"), "");
			String width     = StringHelper.evl(jDTORecord.getFieldString("폭"), "");
			String slength   = StringHelper.evl(jDTORecord.getFieldString("길이"), "");
			//===========================

			// 적치열 STACK_COL_GP 
			String stackColGp = "" + yardId.trim() + ""+ dongId.trim() + "" + equipType.trim() + "" + equipNo.trim() + "";
			
			/**
			 *  이정훈 2007.06.26
			 *  1. 확장 Conv Line Off 요구시 야드 및 수조탱크에 존재시 Skip
	 		 *	
	 		 */
			List stockL1	= dao.getStackLayerInfoWithStockId_04(CoilNo);	
			logger.println(LogLevel.DEBUG,this, "stockL1 갯수 확인 "+stockL1.size());
			if(stockL1.size() > 0)
			{
				logger.println(LogLevel.DEBUG,this, "Coil이 야드에 적치 중");
				return false; 	
			}
			
			/**
			 *  이정훈 2007.06.26
			 *  1. 확장 Conv Line Off 요구시 야드 및 수조탱크에 존재시 Skip
	 		 *	
	 		 */
			List stockL2	= dao.getStackLayerInfoWithStockId_05(CoilNo);	
			logger.println(LogLevel.DEBUG,this, "stockL2 갯수 확인 "+stockL2.size());
			if(stockL2.size() > 0)
			{
				logger.println(LogLevel.DEBUG,this, "Coil이 작업 예약 또는 스케쥴 편성 중");
				return false; 	
			}
			/**
	 		 *	1.	3XDC01 라인 삭제
	 		 */
	 		 
			int iSeq = YmCommonDB.deleteConveyorInfo(YmCommonConst.STACK_COL_GP_3XDC01, CoilNo);
			
			/**
	 		 *	2.	MAP에 존재하는 저장품 삭제
	 		 */
			List stockL	= dao.getStackLayerInfoWithStockId_03(CoilNo);
						 
			if(stockL != null)
			{	
				String sStackColGp   = "";
				String sStackBedGp   = "";
				String sStackLayerGp = "";
				JDTORecord stackV 	   = null;
			 
				for(int inx = 0; inx < stockL.size() ; inx++)
				{
				 	stackV = (JDTORecord)stockL.get(inx);
				 	
				 	sStackColGp   = StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sStackBedGp   = StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
					sStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");
							   					   		
				 	/* 
					 * 적치단 UP위치 Clear
					 * tb_ym_stacklayer Table : stock_id 		 = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat = 'E'(적치가능)
					 */	
					iSeq = dao.updateCraneStackLayerStat(sStackColGp,
			    										 sStackBedGp,
			    										 sStackLayerGp,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
				} 
			}
			
			/**
	 		 *	3.	작업예약이 존재하면 작업예약을 삭제한다.
	 		 */
			JDTORecord stockJr = dao.getStockInfo(CoilNo);
			
			if(stockJr != null){
				
				String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
				
				if(!"".equals(sWbookId)){
					
					iSeq = dao.updateStockWbookId(CoilNo,
						  				  		  ""); 
					
					iSeq = dao.deleteWbookInfo(sWbookId);
					
					logger.println(LogLevel.DEBUG, this, "작업예약취소=> 작업예약 삭제="+iSeq);
				}
			}
			
			/*
			 * 수신한 COIL_NO 해당위치와 STACK_LAYER_STAT 상태를  'L' Update 한다. 
		     */
			iSeq = dao.updateCraneStackLayerStat(stackColGp,
	    										 addrNo,
	    										 YmCommonConst.STACK_LAYER_GP_01,
	    										 CoilNo,
	    										 YmCommonConst.STACK_LAYER_STAT_L);
	    	
			String sQuery3 		= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGpStat";
			JDTORecord colJr  	= ydStackLayerDAO.requestgetData(sQuery3, new Object[]{ CoilNo });

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
															   				CoilNo });
			/*
			 *	작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
			 */
			String sQuery5 		= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
			JDTORecord wbookJr 	= ydStackLayerDAO.requestFind(sQuery5);

			if (wbookJr == null){
				logger.println(LogLevel.DEBUG,this, "작업예약ID 생성 Error");
				return false; 																				
			}
			
			String sWbookId  = StringHelper.evl(wbookJr.getFieldString("WBOOK_SELECT"), "");
    		
    		
    		/*
			 *	작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 
			 *	작업예약조, 등록자, 등록일시) 한다.
			 */
			String sQuery6 = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
			int iSeq2      = ydWBookDAO.requestinsertData(sQuery6, 
														  new Object[]{sWbookId, 
														  			   sYdGp, 
														  			   sBayGp, 
														  			   YmCommonConst.NEW_SCH_WORK_KIND_CELO, 
					               									   YmCommonUtil.getWorkDuty(), 
					               									   YmCommonUtil.getWorkParty() });	
			
			JDTORecord stockV	= dao.getCoilCommonInfo(CoilNo);
			String sStockMv     = "";
			String sProgCd 		= StringHelper.evl(stockV.getFieldString("CURR_PROG_CD"),"").trim();
			String sCoilProc    = "";
	    	String sNextProc 	= StringHelper.evl(stockV.getFieldString("차공정"),"").trim();
			String sPlanProc 	= StringHelper.evl(stockV.getFieldString("계획공정"),"").trim();
			
			if("".equals(sNextProc)){
				sCoilProc = sPlanProc;
			}else{
				sCoilProc = sNextProc;
			}
			
			if(YmCommonConst.CURR_PROG_CD_COIL_1.equals(sProgCd)){		// 생산예정
	    		
				sStockMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_1C;		// 생산예정
	    		
	    	}else{
	    		
	    		if(YmCommonConst.SHEAR_SUPPLY_GP_5K.equals(sCoilProc)){		 //B열연 SPM
				
					sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A2;		//SPM 추출
				}else if(YmCommonConst.SHEAR_SUPPLY_GP_6K.equals(sCoilProc)){ //B열연 SPM2
				
					sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A6;		//SPM2 추출
				}else if(YmCommonConst.SHEAR_SUPPLY_GP_5H.equals(sCoilProc)){//B열연 HFL
				
					sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A1;		//HFL 추출
				}else if(YmCommonConst.SHEAR_SUPPLY_GP_5T.equals(sCoilProc)){//B열연 수냉재
				
					sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A3;		//수냉재 추출
				}else if(YmCommonConst.SHEAR_SUPPLY_GP_5A.equals(sCoilProc)){//B열연 공냉재
				
					sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A4;		//공냉재 추출
				}else if(YmCommonConst.SHEAR_SUPPLY_GP_6H.equals(sCoilProc)){//B열연 결속장 2010.12.21 JKJEUNG
					
					sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A7;		//HFL결속장  추출
				}else{
				
					sStockMv =  YmCommonConst.NEW_STOCK_MOVE_TERM_A2;
				}
			}
					
			logger.println(LogLevel.DEBUG,this,"저장품 이동조건 ="+sStockMv+"=");
			
			/*
			 *	저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
			 * UPDATE TB_YM_STOCK 
			 *    SET WBOOK_ID= ?
			 *      , STOCK_MOVE_TERM = ?
			 *  WHERE STOCK_ID = ?
			 */
			String sQuery7 = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
			int iSeq3      = ydStockDAO.requestupdateData(sQuery7, 
														  new Object[]{sWbookId, 
														  			   sStockMv,
														  			   CoilNo });		
			/*
			 * 2007.07.03 이정훈
			 * 확장 Conv Line Off 요구시 Sch 편성 X -> Sch Call
			 *                        Sch 편성 O -> Skip
			 * 권하시  역순으로 Sch Call
			 */
			String scheduleQuery   = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockSchIDCount_01";
			/*			
			 Select count(SCH_ID) as SchcodeCount 
			   From TB_YM_SCH 
			  Where SCH_WORK_KIND = ? 
			    And YD_GP         = ?
			    AND BAY_GP        = ?
			*/
			JDTORecord schWorkStat 		= ydStockDAO.getData(scheduleQuery, new Object[]{ YmCommonConst.NEW_SCH_WORK_KIND_CELO, sYdGp,dongId   });
            
			String tmpSchcodeCount      = StringHelper.evl(schWorkStat.getFieldString("SCHCODECOUNT"), "");
			
			int schedulecount           = Integer.parseInt(tmpSchcodeCount);
			
			logger.println(LogLevel.DEBUG,this,  " 스케줄테이블 스케줄 코드 갯수 = "+ schedulecount);
			
			if (schedulecount == 0 || !"C".equals(dongId)){
				logger.println(LogLevel.DEBUG,this, "== Sch Call==");
				// Coil Schedule EJB Call
				EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
				Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{ String.class},
																 new Object[]{ sWbookId });	
			}		
			// 고려계전으로 응답정보 송신 (L3->L2->고려계전)
			sendConvLineOffRequest(CoilNo);
				
			return true;
			    
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	}
	
	/**
	 * 오퍼레이션명 : 분기 Conveyor COIL Line Off Request
	 *  
	 * 	1.TC_CD - CF1PB12
        * 	2.Mill LEVEL2로 부터 분기 Conveyor COIL Line Off 요구 정보를 수신
        * 
	 *	COILNo	        CHAR	10		
	 *	SPARE		    CHAR	2		
	 *	근		        CHAR	4		
	 *	조		        CHAR	1		
	 *	분기위치코드		CHAR	2	1F:1STD, 2F:2STD, 3S:3STD, 4E:EXT STD
	 *	행선변경공정		CHAR	2	1F:1STD, 2F:2STD, 3S:3STD, 4E:EXT STD
	 *	SPARE		    CHAR	3		
	 *	행선변경이유		CHAR	2		
	 *	Location		CHAR	1		1:1STD,  2:2STD,  3:3STD,  4:EXT STD
	 *	SKIDNo		    CHAR	1		1~5 
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */ 
	public boolean receiveConvLineOffReq(String sMessage) throws java.rmi.RemoteException{
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"확인 확인");
		
		try{
			
			Level2Parser level2Parser 	= new Level2Parser();
			JDTORecord jDTORecord 		= level2Parser.parse(sMessage);
			
			String sBCOIL_EFF_YN = "N";
			JDTORecord jrChk = ymComm.getNewModuleEffYn_YM();
			sBCOIL_EFF_YN = jrChk.getFieldString("BCOIL_EFF_YN");

			logger.println(LogLevel.DEBUG,this,"확인"+sBCOIL_EFF_YN);

			if(sBCOIL_EFF_YN.equals("Y")) {
					
				//B열연 신규모듈 호출
				jDTORecord.setField("MSG_ID"	, "CF1PB12");  
				EJBConnector ydEjbCon = new EJBConnector("default", this);
			    ydEjbCon.trx("YmCommEJB", "rcvInterface", jDTORecord);
				return true;
			}				

			return receiveConvLineOffReq(jDTORecord);
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}    
	}
	
	/**
	 * 오퍼레이션명 : 분기 Conveyor COIL Line Off Request
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */  
	public boolean receiveConvLineOffReq(JDTORecord jDTORecord) throws java.rmi.RemoteException{
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveConvLineOffReq()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO			dao 		= new CraneSchDAO();
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			int iResult = 0;
			
			String TmpCoilNo       = StringHelper.evl(jDTORecord.getFieldString("COILNo"), "").trim();
			String TmpShift        = StringHelper.evl(jDTORecord.getFieldString("근"), "");
			String TmpGroup        = StringHelper.evl(jDTORecord.getFieldString("조"), "");
			String TmpPositionCode = StringHelper.evl(jDTORecord.getFieldString("분기위치코드"), "");
			String TmpChgProcess   = StringHelper.evl(jDTORecord.getFieldString("행선변경공정"), "");
			String TmpChgRes       = StringHelper.evl(jDTORecord.getFieldString("행선변경이유"), "");
			String TmpLocation     = StringHelper.evl(jDTORecord.getFieldString("Location"), "");
			String TmpSKIDNo       = StringHelper.evl(jDTORecord.getFieldString("SKIDNo"), "");

			String TmpStackColGp   = "";
 		    String TmpBedGp        = "";
			
 		    String yardId = YmCommonConst.YD_GP_3;
 		    String dongId = "";
 		   /**
			 *  이정훈 2007.06.26
			 *  분기 Conv Line Off 요구시 야드 및 수조탱크에 존재시 Skip
	 		 *	
	 		 */
			List stockL1	= dao.getStackLayerInfoWithStockId_04(TmpCoilNo);			 
			if(stockL1.size() > 0)
			{
				logger.println(LogLevel.DEBUG,this, "Coil이 야드에 적치 중");
				return false; 	
			}
 		    
			if (TmpLocation.trim().equals(YmCommonConst.CONVEYOR_LINE_1)){       // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				TmpStackColGp = YmCommonConst.STACK_COL_GP_3AST01;
				dongId        = YmCommonConst.BAY_GP_A;
			}else if (TmpLocation.trim().equals(YmCommonConst.CONVEYOR_LINE_2)){ // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				TmpStackColGp = YmCommonConst.STACK_COL_GP_3BST02;
				dongId        = YmCommonConst.BAY_GP_B;
			}else if (TmpLocation.trim().equals(YmCommonConst.CONVEYOR_LINE_3)){ // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				TmpStackColGp = YmCommonConst.STACK_COL_GP_3CST03;
				dongId        = YmCommonConst.BAY_GP_C;
			}else if (TmpLocation.trim().equals(YmCommonConst.CONVEYOR_LINE_4)){ // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				TmpStackColGp = YmCommonConst.STACK_COL_GP_3CEX01;
				dongId        = YmCommonConst.BAY_GP_C;
			}else if (TmpLocation.trim().equals(YmCommonConst.CONVEYOR_LINE_5)){ // 화면에서 EXT STD 에서 비상시 LINE-OFF 처리
				TmpStackColGp = YmCommonConst.STACK_COL_GP_3CEX01;
				dongId        = YmCommonConst.BAY_GP_C;
			}					
			
			if (TmpSKIDNo.trim().equals(YmCommonConst.SKID_NO_1)){         // 01번지 
				TmpBedGp  = YmCommonConst.STACK_BED_GP_01;					
			}else if (TmpSKIDNo.trim().equals(YmCommonConst.SKID_NO_2)){   // 02번지 
				TmpBedGp  = YmCommonConst.STACK_BED_GP_02;	
			}else if (TmpSKIDNo.trim().equals(YmCommonConst.SKID_NO_3)){   // 03번지 
				TmpBedGp  = YmCommonConst.STACK_BED_GP_03;
			}else if (TmpSKIDNo.trim().equals(YmCommonConst.SKID_NO_4)){   // 04번지 
				TmpBedGp  = YmCommonConst.STACK_BED_GP_04;
			}else if (TmpSKIDNo.trim().equals(YmCommonConst.SKID_NO_5)){   // 05번지 
				TmpBedGp  = YmCommonConst.STACK_BED_GP_05;
			}
			
			
			if (TmpLocation.trim().equals(YmCommonConst.CONVEYOR_LINE_4)){
				
				boolean isTrue = receiveConvToExtConv(TmpCoilNo);
				
			}else{
				
				/**
		 		 *	1.	3XDC01 라인 삭제
		 		 */
		 		 
				int iSeq = YmCommonDB.deleteConveyorInfo(YmCommonConst.STACK_COL_GP_3XDC01, TmpCoilNo);		
				
				/**
		 		 *	2.	MAP에 존재하는 저장품 삭제
		 		 */
				List stockL	= dao.getStackLayerInfoWithStockId_03(TmpCoilNo);
							 
				if(stockL != null)
				{	
					String sStackColGp   = "";
					String sStackBedGp   = "";
					String sStackLayerGp = "";
					JDTORecord stackV 	   = null;
				 
					for(int inx = 0; inx < stockL.size() ; inx++)
					{
					 	stackV = (JDTORecord)stockL.get(inx);
					 	
					 	sStackColGp   = StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
						sStackBedGp   = StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
						sStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");
								   					   		
					 	/* 
						 * 적치단 UP위치 Clear
						 * tb_ym_stacklayer Table : stock_id 		 = ''(Empty)
						 * tb_ym_stacklayer Table : stack_layer_stat = 'E'(적치가능)
						 */	
						iSeq = dao.updateCraneStackLayerStat(sStackColGp,
				    										 sStackBedGp,
				    										 sStackLayerGp,
				    										 "",
				    										 YmCommonConst.STACK_LAYER_STAT_E);
					} 
				} 
				
				/**
		 		 *	3.	작업예약이 존재하면 작업예약을 삭제한다.
		 		 */
				JDTORecord stockJr = dao.getStockInfo(TmpCoilNo);
				
				if(stockJr != null){
				
					String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
					
					if(!"".equals(sWbookId)){
						
						iSeq = dao.updateStockWbookId(TmpCoilNo,
							  				  		  ""); 
						
						iSeq = dao.deleteWbookInfo(sWbookId);
						
						logger.println(LogLevel.DEBUG, this, "작업예약 삭제="+iSeq);
					}
				}
				
				/*
				 * 수신한 COIL_NO 해당위치와 STACK_LAYER_STAT 상태를  'L' Update 한다. 
			     */
				iSeq = dao.updateCraneStackLayerStat(TmpStackColGp,
		    										 TmpBedGp,
		    										 YmCommonConst.STACK_LAYER_GP_01,
		    										 TmpCoilNo,
		    										 YmCommonConst.STACK_LAYER_STAT_L);
		
				String sQuery3 		= "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.selectStackColGpStat";
				JDTORecord colJr  	= ydStackLayerDAO.requestgetData(sQuery3, new Object[]{ TmpCoilNo });
	
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
																   				TmpCoilNo });
				/*
				 *	작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
				 */
				String sQuery5 		= "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
				JDTORecord wbookJr 	= ydStackLayerDAO.requestFind(sQuery5);
	
				if (wbookJr == null){
					logger.println(LogLevel.DEBUG,this, "작업예약ID 생성 Error");
					return false; 																				
				}
				
				String sWbookId  = StringHelper.evl(wbookJr.getFieldString("WBOOK_SELECT"), "");
	    		
	    		
	    		/*
				 *	작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 
				 *	작업예약조, 등록자, 등록일시) 한다.
				 */
				String sQuery6 = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
				int iSeq2      = ydWBookDAO.requestinsertData(sQuery6, 
															  new Object[]{sWbookId, 
															  			   sYdGp, 
															  			   sBayGp, 
															  			   YmCommonConst.NEW_SCH_WORK_KIND_CDLO, 
						               									   YmCommonUtil.getWorkDuty(), 
						               									   YmCommonUtil.getWorkParty() });	
				
				JDTORecord stockV	= dao.getCoilCommonInfo(TmpCoilNo);
				String sStockMv     = "";
				String sProgCd 		= StringHelper.evl(stockV.getFieldString("CURR_PROG_CD"),"").trim();
				String sCoilProc    = "";
		    	String sNextProc 	= StringHelper.evl(stockV.getFieldString("차공정"),"").trim();
				String sPlanProc 	= StringHelper.evl(stockV.getFieldString("계획공정"),"").trim();
				
				if("".equals(sNextProc)){
					sCoilProc = sPlanProc;
				}else{
					sCoilProc = sNextProc;
				}
				
				if(YmCommonConst.CURR_PROG_CD_COIL_1.equals(sProgCd)){
		    		
					sStockMv   = YmCommonConst.NEW_STOCK_MOVE_TERM_1C;
		    		
		    	}else{
		    		
		    		if(YmCommonConst.SHEAR_SUPPLY_GP_5K.equals(sCoilProc)){		 //B열연 SPM
					
						sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A2;		//SPM 추출
					}else if(YmCommonConst.SHEAR_SUPPLY_GP_5H.equals(sCoilProc)){//B열연 HFL
					
						sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A1;		//HFL 추출
					}else if(YmCommonConst.SHEAR_SUPPLY_GP_5T.equals(sCoilProc)){//B열연 수냉재
					
						sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A3;		//수냉재 추출
					}else if(YmCommonConst.SHEAR_SUPPLY_GP_5A.equals(sCoilProc)){//B열연 공냉재
					
						sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A4;		//공냉재 추출
					}else if(YmCommonConst.SHEAR_SUPPLY_GP_6H.equals(sCoilProc)){//B열연 HFL결속대
					
						sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A7;		//B열연 HFL결속대
					}else if(YmCommonConst.SHEAR_SUPPLY_GP_6K.equals(sCoilProc)){//B열연2SPM
					
						sStockMv = YmCommonConst.NEW_STOCK_MOVE_TERM_A6;		//B열연2SPM
					}else{
					
						sStockMv =  YmCommonConst.NEW_STOCK_MOVE_TERM_A2;
					}
				}
						
				logger.println(LogLevel.DEBUG,this,"저장품 이동조건 ="+sStockMv+"=");
				
				/*
				 *	저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.
				 */
				String sQuery7 = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
				int iSeq3      = ydStockDAO.requestupdateData(sQuery7, 
															  new Object[]{sWbookId, 
															  			   sStockMv,
															  			   TmpCoilNo });	
				
				String sCraneNo = "";
				String sWprogSt = "";
				String sSchId   = "";
				String sSchCd   = "";
				
				/*
				 *	1. 현재 해당동의 DC LINE-OFF(CDLO)가 할당된 크레인을 검색해서,
				 *	   상태정보를 확인한다.
				 */
				if(!TmpLocation.trim().equals(YmCommonConst.CONVEYOR_LINE_5)){
						
					JDTORecord craneV = dao.getCoilCraneInfo(sYdGp,
				 										     sBayGp,
				 										     YmCommonConst.NEW_SCH_WORK_KIND_CDLO);
				 	if(craneV != null){
						sCraneNo = StringHelper.evl(craneV.getFieldString("SELECT_CRANE_NO"),"");
					}
					
					JDTORecord equipV = dao.getEquipInfoWithEquipNo(sYdGp,
																	sCraneNo);
			 	
			 		if(equipV != null){
			 			sWprogSt = StringHelper.evl(equipV.getFieldString("WPROG_STAT"),"");
			 			sSchId   = StringHelper.evl(equipV.getFieldString("WBOOK_ID"),"");
			 		}
			 		
			 		JDTORecord schV = dao.getSchInfoWithSchId(sSchId);
			 		
			 		if(schV != null){
			 			sSchCd = StringHelper.evl(schV.getFieldString("SCH_WORK_KIND"),"");
			 		}
			 	}		
				/*
				 *	2. COIL SCHEDULE EJB CALL
				 */  
					EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
					Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{ String.class},
																		     new Object[]{ sWbookId });							
					
				/*
				 *	3. 크레인이 IDLE이 아니고, DC LINE-OFF 작업지시(UP)가 이미
				 *	   나가있으면 다시 작업지시요구 모듈을 CALL한다.
				 *	   작업지시요구모듈에 분기콘베이어 마지막위치부터 LINE-OFF 처리되게 
				 *	   하는 모듈이 있슴.	
				 */
				 if(!TmpLocation.trim().equals(YmCommonConst.CONVEYOR_LINE_5)){
				 	
//				 	if(YmCommonConst.WORK_PROG_STAT_1.equals(sWprogSt)||
//				 	   YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSchCd)
//				 	   ){
				 	//2010.06.16 권상상태에서는 작업 지시가 안 나가도록 처리 	
			 		if(YmCommonConst.WORK_PROG_STAT_W.equals(sWprogSt)&& 
		 			   YmCommonConst.WORK_PROG_STAT_1.equals(sWprogSt) && 
		 			   YmCommonConst.NEW_SCH_WORK_KIND_CDLO.equals(sSchCd)
						 	   ){
				 		
					 	ejbConn = new EJBConnector("default","JNDICWrkOrdReg",this);
						isTrue  = (Boolean)ejbConn.trx("callCraneSchInfo",new  Class[]{ String.class,
																						String.class,
																						String.class,
																						String.class,
																						String.class,
																						String.class,
																						String.class},
																		   new Object[]{YmCommonConst.TC_CN1PB02,
																			  			sYdGp,
																			  			sBayGp,
																			  			YmCommonConst.EQUIP_KIND_CR,
																			  			sCraneNo,
																			  			YmCommonConst.NEW_SCH_WORK_KIND_CDLO,
				    																	""});
			    	}																  
			    }	 
			}
			logger.println(LogLevel.DEBUG,this,"End-receiveConvLineOffReq()");
			
			return true;
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}    
	}

	/**
	 * 오퍼레이션명 : 분기 Conveyor COIL이 확장 Conveyor로 전환시  야드 Level-2 로 송신(CN1BP07)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */   
	public boolean receiveConvToExtConv(String COIL_No) throws java.rmi.RemoteException{
		/*
			전문코드			C	7
			발생일자			C	10
			발생시간			C	8
			전문구분			C	1
			전문길이			C	4
			COILNO			C	10
			군				C	1
			제작번호행번		C	13
			두께				C	7
			폭				C	6
			길이				C	6
			외경				C	5
			중량				C	5
			분기위치CODE		C	2
			확장분기위치CODE	C	2
			냉각방법			C	1
	     */	
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveConvToExtConv()");
		
		StringBuffer sMsg               = new StringBuffer();

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
			
			StringBuffer SendMsgInfo    = new StringBuffer();
			
			String TC				= ""; 
			String sDate			= ""; 
			String sTime			= ""; 
			String Form				= ""; 
			String Message_Length	= ""; 
			String CoilNo			= ""; 
			String sGroup			= ""; 
			String ProductNo		= ""; 
			String Thick			= ""; 
			String Width			= ""; 
			String sLength			= ""; 
			String OutDia			= ""; 
			String Weight			= ""; 
			String BranchCd			= "";
			String ExtConvBranchCd	= "";
			String CoolMethod		= "";
			
			int iTC					=  7; 
			int iDate				= 10; 
			int iTime				=  8; 
			int iForm				=  1; 
			int iMessage_Length		=  4; 
			int iCoilNo				= 10; 
			int iGroup				=  1; 
			int iProductNo			= 13; 
			int iThick				=  7; 
			int iWidth				=  6; 
			int iLength				=  6; 
			int iOutDia				=  5; 
			int iWeight				=  5;
			int iBranchCd			=  2;
			int iExtConvBranchCd	=  2;
			int iCoolMethod  		=  1;
			int iTotalLength		= 58;	
			
		    String sStockQueryId 	= "ym.steelinfo.steelinforecv.dao.YdStockDAO.selectCoilInfoMat";   // Coil소재
			JDTORecord StockCoilNo  = ydStockDAO.getData(sStockQueryId, new Object[]{ COIL_No.trim() });
			
			String sOrdNo   		= StringHelper.evl(StockCoilNo.getFieldString("제작번호"), "");
			String sOrdDtl  		= StringHelper.evl(StockCoilNo.getFieldString("제작행번"), "");
			String sCoilT  			= StringHelper.evl(StockCoilNo.getFieldString("코일두께"), "");
			String sCoilW  			= StringHelper.evl(StockCoilNo.getFieldString("코일폭"), "");
			String sCoilLen  		= StringHelper.evl(StockCoilNo.getFieldString("코일길이"), "");
			String sCoilOutDia  	= StringHelper.evl(StockCoilNo.getFieldString("코일외경"), "");
			String sCoilWeight  	= StringHelper.evl(StockCoilNo.getFieldString("코일중량"), "");
			String sCoilBranchCd  	= StringHelper.evl(StockCoilNo.getFieldString("분기위치코드"), "");
			String sCoilExtConvBranchCd 	= StringHelper.evl(StockCoilNo.getFieldString("확장분기위치코드"), "");
			String sCoilCoolMethod 	= StringHelper.evl(StockCoilNo.getFieldString("냉각방법"), "");
			
			TC 						= "CN1BP07"; 
			sDate 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			sTime					= YmCommonUtil.getCurDate("hh-mm-ss");
			Form					= "I";
			Message_Length			= "0058";
			CoilNo					= COIL_No;
			sGroup					= YmCommonConst.GROUP_2; 					// 군
			ProductNo				= sOrdNo+sOrdDtl; 							// 제작번호행번
			Thick					= StringHelper.replaceStr(
						  	  			YmCommonUtil.format(sCoilT,3,3),".",""); 
			Width					= StringHelper.replaceStr(
						  	  			YmCommonUtil.format(sCoilW,4,1),".",""); 
			sLength					= sCoilLen; 								//길이
			OutDia					= sCoilOutDia; 								//외경
			Weight					= sCoilWeight; 								//중량
			BranchCd       		    = sCoilBranchCd; 							//분기위치CODE
			ExtConvBranchCd 		= sCoilExtConvBranchCd; 					//확장분기위치CODE
			CoolMethod     			= sCoilCoolMethod; 							//냉각방법
			
			sMsg.append(YmCommonUtil.FillToString(TC		    ,iTC));
			sMsg.append(YmCommonUtil.FillToString(sDate			,iDate));
			sMsg.append(YmCommonUtil.FillToString(sTime	    	,iTime));
			sMsg.append(YmCommonUtil.FillToString(Form		    ,iForm));
			sMsg.append(YmCommonUtil.FillToNumber(Message_Length,iMessage_Length));
			sMsg.append(YmCommonUtil.FillToString(CoilNo		,iCoilNo));
			sMsg.append(YmCommonUtil.FillToString(sGroup		,iGroup));
			sMsg.append(YmCommonUtil.FillToString(ProductNo		,iProductNo));
			sMsg.append(YmCommonUtil.FillToNumber(Thick    		,iThick));
			sMsg.append(YmCommonUtil.FillToNumber(Width	    	,iWidth));
			sMsg.append(YmCommonUtil.FillToNumber(sLength	    ,iLength));
			sMsg.append(YmCommonUtil.FillToNumber(OutDia	    ,iOutDia));
			sMsg.append(YmCommonUtil.FillToNumber(Weight	    ,iWeight));
			sMsg.append(YmCommonUtil.FillToNumber(BranchCd	    ,iBranchCd));
			sMsg.append(YmCommonUtil.FillToNumber(ExtConvBranchCd,iExtConvBranchCd));
			sMsg.append(YmCommonUtil.FillToNumber(CoolMethod	,iCoolMethod));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CN1BP07send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString()  });					
			
			logger.println(LogLevel.DEBUG,this,"End-receiveConvToExtConv()");
			
			return true;
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}  			
	}
	
	
	/**
	 * 오퍼레이션명 : 분기 Conveyor COIL Line Off Result
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */    
	public boolean receiveConvLineOffResult(String COIL_No) throws java.rmi.RemoteException{
		/*
	     * 업무 로직
	     * 	1.TC_CD - CF1BP04
	     * 	2.분기 Conveyor COIL Line Off 실적 정보를  Mill LEVEL2로 송신
	     * 
			COILNo	        CHAR	10		
            SPARE		    CHAR	2
	     */	
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveConvLineOffResult()");
		
		StringBuffer sMsg               = new StringBuffer();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			int iResult = 0;
			
			StringBuffer SendMsgInfo    = new StringBuffer();
			
			String TC_CD                = ""; //CF1BP04
			String TC_Date              = "";
			String TC_Time				= "";
			String TC_ID 				= ""; //I
			String TC_Length 			= ""; //0254
			String SPARE     			= "";
			
			int	iTC_CD					= 7;
			int	iTC_Date				= 10;
			int	iTC_Time				= 8;
			int	iTC_ID					= 1;
			int	iTC_Length				= 4;			
			int	iCOIL_No				= 10;
			int	iSPARE      			= 2;
			
			TC_CD 						= "CF1BP04"; 
			TC_Date 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			TC_Time						= YmCommonUtil.getCurDate("hh-mm-ss");
			TC_ID						= "I";
			TC_Length					= "0012";
			SPARE                       = "  ";
			
			sMsg.append(YmCommonUtil.FillToString(TC_CD			,iTC_CD));
			sMsg.append(YmCommonUtil.FillToString(TC_Date		,iTC_Date));
			sMsg.append(YmCommonUtil.FillToString(TC_Time		,iTC_Time));
			sMsg.append(YmCommonUtil.FillToString(TC_ID			,iTC_ID));
			sMsg.append(YmCommonUtil.FillToString(TC_Length		,iTC_Length));
			sMsg.append(YmCommonUtil.FillToString(COIL_No	    ,iCOIL_No));
			sMsg.append(YmCommonUtil.FillToString(SPARE         ,iSPARE));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CF1BP04send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString()  });					
			
			logger.println(LogLevel.DEBUG,this,"End-receiveConvLineOffResult()");
			
			return true;
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}  			
	}
	
	/**
	 * 오퍼레이션명 : 분기 Conveyor COIL Line Off Result
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */     
	public boolean receiveConvLineInResult(String COIL_No, String sPut_Position) throws java.rmi.RemoteException{
		/*
	     * 업무 로직
	     * 	1.TC_CD - CF1BP15
	     * 	2.분기 Conveyor COIL Line In 실적 정보를  Mill LEVEL2로 송신
	     * 
			COILNo	        CHAR	10		
            Destination		CHAR	2		1F:1STD, 2F:2STD, 3S:3STD, 4E:EXT STD
            Position	 	CHAR	1		1:Position1, 2:Position2,Pointx: Refer Note1
		
	     */
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveConvLineInResult()");
		
		StringBuffer sMsg               = new StringBuffer();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			int iResult = 0;
			
			StringBuffer SendMsgInfo    = new StringBuffer();
			
			String TC_CD                = ""; //CF1BP15
			String TC_Date              = "";
			String TC_Time				= "";
			String TC_ID 				= ""; //I
			String TC_Length 			= ""; //0254
			String Destination 			= "";
			String Position				= "";
			
			int	iTC_CD					= 7;
			int	iTC_Date				= 10;
			int	iTC_Time				= 8;
			int	iTC_ID					= 1;
			int	iTC_Length				= 4;			
			int	iCOIL_No				= 10;
			int	iDestination			= 2;
			int	iPosition				= 1;
			
			TC_CD 						= YmCommonConst.TC_CF1BP15; 
			TC_Date 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			TC_Time						= YmCommonUtil.getCurDate("hh-mm-ss");
			TC_ID						= "I";
			TC_Length					= "0013";
			
			String sStackColGp   = sPut_Position.substring(0, 6);
			String sStackBedGp   = sPut_Position.substring(6, 8);
			String sStackLayerGp = sPut_Position.substring(8,10);
						
			if (sStackColGp.trim().equals(YmCommonConst.STACK_COL_GP_3AST01)){      
				Destination = "1F";
			}else if (sStackColGp.trim().equals(YmCommonConst.STACK_COL_GP_3BST02)){
				Destination = "2F";
			}else if (sStackColGp.trim().equals(YmCommonConst.STACK_COL_GP_3CST03)){
				Destination = "3S";
			}else if (sStackColGp.trim().equals(YmCommonConst.STACK_COL_GP_3CEX01)){
				Destination = "4E";
			}			
			
			if (sStackBedGp.trim().equals(YmCommonConst.STACK_BED_GP_01)){         // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				Position  = YmCommonConst.SKID_NO_1;					
			}else if (sStackBedGp.trim().equals(YmCommonConst.STACK_BED_GP_02)){   // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				Position  = YmCommonConst.SKID_NO_2;	
			}else if (sStackBedGp.trim().equals(YmCommonConst.STACK_BED_GP_03)){   // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				Position  = YmCommonConst.SKID_NO_3;
			}else if (sStackBedGp.trim().equals(YmCommonConst.STACK_BED_GP_04)){   // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				Position  = YmCommonConst.SKID_NO_4;
			}else if (sStackBedGp.trim().equals(YmCommonConst.STACK_BED_GP_05)){   // 1:1STD,  2:2STD,  3:3STD,  4:EXT STD
				Position  = YmCommonConst.SKID_NO_5;
			}
			
			sMsg.append(YmCommonUtil.FillToString(TC_CD			,iTC_CD));
			sMsg.append(YmCommonUtil.FillToString(TC_Date		,iTC_Date));
			sMsg.append(YmCommonUtil.FillToString(TC_Time		,iTC_Time));
			sMsg.append(YmCommonUtil.FillToString(TC_ID			,iTC_ID));
			sMsg.append(YmCommonUtil.FillToString(TC_Length		,iTC_Length));
			sMsg.append(YmCommonUtil.FillToString(COIL_No	    ,iCOIL_No));
			sMsg.append(YmCommonUtil.FillToString(Destination	,iDestination));
			sMsg.append(YmCommonUtil.FillToString(Position	    ,iPosition));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CF1BP15send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString()  });					
			
			logger.println(LogLevel.DEBUG,this,"End-receiveConvLineInResult()");
			
			return true;
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}  		
	}
	
	/**
	 * 오퍼레이션명 : Take Out Request (Auto/Manual)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */     
	public boolean receiveTakeoutRequest(String sMessage) throws java.rmi.RemoteException{
		/*
	     * Mill LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당업무로직을 처리한다.
	     * 전문내용을 JDTORecord로 파싱한다.
	     * 업무 로직
	     * 	1.TC_CD - CF1PB13
	     * 	2.Mill LEVEL2로 부터 분기 Conveyor COIL Take Out 요구 정보를 수신
	     *  3.Down Coil 밑 분기 Conveyor Turn Table2 Zone 에서 Take Out 하는것   
	     * 
			COILNo		CHAR	10		
			SPARE		CHAR	2		
			Position	CHAR	1		1:Turn Table2 Zone (3BTT01)
									    2:Turn Table2 Zone (3BTT02)
									    3:Turn Table2 Zone (3XSC01)
			Mode		CHAR	1		1:Auto    2:Manual
	     */		
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveTakeoutRequest()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO			dao 		= new CraneSchDAO();
		
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

			String TmpCOILNo            = StringHelper.evl(jDTORecord.getFieldString("COILNo"), "");
			String TmpPosition          = StringHelper.evl(jDTORecord.getFieldString("Position"), "");
			String TmpMode              = StringHelper.evl(jDTORecord.getFieldString("Mode"), "");
			
			/**
	 		 *	1.	3XDC01 라인 삭제
	 		 */
	 		 
			int iSeq = YmCommonDB.deleteConveyorInfo(YmCommonConst.STACK_COL_GP_3XDC01, TmpCOILNo.trim());
			
			/**
	 		 *	2.	MAP에 존재하는 저장품 삭제
	 		 */
			List stockL	= dao.getStackLayerInfoWithStockId_03(TmpCOILNo.trim());
						 
			if(stockL != null)
			{	
				String sStackColGp   = "";
				String sStackBedGp   = "";
				String sStackLayerGp = "";
				JDTORecord stackV 	   = null;
			 
				for(int inx = 0; inx < stockL.size() ; inx++)
				{
				 	stackV = (JDTORecord)stockL.get(inx);
				 	
				 	sStackColGp   = StringHelper.evl(stackV.getFieldString("STACK_COL_GP"), "");
					sStackBedGp   = StringHelper.evl(stackV.getFieldString("STACK_BED_GP"), "");
					sStackLayerGp = StringHelper.evl(stackV.getFieldString("STACK_LAYER_GP"), "");
							   					   		
				 	/* 
					 * 적치단 UP위치 Clear
					 * tb_ym_stacklayer Table : stock_id 		 = ''(Empty)
					 * tb_ym_stacklayer Table : stack_layer_stat = 'E'(적치가능)
					 */	
					iSeq = dao.updateCraneStackLayerStat(sStackColGp,
			    										 sStackBedGp,
			    										 sStackLayerGp,
			    										 "",
			    										 YmCommonConst.STACK_LAYER_STAT_E);
				} 
			}
			
			/**
	 		 *	3.	작업예약이 존재하면 작업예약을 삭제한다.
	 		 */
			JDTORecord stockJr = dao.getStockInfo(TmpCOILNo.trim());
			
			if(stockJr != null){
				
				String sWbookId = StringHelper.evl(stockJr.getFieldString("WBOOK_ID"), "");
				
				if(!"".equals(sWbookId)){
					
					int iSeq1 = dao.updateStockWbookId(TmpCOILNo.trim(),
						  				  		  	   ""); 
					
					int iSeq2 = dao.deleteWbookInfo(sWbookId);
					
					logger.println(LogLevel.DEBUG, this, "작업예약취소=> 작업예약 삭제="+iSeq2);
				}
			}
			
			String sStackColGp = "";
			
			if("1".equals(TmpPosition)){
				sStackColGp = YmCommonConst.STACK_COL_GP_3BTT01;
			}else if("2".equals(TmpPosition)){
				sStackColGp = YmCommonConst.STACK_COL_GP_3BTT02;
			}else if("3".equals(TmpPosition)){
				sStackColGp = YmCommonConst.STACK_COL_GP_3BSC01;
			}	
			/*
			 * 수신한 COILNo 해당위치와, STACK_LAYER_STAT 상태를  'L' Update 한다. 
		     * update TB_YM_STACKLAYER SET STOCK_ID = CoilNo, STACK_LAYER_STAT='L' 
		     *  WHERE STACK_COL_GP   = '3BTT01'
		     *    AND STACK_BED_GP   =
		     *    AND STACK_LAYER_GP = '01' 
			 */
			String updateHZStackLayer = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateHZStackLayer";
			int updatehzstacklayer    = ydStackLayerDAO.requestupdateData(updateHZStackLayer, 
																		  new Object[]{TmpCOILNo.trim(), 
																		  			   YmCommonConst.STACK_LAYER_STAT_L, 
																		  			   sStackColGp, 
					                    											   YmCommonConst.STACK_BED_GP_01, 
					                    											   YmCommonConst.STACK_LAYER_GP_01 });			
			
			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockTmpCOILNo = ydStockDAO.getData(sStockQueryId, new Object[]{ TmpCOILNo.trim() });

			if (StockTmpCOILNo == null){
				throw new EJBServiceException("수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재하지 않습니다. Error");
			}
			
			String stockId = StringHelper.evl(StockTmpCOILNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockTmpCOILNo.getFieldString("WBOOK_ID"), "");
            
			List list = new ArrayList();
			list.add(TmpCOILNo.trim());
			boolean exsitCoilNo = ydStockDAO.isExistPrimaryKey(list);

			if(exsitCoilNo) {

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
						throw new EJBServiceException("적치단(TB_YM_STACKLAYER) Table에 존재하지않는 coil 입니다. Error");
					}
					
					String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
					String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
					String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
					String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
					
					if (stackCol != null && !stackCol.equals("")){
						
                        // 적치단  Table Update(작업요구상태='S'로 변경)
						// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
						if (stackStat.trim().equals("L")){
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
						
						// Schedule Code : Coil DC Take out
						int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, 
																		new Object[]{wBookid.trim(), 
																					 stackCol1.trim(), 
																					 YmCommonConst.BAY_GP_B, 
						           													 YmCommonConst.NEW_SCH_WORK_KIND_CDTO, 
						           													 YmCommonUtil.getWorkDuty(), 
						           													 YmCommonUtil.getWorkParty()});	

						// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다.  (생산예정)
						// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM = ? WHERE STOCK_ID = ?
						String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
								    wBookid.trim(), YmCommonConst.NEW_STOCK_MOVE_TERM_1C, stockId.trim() });							
						
						logger.println(LogLevel.DEBUG,this, "End-receiveTakeoutRequest()");
						
						//Slab Schedule EJB Call
						EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid.trim() });							
					}
		    	}	
		    }			
			
			logger.println(LogLevel.DEBUG,this,"End-receiveTakeoutRequest()");
			
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}

      /**
	 * 오퍼레이션명 : Take In Request (Auto/Manual)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */     	 
	public boolean receiveTakeInRequest(String sMessage) throws java.rmi.RemoteException{
		/*
	     * Mill LEVEL2로부터 넘어온 전문을 파싱한 후 전문내용을 가지고 해당업무로직을 처리한다.
	     * 전문내용을 JDTORecord로 파싱한다.
	     * 업무 로직
	     * 	1.TC_CD - CF1PB14
	     * 	2.Mill LEVEL2로 부터 분기 Conveyor COIL Take In Request (Auto/Manual) 요구 정보를 수신
	     * 
			COILNo		CHAR	10		
			SPARE		CHAR	2		
			Position	CHAR	1		1:Turn Table 2 Zone
			Mode		CHAR	1		1:Auto    2:Manual 
	     */		
        LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveTakeInRequest()");
		
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

			String TmpCOILNo       = StringHelper.evl(jDTORecord.getFieldString("COILNo"), "");
			String TmpPosition     = StringHelper.evl(jDTORecord.getFieldString("Position"), "");
			String TmpMode         = StringHelper.evl(jDTORecord.getFieldString("Mode"), "");
			
			/* 수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재 하는지 점검 
             * 저장품 Table에 작업예약_ID가 존재한다면 Error
             * Select STOCK_ID, WBOOK_ID from TB_YM_STOCK Where STOCK_ID = ?
             */ 
			String sStockQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.selectStockID";
			JDTORecord StockTmpCOILNo = ydStockDAO.getData(sStockQueryId, new Object[]{ TmpCOILNo.trim() });

			if (StockTmpCOILNo == null){
				throw new EJBServiceException("수신한  Coil No가 저장품(TB_YM_STOCK) Table 에 존재하지 않습니다. Error");
			}
			
			String stockId = StringHelper.evl(StockTmpCOILNo.getFieldString("STOCK_ID"), "");
			String wbookId = StringHelper.evl(StockTmpCOILNo.getFieldString("WBOOK_ID"), "");
            
			List list = new ArrayList();
			list.add(TmpCOILNo.trim());
			boolean exsitCoilNo = ydStockDAO.isExistPrimaryKey(list);

			if(exsitCoilNo) {

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
						throw new EJBServiceException("적치단(TB_YM_STACKLAYER) Table에 존재하지 않습니다. Error");
					}
					
					String stackCol  = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP"), "");
					String stackCol1 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP1"), "");
					String stackCol2 = StringHelper.evl(StackColGp.getFieldString("STACK_COL_GP2"), "");
					String stackStat = StringHelper.evl(StackColGp.getFieldString("STACK_LAYER_STAT"), "");
					
					if (stackCol != null && !stackCol.equals("")){
						
                        // 적치단  Table Update(작업요구상태='S'로 변경)
						// UPDATE TB_YM_STACKLAYER SET STACK_LAYER_STAT = 'S' WHERE STOCK_ID = ?
						if (stackStat.trim().equals("L")){
							String sStkLayerQueryId = "ym.facilitystatus.facilityinquiry.dao.YdStackLayerDAO.updateStackLayerStatMark";
							int stkColGp = ydStackLayerDAO.requestupdateData(sStkLayerQueryId, new Object[]{ 
									       YmCommonConst.STACK_LAYER_STAT_S, stockId.trim() });
						}
						
						// 작업예약(TB_YM_WBOOK) Table Select WBOOK_ID Key 생성 Seq + 1
						String wBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.selectYdWBook";
						JDTORecord wBookSel = ydStackLayerDAO.requestFind(wBookQueryId);

						if (wBookSel == null){
							throw new EJBServiceException("작업예약 ID 생성 Error");
						}
						
						String wBookid  = wBookSel.getFieldString("WBOOK_SELECT");
						
						// 작업예약(TB_YM_WBOOK) Table Insert(Yard 구분, 동구분, 작업예약일시, 작업예약조, 등록자, 등록일시) 한다.
						String sWBookQueryId = "ym.workrequest.wrequestaccept.dao.YdWBookDAO.InsertYdWBook";
						
						// Schedule Code : Coil DC Take In
						int wbookstockId = ydWBookDAO.requestinsertData(sWBookQueryId, new Object[]{ 
						           wBookid.trim(), stackCol1.trim(), stackCol2.trim(), YmCommonConst.NEW_SCH_WORK_KIND_CDTI, 
						           YmCommonUtil.getWorkDuty(), YmCommonUtil.getWorkParty() });	

						// 저장품 Table(TB_YM_STOCK)에 WBOOK_ID를 Update 한다. (생산예정)
						// UPDATE TB_YM_STOCK SET WBOOK_ID= ?, STOCK_MOVE_TERM = ? WHERE STOCK_ID = ?
						String stkQueryId = "ym.steelinfo.steelinforecv.YdStockDAO.updateYdStockStockId";
						int stkId = ydStockDAO.requestupdateData(stkQueryId, new Object[]{ 
								    wBookid.trim(), YmCommonConst.NEW_STOCK_MOVE_TERM_1C, stockId.trim() });							
						
						logger.println(LogLevel.DEBUG,this, "End-receiveTakeoutRequest()");
						
						//Slab Schedule EJB Call
						EJBConnector ejbConn = new EJBConnector("default","JNDICraneSchReg",this);
						Boolean isTrue = (Boolean)ejbConn.trx("callCraneSchInfo",new Class[]{String.class},new Object[]{ wBookid.trim() });							
					}
		    	}	
		    }	
			
			logger.println(LogLevel.DEBUG,this,"End-receiveTakeInRequest()");
			
			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	    	e.printStackTrace();  
	        throw new EJBServiceException(e);
	    }
	}	
	
	/**
	 * 오퍼레이션명 : 분기 Conveyor COIL Take Out Result
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */     	 
	public boolean receiveConvTakeOutResult(String COIL_No) throws java.rmi.RemoteException{
		/*
	     * 업무 로직
	     * 	1.TC_CD - CF1BP05
	     * 	2.분기 Conveyor COIL Take Out 실적 정보를  Mill LEVEL2로 송신
	     * 
			COILNo	        CHAR	10		
            SPARE    		CHAR	2	
	     */
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveConvTakeOutResult()");
		
		StringBuffer sMsg               = new StringBuffer();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			int iResult = 0;
			
			StringBuffer SendMsgInfo    = new StringBuffer();
			
			String TC_CD                = ""; //CF1BP05
			String TC_Date              = "";
			String TC_Time				= "";
			String TC_ID 				= ""; //I
			String TC_Length 			= ""; //0254
			String TC_Space 			= "";
			
			int	iTC_CD					= 7;
			int	iTC_Date				= 10;
			int	iTC_Time				= 8;
			int	iTC_ID					= 1;
			int	iTC_Length				= 4;			
			int	iCOIL_No				= 10;
			int	iSpace   				= 2;
			
			TC_CD 						= YmCommonConst.TC_CF1BP05; 
			TC_Date 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			TC_Time						= YmCommonUtil.getCurDate("hh-mm-ss");
			TC_ID						= "I";
			TC_Length					= "0012";
			
			sMsg.append(YmCommonUtil.FillToString(TC_CD			,iTC_CD));
			sMsg.append(YmCommonUtil.FillToString(TC_Date		,iTC_Date));
			sMsg.append(YmCommonUtil.FillToString(TC_Time		,iTC_Time));
			sMsg.append(YmCommonUtil.FillToString(TC_ID			,iTC_ID));
			sMsg.append(YmCommonUtil.FillToString(TC_Length		,iTC_Length));
			sMsg.append(YmCommonUtil.FillToString(COIL_No	    ,iCOIL_No));
			sMsg.append(YmCommonUtil.FillToString(TC_Space	    ,iSpace));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CF1BP05send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString()  });					
			
			logger.println(LogLevel.DEBUG,this,"End-receiveConvTakeOutResult()");
			
			return true;
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}  		
	}	
	
	/**
	 * 오퍼레이션명 : 분기 Conveyor COIL Take In Result
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */     	 
	public boolean receiveConvTakeInResult(String COIL_No) throws java.rmi.RemoteException{
		/*
	     * 업무 로직
	     * 	1.TC_CD - CF1BP06
	     * 	2.분기 Conveyor COIL Take In 실적 정보를  Mill LEVEL2로 송신
	     * 
			COILNo	        CHAR	10		
            SPARE    		CHAR	2	
	     */
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-receiveConvTakeInResult()");
		
		StringBuffer sMsg               = new StringBuffer();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			
			int iResult = 0;
			
			StringBuffer SendMsgInfo    = new StringBuffer();
			
			String TC_CD                = ""; //CF1BP06
			String TC_Date              = "";
			String TC_Time				= "";
			String TC_ID 				= ""; //I
			String TC_Length 			= ""; //0254
			String TC_Space 			= "";
			
			int	iTC_CD					= 7;
			int	iTC_Date				= 10;
			int	iTC_Time				= 8;
			int	iTC_ID					= 1;
			int	iTC_Length				= 4;			
			int	iCOIL_No				= 10;
			int	iSpace   				= 2;
			
			TC_CD 						= YmCommonConst.TC_CF1BP06; 
			TC_Date 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			TC_Time						= YmCommonUtil.getCurDate("hh-mm-ss");
			TC_ID						= "I";
			TC_Length					= "0012";
			
			sMsg.append(YmCommonUtil.FillToString(TC_CD			,iTC_CD));
			sMsg.append(YmCommonUtil.FillToString(TC_Date		,iTC_Date));
			sMsg.append(YmCommonUtil.FillToString(TC_Time		,iTC_Time));
			sMsg.append(YmCommonUtil.FillToString(TC_ID			,iTC_ID));
			sMsg.append(YmCommonUtil.FillToString(TC_Length		,iTC_Length));
			sMsg.append(YmCommonUtil.FillToString(COIL_No	    ,iCOIL_No));
			sMsg.append(YmCommonUtil.FillToString(TC_Space	    ,iSpace));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CF1BP06send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString()  });					
			
			logger.println(LogLevel.DEBUG,this,"End-receiveConvTakeInResult()");
			
			return true;
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}  		
	}	
	
	/**
	 * 오퍼레이션명 :  확장 Conveyor COIL Line Off 응답 (고려계전으로 송신)
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param 
	 * @return 
	 * @throws 
	 */     	  
	public boolean sendConvLineOffRequest(String COIL_No) throws java.rmi.RemoteException{
		/*
	     * 업무 로직
	     * 	1.TC_CD - CN1BP09
	     * 	2.확장 Conveyor COIL Line Off 요구 정보를 수신후 응답정보를 야드 LEVEL2로 송신
	     * 	COILNo	        CHAR	10		
	     */
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-sendConvLineOffRequest()");
		
		StringBuffer sMsg               = new StringBuffer();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			int iResult = 0;
			
			StringBuffer SendMsgInfo    = new StringBuffer();
			
			String TC_CD                = ""; //CN1BP09
			String TC_Date              = "";
			String TC_Time				= "";
			String TC_ID 				= ""; //I
			String TC_Length 			= ""; //10
			
			int	iTC_CD					= 7;
			int	iTC_Date				= 10;
			int	iTC_Time				= 8;
			int	iTC_ID					= 1;
			int	iTC_Length				= 4;			
			int	iCOIL_No				= 10;
			
			TC_CD 						= YmCommonConst.TC_CN1BP09; 
			TC_Date 					= YmCommonUtil.getCurDate("yyyy-MM-dd");
			TC_Time						= YmCommonUtil.getCurDate("hh-mm-ss");
			TC_ID						= "I";
			TC_Length					= "0010";
			
			sMsg.append(YmCommonUtil.FillToString(TC_CD			,iTC_CD));
			sMsg.append(YmCommonUtil.FillToString(TC_Date		,iTC_Date));
			sMsg.append(YmCommonUtil.FillToString(TC_Time		,iTC_Time));
			sMsg.append(YmCommonUtil.FillToString(TC_ID			,iTC_ID));
			sMsg.append(YmCommonUtil.FillToString(TC_Length		,iTC_Length));
			sMsg.append(YmCommonUtil.FillToString(COIL_No	    ,iCOIL_No));
			
			SendMsgInfo  =  sMsg;

			EJBConnector ejbConn = new EJBConnector("default","JNDIYardWrkResReg",this);
			Boolean isTrue = (Boolean)ejbConn.trx("CN1BP09send",new Class[]{String.class},new Object[]{ SendMsgInfo.toString()  });					
			
			logger.println(LogLevel.DEBUG,this,"End-sendConvLineOffRequest()");
			
			return true;
			
		}catch(DAOException daoe){
		    throw daoe;
		}catch(Exception e){
		    throw new EJBServiceException(e);
		}  		
	}
	
	// 고장여부를 등록한다.
    /**
	 * 오퍼레이션명 : 확장 Conveyor 설비의 고장여부를 등록
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param jDTORecord
	 * @return
	 * @throws 
	 */
	public boolean CCExtWrkOrdRegisHindrance(JDTORecord jDTORecord) throws java.rmi.RemoteException{
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-CCExtWrkOrdRegisHindrance()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO			dao 		= new CraneSchDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			
			int iResult = 0;
			
			String yardId    = StringHelper.evl(jDTORecord.getFieldString("야드구분"), "");
			String dongId    = StringHelper.evl(jDTORecord.getFieldString("동구분"), "");
			String equipType = StringHelper.evl(jDTORecord.getFieldString("설비종류"), "");
			String equipNo   = StringHelper.evl(jDTORecord.getFieldString("설비번호"), "");
			String addrNo    = StringHelper.evl(jDTORecord.getFieldString("번지"), "");
			String hindrance = StringHelper.evl(jDTORecord.getFieldString("고장여부"), "");

			// 적치열 STACK_COL_GP 
			String stackColGp = "" + yardId.trim() + ""+ dongId.trim() + "" + equipType.trim() + "" + equipNo.trim() + "";
			
			logger.println(LogLevel.DEBUG,this,"야드구분: "+yardId);
			logger.println(LogLevel.DEBUG,this,"동구분  : "+dongId);
			logger.println(LogLevel.DEBUG,this,"설비종류: "+equipType);
			logger.println(LogLevel.DEBUG,this,"설비번호: "+equipNo);
			logger.println(LogLevel.DEBUG,this,"번지   : "+addrNo);
			logger.println(LogLevel.DEBUG,this,"고장여부: "+hindrance);
			logger.println(LogLevel.DEBUG,this,"적치열 : "+stackColGp);
			
			// 고장여부 정보를 데이터베이스에 업데이트 한다.
			/*
			 * 
			 * */

			String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateBConveyorHindrance";
			int iSeq3      = ydStockDAO.requestupdateData(sQueryId, 
														new Object[]{hindrance, 
																	 stackColGp,
																	 dongId,
																	 yardId});	

			logger.println(LogLevel.DEBUG,this,"END-CCExtWrkOrdRegisHindrance()");

			return true;
	    }catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	}
	
	// 공통코일테이블에 확장Conveyor분기위치코드를 업데이트 한다.
	// 최규성 2009-10-07
    /**
	 * 오퍼레이션명 : 확장 Conveyor 분기위치코드를 변경한다.
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param listArgu_IN
	 * @return
	 * @throws 
	 */
	public boolean CCExtWrkOrdUpdateExtConvBranchCode(List listArgu_IN) throws java.rmi.RemoteException{
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-CCExtWrkOrdUpdateExtConvBranchCode()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
		CraneSchDAO			dao 		= new CraneSchDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			
			/*
			UPDATE TB_PT_COILCOMM
			SET EXTEND_CONVEYOR_BRANCH_CD = :MODIFY_EC_BR_CD
			WHERE COIL_NO IN (SELECT A.STL_NO             AS STL_NO          --코일번호
			                  FROM TB_PO_ABHRTRACKING A, TB_PT_COILCOMM B, TB_SM_CUSTINFO C
				             WHERE A.PLANT_GP    = 'B'
					           AND  A.PROC_GP     IN('D','X')
			                   AND B.EXTEND_CONVEYOR_BRANCH_CD = :CURR_EC_BR_CD1
					           AND  (
			                           A.EQUIP_GP LIKE 'XT%'  
			                           OR  
			                           A.EQUIP_GP LIKE 'STD%'  
			                           OR  
			                           A.EQUIP_GP LIKE 'EX%' 
			                        ) 
			                   AND  A.STL_NO = B.COIL_NO(+)
			                   AND B.DEMANDER_CD = C.CUST_CD(+)
			               )
			  AND EXTEND_CONVEYOR_BRANCH_CD = :CURR_EC_BR_CD2
			 */  
			String sQueryId = "ym.facilitystatus.facilityinquiry.CraneSchDAO.updateBEXConveyorBranchCode_PIDEV";
			System.out.println("수신한 데이터"+listArgu_IN);
			
			int nRetVal = ydStockDAO.updateData(sQueryId, listArgu_IN );
			logger.println(LogLevel.DEBUG,this,"END-CCExtWrkOrdUpdateExtConvBranchCode()");
			return true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	}
	// 확장컨베이어조회 화면에서 선택된 데이터에 대한 수정을 수행한다.
	// 최규성 2009-10-07
    /**
	 * 오퍼레이션명 : 확장 Conveyor 분기위치코드를 변경한다.
	 *  
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param sQuery_IN
	 * @param listArgu_IN
	 * @return
	 * @throws 
	 */
	public boolean CCExtWrkOrdUpdateBranchCode(String sQuery_IN, List listArgu_IN) throws java.rmi.RemoteException{
		
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-CCExtWrkOrdUpdateBranchCode()");
		
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
//		YdStackLayerDAO ydStackLayerDAO = new YdStackLayerDAO();
//		YdWBookDAO ydWBookDAO           = new YdWBookDAO();
//		CraneSchDAO			dao 		= new CraneSchDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return false;
			} 
			
			
			String sQueryId = sQuery_IN;
			System.out.println("수신한 데이터"+listArgu_IN);
			
			int nRetVal = ydStockDAO.updateData(sQueryId, listArgu_IN );
			logger.println(LogLevel.DEBUG,this,"END-CCExtWrkOrdUpdateBranchCode()");
			return true;
		}catch(DAOException daoe){
	        throw daoe;
	    }catch(Exception e){
	        throw new EJBServiceException(e);
	    }    
	}
	// 정상인 설비코드를 가져옵니다.
	// 최규성 2009-10-07
	/**
	 * 오퍼레이션명 : 컨베이어 설비코드를 가져온다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  queryId, listData
	 * @return
	 * @throws 
	 */
	public List getListConvEquipNo(String queryID, List listData) throws java.rmi.RemoteException{
		YdStockDAO ydStockDAO 	        = new YdStockDAO();
		
		try{
			/*
			 * 구자원 단계별 삭제 로직  
			 */
			String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
			if(sAPP060_OLDSRC_YN.equals("Y")){
				return null;
			} 
			
			return ydStockDAO.getListData(queryID, listData);
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	/**
	 * 오퍼레이션명 : 컨베이어 설비코드를 가져온다.
	 *
	 * @ejb.interface-method EJBDoclet을 생성하는 태그입니다.
	 * @param  queryId, listData
	 * @return
	 * @throws 
	 */
//	public JDTORecord checkFacilityHindrance(List listCoilNo) throws java.rmi.RemoteException{
	public List checkFacilityHindrance(List listCoilNo) throws java.rmi.RemoteException{
		
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		} 
		
		/*
		 * 고장설비 가져오기
		 * 코일별 분기위치코드 및 확장분기위치코드 가져오기
		 * 기준에 따라서 코일공통테이블의 코일별 분기위치코드 및 확장분기위치코드 변경하기
		 */
		LogServiceConfig config = LogService.getInstance().getLogServiceContext().getLogServiceConfig("ym");
		Logger logger = new Logger(config);
		logger.println(LogLevel.DEBUG,this,"Start-checkFacilityHindrance()");

		List listResult 		= new ArrayList();		// 고장난 설비의 정보를 저
		List listArgu			= new ArrayList();
		List listData 			= new ArrayList();
		List listConvInfo		= new ArrayList();
		YdStockDAO ydStockDAO	= new YdStockDAO();
		
		listResult.clear();
		listConvInfo.clear();
		
		ymCommonDAO dao = ymCommonDAO.getInstance();
		
		try{
			// 고장 설비 정보 구하기
			/*
			SELECT EQUIP_GP, YD_GP, BAY_GP, EQUIP_KIND, EQUIP_NO, EQUIP_NAME, EQUIP_STAT
			FROM TB_YM_EQUIP
			WHERE EQUIP_GP LIKE '3_WB%'
			AND EQUIP_STAT = :stat
			*/
			String sQueryId_ExtLoc = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getBEXConveyorLoc";
			int nListDataSize = 0;
			
			// 고장난 설비만을 검사한다.
			listArgu.add("C");			// 고장인 설비를 구한다.
			
			listResult = getListConvEquipNo(sQueryId_ExtLoc, listArgu);
			int nlistSize = listResult.size();
			
			// 고장난 설비코드로 확장분기위치 코드를 판별한다.
			int nIdx = 0;

			boolean bDuple = false;		// 중복 방지용 플래그
			
			JDTORecord jtrSetRec = JDTORecordFactory.getInstance().create();
			if(nlistSize > 0)
			{
				for(int i=0; i< nlistSize;i++)
				{
					JDTORecord jtrData = (JDTORecord)listResult.get(i);
					String sEquipGp = jtrData.getFieldString("EQUIP_GP");
					
					if( sEquipGp.equals("3BWB05") )
					{
						//jtrSetRec.setField("EQUIP_GP"+String.valueOf(nIdx), sEquipGp);// 고장난 설비명
						//jtrSetRec.setField("EXT_BR_CD"+String.valueOf(nIdx), "4E");		// 확장Conv분기위치코드
						//nIdx++;
						
						
						jtrSetRec.setField("EXT_BR_CD", "4E");
					}else if( sEquipGp.equals("3CWB06") || sEquipGp.equals("3CWB10") )
					{
						if ( bDuple == false){
						//jtrSetRec.setField("EQUIP_GP"+String.valueOf(nIdx), sEquipGp);
						//jtrSetRec.setField("EXT_BR_CD"+String.valueOf(nIdx), "6E");
						//nIdx++;
						
						jtrSetRec.setField("EXT_BR_CD", "6E");

						bDuple = true;
						
						}
					}else if( sEquipGp.equals("3DWB07") )
					{
						//jtrSetRec.setField("EQUIP_GP"+String.valueOf(nIdx), sEquipGp);
						//jtrSetRec.setField("EXT_BR_CD"+String.valueOf(nIdx), "5E");
						//nIdx++;
						
						jtrSetRec.setField("EXT_BR_CD", "5E");
					}else{
						logger.println(LogLevel.DEBUG,this,"확인을 위해 로그를 찍음.");
					}
					listConvInfo.add(jtrSetRec);
					
				}
				
				logger.println(LogLevel.DEBUG,this,"편집된 리스트 :"+listConvInfo);
			}
			logger.println(LogLevel.DEBUG,this,"편집된 레코드:"+jtrSetRec);
			
//			return jtrSetRec;
			return listConvInfo;
/*			
			// 코일번호별로 코일공통 테이블에서 분기위치코드 및 확장분기위치코드 정보를 가져온다.
			String sQueryId_ConvInfo = "ym.facilitystatus.facilityinquiry.CraneSchDAO.getConveyorInfoByCoilNo_PIDEV";
			
			listConvInfo =  ydStockDAO.getListData(sQueryId_ConvInfo, listCoilNo);
			
			int listConvInfosize = listConvInfo.size();
			
			
			
			logger.println(LogLevel.DEBUG,this,"End-checkFacilityHindrance()");
			return listResult;
*/
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}
	}
	
	public String modifyCoilDestiOnConv(JDTORecord ERR_CD_IN,int CNT_ERR_CD_IN) throws java.rmi.RemoteException{
		/*
		 * 구자원 단계별 삭제 로직  
		 */
		String sAPP060_OLDSRC_YN = ymComm.BCoilApplyYn("APP060","3","OLD_SRC4");
		if(sAPP060_OLDSRC_YN.equals("Y")){
			return null;
		} 
		
		// 고장난 설비의 확장분기위치코드로 새로운 확장분기위치코드를 정한다.
		// 입력 받을 수 있는 확장분기위치코드는 4E, 5E, 6E 로 한정한다.\
		String sReturnValue = "";
		
		if (CNT_ERR_CD_IN <= 0) return sReturnValue;
		
		// 고장난 확장분기위치코드 저장 배열
		String[] arrErrCD = new String[CNT_ERR_CD_IN];
		// 전체 확장분기위치코드 저장 배열
		String[] arrAllCD = new String[3];
		
		// 이동가능한 확장분기위치코드 저장 배열
		//
		
		try{
			if(ERR_CD_IN.equals("4E") ){
				for(int i=0;i<CNT_ERR_CD_IN;i++)
				{
					
				}
			}else if(ERR_CD_IN.equals("5E")){
				
			}else if(ERR_CD_IN.equals("6E")){
				
			}
			
			
			return sReturnValue;
		}catch(DAOException daoe){
			throw daoe;
		}catch(Exception e){
			throw new EJBServiceException(e);
		}

	}
	
}

